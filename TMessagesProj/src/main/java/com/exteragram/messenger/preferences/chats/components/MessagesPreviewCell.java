package com.exteragram.messenger.preferences.chats.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.components.CustomPreferenceCell;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;

@SuppressLint({"ViewConstructor"})
public class MessagesPreviewCell extends LinearLayout implements CustomPreferenceCell {
    private Drawable backgroundDrawable;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    private final Runnable cancelProgress;
    private ChatMessageCell[] cells;
    private final MessageObject[] messageObjects;
    private final Drawable monetBackgroundDrawable;
    private Drawable oldBackgroundDrawable;
    private BackgroundGradientDrawable.Disposable oldBackgroundGradientDisposable;
    private final INavigationLayout parentLayout;
    private int progress;
    private final Drawable shadowDrawable;
    private final int type;

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSetPressed(boolean z) {
    }

    public /* synthetic */ void lambda$new$0() {
        this.progress = -1;
        for (ChatMessageCell chatMessageCell : this.cells) {
            if (chatMessageCell != null) {
                chatMessageCell.invalidate();
            }
        }
    }

    public MessagesPreviewCell(Context context, INavigationLayout iNavigationLayout) {
        this(context, iNavigationLayout, 0);
    }

    public MessagesPreviewCell(Context context, INavigationLayout iNavigationLayout, final int i) {
        super(context);
        this.progress = -1;
        this.cancelProgress = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                MessagesPreviewCell.this.lambda$new$0();
            }
        };
        this.parentLayout = iNavigationLayout;
        this.type = i;
        int i2 = i == 1 ? 1 : 2;
        this.cells = new ChatMessageCell[i2];
        MessageObject[] messageObjectArr = new MessageObject[i2];
        this.messageObjects = messageObjectArr;
        setWillNotDraw(false);
        setOrientation(1);
        setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
        this.monetBackgroundDrawable = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
        this.shadowDrawable = Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        int iCurrentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        int i3 = iCurrentTimeMillis - 3600;
        if (i == 0) {
            int i4 = iCurrentTimeMillis - 3590;
            tL_message.date = i4;
            tL_message.dialog_id = 1L;
            tL_message.flags = 0x101;

            TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
            tL_message.from_id = tL_peerUser;
            tL_peerUser.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            tL_message.id = 1;
            TLRPC.TL_messageMediaDocument tL_messageMediaDocument = new TLRPC.TL_messageMediaDocument();
            tL_message.media = tL_messageMediaDocument;
            tL_messageMediaDocument.flags = 1;
            tL_messageMediaDocument.document = new TLRPC.TL_document();
            TLRPC.Document document = tL_message.media.document;
            document.mime_type = "image/webp";
            document.file_reference = new byte[0];
            document.access_hash = 0L;
            document.date = i3;
            TLRPC.TL_documentAttributeSticker tL_documentAttributeSticker = new TLRPC.TL_documentAttributeSticker();
            tL_documentAttributeSticker.alt = "🐈\u200d⬛";
            tL_message.media.document.attributes.add(tL_documentAttributeSticker);
            TLRPC.TL_documentAttributeImageSize tL_documentAttributeImageSize = new TLRPC.TL_documentAttributeImageSize();
            tL_documentAttributeImageSize.h = 512;
            tL_documentAttributeImageSize.w = 512;
            tL_message.media.document.attributes.add(tL_documentAttributeImageSize);
            tL_message.message = _UrlKt.FRAGMENT_ENCODE_SET;
            tL_message.out = true;
            TLRPC.TL_peerUser tL_peerUser2 = new TLRPC.TL_peerUser();
            tL_message.peer_id = tL_peerUser2;
            tL_peerUser2.user_id = 0L;
            MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, tL_message, true, false);
            messageObjectArr[0] = messageObject;
            messageObject.useCustomPhoto = true;
            TLRPC.TL_message tL_message2 = new TLRPC.TL_message();
            tL_message2.message = LocaleController.getString(R.string.StickerSizeDialogMessageReplyTo);
            tL_message2.date = i4;
            tL_message2.dialog_id = -1L;
            tL_message2.flags = 0x103;

            tL_message2.id = 2;
            tL_message2.media = new TLRPC.TL_messageMediaEmpty();
            tL_message2.out = false;
            TLRPC.TL_peerUser tL_peerUser3 = new TLRPC.TL_peerUser();
            tL_message2.peer_id = tL_peerUser3;
            tL_peerUser3.user_id = 1L;
            MessageObject messageObject2 = messageObjectArr[0];
            messageObject2.customReplyName = "immat0x1";
            messageObject2.replyMessageObject = new MessageObject(UserConfig.selectedAccount, tL_message2, true, false);
            TLRPC.TL_message tL_message3 = new TLRPC.TL_message();
            tL_message3.message = LocaleController.getString(R.string.StickerSizeDialogMessage);
            tL_message3.date = iCurrentTimeMillis - 3480;
            tL_message3.dialog_id = -1L;
            tL_message3.flags = 0x109;

            tL_message3.id = 2;
            tL_message3.media = new TLRPC.TL_messageMediaEmpty();
            tL_message3.out = false;
            TLRPC.TL_peerUser tL_peerUser4 = new TLRPC.TL_peerUser();
            tL_message3.peer_id = tL_peerUser4;
            tL_peerUser4.user_id = 1L;
            tL_message3.from_id = new TLRPC.TL_peerUser();
            TLRPC.TL_messageReplyHeader tL_messageReplyHeader = new TLRPC.TL_messageReplyHeader();
            tL_message3.reply_to = tL_messageReplyHeader;
            tL_messageReplyHeader.flags |= 16;
            tL_messageReplyHeader.reply_to_msg_id = 5;
            MessageObject messageObject3 = new MessageObject(UserConfig.selectedAccount, tL_message3, true, false);
            messageObjectArr[1] = messageObject3;
            messageObject3.customReplyName = "8055";
            messageObject3.replyMessageObject = messageObjectArr[0];
        } else if (i == 1) {
            TLRPC.TL_message tL_message4 = new TLRPC.TL_message();
            String string = LocaleController.getString(R.string.MessagePreviewDialogMessage);
            tL_message4.message = string;
            int iIndexOf = string.indexOf("🤔");
            if (iIndexOf >= 0) {
                TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji = new TLRPC.TL_messageEntityCustomEmoji();
                tL_messageEntityCustomEmoji.offset = iIndexOf;
                tL_messageEntityCustomEmoji.length = 2;
                tL_messageEntityCustomEmoji.document_id = 5330328892811018097L;
                tL_message4.entities.add(tL_messageEntityCustomEmoji);
            }
            tL_message4.date = iCurrentTimeMillis - 3540;
            tL_message4.dialog_id = 1L;
            tL_message4.flags = 33027;
            tL_message4.edit_date = iCurrentTimeMillis - 3480;
            TLRPC.TL_peerUser tL_peerUser5 = new TLRPC.TL_peerUser();
            tL_message4.from_id = tL_peerUser5;
            tL_peerUser5.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
            tL_message4.id = 1;
            tL_message4.out = false;
            TLRPC.TL_peerUser tL_peerUser6 = new TLRPC.TL_peerUser();
            tL_message4.peer_id = tL_peerUser6;
            tL_peerUser6.user_id = 0L;
            MessageObject messageObject4 = new MessageObject(UserConfig.selectedAccount, tL_message4, true, false);
            messageObjectArr[0] = messageObject4;
            messageObject4.forceAvatar = true;
            messageObject4.resetLayout();
            messageObjectArr[0].eventId = 1L;
        }
        int i5 = 0;
        while (true) {
            ChatMessageCell[] chatMessageCellArr = this.cells;
            if (i5 >= chatMessageCellArr.length) {
                return;
            }
            chatMessageCellArr[i5] = new ChatMessageCell(context, UserConfig.selectedAccount) { 
                @Override // org.telegram.ui.Cells.ChatMessageCell
                public boolean isUserOnline(TLRPC.User user) {
                    if (i == 1) {
                        return true;
                    }
                    return super.isUserOnline(user);
                }

                @Override // android.view.ViewGroup, android.view.View
                public void dispatchDraw(Canvas canvas) {
                    if (getAvatarImage() != null && getAvatarImage().getImageHeight() != 0.0f) {
                        getAvatarImage().setImageCoords(getAvatarImage().getImageX(), (getMeasuredHeight() - getAvatarImage().getImageHeight()) - AndroidUtilities.dp(4.0f), getAvatarImage().getImageWidth(), getAvatarImage().getImageHeight());
                        getAvatarImage().setRoundRadius(ExteraConfig.getAvatarCorners(getAvatarImage().getImageHeight(), true));
                        drawAvatarWithOnlineStatus(canvas, getAvatarImage());
                    }
                    super.dispatchDraw(canvas);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell
                public boolean checkNeedDrawShareButton(MessageObject messageObject5) {
                    if (messageObject5 == null || messageObject5.isOutOwner() || i != 1) {
                        return super.checkNeedDrawShareButton(messageObject5);
                    }
                    return true;
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell
                public boolean shouldHideShareButton(MessageObject messageObject5, boolean z) {
                    if (messageObject5 != null && !messageObject5.isOutOwner() && i == 1) {
                        return ExteraConfig.getHideShareButton();
                    }
                    return super.shouldHideShareButton(messageObject5, z);
                }
            };
            this.cells[i5].setDelegate(new ChatMessageCell.ChatMessageCellDelegate() { 
                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public boolean canPerformActions() {
                    return true;
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public void didPressReplyMessage(ChatMessageCell chatMessageCell, int i6, float f, float f2, boolean z) {
                    MessagesPreviewCell.this.progress = 0;
                    chatMessageCell.invalidate();
                    AndroidUtilities.cancelRunOnUIThread(MessagesPreviewCell.this.cancelProgress);
                    AndroidUtilities.runOnUIThread(MessagesPreviewCell.this.cancelProgress, 5000L);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public boolean isProgressLoading(ChatMessageCell chatMessageCell, int i6) {
                    return i6 == MessagesPreviewCell.this.progress;
                }
            });
            ChatMessageCell chatMessageCell = this.cells[i5];
            chatMessageCell.isChat = false;
            chatMessageCell.setFullyDraw(true);
            this.cells[i5].setMessageObject(this.messageObjects[i5], null, false, false, false);
            addView(this.cells[i5], LayoutHelper.createLinear(-1, -2));
            i5++;
        }
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        for (ChatMessageCell chatMessageCell : this.cells) {
            if (chatMessageCell != null) {
                chatMessageCell.invalidate();
            }
        }
    }

    public void refreshMessages() {
        int i = 0;
        while (true) {
            ChatMessageCell[] chatMessageCellArr = this.cells;
            if (i >= chatMessageCellArr.length) {
                return;
            }
            MessageObject messageObject = this.messageObjects[i];
            if (messageObject != null) {
                messageObject.forceUpdate = true;
            }
            chatMessageCellArr[i].setMessageObject(messageObject, null, false, false, false);
            this.cells[i].invalidate();
            i++;
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onDraw(Canvas canvas) {
        Drawable cachedWallpaperNonBlocking = Theme.isCurrentThemeMonet() ? this.monetBackgroundDrawable : Theme.getCachedWallpaperNonBlocking();
        if (cachedWallpaperNonBlocking != this.backgroundDrawable && cachedWallpaperNonBlocking != null) {
            if (Theme.isAnimatingColor()) {
                this.oldBackgroundDrawable = this.backgroundDrawable;
                this.oldBackgroundGradientDisposable = this.backgroundGradientDisposable;
            } else {
                BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
            }
            this.backgroundDrawable = cachedWallpaperNonBlocking;
        }
        float themeAnimationValue = this.parentLayout.getThemeAnimationValue();
        int i = 0;
        while (i < 2) {
            Drawable drawable = i == 0 ? this.oldBackgroundDrawable : this.backgroundDrawable;
            int i2 = drawable == this.monetBackgroundDrawable ? 150 : 255;
            if (drawable != null) {
                if (i == 1 && this.oldBackgroundDrawable != null) {
                    drawable.setAlpha((int) (i2 * themeAnimationValue));
                } else {
                    drawable.setAlpha(i2);
                }
                if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
                    drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    if (drawable instanceof BackgroundGradientDrawable) {
                        this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable).drawExactBoundsSize(canvas, this);
                    } else {
                        drawable.draw(canvas);
                    }
                } else if (drawable instanceof BitmapDrawable) {
                    if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                        canvas.save();
                        float f = 2.0f / AndroidUtilities.density;
                        canvas.scale(f, f);
                        drawable.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / f), (int) Math.ceil(getMeasuredHeight() / f));
                    } else {
                        int measuredHeight = getMeasuredHeight();
                        float fMax = Math.max(getMeasuredWidth() / drawable.getIntrinsicWidth(), measuredHeight / drawable.getIntrinsicHeight());
                        int iCeil = (int) Math.ceil(drawable.getIntrinsicWidth() * fMax);
                        int iCeil2 = (int) Math.ceil(drawable.getIntrinsicHeight() * fMax);
                        int measuredWidth = (getMeasuredWidth() - iCeil) / 2;
                        int i3 = (measuredHeight - iCeil2) / 2;
                        canvas.save();
                        canvas.clipRect(0, 0, iCeil, getMeasuredHeight());
                        drawable.setBounds(measuredWidth, i3, iCeil + measuredWidth, iCeil2 + i3);
                    }
                    drawable.draw(canvas);
                    canvas.restore();
                }
                if (i == 0 && this.oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                    BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
                    if (disposable2 != null) {
                        disposable2.dispose();
                        this.oldBackgroundGradientDisposable = null;
                    }
                    this.oldBackgroundDrawable = null;
                    invalidate();
                }
            }
            i++;
        }
        this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.shadowDrawable.draw(canvas);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            this.backgroundGradientDisposable = null;
        }
        BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
        if (disposable2 != null) {
            disposable2.dispose();
            this.oldBackgroundGradientDisposable = null;
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.type == 1) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.type == 1) {
            return false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.type == 1) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override 
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MessagesPreviewCell) {
            MessagesPreviewCell messagesPreviewCell = (MessagesPreviewCell) obj;
            if (this.type == messagesPreviewCell.type && this.progress == messagesPreviewCell.progress) {
                return true;
            }
        }
        return false;
    }
}
