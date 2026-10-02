package com.exteragram.messenger.translator.core;

import com.exteragram.messenger.translator.TranslatorUtils;
import com.sun.jna.Callback;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.mvel2.asm.signature.SignatureVisitor;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b&\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J(\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0012H&J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0006R\u0018\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\u00020\u00068VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b¨\u0006\u0016"}, d2 = {"Lcom/exteragram/messenger/translator/core/BaseTranslator;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "supportedLanguages", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "getSupportedLanguages", "()Ljava/util/Set;", "displayName", "getDisplayName", "()Ljava/lang/String;", "translate", _UrlKt.FRAGMENT_ENCODE_SET, "text", "fromLang", "toLang", Callback.METHOD_NAME, "Lcom/exteragram/messenger/translator/TranslatorUtils$TranslateCallback;", "isLanguageSupported", _UrlKt.FRAGMENT_ENCODE_SET, "lang", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nBaseTranslator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BaseTranslator.kt\ncom/exteragram/messenger/translator/core/BaseTranslator\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,42:1\n1807#2,3:43\n*S KotlinDebug\n*F\n+ 1 BaseTranslator.kt\ncom/exteragram/messenger/translator/core/BaseTranslator\n*L\n40#1:43,3\n*E\n"})
public abstract class BaseTranslator {
    public abstract String getDisplayName();

    public abstract Set<String> getSupportedLanguages();

    public abstract void translate(String text, String fromLang, String toLang, TranslatorUtils.TranslateCallback callback);

    public final boolean isLanguageSupported(String lang) {
        if (getSupportedLanguages().isEmpty()) {
            return true;
        }
        String strPrimaryLanguageOf = TranslatorUtils.primaryLanguageOf(lang);
        if (strPrimaryLanguageOf == null) {
            return false;
        }
        Set<String> supportedLanguages = getSupportedLanguages();
        if (supportedLanguages != null && supportedLanguages.isEmpty()) {
            return false;
        }
        Iterator<String> it = supportedLanguages.iterator();
        while (it.hasNext()) {
            String s = it.next().toLowerCase(Locale.US);
            int idx = s.indexOf('-');
            String lCode = idx != -1 ? s.substring(0, idx) : s;
            if (lCode.equals(strPrimaryLanguageOf)) {
                return true;
            }
        }
        return false;
    }
}
