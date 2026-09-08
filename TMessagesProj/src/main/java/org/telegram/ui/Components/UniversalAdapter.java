package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.media.PlatformMediaRouter1RouteProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.plugins.models.SettingItem;
import com.exteragram.messenger.plugins.models.TextSetting;
import java.util.ArrayList;
import me.vkryl.core.BitwiseUtils;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stats;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.BusinessLinksActivity;
import org.telegram.ui.Business.QuickRepliesActivity;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.CollapseTextCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogRadioCell;
import org.telegram.ui.Cells.EditTextCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.SlideIntChooseView;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextRightIconCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChannelMonetizationLayout;
import org.telegram.ui.Charts.BaseChartView;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.StatisticActivity;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet;

public class UniversalAdapter extends AdapterWithDiffUtils {
    private boolean allowReorder;
    private boolean applyBackground;
    private BaseChartView.SharedUiComponents chartSharedUI;
    private final int classGuid;
    private final Context context;
    public final int currentAccount;
    private Section currentReorderSection;
    private Section currentWhiteSection;
    private final boolean dialog;
    protected Utilities.Callback2<ArrayList<UItem>, UniversalAdapter> fillItems;
    private final ArrayList<UItem> items;
    public int itemsOffset;
    protected final RecyclerListView listView;
    private final ArrayList<UItem> oldItems;
    private Utilities.Callback2<Integer, ArrayList<UItem>> onReordered;
    private boolean orderChanged;
    private int orderChangedId;
    private final ArrayList<Section> reorderSections;
    private final Theme.ResourcesProvider resourcesProvider;
    private final ArrayList<Section> whiteSections;

    public static boolean isHeader(int i) {
        return i == 0 || i == 42 || i == 1 || i == 26;
    }

    public UniversalAdapter(RecyclerListView recyclerListView, Context context, int i, int i2, Utilities.Callback2<ArrayList<UItem>, UniversalAdapter> callback2, Theme.ResourcesProvider resourcesProvider) {
        this(recyclerListView, context, i, i2, false, callback2, resourcesProvider);
    }

    public UniversalAdapter(RecyclerListView recyclerListView, Context context, int i, int i2, boolean z, Utilities.Callback2<ArrayList<UItem>, UniversalAdapter> callback2, Theme.ResourcesProvider resourcesProvider) {
        this.applyBackground = true;
        this.oldItems = new ArrayList<>();
        this.items = new ArrayList<>();
        this.itemsOffset = 0;
        this.whiteSections = new ArrayList<>();
        this.reorderSections = new ArrayList<>();
        this.listView = recyclerListView;
        this.context = context;
        this.currentAccount = i;
        this.classGuid = i2;
        this.dialog = z;
        this.fillItems = callback2;
        this.resourcesProvider = resourcesProvider;
        update(false);
    }

    public void setApplyBackground(boolean z) {
        this.applyBackground = z;
    }

    public static class Section {
        public int end;
        public int start;

        private Section() {
        }

        public boolean contains(int i) {
            return i >= this.start && i <= this.end;
        }
    }

    public void whiteSectionStart() {
        Section section = new Section();
        this.currentWhiteSection = section;
        section.start = this.itemsOffset + this.items.size();
        Section section2 = this.currentWhiteSection;
        section2.end = -1;
        this.whiteSections.add(section2);
    }

    public void whiteSectionEnd() {
        Section section = this.currentWhiteSection;
        if (section != null) {
            int i = section.start;
            int iMax = Math.max(0, (this.itemsOffset + this.items.size()) - 1);
            if (ExteraConfig.getSectionsSeparatedHeaders()) {
                while (i <= iMax && isHeader(getItemViewType(i))) {
                    i++;
                }
            }
            Section section2 = this.currentWhiteSection;
            section2.start = i;
            section2.end = iMax;
            if (i >= iMax) {
                this.whiteSections.remove(section2);
            }
            this.currentWhiteSection = null;
        }
    }

    public int reorderSectionStart() {
        Section section = new Section();
        this.currentReorderSection = section;
        section.start = this.items.size();
        Section section2 = this.currentReorderSection;
        section2.end = -1;
        this.reorderSections.add(section2);
        return this.reorderSections.size() - 1;
    }

    public void reorderSectionEnd() {
        Section section = this.currentReorderSection;
        if (section != null) {
            int i = section.start;
            int iMax = Math.max(0, this.items.size() - 1);
            if (ExteraConfig.getSectionsSeparatedHeaders()) {
                while (i <= iMax && isHeader(getItemViewType(i))) {
                    i++;
                }
            }
            Section section2 = this.currentReorderSection;
            section2.start = i;
            section2.end = iMax;
        }
    }

    private void updateReorderSections() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null) {
            return;
        }
        ArrayList<Long> arrayList = recyclerListView.forcedSections;
        if (arrayList == null) {
            recyclerListView.forcedSections = new ArrayList<>();
        } else {
            arrayList.clear();
        }
        ArrayList<Section> arrayList2 = this.whiteSections;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Section section = arrayList2.get(i);
            i++;
            Section section2 = section;
            this.listView.forcedSections.add(Long.valueOf(AndroidUtilities.pack(section2.start, section2.end)));
        }
    }

    public boolean isReorderItem(int i) {
        return getReorderSectionId(i) >= 0;
    }

    public int getReorderSectionId(int i) {
        for (int i2 = 0; i2 < this.reorderSections.size(); i2++) {
            if (this.reorderSections.get(i2).contains(i)) {
                return i2;
            }
        }
        return -1;
    }

    public void swapElements(int i, int i2) {
        int i3;
        if (this.onReordered == null) {
            return;
        }
        int reorderSectionId = getReorderSectionId(i);
        int reorderSectionId2 = getReorderSectionId(i2);
        if (reorderSectionId < 0 || reorderSectionId != reorderSectionId2) {
            return;
        }
        boolean zHasDivider = hasDivider(i);
        boolean zHasDivider2 = hasDivider(i2);
        this.items.add(i2, this.items.remove(i));
        notifyItemMoved(i, i2);
        if (hasDivider(i2) != zHasDivider) {
            notifyItemChanged(i2, 3);
        }
        if (hasDivider(i) != zHasDivider2) {
            notifyItemChanged(i, 3);
        }
        if (this.orderChanged && (i3 = this.orderChangedId) != reorderSectionId) {
            callReorder(i3);
        }
        this.orderChanged = true;
        this.orderChangedId = reorderSectionId;
    }

    private void callReorder(int i) {
        if (i < 0 || i >= this.reorderSections.size()) {
            return;
        }
        Section section = this.reorderSections.get(i);
        this.onReordered.run(Integer.valueOf(i), new ArrayList<>(this.items.subList(section.start, section.end + 1)));
        this.orderChanged = false;
    }

    public void reorderDone() {
        if (this.orderChanged) {
            callReorder(this.orderChangedId);
        }
    }

    public void listenReorder(Utilities.Callback2<Integer, ArrayList<UItem>> callback2) {
        this.onReordered = callback2;
    }

    public void updateReorder(boolean z) {
        this.allowReorder = z;
    }

    public void drawWhiteSections(Canvas canvas, RecyclerListView recyclerListView) {
        for (int i = 0; i < this.whiteSections.size(); i++) {
            Section section = this.whiteSections.get(i);
            int i2 = section.end;
            if (i2 >= 0) {
                recyclerListView.drawSectionBackground(canvas, section.start, i2, getThemedColor(this.dialog ? Theme.key_dialogBackground : Theme.key_windowBackgroundWhite));
            }
        }
    }

    public void update(final boolean z) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && recyclerListView.isComputingLayout()) {
            this.listView.post(new Runnable() { // from class: org.telegram.ui.Components.UniversalAdapter$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$update$0(z);
                }
            });
        } else {
            lambda$update$0(z);
        }
    }

    public void lambda$update$0(boolean z) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || !recyclerListView.isComputingLayout()) {
            this.oldItems.clear();
            this.oldItems.addAll(this.items);
            this.items.clear();
            this.currentWhiteSection = null;
            this.whiteSections.clear();
            this.reorderSections.clear();
            Utilities.Callback2<ArrayList<UItem>, UniversalAdapter> callback2 = this.fillItems;
            if (callback2 != null) {
                callback2.run(this.items, this);
                updateReorderSections();
                if (z) {
                    setItems(this.oldItems, this.items);
                } else {
                    notifyDataSetChanged();
                }
            }
        }
    }

    public void updateWithoutNotify() {
        this.oldItems.clear();
        this.oldItems.addAll(this.items);
        this.items.clear();
        this.whiteSections.clear();
        this.reorderSections.clear();
        Utilities.Callback2<ArrayList<UItem>, UniversalAdapter> callback2 = this.fillItems;
        if (callback2 != null) {
            callback2.run(this.items, this);
        }
        updateReorderSections();
    }

    public boolean shouldApplyBackground(int i) {
        if (!this.applyBackground) {
            return false;
        }
        if (i >= UItem.factoryViewTypeStartsWith) {
            return true;
        }
        switch (i) {
            case -3:
            case -1:
            case 0:
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 27:
            case 28:
            case 29:
            case 30:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
                return true;
            case -2:
            case 2:
            case 7:
            case 8:
            case 26:
            case 31:
            case 38:
            default:
                return false;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        HeaderCell headerCell;
        View universalChartCell;
        View notificationsCheckCell;
        int i2;
        View headerCell2;
        boolean z = this.dialog;
        int i3 = z ? Theme.key_dialogBackground : Theme.key_windowBackgroundWhite;
        if (i >= UItem.factoryViewTypeStartsWith) {
            UItem.UItemFactory<?> uItemFactoryFindFactory = UItem.findFactory(i);
            if (uItemFactoryFindFactory != null) {
                universalChartCell = uItemFactoryFindFactory.createView(this.context, this.listView, this.currentAccount, this.classGuid, this.resourcesProvider);
            } else {
                universalChartCell = new View(this.context);
            }
        } else {
            int i4 = 6;
            switch (i) {
                case -4:
                case -1:
                    FrameLayout frameLayout = new FrameLayout(this.context) { // from class: org.telegram.ui.Components.UniversalAdapter.1
                        @Override // android.widget.FrameLayout, android.view.View
                        public void onMeasure(int i5, int i6) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i5), TLObject.FLAG_30), i6);
                        }
                    };
                    universalChartCell = frameLayout;
                    if (i == -4) {
                        frameLayout.setTag(-33024);
                        universalChartCell = frameLayout;
                    }
                    break;
                case -3:
                    universalChartCell = new FullscreenCustomFrameLayout(this.context);
                    break;
                case -2:
                    universalChartCell = new FrameLayout(this.context) { // from class: org.telegram.ui.Components.UniversalAdapter.2
                        @Override // android.widget.FrameLayout, android.view.View
                        public void onMeasure(int i5, int i6) {
                            int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i5), TLObject.FLAG_30);
                            measureChildren(iMakeMeasureSpec, i6);
                            int iMax = 0;
                            for (int i7 = 0; i7 < getChildCount(); i7++) {
                                iMax = Math.max(iMax, getChildAt(i7).getMeasuredHeight());
                            }
                            super.onMeasure(iMakeMeasureSpec, View.MeasureSpec.makeMeasureSpec(iMax, TLObject.FLAG_30));
                        }
                    };
                    break;
                case 0:
                    if (z) {
                        headerCell = new HeaderCell(this.context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, 0, false, this.resourcesProvider);
                        universalChartCell = headerCell;
                    } else {
                        universalChartCell = new HeaderCell(this.context, this.resourcesProvider);
                    }
                    break;
                case 1:
                    headerCell2 = new HeaderCell(this.context, Theme.key_windowBackgroundWhiteBlackText, 17, 15, false, this.resourcesProvider);
                    universalChartCell = headerCell2;
                    break;
                case 2:
                    universalChartCell = new TopViewCell(this.context, this.resourcesProvider);
                    break;
                case 3:
                    universalChartCell = new TextCell(this.context, this.resourcesProvider);
                    break;
                case 4:
                case 9:
                    TextCheckCell textCheckCell = new TextCheckCell(this.context, this.resourcesProvider);
                    headerCell2 = textCheckCell;
                    if (i == 9) {
                        textCheckCell.setDrawCheckRipple(true);
                        textCheckCell.setColors(Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
                        textCheckCell.setTypeface(AndroidUtilities.bold());
                        textCheckCell.setHeight(56);
                        headerCell2 = textCheckCell;
                    }
                    universalChartCell = headerCell2;
                    break;
                case 5:
                case 6:
                    notificationsCheckCell = new NotificationsCheckCell(this.context, 21, 60, 71, i == 6, this.resourcesProvider);
                    universalChartCell = notificationsCheckCell;
                    break;
                case 7:
                case 8:
                default:
                    universalChartCell = new TextInfoPrivacyCell(this.context, this.resourcesProvider);
                    break;
                case 10:
                    universalChartCell = new DialogRadioCell(this.context);
                    break;
                case 11:
                case 12:
                    UserCell userCell = new UserCell(this.context, 6, i == 12 ? 3 : 0, false);
                    userCell.setSelfAsSavedMessages(true);
                    universalChartCell = userCell;
                    break;
                case 13:
                    headerCell2 = new UserCell(this.context, 6, 0, false, true);
                    universalChartCell = headerCell2;
                    break;
                case 14:
                    universalChartCell = new SlideChooseView(this.context, this.resourcesProvider);
                    break;
                case 15:
                    universalChartCell = new SlideIntChooseView(this.context, this.resourcesProvider);
                    break;
                case 16:
                    universalChartCell = new QuickRepliesActivity.QuickReplyView(this.context, this.onReordered != null, this.resourcesProvider);
                    break;
                case 17:
                    universalChartCell = new QuickRepliesActivity.LargeQuickReplyView(this.context, this.resourcesProvider);
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                    if (this.chartSharedUI == null) {
                        this.chartSharedUI = new BaseChartView.SharedUiComponents();
                    }
                    universalChartCell = new StatisticActivity.UniversalChartCell(this.context, this.currentAccount, i - 18, this.chartSharedUI, this.classGuid);
                    break;
                case 24:
                    universalChartCell = new ChannelMonetizationLayout.ProceedOverviewCell(this.context, this.resourcesProvider);
                    break;
                case 25:
                    universalChartCell = new ChannelMonetizationLayout.TransactionCell(this.context, this.resourcesProvider);
                    break;
                case 26:
                    headerCell = new HeaderCell(this.context, Theme.key_windowBackgroundWhiteBlackText, 23, 20, 0, false, this.resourcesProvider);
                    headerCell.setTextSize(20.0f);
                    universalChartCell = headerCell;
                    break;
                case 27:
                    StoryPrivacyBottomSheet.UserCell userCell2 = new StoryPrivacyBottomSheet.UserCell(this.context, this.resourcesProvider);
                    userCell2.setIsSendAs(false, false);
                    universalChartCell = userCell2;
                    break;
                case 28:
                    universalChartCell = new SpaceView(this.context);
                    break;
                case 29:
                    universalChartCell = new BusinessLinksActivity.BusinessLinkView(this.context, this.resourcesProvider);
                    break;
                case 30:
                    universalChartCell = new TextRightIconCell(this.context, this.resourcesProvider);
                    break;
                case 31:
                    RecyclerListView recyclerListView = this.listView;
                    if (recyclerListView != null && recyclerListView.hasSections()) {
                        GraySectionCell graySectionCell = new GraySectionCell(this.context, 28, this.resourcesProvider);
                        graySectionCell.setNoBackground(true);
                        universalChartCell = graySectionCell;
                    } else {
                        universalChartCell = new GraySectionCell(this.context, this.resourcesProvider);
                    }
                    break;
                case 32:
                    universalChartCell = new ProfileSearchCell(this.context);
                    break;
                case 33:
                    universalChartCell = new DialogCell(null, this.context, false, true);
                    break;
                case 34:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.context, this.resourcesProvider);
                    flickerLoadingView.setIsSingleCell(true);
                    universalChartCell = flickerLoadingView;
                    break;
                case 35:
                case 36:
                case 37:
                case 41:
                    if (i != 35) {
                        if (i != 36) {
                            if (i == 37) {
                                i4 = 7;
                            } else if (i == 41) {
                                i4 = 8;
                            } else {
                                i2 = 0;
                            }
                        }
                        CheckBoxCell checkBoxCell = new CheckBoxCell(this.context, i2, 21, true, this.resourcesProvider);
                        checkBoxCell.getCheckBoxRound().setColor(Theme.key_switch2TrackChecked, Theme.key_radioBackground, Theme.key_checkboxCheck);
                        notificationsCheckCell = checkBoxCell;
                        universalChartCell = notificationsCheckCell;
                    } else {
                        i4 = 4;
                    }
                    i2 = i4;
                    CheckBoxCell checkBoxCell2 = new CheckBoxCell(this.context, i2, 21, true, this.resourcesProvider);
                    checkBoxCell2.getCheckBoxRound().setColor(Theme.key_switch2TrackChecked, Theme.key_radioBackground, Theme.key_checkboxCheck);
                    notificationsCheckCell = checkBoxCell2;
                    universalChartCell = notificationsCheckCell;
                    break;
                case 38:
                    universalChartCell = new CollapseTextCell(this.context, this.resourcesProvider);
                    break;
                case 39:
                case 40:
                    universalChartCell = new TextCheckCell2(this.context);
                    break;
                case 42:
                    headerCell2 = new HeaderCell(this.context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, 0, false, true, this.resourcesProvider);
                    universalChartCell = headerCell2;
                    break;
                case 43:
                    universalChartCell = new TextSettingsCell(this.context, this.resourcesProvider);
                    break;
                case 44:
                    universalChartCell = new RadioButtonCell(this.context);
                    break;
            }
        }
        if (shouldApplyBackground(i)) {
            universalChartCell.setBackgroundColor(getThemedColor(i3));
        }
        return new RecyclerListView.Holder(universalChartCell);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        UItem item = getItem(i);
        if (item == null) {
            return 0;
        }
        return item.viewType;
    }

    private boolean hasDivider(int i) {
        UItem item = getItem(i);
        UItem item2 = getItem(i + 1);
        return (item == null || item2 == null || item.hideDivider || (ExteraConfig.getSectionsSeparatedHeaders() && isHeader(item2.viewType)) || isShadow(item2.viewType) != isShadow(item.viewType)) ? false : true;
    }

    public static boolean isShadow(int i) {
        if (i < UItem.factoryViewTypeStartsWith) {
            return i == 7 || i == 8 || i == 38 || i == 31 || i == -4 || i == 28 || i == 2 || i == -2;
        }
        UItem.UItemFactory<?> uItemFactoryFindFactory = UItem.findFactory(i);
        return uItemFactoryFindFactory != null && uItemFactoryFindFactory.getIsShadowValue();
    }

    void $r8$lambda$zkYqYy_dtBSQIc15WzODxdmLimw(UItem uItem, int i) {
        Utilities.Callback<Integer> callback = uItem.intCallback;
        if (callback != null) {
            callback.run(Integer.valueOf(i));
        }
    }

    public static /* synthetic */ void $r8$lambda$oQs2QYu66NvT3BIPP1RyA3k9ryA(UItem uItem, Integer num) {
        uItem.intValue = num.intValue();
        Utilities.Callback<Integer> callback = uItem.intCallback;
        if (callback != null) {
            callback.run(num);
        }
    }

    public /* synthetic */ StatisticActivity.BaseChartCell lambda$onBindViewHolder$3(UItem uItem) {
        View viewFindViewByItemObject = findViewByItemObject(uItem.object);
        if (viewFindViewByItemObject instanceof StatisticActivity.UniversalChartCell) {
            return (StatisticActivity.UniversalChartCell) viewFindViewByItemObject;
        }
        return null;
    }

    private View findViewByItemObject(Object obj) {
        int i = 0;
        while (true) {
            if (i >= getItemCount()) {
                i = -1;
                break;
            }
            UItem item = getItem(i);
            if (item != null && item.object == obj) {
                break;
            }
            i++;
        }
        if (i == -1) {
            return null;
        }
        for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
            View childAt = this.listView.getChildAt(i2);
            int childAdapterPosition = this.listView.getChildAdapterPosition(childAt);
            if (childAdapterPosition != -1 && childAdapterPosition == i) {
                return childAt;
            }
        }
        return null;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
        updateReorder(viewHolder, this.allowReorder);
        updateColors(viewHolder, getItem(viewHolder.getAdapterPosition()));
    }

    private void updateColors(RecyclerView.ViewHolder viewHolder, UItem uItem) {
        KeyEvent.Callback callback = viewHolder.itemView;
        if (callback instanceof Theme.Colorable) {
            ((Theme.Colorable) callback).updateColors();
        }
        if (shouldApplyBackground(viewHolder.getItemViewType())) {
            if (uItem != null && uItem.transparent) {
                viewHolder.itemView.setBackground(null);
            } else {
                viewHolder.itemView.setBackgroundColor(getThemedColor(this.dialog ? Theme.key_dialogBackground : Theme.key_windowBackgroundWhite));
            }
        }
    }

    public void updateReorder(RecyclerView.ViewHolder viewHolder, boolean z) {
        if (viewHolder == null) {
            return;
        }
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType < UItem.factoryViewTypeStartsWith) {
            if (itemViewType != 16) {
                return;
            }
            ((QuickRepliesActivity.QuickReplyView) viewHolder.itemView).setReorder(z);
        } else {
            UItem.UItemFactory<?> uItemFactoryFindFactory = UItem.findFactory(itemViewType);
            if (uItemFactoryFindFactory != null) {
                uItemFactoryFindFactory.attachedView(this.listView, viewHolder.itemView, getItem(viewHolder.getAdapterPosition()));
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.items.size();
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        UItem item = getItem(viewHolder.getAdapterPosition());
        if (itemViewType >= UItem.factoryViewTypeStartsWith) {
            UItem.UItemFactory<?> uItemFactoryFindFactory = UItem.findFactory(itemViewType);
            if (uItemFactoryFindFactory == null || !uItemFactoryFindFactory.getIsClickableValue()) {
                return false;
            }
        } else if (itemViewType != 3 && itemViewType != 5 && itemViewType != 6 && itemViewType != 30 && itemViewType != 4 && itemViewType != 10 && itemViewType != 44 && itemViewType != 11 && itemViewType != 12 && itemViewType != 17 && itemViewType != 16 && itemViewType != 29 && itemViewType != 25 && itemViewType != 27 && itemViewType != 32 && itemViewType != 33 && itemViewType != 35 && itemViewType != 36 && itemViewType != 37 && itemViewType != 41 && itemViewType != 39 && itemViewType != 40 && itemViewType != 38) {
            return false;
        }
        return item == null || item.enabled;
    }

    public UItem getItem(int i) {
        if (i < 0 || i >= this.items.size()) {
            return null;
        }
        return this.items.get(i);
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public static class FullscreenCustomFrameLayout extends FrameLayout {
        private int minusHeight;
        private boolean minusPadding;

        public FullscreenCustomFrameLayout(Context context) {
            super(context);
            this.minusHeight = 0;
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            int paddingTop = this.minusHeight;
            View view = getParent() instanceof View ? (View) getParent() : null;
            if (this.minusPadding && view != null) {
                paddingTop = paddingTop + view.getPaddingTop() + view.getPaddingBottom();
            }
            if (view != null && view.getMeasuredHeight() > 0) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight() - paddingTop, TLObject.FLAG_30));
                return;
            }
            if (View.MeasureSpec.getMode(i2) != 0) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2) - paddingTop, TLObject.FLAG_30));
                return;
            }
            int size = View.MeasureSpec.getSize(i2);
            int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30);
            measureChildren(iMakeMeasureSpec, i2);
            int iMin = 0;
            for (int i3 = 0; i3 < getChildCount(); i3++) {
                iMin = Math.max(iMin, getChildAt(i3).getMeasuredHeight());
            }
            if (size > 0) {
                iMin = Math.min(iMin, size - paddingTop);
            }
            super.onMeasure(iMakeMeasureSpec, View.MeasureSpec.makeMeasureSpec(iMin, TLObject.FLAG_30));
        }

        public void setMinusHeight(int i) {
            this.minusHeight = i;
        }

        public void setMinusPadding(boolean z) {
            this.minusPadding = z;
        }
    }

    public static class SpaceView extends View {
        private int height;

        public SpaceView(Context context) {
            super(context);
            setTag(-33024);
        }

        public void setHeight(int i) {
            if (this.height == i) {
                return;
            }
            this.height = i;
            requestLayout();
        }

        @Override // android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(this.height, TLObject.FLAG_30));
        }
    }
}
