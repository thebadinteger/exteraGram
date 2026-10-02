package com.exteragram.messenger.translator;

import com.exteragram.messenger.translator.core.BaseTranslator;
import com.exteragram.messenger.translator.core.HttpTranslator;
import com.exteragram.messenger.translator.core.ProviderLimits;
import com.exteragram.messenger.translator.core.TranslationDispatcher;
import com.exteragram.messenger.translator.core.TranslationError;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000_\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\b\u0006*\u0001)\bÆ\u0002\u0018\u00002\u00020\u0001:\u0002,-B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J5\u0010\r\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\nH\u0007¢\u0006\u0004\b\r\u0010\u000eJ=\u0010\r\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u000b\u001a\u00020\nH\u0007¢\u0006\u0004\b\r\u0010\u0011J\u0017\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u0004H\u0007¢\u0006\u0004\b\u0012\u0010\u0013J\u000f\u0010\u0014\u001a\u00020\fH\u0007¢\u0006\u0004\b\u0014\u0010\u0003J\u000f\u0010\u0015\u001a\u00020\fH\u0007¢\u0006\u0004\b\u0015\u0010\u0003JK\u0010\u001c\u001a\u00020\f2\u0006\u0010\u0017\u001a\u00020\u00162\u0006\u0010\t\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00180\u00062\u000e\u0010\u001b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u001aH\u0002¢\u0006\u0004\b\u001c\u0010\u001dJ%\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u001e\u001a\u00020\u00072\u0006\u0010 \u001a\u00020\u001fH\u0002¢\u0006\u0004\b!\u0010\"J1\u0010%\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u00060\u00062\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00180\u00062\u0006\u0010$\u001a\u00020#H\u0002¢\u0006\u0004\b%\u0010&J'\u0010'\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u0007H\u0002¢\u0006\u0004\b'\u0010(R\u0014\u0010*\u001a\u00020)8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b*\u0010+¨\u0006."}, d2 = {"Lcom/exteragram/messenger/translator/ChatTranslator;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", _UrlKt.FRAGMENT_ENCODE_SET, "dialogId", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "texts", "toLang", "Lcom/exteragram/messenger/translator/ChatTranslator$Callback;", com.sun.jna.Callback.METHOD_NAME, _UrlKt.FRAGMENT_ENCODE_SET, "translate", "(JLjava/util/List;Ljava/lang/String;Lcom/exteragram/messenger/translator/ChatTranslator$Callback;)V", "Lcom/exteragram/messenger/translator/core/BaseTranslator;", "current", "(JLjava/util/List;Ljava/lang/String;Lcom/exteragram/messenger/translator/core/BaseTranslator;Lcom/exteragram/messenger/translator/ChatTranslator$Callback;)V", "cancel", "(J)V", "cancelAll", "clearCache", "Lcom/exteragram/messenger/translator/core/HttpTranslator;", "translator", "Lcom/exteragram/messenger/translator/ChatTranslator$Piece;", "pieces", _UrlKt.FRAGMENT_ENCODE_SET, "results", "assemble", "(Lcom/exteragram/messenger/translator/core/HttpTranslator;Ljava/lang/String;Ljava/util/List;Ljava/util/List;[Ljava/lang/String;)V", "text", _UrlKt.FRAGMENT_ENCODE_SET, "limit", "split", "(Ljava/lang/String;I)Ljava/util/List;", "Lcom/exteragram/messenger/translator/core/ProviderLimits;", "limits", "chunk", "(Ljava/util/List;Lcom/exteragram/messenger/translator/core/ProviderLimits;)Ljava/util/List;", "cacheKey", "(Lcom/exteragram/messenger/translator/core/BaseTranslator;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", "com/exteragram/messenger/translator/ChatTranslator$cache$1", "cache", "Lcom/exteragram/messenger/translator/ChatTranslator$cache$1;", "Callback", "Piece", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nChatTranslator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ChatTranslator.kt\ncom/exteragram/messenger/translator/ChatTranslator\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,198:1\n1#2:199\n1586#3:200\n1661#3,3:201\n*S KotlinDebug\n*F\n+ 1 ChatTranslator.kt\ncom/exteragram/messenger/translator/ChatTranslator\n*L\n85#1:200\n85#1:201,3\n*E\n"})
public final class ChatTranslator {
    public static final ChatTranslator INSTANCE = new ChatTranslator();
    private static final ChatTranslator$cache$1 cache = new ChatTranslator$cache$1();

    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\bæ\u0080\u0001\u0018\u00002\u00020\u0001J\"\u0010\u0002\u001a\u00020\u00032\u000e\u0010\u0004\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u00052\b\u0010\u0007\u001a\u0004\u0018\u00010\bH&¨\u0006\tÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/translator/ChatTranslator$Callback;", _UrlKt.FRAGMENT_ENCODE_SET, "onResult", _UrlKt.FRAGMENT_ENCODE_SET, "results", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "error", "Lcom/exteragram/messenger/translator/core/TranslationError;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface Callback {
        void onResult(List<String> results, TranslationError error);
    }

    private ChatTranslator() {
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001c\u0010\f\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000b\"\u0004\b\u000e\u0010\u000f¨\u0006\u0010"}, d2 = {"Lcom/exteragram/messenger/translator/ChatTranslator$Piece;", _UrlKt.FRAGMENT_ENCODE_SET, "messageIndex", _UrlKt.FRAGMENT_ENCODE_SET, "text", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(ILjava/lang/String;)V", "getMessageIndex", "()I", "getText", "()Ljava/lang/String;", "translated", "getTranslated", "setTranslated", "(Ljava/lang/String;)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Piece {
        private final int messageIndex;
        private final String text;
        private String translated;

        public Piece(int i, String str) {
            this.messageIndex = i;
            this.text = str;
        }

        public final int getMessageIndex() {
            return this.messageIndex;
        }

        public final String getText() {
            return this.text;
        }

        public final String getTranslated() {
            return this.translated;
        }

        public final void setTranslated(String str) {
            this.translated = str;
        }
    }

    @JvmStatic
    public static final void translate(long dialogId, List<String> texts, String toLang, Callback callback) {
        translate(dialogId, texts, toLang, TranslatorUtils.getCurrentTranslator(), callback);
    }

    @JvmStatic
    public static final void translate(long dialogId, List<String> texts, final String toLang, BaseTranslator current, Callback callback) {
        ChatTranslator chatTranslator;
        String str;
        final List<String> list = texts;
        final Callback callback2 = callback;
        if (!(current instanceof HttpTranslator)) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.translator.ChatTranslator$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    callback2.onResult(ArraysKt.toList(new String[list.size()]), TranslationError.Fatal.INSTANCE);
                }
            });
            return;
        }
        HttpTranslator httpTranslator = (HttpTranslator) current;
        ProviderLimits limits = httpTranslator.getLimits();
        final String[] strArr = new String[list.size()];
        final ArrayList arrayList = new ArrayList();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String str2 = list.get(i);
            if (StringsKt.isBlank(str2)) {
                strArr[i] = str2;
            } else {
                ChatTranslator$cache$1 chatTranslator$cache$1 = cache;
                synchronized (chatTranslator$cache$1) {
                    chatTranslator = INSTANCE;
                    str = (String) chatTranslator$cache$1.get((Object) chatTranslator.cacheKey(httpTranslator, toLang, str2));
                }
                if (str != null) {
                    strArr[i] = str;
                } else {
                    Iterator<String> it = chatTranslator.split(str2, limits.getMaxCharsPerText()).iterator();
                    while (it.hasNext()) {
                        arrayList.add(new Piece(i, it.next()));
                    }
                }
            }
        }
        if (arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.translator.ChatTranslator$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    callback2.onResult(ArraysKt.toList(strArr), null);
                }
            });
            return;
        }
        List<List<Piece>> listChunk = INSTANCE.chunk(arrayList, limits);
        final Ref.IntRef intRef = new Ref.IntRef();
        intRef.element = listChunk.size();
        final Ref.ObjectRef objectRef = new Ref.ObjectRef();
        for (final List<Piece> list2 : listChunk) {
            List<Piece> list3 = list2;
            ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list3, 10));
            Iterator<Piece> it2 = list3.iterator();
            while (it2.hasNext()) {
                arrayList2.add(((Piece) it2.next()).getText());
            }
            final HttpTranslator httpTranslator2 = httpTranslator;
            httpTranslator = httpTranslator2;
            TranslationDispatcher.enqueue$default(httpTranslator, dialogId, arrayList2, "auto", toLang, false, new TranslationDispatcher.Completion() { // from class: com.exteragram.messenger.translator.ChatTranslator$$ExternalSyntheticLambda2
                @Override // com.exteragram.messenger.translator.core.TranslationDispatcher.Completion
                public final void onDone(List list4, TranslationError translationError) {
                    ChatTranslator.m1754$r8$lambda$mytC8HTQw0Wntx8e7mXkUSUWpY(list2, objectRef, intRef, httpTranslator2, toLang, list, arrayList, strArr, callback2, list4, translationError);
                }
            }, 32, null);
            
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v2, types: [com.exteragram.messenger.translator.core.TranslationError$Transient] */
    /* JADX INFO: renamed from: $r8$lambda$mytC8HTQw-0Wntx8e7mXkUSUWpY, reason: not valid java name */
    public static void m1754$r8$lambda$mytC8HTQw0Wntx8e7mXkUSUWpY(List list, Ref.ObjectRef objectRef, Ref.IntRef intRef, HttpTranslator httpTranslator, String str, List list2, ArrayList arrayList, String[] strArr, Callback callback, List list3, TranslationError translationError) {
        if (list3 != null && list3.size() == list.size()) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                ((Piece) list.get(i)).setTranslated((String) list3.get(i));
            }
        } else if ((translationError instanceof TranslationError.Cancelled) || objectRef.element == null) {
            if (translationError == null) {
                translationError = TranslationError.Transient.INSTANCE;
            }
            objectRef.element = translationError;
        }
        int i2 = intRef.element - 1;
        intRef.element = i2;
        if (i2 == 0) {
            INSTANCE.assemble(httpTranslator, str, list2, arrayList, strArr);
            callback.onResult(ArraysKt.toList(strArr), (TranslationError) objectRef.element);
        }
    }

    @JvmStatic
    public static final void cancel(long dialogId) {
        TranslationDispatcher.cancel(dialogId);
    }

    @JvmStatic
    public static final void cancelAll() {
        TranslationDispatcher.cancelAll();
    }

    @JvmStatic
    public static final void clearCache() {
        ChatTranslator$cache$1 chatTranslator$cache$1 = cache;
        synchronized (chatTranslator$cache$1) {
            chatTranslator$cache$1.clear();
            Unit unit = Unit.INSTANCE;
        }
    }

    private final void assemble(HttpTranslator translator, String toLang, List<String> texts, List<Piece> pieces, String[] results) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < pieces.size()) {
            int messageIndex = pieces.get(i).getMessageIndex();
            boolean z = true;
            int i2 = i;
            while (i2 < pieces.size() && pieces.get(i2).getMessageIndex() == messageIndex) {
                if (pieces.get(i2).getTranslated() == null) {
                    z = false;
                }
                i2++;
            }
            if (z) {
                sb.setLength(0);
                while (i < i2) {
                    sb.append(pieces.get(i).getTranslated());
                    i++;
                }
                String string = sb.toString();
                results[messageIndex] = string;
                ChatTranslator$cache$1 chatTranslator$cache$1 = cache;
                synchronized (chatTranslator$cache$1) {
                    chatTranslator$cache$1.put(INSTANCE.cacheKey(translator, toLang, texts.get(messageIndex)), string);
                    Unit unit = Unit.INSTANCE;
                }
            }
            i = i2;
        }
    }

    private final List<String> split(String text, int limit) {
        if (text.length() <= limit) {
            return CollectionsKt.listOf(text);
        }
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < text.length()) {
            if (text.length() - i <= limit) {
                arrayList.add(text.substring(i));
                return arrayList;
            }
            int i2 = i + limit;
            int i3 = i2 - 1;
            String str = text;
            int iLastIndexOf$default = str.lastIndexOf('\n', i3);
            if (iLastIndexOf$default <= i) {
                iLastIndexOf$default = str.lastIndexOf(' ', i3);
            }
            int i4 = iLastIndexOf$default <= i ? i2 : iLastIndexOf$default + 1;
            arrayList.add(str.substring(i, i4));
            i = i4;
            text = str;
        }
        return arrayList;
    }

    private final List<List<Piece>> chunk(List<Piece> pieces, ProviderLimits limits) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int length = 0;
        for (Piece piece : pieces) {
            if (!arrayList2.isEmpty() && (arrayList2.size() >= limits.getMaxTextsPerRequest() || piece.getText().length() + length > limits.getMaxCharsPerRequest())) {
                arrayList.add(arrayList2);
                arrayList2 = new ArrayList();
                length = 0;
            }
            arrayList2.add(piece);
            length += piece.getText().length();
        }
        if (!arrayList2.isEmpty()) {
            arrayList.add(arrayList2);
        }
        return arrayList;
    }

    private final String cacheKey(BaseTranslator translator, String toLang, String text) {
        return translator.getClass().getSimpleName() + " " + toLang + " " + text;
    }
}
