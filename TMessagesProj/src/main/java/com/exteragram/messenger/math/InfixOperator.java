package com.exteragram.messenger.math;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u00002\u00020\u0001Bt\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012K\u0010\t\u001aG\u0012\u0013\u0012\u00110\u000b¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0013\u0012\u00110\u000b¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000f\u0012\u0013\u0012\u00110\b¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u0010\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018RV\u0010\t\u001aG\u0012\u0013\u0012\u00110\u000b¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0013\u0012\u00110\u000b¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000f\u0012\u0013\u0012\u00110\b¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u0010\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a¨\u0006\u001b"}, d2 = {"Lcom/exteragram/messenger/math/InfixOperator;", _UrlKt.FRAGMENT_ENCODE_SET, "symbols", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "precedence", _UrlKt.FRAGMENT_ENCODE_SET, "rightAssociative", _UrlKt.FRAGMENT_ENCODE_SET, "apply", "Lkotlin/Function3;", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/ParameterName;", "name", "left", "right", "rightIsPercent", "<init>", "(Ljava/util/List;IZLkotlin/jvm/functions/Function3;)V", "getSymbols", "()Ljava/util/List;", "getPrecedence", "()I", "getRightAssociative", "()Z", "getApply", "()Lkotlin/jvm/functions/Function3;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class InfixOperator {
    private final Function3<Double, Double, Boolean, Double> apply;
    private final int precedence;
    private final boolean rightAssociative;
    private final List<String> symbols;

    /* JADX WARN: Multi-variable type inference failed */
    public InfixOperator(List<String> list, int i, boolean z, Function3<Double, Double, Boolean, Double> function3) {
        this.symbols = list;
        this.precedence = i;
        this.rightAssociative = z;
        this.apply = function3;
    }

    public /* synthetic */ InfixOperator(List list, int i, boolean z, Function3 function3, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(list, i, (i2 & 4) != 0 ? false : z, function3);
    }

    public final List<String> getSymbols() {
        return this.symbols;
    }

    public final int getPrecedence() {
        return this.precedence;
    }

    public final boolean getRightAssociative() {
        return this.rightAssociative;
    }

    public final Function3<Double, Double, Boolean, Double> getApply() {
        return this.apply;
    }
}
