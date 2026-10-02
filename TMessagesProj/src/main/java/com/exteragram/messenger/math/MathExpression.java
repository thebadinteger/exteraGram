package com.exteragram.messenger.math;

import java.util.ArrayList;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\r\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\f\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\bÆ\u0002\u0018\u00002\u00020\u0001:\u0004$%&'B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J'\u0010\n\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006H\u0002¢\u0006\u0004\b\n\u0010\u000bJ)\u0010\r\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\u0006H\u0002¢\u0006\u0004\b\r\u0010\u000eJ!\u0010\u0010\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u0006H\u0002¢\u0006\u0004\b\u0010\u0010\u0011J\u0017\u0010\u0014\u001a\u00020\t2\u0006\u0010\u0013\u001a\u00020\u0012H\u0002¢\u0006\u0004\b\u0014\u0010\u0015J\u0017\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0013\u001a\u00020\u0012H\u0002¢\u0006\u0004\b\u0016\u0010\u0015J!\u0010\u001b\u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u0017\u001a\u00020\u00042\b\b\u0002\u0010\u0019\u001a\u00020\u0018¢\u0006\u0004\b\u001b\u0010\u001cJ'\u0010\u001f\u001a\u0004\u0018\u00010\u001e2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u0018¢\u0006\u0004\b\u001f\u0010 R\u0014\u0010\"\u001a\u00020!8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\"\u0010#¨\u0006("}, d2 = {"Lcom/exteragram/messenger/math/MathExpression;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", _UrlKt.FRAGMENT_ENCODE_SET, "text", _UrlKt.FRAGMENT_ENCODE_SET, "from", "to", _UrlKt.FRAGMENT_ENCODE_SET, "looksLikeDate", "(Ljava/lang/CharSequence;II)Z", "end", "nextCandidate", "(Ljava/lang/CharSequence;II)Ljava/lang/Integer;", "equalsIndex", "expressionStart", "(Ljava/lang/CharSequence;I)Ljava/lang/Integer;", _UrlKt.FRAGMENT_ENCODE_SET, "c", "isBlank", "(C)Z", "isExpressionChar", "expression", "Lcom/exteragram/messenger/math/MathOptions;", "options", "Lcom/exteragram/messenger/math/MathResult;", "evaluate", "(Ljava/lang/CharSequence;Lcom/exteragram/messenger/math/MathOptions;)Lcom/exteragram/messenger/math/MathResult;", "caret", "Lcom/exteragram/messenger/math/MathSuggestion;", "suggestionAt", "(Ljava/lang/CharSequence;ILcom/exteragram/messenger/math/MathOptions;)Lcom/exteragram/messenger/math/MathSuggestion;", "Lcom/exteragram/messenger/math/MathExpression$MathParseError;", "error", "Lcom/exteragram/messenger/math/MathExpression$MathParseError;", "MathParseError", "Token", "Operand", "Parser", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class MathExpression {
    public static final MathExpression INSTANCE = new MathExpression();
    private static final MathParseError error = new MathParseError();

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isBlank(char c2) {
        return c2 == ' ' || c2 == '\t' || c2 == 160;
    }

    private MathExpression() {
    }

    @Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0002\u0018\u00002\u00060\u0001j\u0002`\u0002B\u0007¢\u0006\u0004\b\u0003\u0010\u0004¨\u0006\u0005"}, d2 = {"Lcom/exteragram/messenger/math/MathExpression$MathParseError;", "Ljava/lang/RuntimeException;", "Lkotlin/RuntimeException;", "<init>", "()V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class MathParseError extends RuntimeException {
        public MathParseError() {
            super(null, null, false, false);
        }
    }

    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\b\u0002\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0010"}, d2 = {"Lcom/exteragram/messenger/math/MathExpression$Token;", _UrlKt.FRAGMENT_ENCODE_SET, "type", _UrlKt.FRAGMENT_ENCODE_SET, "number", _UrlKt.FRAGMENT_ENCODE_SET, "text", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(IDLjava/lang/String;)V", "getType", "()I", "getNumber", "()D", "getText", "()Ljava/lang/String;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Token {
        private final double number;
        private final String text;
        private final int type;

        public Token(int i, double d, String str) {
            this.type = i;
            this.number = d;
            this.text = str;
        }

        public /* synthetic */ Token(int i, double d, String str, int i2, DefaultConstructorMarker defaultConstructorMarker) {
            this(i, (i2 & 2) != 0 ? 0.0d : d, (i2 & 4) != 0 ? _UrlKt.FRAGMENT_ENCODE_SET : str);
        }

        public final double getNumber() {
            return this.number;
        }

        public final String getText() {
            return this.text;
        }

        public final int getType() {
            return this.type;
        }
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\n¨\u0006\u000b"}, d2 = {"Lcom/exteragram/messenger/math/MathExpression$Operand;", _UrlKt.FRAGMENT_ENCODE_SET, "value", _UrlKt.FRAGMENT_ENCODE_SET, "isPercent", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(DZ)V", "getValue", "()D", "()Z", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Operand {
        private final boolean isPercent;
        private final double value;

        public Operand(double d, boolean z) {
            this.value = d;
            this.isPercent = z;
        }

        public final double getValue() {
            return this.value;
        }

        /* JADX INFO: renamed from: isPercent, reason: from getter */
        public final boolean getIsPercent() {
            return this.isPercent;
        }
    }

    public final MathResult evaluate(CharSequence expression, MathOptions options) {
        try {
            return new Parser(expression, options).parse();
        } catch (MathParseError unused) {
            return null;
        }
    }

    public final MathSuggestion suggestionAt(CharSequence text, int caret, MathOptions options) {
        String str;
        if (caret > 0 && caret <= text.length()) {
            int i = caret - 1;
            if (text.charAt(i) == '=') {
                int i2 = caret;
                while (i2 < text.length() && isBlank(text.charAt(i2))) {
                    i2++;
                }
                if (i2 < text.length()) {
                    char cCharAt = text.charAt(i2);
                    if (Character.isDigit(cCharAt) || cCharAt == '.' || cCharAt == ',') {
                        return null;
                    }
                }
                Integer numExpressionStart = expressionStart(text, i);
                if (numExpressionStart != null) {
                    int iIntValue = numExpressionStart.intValue();
                    while (true) {
                        if (!looksLikeDate(text, iIntValue, i)) {
                            StringBuilder sb = new StringBuilder(i - iIntValue);
                            sb.append(text, iIntValue, i);
                            MathResult mathResultEvaluate = evaluate(sb, options);
                            if (mathResultEvaluate != null) {
                                if (mathResultEvaluate.getHasOperation() && (str = MathFormat.INSTANCE.format(mathResultEvaluate.getValue(), mathResultEvaluate.getDecimalSeparator())) != null) {
                                    return new MathSuggestion(caret, isBlank(text.charAt(caret + (-2))) ? " ".concat(str) : str, str);
                                }
                                return null;
                            }
                        }
                        Integer numNextCandidate = nextCandidate(text, iIntValue, i);
                        if (numNextCandidate == null) {
                            break;
                        }
                        iIntValue = numNextCandidate.intValue();
                    }
                }
            }
        }
        return null;
    }

    private final boolean looksLikeDate(CharSequence text, int from, int to) {
        Integer num;
        Integer num2;
        while (from < to && isBlank(text.charAt(from))) {
            from++;
        }
        while (to > from && isBlank(text.charAt(to - 1))) {
            to--;
        }
        if (to - from < 6) {
            return false;
        }
        ArrayList arrayList = new ArrayList(4);
        int i = 0;
        char c2 = ' ';
        while (from < to) {
            char cCharAt = text.charAt(from);
            if (Character.isDigit(cCharAt)) {
                i++;
            } else {
                if (cCharAt != '/' && cCharAt != '.' && cCharAt != '-') {
                    return false;
                }
                if (c2 == ' ') {
                    c2 = cCharAt;
                } else if (c2 != cCharAt) {
                    return false;
                }
                if (i == 0 || arrayList.size() == 2) {
                    return false;
                }
                arrayList.add(Integer.valueOf(i));
                i = 0;
            }
            from++;
        }
        if (i != 0 && arrayList.size() == 2) {
            arrayList.add(Integer.valueOf(i));
            switch (c2) {
                case '-':
                    Integer num3 = (Integer) arrayList.get(0);
                    if (num3 != null && num3.intValue() == 4 && ((Number) arrayList.get(1)).intValue() <= 2 && ((Number) arrayList.get(2)).intValue() <= 2) {
                        return true;
                    }
                    break;
                case '.':
                case '/':
                    return ((Number) arrayList.get(0)).intValue() <= 2 && ((Number) arrayList.get(1)).intValue() <= 2 && (((num = (Integer) arrayList.get(2)) != null && num.intValue() == 2) || ((num2 = (Integer) arrayList.get(2)) != null && num2.intValue() == 4));
                default:
                    return false;
            }
        }
        return false;
    }

    private final Integer nextCandidate(CharSequence text, int from, int end) {
        while (from < end) {
            char cCharAt = text.charAt(from);
            from++;
            if (cCharAt == '(') {
                return Integer.valueOf(from);
            }
            if (isBlank(cCharAt)) {
                while (from < end && isBlank(text.charAt(from))) {
                    from++;
                }
                if (from >= end) {
                    break;
                }
                return Integer.valueOf(from);
            }
        }
        return null;
    }

    private final Integer expressionStart(CharSequence text, int equalsIndex) {
        int iMax = Math.max(0, equalsIndex - 64);
        int i = equalsIndex - 1;
        while (i >= iMax && isExpressionChar(text.charAt(i))) {
            i--;
        }
        do {
            i++;
            if (i >= equalsIndex) {
                break;
            }
        } while (isBlank(text.charAt(i)));
        if (i >= equalsIndex) {
            return null;
        }
        if (i > 0) {
            char cCharAt = text.charAt(i - 1);
            if (Character.isLetterOrDigit(cCharAt) || cCharAt == '_') {
                return null;
            }
        }
        return Integer.valueOf(i);
    }

    private final boolean isExpressionChar(char c2) {
        return Character.isDigit(c2) || Character.isLetter(c2) || c2 == '.' || c2 == ',' || isBlank(c2) || MathOperators.INSTANCE.getSymbolChars().contains(Character.valueOf(c2));
    }

    @Metadata(d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\r\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\f\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0006\u0010\u0014\u001a\u00020\u0015J\b\u0010\u0016\u001a\u00020\u0017H\u0002J\u0010\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\rH\u0002J\b\u0010\u001a\u001a\u00020\nH\u0002J\b\u0010\u001b\u001a\u00020\nH\u0002J\u0010\u0010\u001c\u001a\u00020\n2\u0006\u0010\u001d\u001a\u00020\rH\u0002J\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\rH\u0002J\b\u0010!\u001a\u00020\u001fH\u0002J\b\u0010\"\u001a\u00020\u001fH\u0002J\b\u0010#\u001a\u00020$H\u0002J\u0010\u0010%\u001a\u00020$2\u0006\u0010&\u001a\u00020'H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\n0\tj\b\u0012\u0004\u0012\u00020\n`\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0013¨\u0006("}, d2 = {"Lcom/exteragram/messenger/math/MathExpression$Parser;", _UrlKt.FRAGMENT_ENCODE_SET, "source", _UrlKt.FRAGMENT_ENCODE_SET, "options", "Lcom/exteragram/messenger/math/MathOptions;", "<init>", "(Ljava/lang/CharSequence;Lcom/exteragram/messenger/math/MathOptions;)V", "tokens", "Ljava/util/ArrayList;", "Lcom/exteragram/messenger/math/MathExpression$Token;", "Lkotlin/collections/ArrayList;", "position", _UrlKt.FRAGMENT_ENCODE_SET, "depth", "hasOperation", _UrlKt.FRAGMENT_ENCODE_SET, "separator", _UrlKt.FRAGMENT_ENCODE_SET, "Ljava/lang/Character;", "parse", "Lcom/exteragram/messenger/math/MathResult;", "tokenize", _UrlKt.FRAGMENT_ENCODE_SET, "readNumber", "from", "peek", "next", "expect", "type", "parseExpression", "Lcom/exteragram/messenger/math/MathExpression$Operand;", "minPrecedence", "parseUnary", "parsePostfix", "parsePrimary", _UrlKt.FRAGMENT_ENCODE_SET, "parseIdentifier", "name", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    @SourceDebugExtension({"SMAP\nMathExpression.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MathExpression.kt\ncom/exteragram/messenger/math/MathExpression$Parser\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,370:1\n1#2:371\n*E\n"})
    public static final class Parser {
        private int depth;
        private boolean hasOperation;
        private final MathOptions options;
        private int position;
        private Character separator;
        private final CharSequence source;
        private final ArrayList<Token> tokens = new ArrayList<>();

        public Parser(CharSequence charSequence, MathOptions mathOptions) {
            this.source = charSequence;
            this.options = mathOptions;
        }

        public final MathResult parse() {
            tokenize();
            Operand expression = parseExpression(0);
            expect(6);
            double value = expression.getValue();
            boolean z = this.hasOperation;
            Character ch = this.separator;
            return new MathResult(value, z, ch != null ? ch.charValue() : this.options.getDecimalSeparator());
        }

        private final void tokenize() {
            int i;
            int number = 0;
            while (number < this.source.length()) {
                char cCharAt = this.source.charAt(number);
                if (!MathExpression.INSTANCE.isBlank(cCharAt)) {
                    if (Character.isDigit(cCharAt) || ((cCharAt == '.' || cCharAt == ',') && (i = number + 1) < this.source.length() && Character.isDigit(this.source.charAt(i)))) {
                        number = readNumber(number);
                    } else if (cCharAt == '(') {
                        this.tokens.add(new Token(2, 0.0d, null, 6, null));
                    } else if (cCharAt == ')') {
                        this.tokens.add(new Token(3, 0.0d, null, 6, null));
                    } else if (cCharAt == ';') {
                        this.tokens.add(new Token(5, 0.0d, null, 6, null));
                    } else if (Character.isLetter(cCharAt)) {
                        int i2 = number;
                        while (i2 < this.source.length() && (Character.isLetter(this.source.charAt(i2)) || Character.isDigit(this.source.charAt(i2)))) {
                            i2++;
                        }
                        this.tokens.add(new Token(4, 0.0d, this.source.subSequence(number, i2).toString().toLowerCase(Locale.ROOT), 2, null));
                        number = i2;
                    } else {
                        String strMatchSymbol = MathOperators.INSTANCE.matchSymbol(this.source, number);
                        if (strMatchSymbol == null) {
                            throw MathExpression.error;
                        }
                        this.tokens.add(new Token(1, 0.0d, strMatchSymbol, 2, null));
                        number += strMatchSymbol.length();
                    }
                }
                number++;
            }
            this.tokens.add(new Token(6, 0.0d, null, 6, null));
        }

        private final int readNumber(int from) {
            int i;
            int i2;
            int i3 = from;
            while (i3 < this.source.length() && Character.isDigit(this.source.charAt(i3))) {
                i3++;
            }
            int i4 = i3 - from;
            char cCharAt = i3 < this.source.length() ? this.source.charAt(i3) : ' ';
            if ((cCharAt == '.' || cCharAt == ',') && (i = i3 + 1) < this.source.length() && Character.isDigit(this.source.charAt(i))) {
                i3 = i;
                while (i3 < this.source.length() && Character.isDigit(this.source.charAt(i3))) {
                    i3++;
                }
                i2 = i3 - i;
                if (cCharAt == ',' && i2 == 3 && 1 <= i4 && i4 < 4 && this.source.charAt(from) != '0') {
                    throw MathExpression.error;
                }
                if (this.separator == null) {
                    this.separator = Character.valueOf(cCharAt);
                }
            } else {
                i2 = 0;
            }
            if (i4 == 0 && i2 == 0) {
                throw MathExpression.error;
            }
            String strReplace$default = this.source.subSequence(from, i3).toString().replace(',', '.');
            ArrayList<Token> arrayList = this.tokens;
            Double doubleOrNull = StringsKt.toDoubleOrNull(strReplace$default);
            if (doubleOrNull == null) {
                throw MathExpression.error;
            }
            arrayList.add(new Token(0, doubleOrNull.doubleValue(), null, 4, null));
            return i3;
        }

        private final Token peek() {
            return this.tokens.get(this.position);
        }

        private final Token next() {
            ArrayList<Token> arrayList = this.tokens;
            int i = this.position;
            this.position = i + 1;
            return arrayList.get(i);
        }

        private final Token expect(int type) {
            Token next = next();
            if (next.getType() == type) {
                return next;
            }
            throw MathExpression.error;
        }

        private final Operand parseExpression(int minPrecedence) {
            InfixOperator infixOperatorInfixFor;
            int i = this.depth + 1;
            this.depth = i;
            if (i > 32) {
                throw MathExpression.error;
            }
            try {
                Operand unary = parseUnary();
                while (true) {
                    Token tokenPeek = peek();
                    if (tokenPeek.getType() != 1 || (infixOperatorInfixFor = MathOperators.INSTANCE.infixFor(tokenPeek.getText())) == null || infixOperatorInfixFor.getPrecedence() < minPrecedence) {
                        break;
                    }
                    next();
                    Operand expression = parseExpression(infixOperatorInfixFor.getRightAssociative() ? infixOperatorInfixFor.getPrecedence() : infixOperatorInfixFor.getPrecedence() + 1);
                    this.hasOperation = true;
                    unary = new Operand(infixOperatorInfixFor.getApply().invoke(Double.valueOf(unary.getValue()), Double.valueOf(expression.getValue()), Boolean.valueOf(expression.getIsPercent())).doubleValue(), false);
                }
                return unary;
            } finally {
                this.depth--;
            }
        }

        private final Operand parseUnary() {
            PrefixOperator prefixOperatorPrefixFor;
            Token tokenPeek = peek();
            if (tokenPeek.getType() == 1 && (prefixOperatorPrefixFor = MathOperators.INSTANCE.prefixFor(tokenPeek.getText())) != null) {
                next();
                Operand expression = parseExpression(prefixOperatorPrefixFor.getPrecedence());
                if (prefixOperatorPrefixFor.getOperation()) {
                    this.hasOperation = true;
                }
                return new Operand(prefixOperatorPrefixFor.getApply().invoke(Double.valueOf(expression.getValue())).doubleValue(), expression.getIsPercent());
            }
            return parsePostfix();
        }

        private final Operand parsePostfix() {
            PostfixOperator postfixOperatorPostfixFor;
            double primary = parsePrimary();
            boolean percent = false;
            while (true) {
                Token tokenPeek = peek();
                if (tokenPeek.getType() != 1 || (postfixOperatorPostfixFor = MathOperators.INSTANCE.postfixFor(tokenPeek.getText())) == null) {
                    break;
                }
                next();
                primary = postfixOperatorPostfixFor.getApply().invoke(Double.valueOf(primary)).doubleValue();
                this.hasOperation = true;
                percent = postfixOperatorPostfixFor.getPercent();
            }
            return new Operand(primary, percent);
        }

        private final double parsePrimary() {
            Token next = next();
            int type = next.getType();
            if (type == 0) {
                return next.getNumber();
            }
            if (type != 2) {
                if (type != 4) {
                    throw MathExpression.error;
                }
                return parseIdentifier(next.getText());
            }
            Operand expression = parseExpression(0);
            expect(3);
            return expression.getValue();
        }

        private final double parseIdentifier(String name) {
            MathOperators mathOperators = MathOperators.INSTANCE;
            Double d = mathOperators.getConstants().get(name);
            if (d != null) {
                return d.doubleValue();
            }
            MathFunction mathFunctionFunctionFor = mathOperators.functionFor(name);
            if (mathFunctionFunctionFor == null) {
                throw MathExpression.error;
            }
            expect(2);
            ArrayList arrayList = new ArrayList(2);
            if (peek().getType() != 3) {
                arrayList.add(Double.valueOf(parseExpression(0).getValue()));
                while (peek().getType() == 5) {
                    next();
                    arrayList.add(Double.valueOf(parseExpression(0).getValue()));
                }
            }
            expect(3);
            IntRange arity = mathFunctionFunctionFor.getArity();
            int first = arity.getFirst();
            int last = arity.getLast();
            int size = arrayList.size();
            if (first > size || size > last) {
                throw MathExpression.error;
            }
            this.hasOperation = true;
            return mathFunctionFunctionFor.getApply().invoke(CollectionsKt.toDoubleArray(arrayList)).doubleValue();
        }
    }
}
