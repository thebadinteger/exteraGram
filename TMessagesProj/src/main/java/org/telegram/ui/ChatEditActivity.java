package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.exoplayer2.util.Consumer;
import com.google.android.material.timepicker.TimeModel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_payments;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LoadingSpan;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.ChatCustomReactionsEditActivity;
import org.telegram.ui.Components.Reactions.ReactionsUtils;
import org.telegram.ui.Components.SectionsScrollView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Stars.BotStarsActivity;
import org.telegram.ui.Stars.BotStarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.bots.AffiliateProgramFragment;
import org.telegram.ui.bots.BotVerifySheet;
import org.telegram.ui.bots.ChannelAffiliateProgramsFragment;
import org.telegram.ui.community.CommunityCreateActivity;
import org.telegram.ui.community.CommunityEditActivity;
import org.telegram.ui.community.CommunitySheet;
import org.telegram.ui.community.cells.CommunityLinkView2;

public class ChatEditActivity extends BaseFragment implements ImageUpdater.ImageUpdaterDelegate, NotificationCenter.NotificationCenterDelegate {
    private TextCell adminCell;
    private TextCell autoTranslationCell;
    private TLRPC.ChatReactions availableReactions;
    private TLRPC.FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private LinearLayout avatarContainer;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private LinearLayout balanceContainer;
    private TextCell blockCell;
    private TL_stories.TL_premium_boostsStatus boostsStatus;
    private TextCell botAffiliateProgramCell;
    private TextInfoPrivacyCell botInfoCell;
    RLottieDrawable cameraDrawable;
    private boolean canForum;
    private TextCell changeBotSettingsCell;
    private TextCell channelAffiliateProgramsCell;
    private TLRPC.TL_chatAdminRights chatAdminRights;
    private TLRPC.TL_chatBannedRights chatBannedRights;
    private TLRPC.TL_chatBannedRights chatDefaultBannedRights;
    private long chatId;
    private PeerColorActivity.ChangeNameColorCell colorCell;
    private TextCell communityCell;
    private TextInfoPrivacyCell communityGapView;
    private TextInfoPrivacyCell communityInfoCell;
    private CommunityLinkView2 communityLinkView;
    private TextCell communityUnlinkCell;
    private boolean createAfterUpload;
    private TLRPC.Chat currentChat;
    private TLRPC.User currentUser;
    private TextSettingsCell deleteCell;
    private FrameLayout deleteContainer;
    private ShadowSectionCell deleteInfoCell;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private TextCell editCommandsCell;
    private TextCell editIntroCell;
    private boolean forum;
    private boolean forumTabs;
    private TextCell forumsCell;
    private boolean hasUploadedPhoto;
    private TextCell historyCell;
    private boolean historyHidden;
    private ImageUpdater imageUpdater;
    private TLRPC.ChatFull info;
    private LinearLayout infoContainer;
    private ShadowSectionCell infoSectionCell;
    private TextCell inviteLinksCell;
    private boolean isChannel;
    private LinearLayout linearLayout;
    private TextCell linkedCell;
    private TextCell locationCell;
    private TextCell logCell;
    private TextCell memberRequestsCell;
    private TextCell membersCell;
    private EditTextEmoji nameTextView;
    private final List<AnimatedEmojiDrawable> preloadedReactions;
    private AlertDialog progressDialog;
    private PhotoViewer.PhotoViewerProvider provider;
    private TextCell publicLinkCell;
    private TextCell reactionsCell;
    private int realAdminCount;
    private SectionsScrollView scrollView;
    private TextCell setAvatarCell;
    private LinearLayout settingsContainer;
    private TextInfoPrivacyCell settingsSectionCell;
    private ShadowSectionCell settingsTopSectionCell;
    private TextCell starsBalanceCell;
    private TextCell statsAndBoosts;
    private TextCell stickersCell;
    private FrameLayout stickersContainer;
    private TextInfoPrivacyCell stickersInfoCell;
    private TextCell suggestedCell;
    private TextCell tonBalanceCell;
    private TextCell typeCell;
    private LinearLayout typeEditContainer;
    private UndoView undoView;
    private ValueAnimator updateHistoryShowAnimator;
    private long userId;
    private TLRPC.UserFull userInfo;
    private TextCell verifyCell;
    private TextInfoPrivacyCell verifyInfoCell;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    public class AnonymousClass1 extends PhotoViewer.EmptyPhotoViewerProvider {
        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canLoadMoreAvatars() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getTotalImageCount() {
            return 1;
        }

        public AnonymousClass1() {
        }

        void lambda$createView$6(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2, long j) {
        TLRPC.TL_channelLocation tL_channelLocation = new TLRPC.TL_channelLocation();
        tL_channelLocation.address = messageMedia.address;
        tL_channelLocation.geo_point = messageMedia.geo;
        TLRPC.ChatFull chatFull = this.info;
        chatFull.location = tL_channelLocation;
        chatFull.flags |= 32768;
        updateFields(false, true);
        getMessagesController().loadFullChat(this.chatId, 0, true);
    }

    public void lambda$createView$10(long j) {
        updateSuggestedCell(Long.valueOf(j), false);
    }

    public void lambda$createView$26(View view, Boolean bool, Boolean bool2) {
        this.forum = bool.booleanValue();
        this.forumTabs = bool2.booleanValue();
        this.avatarImage.animateToRoundRadius(AndroidUtilities.dp(this.forum ? 16.0f : 32.0f));
        ((TextCell) view).setChecked(this.forum);
        updateFields(false, true);
        if (this.donePressed) {
            return;
        }
        TLRPC.Chat chat = this.currentChat;
        if (chat.forum == this.forum && chat.forum_tabs == this.forumTabs) {
            return;
        }
        if (!ChatObject.isChannel(chat) && this.forum) {
            Context context = getContext();
            if (context == null) {
                context = LaunchActivity.instance;
            }
            if (context == null) {
                context = ApplicationLoader.applicationContext;
            }
            if (context == null) {
                return;
            }
            final AlertDialog alertDialog = new AlertDialog(context, 3);
            this.donePressed = true;
            alertDialog.showDelayed(250L);
            getMessagesController().convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.LongCallback() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda63
                @Override 
                public final void run(long j) {
                    this.f$0.lambda$createView$25(alertDialog, j);
                }
            });
            return;
        }
        boolean z = this.currentChat.forum_tabs != this.forumTabs;
        getMessagesController().toggleChannelForum(this.chatId, this.forum, this.forumTabs);
        TLRPC.Chat chat2 = this.currentChat;
        chat2.forum = this.forum;
        chat2.forum_tabs = this.forumTabs;
        if (z) {
            updatePastFragmentsOnTabs();
        }
    }

    public void lambda$createView$46(TLRPC.Bool bool, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            BulletinFactory.of(this).showForError(tL_error);
        }
        AndroidUtilities.removeFromParent(this.communityGapView);
        AndroidUtilities.removeFromParent(this.communityLinkView);
        AndroidUtilities.removeFromParent(this.communityUnlinkCell);
    }

    public void lambda$createView$50(boolean z) {
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, Long.valueOf(-this.chatId));
        } else {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
        }
        finishFragment();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needDeleteDialog, Long.valueOf(-this.currentChat.id), null, this.currentChat, Boolean.valueOf(z));
    }

    public static CharSequence applyNewSpan(String str) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.append((CharSequence) "  d");
        FilterCreateActivity.NewSpan newSpan = new FilterCreateActivity.NewSpan(false, 10);
        newSpan.setTypeface(AndroidUtilities.getTypeface("fonts/num.otf"));
        newSpan.setColor(Theme.getColor(Theme.key_premiumGradient1));
        spannableStringBuilder.setSpan(newSpan, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
        return spannableStringBuilder;
    }

    private void updatePublicLinksCount() {
        if (this.publicLinkCell == null) {
            return;
        }
        if (this.currentUser.usernames.size() > 1) {
            ArrayList<TLRPC.TL_username> arrayList = this.currentUser.usernames;
            int size = arrayList.size();
            int i = 0;
            int i2 = 0;
            while (i2 < size) {
                TLRPC.TL_username tL_username = arrayList.get(i2);
                i2++;
                if (tL_username.active) {
                    i++;
                }
            }
            this.publicLinkCell.setTextAndValueAndIcon((CharSequence) LocaleController.getString(R.string.BotPublicLinks), (CharSequence) LocaleController.formatString(R.string.BotPublicLinksCount, Integer.valueOf(i), Integer.valueOf(this.currentUser.usernames.size())), R.drawable.msg_link2, true);
            return;
        }
        this.publicLinkCell.setTextAndValueAndIcon((CharSequence) LocaleController.getString(R.string.BotPublicLink), (CharSequence) ("t.me/" + this.currentUser.username), R.drawable.msg_link2, true);
    }

    public void openSetPhotoAlert() {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda49
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openSetPhotoAlert$54();
            }
        }, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda50
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                this.f$0.lambda$openSetPhotoAlert$55(dialogInterface);
            }
        }, 0);
        this.cameraDrawable.setCurrentFrame(0);
        this.cameraDrawable.setCustomEndFrame(43);
        this.setAvatarCell.imageView.playAnimation();
    }

    public void lambda$didUploadPhoto$58(TLRPC.PhotoSize photoSize, TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, TLRPC.VideoSize videoSize, TLRPC.PhotoSize photoSize2, double d, String str) {
        TLRPC.FileLocation fileLocation = photoSize.location;
        this.avatar = fileLocation;
        if (inputFile != null || inputFile2 != null || videoSize != null) {
            long j = 0;
            if (this.userId != 0) {
                TLRPC.User user = this.currentUser;
                if (user != null) {
                    user.photo = new TLRPC.TL_userProfilePhoto();
                    TLRPC.UserProfilePhoto userProfilePhoto = this.currentUser.photo;
                    if (inputFile != null) {
                        j = inputFile.id;
                    } else if (inputFile2 != null) {
                        j = inputFile2.id;
                    }
                    userProfilePhoto.photo_id = j;
                    userProfilePhoto.photo_big = photoSize2.location;
                    userProfilePhoto.photo_small = photoSize.location;
                    getMessagesController().putUser(this.currentUser, true);
                }
                TLRPC.TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TLRPC.TL_photos_uploadProfilePhoto();
                if (inputFile != null) {
                    tL_photos_uploadProfilePhoto.file = inputFile;
                    tL_photos_uploadProfilePhoto.flags |= 1;
                }
                if (inputFile2 != null) {
                    tL_photos_uploadProfilePhoto.video = inputFile2;
                    int i = tL_photos_uploadProfilePhoto.flags;
                    tL_photos_uploadProfilePhoto.video_start_ts = d;
                    tL_photos_uploadProfilePhoto.flags = i | 6;
                }
                if (videoSize != null) {
                    tL_photos_uploadProfilePhoto.video_emoji_markup = videoSize;
                    tL_photos_uploadProfilePhoto.flags |= 16;
                }
                tL_photos_uploadProfilePhoto.bot = getMessagesController().getInputUser(this.currentUser);
                tL_photos_uploadProfilePhoto.flags |= 32;
                getConnectionsManager().sendRequest(tL_photos_uploadProfilePhoto, new RequestDelegate() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda51
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$didUploadPhoto$57(tLObject, tL_error);
                    }
                });
            } else {
                getMessagesController().changeChatAvatar(this.chatId, null, inputFile, inputFile2, videoSize, d, str, photoSize.location, photoSize2.location, null);
            }
            if (this.createAfterUpload) {
                try {
                    AlertDialog alertDialog = this.progressDialog;
                    if (alertDialog != null && alertDialog.isShowing()) {
                        this.progressDialog.dismiss();
                        this.progressDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.donePressed = false;
                this.doneButton.performClick();
            }
            showAvatarProgress(false, true);
            return;
        }
        BackupImageView backupImageView = this.avatarImage;
        ImageLocation forLocal = ImageLocation.getForLocal(fileLocation);
        AvatarDrawable avatarDrawable = this.avatarDrawable;
        Object obj = this.currentUser;
        if (obj == null) {
            obj = this.currentChat;
        }
        backupImageView.setImage(forLocal, "50_50", avatarDrawable, obj);
        this.setAvatarCell.setTextAndIcon((CharSequence) LocaleController.getString("ChatSetNewPhoto", R.string.ChatSetNewPhoto), R.drawable.msg_addphoto, true);
        if (this.cameraDrawable == null) {
            this.cameraDrawable = new RLottieDrawable(R.raw.camera_outline, _UrlKt.FRAGMENT_ENCODE_SET + R.raw.camera_outline, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), false, null);
        }
        this.setAvatarCell.imageView.setTranslationX(-AndroidUtilities.dp(8.0f));
        this.setAvatarCell.imageView.setAnimation(this.cameraDrawable);
        showAvatarProgress(true, false);
    }

    public void lambda$processDone$64(TL_bots.setBotInfo setbotinfo, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.UserFull userFull = this.userInfo;
        if (userFull != null) {
            userFull.about = setbotinfo.about;
            getMessagesStorage().updateUserInfo(this.userInfo, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda65
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$processDone$63();
            }
        });
    }

    public /* synthetic */ void lambda$processDone$63() {
        this.progressDialog.dismiss();
        finishFragment();
    }

    public /* synthetic */ void lambda$processDone$65(int i, DialogInterface dialogInterface) {
        this.donePressed = false;
        this.progressDialog = null;
        getConnectionsManager().cancelRequest(i, true);
    }

    public /* synthetic */ void lambda$processDone$66(long j) {
        if (j == 0) {
            this.donePressed = false;
            return;
        }
        this.chatId = j;
        this.currentChat = getMessagesController().getChat(Long.valueOf(j));
        this.donePressed = false;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null) {
            chatFull.hidden_prehistory = true;
        }
        processDone();
    }

    public /* synthetic */ void lambda$processDone$67(DialogInterface dialogInterface) {
        this.createAfterUpload = false;
        this.progressDialog = null;
        this.donePressed = false;
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarProgressView == null) {
            return;
        }
        AnimatorSet animatorSet = this.avatarAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.avatarAnimation = null;
        }
        if (z2) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.avatarAnimation = animatorSet2;
            RadialProgressView radialProgressView = this.avatarProgressView;
            if (z) {
                radialProgressView.setVisibility(0);
                this.avatarOverlay.setVisibility(0);
                AnimatorSet animatorSet3 = this.avatarAnimation;
                RadialProgressView radialProgressView2 = this.avatarProgressView;
                Property property = View.ALPHA;
                animatorSet3.playTogether(ObjectAnimator.ofFloat(radialProgressView2, (Property<RadialProgressView, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.avatarOverlay, (Property<View, Float>) property, 1.0f));
            } else {
                Property property2 = View.ALPHA;
                animatorSet2.playTogether(ObjectAnimator.ofFloat(radialProgressView, (Property<RadialProgressView, Float>) property2, 0.0f), ObjectAnimator.ofFloat(this.avatarOverlay, (Property<View, Float>) property2, 0.0f));
            }
            this.avatarAnimation.setDuration(180L);
            this.avatarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatEditActivity.10
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (ChatEditActivity.this.avatarAnimation == null || ChatEditActivity.this.avatarProgressView == null) {
                        return;
                    }
                    if (!z) {
                        ChatEditActivity.this.avatarProgressView.setVisibility(4);
                        ChatEditActivity.this.avatarOverlay.setVisibility(4);
                    }
                    ChatEditActivity.this.avatarAnimation = null;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    ChatEditActivity.this.avatarAnimation = null;
                }
            });
            this.avatarAnimation.start();
            return;
        }
        RadialProgressView radialProgressView3 = this.avatarProgressView;
        if (z) {
            radialProgressView3.setAlpha(1.0f);
            this.avatarProgressView.setVisibility(0);
            this.avatarOverlay.setAlpha(1.0f);
            this.avatarOverlay.setVisibility(0);
            return;
        }
        radialProgressView3.setAlpha(0.0f);
        this.avatarProgressView.setVisibility(4);
        this.avatarOverlay.setAlpha(0.0f);
        this.avatarOverlay.setVisibility(4);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        super.onActivityResultFragment(i, i2, intent);
        this.imageUpdater.onActivityResult(i, i2, intent);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveSelfArgs(Bundle bundle) {
        String str;
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null && (str = imageUpdater.currentPicturePath) != null) {
            bundle.putString("path", str);
        }
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            String string = editTextEmoji.getText().toString();
            if (string.length() != 0) {
                bundle.putString("nameTextView", string);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void restoreSelfArgs(Bundle bundle) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.currentPicturePath = bundle.getString("path");
        }
    }

    public void setInfo(TLRPC.UserFull userFull) {
        TL_bots.BotInfo botInfo;
        TL_bots.BotInfo botInfo2;
        this.userInfo = userFull;
        if (userFull != null) {
            if (this.currentUser == null) {
                this.currentUser = this.userId == 0 ? null : getMessagesController().getUser(Long.valueOf(this.userId));
            }
            TextCell textCell = this.botAffiliateProgramCell;
            if (textCell != null) {
                textCell.setDrawLoading(this.userInfo == null, 45, true);
                TLRPC.UserFull userFull2 = this.userInfo;
                if (userFull2 != null) {
                    TextCell textCell2 = this.botAffiliateProgramCell;
                    TL_payments.starRefProgram starrefprogram = userFull2.starref_program;
                    textCell2.setValue(starrefprogram == null ? LocaleController.getString(R.string.AffiliateProgramBotOff) : String.format(Locale.US, "%.1f%%", Float.valueOf(starrefprogram.commission_permille / 10.0f)), false);
                }
            }
            TextCell textCell3 = this.verifyCell;
            if (textCell3 != null) {
                TLRPC.UserFull userFull3 = this.userInfo;
                textCell3.setVisibility((userFull3 == null || (botInfo2 = userFull3.bot_info) == null || botInfo2.verifier_settings == null) ? 8 : 0);
            }
            TextInfoPrivacyCell textInfoPrivacyCell = this.verifyInfoCell;
            if (textInfoPrivacyCell != null) {
                TLRPC.UserFull userFull4 = this.userInfo;
                textInfoPrivacyCell.setVisibility((userFull4 == null || (botInfo = userFull4.bot_info) == null || botInfo.verifier_settings == null) ? 8 : 0);
            }
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null) {
            if (this.currentChat == null) {
                this.currentChat = getMessagesController().getChat(Long.valueOf(this.chatId));
            }
            this.historyHidden = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
            this.availableReactions = this.info.available_reactions;
            this.preloadedReactions.clear();
            this.preloadedReactions.addAll(ReactionsUtils.startPreloadReactions(this.currentChat, this.info));
            if (this.channelAffiliateProgramsCell != null && getMessagesController().starrefConnectAllowed && ChatObject.isChannelAndNotMegaGroup(this.currentChat)) {
                this.channelAffiliateProgramsCell.setVisibility(0);
            }
        }
    }

    private void updateFields(boolean z, boolean z2) {
        int i;
        int sendMediaSelectedCount;
        int adminCount;
        TLRPC.ChatParticipants chatParticipants;
        ArrayList<TLRPC.ChatParticipant> arrayList;
        String str;
        int i2;
        TLRPC.ChatFull chatFull;
        String str2;
        int i3;
        String string;
        TextCell textCell;
        TextCell textCell2;
        TextCell textCell3;
        TextCell textCell4;
        String str3;
        int i4;
        String string2;
        TextCell textCell5;
        TextCell textCell6;
        TextCell textCell7;
        TextCell textCell8;
        TextCell textCell9;
        TLRPC.Chat chat;
        if (z && (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) != null) {
            this.currentChat = chat;
        }
        boolean zIsPublic = ChatObject.isPublic(this.currentChat);
        TextInfoPrivacyCell textInfoPrivacyCell = this.settingsSectionCell;
        if (textInfoPrivacyCell != null) {
            textInfoPrivacyCell.setVisibility((this.typeCell != null || ((textCell7 = this.linkedCell) != null && textCell7.getVisibility() == 0) || (((textCell8 = this.historyCell) != null && textCell8.getVisibility() == 0) || ((textCell9 = this.locationCell) != null && textCell9.getVisibility() == 0))) ? 0 : 8);
        }
        TextCell textCell10 = this.logCell;
        if (textCell10 != null) {
            TLRPC.Chat chat2 = this.currentChat;
            textCell10.setVisibility((chat2.megagroup && !chat2.gigagroup && this.info == null) ? 8 : 0);
        }
        TextCell textCell11 = this.linkedCell;
        if (textCell11 != null) {
            TLRPC.ChatFull chatFull2 = this.info;
            if (chatFull2 == null || (!this.isChannel && chatFull2.linked_chat_id == 0)) {
                textCell11.setVisibility(8);
            } else {
                textCell11.setVisibility(0);
                if (this.info.linked_chat_id == 0) {
                    this.linkedCell.setTextAndValueAndIcon((CharSequence) LocaleController.getString("Discussion", R.string.Discussion), (CharSequence) LocaleController.getString("DiscussionInfoShort", R.string.DiscussionInfoShort), R.drawable.msg_discuss, true);
                } else {
                    TLRPC.Chat chat3 = getMessagesController().getChat(Long.valueOf(this.info.linked_chat_id));
                    if (chat3 == null) {
                        this.linkedCell.setVisibility(8);
                    } else if (this.isChannel) {
                        String publicUsername = ChatObject.getPublicUsername(chat3);
                        boolean zIsEmpty = TextUtils.isEmpty(publicUsername);
                        TextCell textCell12 = this.linkedCell;
                        if (zIsEmpty) {
                            textCell12.setTextAndValueAndIcon((CharSequence) LocaleController.getString("Discussion", R.string.Discussion), (CharSequence) chat3.title, R.drawable.msg_discuss, true);
                        } else {
                            textCell12.setTextAndValueAndIcon((CharSequence) LocaleController.getString("Discussion", R.string.Discussion), (CharSequence) ("@" + publicUsername), R.drawable.msg_discuss, true);
                        }
                    } else {
                        String publicUsername2 = ChatObject.getPublicUsername(chat3);
                        boolean zIsEmpty2 = TextUtils.isEmpty(publicUsername2);
                        TextCell textCell13 = this.linkedCell;
                        if (zIsEmpty2) {
                            textCell13.setTextAndValueAndIcon((CharSequence) LocaleController.getString("LinkedChannel", R.string.LinkedChannel), (CharSequence) chat3.title, R.drawable.msg_channel, true);
                        } else {
                            textCell13.setTextAndValueAndIcon((CharSequence) LocaleController.getString("LinkedChannel", R.string.LinkedChannel), (CharSequence) ("@" + publicUsername2), R.drawable.msg_channel, true);
                        }
                    }
                }
            }
        }
        TextCell textCell14 = this.locationCell;
        if (textCell14 != null) {
            TLRPC.ChatFull chatFull3 = this.info;
            if (chatFull3 != null && chatFull3.can_set_location) {
                textCell14.setVisibility(0);
                TLRPC.ChannelLocation channelLocation = this.info.location;
                if (channelLocation instanceof TLRPC.TL_channelLocation) {
                    this.locationCell.setTextAndValue(LocaleController.getString("AttachLocation", R.string.AttachLocation), ((TLRPC.TL_channelLocation) channelLocation).address, z2, true);
                } else {
                    this.locationCell.setTextAndValue(LocaleController.getString("AttachLocation", R.string.AttachLocation), "Unknown address", z2, true);
                }
            } else {
                textCell14.setVisibility(8);
            }
        }
        if (this.typeCell != null) {
            TLRPC.ChatFull chatFull4 = this.info;
            if (chatFull4 != null && (chatFull4.location instanceof TLRPC.TL_channelLocation)) {
                if (!zIsPublic) {
                    string2 = LocaleController.getString("TypeLocationGroupEdit", R.string.TypeLocationGroupEdit);
                } else {
                    string2 = String.format("https://" + getMessagesController().linkPrefix + "/%s", ChatObject.getPublicUsername(this.currentChat));
                }
                TextCell textCell15 = this.typeCell;
                String string3 = LocaleController.getString("TypeLocationGroup", R.string.TypeLocationGroup);
                int i5 = R.drawable.msg_channel;
                TextCell textCell16 = this.historyCell;
                textCell15.setTextAndValueAndIcon(string3, string2, i5, (textCell16 != null && textCell16.getVisibility() == 0) || ((textCell5 = this.linkedCell) != null && textCell5.getVisibility() == 0) || ((textCell6 = this.forumsCell) != null && textCell6.getVisibility() == 0));
            } else {
                boolean z3 = this.currentChat.noforwards;
                if (this.isChannel) {
                    if (zIsPublic) {
                        str3 = "TypePublic";
                        i4 = R.string.TypePublic;
                    } else if (z3) {
                        str3 = "TypePrivateRestrictedForwards";
                        i4 = R.string.TypePrivateRestrictedForwards;
                    } else {
                        str3 = "TypePrivate";
                        i4 = R.string.TypePrivate;
                    }
                    string = LocaleController.getString(str3, i4);
                } else {
                    if (zIsPublic) {
                        str2 = "TypePublicGroup";
                        i3 = R.string.TypePublicGroup;
                    } else if (z3) {
                        str2 = "TypePrivateGroupRestrictedForwards";
                        i3 = R.string.TypePrivateGroupRestrictedForwards;
                    } else {
                        str2 = "TypePrivateGroup";
                        i3 = R.string.TypePrivateGroup;
                    }
                    string = LocaleController.getString(str2, i3);
                }
                boolean z4 = this.isChannel;
                TextCell textCell17 = this.typeCell;
                if (z4) {
                    String string4 = LocaleController.getString("ChannelType", R.string.ChannelType);
                    int i6 = R.drawable.msg_channel;
                    TextCell textCell18 = this.historyCell;
                    textCell17.setTextAndValueAndIcon(string4, string, i6, (textCell18 != null && textCell18.getVisibility() == 0) || ((textCell3 = this.linkedCell) != null && textCell3.getVisibility() == 0) || ((textCell4 = this.forumsCell) != null && textCell4.getVisibility() == 0));
                } else {
                    String string5 = LocaleController.getString("GroupType", R.string.GroupType);
                    int i7 = R.drawable.msg_groups;
                    TextCell textCell19 = this.historyCell;
                    textCell17.setTextAndValueAndIcon(string5, string, i7, (textCell19 != null && textCell19.getVisibility() == 0) || ((textCell = this.linkedCell) != null && textCell.getVisibility() == 0) || ((textCell2 = this.forumsCell) != null && textCell2.getVisibility() == 0));
                }
            }
        }
        if (this.historyCell != null) {
            if (!this.historyHidden || this.forum) {
                str = "ChatHistoryVisible";
                i2 = R.string.ChatHistoryVisible;
            } else {
                str = "ChatHistoryHidden";
                i2 = R.string.ChatHistoryHidden;
            }
            this.historyCell.setTextAndValueAndIcon(LocaleController.getString("ChatHistoryShort", R.string.ChatHistoryShort), LocaleController.getString(str, i2), z2, R.drawable.msg_discuss, this.forumsCell != null);
            this.historyCell.setEnabled(!this.forum);
            updateHistoryShow((this.forum || zIsPublic || ((chatFull = this.info) != null && chatFull.linked_chat_id != 0) || (chatFull != null && (chatFull.location instanceof TLRPC.TL_channelLocation))) ? false : true, z2);
        }
        if (this.membersCell != null) {
            if (this.info != null) {
                TextCell textCell20 = this.memberRequestsCell;
                if (textCell20 != null) {
                    if (textCell20.getParent() == null) {
                        this.infoContainer.addView(this.memberRequestsCell, this.infoContainer.indexOfChild(this.membersCell) + 1, LayoutHelper.createLinear(-1, -2));
                    }
                    this.memberRequestsCell.setVisibility(this.info.requests_pending > 0 ? 0 : 8);
                }
                if (this.isChannel && !ChatObject.isCommunity(this.currentChat)) {
                    this.membersCell.setTextAndValueAndIcon((CharSequence) LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), (CharSequence) String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(this.info.participants_count)), R.drawable.msg_groups, true);
                    TextCell textCell21 = this.blockCell;
                    String string6 = LocaleController.getString(R.string.ChannelBlacklist);
                    TLRPC.ChatFull chatFull5 = this.info;
                    String str4 = String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(Math.max(chatFull5.banned_count, chatFull5.kicked_count)));
                    int i8 = R.drawable.msg_user_remove;
                    TextCell textCell22 = this.logCell;
                    textCell21.setTextAndValueAndIcon(string6, str4, i8, textCell22 != null && textCell22.getVisibility() == 0);
                } else {
                    boolean zIsChannel = ChatObject.isChannel(this.currentChat);
                    TextCell textCell23 = this.membersCell;
                    if (zIsChannel) {
                        textCell23.setTextAndValueAndIcon((CharSequence) LocaleController.getString("ChannelMembers", R.string.ChannelMembers), (CharSequence) String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(this.info.participants_count)), R.drawable.msg_groups, true);
                    } else {
                        String string7 = LocaleController.getString("ChannelMembers", R.string.ChannelMembers);
                        String str5 = String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(this.info.participants.participants.size()));
                        int i9 = R.drawable.msg_groups;
                        TextCell textCell24 = this.memberRequestsCell;
                        textCell23.setTextAndValueAndIcon(string7, str5, i9, textCell24 != null && textCell24.getVisibility() == 0);
                    }
                    TLRPC.Chat chat4 = this.currentChat;
                    if (chat4.gigagroup && !ChatObject.isCommunity(chat4)) {
                        TextCell textCell25 = this.blockCell;
                        String string8 = LocaleController.getString(R.string.ChannelBlacklist);
                        TLRPC.ChatFull chatFull6 = this.info;
                        String str6 = String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(Math.max(chatFull6.banned_count, chatFull6.kicked_count)));
                        int i10 = R.drawable.msg_user_remove;
                        TextCell textCell26 = this.logCell;
                        textCell25.setTextAndValueAndIcon(string8, str6, i10, textCell26 != null && textCell26.getVisibility() == 0);
                    } else {
                        int i11 = this.forum ? 15 : 14;
                        TLRPC.TL_chatBannedRights tL_chatBannedRights = this.currentChat.default_banned_rights;
                        if (tL_chatBannedRights != null) {
                            int i12 = !tL_chatBannedRights.send_plain ? 1 : 0;
                            if (!tL_chatBannedRights.edit_rank) {
                                i12++;
                            }
                            sendMediaSelectedCount = i12 + ChatUsersActivity.getSendMediaSelectedCount(tL_chatBannedRights);
                            TLRPC.TL_chatBannedRights tL_chatBannedRights2 = this.currentChat.default_banned_rights;
                            if (!tL_chatBannedRights2.pin_messages) {
                                sendMediaSelectedCount++;
                            }
                            if (!tL_chatBannedRights2.invite_users) {
                                sendMediaSelectedCount++;
                            }
                            if (this.forum && !tL_chatBannedRights2.manage_topics) {
                                sendMediaSelectedCount++;
                            }
                            if (!tL_chatBannedRights2.change_info) {
                                sendMediaSelectedCount++;
                            }
                            i = i11;
                        } else {
                            i = i11;
                            sendMediaSelectedCount = i;
                        }
                        this.blockCell.setTextAndValueAndIcon(LocaleController.getString(R.string.ChannelPermissions), String.format("%d/%d", Integer.valueOf(sendMediaSelectedCount), Integer.valueOf(i)), z2, R.drawable.msg_permissions, true);
                    }
                    TextCell textCell27 = this.memberRequestsCell;
                    if (textCell27 != null) {
                        String string9 = LocaleController.getString("MemberRequests", R.string.MemberRequests);
                        String str7 = String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(this.info.requests_pending));
                        int i13 = R.drawable.msg_requests;
                        TextCell textCell28 = this.logCell;
                        textCell27.setTextAndValueAndIcon(string9, str7, i13, textCell28 != null && textCell28.getVisibility() == 0);
                    }
                }
                if (ChatObject.hasAdminRights(this.currentChat)) {
                    this.adminCell.setTextAndValueAndIcon((CharSequence) LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), (CharSequence) String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(ChatObject.isChannel(this.currentChat) ? this.info.admins_count : getAdminCount())), R.drawable.msg_admins, true);
                } else if (ChatObject.isChannel(this.currentChat) && (chatParticipants = this.info.participants) != null && (arrayList = chatParticipants.participants) != null && arrayList.size() != this.info.participants_count && this.realAdminCount == 0) {
                    this.adminCell.setTextAndIcon((CharSequence) LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), R.drawable.msg_admins, true);
                    getRealChannelAdminCount();
                } else {
                    TextCell textCell29 = this.adminCell;
                    String string10 = LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators);
                    if (ChatObject.isChannel(this.currentChat)) {
                        adminCount = this.realAdminCount;
                        if (adminCount == 0) {
                            adminCount = getChannelAdminCount();
                        }
                    } else {
                        adminCount = getAdminCount();
                    }
                    textCell29.setTextAndValueAndIcon((CharSequence) string10, (CharSequence) String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(adminCount)), R.drawable.msg_admins, true);
                }
            } else {
                if (this.isChannel && !ChatObject.isCommunity(this.currentChat)) {
                    this.membersCell.setTextAndIcon((CharSequence) LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), R.drawable.msg_groups, true);
                    TextCell textCell30 = this.blockCell;
                    String string11 = LocaleController.getString(R.string.ChannelBlacklist);
                    int i14 = R.drawable.msg_chats_remove;
                    TextCell textCell31 = this.logCell;
                    textCell30.setTextAndIcon(string11, i14, textCell31 != null && textCell31.getVisibility() == 0);
                } else {
                    TextCell textCell32 = this.membersCell;
                    String string12 = LocaleController.getString("ChannelMembers", R.string.ChannelMembers);
                    int i15 = R.drawable.msg_groups;
                    TextCell textCell33 = this.logCell;
                    textCell32.setTextAndIcon(string12, i15, textCell33 != null && textCell33.getVisibility() == 0);
                    boolean z5 = this.currentChat.gigagroup;
                    TextCell textCell34 = this.blockCell;
                    if (z5) {
                        String string13 = LocaleController.getString(R.string.ChannelBlacklist);
                        int i16 = R.drawable.msg_chats_remove;
                        TextCell textCell35 = this.logCell;
                        textCell34.setTextAndIcon(string13, i16, textCell35 != null && textCell35.getVisibility() == 0);
                    } else {
                        textCell34.setTextAndIcon((CharSequence) LocaleController.getString(R.string.ChannelPermissions), R.drawable.msg_permissions, true);
                    }
                }
                this.adminCell.setTextAndIcon((CharSequence) LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), R.drawable.msg_admins, true);
            }
            this.reactionsCell.setVisibility(ChatObject.canChangeChatInfo(this.currentChat) ? 0 : 8);
            updateReactionsCell(z2);
            if (this.info == null || !ChatObject.canUserDoAdminAction(this.currentChat, 3) || (zIsPublic && this.currentChat.creator)) {
                this.inviteLinksCell.setVisibility(8);
            } else {
                int i17 = this.info.invitesCount;
                TextCell textCell36 = this.inviteLinksCell;
                if (i17 > 0) {
                    textCell36.setTextAndValueAndIcon((CharSequence) LocaleController.getString("InviteLinks", R.string.InviteLinks), (CharSequence) Integer.toString(this.info.invitesCount), R.drawable.msg_link2, true);
                } else {
                    textCell36.setTextAndValueAndIcon((CharSequence) LocaleController.getString("InviteLinks", R.string.InviteLinks), (CharSequence) "1", R.drawable.msg_link2, true);
                }
            }
        }
        TextCell textCell37 = this.stickersCell;
        if (textCell37 != null && this.info != null) {
            String string14 = LocaleController.getString(R.string.GroupStickers);
            TLRPC.StickerSet stickerSet = this.info.stickerset;
            textCell37.setTextAndValueAndIcon((CharSequence) string14, (CharSequence) (stickerSet != null ? stickerSet.title : LocaleController.getString(R.string.Add)), R.drawable.msg_sticker, false);
        }
        if (this.suggestedCell != null) {
            updateSuggestedCell(z2);
        }
    }

    private int getChannelAdminCount() {
        TLRPC.ChatParticipants chatParticipants;
        ArrayList<TLRPC.ChatParticipant> arrayList;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null || (chatParticipants = chatFull.participants) == null || (arrayList = chatParticipants.participants) == null) {
            return 1;
        }
        int size = arrayList.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC.ChannelParticipant channelParticipant = ((TLRPC.TL_chatChannelParticipant) this.info.participants.participants.get(i2)).channelParticipant;
            if ((channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) || (channelParticipant instanceof TLRPC.TL_channelParticipantCreator)) {
                i++;
            }
        }
        return i;
    }

    private void getRealChannelAdminCount() {
        TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
        tL_channels_getParticipants.channel = getMessagesController().getInputChannel(this.chatId);
        tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsAdmins();
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_channels_getParticipants, new RequestDelegate() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda0
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$getRealChannelAdminCount$69(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getRealChannelAdminCount$69(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getRealChannelAdminCount$68(tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$getRealChannelAdminCount$68(TLObject tLObject) {
        TextCell textCell = this.adminCell;
        if (textCell == null || tLObject == null) {
            return;
        }
        TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants) tLObject;
        this.realAdminCount = tL_channels_channelParticipants.count;
        textCell.setTextAndValueAndIcon((CharSequence) LocaleController.getString(R.string.ChannelAdministrators), (CharSequence) String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(tL_channels_channelParticipants.count)), R.drawable.msg_admins, true);
    }

    public void updateColorCell() {
        TextCell textCell;
        TextCell textCell2;
        PeerColorActivity.ChangeNameColorCell changeNameColorCell = this.colorCell;
        if (changeNameColorCell != null) {
            TLRPC.Chat chat = this.currentChat;
            TextCell textCell3 = this.historyCell;
            changeNameColorCell.set(chat, (textCell3 != null && textCell3.getVisibility() == 0) || ((textCell = this.forumsCell) != null && textCell.getVisibility() == 0) || ((ChatObject.isMegagroup(this.currentChat) && ChatObject.hasAdminRights(this.currentChat)) || ((textCell2 = this.autoTranslationCell) != null && textCell2.getVisibility() == 0)));
        }
    }

    public void updateSuggestedCell(boolean z) {
        updateSuggestedCell(null, z);
    }

    public void updateSuggestedCell(Long l, boolean z) {
        boolean z2;
        TLRPC.Chat chat = this.currentChat;
        if (chat == null || this.suggestedCell == null) {
            return;
        }
        long jLongValue = 0;
        if (l != null) {
            z2 = l.longValue() >= 0;
        } else {
            z2 = chat.broadcast_messages_allowed;
        }
        if (z2) {
            TLRPC.Chat chat2 = getMessagesController().getChat(Long.valueOf(this.currentChat.linked_monoforum_id));
            if (l != null) {
                jLongValue = l.longValue();
            } else if (chat2 != null) {
                jLongValue = chat2.send_paid_messages_stars;
            }
            this.suggestedCell.setTextAndValueAndIcon(TextCell.applyNewSpan(LocaleController.getString(R.string.PostSuggestions)), (CharSequence) StarsIntroActivity.replaceStarsWithPlain(LocaleController.formatString(R.string.PostSuggestionsStars, Long.valueOf(jLongValue)), 0.66f), R.drawable.msg_markunread, true);
            return;
        }
        this.suggestedCell.setTextAndValueAndIcon(TextCell.applyNewSpan(LocaleController.getString(R.string.PostSuggestions)), (CharSequence) LocaleController.getString(R.string.PostSuggestionsOff), R.drawable.msg_markunread, true);
    }

    private void updateHistoryShow(final boolean z, boolean z2) {
        ValueAnimator valueAnimator = this.updateHistoryShowAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (this.historyCell.getAlpha() <= 0.0f && !z) {
            this.historyCell.setVisibility(8);
            updateColorCell();
            return;
        }
        if (this.historyCell.getVisibility() == 0 && this.historyCell.getAlpha() >= 1.0f && z) {
            return;
        }
        final ArrayList arrayList = new ArrayList();
        boolean z3 = false;
        for (int i = 0; i < this.typeEditContainer.getChildCount(); i++) {
            if (!z3 && this.typeEditContainer.getChildAt(i) == this.historyCell) {
                z3 = true;
            } else if (z3) {
                arrayList.add(this.typeEditContainer.getChildAt(i));
            }
        }
        boolean z4 = false;
        for (int i2 = 0; i2 < this.linearLayout.getChildCount(); i2++) {
            if (!z4 && this.linearLayout.getChildAt(i2) == this.typeEditContainer) {
                z4 = true;
            } else if (z4) {
                arrayList.add(this.linearLayout.getChildAt(i2));
            }
        }
        if (this.historyCell.getVisibility() != 0) {
            this.historyCell.setAlpha(0.0f);
            TextCell textCell = this.historyCell;
            textCell.setTranslationY((-textCell.getHeight()) / 2.0f);
        }
        this.historyCell.setVisibility(0);
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            ((View) arrayList.get(i3)).setTranslationY((-this.historyCell.getHeight()) * (1.0f - this.historyCell.getAlpha()));
        }
        TextCell textCell2 = this.historyCell;
        if (z2) {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(textCell2.getAlpha(), z ? 1.0f : 0.0f);
            this.updateHistoryShowAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda37
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$updateHistoryShow$70(arrayList, valueAnimator2);
                }
            });
            this.updateHistoryShowAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatEditActivity.11
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ChatEditActivity.this.historyCell.setVisibility(z ? 0 : 8);
                    for (int i4 = 0; i4 < arrayList.size(); i4++) {
                        ((View) arrayList.get(i4)).setTranslationY(0.0f);
                    }
                }
            });
            this.updateHistoryShowAnimator.setDuration(320L);
            this.updateHistoryShowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.updateHistoryShowAnimator.start();
            return;
        }
        textCell2.setAlpha(z ? 1.0f : 0.0f);
        TextCell textCell3 = this.historyCell;
        textCell3.setTranslationY(((-textCell3.getHeight()) / 2.0f) * (z ? 0.0f : 1.0f));
        this.historyCell.setScaleY(((z ? 1.0f : 0.0f) * 0.8f) + 0.2f);
        this.historyCell.setVisibility(z ? 0 : 8);
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            ((View) arrayList.get(i4)).setTranslationY(0.0f);
        }
        this.updateHistoryShowAnimator = null;
    }

    public /* synthetic */ void lambda$updateHistoryShow$70(ArrayList arrayList, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.historyCell.setAlpha(fFloatValue);
        TextCell textCell = this.historyCell;
        float f = 1.0f - fFloatValue;
        textCell.setTranslationY(((-textCell.getHeight()) / 2.0f) * f);
        this.historyCell.setScaleY((fFloatValue * 0.8f) + 0.2f);
        for (int i = 0; i < arrayList.size(); i++) {
            ((View) arrayList.get(i)).setTranslationY((-this.historyCell.getHeight()) * f);
        }
    }

    /* JADX WARN: Code duplicated, block: B:21:0x0053  */
    private void updateReactionsCell(boolean z) {
        String str;
        String string;
        TLRPC.ChatFull chatFull = getMessagesController().getChatFull(this.chatId);
        boolean zIsChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(this.currentChat);
        TLRPC.ChatReactions chatReactions = this.availableReactions;
        if (chatReactions == null || (chatReactions instanceof TLRPC.TL_chatReactionsNone)) {
            String string2 = LocaleController.getString(R.string.ReactionsOff);
            if (chatFull == null || !chatFull.paid_reactions_available) {
                str = string2;
            } else {
                string = "1";
            }
            this.reactionsCell.setTextAndValueAndIcon(LocaleController.getString(R.string.Reactions), str, z, R.drawable.msg_reactions2, true);
        }
        if (chatReactions instanceof TLRPC.TL_chatReactionsSome) {
            TLRPC.TL_chatReactionsSome tL_chatReactionsSome = (TLRPC.TL_chatReactionsSome) chatReactions;
            int i = 0;
            for (int i2 = 0; i2 < tL_chatReactionsSome.reactions.size(); i2++) {
                TLRPC.Reaction reaction = tL_chatReactionsSome.reactions.get(i2);
                if (reaction instanceof TLRPC.TL_reactionEmoji) {
                    TLRPC.TL_availableReaction tL_availableReaction = getMediaDataController().getReactionsMap().get(((TLRPC.TL_reactionEmoji) reaction).emoticon);
                    if (tL_availableReaction != null && !tL_availableReaction.inactive) {
                        i++;
                    }
                } else if (reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                    i++;
                }
            }
            if (zIsChannelAndNotMegaGroup) {
                if (chatFull != null && chatFull.paid_reactions_available) {
                    i++;
                }
                string = i == 0 ? LocaleController.getString(R.string.ReactionsOff) : String.valueOf(i);
            } else {
                int iMin = Math.min(getMediaDataController().getEnabledReactionsList().size(), i);
                if (iMin == 0) {
                    string = LocaleController.getString(R.string.ReactionsOff);
                } else {
                    string = LocaleController.formatString(R.string.ReactionsCount, Integer.valueOf(iMin), Integer.valueOf(getMediaDataController().getEnabledReactionsList().size()));
                }
            }
        } else {
            string = LocaleController.getString(R.string.ReactionsAll);
        }
        str = string;
        this.reactionsCell.setTextAndValueAndIcon(LocaleController.getString(R.string.Reactions), str, z, R.drawable.msg_reactions2, true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda39
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                this.f$0.lambda$getThemeDescriptions$71();
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        TextCell textCell = this.setAvatarCell;
        int i = ThemeDescription.FLAG_SELECTOR;
        int i2 = Theme.key_listSelector;
        arrayList.add(new ThemeDescription(textCell, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.setAvatarCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        arrayList.add(new ThemeDescription(this.setAvatarCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        arrayList.add(new ThemeDescription(this.membersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        int i3 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.membersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        int i4 = Theme.key_windowBackgroundWhiteGrayIcon;
        arrayList.add(new ThemeDescription(this.membersCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.adminCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.inviteLinksCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.inviteLinksCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.inviteLinksCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        if (this.memberRequestsCell != null) {
            arrayList.add(new ThemeDescription(this.memberRequestsCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
            arrayList.add(new ThemeDescription(this.memberRequestsCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
            arrayList.add(new ThemeDescription(this.memberRequestsCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        }
        arrayList.add(new ThemeDescription(this.blockCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.blockCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.blockCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.logCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.logCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.logCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        int i5 = Theme.key_windowBackgroundWhiteGrayText2;
        arrayList.add(new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.historyCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.locationCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i3));
        EditTextEmoji editTextEmoji = this.nameTextView;
        int i6 = ThemeDescription.FLAG_HINTTEXTCOLOR;
        int i7 = Theme.key_windowBackgroundWhiteHintText;
        arrayList.add(new ThemeDescription(editTextEmoji, i6, null, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, i7));
        LinearLayout linearLayout = this.avatarContainer;
        int i8 = ThemeDescription.FLAG_BACKGROUND;
        int i9 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(linearLayout, i8, null, null, null, null, i9));
        arrayList.add(new ThemeDescription(this.settingsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i9));
        arrayList.add(new ThemeDescription(this.typeEditContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i9));
        arrayList.add(new ThemeDescription(this.deleteContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i9));
        arrayList.add(new ThemeDescription(this.stickersContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i9));
        arrayList.add(new ThemeDescription(this.infoContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i9));
        int i10 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.settingsTopSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i10));
        arrayList.add(new ThemeDescription(this.settingsSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i10));
        arrayList.add(new ThemeDescription(this.deleteInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i10));
        arrayList.add(new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_text_RedRegular));
        arrayList.add(new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.stickersInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i10));
        arrayList.add(new ThemeDescription(this.stickersInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, themeDescriptionDelegate, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        int i11 = Theme.key_undo_cancelColor;
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i11));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i11));
        int i12 = Theme.key_undo_infoColor;
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i12));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i12));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i12));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i12));
        arrayList.add(new ThemeDescription(this.reactionsCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.reactionsCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.reactionsCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        if (this.suggestedCell != null) {
            arrayList.add(new ThemeDescription(this.suggestedCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
            arrayList.add(new ThemeDescription(this.suggestedCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
            arrayList.add(new ThemeDescription(this.suggestedCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        }
        if (this.statsAndBoosts != null) {
            arrayList.add(new ThemeDescription(this.statsAndBoosts, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i2));
            arrayList.add(new ThemeDescription(this.statsAndBoosts, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
            arrayList.add(new ThemeDescription(this.statsAndBoosts, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        }
        return arrayList;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$71() {
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onInsets(int i, int i2, int i3, int i4) {
        LinearLayout linearLayout = this.linearLayout;
        if (linearLayout != null) {
            linearLayout.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f) + i4);
        }
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.setTranslationY(-i4);
        }
    }
}
