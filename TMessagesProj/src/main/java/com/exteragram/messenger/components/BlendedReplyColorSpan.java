package com.exteragram.messenger.components;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"}, d2 = {"Lcom/exteragram/messenger/components/BlendedReplyColorSpan;", "Landroid/text/style/CharacterStyle;", "<init>", "()V", "updateDrawState", _UrlKt.FRAGMENT_ENCODE_SET, "tp", "Landroid/text/TextPaint;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class BlendedReplyColorSpan extends CharacterStyle {
    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint tp) {
        tp.setColor(BlendedReplySpansKt.blendedReplyColor$default(0.0f, 1, null));
    }
}
