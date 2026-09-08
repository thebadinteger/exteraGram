package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Pair;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.DividerStyle;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.system.VibratorUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.CollapseTextCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.capture.IBlur3Capture;
import org.telegram.ui.Components.blur3.capture.IBlur3Hash;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundProvider;
import org.telegram.ui.FiltersSetupActivity;

public class RecyclerListView extends RecyclerView implements IBlur3Capture {
    public static final int TAG_ROUND_SECTION;
    private static int[] attributes;
    private static boolean gotAttributes;
    private static final Method initializeScrollbars;
    private static final float[] radii;
    private static final Paint sectionBackgroundPaint;
    private static final Path sectionBackgroundPath;
    private static final Paint sectionBackgroundStrokePaint;
    private View.AccessibilityDelegate accessibilityDelegate;
    private boolean accessibilityEnabled;
    private int activeTouches;
    private boolean adaptiveOverScroll;
    private boolean allowItemsInteractionDuringAnimation;
    private boolean allowStopHeaveOperations;
    private boolean animateEmptyView;
    public boolean applyPaddingToSections;
    private Paint backgroundPaint;
    private boolean canCaptureSectionsDecorator;
    private Runnable clickRunnable;
    private final Path clipPath;
    private int currentChildPosition;
    private View currentChildView;
    private int currentFirst;
    int currentSelectedPosition;
    private int currentVisible;
    private boolean disableHighlightState;
    private boolean disallowInterceptTouchEvents;
    private View draggingChild;
    private Utilities.Callback5<Canvas, RectF, Float, Float, Float> drawSectionBackground;
    private boolean drawSelection;
    private boolean drawSelectorBehind;
    private final EdgeEffectTrackerFactory edgeEffectTrackerFactory;
    private View emptyView;
    int emptyViewAnimateToVisibility;
    private int emptyViewAnimationType;
    private FastScroll fastScroll;
    public boolean fastScrollAnimationRunning;
    public ArrayList<Long> forcedSections;
    private GestureDetectorFixDoubleTap gestureDetector;
    private GenericProvider<Integer, Integer> getSelectorColor;
    private ArrayList<View> headers;
    private ArrayList<View> headersCache;
    private boolean hiddenByEmptyView;
    private boolean hideIfEmpty;
    private int highlightPosition;
    private boolean ignoreClipChild;
    private boolean ignoreLayout;
    private boolean instantClick;
    private boolean interceptedByChild;
    private boolean isChildViewEnabled;
    private boolean isHidden;
    private Utilities.CallbackReturn<Integer, Boolean> isViewTypeSection;
    RecyclerItemsEnterAnimator itemsEnterAnimator;
    private long lastAlphaAnimationTime;
    float lastX;
    float lastY;
    int[] listPaddings;
    private boolean longPressCalled;
    boolean multiSelectionGesture;
    boolean multiSelectionGestureStarted;
    onMultiSelectionChanged multiSelectionListener;
    boolean multiselectScrollRunning;
    boolean multiselectScrollToTop;
    private final RecyclerView.AdapterDataObserver observer;
    private OnInterceptTouchListener onInterceptTouchListener;
    private OnItemClickListener onItemClickListener;
    private OnItemClickListenerExtended onItemClickListenerExtended;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemLongClickListenerExtended onItemLongClickListenerExtended;
    private RecyclerView.OnScrollListener onScrollListener;
    private FrameLayout overlayContainer;
    private IntReturnCallback pendingHighlightPosition;
    private View pinnedHeader;
    private float pinnedHeaderShadowAlpha;
    private Drawable pinnedHeaderShadowDrawable;
    private float pinnedHeaderShadowTargetAlpha;
    private Runnable removeHighlighSelectionRunnable;
    private ArrayList<SectionsDrawer.Section> removedSections;
    private boolean resetSelectorOnChanged;
    protected final Theme.ResourcesProvider resourcesProvider;
    private boolean scrollEnabled;
    public boolean scrolledByUserOnce;
    Runnable scroller;
    public boolean scrollingByUser;
    private int sectionOffset;
    private float sectionRadius;
    private float[] sectionRadiusBottom;
    private float[] sectionRadiusTop;
    private ArrayList<SectionsDrawer.Section> sections;
    private SectionsAdapter sectionsAdapter;
    private int sectionsCount;
    private ListSectionsDecoration sectionsItemDecoration;
    private int sectionsType;
    private boolean segmentedSectionsEnabled;
    private Runnable selectChildRunnable;
    HashSet<Integer> selectedPositions;
    protected Drawable selectorDrawable;
    private boolean selectorIsSection;
    protected int selectorPosition;
    private int selectorRadius;
    protected Rect selectorRect;
    private boolean selectorSectionHasNext;
    private boolean selectorSectionHasPrev;
    protected Consumer<Canvas> selectorTransformer;
    private int selectorType;
    protected View selectorView;
    private boolean selfOnLayout;
    private Matrix selfTransformationsMatrix;
    private boolean skipDrawSection;
    private int startSection;
    int startSelectionFrom;
    private boolean stoppedAllHeavyOperations;
    private int topBottomSelectorRadius;
    private int touchSlop;
    private int translateSelector;
    public boolean useLayoutPositionOnClick;
    boolean useRelativePositions;

    public interface HitTestable {
        boolean hasClickableNodeAt(float f, float f2);
    }

    public interface IntReturnCallback {
        int run();
    }

    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnItemClickListenerExtended {
        default boolean hasDoubleTap(View view, int i) {
            return false;
        }

        default void onDoubleTap(View view, int i, float f, float f2) {
        }

        void onItemClick(View view, int i, float f, float f2);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View view, int i);
    }

    public interface OnItemLongClickListenerExtended {
        boolean onItemClick(View view, int i, float f, float f2);

        default void onLongClickRelease() {
        }

        default void onMove(float f, float f2) {
        }
    }

    public static abstract class SelectionAdapter extends RecyclerView.Adapter {
        public int getSelectionBottomPadding(View view) {
            return 0;
        }

        public abstract boolean isEnabled(RecyclerView.ViewHolder viewHolder);
    }

    public interface onMultiSelectionChanged {
        boolean canSelect(int i);

        int checkPosition(int i, boolean z);

        void getPaddings(int[] iArr);

        default int getStartDragDistance() {
            return 0;
        }

        boolean limitReached();

        void onSelectionChanged(int i, boolean z, float f, float f2);

        void scrollBy(int i);
    }

    public boolean allowSelectChildAtPosition(float f, float f2) {
        return true;
    }

    public boolean allowSelectChildAtPosition(View view) {
        return true;
    }

    public boolean canHighlightChildAt(View view, float f, float f2) {
        return true;
    }

    public void emptyViewUpdated(boolean z, boolean z2) {
    }

    public ViewParent getTouchParent() {
        return null;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    static {
        Method declaredMethod;
        try {
            declaredMethod = View.class.getDeclaredMethod("initializeScrollbars", TypedArray.class);
        } catch (Exception unused) {
            declaredMethod = null;
        }
        initializeScrollbars = declaredMethod;
        TAG_ROUND_SECTION = R.id.round_section_tag;
        sectionBackgroundPaint = new Paint(1);
        sectionBackgroundStrokePaint = new Paint(1);
        sectionBackgroundPath = new Path();
        radii = new float[8];
    }

    public void setSelectorTransformer(Consumer<Canvas> consumer) {
        this.selectorTransformer = consumer;
    }

    public FastScroll getFastScroll() {
        return this.fastScroll;
    }

    public static abstract class FastScrollAdapter extends SelectionAdapter {
        public boolean fastScrollIsVisible(RecyclerListView recyclerListView) {
            return true;
        }

        public abstract String getLetter(int i);

        public abstract void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr);

        public void onFastScrollSingleTap() {
        }

        public void onFinishFastScroll(RecyclerListView recyclerListView) {
        }

        public void onStartFastScroll() {
        }

        public int getTotalItemsCount() {
            return getItemCount();
        }

        public float getScrollProgress(RecyclerListView recyclerListView) {
            return recyclerListView.computeVerticalScrollOffset() / ((getTotalItemsCount() * recyclerListView.getChildAt(0).getMeasuredHeight()) - recyclerListView.getMeasuredHeight());
        }
    }

    public static abstract class SectionsAdapter extends FastScrollAdapter {
        private int count;
        private ArrayList<Integer> hashes = new ArrayList<>();
        private SparseIntArray sectionCache;
        private int sectionCount;
        private SparseIntArray sectionCountCache;
        private SparseIntArray sectionPositionCache;

        public abstract int getCountForSection(int i);

        public abstract Object getItem(int i, int i2);

        public abstract int getItemViewType(int i, int i2);

        public abstract int getSectionCount();

        public abstract View getSectionHeaderView(int i, View view);

        public abstract boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2);

        public abstract void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder);

        public void cleanupCache() {
            SparseIntArray sparseIntArray = this.sectionCache;
            if (sparseIntArray == null) {
                this.sectionCache = new SparseIntArray();
                this.sectionPositionCache = new SparseIntArray();
                this.sectionCountCache = new SparseIntArray();
            } else {
                sparseIntArray.clear();
                this.sectionPositionCache.clear();
                this.sectionCountCache.clear();
            }
            this.count = -1;
            this.sectionCount = -1;
        }

        public void notifySectionsChanged() {
            cleanupCache();
        }

        public SectionsAdapter() {
            cleanupCache();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            update(false);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return isEnabled(viewHolder, getSectionForPosition(adapterPosition), getPositionInSectionForPosition(adapterPosition));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int i = this.count;
            if (i >= 0) {
                return i;
            }
            int i2 = 0;
            this.count = 0;
            int iInternalGetSectionCount = internalGetSectionCount();
            while (true) {
                int i3 = this.count;
                if (i2 >= iInternalGetSectionCount) {
                    return i3;
                }
                this.count = i3 + internalGetCountForSection(i2);
                i2++;
            }
        }

        public final Object getItem(int i) {
            return getItem(getSectionForPosition(i), getPositionInSectionForPosition(i));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public final int getItemViewType(int i) {
            return getItemViewType(getSectionForPosition(i), getPositionInSectionForPosition(i));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public final void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            onBindViewHolder(getSectionForPosition(i), getPositionInSectionForPosition(i), viewHolder);
        }

        private int internalGetCountForSection(int i) {
            int i2 = this.sectionCountCache.get(i, Integer.MAX_VALUE);
            if (i2 != Integer.MAX_VALUE) {
                return i2;
            }
            int countForSection = getCountForSection(i);
            this.sectionCountCache.put(i, countForSection);
            return countForSection;
        }

        private int internalGetSectionCount() {
            int i = this.sectionCount;
            if (i >= 0) {
                return i;
            }
            int sectionCount = getSectionCount();
            this.sectionCount = sectionCount;
            return sectionCount;
        }

        public final int getSectionForPosition(int i) {
            int i2 = this.sectionCache.get(i, Integer.MAX_VALUE);
            if (i2 != Integer.MAX_VALUE) {
                return i2;
            }
            int iInternalGetSectionCount = internalGetSectionCount();
            int i3 = 0;
            int i4 = 0;
            while (i3 < iInternalGetSectionCount) {
                int iInternalGetCountForSection = internalGetCountForSection(i3) + i4;
                if (i >= i4 && i < iInternalGetCountForSection) {
                    this.sectionCache.put(i, i3);
                    return i3;
                }
                i3++;
                i4 = iInternalGetCountForSection;
            }
            return -1;
        }

        public int getPositionInSectionForPosition(int i) {
            int i2 = this.sectionPositionCache.get(i, Integer.MAX_VALUE);
            if (i2 != Integer.MAX_VALUE) {
                return i2;
            }
            int iInternalGetSectionCount = internalGetSectionCount();
            int i3 = 0;
            int i4 = 0;
            while (i3 < iInternalGetSectionCount) {
                int iInternalGetCountForSection = internalGetCountForSection(i3) + i4;
                if (i >= i4 && i < iInternalGetCountForSection) {
                    int i5 = i - i4;
                    this.sectionPositionCache.put(i, i5);
                    return i5;
                }
                i3++;
                i4 = iInternalGetCountForSection;
            }
            return -1;
        }

        public void update(boolean z) {
            final ArrayList arrayList = new ArrayList(this.hashes);
            updateHashes();
            if (z) {
                DiffUtil.calculateDiff(new DiffUtil.Callback() { // from class: org.telegram.ui.Components.RecyclerListView.SectionsAdapter.1
                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public int getOldListSize() {
                        return arrayList.size();
                    }

                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public int getNewListSize() {
                        return SectionsAdapter.this.hashes.size();
                    }

                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public boolean areItemsTheSame(int i, int i2) {
                        return Objects.equals(arrayList.get(i), SectionsAdapter.this.hashes.get(i2));
                    }

                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public boolean areContentsTheSame(int i, int i2) {
                        return areItemsTheSame(i, i2);
                    }
                }, true).dispatchUpdatesTo(this);
            } else {
                super.notifyDataSetChanged();
            }
        }

        public void updateHashes() {
            cleanupCache();
            this.hashes.clear();
            int iInternalGetSectionCount = internalGetSectionCount();
            for (int i = 0; i < iInternalGetSectionCount; i++) {
                int iInternalGetCountForSection = internalGetCountForSection(i);
                for (int i2 = 0; i2 < iInternalGetCountForSection; i2++) {
                    this.hashes.add(Integer.valueOf(getHash(i, i2)));
                }
            }
        }

        public int getHash(int i, int i2) {
            return Objects.hash(Integer.valueOf((-49612) * i), getItem(i, i2));
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View view) {
            super(view);
            if (ExteraConfig.getInAppVibration()) {
                return;
            }
            VibratorUtils.disableHapticFeedback(view);
        }
    }

    public class FastScroll extends View {
        private int activeColor;
        private Path arrowPath;
        BlurredBackgroundDrawable blurredCircleDrawable;
        BlurredBackgroundDrawable blurredTagDrawable;
        private float bubbleProgress;
        private String currentLetter;
        Drawable fastScrollBackgroundDrawable;
        Drawable fastScrollShadowDrawable;
        private float floatingDateProgress;
        private boolean floatingDateVisible;
        private boolean fromTop;
        private float fromWidth;
        Runnable hideFloatingDateRunnable;
        private StaticLayout inLetterLayout;
        private int inactiveColor;
        boolean isMoving;
        boolean isRtl;
        public boolean isVisible;
        private float lastLetterY;
        private long lastUpdateTime;
        private float lastY;
        private StaticLayout letterLayout;
        private TextPaint letterPaint;
        private StaticLayout oldLetterLayout;
        private StaticLayout outLetterLayout;
        private Paint paint;
        private Paint paint2;
        private Path path;
        private int[] positionWithOffset;
        private boolean pressed;
        private float progress;
        private float[] radii;
        private RectF rect;
        private float replaceLayoutProgress;
        private int scrollX;
        private StaticLayout stableLetterLayout;
        private float startDy;
        long startTime;
        float startY;
        private float textX;
        private float textY;
        public int topOffset;
        float touchSlop;
        private int type;
        public boolean usePadding;
        float viewAlpha;
        float visibilityAlpha;

        public FastScroll(Context context, int i) {
            super(context);
            this.usePadding = true;
            this.rect = new RectF();
            this.paint = new Paint(1);
            this.paint2 = new Paint(1);
            this.replaceLayoutProgress = 1.0f;
            this.letterPaint = new TextPaint(1);
            this.path = new Path();
            this.arrowPath = new Path();
            this.radii = new float[8];
            this.positionWithOffset = new int[2];
            this.hideFloatingDateRunnable = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView.FastScroll.1
                @Override // java.lang.Runnable
                public void run() {
                    boolean z = FastScroll.this.pressed;
                    FastScroll fastScroll = FastScroll.this;
                    if (z) {
                        AndroidUtilities.cancelRunOnUIThread(fastScroll.hideFloatingDateRunnable);
                        AndroidUtilities.runOnUIThread(FastScroll.this.hideFloatingDateRunnable, 4000L);
                    } else {
                        fastScroll.floatingDateVisible = false;
                        FastScroll.this.invalidate();
                    }
                }
            };
            this.viewAlpha = 1.0f;
            this.type = i;
            if (i == 0) {
                this.letterPaint.setTextSize(AndroidUtilities.dp(45.0f));
                this.isRtl = LocaleController.isRTL;
            } else {
                this.isRtl = false;
                this.letterPaint.setTextSize(AndroidUtilities.dp(13.0f));
                this.letterPaint.setTypeface(AndroidUtilities.bold());
                Paint paint = this.paint2;
                int i2 = Theme.key_windowBackgroundWhite;
                paint.setColor(Theme.getColor(i2, RecyclerListView.this.resourcesProvider));
                Drawable drawableMutate = ContextCompat.getDrawable(context, R.drawable.calendar_date).mutate();
                this.fastScrollBackgroundDrawable = drawableMutate;
                drawableMutate.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(Theme.getColor(i2, RecyclerListView.this.resourcesProvider), -1, 0.1f), PorterDuff.Mode.MULTIPLY));
            }
            for (int i3 = 0; i3 < 8; i3++) {
                this.radii[i3] = AndroidUtilities.dp(44.0f);
            }
            this.scrollX = AndroidUtilities.dp(this.isRtl ? 10.0f : (i == 0 ? 132 : 240) - 15);
            if (RecyclerListView.this.hasSections()) {
                this.scrollX += AndroidUtilities.dp(this.isRtl ? -4.0f : 6.0f);
            }
            updateColors();
            setFocusableInTouchMode(true);
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.fastScrollShadowDrawable = ContextCompat.getDrawable(context, R.drawable.fast_scroll_shadow);
        }

        public void updateColors() {
            this.inactiveColor = this.type == 0 ? Theme.getColor(Theme.key_fastScrollInactive, RecyclerListView.this.resourcesProvider) : ColorUtils.setAlphaComponent(-16777216, 102);
            this.activeColor = Theme.getColor(Theme.key_fastScrollActive, RecyclerListView.this.resourcesProvider);
            this.paint.setColor(this.inactiveColor);
            int i = this.type;
            TextPaint textPaint = this.letterPaint;
            if (i == 0) {
                textPaint.setColor(Theme.getColor(Theme.key_fastScrollText, RecyclerListView.this.resourcesProvider));
            } else {
                textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, RecyclerListView.this.resourcesProvider));
            }
            invalidate();
        }

        @Override // android.view.View
        Boolean $r8$lambda$2wEbk7S82O_026x6oum7LynZ9ss(RecyclerListView recyclerListView, Utilities.CallbackReturn callbackReturn, SparseIntArray sparseIntArray, View view) {
        try {
            if (view.getParent() != recyclerListView) {
                return Boolean.FALSE;
            }
            Boolean bool = (Boolean) callbackReturn.run(view);
            boolean zBooleanValue = bool.booleanValue();
            RecyclerView.ViewHolder childViewHolder = recyclerListView.getChildViewHolder(view);
            if (childViewHolder != null) {
                sparseIntArray.put(childViewHolder.getItemViewType(), zBooleanValue ? 1 : 0);
            }
            return bool;
        } catch (Exception unused) {
            return Boolean.FALSE;
        }
    }

    public static void lambda$drawSectionsBackgrounds$6(Canvas canvas, Float f, Float f2, Float f3, Float f4, Float f5) {
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(this.sectionsItemDecoration.padding, f.floatValue(), getWidth() - this.sectionsItemDecoration.padding, f2.floatValue());
        this.drawSectionBackground.run(canvas, rectF, f3, f4, f5);
    }

    public /* synthetic */ void lambda$drawSectionsBackgrounds$7(Canvas canvas, Float f, Float f2, Float f3, Float f4, Float f5) {
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(getPaddingLeft() + this.sectionsItemDecoration.padding, f.floatValue(), (getWidth() - this.sectionsItemDecoration.padding) - getPaddingRight(), f2.floatValue());
        this.drawSectionBackground.run(canvas, rectF, f3, f4, f5);
    }

    public static void drawBackgroundRect(Canvas canvas, RectF rectF, float f, float f2, float f3, Theme.ResourcesProvider resourcesProvider) {
        if (SharedConfig.shadowsInSections) {
            Paint paint = sectionBackgroundStrokePaint;
            paint.setShadowLayer(AndroidUtilities.dpf2(0.33f), 0.0f, 0.0f, Theme.multAlpha(201326592, f3));
            paint.setColor(0);
            sectionBackgroundPaint.setShadowLayer(AndroidUtilities.dpf2(2.0f), 0.0f, AndroidUtilities.dpf2(0.33f), Theme.multAlpha(167772160, f3));
        } else {
            sectionBackgroundPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        }
        Paint paint2 = sectionBackgroundPaint;
        paint2.setColor(Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider), f3));
        if (f == f2) {
            if (SharedConfig.shadowsInSections) {
                canvas.drawRoundRect(rectF, f, f, sectionBackgroundStrokePaint);
            }
            canvas.drawRoundRect(rectF, f, f, paint2);
            return;
        }
        Path path = sectionBackgroundPath;
        path.rewind();
        float[] fArr = radii;
        fArr[3] = f;
        fArr[2] = f;
        fArr[1] = f;
        fArr[0] = f;
        fArr[7] = f2;
        fArr[6] = f2;
        fArr[5] = f2;
        fArr[4] = f2;
        path.addRoundRect(rectF, fArr, Path.Direction.CW);
        if (SharedConfig.shadowsInSections) {
            canvas.drawPath(path, sectionBackgroundStrokePaint);
        }
        canvas.drawPath(path, paint2);
    }

    public void drawBackgroundRect(Canvas canvas, RectF rectF, float f, float f2, float f3) {
        drawBackgroundRect(canvas, rectF, f, f2, f3, this.resourcesProvider);
    }

    private void clipChild(Canvas canvas, View view) {
        boolean z;
        if (view == null || !this.sectionsItemDecoration.isSectionItem.run(view).booleanValue()) {
            return;
        }
        int childAdapterPosition = getChildAdapterPosition(view);
        boolean z2 = false;
        if (childAdapterPosition == -1) {
            z = false;
        } else {
            View viewFindViewByPosition = findViewByPosition(childAdapterPosition - 1);
            View viewFindViewByPosition2 = findViewByPosition(childAdapterPosition + 1);
            boolean z3 = viewFindViewByPosition != null && this.sectionsItemDecoration.isSectionItem.run(viewFindViewByPosition).booleanValue();
            if (viewFindViewByPosition2 != null && this.sectionsItemDecoration.isSectionItem.run(viewFindViewByPosition2).booleanValue()) {
                z2 = true;
            }
            z = z2;
            z2 = z3;
        }
        clipChildWithSection(canvas, view, z2, z);
    }

    private void clipSelector(Canvas canvas, View view) {
        if (view == null || !this.selectorIsSection) {
            return;
        }
        if (useSegmentedSections()) {
            clipChildWithSegmentedSection(canvas, view);
        } else {
            clipChildWithSection(canvas, view, this.selectorSectionHasPrev, this.selectorSectionHasNext);
        }
    }

    private void clipChildWithSegmentedSection(Canvas canvas, View view) {
        RectF rectF = AndroidUtilities.rectTmp;
        if (setSegmentedSectionRect(view, rectF)) {
            this.clipPath.rewind();
            float[] fArr = radii;
            float segmentedSectionTopRadius = getSegmentedSectionTopRadius(view, rectF);
            fArr[3] = segmentedSectionTopRadius;
            fArr[2] = segmentedSectionTopRadius;
            fArr[1] = segmentedSectionTopRadius;
            fArr[0] = segmentedSectionTopRadius;
            float segmentedSectionBottomRadius = getSegmentedSectionBottomRadius(view, rectF);
            fArr[7] = segmentedSectionBottomRadius;
            fArr[6] = segmentedSectionBottomRadius;
            fArr[5] = segmentedSectionBottomRadius;
            fArr[4] = segmentedSectionBottomRadius;
            this.clipPath.addRoundRect(rectF, fArr, Path.Direction.CW);
            canvas.clipPath(this.clipPath);
        }
    }

    private void clipChildWithSection(Canvas canvas, View view, boolean z, boolean z2) {
        if (useSegmentedSections()) {
            clipChildWithSegmentedSection(canvas, view);
            return;
        }
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(view.getX(), Math.max(this.applyPaddingToSections ? getPaddingTop() : -this.sectionRadius, top(view)), view.getX() + view.getWidth(), Math.min(getHeight() - (this.applyPaddingToSections ? getPaddingBottom() : -this.sectionRadius), bottom(view)));
        if (z && z2) {
            z = top(view) >= rectF.top;
            z2 = bottom(view) <= rectF.bottom;
            if (z && z2) {
                return;
            }
        }
        if (!z && !z2) {
            this.clipPath.rewind();
            float singleSectionRadius = isRoundSectionView(view) ? getSingleSectionRadius(rectF) : this.sectionRadius;
            this.clipPath.addRoundRect(rectF, singleSectionRadius, singleSectionRadius, Path.Direction.CW);
            canvas.clipPath(this.clipPath);
            return;
        }
        if (!z) {
            this.clipPath.rewind();
            this.clipPath.addRoundRect(rectF, this.sectionRadiusTop, Path.Direction.CW);
            canvas.clipPath(this.clipPath);
        } else {
            if (z2) {
                return;
            }
            this.clipPath.rewind();
            this.clipPath.addRoundRect(rectF, this.sectionRadiusBottom, Path.Direction.CW);
            canvas.clipPath(this.clipPath);
        }
    }

    public static float top(View view) {
        if (view.getTag(R.id.dragging) != null) {
            return view.getTop();
        }
        return view.getY();
    }

    public static float bottom(View view) {
        if (view.getTag(R.id.dragging) != null) {
            return view.getBottom();
        }
        return view.getY() + view.getHeight();
    }

    public Drawable getClipBackground(View view) {
        return getClipBackground(view, false);
    }

    public Drawable getClipBackground(View view, boolean z) {
        boolean z2;
        boolean z3;
        if (view.getParent() != this || !hasSections() || !this.sectionsItemDecoration.isSectionItem.run(view).booleanValue()) {
            return null;
        }
        if (useSegmentedSections()) {
            RectF rectF = new RectF();
            if (!setSegmentedSectionRect(view, rectF)) {
                return null;
            }
            Path path = new Path();
            float segmentedSectionTopRadius = getSegmentedSectionTopRadius(view, rectF);
            float segmentedSectionBottomRadius = getSegmentedSectionBottomRadius(view, rectF);
            float[] fArr = radii;
            fArr[3] = segmentedSectionTopRadius;
            fArr[2] = segmentedSectionTopRadius;
            fArr[1] = segmentedSectionTopRadius;
            fArr[0] = segmentedSectionTopRadius;
            fArr[7] = segmentedSectionBottomRadius;
            fArr[6] = segmentedSectionBottomRadius;
            fArr[5] = segmentedSectionBottomRadius;
            fArr[4] = segmentedSectionBottomRadius;
            path.addRoundRect(rectF, fArr, Path.Direction.CW);
            return createClipBackgroundDrawable(view, rectF, path);
        }
        int childAdapterPosition = getChildAdapterPosition(view);
        if (childAdapterPosition == -1) {
            z3 = false;
            z2 = false;
        } else {
            View viewFindViewByPosition = findViewByPosition(childAdapterPosition - 1);
            View viewFindViewByPosition2 = findViewByPosition(childAdapterPosition + 1);
            z2 = viewFindViewByPosition != null && this.sectionsItemDecoration.isSectionItem.run(viewFindViewByPosition).booleanValue();
            z3 = viewFindViewByPosition2 != null && this.sectionsItemDecoration.isSectionItem.run(viewFindViewByPosition2).booleanValue();
        }
        RectF rectF2 = new RectF();
        rectF2.set(view.getX(), Math.max(this.applyPaddingToSections ? getPaddingTop() : 0.0f, top(view)), view.getX() + view.getWidth(), Math.min(getHeight() - (this.applyPaddingToSections ? getPaddingBottom() : 0), bottom(view)));
        if (z2 && z3 && !z) {
            z2 = top(view) >= rectF2.top;
            boolean z4 = bottom(view) <= rectF2.bottom;
            if (z2 && z4) {
                return Theme.createRoundRectDrawable(0, Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider));
            }
            z3 = z4;
        }
        Path path2 = new Path();
        if ((!z2 && !z3) || z) {
            path2.rewind();
            float singleSectionRadius = isRoundSectionView(view) ? getSingleSectionRadius(rectF2) : this.sectionRadius;
            path2.addRoundRect(rectF2, singleSectionRadius, singleSectionRadius, Path.Direction.CW);
        } else if (!z2) {
            path2.rewind();
            path2.addRoundRect(rectF2, this.sectionRadiusTop, Path.Direction.CW);
        } else if (!z3) {
            path2.rewind();
            path2.addRoundRect(rectF2, this.sectionRadiusBottom, Path.Direction.CW);
        }
        return createClipBackgroundDrawable(view, rectF2, path2);
    }

    private Drawable createClipBackgroundDrawable(final View view, final RectF rectF, final Path path) {
        return new Drawable() { // from class: org.telegram.ui.Components.RecyclerListView.7
            private final Paint paint = new Paint(1);

            @Override // android.graphics.drawable.Drawable
            public int getOpacity() {
                return -2;
            }

            @Override // android.graphics.drawable.Drawable
            public void setColorFilter(ColorFilter colorFilter) {
            }

            @Override // android.graphics.drawable.Drawable
            public void draw(Canvas canvas) {
                canvas.save();
                canvas.translate(-view.getX(), -view.getY());
                canvas.clipPath(path);
                this.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite, RecyclerListView.this.resourcesProvider), this.paint.getAlpha()));
                canvas.drawRect(rectF, this.paint);
                canvas.restore();
            }

            @Override // android.graphics.drawable.Drawable
            public void setAlpha(int i) {
                this.paint.setAlpha(i);
            }
        };
    }

    public static class SectionsDrawer {
        private static final ArrayList<float[]> groups = new ArrayList<>();

        public static class Section {
            public float alpha;
            public float from;
            public boolean round;
            public float to;

            public Section(float f, float f2, float f3, boolean z) {
                this.from = f;
                this.to = f2;
                this.alpha = f3;
                this.round = z;
            }
        }

        public static void draw(List<Section> list, float f, Utilities.Callback5<Float, Float, Float, Float, Float> callback5, boolean z) {
            int i;
            if (list == null || list.isEmpty()) {
                return;
            }
            Collections.sort(list, new Comparator() { // from class: org.telegram.ui.Components.RecyclerListView$SectionsDrawer$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return Float.compare(((RecyclerListView.SectionsDrawer.Section) obj).from, ((RecyclerListView.SectionsDrawer.Section) obj2).from);
                }
            });
            groups.clear();
            if (z) {
                drawSegmentedSections(list, f, callback5);
                return;
            }
            for (int i2 = 0; i2 < list.size(); i2 = i) {
                float fMax = list.get(i2).to;
                i = i2 + 1;
                while (i < list.size() && list.get(i).from <= 1.5f + fMax) {
                    fMax = Math.max(fMax, list.get(i).to);
                    i++;
                }
                float[] fArrCalculateGroup = calculateGroup(list, i2, i, f);
                if (fArrCalculateGroup != null) {
                    groups.add(fArrCalculateGroup);
                }
            }
            int i3 = 0;
            while (true) {
                ArrayList<float[]> arrayList = groups;
                if (i3 >= arrayList.size()) {
                    return;
                }
                float[] fArr = arrayList.get(i3);
                float f2 = fArr[0];
                float f3 = fArr[1];
                float fMin = fArr[2];
                float fMin2 = fArr[3];
                float f4 = fArr[4];
                if (i3 > 0) {
                    float f5 = f2 - arrayList.get(i3 - 1)[1];
                    float f6 = f * 0.2f;
                    if (f5 < f6) {
                        fMin = Math.min(fMin, (f5 / f6) * f);
                    }
                }
                if (i3 < arrayList.size() - 1) {
                    float f7 = arrayList.get(i3 + 1)[0] - f3;
                    float f8 = f * 0.2f;
                    if (f7 < f8) {
                        fMin2 = Math.min(fMin2, (f7 / f8) * f);
                    }
                }
                callback5.run(Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(fMin), Float.valueOf(fMin2), Float.valueOf(f4));
                i3++;
            }
        }

        private static void drawSegmentedSections(List<Section> list, float f, Utilities.Callback5<Float, Float, Float, Float, Float> callback5) {
            float fMin = Math.min(f, AndroidUtilities.dp(4.0f));
            float fDp = AndroidUtilities.dp(2.0f) + 1.5f;
            int i = 0;
            while (i < list.size()) {
                Section section = list.get(i);
                if (section.alpha >= 0.001f) {
                    boolean z = i > 0 && section.from <= list.get(i + (-1)).to + fDp;
                    boolean z2 = i < list.size() - 1 && list.get(i + 1).from <= section.to + fDp;
                    float f2 = section.from;
                    float f3 = section.to;
                    if (f3 > f2) {
                        float f4 = z ? fMin : f;
                        float f5 = z2 ? fMin : f;
                        if (!z && !z2 && section.round) {
                            f4 = (f3 - f2) / 2.0f;
                            f5 = f4;
                        }
                        callback5.run(Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4), Float.valueOf(f5), Float.valueOf(section.alpha));
                    }
                }
                i++;
            }
        }

        /* JADX WARN: Code duplicated, block: B:47:0x00d5  */
        private static float[] calculateGroup(List<Section> list, int i, int i2, float f) {
            float f2;
            float fLerp;
            float f3;
            float fLerp2 = f;
            float fMin = Float.MAX_VALUE;
            float fMax = Float.MIN_VALUE;
            int i3 = i;
            float fMin2 = Float.MAX_VALUE;
            float fMax2 = Float.MIN_VALUE;
            while (true) {
                f2 = 0.99f;
                if (i3 >= i2) {
                    break;
                }
                Section section = list.get(i3);
                if (section.alpha >= 0.99f) {
                    fMin2 = Math.min(fMin2, section.from);
                    fMax2 = Math.max(fMax2, section.to);
                }
                i3++;
            }
            boolean z = fMin2 != Float.MAX_VALUE;
            float f4 = 0.0f;
            float fMax3 = 0.0f;
            for (int i4 = i; i4 < i2; i4++) {
                Section section2 = list.get(i4);
                fMin = Math.min(fMin, section2.from);
                fMax = Math.max(fMax, section2.to);
                fMax3 = Math.max(fMax3, section2.alpha);
            }
            if (fMax3 < 0.001f) {
                return null;
            }
            if (z) {
                int i5 = i;
                float f5 = 0.0f;
                Section section3 = null;
                while (i5 < i2) {
                    Section section4 = list.get(i5);
                    float f6 = section4.alpha;
                    if (f6 >= f2) {
                        f3 = f2;
                    } else {
                        f3 = f2;
                        float f7 = section4.from;
                        if (f7 < fMin2) {
                            float f8 = (fMin2 - f7) * f6;
                            if (f8 > f5) {
                                f5 = f8;
                                section3 = section4;
                            }
                        }
                    }
                    i5++;
                    f2 = f3;
                }
                float f9 = f2;
                Section section5 = null;
                for (int i6 = i; i6 < i2; i6++) {
                    Section section6 = list.get(i6);
                    float f10 = section6.alpha;
                    if (f10 < f9) {
                        float f11 = section6.to;
                        if (f11 > fMax2) {
                            float f12 = (f11 - fMax2) * f10;
                            if (f12 > f4) {
                                section5 = section6;
                                f4 = f12;
                            }
                        }
                    }
                }
                if (section3 != null) {
                    float f13 = section3.alpha;
                    if (f13 > 0.001f) {
                        float fLerp3 = AndroidUtilities.lerp(fMin2, section3.from, f13);
                        fLerp = AndroidUtilities.lerp(fLerp2, section3.alpha * fLerp2, (fMin2 - fLerp3) / ((fMin2 - section3.from) + 0.001f));
                        fMin2 = fLerp3;
                    } else {
                        fLerp = fLerp2;
                    }
                } else {
                    fLerp = fLerp2;
                }
                fMax3 = 1.0f;
                if (section5 != null) {
                    float f14 = section5.alpha;
                    if (f14 > 0.001f) {
                        float fLerp4 = AndroidUtilities.lerp(fMax2, section5.to, f14);
                        fLerp2 = AndroidUtilities.lerp(fLerp2, section5.alpha * fLerp2, (fLerp4 - fMax2) / ((section5.to - fMax2) + 0.001f));
                        fMax2 = fLerp4;
                    }
                }
                fMin = fMin2;
            } else {
                fLerp = fLerp2;
                fMax2 = fMax;
            }
            if (fMax2 <= fMin) {
                return null;
            }
            if (i2 - i == 1 && list.get(i).round) {
                fLerp = (fMax2 - fMin) / 2.0f;
                fLerp2 = fLerp;
            }
            return new float[]{fMin, fMax2, fLerp, fLerp2, fMax3};
        }
    }

    public static Class[] filterThemeDescription(int i, Class[] clsArr) {
        if (ExteraConfig.getSectionsSeparatedHeaders() && i == ThemeDescription.FLAG_CELLBACKGROUNDCOLOR && clsArr != null) {
            int i2 = 0;
            for (Class cls : clsArr) {
                if (cls != null && !cls.equals(HeaderCell.class)) {
                    i2++;
                }
            }
            if (i2 != clsArr.length) {
                Class[] clsArr2 = new Class[i2];
                int i3 = 0;
                for (Class cls2 : clsArr) {
                    if (cls2 != null && !cls2.equals(HeaderCell.class)) {
                        clsArr2[i3] = cls2;
                        i3++;
                    }
                }
                return clsArr2;
            }
        }
        return clsArr;
    }
}
