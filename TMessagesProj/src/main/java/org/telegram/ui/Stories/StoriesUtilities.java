package org.telegram.ui.Stories;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GradientTools;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.Shaker$$ExternalSyntheticLambda0;
import org.telegram.ui.Components.Text;
import org.telegram.ui.LaunchActivity;

public abstract class StoriesUtilities {
    public static GradientTools closeFriendsGradientTools;
    public static GradientTools errorGradientTools;
    public static Drawable expiredStoryDrawable;
    public static int grayLastColor;
    public static Paint grayPaint;
    public static Paint liveCutPaint;
    public static GradientTools liveGradientTools;
    public static Paint livePaint;
    public static RectF liveRect;
    public static Text liveText;
    public static GradientTools[] storiesGradientTools = new GradientTools[2];
    public static Paint[] storyCellGreyPaint = new Paint[2];
    private static final int[] storyCellGrayLastColor = new int[2];
    private static final RectF rectTmp = new RectF();
    static boolean scheduled = false;
    static int debugState = 0;
    static Runnable debugRunnable = new Runnable() { // from class: org.telegram.ui.Stories.StoriesUtilities.1
        @Override // java.lang.Runnable
        public void run() {
            int iAbs = Math.abs(Utilities.random.nextInt() % 3);
            StoriesUtilities.debugState = iAbs;
            if (iAbs == 2) {
                StoriesUtilities.debugState = 1;
            } else {
                StoriesUtilities.debugState = 2;
            }
            NotificationCenter.getInstance(UserConfig.selectedAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, 0);
            AndroidUtilities.runOnUIThread(StoriesUtilities.debugRunnable, 1000L);
            LaunchActivity.getLastFragment().getFragmentView();
        }
    };
    private static final RectF forumRect = new RectF();
    private static final Path forumRoundRectPath = new Path();
    private static final Matrix forumRoundRectMatrix = new Matrix();
    private static final PathMeasure forumRoundRectPathMeasure = new PathMeasure();
    private static final Path forumSegmentPath = new Path();

    public static void drawAvatarWithStory(long j, Canvas canvas, ImageReceiver imageReceiver, AvatarStoryParams avatarStoryParams) {
        drawAvatarWithStory(j, canvas, imageReceiver, UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId() != j && MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController().hasStories(j), avatarStoryParams);
    }

    void m19763$r8$lambda$vj5KJCnxfshabut8bbOCF844fQ(EnsureStoryFileLoadedObject ensureStoryFileLoadedObject, Runnable runnable) {
        if (ensureStoryFileLoadedObject.cancelled) {
            return;
        }
        runnable.run();
    }

    public static /* synthetic */ void m19762$r8$lambda$kfHW3kZyQMzq2sFs9GFKip6YvU(Runnable[] runnableArr, EnsureStoryFileLoadedObject ensureStoryFileLoadedObject) {
        runnableArr[0] = null;
        ensureStoryFileLoadedObject.runnable.run();
        ImageReceiver imageReceiver = ensureStoryFileLoadedObject.imageReceiver;
        if (imageReceiver != null) {
            imageReceiver.onDetachedFromWindow();
        }
    }

    public static class AvatarStoryParams {
        public float additionalInset;
        public boolean allowLongress;
        public float alpha;
        public boolean animate;
        public int animateFromUnreadState;
        ButtonBounce buttonBounce;
        public View child;
        public long crossfadeToDialog;
        public float crossfadeToDialogProgress;
        public int currentState;
        private long dialogId;
        public boolean drawHiddenStoriesAsSegments;
        public boolean drawInside;
        public boolean drawSegments;
        public boolean drawnLive;
        public boolean forceAnimateProgressToSegments;
        public int forceState;
        float globalAngle;
        public int globalState;
        boolean inc;
        public boolean isArchive;
        public boolean isDialogStoriesCell;
        public boolean isFirst;
        public boolean isLast;
        public final boolean isStoryCell;
        public float leftBottomAngleToExclude;
        public float leftTopAngleToExclude;
        Runnable longPressRunnable;
        UserStoriesLoadOperation operation;
        public RectF originalAvatarRect;
        boolean pressed;
        public int prevState;
        public int prevUnreadState;
        public float progressToArc;
        public float progressToProgressSegments;
        public float progressToSate;
        public float progressToSegments;
        public Theme.ResourcesProvider resourcesProvider;
        public float rightBottomAngleToExclude;
        public float rightTopAngleToExclude;
        public boolean showProgress;
        float startX;
        float startY;
        public int storyId;
        public TL_stories.StoryItem storyItem;
        float sweepAngle;
        public int unreadState;
        public boolean useArcProgress;

        public boolean isAvatarClickable(long j, TLRPC.Chat chat, TLRPC.User user) {
            return false;
        }

        public boolean onAvatarClick(View view, long j) {
            return false;
        }

        public void onLongPress() {
        }

        public AvatarStoryParams(boolean z) {
            this(z, null);
        }

        public AvatarStoryParams(boolean z, Theme.ResourcesProvider resourcesProvider) {
            this.drawSegments = true;
            this.animate = true;
            this.progressToSegments = 1.0f;
            this.progressToArc = 0.0f;
            this.rightTopAngleToExclude = 0.0f;
            this.rightBottomAngleToExclude = 0.0f;
            this.leftTopAngleToExclude = 0.0f;
            this.leftBottomAngleToExclude = 0.0f;
            this.useArcProgress = true;
            this.alpha = 1.0f;
            this.progressToSate = 1.0f;
            this.showProgress = false;
            this.originalAvatarRect = new RectF();
            this.allowLongress = false;
            this.isStoryCell = z;
            this.resourcesProvider = resourcesProvider;
        }

        public void updateProgressParams() {
            boolean z = this.inc;
            float f = this.sweepAngle;
            if (z) {
                float f2 = f + 0.016f;
                this.sweepAngle = f2;
                if (f2 >= 1.0f) {
                    this.sweepAngle = 1.0f;
                    this.inc = false;
                }
            } else {
                float f3 = f - 0.016f;
                this.sweepAngle = f3;
                if (f3 < 0.0f) {
                    this.sweepAngle = 0.0f;
                    this.inc = true;
                }
            }
            this.globalAngle += 1.152f;
        }

        public boolean checkOnTouchEvent(MotionEvent motionEvent, final View view) {
            TLRPC.User user;
            TLRPC.TL_recentStory tL_recentStory;
            boolean zHasHiddenStories;
            TLRPC.TL_recentStory tL_recentStory2;
            this.child = view;
            StoriesController storiesController = MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController();
            boolean z = false;
            if (motionEvent.getAction() == 0 && this.originalAvatarRect.contains(motionEvent.getX(), motionEvent.getY())) {
                TLRPC.Chat chat = null;
                if (this.dialogId > 0) {
                    user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(this.dialogId));
                } else {
                    chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(-this.dialogId));
                    user = null;
                }
                if (isAvatarClickable(this.dialogId, chat, user)) {
                    zHasHiddenStories = true;
                } else if (this.drawHiddenStoriesAsSegments) {
                    zHasHiddenStories = storiesController.hasHiddenStories();
                } else {
                    if (this.dialogId <= 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController().hasStories(this.dialogId) || (chat != null && !chat.stories_unavailable && (tL_recentStory = chat.stories_max_id) != null && tL_recentStory.max_id > 0) : MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController().hasStories(this.dialogId) || (user != null && !user.stories_unavailable && (tL_recentStory2 = user.stories_max_id) != null && tL_recentStory2.max_id > 0)) {
                        z = true;
                    }
                    zHasHiddenStories = z;
                }
                if (this.dialogId != UserConfig.getInstance(UserConfig.selectedAccount).clientUserId && zHasHiddenStories) {
                    ButtonBounce buttonBounce = this.buttonBounce;
                    if (buttonBounce == null) {
                        this.buttonBounce = new ButtonBounce(view, 1.5f, 5.0f);
                    } else {
                        buttonBounce.setView(view);
                    }
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    this.buttonBounce.setPressed(true);
                    this.pressed = true;
                    this.startX = motionEvent.getX();
                    this.startY = motionEvent.getY();
                    if (this.allowLongress) {
                        Runnable runnable = this.longPressRunnable;
                        if (runnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(runnable);
                        }
                        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Stories.StoriesUtilities$AvatarStoryParams$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$checkOnTouchEvent$0(view);
                            }
                        };
                        this.longPressRunnable = runnable2;
                        AndroidUtilities.runOnUIThread(runnable2, ViewConfiguration.getLongPressTimeout());
                    }
                }
            } else if (motionEvent.getAction() == 2 && this.pressed) {
                if (Math.abs(this.startX - motionEvent.getX()) > AndroidUtilities.touchSlop || Math.abs(this.startY - motionEvent.getY()) > AndroidUtilities.touchSlop) {
                    ButtonBounce buttonBounce2 = this.buttonBounce;
                    if (buttonBounce2 != null) {
                        buttonBounce2.setView(view);
                        this.buttonBounce.setPressed(false);
                    }
                    Runnable runnable3 = this.longPressRunnable;
                    if (runnable3 != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable3);
                    }
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    this.pressed = false;
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                ButtonBounce buttonBounce3 = this.buttonBounce;
                if (buttonBounce3 != null) {
                    buttonBounce3.setView(view);
                    this.buttonBounce.setPressed(false);
                }
                if (this.pressed && motionEvent.getAction() == 1) {
                    processOpenStory(view);
                }
                ViewParent parent = view.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).requestDisallowInterceptTouchEvent(false);
                }
                this.pressed = false;
                Runnable runnable4 = this.longPressRunnable;
                if (runnable4 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable4);
                }
            }
            return this.pressed;
        }

        public /* synthetic */ void lambda$checkOnTouchEvent$0(View view) {
            try {
                view.performHapticFeedback(0);
            } catch (Exception unused) {
            }
            ButtonBounce buttonBounce = this.buttonBounce;
            if (buttonBounce != null) {
                buttonBounce.setPressed(false);
            }
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).requestDisallowInterceptTouchEvent(false);
            }
            this.pressed = false;
            onLongPress();
        }

        private void processOpenStory(View view) {
            TLRPC.TL_recentStory tL_recentStory;
            TLRPC.TL_recentStory tL_recentStory2;
            if (onAvatarClick(view, this.dialogId)) {
                return;
            }
            MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
            StoriesController storiesController = messagesController.getStoriesController();
            if (this.drawHiddenStoriesAsSegments) {
                openStory(0L, null);
                return;
            }
            if (this.dialogId != UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()) {
                boolean zHasStories = storiesController.hasStories(this.dialogId);
                long j = this.dialogId;
                if (zHasStories) {
                    openStory(j, null);
                    return;
                }
                if (j > 0) {
                    TLRPC.User user = messagesController.getUser(Long.valueOf(j));
                    if (user == null || user.stories_unavailable || (tL_recentStory2 = user.stories_max_id) == null || tL_recentStory2.max_id <= 0) {
                        return;
                    }
                    new UserStoriesLoadOperation().load(this.dialogId, view, this);
                    return;
                }
                TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j));
                if (chat == null || chat.stories_unavailable || (tL_recentStory = chat.stories_max_id) == null || tL_recentStory.max_id <= 0) {
                    return;
                }
                new UserStoriesLoadOperation().load(this.dialogId, view, this);
            }
        }

        public void openStory(long j, Runnable runnable) {
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment == null || this.child == null) {
                return;
            }
            lastFragment.getOrCreateStoryViewer().doOnAnimationReady(runnable);
            ViewParent parent = this.child.getParent();
            lastFragment.getOrCreateStoryViewer().open(lastFragment.getContext(), j, parent instanceof RecyclerView ? StoriesListPlaceProvider.of((RecyclerListView) parent) : null);
        }

        public float getScale() {
            ButtonBounce buttonBounce = this.buttonBounce;
            if (buttonBounce == null) {
                return 1.0f;
            }
            return buttonBounce.getScale(0.08f);
        }

        public void reset() {
            UserStoriesLoadOperation userStoriesLoadOperation = this.operation;
            if (userStoriesLoadOperation != null) {
                userStoriesLoadOperation.cancel();
                this.operation = null;
            }
            this.buttonBounce = null;
            this.pressed = false;
        }

        public void onDetachFromWindow() {
            reset();
        }
    }

    public static class UserStoriesLoadOperation {
        boolean canceled;
        private int currentAccount;
        long dialogId;
        int guid = ConnectionsManager.generateClassGuid();
        AvatarStoryParams params;
        int reqId;
        View view;

        public void load(final long j, final View view, final AvatarStoryParams avatarStoryParams) {
            int i = UserConfig.selectedAccount;
            this.currentAccount = i;
            this.dialogId = j;
            this.params = avatarStoryParams;
            this.view = view;
            final MessagesController messagesController = MessagesController.getInstance(i);
            messagesController.getStoriesController().setLoading(j, true);
            view.invalidate();
            TL_stories.TL_stories_getPeerStories tL_stories_getPeerStories = new TL_stories.TL_stories_getPeerStories();
            tL_stories_getPeerStories.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_getPeerStories, new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesUtilities$UserStoriesLoadOperation$$ExternalSyntheticLambda0
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$load$3(j, view, avatarStoryParams, messagesController, tLObject, tL_error);
                }
            });
        }

        public /* synthetic */ void lambda$load$3(final long j, final View view, final AvatarStoryParams avatarStoryParams, final MessagesController messagesController, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesUtilities$UserStoriesLoadOperation$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$load$2(tLObject, j, view, avatarStoryParams, messagesController);
                }
            });
        }

        /* JADX WARN: Code duplicated, block: B:7:0x0041  */
        public /* synthetic */ void lambda$load$2(TLObject tLObject, long j, View view, final AvatarStoryParams avatarStoryParams, MessagesController messagesController) {
            final UserStoriesLoadOperation userStoriesLoadOperation;
            final long j2;
            final View view2;
            boolean z;
            TLRPC.Chat chat;
            TLRPC.User user;
            if (tLObject != null) {
                TL_stories.TL_stories_peerStories tL_stories_peerStories = (TL_stories.TL_stories_peerStories) tLObject;
                MessagesController.getInstance(this.currentAccount).putUsers(tL_stories_peerStories.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(tL_stories_peerStories.chats, false);
                TL_stories.PeerStories peerStories = tL_stories_peerStories.stories;
                if (peerStories.stories.isEmpty()) {
                    userStoriesLoadOperation = this;
                    j2 = j;
                    view2 = view;
                    z = true;
                } else {
                    MessagesController.getInstance(this.currentAccount).getStoriesController().putStories(j, peerStories);
                    userStoriesLoadOperation = this;
                    j2 = j;
                    view2 = view;
                    StoriesUtilities.ensureStoryFileLoaded(peerStories, new Runnable() { // from class: org.telegram.ui.Stories.StoriesUtilities$UserStoriesLoadOperation$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$load$1(view2, j2, avatarStoryParams);
                        }
                    });
                    z = false;
                }
            } else {
                userStoriesLoadOperation = this;
                j2 = j;
                view2 = view;
                z = true;
            }
            if (j2 > 0 && (user = messagesController.getUser(Long.valueOf(j2))) != null) {
                user.stories_unavailable = true;
                MessagesStorage.getInstance(userStoriesLoadOperation.currentAccount).putUsersAndChats(Collections.singletonList(user), null, false, true);
                messagesController.putUser(user, false);
            }
            if (j2 < 0 && (chat = messagesController.getChat(Long.valueOf(-j2))) != null) {
                chat.stories_unavailable = true;
                MessagesStorage.getInstance(userStoriesLoadOperation.currentAccount).putUsersAndChats(null, Collections.singletonList(chat), false, true);
                messagesController.putChat(chat, false);
            }
            if (z) {
                view2.invalidate();
                MessagesController.getInstance(userStoriesLoadOperation.currentAccount).getStoriesController().setLoading(j2, false);
            }
        }

        public /* synthetic */ void lambda$load$1(final View view, final long j, AvatarStoryParams avatarStoryParams) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesUtilities$UserStoriesLoadOperation$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$load$0(view, j);
                }
            }, 500L);
            avatarStoryParams.openStory(j, null);
        }

        public /* synthetic */ void lambda$load$0(View view, long j) {
            view.invalidate();
            MessagesController.getInstance(this.currentAccount).getStoriesController().setLoading(j, false);
        }

        public void cancel() {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, false);
            this.canceled = true;
            this.params = null;
        }
    }

    public static class StoryGradientTools {
        private final AnimatedColor animatedColor1;
        private final AnimatedColor animatedColor2;
        private int color1;
        private int color2;
        public final int currentAccount;
        private final Runnable invalidate;
        private final boolean isDialogCell;
        private final GradientTools tools;

        /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
        public StoryGradientTools(View view, boolean z) {
            this(new Shaker$$ExternalSyntheticLambda0(view), z);
            Objects.requireNonNull(view);
        }

        public StoryGradientTools(Runnable runnable, boolean z) {
            this.currentAccount = UserConfig.selectedAccount;
            this.invalidate = runnable;
            this.isDialogCell = z;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.animatedColor1 = new AnimatedColor(runnable, 350L, cubicBezierInterpolator);
            this.animatedColor2 = new AnimatedColor(runnable, 350L, cubicBezierInterpolator);
            GradientTools gradientTools = new GradientTools();
            this.tools = gradientTools;
            gradientTools.isDiagonal = true;
            gradientTools.isRotate = true;
            resetColors(false);
            gradientTools.paint.setStrokeWidth(AndroidUtilities.dpf2(2.0f));
            gradientTools.paint.setStyle(Paint.Style.STROKE);
            gradientTools.paint.setStrokeCap(Paint.Cap.ROUND);
        }

        public void setUser(TLRPC.User user, boolean z) {
            TLRPC.PeerColor peerColor;
            setColorId((user == null || (peerColor = user.profile_color) == null) ? -1 : peerColor.color, z);
        }

        public void setChat(TLRPC.Chat chat, boolean z) {
            TLRPC.PeerColor peerColor;
            setColorId((chat == null || (peerColor = chat.profile_color) == null) ? -1 : peerColor.color, z);
        }

        public void setColorId(int i, boolean z) {
            MessagesController.PeerColors peerColors = MessagesController.getInstance(this.currentAccount).profilePeerColors;
            setColor(peerColors == null ? null : peerColors.getColor(i), z);
        }

        public void setColor(MessagesController.PeerColor peerColor, boolean z) {
            if (peerColor != null) {
                setColors(peerColor.getStoryColor1(Theme.isCurrentThemeDark()), peerColor.getStoryColor2(Theme.isCurrentThemeDark()), z);
            } else {
                resetColors(z);
            }
        }

        private void resetColors(boolean z) {
            if (this.isDialogCell) {
                setColors(Theme.getColor(Theme.key_stories_circle_dialog1), Theme.getColor(Theme.key_stories_circle_dialog2), z);
            } else {
                setColors(Theme.getColor(Theme.key_stories_circle1), Theme.getColor(Theme.key_stories_circle2), z);
            }
        }

        private void setColors(int i, int i2, boolean z) {
            this.color1 = i;
            this.color2 = i2;
            if (!z) {
                this.animatedColor1.set(i, true);
                this.animatedColor2.set(i2, true);
            }
            Runnable runnable = this.invalidate;
            if (runnable != null) {
                runnable.run();
            }
        }

        public Paint getPaint(RectF rectF) {
            this.tools.setColors(this.animatedColor1.set(this.color1), this.animatedColor2.set(this.color2));
            this.tools.setBounds(rectF.left, rectF.top, rectF.right, rectF.bottom);
            return this.tools.paint;
        }
    }
}
