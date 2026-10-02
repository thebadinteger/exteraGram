package com.exteragram.messenger.components;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import com.exteragram.messenger.utils.MarkdownUtils;
import java.util.ArrayList;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.tl.TL_iv;

/* JADX INFO: loaded from: classes4.dex */
public abstract class PreformattedScrollView extends HorizontalScrollView {
    private Group group;

    public PreformattedScrollView(Context context) {
        super(context);
    }

    public void setBlock(TL_iv.PageBlock pageBlock) {
        MarkdownUtils.PreformattedChunk preformattedChunk = pageBlock instanceof MarkdownUtils.PreformattedChunk ? (MarkdownUtils.PreformattedChunk) pageBlock : null;
        Group group = this.group;
        if (group != null) {
            group.remove(this);
        }
        Group group2 = preformattedChunk != null ? preformattedChunk.scrollGroup : null;
        this.group = group2;
        if (group2 != null && isAttachedToWindow()) {
            this.group.add(this);
        }
        setHorizontalScrollBarEnabled(preformattedChunk == null);
        boolean z = preformattedChunk != null && preformattedChunk.joinsPrevious;
        boolean z2 = preformattedChunk != null && preformattedChunk.joinsNext;
        setPadding(0, z ? 0 : AndroidUtilities.dp(8.0f), 0, z2 ? 0 : AndroidUtilities.dp(8.0f));
        if (getChildCount() > 0) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getChildAt(0).getLayoutParams();
            marginLayoutParams.topMargin = z ? 0 : AndroidUtilities.dp(12.0f);
            marginLayoutParams.bottomMargin = AndroidUtilities.dp(z2 ? 4.0f : 12.0f);
        }
    }

    public int adjustContentWidth(int i) {
        Group group = this.group;
        return group != null ? group.adjustContentWidth(this, i) : i;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.group != null && motionEvent.getActionMasked() == 0) {
            this.group.driver = this;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        Group group = this.group;
        if (group == null || group.driver != this) {
            return;
        }
        this.group.scrollTo(this, i);
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        Group group = this.group;
        if (group != null) {
            scrollTo(group.scrollX, getScrollY());
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Group group = this.group;
        if (group != null) {
            group.add(this);
            scrollTo(this.group.scrollX, getScrollY());
            if (getChildCount() <= 0 || getChildAt(0).getMeasuredWidth() >= this.group.contentWidth) {
                return;
            }
            View childAt = getChildAt(0);
            if (childAt != null) {
                AndroidUtilities.runOnUIThread(childAt::requestLayout);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Group group = this.group;
        if (group != null) {
            group.remove(this);
        }
    }

    public static final class Group {
        private int contentWidth;
        private PreformattedScrollView driver;
        private int scrollX;
        private final ArrayList<PreformattedScrollView> views = new ArrayList<>();

        /* JADX INFO: Access modifiers changed from: private */
        public void add(PreformattedScrollView preformattedScrollView) {
            if (this.views.contains(preformattedScrollView)) {
                return;
            }
            this.views.add(preformattedScrollView);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void remove(PreformattedScrollView preformattedScrollView) {
            this.views.remove(preformattedScrollView);
            if (this.driver == preformattedScrollView) {
                this.driver = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void scrollTo(PreformattedScrollView preformattedScrollView, int i) {
            this.scrollX = i;
            for (int i2 = 0; i2 < this.views.size(); i2++) {
                PreformattedScrollView preformattedScrollView2 = this.views.get(i2);
                if (preformattedScrollView2 != preformattedScrollView) {
                    preformattedScrollView2.scrollTo(i, preformattedScrollView2.getScrollY());
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int adjustContentWidth(PreformattedScrollView preformattedScrollView, int i) {
            if (i > this.contentWidth) {
                this.contentWidth = i;
                for (int i2 = 0; i2 < this.views.size(); i2++) {
                    PreformattedScrollView preformattedScrollView2 = this.views.get(i2);
                    if (preformattedScrollView2 != preformattedScrollView && preformattedScrollView2.getChildCount() > 0) {
                        View childAt = preformattedScrollView2.getChildAt(0);
                        if (childAt != null) {
                            AndroidUtilities.runOnUIThread(childAt::requestLayout);
                        }
                    }
                }
            }
            return this.contentWidth;
        }
    }
}
