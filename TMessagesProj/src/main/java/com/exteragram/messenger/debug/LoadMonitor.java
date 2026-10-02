package com.exteragram.messenger.debug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.Build;
import android.os.PowerManager;
import android.os.Process;
import android.os.SystemClock;
import android.os.health.HealthStats;
import android.os.health.SystemHealthManager;
import android.os.health.TimerStat;
import android.system.Os;
import android.system.OsConstants;
import com.chaquo.python.internal.Common;
import com.exteragram.messenger.plugins.PluginsConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.LaunchActivity;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000®\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010$\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0012\bÆ\u0002\u0018\u00002\u00020\u0001:\b\u0087\u0001\u0088\u0001\u0089\u0001\u008a\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b\u0007\u0010\bJ\r\u0010\n\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u0015\u0010\r\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\t¢\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u0010\u001a\u00020\u000f¢\u0006\u0004\b\u0010\u0010\u0011J\u000f\u0010\u0013\u001a\u0004\u0018\u00010\u0012¢\u0006\u0004\b\u0013\u0010\u0014J\r\u0010\u0016\u001a\u00020\u0015¢\u0006\u0004\b\u0016\u0010\u0017J\u001b\u0010\u001a\u001a\u00020\u00062\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00060\u0018¢\u0006\u0004\b\u001a\u0010\u001bJ\u0015\u0010\u001e\u001a\u00020\u00062\u0006\u0010\u001d\u001a\u00020\u001c¢\u0006\u0004\b\u001e\u0010\u001fJ\u0015\u0010 \u001a\u00020\u00062\u0006\u0010\u001d\u001a\u00020\u001c¢\u0006\u0004\b \u0010\u001fJ\u000f\u0010!\u001a\u00020\u0006H\u0007¢\u0006\u0004\b!\u0010\u0003J\u001f\u0010#\u001a\u00020\u00062\u0006\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\"\u001a\u00020\u0012H\u0002¢\u0006\u0004\b#\u0010$J\u000f\u0010%\u001a\u00020\u0006H\u0002¢\u0006\u0004\b%\u0010\u0003J\u000f\u0010&\u001a\u00020\tH\u0002¢\u0006\u0004\b&\u0010\u000bJ\u0011\u0010'\u001a\u0004\u0018\u00010\u0004H\u0002¢\u0006\u0004\b'\u0010(J\u000f\u0010*\u001a\u00020)H\u0002¢\u0006\u0004\b*\u0010+J\u000f\u0010,\u001a\u00020\u0015H\u0002¢\u0006\u0004\b,\u0010\u0017J\u000f\u0010-\u001a\u00020\u0015H\u0002¢\u0006\u0004\b-\u0010\u0017J+\u00101\u001a\u001e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020/0.j\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020/`0H\u0002¢\u0006\u0004\b1\u00102J\u0019\u00104\u001a\u0004\u0018\u00010/2\u0006\u00103\u001a\u00020\u000fH\u0002¢\u0006\u0004\b4\u00105J\u0017\u00107\u001a\u00020\u00152\u0006\u00106\u001a\u00020\u0015H\u0002¢\u0006\u0004\b7\u00108JS\u0010<\u001a\u001e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00150.j\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0015`02\u0012\u0010:\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020/092\u0012\u0010;\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020/09H\u0002¢\u0006\u0004\b<\u0010=J=\u0010B\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00150A0@2\u0012\u0010>\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0015092\u0006\u0010?\u001a\u00020\tH\u0002¢\u0006\u0004\bB\u0010CJ3\u0010F\u001a\u00020E2\u0018\u0010>\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00150A0@2\b\b\u0002\u0010D\u001a\u00020\u0004H\u0002¢\u0006\u0004\bF\u0010GJ\u0019\u0010I\u001a\u0004\u0018\u00010\u000f2\u0006\u0010H\u001a\u00020\u000fH\u0002¢\u0006\u0004\bI\u0010JJ\u001d\u0010L\u001a\u00020\u00062\f\u0010K\u001a\b\u0012\u0004\u0012\u00020\u000f0@H\u0002¢\u0006\u0004\bL\u0010MJ\u001f\u0010Q\u001a\u0004\u0018\u00010\u000f2\f\u0010P\u001a\b\u0012\u0004\u0012\u00020O0NH\u0002¢\u0006\u0004\bQ\u0010RJ\u0011\u0010T\u001a\u0004\u0018\u00010SH\u0002¢\u0006\u0004\bT\u0010UJ\u001f\u0010X\u001a\u00020\u000f2\u0006\u0010V\u001a\u00020\u000f2\u0006\u0010W\u001a\u00020\tH\u0002¢\u0006\u0004\bX\u0010YJ\u0017\u0010[\u001a\u00020)2\u0006\u0010Z\u001a\u00020SH\u0002¢\u0006\u0004\b[\u0010\\J\u0017\u0010^\u001a\u00020)2\u0006\u0010]\u001a\u00020\u000fH\u0002¢\u0006\u0004\b^\u0010_J\u001f\u0010a\u001a\u00020\u00122\u0006\u0010`\u001a\u00020)2\u0006\u0010]\u001a\u00020\u000fH\u0002¢\u0006\u0004\ba\u0010bJ\u0017\u0010d\u001a\u00020\u000f2\u0006\u0010c\u001a\u00020\u0015H\u0002¢\u0006\u0004\bd\u0010eJ\u001f\u0010h\u001a\u00020\u000f2\u0006\u0010f\u001a\u00020\u00152\u0006\u0010g\u001a\u00020\u0015H\u0002¢\u0006\u0004\bh\u0010iJI\u0010l\u001a\u00020\u000f2\u0006\u0010g\u001a\u00020\u00152\u0006\u0010f\u001a\u00020\u00152\u0006\u0010j\u001a\u00020\u00152\u0006\u0010k\u001a\u00020\u00152\u0018\u0010>\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00150A0@H\u0002¢\u0006\u0004\bl\u0010mR\u001a\u0010n\u001a\b\u0012\u0004\u0012\u00020\u000f0N8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\bn\u0010oR\"\u0010q\u001a\u0010\u0012\f\u0012\n p*\u0004\u0018\u00010\u000f0\u000f0N8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\bq\u0010oR\u0018\u0010s\u001a\u0004\u0018\u00010r8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\bs\u0010tR\u0018\u0010v\u001a\u0004\u0018\u00010u8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\bv\u0010wR\u001e\u0010z\u001a\n\u0012\u0004\u0012\u00020y\u0018\u00010x8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\bz\u0010{RH\u0010|\u001a6\u0012\u0004\u0012\u00020\u000f\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000f090.j\u001a\u0012\u0004\u0012\u00020\u000f\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000f09`08\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b|\u0010}R0\u0010~\u001a\u001e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f0.j\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f`08\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b~\u0010}R0\u0010\u007f\u001a\u001e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\t0.j\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\t`08\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u007f\u0010}R\u001f\u0010\u0083\u0001\u001a\u00020\u00128FX\u0086\u0084\u0002¢\u0006\u000f\n\u0006\b\u0080\u0001\u0010\u0081\u0001\u001a\u0005\b\u0082\u0001\u0010\u0014R\u001f\u0010\u0086\u0001\u001a\u00020\u00158BX\u0082\u0084\u0002¢\u0006\u000f\n\u0006\b\u0084\u0001\u0010\u0081\u0001\u001a\u0005\b\u0085\u0001\u0010\u0017¨\u0006\u008b\u0001"}, d2 = {"Lcom/exteragram/messenger/debug/LoadMonitor;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", _UrlKt.FRAGMENT_ENCODE_SET, "enabled", _UrlKt.FRAGMENT_ENCODE_SET, "setEnabled", "(Z)V", _UrlKt.FRAGMENT_ENCODE_SET, "getCpuPercent", "()I", "percent", "setCpuPercent", "(I)V", _UrlKt.FRAGMENT_ENCODE_SET, "getSummary", "()Ljava/lang/String;", "Ljava/io/File;", "getLastReport", "()Ljava/io/File;", _UrlKt.FRAGMENT_ENCODE_SET, "getReportsSize", "()J", "Lkotlin/Function0;", "onDone", "deleteReports", "(Lkotlin/jvm/functions/Function0;)V", "Landroid/app/Activity;", "activity", "shareLastReport", "(Landroid/app/Activity;)V", "captureAndShare", "init", "file", "share", "(Landroid/app/Activity;Ljava/io/File;)V", "showPendingAlert", "currentState", "isPlugged", "()Ljava/lang/Boolean;", "Lorg/json/JSONObject;", "batteryJson", "()Lorg/json/JSONObject;", "uidRxBytes", "uidTxBytes", "Ljava/util/HashMap;", "Lcom/exteragram/messenger/debug/LoadMonitor$ThreadStat;", "Lkotlin/collections/HashMap;", "readThreads", "()Ljava/util/HashMap;", "stat", "parseStat", "(Ljava/lang/String;)Lcom/exteragram/messenger/debug/LoadMonitor$ThreadStat;", "ticks", "ticksToMs", "(J)J", _UrlKt.FRAGMENT_ENCODE_SET, "previous", "current", "threadDeltas", "(Ljava/util/Map;Ljava/util/Map;)Ljava/util/HashMap;", "threads", "limit", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/Pair;", "topThreads", "(Ljava/util/Map;I)Ljava/util/List;", "withOrigin", "Lorg/json/JSONArray;", "threadsJson", "(Ljava/util/List;Z)Lorg/json/JSONArray;", "name", "originOf", "(Ljava/lang/String;)Ljava/lang/String;", "names", "resolveOrigins", "(Ljava/util/List;)V", _UrlKt.FRAGMENT_ENCODE_SET, "Ljava/lang/StackTraceElement;", "trace", "appFrame", "([Ljava/lang/StackTraceElement;)Ljava/lang/String;", "Landroid/os/health/HealthStats;", "takeHealthStats", "()Landroid/os/health/HealthStats;", "dataType", PluginsConstants.Settings.KEY, "healthKeyName", "(Ljava/lang/String;I)Ljava/lang/String;", "stats", "healthJson", "(Landroid/os/health/HealthStats;)Lorg/json/JSONObject;", "trigger", "baseReport", "(Ljava/lang/String;)Lorg/json/JSONObject;", "report", "writeReport", "(Lorg/json/JSONObject;Ljava/lang/String;)Ljava/io/File;", "ms", "formatDuration", "(J)Ljava/lang/String;", "cpuMs", "wallMs", "formatPercent", "(JJ)Ljava/lang/String;", "rxBytes", "txBytes", "describeEpisode", "(JJJJLjava/util/List;)Ljava/lang/String;", "STATE_NAMES", "[Ljava/lang/String;", "kotlin.jvm.PlatformType", "SKIPPED_FRAMES", "Lcom/exteragram/messenger/debug/LoadMonitor$Sampler;", "sampler", "Lcom/exteragram/messenger/debug/LoadMonitor$Sampler;", "Lcom/exteragram/messenger/debug/LoadMonitor$PendingAlert;", "pendingAlert", "Lcom/exteragram/messenger/debug/LoadMonitor$PendingAlert;", "Ljava/lang/ref/WeakReference;", "Lorg/telegram/ui/ActionBar/AlertDialog;", "alertRef", "Ljava/lang/ref/WeakReference;", "healthKeyNames", "Ljava/util/HashMap;", "threadOrigins", "originAttempts", "reportsDir$delegate", "Lkotlin/Lazy;", "getReportsDir", "reportsDir", "clockTicks$delegate", "getClockTicks", "clockTicks", "ThreadStat", "PendingAlert", "Episode", "Sampler", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nLoadMonitor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LoadMonitor.kt\ncom/exteragram/messenger/debug/LoadMonitor\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,696:1\n3938#2:697\n4474#2,2:698\n13213#2,2:723\n3938#2:728\n4474#2,2:729\n3938#2:745\n4474#2,2:746\n14048#2,2:751\n2045#3,14:700\n1080#3:715\n1586#3:716\n1661#3,3:717\n777#3:720\n873#3,2:721\n1205#3,2:731\n1282#3,4:733\n1586#3:741\n1661#3,3:742\n1080#3:748\n1915#3,2:749\n1#4:714\n410#5,3:725\n413#5,4:737\n*S KotlinDebug\n*F\n+ 1 LoadMonitor.kt\ncom/exteragram/messenger/debug/LoadMonitor\n*L\n130#1:697\n130#1:698,2\n291#1:723,2\n308#1:728\n308#1:729,2\n417#1:745\n417#1:746,2\n137#1:751,2\n131#1:700,14\n253#1:715\n253#1:716\n253#1:717,3\n271#1:720\n271#1:721,2\n309#1:731,2\n309#1:733,4\n382#1:741\n382#1:742,3\n418#1:748\n420#1:749,2\n304#1:725,3\n304#1:737,4\n*E\n"})
public final class LoadMonitor {
    private static WeakReference<AlertDialog> alertRef;
    private static volatile PendingAlert pendingAlert;
    private static volatile Sampler sampler;
    public static final LoadMonitor INSTANCE = new LoadMonitor();
    private static final String[] STATE_NAMES = {"foreground", "background", "screenOff"};
    private static final String[] SKIPPED_FRAMES = {"java.", "javax.", "kotlin.", "android.", "com.android.", "dalvik.", "libcore.", "sun.", "jdk.", "de.robv.android.xposed.", LoadMonitor.class.getName()};
    private static final HashMap<String, Map<Integer, String>> healthKeyNames = new HashMap<>();
    private static final HashMap<String, String> threadOrigins = new HashMap<>();
    private static final HashMap<String, Integer> originAttempts = new HashMap<>();

    /* JADX INFO: renamed from: reportsDir$delegate, reason: from kotlin metadata */
    private static final Lazy reportsDir = LazyKt.lazy(new Function0() { // from class: com.exteragram.messenger.debug.LoadMonitor$$ExternalSyntheticLambda0
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return DebugFilesKt.debugFilesDir("loadreports");
        }
    });

    /* JADX INFO: renamed from: clockTicks$delegate, reason: from kotlin metadata */
    private static final Lazy clockTicks = LazyKt.lazy(new Function0() { // from class: com.exteragram.messenger.debug.LoadMonitor$$ExternalSyntheticLambda1
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return Long.valueOf(LoadMonitor.$r8$lambda$HqwllwjZJtclxFPulNWPmnOfRKg());
        }
    });

    private LoadMonitor() {
    }

    public final File getReportsDir() {
        return (File) reportsDir.getValue();
    }

    private final long getClockTicks() {
        return ((Number) clockTicks.getValue()).longValue();
    }

    public static long $r8$lambda$HqwllwjZJtclxFPulNWPmnOfRKg() {
        long jSysconf = Os.sysconf(OsConstants._SC_CLK_TCK);
        Long lValueOf = Long.valueOf(jSysconf);
        if (jSysconf <= 0) {
            lValueOf = null;
        }
        if (lValueOf != null) {
            return lValueOf.longValue();
        }
        return 100L;
    }

    @JvmStatic
    public static final void init() {
        if (DebugConfig.getLoadMonitorEnabled()) {
            sampler = new Sampler();
        }
    }

    public final void setEnabled(boolean enabled) {
        if (DebugConfig.getLoadMonitorEnabled() == enabled) {
            return;
        }
        DebugConfig.setLoadMonitorEnabled(enabled);
        Sampler sampler2 = sampler;
        if (sampler2 != null) {
            sampler2.stop();
        }
        sampler = enabled ? new Sampler() : null;
        if (enabled) {
            return;
        }
        pendingAlert = null;
    }

    public final int getCpuPercent() {
        return RangesKt.coerceIn(DebugConfig.getLoadMonitorCpuPercent(), 1, 50);
    }

    public final void setCpuPercent(int percent) {
        DebugConfig.setLoadMonitorCpuPercent(percent);
    }

    public final String getSummary() {
        String summary;
        Sampler sampler2 = sampler;
        if (sampler2 != null && (summary = sampler2.getSummary()) != null) {
            return summary;
        }
        return formatDuration(Process.getElapsedCpuTime()) + " CPU since launch";
    }

    public final File getLastReport() {
        File[] fileArrListFiles = getReportsDir().listFiles();
        if (fileArrListFiles == null) {
            return null;
        }
        File lastFile = null;
        for (File file : fileArrListFiles) {
            if (file.getName().endsWith(".json")) {
                if (lastFile == null || file.lastModified() > lastFile.lastModified()) {
                    lastFile = file;
                }
            }
        }
        return lastFile;
    }

    public final long getReportsSize() {
        File[] fileArrListFiles = getReportsDir().listFiles();
        long length = 0;
        if (fileArrListFiles != null) {
            for (File file : fileArrListFiles) {
                length += file.length();
            }
        }
        return length;
    }

    public final void deleteReports(final Function0<Unit> onDone) {
        if (onDone != null) {
            deleteReports(() -> onDone.invoke());
        } else {
            deleteReports((Runnable) null);
        }
    }

    public final void deleteReports(final Runnable onDone) {
        Utilities.globalQueue.postRunnable(() -> {
            File[] fileArrListFiles = INSTANCE.getReportsDir().listFiles();
            if (fileArrListFiles != null) {
                for (File file : fileArrListFiles) {
                    file.delete();
                }
            }
            if (onDone != null) {
                AndroidUtilities.runOnUIThread(onDone);
            }
        });
    }

    public final void shareLastReport(Activity activity) {
        File lastReport = getLastReport();
        if (lastReport != null) {
            INSTANCE.share(activity, lastReport);
        }
    }

    public final void captureAndShare(final Activity activity) {
        final Sampler sampler2 = sampler;
        DispatchQueue queue = (sampler2 != null && sampler2.getQueue() != null) ? sampler2.getQueue() : Utilities.globalQueue;
        queue.postRunnable(() -> {
            File reportFile = null;
            Throwable error = null;
            try {
                JSONObject json = (sampler2 != null) ? sampler2.captureReport("manual") : null;
                if (json == null) {
                    json = INSTANCE.baseReport("manual");
                }
                reportFile = INSTANCE.writeReport(json, "manual");
            } catch (Throwable th) {
                error = th;
            }
            final File finalFile = reportFile;
            final Throwable finalError = error;
            AndroidUtilities.runOnUIThread(() -> {
                if (finalFile != null) {
                    INSTANCE.share(activity, finalFile);
                }
                if (finalError != null) {
                    FileLog.e(finalError);
                    BulletinFactory.global().createSimpleBulletin(R.raw.error, "Load report failed: " + finalError).show();
                }
            });
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void share(Activity activity, File file) {
        DebugFilesKt.shareDebugFile(activity, file, "application/json", "Share load report");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void showPendingAlert() {
        AlertDialog alertDialog;
        final PendingAlert pendingAlert2 = pendingAlert;
        if (pendingAlert2 == null) {
            return;
        }
        final LaunchActivity launchActivity = LaunchActivity.instance;
        if (sampler == null || launchActivity == null || launchActivity.isFinishing()) {
            return;
        }
        WeakReference<AlertDialog> weakReference = alertRef;
        if (weakReference == null || (alertDialog = weakReference.get()) == null || !alertDialog.isShowing()) {
            pendingAlert = null;
            AlertDialog alertDialogCreate = new AlertDialog.Builder(launchActivity).setTitle("High background load").setMessage(pendingAlert2.getMessage()).setPositiveButton("Share", new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.debug.LoadMonitor$$ExternalSyntheticLambda2
                @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                public final void onClick(AlertDialog alertDialog2, int i) {
                    LoadMonitor.INSTANCE.share(launchActivity, pendingAlert2.getFile());
                }
            }).setNeutralButton("Turn off", new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.debug.LoadMonitor$$ExternalSyntheticLambda3
                @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                public final void onClick(AlertDialog alertDialog2, int i) {
                    LoadMonitor.INSTANCE.setEnabled(false);
                }
            }).setNegativeButton("Later", null).create();
            alertRef = new WeakReference<>(alertDialogCreate);
            try {
                alertDialogCreate.show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int currentState() {
        Object systemService = ApplicationLoader.applicationContext.getSystemService("power");
        PowerManager powerManager = systemService instanceof PowerManager ? (PowerManager) systemService : null;
        if (powerManager != null && !powerManager.isInteractive()) {
            return 2;
        }
        ForegroundDetector foregroundDetector = ForegroundDetector.getInstance();
        int i = 0;
        if (foregroundDetector != null && foregroundDetector.isForeground()) {
            i = 1;
        }
        return i ^ 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Boolean isPlugged() {
        try {
            Intent intentRegisterReceiver = ApplicationLoader.applicationContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            if (intentRegisterReceiver != null) {
                return Boolean.valueOf(intentRegisterReceiver.getIntExtra("plugged", 0) != 0);
            }
        } catch (Throwable th) {
        }
        return null;
    }

    public final JSONObject batteryJson() {
        try {
            return new JSONObject().put("level", LiteMode.getBatteryLevel()).put("plugged", isPlugged());
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final long uidRxBytes() {
        return TrafficStats.getUidRxBytes(Process.myUid());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final long uidTxBytes() {
        return TrafficStats.getUidTxBytes(Process.myUid());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final HashMap<Integer, ThreadStat> readThreads() {
        ThreadStat stat;
        HashMap<Integer, ThreadStat> map = new HashMap<>();
        String[] list = new File("/proc/self/task").list();
        if (list != null) {
            byte[] bArr = new byte[1024];
            for (String str : list) {
                Integer intOrNull = StringsKt.toIntOrNull(str);
                if (intOrNull != null) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream("/proc/self/task/" + str + "/stat");
                        try {
                            int i = fileInputStream.read(bArr);
                            CloseableKt.closeFinally(fileInputStream, null);
                            if (i > 0 && (stat = parseStat(new String(bArr, 0, i, Charsets.UTF_8))) != null) {
                                map.put(intOrNull, stat);
                            }
                        } catch (Throwable th) {
                            try {
                                throw th;
                            } catch (Throwable th2) {
                                CloseableKt.closeFinally(fileInputStream, th);
                                throw th2;
                            }
                        }
                    } catch (Exception unused) {
                    }
                }
            }
        }
        return map;
    }

    private final ThreadStat parseStat(String stat) {
        int iIndexOf = stat.indexOf('(');
        int iLastIndexOf = stat.lastIndexOf(')');
        if (iIndexOf < 0 || iLastIndexOf <= iIndexOf || iLastIndexOf + 2 >= stat.length()) {
            return null;
        }
        String[] parts = stat.substring(iLastIndexOf + 2).split(" ");
        if (parts.length < 13) {
            return null;
        }
        try {
            long utime = Long.parseLong(parts[11]);
            long stime = Long.parseLong(parts[12]);
            return new ThreadStat(stat.substring(iIndexOf + 1, iLastIndexOf), utime + stime);
        } catch (Exception e) {
            return null;
        }
    }

    private final long ticksToMs(long ticks) {
        return (ticks * 1000) / getClockTicks();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final HashMap<String, Long> threadDeltas(Map<Integer, ThreadStat> previous, Map<Integer, ThreadStat> current) {
        HashMap<String, Long> map = new HashMap<>();
        for (Map.Entry<Integer, ThreadStat> entry : current.entrySet()) {
            int iIntValue = entry.getKey().intValue();
            ThreadStat value = entry.getValue();
            ThreadStat threadStat = previous.get(Integer.valueOf(iIntValue));
            long ticks = (threadStat == null || !Intrinsics.areEqual(threadStat.getName(), value.getName())) ? value.getTicks() : value.getTicks() - threadStat.getTicks();
            if (ticks > 0) {
                String name = value.getName();
                Long l = map.get(value.getName());
                map.put(name, Long.valueOf((l != null ? l.longValue() : 0L) + ticksToMs(ticks)));
            }
        }
        return map;
    }

    public final List<Pair<String, Long>> topThreads(Map<String, Long> threads, int limit) {
        List<Map.Entry<String, Long>> list = new ArrayList<>(threads.entrySet());
        list.sort((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()));
        if (list.size() > limit) {
            list = list.subList(0, limit);
        }
        ArrayList<Pair<String, Long>> result = new ArrayList<>(list.size());
        for (Map.Entry<String, Long> entry : list) {
            result.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    public static /* synthetic */ JSONArray threadsJson$default(LoadMonitor loadMonitor, List list, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        try {
            return loadMonitor.threadsJson(list, z);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final JSONArray threadsJson(List<Pair<String, Long>> threads, boolean withOrigin) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        for (Pair<String, Long> pair : threads) {
            String strComponent1 = pair.component1();
            JSONObject jSONObjectPut = new JSONObject().put("name", strComponent1).put("cpuMs", pair.component2().longValue());
            if (withOrigin) {
                jSONObjectPut.put("at", originOf(strComponent1));
            }
            jSONArray.put(jSONObjectPut);
        }
        return jSONArray;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final String originOf(String name) {
        String str;
        HashMap<String, String> map = threadOrigins;
        synchronized (map) {
            str = map.get(name);
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:20:0x0045  */
    public final void resolveOrigins(List<String> names) {
        ArrayList<String> arrayList = new ArrayList<>();
        synchronized (threadOrigins) {
            for (String str : names) {
                if (!"<unattributed>".equals(str) && !threadOrigins.containsKey(str)) {
                    Integer num = originAttempts.get(str);
                    if ((num != null ? num.intValue() : 0) < 8) {
                        arrayList.add(str);
                    }
                }
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        Map<Thread, StackTraceElement[]> map = null;
        try {
            map = Thread.getAllStackTraces();
        } catch (Throwable th) {
        }
        if (map == null) {
            return;
        }
        synchronized (threadOrigins) {
            for (String str2 : arrayList) {
                Integer num2 = originAttempts.get(str2);
                originAttempts.put(str2, Integer.valueOf((num2 != null ? num2.intValue() : 0) + 1));
            }
            for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
                Thread thread = entry.getKey();
                StackTraceElement[] stackTraceElementArr = entry.getValue();
                String name = thread.getName();
                String strTake = name.length() > 15 ? name.substring(0, 15) : name;
                if (arrayList.contains(strTake)) {
                    if (!threadOrigins.containsKey(strTake)) {
                        String strAppFrame = appFrame(stackTraceElementArr);
                        if (strAppFrame != null) {
                            threadOrigins.put(strTake, strAppFrame);
                        }
                    }
                }
            }
        }
    }

    private final String appFrame(StackTraceElement[] trace) {
        for (StackTraceElement stackTraceElement : trace) {
            String className = stackTraceElement.getClassName();
            boolean skipped = false;
            for (String skip : SKIPPED_FRAMES) {
                if (className.startsWith(skip)) {
                    skipped = true;
                    break;
                }
            }
            if (!skipped) {
                int lastDot = className.lastIndexOf('.');
                String simpleName = (lastDot >= 0 && lastDot + 1 < className.length()) ? className.substring(lastDot + 1) : className;
                return simpleName + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final HealthStats takeHealthStats() {
        try {
            Object systemService = ApplicationLoader.applicationContext.getSystemService("systemhealth");
            if (systemService instanceof SystemHealthManager) {
                return ((SystemHealthManager) systemService).takeMyUidSnapshot();
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
        return null;
    }

    private final String healthKeyName(String dataType, int key) {
        synchronized (healthKeyNames) {
            Map<Integer, String> map = healthKeyNames.get(dataType);
            if (map == null) {
                map = new HashMap<>();
                try {
                    String className = dataType.contains(".") ? dataType : "android.os.health." + dataType;
                    Field[] fields = Class.forName(className).getFields();
                    for (Field field : fields) {
                        if (Modifier.isStatic(field.getModifiers()) && field.getType() == Integer.TYPE) {
                            map.put(Integer.valueOf(field.getInt(null)), field.getName());
                        }
                    }
                } catch (Throwable th) {
                }
                healthKeyNames.put(dataType, map);
            }
            String str = map.get(Integer.valueOf(key));
            return str == null ? String.valueOf(key) : str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final JSONObject healthJson(HealthStats stats) throws JSONException {
        String dataType = stats.getDataType();
        JSONObject jSONObjectPut = new JSONObject().put("type", dataType);
        if (stats.getMeasurementKeyCount() > 0) {
            JSONObject jSONObject = new JSONObject();
            int measurementKeyCount = stats.getMeasurementKeyCount();
            for (int i = 0; i < measurementKeyCount; i++) {
                int measurementKeyAt = stats.getMeasurementKeyAt(i);
                jSONObject.put(healthKeyName(dataType, measurementKeyAt), stats.getMeasurement(measurementKeyAt));
            }
            jSONObjectPut.put("measurements", jSONObject);
        }
        if (stats.getTimerKeyCount() > 0) {
            JSONObject jSONObject2 = new JSONObject();
            int timerKeyCount = stats.getTimerKeyCount();
            for (int i2 = 0; i2 < timerKeyCount; i2++) {
                int timerKeyAt = stats.getTimerKeyAt(i2);
                jSONObject2.put(healthKeyName(dataType, timerKeyAt), new JSONArray().put(stats.getTimerCount(timerKeyAt)).put(stats.getTimerTime(timerKeyAt)));
            }
            jSONObjectPut.put("timers", jSONObject2);
        }
        if (stats.getMeasurementsKeyCount() > 0) {
            JSONObject jSONObject3 = new JSONObject();
            int measurementsKeyCount = stats.getMeasurementsKeyCount();
            for (int i3 = 0; i3 < measurementsKeyCount; i3++) {
                int measurementsKeyAt = stats.getMeasurementsKeyAt(i3);
                JSONObject jSONObject4 = new JSONObject();
                for (Map.Entry<String, Long> entry : stats.getMeasurements(measurementsKeyAt).entrySet()) {
                    jSONObject4.put(entry.getKey(), entry.getValue().longValue());
                }
                jSONObject3.put(healthKeyName(dataType, measurementsKeyAt), jSONObject4);
            }
            jSONObjectPut.put("measurementMaps", jSONObject3);
        }
        if (stats.getTimersKeyCount() > 0) {
            JSONObject jSONObject5 = new JSONObject();
            int timersKeyCount = stats.getTimersKeyCount();
            for (int i4 = 0; i4 < timersKeyCount; i4++) {
                int timersKeyAt = stats.getTimersKeyAt(i4);
                JSONObject jSONObject6 = new JSONObject();
                for (Map.Entry<String, TimerStat> entry2 : stats.getTimers(timersKeyAt).entrySet()) {
                    String key = entry2.getKey();
                    TimerStat value = entry2.getValue();
                    jSONObject6.put(key, new JSONArray().put(value.getCount()).put(value.getTime()));
                }
                jSONObject5.put(healthKeyName(dataType, timersKeyAt), jSONObject6);
            }
            jSONObjectPut.put("timerMaps", jSONObject5);
        }
        if (stats.getStatsKeyCount() > 0) {
            JSONObject jSONObject7 = new JSONObject();
            int statsKeyCount = stats.getStatsKeyCount();
            for (int i5 = 0; i5 < statsKeyCount; i5++) {
                int statsKeyAt = stats.getStatsKeyAt(i5);
                JSONObject jSONObject8 = new JSONObject();
                for (Map.Entry<String, HealthStats> entry3 : stats.getStats(statsKeyAt).entrySet()) {
                    jSONObject8.put(entry3.getKey(), healthJson(entry3.getValue()));
                }
                jSONObject7.put(healthKeyName(dataType, statsKeyAt), jSONObject8);
            }
            jSONObjectPut.put("stats", jSONObject7);
        }
        return jSONObjectPut;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final JSONObject baseReport(String trigger) throws JSONException {
        Context context = ApplicationLoader.applicationContext;
        Object systemService = context.getSystemService("power");
        PowerManager powerManager = systemService instanceof PowerManager ? (PowerManager) systemService : null;
        HashMap map = new HashMap();
        for (ThreadStat threadStat : readThreads().values()) {
            String name = threadStat.getName();
            Long l = (Long) map.get(threadStat.getName());
            map.put(name, Long.valueOf((l != null ? l.longValue() : 0L) + ticksToMs(threadStat.getTicks())));
        }
        List<Pair<String, Long>> list = topThreads(map, 30);
        List<String> arrayList = new ArrayList<>();
        for (Pair<String, Long> pair : list) {
            arrayList.add(pair.getFirst());
        }
        resolveOrigins(arrayList);
        JSONObject jSONObjectPut = new JSONObject().put("format", 1).put("trigger", trigger).put("createdAt", System.currentTimeMillis()).put(Common.ASSET_APP, new JSONObject().put("version", BuildVars.BUILD_VERSION_STRING).put("code", BuildVars.BUILD_VERSION).put(BuildConfig.BUILD_TYPE, BuildVars.DEBUG_VERSION)).put("device", new JSONObject().put("manufacturer", Build.MANUFACTURER).put("model", Build.MODEL).put("sdk", Build.VERSION.SDK_INT).put("cores", Runtime.getRuntime().availableProcessors())).put("battery", batteryJson());
        boolean z = false;
        JSONObject jSONObjectPut2 = new JSONObject().put("accounts", UserConfig.getActivatedAccountsCount()).put("proxy", SharedConfig.isProxyEnabled()).put("logs", BuildVars.LOGS_ENABLED).put("powerSave", powerManager != null && powerManager.isPowerSaveMode());
        if (powerManager != null && powerManager.isIgnoringBatteryOptimizations(context.getPackageName())) {
            z = true;
        }
        JSONObject jSONObjectPut3 = jSONObjectPut.put("settings", jSONObjectPut2.put("ignoringBatteryOptimizations", z)).put("process", new JSONObject().put("uptimeMs", SystemClock.elapsedRealtime() - Process.getStartElapsedRealtime()).put("cpuMs", Process.getElapsedCpuTime()).put("state", STATE_NAMES[currentState()]).put("threads", threadsJson(list, true)));
        HealthStats healthStatsTakeHealthStats = takeHealthStats();
        return jSONObjectPut3.put("healthNow", healthStatsTakeHealthStats != null ? INSTANCE.healthJson(healthStatsTakeHealthStats) : null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final File writeReport(JSONObject report, String trigger) {
        getReportsDir().mkdirs();
        File file = new File(getReportsDir(), "load_" + BuildVars.BUILD_VERSION_STRING + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + "_" + trigger + ".json");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(report.toString(1).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            FileLog.e(e);
        }
        File[] fileArrListFiles = getReportsDir().listFiles();
        if (fileArrListFiles != null) {
            ArrayList<File> jsonFiles = new ArrayList<>();
            for (File file2 : fileArrListFiles) {
                if (file2.getName().endsWith(".json")) {
                    jsonFiles.add(file2);
                }
            }
            jsonFiles.sort((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            if (jsonFiles.size() > 10) {
                for (int i = 10; i < jsonFiles.size(); i++) {
                    jsonFiles.get(i).delete();
                }
            }
        }
        return file;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final String formatDuration(long ms) {
        long j = ms / 1000;
        if (ms < 1000) {
            return ms + "ms";
        }
        if (j < 60) {
            return j + "s";
        }
        if (j < 3600) {
            return (j / 60) + "m " + (j % 60) + "s";
        }
        return (j / 3600) + "h " + ((j % 3600) / 60) + "m";
    }

    private final String formatPercent(long cpuMs, long wallMs) {
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        return String.format(Locale.US, "%.1f%%", Arrays.copyOf(new Object[]{Double.valueOf(wallMs > 0 ? (cpuMs * 100.0d) / wallMs : 0.0d)}, 1));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final String describeEpisode(long wallMs, long cpuMs, long rxBytes, long txBytes, List<Pair<String, Long>> threads) {
        StringBuilder sb = new StringBuilder();
        sb.append("In " + formatDuration(wallMs) + " in the background, the app used " + formatPercent(cpuMs, wallMs) + " of one core: ");
        sb.append(formatDuration(cpuMs)).append(" of CPU");
        if (rxBytes >= 0 && txBytes >= 0) {
            sb.append(", " + AndroidUtilities.formatFileSize(rxBytes) + " received, " + AndroidUtilities.formatFileSize(txBytes) + " sent");
        }
        sb.append('.');
        if (!threads.isEmpty()) {
            sb.append("\n\nBusiest threads: ");
            StringBuilder threadsStr = new StringBuilder();
            for (int i = 0; i < threads.size(); i++) {
                if (i > 0) threadsStr.append(", ");
                Pair<String, Long> p = threads.get(i);
                threadsStr.append(p.getFirst()).append(" ").append(formatDuration(p.getSecond().longValue()));
            }
            sb.append(threadsStr);
            sb.append('.');
        }
        sb.append("\n\nThe report holds thread names, traffic counters and battery stats, no message content.");
        return sb.toString();
    }

        /* JADX INFO: renamed from: $r8$lambda$EvWZU_-AiIxct8eo7VUHXa2A0bw, reason: not valid java name */
    public static CharSequence m1237$r8$lambda$EvWZU_AiIxct8eo7VUHXa2A0bw(Pair pair) {
        return pair.getFirst() + " " + INSTANCE.formatDuration(((Number) pair.getSecond()).longValue());
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\f"}, d2 = {"Lcom/exteragram/messenger/debug/LoadMonitor$ThreadStat;", _UrlKt.FRAGMENT_ENCODE_SET, "name", _UrlKt.FRAGMENT_ENCODE_SET, "ticks", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;J)V", "getName", "()Ljava/lang/String;", "getTicks", "()J", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class ThreadStat {
        private final String name;
        private final long ticks;

        public ThreadStat(String str, long j) {
            this.name = str;
            this.ticks = j;
        }

        public final String getName() {
            return this.name;
        }

        public final long getTicks() {
            return this.ticks;
        }
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\f"}, d2 = {"Lcom/exteragram/messenger/debug/LoadMonitor$PendingAlert;", _UrlKt.FRAGMENT_ENCODE_SET, "file", "Ljava/io/File;", "message", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/io/File;Ljava/lang/String;)V", "getFile", "()Ljava/io/File;", "getMessage", "()Ljava/lang/String;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class PendingAlert {
        private final File file;
        private final String message;

        public PendingAlert(File file, String str) {
            this.file = file;
            this.message = str;
        }

        public final File getFile() {
            return this.file;
        }

        public final String getMessage() {
            return this.message;
        }
    }

    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0002\u0018\u00002\u00020\u0001BM\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b0\t\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u001d\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b0\t¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0013\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001a\u001a\u00020\u001bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001f¨\u0006 "}, d2 = {"Lcom/exteragram/messenger/debug/LoadMonitor$Episode;", _UrlKt.FRAGMENT_ENCODE_SET, "startedAt", _UrlKt.FRAGMENT_ENCODE_SET, "startRealtime", "cpuMs", "rxBytes", "txBytes", "threads", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/debug/LoadMonitor$ThreadStat;", "health", "Landroid/os/health/HealthStats;", "<init>", "(JJJJJLjava/util/Map;Landroid/os/health/HealthStats;)V", "getStartedAt", "()J", "getStartRealtime", "getCpuMs", "getRxBytes", "getTxBytes", "getThreads", "()Ljava/util/Map;", "getHealth", "()Landroid/os/health/HealthStats;", "reported", _UrlKt.FRAGMENT_ENCODE_SET, "getReported", "()Z", "setReported", "(Z)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Episode {
        private final long cpuMs;
        private final HealthStats health;
        private boolean reported;
        private final long rxBytes;
        private final long startRealtime;
        private final long startedAt;
        private final Map<Integer, ThreadStat> threads;
        private final long txBytes;

        public Episode(long j, long j2, long j3, long j4, long j5, Map<Integer, ThreadStat> map, HealthStats healthStats) {
            this.startedAt = j;
            this.startRealtime = j2;
            this.cpuMs = j3;
            this.rxBytes = j4;
            this.txBytes = j5;
            this.threads = map;
            this.health = healthStats;
        }

        public final long getStartedAt() {
            return this.startedAt;
        }

        public final long getStartRealtime() {
            return this.startRealtime;
        }

        public final long getCpuMs() {
            return this.cpuMs;
        }

        public final long getRxBytes() {
            return this.rxBytes;
        }

        public final long getTxBytes() {
            return this.txBytes;
        }

        public final Map<Integer, ThreadStat> getThreads() {
            return this.threads;
        }

        public final HealthStats getHealth() {
            return this.health;
        }

        public final boolean getReported() {
            return this.reported;
        }

        public final void setReported(boolean z) {
            this.reported = z;
        }
    }

    @Metadata(d1 = {"\u0000\u0084\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0016\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\n\n\u0002\u0010$\n\u0002\b\u0005\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0006\u0010.\u001a\u00020/J\b\u00100\u001a\u00020/H\u0016J\b\u00101\u001a\u00020/H\u0016J5\u00102\u001a\u00020/2\u0006\u00103\u001a\u00020\u00182\u0006\u00104\u001a\u00020\u00182\u0016\u00105\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010706\"\u0004\u0018\u000107H\u0016¢\u0006\u0002\u00108J\b\u00109\u001a\u00020/H\u0002J\b\u0010:\u001a\u00020/H\u0002J\b\u0010;\u001a\u00020/H\u0002J<\u0010<\u001a\u00020/2\u0006\u0010=\u001a\u00020\f2\u0006\u0010>\u001a\u00020\f2\u0006\u0010?\u001a\u00020\f2\u0006\u0010@\u001a\u00020\f2\u0012\u0010A\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u001b0BH\u0002J\n\u0010C\u001a\u0004\u0018\u00010\u000eH\u0002J\u000e\u0010D\u001a\u00020\u00102\u0006\u0010E\u001a\u00020%J\u000e\u0010F\u001a\u00020\u00102\u0006\u0010E\u001a\u00020%R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e¢\u0006\u0002\n\u0000R*\u0010\u0019\u001a\u001e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u001b0\u001aj\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u001b`\u001cX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020 X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020 X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020 X\u0082\u0004¢\u0006\u0002\n\u0000R*\u0010$\u001a\u001e\u0012\u0004\u0012\u00020%\u0012\u0004\u0012\u00020 0\u001aj\u000e\u0012\u0004\u0012\u00020%\u0012\u0004\u0012\u00020 `\u001cX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00100'X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010(\u001a\u0004\u0018\u00010)X\u0082\u000e¢\u0006\u0002\n\u0000R\"\u0010+\u001a\u0004\u0018\u00010%2\b\u0010*\u001a\u0004\u0018\u00010%@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b,\u0010-¨\u0006G"}, d2 = {"Lcom/exteragram/messenger/debug/LoadMonitor$Sampler;", "Lorg/telegram/ui/Components/ForegroundDetector$Listener;", "Lorg/telegram/messenger/NotificationCenter$NotificationCenterDelegate;", "<init>", "()V", "queue", "Lorg/telegram/messenger/DispatchQueue;", "getQueue", "()Lorg/telegram/messenger/DispatchQueue;", "sampleRunnable", "Ljava/lang/Runnable;", "startedAt", _UrlKt.FRAGMENT_ENCODE_SET, "startHealth", "Landroid/os/health/HealthStats;", "startBattery", "Lorg/json/JSONObject;", "recentHealth", "recentHealthRealtime", "lastRealtime", "lastCpuMs", "lastRxBytes", "lastTxBytes", "lastState", _UrlKt.FRAGMENT_ENCODE_SET, "threads", "Ljava/util/HashMap;", "Lcom/exteragram/messenger/debug/LoadMonitor$ThreadStat;", "Lkotlin/collections/HashMap;", "episode", "Lcom/exteragram/messenger/debug/LoadMonitor$Episode;", "wallMs", _UrlKt.FRAGMENT_ENCODE_SET, "cpuMs", "rxBytes", "txBytes", "threadMs", _UrlKt.FRAGMENT_ENCODE_SET, "samples", "Lkotlin/collections/ArrayDeque;", "observers", "Lorg/telegram/messenger/NotificationCenter$ObserversGroup;", "value", "summary", "getSummary", "()Ljava/lang/String;", "stop", _UrlKt.FRAGMENT_ENCODE_SET, "onBecameForeground", "onBecameBackground", "didReceivedNotification", "id", "account", "args", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "(II[Ljava/lang/Object;)V", "scheduleNext", "sample", "updateState", "checkEpisode", "now", "cpu", "rx", "tx", "current", _UrlKt.FRAGMENT_ENCODE_SET, "recentHealthStats", "captureReport", "trigger", "buildReport", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    @SourceDebugExtension({"SMAP\nLoadMonitor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LoadMonitor.kt\ncom/exteragram/messenger/debug/LoadMonitor$Sampler\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,696:1\n777#2:697\n873#2,2:698\n1586#2:700\n1661#2,3:701\n1080#2:712\n410#3,7:704\n1#4:711\n*S KotlinDebug\n*F\n+ 1 LoadMonitor.kt\ncom/exteragram/messenger/debug/LoadMonitor$Sampler\n*L\n567#1:697\n567#1:698,2\n567#1:700\n567#1:701,3\n663#1:712\n574#1:704,7\n*E\n"})
    public static final class Sampler implements ForegroundDetector.Listener, NotificationCenter.NotificationCenterDelegate {
        private final long[] cpuMs;
        private Episode episode;
        private long lastCpuMs;
        private long lastRealtime;
        private long lastRxBytes;
        private int lastState;
        private long lastTxBytes;
        private NotificationCenter.ObserversGroup observers;
        private final DispatchQueue queue;
        private HealthStats recentHealth;
        private long recentHealthRealtime;
        private final long[] rxBytes;
        private final Runnable sampleRunnable;
        private final ArrayDeque<JSONObject> samples;
        private JSONObject startBattery;
        private HealthStats startHealth;
        private final long startedAt;
        private String summary;
        private final long[] txBytes;
        private final long[] wallMs;
        private final HashMap<String, long[]> threadMs;
        private HashMap<Integer, ThreadStat> threads;

        public Sampler() {
            DispatchQueue dispatchQueue = new DispatchQueue("loadMonitorQueue");
            this.queue = dispatchQueue;
            this.sampleRunnable = () -> {
                sample();
                scheduleNext();
            };
            this.startedAt = System.currentTimeMillis();
            this.lastRxBytes = -1L;
            this.lastTxBytes = -1L;
            this.threads = new HashMap<>();
            this.wallMs = new long[LoadMonitor.STATE_NAMES.length];
            this.cpuMs = new long[LoadMonitor.STATE_NAMES.length];
            this.rxBytes = new long[LoadMonitor.STATE_NAMES.length];
            this.txBytes = new long[LoadMonitor.STATE_NAMES.length];
            this.threadMs = new HashMap<>();
            this.samples = new ArrayDeque<>();
            ForegroundDetector foregroundDetector = ForegroundDetector.getInstance();
            if (foregroundDetector != null) {
                foregroundDetector.addListener(this);
            }
            AndroidUtilities.runOnUIThread(() -> {
                this.observers = NotificationCenter.getGlobalInstance().createObserversGroup(this).add(NotificationCenter.screenStateChanged);
            });
            dispatchQueue.postRunnable(() -> {
                this.startHealth = recentHealthStats();
                LoadMonitor loadMonitor = LoadMonitor.INSTANCE;
                this.startBattery = loadMonitor.batteryJson();
                this.lastRealtime = SystemClock.elapsedRealtime();
                this.lastCpuMs = Process.getElapsedCpuTime();
                this.lastRxBytes = loadMonitor.uidRxBytes();
                this.lastTxBytes = loadMonitor.uidTxBytes();
                this.threads = loadMonitor.readThreads();
                updateState();
                scheduleNext();
            });
        }

        public final DispatchQueue getQueue() {
            return this.queue;
        }

        public final String getSummary() {
            return this.summary;
        }

        public final void stop() {
            ForegroundDetector foregroundDetector = ForegroundDetector.getInstance();
            if (foregroundDetector != null) {
                foregroundDetector.removeListener(this);
            }
            AndroidUtilities.runOnUIThread(() -> {
                if (this.observers != null) {
                    this.observers.removeAllObservers();
                    this.observers = null;
                }
            });
            this.queue.cleanupQueue();
            this.queue.recycle();
        }

        @Override
        public void onBecameForeground() {
            this.queue.postRunnable(this::sample);
            AndroidUtilities.runOnUIThread(() -> LoadMonitor.INSTANCE.showPendingAlert(), 1000L);
        }

        @Override
        public void onBecameBackground() {
            this.queue.postRunnable(this::sample);
        }

        @Override
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.screenStateChanged) {
                this.queue.postRunnable(this::sample);
            }
        }

        private final void scheduleNext() {
            this.queue.cancelRunnable(this.sampleRunnable);
            this.queue.postRunnable(this.sampleRunnable, 60000L);
        }

        public final void sample() {
            try {
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                long elapsedCpuTime = Process.getElapsedCpuTime();
                LoadMonitor loadMonitor = LoadMonitor.INSTANCE;
                long jUidRxBytes = loadMonitor.uidRxBytes();
                long jUidTxBytes = loadMonitor.uidTxBytes();
                HashMap<Integer, ThreadStat> currentThreads = loadMonitor.readThreads();
                int i2 = this.lastState;
                long j5 = jElapsedRealtime - this.lastRealtime;
                long j6 = elapsedCpuTime - this.lastCpuMs;
                long j2 = (jUidRxBytes >= 0 && this.lastRxBytes >= 0) ? (jUidRxBytes - this.lastRxBytes) : 0L;
                long j4 = (jUidTxBytes >= 0 && this.lastTxBytes >= 0) ? (jUidTxBytes - this.lastTxBytes) : 0L;

                HashMap<String, Long> mapThreadDeltas = loadMonitor.threadDeltas(this.threads, currentThreads);
                long sumThreads = 0;
                for (Long v : mapThreadDeltas.values()) {
                    if (v != null) sumThreads += v;
                }
                long jSumOfLong = j6 - sumThreads;
                if (jSumOfLong > 0) {
                    mapThreadDeltas.put("<unattributed>", Long.valueOf(jSumOfLong));
                }
                List<Pair<String, Long>> top = loadMonitor.topThreads(mapThreadDeltas, 3);
                ArrayList<String> origins = new ArrayList<>();
                for (Pair<String, Long> p : top) {
                    if (((Number) p.getSecond()).longValue() >= 200) {
                        origins.add((String) p.getFirst());
                    }
                }
                loadMonitor.resolveOrigins(origins);

                this.wallMs[i2] += j5;
                this.cpuMs[i2] += j6;
                this.rxBytes[i2] += j2;
                this.txBytes[i2] += j4;

                for (Map.Entry<String, Long> entry : mapThreadDeltas.entrySet()) {
                    String str = entry.getKey();
                    long jLongValue = entry.getValue().longValue();
                    long[] jArr = this.threadMs.computeIfAbsent(str, k -> new long[LoadMonitor.STATE_NAMES.length]);
                    jArr[i2] += jLongValue;
                }

                if (j5 >= 1000) {
                    JSONObject jSONObjectPut = new JSONObject()
                            .put("t", System.currentTimeMillis())
                            .put("state", LoadMonitor.STATE_NAMES[i2])
                            .put("wallMs", j5)
                            .put("cpuMs", j6)
                            .put("rxBytes", j2)
                            .put("txBytes", j4)
                            .put("plugged", loadMonitor.isPlugged())
                            .put("threads", loadMonitor.threadsJson(loadMonitor.topThreads(mapThreadDeltas, 3), false));
                    this.samples.addLast(jSONObjectPut);
                    while (this.samples.size() > 240) {
                        this.samples.removeFirst();
                    }
                }
                this.lastRealtime = jElapsedRealtime;
                this.lastCpuMs = elapsedCpuTime;
                this.lastRxBytes = jUidRxBytes;
                this.lastTxBytes = jUidTxBytes;
                this.threads = currentThreads;

                long totalCpu = 0;
                for (long v : this.cpuMs) totalCpu += v;
                long totalRx = 0;
                for (long v : this.rxBytes) totalRx += v;
                long totalTx = 0;
                for (long v : this.txBytes) totalTx += v;

                this.summary = loadMonitor.formatDuration(totalCpu) + " CPU, " + AndroidUtilities.formatFileSize(totalRx + totalTx);
                checkEpisode(jElapsedRealtime, elapsedCpuTime, jUidRxBytes, jUidTxBytes, currentThreads);
                updateState();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        private final void updateState() {
            int iCurrentState = LoadMonitor.INSTANCE.currentState();
            this.lastState = iCurrentState;
            if (iCurrentState == 0) {
                this.episode = null;
            } else if (this.episode == null) {
                this.episode = new Episode(System.currentTimeMillis(), this.lastRealtime, this.lastCpuMs, this.lastRxBytes, this.lastTxBytes, this.threads, recentHealthStats());
            }
        }

        private final void checkEpisode(long now, long cpu, long rx, long tx, Map<Integer, ThreadStat> current) {
            Episode episode = this.episode;
            if (episode == null) {
                return;
            }
            long startRealtime = now - episode.getStartRealtime();
            if (episode.getReported() || startRealtime < 300000) {
                return;
            }
            long cpuMs = cpu - episode.getCpuMs();
            long j = 100 * cpuMs;
            LoadMonitor loadMonitor = LoadMonitor.INSTANCE;
            if (j < ((long) loadMonitor.getCpuPercent()) * startRealtime) {
                return;
            }
            episode.setReported(true);
            File file = null;
            try {
                file = loadMonitor.writeReport(buildReport("background"), "background");
            } catch (Throwable th) {
                FileLog.e(th);
            }
            if (file == null) {
                return;
            }
            long txBytes = -1;
            long rxBytes = (rx < 0 || episode.getRxBytes() < 0) ? -1L : rx - episode.getRxBytes();
            if (tx >= 0 && episode.getTxBytes() >= 0) {
                txBytes = tx - episode.getTxBytes();
            }
            LoadMonitor.pendingAlert = new PendingAlert(file, loadMonitor.describeEpisode(startRealtime, cpuMs, rxBytes, txBytes, loadMonitor.topThreads(loadMonitor.threadDeltas(episode.getThreads(), current), 3)));
        }

        private final HealthStats recentHealthStats() {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (this.recentHealth == null || elapsedRealtime - this.recentHealthRealtime >= 30000) {
                this.recentHealthRealtime = elapsedRealtime;
                this.recentHealth = LoadMonitor.INSTANCE.takeHealthStats();
            }
            return this.recentHealth;
        }

        public final JSONObject captureReport(String trigger) throws JSONException {
            sample();
            return buildReport(trigger);
        }

        public final JSONObject buildReport(String trigger) throws JSONException {
            JSONObject jSONObjectBaseReport = LoadMonitor.INSTANCE.baseReport(trigger);
            JSONObject jSONObject = new JSONObject();
            int length = LoadMonitor.STATE_NAMES.length;
            for (int i = 0; i < length; i++) {
                jSONObject.put(LoadMonitor.STATE_NAMES[i], new JSONObject().put("wallMs", this.wallMs[i]).put("cpuMs", this.cpuMs[i]).put("rxBytes", this.rxBytes[i]).put("txBytes", this.txBytes[i]));
            }
            JSONArray jSONArray = new JSONArray();
            List<Map.Entry<String, long[]>> sortedThreads = new ArrayList<>(this.threadMs.entrySet());
            sortedThreads.sort((e1, e2) -> {
                long sum1 = 0;
                for (long v : e1.getValue()) sum1 += v;
                long sum2 = 0;
                for (long v : e2.getValue()) sum2 += v;
                return Long.compare(sum2, sum1);
            });
            if (sortedThreads.size() > 30) {
                sortedThreads = sortedThreads.subList(0, 30);
            }
            for (Map.Entry<String, long[]> entry : sortedThreads) {
                String str = entry.getKey();
                long[] jArr = entry.getValue();
                long sum = 0;
                for (long v : jArr) sum += v;
                JSONObject jSONObjectPut = new JSONObject().put("name", str).put("cpuMs", sum).put("at", LoadMonitor.INSTANCE.originOf(str));
                for (int i2 = 0; i2 < LoadMonitor.STATE_NAMES.length; i2++) {
                    jSONObjectPut.put(LoadMonitor.STATE_NAMES[i2], jArr[i2]);
                }
                jSONArray.put(jSONObjectPut);
            }
            jSONObjectBaseReport.put("monitor", new JSONObject().put("startedAt", this.startedAt).put("sampleIntervalMs", 60000L).put("batteryAtStart", this.startBattery).put("states", jSONObject).put("threads", jSONArray).put("samples", new JSONArray(this.samples)));
            Episode episode = this.episode;
            if (episode != null) {
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                long elapsedCpuTime = Process.getElapsedCpuTime();
                LoadMonitor loadMonitor = LoadMonitor.INSTANCE;
                long jUidRxBytes = loadMonitor.uidRxBytes();
                long jUidTxBytes = loadMonitor.uidTxBytes();
                HashMap<Integer, ThreadStat> readThreads = loadMonitor.readThreads();
                long startRealtime = jElapsedRealtime - episode.getStartRealtime();
                long cpuMs = elapsedCpuTime - episode.getCpuMs();
                long rxBytes = (jUidRxBytes < 0 || episode.getRxBytes() < 0) ? -1L : jUidRxBytes - episode.getRxBytes();
                long txBytes = (jUidTxBytes < 0 || episode.getTxBytes() < 0) ? -1L : jUidTxBytes - episode.getTxBytes();
                JSONObject jSONObjectEpisode = new JSONObject().put("startedAt", episode.getStartedAt()).put("wallMs", startRealtime).put("cpuMs", cpuMs).put("rxBytes", rxBytes).put("txBytes", txBytes).put("threads", loadMonitor.threadsJson(loadMonitor.topThreads(loadMonitor.threadDeltas(episode.getThreads(), readThreads), 10), false));
                HealthStats health = episode.getHealth();
                if (health != null) {
                    jSONObjectEpisode.put("healthEpisodeStart", loadMonitor.healthJson(health));
                }
                jSONObjectBaseReport.put("episode", jSONObjectEpisode);
            }
            HealthStats healthStats = this.startHealth;
            if (healthStats != null) {
                jSONObjectBaseReport.put("healthStart", LoadMonitor.INSTANCE.healthJson(healthStats));
            }
            return jSONObjectBaseReport;
        }
    }
}
