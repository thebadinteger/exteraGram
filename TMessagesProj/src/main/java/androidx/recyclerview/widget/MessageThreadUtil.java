static class SyncQueueItem {

        private static SyncQueueItem sPool;
        private static final Object sPoolLock = new Object();
        SyncQueueItem next;
        public int what;
        public int arg1;
        public int arg2;
        public int arg3;
        public int arg4;
        public int arg5;
        public Object data;

        void recycle() {
            next = null;
            what = arg1 = arg2 = arg3 = arg4 = arg5 = 0;
            data = null;
            synchronized (sPoolLock) {
                if (sPool != null) {
                    next = sPool;
                }
                sPool = this;
            }
        }

        static SyncQueueItem obtainMessage(int what, int arg1, int arg2, int arg3, int arg4,
                                           int arg5, Object data) {
            synchronized (sPoolLock) {
                final SyncQueueItem item;
                if (sPool == null) {
                    item = new SyncQueueItem();
                } else {
                    item = sPool;
                    sPool = sPool.next;
                    item.next = null;
                }
                item.what = what;
                item.arg1 = arg1;
                item.arg2 = arg2;
                item.arg3 = arg3;
                item.arg4 = arg4;
                item.arg5 = arg5;
                item.data = data;
                return item;
            }
        }

        static SyncQueueItem obtainMessage(int what, int arg1, int arg2) {
            return obtainMessage(what, arg1, arg2, 0, 0, 0, null);
        }

        static SyncQueueItem obtainMessage(int what, int arg1, Object data) {
            return obtainMessage(what, arg1, 0, 0, 0, 0, data);
        }
    }

    static class MessageQueue {

        private SyncQueueItem mRoot;

        synchronized SyncQueueItem next() {
            if (mRoot == null) {
                return null;
            }
            final SyncQueueItem next = mRoot;
            mRoot = mRoot.next;
            return next;
        }

        synchronized void sendMessageAtFrontOfQueue(SyncQueueItem item) {
            item.next = mRoot;
            mRoot = item;
        }

        synchronized void sendMessage(SyncQueueItem item) {
            if (mRoot == null) {
                mRoot = item;
                return;
            }
            SyncQueueItem last = mRoot;
            while (last.next != null) {
                last = last.next;
            }
            last.next = item;
        }

        synchronized void removeMessages(int what) {
            while (mRoot != null && mRoot.what == what) {
                SyncQueueItem item = mRoot;
                mRoot = mRoot.next;
                item.recycle();
            }
            if (mRoot != null) {
                SyncQueueItem prev = mRoot;
                SyncQueueItem item = prev.next;
                while (item != null) {
                    SyncQueueItem next = item.next;
                    if (item.what == what) {
                        prev.next = next;
                        item.recycle();
                    } else {
                        prev = item;
                    }
                    item = next;
                }
            }
        }
    }
}
