package com.exteragram.messenger.preferences.chats.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.components.AltSeekbar;
import com.exteragram.messenger.preferences.components.CustomPreferenceCell;
import java.util.Objects;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

@SuppressLint({"ViewConstructor"})
public class SliderPreviewCell extends FrameLayout implements CustomPreferenceCell {
    private final int cellId;
    private int lastWidth;
    private OnSliderChangedListener listener;
    private final MessagesPreviewCell messagesCell;
    public final AltSeekbar seekBar;

    public interface OnSliderChangedListener {
        void onChanged(float f);
    }

    public SliderPreviewCell(INavigationLayout iNavigationLayout, Context context, int i, int i2, int i3, float f, String str, String str2, String str3, boolean z) {
        super(context);
        this.cellId = i;
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setWillNotDraw(false);
        AltSeekbar altSeekbar = new AltSeekbar(context, new AltSeekbar.OnDrag() { 
            @Override 
            public final void run(float f2) {
                SliderPreviewCell.this.lambda$new$0(f2);
            }
        }, i2, i3, str, str2, str3);
        this.seekBar = altSeekbar;
        addView(altSeekbar, LayoutHelper.createFrame(-1, -2.0f));
        if (z) {
            this.messagesCell = new MessagesPreviewCell(context, iNavigationLayout, 1);
        } else {
            this.messagesCell = new MessagesPreviewCell(context, iNavigationLayout);
        }
        this.messagesCell.setImportantForAccessibility(4);
        addView(this.messagesCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, ExteraConfig.getNewSliderStyle() ? 120.0f : 112.0f, 0.0f, 0.0f));
        setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        altSeekbar.setProgress(f);
    }

    public /* synthetic */ void lambda$new$0(float f) {
        OnSliderChangedListener onSliderChangedListener = this.listener;
        if (onSliderChangedListener != null) {
            onSliderChangedListener.onChanged(f);
        }
        invalidate();
    }

    public SliderPreviewCell setListener(OnSliderChangedListener onSliderChangedListener) {
        this.listener = onSliderChangedListener;
        return this;
    }

    public OnSliderChangedListener getListener() {
        return this.listener;
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        if (this.lastWidth != size) {
            this.lastWidth = size;
        }
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        this.lastWidth = -1;
        this.messagesCell.refreshMessages();
        this.seekBar.invalidate();
    }

    @Override 
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SliderPreviewCell) {
            SliderPreviewCell sliderPreviewCell = (SliderPreviewCell) obj;
            if (this.cellId == sliderPreviewCell.cellId && Objects.equals(this.messagesCell, sliderPreviewCell.messagesCell) && Objects.equals(this.seekBar, sliderPreviewCell.seekBar) && this.lastWidth == sliderPreviewCell.lastWidth) {
                return true;
            }
        }
        return false;
    }
}
