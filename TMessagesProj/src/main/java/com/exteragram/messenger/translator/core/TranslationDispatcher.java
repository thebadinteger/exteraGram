package com.exteragram.messenger.translator.core;

import android.os.SystemClock;
import com.exteragram.messenger.plugins.PluginsConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import kotlin.LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001:\u0003/01B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0002JH\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00070\u00122\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010\u0014\u001a\u00020\u00072\b\b\u0002\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0007J\u0010\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0007J\b\u0010\u001a\u001a\u00020\u000eH\u0007J\u001c\u0010\u001b\u001a\u00020\u000e2\u0012\u0010\u001c\u001a\u000e\u0012\u0004\u0012\u00020\u001e\u0012\u0004\u0012\u00020\u00160\u001dH\u0002J\u0010\u0010\u001f\u001a\u00020\u000e2\u0006\u0010 \u001a\u00020\bH\u0002J\u0012\u0010!\u001a\u0004\u0018\u00010\u001e2\u0006\u0010 \u001a\u00020\bH\u0002J\u0018\u0010\"\u001a\u00020\u000e2\u0006\u0010 \u001a\u00020\b2\u0006\u0010#\u001a\u00020\u0010H\u0002J\u0018\u0010$\u001a\u00020\u000e2\u0006\u0010 \u001a\u00020\b2\u0006\u0010%\u001a\u00020\u001eH\u0002J \u0010&\u001a\u00020\u000e2\u0006\u0010 \u001a\u00020\b2\u0006\u0010%\u001a\u00020\u001e2\u0006\u0010'\u001a\u00020(H\u0002J\u0018\u0010)\u001a\u00020\u00102\u0006\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020-H\u0002J2\u0010.\u001a\u00020\u000e2\u0006\u0010 \u001a\u00020\b2\u0006\u0010%\u001a\u00020\u001e2\u000e\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u00122\b\u0010'\u001a\u0004\u0018\u00010(H\u0002R\u000e\u0010\u0004\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R*\u0010\u0005\u001a\u001e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006j\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b`\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u00062"}, d2 = {"Lcom/exteragram/messenger/translator/core/TranslationDispatcher;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "lock", "gates", "Ljava/util/HashMap;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/translator/core/TranslationDispatcher$Gate;", "Lkotlin/collections/HashMap;", "gateOf", "translator", "Lcom/exteragram/messenger/translator/core/HttpTranslator;", "enqueue", _UrlKt.FRAGMENT_ENCODE_SET, "tag", _UrlKt.FRAGMENT_ENCODE_SET, "texts", _UrlKt.FRAGMENT_ENCODE_SET, "fromLang", "toLang", PluginsConstants.MenuItemProperties.PRIORITY, _UrlKt.FRAGMENT_ENCODE_SET, "completion", "Lcom/exteragram/messenger/translator/core/TranslationDispatcher$Completion;", "cancel", "cancelAll", "cancelMatching", "matches", "Lkotlin/Function1;", "Lcom/exteragram/messenger/translator/core/TranslationDispatcher$Job;", "pump", "gate", "nextJob", "schedulePump", "delay", "start", "job", "retryOrFail", "error", "Lcom/exteragram/messenger/translator/core/TranslationError;", "backoff", "limits", "Lcom/exteragram/messenger/translator/core/ProviderLimits;", "attempt", _UrlKt.FRAGMENT_ENCODE_SET, "finish", "Completion", "Job", "Gate", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nTranslationDispatcher.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TranslationDispatcher.kt\ncom/exteragram/messenger/translator/core/TranslationDispatcher\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,251:1\n410#2,3:252\n413#2,4:256\n1#3:255\n777#4:260\n873#4,2:261\n1915#4,2:263\n1915#4,2:265\n*S KotlinDebug\n*F\n+ 1 TranslationDispatcher.kt\ncom/exteragram/messenger/translator/core/TranslationDispatcher\n*L\n59#1:252,3\n59#1:256,4\n97#1:260\n97#1:261,2\n108#1:263,2\n111#1:265,2\n*E\n"})
public final class TranslationDispatcher {
    public static final TranslationDispatcher INSTANCE = new TranslationDispatcher();
    private static final Object lock = new Object();
    private static final HashMap<String, Gate> gates = new HashMap<>();

    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\bæ\u0080\u0001\u0018\u00002\u00020\u0001J\"\u0010\u0002\u001a\u00020\u00032\u000e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u00052\b\u0010\u0007\u001a\u0004\u0018\u00010\bH&¨\u0006\tÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/translator/core/TranslationDispatcher$Completion;", _UrlKt.FRAGMENT_ENCODE_SET, "onDone", _UrlKt.FRAGMENT_ENCODE_SET, "texts", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "error", "Lcom/exteragram/messenger/translator/core/TranslationError;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface Completion {
        void onDone(List<String> texts, TranslationError error);
    }

    private TranslationDispatcher() {
    }

    @Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0002\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\n\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001a\u001a\u00020\u001bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001a\u0010 \u001a\u00020!X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u001c\u0010&\u001a\u0004\u0018\u00010'X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b(\u0010)\"\u0004\b*\u0010+¨\u0006,"}, d2 = {"Lcom/exteragram/messenger/translator/core/TranslationDispatcher$Job;", _UrlKt.FRAGMENT_ENCODE_SET, "translator", "Lcom/exteragram/messenger/translator/core/HttpTranslator;", "tag", _UrlKt.FRAGMENT_ENCODE_SET, "texts", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "fromLang", "toLang", "completion", "Lcom/exteragram/messenger/translator/core/TranslationDispatcher$Completion;", "<init>", "(Lcom/exteragram/messenger/translator/core/HttpTranslator;JLjava/util/List;Ljava/lang/String;Ljava/lang/String;Lcom/exteragram/messenger/translator/core/TranslationDispatcher$Completion;)V", "getTranslator", "()Lcom/exteragram/messenger/translator/core/HttpTranslator;", "getTag", "()J", "getTexts", "()Ljava/util/List;", "getFromLang", "()Ljava/lang/String;", "getToLang", "getCompletion", "()Lcom/exteragram/messenger/translator/core/TranslationDispatcher$Completion;", "attempt", _UrlKt.FRAGMENT_ENCODE_SET, "getAttempt", "()I", "setAttempt", "(I)V", "cancelled", _UrlKt.FRAGMENT_ENCODE_SET, "getCancelled", "()Z", "setCancelled", "(Z)V", "call", "Lokhttp3/Call;", "getCall", "()Lokhttp3/Call;", "setCall", "(Lokhttp3/Call;)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Job {
        private int attempt;
        private Call call;
        private boolean cancelled;
        private final Completion completion;
        private final String fromLang;
        private final long tag;
        private final List<String> texts;
        private final String toLang;
        private final HttpTranslator translator;

        public Job(HttpTranslator httpTranslator, long j, List<String> list, String str, String str2, Completion completion) {
            this.translator = httpTranslator;
            this.tag = j;
            this.texts = list;
            this.fromLang = str;
            this.toLang = str2;
            this.completion = completion;
        }

        public final HttpTranslator getTranslator() {
            return this.translator;
        }

        public final long getTag() {
            return this.tag;
        }

        public final List<String> getTexts() {
            return this.texts;
        }

        public final String getFromLang() {
            return this.fromLang;
        }

        public final String getToLang() {
            return this.toLang;
        }

        public final Completion getCompletion() {
            return this.completion;
        }

        public final int getAttempt() {
            return this.attempt;
        }

        public final void setAttempt(int i) {
            this.attempt = i;
        }

        public final boolean getCancelled() {
            return this.cancelled;
        }

        public final void setCancelled(boolean z) {
            this.cancelled = z;
        }

        public final Call getCall() {
            return this.call;
        }

        public final void setCall(Call call) {
            this.call = call;
        }
    }

    @Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR!\u0010\r\u001a\u0012\u0012\u0004\u0012\u00020\n0\u000ej\b\u0012\u0004\u0012\u00020\n`\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0018\u001a\u00020\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u0015\"\u0004\b\u001a\u0010\u0017R\u001a\u0010\u001b\u001a\u00020\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u0015\"\u0004\b\u001d\u0010\u0017R\u001a\u0010\u001e\u001a\u00020\u001fX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#¨\u0006$"}, d2 = {"Lcom/exteragram/messenger/translator/core/TranslationDispatcher$Gate;", _UrlKt.FRAGMENT_ENCODE_SET, "limits", "Lcom/exteragram/messenger/translator/core/ProviderLimits;", "<init>", "(Lcom/exteragram/messenger/translator/core/ProviderLimits;)V", "getLimits", "()Lcom/exteragram/messenger/translator/core/ProviderLimits;", "queue", "Lkotlin/collections/ArrayDeque;", "Lcom/exteragram/messenger/translator/core/TranslationDispatcher$Job;", "getQueue", "()Lkotlin/collections/ArrayDeque;", "running", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "getRunning", "()Ljava/util/ArrayList;", "lastStartedAt", _UrlKt.FRAGMENT_ENCODE_SET, "getLastStartedAt", "()J", "setLastStartedAt", "(J)V", "cooldownUntil", "getCooldownUntil", "setCooldownUntil", "scheduledPumpAt", "getScheduledPumpAt", "setScheduledPumpAt", "pumpRunnable", "Ljava/lang/Runnable;", "getPumpRunnable", "()Ljava/lang/Runnable;", "setPumpRunnable", "(Ljava/lang/Runnable;)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Gate {
        private long cooldownUntil;
        private long lastStartedAt;
        private final ProviderLimits limits;
        public Runnable pumpRunnable;
        private final ArrayDeque<Job> queue = new ArrayDeque<>();
        private final ArrayList<Job> running = new ArrayList<>();
        private long scheduledPumpAt;

        public Gate(ProviderLimits providerLimits) {
            this.limits = providerLimits;
        }

        public final ProviderLimits getLimits() {
            return this.limits;
        }

        public final ArrayDeque<Job> getQueue() {
            return this.queue;
        }

        public final ArrayList<Job> getRunning() {
            return this.running;
        }

        public final long getLastStartedAt() {
            return this.lastStartedAt;
        }

        public final void setLastStartedAt(long j) {
            this.lastStartedAt = j;
        }

        public final long getCooldownUntil() {
            return this.cooldownUntil;
        }

        public final void setCooldownUntil(long j) {
            this.cooldownUntil = j;
        }

        public final long getScheduledPumpAt() {
            return this.scheduledPumpAt;
        }

        public final void setScheduledPumpAt(long j) {
            this.scheduledPumpAt = j;
        }

        public final Runnable getPumpRunnable() {
            Runnable runnable = this.pumpRunnable;
            if (runnable != null) {
                return runnable;
            }
            return null;
        }

        public final void setPumpRunnable(Runnable runnable) {
            this.pumpRunnable = runnable;
        }
    }

    private final Gate gateOf(HttpTranslator translator) {
        HashMap<String, Gate> map = gates;
        String name = translator.getClass().getName();
        Gate gate = map.get(name);
        if (gate == null) {
            final Gate newGate = new Gate(translator.getLimits());
            gate.setPumpRunnable(new Runnable() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    TranslationDispatcher.INSTANCE.pump(newGate);
                }
            });
            map.put(name, newGate);
            gate = newGate;
        }
        return gate;
    }

    public static /* synthetic */ void enqueue$default(HttpTranslator httpTranslator, long j, List list, String str, String str2, boolean z, Completion completion, int i, Object obj) {
        if ((i & 32) != 0) {
            z = false;
        }
        enqueue(httpTranslator, j, list, str, str2, z, completion);
    }

    @JvmStatic
    public static final void enqueue(HttpTranslator translator, long tag, List<String> texts, String fromLang, String toLang, boolean priority, final Completion completion) {
        final Gate gateGateOf;
        if (texts.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    completion.onDone(CollectionsKt.emptyList(), null);
                }
            });
            return;
        }
        synchronized (lock) {
            try {
                gateGateOf = INSTANCE.gateOf(translator);
                Job job = new Job(translator, tag, texts, fromLang, toLang, completion);
                if (priority) {
                    gateGateOf.getQueue().addFirst(job);
                } else {
                    gateGateOf.getQueue().addLast(job);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                TranslationDispatcher.INSTANCE.pump(gateGateOf);
            }
        });
    }

    /* JADX INFO: renamed from: $r8$lambda$YgkMm1tTSBJeHRcSiH6yxkP-WcU, reason: not valid java name */
    public static boolean m1765$r8$lambda$YgkMm1tTSBJeHRcSiH6yxkPWcU(long j, Job job) {
        return job.getTag() == j;
    }

    @JvmStatic
    public static final void cancel(final long tag) {
        INSTANCE.cancelMatching(new Function1() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(TranslationDispatcher.m1765$r8$lambda$YgkMm1tTSBJeHRcSiH6yxkPWcU(tag, (TranslationDispatcher.Job) obj));
            }
        });
    }

    public static boolean $r8$lambda$dlGT4IYH9GaeUwn58jwdjFq8gMo(Job job) {
        return true;
    }

    @JvmStatic
    public static final void cancelAll() {
        INSTANCE.cancelMatching(new Function1() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(TranslationDispatcher.$r8$lambda$dlGT4IYH9GaeUwn58jwdjFq8gMo((TranslationDispatcher.Job) obj));
            }
        });
    }

    private final void cancelMatching(Function1<? super Job, Boolean> matches) {
        ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        synchronized (lock) {
            try {
                for (Gate gate : gates.values()) {
                    ArrayDeque<Job> queue = gate.getQueue();
                    ArrayList arrayList3 = new ArrayList();
                    for (Job job : queue) {
                        if (matches.invoke(job).booleanValue()) {
                            arrayList3.add(job);
                        }
                    }
                    gate.getQueue().removeAll(arrayList3);
                    arrayList2.addAll(arrayList3);
                    for (Job job2 : gate.getRunning()) {
                        if (matches.invoke(job2).booleanValue()) {
                            job2.setCancelled(true);
                            Call call = job2.getCall();
                            if (call != null) {
                                arrayList.add(call);
                            }
                        }
                    }
                }
                Unit unit = Unit.INSTANCE;
            } catch (Throwable th) {
                throw th;
            }
        }
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((Call) obj).cancel();
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TranslationDispatcher.$r8$lambda$zfieef9Kbhl4WA3IigC4KtjN0KQ(arrayList2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void pump(Gate gate) {
        while (true) {
            Job jobNextJob = nextJob(gate);
            if (jobNextJob == null) {
                return;
            } else {
                start(gate, jobNextJob);
            }
        }
    }

    private final Job nextJob(Gate gate) {
        Job jobRemoveFirst;
        synchronized (lock) {
            try {
                ProviderLimits limits = gate.getLimits();
                jobRemoveFirst = null;
                if (!gate.getQueue().isEmpty() && gate.getRunning().size() < limits.getMaxConcurrent()) {
                    long jElapsedRealtime = SystemClock.elapsedRealtime();
                    long jMax = Math.max(gate.getCooldownUntil(), gate.getLastStartedAt() + limits.getMinIntervalMs());
                    if (jElapsedRealtime < jMax) {
                        INSTANCE.schedulePump(gate, jMax - jElapsedRealtime);
                    } else {
                        jobRemoveFirst = gate.getQueue().removeFirst();
                        gate.setLastStartedAt(jElapsedRealtime);
                        gate.getRunning().add(jobRemoveFirst);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return jobRemoveFirst;
    }

    private final void schedulePump(Gate gate, long delay) {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j = jElapsedRealtime + delay;
        long j2 = jElapsedRealtime + 1;
        long scheduledPumpAt = gate.getScheduledPumpAt();
        if (j2 > scheduledPumpAt || scheduledPumpAt > j) {
            gate.setScheduledPumpAt(j);
            Utilities.globalQueue.cancelRunnable(gate.getPumpRunnable());
            Utilities.globalQueue.postRunnable(gate.getPumpRunnable(), delay);
        }
    }

    private final void start(final Gate gate, final Job job) {
        boolean cancelled;
        try {
            Call callNewCall = job.getTranslator().getClient().newCall(job.getTranslator().buildRequest(job.getTexts(), job.getFromLang(), job.getToLang()));
            synchronized (lock) {
                try {
                    if (!job.getCancelled()) {
                        job.setCall(callNewCall);
                    }
                    cancelled = job.getCancelled();
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (cancelled) {
                finish(gate, job, null, null);
            } else {
                callNewCall.enqueue(new Callback() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher.start.1
                    @Override // okhttp3.Callback
                    public void onFailure(Call call, IOException e) {
                        if (call.isCanceled()) {
                            TranslationDispatcher.INSTANCE.finish(gate, job, null, null);
                        } else {
                            FileLog.e(e);
                            TranslationDispatcher.INSTANCE.retryOrFail(gate, job, TranslationError.Transient.INSTANCE);
                        }
                    }

                    @Override // okhttp3.Callback
                    public void onResponse(Call call, Response response) {
                        ProviderResponse failure;
                        try {
                            Job job2 = job;
                            try {
                                failure = job2.getTranslator().parseResponse(response, job2.getTexts().size());
                                CloseableKt.closeFinally(response, null);
                            } catch (Throwable th2) {
                                try {
                                    throw th2;
                                } catch (Throwable th3) {
                                    CloseableKt.closeFinally(response, th2);
                                    throw th3;
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                            failure = new ProviderResponse.Failure(TranslationError.Transient.INSTANCE);
                        }
                        if (failure instanceof ProviderResponse.Success) {
                            TranslationDispatcher.INSTANCE.finish(gate, job, ((ProviderResponse.Success) failure).getTexts(), null);
                        } else if (failure instanceof ProviderResponse.Failure) {
                            TranslationDispatcher.INSTANCE.retryOrFail(gate, job, ((ProviderResponse.Failure) failure).getError());
                        } else {
                            LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                        }
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e(e);
            finish(gate, job, null, TranslationError.Fatal.INSTANCE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:12:0x0036  */
    public final void retryOrFail(final Gate gate, final Job job, TranslationError error) {
        long jBackoff;
        boolean cancelled;
        job.setAttempt(job.getAttempt() + 1);
        ProviderLimits limits = gate.getLimits();
        if (!error.isRetryable() || job.getAttempt() >= limits.getMaxAttempts()) {
            finish(gate, job, null, error);
            return;
        }
        if (error instanceof TranslationError.RateLimited) {
            TranslationError.RateLimited rateLimited = (TranslationError.RateLimited) error;
            if (rateLimited.getRetryAfterMs() > 0) {
                jBackoff = rateLimited.getRetryAfterMs();
            } else {
                jBackoff = backoff(limits, job.getAttempt());
            }
        } else {
            jBackoff = backoff(limits, job.getAttempt());
        }
        synchronized (lock) {
            try {
                gate.getRunning().remove(job);
                job.setCall(null);
                if (!job.getCancelled()) {
                    if (error instanceof TranslationError.RateLimited) {
                        gate.setCooldownUntil(Math.max(gate.getCooldownUntil(), SystemClock.elapsedRealtime() + jBackoff));
                    }
                    gate.getQueue().addFirst(job);
                    INSTANCE.schedulePump(gate, jBackoff);
                }
                cancelled = job.getCancelled();
            } catch (Throwable th) {
                throw th;
            }
        }
        if (cancelled) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    job.getCompletion().onDone(null, TranslationError.Cancelled.INSTANCE);
                }
            });
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    TranslationDispatcher.INSTANCE.pump(gate);
                }
            });
            return;
        }
        FileLog.d("translator: retrying in " + jBackoff + "ms after " + error + " (attempt " + job.getAttempt() + ")");
    }

    private final long backoff(ProviderLimits limits, int attempt) {
        long jMin = Math.min(limits.getBaseBackoffMs() << RangesKt.coerceIn(attempt - 1, 0, 16), limits.getMaxBackoffMs());
        return jMin + ((long) Utilities.random.nextInt(RangesKt.coerceAtLeast((int) (jMin / 4), 1)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void finish(final Gate gate, final Job job, final List<String> texts, final TranslationError error) {
        final boolean cancelled;
        synchronized (lock) {
            gate.getRunning().remove(job);
            job.setCall(null);
            cancelled = job.getCancelled();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                TranslationDispatcher.$r8$lambda$fKvaJXZz4Z9eg26HlE3NWmdUvgY(cancelled, job, texts, error);
            }
        });
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.translator.core.TranslationDispatcher$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                TranslationDispatcher.INSTANCE.pump(gate);
            }
        });
    }

    public static void $r8$lambda$fKvaJXZz4Z9eg26HlE3NWmdUvgY(boolean z, Job job, List list, TranslationError translationError) {
        if (z) {
            job.getCompletion().onDone(null, TranslationError.Cancelled.INSTANCE);
        } else {
            job.getCompletion().onDone(list, translationError);
        }
    }

    public static void $r8$lambda$zfieef9Kbhl4WA3IigC4KtjN0KQ(ArrayList arrayList) {
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((Job) obj).getCompletion().onDone(null, TranslationError.Cancelled.INSTANCE);
        }
    }
}
