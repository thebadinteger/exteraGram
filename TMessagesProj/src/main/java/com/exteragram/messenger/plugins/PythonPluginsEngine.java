package com.exteragram.messenger.plugins;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import androidx.core.content.FileProvider;
import androidx.mediarouter.media.MediaRouteProviderProtocol;
import com.android.dx.rop.code.RegisterSpec;
import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.plugins.hooks.PluginsHooks;
import com.exteragram.messenger.plugins.models.CustomSetting;
import com.exteragram.messenger.plugins.models.DividerSetting;
import com.exteragram.messenger.plugins.models.EditTextSetting;
import com.exteragram.messenger.plugins.models.HeaderSetting;
import com.exteragram.messenger.plugins.models.InputSetting;
import com.exteragram.messenger.plugins.models.SelectorSetting;
import com.exteragram.messenger.plugins.models.SettingItem;
import com.exteragram.messenger.plugins.models.SwitchSetting;
import com.exteragram.messenger.plugins.models.TextSetting;
import com.exteragram.messenger.plugins.pip.PipController;
import com.exteragram.messenger.plugins.ui.PluginSettingsActivity;
import com.exteragram.messenger.plugins.ui.components.InstallPluginBottomSheet;
import com.exteragram.messenger.plugins.ui.components.PluginFileViewer;
import com.exteragram.messenger.plugins.utils.PyObjectUtils;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.exteragram.messenger.utils.AppUtils;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.android.gms.cast.MediaError;
import com.sun.jna.Callback;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.io.CloseableKt;
import kotlin.jdk7.AutoCloseableKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.random.RandomKt$$ExternalSyntheticBUOutline0;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlin.time.DurationKt;
import okhttp3.HttpUrl$$ExternalSyntheticBUOutline0;
import okhttp3.internal.url._UrlKt;
import org.lsposed.lsparanoid.Deobfuscator$exteraGramDev$TMessagesProj;
import org.mvel2.util.Make$Map$$ExternalSyntheticBUOutline0;
import org.simplifiles.SimpliFiles;
import org.simplifiles.archive.ArchiveExtractionOptions;
import org.simplifiles.archive.ExtractionTargetPolicy;
import org.simplifiles.archive.security.SecurityPolicy;
import org.simplifiles.files.OverwritePolicy;
import org.simplifiles.files.SimpliFile;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.LaunchActivity;

@Metadata(d1 = {"\u0000ô\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u0007\u0018\u0000 \u009b\u00012\u00020\u0001:\u0006\u009b\u0001\u009c\u0001\u009d\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u001b\u001a\u00020\u001cH\u0002J\n\u0010\u001d\u001a\u0004\u0018\u00010\u0011H\u0002J\b\u0010\u001e\u001a\u00020\u001fH\u0002J\u001c\u0010 \u001a\u00020\u000f2\b\u0010!\u001a\u0004\u0018\u00010\"2\b\u0010#\u001a\u0004\u0018\u00010$H\u0016J\b\u0010%\u001a\u00020\u000fH\u0016J\b\u0010&\u001a\u00020\u0007H\u0002J\u0018\u0010'\u001a\u00020\u001f2\u0006\u0010(\u001a\u00020\"2\u0006\u0010)\u001a\u00020\u000fH\u0002J\b\u0010*\u001a\u00020\u000fH\u0002J\b\u0010+\u001a\u00020\u001fH\u0002J \u0010,\u001a\u00020\u001f2\u0006\u0010-\u001a\u00020\u00072\u0006\u0010!\u001a\u00020\"2\u0006\u0010.\u001a\u00020\u0006H\u0002J\u0010\u0010/\u001a\u00020\u001f2\u0006\u00100\u001a\u000201H\u0016J\b\u00102\u001a\u00020\u001fH\u0016J\b\u00103\u001a\u00020\u001fH\u0002J\b\u00104\u001a\u00020\u001fH\u0002J\u0010\u00105\u001a\u00020\u001f2\u0006\u00100\u001a\u000201H\u0016J\u0010\u00106\u001a\u00020\u001f2\b\u00100\u001a\u0004\u0018\u000101J\u0016\u00107\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u0006J \u00107\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00062\b\u0010:\u001a\u0004\u0018\u00010;J*\u00107\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00062\b\u0010:\u001a\u0004\u0018\u00010;2\b\u0010<\u001a\u0004\u0018\u00010=J\"\u0010>\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u0010?\u001a\u00020;2\b\u0010<\u001a\u0004\u0018\u00010=H\u0002J&\u0010@\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u0010A\u001a\u00020\u00062\f\u0010B\u001a\b\u0012\u0004\u0012\u00020\u00060CH\u0002J\u0010\u0010D\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u0006H\u0002J\b\u0010E\u001a\u00020\u001fH\u0002J\u0018\u0010F\u001a\u00020\u001f2\u0006\u0010-\u001a\u00020\u00072\u0006\u0010G\u001a\u00020\u0006H\u0002J\b\u0010H\u001a\u00020\u001fH\u0002J\u0018\u0010I\u001a\u00020\u001f2\u0006\u0010J\u001a\u00020\u00072\u0006\u0010G\u001a\u00020\u0006H\u0002J*\u0010K\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00062\u0006\u0010?\u001a\u00020;2\b\u0010<\u001a\u0004\u0018\u00010=H\u0002J\u000e\u0010L\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u0006J\u001a\u0010M\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\b\u0010N\u001a\u0004\u0018\u00010\"H\u0002J\"\u0010O\u001a\u00020\u001f2\u0006\u0010-\u001a\u00020\u00072\u0006\u00108\u001a\u00020\u00062\b\u0010N\u001a\u0004\u0018\u00010\"H\u0002J(\u0010P\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u0010Q\u001a\u00020\u000f2\u000e\u00100\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010RH\u0016J \u0010S\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u000e\u00100\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010RH\u0016J\u0010\u0010T\u001a\u00020\u00062\u0006\u0010U\u001a\u00020\u0006H\u0016J\b\u0010V\u001a\u00020\u000fH\u0016J\u0010\u0010W\u001a\u00020\u001f2\u0006\u0010U\u001a\u00020\u0006H\u0016J\u0010\u0010X\u001a\u00020\u001f2\u0006\u0010U\u001a\u00020\u0006H\u0016J(\u0010Y\u001a\u00020\u001f2\u0006\u00109\u001a\u00020\u00062\b\u0010?\u001a\u0004\u0018\u00010;2\u000e\u00100\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010RJ2\u0010Y\u001a\u00020\u001f2\u0006\u00109\u001a\u00020\u00062\b\u0010?\u001a\u0004\u0018\u00010;2\u000e\u00100\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010R2\b\u0010<\u001a\u0004\u0018\u00010=J\u0012\u0010Z\u001a\u0004\u0018\u00010\u00062\u0006\u00108\u001a\u00020\u0006H\u0002J\u000e\u0010[\u001a\u00020\\2\u0006\u00109\u001a\u00020\u0006J\u001a\u0010]\u001a\b\u0012\u0004\u0012\u00020_0^2\f\u0010`\u001a\b\u0012\u0004\u0012\u00020\u00070^J\u0018\u0010a\u001a\n\u0012\u0004\u0012\u00020_\u0018\u00010^2\u0006\u0010U\u001a\u00020\u0006H\u0016J\u0010\u0010b\u001a\u00020\u001f2\u0006\u0010c\u001a\u00020\u0006H\u0016J_\u0010d\u001a\b\u0012\u0004\u0012\u0002Hf0e\"\u0004\b\u0000\u0010f2\b\u0010g\u001a\u0004\u0018\u00010\u00072\b\u0010h\u001a\u0004\u0018\u0001Hf2\f\u0010i\u001a\b\u0012\u0004\u0012\u0002Hf0j2\u0006\u0010k\u001a\u00020\u00062\f\u0010l\u001a\b\u0012\u0004\u0012\u0002Hf0m2\f\u0010n\u001a\b\u0012\u0004\u0012\u00020o0RH\u0002¢\u0006\u0002\u0010pJ]\u0010d\u001a\b\u0012\u0004\u0012\u0002Hf0e\"\u0004\b\u0000\u0010f2\u0006\u00108\u001a\u00020\u00062\b\u0010h\u001a\u0004\u0018\u0001Hf2\f\u0010i\u001a\b\u0012\u0004\u0012\u0002Hf0j2\u0006\u0010k\u001a\u00020\u00062\f\u0010l\u001a\b\u0012\u0004\u0012\u0002Hf0m2\f\u0010n\u001a\b\u0012\u0004\u0012\u00020o0RH\u0002¢\u0006\u0002\u0010qJ0\u0010r\u001a\b\u0012\u0004\u0012\u00020s0e2\u0006\u0010t\u001a\u00020\u00062\u0006\u0010u\u001a\u00020v2\b\u0010w\u001a\u0004\u0018\u00010s2\u0006\u00108\u001a\u00020\u0006H\u0016J:\u0010x\u001a\b\u0012\u0004\u0012\u00020y0e2\u0006\u0010t\u001a\u00020\u00062\u0006\u0010u\u001a\u00020v2\b\u0010z\u001a\u0004\u0018\u00010s2\b\u0010{\u001a\u0004\u0018\u00010|2\b\u0010g\u001a\u0004\u0018\u00010\u0007J:\u0010x\u001a\b\u0012\u0004\u0012\u00020y0e2\u0006\u0010t\u001a\u00020\u00062\u0006\u0010u\u001a\u00020v2\b\u0010z\u001a\u0004\u0018\u00010s2\b\u0010{\u001a\u0004\u0018\u00010|2\u0006\u00108\u001a\u00020\u0006H\u0016J1\u0010}\u001a\b\u0012\u0004\u0012\u00020~0e2\u0006\u0010\u007f\u001a\u00020\u00062\u0006\u0010u\u001a\u00020v2\t\u0010\u0080\u0001\u001a\u0004\u0018\u00010~2\u0006\u00108\u001a\u00020\u0006H\u0016J5\u0010\u0081\u0001\u001a\t\u0012\u0005\u0012\u00030\u0082\u00010e2\u0007\u0010\u0083\u0001\u001a\u00020\u00062\u0006\u0010u\u001a\u00020v2\n\u0010\u0084\u0001\u001a\u0005\u0018\u00010\u0082\u00012\u0006\u00108\u001a\u00020\u0006H\u0016J,\u0010\u0085\u0001\u001a\t\u0012\u0005\u0012\u00030\u0086\u00010e2\u0006\u0010u\u001a\u00020v2\n\u0010\u0087\u0001\u001a\u0005\u0018\u00010\u0086\u00012\u0006\u00108\u001a\u00020\u0006H\u0016J\u001c\u0010\u0088\u0001\u001a\u0004\u0018\u00010\u00062\b\u00109\u001a\u0004\u0018\u00010\u00062\u0007\u0010\u0089\u0001\u001a\u00020\u0006J\u001e\u0010\u008a\u0001\u001a\u000f\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u008b\u00012\b\u00109\u001a\u0004\u0018\u00010\u0006J'\u0010\u008c\u0001\u001a\u0004\u0018\u00010\u000b2\u0006\u00108\u001a\u00020\u00062\u0007\u0010\u008d\u0001\u001a\u00020\u00062\t\u0010\u008e\u0001\u001a\u0004\u0018\u00010\u000bH\u0016J%\u0010\u008f\u0001\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0007\u0010\u008d\u0001\u001a\u00020\u00062\t\u0010\u0090\u0001\u001a\u0004\u0018\u00010\u000bH\u0016J\u0011\u0010\u0091\u0001\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u0006H\u0016J\u001e\u0010\u0092\u0001\u001a\u000f\u0012\u0004\u0012\u00020\u0006\u0012\u0002\b\u0003\u0018\u00010\u008b\u00012\u0006\u00108\u001a\u00020\u0006H\u0016J\u001d\u0010\u0093\u0001\u001a\u00020\u001f2\b\u0010\u0094\u0001\u001a\u00030\u0095\u00012\b\u0010\u0087\u0001\u001a\u00030\u0096\u0001H\u0016J\u001b\u0010\u0097\u0001\u001a\u00020\u001f2\u0006\u0010U\u001a\u00020\u00062\b\u0010\u0094\u0001\u001a\u00030\u0095\u0001H\u0016J\u001c\u0010\u0097\u0001\u001a\u00020\u001f2\u0007\u0010\u0098\u0001\u001a\u00020;2\b\u0010\u0094\u0001\u001a\u00030\u0095\u0001H\u0016J%\u0010\u0099\u0001\u001a\u00020\u001f2\u0007\u0010\u0098\u0001\u001a\u00020;2\u0007\u0010\u009a\u0001\u001a\u00020\u00062\b\u0010\u0094\u0001\u001a\u00030\u0095\u0001H\u0016J$\u0010\u0099\u0001\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0007\u0010\u009a\u0001\u001a\u00020\u00062\b\u0010\u0094\u0001\u001a\u00030\u0095\u0001H\u0016R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR(\u0010\n\u001a\u001c\u0012\u0004\u0012\u00020\u0006\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000RN\u0010\f\u001aB\u0012\f\u0012\n \u000e*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000e*\u0004\u0018\u00010\u000f0\u000f \u000e* \u0012\f\u0012\n \u000e*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000e*\u0004\u0018\u00010\u000f0\u000f\u0018\u00010\r0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u0013\u001a\u0004\u0018\u00010\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001c\u0010\u0018\u001a\u0004\u0018\u00010\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u0015\"\u0004\b\u001a\u0010\u0017¨\u0006\u009e\u0001"}, d2 = {"Lcom/exteragram/messenger/plugins/PythonPluginsEngine;", "Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", "<init>", "()V", "pluginInstances", "Ljava/util/concurrent/ConcurrentHashMap;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/chaquo/python/PyObject;", "getPluginInstances", "()Ljava/util/concurrent/ConcurrentHashMap;", "settingsCache", _UrlKt.FRAGMENT_ENCODE_SET, "dependencyPaths", "Ljava/util/concurrent/ConcurrentHashMap$KeySetView;", "kotlin.jvm.PlatformType", _UrlKt.FRAGMENT_ENCODE_SET, "python", "Lcom/chaquo/python/Python;", "devServerClass", "debuggerListener", "getDebuggerListener", "()Lcom/chaquo/python/PyObject;", "setDebuggerListener", "(Lcom/chaquo/python/PyObject;)V", "basePluginClass", "getBasePluginClass", "setBasePluginClass", "getPluginsController", "Lcom/exteragram/messenger/plugins/PluginsController;", "getPython", "initPython", _UrlKt.FRAGMENT_ENCODE_SET, "isPlugin", "file", "Ljava/io/File;", "messageObject", "Lorg/telegram/messenger/MessageObject;", "isEngineAvailable", "requireBasePluginClass", "installSdkArchive", "archiveFile", "fromApk", "initSdk", "stopAndUnloadSdk", "removeModulesRecursive", "sysModules", "prefix", "init", Callback.METHOD_NAME, "Ljava/lang/Runnable;", "checkDevServer", "runDevServer", "stopDevServer", "shutdown", "loadPlugins", "loadPlugin", "pluginId", "filePath", "metadata", "Lcom/exteragram/messenger/plugins/Plugin;", "delegate", "Lcom/exteragram/messenger/plugins/pip/PipController$InstallerDelegate;", "installPluginDependencies", "pluginMetadata", "disableShadowedPlugins", "dependencyName", "providedModules", _UrlKt.FRAGMENT_ENCODE_SET, "removePluginDependencies", "pruneDependencyPaths", "removeModulesUnderPath", "path", "removePluginPathsFromSysPath", "removeFromSysPath", "sysPath", "createPluginInstance", "unloadPlugin", "refreshImportCaches", "moduleDir", "evictPluginModule", "setPluginEnabled", "enabled", "Lorg/telegram/messenger/Utilities$Callback;", "deletePlugin", "getPluginPath", "id", "canOpenInExternalApp", "openInExternalApp", "sharePlugin", "loadPluginFromFile", "findModuleNameOwner", "validatePluginFromFile", "Lcom/exteragram/messenger/plugins/PluginsController$PluginValidationResult;", "parsePySettingDefinitions", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/models/SettingItem;", "pyDefinitionsList", "loadPluginSettings", "executeOnAppEvent", "eventType", "executeHook", "Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "T", "pluginInstance", "initialValue", "valueClass", "Ljava/lang/Class;", "pyResultKey", "caller", "Lcom/exteragram/messenger/plugins/PythonPluginsEngine$PyMethodCaller;", "errorLogger", "Lcom/chaquo/python/PyException;", "(Lcom/chaquo/python/PyObject;Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;Lcom/exteragram/messenger/plugins/PythonPluginsEngine$PyMethodCaller;Lorg/telegram/messenger/Utilities$Callback;)Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;Lcom/exteragram/messenger/plugins/PythonPluginsEngine$PyMethodCaller;Lorg/telegram/messenger/Utilities$Callback;)Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "executePreRequestHook", "Lorg/telegram/tgnet/TLObject;", "requestName", "account", _UrlKt.FRAGMENT_ENCODE_SET, "request", "executePostRequestHook", "Lcom/exteragram/messenger/plugins/hooks/PluginsHooks$PostRequestResult;", "response", MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "Lorg/telegram/tgnet/TLRPC$TL_error;", "executeUpdateHook", "Lorg/telegram/tgnet/TLRPC$Update;", "updateName", "update", "executeUpdatesHook", "Lorg/telegram/tgnet/TLRPC$Updates;", "containerName", "updates", "executeSendMessageHook", "Lorg/telegram/messenger/SendMessagesHelper$SendMessageParams;", "params", "fetchParameterValue", "parameterName", "parsePluginMetadata", _UrlKt.FRAGMENT_ENCODE_SET, "getPluginSetting", "key", "defaultValue", "setPluginSetting", "value", "clearPluginSettings", "getAllPluginSettings", "showInstallDialog", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "Lcom/exteragram/messenger/plugins/ui/components/InstallPluginBottomSheet$PluginInstallParams;", "openPluginSettings", "plugin", "openPluginSetting", "linkAlias", "Companion", "PyMethodCaller", "Updater", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nPythonPluginsEngine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PythonPluginsEngine.kt\ncom/exteragram/messenger/plugins/PythonPluginsEngine\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 SharedPreferences.kt\nandroidx/core/content/SharedPreferencesKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,2775:1\n1#2:2776\n41#3,12:2777\n41#3,12:2795\n41#3,12:2810\n41#3,12:2822\n41#3,12:2834\n1300#4,2:2789\n1315#4,4:2791\n777#4:2807\n873#4,2:2808\n1586#4:2846\n1661#4,3:2847\n296#4,2:2850\n1586#4:2852\n1661#4,3:2853\n777#4:2856\n873#4,2:2857\n1834#4,4:2859\n37#5,2:2863\n*S KotlinDebug\n*F\n+ 1 PythonPluginsEngine.kt\ncom/exteragram/messenger/plugins/PythonPluginsEngine\n*L\n502#1:2777,12\n792#1:2795,12\n1039#1:2810,12\n1044#1:2822,12\n1059#1:2834,12\n752#1:2789,2\n752#1:2791,4\n814#1:2807\n814#1:2808,2\n1281#1:2846\n1281#1:2847,3\n1282#1:2850,2\n1400#1:2852\n1400#1:2853,3\n1401#1:2856\n1401#1:2857,2\n669#1:2859,4\n2093#1:2863,2\n*E\n"})
public final class PythonPluginsEngine implements PluginsController.PluginsEngine {
    private static final long MAX_SDK_VERSION_BYTES = 65536;
    private static boolean SDK_BETA;
    private static File SDK_DIR;
    private static String SDK_VERSION;
    private static volatile boolean sdkInitialized;
    private volatile PyObject basePluginClass;
    private PyObject debuggerListener;
    private PyObject devServerClass;
    private volatile Python python;
    private static final String SAFE_MODE_ENABLE_ERROR = "Plugins cannot be enabled while safe mode is active";

    public static final Companion INSTANCE = new Companion(null);
    private static final Pattern VERSION_PATTERN = Pattern.compile("^(>=|<=|==|>|<)(.+)$");
    private static final String[] SDK_REQUIRED_MODULES = {"_sdk_version", "base_plugin", "plugin_settings"};
    private static final SecurityPolicy SDK_ARCHIVE_POLICY = SecurityPolicy.INSTANCE.builder().maxEntries(100000).maxTotalUncompressedSize(2147483648L).maxSingleFileSize(536870912).maxCompressionRatio(500.0d).build();
    private static final ArchiveExtractionOptions SDK_ARCHIVE_OPTIONS = ArchiveExtractionOptions.INSTANCE.builder().targetPolicy(ExtractionTargetPolicy.REPLACE).build();
    private final ConcurrentHashMap<String, PyObject> pluginInstances = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> settingsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap.KeySetView<String, Boolean> dependencyPaths = ConcurrentHashMap.newKeySet();

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bâ\u0080\u0001\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002J!\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u00042\b\u0010\u0006\u001a\u0004\u0018\u00018\u0000H&¢\u0006\u0002\u0010\u0007¨\u0006\bÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/PythonPluginsEngine$PyMethodCaller;", "T", _UrlKt.FRAGMENT_ENCODE_SET, "call", "Lcom/chaquo/python/PyObject;", "instance", "value", "(Lcom/chaquo/python/PyObject;Ljava/lang/Object;)Lcom/chaquo/python/PyObject;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface PyMethodCaller<T> {
        PyObject call(PyObject instance, T value);
    }

    @Override 
    public boolean canOpenInExternalApp() {
        return true;
    }

    @Metadata(d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010 \u001a\u00020\u00182\u0006\u0010!\u001a\u00020\u001e2\u0006\u0010\"\u001a\u00020\tH\u0002J\u0012\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u001eH\u0002J\u0012\u0010&\u001a\u00020\u00182\b\u0010!\u001a\u0004\u0018\u00010\u001eH\u0002J\u0014\u0010'\u001a\u0004\u0018\u00010\t2\b\u0010(\u001a\u0004\u0018\u00010\tH\u0002J\u0018\u0010)\u001a\b\u0012\u0004\u0012\u00020\t0*2\b\u0010+\u001a\u0004\u0018\u00010\u001eH\u0002R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u001c\u0010\u0012\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0017\u001a\u00020\u0018X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0018X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006,"}, d2 = {"Lcom/exteragram/messenger/plugins/PythonPluginsEngine$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "VERSION_PATTERN", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "SDK_REQUIRED_MODULES", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "[Ljava/lang/String;", "SDK_ARCHIVE_POLICY", "Lorg/simplifiles/archive/security/SecurityPolicy;", "SDK_ARCHIVE_OPTIONS", "Lorg/simplifiles/archive/ArchiveExtractionOptions;", "MAX_SDK_VERSION_BYTES", _UrlKt.FRAGMENT_ENCODE_SET, "SAFE_MODE_ENABLE_ERROR", "SDK_VERSION", "getSDK_VERSION", "()Ljava/lang/String;", "setSDK_VERSION", "(Ljava/lang/String;)V", "SDK_BETA", _UrlKt.FRAGMENT_ENCODE_SET, "getSDK_BETA", "()Z", "setSDK_BETA", "(Z)V", "SDK_DIR", "Ljava/io/File;", "sdkInitialized", "sdkModuleExists", "sdkDir", "moduleName", "deleteFileIfExists", _UrlKt.FRAGMENT_ENCODE_SET, "file", "isSdkDirValid", "canonicalPathOrNull", "path", "topLevelModuleNames", _UrlKt.FRAGMENT_ENCODE_SET, "dir", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    @SourceDebugExtension({"SMAP\nPythonPluginsEngine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PythonPluginsEngine.kt\ncom/exteragram/messenger/plugins/PythonPluginsEngine$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,2775:1\n1#2:2776\n*E\n"})
    public static final class Companion {
        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            public final int getStatus() {
                return Updater.status;
            }

            public final void setStatus(int i) {
                Updater.status = i;
            }

            public final boolean getNotifyWhenChangeStatus() {
                return Updater.notifyWhenChangeStatus;
            }

            public final void setNotifyWhenChangeStatus(boolean z) {
                Updater.notifyWhenChangeStatus = z;
            }

            @JvmStatic
            public final CharSequence getVersion() {
                String sdk_version = (ExteraConfig.getPluginsEngine() && PythonPluginsEngine.sdkInitialized) ? PythonPluginsEngine.INSTANCE.getSDK_VERSION() : null;
                boolean sdk_beta = PythonPluginsEngine.INSTANCE.getSDK_BETA();
                if (sdk_version == null && PythonPluginsEngine.SDK_DIR != null) {
                    File file = new File(PythonPluginsEngine.SDK_DIR, -55188051478063L));
                    if (file.exists()) {
                        String string = StringsKt.trim((CharSequence) SimpliFile.readText$default(SimpliFiles.file(file), 65536L, null, 2, null)).toString();
                        sdk_beta = StringsKt.endsWith$default(string, "|1", false, 2, (Object) null);
                        sdk_version = StringsKt.substringBefore$default(string, '|', (String) null, 2, (Object) null);
                    }
                }
                if (sdk_version == null) {
                    return "SDK not unpacked";
                }
                StringBuilder sb = new StringBuilder(RegisterSpec.PREFIX);
                sb.append(sdk_version);
                sb.append(Deobfuscator$exteraGramDev$TMessagesProj.getString(sdk_beta ? "-beta" : "");
                return sb.toString();
            }

            @JvmStatic
            public final CharSequence getStateString() {
                int status = getStatus();
                if (status == 0) {
                    return getVersion();
                }
                if (status == 1) {
                    return LocaleController.getString(R.string.CheckingForUpdates);
                }
                if (status != 2) {
                    if (status == 3) {
                        return LocaleController.getString(R.string.LoadingUpdate);
                    }
                    if (status != 4) {
                        return null;
                    }
                    return LocaleController.getString(R.string.RestartPluginSystemToApplyUpdate);
                }
                return LocaleController.getString(R.string.LatestVersionInstalled) + " (" + ((Object) getVersion()) + ')';
            }

            @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0014\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010%\u001a\u00020&J\u0006\u0010'\u001a\u00020\u000bR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001c\u0010\u0016\u001a\u0004\u0018\u00010\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0013\"\u0004\b\u0018\u0010\u0015R\u001c\u0010\u0019\u001a\u0004\u0018\u00010\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0013\"\u0004\b\u001b\u0010\u0015R\u001c\u0010\u001c\u001a\u0004\u0018\u00010\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u0013\"\u0004\b\u001e\u0010\u0015R\u001c\u0010\u001f\u001a\u0004\u0018\u00010\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u0013\"\u0004\b!\u0010\u0015R\u001c\u0010\"\u001a\u0004\u0018\u00010\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b#\u0010\u0013\"\u0004\b$\u0010\u0015¨\u0006("}, d2 = {"Lcom/exteragram/messenger/plugins/PythonPluginsEngine$Updater$Companion$PythonSdkUpdateInfo;", "Lorg/telegram/tgnet/TLRPC$TL_help_appUpdate;", "<init>", "()V", "message", "Lorg/telegram/tgnet/TLRPC$Message;", "getMessage", "()Lorg/telegram/tgnet/TLRPC$Message;", "setMessage", "(Lorg/telegram/tgnet/TLRPC$Message;)V", "available", _UrlKt.FRAGMENT_ENCODE_SET, "getAvailable", "()Z", "setAvailable", "(Z)V", "channel", _UrlKt.FRAGMENT_ENCODE_SET, "getChannel", "()Ljava/lang/String;", "setChannel", "(Ljava/lang/String;)V", "appVersionOperator", "getAppVersionOperator", "setAppVersionOperator", "appVersion", "getAppVersion", "setAppVersion", "appVersionCodeOperator", "getAppVersionCodeOperator", "setAppVersionCodeOperator", "appVersionCode", "getAppVersionCode", "setAppVersionCode", "abi", "getAbi", "setAbi", "clear", _UrlKt.FRAGMENT_ENCODE_SET, "canInstall", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
            public static final class PythonSdkUpdateInfo extends TLRPC.TL_help_appUpdate {
                private String abi;
                private String appVersion;
                private String appVersionCode;
                private String appVersionCodeOperator;
                private String appVersionOperator;
                private boolean available;
                private String channel;
                private TLRPC.Message message;

                public PythonSdkUpdateInfo() {
                    clear();
                }

                public final TLRPC.Message getMessage() {
                    return this.message;
                }

                public final void setMessage(TLRPC.Message message) {
                    this.message = message;
                }

                public final boolean getAvailable() {
                    return this.available;
                }

                public final void setAvailable(boolean z) {
                    this.available = z;
                }

                public final String getChannel() {
                    return this.channel;
                }

                public final void setChannel(String str) {
                    this.channel = str;
                }

                public final String getAppVersionOperator() {
                    return this.appVersionOperator;
                }

                public final void setAppVersionOperator(String str) {
                    this.appVersionOperator = str;
                }

                public final String getAppVersion() {
                    return this.appVersion;
                }

                public final void setAppVersion(String str) {
                    this.appVersion = str;
                }

                public final String getAppVersionCodeOperator() {
                    return this.appVersionCodeOperator;
                }

                public final void setAppVersionCodeOperator(String str) {
                    this.appVersionCodeOperator = str;
                }

                public final String getAppVersionCode() {
                    return this.appVersionCode;
                }

                public final void setAppVersionCode(String str) {
                    this.appVersionCode = str;
                }

                public final String getAbi() {
                    return this.abi;
                }

                public final void setAbi(String str) {
                    this.abi = str;
                }

                public final void clear() {
                    this.message = null;
                    this.available = false;
                    this.can_not_skip = false;
                    this.channel = null;
                    this.version = null;
                    this.appVersion = null;
                    this.appVersionOperator = null;
                    this.appVersionCode = null;
                    this.appVersionCodeOperator = null;
                    this.document = null;
                    this.abi = null;
                }

                public final boolean canInstall() {
                    String str = this.appVersion;
                    String str2 = this.appVersionOperator;
                    String str3 = this.appVersionCode;
                    String str4 = this.appVersionCodeOperator;
                    String str5 = this.version;
                    return (str == null || str2 == null || Updater.INSTANCE.isAppVersionCompatible(str2, str)) && (str3 == null || str4 == null || Updater.INSTANCE.isAppVersionCodeCompatible(str4, str3)) && ((str5 == null || Updater.INSTANCE.isSdkVersionNewer(str5, Intrinsics.areEqual(this.channel, "beta"))) && this.document != null && Intrinsics.areEqual(this.abi, Build.SUPPORTED_ABIS[0]));
                }
            }

            @JvmStatic
            public final InputStream sdkFromApk() throws IOException {
                InputStream inputStreamOpen = ApplicationLoader.applicationContext.getAssets().open("plugins_pysdk/sdk-" + Build.SUPPORTED_ABIS[0] + ".zip");
                "open(...)";
                return inputStreamOpen;
            }

            @JvmStatic
            public final boolean isSdkFromApk() {
                return new File(new File(ApplicationLoader.getFilesDirFixed(), "chaquopy"), ".currentSdkFromApk").exists() || requestSdkFromApkFile().exists();
            }

            @JvmStatic
            public final void setBuildFromApk(boolean fromApk) {
                File file = new File(new File(ApplicationLoader.getFilesDirFixed(), "chaquopy"), ".currentSdkFromApk");
                if (file.exists() && !fromApk) {
                    PythonPluginsEngine.INSTANCE.deleteFileIfExists(file);
                }
                if (file.exists() || !fromApk) {
                    return;
                }
                touchFile(file);
            }

            @JvmStatic
            public final String hashBytes(InputStream inputStream) {
                int i;
                "inputStream";
                try {
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
                        byte[] bArr = new byte[1048576];
                        while (true) {
                            int i2 = inputStream.read(bArr);
                            if (i2 <= 0) {
                                break;
                            }
                            messageDigest.update(bArr, 0, i2);
                        }
                        byte[] bArrDigest = messageDigest.digest();
                        StringBuilder sb = new StringBuilder(bArrDigest.length * 2);
                        for (byte b2 : bArrDigest) {
                            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                            String str = String.format("%02x", Arrays.copyOf(new Object[]{Byte.valueOf(b2)}, 1));
                            "format(...)";
                            sb.append(str);
                        }
                        String string = sb.toString();
                        "toString(...)";
                        CloseableKt.closeFinally(inputStream, null);
                        return string;
                    } catch (Throwable th) {
                        try {
                            throw th;
                        } catch (Throwable th2) {
                            CloseableKt.closeFinally(inputStream, th);
                            throw th2;
                        }
                    }
                } catch (IOException e) {
                    HttpUrl$$ExternalSyntheticBUOutline0.m(e);
                    return null;
                } catch (NoSuchAlgorithmException e2) {
                    HttpUrl$$ExternalSyntheticBUOutline0.m(e2);
                    return null;
                }
            }

            @JvmStatic
            public final File getPythonSdkUpdateFile() {
                return new File(new File(ApplicationLoader.getFilesDirFixed(), "chaquopy"), "newSdk");
            }

            @JvmStatic
            public final File getPythonCurrentSdkFile() {
                return new File(new File(ApplicationLoader.getFilesDirFixed(), "chaquopy"), "currentSdk");
            }

            @JvmStatic
            public final File requestSdkFromApkFile() {
                return new File(new File(ApplicationLoader.getFilesDirFixed(), "chaquopy"), ".restoreSdk");
            }

            @JvmStatic
            public final void deleteSdkUpdateFile() {
                File pythonSdkUpdateFile = getPythonSdkUpdateFile();
                if (pythonSdkUpdateFile.exists()) {
                    PythonPluginsEngine.INSTANCE.deleteFileIfExists(pythonSdkUpdateFile);
                    updateStatus(0);
                }
            }

            @JvmStatic
            public final void checkUpdates() {
                checkUpdates(false);
            }

            @JvmStatic
            public final void checkUpdates(boolean force) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                if ((getStatus() != 1 || Math.abs(jCurrentTimeMillis - Updater.lastCheckUpdateTime) >= 6000) && getStatus() <= 2) {
                    if (!force) {
                        boolean z = Math.abs(jCurrentTimeMillis - ExteraConfig.getSdkUpdateScheduleTimestamp()) < DurationKt.MILLIS_IN_HOUR;
                        if (!ExteraConfig.getPluginsEngine() || ExteraConfig.getPluginsSafeMode() || z) {
                            return;
                        }
                    }
                    ExteraConfig.setSdkUpdateScheduleTimestamp(jCurrentTimeMillis);
                    updateStatus(1);
                    Updater.lastCheckUpdateTime = jCurrentTimeMillis;
                    RemoteUtils.searchMessages("python_sdk", new TLRPC.TL_inputMessagesFilterDocument(), new Utilities.Callback2() { 
                        @Override 
                        public final void run(Object obj, Object obj2) {
                            PythonPluginsEngine.Updater.Companion.m1321$r8$lambda$us7HlP4jQnbH6ikxQnBYzBZPGU((TLRPC.messages_Messages) obj, (TLRPC.TL_error) obj2);
                        }
                    }, 3000);
                }
            }

            public static void m1321$r8$lambda$us7HlP4jQnbH6ikxQnBYzBZPGU(TLRPC.messages_Messages messages_messages, TLRPC.TL_error tL_error) {
                Companion companion;
                final PythonSdkUpdateInfo pythonSdkUpdateResponse;
                boolean z = false;
                if (tL_error != null) {
                    FileLog.e("Failed to search messages with sdk updates: " + tL_error.text);
                } else if (messages_messages != null && (pythonSdkUpdateResponse = (companion = Updater.INSTANCE).parsePythonSdkUpdateResponse(messages_messages)) != null) {
                    if (!ExteraConfig.getPluginsPySdkAutoUpdate() && !pythonSdkUpdateResponse.can_not_skip) {
                        final BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
                        if (safeLastFragment != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() { 
                                @Override // java.lang.Runnable
                                public final void run() {
                                    PythonPluginsEngine.Updater.Companion.checkUpdates$lambda$0$0(safeLastFragment, pythonSdkUpdateResponse);
                                }
                            });
                        }
                        z = true;
                    }
                    if (!z) {
                        try {
                            companion.savePythonSdkArchive(pythonSdkUpdateResponse.getMessage(), pythonSdkUpdateResponse.document);
                            z = true;
                        } catch (IOException e) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Failed to load python-plugins-sdk file (");
                            sb.append(pythonSdkUpdateResponse.getChannel());
                            sb.append(", message id = ");
                            TLRPC.Message message = pythonSdkUpdateResponse.getMessage();
                            sb.append(message != null ? Integer.valueOf(message.id) : null);
                            sb.append(')');
                            FileLog.e(sb.toString(), e);
                        }
                    }
                }
                if (z) {
                    return;
                }
                Updater.INSTANCE.updateStatus(2);
            }

            public static final void checkUpdates$lambda$0$0(BaseFragment baseFragment, PythonSdkUpdateInfo pythonSdkUpdateInfo) {
                baseFragment.showDialog(new PythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1(pythonSdkUpdateInfo, baseFragment.getParentActivity(), baseFragment.getCurrentAccount()));
            }

            @JvmStatic
            public final void restoreSdkFromApk() {
                touchFile(requestSdkFromApkFile());
            }

            private final void touchFile(File file) {
                Object objM2315constructorimpl;
                try {
                    Result.Companion companion = Result.INSTANCE;
                    objM2315constructorimpl = Result.m2315constructorimpl(SimpliFiles.file(file).touch());
                } catch (Throwable th) {
                    Result.Companion companion2 = Result.INSTANCE;
                    objM2315constructorimpl = Result.m2315constructorimpl(ResultKt.createFailure(th));
                }
                Throwable thM2318exceptionOrNullimpl = Result.m2318exceptionOrNullimpl(objM2315constructorimpl);
                if (thM2318exceptionOrNullimpl != null) {
                    FileLog.e(thM2318exceptionOrNullimpl);
                }
            }

            public final void updateStatus(int newStatus) {
                setStatus(newStatus);
                if (getNotifyWhenChangeStatus()) {
                    AndroidUtilities.cancelRunOnUIThread(Updater.notifyRunnable);
                    AndroidUtilities.runOnUIThread(Updater.notifyRunnable, newStatus == 1 ? 0L : 600L);
                }
            }

            @JvmStatic
            public final PythonSdkUpdateInfo parsePythonSdkUpdateResponse(TLRPC.messages_Messages res) {
                -54024115340847L);
                PythonSdkUpdateInfo pythonSdkUpdateInfo = new PythonSdkUpdateInfo();
                Iterator<TLRPC.Message> it = res.messages.iterator();
                "iterator(...)";
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    TLRPC.Message next = it.next();
                    if (next instanceof TLRPC.TL_message) {
                        TLRPC.TL_message tL_message = (TLRPC.TL_message) next;
                        if (!TextUtils.isEmpty(tL_message.message) && (tL_message.media instanceof TLRPC.TL_messageMediaDocument)) {
                            String str = tL_message.message;
                            "message";
                            boolean zContains$default = StringsKt.contains$default((CharSequence) str, (CharSequence) "python_sdk_stable", false, 2, (Object) null);
                            String str2 = tL_message.message;
                            "message";
                            boolean zContains$default2 = StringsKt.contains$default((CharSequence) str2, (CharSequence) "python_sdk_beta", false, 2, (Object) null);
                            if (zContains$default || zContains$default2) {
                                if (!zContains$default2 || ExteraConfig.getPluginsPySdkBetaVersions()) {
                                    StringBuilder sb = new StringBuilder();
                                    String str3 = tL_message.message;
                                    "message";
                                    Iterator it2 = StringsKt.split$default((CharSequence) str3, new String[]{"\n"}, false, 0, 6, (Object) null).iterator();
                                    boolean z = false;
                                    while (it2.hasNext()) {
                                        String string = StringsKt.trim((CharSequence) it2.next()).toString();
                                        if (!TextUtils.isEmpty(string) || !z) {
                                            if (StringsKt.startsWith$default(string, "python_sdk_", false, 2, (Object) null)) {
                                                pythonSdkUpdateInfo.setChannel(Deobfuscator$exteraGramDev$TMessagesProj.getString(zContains$default2 ? "beta" : "stable");
                                                z = true;
                                            } else if (z) {
                                                Matcher matcher = Updater.PYTHON_SDK_APP_VERSION_PATTERN.matcher(string);
                                                if (!matcher.matches()) {
                                                    Matcher matcher2 = Updater.PYTHON_SDK_APP_VERSION_CODE_PATTERN.matcher(string);
                                                    if (matcher2.matches()) {
                                                        pythonSdkUpdateInfo.setAppVersionCodeOperator(matcher2.group(1));
                                                        String strGroup = matcher2.group(2);
                                                        pythonSdkUpdateInfo.setAppVersionCode(strGroup != null ? StringsKt.trim((CharSequence) strGroup).toString() : null);
                                                    } else {
                                                        List listSplit$default = StringsKt.split$default((CharSequence) string, new String[]{"="}, false, 2, 2, (Object) null);
                                                        if (listSplit$default.size() == 2) {
                                                            String string2 = StringsKt.trim((CharSequence) listSplit$default.get(0)).toString();
                                                            String string3 = StringsKt.trim((CharSequence) listSplit$default.get(1)).toString();
                                                            int iHashCode = string2.hashCode();
                                                            if (iHashCode != -1085916422) {
                                                                if (iHashCode != 96360) {
                                                                    if (iHashCode == 351608024 && string2.equals("version")) {
                                                                        pythonSdkUpdateInfo.version = string3;
                                                                    }
                                                                } else if (string2.equals("abi")) {
                                                                    pythonSdkUpdateInfo.setAbi(string3);
                                                                }
                                                            } else if (string2.equals("can_not_skip")) {
                                                                pythonSdkUpdateInfo.can_not_skip = Boolean.parseBoolean(string3);
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    pythonSdkUpdateInfo.setAppVersionOperator(matcher.group(1));
                                                    String strGroup2 = matcher.group(2);
                                                    pythonSdkUpdateInfo.setAppVersion(strGroup2 != null ? StringsKt.trim((CharSequence) strGroup2).toString() : null);
                                                }
                                            } else {
                                                sb.append(string);
                                                sb.append("\n");
                                            }
                                        }
                                    }
                                    pythonSdkUpdateInfo.document = tL_message.media.document;
                                    if (!pythonSdkUpdateInfo.canInstall()) {
                                        pythonSdkUpdateInfo.clear();
                                    } else {
                                        pythonSdkUpdateInfo.text = sb.toString();
                                        ArrayList<TLRPC.MessageEntity> arrayList = new ArrayList<>();
                                        "iterator(...)";
                                        for (TLRPC.MessageEntity messageEntity : tL_message.entities) {
                                            if (!(messageEntity instanceof TLRPC.TL_messageEntityPre)) {
                                                arrayList.add(messageEntity);
                                            }
                                        }
                                        pythonSdkUpdateInfo.entities = arrayList;
                                        pythonSdkUpdateInfo.setMessage(next);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (pythonSdkUpdateInfo.getMessage() == null) {
                    return null;
                }
                pythonSdkUpdateInfo.setAvailable((pythonSdkUpdateInfo.document == null || TextUtils.isEmpty(pythonSdkUpdateInfo.version)) ? false : true);
                return pythonSdkUpdateInfo;
            }

            @JvmStatic
            public final boolean isSdkVersionNewer(String remoteVersion, boolean isBeta) {
                "remoteVersion";
                if (!ExteraConfig.getPluginsPySdkBetaVersions() && PythonPluginsEngine.INSTANCE.getSDK_BETA()) {
                    return !isBeta;
                }
                Companion companion = PythonPluginsEngine.INSTANCE;
                if (companion.getSDK_VERSION() != null) {
                    return AppUtils.compareVersions(">", remoteVersion, companion.getSDK_VERSION());
                }
                return false;
            }

            @JvmStatic
            public final boolean isAppVersionCompatible(String operator, String targetVersion) {
                "operator";
                "targetVersion";
                return AppUtils.compareVersions(operator, BuildVars.BUILD_VERSION_STRING, targetVersion);
            }

            @JvmStatic
            public final boolean isAppVersionCodeCompatible(String operator, String targetVersion) {
                "operator";
                "targetVersion";
                return AppUtils.compareVersions(operator, BuildVars.BUILD_VERSION, Integer.parseInt(targetVersion));
            }

            @JvmStatic
            public final void zipFolder(File sourceDir, File zipFile) {
                "sourceDir";
                "zipFile";
                SimpliFiles.directory(sourceDir).zipTo(zipFile, OverwritePolicy.REPLACE);
            }

            public final void copyArchiveToPluginsDirectory(TLRPC.Document document, boolean autoRestartEngine) {
                File pythonSdkUpdateFile = getPythonSdkUpdateFile();
                try {
                    File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document);
                    "getPathToAttach(...)";
                    SimpliFiles.file(pathToAttach).copyTo(pythonSdkUpdateFile, OverwritePolicy.REPLACE);
                    if (autoRestartEngine) {
                        PluginsController.INSTANCE.getInstance().restart();
                    } else {
                        updateStatus(4);
                    }
                } catch (IOException e) {
                    FileLog.e("Failed to copy plugins-sdk file", e);
                    updateStatus(2);
                } catch (Exception e2) {
                    FileLog.e("Failed to copy plugins-sdk file", e2);
                    updateStatus(2);
                }
                Updater.isLoading = false;
            }

            @JvmStatic
            public final void savePythonSdkArchive(TLRPC.Message msg, TLRPC.Document document) {
                savePythonSdkArchive(msg, document, false);
            }

            @JvmStatic
            public final void savePythonSdkArchive(TLRPC.Message msg, final TLRPC.Document document, final boolean autoRestartEngine) {
                if (Updater.isLoading || msg == null || document == null) {
                    return;
                }
                MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, msg, false, true);
                Updater.isLoading = true;
                updateStatus(3);
                if (!messageObject.mediaExists) {
                    Updater.TAG = DownloadController.getInstance(UserConfig.selectedAccount).generateObserverTag();
                    FileLoader.getInstance(UserConfig.selectedAccount).loadFile(document, messageObject, 1, 0);
                    DownloadController.getInstance(UserConfig.selectedAccount).addLoadingFileObserver(FileLoader.getAttachFileName(document), messageObject, new DownloadController.FileDownloadProgressListener() { 
                        @Override 
                        public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
                            "fileName";
                        }

                        @Override 
                        public void onProgressUpload(String fileName, long downloadSize, long totalSize, boolean isEncrypted) {
                            "fileName";
                        }

                        @Override 
                        public void onFailedDownload(String fileName, boolean canceled) {
                            "fileName";
                            FileLog.e("Failed to load plugins-sdk file");
                            PythonPluginsEngine.Updater.isLoading = false;
                            PythonPluginsEngine.Updater.INSTANCE.updateStatus(2);
                        }

                        @Override 
                        public void onSuccessDownload(String fileName) {
                            "fileName";
                            PythonPluginsEngine.Updater.INSTANCE.copyArchiveToPluginsDirectory(document, autoRestartEngine);
                        }

                        @Override 
                        public int getObserverTag() {
                            return PythonPluginsEngine.Updater.TAG;
                        }
                    });
                    return;
                }
                copyArchiveToPluginsDirectory(document, autoRestartEngine);
            }
        }

        private Updater() {
        }
    }
}
