package com.exteragram.messenger.plugins.pip;

import com.chaquo.python.internal.Common;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.url._UrlKt;
import org.lsposed.lsparanoid.Deobfuscator$exteraGramDev$TMessagesProj;
import org.mvel2.asm.signature.SignatureVisitor;
import org.simplifiles.SimpliFiles;
import org.simplifiles.archive.ArchiveExtractionOptions;
import org.simplifiles.archive.CancellationToken;
import org.simplifiles.archive.ExtractionTargetPolicy;
import org.simplifiles.archive.security.SecurityPolicy;
import org.simplifiles.files.OverwritePolicy;
import org.simplifiles.files.SimpliFile;
import org.telegram.messenger.FileLog;
import org.vosk.Model$$ExternalSyntheticBUOutline0;

@Metadata(d1 = {"\u0000®\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\"\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\bÇ\u0002\u0018\u00002\u00020\u0001:\u0006wxyz{|B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010,\u001a\u00020\u00102\u0006\u0010-\u001a\u00020\u0010H\u0002J\b\u0010.\u001a\u00020/H\u0002J&\u00100\u001a \u0012\u0004\u0012\u00020\u0010\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\u0010\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u0010020101H\u0002J.\u00103\u001a\u00020/2$\u00104\u001a \u0012\u0004\u0012\u00020\u0010\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\u0010\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u0010020101H\u0002J\b\u00105\u001a\u00020/H\u0002J\u0006\u00106\u001a\u00020/J.\u00108\u001a\b\u0012\u0004\u0012\u00020\u0010092\f\u0010:\u001a\b\u0012\u0004\u0012\u00020\u0010092\u0006\u0010;\u001a\u00020\u00102\n\b\u0002\u0010<\u001a\u0004\u0018\u00010=JV\u0010>\u001a\u00020/2\u0006\u0010?\u001a\u00020\u00102\u0018\u0010@\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A092\u0018\u0010B\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A0\u00112\u0006\u0010;\u001a\u00020\u00102\b\u0010<\u001a\u0004\u0018\u00010=H\u0002J*\u0010C\u001a\u00020/2\u0006\u0010;\u001a\u00020\u00102\u0018\u0010D\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A02H\u0002J\b\u0010E\u001a\u00020/H\u0002J\b\u0010F\u001a\u00020/H\u0002J\f\u0010G\u001a\b\u0012\u0004\u0012\u00020\u001002J\u000e\u0010H\u001a\u00020/2\u0006\u0010;\u001a\u00020\u0010J4\u0010I\u001a\u00020\u00102\u0006\u0010?\u001a\u00020\u00102\u0018\u0010@\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A092\b\u0010<\u001a\u0004\u0018\u00010=H\u0002J\"\u0010J\u001a\u0004\u0018\u00010K2\u0006\u0010L\u001a\u00020\u00102\u0006\u0010M\u001a\u00020N2\u0006\u0010O\u001a\u00020PH\u0002J\u0016\u0010Q\u001a\u0004\u0018\u00010\u0010*\u00020N2\u0006\u0010-\u001a\u00020\u0010H\u0002J\u001b\u0010R\u001a\u0004\u0018\u00010P*\u00020N2\u0006\u0010-\u001a\u00020\u0010H\u0002¢\u0006\u0002\u0010SJ\u0010\u0010T\u001a\u00020P2\u0006\u0010U\u001a\u00020\u0010H\u0002J\u0010\u0010V\u001a\u00020P2\u0006\u0010W\u001a\u00020\u0010H\u0002J\u001a\u0010X\u001a\u00020Y2\u0006\u0010Z\u001a\u00020[2\b\u0010<\u001a\u0004\u0018\u00010=H\u0002J,\u0010\\\u001a\u0004\u0018\u00010\u00102\u0006\u0010?\u001a\u00020\u00102\u0018\u0010@\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A09H\u0002J,\u0010]\u001a\u0004\u0018\u00010\u00102\u0006\u0010?\u001a\u00020\u00102\u0018\u0010@\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A09H\u0002J\u0018\u0010^\u001a\u00020\t2\u0006\u0010?\u001a\u00020\u00102\u0006\u0010L\u001a\u00020\u0010H\u0002J\u0018\u0010_\u001a\u00020/2\u0006\u0010?\u001a\u00020\u00102\u0006\u0010L\u001a\u00020\u0010H\u0002J.\u0010`\u001a \u0012\u0004\u0012\u00020\u0010\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A090A2\u0006\u0010a\u001a\u00020\u0010H\u0002J\"\u0010b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A092\u0006\u0010c\u001a\u00020\u0010H\u0002J\u0016\u0010d\u001a\b\u0012\u0004\u0012\u00020\u0010092\u0006\u0010e\u001a\u00020\tH\u0002J\u0010\u0010f\u001a\u00020P2\u0006\u0010g\u001a\u00020\u0010H\u0002J\u001a\u0010h\u001a\u0004\u0018\u00010\t2\u0006\u0010?\u001a\u00020\u00102\u0006\u0010L\u001a\u00020\u0010H\u0002J*\u0010i\u001a\u00020P2\u0006\u0010L\u001a\u00020\u00102\u0018\u0010@\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A09H\u0002J\u0010\u0010j\u001a\u00020P2\u0006\u0010k\u001a\u00020\u0010H\u0002J6\u0010l\u001a\b\u0012\u0004\u0012\u00020\u0010092\f\u0010m\u001a\b\u0012\u0004\u0012\u00020\u0010092\u0018\u0010@\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A09H\u0002J\"\u0010n\u001a\u00020P2\u0018\u0010@\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100A09H\u0002J\u0010\u0010o\u001a\u00020P2\u0006\u0010L\u001a\u00020\u0010H\u0002J\u0018\u0010p\u001a\u00020P2\u0006\u0010L\u001a\u00020\u00102\u0006\u0010k\u001a\u00020\u0010H\u0002J\u0016\u0010q\u001a\b\u0012\u0004\u0012\u00020r092\u0006\u0010L\u001a\u00020\u0010H\u0002J\u0010\u0010s\u001a\u00020\u00102\u0006\u0010t\u001a\u00020\tH\u0002J\u0010\u0010u\u001a\u00020v2\u0006\u0010L\u001a\u00020\u0010H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\u00020\t8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\t8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\u000bR4\u0010\u000e\u001a(\u0012\u0004\u0012\u00020\u0010\u0012\u001a\u0012\u0018\u0012\u0004\u0012\u00020\u0010\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00110\u000fj\u0002`\u00120\u000fj\u0002`\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0017\u001a\u00020\u0010X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u000e\u0010\u001c\u001a\u00020\u001dX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001dX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u001dX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0010X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0010X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0010X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020$X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020$X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020$X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010'\u001a\u00020$X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020$X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020$X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020$X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020$X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u00107\u001a\b\u0012\u0004\u0012\u00020\u001002X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006}"}, d2 = {"Lcom/exteragram/messenger/plugins/pip/PipController;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "client", "Lokhttp3/OkHttpClient;", "gson", "Lcom/google/gson/Gson;", "libsDir", "Ljava/io/File;", "getLibsDir", "()Ljava/io/File;", "registryFile", "getRegistryFile", "registry", "Ljava/util/concurrent/ConcurrentHashMap;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/pip/VersionMap;", "Lcom/exteragram/messenger/plugins/pip/RegistryMap;", "installLocks", "wheelArchivePolicy", "Lorg/simplifiles/archive/security/SecurityPolicy;", "pythonVersion", "getPythonVersion", "()Ljava/lang/String;", "setPythonVersion", "(Ljava/lang/String;)V", "MAX_REGISTRY_BYTES", _UrlKt.FRAGMENT_ENCODE_SET, "MAX_METADATA_BYTES", "MAX_WHEEL_BYTES", "ENV_SYS_PLATFORM", "ENV_PLATFORM_SYSTEM", "ENV_OS_NAME", "REGEX_NORMALIZE", "Lkotlin/text/Regex;", "REGEX_REQ_PARSE", "REGEX_REQ_SPECS", "REGEX_REQ_EXTRA", "REGEX_REQ_PAREN", "REGEX_VERSION_SPLIT", "REGEX_MARKER_TOKEN", "REGEX_VERSION_WILDCARD", "normalizePackageName", "name", "loadRegistry", _UrlKt.FRAGMENT_ENCODE_SET, "snapshotRegistry", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "restoreRegistry", "snapshot", "saveRegistry", "cleanup", "PREINSTALLED_PACKAGES", "installDependencies", _UrlKt.FRAGMENT_ENCODE_SET, Common.ASSET_REQUIREMENTS, "pluginId", "delegate", "Lcom/exteragram/messenger/plugins/pip/PipController$InstallerDelegate;", "resolveAndInstall", "pkg", "specs", "Lkotlin/Pair;", "installedAccumulator", "updateRegistryForPlugin", "currentlyNeeded", "cleanupInternal", "removeOrphanedDirectories", "activeLibraryPaths", "uninstallDependencies", "installPackage", "selectWheelCandidate", "Lcom/exteragram/messenger/plugins/pip/PipController$WheelCandidate;", "version", "artifact", "Lcom/google/gson/JsonObject;", "allowYanked", _UrlKt.FRAGMENT_ENCODE_SET, "getStringOrNull", "getBooleanOrNull", "(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/Boolean;", "isPurePythonWheelCompatible", "filename", "isPythonTagCompatible", "tag", "executeWithRetry", "Lokhttp3/Response;", "request", "Lokhttp3/Request;", "findVersionOnDisk", "findInstalledVersion", "getLibPath", "deletePackage", "parseRequirement", "req", "parseSpecs", "specsString", "parseDependenciesFromMetadata", "metadataFile", "isMarkerCompatible", "marker", "findMetadataFile", "checkVersionSatisfies", "isWildcardVersionSpec", "spec", "filterPreReleases", "versions", "specsAllowPreRelease", "isPreReleaseVersion", "matchesVersionWildcard", "parseVersionReleaseParts", _UrlKt.FRAGMENT_ENCODE_SET, "calculateSha256", "file", "parseVersion", "Lcom/exteragram/messenger/plugins/pip/PipController$ParsedVersion;", "InstallerDelegate", "WheelCandidate", "ParsedVersion", "MarkerParser", "SizeLimitedInputStream", "VersionComparator", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nPipController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PipController.kt\ncom/exteragram/messenger/plugins/pip/PipController\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 6 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,1117:1\n221#2:1118\n221#2,2:1119\n222#2:1121\n221#2:1133\n221#2,2:1134\n222#2:1136\n221#2:1140\n221#2,2:1141\n222#2:1143\n221#2:1146\n221#2,2:1147\n222#2:1149\n221#2:1152\n222#2:1155\n221#2:1158\n221#2,2:1159\n222#2:1161\n221#2:1162\n221#2,2:1163\n222#2:1165\n493#3:1122\n442#3:1123\n493#3:1126\n442#3:1127\n1266#4,2:1124\n1266#4,4:1128\n1269#4:1132\n296#4,2:1137\n1915#4,2:1144\n1915#4,2:1150\n1915#4,2:1153\n1915#4,2:1166\n777#4:1168\n873#4,2:1169\n1807#4,3:1171\n1807#4,3:1174\n777#4:1177\n873#4,2:1178\n832#4:1180\n862#4,2:1181\n1807#4,3:1183\n1807#4,3:1186\n1786#4,3:1189\n1642#4,10:1192\n1915#4:1202\n1916#4:1204\n1652#4:1205\n1586#4:1206\n1661#4,3:1207\n1#5:1139\n1#5:1203\n14048#6,2:1156\n*S KotlinDebug\n*F\n+ 1 PipController.kt\ncom/exteragram/messenger/plugins/pip/PipController\n*L\n120#1:1118\n123#1:1119,2\n120#1:1121\n154#1:1133\n156#1:1134,2\n154#1:1136\n306#1:1140\n307#1:1141,2\n306#1:1143\n330#1:1146\n331#1:1147,2\n330#1:1149\n351#1:1152\n351#1:1155\n376#1:1158\n377#1:1159,2\n376#1:1161\n394#1:1162\n395#1:1163,2\n394#1:1165\n146#1:1122\n146#1:1123\n147#1:1126\n147#1:1127\n146#1:1124,2\n147#1:1128,4\n146#1:1132\n238#1:1137,2\n318#1:1144,2\n338#1:1150,2\n352#1:1153,2\n405#1:1166,2\n449#1:1168\n449#1:1169,2\n453#1:1171,3\n593#1:1174,3\n647#1:1177\n647#1:1178,2\n962#1:1180\n962#1:1181,2\n967#1:1183,3\n971#1:1186,3\n1000#1:1189,3\n1006#1:1192,10\n1006#1:1202\n1006#1:1204\n1006#1:1205\n180#1:1206\n180#1:1207,3\n1006#1:1203\n360#1:1156,2\n*E\n"})
public final class PipController {
    public static final PipController INSTANCE;
    private static final long MAX_METADATA_BYTES = 4194304;
    private static final long MAX_REGISTRY_BYTES = 4194304;
    private static final long MAX_WHEEL_BYTES = 262144000;
    private static final Set<String> PREINSTALLED_PACKAGES;
    private static final Regex REGEX_MARKER_TOKEN;
    private static final Regex REGEX_NORMALIZE;
    private static final Regex REGEX_REQ_EXTRA;
    private static final Regex REGEX_REQ_PAREN;
    private static final Regex REGEX_REQ_PARSE;
    private static final Regex REGEX_REQ_SPECS;
    private static final Regex REGEX_VERSION_SPLIT;
    private static final Regex REGEX_VERSION_WILDCARD;
    private static final OkHttpClient client;
    private static final Gson gson;
    private static final ConcurrentHashMap<String, Object> installLocks;
    private static String pythonVersion;
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Set<String>>> registry;
    private static final SecurityPolicy wheelArchivePolicy;
    private static final String ENV_SYS_PLATFORM = "linux";
    private static final String ENV_PLATFORM_SYSTEM = "Linux";
    private static final String ENV_OS_NAME = "posix";

    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&¨\u0006\bÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/pip/PipController$InstallerDelegate;", _UrlKt.FRAGMENT_ENCODE_SET, "onProgress", _UrlKt.FRAGMENT_ENCODE_SET, "text", _UrlKt.FRAGMENT_ENCODE_SET, "isCancelled", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface InstallerDelegate {
        boolean isCancelled();

        void onProgress(String text);
    }

    private PipController() {
    }

    static {
        PipController pipController = new PipController();
        INSTANCE = pipController;
        client = ExteraHttpClient.INSTANCE.getClient();
        gson = new Gson();
        registry = new ConcurrentHashMap<>();
        installLocks = new ConcurrentHashMap<>();
        wheelArchivePolicy = SecurityPolicy.INSTANCE.builder().maxEntries(50000L).maxTotalUncompressedSize(524288000L).maxSingleFileSize(MAX_WHEEL_BYTES).maxCompressionRatio(500.0d).build();
        pythonVersion = "3.11.10";
        REGEX_NORMALIZE = new Regex("[-_.]+");
        REGEX_REQ_PARSE = new Regex("^([a-zA-Z0-9_.-]+)(.*)");
        REGEX_REQ_SPECS = new Regex("^(===|==|>=|<=|>|<|~=|!=)\\s*(.*)");
        REGEX_REQ_EXTRA = new Regex("\\[.*?]");
        REGEX_REQ_PAREN = new Regex("[()]");
        REGEX_VERSION_SPLIT = new Regex("([0-9]+)|([a-zA-Z]+)");
        REGEX_MARKER_TOKEN = new Regex("(\"[^\"]*\"|'[^']*'|===|==|!=|>=|<=|~=|>|<|\\(|\\)|\\b(?:and|or|in|not)\\b|[A-Za-z_][A-Za-z0-9_]*|\\S+)");
        REGEX_VERSION_WILDCARD = new Regex("(?i)(?:^|[.\\-_])\\*$");
        pipController.loadRegistry();
        Set of = SetsKt.setOf((Object[]) new String[]{"beautifulsoup4", "debugpy", "lxml", "packaging", "pillow", "requests", "pyyaml"});
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(of, 10));
        Iterator it = of.iterator();
        while (it.hasNext()) {
            arrayList.add(INSTANCE.normalizePackageName((String) it.next()));
        }
        PREINSTALLED_PACKAGES = CollectionsKt.toSet(arrayList);
    }

    private final File getLibsDir() {
        return SimpliFiles.directory(new File(PluginsController.INSTANCE.getInstance().getPluginsDir(), "libs")).create().getFile();
    }

    private final File getRegistryFile() {
        return new File(getLibsDir(), "registry.json");
    }

    public final String getPythonVersion() {
        return pythonVersion;
    }

    public final void setPythonVersion(String str) {
        "<set-?>";
        pythonVersion = str;
    }

    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u000e\u001a\u0004\u0018\u00010\u0003HÆ\u0003J)\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003HÆ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0013\u001a\u00020\u0014HÖ\u0001J\t\u0010\u0015\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t¨\u0006\u0016"}, d2 = {"Lcom/exteragram/messenger/plugins/pip/PipController$WheelCandidate;", _UrlKt.FRAGMENT_ENCODE_SET, "version", _UrlKt.FRAGMENT_ENCODE_SET, "downloadUrl", "expectedSha256", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getVersion", "()Ljava/lang/String;", "getDownloadUrl", "getExpectedSha256", "component1", "component2", "component3", "copy", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final List installDependencies$default(PipController pipController, List list, String str, InstallerDelegate installerDelegate, int i, Object obj) {
        if ((i & 4) != 0) {
            installerDelegate = null;
        }
        return pipController.installDependencies(list, str, installerDelegate);
    }

    public final List<String> installDependencies(List<String> requirements, String pluginId, InstallerDelegate delegate) {
        PipController pipController;
        String str;
        Exception exc;
        "requirements";
        "pluginId";
        ArrayList arrayList = new ArrayList();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        Map<String, Map<String, Set<String>>> mapSnapshotRegistry = snapshotRegistry();
        try {
            try {
                for (String str2 : requirements) {
                    if (delegate != null) {
                        try {
                            if (delegate.isCancelled()) {
                                throw new IOException("Installation cancelled");
                            }
                        } catch (Exception e) {
                            exc = e;
                            pipController = this;
                            str = pluginId;
                            FileLog.e("PipController: Failed to install dependencies for " + str + ": " + requirements, exc);
                            pipController.restoreRegistry(mapSnapshotRegistry);
                            pipController.removeOrphanedDirectories();
                            throw exc;
                        }
                    }
                    if (!StringsKt.isBlank(str2)) {
                        Pair<String, List<Pair<String, String>>> requirement = this.parseRequirement(str2);
                        String strComponent1 = requirement.component1();
                        List<Pair<String, String>> listComponent2 = requirement.component2();
                        String strNormalizePackageName = this.normalizePackageName(strComponent1);
                        if (!PREINSTALLED_PACKAGES.contains(strNormalizePackageName)) {
                            pipController = this;
                            str = pluginId;
                            InstallerDelegate installerDelegate = delegate;
                            try {
                                pipController.resolveAndInstall(strNormalizePackageName, listComponent2, linkedHashSet, str, installerDelegate);
                                this = pipController;
                                pluginId = str;
                                delegate = installerDelegate;
                            } catch (Exception e2) {
                                e = e2;
                                exc = e;
                                FileLog.e("PipController: Failed to install dependencies for " + str + ": " + requirements, exc);
                                pipController.restoreRegistry(mapSnapshotRegistry);
                                pipController.removeOrphanedDirectories();
                                throw exc;
                            }
                        }
                    }
                }
                pipController = this;
                str = pluginId;
                pipController.updateRegistryForPlugin(str, linkedHashSet);
                for (Pair<String, String> pair : linkedHashSet) {
                    String absolutePath = pipController.getLibPath(pair.component1(), pair.component2()).getAbsolutePath();
                    "getAbsolutePath(...)";
                    arrayList.add(absolutePath);
                }
                pipController.saveRegistry();
                installLocks.clear();
                return arrayList;
            } catch (Throwable th) {
                installLocks.clear();
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            pipController = this;
            str = pluginId;
        }
    }

    private final void resolveAndInstall(String pkg, List<Pair<String, String>> specs, Set<Pair<String, String>> installedAccumulator, String pluginId, InstallerDelegate delegate) throws IOException {
        Object obj;
        Map.Entry entry;
        String strInstallPackage;
        Set<Map.Entry<String, Set<String>>> setEntrySet;
        Object next;
        Object value;
        PipController pipController = this;
        Set<Pair<String, String>> set = installedAccumulator;
        String str = pluginId;
        InstallerDelegate installerDelegate = delegate;
        if (installerDelegate != null && installerDelegate.isCancelled()) {
            Model$$ExternalSyntheticBUOutline0.m("Installation cancelled");
            return;
        }
        ConcurrentHashMap<String, Object> concurrentHashMap = installLocks;
        final Function1 function1 = new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj2) {
                return PipController.$r8$lambda$nM3N8RYnbiCU76VyVnmjZ3F9qY8((String) obj2);
            }
        };
        Object objComputeIfAbsent = concurrentHashMap.computeIfAbsent(pkg, new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj2) {
                return function1.invoke(obj2);
            }
        });
        "computeIfAbsent(...)";
        synchronized (objComputeIfAbsent) {
            if (installerDelegate != null) {
                try {
                    if (installerDelegate.isCancelled()) {
                        throw new IOException("Installation cancelled");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            ConcurrentHashMap<String, Set<String>> concurrentHashMap2 = registry.get(pkg);
            obj = null;
            if (concurrentHashMap2 == null || (setEntrySet = concurrentHashMap2.entrySet()) == null) {
                entry = null;
            } else {
                Iterator<T> it = setEntrySet.iterator();
                do {
                    if (!it.hasNext()) {
                        next = null;
                        break;
                    } else {
                        next = it.next();
                        value = ((Map.Entry) next).getValue();
                        "<get-value>(...)";
                    }
                } while (((Collection) value).isEmpty());
                entry = (Map.Entry) next;
            }
            if (entry != null) {
                Object key = entry.getKey();
                "<get-key>(...)";
                strInstallPackage = (String) key;
                Object value2 = entry.getValue();
                "<get-value>(...)";
                String strJoinToString$default = CollectionsKt.joinToString$default((Iterable) value2, ", ", null, null, 0, null, null, 62, null);
                if (!INSTANCE.checkVersionSatisfies(strInstallPackage, specs)) {
                    throw new IOException("Dependency conflict for '" + pkg + "': Active version " + strInstallPackage + " (used by " + strJoinToString$default + ") does not satisfy requirement '" + (!specs.isEmpty() ? CollectionsKt.joinToString$default(specs, ",", null, null, 0, null, new Function1() { 
                        @Override // kotlin.jvm.functions.Function1
                        public final Object invoke(Object obj2) {
                            return PipController.resolveAndInstall$lambda$2$1((Pair) obj2);
                        }
                    }, 30, null) : "(latest)") + "'.");
                }
                FileLog.d("PipController: Reusing active version " + strInstallPackage + " of " + pkg + " (used by " + strJoinToString$default + ") for " + str);
            } else {
                PipController pipController2 = INSTANCE;
                String strFindInstalledVersion = pipController2.findInstalledVersion(pkg, specs);
                if (strFindInstalledVersion != null) {
                    strInstallPackage = strFindInstalledVersion;
                } else {
                    strFindInstalledVersion = pipController2.findVersionOnDisk(pkg, specs);
                    if (strFindInstalledVersion != null) {
                        FileLog.d("PipController: Found " + pkg + ' ' + strFindInstalledVersion + " on disk, adopting.");
                        strInstallPackage = strFindInstalledVersion;
                    } else {
                        strInstallPackage = pipController2.installPackage(pkg, specs, installerDelegate);
                    }
                }
            }
        }
        for (Object obj2 : set) {
            if (Intrinsics.areEqual(((Pair) obj2).getFirst(), pkg)) {
                obj = obj2;
                break;
            }
        }
        Pair pair = (Pair) obj;
        if (pair != null) {
            String str2 = (String) pair.getSecond();
            if (VersionComparator.INSTANCE.compare(strInstallPackage, str2) <= 0) {
                return;
            }
            FileLog.d("PipController: Upgrading " + pkg + " from " + str2 + " to " + strInstallPackage + " for " + str);
            set.remove(pair);
        }
        ConcurrentHashMap<String, ConcurrentHashMap<String, Set<String>>> concurrentHashMap3 = registry;
        final Function1 function2 = new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj3) {
                return PipController.m1329$r8$lambda$cKu7WIfB65dGzootwJY_PzblQ((String) obj3);
            }
        };
        ConcurrentHashMap<String, Set<String>> concurrentHashMapComputeIfAbsent = concurrentHashMap3.computeIfAbsent(pkg, new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj3) {
                return PipController.m1326$r8$lambda$4GaAqKHHkDxhUEp6Ixv2955PQ8(function2, obj3);
            }
        });
        final Function1 function3 = new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj3) {
                return PipController.$r8$lambda$ba_CzsM0FMRcDjro_ht7norbT1M((String) obj3);
            }
        };
        concurrentHashMapComputeIfAbsent.computeIfAbsent(strInstallPackage, new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj3) {
                return PipController.$r8$lambda$XFN9dt32lYY3xCJ9NHZLBcosePw(function3, obj3);
            }
        }).add(str);
        set.add(TuplesKt.to(pkg, strInstallPackage));
        File fileFindMetadataFile = pipController.findMetadataFile(pkg, strInstallPackage);
        if (fileFindMetadataFile == null || !fileFindMetadataFile.exists()) {
            return;
        }
        for (String str3 : pipController.parseDependenciesFromMetadata(fileFindMetadataFile)) {
            if (installerDelegate != null && installerDelegate.isCancelled()) {
                Model$$ExternalSyntheticBUOutline0.m("Installation cancelled");
                return;
            }
            Pair<String, List<Pair<String, String>>> requirement = pipController.parseRequirement(str3);
            pipController.resolveAndInstall(pipController.normalizePackageName(requirement.component1()), requirement.component2(), set, str, installerDelegate);
            pipController = this;
            set = installedAccumulator;
            str = pluginId;
            installerDelegate = delegate;
        }
    }

    public static Object $r8$lambda$nM3N8RYnbiCU76VyVnmjZ3F9qY8(String str) {
        "it";
        return new Object();
    }

    public static final CharSequence resolveAndInstall$lambda$2$1(Pair pair) {
        "it";
        return ((String) pair.getFirst()) + ((String) pair.getSecond());
    }

    public static ConcurrentHashMap m1326$r8$lambda$4GaAqKHHkDxhUEp6Ixv2955PQ8(Function1 function1, Object obj) {
        return (ConcurrentHashMap) function1.invoke(obj);
    }

    public static ConcurrentHashMap m1329$r8$lambda$cKu7WIfB65dGzootwJY_PzblQ(String str) {
        "it";
        return new ConcurrentHashMap();
    }

    public static Set $r8$lambda$XFN9dt32lYY3xCJ9NHZLBcosePw(Function1 function1, Object obj) {
        return (Set) function1.invoke(obj);
    }

    public static Set $r8$lambda$ba_CzsM0FMRcDjro_ht7norbT1M(String str) {
        "it";
        return Collections.newSetFromMap(new ConcurrentHashMap());
    }

    private final void updateRegistryForPlugin(String pluginId, Set<Pair<String, String>> currentlyNeeded) throws IOException {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, ConcurrentHashMap<String, Set<String>>> entry : registry.entrySet()) {
            String key = entry.getKey();
            for (Map.Entry<String, Set<String>> entry2 : entry.getValue().entrySet()) {
                String key2 = entry2.getKey();
                Set<String> value = entry2.getValue();
                Pair pair = TuplesKt.to(key, key2);
                if (value.contains(pluginId) && !currentlyNeeded.contains(pair)) {
                    value.remove(pluginId);
                    if (value.isEmpty()) {
                        arrayList.add(pair);
                    }
                }
            }
        }
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            Pair pair2 = (Pair) obj;
            String str = (String) pair2.component1();
            String str2 = (String) pair2.component2();
            ConcurrentHashMap<String, ConcurrentHashMap<String, Set<String>>> concurrentHashMap = registry;
            ConcurrentHashMap<String, Set<String>> concurrentHashMap2 = concurrentHashMap.get(str);
            if (concurrentHashMap2 != null) {
                concurrentHashMap2.remove(str2);
            }
            ConcurrentHashMap<String, Set<String>> concurrentHashMap3 = concurrentHashMap.get(str);
            if (concurrentHashMap3 != null && concurrentHashMap3.isEmpty()) {
                concurrentHashMap.remove(str);
            }
            INSTANCE.deletePackage(str, str2);
        }
    }

    private final void cleanupInternal() throws IOException {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, ConcurrentHashMap<String, Set<String>>> entry : registry.entrySet()) {
            String key = entry.getKey();
            for (Map.Entry<String, Set<String>> entry2 : entry.getValue().entrySet()) {
                String key2 = entry2.getKey();
                if (entry2.getValue().isEmpty()) {
                    arrayList.add(TuplesKt.to(key, key2));
                }
            }
        }
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            Pair pair = (Pair) obj;
            String str = (String) pair.component1();
            String str2 = (String) pair.component2();
            ConcurrentHashMap<String, ConcurrentHashMap<String, Set<String>>> concurrentHashMap = registry;
            ConcurrentHashMap<String, Set<String>> concurrentHashMap2 = concurrentHashMap.get(str);
            if (concurrentHashMap2 != null) {
                concurrentHashMap2.remove(str2);
            }
            ConcurrentHashMap<String, Set<String>> concurrentHashMap3 = concurrentHashMap.get(str);
            if (concurrentHashMap3 != null && concurrentHashMap3.isEmpty()) {
                concurrentHashMap.remove(str);
            }
            INSTANCE.deletePackage(str, str2);
        }
        removeOrphanedDirectories();
    }

    private final void removeOrphanedDirectories() {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (Map.Entry<String, ConcurrentHashMap<String, Set<String>>> entry : registry.entrySet()) {
            String key = entry.getKey();
            Set<String> setKeySet = entry.getValue().keySet();
            "<get-keys>(...)";
            Iterator<T> it = setKeySet.iterator();
            while (it.hasNext()) {
                try {
                    linkedHashSet.add(INSTANCE.getLibPath(key, (String) it.next()).getCanonicalPath());
                } catch (IOException unused) {
                }
            }
        }
        File[] fileArrListFiles = getLibsDir().listFiles();
        if (fileArrListFiles != null) {
            for (File file : fileArrListFiles) {
                if (file.isDirectory()) {
                    try {
                        if (!linkedHashSet.contains(file.getCanonicalPath())) {
                            FileLog.d("PipController: Removing orphaned library: " + file.getName());
                            SimpliFiles.directory(file).deleteRecursively();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        }
    }

    public final Set<String> activeLibraryPaths() {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (Map.Entry<String, ConcurrentHashMap<String, Set<String>>> entry : registry.entrySet()) {
            String key = entry.getKey();
            for (Map.Entry<String, Set<String>> entry2 : entry.getValue().entrySet()) {
                String key2 = entry2.getKey();
                if (!entry2.getValue().isEmpty()) {
                    try {
                        linkedHashSet.add(INSTANCE.getLibPath(key, key2).getAbsolutePath());
                    } catch (IOException e) {
                        FileLog.e("PipController: Failed to resolve path for " + key + ' ' + key2, e);
                    }
                }
            }
        }
        return linkedHashSet;
    }

    public final void uninstallDependencies(String pluginId) {
        "pluginId";
        ArrayList arrayList = new ArrayList();
        int i = 0;
        boolean z = false;
        for (Map.Entry<String, ConcurrentHashMap<String, Set<String>>> entry : registry.entrySet()) {
            String key = entry.getKey();
            for (Map.Entry<String, Set<String>> entry2 : entry.getValue().entrySet()) {
                String key2 = entry2.getKey();
                Set<String> value = entry2.getValue();
                if (value.remove(pluginId)) {
                    if (value.isEmpty()) {
                        arrayList.add(TuplesKt.to(key, key2));
                    }
                    z = true;
                }
            }
        }
        int size = arrayList.size();
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            Pair pair = (Pair) obj;
            String str = (String) pair.component1();
            String str2 = (String) pair.component2();
            ConcurrentHashMap<String, ConcurrentHashMap<String, Set<String>>> concurrentHashMap = registry;
            ConcurrentHashMap<String, Set<String>> concurrentHashMap2 = concurrentHashMap.get(str);
            if (concurrentHashMap2 != null) {
                concurrentHashMap2.remove(str2);
            }
            ConcurrentHashMap<String, Set<String>> concurrentHashMap3 = concurrentHashMap.get(str);
            if (concurrentHashMap3 != null && concurrentHashMap3.isEmpty()) {
                concurrentHashMap.remove(str);
            }
            INSTANCE.deletePackage(str, str2);
        }
        if (z) {
            saveRegistry();
        }
    }

    public static CharSequence m1330$r8$lambda$iez_jkcSyBPRjplKRoDAzlgN2I(Pair pair) {
        "it";
        return ((String) pair.getFirst()) + ((String) pair.getSecond());
    }

    private final String installPackage(String pkg, List<Pair<String, String>> specs, final InstallerDelegate delegate) throws IOException {
        Object objM2315constructorimpl;
        Object objM2315constructorimpl2;
        FileLog.d("PipController: Installing " + pkg + ' ' + (!specs.isEmpty() ? CollectionsKt.joinToString$default(specs, ",", null, null, 0, null, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return PipController.m1330$r8$lambda$iez_jkcSyBPRjplKRoDAzlgN2I((Pair) obj);
            }
        }, 30, null) : "(latest)"));
        if (delegate != null) {
            delegate.onProgress("Resolving " + pkg + "...");
        }
        Response responseExecuteWithRetry = executeWithRetry(new Request.Builder().url("https://pypi.org/pypi/" + pkg + "/json").build(), delegate);
        try {
            if (!responseExecuteWithRetry.getIsSuccessful()) {
                if (responseExecuteWithRetry.code() == 404) {
                    throw new IOException("Package " + pkg + " not found on PyPI");
                }
                throw new IOException("Failed to fetch metadata for " + pkg + ": " + responseExecuteWithRetry.code());
            }
            JsonObject jsonObject = (JsonObject) gson.fromJson(responseExecuteWithRetry.body().string(), JsonObject.class);
            if (jsonObject == null) {
                throw new IOException("Invalid metadata response from PyPI for " + pkg);
            }
            JsonObject asJsonObject = jsonObject.getAsJsonObject("releases");
            if (asJsonObject == null) {
                throw new IOException("No releases found for " + pkg + " on PyPI");
            }
            Set<String> setKeySet = asJsonObject.keySet();
            "keySet(...)";
            List list = CollectionsKt.toList(setKeySet);
            PipController pipController = INSTANCE;
            ArrayList arrayList = new ArrayList();
            for (Object obj : list) {
                if (INSTANCE.checkVersionSatisfies((String) obj, specs)) {
                    arrayList.add(obj);
                }
            }
            List<String> listSortedWith = CollectionsKt.sortedWith(pipController.filterPreReleases(arrayList, specs), new Comparator() { 
                @Override // java.util.Comparator
                public final int compare(Object obj2, Object obj3) {
                    return PipController.installPackage$lambda$1$1((String) obj2, (String) obj3);
                }
            });
            List<Pair<String, String>> list2 = specs;
            boolean z = false;
            if (!(list2 instanceof Collection) || !list2.isEmpty()) {
                Iterator<T> it = list2.iterator();
                while (it.hasNext()) {
                    Pair pair = (Pair) it.next();
                    if (Intrinsics.areEqual(pair.getFirst(), "==") || Intrinsics.areEqual(pair.getFirst(), "===")) {
                        z = true;
                        break;
                    }
                }
            }
            WheelCandidate wheelCandidate = null;
            for (String str : listSortedWith) {
                JsonArray asJsonArray = asJsonObject.getAsJsonArray(str);
                if (asJsonArray != null) {
                    Iterator<JsonElement> it2 = asJsonArray.iterator();
                    "iterator(...)";
                    while (it2.hasNext()) {
                        WheelCandidate wheelCandidateSelectWheelCandidate = INSTANCE.selectWheelCandidate(str, it2.next().getAsJsonObject(), z);
                        if (wheelCandidateSelectWheelCandidate != null) {
                            wheelCandidate = wheelCandidateSelectWheelCandidate;
                            break;
                        }
                    }
                    if (wheelCandidate != null) {
                        break;
                    }
                }
            }
            if (wheelCandidate == null) {
                JsonObject asJsonObject2 = jsonObject.getAsJsonObject("info");
                String stringOrNull = asJsonObject2 != null ? INSTANCE.getStringOrNull(asJsonObject2, "requires_python") : null;
                if (stringOrNull != null) {
                    PipController pipController2 = INSTANCE;
                    if (!pipController2.checkVersionSatisfies(pythonVersion, pipController2.parseSpecs(stringOrNull))) {
                        throw new IOException("Package " + pkg + " requires Python " + stringOrNull + ", but current is " + pythonVersion);
                    }
                }
            }
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(responseExecuteWithRetry, null);
            if (wheelCandidate == null) {
                throw new IOException("No pure-Python wheel found for " + pkg + ". Binary packages are not supported.");
            }
            File libPath = getLibPath(pkg, wheelCandidate.getVersion());
            File file = SimpliFiles.directory(new File(getLibsDir(), "tmp_" + pkg + '_' + System.currentTimeMillis())).create().getFile();
            File file2 = new File(file, "extracted");
            try {
                if (delegate != null) {
                    try {
                        delegate.onProgress("Downloading " + pkg + ' ' + wheelCandidate.getVersion() + "...");
                    } catch (Exception e) {
                        FileLog.e("PipController: Failed to download/install " + pkg, e);
                        throw e;
                    }
                }
                File file3 = new File(file, "package.whl");
                Response responseExecuteWithRetry2 = executeWithRetry(new Request.Builder().url(wheelCandidate.getDownloadUrl()).build(), delegate);
                try {
                    if (!responseExecuteWithRetry2.getIsSuccessful()) {
                        throw new IOException("Download failed: " + responseExecuteWithRetry2.code());
                    }
                    long contentLength = responseExecuteWithRetry2.body().getContentLength();
                    if (contentLength > MAX_WHEEL_BYTES) {
                        throw new IOException("Wheel for " + pkg + " is too large: " + contentLength + " bytes (limit 262144000)");
                    }
                    SimpliFile.writeFrom$default(SimpliFiles.file(file3), new SizeLimitedInputStream(responseExecuteWithRetry2.body().byteStream(), MAX_WHEEL_BYTES), 0L, 2, null);
                    CloseableKt.closeFinally(responseExecuteWithRetry2, null);
                    if (wheelCandidate.getExpectedSha256() != null && !StringsKt.equals(calculateSha256(file3), wheelCandidate.getExpectedSha256(), true)) {
                        throw new IOException("Checksum mismatch");
                    }
                    if (delegate != null) {
                        delegate.onProgress("Extracting " + pkg + ' ' + wheelCandidate.getVersion() + "...");
                    }
                    SimpliFiles.archive(file3).withPolicy(wheelArchivePolicy).extractToDirectory(file2, ArchiveExtractionOptions.INSTANCE.builder().cancellationToken(new CancellationToken() { 
                        @Override // org.simplifiles.archive.CancellationToken
                        public final boolean isCancellationRequested() {
                            return PipController.$r8$lambda$WCaEarcnhdtxiKzUVomzfeStLN0(delegate);
                        }
                    }).targetPolicy(ExtractionTargetPolicy.REPLACE).build());
                    if (libPath.exists()) {
                        SimpliFiles.directory(libPath).deleteRecursively();
                    }
                    SimpliFiles.directory(file2).moveTo(libPath, OverwritePolicy.REPLACE);
                    try {
                        Result.Companion companion = Result.INSTANCE;
                        objM2315constructorimpl2 = Result.m2315constructorimpl(Boolean.valueOf(SimpliFiles.directory(file).deleteRecursively()));
                    } catch (Throwable th) {
                        Result.Companion companion2 = Result.INSTANCE;
                        objM2315constructorimpl2 = Result.m2315constructorimpl(ResultKt.createFailure(th));
                    }
                    Throwable thM2318exceptionOrNullimpl = Result.m2318exceptionOrNullimpl(objM2315constructorimpl2);
                    if (thM2318exceptionOrNullimpl != null) {
                        FileLog.e("PipController: Failed to delete temp package directory", thM2318exceptionOrNullimpl);
                    }
                    return wheelCandidate.getVersion();
                } catch (Throwable th2) {
                    try {
                        throw th2;
                    } catch (Throwable th3) {
                        CloseableKt.closeFinally(responseExecuteWithRetry2, th2);
                        throw th3;
                    }
                }
            } catch (Throwable th4) {
                try {
                    Result.Companion companion3 = Result.INSTANCE;
                    objM2315constructorimpl = Result.m2315constructorimpl(Boolean.valueOf(SimpliFiles.directory(file).deleteRecursively()));
                } catch (Throwable th5) {
                    Result.Companion companion4 = Result.INSTANCE;
                    objM2315constructorimpl = Result.m2315constructorimpl(ResultKt.createFailure(th5));
                }
                Throwable thM2318exceptionOrNullimpl2 = Result.m2318exceptionOrNullimpl(objM2315constructorimpl);
                if (thM2318exceptionOrNullimpl2 == null) {
                    throw th4;
                }
                FileLog.e("PipController: Failed to delete temp package directory", thM2318exceptionOrNullimpl2);
                throw th4;
            }
        } catch (Throwable th6) {
            try {
                throw th6;
            } catch (Throwable th7) {
                CloseableKt.closeFinally(responseExecuteWithRetry, th6);
                throw th7;
            }
        }
    }

    public static final int installPackage$lambda$1$1(String str, String str2) {
        return VersionComparator.INSTANCE.compare(str2, str);
    }

    public static boolean $r8$lambda$WCaEarcnhdtxiKzUVomzfeStLN0(InstallerDelegate installerDelegate) {
        return installerDelegate != null && installerDelegate.isCancelled();
    }

    private final WheelCandidate selectWheelCandidate(String version, JsonObject artifact, boolean allowYanked) {
        String stringOrNull;
        String stringOrNull2;
        String stringOrNull3 = getStringOrNull(artifact, "packagetype");
        if (stringOrNull3 == null || !Intrinsics.areEqual(stringOrNull3, "bdist_wheel")) {
            return null;
        }
        if ((!allowYanked && Intrinsics.areEqual(getBooleanOrNull(artifact, "yanked"), Boolean.TRUE)) || (stringOrNull = getStringOrNull(artifact, "filename")) == null || !isPurePythonWheelCompatible(stringOrNull)) {
            return null;
        }
        String stringOrNull4 = getStringOrNull(artifact, "requires_python");
        if ((stringOrNull4 != null && !checkVersionSatisfies(pythonVersion, parseSpecs(stringOrNull4))) || (stringOrNull2 = getStringOrNull(artifact, "url")) == null) {
            return null;
        }
        JsonObject asJsonObject = artifact.getAsJsonObject("digests");
        return new WheelCandidate(version, stringOrNull2, asJsonObject != null ? getStringOrNull(asJsonObject, "sha256") : null);
    }

    private final String getStringOrNull(JsonObject jsonObject, String str) {
        JsonElement jsonElement;
        if (!jsonObject.has(str) || (jsonElement = jsonObject.get(str)) == null || jsonElement.isJsonNull()) {
            return null;
        }
        return jsonElement.getAsString();
    }

    private final Boolean getBooleanOrNull(JsonObject jsonObject, String str) {
        JsonElement jsonElement;
        Object objM2315constructorimpl;
        if (!jsonObject.has(str) || (jsonElement = jsonObject.get(str)) == null || jsonElement.isJsonNull()) {
            return null;
        }
        try {
            Result.Companion companion = Result.INSTANCE;
            objM2315constructorimpl = Result.m2315constructorimpl(Boolean.valueOf(jsonElement.getAsBoolean()));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM2315constructorimpl = Result.m2315constructorimpl(ResultKt.createFailure(th));
        }
        return (Boolean) (Result.m2321isFailureimpl(objM2315constructorimpl) ? null : objM2315constructorimpl);
    }

    private final boolean isPurePythonWheelCompatible(String filename) {
        if (!StringsKt.endsWith$default(filename, ".whl", false, 2, (Object) null)) {
            return false;
        }
        List listSplit$default = StringsKt.split$default((CharSequence) StringsKt.removeSuffix(filename, (CharSequence) ".whl"), new String[]{"-"}, false, 0, 6, (Object) null);
        if (listSplit$default.size() < 5) {
            return false;
        }
        String str = (String) listSplit$default.get(listSplit$default.size() - 3);
        String str2 = (String) listSplit$default.get(listSplit$default.size() - 2);
        String str3 = (String) listSplit$default.get(listSplit$default.size() - 1);
        if (Intrinsics.areEqual(str2, "none") && Intrinsics.areEqual(str3, "any")) {
            List listSplit$default2 = StringsKt.split$default((CharSequence) str, new String[]{"."}, false, 0, 6, (Object) null);
            if ((listSplit$default2 instanceof Collection) && listSplit$default2.isEmpty()) {
                return false;
            }
            Iterator it = listSplit$default2.iterator();
            while (it.hasNext()) {
                if (INSTANCE.isPythonTagCompatible((String) it.next())) {
                    return true;
                }
            }
        }
        return false;
    }

    private final boolean isPythonTagCompatible(String tag) {
        Integer intOrNull;
        Integer intOrNull2;
        List listSplit$default = StringsKt.split$default((CharSequence) pythonVersion, new String[]{"."}, false, 0, 6, (Object) null);
        String str = (String) CollectionsKt.getOrNull(listSplit$default, 0);
        if (str != null && (intOrNull = StringsKt.toIntOrNull(str)) != null) {
            int iIntValue = intOrNull.intValue();
            String str2 = (String) CollectionsKt.getOrNull(listSplit$default, 1);
            if (str2 != null && (intOrNull2 = StringsKt.toIntOrNull(str2)) != null) {
                int iIntValue2 = intOrNull2.intValue();
                String lowerCase = tag.toLowerCase(Locale.ROOT);
                "toLowerCase(...)";
                if (Intrinsics.areEqual(lowerCase, "py" + iIntValue)) {
                    return true;
                }
                if (Intrinsics.areEqual(lowerCase, "cp" + iIntValue + iIntValue2)) {
                    return true;
                }
                Integer intOrNull3 = StringsKt.toIntOrNull(StringsKt.removePrefix(lowerCase, (CharSequence) ("py" + iIntValue)));
                if (intOrNull3 != null && intOrNull3.intValue() <= iIntValue2) {
                    return true;
                }
            }
        }
        return false;
    }

    private final Response executeWithRetry(Request request, InstallerDelegate delegate) throws IOException {
        int i = 0;
        IOException e = null;
        while (i < 3) {
            if (delegate != null && delegate.isCancelled()) {
                Model$$ExternalSyntheticBUOutline0.m("Installation cancelled");
                return null;
            }
            try {
                return client.newCall(request).execute();
            } catch (IOException e2) {
                e = e2;
                i++;
                FileLog.w("PipController: Network error, retrying (" + i + "/3): " + e.getMessage());
                try {
                    Thread.sleep(((long) i) * 1000);
                } catch (InterruptedException unused) {
                }
            }
        }
        if (delegate != null && delegate.isCancelled()) {
            Model$$ExternalSyntheticBUOutline0.m("Installation cancelled");
            return null;
        }
        if (e != null) {
            throw e;
        }
        throw new IOException("Unknown network error");
    }

    private final String findVersionOnDisk(String pkg, final List<Pair<String, String>> specs) {
        Sequence sequenceAsSequence;
        Sequence sequenceFilter;
        Sequence map;
        Sequence sequenceFilter2;
        List<String> list;
        final String str = pkg + SignatureVisitor.SUPER;
        File[] fileArrListFiles = getLibsDir().listFiles();
        if (fileArrListFiles == null || (sequenceAsSequence = ArraysKt.asSequence(fileArrListFiles)) == null || (sequenceFilter = SequencesKt.filter(sequenceAsSequence, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(PipController.m1325$r8$lambda$3qb6Nezx6TG2Tac1y7Xa35y3cA(str, (File) obj));
            }
        })) == null || (map = SequencesKt.map(sequenceFilter, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return PipController.m1328$r8$lambda$LmFvS3nVC4K08Dx0OuVVeARUQM(str, (File) obj);
            }
        })) == null || (sequenceFilter2 = SequencesKt.filter(map, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(PipController.m1327$r8$lambda$GBc8aKnKaBWkdqFW6gQZ9PEPeQ(specs, (String) obj));
            }
        })) == null || (list = SequencesKt.toList(sequenceFilter2)) == null) {
            return null;
        }
        return (String) CollectionsKt.maxWithOrNull(filterPreReleases(list, specs), VersionComparator.INSTANCE);
    }

    public static boolean m1325$r8$lambda$3qb6Nezx6TG2Tac1y7Xa35y3cA(String str, File file) {
        if (file.isDirectory()) {
            String name = file.getName();
            "getName(...)";
            if (StringsKt.startsWith$default(name, str, false, 2, (Object) null)) {
                return true;
            }
        }
        return false;
    }

    public static String m1328$r8$lambda$LmFvS3nVC4K08Dx0OuVVeARUQM(String str, File file) {
        String name = file.getName();
        "getName(...)";
        String strSubstring = name.substring(str.length());
        "substring(...)";
        return strSubstring;
    }

    public static boolean m1327$r8$lambda$GBc8aKnKaBWkdqFW6gQZ9PEPeQ(List list, String str) {
        "version";
        return str.length() > 0 && INSTANCE.checkVersionSatisfies(str, list);
    }

    private final String findInstalledVersion(String pkg, List<Pair<String, String>> specs) {
        ConcurrentHashMap<String, Set<String>> concurrentHashMap = registry.get(pkg);
        if (concurrentHashMap == null) {
            return null;
        }
        Set<String> setKeySet = concurrentHashMap.keySet();
        "<get-keys>(...)";
        ArrayList arrayList = new ArrayList();
        for (Object obj : setKeySet) {
            if (INSTANCE.checkVersionSatisfies((String) obj, specs)) {
                arrayList.add(obj);
            }
        }
        return (String) CollectionsKt.maxWithOrNull(filterPreReleases(arrayList, specs), VersionComparator.INSTANCE);
    }

    private final File getLibPath(String pkg, String version) throws IOException {
        if (StringsKt.contains$default((CharSequence) version, (CharSequence) "/", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) version, (CharSequence) "\\", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) version, (CharSequence) "..", false, 2, (Object) null)) {
            throw new IOException("Invalid version: " + version);
        }
        return new File(getLibsDir(), pkg + SignatureVisitor.SUPER + version);
    }

    private final void deletePackage(String pkg, String version) throws IOException {
        Object objM2315constructorimpl;
        File libPath = getLibPath(pkg, version);
        if (libPath.exists()) {
            try {
                Result.Companion companion = Result.INSTANCE;
                SimpliFiles.directory(libPath).deleteRecursively();
                FileLog.d("PipController: Deleted package " + libPath.getName());
                objM2315constructorimpl = Result.m2315constructorimpl(Unit.INSTANCE);
            } catch (Throwable th) {
                Result.Companion companion2 = Result.INSTANCE;
                objM2315constructorimpl = Result.m2315constructorimpl(ResultKt.createFailure(th));
            }
            Throwable thM2318exceptionOrNullimpl = Result.m2318exceptionOrNullimpl(objM2315constructorimpl);
            if (thM2318exceptionOrNullimpl != null) {
                FileLog.e("PipController: Failed to delete package " + libPath.getName(), thM2318exceptionOrNullimpl);
            }
        }
    }

    private final Pair<String, List<Pair<String, String>>> parseRequirement(String req) {
        String string = StringsKt.trim((CharSequence) REGEX_REQ_PAREN.replace(StringsKt.trim((CharSequence) REGEX_REQ_EXTRA.replace(StringsKt.trim((CharSequence) StringsKt.split$default((CharSequence) req, new String[]{";"}, false, 0, 6, (Object) null).get(0)).toString(), "")).toString(), "")).toString();
        MatchResult matchResultFind$default = Regex.find$default(REGEX_REQ_PARSE, string, 0, 2, null);
        if (matchResultFind$default == null) {
            return TuplesKt.to(string, CollectionsKt.emptyList());
        }
        MatchResult.Destructured destructured = matchResultFind$default.getDestructured();
        return TuplesKt.to(destructured.getMatch().getGroupValues().get(1), parseSpecs(destructured.getMatch().getGroupValues().get(2)));
    }

    private final List<Pair<String, String>> parseSpecs(String specsString) {
        ArrayList arrayList = new ArrayList();
        if (!StringsKt.isBlank(specsString)) {
            Iterator it = StringsKt.split$default((CharSequence) specsString, new String[]{","}, false, 0, 6, (Object) null).iterator();
            while (it.hasNext()) {
                MatchResult matchResultFind$default = Regex.find$default(REGEX_REQ_SPECS, StringsKt.trim((CharSequence) it.next()).toString(), 0, 2, null);
                if (matchResultFind$default != null) {
                    MatchResult.Destructured destructured = matchResultFind$default.getDestructured();
                    arrayList.add(TuplesKt.to(destructured.getMatch().getGroupValues().get(1), StringsKt.trim((CharSequence) destructured.getMatch().getGroupValues().get(2)).toString()));
                }
            }
        }
        return arrayList;
    }

    private final List<String> parseDependenciesFromMetadata(File metadataFile) {
        final ArrayList arrayList = new ArrayList();
        try {
            SimpliFile.forEachLine$default(SimpliFiles.file(metadataFile), 4194304L, null, new Function1() { 
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return PipController.$r8$lambda$3nEd9lhKtncIBzZ9CAoypYcXEk4(arrayList, (String) obj);
                }
            }, 2, null);
            return arrayList;
        } catch (Exception e) {
            FileLog.e("PipController: Failed to parse metadata", e);
            return arrayList;
        }
    }

    public static Unit $r8$lambda$3nEd9lhKtncIBzZ9CAoypYcXEk4(List list, String str) {
        "line";
        if (StringsKt.startsWith$default(str, "Requires-Dist:", false, 2, (Object) null)) {
            String strSubstring = str.substring(14);
            "substring(...)";
            String string = StringsKt.trim((CharSequence) strSubstring).toString();
            if (StringsKt.contains$default((CharSequence) string, (CharSequence) ";", false, 2, (Object) null)) {
                List listSplit$default = StringsKt.split$default((CharSequence) string, new String[]{";"}, false, 2, 2, (Object) null);
                if (!INSTANCE.isMarkerCompatible(StringsKt.trim((CharSequence) listSplit$default.get(1)).toString())) {
                    return Unit.INSTANCE;
                }
                list.add(StringsKt.trim((CharSequence) listSplit$default.get(0)).toString());
            } else {
                list.add(string);
            }
        }
        return Unit.INSTANCE;
    }

    private final boolean isMarkerCompatible(String marker) {
        return new MarkerParser(marker).parse();
    }

    private final File findMetadataFile(String pkg, String version) {
        File[] fileArrListFiles = getLibPath(pkg, version).listFiles(new FilenameFilter() { 
            @Override // java.io.FilenameFilter
            public final boolean accept(File file, String str) {
                return StringsKt.endsWith$default(str, ".dist-info", false, 2, (Object) null);
            }
        });
        File file = fileArrListFiles != null ? (File) ArraysKt.firstOrNull(fileArrListFiles) : null;
        if (file != null) {
            return new File(file, "METADATA");
        }
        return null;
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0017\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\n\u001a\u00020\u000bJ\b\u0010\f\u001a\u00020\u000bH\u0002J\b\u0010\r\u001a\u00020\u000bH\u0002J\b\u0010\u000e\u001a\u00020\u000bH\u0002J\b\u0010\u000f\u001a\u00020\u000bH\u0002J\n\u0010\u0010\u001a\u0004\u0018\u00010\u0003H\u0002J \u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u0003H\u0002J\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0016\u001a\u00020\u0003H\u0002J\u0014\u0010\u0017\u001a\u0004\u0018\u00010\u00032\b\u0010\u0018\u001a\u0004\u0018\u00010\u0003H\u0002J\u0010\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u0003H\u0002J\u0010\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u001b\u001a\u00020\u0003H\u0002J\u0010\u0010\u001c\u001a\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u0003H\u0002J\u0010\u0010\u001e\u001a\u00020\u000b2\u0006\u0010\u001d\u001a\u00020\u0003H\u0002J\n\u0010\u001f\u001a\u0004\u0018\u00010\u0003H\u0002J\u0014\u0010 \u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010!\u001a\u00020\tH\u0002R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\""}, d2 = {"Lcom/exteragram/messenger/plugins/pip/PipController$MarkerParser;", _UrlKt.FRAGMENT_ENCODE_SET, "marker", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;)V", "tokens", _UrlKt.FRAGMENT_ENCODE_SET, "position", _UrlKt.FRAGMENT_ENCODE_SET, "parse", _UrlKt.FRAGMENT_ENCODE_SET, "parseOr", "parseAnd", "parseFactor", "parseComparison", "nextOperator", "evaluateComparison", "left", "operator", "right", "markerVariableName", "token", "markerValue", "name", "invertVersionOperator", "unquote", "value", "match", "expected", "matchKeyword", "next", "peek", "offset", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class MarkerParser {
        private int position;
        private final List<String> tokens;

        public MarkerParser(String str) {
            "marker";
            this.tokens = SequencesKt.toList(SequencesKt.map(Regex.findAll$default(PipController.REGEX_MARKER_TOKEN, str, 0, 2, null), new Function1() { 
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return PipController.MarkerParser.$r8$lambda$2nxjO8cTPCazwOsauAZxX68kyv8((MatchResult) obj);
                }
            }));
        }

        public static String $r8$lambda$2nxjO8cTPCazwOsauAZxX68kyv8(MatchResult matchResult) {
            "it";
            return matchResult.getValue();
        }

        public final boolean parse() {
            if (this.tokens.isEmpty()) {
                return true;
            }
            try {
                return parseOr();
            } catch (Exception e) {
                FileLog.e("PipController: Failed to evaluate marker: " + CollectionsKt.joinToString$default(this.tokens, " ", null, null, 0, null, null, 62, null), e);
                return true;
            }
        }

        private final boolean parseOr() {
            boolean and = parseAnd();
            while (matchKeyword("or")) {
                and = and || parseAnd();
            }
            return and;
        }

        private final boolean parseAnd() {
            boolean factor = parseFactor();
            while (matchKeyword("and")) {
                factor = factor && parseFactor();
            }
            return factor;
        }

        private final boolean parseFactor() {
            if (match("(")) {
                boolean or = parseOr();
                match(")");
                return or;
            }
            return parseComparison();
        }

        private final boolean parseComparison() {
            String strNextOperator;
            String next;
            String next2 = next();
            if (next2 == null || (strNextOperator = nextOperator()) == null || (next = next()) == null) {
                return true;
            }
            return evaluateComparison(next2, strNextOperator, next);
        }

        private final String nextOperator() {
            String lowerCase;
            String strPeek$default = peek$default(this, 0, 1, null);
            if (strPeek$default == null) {
                return null;
            }
            Locale locale = Locale.ROOT;
            String lowerCase2 = strPeek$default.toLowerCase(locale);
            "toLowerCase(...)";
            if (Intrinsics.areEqual(lowerCase2, "not")) {
                String strPeek = peek(1);
                if (strPeek != null) {
                    lowerCase = strPeek.toLowerCase(locale);
                    "toLowerCase(...)";
                } else {
                    lowerCase = null;
                }
                if (Intrinsics.areEqual(lowerCase, "in")) {
                    this.position += 2;
                    return "not in";
                }
            }
            int iHashCode = lowerCase2.hashCode();
            if (iHashCode == 60 ? !lowerCase2.equals("<") : !(iHashCode == 62 ? lowerCase2.equals(">") : iHashCode == 1084 ? lowerCase2.equals("!=") : iHashCode == 1921 ? lowerCase2.equals("<=") : iHashCode == 1952 ? lowerCase2.equals("==") : iHashCode == 1983 ? lowerCase2.equals(">=") : iHashCode == 3365 ? lowerCase2.equals("in") : iHashCode == 3967 ? lowerCase2.equals("~=") : iHashCode == 60573 && lowerCase2.equals("==="))) {
                return null;
            }
            this.position++;
            return lowerCase2;
        }

        String peek$default(MarkerParser markerParser, int i, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                i = 0;
            }
            return markerParser.peek(i);
        }

        private final String peek(int offset) {
            return (String) CollectionsKt.getOrNull(this.tokens, this.position + offset);
        }
    }

    /* JADX WARN: Code duplicated, block: B:33:0x0081  */
    /* JADX WARN: Code duplicated, block: B:40:0x00ec  */
    public final boolean checkVersionSatisfies(String version, List<Pair<String, String>> specs) {
        boolean zMatchesVersionWildcard;
        if (specs.isEmpty()) {
            return true;
        }
        for (Pair<String, String> pair : specs) {
            String strComponent1 = pair.component1();
            String strComponent2 = pair.component2();
            VersionComparator versionComparator = VersionComparator.INSTANCE;
            int iCompare = versionComparator.compare(version, strComponent2);
            int iHashCode = strComponent1.hashCode();
            if (iHashCode != 60) {
                if (iHashCode != 62) {
                    if (iHashCode != 1084) {
                        if (iHashCode != 1921) {
                            if (iHashCode != 1952) {
                                if (iHashCode != 1983) {
                                    if (iHashCode != 3967) {
                                        if (iHashCode == 60573 && strComponent1.equals("===")) {
                                            zMatchesVersionWildcard = Intrinsics.areEqual(version, strComponent2);
                                        }
                                    } else if (strComponent1.equals("~=")) {
                                        if (iCompare >= 0) {
                                            List mutableList = CollectionsKt.toMutableList((Collection) StringsKt.split$default((CharSequence) strComponent2, new String[]{"."}, false, 0, 6, (Object) null));
                                            if (mutableList.size() >= 2) {
                                                mutableList.remove(CollectionsKt.getLastIndex(mutableList));
                                                int lastIndex = CollectionsKt.getLastIndex(mutableList);
                                                Integer intOrNull = StringsKt.toIntOrNull((String) mutableList.get(lastIndex));
                                                if (intOrNull != null) {
                                                    mutableList.set(lastIndex, String.valueOf(intOrNull.intValue() + 1));
                                                    if (versionComparator.compare(version, CollectionsKt.joinToString$default(mutableList, ".", null, null, 0, null, null, 62, null)) < 0) {
                                                    }
                                                }
                                            }
                                        }
                                        zMatchesVersionWildcard = false;
                                    }
                                    zMatchesVersionWildcard = true;
                                } else if (strComponent1.equals(">=") && iCompare < 0) {
                                    zMatchesVersionWildcard = false;
                                } else {
                                    zMatchesVersionWildcard = true;
                                }
                            } else if (!strComponent1.equals("==")) {
                                zMatchesVersionWildcard = true;
                            } else if (isWildcardVersionSpec(strComponent2)) {
                                zMatchesVersionWildcard = matchesVersionWildcard(version, strComponent2);
                            } else if (iCompare == 0) {
                                zMatchesVersionWildcard = true;
                            } else {
                                zMatchesVersionWildcard = false;
                            }
                        } else if (strComponent1.equals("<=") && iCompare > 0) {
                            zMatchesVersionWildcard = false;
                        } else {
                            zMatchesVersionWildcard = true;
                        }
                    } else if (strComponent1.equals("!=") && (!isWildcardVersionSpec(strComponent2) ? iCompare == 0 : matchesVersionWildcard(version, strComponent2))) {
                        zMatchesVersionWildcard = false;
                    } else {
                        zMatchesVersionWildcard = true;
                    }
                } else if (strComponent1.equals(">") && iCompare <= 0) {
                    zMatchesVersionWildcard = false;
                } else {
                    zMatchesVersionWildcard = true;
                }
            } else if (strComponent1.equals("<") && iCompare >= 0) {
                zMatchesVersionWildcard = false;
            } else {
                zMatchesVersionWildcard = true;
            }
            if (!zMatchesVersionWildcard) {
                return false;
            }
        }
        return true;
    }

    private final boolean isWildcardVersionSpec(String spec) {
        return REGEX_VERSION_WILDCARD.containsMatchIn(StringsKt.trim((CharSequence) spec).toString());
    }

    private final List<String> filterPreReleases(List<String> versions, List<Pair<String, String>> specs) {
        if (versions.isEmpty() || specsAllowPreRelease(specs)) {
            return versions;
        }
        ArrayList arrayList = new ArrayList();
        for (Object obj : versions) {
            if (!INSTANCE.isPreReleaseVersion((String) obj)) {
                arrayList.add(obj);
            }
        }
        if (!arrayList.isEmpty()) {
            versions = arrayList;
        }
        return versions;
    }

    private final boolean specsAllowPreRelease(List<Pair<String, String>> specs) {
        List<Pair<String, String>> list = specs;
        if ((list instanceof Collection) && list.isEmpty()) {
            return false;
        }
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            if (INSTANCE.isPreReleaseVersion((String) ((Pair) it.next()).component2())) {
                return true;
            }
        }
        return false;
    }

    private final boolean isPreReleaseVersion(String version) {
        List<String> parts = parseVersion(version).getParts();
        if ((parts instanceof Collection) && parts.isEmpty()) {
            return false;
        }
        for (String str : parts) {
            switch (str.hashCode()) {
                case 97:
                    if (str.equals("a")) {
                        return true;
                    }
                    break;
                    break;
                case 98:
                    if (str.equals("b")) {
                        return true;
                    }
                    break;
                    break;
                case 99:
                    if (str.equals("c")) {
                        return true;
                    }
                    break;
                    break;
                case 3633:
                    if (str.equals("rc")) {
                        return true;
                    }
                    break;
                    break;
                case 99349:
                    if (str.equals("dev")) {
                        return true;
                    }
                    break;
                    break;
                case 3020272:
                    if (str.equals("beta")) {
                        return true;
                    }
                    break;
                    break;
                case 92909918:
                    if (str.equals("alpha")) {
                        return true;
                    }
                    break;
                    break;
            }
        }
        return false;
    }

    private final boolean matchesVersionWildcard(String version, String spec) {
        String strTrimEnd = StringsKt.trimEnd(StringsKt.removeSuffix(StringsKt.removeSuffix(StringsKt.removeSuffix(StringsKt.substringBefore$default(StringsKt.trim((CharSequence) spec).toString(), "+", (String) null, 2, (Object) null), (CharSequence) ".*"), (CharSequence) "-*"), (CharSequence) "_*"), '.', SignatureVisitor.SUPER, '_');
        ParsedVersion version2 = parseVersion(strTrimEnd);
        ParsedVersion version3 = parseVersion(version);
        if (StringsKt.contains$default((CharSequence) strTrimEnd, (CharSequence) "!", false, 2, (Object) null) && version2.getEpoch() != version3.getEpoch()) {
            return false;
        }
        List<Integer> versionReleaseParts = parseVersionReleaseParts(strTrimEnd);
        if (versionReleaseParts.isEmpty()) {
            return true;
        }
        List<Integer> versionReleaseParts2 = parseVersionReleaseParts(version);
        if (versionReleaseParts2.size() < versionReleaseParts.size()) {
            return false;
        }
        Iterable indices = CollectionsKt.getIndices(versionReleaseParts);
        if ((indices instanceof Collection) && ((Collection) indices).isEmpty()) {
            return true;
        }
        Iterator it = indices.iterator();
        while (it.hasNext()) {
            int iNextInt = ((IntIterator) it).nextInt();
            if (versionReleaseParts2.get(iNextInt).intValue() != versionReleaseParts.get(iNextInt).intValue()) {
                return false;
            }
        }
        return true;
    }

    private final List<Integer> parseVersionReleaseParts(String version) {
        List<String> listSplit = new Regex("[.\\-_]").split(parseVersion(version).getPublicVersion(), 0);
        ArrayList arrayList = new ArrayList();
        Iterator<T> it = listSplit.iterator();
        while (it.hasNext()) {
            Integer intOrNull = StringsKt.toIntOrNull((String) it.next());
            if (intOrNull != null) {
                arrayList.add(intOrNull);
            }
        }
        return arrayList;
    }

    @Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0002J\b\u0010\n\u001a\u00020\tH\u0016J \u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\tH\u0016J\b\u0010\u000f\u001a\u00020\tH\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lcom/exteragram/messenger/plugins/pip/PipController$SizeLimitedInputStream;", "Ljava/io/InputStream;", "delegate", "maxBytes", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/io/InputStream;J)V", "total", "track", _UrlKt.FRAGMENT_ENCODE_SET, "read", "b", _UrlKt.FRAGMENT_ENCODE_SET, "off", "len", "available", "close", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class SizeLimitedInputStream extends InputStream {
        private final InputStream delegate;
        private final long maxBytes;
        private long total;

        public SizeLimitedInputStream(InputStream inputStream, long j) {
            "delegate";
            this.delegate = inputStream;
            this.maxBytes = j;
        }

        private final int track(int read) throws IOException {
            if (read > 0) {
                long j = this.total + ((long) read);
                this.total = j;
                if (j > this.maxBytes) {
                    throw new IOException("Download exceeds maximum allowed size of " + this.maxBytes + " bytes");
                }
            }
            return read;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int i = this.delegate.read();
            if (i != -1) {
                track(1);
            }
            return i;
        }

        @Override // java.io.InputStream
        public int read(byte[] b2, int off, int len) {
            "b";
            return track(this.delegate.read(b2, off, len));
        }

        @Override // java.io.InputStream
        public int available() {
            return this.delegate.available();
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.delegate.close();
        }
    }

    private final String calculateSha256(File file) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            byte[] bArr = new byte[8192];
            while (true) {
                int i = fileInputStream.read(bArr);
                if (i != -1) {
                    messageDigest.update(bArr, 0, i);
                } else {
                    Unit unit = Unit.INSTANCE;
                    CloseableKt.closeFinally(fileInputStream, null);
                    byte[] bArrDigest = messageDigest.digest();
                    "digest(...)";
                    return ArraysKt.joinToString$default(bArrDigest, (CharSequence) "", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, new Function1() { 
                        @Override // kotlin.jvm.functions.Function1
                        public final Object invoke(Object obj) {
                            return PipController.$r8$lambda$7CZkhP2Pq3JYvkGUBl8vqtaliPk(((Byte) obj).byteValue());
                        }
                    }, 30, (Object) null);
                }
            }
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                CloseableKt.closeFinally(fileInputStream, th);
                throw th2;
            }
        }
    }

    public static CharSequence $r8$lambda$7CZkhP2Pq3JYvkGUBl8vqtaliPk(byte b2) {
        String str = String.format("%02x", Arrays.copyOf(new Object[]{Byte.valueOf(b2)}, 1));
        "format(...)";
        return str;
    }

    public final ParsedVersion parseVersion(String version) {
        Integer intOrNull;
        String lowerCase = StringsKt.trim((CharSequence) version).toString().toLowerCase(Locale.ROOT);
        "toLowerCase(...)";
        List listSplit$default = StringsKt.split$default((CharSequence) StringsKt.substringBefore$default(StringsKt.removePrefix(lowerCase, (CharSequence) "v"), "+", (String) null, 2, (Object) null), new String[]{"!"}, false, 2, 2, (Object) null);
        int iIntValue = (listSplit$default.size() != 2 || (intOrNull = StringsKt.toIntOrNull((String) listSplit$default.get(0))) == null) ? 0 : intOrNull.intValue();
        String str = (String) (listSplit$default.size() == 2 ? listSplit$default.get(1) : listSplit$default.get(0));
        return new ParsedVersion(iIntValue, str, SequencesKt.toList(SequencesKt.map(Regex.findAll$default(REGEX_VERSION_SPLIT, str, 0, 2, null), new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return PipController.m1324$r8$lambda$0MJ3GR4HlLQMQMAksazHxJXIE((MatchResult) obj);
            }
        })));
    }

    public static String m1324$r8$lambda$0MJ3GR4HlLQMQMAksazHxJXIE(MatchResult matchResult) {
        "it";
        return matchResult.getValue();
    }

    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\bÂ\u0002\u0018\u00002\u0012\u0012\u0004\u0012\u00020\u00020\u0001j\b\u0012\u0004\u0012\u00020\u0002`\u0003B\t\b\u0002¢\u0006\u0004\b\u0004\u0010\u0005J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\u0002H\u0016J\u0010\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u0002H\u0002¨\u0006\f"}, d2 = {"Lcom/exteragram/messenger/plugins/pip/PipController$VersionComparator;", "Ljava/util/Comparator;", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/Comparator;", "<init>", "()V", "compare", _UrlKt.FRAGMENT_ENCODE_SET, "v1", "v2", "getWeight", "s", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class VersionComparator implements Comparator<String> {
        public static final VersionComparator INSTANCE = new VersionComparator();

        private VersionComparator() {
        }

        @Override // java.util.Comparator
        public int compare(String v1, String v2) {
            "v1";
            "v2";
            PipController pipController = PipController.INSTANCE;
            ParsedVersion version = pipController.parseVersion(v1);
            ParsedVersion version2 = pipController.parseVersion(v2);
            int iCompare = Intrinsics.compare(version.getEpoch(), version2.getEpoch());
            if (iCompare != 0) {
                return iCompare;
            }
            List<String> parts = version.getParts();
            List<String> parts2 = version2.getParts();
            int iMax = Math.max(parts.size(), parts2.size());
            for (int i = 0; i < iMax; i++) {
                String string = (String) CollectionsKt.getOrNull(parts, i);
                String string2 = (String) CollectionsKt.getOrNull(parts2, i);
                if (string == null) {
                    if ((string2 != null ? StringsKt.toIntOrNull(string2) : null) != null) {
                        string = "0";
                    }
                }
                if (string2 == null) {
                    if ((string != null ? StringsKt.toIntOrNull(string) : null) != null) {
                        string2 = "0";
                    }
                }
                if (string == null) {
                    string = "";
                }
                if (string2 == null) {
                    string2 = "";
                }
                if (!Intrinsics.areEqual(string, string2)) {
                    Integer intOrNull = StringsKt.toIntOrNull(string);
                    Integer intOrNull2 = StringsKt.toIntOrNull(string2);
                    if (intOrNull != null && intOrNull2 != null) {
                        int iCompare2 = Intrinsics.compare(intOrNull.intValue(), intOrNull2.intValue());
                        if (iCompare2 != 0) {
                            return iCompare2;
                        }
                    } else {
                        if (intOrNull != null) {
                            return 1;
                        }
                        if (intOrNull2 != null) {
                            return -1;
                        }
                        int weight = getWeight(string);
                        int weight2 = getWeight(string2);
                        if (weight != weight2) {
                            return Intrinsics.compare(weight, weight2);
                        }
                        int iCompareTo = string.compareTo(string2);
                        if (iCompareTo != 0) {
                            return iCompareTo;
                        }
                    }
                }
            }
            return 0;
        }

        private final int getWeight(String s) {
            if (Intrinsics.areEqual(s, "post")) {
                return 110;
            }
            if (s.length() == 0) {
                return 100;
            }
            if (Intrinsics.areEqual(s, "rc") || Intrinsics.areEqual(s, "c")) {
                return 80;
            }
            if (Intrinsics.areEqual(s, "beta") || Intrinsics.areEqual(s, "b")) {
                return 70;
            }
            if (Intrinsics.areEqual(s, "alpha") || Intrinsics.areEqual(s, "a")) {
                return 60;
            }
            return Intrinsics.areEqual(s, "dev") ? 50 : 0;
        }
    }
}
