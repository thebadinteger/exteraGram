package com.exteragram.messenger.translators;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.TranslationFormality;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.url._UrlKt;
import okio.GzipSource;
import okio.Okio;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class DeepLTranslator extends BaseTranslator {
    private static DeepLTranslator instance;
    private static final OkHttpClient client = ExteraHttpClient.INSTANCE.getClient().newBuilder().addNetworkInterceptor(new Interceptor() { 
        @Override // okhttp3.Interceptor
        public final Response intercept(Interceptor.Chain chain) {
            return DeepLTranslator.$r8$lambda$C3t8HA5LdSNw5SCJiN1vp78rw6U(chain);
        }
    }).build();
    private static final Set<String> SUPPORTED_LANGUAGES = new HashSet(new java.util.HashSet<>(java.util.Arrays.asList("bg", "cs", "da", "de", "el", "en", "en-GB", "en-US", "es", "fi", "fr", "hu", "id", "it", "ja", "lt", "lv", "nl", "pl", "pt", "pt-BR", "pt-PT", "ro", "ru", "sk", "sl", "sv", "tr", "uk", "zh")));

    public static class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$exteragram$messenger$TranslationFormality;

        static {
            int[] iArr = new int[TranslationFormality.values().length];
            $SwitchMap$com$exteragram$messenger$TranslationFormality = iArr;
            try {
                iArr[TranslationFormality.INFORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$TranslationFormality[TranslationFormality.FORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private static String getFormality() {
        int i = AnonymousClass2.$SwitchMap$com$exteragram$messenger$TranslationFormality[ExteraConfig.getTranslationFormality().ordinal()];
        if (i == 1) {
            return "informal";
        }
        if (i != 2) {
            return null;
        }
        return "formal";
    }
}
