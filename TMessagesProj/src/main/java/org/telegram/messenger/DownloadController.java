);
            }
            if (downloadObject.type == AUTODOWNLOAD_TYPE_PHOTO) {
                photoDownloadQueue.remove(downloadObject);
                if (photoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(AUTODOWNLOAD_TYPE_PHOTO);
                }
            } else if (downloadObject.type == AUTODOWNLOAD_TYPE_AUDIO) {
                audioDownloadQueue.remove(downloadObject);
                if (audioDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(AUTODOWNLOAD_TYPE_AUDIO);
                }
            } else if (downloadObject.type == AUTODOWNLOAD_TYPE_VIDEO) {
                videoDownloadQueue.remove(downloadObject);
                if (videoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(AUTODOWNLOAD_TYPE_VIDEO);
                }
            } else if (downloadObject.type == AUTODOWNLOAD_TYPE_DOCUMENT) {
                documentDownloadQueue.remove(downloadObject);
                if (documentDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(AUTODOWNLOAD_TYPE_DOCUMENT);
                }
            }
        }
    }

    public int generateObserverTag() {
        return lastTag++;
    }

    public void addLoadingFileObserver(String fileName, FileDownloadProgressListener observer) {
        addLoadingFileObserver(fileName, null, observer);
    }

    public void addLoadingFileObserver(String fileName, MessageObject messageObject, FileDownloadProgressListener observer) {
        if (listenerInProgress) {
            addLaterArray.put(fileName, observer);
            return;
        }
        removeLoadingFileObserver(observer);

        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = loadingFileObservers.get(fileName);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            loadingFileObservers.put(fileName, arrayList);
        }
        arrayList.add(new WeakReference<>(observer));
        if (messageObject != null) {
            ArrayList<MessageObject> messageObjects = loadingFileMessagesObservers.get(fileName);
            if (messageObjects == null) {
                messageObjects = new ArrayList<>();
                loadingFileMessagesObservers.put(fileName, messageObjects);
            }
            messageObjects.add(messageObject);
        }

        observersByTag.put(observer.getObserverTag(), fileName);
    }

    public void removeLoadingFileObserver(FileDownloadProgressListener observer) {
        if (listenerInProgress) {
            deleteLaterArray.add(observer);
            return;
        }
        String fileName = observersByTag.get(observer.getObserverTag());
        if (fileName != null) {
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = loadingFileObservers.get(fileName);
            if (arrayList != null) {
                for (int a = 0; a < arrayList.size(); a++) {
                    WeakReference<FileDownloadProgressListener> reference = arrayList.get(a);
                    if (reference.get() == null || reference.get() == observer) {
                        arrayList.remove(a);
                        a--;
                    }
                }
                if (arrayList.isEmpty()) {
                    loadingFileObservers.remove(fileName);
                }
            }
            observersByTag.remove(observer.getObserverTag());
        }
    }

    private void processLaterArrays() {
        for (HashMap.Entry<String, FileDownloadProgressListener> listener : addLaterArray.entrySet()) {
            addLoadingFileObserver(listener.getKey(), listener.getValue());
        }
        addLaterArray.clear();
        for (FileDownloadProgressListener listener : deleteLaterArray) {
            removeLoadingFileObserver(listener);
        }
        deleteLaterArray.clear();
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoadFailed || id == NotificationCenter.httpFileDidFailedLoad) {
            String fileName = (String) args[0];
            Integer canceled = (Integer) args[1];
            listenerInProgress = true;
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = loadingFileObservers.get(fileName);
            if (arrayList != null) {
                for (int a = 0, size = arrayList.size(); a < size; a++) {
                    WeakReference<FileDownloadProgressListener> reference = arrayList.get(a);
                    if (reference.get() != null) {
                        reference.get().onFailedDownload(fileName, canceled == 1);
                        if (canceled != 1) {
                            observersByTag.remove(reference.get().getObserverTag());
                        }
                    }
                }
                if (canceled != 1) {
                    loadingFileObservers.remove(fileName);
                }
            }
            listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, canceled);
        } else if (id == NotificationCenter.fileLoaded || id == NotificationCenter.httpFileDidLoad) {
            listenerInProgress = true;
            String fileName = (String) args[0];
            ArrayList<MessageObject> messageObjects = loadingFileMessagesObservers.get(fileName);
            if (messageObjects != null) {
                for (int a = 0, size = messageObjects.size(); a < size; a++) {
                    MessageObject messageObject = messageObjects.get(a);
                    messageObject.mediaExists = true;
                }
                loadingFileMessagesObservers.remove(fileName);
            }
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = loadingFileObservers.get(fileName);
            if (arrayList != null) {
                for (int a = 0, size = arrayList.size(); a < size; a++) {
                    WeakReference<FileDownloadProgressListener> reference = arrayList.get(a);
                    if (reference.get() != null) {
                        reference.get().onSuccessDownload(fileName);
                        observersByTag.remove(reference.get().getObserverTag());
                    }
                }
                loadingFileObservers.remove(fileName);
            }
            listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, 0);
        } else if (id == NotificationCenter.fileLoadProgressChanged) {
            listenerInProgress = true;
            String fileName = (String) args[0];
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = loadingFileObservers.get(fileName);
            if (arrayList != null) {
                Long loadedSize = (Long) args[1];
                Long totalSize = (Long) args[2];
                for (int a = 0, size = arrayList.size(); a < size; a++) {
                    WeakReference<FileDownloadProgressListener> reference = arrayList.get(a);
                    if (reference.get() != null) {
                        reference.get().onProgressDownload(fileName, loadedSize, totalSize);
                    }
                }
            }
            listenerInProgress = false;
            processLaterArrays();
        } else if (id == NotificationCenter.fileUploadProgressChanged) {
            listenerInProgress = true;
            String fileName = (String) args[0];
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = loadingFileObservers.get(fileName);
            if (arrayList != null) {
                Long loadedSize = (Long) args[1];
                Long totalSize = (Long) args[2];
                Boolean enc = (Boolean) args[3];
                for (int a = 0, size = arrayList.size(); a < size; a++) {
                    WeakReference<FileDownloadProgressListener> reference = arrayList.get(a);
                    if (reference.get() != null) {
                        reference.get().onProgressUpload(fileName, loadedSize, totalSize, enc);
                    }
                }
            }
            listenerInProgress = false;
            processLaterArrays();
            try {
                ArrayList<SendMessagesHelper.DelayedMessage> delayedMessages = getSendMessagesHelper().getDelayedMessages(fileName);
                if (delayedMessages != null) {
                    for (int a = 0; a < delayedMessages.size(); a++) {
                        SendMessagesHelper.DelayedMessage delayedMessage = delayedMessages.get(a);
                        if (delayedMessage.encryptedChat == null) {
                            long dialogId = delayedMessage.peer;
                            int topMessageId = delayedMessage.topMessageId;
                            Long lastTime = typingTimes.get(dialogId);
                            if (delayedMessage.type == 4) {
                                if (lastTime == null || lastTime + 4000 < System.currentTimeMillis()) {
                                    MessageObject messageObject = (MessageObject) delayedMessage.extraHashMap.get(fileName + "_i");
                                    if (messageObject != null && messageObject.isVideo()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 5, 0);
                                    } else if (messageObject != null && messageObject.getDocument() != null) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 3, 0);
                                    } else {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 4, 0);
                                    }
                                    typingTimes.put(dialogId, System.currentTimeMillis());
                                }
                            } else {
                                TLRPC.Document document = delayedMessage.obj.getDocument();
                                if (lastTime == null || lastTime + 4000 < System.currentTimeMillis()) {
                                    if (delayedMessage.obj.isRoundVideo()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 8, 0);
                                    } else if (delayedMessage.obj.isVideo()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 5, 0);
                                    } else if (delayedMessage.obj.isVoice()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 9, 0);
                                    } else if (delayedMessage.obj.getDocument() != null) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 3, 0);
                                    } else if (delayedMessage.photoSize != null) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 4, 0);
                                    }
                                    typingTimes.put(dialogId, System.currentTimeMillis());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static float getProgress(long[] progressSizes) {
        if (progressSizes == null || progressSizes.length < 2 || progressSizes[1] == 0) {
            return 0f;
        }
        return Math.min(1f, progressSizes[0] / (float) progressSizes[1]);
    }


    public void startDownloadFile(TLRPC.Document document, MessageObject parentObject) {
        if (parentObject == null) {
            return;
        }
        TLRPC.Document parentDocument = parentObject.getDocument();
        if (parentDocument == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(() -> {
            if (parentDocument == null) {
                return;
            }
            boolean contains = false;

            for (int i = 0; i < recentDownloadingFiles.size(); i++) {
                MessageObject messageObject = recentDownloadingFiles.get(i);
                if (messageObject == null) {
                    continue;
                }
                TLRPC.Document document1 = messageObject.getDocument();
                if (document1 != null && document1.id == parentDocument.id) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                for (int i = 0; i < downloadingFiles.size(); i++) {
                    MessageObject messageObject = downloadingFiles.get(i);
                    if (messageObject == null) {
                        continue;
                    }
                    TLRPC.Document document1 = messageObject.getDocument();
                    if (document1 != null && document1.id == parentDocument.id) {
                        contains = true;
                        break;
                    }
                }
            }
            if (!contains) {
                downloadingFiles.add(0, parentObject);
                getMessagesStorage().getStorageQueue().postRunnable(() -> {
                    try {
                        NativeByteBuffer data = new NativeByteBuffer(parentObject.messageOwner.getObjectSize());
                        parentObject.messageOwner.serializeToStream(data);

                        SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO downloading_documents VALUES(?, ?, ?, ?, ?)");
                        state.bindByteBuffer(1, data);
                        state.bindInteger(2, parentObject.getDocument().dc_id);
                        state.bindLong(3, parentObject.getDocument().id);
                        state.bindLong(4, System.currentTimeMillis());
                        state.bindInteger(4, 0);

                        state.step();
                        state.dispose();
                        data.reuse();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                });
            }
            getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged);
        });
    }

    public void onDownloadComplete(MessageObject parentObject) {
        if (parentObject == null || parentObject.getDocument() == null) {
            return;
        }
        TLRPC.Document document = parentObject.getDocument();
        AndroidUtilities.runOnUIThread(() -> {
            boolean removed = false;
            for (int i = 0; i < downloadingFiles.size(); i++) {
                if (downloadingFiles.get(i).getDocument() != null && downloadingFiles.get(i).getDocument().id == document.id) {
                    downloadingFiles.remove(i);
                    removed = true;
                    break;
                }
            }

            if (removed) {
                boolean contains = false;
                for (int i = 0; i < recentDownloadingFiles.size(); i++) {
                    if (recentDownloadingFiles.get(i).getDocument() != null && recentDownloadingFiles.get(i).getDocument().id == document.id) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    recentDownloadingFiles.add(0, parentObject);
                    putToUnviewedDownloads(parentObject);
                }
                getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged);
                getMessagesStorage().getStorageQueue().postRunnable(() -> {
                    try {
                        String req = String.format(Locale.ENGLISH, "UPDATE downloading_documents SET state = 1, date = %d WHERE hash = %d AND id = %d", System.currentTimeMillis(), parentObject.getDocument().dc_id,  parentObject.getDocument().id);
                        getMessagesStorage().getDatabase().executeFast(req).stepThis().dispose();
                        SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT COUNT(*) FROM downloading_documents WHERE state = 1");
                        int count = 0;
                        if (cursor.next()) {
                            count = cursor.intValue(0);
                        }
                        cursor.dispose();

                        cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT state FROM downloading_documents WHERE state = 1");
                        if (cursor.next()) {
                            int state = cursor.intValue(0);
                        }
                        cursor.dispose();

                        int limitDownloadsDocuments = 100;
                        if (count > limitDownloadsDocuments) {
                            cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT hash, id FROM downloading_documents WHERE state = 1 ORDER BY date ASC LIMIT " + (limitDownloadsDocuments - count));
                            ArrayList<DownloadingDocumentEntry> entriesToRemove = new ArrayList<>();
                            while (cursor.next()) {
                                DownloadingDocumentEntry entry = new DownloadingDocumentEntry();
                                entry.hash = cursor.intValue(0);
                                entry.id = cursor.longValue(1);
                                entriesToRemove.add(entry);
                            }
                            cursor.dispose();

                            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
                            for (int i = 0; i < entriesToRemove.size(); i++) {
                                state.requery();
                                state.bindInteger(1, entriesToRemove.get(i).hash);
                                state.bindLong(2, entriesToRemove.get(i).id);
                                state.step();
                            }
                            state.dispose();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                });
            }
        });


    }

    public void onDownloadFail(MessageObject parentObject, int reason) {
        if (parentObject == null) {
            return;
        }

        AndroidUtilities.runOnUIThread(() -> {
            boolean removed = false;
            TLRPC.Document parentDocument = parentObject.getDocument();
            for (int i = 0; i < downloadingFiles.size(); i++) {
                TLRPC.Document downloadingDocument = downloadingFiles.get(i).getDocument();
                if (downloadingDocument == null || parentDocument != null && downloadingDocument.id == parentDocument.id) {
                    downloadingFiles.remove(i);
                    removed = true;
                    break;
                }
            }
            if (removed) {
                getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged);
                if (reason == 0) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, Bulletin.TYPE_ERROR, LocaleController.formatString("MessageNotFound", R.string.MessageNotFound));
                } else if (reason == -1) {
                    LaunchActivity.checkFreeDiscSpaceStatic(2);
                }
            }
        });

        getMessagesStorage().getStorageQueue().postRunnable(() -> {
            try {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
                state.bindInteger(1, parentObject.getDocument().dc_id);
                state.bindLong(2, parentObject.getDocument().id);
                state.step();
                state.dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    Runnable clearUnviewedDownloadsRunnale = new Runnable() {
        @Override
        public void run() {
            clearUnviewedDownloads();
            getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged);
        }
    };
    private void putToUnviewedDownloads(MessageObject parentObject) {
        unviewedDownloads.put(parentObject.getId(), parentObject);
        AndroidUtilities.cancelRunOnUIThread(clearUnviewedDownloadsRunnale);
        AndroidUtilities.runOnUIThread(clearUnviewedDownloadsRunnale, 60000);
    }

    public void clearUnviewedDownloads() {
        unviewedDownloads.clear();
    }

    public void checkUnviewedDownloads(int messageId, long dialogId) {
        MessageObject messageObject = unviewedDownloads.get(messageId);
        if (messageObject != null && messageObject.getDialogId() == dialogId) {
            unviewedDownloads.remove(messageId);
            if (unviewedDownloads.size() == 0) {
                getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged);
            }
        }
    }

    public boolean hasUnviewedDownloads() {
        return unviewedDownloads.size() > 0;
    }

    private class DownloadingDocumentEntry {
        long id;
        int hash;
    }

    public void loadDownloadingFiles() {
        getMessagesStorage().getStorageQueue().postRunnable(() -> {
            ArrayList<MessageObject> downloadingMessages = new ArrayList<>();
            ArrayList<MessageObject> recentlyDownloadedMessages = new ArrayList<>();
            ArrayList<MessageObject> newMessages = new ArrayList<>();
            try {
                SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized("SELECT data, state FROM downloading_documents ORDER BY date DESC");
                while (cursor2.next()) {
                    NativeByteBuffer data = cursor2.byteBufferValue(0);
                    int state = cursor2.intValue(1);
                    if (data != null) {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                        if (message != null) {
                            message.readAttachPath(data, UserConfig.getInstance(currentAccount).clientUserId);
                            MessageObject messageObject = new MessageObject(currentAccount, message, false, false);
                            newMessages.add(messageObject);
                            if (state == 0) {
                                downloadingMessages.add(messageObject);
                            } else {
                                recentlyDownloadedMessages.add(messageObject);
                            }
                        }
                        data.reuse();
                    }
                }
                cursor2.dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }

            getFileLoader().checkMediaExistance(downloadingMessages);
            getFileLoader().checkMediaExistance(recentlyDownloadedMessages);

            AndroidUtilities.runOnUIThread(() -> {
                downloadingFiles.clear();
                downloadingFiles.addAll(downloadingMessages);

                recentDownloadingFiles.clear();
                recentDownloadingFiles.addAll(recentlyDownloadedMessages);
            });
        });
    }

    public void swapLoadingPriority(MessageObject o1, MessageObject o2) {
        int index1 = downloadingFiles.indexOf(o1);
        int index2 = downloadingFiles.indexOf(o2);
        if (index1 >= 0 && index2 >= 0) {
            downloadingFiles.set(index1, o2);
            downloadingFiles.set(index2, o1);
        }
        updateFilesLoadingPriority();
    }

    public void updateFilesLoadingPriority() {
        for (int i = downloadingFiles.size() - 1; i >= 0 ; i--) {
            if (getFileLoader().isLoadingFile(downloadingFiles.get(i).getFileName())) {
                getFileLoader().loadFile(downloadingFiles.get(i).getDocument(), downloadingFiles.get(i), FileLoader.PRIORITY_NORMAL_UP, 0);
            }
        }
    }

    public void clearRecentDownloadedFiles() {
        recentDownloadingFiles.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged);

        getMessagesStorage().getStorageQueue().postRunnable(() -> {
            try {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE state = 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    public void deleteRecentFiles(ArrayList<MessageObject> messageObjects) {
        for (int i = 0; i < messageObjects.size(); i++) {
            boolean found = false;
            for (int j = 0; j < recentDownloadingFiles.size(); j++) {
                if (messageObjects.get(i).getId() == recentDownloadingFiles.get(j).getId() && recentDownloadingFiles.get(j).getDialogId() == messageObjects.get(i).getDialogId()) {
                    recentDownloadingFiles.remove(j);
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (int j = 0; j < downloadingFiles.size(); j++) {
                    if (messageObjects.get(i).getId() == downloadingFiles.get(j).getId() && downloadingFiles.get(j).getDialogId() == messageObjects.get(i).getDialogId()) {
                        downloadingFiles.remove(j);
                        found = true;
                        break;
                    }
                }
            }
            messageObjects.get(i).putInDownloadsStore = false;
            FileLoader.getInstance(currentAccount).loadFile(messageObjects.get(i).getDocument(), messageObjects.get(i), FileLoader.PRIORITY_LOW, 0);
            FileLoader.getInstance(currentAccount).cancelLoadFile(messageObjects.get(i).getDocument(), true);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged);
        getMessagesStorage().getStorageQueue().postRunnable(() -> {
            try {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
                for (int i = 0; i < messageObjects.size(); i++) {
                    state.requery();
                    state.bindInteger(1, messageObjects.get(i).getDocument().dc_id);
                    state.bindLong(2, messageObjects.get(i).getDocument().id);
                    state.step();

                    try {
                        File file = FileLoader.getInstance(currentAccount).getPathToMessage(messageObjects.get(i).messageOwner);
                        file.delete();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                state.dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    public boolean isDownloading(int messageId) {
        for (int i = 0; i < downloadingFiles.size(); i++) {
            if (downloadingFiles.get(i).messageOwner.id == messageId) {
                return true;
            }
        }
        return false;
    }

    public boolean canPreloadStories() {
        Preset preset;
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == StatsController.TYPE_WIFI) {
            if (!wifiPreset.enabled) {
                return false;
            }
            preset = getCurrentWiFiPreset();

        } else if (networkType == StatsController.TYPE_ROAMING) {
            if (!roamingPreset.enabled) {
                return false;
            }
            preset = getCurrentRoamingPreset();
        } else {
            if (!mobilePreset.enabled) {
                return false;
            }
            preset = getCurrentMobilePreset();
        }
        return preset.preloadStories;
    }
}
