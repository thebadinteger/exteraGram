package com.exteragram.messenger.math;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001BB\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012!\u0010\u0007\u001a\u001d\u0012\u0013\u0012\u00110\t¢\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\u0004\u0012\u00020\t0\b¢\u0006\u0004\b\r\u0010\u000eR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R,\u0010\u0007\u001a\u001d\u0012\u0013\u0012\u00110\t¢\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0015"}, d2 = {"Lcom/exteragram/messenger/math/PostfixOperator;", _UrlKt.FRAGMENT_ENCODE_SET, "symbols", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "percent", _UrlKt.FRAGMENT_ENCODE_SET, "apply", "Lkotlin/Function1;", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/ParameterName;", "name", "value", "<init>", "(Ljava/util/List;ZLkotlin/jvm/functions/Function1;)V", "getSymbols", "()Ljava/util/List;", "getPercent", "()Z", "getApply", "()Lkotlin/jvm/functions/Function1;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class PostfixOperator {
    private final Function1<Double, Double> apply;
    private final boolean percent;
    private final List<String> symbols;

    /* JADX WARN: Multi-variable type inference failed */
    public PostfixOperator(List<String> list, boolean z, Function1<Double, Double> function1) {
        this.symbols = list;
        this.percent = z;
        this.apply = function1;
    }

    public /* synthetic */ PostfixOperator(List list, boolean z, Function1 function1, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(list, (i & 2) != 0 ? false : z, function1);
    }

    public final List<String> getSymbols() {
        return this.symbols;
    }

    public final boolean getPercent() {
        return this.percent;
    }

    public final Function1<Double, Double> getApply() {
        return this.apply;
    }
}
