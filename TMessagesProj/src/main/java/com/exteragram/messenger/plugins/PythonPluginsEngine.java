package com.exteragram.messenger.plugins;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import androidx.core.content.FileProvider;
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
import com.sun.jna.Callback;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlin.time.DurationKt;
import okhttp3.internal.url._UrlKt;
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

@Metadata(d1 = {"\u0000ô\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u0007\u0018\u0000 \u009b\u00012\u00020\u0001:\u0006\u009b\u0001\u009c\u0001\u009d\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u001b\u001a\u00020\u001cH\u0002J\n\u0010\u001d\u001a\u0004\u0018\u00010\u0011H\u0002J\b\u0010\u001e\u001a\u00020\u001fH\u0002J\u001c\u0010 \u001a\u00020\u000f2\b\u0010!\u001a\u0004\u0018\u00010\"2\b\u0010#\u001a\u0004\u0018\u00010$H\u0016J\b\u0010%\u001a\u00020\u000fH\u0016J\b\u0010&\u001a\u00020\u0007H\u0002J\u0018\u0010'\u001a\u00020\u001f2\u0006\u0010(\u001a\u00020\"2\u0006\u0010)\u001a\u00020\u000fH\u0002J\b\u0010*\u001a\u00020\u000fH\u0002J\b\u0010+\u001a\u00020\u001fH\u0002J \u0010,\u001a\u00020\u001f2\u0006\u0010-\u001a\u00020\u00072\u0006\u0010!\u001a\u00020\"2\u0006\u0010.\u001a\u00020\u0006H\u0002J\u0010\u0010/\u001a\u00020\u001f2\u0006\u00100\u001a\u000201H\u0016J\b\u00102\u001a\u00020\u001fH\u0016J\b\u00103\u001a\u00020\u001fH\u0002J\b\u00104\u001a\u00020\u001fH\u0002J\u0010\u00105\u001a\u00020\u001f2\u0006\u00100\u001a\u000201H\u0016J\u0010\u00106\u001a\u00020\u001f2\b\u00100\u001a\u0004\u0018\u000101J\u0016\u00107\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u0006J \u00107\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00062\b\u0010:\u001a\u0004\u0018\u00010;J*\u00107\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00062\b\u0010:\u001a\u0004\u0018\u00010;2\b\u0010<\u001a\u0004\u0018\u00010=J\"\u0010>\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u0010?\u001a\u00020;2\b\u0010<\u001a\u0004\u0018\u00010=H\u0002J&\u0010@\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u0010A\u001a\u00020\u00062\f\u0010B\u001a\b\u0012\u0004\u0012\u00020\u00060CH\u0002J\u0010\u0010D\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u0006H\u0002J\b\u0010E\u001a\u00020\u001fH\u0002J\u0018\u0010F\u001a\u00020\u001f2\u0006\u0010-\u001a\u00020\u00072\u0006\u0010G\u001a\u00020\u0006H\u0002J\b\u0010H\u001a\u00020\u001fH\u0002J\u0018\u0010I\u001a\u00020\u001f2\u0006\u0010J\u001a\u00020\u00072\u0006\u0010G\u001a\u00020\u0006H\u0002J*\u0010K\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00062\u0006\u0010?\u001a\u00020;2\b\u0010<\u001a\u0004\u0018\u00010=H\u0002J\u000e\u0010L\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u0006J\u001a\u0010M\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\b\u0010N\u001a\u0004\u0018\u00010\"H\u0002J\"\u0010O\u001a\u00020\u001f2\u0006\u0010-\u001a\u00020\u00072\u0006\u00108\u001a\u00020\u00062\b\u0010N\u001a\u0004\u0018\u00010\"H\u0002J(\u0010P\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0006\u0010Q\u001a\u00020\u000f2\u000e\u00100\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010RH\u0016J \u0010S\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u000e\u00100\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010RH\u0016J\u0010\u0010T\u001a\u00020\u00062\u0006\u0010U\u001a\u00020\u0006H\u0016J\b\u0010V\u001a\u00020\u000fH\u0016J\u0010\u0010W\u001a\u00020\u001f2\u0006\u0010U\u001a\u00020\u0006H\u0016J\u0010\u0010X\u001a\u00020\u001f2\u0006\u0010U\u001a\u00020\u0006H\u0016J(\u0010Y\u001a\u00020\u001f2\u0006\u00109\u001a\u00020\u00062\b\u0010?\u001a\u0004\u0018\u00010;2\u000e\u00100\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010RJ2\u0010Y\u001a\u00020\u001f2\u0006\u00109\u001a\u00020\u00062\b\u0010?\u001a\u0004\u0018\u00010;2\u000e\u00100\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010R2\b\u0010<\u001a\u0004\u0018\u00010=J\u0012\u0010Z\u001a\u0004\u0018\u00010\u00062\u0006\u00108\u001a\u00020\u0006H\u0002J\u000e\u0010[\u001a\u00020\\2\u0006\u00109\u001a\u00020\u0006J\u001a\u0010]\u001a\b\u0012\u0004\u0012\u00020_0^2\f\u0010`\u001a\b\u0012\u0004\u0012\u00020\u00070^J\u0018\u0010a\u001a\n\u0012\u0004\u0012\u00020_\u0018\u00010^2\u0006\u0010U\u001a\u00020\u0006H\u0016J\u0010\u0010b\u001a\u00020\u001f2\u0006\u0010c\u001a\u00020\u0006H\u0016J_\u0010d\u001a\b\u0012\u0004\u0012\u0002Hf0e\"\u0004\b\u0000\u0010f2\b\u0010g\u001a\u0004\u0018\u00010\u00072\b\u0010h\u001a\u0004\u0018\u0001Hf2\f\u0010i\u001a\b\u0012\u0004\u0012\u0002Hf0j2\u0006\u0010k\u001a\u00020\u00062\f\u0010l\u001a\b\u0012\u0004\u0012\u0002Hf0m2\f\u0010n\u001a\b\u0012\u0004\u0012\u00020o0RH\u0002¢\u0006\u0002\u0010pJ]\u0010d\u001a\b\u0012\u0004\u0012\u0002Hf0e\"\u0004\b\u0000\u0010f2\u0006\u00108\u001a\u00020\u00062\b\u0010h\u001a\u0004\u0018\u0001Hf2\f\u0010i\u001a\b\u0012\u0004\u0012\u0002Hf0j2\u0006\u0010k\u001a\u00020\u00062\f\u0010l\u001a\b\u0012\u0004\u0012\u0002Hf0m2\f\u0010n\u001a\b\u0012\u0004\u0012\u00020o0RH\u0002¢\u0006\u0002\u0010qJ0\u0010r\u001a\b\u0012\u0004\u0012\u00020s0e2\u0006\u0010t\u001a\u00020\u00062\u0006\u0010u\u001a\u00020v2\b\u0010w\u001a\u0004\u0018\u00010s2\u0006\u00108\u001a\u00020\u0006H\u0016J:\u0010x\u001a\b\u0012\u0004\u0012\u00020y0e2\u0006\u0010t\u001a\u00020\u00062\u0006\u0010u\u001a\u00020v2\b\u0010z\u001a\u0004\u0018\u00010s2\b\u0010{\u001a\u0004\u0018\u00010|2\b\u0010g\u001a\u0004\u0018\u00010\u0007J:\u0010x\u001a\b\u0012\u0004\u0012\u00020y0e2\u0006\u0010t\u001a\u00020\u00062\u0006\u0010u\u001a\u00020v2\b\u0010z\u001a\u0004\u0018\u00010s2\b\u0010{\u001a\u0004\u0018\u00010|2\u0006\u00108\u001a\u00020\u0006H\u0016J1\u0010}\u001a\b\u0012\u0004\u0012\u00020~0e2\u0006\u0010\u007f\u001a\u00020\u00062\u0006\u0010u\u001a\u00020v2\t\u0010\u0080\u0001\u001a\u0004\u0018\u00010~2\u0006\u00108\u001a\u00020\u0006H\u0016J5\u0010\u0081\u0001\u001a\t\u0012\u0005\u0012\u00030\u0082\u00010e2\u0007\u0010\u0083\u0001\u001a\u00020\u00062\u0006\u0010u\u001a\u00020v2\n\u0010\u0084\u0001\u001a\u0005\u0018\u00010\u0082\u00012\u0006\u00108\u001a\u00020\u0006H\u0016J,\u0010\u0085\u0001\u001a\t\u0012\u0005\u0012\u00030\u0086\u00010e2\u0006\u0010u\u001a\u00020v2\n\u0010\u0087\u0001\u001a\u0005\u0018\u00010\u0086\u00012\u0006\u00108\u001a\u00020\u0006H\u0016J\u001c\u0010\u0088\u0001\u001a\u0004\u0018\u00010\u00062\b\u00109\u001a\u0004\u0018\u00010\u00062\u0007\u0010\u0089\u0001\u001a\u00020\u0006J\u001e\u0010\u008a\u0001\u001a\u000f\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u008b\u00012\b\u00109\u001a\u0004\u0018\u00010\u0006J'\u0010\u008c\u0001\u001a\u0004\u0018\u00010\u000b2\u0006\u00108\u001a\u00020\u00062\u0007\u0010\u008d\u0001\u001a\u00020\u00062\t\u0010\u008e\u0001\u001a\u0004\u0018\u00010\u000bH\u0016J%\u0010\u008f\u0001\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0007\u0010\u008d\u0001\u001a\u00020\u00062\t\u0010\u0090\u0001\u001a\u0004\u0018\u00010\u000bH\u0016J\u0011\u0010\u0091\u0001\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u0006H\u0016J\u001e\u0010\u0092\u0001\u001a\u000f\u0012\u0004\u0012\u00020\u0006\u0012\u0002\b\u0003\u0018\u00010\u008b\u00012\u0006\u00108\u001a\u00020\u0006H\u0016J\u001d\u0010\u0093\u0001\u001a\u00020\u001f2\b\u0010\u0094\u0001\u001a\u00030\u0095\u00012\b\u0010\u0087\u0001\u001a\u00030\u0096\u0001H\u0016J\u001b\u0010\u0097\u0001\u001a\u00020\u001f2\u0006\u0010U\u001a\u00020\u00062\b\u0010\u0094\u0001\u001a\u00030\u0095\u0001H\u0016J\u001c\u0010\u0097\u0001\u001a\u00020\u001f2\u0007\u0010\u0098\u0001\u001a\u00020;2\b\u0010\u0094\u0001\u001a\u00030\u0095\u0001H\u0016J%\u0010\u0099\u0001\u001a\u00020\u001f2\u0007\u0010\u0098\u0001\u001a\u00020;2\u0007\u0010\u009a\u0001\u001a\u00020\u00062\b\u0010\u0094\u0001\u001a\u00030\u0095\u0001H\u0016J$\u0010\u0099\u0001\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00062\u0007\u0010\u009a\u0001\u001a\u00020\u00062\b\u0010\u0094\u0001\u001a\u00030\u0095\u0001H\u0016R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR(\u0010\n\u001a\u001c\u0012\u0004\u0012\u00020\u0006\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000RN\u0010\f\u001aB\u0012\f\u0012\n \u000e*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000e*\u0004\u0018\u00010\u000f0\u000f \u000e* \u0012\f\u0012\n \u000e*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000e*\u0004\u0018\u00010\u000f0\u000f\u0018\u00010\r0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u0013\u001a\u0004\u0018\u00010\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001c\u0010\u0018\u001a\u0004\u0018\u00010\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u0015\"\u0004\b\u001a\u0010\u0017¨\u0006\u009e\u0001"}, d2 = {"Lcom/exteragram/messenger/plugins/PythonPluginsEngine;", "Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", "<init>", "()V", "pluginInstances", "Ljava/util/concurrent/ConcurrentHashMap;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/chaquo/python/PyObject;", "getPluginInstances", "()Ljava/util/concurrent/ConcurrentHashMap;", "settingsCache", _UrlKt.FRAGMENT_ENCODE_SET, "dependencyPaths", "Ljava/util/concurrent/ConcurrentHashMap$KeySetView;", "kotlin.jvm.PlatformType", _UrlKt.FRAGMENT_ENCODE_SET, "python", "Lcom/chaquo/python/Python;", "devServerClass", "debuggerListener", "getDebuggerListener", "()Lcom/chaquo/python/PyObject;", "setDebuggerListener", "(Lcom/chaquo/python/PyObject;)V", "basePluginClass", "getBasePluginClass", "setBasePluginClass", "getPluginsController", "Lcom/exteragram/messenger/plugins/PluginsController;", "getPython", "initPython", _UrlKt.FRAGMENT_ENCODE_SET, "isPlugin", "file", "Ljava/io/File;", "messageObject", "Lorg/telegram/messenger/MessageObject;", "isEngineAvailable", "requireBasePluginClass", "installSdkArchive", "archiveFile", "fromApk", "initSdk", "stopAndUnloadSdk", "removeModulesRecursive", "sysModules", "prefix", "init", Callback.METHOD_NAME, "Ljava/lang/Runnable;", "checkDevServer", "runDevServer", "stopDevServer", "shutdown", "loadPlugins", "loadPlugin", "pluginId", "filePath", "metadata", "Lcom/exteragram/messenger/plugins/Plugin;", "delegate", "Lcom/exteragram/messenger/plugins/pip/PipController$InstallerDelegate;", "installPluginDependencies", "pluginMetadata", "disableShadowedPlugins", "dependencyName", "providedModules", _UrlKt.FRAGMENT_ENCODE_SET, "removePluginDependencies", "pruneDependencyPaths", "removeModulesUnderPath", "path", "removePluginPathsFromSysPath", "removeFromSysPath", "sysPath", "createPluginInstance", "unloadPlugin", "refreshImportCaches", "moduleDir", "evictPluginModule", "setPluginEnabled", "enabled", "Lorg/telegram/messenger/Utilities$Callback;", "deletePlugin", "getPluginPath", "id", "canOpenInExternalApp", "openInExternalApp", "sharePlugin", "loadPluginFromFile", "findModuleNameOwner", "validatePluginFromFile", "Lcom/exteragram/messenger/plugins/PluginsController$PluginValidationResult;", "parsePySettingDefinitions", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/models/SettingItem;", "pyDefinitionsList", "loadPluginSettings", "executeOnAppEvent", "eventType", "executeHook", "Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "T", "pluginInstance", "initialValue", "valueClass", "Ljava/lang/Class;", "pyResultKey", "caller", "Lcom/exteragram/messenger/plugins/PythonPluginsEngine$PyMethodCaller;", "errorLogger", "Lcom/chaquo/python/PyException;", "(Lcom/chaquo/python/PyObject;Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;Lcom/exteragram/messenger/plugins/PythonPluginsEngine$PyMethodCaller;Lorg/telegram/messenger/Utilities$Callback;)Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;Lcom/exteragram/messenger/plugins/PythonPluginsEngine$PyMethodCaller;Lorg/telegram/messenger/Utilities$Callback;)Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "executePreRequestHook", "Lorg/telegram/tgnet/TLObject;", "requestName", "account", _UrlKt.FRAGMENT_ENCODE_SET, "request", "executePostRequestHook", "Lcom/exteragram/messenger/plugins/hooks/PluginsHooks$PostRequestResult;", "response", "", "Lorg/telegram/tgnet/TLRPC$TL_error;", "executeUpdateHook", "Lorg/telegram/tgnet/TLRPC$Update;", "updateName", "update", "executeUpdatesHook", "Lorg/telegram/tgnet/TLRPC$Updates;", "containerName", "updates", "executeSendMessageHook", "Lorg/telegram/messenger/SendMessagesHelper$SendMessageParams;", "params", "fetchParameterValue", "parameterName", "parsePluginMetadata", _UrlKt.FRAGMENT_ENCODE_SET, "getPluginSetting", "key", "defaultValue", "setPluginSetting", "value", "clearPluginSettings", "getAllPluginSettings", "showInstallDialog", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "Lcom/exteragram/messenger/plugins/ui/components/InstallPluginBottomSheet$PluginInstallParams;", "openPluginSettings", "plugin", "openPluginSetting", "linkAlias", "Companion", "PyMethodCaller", "Updater", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
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

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
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

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public boolean canOpenInExternalApp() {
        return true;
    }

    @Metadata(d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010 \u001a\u00020\u00182\u0006\u0010!\u001a\u00020\u001e2\u0006\u0010\"\u001a\u00020\tH\u0002J\u0012\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u001eH\u0002J\u0012\u0010&\u001a\u00020\u00182\b\u0010!\u001a\u0004\u0018\u00010\u001eH\u0002J\u0014\u0010'\u001a\u0004\u0018\u00010\t2\b\u0010(\u001a\u0004\u0018\u00010\tH\u0002J\u0018\u0010)\u001a\b\u0012\u0004\u0012\u00020\t0*2\b\u0010+\u001a\u0004\u0018\u00010\u001eH\u0002R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u001c\u0010\u0012\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0017\u001a\u00020\u0018X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0018X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006,"}, d2 = {"Lcom/exteragram/messenger/plugins/PythonPluginsEngine$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "VERSION_PATTERN", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "SDK_REQUIRED_MODULES", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "[Ljava/lang/String;", "SDK_ARCHIVE_POLICY", "Lorg/simplifiles/archive/security/SecurityPolicy;", "SDK_ARCHIVE_OPTIONS", "Lorg/simplifiles/archive/ArchiveExtractionOptions;", "MAX_SDK_VERSION_BYTES", _UrlKt.FRAGMENT_ENCODE_SET, "SAFE_MODE_ENABLE_ERROR", "SDK_VERSION", "getSDK_VERSION", "()Ljava/lang/String;", "setSDK_VERSION", "(Ljava/lang/String;)V", "SDK_BETA", _UrlKt.FRAGMENT_ENCODE_SET, "getSDK_BETA", "()Z", "setSDK_BETA", "(Z)V", "SDK_DIR", "Ljava/io/File;", "sdkInitialized", "sdkModuleExists", "sdkDir", "moduleName", "deleteFileIfExists", _UrlKt.FRAGMENT_ENCODE_SET, "file", "isSdkDirValid", "canonicalPathOrNull", "path", "topLevelModuleNames", _UrlKt.FRAGMENT_ENCODE_SET, "dir", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    @SourceDebugExtension({"SMAP\nPythonPluginsEngine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PythonPluginsEngine.kt\ncom/exteragram/messenger/plugins/PythonPluginsEngine$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,2775:1\n1#2:2776\n*E\n"})
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final String getSDK_VERSION() {
            return PythonPluginsEngine.SDK_VERSION;
        }

        public final void setSDK_VERSION(String str) {
            PythonPluginsEngine.SDK_VERSION = str;
        }

        public final boolean getSDK_BETA() {
            return PythonPluginsEngine.SDK_BETA;
        }

        public final void setSDK_BETA(boolean z) {
            PythonPluginsEngine.SDK_BETA = z;
        }

        private final boolean sdkModuleExists(File sdkDir, String moduleName) {
            if (new File(sdkDir, moduleName + ".so").exists()) {
                return true;
            }
            if (new File(sdkDir, moduleName + ".py").exists()) {
                return true;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(moduleName);
            sb.append(".pyc");
            return new File(sdkDir, sb.toString()).exists();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void deleteFileIfExists(File file) {
            if (file == null || !file.exists()) {
                return;
            }
            try {
                SimpliFiles.file(file).delete();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final boolean isSdkDirValid(File sdkDir) {
            if (sdkDir == null || !sdkDir.isDirectory()) {
                return false;
            }
            for (String str : PythonPluginsEngine.SDK_REQUIRED_MODULES) {
                if (!sdkModuleExists(sdkDir, str)) {
                    return false;
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final String canonicalPathOrNull(String path) {
            if (path == null || path.length() == 0) {
                return null;
            }
            try {
                return new File(path).getCanonicalPath();
            } catch (Throwable th) {
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final Set<String> topLevelModuleNames(File dir) {
            File[] fileArrListFiles;
            if (dir == null || (fileArrListFiles = dir.listFiles()) == null) {
                return SetsKt.emptySet();
            }
            HashSet hashSet = new HashSet();
            for (File file : fileArrListFiles) {
                String name = file.getName();
                if (file.isDirectory()) {
                    if (!Intrinsics.areEqual(name, "__pycache__") && !name.endsWith(".dist-info") && !name.endsWith(".data")) {
                        hashSet.add(name);
                    }
                } else if (name.endsWith(".py") || name.endsWith(".pyc") || name.endsWith(".so")) {
                    hashSet.add(StringsKt.substringBefore(name, ".", name));
                }
            }
            return hashSet;
        }
    }

    public final ConcurrentHashMap<String, PyObject> getPluginInstances() {
        return this.pluginInstances;
    }

    public final PyObject getDebuggerListener() {
        return this.debuggerListener;
    }

    public final void setDebuggerListener(PyObject pyObject) {
        this.debuggerListener = pyObject;
    }

    public final PyObject getBasePluginClass() {
        return this.basePluginClass;
    }

    public final void setBasePluginClass(PyObject pyObject) {
        this.basePluginClass = pyObject;
    }

    private final PluginsController getPluginsController() {
        return PluginsController.INSTANCE.getInstance();
    }

    private final synchronized Python getPython() {
        if (this.python == null) {
            initPython();
            if (this.python == null) {
                android.util.Log.e("PYTHON_INIT", "Python initialization failed, unable to proceed.");
                FileLog.e("Python initialization failed, unable to proceed.");
                return null;
            }
        }
        return this.python;
    }

    private final void initPython() {
        try {
            android.util.Log.e("PYTHON_INIT", "initPython(): isStarted=" + Python.isStarted());
            if (!Python.isStarted()) {
                Python.start(new AndroidPlatform(ApplicationLoader.applicationContext));
            }
            this.python = Python.getInstance();
            android.util.Log.e("PYTHON_INIT", "initPython(): python instance=" + this.python);
        } catch (Throwable e) {
            android.util.Log.e("PYTHON_INIT", "Failed to initialize Python", e);
            FileLog.e("Failed to initialize Python", e);
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public boolean isPlugin(File file, MessageObject messageObject) {
        if (file == null) {
            return false;
        }
        String name = file.getName();
        String lowerCase = name.toLowerCase(Locale.ROOT);
        return lowerCase.endsWith(".plugin");
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public boolean isEngineAvailable() {
        return this.python != null && Python.isStarted() && sdkInitialized && this.basePluginClass != null;
    }

    private final synchronized PyObject requireBasePluginClass() {
        try {
            if (ExteraConfig.getPluginsSafeMode()) {
                throw new Exception("Plugins cannot be enabled while safe mode is active");
            }
            if (!sdkInitialized) {
                throw new Exception("Python plugin SDK is not initialized");
            }
            PyObject pyObject = this.basePluginClass;
            if (pyObject != null) {
                return pyObject;
            }
            Python python = this.python;
            if (python == null) {
                throw new Exception("Python interpreter is not initialized");
            }
            PyObject pyObject2 = (PyObject) python.getModule("base_plugin").get((Object) "BasePlugin");
            if (pyObject2 != null) {
                this.basePluginClass = pyObject2;
                return pyObject2;
            }
            throw new Exception("BasePlugin class is missing from the Python SDK");
        } catch (Throwable th2) {
            if (th2 instanceof RuntimeException) {
                throw (RuntimeException) th2;
            }
            throw new RuntimeException(th2);
        }
    }

    private final void installSdkArchive(File archiveFile, boolean fromApk) throws IOException {
        Object objM2315constructorimpl;
        Object objM2315constructorimpl2;
        File file = SDK_DIR;
        if (file == null) {
            throw new IllegalArgumentException("Required value was null.");
        }
        File file2 = new File(file.getParentFile(), "plugins-sdk.tmp");
        try {
            SimpliFiles.archive(archiveFile).withPolicy(SDK_ARCHIVE_POLICY).extractToDirectory(file2, SDK_ARCHIVE_OPTIONS);
            if (!INSTANCE.isSdkDirValid(file2)) {
                throw new IOException("Python SDK archive unpacked with missing required files");
            }
            SimpliFiles.directory(file2).moveTo(file, OverwritePolicy.REPLACE);
            Updater.INSTANCE.setBuildFromApk(fromApk);
            if (file2.exists()) {
                try {
                    SimpliFiles.directory(file2).deleteRecursively();
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            if (file.exists()) {
                return;
            }
            SimpliFiles.directory(file).create();
        } catch (Throwable th2) {
            if (file2.exists()) {
                try {
                    SimpliFiles.directory(file2).deleteRecursively();
                } catch (Throwable th3) {
                    FileLog.e(th3);
                }
            }
            if (!file.exists()) {
                SimpliFiles.directory(file).create();
            }
            if (th2 instanceof IOException) {
                throw (IOException) th2;
            }
            throw new IOException("Failed to install SDK archive", th2);
        }
    }

    private final boolean initSdk() {
        String str;
        Boolean bool;
        sdkInitialized = false;
        Python python = this.python;
        if (python == null) {
            return false;
        }
        if (SDK_DIR == null) {
            File file = new File(new File(ApplicationLoader.getFilesDirFixed(), "chaquopy"), "plugins-sdk");
            SDK_DIR = file;
            if (!file.exists()) {
                File file2 = SDK_DIR;
                if (file2 == null) {
                    throw new IllegalArgumentException("Required value was null.");
                }
                SimpliFiles.directory(file2).create();
            }
        }
        File file3 = SDK_DIR;
        if (file3 == null) {
            throw new IllegalArgumentException("Required value was null.");
        }
        Updater.Companion companion = Updater.INSTANCE;
        File fileRequestSdkFromApkFile = companion.requestSdkFromApkFile();
        File pythonSdkUpdateFile = companion.getPythonSdkUpdateFile();
        File pythonCurrentSdkFile = companion.getPythonCurrentSdkFile();
        boolean zExists = fileRequestSdkFromApkFile.exists();
        if (zExists) {
            SimpliFiles.directory(file3).clean();
            INSTANCE.deleteFileIfExists(fileRequestSdkFromApkFile);
        }
        boolean z = true;
        if (!zExists && pythonSdkUpdateFile.exists()) {
            try {
                SimpliFiles.file(pythonSdkUpdateFile).copyTo(pythonCurrentSdkFile, OverwritePolicy.REPLACE);
                installSdkArchive(pythonCurrentSdkFile, false);
            } catch (IOException e) {
                FileLog.e("Failed to install updated Python SDK archive", e);
                zExists = true;
            }
        }
        File file4 = new File(file3, "v.txt");
        if (file4.exists()) {
            String string = StringsKt.trim((CharSequence) SimpliFile.readText$default(SimpliFiles.file(file4), 65536L, null, 2, null)).toString();
            boolean zEndsWith$default = string.endsWith("|1");
            String strSubstringBefore$default = StringsKt.substringBefore(string, "|", string);
            try {
                InputStream inputStreamOpen = ApplicationLoader.applicationContext.getAssets().open("plugins_pysdk/v.txt");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamOpen, StandardCharsets.UTF_8));
                    try {
                        StringBuilder sb = new StringBuilder();
                        while (true) {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            sb.append(line);
                            sb.append('\n');
                        }
                        String string2 = sb.toString();
                        try {
                            if (AppUtils.compareVersions(zEndsWith$default ? ">=" : ">", StringsKt.trim((CharSequence) string2).toString(), strSubstringBefore$default)) {
                                zExists = true;
                            }
                        } catch (NumberFormatException e2) {
                            FileLog.e("Invalid Python SDK version file. Restoring SDK from APK.", e2);
                        }
                        Unit unit = Unit.INSTANCE;
                        CloseableKt.closeFinally(bufferedReader, null);
                        CloseableKt.closeFinally(inputStreamOpen, null);
                    } catch (Throwable th2) {
                        try {
                            throw th2;
                        } catch (Throwable th3) {
                            CloseableKt.closeFinally(bufferedReader, th2);
                            throw th3;
                        }
                    }
                } catch (Throwable th4) {
                    throw th4;
                }
            } catch (IOException e3) {
                throw new RuntimeException("Failed to read Python SDK version (v.txt) from APK assets", e3);
            }
        }
        if (zExists || !INSTANCE.isSdkDirValid(file3)) {
            if (!zExists) {
                FileLog.w("Python SDK directory is missing required files. Restoring SDK from APK.");
            }
            SimpliFiles.directory(file3).clean();
            try {
                InputStream inputStreamSdkFromApk = Updater.INSTANCE.sdkFromApk();
                try {
                    SimpliFile.writeFromAtomic$default(SimpliFiles.file(pythonCurrentSdkFile), inputStreamSdkFromApk, 0L, 2, null);
                    CloseableKt.closeFinally(inputStreamSdkFromApk, null);
                    installSdkArchive(pythonCurrentSdkFile, true);
                } catch (Throwable th5) {
                    try {
                        throw th5;
                    } catch (Throwable th6) {
                        CloseableKt.closeFinally(inputStreamSdkFromApk, th5);
                        throw th6;
                    }
                }
            } catch (IOException e4) {
                throw new RuntimeException("Failed to install Python SDK from APK", e4);
            }
        }
        Updater.INSTANCE.deleteSdkUpdateFile();
        PyObject module = python.getModule("sys");
        PyObject pyObject = (PyObject) module.get((Object) "path");
        if (pyObject != null) {
            pyObject.callAttr("append", file3.getAbsolutePath());
        }
        try {
            PyObject module2 = python.getModule("_sdk_version");
            PyObject pyObjectCallAttr = module2.callAttr("__start__", new Object[0]);
            if (pyObjectCallAttr == null || !pyObjectCallAttr.toBoolean()) {
                z = false;
            }
            sdkInitialized = z;
            PyObject pyObject2 = (PyObject) module2.get((Object) "__version__");
            if (pyObject2 == null || (str = (String) pyObject2.toJava(String.class)) == null) {
                str = SDK_VERSION;
            }
            SDK_VERSION = str;
            PyObject pyObject3 = (PyObject) module2.get((Object) "__beta__");
            SDK_BETA = (pyObject3 == null || (bool = (Boolean) pyObject3.toJava(Boolean.TYPE)) == null) ? SDK_BETA : bool.booleanValue();
            if (this.basePluginClass == null && sdkInitialized && !ExteraConfig.getPluginsSafeMode()) {
                try {
                    requireBasePluginClass();
                } catch (Exception e5) {
                    FileLog.e("Failed to load BasePlugin class", e5);
                }
            }
            return sdkInitialized;
        } catch (Throwable th7) {
            FileLog.e("Failed to initialize Python SDK bootstrap", th7);
            try {
                Updater.INSTANCE.restoreSdkFromApk();
            } catch (Throwable th8) {
                FileLog.e("Failed to schedule Python SDK restore from APK", th8);
            }
            return false;
        }
    }

    private final void stopAndUnloadSdk() {
        Python python = this.python;
        if (python == null) {
            return;
        }
        this.basePluginClass = null;
        PyObject module = python.getModule("sys");
        PyObject pyObject = (PyObject) module.get((Object) "modules");
        if (pyObject != null) {
            try {
                PyObject pyObjectCallAttr = pyObject.callAttr("get", "_sdk_version");
                if (pyObjectCallAttr != null) {
                    pyObjectCallAttr.callAttr("__stop__", new Object[0]);
                }
            } catch (Throwable th) {
                FileLog.e("Failed to stop Python SDK bootstrap", th);
            }
        }
        sdkInitialized = false;
        File file = SDK_DIR;
        PyObject pyObject2 = (PyObject) module.get((Object) "path");
        if (file != null && pyObject2 != null && pyObject2.callAttr("__contains__", file.getAbsolutePath()).toBoolean()) {
            pyObject2.callAttr("remove", file.getAbsolutePath());
        }
        if (file == null || pyObject == null) {
            return;
        }
        removeModulesRecursive(pyObject, file, "");
    }

    private final void removeModulesRecursive(PyObject sysModules, File file, String prefix) {
        if (Intrinsics.areEqual(prefix, "plugins-sdk.")) {
            prefix = "";
        }
        if (file.isDirectory()) {
            if (sysModules.callAttr("__contains__", prefix + file.getName()).toBoolean()) {
                sysModules.callAttr("pop", prefix + file.getName());
            }
        }
        File[] fileArrListFiles = file.listFiles();
        if (fileArrListFiles != null) {
            for (File file2 : fileArrListFiles) {
                removeModulesRecursive(sysModules, file2, file.getName() + '.');
            }
        }
        String name = file.getName();
        String strSubstringBefore$default = StringsKt.substringBefore(name, ".", name);
        if (file.isDirectory()) {
            return;
        }
        if (sysModules.callAttr("__contains__", prefix + strSubstringBefore$default).toBoolean()) {
            sysModules.callAttr("pop", prefix + strSubstringBefore$default);
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void init(Runnable callback) {
        android.util.Log.e("PYTHON_INIT", "PythonPluginsEngine.init() started on thread: " + Thread.currentThread().getName());
        if (getPython() == null) {
            android.util.Log.e("PYTHON_INIT", "getPython() returned null!");
            callback.run();
            return;
        }
        android.util.Log.e("PYTHON_INIT", "Python interpreter ready, calling initSdk()...");
        try {
            if (!initSdk()) {
                android.util.Log.e("PYTHON_INIT", "initSdk() returned false!");
                callback.run();
                return;
            }
            android.util.Log.e("PYTHON_INIT", "initSdk() returned true!");
            if (!ExteraConfig.getPluginsSafeMode()) {
                final Updater.Companion companion = Updater.INSTANCE;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        companion.checkUpdates();
                    }
                }, 5000L);
            }
            Python python = this.python;
            if (python == null) {
                android.util.Log.e("PYTHON_INIT", "python field was null after initSdk!");
                callback.run();
                return;
            }
            if (!ExteraConfig.getPluginsSafeMode()) {
                try {
                    PyObject module = python.getModule("plugin_settings");
                    Object java = module.callAttr("init", getPluginsController().getPluginsDir().getAbsolutePath(), getPluginsController().getPreferences().getAll()).toJava(String[].class);
                    String[] strArr = (String[]) java;
                    if (!(strArr.length == 0)) {
                        SharedPreferences.Editor editorEdit = getPluginsController().getPreferences().edit();
                        for (String str : strArr) {
                            editorEdit.remove(str);
                        }
                        editorEdit.apply();
                        FileLog.d("Migrated " + strArr.length + " plugin settings from SharedPreferences to JSON.");
                    }
                } catch (PyException e) {
                    FileLog.e("Failed to initialize plugin_settings module", e);
                }
            }
            android.util.Log.e("PYTHON_INIT", "calling PipController cleanup and loadPlugins...");
            PipController.INSTANCE.cleanup();
            loadPlugins(callback);
            checkDevServer();
            android.util.Log.e("PYTHON_INIT", "PythonPluginsEngine.init() completed!");
        } catch (Throwable th) {
            android.util.Log.e("PYTHON_INIT", "Failed to initialize Python plugins engine", th);
            FileLog.e("Failed to initialize Python plugins engine", th);
            callback.run();
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void checkDevServer() {
        if (ExteraConfig.getPluginsSafeMode()) {
            return;
        }
        if (ExteraConfig.getPluginsDevMode()) {
            runDevServer();
        } else {
            stopDevServer();
        }
    }

    private final void runDevServer() {
        Python python;
        if (ExteraConfig.getPluginsSafeMode() || (python = getPython()) == null) {
            return;
        }
        if (this.devServerClass != null) {
            stopDevServer();
        }
        try {
            PyObject pyObject = (PyObject) python.getModule("dev_server").get((Object) "DevServer");
            this.devServerClass = pyObject;
            if (pyObject == null) {
                return;
            }
            if (pyObject != null) {
                pyObject.callAttrThrows("start_server", new Object[0]);
            }
            FileLog.d("Dev server started successfully.");
        } catch (Throwable th) {
            FileLog.e("Failed to initialize dev server", th);
            this.devServerClass = null;
        }
    }

    private final void stopDevServer() {
        PyObject pyObject = this.devServerClass;
        if (pyObject == null) {
            return;
        }
        try {
            pyObject.callAttrThrows("stop_server", new Object[0]);
            FileLog.d("Dev server stopped successfully.");
            this.devServerClass = null;
        } catch (Throwable th) {
            try {
                FileLog.e("Failed to stop dev server", th);
            } finally {
                this.devServerClass = null;
            }
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void shutdown(Runnable callback) {
        if (getPython() == null) {
            callback.run();
            return;
        }
        try {
            stopDevServer();
            Iterator it = new ArrayList(this.pluginInstances.keySet()).iterator();
            while (it.hasNext()) {
                unloadPlugin((String) it.next());
            }
            PyObject pyObject = this.debuggerListener;
            if (pyObject != null) {
                pyObject.close();
            }
            this.debuggerListener = null;
            this.pluginInstances.clear();
            synchronized (this) {
                removePluginPathsFromSysPath();
                stopAndUnloadSdk();
                this.python = null;
                sdkInitialized = false;
                Unit unit = Unit.INSTANCE;
            }
            FileLog.d("Python plugin engine shut down.");
        } catch (Exception e) {
            FileLog.e(e);
        }
        callback.run();
    }

    public final void loadPlugins(final Runnable callback) {
        PluginsController.INSTANCE.runOnPluginsQueue(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                PythonPluginsEngine.m1313$r8$lambda$2QnMYM5kpMDhr3l1kM17QcThy8(PythonPluginsEngine.this, callback);
            }
        });
    }

    /* JADX INFO: renamed from: $r8$lambda$2QnMYM5kpMDhr3l1k-M17QcThy8, reason: not valid java name */
    public static void m1313$r8$lambda$2QnMYM5kpMDhr3l1kM17QcThy8(PythonPluginsEngine pythonPluginsEngine, Runnable runnable) {
        PluginsController.PluginValidationResult pluginValidationResultValidatePluginFromFile;
        Plugin plugin;
        Python python = pythonPluginsEngine.getPython();
        if (python == null) {
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
                return;
            }
            return;
        }
        try {
            PyObject module = python.getModule("sys");
            PyObject pyObject = (PyObject) module.get((Object) "path");
            String absolutePath = pythonPluginsEngine.getPluginsController().getPluginsDir().getAbsolutePath();
            if (!ExteraConfig.getPluginsSafeMode() && pyObject != null && !pyObject.callAttr("__contains__", absolutePath).toBoolean()) {
                pyObject.callAttr("append", absolutePath);
            }
            File[] fileArrListFiles = pythonPluginsEngine.getPluginsController().getPluginsDir().listFiles(new FilenameFilter() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda6
                @Override // java.io.FilenameFilter
                public final boolean accept(File file, String str) {
                    return PythonPluginsEngine.loadPlugins$lambda$0$0(file, str);
                }
            });
            if (fileArrListFiles == null) {
                pythonPluginsEngine.getPluginsController().notifyPluginsChanged();
                if (runnable != null) {
                    AndroidUtilities.runOnUIThread(runnable);
                    return;
                }
                return;
            }
            int i = 0;
            for (File file : fileArrListFiles) {
                String name = file.getName();
                String strSubstring = name.substring(0, file.getName().length() - 3);
                try {
                    String absolutePath2 = file.getAbsolutePath();
                    pluginValidationResultValidatePluginFromFile = pythonPluginsEngine.validatePluginFromFile(absolutePath2);
                    try {
                        if (pluginValidationResultValidatePluginFromFile.getError() != null) {
                            throw new Exception(pluginValidationResultValidatePluginFromFile.getError());
                        }
                        String absolutePath3 = file.getAbsolutePath();
                        pythonPluginsEngine.loadPlugin(strSubstring, absolutePath3, pluginValidationResultValidatePluginFromFile.getPlugin());
                    } catch (Throwable th) {
                        FileLog.e("Failed to load plugin " + file.getName() + ". Reason: " + th.getMessage(), th);
                        Plugin plugin2 = pluginValidationResultValidatePluginFromFile != null ? pluginValidationResultValidatePluginFromFile.getPlugin() : null;
                        if (plugin2 != null) {
                            plugin = new Plugin(strSubstring, plugin2.getName());
                            plugin.setAuthor(plugin2.getAuthor());
                            plugin.setDescription(plugin2.getDescription());
                            plugin.setIcon(plugin2.getIcon());
                            plugin.setVersion(plugin2.getVersion());
                            plugin.setAppVersion(plugin2.getAppVersion());
                            plugin.setSdkVersion(plugin2.getSdkVersion());
                            plugin.setRequirements(plugin2.getRequirements());
                            plugin.setEngine(plugin2.getEngine());
                        } else {
                            plugin = new Plugin(strSubstring, strSubstring);
                            plugin.setAuthor(LocaleController.getString(R.string.PluginNoAuthor));
                            plugin.setVersion("1.0");
                            plugin.setEngine("python");
                        }
                        plugin.setError(th);
                        plugin.setEnabled(false);
                        pythonPluginsEngine.getPluginsController().getPlugins().put(strSubstring, plugin);
                    }
                } catch (Throwable th2) {
                    pluginValidationResultValidatePluginFromFile = null;
                }
            }
            pythonPluginsEngine.getPluginsController().notifyPluginsChanged();
            Collection<Plugin> collectionValues = pythonPluginsEngine.getPluginsController().getPlugins().values();
            Collection<Plugin> collection = collectionValues;
            if (!collection.isEmpty()) {
                Iterator<Plugin> it = collection.iterator();
                while (it.hasNext()) {
                    if (((Plugin) it.next()).isEnabled() && (i = i + 1) < 0) {
                        CollectionsKt.throwCountOverflow();
                    }
                }
            }
            FileLog.d("Python plugin system initialized. Total: " + pythonPluginsEngine.getPluginsController().getPlugins().size() + ", Enabled: " + i);
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
            }
        } catch (PyException e) {
            FileLog.e("Failed to setup Python environment for plugins", e);
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean loadPlugins$lambda$0$0(File file, String str) {
        String lowerCase = str.toLowerCase(Locale.ROOT);
        return lowerCase.endsWith(".py");
    }

    public final void loadPlugin(String pluginId, String filePath) throws Exception {
        loadPlugin(pluginId, filePath, null, null);
    }

    public final void loadPlugin(String pluginId, String filePath, Plugin metadata) throws Exception {
        loadPlugin(pluginId, filePath, metadata, null);
    }

    public final void loadPlugin(String pluginId, String filePath, Plugin metadata, PipController.InstallerDelegate delegate) throws Exception {
        boolean z = getPluginsController().getPreferences().getBoolean("plugin_enabled_" + pluginId, false);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new Exception("Plugin file not found: " + filePath);
        }
        if (metadata == null) {
            PluginsController.PluginValidationResult pluginValidationResultValidatePluginFromFile = validatePluginFromFile(filePath);
            if (pluginValidationResultValidatePluginFromFile.getError() != null) {
                throw new Exception(pluginValidationResultValidatePluginFromFile.getError());
            }
            metadata = pluginValidationResultValidatePluginFromFile.getPlugin();
        }
        if (metadata == null) {
            throw new IllegalArgumentException("Required value was null.");
        }
        if (!Intrinsics.areEqual(pluginId, metadata.getId())) {
            throw new Exception("Plugin ID mismatch. Expected: " + pluginId + ", but found: " + metadata.getId() + " in metadata.");
        }
        if (this.pluginInstances.containsKey(pluginId)) {
            unloadPlugin(pluginId);
        }
        metadata.setEnabled(false);
        metadata.setError(null);
        getPluginsController().getPlugins().put(pluginId, metadata);
        if (ExteraConfig.getPluginsSafeMode()) {
            return;
        }
        if (z) {
            createPluginInstance(pluginId, filePath, metadata, delegate);
            setPluginEnabled(pluginId, true, null);
        } else if (delegate != null) {
            installPluginDependencies(pluginId, metadata, delegate);
        }
    }

    private final void installPluginDependencies(String pluginId, Plugin pluginMetadata, PipController.InstallerDelegate delegate) throws Exception {
        PyObject module;
        List<String> requirements = pluginMetadata.getRequirements();
        List<String> list = requirements;
        if (list == null || list.isEmpty()) {
            return;
        }
        List<String> listInstallDependencies = PipController.INSTANCE.installDependencies(requirements, pluginId, delegate);
        List<String> list2 = listInstallDependencies;
        LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(list2, 10)), 16));
        for (Object obj : list2) {
            linkedHashMap.put(obj, INSTANCE.topLevelModuleNames(new File((String) obj)));
        }
        Set set = INSTANCE.topLevelModuleNames(SDK_DIR);
        for (Object entryObj : linkedHashMap.entrySet()) {
            Map.Entry entry = (Map.Entry) entryObj;
            String str = (String) entry.getKey();
            Set setIntersect = CollectionsKt.intersect((Set) entry.getValue(), set);
            if (!setIntersect.isEmpty()) {
                throw new Exception("Dependency " + new File(str).getName() + " of " + pluginId + " provides " + CollectionsKt.joinToString(setIntersect, ", ", "", "", -1, "...", null) + ", which would replace the plugins SDK module of the same name.");
            }
        }
        for (Object entry2Obj : linkedHashMap.entrySet()) {
            Map.Entry entry2 = (Map.Entry) entry2Obj;
            String str2 = (String) entry2.getKey();
            Set<String> set2 = (Set) entry2.getValue();
            String name = new File(str2).getName();
            disableShadowedPlugins(pluginId, name, set2);
        }
        Python python = getPython();
        PyObject pyObject = (python == null || (module = python.getModule("sys")) == null) ? null : (PyObject) module.get((Object) "path");
        if (pyObject == null) {
            return;
        }
        int size = listInstallDependencies.size();
        while (true) {
            size--;
            if (-1 >= size) {
                return;
            }
            String str3 = listInstallDependencies.get(size);
            removeFromSysPath(pyObject, str3);
            pyObject.callAttr("insert", 0, str3);
            this.dependencyPaths.add(str3);
        }
    }

    private final void disableShadowedPlugins(String pluginId, String dependencyName, Set<String> providedModules) {
        for (String str : providedModules) {
            if (!Intrinsics.areEqual(str, pluginId)) {
                if (new File(getPluginsController().getPluginsDir(), str + ".py").exists()) {
                    FileLog.w("Disabling plugin '" + str + "': dependency " + dependencyName + " of " + pluginId + " now owns that module name");
                    unloadPlugin(str);
                    SharedPreferences.Editor editorEdit = getPluginsController().getPreferences().edit();
                    StringBuilder sb = new StringBuilder();
                    sb.append("plugin_enabled_");
                    sb.append(str);
                    editorEdit.putBoolean(sb.toString(), false);
                    editorEdit.apply();
                    Plugin plugin = getPluginsController().getPlugins().get(str);
                    if (plugin != null) {
                        plugin.setError(new Exception("Plugin id '" + str + "' is now provided by " + dependencyName + ", a dependency of '" + pluginId + "'. Rename or delete one of them."));
                    }
                }
            }
        }
    }

    private final void removePluginDependencies(String pluginId) {
        PipController.INSTANCE.uninstallDependencies(pluginId);
        pruneDependencyPaths();
    }

    private final void pruneDependencyPaths() {
        PyObject module;
        if (this.dependencyPaths.isEmpty()) {
            return;
        }
        Set<String> setActiveLibraryPaths = PipController.INSTANCE.activeLibraryPaths();
        ConcurrentHashMap.KeySetView<String, Boolean> keySetView = this.dependencyPaths;
        ArrayList arrayList = new ArrayList();
        for (Object obj : keySetView) {
            if (!setActiveLibraryPaths.contains((String) obj)) {
                arrayList.add(obj);
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        try {
            Python python = this.python;
            PyObject module2 = python != null ? python.getModule("sys") : null;
            PyObject pyObject = module2 != null ? (PyObject) module2.get((Object) "path") : null;
            PyObject pyObject2 = module2 != null ? (PyObject) module2.get((Object) "modules") : null;
            PyObject pyObject3 = module2 != null ? (PyObject) module2.get((Object) "path_importer_cache") : null;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj2 = arrayList.get(i);
                i++;
                String str = (String) obj2;
                if (pyObject != null) {
                    removeFromSysPath(pyObject, str);
                }
                if (pyObject3 != null) {
                    pyObject3.callAttr("pop", str, null);
                }
                if (pyObject2 != null) {
                    removeModulesUnderPath(pyObject2, str);
                }
                this.dependencyPaths.remove(str);
            }
            Python python2 = this.python;
            if (python2 == null || (module = python2.getModule("importlib")) == null) {
                return;
            }
            module.callAttr("invalidate_caches", new Object[0]);
        } catch (PyException e) {
            FileLog.e("Failed to drop unused dependency paths from the Python environment", e);
        }
    }

    private final void removeModulesUnderPath(PyObject sysModules, String path) {
        String string;
        PyObject pyObjectCallAttr;
        Object objM2315constructorimpl;
        String string2;
        List<PyObject> listAsList;
        PyObject pyObject;
        String strCanonicalPathOrNull;
        Python python = this.python;
        if (python == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        String str = File.separator;
        sb.append(str);
        String string3 = sb.toString();
        String strCanonicalPathOrNull2 = INSTANCE.canonicalPathOrNull(path);
        String str2 = strCanonicalPathOrNull2 != null ? strCanonicalPathOrNull2 + str : string3;
        ArrayList arrayList = new ArrayList();
        for (PyObject pyObject2 : python.getBuiltins().callAttr("list", sysModules).asList()) {
            if (pyObject2 != null && (string = pyObject2.toString()) != null && (pyObjectCallAttr = sysModules.callAttr("get", string)) != null) {
                PyObject pyObject3 = (PyObject) pyObjectCallAttr.get((Object) "__file__");
                if (pyObject3 == null || (string2 = pyObject3.toString()) == null) {
                    try {
                        PyObject pyObject4 = (PyObject) pyObjectCallAttr.get((Object) "__path__");
                        string2 = (pyObject4 == null || (listAsList = pyObject4.asList()) == null || (pyObject = (PyObject) CollectionsKt.firstOrNull((List) listAsList)) == null) ? null : pyObject.toString();
                    } catch (Throwable th) {
                        string2 = null;
                    }
                }
                if (string2.startsWith(string3) || ((strCanonicalPathOrNull = INSTANCE.canonicalPathOrNull(string2)) != null && strCanonicalPathOrNull.startsWith(str2))) {
                    arrayList.add(string);
                }
            }
        }
        for (Object obj : arrayList) {
            sysModules.callAttr("pop", (String) obj, null);
        }
        if (arrayList.isEmpty()) {
            return;
        }
        FileLog.d("Unloaded " + arrayList.size() + " module(s) belonging to removed dependency " + new File(path).getName());
    }

    private final void removePluginPathsFromSysPath() {
        PyObject module;
        PyObject pyObject;
        Python python = this.python;
        if (python == null || (module = python.getModule("sys")) == null || (pyObject = (PyObject) module.get((Object) "path")) == null) {
            return;
        }
        ArrayList arrayList = new ArrayList(this.dependencyPaths);
        arrayList.add(getPluginsController().getPluginsDir().getAbsolutePath());
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            removeFromSysPath(pyObject, (String) it.next());
        }
        this.dependencyPaths.clear();
    }

    private final void removeFromSysPath(PyObject sysPath, String path) {
        while (sysPath.callAttr("__contains__", path).toBoolean()) {
            sysPath.callAttr("remove", path);
        }
    }

    private final void createPluginInstance(String pluginId, String filePath, Plugin pluginMetadata, PipController.InstallerDelegate delegate) throws Exception {
        PyObject module;
        PyObject pyObjectRequireBasePluginClass = requireBasePluginClass();
        installPluginDependencies(pluginId, pluginMetadata, delegate);
        refreshImportCaches(pluginId, new File(filePath).getParentFile());
        try {
            Python python = getPython();
            if (python == null || (module = python.getModule(pluginId)) == null) {
                throw new Exception("Failed to import plugin module: " + pluginId);
            }
            PyObject pyObject = (PyObject) module.get((Object) "__file__");
            String string = pyObject != null ? pyObject.toString() : null;
            Companion companion = INSTANCE;
            if (!Intrinsics.areEqual(companion.canonicalPathOrNull(string), companion.canonicalPathOrNull(filePath))) {
                StringBuilder sb = new StringBuilder();
                sb.append("Plugin id '");
                sb.append(pluginId);
                sb.append("' is already taken by the Python module ");
                if (string == null) {
                    string = "built into the runtime";
                }
                sb.append(string);
                sb.append(". Change '__id__' to a name that no other module uses.");
                throw new Exception(sb.toString());
            }
            PyObject pyObjectCallAttr = pyObjectRequireBasePluginClass.callAttr("_findPluginClass", module);
            if (pyObjectCallAttr == null) {
                throw new Exception("Could not find a class inheriting from BasePlugin in " + pluginId + ".py. Make sure your main plugin class extends BasePlugin.");
            }
            PyObject pyObjectCall = pyObjectCallAttr.call(new Object[0]);
            pyObjectCall.put("id", (Object) pluginMetadata.getId());
            pyObjectCall.put("name", (Object) pluginMetadata.getName());
            pyObjectCall.put("description", (Object) pluginMetadata.getDescription());
            pyObjectCall.put("author", (Object) pluginMetadata.getAuthor());
            pyObjectCall.put("version", (Object) pluginMetadata.getVersion());
            pyObjectCall.put("icon", (Object) pluginMetadata.getIcon());
            pyObjectCall.put("app_version", (Object) pluginMetadata.getAppVersion());
            pyObjectCall.put("sdk_version", (Object) pluginMetadata.getSdkVersion());
            pyObjectCall.put("requirements", (Object) pluginMetadata.getRequirements());
            String string2 = "enabled";
            Boolean bool = Boolean.FALSE;
            pyObjectCall.put(string2, (Object) bool);
            pyObjectCall.put("initialized", (Object) bool);
            pyObjectCall.put("error_message", (Object) null);
            this.pluginInstances.put(pluginId, pyObjectCall);
        } catch (PyException e) {
            throw new Exception("Failed to import plugin module: " + e.getMessage(), e);
        }
    }

    /* JADX WARN: Code duplicated, block: B:27:0x0096 A[Catch: all -> 0x0060, PyException -> 0x0063, TryCatch #6 {PyException -> 0x0063, blocks: (B:8:0x0031, B:10:0x003b, B:13:0x0054, B:14:0x0058, B:22:0x0082, B:24:0x0088, B:25:0x0093, B:27:0x0096, B:29:0x00a6, B:30:0x00b4, B:32:0x00d0, B:35:0x00d7, B:37:0x00dd, B:39:0x00e1, B:45:0x0115, B:46:0x011a, B:42:0x010f), top: B:98:0x0031, outer: #0 }] */
    /* JADX WARN: Code duplicated, block: B:29:0x00a6 A[Catch: all -> 0x0060, PyException -> 0x0063, TryCatch #6 {PyException -> 0x0063, blocks: (B:8:0x0031, B:10:0x003b, B:13:0x0054, B:14:0x0058, B:22:0x0082, B:24:0x0088, B:25:0x0093, B:27:0x0096, B:29:0x00a6, B:30:0x00b4, B:32:0x00d0, B:35:0x00d7, B:37:0x00dd, B:39:0x00e1, B:45:0x0115, B:46:0x011a, B:42:0x010f), top: B:98:0x0031, outer: #0 }] */
    /* JADX WARN: Code duplicated, block: B:43:0x0112 A[EDGE_INSN: B:43:0x0112->B:44:0x0113 BREAK  A[LOOP:0: B:38:0x00df->B:42:0x010f]] */
    public final void unloadPlugin(String pluginId) {
        this.settingsCache.remove(pluginId);
        PyObject pyObjectRemove = this.pluginInstances.remove(pluginId);
        Plugin plugin = getPluginsController().getPlugins().get(pluginId);
        if (plugin != null) {
            plugin.setEnabled(false);
        }
        try {
            if (pyObjectRemove != null) {
                try {
                    if (PyObjectUtils.getBoolean(pyObjectRemove, "initialized", false)) {
                        getPluginsController().getWatchdog().onPluginExecutionStarted(pluginId);
                        try {
                            pyObjectRemove.callAttr("on_plugin_unload", new Object[0]);
                        } catch (Throwable th) {
                            FileLog.e("Error during on_plugin_unload for " + pluginId, th);
                        } finally {
                            getPluginsController().getWatchdog().onPluginExecutionFinished(pluginId);
                        }
                    }
                    pyObjectRemove.put("initialized", (Object) Boolean.FALSE);
                    pyObjectRemove.put("enabled", (Object) Boolean.FALSE);
                    File file = new File(getPluginsController().getPluginsDir(), "__pycache__");
                    File file2 = null;
                    File[] fileArrListFiles;
                    if (file.exists() && file.isDirectory() && (fileArrListFiles = file.listFiles()) != null) {
                        for (File f : fileArrListFiles) {
                            if (f.getName().startsWith(pluginId + ".cpython-")) {
                                file2 = f;
                                break;
                            }
                        }
                    }
                    if (file2 != null) {
                        INSTANCE.deleteFileIfExists(file2);
                    }
                    refreshImportCaches(pluginId, getPluginsController().getPluginsDir());
                    getPluginsController().cleanupPlugin(pluginId);
                    try {
                        pyObjectRemove.close();
                    } catch (Throwable th) {
                        FileLog.e("Failed to close plugin instance " + pluginId, th);
                    }
                } catch (PyException e) {
                    FileLog.e("Failed to unload plugin " + pluginId, e);
                    getPluginsController().cleanupPlugin(pluginId);
                    try {
                        pyObjectRemove.close();
                    } catch (Throwable th) {
                        FileLog.e("Failed to close plugin instance " + pluginId, th);
                    }
                }
            } else {
                File file = new File(getPluginsController().getPluginsDir(), "__pycache__");
                File file2 = null;
                File[] fileArrListFiles;
                if (file.exists() && file.isDirectory() && (fileArrListFiles = file.listFiles()) != null) {
                    for (File f : fileArrListFiles) {
                        if (f.getName().startsWith(pluginId + ".cpython-")) {
                            file2 = f;
                            break;
                        }
                    }
                }
                if (file2 != null) {
                    INSTANCE.deleteFileIfExists(file2);
                }
                refreshImportCaches(pluginId, getPluginsController().getPluginsDir());
                getPluginsController().cleanupPlugin(pluginId);
            }
        } catch (Throwable th5) {
            getPluginsController().cleanupPlugin(pluginId);
            if (pyObjectRemove != null) {
                try {
                    pyObjectRemove.close();
                } catch (Throwable th6) {
                    FileLog.e("Failed to close plugin instance " + pluginId, th6);
                }
            }
            throw th5;
        }
    }

    private final void refreshImportCaches(String pluginId, File moduleDir) {
        Python python = this.python;
        if (python == null) {
            return;
        }
        try {
            PyObject module = python.getModule("sys");
            PyObject pyObject = (PyObject) module.get((Object) "modules");
            if (pyObject != null) {
                evictPluginModule(pyObject, pluginId, moduleDir);
            }
            PyObject pyObject2 = (PyObject) module.get((Object) "path_importer_cache");
            if (moduleDir != null && pyObject2 != null) {
                pyObject2.callAttr("pop", moduleDir.getAbsolutePath(), null);
            }
            python.getModule("importlib").callAttr("invalidate_caches", new Object[0]);
        } catch (PyException e) {
            FileLog.e("Failed to refresh import caches for " + pluginId, e);
        }
    }

    private final void evictPluginModule(PyObject sysModules, String pluginId, File moduleDir) {
        String strCanonicalPathOrNull;
        PyObject pyObjectCallAttr = sysModules.callAttr("get", pluginId);
        if (pyObjectCallAttr == null) {
            return;
        }
        PyObject pyObject = (PyObject) pyObjectCallAttr.get((Object) "__file__");
        String string = pyObject != null ? pyObject.toString() : null;
        if (moduleDir != null) {
            strCanonicalPathOrNull = INSTANCE.canonicalPathOrNull(new File(moduleDir, pluginId + ".py").getAbsolutePath());
        } else {
            strCanonicalPathOrNull = null;
        }
        if (strCanonicalPathOrNull == null || !Intrinsics.areEqual(INSTANCE.canonicalPathOrNull(string), strCanonicalPathOrNull)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Keeping module '");
            sb.append(pluginId);
            sb.append("' loaded: it belongs to ");
            if (string == null) {
                string = "the Python runtime";
            }
            sb.append(string);
            sb.append(", not to a plugin");
            FileLog.w(sb.toString());
            return;
        }
        sysModules.callAttr("pop", pluginId, null);
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void setPluginEnabled(String pluginId, boolean enabled, final Utilities.Callback<String> callback) {
        Plugin plugin;
        try {
            Plugin plugin2 = getPluginsController().getPlugins().get(pluginId);
            if (plugin2 == null) {
                throw new Exception("Plugin not found: " + pluginId);
            }
            if (enabled && ExteraConfig.getPluginsSafeMode()) {
                if (callback != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda13
                        @Override // java.lang.Runnable
                        public final void run() {
                            callback.run("Plugins cannot be enabled while safe mode is active");
                        }
                    });
                    return;
                }
                return;
            }
            PyObject pyObject = this.pluginInstances.get(pluginId);
            if (enabled && pyObject == null) {
                createPluginInstance(pluginId, getPluginPath(pluginId), plugin2, null);
                pyObject = this.pluginInstances.get(pluginId);
                if (pyObject == null) {
                    throw new Exception("Failed to create plugin instance: " + pluginId);
                }
            }
            if ((pyObject != null && PyObjectUtils.getBoolean(pyObject, "initialized", false)) == enabled && !plugin2.hasError()) {
                if (callback != null) {
                    callback.run(null);
                    return;
                }
                return;
            }
            if (!enabled) {
                SharedPreferences.Editor editorEdit = getPluginsController().getPreferences().edit();
                editorEdit.putBoolean("plugin_enabled_" + pluginId, false);
                editorEdit.apply();
                unloadPlugin(pluginId);
            } else {
                if (pyObject == null) {
                    throw new IllegalArgumentException("Required value was null.".toString());
                }
                getPluginsController().cleanupPlugin(pluginId);
                getPluginsController().getWatchdog().onPluginExecutionStarted(pluginId);
                try {
                    pyObject.callAttr("on_plugin_load", new Object[0]);
                    getPluginsController().getWatchdog().onPluginExecutionFinished(pluginId);
                    String string = "initialized";
                    Boolean bool = Boolean.TRUE;
                    pyObject.put(string, (Object) bool);
                    pyObject.put("error_message", (Object) null);
                    plugin2.setError(null);
                    pyObject.put("enabled", (Object) bool);
                    plugin2.setEnabled(true);
                    SharedPreferences.Editor editorEdit2 = getPluginsController().getPreferences().edit();
                    editorEdit2.putBoolean("plugin_enabled_" + pluginId, true);
                    editorEdit2.apply();
                    getPluginsController().loadPluginSettings(pluginId);
                } catch (Throwable th) {
                    getPluginsController().getWatchdog().onPluginExecutionFinished(pluginId);
                    throw th;
                }
            }
            getPluginsController().notifyPluginsChanged();
            if (callback != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda14
                    @Override // java.lang.Runnable
                    public final void run() {
                        callback.run(null);
                    }
                });
            }
        } catch (Throwable th2) {
            FileLog.e("Unexpected error setting enabled state for " + pluginId, th2);
            if (enabled && (plugin = getPluginsController().getPlugins().get(pluginId)) != null) {
                plugin.setError(th2);
            }
            SharedPreferences.Editor editorEdit3 = getPluginsController().getPreferences().edit();
            editorEdit3.putBoolean("plugin_enabled_" + pluginId, false);
            editorEdit3.apply();
            unloadPlugin(pluginId);
            if (callback != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda15
                    @Override // java.lang.Runnable
                    public final void run() {
                        callback.run(AppUtils.stackTraceToString(th2));
                    }
                });
            }
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void deletePlugin(String pluginId, final Utilities.Callback<String> callback) {
        unloadPlugin(pluginId);
        try {
            removePluginDependencies(pluginId);
        } catch (Exception e) {
            FileLog.e("Failed to uninstall dependencies for " + pluginId, e);
        }
        INSTANCE.deleteFileIfExists(new File(getPluginsController().getPluginsDir(), pluginId + ".py"));
        PluginsController.Companion companion = PluginsController.INSTANCE;
        if (companion.isPluginPinned(pluginId)) {
            companion.setPluginPinned(pluginId, false);
        }
        getPluginsController().clearPluginSettingsPreferences(pluginId, true);
        getPluginsController().getPlugins().remove(pluginId);
        getPluginsController().notifyPluginsChanged();
        if (callback != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    callback.run(null);
                }
            });
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public String getPluginPath(String id) {
        return getPluginsController().getPluginsDir().getAbsolutePath() + File.separator + id + ".py";
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void openInExternalApp(String id) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        File file = new File(getPluginPath(id));
        if (file.exists()) {
            PluginFileViewer.INSTANCE.open(safeLastFragment, file, id + ".plugin");
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void sharePlugin(String id) {
        Activity parentActivity;
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null || (parentActivity = safeLastFragment.getParentActivity()) == null) {
            return;
        }
        String pluginPath = getPluginPath(id);
        File file = new File(ApplicationLoader.getFilesDirFixed(), "temp");
        SimpliFiles.directory(file).create();
        File file2 = new File(file, id + ".plugin");
        try {
            SimpliFiles.file(pluginPath).copyTo(file2, OverwritePolicy.REPLACE);
            Uri uriForFile = FileProvider.getUriForFile(parentActivity, ApplicationLoader.getApplicationId() + ".provider", file2);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setFlags(1);
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.setType("application/x-plugin");
            safeLastFragment.startActivityForResult(Intent.createChooser(intent, LocaleController.getString(R.string.ShareFile)), 500);
            file2.deleteOnExit();
        } catch (IllegalArgumentException e2) {
            FileLog.e(e2);
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public final void loadPluginFromFile(String filePath, Plugin pluginMetadata, Utilities.Callback<String> callback) {
        loadPluginFromFile(filePath, pluginMetadata, callback, null);
    }

    public final void loadPluginFromFile(final String filePath, final Plugin pluginMetadata, final Utilities.Callback<String> callback, final PipController.InstallerDelegate delegate) {
        PluginsController.INSTANCE.runOnPluginsQueue(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                PythonPluginsEngine.$r8$lambda$Uz7P7RWb8TcyDXFz8YyO_rscMmI(pluginMetadata, PythonPluginsEngine.this, filePath, delegate, callback);
            }
        });
    }

    /* JADX WARN: Code duplicated, block: B:37:0x00fc  */
    /* JADX WARN: Code duplicated, block: B:39:0x0104 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:57:0x017e  */
    /* JADX WARN: Code duplicated, block: B:64:0x0197  */
    /* JADX WARN: Code duplicated, block: B:68:0x01ba  */
    /* JADX WARN: Code duplicated, block: B:88:? A[RETURN, SYNTHETIC] */
    public static void $r8$lambda$Uz7P7RWb8TcyDXFz8YyO_rscMmI(Plugin plugin, PythonPluginsEngine pythonPluginsEngine, String str, PipController.InstallerDelegate installerDelegate, final Utilities.Callback callback) {
        Throwable th;
        Exception e;
        File file;
        File file2;
        boolean zExists;
        PyObject pyObjectRemove;
        String str2 = null;
        file = null;
        File file3 = null;
        boolean z = false;
        if (plugin == null) {
            try {
                PluginsController.PluginValidationResult pluginValidationResultValidatePluginFromFile = pythonPluginsEngine.validatePluginFromFile(str);
                if (pluginValidationResultValidatePluginFromFile.getError() != null) {
                    throw new Exception(pluginValidationResultValidatePluginFromFile.getError());
                }
                plugin = pluginValidationResultValidatePluginFromFile.getPlugin();
                if (plugin == null) {
                    throw new IllegalArgumentException("Required value was null.".toString());
                }
            } catch (Throwable thCatched) {
                th = thCatched;
                file = null;
                file2 = null;
                zExists = false;
                FileLog.e("Unexpected error loading plugin from file: " + str, th);
                if (str2 != null) {
                    INSTANCE.deleteFileIfExists(file);
                    if (zExists) {
                        try {
                            SimpliFiles.file(file2).moveTo(file, OverwritePolicy.REPLACE);
                            try {
                                String absolutePath = file.getAbsolutePath();
                                pythonPluginsEngine.loadPlugin(str2, absolutePath);
                                Unit unit = Unit.INSTANCE;
                            } catch (Exception eCatched) {
                                e = eCatched;
                                try {
                                    FileLog.e("Failed to reload original plugin after update failure for " + str2, e);
                                    Plugin plugin2 = pythonPluginsEngine.getPluginsController().getPlugins().get(str2);
                                    if (plugin2 != null) {
                                        plugin2.setError(e);
                                        Unit unit2 = Unit.INSTANCE;
                                    }
                                } catch (Exception e2) {
                                    e = e2;
                                    z = true;
                                    FileLog.e("Failed to restore backup for plugin " + str2, e);
                                    if (!z) {
                                        pythonPluginsEngine.getPluginsController().cleanupPlugin(str2);
                                        try {
                                            pythonPluginsEngine.removePluginDependencies(str2);
                                        } catch (Exception e3) {
                                            FileLog.e(e3);
                                        }
                                        pyObjectRemove = pythonPluginsEngine.pluginInstances.remove(str2);
                                        if (pyObjectRemove != null) {
                                            pyObjectRemove.close();
                                        }
                                        pythonPluginsEngine.getPluginsController().clearPluginSettingsPreferences(str2, true);
                                        pythonPluginsEngine.getPluginsController().getPlugins().remove(str2);
                                        INSTANCE.deleteFileIfExists(file);
                                    }
                                    pythonPluginsEngine.getPluginsController().notifyPluginsChanged();
                                    if (callback != null) {
                                        final Throwable err = th;
                                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda21
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                PythonPluginsEngine.loadPluginFromFile$lambda$0$1(callback, err);
                                            }
                                        });
                                    }
                                }
                            }
                            z = true;
                        } catch (Exception e4) {
                            e = e4;
                        }
                    }
                    if (!z) {
                        pythonPluginsEngine.getPluginsController().cleanupPlugin(str2);
                        pythonPluginsEngine.removePluginDependencies(str2);
                        pyObjectRemove = pythonPluginsEngine.pluginInstances.remove(str2);
                        if (pyObjectRemove != null) {
                            pyObjectRemove.close();
                        }
                        pythonPluginsEngine.getPluginsController().clearPluginSettingsPreferences(str2, true);
                        pythonPluginsEngine.getPluginsController().getPlugins().remove(str2);
                        INSTANCE.deleteFileIfExists(file);
                    }
                }
                pythonPluginsEngine.getPluginsController().notifyPluginsChanged();
                if (callback != null) {
                    final Throwable err2 = th;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda21
                        @Override // java.lang.Runnable
                        public final void run() {
                            PythonPluginsEngine.loadPluginFromFile$lambda$0$1(callback, err2);
                        }
                    });
                }
            }
        }
        String id = plugin.getId();
        try {
            file = new File(pythonPluginsEngine.getPluginsController().getPluginsDir(), id + ".py");
            try {
                zExists = file.exists();
                if (zExists) {
                    try {
                        pythonPluginsEngine.unloadPlugin(id);
                        file2 = new File(pythonPluginsEngine.getPluginsController().getPluginsDir(), id + ".py.bak");
                        try {
                            INSTANCE.deleteFileIfExists(file2);
                            SimpliFiles.file(file).moveTo(file2, OverwritePolicy.ERROR);
                            pythonPluginsEngine.removePluginDependencies(id);
                            file3 = file2;
                        } catch (Throwable th2) {
                            th = th2;
                            str2 = id;
                            FileLog.e("Unexpected error loading plugin from file: " + str, th);
                            if (str2 != null) {
                                INSTANCE.deleteFileIfExists(file);
                                if (zExists && file2 != null && file2.exists() && file != null) {
                                    SimpliFiles.file(file2).moveTo(file, OverwritePolicy.REPLACE);
                                    String absolutePath2 = file.getAbsolutePath();
                                        pythonPluginsEngine.loadPlugin(str2, absolutePath2);
                                    Unit unit3 = Unit.INSTANCE;
                                    z = true;
                                }
                                if (!z) {
                                    pythonPluginsEngine.getPluginsController().cleanupPlugin(str2);
                                    pythonPluginsEngine.removePluginDependencies(str2);
                                    pyObjectRemove = pythonPluginsEngine.pluginInstances.remove(str2);
                                    if (pyObjectRemove != null) {
                                        pyObjectRemove.close();
                                    }
                                    pythonPluginsEngine.getPluginsController().clearPluginSettingsPreferences(str2, true);
                                    pythonPluginsEngine.getPluginsController().getPlugins().remove(str2);
                                    INSTANCE.deleteFileIfExists(file);
                                }
                            }
                            pythonPluginsEngine.getPluginsController().notifyPluginsChanged();
                            if (callback != null) {
                                final Throwable err3 = th;
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda21
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        PythonPluginsEngine.loadPluginFromFile$lambda$0$1(callback, err3);
                                    }
                                });
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        file2 = file3;
                    }
                }
                SimpliFiles.file(str).copyTo(file, OverwritePolicy.REPLACE);
                String absolutePath3 = file.getAbsolutePath();
                pythonPluginsEngine.loadPlugin(id, absolutePath3, plugin, installerDelegate);
                INSTANCE.deleteFileIfExists(file3);
                pythonPluginsEngine.getPluginsController().notifyPluginsChanged();
                if (callback != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda20
                        @Override // java.lang.Runnable
                        public final void run() {
                            callback.run(null);
                        }
                    });
                }
            } catch (Throwable th4) {
                th = th4;
                file2 = null;
                zExists = false;
                str2 = id;
                FileLog.e("Unexpected error loading plugin from file: " + str, th);
                if (str2 != null) {
                    INSTANCE.deleteFileIfExists(file);
                    if (zExists) {
                        SimpliFiles.file(file2).moveTo(file, OverwritePolicy.REPLACE);
                        String absolutePath4 = file.getAbsolutePath();
                                pythonPluginsEngine.loadPlugin(str2, absolutePath4);
                        Unit unit4 = Unit.INSTANCE;
                        z = true;
                    }
                    if (!z) {
                        pythonPluginsEngine.getPluginsController().cleanupPlugin(str2);
                        pythonPluginsEngine.removePluginDependencies(str2);
                        pyObjectRemove = pythonPluginsEngine.pluginInstances.remove(str2);
                        if (pyObjectRemove != null) {
                            pyObjectRemove.close();
                        }
                        pythonPluginsEngine.getPluginsController().clearPluginSettingsPreferences(str2, true);
                        pythonPluginsEngine.getPluginsController().getPlugins().remove(str2);
                        INSTANCE.deleteFileIfExists(file);
                    }
                }
                pythonPluginsEngine.getPluginsController().notifyPluginsChanged();
                if (callback != null) {
                    final Throwable err4 = th;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda21
                        @Override // java.lang.Runnable
                        public final void run() {
                            PythonPluginsEngine.loadPluginFromFile$lambda$0$1(callback, err4);
                        }
                    });
                }
            }
        } catch (Throwable th5) {
            th = th5;
            file = null;
            file2 = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void loadPluginFromFile$lambda$0$1(Utilities.Callback callback, Throwable th) {
        callback.run(AppUtils.stackTraceToString(th));
    }

    private final String findModuleNameOwner(String pluginId) {
        Object obj;
        if (INSTANCE.topLevelModuleNames(SDK_DIR).contains(pluginId)) {
            return "the plugins SDK";
        }
        Iterator<String> it = PipController.INSTANCE.activeLibraryPaths().iterator();
        while (it.hasNext()) {
            File file = new File(it.next());
            if (INSTANCE.topLevelModuleNames(file).contains(pluginId)) {
                return "the installed dependency " + file.getName();
            }
        }
        Python python = getPython();
        if (python == null) {
            return null;
        }
        try {
            PyObject pyObjectCallAttr = python.getModule("importlib.util").callAttr("find_spec", pluginId);
            if (pyObjectCallAttr == null) {
                return null;
            }
            PyObject pyObject = (PyObject) pyObjectCallAttr.get((Object) "origin");
            String string = pyObject != null ? pyObject.toString() : null;
            if (string != null) {
                Companion companion = INSTANCE;
                if (Intrinsics.areEqual(companion.canonicalPathOrNull(string), companion.canonicalPathOrNull(new File(getPluginsController().getPluginsDir(), pluginId + ".py").getAbsolutePath()))) {
                    return null;
                }
                return string;
            }
            PyObject pyObject2 = (PyObject) pyObjectCallAttr.get((Object) "submodule_search_locations");
            if (pyObject2 == null) {
                return "another Python module";
            }
            String strCanonicalPathOrNull = INSTANCE.canonicalPathOrNull(new File(getPluginsController().getPluginsDir(), pluginId).getAbsolutePath());
            List<PyObject> listAsList = python.getBuiltins().callAttr("list", pyObject2).asList();
            List<PyObject> list = listAsList;
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
            Iterator<PyObject> it2 = list.iterator();
            while (it2.hasNext()) {
                arrayList.add(((PyObject) it2.next()).toString());
            }
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                obj = arrayList.get(i);
                i++;
                if (!Intrinsics.areEqual(INSTANCE.canonicalPathOrNull((String) obj), strCanonicalPathOrNull)) {
                    return (String) obj;
                }
            }
            obj = null;
            return (String) obj;
        } catch (PyException e) {
            FileLog.w("Treating plugin id '" + pluginId + "' as taken: " + e.getMessage());
            return "another Python module";
        } catch (Throwable th) {
            FileLog.e("Failed to check whether plugin id '" + pluginId + "' is free", th);
            return null;
        }
    }

    public final PluginsController.PluginValidationResult validatePluginFromFile(String filePath) {
        String string;
        String string2;
        if (!new File(filePath).exists()) {
            return new PluginsController.PluginValidationResult(null, "Plugin file not found.");
        }
        try {
            Map<String, String> pluginMetadata = parsePluginMetadata(filePath);
            String str = pluginMetadata.get("id");
            String str2 = pluginMetadata.get("name");
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
                if (str != null) {
                    if (!new Regex("^[a-zA-Z][a-zA-Z0-9_-]{1,31}$").matches(str)) {
                        return new PluginsController.PluginValidationResult(null, "Plugin '__id__' must be 2-32 characters long, start with a letter, and contain only latin letters, numbers, dashes and underscores.");
                    }
                    String strFindModuleNameOwner = findModuleNameOwner(str);
                    if (strFindModuleNameOwner != null) {
                        return new PluginsController.PluginValidationResult(null, "Plugin '__id__' is already used as a Python module name by " + strFindModuleNameOwner + ". Choose a different '__id__'.");
                    }
                    String str3 = pluginMetadata.get("app_version");
                    if (str3 != null) {
                        Matcher matcher = VERSION_PATTERN.matcher(str3);
                        if (!matcher.matches()) {
                            return new PluginsController.PluginValidationResult(null, "Invalid appVersion: " + str3);
                        }
                        String strGroup = matcher.group(1);
                        if (strGroup == null) {
                            return new PluginsController.PluginValidationResult(null, "Invalid appVersion: " + str3);
                        }
                        String strGroup2 = matcher.group(2);
                        if (strGroup2 == null) {
                            return new PluginsController.PluginValidationResult(null, "Invalid appVersion: " + str3);
                        }
                        string2 = StringsKt.trim((CharSequence) strGroup2).toString();
                        if (!AppUtils.compareVersions(strGroup, BuildVars.BUILD_VERSION_STRING, string2)) {
                            return new PluginsController.PluginValidationResult(null, "Plugin requires app version " + str3 + ", but current is " + BuildVars.BUILD_VERSION_STRING);
                        }
                    }
                    String str4 = pluginMetadata.get("sdk_version");
                    if (str4 != null) {
                        Matcher matcher2 = VERSION_PATTERN.matcher(str4);
                        if (!matcher2.matches()) {
                            return new PluginsController.PluginValidationResult(null, "Invalid sdkVersion: " + str4);
                        }
                        String strGroup3 = matcher2.group(1);
                        if (strGroup3 == null) {
                            return new PluginsController.PluginValidationResult(null, "Invalid sdkVersion: " + str4);
                        }
                        String strGroup4 = matcher2.group(2);
                        if (strGroup4 == null) {
                            return new PluginsController.PluginValidationResult(null, "Invalid sdkVersion: " + str4);
                        }
                        string = StringsKt.trim((CharSequence) strGroup4).toString();
                        if (!AppUtils.compareVersions(strGroup3, SDK_VERSION, string)) {
                            return new PluginsController.PluginValidationResult(null, "Plugin requires sdk version " + str4 + ", but current is " + SDK_VERSION);
                        }
                    }
                    if (str2 != null) {
                        Plugin plugin = new Plugin(str, str2);
                        plugin.setEngine("python");
                        String string3 = pluginMetadata.get("author");
                        if (string3 == null) {
                            string3 = LocaleController.getString(R.string.PluginNoAuthor);
                        }
                        plugin.setAuthor(string3);
                        String string4 = pluginMetadata.get("description");
                        if (string4 == null) {
                            string4 = LocaleController.getString(R.string.PluginNoDescription);
                        }
                        plugin.setDescription(string4);
                        plugin.setIcon(pluginMetadata.get("icon"));
                        String string5 = pluginMetadata.get("version");
                        if (string5 == null) {
                            string5 = "1.0";
                        }
                        plugin.setVersion(string5);
                        plugin.setAppVersion(str3);
                        plugin.setSdkVersion(str4);
                        String str5 = pluginMetadata.get("requirements");
                        if (str5 != null && str5.length() != 0) {
                            List<String> listSplit = new Regex(",(?!\\s*[<>=!~(])").split(str5, 0);
                            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(listSplit, 10));
                            Iterator<String> it = listSplit.iterator();
                            while (it.hasNext()) {
                                arrayList.add(StringsKt.trim((CharSequence) it.next()).toString());
                            }
                            ArrayList arrayList2 = new ArrayList();
                            int size = arrayList.size();
                            int i = 0;
                            while (i < size) {
                                Object obj = arrayList.get(i);
                                i++;
                                if (((String) obj).length() > 0) {
                                    arrayList2.add(obj);
                                }
                            }
                            plugin.setRequirements(arrayList2);
                        }
                        plugin.setEnabled(getPluginsController().getPreferences().getBoolean("plugin_enabled_" + str, false));
                        return new PluginsController.PluginValidationResult(plugin, null);
                    }
                    throw new IllegalArgumentException("Required value was null.".toString());
                }
                throw new IllegalArgumentException("Required value was null.".toString());
            }
            return new PluginsController.PluginValidationResult(null, "Plugin metadata must contain non-empty '__id__' and '__name__'.");
        } catch (PyException e) {
            FileLog.e("Failed to parse metadata from " + filePath + ". Error: " + e.getMessage(), e);
            return new PluginsController.PluginValidationResult(null, e.getMessage());
        } catch (Throwable th) {
            FileLog.e("Unexpected error validating plugin " + filePath, th);
            return new PluginsController.PluginValidationResult(null, th.getMessage());
        }
    }

    public final List<SettingItem> parsePySettingDefinitions(List<? extends PyObject> pyDefinitionsList) throws Exception {
        Object editTextSetting;
        String string;
        View view;
        Object customSetting;
        String string2;
        ArrayList arrayList = new ArrayList(pyDefinitionsList.size());
        for (PyObject pyObject : pyDefinitionsList) {
            Object headerSetting = null;
            String string3 = PyObjectUtils.getString(pyObject, "type", null);
            if (string3 == null) {
                FileLog.w("A setting item in a plugin is missing its 'type'. Skipping.");
            } else {
                String string4 = PyObjectUtils.getString(pyObject, "key", null);
                String string5 = PyObjectUtils.getString(pyObject, "text", null);
                String string6 = PyObjectUtils.getString(pyObject, "subtext", null);
                String string7 = PyObjectUtils.getString(pyObject, "icon", null);
                PyObject pyObject2 = (PyObject) pyObject.get((Object) "on_change");
                PyObject pyObject3 = (PyObject) pyObject.get((Object) "on_long_click");
                String string8 = PyObjectUtils.getString(pyObject, "link_alias", null);
                PyObject pyObject4 = (PyObject) pyObject.get((Object) "default");
                PyObject pyObject5 = (PyObject) pyObject.get((Object) "on_click");
                PyObject pyObject6 = (PyObject) pyObject.get((Object) "create_sub_fragment");
                switch (string3.hashCode()) {
                    case -1866021310:
                        if (string3.equals("edit_text")) {
                            String string9 = PyObjectUtils.getString(pyObject, "hint", null);
                            boolean z = PyObjectUtils.getBoolean(pyObject, "multiline", false);
                            int i = PyObjectUtils.getInt(pyObject, "max_length", 256);
                            String string10 = PyObjectUtils.getString(pyObject, "mask", null);
                            if (string4 != null && string9 != null) {
                                if (pyObject4 == null || (string = pyObject4.toString()) == null) {
                                    string = "";
                                }
                                editTextSetting = new EditTextSetting(string4, string9, string, z, i, string10, pyObject2);
                                headerSetting = editTextSetting;
                                break;
                            }
                        }
                        break;
                    case -1349088399:
                        if (string3.equals("custom")) {
                            PyObject pyObject7 = (PyObject) pyObject.get((Object) "view");
                            PyObject pyObject8 = (PyObject) pyObject.get((Object) "item");
                            PyObject pyObject9 = (PyObject) pyObject.get((Object) "factory");
                            PyObject pyObject10 = (PyObject) pyObject.get((Object) "factory_args");
                            if (pyObject9 != null) {
                                CustomSetting.Factory factory = (CustomSetting.Factory) PyObjectUtils.toJavaCompat(pyObject9, CustomSetting.Factory.class);
                                if (factory != null) {
                                    customSetting = pyObject10 == null ? new CustomSetting((CustomSetting.Factory<?>) factory, pyObject5, pyObject6, pyObject3, string8) : new CustomSetting(factory, pyObject10, pyObject5, pyObject6, pyObject3, string8);
                                    headerSetting = customSetting;
                                }
                                break;
                            } else if (pyObject8 != null) {
                                UItem uItem = (UItem) PyObjectUtils.toJavaCompat(pyObject8, UItem.class);
                                if (uItem != null) {
                                    customSetting = new CustomSetting(uItem, pyObject5, pyObject6, pyObject3, string8);
                                    headerSetting = customSetting;
                                }
                                break;
                            } else if (pyObject7 != null && (view = (View) PyObjectUtils.toJavaCompat(pyObject7, View.class)) != null) {
                                customSetting = new CustomSetting(view, pyObject5, pyObject6, pyObject3, string8);
                                headerSetting = customSetting;
                                break;
                            }
                        }
                        break;
                    case -1221270899:
                        if (string3.equals("header") && string5 != null) {
                            headerSetting = new HeaderSetting(string5);
                        }
                        break;
                    case -889473228:
                        if (string3.equals("switch") && string4 != null && string5 != null && pyObject4 != null) {
                            editTextSetting = new SwitchSetting(string4, string5, pyObject4.toBoolean(), string6, string7, pyObject2, pyObject3, string8);
                            headerSetting = editTextSetting;
                        }
                        break;
                    case 3556653:
                        if (string3.equals("text")) {
                            boolean z2 = PyObjectUtils.getBoolean(pyObject, "accent", false);
                            boolean z3 = PyObjectUtils.getBoolean(pyObject, "red", false);
                            if (string5 != null) {
                                headerSetting = new TextSetting(string5, string6, string7, z2, z3, pyObject5, pyObject6, pyObject3, string8);
                            }
                        }
                        break;
                    case 100358090:
                        if (string3.equals("input") && string4 != null && string5 != null) {
                            if (pyObject4 == null || (string2 = pyObject4.toString()) == null) {
                                string2 = "";
                            }
                            editTextSetting = new InputSetting(string4, string5, string2, string6, string7, pyObject2, pyObject3, string8);
                            headerSetting = editTextSetting;
                        }
                        break;
                    case 1191572447:
                        if (string3.equals("selector")) {
                            String[] stringArray = PyObjectUtils.getStringArray(pyObject, "items", null);
                            if (string4 != null && string5 != null && stringArray != null && stringArray.length != 0 && pyObject4 != null) {
                                editTextSetting = new SelectorSetting(string4, string5, pyObject4.toInt(), stringArray, string7, pyObject2, pyObject3, string8);
                                headerSetting = editTextSetting;
                                break;
                            }
                        }
                        break;
                    case 1674318617:
                        if (string3.equals("divider")) {
                            headerSetting = new DividerSetting(string5);
                        }
                        break;
                }
                if (headerSetting != null) {
                    arrayList.add(headerSetting);
                }
            }
        }
        return arrayList;
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public List<SettingItem> loadPluginSettings(String id) {
        try {
            Plugin plugin = getPluginsController().getPlugins().get(id);
            PyObject pyObject = this.pluginInstances.get(id);
            if (getPluginsController().isPluginActive$TMessagesProj(plugin) && pyObject != null) {
                PyObject pyObjectCallAttr = pyObject.callAttr("create_settings", new Object[0]);
                if (pyObjectCallAttr == null) {
                    return null;
                }
                List<PyObject> listAsList = pyObjectCallAttr.asList();
                if (listAsList.isEmpty()) {
                    return null;
                }
                return parsePySettingDefinitions(listAsList);
            }
            getPluginsController().invalidatePluginSettings(id);
            return null;
        } catch (Exception e) {
            FileLog.e("Failed to load plugin settings", e);
            return null;
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void executeOnAppEvent(String eventType) {
        Python python;
        PyObject module;
        PyObject pyObject;
        PluginsController pluginsController;
        if (!sdkInitialized || ExteraConfig.getPluginsSafeMode() || (python = getPython()) == null || (module = python.getModule("base_plugin")) == null || (pyObject = (PyObject) module.get((Object) "AppEvent")) == null) {
            return;
        }
        PyObject pyObjectCall = pyObject.call(eventType);
        try {
            PyObject pyObject2 = this.debuggerListener;
            if (pyObject2 != null) {
                try {
                    pyObject2.callAttr("on_app_event", pyObjectCall);
                } catch (PyException e) {
                    FileLog.e("Failed to execute app event for debugger listener", e);
                }
            }
            for (Map.Entry<String, PyObject> entry : this.pluginInstances.entrySet()) {
                Map.Entry<String, PyObject> entry2 = entry;
                String key = entry2.getKey();
                String str = key;
                PyObject value = entry2.getValue();
                PyObject pyObject3 = value;
                if (getPluginsController().isPluginActive$TMessagesProj(str) && PyObjectUtils.getBoolean(pyObject3, "enabled", false) && PyObjectUtils.getString(pyObject3, "error_message", null) == null) {
                    getPluginsController().getWatchdog().onPluginExecutionStarted(str);
                    try {
                        try {
                            pyObject3.callAttr("on_app_event", pyObjectCall);
                            pluginsController = getPluginsController();
                        } catch (Throwable th) {
                            getPluginsController().getWatchdog().onPluginExecutionFinished(str);
                            throw th;
                        }
                    } catch (PyException e2) {
                        FileLog.e("Failed to execute app " + eventType + " for " + str, e2);
                        pluginsController = getPluginsController();
                    }
                    pluginsController.getWatchdog().onPluginExecutionFinished(str);
                }
            }
            Unit unit = Unit.INSTANCE;
            AutoCloseableKt.closeFinally(pyObjectCall, null);
        } catch (Throwable th2) {
            try {
                throw th2;
            } catch (Throwable th3) {
                AutoCloseableKt.closeFinally(pyObjectCall, th2);
                throw th3;
            }
        }
    }

    private final <T> PluginsController.HookResult<T> executeHook(PyObject pluginInstance, T initialValue, Class<T> valueClass, String pyResultKey, PyMethodCaller<T> caller, Utilities.Callback<PyException> errorLogger) {
        if (pluginInstance != null) {
            try {
                PyObject pyObjectCall = caller.call(pluginInstance, initialValue);
                if (pyObjectCall != null) {
                    try {
                        String string = PyObjectUtils.getString(pyObjectCall, "strategy", "DEFAULT");
                        if (string != null && string.endsWith("CANCEL")) {
                            return new PluginsController.HookResult<>(null, true, false);
                        }
                        if (string != null && (string.endsWith("MODIFY") || string.endsWith("MODIFY_FINAL"))) {
                            PyObject pyObject = (PyObject) pyObjectCall.get((Object) pyResultKey);
                            if (pyObject != null) {
                                try {
                                    initialValue = (T) pyObject.toJava(valueClass);
                                } finally {
                                    AutoCloseableKt.closeFinally(pyObject, null);
                                }
                            }
                            if (string.endsWith("MODIFY_FINAL")) {
                                return new PluginsController.HookResult<>(initialValue, false, true);
                            }
                        }
                    } finally {
                        AutoCloseableKt.closeFinally(pyObjectCall, null);
                    }
                }
            } catch (PyException e) {
                errorLogger.run(e);
            }
        }
        return new PluginsController.HookResult<>(initialValue, false, false);
    }

    private final <T> PluginsController.HookResult<T> executeHook(String pluginId, T initialValue, Class<T> valueClass, String pyResultKey, PyMethodCaller<T> caller, Utilities.Callback<PyException> errorLogger) {
        return executeHook(this.pluginInstances.get(pluginId), initialValue, valueClass, pyResultKey, caller, errorLogger);
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public PluginsController.HookResult<TLObject> executePreRequestHook(final String requestName, final int account, TLObject request, final String pluginId) {
        return executeHook(pluginId, request, (Class<TLObject>) TLObject.class, "request", (PyMethodCaller<TLObject>) new PyMethodCaller() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda23
            @Override // com.exteragram.messenger.plugins.PythonPluginsEngine.PyMethodCaller
            public final PyObject call(PyObject pyObject, Object obj) {
                return PythonPluginsEngine.$r8$lambda$3HuOhQN3SQ64XZXjHG3mX3zdIqY(requestName, account, pyObject, (TLObject) obj);
            }
        }, new Utilities.Callback() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda24
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                FileLog.e("Failed to execute pre_request_hook in " + pluginId + " for " + requestName, (PyException) obj);
            }
        });
    }

    public static PyObject $r8$lambda$3HuOhQN3SQ64XZXjHG3mX3zdIqY(String str, int i, PyObject pyObject, TLObject tLObject) {
        return pyObject.callAttr("pre_request_hook", str, Integer.valueOf(i), tLObject);
    }

    /* JADX WARN: Code duplicated, block: B:39:0x00b5 A[Catch: all -> 0x005b, TRY_LEAVE, TryCatch #4 {all -> 0x005b, blocks: (B:8:0x0027, B:10:0x003b, B:12:0x004b, B:17:0x005e, B:20:0x0077, B:27:0x0082, B:30:0x009b, B:37:0x00a6, B:39:0x00b5, B:35:0x00a2, B:36:0x00a5, B:25:0x007e, B:26:0x0081, B:44:0x00cc, B:33:0x00a0, B:19:0x006f, B:23:0x007c, B:29:0x0093), top: B:61:0x0027, outer: #2, inners: #0, #3, #5, #6 }] */
    /* JADX WARN: Code duplicated, block: B:59:0x006f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:64:0x0093 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r8v0, types: [java.lang.Object, org.telegram.tgnet.TLObject] */
    /* JADX WARN: Type inference failed for: r8v1 */
    /* JADX WARN: Type inference failed for: r8v10 */
    /* JADX WARN: Type inference failed for: r8v11 */
    /* JADX WARN: Type inference failed for: r8v12 */
    /* JADX WARN: Type inference failed for: r8v13 */
    /* JADX WARN: Type inference failed for: r8v14 */
    /* JADX WARN: Type inference failed for: r8v15 */
    /* JADX WARN: Type inference failed for: r8v16 */
    /* JADX WARN: Type inference failed for: r8v17 */
    /* JADX WARN: Type inference failed for: r8v3 */
    /* JADX WARN: Type inference failed for: r8v4 */
    /* JADX WARN: Type inference failed for: r8v5 */
    /* JADX WARN: Type inference failed for: r8v6 */
    /* JADX WARN: Type inference failed for: r8v7, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r8v8 */
    /* JADX WARN: Type inference failed for: r8v9 */
    /* JADX WARN: Type inference failed for: r9v0, types: [java.lang.Object, org.telegram.tgnet.TLRPC$TL_error] */
    /* JADX WARN: Type inference failed for: r9v1 */
    /* JADX WARN: Type inference failed for: r9v10 */
    /* JADX WARN: Type inference failed for: r9v11 */
    /* JADX WARN: Type inference failed for: r9v12 */
    /* JADX WARN: Type inference failed for: r9v13 */
    /* JADX WARN: Type inference failed for: r9v14 */
    /* JADX WARN: Type inference failed for: r9v15 */
    /* JADX WARN: Type inference failed for: r9v16 */
    /* JADX WARN: Type inference failed for: r9v17 */
    /* JADX WARN: Type inference failed for: r9v3 */
    /* JADX WARN: Type inference failed for: r9v4 */
    /* JADX WARN: Type inference failed for: r9v5 */
    /* JADX WARN: Type inference failed for: r9v6 */
    /* JADX WARN: Type inference failed for: r9v7, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r9v8 */
    /* JADX WARN: Type inference failed for: r9v9 */
    public final PluginsController.HookResult<PluginsHooks.PostRequestResult> executePostRequestHook(String requestName, int account, TLObject response, TLRPC.TL_error error, PyObject pluginInstance) {
        PyObject pyObject;
        PyObject pyObject2;
        Object r9 = null;
        TLObject r8 = response;
        TLRPC.TL_error r10 = error;
        if (pluginInstance != null) {
            try {
                PyObject pyObjectCallAttr = pluginInstance.callAttr("post_request_hook", requestName, Integer.valueOf(account), response, error);
                r8 = response;
                r10 = error;
                if (pyObjectCallAttr != null) {
                    try {
                        String string = PyObjectUtils.getString(pyObjectCallAttr, "strategy", "");
                        TLObject r11 = response;
                        TLRPC.TL_error r12 = error;
                        if (string != null) {
                            if (!string.endsWith("MODIFY")) {
                                if (string.endsWith("MODIFY_FINAL")) {
                                    r11 = response;
                                    r12 = error;
                                    pyObject = (PyObject) pyObjectCallAttr.get((Object) "response");
                                    response = response;
                                    if (pyObject != null) {
                                        response = pyObject.toJava(TLObject.class);
                                        Unit unit = Unit.INSTANCE;
                                        AutoCloseableKt.closeFinally(pyObject, null);
                                        response = response;
                                    }
                                    pyObject2 = (PyObject) pyObjectCallAttr.get((Object) "error");
                                    r9 = error;
                                    if (pyObject2 != null) {
                                        error = pyObject2.toJava(TLRPC.TL_error.class);
                                        Unit unit2 = Unit.INSTANCE;
                                        AutoCloseableKt.closeFinally(pyObject2, null);
                                        r9 = error;
                                    }
                                    r11 = response;
                                    r12 = (TLRPC.TL_error) r9;
                                    if (string.endsWith("MODIFY_FINAL")) {
                                        PluginsController.HookResult<PluginsHooks.PostRequestResult> hookResult = new PluginsController.HookResult<>(new PluginsHooks.PostRequestResult((TLObject) response, (TLRPC.TL_error) r9), false, true);
                                        AutoCloseableKt.closeFinally(pyObjectCallAttr, null);
                                        return hookResult;
                                    }
                                }
                            } else {
                                pyObject = (PyObject) pyObjectCallAttr.get((Object) "response");
                                response = response;
                                if (pyObject != null) {
                                    try {
                                        response = pyObject.toJava(TLObject.class);
                                        Unit unit3 = Unit.INSTANCE;
                                        AutoCloseableKt.closeFinally(pyObject, null);
                                        response = response;
                                    } catch (Throwable th) {
                                        try {
                                            throw th;
                                        } catch (Throwable th2) {
                                            AutoCloseableKt.closeFinally(pyObject, th);
                                            throw th2;
                                        }
                                    }
                                }
                                pyObject2 = (PyObject) pyObjectCallAttr.get((Object) "error");
                                r9 = error;
                                if (pyObject2 != null) {
                                    try {
                                        error = pyObject2.toJava(TLRPC.TL_error.class);
                                        Unit unit4 = Unit.INSTANCE;
                                        AutoCloseableKt.closeFinally(pyObject2, null);
                                        r9 = error;
                                    } catch (Throwable th3) {
                                        try {
                                            throw th3;
                                        } catch (Throwable th4) {
                                            AutoCloseableKt.closeFinally(pyObject2, th3);
                                            throw th4;
                                        }
                                    }
                                }
                                r11 = response;
                                r12 = (TLRPC.TL_error) r9;
                                if (string.endsWith("MODIFY_FINAL")) {
                                    PluginsController.HookResult<PluginsHooks.PostRequestResult> hookResult2 = new PluginsController.HookResult<>(new PluginsHooks.PostRequestResult((TLObject) response, (TLRPC.TL_error) r9), false, true);
                                    AutoCloseableKt.closeFinally(pyObjectCallAttr, null);
                                    return hookResult2;
                                }
                            }
                        }
                        r11 = response;
                        r12 = error;
                        Unit unit5 = Unit.INSTANCE;
                        AutoCloseableKt.closeFinally(pyObjectCallAttr, null);
                        r8 = r11;
                        r10 = r12;
                    } catch (Throwable th5) {
                        try {
                            throw th5;
                        } catch (Throwable th6) {
                            AutoCloseableKt.closeFinally(pyObjectCallAttr, th5);
                            throw th6;
                        }
                    }
                }
            } catch (PyException e) {
                FileLog.e("Failed to execute post_request_hook for " + requestName, e);
                r8 = response;
                r10 = error;
            }
        }
        return new PluginsController.HookResult<>(new PluginsHooks.PostRequestResult((TLObject) r8, (TLRPC.TL_error) r10), false, false);
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public PluginsController.HookResult<PluginsHooks.PostRequestResult> executePostRequestHook(String requestName, int account, TLObject response, TLRPC.TL_error error, String pluginId) {
        return executePostRequestHook(requestName, account, response, error, this.pluginInstances.get(pluginId));
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public PluginsController.HookResult<TLRPC.Update> executeUpdateHook(final String updateName, final int account, TLRPC.Update update, String pluginId) {
        return executeHook(pluginId, update, (Class<TLRPC.Update>) TLRPC.Update.class, "update", (PyMethodCaller<TLRPC.Update>) new PyMethodCaller() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda3
            @Override // com.exteragram.messenger.plugins.PythonPluginsEngine.PyMethodCaller
            public final PyObject call(PyObject pyObject, Object obj) {
                return PythonPluginsEngine.$r8$lambda$gogZ8_uy9hbJzmvkveYPLtlEcFs(updateName, account, pyObject, (TLRPC.Update) obj);
            }
        }, new Utilities.Callback() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda4
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                FileLog.e("Failed to execute on_update_hook for " + updateName, (PyException) obj);
            }
        });
    }

    public static PyObject $r8$lambda$gogZ8_uy9hbJzmvkveYPLtlEcFs(String str, int i, PyObject pyObject, TLRPC.Update update) {
        return pyObject.callAttr("on_update_hook", str, Integer.valueOf(i), update);
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public PluginsController.HookResult<TLRPC.Updates> executeUpdatesHook(final String containerName, final int account, TLRPC.Updates updates, String pluginId) {
        return executeHook(pluginId, updates, (Class<TLRPC.Updates>) TLRPC.Updates.class, "updates", (PyMethodCaller<TLRPC.Updates>) new PyMethodCaller() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda9
            @Override // com.exteragram.messenger.plugins.PythonPluginsEngine.PyMethodCaller
            public final PyObject call(PyObject pyObject, Object obj) {
                return PythonPluginsEngine.$r8$lambda$66nPF4OSjZKgz4yasnWGOzZ1Egk(containerName, account, pyObject, (TLRPC.Updates) obj);
            }
        }, new Utilities.Callback() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda10
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                FileLog.e("Failed to execute on_updates_hook for " + containerName, (PyException) obj);
            }
        });
    }

    public static PyObject $r8$lambda$66nPF4OSjZKgz4yasnWGOzZ1Egk(String str, int i, PyObject pyObject, TLRPC.Updates updates) {
        return pyObject.callAttr("on_updates_hook", str, Integer.valueOf(i), updates);
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public PluginsController.HookResult<SendMessagesHelper.SendMessageParams> executeSendMessageHook(final int account, SendMessagesHelper.SendMessageParams params, final String pluginId) {
        return executeHook(pluginId, params, (Class<SendMessagesHelper.SendMessageParams>) SendMessagesHelper.SendMessageParams.class, "params", (PyMethodCaller<SendMessagesHelper.SendMessageParams>) new PyMethodCaller() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda18
            @Override // com.exteragram.messenger.plugins.PythonPluginsEngine.PyMethodCaller
            public final PyObject call(PyObject pyObject, Object obj) {
                return PythonPluginsEngine.$r8$lambda$_D6PxQm0hg98qBk_zHO3yV4BGHE(account, pyObject, (SendMessagesHelper.SendMessageParams) obj);
            }
        }, new Utilities.Callback() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda19
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                FileLog.e("Failed to execute on_send_message_hook for " + pluginId, (PyException) obj);
            }
        });
    }

    public static PyObject $r8$lambda$_D6PxQm0hg98qBk_zHO3yV4BGHE(int i, PyObject pyObject, SendMessagesHelper.SendMessageParams sendMessageParams) {
        return pyObject.callAttr("on_send_message_hook", Integer.valueOf(i), sendMessageParams);
    }

    public final String fetchParameterValue(String filePath, String parameterName) {
        if (filePath == null) {
            return null;
        }
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                return parsePluginMetadata(filePath).get(parameterName);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public final Map<String, String> parsePluginMetadata(String filePath) {
        HashMap map = new HashMap();
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                Python python = getPython();
                if (python != null) {
                    if (!sdkInitialized) {
                        initSdk();
                    }
                    try {
                        PyObject module = python.getModule("extera_utils.metadata_parser");
                        PyObject pyObjectCallAttr = module.callAttr("get_metadata", filePath);
                        if (pyObjectCallAttr != null) {
                            Map<PyObject, PyObject> mapAsMap = pyObjectCallAttr.asMap();
                            for (Map.Entry<PyObject, PyObject> entry : mapAsMap.entrySet()) {
                                map.put(entry.getKey().toString(), entry.getValue().toString());
                            }
                            return map;
                        }
                    } catch (Throwable e) {
                        FileLog.e("metadata_parser module failed for " + filePath + ", falling back to regex: " + e.getMessage());
                    }
                }
                // Fast regex fallback if Python/SDK is not ready
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    Pattern pattern = Pattern.compile("^\\s*__([a-zA-Z0-9_]+)__\\s*=\\s*[\"'](.*)[\"']\\s*$");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.matches()) {
                            map.put(matcher.group(1), matcher.group(2));
                        }
                    }
                } catch (Exception e) {
                    FileLog.e("Regex metadata parser failed for " + filePath, e);
                }
            }
        }
        return map;
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public Object getPluginSetting(String pluginId, String key, Object defaultValue) {
        Object java;
        ConcurrentHashMap<String, Object> concurrentHashMap = this.settingsCache.get(pluginId);
        if (concurrentHashMap != null && concurrentHashMap.containsKey(key)) {
            return concurrentHashMap.get(key);
        }
        Python python = getPython();
        if (python != null) {
            try {
                PyObject module = python.getModule("plugin_settings");
                PyObject pyObjectCallAttr = module.callAttr("get_setting", pluginId, key, defaultValue);
                if (pyObjectCallAttr != null) {
                    if (defaultValue instanceof Boolean) {
                        java = Boolean.valueOf(pyObjectCallAttr.toBoolean());
                    } else if (defaultValue instanceof Integer) {
                        java = Integer.valueOf(pyObjectCallAttr.toInt());
                    } else if (defaultValue instanceof String) {
                        java = pyObjectCallAttr.toString();
                    } else if (defaultValue instanceof Float) {
                        java = Float.valueOf(pyObjectCallAttr.toFloat());
                    } else if (defaultValue instanceof Long) {
                        java = Long.valueOf(pyObjectCallAttr.toLong());
                    } else if (defaultValue == null) {
                        java = pyObjectCallAttr.toJava(Object.class);
                    } else {
                        java = pyObjectCallAttr.toJava(defaultValue.getClass());
                    }
                    ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> concurrentHashMap2 = this.settingsCache;
                    final Function1 function1 = new Function1() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda25
                        @Override // kotlin.jvm.functions.Function1
                        public final Object invoke(Object obj) {
                            return PythonPluginsEngine.$r8$lambda$QERKwSD6yL4J8XJCKdmRa2I5DR8((String) obj);
                        }
                    };
                    ConcurrentHashMap<String, Object> concurrentHashMapComputeIfAbsent = concurrentHashMap2.computeIfAbsent(pluginId, new Function() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda26
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            return PythonPluginsEngine.$r8$lambda$T5aUeMCGEOtaNiCJriFIVhbHSkA(function1, obj);
                        }
                    });
                    concurrentHashMapComputeIfAbsent.put(key, java);
                    return java;
                }
            } catch (PyException e) {
                FileLog.e("Failed to get plugin setting " + pluginId + '/' + key, e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static ConcurrentHashMap $r8$lambda$QERKwSD6yL4J8XJCKdmRa2I5DR8(String str) {
        return new ConcurrentHashMap();
    }

    public static ConcurrentHashMap $r8$lambda$T5aUeMCGEOtaNiCJriFIVhbHSkA(Function1 function1, Object obj) {
        return (ConcurrentHashMap) function1.invoke(obj);
    }

    public static ConcurrentHashMap $r8$lambda$H3KBjR7kR4EblHmumAscDlVGoa4(Function1 function1, Object obj) {
        return (ConcurrentHashMap) function1.invoke(obj);
    }

    public static ConcurrentHashMap $r8$lambda$M02QGUTSwEr0Np6WtwhMk889TKc(String str) {
        return new ConcurrentHashMap();
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void setPluginSetting(String pluginId, String key, Object value) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> concurrentHashMap = this.settingsCache;
        final Function1 function1 = new Function1() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return PythonPluginsEngine.$r8$lambda$M02QGUTSwEr0Np6WtwhMk889TKc((String) obj);
            }
        };
        ConcurrentHashMap<String, Object> concurrentHashMapComputeIfAbsent = concurrentHashMap.computeIfAbsent(pluginId, new Function() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return PythonPluginsEngine.$r8$lambda$H3KBjR7kR4EblHmumAscDlVGoa4(function1, obj);
            }
        });
        concurrentHashMapComputeIfAbsent.put(key, value);
        Python python = getPython();
        if (python == null) {
            return;
        }
        try {
            PyObject module = python.getModule("plugin_settings");
            module.callAttr("set_setting", pluginId, key, value);
        } catch (PyException e) {
            FileLog.e("Failed to set plugin setting " + pluginId + '/' + key, e);
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void clearPluginSettings(String pluginId) {
        this.settingsCache.remove(pluginId);
        Python python = getPython();
        if (python == null) {
            return;
        }
        try {
            PyObject module = python.getModule("plugin_settings");
            module.callAttr("clear_settings", pluginId);
        } catch (PyException e) {
            FileLog.e("Failed to clear plugin settings for " + pluginId, e);
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public Map<String, ?> getAllPluginSettings(String pluginId) {
        Python python = getPython();
        if (python == null) {
            return null;
        }
        try {
            PyObject module = python.getModule("plugin_settings");
            PyObject pyObjectCallAttr = module.callAttr("get_all_settings", pluginId);
            if (pyObjectCallAttr != null) {
                HashMap map = new HashMap();
                Map<PyObject, PyObject> mapAsMap = pyObjectCallAttr.asMap();
                for (Map.Entry<PyObject, PyObject> entry : mapAsMap.entrySet()) {
                    PyObject key = entry.getKey();
                    PyObject value = entry.getValue();
                    if (key != null) {
                        map.put(key.toString(), value != null ? value.toJava(Object.class) : null);
                    }
                }
                this.settingsCache.put(pluginId, new ConcurrentHashMap<>(map));
                return map;
            }
        } catch (PyException e) {
            FileLog.e("Failed to get all plugin settings for " + pluginId, e);
        }
        return null;
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void showInstallDialog(final BaseFragment fragment, final InstallPluginBottomSheet.PluginInstallParams params) {
        android.util.Log.e("PLUGIN_INSTALL", "PythonPluginsEngine.showInstallDialog called! params.filePath=" + (params != null ? params.getFilePath() : null));
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                android.util.Log.e("PLUGIN_INSTALL", "inside globalQueue runnable!");
                try {
                    File file = new File(params.getFilePath());
                    String nameVal = PythonPluginsEngine.this.fetchParameterValue(params.getFilePath(), "name");
                    android.util.Log.e("PLUGIN_INSTALL", "nameVal=" + nameVal);
                    if (TextUtils.isEmpty(nameVal) && file.exists()) {
                        nameVal = file.getName();
                    }
                    final String strFetchParameterValue = nameVal;
                    final PluginsController.PluginValidationResult pluginValidationResultValidatePluginFromFile = PythonPluginsEngine.this.validatePluginFromFile(params.getFilePath());
                    android.util.Log.e("PLUGIN_INSTALL", "validationResult: plugin=" + pluginValidationResultValidatePluginFromFile.getPlugin() + ", error=" + pluginValidationResultValidatePluginFromFile.getError());
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            android.util.Log.e("PLUGIN_INSTALL", "inside UIThread runnable: fragment=" + fragment);
                            if (fragment == null || !AndroidUtilities.isActivityRunning(fragment.getParentActivity())) {
                                android.util.Log.e("PLUGIN_INSTALL", "activity not running in UIThread!");
                                return;
                            }
                            if (pluginValidationResultValidatePluginFromFile.getPlugin() != null) {
                                android.util.Log.e("PLUGIN_INSTALL", "SHOWING InstallPluginBottomSheet!");
                                new InstallPluginBottomSheet(fragment, pluginValidationResultValidatePluginFromFile, params).show();
                            } else {
                                android.util.Log.e("PLUGIN_INSTALL", "SHOWING Bulletin error: " + pluginValidationResultValidatePluginFromFile.getError());
                                BaseFragment baseFragment = fragment;
                                BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.error, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.PluginInstallError, strFetchParameterValue)), LocaleUtils.createCopySpan(baseFragment), new Runnable() {
                                    @Override
                                    public final void run() {
                                        PythonPluginsEngine.showInstallDialog$lambda$0$0(pluginValidationResultValidatePluginFromFile, fragment);
                                    }
                                }).show();
                            }
                        }
                    });
                } catch (Throwable t) {
                    android.util.Log.e("PLUGIN_INSTALL", "Error in showInstallDialog", t);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void showInstallDialog$lambda$0$0(PluginsController.PluginValidationResult pluginValidationResult, BaseFragment baseFragment) {
        if (AndroidUtilities.addToClipboard(pluginValidationResult.getError())) {
            BulletinFactory.of(baseFragment).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void openPluginSettings(String id, BaseFragment fragment) {
        Plugin plugin = getPluginsController().getPlugins().get(id);
        if (plugin != null) {
            openPluginSettings(plugin, fragment);
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void openPluginSettings(final Plugin plugin, final BaseFragment fragment) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                fragment.presentFragment(new PluginSettingsActivity(plugin));
            }
        });
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void openPluginSetting(final Plugin plugin, final String linkAlias, final BaseFragment fragment) {
        PluginsController.INSTANCE.runOnPluginsQueue(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                try {
                    PythonPluginsEngine.$r8$lambda$tlRSlsg46rHC7NnIW9NYVB_8Z3s(PythonPluginsEngine.this, plugin, linkAlias, fragment);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        });
    }

    public static void $r8$lambda$tlRSlsg46rHC7NnIW9NYVB_8Z3s(PythonPluginsEngine pythonPluginsEngine, Plugin plugin, String str, final BaseFragment baseFragment) throws Throwable {
        final PluginSettingsActivity settingsLinkPrefix;
        if (pythonPluginsEngine.getPluginsController().isPluginActive$TMessagesProj(plugin)) {
            FileLog.d("Opening plugin setting: " + plugin.getId() + '/' + str);
            if (!str.contains(":")) {
                settingsLinkPrefix = new PluginSettingsActivity(plugin, str);
            } else {
                List<SettingItem> list = pythonPluginsEngine.getPluginsController().getSettings().get(plugin.getId());
                if (list == null) {
                    return;
                }
                String[] strArr = (String[]) StringsKt.split((CharSequence) str, new String[]{":"}, false, 0).toArray(new String[0]);
                int length = strArr.length - 1;
                List<SettingItem> pySettingDefinitions = list;
                TextSetting textSetting = null;
                for (int i = 0; i < length; i++) {
                    String str2 = strArr[i];
                    Iterator<SettingItem> it = pySettingDefinitions.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            SettingItem next = it.next();
                            if ((next instanceof TextSetting) && Intrinsics.areEqual(str2, next.getLinkAlias())) {
                                textSetting = (TextSetting) next;
                                try {
                                    if (!pythonPluginsEngine.getPluginsController().isPluginActive$TMessagesProj(plugin)) {
                                        return;
                                    }
                                    PyObject createSubFragmentCallback = ((TextSetting) next).getCreateSubFragmentCallback();
                                    PyObject pyObjectCall = createSubFragmentCallback != null ? createSubFragmentCallback.call(new Object[0]) : null;
                                    if (pyObjectCall != null) {
                                        List<PyObject> listAsList = pyObjectCall.asList();
                                        try {
                                            pySettingDefinitions = pythonPluginsEngine.parsePySettingDefinitions(listAsList);
                                            break;
                                        } catch (Exception unused) {
                                            break;
                                        }
                                    }
                                } catch (Exception unused2) {
                                }
                            }
                        }
                        break;
                    }
                    if (textSetting == null && pySettingDefinitions.isEmpty()) {
                        SettingsRegistry.getInstance().onSettingNotFound(baseFragment);
                        return;
                    }
                }
                if (textSetting == null) {
                    return;
                }
                PluginSettingsActivity pluginSettingsActivity = new PluginSettingsActivity(plugin, textSetting.getText(), pySettingDefinitions, textSetting.getCreateSubFragmentCallback(), strArr[strArr.length - 1]);
                Object[] objArrCopyOf = Arrays.copyOf(strArr, strArr.length - 1);
                settingsLinkPrefix = pluginSettingsActivity.setSettingsLinkPrefix(ArraysKt.joinToString(objArrCopyOf, ":", "", "", -1, "...", null));
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    baseFragment.presentFragment(settingsLinkPrefix);
                }
            });
        }
    }

    @Override // com.exteragram.messenger.plugins.PluginsController.PluginsEngine
    public void openPluginSetting(String pluginId, String linkAlias, BaseFragment fragment) {
        Plugin plugin = getPluginsController().getPlugins().get(pluginId);
        if (plugin != null) {
            openPluginSetting(plugin, linkAlias, fragment);
        }
    }

    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\u0018\u0000 \u00042\u00020\u0001:\u0001\u0004B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0005"}, d2 = {"Lcom/exteragram/messenger/plugins/PythonPluginsEngine$Updater;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Updater {
        private static int TAG;
        private static boolean isLoading;
        private static long lastCheckUpdateTime;
        private static boolean notifyWhenChangeStatus;
        private static int status;

        /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
        public static final Companion INSTANCE = new Companion(null);
        private static final Pattern PYTHON_SDK_APP_VERSION_PATTERN = Pattern.compile("^app_version(>=|<=|==)(.+)$");
        private static final Pattern PYTHON_SDK_APP_VERSION_CODE_PATTERN = Pattern.compile("^app_version_code(>=|<=|==)(.+)$");
        public static final Runnable notifyRunnable = new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$Updater$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pluginsPySdkInfoChanged, new Object[0]);
            }
        };

        @JvmStatic
        public static final void checkUpdates() {
            INSTANCE.checkUpdates();
        }

        @JvmStatic
        public static final void checkUpdates(boolean z) {
            INSTANCE.checkUpdates(z);
        }

        @JvmStatic
        public static final void deleteSdkUpdateFile() {
            INSTANCE.deleteSdkUpdateFile();
        }

        @JvmStatic
        public static final File getPythonCurrentSdkFile() {
            return INSTANCE.getPythonCurrentSdkFile();
        }

        @JvmStatic
        public static final File getPythonSdkUpdateFile() {
            return INSTANCE.getPythonSdkUpdateFile();
        }

        @JvmStatic
        public static final CharSequence getStateString() {
            return INSTANCE.getStateString();
        }

        @JvmStatic
        public static final CharSequence getVersion() {
            return INSTANCE.getVersion();
        }

        @JvmStatic
        public static final String hashBytes(InputStream inputStream) {
            return INSTANCE.hashBytes(inputStream);
        }

        @JvmStatic
        public static final boolean isAppVersionCodeCompatible(String str, String str2) {
            return INSTANCE.isAppVersionCodeCompatible(str, str2);
        }

        @JvmStatic
        public static final boolean isAppVersionCompatible(String str, String str2) {
            return INSTANCE.isAppVersionCompatible(str, str2);
        }

        @JvmStatic
        public static final boolean isSdkFromApk() {
            return INSTANCE.isSdkFromApk();
        }

        @JvmStatic
        public static final boolean isSdkVersionNewer(String str, boolean z) {
            return INSTANCE.isSdkVersionNewer(str, z);
        }

        @JvmStatic
        public static final Companion.PythonSdkUpdateInfo parsePythonSdkUpdateResponse(TLRPC.messages_Messages messages_messages) {
            return INSTANCE.parsePythonSdkUpdateResponse(messages_messages);
        }

        @JvmStatic
        public static final File requestSdkFromApkFile() {
            return INSTANCE.requestSdkFromApkFile();
        }

        @JvmStatic
        public static final void restoreSdkFromApk() {
            INSTANCE.restoreSdkFromApk();
        }

        @JvmStatic
        public static final void savePythonSdkArchive(TLRPC.Message message, TLRPC.Document document) {
            INSTANCE.savePythonSdkArchive(message, document);
        }

        @JvmStatic
        public static final void savePythonSdkArchive(TLRPC.Message message, TLRPC.Document document, boolean z) {
            INSTANCE.savePythonSdkArchive(message, document, z);
        }

        @JvmStatic
        public static final InputStream sdkFromApk() throws IOException {
            return INSTANCE.sdkFromApk();
        }

        @JvmStatic
        public static final void setBuildFromApk(boolean z) {
            INSTANCE.setBuildFromApk(z);
        }

        @JvmStatic
        public static final void zipFolder(File file, File file2) {
            INSTANCE.zipFolder(file, file2);
        }

        @Metadata(d1 = {"\u0000v\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\r\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0001GB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u001a\u001a\u00020\u001bH\u0007J\n\u0010\u001c\u001a\u0004\u0018\u00010\u001bH\u0007J\b\u0010\u001d\u001a\u00020\u001eH\u0007J\b\u0010\u001f\u001a\u00020\u000bH\u0007J\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u000bH\u0007J\u0010\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\u001eH\u0007J\b\u0010&\u001a\u00020'H\u0007J\b\u0010(\u001a\u00020'H\u0007J\b\u0010)\u001a\u00020'H\u0007J\b\u0010*\u001a\u00020!H\u0007J\b\u0010+\u001a\u00020!H\u0007J\u0010\u0010+\u001a\u00020!2\u0006\u0010,\u001a\u00020\u000bH\u0007J\b\u0010-\u001a\u00020!H\u0007J\u0010\u0010.\u001a\u00020!2\u0006\u0010/\u001a\u00020'H\u0002J\u0010\u00100\u001a\u00020!2\u0006\u00101\u001a\u00020\rH\u0002J\u0012\u00102\u001a\u0004\u0018\u0001032\u0006\u00104\u001a\u000205H\u0007J\u0018\u00106\u001a\u00020\u000b2\u0006\u00107\u001a\u00020$2\u0006\u00108\u001a\u00020\u000bH\u0007J\u0018\u00109\u001a\u00020\u000b2\u0006\u0010:\u001a\u00020$2\u0006\u0010;\u001a\u00020$H\u0007J\u0018\u0010<\u001a\u00020\u000b2\u0006\u0010:\u001a\u00020$2\u0006\u0010;\u001a\u00020$H\u0007J\u0018\u0010=\u001a\u00020!2\u0006\u0010>\u001a\u00020'2\u0006\u0010?\u001a\u00020'H\u0007J\u0018\u0010@\u001a\u00020!2\u0006\u0010A\u001a\u00020B2\u0006\u0010C\u001a\u00020\u000bH\u0002J\u001c\u0010D\u001a\u00020!2\b\u0010E\u001a\u0004\u0018\u00010F2\b\u0010A\u001a\u0004\u0018\u00010BH\u0007J$\u0010D\u001a\u00020!2\b\u0010E\u001a\u0004\u0018\u00010F2\b\u0010A\u001a\u0004\u0018\u00010B2\u0006\u0010C\u001a\u00020\u000bH\u0007R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\f\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006H"}, d2 = {"Lcom/exteragram/messenger/plugins/PythonPluginsEngine$Updater$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "PYTHON_SDK_APP_VERSION_PATTERN", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "PYTHON_SDK_APP_VERSION_CODE_PATTERN", "notifyRunnable", "Ljava/lang/Runnable;", "isLoading", _UrlKt.FRAGMENT_ENCODE_SET, "status", _UrlKt.FRAGMENT_ENCODE_SET, "getStatus", "()I", "setStatus", "(I)V", "notifyWhenChangeStatus", "getNotifyWhenChangeStatus", "()Z", "setNotifyWhenChangeStatus", "(Z)V", "lastCheckUpdateTime", _UrlKt.FRAGMENT_ENCODE_SET, "TAG", "getVersion", _UrlKt.FRAGMENT_ENCODE_SET, "getStateString", "sdkFromApk", "Ljava/io/InputStream;", "isSdkFromApk", "setBuildFromApk", _UrlKt.FRAGMENT_ENCODE_SET, "fromApk", "hashBytes", _UrlKt.FRAGMENT_ENCODE_SET, "inputStream", "getPythonSdkUpdateFile", "Ljava/io/File;", "getPythonCurrentSdkFile", "requestSdkFromApkFile", "deleteSdkUpdateFile", "checkUpdates", "force", "restoreSdkFromApk", "touchFile", "file", "updateStatus", "newStatus", "parsePythonSdkUpdateResponse", "Lcom/exteragram/messenger/plugins/PythonPluginsEngine$Updater$Companion$PythonSdkUpdateInfo;", "res", "Lorg/telegram/tgnet/TLRPC$messages_Messages;", "isSdkVersionNewer", "remoteVersion", "isBeta", "isAppVersionCompatible", "operator", "targetVersion", "isAppVersionCodeCompatible", "zipFolder", "sourceDir", "zipFile", "copyArchiveToPluginsDirectory", "document", "Lorg/telegram/tgnet/TLRPC$Document;", "autoRestartEngine", "savePythonSdkArchive", "msg", "Lorg/telegram/tgnet/TLRPC$Message;", "PythonSdkUpdateInfo", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
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
                    File file = new File(PythonPluginsEngine.SDK_DIR, "v.txt");
                    if (file.exists()) {
                        String string = StringsKt.trim((CharSequence) SimpliFile.readText$default(SimpliFiles.file(file), 65536L, null, 2, null)).toString();
                        sdk_beta = string.endsWith("|1");
                        sdk_version = StringsKt.substringBefore(string, "|", string);
                    }
                }
                if (sdk_version == null) {
                    return "SDK not unpacked";
                }
                StringBuilder sb = new StringBuilder("v");
                sb.append(sdk_version);
                sb.append(sdk_beta ? "-beta" : "");
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
                            sb.append(String.format("%02x", Byte.valueOf(b2)));
                        }
                        String string = sb.toString();
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
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e2) {
                    throw new RuntimeException(e2);
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
                        boolean z = Math.abs(jCurrentTimeMillis - ExteraConfig.getSdkUpdateScheduleTimestamp()) < 3600000L;
                        if (!ExteraConfig.getPluginsEngine() || ExteraConfig.getPluginsSafeMode() || z) {
                            return;
                        }
                    }
                    ExteraConfig.setSdkUpdateScheduleTimestamp(jCurrentTimeMillis);
                    updateStatus(1);
                    Updater.lastCheckUpdateTime = jCurrentTimeMillis;
                    RemoteUtils.searchMessages("python_sdk", new TLRPC.TL_inputMessagesFilterDocument(), new Utilities.Callback2() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$Updater$Companion$$ExternalSyntheticLambda1
                        @Override // org.telegram.messenger.Utilities.Callback2
                        public final void run(Object obj, Object obj2) {
                            PythonPluginsEngine.Updater.Companion.m1321$r8$lambda$us7HlP4jQnbH6ikxQnBYzBZPGU((TLRPC.messages_Messages) obj, (TLRPC.TL_error) obj2);
                        }
                    }, 3000);
                }
            }

            /* JADX INFO: renamed from: $r8$lambda$us7HlP4jQnbH-6ikxQnBYzBZPGU, reason: not valid java name */
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
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$Updater$Companion$$ExternalSyntheticLambda0
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
                        } catch (Exception e) {
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

            /* JADX INFO: Access modifiers changed from: private */
            public static final void checkUpdates$lambda$0$0(BaseFragment baseFragment, PythonSdkUpdateInfo pythonSdkUpdateInfo) {
                baseFragment.showDialog(new PythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1(pythonSdkUpdateInfo, baseFragment.getParentActivity(), baseFragment.getCurrentAccount()));
            }

            @JvmStatic
            public final void restoreSdkFromApk() {
                touchFile(requestSdkFromApkFile());
            }

            private final void touchFile(File file) {
                try {
                    SimpliFiles.file(file).touch();
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public final void updateStatus(int newStatus) {
                setStatus(newStatus);
                if (getNotifyWhenChangeStatus()) {
                    AndroidUtilities.cancelRunOnUIThread(Updater.notifyRunnable);
                    AndroidUtilities.runOnUIThread(Updater.notifyRunnable, newStatus == 1 ? 0L : 600L);
                }
            }

            @JvmStatic
            public final PythonSdkUpdateInfo parsePythonSdkUpdateResponse(TLRPC.messages_Messages res) {
                PythonSdkUpdateInfo pythonSdkUpdateInfo = new PythonSdkUpdateInfo();
                Iterator<TLRPC.Message> it = res.messages.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    TLRPC.Message next = it.next();
                    if (next instanceof TLRPC.TL_message) {
                        TLRPC.TL_message tL_message = (TLRPC.TL_message) next;
                        if (!TextUtils.isEmpty(tL_message.message) && (tL_message.media instanceof TLRPC.TL_messageMediaDocument)) {
                            String str = tL_message.message;
                            boolean zContains$default = str.contains("python_sdk_stable");
                            String str2 = tL_message.message;
                            boolean zContains$default2 = str2.contains("python_sdk_beta");
                            if (zContains$default || zContains$default2) {
                                if (!zContains$default2 || ExteraConfig.getPluginsPySdkBetaVersions()) {
                                    StringBuilder sb = new StringBuilder();
                                    String str3 = tL_message.message;
                                    Iterator it2 = StringsKt.split((CharSequence) str3, new String[]{"\n"}, false, 0).iterator();
                                    boolean z = false;
                                    while (it2.hasNext()) {
                                        String string = StringsKt.trim((CharSequence) it2.next()).toString();
                                        if (!TextUtils.isEmpty(string) || !z) {
                                            if (string.startsWith("python_sdk_")) {
                                                pythonSdkUpdateInfo.setChannel(zContains$default2 ? "beta" : "stable");
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
                                                        List listSplit$default = StringsKt.split((CharSequence) string, new String[]{"="}, false, 2);
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
                if (!ExteraConfig.getPluginsPySdkBetaVersions() && PythonPluginsEngine.INSTANCE.getSDK_BETA()) {
                    return !isBeta;
                }
                if (PythonPluginsEngine.SDK_VERSION != null) {
                    return AppUtils.compareVersions(">", remoteVersion, PythonPluginsEngine.SDK_VERSION);
                }
                return false;
            }

            @JvmStatic
            public final boolean isAppVersionCompatible(String operator, String targetVersion) {
                return AppUtils.compareVersions(operator, BuildVars.BUILD_VERSION_STRING, targetVersion);
            }

            @JvmStatic
            public final boolean isAppVersionCodeCompatible(String operator, String targetVersion) {
                return AppUtils.compareVersions(operator, BuildVars.BUILD_VERSION, Integer.parseInt(targetVersion));
            }

            @JvmStatic
            public final void zipFolder(File sourceDir, File zipFile) {
                SimpliFiles.directory(sourceDir).zipTo(zipFile, OverwritePolicy.REPLACE);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public final void copyArchiveToPluginsDirectory(TLRPC.Document document, boolean autoRestartEngine) {
                File pythonSdkUpdateFile = getPythonSdkUpdateFile();
                try {
                    File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document);
                    SimpliFiles.file(pathToAttach).copyTo(pythonSdkUpdateFile, OverwritePolicy.REPLACE);
                    if (autoRestartEngine) {
                        PluginsController.INSTANCE.getInstance().restart();
                    } else {
                        updateStatus(4);
                    }
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
                    DownloadController.getInstance(UserConfig.selectedAccount).addLoadingFileObserver(FileLoader.getAttachFileName(document), messageObject, new DownloadController.FileDownloadProgressListener() { // from class: com.exteragram.messenger.plugins.PythonPluginsEngine$Updater$Companion$savePythonSdkArchive$1
                        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
                        public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
                        }

                        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
                        public void onProgressUpload(String fileName, long downloadSize, long totalSize, boolean isEncrypted) {
                        }

                        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
                        public void onFailedDownload(String fileName, boolean canceled) {
                            FileLog.e("Failed to load plugins-sdk file");
                            PythonPluginsEngine.Updater.isLoading = false;
                            PythonPluginsEngine.Updater.INSTANCE.updateStatus(2);
                        }

                        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
                        public void onSuccessDownload(String fileName) {
                            PythonPluginsEngine.Updater.INSTANCE.copyArchiveToPluginsDirectory(document, autoRestartEngine);
                        }

                        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
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
