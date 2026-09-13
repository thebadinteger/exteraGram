package com.exteragram.messenger.utils.text;

import android.text.TextUtils;
import android.text.style.URLSpan;
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
    private static final String ASSET_APP = "app";
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

    public static /* synthetic */ void $r8$lambda$GOSMYs_gu6yU1qrYwaGIZF7j4O4(Exception exc) {
    }

    private static List<TranslateController.Language> getLanguages() {
        return new ArrayList(TranslateController.getLanguages());
    }

    private static List<TranslateController.Language> getIndexedTargetLanguages() {
        return getCurrentTargetLanguages();
    }

    public static String normalizeLanguageCode(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String strReplace = str.trim().toLowerCase(Locale.US).replace('_', '-');
        return "nb".equals(strReplace) ? "no" : strReplace;
    }

    public static String primaryLanguageOf(String str) {
        String strNormalizeLanguageCode = normalizeLanguageCode(str);
        if (TextUtils.isEmpty(strNormalizeLanguageCode)) {
            return null;
        }
        int iIndexOf = strNormalizeLanguageCode.indexOf(45);
        return iIndexOf >= 0 ? strNormalizeLanguageCode.substring(0, iIndexOf) : strNormalizeLanguageCode;
    }

    public static boolean isTargetLanguageFollowApp() {
        return ASSET_APP.equalsIgnoreCase(ExteraConfig.getTargetLang());
    }

    public static boolean isRestrictedLanguage(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (TextUtils.equals(primaryLanguageOf(str), primaryLanguageOf(getResolvedTargetLanguageCode()))) {
            return true;
        }
        String strPrimaryLanguageOf = primaryLanguageOf(str);
        if (TextUtils.isEmpty(strPrimaryLanguageOf)) {
            return false;
        }
        Iterator<String> it = RestrictedLanguagesSelectActivity.getRestrictedLanguages().iterator();
        while (it.hasNext()) {
            if (TextUtils.equals(strPrimaryLanguageOf, primaryLanguageOf(it.next()))) {
                return true;
            }
        }
        return false;
    }

    private static TranslateController.Language createLanguageItem(String str) {
        String strNormalizeLanguageCode = normalizeLanguageCode(str);
        TranslateController.Language language = new TranslateController.Language();
        language.code = strNormalizeLanguageCode;
        Locale currentLocale = LocaleController.getInstance().getCurrentLocale();
        if (currentLocale == null) {
            currentLocale = Locale.getDefault();
        }
        String upperCase = _UrlKt.FRAGMENT_ENCODE_SET;
        Locale localeForLanguageTag = Locale.forLanguageTag(strNormalizeLanguageCode == null ? _UrlKt.FRAGMENT_ENCODE_SET : strNormalizeLanguageCode);
        String displayName = localeForLanguageTag.getDisplayName(currentLocale);
        String displayName2 = localeForLanguageTag.getDisplayName(localeForLanguageTag);
        if (TextUtils.isEmpty(displayName)) {
            displayName = TranslateAlert2.capitalFirst(TranslateAlert2.languageName(strNormalizeLanguageCode));
        }
        if (TextUtils.isEmpty(displayName2)) {
            displayName2 = TranslateAlert2.capitalFirst(TranslateAlert2.systemLanguageName(strNormalizeLanguageCode, true));
        }
        if (TextUtils.isEmpty(displayName)) {
            if (strNormalizeLanguageCode != null) {
                upperCase = strNormalizeLanguageCode.toUpperCase(Locale.US);
            }
            displayName = upperCase;
        }
        if (TextUtils.isEmpty(displayName2)) {
            displayName2 = displayName;
        }
        language.displayName = TranslateAlert2.capitalFirst(displayName);
        language.ownDisplayName = TranslateAlert2.capitalFirst(displayName2);
        language.q = (language.displayName + " " + language.ownDisplayName).toLowerCase(Locale.US);
        return language;
    }

    public static ArrayList<TranslateController.Language> getCurrentTargetLanguages() {
        BaseTranslator currentTranslator = getCurrentTranslator();
        Set<String> supportedLanguages = currentTranslator.getSupportedLanguages();
        if (supportedLanguages == null || supportedLanguages.isEmpty()) {
            ArrayList<TranslateController.Language> arrayList = new ArrayList<>(TranslateController.getLanguages());
            if (currentTranslator == TelegramTranslator.getInstance()) {
                arrayList.removeIf(new Predicate() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda7
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return TranslatorUtils.m1511$r8$lambda$u3qoQyKPFjQQxfvY3tDPXiLrqg((TranslateController.Language) obj);
                    }
                });
            }
            return arrayList;
        }
        HashSet hashSet = new HashSet();
        Iterator<String> it = supportedLanguages.iterator();
        while (it.hasNext()) {
            String strNormalizeLanguageCode = normalizeLanguageCode(it.next());
            if (!TextUtils.isEmpty(strNormalizeLanguageCode)) {
                hashSet.add(strNormalizeLanguageCode);
            }
        }
        ArrayList<TranslateController.Language> arrayList2 = new ArrayList<>();
        Iterator it2 = hashSet.iterator();
        while (it2.hasNext()) {
            arrayList2.add(createLanguageItem((String) it2.next()));
        }
        arrayList2.sort(Comparator.comparing(new Function() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return TranslatorUtils.m1512$r8$lambda$18OxWnu_CeW2xKMEINptzp4GGI((TranslateController.Language) obj);
            }
        }, new Comparator() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda9
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ((String) obj).compareToIgnoreCase((String) obj2);
            }
        }));
        return arrayList2;
    }

    /* JADX INFO: renamed from: $r8$lambda$-u3qoQyKPFjQQxfvY3tDPXiLrqg, reason: not valid java name */
    public static /* synthetic */ boolean m1511$r8$lambda$u3qoQyKPFjQQxfvY3tDPXiLrqg(TranslateController.Language language) {
        return language == null || TextUtils.isEmpty(language.code) || language.code.contains("-") || language.code.contains("_");
    }

    /* JADX INFO: renamed from: $r8$lambda$18OxWnu_CeW2xKMEINptzp4-GGI, reason: not valid java name */
    public static /* synthetic */ String m1512$r8$lambda$18OxWnu_CeW2xKMEINptzp4GGI(TranslateController.Language language) {
        String str = language.displayName;
        return str == null ? _UrlKt.FRAGMENT_ENCODE_SET : str;
    }

    private static String getResolvedAppLanguageCode() {
        Locale currentLocale = LocaleController.getInstance().getCurrentLocale();
        if (currentLocale == null) {
            currentLocale = Locale.getDefault();
        }
        String strNormalizeLanguageCode = normalizeLanguageCode(currentLocale.getLanguage());
        String country = currentLocale.getCountry();
        if (!TextUtils.isEmpty(strNormalizeLanguageCode) && !TextUtils.isEmpty(country)) {
            String strNormalizeLanguageCode2 = normalizeLanguageCode(strNormalizeLanguageCode + "-" + country);
            if (isTargetLanguageSupportedForCurrentProvider(strNormalizeLanguageCode2)) {
                return strNormalizeLanguageCode2;
            }
        }
        if (!isTargetLanguageSupportedForCurrentProvider(strNormalizeLanguageCode)) {
            String strNormalizeLanguageCode3 = normalizeLanguageCode(LocaleController.getString(R.string.LanguageCode));
            if (isTargetLanguageSupportedForCurrentProvider(strNormalizeLanguageCode3)) {
                return strNormalizeLanguageCode3;
            }
        }
        return strNormalizeLanguageCode;
    }

    public static String getResolvedTargetLanguageCode(String str) {
        if (ASSET_APP.equalsIgnoreCase(str)) {
            return getResolvedAppLanguageCode();
        }
        String strNormalizeLanguageCode = normalizeLanguageCode(str);
        return TextUtils.isEmpty(strNormalizeLanguageCode) ? getResolvedAppLanguageCode() : strNormalizeLanguageCode;
    }

    public static String getResolvedTargetLanguageCode() {
        return getResolvedTargetLanguageCode(ExteraConfig.getTargetLang());
    }

    public static CharSequence[] getTargetLanguageTitles() {
        String str;
        List<TranslateController.Language> indexedTargetLanguages = getIndexedTargetLanguages();
        CharSequence[] charSequenceArr = new CharSequence[indexedTargetLanguages.size() + 1];
        int i = 0;
        charSequenceArr[0] = LocaleController.getString(R.string.TranslationTargetApp);
        while (i < indexedTargetLanguages.size()) {
            TranslateController.Language language = indexedTargetLanguages.get(i);
            i++;
            StringBuilder sb = new StringBuilder();
            sb.append(language.displayName);
            if (language.ownDisplayName == null) {
                str = "";
            } else {
                str = " – " + language.ownDisplayName;
            }
            sb.append(str);
            charSequenceArr[i] = sb.toString();
        }
        return charSequenceArr;
    }

    public static int getTargetLanguageIndexByCode(String str) {
        if (ASSET_APP.equalsIgnoreCase(str)) {
            return 0;
        }
        List<TranslateController.Language> indexedTargetLanguages = getIndexedTargetLanguages();
        String resolvedTargetLanguageCode = getResolvedTargetLanguageCode(str);
        for (int i = 0; i < indexedTargetLanguages.size(); i++) {
            if (TextUtils.equals(indexedTargetLanguages.get(i).code, resolvedTargetLanguageCode)) {
                return i + 1;
            }
        }
        return 0;
    }

    public static String getTargetLanguageCodeByIndex(int i) {
        if (i == 0) {
            return ASSET_APP;
        }
        List<TranslateController.Language> indexedTargetLanguages = getIndexedTargetLanguages();
        int i2 = i - 1;
        if (i2 < 0 || i2 >= indexedTargetLanguages.size()) {
            return null;
        }
        return indexedTargetLanguages.get(i2).code;
    }

    public static String getTargetLanguageTitle() {
        if (isTargetLanguageFollowApp()) {
            return LocaleController.getString(R.string.TranslationTargetApp);
        }
        return getLanguageTitleSystem(getResolvedTargetLanguageCode());
    }

    private static String getStoredSendTargetLanguage() {
        return ExteraConfig.getPreferences().getString("targetLangSend", null);
    }

    private static void storeRecentSendTargetLanguage(String str) {
        final String strNormalizeLanguageCode = normalizeLanguageCode(str);
        if (TextUtils.isEmpty(strNormalizeLanguageCode)) {
            return;
        }
        ArrayList<String> recentSendTargetLanguages = getRecentSendTargetLanguages();
        recentSendTargetLanguages.removeIf(new Predicate() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda11
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return TextUtils.equals((String) obj, strNormalizeLanguageCode);
            }
        });
        recentSendTargetLanguages.add(0, strNormalizeLanguageCode);
        while (recentSendTargetLanguages.size() > 3) {
            recentSendTargetLanguages.remove(recentSendTargetLanguages.size() - 1);
        }
        ExteraConfig.getEditor().putString("targetLangSendRecent", TextUtils.join(",", recentSendTargetLanguages)).apply();
    }

    public static ArrayList<String> getRecentSendTargetLanguages() {
        ArrayList<String> arrayList = new ArrayList<>();
        String string = ExteraConfig.getPreferences().getString("targetLangSendRecent", null);
        if (!TextUtils.isEmpty(string)) {
            HashSet hashSet = new HashSet();
            for (String str : string.split(",")) {
                String strNormalizeLanguageCode = normalizeLanguageCode(str);
                if (!TextUtils.isEmpty(strNormalizeLanguageCode) && !hashSet.contains(strNormalizeLanguageCode) && isTargetLanguageSupportedForCurrentProvider(strNormalizeLanguageCode)) {
                    hashSet.add(strNormalizeLanguageCode);
                    arrayList.add(strNormalizeLanguageCode);
                    if (arrayList.size() >= 3) {
                        break;
                    }
                }
            }
        }
        return arrayList;
    }

    public static String getResolvedSendTargetLanguageCode() {
        String storedSendTargetLanguage = getStoredSendTargetLanguage();
        if (TextUtils.isEmpty(storedSendTargetLanguage)) {
            return "en";
        }
        return getResolvedTargetLanguageCode(storedSendTargetLanguage);
    }

    public static int getSendTargetLanguageIndex() {
        String storedSendTargetLanguage = getStoredSendTargetLanguage();
        if (TextUtils.isEmpty(storedSendTargetLanguage)) {
            storedSendTargetLanguage = "en";
        }
        return getTargetLanguageIndexByCode(storedSendTargetLanguage);
    }

    public static String getSendTargetLanguageTitle() {
        return getLanguageTitleSystem(getResolvedSendTargetLanguageCode());
    }

    public static boolean isSendTargetLanguageFollowApp() {
        return ASSET_APP.equalsIgnoreCase(getStoredSendTargetLanguage());
    }

    public static void setSendTargetLanguage(String str) {
        String strNormalizeLanguageCode = ASSET_APP.equalsIgnoreCase(str) ? ASSET_APP : normalizeLanguageCode(str);
        if (TextUtils.isEmpty(strNormalizeLanguageCode)) {
            ExteraConfig.getEditor().remove("targetLangSend").apply();
            return;
        }
        if (!TextUtils.equals(strNormalizeLanguageCode, ASSET_APP)) {
            storeRecentSendTargetLanguage(strNormalizeLanguageCode);
        }
        if (TextUtils.equals(strNormalizeLanguageCode, "en")) {
            ExteraConfig.getEditor().remove("targetLangSend").apply();
        } else {
            ExteraConfig.getEditor().putString("targetLangSend", strNormalizeLanguageCode).apply();
        }
    }

    public static void setTargetLanguage(String str) {
        String str2 = ASSET_APP;
        String strNormalizeLanguageCode = ASSET_APP.equalsIgnoreCase(str) ? ASSET_APP : normalizeLanguageCode(str);
        if (!TextUtils.isEmpty(strNormalizeLanguageCode)) {
            str2 = strNormalizeLanguageCode;
        }
        ExteraConfig.setTargetLang(str2);
        ExteraConfig.getEditor().putString("targetLang", str2).apply();
        if (MessagesController.getGlobalMainSettings().getBoolean("translate_button_restricted_languages_changed", false)) {
            return;
        }
        MessagesController.getGlobalMainSettings().edit().remove("translate_button_restricted_languages").apply();
        RestrictedLanguagesSelectActivity.invalidateRestrictedLanguages();
        RestrictedLanguagesSelectActivity.checkRestrictedLanguages(false);
        for (int i = 0; i < 16; i++) {
            try {
                MessagesController.getInstance(i).getTranslateController().checkRestrictedLanguagesUpdate();
            } catch (Exception unused) {
            }
        }
    }

    public static boolean isTargetLanguageSupportedForCurrentProvider(String str) {
        if (ASSET_APP.equalsIgnoreCase(str)) {
            return true;
        }
        String strNormalizeLanguageCode = normalizeLanguageCode(str);
        if (TextUtils.isEmpty(strNormalizeLanguageCode)) {
            return false;
        }
        BaseTranslator currentTranslator = getCurrentTranslator();
        Set<String> supportedLanguages = currentTranslator.getSupportedLanguages();
        if (supportedLanguages == null || supportedLanguages.isEmpty()) {
            return (currentTranslator == TelegramTranslator.getInstance() && strNormalizeLanguageCode.contains("-")) ? false : true;
        }
        Iterator<String> it = supportedLanguages.iterator();
        while (it.hasNext()) {
            if (TextUtils.equals(it.next(), strNormalizeLanguageCode)) {
                return true;
            }
        }
        return false;
    }

    public static void ensureTargetLanguageCompatibleWithProvider() {
        if (isTargetLanguageSupportedForCurrentProvider(ExteraConfig.getTargetLang())) {
            return;
        }
        setTargetLanguage(ASSET_APP);
    }

    public static String formatUserAgent() {
        String strValueOf = String.valueOf(Utilities.random.nextInt(7) + 6);
        String[] strArr = DEVICE_MODELS;
        String str = strArr[Utilities.random.nextInt(strArr.length)];
        String[] strArr2 = CHROME_VERSIONS;
        return String.format("Mozilla/5.0 (Linux; Android %s; %s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s Mobile Safari/537.36", strValueOf, str, strArr2[Utilities.random.nextInt(strArr2.length)]);
    }

    public static String getLanguageTitleSystem(final String str) {
        if ("none".equals(str)) {
            return LocaleController.getString(R.string.None);
        }
        return (String) getLanguages().stream().filter(new Predicate() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return TextUtils.equals(((TranslateController.Language) obj).code, str);
            }
        }).findFirst().map(new Function() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((TranslateController.Language) obj).displayName;
            }
        }).orElseGet(new Supplier() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                return TranslatorUtils.$r8$lambda$owKBg0zn5kerZzZQUwDsrIy5hLA(str);
            }
        });
    }

    public static /* synthetic */ String $r8$lambda$owKBg0zn5kerZzZQUwDsrIy5hLA(String str) {
        String strNormalizeLanguageCode = normalizeLanguageCode(str);
        return TextUtils.isEmpty(strNormalizeLanguageCode) ? _UrlKt.FRAGMENT_ENCODE_SET : strNormalizeLanguageCode.toUpperCase(Locale.US);
    }

    public static String getLanguageDisplayName(final String str) {
        return (String) getLanguages().stream().filter(new Predicate() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return TextUtils.equals(((TranslateController.Language) obj).code, str);
            }
        }).findFirst().map(new Function() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda4
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
        final Utilities.CallbackReturn callbackReturn = new Utilities.CallbackReturn() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda13
            @Override // org.telegram.messenger.Utilities.CallbackReturn
            public final Object run(Object obj) {
                return TranslatorUtils.$r8$lambda$W9jvQEKAdqWx7JkQeecPFJnjQRs(chatActivity, messageObject, (URLSpan) obj);
            }
        };
        TLRPC.Message message = messageObject.messageOwner;
        final ArrayList<TLRPC.MessageEntity> arrayList = message != null ? message.entities : null;
        final CharSequence messageText = ChatUtils.getInstance().getMessageText(messageObject, groupedMessages);
        LanguageDetector.detectLanguage(messageText == null ? _UrlKt.FRAGMENT_ENCODE_SET : messageText.toString(), new LanguageDetector.StringCallback() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda14
            @Override // org.telegram.messenger.LanguageDetector.StringCallback
            public final void run(String str) {
                TranslatorUtils.m1515$r8$lambda$JCTNCOaR964lC1lDviVn4VGf54(baseFragment, inputPeer, i, messageText, arrayList, callbackReturn, chatActivity, str);
            }
        }, new LanguageDetector.ExceptionCallback() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda15
            @Override // org.telegram.messenger.LanguageDetector.ExceptionCallback
            public final void run(Exception exc) {
                TranslatorUtils.$r8$lambda$GOSMYs_gu6yU1qrYwaGIZF7j4O4(exc);
            }
        });
    }

    public static /* synthetic */ Boolean $r8$lambda$W9jvQEKAdqWx7JkQeecPFJnjQRs(ChatActivity chatActivity, MessageObject messageObject, URLSpan uRLSpan) {
        chatActivity.didPressMessageUrl(uRLSpan, false, messageObject, null);
        return Boolean.TRUE;
    }

    /* JADX INFO: renamed from: $r8$lambda$JCTNC-OaR964lC1lDviVn4VGf54, reason: not valid java name */
    public static /* synthetic */ void m1515$r8$lambda$JCTNCOaR964lC1lDviVn4VGf54(BaseFragment baseFragment, TLRPC.InputPeer inputPeer, int i, CharSequence charSequence, ArrayList arrayList, Utilities.CallbackReturn callbackReturn, final ChatActivity chatActivity, String str) {
        String toLanguage = TranslateAlert2.getToLanguage();
        if (str != null) {
            if ((!TextUtils.equals(primaryLanguageOf(str), primaryLanguageOf(toLanguage)) || TranslateController.UNKNOWN_LANGUAGE.equals(str)) && !isRestrictedLanguage(str)) {
                TranslateAlert2.showAlert(baseFragment.getContext(), baseFragment, UserConfig.selectedAccount, inputPeer, i, false, str, toLanguage, charSequence, arrayList, false, callbackReturn, new Runnable() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        chatActivity.dimBehindView(false);
                    }
                });
            }
        }
    }

    public static void translateWithDefault(final CharSequence charSequence, TLRPC.InputPeer inputPeer, int i, String str, ArrayList<TLRPC.MessageEntity> arrayList, final TranslateCallback translateCallback) {
        final String resolvedTargetLanguageCode = getResolvedTargetLanguageCode(str);
        TLRPC.TL_messages_translateText tL_messages_translateText = new TLRPC.TL_messages_translateText();
        final TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
        tL_textWithEntities.text = charSequence == null ? _UrlKt.FRAGMENT_ENCODE_SET : charSequence.toString();
        if (arrayList != null) {
            tL_textWithEntities.entities = arrayList;
        }
        int i2 = tL_messages_translateText.flags;
        if (inputPeer != null) {
            tL_messages_translateText.flags = i2 | 1;
            tL_messages_translateText.peer = inputPeer;
            tL_messages_translateText.id.add(Integer.valueOf(i));
        } else {
            tL_messages_translateText.flags = i2 | 2;
            tL_messages_translateText.text.add(tL_textWithEntities);
        }
        String strTrim = resolvedTargetLanguageCode != null ? resolvedTargetLanguageCode.trim() : resolvedTargetLanguageCode;
        if ("nb".equals(strTrim)) {
            strTrim = "no";
        }
        tL_messages_translateText.to_lang = strTrim;
        translateCallback.onReqId(ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_messages_translateText, new RequestDelegate() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda10
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TranslatorUtils.m1513$r8$lambda$AGR88fHZOMFtQZ7va0frxk9zX4(charSequence, resolvedTargetLanguageCode, translateCallback, tL_textWithEntities, tLObject, tL_error);
            }
        }));
    }

    /* JADX INFO: renamed from: $r8$lambda$AG-R88fHZOMFtQZ7va0frxk9zX4, reason: not valid java name */
    public static /* synthetic */ void m1513$r8$lambda$AGR88fHZOMFtQZ7va0frxk9zX4(CharSequence charSequence, String str, final TranslateCallback translateCallback, TLRPC.TL_textWithEntities tL_textWithEntities, TLObject tLObject, final TLRPC.TL_error tL_error) {
        String str2;
        if (tL_error != null && "TRANSLATIONS_DISABLED_ALT".equalsIgnoreCase(tL_error.text)) {
            GoogleTranslator.getInstance().translate(charSequence.toString(), "auto", str, new TranslateCallback() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils.1
                @Override // com.exteragram.messenger.utils.text.TranslatorUtils.TranslateCallback
                public void onSuccess(String str3) {
                    boolean zIsEmpty = TextUtils.isEmpty(str3);
                    TranslateCallback translateCallback2 = translateCallback;
                    if (zIsEmpty) {
                        Objects.requireNonNull(translateCallback2);
                        AndroidUtilities.runOnUIThread(translateCallback2::onFailed);
                    } else {
                        translateCallback2.onSuccess(str3);
                        TLRPC.TL_textWithEntities tL_textWithEntities2 = new TLRPC.TL_textWithEntities();
                        tL_textWithEntities2.text = str3;
                        translateCallback.onSuccess(tL_textWithEntities2);
                    }
                }

                @Override // com.exteragram.messenger.utils.text.TranslatorUtils.TranslateCallback
                public void onSuccess(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    translateCallback.onSuccess(tLObject2, tL_error2);
                }

                @Override // com.exteragram.messenger.utils.text.TranslatorUtils.TranslateCallback
                public void onFailed() {
                    translateCallback.onFailed();
                }

                @Override // com.exteragram.messenger.utils.text.TranslatorUtils.TranslateCallback
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
                    AndroidUtilities.runOnUIThread(translateCallback::onFailed);
                    return;
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda12
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
        AndroidUtilities.runOnUIThread(translateCallback::onFailed);
    }

    public static /* synthetic */ void $r8$lambda$Psw33I0M_hl1fYpsbSR6b9L8Wg8(TranslateCallback translateCallback, TLRPC.TL_messages_translateResult tL_messages_translateResult, TLRPC.TL_error tL_error, TLRPC.TL_textWithEntities tL_textWithEntities, TLRPC.TL_textWithEntities tL_textWithEntities2, String str) {
        translateCallback.onSuccess(tL_messages_translateResult, tL_error);
        if (tL_textWithEntities == null) {
            tL_textWithEntities = tL_textWithEntities2;
        }
        translateCallback.onSuccess(tL_textWithEntities);
        translateCallback.onSuccess(str);
    }

    public static void translate(final CharSequence charSequence, String str, final ArrayList<TLRPC.MessageEntity> arrayList, final TranslateCallback translateCallback) {
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        final String resolvedTargetLanguageCode = getResolvedTargetLanguageCode(str);
        if (LanguageDetector.hasSupport()) {
            LanguageDetector.detectLanguage(charSequence.toString(), new LanguageDetector.StringCallback() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda5
                @Override // org.telegram.messenger.LanguageDetector.StringCallback
                public final void run(String str2) {
                    TranslatorUtils.$r8$lambda$mx_VaLapka_00FV2FdCLbbkzUIg(charSequence, resolvedTargetLanguageCode, arrayList, translateCallback, str2);
                }
            }, new LanguageDetector.ExceptionCallback() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils$$ExternalSyntheticLambda6
                @Override // org.telegram.messenger.LanguageDetector.ExceptionCallback
                public final void run(Exception exc) {
                    TranslatorUtils.translate(charSequence, "auto", resolvedTargetLanguageCode, arrayList, translateCallback);
                }
            });
        } else {
            translate(charSequence, "auto", resolvedTargetLanguageCode, arrayList, translateCallback);
        }
    }

    public static /* synthetic */ void $r8$lambda$mx_VaLapka_00FV2FdCLbbkzUIg(CharSequence charSequence, String str, ArrayList arrayList, TranslateCallback translateCallback, String str2) {
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
        currentTranslator.translate(charSequence.toString(), str, str2, new TranslateCallback() { // from class: com.exteragram.messenger.utils.text.TranslatorUtils.2
            @Override // com.exteragram.messenger.utils.text.TranslatorUtils.TranslateCallback
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

            @Override // com.exteragram.messenger.utils.text.TranslatorUtils.TranslateCallback
            public void onSuccess(TLObject tLObject, TLRPC.TL_error tL_error) {
                translateCallback.onSuccess(tLObject, tL_error);
            }

            @Override // com.exteragram.messenger.utils.text.TranslatorUtils.TranslateCallback
            public void onFailed() {
                translateCallback.onFailed();
            }

            @Override // com.exteragram.messenger.utils.text.TranslatorUtils.TranslateCallback
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
