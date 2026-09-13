package com.exteragram.messenger.pillstack.ui.pills.crypto.utils;

import android.text.TextUtils;
import com.exteragram.messenger.utils.RecordTag;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.pillstack.core.PillType;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;

public abstract class ExchangeRates {
    private static long cacheTimestamp;
    private static State cacheValue;
    private static boolean requestInFlight;
    public static final String[] MAIN_CURRENCIES = {"USD", "EUR", "RUB", "GBP", "KZT", "TRY", "UAH", "PLN", "AED", "CNY", "JPY", "BYN", "ILS", "CZK", "INR", "TON", "BTC", "ETH", "SOL"};
    public static final String[] CRYPTO_CURRENCIES = {"BTC", "ETH", "SOL", "TON", "USD", "EUR"};
    private static final Object sync = new Object();
    private static final ArrayList<Utilities.Callback<State>> pendingCallbacks = new ArrayList<>();

    static {
        NotificationCenter.getGlobalInstance().addObserver(new NotificationCenter.NotificationCenterDelegate() { // from class: com.exteragram.messenger.pillstack.ui.pills.crypto.utils.ExchangeRates$$ExternalSyntheticLambda0
            @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
            public final void didReceivedNotification(int i, int i2, Object[] objArr) {
                ExchangeRates.$r8$lambda$cWqt41mowJXyOQJBhkZjVVpZpSM(i, i2, objArr);
            }
        }, NotificationCenter.pillStackSettingsChanged);
    }

    public static class CoinbaseResponse {

        @SerializedName("data")
        Data data;

        private CoinbaseResponse() {
        }
    }

    public static class Data {

        @SerializedName("currency")
        String currency;

        @SerializedName("rates")
        Map<String, String> rates;

        private Data() {
        }
    }

    public static final class State extends RecordTag {
        public static final DecimalFormat formatter;
        private final Map<String, BigDecimal> usdRates;

        private /* synthetic */ boolean $record$equals(Object obj) {
            return (obj instanceof State) && Objects.equals(this.usdRates, ((State) obj).usdRates);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.usdRates};
        }

        public State(Map<String, BigDecimal> map) {
            this.usdRates = map;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return Objects.hashCode(this.usdRates);
        }

        public final String toString() {
            return "State[usdRates=" + this.usdRates + "]";
        }

        public Map<String, BigDecimal> usdRates() {
            return this.usdRates;
        }

        static {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            formatter = decimalFormat;
            decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        }

        public BigDecimal getUsdRate(String str) {
            if (str == null) {
                return null;
            }
            return this.usdRates.get(ExchangeRates.normalize(str));
        }

        public BigDecimal getRate(String str, String str2) {
            BigDecimal usdRate = getUsdRate(str);
            BigDecimal usdRate2 = getUsdRate(str2);
            if (usdRate == null || usdRate2 == null || usdRate2.signum() == 0) {
                return null;
            }
            return usdRate.divide(usdRate2, 12, RoundingMode.HALF_UP);
        }

        public double formatDonate(String str, double d) {
            BigDecimal rate = getRate("USD", str);
            if (rate != null) {
                d = rate.doubleValue();
            }
            double dFloatValue = d * ((double) RemoteUtils.getFloatConfigValue("donates_amount_usd", 5.0f).floatValue());
            if ("ton".equalsIgnoreCase(str)) {
                dFloatValue += (((double) RemoteUtils.getIntConfigValue("donates_ton_markup_percent", 10).intValue()) / 100.0d) * dFloatValue;
            }
            return Double.parseDouble(formatter.format(dFloatValue));
        }
    }

    public static /* synthetic */ void $r8$lambda$cWqt41mowJXyOQJBhkZjVVpZpSM(int i, int i2, Object[] objArr) {
        if (i == NotificationCenter.pillStackSettingsChanged && PillStackConfig.shouldUpdatePill(objArr, PillType.GRAM.getId(), PillType.BTC.getId(), PillType.USD.getId())) {
            clearCache();
        }
    }

    public static State getCached() {
        if (cacheValue == null) {
            try {
                String string = PillStackConfig.getPreferences().getString("exchangeRatesCache", null);
                if (string != null) {
                    cacheValue = (State) ExteraConfig.getGSON().fromJson(string, State.class);
                    cacheTimestamp = PillStackConfig.getPreferences().getLong("exchangeRatesTimestamp", 0L);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return cacheValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void saveCache(State state) {
        if (state == null) {
            return;
        }
        try {
            PillStackConfig.getEditor().putString("exchangeRatesCache", ExteraConfig.getGSON().toJson(state)).apply();
            PillStackConfig.getEditor().putLong("exchangeRatesTimestamp", System.currentTimeMillis()).apply();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void clearCache() {
        cacheTimestamp = 0L;
    }

    public static boolean isSupportedCurrency(String str) {
        if (str == null) {
            return false;
        }
        String strNormalize = normalize(str);
        for (String str2 : MAIN_CURRENCIES) {
            if (str2.equals(strNormalize)) {
                return true;
            }
        }
        return false;
    }

    public static String resolveTargetCurrency(int i, String str) {
        String strNormalize = normalize(str);
        if (!"AUTO".equals(strNormalize)) {
            return (TextUtils.isEmpty(strNormalize) || !isSupportedCurrency(strNormalize)) ? "USD" : strNormalize;
        }
        String targetCurrency = BillingController.getInstance().getTargetCurrency(i, false);
        if (targetCurrency == null) {
            return "USD";
        }
        String strNormalize2 = normalize(targetCurrency);
        return isSupportedCurrency(strNormalize2) ? strNormalize2 : "USD";
    }

    public static void fetch(final Utilities.Callback<State> callback) {
        boolean z;
        if (callback == null) {
            return;
        }
        final State state = cacheValue;
        if (state != null && !isStale()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.pillstack.ui.pills.crypto.utils.ExchangeRates$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    callback.run(state);
                }
            });
            return;
        }
        synchronized (sync) {
            try {
                pendingCallbacks.add(callback);
                if (requestInFlight) {
                    z = false;
                } else {
                    z = true;
                    requestInFlight = true;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        if (z) {
            ExteraHttpClient.INSTANCE.getClient().newCall(new Request.Builder().url("https://api.coinbase.com/v2/exchange-rates?currency=USD").build()).enqueue(new Callback() { // from class: com.exteragram.messenger.pillstack.ui.pills.crypto.utils.ExchangeRates.1
                @Override // okhttp3.Callback
                public void onFailure(Call call, IOException iOException) {
                    FileLog.e(iOException);
                    ExchangeRates.complete(ExchangeRates.getCached());
                }

                @Override // okhttp3.Callback
                public void onResponse(Call call, Response response) {
                    if (!response.isSuccessful()) {
                        response.close();
                        onFailure(call, new IOException("Unexpected code " + response));
                        return;
                    }
                    try {
                        ResponseBody responseBodyBody = response.body();
                        try {
                            State state2 = ExchangeRates.parseState((CoinbaseResponse) ExteraConfig.getGSON().fromJson(responseBodyBody.charStream(), CoinbaseResponse.class));
                            if (state2 != null) {
                                ExchangeRates.cacheValue = state2;
                                ExchangeRates.cacheTimestamp = System.currentTimeMillis();
                                ExchangeRates.saveCache(state2);
                            }
                            if (state2 == null) {
                                state2 = ExchangeRates.getCached();
                            }
                            ExchangeRates.complete(state2);
                            responseBodyBody.close();
                        } catch (Throwable th2) {
                            if (responseBodyBody != null) {
                                try {
                                    responseBodyBody.close();
                                } catch (Throwable th3) {
                                    th2.addSuppressed(th3);
                                }
                            }
                            throw th2;
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                        ExchangeRates.complete(ExchangeRates.getCached());
                    }
                }
            });
        }
    }

    private static boolean isStale() {
        return getCached() == null || cacheTimestamp == 0 || System.currentTimeMillis() - cacheTimestamp >= 300000;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void complete(final State state) {
        final ArrayList arrayList;
        synchronized (sync) {
            requestInFlight = false;
            ArrayList<Utilities.Callback<State>> arrayList2 = pendingCallbacks;
            arrayList = new ArrayList(arrayList2);
            arrayList2.clear();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.pillstack.ui.pills.crypto.utils.ExchangeRates$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ExchangeRates.m1292$r8$lambda$N2ze8JRy2FQCLLaC_XD27HNkyw(arrayList, state);
            }
        });
    }

    /* JADX INFO: renamed from: $r8$lambda$N2-ze8JRy2FQCLLaC_XD27HNkyw, reason: not valid java name */
    public static /* synthetic */ void m1292$r8$lambda$N2ze8JRy2FQCLLaC_XD27HNkyw(ArrayList arrayList, State state) {
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((Utilities.Callback) obj).run(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static State parseState(CoinbaseResponse coinbaseResponse) {
        Data data;
        if (coinbaseResponse == null || (data = coinbaseResponse.data) == null || data.rates == null) {
            return null;
        }
        HashMap map = new HashMap();
        for (String str : MAIN_CURRENCIES) {
            BigDecimal usdRate = parseUsdRate(str, coinbaseResponse.data.rates);
            if (usdRate != null) {
                map.put(str, usdRate);
            }
        }
        if (map.isEmpty()) {
            return null;
        }
        return new State(map);
    }

    private static BigDecimal parseUsdRate(String str, Map<String, String> map) {
        if ("USD".equals(str)) {
            return BigDecimal.ONE;
        }
        String str2 = map.get(str);
        if (str2 == null) {
            return null;
        }
        try {
            BigDecimal bigDecimal = new BigDecimal(str2);
            if (bigDecimal.signum() == 0) {
                return null;
            }
            return BigDecimal.ONE.divide(bigDecimal, 16, RoundingMode.HALF_UP);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String normalize(String str) {
        if (str == null) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        return str.trim().toUpperCase(Locale.ROOT);
    }
}
