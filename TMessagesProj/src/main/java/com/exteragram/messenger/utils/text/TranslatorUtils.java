package com.exteragram.messenger.utils.text;

import android.text.TextUtils;
import android.text.style.URLSpan;
import com.chaquo.python.internal.Common;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.translators.BaseTranslator;
import com.exteragram.messenger.translators.DeepLTranslator;
import com.exteragram.messenger.translators.GoogleTranslator;
import com.exteragram.messenger.translators.TelegramTranslator;
import com.exteragram.messenger.translators.YandexTranslator;
import com.exteragram.messenger.utils.chats.ChatUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import okhttp3.internal.url._UrlKt;
import org.mvel2.asm.signature.SignatureVisitor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.TranslateAlert2;
import org.telegram.ui.RestrictedLanguagesSelectActivity;

public abstract class TranslatorUtils {
    private static final String[] DEVICE_MODELS = {"Galaxy S6", "Galaxy S7", "Galaxy S8", "Galaxy S9", "Galaxy S10", "Galaxy S21", "Pixel 3", "Pixel 4", "Pixel 5", "OnePlus 6", "OnePlus 7", "OnePlus 8", "OnePlus 9", "Xperia XZ", "Xperia XZ2", "Xperia XZ3", "Xperia 1", "Xperia 5", "Xperia 10", "Xperia L4"};
    private static final String[] CHROME_VERSIONS = {"111.0.5563.57", "94.0.4606.81", "80.0.3987.119", "69.0.3497.100", "92.0.4515.159", "71.0.3578.99"};

    public interface TranslateCallback {
        void onFailed();

        default void onReqId(int i) {
        }

        default void onSuccess(String str) {
        }

        default void onSuccess(TLObject tLObject, TLRPC.TL_error tL_error) {
        }

        default void onSuccess(TLRPC.TL_textWithEntities tL_textWithEntities) {
        }
    }

    public static boolean m1511$r8$lambda$u3qoQyKPFjQQxfvY3tDPXiLrqg(TranslateController.Language language) {
        return language == null || TextUtils.isEmpty(language.code) || language.code.contains("-") || language.code.contains("_");
    }

    public static String $r8$lambda$owKBg0zn5kerZzZQUwDsrIy5hLA(String str) {
        String strNormalizeLanguageCode = normalizeLanguageCode(str);
        return TextUtils.isEmpty(strNormalizeLanguageCode) ? _UrlKt.FRAGMENT_ENCODE_SET : strNormalizeLanguageCode.toUpperCase(Locale.US);
    }

    public static String getLanguageDisplayName(final String str) {
        return (String) getLanguages().stream().filter(new Predicate() { 
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return TextUtils.equals(((TranslateController.Language) obj).code, str);
            }
        }).findFirst().map(new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((TranslateController.Language) obj).ownDisplayName;
            }
        }).orElse(null);
    }

    public static void translateWithAlert(final MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, final TLRPC.InputPeer inputPeer, final int i, final BaseFragment baseFragment) {
        if (messageObject == null) {
            return;
        }
        final ChatActivity chatActivity = (ChatActivity) baseFragment;
        final Utilities.CallbackReturn callbackReturn = new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return TranslatorUtils.$r8$lambda$W9jvQEKAdqWx7JkQeecPFJnjQRs(chatActivity, messageObject, (URLSpan) obj);
            }
        };
        TLRPC.Message message = messageObject.messageOwner;
        final ArrayList<TLRPC.MessageEntity> arrayList = message != null ? message.entities : null;
        final CharSequence messageText = ChatUtils.getInstance().getMessageText(messageObject, groupedMessages);
        LanguageDetector.detectLanguage(messageText == null ? _UrlKt.FRAGMENT_ENCODE_SET : messageText.toString(), new LanguageDetector.StringCallback() { 
            @Override 
            public final void run(String str) {
                TranslatorUtils.m1515$r8$lambda$JCTNCOaR964lC1lDviVn4VGf54(baseFragment, inputPeer, i, messageText, arrayList, callbackReturn, chatActivity, str);
            }
        }, new LanguageDetector.ExceptionCallback() { 
            @Override 
            public final void run(Exception exc) {
                TranslatorUtils.$r8$lambda$GOSMYs_gu6yU1qrYwaGIZF7j4O4(exc);
            }
        });
    }

    public static void m1513$r8$lambda$AGR88fHZOMFtQZ7va0frxk9zX4(CharSequence charSequence, String str, final TranslateCallback translateCallback, TLRPC.TL_textWithEntities tL_textWithEntities, TLObject tLObject, final TLRPC.TL_error tL_error) {
        String str2;
        if (tL_error != null && "TRANSLATIONS_DISABLED_ALT".equalsIgnoreCase(tL_error.text)) {
            GoogleTranslator.getInstance().translate(charSequence.toString(), "auto", str, new TranslateCallback() { 
                @Override 
                public void onSuccess(String str3) {
                    boolean zIsEmpty = TextUtils.isEmpty(str3);
                    TranslateCallback translateCallback2 = translateCallback;
                    if (zIsEmpty) {
                        Objects.requireNonNull(translateCallback2);
                        AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback2));
                    } else {
                        translateCallback2.onSuccess(str3);
                        TLRPC.TL_textWithEntities tL_textWithEntities2 = new TLRPC.TL_textWithEntities();
                        tL_textWithEntities2.text = str3;
                        translateCallback.onSuccess(tL_textWithEntities2);
                    }
                }

                @Override 
                public void onSuccess(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    translateCallback.onSuccess(tLObject2, tL_error2);
                }

                @Override 
                public void onFailed() {
                    translateCallback.onFailed();
                }

                @Override 
                public void onReqId(int i) {
                    translateCallback.onReqId(i);
                }
            });
            return;
        }
        if (tLObject instanceof TLRPC.TL_messages_translateResult) {
            final TLRPC.TL_messages_translateResult tL_messages_translateResult = (TLRPC.TL_messages_translateResult) tLObject;
            if (!tL_messages_translateResult.result.isEmpty() && tL_messages_translateResult.result.get(0) != null && tL_messages_translateResult.result.get(0).text != null) {
                final TLRPC.TL_textWithEntities tL_textWithEntities2 = tL_messages_translateResult.result.get(0);
                final TLRPC.TL_textWithEntities tL_textWithEntitiesPreprocess = TranslateAlert2.preprocess(tL_textWithEntities, tL_textWithEntities2);
                if (tL_textWithEntitiesPreprocess == null || (str2 = tL_textWithEntitiesPreprocess.text) == null) {
                    str2 = tL_textWithEntities2.text;
                }
                final String str3 = str2;
                if (TextUtils.isEmpty(str3)) {
                    Objects.requireNonNull(translateCallback);
                    AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback));
                    return;
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() { 
                        @Override // java.lang.Runnable
                        public final void run() {
                            TranslatorUtils.$r8$lambda$Psw33I0M_hl1fYpsbSR6b9L8Wg8(translateCallback, tL_messages_translateResult, tL_error, tL_textWithEntitiesPreprocess, tL_textWithEntities2, str3);
                        }
                    });
                    return;
                }
            }
        }
        Objects.requireNonNull(translateCallback);
        AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback));
    }

    public static void $r8$lambda$mx_VaLapka_00FV2FdCLbbkzUIg(CharSequence charSequence, String str, ArrayList arrayList, TranslateCallback translateCallback, String str2) {
        if (str2 == null || str2.equals(TranslateController.UNKNOWN_LANGUAGE)) {
            str2 = "auto";
        }
        translate(charSequence, str2, str, arrayList, translateCallback);
    }

    public static void translate(CharSequence charSequence, String str, String str2, ArrayList<TLRPC.MessageEntity> arrayList, final TranslateCallback translateCallback) {
        BaseTranslator currentTranslator = getCurrentTranslator();
        if (currentTranslator == TelegramTranslator.getInstance()) {
            translateWithDefault(charSequence, null, 0, str2, arrayList, translateCallback);
            return;
        }
        if (!currentTranslator.isLanguageSupported(str2)) {
            currentTranslator = GoogleTranslator.getInstance();
        }
        currentTranslator.translate(charSequence.toString(), str, str2, new TranslateCallback() { 
            @Override 
            public void onSuccess(String str3) {
                boolean zIsEmpty = TextUtils.isEmpty(str3);
                TranslateCallback translateCallback2 = translateCallback;
                if (zIsEmpty) {
                    translateCallback2.onFailed();
                    return;
                }
                translateCallback2.onSuccess(str3);
                TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
                tL_textWithEntities.text = str3;
                translateCallback.onSuccess(tL_textWithEntities);
            }

            @Override 
            public void onSuccess(TLObject tLObject, TLRPC.TL_error tL_error) {
                translateCallback.onSuccess(tLObject, tL_error);
            }

            @Override 
            public void onFailed() {
                translateCallback.onFailed();
            }

            @Override 
            public void onReqId(int i) {
                translateCallback.onReqId(i);
            }
        });
    }

    public static BaseTranslator getCurrentTranslator() {
        int translationProvider = ExteraConfig.getTranslationProvider();
        if (translationProvider == 0) {
            return TelegramTranslator.getInstance();
        }
        if (translationProvider == 2) {
            return YandexTranslator.getInstance();
        }
        if (translationProvider == 3) {
            return DeepLTranslator.getInstance();
        }
        return GoogleTranslator.getInstance();
    }

    public static String getCurrentTranslatorName() {
        return getCurrentTranslator().getDisplayName();
    }
}
