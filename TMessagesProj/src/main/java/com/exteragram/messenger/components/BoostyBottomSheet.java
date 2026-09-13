package com.exteragram.messenger.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.api.dto.BoostySubscriberDTO;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public abstract class BoostyBottomSheet extends BottomSheet {
    private final int ITEM_HEIGHT;
    private final int PRIMARY_COLOR;
    private final Runnable autoScrollRunnable;
    public ButtonWithCounterView buttonView;
    private int contentHeight;
    private int currentAutoScrollPosition;
    public LinkSpanDrawable.LinksTextView descriptionView;
    private boolean isUserScrolling;
    private final RecyclerListView listView;
    private Runnable resumeScrollRunnable;
    private final LinearSnapHelper snapHelper;
    private final StarParticlesView.Drawable starDrawable;
    public TextView titleView;
    public FrameLayout topView;

    public abstract void onButtonClick();

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
    public BoostyBottomSheet(final Context context, final List<BoostySubscriberDTO> list) {
        super(context, false);
        this.ITEM_HEIGHT = AndroidUtilities.dp(48.0f);
        this.PRIMARY_COLOR = -830418;
        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        this.snapHelper = linearSnapHelper;
        Runnable runnable = new Runnable() { // from class: com.exteragram.messenger.components.BoostyBottomSheet.1
            @Override // java.lang.Runnable
            public void run() {
                LinearLayoutManager linearLayoutManager;
                if (BoostyBottomSheet.this.listView == null || BoostyBottomSheet.this.isUserScrolling || BoostyBottomSheet.this.listView.getAdapter() == null) {
                    return;
                }
                boolean isWrap = false;
                if (BoostyBottomSheet.this.listView.getAdapter().getItemCount() > 1 && (linearLayoutManager = (LinearLayoutManager) BoostyBottomSheet.this.listView.getLayoutManager()) != null) {
                    BoostyBottomSheet.this.currentAutoScrollPosition++;
                    if (BoostyBottomSheet.this.currentAutoScrollPosition >= BoostyBottomSheet.this.listView.getAdapter().getItemCount()) {
                        BoostyBottomSheet.this.currentAutoScrollPosition = 0;
                        isWrap = true;
                    }
                    final boolean z = isWrap;
                    LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(BoostyBottomSheet.this.listView.getContext()) { // from class: com.exteragram.messenger.components.BoostyBottomSheet.1.1
                        @Override // androidx.recyclerview.widget.LinearSmoothScroller
                        public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                            return super.calculateSpeedPerPixel(displayMetrics) * (z ? 0.6f : 12.5f);
                        }

                        @Override // androidx.recyclerview.widget.LinearSmoothScroller
                        public int calculateDtToFit(int i, int i2, int i3, int i4, int i5) {
                            return (i3 + ((i4 - i3) / 2)) - (i + ((i2 - i) / 2));
                        }
                    };
                    linearSmoothScroller.setTargetPosition(BoostyBottomSheet.this.currentAutoScrollPosition);
                    linearLayoutManager.startSmoothScroll(linearSmoothScroller);
                    BoostyBottomSheet.this.listView.removeCallbacks(this);
                    BoostyBottomSheet.this.listView.postDelayed(this, z ? 3000L : 1500L);
                }
            }
        };
        this.autoScrollRunnable = runnable;
        int i = Theme.isCurrentThemeDark() ? -15198184 : -657931;
        setBackgroundColor(i);
        fixNavigationBar(i);
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        this.useBackgroundTopPadding = false;
        final Paint paint = new Paint(1);
        paint.setColor(-830418);
        StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(300);
        this.starDrawable = drawable;
        drawable.color = 1627389951;
        drawable.size1 = 8;
        drawable.size2 = 6;
        drawable.size3 = 4;
        drawable.k3 = 0.98f;
        drawable.k2 = 0.98f;
        drawable.k1 = 0.98f;
        drawable.useRotate = true;
        drawable.speedScale = 2.0f;
        drawable.checkBounds = true;
        drawable.checkTime = true;
        drawable.useBlur = true;
        drawable.roundEffect = false;
        drawable.init();
        FrameLayout frameLayout = new FrameLayout(context) { // from class: com.exteragram.messenger.components.BoostyBottomSheet.2
            private final Path path = new Path();
            private final RectF rectF = new RectF();

            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i2, int i3) {
                boolean zIsTablet = AndroidUtilities.isTablet();
                BoostyBottomSheet boostyBottomSheet = BoostyBottomSheet.this;
                if (!zIsTablet) {
                    boolean z = boostyBottomSheet.isPortrait();
                    BoostyBottomSheet boostyBottomSheet2 = BoostyBottomSheet.this;
                    if (z) {
                        boostyBottomSheet2.contentHeight = (int) (View.MeasureSpec.getSize(i2) * 0.8f);
                    } else {
                        boostyBottomSheet2.contentHeight = (int) (View.MeasureSpec.getSize(i3) * 0.65f);
                    }
                } else {
                    boostyBottomSheet.contentHeight = (int) (View.MeasureSpec.getSize(i3) * 0.45f);
                }
                super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(BoostyBottomSheet.this.contentHeight + AndroidUtilities.dp(2.0f), TLObject.FLAG_30));
                this.rectF.set(0.0f, AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight() + AndroidUtilities.dp(18.0f));
                BoostyBottomSheet.this.starDrawable.rect.set(0.0f, AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight());
                BoostyBottomSheet.this.starDrawable.rect2.set(-AndroidUtilities.dp(15.0f), -AndroidUtilities.dp(15.0f), getMeasuredWidth() + AndroidUtilities.dp(15.0f), getMeasuredHeight() + AndroidUtilities.dp(15.0f));
                BoostyBottomSheet.this.starDrawable.resetPositions();
            }

            @Override // android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                float fDp = AndroidUtilities.dp(12.0f);
                this.rectF.set(0.0f, AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight() + AndroidUtilities.dp(18.0f));
                paint.setShader(new RadialGradient(this.rectF.centerX(), this.rectF.centerY() - AndroidUtilities.dp(9.0f), Math.max(this.rectF.width(), this.rectF.height()) / 2.0f, -830418, -2536663, Shader.TileMode.CLAMP));
                canvas.save();
                canvas.clipRect(0, AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight());
                float f = fDp - 1.0f;
                canvas.drawRoundRect(this.rectF, f, f, paint);
                BoostyBottomSheet.this.starDrawable.onDraw(canvas);
                invalidate();
                canvas.restore();
                this.path.reset();
                this.path.addRoundRect(this.rectF, f, f, Path.Direction.CW);
                canvas.save();
                canvas.clipPath(this.path);
                super.dispatchDraw(canvas);
                canvas.restore();
            }
        };
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.topView = frameLayout2;
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 2.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: com.exteragram.messenger.components.BoostyBottomSheet.3
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int i2, int i3) {
                int i4;
                int size = View.MeasureSpec.getSize(i3);
                if (size > 0 && getPaddingTop() != (i4 = (size - BoostyBottomSheet.this.ITEM_HEIGHT) / 2)) {
                    setPadding(0, i4, 0, i4);
                }
                super.onMeasure(i2, i3);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                boolean zOnTouchEvent = super.onTouchEvent(motionEvent);
                if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return zOnTouchEvent || motionEvent.getAction() == 0;
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        recyclerListView.setAdapter(new RecyclerView.Adapter<ViewHolder>() { // from class: com.exteragram.messenger.components.BoostyBottomSheet.4
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i2) {
                TextView textView = new TextView(context);
                textView.setTextSize(1, 22.0f);
                textView.setGravity(17);
                textView.setTextColor(-1);
                textView.setTypeface(AndroidUtilities.bold());
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
                textView.setMinHeight(BoostyBottomSheet.this.ITEM_HEIGHT);
                textView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new ViewHolder(textView);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(ViewHolder viewHolder, int i2) {
                viewHolder.textView.setText(((BoostySubscriberDTO) list.get(i2)).getName());
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return list.size();
            }
        });
        recyclerListView.setOnScrollListener(new AnonymousClass5());
        recyclerListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.exteragram.messenger.components.BoostyBottomSheet$$ExternalSyntheticLambda0
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
                BoostyBottomSheet.this.lambda$new$0(view, i2, i3, i4, i5, i6, i7, i8, i9);
            }
        });
        recyclerListView.setOverScrollMode(2);
        recyclerListView.setVerticalScrollBarEnabled(false);
        recyclerListView.setClipToPadding(false);
        recyclerListView.scrollToPosition(0);
        recyclerListView.postDelayed(runnable, 1500L);
        linearSnapHelper.attachToRecyclerView(recyclerListView);
        this.topView.addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 1, 0, 0, 0, 0));
        int i2 = Theme.isCurrentThemeDark() ? -1 : -16777216;
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setGravity(1);
        this.titleView.setTextColor(i2);
        this.titleView.setTextSize(1, 20.0f);
        this.titleView.setTypeface(AndroidUtilities.bold());
        this.titleView.setText(LocaleController.formatPluralString("BoostyPeopleCount", list.size(), new Object[0]));
        linearLayout.addView(this.titleView, LayoutHelper.createLinear(-2, -2, 1, 20, 24, 20, 0));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
        this.descriptionView = linksTextView;
        linksTextView.setGravity(1);
        this.descriptionView.setTextSize(1, 14.0f);
        this.descriptionView.setTextColor(i2);
        this.descriptionView.setText(AndroidUtilities.replaceTags(new SpannableStringBuilder(LocaleController.getString(R.string.BoostyInfo)).append((CharSequence) "\n").append((CharSequence) LocaleController.getString(R.string.BoostyInfo2))));
        linearLayout.addView(this.descriptionView, LayoutHelper.createLinear(-2, -2, 1, 20, 6, 20, 0));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, this.resourcesProvider);
        this.buttonView = buttonWithCounterView;
        buttonWithCounterView.setRound();
        SpannableStringBuilder spannableStringBuilderAppend = new SpannableStringBuilder(LocaleController.getString(R.string.Open)).append((CharSequence) "  Boosty");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.boosty_icon, 2);
        coloredImageSpan.setSize(AndroidUtilities.dp(20.0f));
        spannableStringBuilderAppend.setSpan(coloredImageSpan, spannableStringBuilderAppend.length() - 7, spannableStringBuilderAppend.length() - 6, 33);
        this.buttonView.setText(spannableStringBuilderAppend, false);
        this.buttonView.setTextColor(-1);
        this.buttonView.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.components.BoostyBottomSheet$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BoostyBottomSheet.this.lambda$new$1(view);
            }
        });
        this.buttonView.setColor(-830418, ColorUtils.setAlphaComponent(ColorUtils.blendARGB(-830418, 0, 0.2f), 51));
        linearLayout.addView(this.buttonView, LayoutHelper.createLinear(-1, 48, 0, 14, 24, 14, 14));
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.components.BoostyBottomSheet$5, reason: invalid class name */
    public class AnonymousClass5 extends RecyclerView.OnScrollListener {
        public AnonymousClass5() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            BoostyBottomSheet.this.updateScales();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1) {
                BoostyBottomSheet.this.isUserScrolling = true;
                BoostyBottomSheet.this.listView.removeCallbacks(BoostyBottomSheet.this.autoScrollRunnable);
                if (BoostyBottomSheet.this.resumeScrollRunnable != null) {
                    BoostyBottomSheet.this.listView.removeCallbacks(BoostyBottomSheet.this.resumeScrollRunnable);
                    return;
                }
                return;
            }
            if (i == 0 && BoostyBottomSheet.this.isUserScrolling) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) BoostyBottomSheet.this.listView.getLayoutManager();
                if (linearLayoutManager != null) {
                    View viewFindSnapView = BoostyBottomSheet.this.snapHelper.findSnapView(linearLayoutManager);
                    BoostyBottomSheet boostyBottomSheet = BoostyBottomSheet.this;
                    if (viewFindSnapView != null) {
                        boostyBottomSheet.currentAutoScrollPosition = linearLayoutManager.getPosition(viewFindSnapView);
                    } else {
                        boostyBottomSheet.currentAutoScrollPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    }
                }
                BoostyBottomSheet.this.resumeScrollRunnable = new Runnable() { // from class: com.exteragram.messenger.components.BoostyBottomSheet$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AnonymousClass5.this.lambda$onScrollStateChanged$0();
                    }
                };
                BoostyBottomSheet.this.listView.postDelayed(BoostyBottomSheet.this.resumeScrollRunnable, 3000L);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onScrollStateChanged$0() {
            BoostyBottomSheet.this.isUserScrolling = false;
            BoostyBottomSheet.this.listView.postDelayed(BoostyBottomSheet.this.autoScrollRunnable, 1500L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        updateScales();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        dismiss();
        onButtonClick();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean isTouchOutside(float f, float f2) {
        if (f2 < this.containerView.getTop() - this.backgroundPaddingTop || f2 > this.containerView.getBottom()) {
            return super.isTouchOutside(f, f2);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateScales() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null) {
            return;
        }
        int height = recyclerListView.getHeight() / 2;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            float fMin = Math.min(Math.abs(height - ((childAt.getTop() + childAt.getBottom()) / 2)) / (this.listView.getHeight() / 2.0f), 1.0f);
            float f = 1.0f - (fMin * 0.3f);
            childAt.setScaleX(f);
            childAt.setScaleY(f);
            childAt.setAlpha(((1.0f - fMin) * 0.7f) + 0.3f);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            this.textView = (TextView) view;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    public void dismiss() {
        super.dismiss();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.removeCallbacks(this.autoScrollRunnable);
            Runnable runnable = this.resumeScrollRunnable;
            if (runnable != null) {
                this.listView.removeCallbacks(runnable);
            }
        }
    }

    public boolean isPortrait() {
        return this.isPortrait;
    }
}
