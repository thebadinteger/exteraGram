package org.telegram.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.exteragram.messenger.utils.ui.FolderIcons;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FolderBottomSheet;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;

public class FiltersSetupActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ListAdapter adapter;
    private int filtersStartPosition;
    private int folderTagsPosition;
    private boolean highlightTags;
    private boolean ignoreUpdates;
    private ItemTouchHelper itemTouchHelper;
    private RecyclerListView listView;
    private boolean loadedColors;
    private boolean loadingFiltersForColors;
    private boolean orderChanged;

    @Keep
    private int showTagsRow;
    private UndoView undoView;
    private ArrayList<ItemInner> oldItems = new ArrayList<>();
    private ArrayList<ItemInner> items = new ArrayList<>();
    private int filtersSectionStart = -1;
    private int filtersSectionEnd = -1;
    private int shiftDp = -4;

    public static void lambda$new$0(View view) {
            if (this.imageView.isPlaying()) {
                return;
            }
            this.imageView.setProgress(0.0f);
            this.imageView.playAnimation();
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), i2);
        }
    }

    public class FilterCell extends FrameLayout {
        private final View colorImageView;
        private MessagesController.DialogFilter currentFilter;
        private int lastAppliedColor;
        private int lastColor;
        private final ImageView moveImageView;
        private ValueAnimator moveImageViewAnimator;
        private boolean needDivider;
        private final ImageView optionsImageView;
        float progressToLock;
        private final ImageView shareImageView;
        private boolean shareLoading;
        private final LoadingDrawable shareLoadingDrawable;
        private final SimpleTextView textView;
        private final TextView valueTextView;

        public FilterCell(Context context) {
            super(context);
            this.lastColor = -2;
            this.lastAppliedColor = -1;
            this.shareLoading = false;
            setWillNotDraw(false);
            ImageView imageView = new ImageView(context);
            this.moveImageView = imageView;
            imageView.setFocusable(false);
            ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
            imageView.setScaleType(scaleType);
            imageView.setImageResource(R.drawable.list_reorder);
            int i = Theme.key_stickers_menu;
            int color = Theme.getColor(i);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            imageView.setColorFilter(new PorterDuffColorFilter(color, mode));
            imageView.setContentDescription(LocaleController.getString(R.string.FilterReorder));
            imageView.setClickable(true);
            addView(imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 16, 7.0f, 0.0f, 6.0f, 0.0f));
            View view = new View(context);
            this.colorImageView = view;
            addView(view, LayoutHelper.createFrame(20, 20.0f, (LocaleController.isRTL ? 5 : 3) | 16, 22.0f, 0.0f, 22.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView = simpleTextView;
            simpleTextView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
            simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            simpleTextView.setTextSize(16);
            simpleTextView.setMaxLines(1);
            simpleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.other_lockedfolders2);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), mode));
            simpleTextView.setRightDrawable(drawable);
            simpleTextView.setEmojiColor(Theme.getColor(Theme.key_featuredStickers_addButton, ((BaseFragment) FiltersSetupActivity.this).resourceProvider));
            boolean z = LocaleController.isRTL;
            addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 48, z ? 80.0f : 64.0f, 10.0f, z ? 64.0f : 80.0f, 0.0f));
            TextView textView = new TextView(context);
            this.valueTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            textView.setTextSize(1, 13.0f);
            textView.setTypeface(AndroidUtilities.regular());
            textView.setGravity(LocaleController.isRTL ? 5 : 3);
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setPadding(0, 0, 0, 0);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            boolean z2 = LocaleController.isRTL;
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 80.0f : 64.0f, 35.0f, z2 ? 64.0f : 80.0f, 0.0f));
            textView.setVisibility(8);
            LoadingDrawable loadingDrawable = new LoadingDrawable();
            this.shareLoadingDrawable = loadingDrawable;
            loadingDrawable.setAppearByGradient(true);
            loadingDrawable.setGradientScale(2.0f);
            int i2 = Theme.key_listSelector;
            int color2 = Theme.getColor(i2);
            loadingDrawable.setColors(Theme.multAlpha(color2, 0.4f), Theme.multAlpha(color2, 1.0f), Theme.multAlpha(color2, 0.9f), Theme.multAlpha(color2, 1.7f));
            final int iDp = AndroidUtilities.dp(1.0f);
            loadingDrawable.strokePaint.setStrokeWidth(iDp);
            loadingDrawable.setRadiiDp(40.0f);
            ImageView imageView2 = new ImageView(context) { // from class: org.telegram.ui.FiltersSetupActivity.FilterCell.1
                @Override // android.widget.ImageView, android.view.View
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (FilterCell.this.shareLoading) {
                        LoadingDrawable loadingDrawable2 = FilterCell.this.shareLoadingDrawable;
                        int i3 = iDp;
                        loadingDrawable2.setBounds(i3 / 2, i3 / 2, getWidth() - (iDp / 2), getHeight() - (iDp / 2));
                        FilterCell.this.shareLoadingDrawable.draw(canvas);
                    }
                }

                @Override // android.widget.ImageView, android.view.View
                public boolean verifyDrawable(Drawable drawable2) {
                    return drawable2 == FilterCell.this.shareLoadingDrawable || super.verifyDrawable(drawable2);
                }
            };
            this.shareImageView = imageView2;
            loadingDrawable.setCallback(imageView2);
            imageView2.setFocusable(false);
            imageView2.setScaleType(scaleType);
            imageView2.setBackground(Theme.createSelectorDrawable(color2));
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), mode));
            imageView2.setContentDescription(LocaleController.getString(R.string.FilterShare));
            imageView2.setVisibility(8);
            imageView2.setImageResource(R.drawable.msg_link_folder);
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), mode));
            boolean z3 = LocaleController.isRTL;
            addView(imageView2, LayoutHelper.createFrame(40, 40.0f, (z3 ? 3 : 5) | 16, z3 ? 52.0f : 6.0f, 0.0f, z3 ? 6.0f : 52.0f, 0.0f));
            imageView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FiltersSetupActivity$FilterCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.f$0.lambda$new$1(view2);
                }
            });
            ImageView imageView3 = new ImageView(context);
            this.optionsImageView = imageView3;
            imageView3.setFocusable(false);
            imageView3.setScaleType(scaleType);
            imageView3.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(i2)));
            imageView3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), mode));
            imageView3.setImageResource(R.drawable.msg_actions);
            imageView3.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
            addView(imageView3, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 3 : 5) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        }

        public boolean lambda$onCreateViewHolder$0(FilterCell filterCell, View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return false;
            }
            FiltersSetupActivity.this.itemTouchHelper.startDrag(FiltersSetupActivity.this.listView.getChildViewHolder(filterCell));
            return false;
        }

        public void lambda$onCreateViewHolder$2(Boolean bool) {
            FiltersSetupActivity.this.updateRows(true);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$5(final MessagesController.DialogFilter dialogFilter, AlertDialog alertDialog, int i) {
            final AlertDialog alertDialog2;
            if (FiltersSetupActivity.this.getParentActivity() != null) {
                alertDialog2 = new AlertDialog(FiltersSetupActivity.this.getParentActivity(), 3);
                alertDialog2.setCanCancel(false);
                alertDialog2.show();
            } else {
                alertDialog2 = null;
            }
            TLRPC.TL_messages_updateDialogFilter tL_messages_updateDialogFilter = new TLRPC.TL_messages_updateDialogFilter();
            tL_messages_updateDialogFilter.id = dialogFilter.id;
            FiltersSetupActivity.this.getConnectionsManager().sendRequest(tL_messages_updateDialogFilter, new RequestDelegate() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda8
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$onCreateViewHolder$4(alertDialog2, dialogFilter, tLObject, tL_error);
                }
            });
        }

        public /* synthetic */ void lambda$onCreateViewHolder$4(final AlertDialog alertDialog, final MessagesController.DialogFilter dialogFilter, TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onCreateViewHolder$3(alertDialog, dialogFilter);
                }
            });
        }

        public /* synthetic */ void lambda$onCreateViewHolder$3(AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter) {
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            FiltersSetupActivity.this.getMessagesController().removeFilter(dialogFilter);
            FiltersSetupActivity.this.getMessagesStorage().deleteDialogFilter(dialogFilter);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            if (i == 0) {
                headerCell = new HeaderCell(this.mContext);
            } else if (i == 1) {
                headerCell = new HintInnerCell(this.mContext, R.raw.filters, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.CreateNewFilterInfo, new Object[0])));
            } else if (i == 2) {
                final FilterCell filterCell = FiltersSetupActivity.this.new FilterCell(this.mContext);
                filterCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                filterCell.setOnReorderButtonTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnTouchListener
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        return this.f$0.lambda$onCreateViewHolder$0(filterCell, view, motionEvent);
                    }
                });
                filterCell.setOnOptionsClick(new View.OnClickListener() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$onCreateViewHolder$7(view);
                    }
                });
                headerCell = filterCell;
            } else if (i == 3) {
                headerCell = new TextInfoPrivacyCell(this.mContext);
            } else if (i == 4) {
                headerCell = new TextCell(this.mContext);
            } else if (i == 6) {
                headerCell = new TextCheckCell(this.mContext);
            } else {
                final SuggestedFilterCell suggestedFilterCell = new SuggestedFilterCell(this.mContext);
                suggestedFilterCell.setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$onCreateViewHolder$9(suggestedFilterCell, view);
                    }
                });
                headerCell = suggestedFilterCell;
            }
            return new RecyclerListView.Holder(headerCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$9(SuggestedFilterCell suggestedFilterCell, View view) {
            final TLRPC.TL_dialogFilterSuggested suggestedFilter = suggestedFilterCell.getSuggestedFilter();
            MessagesController.DialogFilter dialogFilter = new MessagesController.DialogFilter();
            TLRPC.TL_textWithEntities tL_textWithEntities = suggestedFilter.filter.title;
            dialogFilter.name = tL_textWithEntities.text;
            dialogFilter.entities = tL_textWithEntities.entities;
            dialogFilter.id = 2;
            while (FiltersSetupActivity.this.getMessagesController().dialogFiltersById.get(dialogFilter.id) != null) {
                dialogFilter.id++;
            }
            dialogFilter.order = FiltersSetupActivity.this.getMessagesController().getDialogFilters().size();
            dialogFilter.unreadCount = -1;
            dialogFilter.pendingUnreadCount = -1;
            int i = 0;
            while (i < 2) {
                TLRPC.DialogFilter dialogFilter2 = suggestedFilter.filter;
                ArrayList<TLRPC.InputPeer> arrayList = i == 0 ? dialogFilter2.include_peers : dialogFilter2.exclude_peers;
                ArrayList<Long> arrayList2 = i == 0 ? dialogFilter.alwaysShow : dialogFilter.neverShow;
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC.InputPeer inputPeer = arrayList.get(i2);
                    long j = inputPeer.user_id;
                    if (j == 0) {
                        long j2 = inputPeer.chat_id;
                        j = j2 != 0 ? -j2 : -inputPeer.channel_id;
                    }
                    arrayList2.add(Long.valueOf(j));
                }
                i++;
            }
            TLRPC.DialogFilter dialogFilter3 = suggestedFilter.filter;
            if (dialogFilter3.groups) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_GROUPS;
            }
            if (dialogFilter3.bots) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_BOTS;
            }
            if (dialogFilter3.contacts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
            }
            if (dialogFilter3.non_contacts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
            }
            if (dialogFilter3.broadcasts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
            }
            if (dialogFilter3.exclude_archived) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
            }
            if (dialogFilter3.exclude_read) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
            }
            if (dialogFilter3.exclude_muted) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
            }
            String str = TextUtils.isEmpty(dialogFilter3.emoticon) ? FolderIcons.getEmoticonFromFlags(dialogFilter.flags).second : suggestedFilter.filter.emoticon;
            dialogFilter.emoticon = str;
            FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.entities, dialogFilter.title_noanimate, dialogFilter.color, str, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, true, true, true, true, true, FiltersSetupActivity.this, new Runnable() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onCreateViewHolder$8(suggestedFilter);
                }
            });
        }

        public /* synthetic */ void lambda$onCreateViewHolder$8(TLRPC.TL_dialogFilterSuggested tL_dialogFilterSuggested) {
            FiltersSetupActivity.this.getMessagesController().suggestedFilters.remove(tL_dialogFilterSuggested);
            FiltersSetupActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ItemInner itemInner = (ItemInner) FiltersSetupActivity.this.items.get(i);
            if (itemInner == null) {
                return;
            }
            int i2 = i + 1;
            boolean z = i2 < FiltersSetupActivity.this.items.size() && ((ItemInner) FiltersSetupActivity.this.items.get(i2)).viewType != 3;
            boolean z2 = i2 >= FiltersSetupActivity.this.items.size();
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ((HeaderCell) viewHolder.itemView).setText(itemInner.text);
                return;
            }
            if (itemViewType == 2) {
                ((FilterCell) viewHolder.itemView).setFilter(itemInner.filter, z, i);
                return;
            }
            if (itemViewType == 3) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (TextUtils.isEmpty(itemInner.text)) {
                    textInfoPrivacyCell.setText(null);
                    textInfoPrivacyCell.setFixedSize(12);
                } else {
                    textInfoPrivacyCell.setFixedSize(0);
                    textInfoPrivacyCell.setText(itemInner.text);
                }
                textInfoPrivacyCell.setBottomPadding(z2 ? 32 : 17);
                return;
            }
            if (itemViewType != 4) {
                if (itemViewType == 5) {
                    ((SuggestedFilterCell) viewHolder.itemView).setFilter(itemInner.suggested, z);
                    return;
                } else {
                    if (itemViewType != 6) {
                        return;
                    }
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    textCheckCell.setTextAndCheck(itemInner.text, FiltersSetupActivity.this.getMessagesController().folderTags, z);
                    textCheckCell.setCheckBoxIcon(FiltersSetupActivity.this.getUserConfig().isPremium() ? 0 : R.drawable.permission_locked);
                    return;
                }
            }
            TextCell textCell = (TextCell) viewHolder.itemView;
            Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.poll_add_circle);
            Drawable drawable2 = this.mContext.getResources().getDrawable(R.drawable.poll_add_plus);
            int color = Theme.getColor(Theme.key_switchTrackChecked);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            drawable.setColorFilter(new PorterDuffColorFilter(color, mode));
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_checkboxCheck), mode));
            textCell.setTextAndIcon(((Object) itemInner.text) + _UrlKt.FRAGMENT_ENCODE_SET, new CombinedDrawable(drawable, drawable2), false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            ItemInner itemInner;
            if (i < 0 || i >= FiltersSetupActivity.this.items.size() || (itemInner = (ItemInner) FiltersSetupActivity.this.items.get(i)) == null) {
                return 3;
            }
            return itemInner.viewType;
        }

        public void swapElements(int i, int i2) {
            MessagesController.DialogFilter dialogFilter;
            MessagesController.DialogFilter dialogFilter2;
            if (i < FiltersSetupActivity.this.filtersStartPosition || i2 < FiltersSetupActivity.this.filtersStartPosition) {
                return;
            }
            ItemInner itemInner = (ItemInner) FiltersSetupActivity.this.items.get(i);
            ItemInner itemInner2 = (ItemInner) FiltersSetupActivity.this.items.get(i2);
            if (itemInner == null || itemInner2 == null || (dialogFilter = itemInner.filter) == null || (dialogFilter2 = itemInner2.filter) == null) {
                return;
            }
            int i3 = dialogFilter.order;
            dialogFilter.order = dialogFilter2.order;
            dialogFilter2.order = i3;
            ArrayList<MessagesController.DialogFilter> arrayList = FiltersSetupActivity.this.getMessagesController().dialogFilters;
            try {
                arrayList.set(i - FiltersSetupActivity.this.filtersStartPosition, itemInner2.filter);
                arrayList.set(i2 - FiltersSetupActivity.this.filtersStartPosition, itemInner.filter);
            } catch (Exception unused) {
            }
            FiltersSetupActivity.this.orderChanged = true;
            FiltersSetupActivity.this.updateRows(true);
        }

        public void moveElementToStart(int i) {
            ArrayList<MessagesController.DialogFilter> arrayList = FiltersSetupActivity.this.getMessagesController().dialogFilters;
            if (i < 0 || i >= arrayList.size()) {
                return;
            }
            arrayList.add(0, arrayList.remove(i));
            for (int i2 = 0; i2 <= i; i2++) {
                arrayList.get(i2).order = i2;
            }
            FiltersSetupActivity.this.orderChanged = true;
            FiltersSetupActivity.this.updateRows(true);
        }
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            FiltersSetupActivity.this.adapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void resetDefaultPosition() {
            if (UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                return;
            }
            ArrayList<MessagesController.DialogFilter> dialogFilters = FiltersSetupActivity.this.getMessagesController().getDialogFilters();
            for (int i = 0; i < dialogFilters.size(); i++) {
                if (dialogFilters.get(i).isDefault() && i != 0) {
                    FiltersSetupActivity.this.adapter.moveElementToStart(i);
                    FiltersSetupActivity.this.listView.scrollToPosition(0);
                    FiltersSetupActivity.this.onDefaultTabMoved();
                    return;
                }
            }
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                FiltersSetupActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            } else {
                AndroidUtilities.cancelRunOnUIThread(new Runnable() { // from class: org.telegram.ui.FiltersSetupActivity$TouchHelperCallback$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.resetDefaultPosition();
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FiltersSetupActivity$TouchHelperCallback$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.resetDefaultPosition();
                    }
                }, 320L);
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
            viewHolder.itemView.setTag(R.id.dragging, null);
        }
    }

    public void onDefaultTabMoved() {
        try {
            this.fragmentView.performHapticFeedback(VibratorUtils.getType(3), 1);
        } catch (Exception unused) {
        }
        BulletinFactory.of(this).createSimpleBulletin(R.raw.filter_reorder, AndroidUtilities.replaceTags(LocaleController.formatString("LimitReachedReorderFolder", R.string.LimitReachedReorderFolder, LocaleController.getString(R.string.FilterAllChats))), LocaleController.getString(R.string.PremiumMore), 5000, new Runnable() { // from class: org.telegram.ui.FiltersSetupActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDefaultTabMoved$7();
            }
        }).show();
    }

    public /* synthetic */ void lambda$onDefaultTabMoved$7() {
        showDialog(new PremiumFeatureBottomSheet(this, 9, true));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, FilterCell.class, SuggestedFilterCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        int i = Theme.key_stickers_menu;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"moveImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menuSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        return arrayList;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onInsets(int i, int i2, int i3, int i4) {
        this.listView.setPadding(0, 0, 0, i4);
        this.listView.setClipToPadding(false);
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.setTranslationY(-i4);
        }
    }
}
