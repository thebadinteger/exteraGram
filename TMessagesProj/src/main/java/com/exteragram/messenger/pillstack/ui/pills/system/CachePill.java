package com.exteragram.messenger.pillstack.ui.pills.system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.pillstack.core.PillType;
import com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity;
import com.exteragram.messenger.pillstack.ui.pills.BasePill;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.LaunchActivity;

@SuppressLint({"ViewConstructor"})
public class CachePill extends BasePill implements NotificationCenter.NotificationCenterDelegate {
    private static final AtomicLong lastKnownCacheSize = new AtomicLong(-1);
    private static float lastKnownProgress = -1.0f;
    private final AtomicBoolean calculating;
    private final ImageView iconView;
    private final LinearLayout layout;
    private final StorageProgressDrawable progressDrawable;
    private final AnimatedTextView textView;

    @Override 
    public long getRefreshInterval() {
        return 180000L;
    }

    public CachePill(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        this.calculating = new AtomicBoolean(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.layout = linearLayout;
        linearLayout.setOrientation(0);
        linearLayout.setGravity(17);
        linearLayout.setMinimumWidth(AndroidUtilities.dp(48.0f));
        linearLayout.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(8.0f), 0);
        addView(linearLayout, LayoutHelper.createFrame(-2, 28, (LocaleController.isRTL ? 3 : 5) | 16));
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        linearLayout.addView(imageView, LayoutHelper.createLinear(16, 16, 16, 0, 0, 6, 0));
        StorageProgressDrawable storageProgressDrawable = new StorageProgressDrawable(imageView);
        this.progressDrawable = storageProgressDrawable;
        imageView.setImageDrawable(storageProgressDrawable);
        AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true);
        this.textView = animatedTextView;
        animatedTextView.setTextSize(AndroidUtilities.dp(13.0f));
        animatedTextView.setTypeface(AndroidUtilities.bold());
        animatedTextView.setIncludeFontPadding(false);
        animatedTextView.adaptWidth = true;
        linearLayout.addView(animatedTextView, LayoutHelper.createLinear(-2, -2, 16));
        setLoadingTargetView(linearLayout);
        updateColors();
        ScaleStateListAnimator.apply(linearLayout);
        AtomicLong atomicLong = lastKnownCacheSize;
        if (atomicLong.get() != -1 && !isRefreshDue()) {
            setData(atomicLong.get(), lastKnownProgress, false);
        } else {
            imageView.setVisibility(8);
            animatedTextView.setVisibility(8);
        }
    }

    @Override 
    public int getPillId() {
        return PillType.CACHE.getId();
    }

    @Override 
    public void onUpdateData(boolean z) {
        boolean z2 = lastKnownCacheSize.get() == -1;
        if ((z || z2 || isRefreshDue()) && this.calculating.compareAndSet(false, true)) {
            if (z || z2) {
                CacheControlActivity.resetCalculatedTotalSIze();
            }
            startLoading();
            ImageLoader.getInstance().checkMediaPaths(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onUpdateData$2();
                }
            });
        }
    }

    public void lambda$onUpdateData$1(final Long l) {
        lastKnownCacheSize.set(l.longValue());
        CacheControlActivity.getDeviceTotalSize(new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$onUpdateData$0(l, (Long) obj, (Long) obj2);
            }
        });
    }

    public void lambda$onPillLongClicked$3() {
        onUpdateData(true);
    }

    public void openCacheSettings() {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment(new CacheControlActivity());
        }
    }

    @Override 
    public void updateColors() {
        int themedColor = getThemedColor(Theme.key_windowBackgroundWhiteBlackText, 0.75f);
        this.layout.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(14.0f), Theme.isCurrentThemeDark() ? getThemedColor(Theme.key_windowBackgroundWhite) : Theme.multAlpha(themedColor, 0.09f), Theme.multAlpha(themedColor, 0.1f)));
        this.textView.setTextColor(themedColor);
        this.progressDrawable.setColor(themedColor);
        updateLoadingColors();
    }

    public static class StorageProgressDrawable extends Drawable {
        private final AnimatedFloat animatedProgress;
        private int color;
        private final Paint paint;
        private float progress;
        private final RectF rectF;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        public StorageProgressDrawable(View view) {
            Paint paint = new Paint(1);
            this.paint = paint;
            this.rectF = new RectF();
            this.progress = 0.0f;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            this.animatedProgress = new AnimatedFloat(view, 650L, CubicBezierInterpolator.EASE_OUT_QUINT);
        }

        public void setProgress(float f, boolean z) {
            float fMax = Math.max(0.05f, Math.min(f, 1.0f));
            this.progress = fMax;
            if (!z) {
                this.animatedProgress.force(fMax);
            }
            invalidateSelf();
        }

        public void setColor(int i) {
            this.color = i;
            invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int iWidth = getBounds().width();
            int iHeight = getBounds().height();
            float fMin = Math.min(iWidth, iHeight) - AndroidUtilities.dp(2.0f);
            float f = iWidth;
            float f2 = (f - fMin) / 2.0f;
            float f3 = iHeight;
            float f4 = (f3 - fMin) / 2.0f;
            this.rectF.set(f2, f4, f2 + fMin, f4 + fMin);
            float f5 = this.animatedProgress.set(this.progress);
            this.paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            this.paint.setColor(this.color);
            this.paint.setAlpha(50);
            canvas.drawCircle(f / 2.0f, f3 / 2.0f, fMin / 2.0f, this.paint);
            this.paint.setAlpha(255);
            canvas.drawArc(this.rectF, -90.0f, f5 * 360.0f, false, this.paint);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            this.paint.setAlpha(i);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            this.paint.setColorFilter(colorFilter);
        }
    }
}
