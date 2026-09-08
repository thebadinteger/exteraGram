package com.exteragram.messenger.utils.chats;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

public abstract class DoubleTapUtils {
    public static CharSequence[] getDoubleTapActions(boolean z) {
        if (!z) {
            return new CharSequence[]{LocaleController.getString(R.string.Disable), LocaleController.getString(R.string.Reactions), LocaleController.getString(R.string.Reply), LocaleController.getString(R.string.Copy), LocaleController.getString(R.string.Forward), LocaleController.getString(R.string.Save), LocaleController.getString(R.string.Repeat), LocaleController.getString(R.string.Delete), LocaleController.getString(R.string.TranslateMessage)};
        }
        return new CharSequence[]{LocaleController.getString(R.string.Disable), LocaleController.getString(R.string.Reactions), LocaleController.getString(R.string.Reply), LocaleController.getString(R.string.Copy), LocaleController.getString(R.string.Forward), LocaleController.getString(R.string.Edit), LocaleController.getString(R.string.Save), LocaleController.getString(R.string.Repeat), LocaleController.getString(R.string.Delete), LocaleController.getString(R.string.TranslateMessage)};
    }

    public static CharSequence getDoubleTapActionLabel(int i, boolean z) {
        CharSequence[] doubleTapActions = getDoubleTapActions(z);
        return doubleTapActions[Math.min(Math.max(i, 0), doubleTapActions.length - 1)];
    }

    public static int getDoubleTapActionIcon(int i, boolean z) {
        int[] doubleTapIcons = getDoubleTapIcons(z);
        return doubleTapIcons[Math.min(Math.max(i, 0), doubleTapIcons.length - 1)];
    }

    public static int[] getDoubleTapIcons(boolean z) {
        if (!z) {
            return new int[]{R.drawable.msg_block, R.drawable.msg_reactions2, R.drawable.menu_reply, R.drawable.msg_copy, R.drawable.msg_forward, R.drawable.msg_saved, R.drawable.msg_repeat, R.drawable.msg_delete, R.drawable.msg_translate};
        }
        return new int[]{R.drawable.msg_block, R.drawable.msg_reactions2, R.drawable.menu_reply, R.drawable.msg_copy, R.drawable.msg_forward, R.drawable.msg_edit, R.drawable.msg_saved, R.drawable.msg_repeat, R.drawable.msg_delete, R.drawable.msg_translate};
    }

    public static int getActionId(int i, boolean z) {
        int iSanitizeSetting = i == 9 ? 9 : sanitizeSetting(i);
        if (!z) {
            switch (iSanitizeSetting) {
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                case 4:
                    return 4;
                case 5:
                    return 6;
                case 6:
                    return 7;
                case 7:
                    return 8;
                case 8:
                    return 9;
                default:
                    return 0;
            }
        }
        switch (iSanitizeSetting) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            default:
                return 0;
        }
    }

    public static int sanitizeSetting(int i) {
        if (i < 0) {
            return 0;
        }
        return Math.min(i, 9);
    }
}
