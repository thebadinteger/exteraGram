package com.exteragram.messenger.appicons.ui.components;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.appicons.AppIcon;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

/* JADX INFO: loaded from: classes4.dex */
@SuppressLint({"ViewConstructor"})
public class AppIconCell extends LinearLayout implements Theme.Colorable {
    private final CheckBox2 checkBox;
    private AppIcon icon;
    private final FrameLayout previewContainer;
    private final AppIconPreviewView previewView;
    private final Theme.ResourcesProvider resourcesProvider;
    private float selection;
    private ValueAnimator selectionAnimator;
    private final Paint selectionPaint;
    private final TextView titleView;

    @Override // org.telegram.ui.ActionBar.Theme.Colorable
    public /* bridge */ /* synthetic */ int[] getColorKeys() {
        return null;
    }

    public AppIconCell(Context context, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.selectionPaint = new Paint(1);
        this.resourcesProvider = resourcesProvider;
        setWillNotDraw(false);
        setClipChildren(false);
        setOrientation(1);
        setGravity(1);
        setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        this.previewContainer = frameLayout;
        frameLayout.setClipChildren(false);
        AppIconPreviewView appIconPreviewView = new AppIconPreviewView(context, resourcesProvider);
        this.previewView = appIconPreviewView;
        frameLayout.addView(appIconPreviewView, LayoutHelper.createFrame(-1, -1.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 21, new Theme.ResourcesProvider() { // from class: com.exteragram.messenger.appicons.ui.components.AppIconCell$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
            public final int getColor(int i) {
                return AppIconCell.this.lambda$new$0(resourcesProvider, i);
            }
        });
        this.checkBox = checkBox2;
        checkBox2.setColor(Theme.key_featuredStickers_addButton, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        checkBox2.setDrawUnchecked(false);
        checkBox2.setDrawBackgroundAsArc(4);
        checkBox2.setProgressDelegate(new CheckBoxBase.ProgressDelegate() { // from class: com.exteragram.messenger.appicons.ui.components.AppIconCell$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.CheckBoxBase.ProgressDelegate
            public final void setProgress(float f) {
                AppIconCell.this.lambda$new$1(f);
            }
        });
        frameLayout.addView(checkBox2, LayoutHelper.createFrame(24, 24.0f, 85, 0.0f, 0.0f, 1.0f, 1.0f));
        addView(frameLayout, LayoutHelper.createLinear(68, 68, 1));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setMaxLines(2);
        textView.setGravity(17);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTypeface(AndroidUtilities.regular());
        textView.setTextSize(1, 13.0f);
        textView.setLineSpacing(0.0f, 0.95f);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        addView(textView, LayoutHelper.createLinear(-1, -2, 1, 0, 8, 0, 0));
        ScaleStateListAnimator.apply(this, 0.05f, 1.2f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$new$0(Theme.ResourcesProvider resourcesProvider, int i) {
        if (i == Theme.key_windowBackgroundWhite) {
            return getSelectionColor();
        }
        return Theme.getColor(i, resourcesProvider);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(float f) {
        this.previewView.setPreviewScale(1.0f - (f * 0.08f));
    }

    @Override // org.telegram.ui.ActionBar.Theme.Colorable
    public void updateColors() {
        this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider));
        invalidate();
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int iMax = Math.max(AndroidUtilities.dp(40.0f), Math.min(AndroidUtilities.dp(68.0f), (View.MeasureSpec.getSize(i) - getPaddingLeft()) - getPaddingRight()));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.previewContainer.getLayoutParams();
        if (layoutParams.width != iMax) {
            layoutParams.height = iMax;
            layoutParams.width = iMax;
        }
        super.onMeasure(i, i2);
    }

    private int getSelectionColor() {
        return ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider), Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider), this.selection * 0.07f);
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onDraw(Canvas canvas) {
        if (this.selection <= 0.0f) {
            return;
        }
        this.selectionPaint.setColor(getSelectionColor());
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), getWidth() - AndroidUtilities.dp(2.0f), getHeight() - AndroidUtilities.dp(2.0f));
        canvas.drawRoundRect(rectF, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), this.selectionPaint);
    }

    public void set(AppIcon appIcon, boolean z, boolean z2) {
        this.icon = appIcon;
        this.previewView.setIcon(appIcon);
        this.titleView.setText(appIcon.getTitle());
        setSelected(z, z2);
    }

    public AppIcon getIcon() {
        return this.icon;
    }

    private void setSelected(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
        float f = z ? 1.0f : 0.0f;
        ValueAnimator valueAnimator = this.selectionAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.selectionAnimator = null;
        }
        if (!z2) {
            this.selection = f;
            invalidate();
            return;
        }
        float f2 = this.selection;
        if (f2 == f) {
            return;
        }
        ValueAnimator duration = ValueAnimator.ofFloat(f2, f).setDuration(220L);
        this.selectionAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.selectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.exteragram.messenger.appicons.ui.components.AppIconCell$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                AppIconCell.this.lambda$setSelected$2(valueAnimator2);
            }
        });
        this.selectionAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setSelected$2(ValueAnimator valueAnimator) {
        this.selection = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public static class Factory extends UItem.UItemFactory<AppIconCell> {
        static {
            UItem.UItemFactory.setup(new Factory());
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public AppIconCell createView(Context context, RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
            return new AppIconCell(context, resourcesProvider);
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public void bindView(View view, UItem uItem, boolean z, UniversalAdapter universalAdapter, UniversalRecyclerView universalRecyclerView) {
            if (view instanceof AppIconCell) {
                AppIconCell appIconCell = (AppIconCell) view;
                Object obj = uItem.object;
                if (obj instanceof AppIcon) {
                    AppIcon appIcon = (AppIcon) obj;
                    appIconCell.set(appIcon, uItem.checked, appIconCell.getIcon() == appIcon);
                }
            }
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean equals(UItem uItem, UItem uItem2) {
            return uItem.object == uItem2.object;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean contentsEquals(UItem uItem, UItem uItem2) {
            return uItem.object == uItem2.object && uItem.checked == uItem2.checked;
        }

        public static UItem asAppIcon(AppIcon appIcon, boolean z) {
            UItem uItemOfFactory = UItem.ofFactory(Factory.class);
            uItemOfFactory.object = appIcon;
            uItemOfFactory.checked = z;
            uItemOfFactory.spanCount = 1;
            uItemOfFactory.transparent = true;
            return uItemOfFactory;
        }
    }
}
