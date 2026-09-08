package com.exteragram.messenger.icons.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.SparseIntArray;
import android.view.View;
import com.exteragram.messenger.icons.ExteraResources;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.icons.IconPack;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;

public class IconPackPreviewView extends View {
    private static final int[] DEFAULT_ICONS = {R.drawable.msg_sticker, R.drawable.msg_link2, R.drawable.msg_pin, R.drawable.msg_photos};
    private float animationProgress;
    private ValueAnimator animator;
    private boolean attached;
    private final List<Integer> availableIconIds;
    private final Runnable changeRunnable;
    private final int circCenterSize;
    private final int circInnerIconSize;
    private final int circInnerRadius;
    private final int circOuterIconSize;
    private final int circOuterRadius;
    private final int cols;
    private int count;
    private final int[] currentIconIds;
    private final Drawable[] currentIcons;
    private IconPack currentPack;
    private final int gap;
    private final int iconSize;
    private boolean isCircularMode;
    private final Drawable[] nextIcons;
    private final Random random;
    private int refreshTimeMilliseconds;
    private final int rows;
    private boolean shouldAnimate;

    public IconPackPreviewView(Context context) {
        super(context);
        this.availableIconIds = new ArrayList();
        this.currentIcons = new Drawable[20];
        this.nextIcons = new Drawable[20];
        this.currentIconIds = new int[20];
        this.random = new Random();
        this.rows = 2;
        this.cols = 2;
        this.count = 4;
        this.iconSize = AndroidUtilities.dp(20.0f);
        this.gap = AndroidUtilities.dp(4.0f);
        this.isCircularMode = false;
        this.circInnerRadius = AndroidUtilities.dp(46.0f);
        this.circInnerIconSize = AndroidUtilities.dp(24.0f);
        this.circOuterRadius = AndroidUtilities.dp(72.0f);
        this.circOuterIconSize = AndroidUtilities.dp(20.0f);
        this.circCenterSize = AndroidUtilities.dp(36.0f);
        this.animationProgress = 0.0f;
        this.refreshTimeMilliseconds = 5000;
        this.changeRunnable = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.animateToNext();
            }
        };
    }

    public void setCircularMode(boolean z) {
        this.isCircularMode = z;
        if (z) {
            this.count = 13;
        }
        requestLayout();
        IconPack iconPack = this.currentPack;
        if (iconPack != null) {
            setIconPack(iconPack);
        }
    }

    public void setRefreshTime(int i) {
        this.refreshTimeMilliseconds = i;
    }

    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        if (this.isCircularMode) {
            setMeasuredDimension(View.getDefaultSize(getSuggestedMinimumWidth(), i), View.resolveSize(((this.circOuterRadius + (this.circOuterIconSize / 2)) * 2) + AndroidUtilities.dp(8.0f), i2));
        } else {
            setMeasuredDimension(View.resolveSize((this.iconSize * 2) + this.gap + getPaddingLeft() + getPaddingRight(), i), View.resolveSize((this.iconSize * 2) + this.gap + getPaddingTop() + getPaddingBottom(), i2));
        }
    }

    public void setIconPack(IconPack iconPack) {
        if (this.currentPack != iconPack || this.availableIconIds.isEmpty() || this.availableIconIds.size() < this.count) {
            this.currentPack = iconPack;
            this.availableIconIds.clear();
            if (iconPack.isBase()) {
                for (int i : DEFAULT_ICONS) {
                    this.availableIconIds.add(Integer.valueOf(i));
                }
                this.shouldAnimate = false;
            } else {
                ArrayList arrayList = new ArrayList();
                HashSet hashSet = new HashSet();
                for (Map.Entry<String, String> entry : iconPack.getIcons().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (!hashSet.contains(value)) {
                        hashSet.add(value);
                        arrayList.add(key);
                    }
                }
                if (arrayList.isEmpty()) {
                    this.availableIconIds.add(Integer.valueOf(R.drawable.msg_media));
                } else {
                    ConcurrentHashMap<String, Integer> systemIcons = IconManager.INSTANCE.getSystemIcons();
                    if (!systemIcons.isEmpty()) {
                        int size = arrayList.size();
                        int i2 = 0;
                        while (i2 < size) {
                            Object obj = arrayList.get(i2);
                            i2++;
                            Integer num = systemIcons.get((String) obj);
                            if (num != null) {
                                this.availableIconIds.add(num);
                            }
                        }
                    } else {
                        int size2 = arrayList.size();
                        int i3 = 0;
                        int i4 = 0;
                        while (i4 < size2) {
                            Object obj2 = arrayList.get(i4);
                            i4++;
                            int identifier = getResources().getIdentifier((String) obj2, "drawable", getContext().getPackageName());
                            if (identifier != 0) {
                                this.availableIconIds.add(Integer.valueOf(identifier));
                            }
                            i3++;
                            if (i3 > 30) {
                                break;
                            }
                        }
                    }
                }
                this.shouldAnimate = hashSet.size() >= this.count * 2;
            }
            loadIcons(false);
        }
    }

    private void loadIcons(final boolean z) {
        final IconPack iconPack = this.currentPack;
        final ArrayList arrayList = new ArrayList(this.availableIconIds);
        final int[] iArr = z ? (int[]) this.currentIconIds.clone() : null;
        boolean zIsBase = iconPack.isBase();
        int iMin = this.count;
        if (!zIsBase) {
            iMin = Math.min(iMin, arrayList.size());
        }
        final int i = iMin;
        Utilities.globalQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadIcons$1(arrayList, i, iArr, iconPack, z);
            }
        });
    }

    public void lambda$loadIcons$0(IconPack iconPack, boolean z, Drawable[] drawableArr, List list, int[] iArr) {
        if (this.currentPack != iconPack) {
            return;
        }
        int i = 0;
        if (z) {
            Drawable[] drawableArr2 = this.nextIcons;
            System.arraycopy(drawableArr, 0, drawableArr2, 0, drawableArr2.length);
            startTransition(list);
            return;
        }
        while (true) {
            Drawable[] drawableArr3 = this.currentIcons;
            if (i >= drawableArr3.length) {
                break;
            }
            this.currentIconIds[i] = iArr[i];
            drawableArr3[i] = drawableArr[i];
            i++;
        }
        invalidate();
        if (this.attached && this.shouldAnimate) {
            scheduleNext();
        } else {
            removeCallbacks(this.changeRunnable);
        }
    }

    private List<Integer> pickRandomIconIdsInternal(List<Integer> list, int i, int[] iArr) {
        ArrayList arrayList = new ArrayList();
        if (!list.isEmpty()) {
            ArrayList arrayList2 = new ArrayList(list);
            if (iArr != null) {
                for (int i2 : iArr) {
                    arrayList2.remove(Integer.valueOf(i2));
                }
            }
            if (arrayList2.isEmpty()) {
                arrayList2.addAll(list);
            }
            HashSet hashSet = new HashSet();
            int size = arrayList2.size();
            for (int i3 = 0; hashSet.size() < i && hashSet.size() < size && i3 < i * 4; i3++) {
                hashSet.add(Integer.valueOf(this.random.nextInt(size)));
            }
            if (hashSet.size() < i && hashSet.size() < size) {
                for (int i4 = 0; i4 < size && hashSet.size() < i; i4++) {
                    hashSet.add(Integer.valueOf(i4));
                }
            }
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                arrayList.add((Integer) arrayList2.get(((Integer) it.next()).intValue()));
            }
        }
        return arrayList;
    }

    private Drawable getIconDrawableInternal(IconPack iconPack, int i) {
        Drawable packIconDrawable = null;
        if (i == 0) {
            return null;
        }
        if (iconPack.isBase()) {
            SparseIntArray preinstalledMap = iconPack.getPreinstalledMap();
            int i2 = preinstalledMap != null ? preinstalledMap.get(i, -1) : -1;
            if (i2 != -1) {
                packIconDrawable = getResources().getDrawable(i2);
            }
        } else {
            packIconDrawable = IconManager.INSTANCE.getPackIconDrawable(iconPack, i);
        }
        if (packIconDrawable == null) {
            if (getResources() instanceof ExteraResources) {
                try {
                    packIconDrawable = ((ExteraResources) getResources()).getOriginalDrawable(i);
                } catch (Exception unused) {
                    packIconDrawable = getResources().getDrawable(i);
                }
            } else {
                packIconDrawable = getResources().getDrawable(i);
            }
        }
        if (packIconDrawable == null) {
            return packIconDrawable;
        }
        Drawable drawableMutate = packIconDrawable.mutate();
        drawableMutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        return drawableMutate;
    }

    public void scheduleNext() {
        removeCallbacks(this.changeRunnable);
        if (this.attached && this.shouldAnimate) {
            postDelayed(this.changeRunnable, this.refreshTimeMilliseconds);
        }
    }

    public void animateToNext() {
        if (this.attached && this.shouldAnimate) {
            loadIcons(true);
        }
    }

    private void startTransition(final List<Integer> list) {
        if (this.attached) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setDuration(300L);
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { 
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$startTransition$2(valueAnimator2);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() { 
                private boolean cancelled;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    this.cancelled = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (IconPackPreviewView.this.animator == animator) {
                        IconPackPreviewView.this.animator = null;
                    }
                    if (this.cancelled || !IconPackPreviewView.this.attached) {
                        return;
                    }
                    for (int i = 0; i < IconPackPreviewView.this.currentIcons.length; i++) {
                        int size = list.size();
                        IconPackPreviewView iconPackPreviewView = IconPackPreviewView.this;
                        if (i < size) {
                            iconPackPreviewView.currentIconIds[i] = ((Integer) list.get(i)).intValue();
                        } else {
                            iconPackPreviewView.currentIconIds[i] = 0;
                        }
                        IconPackPreviewView.this.currentIcons[i] = IconPackPreviewView.this.nextIcons[i];
                        IconPackPreviewView.this.nextIcons[i] = null;
                    }
                    IconPackPreviewView.this.animationProgress = 0.0f;
                    IconPackPreviewView.this.invalidate();
                    IconPackPreviewView.this.scheduleNext();
                }
            });
            this.animator.start();
        }
    }

    public /* synthetic */ void lambda$startTransition$2(ValueAnimator valueAnimator) {
        this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        if (this.shouldAnimate) {
            scheduleNext();
        }
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        this.attached = false;
        removeCallbacks(this.changeRunnable);
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            this.animator = null;
            valueAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.isCircularMode) {
            drawCircularLayout(canvas);
            return;
        }
        if (this.count == 4) {
            int i = 0;
            for (Drawable drawable : this.currentIcons) {
                if (drawable != null) {
                    i++;
                }
            }
            if (i == 1) {
                int iDp = AndroidUtilities.dp(24.0f);
                drawIcon(canvas, 0, (getWidth() - iDp) / 2, (getHeight() - iDp) / 2, iDp, 1.0f);
                return;
            }
            if (i == 2) {
                int iDp2 = AndroidUtilities.dp(20.0f);
                int iDp3 = AndroidUtilities.dp(4.0f);
                int height = (getHeight() - iDp2) / 2;
                drawIcon(canvas, 0, 0, height, iDp2, 1.0f);
                drawIcon(canvas, 1, iDp2 + iDp3, height, iDp2, 1.0f);
                return;
            }
            if (i == 3) {
                int iDp4 = AndroidUtilities.dp(20.0f);
                int iDp5 = AndroidUtilities.dp(4.0f);
                drawIcon(canvas, 0, 0, 0, iDp4, 1.0f);
                int i2 = iDp4 + iDp5;
                drawIcon(canvas, 1, i2, 0, iDp4, 1.0f);
                drawIcon(canvas, 2, (getWidth() - iDp4) / 2, i2, iDp4, 1.0f);
                return;
            }
            int iDp6 = AndroidUtilities.dp(20.0f);
            int iDp7 = iDp6 + AndroidUtilities.dp(4.0f);
            drawIcon(canvas, 0, 0, 0, iDp6, 1.0f);
            drawIcon(canvas, 1, iDp7, 0, iDp6, 1.0f);
            drawIcon(canvas, 2, 0, iDp7, iDp6, 1.0f);
            drawIcon(canvas, 3, iDp7, iDp7, iDp6, 1.0f);
            return;
        }
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int i3 = 0;
        int i4 = 0;
        while (i4 < 2) {
            int i5 = i3;
            for (int i6 = 0; i6 < 2 && i5 < this.currentIcons.length; i6++) {
                int i7 = this.iconSize;
                int i8 = this.gap;
                drawIcon(canvas, i5, ((i7 + i8) * i6) + paddingLeft, paddingTop + ((i8 + i7) * i4), i7, 1.0f);
                i5++;
            }
            i4++;
            i3 = i5;
        }
    }

    private void drawCircularLayout(Canvas canvas) {
        int i;
        IconPackPreviewView iconPackPreviewView = this;
        int width = iconPackPreviewView.getWidth() / 2;
        int height = iconPackPreviewView.getHeight() / 2;
        int i2 = iconPackPreviewView.circCenterSize;
        iconPackPreviewView.drawIcon(canvas, 0, width - (i2 / 2), height - (i2 / 2), i2, 1.0f);
        int i3 = 0;
        int i4 = 0;
        while (i4 < 6 && (i = 1 + i4) < iconPackPreviewView.currentIcons.length) {
            double radians = Math.toRadians(((double) i4) * 60.0d);
            int iCos = ((int) (((double) width) + (((double) iconPackPreviewView.circInnerRadius) * Math.cos(radians)))) - (iconPackPreviewView.circInnerIconSize / 2);
            int iSin = (int) (((double) height) + (((double) iconPackPreviewView.circInnerRadius) * Math.sin(radians)));
            int i5 = iconPackPreviewView.circInnerIconSize;
            iconPackPreviewView.drawIcon(canvas, i, iCos, iSin - (i5 / 2), i5, 0.7f);
            i4 = i;
        }
        while (i3 < 6) {
            int i6 = 7 + i3;
            if (i6 >= iconPackPreviewView.currentIcons.length) {
                return;
            }
            double radians2 = Math.toRadians((((double) i3) * 60.0d) + 30.0d);
            int iCos2 = ((int) (((double) width) + (((double) iconPackPreviewView.circOuterRadius) * Math.cos(radians2)))) - (iconPackPreviewView.circOuterIconSize / 2);
            int iSin2 = (int) (((double) height) + (((double) iconPackPreviewView.circOuterRadius) * Math.sin(radians2)));
            int i7 = iconPackPreviewView.circOuterIconSize;
            iconPackPreviewView.drawIcon(canvas, i6, iCos2, iSin2 - (i7 / 2), i7, 0.3f);
            i3++;
            iconPackPreviewView = this;
        }
    }

    private void drawIcon(Canvas canvas, int i, int i2, int i3, int i4, float f) {
        if (i >= this.currentIcons.length) {
            return;
        }
        canvas.save();
        canvas.translate(i2, i3);
        Drawable drawable = this.currentIcons[i];
        Drawable drawable2 = this.nextIcons[i];
        if (drawable != null) {
            drawable.setBounds(0, 0, i4, i4);
            drawable.setAlpha((int) ((1.0f - this.animationProgress) * f * 255.0f));
            drawable.draw(canvas);
        }
        if (drawable2 != null) {
            drawable2.setBounds(0, 0, i4, i4);
            drawable2.setAlpha((int) (this.animationProgress * f * 255.0f));
            drawable2.draw(canvas);
        }
        canvas.restore();
    }
}
