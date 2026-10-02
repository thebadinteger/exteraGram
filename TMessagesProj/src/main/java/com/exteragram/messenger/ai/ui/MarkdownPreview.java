package com.exteragram.messenger.ai.ui;

import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CodeHighlighting;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.MarkdownParser;
import org.telegram.ui.Components.QuoteSpan;
import org.telegram.ui.iv.RichMessageConvert;

/* JADX INFO: loaded from: classes4.dex */
public abstract class MarkdownPreview {
    public static CharSequence format(String str) {
        if (TextUtils.isEmpty(str)) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        ArrayList arrayList = new ArrayList();
        try {
            MarkdownParser.parse(str, arrayList);
        } catch (Throwable th) {
            FileLog.e(th);
            arrayList.clear();
        }
        if (!arrayList.isEmpty()) {
            CharSequence charSequenceBlocksToCharSequence = RichMessageConvert.blocksToCharSequence(arrayList);
            if (!TextUtils.isEmpty(charSequenceBlocksToCharSequence)) {
                if (charSequenceBlocksToCharSequence instanceof Spannable) {
                    Spannable spannable = (Spannable) charSequenceBlocksToCharSequence;
                    for (CodeHighlighting.Span span : (CodeHighlighting.Span[]) spannable.getSpans(0, spannable.length(), CodeHighlighting.Span.class)) {
                        int spanStart = spannable.getSpanStart(span);
                        int spanEnd = spannable.getSpanEnd(span);
                        spannable.removeSpan(span);
                        spannable.setSpan(new TypefaceSpan("monospace"), spanStart, spanEnd, 33);
                    }
                }
                AndroidUtilities.removeSpans(charSequenceBlocksToCharSequence, QuoteSpan.QuoteStyleSpan.class);
                AndroidUtilities.removeSpans(charSequenceBlocksToCharSequence, QuoteSpan.class);
                int length = charSequenceBlocksToCharSequence.length();
                while (length > 0 && Character.isWhitespace(charSequenceBlocksToCharSequence.charAt(length - 1))) {
                    length--;
                }
                return length == charSequenceBlocksToCharSequence.length() ? charSequenceBlocksToCharSequence : charSequenceBlocksToCharSequence.subSequence(0, length);
            }
        }
        return str;
    }
}
