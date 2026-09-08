package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogMeUrlCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.DialogsHintCell;
import org.telegram.ui.Cells.DialogsRequestedEmptyCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.RequestPeerRequirementsCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.ArchiveHelp;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;

public class DialogsAdapter extends RecyclerListView.SelectionAdapter implements DialogCell.DialogCellDelegate {
    private static final boolean ALLOW_UPDATE_IN_BACKGROUND = BuildVars.DEBUG_PRIVATE_VERSION;
    private boolean allowForwardAsStories;
    private Drawable arrowDrawable;
    private boolean collapsedView;
    public final long communityId;
    private int currentAccount;
    private int currentCount;
    private int dialogsCount;
    private boolean dialogsListFrozen;
    private int dialogsType;
    private int folderId;
    private boolean forceShowEmptyCell;
    private boolean forceUpdatingContacts;
    private boolean hasChatlistHint;
    private boolean hasHints;
    boolean isCalculatingDiff;
    public boolean isEmpty;
    private boolean isOnlySelect;
    private boolean isReordering;
    private boolean isTransitionSupport;
    private long lastSortTime;
    private Context mContext;
    private ArrayList<TLRPC.TL_contact> onlineContacts;
    private long openedDialogId;
    private DialogsActivity parentFragment;
    private DialogsPreloader preloader;
    private PullForegroundDrawable pullForegroundDrawable;
    RecyclerListView recyclerListView;
    private TLRPC.RequestPeerType requestPeerType;
    private ArrayList<Long> selectedDialogs;
    boolean updateListPending;
    private boolean firstUpdate = true;
    ArrayList<ItemInternal> itemInternals = new ArrayList<>();
    ArrayList<ItemInternal> oldItems = new ArrayList<>();
    int stableIdPointer = 10;
    LongSparseIntArray dialogsStableIds = new LongSparseIntArray();
    HashMap<String, Integer> dialogsHeaderStableIds = new HashMap<>();
    public int lastDialogsEmptyType = -1;

    public ViewPager getArchiveHintCellPager() {
        return null;
    }

    public boolean isDataSetChanged() {
        return true;
    }

    public void onArchiveSettingsClick() {
    }

    @Override // org.telegram.ui.Cells.DialogCell.DialogCellDelegate
    public void onButtonClicked(DialogCell dialogCell) {
    }

    @Override // org.telegram.ui.Cells.DialogCell.DialogCellDelegate
    public void onButtonLongPress(DialogCell dialogCell) {
    }

    public void onCreateGroupForThisClick() {
    }

    public void onOpenBot(TLRPC.User user) {
    }

    public boolean showOpenBotButton() {
        return false;
    }

    public DialogsAdapter(DialogsActivity dialogsActivity, Context context, int i, int i2, boolean z, ArrayList<Long> arrayList, int i3, TLRPC.RequestPeerType requestPeerType) {
        this.mContext = context;
        this.parentFragment = dialogsActivity;
        this.dialogsType = i;
        this.folderId = i2;
        this.isOnlySelect = z;
        this.hasHints = i2 == 0 && i == 0 && !z;
        this.selectedDialogs = arrayList;
        this.currentAccount = i3;
        this.communityId = dialogsActivity != null ? dialogsActivity.getCommunityId() : 0L;
        if (i2 == 0) {
            this.preloader = new DialogsPreloader();
        }
        this.requestPeerType = requestPeerType;
    }

    public void setRecyclerListView(RecyclerListView recyclerListView) {
        this.recyclerListView = recyclerListView;
    }

    public void setOpenedDialogId(long j) {
        this.openedDialogId = j;
    }

    public void onReorderStateChanged(boolean z) {
        this.isReordering = z;
    }

    public int fixPosition(int i) {
        if (this.hasChatlistHint) {
            i--;
        }
        if (this.hasHints) {
            i -= MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
        }
        if (this.allowForwardAsStories && this.dialogsType == 3) {
            i--;
        }
        int i2 = this.dialogsType;
        if (i2 == 11 || i2 == 13) {
            return i - 2;
        }
        return i2 == 12 ? i - 1 : i;
    }

    public void setDialogsType(int i) {
        this.dialogsType = i;
        notifyDataSetChanged();
    }

    public void setAllowForwardAsStories(boolean z) {
        this.allowForwardAsStories = z;
    }

    public boolean isAllowForwardAsStories() {
        return this.allowForwardAsStories;
    }

    public int getDialogsType() {
        return this.dialogsType;
    }

    public int getDialogsCount() {
        return this.dialogsCount;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return this.itemInternals.get(i).stableId;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        int size = this.itemInternals.size();
        this.currentCount = size;
        return size;
    }

    public int findDialogPosition(long j) {
        for (int i = 0; i < this.itemInternals.size(); i++) {
            if (this.itemInternals.get(i).dialog != null && this.itemInternals.get(i).dialog.id == j) {
                return i;
            }
        }
        return -1;
    }

    public int fixScrollGap(RecyclerListView recyclerListView, int i, int i2, boolean z, boolean z2, boolean z3, boolean z4) {
        int iDp = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 76.0f : 70.0f);
        int paddingTop = ((recyclerListView.getPaddingTop() + i2) - (i * iDp)) - i;
        if (z) {
            paddingTop += iDp;
        }
        int paddingTop2 = recyclerListView.getPaddingTop();
        return paddingTop > paddingTop2 ? (i2 + paddingTop2) - paddingTop : i2;
    }

    public class ItemInternal extends AdapterWithDiffUtils.Item {
        private TLRPC.Chat chat;
        TL_chatlists.TL_chatlists_chatlistUpdates chatlistUpdates;
        TLRPC.TL_contact contact;
        TLRPC.Dialog dialog;
        private long dialogId;
        private int emptyType;
        private boolean isFolder;
        boolean isForumCell;
        private boolean pinned;
        TLRPC.RecentMeUrl recentMeUrl;
        private final int stableId;
        private String title;
        private TLRPC.User user;

        public ItemInternal(TL_chatlists.TL_chatlists_chatlistUpdates tL_chatlists_chatlistUpdates) {
            super(17, true);
            this.chatlistUpdates = tL_chatlists_chatlistUpdates;
            int i = DialogsAdapter.this.stableIdPointer;
            DialogsAdapter.this.stableIdPointer = i + 1;
            this.stableId = i;
        }

        public ItemInternal(int i, String str) {
            super(i, false);
            Integer num = DialogsAdapter.this.dialogsHeaderStableIds.get(str);
            if (num != null) {
                this.stableId = num.intValue();
            } else {
                int i2 = DialogsAdapter.this.stableIdPointer;
                DialogsAdapter.this.stableIdPointer = i2 + 1;
                this.stableId = i2;
                DialogsAdapter.this.dialogsHeaderStableIds.put(str, Integer.valueOf(i2));
            }
            this.title = str;
        }

        public ItemInternal(int i, TLRPC.User user) {
            super(i, false);
            this.user = user;
            long j = user.id;
            this.dialogId = j;
            int i2 = DialogsAdapter.this.dialogsStableIds.get(j, -1);
            if (i2 >= 0) {
                this.stableId = i2;
                return;
            }
            int i3 = DialogsAdapter.this.stableIdPointer;
            DialogsAdapter.this.stableIdPointer = i3 + 1;
            this.stableId = i3;
            DialogsAdapter.this.dialogsStableIds.put(user.id, i3);
        }

        public ItemInternal(int i, TLRPC.Chat chat) {
            super(i, false);
            this.chat = chat;
            long j = chat.id;
            this.dialogId = -j;
            int i2 = DialogsAdapter.this.dialogsStableIds.get(-j, -1);
            if (i2 >= 0) {
                this.stableId = i2;
                return;
            }
            int i3 = DialogsAdapter.this.stableIdPointer;
            DialogsAdapter.this.stableIdPointer = i3 + 1;
            this.stableId = i3;
            DialogsAdapter.this.dialogsStableIds.put(-chat.id, i3);
        }

        public ItemInternal(int i, TLRPC.Dialog dialog) {
            super(i, true);
            this.dialog = dialog;
            if (dialog != null) {
                int i2 = DialogsAdapter.this.dialogsStableIds.get(dialog.id, -1);
                if (i2 >= 0) {
                    this.stableId = i2;
                } else {
                    int i3 = DialogsAdapter.this.stableIdPointer;
                    DialogsAdapter.this.stableIdPointer = i3 + 1;
                    this.stableId = i3;
                    DialogsAdapter.this.dialogsStableIds.put(dialog.id, i3);
                }
            } else if (i == 19) {
                this.stableId = 5;
            } else {
                int i4 = DialogsAdapter.this.stableIdPointer;
                DialogsAdapter.this.stableIdPointer = i4 + 1;
                this.stableId = i4;
            }
            if (dialog != null) {
                if (DialogsAdapter.this.dialogsType == 7 || DialogsAdapter.this.dialogsType == 8) {
                    MessagesController.DialogFilter dialogFilter = MessagesController.getInstance(DialogsAdapter.this.currentAccount).selectedDialogFilter[DialogsAdapter.this.dialogsType == 8 ? (char) 1 : (char) 0];
                    this.pinned = dialogFilter != null && dialogFilter.pinnedDialogs.indexOfKey(dialog.id) >= 0;
                } else {
                    this.pinned = dialog.pinned;
                }
                this.isFolder = dialog.isFolder;
                this.isForumCell = MessagesController.getInstance(DialogsAdapter.this.currentAccount).isForum(dialog.id);
            }
        }

        public ItemInternal(int i, TLRPC.RecentMeUrl recentMeUrl) {
            super(i, true);
            this.recentMeUrl = recentMeUrl;
            int i2 = DialogsAdapter.this.stableIdPointer;
            DialogsAdapter.this.stableIdPointer = i2 + 1;
            this.stableId = i2;
        }

        public ItemInternal(int i) {
            super(i, true);
            this.emptyType = i;
            if (i == 10) {
                this.stableId = 1;
            } else {
                if (this.viewType == 19) {
                    this.stableId = 5;
                    return;
                }
                int i2 = DialogsAdapter.this.stableIdPointer;
                DialogsAdapter.this.stableIdPointer = i2 + 1;
                this.stableId = i2;
            }
        }

        public ItemInternal(int i, int i2) {
            super(i, true);
            this.emptyType = i2;
            int i3 = DialogsAdapter.this.stableIdPointer;
            DialogsAdapter.this.stableIdPointer = i3 + 1;
            this.stableId = i3;
        }

        public ItemInternal(int i, TLRPC.TL_contact tL_contact) {
            super(i, true);
            this.contact = tL_contact;
            if (tL_contact != null) {
                int i2 = DialogsAdapter.this.dialogsStableIds.get(tL_contact.user_id, -1);
                if (i2 > 0) {
                    this.stableId = i2;
                    return;
                }
                int i3 = DialogsAdapter.this.stableIdPointer;
                DialogsAdapter.this.stableIdPointer = i3 + 1;
                this.stableId = i3;
                DialogsAdapter.this.dialogsStableIds.put(this.contact.user_id, i3);
                return;
            }
            int i4 = DialogsAdapter.this.stableIdPointer;
            DialogsAdapter.this.stableIdPointer = i4 + 1;
            this.stableId = i4;
        }

        public boolean compare(ItemInternal itemInternal) {
            TLRPC.TL_contact tL_contact;
            String str;
            TLRPC.Dialog dialog;
            TLRPC.Dialog dialog2;
            int i = this.viewType;
            if (i != itemInternal.viewType) {
                return false;
            }
            if (i == 0) {
                TLRPC.Dialog dialog3 = this.dialog;
                return dialog3 != null && (dialog2 = itemInternal.dialog) != null && dialog3.id == dialog2.id && this.isFolder == itemInternal.isFolder && this.isForumCell == itemInternal.isForumCell && this.pinned == itemInternal.pinned;
            }
            if (i == 14) {
                TLRPC.Dialog dialog4 = this.dialog;
                return dialog4 != null && (dialog = itemInternal.dialog) != null && dialog4.id == dialog.id && dialog4.isFolder == dialog.isFolder;
            }
            if (i == 4) {
                TLRPC.RecentMeUrl recentMeUrl = this.recentMeUrl;
                return (recentMeUrl == null || itemInternal.recentMeUrl == null || (str = recentMeUrl.url) == null || !str.equals(str)) ? false : true;
            }
            if (i == 6) {
                TLRPC.TL_contact tL_contact2 = this.contact;
                return (tL_contact2 == null || (tL_contact = itemInternal.contact) == null || tL_contact2.user_id != tL_contact.user_id) ? false : true;
            }
            if (i == 5) {
                return this.emptyType == itemInternal.emptyType;
            }
            return i != 10;
        }

        public int hashCode() {
            return Objects.hash(this.dialog, this.chat, this.recentMeUrl, this.contact, this.title);
        }
    }

    public Object getItem(int i) {
        if (i >= 0 && i < this.itemInternals.size()) {
            ItemInternal itemInternal = this.itemInternals.get(i);
            if (itemInternal.title != null) {
                return itemInternal.title;
            }
            if (itemInternal.chat != null) {
                return itemInternal.chat;
            }
            if (itemInternal.user != null) {
                return itemInternal.user;
            }
            TLRPC.Dialog dialog = itemInternal.dialog;
            if (dialog != null) {
                return dialog;
            }
            if (itemInternal.contact != null) {
                return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(itemInternal.contact.user_id));
            }
            TLRPC.RecentMeUrl recentMeUrl = itemInternal.recentMeUrl;
            if (recentMeUrl != null) {
                return recentMeUrl;
            }
        }
        return null;
    }

    public void sortOnlineContacts(boolean z) {
        if (this.onlineContacts != null) {
            if (!z || SystemClock.elapsedRealtime() - this.lastSortTime >= 2000) {
                this.lastSortTime = SystemClock.elapsedRealtime();
                try {
                    final int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                    final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
                    Collections.sort(this.onlineContacts, new Comparator() { // from class: org.telegram.ui.Adapters.DialogsAdapter$$ExternalSyntheticLambda0
                        @Override // java.util.Comparator
                        public final int compare(Object obj, Object obj2) {
                            return DialogsAdapter.$r8$lambda$_IyqYv0ErkEI_Omrn2azgDWluho(messagesController, currentTime, (TLRPC.TL_contact) obj, (TLRPC.TL_contact) obj2);
                        }
                    });
                    if (z) {
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    void lambda$onBindViewHolder$4() {
        this.parentFragment.setScrollDisabled(false);
    }

    public long val$dialog_id;

            public AnonymousClass1(long j) {
                this.val$dialog_id = j;
            }

            @Override 
            public void onMessagesLoaded(final boolean z) {
                final long j = this.val$dialog_id;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onMessagesLoaded$0(z, j);
                    }
                });
            }

            public void lambda$onError$1(long j) {
                if (DialogsPreloader.this.loadingDialogs.remove(Long.valueOf(j))) {
                    DialogsPreloader.this.preloadedErrorMap.add(Long.valueOf(j));
                    DialogsPreloader dialogsPreloader = DialogsPreloader.this;
                    dialogsPreloader.currentRequestCount--;
                    dialogsPreloader.start();
                }
            }
        }

        public boolean isReady(long j) {
            return this.dialogsReadyMap.contains(Long.valueOf(j));
        }

        public void remove(long j) {
            this.preloadDialogsPool.remove(Long.valueOf(j));
        }

        public void clear() {
            this.dialogsReadyMap.clear();
            this.preloadedErrorMap.clear();
            this.loadingDialogs.clear();
            this.preloadDialogsPool.clear();
            this.currentRequestCount = 0;
            this.networkRequestCount = 0;
            AndroidUtilities.cancelRunOnUIThread(this.clearNetworkRequestCount);
            updateList();
        }

        public void resume() {
            this.resumed = true;
            start();
        }

        public void pause() {
            this.resumed = false;
        }
    }

    public int getCurrentCount() {
        return this.currentCount;
    }

    public void setForceShowEmptyCell(boolean z) {
        this.forceShowEmptyCell = z;
    }

    private MessagesController.DialogFilter getCurrentFilter() {
        int i = this.dialogsType;
        if (i == 7 || i == 8) {
            return MessagesController.getInstance(this.currentAccount).selectedDialogFilter[this.dialogsType - 7];
        }
        return null;
    }

    public class LastEmptyView extends FrameLayout {
        public boolean moving;

        public LastEmptyView(Context context) {
            super(context);
        }

        /* JADX WARN: Code duplicated, block: B:53:0x00f7  */
        /* JADX WARN: Code duplicated, block: B:76:0x0168 A[PHI: r14
  0x0168: PHI (r14v9 int) = (r14v7 int), (r14v16 int) binds: [B:88:0x0195, B:75:0x0166] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARN: Code duplicated, block: B:90:0x0198  */
        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            int currentActionBarHeight;
            DialogsAdapter dialogsAdapter;
            int i3;
            int size = DialogsAdapter.this.itemInternals.size();
            boolean z = DialogsAdapter.this.folderId == 0 && DialogsAdapter.this.dialogsType == 0 && MessagesController.getInstance(DialogsAdapter.this.currentAccount).dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null;
            View view = (View) getParent();
            int i4 = view instanceof BlurredRecyclerView ? ((BlurredRecyclerView) view).blurTopPadding : 0;
            boolean z2 = DialogsAdapter.this.collapsedView;
            int paddingTop = view.getPaddingTop();
            int paddingBottom = view.getPaddingBottom();
            int i5 = paddingTop - i4;
            if (DialogsAdapter.this.folderId == 1 && size == 1 && DialogsAdapter.this.itemInternals.get(0).viewType == 19) {
                currentActionBarHeight = View.MeasureSpec.getSize(i2);
                if (currentActionBarHeight == 0) {
                    currentActionBarHeight = view.getMeasuredHeight();
                }
                if (currentActionBarHeight == 0) {
                    currentActionBarHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight;
                }
                if (DialogsAdapter.this.parentFragment.hasStories) {
                    currentActionBarHeight += AndroidUtilities.dp(81.0f);
                }
            } else if (size == 0 || (i5 == 0 && !z)) {
                currentActionBarHeight = 0;
            } else {
                int size2 = View.MeasureSpec.getSize(i2);
                if (size2 == 0) {
                    size2 = view.getMeasuredHeight();
                }
                if (size2 == 0) {
                    size2 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight;
                }
                int i6 = size2 - i4;
                int iDp = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 76.0f : 70.0f);
                int i7 = 0;
                int iDp2 = 0;
                while (true) {
                    dialogsAdapter = DialogsAdapter.this;
                    if (i7 >= size) {
                        break;
                    }
                    int i8 = dialogsAdapter.itemInternals.get(i7).viewType;
                    DialogsAdapter dialogsAdapter2 = DialogsAdapter.this;
                    if (i8 == 0) {
                        if (!dialogsAdapter2.itemInternals.get(i7).isForumCell || z2) {
                            iDp2 += iDp;
                        } else {
                            iDp2 += AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 86.0f : 91.0f);
                        }
                    } else if (dialogsAdapter2.itemInternals.get(i7).viewType == 1) {
                        iDp2 += iDp;
                    }
                    i7++;
                }
                int size3 = iDp2 + (size - 1);
                if (dialogsAdapter.onlineContacts != null) {
                    size3 += (DialogsAdapter.this.onlineContacts.size() * AndroidUtilities.dp(58.0f)) + (DialogsAdapter.this.onlineContacts.size() - 1) + AndroidUtilities.dp(52.0f);
                }
                int i9 = z ? iDp + 1 : 0;
                if (size3 < i6) {
                    currentActionBarHeight = ((i6 - size3) + i9) - paddingBottom;
                    if (i5 != 0) {
                        currentActionBarHeight -= AndroidUtilities.statusBarHeight;
                        if (!z2 && !DialogsAdapter.this.isTransitionSupport) {
                            currentActionBarHeight -= ActionBar.getCurrentActionBarHeight();
                            if (getParent() instanceof DialogsActivity.DialogsRecyclerView) {
                                i3 = ((DialogsActivity.DialogsRecyclerView) getParent()).additionalPadding;
                                currentActionBarHeight -= i3;
                            }
                        } else if (z2) {
                            currentActionBarHeight -= i5;
                        }
                    }
                } else {
                    int i10 = size3 - i6;
                    if (i10 < i9) {
                        currentActionBarHeight = (i9 - i10) - paddingBottom;
                        if (i5 != 0) {
                            currentActionBarHeight -= AndroidUtilities.statusBarHeight;
                            if (!z2 && !DialogsAdapter.this.isTransitionSupport) {
                                currentActionBarHeight -= ActionBar.getCurrentActionBarHeight();
                                if (getParent() instanceof DialogsActivity.DialogsRecyclerView) {
                                    i3 = ((DialogsActivity.DialogsRecyclerView) getParent()).additionalPadding;
                                    currentActionBarHeight -= i3;
                                }
                            } else if (z2) {
                                currentActionBarHeight -= i5;
                            }
                        }
                    } else {
                        currentActionBarHeight = 0;
                    }
                }
            }
            int iDp3 = currentActionBarHeight >= 0 ? currentActionBarHeight : 0;
            if (DialogsAdapter.this.isTransitionSupport) {
                iDp3 += AndroidUtilities.dp(1000.0f);
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(iDp3, TLObject.FLAG_30));
        }
    }

    private void updateItemListForCommunity() {
        ArrayList<MessagesController.CommunityPeerDialog> arrayList;
        String string;
        this.itemInternals.clear();
        updateHasHints();
        MessagesController.CommunityPeersDialog communityPeersDialogBuildCommunityPeers = MessagesController.getInstance(this.currentAccount).buildCommunityPeers(this.communityId);
        this.dialogsCount = communityPeersDialogBuildCommunityPeers.getDialogsCount();
        this.isEmpty = false;
        int i = this.dialogsType == 3 ? 2 : 4;
        for (int i2 = 0; i2 < i; i2++) {
            if (i2 == 0) {
                arrayList = communityPeersDialogBuildCommunityPeers.chatsYouAreIn;
                string = LocaleController.getString(R.string.CommunitySectionChatsYouAreIn);
            } else if (i2 == 1) {
                arrayList = communityPeersDialogBuildCommunityPeers.chatsYouCanView;
                string = LocaleController.getString(R.string.CommunitySectionChatsYouCanView);
            } else if (i2 == 2) {
                arrayList = communityPeersDialogBuildCommunityPeers.chatsYouCanJoin;
                string = LocaleController.getString(R.string.CommunitySectionChatsYouCanRequestToJoin);
            } else {
                arrayList = communityPeersDialogBuildCommunityPeers.chatsOther;
                string = LocaleController.getString(R.string.CommunitySectionHiddenChats);
            }
            if (!arrayList.isEmpty()) {
                this.itemInternals.add(new ItemInternal(22, string));
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    MessagesController.CommunityPeerDialog communityPeerDialog = arrayList.get(i3);
                    TLRPC.Dialog dialog = communityPeerDialog.dialog;
                    if (dialog != null) {
                        this.itemInternals.add(new ItemInternal(0, dialog));
                    } else {
                        TLRPC.Chat chat = communityPeerDialog.chat;
                        if (chat != null) {
                            this.itemInternals.add(new ItemInternal(23, chat));
                        } else {
                            TLRPC.User user = communityPeerDialog.user;
                            if (user != null) {
                                this.itemInternals.add(new ItemInternal(23, user));
                            }
                        }
                    }
                }
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:171:0x0395  */
    /* JADX WARN: Code duplicated, block: B:174:0x039c  */
    /* JADX WARN: Code duplicated, block: B:179:0x03bb  */
    /* JADX WARN: Code duplicated, block: B:194:0x0405  */
    /* JADX WARN: Code duplicated, block: B:196:0x0409  */
    /* JADX WARN: Code duplicated, block: B:198:0x0411  */
    /* JADX WARN: Code duplicated, block: B:199:0x041c  */
    /* JADX WARN: Code duplicated, block: B:200:0x042a  */
    /* JADX WARN: Code duplicated, block: B:202:0x042e A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:211:0x0458 A[LOOP:2: B:211:0x0458->B:220:0x047f, LOOP_START, PHI: r6
  0x0458: PHI (r6v2 int) = (r6v1 int), (r6v4 int) binds: [B:210:0x0456, B:220:0x047f] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:213:0x0460  */
    /* JADX WARN: Code duplicated, block: B:221:0x0481 A[ORIG_RETURN, RETURN] */
    private void updateItemList() {
        ArrayList<TLRPC.Dialog> dialogsArray;
        DialogsActivity dialogsActivity;
        long j;
        ArrayList<ItemInternal> arrayList;
        int i;
        ArrayList<ItemInternal> arrayList2;
        ArrayList<ItemInternal> arrayList3;
        boolean z;
        TLRPC.RequestPeerType requestPeerType;
        ItemInternal itemInternal;
        TLRPC.Dialog dialog;
        int i2;
        int i3;
        TLRPC.RequestPeerType requestPeerType2;
        ArrayList<ItemInternal> arrayList4;
        int i4;
        DialogsActivity dialogsActivity2;
        int i5;
        ArrayList<TLRPC.TL_contact> arrayList5;
        long j2 = 0;
        if (this.communityId != 0) {
            updateItemListForCommunity();
            return;
        }
        this.itemInternals.clear();
        updateHasHints();
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        long j3 = this.communityId;
        if (j3 != 0) {
            dialogsArray = messagesController.getDialogsByCommunity(j3);
        } else {
            dialogsArray = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen);
            if (dialogsArray == null) {
                dialogsArray = new ArrayList<>();
            }
        }
        int size = dialogsArray.size();
        this.dialogsCount = size;
        int i6 = 0;
        this.isEmpty = false;
        if (size == 0 && this.parentFragment.isArchive()) {
            this.itemInternals.add(new ItemInternal(19));
            return;
        }
        TLRPC.Dialog tL_dialog = null;
        if (!this.hasHints && this.dialogsType == 0 && (i5 = this.folderId) == 0 && messagesController.isDialogsEndReached(i5) && !this.forceUpdatingContacts) {
            if (messagesController.getAllFoldersDialogsCount() <= 10 && ContactsController.getInstance(this.currentAccount).doneLoadingContacts && !ContactsController.getInstance(this.currentAccount).contacts.isEmpty()) {
                this.onlineContacts = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                long j4 = UserConfig.getInstance(this.currentAccount).clientUserId;
                int size2 = this.onlineContacts.size();
                int i7 = 0;
                while (true) {
                    arrayList5 = this.onlineContacts;
                    if (i7 >= size2) {
                        break;
                    }
                    long j5 = arrayList5.get(i7).user_id;
                    if (j5 == j4 || messagesController.dialogs_dict.get(j5) != null) {
                        this.onlineContacts.remove(i7);
                        i7--;
                        size2--;
                    }
                    i7++;
                }
                if (arrayList5.isEmpty()) {
                    this.onlineContacts = null;
                } else {
                    sortOnlineContacts(false);
                }
            } else {
                this.onlineContacts = null;
            }
        }
        MessagesController.DialogFilter currentFilter = getCurrentFilter();
        if ((currentFilter == null || currentFilter.isDefault()) && (dialogsActivity = this.parentFragment) != null && dialogsActivity.isReplyTo && dialogsActivity.replyMessageAuthor != 0) {
            this.itemInternals.add(new ItemInternal(20));
            int i8 = 0;
            while (true) {
                if (i8 >= dialogsArray.size()) {
                    j = j2;
                    break;
                }
                j = j2;
                if (dialogsArray.get(i8).id == this.parentFragment.replyMessageAuthor) {
                    tL_dialog = dialogsArray.get(i8);
                    break;
                } else {
                    i8++;
                    j2 = j;
                }
            }
            if (tL_dialog == null) {
                tL_dialog = new TLRPC.TL_dialog();
                tL_dialog.id = this.parentFragment.replyMessageAuthor;
            }
            this.itemInternals.add(new ItemInternal(0, tL_dialog));
            this.itemInternals.add(new ItemInternal(20));
        } else {
            j = 0;
            if ((currentFilter == null || currentFilter.isDefault()) && (dialogsActivity2 = this.parentFragment) != null && this.dialogsType == 3 && dialogsActivity2.forwardOriginalChannel != 0) {
                this.itemInternals.add(new ItemInternal(20));
                for (int i9 = 0; i9 < dialogsArray.size(); i9++) {
                    if (dialogsArray.get(i9).id == this.parentFragment.forwardOriginalChannel) {
                        tL_dialog = dialogsArray.get(i9);
                        break;
                    }
                }
                if (tL_dialog == null) {
                    tL_dialog = new TLRPC.TL_dialog();
                    tL_dialog.id = this.parentFragment.forwardOriginalChannel;
                }
                this.itemInternals.add(new ItemInternal(0, tL_dialog));
                this.itemInternals.add(new ItemInternal(20));
            }
        }
        this.hasChatlistHint = false;
        int i10 = this.dialogsType;
        if ((i10 == 7 || i10 == 8) && currentFilter != null && currentFilter.isChatlist()) {
            messagesController.checkChatlistFolderUpdate(currentFilter.id, false);
            TL_chatlists.TL_chatlists_chatlistUpdates chatlistFolderUpdates = messagesController.getChatlistFolderUpdates(currentFilter.id);
            if (chatlistFolderUpdates != null && chatlistFolderUpdates.missing_peers.size() > 0) {
                this.hasChatlistHint = true;
                this.itemInternals.add(new ItemInternal(chatlistFolderUpdates));
            }
        }
        if (this.requestPeerType != null) {
            this.itemInternals.add(new ItemInternal(15));
        }
        if (this.collapsedView || this.isTransitionSupport) {
            for (int i11 = 0; i11 < dialogsArray.size(); i11++) {
                if (this.dialogsType == 2 && (dialogsArray.get(i11) instanceof DialogsActivity.DialogsHeader)) {
                    this.itemInternals.add(new ItemInternal(14, dialogsArray.get(i11)));
                } else {
                    this.itemInternals.add(new ItemInternal(0, dialogsArray.get(i11)));
                }
            }
            this.itemInternals.add(new ItemInternal(10));
            return;
        }
        if (this.dialogsCount == 0 && this.forceUpdatingContacts) {
            this.isEmpty = true;
            TLRPC.RequestPeerType requestPeerType3 = this.requestPeerType;
            ArrayList<ItemInternal> arrayList6 = this.itemInternals;
            if (requestPeerType3 != null) {
                arrayList6.add(new ItemInternal(16));
            } else {
                arrayList6.add(new ItemInternal(5, dialogsEmptyType()));
            }
            this.itemInternals.add(new ItemInternal(8));
            this.itemInternals.add(new ItemInternal(7));
            this.itemInternals.add(new ItemInternal(13));
        } else {
            ArrayList<TLRPC.TL_contact> arrayList7 = this.onlineContacts;
            if (arrayList7 != null && !arrayList7.isEmpty() && (i = this.dialogsType) != 7 && i != 8) {
                if (this.dialogsCount == 0) {
                    this.isEmpty = true;
                    TLRPC.RequestPeerType requestPeerType4 = this.requestPeerType;
                    ArrayList<ItemInternal> arrayList8 = this.itemInternals;
                    if (requestPeerType4 != null) {
                        arrayList8.add(new ItemInternal(16));
                    } else {
                        arrayList8.add(new ItemInternal(5, dialogsEmptyType()));
                    }
                    this.itemInternals.add(new ItemInternal(8));
                    this.itemInternals.add(new ItemInternal(7));
                } else {
                    int i12 = 0;
                    while (true) {
                        int size3 = dialogsArray.size();
                        arrayList2 = this.itemInternals;
                        if (i12 >= size3) {
                            break;
                        }
                        arrayList2.add(new ItemInternal(0, dialogsArray.get(i12)));
                        i12++;
                    }
                    arrayList2.add(new ItemInternal(8));
                    this.itemInternals.add(new ItemInternal(7));
                }
                int i13 = 0;
                while (true) {
                    int size4 = this.onlineContacts.size();
                    arrayList3 = this.itemInternals;
                    if (i13 >= size4) {
                        break;
                    }
                    arrayList3.add(new ItemInternal(6, this.onlineContacts.get(i13)));
                    i13++;
                }
                arrayList3.add(new ItemInternal(10));
                z = true;
            } else if (this.hasHints) {
                int size5 = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
                this.itemInternals.add(new ItemInternal(2));
                int i14 = 0;
                while (true) {
                    arrayList = this.itemInternals;
                    if (i14 >= size5) {
                        break;
                    }
                    arrayList.add(new ItemInternal(4, MessagesController.getInstance(this.currentAccount).hintDialogs.get(i14)));
                    i14++;
                }
                arrayList.add(new ItemInternal(3));
            } else {
                int i15 = this.dialogsType;
                if (i15 == 11 || i15 == 13) {
                    this.itemInternals.add(new ItemInternal(7));
                    this.itemInternals.add(new ItemInternal(12));
                } else if (i15 == 12) {
                    this.itemInternals.add(new ItemInternal(7));
                }
            }
            requestPeerType = this.requestPeerType;
            if ((!(requestPeerType instanceof TLRPC.TL_requestPeerTypeBroadcast) || (requestPeerType instanceof TLRPC.TL_requestPeerTypeChat)) && this.dialogsCount > 0) {
                this.itemInternals.add(new ItemInternal(12));
            }
            if (this.allowForwardAsStories && this.dialogsType == 3) {
                this.itemInternals.add(new ItemInternal(21));
            }
            if (!z) {
                for (i2 = 0; i2 < dialogsArray.size(); i2++) {
                    if (this.dialogsType != 2 && (dialogsArray.get(i2) instanceof DialogsActivity.DialogsHeader)) {
                        this.itemInternals.add(new ItemInternal(14, dialogsArray.get(i2)));
                    } else {
                        this.itemInternals.add(new ItemInternal(0, dialogsArray.get(i2)));
                    }
                }
                if (this.communityId != j && !this.forceShowEmptyCell && (i4 = this.dialogsType) != 7 && i4 != 8 && !MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
                    if (this.dialogsCount != 0) {
                        this.itemInternals.add(new ItemInternal(1));
                    }
                    this.itemInternals.add(new ItemInternal(10));
                } else {
                    i3 = this.dialogsCount;
                    if (i3 == 0) {
                        this.isEmpty = true;
                        requestPeerType2 = this.requestPeerType;
                        arrayList4 = this.itemInternals;
                        if (requestPeerType2 != null) {
                            arrayList4.add(new ItemInternal(16));
                        } else {
                            arrayList4.add(new ItemInternal(5, dialogsEmptyType()));
                        }
                    } else {
                        if (this.folderId == 0 && i3 > 10 && this.dialogsType == 0 && !ExteraConfig.getHideFloatingButton()) {
                            this.itemInternals.add(new ItemInternal(11));
                        }
                        this.itemInternals.add(new ItemInternal(10));
                    }
                }
            }
            if (messagesController.hiddenUndoChats.isEmpty()) {
            }
            while (i6 < this.itemInternals.size()) {
                itemInternal = this.itemInternals.get(i6);
                if (itemInternal.viewType != 0 && (dialog = itemInternal.dialog) != null && messagesController.isHiddenByUndo(dialog.id)) {
                    this.itemInternals.remove(i6);
                    i6--;
                }
                i6++;
            }
        }
        z = false;
        requestPeerType = this.requestPeerType;
        if (!(requestPeerType instanceof TLRPC.TL_requestPeerTypeBroadcast)) {
            this.itemInternals.add(new ItemInternal(12));
        } else {
            this.itemInternals.add(new ItemInternal(12));
        }
        if (this.allowForwardAsStories) {
            this.itemInternals.add(new ItemInternal(21));
        }
        if (!z) {
            while (i2 < dialogsArray.size()) {
                if (this.dialogsType != 2) {
                    this.itemInternals.add(new ItemInternal(0, dialogsArray.get(i2)));
                } else {
                    this.itemInternals.add(new ItemInternal(0, dialogsArray.get(i2)));
                }
            }
            if (this.communityId != j) {
                i3 = this.dialogsCount;
                if (i3 == 0) {
                    this.isEmpty = true;
                    requestPeerType2 = this.requestPeerType;
                    arrayList4 = this.itemInternals;
                    if (requestPeerType2 != null) {
                        arrayList4.add(new ItemInternal(16));
                    } else {
                        arrayList4.add(new ItemInternal(5, dialogsEmptyType()));
                    }
                } else {
                    if (this.folderId == 0) {
                        this.itemInternals.add(new ItemInternal(11));
                    }
                    this.itemInternals.add(new ItemInternal(10));
                }
            } else {
                i3 = this.dialogsCount;
                if (i3 == 0) {
                    this.isEmpty = true;
                    requestPeerType2 = this.requestPeerType;
                    arrayList4 = this.itemInternals;
                    if (requestPeerType2 != null) {
                        arrayList4.add(new ItemInternal(16));
                    } else {
                        arrayList4.add(new ItemInternal(5, dialogsEmptyType()));
                    }
                } else {
                    if (this.folderId == 0) {
                        this.itemInternals.add(new ItemInternal(11));
                    }
                    this.itemInternals.add(new ItemInternal(10));
                }
            }
        }
        if (messagesController.hiddenUndoChats.isEmpty()) {
            while (i6 < this.itemInternals.size()) {
                itemInternal = this.itemInternals.get(i6);
                if (itemInternal.viewType != 0) {
                }
                i6++;
            }
        }
    }

    public int getItemHeight(int i) {
        int iDp;
        if (this.itemInternals.get(i).viewType != 0) {
            return 0;
        }
        if (this.itemInternals.get(i).isForumCell && !this.collapsedView) {
            iDp = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 86.0f : 91.0f);
        } else {
            iDp = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 76.0f : 70.0f);
        }
        return iDp + 1;
    }
}
