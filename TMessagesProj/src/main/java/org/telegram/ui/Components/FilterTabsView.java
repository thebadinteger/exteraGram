package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.TabIconsMode;
import com.exteragram.messenger.utils.ui.FolderIcons;
import com.google.android.material.timepicker.TimeModel;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Stories.recorder.HintView2;

@SuppressLint({"ViewConstructor"})
public class FilterTabsView extends FrameLayout {
    private final Property<FilterTabsView, Float> COLORS;
    private int aActiveTextColorKey;
    private int aBackgroundColorKey;
    private int aTabLineColorKey;
    private int aUnactiveTextColorKey;
    private int activeTextColorKey;
    private final ListAdapter adapter;
    private int additionalTabWidth;
    private int allTabsWidth;
    private boolean animatingIndicator;
    private float animatingIndicatorProgress;
    private final Runnable animationRunnable;
    private float animationTime;
    private float animationValue;
    private int backgroundColorKey;
    BlurredBackgroundDrawable blurredBackgroundDrawable;
    private final Path clipPath;
    private AnimatorSet colorChangeAnimator;
    private final Paint counterPaint;
    private int currentPosition;
    private FilterTabsViewDelegate delegate;
    private final Paint deletePaint;
    private float editingAnimationProgress;
    private boolean editingForwardAnimation;
    private float editingStartAnimationProgress;
    private ColorFilter emojiColorFilter;
    private final SparseIntArray idToPosition;
    private boolean ignoreLayout;
    private final CubicBezierInterpolator interpolator;
    private boolean invalidated;
    private boolean isEditing;
    private boolean isStaticAllChats;
    DefaultItemAnimator itemAnimator;
    private long lastAnimationTime;
    private long lastEditingAnimationTime;
    private final LinearLayoutManager layoutManager;
    private final RecyclerListView listView;
    private final int listViewPaddingH;
    private Drawable lockDrawable;
    private int lockDrawableColor;
    private int manualScrollingToId;
    private int manualScrollingToPosition;
    private int oldAnimatedTab;
    private boolean orderChanged;
    private final SparseIntArray positionToCount;
    private final SparseIntArray positionToId;
    private final SparseIntArray positionToStableId;
    private final SparseIntArray positionToWidth;
    private final SparseIntArray positionToX;
    private int prevLayoutWidth;
    private int previousId;
    private int previousPosition;
    private final Theme.ResourcesProvider resourcesProvider;
    private int scrollingToChild;
    private int selectedTabId;
    private int selectorColorKey;
    private final GradientDrawable selectorDrawable;
    private int tabLineColorKey;
    private final ArrayList<Tab> tabs;
    private final TextPaint textCounterPaint;
    public final TextPaint textPaint;
    private int unactiveTextColorKey;

    public interface FilterTabsViewDelegate {
        boolean canPerformActions();

        boolean didSelectTab(TabView tabView, boolean z);

        int getTabCounter(int i);

        boolean isTabMenuVisible();

        void onDeletePressed(int i);

        void onPageReorder(int i, int i2);

        void onPageScrolled(float f);

        void onPageSelected(Tab tab, boolean z);

        void onSamePageSelected();

        default void onTabSelected(Tab tab, boolean z, boolean z2) {
        }
    }

    public static @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean zDrawChild = super.drawChild(canvas, view, j);
        if (view == this.listView) {
            drawSelector(canvas);
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long jMin = Math.min(17L, jElapsedRealtime - this.lastEditingAnimationTime);
        this.lastEditingAnimationTime = jElapsedRealtime;
        boolean z = this.isEditing;
        boolean z2 = false;
        boolean z3 = true;
        if (z || this.editingAnimationProgress != 0.0f) {
            boolean z4 = this.editingForwardAnimation;
            float f = this.editingAnimationProgress;
            if (z4) {
                boolean z5 = f <= 0.0f;
                float f2 = f + (jMin / 420.0f);
                this.editingAnimationProgress = f2;
                if (!z && z5 && f2 >= 0.0f) {
                    this.editingAnimationProgress = 0.0f;
                }
                if (this.editingAnimationProgress >= 1.0f) {
                    this.editingAnimationProgress = 1.0f;
                    this.editingForwardAnimation = false;
                }
            } else {
                z2 = f >= 0.0f;
                float f3 = f - (jMin / 420.0f);
                this.editingAnimationProgress = f3;
                if (!z && z2 && f3 <= 0.0f) {
                    this.editingAnimationProgress = 0.0f;
                }
                if (this.editingAnimationProgress <= -1.0f) {
                    this.editingAnimationProgress = -1.0f;
                    this.editingForwardAnimation = true;
                }
            }
            z2 = true;
        }
        if (z) {
            float f4 = this.editingStartAnimationProgress;
            if (f4 < 1.0f) {
                float f5 = f4 + (jMin / 180.0f);
                this.editingStartAnimationProgress = f5;
                if (f5 > 1.0f) {
                    this.editingStartAnimationProgress = 1.0f;
                }
            } else {
                z3 = z2;
            }
        } else if (z) {
            z3 = z2;
        } else {
            float f6 = this.editingStartAnimationProgress;
            if (f6 > 0.0f) {
                float f7 = f6 - (jMin / 180.0f);
                this.editingStartAnimationProgress = f7;
                if (f7 < 0.0f) {
                    this.editingStartAnimationProgress = 0.0f;
                }
            } else {
                z3 = z2;
            }
        }
        if (z3) {
            this.listView.invalidateViews();
            this.listView.invalidate();
            invalidate();
        }
        return zDrawChild;
    }

    /* JADX WARN: Code duplicated, block: B:18:0x0087  */
    private void drawSelector(Canvas canvas) {
        RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition;
        int i;
        int i2;
        float fLerp;
        int paddingTab;
        float fLerp2;
        float x;
        float fLerp3;
        float measuredWidth;
        int measuredHeight = getMeasuredHeight();
        this.selectorDrawable.setAlpha((int) (this.listView.getAlpha() * 255.0f));
        if (this.animatingIndicator || this.manualScrollingToPosition != -1) {
            int iFindFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
            if (iFindFirstVisibleItemPosition == -1 || (viewHolderFindViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(iFindFirstVisibleItemPosition)) == null) {
                fLerp2 = 0.0f;
                x = 0.0f;
            } else {
                if (this.animatingIndicator) {
                    i = this.previousPosition;
                    i2 = this.currentPosition;
                } else {
                    i = this.currentPosition;
                    i2 = this.manualScrollingToPosition;
                }
                int i3 = this.positionToX.get(i);
                int i4 = this.positionToX.get(i2);
                int i5 = this.positionToWidth.get(i);
                int i6 = this.positionToWidth.get(i2);
                float f = this.positionToCount.get(i) != 0 ? 1.0f : 0.0f;
                float f2 = this.positionToCount.get(i2) != 0 ? 1.0f : 0.0f;
                if (this.additionalTabWidth != 0) {
                    fLerp = AndroidUtilities.lerp(i3, i4, this.animatingIndicatorProgress);
                    paddingTab = FolderIcons.getPaddingTab();
                } else {
                    fLerp = AndroidUtilities.lerp(i3, i4, this.animatingIndicatorProgress) - (this.positionToX.get(iFindFirstVisibleItemPosition) - viewHolderFindViewHolderForAdapterPosition.itemView.getLeft());
                    paddingTab = FolderIcons.getPaddingTab();
                }
                float f3 = fLerp + (paddingTab / 2.0f);
                fLerp2 = AndroidUtilities.lerp(i5, i6, this.animatingIndicatorProgress);
                AndroidUtilities.lerp(f, f2, this.animatingIndicatorProgress);
                x = f3;
            }
        } else {
            RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(this.currentPosition);
            if (viewHolderFindViewHolderForAdapterPosition2 != null) {
                TabView tabView = (TabView) viewHolderFindViewHolderForAdapterPosition2.itemView;
                if (tabView.animateTabWidth) {
                    fLerp3 = AndroidUtilities.lerp(tabView.animateFromTabWidth, tabView.tabWidth, tabView.changeProgress);
                } else {
                    fLerp3 = tabView.tabWidth;
                }
                fLerp2 = Math.max(AndroidUtilities.dp(16.0f), fLerp3);
                if (tabView.animateTabWidth) {
                    measuredWidth = AndroidUtilities.lerp(tabView.animateFromTabWidth + AndroidUtilities.dp(20.0f), tabView.getMeasuredWidth(), tabView.changeProgress);
                } else {
                    measuredWidth = tabView.getMeasuredWidth();
                }
                x = (int) (tabView.getX() + ((measuredWidth - fLerp2) / 2.0f));
                float unused = tabView.tabCounterVisible;
            } else {
                fLerp2 = 0.0f;
                x = 0.0f;
            }
        }
        if (fLerp2 != 0.0f) {
            canvas.save();
            canvas.translate(this.listView.getTranslationX(), 0.0f);
            canvas.scale(this.listView.getScaleX(), 1.0f, this.listView.getPivotX() + this.listView.getX(), this.listView.getPivotY());
            float f4 = this.additionalTabWidth / 2.0f;
            int iDp = (measuredHeight / 2) - AndroidUtilities.dp(14.0f);
            this.selectorDrawable.setBounds((int) ((x - AndroidUtilities.dp(12.5f)) - f4), iDp, (int) (x + fLerp2 + AndroidUtilities.dp(12.5f) + f4), AndroidUtilities.dp(28.0f) + iDp);
            this.selectorDrawable.setAlpha(31);
            this.selectorDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.clipPath.rewind();
        this.clipPath.addRoundRect(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), i - AndroidUtilities.dp(9.0f), i2 - AndroidUtilities.dp(9.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Path.Direction.CW);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(this.clipPath);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public void updateColors() {
        BlurredBackgroundDrawable blurredBackgroundDrawable = this.blurredBackgroundDrawable;
        if (blurredBackgroundDrawable != null) {
            blurredBackgroundDrawable.updateColors();
        }
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        if (!this.tabs.isEmpty()) {
            int size = View.MeasureSpec.getSize(i) - (this.listViewPaddingH * 2);
            Tab tabFindDefaultTab = findDefaultTab();
            if (tabFindDefaultTab != null || ExteraConfig.getHideAllChats()) {
                if (!ExteraConfig.getHideAllChats()) {
                    int width = 0;
                    for (int i3 = 0; i3 < this.tabs.size(); i3++) {
                        Tab tab = this.tabs.get(i3);
                        if (tab != tabFindDefaultTab) {
                            width += tab.getWidth(true) + FolderIcons.getPaddingTab();
                        }
                    }
                    String string = LocaleController.getString(R.string.FilterAllChats);
                    String string2 = LocaleController.getString(R.string.FilterAllChatsShort);
                    tabFindDefaultTab.setTitle(string, null, false);
                    int width2 = tabFindDefaultTab.getWidth(true) + FolderIcons.getPaddingTab();
                    if (!this.isStaticAllChats && width2 + width > size) {
                        string = string2;
                    }
                    tabFindDefaultTab.setTitle(string, null, false);
                    this.allTabsWidth = width + tabFindDefaultTab.getWidth(true) + FolderIcons.getPaddingTab();
                }
                int i4 = this.allTabsWidth;
                int i5 = this.additionalTabWidth;
                int size2 = i4 < size ? (size - i4) / this.tabs.size() : 0;
                this.additionalTabWidth = size2;
                if (i5 != size2) {
                    this.ignoreLayout = true;
                    RecyclerView.ItemAnimator itemAnimator = this.listView.getItemAnimator();
                    this.listView.setItemAnimator(null);
                    this.adapter.notifyDataSetChanged();
                    this.listView.setItemAnimator(itemAnimator);
                    this.ignoreLayout = false;
                }
                updateTabsWidths();
                this.invalidated = false;
            }
        }
        super.onMeasure(i, i2);
    }

    private Tab findDefaultTab() {
        for (int i = 0; i < this.tabs.size(); i++) {
            if (this.tabs.get(i).isDefault) {
                return this.tabs.get(i);
            }
        }
        return null;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    private void scrollToChild(int i, boolean z) {
        if (this.tabs.isEmpty() || this.scrollingToChild == i || i < 0 || i >= this.tabs.size()) {
            return;
        }
        this.scrollingToChild = i;
        RecyclerListView recyclerListView = this.listView;
        if (z) {
            recyclerListView.smoothScrollToPosition(i);
        } else {
            recyclerListView.scrollToPosition(i);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int i5 = i3 - i;
        if (this.prevLayoutWidth != i5) {
            this.prevLayoutWidth = i5;
            this.scrollingToChild = -1;
            if (this.animatingIndicator) {
                AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                this.animatingIndicator = false;
                setEnabled(true);
                FilterTabsViewDelegate filterTabsViewDelegate = this.delegate;
                if (filterTabsViewDelegate != null) {
                    filterTabsViewDelegate.onPageScrolled(1.0f);
                }
            }
        }
    }

    public void selectTabWithId(int i, float f) {
        int i2 = this.idToPosition.get(i, -1);
        if (i2 < 0) {
            return;
        }
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        if (f > 0.0f) {
            this.manualScrollingToPosition = i2;
            this.manualScrollingToId = i;
        } else {
            this.manualScrollingToPosition = -1;
            this.manualScrollingToId = -1;
        }
        this.animatingIndicatorProgress = f;
        this.listView.invalidateViews();
        this.listView.invalidate();
        invalidate();
        scrollToChild(i2, f < 1.0f);
        if ((f >= 0.5f && this.oldAnimatedTab != i2) || (f <= 0.5f && this.oldAnimatedTab != this.currentPosition)) {
            int i3 = this.manualScrollingToPosition;
            int i4 = this.currentPosition;
            if (i3 != i4) {
                if (f < 0.5f) {
                    i2 = i4;
                }
                this.delegate.onTabSelected(this.tabs.get(i2), this.currentPosition < i2, true);
                this.oldAnimatedTab = i2;
            }
        }
        if (f >= 1.0f) {
            this.manualScrollingToPosition = -1;
            this.manualScrollingToId = -1;
            this.currentPosition = i2;
            this.selectedTabId = i;
        }
    }

    public boolean isEditing() {
        return this.isEditing;
    }

    public void setIsEditing(boolean z) {
        this.isEditing = z;
        this.editingForwardAnimation = true;
        this.listView.invalidateViews();
        this.listView.invalidate();
        this.adapter.notifyDataSetChanged();
        invalidate();
        if (this.isEditing || !this.orderChanged) {
            return;
        }
        MessagesStorage.getInstance(UserConfig.selectedAccount).saveDialogFiltersOrder();
        TLRPC.TL_messages_updateDialogFiltersOrder tL_messages_updateDialogFiltersOrder = new TLRPC.TL_messages_updateDialogFiltersOrder();
        ArrayList<MessagesController.DialogFilter> dialogFilters = MessagesController.getInstance(UserConfig.selectedAccount).getDialogFilters();
        int size = dialogFilters.size();
        for (int i = 0; i < size; i++) {
            MessagesController.DialogFilter dialogFilter = dialogFilters.get(i);
            boolean zIsDefault = dialogFilter.isDefault();
            ArrayList<Integer> arrayList = tL_messages_updateDialogFiltersOrder.order;
            if (zIsDefault) {
                arrayList.add(0);
            } else {
                arrayList.add(Integer.valueOf(dialogFilter.id));
            }
        }
        MessagesController.getInstance(UserConfig.selectedAccount).lockFiltersInternal();
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_messages_updateDialogFiltersOrder, new RequestDelegate() { // from class: org.telegram.ui.Components.FilterTabsView$$ExternalSyntheticLambda2
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FilterTabsView.$r8$lambda$LUlLhaIciS00vlgmp_dSDwB023s(tLObject, tL_error);
            }
        });
        this.orderChanged = false;
    }

    public void checkTabsCounter() {
        int size = this.tabs.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            Tab tab = this.tabs.get(i);
            if (tab.counter != this.delegate.getTabCounter(tab.id) && this.delegate.getTabCounter(tab.id) >= 0) {
                if (this.positionToWidth.get(i) != tab.getWidth(true) || this.invalidated) {
                    this.invalidated = true;
                    requestLayout();
                    this.allTabsWidth = 0;
                    for (int i2 = 0; i2 < size; i2++) {
                        this.allTabsWidth += this.tabs.get(i2).getWidth(true) + FolderIcons.getPaddingTab();
                    }
                    z = true;
                    break;
                }
                z = true;
            }
        }
        if (z) {
            this.listView.setItemAnimator(this.itemAnimator);
            this.adapter.notifyDataSetChanged();
        }
    }

    public void notifyTabCounterChanged(int i) {
        int i2 = this.idToPosition.get(i, -1);
        if (i2 < 0 || i2 >= this.tabs.size()) {
            return;
        }
        Tab tab = this.tabs.get(i2);
        if (tab.counter == this.delegate.getTabCounter(tab.id) || this.delegate.getTabCounter(tab.id) < 0) {
            return;
        }
        this.listView.invalidateViews();
        if (this.positionToWidth.get(i2) != tab.getWidth(true) || this.invalidated) {
            this.invalidated = true;
            requestLayout();
            this.listView.setItemAnimator(this.itemAnimator);
            this.adapter.notifyDataSetChanged();
            this.allTabsWidth = 0;
            int size = this.tabs.size();
            for (int i3 = 0; i3 < size; i3++) {
                this.allTabsWidth += this.tabs.get(i3).getWidth(true) + FolderIcons.getPaddingTab();
            }
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final Context mContext;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return FilterTabsView.this.tabs.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return FilterTabsView.this.positionToStableId.get(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(FilterTabsView.this.new TabView(this.mContext));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TabView tabView = (TabView) viewHolder.itemView;
            int id = tabView.currentTab != null ? tabView.getId() : -1;
            tabView.setTab((Tab) FilterTabsView.this.tabs.get(i), i);
            if (id != tabView.getId()) {
                tabView.progressToLocked = tabView.currentTab.isLocked ? 1.0f : 0.0f;
            }
        }

        /* JADX WARN: Code duplicated, block: B:26:0x00c3  */
        /* JADX WARN: Code duplicated, block: B:27:0x00ce  */
        /* JADX WARN: Code duplicated, block: B:29:0x00d4  */
        /* JADX WARN: Code duplicated, block: B:32:0x00ea  */
        /* JADX WARN: Code duplicated, block: B:33:0x00f5  */
        /* JADX WARN: Code duplicated, block: B:35:0x00fb  */
        public void swapElements(int i, int i2) {
            int i3;
            int i4;
            Tab tab;
            Tab tab2;
            int i5;
            FilterTabsView filterTabsView;
            int i6;
            FilterTabsView filterTabsView2;
            int size = FilterTabsView.this.tabs.size();
            if (i < 0 || i2 < 0 || i >= size || i2 >= size) {
                return;
            }
            ArrayList<MessagesController.DialogFilter> dialogFilters = MessagesController.getInstance(UserConfig.selectedAccount).getDialogFilters();
            if (ExteraConfig.getHideAllChats()) {
                int i7 = 0;
                for (int i8 = 0; i8 < dialogFilters.size(); i8++) {
                    if (dialogFilters.get(i8).isDefault()) {
                        i7 = i8;
                        break;
                    }
                }
                i3 = i >= i7 ? i + 1 : i;
                if (i2 >= i7) {
                    i4 = i2 + 1;
                }
                MessagesController.DialogFilter dialogFilter = dialogFilters.get(i3);
                MessagesController.DialogFilter dialogFilter2 = dialogFilters.get(i4);
                int i9 = dialogFilter.order;
                dialogFilter.order = dialogFilter2.order;
                dialogFilter2.order = i9;
                dialogFilters.set(i3, dialogFilter2);
                dialogFilters.set(i4, dialogFilter);
                tab = (Tab) FilterTabsView.this.tabs.get(i);
                tab2 = (Tab) FilterTabsView.this.tabs.get(i2);
                int i10 = tab.id;
                tab.id = tab2.id;
                tab2.id = i10;
                int i11 = FilterTabsView.this.positionToStableId.get(i);
                FilterTabsView.this.positionToStableId.put(i, FilterTabsView.this.positionToStableId.get(i2));
                FilterTabsView.this.positionToStableId.put(i2, i11);
                FilterTabsView.this.delegate.onPageReorder(tab2.id, tab.id);
                i5 = FilterTabsView.this.currentPosition;
                filterTabsView = FilterTabsView.this;
                if (i5 == i) {
                    filterTabsView.currentPosition = i2;
                    FilterTabsView.this.selectedTabId = tab.id;
                } else if (filterTabsView.currentPosition == i2) {
                    FilterTabsView.this.currentPosition = i;
                    FilterTabsView.this.selectedTabId = tab2.id;
                }
                i6 = FilterTabsView.this.previousPosition;
                filterTabsView2 = FilterTabsView.this;
                if (i6 == i) {
                    filterTabsView2.previousPosition = i2;
                    FilterTabsView.this.previousId = tab.id;
                } else if (filterTabsView2.previousPosition == i2) {
                    FilterTabsView.this.previousPosition = i;
                    FilterTabsView.this.previousId = tab2.id;
                }
                FilterTabsView.this.tabs.set(i, tab2);
                FilterTabsView.this.tabs.set(i2, tab);
                FilterTabsView.this.updateTabsWidths();
                FilterTabsView.this.orderChanged = true;
                FilterTabsView.this.listView.setItemAnimator(FilterTabsView.this.itemAnimator);
                notifyItemMoved(i, i2);
            }
            i3 = i;
            i4 = i2;
            MessagesController.DialogFilter dialogFilter3 = dialogFilters.get(i3);
            MessagesController.DialogFilter dialogFilter4 = dialogFilters.get(i4);
            int i12 = dialogFilter3.order;
            dialogFilter3.order = dialogFilter4.order;
            dialogFilter4.order = i12;
            dialogFilters.set(i3, dialogFilter4);
            dialogFilters.set(i4, dialogFilter3);
            tab = (Tab) FilterTabsView.this.tabs.get(i);
            tab2 = (Tab) FilterTabsView.this.tabs.get(i2);
            int i13 = tab.id;
            tab.id = tab2.id;
            tab2.id = i13;
            int i14 = FilterTabsView.this.positionToStableId.get(i);
            FilterTabsView.this.positionToStableId.put(i, FilterTabsView.this.positionToStableId.get(i2));
            FilterTabsView.this.positionToStableId.put(i2, i14);
            FilterTabsView.this.delegate.onPageReorder(tab2.id, tab.id);
            i5 = FilterTabsView.this.currentPosition;
            filterTabsView = FilterTabsView.this;
            if (i5 == i) {
                filterTabsView.currentPosition = i2;
                FilterTabsView.this.selectedTabId = tab.id;
            } else if (filterTabsView.currentPosition == i2) {
                FilterTabsView.this.currentPosition = i;
                FilterTabsView.this.selectedTabId = tab2.id;
            }
            i6 = FilterTabsView.this.previousPosition;
            filterTabsView2 = FilterTabsView.this;
            if (i6 == i) {
                filterTabsView2.previousPosition = i2;
                FilterTabsView.this.previousId = tab.id;
            } else if (filterTabsView2.previousPosition == i2) {
                FilterTabsView.this.previousPosition = i;
                FilterTabsView.this.previousId = tab2.id;
            }
            FilterTabsView.this.tabs.set(i, tab2);
            FilterTabsView.this.tabs.set(i2, tab);
            FilterTabsView.this.updateTabsWidths();
            FilterTabsView.this.orderChanged = true;
            FilterTabsView.this.listView.setItemAnimator(FilterTabsView.this.itemAnimator);
            notifyItemMoved(i, i2);
        }

        public void moveElementToStart(int i) {
            int size = FilterTabsView.this.tabs.size();
            if (i < 0 || i >= size) {
                return;
            }
            ArrayList<MessagesController.DialogFilter> dialogFilters = MessagesController.getInstance(UserConfig.selectedAccount).getDialogFilters();
            int i2 = FilterTabsView.this.positionToStableId.get(i);
            int i3 = ((Tab) FilterTabsView.this.tabs.get(i)).id;
            for (int i4 = i - 1; i4 >= 0; i4--) {
                FilterTabsView.this.positionToStableId.put(i4 + 1, FilterTabsView.this.positionToStableId.get(i4));
            }
            MessagesController.DialogFilter dialogFilterRemove = dialogFilters.remove(i);
            dialogFilterRemove.order = 0;
            dialogFilters.add(0, dialogFilterRemove);
            FilterTabsView.this.positionToStableId.put(0, i2);
            FilterTabsView.this.tabs.add(0, (Tab) FilterTabsView.this.tabs.remove(i));
            ((Tab) FilterTabsView.this.tabs.get(0)).id = i3;
            for (int i5 = 0; i5 <= i; i5++) {
                ((Tab) FilterTabsView.this.tabs.get(i5)).id = i5;
                dialogFilters.get(i5).order = i5;
            }
            int i6 = 0;
            while (i6 <= i) {
                if (FilterTabsView.this.currentPosition == i6) {
                    FilterTabsView filterTabsView = FilterTabsView.this;
                    int i7 = i6 == i ? 0 : i6 + 1;
                    filterTabsView.selectedTabId = i7;
                    filterTabsView.currentPosition = i7;
                }
                if (FilterTabsView.this.previousPosition == i6) {
                    FilterTabsView filterTabsView2 = FilterTabsView.this;
                    int i8 = i6 == i ? 0 : i6 + 1;
                    filterTabsView2.previousId = i8;
                    filterTabsView2.previousPosition = i8;
                }
                i6++;
            }
            notifyItemMoved(i, 0);
            FilterTabsView.this.delegate.onPageReorder(((Tab) FilterTabsView.this.tabs.get(i)).id, i3);
            FilterTabsView.this.updateTabsWidths();
            FilterTabsView.this.orderChanged = true;
            FilterTabsView.this.listView.setItemAnimator(FilterTabsView.this.itemAnimator);
        }
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        private final Runnable resetDefaultPosition = new Runnable() { // from class: org.telegram.ui.Components.FilterTabsView$TouchHelperCallback$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0();
            }
        };

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean isLongPressDragEnabled() {
            return FilterTabsView.this.isEditing;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (!ExteraConfig.getHideAllChats() && (!FilterTabsView.this.isEditing || (viewHolder.getAdapterPosition() == 0 && ((Tab) FilterTabsView.this.tabs.get(0)).isDefault && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium()))) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(12, 0);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (!ExteraConfig.getHideAllChats() && ((viewHolder.getAdapterPosition() == 0 || viewHolder2.getAdapterPosition() == 0) && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium())) {
                return false;
            }
            FilterTabsView.this.adapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public /* synthetic */ void lambda$new$0() {
            if (UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                return;
            }
            for (int i = 0; i < FilterTabsView.this.tabs.size(); i++) {
                if (((Tab) FilterTabsView.this.tabs.get(i)).isDefault && i != 0) {
                    FilterTabsView.this.adapter.moveElementToStart(i);
                    FilterTabsView.this.listView.scrollToPosition(0);
                    FilterTabsView.this.onDefaultTabMoved();
                    return;
                }
            }
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                FilterTabsView.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
                viewHolder.itemView.setBackgroundColor(Theme.getColor(FilterTabsView.this.backgroundColorKey, FilterTabsView.this.resourcesProvider));
            } else {
                AndroidUtilities.cancelRunOnUIThread(this.resetDefaultPosition);
                AndroidUtilities.runOnUIThread(this.resetDefaultPosition, 320L);
            }
            super.onSelectedChanged(viewHolder, i);
            if (viewHolder != null) {
                viewHolder.itemView.setTag(R.id.dragging, i == 2 ? Boolean.TRUE : null);
            }
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
            viewHolder.itemView.setBackground(null);
            viewHolder.itemView.setTag(R.id.dragging, null);
        }
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    public boolean currentTabIsDefault() {
        Tab tabFindDefaultTab = findDefaultTab();
        return tabFindDefaultTab != null && tabFindDefaultTab.id == this.selectedTabId;
    }

    public int getDefaultTabId() {
        Tab tabFindDefaultTab = findDefaultTab();
        if (tabFindDefaultTab == null) {
            return -1;
        }
        return tabFindDefaultTab.id;
    }

    public boolean isEmpty() {
        return this.tabs.isEmpty();
    }

    public boolean isFirstTabSelected() {
        return this.tabs.isEmpty() || this.selectedTabId == this.tabs.get(0).id;
    }

    public boolean isLocked(int i) {
        for (int i2 = 0; i2 < this.tabs.size(); i2++) {
            if (this.tabs.get(i2).id == i) {
                return this.tabs.get(i2).isLocked;
            }
        }
        return false;
    }

    public void shakeLock(int i) {
        for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
            if (this.listView.getChildAt(i2) instanceof TabView) {
                TabView tabView = (TabView) this.listView.getChildAt(i2);
                if (tabView.currentTab.id == i) {
                    tabView.shakeLockIcon(1.0f, 0);
                    try {
                        tabView.performHapticFeedback(3);
                        return;
                    } catch (Exception unused) {
                        return;
                    }
                }
            }
        }
    }
}
