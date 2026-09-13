package com.exteragram.messenger.speech.utils;

import android.os.Build;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC;

public class MediaLoader implements NotificationCenter.NotificationCenterDelegate {
    private int copiedFiles;
    private final AccountInstance currentAccount;
    private final HashMap<String, MessageObject> loadingMessageObjects = new HashMap<>();
    private final ArrayList<MessageObject> messageObjects;
    private final MessagesStorage.IntCallback onFinishRunnable;
    private CountDownLatch waitingForFile;

    private MediaLoader(AccountInstance accountInstance, ArrayList<MessageObject> arrayList, MessagesStorage.IntCallback intCallback) {
        this.currentAccount = accountInstance;
        this.messageObjects = arrayList;
        this.onFinishRunnable = intCallback;
        accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
        accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
    }

    public static void loadFiles(AccountInstance accountInstance, ArrayList<MessageObject> arrayList, MessagesStorage.IntCallback intCallback) {
        new MediaLoader(accountInstance, arrayList, intCallback).start();
    }

    public void start() {
        new Thread(new Runnable() { // from class: com.exteragram.messenger.speech.utils.MediaLoader$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MediaLoader.this.lambda$start$0();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:26:0x0068  */
    public /* synthetic */ void lambda$start$0() {
        try {
            int i = Build.VERSION.SDK_INT;
            ArrayList<MessageObject> arrayList = this.messageObjects;
            int i2 = 0;
            if (i >= 29) {
                int size = arrayList.size();
                while (i2 < size) {
                    MessageObject messageObject = this.messageObjects.get(i2);
                    String string = messageObject.messageOwner.attachPath;
                    if (string != null && !string.isEmpty() && !new File(string).exists()) {
                        string = null;
                    }
                    if (string == null || string.isEmpty()) {
                        TLRPC.Document document = messageObject.getDocument();
                        if (TextUtils.isEmpty(FileLoader.getDocumentFileName(document)) || (messageObject.messageOwner instanceof TLRPC.TL_message_secret) || !FileLoader.canSaveAsFile(messageObject)) {
                            string = null;
                        } else {
                            String documentFileName = FileLoader.getDocumentFileName(document);
                            File directory = FileLoader.getDirectory(5);
                            if (directory != null) {
                                string = new File(directory, documentFileName).getAbsolutePath();
                            } else {
                                string = null;
                            }
                        }
                        if (string == null) {
                            string = FileLoader.getInstance(this.currentAccount.getCurrentAccount()).getPathToMessage(messageObject.messageOwner).toString();
                        }
                    }
                    File file = new File(string);
                    if (!file.exists()) {
                        this.waitingForFile = new CountDownLatch(1);
                        addMessageToLoad(messageObject);
                        this.waitingForFile.await();
                    }
                    if (file.exists()) {
                        this.copiedFiles++;
                    }
                    i2++;
                }
            } else {
                int size2 = arrayList.size();
                while (i2 < size2) {
                    MessageObject messageObject2 = this.messageObjects.get(i2);
                    String string2 = messageObject2.messageOwner.attachPath;
                    if (string2 != null && !string2.isEmpty() && !new File(string2).exists()) {
                        string2 = null;
                    }
                    if (string2 == null || string2.isEmpty()) {
                        string2 = FileLoader.getInstance(this.currentAccount.getCurrentAccount()).getPathToMessage(messageObject2.messageOwner).toString();
                    }
                    File file2 = new File(string2);
                    if (!file2.exists()) {
                        this.waitingForFile = new CountDownLatch(1);
                        addMessageToLoad(messageObject2);
                        this.waitingForFile.await();
                    }
                    if (file2.exists()) {
                        this.copiedFiles++;
                    }
                    i2++;
                }
            }
            checkIfFinished();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void checkIfFinished() {
        if (this.loadingMessageObjects.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.speech.utils.MediaLoader$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MediaLoader.this.lambda$checkIfFinished$2();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkIfFinished$2() {
        try {
            if (this.onFinishRunnable != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.speech.utils.MediaLoader$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaLoader.this.lambda$checkIfFinished$1();
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoaded);
        this.currentAccount.getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadFailed);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkIfFinished$1() {
        this.onFinishRunnable.run(this.copiedFiles);
    }

    private void addMessageToLoad(final MessageObject messageObject) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.speech.utils.MediaLoader$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MediaLoader.this.lambda$addMessageToLoad$3(messageObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addMessageToLoad$3(MessageObject messageObject) {
        TLRPC.Document document = messageObject.getDocument();
        if (document == null) {
            return;
        }
        this.loadingMessageObjects.put(FileLoader.getAttachFileName(document), messageObject);
        this.currentAccount.getFileLoader().loadFile(document, messageObject, 0, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileLoaded || i == NotificationCenter.fileLoadFailed) {
            if (this.loadingMessageObjects.remove((String) objArr[0]) != null) {
                this.waitingForFile.countDown();
            }
        }
    }
}
