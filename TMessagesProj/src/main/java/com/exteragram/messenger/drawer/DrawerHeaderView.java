package com.exteragram.messenger.drawer;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.badges.BadgesController;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.DialogsActivity;

public class DrawerHeaderView extends FrameLayout {
    private final AvatarDrawable avatarDrawable;
    private final BackupImageView avatarView;
    private boolean chevronExpanded;
    private final ImageView chevronView;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable exteraBadgeDrawable;
    private int lastProxyColor;
    private int lastProxyState;
    private final SimpleTextView nameView;
    private Runnable onBadgeClick;
    private Runnable onChevronClick;
    private Runnable onNavigateToProfile;
    private Runnable onProxyClick;
    private Runnable onStatusClick;
    private Runnable onThemeToggle;
    private Runnable onThemeToggleLongClick;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable premiumStatusDrawable;
    private final FrameLayout proxyButton;
    private final ImageView proxyIcon;
    private final AnimatedTextView proxyTextView;
    private final SimpleTextView subtitleView;
    private final RLottieDrawable sunDrawable;
    private final FrameLayout themeToggleBg;
    private final RLottieImageView themeToggleView;
    private static final int COLOR_KEY_TEXT = Theme.key_windowBackgroundWhiteBlackText;
    private static final int COLOR_KEY_SUBTITLE = Theme.key_windowBackgroundWhiteGrayText2;
    private static final int COLOR_KEY_ICON = Theme.key_windowBackgroundWhiteGrayIcon;
    private static final int COLOR_KEY_STATUS = Theme.key_profile_verifiedBackground;

    public DrawerHeaderView(Context context) {
        super(context);
        this.lastProxyState = -1;
        this.lastProxyColor = -1;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarView = backupImageView;
        backupImageView.setRoundRadius(ExteraConfig.getAvatarCorners(72.0f));
        addView(backupImageView, LayoutHelper.createFrame(72, 72.0f, 51, 16.0f, 16.0f, 0.0f, 0.0f));
        backupImageView.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DrawerHeaderView.this.lambda$new$0(view);
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.themeToggleBg = frameLayout;
        ScaleStateListAnimator.apply(frameLayout);
        frameLayout.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), getThemeToggleBackgroundColor()));
        addView(frameLayout, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 16.0f, 16.0f, 0.0f));
        int i = R.raw.sun;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(i, String.valueOf(i), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, null);
        this.sunDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.themeToggleView = rLottieImageView;
        rLottieImageView.setAnimation(rLottieDrawable);
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        rLottieImageView.setScaleType(scaleType);
        frameLayout.addView(rLottieImageView, LayoutHelper.createFrame(24, 24, 17));
        setThemeToggleStaticState(Theme.isCurrentThemeDark());
        frameLayout.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DrawerHeaderView.this.lambda$new$1(view);
            }
        });
        frameLayout.setOnLongClickListener(new View.OnLongClickListener() { 
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return DrawerHeaderView.this.lambda$new$2(view);
            }
        });
        updateThemeToggleColors();
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.proxyButton = frameLayout2;
        ScaleStateListAnimator.apply(frameLayout2);
        frameLayout2.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), getThemeToggleBackgroundColor()));
        addView(frameLayout2, LayoutHelper.createFrame(-2, 36.0f, 53, 0.0f, 16.0f, 60.0f, 0.0f));
        frameLayout2.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DrawerHeaderView.this.lambda$new$3(view);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(17);
        linearLayout.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        frameLayout2.addView(linearLayout, LayoutHelper.createFrame(-2, -1.0f));
        ImageView imageView = new ImageView(context);
        this.proxyIcon = imageView;
        imageView.setScaleType(scaleType);
        linearLayout.addView(imageView, LayoutHelper.createLinear(24, 24));
        AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true);
        this.proxyTextView = animatedTextView;
        animatedTextView.setTextSize(AndroidUtilities.dp(13.0f));
        animatedTextView.adaptWidth = true;
        animatedTextView.setTypeface(AndroidUtilities.bold());
        animatedTextView.setTextColor(Theme.getColor(COLOR_KEY_ICON));
        animatedTextView.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(4.0f), 0);
        animatedTextView.setVisibility(8);
        linearLayout.addView(animatedTextView, LayoutHelper.createLinear(-2, -2, 16));
        FrameLayout frameLayout3 = new FrameLayout(context);
        addView(frameLayout3, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 100.0f, 0.0f, 0.0f));
        frameLayout3.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DrawerHeaderView.this.lambda$new$4(view);
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameView = simpleTextView;
        simpleTextView.setTextSize(15);
        simpleTextView.setTypeface(AndroidUtilities.bold());
        simpleTextView.setTextColor(Theme.getColor(COLOR_KEY_TEXT));
        simpleTextView.setGravity(19);
        simpleTextView.setEllipsizeByGradient(true);
        simpleTextView.setCanHideRightDrawable(false);
        simpleTextView.setRightDrawableOutside(true);
        simpleTextView.setClickable(false);
        frameLayout3.addView(simpleTextView, LayoutHelper.createFrame(-1, 24.0f, 51, 16.0f, 0.0f, 64.0f, 0.0f));
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(simpleTextView, AndroidUtilities.dp(22.0f));
        this.premiumStatusDrawable = swapAnimatedEmojiDrawable;
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable2 = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(simpleTextView, AndroidUtilities.dp(22.0f));
        this.exteraBadgeDrawable = swapAnimatedEmojiDrawable2;
        simpleTextView.setRightDrawable(swapAnimatedEmojiDrawable);
        simpleTextView.setRightDrawable2(swapAnimatedEmojiDrawable2);
        simpleTextView.setRightDrawableOnClick(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DrawerHeaderView.this.lambda$new$5(view);
            }
        });
        simpleTextView.setRightDrawable2OnClick(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DrawerHeaderView.this.lambda$new$6(view);
            }
        });
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.subtitleView = simpleTextView2;
        simpleTextView2.setTextSize(12);
        int i2 = COLOR_KEY_SUBTITLE;
        simpleTextView2.setTextColor(Theme.getColor(i2));
        simpleTextView2.setMaxLines(1);
        simpleTextView2.setClickable(false);
        frameLayout3.addView(simpleTextView2, LayoutHelper.createFrame(-2, -2.0f, 51, 16.0f, 26.0f, 64.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.chevronView = imageView2;
        imageView2.setImageResource(R.drawable.msg_expand);
        imageView2.setScaleType(scaleType);
        imageView2.setColorFilter(createColorFilter(i2));
        frameLayout3.addView(imageView2, LayoutHelper.createFrame(24, 24.0f, 21, 0.0f, 0.0f, 22.0f, 0.0f));
    }

    public /* synthetic */ void lambda$new$0(View view) {
        Runnable runnable = this.onNavigateToProfile;
        if (runnable != null) {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$new$1(View view) {
        resetThemeTogglePressAnimation();
        Runnable runnable = this.onThemeToggle;
        if (runnable != null) {
            runnable.run();
        }
    }

    public /* synthetic */ boolean lambda$new$2(View view) {
        Runnable runnable = this.onThemeToggleLongClick;
        if (runnable == null) {
            return false;
        }
        runnable.run();
        return true;
    }

    public /* synthetic */ void lambda$new$3(View view) {
        Runnable runnable = this.onProxyClick;
        if (runnable != null) {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$new$4(View view) {
        Runnable runnable = this.onChevronClick;
        if (runnable != null) {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$new$5(View view) {
        Runnable runnable = this.onStatusClick;
        if (runnable != null) {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$new$6(View view) {
        Runnable runnable = this.onBadgeClick;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setOnChevronClick(Runnable runnable) {
        this.onChevronClick = runnable;
    }

    public void setOnThemeToggle(Runnable runnable) {
        this.onThemeToggle = runnable;
    }

    public void setOnThemeToggleLongClick(Runnable runnable) {
        this.onThemeToggleLongClick = runnable;
    }

    public void setOnNavigateToProfile(Runnable runnable) {
        this.onNavigateToProfile = runnable;
    }

    public void setOnStatusClick(Runnable runnable) {
        this.onStatusClick = runnable;
    }

    public void setOnBadgeClick(Runnable runnable) {
        this.onBadgeClick = runnable;
    }

    public void setOnProxyClick(Runnable runnable) {
        this.onProxyClick = runnable;
    }

    public SimpleTextView getNameView() {
        return this.nameView;
    }

    public RLottieImageView getThemeToggleView() {
        return this.themeToggleView;
    }

    public void updateUserInfo() {
        updateUserInfo(null);
    }

    public void updateUserInfo(BadgeDTO badgeDTO) {
        int i = UserConfig.selectedAccount;
        TLRPC.User currentUser = UserConfig.getInstance(i).getCurrentUser();
        if (currentUser == null) {
            return;
        }
        this.avatarDrawable.setInfo(i, currentUser);
        this.avatarView.setRoundRadius(ExteraConfig.getAvatarCorners(72.0f));
        this.avatarView.getImageReceiver().setCurrentAccount(i);
        this.avatarView.setForUserOrChat(currentUser, this.avatarDrawable);
        this.nameView.setText(ContactsController.formatName(currentUser.first_name, currentUser.last_name));
        this.premiumStatusDrawable.setCurrentAccount(i);
        this.exteraBadgeDrawable.setCurrentAccount(i);
        String publicUsername = DialogObject.getPublicUsername(currentUser);
        if (publicUsername != null && !publicUsername.isEmpty()) {
            this.subtitleView.setText("@".concat(publicUsername));
        } else {
            String str = currentUser.phone;
            if (str != null && !str.isEmpty()) {
                boolean hidePhoneNumber = ExteraConfig.getHidePhoneNumber();
                SimpleTextView simpleTextView = this.subtitleView;
                if (hidePhoneNumber) {
                    simpleTextView.setText(LocaleController.getString(R.string.MobileHidden));
                } else {
                    simpleTextView.setText(PhoneFormat.getInstance().format("+" + currentUser.phone));
                }
            } else {
                this.subtitleView.setText(LocaleController.getString(R.string.NumberUnknown));
            }
        }
        long emojiStatusDocumentId = DialogObject.getEmojiStatusDocumentId(currentUser.emoji_status);
        boolean zIsPremiumUser = MessagesController.getInstance(i).isPremiumUser(currentUser);
        int color = Theme.getColor(COLOR_KEY_STATUS);
        if (emojiStatusDocumentId != 0) {
            this.premiumStatusDrawable.set(emojiStatusDocumentId, true);
        } else {
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.premiumStatusDrawable;
            if (zIsPremiumUser) {
                swapAnimatedEmojiDrawable.set(PremiumGradient.getInstance().premiumStarDrawableMini, true);
            } else {
                swapAnimatedEmojiDrawable.set((Drawable) null, true);
            }
        }
        this.premiumStatusDrawable.setParticles(DialogObject.isEmojiStatusCollectible(currentUser.emoji_status), true);
        this.premiumStatusDrawable.setColor(Integer.valueOf(color));
        if (badgeDTO == null) {
            badgeDTO = BadgesController.INSTANCE.getBadge(currentUser);
        }
        applyNameDrawables((emojiStatusDocumentId != 0 || zIsPremiumUser) ? this.premiumStatusDrawable : null, updateBadgeDrawable(badgeDTO, true, true, color));
        updateProxyStatus();
    }

    public void updateProxyStatus() {
        long jClamp;
        int i;
        SharedConfig.ProxyInfo proxyInfo;
        boolean zIsProxyEnabled = SharedConfig.isProxyEnabled();
        int connectionState = ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState();
        boolean z = connectionState == 3 || connectionState == 5;
        if (zIsProxyEnabled && (proxyInfo = SharedConfig.currentProxy) != null && z) {
            jClamp = Utilities.clamp(proxyInfo.ping, 9999L, 0L);
            i = jClamp > 0 ? 2 : 1;
        } else if (SharedConfig.proxyList.isEmpty()) {
            jClamp = 0;
            i = 0;
        } else {
            jClamp = 0;
            i = 0;
        }
        if (i != this.lastProxyState) {
            TransitionManager.beginDelayedTransition(this, new ChangeBounds().setDuration(150L));
            this.lastProxyState = i;
        }
        FrameLayout frameLayout = this.proxyButton;
        if (i > 0) {
            frameLayout.setVisibility(0);
            AnimatedTextView animatedTextView = this.proxyTextView;
            if (i == 2) {
                animatedTextView.setVisibility(0);
                this.proxyTextView.setText(LocaleController.formatString(R.string.NavigationDrawerProxyPingShort, Long.valueOf(jClamp)), true);
            } else {
                animatedTextView.setVisibility(8);
            }
            int color = Theme.getColor((zIsProxyEnabled && z) ? Theme.key_windowBackgroundWhiteGreenText : COLOR_KEY_ICON);
            if (color != this.lastProxyColor) {
                this.lastProxyColor = color;
                this.proxyButton.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.multAlpha(color, 0.075f)));
                this.proxyIcon.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                this.proxyTextView.setTextColor(color);
            }
            this.proxyIcon.setImageResource((zIsProxyEnabled && z) ? R.drawable.drawer_proxy_on : R.drawable.drawer_proxy_off);
            return;
        }
        frameLayout.setVisibility(8);
    }

    private Drawable updateBadgeDrawable(BadgeDTO badgeDTO, boolean z, boolean z2, int i) {
        if (badgeDTO == null) {
            clearBadgeDrawables(z);
            return null;
        }
        this.exteraBadgeDrawable.set(badgeDTO.getDocumentId(), z);
        this.exteraBadgeDrawable.setParticles(z2, z);
        this.exteraBadgeDrawable.setColor(Integer.valueOf(i));
        return this.exteraBadgeDrawable;
    }

    private void clearBadgeDrawables(boolean z) {
        this.exteraBadgeDrawable.set((Drawable) null, z);
        this.exteraBadgeDrawable.setParticles(false, z);
        this.exteraBadgeDrawable.setColor(null);
    }

    public void setChevronExpanded(boolean z) {
        if (this.chevronExpanded == z) {
            return;
        }
        this.chevronExpanded = z;
        this.chevronView.animate().cancel();
        this.chevronView.animate().rotation(z ? 180.0f : 0.0f).setDuration(250L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
    }

    public int[] getThemeTogglePosition() {
        int[] iArr = new int[2];
        this.themeToggleBg.getLocationInWindow(iArr);
        iArr[0] = iArr[0] + (this.themeToggleBg.getMeasuredWidth() / 2);
        iArr[1] = iArr[1] + (this.themeToggleBg.getMeasuredHeight() / 2);
        return iArr;
    }

    public void animateThemeToggle(boolean z) {
        syncThemeToggle(z, true);
    }

    public void updateColors() {
        this.nameView.setTextColor(Theme.getColor(COLOR_KEY_TEXT));
        SimpleTextView simpleTextView = this.subtitleView;
        int i = COLOR_KEY_SUBTITLE;
        simpleTextView.setTextColor(Theme.getColor(i));
        this.chevronView.setColorFilter(createColorFilter(i));
        this.themeToggleBg.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), getThemeToggleBackgroundColor()));
        this.lastProxyState = -1;
        this.lastProxyColor = -1;
        updateUserInfo();
        if (!this.themeToggleView.isPlaying() && !DialogsActivity.switchingTheme) {
            syncThemeToggle(false);
        }
        if (!DialogsActivity.switchingTheme || Theme.isCurrentThemeDark()) {
            updateThemeToggleColors();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.premiumStatusDrawable.attach();
        this.exteraBadgeDrawable.attach();
        if (this.themeToggleView.isPlaying() || DialogsActivity.switchingTheme) {
            return;
        }
        syncThemeToggle(false);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.premiumStatusDrawable.detach();
        this.exteraBadgeDrawable.detach();
    }

    private void updateThemeToggleColors() {
        RLottieDrawable rLottieDrawable = this.sunDrawable;
        int i = COLOR_KEY_ICON;
        applyThemeToggleColors(rLottieDrawable, Theme.getColor(i));
        this.themeToggleView.setColorFilter(createColorFilter(i));
        this.themeToggleView.invalidate();
    }

    private void applyThemeToggleColors(RLottieDrawable rLottieDrawable, int i) {
        rLottieDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
        rLottieDrawable.beginApplyLayerColors();
        rLottieDrawable.setLayerColor("Sunny.**", i);
        rLottieDrawable.setLayerColor("Path 6.**", i);
        rLottieDrawable.setLayerColor("Path.**", i);
        rLottieDrawable.setLayerColor("Path 5.**", i);
        rLottieDrawable.commitApplyLayerColors();
    }

    private void syncThemeToggle(boolean z) {
        syncThemeToggle(Theme.isCurrentThemeDark(), z);
    }

    private void syncThemeToggle(boolean z, boolean z2) {
        if (this.sunDrawable.getFramesCount() <= 0) {
            return;
        }
        int themeToggleCurrentFrame = getThemeToggleCurrentFrame(z);
        int themeToggleEndFrame = getThemeToggleEndFrame(z);
        if (z2) {
            this.sunDrawable.setCustomEndFrame(themeToggleEndFrame);
            this.themeToggleView.playAnimation();
        } else {
            if (!isAttachedToWindow()) {
                setThemeToggleStaticState(z);
                return;
            }
            this.sunDrawable.stop();
            this.sunDrawable.setCurrentFrame(themeToggleCurrentFrame, false, true);
            this.sunDrawable.setCustomEndFrame(themeToggleCurrentFrame);
            this.themeToggleView.invalidate();
        }
    }

    private int getThemeToggleCurrentFrame(boolean z) {
        if (z) {
            return this.sunDrawable.getFramesCount() - 1;
        }
        return 0;
    }

    private int getThemeToggleEndFrame(boolean z) {
        if (z) {
            return this.sunDrawable.getFramesCount();
        }
        return 0;
    }

    private void setThemeToggleStaticState(boolean z) {
        this.sunDrawable.stop();
        this.sunDrawable.setCurrentFrame(getThemeToggleCurrentFrame(z));
        this.sunDrawable.setCustomEndFrame(getThemeToggleEndFrame(z));
        this.themeToggleView.invalidate();
    }

    private void applyNameDrawables(Drawable drawable, Drawable drawable2) {
        if (drawable != null && drawable == this.nameView.getRightDrawable2()) {
            this.nameView.setRightDrawable2(null);
        }
        this.nameView.setRightDrawable(drawable);
        this.nameView.setRightDrawable2(drawable2);
    }

    private static PorterDuffColorFilter createColorFilter(int i) {
        return new PorterDuffColorFilter(Theme.getColor(i), PorterDuff.Mode.SRC_IN);
    }

    private static int getThemeToggleBackgroundColor() {
        return Theme.multAlpha(Theme.getColor(COLOR_KEY_ICON), 0.075f);
    }

    private void resetThemeTogglePressAnimation() {
        this.themeToggleBg.setPressed(false);
        this.themeToggleBg.setScaleX(1.0f);
        this.themeToggleBg.setScaleY(1.0f);
    }
}
