package com.exteragram.messenger.icons;

import com.exteragram.messenger.export.output.FileManager;
import java.io.File;
import java.io.IOException;
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

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0002\u0018\u00002\u00060\u0001j\u0002`\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\t"}, d2 = {"Lcom/exteragram/messenger/icons/IconPackStorage$IconPackStorageException;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "", "Lcom/exteragram/messenger/icons/IconPackStorageError;", "<init>", "(Lcom/exteragram/messenger/icons/IconPackStorageError;)V", "getError", "()Lcom/exteragram/messenger/icons/IconPackStorageError;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
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

    /* JADX INFO: Access modifiers changed from: private */
    private final boolean isValidPackId(String packId) {
        return (StringsKt.isBlank(packId) || packId.contains("/") || packId.contains("\\") || Intrinsics.areEqual(packId, ".") || Intrinsics.areEqual(packId, "..")) ? false : true;
    }

    public static /* synthetic */ IconPack parseMetadata$default(IconPackStorage iconPackStorage, JSONObject jSONObject, File file, File file2, int i, Object obj) throws JSONException {
        if ((i & 2) != 0) {
            file = null;
        }
        if ((i & 4) != 0) {
            file2 = null;
        }
        return iconPackStorage.parseMetadata(jSONObject, file, file2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final IconPack parseMetadata(JSONObject jsonObject, File packRoot, File location) throws JSONException {
        Iterator<String> itKeys;
        String string = jsonObject.getString("packName");
        String string2 = jsonObject.getString("packId");
        if (!isValidPackId(string2)) {
            throw new SecurityException("Invalid icon pack id: " + string2);
        }
        String strOptString = jsonObject.optString("author", "Unknown");
        String strOptString2 = jsonObject.optString("version", "1.0");
        JSONObject jSONObjectOptJSONObject = jsonObject.optJSONObject("icons");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (jSONObjectOptJSONObject != null && (itKeys = jSONObjectOptJSONObject.keys()) != null) {
            while (itKeys.hasNext()) {
                String next = itKeys.next();
                String string3 = jSONObjectOptJSONObject.getString(next);
                if (packRoot != null) {
                    try {
                        SimpliFiles.directory(packRoot).resolveInside(string3);
                        string3 = FilesKt.getInvariantSeparatorsPath(FilesKt.relativeTo(new File(packRoot, string3).getCanonicalFile(), packRoot.getCanonicalFile()));
                    } catch (IOException e) {
                        FileLog.e(e);
                    }
                }
                linkedHashMap.put(next, string3);
            }
        }
        return new IconPack(string2, string, strOptString, strOptString2, linkedHashMap, null, location, 32, null);
    }

    private final IconPack parseMetadataFile(File file) {
        try {
            return parseMetadata$default(this, new JSONObject(SimpliFile.readText$default(SimpliFiles.file(file), 262144, null, 2, null)), file.getParentFile(), null, 4, null);
        } catch (Exception e) {
            FileLog.e("Error parsing metadata.json", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final JSONObject extractPackArchive(File file, File targetDir) throws IconPackStorageException {
        ArchiveFile archiveFileFile = SimpliFiles.archive(file).withPolicy(iconPackArchivePolicy).extractTo(targetDir).file("metadata.json");
        if (!archiveFileFile.exists()) {
            FileLog.e("Icon pack archive does not contain metadata.json");
            throw new IconPackStorageException(IconPackStorageError.MISSING_METADATA);
        }
        if (archiveFileFile.getSize() > 262144) {
            FileLog.e("Icon pack metadata.json is too large");
            throw new IconPackStorageException(IconPackStorageError.METADATA_TOO_LARGE);
        }
        try {
            return new JSONObject(ArchiveFile.readText$default(archiveFileFile, null, 1, null));
        } catch (JSONException e) {
            throw new IconPackStorageException(IconPackStorageError.INVALID_METADATA);
        }
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private final IconPackStorageError errorFromValidationException(ArchiveValidationException e) {
        ArchiveIssue archiveIssue = (ArchiveIssue) CollectionsKt.firstOrNull((List) e.getReport().getIssues());
        String code = archiveIssue != null ? archiveIssue.getCode() : null;
        if (code != null) {
            switch (code.hashCode()) {
                case -1169710668:
                    if (code.equals("archive.entries.too_many")) {
                        return IconPackStorageError.TOO_MANY_FILES;
                    }
                    break;
                case -964476182:
                    if (code.equals("archive.total_size.too_large")) {
                        return IconPackStorageError.ARCHIVE_TOO_LARGE;
                    }
                    break;
                case 51248523:
                    if (code.equals("archive.entry.size.too_large")) {
                        return IconPackStorageError.FILE_TOO_LARGE;
                    }
                    break;
                case 1340485329:
                    if (code.equals("archive.entry.compression_ratio.too_high")) {
                        return IconPackStorageError.COMPRESSION_RATIO_TOO_HIGH;
                    }
                    break;
            }
        }
        return IconPackStorageError.INVALID_ARCHIVE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final IconPackStorageError errorFromException(Exception e) {
        if (e instanceof IconPackStorageException) {
            return ((IconPackStorageException) e).getError();
        }
        if (e instanceof ArchiveValidationException) {
            return errorFromValidationException((ArchiveValidationException) e);
        }
        if (e instanceof CorruptedArchiveException) {
            return IconPackStorageError.INVALID_ARCHIVE;
        }
        if (!(e instanceof SecurityException) && !(e instanceof JSONException)) {
            return IconPackStorageError.UNKNOWN;
        }
        return IconPackStorageError.INVALID_METADATA;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final SimpliDirectory createTempCacheDirectory(String prefix) {
        return SimpliFiles.directory(new File(ApplicationLoader.applicationContext.getCacheDir(), prefix + '_' + System.currentTimeMillis())).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void deleteDirectoryIfExists(SimpliDirectory directory) {
        try {
            if (directory.exists()) {
                directory.deleteRecursively();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public final synchronized IconPack findPackById(String packId) {
        IconPack iconPack;
        if (!isValidPackId(packId)) {
            return null;
        }
        Map<String, IconPack> map = cachedCustomPacks;
        if (map != null && (iconPack = map.get(packId)) != null) {
            return iconPack;
        }
        SimpliDirectory simpliDirectoryDirectory = SimpliFiles.directory(new File(getIconPacksDirectory(), packId));
        if (simpliDirectoryDirectory.exists()) {
            SimpliFile simpliFileFile = simpliDirectoryDirectory.file("metadata.json");
            if (simpliFileFile.exists()) {
                return parseMetadataFile(simpliFileFile.getFile());
            }
        }
        return null;
    }

    public final File resolveIconFile(String packId, String resourceName) {
        String str;
        if (isValidPackId(packId) && new Regex("[A-Za-z0-9_]+").matches(resourceName)) {
            try {
                IconPack iconPackFindPackById = findPackById(packId);
                if (iconPackFindPackById == null || (str = iconPackFindPackById.getIcons().get(resourceName)) == null) {
                    return null;
                }
                File canonicalFile = new File(getIconPacksDirectory(), packId).getCanonicalFile();
                File canonicalFile2 = new File(canonicalFile, str).getCanonicalFile();
                if (canonicalFile2.getPath().startsWith(canonicalFile.getPath() + File.separator) && canonicalFile2.isFile()) {
                    return canonicalFile2;
                }
                return null;
            } catch (Exception e) {
                FileLog.e("Failed to resolve icon file for pack " + packId, e);
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.icons.IconPackStorage$bundlePack$2, reason: invalid class name */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "Ljava/io/File;", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.icons.IconPackStorage$bundlePack$2", f = "IconPackStorage.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class AnonymousClass2 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super File>, Object> {
        final /* synthetic */ String $packId;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AnonymousClass2(String str, Continuation continuation) {
            super(2, (Continuation) continuation);
            this.$packId = str;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new AnonymousClass2(this.$packId, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super File> continuation) {
            return ((AnonymousClass2) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            IconPack iconPackFindPackById;
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            if (this.label != 0) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            ResultKt.throwOnFailure(obj);
            IconPackStorage iconPackStorage = IconPackStorage.INSTANCE;
            if (!iconPackStorage.isValidPackId(this.$packId) || (iconPackFindPackById = iconPackStorage.findPackById(this.$packId)) == null) {
                return null;
            }
            SimpliDirectory simpliDirectoryDirectory = SimpliFiles.directory(new File(iconPackStorage.getIconPacksDirectory(), this.$packId));
            if (!simpliDirectoryDirectory.exists()) {
                return null;
            }
            SimpliDirectory simpliDirectoryDirectory2 = SimpliFiles.directory(new File(ApplicationLoader.applicationContext.getCacheDir(), "shared_packs"));
            try {
                if (simpliDirectoryDirectory2.exists()) {
                    simpliDirectoryDirectory2.deleteRecursively();
                }
                simpliDirectoryDirectory2.create();
                return simpliDirectoryDirectory.zipTo(simpliDirectoryDirectory2.file(FileManager.fileNameFromUserString(iconPackFindPackById.getName()) + ".icons").getFile(), IconPackStorage.iconPackArchiveSaveOptions).getFile();
            } catch (Exception e) {
                FileLog.e("Failed to bundle pack: " + this.$packId, e);
                return null;
            }
        }
    }

    public final Object bundlePack(String str, Continuation<? super File> continuation) {
        return BuildersKt.withContext(Dispatchers.getIO(), new AnonymousClass2(str, null), continuation);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.icons.IconPackStorage$bundlePackBlocking$1, reason: invalid class name */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "Ljava/io/File;", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.icons.IconPackStorage$bundlePackBlocking$1", f = "IconPackStorage.kt", i = {}, l = {228}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super File>, Object> {
        final /* synthetic */ String $packId;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AnonymousClass1(String str, Continuation continuation) {
            super(2, (Continuation) continuation);
            this.$packId = str;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new AnonymousClass1(this.$packId, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super File> continuation) {
            return ((AnonymousClass1) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i != 0) {
                if (i == 1) {
                    ResultKt.throwOnFailure(obj);
                    return obj;
                }
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            ResultKt.throwOnFailure(obj);
            IconPackStorage iconPackStorage = IconPackStorage.INSTANCE;
            String str = this.$packId;
            this.label = 1;
            Object objBundlePack = iconPackStorage.bundlePack(str, this);
            return objBundlePack == coroutine_suspended ? coroutine_suspended : objBundlePack;
        }
    }

    public final File bundlePackBlocking(String packId) {
        try {
            return (File) BuildersKt.runBlocking(kotlin.coroutines.EmptyCoroutineContext.INSTANCE, new AnonymousClass1(packId, null));
        } catch (InterruptedException e) {
            FileLog.e(e);
            return null;
        }
    }

    public final synchronized List<IconPack> getCustomPacks() {
        IconPack metadataFile;
        Collection<IconPack> collectionValues;
        Map<String, IconPack> map = cachedCustomPacks;
        if (map != null && (collectionValues = map.values()) != null) {
            return new ArrayList(collectionValues);
        }
        ArrayList arrayList = new ArrayList();
        File[] fileArrListFiles = getIconPacksDirectory().listFiles();
        int i = 0;
        if (fileArrListFiles != null) {
            for (File file : fileArrListFiles) {
                SimpliFile simpliFileFile = SimpliFiles.directory(file).file("metadata.json");
                if (simpliFileFile.exists() && (metadataFile = INSTANCE.parseMetadataFile(simpliFileFile.getFile())) != null) {
                    arrayList.add(metadataFile);
                }
            }
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(arrayList, 10)), 16));
        int size = arrayList.size();
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            linkedHashMap.put(((IconPack) obj).getId(), obj);
        }
        cachedCustomPacks = linkedHashMap;
        return arrayList;
    }

    public final synchronized boolean saveIconPackMetadata(IconPack iconPack) {
        if (!isValidPackId(iconPack.getId())) {
            FileLog.e("Invalid icon pack id: " + iconPack.getId());
            return false;
        }
        try {
            SimpliFile simpliFileFile = SimpliFiles.directory(new File(getIconPacksDirectory(), iconPack.getId())).create().file("metadata.json");
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("schemaVersion", 1);
            jSONObject.put("packName", iconPack.getName());
            jSONObject.put("packId", iconPack.getId());
            jSONObject.put("author", iconPack.getAuthor());
            jSONObject.put("version", iconPack.getVersion());
            JSONObject jSONObject2 = new JSONObject();
            for (Map.Entry<String, String> entry : iconPack.getIcons().entrySet()) {
                jSONObject2.put(entry.getKey(), entry.getValue());
            }
            jSONObject.put("icons", jSONObject2);
            SimpliFile.writeTextAtomic$default(simpliFileFile, jSONObject.toString(4), null, 2, null);
            cachedCustomPacks = null;
            return true;
        } catch (Exception e) {
            FileLog.e("Error saving metadata", e);
            return false;
        }
    }

    public final synchronized void deletePack(String packId) {
        if (isValidPackId(packId)) {
            SimpliDirectory simpliDirectoryDirectory = SimpliFiles.directory(new File(getIconPacksDirectory(), packId));
            if (simpliDirectoryDirectory.exists()) {
                deleteDirectoryIfExists(simpliDirectoryDirectory);
                cachedCustomPacks = null;
            }
        }
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.icons.IconPackStorage$installPack$2, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u0003H\n"}, d2 = {"<anonymous>", "Lcom/exteragram/messenger/icons/IconPackStorageResult;", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.icons.IconPackStorage$installPack$2", f = "IconPackStorage.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    @SourceDebugExtension({"SMAP\nIconPackStorage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IconPackStorage.kt\ncom/exteragram/messenger/icons/IconPackStorage$installPack$2\n+ 2 IconPackStorage.kt\ncom/exteragram/messenger/icons/IconPackStorage\n*L\n1#1,361:1\n158#2,6:362\n*S KotlinDebug\n*F\n+ 1 IconPackStorage.kt\ncom/exteragram/messenger/icons/IconPackStorage$installPack$2\n*L\n290#1:362,6\n*E\n"})
    public static final class C01502 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super IconPackStorageResult<? extends Unit>>, Object> {
        final /* synthetic */ File $file;
        private /* synthetic */ Object L$0;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01502(File file, Continuation continuation) {
            super(2, (Continuation) continuation);
            this.$file = file;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C01502 c01502 = new C01502(this.$file, continuation);
            c01502.L$0 = obj;
            return (Continuation) c01502;
        }

        @Override // kotlin.jvm.functions.Function2
        public /* bridge */ /* synthetic */ Object invoke(CoroutineScope coroutineScope, Continuation<? super IconPackStorageResult<? extends Unit>> continuation) {
            return invoke2(coroutineScope, (Continuation<? super IconPackStorageResult<Unit>>) continuation);
        }

        /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
        public final Object invoke2(CoroutineScope coroutineScope, Continuation<? super IconPackStorageResult<Unit>> continuation) {
            return ((C01502) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:32:0x00d3 A[Catch: all -> 0x0095, TryCatch #2 {all -> 0x0095, blocks: (B:6:0x0019, B:12:0x0080, B:14:0x0086, B:16:0x008c, B:24:0x00b1, B:30:0x00cd, B:32:0x00d3, B:34:0x00d9, B:36:0x00df, B:37:0x00e2, B:38:0x00e7, B:39:0x00ef, B:41:0x00f5, B:42:0x00f8, B:43:0x00fa, B:45:0x00fe, B:46:0x00ff, B:50:0x010b, B:51:0x010c, B:29:0x00c3, B:23:0x00a7, B:11:0x0076, B:26:0x00b7, B:8:0x0068, B:20:0x0099, B:44:0x00fb), top: B:66:0x0019, inners: #0, #1, #4, #5 }] */
        /* JADX WARN: Code duplicated, block: B:34:0x00d9 A[Catch: all -> 0x0095, TryCatch #2 {all -> 0x0095, blocks: (B:6:0x0019, B:12:0x0080, B:14:0x0086, B:16:0x008c, B:24:0x00b1, B:30:0x00cd, B:32:0x00d3, B:34:0x00d9, B:36:0x00df, B:37:0x00e2, B:38:0x00e7, B:39:0x00ef, B:41:0x00f5, B:42:0x00f8, B:43:0x00fa, B:45:0x00fe, B:46:0x00ff, B:50:0x010b, B:51:0x010c, B:29:0x00c3, B:23:0x00a7, B:11:0x0076, B:26:0x00b7, B:8:0x0068, B:20:0x0099, B:44:0x00fb), top: B:66:0x0019, inners: #0, #1, #4, #5 }] */
        /* JADX WARN: Code duplicated, block: B:36:0x00df A[Catch: all -> 0x0095, TryCatch #2 {all -> 0x0095, blocks: (B:6:0x0019, B:12:0x0080, B:14:0x0086, B:16:0x008c, B:24:0x00b1, B:30:0x00cd, B:32:0x00d3, B:34:0x00d9, B:36:0x00df, B:37:0x00e2, B:38:0x00e7, B:39:0x00ef, B:41:0x00f5, B:42:0x00f8, B:43:0x00fa, B:45:0x00fe, B:46:0x00ff, B:50:0x010b, B:51:0x010c, B:29:0x00c3, B:23:0x00a7, B:11:0x0076, B:26:0x00b7, B:8:0x0068, B:20:0x0099, B:44:0x00fb), top: B:66:0x0019, inners: #0, #1, #4, #5 }] */
        /* JADX WARN: Code duplicated, block: B:39:0x00ef A[Catch: all -> 0x0095, TryCatch #2 {all -> 0x0095, blocks: (B:6:0x0019, B:12:0x0080, B:14:0x0086, B:16:0x008c, B:24:0x00b1, B:30:0x00cd, B:32:0x00d3, B:34:0x00d9, B:36:0x00df, B:37:0x00e2, B:38:0x00e7, B:39:0x00ef, B:41:0x00f5, B:42:0x00f8, B:43:0x00fa, B:45:0x00fe, B:46:0x00ff, B:50:0x010b, B:51:0x010c, B:29:0x00c3, B:23:0x00a7, B:11:0x0076, B:26:0x00b7, B:8:0x0068, B:20:0x0099, B:44:0x00fb), top: B:66:0x0019, inners: #0, #1, #4, #5 }] */
        /* JADX WARN: Code duplicated, block: B:41:0x00f5 A[Catch: all -> 0x0095, TryCatch #2 {all -> 0x0095, blocks: (B:6:0x0019, B:12:0x0080, B:14:0x0086, B:16:0x008c, B:24:0x00b1, B:30:0x00cd, B:32:0x00d3, B:34:0x00d9, B:36:0x00df, B:37:0x00e2, B:38:0x00e7, B:39:0x00ef, B:41:0x00f5, B:42:0x00f8, B:43:0x00fa, B:45:0x00fe, B:46:0x00ff, B:50:0x010b, B:51:0x010c, B:29:0x00c3, B:23:0x00a7, B:11:0x0076, B:26:0x00b7, B:8:0x0068, B:20:0x0099, B:44:0x00fb), top: B:66:0x0019, inners: #0, #1, #4, #5 }] */
        /* JADX WARN: Code duplicated, block: B:62:0x00b7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:67:0x0099 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:69:0x00fb A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            IconPackStorageResult.Failure failure;
            Object objM2315constructorimpl;
            Object objM2315constructorimpl2;
            Object failure2;
            Object objM2315constructorimpl3;
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            if (this.label != 0) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            ResultKt.throwOnFailure(obj);
            try {
                IconPackStorage iconPackStorage = IconPackStorage.INSTANCE;
                File file = this.$file;
                SimpliDirectory simpliDirectoryCreateTempCacheDirectory = iconPackStorage.createTempCacheDirectory("install");
                try {
                    String id = IconPackStorage.parseMetadata$default(iconPackStorage, iconPackStorage.extractPackArchive(file, simpliDirectoryCreateTempCacheDirectory.getFile()), simpliDirectoryCreateTempCacheDirectory.getFile(), null, 4, null).getId();
                    File file2 = new File(iconPackStorage.getIconPacksDirectory(), id);
                    File file3 = new File(iconPackStorage.getIconPacksDirectory(), id + "_trash_" + System.currentTimeMillis());
                    SimpliDirectory simpliDirectoryDirectory = SimpliFiles.directory(file2);
                    SimpliDirectory simpliDirectoryDirectory2 = SimpliFiles.directory(file3);
                    if (simpliDirectoryDirectory.exists()) {
                        boolean moveFailed = false;
                        try {
                            simpliDirectoryDirectory.moveTo(file3, OverwritePolicy.ERROR);
                        } catch (Throwable th) {
                            moveFailed = true;
                        }
                        if (moveFailed && !simpliDirectoryDirectory.deleteRecursively()) {
                            failure2 = new IconPackStorageResult.Failure(IconPackStorageError.STORAGE_ERROR);
                        } else {
                            boolean move2Failed = false;
                            try {
                                simpliDirectoryCreateTempCacheDirectory.moveTo(file2, OverwritePolicy.ERROR);
                            } catch (Throwable th2) {
                                move2Failed = true;
                            }
                            if (move2Failed) {
                                boolean copyFailed = false;
                                try {
                                    simpliDirectoryCreateTempCacheDirectory.copyTo(file2, OverwritePolicy.ERROR);
                                } catch (Throwable th3) {
                                    copyFailed = true;
                                }
                                if (copyFailed) {
                                    if (simpliDirectoryDirectory2.exists()) {
                                        if (simpliDirectoryDirectory.exists()) {
                                            simpliDirectoryDirectory.deleteRecursively();
                                        }
                                        simpliDirectoryDirectory2.moveTo(file2, OverwritePolicy.ERROR);
                                    }
                                    failure2 = new IconPackStorageResult.Failure(IconPackStorageError.STORAGE_ERROR);
                                } else {
                                    if (simpliDirectoryDirectory2.exists()) {
                                        simpliDirectoryDirectory2.deleteRecursively();
                                    }
                                    synchronized (IconPackStorage.INSTANCE) {
                                        IconPackStorage.cachedCustomPacks = null;
                                    }
                                    failure2 = new IconPackStorageResult.Success(Unit.INSTANCE);
                                }
                            } else {
                                if (simpliDirectoryDirectory2.exists()) {
                                    simpliDirectoryDirectory2.deleteRecursively();
                                }
                                synchronized (IconPackStorage.INSTANCE) {
                                    IconPackStorage.cachedCustomPacks = null;
                                    failure2 = new IconPackStorageResult.Success(Unit.INSTANCE);
                                }
                            }
                        }
                    } else {
                        boolean moveFailed = false;
                        try {
                            simpliDirectoryCreateTempCacheDirectory.moveTo(file2, OverwritePolicy.ERROR);
                        } catch (Throwable th) {
                            moveFailed = true;
                        }
                        if (moveFailed) {
                            boolean copyFailed = false;
                            try {
                                simpliDirectoryCreateTempCacheDirectory.copyTo(file2, OverwritePolicy.ERROR);
                            } catch (Throwable th) {
                                copyFailed = true;
                            }
                            if (copyFailed) {
                                if (simpliDirectoryDirectory2.exists()) {
                                    if (simpliDirectoryDirectory.exists()) {
                                        simpliDirectoryDirectory.deleteRecursively();
                                    }
                                    simpliDirectoryDirectory2.moveTo(file2, OverwritePolicy.ERROR);
                                }
                                failure2 = new IconPackStorageResult.Failure(IconPackStorageError.STORAGE_ERROR);
                            } else {
                                if (simpliDirectoryDirectory2.exists()) {
                                    simpliDirectoryDirectory2.deleteRecursively();
                                }
                                synchronized (IconPackStorage.INSTANCE) {
                                    IconPackStorage.cachedCustomPacks = null;
                                    failure2 = new IconPackStorageResult.Success(Unit.INSTANCE);
                                }
                            }
                        } else {
                            if (simpliDirectoryDirectory2.exists()) {
                                simpliDirectoryDirectory2.deleteRecursively();
                            }
                            synchronized (IconPackStorage.INSTANCE) {
                                IconPackStorage.cachedCustomPacks = null;
                                failure2 = new IconPackStorageResult.Success(Unit.INSTANCE);
                            }
                        }
                    }
                    iconPackStorage.deleteDirectoryIfExists(simpliDirectoryCreateTempCacheDirectory);
                    return failure2;
                } catch (Throwable th4) {
                    iconPackStorage.deleteDirectoryIfExists(simpliDirectoryCreateTempCacheDirectory);
                    throw th4;
                }
            } catch (IconPackStorageException e) {
                FileLog.e("Pack installation failed: " + e.getError());
                failure = new IconPackStorageResult.Failure(e.getError());
                return failure;
            } catch (Exception e2) {
                FileLog.e("Pack installation failed", e2);
                failure = new IconPackStorageResult.Failure(IconPackStorage.INSTANCE.errorFromException(e2));
                return failure;
            }
        }
    }

    public final Object installPack(File file, Continuation<? super IconPackStorageResult<Unit>> continuation) {
        return BuildersKt.withContext(Dispatchers.getIO(), (Function2) new C01502(file, null), continuation);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.icons.IconPackStorage$parsePackFromZip$2, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u0003H\n"}, d2 = {"<anonymous>", "Lcom/exteragram/messenger/icons/IconPackStorageResult;", "Lcom/exteragram/messenger/icons/IconPack;", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.icons.IconPackStorage$parsePackFromZip$2", f = "IconPackStorage.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class C01512 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super IconPackStorageResult<? extends IconPack>>, Object> {
        final /* synthetic */ File $file;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01512(File file, Continuation continuation) {
            super(2, (Continuation) continuation);
            this.$file = file;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new C01512(this.$file, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public /* bridge */ /* synthetic */ Object invoke(CoroutineScope coroutineScope, Continuation<? super IconPackStorageResult<? extends IconPack>> continuation) {
            return invoke2(coroutineScope, (Continuation<? super IconPackStorageResult<IconPack>>) continuation);
        }

        /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
        public final Object invoke2(CoroutineScope coroutineScope, Continuation<? super IconPackStorageResult<IconPack>> continuation) {
            return ((C01512) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            if (this.label != 0) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
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
        return BuildersKt.withContext(Dispatchers.getIO(), (Function2) new C01512(file, null), continuation);
    }
}
