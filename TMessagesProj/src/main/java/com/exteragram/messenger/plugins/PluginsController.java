package com.exteragram.messenger.plugins;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import androidx.mediarouter.media.MediaRouteProviderProtocol;
import com.chaquo.python.PyObject;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.plugins.hooks.EventHookRecord;
import com.exteragram.messenger.plugins.hooks.HookRecord;
import com.exteragram.messenger.plugins.hooks.MenuItemRecord;
import com.exteragram.messenger.plugins.hooks.PluginsHooks;
import com.exteragram.messenger.plugins.hooks.XposedHookRecord;
import com.exteragram.messenger.plugins.models.SettingItem;
import com.exteragram.messenger.plugins.ui.PluginsActivity;
import com.exteragram.messenger.plugins.ui.components.InstallPluginBottomSheet;
import com.exteragram.messenger.plugins.ui.components.SafeModeBottomSheet;
import com.exteragram.messenger.plugins.utils.MenuContextBuilder;
import com.exteragram.messenger.plugins.utils.NativeCrashHandler;
import com.exteragram.messenger.plugins.utils.PluginsWatchdog;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.sun.jna.Callback;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.lsposed.lsparanoid.Deobfuscator$exteraGramDev$TMessagesProj;
import org.simplifiles.SimpliFiles;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.LaunchActivity;

@Metadata(d1 = {"\u0000\u009e\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u0000 ¹\u00012\u00020\u0001:\f´\u0001µ\u0001¶\u0001·\u0001¸\u0001¹\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u00107\u001a\u0004\u0018\u0001082\b\u00109\u001a\u0004\u0018\u00010\u0006J\u0006\u0010:\u001a\u00020;J\u0010\u0010:\u001a\u00020;2\b\u0010<\u001a\u0004\u0018\u000106J\u000e\u0010:\u001a\u00020;2\u0006\u0010=\u001a\u00020#J\u0018\u0010:\u001a\u00020;2\u0006\u0010=\u001a\u00020#2\b\u0010<\u001a\u0004\u0018\u000106J\u0006\u0010>\u001a\u00020;J\u0010\u0010?\u001a\u00020;2\b\u0010<\u001a\u0004\u0018\u000106J\u0006\u0010@\u001a\u00020;J\u000e\u0010@\u001a\u00020;2\u0006\u0010=\u001a\u00020#J\u0006\u0010A\u001a\u00020#J\u0017\u0010B\u001a\u00020#2\b\u00109\u001a\u0004\u0018\u00010\u0006H\u0000¢\u0006\u0002\bCJ\u0017\u0010B\u001a\u00020#2\b\u0010D\u001a\u0004\u0018\u00010\u0007H\u0000¢\u0006\u0002\bCJ\u001c\u0010E\u001a\u00020\u00172\b\u0010F\u001a\u0004\u0018\u00010\u00062\b\u0010G\u001a\u0004\u0018\u00010\u0019H\u0002J\u0012\u0010H\u001a\u00020;2\b\u0010F\u001a\u0004\u0018\u00010\u0006H\u0002J\u0012\u0010I\u001a\u00020\u00172\b\u0010J\u001a\u0004\u0018\u00010\u0006H\u0002J\b\u0010K\u001a\u00020;H\u0002J\u0014\u0010L\u001a\u0004\u0018\u00010\u00062\b\u0010F\u001a\u0004\u0018\u00010\u0006H\u0002J\u0018\u0010M\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b2\b\u00109\u001a\u0004\u0018\u00010\u0006J(\u0010N\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\u0006\u0010O\u001a\u00020#2\u000e\u0010P\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010QJ \u0010R\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\u000e\u0010P\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010QJ\u0010\u0010S\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u0012\u0010T\u001a\u0004\u0018\u00010\u00062\b\u0010U\u001a\u0004\u0018\u00010\u0006J\u001a\u0010V\u001a\u00020;2\b\u0010W\u001a\u0004\u0018\u00010X2\b\u0010Y\u001a\u0004\u0018\u00010ZJ\"\u0010V\u001a\u00020;2\b\u0010W\u001a\u0004\u0018\u00010X2\b\u0010[\u001a\u0004\u0018\u00010\u00062\u0006\u0010\\\u001a\u00020#J\u001c\u0010V\u001a\u00020;2\b\u0010W\u001a\u0004\u0018\u00010X2\b\u0010]\u001a\u0004\u0018\u00010^H\u0002J\u0006\u0010_\u001a\u00020;J\u0010\u0010_\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u0010\u0010`\u001a\u00020#2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u0010\u0010a\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u001c\u0010b\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010c\u001a\u00020#H\u0007J\u001c\u0010d\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0002\b\u0003\u0018\u00010\u001f2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u0010\u0010e\u001a\u00020#2\b\u00109\u001a\u0004\u0018\u00010\u0006J\"\u0010f\u001a\u00020#2\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010g\u001a\u0004\u0018\u00010\u00062\u0006\u0010h\u001a\u00020#J&\u0010i\u001a\u0004\u0018\u00010\u00062\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010g\u001a\u0004\u0018\u00010\u00062\b\u0010h\u001a\u0004\u0018\u00010\u0006J\"\u0010j\u001a\u00020\u00172\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010g\u001a\u0004\u0018\u00010\u00062\u0006\u0010h\u001a\u00020\u0017J$\u0010k\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010g\u001a\u0004\u0018\u00010\u00062\b\u0010l\u001a\u0004\u0018\u00010!J.\u0010m\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010g\u001a\u0004\u0018\u00010\u00062\b\u0010l\u001a\u0004\u0018\u00010!2\b\u0010n\u001a\u0004\u0018\u00010oJ$\u0010p\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010q\u001a\u0004\u0018\u00010\u00142\u0006\u0010r\u001a\u00020\u0006H\u0002J(\u0010s\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\u0006\u0010t\u001a\u00020\u00062\u0006\u0010u\u001a\u00020#2\u0006\u0010v\u001a\u00020\u0017J.\u0010w\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020#0y2\u0006\u0010r\u001a\u00020\u0006H\u0002J\u0018\u0010z\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\u0006\u0010t\u001a\u00020\u0006J\u001e\u0010{\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\f\u0010|\u001a\b\u0018\u00010}R\u00020~J7\u0010\u007f\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062%\u0010\u0080\u0001\u001a \u0012\b\u0012\u00060}R\u00020~\u0018\u00010\u0081\u0001j\u000f\u0012\b\u0012\u00060}R\u00020~\u0018\u0001`\u0082\u0001J\u001f\u0010\u0083\u0001\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\f\u0010|\u001a\b\u0018\u00010}R\u00020~J\u0011\u0010\u0084\u0001\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u001e\u0010\u0085\u0001\u001a\u0004\u0018\u00010\u00062\b\u00109\u001a\u0004\u0018\u00010\u00062\t\u0010\u0086\u0001\u001a\u0004\u0018\u00010oJ\u001c\u0010\u0087\u0001\u001a\u00020#2\b\u00109\u001a\u0004\u0018\u00010\u00062\t\u0010\u0088\u0001\u001a\u0004\u0018\u00010\u0006J\u0011\u0010\u0089\u0001\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u0006J$\u0010\u008a\u0001\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000b2\t\u0010\u008b\u0001\u001a\u0004\u0018\u00010\u00062\n\u0010\u008c\u0001\u001a\u0005\u0018\u00010\u008d\u0001J/\u0010\u008a\u0001\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000b2\t\u0010\u008b\u0001\u001a\u0004\u0018\u00010\u00062\u0015\u0010\u008e\u0001\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020!\u0018\u00010\u001fJ\u0007\u0010\u008f\u0001\u001a\u00020;J\u0012\u0010\u0090\u0001\u001a\u00020;2\t\u0010\u0091\u0001\u001a\u0004\u0018\u00010\u0006J\u001a\u0010\u0092\u0001\u001a\b\u0012\u0004\u0012\u00020\u00060\u000b2\t\u0010\u0093\u0001\u001a\u0004\u0018\u00010\u0006H\u0002J\t\u0010\u0094\u0001\u001a\u00020;H\u0002J\t\u0010\u0095\u0001\u001a\u00020;H\u0002J\t\u0010\u0096\u0001\u001a\u00020#H\u0002J\u0011\u0010\u0097\u0001\u001a\u00020;2\u0006\u00109\u001a\u00020\u0006H\u0002J\u0011\u0010\u0098\u0001\u001a\u00020;2\u0006\u00109\u001a\u00020\u0006H\u0002J\t\u0010\u0099\u0001\u001a\u00020;H\u0002J>\u0010\u009a\u0001\u001a\u0005\u0018\u0001H\u009b\u0001\"\u0005\b\u0000\u0010\u009b\u00012\u0006\u0010t\u001a\u00020\u00062\n\u0010\u009c\u0001\u001a\u0005\u0018\u0001H\u009b\u00012\u000f\u0010\u009d\u0001\u001a\n\u0012\u0005\u0012\u0003H\u009b\u00010\u009e\u0001H\u0002¢\u0006\u0003\u0010\u009f\u0001J(\u0010 \u0001\u001a\u0005\u0018\u00010¡\u00012\u0007\u0010¢\u0001\u001a\u00020\u00062\u0007\u0010£\u0001\u001a\u00020\u00172\b\u0010¤\u0001\u001a\u00030¡\u0001H\u0016J4\u0010¥\u0001\u001a\u00030¦\u00012\u0007\u0010¢\u0001\u001a\u00020\u00062\u0007\u0010£\u0001\u001a\u00020\u00172\n\u0010§\u0001\u001a\u0005\u0018\u00010¡\u00012\n\u0010¨\u0001\u001a\u0005\u0018\u00010©\u0001H\u0016J(\u0010ª\u0001\u001a\u0005\u0018\u00010«\u00012\u0007\u0010¬\u0001\u001a\u00020\u00062\u0007\u0010£\u0001\u001a\u00020\u00172\b\u0010\u00ad\u0001\u001a\u00030«\u0001H\u0016J(\u0010®\u0001\u001a\u0005\u0018\u00010¯\u00012\u0007\u0010°\u0001\u001a\u00020\u00062\u0007\u0010£\u0001\u001a\u00020\u00172\b\u0010±\u0001\u001a\u00030¯\u0001H\u0016J\u001e\u0010²\u0001\u001a\u0005\u0018\u00010³\u00012\u0007\u0010£\u0001\u001a\u00020\u00172\u0007\u0010]\u001a\u00030³\u0001H\u0016R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR#\u0010\n\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\tR\u001a\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u000f0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0010\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00110\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0012\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u00130\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0015\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u000b0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00170\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u00190\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R \u0010\u001e\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u000b0\u001fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020!X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020#X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010%\u001a\u00020&X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010+\u001a\u00020,X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b-\u0010.\"\u0004\b/\u00100R\u0011\u00101\u001a\u000202¢\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u000e\u00105\u001a\u000206X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006º\u0001"}, d2 = {"Lcom/exteragram/messenger/plugins/PluginsController;", "Lcom/exteragram/messenger/plugins/hooks/PluginsHooks;", "<init>", "()V", "plugins", "Ljava/util/concurrent/ConcurrentHashMap;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/Plugin;", "getPlugins", "()Ljava/util/concurrent/ConcurrentHashMap;", "settings", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/models/SettingItem;", "getSettings", "menuItemsById", "Lcom/exteragram/messenger/plugins/hooks/MenuItemRecord;", "menuItemsByMenuType", "Ljava/util/concurrent/CopyOnWriteArrayList;", "hooks", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/hooks/HookRecord;", "interestedPluginsCache", "fileIconIdsByExtension", _UrlKt.FRAGMENT_ENCODE_SET, "fileIconDrawablesById", "Landroid/graphics/drawable/Drawable;", "nextFileIconId", "Ljava/util/concurrent/atomic/AtomicInteger;", "substringMatchEventHooksCache", "Lcom/exteragram/messenger/plugins/hooks/EventHookRecord;", "exactMatchEventHooksCache", _UrlKt.FRAGMENT_ENCODE_SET, "hooksCacheLock", _UrlKt.FRAGMENT_ENCODE_SET, "hooksCacheDirty", _UrlKt.FRAGMENT_ENCODE_SET, "initialized", "pluginsDir", "Ljava/io/File;", "getPluginsDir", "()Ljava/io/File;", "setPluginsDir", "(Ljava/io/File;)V", "preferences", "Landroid/content/SharedPreferences;", "getPreferences", "()Landroid/content/SharedPreferences;", "setPreferences", "(Landroid/content/SharedPreferences;)V", "watchdog", "Lcom/exteragram/messenger/plugins/utils/PluginsWatchdog;", "getWatchdog", "()Lcom/exteragram/messenger/plugins/utils/PluginsWatchdog;", "updateNotificationRunnable", "Ljava/lang/Runnable;", "getPluginEngine", "Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", "pluginId", "init", _UrlKt.FRAGMENT_ENCODE_SET, "onDone", "startWithSafeMode", "checkDevServers", "shutdown", "restart", "isInitialized", "isPluginActive", "isPluginActive$TMessagesProj", "plugin", "registerFileIconInternal", "extension", "drawable", "unregisterFileIconInternal", "getFileIconIdInternal", "fileName", "clearFileIconsInternal", "normalizeFileExtension", "getPluginSettingsList", "setPluginEnabled", "enabled", Callback.METHOD_NAME, "Lorg/telegram/messenger/Utilities$Callback;", "deletePlugin", "cleanupPlugin", "getPluginPath", "id", "showInstallDialog", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "messageObject", "Lorg/telegram/messenger/MessageObject;", "filePath", "trusted", "params", "Lcom/exteragram/messenger/plugins/ui/components/InstallPluginBottomSheet$PluginInstallParams;", "loadPluginSettings", "hasPluginSettings", "invalidatePluginSettings", "clearPluginSettingsPreferences", "clearEnabledState", "getPluginSettingsPreferences", "hasPluginSettingsPreferences", "getPluginSettingBoolean", "key", "defaultValue", "getPluginSettingString", "getPluginSettingInt", "setPluginSetting", "value", "setPluginSettingAndTriggerOnChange", "onChangeCallback", "Lcom/chaquo/python/PyObject;", "addHook", "newHook", "logMessage", "addEventHook", "hookName", "matchSubstring", "priority", "removeHook", "filter", "Lkotlin/Function1;", "removeEventHook", "addXposedHook", "unhook", "Lde/robv/android/xposed/XC_MethodHook$Unhook;", "Lde/robv/android/xposed/XC_MethodHook;", "addXposedHooks", "unhooks", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "removeXposedHook", "removeHooksByPluginId", "addMenuItem", "pyMenuItemData", "removeMenuItem", "itemId", "removeMenuItemsByPluginId", "getMenuItemsForLocation", "menuType", "builder", "Lcom/exteragram/messenger/plugins/utils/MenuContextBuilder;", "contextData", "notifyPluginsChanged", "executeOnAppEvent", "eventType", "getInterestedPluginIds", "eventName", "rebuildHooksCacheIfNeeded", "ensurePreferences", "isOnPluginsQueueThread", "notifyPluginSettingsRegistered", "notifyPluginSettingsUnregistered", "notifyMenuItemsUpdated", "executeGenericHook", "T", "initialObject", "caller", "Lcom/exteragram/messenger/plugins/PluginsController$EngineHookCaller;", "(Ljava/lang/String;Ljava/lang/Object;Lcom/exteragram/messenger/plugins/PluginsController$EngineHookCaller;)Ljava/lang/Object;", "executePreRequestHook", "Lorg/telegram/tgnet/TLObject;", "requestName", "account", "request", "executePostRequestHook", "Lcom/exteragram/messenger/plugins/hooks/PluginsHooks$PostRequestResult;", "response", MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "Lorg/telegram/tgnet/TLRPC$TL_error;", "executeUpdateHook", "Lorg/telegram/tgnet/TLRPC$Update;", "updateName", "update", "executeUpdatesHook", "Lorg/telegram/tgnet/TLRPC$Updates;", "containerName", "updates", "executeSendMessageHook", "Lorg/telegram/messenger/SendMessagesHelper$SendMessageParams;", "EngineHookCaller", "HookResult", "PluginValidationResult", "PluginsEngine", "SingletonHolder", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nPluginsController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PluginsController.kt\ncom/exteragram/messenger/plugins/PluginsController\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 SharedPreferences.kt\nandroidx/core/content/SharedPreferencesKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1323:1\n1#2:1324\n41#3,12:1325\n41#3,12:1337\n1915#4,2:1349\n1915#4,2:1351\n*S KotlinDebug\n*F\n+ 1 PluginsController.kt\ncom/exteragram/messenger/plugins/PluginsController\n*L\n166#1:1325,12\n478#1:1337,12\n598#1:1349,2\n813#1:1351,2\n*E\n"})
public final class PluginsController implements PluginsHooks {
    public static final int PLUGIN_FILE_ICON_ID_START = 101;
    public static final int PLUGIN_FILE_ICON_NONE = -1;
    private volatile Map<String, ? extends List<EventHookRecord>> exactMatchEventHooksCache;
    private final ConcurrentHashMap<Integer, Drawable> fileIconDrawablesById;
    private final ConcurrentHashMap<String, Integer> fileIconIdsByExtension;
    private final ConcurrentHashMap<String, Set<HookRecord>> hooks;
    private volatile boolean hooksCacheDirty;
    private final Object hooksCacheLock;
    private volatile boolean initialized;
    private final ConcurrentHashMap<String, List<String>> interestedPluginsCache;
    private final ConcurrentHashMap<String, MenuItemRecord> menuItemsById;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<MenuItemRecord>> menuItemsByMenuType;
    private final AtomicInteger nextFileIconId;
    private final ConcurrentHashMap<String, Plugin> plugins;
    private File pluginsDir;
    private SharedPreferences preferences;
    private final ConcurrentHashMap<String, List<SettingItem>> settings;
    private volatile List<EventHookRecord> substringMatchEventHooksCache;
    private final Runnable updateNotificationRunnable;
    private final PluginsWatchdog watchdog;
    public static final String PREF_PLUGIN_ENABLED_KEY_PREFIX = "plugin_enabled_";

    public static final Companion INSTANCE = new Companion(null);
    private static final ConcurrentHashMap<String, PluginsEngine> enginesMap = new ConcurrentHashMap<>(MapsKt.mapOf(TuplesKt.to("python", new PythonPluginsEngine())));

    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\bâ\u0080\u0001\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002J-\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00018\u00002\u0006\u0010\b\u001a\u00020\tH&¢\u0006\u0002\u0010\n¨\u0006\u000bÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/PluginsController$EngineHookCaller;", "T", _UrlKt.FRAGMENT_ENCODE_SET, "call", "Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "engine", "Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", "obj", "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "(Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;Ljava/lang/Object;Ljava/lang/String;)Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface EngineHookCaller<T> {
        HookResult<T> call(PluginsEngine engine, T obj, String pluginId);
    }

    @Metadata(d1 = {"\u0000\u009c\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H&J\b\u0010\b\u001a\u00020\u0003H&J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH&J\b\u0010\r\u001a\u00020\nH&J\u0010\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH&J(\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00032\u000e\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0013H&J \u0010\u0014\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u000e\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0013H&J\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0016\u001a\u00020\u0011H&J\b\u0010\u0017\u001a\u00020\u0003H&J\u0010\u0010\u0018\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u0011H&J\u0010\u0010\u0019\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u0011H&J\u0018\u0010\u001a\u001a\n\u0012\u0004\u0012\u00020\u001c\u0018\u00010\u001b2\u0006\u0010\u0016\u001a\u00020\u0011H&J$\u0010\u001d\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\u00112\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H&J\"\u0010 \u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\u00112\b\u0010!\u001a\u0004\u0018\u00010\u0001H&J\u0010\u0010\"\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u0011H&J\u001c\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0002\b\u0003\u0018\u00010$2\u0006\u0010\u0010\u001a\u00020\u0011H&J\u0010\u0010%\u001a\u00020\n2\u0006\u0010&\u001a\u00020\u0011H&J0\u0010'\u001a\b\u0012\u0004\u0012\u00020)0(2\u0006\u0010*\u001a\u00020\u00112\u0006\u0010+\u001a\u00020,2\b\u0010-\u001a\u0004\u0018\u00010)2\u0006\u0010\u0010\u001a\u00020\u0011H&J:\u0010.\u001a\b\u0012\u0004\u0012\u00020/0(2\u0006\u0010*\u001a\u00020\u00112\u0006\u0010+\u001a\u00020,2\b\u00100\u001a\u0004\u0018\u00010)2\b\u00101\u001a\u0004\u0018\u0001022\u0006\u0010\u0010\u001a\u00020\u0011H&J0\u00103\u001a\b\u0012\u0004\u0012\u0002040(2\u0006\u00105\u001a\u00020\u00112\u0006\u0010+\u001a\u00020,2\b\u00106\u001a\u0004\u0018\u0001042\u0006\u0010\u0010\u001a\u00020\u0011H&J0\u00107\u001a\b\u0012\u0004\u0012\u0002080(2\u0006\u00109\u001a\u00020\u00112\u0006\u0010+\u001a\u00020,2\b\u0010:\u001a\u0004\u0018\u0001082\u0006\u0010\u0010\u001a\u00020\u0011H&J(\u0010;\u001a\b\u0012\u0004\u0012\u00020<0(2\u0006\u0010+\u001a\u00020,2\b\u0010=\u001a\u0004\u0018\u00010<2\u0006\u0010\u0010\u001a\u00020\u0011H&J\u0018\u0010>\u001a\u00020\n2\u0006\u0010?\u001a\u00020@2\u0006\u0010=\u001a\u00020AH&J\u0018\u0010B\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010?\u001a\u00020@H&J\u0018\u0010B\u001a\u00020\n2\u0006\u0010C\u001a\u00020D2\u0006\u0010?\u001a\u00020@H&J \u0010E\u001a\u00020\n2\u0006\u0010C\u001a\u00020D2\u0006\u0010F\u001a\u00020\u00112\u0006\u0010?\u001a\u00020@H&J \u0010E\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010F\u001a\u00020\u00112\u0006\u0010?\u001a\u00020@H&¨\u0006GÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", _UrlKt.FRAGMENT_ENCODE_SET, "isPlugin", _UrlKt.FRAGMENT_ENCODE_SET, "file", "Ljava/io/File;", "messageObject", "Lorg/telegram/messenger/MessageObject;", "isEngineAvailable", "init", _UrlKt.FRAGMENT_ENCODE_SET, Callback.METHOD_NAME, "Ljava/lang/Runnable;", "checkDevServer", "shutdown", "setPluginEnabled", "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "enabled", "Lorg/telegram/messenger/Utilities$Callback;", "deletePlugin", "getPluginPath", "id", "canOpenInExternalApp", "openInExternalApp", "sharePlugin", "loadPluginSettings", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/models/SettingItem;", "getPluginSetting", "key", "defaultValue", "setPluginSetting", "value", "clearPluginSettings", "getAllPluginSettings", _UrlKt.FRAGMENT_ENCODE_SET, "executeOnAppEvent", "eventType", "executePreRequestHook", "Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "Lorg/telegram/tgnet/TLObject;", "requestName", "account", _UrlKt.FRAGMENT_ENCODE_SET, "request", "executePostRequestHook", "Lcom/exteragram/messenger/plugins/hooks/PluginsHooks$PostRequestResult;", "response", MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "Lorg/telegram/tgnet/TLRPC$TL_error;", "executeUpdateHook", "Lorg/telegram/tgnet/TLRPC$Update;", "updateName", "update", "executeUpdatesHook", "Lorg/telegram/tgnet/TLRPC$Updates;", "containerName", "updates", "executeSendMessageHook", "Lorg/telegram/messenger/SendMessagesHelper$SendMessageParams;", "params", "showInstallDialog", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "Lcom/exteragram/messenger/plugins/ui/components/InstallPluginBottomSheet$PluginInstallParams;", "openPluginSettings", "plugin", "Lcom/exteragram/messenger/plugins/Plugin;", "openPluginSetting", "linkAlias", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface PluginsEngine {
        boolean canOpenInExternalApp();

        void checkDevServer();

        void clearPluginSettings(String pluginId);

        void deletePlugin(String pluginId, Utilities.Callback<String> callback);

        void executeOnAppEvent(String eventType);

        HookResult<PluginsHooks.PostRequestResult> executePostRequestHook(String requestName, int account, TLObject response, TLRPC.TL_error error, String pluginId);

        HookResult<TLObject> executePreRequestHook(String requestName, int account, TLObject request, String pluginId);

        HookResult<SendMessagesHelper.SendMessageParams> executeSendMessageHook(int account, SendMessagesHelper.SendMessageParams params, String pluginId);

        HookResult<TLRPC.Update> executeUpdateHook(String updateName, int account, TLRPC.Update update, String pluginId);

        HookResult<TLRPC.Updates> executeUpdatesHook(String containerName, int account, TLRPC.Updates updates, String pluginId);

        Map<String, ?> getAllPluginSettings(String pluginId);

        String getPluginPath(String id);

        Object getPluginSetting(String pluginId, String key, Object defaultValue);

        void init(Runnable callback);

        boolean isEngineAvailable();

        boolean isPlugin(File file, MessageObject messageObject);

        List<SettingItem> loadPluginSettings(String id);

        void openInExternalApp(String id);

        void openPluginSetting(Plugin plugin, String linkAlias, BaseFragment fragment);

        void openPluginSetting(String pluginId, String linkAlias, BaseFragment fragment);

        void openPluginSettings(Plugin plugin, BaseFragment fragment);

        void openPluginSettings(String id, BaseFragment fragment);

        void setPluginEnabled(String pluginId, boolean enabled, Utilities.Callback<String> callback);

        void setPluginSetting(String pluginId, String key, Object value);

        void sharePlugin(String id);

        void showInstallDialog(BaseFragment fragment, InstallPluginBottomSheet.PluginInstallParams params);

        void shutdown(Runnable callback);
    }

    public static void $r8$lambda$ToGKbtqH63z4zUzeWzrnKViKrbk() {
    }

    public void clearPluginSettingsPreferences$default(PluginsController pluginsController, String str, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        pluginsController.clearPluginSettingsPreferences(str, z);
    }

    @JvmOverloads
    public final void clearPluginSettingsPreferences(String pluginId, boolean clearEnabledState) {
        if (pluginId == null || pluginId.length() == 0) {
            return;
        }
        PluginsEngine pluginEngine = getPluginEngine(pluginId);
        if (pluginEngine != null) {
            pluginEngine.clearPluginSettings(pluginId);
        } else {
            for (PluginsEngine pluginsEngine : INSTANCE.getEngines().values()) {
                "next(...)";
                pluginsEngine.clearPluginSettings(pluginId);
            }
        }
        ensurePreferences();
        if (clearEnabledState) {
            String str = "plugin_enabled_" + pluginId;
            if (this.preferences.contains(str)) {
                SharedPreferences.Editor editorEdit = this.preferences.edit();
                editorEdit.remove(str);
                editorEdit.apply();
            }
        }
    }

    public final Map<String, ?> getPluginSettingsPreferences(String pluginId) {
        PluginsEngine pluginEngine = getPluginEngine(pluginId);
        if (pluginEngine != null) {
            return pluginEngine.getAllPluginSettings(pluginId);
        }
        return null;
    }

    public final boolean hasPluginSettingsPreferences(String pluginId) {
        Map<String, ?> pluginSettingsPreferences = getPluginSettingsPreferences(pluginId);
        return !(pluginSettingsPreferences == null || pluginSettingsPreferences.isEmpty());
    }

    public final boolean getPluginSettingBoolean(String pluginId, String key, boolean defaultValue) {
        PluginsEngine pluginEngine;
        if (pluginId != null && pluginId.length() != 0 && key != null && key.length() != 0 && (pluginEngine = getPluginEngine(pluginId)) != null) {
            Object pluginSetting = pluginEngine.getPluginSetting(pluginId, key, Boolean.valueOf(defaultValue));
            if (pluginSetting instanceof Boolean) {
                return ((Boolean) pluginSetting).booleanValue();
            }
        }
        return defaultValue;
    }

    public final String getPluginSettingString(String pluginId, String key, String defaultValue) {
        PluginsEngine pluginEngine;
        Object pluginSetting;
        return (pluginId == null || pluginId.length() == 0 || key == null || key.length() == 0 || (pluginEngine = getPluginEngine(pluginId)) == null || (pluginSetting = pluginEngine.getPluginSetting(pluginId, key, defaultValue)) == null) ? defaultValue : pluginSetting.toString();
    }

    public final int getPluginSettingInt(String pluginId, String key, int defaultValue) {
        PluginsEngine pluginEngine;
        if (pluginId != null && pluginId.length() != 0 && key != null && key.length() != 0 && (pluginEngine = getPluginEngine(pluginId)) != null) {
            Object pluginSetting = pluginEngine.getPluginSetting(pluginId, key, Integer.valueOf(defaultValue));
            if (pluginSetting instanceof Number) {
                return ((Number) pluginSetting).intValue();
            }
        }
        return defaultValue;
    }

    public final void setPluginSetting(String pluginId, String key, Object value) {
        setPluginSettingAndTriggerOnChange(pluginId, key, value, null);
    }

    public final void setPluginSettingAndTriggerOnChange(String pluginId, String key, Object value, PyObject onChangeCallback) {
        PluginsEngine pluginEngine;
        if (pluginId == null || pluginId.length() == 0 || key == null || key.length() == 0 || !isPluginActive$TMessagesProj(pluginId) || (pluginEngine = getPluginEngine(pluginId)) == null) {
            return;
        }
        pluginEngine.setPluginSetting(pluginId, key, value);
        if (onChangeCallback != null) {
            try {
                onChangeCallback.call(value);
            } catch (Exception e) {
                FileLog.e("Error executing on_change callback for " + pluginId + '/' + key, e);
            }
        }
        loadPluginSettings(pluginId);
    }

    private final void addHook(String pluginId, HookRecord newHook, String logMessage) {
        if (pluginId == null || pluginId.length() == 0 || newHook == null) {
            return;
        }
        ConcurrentHashMap<String, Set<HookRecord>> concurrentHashMap = this.hooks;
        final Function1 function1 = new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return PluginsController.$r8$lambda$1waTlSZq4_YECP84Ln2JBSIJadc((String) obj);
            }
        };
        Set<HookRecord> setComputeIfAbsent = concurrentHashMap.computeIfAbsent(pluginId, new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return PluginsController.$r8$lambda$Y2S2tGZb5s_dlwEiuNwPZF2NaoU(function1, obj);
            }
        });
        "computeIfAbsent(...)";
        if (setComputeIfAbsent.add(newHook)) {
            FileLog.d(logMessage);
            this.interestedPluginsCache.clear();
            this.hooksCacheDirty = true;
        }
    }

    public static Set $r8$lambda$1waTlSZq4_YECP84Ln2JBSIJadc(String str) {
        "it";
        return new CopyOnWriteArraySet();
    }

    public static Set $r8$lambda$Y2S2tGZb5s_dlwEiuNwPZF2NaoU(Function1 function1, Object obj) {
        return (Set) function1.invoke(obj);
    }

    public final void addEventHook(String pluginId, String hookName, boolean matchSubstring, int priority) {
        "hookName";
        addHook(pluginId, new EventHookRecord(pluginId, hookName, matchSubstring, priority), "Added event hook '" + hookName + "' for plugin " + pluginId);
    }

    private final void removeHook(String pluginId, Function1<? super HookRecord, Boolean> filter, String logMessage) {
        Set<HookRecord> set;
        if (pluginId == null || pluginId.length() == 0 || (set = this.hooks.get(pluginId)) == null || set.isEmpty()) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (HookRecord hookRecord : set) {
            if (filter.invoke(hookRecord).booleanValue()) {
                arrayList2.add(hookRecord);
            } else {
                arrayList.add(hookRecord);
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            ((HookRecord) obj).cleanup();
        }
        boolean zIsEmpty = arrayList.isEmpty();
        ConcurrentHashMap<String, Set<HookRecord>> concurrentHashMap = this.hooks;
        if (zIsEmpty) {
            concurrentHashMap.remove(pluginId);
        } else {
            concurrentHashMap.put(pluginId, new CopyOnWriteArraySet(arrayList));
        }
        FileLog.d(logMessage);
        this.interestedPluginsCache.clear();
        this.hooksCacheDirty = true;
    }

    public final void removeEventHook(String pluginId, final String hookName) {
        "hookName";
        removeHook(pluginId, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(PluginsController.$r8$lambda$m8Lepk5YSi6L1M4adJRbExRUjfI(hookName, (HookRecord) obj));
            }
        }, "Removed event hook(s) matching name '" + hookName + "' for plugin " + pluginId);
    }

    public static boolean $r8$lambda$m8Lepk5YSi6L1M4adJRbExRUjfI(String str, HookRecord hookRecord) {
        "record";
        return (hookRecord instanceof EventHookRecord) && Intrinsics.areEqual(((EventHookRecord) hookRecord).getHookName(), str);
    }

    public final void addXposedHook(String pluginId, XC_MethodHook.Unhook unhook) {
        addHook(pluginId, unhook != null ? new XposedHookRecord(unhook) : null, "Added Xposed hook for plugin " + pluginId);
    }

    public final void addXposedHooks(String pluginId, ArrayList<XC_MethodHook.Unhook> unhooks) {
        if (unhooks == null) {
            return;
        }
        "iterator(...)";
        for (XC_MethodHook.Unhook unhook : unhooks) {
            "next(...)";
            addXposedHook(pluginId, unhook);
        }
    }

    public final void removeXposedHook(String pluginId, final XC_MethodHook.Unhook unhook) {
        removeHook(pluginId, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(PluginsController.$r8$lambda$nCbAkJU29JwWnZ6UwahK1r3Ab34(unhook, (HookRecord) obj));
            }
        }, "Removed Xposed hook for plugin " + pluginId);
    }

    public static boolean $r8$lambda$nCbAkJU29JwWnZ6UwahK1r3Ab34(XC_MethodHook.Unhook unhook, HookRecord hookRecord) {
        "record";
        return (hookRecord instanceof XposedHookRecord) && ((XposedHookRecord) hookRecord).matches(unhook);
    }

    public final void removeHooksByPluginId(String pluginId) {
        Set<HookRecord> setRemove;
        if (pluginId == null || pluginId.length() == 0 || (setRemove = this.hooks.remove(pluginId)) == null) {
            return;
        }
        Iterator<HookRecord> it = setRemove.iterator();
        while (it.hasNext()) {
            it.next().cleanup();
        }
        FileLog.d("Removed all (" + setRemove.size() + ") hooks for plugin " + pluginId);
        this.interestedPluginsCache.clear();
        this.hooksCacheDirty = true;
    }

    public final String addMenuItem(String pluginId, PyObject pyMenuItemData) {
        if (INSTANCE.isPluginEngineAvailable() && pyMenuItemData != null && pluginId != null && pluginId.length() != 0) {
            try {
                final MenuItemRecord menuItemRecord = new MenuItemRecord(pluginId, pyMenuItemData);
                String menuType = menuItemRecord.getMenuType();
                if (menuType == null) {
                    return null;
                }
                MenuItemRecord menuItemRecord2 = this.menuItemsById.get(menuItemRecord.getItemId());
                if (menuItemRecord2 != null && !Intrinsics.areEqual(menuItemRecord2.getPluginId(), pluginId)) {
                    FileLog.w("Plugin " + pluginId + " tried to add a menu item: " + menuItemRecord.getItemId() + ", which is already used by plugin " + menuItemRecord2.getPluginId());
                    return null;
                }
                if (menuItemRecord2 != null) {
                    CopyOnWriteArrayList<MenuItemRecord> copyOnWriteArrayList = this.menuItemsByMenuType.get(menuItemRecord2.getMenuType());
                    if (copyOnWriteArrayList != null) {
                        copyOnWriteArrayList.remove(menuItemRecord2);
                    }
                    menuItemRecord2.markRemoved();
                }
                this.menuItemsById.put(menuItemRecord.getItemId(), menuItemRecord);
                ConcurrentHashMap<String, CopyOnWriteArrayList<MenuItemRecord>> concurrentHashMap = this.menuItemsByMenuType;
                final Function2 function2 = new Function2() { 
                    @Override // kotlin.jvm.functions.Function2
                    public final Object invoke(Object obj, Object obj2) {
                        return PluginsController.m1307$r8$lambda$6XhX7yTKSS7CldZXwz1RN6kOJE(menuItemRecord, (String) obj, (CopyOnWriteArrayList) obj2);
                    }
                };
                concurrentHashMap.compute(menuType, new BiFunction() { 
                    @Override // java.util.function.BiFunction
                    public final Object apply(Object obj, Object obj2) {
                        return PluginsController.$r8$lambda$D9is3Zmj_FosL19hVEaWnD7rlMc(function2, obj, obj2);
                    }
                });
                FileLog.d("Added menu item: " + menuItemRecord.getItemId() + " for plugin " + pluginId + " in type " + menuType);
                notifyMenuItemsUpdated();
                return menuItemRecord.getItemId();
            } catch (Exception unused) {
            }
        }
        return null;
    }

    public static CopyOnWriteArrayList $r8$lambda$D9is3Zmj_FosL19hVEaWnD7rlMc(Function2 function2, Object obj, Object obj2) {
        return (CopyOnWriteArrayList) function2.invoke(obj, obj2);
    }

    public static CopyOnWriteArrayList m1307$r8$lambda$6XhX7yTKSS7CldZXwz1RN6kOJE(final MenuItemRecord menuItemRecord, String str, CopyOnWriteArrayList copyOnWriteArrayList) {
        ArrayList arrayList;
        "<unused var>";
        if (copyOnWriteArrayList == null) {
            arrayList = new ArrayList();
        } else {
            arrayList = new ArrayList(copyOnWriteArrayList);
            CollectionsKt.removeAll((List) arrayList, new Function1() { 
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return Boolean.valueOf(PluginsController.addMenuItem$lambda$0$0$0(menuItemRecord, (MenuItemRecord) obj));
                }
            });
        }
        arrayList.add(menuItemRecord);
        final Function1 function1 = new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Integer.valueOf(((MenuItemRecord) obj).getPriority());
            }
        };
        Comparator comparatorReversed = Comparator.comparingInt(new ToIntFunction() { 
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return PluginsController.addMenuItem$lambda$0$2(function1, obj);
            }
        }).reversed();
        "reversed(...)";
        CollectionsKt.sortWith(arrayList, comparatorReversed);
        return new CopyOnWriteArrayList(arrayList);
    }

    public static final boolean addMenuItem$lambda$0$0$0(MenuItemRecord menuItemRecord, MenuItemRecord menuItemRecord2) {
        return Intrinsics.areEqual(menuItemRecord2.getItemId(), menuItemRecord.getItemId());
    }

    public static final int addMenuItem$lambda$0$2(Function1 function1, Object obj) {
        return ((Number) function1.invoke(obj)).intValue();
    }

    public final boolean removeMenuItem(String pluginId, String itemId) {
        MenuItemRecord menuItemRecordRemove;
        if (itemId == null || itemId.length() == 0 || (menuItemRecordRemove = this.menuItemsById.remove(itemId)) == null || menuItemRecordRemove.getMenuType() == null) {
            return false;
        }
        if (!Intrinsics.areEqual(menuItemRecordRemove.getPluginId(), pluginId)) {
            this.menuItemsById.put(itemId, menuItemRecordRemove);
            return false;
        }
        CopyOnWriteArrayList<MenuItemRecord> copyOnWriteArrayList = this.menuItemsByMenuType.get(menuItemRecordRemove.getMenuType());
        if (copyOnWriteArrayList != null) {
            copyOnWriteArrayList.remove(menuItemRecordRemove);
        }
        menuItemRecordRemove.markRemoved();
        FileLog.d("Removed menu item: " + itemId + " for plugin " + pluginId);
        notifyMenuItemsUpdated();
        return true;
    }

    public final void removeMenuItemsByPluginId(String pluginId) {
        if (pluginId == null || pluginId.length() == 0) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (MenuItemRecord menuItemRecord : this.menuItemsById.values()) {
            "next(...)";
            MenuItemRecord menuItemRecord2 = menuItemRecord;
            if (Intrinsics.areEqual(menuItemRecord2.getPluginId(), pluginId)) {
                arrayList.add(menuItemRecord2.getItemId());
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        "iterator(...)";
        for (Object obj : arrayList) {
            "next(...)";
            removeMenuItem(pluginId, (String) obj);
        }
        FileLog.d("Removed all menu items for plugin: " + pluginId);
    }

    public final List<MenuItemRecord> getMenuItemsForLocation(String menuType, MenuContextBuilder builder) {
        if (builder == null) {
            return getMenuItemsForLocation(menuType, new HashMap());
        }
        return getMenuItemsForLocation(menuType, (Map<String, ? extends Object>) builder.build());
    }

    public final List<MenuItemRecord> getMenuItemsForLocation(String menuType, Map<String, ? extends Object> contextData) {
        CopyOnWriteArrayList<MenuItemRecord> copyOnWriteArrayList;
        if (!INSTANCE.isPluginEngineAvailable() || menuType == null || menuType.length() == 0) {
            List<MenuItemRecord> list = Collections.EMPTY_LIST;
            "emptyList(...)";
            return list;
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        CopyOnWriteArrayList<MenuItemRecord> copyOnWriteArrayList2 = this.menuItemsByMenuType.get(menuType);
        if (copyOnWriteArrayList2 != null && !copyOnWriteArrayList2.isEmpty()) {
            linkedHashSet.addAll(copyOnWriteArrayList2);
        }
        if (Intrinsics.areEqual("main_menu", menuType) && (copyOnWriteArrayList = this.menuItemsByMenuType.get("drawer_menu")) != null && !copyOnWriteArrayList.isEmpty()) {
            linkedHashSet.addAll(copyOnWriteArrayList);
        }
        if (linkedHashSet.isEmpty()) {
            List<MenuItemRecord> list2 = Collections.EMPTY_LIST;
            "emptyList(...)";
            return list2;
        }
        ArrayList arrayList = new ArrayList();
        "iterator(...)";
        for (Object obj : linkedHashSet) {
            "next(...)";
            MenuItemRecord menuItemRecord = (MenuItemRecord) obj;
            if (isPluginActive$TMessagesProj(menuItemRecord.getPluginId())) {
                this.watchdog.onPluginExecutionStarted(menuItemRecord.getPluginId());
                try {
                    if (menuItemRecord.checkCondition(contextData)) {
                        arrayList.add(menuItemRecord);
                    }
                    this.watchdog.onPluginExecutionFinished(menuItemRecord.getPluginId());
                } catch (Throwable th) {
                    this.watchdog.onPluginExecutionFinished(menuItemRecord.getPluginId());
                    throw th;
                }
            }
        }
        return arrayList;
    }

    public final void notifyPluginsChanged() {
        AndroidUtilities.cancelRunOnUIThread(this.updateNotificationRunnable);
        AndroidUtilities.runOnUIThread(this.updateNotificationRunnable, 150L);
    }

    public final void executeOnAppEvent(String eventType) {
        if (this.initialized) {
            Companion companion = INSTANCE;
            if (!companion.isPluginEngineAvailable() || eventType == null) {
                return;
            }
            FileLog.d("Execute scripts on app event " + eventType);
            for (PluginsEngine pluginsEngine : companion.getEngines().values()) {
                "next(...)";
                pluginsEngine.executeOnAppEvent(eventType);
            }
        }
    }

    Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @JvmStatic
        public final boolean isPluginEngineSupported() {
            return true;
        }

        private Companion() {
        }

        @JvmStatic
        public final ConcurrentHashMap<String, PluginsEngine> getEngines() {
            return PluginsController.enginesMap;
        }

        @JvmStatic
        public final PluginsController getInstance() {
            return SingletonHolder.INSTANCE.getINSTANCE();
        }

        @JvmStatic
        public final int registerFileIcon(String extension, Drawable drawable) {
            return getInstance().registerFileIconInternal(extension, drawable);
        }

        @JvmStatic
        public final void unregisterFileIcon(String extension) {
            getInstance().unregisterFileIconInternal(extension);
        }

        @JvmStatic
        public final void clearFileIcons() {
            getInstance().clearFileIconsInternal();
        }

        @JvmStatic
        public final int getFileIconId(String fileName) {
            return getInstance().getFileIconIdInternal(fileName);
        }

        @JvmStatic
        public final boolean isPluginFileIcon(int icon) {
            if (icon < 101) {
                return false;
            }
            return getInstance().fileIconDrawablesById.containsKey(Integer.valueOf(icon));
        }

        @JvmStatic
        public final Drawable getPluginFileIconDrawable(int icon) {
            return (Drawable) getInstance().fileIconDrawablesById.get(Integer.valueOf(icon));
        }

        @JvmStatic
        public final boolean isPluginEngineAvailable() {
            if (isPluginEngineSupported() && ExteraConfig.getPluginsEngine() && !ExteraConfig.getPluginsSafeMode()) {
                for (PluginsEngine pluginsEngine : getEngines().values()) {
                    "next(...)";
                    try {
                        if (pluginsEngine.isEngineAvailable()) {
                            return true;
                        }
                    } catch (Throwable th) {
                        FileLog.e("Error checking engine availability.", th);
                    }
                }
            }
            return false;
        }

        @JvmStatic
        public final void applyArtOpts() {
            if (ExteraConfig.getPreferences().getBoolean("pluginsEngine", false) && ExteraConfig.getPluginsDisableArtOpts() && isPluginEngineSupported()) {
                try {
                    XposedBridge.disableProfileSaver();
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }

        @JvmStatic
        public final boolean isPlugin(MessageObject messageObject) {
            String pathToMessage = ChatUtils.getInstance().getPathToMessage(messageObject);
            return (messageObject == null || messageObject.getDocumentName() == null || TextUtils.isEmpty(pathToMessage) || !isPlugin(new File(pathToMessage), messageObject) || !isPluginEngineSupported()) ? false : true;
        }

        @JvmStatic
        public final boolean isPlugin(File file, MessageObject messageObject) {
            if (file == null) {
                return false;
            }
            for (PluginsEngine pluginsEngine : getEngines().values()) {
                "next(...)";
                if (pluginsEngine.isPlugin(file, messageObject)) {
                    return true;
                }
            }
            return false;
        }

        @JvmStatic
        public final PluginsEngine getPluginEngine(File file) {
            if (file == null) {
                return null;
            }
            for (PluginsEngine pluginsEngine : getEngines().values()) {
                "next(...)";
                PluginsEngine pluginsEngine2 = pluginsEngine;
                if (pluginsEngine2.isPlugin(file, null)) {
                    return pluginsEngine2;
                }
            }
            return null;
        }

        @JvmStatic
        public final void openPluginSettings(String pluginId) {
            openPluginSettings(pluginId, null);
        }

        @JvmStatic
        public final void openPluginSettings(String pluginId, String linkAlias) {
            final BaseFragment lastFragment;
            if (pluginId == null || pluginId.length() == 0 || (lastFragment = LaunchActivity.getLastFragment()) == null) {
                return;
            }
            if (!ExteraConfig.getPluginsEngine()) {
                BulletinFactory.of(lastFragment).createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.PluginEngineNotEnabled, pluginId), LocaleController.getString(R.string.Enable), 2750, new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        lastFragment.presentFragment(new PluginsActivity());
                    }
                }).show();
                return;
            }
            Plugin plugin = getInstance().getPlugins().get(pluginId);
            if (plugin == null) {
                BulletinFactory.of(lastFragment).createEmojiBulletin("🤷‍♂️", LocaleController.formatString(R.string.PluginNotFound, pluginId)).show();
                return;
            }
            if (!getInstance().hasPluginSettings(pluginId)) {
                BulletinFactory.of(lastFragment).createEmojiBulletin("🤷‍♂️", LocaleController.formatString(R.string.PluginHasNoSettings, plugin.getName())).show();
                return;
            }
            PluginsEngine pluginEngine = getInstance().getPluginEngine(pluginId);
            if (pluginEngine != null) {
                if (linkAlias == null) {
                    pluginEngine.openPluginSettings(pluginId, lastFragment);
                } else {
                    pluginEngine.openPluginSetting(pluginId, linkAlias, lastFragment);
                }
            }
        }

        @JvmStatic
        public final boolean isPluginPinned(String pluginId) {
            return (pluginId == null || pluginId.length() == 0 || !ExteraConfig.getPinnedPlugins().contains(pluginId)) ? false : true;
        }

        @JvmStatic
        public final void setPluginPinned(String pluginId, boolean isPinned) {
            if (pluginId == null || pluginId.length() == 0) {
                return;
            }
            HashSet hashSet = new HashSet(ExteraConfig.getPinnedPlugins());
            if (!isPinned) {
                hashSet.remove(pluginId);
            } else {
                hashSet.add(pluginId);
            }
            ExteraConfig.setPinnedPlugins(hashSet);
            ExteraConfig.getEditor().putStringSet("pinnedPlugins", hashSet).apply();
            getInstance().notifyPluginsChanged();
        }

        @JvmStatic
        public final void runOnPluginsQueue(Runnable runnable) {
            "runnable";
            if (Utilities.pluginsQueue == null || !Utilities.pluginsQueue.isAlive()) {
                synchronized (PluginsController.class) {
                    try {
                        if (Utilities.pluginsQueue == null || !Utilities.pluginsQueue.isAlive()) {
                            Utilities.pluginsQueue = new DispatchQueue("pluginsQueue");
                        }
                        Unit unit = Unit.INSTANCE;
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
            Utilities.pluginsQueue.postRunnable(runnable);
        }
    }
}
