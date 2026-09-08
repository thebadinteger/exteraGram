package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import androidx.collection.LongSparseArray;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.material.timepicker.TimeModel;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.Forum.ForumUtilities;

class ChatsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private AccountInstance accountInstance;
    private int appWidgetId;
    private RectF bitmapRect;
    private boolean deleted;
    private Context mContext;
    private Paint roundPaint;
    private ArrayList<Long> dids = new ArrayList<>();
    private LongSparseArray<TLRPC.Dialog> dialogs = new LongSparseArray<>();
    private LongSparseArray<MessageObject> messageObjects = new LongSparseArray<>();

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public int getViewTypeCount() {
        return 2;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public void onDestroy() {
    }

    public ChatsRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        Theme.createDialogsResources(context);
        this.appWidgetId = intent.getIntExtra("appWidgetId", 0);
        SharedPreferences sharedPreferences = context.getSharedPreferences("shortcut_widget", 0);
        int i = sharedPreferences.getInt("account" + this.appWidgetId, -1);
        if (i >= 0) {
            this.accountInstance = AccountInstance.getInstance(i);
        }
        StringBuilder sb = new StringBuilder("deleted");
        sb.append(this.appWidgetId);
        this.deleted = sharedPreferences.getBoolean(sb.toString(), false) || this.accountInstance == null;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public void onCreate() {
        ApplicationLoader.postInitApplication();
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public int getCount() {
        if (this.deleted) {
            return 1;
        }
        return this.dids.size() + 1;
    }

    /* JADX WARN: Code duplicated, block: B:109:0x0261  */
    /* JADX WARN: Code duplicated, block: B:111:0x026d  */
    /* JADX WARN: Code duplicated, block: B:112:0x027c  */
    /* JADX WARN: Code duplicated, block: B:115:0x029c  */
    /* JADX WARN: Code duplicated, block: B:117:0x02a2  */
    /* JADX WARN: Code duplicated, block: B:122:0x02af  */
    /* JADX WARN: Code duplicated, block: B:124:0x02bf  */
    /* JADX WARN: Code duplicated, block: B:188:0x043a  */
    /* JADX WARN: Code duplicated, block: B:190:0x0442  */
    /* JADX WARN: Code duplicated, block: B:195:0x0454  */
    /* JADX WARN: Code duplicated, block: B:197:0x0458  */
    /* JADX WARN: Code duplicated, block: B:202:0x046a  */
    /* JADX WARN: Code duplicated, block: B:204:0x046e  */
    /* JADX WARN: Code duplicated, block: B:206:0x0474  */
    /* JADX WARN: Code duplicated, block: B:207:0x0477  */
    /* JADX WARN: Code duplicated, block: B:209:0x047d  */
    /* JADX WARN: Code duplicated, block: B:210:0x0480  */
    /* JADX WARN: Code duplicated, block: B:212:0x0486  */
    /* JADX WARN: Code duplicated, block: B:213:0x0489  */
    /* JADX WARN: Code duplicated, block: B:215:0x048f  */
    /* JADX WARN: Code duplicated, block: B:217:0x04a4  */
    /* JADX WARN: Code duplicated, block: B:219:0x04a8  */
    /* JADX WARN: Code duplicated, block: B:221:0x04c1  */
    /* JADX WARN: Code duplicated, block: B:223:0x04c5  */
    /* JADX WARN: Code duplicated, block: B:224:0x04dd  */
    /* JADX WARN: Code duplicated, block: B:226:0x04e3  */
    /* JADX WARN: Code duplicated, block: B:227:0x04f7  */
    /* JADX WARN: Code duplicated, block: B:230:0x0505  */
    /* JADX WARN: Code duplicated, block: B:234:0x0534  */
    /* JADX WARN: Code duplicated, block: B:238:0x0545  */
    /* JADX WARN: Code duplicated, block: B:248:0x059f  */
    /* JADX WARN: Code duplicated, block: B:251:0x05b3  */
    /* JADX WARN: Code duplicated, block: B:252:0x05be  */
    /* JADX WARN: Code duplicated, block: B:255:0x05e6  */
    /* JADX WARN: Instruction removed from duplicated block: B:219:0x04a8, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:223:0x04c5, please report this as an issue */
    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public RemoteViews getViewAt(int i) {
        TLRPC.Chat chat;
        String str;
        TLRPC.User user;
        TLRPC.FileLocation fileLocation;
        String monoForumTitle;
        TLRPC.FileLocation fileLocation2;
        TLRPC.ChatPhoto chatPhoto;
        boolean z;
        Bitmap bitmapDecodeFile;
        MessageObject messageObject;
        TLRPC.Dialog dialog;
        int i2;
        Bundle bundle;
        int i3;
        long fromChatId;
        boolean zIsUserDialog;
        AccountInstance accountInstance;
        TLRPC.Chat chat2;
        TLRPC.User user2;
        int color;
        String str2;
        TLRPC.MessageMedia messageMedia;
        CharSequence charSequence;
        CharSequence charSequence2;
        String strReplace;
        SpannableStringBuilder spannableStringBuilderValueOf;
        String string;
        SpannableStringBuilder spannableStringBuilder;
        CharSequence string2;
        CharSequence charSequence3;
        TLRPC.MessageAction messageAction;
        AvatarDrawable avatarDrawable;
        String name;
        TLRPC.UserProfilePhoto userProfilePhoto;
        TLRPC.FileLocation fileLocation3;
        if (this.deleted) {
            RemoteViews remoteViews = new RemoteViews(this.mContext.getPackageName(), R.layout.widget_deleted);
            remoteViews.setTextViewText(R.id.widget_deleted_text, LocaleController.getString(R.string.WidgetLoggedOff));
            return remoteViews;
        }
        if (i >= this.dids.size()) {
            RemoteViews remoteViews2 = new RemoteViews(this.mContext.getPackageName(), R.layout.widget_edititem);
            remoteViews2.setTextViewText(R.id.widget_edititem_text, LocaleController.getString(R.string.TapToEditWidget));
            Bundle bundle2 = new Bundle();
            bundle2.putInt("appWidgetId", this.appWidgetId);
            bundle2.putInt("appWidgetType", 0);
            bundle2.putInt("currentAccount", this.accountInstance.getCurrentAccount());
            Intent intent = new Intent();
            intent.putExtras(bundle2);
            remoteViews2.setOnClickFillInIntent(R.id.widget_edititem, intent);
            return remoteViews2;
        }
        Long l = this.dids.get(i);
        boolean zIsUserDialog2 = DialogObject.isUserDialog(l.longValue());
        AccountInstance accountInstance2 = this.accountInstance;
        String str3 = _UrlKt.FRAGMENT_ENCODE_SET;
        if (zIsUserDialog2) {
            user = accountInstance2.getMessagesController().getUser(l);
            if (user == null) {
                str = _UrlKt.FRAGMENT_ENCODE_SET;
            } else {
                if (UserObject.isUserSelf(user)) {
                    name = LocaleController.getString(R.string.SavedMessages);
                } else if (UserObject.isReplyUser(user)) {
                    name = LocaleController.getString(R.string.RepliesTitle);
                } else if (UserObject.isDeleted(user)) {
                    name = LocaleController.getString(R.string.HiddenName);
                } else {
                    name = ContactsController.formatName(user.first_name, user.last_name);
                }
                if (UserObject.isReplyUser(user) || UserObject.isUserSelf(user) || (userProfilePhoto = user.photo) == null || (fileLocation3 = userProfilePhoto.photo_small) == null || fileLocation3.volume_id == 0 || fileLocation3.local_id == 0) {
                    str = name;
                } else {
                    fileLocation = fileLocation3;
                    str = name;
                    chat = null;
                }
            }
            chat = null;
            fileLocation = null;
        } else {
            TLRPC.Chat chat3 = accountInstance2.getMessagesController().getChat(Long.valueOf(-l.longValue()));
            if (chat3 == null) {
                chat = chat3;
                str = _UrlKt.FRAGMENT_ENCODE_SET;
                user = null;
                fileLocation = null;
            } else {
                if (ChatObject.isMonoForum(chat3)) {
                    monoForumTitle = ForumUtilities.getMonoForumTitle(this.accountInstance.getCurrentAccount(), chat3);
                    TLRPC.Chat chat4 = this.accountInstance.getMessagesController().getChat(Long.valueOf(chat3.linked_monoforum_id));
                    if (chat4 == null || (chatPhoto = chat4.photo) == null || (fileLocation2 = chatPhoto.photo_small) == null || fileLocation2.volume_id == 0 || fileLocation2.local_id == 0) {
                        fileLocation2 = null;
                    }
                } else {
                    monoForumTitle = chat3.title;
                    TLRPC.ChatPhoto chatPhoto2 = chat3.photo;
                    if (chatPhoto2 == null || (fileLocation2 = chatPhoto2.photo_small) == null || fileLocation2.volume_id == 0 || fileLocation2.local_id == 0) {
                        str = monoForumTitle;
                        fileLocation = null;
                        chat = chat3;
                        user = null;
                    }
                }
                fileLocation = fileLocation2;
                str = monoForumTitle;
                chat = chat3;
                user = null;
            }
        }
        RemoteViews remoteViews3 = new RemoteViews(this.mContext.getPackageName(), R.layout.shortcut_widget_item);
        remoteViews3.setTextViewText(R.id.shortcut_widget_item_text, str);
        if (fileLocation != null) {
            try {
                bitmapDecodeFile = BitmapFactory.decodeFile(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(fileLocation, true).toString());
            } catch (Throwable th) {
                th = th;
                z = true;
                FileLog.e(th);
                messageObject = this.messageObjects.get(l.longValue());
                dialog = this.dialogs.get(l.longValue());
                if (messageObject != null) {
                    if (dialog != null) {
                        remoteViews3.setTextViewText(R.id.shortcut_widget_item_time, _UrlKt.FRAGMENT_ENCODE_SET);
                    } else {
                        remoteViews3.setTextViewText(R.id.shortcut_widget_item_time, _UrlKt.FRAGMENT_ENCODE_SET);
                    }
                    remoteViews3.setTextViewText(R.id.shortcut_widget_item_message, _UrlKt.FRAGMENT_ENCODE_SET);
                } else {
                    fromChatId = messageObject.getFromChatId();
                    zIsUserDialog = DialogObject.isUserDialog(fromChatId);
                    accountInstance = this.accountInstance;
                    if (zIsUserDialog) {
                        user2 = accountInstance.getMessagesController().getUser(Long.valueOf(fromChatId));
                        chat2 = null;
                    } else {
                        chat2 = accountInstance.getMessagesController().getChat(Long.valueOf(-fromChatId));
                        user2 = null;
                    }
                    color = this.mContext.getResources().getColor(R.color.widget_text);
                    if (messageObject.messageOwner instanceof TLRPC.TL_messageService) {
                        if (ChatObject.isChannel(chat)) {
                            messageAction = messageObject.messageOwner.action;
                            if (!(messageAction instanceof TLRPC.TL_messageActionHistoryClear)) {
                                charSequence3 = str3;
                                charSequence3 = str3;
                                charSequence3 = messageObject.messageText;
                            }
                        } else {
                            charSequence3 = str3;
                            charSequence3 = str3;
                            charSequence3 = messageObject.messageText;
                        }
                        charSequence3 = str3;
                        charSequence3 = str3;
                        charSequence3 = str3;
                        color = this.mContext.getResources().getColor(R.color.widget_action_text);
                        string2 = charSequence3;
                    } else {
                        str2 = "📎 ";
                        if (chat == null) {
                            messageMedia = messageObject.messageOwner.media;
                            if (!(messageMedia instanceof TLRPC.TL_messageMediaPhoto)) {
                                if (!(messageMedia instanceof TLRPC.TL_messageMediaDocument)) {
                                    if (messageObject.caption != null) {
                                        if (messageObject.isVideo()) {
                                            str2 = "📹 ";
                                        } else if (messageObject.isVoice()) {
                                            str2 = "🎤 ";
                                        } else if (messageObject.isMusic()) {
                                            str2 = "🎧 ";
                                        } else if (messageObject.isPhoto()) {
                                            str2 = "🖼 ";
                                        }
                                        string2 = str2 + ((Object) messageObject.caption);
                                    } else {
                                        if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                                            charSequence = "📊 " + ((TLRPC.TL_messageMediaPoll) messageMedia).poll.question.text;
                                        } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
                                            charSequence = "🎮 " + messageObject.messageOwner.media.game.title;
                                        } else if (messageObject.type == 14) {
                                            charSequence = String.format("🎧 %s - %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle());
                                        } else {
                                            charSequence = messageObject.messageText;
                                            AndroidUtilities.highlightText(charSequence, messageObject.highlightedWords, (Theme.ResourcesProvider) null);
                                        }
                                        charSequence2 = charSequence;
                                        string2 = charSequence2;
                                        if (messageObject.messageOwner.media != null) {
                                            string2 = charSequence2;
                                            color = this.mContext.getResources().getColor(R.color.widget_action_text);
                                            string2 = charSequence2;
                                        }
                                    }
                                } else if (messageObject.caption != null) {
                                    if (messageObject.isVideo()) {
                                        str2 = "📹 ";
                                    } else if (messageObject.isVoice()) {
                                        str2 = "🎤 ";
                                    } else if (messageObject.isMusic()) {
                                        str2 = "🎧 ";
                                    } else if (messageObject.isPhoto()) {
                                        str2 = "🖼 ";
                                    }
                                    string2 = str2 + ((Object) messageObject.caption);
                                } else {
                                    if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                                        charSequence = "📊 " + ((TLRPC.TL_messageMediaPoll) messageMedia).poll.question.text;
                                    } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
                                        charSequence = "🎮 " + messageObject.messageOwner.media.game.title;
                                    } else if (messageObject.type == 14) {
                                        charSequence = String.format("🎧 %s - %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle());
                                    } else {
                                        charSequence = messageObject.messageText;
                                        AndroidUtilities.highlightText(charSequence, messageObject.highlightedWords, (Theme.ResourcesProvider) null);
                                    }
                                    charSequence2 = charSequence;
                                    string2 = charSequence2;
                                    if (messageObject.messageOwner.media != null) {
                                        string2 = charSequence2;
                                        color = this.mContext.getResources().getColor(R.color.widget_action_text);
                                        string2 = charSequence2;
                                    }
                                }
                            } else if (!(messageMedia instanceof TLRPC.TL_messageMediaDocument)) {
                                if (messageObject.caption != null) {
                                    if (messageObject.isVideo()) {
                                        str2 = "📹 ";
                                    } else if (messageObject.isVoice()) {
                                        str2 = "🎤 ";
                                    } else if (messageObject.isMusic()) {
                                        str2 = "🎧 ";
                                    } else if (messageObject.isPhoto()) {
                                        str2 = "🖼 ";
                                    }
                                    string2 = str2 + ((Object) messageObject.caption);
                                } else {
                                    if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                                        charSequence = "📊 " + ((TLRPC.TL_messageMediaPoll) messageMedia).poll.question.text;
                                    } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
                                        charSequence = "🎮 " + messageObject.messageOwner.media.game.title;
                                    } else if (messageObject.type == 14) {
                                        charSequence = String.format("🎧 %s - %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle());
                                    } else {
                                        charSequence = messageObject.messageText;
                                        AndroidUtilities.highlightText(charSequence, messageObject.highlightedWords, (Theme.ResourcesProvider) null);
                                    }
                                    charSequence2 = charSequence;
                                    string2 = charSequence2;
                                    if (messageObject.messageOwner.media != null) {
                                        string2 = charSequence2;
                                        color = this.mContext.getResources().getColor(R.color.widget_action_text);
                                        string2 = charSequence2;
                                    }
                                }
                            } else if (messageObject.caption != null) {
                                if (messageObject.isVideo()) {
                                    str2 = "📹 ";
                                } else if (messageObject.isVoice()) {
                                    str2 = "🎤 ";
                                } else if (messageObject.isMusic()) {
                                    str2 = "🎧 ";
                                } else if (messageObject.isPhoto()) {
                                    str2 = "🖼 ";
                                }
                                string2 = str2 + ((Object) messageObject.caption);
                            } else {
                                if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                                    charSequence = "📊 " + ((TLRPC.TL_messageMediaPoll) messageMedia).poll.question.text;
                                } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
                                    charSequence = "🎮 " + messageObject.messageOwner.media.game.title;
                                } else if (messageObject.type == 14) {
                                    charSequence = String.format("🎧 %s - %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle());
                                } else {
                                    charSequence = messageObject.messageText;
                                    AndroidUtilities.highlightText(charSequence, messageObject.highlightedWords, (Theme.ResourcesProvider) null);
                                }
                                charSequence2 = charSequence;
                                string2 = charSequence2;
                                if (messageObject.messageOwner.media != null) {
                                    string2 = charSequence2;
                                    color = this.mContext.getResources().getColor(R.color.widget_action_text);
                                    string2 = charSequence2;
                                }
                            }
                        } else {
                            messageMedia = messageObject.messageOwner.media;
                            if (!(messageMedia instanceof TLRPC.TL_messageMediaPhoto)) {
                                if (!(messageMedia instanceof TLRPC.TL_messageMediaDocument)) {
                                    if (messageObject.caption != null) {
                                        if (messageObject.isVideo()) {
                                            str2 = "📹 ";
                                        } else if (messageObject.isVoice()) {
                                            str2 = "🎤 ";
                                        } else if (messageObject.isMusic()) {
                                            str2 = "🎧 ";
                                        } else if (messageObject.isPhoto()) {
                                            str2 = "🖼 ";
                                        }
                                        string2 = str2 + ((Object) messageObject.caption);
                                    } else {
                                        if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                                            charSequence = "📊 " + ((TLRPC.TL_messageMediaPoll) messageMedia).poll.question.text;
                                        } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
                                            charSequence = "🎮 " + messageObject.messageOwner.media.game.title;
                                        } else if (messageObject.type == 14) {
                                            charSequence = String.format("🎧 %s - %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle());
                                        } else {
                                            charSequence = messageObject.messageText;
                                            AndroidUtilities.highlightText(charSequence, messageObject.highlightedWords, (Theme.ResourcesProvider) null);
                                        }
                                        charSequence2 = charSequence;
                                        string2 = charSequence2;
                                        if (messageObject.messageOwner.media != null) {
                                            string2 = charSequence2;
                                            color = this.mContext.getResources().getColor(R.color.widget_action_text);
                                            string2 = charSequence2;
                                        }
                                    }
                                } else if (messageObject.caption != null) {
                                    if (messageObject.isVideo()) {
                                        str2 = "📹 ";
                                    } else if (messageObject.isVoice()) {
                                        str2 = "🎤 ";
                                    } else if (messageObject.isMusic()) {
                                        str2 = "🎧 ";
                                    } else if (messageObject.isPhoto()) {
                                        str2 = "🖼 ";
                                    }
                                    string2 = str2 + ((Object) messageObject.caption);
                                } else {
                                    if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                                        charSequence = "📊 " + ((TLRPC.TL_messageMediaPoll) messageMedia).poll.question.text;
                                    } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
                                        charSequence = "🎮 " + messageObject.messageOwner.media.game.title;
                                    } else if (messageObject.type == 14) {
                                        charSequence = String.format("🎧 %s - %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle());
                                    } else {
                                        charSequence = messageObject.messageText;
                                        AndroidUtilities.highlightText(charSequence, messageObject.highlightedWords, (Theme.ResourcesProvider) null);
                                    }
                                    charSequence2 = charSequence;
                                    string2 = charSequence2;
                                    if (messageObject.messageOwner.media != null) {
                                        string2 = charSequence2;
                                        color = this.mContext.getResources().getColor(R.color.widget_action_text);
                                        string2 = charSequence2;
                                    }
                                }
                            } else if (!(messageMedia instanceof TLRPC.TL_messageMediaDocument)) {
                                if (messageObject.caption != null) {
                                    if (messageObject.isVideo()) {
                                        str2 = "📹 ";
                                    } else if (messageObject.isVoice()) {
                                        str2 = "🎤 ";
                                    } else if (messageObject.isMusic()) {
                                        str2 = "🎧 ";
                                    } else if (messageObject.isPhoto()) {
                                        str2 = "🖼 ";
                                    }
                                    string2 = str2 + ((Object) messageObject.caption);
                                } else {
                                    if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                                        charSequence = "📊 " + ((TLRPC.TL_messageMediaPoll) messageMedia).poll.question.text;
                                    } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
                                        charSequence = "🎮 " + messageObject.messageOwner.media.game.title;
                                    } else if (messageObject.type == 14) {
                                        charSequence = String.format("🎧 %s - %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle());
                                    } else {
                                        charSequence = messageObject.messageText;
                                        AndroidUtilities.highlightText(charSequence, messageObject.highlightedWords, (Theme.ResourcesProvider) null);
                                    }
                                    charSequence2 = charSequence;
                                    string2 = charSequence2;
                                    if (messageObject.messageOwner.media != null) {
                                        string2 = charSequence2;
                                        color = this.mContext.getResources().getColor(R.color.widget_action_text);
                                        string2 = charSequence2;
                                    }
                                }
                            } else if (messageObject.caption != null) {
                                if (messageObject.isVideo()) {
                                    str2 = "📹 ";
                                } else if (messageObject.isVoice()) {
                                    str2 = "🎤 ";
                                } else if (messageObject.isMusic()) {
                                    str2 = "🎧 ";
                                } else if (messageObject.isPhoto()) {
                                    str2 = "🖼 ";
                                }
                                string2 = str2 + ((Object) messageObject.caption);
                            } else {
                                if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                                    charSequence = "📊 " + ((TLRPC.TL_messageMediaPoll) messageMedia).poll.question.text;
                                } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
                                    charSequence = "🎮 " + messageObject.messageOwner.media.game.title;
                                } else if (messageObject.type == 14) {
                                    charSequence = String.format("🎧 %s - %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle());
                                } else {
                                    charSequence = messageObject.messageText;
                                    AndroidUtilities.highlightText(charSequence, messageObject.highlightedWords, (Theme.ResourcesProvider) null);
                                }
                                charSequence2 = charSequence;
                                string2 = charSequence2;
                                if (messageObject.messageOwner.media != null) {
                                    string2 = charSequence2;
                                    color = this.mContext.getResources().getColor(R.color.widget_action_text);
                                    string2 = charSequence2;
                                }
                            }
                        }
                    }
                    string2 = charSequence2;
                    remoteViews3.setTextViewText(R.id.shortcut_widget_item_time, LocaleController.stringForMessageListDate(messageObject.messageOwner.date));
                    remoteViews3.setTextViewText(R.id.shortcut_widget_item_message, string2.toString());
                    remoteViews3.setTextColor(R.id.shortcut_widget_item_message, color);
                }
                if (dialog == null) {
                    remoteViews3.setViewVisibility(R.id.shortcut_widget_item_badge, 8);
                } else {
                    remoteViews3.setViewVisibility(R.id.shortcut_widget_item_badge, 8);
                }
                bundle = new Bundle();
                if (DialogObject.isUserDialog(l.longValue())) {
                    bundle.putLong("userId", l.longValue());
                } else {
                    bundle.putLong("chatId", -l.longValue());
                }
                bundle.putInt("currentAccount", this.accountInstance.getCurrentAccount());
                Intent intent2 = new Intent();
                intent2.putExtras(bundle);
                remoteViews3.setOnClickFillInIntent(R.id.shortcut_widget_item, intent2);
                remoteViews3.setViewVisibility(R.id.shortcut_widget_item_divider, i == getCount() ? 8 : 0);
                return remoteViews3;
            }
        } else {
            bitmapDecodeFile = null;
        }
        int iDp = AndroidUtilities.dp(48.0f);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(iDp, iDp, Bitmap.Config.ARGB_8888);
        bitmapCreateBitmap.eraseColor(0);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        if (bitmapDecodeFile == null) {
            if (user != null) {
                avatarDrawable = new AvatarDrawable(user);
                if (UserObject.isReplyUser(user)) {
                    avatarDrawable.setAvatarType(12);
                } else if (UserObject.isUserSelf(user)) {
                    avatarDrawable.setAvatarType(1);
                }
            } else {
                avatarDrawable = new AvatarDrawable();
                avatarDrawable.setInfo(this.accountInstance.getCurrentAccount(), chat);
            }
            avatarDrawable.setRoundRadius(ExteraConfig.getAvatarCorners(iDp, true, chat != null && chat.forum));
            avatarDrawable.setBounds(0, 0, iDp, iDp);
            avatarDrawable.draw(canvas);
            z = true;
        } else {
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            BitmapShader bitmapShader = new BitmapShader(bitmapDecodeFile, tileMode, tileMode);
            if (this.roundPaint == null) {
                this.roundPaint = new Paint(1);
                this.bitmapRect = new RectF();
            }
            float width = iDp / bitmapDecodeFile.getWidth();
            canvas.save();
            canvas.scale(width, width);
            float avatarCorners = ExteraConfig.getAvatarCorners(bitmapDecodeFile.getWidth(), true, chat != null && chat.forum);
            this.roundPaint.setShader(bitmapShader);
            z = true;
            try {
                this.bitmapRect.set(0.0f, 0.0f, bitmapDecodeFile.getWidth(), bitmapDecodeFile.getHeight());
                canvas.drawRoundRect(this.bitmapRect, avatarCorners, avatarCorners, this.roundPaint);
                canvas.restore();
            } catch (Throwable th2) {
                th = th2;
                FileLog.e(th);
            }
        }
        canvas.setBitmap(null);
        remoteViews3.setImageViewBitmap(R.id.shortcut_widget_item_avatar, bitmapCreateBitmap);
        messageObject = this.messageObjects.get(l.longValue());
        dialog = this.dialogs.get(l.longValue());
        if (messageObject != null) {
            if (dialog != null || (i2 = dialog.last_message_date) == 0) {
                remoteViews3.setTextViewText(R.id.shortcut_widget_item_time, _UrlKt.FRAGMENT_ENCODE_SET);
            } else {
                remoteViews3.setTextViewText(R.id.shortcut_widget_item_time, LocaleController.stringForMessageListDate(i2));
            }
            remoteViews3.setTextViewText(R.id.shortcut_widget_item_message, _UrlKt.FRAGMENT_ENCODE_SET);
        } else {
            fromChatId = messageObject.getFromChatId();
            zIsUserDialog = DialogObject.isUserDialog(fromChatId);
            accountInstance = this.accountInstance;
            if (zIsUserDialog) {
                user2 = accountInstance.getMessagesController().getUser(Long.valueOf(fromChatId));
                chat2 = null;
            } else {
                chat2 = accountInstance.getMessagesController().getChat(Long.valueOf(-fromChatId));
                user2 = null;
            }
            color = this.mContext.getResources().getColor(R.color.widget_text);
            if (messageObject.messageOwner instanceof TLRPC.TL_messageService) {
                if (ChatObject.isChannel(chat)) {
                    messageAction = messageObject.messageOwner.action;
                    if (!(messageAction instanceof TLRPC.TL_messageActionHistoryClear) && !(messageAction instanceof TLRPC.TL_messageActionChannelMigrateFrom)) {
                        charSequence3 = str3;
                        charSequence3 = str3;
                        charSequence3 = messageObject.messageText;
                    }
                } else {
                    charSequence3 = str3;
                    charSequence3 = str3;
                    charSequence3 = messageObject.messageText;
                }
                charSequence3 = str3;
                charSequence3 = str3;
                charSequence3 = str3;
                color = this.mContext.getResources().getColor(R.color.widget_action_text);
                string2 = charSequence3;
            } else {
                str2 = "📎 ";
                if (chat == null && chat2 == null && (!ChatObject.isChannel(chat) || ChatObject.isMegagroup(chat))) {
                    if (messageObject.isOutOwner()) {
                        strReplace = LocaleController.getString(R.string.FromYou);
                    } else if (user2 != null) {
                        strReplace = UserObject.getFirstName(user2).replace("\n", _UrlKt.FRAGMENT_ENCODE_SET);
                    } else {
                        strReplace = "DELETED";
                    }
                    String str4 = strReplace;
                    CharSequence charSequence4 = messageObject.caption;
                    try {
                        if (charSequence4 != null) {
                            String string3 = charSequence4.toString();
                            if (string3.length() > 150) {
                                string3 = string3.substring(0, 150);
                            }
                            if (messageObject.isVideo()) {
                                str2 = "📹 ";
                            } else if (messageObject.isVoice()) {
                                str2 = "🎤 ";
                            } else if (messageObject.isMusic()) {
                                str2 = "🎧 ";
                            } else if (messageObject.isPhoto()) {
                                str2 = "🖼 ";
                            }
                            spannableStringBuilderValueOf = SpannableStringBuilder.valueOf(String.format("%2$s: \u2068%1$s\u2069", str2 + string3.replace('\n', ' '), str4));
                        } else {
                            if (messageObject.messageOwner.media != null && !messageObject.isMediaEmpty()) {
                                color = this.mContext.getResources().getColor(R.color.widget_action_text);
                                TLRPC.MessageMedia messageMedia2 = messageObject.messageOwner.media;
                                if (messageMedia2 instanceof TLRPC.TL_messageMediaPoll) {
                                    string = String.format("📊 \u2068%s\u2069", ((TLRPC.TL_messageMediaPoll) messageMedia2).poll.question.text);
                                } else if (messageMedia2 instanceof TLRPC.TL_messageMediaGame) {
                                    string = String.format("🎮 \u2068%s\u2069", messageMedia2.game.title);
                                } else if (messageObject.type == 14) {
                                    string = String.format("🎧 \u2068%s - %s\u2069", messageObject.getMusicAuthor(), messageObject.getMusicTitle());
                                } else {
                                    string = messageObject.messageText.toString();
                                }
                                SpannableStringBuilder spannableStringBuilderValueOf2 = SpannableStringBuilder.valueOf(String.format("%2$s: \u2068%1$s\u2069", string.replace('\n', ' '), str4));
                                try {
                                    spannableStringBuilderValueOf2.setSpan(new ForegroundColorSpanThemable(Theme.key_chats_attachMessage), str4.length() + 2, spannableStringBuilderValueOf2.length(), 33);
                                    spannableStringBuilder = spannableStringBuilderValueOf2;
                                } catch (Exception e) {
                                    FileLog.e(e);
                                    spannableStringBuilder = spannableStringBuilderValueOf2;
                                }
                            } else {
                                String strSubstring = messageObject.messageOwner.message;
                                if (strSubstring != null) {
                                    if (strSubstring.length() > 150) {
                                        strSubstring = strSubstring.substring(0, 150);
                                    }
                                    spannableStringBuilderValueOf = SpannableStringBuilder.valueOf(String.format("%2$s: \u2068%1$s\u2069", strSubstring.replace('\n', ' ').trim(), str4));
                                } else {
                                    spannableStringBuilderValueOf = SpannableStringBuilder.valueOf(_UrlKt.FRAGMENT_ENCODE_SET);
                                }
                            }
                            spannableStringBuilder.setSpan(new ForegroundColorSpanThemable(Theme.key_chats_nameMessage), 0, str4.length() + 1, 33);
                            string2 = spannableStringBuilder;
                        }
                        spannableStringBuilder.setSpan(new ForegroundColorSpanThemable(Theme.key_chats_nameMessage), 0, str4.length() + 1, 33);
                        string2 = spannableStringBuilder;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                        string2 = spannableStringBuilder;
                    }
                    spannableStringBuilder = spannableStringBuilderValueOf;
                } else {
                    messageMedia = messageObject.messageOwner.media;
                    if (!(messageMedia instanceof TLRPC.TL_messageMediaPhoto) && (messageMedia.photo instanceof TLRPC.TL_photoEmpty) && messageMedia.ttl_seconds != 0) {
                        string2 = LocaleController.getString(R.string.AttachPhotoExpired);
                    } else if (!(messageMedia instanceof TLRPC.TL_messageMediaDocument) && (messageMedia.document instanceof TLRPC.TL_documentEmpty) && messageMedia.ttl_seconds != 0) {
                        string2 = LocaleController.getString(R.string.AttachVideoExpired);
                    } else if (messageObject.caption != null) {
                        if (messageObject.isVideo()) {
                            str2 = "📹 ";
                        } else if (messageObject.isVoice()) {
                            str2 = "🎤 ";
                        } else if (messageObject.isMusic()) {
                            str2 = "🎧 ";
                        } else if (messageObject.isPhoto()) {
                            str2 = "🖼 ";
                        }
                        string2 = str2 + ((Object) messageObject.caption);
                    } else {
                        if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                            charSequence = "📊 " + ((TLRPC.TL_messageMediaPoll) messageMedia).poll.question.text;
                        } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
                            charSequence = "🎮 " + messageObject.messageOwner.media.game.title;
                        } else if (messageObject.type == 14) {
                            charSequence = String.format("🎧 %s - %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle());
                        } else {
                            charSequence = messageObject.messageText;
                            AndroidUtilities.highlightText(charSequence, messageObject.highlightedWords, (Theme.ResourcesProvider) null);
                        }
                        charSequence2 = charSequence;
                        string2 = charSequence2;
                        if (messageObject.messageOwner.media != null && !messageObject.isMediaEmpty()) {
                            string2 = charSequence2;
                            color = this.mContext.getResources().getColor(R.color.widget_action_text);
                            string2 = charSequence2;
                        }
                    }
                }
            }
            string2 = charSequence2;
            remoteViews3.setTextViewText(R.id.shortcut_widget_item_time, LocaleController.stringForMessageListDate(messageObject.messageOwner.date));
            remoteViews3.setTextViewText(R.id.shortcut_widget_item_message, string2.toString());
            remoteViews3.setTextColor(R.id.shortcut_widget_item_message, color);
        }
        if (dialog == null && (i3 = dialog.unread_count) > 0) {
            remoteViews3.setTextViewText(R.id.shortcut_widget_item_badge, String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(i3)));
            remoteViews3.setViewVisibility(R.id.shortcut_widget_item_badge, 0);
            if (this.accountInstance.getMessagesController().isDialogMuted(dialog.id, 0L)) {
                remoteViews3.setBoolean(R.id.shortcut_widget_item_badge, "setEnabled", false);
                remoteViews3.setInt(R.id.shortcut_widget_item_badge, "setBackgroundResource", R.drawable.widget_badge_muted_background);
            } else {
                remoteViews3.setBoolean(R.id.shortcut_widget_item_badge, "setEnabled", z);
                remoteViews3.setInt(R.id.shortcut_widget_item_badge, "setBackgroundResource", R.drawable.widget_badge_background);
            }
        } else {
            remoteViews3.setViewVisibility(R.id.shortcut_widget_item_badge, 8);
        }
        bundle = new Bundle();
        if (DialogObject.isUserDialog(l.longValue())) {
            bundle.putLong("userId", l.longValue());
        } else {
            bundle.putLong("chatId", -l.longValue());
        }
        bundle.putInt("currentAccount", this.accountInstance.getCurrentAccount());
        Intent intent3 = new Intent();
        intent3.putExtras(bundle);
        remoteViews3.setOnClickFillInIntent(R.id.shortcut_widget_item, intent3);
        remoteViews3.setViewVisibility(R.id.shortcut_widget_item_divider, i == getCount() ? 8 : 0);
        return remoteViews3;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public void onDataSetChanged() {
        this.dids.clear();
        this.messageObjects.clear();
        AccountInstance accountInstance = this.accountInstance;
        if (accountInstance == null || !accountInstance.getUserConfig().isClientActivated()) {
            return;
        }
        ArrayList<TLRPC.User> arrayList = new ArrayList<>();
        ArrayList<TLRPC.Chat> arrayList2 = new ArrayList<>();
        LongSparseArray<TLRPC.Message> longSparseArray = new LongSparseArray<>();
        this.accountInstance.getMessagesStorage().getWidgetDialogs(this.appWidgetId, 0, this.dids, this.dialogs, longSparseArray, arrayList, arrayList2);
        this.accountInstance.getMessagesController().putUsers(arrayList, true);
        this.accountInstance.getMessagesController().putChats(arrayList2, true);
        this.messageObjects.clear();
        int size = longSparseArray.size();
        for (int i = 0; i < size; i++) {
            this.messageObjects.put(longSparseArray.keyAt(i), new MessageObject(this.accountInstance.getCurrentAccount(), longSparseArray.valueAt(i), (LongSparseArray<TLRPC.User>) null, (LongSparseArray<TLRPC.Chat>) null, false, true));
        }
    }
}
