private void awaitFileLoadOperation(CountDownLatch latch, boolean ignoreInterruption) {
        try {
            latch.await();
        } catch (Exception e) {
            FileLog.e(e, false);
            if (ignoreInterruption) awaitFileLoadOperation(latch, false);
        }
    }

    private void checkDownloadQueue(FileLoadOperation operation, FileLoaderPriorityQueue queue) {
        checkDownloadQueue(operation, queue, 0);
    }

    private void checkDownloadQueue(FileLoadOperation operation, FileLoaderPriorityQueue queue, long delay) {
        fileLoaderQueue.postRunnable(() -> {
            if (queue.remove(operation)) {
                loadOperationPaths.remove(operation.getFileName());
                queue.checkLoadingOperations(operation.isStory);
            }
        }, delay);
    }

    public void setDelegate(FileLoaderDelegate fileLoaderDelegate) {
        delegate = fileLoaderDelegate;
    }

    public static String getMessageFileName(TLRPC.Message message) {
        if (message == null) {
            return "";
        }
        if (message instanceof TLRPC.TL_messageService) {
            if (message.action.photo != null) {
                ArrayList<TLRPC.PhotoSize> sizes = message.action.photo.sizes;
                if (sizes.size() > 0) {
                    TLRPC.PhotoSize sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull != null) {
                        return getAttachFileName(sizeFull);
                    }
                }
            }
        } else {
            if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaDocument) {
                return getAttachFileName(MessageObject.getMedia(message).document);
            } else if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> sizes = MessageObject.getMedia(message).photo.sizes;
                if (sizes.size() > 0) {
                    TLRPC.PhotoSize sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize(true), false, null, true);
                    if (sizeFull != null) {
                        return getAttachFileName(sizeFull);
                    }
                }
            } else if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
                if (MessageObject.getMedia(message).webpage.document != null) {
                    return getAttachFileName(MessageObject.getMedia(message).webpage.document);
                } else if (MessageObject.getMedia(message).webpage.photo != null) {
                    ArrayList<TLRPC.PhotoSize> sizes = MessageObject.getMedia(message).webpage.photo.sizes;
                    if (sizes.size() > 0) {
                        TLRPC.PhotoSize sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                        if (sizeFull != null) {
                            return getAttachFileName(sizeFull);
                        }
                    }
                }
            } else if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaInvoice) {
                TLRPC.WebDocument document = ((TLRPC.TL_messageMediaInvoice) MessageObject.getMedia(message)).webPhoto;
                if (document != null) {
                    return Utilities.MD5(document.url) + "." + ImageLoader.getHttpUrlExtension(document.url, getMimeTypePart(document.mime_type));
                }
            }
        }
        return "";
    }

    public File getPathToMessage(TLRPC.Message message) {
        return getPathToMessage(message, true);
    }

    public File getPathToMessage(TLRPC.Message message, boolean useFileDatabaseQueue) {
        return getPathToMessage(message, false, useFileDatabaseQueue);
    }

    public File getPathToMessage(TLRPC.Message message, boolean forceCache, boolean useFileDatabaseQueue) {
        if (message == null) {
            return new File("");
        }
        if (message instanceof TLRPC.TL_messageService) {
            if (message.action.photo != null) {
                ArrayList<TLRPC.PhotoSize> sizes = message.action.photo.sizes;
                if (sizes.size() > 0) {
                    TLRPC.PhotoSize sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                    if (sizeFull != null) {
                        return getPathToAttach(sizeFull, null, forceCache, useFileDatabaseQueue);
                    }
                }
            }
        } else {
            if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaDocument) {
                return getPathToAttach(MessageObject.getMedia(message).document, null, forceCache || MessageObject.getMedia(message).ttl_seconds != 0, useFileDatabaseQueue);
            } else if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> sizes = MessageObject.getMedia(message).photo.sizes;
                if (sizes.size() > 0) {
                    TLRPC.PhotoSize sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize(true), false, null, true);
                    if (sizeFull != null) {
                        return getPathToAttach(sizeFull, null, forceCache || MessageObject.getMedia(message).ttl_seconds != 0, useFileDatabaseQueue);
                    }
                }
            } else if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
                if (MessageObject.getMedia(message).webpage.document != null) {
                    return getPathToAttach(MessageObject.getMedia(message).webpage.document, null, forceCache, useFileDatabaseQueue);
                } else if (MessageObject.getMedia(message).webpage.photo != null) {
                    ArrayList<TLRPC.PhotoSize> sizes = MessageObject.getMedia(message).webpage.photo.sizes;
                    if (sizes.size() > 0) {
                        TLRPC.PhotoSize sizeFull = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                        if (sizeFull != null) {
                            return getPathToAttach(sizeFull, null, forceCache, useFileDatabaseQueue);
                        }
                    }
                }
            } else if (MessageObject.getMedia(message) instanceof TLRPC.TL_messageMediaInvoice) {
                return getPathToAttach(((TLRPC.TL_messageMediaInvoice) MessageObject.getMedia(message)).photo, null, true, useFileDatabaseQueue);
            }
        }
        return new File("");
    }

    public File getPathToAttach(TLObject attach) {
        return getPathToAttach(attach, null, false);
    }

    public File getPathToAttach(TLObject attach, boolean forceCache) {
        return getPathToAttach(attach, null, forceCache);
    }

    public File getPathToAttach(TLObject attach, String ext, boolean forceCache) {
        return getPathToAttach(attach, null, ext, forceCache, true);
    }

    public File getPathToAttach(TLObject attach, String ext, boolean forceCache, boolean useFileDatabaseQueue) {
        return getPathToAttach(attach, null, ext, forceCache, useFileDatabaseQueue);
    }

    /**
     * Return real file name. Used before file.exist()
     */
    public File getPathToAttach(TLObject attach, String size, String ext, boolean forceCache, boolean useFileDatabaseQueue) {
        File dir = null;
        long documentId = 0;
        int dcId = 0;
        int type = 0;
        if (forceCache) {
            dir = getDirectory(MEDIA_DIR_CACHE);
        } else {
            if (attach instanceof TLRPC.Document) {
                TLRPC.Document document = (TLRPC.Document) attach;
                if (!TextUtils.isEmpty(document.localPath)) {
                    return new File(document.localPath);
                }
                if (document.key != null) {
                    type = MEDIA_DIR_CACHE;
                } else {
                    if (MessageObject.isVoiceDocument(document)) {
                        type = MEDIA_DIR_AUDIO;
                    } else if (MessageObject.isVideoDocument(document)) {
                        type = MEDIA_DIR_VIDEO;
                    } else {
                        type = MEDIA_DIR_DOCUMENT;
                    }
                }
                documentId = document.id;
                dcId = document.dc_id;
                dir = getDirectory(type);
            } else if (attach instanceof TLRPC.Photo) {
                TLRPC.PhotoSize photoSize = getClosestPhotoSizeWithSize(((TLRPC.Photo) attach).sizes, AndroidUtilities.getPhotoSize(true));
                return getPathToAttach(photoSize, ext, false, useFileDatabaseQueue);
            } else if (attach instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize photoSize = (TLRPC.PhotoSize) attach;
                if (photoSize instanceof TLRPC.TL_photoStrippedSize || photoSize instanceof TLRPC.TL_photoPathSize) {
                    dir = null;
                } else if (photoSize.location == null || photoSize.location.key != null || photoSize.location.volume_id == Integer.MIN_VALUE && photoSize.location.local_id < 0 || photoSize.size < 0) {
                    dir = getDirectory(type = MEDIA_DIR_CACHE);
                } else {
                    dir = getDirectory(type = MEDIA_DIR_IMAGE);
                }
                documentId = photoSize.location.volume_id;
                dcId = photoSize.location.dc_id + (photoSize.location.local_id << 16);
            } else if (attach instanceof TLRPC.TL_videoSize) {
                TLRPC.TL_videoSize videoSize = (TLRPC.TL_videoSize) attach;
                if (videoSize.location == null || videoSize.location.key != null || videoSize.location.volume_id == Integer.MIN_VALUE && videoSize.location.local_id < 0 || videoSize.size < 0) {
                    dir = getDirectory(type = MEDIA_DIR_CACHE);
                } else {
                    dir = getDirectory(type = MEDIA_DIR_IMAGE);
                }
                documentId = videoSize.location.volume_id;
                dcId = videoSize.location.dc_id + (videoSize.location.local_id << 16);
            } else if (attach instanceof TLRPC.FileLocation) {
                TLRPC.FileLocation fileLocation = (TLRPC.FileLocation) attach;
                if (fileLocation.key != null || fileLocation.volume_id == Integer.MIN_VALUE && fileLocation.local_id < 0) {
                    dir = getDirectory(MEDIA_DIR_CACHE);
                } else {
                    documentId = fileLocation.volume_id;
                    dcId = fileLocation.dc_id + (fileLocation.local_id << 16);
                    dir = getDirectory(type = MEDIA_DIR_IMAGE);
                }
            } else if (attach instanceof TLRPC.UserProfilePhoto || attach instanceof TLRPC.ChatPhoto) {
                if (size == null) {
                    size = "s";
                }
                if ("s".equals(size)) {
                    dir = getDirectory(MEDIA_DIR_CACHE);
                } else {
                    dir = getDirectory(MEDIA_DIR_IMAGE);
                }
            } else if (attach instanceof WebFile) {
                WebFile document = (WebFile) attach;
                if (document.mime_type.startsWith("image/")) {
                    dir = getDirectory(MEDIA_DIR_IMAGE);
                } else if (document.mime_type.startsWith("audio/")) {
                    dir = getDirectory(MEDIA_DIR_AUDIO);
                } else if (document.mime_type.startsWith("video/")) {
                    dir = getDirectory(MEDIA_DIR_VIDEO);
                } else {
                    dir = getDirectory(MEDIA_DIR_DOCUMENT);
                }
            } else if (attach instanceof TLRPC.TL_secureFile || attach instanceof SecureDocument) {
                dir = getDirectory(MEDIA_DIR_CACHE);
            }
        }
        if (dir == null) {
            return new File("");
        }
        if (documentId != 0) {
            String path = getInstance(UserConfig.selectedAccount).getFileDatabase().getPath(documentId, dcId, type, useFileDatabaseQueue);
            if (path != null) {
                return new File(path);
            }
        }
        return new File(dir, getAttachFileName(attach, ext));
    }

    public FilePathDatabase getFileDatabase() {
        return filePathDatabase;
    }

    public static TLRPC.TL_photoStrippedSize getStrippedPhotoSize(ArrayList<TLRPC.PhotoSize> sizes) {
        if (sizes == null) return null;
        for (int i = 0; i < sizes.size(); ++i) {
            final TLRPC.PhotoSize photoSize = sizes.get(i);
            if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                return (TLRPC.TL_photoStrippedSize) photoSize;
            }
        }
        return null;
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side) {
        return getClosestPhotoSizeWithSize(sizes, side, false);
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side, boolean byMinSide) {
        return getClosestPhotoSizeWithSize(sizes, side, byMinSide, null, false);
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side, boolean byMinSide, TLRPC.PhotoSize toIgnore, boolean ignoreStripped) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        int lastSide = 0;
        TLRPC.PhotoSize closestObject = null;
        for (int a = 0; a < sizes.size(); a++) {
            TLRPC.PhotoSize obj = sizes.get(a);
            if (obj == null || obj == toIgnore || obj instanceof TLRPC.TL_photoSizeEmpty || obj instanceof TLRPC.TL_photoPathSize || ignoreStripped && obj instanceof TLRPC.TL_photoStrippedSize) {
                continue;
            }
            if (byMinSide) {
                int currentSide = Math.min(obj.h, obj.w);
                if (
                    closestObject == null ||
                    side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE ||
                    obj instanceof TLRPC.TL_photoCachedSize || side > lastSide && lastSide < currentSide
                ) {
                    closestObject = obj;
                    lastSide = currentSide;
                }
            } else {
                int currentSide = Math.max(obj.w, obj.h);
                if (
                    closestObject == null ||
                    side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE ||
                    obj instanceof TLRPC.TL_photoCachedSize ||
                    currentSide <= side && lastSide < currentSide
                ) {
                    closestObject = obj;
                    lastSide = currentSide;
                }
            }
        }
        return closestObject;
    }

    public static TLRPC.VideoSize getClosestVideoSizeWithSize(ArrayList<TLRPC.VideoSize> sizes, int side) {
        return getClosestVideoSizeWithSize(sizes, side, false);
    }

    public static TLRPC.VideoSize getClosestVideoSizeWithSize(ArrayList<TLRPC.VideoSize> sizes, int side, boolean byMinSide) {
        return getClosestVideoSizeWithSize(sizes, side, byMinSide, false);
    }

    public static TLRPC.VideoSize getClosestVideoSizeWithSize(ArrayList<TLRPC.VideoSize> sizes, int side, boolean byMinSide, boolean ignoreStripped) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        int lastSide = 0;
        TLRPC.VideoSize closestObject = null;
        for (int a = 0; a < sizes.size(); a++) {
            TLRPC.VideoSize obj = sizes.get(a);
            if (obj == null || obj instanceof TLRPC.TL_videoSizeEmojiMarkup || obj instanceof TLRPC.TL_videoSizeStickerMarkup) {
                continue;
            }
            if (byMinSide) {
                int currentSide = Math.min(obj.h, obj.w);
                if (closestObject == null ||
                                side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE ||
                                side > lastSide && lastSide < currentSide) {
                    closestObject = obj;
                    lastSide = currentSide;
                }
            } else {
                int currentSide = Math.max(obj.w, obj.h);
                if (
                        closestObject == null ||
                                side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE ||
                                currentSide <= side && lastSide < currentSide
                ) {
                    closestObject = obj;
                    lastSide = currentSide;
                }
            }
        }
        return closestObject;
    }

    public static TLRPC.TL_photoPathSize getPathPhotoSize(ArrayList<TLRPC.PhotoSize> sizes) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        for (int a = 0; a < sizes.size(); a++) {
            TLRPC.PhotoSize obj = sizes.get(a);
            if (obj instanceof TLRPC.TL_photoPathSize) {
                continue;
            }
            return (TLRPC.TL_photoPathSize) obj;
        }
        return null;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf('.') + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static String fixFileName(String fileName) {
        if (fileName != null) {
            fileName = fileName.replaceAll("[\u0001-\u001f<>\u202E:\"/\\\\|?*\u007f]+", "").trim();
        }
        return fileName;
    }

    public static String getDocumentFileName(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        if (document.file_name_fixed != null) {
            return document.file_name_fixed;
        }
        String fileName = null;
        if (document != null) {
            if (document.file_name != null) {
                fileName = document.file_name;
            } else {
                for (int a = 0; a < document.attributes.size(); a++) {
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(a);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeFilename) {
                        fileName = documentAttribute.file_name;
                    }
                }
            }
        }
        fileName = fixFileName(fileName);
        return fileName != null ? fileName : "";
    }

    public static String getMimeTypePart(String mime) {
        int index;
        if ((index = mime.lastIndexOf('/')) != -1) {
            return mime.substring(index + 1);
        }
        return "";
    }

    public static String getExtensionByMimeType(String mime) {
        if (mime != null) {
            switch (mime) {
                case "video/mp4":
                    return ".mp4";
                case "video/x-matroska":
                    return ".mkv";
                case "audio/ogg":
                    return ".ogg";
            }
        }
        return "";
    }

    public static File getInternalCacheDir() {
        return ApplicationLoader.applicationContext.getCacheDir();
    }

    public static String getDocumentExtension(TLRPC.Document document) {
        String fileName = getDocumentFileName(document);
        int idx = fileName.lastIndexOf('.');
        String ext = null;
        if (idx != -1) {
            ext = fileName.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0) {
            ext = document.mime_type;
        }
        if (ext == null) {
            ext = "";
        }
        ext = ext.toUpperCase();
        return ext;
    }

    public static String getAttachFileName(TLObject attach) {
        return getAttachFileName(attach, null);
    }

    public static String getAttachFileName(TLObject attach, String ext) {
        return getAttachFileName(attach, null, ext);
    }

    /**
     * file hash. contains docId, dcId, ext.
     */
    public static String getAttachFileName(TLObject attach, String size, String ext) {
        if (attach instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) attach;
            String docExt;
            docExt = getDocumentFileName(document);
            int idx;
            if ((idx = docExt.lastIndexOf('.')) == -1) {
                docExt = "";
            } else {
                docExt = docExt.substring(idx);
            }
            if (docExt.length() <= 1) {
                docExt = getExtensionByMimeType(document.mime_type);
            }
            if (docExt.length() > 1) {
                return document.dc_id + "_" + document.id + docExt;
            } else {
                return document.dc_id + "_" + document.id;
            }
        } else if (attach instanceof SecureDocument) {
            SecureDocument secureDocument = (SecureDocument) attach;
            return secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id + ".jpg";
        } else if (attach instanceof TLRPC.TL_secureFile) {
            TLRPC.TL_secureFile secureFile = (TLRPC.TL_secureFile) attach;
            return secureFile.dc_id + "_" + secureFile.id + ".jpg";
        } else if (attach instanceof WebFile) {
            WebFile document = (WebFile) attach;
            return Utilities.MD5(document.url) + "." + ImageLoader.getHttpUrlExtension(document.url, getMimeTypePart(document.mime_type));
        } else if (attach instanceof TLRPC.PhotoSize) {
            TLRPC.PhotoSize photo = (TLRPC.PhotoSize) attach;
            if (photo.location == null || photo.location instanceof TLRPC.TL_fileLocationUnavailable) {
                return "";
            }
            return photo.location.volume_id + "_" + photo.location.local_id + "." + (ext != null ? ext : "jpg");
        } else if (attach instanceof TLRPC.TL_videoSize) {
            TLRPC.TL_videoSize video = (TLRPC.TL_videoSize) attach;
            if (video.location == null || video.location instanceof TLRPC.TL_fileLocationUnavailable) {
                return "";
            }
            return video.location.volume_id + "_" + video.location.local_id + "." + (ext != null ? ext : "mp4");
        } else if (attach instanceof TLRPC.FileLocation) {
            if (attach instanceof TLRPC.TL_fileLocationUnavailable) {
                return "";
            }
            TLRPC.FileLocation location = (TLRPC.FileLocation) attach;
            return location.volume_id + "_" + location.local_id + "." + (ext != null ? ext : "jpg");
        } else if (attach instanceof TLRPC.UserProfilePhoto) {
            if (size == null) {
                size = "s";
            }
            TLRPC.UserProfilePhoto location = (TLRPC.UserProfilePhoto) attach;
            if (location.photo_small != null) {
                if ("s".equals(size)) {
                    return getAttachFileName(location.photo_small, ext);
                } else {
                    return getAttachFileName(location.photo_big, ext);
                }
            } else {
                return location.photo_id + "_" + size + "." + (ext != null ? ext : "jpg");
            }
        } else if (attach instanceof TLRPC.ChatPhoto) {
            TLRPC.ChatPhoto location = (TLRPC.ChatPhoto) attach;
            if (location.photo_small != null) {
                if ("s".equals(size)) {
                    return getAttachFileName(location.photo_small, ext);
                } else {
                    return getAttachFileName(location.photo_big, ext);
                }
            } else {
                return location.photo_id + "_" + size + "." + (ext != null ? ext : "jpg");
            }
        }
        return "";
    }

    public void deleteFiles(final ArrayList<File> files, final int type) {
        if (files == null || files.isEmpty()) {
            return;
        }
        fileLoaderQueue.postRunnable(() -> {
            for (int a = 0; a < files.size(); a++) {
                File file = files.get(a);
                File encrypted = new File(file.getAbsolutePath() + ".enc");
                if (encrypted.exists()) {
                    try {
                        if (!encrypted.delete()) {
                            encrypted.deleteOnExit();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    try {
                        File key = new File(FileLoader.getInternalCacheDir(), file.getName() + ".enc.key");
                        if (!key.delete()) {
                            key.deleteOnExit();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (file.exists()) {
                    try {
                        if (!file.delete()) {
                            file.deleteOnExit();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                try {
                    File qFile = new File(file.getParentFile(), "q_" + file.getName());
                    if (qFile.exists()) {
                        if (!qFile.delete()) {
                            qFile.deleteOnExit();
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (type == 2) {
                ImageLoader.getInstance().clearMemory();
            }
        });
    }

    public static boolean isVideoMimeType(String mime) {
        return "video/mp4".equals(mime) || SharedConfig.streamMkv && "video/x-matroska".equals(mime);
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        return copyFile(sourceFile, destFile, -1);
    }

    public static boolean copyFile(InputStream sourceFile, File destFile, int maxSize) throws IOException {
        FileOutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        int len;
        int totalLen = 0;
        while ((len = sourceFile.read(buf)) > 0) {
            Thread.yield();
            out.write(buf, 0, len);
            totalLen += len;
            if (maxSize > 0 && totalLen >= maxSize) {
                break;
            }
        }
        out.getFD().sync();
        out.close();
        return true;
    }

    public static boolean isSamePhoto(TLObject photo1, TLObject photo2) {
        if (photo1 == null && photo2 != null || photo1 != null && photo2 == null) {
            return false;
        }
        if (photo1 == null && photo2 == null) {
            return true;
        }
        if (photo1.getClass() != photo2.getClass()) {
            return false;
        }
        if (photo1 instanceof TLRPC.UserProfilePhoto) {
            TLRPC.UserProfilePhoto p1 = (TLRPC.UserProfilePhoto) photo1;
            TLRPC.UserProfilePhoto p2 = (TLRPC.UserProfilePhoto) photo2;
            return p1.photo_id == p2.photo_id;
        } else if (photo1 instanceof TLRPC.ChatPhoto) {
            TLRPC.ChatPhoto p1 = (TLRPC.ChatPhoto) photo1;
            TLRPC.ChatPhoto p2 = (TLRPC.ChatPhoto) photo2;
            return p1.photo_id == p2.photo_id;
        }
        return false;
    }

    public static boolean isSamePhoto(TLRPC.FileLocation location, TLRPC.Photo photo) {
        if (location == null || !(photo instanceof TLRPC.TL_photo)) {
            return false;
        }
        for (int b = 0, N = photo.sizes.size(); b < N; b++) {
            TLRPC.PhotoSize size = photo.sizes.get(b);
            if (size.location != null && size.location.local_id == location.local_id && size.location.volume_id == location.volume_id) {
                return true;
            }
        }
        if (-location.volume_id == photo.id) {
            return true;
        }
        return false;
    }

    public static long getPhotoId(TLObject object) {
        if (object instanceof TLRPC.Photo) {
            return ((TLRPC.Photo) object).id;
        } else if (object instanceof TLRPC.ChatPhoto) {
            return ((TLRPC.ChatPhoto) object).photo_id;
        } else if (object instanceof TLRPC.UserProfilePhoto) {
            return ((TLRPC.UserProfilePhoto) object).photo_id;
        }
        return 0;
    }

    public void getCurrentLoadingFiles(ArrayList<MessageObject> currentLoadingFiles) {
        currentLoadingFiles.clear();
        currentLoadingFiles.addAll(getDownloadController().downloadingFiles);
        for (int i = 0; i < currentLoadingFiles.size(); i++) {
            currentLoadingFiles.get(i).isDownloadingFile = true;
        }
    }

    public void getRecentLoadingFiles(ArrayList<MessageObject> recentLoadingFiles) {
        recentLoadingFiles.clear();
        recentLoadingFiles.addAll(getDownloadController().recentDownloadingFiles);
        for (int i = 0; i < recentLoadingFiles.size(); i++) {
            recentLoadingFiles.get(i).isDownloadingFile = true;
        }
    }

    public void checkCurrentDownloadsFiles() {
        ArrayList<MessageObject> messagesToRemove = new ArrayList<>();
        ArrayList<MessageObject> messageObjects = new ArrayList<>(getDownloadController().recentDownloadingFiles);
        for (int i = 0; i < messageObjects.size(); i++) {
            messageObjects.get(i).checkMediaExistance();
            if (messageObjects.get(i).mediaExists) {
                messagesToRemove.add(messageObjects.get(i));
            }
        }
        if (!messagesToRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(() -> {
                getDownloadController().recentDownloadingFiles.removeAll(messagesToRemove);
                getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged);
            });
        }
    }

    /**
     * optimezed for bulk messages
     */
    public void checkMediaExistance(ArrayList<MessageObject> messageObjects) {
        getFileDatabase().checkMediaExistance(messageObjects);
    }

    public interface FileResolver {
        File getFile();
    }

    public void clearRecentDownloadedFiles() {
        getDownloadController().clearRecentDownloadedFiles();
    }

    public void clearFilePaths() {
        filePathDatabase.clear();
    }

    public static boolean checkUploadFileSize(int currentAccount, long length) {
        boolean premium = AccountInstance.getInstance(currentAccount).getUserConfig().isPremium();
        if (length < DEFAULT_MAX_FILE_SIZE || (length < DEFAULT_MAX_FILE_SIZE_PREMIUM && premium)) {
            return true;
        }
        return false;
    }

    private static class LoadOperationUIObject {
        Runnable loadInternalRunnable;
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        long l = 0;
        for (int i = 0; i < 8; i++) {
            l <<= 8;
            l ^= bytes[i] & 0xFF;
        }
        return l;
    }

    Runnable dumpFilesQueueRunnable = () -> {
        for (int i = 0; i < smallFilesQueue.length; i++) {
            if (smallFilesQueue[i].getCount() > 0 || largeFilesQueue[i].getCount() > 0) {
                FileLog.d("download queue: dc" + (i + 1) + " account=" + currentAccount + " small_operations=" + smallFilesQueue[i].getCount() + " large_operations=" + largeFilesQueue[i].getCount());
//                if (!largeFilesQueue[i].allOperations.isEmpty()) {
//                    largeFilesQueue[i].allOperations.get(0).dump();
//                }
            }
        }
        dumpFilesQueue();
    };

    public void dumpFilesQueue() {
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        fileLoaderQueue.cancelRunnable(dumpFilesQueueRunnable);
        fileLoaderQueue.postRunnable(dumpFilesQueueRunnable, 10000);
    }

    public void uploadFile(final String path, Utilities.Callback<TLRPC.InputFile> done) {
        NotificationCenter.NotificationCenterDelegate[] observer = new NotificationCenter.NotificationCenterDelegate[1];
        final Runnable unlisten = () -> {
            getNotificationCenter().removeObserver(observer[0], NotificationCenter.fileUploaded);
            getNotificationCenter().removeObserver(observer[0], NotificationCenter.fileUploadFailed);
        };
        observer[0] = (id, account, args) -> {
            if (id == NotificationCenter.fileUploaded) {
                if (args[0] == path) {
                    done.run((TLRPC.InputFile) args[1]);
                    unlisten.run();
                }
            } else if (id == NotificationCenter.fileUploadFailed) {
                if (args[0] == path) {
                    done.run(null);
                    unlisten.run();
                }
            }
        };
        getNotificationCenter().addObserver(observer[0], NotificationCenter.fileUploaded);
        getNotificationCenter().addObserver(observer[0], NotificationCenter.fileUploadFailed);
        uploadFile(path, false, false, ConnectionsManager.FileTypeFile);
    }
}
