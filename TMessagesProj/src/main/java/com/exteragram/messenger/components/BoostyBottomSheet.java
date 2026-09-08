package com.exteragram.messenger.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.api.dto.BoostySubscriberDTO;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public abstract class BoostyBottomSheet extends BottomSheet {
    private final int ITEM_HEIGHT;
    private final int PRIMARY_COLOR;
    private final Runnable autoScrollRunnable;
    public ButtonWithCounterView buttonView;
    private int contentHeight;
    private int currentAutoScrollPosition;
    public LinkSpanDrawable.LinksTextView descriptionView;
    private boolean isUserScrolling;
    private final RecyclerListView listView;
    private Runnable resumeScrollRunnable;
    private final LinearSnapHelper snapHelper;
    private final StarParticlesView.Drawable starDrawable;
    public TextView titleView;
    public FrameLayout topView;

    public abstract void onButtonClick();

    void lambda$onScrollStateChanged$0() {
            BoostyBottomSheet.this.isUserScrolling = false;
            BoostyBottomSheet.this.listView.postDelayed(BoostyBottomSheet.this.autoScrollRunnable, 1500L);
        }
    }

    public /* synthetic */ void lambda$new$0(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        updateScales();
    }

    public /* synthetic */ void lambda$new$1(View view) {
        dismiss();
        onButtonClick();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean isTouchOutside(float f, float f2) {
        if (f2 < this.containerView.getTop() - this.backgroundPaddingTop || f2 > this.containerView.getBottom()) {
            return super.isTouchOutside(f, f2);
        }
        return false;
    }

    public void updateScales() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null) {
            return;
        }
        int height = recyclerListView.getHeight() / 2;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            float fMin = Math.min(Math.abs(height - ((childAt.getTop() + childAt.getBottom()) / 2)) / (this.listView.getHeight() / 2.0f), 1.0f);
            float f = 1.0f - (fMin * 0.3f);
            childAt.setScaleX(f);
            childAt.setScaleY(f);
            childAt.setAlpha(((1.0f - fMin) * 0.7f) + 0.3f);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.textView = (TextView) view;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    public void dismiss() {
        super.dismiss();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.removeCallbacks(this.autoScrollRunnable);
            Runnable runnable = this.resumeScrollRunnable;
            if (runnable != null) {
                this.listView.removeCallbacks(runnable);
            }
        }
    }
}
