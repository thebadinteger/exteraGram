package com.exteragram.messenger.feed;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.jvm.internal.LongCompanionObject;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

final class FeedMessageIdentityMap {
    private final HashMap<MessageCompositeID, Integer> generatedIds = new HashMap<>();
    private final ConcurrentHashMap<Integer, MessageCompositeID> realIdsByGeneratedId = new ConcurrentHashMap<>();
    private final HashMap<MessageCompositeID, MessageObject> messagesByRealId = new HashMap<>();
    private final HashMap<GroupKey, MessageObject> primaryByGroup = new HashMap<>();
    private int lastGeneratedId = 2147483637;

    public boolean register(MessageObject messageObject) {
        boolean z;
        messageObject.reactionsLastCheckTime = LongCompanionObject.MAX_VALUE;
        MessageCompositeID messageCompositeID = new MessageCompositeID(messageObject.messageOwner);
        int i = messageObject.messageOwner.id;
        Integer numValueOf = this.generatedIds.get(messageCompositeID);
        if (numValueOf == null) {
            int i2 = this.lastGeneratedId;
            this.lastGeneratedId = i2 - 1;
            numValueOf = Integer.valueOf(i2);
            this.generatedIds.put(messageCompositeID, numValueOf);
        }
        this.realIdsByGeneratedId.put(numValueOf, messageCompositeID);
        if (this.messagesByRealId.containsKey(messageCompositeID)) {
            z = false;
        } else {
            updatePrimaryGroupFlag(messageObject, messageCompositeID.dialog_id, i);
            this.messagesByRealId.put(messageCompositeID, messageObject);
            z = true;
        }
        TLRPC.Message message = messageObject.messageOwner;
        message.realId = i;
        message.id = numValueOf.intValue();
        return z;
    }

    public void replace(MessageObject messageObject) {
        messageObject.reactionsLastCheckTime = LongCompanionObject.MAX_VALUE;
        MessageCompositeID messageCompositeID = new MessageCompositeID(messageObject.getDialogId(), messageObject.getRealId());
        this.generatedIds.put(messageCompositeID, Integer.valueOf(messageObject.getId()));
        this.realIdsByGeneratedId.put(Integer.valueOf(messageObject.getId()), messageCompositeID);
        MessageObject messageObjectPut = this.messagesByRealId.put(messageCompositeID, messageObject);
        if (messageObject.hasValidGroupId()) {
            GroupKey groupKey = new GroupKey(messageCompositeID.dialog_id, messageObject.messageOwner.grouped_id);
            if (messageObjectPut == null || this.primaryByGroup.get(groupKey) != messageObjectPut) {
                return;
            }
            this.primaryByGroup.put(groupKey, messageObject);
        }
    }

    public void releaseRow(MessageObject messageObject) {
        this.messagesByRealId.remove(new MessageCompositeID(messageObject.getDialogId(), messageObject.getRealId()));
        if (messageObject.hasValidGroupId()) {
            GroupKey groupKey = new GroupKey(messageObject.getDialogId(), messageObject.messageOwner.grouped_id);
            if (this.primaryByGroup.get(groupKey) == messageObject) {
                this.primaryByGroup.remove(groupKey);
            }
        }
    }

    public void purge(MessageObject messageObject) {
        MessageCompositeID messageCompositeID = new MessageCompositeID(messageObject.getDialogId(), messageObject.getRealId());
        this.generatedIds.remove(messageCompositeID);
        this.messagesByRealId.remove(messageCompositeID);
        this.realIdsByGeneratedId.remove(Integer.valueOf(messageObject.getId()));
        if (messageObject.hasValidGroupId()) {
            GroupKey groupKey = new GroupKey(messageCompositeID.dialog_id, messageObject.messageOwner.grouped_id);
            if (this.primaryByGroup.get(groupKey) == messageObject) {
                this.primaryByGroup.remove(groupKey);
            }
        }
    }

    public MessageObject getByRealId(long j, int i) {
        return this.messagesByRealId.get(new MessageCompositeID(j, i));
    }

    public MessageObject getByAnyId(long j, int i) {
        MessageObject messageObject = this.messagesByRealId.get(new MessageCompositeID(j, i));
        if (messageObject != null) {
            return messageObject;
        }
        int iResolveRealMessageId = resolveRealMessageId(j, i);
        if (iResolveRealMessageId != i) {
            return this.messagesByRealId.get(new MessageCompositeID(j, iResolveRealMessageId));
        }
        return null;
    }

    public int resolveRealMessageId(long j, int i) {
        MessageCompositeID messageCompositeID = this.realIdsByGeneratedId.get(Integer.valueOf(i));
        return (messageCompositeID == null || messageCompositeID.dialog_id != j) ? i : messageCompositeID.id;
    }

    public long resolveRealDialogId(int i) {
        MessageCompositeID messageCompositeID = this.realIdsByGeneratedId.get(Integer.valueOf(i));
        if (messageCompositeID != null) {
            return messageCompositeID.dialog_id;
        }
        return 0L;
    }

    public boolean isEmpty() {
        return this.realIdsByGeneratedId.isEmpty();
    }

    public void clear() {
        this.generatedIds.clear();
        this.realIdsByGeneratedId.clear();
        this.messagesByRealId.clear();
        this.primaryByGroup.clear();
        this.lastGeneratedId = 2147483637;
    }

    private void updatePrimaryGroupFlag(MessageObject messageObject, long j, int i) {
        if (!messageObject.hasValidGroupId()) {
            messageObject.isPrimaryGroupMessage = false;
            return;
        }
        GroupKey groupKey = new GroupKey(j, messageObject.messageOwner.grouped_id);
        MessageObject messageObject2 = this.primaryByGroup.get(groupKey);
        if (messageObject2 == null || i > messageObject2.getRealId()) {
            messageObject.isPrimaryGroupMessage = true;
            if (messageObject2 != null) {
                messageObject2.isPrimaryGroupMessage = false;
            }
            this.primaryByGroup.put(groupKey, messageObject);
            return;
        }
        messageObject.isPrimaryGroupMessage = false;
    }

    public static final class GroupKey {
        final long dialog_id;
        final long groupedId;

        public GroupKey(long j, long j2) {
            this.dialog_id = j;
            this.groupedId = j2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && GroupKey.class == obj.getClass()) {
                GroupKey groupKey = (GroupKey) obj;
                if (this.dialog_id == groupKey.dialog_id && this.groupedId == groupKey.groupedId) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return (Long.hashCode(this.dialog_id) * 31) + Long.hashCode(this.groupedId);
        }
    }

    public static final class MessageCompositeID {
        final long dialog_id;
        final int id;

        public MessageCompositeID(TLRPC.Message message) {
            this.dialog_id = MessageObject.getDialogId(message);
            this.id = message.id;
        }

        public MessageCompositeID(long j, int i) {
            this.dialog_id = j;
            this.id = i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && MessageCompositeID.class == obj.getClass()) {
                MessageCompositeID messageCompositeID = (MessageCompositeID) obj;
                if (this.dialog_id == messageCompositeID.dialog_id && this.id == messageCompositeID.id) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return (Long.hashCode(this.dialog_id) * 31) + this.id;
        }
    }
}
