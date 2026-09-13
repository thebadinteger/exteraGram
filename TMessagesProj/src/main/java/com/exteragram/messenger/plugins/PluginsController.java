package com.exteragram.messenger.plugins;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
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
import org.simplifiles.SimpliFiles;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
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

@Metadata(d1 = {"\u0000\u009e\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u0000 ¹\u00012\u00020\u0001:\f´\u0001µ\u0001¶\u0001·\u0001¸\u0001¹\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u00107\u001a\u0004\u0018\u0001082\b\u00109\u001a\u0004\u0018\u00010\u0006J\u0006\u0010:\u001a\u00020;J\u0010\u0010:\u001a\u00020;2\b\u0010<\u001a\u0004\u0018\u000106J\u000e\u0010:\u001a\u00020;2\u0006\u0010=\u001a\u00020#J\u0018\u0010:\u001a\u00020;2\u0006\u0010=\u001a\u00020#2\b\u0010<\u001a\u0004\u0018\u000106J\u0006\u0010>\u001a\u00020;J\u0010\u0010?\u001a\u00020;2\b\u0010<\u001a\u0004\u0018\u000106J\u0006\u0010@\u001a\u00020;J\u000e\u0010@\u001a\u00020;2\u0006\u0010=\u001a\u00020#J\u0006\u0010A\u001a\u00020#J\u0017\u0010B\u001a\u00020#2\b\u00109\u001a\u0004\u0018\u00010\u0006H\u0000¢\u0006\u0002\bCJ\u0017\u0010B\u001a\u00020#2\b\u0010D\u001a\u0004\u0018\u00010\u0007H\u0000¢\u0006\u0002\bCJ\u001c\u0010E\u001a\u00020\u00172\b\u0010F\u001a\u0004\u0018\u00010\u00062\b\u0010G\u001a\u0004\u0018\u00010\u0019H\u0002J\u0012\u0010H\u001a\u00020;2\b\u0010F\u001a\u0004\u0018\u00010\u0006H\u0002J\u0012\u0010I\u001a\u00020\u00172\b\u0010J\u001a\u0004\u0018\u00010\u0006H\u0002J\b\u0010K\u001a\u00020;H\u0002J\u0014\u0010L\u001a\u0004\u0018\u00010\u00062\b\u0010F\u001a\u0004\u0018\u00010\u0006H\u0002J\u0018\u0010M\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b2\b\u00109\u001a\u0004\u0018\u00010\u0006J(\u0010N\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\u0006\u0010O\u001a\u00020#2\u000e\u0010P\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010QJ \u0010R\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\u000e\u0010P\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010QJ\u0010\u0010S\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u0012\u0010T\u001a\u0004\u0018\u00010\u00062\b\u0010U\u001a\u0004\u0018\u00010\u0006J\u001a\u0010V\u001a\u00020;2\b\u0010W\u001a\u0004\u0018\u00010X2\b\u0010Y\u001a\u0004\u0018\u00010ZJ\"\u0010V\u001a\u00020;2\b\u0010W\u001a\u0004\u0018\u00010X2\b\u0010[\u001a\u0004\u0018\u00010\u00062\u0006\u0010\\\u001a\u00020#J\u001c\u0010V\u001a\u00020;2\b\u0010W\u001a\u0004\u0018\u00010X2\b\u0010]\u001a\u0004\u0018\u00010^H\u0002J\u0006\u0010_\u001a\u00020;J\u0010\u0010_\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u0010\u0010`\u001a\u00020#2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u0010\u0010a\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u001c\u0010b\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010c\u001a\u00020#H\u0007J\u001c\u0010d\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0002\b\u0003\u0018\u00010\u001f2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u0010\u0010e\u001a\u00020#2\b\u00109\u001a\u0004\u0018\u00010\u0006J\"\u0010f\u001a\u00020#2\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010g\u001a\u0004\u0018\u00010\u00062\u0006\u0010h\u001a\u00020#J&\u0010i\u001a\u0004\u0018\u00010\u00062\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010g\u001a\u0004\u0018\u00010\u00062\b\u0010h\u001a\u0004\u0018\u00010\u0006J\"\u0010j\u001a\u00020\u00172\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010g\u001a\u0004\u0018\u00010\u00062\u0006\u0010h\u001a\u00020\u0017J$\u0010k\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010g\u001a\u0004\u0018\u00010\u00062\b\u0010l\u001a\u0004\u0018\u00010!J.\u0010m\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010g\u001a\u0004\u0018\u00010\u00062\b\u0010l\u001a\u0004\u0018\u00010!2\b\u0010n\u001a\u0004\u0018\u00010oJ$\u0010p\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\b\u0010q\u001a\u0004\u0018\u00010\u00142\u0006\u0010r\u001a\u00020\u0006H\u0002J(\u0010s\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\u0006\u0010t\u001a\u00020\u00062\u0006\u0010u\u001a\u00020#2\u0006\u0010v\u001a\u00020\u0017J.\u0010w\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\u0012\u0010x\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020#0y2\u0006\u0010r\u001a\u00020\u0006H\u0002J\u0018\u0010z\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\u0006\u0010t\u001a\u00020\u0006J\u001e\u0010{\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\f\u0010|\u001a\b\u0018\u00010}R\u00020~J7\u0010\u007f\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062%\u0010\u0080\u0001\u001a \u0012\b\u0012\u00060}R\u00020~\u0018\u00010\u0081\u0001j\u000f\u0012\b\u0012\u00060}R\u00020~\u0018\u0001`\u0082\u0001J\u001f\u0010\u0083\u0001\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u00062\f\u0010|\u001a\b\u0018\u00010}R\u00020~J\u0011\u0010\u0084\u0001\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u0006J\u001e\u0010\u0085\u0001\u001a\u0004\u0018\u00010\u00062\b\u00109\u001a\u0004\u0018\u00010\u00062\t\u0010\u0086\u0001\u001a\u0004\u0018\u00010oJ\u001c\u0010\u0087\u0001\u001a\u00020#2\b\u00109\u001a\u0004\u0018\u00010\u00062\t\u0010\u0088\u0001\u001a\u0004\u0018\u00010\u0006J\u0011\u0010\u0089\u0001\u001a\u00020;2\b\u00109\u001a\u0004\u0018\u00010\u0006J$\u0010\u008a\u0001\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000b2\t\u0010\u008b\u0001\u001a\u0004\u0018\u00010\u00062\n\u0010\u008c\u0001\u001a\u0005\u0018\u00010\u008d\u0001J/\u0010\u008a\u0001\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000b2\t\u0010\u008b\u0001\u001a\u0004\u0018\u00010\u00062\u0015\u0010\u008e\u0001\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020!\u0018\u00010\u001fJ\u0007\u0010\u008f\u0001\u001a\u00020;J\u0012\u0010\u0090\u0001\u001a\u00020;2\t\u0010\u0091\u0001\u001a\u0004\u0018\u00010\u0006J\u001a\u0010\u0092\u0001\u001a\b\u0012\u0004\u0012\u00020\u00060\u000b2\t\u0010\u0093\u0001\u001a\u0004\u0018\u00010\u0006H\u0002J\t\u0010\u0094\u0001\u001a\u00020;H\u0002J\t\u0010\u0095\u0001\u001a\u00020;H\u0002J\t\u0010\u0096\u0001\u001a\u00020#H\u0002J\u0011\u0010\u0097\u0001\u001a\u00020;2\u0006\u00109\u001a\u00020\u0006H\u0002J\u0011\u0010\u0098\u0001\u001a\u00020;2\u0006\u00109\u001a\u00020\u0006H\u0002J\t\u0010\u0099\u0001\u001a\u00020;H\u0002J>\u0010\u009a\u0001\u001a\u0005\u0018\u0001H\u009b\u0001\"\u0005\b\u0000\u0010\u009b\u00012\u0006\u0010t\u001a\u00020\u00062\n\u0010\u009c\u0001\u001a\u0005\u0018\u0001H\u009b\u00012\u000f\u0010\u009d\u0001\u001a\n\u0012\u0005\u0012\u0003H\u009b\u00010\u009e\u0001H\u0002¢\u0006\u0003\u0010\u009f\u0001J(\u0010 \u0001\u001a\u0005\u0018\u00010¡\u00012\u0007\u0010¢\u0001\u001a\u00020\u00062\u0007\u0010£\u0001\u001a\u00020\u00172\b\u0010¤\u0001\u001a\u00030¡\u0001H\u0016J4\u0010¥\u0001\u001a\u00030¦\u00012\u0007\u0010¢\u0001\u001a\u00020\u00062\u0007\u0010£\u0001\u001a\u00020\u00172\n\u0010§\u0001\u001a\u0005\u0018\u00010¡\u00012\n\u0010¨\u0001\u001a\u0005\u0018\u00010©\u0001H\u0016J(\u0010ª\u0001\u001a\u0005\u0018\u00010«\u00012\u0007\u0010¬\u0001\u001a\u00020\u00062\u0007\u0010£\u0001\u001a\u00020\u00172\b\u0010\u00ad\u0001\u001a\u00030«\u0001H\u0016J(\u0010®\u0001\u001a\u0005\u0018\u00010¯\u00012\u0007\u0010°\u0001\u001a\u00020\u00062\u0007\u0010£\u0001\u001a\u00020\u00172\b\u0010±\u0001\u001a\u00030¯\u0001H\u0016J\u001e\u0010²\u0001\u001a\u0005\u0018\u00010³\u00012\u0007\u0010£\u0001\u001a\u00020\u00172\u0007\u0010]\u001a\u00030³\u0001H\u0016R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR#\u0010\n\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\tR\u001a\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u000f0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0010\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00110\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0012\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u00130\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0015\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u000b0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00170\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u00190\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R \u0010\u001e\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u000b0\u001fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020!X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020#X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010%\u001a\u00020&X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010+\u001a\u00020,X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b-\u0010.\"\u0004\b/\u00100R\u0011\u00101\u001a\u000202¢\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u000e\u00105\u001a\u000206X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006º\u0001"}, d2 = {"Lcom/exteragram/messenger/plugins/PluginsController;", "Lcom/exteragram/messenger/plugins/hooks/PluginsHooks;", "<init>", "()V", "plugins", "Ljava/util/concurrent/ConcurrentHashMap;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/Plugin;", "getPlugins", "()Ljava/util/concurrent/ConcurrentHashMap;", "settings", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/models/SettingItem;", "getSettings", "menuItemsById", "Lcom/exteragram/messenger/plugins/hooks/MenuItemRecord;", "menuItemsByMenuType", "Ljava/util/concurrent/CopyOnWriteArrayList;", "hooks", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/hooks/HookRecord;", "interestedPluginsCache", "fileIconIdsByExtension", _UrlKt.FRAGMENT_ENCODE_SET, "fileIconDrawablesById", "Landroid/graphics/drawable/Drawable;", "nextFileIconId", "Ljava/util/concurrent/atomic/AtomicInteger;", "substringMatchEventHooksCache", "Lcom/exteragram/messenger/plugins/hooks/EventHookRecord;", "exactMatchEventHooksCache", _UrlKt.FRAGMENT_ENCODE_SET, "hooksCacheLock", _UrlKt.FRAGMENT_ENCODE_SET, "hooksCacheDirty", _UrlKt.FRAGMENT_ENCODE_SET, "initialized", "pluginsDir", "Ljava/io/File;", "getPluginsDir", "()Ljava/io/File;", "setPluginsDir", "(Ljava/io/File;)V", "preferences", "Landroid/content/SharedPreferences;", "getPreferences", "()Landroid/content/SharedPreferences;", "setPreferences", "(Landroid/content/SharedPreferences;)V", "watchdog", "Lcom/exteragram/messenger/plugins/utils/PluginsWatchdog;", "getWatchdog", "()Lcom/exteragram/messenger/plugins/utils/PluginsWatchdog;", "updateNotificationRunnable", "Ljava/lang/Runnable;", "getPluginEngine", "Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", "pluginId", "init", _UrlKt.FRAGMENT_ENCODE_SET, "onDone", "startWithSafeMode", "checkDevServers", "shutdown", "restart", "isInitialized", "isPluginActive", "isPluginActive$TMessagesProj", "plugin", "registerFileIconInternal", "extension", "drawable", "unregisterFileIconInternal", "getFileIconIdInternal", "fileName", "clearFileIconsInternal", "normalizeFileExtension", "getPluginSettingsList", "setPluginEnabled", "enabled", Callback.METHOD_NAME, "Lorg/telegram/messenger/Utilities$Callback;", "deletePlugin", "cleanupPlugin", "getPluginPath", "id", "showInstallDialog", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "messageObject", "Lorg/telegram/messenger/MessageObject;", "filePath", "trusted", "params", "Lcom/exteragram/messenger/plugins/ui/components/InstallPluginBottomSheet$PluginInstallParams;", "loadPluginSettings", "hasPluginSettings", "invalidatePluginSettings", "clearPluginSettingsPreferences", "clearEnabledState", "getPluginSettingsPreferences", "hasPluginSettingsPreferences", "getPluginSettingBoolean", "key", "defaultValue", "getPluginSettingString", "getPluginSettingInt", "setPluginSetting", "value", "setPluginSettingAndTriggerOnChange", "onChangeCallback", "Lcom/chaquo/python/PyObject;", "addHook", "newHook", "logMessage", "addEventHook", "hookName", "matchSubstring", "priority", "removeHook", "filter", "Lkotlin/Function1;", "removeEventHook", "addXposedHook", "unhook", "Lde/robv/android/xposed/XC_MethodHook$Unhook;", "Lde/robv/android/xposed/XC_MethodHook;", "addXposedHooks", "unhooks", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "removeXposedHook", "removeHooksByPluginId", "addMenuItem", "pyMenuItemData", "removeMenuItem", "itemId", "removeMenuItemsByPluginId", "getMenuItemsForLocation", "menuType", "builder", "Lcom/exteragram/messenger/plugins/utils/MenuContextBuilder;", "contextData", "notifyPluginsChanged", "executeOnAppEvent", "eventType", "getInterestedPluginIds", "eventName", "rebuildHooksCacheIfNeeded", "ensurePreferences", "isOnPluginsQueueThread", "notifyPluginSettingsRegistered", "notifyPluginSettingsUnregistered", "notifyMenuItemsUpdated", "executeGenericHook", "T", "initialObject", "caller", "Lcom/exteragram/messenger/plugins/PluginsController$EngineHookCaller;", "(Ljava/lang/String;Ljava/lang/Object;Lcom/exteragram/messenger/plugins/PluginsController$EngineHookCaller;)Ljava/lang/Object;", "executePreRequestHook", "Lorg/telegram/tgnet/TLObject;", "requestName", "account", "request", "executePostRequestHook", "Lcom/exteragram/messenger/plugins/hooks/PluginsHooks$PostRequestResult;", "response", "", "Lorg/telegram/tgnet/TLRPC$TL_error;", "executeUpdateHook", "Lorg/telegram/tgnet/TLRPC$Update;", "updateName", "update", "executeUpdatesHook", "Lorg/telegram/tgnet/TLRPC$Updates;", "containerName", "updates", "executeSendMessageHook", "Lorg/telegram/messenger/SendMessagesHelper$SendMessageParams;", "EngineHookCaller", "HookResult", "PluginValidationResult", "PluginsEngine", "SingletonHolder", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
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
    private final Set<DownloadController.FileDownloadProgressListener> downloadProgressListeners = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Runnable updateNotificationRunnable;
    private final PluginsWatchdog watchdog;
    public static final String PREF_PLUGIN_ENABLED_KEY_PREFIX = "plugin_enabled_";

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final ConcurrentHashMap<String, PluginsEngine> enginesMap = new ConcurrentHashMap<>(MapsKt.mapOf(TuplesKt.to("python", new PythonPluginsEngine())));

    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\bâ\u0080\u0001\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002J-\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00018\u00002\u0006\u0010\b\u001a\u00020\tH&¢\u0006\u0002\u0010\n¨\u0006\u000bÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/PluginsController$EngineHookCaller;", "T", _UrlKt.FRAGMENT_ENCODE_SET, "call", "Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "engine", "Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", "obj", "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "(Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;Ljava/lang/Object;Ljava/lang/String;)Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface EngineHookCaller<T> {
        HookResult<T> call(PluginsEngine engine, T obj, String pluginId);
    }

    @Metadata(d1 = {"\u0000\u009c\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H&J\b\u0010\b\u001a\u00020\u0003H&J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH&J\b\u0010\r\u001a\u00020\nH&J\u0010\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH&J(\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00032\u000e\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0013H&J \u0010\u0014\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u000e\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0013H&J\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0016\u001a\u00020\u0011H&J\b\u0010\u0017\u001a\u00020\u0003H&J\u0010\u0010\u0018\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u0011H&J\u0010\u0010\u0019\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u0011H&J\u0018\u0010\u001a\u001a\n\u0012\u0004\u0012\u00020\u001c\u0018\u00010\u001b2\u0006\u0010\u0016\u001a\u00020\u0011H&J$\u0010\u001d\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\u00112\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H&J\"\u0010 \u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\u00112\b\u0010!\u001a\u0004\u0018\u00010\u0001H&J\u0010\u0010\"\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u0011H&J\u001c\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0002\b\u0003\u0018\u00010$2\u0006\u0010\u0010\u001a\u00020\u0011H&J\u0010\u0010%\u001a\u00020\n2\u0006\u0010&\u001a\u00020\u0011H&J0\u0010'\u001a\b\u0012\u0004\u0012\u00020)0(2\u0006\u0010*\u001a\u00020\u00112\u0006\u0010+\u001a\u00020,2\b\u0010-\u001a\u0004\u0018\u00010)2\u0006\u0010\u0010\u001a\u00020\u0011H&J:\u0010.\u001a\b\u0012\u0004\u0012\u00020/0(2\u0006\u0010*\u001a\u00020\u00112\u0006\u0010+\u001a\u00020,2\b\u00100\u001a\u0004\u0018\u00010)2\b\u00101\u001a\u0004\u0018\u0001022\u0006\u0010\u0010\u001a\u00020\u0011H&J0\u00103\u001a\b\u0012\u0004\u0012\u0002040(2\u0006\u00105\u001a\u00020\u00112\u0006\u0010+\u001a\u00020,2\b\u00106\u001a\u0004\u0018\u0001042\u0006\u0010\u0010\u001a\u00020\u0011H&J0\u00107\u001a\b\u0012\u0004\u0012\u0002080(2\u0006\u00109\u001a\u00020\u00112\u0006\u0010+\u001a\u00020,2\b\u0010:\u001a\u0004\u0018\u0001082\u0006\u0010\u0010\u001a\u00020\u0011H&J(\u0010;\u001a\b\u0012\u0004\u0012\u00020<0(2\u0006\u0010+\u001a\u00020,2\b\u0010=\u001a\u0004\u0018\u00010<2\u0006\u0010\u0010\u001a\u00020\u0011H&J\u0018\u0010>\u001a\u00020\n2\u0006\u0010?\u001a\u00020@2\u0006\u0010=\u001a\u00020AH&J\u0018\u0010B\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010?\u001a\u00020@H&J\u0018\u0010B\u001a\u00020\n2\u0006\u0010C\u001a\u00020D2\u0006\u0010?\u001a\u00020@H&J \u0010E\u001a\u00020\n2\u0006\u0010C\u001a\u00020D2\u0006\u0010F\u001a\u00020\u00112\u0006\u0010?\u001a\u00020@H&J \u0010E\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010F\u001a\u00020\u00112\u0006\u0010?\u001a\u00020@H&¨\u0006GÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", _UrlKt.FRAGMENT_ENCODE_SET, "isPlugin", _UrlKt.FRAGMENT_ENCODE_SET, "file", "Ljava/io/File;", "messageObject", "Lorg/telegram/messenger/MessageObject;", "isEngineAvailable", "init", _UrlKt.FRAGMENT_ENCODE_SET, Callback.METHOD_NAME, "Ljava/lang/Runnable;", "checkDevServer", "shutdown", "setPluginEnabled", "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "enabled", "Lorg/telegram/messenger/Utilities$Callback;", "deletePlugin", "getPluginPath", "id", "canOpenInExternalApp", "openInExternalApp", "sharePlugin", "loadPluginSettings", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/models/SettingItem;", "getPluginSetting", "key", "defaultValue", "setPluginSetting", "value", "clearPluginSettings", "getAllPluginSettings", _UrlKt.FRAGMENT_ENCODE_SET, "executeOnAppEvent", "eventType", "executePreRequestHook", "Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "Lorg/telegram/tgnet/TLObject;", "requestName", "account", _UrlKt.FRAGMENT_ENCODE_SET, "request", "executePostRequestHook", "Lcom/exteragram/messenger/plugins/hooks/PluginsHooks$PostRequestResult;", "response", "", "Lorg/telegram/tgnet/TLRPC$TL_error;", "executeUpdateHook", "Lorg/telegram/tgnet/TLRPC$Update;", "updateName", "update", "executeUpdatesHook", "Lorg/telegram/tgnet/TLRPC$Updates;", "containerName", "updates", "executeSendMessageHook", "Lorg/telegram/messenger/SendMessagesHelper$SendMessageParams;", "params", "showInstallDialog", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "Lcom/exteragram/messenger/plugins/ui/components/InstallPluginBottomSheet$PluginInstallParams;", "openPluginSettings", "plugin", "Lcom/exteragram/messenger/plugins/Plugin;", "openPluginSetting", "linkAlias", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
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

    public /* synthetic */ PluginsController(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    @JvmStatic
    public static final void applyArtOpts() {
        INSTANCE.applyArtOpts();
    }

    @JvmStatic
    public static final void clearFileIcons() {
        INSTANCE.clearFileIcons();
    }

    @JvmStatic
    public static final ConcurrentHashMap<String, PluginsEngine> getEngines() {
        return INSTANCE.getEngines();
    }

    @JvmStatic
    public static final int getFileIconId(String str) {
        return INSTANCE.getFileIconId(str);
    }

    @JvmStatic
    public static final PluginsController getInstance() {
        return INSTANCE.getInstance();
    }

    @JvmStatic
    public static final PluginsEngine getPluginEngine(File file) {
        return INSTANCE.getPluginEngine(file);
    }

    @JvmStatic
    public static final Drawable getPluginFileIconDrawable(int i) {
        return INSTANCE.getPluginFileIconDrawable(i);
    }

    @JvmStatic
    public static final boolean isPlugin(File file, MessageObject messageObject) {
        return INSTANCE.isPlugin(file, messageObject);
    }

    @JvmStatic
    public static final boolean isPlugin(MessageObject messageObject) {
        return INSTANCE.isPlugin(messageObject);
    }

    @JvmStatic
    public static final boolean isPluginEngineAvailable() {
        return INSTANCE.isPluginEngineAvailable();
    }

    @JvmStatic
    public static final boolean isPluginEngineSupported() {
        return INSTANCE.isPluginEngineSupported();
    }

    @JvmStatic
    public static final boolean isPluginFileIcon(int i) {
        return INSTANCE.isPluginFileIcon(i);
    }

    @JvmStatic
    public static final boolean isPluginPinned(String str) {
        return INSTANCE.isPluginPinned(str);
    }

    @JvmStatic
    public static final void openPluginSettings(String str) {
        INSTANCE.openPluginSettings(str);
    }

    @JvmStatic
    public static final void openPluginSettings(String str, String str2) {
        INSTANCE.openPluginSettings(str, str2);
    }

    @JvmStatic
    public static final int registerFileIcon(String str, Drawable drawable) {
        return INSTANCE.registerFileIcon(str, drawable);
    }

    @JvmStatic
    public static final void runOnPluginsQueue(Runnable runnable) {
        INSTANCE.runOnPluginsQueue(runnable);
    }

    @JvmStatic
    public static final void setPluginPinned(String str, boolean z) {
        INSTANCE.setPluginPinned(str, z);
    }

    @JvmStatic
    public static final void unregisterFileIcon(String str) {
        INSTANCE.unregisterFileIcon(str);
    }

    @JvmOverloads
    public final void clearPluginSettingsPreferences(String str) {
        clearPluginSettingsPreferences$default(this, str, false, 2, null);
    }

    private PluginsController() {
        this.plugins = new ConcurrentHashMap<>();
        this.settings = new ConcurrentHashMap<>();
        this.menuItemsById = new ConcurrentHashMap<>();
        this.menuItemsByMenuType = new ConcurrentHashMap<>();
        this.hooks = new ConcurrentHashMap<>();
        this.interestedPluginsCache = new ConcurrentHashMap<>();
        this.fileIconIdsByExtension = new ConcurrentHashMap<>();
        this.fileIconDrawablesById = new ConcurrentHashMap<>();
        this.nextFileIconId = new AtomicInteger(101);
        this.substringMatchEventHooksCache = CollectionsKt.emptyList();
        this.exactMatchEventHooksCache = MapsKt.emptyMap();
        this.hooksCacheLock = new Object();
        this.hooksCacheDirty = true;
        this.pluginsDir = new File(ApplicationLoader.getFilesDirFixed(), "plugins");
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("plugin_settings", 0);
        this.preferences = sharedPreferences;
        this.watchdog = new PluginsWatchdog(this);
        this.updateNotificationRunnable = new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                PluginsController.$r8$lambda$TRI271OAbh24WtM0hRmdWUfkDcA();
            }
        };
    }

    public final ConcurrentHashMap<String, Plugin> getPlugins() {
        return this.plugins;
    }

    public final ConcurrentHashMap<String, List<SettingItem>> getSettings() {
        return this.settings;
    }

    public final File getPluginsDir() {
        return this.pluginsDir;
    }

    public final void setPluginsDir(File file) {
        this.pluginsDir = file;
    }

    public final SharedPreferences getPreferences() {
        return this.preferences;
    }

    public final void setPreferences(SharedPreferences sharedPreferences) {
        this.preferences = sharedPreferences;
    }

    public final PluginsWatchdog getWatchdog() {
        return this.watchdog;
    }

    public static void $r8$lambda$TRI271OAbh24WtM0hRmdWUfkDcA() {
        NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pluginsUpdated, new Object[0]);
        NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pluginMenuItemsUpdated, new Object[0]);
    }

    public final PluginsEngine getPluginEngine(String pluginId) {
        Plugin plugin;
        if (pluginId == null || pluginId.length() == 0 || (plugin = this.plugins.get(pluginId)) == null) {
            return null;
        }
        PluginsEngine cachedEngine = plugin.getCachedEngine();
        if (cachedEngine != null) {
            return cachedEngine;
        }
        String engine = plugin.getEngine();
        if (engine == null) {
            return null;
        }
        PluginsEngine pluginsEngine = INSTANCE.getEngines().get(engine);
        if (pluginsEngine != null) {
            plugin.setCachedEngine(pluginsEngine);
        }
        return pluginsEngine;
    }

    public final void init() {
        init(false, null);
    }

    public final void init(Runnable onDone) {
        init(false, onDone);
    }

    public final void init(boolean startWithSafeMode) {
        init(startWithSafeMode, null);
    }

    public final void init(boolean startWithSafeMode, final Runnable onDone) {
        Companion companion = INSTANCE;
        if (!companion.isPluginEngineSupported() || !ExteraConfig.getPluginsEngine()) {
            this.initialized = false;
            if (onDone != null) {
                onDone.run();
                return;
            }
            return;
        }
        NativeCrashHandler.checkAndHandleNativeCrash();
        this.watchdog.start();
        companion.runOnPluginsQueue(new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PluginsController.$r8$lambda$ToGKbtqH63z4zUzeWzrnKViKrbk();
            }
        });
        ensurePreferences();
        try {
            boolean z = this.preferences.getBoolean("had_crash", false);
            String string = this.preferences.getString("crashed_plugin_id", null);
            boolean z2 = (string != null && Intrinsics.areEqual(string, "manual!")) || startWithSafeMode;
            this.preferences.edit().remove("had_crash").remove("crashed_plugin_id").apply();
            if (z) {
                if (string != null && !z2) {
                    SharedPreferences.Editor editorEdit = this.preferences.edit();
                    editorEdit.putBoolean("plugin_enabled_" + string, false);
                    editorEdit.apply();
                } else {
                    SharedPreferences.Editor editor = ExteraConfig.getEditor();
                    String string2 = "pluginsSafeMode";
                    ExteraConfig.setPluginsSafeMode(true);
                    Unit unit = Unit.INSTANCE;
                    editor.putBoolean(string2, true).apply();
                }
                if (!z2) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            PluginsController.$r8$lambda$CIw6dHotoGkENlaplpdVEVp8v_U();
                        }
                    }, 800L);
                }
            } else {
                SharedPreferences.Editor editor2 = ExteraConfig.getEditor();
                String string3 = "pluginsSafeMode";
                ExteraConfig.setPluginsSafeMode(startWithSafeMode);
                Unit unit2 = Unit.INSTANCE;
                editor2.putBoolean(string3, startWithSafeMode).apply();
            }
        } catch (Exception unused) {
        }
        File file = new File(ApplicationLoader.getFilesDirFixed(), "plugins");
        this.pluginsDir = file;
        if (!file.exists()) {
            SimpliFiles.directory(this.pluginsDir).create();
        }
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final Runnable runnable = new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                PluginsController.$r8$lambda$6Rk2iqGONVXohJ42e6LwpSycLiM(atomicInteger, PluginsController.this, onDone);
            }
        };
        companion.runOnPluginsQueue(new Runnable() {
            @Override
            public final void run() {
                for (PluginsEngine pluginsEngine : INSTANCE.getEngines().values()) {
                    try {
                        pluginsEngine.init(runnable);
                    } catch (Throwable th) {
                        FileLog.e("Failed to initialize plugin engine", th);
                        runnable.run();
                    }
                }
            }
        });
    }

    public static void $r8$lambda$CIw6dHotoGkENlaplpdVEVp8v_U() {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment != null) {
            new SafeModeBottomSheet(lastFragment).show();
        }
    }

    public static void $r8$lambda$6Rk2iqGONVXohJ42e6LwpSycLiM(AtomicInteger atomicInteger, PluginsController pluginsController, Runnable runnable) {
        if (atomicInteger.incrementAndGet() >= INSTANCE.getEngines().size()) {
            pluginsController.initialized = true;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public final void checkDevServers() {
        for (PluginsEngine pluginsEngine : INSTANCE.getEngines().values()) {
            pluginsEngine.checkDevServer();
        }
    }

    public final void shutdown(final Runnable onDone) {
        if (this.initialized) {
            INSTANCE.runOnPluginsQueue(new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    PluginsController.$r8$lambda$icNspkMlrUGholajStQgzMU2ll4(PluginsController.this, onDone);
                }
            });
        } else if (onDone != null) {
            onDone.run();
        }
    }

    public static void $r8$lambda$icNspkMlrUGholajStQgzMU2ll4(final PluginsController pluginsController, final Runnable runnable) {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        Runnable runnable2 = new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                PluginsController.shutdown$lambda$0$0(atomicInteger, pluginsController, runnable);
            }
        };
        for (PluginsEngine pluginsEngine : INSTANCE.getEngines().values()) {
            pluginsEngine.shutdown(runnable2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void shutdown$lambda$0$0(AtomicInteger atomicInteger, PluginsController pluginsController, Runnable runnable) {
        if (atomicInteger.incrementAndGet() >= INSTANCE.getEngines().size()) {
            pluginsController.watchdog.stop();
            pluginsController.plugins.clear();
            pluginsController.settings.clear();
            pluginsController.initialized = false;
            FileLog.d("Plugin system shut down.");
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public final void restart() {
        restart(ExteraConfig.getPluginsSafeMode());
    }

    public final void restart(final boolean startWithSafeMode) {
        FileLog.d("Restarting plugins engine...");
        shutdown(new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                PluginsController.$r8$lambda$cBNgrqfMkZ1EABZ_fGA8dXDZl_c(PluginsController.this, startWithSafeMode);
            }
        });
    }

    public static void $r8$lambda$cBNgrqfMkZ1EABZ_fGA8dXDZl_c(PluginsController pluginsController, boolean z) {
        if (ExteraConfig.getPluginsEngine()) {
            pluginsController.init(z, new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    PluginsController.restart$lambda$0$0();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void restart$lambda$0$0() {
        FileLog.d("Plugins engine restarted.");
    }

    public final boolean isInitialized() {
        return this.initialized;
    }

    /* JADX INFO: renamed from: isInitialized, reason: from getter */
    public final boolean getInitialized() {
        return this.initialized;
    }

    public final boolean isPluginActive$TMessagesProj(String pluginId) {
        Plugin plugin;
        return (pluginId == null || pluginId.length() == 0 || (plugin = this.plugins.get(pluginId)) == null || !plugin.isEnabled()) ? false : true;
    }

    public final boolean isPluginActive$TMessagesProj(Plugin plugin) {
        return plugin != null && this.plugins.get(plugin.getId()) == plugin && plugin.isEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int registerFileIconInternal(String extension, Drawable drawable) {
        String strNormalizeFileExtension = normalizeFileExtension(extension);
        if (strNormalizeFileExtension == null || drawable == null) {
            return -1;
        }
        Integer num = this.fileIconIdsByExtension.get(strNormalizeFileExtension);
        if (num != null) {
            this.fileIconDrawablesById.put(num, drawable);
            return num.intValue();
        }
        int andIncrement = this.nextFileIconId.getAndIncrement();
        Integer numPutIfAbsent = this.fileIconIdsByExtension.putIfAbsent(strNormalizeFileExtension, Integer.valueOf(andIncrement));
        if (numPutIfAbsent != null) {
            andIncrement = numPutIfAbsent.intValue();
        }
        this.fileIconDrawablesById.put(Integer.valueOf(andIncrement), drawable);
        return andIncrement;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void unregisterFileIconInternal(String extension) {
        Integer numRemove;
        String strNormalizeFileExtension = normalizeFileExtension(extension);
        if (strNormalizeFileExtension == null || (numRemove = this.fileIconIdsByExtension.remove(strNormalizeFileExtension)) == null) {
            return;
        }
        this.fileIconDrawablesById.remove(numRemove);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int getFileIconIdInternal(String fileName) {
        String strSubstring;
        Integer num;
        if (fileName != null && fileName.length() != 0 && !this.fileIconIdsByExtension.isEmpty()) {
            int iLastIndexOf = fileName.lastIndexOf('.');
            if (iLastIndexOf >= 0) {
                strSubstring = fileName.substring(iLastIndexOf + 1);
            } else {
                strSubstring = fileName;
            }
            String strNormalizeFileExtension = normalizeFileExtension(strSubstring);
            if (strNormalizeFileExtension != null && (num = this.fileIconIdsByExtension.get(strNormalizeFileExtension)) != null) {
                return num.intValue();
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void clearFileIconsInternal() {
        this.fileIconIdsByExtension.clear();
        this.fileIconDrawablesById.clear();
    }

    private final String normalizeFileExtension(String extension) {
        if (extension == null || extension.length() == 0) {
            return null;
        }
        Locale locale = Locale.ROOT;
        String lowerCase = extension.toLowerCase(locale);
        return lowerCase;
    }

    public final List<SettingItem> getPluginSettingsList(String pluginId) {
        if (pluginId == null || pluginId.length() == 0) {
            return null;
        }
        return this.settings.get(pluginId);
    }

    public final void setPluginEnabled(final String pluginId, final boolean enabled, final Utilities.Callback<String> callback) {
        INSTANCE.runOnPluginsQueue(new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                PluginsController.$r8$lambda$hS703SyhRNV9SkJH3iBjUMIVIvE(PluginsController.this, pluginId, enabled, callback);
            }
        });
    }

    public static void $r8$lambda$hS703SyhRNV9SkJH3iBjUMIVIvE(PluginsController pluginsController, String str, boolean z, Utilities.Callback callback) {
        PluginsEngine pluginEngine = pluginsController.getPluginEngine(str);
        if (pluginEngine == null || str == null) {
            return;
        }
        pluginEngine.setPluginEnabled(str, z, callback);
        pluginsController.interestedPluginsCache.clear();
    }

    public final void deletePlugin(final String pluginId, final Utilities.Callback<String> callback) {
        INSTANCE.runOnPluginsQueue(new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                PluginsController.$r8$lambda$bH2Xuh4PqqRQxmZWhj67yYxRafE(PluginsController.this, pluginId, callback);
            }
        });
    }

    public static void $r8$lambda$bH2Xuh4PqqRQxmZWhj67yYxRafE(PluginsController pluginsController, String str, Utilities.Callback callback) {
        PluginsEngine pluginEngine = pluginsController.getPluginEngine(str);
        if (pluginEngine == null || str == null) {
            return;
        }
        pluginEngine.deletePlugin(str, callback);
    }

    public final void cleanupPlugin(String pluginId) {
        removeHooksByPluginId(pluginId);
        invalidatePluginSettings(pluginId);
        removeMenuItemsByPluginId(pluginId);
    }

    public final String getPluginPath(String id) {
        PluginsEngine pluginEngine;
        if (id == null || id.length() == 0 || (pluginEngine = getPluginEngine(id)) == null) {
            return null;
        }
        return pluginEngine.getPluginPath(id);
    }

    public final void showInstallDialog(final BaseFragment fragment, final MessageObject messageObject) {
        if (messageObject == null) {
            android.util.Log.e("PLUGIN_INSTALL", "showInstallDialog: messageObject is null");
            return;
        }
        final InstallPluginBottomSheet.PluginInstallParams params = InstallPluginBottomSheet.PluginInstallParams.INSTANCE.of(messageObject);
        String filePath = params.getFilePath();
        android.util.Log.e("PLUGIN_INSTALL", "showInstallDialog: params.filePath=" + filePath + ", exists=" + (filePath != null && new File(filePath).exists()));
        if (filePath != null && filePath.length() > 0 && new File(filePath).exists()) {
            showInstallDialog(fragment, params);
            return;
        }
        final TLRPC.Document document = messageObject.getDocument();
        if (document == null) {
            return;
        }
        final FileLoader fileLoader = FileLoader.getInstance(messageObject.currentAccount);
        File attachFile = fileLoader.getPathToAttach(document, true);
        if (attachFile != null && attachFile.exists()) {
            params.setFilePath(attachFile.getAbsolutePath());
            showInstallDialog(fragment, params);
            return;
        }
        File docFile = fileLoader.getPathToAttach(document, false);
        if (docFile != null && docFile.exists()) {
            params.setFilePath(docFile.getAbsolutePath());
            showInstallDialog(fragment, params);
            return;
        }
        File messageFile = fileLoader.getPathToMessage(messageObject.messageOwner);
        if (messageFile != null && messageFile.exists()) {
            params.setFilePath(messageFile.getAbsolutePath());
            showInstallDialog(fragment, params);
            return;
        }

        final DownloadController downloadController = DownloadController.getInstance(messageObject.currentAccount);
        final String attachFileName = FileLoader.getAttachFileName(document);
        final int observerTag = downloadController.generateObserverTag();
        final boolean trusted = params.getTrusted();

        BulletinFactory.of(fragment).createSimpleBulletin(R.raw.ic_download, LocaleController.getString(R.string.Downloading)).show();

        final DownloadController.FileDownloadProgressListener listener = new DownloadController.FileDownloadProgressListener() {
            @Override
            public void onFailedDownload(String fileName, boolean canceled) {
                downloadProgressListeners.remove(this);
                downloadController.removeLoadingFileObserver(this);
            }

            @Override
            public void onSuccessDownload(String fileName) {
                downloadProgressListeners.remove(this);
                downloadController.removeLoadingFileObserver(this);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        String downloadedPath = ChatUtils.getInstance().getPathToMessage(messageObject);
                        if (downloadedPath == null || downloadedPath.length() == 0) {
                            File f = fileLoader.getPathToAttach(document, false);
                            if (f != null && f.exists()) {
                                downloadedPath = f.getAbsolutePath();
                            } else {
                                f = fileLoader.getPathToAttach(document, true);
                                if (f != null && f.exists()) {
                                    downloadedPath = f.getAbsolutePath();
                                }
                            }
                        }
                        if (downloadedPath != null && downloadedPath.length() > 0) {
                            showInstallDialog(fragment, downloadedPath, trusted);
                        }
                    }
                });
            }

            @Override
            public void onProgressDownload(String fileName, long downloadSize, long totalSize) {}

            @Override
            public void onProgressUpload(String fileName, long downloadSize, long totalSize, boolean isEncrypted) {}

            @Override
            public int getObserverTag() {
                return observerTag;
            }
        };

        downloadProgressListeners.add(listener);
        downloadController.addLoadingFileObserver(attachFileName, messageObject, listener);
        fileLoader.loadFile(document, messageObject, FileLoader.PRIORITY_NORMAL_UP, 0);
    }

    public final void showInstallDialog(BaseFragment fragment, String filePath, boolean trusted) {
        if (filePath == null || filePath.length() == 0) {
            return;
        }
        showInstallDialog(fragment, new InstallPluginBottomSheet.PluginInstallParams(filePath, trusted));
    }

    private final void showInstallDialog(final BaseFragment fragment, InstallPluginBottomSheet.PluginInstallParams params) {
        android.util.Log.e("PLUGIN_INSTALL", "showInstallDialog internal: fragment=" + fragment + ", activityRunning=" + (fragment != null && AndroidUtilities.isActivityRunning(fragment.getParentActivity())) + ", pluginsEngine=" + ExteraConfig.getPluginsEngine());
        if (fragment == null || !AndroidUtilities.isActivityRunning(fragment.getParentActivity())) {
            android.util.Log.e("PLUGIN_INSTALL", "showInstallDialog: activity not running!");
            return;
        }
        String filePath = params != null ? params.getFilePath() : null;
        if (filePath == null || filePath.length() == 0) {
            android.util.Log.e("PLUGIN_INSTALL", "showInstallDialog: filePath empty");
            return;
        }
        File file = new File(params.getFilePath());
        if (!ExteraConfig.getPluginsEngine()) {
            android.util.Log.e("PLUGIN_INSTALL", "showInstallDialog: getPluginsEngine is false!");
            BulletinFactory.of(fragment).createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.PluginNotEnabled, file.getName()), LocaleController.getString(R.string.Enable), 2750, new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    fragment.presentFragment(new PluginsActivity());
                }
            }).show();
            return;
        }
        PluginsEngine pluginEngine = INSTANCE.getPluginEngine(file);
        android.util.Log.e("PLUGIN_INSTALL", "showInstallDialog: pluginEngine=" + pluginEngine);
        if (pluginEngine == null) {
            return;
        }
        pluginEngine.showInstallDialog(fragment, params);
    }

    public final void loadPluginSettings() {
        loadPluginSettings(null);
    }

    public final void loadPluginSettings(final String pluginId) {
        if (pluginId == null || pluginId.length() == 0) {
            for (String str : this.plugins.keySet()) {
                String str2 = str;
                Plugin plugin = this.plugins.get(str2);
                if (isPluginActive$TMessagesProj(plugin)) {
                    loadPluginSettings(str2);
                } else if (plugin != null) {
                    invalidatePluginSettings(str2);
                }
            }
            return;
        }
        Runnable runnable = new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                PluginsController.$r8$lambda$9zgaAPWG05B5QpqSmC1f6oCyG8s(PluginsController.this, pluginId);
            }
        };
        if (isOnPluginsQueueThread()) {
            runnable.run();
        } else {
            INSTANCE.runOnPluginsQueue(runnable);
        }
    }

    public static void $r8$lambda$9zgaAPWG05B5QpqSmC1f6oCyG8s(PluginsController pluginsController, String str) {
        try {
            PluginsEngine pluginEngine = pluginsController.getPluginEngine(str);
            if (pluginEngine == null) {
                return;
            }
            List<SettingItem> listLoadPluginSettings = pluginEngine.loadPluginSettings(str);
            if (listLoadPluginSettings == null) {
                pluginsController.invalidatePluginSettings(str);
                return;
            }
            pluginsController.settings.put(str, listLoadPluginSettings);
            FileLog.d("Registered settings for plugin " + str);
            pluginsController.notifyPluginSettingsRegistered(str);
        } catch (Throwable th) {
            FileLog.e(th);
            pluginsController.invalidatePluginSettings(str);
        }
    }

    public final boolean hasPluginSettings(String pluginId) {
        return (pluginId == null || pluginId.length() == 0 || !this.settings.containsKey(pluginId)) ? false : true;
    }

    public final void invalidatePluginSettings(String pluginId) {
        List<SettingItem> listRemove;
        if (pluginId == null || pluginId.length() == 0 || (listRemove = this.settings.remove(pluginId)) == null) {
            return;
        }
        Iterator<SettingItem> it = listRemove.iterator();
        while (it.hasNext()) {
            it.next().cleanup();
        }
        notifyPluginSettingsUnregistered(pluginId);
    }

    public static /* synthetic */ void clearPluginSettingsPreferences$default(PluginsController pluginsController, String str, boolean z, int i, Object obj) {
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
        final Function1 function1 = new Function1() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda22
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return PluginsController.$r8$lambda$1waTlSZq4_YECP84Ln2JBSIJadc((String) obj);
            }
        };
        Set<HookRecord> setComputeIfAbsent = concurrentHashMap.computeIfAbsent(pluginId, new Function() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda23
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return PluginsController.$r8$lambda$Y2S2tGZb5s_dlwEiuNwPZF2NaoU(function1, obj);
            }
        });
        if (setComputeIfAbsent.add(newHook)) {
            FileLog.d(logMessage);
            this.interestedPluginsCache.clear();
            this.hooksCacheDirty = true;
        }
    }

    public static Set $r8$lambda$1waTlSZq4_YECP84Ln2JBSIJadc(String str) {
        return new CopyOnWriteArraySet();
    }

    public static Set $r8$lambda$Y2S2tGZb5s_dlwEiuNwPZF2NaoU(Function1 function1, Object obj) {
        return (Set) function1.invoke(obj);
    }

    public final void addEventHook(String pluginId, String hookName, boolean matchSubstring, int priority) {
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
        removeHook(pluginId, new Function1() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda21
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(PluginsController.$r8$lambda$m8Lepk5YSi6L1M4adJRbExRUjfI(hookName, (HookRecord) obj));
            }
        }, "Removed event hook(s) matching name '" + hookName + "' for plugin " + pluginId);
    }

    public static boolean $r8$lambda$m8Lepk5YSi6L1M4adJRbExRUjfI(String str, HookRecord hookRecord) {
        return (hookRecord instanceof EventHookRecord) && Intrinsics.areEqual(((EventHookRecord) hookRecord).getHookName(), str);
    }

    public final void addXposedHook(String pluginId, XC_MethodHook.Unhook unhook) {
        addHook(pluginId, unhook != null ? new XposedHookRecord(unhook) : null, "Added Xposed hook for plugin " + pluginId);
    }

    public final void addXposedHooks(String pluginId, ArrayList<XC_MethodHook.Unhook> unhooks) {
        if (unhooks == null) {
            return;
        }
        for (XC_MethodHook.Unhook unhook : unhooks) {
            addXposedHook(pluginId, unhook);
        }
    }

    public final void removeXposedHook(String pluginId, final XC_MethodHook.Unhook unhook) {
        removeHook(pluginId, new Function1() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda9
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(PluginsController.$r8$lambda$nCbAkJU29JwWnZ6UwahK1r3Ab34(unhook, (HookRecord) obj));
            }
        }, "Removed Xposed hook for plugin " + pluginId);
    }

    public static boolean $r8$lambda$nCbAkJU29JwWnZ6UwahK1r3Ab34(XC_MethodHook.Unhook unhook, HookRecord hookRecord) {
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
                final Function2 function2 = new Function2() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda3
                    @Override // kotlin.jvm.functions.Function2
                    public final Object invoke(Object obj, Object obj2) {
                        return PluginsController.m1307$r8$lambda$6XhX7yTKSS7CldZXwz1RN6kOJE(menuItemRecord, (String) obj, (CopyOnWriteArrayList) obj2);
                    }
                };
                concurrentHashMap.compute(menuType, new BiFunction() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda4
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

    /* JADX INFO: renamed from: $r8$lambda$-6XhX7yTKSS7CldZXwz1RN6kOJE, reason: not valid java name */
    public static CopyOnWriteArrayList m1307$r8$lambda$6XhX7yTKSS7CldZXwz1RN6kOJE(final MenuItemRecord menuItemRecord, String str, CopyOnWriteArrayList copyOnWriteArrayList) {
        ArrayList arrayList;
        if (copyOnWriteArrayList == null) {
            arrayList = new ArrayList();
        } else {
            arrayList = new ArrayList(copyOnWriteArrayList);
            CollectionsKt.removeAll((List) arrayList, new Function1() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda6
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return Boolean.valueOf(PluginsController.addMenuItem$lambda$0$0$0(menuItemRecord, (MenuItemRecord) obj));
                }
            });
        }
        arrayList.add(menuItemRecord);
        final Function1 function1 = new Function1() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda7
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Integer.valueOf(((MenuItemRecord) obj).getPriority());
            }
        };
        Comparator comparatorReversed = Comparator.comparingInt(new ToIntFunction() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda8
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return PluginsController.addMenuItem$lambda$0$2(function1, obj);
            }
        }).reversed();
        CollectionsKt.sortWith(arrayList, comparatorReversed);
        return new CopyOnWriteArrayList(arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean addMenuItem$lambda$0$0$0(MenuItemRecord menuItemRecord, MenuItemRecord menuItemRecord2) {
        return Intrinsics.areEqual(menuItemRecord2.getItemId(), menuItemRecord.getItemId());
    }

    /* JADX INFO: Access modifiers changed from: private */
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
            MenuItemRecord menuItemRecord2 = menuItemRecord;
            if (Intrinsics.areEqual(menuItemRecord2.getPluginId(), pluginId)) {
                arrayList.add(menuItemRecord2.getItemId());
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        for (Object obj : arrayList) {
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
            return list2;
        }
        ArrayList arrayList = new ArrayList();
        for (Object obj : linkedHashSet) {
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
                pluginsEngine.executeOnAppEvent(eventType);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v10, types: [java.util.ArrayList] */
    /* JADX WARN: Type inference failed for: r0v11, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r0v12, types: [java.util.List] */
    /* JADX WARN: Type inference failed for: r0v14 */
    /* JADX WARN: Type inference failed for: r0v15 */
    /* JADX WARN: Type inference failed for: r0v16 */
    /* JADX WARN: Type inference failed for: r0v17 */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.util.List<java.lang.String>] */
    /* JADX WARN: Type inference failed for: r5v4 */
    /* JADX WARN: Type inference failed for: r5v7, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference incomplete: some casts might be missing */
    private final List<String> getInterestedPluginIds(String eventName) {
        if (eventName == null || eventName.length() == 0) {
            return Collections.emptyList();
        }
        List<String> list2 = this.interestedPluginsCache.get(eventName);
        if (list2 != null) {
            return list2;
        }
        rebuildHooksCacheIfNeeded();
        HashMap<String, Integer> map = new HashMap<>();
        List<EventHookRecord> list3 = this.exactMatchEventHooksCache.get(eventName);
        if (list3 != null) {
            for (final EventHookRecord eventHookRecord : list3) {
                String pluginId2 = eventHookRecord.getPluginId();
                if (pluginId2 != null) {
                    final Function2 function2 = new Function2() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda30
                        @Override // kotlin.jvm.functions.Function2
                        public final Object invoke(Object obj, Object obj2) {
                            return PluginsController.getInterestedPluginIds$lambda$0$0(eventHookRecord, (String) obj, (Integer) obj2);
                        }
                    };
                    map.compute(pluginId2, new BiFunction() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda31
                        @Override // java.util.function.BiFunction
                        public final Object apply(Object obj, Object obj2) {
                            return PluginsController.getInterestedPluginIds$lambda$0$1(function2, obj, obj2);
                        }
                    });
                }
            }
        }
        for (final EventHookRecord eventHookRecord2 : this.substringMatchEventHooksCache) {
            String pluginId;
            if (eventHookRecord2.matches(eventName) && (pluginId = eventHookRecord2.getPluginId()) != null) {
                final Function2 function3 = new Function2() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda32
                    @Override // kotlin.jvm.functions.Function2
                    public final Object invoke(Object obj, Object obj2) {
                        return PluginsController.m1309$r8$lambda$HfgCYkGwi8KAUz5HS9xUmkl8g(eventHookRecord2, (String) obj, (Integer) obj2);
                    }
                };
                map.compute(pluginId, new BiFunction() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda33
                    @Override // java.util.function.BiFunction
                    public final Object apply(Object obj, Object obj2) {
                        return PluginsController.$r8$lambda$DCTD49SParBUBy24s3SCzJWiKD8(function3, obj, obj2);
                    }
                });
            }
        }
        List<String> resultList;
        if (map.isEmpty()) {
            resultList = Collections.emptyList();
        } else {
            ArrayList<Map.Entry<String, Integer>> arrayList2 = new ArrayList<>(map.entrySet());
            final Function2 function4 = new Function2() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda34
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return Integer.valueOf(PluginsController.m1310$r8$lambda$IAMjXVT_CPUvmW_ONgKDOtGfT4((Map.Entry) obj, (Map.Entry) obj2));
                }
            };
            CollectionsKt.sortWith(arrayList2, new Comparator() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda35
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ((Number) function4.invoke(obj, obj2)).intValue();
                }
            });
            ArrayList<String> activePluginIds = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : arrayList2) {
                if (isPluginActive$TMessagesProj(entry.getKey())) {
                    activePluginIds.add(entry.getKey());
                }
            }
            resultList = activePluginIds;
        }
        this.interestedPluginsCache.put(eventName, resultList);
        if (!resultList.isEmpty()) {
            FileLog.d("Calculated and cached potential plugins for '" + eventName + "': " + resultList);
        }
        return resultList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Integer getInterestedPluginIds$lambda$0$1(Function2 function2, Object obj, Object obj2) {
        return (Integer) function2.invoke(obj, obj2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Integer getInterestedPluginIds$lambda$0$0(EventHookRecord eventHookRecord, String str, Integer num) {
        return Integer.valueOf(num == null ? eventHookRecord.getPriority() : Math.max(num.intValue(), eventHookRecord.getPriority()));
    }

    public static Integer $r8$lambda$DCTD49SParBUBy24s3SCzJWiKD8(Function2 function2, Object obj, Object obj2) {
        return (Integer) function2.invoke(obj, obj2);
    }

    /* JADX INFO: renamed from: $r8$lambda$Hf--gCYkGwi8KAUz5HS9xUmkl8g, reason: not valid java name */
    public static Integer m1309$r8$lambda$HfgCYkGwi8KAUz5HS9xUmkl8g(EventHookRecord eventHookRecord, String str, Integer num) {
        return num == null ? Integer.valueOf(eventHookRecord.getPriority()) : Integer.valueOf(Math.max(num.intValue(), eventHookRecord.getPriority()));
    }

    /* JADX INFO: renamed from: $r8$lambda$IAMjXVT_CPUvmW_ONgKDOt-GfT4, reason: not valid java name */
    public static int m1310$r8$lambda$IAMjXVT_CPUvmW_ONgKDOtGfT4(Map.Entry entry, Map.Entry entry2) {
        int iIntValue = ((Number) entry2.getValue()).intValue();
        Object value = entry.getValue();
        int iCompare = Intrinsics.compare(iIntValue, ((Number) value).intValue());
        if (iCompare != 0) {
            return iCompare;
        }
        String str = (String) entry.getKey();
        Object key = entry2.getKey();
        return str.compareTo((String) key);
    }

    private final void rebuildHooksCacheIfNeeded() {
        if (this.hooksCacheDirty) {
            synchronized (this.hooksCacheLock) {
                try {
                    if (this.hooksCacheDirty) {
                        HashMap map = new HashMap();
                        ArrayList arrayList = new ArrayList();
                        for (Set<HookRecord> set : this.hooks.values()) {
                            for (HookRecord hookRecord : set) {
                                if (hookRecord instanceof EventHookRecord) {
                                    if (((EventHookRecord) hookRecord).getMatchSubstring()) {
                                        arrayList.add(hookRecord);
                                    } else {
                                        String hookName = ((EventHookRecord) hookRecord).getHookName();
                                        if (hookName != null) {
                                            final Function1 function1 = new Function1() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda24
                                                @Override // kotlin.jvm.functions.Function1
                                                public final Object invoke(Object obj) {
                                                    return PluginsController.rebuildHooksCacheIfNeeded$lambda$0$0((String) obj);
                                                }
                                            };
                                            ((List) map.computeIfAbsent(hookName, new Function() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda25
                                                @Override // java.util.function.Function
                                                public final Object apply(Object obj) {
                                                    return PluginsController.rebuildHooksCacheIfNeeded$lambda$0$1(function1, obj);
                                                }
                                            })).add(hookRecord);
                                        }
                                    }
                                }
                            }
                        }
                        this.exactMatchEventHooksCache = map;
                        this.substringMatchEventHooksCache = arrayList;
                        this.hooksCacheDirty = false;
                        Unit unit = Unit.INSTANCE;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List rebuildHooksCacheIfNeeded$lambda$0$0(String str) {
        return new ArrayList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List rebuildHooksCacheIfNeeded$lambda$0$1(Function1 function1, Object obj) {
        return (List) function1.invoke(obj);
    }

    private final void ensurePreferences() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("plugin_settings", 0);
        this.preferences = sharedPreferences;
    }

    private final boolean isOnPluginsQueueThread() {
        return (Utilities.pluginsQueue == null || Utilities.pluginsQueue.getHandler() == null || !Intrinsics.areEqual(Thread.currentThread(), Utilities.pluginsQueue.getHandler().getLooper().getThread())) ? false : true;
    }

    private final void notifyPluginSettingsRegistered(final String pluginId) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pluginSettingsRegistered, pluginId);
            }
        });
    }

    private final void notifyPluginSettingsUnregistered(final String pluginId) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pluginSettingsUnregistered, pluginId);
            }
        });
    }

    private final void notifyMenuItemsUpdated() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pluginMenuItemsUpdated, new Object[0]);
            }
        });
    }

    private final <T> T executeGenericHook(String hookName, T initialObject, EngineHookCaller<T> caller) {
        PluginsEngine pluginEngine;
        if (INSTANCE.isPluginEngineAvailable()) {
            List<String> interestedPluginIds = getInterestedPluginIds(hookName);
            if (!interestedPluginIds.isEmpty()) {
                for (String str : interestedPluginIds) {
                    if (isPluginActive$TMessagesProj(str) && (pluginEngine = getPluginEngine(str)) != null) {
                        this.watchdog.onPluginExecutionStarted(str);
                        try {
                            HookResult<T> hookResultCall = caller.call(pluginEngine, initialObject, str);
                            if (hookResultCall == null) {
                                this.watchdog.onPluginExecutionFinished(str);
                                continue;
                            }
                            T result = hookResultCall.getResult();
                            if (!hookResultCall.getCancel()) {
                                boolean isFinal = hookResultCall.getIsFinal();
                                PluginsWatchdog pluginsWatchdog = this.watchdog;
                                if (isFinal) {
                                    pluginsWatchdog.onPluginExecutionFinished(str);
                                    return result;
                                }
                                pluginsWatchdog.onPluginExecutionFinished(str);
                                initialObject = result;
                            } else {
                                this.watchdog.onPluginExecutionFinished(str);
                                return null;
                            }
                        } catch (Throwable th) {
                            this.watchdog.onPluginExecutionFinished(str);
                            FileLog.e("Failed to execute hook " + hookName + " for plugin " + str, th);
                        }
                    }
                }
                return initialObject;
            }
        }
        return initialObject;
    }

    @Override // com.exteragram.messenger.plugins.hooks.PluginsHooks
    public TLObject executePreRequestHook(final String requestName, final int account, TLObject request) {
        return (TLObject) executeGenericHook(requestName, request, new EngineHookCaller() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda26
            @Override // com.exteragram.messenger.plugins.PluginsController.EngineHookCaller
            public final PluginsController.HookResult call(PluginsController.PluginsEngine pluginsEngine, Object obj, String str) {
                return PluginsController.$r8$lambda$JziAQxL1SNJMVFFBpBtTlnfNOjw(requestName, account, pluginsEngine, (TLObject) obj, str);
            }
        });
    }

    public static HookResult $r8$lambda$JziAQxL1SNJMVFFBpBtTlnfNOjw(String str, int i, PluginsEngine pluginsEngine, TLObject tLObject, String str2) {
        return pluginsEngine.executePreRequestHook(str, i, tLObject, str2);
    }

    @Override // com.exteragram.messenger.plugins.hooks.PluginsHooks
    public PluginsHooks.PostRequestResult executePostRequestHook(final String requestName, final int account, TLObject response, TLRPC.TL_error error) {
        PluginsHooks.PostRequestResult postRequestResult = (PluginsHooks.PostRequestResult) executeGenericHook(requestName, new PluginsHooks.PostRequestResult(response, error), new EngineHookCaller() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda11
            @Override // com.exteragram.messenger.plugins.PluginsController.EngineHookCaller
            public final PluginsController.HookResult call(PluginsController.PluginsEngine pluginsEngine, Object obj, String str) {
                return PluginsController.$r8$lambda$DNVJceqQWqE0LCLQl0WusvtpDkE(requestName, account, pluginsEngine, (PluginsHooks.PostRequestResult) obj, str);
            }
        });
        return postRequestResult == null ? new PluginsHooks.PostRequestResult(response, error) : postRequestResult;
    }

    public static HookResult $r8$lambda$DNVJceqQWqE0LCLQl0WusvtpDkE(String str, int i, PluginsEngine pluginsEngine, PluginsHooks.PostRequestResult postRequestResult, String str2) {
        return pluginsEngine.executePostRequestHook(str, i, postRequestResult != null ? postRequestResult.getResponse() : null, postRequestResult != null ? postRequestResult.getError() : null, str2);
    }

    @Override // com.exteragram.messenger.plugins.hooks.PluginsHooks
    public TLRPC.Update executeUpdateHook(final String updateName, final int account, TLRPC.Update update) {
        return (TLRPC.Update) executeGenericHook(updateName, update, new EngineHookCaller() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda17
            @Override // com.exteragram.messenger.plugins.PluginsController.EngineHookCaller
            public final PluginsController.HookResult call(PluginsController.PluginsEngine pluginsEngine, Object obj, String str) {
                return PluginsController.$r8$lambda$OHXh3kQ8DZ_wjP82nv3qDlWGYDc(updateName, account, pluginsEngine, (TLRPC.Update) obj, str);
            }
        });
    }

    public static HookResult $r8$lambda$OHXh3kQ8DZ_wjP82nv3qDlWGYDc(String str, int i, PluginsEngine pluginsEngine, TLRPC.Update update, String str2) {
        return pluginsEngine.executeUpdateHook(str, i, update, str2);
    }

    @Override // com.exteragram.messenger.plugins.hooks.PluginsHooks
    public TLRPC.Updates executeUpdatesHook(final String containerName, final int account, TLRPC.Updates updates) {
        return (TLRPC.Updates) executeGenericHook(containerName, updates, new EngineHookCaller() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda29
            @Override // com.exteragram.messenger.plugins.PluginsController.EngineHookCaller
            public final PluginsController.HookResult call(PluginsController.PluginsEngine pluginsEngine, Object obj, String str) {
                return PluginsController.$r8$lambda$faHgcy3k1tshQuZQuTJlgU8Xri4(containerName, account, pluginsEngine, (TLRPC.Updates) obj, str);
            }
        });
    }

    public static HookResult $r8$lambda$faHgcy3k1tshQuZQuTJlgU8Xri4(String str, int i, PluginsEngine pluginsEngine, TLRPC.Updates updates, String str2) {
        return pluginsEngine.executeUpdatesHook(str, i, updates, str2);
    }

    @Override // com.exteragram.messenger.plugins.hooks.PluginsHooks
    public SendMessagesHelper.SendMessageParams executeSendMessageHook(final int account, SendMessagesHelper.SendMessageParams params) {
        return (SendMessagesHelper.SendMessageParams) executeGenericHook("send_message_hook", params, new EngineHookCaller() { // from class: com.exteragram.messenger.plugins.PluginsController$$ExternalSyntheticLambda20
            @Override // com.exteragram.messenger.plugins.PluginsController.EngineHookCaller
            public final PluginsController.HookResult call(PluginsController.PluginsEngine pluginsEngine, Object obj, String str) {
                return PluginsController.$r8$lambda$DAYJLe1d6xThRYHnkIRE8Mr2fSk(account, pluginsEngine, (SendMessagesHelper.SendMessageParams) obj, str);
            }
        });
    }

    public static HookResult $r8$lambda$DAYJLe1d6xThRYHnkIRE8Mr2fSk(int i, PluginsEngine pluginsEngine, SendMessagesHelper.SendMessageParams sendMessageParams, String str) {
        return pluginsEngine.executeSendMessageHook(i, sendMessageParams, str);
    }

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000e\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B!\u0012\b\u0010\u0003\u001a\u0004\u0018\u00018\u0000\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bR\u001e\u0010\u0003\u001a\u0004\u0018\u00018\u0000X\u0086\u000e¢\u0006\u0010\n\u0002\u0010\r\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0006\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u000f\"\u0004\b\u0012\u0010\u0011¨\u0006\u0013"}, d2 = {"Lcom/exteragram/messenger/plugins/PluginsController$HookResult;", "T", _UrlKt.FRAGMENT_ENCODE_SET, "result", "cancel", _UrlKt.FRAGMENT_ENCODE_SET, "isFinal", "<init>", "(Ljava/lang/Object;ZZ)V", "getResult", "()Ljava/lang/Object;", "setResult", "(Ljava/lang/Object;)V", "Ljava/lang/Object;", "getCancel", "()Z", "setCancel", "(Z)V", "setFinal", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class HookResult<T> {
        private boolean cancel;
        private boolean isFinal;
        private T result;

        public HookResult(T t, boolean z, boolean z2) {
            this.result = t;
            this.cancel = z;
            this.isFinal = z2;
        }

        public final T getResult() {
            return this.result;
        }

        public final void setResult(T t) {
            this.result = t;
        }

        public final boolean getCancel() {
            return this.cancel;
        }

        public final void setCancel(boolean z) {
            this.cancel = z;
        }

        /* JADX INFO: renamed from: isFinal, reason: from getter */
        public final boolean getIsFinal() {
            return this.isFinal;
        }

        public final void setFinal(boolean z) {
            this.isFinal = z;
        }
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\u001b\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f¨\u0006\u0010"}, d2 = {"Lcom/exteragram/messenger/plugins/PluginsController$PluginValidationResult;", _UrlKt.FRAGMENT_ENCODE_SET, "plugin", "Lcom/exteragram/messenger/plugins/Plugin;", "", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Lcom/exteragram/messenger/plugins/Plugin;Ljava/lang/String;)V", "getPlugin", "()Lcom/exteragram/messenger/plugins/Plugin;", "setPlugin", "(Lcom/exteragram/messenger/plugins/Plugin;)V", "getError", "()Ljava/lang/String;", "setError", "(Ljava/lang/String;)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class PluginValidationResult {
        private String error;
        private Plugin plugin;

        public PluginValidationResult(Plugin plugin, String str) {
            this.plugin = plugin;
            this.error = str;
        }

        public final Plugin getPlugin() {
            return this.plugin;
        }

        public final void setPlugin(Plugin plugin) {
            this.plugin = plugin;
        }

        public final String getError() {
            return this.error;
        }

        public final void setError(String str) {
            this.error = str;
        }
    }

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\bÂ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0013\u0010\u0004\u001a\u00020\u0005¢\u0006\n\n\u0002\b\b\u001a\u0004\b\u0006\u0010\u0007¨\u0006\t"}, d2 = {"Lcom/exteragram/messenger/plugins/PluginsController$SingletonHolder;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "INSTANCE", "Lcom/exteragram/messenger/plugins/PluginsController;", "getINSTANCE", "()Lcom/exteragram/messenger/plugins/PluginsController;", "INSTANCE$1", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class SingletonHolder {
        public static final SingletonHolder INSTANCE = new SingletonHolder();

        /* JADX INFO: renamed from: INSTANCE$1, reason: from kotlin metadata */
        private static final PluginsController instance = new PluginsController(null);

        private SingletonHolder() {
        }

        public final PluginsController getINSTANCE() {
            return instance;
        }
    }

    @Metadata(d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000b0\nH\u0007J\b\u0010\r\u001a\u00020\u000eH\u0007J\u001c\u0010\u000f\u001a\u00020\u00072\b\u0010\u0010\u001a\u0004\u0018\u00010\u00052\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0007J\u0012\u0010\u0013\u001a\u00020\u00142\b\u0010\u0010\u001a\u0004\u0018\u00010\u0005H\u0007J\b\u0010\u0015\u001a\u00020\u0014H\u0007J\u0012\u0010\u0016\u001a\u00020\u00072\b\u0010\u0017\u001a\u0004\u0018\u00010\u0005H\u0007J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0007H\u0007J\u0012\u0010\u001b\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u001a\u001a\u00020\u0007H\u0007J\b\u0010\u001c\u001a\u00020\u0019H\u0007J\b\u0010\u001d\u001a\u00020\u0019H\u0007J\b\u0010\u001e\u001a\u00020\u0014H\u0007J\u0012\u0010\u001f\u001a\u00020\u00192\b\u0010 \u001a\u0004\u0018\u00010!H\u0007J\u001c\u0010\u001f\u001a\u00020\u00192\b\u0010\"\u001a\u0004\u0018\u00010#2\b\u0010 \u001a\u0004\u0018\u00010!H\u0007J\u0014\u0010$\u001a\u0004\u0018\u00010\u000b2\b\u0010\"\u001a\u0004\u0018\u00010#H\u0007J\u0012\u0010%\u001a\u00020\u00142\b\u0010&\u001a\u0004\u0018\u00010\u0005H\u0007J\u001c\u0010%\u001a\u00020\u00142\b\u0010&\u001a\u0004\u0018\u00010\u00052\b\u0010'\u001a\u0004\u0018\u00010\u0005H\u0007J\u0012\u0010(\u001a\u00020\u00192\b\u0010&\u001a\u0004\u0018\u00010\u0005H\u0007J\u001a\u0010)\u001a\u00020\u00142\b\u0010&\u001a\u0004\u0018\u00010\u00052\u0006\u0010*\u001a\u00020\u0019H\u0007J\u0010\u0010+\u001a\u00020\u00142\u0006\u0010,\u001a\u00020-H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006."}, d2 = {"Lcom/exteragram/messenger/plugins/PluginsController$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "PREF_PLUGIN_ENABLED_KEY_PREFIX", _UrlKt.FRAGMENT_ENCODE_SET, "PLUGIN_FILE_ICON_NONE", _UrlKt.FRAGMENT_ENCODE_SET, "PLUGIN_FILE_ICON_ID_START", "enginesMap", "Ljava/util/concurrent/ConcurrentHashMap;", "Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", "getEngines", "getInstance", "Lcom/exteragram/messenger/plugins/PluginsController;", "registerFileIcon", "extension", "drawable", "Landroid/graphics/drawable/Drawable;", "unregisterFileIcon", _UrlKt.FRAGMENT_ENCODE_SET, "clearFileIcons", "getFileIconId", "fileName", "isPluginFileIcon", _UrlKt.FRAGMENT_ENCODE_SET, "icon", "getPluginFileIconDrawable", "isPluginEngineSupported", "isPluginEngineAvailable", "applyArtOpts", "isPlugin", "messageObject", "Lorg/telegram/messenger/MessageObject;", "file", "Ljava/io/File;", "getPluginEngine", "openPluginSettings", "pluginId", "linkAlias", "isPluginPinned", "setPluginPinned", "isPinned", "runOnPluginsQueue", "runnable", "Ljava/lang/Runnable;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
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
            if (messageObject == null || messageObject.getDocumentName() == null || !isPluginEngineSupported()) {
                return false;
            }
            String pathToMessage = ChatUtils.getInstance().getPathToMessage(messageObject);
            if (!TextUtils.isEmpty(pathToMessage)) {
                return isPlugin(new File(pathToMessage), messageObject);
            }
            return isPlugin(new File(messageObject.getDocumentName()), messageObject);
        }

        @JvmStatic
        public final boolean isPlugin(File file, MessageObject messageObject) {
            if (file == null) {
                return false;
            }
            for (PluginsEngine pluginsEngine : getEngines().values()) {
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
                BulletinFactory.of(lastFragment).createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.PluginEngineNotEnabled, pluginId), LocaleController.getString(R.string.Enable), 2750, new Runnable() { // from class: com.exteragram.messenger.plugins.PluginsController$Companion$$ExternalSyntheticLambda0
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
