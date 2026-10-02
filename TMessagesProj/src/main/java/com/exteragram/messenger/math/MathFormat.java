package com.exteragram.messenger.math;

import java.math.BigDecimal;
import java.math.MathContext;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.mvel2.MVEL;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001f\u0010\t\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\t\u0010\n¨\u0006\u000b"}, d2 = {"Lcom/exteragram/messenger/math/MathFormat;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", _UrlKt.FRAGMENT_ENCODE_SET, "value", _UrlKt.FRAGMENT_ENCODE_SET, "decimalSeparator", _UrlKt.FRAGMENT_ENCODE_SET, "format", "(DC)Ljava/lang/String;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class MathFormat {
    public static final MathFormat INSTANCE = new MathFormat();

    private MathFormat() {
    }

    public final String format(double value, char decimalSeparator) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return null;
        }
        double dAbs = Math.abs(value);
        if (dAbs >= 1.0E12d) {
            return null;
        }
        if (dAbs != 0.0d && dAbs < 1.0E-9d) {
            return null;
        }
        String plainString = BigDecimal.valueOf(value).round(new MathContext(12)).stripTrailingZeros().toPlainString();
        if (Intrinsics.areEqual(plainString, "-0")) {
            plainString = "0";
        }
        String str = plainString;
        if (str.length() > 24) {
            return null;
        }
        return decimalSeparator == '.' ? str : str.replace('.', decimalSeparator);
    }
}
