package com.exteragram.messenger.utils.chats;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.StickerTimeMode;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.Theme;

/* JADX INFO: loaded from: classes4.dex */
public abstract class StickerTime {

    public static final class Row {
        public float anchorY;
        public float cellWidth;
        public boolean hasName;
        public MessageObject message;
        public float nameHeight;
        public float nameLeft;
        public float nameTop;
        public float nameWidth;
        public float overStickerOffsetX;
        public boolean sideButton;
        public float stickerLeft;
        public float stickerRight;
        public float stickerWidth;
        public float timeWidth;
    }

    public static boolean isHidden(MessageObject messageObject) {
        return ExteraConfig.getStickerTimeMode() == StickerTimeMode.HIDDEN && messageObject != null && messageObject.isAnyKindOfSticker();
    }

    public static boolean shouldPreserveOnPreview(MessageObject messageObject, MessageObject messageObject2) {
        return messageObject != null && messageObject2 != null && messageObject.getId() == messageObject2.getId() && messageObject2.preview && messageObject2.isAnyKindOfSticker() && ExteraConfig.getStickerTimeMode() != StickerTimeMode.HIDDEN;
    }

    public static float getTimeOffsetX(Row row, float f) {
        MessageObject messageObject = row.message;
        if (messageObject == null || !messageObject.isAnyKindOfSticker()) {
            return 0.0f;
        }
        if (!isBeside(row)) {
            return row.overStickerOffsetX;
        }
        return (getTimeLeft(row) + AndroidUtilities.dp(6.0f)) - f;
    }

    public static float getSideButtonX(Row row, float f) {
        if (!isBeside(row)) {
            return f;
        }
        float fDp = AndroidUtilities.dp(8.0f) + AndroidUtilities.dp(32.0f);
        if (row.message.isOutOwner()) {
            return Math.min(f, getTimeLeft(row) - fDp);
        }
        return Math.max(f, getTimeLeft(row) + getTimeWidth(row) + AndroidUtilities.dp(8.0f));
    }

    public static float getSideButtonTouchLeft(Row row, float f) {
        return (!isBeside(row) || row.message.isOutOwner()) ? f : AndroidUtilities.dp(8.0f);
    }

    public static float getSideButtonTouchRight(Row row, float f) {
        return (isBeside(row) && row.message.isOutOwner()) ? AndroidUtilities.dp(8.0f) + AndroidUtilities.dp(32.0f) : f;
    }

    private static boolean isBeside(Row row) {
        MessageObject messageObject = row.message;
        if (messageObject != null && messageObject.isAnyKindOfSticker() && ExteraConfig.getStickerTimeMode() == StickerTimeMode.SIDE && messageObject.type != 19 && row.stickerWidth > 0.0f) {
            float fDp = AndroidUtilities.dp(8.0f);
            float timeLeft = getTimeLeft(row);
            float timeWidth = getTimeWidth(row) + timeLeft;
            if (row.sideButton) {
                if (messageObject.isOutOwner()) {
                    timeLeft -= AndroidUtilities.dp(32.0f) + fDp;
                } else {
                    timeWidth += AndroidUtilities.dp(32.0f) + fDp;
                }
            }
            if (timeLeft >= fDp && timeWidth <= row.cellWidth - fDp) {
                return true;
            }
        }
        return false;
    }

    private static float getTimeWidth(Row row) {
        return row.timeWidth + AndroidUtilities.dp(12.0f) + (row.message.isOutOwner() ? AndroidUtilities.dp(20.0f) : 0);
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0018  */
    private static float getTimeLeft(Row row) {
        boolean z;
        float fDp = AndroidUtilities.dp(8.0f);
        if (row.hasName) {
            float f = row.nameTop;
            if (overlapsRow(row, f, row.nameHeight + f)) {
                z = true;
            } else {
                z = false;
            }
        } else {
            z = false;
        }
        if (row.message.isOutOwner()) {
            float fMin = row.stickerLeft - fDp;
            if (z) {
                fMin = Math.min(fMin, row.nameLeft - fDp);
            }
            return fMin - getTimeWidth(row);
        }
        float f2 = row.stickerRight + fDp;
        return z ? Math.max(f2, row.nameLeft + row.nameWidth + fDp) : f2;
    }

    private static boolean overlapsRow(Row row, float f, float f2) {
        float fDp = row.anchorY - AndroidUtilities.dp(23.0f);
        return f < Math.max((float) AndroidUtilities.dp(17.0f), Theme.chat_timePaint.getTextSize() + ((float) AndroidUtilities.dp(5.0f))) + fDp && f2 > fDp;
    }
}
