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

class ContactsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private AccountInstance accountInstance;
    private int appWidgetId;
    private RectF bitmapRect;
    private boolean deleted;
    private Context mContext;
    private Paint roundPaint;
    private ArrayList<Long> dids = new ArrayList<>();
    private LongSparseArray<TLRPC.Dialog> dialogs = new LongSparseArray<>();

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

    public ContactsRemoteViewsFactory(Context context, Intent intent) {
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
        return ((int) Math.ceil(this.dids.size() / 2.0f)) + 1;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public RemoteViews getViewAt(int i) {
        String str;
        String str2;
        TLRPC.FileLocation fileLocation;
        TLRPC.Chat chat;
        TLRPC.User user;
        Bitmap bitmapDecodeFile;
        int i2;
        String str3;
        AvatarDrawable avatarDrawable;
        String firstName;
        TLRPC.UserProfilePhoto userProfilePhoto;
        if (this.deleted) {
            RemoteViews remoteViews = new RemoteViews(this.mContext.getPackageName(), R.layout.widget_deleted);
            remoteViews.setTextViewText(R.id.widget_deleted_text, LocaleController.getString(R.string.WidgetLoggedOff));
            return remoteViews;
        }
        if (i >= getCount() - 1) {
            RemoteViews remoteViews2 = new RemoteViews(this.mContext.getPackageName(), R.layout.widget_edititem);
            remoteViews2.setTextViewText(R.id.widget_edititem_text, LocaleController.getString(R.string.TapToEditWidgetShort));
            Bundle bundle = new Bundle();
            bundle.putInt("appWidgetId", this.appWidgetId);
            bundle.putInt("appWidgetType", 1);
            bundle.putInt("currentAccount", this.accountInstance.getCurrentAccount());
            Intent intent = new Intent();
            intent.putExtras(bundle);
            remoteViews2.setOnClickFillInIntent(R.id.widget_edititem, intent);
            return remoteViews2;
        }
        RemoteViews remoteViews3 = new RemoteViews(this.mContext.getPackageName(), R.layout.contacts_widget_item);
        int i3 = 0;
        while (i3 < 2) {
            int i4 = (2 * i) + i3;
            if (i4 >= this.dids.size()) {
                remoteViews3.setViewVisibility(i3 == 0 ? R.id.contacts_widget_item1 : R.id.contacts_widget_item2, 4);
            } else {
                remoteViews3.setViewVisibility(i3 == 0 ? R.id.contacts_widget_item1 : R.id.contacts_widget_item2, 0);
                Long l = this.dids.get(i4);
                boolean zIsUserDialog = DialogObject.isUserDialog(l.longValue());
                AccountInstance accountInstance = this.accountInstance;
                if (zIsUserDialog) {
                    user = accountInstance.getMessagesController().getUser(l);
                    if (UserObject.isUserSelf(user)) {
                        firstName = LocaleController.getString(R.string.SavedMessages);
                    } else if (UserObject.isReplyUser(user)) {
                        firstName = LocaleController.getString(R.string.RepliesTitle);
                    } else if (UserObject.isDeleted(user)) {
                        firstName = LocaleController.getString(R.string.HiddenName);
                    } else {
                        firstName = UserObject.getFirstName(user);
                    }
                    if (UserObject.isReplyUser(user) || UserObject.isUserSelf(user) || user == null || (userProfilePhoto = user.photo) == null || (fileLocation = userProfilePhoto.photo_small) == null || fileLocation.volume_id == 0 || fileLocation.local_id == 0) {
                        str2 = firstName;
                        chat = null;
                        fileLocation = null;
                    } else {
                        str2 = firstName;
                        chat = null;
                    }
                } else {
                    TLRPC.Chat chat2 = accountInstance.getMessagesController().getChat(Long.valueOf(-l.longValue()));
                    if (chat2 != null) {
                        str = chat2.title;
                        TLRPC.ChatPhoto chatPhoto = chat2.photo;
                        if (chatPhoto != null && (fileLocation = chatPhoto.photo_small) != null && fileLocation.volume_id != 0 && fileLocation.local_id != 0) {
                            str2 = str;
                            chat = chat2;
                            user = null;
                        }
                    } else {
                        str = _UrlKt.FRAGMENT_ENCODE_SET;
                    }
                    str2 = str;
                    fileLocation = null;
                    chat = chat2;
                    user = null;
                }
                remoteViews3.setTextViewText(i3 == 0 ? R.id.contacts_widget_item_text1 : R.id.contacts_widget_item_text2, str2);
                if (fileLocation != null) {
                    try {
                        bitmapDecodeFile = BitmapFactory.decodeFile(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(fileLocation, true).toString());
                    } catch (Throwable th) {
                        FileLog.e(th);
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
                    avatarDrawable.setBounds(0, 0, iDp, iDp);
                    avatarDrawable.setRoundRadius(ExteraConfig.getAvatarCorners(iDp, true, chat != null && chat.forum));
                    avatarDrawable.draw(canvas);
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
                    this.bitmapRect.set(0.0f, 0.0f, bitmapDecodeFile.getWidth(), bitmapDecodeFile.getHeight());
                    canvas.drawRoundRect(this.bitmapRect, avatarCorners, avatarCorners, this.roundPaint);
                    canvas.restore();
                }
                canvas.setBitmap(null);
                remoteViews3.setImageViewBitmap(i3 == 0 ? R.id.contacts_widget_item_avatar1 : R.id.contacts_widget_item_avatar2, bitmapCreateBitmap);
                TLRPC.Dialog dialog = this.dialogs.get(l.longValue());
                if (dialog != null && (i2 = dialog.unread_count) > 0) {
                    if (i2 > 99) {
                        str3 = String.format("%d+", 99);
                    } else {
                        str3 = String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(i2));
                    }
                    remoteViews3.setTextViewText(i3 == 0 ? R.id.contacts_widget_item_badge1 : R.id.contacts_widget_item_badge2, str3);
                    remoteViews3.setViewVisibility(i3 == 0 ? R.id.contacts_widget_item_badge_bg1 : R.id.contacts_widget_item_badge_bg2, 0);
                } else {
                    remoteViews3.setViewVisibility(i3 == 0 ? R.id.contacts_widget_item_badge_bg1 : R.id.contacts_widget_item_badge_bg2, 8);
                }
                Bundle bundle2 = new Bundle();
                if (DialogObject.isUserDialog(l.longValue())) {
                    bundle2.putLong("userId", l.longValue());
                } else {
                    bundle2.putLong("chatId", -l.longValue());
                }
                bundle2.putInt("currentAccount", this.accountInstance.getCurrentAccount());
                Intent intent2 = new Intent();
                intent2.putExtras(bundle2);
                remoteViews3.setOnClickFillInIntent(i3 == 0 ? R.id.contacts_widget_item1 : R.id.contacts_widget_item2, intent2);
            }
            i3++;
        }
        return remoteViews3;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public void onDataSetChanged() {
        this.dids.clear();
        AccountInstance accountInstance = this.accountInstance;
        if (accountInstance == null || !accountInstance.getUserConfig().isClientActivated()) {
            return;
        }
        ArrayList<TLRPC.User> arrayList = new ArrayList<>();
        ArrayList<TLRPC.Chat> arrayList2 = new ArrayList<>();
        this.accountInstance.getMessagesStorage().getWidgetDialogs(this.appWidgetId, 1, this.dids, this.dialogs, new LongSparseArray<>(), arrayList, arrayList2);
        this.accountInstance.getMessagesController().putUsers(arrayList, true);
        this.accountInstance.getMessagesController().putChats(arrayList2, true);
    }
}
