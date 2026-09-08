void sendToTarget();

    HandlerWrapper getTarget();
  }

  boolean sendMessageAtFrontOfQueue(Message message);

  /** See {@link Handler#sendEmptyMessage(int)}. */
  boolean sendEmptyMessage(int what);

  /** See {@link Handler#sendEmptyMessageDelayed(int, long)}. */
  boolean sendEmptyMessageDelayed(int what, int delayMs);

  /** See {@link Handler#sendEmptyMessageAtTime(int, long)}. */
  boolean sendEmptyMessageAtTime(int what, long uptimeMs);

  /** See {@link Handler#removeMessages(int)}. */
  void removeMessages(int what);

  /** See {@link Handler#removeCallbacksAndMessages(Object)}. */
  void removeCallbacksAndMessages(@Nullable Object token);

  /** See {@link Handler#post(Runnable)}. */
  boolean post(Runnable runnable);

  /** See {@link Handler#postDelayed(Runnable, long)}. */
  boolean postDelayed(Runnable runnable, long delayMs);

  /** See {@link android.os.Handler#postAtFrontOfQueue(Runnable)}. */
  boolean postAtFrontOfQueue(Runnable runnable);
}
