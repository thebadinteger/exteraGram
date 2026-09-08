package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import com.exteragram.messenger.AvatarCornerType;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.badges.BadgesController;
import com.google.android.material.timepicker.TimeModel;
import java.util.Locale;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.DrawableUtils;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CanvasButton;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.PhotoBubbleClip;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.Text;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.Stories.StoriesUtilities;

public class ProfileSearchCell extends BaseCell implements NotificationCenter.NotificationCenterDelegate, Theme.Colorable {
    CanvasButton actionButton;
    private StaticLayout actionLayout;
    private int actionLeft;
    private TLRPC.TL_sponsoredPeer ad;
    private Paint adBackgroundPaint;
    private final ButtonBounce adBounce;
    private final RectF adBounds;
    private Text adText;
    private boolean allowBotOpenButton;
    private boolean allowEmojiStatus;
    private AvatarDrawable avatarDrawable;
    public ImageReceiver avatarImage;
    public StoriesUtilities.AvatarStoryParams avatarStoryParams;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable botVerificationDrawable;
    private PhotoBubbleClip bubbleClip;
    private boolean callCellStyle;
    private TLRPC.Chat chat;
    CheckBox2 checkBox;
    private ContactsController.Contact contact;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop;
    private int countWidth;
    private int currentAccount;
    private CharSequence currentName;
    private boolean customPaints;
    private long dialog_id;
    public boolean dontDrawAvatar;
    private boolean drawCheck;
    private boolean drawCount;
    private boolean drawNameLock;
    private boolean drawPremium;
    private TLRPC.EncryptedChat encryptedChat;
    private boolean[] isOnline;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private int lastUnreadCount;
    private Drawable lockDrawable;
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private TextPaint namePaint;
    private int nameTop;
    private int nameWidth;
    private Utilities.Callback<TLRPC.User> onOpenButtonClick;
    private Utilities.Callback2<ProfileSearchCell, TLRPC.TL_sponsoredPeer> onSponsoredOptionsClick;
    private boolean openBot;
    private final Paint openButtonBackgroundPaint;
    private final ButtonBounce openButtonBounce;
    private final RectF openButtonRect;
    private Text openButtonText;
    private boolean premiumBlocked;
    private final AnimatedFloat premiumBlockedT;
    private PremiumGradient.PremiumGradientTools premiumGradient;
    private RectF rect;
    private boolean rectangularAvatar;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean savedMessages;
    private boolean showPremiumBlocked;
    private final AnimatedFloat starsBlockedT;
    private long starsPriceBlocked;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable statusDrawable;
    private StaticLayout statusLayout;
    private int statusLeft;
    private TextPaint statusPaint;
    private CharSequence subLabel;
    private int sublabelOffsetX;
    private int sublabelOffsetY;
    public boolean useSeparator;
    private TLRPC.User user;

    @Override // org.telegram.ui.ActionBar.Theme.Colorable
    public void lambda$buildLayout$0() {
        if (getParent() instanceof RecyclerListView) {
            RecyclerListView recyclerListView = (RecyclerListView) getParent();
            recyclerListView.getOnItemClickListener().onItemClick(this, recyclerListView.getChildAdapterPosition(this));
        } else {
            callOnClick();
        }
    }

    public void updateStatus(boolean z, BadgeDTO badgeDTO, TLRPC.User user, TLRPC.Chat chat, boolean z2) {
        long botVerificationIcon;
        this.statusDrawable.center = LocaleController.isRTL;
        if (this.allowEmojiStatus && user != null && !this.savedMessages && DialogObject.getEmojiStatusDocumentId(user.emoji_status) != 0) {
            this.statusDrawable.set(DialogObject.getEmojiStatusDocumentId(user.emoji_status), z2);
            this.statusDrawable.setColor(Integer.valueOf(Theme.getColor(Theme.key_chats_verifiedBackground, this.resourcesProvider)));
        } else if (this.allowEmojiStatus && chat != null && !this.savedMessages && DialogObject.getEmojiStatusDocumentId(chat.emoji_status) != 0) {
            this.statusDrawable.set(DialogObject.getEmojiStatusDocumentId(chat.emoji_status), z2);
            this.statusDrawable.setColor(Integer.valueOf(Theme.getColor(Theme.key_chats_verifiedBackground, this.resourcesProvider)));
        } else {
            boolean z3 = this.allowEmojiStatus;
            if (z3 && z) {
                this.statusDrawable.set(new CombinedDrawable(Theme.dialogs_verifiedDrawable, Theme.dialogs_verifiedCheckDrawable, 0, 0), z2);
                this.statusDrawable.setColor(null);
            } else if (badgeDTO != null && !this.savedMessages) {
                this.statusDrawable.set(badgeDTO.getDocumentId(), z2);
                this.statusDrawable.setParticles(true, false);
                this.statusDrawable.setColor(Integer.valueOf(Theme.getColor(Theme.key_chats_verifiedBackground, this.resourcesProvider)));
            } else if (z3 && user != null && !this.savedMessages && MessagesController.getInstance(this.currentAccount).isPremiumUser(user)) {
                this.statusDrawable.set(PremiumGradient.getInstance().premiumStarDrawableMini, z2);
                this.statusDrawable.setColor(Integer.valueOf(Theme.getColor(Theme.key_chats_verifiedBackground, this.resourcesProvider)));
            } else {
                this.statusDrawable.set((Drawable) null, z2);
                this.statusDrawable.setColor(Integer.valueOf(Theme.getColor(Theme.key_chats_verifiedBackground, this.resourcesProvider)));
            }
        }
        if (badgeDTO == null || this.savedMessages) {
            this.statusDrawable.setParticles(false, false);
        }
        if (user != null) {
            botVerificationIcon = DialogObject.getBotVerificationIcon(user);
        } else {
            botVerificationIcon = chat != null ? DialogObject.getBotVerificationIcon(chat) : 0L;
        }
        if (botVerificationIcon == 0 || this.savedMessages) {
            this.botVerificationDrawable.set((Drawable) null, z2);
        } else {
            this.botVerificationDrawable.set(botVerificationIcon, z2);
        }
        this.botVerificationDrawable.setColor(Integer.valueOf(Theme.getColor(Theme.key_chats_verifiedBackground, this.resourcesProvider)));
    }

    public void setRectangularAvatar(boolean z) {
        this.rectangularAvatar = z;
    }

    /* JADX WARN: Code duplicated, block: B:15:0x0050 A[PHI: r3
  0x0050: PHI (r3v17 org.telegram.tgnet.TLRPC$FileLocation) = (r3v0 org.telegram.tgnet.TLRPC$FileLocation), (r3v19 org.telegram.tgnet.TLRPC$FileLocation) binds: [B:11:0x0046, B:13:0x004c] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:25:0x007e A[PHI: r3
  0x007e: PHI (r3v1 org.telegram.tgnet.TLRPC$FileLocation) = (r3v0 org.telegram.tgnet.TLRPC$FileLocation), (r3v3 org.telegram.tgnet.TLRPC$FileLocation) binds: [B:21:0x0074, B:23:0x007a] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:84:0x016b  */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public void update(int i) {
        Drawable drawable;
        int avatarCorners;
        String monoForumTitle;
        boolean z;
        TLRPC.Dialog dialog;
        String monoForumTitle2;
        TLRPC.User user;
        TLRPC.User user2;
        TLRPC.FileLocation fileLocation;
        Drawable drawable2;
        TLRPC.User user3 = this.user;
        TLRPC.FileLocation fileLocation2 = null;
        if (user3 != null) {
            this.avatarDrawable.setInfo(this.currentAccount, user3);
            if (UserObject.isReplyUser(this.user)) {
                this.avatarDrawable.setAvatarType(12);
                this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
            } else {
                boolean z2 = this.savedMessages;
                AvatarDrawable avatarDrawable = this.avatarDrawable;
                if (z2) {
                    avatarDrawable.setAvatarType(1);
                    this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
                } else {
                    TLRPC.User user4 = this.user;
                    TLRPC.UserProfilePhoto userProfilePhoto = user4.photo;
                    if (userProfilePhoto != null) {
                        fileLocation2 = userProfilePhoto.photo_small;
                        BitmapDrawable bitmapDrawable = userProfilePhoto.strippedBitmap;
                        if (bitmapDrawable != null) {
                            drawable2 = bitmapDrawable;
                        } else {
                            drawable2 = avatarDrawable;
                        }
                    } else {
                        drawable2 = avatarDrawable;
                    }
                    this.avatarImage.setImage(ImageLocation.getForUserOrChat(this.currentAccount, user4, 1), "50_50", ImageLocation.getForUserOrChat(this.user, 2), "50_50", drawable2, this.user, 0);
                }
            }
        } else {
            TLRPC.Chat chat = this.chat;
            if (chat != null) {
                AvatarDrawable avatarDrawable2 = this.avatarDrawable;
                TLRPC.ChatPhoto chatPhoto = chat.photo;
                if (chatPhoto != null) {
                    fileLocation2 = chatPhoto.photo_small;
                    BitmapDrawable bitmapDrawable2 = chatPhoto.strippedBitmap;
                    if (bitmapDrawable2 != null) {
                        drawable = bitmapDrawable2;
                    } else {
                        drawable = avatarDrawable2;
                    }
                } else {
                    drawable = avatarDrawable2;
                }
                boolean z3 = chat.monoforum;
                int i2 = this.currentAccount;
                if (z3) {
                    ForumUtilities.setMonoForumAvatar(i2, chat, avatarDrawable2, this.avatarImage);
                } else {
                    avatarDrawable2.setInfo(i2, chat);
                    this.avatarImage.setImage(ImageLocation.getForUserOrChat(this.currentAccount, this.chat, 1), "50_50", ImageLocation.getForUserOrChat(this.chat, 2), "50_50", drawable, this.chat, 0);
                }
            } else {
                ContactsController.Contact contact = this.contact;
                AvatarDrawable avatarDrawable3 = this.avatarDrawable;
                if (contact != null) {
                    avatarDrawable3.setInfo(0L, contact.first_name, contact.last_name);
                    this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
                } else {
                    avatarDrawable3.setInfo(0L, null, null);
                    this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
                }
            }
        }
        TLRPC.FileLocation fileLocation3 = fileLocation2;
        boolean zHasStories = MessagesController.getInstance(this.currentAccount).getStoriesController().hasStories(this.dialog_id);
        ImageReceiver imageReceiver = this.avatarImage;
        if (ChatObject.isCommunity(this.chat)) {
            avatarCorners = ExteraConfig.getAvatarCorners(46.0f, false, AvatarCornerType.COMMUNITY, zHasStories);
        } else {
            TLRPC.Chat chat2 = this.chat;
            if (chat2 == null || !chat2.monoforum) {
                avatarCorners = ExteraConfig.getAvatarCorners(46.0f, false, (chat2 != null && chat2.forum) || this.rectangularAvatar, zHasStories);
            } else {
                avatarCorners = 0;
            }
        }
        imageReceiver.setRoundRadius(avatarCorners);
        if (i != 0) {
            boolean z4 = !(((i & MessagesController.UPDATE_MASK_AVATAR) == 0 || this.user == null) && ((i & MessagesController.UPDATE_MASK_CHAT_AVATAR) == 0 || this.chat == null)) && (((fileLocation = this.lastAvatar) != null && fileLocation3 == null) || ((fileLocation == null && fileLocation3 != null) || !(fileLocation == null || (fileLocation.volume_id == fileLocation3.volume_id && fileLocation.local_id == fileLocation3.local_id))));
            if (z4 || (i & MessagesController.UPDATE_MASK_STATUS) == 0 || (user2 = this.user) == null) {
                z = z4;
            } else {
                TLRPC.UserStatus userStatus = user2.status;
                if ((userStatus != null ? userStatus.expires : 0) != this.lastStatus) {
                    z = true;
                } else {
                    z = z4;
                }
            }
            if (!z && (i & MessagesController.UPDATE_MASK_EMOJI_STATUS) != 0 && ((user = this.user) != null || this.chat != null)) {
                boolean z5 = user != null ? user.verified : this.chat.verified;
                BadgesController badgesController = BadgesController.INSTANCE;
                TLObject tLObject = user;
                if (user == null) {
                    tLObject = this.chat;
                }
                updateStatus(z5, badgesController.getBadge(tLObject), this.user, this.chat, true);
            }
            if ((!z && (i & MessagesController.UPDATE_MASK_NAME) != 0 && this.user != null) || ((i & MessagesController.UPDATE_MASK_CHAT_NAME) != 0 && this.chat != null)) {
                if (this.user != null) {
                    monoForumTitle2 = this.user.first_name + this.user.last_name;
                } else {
                    TLRPC.Chat chat3 = this.chat;
                    if (chat3.monoforum) {
                        monoForumTitle2 = ForumUtilities.getMonoForumTitle(this.currentAccount, chat3);
                    } else {
                        monoForumTitle2 = chat3.title;
                    }
                }
                if (!monoForumTitle2.equals(this.lastName)) {
                    z = true;
                }
            }
            if (!((z || !this.drawCount || (i & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) == 0 || (dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id)) == null || MessagesController.getInstance(this.currentAccount).getDialogUnreadCount(dialog) == this.lastUnreadCount) ? z : true)) {
                return;
            }
        }
        TLRPC.User user5 = this.user;
        if (user5 != null) {
            TLRPC.UserStatus userStatus2 = user5.status;
            if (userStatus2 != null) {
                this.lastStatus = userStatus2.expires;
            } else {
                this.lastStatus = 0;
            }
            this.lastName = this.user.first_name + this.user.last_name;
        } else {
            TLRPC.Chat chat4 = this.chat;
            if (chat4 != null) {
                if (chat4.monoforum) {
                    monoForumTitle = ForumUtilities.getMonoForumTitle(this.currentAccount, chat4);
                } else {
                    monoForumTitle = chat4.title;
                }
                this.lastName = monoForumTitle;
            }
        }
        this.lastAvatar = fileLocation3;
        if (getMeasuredWidth() != 0 || getMeasuredHeight() != 0) {
            buildLayout();
        } else {
            requestLayout();
        }
        postInvalidate();
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Canvas canvas2;
        int width;
        int iCeil;
        int lineRight;
        Theme.ResourcesProvider resourcesProvider;
        if (this.user == null && this.chat == null && this.encryptedChat == null && this.contact == null) {
            return;
        }
        if (this.useSeparator) {
            Paint paint = (!this.customPaints || (resourcesProvider = this.resourcesProvider) == null) ? null : resourcesProvider.getPaint("paintDivider");
            if (paint == null) {
                paint = Theme.dividerPaint;
            }
            Paint paint2 = paint;
            if (LocaleController.isRTL) {
                canvas2 = canvas;
                canvas2.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, paint2);
            } else {
                canvas2 = canvas;
                canvas2.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, paint2);
            }
        } else {
            canvas2 = canvas;
        }
        if (this.drawNameLock) {
            BaseCell.setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_lockDrawable.draw(canvas2);
        }
        StaticLayout staticLayout = this.nameLayout;
        if (staticLayout != null) {
            if (LocaleController.isRTL) {
                iCeil = (int) (this.nameLeft + staticLayout.getLineRight(0) + AndroidUtilities.dp(6.0f));
            } else if (staticLayout.getLineLeft(0) == 0.0f) {
                iCeil = (this.nameLeft - AndroidUtilities.dp(3.0f)) - this.botVerificationDrawable.getIntrinsicWidth();
            } else {
                iCeil = (int) (((((double) (this.nameLeft + this.nameWidth)) - Math.ceil(this.nameLayout.getLineWidth(0))) - ((double) AndroidUtilities.dp(3.0f))) - ((double) this.botVerificationDrawable.getIntrinsicWidth()));
            }
            BaseCell.setDrawableBounds(this.botVerificationDrawable, iCeil, this.nameTop + ((this.nameLayout.getHeight() - this.botVerificationDrawable.getIntrinsicHeight()) / 2.0f));
            this.botVerificationDrawable.draw(canvas2);
            canvas2.save();
            canvas2.translate(this.nameLeft, this.nameTop);
            this.nameLayout.draw(canvas2);
            canvas2.restore();
            if (!LocaleController.isRTL) {
                lineRight = (int) (this.nameLeft + this.nameLayout.getLineRight(0) + AndroidUtilities.dp(6.0f));
            } else if (this.nameLayout.getLineLeft(0) == 0.0f) {
                lineRight = (this.nameLeft - AndroidUtilities.dp(3.0f)) - this.statusDrawable.getIntrinsicWidth();
            } else {
                lineRight = (int) (((((double) (this.nameLeft + this.nameWidth)) - Math.ceil(this.nameLayout.getLineWidth(0))) - ((double) AndroidUtilities.dp(3.0f))) - ((double) this.statusDrawable.getIntrinsicWidth()));
            }
            BaseCell.setDrawableBounds(this.statusDrawable, lineRight, this.nameTop + ((this.nameLayout.getHeight() - this.statusDrawable.getIntrinsicHeight()) / 2.0f));
            this.statusDrawable.draw(canvas2);
        }
        if (this.ad != null && this.adText != null && this.adBackgroundPaint != null) {
            int color = Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider);
            this.adBackgroundPaint.setColor(Theme.multAlpha(color, 0.1f));
            int width2 = ((int) this.adText.getWidth()) + AndroidUtilities.dp(12.66f);
            int iDp = AndroidUtilities.dp(17.33f);
            if (LocaleController.isRTL) {
                width = AndroidUtilities.dp(12.0f);
            } else {
                width = (getWidth() - AndroidUtilities.dp(12.0f)) - width2;
            }
            RectF rectF = this.adBounds;
            float f = width;
            int i = this.nameTop;
            rectF.set(f, i, width + width2, i + iDp);
            this.adBounds.inset(-AndroidUtilities.dp(6.0f), -AndroidUtilities.dp(6.0f));
            canvas2.save();
            float scale = this.adBounce.getScale(0.1f);
            canvas2.scale(scale, scale, this.adBounds.centerX(), this.adBounds.centerY());
            canvas2.translate(f, this.nameTop);
            RectF rectF2 = AndroidUtilities.rectTmp;
            float f2 = iDp;
            rectF2.set(0.0f, 0.0f, width2, f2);
            float f3 = f2 / 2.0f;
            canvas2.drawRoundRect(rectF2, f3, f3, this.adBackgroundPaint);
            this.adText.draw(canvas2, AndroidUtilities.dp(6.33f), f3, color, 1.0f);
            canvas2.restore();
        }
        if (this.statusLayout != null) {
            canvas2.save();
            canvas2.translate(this.statusLeft + this.sublabelOffsetX, AndroidUtilities.dp(this.callCellStyle ? 35.0f : 33.0f) + this.sublabelOffsetY);
            this.statusLayout.draw(canvas2);
            canvas2.restore();
        }
        if (this.countLayout != null) {
            int iDp2 = this.countLeft - AndroidUtilities.dp(5.5f);
            this.rect.set(iDp2, this.countTop, iDp2 + this.countWidth + AndroidUtilities.dp(11.0f), this.countTop + AndroidUtilities.dp(23.0f));
            RectF rectF3 = this.rect;
            float f4 = AndroidUtilities.density;
            canvas2.drawRoundRect(rectF3, f4 * 11.5f, f4 * 11.5f, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id, 0L) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
            canvas2.save();
            canvas2.translate(this.countLeft, this.countTop + AndroidUtilities.dp(4.0f));
            this.countLayout.draw(canvas2);
            canvas2.restore();
        }
        if (this.actionLayout != null) {
            this.actionButton.setColor(Theme.getColor(Theme.key_chats_unreadCounter), Theme.getColor(Theme.key_chats_unreadCounterText));
            RectF rectF4 = AndroidUtilities.rectTmp;
            int i2 = this.actionLeft;
            rectF4.set(i2, this.countTop, i2 + this.actionLayout.getWidth(), this.countTop + AndroidUtilities.dp(23.0f));
            rectF4.inset(-AndroidUtilities.dp(16.0f), -AndroidUtilities.dp(4.0f));
            this.actionButton.setRect(rectF4);
            this.actionButton.setRounded(true);
            this.actionButton.draw(canvas2);
            canvas2.save();
            canvas2.translate(this.actionLeft, this.countTop + AndroidUtilities.dp(4.0f));
            this.actionLayout.draw(canvas2);
            canvas2.restore();
        }
        if (!this.dontDrawAvatar) {
            TLRPC.Chat chat = this.chat;
            if (chat != null && chat.monoforum) {
                if (this.bubbleClip == null) {
                    this.bubbleClip = new PhotoBubbleClip();
                }
                this.bubbleClip.setBounds((int) this.avatarStoryParams.originalAvatarRect.centerX(), (int) this.avatarStoryParams.originalAvatarRect.centerY(), (int) (this.avatarStoryParams.originalAvatarRect.width() / 2.0f));
                canvas2.save();
                canvas2.clipPath(this.bubbleClip);
                this.avatarImage.setImageCoords(this.avatarStoryParams.originalAvatarRect);
                this.avatarImage.draw(canvas2);
                canvas2.restore();
            } else {
                TLRPC.User user = this.user;
                if (user != null) {
                    StoriesUtilities.drawAvatarWithStory(user.id, canvas2, this.avatarImage, this.avatarStoryParams);
                } else if (chat != null) {
                    if (ChatObject.isCommunity(chat)) {
                        DrawableUtils.drawCommunityCardDrawable(canvas2, Theme.dialogs_communityCardsDrawable, this.avatarStoryParams.originalAvatarRect.centerX(), this.avatarStoryParams.originalAvatarRect.centerY(), this.avatarStoryParams.originalAvatarRect.width());
                    }
                    StoriesUtilities.drawAvatarWithStory(-this.chat.id, canvas2, this.avatarImage, this.avatarStoryParams);
                } else {
                    this.avatarImage.setImageCoords(this.avatarStoryParams.originalAvatarRect);
                    this.avatarImage.draw(canvas2);
                }
            }
        }
        float f5 = this.premiumBlockedT.set(this.premiumBlocked);
        if (f5 > 0.0f) {
            float centerY = this.avatarImage.getCenterY() + AndroidUtilities.dp(14.0f);
            float centerX = this.avatarImage.getCenterX() + AndroidUtilities.dp(16.0f);
            canvas2.save();
            Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider));
            canvas2.drawCircle(centerX, centerY, AndroidUtilities.dp(11.33f) * f5, Theme.dialogs_onlineCirclePaint);
            if (this.premiumGradient == null) {
                this.premiumGradient = new PremiumGradient.PremiumGradientTools(Theme.key_premiumGradient1, Theme.key_premiumGradient2, -1, -1, -1, this.resourcesProvider);
            }
            this.premiumGradient.gradientMatrix((int) (centerX - AndroidUtilities.dp(10.0f)), (int) (centerY - AndroidUtilities.dp(10.0f)), (int) (AndroidUtilities.dp(10.0f) + centerX), (int) (AndroidUtilities.dp(10.0f) + centerY), 0.0f, 0.0f);
            canvas2.drawCircle(centerX, centerY, AndroidUtilities.dp(10.0f) * f5, this.premiumGradient.paint);
            if (this.lockDrawable == null) {
                Drawable drawableMutate = getContext().getResources().getDrawable(R.drawable.msg_mini_lock2).mutate();
                this.lockDrawable = drawableMutate;
                drawableMutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
            }
            Drawable drawable = this.lockDrawable;
            drawable.setBounds((int) (centerX - (((drawable.getIntrinsicWidth() / 2.0f) * 0.875f) * f5)), (int) (centerY - (((this.lockDrawable.getIntrinsicHeight() / 2.0f) * 0.875f) * f5)), (int) (centerX + ((this.lockDrawable.getIntrinsicWidth() / 2.0f) * 0.875f * f5)), (int) (centerY + ((this.lockDrawable.getIntrinsicHeight() / 2.0f) * 0.875f * f5)));
            this.lockDrawable.setAlpha((int) (f5 * 255.0f));
            this.lockDrawable.draw(canvas2);
            canvas2.restore();
        }
        if (!this.openBot || this.openButtonText == null) {
            return;
        }
        float fDp = AndroidUtilities.dp(28.0f) + this.openButtonText.getCurrentWidth();
        float fDp2 = LocaleController.isRTL ? AndroidUtilities.dp(15.0f) : (getWidth() - fDp) - AndroidUtilities.dp(15.0f);
        float fDp3 = AndroidUtilities.dp(28.0f);
        this.openButtonBackgroundPaint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        this.openButtonRect.set(fDp2, (getHeight() - fDp3) / 2.0f, fDp + fDp2, (getHeight() + fDp3) / 2.0f);
        canvas2.save();
        float scale2 = this.openButtonBounce.getScale(0.06f);
        canvas2.scale(scale2, scale2, this.openButtonRect.centerX(), this.openButtonRect.centerY());
        RectF rectF5 = this.openButtonRect;
        canvas2.drawRoundRect(rectF5, rectF5.height() / 2.0f, this.openButtonRect.height() / 2.0f, this.openButtonBackgroundPaint);
        this.openButtonText.draw(canvas2, fDp2 + AndroidUtilities.dp(14.0f), getHeight() / 2.0f, -1, 1.0f);
        canvas2.restore();
    }

    public boolean isBlocked() {
        return this.premiumBlocked;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        StringBuilder sb = new StringBuilder();
        StaticLayout staticLayout = this.nameLayout;
        if (staticLayout != null) {
            sb.append(staticLayout.getText());
        }
        if (this.drawCheck) {
            sb.append(", ");
            sb.append(LocaleController.getString(R.string.AccDescrVerified));
            sb.append("\n");
        }
        if (this.statusLayout != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(this.statusLayout.getText());
        }
        accessibilityNodeInfo.setText(sb.toString());
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(this.checkBox.isChecked());
            accessibilityNodeInfo.setClassName("android.widget.CheckBox");
        }
    }

    public long getDialogId() {
        return this.dialog_id;
    }

    public void setChecked(boolean z, boolean z2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 == null) {
            return;
        }
        checkBox2.setChecked(z, z2);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.openBot && this.onOpenButtonClick != null && this.user != null) {
            boolean zContains = this.openButtonRect.contains(motionEvent.getX(), motionEvent.getY());
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                this.openButtonBounce.setPressed(zContains);
            } else {
                if (motionEvent.getAction() == 1) {
                    if (this.openButtonBounce.isPressed()) {
                        this.onOpenButtonClick.run(this.user);
                    }
                    this.openButtonBounce.setPressed(false);
                    return true;
                }
                if (motionEvent.getAction() == 3) {
                    this.openButtonBounce.setPressed(false);
                    return true;
                }
            }
            if (zContains || this.openButtonBounce.isPressed()) {
                return true;
            }
        } else if (this.ad != null && this.onSponsoredOptionsClick != null) {
            boolean zContains2 = this.adBounds.contains(motionEvent.getX(), motionEvent.getY());
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                this.adBounce.setPressed(zContains2);
            } else {
                if (motionEvent.getAction() == 1) {
                    if (this.adBounce.isPressed()) {
                        this.onSponsoredOptionsClick.run(this, this.ad);
                    }
                    this.adBounce.setPressed(false);
                    return true;
                }
                if (motionEvent.getAction() == 3) {
                    this.adBounce.setPressed(false);
                    return true;
                }
            }
            if (zContains2 || this.adBounce.isPressed()) {
                return true;
            }
        }
        if (!(this.user == null && this.chat == null) && this.avatarStoryParams.checkOnTouchEvent(motionEvent, this)) {
            return true;
        }
        CanvasButton canvasButton = this.actionButton;
        if (canvasButton == null || !canvasButton.checkTouchEvent(motionEvent)) {
            return super.onTouchEvent(motionEvent);
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.Theme.Colorable
    public void updateColors() {
        if (this.nameLayout == null || getMeasuredWidth() <= 0) {
            return;
        }
        buildLayout();
    }
}
