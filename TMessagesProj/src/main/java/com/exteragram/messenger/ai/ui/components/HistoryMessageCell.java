package com.exteragram.messenger.ai.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.exteragram.messenger.ai.data.Message;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

/* JADX INFO: loaded from: classes4.dex */
@SuppressLint({"ViewConstructor"})
public class HistoryMessageCell extends FrameLayout {
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private final TextView textView;
    private final TextView titleView;

    public HistoryMessageCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setWillNotDraw(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 51, 22.0f, 10.0f, 22.0f, 11.0f));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextSize(1, 14.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(3);
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        textView2.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, resourcesProvider));
        textView2.setTextSize(1, 15.0f);
        textView2.setGravity(3);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 3.0f, 0.0f, 0.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), i2);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(AndroidUtilities.dp(22.0f), getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(22.0f), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    public void set(Message message, CharSequence charSequence, boolean z, boolean z2) {
        int i;
        this.needDivider = z2;
        this.textView.setMaxLines(z ? Integer.MAX_VALUE : 6);
        this.textView.setEllipsize(z ? null : TextUtils.TruncateAt.END);
        boolean zEquals = "assistant".equals(message.role());
        this.titleView.setText(LocaleController.getString(!zEquals ? R.string.FromYou : R.string.AIAssistant));
        TextView textView = this.titleView;
        if (!zEquals) {
            i = Theme.key_windowBackgroundWhiteBlueHeader;
        } else {
            i = Theme.key_windowBackgroundWhiteGrayText2;
        }
        textView.setTextColor(Theme.getColor(i, this.resourcesProvider));
        TextView textView2 = this.textView;
        if (charSequence == null) {
            charSequence = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        textView2.setText(Emoji.replaceEmoji(charSequence, textView2.getPaint().getFontMetricsInt(), false));
    }

    public static class Factory extends UItem.UItemFactory<HistoryMessageCell> {
        static {
            UItem.UItemFactory.setup(new Factory());
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public HistoryMessageCell createView(Context context, RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
            return new HistoryMessageCell(context, resourcesProvider);
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public void bindView(View view, UItem uItem, boolean z, UniversalAdapter universalAdapter, UniversalRecyclerView universalRecyclerView) {
            if (view instanceof HistoryMessageCell) {
                HistoryMessageCell historyMessageCell = (HistoryMessageCell) view;
                Object obj = uItem.object;
                if (obj instanceof Message) {
                    historyMessageCell.set((Message) obj, uItem.text, uItem.checked, z);
                }
            }
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean equals(UItem uItem, UItem uItem2) {
            return uItem.id == uItem2.id;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean contentsEquals(UItem uItem, UItem uItem2) {
            Object obj = uItem.object;
            if (obj instanceof Message) {
                Message message = (Message) obj;
                Object obj2 = uItem2.object;
                if (obj2 instanceof Message) {
                    Message message2 = (Message) obj2;
                    if (uItem.checked == uItem2.checked && TextUtils.equals(message.role(), message2.role()) && TextUtils.equals(uItem.text, uItem2.text)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static UItem asHistoryCell(int i, Message message, CharSequence charSequence, boolean z) {
            UItem uItemOfFactory = UItem.ofFactory(Factory.class);
            uItemOfFactory.id = i;
            uItemOfFactory.object = message;
            uItemOfFactory.text = charSequence;
            uItemOfFactory.checked = z;
            return uItemOfFactory;
        }
    }
}
