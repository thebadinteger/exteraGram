package org.telegram.ui.Stories.recorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.google.android.exoplayer2.util.Consumer;
import com.google.android.gms.cast.MediaError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.SlideIntChooseView;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CreateRtmpStreamBottomSheet;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Stories.DarkThemeResourceProvider;
import org.telegram.ui.Stories.StoriesController;

public class StoryPrivacyBottomSheet extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private int activePage;
    private boolean allowComments;
    private boolean allowCover;
    private boolean allowScreenshots;
    private boolean allowSmallChats;
    private boolean applyWhenDismiss;
    private final Paint backgroundPaint;
    private boolean canChangePeer;
    private int commentsPrice;
    private Drawable coverDrawable;
    private final ArrayList<Long> excludedContacts;
    private final ArrayList<Long> excludedEveryone;
    private final HashMap<Long, ArrayList<Long>> excludedEveryoneByGroup;
    private int excludedEveryoneCount;
    private boolean isEdit;
    private boolean isLive;
    private boolean isRtmpStream;
    private boolean keepOnMyPage;
    private boolean liveSettings;
    private boolean loadedContacts;
    private final ArrayList<Long> messageUsers;
    private Utilities.Callback<StoryPrivacy> onDismiss;
    private DoneCallback onDone;
    private Utilities.Callback<ArrayList<Long>> onDone2;
    private Utilities.Callback<HashSet<Integer>> onSelectedAlbums;
    private Utilities.Callback<TLRPC.InputPeer> onSelectedPeer;
    private final HashSet<Integer> selectedAlbums;
    private final ArrayList<Long> selectedContacts;
    private final HashMap<Long, ArrayList<Long>> selectedContactsByGroup;
    private int selectedContactsCount;
    public TLRPC.InputPeer selectedPeer;
    private int selectedType;
    private boolean sendAsMessageEnabled;
    private int shiftDp;
    private HashMap<Long, Integer> smallChatsParticipantsCount;
    private boolean startedFromSendAsMessage;
    private int storiesCount;
    private int storyPeriod;
    private ViewPagerFixed viewPager;
    private ArrayList<String> warnUsers;
    private Runnable whenCoverClicked;

    public interface DoneCallback {
        void done(StoryPrivacy storyPrivacy, boolean z, boolean z2, boolean z3, boolean z4, TLRPC.InputPeer inputPeer, int i, Runnable runnable, Runnable runnable2);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean canSwipeToBack(MotionEvent motionEvent) {
        return false;
    }

    public HashSet<Long> mergeUsers(ArrayList<Long> arrayList, HashMap<Long, ArrayList<Long>> map) {
        HashSet<Long> hashSet = new HashSet<>();
        if (arrayList != null) {
            hashSet.addAll(arrayList);
        }
        if (map != null) {
            Iterator<ArrayList<Long>> it = map.values().iterator();
            while (it.hasNext()) {
                hashSet.addAll(it.next());
            }
        }
        return hashSet;
    }

    public class Page extends FrameLayout implements View.OnClickListener, NotificationCenter.NotificationCenterDelegate {
        private Adapter adapter;
        private final ArrayList<TLObject> atTop;
        private final ButtonWithCounterView button;
        private final ButtonWithCounterView button2;
        private final ButtonContainer buttonContainer;
        private final LongSparseArray<Boolean> changelog;
        private boolean containsHeader;
        private final FrameLayout contentView;
        private HeaderCell headerView;
        private boolean isActionBar;
        private final ArrayList<ItemInner> items;
        private int keyboardHeight;
        private boolean keyboardMoving;
        private int lastSelectedType;
        private LinearLayoutManager layoutManager;
        private RecyclerListView listView;
        private final ArrayList<ItemInner> oldItems;
        public int pageType;
        private AlertDialog progressDialog;
        private String query;
        private boolean scrolling;
        private SearchUsersCell searchField;
        private ValueAnimator searchFieldAnimator;
        private int searchPosition;
        private boolean searchTranslationAnimating;
        private float searchTranslationAnimatingTo;
        private GraySectionCell sectionCell;
        private final ArrayList<Long> selectedUsers;
        private final HashMap<Long, ArrayList<Long>> selectedUsersByGroup;
        private final View underKeyboardView;
        private long waitingForChatId;
        public boolean wasAtBottom;
        public boolean wasAtTop;
        private boolean wasKeyboardVisible;

        public Page(final Context context) {
            super(context);
            this.changelog = new LongSparseArray<>();
            this.selectedUsers = new ArrayList<>();
            this.selectedUsersByGroup = new HashMap<>();
            this.searchPosition = -1;
            this.atTop = new ArrayList<>();
            this.oldItems = new ArrayList<>();
            this.items = new ArrayList<>();
            this.lastSelectedType = -1;
            this.sectionCell = new GraySectionCell(context, ((BottomSheet) StoryPrivacyBottomSheet.this).resourcesProvider);
            SearchUsersCell searchUsersCell = new SearchUsersCell(context, ((BottomSheet) StoryPrivacyBottomSheet.this).resourcesProvider, new Runnable() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$0();
                }
            }) { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet.Page.1
                @Override // org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet.SearchUsersCell
                public void setContainerHeight(float f) {
                    super.setContainerHeight(f);
                    Page.this.sectionCell.setTranslationY(((getY() - (Page.this.contentView == null ? 0 : Page.this.contentView.getPaddingTop())) + Math.min(AndroidUtilities.dp(150.0f), this.containerHeight)) - 1.0f);
                    if (Page.this.contentView != null) {
                        Page.this.contentView.invalidate();
                    }
                }

                @Override // android.view.View
                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    Page.this.sectionCell.setTranslationY(((getY() - (Page.this.contentView == null ? 0 : Page.this.contentView.getPaddingTop())) + Math.min(AndroidUtilities.dp(150.0f), this.containerHeight)) - 1.0f);
                    if (Page.this.contentView != null) {
                        Page.this.contentView.invalidate();
                    }
                }
            };
            this.searchField = searchUsersCell;
            int i = Theme.key_dialogBackground;
            searchUsersCell.setBackgroundColor(StoryPrivacyBottomSheet.this.getThemedColor(i));
            this.searchField.setOnSearchTextChange(new Utilities.Callback() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda4
                @Override 
                public final void run(Object obj) {
                    this.f$0.onSearch((String) obj);
                }
            });
            HeaderCell headerCell = new HeaderCell(context, ((BottomSheet) StoryPrivacyBottomSheet.this).resourcesProvider);
            this.headerView = headerCell;
            headerCell.setOnCloseClickListener(new Runnable() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$1();
                }
            });
            FrameLayout frameLayout = new FrameLayout(context);
            this.contentView = frameLayout;
            frameLayout.setPadding(0, AndroidUtilities.statusBarHeight + AndroidUtilities.dp(56.0f), 0, 0);
            frameLayout.setClipToPadding(true);
            addView(frameLayout, LayoutHelper.createFrame(-1, -1, 119));
            RecyclerListView recyclerListView = new RecyclerListView(context, ((BottomSheet) StoryPrivacyBottomSheet.this).resourcesProvider);
            this.listView = recyclerListView;
            recyclerListView.setClipToPadding(false);
            this.listView.setTranslateSelector(true);
            RecyclerListView recyclerListView2 = this.listView;
            Adapter adapter = new Adapter(context, ((BottomSheet) StoryPrivacyBottomSheet.this).resourcesProvider, this.searchField, new Runnable() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    storyPrivacyBottomSheet.lambda$openCrafting$8();
                }
            });
            this.adapter = adapter;
            recyclerListView2.setAdapter(adapter);
            this.adapter.listView = this.listView;
            RecyclerListView recyclerListView3 = this.listView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            this.layoutManager = linearLayoutManager;
            recyclerListView3.setLayoutManager(linearLayoutManager);
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet.Page.2
                private boolean canScrollDown;

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                    boolean zCanScrollVertically = Page.this.listView.canScrollVertically(1);
                    if (zCanScrollVertically != this.canScrollDown) {
                        Page.this.buttonContainer.invalidate();
                        this.canScrollDown = zCanScrollVertically;
                    }
                    Page.this.contentView.invalidate();
                    ((BottomSheet) StoryPrivacyBottomSheet.this).containerView.invalidate();
                    Page page = Page.this;
                    if (page.pageType != 6 || page.listView.getChildCount() <= 0 || Page.this.listView.getChildAdapterPosition(Page.this.listView.getChildAt(0)) < MessagesController.getInstance(((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount).getStoriesController().blocklist.size()) {
                        return;
                    }
                    MessagesController.getInstance(((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount).getStoriesController().loadBlocklist(false);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int i2) {
                    if (i2 == 1 && ((BottomSheet) StoryPrivacyBottomSheet.this).keyboardVisible && Page.this.searchField != null) {
                        StoryPrivacyBottomSheet.this.closeKeyboard();
                    }
                    if (i2 == 0) {
                        Page page = Page.this;
                        page.wasAtTop = page.atTop();
                        Page page2 = Page.this;
                        page2.wasAtBottom = page2.atBottom();
                    }
                    Page.this.scrolling = i2 != 0;
                }
            });
            this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda7
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
                public final void onItemClick(View view, int i2, float f, float f2) {
                    this.f$0.lambda$new$14(context, view, i2, f, f2);
                }
            });
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet.Page.3
                @Override // androidx.recyclerview.widget.SimpleItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
                public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                    return true;
                }

                @Override // androidx.recyclerview.widget.DefaultItemAnimator
                public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                    ((BottomSheet) StoryPrivacyBottomSheet.this).containerView.invalidate();
                    Page.this.contentView.invalidate();
                    Page.this.listView.invalidate();
                }

                @Override // androidx.recyclerview.widget.DefaultItemAnimator
                public void onChangeAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                    ((BottomSheet) StoryPrivacyBottomSheet.this).containerView.invalidate();
                    Page.this.contentView.invalidate();
                }

                @Override // androidx.recyclerview.widget.DefaultItemAnimator
                public void onAddAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                    ((BottomSheet) StoryPrivacyBottomSheet.this).containerView.invalidate();
                    Page.this.contentView.invalidate();
                }

                @Override // androidx.recyclerview.widget.DefaultItemAnimator
                public void onRemoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                    ((BottomSheet) StoryPrivacyBottomSheet.this).containerView.invalidate();
                    Page.this.contentView.invalidate();
                }
            };
            defaultItemAnimator.setDurations(350L);
            defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setSupportsChangeAnimations(false);
            this.listView.setItemAnimator(defaultItemAnimator);
            frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -2, 55));
            frameLayout.addView(this.sectionCell, LayoutHelper.createFrame(-1, 32, 55));
            addView(this.headerView, LayoutHelper.createFrame(-1, -2, 55));
            ButtonContainer buttonContainer = new ButtonContainer(context);
            this.buttonContainer = buttonContainer;
            buttonContainer.setClickable(true);
            buttonContainer.setOrientation(1);
            buttonContainer.setPadding(AndroidUtilities.dp(10.0f) + ((BottomSheet) StoryPrivacyBottomSheet.this).backgroundPaddingLeft, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f) + ((BottomSheet) StoryPrivacyBottomSheet.this).backgroundPaddingLeft, AndroidUtilities.dp(10.0f));
            buttonContainer.setBackgroundColor(Theme.getColor(i, ((BottomSheet) StoryPrivacyBottomSheet.this).resourcesProvider));
            ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, ((BottomSheet) StoryPrivacyBottomSheet.this).resourcesProvider);
            this.button = buttonWithCounterView;
            buttonWithCounterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.onButton1Click(view);
                }
            });
            buttonWithCounterView.setRound();
            buttonContainer.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 87));
            ButtonWithCounterView buttonWithCounterView2 = new ButtonWithCounterView(context, false, ((BottomSheet) StoryPrivacyBottomSheet.this).resourcesProvider);
            this.button2 = buttonWithCounterView2;
            buttonWithCounterView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda9
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.onButton2Click(view);
                }
            });
            buttonWithCounterView2.setRound();
            buttonContainer.addView(buttonWithCounterView2, LayoutHelper.createLinear(-1, 48, 87, 0, 8, 0, 0));
            View view = new View(context);
            this.underKeyboardView = view;
            view.setBackgroundColor(Theme.getColor(i, ((BottomSheet) StoryPrivacyBottomSheet.this).resourcesProvider));
            addView(view, LayoutHelper.createFrame(-1, 500.0f, 87, 0.0f, 0.0f, 0.0f, -500.0f));
            addView(buttonContainer, LayoutHelper.createFrame(-1, -2, 87));
        }

        public void lambda$new$2(TLRPC.InputPeer inputPeer) {
            StoryPrivacyBottomSheet storyPrivacyBottomSheet = StoryPrivacyBottomSheet.this;
            storyPrivacyBottomSheet.selectedPeer = inputPeer;
            storyPrivacyBottomSheet.selectedAlbums.clear();
            if (StoryPrivacyBottomSheet.this.isLive && StoryPrivacyBottomSheet.this.isRtmpStream) {
                StoryPrivacyBottomSheet.this.isRtmpStream = false;
            }
            if (StoryPrivacyBottomSheet.this.onSelectedPeer != null) {
                StoryPrivacyBottomSheet.this.onSelectedPeer.run(StoryPrivacyBottomSheet.this.selectedPeer);
            }
            if (StoryPrivacyBottomSheet.this.onSelectedAlbums != null) {
                StoryPrivacyBottomSheet.this.onSelectedAlbums.run(new HashSet(StoryPrivacyBottomSheet.this.selectedAlbums));
            }
            updateItems(true);
        }

        public void lambda$new$5(long j, TLRPC.ChatFull chatFull) {
            selectChat(j, chatFull.participants);
        }

        public void lambda$new$8(long j, String str) {
            StoryPrivacyBottomSheet.this.getStoriesController().createAlbum(j, str, new Utilities.Callback() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda27
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$new$7((StoriesController.StoryAlbum) obj);
                }
            });
        }

        public void lambda$new$11(CreateRtmpStreamBottomSheet[] createRtmpStreamBottomSheetArr, Browser.Progress progress) {
            StoryPrivacyBottomSheet.this.isRtmpStream = true;
            createRtmpStreamBottomSheetArr[0].lambda$new$0();
            updateItems(true);
        }

        public class ButtonContainer extends LinearLayout {
            final AnimatedFloat alpha;
            private ValueAnimator animator;
            final Paint dividerPaint;
            private ValueAnimator hideAnimator;
            private float translationY;
            private float translationY2;

            public ButtonContainer(Context context) {
                super(context);
                this.dividerPaint = new Paint(1);
                this.alpha = new AnimatedFloat(this);
            }

            public void hide(final boolean z, boolean z2) {
                ValueAnimator valueAnimator = this.hideAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                if (z2) {
                    setVisibility(0);
                    ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.translationY2, z ? getMeasuredHeight() : 0.0f);
                    this.hideAnimator = valueAnimatorOfFloat;
                    valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$ButtonContainer$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            this.f$0.lambda$hide$0(valueAnimator2);
                        }
                    });
                    this.hideAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet.Page.ButtonContainer.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (z) {
                                ButtonContainer.this.setVisibility(8);
                            }
                            ButtonContainer.this.hideAnimator = null;
                        }
                    });
                    this.hideAnimator.setDuration(320L);
                    this.hideAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.hideAnimator.start();
                    return;
                }
                setVisibility(z ? 8 : 0);
                float measuredHeight = z ? getMeasuredHeight() : 0.0f;
                this.translationY2 = measuredHeight;
                super.setTranslationY(measuredHeight + this.translationY);
            }

            public void lambda$selectChat$15(long j, ArrayList arrayList, AlertDialog alertDialog, int i) {
            this.selectedUsersByGroup.put(Long.valueOf(j), arrayList);
            int size = arrayList.size();
            int i2 = 0;
            while (i2 < size) {
                Object obj = arrayList.get(i2);
                i2++;
                this.changelog.put(((Long) obj).longValue(), Boolean.TRUE);
            }
            updateSpans(true);
            updateButton(true);
            updateCheckboxes(true);
            alertDialog.dismiss();
            this.searchField.scrollToBottom();
        }

        private void updateSpans(boolean z) {
            Object chat;
            HashSet<Long> hashSetMergeUsers = StoryPrivacyBottomSheet.this.mergeUsers(this.selectedUsers, this.selectedUsersByGroup);
            int i = this.pageType;
            if (i == 3) {
                StoryPrivacyBottomSheet.this.selectedContactsCount = hashSetMergeUsers.size();
            } else if (i == 4) {
                StoryPrivacyBottomSheet.this.excludedEveryoneCount = hashSetMergeUsers.size();
            }
            MessagesController messagesController = MessagesController.getInstance(((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount);
            ArrayList<GroupCreateSpan> arrayList = new ArrayList<>();
            ArrayList<GroupCreateSpan> arrayList2 = new ArrayList<>();
            for (int i2 = 0; i2 < this.searchField.allSpans.size(); i2++) {
                GroupCreateSpan groupCreateSpan = this.searchField.allSpans.get(i2);
                if (!hashSetMergeUsers.contains(Long.valueOf(groupCreateSpan.getUid()))) {
                    arrayList.add(groupCreateSpan);
                }
            }
            for (Long l : hashSetMergeUsers) {
                long jLongValue = l.longValue();
                int i3 = 0;
                while (true) {
                    if (i3 >= this.searchField.allSpans.size()) {
                        if (jLongValue >= 0) {
                            chat = messagesController.getUser(l);
                        } else {
                            chat = messagesController.getChat(l);
                        }
                        Object obj = chat;
                        if (obj != null) {
                            GroupCreateSpan groupCreateSpan2 = new GroupCreateSpan(getContext(), obj, null, true, ((BottomSheet) StoryPrivacyBottomSheet.this).resourcesProvider);
                            groupCreateSpan2.setOnClickListener(this);
                            arrayList2.add(groupCreateSpan2);
                            break;
                        }
                        break;
                    }
                    if (this.searchField.allSpans.get(i3).getUid() == jLongValue) {
                        break;
                    } else {
                        i3++;
                    }
                }
            }
            if (arrayList.isEmpty() && arrayList2.isEmpty()) {
                return;
            }
            this.searchField.spansContainer.updateSpans(arrayList, arrayList2, z);
        }

        public void onButton1Click(View view) {
            StoryPrivacy storyPrivacy;
            if (this.button.isLoading()) {
                return;
            }
            final MessagesController messagesController = MessagesController.getInstance(((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount);
            int i = this.pageType;
            if (i == 5) {
                if (StoryPrivacyBottomSheet.this.onDone2 != null) {
                    StoryPrivacyBottomSheet.this.onDone2.run(this.selectedUsers);
                }
                StoryPrivacyBottomSheet.this.lambda$new$0();
                return;
            }
            if (i == 1) {
                TLRPC.TL_editCloseFriends tL_editCloseFriends = new TLRPC.TL_editCloseFriends();
                tL_editCloseFriends.id.addAll(this.selectedUsers);
                this.button.setLoading(true);
                ConnectionsManager.getInstance(((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount).sendRequest(tL_editCloseFriends, new RequestDelegate() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda10
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$onButton1Click$17(messagesController, tLObject, tL_error);
                    }
                });
                return;
            }
            if (i == 0) {
                boolean z = StoryPrivacyBottomSheet.this.applyWhenDismiss;
                StoryPrivacyBottomSheet storyPrivacyBottomSheet = StoryPrivacyBottomSheet.this;
                if (!z) {
                    int i2 = storyPrivacyBottomSheet.selectedType;
                    StoryPrivacyBottomSheet storyPrivacyBottomSheet2 = StoryPrivacyBottomSheet.this;
                    if (i2 == 3) {
                        storyPrivacy = new StoryPrivacy(StoryPrivacyBottomSheet.this.selectedType, ((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount, (ArrayList<Long>) new ArrayList(storyPrivacyBottomSheet2.mergeUsers(storyPrivacyBottomSheet2.selectedContacts, StoryPrivacyBottomSheet.this.selectedContactsByGroup)));
                        storyPrivacy.selectedUserIds.clear();
                        storyPrivacy.selectedUserIds.addAll(StoryPrivacyBottomSheet.this.selectedContacts);
                        storyPrivacy.selectedUserIdsByGroup.clear();
                        storyPrivacy.selectedUserIdsByGroup.putAll(StoryPrivacyBottomSheet.this.selectedContactsByGroup);
                    } else {
                        int i3 = storyPrivacyBottomSheet2.selectedType;
                        StoryPrivacyBottomSheet storyPrivacyBottomSheet3 = StoryPrivacyBottomSheet.this;
                        if (i3 == 2) {
                            storyPrivacy = new StoryPrivacy(storyPrivacyBottomSheet3.selectedType, ((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount, (ArrayList<Long>) StoryPrivacyBottomSheet.this.excludedContacts);
                        } else {
                            int i4 = storyPrivacyBottomSheet3.selectedType;
                            StoryPrivacyBottomSheet storyPrivacyBottomSheet4 = StoryPrivacyBottomSheet.this;
                            if (i4 == 4) {
                                storyPrivacy = new StoryPrivacy(StoryPrivacyBottomSheet.this.selectedType, ((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount, (ArrayList<Long>) new ArrayList(storyPrivacyBottomSheet4.mergeUsers(storyPrivacyBottomSheet4.excludedEveryone, StoryPrivacyBottomSheet.this.excludedEveryoneByGroup)));
                                storyPrivacy.selectedUserIds.clear();
                                storyPrivacy.selectedUserIds.addAll(StoryPrivacyBottomSheet.this.excludedEveryone);
                                storyPrivacy.selectedUserIdsByGroup.clear();
                                storyPrivacy.selectedUserIdsByGroup.putAll(StoryPrivacyBottomSheet.this.excludedEveryoneByGroup);
                            } else {
                                storyPrivacy = new StoryPrivacy(storyPrivacyBottomSheet4.selectedType, ((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount, (ArrayList<Long>) null);
                            }
                        }
                    }
                    StoryPrivacyBottomSheet storyPrivacyBottomSheet5 = StoryPrivacyBottomSheet.this;
                    storyPrivacyBottomSheet5.done(storyPrivacy, new StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda11(storyPrivacyBottomSheet5));
                    return;
                }
                storyPrivacyBottomSheet.lambda$new$0();
                return;
            }
            if (i == 2) {
                boolean z2 = StoryPrivacyBottomSheet.this.isEdit;
                StoryPrivacyBottomSheet storyPrivacyBottomSheet6 = StoryPrivacyBottomSheet.this;
                if (z2) {
                    storyPrivacyBottomSheet6.closeKeyboard();
                    StoryPrivacyBottomSheet storyPrivacyBottomSheet7 = StoryPrivacyBottomSheet.this;
                    storyPrivacyBottomSheet7.done(new StoryPrivacy(2, ((BottomSheet) storyPrivacyBottomSheet7).currentAccount, this.selectedUsers), new StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda11(StoryPrivacyBottomSheet.this));
                    return;
                } else {
                    storyPrivacyBottomSheet6.closeKeyboard();
                    StoryPrivacyBottomSheet.this.viewPager.scrollToPosition(0);
                    return;
                }
            }
            if (i == 3) {
                boolean z3 = StoryPrivacyBottomSheet.this.isEdit;
                StoryPrivacyBottomSheet storyPrivacyBottomSheet8 = StoryPrivacyBottomSheet.this;
                if (z3) {
                    HashSet hashSetMergeUsers = storyPrivacyBottomSheet8.mergeUsers(this.selectedUsers, this.selectedUsersByGroup);
                    if (hashSetMergeUsers.isEmpty()) {
                        return;
                    }
                    StoryPrivacyBottomSheet.this.closeKeyboard();
                    StoryPrivacy storyPrivacy2 = new StoryPrivacy(3, ((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount, (ArrayList<Long>) new ArrayList(hashSetMergeUsers));
                    storyPrivacy2.selectedUserIds.clear();
                    storyPrivacy2.selectedUserIds.addAll(this.selectedUsers);
                    storyPrivacy2.selectedUserIdsByGroup.clear();
                    storyPrivacy2.selectedUserIdsByGroup.putAll(this.selectedUsersByGroup);
                    StoryPrivacyBottomSheet.this.done(storyPrivacy2, new Runnable() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda12
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onButton1Click$18();
                        }
                    });
                    return;
                }
                if (storyPrivacyBottomSheet8.mergeUsers(this.selectedUsers, this.selectedUsersByGroup).isEmpty()) {
                    return;
                }
                StoryPrivacyBottomSheet.this.selectedType = 3;
                StoryPrivacyBottomSheet.this.closeKeyboard();
                StoryPrivacyBottomSheet.this.viewPager.scrollToPosition(0);
                return;
            }
            StoryPrivacyBottomSheet storyPrivacyBottomSheet9 = StoryPrivacyBottomSheet.this;
            if (i == 6) {
                HashSet<Long> hashSetMergeUsers2 = storyPrivacyBottomSheet9.mergeUsers(this.selectedUsers, this.selectedUsersByGroup);
                this.button.setLoading(true);
                MessagesController.getInstance(((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount).getStoriesController().updateBlockedUsers(hashSetMergeUsers2, new Runnable() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda13
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onButton1Click$19();
                    }
                });
            } else {
                storyPrivacyBottomSheet9.selectedType = i;
                StoryPrivacyBottomSheet.this.closeKeyboard();
                StoryPrivacyBottomSheet.this.viewPager.scrollToPosition(0);
            }
        }

        public void lambda$onButton2Click$20(ArrayList arrayList) {
            StoryPrivacyBottomSheet storyPrivacyBottomSheet = StoryPrivacyBottomSheet.this;
            storyPrivacyBottomSheet.done(new StoryPrivacy(5, ((BottomSheet) storyPrivacyBottomSheet).currentAccount, (ArrayList<Long>) arrayList), new StoryPrivacyBottomSheet$Page$$ExternalSyntheticLambda11(StoryPrivacyBottomSheet.this));
        }

        public float top() {
            float paddingTop;
            float y;
            float fMin = (this.layoutManager.getReverseLayout() || this.pageType == 0) ? AndroidUtilities.displaySize.y : 0.0f;
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                View childAt = this.listView.getChildAt(i);
                if (this.layoutManager.getReverseLayout()) {
                    float paddingTop2 = this.contentView.getPaddingTop() + childAt.getY();
                    float alpha = childAt.getAlpha();
                    if (paddingTop2 < fMin) {
                        fMin = AndroidUtilities.lerp(fMin, paddingTop2, alpha);
                    }
                } else if (this.pageType == 0) {
                    if (!(childAt instanceof PadView)) {
                        fMin = Math.min(this.contentView.getPaddingTop() + childAt.getY(), fMin);
                    }
                } else {
                    if ((childAt.getTag() instanceof Integer) && ((Integer) childAt.getTag()).intValue() == 33) {
                        paddingTop = this.contentView.getPaddingTop() + childAt.getBottom();
                        y = childAt.getTranslationY();
                    } else if ((childAt.getTag() instanceof Integer) && ((Integer) childAt.getTag()).intValue() == 35) {
                        paddingTop = this.contentView.getPaddingTop();
                        y = childAt.getY();
                    }
                    return paddingTop + y;
                }
            }
            return fMin;
        }

        public void bind(int i) {
            this.pageType = i;
            this.changelog.clear();
            this.selectedUsers.clear();
            this.selectedUsersByGroup.clear();
            if (i == 4) {
                this.selectedUsers.addAll(StoryPrivacyBottomSheet.this.excludedEveryone);
                this.selectedUsersByGroup.putAll(StoryPrivacyBottomSheet.this.excludedEveryoneByGroup);
            } else if (i == 5) {
                this.selectedUsers.addAll(StoryPrivacyBottomSheet.this.messageUsers);
            } else if (i == 1) {
                ArrayList closeFriends = StoryPrivacyBottomSheet.this.getCloseFriends();
                for (int i2 = 0; i2 < closeFriends.size(); i2++) {
                    this.selectedUsers.add(Long.valueOf(((TLRPC.User) closeFriends.get(i2)).id));
                }
            } else if (i == 2) {
                this.selectedUsers.addAll(StoryPrivacyBottomSheet.this.excludedContacts);
            } else if (i == 3) {
                this.selectedUsers.addAll(StoryPrivacyBottomSheet.this.selectedContacts);
                this.selectedUsersByGroup.putAll(StoryPrivacyBottomSheet.this.selectedContactsByGroup);
            } else if (i == 6) {
                applyBlocklist(false);
            }
            LinearLayoutManager linearLayoutManager = this.layoutManager;
            this.adapter.reversedLayout = false;
            linearLayoutManager.setReverseLayout(false);
            updateSpans(false);
            this.searchField.setText(_UrlKt.FRAGMENT_ENCODE_SET);
            this.searchField.setVisibility(i == 0 ? 8 : 0);
            this.searchField.scrollToBottom();
            this.query = null;
            updateItems(false);
            updateButton(false);
            updateCheckboxes(false);
            scrollToTop();
            this.listView.requestLayout();
            this.lastSelectedType = -1;
        }

        public void applyBlocklist(boolean z) {
            if (this.pageType != 6) {
                return;
            }
            this.selectedUsers.clear();
            this.selectedUsers.addAll(MessagesController.getInstance(((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount).getStoriesController().blocklist);
            for (int i = 0; i < this.changelog.size(); i++) {
                long jKeyAt = this.changelog.keyAt(i);
                boolean zBooleanValue = this.changelog.valueAt(i).booleanValue();
                ArrayList<Long> arrayList = this.selectedUsers;
                if (zBooleanValue) {
                    if (!arrayList.contains(Long.valueOf(jKeyAt))) {
                        this.selectedUsers.add(Long.valueOf(jKeyAt));
                    }
                } else {
                    arrayList.remove(Long.valueOf(jKeyAt));
                }
            }
            if (z) {
                updateItems(true);
                updateButton(true);
                updateCheckboxes(true);
            }
        }

        public void updateItems(boolean z) {
            updateItems(z, true);
        }

        void lambda$onMeasure$24() {
            this.keyboardMoving = false;
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.searchField.allSpans.contains(view)) {
                GroupCreateSpan groupCreateSpan = (GroupCreateSpan) view;
                boolean zIsDeleting = groupCreateSpan.isDeleting();
                SearchUsersCell searchUsersCell = this.searchField;
                if (zIsDeleting) {
                    searchUsersCell.currentDeletingSpan = null;
                    this.searchField.spansContainer.removeSpan(groupCreateSpan);
                    long uid = groupCreateSpan.getUid();
                    Iterator<Map.Entry<Long, ArrayList<Long>>> it = this.selectedUsersByGroup.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Long, ArrayList<Long>> next = it.next();
                        if (next.getValue().contains(Long.valueOf(uid))) {
                            it.remove();
                            this.selectedUsers.addAll(next.getValue());
                            this.selectedUsers.remove(Long.valueOf(uid));
                        }
                    }
                    this.selectedUsers.remove(Long.valueOf(uid));
                    updateCheckboxes(true);
                    updateButton(true);
                    return;
                }
                if (searchUsersCell.currentDeletingSpan != null) {
                    this.searchField.currentDeletingSpan.cancelDeleteAnimation();
                    this.searchField.currentDeletingSpan = null;
                }
                this.searchField.currentDeletingSpan = groupCreateSpan;
                groupCreateSpan.startDeleteAnimation();
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            NotificationCenter.getInstance(((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(((BottomSheet) StoryPrivacyBottomSheet.this).currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        }

        public class PadView extends View {
            public PadView(Context context) {
                super(context);
            }
        }

        public class Adapter extends AdapterWithDiffUtils {
            private Context context;
            private RecyclerListView listView;
            private Runnable onBack;
            private Theme.ResourcesProvider resourcesProvider;
            public boolean reversedLayout;
            private SearchUsersCell searchField;

            public Adapter(Context context, Theme.ResourcesProvider resourcesProvider, SearchUsersCell searchUsersCell, Runnable runnable) {
                this.context = context;
                this.resourcesProvider = resourcesProvider;
                this.searchField = searchUsersCell;
                this.onBack = runnable;
            }

            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return (viewHolder.getItemViewType() == 3 && StoryPrivacyBottomSheet.this.canChangePeer) || viewHolder.getItemViewType() == 7 || viewHolder.getItemViewType() == 9 || viewHolder.getItemViewType() == 10;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                TextCell textCell;
                View slideIntChooseView;
                View headerCell2;
                if (i == -1) {
                    slideIntChooseView = Page.this.new PadView(this.context);
                } else if (i == 0) {
                    View view = new View(this.context);
                    view.setTag(35);
                    slideIntChooseView = view;
                } else if (i == 1) {
                    View view2 = new View(this.context);
                    view2.setTag(34);
                    slideIntChooseView = view2;
                } else if (i == 3) {
                    slideIntChooseView = new UserCell(this.context, this.resourcesProvider);
                } else {
                    if (i == 4) {
                        headerCell2 = new HeaderCell2(this.context, this.resourcesProvider, true);
                    } else if (i == 11) {
                        slideIntChooseView = new HeaderCell2(this.context, this.resourcesProvider, false);
                    } else if (i == 8) {
                        org.telegram.ui.Cells.HeaderCell headerCell = new org.telegram.ui.Cells.HeaderCell(this.context, this.resourcesProvider);
                        headerCell.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, this.resourcesProvider));
                        slideIntChooseView = headerCell;
                    } else if (i == 5) {
                        StickerEmptyView stickerEmptyView = new StickerEmptyView(this.context, null, 1, this.resourcesProvider);
                        stickerEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
                        stickerEmptyView.subtitle.setText(LocaleController.getString(R.string.SearchEmptyViewFilteredSubtitle2));
                        stickerEmptyView.linearLayout.setTranslationY(AndroidUtilities.dp(24.0f));
                        headerCell2 = stickerEmptyView;
                    } else if (i == 6) {
                        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.context, this.resourcesProvider);
                        textInfoPrivacyCell.setBackgroundColor(-15921907);
                        slideIntChooseView = textInfoPrivacyCell;
                    } else {
                        if (i == 7) {
                            textCell = new TextCell(this.context, 23, true, true, this.resourcesProvider);
                        } else if (i == 9) {
                            textCell = new TextCell(this.context, 23, true, false, this.resourcesProvider);
                        } else if (i == 10) {
                            slideIntChooseView = new SlideIntChooseView(this.context, this.resourcesProvider);
                        } else {
                            slideIntChooseView = new View(this.context) { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet.Page.Adapter.1
                                @Override // android.view.View
                                public void onMeasure(int i2, int i3) {
                                    super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), TLObject.FLAG_30));
                                }
                            };
                        }
                        slideIntChooseView = textCell;
                    }
                    slideIntChooseView = headerCell2;
                }
                return new RecyclerListView.Holder(slideIntChooseView);
            }

            CharSequence $r8$lambda$UIS_Jkk_zlzxU3kJNwsMLu39uAs(Integer num, Integer num2) {
                if (num.intValue() == 0) {
                    return num2.intValue() == 0 ? LocaleController.getString(R.string.LiveStoryPricePerCommentFree) : LocaleController.formatPluralStringComma("Stars", num2.intValue());
                }
                return _UrlKt.FRAGMENT_ENCODE_SET + num2;
            }

            public void lambda$new$1(MessagesStorage messagesStorage) {
        final HashMap<Long, Integer> smallGroupsParticipantsCount = messagesStorage.getSmallGroupsParticipantsCount();
        if (smallGroupsParticipantsCount == null || smallGroupsParticipantsCount.isEmpty()) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0(smallGroupsParticipantsCount);
            }
        });
    }

    public void lambda$done$2(StoryPrivacy storyPrivacy, Runnable runnable, AlertDialog alertDialog, int i) {
        done(storyPrivacy, runnable, true);
    }

    public static void lambda$new$0(View view) {
            Runnable runnable = this.onCloseClickListener;
            if (runnable != null) {
                runnable.run();
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.dividerPaint.setColor(Theme.getColor(Theme.key_divider, this.resourcesProvider));
            canvas.drawRect(0.0f, getHeight() - AndroidUtilities.getShadowHeight(), getWidth(), getHeight(), this.dividerPaint);
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }

        public void setCloseImageVisible(boolean z) {
            this.closeView.setVisibility(z ? 0 : 8);
            TextView textView = this.textView;
            boolean z2 = LocaleController.isRTL;
            textView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, 23, (z2 || !z) ? 22.0f : 53.0f, 0.0f, (z2 && z) ? 53.0f : 22.0f, 0.0f));
        }

        public void setOnCloseClickListener(Runnable runnable) {
            this.onCloseClickListener = runnable;
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), TLObject.FLAG_30));
        }
    }

    public static class SearchUsersCell extends ScrollView {
        public ArrayList<GroupCreateSpan> allSpans;
        private final LinearGradient bottomGradient;
        private final AnimatedFloat bottomGradientAlpha;
        private final Matrix bottomGradientMatrix;
        private final Paint bottomGradientPaint;
        public float containerHeight;
        private GroupCreateSpan currentDeletingSpan;
        private EditTextBoldCursor editText;
        private int fieldY;
        private int hintTextWidth;
        private boolean ignoreScrollEvent;
        private boolean ignoreTextChange;
        private Utilities.Callback<String> onSearchTextChange;
        private int prevResultContainerHeight;
        private final Theme.ResourcesProvider resourcesProvider;
        public int resultContainerHeight;
        private boolean scroll;
        public SpansContainer spansContainer;
        private final LinearGradient topGradient;
        private final AnimatedFloat topGradientAlpha;
        private final Matrix topGradientMatrix;
        private final Paint topGradientPaint;
        private Runnable updateHeight;

        public SearchUsersCell(Context context, Theme.ResourcesProvider resourcesProvider, Runnable runnable) {
            super(context);
            this.allSpans = new ArrayList<>();
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.topGradientAlpha = new AnimatedFloat(this, 0L, 300L, cubicBezierInterpolator);
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(8.0f), new int[]{-16777216, 0}, new float[]{0.0f, 1.0f}, tileMode);
            this.topGradient = linearGradient;
            Paint paint = new Paint(1);
            this.topGradientPaint = paint;
            this.topGradientMatrix = new Matrix();
            this.bottomGradientAlpha = new AnimatedFloat(this, 0L, 300L, cubicBezierInterpolator);
            LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(8.0f), new int[]{0, -16777216}, new float[]{0.0f, 1.0f}, tileMode);
            this.bottomGradient = linearGradient2;
            Paint paint2 = new Paint(1);
            this.bottomGradientPaint = paint2;
            this.bottomGradientMatrix = new Matrix();
            paint.setShader(linearGradient);
            PorterDuff.Mode mode = PorterDuff.Mode.DST_OUT;
            paint.setXfermode(new PorterDuffXfermode(mode));
            paint2.setShader(linearGradient2);
            paint2.setXfermode(new PorterDuffXfermode(mode));
            this.resourcesProvider = resourcesProvider;
            this.updateHeight = runnable;
            setVerticalScrollBarEnabled(false);
            AndroidUtilities.setScrollViewEdgeEffectColor(this, Theme.getColor(Theme.key_windowBackgroundWhite));
            SpansContainer spansContainer = new SpansContainer(context);
            this.spansContainer = spansContainer;
            addView(spansContainer, LayoutHelper.createFrame(-1, -2.0f));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet.SearchUsersCell.1
                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (SearchUsersCell.this.currentDeletingSpan != null) {
                        SearchUsersCell.this.currentDeletingSpan.cancelDeleteAnimation();
                        SearchUsersCell.this.currentDeletingSpan = null;
                    }
                    if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                        SearchUsersCell.this.fullScroll(130);
                        clearFocus();
                        requestFocus();
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.editText = editTextBoldCursor;
            if (Build.VERSION.SDK_INT >= 25) {
                editTextBoldCursor.setRevealOnFocusHint(false);
            }
            this.editText.setTextSize(1, 16.0f);
            this.editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText, resourcesProvider));
            this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            EditTextBoldCursor editTextBoldCursor2 = this.editText;
            int i = Theme.key_groupcreate_cursor;
            editTextBoldCursor2.setCursorColor(Theme.getColor(i, resourcesProvider));
            this.editText.setHandlesColor(Theme.getColor(i, resourcesProvider));
            this.editText.setCursorWidth(1.5f);
            EditTextBoldCursor editTextBoldCursor3 = this.editText;
            editTextBoldCursor3.setInputType(editTextBoldCursor3.getInputType() | 176);
            this.editText.setSingleLine(true);
            this.editText.setBackgroundDrawable(null);
            this.editText.setVerticalScrollBarEnabled(false);
            this.editText.setHorizontalScrollBarEnabled(false);
            this.editText.setTextIsSelectable(false);
            this.editText.setPadding(0, 0, 0, 0);
            this.editText.setImeOptions(268435462);
            this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.spansContainer.addView(this.editText);
            this.editText.setHintText(LocaleController.getString(R.string.Search));
            this.hintTextWidth = (int) this.editText.getPaint().measureText(LocaleController.getString(R.string.Search));
            this.editText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet.SearchUsersCell.2
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                    if (SearchUsersCell.this.ignoreTextChange || SearchUsersCell.this.onSearchTextChange == null || editable == null) {
                        return;
                    }
                    SearchUsersCell.this.onSearchTextChange.run(editable.toString());
                }
            });
        }

        @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            return super.dispatchKeyEvent(keyEvent);
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            int scrollY = getScrollY();
            float f = scrollY;
            canvas.saveLayerAlpha(0.0f, f, getWidth(), getHeight() + scrollY, 255, 31);
            super.dispatchDraw(canvas);
            canvas.save();
            float f2 = this.topGradientAlpha.set(canScrollVertically(-1));
            this.topGradientMatrix.reset();
            this.topGradientMatrix.postTranslate(0.0f, f);
            this.topGradient.setLocalMatrix(this.topGradientMatrix);
            this.topGradientPaint.setAlpha((int) (f2 * 255.0f));
            canvas.drawRect(0.0f, f, getWidth(), AndroidUtilities.dp(8.0f) + scrollY, this.topGradientPaint);
            float f3 = this.bottomGradientAlpha.set(canScrollVertically(1));
            this.bottomGradientMatrix.reset();
            this.bottomGradientMatrix.postTranslate(0.0f, (getHeight() + scrollY) - AndroidUtilities.dp(8.0f));
            this.bottomGradient.setLocalMatrix(this.bottomGradientMatrix);
            this.bottomGradientPaint.setAlpha((int) (f3 * 255.0f));
            canvas.drawRect(0.0f, (getHeight() + scrollY) - AndroidUtilities.dp(8.0f), getWidth(), scrollY + getHeight(), this.bottomGradientPaint);
            canvas.restore();
            canvas.restore();
        }

        public void setText(CharSequence charSequence) {
            this.ignoreTextChange = true;
            this.editText.setText(charSequence);
            this.ignoreTextChange = false;
        }

        public void setOnSearchTextChange(Utilities.Callback<String> callback) {
            this.onSearchTextChange = callback;
        }

        @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
        public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
            if (this.ignoreScrollEvent) {
                this.ignoreScrollEvent = false;
                return false;
            }
            rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
            rect.top += this.fieldY + AndroidUtilities.dp(20.0f);
            rect.bottom += this.fieldY + AndroidUtilities.dp(50.0f);
            return super.requestChildRectangleOnScreen(view, rect, z);
        }

        @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(150.0f), Integer.MIN_VALUE));
        }

        public void setContainerHeight(float f) {
            this.containerHeight = f;
            SpansContainer spansContainer = this.spansContainer;
            if (spansContainer != null) {
                spansContainer.requestLayout();
            }
        }

        public Animator getContainerHeightAnimator(float f) {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.containerHeight, f);
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$SearchUsersCell$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$getContainerHeightAnimator$0(valueAnimator);
                }
            });
            return valueAnimatorOfFloat;
        }

        public void lambda$new$1(final Utilities.Callback callback, Theme.ResourcesProvider resourcesProvider, int i, View view, int i2) {
            if (i2 <= 1) {
                return;
            }
            final TLRPC.InputPeer inputPeer = this.peers.get(i2 - 2);
            if (inputPeer.channel_id == 0 && inputPeer.chat_id == 0) {
                callback.run(inputPeer);
                lambda$new$0();
            } else {
                final AlertDialog alertDialog = new AlertDialog(getContext(), 3, resourcesProvider);
                alertDialog.showDelayed(200L);
                MessagesController.getInstance(i).getStoriesController().canSendStoryFor(DialogObject.getPeerDialogId(inputPeer), new Consumer() { // from class: org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet$ChoosePeerSheet$$ExternalSyntheticLambda1
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        StoryPrivacyBottomSheet.ChoosePeerSheet.m20327$r8$lambda$ICflmlKPAU9KoQoEI7u_xSwVWI(alertDialog, callback, inputPeer, (Boolean) obj);
                    }
                }, true, resourcesProvider);
                lambda$new$0();
            }
        }

        public static /* synthetic */ void m20327$r8$lambda$ICflmlKPAU9KoQoEI7u_xSwVWI(AlertDialog alertDialog, Utilities.Callback callback, TLRPC.InputPeer inputPeer, Boolean bool) {
            alertDialog.dismiss();
            if (!bool.booleanValue() || callback == null) {
                return;
            }
            callback.run(inputPeer);
        }

        @Override // android.app.Dialog, android.view.Window.Callback
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.storiesSendAsUpdate);
        }

        @Override // android.app.Dialog, android.view.Window.Callback
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesSendAsUpdate);
        }

        @Override 
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.storiesSendAsUpdate) {
                this.peers = MessagesController.getInstance(this.currentAccount).getStoriesController().sendAs;
                this.adapter.notifyDataSetChanged();
            }
        }

        public float top() {
            int childAdapterPosition;
            float measuredHeight = this.containerView.getMeasuredHeight();
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt != null && (childAdapterPosition = this.listView.getChildAdapterPosition(childAt)) != -1 && childAdapterPosition > 0) {
                    measuredHeight = Math.min(AndroidUtilities.lerp(measuredHeight, childAt.getY(), childAt.getAlpha()), measuredHeight);
                }
            }
            return measuredHeight;
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet
        public boolean canDismissWithSwipe() {
            return top() > ((float) ((int) (((float) AndroidUtilities.displaySize.y) * 0.5f)));
        }

        public class Adapter extends RecyclerListView.SelectionAdapter {
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i) {
                if (i == 0) {
                    return 0;
                }
                return i == 1 ? 1 : 2;
            }

            private Adapter() {
            }

            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 2;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view;
                int iDp;
                if (i == 0 || i == 1) {
                    view = new View(ChoosePeerSheet.this.getContext());
                    if (i == 0) {
                        iDp = (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(306.0f);
                    } else {
                        iDp = AndroidUtilities.dp(54.0f);
                    }
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, iDp));
                } else {
                    view = new UserCell(ChoosePeerSheet.this.getContext(), ((BottomSheet) ChoosePeerSheet.this).resourcesProvider);
                }
                return new RecyclerListView.Holder(view);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (viewHolder.getItemViewType() == 2) {
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    userCell.setIsSendAs(true, true);
                    TLRPC.InputPeer inputPeer = (TLRPC.InputPeer) ChoosePeerSheet.this.peers.get(i - 2);
                    if (inputPeer instanceof TLRPC.TL_inputPeerSelf) {
                        userCell.setUser(UserConfig.getInstance(ChoosePeerSheet.this.currentAccount).getCurrentUser());
                    } else if (inputPeer instanceof TLRPC.TL_inputPeerUser) {
                        userCell.setUser(MessagesController.getInstance(ChoosePeerSheet.this.currentAccount).getUser(Long.valueOf(inputPeer.user_id)));
                    } else if (inputPeer instanceof TLRPC.TL_inputPeerChat) {
                        userCell.setChat(MessagesController.getInstance(ChoosePeerSheet.this.currentAccount).getChat(Long.valueOf(inputPeer.chat_id)), 0);
                    } else if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
                        userCell.setChat(MessagesController.getInstance(ChoosePeerSheet.this.currentAccount).getChat(Long.valueOf(inputPeer.channel_id)), 0);
                    }
                    userCell.checkBox.setVisibility(8);
                    userCell.radioButton.setVisibility(0);
                    userCell.setChecked((ChoosePeerSheet.this.selectedPeer == null && i == 2) || did(ChoosePeerSheet.this.selectedPeer) == did(inputPeer), false);
                    userCell.setDivider(i != getItemCount() - 1);
                }
            }

            private long did(TLRPC.InputPeer inputPeer) {
                if (inputPeer instanceof TLRPC.TL_inputPeerSelf) {
                    return UserConfig.getInstance(ChoosePeerSheet.this.currentAccount).getClientUserId();
                }
                return DialogObject.getPeerDialogId(inputPeer);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return ChoosePeerSheet.this.peers.size() + 2;
            }
        }
    }

    public StoriesController getStoriesController() {
        return MessagesController.getInstance(this.currentAccount).getStoriesController();
    }
}
