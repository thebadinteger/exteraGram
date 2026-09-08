public final class PlayerEmsgHandler implements Handler.Callback {

  private static final int EMSG_MANIFEST_EXPIRED = 1;

  private static boolean isPlayerEmsgEvent(String schemeIdUri, String value) {
    return "urn:mpeg:dash:event:2012".equals(schemeIdUri)
        && ("1".equals(value) || "2".equals(value) || "3".equals(value));
  }

  /** Handles emsg messages for a specific track for the player. */
  public final class PlayerTrackEmsgHandler implements TrackOutput {

    private final SampleQueue sampleQueue;
    private final FormatHolder formatHolder;
    private final MetadataInputBuffer buffer;

    private long maxLoadedChunkEndTimeUs;

    /* package */ PlayerTrackEmsgHandler(Allocator allocator) {
      this.sampleQueue = SampleQueue.createWithoutDrm(allocator);
      formatHolder = new FormatHolder();
      buffer = new MetadataInputBuffer();
      maxLoadedChunkEndTimeUs = C.TIME_UNSET;
    }

    @Override
    public void format(Format format) {
      sampleQueue.format(format);
    }

    @Override
    public int sampleData(
        DataReader input, int length, boolean allowEndOfInput, @SampleDataPart int sampleDataPart)
        throws IOException {
      return sampleQueue.sampleData(input, length, allowEndOfInput);
    }

    @Override
    public void sampleData(ParsableByteArray data, int length, @SampleDataPart int sampleDataPart) {
      sampleQueue.sampleData(data, length);
    }

    @Override
    public void sampleMetadata(
        long timeUs, int flags, int size, int offset, @Nullable CryptoData cryptoData) {
      sampleQueue.sampleMetadata(timeUs, flags, size, offset, cryptoData);
      parseAndDiscardSamples();
    }

    /**
     * For live streaming, check if the DASH manifest is expired before the next segment start time.
     * If it is, the DASH media source will be notified to refresh the manifest.
     *
     * @param presentationPositionUs The next load position in presentation time.
     * @return True if manifest refresh has been requested, false otherwise.
     */
    public boolean maybeRefreshManifestBeforeLoadingNextChunk(long presentationPositionUs) {
      return PlayerEmsgHandler.this.maybeRefreshManifestBeforeLoadingNextChunk(
          presentationPositionUs);
    }

    /**
     * Called when a chunk load has been completed.
     *
     * @param chunk The chunk whose load has been completed.
     */
    public void onChunkLoadCompleted(Chunk chunk) {
      if (maxLoadedChunkEndTimeUs == C.TIME_UNSET || chunk.endTimeUs > maxLoadedChunkEndTimeUs) {
        maxLoadedChunkEndTimeUs = chunk.endTimeUs;
      }
      PlayerEmsgHandler.this.onChunkLoadCompleted(chunk);
    }

    /**
     * Called when a chunk load has encountered an error.
     *
     * @param chunk The chunk whose load encountered an error.
     * @return Whether a manifest refresh has been requested.
     */
    public boolean onChunkLoadError(Chunk chunk) {
      boolean isAfterForwardSeek =
          maxLoadedChunkEndTimeUs != C.TIME_UNSET && maxLoadedChunkEndTimeUs < chunk.startTimeUs;
      return PlayerEmsgHandler.this.onChunkLoadError(isAfterForwardSeek);
    }

    /** Release this track emsg handler. It should not be reused after this call. */
    public void release() {
      sampleQueue.release();
    }

    // Internal methods.

    private void parseAndDiscardSamples() {
      while (sampleQueue.isReady(/* loadingFinished= */ false)) {
        @Nullable MetadataInputBuffer inputBuffer = dequeueSample();
        if (inputBuffer == null) {
          continue;
        }
        long eventTimeUs = inputBuffer.timeUs;
        @Nullable Metadata metadata = decoder.decode(inputBuffer);
        if (metadata == null) {
          continue;
        }
        EventMessage eventMessage = (EventMessage) metadata.get(0);
        if (isPlayerEmsgEvent(eventMessage.schemeIdUri, eventMessage.value)) {
          parsePlayerEmsgEvent(eventTimeUs, eventMessage);
        }
      }
      sampleQueue.discardToRead();
    }

    @Nullable
    private MetadataInputBuffer dequeueSample() {
      buffer.clear();
      int result =
          sampleQueue.read(formatHolder, buffer, /* readFlags= */ 0, /* loadingFinished= */ false);
      if (result == C.RESULT_BUFFER_READ) {
        buffer.flip();
        return buffer;
      }
      return null;
    }

    private void parsePlayerEmsgEvent(long eventTimeUs, EventMessage eventMessage) {
      long manifestPublishTimeMsInEmsg = getManifestPublishTimeMsInEmsg(eventMessage);
      if (manifestPublishTimeMsInEmsg == C.TIME_UNSET) {
        return;
      }
      onManifestExpiredMessageEncountered(eventTimeUs, manifestPublishTimeMsInEmsg);
    }

    private void onManifestExpiredMessageEncountered(
        long eventTimeUs, long manifestPublishTimeMsInEmsg) {
      ManifestExpiryEventInfo manifestExpiryEventInfo =
          new ManifestExpiryEventInfo(eventTimeUs, manifestPublishTimeMsInEmsg);
      handler.sendMessage(handler.obtainMessage(EMSG_MANIFEST_EXPIRED, manifestExpiryEventInfo));
    }
  }

  /** Holds information related to a manifest expiry event. */
  private static final class ManifestExpiryEventInfo {

    public final long eventTimeUs;
    public final long manifestPublishTimeMsInEmsg;

    public ManifestExpiryEventInfo(long eventTimeUs, long manifestPublishTimeMsInEmsg) {
      this.eventTimeUs = eventTimeUs;
      this.manifestPublishTimeMsInEmsg = manifestPublishTimeMsInEmsg;
    }
  }
}
