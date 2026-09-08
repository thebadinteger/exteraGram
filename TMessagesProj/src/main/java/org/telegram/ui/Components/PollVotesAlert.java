package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.material.timepicker.TimeModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.utils.GradientProtectionDrawable;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;

public class PollVotesAlert extends BottomSheet {
    public static final Property<UserCell, Float> USER_CELL_PROPERTY = new AnimationProperties.FloatProperty<UserCell>("placeholderAlpha") { // from class: org.telegram.ui.Components.PollVotesAlert.1
        @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
        public void setValue(UserCell userCell, float f) {
            userCell.setPlaceholderAlpha(f);
        }

        @Override // android.util.Property
        public Float get(UserCell userCell) {
            return Float.valueOf(userCell.getPlaceholderAlpha());
        }
    };
    private final ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private final View actionBarShadow;
    private float gradientWidth;
    private Adapter listAdapter;
    private RecyclerListView listView;
    private final HashSet<VotesList> loadingMore;
    private boolean loadingResults;
    private final TLRPC.TL_messageMediaPoll messageMediaPoll;
    private final MessageObject messageObject;
    private final TLRPC.InputPeer peer;
    private LinearGradient placeholderGradient;
    private Matrix placeholderMatrix;
    private final Paint placeholderPaint;
    private final TLRPC.Poll poll;
    private final ArrayList<Integer> queries;
    private final RectF rect;
    private int scrollOffsetY;
    private final Drawable shadowDrawable;
    private final AnimatedEmojiSpan.TextViewEmojis titleTextView;
    private float totalTranslation;
    private final ArrayList<VotesList> voters;
    private final HashMap<VotesList, Button> votesPercents;

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean canDismissWithSwipe() {
        return false;
    }

    public static class VotesList {
        public boolean collapsed;
        public int collapsedCount = 10;
        public int count;
        public String next_offset;
        public byte[] option;
        public ArrayList<TLRPC.User> users;
        public ArrayList<TLRPC.MessagePeerVote> votes;

        public VotesList(TLRPC.TL_messages_votesList tL_messages_votesList, byte[] bArr) {
            this.count = tL_messages_votesList.count;
            this.votes = tL_messages_votesList.votes;
            this.users = tL_messages_votesList.users;
            this.next_offset = tL_messages_votesList.next_offset;
            this.option = bArr;
        }

        public int getCount() {
            if (this.collapsed) {
                return Math.min(this.collapsedCount, this.votes.size());
            }
            return this.votes.size();
        }

        public int getCollapsed() {
            if (this.votes.size() <= 15) {
                return 0;
            }
            return this.collapsed ? 1 : 2;
        }
    }

    public class SectionCell extends FrameLayout {
        private final TextView middleTextView;
        private final AnimatedTextView righTextView;
        private final AnimatedEmojiSpan.TextViewEmojis textView;

        public abstract void onCollapseClick();

        public SectionCell(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
            AnimatedEmojiSpan.TextViewEmojis textViewEmojis = new AnimatedEmojiSpan.TextViewEmojis(getContext());
            this.textView = textViewEmojis;
            textViewEmojis.setTextSize(1, 14.0f);
            textViewEmojis.setTypeface(AndroidUtilities.bold());
            int i = Theme.key_graySectionText;
            textViewEmojis.setTextColor(Theme.getColor(i));
            textViewEmojis.setSingleLine(true);
            textViewEmojis.setEllipsize(TextUtils.TruncateAt.END);
            textViewEmojis.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView = new TextView(getContext());
            this.middleTextView = textView;
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(Theme.getColor(i));
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            AnimatedTextView animatedTextView = new AnimatedTextView(getContext()) { // from class: org.telegram.ui.Components.PollVotesAlert.SectionCell.1
                @Override // android.view.View
                public boolean post(Runnable runnable) {
                    return ((BottomSheet) PollVotesAlert.this).containerView.post(runnable);
                }

                @Override // android.view.View
                public boolean postDelayed(Runnable runnable, long j) {
                    return ((BottomSheet) PollVotesAlert.this).containerView.postDelayed(runnable, j);
                }

                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    SectionCell sectionCell = SectionCell.this;
                    if (sectionCell == PollVotesAlert.this.listView.getPinnedHeader()) {
                        PollVotesAlert.this.listView.invalidate();
                    }
                }
            };
            this.righTextView = animatedTextView;
            animatedTextView.setTextSize(AndroidUtilities.dp(14.0f));
            animatedTextView.setTextColor(Theme.getColor(i));
            animatedTextView.setGravity(LocaleController.isRTL ? 3 : 5);
            animatedTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PollVotesAlert$SectionCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$0(view);
                }
            });
            boolean z = LocaleController.isRTL;
            addView(textViewEmojis, LayoutHelper.createFrame(-2, -1.0f, (z ? 5 : 3) | 48, z ? 0 : 16, 0.0f, z ? 16 : 0, 0.0f));
            addView(textView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 0.0f, 0.0f, 0.0f));
            addView(animatedTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        }

        public public void update(int i) {
            TLRPC.FileLocation fileLocation;
            TLRPC.ChatPhoto chatPhoto;
            boolean z;
            TLRPC.FileLocation fileLocation2;
            TLRPC.UserProfilePhoto userProfilePhoto;
            TLRPC.User user = this.currentUser;
            String userName = null;
            if (user != null && (userProfilePhoto = user.photo) != null) {
                fileLocation = userProfilePhoto.photo_small;
            } else {
                TLRPC.Chat chat = this.currentChat;
                fileLocation = (chat == null || (chatPhoto = chat.photo) == null) ? null : chatPhoto.photo_small;
            }
            if (i != 0) {
                boolean z2 = (MessagesController.UPDATE_MASK_AVATAR & i) != 0 && (((fileLocation2 = this.lastAvatar) != null && fileLocation == null) || ((fileLocation2 == null && fileLocation != null) || !(fileLocation2 == null || fileLocation == null || (fileLocation2.volume_id == fileLocation.volume_id && fileLocation2.local_id == fileLocation.local_id))));
                if (user != null && !z2 && (MessagesController.UPDATE_MASK_STATUS & i) != 0) {
                    TLRPC.UserStatus userStatus = user.status;
                    if ((userStatus != null ? userStatus.expires : 0) != this.lastStatus) {
                        z2 = true;
                    }
                }
                if (!z2 && this.lastName != null && (i & MessagesController.UPDATE_MASK_NAME) != 0) {
                    if (user != null) {
                        userName = UserObject.getUserName(user);
                    } else {
                        TLRPC.Chat chat2 = this.currentChat;
                        if (chat2 != null) {
                            userName = chat2.title;
                        }
                    }
                    z = TextUtils.equals(userName, this.lastName) ? z2 : true;
                }
                if (!z) {
                    return;
                }
            }
            TLRPC.User user2 = this.currentUser;
            if (user2 != null) {
                this.avatarDrawable.setInfo(this.currentAccount, user2);
                TLRPC.UserStatus userStatus2 = this.currentUser.status;
                if (userStatus2 != null) {
                    this.lastStatus = userStatus2.expires;
                } else {
                    this.lastStatus = 0;
                }
            } else {
                TLRPC.Chat chat3 = this.currentChat;
                if (chat3 != null) {
                    this.avatarDrawable.setInfo(this.currentAccount, chat3);
                }
            }
            TLRPC.User user3 = this.currentUser;
            if (user3 != null) {
                if (userName == null) {
                    userName = UserObject.getUserName(user3);
                }
                this.lastName = userName;
                this.lastName = Emoji.replaceEmoji(userName, this.nameTextView.getPaint().getFontMetricsInt(), false);
            } else {
                TLRPC.Chat chat4 = this.currentChat;
                if (chat4 != null) {
                    String str = chat4.title;
                    this.lastName = str;
                    this.lastName = Emoji.replaceEmoji(str, this.nameTextView.getPaint().getFontMetricsInt(), false);
                } else {
                    this.lastName = _UrlKt.FRAGMENT_ENCODE_SET;
                }
            }
            this.nameTextView.setText(this.lastName);
            this.nameTextView.setRightDrawable(this.statusBadgeComponent.updateDrawable(this.currentUser, this.currentChat, Theme.getColor(Theme.key_chats_verifiedBackground, ((BottomSheet) PollVotesAlert.this).resourcesProvider), false));
            this.lastAvatar = fileLocation;
            TLRPC.Chat chat5 = this.currentChat;
            if (chat5 != null) {
                this.avatarImageView.setForUserOrChat(chat5, this.avatarDrawable);
                return;
            }
            TLRPC.User user4 = this.currentUser;
            BackupImageView backupImageView = this.avatarImageView;
            if (user4 != null) {
                backupImageView.setForUserOrChat(user4, this.avatarDrawable);
            } else {
                backupImageView.setImageDrawable(this.avatarDrawable);
            }
        }

        @Override // android.widget.LinearLayout, android.view.View
        public void onDraw(Canvas canvas) {
            int iDp;
            int iDp2;
            int iDp3;
            int iDp4;
            if (this.drawPlaceholder || this.placeholderAlpha != 0.0f) {
                PollVotesAlert.this.placeholderPaint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                int left = this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2);
                int top = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
                canvas.drawCircle(left, top, this.avatarImageView.getMeasuredWidth() / 2, PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    iDp = AndroidUtilities.dp(65.0f);
                    iDp2 = AndroidUtilities.dp(48.0f);
                } else {
                    iDp = AndroidUtilities.dp(65.0f);
                    iDp2 = AndroidUtilities.dp(60.0f);
                }
                if (LocaleController.isRTL) {
                    iDp = (getMeasuredWidth() - iDp) - iDp2;
                }
                PollVotesAlert.this.rect.set(iDp, top - AndroidUtilities.dp(4.0f), iDp + iDp2, AndroidUtilities.dp(4.0f) + top);
                canvas.drawRoundRect(PollVotesAlert.this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), PollVotesAlert.this.placeholderPaint);
                if (this.placeholderNum % 2 == 0) {
                    iDp3 = AndroidUtilities.dp(119.0f);
                    iDp4 = AndroidUtilities.dp(60.0f);
                } else {
                    iDp3 = AndroidUtilities.dp(131.0f);
                    iDp4 = AndroidUtilities.dp(80.0f);
                }
                if (LocaleController.isRTL) {
                    iDp3 = (getMeasuredWidth() - iDp3) - iDp4;
                }
                PollVotesAlert.this.rect.set(iDp3, top - AndroidUtilities.dp(4.0f), iDp3 + iDp4, top + AndroidUtilities.dp(4.0f));
                canvas.drawRoundRect(PollVotesAlert.this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), PollVotesAlert.this.placeholderPaint);
            }
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(64.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(64.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }

    public static void showForPoll(BaseFragment baseFragment, MessageObject messageObject) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        baseFragment.showDialog(new PollVotesAlert(baseFragment.getContext(), baseFragment.getCurrentAccount(), messageObject, baseFragment.getResourceProvider()));
    }

    public static class Button {
        private float decimal;
        private int percent;
        private int votesCount;

        private Button() {
        }
    }

    public MessagesController getMessagesController() {
        return MessagesController.getInstance(this.currentAccount);
    }

    public ConnectionsManager getConnectionsManager() {
        return ConnectionsManager.getInstance(this.currentAccount);
    }

    void lambda$new$1(final Integer[] numArr, final int i, final ArrayList arrayList, final TLRPC.PollAnswerVoters pollAnswerVoters, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PollVotesAlert$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0(numArr, i, tLObject, arrayList, pollAnswerVoters);
            }
        });
    }

    public void lambda$new$3(final VotesList votesList, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PollVotesAlert$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$2(votesList, tLObject);
            }
        });
    }

    public int m11978$r8$lambda$FQIb_wws2uU5VAs1x2U4THvRXQ(Button button, Button button2) {
        if (button.decimal > button2.decimal) {
            return -1;
        }
        return button.decimal < button2.decimal ? 1 : 0;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        int size = this.queries.size();
        for (int i = 0; i < size; i++) {
            getConnectionsManager().cancelRequest(this.queries.get(i).intValue(), true);
        }
        super.dismissInternal();
    }

    public void updateLayout(boolean z) {
        int childCount = this.listView.getChildCount();
        RecyclerListView recyclerListView = this.listView;
        if (childCount <= 0) {
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View childAt = recyclerListView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int iDp = AndroidUtilities.dp(7.0f);
        if (top < AndroidUtilities.dp(7.0f) || holder == null || holder.getAdapterPosition() != 0) {
            top = iDp;
        }
        boolean z2 = top <= AndroidUtilities.dp(12.0f);
        if ((z2 && this.actionBar.getTag() == null) || (!z2 && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z2 ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(180L);
            AnimatorSet animatorSet3 = this.actionBarAnimation;
            ActionBar actionBar = this.actionBar;
            Property property = View.ALPHA;
            animatorSet3.playTogether(ObjectAnimator.ofFloat(actionBar, (Property<ActionBar, Float>) property, z2 ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.actionBarShadow, (Property<View, Float>) property, z2 ? 1.0f : 0.0f));
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PollVotesAlert.9
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    PollVotesAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        int iDp2 = top + (layoutParams.topMargin - AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != iDp2) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = iDp2;
            recyclerListView2.setTopGlowOffset(iDp2 - layoutParams.topMargin);
            this.containerView.invalidate();
        }
    }

    public void updatePlaceholder() {
        if (this.placeholderPaint == null) {
            return;
        }
        int color = Theme.getColor(Theme.key_dialogBackground);
        int color2 = Theme.getColor(Theme.key_dialogBackgroundGray);
        int averageColor = AndroidUtilities.getAverageColor(color2, color);
        this.placeholderPaint.setColor(color2);
        float fDp = AndroidUtilities.dp(500.0f);
        this.gradientWidth = fDp;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, fDp, 0.0f, new int[]{color2, averageColor, color2}, new float[]{0.0f, 0.18f, 0.36f}, Shader.TileMode.REPEAT);
        this.placeholderGradient = linearGradient;
        this.placeholderPaint.setShader(linearGradient);
        Matrix matrix = new Matrix();
        this.placeholderMatrix = matrix;
        this.placeholderGradient.setLocalMatrix(matrix);
    }

    public class Adapter extends RecyclerListView.SectionsAdapter {
        private final int currentAccount = UserConfig.selectedAccount;
        private final Context mContext;

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int i) {
            return null;
        }

        public Adapter(Context context) {
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public Object getItem(int i, int i2) {
            int i3;
            if (i == 0) {
                return 293145;
            }
            int i4 = i - 1;
            if (i2 == 0) {
                return -928312;
            }
            if (i4 >= 0 && i4 < PollVotesAlert.this.voters.size() && (i3 = i2 - 1) < ((VotesList) PollVotesAlert.this.voters.get(i4)).getCount()) {
                return Integer.valueOf(Objects.hash(Long.valueOf(DialogObject.getPeerDialogId(((VotesList) PollVotesAlert.this.voters.get(i4)).votes.get(i3).peer))));
            }
            return -182734;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            if (i == 0 || i2 == 0) {
                return false;
            }
            return PollVotesAlert.this.queries == null || PollVotesAlert.this.queries.isEmpty();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getSectionCount() {
            return PollVotesAlert.this.voters.size() + 1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getCountForSection(int i) {
            int i2 = 1;
            if (i == 0) {
                return 1;
            }
            VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
            int count = votesList.getCount() + 1;
            if (TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                i2 = 0;
            }
            return count + i2;
        }

        private SectionCell createSectionCell() {
            return new SectionCell(this.mContext) { // from class: org.telegram.ui.Components.PollVotesAlert.Adapter.1
                {
                    PollVotesAlert pollVotesAlert = PollVotesAlert.this;
                }

                @Override // org.telegram.ui.Components.PollVotesAlert.SectionCell
                public void onCollapseClick() {
                    VotesList votesList = (VotesList) getTag(R.id.object_tag);
                    if (votesList.votes.size() <= 15) {
                        return;
                    }
                    boolean z = votesList.collapsed;
                    votesList.collapsed = !z;
                    if (!z) {
                        votesList.collapsedCount = 10;
                    }
                    PollVotesAlert.this.animateSectionUpdates(this);
                    PollVotesAlert.this.listAdapter.update(true);
                }
            };
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = createSectionCell();
            }
            SectionCell sectionCell = (SectionCell) view;
            if (i == 0) {
                sectionCell.setAlpha(0.0f);
                return view;
            }
            view.setAlpha(1.0f);
            VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
            int size = PollVotesAlert.this.poll.answers.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC.PollAnswer pollAnswer = PollVotesAlert.this.poll.answers.get(i2);
                if (Arrays.equals(pollAnswer.option, votesList.option) && ((Button) PollVotesAlert.this.votesPercents.get(votesList)) != null) {
                    TLRPC.TL_textWithEntities tL_textWithEntities = pollAnswer.text;
                    if (PollVotesAlert.this.messageObject != null && PollVotesAlert.this.messageObject.translated && PollVotesAlert.this.messageObject.messageOwner != null && PollVotesAlert.this.messageObject.messageOwner.translatedPoll != null) {
                        for (int i3 = 0; i3 < PollVotesAlert.this.messageObject.messageOwner.translatedPoll.answers.size(); i3++) {
                            TLRPC.PollAnswer pollAnswer2 = PollVotesAlert.this.messageObject.messageOwner.translatedPoll.answers.get(i3);
                            if (Arrays.equals(pollAnswer2.option, pollAnswer.option)) {
                                tL_textWithEntities = pollAnswer2.text;
                                break;
                            }
                        }
                    }
                    sectionCell.setText(tL_textWithEntities == null ? _UrlKt.FRAGMENT_ENCODE_SET : tL_textWithEntities.text, tL_textWithEntities == null ? null : tL_textWithEntities.entities, PollVotesAlert.this.calcPercent(votesList.option), votesList.count, votesList.getCollapsed(), false);
                    sectionCell.setTag(R.id.object_tag, votesList);
                    return view;
                }
            }
            return view;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View userCell;
            if (i == 0) {
                userCell = PollVotesAlert.this.new UserCell(this.mContext);
            } else if (i == 1) {
                if (PollVotesAlert.this.titleTextView.getParent() != null) {
                    ((ViewGroup) PollVotesAlert.this.titleTextView.getParent()).removeView(PollVotesAlert.this.titleTextView);
                }
                userCell = PollVotesAlert.this.titleTextView;
            } else if (i == 2) {
                SectionCell sectionCellCreateSectionCell = createSectionCell();
                sectionCellCreateSectionCell.setTag(-33024);
                userCell = sectionCellCreateSectionCell;
            } else {
                TextCell textCell = new TextCell(this.mContext, 23, true);
                textCell.setOffsetFromImage(65);
                textCell.setBackgroundColor(PollVotesAlert.this.getThemedColor(Theme.key_dialogBackground));
                textCell.setColors(Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhiteBlueText4);
                userCell = textCell;
            }
            return new RecyclerListView.Holder(userCell);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 2) {
                if (itemViewType != 3) {
                    return;
                }
                TextCell textCell = (TextCell) viewHolder.itemView;
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(i - 1);
                textCell.setTextAndIcon((CharSequence) LocaleController.formatPluralString("ShowVotes", votesList.count - votesList.getCount(), new Object[0]), R.drawable.arrow_more, false);
                return;
            }
            SectionCell sectionCell = (SectionCell) viewHolder.itemView;
            VotesList votesList2 = (VotesList) PollVotesAlert.this.voters.get(i - 1);
            votesList2.votes.get(0);
            int size = PollVotesAlert.this.poll.answers.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC.PollAnswer pollAnswer = PollVotesAlert.this.poll.answers.get(i3);
                if (Arrays.equals(pollAnswer.option, votesList2.option) && ((Button) PollVotesAlert.this.votesPercents.get(votesList2)) != null) {
                    TLRPC.TL_textWithEntities tL_textWithEntities = pollAnswer.text;
                    if (PollVotesAlert.this.messageObject != null && PollVotesAlert.this.messageObject.translated && PollVotesAlert.this.messageObject.messageOwner != null && PollVotesAlert.this.messageObject.messageOwner.translatedPoll != null) {
                        for (int i4 = 0; i4 < PollVotesAlert.this.messageObject.messageOwner.translatedPoll.answers.size(); i4++) {
                            TLRPC.PollAnswer pollAnswer2 = PollVotesAlert.this.messageObject.messageOwner.translatedPoll.answers.get(i4);
                            if (Arrays.equals(pollAnswer2.option, pollAnswer.option)) {
                                tL_textWithEntities = pollAnswer2.text;
                                break;
                            }
                        }
                    }
                    sectionCell.setText(tL_textWithEntities == null ? _UrlKt.FRAGMENT_ENCODE_SET : tL_textWithEntities.text, tL_textWithEntities == null ? null : tL_textWithEntities.entities, PollVotesAlert.this.calcPercent(votesList2.option), votesList2.count, votesList2.getCollapsed(), false);
                    sectionCell.setTag(R.id.object_tag, votesList2);
                    return;
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 0) {
                int adapterPosition = viewHolder.getAdapterPosition();
                int sectionForPosition = getSectionForPosition(adapterPosition);
                int positionInSectionForPosition = getPositionInSectionForPosition(adapterPosition) - 1;
                UserCell userCell = (UserCell) viewHolder.itemView;
                VotesList votesList = (VotesList) PollVotesAlert.this.voters.get(sectionForPosition - 1);
                TLRPC.MessagePeerVote messagePeerVote = votesList.votes.get(positionInSectionForPosition);
                TLObject userOrChat = PollVotesAlert.this.getMessagesController().getUserOrChat(DialogObject.getPeerDialogId(messagePeerVote.peer));
                int i = messagePeerVote.date;
                boolean z = true;
                if (positionInSectionForPosition == votesList.getCount() - 1 && TextUtils.isEmpty(votesList.next_offset) && !votesList.collapsed) {
                    z = false;
                }
                userCell.setData(userOrChat, i, positionInSectionForPosition, z);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getItemViewType(int i, int i2) {
            if (i == 0) {
                return 1;
            }
            if (i2 == 0) {
                return 2;
            }
            return i2 + (-1) < ((VotesList) PollVotesAlert.this.voters.get(i + (-1))).getCount() ? 0 : 3;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }
    }

    public int calcPercent(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < this.voters.size(); i3++) {
            VotesList votesList = this.voters.get(i3);
            if (votesList != null) {
                i += votesList.count;
                if (Arrays.equals(votesList.option, bArr)) {
                    i2 += votesList.count;
                }
            }
        }
        TLRPC.TL_messageMediaPoll tL_messageMediaPoll = this.messageMediaPoll;
        if (tL_messageMediaPoll.poll.multiple_choice) {
            i = tL_messageMediaPoll.results.total_voters;
        }
        if (i <= 0) {
            return 0;
        }
        return Math.round((i2 / i) * 100.0f);
    }

    public void animateSectionUpdates(View view) {
        View pinnedHeader;
        TLRPC.Message message;
        int i = -2;
        while (i < this.listView.getChildCount()) {
            if (i == -2) {
                pinnedHeader = view;
            } else {
                RecyclerListView recyclerListView = this.listView;
                pinnedHeader = i == -1 ? recyclerListView.getPinnedHeader() : recyclerListView.getChildAt(i);
            }
            if ((pinnedHeader instanceof SectionCell) && (pinnedHeader.getTag(R.id.object_tag) instanceof VotesList)) {
                SectionCell sectionCell = (SectionCell) pinnedHeader;
                VotesList votesList = (VotesList) pinnedHeader.getTag(R.id.object_tag);
                int size = this.poll.answers.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC.PollAnswer pollAnswer = this.poll.answers.get(i2);
                    if (Arrays.equals(pollAnswer.option, votesList.option) && this.votesPercents.get(votesList) != null) {
                        TLRPC.TL_textWithEntities tL_textWithEntities = pollAnswer.text;
                        MessageObject messageObject = this.messageObject;
                        if (messageObject != null && messageObject.translated && (message = messageObject.messageOwner) != null && message.translatedPoll != null) {
                            for (int i3 = 0; i3 < this.messageObject.messageOwner.translatedPoll.answers.size(); i3++) {
                                TLRPC.PollAnswer pollAnswer2 = this.messageObject.messageOwner.translatedPoll.answers.get(i3);
                                if (Arrays.equals(pollAnswer2.option, pollAnswer.option)) {
                                    tL_textWithEntities = pollAnswer2.text;
                                    break;
                                }
                            }
                        }
                        sectionCell.setText(tL_textWithEntities == null ? _UrlKt.FRAGMENT_ENCODE_SET : tL_textWithEntities.text, tL_textWithEntities == null ? null : tL_textWithEntities.entities, calcPercent(votesList.option), votesList.count, votesList.getCollapsed(), true);
                        sectionCell.setTag(R.id.object_tag, votesList);
                        break;
                    }
                }
            }
            i++;
        }
        this.listView.relayoutPinnedHeader();
        this.listView.invalidate();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.PollVotesAlert$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                this.f$0.updatePlaceholder();
            }
        };
        arrayList.add(new ThemeDescription(this.containerView, 0, null, null, null, null, Theme.key_sheet_scrollUp));
        ViewGroup viewGroup = this.containerView;
        Drawable[] drawableArr = {this.shadowDrawable};
        int i = Theme.key_dialogBackground;
        arrayList.add(new ThemeDescription(viewGroup, 0, null, null, drawableArr, null, i));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        ActionBar actionBar = this.actionBar;
        int i2 = ThemeDescription.FLAG_AB_ITEMSCOLOR;
        int i3 = Theme.key_dialogTextBlack;
        arrayList.add(new ThemeDescription(actionBar, i2, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, Theme.key_player_actionBarSubtitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.actionBarShadow, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, i));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_dialogBackgroundGray));
        int i4 = Theme.key_graySectionText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"middleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, new String[]{"righTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SectionCell.class}, null, null, null, Theme.key_graySection));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        return arrayList;
    }
}
