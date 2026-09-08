public static class AudioSamples {
    /** See {@link AudioRecord#getAudioFormat()} */
    private final int audioFormat;
    /** See {@link AudioRecord#getChannelCount()} */
    private final int channelCount;
    /** See {@link AudioRecord#getSampleRate()} */
    private final int sampleRate;

    private final byte[] data;

    private AudioSamples(AudioRecord audioRecord, byte[] data) {
      this.audioFormat = audioRecord.getAudioFormat();
      this.channelCount = audioRecord.getChannelCount();
      this.sampleRate = audioRecord.getSampleRate();
      this.data = data;
    }

    public int getAudioFormat() {
      return audioFormat;
    }

    public int getChannelCount() {
      return channelCount;
    }

    public int getSampleRate() {
      return sampleRate;
    }

    public byte[] getData() {
      return data;
    }
  }

  /** Called when new audio samples are ready. This should only be set for debug purposes */
  public interface WebRtcAudioRecordSamplesReadyCallback {
    void onWebRtcAudioRecordSamplesReady(AudioSamples samples);
  }

  private static @Nullable WebRtcAudioRecordSamplesReadyCallback audioSamplesReadyCallback;

  public static void setOnAudioSamplesReady(WebRtcAudioRecordSamplesReadyCallback callback) {
    audioSamplesReadyCallback = callback;
  }

  /**
   * Audio thread which keeps calling ByteBuffer.read() waiting for audio
   * to be recorded. Feeds recorded data to the native counterpart as a
   * periodic sequence of callbacks using DataIsRecorded().
   * This thread uses a Process.THREAD_PRIORITY_URGENT_AUDIO priority.
   */
  private class AudioRecordThread extends Thread {
    private volatile boolean keepAlive = true;

    public AudioRecordThread(String name) {
      super(name);
    }

    // TODO(titovartem) make correct fix during webrtc:9175
    @SuppressWarnings("ByteBufferBackingArray")
    @Override
    public void run() {
      Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
      Logging.d(TAG, "AudioRecordThread" + WebRtcAudioUtils.getThreadInfo());

      long lastTime = System.nanoTime();
      while (keepAlive) {
        int bytesRead = audioRecord.read(byteBuffer, byteBuffer.capacity());
        int deviceBytesRead;
        if (deviceAudioRecord != null) {
          deviceBytesRead = deviceAudioRecord.read(deviceByteBuffer, deviceByteBuffer.capacity());
        } else {
          deviceBytesRead = 0;
        }
        if (bytesRead == byteBuffer.capacity()) {
          if (microphoneMute) {
            byteBuffer.clear();
            byteBuffer.put(emptyBytes);
          }
          if (bytesRead == deviceBytesRead) {
            deviceByteBuffer.position(0);
            byteBuffer.position(0);
            for (int a = 0; a < bytesRead / 2; a++) {
              int mixed = byteBuffer.getShort(a * 2) + deviceByteBuffer.getShort(a * 2) / 10;
              if (mixed > 32767) {
                mixed = 32767;
              }
              if (mixed < -32768) {
                mixed = -32768;
              }
              byteBuffer.putShort(a * 2, (short) mixed);
            }
          }
          // It's possible we've been shut down during the read, and stopRecording() tried and
          // failed to join this thread. To be a bit safer, try to avoid calling any native methods
          // in case they've been unregistered after stopRecording() returned.
          if (keepAlive) {
            try {
              nativeDataIsRecorded(bytesRead, nativeAudioRecord);
            } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
              FileLog.e(unsatisfiedLinkError);
              keepAlive = false;
            }
          }
          if (audioSamplesReadyCallback != null) {
            // Copy the entire byte buffer array.  Assume that the start of the byteBuffer is
            // at index 0.
            byte[] data = Arrays.copyOf(byteBuffer.array(), byteBuffer.capacity());
            audioSamplesReadyCallback.onWebRtcAudioRecordSamplesReady(
                new AudioSamples(audioRecord, data));
          }
        } else {
          String errorMessage = "AudioRecord.read failed: " + bytesRead;
          Logging.e(TAG, errorMessage);
          if (bytesRead == AudioRecord.ERROR_INVALID_OPERATION) {
            keepAlive = false;
            reportWebRtcAudioRecordError(errorMessage);
          }
        }
      }

      try {
        if (audioRecord != null) {
          audioRecord.stop();
        }
      } catch (IllegalStateException e) {
        Logging.e(TAG, "AudioRecord.stop failed: " + e.getMessage());
      }
    }

    // Stops the inner thread loop and also calls AudioRecord.stop().
    // Does not block the calling thread.
    public void stopThread() {
      Logging.d(TAG, "stopThread");
      keepAlive = false;
    }
  }

  WebRtcAudioRecord(long nativeAudioRecord, int type) {
    Logging.d(TAG, "ctor" + WebRtcAudioUtils.getThreadInfo());
    this.nativeAudioRecord = nativeAudioRecord;
    effects = WebRtcAudioEffects.create();
    captureType = type;
    if (captureType == 2 && Instance == null) {
      Instance = this;
    }
  }

  private void onDestroy() {
    stopDeviceAudioRecord();
    if (Instance == this) {
      Instance = null;
    }
  }

  private boolean enableBuiltInAEC(boolean enable) {
    Logging.d(TAG, "enableBuiltInAEC(" + enable + ')');
    if (effects == null) {
      Logging.e(TAG, "Built-in AEC is not supported on this platform");
      return false;
    }
    return effects.setAEC(enable);
  }

  private boolean enableBuiltInNS(boolean enable) {
    Logging.d(TAG, "enableBuiltInNS(" + enable + ')');
    if (effects == null) {
      Logging.e(TAG, "Built-in NS is not supported on this platform");
      return false;
    }
    return effects.setNS(enable);
  }

  private int initRecording(int sampleRate, int channels) {
    if (captureType == 1 && Build.VERSION.SDK_INT < 29) {
      return -1;
    }
    requestedSampleRate = sampleRate;
    requestedChannels = channels;
    Logging.d(TAG, "initRecording(sampleRate=" + sampleRate + ", channels=" + channels + ")");
    if (audioRecord != null) {
      reportWebRtcAudioRecordInitError("InitRecording called twice without StopRecording.");
      return -1;
    }
    final int bytesPerFrame = channels * (BITS_PER_SAMPLE / 8);
    final int framesPerBuffer = sampleRate / BUFFERS_PER_SECOND;
    byteBuffer = ByteBuffer.allocateDirect(bytesPerFrame * framesPerBuffer);
    byteBuffer.order(ByteOrder.nativeOrder());
    Logging.d(TAG, "byteBuffer.capacity: " + byteBuffer.capacity());
    emptyBytes = new byte[byteBuffer.capacity()];
    // Rather than passing the ByteBuffer with every callback (requiring
    // the potentially expensive GetDirectBufferAddress) we simply have the
    // the native class cache the address to the memory once.
    nativeCacheDirectBufferAddress(byteBuffer, nativeAudioRecord);

    // Get the minimum buffer size required for the successful creation of
    // an AudioRecord object, in byte units.
    // Note that this size doesn't guarantee a smooth recording under load.
    final int channelConfig = channelCountToConfiguration(channels);
    int minBufferSize =
        AudioRecord.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
    if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
      reportWebRtcAudioRecordInitError("AudioRecord.getMinBufferSize failed: " + minBufferSize);
      return -1;
    }
    Logging.d(TAG, "AudioRecord.getMinBufferSize: " + minBufferSize);

    // Use a larger buffer size than the minimum required when creating the
    // AudioRecord instance to ensure smooth recording under load. It has been
    // verified that it does not increase the actual recording latency.
    int bufferSizeInBytes = Math.max(BUFFER_SIZE_FACTOR * minBufferSize, byteBuffer.capacity());
    Logging.d(TAG, "bufferSizeInBytes: " + bufferSizeInBytes);

    if (captureType == 1) {
      if (Build.VERSION.SDK_INT >= 29) {
        try {
          MediaProjection projection = VideoCapturerDevice.getMediaProjection();
          if (projection == null) {
            return -1;
          }
          AudioPlaybackCaptureConfiguration.Builder builder = new AudioPlaybackCaptureConfiguration.Builder(projection);
          builder.addMatchingUsage(AudioAttributes.USAGE_MEDIA);
          builder.addMatchingUsage(AudioAttributes.USAGE_GAME);
          builder.addMatchingUsage(AudioAttributes.USAGE_UNKNOWN);

          AudioRecord.Builder audioRecordBuilder = new AudioRecord.Builder();
          audioRecordBuilder.setAudioPlaybackCaptureConfig(builder.build());
          audioRecordBuilder.setAudioFormat(new AudioFormat.Builder().setChannelMask(channelConfig).setSampleRate(sampleRate).setEncoding(AudioFormat.ENCODING_PCM_16BIT).build());
          audioRecordBuilder.setBufferSizeInBytes(bufferSizeInBytes);
          audioRecord = audioRecordBuilder.build();
        } catch (Throwable e) {
          reportWebRtcAudioRecordInitError("AudioRecord ctor error: " + e.getMessage());
          releaseAudioResources(false);
          return -1;
        }
      }
    } else {
      try {
        audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
      } catch (IllegalArgumentException e) {
        reportWebRtcAudioRecordInitError("AudioRecord ctor error: " + e.getMessage());
        releaseAudioResources(false);
        return -1;
      }
    }
    if (audioRecord == null || audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
      reportWebRtcAudioRecordInitError("Failed to create a new AudioRecord instance");
      releaseAudioResources(false);
      return -1;
    }
    if (captureType == 0 && effects != null) {
      effects.enable(audioRecord.getAudioSessionId());
    }
    logMainParameters();
    logMainParametersExtended();
    return framesPerBuffer;
  }

  @TargetApi(29)
  public void initDeviceAudioRecord(MediaProjection mediaProjection) {
    if (Build.VERSION.SDK_INT < 29) {
      return;
    }
    final int bytesPerFrame = requestedChannels * (BITS_PER_SAMPLE / 8);
    final int framesPerBuffer = requestedSampleRate / BUFFERS_PER_SECOND;
    deviceByteBuffer = ByteBuffer.allocateDirect(bytesPerFrame * framesPerBuffer);
    deviceByteBuffer.order(ByteOrder.nativeOrder());

    final int channelConfig = channelCountToConfiguration(requestedChannels);
    int minBufferSize = AudioRecord.getMinBufferSize(requestedSampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
    if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
      reportWebRtcAudioRecordInitError("AudioRecord.getMinBufferSize failed: " + minBufferSize);
      return;
    }

    int bufferSizeInBytes = Math.max(BUFFER_SIZE_FACTOR * minBufferSize, deviceByteBuffer.capacity());
    try {
      AudioPlaybackCaptureConfiguration.Builder builder = new AudioPlaybackCaptureConfiguration.Builder(mediaProjection);
      builder.addMatchingUsage(AudioAttributes.USAGE_MEDIA);
      builder.addMatchingUsage(AudioAttributes.USAGE_GAME);

      AudioRecord.Builder audioRecordBuilder = new AudioRecord.Builder();
      audioRecordBuilder.setAudioPlaybackCaptureConfig(builder.build());
      audioRecordBuilder.setAudioFormat(new AudioFormat.Builder().setChannelMask(channelConfig).setSampleRate(requestedSampleRate).setEncoding(AudioFormat.ENCODING_PCM_16BIT).build());
      audioRecordBuilder.setBufferSizeInBytes(bufferSizeInBytes);
      deviceAudioRecord = audioRecordBuilder.build();
    } catch (Throwable e) {
      reportWebRtcAudioRecordInitError("AudioRecord ctor error: " + e.getMessage());
      releaseAudioResources(true);
      return;
    }
    if (deviceAudioRecord == null || deviceAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
      reportWebRtcAudioRecordInitError("Failed to create a new AudioRecord instance");
      releaseAudioResources(true);
      return;
    }
    try {
      deviceAudioRecord.startRecording();
    } catch (IllegalStateException e) {
      reportWebRtcAudioRecordStartError(AudioRecordStartErrorCode.AUDIO_RECORD_START_EXCEPTION, "AudioRecord.startRecording failed: " + e.getMessage());
      return;
    }
    if (deviceAudioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
      reportWebRtcAudioRecordStartError(AudioRecordStartErrorCode.AUDIO_RECORD_START_STATE_MISMATCH, "AudioRecord.startRecording failed - incorrect state :" + deviceAudioRecord.getRecordingState());
    }
  }

  @TargetApi(29)
  public void stopDeviceAudioRecord() {
    if (deviceAudioRecord == null) {
      return;
    }
    try {
      deviceAudioRecord.stop();
    } catch (Throwable e) {
      FileLog.e(e);
    }
    releaseAudioResources(true);
  }

  private boolean startRecording() {
    Logging.d(TAG, "startRecording");
    assertTrue(audioRecord != null);
    assertTrue(audioThread == null);
    try {
      audioRecord.startRecording();
    } catch (IllegalStateException e) {
      reportWebRtcAudioRecordStartError(AudioRecordStartErrorCode.AUDIO_RECORD_START_EXCEPTION, "AudioRecord.startRecording failed: " + e.getMessage());
      return false;
    }
    if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
      reportWebRtcAudioRecordStartError(AudioRecordStartErrorCode.AUDIO_RECORD_START_STATE_MISMATCH, "AudioRecord.startRecording failed - incorrect state :" + audioRecord.getRecordingState());
      return false;
    }
    audioThread = new AudioRecordThread("AudioRecordJavaThread");
    audioThread.start();
    return true;
  }

  private boolean stopRecording() {
    Logging.d(TAG, "stopRecording");
    assertTrue(audioThread != null);
    audioThread.stopThread();
    if (!ThreadUtils.joinUninterruptibly(audioThread, AUDIO_RECORD_THREAD_JOIN_TIMEOUT_MS)) {
      Logging.e(TAG, "Join of AudioRecordJavaThread timed out");
      WebRtcAudioUtils.logAudioState(TAG);
    }
    audioThread = null;
    if (effects != null) {
      effects.release();
    }
    try {
      audioRecord.stop();
    } catch (Throwable e) {
      FileLog.e(e);
    }
    releaseAudioResources(false);
    return true;
  }

  private void logMainParameters() {
    Logging.d(TAG, "AudioRecord: "
            + "session ID: " + audioRecord.getAudioSessionId() + ", "
            + "channels: " + audioRecord.getChannelCount() + ", "
            + "sample rate: " + audioRecord.getSampleRate());
  }

  private void logMainParametersExtended() {
    if (Build.VERSION.SDK_INT >= 23) {
      Logging.d(TAG, "AudioRecord: "
              // The frame count of the native AudioRecord buffer.
              + "buffer size in frames: " + audioRecord.getBufferSizeInFrames());
    }
  }

  // Helper method which throws an exception  when an assertion has failed.
  private static void assertTrue(boolean condition) {
    if (!condition) {
      throw new AssertionError("Expected condition to be true");
    }
  }

  private int channelCountToConfiguration(int channels) {
    return (channels == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO);
  }

  private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer, long nativeAudioRecord);

  private native void nativeDataIsRecorded(int bytes, long nativeAudioRecord);

  @SuppressWarnings("NoSynchronizedMethodCheck")
  public static synchronized void setAudioSource(int source) {
    Logging.w(TAG, "Audio source is changed from: " + audioSource
            + " to " + source);
    audioSource = source;
  }

  private static int getDefaultAudioSource() {
    return AudioSource.VOICE_COMMUNICATION;
  }

  // Sets all recorded samples to zero if |mute| is true, i.e., ensures that
  // the microphone is muted.
  public static void setMicrophoneMute(boolean mute) {
    Logging.w(TAG, "setMicrophoneMute(" + mute + ")");
    microphoneMute = mute;
  }

  // Releases the native AudioRecord resources.
  private void releaseAudioResources(boolean device) {
    Logging.d(TAG, "releaseAudioResources " + device);
    if (device) {
      if (deviceAudioRecord != null) {
        deviceAudioRecord.release();
        deviceAudioRecord = null;
      }
    } else {
      if (audioRecord != null) {
        audioRecord.release();
        audioRecord = null;
      }
    }
  }

  private void reportWebRtcAudioRecordInitError(String errorMessage) {
    Logging.e(TAG, "Init recording error: " + errorMessage);
    WebRtcAudioUtils.logAudioState(TAG);
    if (errorCallback != null) {
      errorCallback.onWebRtcAudioRecordInitError(errorMessage);
    }
  }

  private void reportWebRtcAudioRecordStartError(
      AudioRecordStartErrorCode errorCode, String errorMessage) {
    Logging.e(TAG, "Start recording error: " + errorCode + ". " + errorMessage);
    WebRtcAudioUtils.logAudioState(TAG);
    if (errorCallback != null) {
      errorCallback.onWebRtcAudioRecordStartError(errorCode, errorMessage);
    }
  }

  private void reportWebRtcAudioRecordError(String errorMessage) {
    Logging.e(TAG, "Run-time recording error: " + errorMessage);
    WebRtcAudioUtils.logAudioState(TAG);
    if (errorCallback != null) {
      errorCallback.onWebRtcAudioRecordError(errorMessage);
    }
  }
}
