package com.exteragram.messenger.icons;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import com.android.dx.rop.code.RegisterSpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.UByte;
import kotlin.collections.ArraysKt;
import kotlin.collections.SetsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Charsets;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;

@Metadata(d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\u0018\u0000 )2\u00020\u0001:\u0001)B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\u0007H\u0016J\u0010\u0010\r\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\tH\u0002J\u0012\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\b\u001a\u00020\tH\u0002J\n\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0002J\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0012\u001a\u00020\u000fH\u0002J,\u0010\u0013\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0014\u001a\u00020\u00072\u0018\u0010\u0015\u001a\u0014\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00050\u0016H\u0002JO\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\u0006\u0010\b\u001a\u00020\t2\u0010\u0010\u001b\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u0007\u0018\u00010\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u00072\u0010\u0010\u001e\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u0007\u0018\u00010\u001c2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0007H\u0016¢\u0006\u0002\u0010 J\u001c\u0010!\u001a\u0004\u0018\u00010\t2\u0006\u0010\b\u001a\u00020\t2\b\u0010\"\u001a\u0004\u0018\u00010#H\u0016J1\u0010$\u001a\u00020%2\u0006\u0010\b\u001a\u00020\t2\b\u0010\u001d\u001a\u0004\u0018\u00010\u00072\u0010\u0010\u001e\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u0007\u0018\u00010\u001cH\u0016¢\u0006\u0002\u0010&J;\u0010'\u001a\u00020%2\u0006\u0010\b\u001a\u00020\t2\b\u0010\"\u001a\u0004\u0018\u00010#2\b\u0010\u001d\u001a\u0004\u0018\u00010\u00072\u0010\u0010\u001e\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u0007\u0018\u00010\u001cH\u0016¢\u0006\u0002\u0010(¨\u0006*"}, d2 = {"Lcom/exteragram/messenger/icons/IconPackProvider;", "Landroid/content/ContentProvider;", "<init>", "()V", "onCreate", _UrlKt.FRAGMENT_ENCODE_SET, "getType", _UrlKt.FRAGMENT_ENCODE_SET, "uri", "Landroid/net/Uri;", "openFile", "Landroid/os/ParcelFileDescriptor;", "mode", "isIconUri", "resolveSource", "Ljava/io/File;", "getFallbackIcon", "getRasterizedIcon", "source", "materializeCacheFile", "name", "write", "Lkotlin/Function2;", "Landroid/content/Context;", "Ljava/io/FileOutputStream;", "query", "Landroid/database/Cursor;", "projection", _UrlKt.FRAGMENT_ENCODE_SET, "selection", "selectionArgs", "sortOrder", "(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;", "insert", "values", "Landroid/content/ContentValues;", "delete", _UrlKt.FRAGMENT_ENCODE_SET, "(Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I", "update", "(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nIconPackProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IconPackProvider.kt\ncom/exteragram/messenger/icons/IconPackProvider\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,172:1\n1#2:173\n*E\n"})
public final class IconPackProvider extends ContentProvider {

    public static final Companion INSTANCE = new Companion(null);

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u0005H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lcom/exteragram/messenger/icons/IconPackProvider$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "PATH_ICON", _UrlKt.FRAGMENT_ENCODE_SET, "getIconUri", "Landroid/net/Uri;", "packId", "resourceName", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final Uri getIconUri(String packId, String resourceName) {
            File fileResolveIconFile = IconPackStorage.INSTANCE.resolveIconFile(packId, resourceName);
            if (fileResolveIconFile == null || !SetsKt.setOf((Object[]) new String[]{"png", "webp", "jpg", "jpeg", "svg"}).contains(FilesKt.getExtension(fileResolveIconFile).toLowerCase(Locale.ROOT))) {
                return null;
            }
            Uri.Builder builderAppendPath = new Uri.Builder().scheme("content").authority(ApplicationLoader.getApplicationId() + ".icon_pack_provider").appendPath("icon").appendPath(packId).appendPath(resourceName);
            StringBuilder sb = new StringBuilder();
            sb.append(fileResolveIconFile.lastModified());
            sb.append('_');
            sb.append(fileResolveIconFile.length());
            return builderAppendPath.appendQueryParameter(RegisterSpec.PREFIX, sb.toString()).build();
        }
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        Context applicationContext;
        Context context = getContext();
        if (context == null || (applicationContext = context.getApplicationContext()) == null || ApplicationLoader.applicationContext != null) {
            return true;
        }
        ApplicationLoader.applicationContext = applicationContext;
        return true;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        if (isIconUri(uri)) {
            return "image/png";
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Object objM2315constructorimpl;
        if (!Intrinsics.areEqual(mode, "r")) {
            throw new SecurityException("Icon pack files are read-only");
        }
        if (!isIconUri(uri)) {
            throw new FileNotFoundException(uri.toString());
        }
        try {
            Result.Companion companion = Result.INSTANCE;
            File fileResolveSource = resolveSource(uri);
            objM2315constructorimpl = Result.m2315constructorimpl(fileResolveSource != null ? getRasterizedIcon(fileResolveSource) : null);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM2315constructorimpl = Result.m2315constructorimpl(ResultKt.createFailure(th));
        }
        File fallbackIcon = (File) (Result.m2321isFailureimpl(objM2315constructorimpl) ? null : objM2315constructorimpl);
        if (fallbackIcon == null && (fallbackIcon = getFallbackIcon()) == null) {
            throw new FileNotFoundException(uri.toString());
        }
        return ParcelFileDescriptor.open(fallbackIcon, 268435456);
    }

    private final boolean isIconUri(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        return pathSegments.size() == 3 && Intrinsics.areEqual(pathSegments.get(0), "icon");
    }

    private final File resolveSource(Uri uri) {
        File fileResolveIconFile;
        List<String> pathSegments = uri.getPathSegments();
        if (!isIconUri(uri) || (fileResolveIconFile = IconPackStorage.INSTANCE.resolveIconFile(pathSegments.get(1), pathSegments.get(2))) == null) {
            return null;
        }
        switch (FilesKt.getExtension(fileResolveIconFile).toLowerCase(Locale.ROOT)) {
            case "jpg":
            case "png":
            case "svg":
            case "jpeg":
            case "webp":
                return fileResolveIconFile;
            default:
                return null;
        }
    }

    private final File getFallbackIcon() {
        Resources resources;
        DisplayMetrics displayMetrics;
        Context context = getContext();
        if (context == null || (resources = context.getResources()) == null || (displayMetrics = resources.getDisplayMetrics()) == null) {
            return null;
        }
        return materializeCacheFile("default_" + displayMetrics.densityDpi + ".png", new Function2() { 
            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(Object obj, Object obj2) {
                return Boolean.valueOf(IconPackProvider.$r8$lambda$SyNGJm9nV6CHnMrynDpJa0DoWpo((Context) obj, (FileOutputStream) obj2));
            }
        });
    }

    public static boolean $r8$lambda$SyNGJm9nV6CHnMrynDpJa0DoWpo(Context context, FileOutputStream fileOutputStream) {
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification);
        if (bitmapDecodeResource == null) {
            return false;
        }
        try {
            return bitmapDecodeResource.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } finally {
            bitmapDecodeResource.recycle();
        }
    }

    private final File getRasterizedIcon(final File source) {
        Resources resources;
        DisplayMetrics displayMetrics;
        Context context = getContext();
        if (context == null || (resources = context.getResources()) == null || (displayMetrics = resources.getDisplayMetrics()) == null) {
            return null;
        }
        return materializeCacheFile(ArraysKt.joinToString$default(MessageDigest.getInstance("SHA-256").digest((source.getCanonicalPath() + ':' + source.lastModified() + ':' + source.length() + ':' + displayMetrics.densityDpi).getBytes(Charsets.UTF_8)), (CharSequence) _UrlKt.FRAGMENT_ENCODE_SET, (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return String.format("%02x", Arrays.copyOf(new Object[]{Integer.valueOf(((Byte) obj).byteValue() & UByte.MAX_VALUE)}, 1));
            }
        }, 30, (Object) null) + ".png", new Function2() { 
            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(Object obj, Object obj2) {
                return Boolean.valueOf(IconPackProvider.$r8$lambda$kh6b8SwIq737lJ0bnqOcgLGaQv8(source, (Context) obj, (FileOutputStream) obj2));
            }
        });
    }

    public static boolean $r8$lambda$kh6b8SwIq737lJ0bnqOcgLGaQv8(File file, Context context, FileOutputStream fileOutputStream) {
        Bitmap bitmapCreateBitmapFromFile = IconManager.INSTANCE.createBitmapFromFile(file.getAbsolutePath(), R.drawable.notification, context.getResources().getDisplayMetrics().densityDpi, null);
        if (bitmapCreateBitmapFromFile == null) {
            return false;
        }
        try {
            return bitmapCreateBitmapFromFile.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } finally {
            bitmapCreateBitmapFromFile.recycle();
        }
    }

    private final File materializeCacheFile(String name, Function2<? super Context, ? super FileOutputStream, Boolean> write) {
        Context context = getContext();
        File file = null;
        if (context == null) {
            return null;
        }
        File file2 = new File(context.getCacheDir(), "notification_icons");
        if (!file2.exists() && !file2.mkdirs()) {
            return null;
        }
        File file3 = new File(file2, name);
        if (file3.isFile() && file3.length() > 0) {
            return file3;
        }
        synchronized (IconPackProvider.class) {
            if (file3.isFile() && file3.length() > 0) {
                return file3;
            }
            File file4 = new File(file2, name + ".tmp");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file4);
                try {
                    boolean zBooleanValue = write.invoke(context, fileOutputStream).booleanValue();
                    CloseableKt.closeFinally(fileOutputStream, null);
                    if (!zBooleanValue) {
                        if (file4.exists()) {
                            file4.delete();
                        }
                        return null;
                    }
                    if (!file4.renameTo(file3)) {
                        FilesKt.copyTo$default(file4, file3, true, 0, 4, null);
                        file4.delete();
                    }
                    if (file4.exists()) {
                        file4.delete();
                    }
                    file = file3;
                    return file;
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        CloseableKt.closeFinally(fileOutputStream, th);
                        throw th2;
                    }
                }
            } catch (Exception unused) {
                if (file4.exists()) {
                    file4.delete();
                }
            } catch (Throwable th3) {
                if (!file4.exists()) {
                    throw th3;
                }
                file4.delete();
                throw th3;
            }
        }
    }
}
