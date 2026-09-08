package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.arch.core.util.Function;
import androidx.camera.core.ImageCapture$$ExternalSyntheticBackport1;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.backup.PreferencesUtils;
import com.exteragram.messenger.components.TranslateBeforeSendWrapper;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.jvm.internal.LongCompanionObject;
import okhttp3.internal.url._UrlKt;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Cells.ShareTopicCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.BlurredBackgroundWithFadeDrawable;
import org.telegram.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import org.telegram.ui.Components.blur3.RenderNodeWithHash;
import org.telegram.ui.Components.blur3.ViewGroupPartRenderer;
import org.telegram.ui.Components.blur3.capture.IBlur3Capture;
import org.telegram.ui.Components.blur3.capture.IBlur3Hash;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.MessageStatisticActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.Stories.DarkThemeResourceProvider;

public class ShareAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private View bottomFadeView;
    private FrameLayout bulletinContainer;
    public FrameLayout bulletinContainer2;
    private BlurredBackgroundDrawable captionContainerBg;
    private float captionEditTextTopOffset;
    private float chatActivityEnterViewAnimateFromTop;
    private EditTextEmoji commentTextView;
    private int containerViewTop;
    private boolean copyLinkOnEnd;
    private float currentPanTranslationY;
    private boolean darkTheme;
    private ShareAlertDelegate delegate;
    private BlurredBackgroundDrawable emojiViewChildBg;
    private TLRPC.TL_exportedMessageLink exportedMessageLink;
    private BlurredBackgroundWithFadeDrawable fadeDrawable;
    public boolean forceDarkThemeForHint;
    private FrameLayout frameLayout;
    private FrameLayout frameLayout2;
    private boolean fullyShown;
    private RecyclerListView gridView;
    private int hasPoll;
    private boolean hideCaption;
    private boolean hideSendersName;
    private IBlur3Capture iBlur3Capture;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryFade;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryFrostedLiquidGlass;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryLiquidGlass;
    private final RectF iBlur3PositionMainTabs;
    private final ArrayList<RectF> iBlur3Positions;
    private final BlurredBackgroundSourceColor iBlur3SourceColor;
    private final BlurredBackgroundSourceRenderNode iBlur3SourceGlass;
    private final BlurredBackgroundSourceRenderNode iBlur3SourceGlassFrosted;
    private boolean includeStory;
    public boolean includeStoryFromMessage;
    private boolean isChannel;
    private int keyboardSize2;
    private float keyboardT;
    int lastOffset;
    private GridLayoutManager layoutManager;
    private LinearLayout linkContainer;
    private TextView linkCopyButton;
    private SimpleTextView linkTextView;
    private String[] linkToCopy;
    private ShareDialogsAdapter listAdapter;
    private boolean loadingLink;
    private Paint paint;
    private boolean panTranslationMoveLayout;
    private Activity parentActivity;
    private ChatActivity parentFragment;
    private FrameLayout pickerBottom;
    private FrameLayout pickerBottomLayout;
    private int previousScrollOffsetY;
    private ArrayList<DialogsSearchAdapter.RecentSearchObject> recentSearchObjects;
    private LongSparseArray<DialogsSearchAdapter.RecentSearchObject> recentSearchObjectsById;
    private RectF rect;
    RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
    private int scrollOffsetY;
    private final DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private ShareSearchAdapter searchAdapter;
    private StickerEmptyView searchEmptyView;
    private RecyclerListView searchGridView;
    private boolean searchIsVisible;
    private FillLastGridLayoutManager searchLayoutManager;
    FragmentSearchField searchView;
    private boolean searchWasVisibleBeforeTopics;
    protected Map<TLRPC.Dialog, TLRPC.TL_forumTopic> selectedDialogTopics;
    protected LongSparseArray<TLRPC.Dialog> selectedDialogs;
    private TLRPC.Dialog selectedTopicDialog;
    private ActionBarPopupWindow sendPopupWindow;
    private String sendingFile;
    protected ArrayList<MessageObject> sendingMessageObjects;
    private String[] sendingText;
    private View[] shadow;
    private AnimatorSet[] shadowAnimation;
    private Drawable shadowDrawable;
    private ShareTopicsAdapter shareTopicsAdapter;
    private LinearLayout sharesCountLayout;
    private int shiftDp;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    TL_stories.StoryItem storyItem;
    private SwitchView switchView;
    private Insets systemInsets;
    private TextPaint textPaint;
    public int timestamp;
    public CheckBox2 timestampCheckbox;
    public FrameLayout timestampFrameLayout;
    public LinearLayout timestampLayout;
    public TextView timestampTextView;
    private ValueAnimator topBackgroundAnimator;
    private int topBeforeSwitch;
    private SpringAnimation topicsAnimation;
    ActionBar topicsBackActionBar;
    private RecyclerListView topicsGridView;
    private GridLayoutManager topicsLayoutManager;
    private boolean updateSearchAdapter;
    private ChatActivityEnterView.SendButton writeButton;
    private FrameLayout writeButtonContainer;

    public static class DialogSearchResult {
        public int date;
        public TLRPC.Dialog dialog = new TLRPC.TL_dialog();
        public CharSequence name;
        public TLObject object;
    }

    public interface ShareAlertDelegate {
        boolean didCopy();

        default void didShare() {
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void onSend(LongSparseArray<TLRPC.Dialog> longSparseArray, int i, TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
    }

    public void onShareStory(View view) {
    }

    public void setStoryToShare(TL_stories.StoryItem storyItem) {
        this.storyItem = storyItem;
    }

    public class SwitchView extends FrameLayout {
        private AnimatorSet animator;
        private int currentTab;
        private int lastColor;
        private SimpleTextView leftTab;
        private LinearGradient linearGradient;
        private Paint paint;
        private RectF rect;
        private SimpleTextView rightTab;
        private View searchBackground;
        private View slidingView;

        public abstract void onTabSwitch(int i);

        public SwitchView(Context context) {
            super(context);
            this.paint = new Paint(1);
            this.rect = new RectF();
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ShareAlert.this.getThemedColor(Theme.key_dialogSearchBackground)));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
            View view2 = new View(context) { // from class: org.telegram.ui.Components.ShareAlert.SwitchView.1
                @Override // android.view.View
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    invalidate();
                }

                @Override // android.view.View
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    int offsetColor = AndroidUtilities.getOffsetColor(-9057429, -10513163, getTranslationX() / getMeasuredWidth(), 1.0f);
                    int offsetColor2 = AndroidUtilities.getOffsetColor(-11554882, -4629871, getTranslationX() / getMeasuredWidth(), 1.0f);
                    if (offsetColor != SwitchView.this.lastColor) {
                        SwitchView.this.linearGradient = new LinearGradient(0.0f, 0.0f, getMeasuredWidth(), 0.0f, new int[]{offsetColor, offsetColor2}, (float[]) null, Shader.TileMode.CLAMP);
                        SwitchView.this.paint.setShader(SwitchView.this.linearGradient);
                    }
                    SwitchView.this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    canvas.drawRoundRect(SwitchView.this.rect, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), SwitchView.this.paint);
                }
            };
            this.slidingView = view2;
            addView(view2, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.leftTab = simpleTextView;
            int i = Theme.key_voipgroup_nameText;
            simpleTextView.setTextColor(ShareAlert.this.getThemedColor(i));
            this.leftTab.setTextSize(13);
            this.leftTab.setLeftDrawable(R.drawable.msg_tabs_mic1);
            this.leftTab.setText(LocaleController.getString(R.string.VoipGroupInviteCanSpeak));
            this.leftTab.setGravity(17);
            addView(this.leftTab, LayoutHelper.createFrame(-1, -1.0f, 51, 14.0f, 0.0f, 0.0f, 0.0f));
            this.leftTab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$SwitchView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    this.f$0.lambda$new$0(view3);
                }
            });
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.rightTab = simpleTextView2;
            simpleTextView2.setTextColor(ShareAlert.this.getThemedColor(i));
            this.rightTab.setTextSize(13);
            this.rightTab.setLeftDrawable(R.drawable.msg_tabs_mic2);
            this.rightTab.setText(LocaleController.getString(R.string.VoipGroupInviteListenOnly));
            this.rightTab.setGravity(17);
            addView(this.rightTab, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 14.0f, 0.0f));
            this.rightTab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ShareAlert$SwitchView$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    this.f$0.lambda$new$1(view3);
                }
            });
        }

        public void lambda$new$1(final Context context, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ShareAlert$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0(tLObject, context);
            }
        });
    }

    public void lambda$afterTextChanged$0() {
            ShareAlert.this.updateSelectedCount(1);
        }
    }

    public void lambda$didReceivedNotification$0(View view, int[] iArr, DynamicAnimation dynamicAnimation, float f, float f2) {
            ShareAlert.this.invalidateTopicsAnimation(view, iArr, f / 1000.0f);
        }

        public void lambda$onSendLongClick$24(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public void onDataSetChanged(int i) {
                    ShareSearchAdapter.this.lastGlobalSearchId = i;
                    if (ShareSearchAdapter.this.lastLocalSearchId != i) {
                        ShareSearchAdapter.this.searchResult.clear();
                    }
                    ShareSearchAdapter shareSearchAdapter = ShareSearchAdapter.this;
                    int i2 = shareSearchAdapter.lastItemCont;
                    if (shareSearchAdapter.getItemCount() != 0 || ShareSearchAdapter.this.searchAdapterHelper.isSearchInProgress()) {
                        ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(i2);
                    } else {
                        ShareSearchAdapter shareSearchAdapter2 = ShareSearchAdapter.this;
                        if (!shareSearchAdapter2.internalDialogsIsSearching) {
                            ShareAlert.this.searchEmptyView.showProgress(false, true);
                        } else {
                            ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(i2);
                        }
                    }
                    ShareSearchAdapter.this.notifyDataSetChanged();
                    ShareAlert.this.checkCurrentList(true);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public boolean canApplySearchResults(int i) {
                    return i == ShareSearchAdapter.this.lastSearchId;
                }
            });
        }

        private void searchDialogsInternal(final String str, final int i) {
            MessagesStorage.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$searchDialogsInternal$1(str, i);
                }
            });
        }

        /* JADX WARN: Code duplicated, block: B:159:0x0389  */
        /* JADX WARN: Code duplicated, block: B:195:0x0163 A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:67:0x016c A[Catch: Exception -> 0x0418, TryCatch #0 {Exception -> 0x0418, blocks: (B:3:0x0002, B:5:0x0011, B:7:0x001e, B:9:0x002c, B:16:0x003a, B:18:0x0041, B:19:0x0043, B:20:0x0068, B:22:0x006e, B:24:0x0086, B:26:0x0090, B:27:0x0098, B:29:0x009e, B:31:0x00a9, B:32:0x00b1, B:35:0x00c3, B:36:0x00e8, B:38:0x00ee, B:41:0x0101, B:43:0x0107, B:47:0x0114, B:49:0x011c, B:52:0x0135, B:54:0x013b, B:58:0x0153, B:65:0x0163, B:67:0x016c, B:69:0x0183, B:72:0x0192, B:74:0x01c6, B:73:0x019d, B:76:0x01d4, B:80:0x01ef, B:82:0x01fc, B:84:0x0202, B:85:0x0227, B:87:0x022d, B:92:0x0244, B:94:0x024c, B:97:0x0263, B:99:0x0269, B:102:0x0280, B:103:0x0283, B:105:0x0289, B:107:0x0296, B:109:0x029c, B:111:0x02a2, B:113:0x02a6, B:115:0x02aa, B:117:0x02ae, B:119:0x02b2, B:120:0x02d1, B:121:0x02d4, B:122:0x02da, B:124:0x02e0, B:126:0x02ea, B:128:0x02ee, B:129:0x02f1, B:130:0x02f4, B:131:0x030b, B:133:0x0311, B:136:0x031d, B:139:0x0331, B:141:0x0338, B:145:0x0344, B:147:0x034c, B:150:0x0363, B:152:0x0369, B:156:0x0381, B:161:0x038c, B:163:0x0393, B:165:0x03a7, B:166:0x03ae, B:168:0x03bb, B:170:0x03f0, B:169:0x03c7, B:172:0x03f9, B:174:0x0407), top: B:179:0x0002 }] */
        /* JADX WARN: Code duplicated, block: B:69:0x0183 A[Catch: Exception -> 0x0418, TryCatch #0 {Exception -> 0x0418, blocks: (B:3:0x0002, B:5:0x0011, B:7:0x001e, B:9:0x002c, B:16:0x003a, B:18:0x0041, B:19:0x0043, B:20:0x0068, B:22:0x006e, B:24:0x0086, B:26:0x0090, B:27:0x0098, B:29:0x009e, B:31:0x00a9, B:32:0x00b1, B:35:0x00c3, B:36:0x00e8, B:38:0x00ee, B:41:0x0101, B:43:0x0107, B:47:0x0114, B:49:0x011c, B:52:0x0135, B:54:0x013b, B:58:0x0153, B:65:0x0163, B:67:0x016c, B:69:0x0183, B:72:0x0192, B:74:0x01c6, B:73:0x019d, B:76:0x01d4, B:80:0x01ef, B:82:0x01fc, B:84:0x0202, B:85:0x0227, B:87:0x022d, B:92:0x0244, B:94:0x024c, B:97:0x0263, B:99:0x0269, B:102:0x0280, B:103:0x0283, B:105:0x0289, B:107:0x0296, B:109:0x029c, B:111:0x02a2, B:113:0x02a6, B:115:0x02aa, B:117:0x02ae, B:119:0x02b2, B:120:0x02d1, B:121:0x02d4, B:122:0x02da, B:124:0x02e0, B:126:0x02ea, B:128:0x02ee, B:129:0x02f1, B:130:0x02f4, B:131:0x030b, B:133:0x0311, B:136:0x031d, B:139:0x0331, B:141:0x0338, B:145:0x0344, B:147:0x034c, B:150:0x0363, B:152:0x0369, B:156:0x0381, B:161:0x038c, B:163:0x0393, B:165:0x03a7, B:166:0x03ae, B:168:0x03bb, B:170:0x03f0, B:169:0x03c7, B:172:0x03f9, B:174:0x0407), top: B:179:0x0002 }] */
        /* JADX WARN: Code duplicated, block: B:70:0x018d  */
        /* JADX WARN: Code duplicated, block: B:72:0x0192 A[Catch: Exception -> 0x0418, TryCatch #0 {Exception -> 0x0418, blocks: (B:3:0x0002, B:5:0x0011, B:7:0x001e, B:9:0x002c, B:16:0x003a, B:18:0x0041, B:19:0x0043, B:20:0x0068, B:22:0x006e, B:24:0x0086, B:26:0x0090, B:27:0x0098, B:29:0x009e, B:31:0x00a9, B:32:0x00b1, B:35:0x00c3, B:36:0x00e8, B:38:0x00ee, B:41:0x0101, B:43:0x0107, B:47:0x0114, B:49:0x011c, B:52:0x0135, B:54:0x013b, B:58:0x0153, B:65:0x0163, B:67:0x016c, B:69:0x0183, B:72:0x0192, B:74:0x01c6, B:73:0x019d, B:76:0x01d4, B:80:0x01ef, B:82:0x01fc, B:84:0x0202, B:85:0x0227, B:87:0x022d, B:92:0x0244, B:94:0x024c, B:97:0x0263, B:99:0x0269, B:102:0x0280, B:103:0x0283, B:105:0x0289, B:107:0x0296, B:109:0x029c, B:111:0x02a2, B:113:0x02a6, B:115:0x02aa, B:117:0x02ae, B:119:0x02b2, B:120:0x02d1, B:121:0x02d4, B:122:0x02da, B:124:0x02e0, B:126:0x02ea, B:128:0x02ee, B:129:0x02f1, B:130:0x02f4, B:131:0x030b, B:133:0x0311, B:136:0x031d, B:139:0x0331, B:141:0x0338, B:145:0x0344, B:147:0x034c, B:150:0x0363, B:152:0x0369, B:156:0x0381, B:161:0x038c, B:163:0x0393, B:165:0x03a7, B:166:0x03ae, B:168:0x03bb, B:170:0x03f0, B:169:0x03c7, B:172:0x03f9, B:174:0x0407), top: B:179:0x0002 }] */
        /* JADX WARN: Code duplicated, block: B:73:0x019d A[Catch: Exception -> 0x0418, TryCatch #0 {Exception -> 0x0418, blocks: (B:3:0x0002, B:5:0x0011, B:7:0x001e, B:9:0x002c, B:16:0x003a, B:18:0x0041, B:19:0x0043, B:20:0x0068, B:22:0x006e, B:24:0x0086, B:26:0x0090, B:27:0x0098, B:29:0x009e, B:31:0x00a9, B:32:0x00b1, B:35:0x00c3, B:36:0x00e8, B:38:0x00ee, B:41:0x0101, B:43:0x0107, B:47:0x0114, B:49:0x011c, B:52:0x0135, B:54:0x013b, B:58:0x0153, B:65:0x0163, B:67:0x016c, B:69:0x0183, B:72:0x0192, B:74:0x01c6, B:73:0x019d, B:76:0x01d4, B:80:0x01ef, B:82:0x01fc, B:84:0x0202, B:85:0x0227, B:87:0x022d, B:92:0x0244, B:94:0x024c, B:97:0x0263, B:99:0x0269, B:102:0x0280, B:103:0x0283, B:105:0x0289, B:107:0x0296, B:109:0x029c, B:111:0x02a2, B:113:0x02a6, B:115:0x02aa, B:117:0x02ae, B:119:0x02b2, B:120:0x02d1, B:121:0x02d4, B:122:0x02da, B:124:0x02e0, B:126:0x02ea, B:128:0x02ee, B:129:0x02f1, B:130:0x02f4, B:131:0x030b, B:133:0x0311, B:136:0x031d, B:139:0x0331, B:141:0x0338, B:145:0x0344, B:147:0x034c, B:150:0x0363, B:152:0x0369, B:156:0x0381, B:161:0x038c, B:163:0x0393, B:165:0x03a7, B:166:0x03ae, B:168:0x03bb, B:170:0x03f0, B:169:0x03c7, B:172:0x03f9, B:174:0x0407), top: B:179:0x0002 }] */
        /* JADX WARN: Code duplicated, block: B:76:0x01d4 A[Catch: Exception -> 0x0418, LOOP:2: B:46:0x0112->B:76:0x01d4, LOOP_END, TryCatch #0 {Exception -> 0x0418, blocks: (B:3:0x0002, B:5:0x0011, B:7:0x001e, B:9:0x002c, B:16:0x003a, B:18:0x0041, B:19:0x0043, B:20:0x0068, B:22:0x006e, B:24:0x0086, B:26:0x0090, B:27:0x0098, B:29:0x009e, B:31:0x00a9, B:32:0x00b1, B:35:0x00c3, B:36:0x00e8, B:38:0x00ee, B:41:0x0101, B:43:0x0107, B:47:0x0114, B:49:0x011c, B:52:0x0135, B:54:0x013b, B:58:0x0153, B:65:0x0163, B:67:0x016c, B:69:0x0183, B:72:0x0192, B:74:0x01c6, B:73:0x019d, B:76:0x01d4, B:80:0x01ef, B:82:0x01fc, B:84:0x0202, B:85:0x0227, B:87:0x022d, B:92:0x0244, B:94:0x024c, B:97:0x0263, B:99:0x0269, B:102:0x0280, B:103:0x0283, B:105:0x0289, B:107:0x0296, B:109:0x029c, B:111:0x02a2, B:113:0x02a6, B:115:0x02aa, B:117:0x02ae, B:119:0x02b2, B:120:0x02d1, B:121:0x02d4, B:122:0x02da, B:124:0x02e0, B:126:0x02ea, B:128:0x02ee, B:129:0x02f1, B:130:0x02f4, B:131:0x030b, B:133:0x0311, B:136:0x031d, B:139:0x0331, B:141:0x0338, B:145:0x0344, B:147:0x034c, B:150:0x0363, B:152:0x0369, B:156:0x0381, B:161:0x038c, B:163:0x0393, B:165:0x03a7, B:166:0x03ae, B:168:0x03bb, B:170:0x03f0, B:169:0x03c7, B:172:0x03f9, B:174:0x0407), top: B:179:0x0002 }] */
        /* JADX WARN: Instruction removed from duplicated block: B:73:0x019d, please report this as an issue */
        public /* synthetic */ void lambda$searchDialogsInternal$1(String str, int i) {
            String[] strArr;
            int i2;
            TLRPC.TL_chatAdminRights tL_chatAdminRights;
            int i3;
            int i4;
            int i5;
            NativeByteBuffer nativeByteBufferByteBufferValue;
            TLRPC.User userTLdeserialize;
            DialogSearchResult dialogSearchResult;
            TLRPC.UserStatus userStatus;
            int i6;
            try {
                String lowerCase = str.trim().toLowerCase();
                int i7 = -1;
                if (lowerCase.length() == 0) {
                    this.lastSearchId = -1;
                    updateSearchResults(new ArrayList<>(), this.lastSearchId);
                    return;
                }
                String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
                if (lowerCase.equals(translitString) || translitString.length() == 0) {
                    translitString = null;
                }
                int i8 = 0;
                int i9 = (translitString != null ? 1 : 0) + 1;
                String[] strArr2 = new String[i9];
                strArr2[0] = lowerCase;
                if (translitString != null) {
                    strArr2[1] = translitString;
                }
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                LongSparseArray longSparseArray = new LongSparseArray();
                SQLiteCursor sQLiteCursorQueryFinalized = MessagesStorage.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400", new Object[0]);
                while (sQLiteCursorQueryFinalized.next()) {
                    long jLongValue = sQLiteCursorQueryFinalized.longValue(0);
                    DialogSearchResult dialogSearchResult2 = new DialogSearchResult();
                    dialogSearchResult2.date = sQLiteCursorQueryFinalized.intValue(1);
                    longSparseArray.put(jLongValue, dialogSearchResult2);
                    if (DialogObject.isUserDialog(jLongValue)) {
                        if (!arrayList.contains(Long.valueOf(jLongValue))) {
                            arrayList.add(Long.valueOf(jLongValue));
                        }
                    } else if (DialogObject.isChatDialog(jLongValue)) {
                        long j = -jLongValue;
                        if (!arrayList2.contains(Long.valueOf(j))) {
                            arrayList2.add(Long.valueOf(j));
                        }
                    }
                }
                sQLiteCursorQueryFinalized.dispose();
                int i10 = 2;
                String str2 = " ";
                if (arrayList.isEmpty()) {
                    strArr = strArr2;
                    i2 = 0;
                } else {
                    SQLiteCursor sQLiteCursorQueryFinalized2 = MessagesStorage.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", TextUtils.join(",", arrayList)), new Object[0]);
                    int i11 = 0;
                    while (sQLiteCursorQueryFinalized2.next()) {
                        String strStringValue = sQLiteCursorQueryFinalized2.stringValue(i10);
                        String translitString2 = LocaleController.getInstance().getTranslitString(strStringValue);
                        if (strStringValue.equals(translitString2)) {
                            translitString2 = null;
                        }
                        int iLastIndexOf = strStringValue.lastIndexOf(";;;");
                        String strSubstring = iLastIndexOf != i7 ? strStringValue.substring(iLastIndexOf + 3) : null;
                        int i12 = i8;
                        int i13 = i12;
                        while (true) {
                            if (i12 < i9) {
                                String str3 = strArr2[i12];
                                if (strStringValue.startsWith(str3)) {
                                    i4 = i12;
                                } else {
                                    i4 = i12;
                                    if (!strStringValue.contains(" " + str3)) {
                                        if (translitString2 != null) {
                                            if (!translitString2.startsWith(str3)) {
                                                if (translitString2.contains(" " + str3)) {
                                                }
                                            }
                                        }
                                        i5 = (strSubstring == null || !strSubstring.startsWith(str3)) ? i13 : 2;
                                    }
                                    if (i5 != 0) {
                                        i3 = i11;
                                        nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized2.byteBufferValue(0);
                                        if (nativeByteBufferByteBufferValue != null) {
                                            userTLdeserialize = TLRPC.User.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                                            nativeByteBufferByteBufferValue.reuse();
                                            dialogSearchResult = (DialogSearchResult) longSparseArray.get(userTLdeserialize.id);
                                            userStatus = userTLdeserialize.status;
                                            if (userStatus != null) {
                                                i6 = 1;
                                                userStatus.expires = sQLiteCursorQueryFinalized2.intValue(1);
                                            } else {
                                                i6 = 1;
                                            }
                                            if (i5 == i6) {
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName(userTLdeserialize.first_name, userTLdeserialize.last_name, str3);
                                            } else {
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName("@" + UserObject.getPublicUsername(userTLdeserialize), null, "@" + str3);
                                            }
                                            dialogSearchResult.object = userTLdeserialize;
                                            dialogSearchResult.dialog.id = userTLdeserialize.id;
                                            i11 = i3 + 1;
                                        }
                                        strArr2 = strArr2;
                                        i7 = -1;
                                        i8 = 0;
                                        i10 = 2;
                                    } else {
                                        i13 = i5;
                                        i12 = i4 + 1;
                                        i11 = i11;
                                    }
                                }
                                i5 = 1;
                                if (i5 != 0) {
                                    i3 = i11;
                                    nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized2.byteBufferValue(0);
                                    if (nativeByteBufferByteBufferValue != null) {
                                        userTLdeserialize = TLRPC.User.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                                        nativeByteBufferByteBufferValue.reuse();
                                        dialogSearchResult = (DialogSearchResult) longSparseArray.get(userTLdeserialize.id);
                                        userStatus = userTLdeserialize.status;
                                        if (userStatus != null) {
                                            i6 = 1;
                                            userStatus.expires = sQLiteCursorQueryFinalized2.intValue(1);
                                        } else {
                                            i6 = 1;
                                        }
                                        if (i5 == i6) {
                                            dialogSearchResult.name = AndroidUtilities.generateSearchName(userTLdeserialize.first_name, userTLdeserialize.last_name, str3);
                                        } else {
                                            dialogSearchResult.name = AndroidUtilities.generateSearchName("@" + UserObject.getPublicUsername(userTLdeserialize), null, "@" + str3);
                                        }
                                        dialogSearchResult.object = userTLdeserialize;
                                        dialogSearchResult.dialog.id = userTLdeserialize.id;
                                        i11 = i3 + 1;
                                    }
                                    strArr2 = strArr2;
                                    i7 = -1;
                                    i8 = 0;
                                    i10 = 2;
                                } else {
                                    i13 = i5;
                                    i12 = i4 + 1;
                                    i11 = i11;
                                }
                            } else {
                                i3 = i11;
                            }
                            strArr2 = strArr2;
                            i11 = i3;
                            strArr2 = strArr2;
                            i7 = -1;
                            i8 = 0;
                            i10 = 2;
                        }
                    }
                    strArr = strArr2;
                    sQLiteCursorQueryFinalized2.dispose();
                    i2 = i11;
                }
                if (!arrayList2.isEmpty()) {
                    SQLiteCursor sQLiteCursorQueryFinalized3 = MessagesStorage.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", TextUtils.join(",", arrayList2)), new Object[0]);
                    while (sQLiteCursorQueryFinalized3.next()) {
                        String strStringValue2 = sQLiteCursorQueryFinalized3.stringValue(1);
                        String translitString3 = LocaleController.getInstance().getTranslitString(strStringValue2);
                        if (strStringValue2.equals(translitString3)) {
                            translitString3 = null;
                        }
                        int i14 = 0;
                        while (true) {
                            if (i14 < i9) {
                                String str4 = strArr[i14];
                                if (!strStringValue2.startsWith(str4)) {
                                    if (!strStringValue2.contains(" " + str4)) {
                                        if (translitString3 != null) {
                                            if (!translitString3.startsWith(str4)) {
                                                if (translitString3.contains(" " + str4)) {
                                                }
                                            }
                                        }
                                        i14++;
                                    }
                                }
                                NativeByteBuffer nativeByteBufferByteBufferValue2 = sQLiteCursorQueryFinalized3.byteBufferValue(0);
                                if (nativeByteBufferByteBufferValue2 != null) {
                                    TLRPC.Chat chatTLdeserialize = TLRPC.Chat.TLdeserialize(nativeByteBufferByteBufferValue2, nativeByteBufferByteBufferValue2.readInt32(false), false);
                                    nativeByteBufferByteBufferValue2.reuse();
                                    if (chatTLdeserialize != null && !ChatObject.isNotInChat(chatTLdeserialize) && (!ChatObject.isChannel(chatTLdeserialize) || chatTLdeserialize.creator || (((tL_chatAdminRights = chatTLdeserialize.admin_rights) != null && tL_chatAdminRights.post_messages) || chatTLdeserialize.megagroup))) {
                                        DialogSearchResult dialogSearchResult3 = (DialogSearchResult) longSparseArray.get(-chatTLdeserialize.id);
                                        dialogSearchResult3.name = AndroidUtilities.generateSearchName(chatTLdeserialize.title, null, str4);
                                        dialogSearchResult3.object = chatTLdeserialize;
                                        dialogSearchResult3.dialog.id = -chatTLdeserialize.id;
                                        i2++;
                                    }
                                }
                            }
                        }
                    }
                    sQLiteCursorQueryFinalized3.dispose();
                }
                ArrayList<Object> arrayList3 = new ArrayList<>(i2);
                for (int i15 = 0; i15 < longSparseArray.size(); i15++) {
                    DialogSearchResult dialogSearchResult4 = (DialogSearchResult) longSparseArray.valueAt(i15);
                    if (dialogSearchResult4.object != null && dialogSearchResult4.name != null) {
                        arrayList3.add(dialogSearchResult4);
                    }
                }
                SQLiteCursor sQLiteCursorQueryFinalized4 = MessagesStorage.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
                while (sQLiteCursorQueryFinalized4.next()) {
                    if (longSparseArray.indexOfKey(sQLiteCursorQueryFinalized4.longValue(3)) < 0) {
                        char c2 = 2;
                        String strStringValue3 = sQLiteCursorQueryFinalized4.stringValue(2);
                        String translitString4 = LocaleController.getInstance().getTranslitString(strStringValue3);
                        if (strStringValue3.equals(translitString4)) {
                            translitString4 = null;
                        }
                        int iLastIndexOf2 = strStringValue3.lastIndexOf(";;;");
                        String strSubstring2 = iLastIndexOf2 != -1 ? strStringValue3.substring(iLastIndexOf2 + 3) : null;
                        int i16 = 0;
                        char c3 = 0;
                        while (true) {
                            if (i16 < i9) {
                                String str5 = strArr[i16];
                                if (strStringValue3.startsWith(str5)) {
                                    c3 = 1;
                                } else {
                                    if (strStringValue3.contains(str2 + str5)) {
                                        c3 = 1;
                                    } else {
                                        if (translitString4 != null) {
                                            if (!translitString4.startsWith(str5)) {
                                                if (translitString4.contains(str2 + str5)) {
                                                }
                                            }
                                            c3 = 1;
                                        }
                                        if (strSubstring2 != null && strSubstring2.startsWith(str5)) {
                                            c3 = c2;
                                        }
                                    }
                                }
                                if (c3 != 0) {
                                    NativeByteBuffer nativeByteBufferByteBufferValue3 = sQLiteCursorQueryFinalized4.byteBufferValue(0);
                                    if (nativeByteBufferByteBufferValue3 != null) {
                                        TLRPC.User userTLdeserialize2 = TLRPC.User.TLdeserialize(nativeByteBufferByteBufferValue3, nativeByteBufferByteBufferValue3.readInt32(false), false);
                                        nativeByteBufferByteBufferValue3.reuse();
                                        DialogSearchResult dialogSearchResult5 = new DialogSearchResult();
                                        TLRPC.UserStatus userStatus2 = userTLdeserialize2.status;
                                        if (userStatus2 != null) {
                                            userStatus2.expires = sQLiteCursorQueryFinalized4.intValue(1);
                                        }
                                        dialogSearchResult5.dialog.id = userTLdeserialize2.id;
                                        dialogSearchResult5.object = userTLdeserialize2;
                                        if (c3 == 1) {
                                            dialogSearchResult5.name = AndroidUtilities.generateSearchName(userTLdeserialize2.first_name, userTLdeserialize2.last_name, str5);
                                        } else {
                                            dialogSearchResult5.name = AndroidUtilities.generateSearchName("@" + UserObject.getPublicUsername(userTLdeserialize2), null, "@" + str5);
                                        }
                                        arrayList3.add(dialogSearchResult5);
                                        break;
                                    }
                                } else {
                                    i16++;
                                    c2 = 2;
                                }
                            }
                            break;
                        }
                        str2 = str2;
                    }
                }
                sQLiteCursorQueryFinalized4.dispose();
                Collections.sort(arrayList3, new Comparator() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda4
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        return ShareAlert.ShareSearchAdapter.$r8$lambda$N2leCj1fsSDLiXy7aIlYyot2xnY(obj, obj2);
                    }
                });
                updateSearchResults(arrayList3, i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public static /* synthetic */ int $r8$lambda$N2leCj1fsSDLiXy7aIlYyot2xnY(Object obj, Object obj2) {
            int i = ((DialogSearchResult) obj).date;
            int i2 = ((DialogSearchResult) obj2).date;
            if (i < i2) {
                return 1;
            }
            return i > i2 ? -1 : 0;
        }

        private void updateSearchResults(final ArrayList<Object> arrayList, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$updateSearchResults$2(i, arrayList);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$2(int i, ArrayList arrayList) {
            if (i != this.lastSearchId) {
                return;
            }
            getItemCount();
            this.internalDialogsIsSearching = false;
            this.lastLocalSearchId = i;
            if (this.lastGlobalSearchId != i) {
                this.searchAdapterHelper.clear();
            }
            if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                ShareAlert shareAlert = ShareAlert.this;
                shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                ShareAlert.this.searchAdapter.notifyDataSetChanged();
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLObject tLObject = ((DialogSearchResult) arrayList.get(i2)).object;
                if (tLObject instanceof TLRPC.User) {
                    MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).putUser((TLRPC.User) tLObject, true);
                } else if (tLObject instanceof TLRPC.Chat) {
                    MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).putChat((TLRPC.Chat) tLObject, true);
                }
            }
            boolean z = !this.searchResult.isEmpty() && arrayList.isEmpty();
            if (this.searchResult.isEmpty()) {
                arrayList.isEmpty();
            }
            if (z) {
                ShareAlert shareAlert2 = ShareAlert.this;
                shareAlert2.topBeforeSwitch = shareAlert2.getCurrentTop();
            }
            this.searchResult = arrayList;
            this.searchAdapterHelper.mergeResults(arrayList, null);
            int i3 = this.lastItemCont;
            if (getItemCount() == 0 && !this.searchAdapterHelper.isSearchInProgress() && !this.internalDialogsIsSearching) {
                ShareAlert.this.searchEmptyView.showProgress(false, true);
            } else {
                ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(i3);
            }
            notifyDataSetChanged();
            ShareAlert.this.checkCurrentList(true);
        }

        public void searchDialogs(final String str) {
            if (str == null || !str.equals(this.lastSearchText)) {
                this.lastSearchText = str;
                if (this.searchRunnable != null) {
                    Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                    this.searchRunnable = null;
                }
                Runnable runnable = this.searchRunnable2;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.searchRunnable2 = null;
                }
                this.searchResult.clear();
                this.searchAdapterHelper.mergeResults(null);
                this.searchAdapterHelper.queryServerSearch(null, true, true, true, true, false, 0L, false, 0, 0);
                notifyDataSetChanged();
                ShareAlert.this.checkCurrentList(true);
                if (TextUtils.isEmpty(str)) {
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    this.lastSearchId = -1;
                    this.internalDialogsIsSearching = false;
                } else {
                    this.internalDialogsIsSearching = true;
                    final int i = this.lastSearchId + 1;
                    this.lastSearchId = i;
                    ShareAlert.this.searchEmptyView.showProgress(true, true);
                    DispatchQueue dispatchQueue = Utilities.searchQueue;
                    Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$searchDialogs$4(str, i);
                        }
                    };
                    this.searchRunnable = runnable2;
                    dispatchQueue.postRunnable(runnable2, 300L);
                }
                ShareAlert.this.checkCurrentList(false);
            }
        }

        public /* synthetic */ void lambda$searchDialogs$4(final String str, final int i) {
            this.searchRunnable = null;
            searchDialogsInternal(str, i);
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$searchDialogs$3(i, str);
                }
            };
            this.searchRunnable2 = runnable;
            AndroidUtilities.runOnUIThread(runnable);
        }

        public /* synthetic */ void lambda$searchDialogs$3(int i, String str) {
            this.searchRunnable2 = null;
            if (i != this.lastSearchId) {
                return;
            }
            this.searchAdapterHelper.queryServerSearch(str, true, true, true, true, false, 0L, false, 0, i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            this.itemsCount = 0;
            this.hintsCell = -1;
            this.resentTitleCell = -1;
            this.recentDialogsStartRow = -1;
            this.searchResultsStartRow = -1;
            this.lastFilledItem = -1;
            boolean zIsEmpty = TextUtils.isEmpty(this.lastSearchText);
            int i = this.itemsCount;
            if (zIsEmpty) {
                this.firstEmptyViewCell = i;
                this.itemsCount = i + 2;
                this.hintsCell = i + 1;
                if (ShareAlert.this.recentSearchObjects.size() > 0) {
                    int i2 = this.itemsCount;
                    int i3 = i2 + 1;
                    this.itemsCount = i3;
                    this.resentTitleCell = i2;
                    this.recentDialogsStartRow = i3;
                    this.itemsCount = i3 + ShareAlert.this.recentSearchObjects.size();
                }
                int i4 = this.itemsCount;
                int i5 = i4 + 1;
                this.itemsCount = i5;
                this.lastFilledItem = i4;
                this.lastItemCont = i5;
                return i5;
            }
            int i6 = i + 1;
            this.itemsCount = i6;
            this.firstEmptyViewCell = i;
            this.searchResultsStartRow = i6;
            int size = i6 + this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size();
            this.itemsCount = size;
            if (size == 1) {
                this.firstEmptyViewCell = -1;
                this.itemsCount = 0;
                this.lastItemCont = 0;
                return 0;
            }
            int i7 = size + 1;
            this.itemsCount = i7;
            this.lastFilledItem = size;
            this.lastItemCont = i7;
            return i7;
        }

        public TLRPC.Dialog getItem(int i) {
            int i2 = this.recentDialogsStartRow;
            if (i >= i2 && i2 >= 0) {
                int i3 = i - i2;
                if (i3 >= 0 && i3 < ShareAlert.this.recentSearchObjects.size()) {
                    TLObject tLObject = ((DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(i3)).object;
                    TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
                    if (tLObject instanceof TLRPC.User) {
                        tL_dialog.id = ((TLRPC.User) tLObject).id;
                        return tL_dialog;
                    }
                    if (tLObject instanceof TLRPC.Chat) {
                        tL_dialog.id = -((TLRPC.Chat) tLObject).id;
                        return tL_dialog;
                    }
                }
                return null;
            }
            int i4 = i - 1;
            if (i4 < 0) {
                return null;
            }
            int size = this.searchResult.size();
            ArrayList<Object> arrayList = this.searchResult;
            if (i4 < size) {
                return ((DialogSearchResult) arrayList.get(i4)).dialog;
            }
            int size2 = i4 - arrayList.size();
            ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            if (size2 < localServerSearch.size()) {
                TLObject tLObject2 = localServerSearch.get(size2);
                TLRPC.TL_dialog tL_dialog2 = new TLRPC.TL_dialog();
                if (tLObject2 instanceof TLRPC.User) {
                    tL_dialog2.id = ((TLRPC.User) tLObject2).id;
                    return tL_dialog2;
                }
                if (tLObject2 instanceof TLRPC.Chat) {
                    tL_dialog2.id = -((TLRPC.Chat) tLObject2).id;
                    return tL_dialog2;
                }
            }
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return (viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 4) ? false : true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View viewShowPremiumBlock;
            if (i == 0) {
                viewShowPremiumBlock = new ProfileSearchCell(this.context, ((BottomSheet) ShareAlert.this).resourcesProvider).useCustomPaints().showPremiumBlock(true);
            } else if (i == 2) {
                RecyclerListView recyclerListView = new RecyclerListView(this.context, ((BottomSheet) ShareAlert.this).resourcesProvider) { // from class: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.3
                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        if (getParent() != null && getParent().getParent() != null) {
                            ViewParent parent = getParent().getParent();
                            boolean z = true;
                            if (!canScrollHorizontally(-1) && !canScrollHorizontally(1)) {
                                z = false;
                            }
                            parent.requestDisallowInterceptTouchEvent(z);
                        }
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                };
                this.categoryListView = recyclerListView;
                recyclerListView.setItemAnimator(null);
                recyclerListView.setLayoutAnimation(null);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context) { // from class: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.4
                    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                linearLayoutManager.setOrientation(0);
                recyclerListView.setLayoutManager(linearLayoutManager);
                DialogsSearchAdapter.CategoryAdapterRecycler categoryAdapterRecycler = new DialogsSearchAdapter.CategoryAdapterRecycler(this.context, ((BottomSheet) ShareAlert.this).currentAccount, true, true, ((BottomSheet) ShareAlert.this).resourcesProvider) { // from class: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.5
                    @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler, androidx.recyclerview.widget.RecyclerView.Adapter
                    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i2) {
                        TLRPC.Chat chat;
                        String firstName;
                        HintDialogCell hintDialogCell = (HintDialogCell) viewHolder.itemView;
                        if (ShareAlert.this.darkTheme || ShareAlert.this.forceDarkThemeForHint) {
                            hintDialogCell.setColors(Theme.key_voipgroup_nameText, Theme.key_voipgroup_inviteMembersBackground);
                        }
                        TLRPC.TL_topPeer tL_topPeer = MediaDataController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).hints.get(i2);
                        TLRPC.Peer peer = tL_topPeer.peer;
                        long j = peer.user_id;
                        TLRPC.User user = null;
                        if (j != 0) {
                            user = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getUser(Long.valueOf(tL_topPeer.peer.user_id));
                            chat = null;
                        } else {
                            long j2 = peer.channel_id;
                            if (j2 != 0) {
                                j = -j2;
                                chat = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getChat(Long.valueOf(tL_topPeer.peer.channel_id));
                            } else {
                                long j3 = peer.chat_id;
                                if (j3 != 0) {
                                    j = -j3;
                                    chat = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getChat(Long.valueOf(tL_topPeer.peer.chat_id));
                                } else {
                                    j = 0;
                                    chat = null;
                                }
                            }
                        }
                        boolean z = j == hintDialogCell.getDialogId();
                        hintDialogCell.setTag(Long.valueOf(j));
                        if (user != null) {
                            firstName = UserObject.getFirstName(user);
                        } else if (chat == null) {
                            firstName = _UrlKt.FRAGMENT_ENCODE_SET;
                        } else {
                            firstName = chat.title;
                        }
                        hintDialogCell.setDialog(j, true, firstName);
                        hintDialogCell.setChecked(ShareAlert.this.selectedDialogs.indexOfKey(j) >= 0, z);
                    }
                };
                this.categoryAdapter = categoryAdapterRecycler;
                recyclerListView.setAdapter(categoryAdapterRecycler);
                recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ShareAlert$ShareSearchAdapter$$ExternalSyntheticLambda0
                    @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                    public final void onItemClick(View view, int i2) {
                        this.f$0.lambda$onCreateViewHolder$5(view, i2);
                    }
                });
                viewShowPremiumBlock = recyclerListView;
            } else if (i == 3) {
                GraySectionCell graySectionCell = new GraySectionCell(this.context, ((BottomSheet) ShareAlert.this).resourcesProvider);
                graySectionCell.setTextColor(Theme.key_graySectionText);
                graySectionCell.setBackgroundColor(ShareAlert.this.getThemedColor(Theme.key_graySection));
                graySectionCell.setText(LocaleController.getString(R.string.Recent));
                viewShowPremiumBlock = graySectionCell;
            } else if (i == 4) {
                viewShowPremiumBlock = new View(this.context) { // from class: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.6
                    @Override // android.view.View
                    public void onMeasure(int i2, int i3) {
                        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(ShareAlert.this.searchLayoutManager.lastItemHeight, TLObject.FLAG_30));
                    }
                };
            } else if (i == 5) {
                ShareDialogCell shareDialogCell = new ShareDialogCell(this.context, 0, ((BottomSheet) ShareAlert.this).resourcesProvider);
                shareDialogCell.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
                viewShowPremiumBlock = shareDialogCell;
            } else {
                View view = new View(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 56.0f : 109.0f)));
                viewShowPremiumBlock = view;
            }
            return new RecyclerListView.Holder(viewShowPremiumBlock);
        }

        /* JADX WARN: Code duplicated, block: B:7:0x002a A[PHI: r1
  0x002a: PHI (r1v5 long) = (r1v2 long), (r1v3 long) binds: [B:6:0x0028, B:9:0x0030] A[DONT_GENERATE, DONT_INLINE]] */
        public /* synthetic */ void lambda$onCreateViewHolder$5(View view, int i) {
            HintDialogCell hintDialogCell = (HintDialogCell) view;
            TLRPC.TL_topPeer tL_topPeer = MediaDataController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).hints.get(i);
            TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
            TLRPC.Peer peer = tL_topPeer.peer;
            long j = peer.user_id;
            if (j == 0) {
                long j2 = peer.channel_id;
                if (j2 != 0) {
                    j = -j2;
                } else {
                    j2 = peer.chat_id;
                    if (j2 != 0) {
                        j = -j2;
                    } else {
                        j = 0;
                    }
                }
            }
            if (hintDialogCell.isBlocked()) {
                ShareAlert.this.showPremiumBlockedToast(hintDialogCell, j);
                return;
            }
            tL_dialog.id = j;
            ShareAlert.this.selectDialog(null, tL_dialog);
            hintDialogCell.setChecked(ShareAlert.this.selectedDialogs.indexOfKey(j) >= 0, true);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            long j;
            String name;
            CharSequence charSequence;
            long j2;
            int iIndexOfIgnoreCase;
            Object obj;
            CharSequence charSequence2;
            int iIndexOfIgnoreCase2;
            if (viewHolder.getItemViewType() == 0 || viewHolder.getItemViewType() == 5) {
                TLObject tLObject = null;
                name = null;
                CharSequence name2 = null;
                TLRPC.EncryptedChat encryptedChat = null;
                if (TextUtils.isEmpty(this.lastSearchText)) {
                    int i2 = this.recentDialogsStartRow;
                    long j3 = 0;
                    if (i2 < 0 || i < i2) {
                        obj = null;
                        charSequence2 = null;
                    } else {
                        Object obj2 = ((DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(i - i2)).object;
                        if (obj2 instanceof TLRPC.User) {
                            TLRPC.User user = (TLRPC.User) obj2;
                            j3 = user.id;
                            name2 = ContactsController.formatName(user.first_name, user.last_name);
                        } else if (obj2 instanceof TLRPC.Chat) {
                            TLRPC.Chat chat = (TLRPC.Chat) obj2;
                            j3 = -chat.id;
                            name2 = chat.title;
                        } else if (obj2 instanceof TLRPC.TL_encryptedChat) {
                            encryptedChat = (TLRPC.TL_encryptedChat) obj2;
                            TLRPC.User user2 = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getUser(Long.valueOf(encryptedChat.user_id));
                            if (user2 != null) {
                                j3 = user2.id;
                                name2 = ContactsController.formatName(user2.first_name, user2.last_name);
                            }
                        }
                        String lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                        if (TextUtils.isEmpty(lastFoundUsername) || name2 == null || (iIndexOfIgnoreCase2 = AndroidUtilities.indexOfIgnoreCase(name2.toString(), lastFoundUsername)) == -1) {
                            obj = obj2;
                            charSequence2 = name2;
                        } else {
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(name2);
                            spannableStringBuilder.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, ((BottomSheet) ShareAlert.this).resourcesProvider), iIndexOfIgnoreCase2, lastFoundUsername.length() + iIndexOfIgnoreCase2, 33);
                            obj = obj2;
                            charSequence2 = spannableStringBuilder;
                        }
                    }
                    TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
                    View view = viewHolder.itemView;
                    if (view instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) view).setData(obj, encryptedChat2, charSequence2, null, false, false);
                        ((ProfileSearchCell) viewHolder.itemView).useSeparator = i < getItemCount() + (-2);
                        return;
                    } else {
                        CharSequence charSequence3 = charSequence2;
                        if (view instanceof ShareDialogCell) {
                            ((ShareDialogCell) view).setDialog(j3, ShareAlert.this.selectedDialogs.indexOfKey(j3) >= 0, charSequence3);
                            return;
                        }
                        return;
                    }
                }
                int size = i - 1;
                int size2 = this.searchResult.size();
                ArrayList<Object> arrayList = this.searchResult;
                if (size < size2) {
                    DialogSearchResult dialogSearchResult = (DialogSearchResult) arrayList.get(size);
                    j2 = dialogSearchResult.dialog.id;
                    charSequence = dialogSearchResult.name;
                } else {
                    size -= arrayList.size();
                    tLObject = this.searchAdapterHelper.getLocalServerSearch().get(size);
                    if (tLObject instanceof TLRPC.User) {
                        TLRPC.User user3 = (TLRPC.User) tLObject;
                        j = user3.id;
                        name = ContactsController.formatName(user3.first_name, user3.last_name);
                    } else {
                        TLRPC.Chat chat2 = (TLRPC.Chat) tLObject;
                        j = -chat2.id;
                        name = chat2.title;
                    }
                    String lastFoundUsername2 = this.searchAdapterHelper.getLastFoundUsername();
                    if (TextUtils.isEmpty(lastFoundUsername2) || name == null || (iIndexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(name.toString(), lastFoundUsername2)) == -1) {
                        charSequence = name;
                    } else {
                        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(name);
                        spannableStringBuilder2.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, ((BottomSheet) ShareAlert.this).resourcesProvider), iIndexOfIgnoreCase, lastFoundUsername2.length() + iIndexOfIgnoreCase, 33);
                        charSequence = spannableStringBuilder2;
                    }
                    j2 = j;
                }
                TLObject tLObject2 = tLObject;
                View view2 = viewHolder.itemView;
                if (view2 instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) view2).setData(tLObject2, null, charSequence, null, false, false);
                    ((ProfileSearchCell) viewHolder.itemView).useSeparator = size < getItemCount() + (-2);
                    return;
                } else {
                    if (view2 instanceof ShareDialogCell) {
                        ((ShareDialogCell) view2).setDialog(j2, ShareAlert.this.selectedDialogs.indexOfKey(j2) >= 0, charSequence);
                        return;
                    }
                    return;
                }
            }
            if (viewHolder.getItemViewType() == 2) {
                ((RecyclerListView) viewHolder.itemView).getAdapter().notifyDataSetChanged();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == this.lastFilledItem) {
                return 4;
            }
            if (i == this.firstEmptyViewCell) {
                return 1;
            }
            if (i == this.hintsCell) {
                return 2;
            }
            if (i == this.resentTitleCell) {
                return 3;
            }
            return TextUtils.isEmpty(this.lastSearchText) ? 0 : 5;
        }

        public int getSpanSize(int i, int i2) {
            if (i2 == this.hintsCell || i2 == this.resentTitleCell || i2 == this.firstEmptyViewCell || i2 == this.lastFilledItem || getItemViewType(i2) == 0) {
                return i;
            }
            return 1;
        }
    }

    public void checkCurrentList(boolean z) {
        boolean z2 = true;
        if (!TextUtils.isEmpty(this.searchView.editText.getText()) || ((this.keyboardVisible && this.searchView.editText.hasFocus()) || this.searchWasVisibleBeforeTopics)) {
            this.updateSearchAdapter = true;
            if (this.selectedTopicDialog == null) {
                AndroidUtilities.updateViewVisibilityAnimated(this.gridView, false, 0.98f, true);
                AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, true);
            }
        } else {
            if (this.selectedTopicDialog == null) {
                AndroidUtilities.updateViewVisibilityAnimated(this.gridView, true, 0.98f, true);
                AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, false);
            }
            z2 = false;
        }
        if (this.searchIsVisible != z2 || z) {
            this.searchIsVisible = z2;
            this.searchAdapter.notifyDataSetChanged();
            this.listAdapter.notifyDataSetChanged();
            boolean z3 = this.searchIsVisible;
            int i = this.lastOffset;
            if (z3) {
                RecyclerListView recyclerListView = this.searchGridView;
                if (i == Integer.MAX_VALUE) {
                    ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(0, -this.searchGridView.getPaddingTop());
                } else {
                    ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(0, this.lastOffset - this.searchGridView.getPaddingTop());
                }
                this.searchAdapter.searchDialogs(this.searchView.editText.getText().toString());
                return;
            }
            GridLayoutManager gridLayoutManager = this.layoutManager;
            if (i == Integer.MAX_VALUE) {
                gridLayoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                gridLayoutManager.scrollToPositionWithOffset(0, 0);
            }
        }
    }

    private String getLink() {
        String string;
        SwitchView switchView = this.switchView;
        if (switchView != null) {
            string = this.linkToCopy[switchView.currentTab];
        } else {
            TLRPC.TL_exportedMessageLink tL_exportedMessageLink = this.exportedMessageLink;
            string = tL_exportedMessageLink != null ? tL_exportedMessageLink.link : null;
            if (string == null) {
                string = this.linkToCopy[0];
            }
        }
        CheckBox2 checkBox2 = this.timestampCheckbox;
        if (checkBox2 != null && checkBox2.isChecked()) {
            try {
                string = Uri.parse(string).buildUpon().appendQueryParameter("t", AndroidUtilities.formatTimestamp(this.timestamp)).build().toString();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return string == null ? _UrlKt.FRAGMENT_ENCODE_SET : string;
    }

    public void updateLinkTextView() {
        if (this.linkTextView != null) {
            String link = getLink();
            if (link != null) {
                if (link.startsWith("https://")) {
                    link = link.substring(8);
                } else if (link.startsWith("http://")) {
                    link = link.substring(7);
                }
            }
            this.linkTextView.setText(link);
        }
    }

    public void updateBottomOverlay() {
        AdjustPanLayoutHelper adjustPanLayoutHelper;
        if (this.frameLayout2 == null) {
            return;
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        float fDp = 0.0f;
        if (editTextEmoji != null && editTextEmoji.isPopupVisible()) {
            this.keyboardT = this.commentTextView.getEmojiPaddingShown();
        } else {
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierFrameLayout;
            if (sizeNotifierFrameLayout != null && (adjustPanLayoutHelper = sizeNotifierFrameLayout.adjustPanLayoutHelper) != null && !adjustPanLayoutHelper.animationInProgress()) {
                this.keyboardT = this.keyboardSize2 > AndroidUtilities.dp(20.0f) ? 1.0f : 0.0f;
            }
        }
        FrameLayout frameLayout = this.timestampFrameLayout;
        if (frameLayout != null) {
            frameLayout.setTranslationY(-0.0f);
            fDp = 0.0f + AndroidUtilities.dp(48.0f);
        }
        FrameLayout frameLayout2 = this.pickerBottom;
        if (frameLayout2 != null) {
            float f = -fDp;
            frameLayout2.setTranslationY(f);
            LinearLayout linearLayout = this.sharesCountLayout;
            if (linearLayout != null) {
                linearLayout.setTranslationY(f);
            }
        }
        float f2 = -fDp;
        this.frameLayout2.setTranslationY(f2);
        this.writeButtonContainer.setTranslationY(f2);
    }

    private void onTopicCellClick(TLRPC.TL_forumTopic tL_forumTopic) {
        TLRPC.Dialog dialog;
        if (tL_forumTopic == null || (dialog = this.selectedTopicDialog) == null) {
            return;
        }
        long j = dialog.id;
        boolean zIsMonoForum = MessagesController.getInstance(this.currentAccount).isMonoForum(j);
        TLRPC.Dialog dialog2 = this.selectedTopicDialog;
        this.selectedDialogs.put(j, dialog2);
        this.selectedDialogTopics.put(dialog2, tL_forumTopic);
        updateSelectedCount(2);
        if (this.searchIsVisible || this.searchWasVisibleBeforeTopics) {
            if (((TLRPC.Dialog) this.listAdapter.dialogsMap.get(dialog2.id)) == null) {
                this.listAdapter.dialogsMap.put(dialog2.id, dialog2);
                this.listAdapter.dialogs.add(!this.listAdapter.dialogs.isEmpty() ? 1 : 0, dialog2);
            }
            this.listAdapter.notifyDataSetChanged();
            this.updateSearchAdapter = false;
            this.searchView.editText.setText(_UrlKt.FRAGMENT_ENCODE_SET);
            checkCurrentList(false);
        }
        for (int i = 0; i < getMainGridView().getChildCount(); i++) {
            View childAt = getMainGridView().getChildAt(i);
            if (childAt instanceof ShareDialogCell) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) childAt;
                if (shareDialogCell.getCurrentDialog() == this.selectedTopicDialog.id) {
                    shareDialogCell.setTopic(tL_forumTopic, zIsMonoForum, true);
                    shareDialogCell.setChecked(true, true);
                }
            }
        }
        collapseTopics();
    }

    private void onTopicCreateCellClick() {
        TLRPC.Dialog dialog = this.selectedTopicDialog;
        if (dialog == null) {
            return;
        }
        this.selectedDialogs.put(dialog.id, dialog);
        this.selectedDialogTopics.remove(dialog);
        updateSelectedCount(2);
        if (this.searchIsVisible || this.searchWasVisibleBeforeTopics) {
            if (((TLRPC.Dialog) this.listAdapter.dialogsMap.get(dialog.id)) == null) {
                this.listAdapter.dialogsMap.put(dialog.id, dialog);
                this.listAdapter.dialogs.add(!this.listAdapter.dialogs.isEmpty() ? 1 : 0, dialog);
            }
            this.listAdapter.notifyDataSetChanged();
            this.updateSearchAdapter = false;
            this.searchView.editText.setText(_UrlKt.FRAGMENT_ENCODE_SET);
            checkCurrentList(false);
        }
        for (int i = 0; i < getMainGridView().getChildCount(); i++) {
            View childAt = getMainGridView().getChildAt(i);
            if (childAt instanceof ShareDialogCell) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) childAt;
                if (shareDialogCell.getCurrentDialog() == this.selectedTopicDialog.id) {
                    shareDialogCell.setTopic(null, false, true);
                    shareDialogCell.setChecked(true, true);
                }
            }
        }
        collapseTopics();
    }

    public void blur3_InvalidateBlur() {
        if (Build.VERSION.SDK_INT < 31 || this.scrollableViewNoiseSuppressor == null) {
            return;
        }
        this.iBlur3PositionMainTabs.set(0.0f, 0.0f, this.containerView.getMeasuredWidth(), this.containerView.getMeasuredHeight());
        this.iBlur3PositionMainTabs.inset(0.0f, LiteMode.isEnabled(262144) ? 0.0f : -AndroidUtilities.dp(48.0f));
        this.scrollableViewNoiseSuppressor.setupRenderNodes(this.iBlur3Positions, 1);
        this.scrollableViewNoiseSuppressor.invalidateResultRenderNodes(this.iBlur3Capture, this.containerView.getMeasuredWidth(), this.containerView.getMeasuredHeight());
    }
}
