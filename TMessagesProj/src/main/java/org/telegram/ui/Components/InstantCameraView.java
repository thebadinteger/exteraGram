package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.camera.core.Preview;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.CameraType;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.VideoMessagesCamera;
import com.exteragram.messenger.camera.CameraDebugUtils;
import com.exteragram.messenger.camera.CameraXSession;
import com.exteragram.messenger.camera.InstantCameraZoomSlider;
import com.exteragram.messenger.camera.RoundVideoEncoder;
import com.exteragram.messenger.debug.DebugConfig;
import com.exteragram.messenger.debug.DebugOverlayView;
import com.exteragram.messenger.utils.system.SystemUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.AutoDeleteMediaTask;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.Camera2Session;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraInfo;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.Stories.recorder.DualCameraView;
import org.telegram.ui.Stories.recorder.FlashViews;
import org.telegram.ui.Stories.recorder.SliderView;
import org.telegram.ui.Stories.recorder.StoryEntry;

@SuppressLint({"ViewConstructor"})
public class InstantCameraView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final int[] ALLOW_BIG_CAMERA_WHITELIST = {285904780, -1394191079};
    private float animationTranslationY;
    private AnimatorSet animatorSet;
    private org.telegram.messenger.camera.Size aspectRatio;
    private volatile boolean bothCameras;
    private final LinearLayout buttonsLayout;
    private final int buttonsSizePx;
    private CameraXSession.CameraLifecycle camLifecycle;
    private Camera2Session camera2SessionCurrent;
    private Camera2Session[] camera2Sessions;
    private InstantViewCameraContainer cameraContainer;
    private File cameraFile;
    private volatile boolean cameraReady;
    private CameraSession cameraSession;
    private final int[] cameraTexture;
    private float cameraTextureAlpha;
    private volatile boolean cameraTextureAvailable;
    private CameraGLThread cameraThread;
    private volatile CameraXSession cameraXSession;
    private float cameraZoom;
    private boolean cancelled;
    private int currentAccount;
    private Delegate delegate;
    private final RoundVideoEncoder.Callback encoderCallback;
    private File encoderFile;
    private boolean encoderFinishRequested;
    private int encoderFrameRate;
    private int encoderSend;
    private SendOptions encoderSendOptions;
    private TLRPC.InputEncryptedFile encryptedFile;
    private TLRPC.InputFile file;
    private ValueAnimator finishZoomTransition;
    private Bitmap firstFrameThumb;
    private final FlashViews.ImageViewInvertable flashButton;
    private RLottieDrawable flashOffDrawable;
    private RLottieDrawable flashOnDrawable;
    private final FlashViews flashViews;
    private boolean flashing;
    private boolean flipAnimationInProgress;
    private boolean frontFlashing;
    private DispatchQueue generateKeyframeThumbsQueue;
    private float initialCameraZoom;
    private int internalPaddingBottom;
    private boolean isFrontface;
    boolean isInPinchToZoomTouchMode;
    private boolean isMessageTransition;
    private boolean isSecretChat;
    private ItemOptions itemOptions;
    private byte[] iv;
    private byte[] key;
    private final ArrayList<Bitmap> keyframeThumbs;
    private Bitmap lastBitmap;
    private final float[] mMVPMatrix;
    private final float[] mSTMatrix;
    boolean maybePinchToZoomTouchMode;
    private final float[] moldSTMatrix;
    private AnimatorSet muteAnimation;
    private ImageView muteImageView;
    private boolean needDrawFlickerStub;
    private final int[] oldCameraTexture;
    private org.telegram.messenger.camera.Size oldTexturePreviewSize;
    private FloatBuffer oldTextureTextureBuffer;
    public boolean opened;
    private Paint paint;
    private float panTranslationY;
    private View parentView;
    private org.telegram.messenger.camera.Size pictureSize;
    float pinchStartDistance;
    private int pointerId1;
    private int pointerId2;
    private final int[] position;
    private File previewFile;
    private org.telegram.messenger.camera.Size[] previewSize;
    private float progress;
    private Timer progressTimer;
    private long recordPlusTime;
    private long recordStartTime;
    private long recordedTime;
    private boolean recording;
    private int recordingGuid;
    private RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private final ScaleGestureDetector scaleGestureDetector;
    private float scaleX;
    private float scaleY;
    private CameraInfo selectedCamera;
    private boolean sentMedia;
    private boolean setVisibilityFromPause;
    private long size;
    private volatile int surfaceIndex;
    private final FlashViews.ImageViewInvertable switchCameraButton;
    private RLottieDrawable switchCameraDrawable;
    private FloatBuffer textureBuffer;
    private final float[] textureCoordsData;
    private BackupImageView textureOverlayView;
    private TextureView textureView;
    private int textureViewSize;
    private boolean updateTextureViewSize;
    private final boolean useCamera2;
    private FloatBuffer vertexBuffer;
    private boolean videoConvertFirstWrite;
    private VideoEditedInfo videoEditedInfo;
    private RoundVideoEncoder videoEncoder;
    private VideoPlayer videoPlayer;
    private Boolean wasFlashing;
    private ValueAnimator zoomAnimator;
    private InstantCameraZoomSlider zoomSlider;
    private boolean zoomWas;

    public interface Delegate {
        int getClassGuid();

        long getDialogId();

        View getFragmentView();

        Activity getParentActivity();

        default boolean isInScheduleMode() {
            return false;
        }

        default boolean isSecretChat() {
            return false;
        }

        void sendMedia(MediaController.PhotoEntry photoEntry, VideoEditedInfo videoEditedInfo, boolean z, int i, int i2, boolean z2, long j);
    }

    private boolean isCameraSessionInitiated() {
        if (this.useCamera2) {
            Camera2Session camera2Session = this.camera2SessionCurrent;
            return camera2Session != null && camera2Session.isInitiated();
        }
        CameraSession cameraSession = this.cameraSession;
        return cameraSession != null && cameraSession.isInitied();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public InstantCameraView(Context context, Delegate delegate, final Theme.ResourcesProvider resourcesProvider, boolean z) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        int i = 1;
        this.isFrontface = true;
        this.position = new int[2];
        this.cameraTexture = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
        this.oldCameraTexture = new int[1];
        this.cameraTextureAlpha = 1.0f;
        this.previewSize = new org.telegram.messenger.camera.Size[2];
        this.aspectRatio = SharedConfig.roundCamera16to9 ? new org.telegram.messenger.camera.Size(16, 9) : new org.telegram.messenger.camera.Size(4, 3);
        this.useCamera2 = ExteraConfig.getCameraType() == CameraType.CAMERA_2;
        this.camera2Sessions = new Camera2Session[2];
        this.mMVPMatrix = new float[16];
        this.mSTMatrix = new float[16];
        this.moldSTMatrix = new float[16];
        this.textureCoordsData = new float[8];
        this.encoderFrameRate = 30;
        this.keyframeThumbs = new ArrayList<>();
        this.encoderCallback = new AnonymousClass12();
        this.initialCameraZoom = 0.0f;
        this.buttonsSizePx = AndroidUtilities.dp(z ? 24.0f : 28.0f);
        this.resourcesProvider = resourcesProvider;
        this.parentView = delegate.getFragmentView();
        setWillNotDraw(false);
        this.delegate = delegate;
        this.recordingGuid = delegate.getClassGuid();
        this.isSecretChat = delegate.isSecretChat();
        Paint paint = new Paint(i) { // from class: org.telegram.ui.Components.InstantCameraView.1
            @Override // android.graphics.Paint
            public void setAlpha(int i2) {
                super.setAlpha(i2);
                InstantCameraView.this.invalidate();
            }
        };
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
        this.paint.setColor(-1);
        this.rect = new RectF();
        FlashViews flashViews = new FlashViews(getContext(), null, this, null);
        this.flashViews = flashViews;
        flashViews.setWarmth(ExteraConfig.getFlashWarmth());
        flashViews.setIntensity(ExteraConfig.getFlashIntensity());
        addView(flashViews.backgroundView, LayoutHelper.createFrame(-1, -1, 119));
        this.scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() { // from class: org.telegram.ui.Components.InstantCameraView.2
            @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                InstantCameraView.this.cancelZoomAnimations();
                InstantCameraView.this.zoomSlider.beginPinchZoomGesture();
                return true;
            }

            @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                if (InstantCameraView.this.cameraXSession == null) {
                    return true;
                }
                InstantCameraView.this.zoomSlider.scaleCameraXZoom((float) Math.pow(scaleGestureDetector.getScaleFactor(), 2.0d));
                InstantCameraView instantCameraView = InstantCameraView.this;
                instantCameraView.cameraZoom = instantCameraView.cameraXSession.getLinearZoom();
                return true;
            }

            @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                InstantCameraView.this.finishZoom();
                super.onScaleEnd(scaleGestureDetector);
            }
        });
        InstantViewCameraContainer instantViewCameraContainer = new InstantViewCameraContainer(context) { // from class: org.telegram.ui.Components.InstantCameraView.3
            @Override // android.view.View
            public void setRotationY(float f) {
                super.setRotationY(f);
                InstantCameraView.this.invalidate();
            }

            @Override // android.view.View
            public void setAlpha(float f) {
                super.setAlpha(f);
                InstantCameraView.this.invalidate();
            }
        };
        this.cameraContainer = instantViewCameraContainer;
        instantViewCameraContainer.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.InstantCameraView.4
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, InstantCameraView.this.textureViewSize, InstantCameraView.this.textureViewSize);
            }
        });
        this.cameraContainer.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda9
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return this.f$0.lambda$new$0(view, motionEvent);
            }
        });
        this.cameraContainer.setClipToOutline(true);
        this.cameraContainer.setWillNotDraw(false);
        View view = this.cameraContainer;
        int i2 = AndroidUtilities.roundPlayingMessageSize;
        addView(view, new FrameLayout.LayoutParams(i2, i2, 17));
        addView(flashViews.foregroundView, LayoutHelper.createFrame(-1, -1, 119));
        LinearLayout linearLayout = new LinearLayout(context);
        this.buttonsLayout = linearLayout;
        linearLayout.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createFrame(-2, 56.0f, 83, 1.0f, 0.0f, 0.0f, 0.0f));
        FlashViews.ImageViewInvertable imageViewInvertable = new FlashViews.ImageViewInvertable(context);
        this.switchCameraButton = imageViewInvertable;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageViewInvertable.setScaleType(scaleType);
        imageViewInvertable.setContentDescription(LocaleController.getString(R.string.AccDescrSwitchCamera));
        linearLayout.addView(imageViewInvertable, LayoutHelper.createLinear(44, 44));
        imageViewInvertable.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.lambda$new$2(view2);
            }
        });
        FlashViews.ImageViewInvertable imageViewInvertable2 = new FlashViews.ImageViewInvertable(context);
        this.flashButton = imageViewInvertable2;
        imageViewInvertable2.setScaleType(scaleType);
        linearLayout.addView(imageViewInvertable2, LayoutHelper.createLinear(44, 44));
        imageViewInvertable2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.lambda$new$3(view2);
            }
        });
        imageViewInvertable2.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda12
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view2) {
                return this.f$0.lambda$new$7(resourcesProvider, view2);
            }
        });
        updateFlash();
        if (!z) {
            flashViews.add(imageViewInvertable);
            flashViews.add(imageViewInvertable2);
        } else if (!resourcesProvider.isDark()) {
            imageViewInvertable.setInvert(0.6f);
            imageViewInvertable2.setInvert(0.6f);
        }
        ImageView imageView = new ImageView(context);
        this.muteImageView = imageView;
        imageView.setScaleType(scaleType);
        this.muteImageView.setImageResource(R.drawable.video_mute);
        this.muteImageView.setAlpha(0.0f);
        addView(this.muteImageView, LayoutHelper.createFrame(48, 48, 17));
        final Paint paint2 = new Paint(1);
        paint2.setColor(ColorUtils.setAlphaComponent(-16777216, 40));
        BackupImageView backupImageView = new BackupImageView(getContext()) { // from class: org.telegram.ui.Components.InstantCameraView.7
            CellFlickerDrawable flickerDrawable = new CellFlickerDrawable();

            @Override // org.telegram.ui.Components.BackupImageView, android.view.View
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (InstantCameraView.this.needDrawFlickerStub) {
                    this.flickerDrawable.setParentWidth(InstantCameraView.this.textureViewSize);
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, 0.0f, InstantCameraView.this.textureViewSize, InstantCameraView.this.textureViewSize);
                    float fWidth = rectF.width() / 2.0f;
                    canvas.drawRoundRect(rectF, fWidth, fWidth, paint2);
                    rectF.inset(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                    this.flickerDrawable.draw(canvas, rectF, fWidth, null);
                    invalidate();
                }
            }
        };
        this.textureOverlayView = backupImageView;
        int i3 = AndroidUtilities.roundPlayingMessageSize;
        addView(backupImageView, new FrameLayout.LayoutParams(i3, i3, 17));
        InstantCameraZoomSlider instantCameraZoomSlider = new InstantCameraZoomSlider(context, resourcesProvider);
        this.zoomSlider = instantCameraZoomSlider;
        instantCameraZoomSlider.setOnCameraZoomChangeListener(new InstantCameraZoomSlider.OnCameraZoomChangeListener() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda13
            @Override 
            public final void onCameraZoomChanged(float f, boolean z2) {
                this.f$0.lambda$new$8(f, z2);
            }
        });
        this.zoomSlider.setOpenAlpha(0.0f);
        addView(this.zoomSlider, LayoutHelper.createFrame(-2, -2, 17));
        if (DebugConfig.getDebugCameraMetrics()) {
            DebugOverlayView debugOverlayView = new DebugOverlayView(context);
            debugOverlayView.setDataSource(new DebugOverlayView.DataSource() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda14
                @Override 
                public final void build(DebugOverlayView.ContentBuilder contentBuilder) {
                    this.f$0.populateCameraDebugOverlay(contentBuilder);
                }
            });
            addView(debugOverlayView, DebugOverlayView.createLayoutParams());
        }
        this.setVisibilityFromPause = false;
        setVisibility(4);
    }

    public void lambda$new$4(Float f) {
        ExteraConfig.setFlashWarmth(f.floatValue());
        this.flashViews.setWarmth(f.floatValue());
    }

    public void lambda$startAnimation$9(boolean z, ValueAnimator valueAnimator) {
        this.animationTranslationY = z ? 0.0f : (getMeasuredHeight() / 2.0f) * ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateTranslationY();
    }

    private void updateTranslationY() {
        this.textureOverlayView.setTranslationY(this.animationTranslationY + this.panTranslationY);
        this.cameraContainer.setTranslationY(this.animationTranslationY + this.panTranslationY);
        this.zoomSlider.setBaseTranslationY(this.animationTranslationY + this.panTranslationY);
    }

    public RectOld getCameraRect() {
        this.cameraContainer.getLocationOnScreen(this.position);
        int[] iArr = this.position;
        return new RectOld(iArr[0], iArr[1], this.cameraContainer.getWidth(), this.cameraContainer.getHeight());
    }

    public void changeVideoPreviewState(int i, float f) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return;
        }
        if (i == 0) {
            startProgressTimer();
            this.videoPlayer.play();
        } else if (i == 1) {
            stopProgressTimer();
            this.videoPlayer.pause();
        } else if (i == 2) {
            videoPlayer.seekTo((long) (f * videoPlayer.getDuration()));
        }
    }

    public void send(int i, boolean z, int i2, int i3, int i4, long j, long j2) {
        int i5;
        if (this.textureView == null) {
            return;
        }
        stopProgressTimer();
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
        }
        int i6 = 4;
        if (i == 4) {
            if (this.videoEncoder != null && this.recordedTime > 800) {
                requestStopRecording(1, new SendOptions(z, i2, i3, i4, j, j2));
                return;
            }
            if (BuildVars.DEBUG_VERSION && !this.cameraFile.exists()) {
                FileLog.e(new RuntimeException("file not found :( round video"));
            }
            if (this.videoEditedInfo == null) {
                VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
                this.videoEditedInfo = videoEditedInfo;
                videoEditedInfo.startTime = -1L;
                videoEditedInfo.endTime = -1L;
            }
            if (this.videoEditedInfo.needConvert()) {
                this.file = null;
                this.encryptedFile = null;
                this.key = null;
                this.iv = null;
                VideoEditedInfo videoEditedInfo2 = this.videoEditedInfo;
                long j3 = videoEditedInfo2.estimatedDuration;
                double d = j3;
                long j4 = videoEditedInfo2.startTime;
                if (j4 < 0) {
                    j4 = 0;
                }
                long j5 = videoEditedInfo2.endTime;
                if (j5 >= 0) {
                    j3 = j5;
                }
                long j6 = j3 - j4;
                videoEditedInfo2.estimatedDuration = j6;
                videoEditedInfo2.estimatedSize = Math.max(1L, (long) (this.size * (j6 / d)));
                this.videoEditedInfo.bitrate = SystemUtils.getRoundVideoBitrate() * 1024;
                VideoEditedInfo videoEditedInfo3 = this.videoEditedInfo;
                long j7 = videoEditedInfo3.startTime;
                if (j7 > 0) {
                    videoEditedInfo3.startTime = j7 * 1000;
                }
                long j8 = videoEditedInfo3.endTime;
                if (j8 > 0) {
                    videoEditedInfo3.endTime = j8 * 1000;
                }
                FileLoader.getInstance(this.currentAccount).cancelFileUpload(this.cameraFile.getAbsolutePath(), false);
            } else {
                this.videoEditedInfo.estimatedSize = Math.max(1L, this.size);
            }
            VideoEditedInfo videoEditedInfo4 = this.videoEditedInfo;
            videoEditedInfo4.file = this.file;
            videoEditedInfo4.encryptedFile = this.encryptedFile;
            videoEditedInfo4.key = this.key;
            videoEditedInfo4.iv = this.iv;
            MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, 0, 0L, this.cameraFile.getAbsolutePath(), 0, true, 0, 0, 0L);
            photoEntry.ttl = i4;
            photoEntry.effectId = j;
            this.delegate.sendMedia(photoEntry, this.videoEditedInfo, z, i2, i3, false, j2);
            if (i2 != 0) {
                startAnimation(false, false);
            }
            MediaController.getInstance().requestRecordAudioFocus(false);
            return;
        }
        this.cancelled = this.recordedTime < 800;
        this.recording = false;
        this.flashing = false;
        updateFlash();
        if (!this.cancelled) {
            i6 = i == 3 ? 2 : 5;
        }
        if (this.cameraThread != null) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.recordStopped, Integer.valueOf(this.recordingGuid), Integer.valueOf(i6));
            if (this.cancelled) {
                i5 = 0;
            } else {
                i5 = i == 3 ? 2 : 1;
            }
            saveLastCameraBitmap();
            this.cameraThread.shutdown(i5, z, i2, i3, i4, j);
            this.cameraThread = null;
        }
        if (this.cancelled) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), Boolean.TRUE, Integer.valueOf((int) this.recordedTime));
            startAnimation(false, false);
            MediaController.getInstance().requestRecordAudioFocus(false);
        }
    }

    private void saveLastCameraBitmap() {
        Bitmap bitmap = this.textureView.getBitmap();
        if (bitmap == null || bitmap.getPixel(0, 0) == 0) {
            return;
        }
        final Bitmap bitmapCreateScaledBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
        this.lastBitmap = bitmapCreateScaledBitmap;
        Utilities.blurBitmap(bitmapCreateScaledBitmap, 7, 1, bitmapCreateScaledBitmap.getWidth(), bitmapCreateScaledBitmap.getHeight(), bitmapCreateScaledBitmap.getRowBytes());
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                InstantCameraView.$r8$lambda$dwdTlZURzTW3rnHrKLBue44ayhQ(bitmapCreateScaledBitmap);
            }
        });
    }

    public static int $r8$lambda$JGI5kBaG7zbZNQIRRLRBaADb4XI(org.telegram.messenger.camera.Size size, org.telegram.messenger.camera.Size size2) {
        float fAbs = Math.abs(1.0f - (Math.min(size.mHeight, size.mWidth) / Math.max(size.mHeight, size.mWidth)));
        float fAbs2 = Math.abs(1.0f - (Math.min(size2.mHeight, size2.mWidth) / Math.max(size2.mHeight, size2.mWidth)));
        if (fAbs < fAbs2) {
            return -1;
        }
        return fAbs > fAbs2 ? 1 : 0;
    }

    @Deprecated
    private boolean allowBigSizeCamera() {
        if (SharedConfig.bigCameraForRound || SharedConfig.deviceIsAboveAverage() || Math.max(SharedConfig.getDevicePerformanceClass(), SharedConfig.getLegacyDevicePerformanceClass()) == 2) {
            return true;
        }
        int iHashCode = (Build.MANUFACTURER + " " + Build.DEVICE).toUpperCase().hashCode();
        int i = 0;
        while (true) {
            int[] iArr = ALLOW_BIG_CAMERA_WHITELIST;
            if (i >= iArr.length) {
                return false;
            }
            if (iArr[i] == iHashCode) {
                return true;
            }
            i++;
        }
    }

    @Deprecated
    public static boolean allowBigSizeCameraDebug() {
        if (Math.max(SharedConfig.getDevicePerformanceClass(), SharedConfig.getLegacyDevicePerformanceClass()) == 2) {
            return true;
        }
        int iHashCode = (Build.MANUFACTURER + " " + Build.DEVICE).toUpperCase().hashCode();
        int i = 0;
        while (true) {
            int[] iArr = ALLOW_BIG_CAMERA_WHITELIST;
            if (i >= iArr.length) {
                return false;
            }
            if (iArr[i] == iHashCode) {
                return true;
            }
            i++;
        }
    }

    public void createCamera(final int i, final SurfaceTexture surfaceTexture) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$createCamera$17(i, surfaceTexture);
            }
        });
    }

    public void lambda$createCamera$13(CameraSession cameraSession) {
        CameraGLThread cameraGLThread;
        if (this.cameraSession == cameraSession) {
            updateFlash();
            boolean z = false;
            try {
                Camera.Size currentPreviewSize = cameraSession.getCurrentPreviewSize();
                if (currentPreviewSize.width != this.previewSize[0].getWidth() || currentPreviewSize.height != this.previewSize[0].getHeight()) {
                    this.previewSize[0] = new org.telegram.messenger.camera.Size(currentPreviewSize.width, currentPreviewSize.height);
                    FileLog.d("InstantCamera change preview size to w = " + this.previewSize[0].getWidth() + " h = " + this.previewSize[0].getHeight());
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                Camera.Size currentPictureSize = cameraSession.getCurrentPictureSize();
                if (currentPictureSize.width != this.pictureSize.getWidth() || currentPictureSize.height != this.pictureSize.getHeight()) {
                    this.pictureSize = new org.telegram.messenger.camera.Size(currentPictureSize.width, currentPictureSize.height);
                    FileLog.d("InstantCamera change picture size to w = " + this.pictureSize.getWidth() + " h = " + this.pictureSize.getHeight());
                    z = true;
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("InstantCamera camera initied");
            }
            cameraSession.setInitied();
            this.zoomSlider.bindSession(cameraSession, this.cameraZoom);
            if (!z || (cameraGLThread = this.cameraThread) == null) {
                return;
            }
            cameraGLThread.reinitForNewCamera();
        }
    }

    public private void onDraw(Integer num, boolean z, boolean z2) {
            boolean z3;
            int displayOrientation;
            if (this.initied) {
                Integer num2 = this.cameraId;
                if (num2 == null || num2.equals(num)) {
                    if (!this.eglContext.equals(this.egl10.eglGetCurrentContext()) || !this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) {
                        EGL10 egl10 = this.egl10;
                        EGLDisplay eGLDisplay = this.eglDisplay;
                        EGLSurface eGLSurface = this.eglSurface;
                        if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                                return;
                            }
                            return;
                        }
                    }
                    if (z) {
                        try {
                            this.cameraSurface[0].updateTexImage();
                        } catch (Throwable th) {
                            FileLog.e(th);
                            return;
                        }
                    }
                    if (z2) {
                        try {
                            this.cameraSurface[1].updateTexImage();
                        } catch (Throwable th2) {
                            FileLog.e(th2);
                            return;
                        }
                    }
                    boolean z4 = InstantCameraView.this.surfaceIndex == 0 ? z : z2;
                    CameraType cameraType = ExteraConfig.getCameraType();
                    CameraType cameraType2 = CameraType.CAMERA_X;
                    if (cameraType == cameraType2 && InstantCameraView.this.bothCameras && !z4) {
                        return;
                    }
                    if (this.recording) {
                        z3 = false;
                    } else {
                        if (InstantCameraView.this.videoEncoder == null) {
                            InstantCameraView.this.sentMedia = false;
                            InstantCameraView.this.videoConvertFirstWrite = true;
                            InstantCameraView.this.encoderFinishRequested = false;
                            InstantCameraView.this.encoderSend = 0;
                            InstantCameraView.this.encoderSendOptions = null;
                            InstantCameraView.this.keyframeThumbs.clear();
                            if (InstantCameraView.this.generateKeyframeThumbsQueue != null) {
                                InstantCameraView.this.generateKeyframeThumbsQueue.cleanupQueue();
                                InstantCameraView.this.generateKeyframeThumbsQueue.recycle();
                            }
                            InstantCameraView.this.generateKeyframeThumbsQueue = new DispatchQueue("keyframes_thumb_queue");
                            InstantCameraView instantCameraView = InstantCameraView.this;
                            instantCameraView.videoEncoder = new RoundVideoEncoder(new EncoderRenderer(), InstantCameraView.this.encoderCallback, InstantCameraView.this.isSecretChat);
                            InstantCameraView instantCameraView2 = InstantCameraView.this;
                            instantCameraView2.encoderFrameRate = instantCameraView2.resolveEncoderFrameRate();
                        }
                        if (InstantCameraView.this.videoEncoder.isStarted()) {
                            if (!InstantCameraView.this.cameraReady) {
                                InstantCameraView.this.cameraReady = true;
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$CameraGLThread$$ExternalSyntheticLambda2
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        this.f$0.lambda$onDraw$2();
                                    }
                                });
                            }
                            z3 = false;
                        } else {
                            z3 = true;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$CameraGLThread$$ExternalSyntheticLambda3
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$onDraw$3();
                            }
                        });
                        InstantCameraView instantCameraView3 = InstantCameraView.this;
                        instantCameraView3.encoderFile = instantCameraView3.cameraFile;
                        InstantCameraView.this.videoEncoder.startRecording(InstantCameraView.this.cameraFile, EGL14.eglGetCurrentContext(), InstantCameraView.this.encoderFrameRate);
                        this.recording = true;
                        if (ExteraConfig.getCameraType() != cameraType2) {
                            Object obj = this.currentSession;
                            if (obj instanceof CameraSession) {
                                displayOrientation = ((CameraSession) obj).getCurrentOrientation();
                            } else if (obj instanceof Camera2Session) {
                                displayOrientation = ((Camera2Session) obj).getCurrentOrientation();
                            } else {
                                displayOrientation = 0;
                            }
                        } else {
                            CameraXSession cameraXSession = InstantCameraView.this.cameraXSession;
                            if (cameraXSession != null) {
                                displayOrientation = cameraXSession.getDisplayOrientation();
                            } else {
                                displayOrientation = 0;
                            }
                        }
                        if (displayOrientation == 90 || displayOrientation == 270) {
                            float f = InstantCameraView.this.scaleX;
                            InstantCameraView instantCameraView4 = InstantCameraView.this;
                            instantCameraView4.scaleX = instantCameraView4.scaleY;
                            InstantCameraView.this.scaleY = f;
                        }
                        this.recording = true;
                        InstantCameraView.this.updateFlash();
                    }
                    if (InstantCameraView.this.videoEncoder != null && ((InstantCameraView.this.surfaceIndex == 0 && z) || (InstantCameraView.this.surfaceIndex == 1 && z2))) {
                        fillFrameSnapshot(this.frameSnapshotScratch, InstantCameraView.this.surfaceIndex, InstantCameraView.this.bothCameras ? InstantCameraView.this.surfaceIndex : num.intValue());
                        InstantCameraView.this.videoEncoder.frameAvailable(this.frameSnapshotScratch);
                    }
                    this.cameraSurface[InstantCameraView.this.surfaceIndex].getTransformMatrix(InstantCameraView.this.mSTMatrix);
                    GLES20.glUseProgram(this.drawProgram);
                    GLES20.glActiveTexture(33984);
                    GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[InstantCameraView.this.surfaceIndex]);
                    GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, (Buffer) InstantCameraView.this.vertexBuffer);
                    GLES20.glEnableVertexAttribArray(this.positionHandle);
                    GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, (Buffer) InstantCameraView.this.textureBuffer);
                    GLES20.glEnableVertexAttribArray(this.textureHandle);
                    GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
                    GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
                    GLES20.glDrawArrays(5, 0, 4);
                    GLES20.glDisableVertexAttribArray(this.positionHandle);
                    GLES20.glDisableVertexAttribArray(this.textureHandle);
                    GLES20.glBindTexture(36197, 0);
                    GLES20.glUseProgram(0);
                    this.egl10.eglSwapBuffers(this.eglDisplay, this.eglSurface);
                    if (z3) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$CameraGLThread$$ExternalSyntheticLambda4
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$onDraw$4();
                            }
                        });
                    }
                }
            }
        }

        public void lambda$onAudioAmplitude$0(double d) {
            NotificationCenter.getInstance(InstantCameraView.this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.recordProgressChanged, Integer.valueOf(InstantCameraView.this.recordingGuid), Double.valueOf(d));
        }

        @Override 
        public void onAudioAmplitude(final double d) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$12$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onAudioAmplitude$0(d);
                }
            });
        }

        @Override 
        public void onWriteData(long j) {
            File file = InstantCameraView.this.encoderFile;
            if (file != null) {
                InstantCameraView.this.didWriteData(file, j, false);
            }
        }

        @Override 
        public void onPaused(File file) throws Throwable {
            InstantCameraView.this.handleEncoderPaused(file);
        }

        @Override 
        public void onFinished(RoundVideoEncoder.FinishReason finishReason) throws Throwable {
            InstantCameraView.this.handleEncoderFinished(finishReason);
        }
    }

    public void requestStopRecording(int i, final SendOptions sendOptions) {
        RoundVideoEncoder roundVideoEncoder = this.videoEncoder;
        if (roundVideoEncoder == null) {
            return;
        }
        if (!this.encoderFinishRequested) {
            this.encoderFinishRequested = true;
            this.encoderSend = i;
            this.encoderSendOptions = sendOptions;
            if (i == 1) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() throws Throwable {
                        this.f$0.lambda$requestStopRecording$18(sendOptions);
                    }
                });
            }
        }
        if (i == 0) {
            roundVideoEncoder.cancel();
        } else {
            roundVideoEncoder.stop();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$requestStopRecording$19();
            }
        });
    }

    public void lambda$onDrawEncoderFrame$0() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120L).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        public void lambda$finishZoom$22(ValueAnimator valueAnimator) {
        if (this.cameraXSession != null) {
            this.zoomSlider.setCameraXZoomRatio(((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.cameraZoom = this.cameraXSession.getLinearZoom();
        }
    }

    public /* synthetic */ void lambda$finishZoom$23(ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.cameraZoom = fFloatValue;
        if (this.useCamera2) {
            Camera2Session camera2Session = this.camera2SessionCurrent;
            if (camera2Session != null) {
                camera2Session.setZoom(fFloatValue);
            }
        } else {
            CameraSession cameraSession = this.cameraSession;
            if (cameraSession != null) {
                cameraSession.setZoom(fFloatValue);
            }
        }
        this.zoomSlider.syncZoom(this.cameraZoom);
    }

    private void adjustZoom(boolean z) {
        float fClamp;
        if (isCameraReady()) {
            ValueAnimator valueAnimator = this.zoomAnimator;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                cancelZoomAnimations();
                this.zoomSlider.beginSteppedZoomGesture();
                if (ExteraConfig.getCameraType() == CameraType.CAMERA_X) {
                    float zoom = this.zoomSlider.getZoom();
                    float minimumZoom = this.zoomSlider.getMinimumZoom();
                    float maximumZoom = this.zoomSlider.getMaximumZoom();
                    float fClamp2 = Utilities.clamp(this.zoomSlider.getDisplayOneZoom(), maximumZoom, minimumZoom);
                    double d = maximumZoom / fClamp2;
                    int iRound = (int) Math.round(Math.log(d) / Math.log(1.75d));
                    if (iRound < 1) {
                        iRound = 1;
                    }
                    float fPow = (float) Math.pow(d, 1.0d / ((double) iRound));
                    if (z) {
                        if (zoom >= fClamp2) {
                            fClamp2 = zoom * fPow;
                        }
                    } else if (zoom > fClamp2) {
                        float f = zoom / fPow;
                        if (f >= fClamp2) {
                            fClamp2 = f;
                        }
                    } else {
                        fClamp2 = minimumZoom;
                    }
                    ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(zoom, Utilities.clamp(fClamp2, maximumZoom, minimumZoom));
                    this.zoomAnimator = valueAnimatorOfFloat;
                    valueAnimatorOfFloat.setDuration(175L);
                    this.zoomAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.zoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda3
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            this.f$0.lambda$adjustZoom$24(valueAnimator2);
                        }
                    });
                    this.zoomAnimator.start();
                    return;
                }
                float f2 = this.cameraZoom;
                float f3 = 1.0f;
                if (this.useCamera2) {
                    Camera2Session camera2Session = this.camera2SessionCurrent;
                    if (camera2Session == null) {
                        return;
                    }
                    float minZoom = camera2Session.getMinZoom();
                    float maxZoom = this.camera2SessionCurrent.getMaxZoom();
                    double d2 = maxZoom;
                    int iRound2 = (int) Math.round(Math.log(d2) / Math.log(1.75d));
                    if (iRound2 < 1) {
                        iRound2 = 1;
                    }
                    float fPow2 = (float) Math.pow(d2, 1.0d / ((double) iRound2));
                    float f4 = this.cameraZoom;
                    if (z) {
                        f3 = f4 * fPow2;
                    } else {
                        float f5 = f4 / fPow2;
                        if (f5 >= 1.0f) {
                            f3 = f5;
                        }
                    }
                    fClamp = Utilities.clamp(f3, maxZoom, minZoom);
                } else {
                    fClamp = Utilities.clamp(f2 + ((z ? 1 : -1) * 0.125f), 1.0f, 0.0f);
                }
                float f6 = this.cameraZoom;
                if (f6 == fClamp) {
                    return;
                }
                ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(f6, fClamp);
                this.zoomAnimator = valueAnimatorOfFloat2;
                valueAnimatorOfFloat2.setDuration(175L);
                this.zoomAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.zoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda4
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        this.f$0.lambda$adjustZoom$25(valueAnimator2);
                    }
                });
                this.zoomAnimator.start();
            }
        }
    }

    public /* synthetic */ void lambda$adjustZoom$24(ValueAnimator valueAnimator) {
        if (this.cameraXSession != null) {
            this.zoomSlider.setCameraXZoomRatio(((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.cameraZoom = this.cameraXSession.getLinearZoom();
        }
    }

    public /* synthetic */ void lambda$adjustZoom$25(ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.cameraZoom = fFloatValue;
        if (this.useCamera2) {
            Camera2Session camera2Session = this.camera2SessionCurrent;
            if (camera2Session != null) {
                camera2Session.setZoom(fFloatValue);
            }
        } else {
            CameraSession cameraSession = this.cameraSession;
            if (cameraSession != null) {
                cameraSession.setZoom(fFloatValue);
            }
        }
        this.zoomSlider.syncZoom(this.cameraZoom);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && i == 24) {
            adjustZoom(true);
            return true;
        }
        if (keyEvent.getAction() == 0 && i == 25) {
            adjustZoom(false);
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    public boolean isCameraReady() {
        boolean zIsCameraSessionInitiated;
        if (ExteraConfig.getCameraType() == CameraType.CAMERA_X) {
            zIsCameraSessionInitiated = this.cameraXSession != null && this.cameraXSession.isReady();
        } else {
            zIsCameraSessionInitiated = isCameraSessionInitiated();
        }
        return this.cameraReady && zIsCameraSessionInitiated && this.cameraThread != null;
    }
}
