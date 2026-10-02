package com.exteragram.messenger.translator;

import android.os.SystemClock;
import com.exteragram.messenger.translator.core.TranslationError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.TranslateAlert2;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003Jq\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\b2$\u0010\u000f\u001a \u0012\u001c\u0012\u001a\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000e0\r0\b2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u0011H\u0007¢\u0006\u0004\b\u0014\u0010\u0015JC\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0005\u001a\u00020\u00042\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\b2\u0006\u0010\u0010\u001a\u00020\u000e2\u0014\u0010\u0017\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\b0\u0016H\u0007¢\u0006\u0004\b\u0018\u0010\u0019JQ\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u0005\u001a\u00020\u00042\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\b2\u0006\u0010\u0010\u001a\u00020\u000e2\"\u0010\u0017\u001a\u001e\u0012\u000e\u0012\f\u0012\u0006\u0012\u0004\u0018\u00010\u000b\u0018\u00010\b\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00130\u001aH\u0002¢\u0006\u0004\b\u001b\u0010\u001cJ\u001f\u0010\u001f\u001a\u00020\u00132\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u001dH\u0002¢\u0006\u0004\b\u001f\u0010 R0\u0010#\u001a\u001e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040!j\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u0004`\"8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b#\u0010$¨\u0006%"}, d2 = {"Lcom/exteragram/messenger/translator/ChatTranslationBridge;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", _UrlKt.FRAGMENT_ENCODE_SET, "dialogId", _UrlKt.FRAGMENT_ENCODE_SET, "isTranscription", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "ids", "Lorg/telegram/tgnet/TLRPC$TL_textWithEntities;", "sources", "Lorg/telegram/messenger/Utilities$Callback4;", _UrlKt.FRAGMENT_ENCODE_SET, "callbacks", "toLang", "Ljava/lang/Runnable;", "onFinished", _UrlKt.FRAGMENT_ENCODE_SET, "translateMessages", "(JZLjava/util/List;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/Runnable;)V", "Lorg/telegram/messenger/Utilities$Callback;", "onDone", "translateTexts", "(JLjava/util/List;Ljava/lang/String;Lorg/telegram/messenger/Utilities$Callback;)V", "Lkotlin/Function2;", "translate", "(JLjava/util/List;Ljava/lang/String;Lkotlin/jvm/functions/Function2;)V", "Lcom/exteragram/messenger/translator/core/TranslationError;", "error", "notifyFailure", "(JLcom/exteragram/messenger/translator/core/TranslationError;)V", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "lastFailureNotice", "Ljava/util/HashMap;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nChatTranslationBridge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ChatTranslationBridge.kt\ncom/exteragram/messenger/translator/ChatTranslationBridge\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,102:1\n1586#2:103\n1661#2,3:104\n1#3:107\n*S KotlinDebug\n*F\n+ 1 ChatTranslationBridge.kt\ncom/exteragram/messenger/translator/ChatTranslationBridge\n*L\n70#1:103\n70#1:104,3\n*E\n"})
public final class ChatTranslationBridge {
    public static final ChatTranslationBridge INSTANCE = new ChatTranslationBridge();
    private static final HashMap<Long, Long> lastFailureNotice = new HashMap<>();

    private ChatTranslationBridge() {
    }

    @JvmStatic
    public static final void translateMessages(long dialogId, final boolean isTranscription, final List<Integer> ids, List<? extends TLRPC.TL_textWithEntities> sources, final List<? extends Utilities.Callback4<Boolean, Integer, TLRPC.TL_textWithEntities, String>> callbacks, final String toLang, final Runnable onFinished) {
        INSTANCE.translate(dialogId, sources, toLang, new Function2() { // from class: com.exteragram.messenger.translator.ChatTranslationBridge$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(Object obj, Object obj2) {
                return ChatTranslationBridge.$r8$lambda$n4IWVuXo_sYKN8VFSAsNKZlNhQ4(callbacks, isTranscription, ids, toLang, onFinished, (List) obj, ((Boolean) obj2).booleanValue());
            }
        });
    }

    public static Unit $r8$lambda$n4IWVuXo_sYKN8VFSAsNKZlNhQ4(List list, boolean z, List list2, String str, Runnable runnable, List list3, boolean z2) {
        if (!z2) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                ((Utilities.Callback4) list.get(i)).run(Boolean.valueOf(z), list2.get(i), list3 != null ? (TLRPC.TL_textWithEntities) CollectionsKt.getOrNull(list3, i) : null, str);
            }
        }
        runnable.run();
        return Unit.INSTANCE;
    }

    @JvmStatic
    public static final void translateTexts(long dialogId, final List<? extends TLRPC.TL_textWithEntities> sources, String toLang, final Utilities.Callback<List<TLRPC.TL_textWithEntities>> onDone) {
        INSTANCE.translate(dialogId, sources, toLang, new Function2() { // from class: com.exteragram.messenger.translator.ChatTranslationBridge$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(Object obj, Object obj2) {
                return ChatTranslationBridge.$r8$lambda$29bWt5csaXfFme9ETH_dYptwKc4(onDone, sources, (List) obj, ((Boolean) obj2).booleanValue());
            }
        });
    }

    public static Unit $r8$lambda$29bWt5csaXfFme9ETH_dYptwKc4(Utilities.Callback callback, List list, List list2, boolean z) {
        List listFilterNotNull;
        List list3 = null;
        if (list2 != null && (listFilterNotNull = CollectionsKt.filterNotNull(list2)) != null && listFilterNotNull.size() == list.size()) {
            list3 = listFilterNotNull;
        }
        callback.run(list3);
        return Unit.INSTANCE;
    }

    private final void translate(final long dialogId, final List<? extends TLRPC.TL_textWithEntities> sources, String toLang, final Function2<? super List<? extends TLRPC.TL_textWithEntities>, ? super Boolean, Unit> onDone) {
        List<? extends TLRPC.TL_textWithEntities> list = sources;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<? extends TLRPC.TL_textWithEntities> it = list.iterator();
        while (it.hasNext()) {
            String str = ((TLRPC.TL_textWithEntities) it.next()).text;
            if (str == null) {
                str = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            arrayList.add(str);
        }
        ChatTranslator.translate(dialogId, arrayList, toLang, new ChatTranslator.Callback() { // from class: com.exteragram.messenger.translator.ChatTranslationBridge$$ExternalSyntheticLambda2
            @Override // com.exteragram.messenger.translator.ChatTranslator.Callback
            public final void onResult(List list2, TranslationError translationError) {
                ChatTranslationBridge.$r8$lambda$qFx9JU0PIK7NSKHEqlnh55oJOqM(sources, onDone, dialogId, list2, translationError);
            }
        });
    }

    public static void $r8$lambda$qFx9JU0PIK7NSKHEqlnh55oJOqM(List list, Function2 function2, long j, List list2, TranslationError translationError) {
        TLRPC.TL_textWithEntities tL_textWithEntitiesPreprocess;
        boolean z = translationError instanceof TranslationError.Cancelled;
        ArrayList arrayList = null;
        if (!z) {
            int size = list.size();
            ArrayList arrayList2 = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                String str = (String) CollectionsKt.getOrNull(list2, i);
                if (str != null) {
                    TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
                    tL_textWithEntities.text = str;
                    tL_textWithEntitiesPreprocess = TranslateAlert2.preprocess((TLRPC.TL_textWithEntities) list.get(i), tL_textWithEntities);
                } else {
                    tL_textWithEntitiesPreprocess = null;
                }
                arrayList2.add(tL_textWithEntitiesPreprocess);
            }
            arrayList = arrayList2;
        }
        function2.invoke(arrayList, Boolean.valueOf(z));
        if (translationError == null || z) {
            return;
        }
        INSTANCE.notifyFailure(j, translationError);
    }

    private final void notifyFailure(long dialogId, TranslationError error) {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        HashMap<Long, Long> map = lastFailureNotice;
        Long l = map.get(Long.valueOf(dialogId));
        if (l == null || jElapsedRealtime - l.longValue() >= 60000) {
            map.put(Long.valueOf(dialogId), Long.valueOf(jElapsedRealtime));
            NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.showBulletin, 1, LocaleController.getString(error instanceof TranslationError.RateLimited ? R.string.TranslationFailedAlert1 : R.string.TranslationFailedAlert2));
        }
    }
}
