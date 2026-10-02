package com.exteragram.messenger.plugins.ui;

import android.text.TextUtils;

import com.exteragram.messenger.plugins.PluginsConstants;
import com.exteragram.messenger.translator.ChatTranslator;
import com.exteragram.messenger.translator.TranslatorUtils;
import com.exteragram.messenger.translator.core.HttpTranslator;
import com.exteragram.messenger.translator.core.TranslationError;
import com.exteragram.messenger.translator.providers.GoogleTranslator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u001e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 :2\u00020\u0001:\u00029:B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0006J\u000e\u0010\u001d\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0006J\u0006\u0010\u001e\u001a\u00020\u001bJ\u000e\u0010\u001f\u001a\u00020\u001b2\u0006\u0010\u0015\u001a\u00020\u0013J\u0012\u0010 \u001a\u0004\u0018\u00010\n2\b\u0010!\u001a\u0004\u0018\u00010\nJ\u0014\u0010\"\u001a\u00020\u001b2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\n0$J\u0016\u0010%\u001a\u00020\u001b2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\n0$H\u0002J\u001e\u0010&\u001a\u00020\u001b2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\n0'2\u0006\u0010(\u001a\u00020\nH\u0002J:\u0010)\u001a\u00020\u001b2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\n0'2\u0006\u0010(\u001a\u00020\n2\u001a\u0010*\u001a\u0016\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\n0'\u0012\u0004\u0012\u00020\u001b0+H\u0002Je\u0010,\u001a\u00020\u001b2\u0012\u0010-\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020.0'0'2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\n0'2\u0006\u0010(\u001a\u00020\n2\u000e\u0010/\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n002\u0006\u00101\u001a\u00020\u00132\u0006\u00102\u001a\u00020.2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u001b03H\u0002¢\u0006\u0002\u00104JW\u00105\u001a\u00020\u001b2\f\u00106\u001a\b\u0012\u0004\u0012\u00020.0'2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\n0'2\u0006\u0010(\u001a\u00020\n2\u000e\u0010/\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n002\u0006\u00101\u001a\u00020\u00132\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u001b03H\u0002¢\u0006\u0002\u00107J\u0010\u00108\u001a\u00020\u001b2\u0006\u0010\u000f\u001a\u00020\u0013H\u0002R\u001e\u0010\u0004\u001a\u0012\u0012\u0004\u0012\u00020\u00060\u0005j\b\u0012\u0004\u0012\u00020\u0006`\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R*\u0010\b\u001a\u001e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\tj\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n`\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\f\u001a\u0012\u0012\u0004\u0012\u00020\n0\rj\b\u0012\u0004\u0012\u00020\n`\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u000f\u001a\u0012\u0012\u0004\u0012\u00020\n0\rj\b\u0012\u0004\u0012\u00020\n`\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u0015\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0017R\u0011\u0010\u0019\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u0017¨\u0006;"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/PluginSettingsTranslation;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "listeners", "Ljava/util/ArrayList;", "Lcom/exteragram/messenger/plugins/ui/PluginSettingsTranslation$Listener;", "Lkotlin/collections/ArrayList;", "translations", "Ljava/util/HashMap;", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/collections/HashMap;", "pending", "Ljava/util/HashSet;", "Lkotlin/collections/HashSet;", "failed", "targetLanguage", "detectedLanguage", "detecting", _UrlKt.FRAGMENT_ENCODE_SET, "value", "enabled", "getEnabled", "()Z", "isLoading", "isTranslatable", "addListener", _UrlKt.FRAGMENT_ENCODE_SET, "listener", "removeListener", "resetFailed", "setEnabled", "text", "original", "check", "texts", _UrlKt.FRAGMENT_ENCODE_SET, "detect", PluginsConstants.REQUEST, _UrlKt.FRAGMENT_ENCODE_SET, "language", "translate", "done", "Lkotlin/Function1;", "requestEach", "chunks", _UrlKt.FRAGMENT_ENCODE_SET, "results", _UrlKt.FRAGMENT_ENCODE_SET, "retry", "position", "Lkotlin/Function0;", "(Ljava/util/List;Ljava/util/List;Ljava/lang/String;[Ljava/lang/String;ZILkotlin/jvm/functions/Function0;)V", "requestChunk", "chunk", "(Ljava/util/List;Ljava/util/List;Ljava/lang/String;[Ljava/lang/String;ZLkotlin/jvm/functions/Function0;)V", "notifyListeners", "Listener", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nPluginSettingsTranslation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PluginSettingsTranslation.kt\ncom/exteragram/messenger/plugins/ui/PluginSettingsTranslation\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,243:1\n1#2:244\n777#3:245\n873#3,2:246\n1924#3,3:248\n1924#3,3:251\n1924#3,3:254\n1586#3:257\n1661#3,3:258\n1586#3:261\n1661#3,3:262\n*S KotlinDebug\n*F\n+ 1 PluginSettingsTranslation.kt\ncom/exteragram/messenger/plugins/ui/PluginSettingsTranslation\n*L\n93#1:245\n93#1:246,2\n123#1:248,3\n213#1:251,3\n190#1:254,3\n205#1:257\n205#1:258,3\n209#1:261\n209#1:262,3\n*E\n"})
public final class PluginSettingsTranslation {
    private static final int DETECTION_SAMPLE_LENGTH = 2000;
    private static final int MAX_TEXTS_PER_REQUEST = 20;
    private static final long TRANSLATION_TAG = -9223372036854775807L;
    private String detectedLanguage;
    private boolean detecting;
    private boolean enabled;
    private final HashSet<String> failed;
    private final ArrayList<Listener> listeners;
    private final HashSet<String> pending;
    private String targetLanguage;
    private final HashMap<String, String> translations;

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final HashMap<String, PluginSettingsTranslation> instances = new HashMap<>();

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\bæ\u0080\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006À\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/PluginSettingsTranslation$Listener;", _UrlKt.FRAGMENT_ENCODE_SET, "onTranslationChanged", _UrlKt.FRAGMENT_ENCODE_SET, "failed", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface Listener {
        void onTranslationChanged(boolean failed);
    }

    public /* synthetic */ PluginSettingsTranslation(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    @JvmStatic
    public static final PluginSettingsTranslation of(String str) {
        return INSTANCE.of(str);
    }

    private PluginSettingsTranslation() {
        this.listeners = new ArrayList<>();
        this.translations = new HashMap<>();
        this.pending = new HashSet<>();
        this.failed = new HashSet<>();
    }

    public final boolean getEnabled() {
        return this.enabled;
    }

    public final boolean isLoading() {
        return !this.pending.isEmpty();
    }

    public final boolean isTranslatable() {
        String str = this.detectedLanguage;
        if (str != null) {
            return !TranslatorUtils.isRestrictedLanguage(str);
        }
        return false;
    }

    public final void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public final void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public final void resetFailed() {
        this.failed.clear();
    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            this.failed.clear();
        }
        notifyListeners(false);
    }

    public final String text(String original) {
        String str;
        return (!this.enabled || original == null || (str = this.translations.get(original)) == null) ? original : str;
    }

    public final void check(Collection<String> texts) {
        String resolvedTargetLanguageCode = TranslatorUtils.getResolvedTargetLanguageCode();
        if (!Intrinsics.areEqual(resolvedTargetLanguageCode, this.targetLanguage)) {
            this.targetLanguage = resolvedTargetLanguageCode;
            this.translations.clear();
            this.failed.clear();
        }
        if (this.detectedLanguage == null && !this.detecting && !texts.isEmpty() && LanguageDetector.hasSupport()) {
            detect(texts);
        }
        if (this.enabled) {
            ArrayList arrayList = new ArrayList();
            for (Object obj : texts) {
                String str = (String) obj;
                if (!StringsKt.isBlank(str) && !this.translations.containsKey(str) && !this.pending.contains(str) && !this.failed.contains(str)) {
                    arrayList.add(obj);
                }
            }
            if (arrayList.isEmpty()) {
                return;
            }
            request(arrayList, resolvedTargetLanguageCode);
        }
    }

    private final void detect(Collection<String> texts) {
        String sample = TextUtils.join("\n", texts);
        if (sample.length() > 2000) {
            sample = sample.substring(0, 2000);
        }
        LanguageDetector.detectLanguage(sample, new LanguageDetector.StringCallback() { // from class: com.exteragram.messenger.plugins.ui.PluginSettingsTranslation$$ExternalSyntheticLambda0
            @Override // org.telegram.messenger.LanguageDetector.StringCallback
            public final void run(String str) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.ui.PluginSettingsTranslation$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        PluginSettingsTranslation.$r8$lambda$PTUdpZwtDYtdYwXecXf41YtewTI(str, PluginSettingsTranslation.this);
                    }
                });
            }
        }, null);
    }

    public static void $r8$lambda$PTUdpZwtDYtdYwXecXf41YtewTI(String str, PluginSettingsTranslation pluginSettingsTranslation) {
        if (str == null || str.length() == 0 || Intrinsics.areEqual(str, TranslateController.UNKNOWN_LANGUAGE)) {
            return;
        }
        pluginSettingsTranslation.detectedLanguage = str;
        pluginSettingsTranslation.notifyListeners(false);
    }

    private final void request(final List<String> texts, final String language) {
        this.pending.addAll(texts);
        translate(texts, language, new Function1() { // from class: com.exteragram.messenger.plugins.ui.PluginSettingsTranslation$$ExternalSyntheticLambda5
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return PluginSettingsTranslation.m1605$r8$lambda$GmVCDGYKoJLxcaC_JAKQEYoydA(PluginSettingsTranslation.this, texts, language, (List) obj);
            }
        });
    }

    /* JADX INFO: renamed from: $r8$lambda$GmVCDGY-KoJLxcaC_JAKQEYoydA, reason: not valid java name */
    public static Unit m1605$r8$lambda$GmVCDGYKoJLxcaC_JAKQEYoydA(PluginSettingsTranslation pluginSettingsTranslation, List list, String str, List list2) {
        List list3 = list;
        pluginSettingsTranslation.pending.removeAll(CollectionsKt.toSet(list3));
        if (!Intrinsics.areEqual(str, pluginSettingsTranslation.targetLanguage)) {
            pluginSettingsTranslation.notifyListeners(false);
            return Unit.INSTANCE;
        }
        Iterator it = list3.iterator();
        boolean z = false;
        int i = 0;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object next = it.next();
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            String str2 = (String) next;
            String str3 = (String) CollectionsKt.getOrNull(list2, i);
            if (str3 == null || StringsKt.isBlank(str3)) {
                pluginSettingsTranslation.failed.add(str2);
                z = true;
            } else {
                pluginSettingsTranslation.translations.put(str2, str3);
            }
            i = i2;
        }
        boolean z2 = z && pluginSettingsTranslation.translations.isEmpty();
        if (z2) {
            pluginSettingsTranslation.enabled = false;
        }
        pluginSettingsTranslation.notifyListeners(z2);
        return Unit.INSTANCE;
    }

    private final void translate(List<String> texts, String language, final Function1<? super List<String>, Unit> done) {
        if (TranslatorUtils.getCurrentTranslator() instanceof HttpTranslator) {
            ChatTranslator.translate(TRANSLATION_TAG, texts, language, new ChatTranslator.Callback() { // from class: com.exteragram.messenger.plugins.ui.PluginSettingsTranslation$$ExternalSyntheticLambda6
                @Override // com.exteragram.messenger.translator.ChatTranslator.Callback
                public final void onResult(List list, TranslationError translationError) {
                    done.invoke((List<String>) list);
                }
            });
        } else {
            final String[] strArr = new String[texts.size()];
            requestEach(CollectionsKt.chunked(CollectionsKt.getIndices(texts), 20), texts, language, strArr, true, 0, new Function0() { // from class: com.exteragram.messenger.plugins.ui.PluginSettingsTranslation$$ExternalSyntheticLambda7
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return PluginSettingsTranslation.$r8$lambda$vM607UZKvUJyPyzilOIBKU_9N4g(done, strArr);
                }
            });
        }
    }

    public static Unit $r8$lambda$vM607UZKvUJyPyzilOIBKU_9N4g(Function1 function1, String[] strArr) {
        function1.invoke(ArraysKt.toList(strArr));
        return Unit.INSTANCE;
    }

    private final void requestEach(final List<? extends List<Integer>> chunks, final List<String> texts, final String language, final String[] results, final boolean retry, final int position, final Function0<Unit> done) {
        if (position == chunks.size()) {
            done.invoke();
        } else {
            requestChunk(chunks.get(position), texts, language, results, retry, new Function0() { // from class: com.exteragram.messenger.plugins.ui.PluginSettingsTranslation$$ExternalSyntheticLambda4
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return PluginSettingsTranslation.$r8$lambda$DNIlH7lxxikJOF6XvGqwVcxihjI(PluginSettingsTranslation.this, chunks, texts, language, results, retry, position, done);
                }
            });
        }
    }

    public static Unit $r8$lambda$DNIlH7lxxikJOF6XvGqwVcxihjI(PluginSettingsTranslation pluginSettingsTranslation, List list, List list2, String str, String[] strArr, boolean z, int i, Function0 function0) {
        pluginSettingsTranslation.requestEach(list, list2, str, strArr, z, i + 1, function0);
        return Unit.INSTANCE;
    }

    private final void requestChunk(final List<Integer> chunk, final List<String> texts, final String language, final String[] results, final boolean retry, final Function0<Unit> done) {
        TLRPC.TL_messages_translateText tL_messages_translateText = new TLRPC.TL_messages_translateText();
        tL_messages_translateText.flags |= 2;
        Iterator<Integer> it = chunk.iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            ArrayList<TLRPC.TL_textWithEntities> arrayList = tL_messages_translateText.text;
            TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
            tL_textWithEntities.text = texts.get(iIntValue);
            arrayList.add(tL_textWithEntities);
        }
        tL_messages_translateText.to_lang = language;
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_messages_translateText, new RequestDelegate() { // from class: com.exteragram.messenger.plugins.ui.PluginSettingsTranslation$$ExternalSyntheticLambda1
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.ui.PluginSettingsTranslation$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        PluginSettingsTranslation.$r8$lambda$3KJzwSQq1gHWVma1Zl0vBkVJAcM(tLObject, chunk, retry, done, PluginSettingsTranslation.this, texts, language, results, tL_error);
                    }
                });
            }
        });
    }

    public static void $r8$lambda$3KJzwSQq1gHWVma1Zl0vBkVJAcM(TLObject tLObject, final List list, boolean z, final Function0 function0, PluginSettingsTranslation pluginSettingsTranslation, List list2, String str, final String[] strArr, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.TL_messages_translateResult)) {
            if (tL_error == null || !StringsKt.equals("TRANSLATIONS_DISABLED_ALT", tL_error.text, true)) {
                function0.invoke();
                return;
            }
            List list3 = list;
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list3, 10));
            Iterator it = list3.iterator();
            while (it.hasNext()) {
                arrayList.add((String) list2.get(((Number) it.next()).intValue()));
            }
            ChatTranslator.translate(TRANSLATION_TAG, arrayList, str, GoogleTranslator.INSTANCE.getInstance(), new ChatTranslator.Callback() { // from class: com.exteragram.messenger.plugins.ui.PluginSettingsTranslation$$ExternalSyntheticLambda3
                @Override // com.exteragram.messenger.translator.ChatTranslator.Callback
                public final void onResult(List list4, TranslationError translationError) {
                    PluginSettingsTranslation.$r8$lambda$BhislwJ_w_bX3GhCIz9I8vwOc8g(list, function0, strArr, list4, translationError);
                }
            });
            return;
        }
        ArrayList arrayList2 = new ArrayList();
        TLRPC.TL_messages_translateResult tL_messages_translateResult = (TLRPC.TL_messages_translateResult) tLObject;
        int i = 0;
        if (tL_messages_translateResult.result.size() == list.size()) {
            int i2 = 0;
            for (Object obj : list) {
                int i3 = i2 + 1;
                if (i2 < 0) {
                    CollectionsKt.throwIndexOverflow();
                }
                int iIntValue = ((Number) obj).intValue();
                TLRPC.TL_textWithEntities tL_textWithEntities = tL_messages_translateResult.result.get(i2);
                String str2 = tL_textWithEntities != null ? tL_textWithEntities.text : null;
                if (str2 == null || StringsKt.isBlank(str2)) {
                    arrayList2.add(Integer.valueOf(iIntValue));
                } else {
                    strArr[iIntValue] = str2;
                }
                i2 = i3;
            }
        } else {
            arrayList2.addAll(list);
        }
        if (!z || arrayList2.isEmpty()) {
            function0.invoke();
            return;
        }
        ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(arrayList2, 10));
        int size = arrayList2.size();
        while (i < size) {
            Object obj2 = arrayList2.get(i);
            i++;
            arrayList3.add(CollectionsKt.listOf(Integer.valueOf(((Number) obj2).intValue())));
        }
        pluginSettingsTranslation.requestEach(arrayList3, list2, str, strArr, false, 0, function0);
    }

    public static void $r8$lambda$BhislwJ_w_bX3GhCIz9I8vwOc8g(List list, Function0 function0, String[] strArr, List list2, TranslationError translationError) {
        int i = 0;
        for (Object obj : list) {
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            strArr[((Number) obj).intValue()] = (String) CollectionsKt.getOrNull(list2, i);
            i = i2;
        }
        function0.invoke();
    }

    private final void notifyListeners(boolean failed) {
        Iterator it = new ArrayList(this.listeners).iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onTranslationChanged(failed);
        }
    }

    @Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u000bH\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R*\u0010\t\u001a\u001e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\nj\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f`\rX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/PluginSettingsTranslation$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "TRANSLATION_TAG", _UrlKt.FRAGMENT_ENCODE_SET, "MAX_TEXTS_PER_REQUEST", _UrlKt.FRAGMENT_ENCODE_SET, "DETECTION_SAMPLE_LENGTH", "instances", "Ljava/util/HashMap;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/ui/PluginSettingsTranslation;", "Lkotlin/collections/HashMap;", "of", "pluginId", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    @SourceDebugExtension({"SMAP\nPluginSettingsTranslation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PluginSettingsTranslation.kt\ncom/exteragram/messenger/plugins/ui/PluginSettingsTranslation$Companion\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,243:1\n410#2,7:244\n*S KotlinDebug\n*F\n+ 1 PluginSettingsTranslation.kt\ncom/exteragram/messenger/plugins/ui/PluginSettingsTranslation$Companion\n*L\n240#1:244,7\n*E\n"})
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final PluginSettingsTranslation of(String pluginId) {
            HashMap map = PluginSettingsTranslation.instances;
            Object pluginSettingsTranslation = map.get(pluginId);
            if (pluginSettingsTranslation == null) {
                pluginSettingsTranslation = new PluginSettingsTranslation(null);
                map.put(pluginId, pluginSettingsTranslation);
            }
            return (PluginSettingsTranslation) pluginSettingsTranslation;
        }
    }
}
