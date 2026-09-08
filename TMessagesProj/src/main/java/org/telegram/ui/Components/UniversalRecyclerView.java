package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.iv.RichEditor;

public class UniversalRecyclerView extends RecyclerListView {
    public final UniversalAdapter adapter;
    private boolean doNotDetachViews;
    public ItemTouchHelper itemTouchHelper;
    public LinearLayoutManager layoutManager;
    private boolean reorderHandleOnly;
    private boolean reorderingAllowed;
    private View reorderingBackgroundView;
    private boolean reorderingLongPressEnabled;
    private boolean reorderingOnOtherAxis;
    private Drawable reorderingOriginalBackground;
    private RecyclerView.ViewHolder reorderingViewHolder;

    public boolean isReorderRemoving() {
        return false;
    }

    public void onLayoutUpdate() {
    }

    public void onReorderEnd(RecyclerView.ViewHolder viewHolder) {
    }

    public void onReorderMoved(RecyclerView.ViewHolder viewHolder) {
    }

    public void onReorderRemove(RecyclerView.ViewHolder viewHolder) {
    }

    public void onReorderStart(RecyclerView.ViewHolder viewHolder) {
    }

    public void swappedElements() {
    }

    public void doNotDetachViews() {
        this.doNotDetachViews = true;
    }

    public void doNotDetachViews(boolean z) {
        this.doNotDetachViews = z;
    }

    public UniversalRecyclerView(BaseFragment baseFragment, Utilities.Callback2<ArrayList<UItem>, UniversalAdapter> callback2, Utilities.Callback5<UItem, View, Integer, Float, Float> callback5, Utilities.Callback5Return<UItem, View, Integer, Float, Float, Boolean> callback5Return) {
        this(baseFragment.getContext(), baseFragment.getCurrentAccount(), baseFragment.getClassGuid(), callback2, callback5, callback5Return, baseFragment.getResourceProvider());
    }

    public UniversalRecyclerView(Context context, int i, int i2, Utilities.Callback2<ArrayList<UItem>, UniversalAdapter> callback2, Utilities.Callback5<UItem, View, Integer, Float, Float> callback5, Utilities.Callback5Return<UItem, View, Integer, Float, Float, Boolean> callback5Return, Theme.ResourcesProvider resourcesProvider) {
        this(context, i, i2, false, callback2, callback5, callback5Return, resourcesProvider);
    }

    public UniversalRecyclerView(Context context, int i, int i2, boolean z, Utilities.Callback2<ArrayList<UItem>, UniversalAdapter> callback2, Utilities.Callback5<UItem, View, Integer, Float, Float> callback5, Utilities.Callback5Return<UItem, View, Integer, Float, Float, Boolean> callback5Return, Theme.ResourcesProvider resourcesProvider) {
        this(context, i, i2, z, callback2, callback5, callback5Return, resourcesProvider, -1, 1);
    }

    public UniversalRecyclerView(Context context, int i, int i2, boolean z, Utilities.Callback2<ArrayList<UItem>, UniversalAdapter> callback2, final Utilities.Callback5<UItem, View, Integer, Float, Float> callback5, final Utilities.Callback5Return<UItem, View, Integer, Float, Float, Boolean> callback5Return, Theme.ResourcesProvider resourcesProvider, int i3, int i4) {
        super(context, resourcesProvider);
        this.reorderingLongPressEnabled = true;
        boolean z2 = false;
        if (i3 == -1) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, i4, z2) { // from class: org.telegram.ui.Components.UniversalRecyclerView.1
                @Override // androidx.recyclerview.widget.LinearLayoutManager
                public int getExtraLayoutSpace(RecyclerView.State state) {
                    return UniversalRecyclerView.this.doNotDetachViews ? AndroidUtilities.displaySize.y : super.getExtraLayoutSpace(state);
                }
            };
            this.layoutManager = linearLayoutManager;
            setLayoutManager(linearLayoutManager);
        } else {
            final ExtendedGridLayoutManager extendedGridLayoutManager = new ExtendedGridLayoutManager(context, i3) { // from class: org.telegram.ui.Components.UniversalRecyclerView.2
                @Override // androidx.recyclerview.widget.LinearLayoutManager
                public int getExtraLayoutSpace(RecyclerView.State state) {
                    return UniversalRecyclerView.this.doNotDetachViews ? AndroidUtilities.displaySize.y : super.getExtraLayoutSpace(state);
                }
            };
            extendedGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.UniversalRecyclerView.3
                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int i5) {
                    int i6;
                    UniversalAdapter universalAdapter = UniversalRecyclerView.this.adapter;
                    if (universalAdapter == null) {
                        return extendedGridLayoutManager.getSpanCount();
                    }
                    UItem item = universalAdapter.getItem(i5);
                    return (item == null || (i6 = item.spanCount) == -1) ? extendedGridLayoutManager.getSpanCount() : i6;
                }
            });
            this.layoutManager = extendedGridLayoutManager;
            setLayoutManager(extendedGridLayoutManager);
        }
        UniversalAdapter universalAdapter = new UniversalAdapter(this, context, i, i2, z, callback2, resourcesProvider);
        this.adapter = universalAdapter;
        setAdapter(universalAdapter);
        setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.Components.UniversalRecyclerView$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i5, float f, float f2) {
                this.f$0.lambda$new$0(callback5, view, i5, f, f2);
            }
        });
        setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() { // from class: org.telegram.ui.Components.UniversalRecyclerView$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public final boolean onItemClick(View view, int i5, float f, float f2) {
                return this.f$0.lambda$new$1(callback5Return, view, i5, f, f2);
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.Components.UniversalRecyclerView.4
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                super.onMoveAnimationUpdate(viewHolder);
                UniversalRecyclerView.this.invalidate();
                UniversalRecyclerView.this.onLayoutUpdate();
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onRemoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                super.onRemoveAnimationUpdate(viewHolder);
                if (UniversalRecyclerView.this.hasSections()) {
                    UniversalRecyclerView.this.invalidate();
                }
                UniversalRecyclerView.this.onLayoutUpdate();
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onAddAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                super.onAddAnimationUpdate(viewHolder);
                if (UniversalRecyclerView.this.hasSections()) {
                    UniversalRecyclerView.this.invalidate();
                }
                UniversalRecyclerView.this.onLayoutUpdate();
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onChangeAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                super.onChangeAnimationUpdate(viewHolder);
                if (UniversalRecyclerView.this.hasSections()) {
                    UniversalRecyclerView.this.invalidate();
                }
                UniversalRecyclerView.this.onLayoutUpdate();
            }
        };
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDurations(350L);
        setItemAnimator(defaultItemAnimator);
    }

    public Boolean lambda$setSections$3(View view) {
        UItem item;
        if (view.getParent() != this) {
            return Boolean.FALSE;
        }
        RecyclerView.ViewHolder childViewHolder = getChildViewHolder(view);
        Object tag = childViewHolder.itemView.getTag(R.id.parent_tag);
        if ((tag instanceof Boolean) && ((Boolean) tag).booleanValue()) {
            return Boolean.FALSE;
        }
        int adapterPosition = childViewHolder.getAdapterPosition();
        if (adapterPosition != -1 && (item = this.adapter.getItem(adapterPosition)) != null && item.transparent) {
            return Boolean.FALSE;
        }
        int itemViewType = childViewHolder.getItemViewType();
        if (ExteraConfig.getSectionsSeparatedHeaders() && UniversalAdapter.isHeader(itemViewType)) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(!UniversalAdapter.isShadow(itemViewType));
    }

    public /* synthetic */ void lambda$setSections$5(Canvas canvas, RectF rectF, float f, float f2, float f3) {
        super.drawBackgroundRect(canvas, rectF, f, f2, f3);
    }
}
