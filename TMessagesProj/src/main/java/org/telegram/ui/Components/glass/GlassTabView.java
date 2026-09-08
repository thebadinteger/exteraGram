package org.telegram.ui.Components.glass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.IconPackType;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.utils.ui.MainTabsUiHelper;
import me.vkryl.android.AnimatorUtils;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.MainTabsLayout;

public class GlassTabView extends FrameLayout implements MainTabsLayout.Tab, FactorAnimator.Target {
    private static final RectF tmpRectF = new RectF();
    private int additionalWidth;
    public float attachScale;
    private AvatarDrawable avatarDrawable;
    private BackupImageView backupImageView;
    private int colorDefault;
    private int colorSelected;
    private int colorSelectedText;
    private final AnimatedTextView.AnimatedTextDrawable counter;
    private final TextPaint defaultTextPaint;
    private float gestureSelectedOverride;
    private boolean hasGestureSelectedOverride;
    private boolean hasVisualWidth;
    private final RLottieImageView imageView;
    private final BoolAnimator isHasCounterAnimator;
    private final BoolAnimator isHasCounterErrorAnimator;
    private final BoolAnimator isSelectedAnimator;
    private long lastBotIconId;
    private int lastIconAnimationRaw;
    private boolean lastIsSelected;
    private boolean needUpdateBackupViewColor;
    private final Paint paintCounterBackground;
    private Drawable premiumStarDrawable;
    private Theme.ResourcesProvider resourcesProvider;
    private TextPaint scaledTextPaint;
    private final BoolAnimator selectedIndicatorAlphaAnimator;
    private boolean selfMeasure;
    private boolean skipDrawSelector;
    private TabAnimation tabAnimation;
    private TLRPC.TL_attachMenuBot tabAnimationBot;
    private final TextView textView;
    private boolean useMainTabSelectedIndicator;
    private boolean usePremiumCounter;
    private float visualWidth;

    public enum TabAnimationType {
        LOTTIE,
        STATIC
    }

    public void onPreBind() {
    }

    public GlassTabView(Context context) {
        super(context);
        this.paintCounterBackground = new Paint(1);
        DecelerateInterpolator decelerateInterpolator = AnimatorUtils.DECELERATE_INTERPOLATOR;
        this.isSelectedAnimator = new BoolAnimator(0, this, decelerateInterpolator, 320L);
        this.selectedIndicatorAlphaAnimator = new BoolAnimator(3, this, decelerateInterpolator, 0L);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.isHasCounterAnimator = new BoolAnimator(1, this, cubicBezierInterpolator, 380L);
        this.isHasCounterErrorAnimator = new BoolAnimator(2, this, cubicBezierInterpolator, 380L);
        this.attachScale = 1.0f;
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        addView(rLottieImageView, LayoutHelper.createFrame(44, 44.0f, 49, 0.0f, -6.0f, 0.0f, 0.0f));
        rLottieImageView.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.SRC_IN));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 12.0f);
        textView.setSingleLine();
        textView.setLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setGravity(17);
        this.defaultTextPaint = new TextPaint(textView.getPaint());
        addView(textView, LayoutHelper.createFrame(-1, -2.0f, 49, 0.0f, 28.33f, 0.0f, 0.0f));
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable();
        this.counter = animatedTextDrawable;
        animatedTextDrawable.setTypeface(AndroidUtilities.bold());
        animatedTextDrawable.setCallback(this);
        animatedTextDrawable.setGravity(17);
        animatedTextDrawable.setTextColor(Theme.getColor(Theme.key_glass_targetMainTabs));
        animatedTextDrawable.setTextSize(AndroidUtilities.dp(10.0f));
    }

    public void setVisualWidth(float f) {
        this.hasVisualWidth = true;
        if (this.visualWidth != f) {
            this.visualWidth = f;
            checkVisualWidth();
            invalidate();
        }
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        checkVisualWidth();
    }

    private void checkVisualWidth() {
        if (this.hasVisualWidth) {
            float measuredWidth = (this.visualWidth - getMeasuredWidth()) / 2.0f;
            this.imageView.setTranslationX(measuredWidth);
            this.textView.setTranslationX(measuredWidth);
        }
    }

    public void setGestureSelectedOverride(float f, boolean z) {
        this.gestureSelectedOverride = f;
        this.hasGestureSelectedOverride = z && !this.useMainTabSelectedIndicator;
        invalidate();
    }

    public void setSkipDrawSelector(boolean z) {
        if (this.skipDrawSelector != z) {
            this.skipDrawSelector = z;
            invalidate();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        float interpolation;
        float width = this.hasVisualWidth ? this.visualWidth : getWidth();
        float floatValue = this.hasGestureSelectedOverride ? this.gestureSelectedOverride : this.isSelectedAnimator.getFloatValue();
        if (floatValue > 0.0f && !this.skipDrawSelector) {
            if (this.useMainTabSelectedIndicator) {
                interpolation = this.selectedIndicatorAlphaAnimator.getFloatValue();
            } else {
                interpolation = AnimatorUtils.DECELERATE_INTERPOLATOR.getInterpolation(floatValue);
            }
            boolean z = this.useMainTabSelectedIndicator;
            Paint paint = this.paintCounterBackground;
            if (z) {
                paint.setColor(MainTabsUiHelper.getMainTabSelectedIndicatorColor(this.colorSelected, interpolation));
                MainTabsUiHelper.setMainTabSelectedIndicatorBounds(tmpRectF, width, getHeight());
            } else {
                paint.setColor(Theme.multAlpha(this.colorSelected, interpolation * 0.09f));
                tmpRectF.set(0.0f, 0.0f, width, getHeight());
            }
            RectF rectF = tmpRectF;
            float fMin = Math.min(rectF.width(), rectF.height()) / 2.0f;
            float fClamp = MathUtils.clamp(this.attachScale, 0.0f, 1.0f);
            float selectedBackgroundScaleX = MainTabsUiHelper.getSelectedBackgroundScaleX(this.useMainTabSelectedIndicator, floatValue) * fClamp;
            float selectedBackgroundScaleY = MainTabsUiHelper.getSelectedBackgroundScaleY(this.useMainTabSelectedIndicator, floatValue) * fClamp;
            canvas.save();
            canvas.scale(selectedBackgroundScaleX, selectedBackgroundScaleY, rectF.centerX(), rectF.centerY());
            canvas.drawRoundRect(rectF, fMin, fMin, this.paintCounterBackground);
            canvas.restore();
        }
        float floatValue2 = (this.usePremiumCounter ? 1.0f : this.isHasCounterAnimator.getFloatValue()) * this.attachScale;
        boolean z2 = floatValue2 > 0.0f;
        if (z2) {
            canvas.saveLayer(0.0f, 0.0f, width, getHeight(), null);
        }
        super.dispatchDraw(canvas);
        if (floatValue2 > 0.0f) {
            canvas.save();
            float fDpf2 = AndroidUtilities.dpf2(1.33f);
            float fDpf3 = (width / 2.0f) + AndroidUtilities.dpf2(11.0f);
            float mainTabCounterCenterY = MainTabsUiHelper.getMainTabCounterCenterY(this.useMainTabSelectedIndicator);
            float fDpf4 = AndroidUtilities.dpf2(16.0f);
            float fMax = Math.max(fDpf4, this.counter.getCurrentWidth() + AndroidUtilities.dp(8.0f));
            float fDpf5 = AndroidUtilities.dpf2(9.333f);
            float fDpf6 = AndroidUtilities.dpf2(8.0f);
            RectF rectF2 = tmpRectF;
            float f = fMax / 2.0f;
            float f2 = fDpf4 / 2.0f;
            rectF2.set((fDpf3 - f) - fDpf2, (mainTabCounterCenterY - f2) - fDpf2, f + fDpf3 + fDpf2, f2 + mainTabCounterCenterY + fDpf2);
            canvas.scale(floatValue2, floatValue2, fDpf3, mainTabCounterCenterY);
            canvas.drawRoundRect(rectF2, fDpf5, fDpf5, Theme.PAINT_CLEAR);
            rectF2.inset(fDpf2, fDpf2);
            if (this.usePremiumCounter) {
                if (this.premiumStarDrawable == null) {
                    this.premiumStarDrawable = getContext().getResources().getDrawable(R.drawable.star).mutate();
                }
                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, AndroidUtilities.dp(96.0f), AndroidUtilities.dp(16.0f), 0.0f, 0.0f);
                canvas.drawRoundRect(rectF2, fDpf6, fDpf6, PremiumGradient.getInstance().getMainGradientPaint());
                int iDpf2 = (int) (fDpf3 - AndroidUtilities.dpf2(7.0f));
                int iDpf3 = (int) (mainTabCounterCenterY - AndroidUtilities.dpf2(7.0f));
                this.premiumStarDrawable.setBounds(iDpf2, iDpf3, AndroidUtilities.dp(14.0f) + iDpf2, AndroidUtilities.dp(14.0f) + iDpf3);
                this.premiumStarDrawable.draw(canvas);
            } else {
                this.paintCounterBackground.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_telegram_color), Theme.getColor(Theme.key_fill_RedNormal), this.isHasCounterErrorAnimator.getFloatValue()));
                canvas.drawRoundRect(rectF2, fDpf6, fDpf6, this.paintCounterBackground);
                this.counter.setBounds(rectF2);
                this.counter.draw(canvas);
            }
            canvas.restore();
        }
        if (z2) {
            canvas.restore();
        }
    }

    public void setCounter(String str, boolean z, boolean z2) {
        this.counter.setText(str, z2);
        this.isHasCounterAnimator.setValue(!TextUtils.isEmpty(str), z2);
        this.isHasCounterErrorAnimator.setValue(z, z2);
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        return drawable == this.counter || super.verifyDrawable(drawable);
    }

    public void setPremiumBadge(boolean z) {
        this.usePremiumCounter = z;
    }

    public void setSelected(boolean z, boolean z2) {
        Typeface typefaceBold;
        boolean z3 = this.useMainTabSelectedIndicator;
        BoolAnimator boolAnimator = this.isSelectedAnimator;
        if (z3) {
            MainTabsUiHelper.setMaterial3MainTabSelected(boolAnimator, this.selectedIndicatorAlphaAnimator, z, z2);
        } else {
            boolAnimator.setValue(z, z2);
        }
        checkPlayAnimation(z2);
        TextView textView = this.textView;
        if (this.useMainTabSelectedIndicator || !z) {
            typefaceBold = AndroidUtilities.bold();
        } else {
            typefaceBold = AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_EXTRA_BOLD);
        }
        textView.setTypeface(typefaceBold);
    }

    public boolean isTabSelected() {
        return this.isSelectedAnimator.getValue();
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 0) {
            updateColors();
        }
        invalidate();
    }

    private void updateColors() {
        int iBlendARGB = ColorUtils.blendARGB(this.colorDefault, this.colorSelected, this.isSelectedAnimator.getFloatValue());
        int iBlendARGB2 = ColorUtils.blendARGB(this.colorDefault, this.colorSelectedText, this.isSelectedAnimator.getFloatValue());
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(iBlendARGB, PorterDuff.Mode.SRC_IN);
        BackupImageView backupImageView = this.backupImageView;
        if (backupImageView != null && this.needUpdateBackupViewColor) {
            backupImageView.setColorFilter(porterDuffColorFilter);
            this.backupImageView.invalidate();
        }
        this.imageView.setColorFilter(porterDuffColorFilter);
        this.textView.setTextColor(iBlendARGB2);
        this.counter.setTextColor(Theme.getColor(Theme.key_glass_targetMainTabs));
    }

    public void updateColorsLottie() {
        this.colorDefault = Theme.getColor(Theme.key_glass_tabUnselected, this.resourcesProvider);
        this.colorSelected = Theme.getColor(Theme.key_glass_tabSelected, this.resourcesProvider);
        this.colorSelectedText = Theme.getColor(Theme.key_glass_tabSelectedText, this.resourcesProvider);
        updateColors();
        invalidate();
    }

    private void checkPlayAnimation(boolean z) {
        TabAnimation tabAnimation;
        int i;
        TLRPC.Document document;
        boolean value = this.isSelectedAnimator.getValue();
        TLRPC.TL_attachMenuBot tL_attachMenuBot = this.tabAnimationBot;
        boolean z2 = true;
        if (tL_attachMenuBot != null) {
            TLRPC.TL_attachMenuBotIcon animatedAttachMenuBotIcon = MediaDataController.getAnimatedAttachMenuBotIcon(tL_attachMenuBot, value);
            if (animatedAttachMenuBotIcon == null) {
                animatedAttachMenuBotIcon = MediaDataController.getStaticAttachMenuBotIcon(this.tabAnimationBot);
                z2 = false;
            }
            if (animatedAttachMenuBotIcon == null || (document = animatedAttachMenuBotIcon.icon) == null) {
                this.backupImageView.clearImage();
            } else if (this.lastBotIconId != document.id) {
                this.backupImageView.setImage(ImageLocation.getForDocument(document), "24_24_lastframe", ImageLocation.getForDocument(document), "24_24_lastframe", z2 ? null : DocumentObject.getSvgThumb(document, Theme.key_windowBackgroundGray, 1.0f), this.tabAnimationBot);
                this.lastBotIconId = document.id;
            }
            updateColors();
            return;
        }
        TabAnimation tabAnimation2 = this.tabAnimation;
        if (tabAnimation2 == null) {
            return;
        }
        if ((tabAnimation2.iconToFilled == 0 || !IconManager.INSTANCE.isBasePackOnly(IconPackType.DEFAULT)) && ((i = (tabAnimation = this.tabAnimation).iconDrawableOutline) != 0 || tabAnimation.iconDrawableFilled != 0)) {
            if (value) {
                i = tabAnimation.iconDrawableFilled;
            }
            if (this.lastIconAnimationRaw != i) {
                this.lastIconAnimationRaw = i;
                this.imageView.clearAnimationDrawable();
                this.imageView.setImageResource(i);
            }
            updateColors();
            return;
        }
        TabAnimation tabAnimation3 = this.tabAnimation;
        int i2 = tabAnimation3.iconStatic;
        if (i2 != -1) {
            this.imageView.setImageResource(i2);
            updateColors();
            return;
        }
        int i3 = value ? tabAnimation3.iconToFilled : tabAnimation3.iconToOutline;
        if (tabAnimation3.endFrameMid != -1) {
            boolean z3 = this.lastIsSelected != value;
            if (this.lastIconAnimationRaw != i3) {
                this.lastIconAnimationRaw = i3;
                this.imageView.setAnimation(i3, 24, 24);
                z3 = true;
            }
            if (z3) {
                RLottieDrawable animatedDrawable = this.imageView.getAnimatedDrawable();
                if (animatedDrawable == null) {
                    return;
                }
                if (value) {
                    animatedDrawable.setCustomEndFrame(this.tabAnimation.endFrameMid);
                    if (animatedDrawable.getCurrentFrame() >= this.tabAnimation.endFrameEnd - 2) {
                        animatedDrawable.setCurrentFrame(0, false);
                    }
                    int currentFrame = animatedDrawable.getCurrentFrame();
                    int i4 = this.tabAnimation.endFrameMid;
                    if (currentFrame <= i4) {
                        animatedDrawable.start();
                    } else {
                        animatedDrawable.setCurrentFrame(i4);
                    }
                } else {
                    int currentFrame2 = animatedDrawable.getCurrentFrame();
                    TabAnimation tabAnimation4 = this.tabAnimation;
                    if (currentFrame2 >= tabAnimation4.endFrameMid - 1) {
                        animatedDrawable.setCustomEndFrame(tabAnimation4.endFrameEnd - 1);
                        animatedDrawable.start();
                    } else {
                        animatedDrawable.setCustomEndFrame(0);
                        animatedDrawable.setCurrentFrame(0);
                    }
                }
            }
            this.lastIsSelected = value;
            return;
        }
        if (tabAnimation3.iconToFilled != tabAnimation3.iconToOutline) {
            if (this.lastIconAnimationRaw != i3) {
                this.lastIconAnimationRaw = i3;
                this.imageView.setAnimation(i3, 24, 24);
                this.imageView.getAnimatedDrawable().setPlayInDirectionOfCustomEndFrame(false);
                RLottieImageView rLottieImageView = this.imageView;
                if (z) {
                    rLottieImageView.getAnimatedDrawable().setCurrentFrame(0);
                    this.imageView.playAnimation();
                    return;
                } else {
                    rLottieImageView.getAnimatedDrawable().setProgress(0.99f);
                    return;
                }
            }
            return;
        }
        if (this.imageView.getAnimatedDrawable() == null) {
            this.imageView.setAnimation(this.tabAnimation.iconToFilled, 24, 24);
        }
        RLottieDrawable animatedDrawable2 = this.imageView.getAnimatedDrawable();
        if (animatedDrawable2 == null || this.lastIsSelected == value) {
            return;
        }
        this.lastIsSelected = value;
        if (value) {
            animatedDrawable2.setPlayInDirectionOfCustomEndFrame(false);
            animatedDrawable2.setCurrentFrame(0);
            animatedDrawable2.setCustomEndFrame(animatedDrawable2.getFramesCount());
        } else {
            animatedDrawable2.setPlayInDirectionOfCustomEndFrame(true);
            animatedDrawable2.setCurrentFrame(animatedDrawable2.getFramesCount());
            animatedDrawable2.setCustomEndFrame(0);
        }
        this.imageView.playAnimation();
    }

    public static GlassTabView createMainTab(Context context, Theme.ResourcesProvider resourcesProvider, TabAnimation tabAnimation, int i) {
        GlassTabView glassTabView = new GlassTabView(context);
        glassTabView.resourcesProvider = resourcesProvider;
        glassTabView.tabAnimation = tabAnimation;
        glassTabView.textView.setText(LocaleController.getString(i));
        glassTabView.checkPlayAnimation(false);
        glassTabView.imageView.setLayoutParams(LayoutHelper.createFrame(24, 24.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        glassTabView.colorDefault = Theme.getColor(Theme.key_glass_tabUnselected, resourcesProvider);
        glassTabView.colorSelected = Theme.getColor(Theme.key_glass_tabSelected, resourcesProvider);
        glassTabView.colorSelectedText = Theme.getColor(Theme.key_glass_tabSelectedText, resourcesProvider);
        glassTabView.updateColors();
        return glassTabView;
    }

    public static GlassTabView createMainNavigationTab(Context context, Theme.ResourcesProvider resourcesProvider, TabAnimation tabAnimation, int i) {
        GlassTabView glassTabViewCreateMainTab = createMainTab(context, resourcesProvider, tabAnimation, i);
        glassTabViewCreateMainTab.setMainTabStyle();
        return glassTabViewCreateMainTab;
    }

    public static GlassTabView createAvatar(Context context, Theme.ResourcesProvider resourcesProvider, int i, int i2) {
        GlassTabView glassTabView = new GlassTabView(context);
        glassTabView.textView.setText(LocaleController.getString(i2));
        glassTabView.imageView.setVisibility(8);
        TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(UserConfig.getInstance(i).getClientUserId()));
        AvatarDrawable avatarDrawable = new AvatarDrawable(user);
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setForUserOrChat(user, avatarDrawable);
        backupImageView.setRoundRadius(ExteraConfig.getAvatarCorners(22.0f));
        glassTabView.backupImageView = backupImageView;
        glassTabView.addView(backupImageView, LayoutHelper.createFrame(22, 22.0f, 49, 0.0f, 5.0f, 0.0f, 0.0f));
        glassTabView.colorDefault = Theme.getColor(Theme.key_glass_tabUnselected, resourcesProvider);
        glassTabView.colorSelected = Theme.getColor(Theme.key_glass_tabSelected, resourcesProvider);
        glassTabView.colorSelectedText = Theme.getColor(Theme.key_glass_tabSelectedText, resourcesProvider);
        glassTabView.updateColors();
        return glassTabView;
    }

    public static GlassTabView createMainNavigationAvatar(Context context, Theme.ResourcesProvider resourcesProvider, int i, int i2) {
        GlassTabView glassTabViewCreateAvatar = createAvatar(context, resourcesProvider, i, i2);
        glassTabViewCreateAvatar.setMainTabStyle();
        return glassTabViewCreateAvatar;
    }

    private void setMainTabStyle() {
        if (MainTabsUiHelper.isMaterial3NavigationBar()) {
            this.useMainTabSelectedIndicator = true;
            this.imageView.setLayoutParams(LayoutHelper.createFrame(24, 24.0f, 49, 0.0f, MainTabsUiHelper.getMaterial3MainTabIconTopDp(), 0.0f, 0.0f));
            BackupImageView backupImageView = this.backupImageView;
            if (backupImageView != null) {
                backupImageView.setLayoutParams(LayoutHelper.createFrame(22, 22.0f, 49, 0.0f, MainTabsUiHelper.getMaterial3MainTabAvatarTopDp(), 0.0f, 0.0f));
            }
            MainTabsUiHelper.applyMaterial3MainTabStyle(this.textView, this.isSelectedAnimator);
        }
    }

    public void updateUserAvatar(int i) {
        TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(UserConfig.getInstance(i).getClientUserId()));
        this.backupImageView.setForUserOrChat(user, new AvatarDrawable(user));
    }

    public static GlassTabView createAttachTab(Context context, Theme.ResourcesProvider resourcesProvider) {
        GlassTabView glassTabView = new GlassTabView(context);
        glassTabView.resourcesProvider = resourcesProvider;
        glassTabView.selfMeasure = true;
        glassTabView.textView.setTextSize(1, 11.0f);
        glassTabView.textView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        glassTabView.checkPlayAnimation(false);
        glassTabView.imageView.setLayoutParams(LayoutHelper.createFrame(24, 24.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        glassTabView.colorDefault = Theme.getColor(Theme.key_glass_tabUnselected, resourcesProvider);
        glassTabView.colorSelected = Theme.getColor(Theme.key_glass_tabSelected, resourcesProvider);
        glassTabView.colorSelectedText = Theme.getColor(Theme.key_glass_tabSelectedText, resourcesProvider);
        glassTabView.updateColors();
        return glassTabView;
    }

    public static GlassTabView createAttachBotTab(Context context, Theme.ResourcesProvider resourcesProvider) {
        GlassTabView glassTabView = new GlassTabView(context);
        glassTabView.resourcesProvider = resourcesProvider;
        glassTabView.selfMeasure = true;
        glassTabView.textView.setTextSize(1, 11.0f);
        glassTabView.textView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        glassTabView.imageView.setVisibility(8);
        glassTabView.checkPlayAnimation(false);
        BackupImageView backupImageView = new BackupImageView(context);
        glassTabView.backupImageView = backupImageView;
        glassTabView.addView(backupImageView, LayoutHelper.createFrame(24, 24.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        glassTabView.colorDefault = Theme.getColor(Theme.key_glass_tabUnselected, resourcesProvider);
        glassTabView.colorSelected = Theme.getColor(Theme.key_glass_tabSelected, resourcesProvider);
        glassTabView.colorSelectedText = Theme.getColor(Theme.key_glass_tabSelectedText, resourcesProvider);
        glassTabView.updateColors();
        return glassTabView;
    }

    public BackupImageView getBackupImageView() {
        return this.backupImageView;
    }

    public void setAdditionalWidth(int i) {
        this.additionalWidth = i;
        this.selfMeasure = true;
    }

    public float measureAttachTabWidth() {
        float fMeasureTextWidth = measureTextWidth();
        return Math.min(AndroidUtilities.dp(84.0f), (int) (fMeasureTextWidth + (AndroidUtilities.lerp(AndroidUtilities.dpf2(16.0f), AndroidUtilities.dp(8.0f), MathUtils.clamp((fMeasureTextWidth - AndroidUtilities.dp(40.0f)) / AndroidUtilities.dp(16.0f), 0.0f, 1.0f)) * 2.0f)));
    }

    public void setAttachScale(float f) {
        this.textView.setScaleX(f);
        this.textView.setScaleY(f);
        this.imageView.setScaleX(f);
        this.imageView.setScaleY(f);
        BackupImageView backupImageView = this.backupImageView;
        if (backupImageView != null) {
            backupImageView.setScaleX(f);
            this.backupImageView.setScaleY(f);
        }
        this.attachScale = f;
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        if (this.selfMeasure) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(((int) measureAttachTabWidth()) + this.additionalWidth, TLObject.FLAG_30), i2);
        } else {
            super.onMeasure(i, i2);
        }
    }

    public float measureTextWidth() {
        return this.defaultTextPaint.measureText(this.textView.getText().toString());
    }

    @Override // org.telegram.ui.MainTabsLayout.Tab
    public float measureTextWidth(float f) {
        if (this.scaledTextPaint == null) {
            this.scaledTextPaint = new TextPaint(this.defaultTextPaint);
        }
        this.scaledTextPaint.setTextSize(AndroidUtilities.dp(f));
        return this.scaledTextPaint.measureText(this.textView.getText().toString());
    }

    @Override // org.telegram.ui.MainTabsLayout.Tab
    public void setTextSizeDp(float f) {
        float fDp = AndroidUtilities.dp(f);
        if (this.textView.getTextSize() != fDp) {
            this.textView.setTextSize(1, f);
            this.defaultTextPaint.setTextSize(fDp);
        }
    }

    public enum TabAnimation {
        CONTACTS(R.raw.tab_contacts, R.drawable.tabs_contact_active_24, R.drawable.tabs_contacts_24, -1, -1),
        CALLS(R.raw.tab_calls, R.drawable.tabs_calls_active_24, R.drawable.tabs_calls_24, -1, -1),
        CHATS(R.raw.tab_chats, R.drawable.tabs_chats_active_24, R.drawable.tabs_chats_24, -1, -1),
        SETTINGS(R.raw.tab_settings, R.drawable.filled_profile_settings, R.drawable.outline_profile_settings, -1, -1),
        FEED(0, R.drawable.ic_feed_filled, R.drawable.ic_feed, -1, -1),
        CHECKLIST(R.raw.tab_checklist, R.raw.tab_checklist_reverse),
        COLORS(R.raw.tab_colors, R.raw.tab_colors_reverse),
        FILES(R.raw.tab_files, R.raw.tab_files_reverse),
        GALLERY(R.raw.tab_gallery, R.raw.tab_gallery_reverse),
        GIFT(R.raw.tab_gift, R.raw.tab_gift_reverse),
        LOCATION(R.raw.tab_location, R.raw.tab_location_reverse),
        STICKER(R.raw.tab_sticker, R.raw.tab_sticker_reverse),
        EMOJI(R.raw.tab_emoji, R.raw.tab_emoji_reverse),
        MODELS(R.raw.tab_models, R.raw.tab_models_reverse),
        MUSIC(R.raw.tab_music, R.raw.tab_music_reverse),
        POLL(R.raw.tab_poll, R.raw.tab_poll_reverse),
        SYMBOLS(R.raw.tab_symbols, R.raw.tab_symbols_reverse),
        REPLIES(R.raw.tab_reply, R.raw.tab_reply_reverse),
        WALLET(R.raw.tab_wallet, R.raw.tab_wallet_reverse),
        LINK(TabAnimationType.STATIC, R.drawable.tabs_link_24),
        ARTICLE(R.raw.tab_article, R.raw.tab_article_reverse),
        BOOSTS(R.raw.boosts, 25, 49),
        MONETIZATION(R.raw.monetize, 19, 45);

        public final int endFrameEnd;
        public final int endFrameMid;
        public final int iconDrawableFilled;
        public final int iconDrawableOutline;
        public final int iconStatic;
        public final int iconToFilled;
        public final int iconToOutline;

        TabAnimation(int i, int i2, int i3) {
            this.iconToFilled = i;
            this.iconToOutline = i;
            this.iconDrawableFilled = 0;
            this.iconDrawableOutline = 0;
            this.endFrameMid = i2;
            this.endFrameEnd = i3;
            this.iconStatic = -1;
        }

        TabAnimation(TabAnimationType tabAnimationType, int i) {
            if (tabAnimationType == TabAnimationType.LOTTIE) {
                this.iconToFilled = i;
                this.iconToOutline = i;
                this.iconStatic = -1;
            } else {
                this.iconStatic = i;
                this.iconToFilled = -1;
                this.iconToOutline = -1;
            }
            this.iconDrawableFilled = 0;
            this.iconDrawableOutline = 0;
            this.endFrameMid = -1;
            this.endFrameEnd = -1;
        }

        TabAnimation(int i, int i2, int i3, int i4, int i5) {
            this.iconToFilled = i;
            this.iconToOutline = i;
            this.iconDrawableFilled = i2;
            this.iconDrawableOutline = i3;
            this.endFrameMid = i4;
            this.endFrameEnd = i5;
            this.iconStatic = -1;
        }

        TabAnimation(int i, int i2) {
            this.iconToFilled = i;
            this.iconToOutline = i2;
            this.iconDrawableFilled = 0;
            this.iconDrawableOutline = 0;
            this.endFrameMid = -1;
            this.endFrameEnd = -1;
            this.iconStatic = -1;
        }
    }

    public void setTabAnimation(TabAnimation tabAnimation) {
        this.tabAnimation = tabAnimation;
        this.tabAnimationBot = null;
        this.lastIconAnimationRaw = 0;
        this.lastBotIconId = 0L;
        this.imageView.clearAnimationDrawable();
        checkPlayAnimation(false);
    }

    public void setText(CharSequence charSequence) {
        this.textView.setText(charSequence);
    }

    public void setAttachBot(TLRPC.User user, TLRPC.TL_attachMenuBot tL_attachMenuBot, int i) {
        if (user == null || tL_attachMenuBot == null) {
            return;
        }
        this.tabAnimation = null;
        this.tabAnimationBot = tL_attachMenuBot;
        this.lastIconAnimationRaw = 0;
        this.lastBotIconId = 0L;
        this.textView.setText(tL_attachMenuBot.short_name);
        this.backupImageView.setRoundRadius(0);
        this.backupImageView.setSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
        this.backupImageView.setLayoutParams(LayoutHelper.createFrame(24, 24.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.needUpdateBackupViewColor = true;
        checkPlayAnimation(false);
        updateColors();
        invalidate();
    }

    public void setAttachBotUser(TLRPC.User user, int i) {
        if (user == null) {
            return;
        }
        this.tabAnimation = null;
        this.tabAnimationBot = null;
        this.lastIconAnimationRaw = 0;
        this.lastBotIconId = 0L;
        this.textView.setText(ContactsController.formatName(user.first_name, user.last_name));
        if (this.avatarDrawable == null) {
            this.avatarDrawable = new AvatarDrawable();
        }
        this.avatarDrawable.setInfo(i, user);
        this.backupImageView.setForUserOrChat(user, this.avatarDrawable);
        this.backupImageView.setSize(-1, -1);
        this.backupImageView.setRoundRadius(ExteraConfig.getAvatarCorners(22.0f));
        this.backupImageView.setLayoutParams(LayoutHelper.createFrame(22, 22.0f, 49, 0.0f, 5.0f, 0.0f, 0.0f));
        this.backupImageView.setColorFilter(null);
        this.needUpdateBackupViewColor = false;
        invalidate();
    }
}
