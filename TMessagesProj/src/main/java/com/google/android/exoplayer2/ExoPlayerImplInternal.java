false);
        pendingMessages.remove(i);
      }
    }
    // Re-sort messages by playback order.
    Collections.sort(pendingMessages);
  }

  private void maybeTriggerPendingMessages(long oldPeriodPositionUs, long newPeriodPositionUs)
      throws ExoPlaybackException {
    if (pendingMessages.isEmpty() || playbackInfo.periodId.isAd()) {
      return;
    }
    // If this is the first call after resetting the renderer position, include oldPeriodPositionUs
    // in potential trigger positions, but make sure we deliver it only once.
    if (deliverPendingMessageAtStartPositionRequired) {
      oldPeriodPositionUs--;
      deliverPendingMessageAtStartPositionRequired = false;
    }

    // Correct next index if necessary (e.g. after seeking, timeline changes, or new messages)
    int currentPeriodIndex =
        playbackInfo.timeline.getIndexOfPeriod(playbackInfo.periodId.periodUid);
    int nextPendingMessageIndex = min(nextPendingMessageIndexHint, pendingMessages.size());
    PendingMessageInfo previousInfo =
        nextPendingMessageIndex > 0 ? pendingMessages.get(nextPendingMessageIndex - 1) : null;
    while (previousInfo != null
        && (previousInfo.resolvedPeriodIndex > currentPeriodIndex
            || (previousInfo.resolvedPeriodIndex == currentPeriodIndex
                && previousInfo.resolvedPeriodTimeUs > oldPeriodPositionUs))) {
      nextPendingMessageIndex--;
      previousInfo =
          nextPendingMessageIndex > 0 ? pendingMessages.get(nextPendingMessageIndex - 1) : null;
    }
    PendingMessageInfo nextInfo =
        nextPendingMessageIndex < pendingMessages.size()
            ? pendingMessages.get(nextPendingMessageIndex)
            : null;
    while (nextInfo != null
        && nextInfo.resolvedPeriodUid != null
        && (nextInfo.resolvedPeriodIndex < currentPeriodIndex
            || (nextInfo.resolvedPeriodIndex == currentPeriodIndex
                && nextInfo.resolvedPeriodTimeUs <= oldPeriodPositionUs))) {
      nextPendingMessageIndex++;
      nextInfo =
          nextPendingMessageIndex < pendingMessages.size()
              ? pendingMessages.get(nextPendingMessageIndex)
              : null;
    }
    // Check if any message falls within the covered time span.
    while (nextInfo != null
        && nextInfo.resolvedPeriodUid != null
        && nextInfo.resolvedPeriodIndex == currentPeriodIndex
        && nextInfo.resolvedPeriodTimeUs > oldPeriodPositionUs
        && nextInfo.resolvedPeriodTimeUs <= newPeriodPositionUs) {
      try {
        sendMessageToTarget(nextInfo.message);
      } finally {
        if (nextInfo.message.getDeleteAfterDelivery() || nextInfo.message.isCanceled()) {
          pendingMessages.remove(nextPendingMessageIndex);
        } else {
          nextPendingMessageIndex++;
        }
      }
      nextInfo =
          nextPendingMessageIndex < pendingMessages.size()
              ? pendingMessages.get(nextPendingMessageIndex)
              : null;
    }
    nextPendingMessageIndexHint = nextPendingMessageIndex;
  }

  private void ensureStopped(Renderer renderer) throws ExoPlaybackException {
    if (renderer.getState() == Renderer.STATE_STARTED) {
      renderer.stop();
    }
  }

  private void disableRenderer(Renderer renderer) throws ExoPlaybackException {
    if (!isRendererEnabled(renderer)) {
      return;
    }
    mediaClock.onRendererDisabled(renderer);
    ensureStopped(renderer);
    renderer.disable();
    enabledRendererCount--;
  }

  private void reselectTracksInternal() throws ExoPlaybackException {
    float playbackSpeed = mediaClock.getPlaybackParameters().speed;
    // Reselect tracks on each period in turn, until the selection changes.
    MediaPeriodHolder periodHolder = queue.getPlayingPeriod();
    MediaPeriodHolder readingPeriodHolder = queue.getReadingPeriod();
    boolean selectionsChangedForReadPeriod = true;
    TrackSelectorResult newTrackSelectorResult;
    while (true) {
      if (periodHolder == null || !periodHolder.prepared) {
        // The reselection did not change any prepared periods.
        return;
      }
      newTrackSelectorResult = periodHolder.selectTracks(playbackSpeed, playbackInfo.timeline);
      if (!newTrackSelectorResult.isEquivalent(periodHolder.getTrackSelectorResult())) {
        // Selected tracks have changed for this period.
        break;
      }
      if (periodHolder == readingPeriodHolder) {
        // The track reselection didn't affect any period that has been read.
        selectionsChangedForReadPeriod = false;
      }
      periodHolder = periodHolder.getNext();
    }

    if (selectionsChangedForReadPeriod) {
      // Update streams and rebuffer for the new selection, recreating all streams if reading ahead.
      MediaPeriodHolder playingPeriodHolder = queue.getPlayingPeriod();
      boolean recreateStreams = queue.removeAfter(playingPeriodHolder);

      boolean[] streamResetFlags = new boolean[renderers.length];
      long periodPositionUs =
          playingPeriodHolder.applyTrackSelection(
              newTrackSelectorResult, playbackInfo.positionUs, recreateStreams, streamResetFlags);
      boolean hasDiscontinuity =
          playbackInfo.playbackState != Player.STATE_ENDED
              && periodPositionUs != playbackInfo.positionUs;
      playbackInfo =
          handlePositionDiscontinuity(
              playbackInfo.periodId,
              periodPositionUs,
              playbackInfo.requestedContentPositionUs,
              playbackInfo.discontinuityStartPositionUs,
              hasDiscontinuity,
              Player.DISCONTINUITY_REASON_INTERNAL);
      if (hasDiscontinuity) {
        resetRendererPosition(periodPositionUs);
      }

      boolean[] rendererWasEnabledFlags = new boolean[renderers.length];
      for (int i = 0; i < renderers.length; i++) {
        Renderer renderer = renderers[i];
        rendererWasEnabledFlags[i] = isRendererEnabled(renderer);
        SampleStream sampleStream = playingPeriodHolder.sampleStreams[i];
        if (rendererWasEnabledFlags[i]) {
          if (sampleStream != renderer.getStream()) {
            // We need to disable the renderer.
            disableRenderer(renderer);
          } else if (streamResetFlags[i]) {
            // The renderer will continue to consume from its current stream, but needs to be reset.
            renderer.resetPosition(rendererPositionUs);
          }
        }
      }
      enableRenderers(rendererWasEnabledFlags);
    } else {
      // Release and re-prepare/buffer periods after the one whose selection changed.
      queue.removeAfter(periodHolder);
      if (periodHolder.prepared) {
        long loadingPeriodPositionUs =
            max(periodHolder.info.startPositionUs, periodHolder.toPeriodTime(rendererPositionUs));
        periodHolder.applyTrackSelection(newTrackSelectorResult, loadingPeriodPositionUs, false);
      }
    }
    handleLoadingMediaPeriodChanged(false,
              repeatMode,
              shuffleModeEnabled,
              window,
              period);
      if (periodPosition == null) {
        return false;
      }
      pendingMessageInfo.setResolvedPosition(
          newTimeline.getIndexOfPeriod(periodPositionUs.first),
          /* periodTimeUs= */ periodPositionUs.second,
          /* periodUid= */ periodPositionUs.first);
    }
    return true;
  }

  private static void resolvePendingMessageEndOfStreamPosition(
      Timeline timeline,
      PendingMessageInfo messageInfo,
      Timeline.Window window,
      Timeline.Period period) {
    int windowIndex = timeline.getPeriodByUid(messageInfo.resolvedPeriodUid, period).windowIndex;
    int lastPeriodIndex = timeline.getWindow(windowIndex, window).lastPeriodIndex;
    Object lastPeriodUid = timeline.getPeriod(lastPeriodIndex, period, /* setIds= */ true).uid;
    long positionUs = period.durationUs != C.TIME_UNSET ? period.durationUs - 1 : Long.MAX_VALUE;
    messageInfo.setResolvedPosition(lastPeriodIndex, positionUs, lastPeriodUid);
  }

  /**
   * Converts a {@link SeekPosition} into the corresponding (periodUid, periodPositionUs) for the
   * internal timeline.
   *
   * @param seekPosition The position to resolve.
   * @param trySubsequentPeriods Whether the position can be resolved to a subsequent matching
   *     period if the original period is no longer available.
   * @return The resolved position, or null if resolution was not successful.
   * @throws IllegalSeekPositionException If the window index of the seek position is outside the
   *     bounds of the timeline.
   */
  @Nullable
  private static Pair<Object, Long> resolveSeekPositionUs(
      Timeline timeline,
      SeekPosition seekPosition,
      boolean trySubsequentPeriods,
      @RepeatMode int repeatMode,
      boolean shuffleModeEnabled,
      Timeline.Window window,
      Timeline.Period period) {
    Timeline seekTimeline = seekPosition.timeline;
    if (timeline.isEmpty()) {
      // We don't have a valid timeline yet, so we can't resolve the position.
      return null;
    }
    if (seekTimeline.isEmpty()) {
      // The application performed a blind seek with an empty timeline (most likely based on
      // knowledge of what the future timeline will be). Use the internal timeline.
      seekTimeline = timeline;
    }
    // Map the SeekPosition to a position in the corresponding timeline.
    Pair<Object, Long> periodPositionUs;
    try {
      periodPositionUs =
          seekTimeline.getPeriodPositionUs(
              window, period, seekPosition.windowIndex, seekPosition.windowPositionUs);
    } catch (IndexOutOfBoundsException e) {
      // The window index of the seek position was outside the bounds of the timeline.
      return null;
    }
    if (timeline.equals(seekTimeline)) {
      // Our internal timeline is the seek timeline, so the mapped position is correct.
      return periodPositionUs;
    }
    // Attempt to find the mapped period in the internal timeline.
    int periodIndex = timeline.getIndexOfPeriod(periodPositionUs.first);
    if (periodIndex != C.INDEX_UNSET) {
      // We successfully located the period in the internal timeline.
      if (seekTimeline.getPeriodByUid(periodPositionUs.first, period).isPlaceholder
          && seekTimeline.getWindow(period.windowIndex, window).firstPeriodIndex
              == seekTimeline.getIndexOfPeriod(periodPositionUs.first)) {
        // The seek timeline was using a placeholder, so we need to re-resolve using the updated
        // timeline in case the resolved position changed. Only resolve the first period in a window
        // because subsequent periods must start at position 0 and don't need to be resolved.
        int newWindowIndex = timeline.getPeriodByUid(periodPositionUs.first, period).windowIndex;
        periodPositionUs =
            timeline.getPeriodPositionUs(
                window, period, newWindowIndex, seekPosition.windowPositionUs);
      }
      return periodPositionUs;
    }
    if (trySubsequentPeriods) {
      // Try and find a subsequent period from the seek timeline in the internal timeline.
      @Nullable
      Object periodUid =
          resolveSubsequentPeriod(
              window,
              period,
              repeatMode,
              shuffleModeEnabled,
              periodPositionUs.first,
              seekTimeline,
              timeline);
      if (periodUid != null) {
        // We found one. Use the default position of the corresponding window.
        return timeline.getPeriodPositionUs(
            window,
            period,
            timeline.getPeriodByUid(periodUid, period).windowIndex,
            /* windowPositionUs= */ C.TIME_UNSET);
      }
    }
    // We didn't find one. Give up.
    return null;
  }

  /**
   * Given a period index into an old timeline, finds the first subsequent period that also exists
   * in a new timeline. The uid of this period in the new timeline is returned.
   *
   * @param window A {@link Timeline.Window} to be used internally.
   * @param period A {@link Timeline.Period} to be used internally.
   * @param repeatMode The repeat mode to use.
   * @param shuffleModeEnabled Whether the shuffle mode is enabled.
   * @param oldPeriodUid The index of the period in the old timeline.
   * @param oldTimeline The old timeline.
   * @param newTimeline The new timeline.
   * @return The uid in the new timeline of the first subsequent period, or null if no such period
   *     was found.
   */
  /* package */ @Nullable
  static Object resolveSubsequentPeriod(
      Timeline.Window window,
      Timeline.Period period,
      @Player.RepeatMode int repeatMode,
      boolean shuffleModeEnabled,
      Object oldPeriodUid,
      Timeline oldTimeline,
      Timeline newTimeline) {
    int oldPeriodIndex = oldTimeline.getIndexOfPeriod(oldPeriodUid);
    int newPeriodIndex = C.INDEX_UNSET;
    int maxIterations = oldTimeline.getPeriodCount();
    for (int i = 0; i < maxIterations && newPeriodIndex == C.INDEX_UNSET; i++) {
      oldPeriodIndex =
          oldTimeline.getNextPeriodIndex(
              oldPeriodIndex, period, window, repeatMode, shuffleModeEnabled);
      if (oldPeriodIndex == C.INDEX_UNSET) {
        // We've reached the end of the old timeline.
        break;
      }
      newPeriodIndex = newTimeline.getIndexOfPeriod(oldTimeline.getUidOfPeriod(oldPeriodIndex));
    }
    return newPeriodIndex == C.INDEX_UNSET ? null : newTimeline.getUidOfPeriod(newPeriodIndex);
  }

  private static Format[] getFormats(ExoTrackSelection newSelection) {
    // Build an array of formats contained by the selection.
    int length = newSelection != null ? newSelection.length() : 0;
    Format[] formats = new Format[length];
    for (int i = 0; i < length; i++) {
      formats[i] = newSelection.getFormat(i);
    }
    return formats;
  }

  private static boolean isRendererEnabled(Renderer renderer) {
    return renderer.getState() != Renderer.STATE_DISABLED;
  }

  private static final class SeekPosition {

    public final Timeline timeline;
    public final int windowIndex;
    public final long windowPositionUs;

    public SeekPosition(Timeline timeline, int windowIndex, long windowPositionUs) {
      this.timeline = timeline;
      this.windowIndex = windowIndex;
      this.windowPositionUs = windowPositionUs;
    }
  }

  private static final class PositionUpdateForPlaylistChange {
    public final MediaPeriodId periodId;
    public final long periodPositionUs;
    public final long requestedContentPositionUs;
    public final boolean forceBufferingState;
    public final boolean endPlayback;
    public final boolean setTargetLiveOffset;

    public PositionUpdateForPlaylistChange(
        MediaPeriodId periodId,
        long periodPositionUs,
        long requestedContentPositionUs,
        boolean forceBufferingState,
        boolean endPlayback,
        boolean setTargetLiveOffset) {
      this.periodId = periodId;
      this.periodPositionUs = periodPositionUs;
      this.requestedContentPositionUs = requestedContentPositionUs;
      this.forceBufferingState = forceBufferingState;
      this.endPlayback = endPlayback;
      this.setTargetLiveOffset = setTargetLiveOffset;
    }
  }

  private static final class PendingMessageInfo implements Comparable<PendingMessageInfo> {

    public final PlayerMessage message;

    public int resolvedPeriodIndex;
    public long resolvedPeriodTimeUs;
    @Nullable public Object resolvedPeriodUid;

    public PendingMessageInfo(PlayerMessage message) {
      this.message = message;
    }

    public void setResolvedPosition(int periodIndex, long periodTimeUs, Object periodUid) {
      resolvedPeriodIndex = periodIndex;
      resolvedPeriodTimeUs = periodTimeUs;
      resolvedPeriodUid = periodUid;
    }

    @Override
    public int compareTo(PendingMessageInfo other) {
      if ((resolvedPeriodUid == null) != (other.resolvedPeriodUid == null)) {
        // PendingMessageInfos with a resolved period position are always smaller.
        return resolvedPeriodUid != null ? -1 : 1;
      }
      if (resolvedPeriodUid == null) {
        // Don't sort message with unresolved positions.
        return 0;
      }
      // Sort resolved media times by period index and then by period position.
      int comparePeriodIndex = resolvedPeriodIndex - other.resolvedPeriodIndex;
      if (comparePeriodIndex != 0) {
        return comparePeriodIndex;
      }
      return Util.compareLong(resolvedPeriodTimeUs, other.resolvedPeriodTimeUs);
    }
  }

  private static final class MediaSourceListUpdateMessage {

    private final List<MediaSourceList.MediaSourceHolder> mediaSourceHolders;
    private final ShuffleOrder shuffleOrder;
    private final int windowIndex;
    private final long positionUs;

    private MediaSourceListUpdateMessage(
        List<MediaSourceList.MediaSourceHolder> mediaSourceHolders,
        ShuffleOrder shuffleOrder,
        int windowIndex,
        long positionUs) {
      this.mediaSourceHolders = mediaSourceHolders;
      this.shuffleOrder = shuffleOrder;
      this.windowIndex = windowIndex;
      this.positionUs = positionUs;
    }
  }

  private static class MoveMediaItemsMessage {

    public final int fromIndex;
    public final int toIndex;
    public final int newFromIndex;
    public final ShuffleOrder shuffleOrder;

    public MoveMediaItemsMessage(
        int fromIndex, int toIndex, int newFromIndex, ShuffleOrder shuffleOrder) {
      this.fromIndex = fromIndex;
      this.toIndex = toIndex;
      this.newFromIndex = newFromIndex;
      this.shuffleOrder = shuffleOrder;
    }
  }
}
