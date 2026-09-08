package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;
import java.util.HashSet;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import me.vkryl.core.BitwiseUtils;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.TopicsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.AvatarSpan;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.GradientClip;
import org.telegram.ui.TopicCreateFragment;

@SuppressLint({"ViewConstructor"})
public class TopicsTabsView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, FactorAnimator.Target {
    private boolean allTopicsHidden;
    private long animateFromSelectedTopicId;
    private ValueAnimator animator;
    private final BoolAnimator animatorCloseButtonVisibility;
    private final BoolAnimator animatorTopicsVisibility;
    private final boolean bot;
    private final HorizontalTabView botCreateTopicButtonHorizontal;
    private final VerticalTabView botCreateTopicButtonVertical;
    private final boolean canShowProgress;
    private final ImageView closeButtonSide;
    private final ImageView closeButtonTop;
    private final int currentAccount;
    private long currentTopicId;
    private final long dialogId;
    private final HashSet<Integer> excludeTopics;
    private final BaseFragment fragment;
    private long lastSelectedTopicId;
    private final boolean mono;
    private boolean notificationsAttached;
    private Utilities.Callback2<Long, Boolean> onDialogSelected;
    private Runnable onTopicCreated;
    private Utilities.Callback2<Integer, Boolean> onTopicSelected;
    private Runnable onUpdateSideMenuPosition;
    private Boolean pendingSidemenu;
    private final Theme.ResourcesProvider resourcesProvider;
    private BlurredBackgroundDrawable sideMenuBackgroundDrawable;
    private float sideMenuBackgroundMarginBottom;
    private float sideMenuBackgroundMarginTop;
    private final UniversalRecyclerView sideTabs;
    private final FrameLayout sideTabsContainer;
    private boolean sidemenuAnimating;
    private boolean sidemenuEnabled;
    private float sidemenuT;
    private final ImageView toggleButtonSide;
    private final ImageView toggleButtonTop;
    private BlurredBackgroundDrawable topMenuBackgroundDrawable;
    private final UniversalRecyclerView topTabs;
    private final FrameLayout topTabsContainer;
    private boolean topicBottom;

    public enum Position {
        TOP,
        LEFT,
        BOTTOM
    }

    public static void lambda$new$0(View view) {
        this.onTopicSelected.run(0, Boolean.FALSE);
    }

    private void checkTopicsVisibility(boolean z) {
        ArrayList<TLRPC.TL_forumTopic> topics = MessagesController.getInstance(this.currentAccount).getTopicsController().getTopics(-this.dialogId);
        this.animatorTopicsVisibility.setValue((topics == null || topics.isEmpty() || this.allTopicsHidden) ? false : true, z);
    }

    public void onSideMenuButtonClick(View view) {
        Boolean bool = this.pendingSidemenu;
        boolean z = false;
        if (bool == null ? !this.sidemenuEnabled : !bool.booleanValue()) {
            z = true;
        }
        animateSidemenuTo(z);
    }

    public void onCloseButtonClick(View view) {
        this.sideTabs.allowReorder(false);
        this.topTabs.allowReorder(false);
        this.animatorCloseButtonVisibility.setValue(false, true);
        AndroidUtilities.updateVisibleRows(this.sideTabs);
        AndroidUtilities.updateVisibleRows(this.topTabs);
    }

    private ImageView createButton(Context context, int i, View.OnClickListener onClickListener) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(i);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setOnClickListener(onClickListener);
        ScaleStateListAnimator.apply(imageView);
        return imageView;
    }

    public void lambda$onTabLongClick$5(ItemOptions itemOptions, final long j, TLRPC.Chat chat) {
        itemOptions.dismiss();
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
        if (user != null) {
            AlertsCreator.createClearDaysDialogAlert(this.fragment, -1, user, chat, true, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.Components.TopicsTabsView$$ExternalSyntheticLambda19
                @Override 
                public final void run(boolean z) {
                    this.f$0.lambda$onTabLongClick$4(j, z);
                }
            }, this.fragment.getResourceProvider());
        }
    }

    public /* synthetic */ void lambda$onTabLongClick$4(long j, boolean z) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof ChatActivity) {
            ((ChatActivity) baseFragment).performHistoryClear(j, false, true);
        }
    }

    public /* synthetic */ void lambda$onTabLongClick$10(final ActionBarMenuSubItem actionBarMenuSubItem, final ItemOptions itemOptions, final long j, final TLRPC.User user, final TLRPC.Chat chat, final boolean z, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TopicsTabsView$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onTabLongClick$9(z, actionBarMenuSubItem, itemOptions, j, user, chat);
            }
        });
    }

    public /* synthetic */ void lambda$onTabLongClick$9(boolean z, ActionBarMenuSubItem actionBarMenuSubItem, final ItemOptions itemOptions, final long j, final TLRPC.User user, final TLRPC.Chat chat) {
        final boolean z2 = !z;
        actionBarMenuSubItem.setVisibility(0);
        actionBarMenuSubItem.setText(LocaleController.getString(!z ? R.string.UnbanUserMonoforum : R.string.BanUserMonoforum));
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TopicsTabsView$$ExternalSyntheticLambda24
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onTabLongClick$8(itemOptions, z2, j, user, chat, view);
            }
        });
    }

    public /* synthetic */ void lambda$onTabLongClick$8(ItemOptions itemOptions, boolean z, long j, TLRPC.User user, TLRPC.Chat chat, View view) {
        itemOptions.dismiss();
        if (!z) {
            MessagesController.getInstance(this.currentAccount).deleteParticipantFromChat(j, user, (TLRPC.Chat) null, false, false);
            return;
        }
        TLRPC.TL_channels_editBanned tL_channels_editBanned = new TLRPC.TL_channels_editBanned();
        tL_channels_editBanned.participant = MessagesController.getInputPeer(user);
        tL_channels_editBanned.channel = MessagesController.getInputChannel(chat);
        tL_channels_editBanned.banned_rights = new TLRPC.TL_chatBannedRights();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_editBanned, new RequestDelegate() { // from class: org.telegram.ui.Components.TopicsTabsView$$ExternalSyntheticLambda25
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$onTabLongClick$7(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$onTabLongClick$7(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            final TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            if (updates.chats.isEmpty()) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TopicsTabsView$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onTabLongClick$6(updates);
                }
            }, 1000L);
        }
    }

    public /* synthetic */ void lambda$onTabLongClick$6(TLRPC.Updates updates) {
        MessagesController.getInstance(this.currentAccount).loadFullChat(updates.chats.get(0).id, 0, true);
    }

    public /* synthetic */ void lambda$onTabLongClick$11(ItemOptions itemOptions, MessagesController messagesController, TLRPC.TL_forumTopic tL_forumTopic) {
        itemOptions.dismiss();
        messagesController.getTopicsController().pinTopic(-this.dialogId, tL_forumTopic.id, !tL_forumTopic.pinned, this.fragment);
    }

    public /* synthetic */ void lambda$onTabLongClick$12() {
        this.sideTabs.allowReorder(true);
        this.topTabs.allowReorder(true);
        this.animatorCloseButtonVisibility.setValue(true, true);
        AndroidUtilities.updateVisibleRows(this.topTabs);
        AndroidUtilities.updateVisibleRows(this.sideTabs);
    }

    public /* synthetic */ void lambda$onTabLongClick$13(ItemOptions itemOptions, TLRPC.TL_forumTopic tL_forumTopic) {
        itemOptions.dismiss();
        this.fragment.presentFragment(TopicCreateFragment.create(-this.dialogId, tL_forumTopic.id));
    }

    public /* synthetic */ void lambda$onTabLongClick$14(MessagesController messagesController, TLRPC.TL_forumTopic tL_forumTopic, ItemOptions itemOptions, ItemOptions itemOptions2) {
        if (messagesController.isDialogMuted(this.dialogId, tL_forumTopic.id)) {
            itemOptions.dismiss();
            NotificationsController.getInstance(this.currentAccount).muteDialog(this.dialogId, tL_forumTopic.id, false);
            if (BulletinFactory.canShowBulletin(this.fragment)) {
                BulletinFactory.createMuteBulletin(this.fragment, 4, 0, this.resourcesProvider).show();
                return;
            }
            return;
        }
        itemOptions.openSwipeback(itemOptions2);
    }

    public /* synthetic */ void lambda$onTabLongClick$15(ItemOptions itemOptions, TLRPC.TL_forumTopic tL_forumTopic) {
        itemOptions.dismiss();
        MessagesController.getInstance(this.currentAccount).getTopicsController().toggleCloseTopic(-this.dialogId, tL_forumTopic.id, !tL_forumTopic.closed);
    }

    public /* synthetic */ void lambda$onTabLongClick$17(ItemOptions itemOptions, TLRPC.TL_forumTopic tL_forumTopic) {
        itemOptions.dismiss();
        HashSet<Integer> hashSet = new HashSet<>();
        hashSet.add(Integer.valueOf(tL_forumTopic.id));
        deleteTopics(hashSet, new Runnable() { // from class: org.telegram.ui.Components.TopicsTabsView$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                TopicsTabsView.$r8$lambda$whz9_2q0Ul6L4zdHpqUqnJxFQVc();
            }
        });
    }

    public TLRPC.TL_forumTopic getTopic(long j) {
        ArrayList<TLRPC.TL_forumTopic> topics = MessagesController.getInstance(this.currentAccount).getTopicsController().getTopics(-this.dialogId);
        if (topics == null) {
            return null;
        }
        int size = topics.size();
        int i = 0;
        while (i < size) {
            TLRPC.TL_forumTopic tL_forumTopic = topics.get(i);
            i++;
            TLRPC.TL_forumTopic tL_forumTopic2 = tL_forumTopic;
            if (tL_forumTopic2.id == j) {
                return tL_forumTopic2;
            }
        }
        return null;
    }

    public void setCurrentTopic(long j) {
        this.currentTopicId = j;
        this.topTabs.adapter.update(true);
        this.topTabs.invalidate();
        this.sideTabs.adapter.update(true);
        VerticalTabView verticalTabView = this.botCreateTopicButtonVertical;
        if (verticalTabView != null) {
            verticalTabView.setAll(true, false, j == 0);
        }
        HorizontalTabView horizontalTabView = this.botCreateTopicButtonHorizontal;
        if (horizontalTabView != null) {
            horizontalTabView.setAll(true, false, j == 0);
        }
    }

    public void setOnTopicSelected(Utilities.Callback2<Integer, Boolean> callback2) {
        this.onTopicSelected = callback2;
    }

    public void setOnNewTopicSelected(Runnable runnable) {
        this.onTopicCreated = runnable;
    }

    public void selectTopic(long j, boolean z) {
        if (this.mono) {
            Utilities.Callback2<Long, Boolean> callback2 = this.onDialogSelected;
            if (callback2 != null) {
                callback2.run(Long.valueOf(j), Boolean.valueOf(z));
                return;
            }
            return;
        }
        Utilities.Callback2<Integer, Boolean> callback3 = this.onTopicSelected;
        if (callback3 != null) {
            callback3.run(Integer.valueOf((int) j), Boolean.valueOf(z));
        }
    }

    public void setOnDialogSelected(Utilities.Callback2<Long, Boolean> callback2) {
        this.onDialogSelected = callback2;
    }

    public static class VerticalTabView extends FrameLayout {
        private final AvatarDrawable avatarDrawable;
        private float countScale;
        private ValueAnimator counterAnimator;
        private int counterBackgroundColorKey;
        private final AnimatedTextView.AnimatedTextDrawable counterText;
        private final int currentAccount;
        private final FrameLayout imageLayoutView;
        private final BackupImageView imageView;
        private final FrameLayout.LayoutParams imageViewParams;
        private boolean isAdd;
        private boolean lastMention;
        private boolean lastReactions;
        private int lastUnread;
        private final LinearLayout layout;
        private final View lineView;
        private LoadingDrawable loadingDrawable;
        private CharSequence mentionString;
        private boolean mono;
        private boolean pinned;
        private CharSequence reactionString;
        private boolean reorder;
        private final Theme.ResourcesProvider resourcesProvider;
        private ValueAnimator selectAnimator;
        private float selectT;
        private boolean selected;
        private Shaker shaker;
        private boolean staticImage;
        private final TextView textView;
        private long topicId;

        public static /* bridge */ /* synthetic */ float m13484$$Nest$fgetcountScale(VerticalTabView verticalTabView) {
            return verticalTabView.countScale;
        }

        public static /* bridge */ /* synthetic */ int m13485$$Nest$fgetcounterBackgroundColorKey(VerticalTabView verticalTabView) {
            return verticalTabView.counterBackgroundColorKey;
        }

        public static /* bridge */ /* synthetic */ AnimatedTextView.AnimatedTextDrawable m13486$$Nest$fgetcounterText(VerticalTabView verticalTabView) {
            return verticalTabView.counterText;
        }

        public void setReorder(boolean z) {
            this.reorder = z;
            this.layout.invalidate();
        }

        public VerticalTabView(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.mono = false;
            this.pinned = false;
            this.counterBackgroundColorKey = Theme.key_chats_unreadCounter;
            this.countScale = 1.0f;
            this.topicId = 0L;
            this.isAdd = false;
            this.staticImage = false;
            this.currentAccount = i;
            this.resourcesProvider = resourcesProvider;
            LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.TopicsTabsView.VerticalTabView.1
                private final AnimatedFloat shakeAlpha = new AnimatedFloat(this, 360, CubicBezierInterpolator.EASE_OUT_QUINT);

                @Override // android.view.ViewGroup, android.view.View
                public void dispatchDraw(Canvas canvas) {
                    canvas.save();
                    float f = this.shakeAlpha.set(VerticalTabView.this.reorder);
                    if (f > 0.0f) {
                        if (VerticalTabView.this.shaker == null) {
                            VerticalTabView.this.shaker = new Shaker(this);
                        }
                        canvas.translate(getWidth() / 2.0f, getHeight() / 2.0f);
                        VerticalTabView.this.shaker.concat(canvas, f);
                        canvas.translate((-getWidth()) / 2.0f, (-getHeight()) / 2.0f);
                    }
                    super.dispatchDraw(canvas);
                    canvas.restore();
                }
            };
            this.layout = linearLayout;
            linearLayout.setWillNotDraw(false);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f, 119, 1.0f, 0.0f, 0.0f, 0.0f));
            ScaleStateListAnimator.apply(linearLayout);
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable();
            this.counterText = animatedTextDrawable;
            animatedTextDrawable.setTextSize(AndroidUtilities.dp(11.0f));
            animatedTextDrawable.setTypeface(AndroidUtilities.bold());
            animatedTextDrawable.setTextColor(Theme.getColor(Theme.key_chats_unreadCounterText, resourcesProvider));
            animatedTextDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
            animatedTextDrawable.setGravity(17);
            FrameLayout frameLayout = new FrameLayout(context, resourcesProvider) { // from class: org.telegram.ui.Components.TopicsTabsView.VerticalTabView.2
                private final AnimatedPaint backgroundPaint;
                private final Paint clipPaint;
                final /* synthetic */ Theme.ResourcesProvider val$resourcesProvider;

                {
                    this.val$resourcesProvider = resourcesProvider;
                    Paint paint = new Paint(1);
                    this.clipPaint = paint;
                    this.backgroundPaint = new AnimatedPaint(this, resourcesProvider);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    VerticalTabView.this.counterText.setCallback(this);
                }

                @Override // android.view.View
                public boolean verifyDrawable(Drawable drawable) {
                    return VerticalTabView.this.counterText == drawable || super.verifyDrawable(drawable);
                }

                /* JADX WARN: Failed to calculate best type for var: r0v2 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v2 ??, new type: org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r15v0 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r15v0 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r15v1 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r15v1 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r16v0 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r16v0 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r16v1 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r16v1 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r19v0 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r19v0 ??, new type: android.graphics.Canvas
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r1v10 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v10 ??, new type: android.graphics.Canvas
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r1v11 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v11 ??, new type: android.graphics.Canvas
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r1v15 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v15 ??, new type: android.graphics.Canvas
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r2v11 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v11 ??, new type: android.graphics.RectF
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r2v14 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v14 ??, new type: android.graphics.RectF
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r2v7 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v7 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r2v7 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v7 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r2v8 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v8 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r3v10 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v10 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r3v11 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v11 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r3v2 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v2 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r3v6 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v6 ??, new type: org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r3v9 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v9 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r4v1 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v1 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r4v10 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v10 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r4v9 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v9 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r5v3 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r5v3 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to calculate best type for var: r6v1 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v1 ??, new type: float
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /* JADX WARN: Failed to set immutable type for var: r19v0 ??
                jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r19v0 ??, new type: android.graphics.Canvas
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                	at jadx.core.dex.visitors.typeinference.TypeUpdate.applyWithWiderIgnSame(TypeUpdate.java:73)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setImmutableType(TypeInferenceVisitor.java:111)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$0(TypeInferenceVisitor.java:102)
                	at java.base/java.util.ArrayList.forEach(Unknown Source)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:102)
                	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
                Caused by: java.lang.NullPointerException
                 */
                /*  JADX ERROR: Types fix failed
                    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v7 ??, new type: float
                    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
                    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:186)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:245)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
                    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
                    Caused by: java.lang.NullPointerException
                    */
                @Override // android.view.ViewGroup, android.view.View
                public void dispatchDraw(android.graphics.Canvas r19) {
                    /*
                        Method dump skipped, instruction units count: 249
                        To view this dump add '--comments-level debug' option
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TopicsTabsView.VerticalTabView.AnonymousClass2.dispatchDraw(android.graphics.Canvas):void");
                }
            };
            this.imageLayoutView = frameLayout;
            frameLayout.setWillNotDraw(false);
            frameLayout.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 17));
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            FrameLayout.LayoutParams layoutParamsCreateFrame = LayoutHelper.createFrame(34, 34, 17);
            this.imageViewParams = layoutParamsCreateFrame;
            frameLayout.addView(backupImageView, layoutParamsCreateFrame);
            this.avatarDrawable = new AvatarDrawable();
            TextView textView = new TextView(context);
            this.textView = textView;
            int color = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider);
            int i2 = Theme.key_featuredStickers_addButton;
            textView.setTextColor(ColorUtils.blendARGB(color, Theme.getColor(i2, resourcesProvider), this.selectT));
            textView.setTextSize(1, 10.0f);
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setMaxLines(3);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 17, 4, 0, 4, 0));
            linearLayout.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
            View imageView = new ImageView(context);
            this.lineView = imageView;
            imageView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(2.33f), Theme.getColor(i2, resourcesProvider)));
            addView(imageView, LayoutHelper.createFrame(6, -1.0f, 115, -3.0f, 3.0f, 0.0f, 3.0f));
            imageView.setTranslationX(-AndroidUtilities.dp(3.0f));
            imageView.setVisibility(8);
        }

        private void setLayout(boolean z) {
            if (this.mono == z) {
                return;
            }
            this.mono = z;
            this.imageView.setRoundRadius(ExteraConfig.getAvatarCorners(28.0f));
            this.imageLayoutView.setPadding(0, AndroidUtilities.dp(z ? 7.0f : 4.0f), 0, 0);
            this.imageViewParams.width = z ? AndroidUtilities.dp(28.0f) : AndroidUtilities.dp(30.0f);
            this.imageViewParams.height = z ? AndroidUtilities.dp(28.0f) : AndroidUtilities.dp(30.0f);
        }

        private void setPinned(boolean z, boolean z2) {
            if (this.pinned != z) {
                this.pinned = z;
            }
        }

        private void setCounter(boolean z, int i, boolean z2, boolean z3, boolean z4) {
            if (z3) {
                this.counterBackgroundColorKey = Theme.key_dialogReactionMentionBackground;
                if (this.reactionString == null) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("❤️");
                    ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.mini_like_filled);
                    coloredImageSpan.setScale(0.8f, 0.8f);
                    coloredImageSpan.spaceScaleX = 0.5f;
                    coloredImageSpan.translate(-AndroidUtilities.dp(3.0f), 0.0f);
                    spannableStringBuilder.setSpan(coloredImageSpan, 0, spannableStringBuilder.length(), 33);
                    this.reactionString = spannableStringBuilder;
                }
                this.counterText.setText(this.reactionString, z4);
            } else if (z2) {
                this.counterBackgroundColorKey = z ? Theme.key_chats_unreadCounterMuted : Theme.key_chats_unreadCounter;
                if (this.mentionString == null) {
                    SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder("@");
                    ColoredImageSpan coloredImageSpan2 = new ColoredImageSpan(R.drawable.mini_mention_filled_16);
                    coloredImageSpan2.setScale(0.8f, 0.8f);
                    coloredImageSpan2.spaceScaleX = 0.5f;
                    coloredImageSpan2.translate(-AndroidUtilities.dp(3.0f), 0.0f);
                    spannableStringBuilder2.setSpan(coloredImageSpan2, 0, 1, 33);
                    this.mentionString = spannableStringBuilder2;
                }
                this.counterText.setText(this.mentionString, z4);
            } else if (i > 0) {
                this.counterBackgroundColorKey = z ? Theme.key_chats_unreadCounterMuted : Theme.key_chats_unreadCounter;
                this.counterText.setText(LocaleController.formatNumber(i, ','), z4);
            } else {
                this.counterBackgroundColorKey = Theme.key_chats_unreadCounterMuted;
                this.counterText.setText(_UrlKt.FRAGMENT_ENCODE_SET, z4);
            }
            if (z4 && (this.lastUnread < i || ((!this.lastMention && z2) || (!this.lastReactions && z3)))) {
                animateCounterBounce();
            }
            this.lastUnread = i;
            this.lastMention = z2;
            this.lastReactions = z3;
            this.imageLayoutView.invalidate();
        }

        private void animateCounterBounce() {
            ValueAnimator valueAnimator = this.counterAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.counterAnimator = null;
            }
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.counterAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TopicsTabsView$VerticalTabView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$animateCounterBounce$0(valueAnimator2);
                }
            });
            this.counterAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TopicsTabsView.VerticalTabView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    VerticalTabView.this.countScale = 1.0f;
                    VerticalTabView.this.imageLayoutView.invalidate();
                }
            });
            this.counterAnimator.setInterpolator(new OvershootInterpolator(2.0f));
            this.counterAnimator.setDuration(200L);
            this.counterAnimator.start();
        }

        public /* synthetic */ void lambda$animateCounterBounce$0(ValueAnimator valueAnimator) {
            this.countScale = Math.max(1.0f, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.imageLayoutView.invalidate();
        }

        public void setAll(boolean z, boolean z2, boolean z3) {
            setLayout(z2);
            this.topicId = -1L;
            this.staticImage = true;
            this.isAdd = false;
            this.textView.setText(LocaleController.getString(z ? R.string.BotForumNewTopic : R.string.AllTopicsSide));
            this.textView.setVisibility(z ? 8 : 0);
            this.imageView.clearImage();
            this.imageView.setAnimatedEmojiDrawable(null);
            if (z) {
                BotNewTopicDrawable botNewTopicDrawable = new BotNewTopicDrawable(getContext());
                botNewTopicDrawable.setColor(Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider));
                this.imageView.setImageDrawable(botNewTopicDrawable);
            } else {
                this.imageView.setImageResource(R.drawable.other_chats);
            }
            this.imageView.setScaleX(1.0f);
            this.imageView.setScaleY(1.0f);
            setSelected(z3);
            updateImageColor();
            updateState();
            setCounter(true, 0, false, false, false);
            setPinned(false, false);
        }

        public void setAdd(boolean z, boolean z2) {
            setLayout(z);
            this.staticImage = true;
            this.isAdd = true;
            this.textView.setText(LocaleController.getString(R.string.NewTopic));
            this.textView.setVisibility(0);
            this.imageView.clearImage();
            this.imageView.setAnimatedEmojiDrawable(null);
            this.imageView.setImageResource(R.drawable.emoji_tabs_new3);
            this.imageView.setScaleX(1.0f);
            this.imageView.setScaleY(1.0f);
            setSelected(z2);
            updateImageColor();
            updateState();
            setCounter(true, 0, false, false, false);
            setPinned(false, false);
        }

        public void setLoading() {
            setLayout(false);
            this.topicId = -1L;
            this.staticImage = true;
            this.isAdd = false;
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("x");
            LoadingSpan loadingSpan = new LoadingSpan(this.textView, AndroidUtilities.dp(38.0f));
            loadingSpan.setScaleY(0.75f);
            spannableStringBuilder.setSpan(loadingSpan, 0, 1, 33);
            this.textView.setText(spannableStringBuilder);
            this.textView.setVisibility(0);
            this.imageView.clearImage();
            this.imageView.setAnimatedEmojiDrawable(null);
            if (this.loadingDrawable == null) {
                LoadingDrawable loadingDrawable = new LoadingDrawable(this.resourcesProvider);
                this.loadingDrawable = loadingDrawable;
                loadingDrawable.setRadiiDp(38.0f);
                this.loadingDrawable.setCallback(this.imageView);
                int color = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider);
                this.loadingDrawable.setColors(Theme.multAlpha(color, 0.15f), Theme.multAlpha(color, 0.5f), Theme.multAlpha(color, 0.6f), Theme.multAlpha(color, 0.15f));
                this.loadingDrawable.stroke = false;
            }
            this.imageView.setImageDrawable(this.loadingDrawable);
            this.imageView.setScaleX(1.0f);
            this.imageView.setScaleY(1.0f);
            setSelected(false);
            updateImageColor();
            setCounter(true, 0, false, false, false);
            setPinned(false, false);
            updateState();
        }

        public void set(long j, TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
            setLayout(false);
            long j2 = this.topicId;
            int i = tL_forumTopic.id;
            boolean z2 = j2 == ((long) i);
            this.staticImage = false;
            this.topicId = i;
            this.isAdd = false;
            this.textView.setText(tL_forumTopic.title);
            this.textView.setVisibility(0);
            if (tL_forumTopic.id == 1) {
                this.staticImage = true;
                this.imageView.clearImage();
                this.imageView.setAnimatedEmojiDrawable(null);
                this.imageView.setImageResource(R.drawable.msg_filled_general);
                this.imageView.setScaleX(0.66f);
                this.imageView.setScaleY(0.66f);
            } else {
                long j3 = tL_forumTopic.icon_emoji_id;
                BackupImageView backupImageView = this.imageView;
                if (j3 != 0) {
                    backupImageView.clearImage();
                    this.imageView.setAnimatedEmojiDrawable(AnimatedEmojiDrawable.make(UserConfig.selectedAccount, 3, tL_forumTopic.icon_emoji_id));
                    this.imageView.setScaleX(1.0f);
                    this.imageView.setScaleY(1.0f);
                } else {
                    backupImageView.setAnimatedEmojiDrawable(null);
                    this.imageView.setImageDrawable(ForumUtilities.createTopicDrawable(tL_forumTopic, false));
                    this.imageView.setScaleX(1.0f);
                    this.imageView.setScaleY(1.0f);
                }
            }
            setSelected(z);
            updateImageColor();
            setCounter(MessagesController.getInstance(this.currentAccount).isDialogMuted(j, tL_forumTopic.id), tL_forumTopic.unread_count, tL_forumTopic.unread_mentions_count > 0, tL_forumTopic.unread_reactions_count > 0, z2);
            setPinned(tL_forumTopic.pinned, z2);
            updateState();
        }

        public void updateImageColor() {
            int iBlendARGB = ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider), Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider), this.isAdd ? 1.0f : this.selectT);
            boolean z = this.staticImage;
            BackupImageView backupImageView = this.imageView;
            if (!z) {
                backupImageView.setColorFilter(null);
            } else {
                backupImageView.setColorFilter(new PorterDuffColorFilter(iBlendARGB, PorterDuff.Mode.SRC_IN));
            }
            this.imageView.setEmojiColorFilter(new PorterDuffColorFilter(iBlendARGB, PorterDuff.Mode.SRC_IN));
            this.imageView.invalidate();
        }

        public void setMf(TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
            setLayout(true);
            this.isAdd = false;
            this.staticImage = false;
            long peerDialogId = DialogObject.getPeerDialogId(tL_forumTopic.from_id);
            boolean z2 = peerDialogId == this.topicId;
            this.topicId = peerDialogId;
            this.textView.setText(DialogObject.getName(peerDialogId));
            this.textView.setVisibility(0);
            int i = this.currentAccount;
            if (peerDialogId >= 0) {
                TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(peerDialogId));
                this.avatarDrawable.setInfo(user);
                this.imageView.setForUserOrChat(user, this.avatarDrawable);
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(-peerDialogId));
                this.avatarDrawable.setInfo(chat);
                this.imageView.setForUserOrChat(chat, this.avatarDrawable);
            }
            this.imageView.setScaleX(1.0f);
            this.imageView.setScaleY(1.0f);
            updateState();
            setSelected(z);
            setCounter(false, tL_forumTopic.unread_count, false, tL_forumTopic.unread_reactions_count > 0, z2);
            setPinned(false, z2);
        }

        @Override // android.view.View
        public void setSelected(final boolean z) {
            if (this.selected == z) {
                return;
            }
            this.selected = z;
            ValueAnimator valueAnimator = this.selectAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.selectT, z ? 1.0f : 0.0f);
            this.selectAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TopicsTabsView$VerticalTabView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$setSelected$1(valueAnimator2);
                }
            });
            this.selectAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TopicsTabsView.VerticalTabView.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    VerticalTabView.this.selectT = z ? 1.0f : 0.0f;
                    VerticalTabView.this.updateState();
                    VerticalTabView.this.updateImageColor();
                }
            });
            this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.selectAnimator.setDuration(320L);
            this.selectAnimator.start();
        }

        public /* synthetic */ void lambda$setSelected$1(ValueAnimator valueAnimator) {
            this.selectT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            updateState();
            updateImageColor();
        }

        public void updateState() {
            this.lineView.setTranslationX((-AndroidUtilities.dp(3.0f)) * (1.0f - this.selectT));
            this.lineView.setVisibility(this.selectT <= 0.0f ? 8 : 0);
            this.textView.setTextColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider), Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider), this.isAdd ? 1.0f : this.selectT));
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), TLObject.FLAG_30), i2);
        }

        public static class Factory extends UItem.UItemFactory<VerticalTabView> {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public VerticalTabView createView(Context context, RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new VerticalTabView(context, i, resourcesProvider);
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public void bindView(View view, UItem uItem, boolean z, UniversalAdapter universalAdapter, UniversalRecyclerView universalRecyclerView) {
                VerticalTabView verticalTabView = (VerticalTabView) view;
                boolean z2 = false;
                if (uItem.red) {
                    verticalTabView.setLoading();
                } else {
                    Object obj = uItem.object;
                    if (obj == null) {
                        if (uItem.longValue == -2) {
                            verticalTabView.setAdd(uItem.accent, uItem.checked);
                        } else {
                            verticalTabView.setAll((uItem.flags & 1) != 0, uItem.accent, uItem.checked);
                        }
                    } else if (obj instanceof TLRPC.TL_forumTopic) {
                        if (!uItem.withUsername) {
                            verticalTabView.setMf((TLRPC.TL_forumTopic) obj, uItem.checked);
                        } else {
                            verticalTabView.set(uItem.dialogId, (TLRPC.TL_forumTopic) obj, uItem.checked);
                        }
                    }
                }
                if (universalRecyclerView != null && universalRecyclerView.isReorderAllowed() && verticalTabView.pinned) {
                    z2 = true;
                }
                verticalTabView.setReorder(z2);
            }

            public static UItem asAll(boolean z, boolean z2) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = 0;
                uItemOfFactory.longValue = 0L;
                uItemOfFactory.object = null;
                uItemOfFactory.accent = z2;
                uItemOfFactory.flags = z ? 1 : 0;
                return uItemOfFactory;
            }

            public static UItem asAdd(boolean z) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = -2;
                uItemOfFactory.longValue = -2L;
                uItemOfFactory.object = null;
                uItemOfFactory.accent = z;
                return uItemOfFactory;
            }

            public static UItem asTab(long j, TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.dialogId = j;
                uItemOfFactory.id = tL_forumTopic.id;
                uItemOfFactory.object = tL_forumTopic;
                if (z) {
                    uItemOfFactory.longValue = DialogObject.getPeerDialogId(tL_forumTopic.from_id);
                    uItemOfFactory.withUsername = false;
                }
                return uItemOfFactory;
            }

            public static UItem asLoading(int i) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = i;
                uItemOfFactory.red = true;
                uItemOfFactory.checked = false;
                return uItemOfFactory;
            }
        }
    }

    public static class HorizontalTabView extends FrameLayout {
        private int addW;
        private AvatarSpan avatarSpan;
        private ValueAnimator counterAnimator;
        private int counterBackgroundColorKey;
        private final AnimatedTextView.AnimatedTextDrawable counterText;
        private final View counterView;
        int counterViewX;
        private final int currentAccount;
        private final ImageView imageView;
        private boolean isAdd;
        private boolean lastMention;
        private boolean lastReactions;
        private int lastUnread;
        private CharSequence mentionString;
        private boolean mono;
        private boolean pinned;
        private CharSequence reactionString;
        private boolean reorder;
        private final Theme.ResourcesProvider resourcesProvider;
        private ValueAnimator selectAnimator;
        private float selectT;
        private boolean selected;
        private final AnimatedFloat shakeAlpha;
        private Shaker shaker;
        private boolean staticImage;
        private final LinkSpanDrawable.LinksTextView textView;
        private long topicId;

        public void setReorder(boolean z) {
            this.reorder = z;
            invalidate();
        }

        @Override // android.view.ViewGroup
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view == this.textView) {
                canvas.save();
                float f = this.shakeAlpha.set(this.reorder);
                if (f > 0.0f) {
                    if (this.shaker == null) {
                        this.shaker = new Shaker(this);
                    }
                    canvas.translate(getWidth() / 2.0f, getHeight() / 2.0f);
                    this.shaker.concat(canvas, f);
                    canvas.translate((-getWidth()) / 2.0f, (-getHeight()) / 2.0f);
                }
                boolean zDrawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return zDrawChild;
            }
            return super.drawChild(canvas, view, j);
        }

        public HorizontalTabView(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.shakeAlpha = new AnimatedFloat(this, 360L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.pinned = false;
            this.isAdd = false;
            this.mono = false;
            this.staticImage = false;
            this.counterBackgroundColorKey = Theme.key_chats_unreadCounter;
            this.addW = 0;
            this.currentAccount = i;
            this.resourcesProvider = resourcesProvider;
            setClipChildren(false);
            setClipToPadding(false);
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
            this.textView = linksTextView;
            linksTextView.setTextSize(1, 14.0f);
            linksTextView.setTypeface(AndroidUtilities.bold());
            addView(linksTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 11.0f, 0.0f, 11.0f, 0.0f));
            ScaleStateListAnimator.apply(linksTextView);
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            addView(imageView, LayoutHelper.createFrame(34, 34, 17));
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable();
            this.counterText = animatedTextDrawable;
            animatedTextDrawable.setTextSize(AndroidUtilities.dp(11.0f));
            animatedTextDrawable.setTypeface(AndroidUtilities.bold());
            animatedTextDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
            animatedTextDrawable.setGravity(17);
            View view = new View(context, resourcesProvider) { // from class: org.telegram.ui.Components.TopicsTabsView.HorizontalTabView.1
                private final AnimatedPaint backgroundPaint;
                final /* synthetic */ Theme.ResourcesProvider val$resourcesProvider;

                {
                    this.val$resourcesProvider = resourcesProvider;
                    this.backgroundPaint = new AnimatedPaint(this, resourcesProvider);
                    HorizontalTabView.this.counterText.setCallback(this);
                }

                @Override // android.view.View
                public boolean verifyDrawable(Drawable drawable) {
                    return HorizontalTabView.this.counterText == drawable || super.verifyDrawable(drawable);
                }

                @Override // android.view.View
                public void dispatchDraw(Canvas canvas) {
                    float fIsNotEmpty = HorizontalTabView.this.counterText.isNotEmpty();
                    if (fIsNotEmpty > 0.0f) {
                        float fLerp = AndroidUtilities.lerp(0.6f, 1.0f, fIsNotEmpty);
                        float fMax = Math.max(AndroidUtilities.dp(16.66f), HorizontalTabView.this.counterText.getCurrentWidth() + AndroidUtilities.dp(10.0f));
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(0.0f, 0.0f, fMax, getHeight());
                        canvas.save();
                        canvas.scale(fLerp, fLerp, rectF.centerX(), rectF.centerY());
                        canvas.drawRoundRect(rectF, AndroidUtilities.dp(8.33f), AndroidUtilities.dp(8.33f), this.backgroundPaint.setByKey(HorizontalTabView.this.counterBackgroundColorKey).blendTo(HorizontalTabView.this.getTextColor(), HorizontalTabView.this.selectT).multAlpha(fIsNotEmpty));
                        HorizontalTabView.this.counterText.setBounds(rectF);
                        HorizontalTabView.this.counterText.setAlpha((int) (fIsNotEmpty * 255.0f));
                        HorizontalTabView.this.counterText.setTextColor(Theme.getColor(Theme.key_chats_unreadCounterText, this.val$resourcesProvider));
                        HorizontalTabView.this.counterText.draw(canvas);
                        canvas.restore();
                    }
                    super.dispatchDraw(canvas);
                }

                @Override // android.view.View
                public void onMeasure(int i2, int i3) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) Math.max(AndroidUtilities.dp(16.66f), HorizontalTabView.this.counterText.getAnimateToWidth() + AndroidUtilities.dp(10.0f)), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(16.66f), TLObject.FLAG_30));
                }
            };
            this.counterView = view;
            addView(view, LayoutHelper.createFrame(-2, -2.0f, 21, 4.66f, 0.0f, 11.0f, 0.0f));
            ScaleStateListAnimator.apply(view);
            updateTextColor();
        }

        private void setPinned(boolean z, boolean z2) {
            if (this.pinned != z) {
                this.pinned = z;
            }
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5 = i3 - i;
            int i6 = i4 - i2;
            int measuredWidth = (i5 - this.imageView.getMeasuredWidth()) / 2;
            int measuredHeight = (i6 - this.imageView.getMeasuredHeight()) / 2;
            ImageView imageView = this.imageView;
            imageView.layout(measuredWidth, measuredHeight, imageView.getMeasuredWidth() + measuredWidth, this.imageView.getMeasuredHeight() + measuredHeight);
            int i7 = i6 / 2;
            this.textView.layout(AndroidUtilities.dp(11.0f), i7 - (this.textView.getMeasuredHeight() / 2), AndroidUtilities.dp(11.0f) + this.textView.getMeasuredWidth(), (this.textView.getMeasuredHeight() / 2) + i7);
            float animateToWidth = this.counterText.getAnimateToWidth();
            View view = this.counterView;
            if (animateToWidth > 0.0f) {
                view.layout((i5 - AndroidUtilities.dp(11.0f)) - this.counterView.getMeasuredWidth(), i7 - (this.counterView.getMeasuredHeight() / 2), i5 - AndroidUtilities.dp(11.0f), i7 + (this.counterView.getMeasuredHeight() / 2));
            } else {
                view.layout(AndroidUtilities.dp(11.0f) + this.textView.getMeasuredWidth() + AndroidUtilities.dp(4.66f), i7 - (this.counterView.getMeasuredHeight() / 2), AndroidUtilities.dp(11.0f) + this.textView.getMeasuredWidth() + AndroidUtilities.dp(4.66f) + this.counterView.getMeasuredWidth(), i7 + (this.counterView.getMeasuredHeight() / 2));
            }
            if (this.counterViewX != 0 && this.counterView.getLeft() != this.counterViewX) {
                View view2 = this.counterView;
                view2.setTranslationX((-view2.getLeft()) + this.counterViewX);
                this.counterView.animate().translationX(0.0f).setDuration(320L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }
            this.counterViewX = this.counterView.getLeft();
        }

        private void setLayout(boolean z) {
            if (this.mono == z) {
                return;
            }
            this.mono = z;
        }

        public long getTopicId() {
            return this.topicId;
        }

        public void setAll(boolean z, boolean z2, boolean z3) {
            setLayout(z2);
            this.topicId = 0L;
            this.isAdd = false;
            this.staticImage = true;
            this.imageView.setVisibility(z ? 0 : 8);
            if (z) {
                BotNewTopicDrawable botNewTopicDrawable = new BotNewTopicDrawable(getContext());
                botNewTopicDrawable.setColor(Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider));
                this.imageView.setImageDrawable(botNewTopicDrawable);
            }
            this.textView.setText(LocaleController.getString(z ? R.string.BotForumNewTopic : R.string.AllTopicsShort));
            this.textView.setVisibility(z ? 8 : 0);
            setSelected(z3);
            updateTextColor();
            setCounter(true, 0, false, false, false);
            setPinned(false, false);
        }

        public void setAdd() {
            setLayout(false);
            this.topicId = 0L;
            this.isAdd = true;
            this.staticImage = false;
            this.imageView.setVisibility(8);
            this.textView.setVisibility(0);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("e\u200b");
            spannableStringBuilder.setSpan(new ColoredImageSpan(R.drawable.menu_topic_add), 0, 1, 33);
            this.textView.setText(spannableStringBuilder);
            setSelected(false);
            updateTextColor();
            setCounter(true, 0, false, false, false);
            setPinned(false, false);
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        public void setLoading() {
            setLayout(false);
            this.topicId = -1L;
            this.staticImage = true;
            this.imageView.setVisibility(8);
            this.textView.setVisibility(0);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("x");
            LoadingSpan loadingSpan = new LoadingSpan(this.textView, AndroidUtilities.dp(42.0f));
            loadingSpan.setScaleY(0.95f);
            spannableStringBuilder.setSpan(loadingSpan, 0, 1, 33);
            this.textView.setText(spannableStringBuilder);
            setSelected(false);
            updateTextColor();
            setCounter(true, 0, false, false, false);
            setPinned(false, false);
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        public void set(long j, TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
            setLayout(false);
            long j2 = this.topicId;
            int i = tL_forumTopic.id;
            boolean z2 = j2 == ((long) i);
            this.topicId = i;
            this.staticImage = false;
            this.imageView.setVisibility(8);
            this.textView.setVisibility(0);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (tL_forumTopic.id == 1) {
                spannableStringBuilder.append((CharSequence) "#");
                spannableStringBuilder.append((CharSequence) (tL_forumTopic.hidden ? "\u200b" : " "));
                ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_filled_general);
                coloredImageSpan.setScale(0.66f, 0.66f);
                spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 18);
            } else if (tL_forumTopic.icon_emoji_id != 0) {
                spannableStringBuilder.append((CharSequence) "x ");
                spannableStringBuilder.setSpan(new AnimatedEmojiSpan(tL_forumTopic.icon_emoji_id, this.textView.getPaint().getFontMetricsInt()), 0, 1, 33);
            }
            if (!tL_forumTopic.hidden) {
                spannableStringBuilder.append((CharSequence) tL_forumTopic.title);
            }
            this.textView.setText(spannableStringBuilder);
            setSelected(z);
            updateTextColor();
            setCounter(MessagesController.getInstance(this.currentAccount).isDialogMuted(j, this.topicId), tL_forumTopic.unread_count, false, false, z2);
            setPinned(tL_forumTopic.pinned, z2);
        }

        public void updateTextColor() {
            int textColor = getTextColor();
            this.textView.setTextColor(textColor);
            this.textView.setEmojiColor(textColor);
            this.counterView.invalidate();
        }

        public int getTextColor() {
            return ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider), Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider), this.isAdd ? 1.0f : this.selectT);
        }

        public void setMf(long j, TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
            setLayout(true);
            long peerDialogId = DialogObject.getPeerDialogId(tL_forumTopic.from_id);
            boolean z2 = this.topicId == peerDialogId;
            this.topicId = peerDialogId;
            this.staticImage = false;
            this.imageView.setVisibility(8);
            this.textView.setVisibility(0);
            if (this.avatarSpan == null) {
                AvatarSpan avatarSpan = new AvatarSpan(this.textView, this.currentAccount, 18.0f);
                this.avatarSpan = avatarSpan;
                avatarSpan.usePaintAlpha = false;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            TLObject userOrChat = MessagesController.getInstance(this.currentAccount).getUserOrChat(peerDialogId);
            if (userOrChat != null) {
                spannableStringBuilder.append((CharSequence) "x  ");
                this.avatarSpan.setObject(userOrChat);
                spannableStringBuilder.setSpan(this.avatarSpan, 0, 1, 33);
            }
            spannableStringBuilder.append((CharSequence) DialogObject.getName(peerDialogId));
            LinkSpanDrawable.LinksTextView linksTextView = this.textView;
            linksTextView.setText(TextUtils.ellipsize(spannableStringBuilder, linksTextView.getPaint(), AndroidUtilities.dp(150.0f), TextUtils.TruncateAt.END));
            setSelected(z);
            setCounter(MessagesController.getInstance(this.currentAccount).isDialogMuted(j, peerDialogId), tL_forumTopic.unread_count, false, false, z2);
            setPinned(false, z2);
        }

        @Override // android.view.View
        public void setSelected(final boolean z) {
            if (this.selected == z) {
                return;
            }
            this.selected = z;
            ValueAnimator valueAnimator = this.selectAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.selectT, z ? 1.0f : 0.0f);
            this.selectAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TopicsTabsView$HorizontalTabView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$setSelected$0(valueAnimator2);
                }
            });
            this.selectAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TopicsTabsView.HorizontalTabView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    HorizontalTabView.this.selectT = z ? 1.0f : 0.0f;
                    HorizontalTabView.this.updateTextColor();
                }
            });
            this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.selectAnimator.setDuration(320L);
            this.selectAnimator.start();
        }

        public /* synthetic */ void lambda$setSelected$0(ValueAnimator valueAnimator) {
            this.selectT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            updateTextColor();
        }

        private void setCounter(boolean z, int i, boolean z2, boolean z3, boolean z4) {
            if (z3) {
                this.counterBackgroundColorKey = Theme.key_dialogReactionMentionBackground;
                if (this.reactionString == null) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("❤️");
                    ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.mini_like_filled);
                    coloredImageSpan.setScale(0.8f, 0.8f);
                    coloredImageSpan.spaceScaleX = 0.5f;
                    coloredImageSpan.translate(-AndroidUtilities.dp(3.0f), 0.0f);
                    spannableStringBuilder.setSpan(coloredImageSpan, 0, spannableStringBuilder.length(), 33);
                    this.reactionString = spannableStringBuilder;
                }
                this.counterText.setText(this.reactionString, z4);
            } else if (z2) {
                this.counterBackgroundColorKey = z ? Theme.key_chats_unreadCounterMuted : Theme.key_chats_unreadCounter;
                if (this.mentionString == null) {
                    SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder("@");
                    ColoredImageSpan coloredImageSpan2 = new ColoredImageSpan(R.drawable.mini_mention_filled_16);
                    coloredImageSpan2.setScale(0.8f, 0.8f);
                    coloredImageSpan2.spaceScaleX = 0.5f;
                    coloredImageSpan2.translate(-AndroidUtilities.dp(3.0f), 0.0f);
                    spannableStringBuilder2.setSpan(coloredImageSpan2, 0, 1, 33);
                    this.mentionString = spannableStringBuilder2;
                }
                this.counterText.setText(this.mentionString, z4);
            } else if (i > 0) {
                this.counterBackgroundColorKey = z ? Theme.key_chats_unreadCounterMuted : Theme.key_chats_unreadCounter;
                this.counterText.setText(LocaleController.formatNumber(i, ','), z4);
            } else {
                this.counterBackgroundColorKey = Theme.key_chats_unreadCounterMuted;
                this.counterText.setText(_UrlKt.FRAGMENT_ENCODE_SET, z4);
            }
            if (z4 && (this.lastUnread < i || ((!this.lastMention && z2) || (!this.lastReactions && z3)))) {
                animateCounterBounce();
            }
            this.lastUnread = i;
            this.lastMention = z2;
            this.lastReactions = z3;
            this.counterView.invalidate();
            if (getMeasuringWidth() != getMeasuredWidth()) {
                requestLayout();
            }
        }

        private void animateCounterBounce() {
            ValueAnimator valueAnimator = this.counterAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.counterAnimator = null;
            }
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.counterAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TopicsTabsView$HorizontalTabView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$animateCounterBounce$1(valueAnimator2);
                }
            });
            this.counterAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TopicsTabsView.HorizontalTabView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    HorizontalTabView.this.counterView.setScaleX(1.0f);
                    HorizontalTabView.this.counterView.setScaleY(1.0f);
                    HorizontalTabView.this.counterView.invalidate();
                }
            });
            this.counterAnimator.setInterpolator(new OvershootInterpolator(2.0f));
            this.counterAnimator.setDuration(200L);
            this.counterAnimator.start();
        }

        public /* synthetic */ void lambda$animateCounterBounce$1(ValueAnimator valueAnimator) {
            this.counterView.setScaleX(Math.max(1.0f, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
            this.counterView.setScaleY(Math.max(1.0f, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
            this.counterView.invalidate();
        }

        private int getMeasuringWidth() {
            return AndroidUtilities.dp(11.0f) + this.textView.getMeasuredWidth() + (this.counterText.getAnimateToWidth() > 0.0f ? AndroidUtilities.dp(4.66f) + ((int) Math.max(AndroidUtilities.dp(16.66f), this.counterText.getAnimateToWidth() + AndroidUtilities.dp(10.0f))) : 0) + AndroidUtilities.dp(11.0f) + this.addW;
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            this.textView.measure(i, i2);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(getMeasuringWidth(), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), TLObject.FLAG_30));
        }

        public static class Factory extends UItem.UItemFactory<HorizontalTabView> {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public HorizontalTabView createView(Context context, RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new HorizontalTabView(context, i, resourcesProvider);
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public void bindView(View view, UItem uItem, boolean z, UniversalAdapter universalAdapter, UniversalRecyclerView universalRecyclerView) {
                HorizontalTabView horizontalTabView = (HorizontalTabView) view;
                boolean z2 = false;
                if (uItem.red) {
                    horizontalTabView.setLoading();
                } else {
                    Object obj = uItem.object;
                    if (obj == null) {
                        if (uItem.id == -2) {
                            horizontalTabView.setAdd();
                        } else {
                            horizontalTabView.setAll((uItem.flags & 1) != 0, uItem.accent, uItem.checked);
                        }
                    } else if (obj instanceof TLRPC.TL_forumTopic) {
                        boolean z3 = uItem.withUsername;
                        long j = uItem.dialogId;
                        if (!z3) {
                            horizontalTabView.setMf(j, (TLRPC.TL_forumTopic) obj, uItem.checked);
                        } else {
                            horizontalTabView.set(j, (TLRPC.TL_forumTopic) obj, uItem.checked);
                        }
                    }
                }
                horizontalTabView.addW = BitwiseUtils.hasFlag(uItem.flags, 8) ? AndroidUtilities.dp(10.0f) : 0;
                if (universalRecyclerView != null && universalRecyclerView.isReorderAllowed() && horizontalTabView.pinned) {
                    z2 = true;
                }
                horizontalTabView.setReorder(z2);
            }

            public static UItem asAll(boolean z, boolean z2) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = 0;
                uItemOfFactory.longValue = 0L;
                uItemOfFactory.object = null;
                uItemOfFactory.accent = z2;
                return uItemOfFactory;
            }

            public static UItem asTab(long j, TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.dialogId = j;
                uItemOfFactory.id = tL_forumTopic.id;
                uItemOfFactory.object = tL_forumTopic;
                if (z) {
                    uItemOfFactory.longValue = DialogObject.getPeerDialogId(tL_forumTopic.from_id);
                    uItemOfFactory.withUsername = false;
                }
                return uItemOfFactory;
            }

            public static UItem asLoading(int i) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = i;
                uItemOfFactory.red = true;
                return uItemOfFactory;
            }

            public static UItem asAdd() {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = -2;
                uItemOfFactory.longValue = -2L;
                uItemOfFactory.object = null;
                return uItemOfFactory;
            }
        }
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        checkUi_topicsVerticalPosition();
    }

    public void setAllTopicsHidden(boolean z) {
        if (this.allTopicsHidden != z) {
            this.allTopicsHidden = z;
            checkTopicsVisibility(true);
        }
    }

    private void deleteTopics(final HashSet<Integer> hashSet, final Runnable runnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getPluralString("DeleteTopics", hashSet.size()));
        final ArrayList arrayList = new ArrayList(hashSet);
        final long j = this.currentTopicId;
        if (hashSet.size() == 1) {
            builder.setMessage(LocaleController.formatString(R.string.DeleteSelectedTopic, MessagesController.getInstance(this.currentAccount).getTopicsController().findTopic(-this.dialogId, ((Integer) arrayList.get(0)).intValue()).title));
        } else {
            builder.setMessage(LocaleController.getString(R.string.DeleteSelectedTopics));
        }
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.Components.TopicsTabsView$$ExternalSyntheticLambda22
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                this.f$0.lambda$deleteTopics$20(arrayList, j, hashSet, runnable, alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.Components.TopicsTabsView$$ExternalSyntheticLambda23
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                alertDialog.dismiss();
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.show();
        TextView textView = (TextView) alertDialogCreate.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    public /* synthetic */ void lambda$deleteTopics$20(final ArrayList arrayList, final long j, final HashSet hashSet, final Runnable runnable, AlertDialog alertDialog, int i) {
        int size = arrayList.size();
        int i2 = 0;
        while (i2 < size) {
            Object obj = arrayList.get(i2);
            i2++;
            if (j == ((Integer) obj).intValue()) {
                selectTopic(0L, false);
            }
        }
        this.excludeTopics.addAll(hashSet);
        updateTabs();
        BulletinFactory.of(this.fragment).createUndoBulletin(LocaleController.getPluralString("TopicsDeleted", hashSet.size()), new Runnable() { // from class: org.telegram.ui.Components.TopicsTabsView$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$deleteTopics$18(hashSet, arrayList, j);
            }
        }, new Runnable() { // from class: org.telegram.ui.Components.TopicsTabsView$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$deleteTopics$19(arrayList, runnable);
            }
        }).show();
        alertDialog.dismiss();
    }

    public /* synthetic */ void lambda$deleteTopics$18(HashSet hashSet, ArrayList arrayList, long j) {
        this.excludeTopics.removeAll(hashSet);
        updateTabs();
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            long jIntValue = ((Integer) obj).intValue();
            if (j == jIntValue) {
                selectTopic(jIntValue, false);
                return;
            }
        }
    }

    public /* synthetic */ void lambda$deleteTopics$19(ArrayList arrayList, Runnable runnable) {
        MessagesController.getInstance(this.currentAccount).getTopicsController().deleteTopics(-this.dialogId, arrayList);
        runnable.run();
    }

    private long getTopicId(TLRPC.TL_forumTopic tL_forumTopic) {
        return this.mono ? DialogObject.getPeerDialogId(tL_forumTopic.from_id) : tL_forumTopic.id;
    }

    public static class BotNewTopicDrawable extends Drawable {
        private final Drawable drawable;
        private final Paint paint = new Paint(1);
        private final RectF rectF = new RectF();

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return 0;
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public BotNewTopicDrawable(Context context) {
            this.drawable = context.getResources().getDrawable(R.drawable.menu_topic_add).mutate();
        }

        public void setColor(int i) {
            this.paint.setColor(i);
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), this.paint);
            this.drawable.draw(canvas);
        }

        @Override // android.graphics.drawable.Drawable
        public void onBoundsChange(Rect rect) {
            super.onBoundsChange(rect);
            this.rectF.set(rect);
            int iCenterX = rect.centerX() - AndroidUtilities.dp(12.0f);
            int iCenterY = rect.centerY() - AndroidUtilities.dp(12.0f);
            this.drawable.setBounds(iCenterX, iCenterY, AndroidUtilities.dp(24.0f) + iCenterX, AndroidUtilities.dp(24.0f) + iCenterY);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            this.paint.setAlpha(i);
            this.drawable.setAlpha(i);
        }
    }

    private static int getTabsSize(Position position) {
        return AndroidUtilities.dp(position == Position.LEFT ? 64.0f : 36.0f);
    }

    public float getSideMenuT() {
        return this.sidemenuT * this.animatorTopicsVisibility.getFloatValue();
    }

    public boolean isSideMenuEnabled() {
        return this.sidemenuEnabled && this.animatorTopicsVisibility.getValue();
    }

    private float getTabsVisibility(Position position) {
        float floatValue = this.animatorTopicsVisibility.getFloatValue();
        if (position == Position.LEFT) {
            return this.sidemenuT * floatValue;
        }
        if ((position != Position.TOP || this.topicBottom) && !(position == Position.BOTTOM && this.topicBottom)) {
            return 0.0f;
        }
        return (1.0f - this.sidemenuT) * floatValue;
    }

    public Position getCurrentTabsPosition() {
        if (this.sidemenuEnabled) {
            return Position.LEFT;
        }
        return this.topicBottom ? Position.BOTTOM : Position.TOP;
    }

    public float getTabsVisibleSpaceWithPadding(Position position, float f) {
        return (getTabsSize(position) + f) * getTabsVisibility(position);
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        updateSidemenuPosition();
    }
}
