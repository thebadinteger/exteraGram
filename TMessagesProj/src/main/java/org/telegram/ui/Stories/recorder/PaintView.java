package org.telegram.ui.Stories.recorder;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.AndroidUtilities.dpf2;
import static org.telegram.messenger.AndroidUtilities.lerp;
import static org.telegram.messenger.LocaleController.formatPluralString;
import static org.telegram.messenger.LocaleController.getString;
import static org.telegram.ui.Stars.StarsController.findAttribute;

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
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Looper;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.zxing.common.detector.MathUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.IPhotoPaintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Brush;
import org.telegram.ui.Components.Paint.ColorPickerBottomSheet;
import org.telegram.ui.Components.Paint.PaintTypeface;
import org.telegram.ui.Components.Paint.Painting;
import org.telegram.ui.Components.Paint.PersistColorPalette;
import org.telegram.ui.Components.Paint.PhotoFace;
import org.telegram.ui.Components.Paint.RenderView;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.UndoStore;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;
import org.telegram.ui.Components.Paint.Views.EntitiesContainerView;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Paint.Views.LinkPreview;
import org.telegram.ui.Components.Paint.Views.LinkView;
import org.telegram.ui.Components.Paint.Views.LocationView;
import org.telegram.ui.Components.Paint.Views.MessageEntityView;
import org.telegram.ui.Components.Paint.Views.PaintCancelView;
import org.telegram.ui.Components.Paint.Views.PaintColorsListView;
import org.telegram.ui.Components.Paint.Views.PaintDoneView;
import org.telegram.ui.Components.Paint.Views.PaintTextOptionsView;
import org.telegram.ui.Components.Paint.Views.PaintToolsView;
import org.telegram.ui.Components.Paint.Views.PaintTypefaceListView;
import org.telegram.ui.Components.Paint.Views.PaintWeightChooserView;
import org.telegram.ui.Components.Paint.Views.PhotoView;
import org.telegram.ui.Components.Paint.Views.ReactionWidgetEntityView;
import org.telegram.ui.Components.Paint.Views.RoundView;
import org.telegram.ui.Components.Paint.Views.StickerView;
import org.telegram.ui.Components.Paint.Views.TextPaintView;
import org.telegram.ui.Components.Paint.Views.WeatherView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.Reactions.ReactionsUtils;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.Components.Size;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.Stories.DarkThemeResourceProvider;
import org.telegram.ui.WrappedResourceProvider;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaintView extends SizeNotifierFrameLayoutPhoto implements IPhotoPaintView, PaintToolsView.Delegate, EntityView.EntityViewDelegate, PaintTextOptionsView.Delegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StoryRecorder.Touchable {
    private PaintCancelView cancelButton;
    private PaintDoneView doneButton;
    private float offsetTranslationY;

    private Bitmap bitmapToEdit;
    private Bitmap blurBitmapToEdit;
    private Bitmap facesBitmap;
    private UndoStore undoStore;

    private DispatchQueue queue;

    private MediaController.CropState currentCropState;
    private float panTranslationProgress;
    private float panTranslationY, scale, inputTransformX, inputTransformY, transformX, transformY, imageWidth, imageHeight;
    private boolean ignoreLayout;
    private float baseScale;
    private Size paintingSize;
    public boolean drawForThemeToggle, clipVideoMessageForBitmap;

    private EntityView currentEntityView;
    private boolean editingText;
    private int selectedTextType;
    public boolean enteredThroughText;

    private boolean inBubbleMode;

    private RenderView renderView;
    private View renderInputView;
    private FrameLayout selectionContainerView;
    public EntitiesContainerView entitiesView;
    private FrameLayout topLayout;
    private FrameLayout bottomLayout;
    private FrameLayout overlayLayout;
    private FrameLayout pipetteContainerLayout;
    private LinearLayout tabsLayout;
    private View textDim;

    private int tabsSelectedIndex = 0;
    private int tabsNewSelectedIndex = -1;
    private float tabsSelectionProgress;
    private ValueAnimator tabsSelectionAnimator;

    private boolean ignoreToolChangeAnimationOnce;

    private PaintWeightChooserView weightChooserView;
    private final PaintWeightChooserView.ValueOverride weightDefaultValueOverride = new PaintWeightChooserView.ValueOverride() {
        @Override
        public float get() {
            Brush brush = renderView.getCurrentBrush();
            if (brush == null) {
                return PersistColorPalette.getInstance(currentAccount).getCurrentWeight();
            }
            return PersistColorPalette.getInstance(currentAccount).getWeight(String.valueOf(Brush.BRUSHES_LIST.indexOf(brush)), brush.getDefaultWeight());
        }

        @Override
        public void set(float val) {
            PersistColorPalette.getInstance(currentAccount).setWeight(String.valueOf(Brush.BRUSHES_LIST.indexOf(renderView.getCurrentBrush())), val);
            colorSwatch.brushWeight = val;
            setCurrentSwatch(colorSwatch, true);
        }
    };

    private ArrayList<PhotoFace> faces;
    private int originalBitmapRotation;
    private BigInteger lcm;

    private TextView drawTab;
    private TextView stickerTab;
    private TextView textTab;

    private PaintToolsView paintToolsView;
    private PaintTextOptionsView textOptionsView;
    private PaintTypefaceListView typefaceListView;
    private ImageView undoButton;
    private LinearLayout zoomOutButton;
    private ImageView zoomOutImage;
    private TextView zoomOutText;
    private TextView undoAllButton;
    private TextView cancelTextButton;
    private TextView doneTextButton;

    private Paint typefaceMenuOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint typefaceMenuBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float typefaceMenuTransformProgress;
    private boolean isTypefaceMenuShown;
    private SpringAnimation typefaceMenuTransformAnimation;

    private PaintColorsListView colorsListView;
    private Paint colorPickerRainbowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint colorSwatchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint colorSwatchOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Swatch colorSwatch = new Swatch(Color.WHITE, 1f, 0.016773745f);
    private boolean fillShapes = false;

    private boolean isColorListShown;
    private SpringAnimation toolsTransformAnimation;
    private float toolsTransformProgress;
    private Paint toolsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int currentAccount;
    private Theme.ResourcesProvider resourcesProvider;

    private ActionBarPopupWindow popupWindow;
    private PopupWindowLayout popupLayout;
    private Rect popupRect;

    private Runnable onDoneButtonClickedListener;
    private Runnable onCancelButtonClickedListener;

    private StoryRecorder.WindowView parent;

    private AnimatorSet keyboardAnimator;
    public final KeyboardNotifier keyboardNotifier;

    private StoryEntry initialEntry;
    private ArrayList<VideoEditedInfo.MediaEntity> initialEntities;
    private int w, h;

    private ColorPickerBottomSheet colorPickerBottomSheet;
    private boolean fileFromGallery;
    private File file;
    private boolean isVideo;
    private boolean isBot;
    private boolean hasAudio;
    public ReactionsContainerLayout reactionLayout;
    ReactionWidgetEntityView reactionForEntity;
    private float reactionShowProgress;
    private boolean reactionLayoutShowing;
    private boolean invalidateReactionPosition;
    private BlurringShader.BlurManager blurManager;
    private PreviewView.TextureViewHolder videoTextureHolder;
    private PreviewView previewView;

    public void setHasAudio(boolean audio) {
        if (audio != hasAudio) {
            hasAudio = audio;
            checkEntitiesIsVideo();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public PaintView(Context context, boolean fileFromGallery, File file, boolean isVideo, boolean isBot, StoryRecorder.WindowView parent, Activity activity, int currentAccount, Bitmap bitmap, Bitmap blurBitmap, Bitmap originalBitmap, int originalRotation, ArrayList<VideoEditedInfo.MediaEntity> entities, StoryEntry entry, int viewWidth, int viewHeight, MediaController.CropState cropState, Runnable onInit, BlurringShader.BlurManager blurManager, Theme.ResourcesProvider resourcesProvider, PreviewView.TextureViewHolder videoTextureHolder, PreviewView previewView) {
        super(context, activity, true);
        setDelegate(this);
        this.blurManager = blurManager;
        this.videoTextureHolder = videoTextureHolder;
        this.fileFromGallery = fileFromGallery;
        this.file = file;
        this.isVideo = isVideo;
        this.isBot = isBot;
        this.parent = parent;
        this.w = viewWidth;
        this.h = viewHeight;
        this.previewView = previewView;

        this.currentAccount = currentAccount;
        this.resourcesProvider = new Theme.ResourcesProvider() {
            @Override
            public int getColor(int key) {
                if (key == Theme.key_actionBarDefaultSubmenuBackground) {
                    return 0xFF282829;
                } else if (key == Theme.key_actionBarDefaultSubmenuItem) {
                    return 0xFFFFFFFF;
                } else if (key == Theme.key_dialogBackground) {
                    return 0xFF1F1F1F;
                } else if (key == Theme.key_dialogTextBlack) {
                    return -592138;
                } else if (key == Theme.key_dialogTextGray3) {
                    return -8553091;
                } else if (key == Theme.key_chat_emojiPanelBackground) {
                    return 0xFF000000;
                } else if (key == Theme.key_chat_emojiPanelShadowLine) {
                    return -1610612736;
                } else if (key == Theme.key_chat_emojiBottomPanelIcon) {
                    return -9539985;
                } else if (key == Theme.key_chat_emojiPanelBackspace) {
                    return -9539985;
                } else if (key == Theme.key_chat_emojiPanelIcon) {
                    return -9539985;
//                } else if (key == Theme.key_chat_emojiPanelIconSelected) {
//                    return -10177041;
                } else if (key == Theme.key_windowBackgroundWhiteBlackText) {
                    return -1;
                } else if (key == Theme.key_featuredStickers_addedIcon) {
                    return -11754001;
                } else if (key == Theme.key_listSelector) {
                    return 0x1FFFFFFF;
                } else if (key == Theme.key_profile_tabSelectedText) {
                    return 0xFFFFFFFF;
                } else if (key == Theme.key_profile_tabText) {
                    return 0xFFFFFFFF;
                } else if (key == Theme.key_profile_tabSelectedLine) {
                    return 0xFFFFFFFF;
                } else if (key == Theme.key_profile_tabSelector) {
                    return 0x14FFFFFF;
                } else if (key == Theme.key_chat_emojiSearchIcon || key == Theme.key_featuredStickers_addedIcon) {
                    return 0xFF878787;
                } else if (key == Theme.key_chat_emojiSearchBackground) {
                    return 0x2E878787;
                } else if (key == Theme.key_windowBackgroundGray) {
                    return 0xFF0D0D0D;
                }

                if (resourcesProvider != null) {
                    return resourcesProvider.getColor(key);
                } else {
                    return Theme.getColor(key);
                }
            }

            @Override
            public Paint getPaint(String paintKey) {
                return resourcesProvider.getPaint(paintKey);
            }

            private ColorFilter animatedEmojiColorFilter;

            @Override
            public ColorFilter getAnimatedEmojiColorFilter() {
                if (animatedEmojiColorFilter == null) {
                    animatedEmojiColorFilter = new PorterDuffColorFilter(0xFFFFFFFF, PorterDuff.Mode.SRC_IN);
                }
                return animatedEmojiColorFilter;
            }
        };
        this.currentCropState = cropState;

        inBubbleMode = context instanceof BubbleActivity;

        PersistColorPalette palette = PersistColorPalette.getInstance(currentAccount);
        palette.resetCurrentColor();
        colorSwatch.color = palette.getCurrentColor();
        colorSwatch.brushWeight = palette.getCurrentWeight();

        queue = new DispatchQueue("Paint");

        bitmapToEdit = bitmap;
        blurBitmapToEdit = blurBitmap;
        facesBitmap = originalBitmap;
        originalBitmapRotation = originalRotation;
        undoStore = new UndoStore();
        undoStore.setDelegate(() -> {
            boolean canUndo = undoStore.canUndo();
            undoButton.animate().cancel();
            undoButton.animate().alpha(canUndo ? 1f : 0.6f).translationY(0).setDuration(150).start();
            undoButton.setClickable(canUndo);
            undoAllButton.animate().cancel();
            undoAllButton.animate().alpha(canUndo ? 1f : 0.6f).translationY(0).setDuration(150).start();
            undoAllButton.setClickable(canUndo);
        });

        textDim = new View(context);
        textDim.setVisibility(View.GONE);
        textDim.setBackgroundColor(0x4d000000);
        textDim.setAlpha(0f);

        renderView = new RenderView(context, new Painting(getPaintingSize(), originalBitmap, originalRotation, blurManager), bitmapToEdit, blurBitmapToEdit, entry != null && entry.isRepostMessage ? null : blurManager) {
            @Override
            public void selectBrush(Brush brush) {
                int index = 1 + Brush.BRUSHES_LIST.indexOf(brush);
                if (index > 1 && originalBitmap == null) {
                    index--;
                }
                paintToolsView.select(index);
                onBrushSelected(brush);
            }
        };
        renderView.setDelegate(new RenderView.RenderViewDelegate() {

            @Override
            public void onFirstDraw() {
                if (onInit != null) {
                    onInit.run();
                }
            }

            @Override
            public void onBeganDrawing() {
                if (currentEntityView != null) {
                    selectEntity(null);
                }
                weightChooserView.setViewHidden(true);
            }

            @Override
            public void onFinishedDrawing(boolean moved) {
                undoStore.getDelegate().historyChanged();
                weightChooserView.setViewHidden(false);
            }

            @Override
            public boolean shouldDraw() {
                boolean draw = currentEntityView == null;
                if (!draw) {
                    selectEntity(null);
                }
                return draw;
            }

            @Override
            public void invalidateInputView() {
                if (renderInputView != null) {
                    renderInputView.invalidate();
                }
            }

            @Override
            public void resetBrush() {
                if (ignoreToolChangeAnimationOnce) {
                    ignoreToolChangeAnimationOnce = false;
                    return;
                }
                paintToolsView.select(1);
                onBrushSelected(Brush.BRUSHES_LIST.get(0));
            }
        });
        renderView.setUndoStore(undoStore);
        renderView.setQueue(queue);
        renderView.setVisibility(View.INVISIBLE);
//        addView(renderView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        renderInputView = new View(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (renderView != null) {
                    renderView.onDrawForInput(canvas);
                }
            }
        };
        renderInputView.setVisibility(View.INVISIBLE);
//        addView(renderInputView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        entitiesView = new EntitiesContainerView(context, new EntitiesContainerView.EntitiesContainerViewDelegate() {
            @Override
            public boolean shouldReceiveTouches() {
                return true;
            }

            @Override
            public EntityView onSelectedEntityRequest() {
                return currentEntityView;
            }

            @Override
            public void onEntityDeselect() {
                selectEntity(null);
                if (enteredThroughText) {
                    dismiss();
                    enteredThroughText = false;
                }
                showReactionsLayout(false);
            }
        }) {
            Paint linePaint = new Paint();

            long lastUpdate;
            float stickyXAlpha, stickyYAlpha;

            {
                setWillNotDraw(false);
                linePaint.setStrokeWidth(dp(2));
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setColor(Color.WHITE);
            }

            private int lastStickyX, lastStickyY;

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                long dt = Math.min(16, System.currentTimeMillis() - lastUpdate);
                lastUpdate = System.currentTimeMillis();
                int stickyX = EntityView.STICKY_NONE, stickyY = EntityView.STICKY_NONE;

                if (currentEntityView != null && currentEntityView.hasTouchDown() && currentEntityView.hasPanned()) {
                    stickyX = currentEntityView.getStickyX();
                    stickyY = currentEntityView.getStickyY();
                }
                if (stickyX != EntityView.STICKY_NONE) {
                    lastStickyX = stickyX;
                }
                if (stickyY != EntityView.STICKY_NONE) {
                    lastStickyY = stickyY;
                }

                final float STICKY_DURATION = 150;
                if (stickyX != EntityView.STICKY_NONE && stickyXAlpha != 1f) {
                    stickyXAlpha = Math.min(1f, stickyXAlpha + dt / STICKY_DURATION);
                    invalidate();
                } else if (stickyX == EntityView.STICKY_NONE && stickyXAlpha != 0f) {
                    stickyXAlpha = Math.max(0f, stickyXAlpha - dt / STICKY_DURATION);
                    invalidate();
                }

                if (stickyY != EntityView.STICKY_NONE && stickyYAlpha != 1f) {
                    stickyYAlpha = Math.min(1f, stickyYAlpha + dt / STICKY_DURATION);
                    invalidate();
                } else if (stickyY == EntityView.STICKY_NONE && stickyYAlpha != 0f) {
                    stickyYAlpha = Math.max(0f, stickyYAlpha - dt / STICKY_DURATION);
                    invalidate();
                }

                if (stickyYAlpha != 0f) {
                    linePaint.setAlpha((int) (stickyYAlpha * 0xFF));
                    float y;
                    if (lastStickyY == EntityView.STICKY_START) {
                        y = dp(EntityView.STICKY_PADDING_Y_DP);
                    } else if (lastStickyY == EntityView.STICKY_CENTER) {
                        y = getMeasuredHeight() / 2f;
                    } else {
                        y = getMeasuredHeight() - dp(EntityView.STICKY_PADDING_Y_DP);
                    }
                    canvas.drawLine(0, y, getMeasuredWidth(), y, linePaint);
                }
                if (stickyXAlpha != 0f) {
                    linePaint.setAlpha((int) (stickyXAlpha * 0xFF));
                    float x;
                    if (lastStickyX == EntityView.STICKY_START) {
                        x = dp(EntityView.STICKY_PADDING_X_DP);
                    } else if (lastStickyX == EntityView.STICKY_CENTER) {
                        x = getMeasuredWidth() / 2f;
                    } else {
                        x = getMeasuredWidth() - dp(EntityView.STICKY_PADDING_X_DP);
                    }
                    canvas.drawLine(x, 0, x, getMeasuredHeight(), linePaint);
                }
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (w <= 0) {
                    w = entitiesView.getMeasuredWidth();
                }
                if (h <= 0) {
                    h = entitiesView.getMeasuredHeight();
                }
                setupEntities();
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (isCoverPreview) {
                    return false;
                }
                return super.dispatchTouchEvent(ev);
            }
        };
//        addView(entitiesView);

        this.initialEntry = entry;
        this.initialEntities = entities;
        if (w > 0 && h > 0) {
            setupEntities();
        }

        entitiesView.setVisibility(INVISIBLE);

        selectionContainerView = new FrameLayout(context) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return false;
            }
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (isCoverPreview) return false;
                return super.dispatchTouchEvent(ev);
            }
        };
//        addView(selectionContainerView);

        topLayout = new FrameLayout(context);
        topLayout.setPadding(dp(4), dp(12), dp(4), dp(12));
        topLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int [] {0x40000000, 0x00000000} ));
        addView(topLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));

        undoButton = new ImageView(context);
        undoButton.setImageResource(R.drawable.photo_undo2);
        undoButton.setPadding(dp(3), dp(3), dp(3), dp(3));
        undoButton.setBackground(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        undoButton.setOnClickListener(v -> {
            if (renderView != null && renderView.getCurrentBrush() instanceof Brush.Shape) {
                renderView.clearShape();
                paintToolsView.setSelectedIndex(1);
                onBrushSelected(Brush.BRUSHES_LIST.get(0));
            } else {
                undoStore.undo();
            }
        });
        undoButton.setAlpha(0.6f);
        undoButton.setClickable(false);
        topLayout.addView(undoButton, LayoutHelper.createFrame(32, 32, Gravity.TOP | Gravity.LEFT, 12, 0, 0, 0));

        zoomOutButton = new LinearLayout(context);
        zoomOutButton.setOrientation(LinearLayout.HORIZONTAL);
        zoomOutButton.setBackground(Theme.createSelectorDrawable(0x30ffffff, Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        zoomOutButton.setPadding(dp(8), 0, dp(8), 0);
        zoomOutText = new TextView(context);
        zoomOutText.setTextColor(Color.WHITE);
        zoomOutText.setTypeface(AndroidUtilities.bold());
        zoomOutText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        zoomOutText.setText(getString(R.string.PhotoEditorZoomOut));
        zoomOutImage = new ImageView(context);
        zoomOutImage.setImageResource(R.drawable.photo_zoomout);
        zoomOutButton.addView(zoomOutImage, LayoutHelper.createLinear(24, 24, Gravity.CENTER_VERTICAL, 0, 0, 8, 0));
        zoomOutButton.addView(zoomOutText, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
        zoomOutButton.setAlpha(0);
        zoomOutButton.setOnClickListener(e -> {
            PhotoViewer.getInstance().zoomOut();
        });
        topLayout.addView(zoomOutButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 32, Gravity.CENTER));

        undoAllButton = new TextView(context);
        undoAllButton.setBackground(Theme.createSelectorDrawable(0x30ffffff, Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        undoAllButton.setPadding(dp(8), 0, dp(8), 0);
        undoAllButton.setText(getString(R.string.PhotoEditorClearAll));
        undoAllButton.setGravity(Gravity.CENTER_VERTICAL);
        undoAllButton.setTextColor(Color.WHITE);
        undoAllButton.setTypeface(AndroidUtilities.bold());
        undoAllButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        undoAllButton.setOnClickListener(v -> clearAll());
        undoAllButton.setAlpha(0.6f);
        topLayout.addView(undoAllButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 32, Gravity.RIGHT, 0, 0, 4, 0));

        cancelTextButton = new TextView(context);
        cancelTextButton.setBackground(Theme.createSelectorDrawable(0x30ffffff, Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        cancelTextButton.setText(getString(R.string.Clear));
        cancelTextButton.setPadding(dp(8), 0, dp(8), 0);
        cancelTextButton.setGravity(Gravity.CENTER_VERTICAL);
        cancelTextButton.setTextColor(Color.WHITE);
        cancelTextButton.setTypeface(AndroidUtilities.bold());
        cancelTextButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        cancelTextButton.setOnClickListener(v -> {
            if (currentEntityView instanceof TextPaintView) {
                AndroidUtilities.hideKeyboard(((TextPaintView) currentEntityView).getFocusedView());
            }
            if (emojiViewVisible) {
                hideEmojiPopup(false);
            }
            removeEntity(currentEntityView);
            selectEntity(null);
        });
        cancelTextButton.setAlpha(0);
        cancelTextButton.setVisibility(View.GONE);
        topLayout.addView(cancelTextButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 32, Gravity.LEFT | Gravity.TOP, 4, 0, 0, 0));

        doneTextButton = new TextView(context);
        doneTextButton.setBackground(Theme.createSelectorDrawable(0x30ffffff, Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        doneTextButton.setText(getString(R.string.Done));
        doneTextButton.setPadding(dp(8), 0, dp(8), 0);
        doneTextButton.setGravity(Gravity.CENTER_VERTICAL);
        doneTextButton.setTextColor(Color.WHITE);
        doneTextButton.setTypeface(AndroidUtilities.bold());
        doneTextButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        doneTextButton.setOnClickListener(v -> {
            selectEntity(null);
        });
        doneTextButton.setAlpha(0);
        doneTextButton.setVisibility(View.GONE);
        topLayout.addView(doneTextButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 32, Gravity.RIGHT, 0, 0, 4, 0));

        bottomLayout = new FrameLayout(context) {
            private float lastRainbowX, lastRainbowY;
            private Path path = new Path();

            {
                setWillNotDraw(false);
                colorPickerRainbowPaint.setStyle(Paint.Style.STROKE);
                colorPickerRainbowPaint.setStrokeWidth(dp(2));
            }

            private void checkRainbow(float cx, float cy) {
                if (cx != lastRainbowX || cy != lastRainbowY) {
                    lastRainbowX = cx;
                    lastRainbowY = cy;

                    int[] colors = {
                            0xffeb4b4b,
                            0xffee82ee,
                            0xff6080e4,
                            Color.CYAN,
                            0xff8fce00,
                            Color.YELLOW,
                            0xffffa500,
                            0xffeb4b4b
                    };
                    colorPickerRainbowPaint.setShader(new SweepGradient(cx, cy, colors, null));
                }
            }

            @Override
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                if (overlayLayout != null) {
                    overlayLayout.invalidate();
                }
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                ViewGroup barView = getBarView();
                AndroidUtilities.rectTmp.set(
                        lerp(barView.getLeft(), colorsListView.getLeft(), toolsTransformProgress),
                        lerp(barView.getTop(), colorsListView.getTop(), toolsTransformProgress),
                        lerp(barView.getRight(), colorsListView.getRight(), toolsTransformProgress),
                        lerp(barView.getBottom(), colorsListView.getBottom(), toolsTransformProgress)
                );
                final float radius = lerp(dp(32), dp(24), toolsTransformProgress);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, radius, radius, toolsPaint);

                if (barView != null && barView.getChildCount() >= 1 && toolsTransformProgress != 1f) {
                    canvas.save();
                    canvas.translate(barView.getLeft(), barView.getTop());

                    View child = barView.getChildAt(0);
                    if (barView instanceof PaintTextOptionsView) {
                        child = ((PaintTextOptionsView) barView).getColorClickableView();
                    }

                    if (child.getAlpha() != 0f) {
                        canvas.scale(child.getScaleX(), child.getScaleY(), child.getPivotX(), child.getPivotY());

                        colorPickerRainbowPaint.setAlpha((int) ((1f - toolsTransformProgress) * child.getAlpha() * 0xFF));

                        int childWidth = child.getWidth() - child.getPaddingLeft() - child.getPaddingRight();
                        int childHeight = child.getHeight() - child.getPaddingTop() - child.getPaddingBottom();
                        float cx = child.getX() + child.getPaddingLeft() + childWidth / 2f, cy = child.getY() + child.getPaddingTop() + childHeight / 2f;
                        int colorCircle = colorSwatch.color;
                        if (tabsNewSelectedIndex != -1) {
                            ViewGroup barView2 = (ViewGroup) getBarView(tabsNewSelectedIndex);
                            View newView = (barView2 == null ? barView : barView2).getChildAt(0);
                            if (barView2 instanceof PaintTextOptionsView) {
                                newView = ((PaintTextOptionsView) barView2).getColorClickableView();
                            }
                            cx = lerp(cx, newView.getX() + newView.getPaddingLeft() + (newView.getWidth() - newView.getPaddingLeft() - newView.getPaddingRight()) / 2f, tabsSelectionProgress);
                            cy = lerp(cy, newView.getY() + newView.getPaddingTop() + (newView.getHeight() - newView.getPaddingTop() - newView.getPaddingBottom()) / 2f, tabsSelectionProgress);
                        }
                        if (colorsListView != null && colorsListView.getChildCount() > 0) {
                            View animateToView = colorsListView.getChildAt(0);
                            cx = lerp(cx, colorsListView.getX() - barView.getLeft() + animateToView.getX() + animateToView.getWidth() / 2f, toolsTransformProgress);
                            cy = lerp(cy, colorsListView.getY() - barView.getTop() + animateToView.getY() + animateToView.getHeight() / 2f, toolsTransformProgress);
                            int paletteFirstColor = palette.getColor(0);
                            colorCircle = ColorUtils.blendARGB(colorSwatch.color, paletteFirstColor, toolsTransformProgress);
                        }
                        checkRainbow(cx, cy);

                        float rad = Math.min(childWidth, childHeight) / 2f - dp(0.5f);
                        if (colorsListView != null && colorsListView.getChildCount() > 0) {
                            View animateToView = colorsListView.getChildAt(0);
                            rad = lerp(rad, Math.min(animateToView.getWidth() - animateToView.getPaddingLeft() - animateToView.getPaddingRight(), animateToView.getHeight() - animateToView.getPaddingTop() - animateToView.getPaddingBottom()) / 2f - dp(2f), toolsTransformProgress);
                        }
                        AndroidUtilities.rectTmp.set(cx - rad, cy - rad, cx + rad, cy + rad);
                        canvas.drawArc(AndroidUtilities.rectTmp, 0, 360, false, colorPickerRainbowPaint);

                        colorSwatchPaint.setColor(colorCircle);
                        colorSwatchPaint.setAlpha((int) (colorSwatchPaint.getAlpha() * child.getAlpha()));
                        colorSwatchOutlinePaint.setColor(colorCircle);
                        colorSwatchOutlinePaint.setAlpha((int) (0xFF * child.getAlpha()));

                        float rad2 = rad - dp(3f);
                        if (colorsListView != null && colorsListView.getSelectedColorIndex() != 0) {
                            rad2 = lerp(rad - dp(3f), rad + dp(2), toolsTransformProgress);
                        }
                        PaintColorsListView.drawColorCircle(canvas, cx, cy, rad2, colorSwatchPaint.getColor());

                        if (colorsListView != null && colorsListView.getSelectedColorIndex() == 0) {
                            colorSwatchOutlinePaint.setAlpha((int) (colorSwatchOutlinePaint.getAlpha() * toolsTransformProgress * child.getAlpha()));
                            canvas.drawCircle(cx, cy, rad - (AndroidUtilities.dp(3f) + colorSwatchOutlinePaint.getStrokeWidth()) * (1f - toolsTransformProgress), colorSwatchOutlinePaint);
                        }
                    }

                    canvas.restore();
                }
            }
        };
        bottomLayout.setPadding(dp(8), dp(8), dp(8), 0);
        bottomLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int [] {0x00000000, 0x80000000} ));
        addView(bottomLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 44 + 60, Gravity.BOTTOM));

        paintToolsView = new PaintToolsView(context, entry != null && !entry.isCollage() && !entry.isRepostMessage && blurManager != null);
        paintToolsView.setPadding(dp(16), 0, dp(16), 0);
        paintToolsView.setDelegate(this);
//        paintToolsView.setSelectedIndex(MathUtils.clamp(palette.getCurrentBrush(), 0, Brush.BRUSHES_LIST.size()) + 1);
        paintToolsView.setSelectedIndex(1);
        bottomLayout.addView(paintToolsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48));

        textOptionsView = new PaintTextOptionsView(context);
        textOptionsView.setPadding(dp(16), 0, dp(8), 0);
        textOptionsView.setVisibility(GONE);
        textOptionsView.setDelegate(this);
        post(() -> textOptionsView.setTypeface(PersistColorPalette.getInstance(currentAccount).getCurrentTypeface()));
        textOptionsView.setAlignment(PersistColorPalette.getInstance(currentAccount).getCurrentAlignment());
        bottomLayout.addView(textOptionsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48));

        overlayLayout = new FrameLayout(context) {
            {
                setWillNotDraw(false);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN && isTypefaceMenuShown) {
                    showTypefaceMenu(false);
                    return true;
                }
                return super.onTouchEvent(event);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                typefaceMenuOutlinePaint.setAlpha((int) (0x14 * textOptionsView.getAlpha() * (1f - typefaceMenuTransformProgress)));

                textOptionsView.getTypefaceCellBounds(AndroidUtilities.rectTmp);
                float yOffset = bottomLayout.getTop() + textOptionsView.getTop() + bottomLayout.getTranslationY() + textOptionsView.getTranslationY();
                AndroidUtilities.rectTmp.set(
                    lerp(AndroidUtilities.rectTmp.left, typefaceListView.getLeft(), typefaceMenuTransformProgress),
                    lerp(yOffset + AndroidUtilities.rectTmp.top, typefaceListView.getTop() - typefaceListView.getTranslationY(), typefaceMenuTransformProgress),
                    lerp(AndroidUtilities.rectTmp.right, typefaceListView.getRight(), typefaceMenuTransformProgress),
                    lerp(yOffset + AndroidUtilities.rectTmp.bottom, typefaceListView.getBottom() - typefaceListView.getTranslationY(), typefaceMenuTransformProgress)
                );
                float rad = dp(lerp(32, 16, typefaceMenuTransformProgress));

                int alpha = typefaceMenuBackgroundPaint.getAlpha();
                typefaceMenuBackgroundPaint.setAlpha((int) (alpha * typefaceMenuTransformProgress));
                canvas.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, typefaceMenuBackgroundPaint);
                typefaceMenuBackgroundPaint.setAlpha(alpha);

                canvas.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, typefaceMenuOutlinePaint);
            }
        };
        addView(overlayLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        typefaceListView = new PaintTypefaceListView(context);
        typefaceListView.setVisibility(GONE);
        typefaceListView.setOnItemClickListener((view, position) -> {
            PaintTypeface typeface = PaintTypeface.get().get(position);
            textOptionsView.setTypeface(typeface.getKey());
            onTypefaceSelected(typeface);
            showTypefaceMenu(false);
        });
        textOptionsView.setTypefaceListView(typefaceListView);
        overlayLayout.addView(typefaceListView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.BOTTOM, 0, 0, 8, 8));

        typefaceMenuOutlinePaint.setStyle(Paint.Style.FILL);
        typefaceMenuOutlinePaint.setColor(0x14ffffff);

        typefaceMenuBackgroundPaint.setColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));

        colorsListView = new PaintColorsListView(context) {
            private Path path = new Path();

            @Override
            public void draw(Canvas c) {
                ViewGroup barView = getBarView();
                AndroidUtilities.rectTmp.set(
                        lerp(barView.getLeft() - getLeft(), 0, toolsTransformProgress),
                        lerp(barView.getTop() - getTop(), 0, toolsTransformProgress),
                        lerp(barView.getRight() - getLeft(), getWidth(), toolsTransformProgress),
                        lerp(barView.getBottom() - getTop(), getHeight(), toolsTransformProgress)
                );

                path.rewind();
                path.addRoundRect(AndroidUtilities.rectTmp, dp(32), dp(32), Path.Direction.CW);

                c.save();
                c.clipPath(path);
                super.draw(c);
                c.restore();
            }
        };
        colorsListView.setVisibility(GONE);
        colorsListView.setColorPalette(PersistColorPalette.getInstance(currentAccount));
        colorsListView.setColorListener(color -> {
            setNewColor(color);
            showColorList(false);
        });
        bottomLayout.addView(colorsListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 84, Gravity.TOP, 56, 0, 56, 6));

        setupTabsLayout(context);

        cancelButton = new PaintCancelView(context);
        cancelButton.setPadding(dp(8), dp(8), dp(8), dp(8));
        cancelButton.setBackground(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        bottomLayout.addView(cancelButton, LayoutHelper.createFrame(32, 32, Gravity.BOTTOM | Gravity.LEFT, 12, 0, 0, 4));
        cancelButton.setOnClickListener(e -> {
            if (isColorListShown) {
                showColorList(false);
                return;
            }
            if (emojiViewVisible) {
                hideEmojiPopup(true);
                return;
            }
            if (editingText) {
                selectEntity(null);
                return;
            }
//            clearAll();
            if (onCancelButtonClickedListener != null) {
                onCancelButtonClickedListener.run();
            }
        });

        doneButton = new PaintDoneView(context);
        doneButton.setPadding(dp(8), dp(8), dp(8), dp(8));
        doneButton.setBackground(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        doneButton.setOnClickListener(v -> {
            if (isColorListShown) {
                colorPickerBottomSheet = new ColorPickerBottomSheet(context, this.resourcesProvider);
                colorPickerBottomSheet.setColor(colorSwatch.color).setPipetteDelegate(new ColorPickerBottomSheet.PipetteDelegate() {
                    private boolean hasPipette;

                    @Override
                    public void onStartColorPipette() {
                        hasPipette = true;
                    }

                    @Override
                    public void onStopColorPipette() {
                        hasPipette = false;
                    }

                    @Override
                    public ViewGroup getContainerView() {
                        return pipetteContainerLayout;
                    }

                    @Override
                    public View getSnapshotDrawingView() {
                        return PaintView.this;
                    }

                    @Override
                    public void onDrawImageOverCanvas(Bitmap bitmap, Canvas canvas) {
                        Matrix matrix = renderView.getMatrix();
                        canvas.save();
                        canvas.translate(renderView.getX(), renderView.getY());
                        canvas.concat(matrix);
                        canvas.scale(renderView.getWidth() / (float) originalBitmap.getWidth(), renderView.getHeight() / (float) originalBitmap.getHeight(), 0, 0);
                        canvas.drawBitmap(originalBitmap, 0, 0, null);
                        canvas.restore();
                    }

                    @Override
                    public boolean isPipetteVisible() {
                        return hasPipette;
                    }

                    @Override
                    public boolean isPipetteAvailable() {
                        // TODO: Get bitmap from VideoPlayer to support videos
                        return originalBitmap != null;
                    }

                    @Override
                    public void onColorSelected(int color) {
                        showColorList(false);

                        palette.selectColor(color);
                        palette.saveColors();
                        setNewColor(color);
                        colorsListView.setSelectedColorIndex(palette.getCurrentColorPosition());
                        colorsListView.getAdapter().notifyDataSetChanged();
                    }
                }).setColorListener(color -> {
                    palette.selectColor(color);
                    palette.saveColors();
                    setNewColor(color);
                    colorsListView.setSelectedColorIndex(palette.getCurrentColorPosition());
                    colorPickerBottomSheet = null;
                }).show();
                return;
            }
            if (onDoneButtonClickedListener != null) {
                onDoneButtonClickedListener.run();
            }
        });
        bottomLayout.addView(doneButton, LayoutHelper.createFrame(32, 32, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 12, 4));

        weightChooserView = new PaintWeightChooserView(context);
        weightChooserView.setColorSwatch(colorSwatch);
        weightChooserView.setRenderView(renderView);
        weightChooserView.setValueOverride(weightDefaultValueOverride);
        colorSwatch.brushWeight = weightDefaultValueOverride.get();
        weightChooserView.setOnUpdate(()-> {
            setCurrentSwatch(colorSwatch, true);
            PersistColorPalette.getInstance(currentAccount).setCurrentWeight(colorSwatch.brushWeight);
        });
        addView(weightChooserView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        pipetteContainerLayout = new FrameLayout(context);
        addView(pipetteContainerLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        colorSwatchOutlinePaint.setStyle(Paint.Style.STROKE);
        colorSwatchOutlinePaint.setStrokeWidth(dp(2));

        setCurrentSwatch(colorSwatch, true);
//        onBrushSelected(Brush.BRUSHES_LIST.get(MathUtils.clamp(palette.getCurrentBrush(), 0, Brush.BRUSHES_LIST.size())));
        onBrushSelected(Brush.BRUSHES_LIST.get(0));
        updateColors();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setSystemGestureExclusionRects(Arrays.asList(new Rect(0, (int) (AndroidUtilities.displaySize.y * .35f), dp(100), (int) (AndroidUtilities.displaySize.y * .65))));
        }

        keyboardNotifier = new KeyboardNotifier(parent, keyboardHeight -> {
            keyboardHeight = Math.max(keyboardHeight - parent.getBottomPadding2(), emojiPadding - parent.getPaddingUnderContainer());
            keyboardHeight = Math.max(0, keyboardHeight);

            notifyHeightChanged();
            final boolean keyboardVisible = keyboardHeight > 0 && currentEntityView instanceof TextPaintView && ((TextPaintView) currentEntityView).getEditText().isFocused();

            if (keyboardAnimator != null) {
                keyboardAnimator.cancel();
            }
            keyboardAnimator = new AnimatorSet();
            final ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(weightChooserView, View.TRANSLATION_Y, keyboardHeight > 0 ? Math.min(0, -keyboardHeight / 2f - dp(8)) : 0));
            animators.add(ObjectAnimator.ofFloat(bottomLayout, View.TRANSLATION_Y, (keyboardHeight > 0 ? Math.min(0, -keyboardHeight + dp(40)) : 0)public EmojiView emojiView;
    private boolean emojiViewVisible, emojiViewWasVisible;
    private boolean keyboardVisible, isAnimatePopupClosing;
    private int emojiPadding, emojiWasPadding;
    private boolean translateBottomPanelAfterResize;
    private int keyboardHeight, keyboardHeightLand;
    private boolean waitingForKeyboardOpen;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private boolean destroyed;

    private Runnable openKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            if (!(currentEntityView instanceof TextPaintView)) {
                return;
            }
            final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
            if (!destroyed && editText != null && waitingForKeyboardOpen && !keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow && AndroidUtilities.isTablet()) {
                editText.requestFocus();
                AndroidUtilities.showKeyboard(editText);
                AndroidUtilities.cancelRunOnUIThread(openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(openKeyboardRunnable, 100);
            }
        }
    };

    @Override
    public void onEmojiButtonClick() {
        boolean emojiViewWasVisible = emojiViewVisible;
        if (emojiViewWasVisible && currentEntityView instanceof TextPaintView) {
            keyboardNotifier.awaitKeyboard();
            final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
            AndroidUtilities.showKeyboard(editText);
        }
        showEmojiPopup(emojiViewVisible ? 0 : 1);
    }

    private void showEmojiPopup(int show) {
        if (show == 1) {
            boolean emojiWasVisible = emojiView != null && emojiView.getVisibility() == View.VISIBLE;
            createEmojiView();

            emojiView.setVisibility(VISIBLE);
            emojiViewWasVisible = emojiViewVisible;
            emojiViewVisible = true;
            View currentView = emojiView;

            if (keyboardHeight <= 0) {
                if (AndroidUtilities.isTablet()) {
                    keyboardHeight = dp(150);
                } else {
                    keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", dp(200));
                }
            }
            if (keyboardHeightLand <= 0) {
                if (AndroidUtilities.isTablet()) {
                    keyboardHeightLand = dp(150);
                } else {
                    keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", dp(200));
                }
            }
            int currentHeight = (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? keyboardHeightLand : keyboardHeight) + parent.getPaddingUnderContainer();

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) currentView.getLayoutParams();
            layoutParams.height = currentHeight;
            currentView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet() && currentEntityView instanceof TextPaintView) {
                final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
                AndroidUtilities.hideKeyboard(editText);
            }

            emojiPadding = emojiWasPadding = currentHeight;
            keyboardNotifier.fire();
            requestLayout();

            ChatActivityEnterViewAnimatedIconView emojiButton = textOptionsView.getEmojiButton();
            if (emojiButton != null) {
                emojiButton.setState(ChatActivityEnterViewAnimatedIconView.State.KEYBOARD, true);
            }

            if (!emojiWasVisible) {
                if (keyboardVisible) {
                    translateBottomPanelAfterResize = true;
                } else {
                    ValueAnimator animator = ValueAnimator.ofFloat(emojiPadding, 0);
                    animator.addUpdateListener(animation -> {
                        float v = (float) animation.getAnimatedValue();
                        emojiView.setTranslationY(v);
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            emojiView.setTranslationY(0);
                        }
                    });
                    animator.setDuration(AdjustPanLayoutHelper.keyboardDuration);
                    animator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    animator.start();
                }

            }
        } else {
            ChatActivityEnterViewAnimatedIconView emojiButton = textOptionsView.getEmojiButton();
            if (emojiButton != null) {
                emojiButton.setState(ChatActivityEnterViewAnimatedIconView.State.SMILE, true);
            }
            if (emojiView != null) {
                emojiViewWasVisible = emojiViewVisible;
                emojiViewVisible = false;
                if (AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    emojiView.setVisibility(GONE);
                }
            }
            if (show == 0) {
                emojiPadding = 0;
                keyboardNotifier.fire();
            }
            requestLayout();
        }

        updatePlusEmojiKeyboardButton();
    }

    private void hideEmojiPopup(boolean byBackButton) {
        if (emojiViewVisible) {
            showEmojiPopup(0);
        }
        if (byBackButton) {
            if (emojiView != null && emojiView.getVisibility() == View.VISIBLE && !waitingForKeyboardOpen) {
                int height = emojiView.getMeasuredHeight();
                ValueAnimator animator = ValueAnimator.ofFloat(0, height);
                animator.addUpdateListener(animation -> {
                    float v = (float) animation.getAnimatedValue();
                    emojiView.setTranslationY(v);
                });
                isAnimatePopupClosing = true;
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimatePopupClosing = false;
                        emojiView.setTranslationY(0);
                        hideEmojiView();
                    }
                });
                animator.setDuration(AdjustPanLayoutHelper.keyboardDuration);
                animator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                animator.start();
            } else {
                hideEmojiView();
            }
        }
    }

    @Override
    public int getEmojiPadding(boolean panned) {
        return emojiPadding;
    }

    private void hideEmojiView() {
        if (!emojiViewVisible && emojiView != null && emojiView.getVisibility() != GONE) {
            emojiView.setVisibility(GONE);
        }
        int wasEmojiPadding = emojiPadding;
        emojiPadding = 0;
        if (wasEmojiPadding != emojiPadding) {
            keyboardNotifier.fire();
        }
    }

    @Override
    public int measureKeyboardHeight() {
        return keyboardNotifier.getKeyboardHeight() - parent.getBottomPadding2();
    }

    @Override
    public void onSizeChanged(int height, boolean isWidthGreater) {
        if (height > dp(50) && keyboardVisible && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
            if (isWidthGreater) {
                keyboardHeightLand = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", keyboardHeightLand).commit();
            } else {
                keyboardHeight = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", keyboardHeight).commit();
            }
        }

        if (emojiViewVisible) {
            int newHeight = (isWidthGreater ? keyboardHeightLand : keyboardHeight) + parent.getPaddingUnderContainer();

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) emojiView.getLayoutParams();
            if (layoutParams.width != AndroidUtilities.displaySize.x || layoutParams.height != newHeight) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = newHeight;
                emojiView.setLayoutParams(layoutParams);

                emojiPadding = emojiWasPadding = layoutParams.height;
                keyboardNotifier.fire();
                requestLayout();
            }
        }

        if (lastSizeChangeValue1 == height && lastSizeChangeValue2 == isWidthGreater) {
            return;
        }
        lastSizeChangeValue1 = height;
        lastSizeChangeValue2 = isWidthGreater;

        boolean oldValue = keyboardVisible;
        if (currentEntityView instanceof TextPaintView) {
            final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
            keyboardVisible = editText.isFocused() && keyboardNotifier.keyboardVisible();
        } else {
            keyboardVisible = false;
        }
        if (keyboardVisible && emojiViewVisible) {
            showEmojiPopup(0);
        }
        if (emojiPadding != 0 && !keyboardVisible && keyboardVisible != oldValue && !emojiViewVisible) {
            emojiPadding = 0;
            keyboardNotifier.fire();
            requestLayout();
        }
        updateTextDim();
        if (oldValue && !keyboardVisible && emojiPadding > 0 && translateBottomPanelAfterResize) {
            translateBottomPanelAfterResize = false;
        }
        if (keyboardVisible && waitingForKeyboardOpen) {
            waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(openKeyboardRunnable);
        }

        updatePlusEmojiKeyboardButton();
    }

    private void updateTextDim() {
        final boolean show = currentEntityView instanceof TextPaintView && (keyboardNotifier.keyboardVisible() || emojiPadding > 0) && !keyboardNotifier.ignoring;
        textDim.animate().cancel();
        textDim.setVisibility(View.VISIBLE);
        textDim.animate().alpha(show ? 1f : 0f).withEndAction(() -> {
            if (!show) {
                textDim.setVisibility(View.GONE);
            }
        }).start();
    }

    private void updatePlusEmojiKeyboardButton() {
        if (textOptionsView != null) {
            if (keyboardNotifier.keyboardVisible()) {
                textOptionsView.animatePlusToIcon(R.drawable.input_smile);
            } else if (emojiViewVisible) {
                textOptionsView.animatePlusToIcon(R.drawable.input_keyboard);
            } else {
                textOptionsView.animatePlusToIcon(R.drawable.msg_add);
            }
        }

        final boolean text = keyboardNotifier.keyboardVisible() || emojiViewVisible;

        AndroidUtilities.updateViewShow(undoAllButton, !text, false, 1, true, null);
        AndroidUtilities.updateViewShow(undoButton, !text, false, 1, true, null);

        AndroidUtilities.updateViewShow(doneTextButton, text, false, 1, true, null);
        AndroidUtilities.updateViewShow(cancelTextButton, text, false, 1, true, null);
    }

    protected void createEmojiView() {
        if (emojiView != null && emojiView.currentAccount != UserConfig.selectedAccount) {
            parent.removeView(emojiView);
            emojiView = null;
        }
        if (emojiView != null) {
            return;
        }
        emojiView = new EmojiView(null, true, false, false, getContext(), false, null, null, true, resourcesProvider, false);
        emojiView.fixBottomTabContainerTranslation = false;
        emojiView.allowEmojisForNonPremium(true);
        emojiView.setVisibility(GONE);
        if (AndroidUtilities.isTablet()) {
            emojiView.setForseMultiwindowLayout(true);
        }
        emojiView.setDelegate(new EmojiView.EmojiViewDelegate() {
            int innerTextChange;

            @Override
            public boolean onBackspace() {
                final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
                if (editText == null || editText.length() == 0) {
                    return false;
                }
                editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                return true;
            }

            @Override
            public void onAnimatedEmojiUnlockClick() {
//                BaseFragment fragment = parentFragment;
//                if (fragment == null) {
//                    fragment = new BaseFragment() {
//                        @Override
//                        public int getCurrentAccount() {
//                            return currentAccount;
//                        }
//
//                        @Override
//                        public Context getContext() {
//                            return EditTextEmoji.this.getContext();
//                        }
//
//                        @Override
//                        public Activity getParentActivity() {
//                            Context context = getContext();
//                            while (context instanceof ContextWrapper) {
//                                if (context instanceof Activity) {
//                                    return (Activity) context;
//                                }
//                                context = ((ContextWrapper) context).getBaseContext();
//                            }
//                            return null;
//                        }
//
//                        @Override
//                        public Dialog getVisibleDialog() {
//                            return new Dialog(getContext()) {
//                                @Override
//                                public void dismiss() {
//                                    hidePopup(false);
//                                    closeParent();
//                                }
//                            };
//                        }
//                    };
//                    new PremiumFeatureBottomSheet(fragment, PremiumPreviewFragment.PREMIUM_FEATURE_ANIMATED_EMOJI, false).show();
//                } else {
//                    fragment.showDialog(new PremiumFeatureBottomSheet(fragment, PremiumPreviewFragment.PREMIUM_FEATURE_ANIMATED_EMOJI, false));
//                }
            }

            @Override
            public void onEmojiSelected(String symbol) {
                if (!(currentEntityView instanceof TextPaintView)) {
                    return;
                }
                TextPaintView textPaintView = (TextPaintView) currentEntityView;
                final EditTextOutline editText = textPaintView.getEditText();
                if (editText == null) {
                    return;
                }
                int i = editText.getSelectionEnd();
                if (i < 0) {
                    i = 0;
                }
                try {
                    innerTextChange = 2;
                    CharSequence localCharSequence = Emoji.replaceEmoji(symbol, textPaintView.getFontMetricsInt(), false);
                    if (localCharSequence instanceof Spanned) {
                        Emoji.EmojiSpan[] spans = ((Spanned) localCharSequence).getSpans(0, localCharSequence.length(), Emoji.EmojiSpan.class);
                        if (spans != null) {
                            for (int a = 0; a < spans.length; ++a) {
                                spans[a].scale = .85f;
                            }
                        }
                    }
                    editText.setText(editText.getText().insert(i, localCharSequence));
                    int j = i + localCharSequence.length();
                    editText.setSelection(j, j);
                } catch (Exception e) {
                    FileLog.e(e);
                } finally {
                    innerTextChange = 0;
                }
            }

            @Override
            public void onCustomEmojiSelected(long documentId, TLRPC.Document document, String emoticon, boolean isRecent) {
                final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
                if (editText == null) {
                    return;
                }
                int i = editText.getSelectionEnd();
                if (i < 0) {
                    i = 0;
                }
                try {
                    innerTextChange = 2;
                    SpannableString spannable = new SpannableString(emoticon);
                    AnimatedEmojiSpan span;
                    if (document != null) {
                        span = new AnimatedEmojiSpan(document, 1f, editText.getPaint().getFontMetricsInt());
                    } else {
                        span = new AnimatedEmojiSpan(documentId, 1f, editText.getPaint().getFontMetricsInt());
                    }
                    spannable.setSpan(span, 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    editText.setText(editText.getText().insert(i, spannable));
                    int j = i + spannable.length();
                    editText.setSelection(j, j);
                } catch (Exception e) {
                    FileLog.e(e);
                } finally {
                    innerTextChange = 0;
                }
            }

            @Override
            public void onClearEmojiRecent() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), resourcesProvider);
                builder.setTitle(getString(R.string.ClearRecentEmojiTitle));
                builder.setMessage(getString(R.string.ClearRecentEmojiText));
                builder.setPositiveButton(getString(R.string.ClearButton), (dialogInterface, i) -> emojiView.clearRecentEmoji());
                builder.setNegativeButton(getString(R.string.Cancel), null);
                builder.show();
            }
        });
        parent.addView(emojiView);
    }

    @Override
    public float adjustPanLayoutHelperProgress() {
        return 0;
    }

    @Override
    protected void onAttachedToWindow() {
        destroyed = false;
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        destroyed = true;
        if (reactionLayout != null) {
            AndroidUtilities.removeFromParent(reactionLayout);
            reactionLayout = null;
        }
        super.onDetachedFromWindow();
    }

    protected void onGalleryClick() {
    }

    public EntityView getSelectedEntity() {
        return currentEntityView;
    }

    public RoundView findRoundView() {
        for (int i = 0; i < entitiesView.getChildCount(); ++i) {
            View child = entitiesView.getChildAt(i);
            if (child instanceof RoundView) {
                return (RoundView) child;
            }
        }
        return null;
    }

    public static int argb2Rgba(int argb) {
        int alpha = (argb >>> 24) & 0xFF;
        int red = (argb >>> 16) & 0xFF;
        int green = (argb >>> 8) & 0xFF;
        int blue = argb & 0xFF;

        return (red << 24) | (green << 16) | (blue << 8) | alpha;
    }

    protected void onPhotoEntityCropClick(PhotoView photoView) {

    }
}
