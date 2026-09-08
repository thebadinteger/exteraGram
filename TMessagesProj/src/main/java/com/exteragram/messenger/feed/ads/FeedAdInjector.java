package com.exteragram.messenger.feed.ads;

import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import com.exteragram.messenger.feed.FeedChatIntegration;
import com.exteragram.messenger.feed.FeedMessageUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import org.telegram.messenger.MessageObject;

public final class FeedAdInjector {
    private final int currentAccount;
    private final FeedChatIntegration.Host host;
    private final HashMap<MessageObject, MessageObject> adByAnchor = new HashMap<>();
    private final HashMap<MessageObject, Integer> slotOrdinalByAnchor = new HashMap<>();

    public FeedAdInjector(int i, FeedChatIntegration.Host host) {
        this.currentAccount = i;
        this.host = host;
    }

    public void clear() {
        this.adByAnchor.clear();
        this.slotOrdinalByAnchor.clear();
    }

    public void refresh(MessageObject messageObject) {
        int iIndexOf;
        int iIndexOf2;
        if (this.host.isListReady()) {
            ArrayList<MessageObject> messages = this.host.getMessages();
            FeedAdController feedAdController = FeedAdController.getInstance(this.currentAccount);
            boolean zIsEnabled = feedAdController.isEnabled();
            ArrayList arrayList = new ArrayList(this.adByAnchor.keySet());
            int size = arrayList.size();
            int i = 0;
            boolean zRemoveAd = false;
            int i2 = 0;
            while (i2 < size) {
                Object obj = arrayList.get(i2);
                i2++;
                MessageObject messageObject2 = (MessageObject) obj;
                if (!zIsEnabled || !messages.contains(messageObject2)) {
                    zRemoveAd |= removeAd(messages, this.adByAnchor.remove(messageObject2));
                    this.slotOrdinalByAnchor.remove(messageObject2);
                }
            }
            if (zIsEnabled) {
                boolean zRevalidateKeptAds = revalidateKeptAds(messages, feedAdController) | zRemoveAd;
                for (Map.Entry<MessageObject, MessageObject> entry : this.adByAnchor.entrySet()) {
                    MessageObject key = entry.getKey();
                    MessageObject value = entry.getValue();
                    int iIndexOf3 = messages.indexOf(key);
                    if (iIndexOf3 >= 0 && (iIndexOf = messages.indexOf(value)) != (iIndexOf2 = iIndexOf3 + 1)) {
                        if (iIndexOf >= 0) {
                            messages.remove(iIndexOf);
                            this.host.notifyMessageRemoved(iIndexOf);
                            iIndexOf2 = messages.indexOf(key) + 1;
                        }
                        messages.add(iIndexOf2, value);
                        this.host.notifyMessageInserted(iIndexOf2);
                        zRevalidateKeptAds = true;
                    }
                }
                ArrayList<AnchorSlot> arrayListComputeNewAnchors = computeNewAnchors(messages, messageObject, feedAdController);
                int size2 = arrayListComputeNewAnchors.size();
                while (i < size2) {
                    AnchorSlot anchorSlot = arrayListComputeNewAnchors.get(i);
                    i++;
                    AnchorSlot anchorSlot2 = anchorSlot;
                    MessageObject messageObject3 = anchorSlot2.anchor;
                    FeedAd feedAdNextAd = feedAdController.nextAd();
                    if (feedAdNextAd == null) {
                        break;
                    }
                    int iIndexOf4 = messages.indexOf(messageObject3);
                    if (iIndexOf4 >= 0) {
                        MessageObject messageObjectCreateAdMessageObject = FeedAdFactory.createAdMessageObject(this.currentAccount, feedAdNextAd);
                        messageObjectCreateAdMessageObject.stableId = this.host.nextStableId();
                        this.adByAnchor.put(messageObject3, messageObjectCreateAdMessageObject);
                        this.slotOrdinalByAnchor.put(messageObject3, Integer.valueOf(anchorSlot2.ordinal));
                        int i3 = iIndexOf4 + 1;
                        messages.add(i3, messageObjectCreateAdMessageObject);
                        this.host.notifyMessageInserted(i3);
                        zRevalidateKeptAds = true;
                    }
                }
                zRemoveAd = zRevalidateKeptAds;
            }
            if (zRemoveAd) {
                this.host.onFeedListChanged();
                this.host.invalidateVisiblePart();
            }
        }
    }

    private boolean revalidateKeptAds(ArrayList<MessageObject> arrayList, FeedAdController feedAdController) {
        int iLastGroupMemberIndex;
        if (this.adByAnchor.isEmpty()) {
            return false;
        }
        ArrayList arrayList2 = new ArrayList(this.adByAnchor.keySet());
        int size = arrayList2.size();
        boolean zRemoveAd = false;
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            MessageObject messageObject = (MessageObject) obj;
            int iIndexOf = arrayList.indexOf(messageObject);
            if (iIndexOf >= 0 && (iLastGroupMemberIndex = lastGroupMemberIndex(arrayList, iIndexOf)) != iIndexOf) {
                MessageObject messageObjectRemove = this.adByAnchor.remove(messageObject);
                Integer numRemove = this.slotOrdinalByAnchor.remove(messageObject);
                MessageObject messageObject2 = arrayList.get(iLastGroupMemberIndex);
                if (this.adByAnchor.containsKey(messageObject2)) {
                    zRemoveAd |= removeAd(arrayList, messageObjectRemove);
                } else {
                    this.adByAnchor.put(messageObject2, messageObjectRemove);
                    if (numRemove != null) {
                        this.slotOrdinalByAnchor.put(messageObject2, numRemove);
                    }
                }
            }
        }
        HashMap map = new HashMap();
        int i2 = 0;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            if (FeedMessageUtils.isPostRow(arrayList.get(i3))) {
                map.put(arrayList.get(i3), Integer.valueOf(i2));
                i2++;
            }
        }
        int baseEvery = feedAdController.getBaseEvery();
        int minTrailing = feedAdController.getMinTrailing();
        TreeMap treeMap = new TreeMap();
        for (MessageObject messageObject3 : this.adByAnchor.keySet()) {
            Integer num = (Integer) map.get(messageObject3);
            if (num != null) {
                treeMap.put(num, messageObject3);
            }
        }
        int i4 = Integer.MIN_VALUE;
        for (Map.Entry entry : treeMap.entrySet()) {
            int iIntValue = ((Integer) entry.getKey()).intValue();
            if (iIntValue < minTrailing || (i4 != Integer.MIN_VALUE && iIntValue - i4 < baseEvery)) {
                MessageObject messageObject4 = (MessageObject) entry.getValue();
                zRemoveAd |= removeAd(arrayList, this.adByAnchor.remove(messageObject4));
                this.slotOrdinalByAnchor.remove(messageObject4);
            } else {
                i4 = iIntValue;
            }
        }
        return zRemoveAd;
    }

    private boolean removeAd(ArrayList<MessageObject> arrayList, MessageObject messageObject) {
        int iIndexOf;
        if (messageObject == null || (iIndexOf = arrayList.indexOf(messageObject)) < 0) {
            return false;
        }
        arrayList.remove(iIndexOf);
        this.host.notifyMessageRemoved(iIndexOf);
        return true;
    }

    private ArrayList<AnchorSlot> computeNewAnchors(ArrayList<MessageObject> arrayList, MessageObject messageObject, FeedAdController feedAdController) {
        int i;
        Object obj;
        ArrayList arrayList2 = new ArrayList();
        HashMap map = new HashMap();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            if (FeedMessageUtils.isPostRow(arrayList.get(i2))) {
                map.put(arrayList.get(i2), Integer.valueOf(arrayList2.size()));
                arrayList2.add(Integer.valueOf(i2));
            }
        }
        ArrayList<AnchorSlot> arrayList3 = new ArrayList<>();
        int size = arrayList2.size();
        if (size != 0) {
            int firstAfter = feedAdController.getFirstAfter();
            int effectiveEvery = feedAdController.getEffectiveEvery();
            int minTrailing = feedAdController.getMinTrailing();
            int iIndexOf = messageObject != null ? arrayList.indexOf(messageObject) : -1;
            if (iIndexOf >= 0) {
                int size2 = arrayList2.size();
                i = 0;
                int i3 = 0;
                while (i3 < size2) {
                    Object obj2 = arrayList2.get(i3);
                    i3++;
                    if (((Integer) obj2).intValue() >= iIndexOf) {
                        break;
                    }
                    i++;
                }
            } else {
                i = 0;
            }
            TreeSet<Integer> treeSet = new TreeSet();
            for (int i4 = i - firstAfter; i4 >= 0; i4 -= effectiveEvery) {
                treeSet.add(Integer.valueOf(i4));
            }
            for (int i5 = i + firstAfter; i5 <= size - 1; i5 += effectiveEvery) {
                treeSet.add(Integer.valueOf(i5));
            }
            ArrayList arrayList4 = new ArrayList();
            for (MessageObject messageObject2 : this.adByAnchor.keySet()) {
                if (map.containsKey(messageObject2)) {
                    Integer num = this.slotOrdinalByAnchor.get(messageObject2);
                    if (num == null) {
                        num = (Integer) map.get(messageObject2);
                    }
                    arrayList4.add(num);
                }
            }
            for (Integer num2 : treeSet) {
                int iIntValue = num2.intValue();
                if (iIntValue >= minTrailing) {
                    MessageObject messageObject3 = arrayList.get(lastGroupMemberIndex(arrayList, ((Integer) arrayList2.get(iIntValue)).intValue()));
                    if (((Integer) map.get(messageObject3)) != null && !this.adByAnchor.containsKey(messageObject3)) {
                        int size3 = arrayList4.size();
                        int i6 = 0;
                        do {
                            if (i6 >= size3) {
                                arrayList3.add(new AnchorSlot(messageObject3, iIntValue));
                                arrayList4.add(num2);
                                break;
                            }
                            obj = arrayList4.get(i6);
                            i6++;
                        } while (Math.abs(iIntValue - ((Integer) obj).intValue()) >= effectiveEvery);
                    }
                }
            }
        }
        return arrayList3;
    }

    private static int lastGroupMemberIndex(ArrayList<MessageObject> arrayList, int i) {
        int i2;
        MessageObject messageObject;
        long groupId = arrayList.get(i).getGroupId();
        if (groupId == 0) {
            return i;
        }
        do {
            i2 = i;
            i++;
            if (i >= arrayList.size()) {
                break;
            }
            messageObject = arrayList.get(i);
            if (!FeedMessageUtils.isPostRow(messageObject)) {
                break;
            }
        } while (messageObject.getGroupId() == groupId);
        return i2;
    }

    public static final class AnchorSlot extends RecordTag {
        private final MessageObject anchor;
        private final int ordinal;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof AnchorSlot)) {
                return false;
            }
            AnchorSlot anchorSlot = (AnchorSlot) obj;
            return this.ordinal == anchorSlot.ordinal && Objects.equals(this.anchor, anchorSlot.anchor);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.anchor, Integer.valueOf(this.ordinal)};
        }

        private AnchorSlot(MessageObject messageObject, int i) {
            this.anchor = messageObject;
            this.ordinal = i;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return FeedAdInjector$AnchorSlot$$ExternalSyntheticRecord0.m(this.ordinal, this.anchor);
        }

        public final String toString() {
            return Client$ImagePayload$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), AnchorSlot.class, "anchor;ordinal");
        }
    }
}
