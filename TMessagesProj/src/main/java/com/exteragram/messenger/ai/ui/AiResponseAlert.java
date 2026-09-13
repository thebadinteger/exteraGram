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
        this.textViewContainer = new FrameLayout(context) { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.1
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
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.2
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
                    AiResponseAlert.this.dismiss();
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
        recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                AiResponseAlert.this.containerView.invalidate();
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
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.4
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onChangeAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                AiResponseAlert.this.containerView.invalidate();
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                AiResponseAlert.this.containerView.invalidate();
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
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AiResponseAlert.this.lambda$new$0(client, view);
            }
        });
        this.containerView.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 87, 16.0f, 16.0f, 16.0f, 16.0f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Client client, View view) {
        client.stopRequest(this.currentRequestId);
        dismiss();
    }

    private Markwon createMarkwon(Context context) {
        final int themedColor = getThemedColor(Theme.key_dialogTextBlack);
        final int themedColor2 = getThemedColor(Theme.key_dialogTextLink);
        int themedColor3 = getThemedColor(Theme.key_windowBackgroundGray);
        final int iBlendARGB = ColorUtils.blendARGB(themedColor3, themedColor, 0.04f);
        final int iBlendARGB2 = ColorUtils.blendARGB(themedColor3, themedColor, 0.06f);
        final int iMultAlpha = Theme.multAlpha(themedColor, 0.16f);
        final int iMultAlpha2 = Theme.multAlpha(themedColor, 0.08f);
        final int iMultAlpha3 = Theme.multAlpha(themedColor, 0.04f);
        return Markwon.builderNoCore(context).usePlugin(CorePlugin.create().hasExplicitMovementMethod(true)).usePlugin(new AbstractMarkwonPlugin() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.5
            @Override // io.noties.markwon.AbstractMarkwonPlugin, io.noties.markwon.MarkwonPlugin
            public void configureTheme(MarkwonTheme.Builder builder) {
                MarkwonTheme.Builder builderCodeBlockTextSize = builder.linkColor(themedColor2).isLinkUnderlined(false).blockMargin(AndroidUtilities.dp(16.0f)).blockQuoteWidth(AndroidUtilities.dp(3.0f)).blockQuoteColor(themedColor2).listItemColor(themedColor).bulletListItemStrokeWidth(AndroidUtilities.dp(1.0f)).bulletWidth(AndroidUtilities.dp(4.0f)).codeTextColor(themedColor).codeBlockTextColor(themedColor).codeBackgroundColor(iBlendARGB).codeBlockBackgroundColor(iBlendARGB2).codeBlockMargin(AndroidUtilities.dp(12.0f)).codeTextSize(AndroidUtilities.dp((int) (SharedConfig.fontSize * 0.92f))).codeBlockTextSize(AndroidUtilities.dp((int) (SharedConfig.fontSize * 0.92f)));
                Typeface typeface = Typeface.MONOSPACE;
                builderCodeBlockTextSize.codeTypeface(typeface).codeBlockTypeface(typeface).headingBreakHeight(0).headingBreakColor(Theme.multAlpha(themedColor, 0.12f)).headingTextSizeMultipliers(new float[]{1.25f, 1.18f, 1.12f, 1.06f, 1.0f, 1.0f}).thematicBreakColor(Theme.multAlpha(themedColor, 0.12f)).thematicBreakHeight(AndroidUtilities.dp(1.0f));
            }
        }).usePlugin(MarkwonInlineParserPlugin.create()).usePlugin(StrikethroughPlugin.create()).usePlugin(TablePlugin.create(new TablePlugin.ThemeConfigure() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$$ExternalSyntheticLambda1
            @Override // io.noties.markwon.ext.tables.TablePlugin.ThemeConfigure
            public final void configureTheme(TableTheme.Builder builder) {
                int i = iMultAlpha;
                builder.tableCellPadding(AndroidUtilities.dp(8.0f)).tableBorderWidth(AndroidUtilities.dp(1.0f)).tableBorderColor(i).tableHeaderRowBackgroundColor(iMultAlpha2).tableOddRowBackgroundColor(iMultAlpha3);
            }
        })).usePlugin(HtmlPlugin.create()).build();
    }

    public static AiResponseAlert showAlert(BaseFragment baseFragment, Client client, String str, boolean z, boolean z2, Utilities.CallbackReturn<URLSpan, Boolean> callbackReturn, Runnable runnable, Utilities.Callback2<String, CharSequence> callback2) {
        return showAlert(baseFragment, client, str, null, z, z2, callbackReturn, runnable, callback2);
    }

    public static AiResponseAlert showAlert(BaseFragment baseFragment, Client client, String str, String str2, boolean z, boolean z2, Utilities.CallbackReturn<URLSpan, Boolean> callbackReturn, final Runnable runnable, Utilities.Callback2<String, CharSequence> callback2) {
        AiResponseAlert aiResponseAlert = new AiResponseAlert(baseFragment.getContext(), client, str, str2, z, baseFragment.getResourceProvider()) { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.6
            @Override // com.exteragram.messenger.ai.ui.AiResponseAlert, org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
            /* JADX INFO: renamed from: dismiss */
            public void dismiss() {
                super.dismiss();
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        };
        aiResponseAlert.setNoforwards(z2);
        aiResponseAlert.setFragment(baseFragment);
        aiResponseAlert.setOnLinkPress(callbackReturn);
        aiResponseAlert.setOnInsertPress(callback2);
        if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(aiResponseAlert);
        }
        aiResponseAlert.generate();
        return aiResponseAlert;
    }

    public void setPrompt(String str) {
        this.prompt = str;
        LoadingTextView loadingTextView = this.loadingTextView;
        loadingTextView.setText(Emoji.replaceEmoji(str, loadingTextView.getPaint().getFontMetricsInt(), true));
        formatPrompt();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMainButton(boolean z) {
        updateMainButton(z, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMainButton(boolean z, boolean z2) {
        this.mainButton.setLoading(z);
        boolean z3 = false;
        AndroidUtilities.updateViewVisibilityAnimated(this.headerView.optionsButton, (z || z2) ? false : true, 0.5f, true);
        if (this.onInsertPress != null) {
            ImageView imageView = this.headerView.insertButton;
            if (!z && !z2) {
                z3 = true;
            }
            AndroidUtilities.updateViewVisibilityAnimated(imageView, z3, 0.5f, true);
        }
    }

    private void formatPrompt() {
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_mini_arrow_mediathin);
        int i = Theme.key_player_actionBarSubtitle;
        coloredImageSpan.setColorKey(i);
        coloredImageSpan.setScale(1.0f, 1.0f);
        coloredImageSpan.setTranslateY(AndroidUtilities.dpf2(1.5f));
        coloredImageSpan.spaceScaleX = 0.95f;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("→ " + this.prompt);
        spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 33);
        spannableStringBuilder.setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(4.0f)), 1, 2, 33);
        spannableStringBuilder.setSpan(new RelativeSizeSpan(0.8f), 2, spannableStringBuilder.length(), 33);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getThemedColor(i)), 2, spannableStringBuilder.length(), 33);
        this.spannedPrompt = spannableStringBuilder;
    }

    private CharSequence formatResponse(CharSequence charSequence) {
        if (AiConfig.getShowResponseOnly() || this.spannedPrompt == null) {
            return formatMarkdown(charSequence);
        }
        return new SpannableStringBuilder(this.spannedPrompt).append((CharSequence) "\n\n").append(formatMarkdown(charSequence));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CharSequence getRawResponse(CharSequence charSequence) {
        if (AiConfig.getShowResponseOnly() || TextUtils.isEmpty(this.prompt)) {
            return charSequence;
        }
        return "→ " + this.prompt + "\n\n" + ((Object) charSequence);
    }

    private CharSequence formatMarkdown(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        try {
            charSequence = this.markwon.toMarkdown(charSequence.toString());
        } catch (Exception unused) {
        }
        return replaceLinks(replaceMarkdownLinks(charSequence));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void generate() {
        this.currentResponse = null;
        stopThinking();
        if (AiController.getInstance().getSelected().isReasoningEnabled() && AiConfig.getResponseStreaming()) {
            showThinking();
        }
        this.currentRequestId = this.client.getResponse(this.prompt, this.useHistory, AiConfig.getResponseStreaming(), this.imagePath, new GenerationCallback() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.7
            @Override // com.exteragram.messenger.ai.network.GenerationCallback
            public void onThinking() {
                AiResponseAlert.this.showThinking();
            }

            @Override // com.exteragram.messenger.ai.network.GenerationCallback
            public void onResponse(String str) {
                if (TextUtils.isEmpty(str)) {
                    return;
                }
                AiResponseAlert.this.stopThinking();
                AiResponseAlert.this.currentResponse = str;
                AiResponseAlert.this.setFormattedResponse(str);
                AiResponseAlert.this.adapter.updateMainView(AiResponseAlert.this.textViewContainer);
                AiResponseAlert.this.updateMainButton(false);
            }

            @Override // com.exteragram.messenger.ai.network.GenerationCallback
            public void onChunk(String str) {
                if (TextUtils.isEmpty(str)) {
                    return;
                }
                AiResponseAlert.this.stopThinking();
                AiResponseAlert.this.currentResponse = str;
                AiResponseAlert.this.setFormattedResponse(str);
                AiResponseAlert.this.adapter.updateMainView(AiResponseAlert.this.textViewContainer);
            }

            @Override // com.exteragram.messenger.ai.network.GenerationCallback
            public void onError(int i, String str) {
                AiResponseAlert.this.stopThinking();
                AiController.showErrorBulletin(AiResponseAlert.this.containerView, AiResponseAlert.this.resourcesProvider, i);
                AiResponseAlert.this.adapter.updateMainView(AiResponseAlert.this.textViewContainer);
                AiResponseAlert.this.updateMainButton(false, true);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showThinking() {
        if (this.thinkingVisible || !TextUtils.isEmpty(this.currentResponse)) {
            return;
        }
        this.thinkingVisible = true;
        this.thinkingDotsView.start();
        this.adapter.updateMainView(this.thinkingDotsView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopThinking() {
        this.thinkingVisible = false;
        this.thinkingDotsView.stop();
    }

    private CharSequence replaceMarkdownLinks(CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        for (URLSpan uRLSpan : (URLSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class)) {
            int spanStart = spannableStringBuilder.getSpanStart(uRLSpan);
            int spanEnd = spannableStringBuilder.getSpanEnd(uRLSpan);
            int spanFlags = spannableStringBuilder.getSpanFlags(uRLSpan);
            if (spanStart >= 0 && spanEnd >= 0) {
                String url = uRLSpan.getURL();
                spannableStringBuilder.removeSpan(uRLSpan);
                spannableStringBuilder.setSpan(createUrlSpan(url), spanStart, spanEnd, spanFlags);
            }
        }
        return spannableStringBuilder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFormattedResponse(CharSequence charSequence) {
        CharSequence response = formatResponse(charSequence);
        if (response instanceof Spanned) {
            this.markwon.setParsedMarkdown(this.textView, (Spanned) response);
        } else {
            this.textView.setText(response);
        }
    }

    public CharSequence replaceLinks(CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        Matcher matcher = LinkifyPort.WEB_URL.matcher(charSequence);
        int iEnd = 0;
        while (matcher.find()) {
            if (hasClickableSpan(charSequence, matcher.start(), matcher.end())) {
                spannableStringBuilder.append(charSequence, iEnd, matcher.end());
                iEnd = matcher.end();
            } else {
                spannableStringBuilder.append(charSequence, iEnd, matcher.start());
                String strGroup = matcher.group(1);
                String strGroup2 = matcher.group(2);
                spannableStringBuilder.append((CharSequence) strGroup);
                spannableStringBuilder.setSpan(createUrlSpan(strGroup2), strGroup != null ? spannableStringBuilder.length() - strGroup.length() : 0, spannableStringBuilder.length(), 33);
                iEnd = matcher.end();
            }
        }
        spannableStringBuilder.append(charSequence, iEnd, charSequence.length());
        return Emoji.replaceEmoji((CharSequence) spannableStringBuilder, this.textView.getPaint().getFontMetricsInt(), false, (int[]) null);
    }

    private boolean hasClickableSpan(CharSequence charSequence, int i, int i2) {
        if (charSequence instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence;
            for (ClickableSpan clickableSpan : (ClickableSpan[]) spanned.getSpans(i, i2, ClickableSpan.class)) {
                if (spanned.getSpanStart(clickableSpan) < i2 && spanned.getSpanEnd(clickableSpan) > i) {
                    return true;
                }
            }
        }
        return false;
    }

    private ClickableSpan createUrlSpan(final String str) {
        return new ClickableSpan() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.8
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                Utilities.CallbackReturn callbackReturn = AiResponseAlert.this.onLinkPress;
                AiResponseAlert aiResponseAlert = AiResponseAlert.this;
                if (callbackReturn != null) {
                    if (((Boolean) aiResponseAlert.onLinkPress.run(new URLSpan(str))).booleanValue()) {
                        AiResponseAlert.this.dismiss();
                    }
                } else if (aiResponseAlert.fragment != null) {
                    AlertsCreator.showOpenUrlAlert(AiResponseAlert.this.fragment, str, false, false);
                }
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                int iMin = Math.min(textPaint.getAlpha(), (textPaint.getColor() >> 24) & 255);
                textPaint.setUnderlineText(false);
                textPaint.setColor(Theme.getColor(Theme.key_dialogTextLink));
                textPaint.setAlpha(iMin);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasEnoughHeight() {
        RecyclerListView recyclerListView;
        float height = 0.0f;
        int i = 0;
        while (true) {
            int childCount = this.listView.getChildCount();
            recyclerListView = this.listView;
            if (i >= childCount) {
                break;
            }
            View childAt = recyclerListView.getChildAt(i);
            if (this.listView.getChildAdapterPosition(childAt) == 1) {
                height += childAt.getHeight();
            }
            i++;
        }
        return height >= ((float) ((recyclerListView.getHeight() - this.listView.getPaddingTop()) - this.listView.getPaddingBottom()));
    }

    public void setFragment(BaseFragment baseFragment) {
        this.fragment = baseFragment;
    }

    public void setOnLinkPress(Utilities.CallbackReturn<URLSpan, Boolean> callbackReturn) {
        this.onLinkPress = callbackReturn;
    }

    private void setOnInsertPress(Utilities.Callback2<String, CharSequence> callback2) {
        this.onInsertPress = callback2;
    }

    public void setNoforwards(boolean z) {
        LinkSpanDrawable.LinksTextView linksTextView = this.textView;
        if (linksTextView != null) {
            linksTextView.setTextIsSelectable(!z);
        }
        if (getWindow() != null) {
            if (z) {
                getWindow().addFlags(8192);
            } else {
                getWindow().clearFlags(8192);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getSheetTop() {
        return getSheetTop(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getSheetTop(boolean z) {
        AnimatedFloat animatedFloat;
        float top = this.listView.getTop();
        if (this.listView.getChildCount() >= 1) {
            RecyclerListView recyclerListView = this.listView;
            top += Math.max(0, recyclerListView.getChildAt(recyclerListView.getChildCount() - 1).getTop());
        }
        float fMax = Math.max(0.0f, top - AndroidUtilities.dp(78.0f));
        if (z && (animatedFloat = this.sheetTopAnimated) != null) {
            if (!this.listView.scrollingByUser && !this.sheetTopNotAnimate) {
                return animatedFloat.set(fMax);
            }
            animatedFloat.set(fMax, true);
        }
        return fMax;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    /* JADX INFO: renamed from: dismiss */
    public void dismiss() {
        super.dismiss();
        stopThinking();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            this.loadingTextView.invalidate();
            this.textView.invalidate();
        }
    }

    public static class LoadingTextView extends TextView {
        private final LoadingDrawable loadingDrawable;
        private final LinkPath path;

        public LoadingTextView(Context context) {
            super(context);
            LinkPath linkPath = new LinkPath(true);
            this.path = linkPath;
            LoadingDrawable loadingDrawable = new LoadingDrawable();
            this.loadingDrawable = loadingDrawable;
            loadingDrawable.usePath(linkPath);
            loadingDrawable.setSpeed(0.65f);
            loadingDrawable.setRadiiDp(4.0f);
            setBackground(loadingDrawable);
        }

        @Override // android.widget.TextView
        public void setTextColor(int i) {
            super.setTextColor(Theme.multAlpha(i, 0.2f));
            this.loadingDrawable.setColors(Theme.multAlpha(i, 0.03f), Theme.multAlpha(i, 0.175f), Theme.multAlpha(i, 0.2f), Theme.multAlpha(i, 0.45f));
        }

        private void updateDrawable() {
            LinkPath linkPath = this.path;
            if (linkPath == null || this.loadingDrawable == null) {
                return;
            }
            linkPath.rewind();
            if (getLayout() != null && getLayout().getText() != null) {
                this.path.setCurrentLayout(getLayout(), 0, getPaddingLeft(), getPaddingTop());
                getLayout().getSelectionPath(0, getLayout().getText().length(), this.path);
            }
            this.loadingDrawable.updateBounds();
        }

        @Override // android.widget.TextView
        public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
            super.setText(charSequence, bufferType);
            updateDrawable();
        }

        @Override // android.widget.TextView, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            updateDrawable();
        }

        @Override // android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.loadingDrawable.reset();
        }
    }

    public static class ThinkingDotsView extends View {
        private final TypingDotsDrawable drawable;

        public ThinkingDotsView(Context context, int i) {
            super(context);
            TypingDotsDrawable typingDotsDrawable = new TypingDotsDrawable(true);
            this.drawable = typingDotsDrawable;
            typingDotsDrawable.setCallback(this);
            typingDotsDrawable.setIgnoreAnimationLocks();
            typingDotsDrawable.setColor(i);
        }

        public void start() {
            if (this.drawable.isStarted()) {
                return;
            }
            this.drawable.start();
        }

        public void stop() {
            this.drawable.stop();
        }

        @Override // android.view.View
        public boolean verifyDrawable(Drawable drawable) {
            return super.verifyDrawable(drawable) || drawable == this.drawable;
        }

        @Override // android.view.View
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(42.0f));
        }

        @Override // android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.drawable.isStarted()) {
                invalidate();
            }
        }

        @Override // android.view.View
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            DrawableUtils.setBounds(this.drawable, AndroidUtilities.dp(22.0f), i2 / 2.0f, 19);
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            this.drawable.draw(canvas);
            if (this.drawable.isStarted()) {
                invalidate();
            }
        }

        @Override // android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            stop();
        }
    }

    public static class PaddedAdapter extends RecyclerView.Adapter {
        private final Context mContext;
        private View mMainView;
        private int mainViewType = 1;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return 2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public PaddedAdapter(Context context, View view) {
            this.mContext = context;
            this.mMainView = view;
        }

        public void updateMainView(View view) {
            if (this.mMainView == view) {
                return;
            }
            this.mainViewType++;
            this.mMainView = view;
            notifyItemChanged(1);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 0) {
                return new RecyclerListView.Holder(new View(this.mContext) { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.PaddedAdapter.1
                    @Override // android.view.View
                    public void onMeasure(int i2, int i3) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec((int) (AndroidUtilities.displaySize.y * 0.4f), TLObject.FLAG_30));
                    }
                });
            }
            return new RecyclerListView.Holder(this.mMainView);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            return this.mainViewType;
        }
    }

    public class HeaderView extends FrameLayout {
        private final ImageView backButton;
        public final ImageView insertButton;
        private final AnimatedTextView modelSelector;
        public final ActionBarMenuItem optionsButton;
        private final View shadow;
        private final LinearLayout subtitleView;
        private final AnimatedTextView titleTextView;

        public HeaderView(Context context) {
            super(context);
            View view = new View(context);
            view.setBackgroundColor(AiResponseAlert.this.getThemedColor(Theme.key_dialogBackground));
            addView(view, LayoutHelper.createFrame(-1, 44.0f, 55, 0.0f, 12.0f, 0.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.backButton = imageView;
            ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
            imageView.setScaleType(scaleType);
            imageView.setImageResource(R.drawable.ic_ab_back);
            int i = Theme.key_dialogTextBlack;
            int themedColor = AiResponseAlert.this.getThemedColor(i);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            imageView.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
            int i2 = Theme.key_listSelector;
            imageView.setBackground(Theme.createSelectorDrawable(AiResponseAlert.this.getThemedColor(i2)));
            imageView.setAlpha(0.0f);
            imageView.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$HeaderView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    HeaderView.this.lambda$new$0(view2);
                }
            });
            addView(imageView, LayoutHelper.createFrame(54, 54.0f, 48, 1.0f, 1.0f, 1.0f, 1.0f));
            ImageView imageView2 = new ImageView(context);
            this.insertButton = imageView2;
            ScaleStateListAnimator.apply(imageView2, 0.15f, 1.5f);
            imageView2.setScaleType(scaleType);
            imageView2.setImageResource(R.drawable.msg_send);
            int i3 = Theme.key_player_actionBarSubtitle;
            imageView2.setColorFilter(new PorterDuffColorFilter(AiResponseAlert.this.getThemedColor(i3), mode));
            imageView2.setBackground(Theme.createSelectorDrawable(AiResponseAlert.this.getThemedColor(i2)));
            imageView2.setVisibility(8);
            imageView2.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$HeaderView$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    HeaderView.this.lambda$new$1(view2);
                }
            });
            addView(imageView2, LayoutHelper.createFrame(48, 54.0f, 53, 1.0f, 1.0f, 64.0f, 1.0f));
            ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, null, 0, i3, false, AiResponseAlert.this.resourcesProvider);
            this.optionsButton = actionBarMenuItem;
            actionBarMenuItem.setLongClickEnabled(false);
            actionBarMenuItem.setShowSubmenuByMove(false);
            actionBarMenuItem.setIcon(R.drawable.ic_ab_other);
            actionBarMenuItem.setSubMenuOpenSide(2);
            actionBarMenuItem.setVisibility(8);
            actionBarMenuItem.setBackground(Theme.createSelectorDrawable(AiResponseAlert.this.getThemedColor(i2), 1, AndroidUtilities.dp(18.0f)));
            addView(actionBarMenuItem, LayoutHelper.createFrame(48, 54.0f, 53, 1.0f, 1.0f, 16.0f, 1.0f));
            actionBarMenuItem.addSubItem(1, R.drawable.msg_copy, LocaleController.getString(R.string.Copy));
            actionBarMenuItem.addSubItem(2, R.drawable.msg_retry, LocaleController.getString(R.string.Retry));
            actionBarMenuItem.addSubItem(3, R.drawable.msg_edit, LocaleController.getString(R.string.Edit));
            if (AiResponseAlert.this.useHistory) {
                actionBarMenuItem.addSubItem(4, R.drawable.menu_reply, LocaleController.getString(R.string.Reply));
            }
            actionBarMenuItem.setShowedFromBottom(false);
            actionBarMenuItem.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$HeaderView$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    HeaderView.this.lambda$new$2(view2);
                }
            });
            actionBarMenuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$HeaderView$$ExternalSyntheticLambda3
                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
                public final void onItemClick(int i4) {
                    HeaderView.this.lambda$new$5(i4);
                }
            });
            actionBarMenuItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
            AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, false);
            this.titleTextView = animatedTextView;
            animatedTextView.setTextColor(AiResponseAlert.this.getThemedColor(i));
            animatedTextView.setTextSize(AndroidUtilities.dp(20.0f));
            animatedTextView.setTypeface(AndroidUtilities.bold());
            animatedTextView.setText(AiConfig.getSelectedRole());
            animatedTextView.setPivotX(0.0f);
            animatedTextView.setPivotY(0.0f);
            addView(animatedTextView, LayoutHelper.createFrame(-1, 30.0f, 55, 22.0f, 20.0f, 22.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.subtitleView = linearLayout;
            linearLayout.setPivotX(0.0f);
            linearLayout.setPivotY(0.0f);
            AnimatedTextView animatedTextView2 = new AnimatedTextView(context) { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.HeaderView.1
                private final Paint bgPaint = new Paint(1);
                private final LinkSpanDrawable.LinkCollector links = new LinkSpanDrawable.LinkCollector();

                @Override // org.telegram.ui.Components.AnimatedTextView, android.view.View
                public void onDraw(Canvas canvas) {
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, (getHeight() - AndroidUtilities.dp(18.0f)) / 2.0f, width(), (getHeight() + AndroidUtilities.dp(18.0f)) / 2.0f);
                    this.bgPaint.setColor(Theme.multAlpha(AiResponseAlert.this.getThemedColor(Theme.key_player_actionBarSubtitle), 0.1175f));
                    canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.bgPaint);
                    if (this.links.draw(canvas)) {
                        invalidate();
                    }
                    super.onDraw(canvas);
                }

                @Override // android.view.View
                @SuppressLint({"ClickableViewAccessibility"})
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0) {
                        LinkSpanDrawable linkSpanDrawable = new LinkSpanDrawable(null, AiResponseAlert.this.resourcesProvider, motionEvent.getX(), motionEvent.getY());
                        linkSpanDrawable.setColor(Theme.multAlpha(AiResponseAlert.this.getThemedColor(Theme.key_player_actionBarSubtitle), 0.1175f));
                        LinkPath linkPathObtainNewPath = linkSpanDrawable.obtainNewPath();
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(0.0f, (getHeight() - AndroidUtilities.dp(18.0f)) / 2.0f, width(), (getHeight() + AndroidUtilities.dp(18.0f)) / 2.0f);
                        linkPathObtainNewPath.addRect(rectF, Path.Direction.CW);
                        this.links.addLink(linkSpanDrawable);
                        invalidate();
                        return true;
                    }
                    if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                        if (motionEvent.getAction() == 1) {
                            performClick();
                        }
                        this.links.clear();
                        invalidate();
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.modelSelector = animatedTextView2;
            animatedTextView2.setAnimationProperties(0.25f, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
            animatedTextView2.setTextColor(AiResponseAlert.this.getThemedColor(i3));
            animatedTextView2.setTextSize(AndroidUtilities.dp(14.0f));
            animatedTextView2.setText(AiController.getInstance().getSelected().getShortModel());
            animatedTextView2.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f));
            animatedTextView2.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$HeaderView$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    HeaderView.this.lambda$new$6(view2);
                }
            });
            linearLayout.addView(animatedTextView2, LayoutHelper.createLinear(-2, -2, 16, 0, 0, 3, 0));
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 55, 22.0f, 43.0f, 22.0f, 0.0f));
            View view2 = new View(context);
            this.shadow = view2;
            view2.setBackgroundColor(AiResponseAlert.this.getThemedColor(Theme.key_divider));
            view2.setAlpha(0.0f);
            addView(view2, LayoutHelper.createFrame(-1, AndroidUtilities.getShadowHeight() / AndroidUtilities.dpf2(1.0f), 55, 0.0f, 56.0f, 0.0f, 0.0f));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            AiResponseAlert.this.dismiss();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            Utilities.Callback2 callback2 = AiResponseAlert.this.onInsertPress;
            String str = AiResponseAlert.this.prompt;
            AiResponseAlert aiResponseAlert = AiResponseAlert.this;
            callback2.run(str, aiResponseAlert.getRawResponse(aiResponseAlert.currentResponse));
            AiResponseAlert.this.dismiss();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(View view) {
            this.optionsButton.toggleSubMenu();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(GenerateFromMessageBottomSheet.GenerationData generationData) {
            AiConfig.removeLastFromHistory();
            AiResponseAlert.this.setPrompt(generationData.prompt());
            AiResponseAlert.this.imagePath = generationData.imagePath();
            AiResponseAlert.this.useHistory = generationData.useHistory();
            AiResponseAlert.this.adapter.updateMainView(AiResponseAlert.this.loadingTextView);
            AiResponseAlert.this.updateMainButton(true);
            AiResponseAlert.this.generate();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5(int i) {
            if (i == 1) {
                AiResponseAlert aiResponseAlert = AiResponseAlert.this;
                if (AndroidUtilities.addToClipboard(aiResponseAlert.getRawResponse(aiResponseAlert.currentResponse))) {
                    BulletinFactory.of((FrameLayout) AiResponseAlert.this.containerView, AiResponseAlert.this.resourcesProvider).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
                    return;
                }
                return;
            }
            if (i == 2) {
                AiConfig.removeLastFromHistory();
                AiResponseAlert.this.adapter.updateMainView(AiResponseAlert.this.loadingTextView);
                AiResponseAlert.this.updateMainButton(true);
                AiResponseAlert.this.generate();
                return;
            }
            if (i == 3) {
                new GenerateFromMessageBottomSheet(AiResponseAlert.this.prompt, AiResponseAlert.this.imagePath, AiResponseAlert.this.fragment, getContext(), new Utilities.Callback() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$HeaderView$$ExternalSyntheticLambda5
                    @Override // org.telegram.messenger.Utilities.Callback
                    public final void run(Object obj) {
                        HeaderView.this.lambda$new$3((GenerateFromMessageBottomSheet.GenerationData) obj);
                    }
                }, AiResponseAlert.this.useHistory).show();
            } else {
                if (i != 4) {
                    return;
                }
                new GenerateFromMessageBottomSheet(AiResponseAlert.this.fragment, getContext(), new Utilities.Callback() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$HeaderView$$ExternalSyntheticLambda6
                    @Override // org.telegram.messenger.Utilities.Callback
                    public final void run(Object obj) {
                        HeaderView.this.lambda$new$4((GenerateFromMessageBottomSheet.GenerationData) obj);
                    }
                }, false).show();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(GenerateFromMessageBottomSheet.GenerationData generationData) {
            AiResponseAlert.this.setPrompt(generationData.prompt());
            AiResponseAlert.this.imagePath = null;
            AiResponseAlert.this.useHistory = generationData.useHistory();
            AiResponseAlert.this.adapter.updateMainView(AiResponseAlert.this.loadingTextView);
            AiResponseAlert.this.updateMainButton(true);
            AiResponseAlert.this.generate();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(View view) {
            openModelSelect();
        }

        public void openModelSelect() {
            if (AiResponseAlert.this.client.isGenerating()) {
                return;
            }
            final ArrayList<Service> services = AiConfig.getServices();
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext()) { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.HeaderView.2
                @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout, android.widget.FrameLayout, android.view.View
                public void onMeasure(int i, int i2) {
                    if (services.size() >= 6) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(336.0f), TLObject.FLAG_30));
                    } else {
                        super.onMeasure(i, i2);
                    }
                }
            };
            Drawable drawableMutate = ContextCompat.getDrawable(getContext(), R.drawable.popup_fixed_alert).mutate();
            drawableMutate.setColorFilter(new PorterDuffColorFilter(AiResponseAlert.this.getThemedColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY));
            actionBarPopupWindowLayout.setBackground(drawableMutate);
            final Runnable[] runnableArr = new Runnable[1];
            int i = 0;
            while (i < services.size()) {
                final Service service = services.get(i);
                ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), 2, i == 0, i == services.size() - 1, AiResponseAlert.this.resourcesProvider);
                actionBarMenuSubItem.setText(service.getModel());
                actionBarMenuSubItem.setSubtext(service.getUrl());
                actionBarMenuSubItem.subtextView.setPadding(0, 0, service.isSelected() ? AndroidUtilities.dp(34.0f) : 0, 0);
                actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
                actionBarMenuSubItem.setItemHeight(56);
                actionBarMenuSubItem.setChecked(service.isSelected());
                actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$HeaderView$$ExternalSyntheticLambda7
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        HeaderView.this.lambda$openModelSelect$7(runnableArr, service, view);
                    }
                });
                actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
                i++;
            }
            final ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
            runnableArr[0] = new Runnable() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert$HeaderView$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    actionBarPopupWindow.dismiss();
                }
            };
            actionBarPopupWindow.setPauseNotifications(true);
            actionBarPopupWindow.setDismissAnimationDuration(220);
            actionBarPopupWindow.setOutsideTouchable(true);
            actionBarPopupWindow.setClippingEnabled(true);
            actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
            actionBarPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            this.modelSelector.getLocationInWindow(iArr);
            actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
            int measuredHeight = actionBarPopupWindowLayout.getMeasuredHeight();
            int i2 = iArr[1];
            actionBarPopupWindow.showAtLocation(AiResponseAlert.this.containerView, 51, iArr[0] - AndroidUtilities.dp(8.0f), ((float) i2) > (((float) AndroidUtilities.displaySize.y) * 0.9f) - ((float) measuredHeight) ? (i2 - measuredHeight) + AndroidUtilities.dp(8.0f) : (i2 + this.modelSelector.getMeasuredHeight()) - AndroidUtilities.dp(8.0f));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$openModelSelect$7(Runnable[] runnableArr, Service service, View view) {
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
            Bulletin.addDelegate(this, new Bulletin.Delegate() { // from class: com.exteragram.messenger.ai.ui.AiResponseAlert.ContainerView.1
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
