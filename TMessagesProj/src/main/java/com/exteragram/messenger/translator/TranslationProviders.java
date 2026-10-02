package com.exteragram.messenger.translator;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.translator.core.BaseTranslator;
import com.exteragram.messenger.translator.providers.GoogleTranslator;
import com.exteragram.messenger.translator.providers.MicrosoftTranslator;
import com.exteragram.messenger.translator.providers.TelegramTranslator;
import com.exteragram.messenger.translator.providers.YandexTranslator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\r\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0007\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0007¢\u0006\u0004\b\u0006\u0010\u0007J\u000f\u0010\t\u001a\u00020\bH\u0007¢\u0006\u0004\b\t\u0010\nJ\u000f\u0010\f\u001a\u00020\u000bH\u0007¢\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000e\u001a\u00020\u000bH\u0007¢\u0006\u0004\b\u000e\u0010\rJ\u001f\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0011H\u0007¢\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u000fH\u0007¢\u0006\u0004\b\u0013\u0010\u0015R\u001a\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\b0\u00168\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0017\u0010\u0018R\u001a\u0010\u001c\u001a\u00020\u000f8FX\u0087\u0004¢\u0006\f\u0012\u0004\b\u001b\u0010\u0003\u001a\u0004\b\u0019\u0010\u001a¨\u0006\u001d"}, d2 = {"Lcom/exteragram/messenger/translator/TranslationProviders;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "names", "()[Ljava/lang/CharSequence;", "Lcom/exteragram/messenger/translator/core/BaseTranslator;", "current", "()Lcom/exteragram/messenger/translator/core/BaseTranslator;", _UrlKt.FRAGMENT_ENCODE_SET, "isTelegram", "()Z", "isAlternative", _UrlKt.FRAGMENT_ENCODE_SET, "currentAccount", _UrlKt.FRAGMENT_ENCODE_SET, "dialogId", "isChatTranslationUnlocked", "(IJ)Z", "(I)Z", _UrlKt.FRAGMENT_ENCODE_SET, "ALL", "Ljava/util/List;", "getLastIndex", "()I", "getLastIndex$annotations", "lastIndex", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nTranslationProviders.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TranslationProviders.kt\ncom/exteragram/messenger/translator/TranslationProviders\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,62:1\n1#2:63\n*E\n"})
public final class TranslationProviders {
    public static final TranslationProviders INSTANCE = new TranslationProviders();

    @JvmField
    public static final List<BaseTranslator> ALL = java.util.Arrays.asList(TelegramTranslator.INSTANCE.getInstance(), GoogleTranslator.INSTANCE.getInstance(), YandexTranslator.INSTANCE.getInstance(), MicrosoftTranslator.INSTANCE.getInstance());

    private TranslationProviders() {
    }

    public static final int getLastIndex() {
        return ALL.size() - 1;
    }

    @JvmStatic
    public static final CharSequence[] names() {
        int size = ALL.size();
        CharSequence[] charSequenceArr = new CharSequence[size];
        for (int i = 0; i < size; i++) {
            charSequenceArr[i] = ALL.get(i).getDisplayName();
        }
        return charSequenceArr;
    }

    @JvmStatic
    public static final BaseTranslator current() {
        List<BaseTranslator> list = ALL;
        int translationProvider = ExteraConfig.getTranslationProvider();
        return (translationProvider < 0 || translationProvider >= list.size()) ? list.get(1) : list.get(translationProvider);
    }

    @JvmStatic
    public static final boolean isTelegram() {
        return current() instanceof TelegramTranslator;
    }

    @JvmStatic
    public static final boolean isAlternative() {
        return !isTelegram();
    }

    @JvmStatic
    public static final boolean isChatTranslationUnlocked(int currentAccount, long dialogId) {
        if (isChatTranslationUnlocked(currentAccount)) {
            return true;
        }
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(-dialogId));
        return chat != null && chat.autotranslation;
    }

    @JvmStatic
    public static final boolean isChatTranslationUnlocked(int currentAccount) {
        return isAlternative() || UserConfig.getInstance(currentAccount).isPremium();
    }
}
