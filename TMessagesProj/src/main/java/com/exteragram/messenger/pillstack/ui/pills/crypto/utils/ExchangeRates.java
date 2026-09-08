package com.exteragram.messenger.pillstack.ui.pills.crypto.utils;

import android.text.TextUtils;
import com.android.tools.r8.RecordTag;
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
        NotificationCenter.getGlobalInstance().addObserver(new NotificationCenter.NotificationCenterDelegate() { 
            @Override 
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

        private void m1292$r8$lambda$N2ze8JRy2FQCLLaC_XD27HNkyw(ArrayList arrayList, State state) {
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((Utilities.Callback) obj).run(state);
        }
    }

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

    public static String normalize(String str) {
        if (str == null) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        return str.trim().toUpperCase(Locale.ROOT);
    }
}
