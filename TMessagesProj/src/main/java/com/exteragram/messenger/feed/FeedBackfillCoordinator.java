package com.exteragram.messenger.feed;

import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

final class FeedBackfillCoordinator {
    private final int currentAccount;
    private int loadIndex;
    private final Runnable onRoundFinished;
    private int roundId;
    private boolean running;
    private final int guid = ConnectionsManager.generateClassGuid();
    private final HashSet<Long> pending = new HashSet<>();
    private final HashSet<Long> exhausted = new HashSet<>();

    public FeedBackfillCoordinator(int i, Runnable runnable) {
        this.currentAccount = i;
        this.onRoundFinished = runnable;
    }

    public HashSet<Long> getExhaustedSnapshot() {
        return new HashSet<>(this.exhausted);
    }

    public void clearExhausted() {
        this.exhausted.clear();
    }

    public void cancel() {
        this.running = false;
        this.roundId++;
        this.pending.clear();
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.guid);
    }

    public void startRound(ArrayList<long[]> arrayList) {
        this.running = true;
        final int i = this.roundId + 1;
        this.roundId = i;
        this.pending.clear();
        int iMin = Math.min(4, arrayList.size());
        for (int i2 = 0; i2 < iMin; i2++) {
            this.pending.add(Long.valueOf(arrayList.get(i2)[0]));
        }
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        for (int i3 = 0; i3 < iMin; i3++) {
            long j = arrayList.get(i3)[0];
            int i4 = (int) arrayList.get(i3)[1];
            int i5 = this.guid;
            int i6 = this.loadIndex;
            this.loadIndex = i6 + 1;
            messagesController.loadMessages(j, 0L, false, 20, i4, 0, false, 0, i5, 0, 0, 0, 0L, 0, i6, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                FeedBackfillCoordinator.this.lambda$startRound$0(i);
            }
        }, 10000L);
    }

    public /* synthetic */ void lambda$startRound$0(int i) {
        if (i == this.roundId && this.running) {
            this.exhausted.addAll(this.pending);
            finishRound();
        }
    }

    public void onMessagesDidLoad(Object... objArr) {
        if (((Integer) objArr[10]).intValue() != this.guid) {
            return;
        }
        Long l = (Long) objArr[0];
        long jLongValue = l.longValue();
        if (((ArrayList) objArr[2]).size() < 20) {
            this.exhausted.add(l);
        }
        onResult(jLongValue);
    }

    public void onLoadingMessagesFailed(Object... objArr) {
        long j;
        TLRPC.InputPeer inputPeer;
        if (((Integer) objArr[0]).intValue() != this.guid) {
            return;
        }
        Object obj = objArr[1];
        if (!(obj instanceof TLRPC.TL_messages_getHistory) || (inputPeer = ((TLRPC.TL_messages_getHistory) obj).peer) == null) {
            j = 0;
        } else {
            long j2 = inputPeer.channel_id;
            if (j2 == 0) {
                j2 = inputPeer.chat_id;
            }
            j = -j2;
        }
        if (j != 0) {
            this.exhausted.add(Long.valueOf(j));
        }
        onResult(j);
    }

    private void onResult(long j) {
        if (this.running && this.pending.remove(Long.valueOf(j)) && this.pending.isEmpty()) {
            finishRound();
        }
    }

    private void finishRound() {
        this.running = false;
        this.roundId++;
        this.pending.clear();
        this.onRoundFinished.run();
    }
}
