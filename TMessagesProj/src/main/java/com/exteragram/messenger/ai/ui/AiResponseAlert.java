package com.exteragram.messenger.ai.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.data.Service;
import com.exteragram.messenger.ai.network.Client;
import com.exteragram.messenger.ai.network.GenerationCallback;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tables.TableTheme;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import java.util.ArrayList;
import java.util.regex.Matcher;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LinkifyPort;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.messenger.utils.DrawableUtils;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EllipsizeSpanAnimator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.TypingDotsDrawable;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public abstract class AiResponseAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private final PaddedAdapter adapter;
    private final Client client;
    private String currentRequestId;
    private CharSequence currentResponse;
    EllipsizeSpanAnimator ellipsizeSpanAnimator;
    private BaseFragment fragment;
    private final HeaderView headerView;
    private String imagePath;
    private final RecyclerListView listView;
    private final LoadingTextView loadingTextView;
    private final ButtonWithCounterView mainButton;
    private final Markwon markwon;
    private Utilities.Callback2<String, CharSequence> onInsertPress;
    private Utilities.CallbackReturn<URLSpan, Boolean> onLinkPress;
    private String prompt;
    private final AnimatedFloat sheetTopAnimated;
    private boolean sheetTopNotAnimate;
    private Spanned spannedPrompt;
    private final LinkSpanDrawable.LinksTextView textView;
    private final FrameLayout textViewContainer;
    private final ThinkingDotsView thinkingDotsView;
    private boolean thinkingVisible;
    private boolean useHistory;

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean canDismissWithSwipe() {
        return false;
    }

    private AiResponseAlert(Context context, final Client client, String str, String str2, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.client = client;
        this.imagePath = str2;
        this.useHistory = z;
        this.markwon = createMarkwon(context);
        this.backgroundPaddingLeft = 0;
        fixNavigationBar();
        ContainerView containerView = new ContainerView(context);
        this.containerView = containerView;
        this.sheetTopAnimated = new AnimatedFloat(containerView, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
        LoadingTextView loadingTextView = new LoadingTextView(context);
        this.loadingTextView = loadingTextView;
        loadingTextView.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(6.0f));
        loadingTextView.setTextSize(1, SharedConfig.fontSize);
        int i = Theme.key_dialogTextBlack;
        loadingTextView.setTextColor(getThemedColor(i));
        loadingTextView.setTypeface(AndroidUtilities.regular());
        loadingTextView.setLinkTextColor(Theme.multAlpha(getThemedColor(i), 0.2f));
        this.thinkingDotsView = new ThinkingDotsView(context, getThemedColor(i));
        setPrompt(str.trim());
        this.textViewContainer = new FrameLayout(context) { 
            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i2, int i3) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), TLObject.FLAG_30), i3);
            }
        };
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        this.textView = linksTextView;
        linksTextView.setDisablePaddingsOffsetY(true);
        linksTextView.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(6.0f));
        linksTextView.setTextSize(1, SharedConfig.fontSize);
        linksTextView.setTypeface(AndroidUtilities.regular());
        linksTextView.setTextColor(getThemedColor(i));
        linksTextView.setLinkTextColor(getThemedColor(Theme.key_chat_messageLinkIn));
        linksTextView.setTextIsSelectable(true);
        linksTextView.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
        int themedColor = getThemedColor(Theme.key_chat_TextSelectionCursor);
        try {
            if (Build.VERSION.SDK_INT >= 29 && !XiaomiUtilities.isMIUI()) {
                Drawable textSelectHandleLeft = linksTextView.getTextSelectHandleLeft();
                if (textSelectHandleLeft != null) {
                    textSelectHandleLeft.setColorFilter(themedColor, PorterDuff.Mode.SRC_IN);
                    linksTextView.setTextSelectHandleLeft(textSelectHandleLeft);
                }
                Drawable textSelectHandleRight = linksTextView.getTextSelectHandleRight();
                if (textSelectHandleRight != null) {
                    textSelectHandleRight.setColorFilter(themedColor, PorterDuff.Mode.SRC_IN);
                    linksTextView.setTextSelectHandleRight(textSelectHandleRight);
                }
            }
        } catch (Exception unused) {
        }
        this.textViewContainer.addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) { 
            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onRequestFocusInDescendants(int i2, Rect rect) {
                return true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.ViewParent
            public void requestChildFocus(View view, View view2) {
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && motionEvent.getY() < AiResponseAlert.this.getSheetTop() - getTop()) {
                    AiResponseAlert.this.lambda$new$0();
                    return true;
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setOverScrollMode(1);
        recyclerListView.setPadding(0, AndroidUtilities.statusBarHeight + AndroidUtilities.dp(56.0f), 0, AndroidUtilities.dp(80.0f));
        recyclerListView.setClipToPadding(true);
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        PaddedAdapter paddedAdapter = new PaddedAdapter(context, this.loadingTextView);
        this.adapter = paddedAdapter;
        recyclerListView.setAdapter(paddedAdapter);
        recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() { 
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                ((BottomSheet) AiResponseAlert.this).containerView.invalidate();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i2) {
                if (i2 == 0) {
                    AiResponseAlert.this.sheetTopNotAnimate = false;
                }
                if ((i2 == 0 || i2 == 2) && AiResponseAlert.this.getSheetTop(false) > 0.0f && AiResponseAlert.this.getSheetTop(false) < AndroidUtilities.dp(96.0f) && AiResponseAlert.this.listView.canScrollVertically(1) && AiResponseAlert.this.hasEnoughHeight()) {
                    AiResponseAlert.this.sheetTopNotAnimate = true;
                    AiResponseAlert.this.listView.smoothScrollBy(0, (int) AiResponseAlert.this.getSheetTop(false));
                }
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { 
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onChangeAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                ((BottomSheet) AiResponseAlert.this).containerView.invalidate();
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                ((BottomSheet) AiResponseAlert.this).containerView.invalidate();
            }
        };
        defaultItemAnimator.setDurations(180L);
        defaultItemAnimator.setInterpolator(new LinearInterpolator());
        recyclerListView.setItemAnimator(defaultItemAnimator);
        this.containerView.addView(recyclerListView, LayoutHelper.createFrame(-1, -2, 80));
        HeaderView headerView = new HeaderView(context);
        this.headerView = headerView;
        this.containerView.addView(headerView, LayoutHelper.createFrame(-1, 78, 55));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        this.mainButton = buttonWithCounterView;
        buttonWithCounterView.setRound();
        buttonWithCounterView.setColor(getThemedColor(Theme.key_featuredStickers_addButton));
        buttonWithCounterView.setText(LocaleController.getString(R.string.Close), false);
        buttonWithCounterView.setLoading(true);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$0(client, view);
            }
        });
        this.containerView.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 87, 16.0f, 16.0f, 16.0f, 16.0f));
    }

    public void lambda$new$0(View view) {
            AiResponseAlert.this.lambda$new$0();
        }

        public void lambda$new$4(GenerateFromMessageBottomSheet.GenerationData generationData) {
            AiResponseAlert.this.setPrompt(generationData.prompt());
            AiResponseAlert.this.imagePath = null;
            AiResponseAlert.this.useHistory = generationData.useHistory();
            AiResponseAlert.this.adapter.updateMainView(AiResponseAlert.this.loadingTextView);
            AiResponseAlert.this.updateMainButton(true);
            AiResponseAlert.this.generate();
        }

        public void lambda$openModelSelect$7(Runnable[] runnableArr, Service service, View view) {
            Runnable runnable = runnableArr[0];
            if (runnable != null) {
                runnable.run();
            }
            if (service.isSelected()) {
                return;
            }
            this.modelSelector.setText(service.getShortModel());
            AiResponseAlert.this.adapter.updateMainView(AiResponseAlert.this.loadingTextView);
            AiConfig.setSelectedServices(service);
            AiResponseAlert.this.updateMainButton(true);
            AiResponseAlert.this.generate();
        }

        @Override // android.view.View
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            float fClamp = MathUtils.clamp((f - AndroidUtilities.statusBarHeight) / AndroidUtilities.dp(64.0f), 0.0f, 1.0f);
            if (!AiResponseAlert.this.hasEnoughHeight()) {
                fClamp = 1.0f;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(fClamp);
            this.titleTextView.setScaleX(AndroidUtilities.lerp(0.85f, 1.0f, interpolation));
            this.titleTextView.setScaleY(AndroidUtilities.lerp(0.85f, 1.0f, interpolation));
            this.titleTextView.setTranslationY(AndroidUtilities.lerp(AndroidUtilities.dpf2(-12.0f), 0.0f, interpolation));
            this.titleTextView.setTranslationX(AndroidUtilities.lerp(AndroidUtilities.dpf2(50.0f), 0.0f, interpolation));
            this.subtitleView.setTranslationX(AndroidUtilities.lerp(AndroidUtilities.dpf2(50.0f), 0.0f, interpolation));
            this.subtitleView.setTranslationY(AndroidUtilities.lerp(AndroidUtilities.dpf2(-22.0f), 0.0f, interpolation));
            this.backButton.setTranslationX(AndroidUtilities.lerp(0.0f, AndroidUtilities.dpf2(-25.0f), interpolation));
            float f2 = 1.0f - interpolation;
            this.backButton.setAlpha(f2);
            this.insertButton.setTranslationX(AndroidUtilities.lerp(AndroidUtilities.dpf2(14.0f), AndroidUtilities.dpf2(8.0f), interpolation));
            this.insertButton.setTranslationY(AndroidUtilities.lerp(AndroidUtilities.dpf2(0.0f), AndroidUtilities.dpf2(16.0f), interpolation));
            ImageView imageView = this.insertButton;
            AiResponseAlert aiResponseAlert = AiResponseAlert.this;
            int i = Theme.key_dialogTextBlack;
            int themedColor = aiResponseAlert.getThemedColor(i);
            AiResponseAlert aiResponseAlert2 = AiResponseAlert.this;
            int i2 = Theme.key_player_actionBarSubtitle;
            imageView.setColorFilter(ColorUtils.blendARGB(themedColor, aiResponseAlert2.getThemedColor(i2), interpolation), PorterDuff.Mode.MULTIPLY);
            this.optionsButton.setTranslationX(AndroidUtilities.lerp(AndroidUtilities.dpf2(14.0f), AndroidUtilities.dpf2(8.0f), interpolation));
            this.optionsButton.setTranslationY(AndroidUtilities.lerp(AndroidUtilities.dpf2(0.0f), AndroidUtilities.dpf2(16.0f), interpolation));
            this.optionsButton.setIconColor(ColorUtils.blendARGB(AiResponseAlert.this.getThemedColor(i), AiResponseAlert.this.getThemedColor(i2), interpolation));
            this.shadow.setTranslationY(AndroidUtilities.lerp(0.0f, AndroidUtilities.dpf2(22.0f), interpolation));
            this.shadow.setAlpha(f2);
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0f), TLObject.FLAG_30));
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            EllipsizeSpanAnimator ellipsizeSpanAnimator = AiResponseAlert.this.ellipsizeSpanAnimator;
            if (ellipsizeSpanAnimator != null) {
                ellipsizeSpanAnimator.onAttachedToWindow();
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            EllipsizeSpanAnimator ellipsizeSpanAnimator = AiResponseAlert.this.ellipsizeSpanAnimator;
            if (ellipsizeSpanAnimator != null) {
                ellipsizeSpanAnimator.onDetachedFromWindow();
            }
        }
    }

    public class ContainerView extends FrameLayout {
        private final Paint bgPaint;
        private final Path bgPath;
        private Boolean lightStatusBarFull;

        public ContainerView(Context context) {
            super(context);
            this.bgPath = new Path();
            Paint paint = new Paint(1);
            this.bgPaint = paint;
            paint.setColor(AiResponseAlert.this.getThemedColor(Theme.key_dialogBackground));
            Theme.applyDefaultShadow(paint);
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            float sheetTop = AiResponseAlert.this.getSheetTop();
            float fLerp = AndroidUtilities.lerp(0, AndroidUtilities.dp(12.0f), MathUtils.clamp(sheetTop / AndroidUtilities.dpf2(24.0f), 0.0f, 1.0f));
            AiResponseAlert.this.headerView.setTranslationY(Math.max(AndroidUtilities.statusBarHeight, sheetTop));
            updateLightStatusBar(sheetTop <= ((float) AndroidUtilities.statusBarHeight) / 2.0f);
            this.bgPath.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, sheetTop, getWidth(), getHeight() + fLerp);
            this.bgPath.addRoundRect(rectF, fLerp, fLerp, Path.Direction.CW);
            canvas.drawPath(this.bgPath, this.bgPaint);
            super.dispatchDraw(canvas);
        }

        private void updateLightStatusBar(boolean z) {
            int iBlendOver;
            Boolean bool = this.lightStatusBarFull;
            if (bool == null || bool.booleanValue() != z) {
                this.lightStatusBarFull = Boolean.valueOf(z);
                Window window = AiResponseAlert.this.getWindow();
                AiResponseAlert aiResponseAlert = AiResponseAlert.this;
                if (z) {
                    iBlendOver = aiResponseAlert.getThemedColor(Theme.key_dialogBackground);
                } else {
                    iBlendOver = Theme.blendOver(aiResponseAlert.getThemedColor(Theme.key_actionBarDefault), 855638016);
                }
                AndroidUtilities.setLightStatusBar(window, AndroidUtilities.computePerceivedBrightness(iBlendOver) > 0.721f);
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), TLObject.FLAG_30));
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            Bulletin.addDelegate(this, new Bulletin.Delegate() { 
                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public int getBottomOffset(int i) {
                    return AndroidUtilities.dp(80.0f);
                }
            });
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Bulletin.removeDelegate(this);
        }
    }
}
