package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.AvatarCornerType;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.components.ActionRow;
import com.exteragram.messenger.utils.chats.GlassMenuHelper;
import java.util.ArrayList;
import java.util.HashMap;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.PopupSwipeBackLayout;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;

public class ActionBarMenuItem extends FrameLayout {
    private int additionalXOffset;
    private int additionalYOffset;
    private boolean allowCloseAnimation;
    private boolean animateClear;
    private boolean animationEnabled;
    private boolean centerTitleVisibleForPartialAlpha;
    private ImageView clearButton;
    private AnimatorSet clearButtonAnimator;
    private ArrayList<FiltersView.MediaFilterData> currentSearchFilters;
    private ActionBarMenuItemDelegate delegate;
    private float dimMenu;
    private boolean fixBackground;
    private boolean forceSmoothKeyboard;
    protected RLottieImageView iconView;
    private int iconViewResId;
    private boolean ignoreOnTextChange;
    private boolean isSearchField;
    private boolean layoutInScreen;
    private ArrayList<Item> lazyList;
    private HashMap<Integer, Item> lazyMap;
    protected ActionBarMenuItemSearchListener listener;
    private int[] location;
    private boolean longClickEnabled;
    private boolean measurePopup;
    private final AnimationNotificationsLocker notificationsLocker;
    private View.OnClickListener onClickListener;
    protected boolean overrideMenuClick;
    private ActionBarMenu parentMenu;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private ActionBarPopupWindow popupWindow;
    private boolean processedPopupClick;
    private CloseProgressDrawable2 progressDrawable;
    private Rect rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private View searchAdditionalButton;
    private FrameLayout searchContainer;
    AnimatorSet searchContainerAnimator;
    private EditTextBoldCursor searchField;
    private TextView searchFieldCaption;
    private CharSequence searchFieldHint;
    private CharSequence searchFieldText;
    private LinearLayout searchFilterLayout;
    private ArrayList<SearchFilterView> searchFilterViews;
    public int searchItemPaddingStart;
    public int searchRightMargin;
    private int selectedFilterIndex;
    private View selectedMenuView;
    private Runnable showMenuRunnable;
    private View showSubMenuFrom;
    private boolean showSubmenuByMove;
    private ActionBarSubMenuItemDelegate subMenuDelegate;
    private int subMenuOpenSide;
    protected TextView textView;
    private float transitionOffset;
    private boolean wrapSearchInScrollView;
    private FrameLayout wrappedSearchFrameLayout;
    private int xOffset;
    private int yOffset;

    public interface ActionBarMenuItemDelegate {
        void onItemClick(int i);
    }

    public static class ActionBarMenuItemSearchListener {
        public boolean canClearCaption() {
            return true;
        }

        public boolean canCollapseSearch() {
            return true;
        }

        public boolean canToggleSearch() {
            return true;
        }

        public boolean forceShowClear() {
            return false;
        }

        public Animator getCustomToggleTransition() {
            return null;
        }

        public void onCaptionCleared() {
        }

        public void onLayout(int i, int i2, int i3, int i4) {
        }

        public void onPreToggleSearch() {
        }

        public void onSearchCollapse() {
        }

        public void onSearchExpand() {
        }

        public void onSearchFilterCleared(FiltersView.MediaFilterData mediaFilterData) {
        }

        public void onSearchPressed(EditText editText) {
        }

        public void onTextChanged(EditText editText) {
        }

        public boolean showClearForCaption() {
            return true;
        }
    }

    public interface ActionBarSubMenuItemDelegate {
        void onHideSubMenu();

        void onShowSubMenu();
    }

    public void onDismiss() {
    }

    public static void addText(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, String str, Theme.ResourcesProvider resourcesProvider) {
        TextView textView = new TextView(actionBarPopupWindowLayout.getContext());
        textView.setTextSize(1, 13.0f);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        textView.setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
        textView.setText(str);
        textView.setTag(R.id.fit_width_tag, 1);
        textView.setMaxWidth(AndroidUtilities.dp(200.0f));
        actionBarPopupWindowLayout.addView((View) textView, LayoutHelper.createLinear(-1, -2));
    }

    public void setSearchPaddingStart(int i) {
        this.searchItemPaddingStart = i;
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null) {
            ((ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams()).leftMargin = AndroidUtilities.dp(i);
            this.searchContainer.setClipChildren(this.searchItemPaddingStart != 0);
            FrameLayout frameLayout2 = this.searchContainer;
            frameLayout2.setLayoutParams(frameLayout2.getLayoutParams());
        }
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2) {
        this(context, actionBarMenu, i, i2, false);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
        this(context, actionBarMenu, i, i2, false, resourcesProvider);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2, boolean z) {
        this(context, actionBarMenu, i, i2, z, null);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.searchFilterViews = new ArrayList<>();
        this.allowCloseAnimation = true;
        this.animationEnabled = true;
        this.animateClear = true;
        this.measurePopup = true;
        this.showSubmenuByMove = true;
        this.currentSearchFilters = new ArrayList<>();
        this.selectedFilterIndex = -1;
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.centerTitleVisibleForPartialAlpha = true;
        this.resourcesProvider = resourcesProvider;
        if (i != 0) {
            setBackgroundDrawable(Theme.createSelectorDrawable(i, z ? 5 : 1));
        }
        this.parentMenu = actionBarMenu;
        if (z) {
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 15.0f);
            this.textView.setTypeface(AndroidUtilities.bold());
            this.textView.setGravity(17);
            this.textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            this.textView.setImportantForAccessibility(2);
            if (i2 != 0) {
                this.textView.setTextColor(i2);
            }
            addView(this.textView, LayoutHelper.createFrame(-2, -1.0f));
            return;
        }
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.iconView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.iconView.setImportantForAccessibility(2);
        addView(this.iconView, LayoutHelper.createFrame(-1, -1.0f));
        if (i2 != 0) {
            this.iconView.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
        }
    }

    @Override // android.view.View
    public void setTranslationX(float f) {
        super.setTranslationX(f + this.transitionOffset);
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        boolean zIsVisibleForCenterTitle = isVisibleForCenterTitle();
        float alpha = getAlpha();
        super.setAlpha(f);
        if (f > alpha) {
            this.centerTitleVisibleForPartialAlpha = true;
        } else if (f < alpha) {
            this.centerTitleVisibleForPartialAlpha = false;
        }
        if (!ExteraConfig.getCenterTitle() || zIsVisibleForCenterTitle == isVisibleForCenterTitle()) {
            return;
        }
        ViewParent parent = getParent();
        if (parent instanceof ActionBarMenu) {
            Object parent2 = ((ActionBarMenu) parent).getParent();
            if (parent2 instanceof View) {
                ((View) parent2).requestLayout();
            }
        }
    }

    public boolean isVisibleForCenterTitle() {
        if (getVisibility() != 0) {
            return false;
        }
        float alpha = getAlpha();
        if (alpha >= 1.0f) {
            return true;
        }
        if (alpha <= 0.0f) {
            return false;
        }
        return this.centerTitleVisibleForPartialAlpha;
    }

    public void setLongClickEnabled(boolean z) {
        this.longClickEnabled = z;
    }

    public void setFixBackground(boolean z) {
        this.fixBackground = z;
        invalidate();
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        if (this.fixBackground) {
            getBackground().draw(canvas);
        }
        super.draw(canvas);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        ActionBarPopupWindow actionBarPopupWindow2;
        ActionBarPopupWindow actionBarPopupWindow3;
        if (motionEvent.getActionMasked() == 0) {
            if (this.longClickEnabled && hasSubMenu() && ((actionBarPopupWindow3 = this.popupWindow) == null || !actionBarPopupWindow3.isShowing())) {
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onTouchEvent$0();
                    }
                };
                this.showMenuRunnable = runnable;
                AndroidUtilities.runOnUIThread(runnable, 200L);
            }
        } else if (motionEvent.getActionMasked() == 2) {
            if (this.showSubmenuByMove && hasSubMenu() && ((actionBarPopupWindow2 = this.popupWindow) == null || !actionBarPopupWindow2.isShowing())) {
                if (motionEvent.getY() > getHeight()) {
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    toggleSubMenu();
                    return true;
                }
            } else if (this.showSubmenuByMove && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
                getLocationOnScreen(this.location);
                float x = motionEvent.getX() + this.location[0];
                float y = motionEvent.getY();
                int[] iArr = this.location;
                float f = y + iArr[1];
                this.popupLayout.getLocationOnScreen(iArr);
                int[] iArr2 = this.location;
                float f2 = x - iArr2[0];
                float f3 = f - iArr2[1];
                this.selectedMenuView = null;
                for (int i = 0; i < this.popupLayout.getItemsCount(); i++) {
                    View itemAt = this.popupLayout.getItemAt(i);
                    itemAt.getHitRect(this.rect);
                    Object tag = itemAt.getTag();
                    if (((tag instanceof Integer) && ((Integer) tag).intValue() < 100) || ((itemAt instanceof ActionBarMenuSubItem) && ((ActionBarMenuSubItem) itemAt).openSwipeBackLayout != null)) {
                        if (!this.rect.contains((int) f2, (int) f3)) {
                            itemAt.setPressed(false);
                            itemAt.setSelected(false);
                        } else {
                            itemAt.setPressed(true);
                            itemAt.setSelected(true);
                            itemAt.drawableHotspotChanged(f2, f3 - itemAt.getTop());
                            this.selectedMenuView = itemAt;
                        }
                    } else if (itemAt instanceof ActionRow) {
                        ActionRow actionRow = (ActionRow) itemAt;
                        if (this.rect.contains((int) f2, (int) f3)) {
                            float left = f2 - itemAt.getLeft();
                            float top = f3 - itemAt.getTop();
                            ViewGroup viewGroup = (ViewGroup) actionRow.getChildAt(0);
                            if (viewGroup != null) {
                                float left2 = left - viewGroup.getLeft();
                                float top2 = top - viewGroup.getTop();
                                for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
                                    View childAt = viewGroup.getChildAt(i2);
                                    childAt.getHitRect(this.rect);
                                    if (this.rect.contains((int) left2, (int) top2)) {
                                        childAt.setPressed(true);
                                        childAt.setSelected(true);
                                        childAt.drawableHotspotChanged(left2, top2 - childAt.getTop());
                                        this.selectedMenuView = childAt;
                                    } else {
                                        childAt.setPressed(false);
                                        childAt.setSelected(false);
                                    }
                                }
                            }
                        } else {
                            ViewGroup viewGroup2 = (ViewGroup) actionRow.getChildAt(0);
                            if (viewGroup2 != null) {
                                for (int i3 = 0; i3 < viewGroup2.getChildCount(); i3++) {
                                    View childAt2 = viewGroup2.getChildAt(i3);
                                    childAt2.setPressed(false);
                                    childAt2.setSelected(false);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            ActionBarPopupWindow actionBarPopupWindow4 = this.popupWindow;
            if (actionBarPopupWindow4 != null && actionBarPopupWindow4.isShowing() && motionEvent.getActionMasked() == 1) {
                View view = this.selectedMenuView;
                if (view != null) {
                    view.setSelected(false);
                    View view2 = this.selectedMenuView;
                    if ((view2 instanceof ActionBarMenuSubItem) && ((ActionBarMenuSubItem) view2).openSwipeBackLayout != null) {
                        view2.performClick();
                    } else {
                        Object tag2 = view2.getTag();
                        if (tag2 instanceof ActionRow.ActionItem) {
                            final ActionRow.ActionItem actionItem = (ActionRow.ActionItem) tag2;
                            if (this.processedPopupClick) {
                                return super.onTouchEvent(motionEvent);
                            }
                            this.processedPopupClick = true;
                            if (actionItem.enabled && actionItem.action != null) {
                                final View view3 = this.selectedMenuView;
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda4
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        actionItem.action.onClick(view3);
                                    }
                                });
                            }
                            ActionBarPopupWindow actionBarPopupWindow5 = this.popupWindow;
                            if (actionBarPopupWindow5 != null) {
                                actionBarPopupWindow5.dismiss(this.allowCloseAnimation);
                            }
                        } else {
                            ActionBarMenu actionBarMenu = this.parentMenu;
                            if (actionBarMenu != null) {
                                actionBarMenu.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                            } else {
                                ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
                                if (actionBarMenuItemDelegate != null) {
                                    actionBarMenuItemDelegate.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                                }
                            }
                            this.popupWindow.dismiss(this.allowCloseAnimation);
                        }
                    }
                } else if (this.showSubmenuByMove) {
                    this.popupWindow.dismiss();
                }
            } else {
                View view4 = this.selectedMenuView;
                if (view4 != null) {
                    view4.setSelected(false);
                    this.selectedMenuView = null;
                }
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public /* synthetic */ void lambda$onTouchEvent$0() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        toggleSubMenu();
    }

    public void setDelegate(ActionBarMenuItemDelegate actionBarMenuItemDelegate) {
        this.delegate = actionBarMenuItemDelegate;
    }

    public void setSubMenuDelegate(ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate) {
        this.subMenuDelegate = actionBarSubMenuItemDelegate;
    }

    public void setShowSubmenuByMove(boolean z) {
        this.showSubmenuByMove = z;
    }

    public void setIconColor(int i) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
        TextView textView = this.textView;
        if (textView != null) {
            textView.setTextColor(i);
        }
        ImageView imageView = this.clearButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setSubMenuOpenSide(int i) {
        this.subMenuOpenSide = i;
    }

    public void setLayoutInScreen(boolean z) {
        this.layoutInScreen = z;
    }

    public void setForceSmoothKeyboard(boolean z) {
        this.forceSmoothKeyboard = z;
    }

    public void createPopupLayout() {
        if (this.popupLayout != null) {
            return;
        }
        this.rect = new Rect();
        this.location = new int[2];
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), R.drawable.popup_fixed_alert4, this.resourcesProvider, 1);
        this.popupLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda10
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return this.f$0.lambda$createPopupLayout$2(view, motionEvent);
            }
        });
        this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda11
            @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
            public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                this.f$0.lambda$createPopupLayout$3(keyEvent);
            }
        });
    }

    public /* synthetic */ boolean lambda$createPopupLayout$2(View view, MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(this.rect);
        if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    public /* synthetic */ void lambda$createPopupLayout$3(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public void removeAllSubItems() {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        actionBarPopupWindowLayout.removeInnerViews();
    }

    public void setShowedFromBottom(boolean z) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        actionBarPopupWindowLayout.setShownFromBottom(z);
    }

    public void setFitSubItems(boolean z) {
        this.popupLayout.setFitItems(z);
    }

    public void addSubItem(View view, int i, int i2) {
        createPopupLayout();
        this.popupLayout.addView(view, new LinearLayout.LayoutParams(i, i2));
    }

    public void addSubItem(int i, View view, int i2, int i3) {
        createPopupLayout();
        view.setLayoutParams(new LinearLayout.LayoutParams(i2, i3));
        this.popupLayout.addView(view);
        view.setTag(Integer.valueOf(i));
        view.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda17
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.lambda$addSubItem$4(view2);
            }
        });
        view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    }

    public /* synthetic */ void lambda$addSubItem$4(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (this.processedPopupClick) {
                return;
            }
            this.processedPopupClick = true;
            this.popupWindow.dismiss(this.allowCloseAnimation);
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public TextView addSubItem(int i, CharSequence charSequence) {
        createPopupLayout();
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (!LocaleController.isRTL) {
            textView.setGravity(16);
        } else {
            textView.setGravity(21);
        }
        textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        textView.setTextSize(1, 16.0f);
        textView.setMinWidth(AndroidUtilities.dp(196.0f));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTag(Integer.valueOf(i));
        textView.setText(charSequence);
        this.popupLayout.addView(textView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        textView.setLayoutParams(layoutParams);
        textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$addSubItem$5(view);
            }
        });
        return textView;
    }

    public /* synthetic */ void lambda$addSubItem$5(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (this.processedPopupClick) {
                return;
            }
            this.processedPopupClick = true;
            if (!this.allowCloseAnimation) {
                this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
            }
            this.popupWindow.dismiss(this.allowCloseAnimation);
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence) {
        return addSubItem(i, i2, null, charSequence, true, false);
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence, Theme.ResourcesProvider resourcesProvider) {
        return addSubItem(i, i2, null, charSequence, true, false, resourcesProvider);
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence, boolean z) {
        return addSubItem(i, i2, null, charSequence, true, z);
    }

    public static View addGap(int i, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        View view = new View(actionBarPopupWindowLayout.getContext());
        view.setTag(Integer.valueOf(i));
        view.setTag(R.id.object_tag, 1);
        view.setTag(R.id.fit_width_tag, 1);
        actionBarPopupWindowLayout.addView(view);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(6.0f);
        view.setLayoutParams(layoutParams);
        return view;
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, Drawable drawable, CharSequence charSequence, boolean z, boolean z2) {
        return addSubItem(i, i2, drawable, charSequence, z, z2, this.resourcesProvider);
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public ActionBarMenuSubItem addSubItem(int i, int i2, Drawable drawable, CharSequence charSequence, final boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        createPopupLayout();
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), z2, false, false, resourcesProvider);
        actionBarMenuSubItem.setTextAndIcon(charSequence, i2, drawable);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarMenuSubItem.setTag(Integer.valueOf(i));
        this.popupLayout.addView(actionBarMenuSubItem);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) actionBarMenuSubItem.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        actionBarMenuSubItem.setLayoutParams(layoutParams);
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$addSubItem$6(z, view);
            }
        });
        return actionBarMenuSubItem;
    }

    public /* synthetic */ void lambda$addSubItem$6(boolean z, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing() && z) {
            if (this.processedPopupClick) {
                return;
            }
            this.processedPopupClick = true;
            this.popupWindow.dismiss(this.allowCloseAnimation);
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public View addSubItem(int i, View view) {
        createPopupLayout();
        view.setMinimumWidth(AndroidUtilities.dp(196.0f));
        view.setTag(Integer.valueOf(i));
        this.popupLayout.addView(view);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        view.setLayoutParams(layoutParams);
        view.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda18
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.lambda$addSubItem$7(view2);
            }
        });
        return view;
    }

    public /* synthetic */ void lambda$addSubItem$7(View view) {
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public ActionBarMenuSubItem addSwipeBackItem(int i, Drawable drawable, String str, View view) {
        createPopupLayout();
        final ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), false, false, false, this.resourcesProvider);
        actionBarMenuSubItem.setTextAndIcon(str, i, drawable);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarMenuSubItem.setRightIcon(R.drawable.msg_arrowright);
        this.popupLayout.addView(actionBarMenuSubItem);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) actionBarMenuSubItem.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        actionBarMenuSubItem.setLayoutParams(layoutParams);
        final int iAddViewToSwipeBack = this.popupLayout.addViewToSwipeBack(view);
        actionBarMenuSubItem.openSwipeBackLayout = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$addSwipeBackItem$8(iAddViewToSwipeBack);
            }
        };
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                actionBarMenuSubItem.openSwipeBack();
            }
        });
        this.popupLayout.swipeBackGravityRight = true;
        return actionBarMenuSubItem;
    }

    public /* synthetic */ void lambda$addSwipeBackItem$8(int i) {
        if (this.popupLayout.getSwipeBack() != null) {
            this.popupLayout.getSwipeBack().openForeground(i);
        }
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public View addDivider(int i) {
        createPopupLayout();
        TextView textView = new TextView(getContext());
        textView.setBackgroundColor(i);
        textView.setMinimumWidth(AndroidUtilities.dp(196.0f));
        this.popupLayout.addView(textView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = 1;
        int iDp = AndroidUtilities.dp(3.0f);
        layoutParams.bottomMargin = iDp;
        layoutParams.topMargin = iDp;
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public void redrawPopup(int i) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null || actionBarPopupWindowLayout.getBackgroundColor() == i) {
            return;
        }
        this.popupLayout.setBackgroundColor(i);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupLayout.invalidate();
    }

    public void setPopupItemsColor(int i, boolean z) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        LinearLayout linearLayout = actionBarPopupWindowLayout.linearLayout;
        int childCount = linearLayout.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = linearLayout.getChildAt(i2);
            if (childAt instanceof TextView) {
                ((TextView) childAt).setTextColor(i);
            } else if (childAt instanceof ActionBarMenuSubItem) {
                if (z) {
                    ((ActionBarMenuSubItem) childAt).setIconColor(i);
                } else {
                    ((ActionBarMenuSubItem) childAt).setTextColor(i);
                }
            }
        }
    }

    public void setPopupItemsSelectorColor(int i) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        LinearLayout linearLayout = actionBarPopupWindowLayout.linearLayout;
        int childCount = linearLayout.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = linearLayout.getChildAt(i2);
            if (childAt instanceof ActionBarMenuSubItem) {
                ((ActionBarMenuSubItem) childAt).setSelectorColor(i);
            }
        }
    }

    public void setupPopupRadialSelectors(int i) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setupRadialSelectors(i);
        }
    }

    public boolean hasSubMenu() {
        if (this.popupLayout != null) {
            return true;
        }
        ArrayList<Item> arrayList = this.lazyList;
        return (arrayList == null || arrayList.isEmpty()) ? false : true;
    }

    public ActionBarPopupWindow.ActionBarPopupWindowLayout getPopupLayout() {
        if (this.popupLayout == null) {
            createPopupLayout();
        }
        return this.popupLayout;
    }

    public void setMenuYOffset(int i) {
        this.yOffset = i;
    }

    public void setMenuXOffset(int i) {
        this.xOffset = i;
    }

    public void toggleSubMenu(final View view, View view2) {
        View view3;
        View childAt;
        ActionBar actionBar;
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            layoutLazyItems();
        }
        if (this.popupLayout != null) {
            ActionBarMenu actionBarMenu = this.parentMenu;
            if (actionBarMenu == null || !actionBarMenu.isActionMode || (actionBar = actionBarMenu.parentActionBar) == null || actionBar.isActionModeShowed()) {
                Runnable runnable = this.showMenuRunnable;
                FrameLayout frameLayout = null;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.showMenuRunnable = null;
                }
                ActionBarPopupWindow actionBarPopupWindow2 = this.popupWindow;
                if (actionBarPopupWindow2 != null && actionBarPopupWindow2.isShowing()) {
                    this.popupWindow.dismiss();
                    return;
                }
                this.showSubMenuFrom = view2;
                ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate = this.subMenuDelegate;
                if (actionBarSubMenuItemDelegate != null) {
                    actionBarSubMenuItemDelegate.onShowSubMenu();
                }
                if (this.popupLayout.getParent() != null) {
                    ((ViewGroup) this.popupLayout.getParent()).removeView(this.popupLayout);
                }
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
                if (view != null) {
                    LinearLayout linearLayout = new LinearLayout(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.1
                        @Override // android.widget.LinearLayout, android.view.View
                        public void onMeasure(int i, int i2) {
                            ActionBarMenuItem.this.popupLayout.measure(i, i2);
                            PopupSwipeBackLayout swipeBack = ActionBarMenuItem.this.popupLayout.getSwipeBack();
                            View view4 = view;
                            if (swipeBack != null) {
                                view4.getLayoutParams().width = ActionBarMenuItem.this.popupLayout.getSwipeBack().getChildAt(0).getMeasuredWidth();
                            } else {
                                view4.getLayoutParams().width = ActionBarMenuItem.this.popupLayout.getMeasuredWidth() - AndroidUtilities.dp(16.0f);
                            }
                            super.onMeasure(i, i2);
                        }
                    };
                    linearLayout.setOrientation(1);
                    frameLayout = new FrameLayout(getContext());
                    frameLayout.setAlpha(0.0f);
                    frameLayout.animate().alpha(1.0f).setDuration(100L).setStartDelay(this.popupLayout.shownFromBottom ? 165L : 0L).start();
                    if (view.getParent() instanceof ViewGroup) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    if ((view instanceof ActionBarMenuSubItem) || (view instanceof LinearLayout)) {
                        if (this.popupLayout.getGlassBackgroundFactory() != null) {
                            frameLayout.setBackground(GlassMenuHelper.createPanelBackground(this.popupLayout.getGlassBackgroundFactory(), this.resourcesProvider, frameLayout));
                        } else {
                            Drawable drawableMutate = ContextCompat.getDrawable(getContext(), R.drawable.popup_fixed_alert2).mutate();
                            drawableMutate.setColorFilter(new PorterDuffColorFilter(this.popupLayout.getBackgroundColor(), PorterDuff.Mode.MULTIPLY));
                            frameLayout.setBackground(drawableMutate);
                        }
                    }
                    frameLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f));
                    linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
                    linearLayout.addView(this.popupLayout, LayoutHelper.createLinear(-2, -2, 0, 0, -10, 0, 0));
                    this.popupLayout.setTopView(frameLayout);
                    view3 = linearLayout;
                } else {
                    actionBarPopupWindowLayout.setTopView(null);
                    view3 = actionBarPopupWindowLayout;
                }
                ActionBarPopupWindow actionBarPopupWindow3 = new ActionBarPopupWindow(view3, -2, -2);
                this.popupWindow = actionBarPopupWindow3;
                if (this.animationEnabled) {
                    actionBarPopupWindow3.setAnimationStyle(0);
                } else {
                    actionBarPopupWindow3.setAnimationStyle(R.style.PopupAnimation);
                }
                boolean z = this.animationEnabled;
                if (!z) {
                    this.popupWindow.setAnimationEnabled(z);
                }
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                if (this.layoutInScreen) {
                    this.popupWindow.setLayoutInScreen(true);
                }
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                view3.setFocusableInTouchMode(true);
                view3.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda5
                    @Override // android.view.View.OnKeyListener
                    public final boolean onKey(View view4, int i, KeyEvent keyEvent) {
                        return this.f$0.lambda$toggleSubMenu$10(view4, i, keyEvent);
                    }
                });
                this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda6
                    @Override // android.widget.PopupWindow.OnDismissListener
                    public final void onDismiss() {
                        this.f$0.lambda$toggleSubMenu$11();
                    }
                });
                view3.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
                if (frameLayout != null && frameLayout.getLayoutParams() != null && this.popupLayout.getSwipeBack() != null && (childAt = this.popupLayout.getSwipeBack().getChildAt(0)) != null && childAt.getMeasuredWidth() > 0) {
                    frameLayout.getLayoutParams().width = childAt.getMeasuredWidth() + AndroidUtilities.dp(16.0f);
                }
                this.measurePopup = false;
                this.processedPopupClick = false;
                this.popupWindow.setFocusable(true);
                updateOrShowPopup(true, view3.getMeasuredWidth() == 0);
                this.popupLayout.updateRadialSelectors();
                if (this.popupLayout.getSwipeBack() != null) {
                    this.popupLayout.getSwipeBack().closeForeground(false);
                }
                this.popupWindow.startAnimation();
                float f = this.dimMenu;
                if (f > 0.0f) {
                    this.popupWindow.dimBehind(f);
                }
            }
        }
    }

    public /* synthetic */ boolean lambda$toggleSubMenu$10(View view, int i, KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        this.popupWindow.dismiss();
        return true;
    }

    public /* synthetic */ void lambda$toggleSubMenu$11() {
        onDismiss();
        ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate = this.subMenuDelegate;
        if (actionBarSubMenuItemDelegate != null) {
            actionBarSubMenuItemDelegate.onHideSubMenu();
        }
    }

    public void setDimMenu(float f) {
        this.dimMenu = f;
    }

    public void toggleSubMenu() {
        toggleSubMenu(null, null);
    }

    public void setOnMenuDismiss(final Utilities.Callback<Boolean> callback) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda14
                @Override // android.widget.PopupWindow.OnDismissListener
                public final void onDismiss() {
                    this.f$0.lambda$setOnMenuDismiss$12(callback);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setOnMenuDismiss$12(Utilities.Callback callback) {
        if (callback != null) {
            callback.run(Boolean.valueOf(this.processedPopupClick));
        }
    }

    public void openSearch(boolean z) {
        ActionBarMenu actionBarMenu;
        checkCreateSearchField();
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout == null || frameLayout.getVisibility() == 0 || (actionBarMenu = this.parentMenu) == null) {
            return;
        }
        actionBarMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(z));
    }

    public boolean isSearchFieldVisible() {
        FrameLayout frameLayout = this.searchContainer;
        return frameLayout != null && frameLayout.getVisibility() == 0;
    }

    public boolean isSearchFieldVisible2() {
        FrameLayout frameLayout = this.searchContainer;
        return (frameLayout == null || frameLayout.getTag() == null) ? false : true;
    }

    public boolean toggleSearch(boolean z) {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        AnimatorSet animatorSet;
        RLottieImageView iconView;
        Animator customToggleTransition;
        checkCreateSearchField();
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
        if (actionBarMenuItemSearchListener2 != null) {
            actionBarMenuItemSearchListener2.onPreToggleSearch();
        }
        if (this.searchContainer == null || !((actionBarMenuItemSearchListener = this.listener) == null || actionBarMenuItemSearchListener.canToggleSearch())) {
            return false;
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener3 = this.listener;
        if (actionBarMenuItemSearchListener3 != null && (customToggleTransition = actionBarMenuItemSearchListener3.getCustomToggleTransition()) != null) {
            customToggleTransition.start();
            return true;
        }
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.parentMenu.getChildCount(); i++) {
            View childAt = this.parentMenu.getChildAt(i);
            if ((childAt instanceof ActionBarMenuItem) && (iconView = ((ActionBarMenuItem) childAt).getIconView()) != null) {
                arrayList.add(iconView);
            }
        }
        Object tag = this.searchContainer.getTag();
        FrameLayout frameLayout = this.searchContainer;
        Property property = View.ALPHA;
        if (tag != null) {
            frameLayout.setTag(null);
            AnimatorSet animatorSet2 = this.searchContainerAnimator;
            if (animatorSet2 != null) {
                animatorSet2.removeAllListeners();
                this.searchContainerAnimator.cancel();
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.searchContainerAnimator = animatorSet3;
            FrameLayout frameLayout2 = this.searchContainer;
            animatorSet3.playTogether(ObjectAnimator.ofFloat(frameLayout2, (Property<FrameLayout, Float>) property, frameLayout2.getAlpha(), 0.0f));
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                ((View) arrayList.get(i2)).setAlpha(0.0f);
                this.searchContainerAnimator.playTogether(ObjectAnimator.ofFloat((View) arrayList.get(i2), (Property<View, Float>) property, ((View) arrayList.get(i2)).getAlpha(), 1.0f));
            }
            this.searchContainerAnimator.setDuration(150L);
            this.searchContainerAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ActionBarMenuItem.this.searchContainer.setAlpha(0.0f);
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        ((View) arrayList.get(i3)).setAlpha(1.0f);
                    }
                    ActionBarMenuItem.this.searchContainer.setVisibility(8);
                }
            });
            this.searchContainerAnimator.start();
            this.searchField.clearFocus();
            setVisibility(0);
            if (!this.currentSearchFilters.isEmpty() && this.listener != null) {
                for (int i3 = 0; i3 < this.currentSearchFilters.size(); i3++) {
                    if (this.currentSearchFilters.get(i3).removable) {
                        this.listener.onSearchFilterCleared(this.currentSearchFilters.get(i3));
                    }
                }
            }
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener4 = this.listener;
            if (actionBarMenuItemSearchListener4 != null) {
                actionBarMenuItemSearchListener4.onSearchCollapse();
            }
            if (z) {
                AndroidUtilities.hideKeyboard(this.searchField);
            }
            this.parentMenu.requestLayout();
            requestLayout();
            return false;
        }
        frameLayout.setVisibility(0);
        this.searchContainer.setAlpha(0.0f);
        AnimatorSet animatorSet4 = this.searchContainerAnimator;
        if (animatorSet4 != null) {
            animatorSet4.removeAllListeners();
            this.searchContainerAnimator.cancel();
        }
        AnimatorSet animatorSet5 = new AnimatorSet();
        this.searchContainerAnimator = animatorSet5;
        FrameLayout frameLayout3 = this.searchContainer;
        animatorSet5.playTogether(ObjectAnimator.ofFloat(frameLayout3, (Property<FrameLayout, Float>) property, frameLayout3.getAlpha(), 1.0f));
        int i4 = 0;
        while (true) {
            int size = arrayList.size();
            animatorSet = this.searchContainerAnimator;
            if (i4 >= size) {
                break;
            }
            animatorSet.playTogether(ObjectAnimator.ofFloat((View) arrayList.get(i4), (Property<View, Float>) property, ((View) arrayList.get(i4)).getAlpha(), 0.0f));
            i4++;
        }
        animatorSet.setDuration(150L);
        this.searchContainerAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ActionBarMenuItem.this.searchContainer.setAlpha(1.0f);
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    ((View) arrayList.get(i5)).setAlpha(0.0f);
                }
            }
        });
        this.searchContainerAnimator.start();
        setVisibility(8);
        clearSearchFilters();
        this.searchField.setText(_UrlKt.FRAGMENT_ENCODE_SET);
        this.searchField.requestFocus();
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$toggleSearch$13();
                }
            });
        }
        this.searchContainer.setTag(1);
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener5 = this.listener;
        if (actionBarMenuItemSearchListener5 != null) {
            actionBarMenuItemSearchListener5.onSearchExpand();
        }
        return true;
    }

    public /* synthetic */ void lambda$toggleSearch$13() {
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.searchField);
        }
    }

    public void removeSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        if (mediaFilterData.removable) {
            this.currentSearchFilters.remove(mediaFilterData);
            int i = this.selectedFilterIndex;
            if (i < 0 || i > this.currentSearchFilters.size() - 1) {
                this.selectedFilterIndex = this.currentSearchFilters.size() - 1;
            }
            onFiltersChanged();
            this.searchField.hideActionMode();
        }
    }

    public void addSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        this.currentSearchFilters.add(mediaFilterData);
        if (this.searchContainer.getTag() != null) {
            this.selectedFilterIndex = this.currentSearchFilters.size() - 1;
        }
        onFiltersChanged();
    }

    public void clearSearchFilters() {
        int i = 0;
        while (i < this.currentSearchFilters.size()) {
            if (this.currentSearchFilters.get(i).removable) {
                this.currentSearchFilters.remove(i);
                i--;
            }
            i++;
        }
        onFiltersChanged();
    }

    public void onFiltersChanged() {
        final SearchFilterView searchFilterView;
        boolean zIsEmpty = this.currentSearchFilters.isEmpty();
        ArrayList arrayList = new ArrayList(this.currentSearchFilters);
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null && frameLayout.getTag() != null) {
            TransitionSet transitionSet = new TransitionSet();
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(150L);
            transitionSet.addTransition(new Visibility() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.4
                @Override // android.transition.Visibility
                public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                    if (view instanceof SearchFilterView) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_X, 0.5f, 1.0f), ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, 0.5f, 1.0f));
                        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        return animatorSet;
                    }
                    return ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f);
                }

                @Override // android.transition.Visibility
                public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                    if (view instanceof SearchFilterView) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, view.getAlpha(), 0.0f), ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_X, view.getScaleX(), 0.5f), ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, view.getScaleX(), 0.5f));
                        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        return animatorSet;
                    }
                    return ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, 1.0f, 0.0f);
                }
            }.setDuration(150L)).addTransition(changeBounds);
            transitionSet.setOrdering(0);
            transitionSet.setInterpolator((TimeInterpolator) CubicBezierInterpolator.EASE_OUT);
            transitionSet.addListener(new Transition.TransitionListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.5
                @Override // android.transition.Transition.TransitionListener
                public void onTransitionPause(Transition transition) {
                }

                @Override // android.transition.Transition.TransitionListener
                public void onTransitionResume(Transition transition) {
                }

                @Override // android.transition.Transition.TransitionListener
                public void onTransitionStart(Transition transition) {
                    ActionBarMenuItem.this.notificationsLocker.lock();
                }

                @Override // android.transition.Transition.TransitionListener
                public void onTransitionEnd(Transition transition) {
                    ActionBarMenuItem.this.notificationsLocker.unlock();
                }

                @Override // android.transition.Transition.TransitionListener
                public void onTransitionCancel(Transition transition) {
                    ActionBarMenuItem.this.notificationsLocker.unlock();
                }
            });
            TransitionManager.beginDelayedTransition(this.searchFilterLayout, transitionSet);
        }
        if (this.searchFilterLayout != null) {
            int i = 0;
            while (i < this.searchFilterLayout.getChildCount()) {
                if (!arrayList.remove(((SearchFilterView) this.searchFilterLayout.getChildAt(i)).getFilter())) {
                    this.searchFilterLayout.removeViewAt(i);
                    i--;
                }
                i++;
            }
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            FiltersView.MediaFilterData mediaFilterData = (FiltersView.MediaFilterData) arrayList.get(i2);
            if (mediaFilterData.reaction != null) {
                searchFilterView = new ReactionFilterView(getContext(), this.resourcesProvider, false);
            } else {
                searchFilterView = new SearchFilterView(getContext(), this.resourcesProvider, false);
            }
            searchFilterView.setData(mediaFilterData);
            searchFilterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$onFiltersChanged$14(searchFilterView, view);
                }
            });
            this.searchFilterLayout.addView(searchFilterView, LayoutHelper.createLinear(-2, -1, 0, 0, 0, 6, 0));
        }
        if (this.searchFilterLayout != null) {
            int i3 = 0;
            while (i3 < this.searchFilterLayout.getChildCount()) {
                ((SearchFilterView) this.searchFilterLayout.getChildAt(i3)).setExpanded(i3 == this.selectedFilterIndex);
                i3++;
            }
        }
        LinearLayout linearLayout = this.searchFilterLayout;
        if (linearLayout != null) {
            linearLayout.setTag(!zIsEmpty ? 1 : null);
        }
        final float x = this.searchField.getX();
        if (this.searchContainer.getTag() != null) {
            this.searchField.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.6
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    ActionBarMenuItem.this.searchField.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (ActionBarMenuItem.this.searchField.getX() != x) {
                        ActionBarMenuItem.this.searchField.setTranslationX(x - ActionBarMenuItem.this.searchField.getX());
                    }
                    ActionBarMenuItem.this.searchField.animate().translationX(0.0f).setDuration(250L).setStartDelay(0L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    return true;
                }
            });
        }
        checkClearButton();
    }

    public /* synthetic */ void lambda$onFiltersChanged$14(SearchFilterView searchFilterView, View view) {
        int iIndexOf = this.currentSearchFilters.indexOf(searchFilterView.getFilter());
        if (this.selectedFilterIndex != iIndexOf) {
            this.selectedFilterIndex = iIndexOf;
            onFiltersChanged();
            return;
        }
        if (searchFilterView.getFilter().removable) {
            if (!searchFilterView.isSelectedForDelete()) {
                searchFilterView.setSelectedForDelete(true);
                return;
            }
            FiltersView.MediaFilterData filter = searchFilterView.getFilter();
            removeSearchFilter(filter);
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
            if (actionBarMenuItemSearchListener != null) {
                actionBarMenuItemSearchListener.onSearchFilterCleared(filter);
                this.listener.onTextChanged(this.searchField);
            }
        }
    }

    public boolean isSubMenuShowing() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        return actionBarPopupWindow != null && actionBarPopupWindow.isShowing();
    }

    public void closeSubMenu() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupWindow.dismiss();
    }

    public void setIcon(Drawable drawable) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView == null) {
            return;
        }
        if (drawable instanceof RLottieDrawable) {
            rLottieImageView.setAnimation((RLottieDrawable) drawable);
        } else {
            rLottieImageView.setImageDrawable(drawable);
        }
        this.iconViewResId = 0;
    }

    public RLottieImageView getIconView() {
        return this.iconView;
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setIcon(int i) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView == null) {
            return;
        }
        this.iconViewResId = i;
        rLottieImageView.setImageResource(i);
    }

    public void setIcon(int i, boolean z) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView == null || this.iconViewResId == i) {
            return;
        }
        if (z) {
            this.iconViewResId = i;
            AndroidUtilities.updateImageViewImageAnimated(rLottieImageView, i);
        } else {
            this.iconViewResId = i;
            rLottieImageView.setImageResource(i);
        }
    }

    public void setText(CharSequence charSequence) {
        TextView textView = this.textView;
        if (textView == null) {
            return;
        }
        textView.setText(charSequence);
    }

    public View getContentView() {
        RLottieImageView rLottieImageView = this.iconView;
        return rLottieImageView != null ? rLottieImageView : this.textView;
    }

    public void setSearchFieldHint(CharSequence charSequence) {
        this.searchFieldHint = charSequence;
        if (this.searchFieldCaption == null) {
            return;
        }
        this.searchField.setHint(charSequence);
        setContentDescription(charSequence);
    }

    public void setSearchFieldText(CharSequence charSequence, boolean z) {
        this.searchFieldText = charSequence;
        if (this.searchFieldCaption == null) {
            return;
        }
        this.animateClear = z;
        this.searchField.setText(charSequence);
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        this.searchField.setSelection(charSequence.length());
    }

    public void onSearchPressed() {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
        }
    }

    public EditTextBoldCursor getSearchField() {
        checkCreateSearchField();
        return this.searchField;
    }

    public ActionBarMenuItem setOverrideMenuClick(boolean z) {
        this.overrideMenuClick = z;
        return this;
    }

    public ActionBarMenuItem setIsSearchField(boolean z) {
        return setIsSearchField(z, false);
    }

    public void setSearchAdditionalButton(View view) {
        this.searchAdditionalButton = view;
    }

    public ActionBarMenuItem setIsSearchField(boolean z, boolean z2) {
        if (this.parentMenu == null) {
            return this;
        }
        this.isSearchField = z;
        this.wrapSearchInScrollView = z2;
        return this;
    }

    private void checkCreateSearchField() {
        if (this.searchContainer == null && this.isSearchField) {
            FrameLayout frameLayout = new FrameLayout(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.7
                private boolean ignoreRequestLayout;

                @Override // android.view.View
                public void setVisibility(int i) {
                    super.setVisibility(i);
                    if (ActionBarMenuItem.this.clearButton != null) {
                        ActionBarMenuItem.this.clearButton.setVisibility(i);
                    }
                    if (ActionBarMenuItem.this.searchAdditionalButton != null) {
                        ActionBarMenuItem.this.searchAdditionalButton.setVisibility(i);
                    }
                    if (ActionBarMenuItem.this.wrappedSearchFrameLayout != null) {
                        ActionBarMenuItem.this.wrappedSearchFrameLayout.setVisibility(i);
                    }
                }

                @Override // android.view.View
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    if (ActionBarMenuItem.this.clearButton == null || ActionBarMenuItem.this.clearButton.getTag() == null) {
                        return;
                    }
                    ActionBarMenuItem.this.clearButton.setAlpha(f);
                    ActionBarMenuItem.this.clearButton.setScaleX(f);
                    ActionBarMenuItem.this.clearButton.setScaleY(f);
                }

                @Override // android.widget.FrameLayout, android.view.View
                public void onMeasure(int i, int i2) {
                    AnonymousClass7 anonymousClass7;
                    int i3;
                    int i4;
                    int measuredWidth;
                    int measuredWidth2;
                    if (ActionBarMenuItem.this.wrapSearchInScrollView) {
                        anonymousClass7 = this;
                        i3 = i;
                        i4 = i2;
                    } else {
                        measureChildWithMargins(ActionBarMenuItem.this.clearButton, i, 0, i2, 0);
                        anonymousClass7 = this;
                        i3 = i;
                        i4 = i2;
                        if (ActionBarMenuItem.this.searchAdditionalButton != null) {
                            anonymousClass7.measureChildWithMargins(ActionBarMenuItem.this.searchAdditionalButton, i3, 0, i4, 0);
                        }
                    }
                    boolean z = LocaleController.isRTL;
                    ActionBarMenuItem actionBarMenuItem = ActionBarMenuItem.this;
                    if (!z) {
                        if (actionBarMenuItem.searchFieldCaption.getVisibility() == 0) {
                            anonymousClass7.measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, i3, View.MeasureSpec.getSize(i3) / 2, i4, 0);
                            measuredWidth2 = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                        } else {
                            measuredWidth2 = 0;
                        }
                        int size = View.MeasureSpec.getSize(i3);
                        anonymousClass7.ignoreRequestLayout = true;
                        anonymousClass7.measureChildWithMargins(ActionBarMenuItem.this.searchFilterLayout, i3, measuredWidth2, i4, 0);
                        int measuredWidth3 = ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0 ? ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth() : 0;
                        anonymousClass7.measureChildWithMargins(ActionBarMenuItem.this.searchField, i3, measuredWidth2 + measuredWidth3 + (ActionBarMenuItem.this.searchAdditionalButton != null ? ActionBarMenuItem.this.searchAdditionalButton.getMeasuredWidth() : 0), i4, 0);
                        anonymousClass7.ignoreRequestLayout = false;
                        anonymousClass7.setMeasuredDimension(Math.max(measuredWidth3 + ActionBarMenuItem.this.searchField.getMeasuredWidth(), size), View.MeasureSpec.getSize(i4));
                        return;
                    }
                    if (actionBarMenuItem.searchFieldCaption.getVisibility() == 0) {
                        anonymousClass7.measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, i3, View.MeasureSpec.getSize(i3) / 2, i4, 0);
                        measuredWidth = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                    } else {
                        measuredWidth = 0;
                    }
                    int size2 = View.MeasureSpec.getSize(i3);
                    anonymousClass7.ignoreRequestLayout = true;
                    anonymousClass7.measureChildWithMargins(ActionBarMenuItem.this.searchFilterLayout, i3, measuredWidth, i4, 0);
                    int measuredWidth4 = ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0 ? ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth() : 0;
                    anonymousClass7.measureChildWithMargins(ActionBarMenuItem.this.searchField, View.MeasureSpec.makeMeasureSpec(size2 - AndroidUtilities.dp(12.0f), 0), measuredWidth + measuredWidth4, i4, 0);
                    anonymousClass7.ignoreRequestLayout = false;
                    anonymousClass7.setMeasuredDimension(Math.max(measuredWidth4 + ActionBarMenuItem.this.searchField.getMeasuredWidth(), size2), View.MeasureSpec.getSize(i4));
                }

                @Override // android.view.View, android.view.ViewParent
                public void requestLayout() {
                    if (this.ignoreRequestLayout) {
                        return;
                    }
                    super.requestLayout();
                }

                @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    int measuredWidth = 0;
                    if (!LocaleController.isRTL && ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        measuredWidth = AndroidUtilities.dp(4.0f) + ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth();
                    }
                    if (ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0) {
                        measuredWidth += ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth();
                    }
                    ActionBarMenuItem.this.searchField.layout(measuredWidth, ActionBarMenuItem.this.searchField.getTop(), ActionBarMenuItem.this.searchField.getMeasuredWidth() + measuredWidth, ActionBarMenuItem.this.searchField.getBottom());
                }
            };
            this.searchContainer = frameLayout;
            frameLayout.setClipChildren(this.searchItemPaddingStart != 0);
            this.wrappedSearchFrameLayout = null;
            if (this.wrapSearchInScrollView) {
                this.wrappedSearchFrameLayout = new FrameLayout(getContext());
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.8
                    boolean isDragging;

                    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        checkDragg(motionEvent);
                        return super.onInterceptTouchEvent(motionEvent);
                    }

                    @Override // android.widget.HorizontalScrollView, android.view.View
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        checkDragg(motionEvent);
                        return super.onTouchEvent(motionEvent);
                    }

                    private void checkDragg(MotionEvent motionEvent) {
                        if (motionEvent.getAction() == 0) {
                            this.isDragging = true;
                        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                            this.isDragging = false;
                        }
                    }

                    @Override // android.widget.HorizontalScrollView, android.view.View
                    public void onOverScrolled(int i, int i2, boolean z, boolean z2) {
                        if (this.isDragging) {
                            super.onOverScrolled(i, i2, z, z2);
                        }
                    }
                };
                horizontalScrollView.addView(this.searchContainer, LayoutHelper.createScroll(-2, -1, 0));
                horizontalScrollView.setHorizontalScrollBarEnabled(false);
                horizontalScrollView.setClipChildren(this.searchItemPaddingStart != 0);
                this.wrappedSearchFrameLayout.addView(horizontalScrollView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 48.0f, 0.0f));
                this.parentMenu.addView(this.wrappedSearchFrameLayout, 0, LayoutHelper.createLinear(0, -1, 1.0f, this.searchItemPaddingStart, 0, 0, 0));
            } else {
                this.parentMenu.addView(this.searchContainer, 0, LayoutHelper.createLinear(0, -1, 1.0f, this.searchItemPaddingStart + 6, 0, this.searchRightMargin, 0));
            }
            this.searchContainer.setVisibility(8);
            TextView textView = new TextView(getContext());
            this.searchFieldCaption = textView;
            textView.setTextSize(1, 18.0f);
            TextView textView2 = this.searchFieldCaption;
            int i = Theme.key_actionBarDefaultSearch;
            textView2.setTextColor(getThemedColor(i));
            this.searchFieldCaption.setSingleLine(true);
            this.searchFieldCaption.setEllipsize(TextUtils.TruncateAt.END);
            this.searchFieldCaption.setVisibility(8);
            this.searchFieldCaption.setGravity(LocaleController.isRTL ? 5 : 3);
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.9
                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public void onMeasure(int i2, int i3) {
                    super.onMeasure(i2, i3);
                    setMeasuredDimension(Math.max(View.MeasureSpec.getSize(i2), getMeasuredWidth()) + AndroidUtilities.dp(3.0f), getMeasuredHeight());
                }

                @Override // org.telegram.ui.Components.EditTextEffects, android.widget.TextView
                public void onSelectionChanged(int i2, int i3) {
                    super.onSelectionChanged(i2, i3);
                }

                @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
                public boolean onKeyDown(int i2, KeyEvent keyEvent) {
                    if (i2 == 67 && ActionBarMenuItem.this.searchField.length() == 0 && ((ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0 && ActionBarMenuItem.this.searchFieldCaption.length() > 0) || ActionBarMenuItem.this.hasRemovableFilters())) {
                        boolean zHasRemovableFilters = ActionBarMenuItem.this.hasRemovableFilters();
                        ActionBarMenuItem actionBarMenuItem = ActionBarMenuItem.this;
                        if (zHasRemovableFilters) {
                            FiltersView.MediaFilterData mediaFilterData = (FiltersView.MediaFilterData) actionBarMenuItem.currentSearchFilters.get(ActionBarMenuItem.this.currentSearchFilters.size() - 1);
                            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = ActionBarMenuItem.this.listener;
                            if (actionBarMenuItemSearchListener != null) {
                                actionBarMenuItemSearchListener.onSearchFilterCleared(mediaFilterData);
                            }
                            ActionBarMenuItem.this.removeSearchFilter(mediaFilterData);
                        } else {
                            actionBarMenuItem.clearButton.callOnClick();
                        }
                        return true;
                    }
                    return super.onKeyDown(i2, keyEvent);
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    boolean zOnTouchEvent = super.onTouchEvent(motionEvent);
                    if (motionEvent.getAction() == 1 && !AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return zOnTouchEvent;
                }
            };
            this.searchField = editTextBoldCursor;
            editTextBoldCursor.setScrollContainer(false);
            this.searchField.setCursorWidth(1.5f);
            this.searchField.setCursorColor(getThemedColor(i));
            this.searchField.setTextSize(1, 18.0f);
            this.searchField.setHintTextColor(getThemedColor(Theme.key_actionBarDefaultSearchPlaceholder));
            this.searchField.setTextColor(getThemedColor(i));
            this.searchField.setSingleLine(true);
            this.searchField.setBackgroundResource(0);
            this.searchField.setPadding(0, 0, 0, 0);
            this.searchField.setInputType(this.searchField.getInputType() | 524288);
            this.searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda12
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView3, int i2, KeyEvent keyEvent) {
                    return this.f$0.lambda$checkCreateSearchField$15(textView3, i2, keyEvent);
                }
            });
            this.searchField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.11
                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                    boolean z = ActionBarMenuItem.this.ignoreOnTextChange;
                    ActionBarMenuItem actionBarMenuItem = ActionBarMenuItem.this;
                    if (z) {
                        actionBarMenuItem.ignoreOnTextChange = false;
                        return;
                    }
                    ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = actionBarMenuItem.listener;
                    if (actionBarMenuItemSearchListener != null) {
                        actionBarMenuItemSearchListener.onTextChanged(actionBarMenuItem.searchField);
                    }
                    ActionBarMenuItem.this.checkClearButton();
                    if (ActionBarMenuItem.this.currentSearchFilters.isEmpty() || TextUtils.isEmpty(ActionBarMenuItem.this.searchField.getText()) || ActionBarMenuItem.this.selectedFilterIndex < 0) {
                        return;
                    }
                    ActionBarMenuItem.this.selectedFilterIndex = -1;
                    ActionBarMenuItem.this.onFiltersChanged();
                }
            });
            this.searchField.setImeOptions(234881027);
            this.searchField.setTextIsSelectable(false);
            this.searchField.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
            this.searchField.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
            CharSequence charSequence = this.searchFieldHint;
            if (charSequence != null) {
                this.searchField.setHint(charSequence);
                setContentDescription(this.searchFieldHint);
            }
            CharSequence charSequence2 = this.searchFieldText;
            if (charSequence2 != null) {
                this.searchField.setText(charSequence2);
            }
            LinearLayout linearLayout = new LinearLayout(getContext());
            this.searchFilterLayout = linearLayout;
            linearLayout.setOrientation(0);
            this.searchFilterLayout.setVisibility(0);
            boolean z = LocaleController.isRTL;
            FrameLayout frameLayout2 = this.searchContainer;
            if (!z) {
                frameLayout2.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 19, 0.0f, 5.5f, 0.0f, 0.0f));
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 6.0f, 0.0f, this.wrapSearchInScrollView ? 0.0f : 48.0f, 0.0f));
                this.searchContainer.addView(this.searchFilterLayout, LayoutHelper.createFrame(-2, 32.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
            } else {
                frameLayout2.addView(this.searchFilterLayout, LayoutHelper.createFrame(-2, 32.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 0.0f, 0.0f, this.wrapSearchInScrollView ? 0.0f : 48.0f, 0.0f));
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 21, 0.0f, 5.5f, 48.0f, 0.0f));
            }
            this.searchFilterLayout.setClipChildren(false);
            ImageView imageView = new ImageView(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.12
                @Override // android.widget.ImageView, android.view.View
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    clearAnimation();
                    Object tag = getTag();
                    ActionBarMenuItem actionBarMenuItem = ActionBarMenuItem.this;
                    if (tag == null) {
                        actionBarMenuItem.clearButton.setVisibility(4);
                        ActionBarMenuItem.this.clearButton.setAlpha(0.0f);
                        ActionBarMenuItem.this.clearButton.setRotation(45.0f);
                        ActionBarMenuItem.this.clearButton.setScaleX(0.0f);
                        ActionBarMenuItem.this.clearButton.setScaleY(0.0f);
                        return;
                    }
                    actionBarMenuItem.clearButton.setAlpha(1.0f);
                    ActionBarMenuItem.this.clearButton.setRotation(0.0f);
                    ActionBarMenuItem.this.clearButton.setScaleX(1.0f);
                    ActionBarMenuItem.this.clearButton.setScaleY(1.0f);
                }

                @Override // android.view.View
                public void draw(Canvas canvas) {
                    getBackground().draw(canvas);
                    super.draw(canvas);
                }
            };
            this.clearButton = imageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.13
                @Override // org.telegram.ui.Components.CloseProgressDrawable2
                public int getCurrentColor() {
                    return ActionBarMenuItem.this.parentMenu.parentActionBar.itemsColor;
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView.setImageDrawable(closeProgressDrawable2);
            this.clearButton.setBackground(Theme.createSelectorDrawable(this.parentMenu.parentActionBar.itemsActionModeBackgroundColor, 1));
            this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
            this.clearButton.setAlpha(0.0f);
            this.clearButton.setRotation(45.0f);
            this.clearButton.setScaleX(0.0f);
            this.clearButton.setScaleY(0.0f);
            this.clearButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda13
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$checkCreateSearchField$16(view);
                }
            });
            this.clearButton.setContentDescription(LocaleController.getString(R.string.ClearButton));
            if (this.wrapSearchInScrollView) {
                this.wrappedSearchFrameLayout.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
            } else {
                this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
            }
        }
    }

    public /* synthetic */ boolean lambda$checkCreateSearchField$15(TextView textView, int i, KeyEvent keyEvent) {
        if (keyEvent == null) {
            return false;
        }
        if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.searchField);
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener == null) {
            return false;
        }
        actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
        return false;
    }

    public /* synthetic */ void lambda$checkCreateSearchField$16(View view) {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        if (this.searchField.length() != 0) {
            this.searchField.setText(_UrlKt.FRAGMENT_ENCODE_SET);
        } else if (hasRemovableFilters()) {
            this.searchField.hideActionMode();
            for (int i = 0; i < this.currentSearchFilters.size(); i++) {
                if (this.listener != null && this.currentSearchFilters.get(i).removable) {
                    this.listener.onSearchFilterCleared(this.currentSearchFilters.get(i));
                }
            }
            clearSearchFilters();
        } else {
            TextView textView = this.searchFieldCaption;
            if (textView != null && textView.getVisibility() == 0 && ((actionBarMenuItemSearchListener = this.listener) == null || actionBarMenuItemSearchListener.canClearCaption())) {
                this.searchFieldCaption.setVisibility(8);
                ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
                if (actionBarMenuItemSearchListener2 != null) {
                    actionBarMenuItemSearchListener2.onCaptionCleared();
                }
            }
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    public View.OnClickListener getOnClickListener() {
        return this.onClickListener;
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        super.setOnClickListener(onClickListener);
    }

    public void checkClearButton() {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        TextView textView;
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2;
        if (this.clearButton != null) {
            boolean zHasRemovableFilters = hasRemovableFilters();
            Property property = View.ROTATION;
            Property property2 = View.SCALE_Y;
            Property property3 = View.SCALE_X;
            Property property4 = View.ALPHA;
            if (!zHasRemovableFilters && TextUtils.isEmpty(this.searchField.getText()) && (((actionBarMenuItemSearchListener = this.listener) == null || !actionBarMenuItemSearchListener.forceShowClear()) && ((textView = this.searchFieldCaption) == null || textView.getVisibility() != 0 || ((actionBarMenuItemSearchListener2 = this.listener) != null && !actionBarMenuItemSearchListener2.showClearForCaption())))) {
                if (this.clearButton.getTag() != null) {
                    this.clearButton.setTag(null);
                    AnimatorSet animatorSet = this.clearButtonAnimator;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    if (this.animateClear) {
                        AnimatorSet duration = new AnimatorSet().setDuration(180L);
                        duration.setInterpolator(new DecelerateInterpolator());
                        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda15
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                this.f$0.lambda$checkClearButton$17(valueAnimator);
                            }
                        });
                        duration.playTogether(ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) property4, 0.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) property3, 0.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) property2, 0.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) property, 45.0f), valueAnimatorOfFloat);
                        duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.14
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                ActionBarMenuItem.this.clearButton.setVisibility(4);
                                ActionBarMenuItem.this.clearButtonAnimator = null;
                            }
                        });
                        duration.start();
                        this.clearButtonAnimator = duration;
                        return;
                    }
                    this.clearButton.setAlpha(0.0f);
                    this.clearButton.setRotation(45.0f);
                    this.clearButton.setScaleX(0.0f);
                    this.clearButton.setScaleY(0.0f);
                    this.clearButton.setVisibility(4);
                    this.animateClear = true;
                    return;
                }
                return;
            }
            if (this.clearButton.getTag() == null) {
                this.clearButton.setTag(1);
                AnimatorSet animatorSet2 = this.clearButtonAnimator;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                this.clearButton.setVisibility(0);
                if (this.animateClear) {
                    AnimatorSet duration2 = new AnimatorSet().setDuration(180L);
                    duration2.setInterpolator(new DecelerateInterpolator());
                    ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(1.0f, 0.0f);
                    valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda16
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            this.f$0.lambda$checkClearButton$18(valueAnimator);
                        }
                    });
                    duration2.playTogether(ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) property4, 1.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) property3, 1.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) property, 0.0f), valueAnimatorOfFloat2);
                    duration2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.15
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            ActionBarMenuItem.this.clearButtonAnimator = null;
                        }
                    });
                    duration2.start();
                    this.clearButtonAnimator = duration2;
                    return;
                }
                this.clearButton.setAlpha(1.0f);
                this.clearButton.setRotation(0.0f);
                this.clearButton.setScaleX(1.0f);
                this.clearButton.setScaleY(1.0f);
                View view = this.searchAdditionalButton;
                if (view != null) {
                    view.setTranslationX(0.0f);
                }
                this.animateClear = true;
            }
        }
    }

    public /* synthetic */ void lambda$checkClearButton$17(ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.searchAdditionalButton;
        if (view != null) {
            view.setTranslationX(AndroidUtilities.dp(32.0f) * fFloatValue);
        }
    }

    public /* synthetic */ void lambda$checkClearButton$18(ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.searchAdditionalButton;
        if (view != null) {
            view.setTranslationX(AndroidUtilities.dp(32.0f) * fFloatValue);
        }
    }

    public boolean hasRemovableFilters() {
        if (this.currentSearchFilters.isEmpty()) {
            return false;
        }
        for (int i = 0; i < this.currentSearchFilters.size(); i++) {
            if (this.currentSearchFilters.get(i).removable) {
                return true;
            }
        }
        return false;
    }

    public void setShowSearchProgress(boolean z) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 == null) {
            return;
        }
        if (z) {
            closeProgressDrawable2.startAnimation();
        } else {
            closeProgressDrawable2.stopAnimation();
        }
    }

    public void setSearchFieldCaption(CharSequence charSequence) {
        if (this.searchFieldCaption == null) {
            return;
        }
        boolean zIsEmpty = TextUtils.isEmpty(charSequence);
        TextView textView = this.searchFieldCaption;
        if (zIsEmpty) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
            this.searchFieldCaption.setText(charSequence);
        }
    }

    public boolean isSearchField() {
        return this.isSearchField;
    }

    public void clearSearchText() {
        this.searchFieldText = null;
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor == null) {
            return;
        }
        editTextBoldCursor.setText(_UrlKt.FRAGMENT_ENCODE_SET);
    }

    public ActionBarMenuItem setActionBarMenuItemSearchListener(ActionBarMenuItemSearchListener actionBarMenuItemSearchListener) {
        this.listener = actionBarMenuItemSearchListener;
        return this;
    }

    public ActionBarMenuItem setAllowCloseAnimation(boolean z) {
        this.allowCloseAnimation = z;
        return this;
    }

    public void setPopupAnimationEnabled(boolean z) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.setAnimationEnabled(z);
        }
        this.animationEnabled = z;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            updateOrShowPopup(false, true);
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onLayout(i, i2, i3, i4);
        }
    }

    public void setAdditionalYOffset(int i) {
        this.additionalYOffset = i;
    }

    public void setAdditionalXOffset(int i) {
        this.additionalXOffset = i;
    }

    public void forceUpdatePopupPosition() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        updateOrShowPopup(true, true);
    }

    private void updateOrShowPopup(boolean z, boolean z2) {
        int top;
        int paddingTop;
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            top = (-actionBarMenu.parentActionBar.getMeasuredHeight()) + this.parentMenu.getTop();
            paddingTop = this.parentMenu.getPaddingTop();
        } else {
            float scaleY = getScaleY();
            top = -((int) ((getMeasuredHeight() * scaleY) - ((this.subMenuOpenSide != 2 ? getTranslationY() : 0.0f) / scaleY)));
            paddingTop = this.additionalYOffset;
        }
        int i = top + paddingTop + this.yOffset;
        if (z) {
            this.popupLayout.scrollToTop();
        }
        View view = this.showSubMenuFrom;
        if (view == null) {
            view = this;
        }
        ActionBarMenu actionBarMenu2 = this.parentMenu;
        if (actionBarMenu2 != null) {
            ActionBar actionBar = actionBarMenu2.parentActionBar;
            if (this.subMenuOpenSide == 0) {
                if (z) {
                    this.popupWindow.showAsDropDown(actionBar, (((view.getLeft() + this.parentMenu.getLeft()) + view.getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + ((int) getTranslationX()) + this.xOffset, i);
                }
                if (z2) {
                    this.popupWindow.update(actionBar, (((view.getLeft() + this.parentMenu.getLeft()) + view.getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + ((int) getTranslationX()) + this.xOffset, i, -1, -1);
                    return;
                }
                return;
            }
            if (z) {
                boolean z3 = this.forceSmoothKeyboard;
                ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
                if (z3) {
                    actionBarPopupWindow.showAtLocation(actionBar, 51, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()) + this.xOffset, i);
                } else {
                    actionBarPopupWindow.showAsDropDown(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()) + this.xOffset, i);
                }
            }
            if (z2) {
                this.popupWindow.update(actionBar, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()) + this.xOffset, i, -1, -1);
                return;
            }
            return;
        }
        int i2 = this.subMenuOpenSide;
        if (i2 == 0) {
            if (getParent() != null) {
                View view2 = (View) getParent();
                if (z) {
                    this.popupWindow.showAsDropDown(view2, ((getLeft() + getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset + this.xOffset, i);
                }
                if (z2) {
                    this.popupWindow.update(view2, ((getLeft() + getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset + this.xOffset, i, -1, -1);
                    return;
                }
                return;
            }
            return;
        }
        if (i2 == 1) {
            if (z) {
                this.popupWindow.showAsDropDown(this, (-AndroidUtilities.dp(8.0f)) + this.additionalXOffset + this.xOffset, i);
            }
            if (z2) {
                this.popupWindow.update(this, (-AndroidUtilities.dp(8.0f)) + this.additionalXOffset + this.xOffset, i, -1, -1);
                return;
            }
            return;
        }
        if (z) {
            this.popupWindow.showAsDropDown(this, (getMeasuredWidth() - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset + this.xOffset, i);
        }
        if (z2) {
            this.popupWindow.update(this, (getMeasuredWidth() - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset + this.xOffset, i, -1, -1);
        }
    }

    public void hideSubItem(int i) {
        View viewFindViewWithTag;
        Item itemFindLazyItem = findLazyItem(i);
        if (itemFindLazyItem != null) {
            itemFindLazyItem.setVisibility(8);
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null || (viewFindViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i))) == null || viewFindViewWithTag.getVisibility() == 8) {
            return;
        }
        viewFindViewWithTag.setVisibility(8);
        this.measurePopup = true;
    }

    public boolean hasSubItem(int i) {
        if (findLazyItem(i) != null) {
            return true;
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        return (actionBarPopupWindowLayout == null || actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i)) == null) ? false : true;
    }

    public void checkHideMenuItem() {
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= this.popupLayout.getItemsCount()) {
                i = 8;
                break;
            } else if (this.popupLayout.getItemAt(i2).getVisibility() == 0) {
                break;
            } else {
                i2++;
            }
        }
        if (i != getVisibility()) {
            setVisibility(i);
        }
    }

    public boolean isSubItemVisible(int i) {
        View viewFindViewWithTag;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        return (actionBarPopupWindowLayout == null || (viewFindViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i))) == null || viewFindViewWithTag.getVisibility() != 0) ? false : true;
    }

    public void showSubItem(int i) {
        showSubItem(i, false);
    }

    public void showSubItem(int i, boolean z) {
        View viewFindViewWithTag;
        Item itemFindLazyItem = findLazyItem(i);
        if (itemFindLazyItem != null) {
            itemFindLazyItem.setVisibility(0);
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null || (viewFindViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i))) == null || viewFindViewWithTag.getVisibility() == 0) {
            return;
        }
        viewFindViewWithTag.setAlpha(0.0f);
        viewFindViewWithTag.animate().alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(150L).start();
        viewFindViewWithTag.setVisibility(0);
        this.measurePopup = true;
    }

    public void setSubItemShown(int i, boolean z) {
        if (z) {
            showSubItem(i);
        } else {
            hideSubItem(i);
        }
    }

    public int getVisibleSubItemsCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.popupLayout.getItemsCount(); i2++) {
            View itemAt = this.popupLayout.getItemAt(i2);
            if (itemAt != null && itemAt.getVisibility() == 0) {
                i++;
            }
        }
        return i;
    }

    public void requestFocusOnSearchView() {
        if (this.searchContainer.getWidth() == 0 || this.searchField.isFocused()) {
            return;
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    public void clearFocusOnSearchView() {
        this.searchField.clearFocus();
        AndroidUtilities.hideKeyboard(this.searchField);
    }

    public FrameLayout getSearchContainer() {
        return this.searchContainer;
    }

    public ImageView getSearchClearButton() {
        return this.clearButton;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.iconView != null) {
            accessibilityNodeInfo.setClassName("android.widget.ImageButton");
        } else if (this.textView != null) {
            accessibilityNodeInfo.setClassName("android.widget.Button");
            if (TextUtils.isEmpty(accessibilityNodeInfo.getText())) {
                accessibilityNodeInfo.setText(this.textView.getText());
            }
        }
    }

    public void updateColor() {
        if (this.searchFilterLayout != null) {
            for (int i = 0; i < this.searchFilterLayout.getChildCount(); i++) {
                if (this.searchFilterLayout.getChildAt(i) instanceof SearchFilterView) {
                    ((SearchFilterView) this.searchFilterLayout.getChildAt(i)).updateColors();
                }
            }
        }
        if (this.popupLayout != null) {
            for (int i2 = 0; i2 < this.popupLayout.getItemsCount(); i2++) {
                if (this.popupLayout.getItemAt(i2) instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) this.popupLayout.getItemAt(i2)).setSelectorColor(getThemedColor(Theme.key_dialogButtonSelector));
                }
            }
        }
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor != null) {
            int i3 = Theme.key_actionBarDefaultSearch;
            editTextBoldCursor.setCursorColor(getThemedColor(i3));
            this.searchField.setHintTextColor(getThemedColor(Theme.key_actionBarDefaultSearchPlaceholder));
            this.searchField.setTextColor(getThemedColor(i3));
            this.searchField.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
            this.searchField.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
        }
    }

    public void setTransitionOffset(float f) {
        this.transitionOffset = f;
        setTranslationX(0.0f);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    @SuppressLint({"ViewConstructor"})
    public static class ReactionFilterView extends SearchFilterView {
        private boolean attached;
        private ReactionsLayoutInBubble.ReactionButton reactionButton;

        public ReactionFilterView(Context context, Theme.ResourcesProvider resourcesProvider, boolean z) {
            super(context, resourcesProvider, z);
            removeAllViews();
            setBackground(null);
            setWillNotDraw(false);
        }

        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.SearchFilterView
        public void setData(FiltersView.MediaFilterData mediaFilterData) {
            TLRPC.TL_reactionCount tL_reactionCount = new TLRPC.TL_reactionCount();
            tL_reactionCount.count = 1;
            tL_reactionCount.reaction = mediaFilterData.reaction.toTLReaction();
            ReactionsLayoutInBubble.ReactionButton reactionButton = new ReactionsLayoutInBubble.ReactionButton(null, UserConfig.selectedAccount, this, tL_reactionCount, false, true, this.resourcesProvider) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.ReactionFilterView.1
                @Override // org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.ReactionButton
                public int getCacheType() {
                    return 9;
                }

                @Override // org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.ReactionButton
                public void updateColors(float f) {
                    this.lastDrawnBackgroundColor = ColorUtils.blendARGB(this.fromBackgroundColor, Theme.getColor(Theme.key_chat_inReactionButtonBackground, ReactionFilterView.this.resourcesProvider), f);
                    this.lastDrawnTagDotColor = ColorUtils.blendARGB(this.fromTagDotColor, 1526726655, f);
                }
            };
            this.reactionButton = reactionButton;
            reactionButton.isTag = true;
            reactionButton.width = AndroidUtilities.dp(44.33f);
            this.reactionButton.height = AndroidUtilities.dp(28.0f);
            ReactionsLayoutInBubble.ReactionButton reactionButton2 = this.reactionButton;
            reactionButton2.choosen = true;
            if (this.attached) {
                reactionButton2.attach();
            }
        }

        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.SearchFilterView, android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(49.0f), AndroidUtilities.dp(32.0f));
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            ReactionsLayoutInBubble.ReactionButton reactionButton = this.reactionButton;
            if (reactionButton != null) {
                reactionButton.draw(canvas, ((getWidth() - AndroidUtilities.dp(4.0f)) - this.reactionButton.width) / 2.0f, (getHeight() - this.reactionButton.height) / 2.0f, 1.0f, 1.0f, false, false, 0.0f);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.attached) {
                return;
            }
            ReactionsLayoutInBubble.ReactionButton reactionButton = this.reactionButton;
            if (reactionButton != null) {
                reactionButton.attach();
            }
            this.attached = true;
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.attached) {
                ReactionsLayoutInBubble.ReactionButton reactionButton = this.reactionButton;
                if (reactionButton != null) {
                    reactionButton.detach();
                }
                this.attached = false;
            }
        }
    }

    @SuppressLint({"ViewConstructor"})
    public static class SearchFilterView extends FrameLayout implements FactorAnimator.Target {
        private final BoolAnimator animatorIsSelected;
        BackupImageView avatarImageView;
        ImageView closeIconView;
        FiltersView.MediaFilterData data;
        private boolean glass;
        private boolean isCommunity;
        private int mBackgroundColor;
        private int mBackgroundRadius;
        Runnable removeSelectionRunnable;
        protected final Theme.ResourcesProvider resourcesProvider;
        Drawable thumbDrawable;
        TextView titleView;
        private final boolean whiteBg;

        public /* synthetic */ void lambda$new$0() {
            setSelectedForDelete(false);
        }

        public SearchFilterView(Context context, Theme.ResourcesProvider resourcesProvider, boolean z) {
            super(context);
            this.animatorIsSelected = new BoolAnimator(0, this, CubicBezierInterpolator.EASE_OUT_QUINT, 380L);
            this.removeSelectionRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$SearchFilterView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$0();
                }
            };
            this.resourcesProvider = resourcesProvider;
            this.whiteBg = z;
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(32, 32.0f));
            ImageView imageView = new ImageView(context);
            this.closeIconView = imageView;
            imageView.setImageResource(R.drawable.ic_close_white);
            addView(this.closeIconView, LayoutHelper.createFrame(24, 24.0f, 16, 8.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setSingleLine();
            this.titleView.setEllipsize(TextUtils.TruncateAt.END);
            this.titleView.setTextSize(1, 14.0f);
            addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 16, 38.0f, 0.0f, 12.0f, 0.0f));
            this.mBackgroundRadius = AndroidUtilities.dp(28.0f);
            updateColors();
        }

        public void setGlass() {
            this.glass = true;
            updateColors();
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            if (this.isCommunity) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(135.0f), Integer.MIN_VALUE), i2);
            } else {
                super.onMeasure(i, i2);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            float width = getWidth();
            float height = getHeight();
            int i = this.mBackgroundRadius;
            canvas.drawRoundRect(0.0f, 0.0f, width, height, i, i, Theme.fillingPaint(this.mBackgroundColor));
            super.dispatchDraw(canvas);
        }

        public void updateColors() {
            int themedColor;
            float floatValue = this.animatorIsSelected.getFloatValue();
            if (this.glass) {
                themedColor = Theme.multAlpha(getThemedColor(Theme.key_windowBackgroundWhiteBlackText), 0.075f);
            } else {
                themedColor = getThemedColor(this.whiteBg ? Theme.key_windowBackgroundWhite : Theme.key_groupcreate_spanBackground);
            }
            int i = Theme.key_featuredStickers_addButton;
            int themedColor2 = getThemedColor(i);
            int themedColor3 = getThemedColor(Theme.key_windowBackgroundWhiteBlackText);
            int i2 = Theme.key_featuredStickers_buttonText;
            int themedColor4 = getThemedColor(i2);
            this.mBackgroundColor = ColorUtils.blendARGB(themedColor, themedColor2, floatValue);
            this.titleView.setTextColor(ColorUtils.blendARGB(themedColor3, themedColor4, floatValue));
            this.closeIconView.setColorFilter(themedColor4);
            this.closeIconView.setAlpha(floatValue);
            float f = 0.82f * floatValue;
            this.closeIconView.setScaleX(f);
            this.closeIconView.setScaleY(f);
            Drawable drawable = this.thumbDrawable;
            if (drawable != null) {
                Theme.setCombinedDrawableColor(drawable, getThemedColor(i), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(i2), true);
            }
            this.avatarImageView.setAlpha(1.0f - floatValue);
            FiltersView.MediaFilterData mediaFilterData = this.data;
            if (mediaFilterData != null && mediaFilterData.filterType == 7) {
                setData(mediaFilterData);
            }
            invalidate();
        }

        public boolean isSelectedForDelete() {
            return this.animatorIsSelected.getValue();
        }

        public void setData(FiltersView.MediaFilterData mediaFilterData) {
            this.data = mediaFilterData;
            this.isCommunity = false;
            this.titleView.setText(mediaFilterData.getTitle());
            CombinedDrawable combinedDrawableCreateCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), mediaFilterData.iconResFilled);
            this.thumbDrawable = combinedDrawableCreateCircleDrawableWithIcon;
            int i = Theme.key_featuredStickers_addButton;
            Theme.setCombinedDrawableColor(combinedDrawableCreateCircleDrawableWithIcon, getThemedColor(i), false);
            Drawable drawable = this.thumbDrawable;
            int i2 = Theme.key_featuredStickers_buttonText;
            Theme.setCombinedDrawableColor(drawable, getThemedColor(i2), true);
            int i3 = mediaFilterData.filterType;
            if (i3 != 4) {
                if (i3 == 7) {
                    CombinedDrawable combinedDrawableCreateCircleDrawableWithIcon2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_archive);
                    combinedDrawableCreateCircleDrawableWithIcon2.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                    Theme.setCombinedDrawableColor(combinedDrawableCreateCircleDrawableWithIcon2, getThemedColor(i), false);
                    Theme.setCombinedDrawableColor(combinedDrawableCreateCircleDrawableWithIcon2, getThemedColor(i2), true);
                    this.avatarImageView.setImageDrawable(combinedDrawableCreateCircleDrawableWithIcon2);
                    return;
                }
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
                return;
            }
            TLObject tLObject = mediaFilterData.chat;
            if (tLObject instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) tLObject;
                if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == user.id) {
                    CombinedDrawable combinedDrawableCreateCircleDrawableWithIcon3 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_saved);
                    combinedDrawableCreateCircleDrawableWithIcon3.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                    Theme.setCombinedDrawableColor(combinedDrawableCreateCircleDrawableWithIcon3, getThemedColor(i), false);
                    Theme.setCombinedDrawableColor(combinedDrawableCreateCircleDrawableWithIcon3, getThemedColor(i2), true);
                    this.avatarImageView.setImageDrawable(combinedDrawableCreateCircleDrawableWithIcon3);
                    return;
                }
                this.avatarImageView.getImageReceiver().setRoundRadius(ExteraConfig.getAvatarCorners(32.0f));
                this.avatarImageView.getImageReceiver().setForUserOrChat(user, this.thumbDrawable);
                return;
            }
            if (tLObject instanceof TLRPC.Chat) {
                TLRPC.Chat chat = (TLRPC.Chat) tLObject;
                this.isCommunity = ChatObject.isCommunity(chat);
                ImageReceiver imageReceiver = this.avatarImageView.getImageReceiver();
                int avatarCorners = ExteraConfig.getAvatarCorners(32.0f, false, this.isCommunity ? AvatarCornerType.COMMUNITY : AvatarCornerType.DEFAULT);
                this.mBackgroundRadius = avatarCorners;
                imageReceiver.setRoundRadius(avatarCorners);
                this.avatarImageView.getImageReceiver().setForUserOrChat(chat, this.thumbDrawable);
            }
        }

        public void setExpanded(boolean z) {
            TextView textView = this.titleView;
            if (z) {
                textView.setVisibility(0);
            } else {
                textView.setVisibility(8);
                setSelectedForDelete(false);
            }
        }

        public void setSelectedForDelete(boolean z) {
            if (this.animatorIsSelected.getValue() == z) {
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.removeSelectionRunnable);
            this.animatorIsSelected.setValue(z, true);
            if (z) {
                AndroidUtilities.runOnUIThread(this.removeSelectionRunnable, 2000L);
            }
        }

        public FiltersView.MediaFilterData getFilter() {
            return this.data;
        }

        public int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        @Override // me.vkryl.android.animator.FactorAnimator.Target
        public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
            if (i == 0) {
                updateColors();
                invalidate();
            }
        }
    }

    public ActionBarPopupWindow.GapView addColoredGap() {
        return addColoredGap(-1);
    }

    public ActionBarPopupWindow.GapView addColoredGap(int i) {
        createPopupLayout();
        ActionBarPopupWindow.GapView gapView = new ActionBarPopupWindow.GapView(getContext(), this.resourcesProvider, Theme.key_actionBarDefaultSubmenuSeparator);
        if (i != -1) {
            gapView.setTag(Integer.valueOf(i));
        }
        gapView.setTag(R.id.fit_width_tag, 1);
        this.popupLayout.addView((View) gapView, LayoutHelper.createLinear(-1, 8));
        return gapView;
    }

    public static class Item {
        public LinearLayout.LayoutParams customLayoutParams;
        public View customView;
        public boolean dismiss;
        public int icon;
        private Integer iconColor;
        public Drawable iconDrawable;
        public int id;
        public boolean needCheck;
        private View.OnClickListener overrideClickListener;
        private View.OnLongClickListener overrideLongClickListener;
        public CharSequence text;
        private Integer textColor;
        public int textSizeDp;
        private View view;
        public View viewToSwipeBack;
        public int viewType;
        private int visibility = 0;
        private int rightIconVisibility = 0;

        private Item(int i) {
            this.viewType = i;
        }

        public static Item asSubItem(int i, int i2, Drawable drawable, CharSequence charSequence, boolean z, boolean z2) {
            Item item = new Item(0);
            item.id = i;
            item.icon = i2;
            item.iconDrawable = drawable;
            item.text = charSequence;
            item.dismiss = z;
            item.needCheck = z2;
            return item;
        }

        public static Item asColoredGap() {
            return new Item(1);
        }

        public static Item asCustom(View view, LinearLayout.LayoutParams layoutParams) {
            Item item = new Item(3);
            item.customView = view;
            item.customLayoutParams = layoutParams;
            return item;
        }

        public static Item asSwipeBackItem(int i, Drawable drawable, String str, View view) {
            Item item = new Item(2);
            item.icon = i;
            item.iconDrawable = drawable;
            item.text = str;
            item.viewToSwipeBack = view;
            return item;
        }

        public static Item asText(CharSequence charSequence, int i) {
            Item item = new Item(4);
            item.text = charSequence;
            item.textSizeDp = i;
            return item;
        }

        public /* synthetic */ void lambda$add$0(ActionBarMenuItem actionBarMenuItem, View view) {
            if (actionBarMenuItem.popupWindow != null && actionBarMenuItem.popupWindow.isShowing() && this.dismiss) {
                if (actionBarMenuItem.processedPopupClick) {
                    return;
                }
                actionBarMenuItem.processedPopupClick = true;
                actionBarMenuItem.popupWindow.dismiss(actionBarMenuItem.allowCloseAnimation);
            }
            if (actionBarMenuItem.parentMenu != null) {
                actionBarMenuItem.parentMenu.onItemClick(((Integer) view.getTag()).intValue());
            } else if (actionBarMenuItem.delegate != null) {
                actionBarMenuItem.delegate.onItemClick(((Integer) view.getTag()).intValue());
            }
        }

        public View add(final ActionBarMenuItem actionBarMenuItem) {
            actionBarMenuItem.createPopupLayout();
            if (this.view != null) {
                actionBarMenuItem.popupLayout.addView(this.view);
            } else {
                int i = this.viewType;
                if (i == 0) {
                    ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(actionBarMenuItem.getContext(), this.needCheck, false, false, actionBarMenuItem.resourcesProvider);
                    actionBarMenuSubItem.setTextAndIcon(this.text, this.icon, this.iconDrawable);
                    actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
                    actionBarMenuSubItem.setTag(Integer.valueOf(this.id));
                    actionBarMenuItem.popupLayout.addView(actionBarMenuSubItem);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) actionBarMenuSubItem.getLayoutParams();
                    if (LocaleController.isRTL) {
                        layoutParams.gravity = 5;
                    }
                    layoutParams.width = -1;
                    layoutParams.height = AndroidUtilities.dp(48.0f);
                    actionBarMenuSubItem.setLayoutParams(layoutParams);
                    actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$Item$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            this.f$0.lambda$add$0(actionBarMenuItem, view);
                        }
                    });
                    Integer num = this.textColor;
                    if (num != null && this.iconColor != null) {
                        actionBarMenuSubItem.setColors(num.intValue(), this.iconColor.intValue());
                    }
                    this.view = actionBarMenuSubItem;
                } else if (i == 1) {
                    ActionBarPopupWindow.GapView gapView = new ActionBarPopupWindow.GapView(actionBarMenuItem.getContext(), actionBarMenuItem.resourcesProvider, Theme.key_actionBarDefaultSubmenuSeparator);
                    gapView.setTag(R.id.fit_width_tag, 1);
                    actionBarMenuItem.popupLayout.addView((View) gapView, LayoutHelper.createLinear(-1, 8));
                    this.view = gapView;
                } else if (i == 3) {
                    actionBarMenuItem.popupLayout.addView(this.customView, this.customLayoutParams);
                    this.view = this.customView;
                } else if (i == 2) {
                    final ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(actionBarMenuItem.getContext(), false, false, false, actionBarMenuItem.resourcesProvider);
                    actionBarMenuSubItem2.setTextAndIcon(this.text, this.icon, this.iconDrawable);
                    actionBarMenuSubItem2.setMinimumWidth(AndroidUtilities.dp(196.0f));
                    actionBarMenuSubItem2.setRightIcon(R.drawable.msg_arrowright);
                    actionBarMenuSubItem2.getRightIcon().setVisibility(this.rightIconVisibility);
                    actionBarMenuItem.popupLayout.addView(actionBarMenuSubItem2);
                    LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) actionBarMenuSubItem2.getLayoutParams();
                    if (LocaleController.isRTL) {
                        layoutParams2.gravity = 5;
                    }
                    layoutParams2.width = -1;
                    layoutParams2.height = AndroidUtilities.dp(48.0f);
                    actionBarMenuSubItem2.setLayoutParams(layoutParams2);
                    final int iAddViewToSwipeBack = actionBarMenuItem.popupLayout.addViewToSwipeBack(this.viewToSwipeBack);
                    actionBarMenuSubItem2.openSwipeBackLayout = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$Item$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ActionBarMenuItem.Item.m5675$r8$lambda$XCtaINqCCgAfz9UMfY_1BLwZN8(actionBarMenuItem, iAddViewToSwipeBack);
                        }
                    };
                    actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$Item$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            actionBarMenuSubItem2.openSwipeBack();
                        }
                    });
                    actionBarMenuItem.popupLayout.swipeBackGravityRight = true;
                    Integer num2 = this.textColor;
                    if (num2 != null && this.iconColor != null) {
                        actionBarMenuSubItem2.setColors(num2.intValue(), this.iconColor.intValue());
                    }
                    this.view = actionBarMenuSubItem2;
                } else if (i == 4) {
                    LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(actionBarMenuItem.getContext());
                    linksTextView.setTag(R.id.fit_width_tag, 1);
                    linksTextView.setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
                    linksTextView.setTextSize(1, this.textSizeDp);
                    linksTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
                    linksTextView.setMovementMethod(LinkMovementMethod.getInstance());
                    linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
                    linksTextView.setText(this.text);
                    linksTextView.setMaxWidth(AndroidUtilities.dp(200.0f));
                    actionBarMenuItem.popupLayout.addView((View) linksTextView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 8, 0, 0));
                    this.view = linksTextView;
                }
            }
            View view = this.view;
            if (view != null) {
                view.setVisibility(this.visibility);
                View.OnClickListener onClickListener = this.overrideClickListener;
                if (onClickListener != null) {
                    this.view.setOnClickListener(onClickListener);
                }
                View.OnLongClickListener onLongClickListener = this.overrideLongClickListener;
                if (onLongClickListener != null) {
                    this.view.setOnLongClickListener(onLongClickListener);
                }
            }
            return this.view;
        }

        public static /* synthetic */ void m5675$r8$lambda$XCtaINqCCgAfz9UMfY_1BLwZN8(ActionBarMenuItem actionBarMenuItem, int i) {
            if (actionBarMenuItem.popupLayout.getSwipeBack() != null) {
                actionBarMenuItem.popupLayout.getSwipeBack().openForeground(i);
            }
        }

        public void setVisibility(int i) {
            this.visibility = i;
            View view = this.view;
            if (view != null) {
                view.setVisibility(i);
            }
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.overrideClickListener = onClickListener;
            View view = this.view;
            if (view != null) {
                view.setOnClickListener(onClickListener);
            }
        }

        public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
            this.overrideLongClickListener = onLongClickListener;
            View view = this.view;
            if (view != null) {
                view.setOnLongClickListener(onLongClickListener);
            }
        }

        public void openSwipeBack() {
            View view = this.view;
            if (view instanceof ActionBarMenuSubItem) {
                ((ActionBarMenuSubItem) view).openSwipeBack();
            }
        }

        public void setText(CharSequence charSequence) {
            this.text = charSequence;
            View view = this.view;
            if (view instanceof ActionBarMenuSubItem) {
                ((ActionBarMenuSubItem) view).setText(charSequence);
            } else if (view instanceof TextView) {
                ((TextView) view).setText(charSequence);
            }
        }

        public void setIcon(int i) {
            if (i != this.icon) {
                this.icon = i;
                View view = this.view;
                if (view instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) view).setIcon(i);
                }
            }
        }

        public void setRightIconVisibility(int i) {
            if (this.rightIconVisibility != i) {
                this.rightIconVisibility = i;
                View view = this.view;
                if (view instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) view).getRightIcon().setVisibility(this.rightIconVisibility);
                }
            }
        }

        public void setColors(int i, int i2) {
            Integer num = this.textColor;
            if (num == null || this.iconColor == null || num.intValue() != i || this.iconColor.intValue() != i2) {
                this.textColor = Integer.valueOf(i);
                this.iconColor = Integer.valueOf(i2);
                View view = this.view;
                if (view instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) view).setColors(i, i2);
                }
            }
        }
    }

    public Item lazilyAddSwipeBackItem(int i, Drawable drawable, String str, View view) {
        return putLazyItem(Item.asSwipeBackItem(i, drawable, str, view));
    }

    public Item lazilyAddSubItem(int i, int i2, CharSequence charSequence) {
        return lazilyAddSubItem(i, i2, null, charSequence, true, false);
    }

    public Item lazilyAddSubItem(int i, int i2, Drawable drawable, CharSequence charSequence, boolean z, boolean z2) {
        return putLazyItem(Item.asSubItem(i, i2, drawable, charSequence, z, z2));
    }

    public Item lazilyAddColoredGap() {
        return putLazyItem(Item.asColoredGap());
    }

    public Item lazilyAddView(View view, LinearLayout.LayoutParams layoutParams) {
        return putLazyItem(Item.asCustom(view, layoutParams));
    }

    public Item lazilyAddText(CharSequence charSequence, int i) {
        return putLazyItem(Item.asText(charSequence, i));
    }

    private Item putLazyItem(Item item) {
        if (item == null) {
            return null;
        }
        if (this.lazyList == null) {
            this.lazyList = new ArrayList<>();
        }
        this.lazyList.add(item);
        if (this.lazyMap == null) {
            this.lazyMap = new HashMap<>();
        }
        this.lazyMap.put(Integer.valueOf(item.id), item);
        return item;
    }

    private Item findLazyItem(int i) {
        HashMap<Integer, Item> map = this.lazyMap;
        if (map == null) {
            return null;
        }
        return map.get(Integer.valueOf(i));
    }

    private void layoutLazyItems() {
        if (this.lazyList == null) {
            return;
        }
        int i = 0;
        while (true) {
            int size = this.lazyList.size();
            ArrayList<Item> arrayList = this.lazyList;
            if (i < size) {
                arrayList.get(i).add(this);
                i++;
            } else {
                arrayList.clear();
                return;
            }
        }
    }

    public static ActionBarMenuSubItem addItem(ViewGroup viewGroup, int i, CharSequence charSequence, boolean z, Theme.ResourcesProvider resourcesProvider) {
        return addItem(false, false, viewGroup, i, charSequence, z, resourcesProvider);
    }

    public static ActionBarMenuSubItem addItem(boolean z, boolean z2, ViewGroup viewGroup, int i, CharSequence charSequence, boolean z3, Theme.ResourcesProvider resourcesProvider) {
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(viewGroup.getContext(), z3, z, z2, resourcesProvider);
        actionBarMenuSubItem.setTextAndIcon(charSequence, i);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        viewGroup.addView(actionBarMenuSubItem);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) actionBarMenuSubItem.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        actionBarMenuSubItem.setLayoutParams(layoutParams);
        return actionBarMenuSubItem;
    }
}
