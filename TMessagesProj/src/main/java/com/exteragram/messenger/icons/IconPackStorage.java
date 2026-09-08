package com.exteragram.messenger.icons;

import androidx.mediarouter.media.MediaRouteProviderProtocol;
import com.exteragram.messenger.export.output.FileManager;
import com.google.android.gms.cast.MediaStatus;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.BuildersKt__BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import okhttp3.internal.url._UrlKt;
import org.json.JSONException;
import org.json.JSONObject;
import org.simplifiles.SimpliFiles;
import org.simplifiles.archive.ArchiveFile;
import org.simplifiles.archive.ArchiveIssue;
import org.simplifiles.archive.ArchiveSaveOptions;
import org.simplifiles.archive.security.SecurityPolicy;
import org.simplifiles.exception.ArchiveValidationException;
import org.simplifiles.exception.CorruptedArchiveException;
import org.simplifiles.files.OverwritePolicy;
import org.simplifiles.files.SimpliDirectory;
import org.simplifiles.files.SimpliFile;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SaveToGallerySettingsHelper;

@Metadata(d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001HB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0002¢\u0006\u0004\b\u0007\u0010\bJ/\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\t2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u000b2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000bH\u0002¢\u0006\u0004\b\u000f\u0010\u0010J\u0019\u0010\u0012\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0011\u001a\u00020\u000bH\u0002¢\u0006\u0004\b\u0012\u0010\u0013J\u001f\u0010\u0015\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u000bH\u0002¢\u0006\u0004\b\u0015\u0010\u0016J\u0017\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0018\u001a\u00020\u0017H\u0002¢\u0006\u0004\b\u001a\u0010\u001bJ\u001b\u0010\u001e\u001a\u00020\u00192\n\u0010\u0018\u001a\u00060\u001cj\u0002`\u001dH\u0002¢\u0006\u0004\b\u001e\u0010\u001fJ\u0017\u0010\"\u001a\u00020!2\u0006\u0010 \u001a\u00020\u0004H\u0002¢\u0006\u0004\b\"\u0010#J\u0017\u0010&\u001a\u00020%2\u0006\u0010$\u001a\u00020!H\u0002¢\u0006\u0004\b&\u0010'J\r\u0010(\u001a\u00020\u000b¢\u0006\u0004\b(\u0010)J\u0017\u0010*\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b*\u0010+J\u001f\u0010-\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010,\u001a\u00020\u0004¢\u0006\u0004\b-\u0010.J\u001a\u0010/\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0005\u001a\u00020\u0004H\u0086@¢\u0006\u0004\b/\u00100J\u0017\u00101\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b1\u00102J\u0013\u00104\u001a\b\u0012\u0004\u0012\u00020\u000e03¢\u0006\u0004\b4\u00105J\u0015\u00107\u001a\u00020\u00062\u0006\u00106\u001a\u00020\u000e¢\u0006\u0004\b7\u00108J\u0015\u00109\u001a\u00020%2\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b9\u0010:J\u001e\u0010<\u001a\b\u0012\u0004\u0012\u00020%0;2\u0006\u0010\u0011\u001a\u00020\u000bH\u0086@¢\u0006\u0004\b<\u0010=J\u001e\u0010>\u001a\b\u0012\u0004\u0012\u00020\u000e0;2\u0006\u0010\u0011\u001a\u00020\u000bH\u0086@¢\u0006\u0004\b>\u0010=R$\u0010@\u001a\u0010\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u000e\u0018\u00010?8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b@\u0010AR\u0014\u0010C\u001a\u00020B8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\bC\u0010DR\u0014\u0010F\u001a\u00020E8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\bF\u0010G¨\u0006I"}, d2 = {"Lcom/exteragram/messenger/icons/IconPackStorage;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", _UrlKt.FRAGMENT_ENCODE_SET, "packId", _UrlKt.FRAGMENT_ENCODE_SET, "isValidPackId", "(Ljava/lang/String;)Z", "Lorg/json/JSONObject;", "jsonObject", "Ljava/io/File;", "packRoot", "location", "Lcom/exteragram/messenger/icons/IconPack;", "parseMetadata", "(Lorg/json/JSONObject;Ljava/io/File;Ljava/io/File;)Lcom/exteragram/messenger/icons/IconPack;", "file", "parseMetadataFile", "(Ljava/io/File;)Lcom/exteragram/messenger/icons/IconPack;", "targetDir", "extractPackArchive", "(Ljava/io/File;Ljava/io/File;)Lorg/json/JSONObject;", "Lorg/simplifiles/exception/ArchiveValidationException;", "e", "Lcom/exteragram/messenger/icons/IconPackStorageError;", "errorFromValidationException", "(Lorg/simplifiles/exception/ArchiveValidationException;)Lcom/exteragram/messenger/icons/IconPackStorageError;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "errorFromException", "(Ljava/lang/Exception;)Lcom/exteragram/messenger/icons/IconPackStorageError;", "prefix", "Lorg/simplifiles/files/SimpliDirectory;", "createTempCacheDirectory", "(Ljava/lang/String;)Lorg/simplifiles/files/SimpliDirectory;", "directory", _UrlKt.FRAGMENT_ENCODE_SET, "deleteDirectoryIfExists", "(Lorg/simplifiles/files/SimpliDirectory;)V", "getIconPacksDirectory", "()Ljava/io/File;", "findPackById", "(Ljava/lang/String;)Lcom/exteragram/messenger/icons/IconPack;", "resourceName", "resolveIconFile", "(Ljava/lang/String;Ljava/lang/String;)Ljava/io/File;", "bundlePack", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "bundlePackBlocking", "(Ljava/lang/String;)Ljava/io/File;", _UrlKt.FRAGMENT_ENCODE_SET, "getCustomPacks", "()Ljava/util/List;", "iconPack", "saveIconPackMetadata", "(Lcom/exteragram/messenger/icons/IconPack;)Z", "deletePack", "(Ljava/lang/String;)V", "Lcom/exteragram/messenger/icons/IconPackStorageResult;", "installPack", "(Ljava/io/File;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "parsePackFromZip", _UrlKt.FRAGMENT_ENCODE_SET, "cachedCustomPacks", "Ljava/util/Map;", "Lorg/simplifiles/archive/security/SecurityPolicy;", "iconPackArchivePolicy", "Lorg/simplifiles/archive/security/SecurityPolicy;", "Lorg/simplifiles/archive/ArchiveSaveOptions;", "iconPackArchiveSaveOptions", "Lorg/simplifiles/archive/ArchiveSaveOptions;", "IconPackStorageException", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nIconPackStorage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IconPackStorage.kt\ncom/exteragram/messenger/icons/IconPackStorage\n+ 2 Iterators.kt\nkotlin/collections/CollectionsKt__IteratorsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,361:1\n32#2,2:362\n1#3:364\n14048#4,2:365\n1220#5,2:367\n1249#5,4:369\n*S KotlinDebug\n*F\n+ 1 IconPackStorage.kt\ncom/exteragram/messenger/icons/IconPackStorage\n*L\n80#1:362,2\n236#1:365,2\n243#1:367,2\n243#1:369,4\n*E\n"})
public final class IconPackStorage {
    private static Map<String, IconPack> cachedCustomPacks;
    public static final IconPackStorage INSTANCE = new IconPackStorage();
    private static final SecurityPolicy iconPackArchivePolicy = SecurityPolicy.INSTANCE.builder().maxEntries(3000).maxTotalUncompressedSize(SaveToGallerySettingsHelper.DEFAULT_VIDEO_LIMIT).maxSingleFileSize(10485760).maxCompressionRatio(250.0d).build();
    private static final ArchiveSaveOptions iconPackArchiveSaveOptions = ArchiveSaveOptions.INSTANCE.builder().compressionLevel(0).build();

    private IconPackStorage() {
    }

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0002\u0018\u00002\u00060\u0001j\u0002`\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\t"}, d2 = {"Lcom/exteragram/messenger/icons/IconPackStorage$IconPackStorageException;", "Ljava/lang/Exception;", "Lkotlin/Exception;", MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "Lcom/exteragram/messenger/icons/IconPackStorageError;", "<init>", "(Lcom/exteragram/messenger/icons/IconPackStorageError;)V", "getError", "()Lcom/exteragram/messenger/icons/IconPackStorageError;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class IconPackStorageException extends Exception {
        private final IconPackStorageError error;

        public IconPackStorageException(IconPackStorageError iconPackStorageError) {
            super(iconPackStorageError.name());
            this.error = iconPackStorageError;
        }

        public final IconPackStorageError getError() {
            return this.error;
        }
    }

    public final File getIconPacksDirectory() {
        File file = new File(ApplicationLoader.applicationContext.getFilesDir(), "icon_packs");
        SimpliFiles.directory(file).create();
        return file;
    }

    public final boolean isValidPackId(String packId) {
        return (StringsKt.isBlank(packId) || StringsKt.contains$default((CharSequence) packId, '/', false, 2, (Object) null) || StringsKt.contains$default((CharSequence) packId, '\\', false, 2, (Object) null) || Intrinsics.areEqual(packId, ".") || Intrinsics.areEqual(packId, "..")) ? false : true;
    }

    public static String $packId;
        int label;

        String $packId;
        int label;

        File $file;
        private File $file;
        int label;

        public C01512(File file, Continuation<? super C01512> continuation) {
            super(2, continuation);
            this.$file = file;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new C01512(this.$file, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public /* bridge */ /* synthetic */ Object invoke(CoroutineScope coroutineScope, Continuation<? super IconPackStorageResult<? extends IconPack>> continuation) {
            return invoke2(coroutineScope, (Continuation<? super IconPackStorageResult<IconPack>>) continuation);
        }

        public final Object invoke2(CoroutineScope coroutineScope, Continuation<? super IconPackStorageResult<IconPack>> continuation) {
            return ((C01512) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) throws Exception {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            if (this.label != 0) {
                Segment$$ExternalSyntheticBUOutline1.m("call to 'resume' before 'invoke' with coroutine");
                return null;
            }
            ResultKt.throwOnFailure(obj);
            IconPackStorage iconPackStorage = IconPackStorage.INSTANCE;
            SimpliDirectory simpliDirectoryCreateTempCacheDirectory = iconPackStorage.createTempCacheDirectory("preview");
            try {
                return new IconPackStorageResult.Success(iconPackStorage.parseMetadata(iconPackStorage.extractPackArchive(this.$file, simpliDirectoryCreateTempCacheDirectory.getFile()), simpliDirectoryCreateTempCacheDirectory.getFile(), simpliDirectoryCreateTempCacheDirectory.getFile()));
            } catch (IconPackStorageException e) {
                FileLog.e("Failed to parse pack for preview: " + e.getError());
                IconPackStorage.INSTANCE.deleteDirectoryIfExists(simpliDirectoryCreateTempCacheDirectory);
                return new IconPackStorageResult.Failure(e.getError());
            } catch (Exception e2) {
                FileLog.e("Failed to parse pack for preview", e2);
                IconPackStorage iconPackStorage2 = IconPackStorage.INSTANCE;
                iconPackStorage2.deleteDirectoryIfExists(simpliDirectoryCreateTempCacheDirectory);
                return new IconPackStorageResult.Failure(iconPackStorage2.errorFromException(e2));
            }
        }
    }

    public final Object parsePackFromZip(File file, Continuation<? super IconPackStorageResult<IconPack>> continuation) {
        return BuildersKt.withContext(Dispatchers.getIO(), new C01512(file, null), continuation);
    }
}
