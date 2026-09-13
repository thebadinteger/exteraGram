package com.exteragram.messenger.pillstack.ui.pills.crypto.utils;

import com.exteragram.messenger.utils.RecordTag;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

public abstract class PillStackCurrencies {
    private static final HashSet<String> AMBIGUOUS_SYMBOLS = new HashSet<>(Arrays.asList("$", "kr", "Fr", "₩"));
    private static final Map<String, CurrencyInfo> CURRENCIES = new HashMap();
    public static final String[] TARGET_CURRENCIES;

    public static final class CurrencyInfo extends RecordTag {
        private final String code;
        private final int nameResId;
        private final boolean suffixSymbol;
        private final String symbolOverride;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof CurrencyInfo)) {
                return false;
            }
            CurrencyInfo currencyInfo = (CurrencyInfo) obj;
            return this.suffixSymbol == currencyInfo.suffixSymbol && this.nameResId == currencyInfo.nameResId && Objects.equals(this.code, currencyInfo.code) && Objects.equals(this.symbolOverride, currencyInfo.symbolOverride);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.code, Integer.valueOf(this.nameResId), this.symbolOverride, Boolean.valueOf(this.suffixSymbol)};
        }

        private CurrencyInfo(String str, int i, String str2, boolean z) {
            this.code = str;
            this.nameResId = i;
            this.symbolOverride = str2;
            this.suffixSymbol = z;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.suffixSymbol, this.nameResId, this.code, this.symbolOverride);
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), CurrencyInfo.class, "code;nameResId;symbolOverride;suffixSymbol");
        }
    }

    static {
        addCurrency("USD", R.string.CryptoCurrencyUsd, "$", false);
        addCurrency("EUR", R.string.CryptoCurrencyEur, null, false);
        addCurrency("RUB", R.string.CryptoCurrencyRub, "₽", true);
        addCurrency("GBP", R.string.CryptoCurrencyGbp, null, false);
        addCurrency("KZT", R.string.CryptoCurrencyKzt, "₸", true);
        addCurrency("TRY", R.string.CryptoCurrencyTry, "₺", true);
        addCurrency("UAH", R.string.CryptoCurrencyUah, "₴", true);
        addCurrency("PLN", R.string.CryptoCurrencyPln, "zł", true);
        addCurrency("AED", R.string.CryptoCurrencyAed, null, false);
        addCurrency("CNY", R.string.CryptoCurrencyCny, "CN¥", false);
        addCurrency("JPY", R.string.CryptoCurrencyJpy, null, false);
        addCurrency("BYN", R.string.CryptoCurrencyByn, "Br", true);
        addCurrency("ILS", R.string.CryptoCurrencyIls, "₪", false);
        addCurrency("CZK", R.string.CryptoCurrencyCzk, "Kč", true);
        addCurrency("INR", R.string.CryptoCurrencyInr, "₹", false);
        String[] strArr = new String[16];
        TARGET_CURRENCIES = strArr;
        strArr[0] = "AUTO";
        System.arraycopy(new String[]{"AED", "BYN", "CNY", "CZK", "EUR", "GBP", "ILS", "INR", "JPY", "KZT", "PLN", "RUB", "TRY", "UAH", "USD"}, 0, strArr, 1, 15);
    }

    private static void addCurrency(String str, int i, String str2, boolean z) {
        String strNormalize = normalize(str);
        if (strNormalize.isEmpty()) {
            return;
        }
        CURRENCIES.put(strNormalize, new CurrencyInfo(strNormalize, i, str2, z));
    }

    public static CharSequence getTargetCurrencyLabel(String str) {
        if (str == null || "AUTO".equalsIgnoreCase(str)) {
            return LocaleController.getString(R.string.QualityAuto);
        }
        return getCurrencyLabelWithCode(str);
    }

    public static CharSequence getTargetCurrencySubtext(String str) {
        if (str == null || "AUTO".equalsIgnoreCase(str)) {
            return LocaleController.getString(R.string.QualityAuto);
        }
        return getCurrencyName(str);
    }

    public static String getCurrencyName(String str) {
        String strNormalize = normalize(str);
        CurrencyInfo currencyInfo = CURRENCIES.get(strNormalize);
        return currencyInfo == null ? strNormalize : LocaleController.getString(currencyInfo.nameResId);
    }

    public static String getCurrencyLabelWithCode(String str) {
        String strNormalize = normalize(str);
        CurrencyInfo currencyInfo = CURRENCIES.get(strNormalize);
        if (currencyInfo == null) {
            return strNormalize;
        }
        return LocaleController.getString(currencyInfo.nameResId) + " — " + strNormalize;
    }

    public static String[] getTargetCurrencies(String str) {
        if (str == null || str.isEmpty()) {
            return TARGET_CURRENCIES;
        }
        int i = 0;
        for (String str2 : TARGET_CURRENCIES) {
            if (!str.equalsIgnoreCase(str2)) {
                i++;
            }
        }
        String[] strArr = new String[i];
        int i2 = 0;
        for (String str3 : TARGET_CURRENCIES) {
            if (!str.equalsIgnoreCase(str3)) {
                strArr[i2] = str3;
                i2++;
            }
        }
        return strArr;
    }

    public static String formatFiatPrice(BigDecimal bigDecimal, String str) {
        if (bigDecimal != null && str != null && !str.isEmpty()) {
            try {
                int iMax = Math.max(0, BillingController.getInstance().getCurrencyExp(str));
                BigDecimal scale = bigDecimal.setScale(iMax, RoundingMode.HALF_UP);
                Locale locale = Locale.US;
                NumberFormat numberInstance = NumberFormat.getNumberInstance(locale);
                numberInstance.setGroupingUsed(true);
                numberInstance.setMinimumFractionDigits(iMax);
                numberInstance.setMaximumFractionDigits(iMax);
                String str2 = numberInstance.format(scale);
                String strNormalize = normalize(str);
                CurrencyInfo currencyInfo = CURRENCIES.get(strNormalize);
                String symbol = currencyInfo != null ? currencyInfo.symbolOverride : null;
                boolean z = symbol != null;
                if (!z) {
                    try {
                        symbol = Currency.getInstance(strNormalize).getSymbol(locale);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                if (symbol != null && !symbol.isEmpty() && !symbol.equalsIgnoreCase(strNormalize)) {
                    if (!z && AMBIGUOUS_SYMBOLS.contains(symbol)) {
                        return str2 + " " + str;
                    }
                    if (currencyInfo != null && currencyInfo.suffixSymbol) {
                        return str2 + " " + symbol;
                    }
                    return symbol + str2;
                }
                return str2 + " " + str;
            } catch (Exception unused) {
            }
        }
        return null;
    }

    private static String normalize(String str) {
        if (str == null) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        return str.trim().toUpperCase(Locale.ROOT);
    }
}
