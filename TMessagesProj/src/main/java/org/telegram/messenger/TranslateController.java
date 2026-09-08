package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.icu.text.Collator;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Pair;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.translators.TelegramTranslator;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import okhttp3.internal.url._UrlKt;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.TranslateAlert2;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.RestrictedLanguagesSelectActivity;

public class TranslateController extends BaseController {
    private static final int GROUPING_TRANSLATIONS_TIMEOUT = 80;
    private static final int MAX_MESSAGES_PER_REQUEST = 20;
    private static final int MAX_SYMBOLS_PER_REQUEST = 25000;
    private static final float REQUIRED_MIN_MESSAGES_TRANSLATABLE_AUTOTRANSLATE = 2.0f;
    private static final float REQUIRED_MIN_PERCENTAGE_MESSAGES_UNKNOWN = 0.65f;
    private static final float REQUIRED_MIN_PERCENTAGE_MESSAGES_UNKNOWN_AUTOTRANSLATE = 0.8f;
    private static final float REQUIRED_PERCENTAGE_MESSAGES_TRANSLATABLE = 0.6f;
    private static final int REQUIRED_TOTAL_MESSAGES_CHECKED = 6;
    private static final int REQUIRED_TOTAL_MESSAGES_CHECKED_AUTOTRANSLATE = 2;
    public static final String UNKNOWN_LANGUAGE = "und";
    private Boolean chatTranslateEnabled;
    private Boolean contextTranslateEnabled;
    private final HashMap<Long, String> detectedDialogLanguage;
    private final HashSet<MessageKey> detectingPhotos;
    private final HashSet<StoryKey> detectingStories;
    private final Set<Long> hideTranslateDialogs;
    private final HashMap<Long, HashMap<Integer, MessageObject>> keptReplyMessageObjects;
    private final HashSet<Integer> loadingSummarizations;
    private final Set<Integer> loadingTranscriptionTranslations;
    private final Set<Integer> loadingTranslations;
    private MessagesController messagesController;
    private ArrayList<Integer> pendingLanguageChecks;
    private final HashMap<Long, ArrayList<PendingPollTranslation>> pendingPollTranslations;
    private final HashMap<Long, ArrayList<PendingRichTranslation>> pendingRichTranslations;
    private final HashMap<Long, ArrayList<PendingTranslation>> pendingTranscriptionsTranslations;
    private final HashMap<Long, ArrayList<PendingTranslation>> pendingTranslations;
    private final HashMap<Long, TranslatableDecision> translatableDialogMessages;
    private final Set<Long> translatableDialogs;
    private final HashMap<Long, String> translateDialogLanguage;
    private final LongSparseArray<Boolean> translatingDialogs;
    private final HashSet<MessageKey> translatingPhotos;
    private final HashSet<StoryKey> translatingStories;
    private static final List<String> languagesOrder = Arrays.asList("en", "ar", "zh", "fr", "de", "it", "ja", "ko", "pt", "ru", "es", "uk");
    private static final List<String> allLanguages = Arrays.asList("af", "sq", "am", "ar", "hy", "az", "eu", "be", "bn", "bs", "bg", "ca", "ceb", "zh-cn", "zh", "zh-tw", "co", "hr", "cs", "da", "nl", "en", "eo", "et", "fi", "fr", "fy", ImageLoader.AUTOPLAY_FILTER_NONLOOP, "ka", "de", "el", "gu", "ht", "ha", "haw", "he", "hi", "hmn", "hu", "is", "ig", "id", "ga", "it", "ja", "jv", "kn", "kk", "km", "rw", "ko", "ku", "ky", "lo", "la", "lv", "lt", "lb", "mk", "mg", "ms", "ml", "mt", "mi", "mr", "mn", "my", "ne", "no", "ny", "or", "ps", "fa", "pl", "pt", "pt-br", "pa", "ro", "ru", "sm", "gd", "sr", "st", "sn", "sd", "si", "sk", "sl", "so", "es", "su", "sw", "sv", "tl", "tg", "ta", "tt", "te", "th", "tr", "tk", "uk", "ur", "ug", "uz", "vi", "cy", "xh", "yi", "yo", "zu");
    private static LinkedHashSet<String> suggestedLanguageCodes = null;

    public static class Language {
        public String code;
        public String displayName;
        public String ownDisplayName;
        public String q;
    }

    public static class TranslatableDecision {
        Set<Integer> certainlyTranslatable = new HashSet();
        Set<Integer> unknown = new HashSet();
        Set<Integer> certainlyNotTranslatable = new HashSet();
    }

    public TranslateController(MessagesController messagesController) {
        super(messagesController.currentAccount);
        this.translatingDialogs = new LongSparseArray<>();
        this.translatableDialogs = new HashSet();
        this.translatableDialogMessages = new HashMap<>();
        this.translateDialogLanguage = new HashMap<>();
        this.detectedDialogLanguage = new HashMap<>();
        this.keptReplyMessageObjects = new HashMap<>();
        this.hideTranslateDialogs = new HashSet();
        this.pendingLanguageChecks = new ArrayList<>();
        this.loadingTranslations = new HashSet();
        this.loadingTranscriptionTranslations = new HashSet();
        this.pendingTranslations = new HashMap<>();
        this.pendingTranscriptionsTranslations = new HashMap<>();
        this.loadingSummarizations = new HashSet<>();
        this.pendingPollTranslations = new HashMap<>();
        this.pendingRichTranslations = new HashMap<>();
        this.detectingStories = new HashSet<>();
        this.translatingStories = new HashSet<>();
        this.detectingPhotos = new HashSet<>();
        this.translatingPhotos = new HashSet<>();
        this.messagesController = messagesController;
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.loadTranslatingDialogsCached();
            }
        }, 150L);
    }

    public boolean isFeatureAvailable() {
        return UserConfig.getInstance(this.currentAccount).isPremium() && isChatTranslateEnabled();
    }

    public boolean isFeatureAvailable(long j) {
        if (!isChatTranslateEnabled()) {
            return false;
        }
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-j));
        if (UserConfig.getInstance(this.currentAccount).isPremium()) {
            return true;
        }
        return chat != null && chat.autotranslation;
    }

    public boolean isChatTranslateEnabled() {
        if (!getMessagesController().isTranslationsAutoEnabled()) {
            return false;
        }
        if (this.chatTranslateEnabled == null) {
            this.chatTranslateEnabled = Boolean.valueOf(this.messagesController.getMainSettings().getBoolean("translate_chat_button", false));
        }
        return this.chatTranslateEnabled.booleanValue();
    }

    public boolean isContextTranslateEnabled() {
        if (!getMessagesController().isTranslationsManualEnabled()) {
            return false;
        }
        if (this.contextTranslateEnabled == null) {
            this.contextTranslateEnabled = Boolean.valueOf(this.messagesController.getMainSettings().getBoolean("translate_button", MessagesController.getGlobalMainSettings().getBoolean("translate_button", true)));
        }
        return this.contextTranslateEnabled.booleanValue();
    }

    public void setContextTranslateEnabled(boolean z) {
        SharedPreferences.Editor editorEdit = this.messagesController.getMainSettings().edit();
        this.contextTranslateEnabled = Boolean.valueOf(z);
        editorEdit.putBoolean("translate_button", z).apply();
    }

    public void setChatTranslateEnabled(boolean z) {
        SharedPreferences.Editor editorEdit = this.messagesController.getMainSettings().edit();
        this.chatTranslateEnabled = Boolean.valueOf(z);
        editorEdit.putBoolean("translate_chat_button", z).apply();
    }

    public static boolean isSummarizable(MessageObject messageObject) {
        TLRPC.Message message;
        if (!ExteraConfig.getTelegramAiSummaries() || messageObject == null || (message = messageObject.messageOwner) == null || message.summary_from_language == null || messageObject.isOutOwner() || messageObject.isRestrictedMessage || messageObject.isSponsored()) {
            return false;
        }
        int i = messageObject.type;
        return (i == 0 || i == 3 || i == 1 || i == 9 || i == 14 || i == 17) && !TextUtils.isEmpty(messageObject.messageOwner.message) && messageObject.messageOwner.message.length() > 100;
    }

    public static boolean isTranslatable(MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null || messageObject.isOutOwner() || messageObject.isRestrictedMessage || messageObject.isSponsored()) {
            return false;
        }
        int i = messageObject.type;
        if (i != 0 && i != 3 && i != 1 && i != 2 && i != 5 && i != 9 && i != 14 && i != 17 && i != 36) {
            return false;
        }
        if (TextUtils.isEmpty(messageObject.messageOwner.message) && !(MessageObject.getMedia(messageObject) instanceof TLRPC.TL_messageMediaPoll)) {
            TLRPC.Message message = messageObject.messageOwner;
            if (message.rich_message == null && (!message.voiceTranscriptionOpen || TextUtils.isEmpty(message.voiceTranscription) || !messageObject.messageOwner.voiceTranscriptionFinal)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDialogTranslatable(long j) {
        return this.translatableDialogs.contains(Long.valueOf(j)) && isFeatureAvailable(j) && !DialogObject.isEncryptedDialog(j) && getUserConfig().getClientUserId() != j;
    }

    public boolean isTranslateDialogHidden(long j) {
        if (this.hideTranslateDialogs.contains(Long.valueOf(j))) {
            return true;
        }
        TLRPC.ChatFull chatFull = getMessagesController().getChatFull(-j);
        if (chatFull != null) {
            return chatFull.translations_disabled;
        }
        TLRPC.UserFull userFull = getMessagesController().getUserFull(j);
        if (userFull != null) {
            return userFull.translations_disabled;
        }
        return false;
    }

    private boolean isChatAutoTranslated(long j) {
        TLRPC.Chat chat;
        return isDialogTranslatable(j) && (chat = getMessagesController().getChat(Long.valueOf(-j))) != null && chat.autotranslation;
    }

    public boolean isTranslatingDialog(long j) {
        return isFeatureAvailable(j) && this.translatingDialogs.get(j, Boolean.valueOf(isChatAutoTranslated(j))).booleanValue();
    }

    public void toggleTranslatingDialog(long j) {
        toggleTranslatingDialog(j, !isTranslatingDialog(j));
    }

    public boolean toggleTranslatingDialog(long j, boolean z) {
        boolean zIsTranslatingDialog = isTranslatingDialog(j);
        boolean z2 = true;
        if (z && !zIsTranslatingDialog) {
            LongSparseArray<Boolean> longSparseArray = this.translatingDialogs;
            Boolean bool = Boolean.TRUE;
            longSparseArray.put(j, bool);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogTranslate, Long.valueOf(j), bool);
        } else if (z || !zIsTranslatingDialog) {
            z2 = false;
        } else {
            LongSparseArray<Boolean> longSparseArray2 = this.translatingDialogs;
            Boolean bool2 = Boolean.FALSE;
            longSparseArray2.put(j, bool2);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogTranslate, Long.valueOf(j), bool2);
            cancelTranslations(j);
        }
        saveTranslatingDialogsCache();
        return z2;
    }

    private int hash(MessageObject messageObject) {
        if (messageObject == null) {
            return 0;
        }
        return Objects.hash(Long.valueOf(messageObject.getDialogId()), Integer.valueOf(messageObject.getId()));
    }

    public static String currentLanguage() {
        String str = LocaleController.getInstance().getCurrentLocaleInfo().pluralLangCode;
        if (str != null) {
            str = str.split("_")[0];
        }
        return TranslatorUtils.normalizeLanguageCode(str);
    }

    public String getDialogTranslateTo(long j) {
        String toLanguage = this.translateDialogLanguage.get(Long.valueOf(j));
        if (toLanguage == null) {
            toLanguage = TranslateAlert2.getToLanguage();
            if (TextUtils.isEmpty(toLanguage) || TranslatorUtils.isRestrictedLanguage(getDialogDetectedLanguage(j)) || TextUtils.equals(TranslatorUtils.primaryLanguageOf(toLanguage), TranslatorUtils.primaryLanguageOf(getDialogDetectedLanguage(j)))) {
                toLanguage = currentLanguage();
            }
        }
        return "nb".equals(toLanguage) ? "no" : toLanguage;
    }

    public void setDialogTranslateTo(final long j, String str) {
        Boolean bool;
        final String strNormalizeLanguageCode = TranslatorUtils.normalizeLanguageCode(str);
        if (TextUtils.equals(getDialogTranslateTo(j), strNormalizeLanguageCode)) {
            return;
        }
        if (isTranslatingDialog(j)) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setDialogTranslateTo$0(j, strNormalizeLanguageCode);
                }
            }, 150L);
        } else {
            synchronized (this) {
                this.translateDialogLanguage.put(Long.valueOf(j), strNormalizeLanguageCode);
            }
        }
        cancelTranslations(j);
        synchronized (this) {
            LongSparseArray<Boolean> longSparseArray = this.translatingDialogs;
            bool = Boolean.FALSE;
            longSparseArray.put(j, bool);
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogTranslate, Long.valueOf(j), bool);
    }

    public int m5190$r8$lambda$I1ZYF6DcBAYUD_g0b0SkyUIzBo(LocaleController.LocaleInfo localeInfo, LocaleController.LocaleInfo localeInfo2, LocaleController.LocaleInfo localeInfo3) {
        if (localeInfo2 == localeInfo) {
            return -1;
        }
        if (localeInfo3 == localeInfo) {
            return 1;
        }
        List<String> list = languagesOrder;
        int iIndexOf = list.indexOf(localeInfo2.pluralLangCode);
        int iIndexOf2 = list.indexOf(localeInfo3.pluralLangCode);
        if (iIndexOf >= 0 && iIndexOf2 >= 0) {
            return iIndexOf - iIndexOf2;
        }
        if (iIndexOf >= 0) {
            return -1;
        }
        if (iIndexOf2 >= 0) {
            return 1;
        }
        int i = localeInfo2.serverIndex;
        int i2 = localeInfo3.serverIndex;
        if (i == i2) {
            return localeInfo2.name.compareTo(localeInfo3.name);
        }
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void checkRestrictedLanguagesUpdate() {
        synchronized (this) {
            try {
                this.translatableDialogMessages.clear();
                ArrayList arrayList = new ArrayList();
                for (Long l : this.translatableDialogs) {
                    long jLongValue = l.longValue();
                    String str = this.detectedDialogLanguage.get(l);
                    if (str != null && isLanguageRestricted(str)) {
                        cancelTranslations(jLongValue);
                        this.translatingDialogs.remove(jLongValue);
                        arrayList.add(l);
                    }
                }
                this.translatableDialogs.clear();
                saveTranslatingDialogsCache();
                int size = arrayList.size();
                int i = 0;
                while (i < size) {
                    Object obj = arrayList.get(i);
                    i++;
                    Long l2 = (Long) obj;
                    l2.longValue();
                    NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogTranslate, l2, Boolean.FALSE);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public String getDialogDetectedLanguage(long j) {
        return this.detectedDialogLanguage.get(Long.valueOf(j));
    }

    public void checkTranslation(MessageObject messageObject, boolean z) {
        checkTranslation(messageObject, z, false);
    }

    void lambda$checkTranslation$6(MessageObject messageObject, long j, TLRPC.TL_textWithEntities tL_textWithEntities) {
        TLRPC.Message message = messageObject.messageOwner;
        message.summaryText = tL_textWithEntities;
        if (tL_textWithEntities == null) {
            message.summarizedOpen = false;
        }
        updateMessageCustomParams(j, messageObject);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.messageTranslated, messageObject, Boolean.TRUE);
    }

    public void lambda$checkTranslation$8(MessageObject messageObject, String str, long j, TLRPC.TL_textWithEntities tL_textWithEntities) {
        messageObject.messageOwner.translatedSummaryLanguage = tL_textWithEntities != null ? TranslatorUtils.normalizeLanguageCode(str) : null;
        TLRPC.Message message = messageObject.messageOwner;
        message.translatedSummaryText = tL_textWithEntities;
        if (tL_textWithEntities == null) {
            message.summarizedOpen = false;
        }
        updateMessageCustomParams(j, messageObject);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.messageTranslated, messageObject, Boolean.TRUE);
    }

    public void lambda$checkTranslation$10(MessageObject messageObject, boolean z, long j, Boolean bool, Integer num, TLRPC.TL_textWithEntities tL_textWithEntities, String str) {
        if (messageObject.getId() != num.intValue()) {
            FileLog.e("wtf, asked to translate " + messageObject.getId() + " but got " + num + "!");
        }
        String strNormalizeLanguageCode = TranslatorUtils.normalizeLanguageCode(str);
        messageObject.messageOwner.translatedToLanguage = strNormalizeLanguageCode;
        boolean zBooleanValue = bool.booleanValue();
        TLRPC.Message message = messageObject.messageOwner;
        if (zBooleanValue) {
            message.translatedVoiceTranscription = tL_textWithEntities;
        } else {
            message.translatedText = tL_textWithEntities;
        }
        messageObject.messageOwner.translatedPoll = null;
        if (z) {
            keepReplyMessage(messageObject);
        }
        updateMessageCustomParams(j, messageObject);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.messageTranslated, messageObject);
        ArrayList<MessageObject> arrayList = this.messagesController.dialogMessage.get(j);
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                MessageObject messageObject2 = arrayList.get(i);
                if (messageObject2 != null && messageObject2.getId() == messageObject.getId()) {
                    messageObject2.messageOwner.translatedToLanguage = strNormalizeLanguageCode;
                    boolean zBooleanValue2 = bool.booleanValue();
                    TLRPC.Message message2 = messageObject2.messageOwner;
                    if (zBooleanValue2) {
                        message2.translatedVoiceTranscription = tL_textWithEntities;
                    } else {
                        message2.translatedText = tL_textWithEntities;
                    }
                    messageObject2.messageOwner.translatedPoll = null;
                    if (messageObject2.updateTranslation()) {
                        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, 0);
                        return;
                    }
                    return;
                }
            }
        }
    }

    public void invalidateTranslation(final MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null) {
            return;
        }
        final long dialogId = messageObject.getDialogId();
        if (isFeatureAvailable(dialogId)) {
            TLRPC.Message message = messageObject.messageOwner;
            message.translatedToLanguage = null;
            message.translatedText = null;
            message.translatedVoiceTranscription = null;
            message.translatedPoll = null;
            message.summaryText = null;
            message.translatedSummaryText = null;
            message.translatedSummaryLanguage = null;
            updateMessageCustomParams(dialogId, messageObject);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$invalidateTranslation$11(messageObject, dialogId);
                }
            });
        }
    }

    public void lambda$checkDialogMessageSure$13(long j) {
        final ArrayList<MessageObject> arrayList = this.messagesController.dialogMessage.get(j);
        if (arrayList == null) {
            return;
        }
        final ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject messageObject = arrayList.get(i);
            if (messageObject == null || messageObject.messageOwner == null) {
                arrayList2.add(null);
            } else {
                arrayList2.add(getMessagesStorage().getMessageWithCustomParamsOnlyInternal(messageObject.getId(), messageObject.getDialogId()));
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkDialogMessageSure$12(arrayList2, arrayList);
            }
        });
    }

    public void lambda$checkLanguage$15(final MessageObject messageObject, final long j, final int i, final String str) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkLanguage$14(str, messageObject, j, i);
            }
        });
    }

    public void lambda$checkLanguage$14(String str, MessageObject messageObject, long j, int i) {
        if (str == null) {
            str = UNKNOWN_LANGUAGE;
        }
        messageObject.messageOwner.originalLanguage = str;
        updateMessageCustomParams(j, messageObject);
        this.pendingLanguageChecks.remove(Integer.valueOf(i));
        checkDialogTranslatable(messageObject);
    }

    public void lambda$checkLanguage$16(MessageObject messageObject, long j, int i) {
        messageObject.messageOwner.originalLanguage = UNKNOWN_LANGUAGE;
        updateMessageCustomParams(j, messageObject);
        this.pendingLanguageChecks.remove(Integer.valueOf(i));
    }

    private void checkDialogTranslatable(MessageObject messageObject) {
        String str;
        String str2;
        if (messageObject == null || messageObject.messageOwner == null) {
            return;
        }
        final long dialogId = messageObject.getDialogId();
        TranslatableDecision translatableDecision = this.translatableDialogMessages.get(Long.valueOf(dialogId));
        if (translatableDecision == null) {
            HashMap<Long, TranslatableDecision> map = this.translatableDialogMessages;
            Long lValueOf = Long.valueOf(dialogId);
            TranslatableDecision translatableDecision2 = new TranslatableDecision();
            map.put(lValueOf, translatableDecision2);
            translatableDecision = translatableDecision2;
        }
        boolean z = false;
        boolean z2 = isTranslatable(messageObject) && ((str2 = messageObject.messageOwner.originalLanguage) == null || UNKNOWN_LANGUAGE.equals(str2));
        if (isTranslatable(messageObject) && (str = messageObject.messageOwner.originalLanguage) != null && !UNKNOWN_LANGUAGE.equals(str) && !isLanguageRestricted(messageObject.messageOwner.originalLanguage)) {
            z = true;
        }
        if (z2) {
            translatableDecision.unknown.add(Integer.valueOf(messageObject.getId()));
        } else {
            (z ? translatableDecision.certainlyTranslatable : translatableDecision.certainlyNotTranslatable).add(Integer.valueOf(messageObject.getId()));
        }
        if (!z2) {
            this.detectedDialogLanguage.put(Long.valueOf(dialogId), messageObject.messageOwner.originalLanguage);
        }
        int size = translatableDecision.certainlyTranslatable.size();
        int size2 = translatableDecision.unknown.size();
        int size3 = translatableDecision.certainlyNotTranslatable.size();
        int i = size + size2 + size3;
        boolean zIsChatAutoTranslated = isChatAutoTranslated(dialogId);
        if (i >= (zIsChatAutoTranslated ? 2 : 6)) {
            if (zIsChatAutoTranslated) {
                if (size < REQUIRED_MIN_MESSAGES_TRANSLATABLE_AUTOTRANSLATE) {
                    return;
                }
            } else if (size / (size + size3) < REQUIRED_PERCENTAGE_MESSAGES_TRANSLATABLE) {
                return;
            }
            if (size2 / i < (zIsChatAutoTranslated ? REQUIRED_MIN_PERCENTAGE_MESSAGES_UNKNOWN_AUTOTRANSLATE : REQUIRED_MIN_PERCENTAGE_MESSAGES_UNKNOWN)) {
                this.translatableDialogs.add(Long.valueOf(dialogId));
                this.translatableDialogMessages.remove(Long.valueOf(dialogId));
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$checkDialogTranslatable$19(dialogId);
                    }
                }, 450L);
            }
        }
    }

    public void lambda$pushToSummarize$21(int i, Utilities.Callback callback, TLRPC.TL_textWithEntities tL_textWithEntities, TLRPC.TL_error tL_error) {
        final BaseFragment safeLastFragment;
        if (tL_textWithEntities != null) {
            this.loadingSummarizations.remove(Integer.valueOf(i));
            callback.run(tL_textWithEntities);
        } else if (tL_error != null) {
            if ("SUMMARY_FLOOD_PREMIUM".equalsIgnoreCase(tL_error.text) && (safeLastFragment = LaunchActivity.getSafeLastFragment()) != null) {
                BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.star_premium_2, LocaleController.getString(R.string.SummaryLimit), LocaleController.getString(R.string.SummaryLimitUpgrade), new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        safeLastFragment.presentFragment(new PremiumPreviewFragment("summarize_limit"));
                    }
                }).setDuration(5000).show(true);
            }
            this.loadingSummarizations.remove(Integer.valueOf(i));
            callback.run(null);
        }
    }

    public static class PendingTranslation {
        ArrayList<Utilities.Callback4<Boolean, Integer, TLRPC.TL_textWithEntities, String>> callbacks;
        int delay;
        String language;
        ArrayList<Integer> messageIds;
        ArrayList<TLRPC.TL_textWithEntities> messageTexts;
        int reqId;
        Runnable runnable;
        int symbolsCount;

        private PendingTranslation() {
            this.messageIds = new ArrayList<>();
            this.messageTexts = new ArrayList<>();
            this.callbacks = new ArrayList<>();
            this.delay = 80;
            this.reqId = -1;
        }
    }

    private void pushToTranslate(MessageObject messageObject, String str, Utilities.Callback4<Boolean, Integer, TLRPC.TL_textWithEntities, String> callback4) {
        PendingTranslation pendingTranslation;
        int length;
        String str2;
        if (messageObject == null || messageObject.messageOwner == null || messageObject.getId() < 0 || callback4 == null) {
            return;
        }
        TLRPC.Message message = messageObject.messageOwner;
        final boolean z = false;
        if (message.voiceTranscription != null && message.voiceTranscriptionFinal && message.voiceTranscriptionOpen) {
            z = true;
        }
        final long dialogId = messageObject.getDialogId();
        HashMap<Long, ArrayList<PendingTranslation>> map = z ? this.pendingTranscriptionsTranslations : this.pendingTranslations;
        Set<Integer> set = z ? this.loadingTranscriptionTranslations : this.loadingTranslations;
        TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
        TLRPC.Message message2 = messageObject.messageOwner;
        if (z) {
            String str3 = message2.voiceTranscription;
            tL_textWithEntities.text = str3;
            if (TextUtils.isEmpty(str3)) {
                return;
            }
        } else {
            tL_textWithEntities.text = message2.message;
            tL_textWithEntities.entities = message2.entities;
        }
        synchronized (this) {
            try {
                ArrayList<PendingTranslation> arrayList = map.get(Long.valueOf(dialogId));
                if (arrayList == null) {
                    Long lValueOf = Long.valueOf(dialogId);
                    ArrayList<PendingTranslation> arrayList2 = new ArrayList<>();
                    map.put(lValueOf, arrayList2);
                    arrayList = arrayList2;
                }
                if (arrayList.isEmpty()) {
                    pendingTranslation = new PendingTranslation();
                    arrayList.add(pendingTranslation);
                } else {
                    pendingTranslation = arrayList.get(arrayList.size() - 1);
                }
                if (pendingTranslation.messageIds.contains(Integer.valueOf(messageObject.getId()))) {
                    return;
                }
                TLRPC.Message message3 = messageObject.messageOwner;
                if (z) {
                    String str4 = message3.voiceTranscription;
                    length = str4 == null ? 0 : str4.length();
                } else if (message3 != null && (str2 = message3.message) != null) {
                    length = str2.length();
                } else {
                    CharSequence charSequence = messageObject.caption;
                    if (charSequence != null) {
                        length = charSequence.length();
                    } else {
                        CharSequence charSequence2 = messageObject.messageText;
                        length = charSequence2 != null ? charSequence2.length() : 0;
                    }
                }
                if (pendingTranslation.symbolsCount + length >= MAX_SYMBOLS_PER_REQUEST || pendingTranslation.messageIds.size() + 1 >= 20) {
                    AndroidUtilities.cancelRunOnUIThread(pendingTranslation.runnable);
                    AndroidUtilities.runOnUIThread(pendingTranslation.runnable);
                    pendingTranslation = new PendingTranslation();
                    arrayList.add(pendingTranslation);
                }
                Runnable runnable = pendingTranslation.runnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                set.add(Integer.valueOf(messageObject.getId()));
                pendingTranslation.messageIds.add(Integer.valueOf(messageObject.getId()));
                FileLog.d("pending translation +" + messageObject.getId() + " message");
                pendingTranslation.messageTexts.add(tL_textWithEntities);
                pendingTranslation.callbacks.add(callback4);
                pendingTranslation.language = str;
                pendingTranslation.symbolsCount += length;
                final HashMap<Long, ArrayList<PendingTranslation>> map2 = map;
                final Set<Integer> set2 = set;
                final PendingTranslation pendingTranslation2 = pendingTranslation;
                Runnable runnable2 = new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$pushToTranslate$26(map2, dialogId, pendingTranslation2, z, set2);
                    }
                };
                pendingTranslation2.runnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, pendingTranslation2.delay);
                pendingTranslation2.delay /= 2;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void lambda$pushToTranslate$22(Utilities.Callback4 callback4, boolean z, int i, String str, long j, String str2, Boolean bool) {
        if (str2 != null) {
            TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
            tL_textWithEntities.text = str2;
            callback4.run(Boolean.valueOf(z), Integer.valueOf(i), tL_textWithEntities, str);
        } else {
            toggleTranslatingDialog(j, false);
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 1, LocaleController.getString(bool.booleanValue() ? R.string.TranslationFailedAlert1 : R.string.TranslationFailedAlert2));
        }
    }

    public void lambda$pushToTranslate$23(Utilities.Callback4 callback4, boolean z, int i, String str, long j, String str2, Boolean bool) {
        if (str2 != null) {
            TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
            tL_textWithEntities.text = str2;
            callback4.run(Boolean.valueOf(z), Integer.valueOf(i), tL_textWithEntities, str);
        } else {
            toggleTranslatingDialog(j, false);
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 1, LocaleController.getString(bool.booleanValue() ? R.string.TranslationFailedAlert1 : R.string.TranslationFailedAlert2));
        }
    }

    public void lambda$pushPollToTranslate$29(final long j, final PendingPollTranslation pendingPollTranslation) {
        synchronized (this) {
            try {
                ArrayList<PendingTranslation> arrayList = this.pendingTranslations.get(Long.valueOf(j));
                if (arrayList != null) {
                    arrayList.remove(pendingPollTranslation);
                    if (arrayList.isEmpty()) {
                        this.pendingTranslations.remove(Long.valueOf(j));
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        TLRPC.TL_messages_translateText tL_messages_translateText = new TLRPC.TL_messages_translateText();
        tL_messages_translateText.flags |= 2;
        ArrayList<Pair<PollText, PollText>> arrayList2 = pendingPollTranslation.messageTexts;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Pair<PollText, PollText> pair = arrayList2.get(i);
            i++;
            Pair<PollText, PollText> pair2 = pair;
            PollText pollText = (PollText) pair2.first;
            PollText pollText2 = (PollText) pair2.second;
            TLRPC.TL_textWithEntities tL_textWithEntities = pollText.question;
            if (tL_textWithEntities != null && (pollText2 == null || pollText2.question == null)) {
                tL_messages_translateText.text.add(tL_textWithEntities);
            }
            if (pollText.answers.size() != (pollText2 == null ? 0 : pollText2.answers.size())) {
                ArrayList<TLRPC.PollAnswer> arrayList3 = pollText.answers;
                int size2 = arrayList3.size();
                int i2 = 0;
                while (i2 < size2) {
                    TLRPC.PollAnswer pollAnswer = arrayList3.get(i2);
                    i2++;
                    tL_messages_translateText.text.add(pollAnswer.text);
                }
            }
            TLRPC.TL_textWithEntities tL_textWithEntities2 = pollText.solution;
            if (tL_textWithEntities2 != null && (pollText2 == null || pollText2.solution == null)) {
                tL_messages_translateText.text.add(tL_textWithEntities2);
            }
        }
        tL_messages_translateText.to_lang = normalizeLanguage(pendingPollTranslation.language);
        int iSendRequest = getConnectionsManager().sendRequest(tL_messages_translateText, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$pushPollToTranslate$28(pendingPollTranslation, j, tLObject, tL_error);
            }
        });
        synchronized (this) {
            pendingPollTranslation.reqId = iSendRequest;
        }
    }

    public void lambda$pushPollToTranslate$27(PendingPollTranslation pendingPollTranslation, TLObject tLObject, TLRPC.TL_error tL_error, long j) {
        ArrayList<Integer> arrayList;
        ArrayList<Utilities.Callback3<Integer, PollText, String>> arrayList2;
        ArrayList<Pair<PollText, PollText>> arrayList3;
        int i;
        TLRPC.TL_textWithEntities tL_textWithEntities;
        int i2;
        ArrayList<Pair<PollText, PollText>> arrayList4;
        TLRPC.TL_textWithEntities tL_textWithEntities2;
        TLRPC.TL_textWithEntities tL_textWithEntities3;
        int i3;
        TLRPC.TL_textWithEntities tL_textWithEntities4;
        TLRPC.TL_textWithEntities tL_textWithEntities5;
        synchronized (this) {
            arrayList = pendingPollTranslation.messageIds;
            arrayList2 = pendingPollTranslation.callbacks;
            arrayList3 = pendingPollTranslation.messageTexts;
        }
        if (tLObject instanceof TLRPC.TL_messages_translateResult) {
            ArrayList<TLRPC.TL_textWithEntities> arrayList5 = ((TLRPC.TL_messages_translateResult) tLObject).result;
            ArrayList arrayList6 = new ArrayList();
            int size = arrayList3.size();
            int i4 = 0;
            int i5 = 0;
            while (i5 < size) {
                Pair<PollText, PollText> pair = arrayList3.get(i5);
                i5++;
                Pair<PollText, PollText> pair2 = pair;
                PollText pollText = (PollText) pair2.first;
                PollText pollText2 = (PollText) pair2.second;
                PollText pollText3 = new PollText();
                if (pollText2 != null && (tL_textWithEntities5 = pollText2.question) != null) {
                    pollText3.question = tL_textWithEntities5;
                } else if (pollText.question != null) {
                    if (i4 >= arrayList5.size()) {
                        tL_textWithEntities = new TLRPC.TL_textWithEntities();
                    } else {
                        tL_textWithEntities = arrayList5.get(i4);
                        i4++;
                    }
                    pollText3.question = TranslateAlert2.preprocess(pollText.question, tL_textWithEntities);
                }
                if (pollText.answers.size() != (pollText2 == null ? 0 : pollText2.answers.size())) {
                    ArrayList<TLRPC.PollAnswer> arrayList7 = pollText.answers;
                    int size2 = arrayList7.size();
                    int i6 = 0;
                    while (i6 < size2) {
                        TLRPC.PollAnswer pollAnswer = arrayList7.get(i6);
                        i6++;
                        int i7 = size;
                        TLRPC.PollAnswer pollAnswer2 = pollAnswer;
                        ArrayList<Pair<PollText, PollText>> arrayList8 = arrayList3;
                        if (i4 >= arrayList5.size()) {
                            tL_textWithEntities4 = new TLRPC.TL_textWithEntities();
                            i3 = i4;
                        } else {
                            i3 = i4 + 1;
                            tL_textWithEntities4 = arrayList5.get(i4);
                        }
                        TLRPC.TL_pollAnswer tL_pollAnswer = new TLRPC.TL_pollAnswer();
                        tL_pollAnswer.text = tL_textWithEntities4;
                        tL_pollAnswer.option = pollAnswer2.option;
                        pollText3.answers.add(tL_pollAnswer);
                        size = i7;
                        i4 = i3;
                        arrayList3 = arrayList8;
                    }
                    i2 = size;
                    arrayList4 = arrayList3;
                } else {
                    i2 = size;
                    arrayList4 = arrayList3;
                    if (pollText2 != null) {
                        pollText3.answers = pollText2.answers;
                    }
                }
                if (pollText2 != null && (tL_textWithEntities3 = pollText2.solution) != null) {
                    pollText3.solution = tL_textWithEntities3;
                } else if (pollText.solution != null) {
                    if (i4 >= arrayList5.size()) {
                        tL_textWithEntities2 = new TLRPC.TL_textWithEntities();
                    } else {
                        TLRPC.TL_textWithEntities tL_textWithEntities6 = arrayList5.get(i4);
                        i4++;
                        tL_textWithEntities2 = tL_textWithEntities6;
                    }
                    pollText3.solution = TranslateAlert2.preprocess(pollText.solution, tL_textWithEntities2);
                }
                arrayList6.add(pollText3);
                size = i2;
                arrayList3 = arrayList4;
            }
            int iMin = Math.min(arrayList2.size(), arrayList6.size());
            for (int i8 = 0; i8 < iMin; i8++) {
                arrayList2.get(i8).run(arrayList.get(i8), (PollText) arrayList6.get(i8), pendingPollTranslation.language);
            }
            i = 0;
        } else if (tL_error != null && "TO_LANG_INVALID".equals(tL_error.text)) {
            i = 0;
            toggleTranslatingDialog(j, false);
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 1, LocaleController.getString(R.string.TranslationFailedAlert2));
        } else {
            i = 0;
            if (tL_error != null && "QUOTA_EXCEEDED".equals(tL_error.text)) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 1, LocaleController.getString(R.string.TranslationFailedAlert1));
            }
            for (int i9 = 0; i9 < arrayList2.size(); i9++) {
                arrayList2.get(i9).run(arrayList.get(i9), null, pendingPollTranslation.language);
            }
        }
        synchronized (this) {
            for (int i10 = i; i10 < arrayList.size(); i10++) {
                try {
                    this.loadingTranslations.remove(arrayList.get(i10));
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public static class PendingRichTranslation {
        ArrayList<Utilities.Callback3<Integer, TL_iv.RichMessage, String>> callbacks;
        int delay;
        String language;
        ArrayList<Integer> messageIds;
        int reqId;
        Runnable runnable;

        private PendingRichTranslation() {
            this.messageIds = new ArrayList<>();
            this.callbacks = new ArrayList<>();
            this.delay = 80;
            this.reqId = -1;
        }
    }

    private void pushRichMessageToTranslate(MessageObject messageObject, String str, Utilities.Callback3<Integer, TL_iv.RichMessage, String> callback3) {
        final PendingRichTranslation pendingRichTranslation;
        if (messageObject == null || messageObject.messageOwner == null || messageObject.getId() < 0 || callback3 == null || messageObject.messageOwner.rich_message == null) {
            return;
        }
        final long dialogId = messageObject.getDialogId();
        synchronized (this) {
            try {
                ArrayList<PendingRichTranslation> arrayList = this.pendingRichTranslations.get(Long.valueOf(dialogId));
                if (arrayList == null) {
                    HashMap<Long, ArrayList<PendingRichTranslation>> map = this.pendingRichTranslations;
                    Long lValueOf = Long.valueOf(dialogId);
                    ArrayList<PendingRichTranslation> arrayList2 = new ArrayList<>();
                    map.put(lValueOf, arrayList2);
                    arrayList = arrayList2;
                }
                if (arrayList.isEmpty()) {
                    pendingRichTranslation = new PendingRichTranslation();
                    arrayList.add(pendingRichTranslation);
                } else {
                    pendingRichTranslation = arrayList.get(arrayList.size() - 1);
                }
                if (pendingRichTranslation.messageIds.contains(Integer.valueOf(messageObject.getId()))) {
                    return;
                }
                if (pendingRichTranslation.messageIds.size() + 1 >= 20) {
                    AndroidUtilities.cancelRunOnUIThread(pendingRichTranslation.runnable);
                    AndroidUtilities.runOnUIThread(pendingRichTranslation.runnable);
                    pendingRichTranslation = new PendingRichTranslation();
                    arrayList.add(pendingRichTranslation);
                }
                Runnable runnable = pendingRichTranslation.runnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                this.loadingTranslations.add(Integer.valueOf(messageObject.getId()));
                pendingRichTranslation.messageIds.add(Integer.valueOf(messageObject.getId()));
                FileLog.d("pending translation +" + messageObject.getId() + " rich message");
                pendingRichTranslation.callbacks.add(callback3);
                pendingRichTranslation.language = str;
                Runnable runnable2 = new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$pushRichMessageToTranslate$32(dialogId, pendingRichTranslation);
                    }
                };
                pendingRichTranslation.runnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, pendingRichTranslation.delay);
                pendingRichTranslation.delay /= 2;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void lambda$pushRichMessageToTranslate$31(final PendingRichTranslation pendingRichTranslation, final long j, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$pushRichMessageToTranslate$30(pendingRichTranslation, tLObject, tL_error, j);
            }
        });
    }

    public public boolean isTranslating(MessageObject messageObject) {
        boolean z;
        TLRPC.Message message;
        if (messageObject != null && (message = messageObject.messageOwner) != null && message.summarizedOpen) {
            return this.loadingSummarizations.contains(Integer.valueOf(Objects.hash(Long.valueOf(messageObject.getDialogId()), Integer.valueOf(messageObject.getId()), Integer.valueOf(isTranslatingDialog(messageObject.getDialogId()) ? 1 : 0))));
        }
        synchronized (this) {
            if (messageObject != null) {
                try {
                    TLRPC.Message message2 = messageObject.messageOwner;
                    if (message2 == null) {
                        z = false;
                    } else {
                        if (((message2.voiceTranscriptionOpen && message2.voiceTranscriptionFinal) ? this.loadingTranscriptionTranslations : this.loadingTranslations).contains(Integer.valueOf(messageObject.getId())) && isTranslatingDialog(messageObject.getDialogId())) {
                            z = true;
                        } else {
                            z = false;
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                z = false;
            }
        }
        return z;
    }

    public boolean isTranslating(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages) {
        if (messageObject == null || !isTranslatingDialog(messageObject.getDialogId())) {
            return false;
        }
        TLRPC.Message message = messageObject.messageOwner;
        boolean z = message != null && message.voiceTranscriptionOpen && message.voiceTranscriptionFinal;
        synchronized (this) {
            try {
                if ((z ? this.loadingTranscriptionTranslations : this.loadingTranslations).contains(Integer.valueOf(messageObject.getId()))) {
                    return true;
                }
                if (groupedMessages != null) {
                    ArrayList<MessageObject> arrayList = groupedMessages.messages;
                    int size = arrayList.size();
                    int i = 0;
                    while (i < size) {
                        MessageObject messageObject2 = arrayList.get(i);
                        i++;
                        if ((z ? this.loadingTranscriptionTranslations : this.loadingTranslations).contains(Integer.valueOf(messageObject2.getId()))) {
                            return true;
                        }
                    }
                }
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void cancelAllTranslations() {
        synchronized (this) {
            try {
                for (ArrayList<PendingTranslation> arrayList : this.pendingTranslations.values()) {
                    if (arrayList != null) {
                        int size = arrayList.size();
                        int i = 0;
                        while (i < size) {
                            PendingTranslation pendingTranslation = arrayList.get(i);
                            i++;
                            PendingTranslation pendingTranslation2 = pendingTranslation;
                            AndroidUtilities.cancelRunOnUIThread(pendingTranslation2.runnable);
                            if (pendingTranslation2.reqId != -1) {
                                getConnectionsManager().cancelRequest(pendingTranslation2.reqId, true);
                                ArrayList<Integer> arrayList2 = pendingTranslation2.messageIds;
                                int size2 = arrayList2.size();
                                int i2 = 0;
                                while (i2 < size2) {
                                    Integer num = arrayList2.get(i2);
                                    i2++;
                                    this.loadingTranslations.remove(num);
                                }
                            }
                        }
                    }
                }
                for (ArrayList<PendingTranslation> arrayList3 : this.pendingTranscriptionsTranslations.values()) {
                    if (arrayList3 != null) {
                        int size3 = arrayList3.size();
                        int i3 = 0;
                        while (i3 < size3) {
                            PendingTranslation pendingTranslation3 = arrayList3.get(i3);
                            i3++;
                            PendingTranslation pendingTranslation4 = pendingTranslation3;
                            AndroidUtilities.cancelRunOnUIThread(pendingTranslation4.runnable);
                            if (pendingTranslation4.reqId != -1) {
                                getConnectionsManager().cancelRequest(pendingTranslation4.reqId, true);
                                ArrayList<Integer> arrayList4 = pendingTranslation4.messageIds;
                                int size4 = arrayList4.size();
                                int i4 = 0;
                                while (i4 < size4) {
                                    Integer num2 = arrayList4.get(i4);
                                    i4++;
                                    this.loadingTranscriptionTranslations.remove(num2);
                                }
                            }
                        }
                    }
                }
                for (ArrayList<PendingPollTranslation> arrayList5 : this.pendingPollTranslations.values()) {
                    if (arrayList5 != null) {
                        int size5 = arrayList5.size();
                        int i5 = 0;
                        while (i5 < size5) {
                            PendingPollTranslation pendingPollTranslation = arrayList5.get(i5);
                            i5++;
                            PendingPollTranslation pendingPollTranslation2 = pendingPollTranslation;
                            AndroidUtilities.cancelRunOnUIThread(pendingPollTranslation2.runnable);
                            if (pendingPollTranslation2.reqId != -1) {
                                getConnectionsManager().cancelRequest(pendingPollTranslation2.reqId, true);
                                ArrayList<Integer> arrayList6 = pendingPollTranslation2.messageIds;
                                int size6 = arrayList6.size();
                                int i6 = 0;
                                while (i6 < size6) {
                                    Integer num3 = arrayList6.get(i6);
                                    i6++;
                                    this.loadingTranslations.remove(num3);
                                }
                            }
                        }
                    }
                }
                for (ArrayList<PendingRichTranslation> arrayList7 : this.pendingRichTranslations.values()) {
                    if (arrayList7 != null) {
                        int size7 = arrayList7.size();
                        int i7 = 0;
                        while (i7 < size7) {
                            PendingRichTranslation pendingRichTranslation = arrayList7.get(i7);
                            i7++;
                            PendingRichTranslation pendingRichTranslation2 = pendingRichTranslation;
                            AndroidUtilities.cancelRunOnUIThread(pendingRichTranslation2.runnable);
                            if (pendingRichTranslation2.reqId != -1) {
                                getConnectionsManager().cancelRequest(pendingRichTranslation2.reqId, true);
                                ArrayList<Integer> arrayList8 = pendingRichTranslation2.messageIds;
                                int size8 = arrayList8.size();
                                int i8 = 0;
                                while (i8 < size8) {
                                    Integer num4 = arrayList8.get(i8);
                                    i8++;
                                    this.loadingTranslations.remove(num4);
                                }
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public void cancelTranslations(long j) {
        synchronized (this) {
            try {
                ArrayList<PendingTranslation> arrayList = this.pendingTranslations.get(Long.valueOf(j));
                if (arrayList != null) {
                    int size = arrayList.size();
                    int i = 0;
                    while (i < size) {
                        PendingTranslation pendingTranslation = arrayList.get(i);
                        i++;
                        PendingTranslation pendingTranslation2 = pendingTranslation;
                        AndroidUtilities.cancelRunOnUIThread(pendingTranslation2.runnable);
                        if (pendingTranslation2.reqId != -1) {
                            getConnectionsManager().cancelRequest(pendingTranslation2.reqId, true);
                            ArrayList<Integer> arrayList2 = pendingTranslation2.messageIds;
                            int size2 = arrayList2.size();
                            int i2 = 0;
                            while (i2 < size2) {
                                Integer num = arrayList2.get(i2);
                                i2++;
                                this.loadingTranslations.remove(num);
                            }
                        }
                    }
                    this.pendingTranslations.remove(Long.valueOf(j));
                }
                ArrayList<PendingTranslation> arrayList3 = this.pendingTranscriptionsTranslations.get(Long.valueOf(j));
                if (arrayList3 != null) {
                    int size3 = arrayList3.size();
                    int i3 = 0;
                    while (i3 < size3) {
                        PendingTranslation pendingTranslation3 = arrayList3.get(i3);
                        i3++;
                        PendingTranslation pendingTranslation4 = pendingTranslation3;
                        AndroidUtilities.cancelRunOnUIThread(pendingTranslation4.runnable);
                        if (pendingTranslation4.reqId != -1) {
                            getConnectionsManager().cancelRequest(pendingTranslation4.reqId, true);
                            ArrayList<Integer> arrayList4 = pendingTranslation4.messageIds;
                            int size4 = arrayList4.size();
                            int i4 = 0;
                            while (i4 < size4) {
                                Integer num2 = arrayList4.get(i4);
                                i4++;
                                this.loadingTranscriptionTranslations.remove(num2);
                            }
                        }
                    }
                    this.pendingTranscriptionsTranslations.remove(Long.valueOf(j));
                }
                ArrayList<PendingPollTranslation> arrayList5 = this.pendingPollTranslations.get(Long.valueOf(j));
                if (arrayList5 != null) {
                    int size5 = arrayList5.size();
                    int i5 = 0;
                    while (i5 < size5) {
                        PendingPollTranslation pendingPollTranslation = arrayList5.get(i5);
                        i5++;
                        PendingPollTranslation pendingPollTranslation2 = pendingPollTranslation;
                        AndroidUtilities.cancelRunOnUIThread(pendingPollTranslation2.runnable);
                        if (pendingPollTranslation2.reqId != -1) {
                            getConnectionsManager().cancelRequest(pendingPollTranslation2.reqId, true);
                            ArrayList<Integer> arrayList6 = pendingPollTranslation2.messageIds;
                            int size6 = arrayList6.size();
                            int i6 = 0;
                            while (i6 < size6) {
                                Integer num3 = arrayList6.get(i6);
                                i6++;
                                this.loadingTranslations.remove(num3);
                            }
                        }
                    }
                    this.pendingPollTranslations.remove(Long.valueOf(j));
                }
                ArrayList<PendingRichTranslation> arrayList7 = this.pendingRichTranslations.get(Long.valueOf(j));
                if (arrayList7 != null) {
                    int size7 = arrayList7.size();
                    int i7 = 0;
                    while (i7 < size7) {
                        PendingRichTranslation pendingRichTranslation = arrayList7.get(i7);
                        i7++;
                        PendingRichTranslation pendingRichTranslation2 = pendingRichTranslation;
                        AndroidUtilities.cancelRunOnUIThread(pendingRichTranslation2.runnable);
                        if (pendingRichTranslation2.reqId != -1) {
                            getConnectionsManager().cancelRequest(pendingRichTranslation2.reqId, true);
                            ArrayList<Integer> arrayList8 = pendingRichTranslation2.messageIds;
                            int size8 = arrayList8.size();
                            int i8 = 0;
                            while (i8 < size8) {
                                Integer num4 = arrayList8.get(i8);
                                i8++;
                                this.loadingTranslations.remove(num4);
                            }
                        }
                    }
                    this.pendingRichTranslations.remove(Long.valueOf(j));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void keepReplyMessage(MessageObject messageObject) {
        if (messageObject == null) {
            return;
        }
        HashMap<Integer, MessageObject> map = this.keptReplyMessageObjects.get(Long.valueOf(messageObject.getDialogId()));
        if (map == null) {
            HashMap<Long, HashMap<Integer, MessageObject>> map2 = this.keptReplyMessageObjects;
            Long lValueOf = Long.valueOf(messageObject.getDialogId());
            HashMap<Integer, MessageObject> map3 = new HashMap<>();
            map2.put(lValueOf, map3);
            map = map3;
        }
        map.put(Integer.valueOf(messageObject.getId()), messageObject);
    }

    public MessageObject findReplyMessageObject(long j, int i) {
        HashMap<Integer, MessageObject> map = this.keptReplyMessageObjects.get(Long.valueOf(j));
        if (map == null) {
            return null;
        }
        return map.get(Integer.valueOf(i));
    }

    private void clearAllKeptReplyMessages(long j) {
        this.keptReplyMessageObjects.remove(Long.valueOf(j));
    }

    private boolean isLanguageRestricted(String str) {
        return TranslatorUtils.isRestrictedLanguage(str);
    }

    public void loadTranslatingDialogsCached() {
        boolean z;
        String string = this.messagesController.getMainSettings().getString("translating_dialog_languages2", null);
        if (string == null) {
            return;
        }
        for (String str : string.split(";")) {
            String[] strArrSplit = str.split("=");
            if (strArrSplit.length >= 2) {
                long j = Long.parseLong(strArrSplit[0]);
                String[] strArrSplit2 = strArrSplit[1].split(">");
                if (strArrSplit2.length == 2) {
                    String str2 = strArrSplit2[0];
                    String strSubstring = strArrSplit2[1];
                    if (strSubstring.length() <= 0 || strSubstring.charAt(strSubstring.length() - 1) != '!') {
                        z = false;
                    } else {
                        strSubstring = strSubstring.substring(0, strSubstring.length() - 1);
                        z = true;
                    }
                    if ("null".equals(str2)) {
                        str2 = null;
                    }
                    if ("null".equals(strSubstring)) {
                        strSubstring = null;
                    }
                    if (str2 != null) {
                        this.detectedDialogLanguage.put(Long.valueOf(j), str2);
                        if (!isLanguageRestricted(str2)) {
                            this.translatingDialogs.put(j, Boolean.valueOf(true ^ z));
                            this.translatableDialogs.add(Long.valueOf(j));
                        }
                        if (strSubstring != null) {
                            this.translateDialogLanguage.put(Long.valueOf(j), strSubstring);
                        }
                    }
                }
            }
        }
        Set<String> stringSet = this.messagesController.getMainSettings().getStringSet("hidden_translation_at", null);
        if (stringSet != null) {
            Iterator<String> it = stringSet.iterator();
            while (it.hasNext()) {
                try {
                    this.hideTranslateDialogs.add(Long.valueOf(Long.parseLong(it.next())));
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    private void saveTranslatingDialogsCache() {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (int i = 0; i < this.translatingDialogs.size(); i++) {
            try {
                long jKeyAt = this.translatingDialogs.keyAt(i);
                if (!z) {
                    sb.append(";");
                }
                if (z) {
                    z = false;
                }
                String str = this.detectedDialogLanguage.get(Long.valueOf(jKeyAt));
                String str2 = "null";
                if (str == null) {
                    str = "null";
                }
                String dialogTranslateTo = getDialogTranslateTo(jKeyAt);
                if (dialogTranslateTo != null) {
                    str2 = dialogTranslateTo;
                }
                sb.append(jKeyAt);
                sb.append("=");
                sb.append(str);
                sb.append(">");
                sb.append(str2);
                if (!this.translatingDialogs.valueAt(i).booleanValue()) {
                    sb.append("!");
                }
            } catch (Exception unused) {
            }
        }
        HashSet hashSet = new HashSet();
        Iterator<Long> it = this.hideTranslateDialogs.iterator();
        while (it.hasNext()) {
            try {
                hashSet.add(_UrlKt.FRAGMENT_ENCODE_SET + it.next());
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        MessagesController.getMainSettings(this.currentAccount).edit().putString("translating_dialog_languages2", sb.toString()).putStringSet("hidden_translation_at", hashSet).apply();
    }

    private void resetTranslatingDialogsCache() {
        MessagesController.getMainSettings(this.currentAccount).edit().remove("translating_dialog_languages2").remove("hidden_translation_at").apply();
    }

    public void detectStoryLanguage(final TL_stories.StoryItem storyItem) {
        String str;
        if (storyItem == null || storyItem.detectedLng != null || (str = storyItem.caption) == null || str.length() == 0 || !LanguageDetector.hasSupport()) {
            return;
        }
        final StoryKey storyKey = new StoryKey(storyItem);
        if (this.detectingStories.contains(storyKey)) {
            return;
        }
        this.detectingStories.add(storyKey);
        LanguageDetector.detectLanguage(storyItem.caption, new LanguageDetector.StringCallback() { 
            @Override 
            public final void run(String str2) {
                this.f$0.lambda$detectStoryLanguage$34(storyItem, storyKey, str2);
            }
        }, new LanguageDetector.ExceptionCallback() { 
            @Override 
            public final void run(Exception exc) {
                this.f$0.lambda$detectStoryLanguage$36(storyItem, storyKey, exc);
            }
        });
    }

    public void lambda$detectStoryLanguage$33(TL_stories.StoryItem storyItem, String str, StoryKey storyKey) {
        storyItem.detectedLng = str;
        getMessagesController().getStoriesController().getStoriesStorage().putStoryInternal(storyItem.dialogId, storyItem);
        this.detectingStories.remove(storyKey);
    }

    public void lambda$detectStoryLanguage$35(TL_stories.StoryItem storyItem, StoryKey storyKey) {
        storyItem.detectedLng = UNKNOWN_LANGUAGE;
        getMessagesController().getStoriesController().getStoriesStorage().putStoryInternal(storyItem.dialogId, storyItem);
        this.detectingStories.remove(storyKey);
    }

    public boolean canTranslateStory(TL_stories.StoryItem storyItem) {
        if (storyItem == null || TextUtils.isEmpty(storyItem.caption) || Emoji.fullyConsistsOfEmojis(storyItem.caption)) {
            return false;
        }
        if (storyItem.detectedLng == null && storyItem.translatedText != null && TextUtils.equals(storyItem.translatedLng, TranslateAlert2.getToLanguage())) {
            return true;
        }
        String str = storyItem.detectedLng;
        return (str == null || isLanguageRestricted(str)) ? false : true;
    }

    public void translateStory(final TL_stories.StoryItem storyItem, final Runnable runnable) {
        if (storyItem == null) {
            return;
        }
        final StoryKey storyKey = new StoryKey(storyItem);
        final String strNormalizeLanguageCode = TranslatorUtils.normalizeLanguageCode(TranslateAlert2.getToLanguage());
        if (storyItem.translatedText != null && TextUtils.equals(storyItem.translatedLng, strNormalizeLanguageCode)) {
            if (runnable != null) {
                runnable.run();
            }
        } else {
            if (this.translatingStories.contains(storyKey)) {
                if (runnable != null) {
                    runnable.run();
                    return;
                }
                return;
            }
            this.translatingStories.add(storyKey);
            TLRPC.TL_messages_translateText tL_messages_translateText = new TLRPC.TL_messages_translateText();
            tL_messages_translateText.flags |= 2;
            final TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
            tL_textWithEntities.text = storyItem.caption;
            tL_textWithEntities.entities = storyItem.entities;
            tL_messages_translateText.text.add(tL_textWithEntities);
            tL_messages_translateText.to_lang = normalizeLanguage(strNormalizeLanguageCode);
            getConnectionsManager().sendRequest(tL_messages_translateText, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$translateStory$40(storyItem, strNormalizeLanguageCode, storyKey, runnable, tL_textWithEntities, tLObject, tL_error);
                }
            });
        }
    }

    public void lambda$translateStory$37(TL_stories.StoryItem storyItem, String str, StoryKey storyKey, Runnable runnable) {
        storyItem.translatedLng = str;
        storyItem.translatedText = null;
        getMessagesController().getStoriesController().getStoriesStorage().putStoryInternal(storyItem.dialogId, storyItem);
        this.translatingStories.remove(storyKey);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$detectPhotoLanguage$42(final MessageObject messageObject, final MessageKey messageKey, final Utilities.Callback callback, final String str) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$detectPhotoLanguage$41(messageObject, str, messageKey, callback);
            }
        });
    }

    public void lambda$detectPhotoLanguage$44(final MessageObject messageObject, final MessageKey messageKey, final Utilities.Callback callback, Exception exc) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$detectPhotoLanguage$43(messageObject, messageKey, callback);
            }
        });
    }

    public public boolean canTranslatePhoto(MessageObject messageObject, String str) {
        TLRPC.Message message;
        TLRPC.Message message2;
        String str2;
        if (messageObject != null && (message2 = messageObject.messageOwner) != null && (str2 = message2.originalLanguage) != null) {
            str = str2;
        }
        if (messageObject == null || (message = messageObject.messageOwner) == null || TextUtils.isEmpty(message.message)) {
            return false;
        }
        if (str != null) {
            return str != null ? false : false;
        }
        TLRPC.Message message3 = messageObject.messageOwner;
        if (message3.translatedText == null || !TextUtils.equals(message3.translatedToLanguage, TranslateAlert2.getToLanguage())) {
            if (str != null || isLanguageRestricted(messageObject.messageOwner.originalLanguage)) {
            }
        }
        return !messageObject.translated;
    }

    public void translatePhoto(final MessageObject messageObject, final Runnable runnable) {
        if (messageObject == null || messageObject.messageOwner == null) {
            return;
        }
        final MessageKey messageKey = new MessageKey(messageObject);
        final String toLanguage = TranslateAlert2.getToLanguage();
        TLRPC.Message message = messageObject.messageOwner;
        if (message.translatedText != null && TextUtils.equals(message.translatedToLanguage, toLanguage)) {
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        if (this.translatingPhotos.contains(messageKey)) {
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        this.translatingPhotos.add(messageKey);
        TLRPC.TL_messages_translateText tL_messages_translateText = new TLRPC.TL_messages_translateText();
        tL_messages_translateText.flags |= 2;
        final TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
        TLRPC.Message message2 = messageObject.messageOwner;
        tL_textWithEntities.text = message2.message;
        ArrayList<TLRPC.MessageEntity> arrayList = message2.entities;
        tL_textWithEntities.entities = arrayList;
        if (arrayList == null) {
            tL_textWithEntities.entities = new ArrayList<>();
        }
        tL_messages_translateText.text.add(tL_textWithEntities);
        tL_messages_translateText.to_lang = normalizeLanguage(toLanguage);
        final long jCurrentTimeMillis = System.currentTimeMillis();
        getConnectionsManager().sendRequest(tL_messages_translateText, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$translatePhoto$48(messageObject, toLanguage, messageKey, runnable, jCurrentTimeMillis, tL_textWithEntities, tLObject, tL_error);
            }
        });
    }

    public void lambda$translatePhoto$45(MessageObject messageObject, String str, MessageKey messageKey, Runnable runnable, long j) {
        messageObject.messageOwner.translatedToLanguage = TranslatorUtils.normalizeLanguageCode(str);
        messageObject.messageOwner.translatedText = null;
        updateMessageCustomParams(messageKey.dialogId, messageObject);
        this.translatingPhotos.remove(messageKey);
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable, Math.max(0L, 400 - (System.currentTimeMillis() - j)));
        }
    }

    public void lambda$translatePhoto$47(MessageObject messageObject, String str, MessageKey messageKey, Runnable runnable, long j) {
        messageObject.messageOwner.translatedToLanguage = TranslatorUtils.normalizeLanguageCode(str);
        messageObject.messageOwner.translatedText = null;
        updateMessageCustomParams(messageKey.dialogId, messageObject);
        this.translatingPhotos.remove(messageKey);
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable, Math.max(0L, 400 - (System.currentTimeMillis() - j)));
        }
    }

    public static String normalizeLanguage(String str) {
        if (str == null) {
            return null;
        }
        if (str.contains("_")) {
            String[] strArrSplit = str.split("_", 2);
            return strArrSplit[0].toLowerCase() + "-" + strArrSplit[1].toUpperCase();
        }
        if (!str.contains("-")) {
            return str;
        }
        String[] strArrSplit2 = str.split("-", 2);
        return strArrSplit2[0].toLowerCase() + "-" + strArrSplit2[1].toUpperCase();
    }

    public static class MessageKey {
        public long dialogId;
        public int id;

        public MessageKey(MessageObject messageObject) {
            this.dialogId = messageObject.getDialogId();
            this.id = messageObject.getId();
        }
    }

    public static class PollText extends TLObject {
        public static final int constructor = 613759672;
        public ArrayList<TLRPC.PollAnswer> answers = new ArrayList<>();
        public TLRPC.TL_textWithEntities question;
        public TLRPC.TL_textWithEntities solution;

        public static PollText TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            return (PollText) TLObject.TLdeserialize(PollText.class, 613759672 != i ? null : new PollText(), inputSerializedData, i, z);
        }

        @Override // org.telegram.tgnet.TLObject
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int int32 = inputSerializedData.readInt32(z);
            if ((int32 & 1) != 0) {
                this.question = TLRPC.TL_textWithEntities.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((int32 & 2) != 0) {
                this.answers = Vector.deserialize(inputSerializedData, new TranslateController$PollText$$ExternalSyntheticLambda0(), z);
            }
            if ((int32 & 4) != 0) {
                this.solution = TLRPC.TL_textWithEntities.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override // org.telegram.tgnet.TLObject
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(constructor);
            int i = this.question != null ? 1 : 0;
            ArrayList<TLRPC.PollAnswer> arrayList = this.answers;
            if (arrayList != null && !arrayList.isEmpty()) {
                i |= 2;
            }
            if (this.solution != null) {
                i |= 4;
            }
            outputSerializedData.writeInt32(i);
            if ((i & 1) != 0) {
                this.question.serializeToStream(outputSerializedData);
            }
            if ((i & 2) != 0) {
                Vector.serialize(outputSerializedData, this.answers);
            }
            if ((i & 4) != 0) {
                this.solution.serializeToStream(outputSerializedData);
            }
        }

        public int length() {
            TLRPC.TL_textWithEntities tL_textWithEntities = this.question;
            int length = tL_textWithEntities != null ? tL_textWithEntities.text.length() : 0;
            for (int i = 0; i < this.answers.size(); i++) {
                length += this.answers.get(i).text.text.length();
            }
            TLRPC.TL_textWithEntities tL_textWithEntities2 = this.solution;
            return tL_textWithEntities2 != null ? length + tL_textWithEntities2.text.length() : length;
        }

        public static PollText fromMessage(MessageObject messageObject) {
            TLRPC.MessageMedia media = MessageObject.getMedia(messageObject);
            if (media instanceof TLRPC.TL_messageMediaPoll) {
                return fromPoll((TLRPC.TL_messageMediaPoll) media);
            }
            return null;
        }

        public static PollText fromPoll(TLRPC.TL_messageMediaPoll tL_messageMediaPoll) {
            TLRPC.Poll poll = tL_messageMediaPoll.poll;
            PollText pollText = new PollText();
            pollText.question = poll.question;
            for (int i = 0; i < poll.answers.size(); i++) {
                TLRPC.PollAnswer pollAnswer = poll.answers.get(i);
                TLRPC.TL_pollAnswer tL_pollAnswer = new TLRPC.TL_pollAnswer();
                tL_pollAnswer.text = pollAnswer.text;
                tL_pollAnswer.option = pollAnswer.option;
                pollText.answers.add(tL_pollAnswer);
            }
            TLRPC.PollResults pollResults = tL_messageMediaPoll.results;
            if (pollResults != null && !TextUtils.isEmpty(pollResults.solution)) {
                TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
                pollText.solution = tL_textWithEntities;
                TLRPC.PollResults pollResults2 = tL_messageMediaPoll.results;
                tL_textWithEntities.text = pollResults2.solution;
                tL_textWithEntities.entities = pollResults2.solution_entities;
            }
            return pollText;
        }

        public static boolean isFullyTranslated(MessageObject messageObject, PollText pollText) {
            TLRPC.TL_messageMediaPoll tL_messageMediaPoll;
            TLRPC.Poll poll;
            TLRPC.MessageMedia media = MessageObject.getMedia(messageObject);
            if (!(media instanceof TLRPC.TL_messageMediaPoll) || (poll = (tL_messageMediaPoll = (TLRPC.TL_messageMediaPoll) media).poll) == null) {
                return true;
            }
            if ((poll.question != null) != (pollText.question != null)) {
                return false;
            }
            TLRPC.PollResults pollResults = tL_messageMediaPoll.results;
            return (pollResults != null && pollResults.solution != null) == (pollText.solution != null) && poll.answers.size() == pollText.answers.size();
        }
    }
}
