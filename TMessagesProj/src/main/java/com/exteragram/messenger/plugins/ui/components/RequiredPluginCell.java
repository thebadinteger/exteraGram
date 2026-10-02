package com.exteragram.messenger.plugins.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.exteragram.messenger.plugins.PluginsConstants;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\b\u0007\u0018\u0000 \"2\u00020\u0001:\u0002\"#B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016H\u0014J\u0010\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001aH\u0014J\u000e\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u0012J \u0010\u001d\u001a\u00020\u00142\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u000e\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\u0010J\u000e\u0010\u001e\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020\u0012J\u0010\u0010 \u001a\u00020\u00162\u0006\u0010!\u001a\u00020\u0016H\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006$"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/RequiredPluginCell;", "Landroid/widget/LinearLayout;", "context", "Landroid/content/Context;", "resourcesProvider", "Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;", "<init>", "(Landroid/content/Context;Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;)V", "textView", "Lorg/telegram/ui/ActionBar/SimpleTextView;", "subtitleView", "actionButton", "Lorg/telegram/ui/Components/ProgressButton;", "info", "Lcom/exteragram/messenger/plugins/ui/components/RequiredPluginInfo;", "delegate", "Lorg/telegram/messenger/Utilities$Callback;", "needDivider", _UrlKt.FRAGMENT_ENCODE_SET, "onMeasure", _UrlKt.FRAGMENT_ENCODE_SET, "widthMeasureSpec", _UrlKt.FRAGMENT_ENCODE_SET, "heightMeasureSpec", "onDraw", "canvas", "Landroid/graphics/Canvas;", "setNeedDivider", PluginsConstants.Settings.TYPE_DIVIDER, "set", "setLoading", "loading", "getThemedColor", PluginsConstants.Settings.KEY, "Companion", "Factory", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SuppressLint({"ViewConstructor"})
@SourceDebugExtension({"SMAP\nRequiredPluginCell.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RequiredPluginCell.kt\ncom/exteragram/messenger/plugins/ui/components/RequiredPluginCell\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,295:1\n1#2:296\n*E\n"})
public final class RequiredPluginCell extends LinearLayout {
    private static final int BUTTON_GAP_DP = 12;
    private static final int BUTTON_HEIGHT_DP = 28;
    private static final int BUTTON_RIGHT_PADDING = 18;
    private static final int HEIGHT_DP = 64;
    private static final int LEFT_PADDING = 21;
    private static final float LINE_GAP = 4.0f;
    private final ProgressButton actionButton;
    private Utilities.Callback<RequiredPluginInfo> delegate;
    private RequiredPluginInfo info;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private final SimpleTextView subtitleView;
    private final SimpleTextView textView;

    public RequiredPluginCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setOrientation(0);
        setGravity(16);
        setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(18.0f), 0);
        SimpleTextView simpleTextView = new SimpleTextView(context);
        simpleTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        simpleTextView.setTextSize(16);
        simpleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        simpleTextView.setImportantForAccessibility(2);
        this.textView = simpleTextView;
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        simpleTextView2.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        simpleTextView2.setTextSize(13);
        simpleTextView2.setGravity(LocaleController.isRTL ? 5 : 3);
        simpleTextView2.setImportantForAccessibility(2);
        this.subtitleView = simpleTextView2;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.addView(simpleTextView, LayoutHelper.createLinear(-1, -2));
        linearLayout.addView(simpleTextView2, LayoutHelper.createLinear(-1, -2, 0.0f, LINE_GAP, 0.0f, 0.0f));
        ProgressButton progressButton = new ProgressButton(context);
        this.actionButton = progressButton;
        ScaleStateListAnimator.apply(progressButton, 0.05f, 1.5f);
        progressButton.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.plugins.ui.components.RequiredPluginCell$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RequiredPluginCell.$r8$lambda$uedXg2VNTw6uGtLndaaVv098w8A(RequiredPluginCell.this, view);
            }
        });
        ViewGroup.LayoutParams layoutParamsCreateLinear = LayoutHelper.createLinear(0, -2, 1.0f, 16);
        boolean z = LocaleController.isRTL;
        ViewGroup.LayoutParams layoutParamsCreateLinear2 = LayoutHelper.createLinear(-2, 28, 16, z ? 0.0f : 12.0f, 0.0f, z ? 12.0f : 0.0f, 0.0f);
        if (LocaleController.isRTL) {
            addView(progressButton, layoutParamsCreateLinear2);
            addView(linearLayout, layoutParamsCreateLinear);
        } else {
            addView(linearLayout, layoutParamsCreateLinear);
            addView(progressButton, layoutParamsCreateLinear2);
        }
    }

    public static void $r8$lambda$uedXg2VNTw6uGtLndaaVv098w8A(RequiredPluginCell requiredPluginCell, View view) {
        Utilities.Callback<RequiredPluginInfo> callback;
        RequiredPluginInfo requiredPluginInfo = requiredPluginCell.info;
        if (requiredPluginInfo == null || (callback = requiredPluginCell.delegate) == null) {
            return;
        }
        callback.run(requiredPluginInfo);
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), TLObject.FLAG_30));
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onDraw(Canvas canvas) {
        Paint paint;
        if (this.needDivider) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            if (resourcesProvider == null || (paint = resourcesProvider.getPaint("paintDivider")) == null) {
                paint = Theme.dividerPaint;
            }
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(21.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(21.0f) : 0), getMeasuredHeight() - 1, paint);
        }
    }

    public final void setNeedDivider(boolean divider) {
        this.needDivider = divider;
        setWillNotDraw(!divider);
        invalidate();
    }

    public final void set(RequiredPluginInfo info, Utilities.Callback<RequiredPluginInfo> delegate) {
        if (info == null) {
            return;
        }
        this.info = info;
        this.delegate = delegate;
        this.textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setText(info.title());
        this.subtitleView.setTextColor(getThemedColor(info.isSatisfied() ? Theme.key_windowBackgroundWhiteGrayText : Theme.key_text_RedRegular));
        this.subtitleView.setText(info.subtitle());
        int themedColor = getThemedColor(Theme.key_featuredStickers_addButton);
        this.actionButton.setText(info.actionText());
        boolean zIsSatisfied = info.isSatisfied();
        ProgressButton progressButton = this.actionButton;
        if (zIsSatisfied) {
            progressButton.setTextColor(themedColor);
            this.actionButton.setBackground(null);
            this.actionButton.setClickable(false);
            this.actionButton.setFocusable(false);
        } else {
            progressButton.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
            this.actionButton.setProgressColor(getThemedColor(Theme.key_featuredStickers_buttonProgress));
            this.actionButton.setBackground(Theme.AdaptiveRipple.createRect(themedColor, getThemedColor(Theme.key_featuredStickers_addButtonPressed), 14.0f));
            this.actionButton.setClickable(true);
            this.actionButton.setFocusable(true);
        }
        requestLayout();
    }

    public final void setLoading(boolean loading) {
        this.actionButton.setDrawProgress(loading, true);
    }

    private final int getThemedColor(int key) {
        return Theme.getColor(key, this.resourcesProvider);
    }

    @Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \u001f2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001fB\u0007¢\u0006\u0004\b\u0003\u0010\u0004J4\u0010\u0005\u001a\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016J4\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\b\u0010\b\u001a\u0004\u0018\u00010\u0019H\u0016J\u0019\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u0014H\u0096\u0002J\u0018\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u0014H\u0016J\b\u0010\u001e\u001a\u00020\u0016H\u0016¨\u0006 "}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/RequiredPluginCell$Factory;", "Lorg/telegram/ui/Components/UItem$UItemFactory;", "Lcom/exteragram/messenger/plugins/ui/components/RequiredPluginCell;", "<init>", "()V", "createView", "context", "Landroid/content/Context;", "listView", "Lorg/telegram/ui/Components/RecyclerListView;", "currentAccount", _UrlKt.FRAGMENT_ENCODE_SET, "classGuid", "resourcesProvider", "Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;", "bindView", _UrlKt.FRAGMENT_ENCODE_SET, PluginsConstants.Settings.VIEW, "Landroid/view/View;", PluginsConstants.Settings.ITEM, "Lorg/telegram/ui/Components/UItem;", PluginsConstants.Settings.TYPE_DIVIDER, _UrlKt.FRAGMENT_ENCODE_SET, "adapter", "Lorg/telegram/ui/Components/UniversalAdapter;", "Lorg/telegram/ui/Components/UniversalRecyclerView;", "equals", "a", "b", "contentsEquals", "isClickable", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Factory extends UItem.UItemFactory<RequiredPluginCell> {

        /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
        public static final Companion INSTANCE = new Companion(null);

        @JvmStatic
        public static final UItem asRequirement(RequiredPluginInfo requiredPluginInfo, Utilities.Callback<RequiredPluginInfo> callback) {
            return INSTANCE.asRequirement(requiredPluginInfo, callback);
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        /* JADX INFO: renamed from: isClickable */
        public boolean isClickable() {
            return false;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public RequiredPluginCell createView(Context context, RecyclerListView listView, int currentAccount, int classGuid, Theme.ResourcesProvider resourcesProvider) {
            return new RequiredPluginCell(context, resourcesProvider);
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public void bindView(View view, UItem item, boolean divider, UniversalAdapter adapter, UniversalRecyclerView listView) {
            if (view instanceof RequiredPluginCell) {
                RequiredPluginCell requiredPluginCell = (RequiredPluginCell) view;
                Object obj = item.object;
                RequiredPluginInfo requiredPluginInfo = obj instanceof RequiredPluginInfo ? (RequiredPluginInfo) obj : null;
                Object obj2 = item.object2;
                requiredPluginCell.set(requiredPluginInfo, obj2 instanceof Utilities.Callback ? (Utilities.Callback) obj2 : null);
                requiredPluginCell.setNeedDivider(divider);
            }
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean equals(UItem a2, UItem b2) {
            return a2.id == b2.id;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean contentsEquals(UItem a2, UItem b2) {
            return a2.id == b2.id && TextUtils.equals(a2.text, b2.text);
        }

        @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J \u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u000e\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\tH\u0007¨\u0006\n"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/RequiredPluginCell$Factory$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "asRequirement", "Lorg/telegram/ui/Components/UItem;", "info", "Lcom/exteragram/messenger/plugins/ui/components/RequiredPluginInfo;", "delegate", "Lorg/telegram/messenger/Utilities$Callback;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            @JvmStatic
            public final UItem asRequirement(RequiredPluginInfo info, Utilities.Callback<RequiredPluginInfo> delegate) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = info.id.hashCode();
                uItemOfFactory.text = info.contentKey();
                uItemOfFactory.object = info;
                uItemOfFactory.object2 = delegate;
                return uItemOfFactory;
            }
        }

        static {
            UItem.UItemFactory.setup(new Factory());
        }
    }
}
