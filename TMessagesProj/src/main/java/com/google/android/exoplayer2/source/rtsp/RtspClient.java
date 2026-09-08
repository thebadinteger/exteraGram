public void start() throws IOException {
    try {
      messageChannel.open(getSocket(uri));
    } catch (IOException e) {
      Util.closeQuietly(messageChannel);
      throw e;
    }
    messageSender.sendOptionsRequest(uri, sessionId);
  }

  public MessageListener() {
      messageHandler = Util.createHandlerForCurrentLooper();
    }

    @Override
    public void onRtspMessageReceived(List<String> message) {
      messageHandler.post(() -> handleRtspMessage(message));
    }

    private void handleRtspMessage(List<String> message) {
      maybeLogMessage(message);

      if (RtspMessageUtil.isRtspResponse(message)) {
        handleRtspResponse(message);
      } else {
        handleRtspRequest(message);
      }
    }

    private void handleRtspRequest(List<String> message) {
      // Handling RTSP requests on the client is optional (RFC2326 Section 10). Decline all
      // requests with 'Method Not Allowed'.
      messageSender.sendMethodNotAllowedResponse(
          Integer.parseInt(
              checkNotNull(RtspMessageUtil.parseRequest(message).headers.get(RtspHeaders.CSEQ))));
    }

    private void handleRtspResponse(List<String> message) {
      RtspResponse response = RtspMessageUtil.parseResponse(message);

      int cSeq = Integer.parseInt(checkNotNull(response.headers.get(RtspHeaders.CSEQ)));

      @Nullable RtspRequest matchingRequest = pendingRequests.get(cSeq);
      if (matchingRequest == null) {
        return;
      } else {
        pendingRequests.remove(cSeq);
      }

      @RtspRequest.Method int requestMethod = matchingRequest.method;

      try {
        switch (response.status) {
          case 200:
            break;
          case 301:
          case 302:
            // Redirection request.
            if (rtspState != RTSP_STATE_UNINITIALIZED) {
              rtspState = RTSP_STATE_INIT;
            }
            @Nullable String redirectionUriString = response.headers.get(RtspHeaders.LOCATION);
            if (redirectionUriString == null) {
              sessionInfoListener.onSessionTimelineRequestFailed(
                  "Redirection without new location.", null);
            }

            RtspSessionHeader sessionHeader =
                RtspMessageUtil.parseSessionHeader(sessionHeaderString);
            onSetupResponseReceived(
                new RtspSetupResponse(response.status, sessionHeader, transportHeaderString));
            break;

          case METHOD_PLAY:
            // Range header is optional for a PLAY response (RFC2326 Section 12).
            @Nullable String startTimingString = response.headers.get(RtspHeaders.RANGE);
            RtspSessionTiming timing =
                startTimingString == null
                    ? RtspSessionTiming.DEFAULT
                    : RtspSessionTiming.parseTiming(startTimingString);

            ImmutableList<RtspTrackTiming> trackTimingList;
            try {
              @Nullable String rtpInfoString = response.headers.get(RtspHeaders.RTP_INFO);
              trackTimingList =
                  rtpInfoString == null
                      ? ImmutableList.of()
                      : RtspTrackTiming.parseTrackTiming(rtpInfoString, uri);
            } catch (ParserException e) {
              trackTimingList = ImmutableList.of();
            }

            onPlayResponseReceived(new RtspPlayResponse(response.status, timing, trackTimingList));
            break;

          case METHOD_PAUSE:
            onPauseResponseReceived();
            break;

          case METHOD_GET_PARAMETER:
          case METHOD_TEARDOWN:
          case METHOD_PLAY_NOTIFY:
          case METHOD_RECORD:
          case METHOD_REDIRECT:
          case METHOD_ANNOUNCE:
          case METHOD_SET_PARAMETER:
            break;
          case METHOD_UNSET:
          default:
            throw new IllegalStateException();
        }
      } catch (ParserException e) {
        dispatchRtspError(new RtspPlaybackException(e));
      }
    }

    // Response handlers must only be called only on 200 (OK) responses.

    private void onOptionsResponseReceived(RtspOptionsResponse response) {
      if (keepAliveMonitor != null) {
        // Ignores the OPTIONS requests that are sent to keep RTSP connection alive.
        return;
      }

      if (serverSupportsDescribe(response.supportedMethods)) {
        messageSender.sendDescribeRequest(uri, sessionId);
      } else {
        sessionInfoListener.onSessionTimelineRequestFailed(
            "DESCRIBE not supported.", /* cause= */ null);
      }
    }

    private void onDescribeResponseReceived(RtspDescribeResponse response) {
      RtspSessionTiming sessionTiming = RtspSessionTiming.DEFAULT;
      @Nullable
      String sessionRangeAttributeString =
          response.sessionDescription.attributes.get(SessionDescription.ATTR_RANGE);
      if (sessionRangeAttributeString != null) {
        try {
          sessionTiming = RtspSessionTiming.parseTiming(sessionRangeAttributeString);
        } catch (ParserException e) {
          sessionInfoListener.onSessionTimelineRequestFailed("SDP format error.", /* cause= */ e);
          return;
        }
      }

      ImmutableList<RtspMediaTrack> tracks = buildTrackList(response.sessionDescription, uri);
      if (tracks.isEmpty()) {
        sessionInfoListener.onSessionTimelineRequestFailed("No playable track.", /* cause= */ null);
        return;
      }

      sessionInfoListener.onSessionTimelineUpdated(sessionTiming, tracks);
      hasUpdatedTimelineAndTracks = true;
    }

    private void onSetupResponseReceived(RtspSetupResponse response) {
      checkState(rtspState != RTSP_STATE_UNINITIALIZED);

      rtspState = RTSP_STATE_READY;
      sessionId = response.sessionHeader.sessionId;
      continueSetupRtspTrack();
    }

    private void onPlayResponseReceived(RtspPlayResponse response) {
      checkState(rtspState == RTSP_STATE_READY);

      rtspState = RTSP_STATE_PLAYING;
      if (keepAliveMonitor == null) {
        keepAliveMonitor = new KeepAliveMonitor(DEFAULT_RTSP_KEEP_ALIVE_INTERVAL_MS);
        keepAliveMonitor.start();
      }

      pendingSeekPositionUs = C.TIME_UNSET;
      // onPlaybackStarted could initiate another seek request, which will set
      // pendingSeekPositionUs.
      playbackEventListener.onPlaybackStarted(
          Util.msToUs(response.sessionTiming.startTimeMs), response.trackTimingList);
    }

    private void onPauseResponseReceived() {
      checkState(rtspState == RTSP_STATE_PLAYING);

      rtspState = RTSP_STATE_READY;
      hasPendingPauseRequest = false;
      if (pendingSeekPositionUs != C.TIME_UNSET) {
        startPlayback(Util.usToMs(pendingSeekPositionUs));
      }
    }
  }

  /** Sends periodic OPTIONS requests to keep RTSP connection alive. */
  private final class KeepAliveMonitor implements Runnable, Closeable {

    private final Handler keepAliveHandler;
    private final long intervalMs;
    private boolean isStarted;

    /**
     * Creates a new instance.
     *
     * <p>Constructor must be invoked on the playback thread.
     *
     * @param intervalMs The time between consecutive RTSP keep-alive requests, in milliseconds.
     */
    public KeepAliveMonitor(long intervalMs) {
      this.intervalMs = intervalMs;
      keepAliveHandler = Util.createHandlerForCurrentLooper();
    }

    /** Starts Keep-alive. */
    public void start() {
      if (isStarted) {
        return;
      }

      isStarted = true;
      keepAliveHandler.postDelayed(this, intervalMs);
    }

    @Override
    public void run() {
      messageSender.sendOptionsRequest(uri, sessionId);
      keepAliveHandler.postDelayed(this, intervalMs);
    }

    @Override
    public void close() {
      isStarted = false;
      keepAliveHandler.removeCallbacks(this);
    }
  }
}
