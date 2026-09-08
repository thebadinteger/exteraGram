package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.mediarouter.media.PlatformMediaRouter1RouteProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.utils.system.VibratorUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import kotlin.CharCodeKt$$ExternalSyntheticBUOutline0;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.CompoundEmoji;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.utils.ViewOutlineProviderImpl;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetGroupInfoCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ListView.RecyclerListViewWithOverlayDraw;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import org.telegram.ui.Components.blur3.ViewGroupPartRenderer;
import org.telegram.ui.Components.blur3.capture.IBlur3Capture;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.emojiview.FoundEmojiPacksRecyclerView;
import org.telegram.ui.Components.emojiview.FoundStickerPackButton;
import org.telegram.ui.Components.emojiview.FoundStickerPackButtonContainer;
import org.telegram.ui.Components.emojiview.FoundStickerPackCell;
import org.telegram.ui.Components.emojiview.FoundStickerPacksHeaderCell;
import org.telegram.ui.Components.inset.InAppKeyboardInsetView;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.StickersActivity;

@SuppressLint({"ViewConstructor"})
public class EmojiView extends FrameLayout implements FactorAnimator.Target, NotificationCenter.NotificationCenterDelegate, InAppKeyboardInsetView {
    private ArrayList<Tab> allTabs;
    private boolean allowAnimatedEmoji;
    private boolean allowEmojisForNonPremium;
    private View animateExpandFromButton;
    private int animateExpandFromPosition;
    private long animateExpandStartTime;
    private int animateExpandToPosition;
    private LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
    private PorterDuffColorFilter animatedEmojiTextColorFilter;
    private final BoolAnimator animatorSearchEmojiPackSelected;
    private final BoolAnimator animatorSearchStickerPackSelected;
    private ImageView backspaceButton;
    private AnimatorSet backspaceButtonAnimation;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private final IBlur3Capture blurCaptureMethod;
    private final BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableFactory;
    private final BlurredBackgroundSourceColor blurredBackgroundSourceColor;
    private final BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode;
    private final RectF blurredRectF;
    private final ArrayList<RectF> blurredRectList;
    private int bottomInset;
    private FrameLayout bottomTabContainer;
    private View bottomTabContainerBackground;
    private final BoolAnimator bottomTabVisibility;
    private FrameLayout bulletinContainer;
    private FrameLayout bulletinContainer2;
    private Runnable checkExpandStickerTabsRunnable;
    private ChooseStickerActionTracker chooseStickerActionTracker;
    private EmojiColorPickerWindow colorPickerView;
    private ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate;
    public int currentAccount;
    private int currentBackgroundType;
    private long currentChatId;
    private int currentPage;
    private ArrayList<Tab> currentTabs;
    public boolean customOutline;
    private EmojiViewDelegate delegate;
    private boolean disableStickerEditor;
    private Paint dotPaint;
    private DragListener dragListener;
    private EmojiGridAdapter emojiAdapter;
    private FoundStickerPackButton emojiAddPackButton;
    private FoundStickerPackButtonContainer emojiAddPackButtonContainer;
    boolean emojiBanned;
    public int emojiCacheType;
    private FrameLayout emojiContainer;
    private EmojiGridView emojiGridView;
    private float emojiLastX;
    private float emojiLastY;
    private GridLayoutManager emojiLayoutManager;
    private Drawable emojiLockDrawable;
    private Paint emojiLockPaint;
    private boolean emojiPackAlertOpened;
    EmojiPagesAdapter emojiPagerAdapter;
    private RecyclerAnimationScrollHelper emojiScrollHelper;
    private EmojiSearchAdapter emojiSearchAdapter;
    private SearchField emojiSearchField;
    private FoundStickerPacksHeaderCell emojiSearchHeader;
    private int emojiSize;
    private boolean emojiSmoothScrolling;
    private AnimatorSet emojiTabShadowAnimator;
    private EmojiTabsStrip emojiTabs;
    private View emojiTabsShadow;
    private String[] emojiTitles;
    private ImageViewEmoji emojiTouchedView;
    private float emojiTouchedX;
    private float emojiTouchedY;
    private ArrayList<EmojiPack> emojipacksProcessed;
    private boolean expandStickersByDragg;
    private ArrayList<Long> expandedEmojiSets;
    private final GradientDrawable fadeDrawable;
    private int favTabNum;
    private ArrayList<TLRPC.Document> favouriteStickers;
    private ArrayList<TLRPC.StickerSetCovered> featuredEmojiSets;
    private ArrayList<TLRPC.StickerSetCovered> featuredStickerSets;
    private boolean firstEmojiAttach;
    private boolean firstGifAttach;
    private boolean firstStickersAttach;
    private boolean firstTabUpdate;
    public boolean fixBottomTabContainerTranslation;
    private boolean forseMultiwindowLayout;
    private BaseFragment fragment;
    private boolean frozen;
    ArrayList<TLRPC.TL_messages_stickerSet> frozenStickerSets;
    private GifAdapter gifAdapter;
    private final Map<String, TLRPC.messages_BotResults> gifCache;
    private FrameLayout gifContainer;
    private int gifFirstEmojiTabNum;
    private RecyclerListView gifGridView;
    private Drawable[] gifIcons;
    private GifLayoutManager gifLayoutManager;
    private RecyclerListView.OnItemClickListener gifOnItemClickListener;
    private int gifRecentTabNum;
    private GifAdapter gifSearchAdapter;
    private SearchField gifSearchField;
    private GifSearchPreloader gifSearchPreloader;
    private ScrollSlidingTabStrip gifTabs;
    private int gifTrendingTabNum;
    private boolean glassDesign;
    private int groupStickerPackNum;
    private int groupStickerPackPosition;
    private TLRPC.TL_messages_stickerSet groupStickerSet;
    private boolean groupStickersHidden;
    private boolean hasChatStickers;
    private int hasRecentEmoji;
    private Runnable hideStickersBan;
    private boolean ignorePagerScroll;
    private boolean ignoreStickersScroll;
    private TLRPC.ChatFull info;
    public ArrayList<Long> installedEmojiSets;
    private LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets;
    private boolean isLayout;
    public boolean isNewHeightControl;
    private ArrayList<Long> keepFeaturedDuplicate;
    private float lastBottomScrollDy;
    private int lastFadeColor;
    private int lastNotifyWidth;
    private ArrayList<String> lastRecentArray;
    private int lastRecentCount;
    private String[] lastSearchKeyboardLanguage;
    private float lastStickersX;
    private int[] location;
    private boolean mForceHideBackspaceButton;
    private boolean mForceHideSettingsButton;
    private TextView mediaBanTooltip;
    private final Paint navbarFillPaint;
    private boolean needEmojiSearch;
    private Object outlineProvider;
    private ViewPager pager;
    private boolean premiumBulletin;
    private ArrayList<TLRPC.Document> premiumStickers;
    private int premiumTabNum;
    private TLRPC.StickerSetCovered[] primaryInstallingStickerSets;
    private ArrayList<TLRPC.Document> recentGifs;
    private ArrayList<TLRPC.Document> recentStickers;
    private int recentTabNum;
    Rect rect;
    private LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets;
    private final Theme.ResourcesProvider resourcesProvider;
    private final DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private AnimatorSet searchAnimation;
    private ImageView searchButton;
    private int searchFieldHeight;
    private Drawable searchIconDotDrawable;
    private Drawable searchIconDrawable;
    private boolean shouldDrawBackground;
    public boolean shouldDrawStickerSettings;
    public boolean shouldLightenBackground;
    private boolean showGifs;
    private boolean showLocalPremiumEmojiHint;
    private AnimatorSet showStickersBanAnimator;
    private boolean showing;
    private long shownBottomTabAfterClick;
    private FoundStickerPackButton stickerAddPackButton;
    private FoundStickerPackButtonContainer stickerAddPackButtonContainer;
    private Drawable[] stickerIcons;
    private FoundStickerPacksHeaderCell stickerSearchHeader;
    private ArrayList<TLRPC.TL_messages_stickerSet> stickerSets;
    private ImageView stickerSettingsButton;
    boolean stickersBanned;
    private AnimatorSet stickersButtonAnimation;
    private FrameLayout stickersContainer;
    private boolean stickersContainerAttached;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private RecyclerAnimationScrollHelper stickersScrollHelper;
    private SearchField stickersSearchField;
    private StickersSearchGridAdapter stickersSearchGridAdapter;
    private ScrollSlidingTabStrip stickersTab;
    private FrameLayout stickersTabContainer;
    private int stickersTabOffset;
    private Drawable[] tabIcons;
    private final int[] tabsMinusDy;
    private ObjectAnimator[] tabsYAnimators;
    private HashMap<Long, Utilities.Callback<TLRPC.TL_messages_stickerSet>> toInstall;
    private TrendingAdapter trendingAdapter;
    private TrendingAdapter trendingEmojiAdapter;
    private int trendingTabNum;
    private PagerSlidingTabStrip typeTabs;
    private Runnable updateStickersLoadedDelayed;
    private float visibleInAppKeyboardHeight;

    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    public static class EmojiPack {
        public ArrayList<TLRPC.Document> documents = new ArrayList<>();
        public boolean expanded;
        public boolean featured;
        public boolean forGroup;
        public boolean free;
        public int index;
        public boolean installed;
        public TLRPC.InputStickerSet needLoadSet;
        public int resId;
        public TLRPC.StickerSet set;
        public Long thumbDocumentId;
    }

    public interface EmojiViewDelegate {
        default boolean canAddCaptionToGif(TLRPC.Document document) {
            return false;
        }

        default boolean canSchedule() {
            return false;
        }

        default long getDialogId() {
            return 0L;
        }

        default float getProgressToSearchOpened() {
            return 0.0f;
        }

        default int getThreadId() {
            return 0;
        }

        default void invalidateEnterView() {
        }

        default boolean isExpanded() {
            return false;
        }

        default boolean isInScheduleMode() {
            return false;
        }

        default boolean isSearchOpened() {
            return false;
        }

        default boolean isUserSelf() {
            return false;
        }

        default void onAnimatedEmojiUnlockClick() {
        }

        default boolean onBackspace() {
            return false;
        }

        default void onClearEmojiRecent() {
        }

        void onCustomEmojiSelected(long j, TLRPC.Document document, String str, boolean z);

        default void onEmojiSelected(String str) {
        }

        default void onEmojiSettingsClick(ArrayList<TLRPC.TL_messages_stickerSet> arrayList) {
        }

        default void onGifSelected(View view, Object obj, String str, Object obj2, boolean z, int i, int i2) {
        }

        default void onGifSelectedForAddCaption(View view, Object obj, String str, Object obj2, boolean z, int i, int i2) {
        }

        default void onSearchOpenClose(int i) {
        }

        default void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet, boolean z) {
        }

        default void onStickerSelected(View view, TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i, int i2) {
        }

        default void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered) {
        }

        default void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered) {
        }

        default void onStickersGroupClick(long j) {
        }

        default void onStickersSettingsClick() {
        }

        default void onTabOpened(int i) {
        }

        default void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout) {
        }
    }

    public interface SearchRunnable extends Runnable {
        boolean isCompleted();

        boolean isLoading();

        void loadNext();
    }

    public static void lambda$new$0(View view) {
            if (this.searchStateDrawable.getIconState() == 1) {
                this.searchEditText.setText(_UrlKt.FRAGMENT_ENCODE_SET);
                search(null, false);
                StickerCategoriesListView stickerCategoriesListView = this.categoriesListView;
                if (stickerCategoriesListView != null) {
                    stickerCategoriesListView.scrollToStart();
                    this.categoriesListView.selectCategory((StickerCategoriesListView.EmojiCategory) null);
                    this.categoriesListView.updateCategoriesShown(true, true);
                }
                toggleClear(false);
                EditTextBoldCursor editTextBoldCursor = this.searchEditText;
                if (editTextBoldCursor != null) {
                    editTextBoldCursor.clearAnimation();
                    this.searchEditText.animate().translationX(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                }
                showInputBoxGradient(false);
            }
        }

        public @Override // org.telegram.ui.Components.EmojiTabsStrip
            public boolean onTabClick(int i8) {
                Integer numValueOf;
                int iDp;
                if (EmojiView.this.emojiSmoothScrolling) {
                    return false;
                }
                if (EmojiView.this.emojiSearchAdapter != null) {
                    EmojiView.this.emojiSearchAdapter.search(null);
                }
                if (EmojiView.this.emojiSearchField != null && EmojiView.this.emojiSearchField.categoriesListView != null) {
                    EmojiView.this.emojiSearchField.categoriesListView.selectCategory((StickerCategoriesListView.EmojiCategory) null);
                }
                if (i8 == 0) {
                    numValueOf = Integer.valueOf(EmojiView.this.needEmojiSearch ? 1 : 0);
                } else {
                    i8--;
                    numValueOf = null;
                }
                if (numValueOf == null && i8 < EmojiData.dataColored.length && EmojiView.this.emojiAdapter.sectionToPosition.indexOfKey(i8) >= 0) {
                    numValueOf = Integer.valueOf(EmojiView.this.emojiAdapter.sectionToPosition.get(i8));
                }
                if (numValueOf == null) {
                    ArrayList<EmojiPack> emojipacks = EmojiView.this.getEmojipacks();
                    int length = i8 - EmojiData.dataColored.length;
                    if (emojipacks == null || length < 0 || length >= emojipacks.size()) {
                        iDp = 0;
                    } else {
                        int i9 = 0;
                        while (true) {
                            if (i9 >= EmojiView.this.emojipacksProcessed.size()) {
                                i9 = -1;
                                break;
                            }
                            if (((EmojiPack) EmojiView.this.emojipacksProcessed.get(i9)).set.id == emojipacks.get(length).set.id) {
                                break;
                            }
                            i9++;
                        }
                        numValueOf = Integer.valueOf(EmojiView.this.emojiAdapter.sectionToPosition.get(i9 + EmojiData.dataColored.length));
                        iDp = AndroidUtilities.dp(-9.0f);
                    }
                } else {
                    iDp = 0;
                }
                if (numValueOf == null) {
                    return true;
                }
                EmojiView.this.emojiGridView.stopScroll();
                EmojiView.this.updateEmojiTabsPosition(numValueOf.intValue());
                EmojiView.this.scrollEmojisToPosition(numValueOf.intValue(), iDp);
                EmojiView.this.checkEmojiTabY(null, 0);
                return true;
            }

            @Override // org.telegram.ui.Components.EmojiTabsStrip
            public ColorFilter getEmojiColorFilter() {
                return EmojiView.this.animatedEmojiTextColorFilter;
            }
        };
        if (z4) {
            SearchField searchField = new SearchField(context, i7) { // from class: org.telegram.ui.Components.EmojiView.11
                @Override // android.view.View
                public void setTranslationY(float f) {
                    if (f != getTranslationY()) {
                        super.setTranslationY(f);
                        EmojiView.this.emojiContainer.invalidate();
                    }
                }
            };
            this.emojiSearchField = searchField;
            i = -1;
            this.emojiContainer.addView(searchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            this.emojiSearchField.searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.Components.EmojiView.12
                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View view, boolean z9) {
                    if (z9) {
                        EmojiView.this.lastSearchKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(EmojiView.this.lastSearchKeyboardLanguage);
                    }
                }
            });
            FoundStickerPacksHeaderCell foundStickerPacksHeaderCell = new FoundStickerPacksHeaderCell(context, resourcesProvider);
            this.emojiSearchHeader = foundStickerPacksHeaderCell;
            foundStickerPacksHeaderCell.setVisibility(8);
            this.emojiSearchHeader.setOnBackClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda23
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$6(view);
                }
            });
            this.emojiContainer.addView(this.emojiSearchHeader, new FrameLayout.LayoutParams(-1, this.searchFieldHeight));
        } else {
            i = -1;
        }
        int themedColor = getThemedColor(i6);
        if (Color.alpha(themedColor) >= 255) {
            this.emojiTabs.setBackgroundColor(themedColor);
        }
        this.emojiAdapter.processEmoji(true);
        this.emojiTabs.updateEmojiPacks(getEmojipacks());
        this.emojiContainer.addView(this.emojiTabs, LayoutHelper.createFrame(i, 36.0f));
        View view = new View(context);
        this.emojiTabsShadow = view;
        view.setAlpha(0.0f);
        this.emojiTabsShadow.setTag(1);
        View view2 = this.emojiTabsShadow;
        int i8 = Theme.key_chat_emojiPanelShadowLine;
        view2.setBackgroundColor(getThemedColor(i8));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(i, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(36.0f);
        this.emojiContainer.addView(this.emojiTabsShadow, layoutParams);
        this.emojiAddPackButton = new FoundStickerPackButton(context, resourcesProvider);
        FoundStickerPackButtonContainer foundStickerPackButtonContainer = new FoundStickerPackButtonContainer(context, resourcesProvider);
        this.emojiAddPackButtonContainer = foundStickerPackButtonContainer;
        foundStickerPackButtonContainer.setVisibility(8);
        this.emojiAddPackButtonContainer.addView(this.emojiAddPackButton, LayoutHelper.createFrame(-1, 48.0f, 80, 10.0f, 5.0f, 10.0f, 10.0f));
        this.emojiContainer.addView(this.emojiAddPackButtonContainer, LayoutHelper.createFrame(i, -2, 80));
        if (z2) {
            if (z3) {
                this.gifContainer = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiView.13
                    @Override // android.view.ViewGroup
                    public boolean drawChild(Canvas canvas, View view3, long j) {
                        if (view3 == EmojiView.this.gifGridView) {
                            canvas.save();
                            canvas.clipRect(0.0f, EmojiView.this.gifSearchField.getY() + EmojiView.this.gifSearchField.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                            boolean zDrawChild = super.drawChild(canvas, view3, j);
                            canvas.restore();
                            return zDrawChild;
                        }
                        return super.drawChild(canvas, view3, j);
                    }
                };
                emojiViewIA2 = null;
                Tab tab2 = new Tab();
                tab2.type = 1;
                tab2.view = this.gifContainer;
                this.allTabs.add(tab2);
                RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.EmojiView.14
                    private boolean ignoreLayout;
                    private boolean wasMeasured;

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.gifGridView, 0, EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
                    public void onMeasure(int i9, int i10) {
                        super.onMeasure(i9, i10);
                        if (this.wasMeasured) {
                            return;
                        }
                        EmojiView.this.gifAdapter.notifyDataSetChanged();
                        this.wasMeasured = true;
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
                    public void onLayout(boolean z9, int i9, int i10, int i11, int i12) {
                        if (EmojiView.this.firstGifAttach && EmojiView.this.gifAdapter.getItemCount() > 1) {
                            this.ignoreLayout = true;
                            EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(0, 0);
                            EmojiView.this.gifSearchField.setVisibility(0);
                            EmojiView.this.gifTabs.onPageScrolled(0, 0);
                            EmojiView.this.firstGifAttach = false;
                            this.ignoreLayout = false;
                        }
                        super.onLayout(z9, i9, i10, i11, i12);
                        EmojiView.this.checkGifSearchFieldScroll(true);
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
                    public void requestLayout() {
                        if (this.ignoreLayout) {
                            return;
                        }
                        super.requestLayout();
                    }
                };
                this.gifGridView = recyclerListView;
                recyclerListView.setClipToPadding(false);
                RecyclerListView recyclerListView2 = this.gifGridView;
                GifLayoutManager gifLayoutManager = new GifLayoutManager(context);
                this.gifLayoutManager = gifLayoutManager;
                recyclerListView2.setLayoutManager(gifLayoutManager);
                this.gifGridView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.EmojiView.15
                    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
                    public void getItemOffsets(Rect rect, View view3, RecyclerView recyclerView, RecyclerView.State state) {
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(view3);
                        if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter && childAdapterPosition == EmojiView.this.gifAdapter.trendingSectionItem) {
                            rect.set(0, 0, 0, 0);
                            return;
                        }
                        if (childAdapterPosition != 0 || !EmojiView.this.gifAdapter.addSearch) {
                            rect.left = 0;
                            rect.bottom = 0;
                            rect.top = AndroidUtilities.dp(2.0f);
                            rect.right = EmojiView.this.gifLayoutManager.isLastInRow(childAdapterPosition - (EmojiView.this.gifAdapter.addSearch ? 1 : 0)) ? 0 : AndroidUtilities.dp(2.0f);
                            return;
                        }
                        rect.set(0, 0, 0, 0);
                    }
                });
                this.gifGridView.setPadding(0, this.searchFieldHeight, 0, AndroidUtilities.dp(44.0f) + this.bottomInset);
                i2 = 2;
                this.gifGridView.setOverScrollMode(2);
                ((SimpleItemAnimator) this.gifGridView.getItemAnimator()).setSupportsChangeAnimations(false);
                RecyclerListView recyclerListView3 = this.gifGridView;
                GifAdapter gifAdapter = new GifAdapter(this, context, true);
                this.gifAdapter = gifAdapter;
                recyclerListView3.setAdapter(gifAdapter);
                this.gifSearchAdapter = new GifAdapter(this, context);
                this.gifGridView.setOnScrollListener(new TypedScrollListener(i2) { // from class: org.telegram.ui.Components.EmojiView.16
                    @Override // org.telegram.ui.Components.EmojiView.TypedScrollListener, androidx.recyclerview.widget.RecyclerView.OnScrollListener
                    public void onScrolled(RecyclerView recyclerView, int i9, int i10) {
                        super.onScrolled(recyclerView, i9, i10);
                        if (Build.VERSION.SDK_INT < 31 || EmojiView.this.scrollableViewNoiseSuppressor == null) {
                            return;
                        }
                        EmojiView.this.scrollableViewNoiseSuppressor.onScrolled(i9, i10);
                        EmojiView.this.invalidateBlurCaptures();
                    }
                });
                this.gifGridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda24
                    @Override // android.view.View.OnTouchListener
                    public final boolean onTouch(View view3, MotionEvent motionEvent) {
                        return this.f$0.lambda$new$7(resourcesProvider, view3, motionEvent);
                    }
                });
                RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda25
                    @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                    public final void onItemClick(View view3, int i9) {
                        this.f$0.lambda$new$8(view3, i9);
                    }
                };
                this.gifOnItemClickListener = onItemClickListener;
                this.gifGridView.setOnItemClickListener(onItemClickListener);
                this.gifContainer.addView(this.gifGridView, LayoutHelper.createFrame(i, -1.0f));
                SearchField searchField2 = new SearchField(context, i2) { // from class: org.telegram.ui.Components.EmojiView.17
                    @Override // android.view.View
                    public void setTranslationY(float f) {
                        if (getTranslationY() != f) {
                            super.setTranslationY(f);
                            EmojiView.this.gifContainer.invalidate();
                        }
                    }
                };
                this.gifSearchField = searchField2;
                this.gifContainer.addView(searchField2, new FrameLayout.LayoutParams(i, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
                DraggableScrollSlidingTabStrip draggableScrollSlidingTabStrip = new DraggableScrollSlidingTabStrip(context, resourcesProvider);
                this.gifTabs = draggableScrollSlidingTabStrip;
                draggableScrollSlidingTabStrip.setType(ScrollSlidingTabStrip.Type.TAB);
                this.gifTabs.setUnderlineHeight(AndroidUtilities.getShadowHeight());
                i3 = i5;
                this.gifTabs.setIndicatorColor(getThemedColor(i3));
                this.gifTabs.setUnderlineColor(getThemedColor(i8));
                this.gifTabs.setBackgroundColor(getThemedColor(i6));
                updateGifTabs();
                this.gifTabs.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda26
                    @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
                    public final void onPageSelected(int i9) {
                        this.f$0.lambda$new$9(i9);
                    }
                });
                this.gifAdapter.loadTrendingGifs();
            } else {
                i3 = i5;
                emojiViewIA2 = null;
                i2 = 2;
            }
            z8 = z5;
            this.stickersContainer = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiView.18
                @Override // android.view.ViewGroup, android.view.View
                public void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    EmojiView.this.stickersContainerAttached = true;
                    EmojiView.this.updateStickerTabsPosition();
                    if (EmojiView.this.chooseStickerActionTracker != null) {
                        EmojiView.this.chooseStickerActionTracker.checkVisibility();
                    }
                }

                @Override // android.view.ViewGroup, android.view.View
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    EmojiView.this.stickersContainerAttached = false;
                    EmojiView.this.updateStickerTabsPosition();
                    if (EmojiView.this.chooseStickerActionTracker != null) {
                        EmojiView.this.chooseStickerActionTracker.checkVisibility();
                    }
                }

                @Override // android.view.ViewGroup
                public boolean drawChild(Canvas canvas, View view3, long j) {
                    if (!z8 && (view3 == EmojiView.this.stickersGridView || view3 == EmojiView.this.stickersSearchField)) {
                        canvas.save();
                        float y = EmojiView.this.stickersTab.getY() + EmojiView.this.stickersTab.getMeasuredHeight() + 1.0f;
                        if (view3 == EmojiView.this.stickersGridView) {
                            y = Math.max(y, EmojiView.this.stickersSearchField.getY() + EmojiView.this.stickersSearchField.getMeasuredHeight() + 1.0f);
                        }
                        canvas.clipRect(0.0f, y - (AndroidUtilities.dp(16.0f) * EmojiView.this.animatorSearchStickerPackSelected.getFloatValue()), getMeasuredWidth(), getMeasuredHeight());
                        boolean zDrawChild = super.drawChild(canvas, view3, j);
                        canvas.restore();
                        return zDrawChild;
                    }
                    return super.drawChild(canvas, view3, j);
                }
            };
            MediaDataController.getInstance(this.currentAccount).checkStickers(0);
            MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
            RecyclerListViewWithOverlayDraw recyclerListViewWithOverlayDraw = new RecyclerListViewWithOverlayDraw(context) { // from class: org.telegram.ui.Components.EmojiView.19
                boolean ignoreLayout;

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (EmojiView.this.ignorePagerScroll) {
                        return false;
                    }
                    return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
                public void setVisibility(int i9) {
                    super.setVisibility(i9);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
                public void onLayout(boolean z9, int i9, int i10, int i11, int i12) {
                    if (EmojiView.this.firstStickersAttach && EmojiView.this.stickersGridAdapter.getItemCount() > 0) {
                        this.ignoreLayout = true;
                        EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
                        EmojiView.this.firstStickersAttach = false;
                        this.ignoreLayout = false;
                    }
                    super.onLayout(z9, i9, i10, i11, i12);
                    EmojiView.this.checkStickersSearchFieldScroll(true);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
                public void requestLayout() {
                    if (this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }

                @Override // androidx.recyclerview.widget.RecyclerView
                public void onScrolled(int i9, int i10) {
                    super.onScrolled(i9, i10);
                    if (Build.VERSION.SDK_INT >= 31 && EmojiView.this.scrollableViewNoiseSuppressor != null) {
                        EmojiView.this.scrollableViewNoiseSuppressor.onScrolled(i9, i10);
                        EmojiView.this.invalidateBlurCaptures();
                    }
                    if (EmojiView.this.stickersTabContainer != null) {
                        EmojiView.this.stickersTab.setUnderlineHeight(EmojiView.this.stickersGridView.canScrollVertically(-1) ? AndroidUtilities.getShadowHeight() : 0);
                    }
                    if (EmojiView.this.stickersSearchGridAdapter == null || getAdapter() != EmojiView.this.stickersSearchGridAdapter || EmojiView.this.stickersSearchGridAdapter.selectedPackId != 0 || EmojiView.this.stickersSearchGridAdapter.searchRunnable.isLoading() || EmojiView.this.stickersSearchGridAdapter.searchRunnable.isCompleted()) {
                        return;
                    }
                    if (EmojiView.this.stickersLayoutManager.findLastVisibleItemPosition() + 50 > EmojiView.this.stickersSearchGridAdapter.getItemCount()) {
                        SearchRunnable searchRunnable = EmojiView.this.stickersSearchGridAdapter.searchRunnable;
                        Objects.requireNonNull(searchRunnable);
                        AndroidUtilities.runOnUIThread(new EmojiView$19$$ExternalSyntheticLambda0(searchRunnable));
                    }
                }
            };
            this.stickersGridView = recyclerListViewWithOverlayDraw;
            GridLayoutManager gridLayoutManager2 = new GridLayoutManager(context, 5) { // from class: org.telegram.ui.Components.EmojiView.20
                @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i9) {
                    try {
                        LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2);
                        linearSmoothScrollerCustom.setTargetPosition(i9);
                        startSmoothScroll(linearSmoothScrollerCustom);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public int scrollVerticallyBy(int i9, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    int iScrollVerticallyBy = super.scrollVerticallyBy(i9, recycler, state);
                    if (iScrollVerticallyBy != 0 && EmojiView.this.stickersGridView.getScrollState() == 1) {
                        EmojiView.this.expandStickersByDragg = false;
                        EmojiView.this.updateStickerTabsPosition();
                    }
                    if (EmojiView.this.chooseStickerActionTracker == null) {
                        EmojiView.this.createStickersChooseActionTracker();
                    }
                    EmojiView.this.chooseStickerActionTracker.doSomeAction();
                    return iScrollVerticallyBy;
                }
            };
            this.stickersLayoutManager = gridLayoutManager2;
            recyclerListViewWithOverlayDraw.setLayoutManager(gridLayoutManager2);
            this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiView.21
                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int i9) {
                    if (EmojiView.this.stickersGridView.getAdapter() == EmojiView.this.stickersGridAdapter) {
                        EmojiView emojiView = EmojiView.this;
                        if (i9 == 0) {
                            return emojiView.stickersGridAdapter.stickersPerRow;
                        }
                        if (i9 == emojiView.stickersGridAdapter.totalItems || !(EmojiView.this.stickersGridAdapter.cache.get(i9) == null || (EmojiView.this.stickersGridAdapter.cache.get(i9) instanceof TLRPC.Document))) {
                            return EmojiView.this.stickersGridAdapter.stickersPerRow;
                        }
                        return 1;
                    }
                    if (i9 == EmojiView.this.stickersSearchGridAdapter.totalItems || !(EmojiView.this.stickersSearchGridAdapter.cache.get(i9) == null || (EmojiView.this.stickersSearchGridAdapter.cache.get(i9) instanceof TLRPC.Document))) {
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    }
                    return 1;
                }
            });
            this.stickersGridView.setPadding(0, AndroidUtilities.dp(36.0f), 0, AndroidUtilities.dp(44.0f));
            this.stickersGridView.setClipToPadding(false);
            Tab tab3 = new Tab();
            tab3.type = i2;
            tab3.view = this.stickersContainer;
            this.allTabs.add(tab3);
            this.stickersSearchGridAdapter = new StickersSearchGridAdapter(context);
            RecyclerListView recyclerListView4 = this.stickersGridView;
            StickersGridAdapter stickersGridAdapter = new StickersGridAdapter(context);
            this.stickersGridAdapter = stickersGridAdapter;
            recyclerListView4.setAdapter(stickersGridAdapter);
            this.stickersGridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda2
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view3, MotionEvent motionEvent) {
                    return this.f$0.lambda$new$10(resourcesProvider, view3, motionEvent);
                }
            });
            RecyclerListView.OnItemClickListener onItemClickListener2 = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda3
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view3, int i9) {
                    this.f$0.lambda$new$11(view3, i9);
                }
            };
            this.stickersOnItemClickListener = onItemClickListener2;
            this.stickersGridView.setOnItemClickListener(onItemClickListener2);
            this.stickersGridView.setGlowColor(getThemedColor(i6));
            this.stickersContainer.addView(this.stickersGridView);
            this.stickersScrollHelper = new RecyclerAnimationScrollHelper(this.stickersGridView, this.stickersLayoutManager);
            SearchField searchField3 = new SearchField(context, 0) { // from class: org.telegram.ui.Components.EmojiView.22
                @Override // android.view.View
                public void setTranslationY(float f) {
                    if (f != getTranslationY()) {
                        super.setTranslationY(f);
                        EmojiView.this.stickersContainer.invalidate();
                    }
                }
            };
            this.stickersSearchField = searchField3;
            this.stickersContainer.addView(searchField3, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            FoundStickerPacksHeaderCell foundStickerPacksHeaderCell2 = new FoundStickerPacksHeaderCell(context, resourcesProvider);
            this.stickerSearchHeader = foundStickerPacksHeaderCell2;
            foundStickerPacksHeaderCell2.setVisibility(8);
            this.stickerSearchHeader.setOnBackClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    this.f$0.lambda$new$12(view3);
                }
            });
            this.stickersContainer.addView(this.stickerSearchHeader, new FrameLayout.LayoutParams(-1, this.searchFieldHeight));
            emojiViewIA = null;
            AnonymousClass23 anonymousClass23 = new AnonymousClass23(context, resourcesProvider, baseFragment, z8);
            this.stickersTab = anonymousClass23;
            anonymousClass23.setDragEnabled(true);
            this.stickersTab.setWillNotDraw(false);
            this.stickersTab.setType(ScrollSlidingTabStrip.Type.TAB);
            this.stickersTab.setUnderlineHeight(this.stickersGridView.canScrollVertically(-1) ? AndroidUtilities.getShadowHeight() : 0);
            this.stickersTab.setIndicatorColor(getThemedColor(i3));
            this.stickersTab.setUnderlineColor(getThemedColor(i8));
            if (viewGroup != null && z8) {
                FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiView.24
                    Paint paint = new Paint();

                    @Override // android.view.ViewGroup, android.view.View
                    public void dispatchDraw(Canvas canvas) {
                        float fDp = AndroidUtilities.dp(50.0f) * EmojiView.this.delegate.getProgressToSearchOpened();
                        if (fDp > getMeasuredHeight()) {
                            return;
                        }
                        canvas.save();
                        if (fDp != 0.0f) {
                            canvas.clipRect(0.0f, fDp, getMeasuredWidth(), getMeasuredHeight());
                        }
                        this.paint.setColor(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelBackground));
                        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(36.0f) + EmojiView.this.stickersTab.getExpandedOffset(), this.paint);
                        super.dispatchDraw(canvas);
                        EmojiView.this.stickersTab.drawOverlays(canvas);
                        canvas.restore();
                    }

                    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                    public void onLayout(boolean z9, int i9, int i10, int i11, int i12) {
                        super.onLayout(z9, i9, i10, i11, i12);
                        EmojiView.this.updateStickerTabsPosition();
                    }
                };
                this.stickersTabContainer = frameLayout;
                frameLayout.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
                viewGroup.addView(this.stickersTabContainer, LayoutHelper.createFrame(-1, -2.0f));
            } else {
                this.stickersContainer.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
            }
            updateStickerTabs(true);
            this.stickersTab.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda5
                @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
                public final void onPageSelected(int i9) {
                    this.f$0.lambda$new$13(i9);
                }
            });
            this.stickersGridView.setOnScrollListener(new TypedScrollListener(0));
            this.stickerAddPackButton = new FoundStickerPackButton(context, resourcesProvider);
            FoundStickerPackButtonContainer foundStickerPackButtonContainer2 = new FoundStickerPackButtonContainer(context, resourcesProvider);
            this.stickerAddPackButtonContainer = foundStickerPackButtonContainer2;
            foundStickerPackButtonContainer2.setVisibility(8);
            this.stickerAddPackButtonContainer.addView(this.stickerAddPackButton, LayoutHelper.createFrame(-1, 48.0f, 80, 10.0f, 5.0f, 10.0f, 10.0f));
            this.stickersContainer.addView(this.stickerAddPackButtonContainer, LayoutHelper.createFrame(-1, -2, 80));
        } else {
            z8 = z5;
            emojiViewIA = null;
            i2 = 2;
        }
        this.currentTabs.clear();
        this.currentTabs.addAll(this.allTabs);
        ViewPager viewPager = new ViewPager(context) { // from class: org.telegram.ui.Components.EmojiView.25
            @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (EmojiView.this.ignorePagerScroll) {
                    return false;
                }
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                }
                try {
                    return super.onInterceptTouchEvent(motionEvent);
                } catch (IllegalArgumentException unused) {
                    return false;
                }
            }

            @Override // androidx.viewpager.widget.ViewPager
            public void setCurrentItem(int i9, boolean z9) {
                EmojiView.this.startStopVisibleGifs(i9 == 1);
                if (i9 != getCurrentItem()) {
                    super.setCurrentItem(i9, z9);
                    return;
                }
                if (i9 == 0) {
                    EmojiView.this.tabsMinusDy[1] = 0;
                    ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(EmojiView.this.emojiTabs, (Property<EmojiTabsStrip, Float>) ViewGroup.TRANSLATION_Y, 0.0f);
                    objectAnimatorOfFloat.setDuration(150L);
                    objectAnimatorOfFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    objectAnimatorOfFloat.start();
                    EmojiView.this.scrollEmojisToPosition(1, 0);
                    if (EmojiView.this.emojiTabs != null) {
                        EmojiView.this.emojiTabs.select(0);
                        return;
                    }
                    return;
                }
                EmojiView emojiView = EmojiView.this;
                if (i9 == 1) {
                    emojiView.gifGridView.smoothScrollToPosition(0);
                } else {
                    emojiView.stickersGridView.smoothScrollToPosition(1);
                }
            }
        };
        this.pager = viewPager;
        EmojiPagesAdapter emojiPagesAdapter = new EmojiPagesAdapter();
        this.emojiPagerAdapter = emojiPagesAdapter;
        viewPager.setAdapter(emojiPagesAdapter);
        ImageView imageView = new ImageView(context) { // from class: org.telegram.ui.Components.EmojiView.26
            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    EmojiView.this.backspacePressed = true;
                    EmojiView.this.backspaceOnce = false;
                    EmojiView.this.postBackspaceRunnable(350);
                } else if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                    EmojiView.this.backspacePressed = false;
                    if (!EmojiView.this.backspaceOnce && EmojiView.this.delegate != null && EmojiView.this.delegate.onBackspace()) {
                        try {
                            EmojiView.this.backspaceButton.performHapticFeedback(3);
                        } catch (Exception unused) {
                        }
                    }
                }
                super.onTouchEvent(motionEvent);
                return true;
            }
        };
        this.backspaceButton = imageView;
        imageView.setHapticFeedbackEnabled(true);
        this.backspaceButton.setImageResource(R.drawable.smiles_tab_clear);
        ImageView imageView2 = this.backspaceButton;
        int glassIconColor = z7 ? getGlassIconColor(0.6f) : getThemedColor(Theme.key_chat_emojiPanelBackspace);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        imageView2.setColorFilter(new PorterDuffColorFilter(glassIconColor, mode));
        ImageView imageView3 = this.backspaceButton;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView3.setScaleType(scaleType);
        this.backspaceButton.setContentDescription(LocaleController.getString(R.string.AccDescrBackspace));
        this.backspaceButton.setFocusable(true);
        this.backspaceButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                EmojiView.$r8$lambda$QgvpfMFqNjYqAUCQmi9fZc2cUmM(view3);
            }
        });
        ScaleStateListAnimator.apply(this.backspaceButton);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.bulletinContainer = frameLayout2;
        if (z4) {
            addView(frameLayout2, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, (AndroidUtilities.getShadowHeight() / AndroidUtilities.density) + 40.0f));
        } else {
            addView(frameLayout2, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.bulletinContainer2 = frameLayout3;
        addView(frameLayout3, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, 64.0f));
        this.bottomTabContainer = new FrameLayout(context);
        View view3 = new View(context);
        this.bottomTabContainerBackground = view3;
        int i9 = i2;
        this.bottomTabContainer.addView(view3, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(40.0f), 83));
        View view4 = this.bottomTabContainer;
        if (z4) {
            addView(view4, LayoutHelper.createFrame(-1, 48, 80));
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame(48, 48.0f, 85, 2.0f, 0.0f, 2.0f, 0.0f));
            if (z2) {
                ImageView imageView4 = new ImageView(context);
                this.stickerSettingsButton = imageView4;
                imageView4.setImageResource(R.drawable.smiles_tab_settings);
                this.stickerSettingsButton.setColorFilter(new PorterDuffColorFilter(z7 ? getGlassIconColor(0.6f) : getThemedColor(Theme.key_chat_emojiPanelBackspace), mode));
                this.stickerSettingsButton.setScaleType(scaleType);
                this.stickerSettingsButton.setFocusable(true);
                this.stickerSettingsButton.setContentDescription(LocaleController.getString(R.string.Settings));
                ScaleStateListAnimator.apply(this.stickerSettingsButton);
                this.bottomTabContainer.addView(this.stickerSettingsButton, LayoutHelper.createFrame(48, 48.0f, 85, 2.0f, 0.0f, 2.0f, 0.0f));
                this.stickerSettingsButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda7
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view5) {
                        this.f$0.lambda$new$15(view5);
                    }
                });
            }
            PagerSlidingTabStrip pagerSlidingTabStrip = new PagerSlidingTabStrip(context, resourcesProvider);
            this.typeTabs = pagerSlidingTabStrip;
            pagerSlidingTabStrip.setViewPager(this.pager);
            this.typeTabs.setShouldExpand(false);
            this.typeTabs.setIndicatorHeight(AndroidUtilities.dp(3.0f));
            this.typeTabs.setIndicatorColor(ColorUtils.setAlphaComponent(getThemedColor(Theme.key_chat_emojiPanelIconSelected), 20));
            this.typeTabs.setUnderlineHeight(0);
            this.typeTabs.setTabPaddingLeftRight(AndroidUtilities.dp(11.0f));
            this.typeTabs.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(11.0f));
            this.bottomTabContainer.addView(this.typeTabs, LayoutHelper.createFrame(-2, 48, 81));
            this.typeTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.EmojiView.27
                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrollStateChanged(int i10) {
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrolled(int i10, float f, int i11) {
                    SearchField searchField4;
                    SearchField searchField5;
                    EmojiView.this.checkGridVisibility(i10, f);
                    EmojiView emojiView = EmojiView.this;
                    emojiView.onPageScrolled(i10, (emojiView.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), i11);
                    boolean z9 = true;
                    EmojiView.this.showBottomTab(true, true);
                    int currentItem = EmojiView.this.pager.getCurrentItem();
                    if (currentItem == 0) {
                        searchField4 = EmojiView.this.emojiSearchField;
                    } else {
                        EmojiView emojiView2 = EmojiView.this;
                        if (currentItem == 1) {
                            searchField4 = emojiView2.gifSearchField;
                        } else {
                            searchField4 = emojiView2.stickersSearchField;
                        }
                    }
                    String string = searchField4.searchEditText.getText().toString();
                    for (int i12 = 0; i12 < 3; i12++) {
                        if (i12 == 0) {
                            searchField5 = EmojiView.this.emojiSearchField;
                        } else {
                            EmojiView emojiView3 = EmojiView.this;
                            if (i12 == 1) {
                                searchField5 = emojiView3.gifSearchField;
                            } else {
                                searchField5 = emojiView3.stickersSearchField;
                            }
                        }
                        if (searchField5 != null && searchField5 != searchField4 && searchField5.searchEditText != null && !searchField5.searchEditText.getText().toString().equals(string)) {
                            searchField5.searchEditText.setText(string);
                            searchField5.searchEditText.setSelection(string.length());
                        }
                    }
                    EmojiView emojiView4 = EmojiView.this;
                    if ((i10 != 0 || f <= 0.0f) && i10 != 1) {
                        z9 = false;
                    }
                    emojiView4.startStopVisibleGifs(z9);
                    EmojiView.this.updateStickerTabsPosition();
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageSelected(int i10) {
                    EmojiView.this.saveNewPage();
                    boolean z9 = false;
                    EmojiView.this.showBackspaceButton(i10 == 0, true);
                    EmojiView emojiView = EmojiView.this;
                    if (i10 == 2 && (z8 || emojiView.shouldDrawStickerSettings)) {
                        z9 = true;
                    }
                    emojiView.showStickerSettingsButton(z9, true);
                    if (EmojiView.this.delegate.isSearchOpened()) {
                        if (i10 == 0) {
                            if (EmojiView.this.emojiSearchField != null) {
                                EmojiView.this.emojiSearchField.searchEditText.requestFocus();
                                return;
                            }
                            return;
                        }
                        EmojiView emojiView2 = EmojiView.this;
                        if (i10 == 1) {
                            if (emojiView2.gifSearchField != null) {
                                EmojiView.this.gifSearchField.searchEditText.requestFocus();
                            }
                        } else if (emojiView2.stickersSearchField != null) {
                            EmojiView.this.stickersSearchField.searchEditText.requestFocus();
                        }
                    }
                }
            });
            ImageView imageView5 = new ImageView(context);
            this.searchButton = imageView5;
            imageView5.setImageResource(R.drawable.smiles_tab_search);
            this.searchButton.setColorFilter(new PorterDuffColorFilter(z7 ? getGlassIconColor(0.6f) : getThemedColor(Theme.key_chat_emojiPanelBackspace), mode));
            this.searchButton.setScaleType(scaleType);
            this.searchButton.setContentDescription(LocaleController.getString(R.string.Search));
            this.searchButton.setFocusable(true);
            this.searchButton.setVisibility(8);
            this.bottomTabContainer.addView(this.searchButton, LayoutHelper.createFrame(48, 48.0f, 83, 2.0f, 0.0f, 2.0f, 0.0f));
            this.searchButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view5) {
                    this.f$0.lambda$new$16(view5);
                }
            });
        } else {
            addView(view4, LayoutHelper.createFrame(56, 48.0f, (LocaleController.isRTL ? 3 : 5) | 80, 0.0f, 0.0f, 2.0f, 0.0f));
            Drawable drawableCreateSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor(i6), getThemedColor(i6));
            ScaleStateListAnimator.apply(this.backspaceButton);
            this.backspaceButton.setPadding(0, 0, AndroidUtilities.dp(2.0f), 0);
            this.backspaceButton.setBackground(drawableCreateSimpleSelectorCircleDrawable);
            this.backspaceButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chats_actionIcon), mode));
            this.backspaceButton.setContentDescription(LocaleController.getString(R.string.AccDescrBackspace));
            this.backspaceButton.setFocusable(true);
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame(48, 48.0f, 51, 2.0f, 0.0f, 2.0f, 0.0f));
            this.bottomTabContainerBackground.setVisibility(8);
        }
        addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context);
        this.mediaBanTooltip = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor(Theme.key_chat_gifSaveHintBackground)));
        this.mediaBanTooltip.setTextColor(getThemedColor(Theme.key_chat_gifSaveHintText));
        this.mediaBanTooltip.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(7.0f));
        this.mediaBanTooltip.setGravity(16);
        this.mediaBanTooltip.setTextSize(1, 14.0f);
        this.mediaBanTooltip.setVisibility(4);
        addView(this.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 81, 5.0f, 0.0f, 5.0f, 53.0f));
        this.emojiSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        EmojiColorPickerWindow emojiColorPickerWindowCreate = EmojiColorPickerWindow.create(context, resourcesProvider);
        this.colorPickerView = emojiColorPickerWindowCreate;
        emojiColorPickerWindowCreate.setOnSelectionUpdateListener(new Utilities.Callback2() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda9
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$new$17((Integer) obj, (Integer) obj2);
            }
        });
        this.currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
        Emoji.loadRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
        setAllow(z2, z3, false);
        if (Build.VERSION.SDK_INT >= 31) {
            BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode = new BlurredBackgroundSourceRenderNode(null);
            this.blurredBackgroundSourceRenderNode = blurredBackgroundSourceRenderNode;
            BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceRenderNode);
            this.blurredBackgroundDrawableFactory = blurredBackgroundDrawableViewFactory;
            blurredBackgroundDrawableViewFactory.setLiquidGlassEffectAllowed(LiteMode.isEnabled(262144));
            this.scrollableViewNoiseSuppressor = new DownscaleScrollableNoiseSuppressor();
        } else {
            this.blurredBackgroundSourceRenderNode = null;
            this.blurredBackgroundDrawableFactory = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceColor);
            this.scrollableViewNoiseSuppressor = null;
        }
        ViewPositionWatcher viewPositionWatcher = new ViewPositionWatcher(this);
        PagerSlidingTabStrip pagerSlidingTabStrip2 = this.typeTabs;
        if (pagerSlidingTabStrip2 != null) {
            viewPositionWatcher.subscribe(pagerSlidingTabStrip2, this, new ViewPositionWatcher.OnChangedListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda10
                @Override // org.telegram.ui.Components.chat.ViewPositionWatcher.OnChangedListener
                public final void onPositionChanged(View view5, RectF rectF2) {
                    this.f$0.lambda$new$18(view5, rectF2);
                }
            });
        }
        this.blurredBackgroundDrawableFactory.setSourceRootView(viewPositionWatcher, this);
        final IBlur3Capture[] iBlur3CaptureArr = new IBlur3Capture[3];
        EmojiGridView emojiGridView4 = this.emojiGridView;
        if (emojiGridView4 != null) {
            emojiGridView4.addEdgeEffectListener(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$19();
                }
            });
            final EmojiGridView emojiGridView5 = this.emojiGridView;
            Objects.requireNonNull(emojiGridView5);
            iBlur3CaptureArr[0] = new ViewGroupPartRenderer(emojiGridView5, this, new ViewGroupPartRenderer.DrawChildMethod() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda13
                @Override // org.telegram.ui.Components.blur3.ViewGroupPartRenderer.DrawChildMethod
                public final boolean drawChild(Canvas canvas, View view5, long j) {
                    return emojiGridView5.drawChild(canvas, view5, j);
                }
            });
        }
        RecyclerListView recyclerListView5 = this.gifGridView;
        if (recyclerListView5 != null) {
            recyclerListView5.addEdgeEffectListener(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$20();
                }
            });
            RecyclerListView recyclerListView6 = this.gifGridView;
            Objects.requireNonNull(recyclerListView6);
            iBlur3CaptureArr[1] = new ViewGroupPartRenderer(recyclerListView6, this, new EmojiView$$ExternalSyntheticLambda15(recyclerListView6));
        }
        RecyclerListView recyclerListView7 = this.stickersGridView;
        if (recyclerListView7 != null) {
            recyclerListView7.addEdgeEffectListener(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$21();
                }
            });
            iBlur3CaptureArr[i9] = new ViewGroupPartRenderer(this.stickersGridView, this, new ViewGroupPartRenderer.DrawChildMethod() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda17
                @Override // org.telegram.ui.Components.blur3.ViewGroupPartRenderer.DrawChildMethod
                public final boolean drawChild(Canvas canvas, View view5, long j) {
                    return this.f$0.lambda$new$22(canvas, view5, j);
                }
            });
        }
        this.blurCaptureMethod = new IBlur3Capture() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda18
            @Override // org.telegram.ui.Components.blur3.capture.IBlur3Capture
            public final void capture(Canvas canvas, RectF rectF2) {
                EmojiView.m10431$r8$lambda$4HN73U0mDwgTGy7HxQE4G45_LQ(iBlur3CaptureArr, canvas, rectF2);
            }
        };
        setBlurredBackgroundDrawableFactory(this.blurredBackgroundDrawableFactory);
    }

    public void lambda$install$7() {
            this.pack.installed = true;
            updateState(true);
        }

        private void uninstall(final TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            EmojiPacksAlert.uninstallSet(getFragment(), tL_messages_stickerSet, true, new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$uninstall$8(tL_messages_stickerSet);
                }
            }, false);
        }

        public void lambda$onCreateViewHolder$1(StickerSetNameCell stickerSetNameCell, View view) {
            RecyclerView.ViewHolder childViewHolder;
            if (EmojiView.this.stickersGridView.indexOfChild(stickerSetNameCell) == -1 || (childViewHolder = EmojiView.this.stickersGridView.getChildViewHolder(stickerSetNameCell)) == null) {
                return;
            }
            if (childViewHolder.getAdapterPosition() == EmojiView.this.groupStickerPackPosition) {
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = EmojiView.this.groupStickerSet;
                EmojiView emojiView = EmojiView.this;
                if (tL_messages_stickerSet != null) {
                    if (emojiView.delegate != null) {
                        EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
                        return;
                    }
                    return;
                }
                MessagesController.getEmojiSettings(emojiView.currentAccount).edit().putLong("group_hide_stickers_" + EmojiView.this.info.id, EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0L).apply();
                EmojiView.this.updateStickerTabs(false);
                if (EmojiView.this.stickersGridAdapter != null) {
                    EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
                    return;
                }
                return;
            }
            if (this.cache.get(childViewHolder.getAdapterPosition()) == EmojiView.this.recentStickers) {
                AlertDialog alertDialogCreate = new AlertDialog.Builder(this.context, EmojiView.this.resourcesProvider).setTitle(LocaleController.getString(R.string.ClearRecentStickersAlertTitle)).setMessage(LocaleController.getString(R.string.ClearRecentStickersAlertMessage)).setPositiveButton(LocaleController.getString(R.string.ClearButton), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda7
                    @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                    public final void onClick(AlertDialog alertDialog, int i) {
                        this.f$0.lambda$onCreateViewHolder$0(alertDialog, i);
                    }
                }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
                alertDialogCreate.show();
                TextView textView = (TextView) alertDialogCreate.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(EmojiView.this.getThemedColor(Theme.key_text_RedBold));
                }
            }
        }

        public void lambda$run$8(final String str) {
                String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, currentKeyboardLanguage)) {
                    MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
                }
                EmojiView.this.lastSearchKeyboardLanguage = currentKeyboardLanguage;
                final ArrayList arrayList = new ArrayList();
                final ArrayList arrayList2 = new ArrayList();
                final ArrayList arrayList3 = new ArrayList();
                Utilities.doCallbacks(new Utilities.Callback() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$5$$ExternalSyntheticLambda3
                    @Override 
                    public final void run(Object obj) {
                        this.f$0.lambda$run$2(str, arrayList3, (Runnable) obj);
                    }
                }, new Utilities.Callback() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$5$$ExternalSyntheticLambda4
                    @Override 
                    public final void run(Object obj) {
                        this.f$0.lambda$run$4(str, (Runnable) obj);
                    }
                }, new Utilities.Callback() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$5$$ExternalSyntheticLambda5
                    @Override 
                    public final void run(Object obj) {
                        this.f$0.lambda$run$5(str, arrayList2, (Runnable) obj);
                    }
                }, new Utilities.Callback() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$5$$ExternalSyntheticLambda6
                    @Override 
                    public final void run(Object obj) {
                        this.f$0.lambda$run$6(arrayList, (Runnable) obj);
                    }
                }, new Utilities.Callback() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$5$$ExternalSyntheticLambda7
                    @Override 
                    public final void run(Object obj) {
                        this.f$0.lambda$run$7(str, arrayList, arrayList2, arrayList3, (Runnable) obj);
                    }
                });
            }

            public void lambda$run$1(ArrayList arrayList, Runnable runnable, ArrayList arrayList2) {
                ArrayList<TLRPC.Document> arrayList3;
                int size = arrayList2.size();
                int i = 0;
                while (i < size) {
                    Object obj = arrayList2.get(i);
                    i++;
                    TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) obj;
                    if (stickerSetCovered instanceof TLRPC.TL_stickerSetFullCovered) {
                        arrayList3 = ((TLRPC.TL_stickerSetFullCovered) stickerSetCovered).documents;
                    } else if (stickerSetCovered instanceof TLRPC.TL_stickerSetNoCovered) {
                        TLRPC.TL_messages_stickerSet stickerSet = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSet(MediaDataController.getInputStickerSet(stickerSetCovered.set), Integer.valueOf(stickerSetCovered.set.hash), true);
                        arrayList3 = stickerSet != null ? stickerSet.documents : null;
                    } else {
                        arrayList3 = stickerSetCovered.covers;
                    }
                    if (arrayList3 != null && !arrayList3.isEmpty()) {
                        arrayList.add(new EmojiPackInfo(stickerSetCovered, arrayList3));
                    }
                }
                runnable.run();
            }

            public void lambda$run$3(String str, Runnable runnable, ArrayList arrayList, String str2) {
                if (str.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
                    EmojiSearchAdapter.this.lastSearchAlias = str2;
                    EmojiSearchAdapter.this.resultPre.addAll(arrayList);
                    runnable.run();
                }
            }

            void lambda$searchEmoji$0(String str, ArrayList arrayList, Runnable runnable, ArrayList arrayList2) {
            if (str.equals(this.lastSearchEmojiString)) {
                AnimatedEmojiDrawable.getDocumentFetcher(EmojiView.this.currentAccount).putDocuments(arrayList2);
                int size = arrayList2.size();
                int i = 0;
                while (i < size) {
                    Object obj = arrayList2.get(i);
                    i++;
                    MediaDataController.KeywordResult keywordResult = new MediaDataController.KeywordResult();
                    keywordResult.emoji = "animated_" + ((TLRPC.Document) obj).id;
                    keywordResult.keyword = null;
                    arrayList.add(keywordResult);
                }
                runnable.run();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.foundPacksListView.adapter.update(false);
            super.notifyDataSetChanged();
        }
    }

    public class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public void customOnDraw(Canvas canvas, View view, int i) {
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public Drawable getPageIconDrawable(int i) {
            return null;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        private EmojiPagesAdapter() {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public boolean canScrollToTab(int i) {
            if (i == 1 || i == 2) {
                EmojiView emojiView = EmojiView.this;
                if (emojiView.stickersBanned) {
                    emojiView.showStickerBanHint(true, false, i == 1);
                    return false;
                }
            }
            if (i == 0) {
                EmojiView emojiView2 = EmojiView.this;
                if (emojiView2.emojiBanned) {
                    emojiView2.showStickerBanHint(true, true, false);
                    return false;
                }
            }
            return true;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return EmojiView.this.currentTabs.size();
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public CharSequence getPageTitle(int i) {
            if (i == 0) {
                return LocaleController.getString(R.string.Emoji);
            }
            if (i == 1) {
                return LocaleController.getString(R.string.AccDescrGIFs);
            }
            if (i != 2) {
                return null;
            }
            return LocaleController.getString(R.string.AccDescrStickers);
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public int getTabPadding(int i) {
            return AndroidUtilities.dp(i == 1 ? 12.0f : 18.0f);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View view = ((Tab) EmojiView.this.currentTabs.get(i)).view;
            viewGroup.addView(view);
            return view;
        }
    }

    public class GifAdapter extends RecyclerListView.SelectionAdapter {
        private boolean addSearch;
        private TLRPC.User bot;
        private final Context context;
        private int firstResultItem;
        private int itemsCount;
        private String lastSearchImageString;
        private boolean lastSearchIsEmoji;
        private final int maxRecentRowsCount;
        private String nextSearchOffset;
        private final GifProgressEmptyView progressEmptyView;
        private int recentItemsCount;
        private int reqId;
        private ArrayList<TLRPC.BotInlineResult> results;
        private HashMap<String, TLRPC.BotInlineResult> resultsMap;
        private boolean searchEndReached;
        private Runnable searchRunnable;
        private boolean searchingUser;
        private boolean showTrendingWhenSearchEmpty;
        private int trendingSectionItem;
        private final boolean withRecent;

        public GifAdapter(EmojiView emojiView, Context context) {
            this(context, false, 0);
        }

        public GifAdapter(EmojiView emojiView, Context context, boolean z) {
            this(context, z, z ? Integer.MAX_VALUE : 0);
        }

        public GifAdapter(Context context, boolean z, int i) {
            this.results = new ArrayList<>();
            this.resultsMap = new HashMap<>();
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.context = context;
            this.withRecent = z;
            this.maxRecentRowsCount = i;
            this.progressEmptyView = z ? null : EmojiView.this.new GifProgressEmptyView(context);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.itemsCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0 && this.addSearch) {
                return 1;
            }
            boolean z = this.withRecent;
            if (z && i == this.trendingSectionItem) {
                return 2;
            }
            return (z || !this.results.isEmpty()) ? 0 : 3;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            if (i != 0) {
                if (i == 1) {
                    View view3 = new View(EmojiView.this.getContext());
                    view3.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    view2 = view3;
                } else if (i == 2) {
                    StickerSetNameCell stickerSetNameCell = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider, EmojiView.this.glassDesign);
                    stickerSetNameCell.setText(LocaleController.getString(R.string.FeaturedGifs), 0);
                    RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -2);
                    ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = AndroidUtilities.dp(2.5f);
                    ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = AndroidUtilities.dp(5.5f);
                    stickerSetNameCell.setLayoutParams(layoutParams);
                    view2 = stickerSetNameCell;
                } else {
                    GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
                    gifProgressEmptyView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view = gifProgressEmptyView;
                }
                view = view2;
            } else {
                ContextLinkCell contextLinkCell = new ContextLinkCell(this.context);
                contextLinkCell.setIsKeyboard(true);
                contextLinkCell.setCanPreviewGif(true);
                view = contextLinkCell;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() != 0) {
                return;
            }
            ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
            int i2 = this.firstResultItem;
            if (i2 >= 0 && i >= i2) {
                contextLinkCell.setLink(this.results.get(i - i2), this.bot, true, false, false, true);
            } else {
                contextLinkCell.setGif((TLRPC.Document) EmojiView.this.recentGifs.get(i - (this.addSearch ? 1 : 0)), false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRecentItemsCount();
            updateItems();
            super.notifyDataSetChanged();
        }

        private void updateItems() {
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.itemsCount = 0;
            if (this.addSearch) {
                this.itemsCount = 1;
            }
            if (this.withRecent) {
                this.itemsCount += this.recentItemsCount;
            }
            boolean zIsEmpty = this.results.isEmpty();
            boolean z = this.withRecent;
            if (zIsEmpty) {
                if (z) {
                    return;
                }
                this.itemsCount++;
                return;
            }
            if (z && this.recentItemsCount > 0) {
                int i = this.itemsCount;
                this.itemsCount = i + 1;
                this.trendingSectionItem = i;
            }
            int i2 = this.itemsCount;
            this.firstResultItem = i2;
            this.itemsCount = i2 + this.results.size();
        }

        private void updateRecentItemsCount() {
            int i;
            if (!this.withRecent || (i = this.maxRecentRowsCount) == 0) {
                return;
            }
            EmojiView emojiView = EmojiView.this;
            if (i == Integer.MAX_VALUE) {
                this.recentItemsCount = emojiView.recentGifs.size();
                return;
            }
            if (emojiView.gifGridView.getMeasuredWidth() == 0) {
                return;
            }
            int measuredWidth = EmojiView.this.gifGridView.getMeasuredWidth();
            int spanCount = EmojiView.this.gifLayoutManager.getSpanCount();
            int iDp = AndroidUtilities.dp(100.0f);
            this.recentItemsCount = 0;
            int size = EmojiView.this.recentGifs.size();
            int i2 = spanCount;
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < size; i5++) {
                Size sizeFixSize = EmojiView.this.gifLayoutManager.fixSize(EmojiView.this.gifLayoutManager.getSizeForItem((TLRPC.Document) EmojiView.this.recentGifs.get(i5)));
                int iMin = Math.min(spanCount, (int) Math.floor(spanCount * (((sizeFixSize.width / sizeFixSize.height) * iDp) / measuredWidth)));
                if (i2 < iMin) {
                    this.recentItemsCount += i3;
                    i4++;
                    if (i4 == this.maxRecentRowsCount) {
                        break;
                    }
                    i2 = spanCount;
                    i3 = 0;
                }
                i3++;
                i2 -= iMin;
            }
            if (i4 < this.maxRecentRowsCount) {
                this.recentItemsCount += i3;
            }
        }

        public void loadTrendingGifs() {
            search(_UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, true, true, true);
        }

        private void searchBotUser() {
            if (this.searchingUser) {
                return;
            }
            this.searchingUser = true;
            TLRPC.TL_contacts_resolveUsername tL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
            tL_contacts_resolveUsername.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_contacts_resolveUsername, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda1
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$searchBotUser$1(tLObject, tL_error);
                }
            });
        }

        public void lambda$preload$1(final String str, final String str2, final boolean z, final String str3, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifSearchPreloader$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$preload$0(str, str2, z, str3, tLObject);
                }
            });
        }

        public void lambda$preload$0(String str, String str2, boolean z, String str3, TLObject tLObject) {
            this.loadingKeys.remove(str3);
            if (EmojiView.this.gifSearchAdapter.lastSearchIsEmoji && EmojiView.this.gifSearchAdapter.lastSearchImageString.equals(str)) {
                EmojiView.this.gifSearchAdapter.lambda$search$3(str, str2, false, true, z, str3, tLObject);
                return;
            }
            if (z && (!(tLObject instanceof TLRPC.messages_BotResults) || ((TLRPC.messages_BotResults) tLObject).results.isEmpty())) {
                preload(str, str2, false);
            } else {
                if (!(tLObject instanceof TLRPC.messages_BotResults) || EmojiView.this.gifCache.containsKey(str3)) {
                    return;
                }
                EmojiView.this.gifCache.put(str3, (TLRPC.messages_BotResults) tLObject);
            }
        }
    }

    public class GifLayoutManager extends ExtendedGridLayoutManager {
        private Size size;

        public GifLayoutManager(Context context) {
            super(context, 100, true);
            this.size = new Size();
            setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiView.GifLayoutManager.1
                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int i) {
                    if ((i == 0 && EmojiView.this.gifAdapter.addSearch) || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                        return GifLayoutManager.this.getSpanCount();
                    }
                    GifLayoutManager gifLayoutManager = GifLayoutManager.this;
                    return gifLayoutManager.getSpanSizeForItem(i - (EmojiView.this.gifAdapter.addSearch ? 1 : 0));
                }
            });
        }

        @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
        public Size getSizeForItem(int i) {
            ArrayList<TLRPC.DocumentAttribute> arrayList;
            TLRPC.Document document;
            RecyclerView.Adapter adapter = EmojiView.this.gifGridView.getAdapter();
            GifAdapter gifAdapter = EmojiView.this.gifAdapter;
            EmojiView emojiView = EmojiView.this;
            TLRPC.Document document2 = null;
            arrayList = null;
            ArrayList<TLRPC.DocumentAttribute> arrayList2 = null;
            if (adapter == gifAdapter) {
                int i2 = emojiView.gifAdapter.recentItemsCount;
                EmojiView emojiView2 = EmojiView.this;
                if (i > i2) {
                    TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) emojiView2.gifAdapter.results.get((i - EmojiView.this.gifAdapter.recentItemsCount) - 1);
                    document = botInlineResult.document;
                    if (document != null) {
                        arrayList2 = document.attributes;
                    } else {
                        TLRPC.WebDocument webDocument = botInlineResult.content;
                        if (webDocument != null) {
                            arrayList2 = webDocument.attributes;
                        } else {
                            TLRPC.WebDocument webDocument2 = botInlineResult.thumb;
                            if (webDocument2 != null) {
                                arrayList2 = webDocument2.attributes;
                            }
                        }
                    }
                    arrayList = arrayList2;
                    document2 = document;
                } else {
                    if (i == emojiView2.gifAdapter.recentItemsCount) {
                        return null;
                    }
                    document2 = (TLRPC.Document) EmojiView.this.recentGifs.get(i);
                    arrayList = document2.attributes;
                }
            } else if (emojiView.gifSearchAdapter.results.isEmpty()) {
                arrayList = null;
            } else {
                TLRPC.BotInlineResult botInlineResult2 = (TLRPC.BotInlineResult) EmojiView.this.gifSearchAdapter.results.get(i);
                document = botInlineResult2.document;
                if (document != null) {
                    arrayList2 = document.attributes;
                } else {
                    TLRPC.WebDocument webDocument3 = botInlineResult2.content;
                    if (webDocument3 != null) {
                        arrayList2 = webDocument3.attributes;
                    } else {
                        TLRPC.WebDocument webDocument4 = botInlineResult2.thumb;
                        if (webDocument4 != null) {
                            arrayList2 = webDocument4.attributes;
                        }
                    }
                }
                arrayList = arrayList2;
                document2 = document;
            }
            return getSizeForItem(document2, arrayList);
        }

        @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
        public int getFlowItemCount() {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                return 0;
            }
            return getItemCount() - 1;
        }

        public Size getSizeForItem(TLRPC.Document document) {
            return getSizeForItem(document, document.attributes);
        }

        public Size getSizeForItem(TLRPC.Document document, List<TLRPC.DocumentAttribute> list) {
            TLRPC.PhotoSize closestPhotoSizeWithSize;
            int i;
            int i2;
            Size size = this.size;
            size.height = 100.0f;
            size.width = 100.0f;
            if (document != null && (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90)) != null && (i = closestPhotoSizeWithSize.w) != 0 && (i2 = closestPhotoSizeWithSize.h) != 0) {
                Size size2 = this.size;
                size2.width = i;
                size2.height = i2;
            }
            if (list != null) {
                for (int i3 = 0; i3 < list.size(); i3++) {
                    TLRPC.DocumentAttribute documentAttribute = list.get(i3);
                    if ((documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) || (documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                        Size size3 = this.size;
                        size3.width = documentAttribute.w;
                        size3.height = documentAttribute.h;
                        break;
                    }
                }
            }
            return this.size;
        }
    }

    public class GifProgressEmptyView extends FrameLayout {
        private final ImageView imageView;
        private boolean loadingState;
        private final RadialProgressView progressView;
        private final TextView textView;

        public GifProgressEmptyView(Context context) {
            super(context);
            ImageView imageView = new ImageView(getContext());
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(R.drawable.gif_empty);
            int i = Theme.key_chat_emojiPanelEmptyText;
            imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor(i), PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 8.0f, 0.0f, 0.0f));
            TextView textView = new TextView(getContext());
            this.textView = textView;
            textView.setText(LocaleController.getString(R.string.NoGIFsFound));
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(EmojiView.this.getThemedColor(i));
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 42.0f, 0.0f, 0.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context, EmojiView.this.resourcesProvider);
            this.progressView = radialProgressView;
            radialProgressView.setVisibility(8);
            radialProgressView.setProgressColor(EmojiView.this.getThemedColor(Theme.key_progressCircle));
            addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            int iDp;
            int measuredHeight = EmojiView.this.gifGridView.getMeasuredHeight();
            if (!this.loadingState) {
                iDp = (int) ((((measuredHeight - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f);
            } else {
                iDp = measuredHeight - AndroidUtilities.dp(80.0f);
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(iDp, TLObject.FLAG_30));
        }

        public void setLoadingState(boolean z) {
            if (this.loadingState != z) {
                this.loadingState = z;
                this.imageView.setVisibility(z ? 8 : 0);
                this.textView.setVisibility(z ? 8 : 0);
                this.progressView.setVisibility(z ? 0 : 8);
            }
        }
    }

    public class StickersSearchGridAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int emojiSearchId;
        private FoundEmojiPacksRecyclerView foundPacksListView;
        private boolean isCompleted;
        private int reqId;
        private int reqId2;
        private String searchQuery;
        private long selectedPackId;
        private TLRPC.StickerSet selectedPackStickerSet;
        private ArrayList<TLRPC.Document> selectedPackStickers;
        private int totalItems;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        private SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParent = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<String> positionToEmoji = new SparseArray<>();
        private ArrayList<TLRPC.TL_messages_stickerSet> localPacks = new ArrayList<>();
        private HashMap<TLRPC.TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
        private HashMap<TLRPC.TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
        private HashMap<ArrayList<TLRPC.Document>, String> emojiStickers = new HashMap<>();
        private ArrayList<ArrayList<TLRPC.Document>> emojiArrays = new ArrayList<>();
        private ArrayList<EmojiPackInfo> foundEmojiPacks = new ArrayList<>();
        private ArrayList<TLRPC.Document> globalSearchArray = new ArrayList<>();
        private final SearchRunnable searchRunnable = new AnonymousClass1();
        private int foundPacksRow = -1;

        public class AnonymousClass1 implements SearchRunnable {
            int lastId;
            String query;
            final ArrayList<TLRPC.TL_messages_stickerSet> localPacks = new ArrayList<>();
            final HashMap<TLRPC.TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
            final HashMap<TLRPC.TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
            final HashMap<ArrayList<TLRPC.Document>, String> emojiStickers = new HashMap<>();
            final ArrayList<ArrayList<TLRPC.Document>> emojiArrays = new ArrayList<>();
            final ArrayList<EmojiPackInfo> foundEmojiPacks = new ArrayList<>();
            final ArrayList<TLRPC.Document> emojiStickersArray = new ArrayList<>(0);
            final ArrayList<TLRPC.Document> emojiStickersArray2 = new ArrayList<>(0);
            final LongSparseArray<TLRPC.Document> emojiStickersMap = new LongSparseArray<>(0);

            public AnonymousClass1() {
            }

            public void searchFinish() {
                if (StickersSearchGridAdapter.this.emojiSearchId != this.lastId) {
                    return;
                }
                this.emojiArrays.remove(this.emojiStickersArray);
                StickersSearchGridAdapter.this.localPacks = this.localPacks;
                StickersSearchGridAdapter.this.localPacksByShortName = this.localPacksByShortName;
                StickersSearchGridAdapter.this.localPacksByName = this.localPacksByName;
                StickersSearchGridAdapter.this.emojiStickers = this.emojiStickers;
                StickersSearchGridAdapter.this.emojiArrays = this.emojiArrays;
                StickersSearchGridAdapter.this.foundEmojiPacks = this.foundEmojiPacks;
                StickersSearchGridAdapter.this.globalSearchArray = new ArrayList(this.emojiStickersArray);
                EmojiView.this.stickersSearchField.showProgress(false);
                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                }
                StickersSearchGridAdapter.this.notifyDataSetChanged();
            }

            void lambda$addFromSuggestions$0(HashMap map, Runnable runnable, ArrayList arrayList, String str) {
                if (StickersSearchGridAdapter.this.emojiSearchId != this.lastId) {
                    return;
                }
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    String str2 = ((MediaDataController.KeywordResult) arrayList.get(i)).emoji;
                    ArrayList<TLRPC.Document> arrayList2 = (ArrayList) map.get(str2);
                    if (arrayList2 != null && !arrayList2.isEmpty() && !this.emojiStickers.containsKey(arrayList2)) {
                        this.emojiStickers.put(arrayList2, str2);
                        this.emojiArrays.add(arrayList2);
                    }
                }
                runnable.run();
            }

            public void addPremiumStickers(Runnable runnable) {
                HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(EmojiView.this.currentAccount).getAllStickers();
                HashSet hashSet = new HashSet();
                ArrayList arrayList = new ArrayList();
                Iterator<ArrayList<TLRPC.Document>> it = allStickers.values().iterator();
                while (true) {
                    int i = 0;
                    if (!it.hasNext()) {
                        break;
                    }
                    ArrayList<TLRPC.Document> next = it.next();
                    int size = next.size();
                    while (i < size) {
                        TLRPC.Document document = next.get(i);
                        i++;
                        TLRPC.Document document2 = document;
                        if (!hashSet.contains(Long.valueOf(document2.id)) && MessageObject.isPremiumSticker(document2)) {
                            hashSet.add(Long.valueOf(document2.id));
                            arrayList.add(document2);
                            this.emojiStickersMap.put(document2.id, document2);
                        }
                    }
                }
                ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedStickerSets();
                int size2 = featuredStickerSets.size();
                int i2 = 0;
                while (i2 < size2) {
                    TLRPC.StickerSetCovered stickerSetCovered = featuredStickerSets.get(i2);
                    i2++;
                    TLRPC.StickerSetCovered stickerSetCovered2 = stickerSetCovered;
                    TLRPC.Document document3 = stickerSetCovered2.cover;
                    if (document3 != null && !hashSet.contains(Long.valueOf(document3.id)) && MessageObject.isPremiumSticker(stickerSetCovered2.cover)) {
                        hashSet.add(Long.valueOf(stickerSetCovered2.cover.id));
                        arrayList.add(stickerSetCovered2.cover);
                        LongSparseArray<TLRPC.Document> longSparseArray = this.emojiStickersMap;
                        TLRPC.Document document4 = stickerSetCovered2.cover;
                        longSparseArray.put(document4.id, document4);
                    }
                    ArrayList<TLRPC.Document> arrayList2 = stickerSetCovered2.covers;
                    if (arrayList2 != null) {
                        int size3 = arrayList2.size();
                        int i3 = 0;
                        while (i3 < size3) {
                            TLRPC.Document document5 = arrayList2.get(i3);
                            i3++;
                            TLRPC.Document document6 = document5;
                            if (!hashSet.contains(Long.valueOf(document6.id)) && MessageObject.isPremiumSticker(document6)) {
                                hashSet.add(Long.valueOf(document6.id));
                                arrayList.add(document6);
                                this.emojiStickersMap.put(document6.id, document6);
                            }
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    this.emojiStickersArray2.addAll(arrayList);
                    this.emojiStickers.put(this.emojiStickersArray2, StickersSearchGridAdapter.this.searchQuery);
                    this.emojiArrays.add(this.emojiStickersArray2);
                }
                runnable.run();
            }

            public void addLocalPacks(Runnable runnable) {
                int iIndexOfIgnoreCase;
                int iIndexOfIgnoreCase2;
                ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSets(0);
                MessagesController.getInstance(EmojiView.this.currentAccount).filterPremiumStickers(stickerSets);
                int size = stickerSets.size();
                for (int i = 0; i < size; i++) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSets.get(i);
                    int iIndexOfIgnoreCase3 = AndroidUtilities.indexOfIgnoreCase(tL_messages_stickerSet.set.title, StickersSearchGridAdapter.this.searchQuery);
                    if (iIndexOfIgnoreCase3 >= 0) {
                        if (iIndexOfIgnoreCase3 == 0 || tL_messages_stickerSet.set.title.charAt(iIndexOfIgnoreCase3 - 1) == ' ') {
                            this.localPacks.add(tL_messages_stickerSet);
                            this.localPacksByName.put(tL_messages_stickerSet, Integer.valueOf(iIndexOfIgnoreCase3));
                        }
                    } else {
                        String str = tL_messages_stickerSet.set.short_name;
                        if (str != null && (iIndexOfIgnoreCase2 = AndroidUtilities.indexOfIgnoreCase(str, StickersSearchGridAdapter.this.searchQuery)) >= 0 && (iIndexOfIgnoreCase2 == 0 || tL_messages_stickerSet.set.short_name.charAt(iIndexOfIgnoreCase2 - 1) == ' ')) {
                            this.localPacks.add(tL_messages_stickerSet);
                            this.localPacksByShortName.put(tL_messages_stickerSet, Boolean.TRUE);
                        }
                    }
                }
                ArrayList<TLRPC.TL_messages_stickerSet> stickerSets2 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSets(3);
                MessagesController.getInstance(EmojiView.this.currentAccount).filterPremiumStickers(stickerSets2);
                int size2 = stickerSets2.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = stickerSets2.get(i2);
                    int iIndexOfIgnoreCase4 = AndroidUtilities.indexOfIgnoreCase(tL_messages_stickerSet2.set.title, StickersSearchGridAdapter.this.searchQuery);
                    if (iIndexOfIgnoreCase4 >= 0) {
                        if (iIndexOfIgnoreCase4 == 0 || tL_messages_stickerSet2.set.title.charAt(iIndexOfIgnoreCase4 - 1) == ' ') {
                            this.localPacks.add(tL_messages_stickerSet2);
                            this.localPacksByName.put(tL_messages_stickerSet2, Integer.valueOf(iIndexOfIgnoreCase4));
                        }
                    } else {
                        String str2 = tL_messages_stickerSet2.set.short_name;
                        if (str2 != null && (iIndexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(str2, StickersSearchGridAdapter.this.searchQuery)) >= 0 && (iIndexOfIgnoreCase == 0 || tL_messages_stickerSet2.set.short_name.charAt(iIndexOfIgnoreCase - 1) == ' ')) {
                            this.localPacks.add(tL_messages_stickerSet2);
                            this.localPacksByShortName.put(tL_messages_stickerSet2, Boolean.TRUE);
                        }
                    }
                }
                runnable.run();
            }

            public void searchStickerSetsByName(final Runnable runnable) {
                MediaDataController.getInstance(EmojiView.this.currentAccount).searchStickerSets(false, this.query, new Utilities.Callback() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda9
                    @Override 
                    public final void run(Object obj) {
                        this.f$0.lambda$searchStickerSetsByName$1(runnable, (ArrayList) obj);
                    }
                });
            }

            public void lambda$searchStickerSets$2(boolean z, Runnable runnable, ArrayList arrayList) {
                if (StickersSearchGridAdapter.this.emojiSearchId != this.lastId) {
                    return;
                }
                int i = 0;
                if (z) {
                    int size = this.emojiStickersArray.size();
                    this.emojiStickersArray.clear();
                    StickersSearchGridAdapter.this.isCompleted = size == arrayList.size();
                }
                this.emojiStickersArray.addAll(arrayList);
                int size2 = arrayList.size();
                while (i < size2) {
                    Object obj = arrayList.get(i);
                    i++;
                    TLRPC.Document document = (TLRPC.Document) obj;
                    this.emojiStickersMap.put(document.id, document);
                }
                this.emojiStickers.put(this.emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                runnable.run();
            }

            public void searchStickers(final Runnable runnable) {
                if (Emoji.fullyConsistsOfEmojis(StickersSearchGridAdapter.this.searchQuery)) {
                    final TLRPC.TL_messages_getStickers tL_messages_getStickers = new TLRPC.TL_messages_getStickers();
                    tL_messages_getStickers.emoticon = this.query;
                    tL_messages_getStickers.hash = 0L;
                    StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                    stickersSearchGridAdapter.reqId2 = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_messages_getStickers, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda10
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            this.f$0.lambda$searchStickers$4(tL_messages_getStickers, runnable, tLObject, tL_error);
                        }
                    });
                    return;
                }
                runnable.run();
            }

            public void lambda$loadNext$5(Runnable runnable) {
                searchStickerSets(runnable, true);
            }
        }

        public StickersSearchGridAdapter(Context context) {
            this.context = context;
            FoundEmojiPacksRecyclerView foundEmojiPacksRecyclerView = new FoundEmojiPacksRecyclerView(context, EmojiView.this.currentAccount, -1, false, new Utilities.Callback2() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$$ExternalSyntheticLambda1
                @Override 
                public final void run(Object obj, Object obj2) {
                    this.f$0.foundPackListFillItems((ArrayList) obj, (UniversalAdapter) obj2);
                }
            }, new Utilities.Callback5() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$$ExternalSyntheticLambda2
                @Override 
                public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    this.f$0.foundPackListOnClickItem((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
                }
            }, null, EmojiView.this.resourcesProvider, -1, 0) { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.2
            };
            this.foundPacksListView = foundEmojiPacksRecyclerView;
            foundEmojiPacksRecyclerView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f));
            this.foundPacksListView.setClipToPadding(false);
            this.foundPacksListView.adapter.setApplyBackground(false);
            this.foundPacksListView.setNestedScrollingEnabled(false);
            this.foundPacksListView.setDrawSelection(false);
            this.foundPacksListView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.3
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0) {
                        EmojiView.this.ignorePagerScroll = true;
                    } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                        EmojiView.this.ignorePagerScroll = false;
                    }
                    return false;
                }
            });
        }

        /* JADX WARN: Code duplicated, block: B:19:0x0059  */
        /* JADX WARN: Code duplicated, block: B:22:0x0064  */
        /* JADX WARN: Code duplicated, block: B:38:0x00be  */
        /* JADX WARN: Code duplicated, block: B:39:0x00c1  */
        /* JADX WARN: Code duplicated, block: B:42:0x00d1  */
        /* JADX WARN: Code duplicated, block: B:43:0x00d3  */
        /* JADX WARN: Code duplicated, block: B:46:0x00e3  */
        /* JADX WARN: Code duplicated, block: B:49:0x00f9  */
        /* JADX WARN: Code duplicated, block: B:53:0x0067 A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:54:? A[RETURN, SYNTHETIC] */
        public void foundPackListOnClickItem(UItem uItem, View view, int i, float f, float f2) {
            TLRPC.StickerSet stickerSet;
            TLRPC.StickerSet stickerSet2;
            int childCount;
            int i2;
            ArrayList<TLRPC.Document> arrayList;
            boolean z;
            boolean z2;
            TLRPC.TL_messages_stickerSet stickerSet3;
            FoundStickerPackCell foundStickerPackCell;
            Object obj = uItem.object;
            TLRPC.Document document = null;
            if (obj instanceof TLRPC.StickerSetCovered) {
                TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) obj;
                EmojiPackInfo emojiPackInfo = (EmojiPackInfo) uItem.object2;
                long j = this.selectedPackId;
                stickerSet2 = stickerSetCovered.set;
                long j2 = stickerSet2.id;
                if (j == j2) {
                    this.selectedPackId = 0L;
                    stickerSet2 = null;
                } else {
                    this.selectedPackId = j2;
                    this.selectedPackStickers = emojiPackInfo.documents;
                    this.selectedPackStickerSet = stickerSetCovered.set;
                }
            } else {
                if (obj instanceof TLRPC.TL_messages_stickerSet) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) obj;
                    long j3 = this.selectedPackId;
                    stickerSet2 = tL_messages_stickerSet.set;
                    long j4 = stickerSet2.id;
                    if (j3 == j4) {
                        this.selectedPackId = 0L;
                    } else {
                        this.selectedPackId = j4;
                        this.selectedPackStickers = tL_messages_stickerSet.documents;
                        this.selectedPackStickerSet = stickerSet2;
                    }
                }
                stickerSet = null;
                childCount = this.foundPacksListView.getChildCount();
                for (i2 = 0; i2 < childCount; i2++) {
                    foundStickerPackCell = (FoundStickerPackCell) this.foundPacksListView.getChildAt(i2);
                    if (foundStickerPackCell == view) {
                        foundStickerPackCell.setSelected(false, true);
                    }
                }
                if (this.selectedPackId != 0 && this.selectedPackStickers.size() < this.selectedPackStickerSet.count && (stickerSet3 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSet(this.selectedPackStickerSet, false)) != null) {
                    this.selectedPackStickers = stickerSet3.documents;
                }
                TLObject tLObject = (TLObject) uItem.object;
                EmojiView emojiView = EmojiView.this;
                FoundStickerPackButton foundStickerPackButton = emojiView.stickerAddPackButton;
                arrayList = this.selectedPackStickers;
                if (arrayList != null && !arrayList.isEmpty()) {
                    document = this.selectedPackStickers.get(0);
                }
                TLRPC.Document document2 = document;
                if (EmojiView.this.animatorSearchStickerPackSelected.getFloatValue() > 0.0f) {
                    z = true;
                } else {
                    z = false;
                }
                emojiView.setFoundPackButtonText(foundStickerPackButton, tLObject, stickerSet, document2, false, z);
                FoundStickerPackCell foundStickerPackCell2 = (FoundStickerPackCell) view;
                if (this.selectedPackId != 0) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                foundStickerPackCell2.setSelected(z2, true);
                EmojiView.this.animatorSearchStickerPackSelected.setValue(this.selectedPackId != 0, true);
                notifyDataSetChanged();
                EmojiView.this.stickersSearchField.hideKeyboard();
                if (this.selectedPackId != 0) {
                    this.foundPacksListView.scrollOnSelect(view);
                }
            }
            stickerSet = stickerSet2;
            childCount = this.foundPacksListView.getChildCount();
            while (i2 < childCount) {
                foundStickerPackCell = (FoundStickerPackCell) this.foundPacksListView.getChildAt(i2);
                if (foundStickerPackCell == view) {
                    foundStickerPackCell.setSelected(false, true);
                }
            }
            if (this.selectedPackId != 0) {
                this.selectedPackStickers = stickerSet3.documents;
            }
            TLObject tLObject2 = (TLObject) uItem.object;
            EmojiView emojiView2 = EmojiView.this;
            FoundStickerPackButton foundStickerPackButton2 = emojiView2.stickerAddPackButton;
            arrayList = this.selectedPackStickers;
            if (arrayList != null) {
                document = this.selectedPackStickers.get(0);
            }
            TLRPC.Document document3 = document;
            if (EmojiView.this.animatorSearchStickerPackSelected.getFloatValue() > 0.0f) {
                z = true;
            } else {
                z = false;
            }
            emojiView2.setFoundPackButtonText(foundStickerPackButton2, tLObject2, stickerSet, document3, false, z);
            FoundStickerPackCell foundStickerPackCell3 = (FoundStickerPackCell) view;
            if (this.selectedPackId != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            foundStickerPackCell3.setSelected(z2, true);
            EmojiView.this.animatorSearchStickerPackSelected.setValue(this.selectedPackId != 0, true);
            notifyDataSetChanged();
            EmojiView.this.stickersSearchField.hideKeyboard();
            if (this.selectedPackId != 0) {
                this.foundPacksListView.scrollOnSelect(view);
            }
        }

        public void resetSelectedPackId() {
            int childCount = this.foundPacksListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                ((FoundStickerPackCell) this.foundPacksListView.getChildAt(i)).setSelected(false, true);
            }
            this.selectedPackId = 0L;
            EmojiView.this.animatorSearchStickerPackSelected.setValue(false, true);
            notifyDataSetChanged();
        }

        public void foundPackListFillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
            LongSparseIntArray longSparseIntArray = new LongSparseIntArray();
            ArrayList<TLRPC.TL_messages_stickerSet> arrayList2 = this.localPacks;
            int size = arrayList2.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = arrayList2.get(i);
                i++;
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = tL_messages_stickerSet;
                if (longSparseIntArray.indexOfKey(tL_messages_stickerSet2.set.id) < 0) {
                    longSparseIntArray.append(tL_messages_stickerSet2.set.id, 1);
                    arrayList.add(FoundStickerPackFactory.of(tL_messages_stickerSet2, tL_messages_stickerSet2.set.id == this.selectedPackId));
                }
            }
            ArrayList<EmojiPackInfo> arrayList3 = this.foundEmojiPacks;
            int size2 = arrayList3.size();
            int i2 = 0;
            while (i2 < size2) {
                EmojiPackInfo emojiPackInfo = arrayList3.get(i2);
                i2++;
                EmojiPackInfo emojiPackInfo2 = emojiPackInfo;
                if (longSparseIntArray.indexOfKey(emojiPackInfo2.set.id) < 0) {
                    longSparseIntArray.append(emojiPackInfo2.set.id, 1);
                    arrayList.add(FoundStickerPackFactory.of(emojiPackInfo2.stickerSetCovered, emojiPackInfo2, emojiPackInfo2.set.id == this.selectedPackId));
                }
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 7;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int i = this.totalItems;
            if (i != 1) {
                return i + 1;
            }
            return 2;
        }

        public void search(String str, boolean z) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (this.reqId2 != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId2, true);
                this.reqId2 = 0;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchQuery = null;
                this.localPacks.clear();
                this.emojiStickers.clear();
                this.globalSearchArray = new ArrayList<>();
                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersGridAdapter);
                }
                this.selectedPackId = 0L;
                EmojiView.this.animatorSearchStickerPackSelected.setValue(false, true);
                notifyDataSetChanged();
                EmojiView.this.stickersSearchField.showProgress(false);
            } else {
                this.searchQuery = str.toLowerCase();
                EmojiView.this.stickersSearchField.showProgress(true);
            }
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            AndroidUtilities.runOnUIThread(this.searchRunnable, 300L);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (this.selectedPackId != 0 && i == getItemCount() - 1) {
                return 8;
            }
            if (i == this.foundPacksRow) {
                return 7;
            }
            if (i == 0) {
                return 4;
            }
            if (i == 1 && this.totalItems == 1) {
                return 5;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC.Document) {
                return 0;
            }
            return obj instanceof TLRPC.StickerSetCovered ? 3 : 2;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
            TLRPC.StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0 || EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0) {
                return;
            }
            if (featuredStickerSetInfoCell.isInstalled()) {
                EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                EmojiView.this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
            } else {
                featuredStickerSetInfoCell.setAddDrawProgress(true, true);
                EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                EmojiView.this.delegate.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            View emptyCell;
            View view;
            boolean z = true;
            switch (i) {
                case 0:
                    frameLayout = new StickerEmojiCell(this.context, z, EmojiView.this.resourcesProvider) { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.4
                        @Override // android.widget.FrameLayout, android.view.View
                        public void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), TLObject.FLAG_30));
                        }
                    };
                    view = frameLayout;
                    break;
                case 1:
                    emptyCell = new EmptyCell(this.context);
                    view = emptyCell;
                    break;
                case 2:
                    emptyCell = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider, EmojiView.this.glassDesign);
                    view = emptyCell;
                    break;
                case 3:
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell = new FeaturedStickerSetInfoCell(this.context, 17, false, true, EmojiView.this.resourcesProvider);
                    featuredStickerSetInfoCell.setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            this.f$0.lambda$onCreateViewHolder$0(view2);
                        }
                    });
                    view = featuredStickerSetInfoCell;
                    break;
                case 4:
                    emptyCell = new View(this.context);
                    emptyCell.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    view = emptyCell;
                    break;
                case 5:
                    frameLayout = new FrameLayout(this.context) { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.5
                        @Override // android.widget.FrameLayout, android.view.View
                        public void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec((int) ((((EmojiView.this.stickersGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f), TLObject.FLAG_30));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(R.drawable.stickers_empty);
                    EmojiView emojiView = EmojiView.this;
                    int i2 = Theme.key_chat_emojiPanelEmptyText;
                    imageView.setColorFilter(new PorterDuffColorFilter(emojiView.getThemedColor(i2), PorterDuff.Mode.MULTIPLY));
                    imageView.setTranslationY(-AndroidUtilities.dp(24.0f));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 42.0f, 0.0f, 28.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString(R.string.NoStickersFound));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(EmojiView.this.getThemedColor(i2));
                    frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 42.0f, 0.0f, 9.0f));
                    frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view = frameLayout;
                    break;
                case 6:
                default:
                    view = null;
                    break;
                case 7:
                    view = this.foundPacksListView;
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(79.0f)));
                    break;
                case 8:
                    emptyCell = new View(EmojiView.this.getContext());
                    emptyCell.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(68.0f)));
                    view = emptyCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            z = true;
            if (itemViewType == 0) {
                TLRPC.Document document = (TLRPC.Document) this.cache.get(i);
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                stickerEmojiCell.setSticker(document, null, this.cacheParent.get(i), this.positionToEmoji.get(i), false);
                if (!EmojiView.this.recentStickers.contains(document) && !EmojiView.this.favouriteStickers.contains(document)) {
                    z = false;
                }
                stickerEmojiCell.setRecent(z);
                return;
            }
            Integer numValueOf = null;
            if (itemViewType == 1) {
                EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                if (i == this.totalItems) {
                    int i2 = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                    if (i2 == Integer.MIN_VALUE) {
                        emptyCell.setHeight(1);
                        return;
                    }
                    Object obj = this.rowStartPack.get(i2);
                    if (obj instanceof TLRPC.TL_messages_stickerSet) {
                        numValueOf = Integer.valueOf(((TLRPC.TL_messages_stickerSet) obj).documents.size());
                    } else if (obj instanceof Integer) {
                        numValueOf = (Integer) obj;
                    }
                    if (numValueOf == null) {
                        emptyCell.setHeight(1);
                        return;
                    } else if (numValueOf.intValue() == 0) {
                        emptyCell.setHeight(AndroidUtilities.dp(8.0f));
                        return;
                    } else {
                        int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil(numValueOf.intValue() / EmojiView.this.stickersGridAdapter.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                        emptyCell.setHeight(height > 0 ? height : 1);
                        return;
                    }
                }
                emptyCell.setHeight(AndroidUtilities.dp(82.0f));
                return;
            }
            if (itemViewType == 2) {
                StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                Object obj2 = this.cache.get(i);
                if (obj2 instanceof TLRPC.TL_messages_stickerSet) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) obj2;
                    if (!TextUtils.isEmpty(this.searchQuery) && this.localPacksByShortName.containsKey(tL_messages_stickerSet)) {
                        TLRPC.StickerSet stickerSet = tL_messages_stickerSet.set;
                        if (stickerSet != null) {
                            stickerSetNameCell.setText(stickerSet.title, 0);
                        }
                        stickerSetNameCell.setUrl(tL_messages_stickerSet.set.short_name, this.searchQuery.length());
                        return;
                    }
                    Integer num = this.localPacksByName.get(tL_messages_stickerSet);
                    TLRPC.StickerSet stickerSet2 = tL_messages_stickerSet.set;
                    if (stickerSet2 != null && num != null) {
                        stickerSetNameCell.setText(stickerSet2.title, 0, num.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                    }
                    stickerSetNameCell.setUrl(null, 0);
                    return;
                }
                if (obj2 instanceof String) {
                    stickerSetNameCell.setText((String) obj2, 0);
                    stickerSetNameCell.setUrl(null, 0);
                    return;
                }
                return;
            }
            if (itemViewType != 3) {
                return;
            }
            TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) this.cache.get(i);
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) viewHolder.itemView;
            boolean z2 = EmojiView.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
            char c2 = EmojiView.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) < 0 ? (char) 0 : (char) 1;
            if (z2 || c2 != 0) {
                if (z2 && featuredStickerSetInfoCell.isInstalled()) {
                    EmojiView.this.installingStickerSets.remove(stickerSetCovered.set.id);
                    z2 = false;
                } else if (c2 != 0 && !featuredStickerSetInfoCell.isInstalled()) {
                    EmojiView.this.removingStickerSets.remove(stickerSetCovered.set.id);
                }
            }
            featuredStickerSetInfoCell.setAddDrawProgress(z2, false);
            int iIndexOfIgnoreCase = TextUtils.isEmpty(this.searchQuery) ? -1 : AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.title, this.searchQuery);
            if (iIndexOfIgnoreCase >= 0) {
                featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, false, false, iIndexOfIgnoreCase, this.searchQuery.length());
                return;
            }
            featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, false);
            if (TextUtils.isEmpty(this.searchQuery) || AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.short_name, this.searchQuery) != 0) {
                return;
            }
            featuredStickerSetInfoCell.setUrl(stickerSetCovered.set.short_name, this.searchQuery.length());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            rebuild();
            super.notifyDataSetChanged();
        }

        private void rebuild() {
            int i;
            int i2;
            int i3;
            int i4;
            this.foundPacksRow = -1;
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionToEmoji.clear();
            int i5 = 0;
            this.totalItems = 0;
            int size = this.localPacks.size() + this.localPacksByName.size();
            this.foundPacksListView.adapter.update(false);
            long j = this.selectedPackId;
            String str = _UrlKt.FRAGMENT_ENCODE_SET;
            if (j != 0) {
                ArrayList<TLRPC.Document> arrayList = this.selectedPackStickers;
                SparseArray<Object> sparseArray = this.cache;
                int i6 = this.totalItems;
                this.totalItems = i6 + 1;
                sparseArray.put(i6, "search");
                if (size > 0) {
                    SparseArray<Object> sparseArray2 = this.cache;
                    int i7 = this.totalItems;
                    this.totalItems = i7 + 1;
                    this.foundPacksRow = i7;
                    sparseArray2.put(i7, "packs");
                    SparseArray<Object> sparseArray3 = this.cache;
                    int i8 = this.totalItems;
                    this.totalItems = i8 + 1;
                    sparseArray3.put(i8, LocaleController.formatPluralString("Stickers", this.selectedPackStickerSet.count, new Object[0]));
                    i4 = 3;
                } else {
                    i4 = 1;
                }
                String str2 = this.emojiStickers.get(arrayList);
                if (str2 != null && !_UrlKt.FRAGMENT_ENCODE_SET.equals(str2)) {
                    this.positionToEmoji.put(this.totalItems, str2);
                }
                int size2 = arrayList.size();
                int i9 = 0;
                for (int i10 = 0; i10 < size2; i10++) {
                    int i11 = this.totalItems + i9;
                    int i12 = (i9 / EmojiView.this.stickersGridAdapter.stickersPerRow) + i4;
                    TLRPC.Document document = arrayList.get(i10);
                    this.cache.put(i11, document);
                    TLRPC.TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(document));
                    if (stickerSetById != null) {
                        this.cacheParent.put(i11, stickerSetById);
                    }
                    this.positionToRow.put(i11, i12);
                    i9++;
                }
                int iCeil = (int) Math.ceil(i9 / EmojiView.this.stickersGridAdapter.stickersPerRow);
                while (i5 < iCeil) {
                    this.rowStartPack.put(i4 + i5, Integer.valueOf(i9));
                    i5++;
                }
                this.totalItems += iCeil * EmojiView.this.stickersGridAdapter.stickersPerRow;
                return;
            }
            boolean zIsEmpty = this.emojiArrays.isEmpty();
            ArrayList<TLRPC.Document> arrayList2 = this.globalSearchArray;
            boolean z = (arrayList2 == null || arrayList2.isEmpty()) ? false : true;
            SparseArray<Object> sparseArray4 = this.cache;
            int i13 = this.totalItems;
            this.totalItems = i13 + 1;
            sparseArray4.put(i13, "search");
            if (size > 0) {
                SparseArray<Object> sparseArray5 = this.cache;
                int i14 = this.totalItems;
                this.totalItems = i14 + 1;
                this.foundPacksRow = i14;
                sparseArray5.put(i14, "packs");
                i = 2;
            } else {
                i = 1;
            }
            if (zIsEmpty) {
                i2 = size;
                i3 = 1;
            } else {
                SparseArray<Object> sparseArray6 = this.cache;
                int i15 = this.totalItems;
                this.totalItems = i15 + 1;
                sparseArray6.put(i15, LocaleController.getString(R.string.StickerOrEmojiSearchResult));
                int i16 = i + 1;
                int size3 = this.emojiArrays.size();
                int i17 = 0;
                int i18 = 0;
                while (i17 < size3) {
                    ArrayList<TLRPC.Document> arrayList3 = this.emojiArrays.get(i17);
                    String str3 = this.emojiStickers.get(arrayList3);
                    if (str3 != null && !str.equals(str3)) {
                        this.positionToEmoji.put(this.totalItems + i18, str3);
                        str = str3;
                    }
                    int size4 = arrayList3.size();
                    int i19 = i5;
                    while (i19 < size4) {
                        int i20 = this.totalItems + i18;
                        int i21 = (i18 / EmojiView.this.stickersGridAdapter.stickersPerRow) + i16;
                        TLRPC.Document document2 = arrayList3.get(i19);
                        this.cache.put(i20, document2);
                        int i22 = size;
                        TLRPC.TL_messages_stickerSet stickerSetById2 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(document2));
                        if (stickerSetById2 != null) {
                            this.cacheParent.put(i20, stickerSetById2);
                        }
                        this.positionToRow.put(i20, i21);
                        i18++;
                        i19++;
                        size = i22;
                    }
                    i17++;
                    i5 = 0;
                }
                i2 = size;
                i3 = 1;
                int iCeil2 = (int) Math.ceil(i18 / EmojiView.this.stickersGridAdapter.stickersPerRow);
                for (int i23 = 0; i23 < iCeil2; i23++) {
                    this.rowStartPack.put(i16 + i23, Integer.valueOf(i18));
                }
                this.totalItems += EmojiView.this.stickersGridAdapter.stickersPerRow * iCeil2;
                i = i16 + iCeil2;
            }
            if (z) {
                SparseArray<Object> sparseArray7 = this.cache;
                int i24 = this.totalItems;
                this.totalItems = i24 + 1;
                sparseArray7.put(i24, LocaleController.getString(R.string.StickerOrEmojiGlobalSearchResult));
                int i25 = i + 1;
                String str4 = this.emojiStickers.get(this.globalSearchArray);
                if (str4 != null) {
                    this.positionToEmoji.put(this.totalItems, str4);
                }
                int size5 = this.globalSearchArray.size();
                int i26 = 0;
                for (int i27 = 0; i27 < size5; i27++) {
                    int i28 = this.totalItems + i26;
                    int i29 = (i26 / EmojiView.this.stickersGridAdapter.stickersPerRow) + i25;
                    TLRPC.Document document3 = this.globalSearchArray.get(i27);
                    this.cache.put(i28, document3);
                    TLRPC.TL_messages_stickerSet stickerSetById3 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(document3));
                    if (stickerSetById3 != null) {
                        this.cacheParent.put(i28, stickerSetById3);
                    }
                    this.positionToRow.put(i28, i29);
                    i26++;
                }
                int iCeil3 = (int) Math.ceil(i26 / EmojiView.this.stickersGridAdapter.stickersPerRow);
                for (int i30 = 0; i30 < iCeil3; i30++) {
                    this.rowStartPack.put(i25 + i30, Integer.valueOf(i26));
                }
                this.totalItems += iCeil3 * EmojiView.this.stickersGridAdapter.stickersPerRow;
            }
            if (zIsEmpty && !z && i2 == 0) {
                this.totalItems = i3;
            }
        }
    }

    public void searchProgressChanged() {
        updateStickerTabsPosition();
    }

    public float getStickersExpandOffset() {
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip == null) {
            return 0.0f;
        }
        return scrollSlidingTabStrip.getExpandedOffset();
    }

    public void setShowing(boolean z) {
        this.showing = z;
        updateStickerTabsPosition();
    }

    public void onMessageSend() {
        ChooseStickerActionTracker chooseStickerActionTracker = this.chooseStickerActionTracker;
        if (chooseStickerActionTracker != null) {
            chooseStickerActionTracker.reset();
        }
    }

    public static abstract class ChooseStickerActionTracker {
        private final int currentAccount;
        private final long dialogId;
        private final long threadId;
        boolean typingWasSent;
        boolean visible = false;
        long lastActionTime = -1;

        public abstract boolean isShown();

        public ChooseStickerActionTracker(int i, long j, long j2) {
            this.currentAccount = i;
            this.dialogId = j;
            this.threadId = j2;
        }

        public void doSomeAction() {
            if (this.visible) {
                if (this.lastActionTime == -1) {
                    this.lastActionTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - this.lastActionTime > 2000) {
                    this.typingWasSent = true;
                    this.lastActionTime = System.currentTimeMillis();
                    MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadId, 10, 0);
                }
            }
        }

        public void reset() {
            if (this.typingWasSent) {
                MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadId, 2, 0);
            }
            this.lastActionTime = -1L;
        }

        public void checkVisibility() {
            boolean zIsShown = isShown();
            this.visible = zIsShown;
            if (zIsShown) {
                return;
            }
            reset();
        }
    }

    public class Tab {
        int type;
        View view;

        private Tab() {
        }
    }

    public void freeze(boolean z) {
        StickersGridAdapter stickersGridAdapter;
        boolean z2 = this.frozen;
        this.frozen = z;
        if (!z2 || z) {
            return;
        }
        int i = this.currentPage;
        if (i == 0) {
            EmojiGridAdapter emojiGridAdapter = this.emojiAdapter;
            if (emojiGridAdapter != null) {
                emojiGridAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        if (i == 1) {
            GifAdapter gifAdapter = this.gifAdapter;
            if (gifAdapter != null) {
                gifAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        if (i != 2 || (stickersGridAdapter = this.stickersGridAdapter) == null) {
            return;
        }
        stickersGridAdapter.notifyDataSetChanged();
    }

    public int getGlassIconColor(float f) {
        return ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_glass_defaultIcon, this.resourcesProvider), (int) (f * 255.0f));
    }

    public static class FoundStickerPackFactory extends UItem.UItemFactory<FoundStickerPackCell> {
        private FoundStickerPackFactory() {
        }

        static {
            UItem.UItemFactory.setup(new FoundStickerPackFactory());
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public FoundStickerPackCell createView(Context context, RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
            FoundStickerPackCell foundStickerPackCell = new FoundStickerPackCell(context, resourcesProvider);
            foundStickerPackCell.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(64.0f), -1));
            return foundStickerPackCell;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public void bindView(View view, UItem uItem, boolean z, UniversalAdapter universalAdapter, UniversalRecyclerView universalRecyclerView) {
            FoundStickerPackCell foundStickerPackCell = (FoundStickerPackCell) view;
            Object obj = uItem.object;
            if (obj instanceof TLRPC.TL_messages_stickerSet) {
                foundStickerPackCell.setPack((TLRPC.TL_messages_stickerSet) obj);
            } else if (obj instanceof TLRPC.StickerSetCovered) {
                foundStickerPackCell.setPack((TLRPC.StickerSetCovered) obj, ((EmojiPackInfo) uItem.object2).firstDocument);
            }
            foundStickerPackCell.setSelected(uItem.checked, false);
        }

        public static UItem of(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, boolean z) {
            UItem uItemOfFactory = UItem.ofFactory(FoundStickerPackFactory.class);
            uItemOfFactory.id = Long.hashCode(tL_messages_stickerSet.set.id);
            uItemOfFactory.longValue = tL_messages_stickerSet.set.id;
            uItemOfFactory.object = tL_messages_stickerSet;
            uItemOfFactory.checked = z;
            return uItemOfFactory;
        }

        public static UItem of(TLRPC.StickerSetCovered stickerSetCovered, EmojiPackInfo emojiPackInfo, boolean z) {
            UItem uItemOfFactory = UItem.ofFactory(FoundStickerPackFactory.class);
            uItemOfFactory.id = Long.hashCode(stickerSetCovered.set.id + 1);
            uItemOfFactory.longValue = stickerSetCovered.set.id;
            uItemOfFactory.object = stickerSetCovered;
            uItemOfFactory.object2 = emojiPackInfo;
            uItemOfFactory.checked = z;
            return uItemOfFactory;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean equals(UItem uItem, UItem uItem2) {
            return uItem.longValue == uItem2.longValue;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean contentsEquals(UItem uItem, UItem uItem2) {
            return uItem.longValue == uItem2.longValue && uItem.checked == uItem2.checked;
        }
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 0) {
            checkStickersSearchFieldScroll(false);
            checkStickersSearchFieldVisibility();
            updateBottomTabContainerPosition();
            this.stickersContainer.invalidate();
            return;
        }
        if (i == 1) {
            checkEmojiSearchFieldScroll(false);
            checkEmojiSearchFieldVisibility();
            updateBottomTabContainerPosition();
            this.emojiContainer.invalidate();
        }
    }

    public void setFoundPackButtonText(final FoundStickerPackButton foundStickerPackButton, final TLObject tLObject, final TLRPC.StickerSet stickerSet, final TLRPC.Document document, final boolean z, boolean z2) {
        String pluralString;
        StickersSearchGridAdapter stickersSearchGridAdapter;
        EmojiSearchAdapter emojiSearchAdapter;
        if (stickerSet == null) {
            return;
        }
        if (!z || (emojiSearchAdapter = this.emojiSearchAdapter) == null || emojiSearchAdapter.selectedPackId == stickerSet.id) {
            if (z || (stickersSearchGridAdapter = this.stickersSearchGridAdapter) == null || stickersSearchGridAdapter.selectedPackId == stickerSet.id) {
                final boolean zIsStickerPackInstalled = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickerSet.id);
                boolean z3 = stickerSet.masks;
                if (zIsStickerPackInstalled) {
                    if (z3) {
                        pluralString = LocaleController.formatPluralString("RemoveManyMasksCount", stickerSet.count, new Object[0]);
                    } else {
                        boolean z4 = stickerSet.emojis;
                        int i = stickerSet.count;
                        if (z4) {
                            pluralString = LocaleController.formatPluralString("RemoveManyEmojiCount", i, new Object[0]);
                        } else {
                            pluralString = LocaleController.formatPluralString("RemoveManyStickersCount", i, new Object[0]);
                        }
                    }
                } else if (z3) {
                    pluralString = LocaleController.formatPluralString("AddManyMasksCount", stickerSet.count, new Object[0]);
                } else {
                    boolean z5 = stickerSet.emojis;
                    int i2 = stickerSet.count;
                    if (z5) {
                        pluralString = LocaleController.formatPluralString("AddManyEmojiCount", i2, new Object[0]);
                    } else {
                        pluralString = LocaleController.formatPluralString("AddManyStickersCount", i2, new Object[0]);
                    }
                }
                foundStickerPackButton.setText(pluralString, z2);
                foundStickerPackButton.setIsPrimary(!zIsStickerPackInstalled, z2);
                foundStickerPackButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda36
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$setFoundPackButtonText$33(tLObject, document, zIsStickerPackInstalled, foundStickerPackButton, stickerSet, z, view);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$setFoundPackButtonText$33(final TLObject tLObject, final TLRPC.Document document, boolean z, final FoundStickerPackButton foundStickerPackButton, final TLRPC.StickerSet stickerSet, final boolean z2, View view) {
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), tLObject, document, z ? 0 : 2, this.fragment, this.bulletinContainer2, false, true, new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setFoundPackButtonText$32(foundStickerPackButton, tLObject, stickerSet, document, z2);
            }
        }, false);
        setFoundPackButtonText(foundStickerPackButton, tLObject, stickerSet, document, z2, true);
    }

    public /* synthetic */ void lambda$setFoundPackButtonText$32(FoundStickerPackButton foundStickerPackButton, TLObject tLObject, TLRPC.StickerSet stickerSet, TLRPC.Document document, boolean z) {
        setFoundPackButtonText(foundStickerPackButton, tLObject, stickerSet, document, z, true);
    }
}
