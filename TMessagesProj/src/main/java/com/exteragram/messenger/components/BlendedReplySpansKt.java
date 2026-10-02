package com.exteragram.messenger.components;

import android.graphics.Color;
import kotlin.Metadata;
import kotlin.ranges.RangesKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.ui.ActionBar.Theme;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\u001a\u0012\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"}, d2 = {"baseHsv", _UrlKt.FRAGMENT_ENCODE_SET, "nameHsv", "blendedReplyColor", _UrlKt.FRAGMENT_ENCODE_SET, "strength", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 2, mv = {2, 2, 0}, xi = 48)
public abstract class BlendedReplySpansKt {
    private static final float[] baseHsv = new float[3];
    private static final float[] nameHsv = new float[3];

    public static /* synthetic */ int blendedReplyColor$default(float f, int i, Object obj) {
        if ((i & 1) != 0) {
            f = 0.75f;
        }
        return blendedReplyColor(f);
    }

    private static final int blendedReplyColor(float f) {
        int color = Theme.chat_replyTextPaint.getColor();
        int color2 = Theme.chat_replyNamePaint.getColor();
        float[] fArr = baseHsv;
        Color.colorToHSV(color, fArr);
        float[] fArr2 = nameHsv;
        Color.colorToHSV(color2, fArr2);
        if (fArr[2] < 0.35f) {
            fArr[2] = 0.5f;
        }
        fArr[0] = fArr2[0];
        fArr[1] = RangesKt.coerceIn(fArr2[1] * f, 0.0f, 1.0f);
        return Color.HSVToColor(Color.alpha(color), fArr);
    }
}
