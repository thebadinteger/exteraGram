package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
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
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.translators.DeepLTranslator;
import com.exteragram.messenger.translators.GoogleTranslator;
import com.exteragram.messenger.translators.TelegramTranslator;
import com.exteragram.messenger.translators.YandexTranslator;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Predicate;
import okhttp3.internal.url._UrlKt;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.telegram.messenger.AiTonesController$$ExternalSyntheticLambda0;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.RichMessageLayout;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public abstract class TranslateAlert2 extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private static HashMap<String, Locale> localesByCode;
    public static final String[] userAgents = {"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"};
    private PaddedAdapter adapter;
    private Boolean buttonShadowShown;
    private boolean firstTranslation;
    private BaseFragment fragment;
    private String fromLanguage;
    private HeaderView headerView;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private LoadingTextView loadingTextView;
    private ButtonWithCounterView mainButton;
    private Utilities.CallbackReturn<URLSpan, Boolean> onLinkPress;
    private String prevToLanguage;
    private Integer reqId;
    private ArrayList<TLRPC.MessageEntity> reqMessageEntities;
    private int reqMessageId;
    private TLRPC.InputPeer reqPeer;
    private TL_iv.RichMessage reqRichMessage;
    private boolean reqSum;
    private CharSequence reqText;
    private RichMessageLayout.PreviewView richLoadingPreviewView;
    private RichMessageLayout.PreviewView richPreviewView;
    private AnimatedFloat sheetTopAnimated;
    private boolean sheetTopNotAnimate;
    private TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelper;
    private TextSelectionHelper.TextSelectionOverlay textSelectionOverlay;
    private LinkSpanDrawable.LinksTextView textView;
    private FrameLayout textViewContainer;
    private String toLanguage;

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean canDismissWithSwipe() {
        return false;
    }

    public TranslateAlert2(Context context, String str, String str2, CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, Theme.ResourcesProvider resourcesProvider) {
        this(context, str, str2, charSequence, arrayList, null, 0, false, null, resourcesProvider);
    }

    private TranslateAlert2(Context context, String str, String str2, CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, TLRPC.InputPeer inputPeer, int i, boolean z, TL_iv.RichMessage richMessage, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.firstTranslation = true;
        this.backgroundPaddingLeft = 0;
        fixNavigationBar();
        this.reqText = charSequence;
        this.reqPeer = inputPeer;
        this.reqMessageId = i;
        this.reqSum = z;
        this.reqRichMessage = richMessage;
        this.fromLanguage = str;
        this.toLanguage = str2;
        ContainerView containerView = new ContainerView(context);
        this.containerView = containerView;
        this.sheetTopAnimated = new AnimatedFloat(containerView, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
        LoadingTextView loadingTextView = new LoadingTextView(context);
        this.loadingTextView = loadingTextView;
        loadingTextView.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(6.0f));
        this.loadingTextView.setTextSize(1, SharedConfig.fontSize);
        LoadingTextView loadingTextView2 = this.loadingTextView;
        int i2 = Theme.key_dialogTextBlack;
        loadingTextView2.setTextColor(getThemedColor(i2));
        this.loadingTextView.setLinkTextColor(Theme.multAlpha(getThemedColor(i2), 0.2f));
        this.loadingTextView.setText(Emoji.replaceEmoji(charSequence == null ? _UrlKt.FRAGMENT_ENCODE_SET : charSequence.toString(), this.loadingTextView.getPaint().getFontMetricsInt(), true));
        this.textViewContainer = new FrameLayout(context) { // from class: org.telegram.ui.Components.TranslateAlert2.1
            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i3, int i4) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3), TLObject.FLAG_30), i4);
            }
        };
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        this.textView = linksTextView;
        linksTextView.setDisablePaddingsOffsetY(true);
        this.textView.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(6.0f));
        this.textView.setTextSize(1, SharedConfig.fontSize);
        this.textView.setTextColor(getThemedColor(i2));
        this.textView.setLinkTextColor(getThemedColor(Theme.key_chat_messageLinkIn));
        this.textView.setTextIsSelectable(true);
        this.textView.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
        int themedColor = getThemedColor(Theme.key_chat_TextSelectionCursor);
        try {
            if (Build.VERSION.SDK_INT >= 29 && !XiaomiUtilities.isMIUI()) {
                Drawable textSelectHandleLeft = this.textView.getTextSelectHandleLeft();
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
                textSelectHandleLeft.setColorFilter(themedColor, mode);
                this.textView.setTextSelectHandleLeft(textSelectHandleLeft);
                Drawable textSelectHandleRight = this.textView.getTextSelectHandleRight();
                textSelectHandleRight.setColorFilter(themedColor, mode);
                this.textView.setTextSelectHandleRight(textSelectHandleRight);
            }
        } catch (Exception unused) {
        }
        this.textViewContainer.addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
        if (this.reqRichMessage != null) {
            RichMessageLayout.PreviewView previewView = new RichMessageLayout.PreviewView(context, this.currentAccount, resourcesProvider);
            this.richLoadingPreviewView = previewView;
            previewView.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(6.0f));
            this.richLoadingPreviewView.set(this.reqRichMessage);
            this.richLoadingPreviewView.setTranslationLoading(true);
            RichMessageLayout.PreviewView previewView2 = new RichMessageLayout.PreviewView(context, this.currentAccount, resourcesProvider);
            this.richPreviewView = previewView2;
            previewView2.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(6.0f));
        }
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.TranslateAlert2.2
            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onRequestFocusInDescendants(int i3, Rect rect) {
                return true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.ViewParent
            public void requestChildFocus(View view, View view2) {
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && motionEvent.getY() < TranslateAlert2.this.getSheetTop() - getTop()) {
                    TranslateAlert2.this.lambda$new$0();
                    return true;
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setOverScrollMode(1);
        this.listView.setPadding(0, AndroidUtilities.statusBarHeight + AndroidUtilities.dp(56.0f), 0, AndroidUtilities.dp(80.0f));
        this.listView.setClipToPadding(true);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        PaddedAdapter paddedAdapter = new PaddedAdapter(context, this.reqRichMessage != null ? this.richLoadingPreviewView : this.loadingTextView);
        this.adapter = paddedAdapter;
        recyclerListView3.setAdapter(paddedAdapter);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.TranslateAlert2.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                ((BottomSheet) TranslateAlert2.this).containerView.invalidate();
                TranslateAlert2 translateAlert2 = TranslateAlert2.this;
                translateAlert2.updateButtonShadow(translateAlert2.listView.canScrollVertically(1));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i3) {
                if (i3 == 0) {
                    TranslateAlert2.this.sheetTopNotAnimate = false;
                }
                if ((i3 == 0 || i3 == 2) && TranslateAlert2.this.getSheetTop(false) > 0.0f && TranslateAlert2.this.getSheetTop(false) < AndroidUtilities.dp(96.0f) && TranslateAlert2.this.listView.canScrollVertically(1) && TranslateAlert2.this.hasEnoughHeight()) {
                    TranslateAlert2.this.sheetTopNotAnimate = true;
                    TranslateAlert2.this.listView.smoothScrollBy(0, (int) TranslateAlert2.this.getSheetTop(false));
                }
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.Components.TranslateAlert2.4
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onChangeAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                ((BottomSheet) TranslateAlert2.this).containerView.invalidate();
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                ((BottomSheet) TranslateAlert2.this).containerView.invalidate();
            }
        };
        defaultItemAnimator.setDurations(180L);
        defaultItemAnimator.setInterpolator(new LinearInterpolator());
        this.listView.setItemAnimator(defaultItemAnimator);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -2, 80));
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = new TextSelectionHelper.ArticleTextSelectionHelper();
        this.textSelectionHelper = articleTextSelectionHelper;
        articleTextSelectionHelper.setParentView(this.listView);
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper2 = this.textSelectionHelper;
        articleTextSelectionHelper2.layoutManager = this.layoutManager;
        TextSelectionHelper<Cell>.TextSelectionOverlay overlayView = articleTextSelectionHelper2.getOverlayView(context);
        this.textSelectionOverlay = overlayView;
        AndroidUtilities.removeFromParent(overlayView);
        this.containerView.addView(this.textSelectionOverlay, LayoutHelper.createFrame(-1, -1, 119));
        RichMessageLayout.PreviewView previewView3 = this.richPreviewView;
        if (previewView3 != null) {
            previewView3.setTextSelectionHelper(this.textSelectionHelper);
        }
        HeaderView headerView = new HeaderView(context);
        this.headerView = headerView;
        this.containerView.addView(headerView, LayoutHelper.createFrame(-1, 78, 55));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        this.mainButton = buttonWithCounterView;
        buttonWithCounterView.setRound();
        this.mainButton.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
        this.mainButton.setText(LocaleController.getString(R.string.CloseTranslation), false);
        this.mainButton.setLoading(true);
        this.mainButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$0(view);
            }
        });
        this.containerView.addView(this.mainButton, LayoutHelper.createFrame(-1, 48.0f, 87, 16.0f, 16.0f, 16.0f, 16.0f));
        translate();
    }

    public void lambda$translate$2(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$translate$1(tLObject);
            }
        });
    }

    public void lambda$translateAlt$4(String str, Boolean bool) {
        if (str != null) {
            this.firstTranslation = false;
            this.textView.setText(preprocessText(str));
            this.adapter.updateMainView(this.textViewContainer);
        } else {
            if (isDismissed()) {
                return;
            }
            if (this.firstTranslation) {
                lambda$new$0();
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 1, LocaleController.getString(bool.booleanValue() ? R.string.TranslationFailedAlert1 : R.string.TranslationFailedAlert2));
                return;
            }
            BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createErrorBulletin(LocaleController.getString(bool.booleanValue() ? R.string.TranslationFailedAlert1 : R.string.TranslationFailedAlert2)).show();
            AnimatedTextView animatedTextView = this.headerView.toLanguageTextView;
            String str2 = this.prevToLanguage;
            this.toLanguage = str2;
            animatedTextView.setText(languageName(str2));
            this.adapter.updateMainView(this.textViewContainer);
        }
    }

    private static int lastIndexOfSafe(String str, String str2, int i, int i2) {
        int iLastIndexOf = str.lastIndexOf(str2, i2 - 1);
        if (iLastIndexOf >= i) {
            return iLastIndexOf;
        }
        return -1;
    }

    public static ArrayList<String> cut(String str, int i) {
        ArrayList<String> arrayList = new ArrayList<>();
        int i2 = 0;
        while (i2 < str.length()) {
            int iMin = Math.min(i2 + i, str.length());
            int iLastIndexOfSafe = lastIndexOfSafe(str, "%0A", i2, iMin);
            if (iLastIndexOfSafe == -1) {
                iLastIndexOfSafe = lastIndexOfSafe(str, "%20", i2, iMin);
            }
            if (iLastIndexOfSafe != -1) {
                iMin = iLastIndexOfSafe + 3;
            }
            arrayList.add(str.substring(i2, iMin));
            i2 = iMin;
        }
        return arrayList;
    }

    public static void alternativeTranslate(final String str, String str2, final String str3, final Utilities.Callback2<String, Boolean> callback2) {
        if (callback2 == null) {
            return;
        }
        if (str2 == null) {
            LanguageDetector.detectLanguage(str, new LanguageDetector.StringCallback() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda0
                @Override 
                public final void run(String str4) {
                    TranslateAlert2.alternativeTranslate(str, str4, str3, callback2);
                }
            }, new LanguageDetector.ExceptionCallback() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda1
                @Override 
                public final void run(Exception exc) {
                    TranslateAlert2.alternativeTranslate(str, "en", str3, callback2);
                }
            });
            return;
        }
        String strEncode = Uri.encode(str);
        if (strEncode.length() > 5000) {
            ArrayList<String> arrayListCut = cut(strEncode, 5000);
            final ArrayList arrayList = new ArrayList();
            for (int i = 0; i < arrayListCut.size(); i++) {
                arrayList.add(null);
            }
            final boolean[] zArr = new boolean[1];
            for (final int i2 = 0; i2 < arrayListCut.size(); i2++) {
                alternativeTranslateInternal(arrayListCut.get(i2), str2, str3, new Utilities.Callback2() { // from class: org.telegram.ui.Components.TranslateAlert2$$ExternalSyntheticLambda2
                    @Override 
                    public final void run(Object obj, Object obj2) {
                        TranslateAlert2.m13501$r8$lambda$CuMviJH3DwfD3vmK8xG1TrEons(zArr, arrayList, i2, callback2, (String) obj, (Boolean) obj2);
                    }
                });
            }
            return;
        }
        alternativeTranslateInternal(strEncode, str2, str3, callback2);
    }

    public static void lambda$new$0(View view) {
            TranslateAlert2.this.lambda$new$0();
        }

        public /* synthetic */ void lambda$new$1(View view) {
            if (AndroidUtilities.addToClipboard(TranslateAlert2.this.textView.getText())) {
                BulletinFactory.of((FrameLayout) ((BottomSheet) TranslateAlert2.this).containerView, ((BottomSheet) TranslateAlert2.this).resourcesProvider).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
            }
        }

        public /* synthetic */ void lambda$new$2(View view) {
            openProviderSelect();
        }

        public /* synthetic */ void lambda$new$3(View view) {
            openLanguagesSelect();
        }

        public void openProviderSelect() {
            ItemOptions itemOptionsTranslate = ItemOptions.makeOptions(((BottomSheet) TranslateAlert2.this).containerView, ((BottomSheet) TranslateAlert2.this).resourcesProvider, this.titleTextView).setDrawScrim(false).setDimAlpha(0).setGravity(LocaleController.isRTL ? 5 : 3).translate(LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : -AndroidUtilities.dp(8.0f), 0.0f);
            int translationProvider = ExteraConfig.getTranslationProvider();
            CharSequence[] charSequenceArrProviderNames = ProviderNames();
            final int i = 0;
            while (i < charSequenceArrProviderNames.length) {
                itemOptionsTranslate.addChecked(translationProvider == i, charSequenceArrProviderNames[i], new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$HeaderView$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$openProviderSelect$4(i);
                    }
                });
                i++;
            }
            itemOptionsTranslate.show();
        }

        public /* synthetic */ void lambda$openProviderSelect$4(int i) {
            if (ExteraConfig.getTranslationProvider() == i) {
                return;
            }
            onProviderSelect(i);
        }

        private void onProviderSelect(int i) {
            String str = TranslateAlert2.this.toLanguage;
            ExteraConfig.setTranslationProvider(i);
            TranslatorUtils.ensureTargetLanguageCompatibleWithProvider();
            this.titleTextView.setText(TranslatorUtils.getCurrentTranslatorName());
            String toLanguage = TranslateAlert2.getToLanguage();
            if (!TextUtils.equals(str, toLanguage)) {
                if (TranslateAlert2.this.adapter.mMainView == TranslateAlert2.this.textViewContainer) {
                    TranslateAlert2.this.prevToLanguage = str;
                }
                TranslateAlert2.this.toLanguage = toLanguage;
                this.toLanguageTextView.setText(TranslateAlert2.capitalFirst(TranslateAlert2.languageName(TranslateAlert2.this.toLanguage)));
            }
            TranslateAlert2.this.adapter.updateMainView(TranslateAlert2.this.loadingTextView);
            TranslateAlert2.this.mainButton.setLoading(true);
            AndroidUtilities.updateViewVisibilityAnimated(TranslateAlert2.this.headerView.copyButton, false, 0.5f, true);
            TranslateAlert2.this.translate();
        }

        private CharSequence[] ProviderNames() {
            return new CharSequence[]{TelegramTranslator.getInstance().getDisplayName(), GoogleTranslator.getInstance().getDisplayName(), YandexTranslator.getInstance().getDisplayName(), DeepLTranslator.getInstance().getDisplayName()};
        }

        public void openLanguagesSelect() {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext()) { // from class: org.telegram.ui.Components.TranslateAlert2.HeaderView.4
                @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout, android.widget.FrameLayout, android.view.View
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min((int) (AndroidUtilities.displaySize.y * 0.33f), View.MeasureSpec.getSize(i2)), TLObject.FLAG_30));
                }
            };
            Drawable drawableMutate = ContextCompat.getDrawable(getContext(), R.drawable.popup_fixed_alert).mutate();
            drawableMutate.setColorFilter(new PorterDuffColorFilter(TranslateAlert2.this.getThemedColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY));
            actionBarPopupWindowLayout.setBackground(drawableMutate);
            final Runnable[] runnableArr = new Runnable[1];
            ArrayList arrayList = new ArrayList(TranslatorUtils.getCurrentTargetLanguages());
            arrayList.removeIf(new Predicate() { // from class: org.telegram.ui.Components.TranslateAlert2$HeaderView$$ExternalSyntheticLambda5
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return this.f$0.lambda$openLanguagesSelect$5((TranslateController.Language) obj);
                }
            });
            boolean z = true;
            int i = 0;
            while (i < arrayList.size()) {
                final TranslateController.Language language = (TranslateController.Language) arrayList.get(i);
                final String str = language.displayName;
                ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), 2, z, i == arrayList.size() - 1, ((BottomSheet) TranslateAlert2.this).resourcesProvider);
                actionBarMenuSubItem.setText(str);
                actionBarMenuSubItem.setChecked(TextUtils.equals(TranslateAlert2.this.toLanguage, language.code));
                actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TranslateAlert2$HeaderView$$ExternalSyntheticLambda6
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$openLanguagesSelect$6(runnableArr, language, str, view);
                    }
                });
                actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
                i++;
                z = false;
            }
            final ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
            runnableArr[0] = new Runnable() { // from class: org.telegram.ui.Components.TranslateAlert2$HeaderView$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    actionBarPopupWindow.dismiss();
                }
            };
            actionBarPopupWindow.setPauseNotifications(true);
            actionBarPopupWindow.setDismissAnimationDuration(Opcodes.REM_INT_LIT8);
            actionBarPopupWindow.setOutsideTouchable(true);
            actionBarPopupWindow.setClippingEnabled(true);
            actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
            actionBarPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            this.toLanguageTextView.getLocationInWindow(iArr);
            actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
            int measuredHeight = actionBarPopupWindowLayout.getMeasuredHeight();
            int i2 = iArr[1];
            actionBarPopupWindow.showAtLocation(((BottomSheet) TranslateAlert2.this).containerView, 51, iArr[0] - AndroidUtilities.dp(8.0f), ((float) i2) > (((float) AndroidUtilities.displaySize.y) * 0.9f) - ((float) measuredHeight) ? (i2 - measuredHeight) + AndroidUtilities.dp(8.0f) : (i2 + this.toLanguageTextView.getMeasuredHeight()) - AndroidUtilities.dp(8.0f));
        }

        public /* synthetic */ boolean lambda$openLanguagesSelect$5(TranslateController.Language language) {
            return language == null || TextUtils.isEmpty(language.code) || TextUtils.equals(language.code, TranslateAlert2.this.fromLanguage);
        }

        public /* synthetic */ void lambda$openLanguagesSelect$6(Runnable[] runnableArr, TranslateController.Language language, CharSequence charSequence, View view) {
            Runnable runnable = runnableArr[0];
            if (runnable != null) {
                runnable.run();
            }
            if (TextUtils.equals(TranslateAlert2.this.toLanguage, language.code)) {
                return;
            }
            if (TranslateAlert2.this.adapter.mMainView == TranslateAlert2.this.textViewContainer || TranslateAlert2.this.adapter.mMainView == TranslateAlert2.this.richPreviewView) {
                TranslateAlert2 translateAlert2 = TranslateAlert2.this;
                translateAlert2.prevToLanguage = translateAlert2.toLanguage;
            }
            this.toLanguageTextView.setText(charSequence);
            TranslateAlert2.this.toLanguage = language.code;
            PaddedAdapter paddedAdapter = TranslateAlert2.this.adapter;
            TL_iv.RichMessage richMessage = TranslateAlert2.this.reqRichMessage;
            TranslateAlert2 translateAlert3 = TranslateAlert2.this;
            paddedAdapter.updateMainView(richMessage != null ? translateAlert3.richLoadingPreviewView : translateAlert3.loadingTextView);
            TranslateAlert2.setToLanguage(language.code);
            TranslateAlert2.this.mainButton.setLoading(true);
            AndroidUtilities.updateViewVisibilityAnimated(TranslateAlert2.this.headerView.copyButton, false, 0.5f, true);
            TranslateAlert2.this.translate();
        }

        @Override // android.view.View
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            float fClamp = MathUtils.clamp((f - AndroidUtilities.statusBarHeight) / AndroidUtilities.dp(64.0f), 0.0f, 1.0f);
            if (!TranslateAlert2.this.hasEnoughHeight()) {
                fClamp = 1.0f;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(fClamp);
            this.titleTextView.setScaleX(AndroidUtilities.lerp(0.85f, 1.0f, interpolation));
            this.titleTextView.setScaleY(AndroidUtilities.lerp(0.85f, 1.0f, interpolation));
            this.titleTextView.setTranslationY(AndroidUtilities.lerp(AndroidUtilities.dpf2(-12.0f), 0.0f, interpolation));
            if (!LocaleController.isRTL) {
                this.titleTextView.setTranslationX(AndroidUtilities.lerp(AndroidUtilities.dpf2(50.0f), 0.0f, interpolation));
                this.subtitleView.setTranslationX(AndroidUtilities.lerp(AndroidUtilities.dpf2(50.0f), 0.0f, interpolation));
            }
            this.subtitleView.setTranslationY(AndroidUtilities.lerp(AndroidUtilities.dpf2(-22.0f), 0.0f, interpolation));
            this.backButton.setTranslationX(AndroidUtilities.lerp(0.0f, AndroidUtilities.dpf2(-25.0f), interpolation));
            float f2 = 1.0f - interpolation;
            this.backButton.setAlpha(f2);
            this.copyButton.setTranslationX(AndroidUtilities.lerp(AndroidUtilities.dpf2(14.0f), AndroidUtilities.dpf2(8.0f), interpolation));
            this.copyButton.setTranslationY(AndroidUtilities.lerp(AndroidUtilities.dpf2(0.0f), AndroidUtilities.dpf2(16.0f), interpolation));
            this.copyButton.setColorFilter(ColorUtils.blendARGB(TranslateAlert2.this.getThemedColor(Theme.key_dialogTextBlack), TranslateAlert2.this.getThemedColor(Theme.key_player_actionBarSubtitle), interpolation), PorterDuff.Mode.MULTIPLY);
            this.shadow.setTranslationY(AndroidUtilities.lerp(0.0f, AndroidUtilities.dpf2(22.0f), interpolation));
            this.shadow.setAlpha(f2);
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0f), TLObject.FLAG_30));
        }
    }

    public class ContainerView extends FrameLayout {
        private Paint bgPaint;
        private Path bgPath;
        private Boolean lightStatusBarFull;

        public ContainerView(Context context) {
            super(context);
            this.bgPath = new Path();
            Paint paint = new Paint(1);
            this.bgPaint = paint;
            paint.setColor(TranslateAlert2.this.getThemedColor(Theme.key_dialogBackground));
            Theme.applyDefaultShadow(this.bgPaint);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (TranslateAlert2.this.textSelectionHelper != null && TranslateAlert2.this.textSelectionOverlay != null) {
                if (motionEvent.getAction() == 0 || motionEvent.getAction() == 1) {
                    Log.d("TA2", "container dispatch act=" + motionEvent.getAction() + " inSel=" + TranslateAlert2.this.textSelectionHelper.isInSelectionMode());
                }
                if (TranslateAlert2.this.textSelectionHelper.isInSelectionMode() && TranslateAlert2.this.textSelectionOverlay.onTouchEvent(motionEvent)) {
                    Log.d("TA2", "overlay consumed (handle)");
                    return true;
                }
                boolean zCheckOnTap = TranslateAlert2.this.textSelectionOverlay.checkOnTap(motionEvent);
                if (motionEvent.getAction() == 1) {
                    Log.d("TA2", "checkOnTap=" + zCheckOnTap);
                }
                if (zCheckOnTap) {
                    motionEvent.setAction(3);
                }
            }
            return super.dispatchTouchEvent(motionEvent);
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            float sheetTop = TranslateAlert2.this.getSheetTop();
            float fLerp = AndroidUtilities.lerp(0, AndroidUtilities.dp(12.0f), MathUtils.clamp(sheetTop / AndroidUtilities.dpf2(24.0f), 0.0f, 1.0f));
            TranslateAlert2.this.headerView.setTranslationY(Math.max(AndroidUtilities.statusBarHeight, sheetTop));
            updateLightStatusBar(sheetTop <= ((float) AndroidUtilities.statusBarHeight) / 2.0f);
            FrameLayout frameLayout = TranslateAlert2.this.topBulletinContainer;
            frameLayout.setTranslationY(((-frameLayout.getTop()) - TranslateAlert2.this.topBulletinContainer.getHeight()) + getTranslationY() + Math.max(AndroidUtilities.statusBarHeight + AndroidUtilities.dp(56.0f) + TranslateAlert2.this.topBulletinContainer.getHeight(), sheetTop));
            this.bgPath.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, sheetTop, getWidth(), getHeight() + fLerp);
            this.bgPath.addRoundRect(rectF, fLerp, fLerp, Path.Direction.CW);
            canvas.drawPath(this.bgPath, this.bgPaint);
            super.dispatchDraw(canvas);
        }

        @Override // android.view.View
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            FrameLayout frameLayout = TranslateAlert2.this.topBulletinContainer;
            frameLayout.setTranslationY(((-frameLayout.getTop()) - TranslateAlert2.this.topBulletinContainer.getHeight()) + f + Math.max(AndroidUtilities.statusBarHeight + AndroidUtilities.dp(56.0f) + TranslateAlert2.this.topBulletinContainer.getHeight(), TranslateAlert2.this.getSheetTop()));
        }

        private void updateLightStatusBar(boolean z) {
            int iBlendOver;
            Boolean bool = this.lightStatusBarFull;
            if (bool == null || bool.booleanValue() != z) {
                this.lightStatusBarFull = Boolean.valueOf(z);
                Window window = TranslateAlert2.this.getWindow();
                TranslateAlert2 translateAlert2 = TranslateAlert2.this;
                if (z) {
                    iBlendOver = translateAlert2.getThemedColor(Theme.key_dialogBackground);
                } else {
                    iBlendOver = Theme.blendOver(translateAlert2.getThemedColor(Theme.key_actionBarDefault), 855638016);
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
            Bulletin.addDelegate(this, new Bulletin.Delegate() { // from class: org.telegram.ui.Components.TranslateAlert2.ContainerView.1
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

    public static String capitalFirst(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static CharSequence capitalFirst(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() <= 0) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilderValueOf = charSequence instanceof SpannableStringBuilder ? (SpannableStringBuilder) charSequence : SpannableStringBuilder.valueOf(charSequence);
        spannableStringBuilderValueOf.replace(0, 1, (CharSequence) spannableStringBuilderValueOf.toString().substring(0, 1).toUpperCase());
        return spannableStringBuilderValueOf;
    }

    public static String languageName(String str) {
        return languageName(str, null, null);
    }

    public static String languageName(String str, boolean[] zArr) {
        return languageName(str, zArr, null);
    }

    public static String languageName(String str, boolean[] zArr, boolean[] zArr2) {
        if (str == null || str.equals(TranslateController.UNKNOWN_LANGUAGE) || str.equals("auto")) {
            return null;
        }
        String str2 = str.split("_")[0];
        if ("nb".equals(str2)) {
            str2 = "no";
        }
        if (zArr != null) {
            String string = LocaleController.getString("TranslateLanguage" + str2.toUpperCase());
            boolean z = (string == null || string.startsWith("LOC_ERR")) ? false : true;
            zArr[0] = z;
            if (z) {
                return string;
            }
        }
        if (zArr2 != null) {
            String string2 = LocaleController.getString("TranslateLanguageGenitive" + str2.toUpperCase());
            boolean z2 = (string2 == null || string2.startsWith("LOC_ERR")) ? false : true;
            zArr2[0] = z2;
            if (z2) {
                return string2;
            }
        }
        String strSystemLanguageName = systemLanguageName(str);
        if (strSystemLanguageName == null) {
            strSystemLanguageName = systemLanguageName(str2);
        }
        if (strSystemLanguageName != null) {
            return strSystemLanguageName;
        }
        if ("no".equals(str)) {
            str = "nb";
        }
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        LocaleController.LocaleInfo builtinLanguageByPlural = LocaleController.getInstance().getBuiltinLanguageByPlural(str);
        if (builtinLanguageByPlural == null) {
            return null;
        }
        if (currentLocaleInfo != null && "en".equals(currentLocaleInfo.pluralLangCode)) {
            return builtinLanguageByPlural.nameEnglish;
        }
        return builtinLanguageByPlural.name;
    }

    public static String languageNameCapital(String str) {
        String strLanguageName = languageName(str);
        if (strLanguageName == null) {
            return null;
        }
        return strLanguageName.substring(0, 1).toUpperCase() + strLanguageName.substring(1);
    }

    public static String systemLanguageName(String str) {
        return systemLanguageName(str, false);
    }

    public static String systemLanguageName(String str, boolean z) {
        if (str == null) {
            return null;
        }
        if (localesByCode == null) {
            localesByCode = new HashMap<>();
            try {
                Locale[] availableLocales = Locale.getAvailableLocales();
                for (int i = 0; i < availableLocales.length; i++) {
                    localesByCode.put(availableLocales[i].getLanguage(), availableLocales[i]);
                    String country = availableLocales[i].getCountry();
                    if (country != null && country.length() > 0) {
                        localesByCode.put(availableLocales[i].getLanguage() + "-" + country.toLowerCase(), availableLocales[i]);
                    }
                }
            } catch (Exception unused) {
            }
        }
        String lowerCase = str.replace("_", "-").toLowerCase();
        try {
            Locale locale = localesByCode.get(lowerCase);
            if (locale != null) {
                String displayLanguage = locale.getDisplayLanguage(z ? locale : Locale.getDefault());
                if (!lowerCase.contains("-")) {
                    return displayLanguage;
                }
                String displayCountry = locale.getDisplayCountry(z ? locale : Locale.getDefault());
                if (TextUtils.isEmpty(displayCountry)) {
                    return displayLanguage;
                }
                return displayLanguage + " (" + displayCountry + ")";
            }
        } catch (Exception unused2) {
        }
        return null;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.translationModelDownloaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.translationModelDownloading);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    public void lambda$new$0() {
        super.lambda$new$0();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.translationModelDownloaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.translationModelDownloading);
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            this.loadingTextView.invalidate();
            this.textView.invalidate();
        }
    }

    public void updateButtonShadow(boolean z) {
        Boolean bool = this.buttonShadowShown;
        if (bool == null || bool.booleanValue() != z) {
            this.buttonShadowShown = Boolean.valueOf(z);
        }
    }

    public static TranslateAlert2 showAlert(Context context, BaseFragment baseFragment, int i, TLRPC.InputPeer inputPeer, int i2, boolean z, String str, String str2, CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, boolean z2, Utilities.CallbackReturn<URLSpan, Boolean> callbackReturn, final Runnable runnable) {
        TranslateAlert2 translateAlert2 = new TranslateAlert2(context, str, str2, charSequence, arrayList, inputPeer, i2, z, null, null) { // from class: org.telegram.ui.Components.TranslateAlert2.9
            @Override // org.telegram.ui.Components.TranslateAlert2, org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
            public void lambda$new$0() {
                super.lambda$new$0();
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        };
        translateAlert2.setNoforwards(z2);
        translateAlert2.setFragment(baseFragment);
        translateAlert2.setOnLinkPress(callbackReturn);
        if (baseFragment != null) {
            if (baseFragment.getParentActivity() != null) {
                baseFragment.showDialog(translateAlert2);
            }
            return translateAlert2;
        }
        translateAlert2.show();
        return translateAlert2;
    }

    public static TranslateAlert2 showAlert(Context context, BaseFragment baseFragment, int i, TLRPC.InputPeer inputPeer, int i2, String str, String str2, TL_iv.RichMessage richMessage, boolean z, Utilities.CallbackReturn<URLSpan, Boolean> callbackReturn, final Runnable runnable) {
        if (context == null) {
            return null;
        }
        TranslateAlert2 translateAlert2 = new TranslateAlert2(context, str, str2, null, null, inputPeer, i2, false, richMessage, null) { // from class: org.telegram.ui.Components.TranslateAlert2.10
            @Override // org.telegram.ui.Components.TranslateAlert2, org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
            public void lambda$new$0() {
                super.lambda$new$0();
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        };
        translateAlert2.setNoforwards(z);
        translateAlert2.setFragment(baseFragment);
        translateAlert2.setOnLinkPress(callbackReturn);
        if (baseFragment != null) {
            if (baseFragment.getParentActivity() != null) {
                baseFragment.showDialog(translateAlert2);
            }
            return translateAlert2;
        }
        translateAlert2.show();
        return translateAlert2;
    }

    public static TranslateAlert2 showAlert(Context context, BaseFragment baseFragment, int i, String str, String str2, CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, boolean z, Utilities.CallbackReturn<URLSpan, Boolean> callbackReturn, final Runnable runnable) {
        if (context == null) {
            return null;
        }
        TranslateAlert2 translateAlert2 = new TranslateAlert2(context, str, str2, charSequence, arrayList, null) { // from class: org.telegram.ui.Components.TranslateAlert2.11
            @Override // org.telegram.ui.Components.TranslateAlert2, org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
            public void lambda$new$0() {
                super.lambda$new$0();
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        };
        translateAlert2.setNoforwards(z);
        translateAlert2.setFragment(baseFragment);
        translateAlert2.setOnLinkPress(callbackReturn);
        if (baseFragment != null) {
            if (baseFragment.getParentActivity() != null) {
                baseFragment.showDialog(translateAlert2);
            }
            return translateAlert2;
        }
        translateAlert2.show();
        return translateAlert2;
    }

    public static String getToLanguage() {
        return TranslatorUtils.getResolvedTargetLanguageCode();
    }

    public static void setToLanguage(String str) {
        TranslatorUtils.setTargetLanguage(str);
    }
}
