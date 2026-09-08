package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.net.Uri;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.LongSparseArray;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import com.exteragram.messenger.ai.ui.activities.EditServiceActivity$ParsedServiceInput$$ExternalSyntheticRecord0;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.DefaultAudioSink;
import com.google.android.exoplayer2.audio.TeeAudioProcessor;
import com.google.android.exoplayer2.mediacodec.MediaCodecDecoderException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.video.ColorInfo;
import com.google.android.exoplayer2.video.SurfaceNotValidException;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.exoplayer2.video.VideoSize;
import java.io.File;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kotlin.jvm.internal.LongCompanionObject;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FourierTransform;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.chromecast.ChromecastMedia;
import org.telegram.messenger.chromecast.ChromecastMediaVariations;
import org.telegram.messenger.secretmedia.ExtendedDefaultDataSourceFactory;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Stories.recorder.StoryEntry;

@SuppressLint({"NewApi"})
public class VideoPlayer implements Player.Listener, VideoListener, AnalyticsListener, NotificationCenter.NotificationCenterDelegate {
    private static HashMap<String, Boolean> cachedSupportedCodec;
    private static int lastPlayerId;
    public boolean allowMultipleInstances;
    boolean audioDisabled;
    private ExoPlayer audioPlayer;
    private boolean audioPlayerReady;
    private String audioType;
    Handler audioUpdateHandler;
    private Uri audioUri;
    private AudioVisualizerDelegate audioVisualizerDelegate;
    private boolean autoIsOriginal;
    private boolean autoplay;
    private ExternalSubtitle currentExternalSubtitle;
    private boolean currentStreamIsHls;
    private Uri currentUri;
    private long currentVideoByteOffset;
    MediaSource.Factory dashMediaSourceFactory;
    private VideoPlayerDelegate delegate;
    private EGLContext eglParentContext;
    private long fallbackDuration;
    private long fallbackPosition;
    private boolean handleAudioFocus;
    HlsMediaSource.Factory hlsMediaSourceFactory;
    private boolean isStory;
    private boolean isStreaming;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState;
    private Looper looper;
    private boolean looping;
    private boolean loopingMediaSource;
    private ArrayList<VideoUri> manifestUris;
    private ExtendedDefaultDataSourceFactory mediaDataSourceFactory;
    private boolean mixedAudio;
    private boolean mixedPlayWhenReady;
    private Runnable onQualityChangeListener;
    public ExoPlayer player;
    public final int playerId;
    ProgressiveMediaSource.Factory progressiveMediaSourceFactory;
    private int repeatCount;
    private final ArrayList<Runnable> seekFinishedListeners;
    private int selectedQualityIndex;
    private boolean shouldPauseOther;
    SsMediaSource.Factory ssMediaSourceFactory;
    private Surface surface;
    private SurfaceView surfaceView;
    private TextureView textureView;
    private MappingTrackSelector trackSelector;
    private boolean triedReinit;
    private boolean videoPlayerReady;
    private ArrayList<Quality> videoQualities;
    private Quality videoQualityToSelect;
    private String videoType;
    private Uri videoUri;
    private DispatchQueue workerQueue;
    public static final HashSet<Integer> activePlayers = new HashSet<>();
    static int playerCounter = 0;

    public interface AudioVisualizerDelegate {
        boolean needUpdate();

        void onVisualizerUpdate(boolean z, boolean z2, float[] fArr);
    }

    public interface VideoPlayerDelegate {
        default void onCues(CueGroup cueGroup) {
        }

        void onError(VideoPlayer videoPlayer, Exception exc);

        void onRenderedFirstFrame();

        default void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
        }

        default void onSeekFinished(AnalyticsListener.EventTime eventTime) {
        }

        default void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        }

        void onStateChanged(boolean z, int i);

        boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    @Override // com.google.android.exoplayer2.Player.Listener
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override // com.google.android.exoplayer2.Player.Listener
    public void onRepeatModeChanged(int i) {
    }

    @Override // com.google.android.exoplayer2.Player.Listener
    public void onSurfaceSizeChanged(int i, int i2) {
    }

    public boolean createdWithAudioTrack() {
        return !this.audioDisabled;
    }

    public static final class ExternalSubtitle extends RecordTag {
        private final String label;
        private final String mimeType;
        private final Uri uri;

        private DataSource lambda$mediaSourceFromUri$0(long j) {
        return new OffsetDataSource(this.mediaDataSourceFactory.createDataSource(), j);
    }

    private MediaSource maybeWrapWithExternalSubtitle(MediaSource mediaSource) {
        return this.currentExternalSubtitle == null ? mediaSource : new MergingMediaSource(mediaSource, new SingleSampleMediaSource.Factory(this.mediaDataSourceFactory).createMediaSource(this.currentExternalSubtitle.toSubtitleConfiguration(), -9223372036854775807L));
    }

    public void preparePlayer(Uri uri, String str) {
        preparePlayer(uri, str, 3, 0L);
    }

    public void preparePlayer(Uri uri, String str, int i, long j) {
        this.videoQualities = null;
        this.videoQualityToSelect = null;
        this.videoUri = uri;
        this.videoType = str;
        this.audioUri = null;
        this.audioType = null;
        this.currentVideoByteOffset = j;
        boolean z = false;
        this.loopingMediaSource = false;
        this.autoIsOriginal = false;
        this.currentStreamIsHls = false;
        this.videoPlayerReady = false;
        this.mixedAudio = false;
        this.currentUri = uri;
        String scheme = uri != null ? uri.getScheme() : null;
        if (scheme != null && !scheme.startsWith("file")) {
            z = true;
        }
        this.isStreaming = z;
        ensurePlayerCreated();
        this.player.setMediaSource(mediaSourceFromUri(uri, j, str, true), true);
        this.player.prepare();
    }

    public void preparePlayer(ArrayList<Quality> arrayList, Quality quality) {
        ArrayList<Quality> arrayList2;
        this.videoQualities = arrayList;
        this.videoQualityToSelect = quality;
        this.videoUri = null;
        this.videoType = "hls";
        this.audioUri = null;
        this.audioType = null;
        this.loopingMediaSource = false;
        this.autoIsOriginal = false;
        this.videoPlayerReady = false;
        this.mixedAudio = false;
        this.currentUri = null;
        this.isStreaming = true;
        ensurePlayerCreated();
        this.currentStreamIsHls = false;
        this.selectedQualityIndex = (quality == null || (arrayList2 = this.videoQualities) == null) ? -1 : arrayList2.indexOf(quality);
        setSelectedQuality(true, quality);
        if (this.autoIsOriginal) {
            this.selectedQualityIndex = -1;
        }
    }

    public static Quality getSavedQuality(ArrayList<Quality> arrayList, MessageObject messageObject) {
        if (ExteraConfig.getPreferOriginalQuality()) {
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Quality quality = arrayList.get(i);
                i++;
                Quality quality2 = quality;
                if (quality2.original) {
                    return quality2;
                }
            }
        }
        if (messageObject == null) {
            return null;
        }
        return getSavedQuality(arrayList, messageObject.getDialogId(), messageObject.getId());
    }

    public static Quality getSavedQuality(ArrayList<Quality> arrayList, long j, int i) {
        int i2 = 0;
        String string = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).getString(j + "_" + i + "q2", _UrlKt.FRAGMENT_ENCODE_SET);
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        int size = arrayList.size();
        while (i2 < size) {
            Quality quality = arrayList.get(i2);
            i2++;
            Quality quality2 = quality;
            StringBuilder sb = new StringBuilder();
            sb.append(quality2.width);
            sb.append("x");
            sb.append(quality2.height);
            sb.append(quality2.original ? "s" : _UrlKt.FRAGMENT_ENCODE_SET);
            if (TextUtils.equals(string, sb.toString())) {
                return quality2;
            }
        }
        return null;
    }

    public static void saveQuality(Quality quality, MessageObject messageObject) {
        if (messageObject == null) {
            return;
        }
        saveQuality(quality, messageObject.getDialogId(), messageObject.getId());
    }

    public static void saveQuality(Quality quality, long j, int i) {
        SharedPreferences.Editor editorEdit = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit();
        if (quality == null) {
            editorEdit.remove(j + "_" + i + "q2");
        } else {
            String str = j + "_" + i + "q2";
            StringBuilder sb = new StringBuilder();
            sb.append(quality.width);
            sb.append("x");
            sb.append(quality.height);
            sb.append(quality.original ? "s" : _UrlKt.FRAGMENT_ENCODE_SET);
            editorEdit.putString(str, sb.toString());
        }
        editorEdit.apply();
    }

    public static void saveLooping(boolean z, MessageObject messageObject) {
        if (messageObject == null) {
            return;
        }
        ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit().putBoolean(messageObject.getDialogId() + "_" + messageObject.getId() + "loop", z).apply();
    }

    public static Boolean getLooping(MessageObject messageObject) {
        if (messageObject == null) {
            return null;
        }
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0);
        String str = messageObject.getDialogId() + "_" + messageObject.getId() + "loop";
        if (sharedPreferences.contains(str)) {
            return Boolean.valueOf(sharedPreferences.getBoolean(str, false));
        }
        return null;
    }

    public Quality getQuality(int i) {
        ArrayList<Quality> arrayList = this.videoQualities;
        if (arrayList == null) {
            return getHighestQuality(Boolean.FALSE);
        }
        if (i < 0 || i >= arrayList.size()) {
            return getHighestQuality(Boolean.FALSE);
        }
        return this.videoQualities.get(i);
    }

    public Quality getOriginalQuality() {
        for (int i = 0; i < getQualitiesCount(); i++) {
            Quality quality = getQuality(i);
            if (quality.original) {
                return quality;
            }
        }
        return null;
    }

    public Quality getHighestQuality(Boolean bool) {
        Quality quality = null;
        for (int i = 0; i < getQualitiesCount(); i++) {
            Quality quality2 = getQuality(i);
            if ((bool == null || quality2.original == bool.booleanValue()) && (quality == null || quality.width * quality.height < quality2.width * quality2.height)) {
                quality = quality2;
            }
        }
        return quality;
    }

    public int getQualitiesCount() {
        ArrayList<Quality> arrayList = this.videoQualities;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public File getLowestFile() {
        ArrayList<Quality> arrayList = this.videoQualities;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ArrayList<VideoUri> arrayList2 = this.videoQualities.get(size).uris;
                int size2 = arrayList2.size();
                int i = 0;
                while (i < size2) {
                    VideoUri videoUri = arrayList2.get(i);
                    i++;
                    VideoUri videoUri2 = videoUri;
                    if (!videoUri2.isCached()) {
                        videoUri2.updateCached(true);
                    }
                    if (videoUri2.isCached()) {
                        return new File(videoUri2.uri.getPath());
                    }
                }
            }
        }
        Uri uri = this.videoUri;
        if (uri == null || !"file".equalsIgnoreCase(uri.getScheme())) {
            return null;
        }
        return new File(this.videoUri.getPath());
    }

    public int getSelectedQuality() {
        return this.selectedQualityIndex;
    }

    public TLRPC.Document getCurrentDocument() {
        Format videoFormat;
        ArrayList<Quality> arrayList;
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null && (videoFormat = exoPlayer.getVideoFormat()) != null && videoFormat.documentId != 0 && (arrayList = this.videoQualities) != null) {
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Quality quality = arrayList.get(i);
                i++;
                ArrayList<VideoUri> arrayList2 = quality.uris;
                int size2 = arrayList2.size();
                int i2 = 0;
                while (i2 < size2) {
                    VideoUri videoUri = arrayList2.get(i2);
                    i2++;
                    VideoUri videoUri2 = videoUri;
                    if (videoUri2.docId == videoFormat.documentId) {
                        return videoUri2.document;
                    }
                }
            }
        }
        return null;
    }

    public int getCurrentQualityIndex() {
        Format videoFormat;
        if (this.selectedQualityIndex == -1) {
            try {
                if (this.autoIsOriginal) {
                    for (int i = 0; i < getQualitiesCount(); i++) {
                        if (getQuality(i).original) {
                            return i;
                        }
                    }
                }
                ExoPlayer exoPlayer = this.player;
                if (exoPlayer == null || (videoFormat = exoPlayer.getVideoFormat()) == null) {
                    return -1;
                }
                for (int i2 = 0; i2 < getQualitiesCount(); i2++) {
                    Quality quality = getQuality(i2);
                    if (!quality.original && videoFormat.width == quality.width && videoFormat.height == quality.height && videoFormat.bitrate == ((int) Math.floor(quality.uris.get(0).bitrate * 8.0d))) {
                        return i2;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
                return -1;
            }
        }
        return this.selectedQualityIndex;
    }

    private TrackSelectionOverride getQualityTrackSelection(VideoUri videoUri) {
        int i;
        try {
            int iIndexOf = this.manifestUris.indexOf(videoUri);
            MappingTrackSelector.MappedTrackInfo currentMappedTrackInfo = this.trackSelector.getCurrentMappedTrackInfo();
            for (int i2 = 0; i2 < currentMappedTrackInfo.getRendererCount(); i2++) {
                TrackGroupArray trackGroups = currentMappedTrackInfo.getTrackGroups(i2);
                for (int i3 = 0; i3 < trackGroups.length; i3++) {
                    TrackGroup trackGroup = trackGroups.get(i3);
                    for (int i4 = 0; i4 < trackGroup.length; i4++) {
                        Format format = trackGroup.getFormat(i4);
                        try {
                            i = Integer.parseInt(format.id);
                        } catch (Exception unused) {
                            i = -1;
                        }
                        if (i >= 0 && iIndexOf == i) {
                            return new TrackSelectionOverride(trackGroup, i4);
                        }
                        if (format.width == videoUri.width && format.height == videoUri.height) {
                            return new TrackSelectionOverride(trackGroup, i4);
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    @Override // com.google.android.exoplayer2.Player.Listener
    public void onCues(final CueGroup cueGroup) {
        super.onCues(cueGroup);
        if (this.delegate != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.VideoPlayer$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onCues$1(cueGroup);
                }
            });
        }
    }

    public void lambda$onPlayerError$3(PlaybackException playbackException) {
        Throwable cause = playbackException.getCause();
        if ((cause instanceof MediaCodecDecoderException) && (cause.toString().contains("av1") || cause.toString().contains("av01"))) {
            FileLog.e(playbackException);
            FileLog.e("av1 codec failed, we think this codec is not supported");
            MessagesController.getGlobalMainSettings().edit().putBoolean("unsupport_video/av01", true).commit();
            HashMap<String, Boolean> map = cachedSupportedCodec;
            if (map != null) {
                map.clear();
            }
            ArrayList<Quality> arrayListFilterByCodec = Quality.filterByCodec(this.videoQualities);
            this.videoQualities = arrayListFilterByCodec;
            if (arrayListFilterByCodec != null) {
                preparePlayer(arrayListFilterByCodec, this.videoQualityToSelect);
                return;
            }
            return;
        }
        TextureView textureView = this.textureView;
        if (textureView != null && ((!this.triedReinit && (cause instanceof MediaCodecRenderer.DecoderInitializationException)) || (cause instanceof SurfaceNotValidException))) {
            this.triedReinit = true;
            if (this.player != null) {
                ViewGroup viewGroup = (ViewGroup) textureView.getParent();
                if (viewGroup != null) {
                    int iIndexOfChild = viewGroup.indexOfChild(this.textureView);
                    viewGroup.removeView(this.textureView);
                    viewGroup.addView(this.textureView, iIndexOfChild);
                }
                DispatchQueue dispatchQueue = this.workerQueue;
                if (dispatchQueue != null) {
                    dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.VideoPlayer$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onPlayerError$2();
                        }
                    });
                    return;
                }
                this.player.clearVideoTextureView(this.textureView);
                this.player.setVideoTextureView(this.textureView);
                ArrayList<Quality> arrayList = this.videoQualities;
                if (arrayList != null) {
                    preparePlayer(arrayList, this.videoQualityToSelect);
                } else {
                    boolean z = this.loopingMediaSource;
                    Uri uri = this.videoUri;
                    if (z) {
                        preparePlayerLoop(uri, this.videoType, this.audioUri, this.audioType);
                    } else {
                        preparePlayer(uri, this.videoType);
                    }
                }
                play();
                return;
            }
            return;
        }
        VideoPlayerDelegate videoPlayerDelegate = this.delegate;
        if (videoPlayerDelegate != null) {
            videoPlayerDelegate.onError(this, playbackException);
        }
    }

    public /* synthetic */ void lambda$onPlayerError$2() {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            exoPlayer.clearVideoTextureView(this.textureView);
            this.player.setVideoTextureView(this.textureView);
            ArrayList<Quality> arrayList = this.videoQualities;
            if (arrayList != null) {
                preparePlayer(arrayList, this.videoQualityToSelect);
            } else {
                boolean z = this.loopingMediaSource;
                Uri uri = this.videoUri;
                if (z) {
                    preparePlayerLoop(uri, this.videoType, this.audioUri, this.audioType);
                } else {
                    preparePlayer(uri, this.videoType);
                }
            }
            play();
        }
    }

    @Override // com.google.android.exoplayer2.Player.Listener
    public void onVideoSizeChanged(VideoSize videoSize) {
        this.delegate.onVideoSizeChanged(videoSize.width, videoSize.height, videoSize.unappliedRotationDegrees, videoSize.pixelWidthHeightRatio);
        super.onVideoSizeChanged(videoSize);
    }

    @Override // com.google.android.exoplayer2.Player.Listener
    public void onRenderedFirstFrame() {
        this.delegate.onRenderedFirstFrame();
    }

    @Override // com.google.android.exoplayer2.video.VideoListener
    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        return this.delegate.onSurfaceDestroyed(surfaceTexture);
    }

    @Override // com.google.android.exoplayer2.video.VideoListener
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        this.delegate.onSurfaceTextureUpdated(surfaceTexture);
    }

    private void maybeReportPlayerState() {
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer == null) {
            return;
        }
        boolean playWhenReady = exoPlayer.getPlayWhenReady();
        int playbackState = this.player.getPlaybackState();
        if (this.lastReportedPlayWhenReady == playWhenReady && this.lastReportedPlaybackState == playbackState) {
            return;
        }
        this.delegate.onStateChanged(playWhenReady, playbackState);
        this.lastReportedPlayWhenReady = playWhenReady;
        this.lastReportedPlaybackState = playbackState;
    }

    public int getRepeatCount() {
        return this.repeatCount;
    }

    public class AudioVisualizerRenderersFactory extends DefaultRenderersFactory {
        public AudioVisualizerRenderersFactory(Context context) {
            super(context);
        }

        @Override // com.google.android.exoplayer2.DefaultRenderersFactory
        public AudioSink buildAudioSink(Context context, boolean z, boolean z2, boolean z3) {
            return new DefaultAudioSink.Builder().setAudioCapabilities(AudioCapabilities.getCapabilities(context)).setEnableFloatOutput(z).setEnableAudioTrackPlaybackParams(z2).setAudioProcessors(new AudioProcessor[]{new TeeAudioProcessor(VideoPlayer.this.new VisualizerBufferSink())}).setOffloadMode(z3 ? 1 : 0).build();
        }
    }

    public class VisualizerBufferSink implements TeeAudioProcessor.AudioBufferSink {
        ByteBuffer byteBuffer;
        long lastUpdateTime;
        private final int BUFFER_SIZE = 1024;
        private final int MAX_BUFFER_SIZE = 8192;
        FourierTransform.FFT fft = new FourierTransform.FFT(1024, 48000.0f);
        float[] real = new float[1024];
        int position = 0;

        @Override // com.google.android.exoplayer2.audio.TeeAudioProcessor.AudioBufferSink
        public void flush(int i, int i2, int i3) {
        }

        public VisualizerBufferSink() {
            ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect(8192);
            this.byteBuffer = byteBufferAllocateDirect;
            byteBufferAllocateDirect.position(0);
        }

        @Override // com.google.android.exoplayer2.audio.TeeAudioProcessor.AudioBufferSink
        public void handleBuffer(ByteBuffer byteBuffer) {
            if (VideoPlayer.this.audioVisualizerDelegate == null) {
                return;
            }
            if (byteBuffer == AudioProcessor.EMPTY_BUFFER || !VideoPlayer.this.mixedPlayWhenReady) {
                VideoPlayer.this.audioUpdateHandler.postDelayed(new Runnable() { // from class: org.telegram.ui.Components.VideoPlayer$VisualizerBufferSink$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$handleBuffer$0();
                    }
                }, 80L);
                return;
            }
            if (VideoPlayer.this.audioVisualizerDelegate.needUpdate()) {
                int iLimit = byteBuffer.limit();
                int i = 0;
                if (iLimit > 8192) {
                    VideoPlayer.this.audioUpdateHandler.removeCallbacksAndMessages(null);
                    VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(false, true, null);
                    return;
                }
                this.byteBuffer.put(byteBuffer);
                int i2 = this.position + iLimit;
                this.position = i2;
                if (i2 >= 1024) {
                    this.byteBuffer.position(0);
                    for (int i3 = 0; i3 < 1024; i3++) {
                        this.real[i3] = this.byteBuffer.getShort() / 32768.0f;
                    }
                    this.byteBuffer.rewind();
                    this.position = 0;
                    this.fft.forward(this.real);
                    float f = 0.0f;
                    int i4 = 0;
                    while (true) {
                        float f2 = 1.0f;
                        if (i4 >= 1024) {
                            break;
                        }
                        float f3 = this.fft.getSpectrumReal()[i4];
                        float f4 = this.fft.getSpectrumImaginary()[i4];
                        float fSqrt = ((float) Math.sqrt((f3 * f3) + (f4 * f4))) / 30.0f;
                        if (fSqrt <= 1.0f) {
                            f2 = fSqrt < 0.0f ? 0.0f : fSqrt;
                        }
                        f += f2 * f2;
                        i4++;
                    }
                    float fSqrt2 = (float) Math.sqrt(f / 1024.0f);
                    final float[] fArr = new float[7];
                    fArr[6] = fSqrt2;
                    if (fSqrt2 < 0.4f) {
                        while (i < 7) {
                            fArr[i] = 0.0f;
                            i++;
                        }
                    } else {
                        while (i < 6) {
                            int i5 = 170 * i;
                            float f5 = this.fft.getSpectrumReal()[i5];
                            float f6 = this.fft.getSpectrumImaginary()[i5];
                            float fSqrt3 = (float) (Math.sqrt((f5 * f5) + (f6 * f6)) / 30.0d);
                            fArr[i] = fSqrt3;
                            if (fSqrt3 > 1.0f) {
                                fArr[i] = 1.0f;
                            } else if (fSqrt3 < 0.0f) {
                                fArr[i] = 0.0f;
                            }
                            i++;
                        }
                    }
                    if (System.currentTimeMillis() - this.lastUpdateTime < 64) {
                        return;
                    }
                    this.lastUpdateTime = System.currentTimeMillis();
                    VideoPlayer.this.audioUpdateHandler.postDelayed(new Runnable() { // from class: org.telegram.ui.Components.VideoPlayer$VisualizerBufferSink$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$handleBuffer$1(fArr);
                        }
                    }, 130L);
                }
            }
        }

        public /* synthetic */ void lambda$handleBuffer$0() {
            VideoPlayer.this.audioUpdateHandler.removeCallbacksAndMessages(null);
            VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(false, true, null);
        }

        public /* synthetic */ void lambda$handleBuffer$1(float[] fArr) {
            VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(true, true, fArr);
        }
    }

    public boolean isHDR() {
        ColorInfo colorInfo;
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer == null) {
            return false;
        }
        try {
            Format videoFormat = exoPlayer.getVideoFormat();
            if (videoFormat != null && (colorInfo = videoFormat.colorInfo) != null) {
                int i = colorInfo.colorTransfer;
                return i == 6 || i == 7;
            }
        } catch (Exception unused) {
        }
        return false;
    }

    public StoryEntry.HDRInfo getHDRStaticInfo(StoryEntry.HDRInfo hDRInfo) {
        if (hDRInfo == null) {
            hDRInfo = new StoryEntry.HDRInfo();
        }
        try {
            MediaFormat mediaFormat = ((MediaCodecRenderer) this.player.getRenderer(0)).codecOutputMediaFormat;
            ByteBuffer byteBuffer = mediaFormat.getByteBuffer("hdr-static-info");
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            if (byteBuffer.get() == 0) {
                hDRInfo.maxlum = byteBuffer.getShort(17);
                hDRInfo.minlum = byteBuffer.getShort(19) * 1.0E-4f;
            }
            if (mediaFormat.containsKey("color-transfer")) {
                hDRInfo.colorTransfer = mediaFormat.getInteger("color-transfer");
            }
            if (mediaFormat.containsKey("color-standard")) {
                hDRInfo.colorStandard = mediaFormat.getInteger("color-standard");
            }
            if (mediaFormat.containsKey("color-range")) {
                hDRInfo.colorRange = mediaFormat.getInteger("color-range");
            }
            return hDRInfo;
        } catch (Exception unused) {
            hDRInfo.minlum = 0.0f;
            hDRInfo.maxlum = 0.0f;
            return hDRInfo;
        }
    }

    public void setWorkerQueue(DispatchQueue dispatchQueue) {
        this.workerQueue = dispatchQueue;
        this.player.setWorkerQueue(dispatchQueue);
    }

    public void setIsStory() {
        this.isStory = true;
    }

    @Override // com.google.android.exoplayer2.Player.Listener
    public void onTracksChanged(Tracks tracks) {
        super.onTracksChanged(tracks);
        Runnable runnable = this.onQualityChangeListener;
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public ChromecastMediaVariations getCurrentChromecastMedia(String str, String str2, String str3) {
        if (this.videoQualities == null) {
            if (this.videoUri == null) {
                return null;
            }
            String str4 = "/mtproto_" + str;
            String queryParameter = this.videoUri.getQueryParameter("mime");
            return ChromecastMediaVariations.of(ChromecastMedia.Builder.fromUri(this.videoUri, str4, TextUtils.isEmpty(queryParameter) ? "video/mp4" : queryParameter).setTitle(str2).setSubtitle(str3).build());
        }
        ChromecastMediaVariations.Builder builder = new ChromecastMediaVariations.Builder();
        ArrayList<Quality> arrayList = this.videoQualities;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Quality quality = arrayList.get(i);
            i++;
            ArrayList<VideoUri> arrayList2 = quality.uris;
            int size2 = arrayList2.size();
            int i2 = 0;
            while (i2 < size2) {
                VideoUri videoUri = arrayList2.get(i2);
                i2++;
                VideoUri videoUri2 = videoUri;
                StringBuilder sb = new StringBuilder("/mtproto_");
                int i3 = size;
                sb.append(videoUri2.docId);
                String string = sb.toString();
                TLRPC.Document document = videoUri2.document;
                String str5 = document != null ? document.mime_type : null;
                if (TextUtils.isEmpty(str5)) {
                    str5 = "video/mp4";
                }
                builder.add(ChromecastMedia.Builder.fromUri(videoUri2.uri, string, str5).setTitle(str2).setSubtitle(str3).setSize(videoUri2.width, videoUri2.height).build());
                size = i3;
            }
        }
        return builder.build();
    }

    public static class OffsetDataSource implements DataSource {
        private final long byteOffset;
        private final DataSource upstream;

        public OffsetDataSource(DataSource dataSource, long j) {
            this.upstream = dataSource;
            this.byteOffset = j;
        }

        @Override // com.google.android.exoplayer2.upstream.DataSource
        public void addTransferListener(TransferListener transferListener) {
            this.upstream.addTransferListener(transferListener);
        }

        @Override // com.google.android.exoplayer2.upstream.DataSource
        public long open(DataSpec dataSpec) {
            return this.upstream.open(dataSpec.buildUpon().setPosition(dataSpec.position + this.byteOffset).build());
        }

        @Override // com.google.android.exoplayer2.upstream.DataReader
        public int read(byte[] bArr, int i, int i2) {
            return this.upstream.read(bArr, i, i2);
        }

        @Override // com.google.android.exoplayer2.upstream.DataSource
        public Uri getUri() {
            return this.upstream.getUri();
        }

        @Override // com.google.android.exoplayer2.upstream.DataSource
        public void close() {
            this.upstream.close();
        }

        @Override // com.google.android.exoplayer2.upstream.DataSource
        public Map<String, List<String>> getResponseHeaders() {
            return this.upstream.getResponseHeaders();
        }
    }
}
