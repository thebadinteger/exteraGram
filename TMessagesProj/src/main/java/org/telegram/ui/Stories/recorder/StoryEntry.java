package org.telegram.ui.Stories.recorder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.video.MediaCodecVideoConvertor;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.RLottieNative;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StoryEntry {

    public static final int MAX_ENTRIES = 10;
    public final int currentAccount = UserConfig.selectedAccount;

    public long draftId;
    public boolean isDraft;
    public long draftDate;

    public long editStoryPeerId;
    public int editStoryId;
    public boolean isEdit;
    public boolean isEditSaved;
    public double fileDuration = -1;
    public boolean editedMedia, editedCaption, editedPrivacy;
    public ArrayList<TL_stories.MediaArea> editedMediaAreas;

    public boolean isRepost;
    public boolean isShare;
    public CharSequence repostPeerName;
    public TLRPC.Peer repostPeer;
    public int repostStoryId;
    public String repostCaption;
    public TLRPC.MessageMedia repostMedia;

    public boolean isRepostMessage;
    public ArrayList<MessageObject> messageObjects;

    public boolean isError;
    public TLRPC.TL_error error;

    public String audioPath;
    public TLRPC.InputDocument audioDocument;
    public String audioAuthor, audioTitle;
    public long audioDuration;
    public long audioOffset;
    public float audioLeft, audioRight = 1;
    public float audioVolume = 1;

    public long editDocumentId;
    public long editPhotoId;
    public long editExpireDate;

    public boolean isVideo;
    public File file;
    public boolean fileDeletable;
    public String thumbPath;
    public Bitmap thumbPathBitmap;
    public float videoVolume = 1f;
    public int orientation, invert;

    public CollageLayout collage;
    public ArrayList<StoryEntry> collageContent;
    public boolean videoLoop = false;
    public float videoLeft = 0f, videoRight = 1f;
    public long videoOffset;

    public boolean muted;
    public float left, right = 1;

    public boolean isEditingCover;
    public TLRPC.Document editingCoverDocument;
    public Utilities.Callback<Utilities.Callback<TLRPC.Document>> updateDocumentRef;
    public long cover = -1;
    public boolean coverSet;
    public Bitmap coverBitmap;

    public long duration;

    public int resultWidth = 720;
    public int resultHeight = 1280;

    public int width, height;
    public MediaController.CropState crop;
    // matrix describes transformations from width x height to resultWidth x resultHeight
    public final Matrix matrix = new Matrix();

    public File round;
    public String roundThumb;
    public long roundDuration;
    public long roundOffset;
    public float roundLeft, roundRight = 1;
    public float roundVolume = 1;

    public TLRPC.InputPeer peer;
    public HashSet<Integer> albums;

    public Drawable backgroundDrawable;
    public boolean isDark = Theme.isCurrentThemeDark();
    public long backgroundWallpaperPeerId = Long.MIN_VALUE; // Long.MIN_VALUE = no wallpaper
    public String backgroundWallpaperEmoticon;
    public int gradientTopColor, gradientBottomColor;

    public CharSequence caption;
    public boolean captionEntitiesAllowed = true;
    public StoryPrivacyBottomSheet.StoryPrivacy privacy;
    public final ArrayList<TLRPC.InputPrivacyRule> privacyRules = new ArrayList<>();

    public boolean pinned = true;
    public boolean allowScreenshots;

    public int period = 86400;

    public long botId;
    public String botLang = "";
    public TLRPC.InputMedia editingBotPreview;

    // share as message (postponed)
    public ArrayList<Long> shareUserIds;
    public boolean silent;
    public int scheduleDate;

    public Bitmap blurredVideoThumb;
    public File uploadThumbFile;
    public File draftThumbFile;

    // paint
    public File paintFile;
    public File paintBlurFile;
    public File paintEntitiesFile;
    public long averageDuration = 5000;
    public ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
    public List<TLRPC.InputDocument> stickers;
    public List<TLRPC.InputDocument> editStickers;
    public File messageFile;
    public File messageVideoMaskFile;
    public File backgroundFile;

    // filter
    public File filterFile;
    public MediaController.SavedFilterState filterState;

    public Bitmap thumbBitmap;
    private boolean fromCamera;

    public boolean wouldBeVideo() {
        return wouldBeVideo(mediaEntities);
    }

    public boolean wouldBeVideo(ArrayList<VideoEditedInfo.MediaEntity> mediaEntities) {
        if (isVideo) {
            return true;
        }
        if (audioPath != null) {
            return true;
        }
        if (round != null) {
            return true;
        }
        if (messageObjects != null && messageObjects.size() == 1) {
            final MessageObject messageObject = messageObjects.get(0);
            if (messageObject != null && messageObject.messageOwner != null && messageObject.messageOwner.action instanceof TLRPC.TL_messageActionStarGiftUnique) {
                return true;
            }
        }
        if (mediaEntities != null && !mediaEntities.isEmpty()) {
            for (int i = 0; i < mediaEntities.size(); ++i) {
                VideoEditedInfo.MediaEntity entity = mediaEntities.get(i);
                if (entity.type == VideoEditedInfo.MediaEntity.TYPE_STICKER) {
                    if (isAnimated(entity.document, entity.text)) {
                        return true;
                    }
                } else if ((entity.type == VideoEditedInfo.MediaEntity.TYPE_TEXT}

    public void decodeBounds(String path) {
        if (path != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                width = options.outWidth;
                height = options.outHeight;
            } catch (Exception ignore) {}
        }
        if (!isVideo) {
            int side = (int) Math.max(width, height / 16f * 9f);
//            if (side <= (480 + 720) / 2) {
//                resultWidth = 480;
//                resultHeight = 853;
//            } else
            if (side <= (720 + 1080) / 2) {
                resultWidth = 720;
                resultHeight = 1280;
            } else {
                resultWidth = 1080;
                resultHeight = 1920;
            }
        }
    }

    public void setupMatrix() {
        setupMatrix(matrix, 0);
    }

    public void setupMatrix(Matrix matrix, int rotate) {
        matrix.reset();
        int width = this.width, height = this.height;
        int or = orientation + rotate;
        matrix.postScale(invert == 1 ? -1.0f : 1.0f, invert == 2 ? -1.0f : 1.0f, width / 2f, height / 2f);
        if (or != 0) {
            matrix.postTranslate(-width / 2f, -height / 2f);
            matrix.postRotate(or);
            if (or == 90 || or == 270) {
                final int swap = height;
                height = width;
                width = swap;
            }
            matrix.postTranslate(width / 2f, height / 2f);
        }
        float scale = (float) resultWidth / width;
        if (botId != 0) {
            scale = Math.min(scale, (float) resultHeight / height);
        } else if ((float) height / (float) width > 1.29f) {
            scale = Math.max(scale, (float) resultHeight / height);
        }
        matrix.postScale(scale, scale);
        matrix.postTranslate((resultWidth - width * scale) / 2f, (resultHeight - height * scale) / 2f);
    }

    public void setupGradient(Runnable done) {
        if (isVideo && gradientTopColor == 0 && gradientBottomColor == 0) {
            if (thumbPath != null) {
                Bitmap bitmap = null;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    if (thumbPath.startsWith("vthumb://")) {
                        long id = Integer.parseInt(thumbPath.substring(9));
                        options.inJustDecodeBounds = true;
                        MediaStore.Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, options);

                        options.inSampleSize = calculateInSampleSize(options, 240, 240);
                        options.inJustDecodeBounds = false;
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        bitmap = MediaStore.Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, options);
                    } else {
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(thumbPath);

                        options.inSampleSize = calculateInSampleSize(options, 240, 240);
                        options.inJustDecodeBounds = false;
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        bitmap = BitmapFactory.decodeFile(thumbPath);
                    }
                } catch (Exception ignore) {}
                if (bitmap != null) {
                    final Bitmap finalBitmap = bitmap;
                    DominantColors.getColors(true, finalBitmap, true, colors -> {
                        gradientTopColor = colors[0];
                        gradientBottomColor = colors[1];
                        finalBitmap.recycle();

                        if (done != null) {
                            done.run();
                        }
                    });
                }
            } else if (thumbPathBitmap != null) {
                DominantColors.getColors(true, thumbPathBitmap, true, colors -> {
                    gradientTopColor = colors[0];
                    gradientBottomColor = colors[1];
                    if (done != null) {
                        done.run();
                    }
                });
            }
        }
    }

    public static StoryEntry fromVideoShoot(File file, String thumbPath, long duration) {
        StoryEntry entry = new StoryEntry();
        entry.fromCamera = true;
        entry.file = file;
        entry.fileDeletable = true;
        entry.orientation = 0;
        entry.invert = 0;
        entry.isVideo = true;
        entry.duration = duration;
        entry.thumbPath = thumbPath;
        entry.left = 0;
        entry.right = Math.min(1, 59_500f / entry.duration);
        return entry;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = (int) Math.ceil((float) height / (float) reqHeight);
            final int widthRatio = (int) Math.ceil((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        return Math.max(1, (int) Math.pow(inSampleSize, Math.floor(Math.log(inSampleSize) / Math.log(2))));
    }

    public static void setupScale(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long availableMemory = maxMemory - usedMemory;
        final boolean enoughMemory = options.outWidth * options.outHeight * 4L * 2L <= availableMemory;
        if (!enoughMemory || Math.max(options.outWidth, options.outHeight) > 4200 || SharedConfig.getDevicePerformanceClass() <= SharedConfig.PERFORMANCE_CLASS_LOW) {
//            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inScaled = true;
            options.inDensity = options.outWidth;
            options.inTargetDensity = reqWidth;
        }
    }

    public int getTotalCount() {
        if (!isVideo || isCollage() || isEdit || duration <= 0 || isRepost)
            return 1;
        final long totalDuration = (long) ((right - left) * duration);
        if (totalDuration < TimelineView.MAX_SELECT_DURATION + 9_999L)
            return 1;
        return (int) Math.ceil((float) totalDuration / TimelineView.MAX_SELECT_DURATION);
    }

    public ArrayList<StoryEntry> cutIntoEntries() {
        if (!isVideo || isCollage() || isEdit || duration <= 0 || isRepost)
            return null;
        final long totalDuration = (long) ((right - left) * duration);
        if (totalDuration < TimelineView.MAX_SELECT_DURATION + 9_999L)
            return null;

        long runDuration = 0;
        final ArrayList<StoryEntry> entries = new ArrayList<>();
        this.right = left + (float) TimelineView.MAX_SELECT_DURATION / duration;
        runDuration += TimelineView.MAX_SELECT_DURATION;
        entries.add(this);

        while (runDuration < totalDuration) {
            final long thisDuration = Math.min(TimelineView.MAX_SELECT_DURATION, totalDuration - runDuration);
            if (thisDuration < TimelineView.MIN_SELECT_DURATION) {
                break;
            }
            final StoryEntry next = this.copy(true);
            next.left = this.left + (float) runDuration / duration;
            next.right = this.left + (float) (runDuration + thisDuration) / duration;
            next.caption = "";
            runDuration += TimelineView.MAX_SELECT_DURATION;
            entries.add(next);
        }

        return entries;
    }

    public void getVideoEditedInfo(@NonNull Utilities.Callback<VideoEditedInfo> whenDone) {
        if (!wouldBeVideo()) {
            whenDone.run(null);
            return;
        }
        if (!isVideo && (resultWidth > 720 || resultHeight > 1280)) {
            float s = 720f / resultWidth;
            matrix.postScale(s, s, 0, 0);
            resultWidth = 720;
            resultHeight = 1280;
        }
        final String videoPath = file == null ? null : file.getAbsolutePath();
        final int[][] params = new int[Math.max(1, isCollage() ? collageContent.size() : 0)][AnimatedFileDrawable.PARAM_NUM_COUNT];
        params[0] = new int[AnimatedFileDrawable.PARAM_NUM_COUNT];
        Runnable fill = () -> {
            VideoEditedInfo info = new VideoEditedInfo();

            info.isStory = true;
            info.fromCamera = fromCamera;
            info.originalWidth = width;
            info.originalHeight = height;
            info.resultWidth = resultWidth;
            info.resultHeight = resultHeight;
            info.paintPath = paintFile == null ? null : paintFile.getPath();
            info.messagePath = messageFile == null ? null : messageFile.getPath();
            info.messageVideoMaskPath = messageVideoMaskFile == null ? null : messageVideoMaskFile.getPath();
            info.backgroundPath = backgroundFile == null ? null : backgroundFile.getPath();

            long generalOffset = 0;
            final int encoderBitrate = MediaController.extractRealEncoderBitrate(info.resultWidth, info.resultHeight, info.bitrate, true);
            if (isVideo && videoPath != null && !isCollage()) {
                info.originalPath = videoPath;
                info.isPhoto = false;
                info.framerate = Math.min(59, params[0][AnimatedFileDrawable.PARAM_NUM_FRAMERATE]);
                int videoBitrate = MediaController.getVideoBitrate(videoPath);
                info.originalBitrate = videoBitrate == -1 ? params[0][AnimatedFileDrawable.PARAM_NUM_BITRATE] : videoBitrate;
                if (info.originalBitrate < 1_000_000 && (mediaEntities != null && !mediaEntities.isEmpty())) {
                    info.bitrate = 2_000_000;
                    info.originalBitrate = -1;
                } else if (info.originalBitrate < 500_000) {
                    info.bitrate = 2_500_000;
                    info.originalBitrate = -1;
                } else {
                    info.bitrate = Utilities.clamp(info.originalBitrate, 3_000_000, 500_000);
                }
                FileLog.d("story bitrate, original = " + info.originalBitrate + " => " + info.bitrate);
                info.originalDuration = (duration = params[0][AnimatedFileDrawable.PARAM_NUM_DURATION]) * 1000L;
                info.startTime = (long) (left * duration) * 1000L;
                info.endTime = (long) (right * duration) * 1000L;
                info.estimatedDuration = info.endTime - info.startTime;
                info.volume = videoVolume;
                info.muted = muted;
                info.estimatedSize = (long) (params[0][AnimatedFileDrawable.PARAM_NUM_AUDIO_FRAME_SIZE] + params[0][AnimatedFileDrawable.PARAM_NUM_DURATION] / 1000.0f * encoderBitrate / 8);
                info.estimatedSize = Math.max(file.length(), info.estimatedSize);
                info.filterState = filterState;
                info.blurPath = paintBlurFile == null ? null : paintBlurFile.getPath();
            } else {
                if (filterFile != null) {
                    info.originalPath = filterFile.getAbsolutePath();
                } else {
                    info.originalPath = videoPath;
                }
                info.isPhoto = true;
                info.collage = collage;
                if (isCollage()) {
                    boolean hasVideo = false;
                    for (int i = 0; i < collageContent.size(); ++i) {
                        StoryEntry e = collageContent.get(i);
                        if (e.isVideo) {
                            hasVideo = true;
                            e.width = Math.max(e.width, params[i][AnimatedFileDrawable.PARAM_NUM_WIDTH]);
                            e.height = Math.max(e.height, params[i][AnimatedFileDrawable.PARAM_NUM_HEIGHT]);
                            e.duration = Math.max(e.duration, params[i][AnimatedFileDrawable.PARAM_NUM_DURATION]);
                        }
                    }
                    info.collageParts = VideoEditedInfo.Part.toParts(this);
                    if (!hasVideo) {
                        info.estimatedDuration = info.originalDuration = duration = averageDuration;
                    } else {
                        long maxPartDuration = 0;
                        VideoEditedInfo.Part maxPart = null;
                        for (VideoEditedInfo.Part part : info.collageParts) {
                            if (part.isVideo && part.duration > maxPartDuration) {
                                maxPartDuration = part.duration;
                                maxPart = part;
                            }
                        }
                        if (maxPart != null) {
                            info.estimatedDuration = info.originalDuration = duration = (long) (maxPart.duration * (maxPart.right - maxPart.left));
                            generalOffset = -(maxPart.offset + (long) (maxPart.left * maxPart.duration));
                            maxPart.offset = generalOffset;
                            for (VideoEditedInfo.Part part : info.collageParts) {
                                if (part.isVideo && part != maxPart) {
                                    part.offset += generalOffset;
                                }
                            }
                        }
                    }
                } else if (round != null) {
                    info.estimatedDuration = info.originalDuration = duration = (long) ((roundRight - roundLeft) * roundDuration);
                } else if (audioPath != null) {
                    info.estimatedDuration = info.originalDuration = duration = (long) ((audioRight - audioLeft) * audioDuration);
                } else {
                    info.estimatedDuration = info.originalDuration = duration = averageDuration;
                }
                info.startTime = -1;
                info.endTime = -1;
                info.muted = true;
                info.originalBitrate = -1;
                info.volume = 1f;
                info.bitrate = -1;
                info.framerate = 30;
                info.estimatedSize = (long) (duration / 1000.0f * encoderBitrate / 8);
                info.filterState = null;
            }
            info.account = currentAccount;
            info.wallpaperPeerId = backgroundWallpaperPeerId;
            info.isDark = isDark;
            info.avatarStartTime = -1;

            if (crop != null) {
                info.cropState = crop.clone();
            } else {
                info.cropState = new MediaController.CropState();
            }
            info.cropState.useMatrix = new Matrix();
            info.cropState.useMatrix.set(matrix);

            info.mediaEntities = mediaEntities;

            info.gradientTopColor = gradientTopColor;
            info.gradientBottomColor = gradientBottomColor;
            info.forceFragmenting = true;

            info.hdrInfo = hdrInfo;

            info.mixedSoundInfos.clear();
            if (isCollage() && !muted) {
                for (VideoEditedInfo.Part part : info.collageParts) {
                    if (part.isVideo && part.volume > 0.0f && !part.muted) {
                        final MediaCodecVideoConvertor.MixedSoundInfo soundInfo = new MediaCodecVideoConvertor.MixedSoundInfo(part.path);
                        soundInfo.volume = part.volume;
                        soundInfo.audioOffset = (long) (part.left * part.duration) * 1000L;
                        soundInfo.startTime = (long) (part.offset) * 1000L;
                        soundInfo.duration = (long) ((part.right - part.left) * part.duration) * 1000L;
                        info.mixedSoundInfos.add(soundInfo);
                    }
                }
            }
            if (round != null) {
                final MediaCodecVideoConvertor.MixedSoundInfo soundInfo = new MediaCodecVideoConvertor.MixedSoundInfo(round.getAbsolutePath());
                soundInfo.volume = roundVolume;
                soundInfo.audioOffset = (long) (roundLeft * roundDuration) * 1000L;
                if (isVideo) {
                    soundInfo.startTime = (long) (roundOffset - left * duration) * 1000L;
                } else {
                    soundInfo.startTime = 0;
                }
                soundInfo.startTime += generalOffset;
                if (soundInfo.startTime < 0) {
                    soundInfo.audioOffset -= soundInfo.startTime;
                    soundInfo.startTime = 0;
                }
                soundInfo.duration = (long) ((roundRight - roundLeft) * roundDuration) * 1000L;
                info.mixedSoundInfos.add(soundInfo);
            }
            if (audioPath != null) {
                final MediaCodecVideoConvertor.MixedSoundInfo soundInfo = new MediaCodecVideoConvertor.MixedSoundInfo(audioPath);
                soundInfo.volume = audioVolume;
                soundInfo.audioOffset = (long) (audioLeft * audioDuration) * 1000L;
                if (isVideo) {
                    soundInfo.startTime = (long) (audioOffset - left * duration) * 1000L;
                } else {
                    soundInfo.startTime = 0;
                }
                soundInfo.startTime += generalOffset;
                if (soundInfo.startTime < 0) {
                    soundInfo.audioOffset -= soundInfo.startTime;
                    soundInfo.startTime = 0;
                }
                soundInfo.duration = (long) ((audioRight - audioLeft) * audioDuration) * 1000L;
                info.mixedSoundInfos.add(soundInfo);
            }

            whenDone.run(info);
        };
        if (isCollage()) {
            final String[] paths = new String[collageContent.size()];
            for (int i = 0; i < collageContent.size(); ++i) {
                paths[i] = collageContent.get(i).file == null ? null : collageContent.get(i).file.getAbsolutePath();
                params[i] = new int[AnimatedFileDrawable.PARAM_NUM_COUNT];
            }
            Utilities.globalQueue.postRunnable(() -> {
                for (int i = 0; i < paths.length; ++i)
                    if (paths[i] != null)
                        AnimatedFileDrawable.getVideoInfo(paths[i], params[i], 0);
                AndroidUtilities.runOnUIThread(fill);
            });
        } else if (file == null) {
            fill.run();
        } else {
            Utilities.globalQueue.postRunnable(() -> {
                AnimatedFileDrawable.getVideoInfo(videoPath, params[0], 0);
                AndroidUtilities.runOnUIThread(fill);
            });
        }
    }

    public static File makeCacheFile(final int account, boolean video) {
        return makeCacheFile(account, video ? "mp4" : "jpg");
    }

    public static File makeCacheFile(final int account, String ext) {
        TLRPC.TL_fileLocationToBeDeprecated location = new TLRPC.TL_fileLocationToBeDeprecated();
        location.volume_id = Integer.MIN_VALUE;
        location.dc_id = Integer.MIN_VALUE;
        location.local_id = SharedConfig.getLastLocalId();
        location.file_reference = new byte[0];

        TLObject object;
        if ("mp4".equals(ext) || "webm".equals(ext)) {
            TLRPC.VideoSize videoSize = new TLRPC.TL_videoSize_layer127();
            videoSize.location = location;
            object = videoSize;
        } else {
            TLRPC.PhotoSize photoSize = new TLRPC.TL_photoSize_layer127();
            photoSize.location = location;
            object = photoSize;
        }

        return FileLoader.getInstance(account).getPathToAttach(object, ext, true);
    }

    public static class HDRInfo {

        public int colorStandard;
        public int colorRange;
        public int colorTransfer;

        public float maxlum;
        public float minlum;

        public int getHDRType() {
//            if (maxlum <= 0 && minlum <= 0) {
//                return 0;
//            } else
            if (colorStandard == MediaFormat.COLOR_STANDARD_BT2020) {
                if (colorTransfer == MediaFormat.COLOR_TRANSFER_HLG) {
                    return 1;
                } else if (colorTransfer == MediaFormat.COLOR_TRANSFER_ST2084) {
                    return 2;
                }
            }
            return 0;
        }
    }

    public HDRInfo hdrInfo;

    public void detectHDR(Utilities.Callback<HDRInfo> whenDetected) {
        if (whenDetected == null) {
            return;
        }
        if (hdrInfo != null) {
            whenDetected.run(hdrInfo);
            return;
        }
        if (!isVideo || Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            whenDetected.run(hdrInfo = new HDRInfo());
            return;
        }
        Utilities.globalQueue.postRunnable(() -> {
            try {
                HDRInfo hdrInfo;
                if (this.hdrInfo == null) {
                    hdrInfo = this.hdrInfo = new HDRInfo();
                    hdrInfo.maxlum = 1000f;
                    hdrInfo.minlum = 0.001f;
                } else {
                    hdrInfo = this.hdrInfo;
                }
                MediaExtractor extractor = new MediaExtractor();
                extractor.setDataSource(file.getAbsolutePath());
                int videoIndex = MediaController.findTrack(extractor, false);
                extractor.selectTrack(videoIndex);
                MediaFormat videoFormat = extractor.getTrackFormat(videoIndex);
                if (videoFormat.containsKey(MediaFormat.KEY_COLOR_TRANSFER)) {
                    hdrInfo.colorTransfer = videoFormat.getInteger(MediaFormat.KEY_COLOR_TRANSFER);
                }
                if (videoFormat.containsKey(MediaFormat.KEY_COLOR_STANDARD)) {
                    hdrInfo.colorStandard = videoFormat.getInteger(MediaFormat.KEY_COLOR_STANDARD);
                }
                if (videoFormat.containsKey(MediaFormat.KEY_COLOR_RANGE)) {
                    hdrInfo.colorRange = videoFormat.getInteger(MediaFormat.KEY_COLOR_RANGE);
                }
            } catch (Exception e) {
                FileLog.e(e);
            } finally {
                this.hdrInfo = hdrInfo;
                AndroidUtilities.runOnUIThread(() -> whenDetected.run(hdrInfo));
            }
        });
    }

    public void checkStickers(TL_stories.StoryItem storyItem) {
        if (storyItem == null || storyItem.media == null) {
            return;
        }
        final TLRPC.TL_messages_getAttachedStickers req = new TLRPC.TL_messages_getAttachedStickers();
        if (storyItem.media.photo != null) {
            TLRPC.Photo photo = (TLRPC.Photo) storyItem.media.photo;
            if (!photo.has_stickers) {
                return;
            }
            TLRPC.TL_inputStickeredMediaPhoto inputStickeredMediaPhoto = new TLRPC.TL_inputStickeredMediaPhoto();
            inputStickeredMediaPhoto.id = new TLRPC.TL_inputPhoto();
            inputStickeredMediaPhoto.id.id = photo.id;
            inputStickeredMediaPhoto.id.access_hash = photo.access_hash;
            inputStickeredMediaPhoto.id.file_reference = photo.file_reference;
            if (inputStickeredMediaPhoto.id.file_reference == null) {
                inputStickeredMediaPhoto.id.file_reference = new byte[0];
            }
            req.media = inputStickeredMediaPhoto;
        } else if (storyItem.media.document != null) {
            TLRPC.Document document = (TLRPC.Document) storyItem.media.document;
            if (!MessageObject.isDocumentHasAttachedStickers(document)) {
                return;
            }
            TLRPC.TL_inputStickeredMediaDocument inputStickeredMediaDocument = new TLRPC.TL_inputStickeredMediaDocument();
            inputStickeredMediaDocument.id = new TLRPC.TL_inputDocument();
            inputStickeredMediaDocument.id.id = document.id;
            inputStickeredMediaDocument.id.access_hash = document.access_hash;
            inputStickeredMediaDocument.id.file_reference = document.file_reference;
            if (inputStickeredMediaDocument.id.file_reference == null) {
                inputStickeredMediaDocument.id.file_reference = new byte[0];
            }
            req.media = inputStickeredMediaDocument;
        } else {
            return;
        }
        final RequestDelegate requestDelegate = (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            checkStickersReqId = 0;
            if (response instanceof Vector) {
                editStickers = new ArrayList<>();
                Vector vector = (Vector) response;
                for (int i = 0; i < vector.objects.size(); ++i) {
                    TLRPC.StickerSetCovered setCovered = (TLRPC.StickerSetCovered) vector.objects.get(i);
                    TLRPC.Document document = setCovered.cover;
                    if (document == null && !setCovered.covers.isEmpty()) {
                        document = setCovered.covers.get(0);
                    }
                    if (document == null && setCovered instanceof TLRPC.TL_stickerSetFullCovered) {
                        TLRPC.TL_stickerSetFullCovered fullCovered = ((TLRPC.TL_stickerSetFullCovered) setCovered);
                        if (!fullCovered.documents.isEmpty()) {
                            document = fullCovered.documents.get(0);
                        }
                    }
                    if (document != null) {
                        TLRPC.InputDocument inputDocument = new TLRPC.TL_inputDocument();
                        inputDocument.id = document.id;
                        inputDocument.access_hash = document.access_hash;
                        inputDocument.file_reference = document.file_reference;
                        editStickers.add(inputDocument);
                    }
                }
            }
        });
        checkStickersReqId = ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> {
            if (error != null && FileRefController.isFileRefError(error.text) && storyItem != null) {
                FileRefController.getInstance(currentAccount).requestReference(storyItem, req, requestDelegate);
                return;
            }
            requestDelegate.run(response, error);
        });
    }

    private int checkStickersReqId = 0;
    public void cancelCheckStickers() {
        if (checkStickersReqId != 0) {
            ConnectionsManager.getInstance(currentAccount).cancelRequest(checkStickersReqId, true);
        }
    }

    public StoryEntry copy() {
        return copy(false);
    }

    public StoryEntry copy(boolean withFiles) {
        StoryEntry newEntry = new StoryEntry();
        newEntry.draftId = draftId;
        newEntry.isDraft = isDraft;
        newEntry.draftDate = draftDate;
        newEntry.editStoryPeerId = editStoryPeerId;
        newEntry.editStoryId = editStoryId;
        newEntry.isEdit = isEdit;
        newEntry.isEditSaved = isEditSaved;
        newEntry.fileDuration = fileDuration;
        newEntry.editedMedia = editedMedia;
        newEntry.editedCaption = editedCaption;
        newEntry.editedPrivacy = editedPrivacy;
        newEntry.editedMediaAreas = editedMediaAreas;
        newEntry.isError = isError;
        newEntry.error = error;
        newEntry.audioPath = audioPath;
        newEntry.audioDocument = audioDocument;
        newEntry.audioAuthor = audioAuthor;
        newEntry.audioTitle = audioTitle;
        newEntry.audioDuration = audioDuration;
        newEntry.audioOffset = audioOffset;
        newEntry.audioLeft = audioLeft;
        newEntry.audioRight = audioRight;
        newEntry.audioVolume = audioVolume;
        newEntry.editDocumentId = editDocumentId;
        newEntry.editPhotoId = editPhotoId;
        newEntry.editExpireDate = editExpireDate;
        newEntry.isVideo = isVideo;
        newEntry.file = file;
        newEntry.fileDeletable = fileDeletable;
        if (fileDeletable) {
            newEntry.file = StoryEntry.makeCacheFile(currentAccount, ext(file));
            AndroidUtilities.copyFileSafe(file, newEntry.file);
        }
        newEntry.thumbPath = thumbPath;
        newEntry.muted = muted;
        newEntry.left = left;
        newEntry.right = right;
        newEntry.duration = duration;
        newEntry.width = width;
        newEntry.height = height;
        newEntry.resultWidth = resultWidth;
        newEntry.resultHeight = resultHeight;
        newEntry.peer = peer;
        newEntry.invert = invert;
        newEntry.matrix.set(matrix);
        newEntry.gradientTopColor = gradientTopColor;
        newEntry.gradientBottomColor = gradientBottomColor;
        newEntry.caption = caption;
        newEntry.captionEntitiesAllowed = captionEntitiesAllowed;
        newEntry.privacy = privacy;
        newEntry.privacyRules.clear();
        newEntry.privacyRules.addAll(privacyRules);
        newEntry.pinned = pinned;
        newEntry.allowScreenshots = allowScreenshots;
        newEntry.period = period;
        newEntry.shareUserIds = shareUserIds;
        newEntry.silent = silent;
        newEntry.scheduleDate = scheduleDate;
        newEntry.blurredVideoThumb = blurredVideoThumb;
        newEntry.uploadThumbFile = uploadThumbFile;
        newEntry.albums = albums;
        if (uploadThumbFile != null && uploadThumbFile.exists()) {
            newEntry.uploadThumbFile = StoryEntry.makeCacheFile(currentAccount, ext(uploadThumbFile));
            AndroidUtilities.copyFileSafe(uploadThumbFile, newEntry.uploadThumbFile);
        }
        newEntry.draftThumbFile = draftThumbFile;
        if (draftThumbFile != null && draftThumbFile.exists()) {
            newEntry.draftThumbFile = StoryEntry.makeCacheFile(currentAccount, ext(draftThumbFile));
            AndroidUtilities.copyFileSafe(draftThumbFile, newEntry.draftThumbFile);
        }
        newEntry.paintFile = paintFile;
        if (paintFile != null && paintFile.exists()) {
            newEntry.paintFile = StoryEntry.makeCacheFile(currentAccount, ext(paintFile));
            AndroidUtilities.copyFileSafe(paintFile, newEntry.paintFile);
        }
        newEntry.messageFile = messageFile;
        if (messageFile != null && messageFile.exists()) {
            newEntry.messageFile = StoryEntry.makeCacheFile(currentAccount, ext(messageFile));
            AndroidUtilities.copyFileSafe(messageFile, newEntry.messageFile);
        }
        newEntry.backgroundFile = backgroundFile;
        if (backgroundFile != null && backgroundFile.exists()) {
            newEntry.backgroundFile = StoryEntry.makeCacheFile(currentAccount, ext(backgroundFile));
            AndroidUtilities.copyFileSafe(backgroundFile, newEntry.backgroundFile);
        }
        newEntry.paintBlurFile = paintBlurFile;
        if (paintBlurFile != null && paintBlurFile.exists()) {
            newEntry.paintBlurFile = StoryEntry.makeCacheFile(currentAccount, ext(paintBlurFile));
            AndroidUtilities.copyFileSafe(paintBlurFile, newEntry.paintBlurFile);
        }
        newEntry.paintEntitiesFile = paintEntitiesFile;
        if (paintEntitiesFile != null && paintEntitiesFile.exists()) {
            newEntry.paintEntitiesFile = StoryEntry.makeCacheFile(currentAccount, ext(paintEntitiesFile));
            AndroidUtilities.copyFileSafe(paintEntitiesFile, newEntry.paintEntitiesFile);
        }
        newEntry.averageDuration = averageDuration;
        newEntry.mediaEntities = new ArrayList<>();
        if (mediaEntities != null) {
            for (int i = 0; i < mediaEntities.size(); ++i) {
                newEntry.mediaEntities.add(mediaEntities.get(i).copy());
            }
        }
        newEntry.stickers = stickers;
        newEntry.editStickers = editStickers;
        newEntry.filterFile = filterFile;
        if (filterFile != null && filterFile.exists()) {
            newEntry.filterFile = StoryEntry.makeCacheFile(currentAccount, ext(filterFile));
            AndroidUtilities.copyFileSafe(filterFile, newEntry.filterFile);
        }
        newEntry.filterState = filterState;
        newEntry.thumbBitmap = thumbBitmap;
        newEntry.fromCamera = fromCamera;
        newEntry.thumbPathBitmap = thumbPathBitmap;
        newEntry.isRepost = isRepost;
        newEntry.isShare = isShare;
        newEntry.round = round;
        newEntry.roundLeft = roundLeft;
        newEntry.roundRight = roundRight;
        newEntry.roundDuration = roundDuration;
        newEntry.roundThumb = roundThumb;
        newEntry.roundOffset = roundOffset;
        newEntry.roundVolume = roundVolume;
        newEntry.isEditingCover = isEditingCover;
        newEntry.botId = botId;
        newEntry.botLang = botLang;
        newEntry.editingBotPreview = editingBotPreview;
        newEntry.cover = cover;
        newEntry.collageContent = collageContent;
        newEntry.collage = collage;
        newEntry.videoLoop = videoLoop;
        newEntry.videoOffset = videoOffset;
        newEntry.videoVolume = videoVolume;
        return newEntry;
    }

    public static long getCoverTime(TL_stories.StoryItem storyItem) {
        if (storyItem == null) return 0;
        if (storyItem.media == null || storyItem.media.document == null) return 0;
        TLRPC.Document doc = storyItem.media.document;
        TLRPC.TL_documentAttributeVideo attr = null;
        for (int i = 0; i < doc.attributes.size(); ++i) {
            if (doc.attributes.get(i) instanceof TLRPC.TL_documentAttributeVideo) {
                attr = (TLRPC.TL_documentAttributeVideo) doc.attributes.get(i);
                break;
            }
        }
        if (attr == null) return 0;
        return (long) (attr.video_start_ts * 1000L);
    }
}
