private class AudioTrackThread extends Thread {
    private volatile boolean keepAlive = true;

    private long writtenFrames = 0;
    private long lastPlaybackHeadPosition = 0;
    private long lastTimestamp = System.nanoTime();
    private long targetTimeNs;

    public AudioTrackThread(String name) {
      super(name);
    }

    @Override
    public void run() {
      Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
      Logging.d(TAG, "AudioTrackThread" + WebRtcAudioUtils.getThreadInfo());
      assertTrue(audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING);

      // Fixed size in bytes of each 10ms block of audio data that we ask for
      // using callbacks to the native WebRTC client.
      final int sizeInBytes = byteBuffer.capacity();
      final int bytesPerFrame = audioTrack.getChannelCount() * (BITS_PER_SAMPLE / 8);
      final int sampleRate = audioTrack.getSampleRate();

      targetTimeNs = System.nanoTime();
      boolean blocking = false;

      while (keepAlive && audioTrack != null) {
        // Get 10ms of PCM data from the native WebRTC client. Audio data is
        // written into the common ByteBuffer using the address that was
        // cached at construction.
        try {
          nativeGetPlayoutData(sizeInBytes, nativeAudioTrack);
        } catch (Throwable e) {
          keepAlive = false;
          continue;
        }
        // Write data until all data has been written to the audio sink.
        // Upon return, the buffer position will have been advanced to reflect
        // the amount of data that was successfully written to the AudioTrack.
        assertTrue(sizeInBytes <= byteBuffer.remaining());
        if (speakerMute) {
          byteBuffer.clear();
          byteBuffer.put(emptyBytes);
          byteBuffer.position(0);
        }
        int bytesWritten = writeBytes(audioTrack, byteBuffer, sizeInBytes, blocking);
        if (bytesWritten != sizeInBytes) {
          Logging.e(TAG, "AudioTrack.write played invalid number of bytes: " + bytesWritten);
          // If a write() returns a negative value, an error has occurred.
          // Stop playing and report an error in this case.
          if (bytesWritten < 0) {
            keepAlive = false;
            reportWebRtcAudioTrackError("AudioTrack.write failed: " + bytesWritten);
          }
        }
        // The byte buffer must be rewinded since byteBuffer.position() is
        // increased at each call to AudioTrack.write(). If we don't do this,
        // next call to AudioTrack.write() will fail.
        byteBuffer.rewind();
        blocking = !blocking;

        // The byte buffer must be rewinded since byteBuffer.position() is
        // increased at each call to AudioTrack.write(). If we don't do this,
        // next call to AudioTrack.write() will fail.
        byteBuffer.rewind();

        // Calculate the time to sleep to maintain a steady playback rate
        targetTimeNs += CALLBACK_BUFFER_SIZE_MS * 1_000_000L; // 10ms in nanoseconds
        long currentTimeNs = System.nanoTime();
        long sleepTimeNs = targetTimeNs - currentTimeNs;
        if (sleepTimeNs > 0) {
          try {
            Thread.sleep(sleepTimeNs / 1_000_000L, (int) (sleepTimeNs % 1_000_000L));
          } catch (InterruptedException e) {
            FileLog.e(e);
          }
        } else {
          // Missed deadline
          targetTimeNs = System.nanoTime(); // Reset target time to current time
        }
      }

      // Stops playing the audio data. Since the instance was created in
      // MODE_STREAM mode, audio will stop playing after the last buffer that
      // was written has been played.
      if (audioTrack != null) {
        Logging.d(TAG, "Calling AudioTrack.stop...");
        try {
          audioTrack.stop();
          Logging.d(TAG, "AudioTrack.stop is done.");
        } catch (Exception e) {
          Logging.e(TAG, "AudioTrack.stop failed: " + e.getMessage());
        }
      }
    }

    private int writeBytes(AudioTrack audioTrack, ByteBuffer byteBuffer, int sizeInBytes, boolean blocking) {
      if (audioTrack == null) return 0;
      if (Build.VERSION.SDK_INT >= 21) {
        return audioTrack.write(byteBuffer, sizeInBytes, blocking ? AudioTrack.WRITE_BLOCKING : AudioTrack.WRITE_NON_BLOCKING);
      } else {
        return audioTrack.write(byteBuffer.array(), byteBuffer.arrayOffset(), sizeInBytes);
      }
    }

    // Stops the inner thread loop which results in calling AudioTrack.stop().
    // Does not block the calling thread.
    public void stopThread() {
      Logging.d(TAG, "stopThread");
      keepAlive = false;
    }
  }

  WebRtcAudioTrack(long nativeAudioTrack) {
    threadChecker.checkIsOnValidThread();
    Logging.d(TAG, "ctor" + WebRtcAudioUtils.getThreadInfo());
    this.nativeAudioTrack = nativeAudioTrack;
    audioManager =
        (AudioManager) ContextUtils.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    if (DEBUG) {
      WebRtcAudioUtils.logDeviceInfo(TAG);
    }
  }

  private int initPlayout(int sampleRate, int channels, double bufferSizeFactor) {
    threadChecker.checkIsOnValidThread();
    Logging.d(TAG,
        "initPlayout(sampleRate=" + sampleRate + ", channels=" + channels
            + ", bufferSizeFactor=" + bufferSizeFactor + ")");
    final int bytesPerFrame = channels * (BITS_PER_SAMPLE / 8);
    byteBuffer = ByteBuffer.allocateDirect(bytesPerFrame * (sampleRate / BUFFERS_PER_SECOND));
    Logging.d(TAG, "byteBuffer.capacity: " + byteBuffer.capacity());
    emptyBytes = new byte[byteBuffer.capacity()];
    // Rather than passing the ByteBuffer with every callback (requiring
    // the potentially expensive GetDirectBufferAddress) we simply have the
    // the native class cache the address to the memory once.
    nativeCacheDirectBufferAddress(byteBuffer, nativeAudioTrack);

    // Get the minimum buffer size required for the successful creation of an
    // AudioTrack object to be created in the MODE_STREAM mode.
    // Note that this size doesn't guarantee a smooth playback under load.
    final int channelConfig = channelCountToConfiguration(channels);
    final int minBufferSizeInBytes = (int) (AudioTrack.getMinBufferSize(sampleRate, channelConfig,
                                                AudioFormat.ENCODING_PCM_16BIT)
        * bufferSizeFactor);
    Logging.d(TAG, "minBufferSizeInBytes: " + minBufferSizeInBytes);
    // For the streaming mode, data must be written to the audio sink in
    // chunks of size (given by byteBuffer.capacity()) less than or equal
    // to the total buffer size |minBufferSizeInBytes|. But, we have seen
    // reports of "getMinBufferSize(): error querying hardware". Hence, it
    // can happen that |minBufferSizeInBytes| contains an invalid value.
    if (minBufferSizeInBytes < byteBuffer.capacity()) {
      reportWebRtcAudioTrackInitError("AudioTrack.getMinBufferSize returns an invalid value.");
      return -1;
    }

    // Ensure that prevision audio session was stopped correctly before trying
    // to create a new AudioTrack.
    if (audioTrack != null) {
      reportWebRtcAudioTrackInitError("Conflict with existing AudioTrack.");
      return -1;
    }
    try {
      // Create an AudioTrack object and initialize its associated audio buffer.
      // The size of this buffer determines how long an AudioTrack can play
      // before running out of data.
      if (Build.VERSION.SDK_INT >= 21) {
        // If we are on API level 21 or higher, it is possible to use a special AudioTrack
        // constructor that uses AudioAttributes and AudioFormat as input. It allows us to
        // supersede the notion of stream types for defining the behavior of audio playback,
        // and to allow certain platforms or routing policies to use this information for more
        // refined volume or routing decisions.
        audioTrack = createAudioTrackOnLollipopOrHigher(
            sampleRate, channelConfig, minBufferSizeInBytes);
      } else {
        // Use default constructor for API levels below 21.
        audioTrack =
            createAudioTrackOnLowerThanLollipop(sampleRate, channelConfig, minBufferSizeInBytes);
      }
    } catch (IllegalArgumentException e) {
      reportWebRtcAudioTrackInitError(e.getMessage());
      releaseAudioResources();
      return -1;
    }

    // It can happen that an AudioTrack is created but it was not successfully
    // initialized upon creation. Seems to be the case e.g. when the maximum
    // number of globally available audio tracks is exceeded.
    if (audioTrack == null || audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
      reportWebRtcAudioTrackInitError("Initialization of audio track failed.");
      releaseAudioResources();
      return -1;
    }
    logMainParameters();
    logMainParametersExtended();
    return minBufferSizeInBytes;
  }

  private boolean startPlayout() {
    threadChecker.checkIsOnValidThread();
    Logging.d(TAG, "startPlayout");
    assertTrue(audioTrack != null);
    assertTrue(audioThread == null);

    // Starts playing an audio track.
    try {
      audioTrack.play();
    } catch (IllegalStateException e) {
      reportWebRtcAudioTrackStartError(AudioTrackStartErrorCode.AUDIO_TRACK_START_EXCEPTION,
          "AudioTrack.play failed: " + e.getMessage());
      releaseAudioResources();
      return false;
    }
    if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
      reportWebRtcAudioTrackStartError(
          AudioTrackStartErrorCode.AUDIO_TRACK_START_STATE_MISMATCH,
          "AudioTrack.play failed - incorrect state :"
          + audioTrack.getPlayState());
      releaseAudioResources();
      return false;
    }

    // Create and start new high-priority thread which calls AudioTrack.write()
    // and where we also call the native nativeGetPlayoutData() callback to
    // request decoded audio from WebRTC.
    audioThread = new AudioTrackThread("AudioTrackJavaThread");
    audioThread.start();
    return true;
  }

  private boolean stopPlayout() {
    try {
      threadChecker.checkIsOnValidThread();
      Logging.d(TAG, "stopPlayout");
      assertTrue(audioThread != null);
      logUnderrunCount();
      audioThread.stopThread();

      Logging.d(TAG, "Stopping the AudioTrackThread...");
      audioThread.interrupt();
      if (!ThreadUtils.joinUninterruptibly(audioThread, AUDIO_TRACK_THREAD_JOIN_TIMEOUT_MS)) {
        Logging.e(TAG, "Join of AudioTrackThread timed out.");
        WebRtcAudioUtils.logAudioState(TAG);
      }
      Logging.d(TAG, "AudioTrackThread has now been stopped.");
    } catch (Throwable e) {
      FileLog.e(e);
    } finally {
      audioThread = null;
    }
    try {
      releaseAudioResources();
    } catch (Throwable e) {
      FileLog.e(e);
    }
    return true;
  }

  // Get max possible volume index for a phone call audio stream.
  private int getStreamMaxVolume() {
    threadChecker.checkIsOnValidThread();
    Logging.d(TAG, "getStreamMaxVolume");
    assertTrue(audioManager != null);
    return audioManager.getStreamMaxVolume(streamType);
  }

  // Set current volume level for a phone call audio stream.
  private boolean setStreamVolume(int volume) {
    threadChecker.checkIsOnValidThread();
    Logging.d(TAG, "setStreamVolume(" + volume + ")");
    assertTrue(audioManager != null);
    if (isVolumeFixed()) {
      Logging.e(TAG, "The device implements a fixed volume policy.");
      return false;
    }
    audioManager.setStreamVolume(streamType, volume, 0);
    return true;
  }

  private boolean isVolumeFixed() {
    if (Build.VERSION.SDK_INT < 21)
      return false;
    return audioManager.isVolumeFixed();
  }

  /** Get current volume level for a phone call audio stream. */
  private int getStreamVolume() {
    threadChecker.checkIsOnValidThread();
    Logging.d(TAG, "getStreamVolume");
    assertTrue(audioManager != null);
    return audioManager.getStreamVolume(streamType);
  }

  private void logMainParameters() {
    Logging.d(TAG, "AudioTrack: "
            + "session ID: " + audioTrack.getAudioSessionId() + ", "
            + "channels: " + audioTrack.getChannelCount() + ", "
            + "sample rate: " + audioTrack.getSampleRate() + ", "
            // Gain (>=1.0) expressed as linear multiplier on sample values.
            + "max gain: " + AudioTrack.getMaxVolume());
  }

  // Creates and AudioTrack instance using AudioAttributes and AudioFormat as input.
  // It allows certain platforms or routing policies to use this information for more
  // refined volume or routing decisions.
  @TargetApi(21)
  private static AudioTrack createAudioTrackOnLollipopOrHigher(
      int sampleRateInHz, int channelConfig, int bufferSizeInBytes) {
    Logging.d(TAG, "createAudioTrackOnLollipopOrHigher");
    // TODO(henrika): use setPerformanceMode(int) with PERFORMANCE_MODE_LOW_LATENCY to control
    // performance when Android O is supported. Add some logging in the mean time.
    final int nativeOutputSampleRate =
        AudioTrack.getNativeOutputSampleRate(streamType);
    Logging.d(TAG, "nativeOutputSampleRate: " + nativeOutputSampleRate);
    if (sampleRateInHz != nativeOutputSampleRate) {
      Logging.w(TAG, "Unable to use fast mode since requested sample rate is not native");
    }
    if (usageAttribute != DEFAULT_USAGE) {
      Logging.w(TAG, "A non default usage attribute is used: " + usageAttribute);
    }
    // Create an audio track where the audio usage is for VoIP and the content type is speech.
    return new AudioTrack(
        new AudioAttributes.Builder()
            .setUsage(usageAttribute)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build(),
        new AudioFormat.Builder()
          .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
          .setSampleRate(sampleRateInHz)
          .setChannelMask(channelConfig)
          .build(),
        bufferSizeInBytes,
        AudioTrack.MODE_STREAM,
        AudioManager.AUDIO_SESSION_ID_GENERATE);
  }

  @SuppressWarnings("deprecation") // Deprecated in API level 25.
  private static AudioTrack createAudioTrackOnLowerThanLollipop(
      int sampleRateInHz, int channelConfig, int bufferSizeInBytes) {
    return new AudioTrack(streamType, sampleRateInHz, channelConfig,
        AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);
  }

  private void logBufferSizeInFrames() {
    if (Build.VERSION.SDK_INT >= 23) {
      Logging.d(TAG, "AudioTrack: "
              // The effective size of the AudioTrack buffer that the app writes to.
              + "buffer size in frames: " + audioTrack.getBufferSizeInFrames());
    }
  }

  private int getBufferSizeInFrames() {
    if (Build.VERSION.SDK_INT >= 23) {
      return audioTrack.getBufferSizeInFrames();
    }
    return -1;
  }

  private void logBufferCapacityInFrames() {
    if (Build.VERSION.SDK_INT >= 24) {
      Logging.d(TAG,
          "AudioTrack: "
              // Maximum size of the AudioTrack buffer in frames.
              + "buffer capacity in frames: " + audioTrack.getBufferCapacityInFrames());
    }
  }

  private void logMainParametersExtended() {
    logBufferSizeInFrames();
    logBufferCapacityInFrames();
  }

  // Prints the number of underrun occurrences in the application-level write
  // buffer since the AudioTrack was created. An underrun occurs if the app does
  // not write audio data quickly enough, causing the buffer to underflow and a
  // potential audio glitch.
  // TODO(henrika): keep track of this value in the field and possibly add new
  // UMA stat if needed.
  private void logUnderrunCount() {
    if (Build.VERSION.SDK_INT >= 24) {
      Logging.d(TAG, "underrun count: " + audioTrack.getUnderrunCount());
    }
  }

  // Helper method which throws an exception  when an assertion has failed.
  private static void assertTrue(boolean condition) {
    if (!condition) {
      throw new AssertionError("Expected condition to be true");
    }
  }

  private int channelCountToConfiguration(int channels) {
    return (channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO);
  }

  private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer, long nativeAudioRecord);

  private native void nativeGetPlayoutData(int bytes, long nativeAudioRecord);

  // Sets all samples to be played out to zero if |mute| is true, i.e.,
  // ensures that the speaker is muted.
  public static void setSpeakerMute(boolean mute) {
    Logging.w(TAG, "setSpeakerMute(" + mute + ")");
    speakerMute = mute;
  }

  public static boolean isSpeakerMuted() {
    return speakerMute;
  }

  // Releases the native AudioTrack resources.
  private void releaseAudioResources() {
    Logging.e(TAG, "releaseAudioResources", new Exception());
    if (audioTrack != null) {
      AudioTrack track = audioTrack;
      audioTrack = null;
      try {
        track.release();
      } catch (Throwable e) {
        FileLog.e(e);
      }
    }
  }

  private void reportWebRtcAudioTrackInitError(String errorMessage) {
    Logging.e(TAG, "Init playout error: " + errorMessage);
    WebRtcAudioUtils.logAudioState(TAG);
    if (errorCallbackOld != null) {
      errorCallbackOld.onWebRtcAudioTrackInitError(errorMessage);
    }
    if (errorCallback != null) {
      errorCallback.onWebRtcAudioTrackInitError(errorMessage);
    }
  }

  private void reportWebRtcAudioTrackStartError(
      AudioTrackStartErrorCode errorCode, String errorMessage) {
    Logging.e(TAG, "Start playout error: "  + errorCode + ". " + errorMessage);
    WebRtcAudioUtils.logAudioState(TAG);
    if (errorCallbackOld != null) {
      errorCallbackOld.onWebRtcAudioTrackStartError(errorMessage);
    }
    if (errorCallback != null) {
      errorCallback.onWebRtcAudioTrackStartError(errorCode, errorMessage);
    }
  }

  private void reportWebRtcAudioTrackError(String errorMessage) {
    Logging.e(TAG, "Run-time playback error: " + errorMessage);
    WebRtcAudioUtils.logAudioState(TAG);
    if (errorCallbackOld != null) {
      errorCallbackOld.onWebRtcAudioTrackError(errorMessage);
    }
    if (errorCallback != null) {
      errorCallback.onWebRtcAudioTrackError(errorMessage);
    }
  }
}
