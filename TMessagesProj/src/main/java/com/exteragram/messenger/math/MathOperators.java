package com.exteragram.messenger.math;

import com.google.android.gms.cast.MediaTrack;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.math.MathKt;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\r\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\n\n\u0002\u0010$\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\u0010\f\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u001f\u0010\r\u001a\u0004\u0018\u00010\f2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\n¢\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u0011\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u000f\u001a\u00020\f¢\u0006\u0004\b\u0011\u0010\u0012J\u0017\u0010\u0014\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u000f\u001a\u00020\f¢\u0006\u0004\b\u0014\u0010\u0015J\u0017\u0010\u0017\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u000f\u001a\u00020\f¢\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u001b\u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u0019\u001a\u00020\f¢\u0006\u0004\b\u001b\u0010\u001cR\u001d\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00100\u001d8\u0006¢\u0006\f\n\u0004\b\u001e\u0010\u001f\u001a\u0004\b \u0010!R\u001d\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00130\u001d8\u0006¢\u0006\f\n\u0004\b\"\u0010\u001f\u001a\u0004\b#\u0010!R\u001d\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00160\u001d8\u0006¢\u0006\f\n\u0004\b$\u0010\u001f\u001a\u0004\b%\u0010!R\u001d\u0010&\u001a\b\u0012\u0004\u0012\u00020\u001a0\u001d8\u0006¢\u0006\f\n\u0004\b&\u0010\u001f\u001a\u0004\b'\u0010!R#\u0010)\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00040(8\u0006¢\u0006\f\n\u0004\b)\u0010*\u001a\u0004\b+\u0010,R\u001a\u0010-\u001a\b\u0012\u0004\u0012\u00020\f0\u001d8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b-\u0010\u001fR\u001d\u00100\u001a\b\u0012\u0004\u0012\u00020/0.8\u0006¢\u0006\f\n\u0004\b0\u00101\u001a\u0004\b2\u00103¨\u00064"}, d2 = {"Lcom/exteragram/messenger/math/MathOperators;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", _UrlKt.FRAGMENT_ENCODE_SET, "value", "factorial", "(D)D", _UrlKt.FRAGMENT_ENCODE_SET, "text", _UrlKt.FRAGMENT_ENCODE_SET, "at", _UrlKt.FRAGMENT_ENCODE_SET, "matchSymbol", "(Ljava/lang/CharSequence;I)Ljava/lang/String;", "symbol", "Lcom/exteragram/messenger/math/InfixOperator;", "infixFor", "(Ljava/lang/String;)Lcom/exteragram/messenger/math/InfixOperator;", "Lcom/exteragram/messenger/math/PrefixOperator;", "prefixFor", "(Ljava/lang/String;)Lcom/exteragram/messenger/math/PrefixOperator;", "Lcom/exteragram/messenger/math/PostfixOperator;", "postfixFor", "(Ljava/lang/String;)Lcom/exteragram/messenger/math/PostfixOperator;", "name", "Lcom/exteragram/messenger/math/MathFunction;", "functionFor", "(Ljava/lang/String;)Lcom/exteragram/messenger/math/MathFunction;", _UrlKt.FRAGMENT_ENCODE_SET, "infix", "Ljava/util/List;", "getInfix", "()Ljava/util/List;", "prefix", "getPrefix", "postfix", "getPostfix", "functions", "getFunctions", _UrlKt.FRAGMENT_ENCODE_SET, "constants", "Ljava/util/Map;", "getConstants", "()Ljava/util/Map;", "symbols", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "symbolChars", "Ljava/util/Set;", "getSymbolChars", "()Ljava/util/Set;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nMathOperators.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MathOperators.kt\ncom/exteragram/messenger/math/MathOperators\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,169:1\n296#2,2:170\n296#2,2:172\n296#2,2:174\n296#2,2:176\n296#2,2:178\n1915#2,2:180\n1915#2,2:182\n1915#2,2:184\n1080#2:186\n1915#2:187\n1916#2:190\n1915#2:191\n1916#2:194\n1198#3,2:188\n1198#3,2:192\n*S KotlinDebug\n*F\n+ 1 MathOperators.kt\ncom/exteragram/messenger/math/MathOperators\n*L\n146#1:170,2\n150#1:172,2\n152#1:174,2\n154#1:176,2\n156#1:178,2\n132#1:180,2\n133#1:182,2\n134#1:184,2\n135#1:186\n138#1:187\n138#1:190\n139#1:191\n139#1:194\n138#1:188,2\n139#1:192,2\n*E\n"})
public final class MathOperators {
    public static final MathOperators INSTANCE = new MathOperators();
    private static final Map<String, Double> constants;
    private static final List<MathFunction> functions;
    private static final List<InfixOperator> infix;
    private static final List<PostfixOperator> postfix;
    private static final List<PrefixOperator> prefix;
    private static final Set<Character> symbolChars;
    private static final List<String> symbols;

    public static double $r8$lambda$S_4WW7PoHbBxDlspQ5N_JRX0z3E(double d) {
        return d;
    }

    private MathOperators() {
    }

    static {
        List<InfixOperator> listListOf = CollectionsKt.listOf(new InfixOperator[]{new InfixOperator(CollectionsKt.listOf("+"), 10, false, new Function3() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function3
            public final Object invoke(Object obj, Object obj2, Object obj3) {
                return Double.valueOf(MathOperators.$r8$lambda$PdmqHd67MmY6SKLIBqdx6ABWaHA(((Double) obj).doubleValue(), ((Double) obj2).doubleValue(), ((Boolean) obj3).booleanValue()));
            }
        }, 4, null), new InfixOperator(CollectionsKt.listOf(new String[]{"-", "−"}), 10, false, new Function3() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda11
            @Override // kotlin.jvm.functions.Function3
            public final Object invoke(Object obj, Object obj2, Object obj3) {
                return Double.valueOf(MathOperators.m1522$r8$lambda$sdagQo9XABXCkW290RSZx00_1Q(((Double) obj).doubleValue(), ((Double) obj2).doubleValue(), ((Boolean) obj3).booleanValue()));
            }
        }, 4, null), new InfixOperator(CollectionsKt.listOf(new String[]{"*", "×"}), 20, false, new Function3() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda22
            @Override // kotlin.jvm.functions.Function3
            public final Object invoke(Object obj, Object obj2, Object obj3) {
                return Double.valueOf(MathOperators.m1514$r8$lambda$NXVI9Lp4mK_LTV06it5t7lb3Fk(((Double) obj).doubleValue(), ((Double) obj2).doubleValue(), ((Boolean) obj3).booleanValue()));
            }
        }, 4, null), new InfixOperator(CollectionsKt.listOf(new String[]{"/", "÷"}), 20, false, new Function3() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda27
            @Override // kotlin.jvm.functions.Function3
            public final Object invoke(Object obj, Object obj2, Object obj3) {
                return Double.valueOf(MathOperators.$r8$lambda$qDx7KSHZSEy2I8Nv_PQ5qYjADq4(((Double) obj).doubleValue(), ((Double) obj2).doubleValue(), ((Boolean) obj3).booleanValue()));
            }
        }, 4, null), new InfixOperator(CollectionsKt.listOf("^"), 40, true, new Function3() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda28
            @Override // kotlin.jvm.functions.Function3
            public final Object invoke(Object obj, Object obj2, Object obj3) {
                double dDoubleValue = ((Double) obj).doubleValue();
                double dDoubleValue2 = ((Double) obj2).doubleValue();
                ((Boolean) obj3).booleanValue();
                return Double.valueOf(Math.pow(dDoubleValue, dDoubleValue2));
            }
        })});
        infix = listListOf;
        prefix = CollectionsKt.listOf(new PrefixOperator[]{new PrefixOperator(CollectionsKt.listOf(new String[]{"-", "−"}), 30, false, new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda29
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(MathOperators.m1516$r8$lambda$U0VnCyXeHU4faq0CS1bbkcX9mE(((Double) obj).doubleValue()));
            }
        }, 4, null), new PrefixOperator(CollectionsKt.listOf("+"), 30, false, new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda30
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(MathOperators.$r8$lambda$S_4WW7PoHbBxDlspQ5N_JRX0z3E(((Double) obj).doubleValue()));
            }
        }, 4, null), new PrefixOperator(CollectionsKt.listOf("√"), 40, true, new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda31
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.sqrt(((Double) obj).doubleValue()));
            }
        })});
        postfix = CollectionsKt.listOf(new PostfixOperator[]{new PostfixOperator(CollectionsKt.listOf("%"), true, new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda32
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(MathOperators.$r8$lambda$_1ulvUA5i0TMrKEIFncewapu3aY(((Double) obj).doubleValue()));
            }
        }), new PostfixOperator(CollectionsKt.listOf("!"), false, new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda33
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(MathOperators.INSTANCE.factorial(((Double) obj).doubleValue()));
            }
        }, 2, null)});
        functions = CollectionsKt.listOf(new MathFunction[]{new MathFunction("sqrt", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.sqrt(((double[]) obj)[0]));
            }
        }), new MathFunction("cbrt", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.cbrt(((double[]) obj)[0]));
            }
        }), new MathFunction("abs", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda3
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.abs(((double[]) obj)[0]));
            }
        }), new MathFunction(MediaTrack.ROLE_SIGN, new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda4
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.signum(((double[]) obj)[0]));
            }
        }), new MathFunction("round", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda5
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(MathKt.roundToLong(((double[]) obj)[0]));
            }
        }), new MathFunction("floor", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda6
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.floor(((double[]) obj)[0]));
            }
        }), new MathFunction("ceil", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda7
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.ceil(((double[]) obj)[0]));
            }
        }), new MathFunction("fact", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda8
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(MathOperators.INSTANCE.factorial(((double[]) obj)[0]));
            }
        }), new MathFunction("min", new IntRange(1, Integer.MAX_VALUE), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda9
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(ArraysKt.minOrThrow((double[]) obj));
            }
        }), new MathFunction("max", new IntRange(1, Integer.MAX_VALUE), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda10
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(ArraysKt.maxOrThrow((double[]) obj));
            }
        }), new MathFunction("log", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda12
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.log10(((double[]) obj)[0]));
            }
        }), new MathFunction("log2", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda13
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(MathKt.log2(((double[]) obj)[0]));
            }
        }), new MathFunction("ln", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda14
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.log(((double[]) obj)[0]));
            }
        }), new MathFunction("exp", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda15
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.exp(((double[]) obj)[0]));
            }
        }), new MathFunction("sin", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda16
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.sin(((double[]) obj)[0]));
            }
        }), new MathFunction("cos", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda17
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.cos(((double[]) obj)[0]));
            }
        }), new MathFunction("tan", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda18
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.tan(((double[]) obj)[0]));
            }
        }), new MathFunction("asin", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda19
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.asin(((double[]) obj)[0]));
            }
        }), new MathFunction("acos", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda20
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.acos(((double[]) obj)[0]));
            }
        }), new MathFunction("atan", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda21
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.atan(((double[]) obj)[0]));
            }
        }), new MathFunction("atan2", new IntRange(2, 2), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda23
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                double[] dArr = (double[]) obj;
                return Double.valueOf(Math.atan2(dArr[0], dArr[1]));
            }
        }), new MathFunction("sinh", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda24
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.sinh(((double[]) obj)[0]));
            }
        }), new MathFunction("cosh", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda25
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.cosh(((double[]) obj)[0]));
            }
        }), new MathFunction("tanh", new IntRange(1, 1), new Function1() { // from class: com.exteragram.messenger.math.MathOperators$$ExternalSyntheticLambda26
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Double.valueOf(Math.tanh(((double[]) obj)[0]));
            }
        })});
        Double dValueOf = Double.valueOf(3.141592653589793d);
        constants = MapsKt.mapOf(TuplesKt.to("pi", dValueOf), TuplesKt.to("π", dValueOf), TuplesKt.to("e", Double.valueOf(2.718281828459045d)));
        List listCreateListBuilder = CollectionsKt.createListBuilder();
        Iterator it = listListOf.iterator();
        while (it.hasNext()) {
            listCreateListBuilder.addAll(((InfixOperator) it.next()).getSymbols());
        }
        Iterator it2 = prefix.iterator();
        while (it2.hasNext()) {
            listCreateListBuilder.addAll(((PrefixOperator) it2.next()).getSymbols());
        }
        Iterator it3 = postfix.iterator();
        while (it3.hasNext()) {
            listCreateListBuilder.addAll(((PostfixOperator) it3.next()).getSymbols());
        }
        List<String> listSortedWith = CollectionsKt.sortedWith(CollectionsKt.distinct(CollectionsKt.build(listCreateListBuilder)), new Comparator() { // from class: com.exteragram.messenger.math.MathOperators$special$$inlined$sortedByDescending$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(Object t, Object t2) {
                return ComparisonsKt.compareValues(Integer.valueOf(((String) t2).length()), Integer.valueOf(((String) t).length()));
            }
        });
        symbols = listSortedWith;
        Set setCreateSetBuilder = SetsKt.createSetBuilder();
        Iterator it4 = listSortedWith.iterator();
        while (true) {
            if (!it4.hasNext()) {
                break;
            }
            String str = (String) it4.next();
            for (int i = 0; i < str.length(); i++) {
                setCreateSetBuilder.add(Character.valueOf(str.charAt(i)));
            }
        }
        for (String str2 : constants.keySet()) {
            for (int i2 = 0; i2 < str2.length(); i2++) {
                char cCharAt = str2.charAt(i2);
                if (!Character.isLetter(cCharAt)) {
                    setCreateSetBuilder.add(Character.valueOf(cCharAt));
                }
            }
        }
        setCreateSetBuilder.add('(');
        setCreateSetBuilder.add(')');
        setCreateSetBuilder.add(';');
        symbolChars = SetsKt.build(setCreateSetBuilder);
    }

    public static double $r8$lambda$PdmqHd67MmY6SKLIBqdx6ABWaHA(double d, double d2, boolean z) {
        return z ? d + (d2 * d) : d + d2;
    }

    /* JADX INFO: renamed from: $r8$lambda$sdagQo9XABXCk-W290RSZx00_1Q, reason: not valid java name */
    public static double m1522$r8$lambda$sdagQo9XABXCkW290RSZx00_1Q(double d, double d2, boolean z) {
        return z ? d - (d2 * d) : d - d2;
    }

    /* JADX INFO: renamed from: $r8$lambda$NX-VI9Lp4mK_LTV06it5t7lb3Fk, reason: not valid java name */
    public static double m1514$r8$lambda$NXVI9Lp4mK_LTV06it5t7lb3Fk(double d, double d2, boolean z) {
        return d * d2;
    }

    public static double $r8$lambda$qDx7KSHZSEy2I8Nv_PQ5qYjADq4(double d, double d2, boolean z) {
        return d / d2;
    }

    /* JADX INFO: renamed from: $r8$lambda$U0VnCyXeHU4f-aq0CS1bbkcX9mE, reason: not valid java name */
    public static double m1516$r8$lambda$U0VnCyXeHU4faq0CS1bbkcX9mE(double d) {
        return -d;
    }

    public static double $r8$lambda$_1ulvUA5i0TMrKEIFncewapu3aY(double d) {
        return d / 100.0d;
    }

    public final Map<String, Double> getConstants() {
        return constants;
    }

    public final Set<Character> getSymbolChars() {
        return symbolChars;
    }

    public final String matchSymbol(CharSequence text, int at) {
        Object next;
        CharSequence charSequence;
        int i;
        Iterator it = symbols.iterator();
        while (it.hasNext()) {
            next = it.next();
            String str = (String) next;
            if (str.length() + at <= text.length()) {
                charSequence = text;
                i = at;
                if (charSequence.toString().regionMatches(i, str, 0, str.length())) {
                    return (String) next;
                }
            } else {
                charSequence = text;
                i = at;
            }
            text = charSequence;
            at = i;
        }
        next = null;
        return (String) next;
    }

    public final InfixOperator infixFor(String symbol) {
        Object next;
        Iterator it = infix.iterator();
        while (it.hasNext()) {
            next = it.next();
            if (((InfixOperator) next).getSymbols().contains(symbol)) {
                return (InfixOperator) next;
            }
        }
        next = null;
        return (InfixOperator) next;
    }

    public final PrefixOperator prefixFor(String symbol) {
        Object next;
        Iterator it = prefix.iterator();
        while (it.hasNext()) {
            next = it.next();
            if (((PrefixOperator) next).getSymbols().contains(symbol)) {
                return (PrefixOperator) next;
            }
        }
        next = null;
        return (PrefixOperator) next;
    }

    public final PostfixOperator postfixFor(String symbol) {
        Object next;
        Iterator it = postfix.iterator();
        while (it.hasNext()) {
            next = it.next();
            if (((PostfixOperator) next).getSymbols().contains(symbol)) {
                return (PostfixOperator) next;
            }
        }
        next = null;
        return (PostfixOperator) next;
    }

    public final MathFunction functionFor(String name) {
        Object next;
        Iterator it = functions.iterator();
        while (it.hasNext()) {
            next = it.next();
            if (Intrinsics.areEqual(((MathFunction) next).getName(), name)) {
                return (MathFunction) next;
            }
        }
        next = null;
        return (MathFunction) next;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final double factorial(double value) {
        if (value < 0.0d || value != Math.floor(value) || value > 170.0d) {
            return Double.NaN;
        }
        int i = (int) value;
        double d = 1.0d;
        int i2 = 2;
        if (2 <= i) {
            while (true) {
                d *= (double) i2;
                if (i2 == i) {
                    break;
                }
                i2++;
            }
        }
        return d;
    }
}
