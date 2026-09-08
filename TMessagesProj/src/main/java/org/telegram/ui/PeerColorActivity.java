package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.exoplayer2.util.Consumer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.utils.DrawableUtils;
import org.telegram.messenger.utils.tlutils.AmountUtils$Amount;
import org.telegram.messenger.utils.tlutils.AmountUtils$Currency;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.FilledTabsView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.TextHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Gifts.ResaleGiftsFragment;
import org.telegram.ui.Stars.StarGiftPatterns;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.StoriesUtilities;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class PeerColorActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout actionBarContainer;
    private boolean applying;
    private boolean applyingName;
    private boolean applyingProfile;
    private ImageView backButton;
    private BaseFragment bulletinFragment;
    private View changeDayNightView;
    private ValueAnimator changeDayNightViewAnimator;
    private float changeDayNightViewProgress;
    private ColoredActionBar colorBar;
    private FrameLayout contentView;
    private final SparseIntArray currentColors = new SparseIntArray();
    private ImageView dayNightItem;
    private final long dialogId;
    private boolean forceDark;
    private final StarsController.GiftsList gifts;
    private final StarsController.GiftsList giftsWithPeerColor;
    private final boolean isChannel;
    private boolean isDark;
    public boolean loading;
    private final Theme.MessageDrawable msgInDrawable;
    private final Theme.MessageDrawable msgInDrawableSelected;
    public Page namePage;
    private Theme.ResourcesProvider parentResourcesProvider;
    public Page profilePage;
    private boolean startAtProfile;
    private RLottieDrawable sunDrawable;
    private FilledTabsView tabsView;
    private SimpleTextView titleView;
    private ViewPagerFixed viewPager;

    public Page getCurrentPage() {
        return this.viewPager.getCurrentPosition() == 0 ? this.profilePage : this.namePage;
    }

    public class Page extends FrameLayout {
        private int actionBarHeight;
        private ButtonWithCounterView button;
        private CharSequence buttonCollectible;
        private FrameLayout buttonContainer;
        private CharSequence buttonLocked;
        int buttonRow;
        private View buttonShadow;
        private CharSequence buttonUnlocked;
        int clearRow;
        int colorPickerRow;
        int giftsCount;
        int giftsEmptyRow;
        int giftsEndRow;
        int giftsHeaderRow;
        int giftsInfoRow;
        int giftsLoadingEndRow;
        int giftsLoadingStartRow;
        int giftsStartRow;
        int giftsTabsRow;
        int iconRow;
        private final HashMap<Integer, TL_stars.StarGift> index2gift;
        int info2Row;
        int infoRow;
        private GridLayoutManager layoutManager;
        private RecyclerView.Adapter listAdapter;
        private RecyclerListView listView;
        private ThemePreviewMessagesCell messagesCellPreview;
        private PeerColorGrid peerColorPicker;
        private ProfilePreview profilePreview;
        private ResaleGiftsFragment.ResaleGiftsList resaleGifts;
        int rowCount;
        private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
        private int selectedColor;
        private long selectedEmoji;
        private TLRPC.TL_emojiStatusCollectible selectedEmojiCollectible;
        private TLRPC.TL_peerColorCollectible selectedPeerCollectible;
        private TL_stars.TL_starGiftUnique selectedResaleGift;
        private TL_stars.StarGift selectedTabGift;
        private SetReplyIconCell setReplyIconCell;
        int shadowRow;
        private final ArrayList<CharSequence> tabs;
        private final int type;
        final ArrayList<TL_stars.TL_starGiftUnique> uniqueGifts;

        Context val$context;
            final void lambda$onCreateViewHolder$0(Integer num) {
                Page.this.selectedColor = num.intValue();
                Page.this.selectedEmojiCollectible = null;
                Page.this.selectedPeerCollectible = null;
                Page.this.selectedResaleGift = null;
                Page.this.updateProfilePreview(true);
                Page.this.updateMessages();
                Page.this.updateButton(true);
                if (Page.this.setReplyIconCell != null) {
                    Page.this.setReplyIconCell.invalidate();
                }
                Page page = PeerColorActivity.this.profilePage;
                if (page == null || page.profilePreview == null) {
                    return;
                }
                PeerColorActivity peerColorActivity = PeerColorActivity.this;
                if (peerColorActivity.namePage != null) {
                    peerColorActivity.profilePage.profilePreview.overrideAvatarColor(PeerColorActivity.this.namePage.selectedColor);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                String string;
                boolean z = true;
                switch (getItemViewType(i)) {
                    case 1:
                        viewHolder.itemView.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                        ((PeerColorGrid) viewHolder.itemView).updateColors();
                        break;
                    case 2:
                        TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                        textInfoPrivacyCell.setFixedSize(0);
                        Page page = Page.this;
                        if (i == page.infoRow) {
                            if (this.val$type == 1) {
                                string = LocaleController.getString(PeerColorActivity.this.isChannel ? R.string.ChannelColorHint : R.string.UserColorHint);
                            } else {
                                string = LocaleController.getString(PeerColorActivity.this.isChannel ? R.string.ChannelProfileHint : R.string.UserProfileHint2);
                            }
                            final int i2 = this.val$type;
                            textInfoPrivacyCell.setText(AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(string, new Runnable() { // from class: org.telegram.ui.PeerColorActivity$Page$4$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    this.f$0.lambda$onBindViewHolder$1(i2);
                                }
                            }), true));
                            textInfoPrivacyCell.setBackground(Theme.getThemedDrawableByKey(Page.this.getContext(), Page.this.clearRow >= 0 ? R.drawable.greydivider : R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        } else if (i == page.shadowRow) {
                            textInfoPrivacyCell.setText(_UrlKt.FRAGMENT_ENCODE_SET);
                            textInfoPrivacyCell.setFixedSize(12);
                            textInfoPrivacyCell.setBackground(Theme.getThemedDrawableByKey(Page.this.getContext(), Page.this.giftsHeaderRow >= 0 ? R.drawable.greydivider : R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        } else if (i == page.giftsInfoRow) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.UserProfileCollectibleInfo));
                            textInfoPrivacyCell.setBackground(Theme.getThemedDrawableByKey(Page.this.getContext(), R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        }
                        break;
                    case 3:
                        ((SetReplyIconCell) viewHolder.itemView).updateColors();
                        break;
                    case 6:
                        TextCell textCell = (TextCell) viewHolder.itemView;
                        textCell.updateColors();
                        textCell.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                        textCell.updateColors();
                        Page page2 = Page.this;
                        if (i == page2.clearRow) {
                            textCell.setText(LocaleController.getString(PeerColorActivity.this.isChannel ? R.string.ChannelProfileColorReset : R.string.UserProfileColorReset), false);
                        }
                        break;
                    case 7:
                        HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                        if (i == Page.this.giftsHeaderRow) {
                            headerCell.setText(LocaleController.getString(R.string.UserProfileCollectibleHeader), false);
                        }
                        headerCell.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                        break;
                    case 8:
                        GiftCell giftCell = (GiftCell) viewHolder.itemView;
                        Page page3 = Page.this;
                        int i3 = i - page3.giftsStartRow;
                        if (i3 >= 0 && i3 < page3.uniqueGifts.size()) {
                            TL_stars.TL_starGiftUnique tL_starGiftUnique = Page.this.uniqueGifts.get(i3);
                            giftCell.set(i3, tL_starGiftUnique);
                            if ((Page.this.selectedEmojiCollectible == null || Page.this.selectedEmojiCollectible.collectible_id != tL_starGiftUnique.id) && (Page.this.selectedPeerCollectible == null || Page.this.selectedPeerCollectible.collectible_id != tL_starGiftUnique.id)) {
                                z = false;
                            }
                            giftCell.setSelected(z, false);
                            giftCell.card.invalidate();
                            break;
                        }
                        break;
                    case 10:
                        GiftSheet.Tabs tabs = (GiftSheet.Tabs) viewHolder.itemView;
                        Page.this.tabs.clear();
                        Page.this.index2gift.clear();
                        ArrayList<TL_stars.StarGift> arrayList = StarsController.getInstance(((BaseFragment) PeerColorActivity.this).currentAccount).sortedGifts;
                        Page.this.tabs.add(LocaleController.getString(R.string.Gift2TabMine));
                        int size = 0;
                        for (int i4 = 0; i4 < arrayList.size(); i4++) {
                            TL_stars.StarGift starGift = arrayList.get(i4);
                            int i5 = this.val$type;
                            if ((i5 == 0 || (i5 == 1 && starGift.peer_color_available)) && starGift.availability_resale > 0) {
                                if (Page.this.selectedTabGift == starGift) {
                                    size = Page.this.tabs.size();
                                }
                                Page.this.index2gift.put(Integer.valueOf(Page.this.tabs.size()), starGift);
                                TextPaint textPaint = new TextPaint(1);
                                textPaint.setTextSize(AndroidUtilities.dp(14.0f));
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("x ");
                                AnimatedEmojiSpan animatedEmojiSpan = new AnimatedEmojiSpan(starGift.getDocument(), textPaint.getFontMetricsInt());
                                animatedEmojiSpan.size = AndroidUtilities.dp(14.0f);
                                spannableStringBuilder.setSpan(animatedEmojiSpan, 0, 1, 33);
                                spannableStringBuilder.append((CharSequence) starGift.title);
                                Page.this.tabs.add(spannableStringBuilder);
                            }
                        }
                        tabs.set(0, Page.this.tabs, size, new Utilities.Callback() { // from class: org.telegram.ui.PeerColorActivity$Page$4$$ExternalSyntheticLambda1
                            @Override 
                            public final void run(Object obj) {
                                this.f$0.lambda$onBindViewHolder$3((Integer) obj);
                            }
                        });
                        tabs.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                        tabs.updateColors();
                        break;
                    case 11:
                        ((EmptyView) viewHolder.itemView).updateColors();
                        break;
                    case 12:
                        GiftSheet.GiftCell giftCell2 = (GiftSheet.GiftCell) viewHolder.itemView;
                        Page page4 = Page.this;
                        int i6 = i - page4.giftsStartRow;
                        if (page4.resaleGifts != null && i6 >= 0 && i6 < Page.this.uniqueGifts.size()) {
                            TL_stars.TL_starGiftUnique tL_starGiftUnique2 = Page.this.uniqueGifts.get(i6);
                            giftCell2.setStarsGift(tL_starGiftUnique2, false, false, false, true, false);
                            if ((Page.this.selectedEmojiCollectible == null || Page.this.selectedEmojiCollectible.collectible_id != tL_starGiftUnique2.id) && (Page.this.selectedPeerCollectible == null || Page.this.selectedPeerCollectible.collectible_id != tL_starGiftUnique2.id)) {
                                z = false;
                            }
                            giftCell2.setSelected(z, false);
                        }
                        break;
                }
            }

            public void lambda$onBindViewHolder$2(Boolean bool) {
                Page.this.update();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
                super.onViewAttachedToWindow(viewHolder);
                boolean z = true;
                if (viewHolder.getItemViewType() == 8) {
                    GiftCell giftCell = (GiftCell) viewHolder.itemView;
                    int adapterPosition = viewHolder.getAdapterPosition();
                    Page page = Page.this;
                    int i = adapterPosition - page.giftsStartRow;
                    if (i < 0 || i >= page.uniqueGifts.size()) {
                        return;
                    }
                    TL_stars.TL_starGiftUnique tL_starGiftUnique = Page.this.uniqueGifts.get(i);
                    giftCell.set(i, tL_starGiftUnique);
                    if ((Page.this.selectedEmojiCollectible == null || Page.this.selectedEmojiCollectible.collectible_id != tL_starGiftUnique.id) && (Page.this.selectedPeerCollectible == null || Page.this.selectedPeerCollectible.collectible_id != tL_starGiftUnique.id)) {
                        z = false;
                    }
                    giftCell.setSelected(z, false);
                    return;
                }
                if (viewHolder.getItemViewType() == 12) {
                    GiftSheet.GiftCell giftCell2 = (GiftSheet.GiftCell) viewHolder.itemView;
                    int adapterPosition2 = viewHolder.getAdapterPosition();
                    Page page2 = Page.this;
                    int i2 = adapterPosition2 - page2.giftsStartRow;
                    if (page2.resaleGifts != null && i2 >= 0 && i2 < Page.this.uniqueGifts.size()) {
                        TL_stars.TL_starGiftUnique tL_starGiftUnique2 = Page.this.uniqueGifts.get(i2);
                        giftCell2.setStarsGift(tL_starGiftUnique2, false, false, false, true, false);
                        if ((Page.this.selectedEmojiCollectible == null || Page.this.selectedEmojiCollectible.collectible_id != tL_starGiftUnique2.id) && (Page.this.selectedPeerCollectible == null || Page.this.selectedPeerCollectible.collectible_id != tL_starGiftUnique2.id)) {
                            z = false;
                        }
                        giftCell2.setSelected(z, false);
                    }
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return Page.this.rowCount;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i) {
                Page page = Page.this;
                if (i != page.infoRow && i != page.giftsInfoRow && i != page.info2Row && i != page.shadowRow) {
                    if (i == page.colorPickerRow) {
                        return 1;
                    }
                    if (i == page.iconRow) {
                        return 3;
                    }
                    if (i == page.buttonRow) {
                        return 5;
                    }
                    if (i == page.clearRow) {
                        return 6;
                    }
                    if (i == page.giftsTabsRow) {
                        return 10;
                    }
                    if (i == page.giftsEmptyRow) {
                        return 11;
                    }
                    if (i == page.giftsHeaderRow) {
                        return 7;
                    }
                    if (i >= page.giftsStartRow && i < page.giftsEndRow) {
                        return page.selectedTabGift == null ? 8 : 12;
                    }
                    if (i >= page.giftsLoadingStartRow && i < page.giftsLoadingEndRow) {
                        return 9;
                    }
                    if (i == getItemCount() - 1) {
                        return 4;
                    }
                }
                return 2;
            }
        }

        public void lambda$new$0(Boolean bool) {
                Page.this.update();
            }

            public void updateColors() {
                setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                this.title.setTextColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
                TextView textView = this.subtitle;
                PeerColorActivity peerColorActivity = PeerColorActivity.this;
                int i = Theme.key_chat_messageLinkIn;
                textView.setTextColor(peerColorActivity.getThemedColor(i));
                this.subtitle.setLinkTextColor(PeerColorActivity.this.getThemedColor(i));
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (PeerColorActivity.this.getParentLayout() != null) {
                PeerColorActivity.this.getParentLayout().drawHeaderShadow(canvas, this.actionBarHeight);
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            if (this.type == 1) {
                super.onMeasure(i, i2);
                this.actionBarHeight = this.messagesCellPreview.getMeasuredHeight() + ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
                ((ViewGroup.MarginLayoutParams) this.messagesCellPreview.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
                ((ViewGroup.MarginLayoutParams) this.listView.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
                this.listView.setPadding(0, this.messagesCellPreview.getMeasuredHeight(), 0, 0);
            } else {
                this.actionBarHeight = AndroidUtilities.dp(230.0f) + AndroidUtilities.statusBarHeight;
                ((ViewGroup.MarginLayoutParams) this.listView.getLayoutParams()).topMargin = this.actionBarHeight;
                ((ViewGroup.MarginLayoutParams) this.profilePreview.getLayoutParams()).height = this.actionBarHeight;
            }
            super.onMeasure(i, i2);
        }

        public boolean hasUnsavedChanged() {
            boolean z = PeerColorActivity.this.isChannel;
            PeerColorActivity peerColorActivity = PeerColorActivity.this;
            if (z) {
                TLRPC.Chat chat = peerColorActivity.getMessagesController().getChat(Long.valueOf(-PeerColorActivity.this.dialogId));
                if (chat == null) {
                    return false;
                }
                int i = this.type;
                int i2 = this.selectedColor;
                if (i == 1) {
                    if (i2 == ChatObject.getColorId(chat) && this.selectedEmoji == ChatObject.getEmojiId(chat)) {
                        TLRPC.PeerColor peerColor = chat.color;
                        if (PeerColorActivity.eq(peerColor instanceof TLRPC.TL_peerColorCollectible ? (TLRPC.TL_peerColorCollectible) peerColor : null, this.selectedPeerCollectible)) {
                            return false;
                        }
                    }
                    return true;
                }
                if (i2 == (chat.emoji_status instanceof TLRPC.TL_emojiStatusCollectible ? -1 : ChatObject.getProfileColorId(chat))) {
                    if (this.selectedEmoji == (chat.emoji_status instanceof TLRPC.TL_emojiStatusCollectible ? 0L : ChatObject.getOnlyProfileEmojiId(chat)) && PeerColorActivity.eq(chat.emoji_status, this.selectedEmojiCollectible)) {
                        return false;
                    }
                }
                return true;
            }
            TLRPC.User currentUser = peerColorActivity.getUserConfig().getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            int i3 = this.type;
            int i4 = this.selectedColor;
            if (i3 == 1) {
                if (i4 == (currentUser.color instanceof TLRPC.TL_peerColorCollectible ? -1 : UserObject.getColorId(currentUser)) && this.selectedEmoji == UserObject.getEmojiId(currentUser)) {
                    TLRPC.PeerColor peerColor2 = currentUser.color;
                    if (PeerColorActivity.eq(peerColor2 instanceof TLRPC.TL_peerColorCollectible ? (TLRPC.TL_peerColorCollectible) peerColor2 : null, this.selectedPeerCollectible)) {
                        return false;
                    }
                }
                return true;
            }
            if (i4 == (currentUser.emoji_status instanceof TLRPC.TL_emojiStatusCollectible ? -1 : UserObject.getProfileColorId(currentUser))) {
                if (this.selectedEmoji == (currentUser.emoji_status instanceof TLRPC.TL_emojiStatusCollectible ? 0L : UserObject.getOnlyProfileEmojiId(currentUser)) && PeerColorActivity.eq(currentUser.emoji_status, this.selectedEmojiCollectible)) {
                    return false;
                }
            }
            return true;
        }

        public void updateButtonY() {
            if (this.buttonContainer == null) {
                return;
            }
            int itemCount = this.listAdapter.getItemCount() - 1;
            boolean z = false;
            int measuredHeight = 0;
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                View childAt = this.listView.getChildAt(i);
                int childAdapterPosition = this.listView.getChildAdapterPosition(childAt);
                if (childAdapterPosition != -1 && childAdapterPosition <= itemCount) {
                    measuredHeight = Math.max(measuredHeight, childAt.getTop());
                    if (childAdapterPosition == itemCount) {
                        z = true;
                    }
                }
            }
            if (!z) {
                measuredHeight = this.listView.getMeasuredHeight();
            }
            float fMax = Math.max(0, measuredHeight - (this.listView.getMeasuredHeight() - AndroidUtilities.dp(76.66f)));
            int i2 = this.type;
            if (i2 == 0 || i2 == 1) {
                this.buttonShadow.animate().alpha(fMax > 0.0f ? 0.0f : 1.0f).start();
                fMax = 0.0f;
            }
            this.buttonContainer.setTranslationY(fMax);
        }

        public class SetReplyIconCell extends FrameLayout {
            private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable imageDrawable;
            private Text offText;
            private TextView textView;

            public SetReplyIconCell(Context context) {
                super(context);
                setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                TextView textView = new TextView(context);
                this.textView = textView;
                textView.setTextSize(1, 16.0f);
                this.textView.setTextColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
                int i = Page.this.type;
                TextView textView2 = this.textView;
                if (i == 1) {
                    textView2.setText(LocaleController.getString(PeerColorActivity.this.isChannel ? R.string.ChannelReplyIcon : R.string.UserReplyIcon));
                } else {
                    textView2.setText(LocaleController.getString(PeerColorActivity.this.isChannel ? R.string.ChannelProfileIcon : R.string.UserProfileIcon));
                }
                addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 23, 20.0f, 0.0f, 20.0f, 0.0f));
                this.imageDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, false, AndroidUtilities.dp(24.0f), 13);
            }

            public void updateColors() {
                setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                this.textView.setTextColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            }

            public void update(boolean z) {
                long j = Page.this.selectedEmoji;
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.imageDrawable;
                if (j != 0) {
                    swapAnimatedEmojiDrawable.set(Page.this.selectedEmoji, z);
                    this.offText = null;
                } else {
                    swapAnimatedEmojiDrawable.set((Drawable) null, z);
                    if (this.offText == null) {
                        this.offText = new Text(LocaleController.getString(PeerColorActivity.this.isChannel ? R.string.ChannelReplyIconOff : R.string.UserReplyIconOff), 16.0f);
                    }
                }
            }

            public void updateImageBounds() {
                this.imageDrawable.setBounds(LocaleController.isRTL ? AndroidUtilities.dp(21.0f) : (getWidth() - this.imageDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(21.0f), (getHeight() - this.imageDrawable.getIntrinsicHeight()) / 2, LocaleController.isRTL ? AndroidUtilities.dp(21.0f) + this.imageDrawable.getIntrinsicWidth() : getWidth() - AndroidUtilities.dp(21.0f), (getHeight() + this.imageDrawable.getIntrinsicHeight()) / 2);
            }

            @Override // android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                updateImageBounds();
                this.imageDrawable.setColor(Integer.valueOf(getColor()));
                Text text = this.offText;
                if (text != null) {
                    text.draw(canvas, (getMeasuredWidth() - this.offText.getWidth()) - AndroidUtilities.dp(19.0f), getMeasuredHeight() / 2.0f, PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText4), 1.0f);
                } else {
                    this.imageDrawable.draw(canvas);
                }
            }

            public int getColor() {
                MessagesController.PeerColor color;
                int i = Page.this.selectedColor;
                Page page = Page.this;
                if (i < 0) {
                    PeerColorActivity peerColorActivity = PeerColorActivity.this;
                    int i2 = Theme.key_actionBarDefault;
                    if (AndroidUtilities.computePerceivedBrightness(peerColorActivity.getThemedColor(i2)) > 0.8f) {
                        return Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, ((BaseFragment) PeerColorActivity.this).resourceProvider);
                    }
                    return AndroidUtilities.computePerceivedBrightness(PeerColorActivity.this.getThemedColor(i2)) < 0.2f ? Theme.multAlpha(Theme.getColor(Theme.key_actionBarDefaultTitle, ((BaseFragment) PeerColorActivity.this).resourceProvider), 0.5f) : Theme.blendOver(Theme.getColor(Theme.key_windowBackgroundWhite, ((BaseFragment) PeerColorActivity.this).resourceProvider), Theme.multAlpha(PeerColorActivity.adaptProfileEmojiColor(Theme.getColor(i2, ((BaseFragment) PeerColorActivity.this).resourceProvider)), 0.7f));
                }
                int i3 = page.selectedColor;
                Page page2 = Page.this;
                if (i3 < 7) {
                    return PeerColorActivity.this.getThemedColor(Theme.keys_avatar_nameInMessage[page2.selectedColor]);
                }
                int i4 = page2.type;
                Page page3 = Page.this;
                MessagesController.PeerColors peerColors = i4 == 1 ? MessagesController.getInstance(((BaseFragment) PeerColorActivity.this).currentAccount).peerColors : MessagesController.getInstance(((BaseFragment) PeerColorActivity.this).currentAccount).profilePeerColors;
                if (peerColors != null && (color = peerColors.getColor(Page.this.selectedColor)) != null) {
                    return color.getColor1();
                }
                return PeerColorActivity.this.getThemedColor(Theme.keys_avatar_nameInMessage[0]);
            }

            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), TLObject.FLAG_30));
            }

            @Override // android.view.ViewGroup, android.view.View
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                this.imageDrawable.detach();
            }

            @Override // android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.imageDrawable.attach();
            }
        }

        public void showSelectStatusDialog(final SetReplyIconCell setReplyIconCell) {
            int iCenterX;
            int iDp;
            if (this.selectAnimatedEmojiDialog != null || setReplyIconCell == null) {
                return;
            }
            final SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[1];
            int iMin = (int) Math.min(AndroidUtilities.dp(330.0f), AndroidUtilities.displaySize.y * 0.75f);
            int iMin2 = (int) Math.min(AndroidUtilities.dp(324.0f), AndroidUtilities.displaySize.x * 0.95f);
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = setReplyIconCell.imageDrawable;
            if (setReplyIconCell.imageDrawable != null) {
                setReplyIconCell.imageDrawable.play();
                setReplyIconCell.updateImageBounds();
                Rect rect = AndroidUtilities.rectTmp2;
                rect.set(setReplyIconCell.imageDrawable.getBounds());
                if (this.type == 1) {
                    iDp = ((-rect.centerY()) + AndroidUtilities.dp(12.0f)) - iMin;
                } else {
                    iDp = (-(setReplyIconCell.getHeight() - rect.centerY())) - AndroidUtilities.dp(16.0f);
                }
                iCenterX = rect.centerX() - (setReplyIconCell.getRight() - iMin2);
            } else {
                iCenterX = 0;
                iDp = 0;
            }
            int i = iDp;
            SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = new SelectAnimatedEmojiDialog(PeerColorActivity.this, getContext(), true, Integer.valueOf(iCenterX), this.type == 1 ? 5 : 7, true, PeerColorActivity.this.getResourceProvider(), this.type == 1 ? 24 : 16, setReplyIconCell.getColor()) { // from class: org.telegram.ui.PeerColorActivity.Page.7
                @Override // org.telegram.ui.SelectAnimatedEmojiDialog
                public float getScrimDrawableTranslationY() {
                    return 0.0f;
                }

                @Override // org.telegram.ui.SelectAnimatedEmojiDialog
                public void onEmojiSelected(View view, Long l, TLRPC.Document document, TL_stars.TL_starGiftUnique tL_starGiftUnique, Integer num) {
                    Page page = Page.this;
                    if (tL_starGiftUnique != null) {
                        if (page.type == 0) {
                            TLRPC.PeerColor peerColor = tL_starGiftUnique.peer_color;
                            if (!(peerColor instanceof TLRPC.TL_peerColorCollectible)) {
                                return;
                            }
                            Page.this.selectedPeerCollectible = (TLRPC.TL_peerColorCollectible) peerColor;
                            Page.this.selectedEmojiCollectible = null;
                        } else {
                            Page.this.selectedPeerCollectible = null;
                            Page.this.selectedEmojiCollectible = MessagesController.emojiStatusCollectibleFromGift(tL_starGiftUnique);
                        }
                        Page.this.selectedResaleGift = null;
                        Page.this.selectedColor = -1;
                    } else {
                        page.selectedEmoji = l == null ? 0L : l.longValue();
                        Page.this.selectedEmojiCollectible = null;
                        Page.this.selectedPeerCollectible = null;
                        Page.this.selectedResaleGift = null;
                    }
                    SetReplyIconCell setReplyIconCell2 = setReplyIconCell;
                    if (setReplyIconCell2 != null) {
                        setReplyIconCell2.update(true);
                    }
                    Page.this.updateProfilePreview(true);
                    Page.this.updateMessages();
                    Page.this.updateButton(true);
                    if (selectAnimatedEmojiDialogWindowArr[0] != null) {
                        Page.this.selectAnimatedEmojiDialog = null;
                        selectAnimatedEmojiDialogWindowArr[0].dismiss();
                    }
                }
            };
            selectAnimatedEmojiDialog.useAccentForPlus = true;
            long j = this.selectedEmoji;
            selectAnimatedEmojiDialog.setSelected(j == 0 ? null : Long.valueOf(j));
            selectAnimatedEmojiDialog.setSaveState(3);
            selectAnimatedEmojiDialog.setScrimDrawable(swapAnimatedEmojiDrawable, setReplyIconCell);
            int i2 = -2;
            SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialogWindow = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow(selectAnimatedEmojiDialog, i2, i2) { // from class: org.telegram.ui.PeerColorActivity.Page.8
                @Override // org.telegram.ui.SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow, android.widget.PopupWindow
                public void dismiss() {
                    super.dismiss();
                    Page.this.selectAnimatedEmojiDialog = null;
                }
            };
            this.selectAnimatedEmojiDialog = selectAnimatedEmojiDialogWindow;
            selectAnimatedEmojiDialogWindowArr[0] = selectAnimatedEmojiDialogWindow;
            selectAnimatedEmojiDialogWindow.showAsDropDown(setReplyIconCell, 0, i, (LocaleController.isRTL ? 3 : 5) | 48);
            selectAnimatedEmojiDialogWindowArr[0].dimBehind();
        }

        public void checkResetColorButton() {
            int i;
            if (this.type != 0) {
                return;
            }
            int i2 = this.clearRow;
            updateRows();
            if (i2 >= 0 && this.clearRow < 0) {
                this.listAdapter.notifyItemRangeRemoved(i2, 2);
            } else {
                if (i2 >= 0 || (i = this.clearRow) < 0) {
                    return;
                }
                this.listAdapter.notifyItemRangeInserted(i, 2);
            }
        }

        public void updateSelectedGift() {
            TLRPC.TL_peerColorCollectible tL_peerColorCollectible;
            TLRPC.TL_peerColorCollectible tL_peerColorCollectible2;
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof GiftCell) {
                    GiftCell giftCell = (GiftCell) childAt;
                    TLRPC.TL_emojiStatusCollectible tL_emojiStatusCollectible = this.selectedEmojiCollectible;
                    giftCell.setSelected((tL_emojiStatusCollectible != null && tL_emojiStatusCollectible.collectible_id == giftCell.getGiftId()) || ((tL_peerColorCollectible2 = this.selectedPeerCollectible) != null && tL_peerColorCollectible2.collectible_id == giftCell.getGiftId()), true);
                } else if (childAt instanceof GiftSheet.GiftCell) {
                    GiftSheet.GiftCell giftCell2 = (GiftSheet.GiftCell) childAt;
                    TLRPC.TL_emojiStatusCollectible tL_emojiStatusCollectible2 = this.selectedEmojiCollectible;
                    giftCell2.setSelected((tL_emojiStatusCollectible2 != null && tL_emojiStatusCollectible2.collectible_id == giftCell2.getGiftId()) || ((tL_peerColorCollectible = this.selectedPeerCollectible) != null && tL_peerColorCollectible.collectible_id == giftCell2.getGiftId()), true);
                }
            }
        }

        private void updateRows() {
            this.clearRow = -1;
            this.shadowRow = -1;
            this.giftsHeaderRow = -1;
            this.giftsStartRow = -1;
            this.giftsLoadingStartRow = -1;
            this.giftsLoadingEndRow = -1;
            this.giftsEndRow = -1;
            this.giftsInfoRow = -1;
            this.giftsTabsRow = -1;
            this.giftsEmptyRow = -1;
            int i = 0;
            this.giftsCount = 0;
            this.uniqueGifts.clear();
            this.colorPickerRow = 0;
            int i2 = 1 + 1;
            this.iconRow = 1;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.infoRow = i2;
            int i4 = this.type;
            int i5 = 3;
            if (i4 == 0 && (this.selectedColor >= 0 || this.selectedEmojiCollectible != null || this.selectedPeerCollectible != null)) {
                this.clearRow = i3;
                this.rowCount = i2 + 3;
                this.shadowRow = i2 + 2;
            }
            PeerColorActivity peerColorActivity = PeerColorActivity.this;
            StarsController.GiftsList giftsList = i4 == 1 ? peerColorActivity.giftsWithPeerColor : peerColorActivity.gifts;
            int i6 = this.type;
            if ((i6 == 0 || i6 == 1) && giftsList != null) {
                int i7 = this.rowCount;
                this.rowCount = i7 + 1;
                this.giftsTabsRow = i7;
                TL_stars.StarGift starGift = this.selectedTabGift;
                if (starGift == null) {
                    while (i < giftsList.gifts.size()) {
                        TL_stars.StarGift starGift2 = giftsList.gifts.get(i).gift;
                        if (starGift2 instanceof TL_stars.TL_starGiftUnique) {
                            this.uniqueGifts.add((TL_stars.TL_starGiftUnique) starGift2);
                        }
                        i++;
                    }
                    int i8 = this.rowCount;
                    this.giftsStartRow = i8;
                    this.rowCount = i8 + this.uniqueGifts.size();
                    this.giftsCount += this.uniqueGifts.size();
                    this.giftsEndRow = this.rowCount;
                    if (PeerColorActivity.this.gifts.loading || !PeerColorActivity.this.gifts.endReached) {
                        int i9 = this.rowCount;
                        this.giftsLoadingStartRow = i9;
                        int i10 = this.giftsCount;
                        int i11 = 3 - (i10 % 3);
                        if (i10 <= 0) {
                            i5 = 9;
                        } else if (i11 > 0) {
                            i5 = i11;
                        }
                        int i12 = i9 + i5;
                        this.rowCount = i12;
                        this.giftsCount = i10 + i5;
                        this.giftsLoadingEndRow = i12;
                    } else if (this.uniqueGifts.isEmpty()) {
                        int i13 = this.rowCount;
                        this.rowCount = i13 + 1;
                        this.giftsEmptyRow = i13;
                    }
                    if (seesLoading()) {
                        giftsList.load();
                    }
                } else if (starGift != null && this.resaleGifts != null) {
                    long clientUserId = UserConfig.getInstance(((BaseFragment) PeerColorActivity.this).currentAccount).getClientUserId();
                    while (i < this.resaleGifts.gifts.size()) {
                        TL_stars.TL_starGiftUnique tL_starGiftUnique = this.resaleGifts.gifts.get(i);
                        if (DialogObject.getPeerDialogId(tL_starGiftUnique.owner_id) != clientUserId && DialogObject.getPeerDialogId(tL_starGiftUnique.host_id) != clientUserId) {
                            this.uniqueGifts.add(tL_starGiftUnique);
                        }
                        i++;
                    }
                    int i14 = this.rowCount;
                    this.giftsStartRow = i14;
                    this.rowCount = i14 + this.uniqueGifts.size();
                    int size = this.giftsCount + this.uniqueGifts.size();
                    this.giftsCount = size;
                    int i15 = this.rowCount;
                    this.giftsEndRow = i15;
                    ResaleGiftsFragment.ResaleGiftsList resaleGiftsList = this.resaleGifts;
                    if (resaleGiftsList.loading || !resaleGiftsList.endReached) {
                        this.giftsLoadingStartRow = i15;
                        int i16 = 3 - (size % 3);
                        if (size <= 0) {
                            i5 = 9;
                        } else if (i16 > 0) {
                            i5 = i16;
                        }
                        int i17 = i15 + i5;
                        this.rowCount = i17;
                        this.giftsCount = size + i5;
                        this.giftsLoadingEndRow = i17;
                    }
                    if (resaleGiftsList != null && seesLoading()) {
                        this.resaleGifts.load();
                    }
                }
                int i18 = this.rowCount;
                this.rowCount = i18 + 1;
                this.giftsInfoRow = i18;
            }
            int i19 = this.rowCount;
            this.rowCount = i19 + 1;
            this.buttonRow = i19;
        }

        public boolean seesLoading() {
            if (this.listView == null) {
                return false;
            }
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                if (this.listView.getChildAt(i) instanceof FlickerLoadingView) {
                    return true;
                }
            }
            return false;
        }

        public void updateButton(boolean z) {
            CharSequence charSequence;
            ButtonWithCounterView buttonWithCounterView = this.button;
            if (buttonWithCounterView == null) {
                return;
            }
            TL_stars.TL_starGiftUnique tL_starGiftUnique = this.selectedResaleGift;
            if (tL_starGiftUnique != null) {
                AmountUtils$Amount resellAmount = tL_starGiftUnique.getResellAmount(AmountUtils$Currency.STARS);
                if (tL_starGiftUnique.resale_ton_only) {
                    this.button.setText(StarsIntroActivity.replaceStars(true, (CharSequence) LocaleController.formatString(R.string.ResellGiftBuyTON, tL_starGiftUnique.getResellAmount(AmountUtils$Currency.TON).asFormatString())), z);
                    this.button.setSubText(StarsIntroActivity.replaceStars(LocaleController.formatPluralStringComma("ResellGiftBuyEq", (int) resellAmount.asDecimal())), z);
                    return;
                } else {
                    this.button.setText(StarsIntroActivity.replaceStars(LocaleController.formatPluralStringComma("ResellGiftBuy", (int) resellAmount.asDecimal())), z);
                    this.button.setSubText(null, z);
                    return;
                }
            }
            if (PeerColorActivity.this.getUserConfig().isPremium() || PeerColorActivity.this.isChannel) {
                charSequence = this.selectedEmojiCollectible != null ? this.buttonCollectible : this.buttonUnlocked;
            } else {
                charSequence = this.buttonLocked;
            }
            buttonWithCounterView.setText(charSequence, z);
            this.button.setSubText(null, z);
        }

        public void updateProfilePreview(boolean z) {
            PeerColorGrid peerColorGrid = this.peerColorPicker;
            if (peerColorGrid != null) {
                peerColorGrid.setSelected(this.selectedColor, z);
            }
            ProfilePreview profilePreview = this.profilePreview;
            if (profilePreview != null) {
                TLRPC.TL_emojiStatusCollectible tL_emojiStatusCollectible = this.selectedEmojiCollectible;
                if (tL_emojiStatusCollectible != null) {
                    profilePreview.setStatusEmoji(tL_emojiStatusCollectible.document_id, true, z);
                    this.profilePreview.setColor(MessagesController.PeerColor.fromCollectible(this.selectedEmojiCollectible), z);
                    this.profilePreview.setEmoji(this.selectedEmojiCollectible.pattern_document_id, true, z);
                } else {
                    boolean zIsEmojiStatusCollectible = DialogObject.isEmojiStatusCollectible(PeerColorActivity.this.dialogId);
                    ProfilePreview profilePreview2 = this.profilePreview;
                    if (zIsEmojiStatusCollectible) {
                        profilePreview2.setStatusEmoji(0L, false, z);
                    } else {
                        profilePreview2.setStatusEmoji(DialogObject.getEmojiStatusDocumentId(PeerColorActivity.this.dialogId), DialogObject.isEmojiStatusCollectible(PeerColorActivity.this.dialogId), z);
                    }
                    this.profilePreview.setColor(this.selectedColor, z);
                    this.profilePreview.setEmoji(this.selectedEmoji, false, z);
                }
            }
            if (this.type == 0 && PeerColorActivity.this.colorBar != null) {
                TLRPC.TL_emojiStatusCollectible tL_emojiStatusCollectible2 = this.selectedEmojiCollectible;
                PeerColorActivity peerColorActivity = PeerColorActivity.this;
                if (tL_emojiStatusCollectible2 == null) {
                    peerColorActivity.colorBar.setColor(((BaseFragment) PeerColorActivity.this).currentAccount, this.selectedColor, z);
                } else {
                    peerColorActivity.colorBar.setColor(MessagesController.PeerColor.fromCollectible(this.selectedEmojiCollectible), z);
                }
            }
            checkResetColorButton();
            updateSelectedGift();
        }

        public void updateMessages() {
            MessageObject messageObject;
            ThemePreviewMessagesCell themePreviewMessagesCell = this.messagesCellPreview;
            if (themePreviewMessagesCell != null) {
                ChatMessageCell[] cells = themePreviewMessagesCell.getCells();
                for (int i = 0; i < cells.length; i++) {
                    ChatMessageCell chatMessageCell = cells[i];
                    if (chatMessageCell != null && (messageObject = chatMessageCell.getMessageObject()) != null) {
                        messageObject.notime = true;
                        PeerColorGrid peerColorGrid = this.peerColorPicker;
                        if (peerColorGrid != null) {
                            messageObject.overrideLinkColor = peerColorGrid.getColorId();
                        }
                        messageObject.overrideLinkEmoji = this.selectedEmoji;
                        messageObject.overrideLinkPeerColor = this.selectedPeerCollectible;
                        cells[i].setAvatar(messageObject);
                        cells[i].invalidate();
                    }
                }
            }
        }

        public void update() {
            updateRows();
            this.listAdapter.notifyDataSetChanged();
        }

        public void updateColors() {
            RecyclerListView recyclerListView = this.listView;
            PeerColorActivity peerColorActivity = PeerColorActivity.this;
            int i = Theme.key_windowBackgroundGray;
            recyclerListView.setBackgroundColor(peerColorActivity.getThemedColor(i));
            ButtonWithCounterView buttonWithCounterView = this.button;
            if (buttonWithCounterView != null) {
                buttonWithCounterView.updateColors();
            }
            ThemePreviewMessagesCell themePreviewMessagesCell = this.messagesCellPreview;
            if (themePreviewMessagesCell != null) {
                themePreviewMessagesCell.invalidate();
            }
            updateProfilePreview(true);
            this.buttonContainer.setBackgroundColor(PeerColorActivity.this.getThemedColor(i));
            this.buttonShadow.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_divider));
            AndroidUtilities.forEachViews((RecyclerView) this.listView, (Consumer<View>) new Consumer() { // from class: org.telegram.ui.PeerColorActivity$Page$$ExternalSyntheticLambda2
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$updateColors$2((View) obj);
                }
            });
        }

        public void lambda$createView$0(Integer num) {
        ViewPagerFixed viewPagerFixed = this.viewPager;
        if (viewPagerFixed != null) {
            viewPagerFixed.scrollToPosition(num.intValue());
        }
    }

    public void lambda$buttonClick$5(Page page, Boolean bool) {
        this.loading = false;
        page.button.setLoading(false);
        if (bool.booleanValue()) {
            apply();
            finishFragment();
            showBulletin();
        }
    }

    public void buy(final TL_stars.TL_starGiftUnique tL_starGiftUnique, final Utilities.Callback<Boolean> callback) {
        final long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        final AmountUtils$Currency amountUtils$Currency = tL_starGiftUnique.resale_ton_only ? AmountUtils$Currency.TON : AmountUtils$Currency.STARS;
        StarsController.getInstance(this.currentAccount, amountUtils$Currency).getResellingGiftForm(tL_starGiftUnique, clientUserId, new Utilities.Callback() { // from class: org.telegram.ui.PeerColorActivity$$ExternalSyntheticLambda9
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$buy$9(amountUtils$Currency, tL_starGiftUnique, clientUserId, callback, (TLRPC.TL_payments_paymentFormStarGift) obj);
            }
        });
    }

    public void lambda$buy$7(boolean[] zArr, TL_stars.TL_starGiftUnique tL_starGiftUnique, long j, final Utilities.Callback callback, StarGiftSheet.PaymentFormState paymentFormState, final Browser.Progress progress) {
        zArr[0] = true;
        progress.init();
        StarsController.getInstance(this.currentAccount, paymentFormState.currency).buyResellingGift(paymentFormState.form, tL_starGiftUnique, j, new Utilities.Callback2() { // from class: org.telegram.ui.PeerColorActivity$$ExternalSyntheticLambda12
            @Override 
            public final void run(Object obj, Object obj2) {
                PeerColorActivity.$r8$lambda$lShP0hmlQro1U0FxWJEJJ8rMQSc(progress, callback, (Boolean) obj, (String) obj2);
            }
        });
    }

    public static private void apply() {
        TL_account.updateColor updatecolor;
        TLRPC.TL_peerColorCollectible tL_peerColorCollectible;
        int i;
        TL_account.updateColor updatecolor2;
        int i2;
        if (this.applying) {
            return;
        }
        if (this.isChannel || getUserConfig().isPremium()) {
            if (this.isChannel) {
                finishFragment();
            } else {
                TLRPC.User currentUser = getUserConfig().getCurrentUser();
                if (currentUser.color == null) {
                    TLRPC.TL_peerColor tL_peerColor = new TLRPC.TL_peerColor();
                    currentUser.color = tL_peerColor;
                    tL_peerColor.flags |= 1;
                    tL_peerColor.color = (int) (currentUser.id % 7);
                }
                TL_stars.TL_starGiftUnique tL_starGiftUnique = null;
                if (this.namePage.selectedColor == UserObject.getColorId(currentUser) && this.namePage.selectedEmoji == UserObject.getEmojiId(currentUser)) {
                    long j = this.namePage.selectedPeerCollectible == null ? 0L : this.namePage.selectedPeerCollectible.collectible_id;
                    TLRPC.PeerColor peerColor = currentUser.color;
                    if (j != (peerColor instanceof TLRPC.TL_peerColorCollectible ? peerColor.collectible_id : 0L)) {
                        this.applyingName = true;
                        updatecolor = new TL_account.updateColor();
                        currentUser.flags2 |= 256;
                        currentUser.color.flags |= 1;
                        tL_peerColorCollectible = this.namePage.selectedPeerCollectible;
                        i = updatecolor.flags;
                        if (tL_peerColorCollectible != null) {
                            updatecolor.flags = i | 4;
                            TLRPC.TL_inputPeerColorCollectible tL_inputPeerColorCollectible = new TLRPC.TL_inputPeerColorCollectible();
                            updatecolor.color = tL_inputPeerColorCollectible;
                            tL_inputPeerColorCollectible.collectible_id = this.namePage.selectedPeerCollectible.collectible_id;
                            currentUser.color = this.namePage.selectedPeerCollectible;
                        } else {
                            updatecolor.flags = i | 4;
                            TLRPC.TL_peerColor tL_peerColor2 = new TLRPC.TL_peerColor();
                            updatecolor.color = tL_peerColor2;
                            tL_peerColor2.flags |= 1;
                            tL_peerColor2.color = this.namePage.selectedColor;
                            TLRPC.PeerColor peerColor2 = currentUser.color;
                            peerColor2.flags |= 1;
                            peerColor2.color = this.namePage.selectedColor;
                            if (this.namePage.selectedEmoji != 0) {
                                updatecolor.flags |= 1;
                                TLRPC.PeerColor peerColor3 = currentUser.color;
                                peerColor3.flags |= 2;
                                TLRPC.PeerColor peerColor4 = updatecolor.color;
                                peerColor4.flags |= 2;
                                long j2 = this.namePage.selectedEmoji;
                                peerColor3.background_emoji_id = j2;
                                peerColor4.background_emoji_id = j2;
                            } else {
                                TLRPC.PeerColor peerColor5 = currentUser.color;
                                peerColor5.flags &= -3;
                                peerColor5.background_emoji_id = 0L;
                            }
                        }
                        getConnectionsManager().sendRequest(updatecolor, null);
                    }
                } else {
                    this.applyingName = true;
                    updatecolor = new TL_account.updateColor();
                    currentUser.flags2 |= 256;
                    currentUser.color.flags |= 1;
                    tL_peerColorCollectible = this.namePage.selectedPeerCollectible;
                    i = updatecolor.flags;
                    if (tL_peerColorCollectible != null) {
                        updatecolor.flags = i | 4;
                        TLRPC.TL_inputPeerColorCollectible tL_inputPeerColorCollectible2 = new TLRPC.TL_inputPeerColorCollectible();
                        updatecolor.color = tL_inputPeerColorCollectible2;
                        tL_inputPeerColorCollectible2.collectible_id = this.namePage.selectedPeerCollectible.collectible_id;
                        currentUser.color = this.namePage.selectedPeerCollectible;
                    } else {
                        updatecolor.flags = i | 4;
                        TLRPC.TL_peerColor tL_peerColor3 = new TLRPC.TL_peerColor();
                        updatecolor.color = tL_peerColor3;
                        tL_peerColor3.flags |= 1;
                        tL_peerColor3.color = this.namePage.selectedColor;
                        TLRPC.PeerColor peerColor6 = currentUser.color;
                        peerColor6.flags |= 1;
                        peerColor6.color = this.namePage.selectedColor;
                        if (this.namePage.selectedEmoji != 0) {
                            updatecolor.flags |= 1;
                            TLRPC.PeerColor peerColor7 = currentUser.color;
                            peerColor7.flags |= 2;
                            TLRPC.PeerColor peerColor8 = updatecolor.color;
                            peerColor8.flags |= 2;
                            long j3 = this.namePage.selectedEmoji;
                            peerColor7.background_emoji_id = j3;
                            peerColor8.background_emoji_id = j3;
                        } else {
                            TLRPC.PeerColor peerColor9 = currentUser.color;
                            peerColor9.flags &= -3;
                            peerColor9.background_emoji_id = 0L;
                        }
                    }
                    getConnectionsManager().sendRequest(updatecolor, null);
                }
                if (this.profilePage.selectedColor != UserObject.getProfileColorId(currentUser) || this.profilePage.selectedEmoji != UserObject.getOnlyProfileEmojiId(currentUser)) {
                    this.applyingProfile = true;
                    if (currentUser.profile_color == null) {
                        currentUser.profile_color = new TLRPC.TL_peerColor();
                    }
                    updatecolor2 = new TL_account.updateColor();
                    updatecolor2.for_profile = true;
                    currentUser.flags2 |= 512;
                    if (this.profilePage.selectedColor < 0) {
                        currentUser.profile_color.flags &= -2;
                    } else {
                        if (updatecolor2.color == null) {
                            updatecolor2.flags |= 4;
                            updatecolor2.color = new TLRPC.TL_peerColor();
                        }
                        TLRPC.PeerColor peerColor10 = updatecolor2.color;
                        peerColor10.flags |= 1;
                        peerColor10.color = this.profilePage.selectedColor;
                        TLRPC.PeerColor peerColor11 = currentUser.profile_color;
                        peerColor11.flags |= 1;
                        peerColor11.color = this.profilePage.selectedColor;
                    }
                    if (this.profilePage.selectedEmoji != 0) {
                        i2 = updatecolor2.flags;
                        updatecolor2.flags = i2 | 1;
                        currentUser.profile_color.flags |= 2;
                        if (updatecolor2.color == null) {
                            updatecolor2.flags = i2 | 5;
                            updatecolor2.color = new TLRPC.TL_peerColor();
                        }
                        TLRPC.PeerColor peerColor12 = updatecolor2.color;
                        peerColor12.flags |= 2;
                        TLRPC.PeerColor peerColor13 = currentUser.profile_color;
                        long j4 = this.profilePage.selectedEmoji;
                        peerColor13.background_emoji_id = j4;
                        peerColor12.background_emoji_id = j4;
                    } else {
                        TLRPC.PeerColor peerColor14 = currentUser.profile_color;
                        peerColor14.flags &= -3;
                        peerColor14.background_emoji_id = 0L;
                    }
                    getConnectionsManager().sendRequest(updatecolor2, null);
                } else if ((this.profilePage.selectedEmojiCollectible == null ? 0L : this.profilePage.selectedEmojiCollectible.collectible_id) != UserObject.getProfileCollectibleId(currentUser)) {
                    this.applyingProfile = true;
                    if (currentUser.profile_color == null) {
                        currentUser.profile_color = new TLRPC.TL_peerColor();
                    }
                    updatecolor2 = new TL_account.updateColor();
                    updatecolor2.for_profile = true;
                    currentUser.flags2 |= 512;
                    if (this.profilePage.selectedColor < 0) {
                        currentUser.profile_color.flags &= -2;
                    } else {
                        if (updatecolor2.color == null) {
                            updatecolor2.flags |= 4;
                            updatecolor2.color = new TLRPC.TL_peerColor();
                        }
                        TLRPC.PeerColor peerColor15 = updatecolor2.color;
                        peerColor15.flags |= 1;
                        peerColor15.color = this.profilePage.selectedColor;
                        TLRPC.PeerColor peerColor16 = currentUser.profile_color;
                        peerColor16.flags |= 1;
                        peerColor16.color = this.profilePage.selectedColor;
                    }
                    if (this.profilePage.selectedEmoji != 0) {
                        i2 = updatecolor2.flags;
                        updatecolor2.flags = i2 | 1;
                        currentUser.profile_color.flags |= 2;
                        if (updatecolor2.color == null) {
                            updatecolor2.flags = i2 | 5;
                            updatecolor2.color = new TLRPC.TL_peerColor();
                        }
                        TLRPC.PeerColor peerColor17 = updatecolor2.color;
                        peerColor17.flags |= 2;
                        TLRPC.PeerColor peerColor18 = currentUser.profile_color;
                        long j5 = this.profilePage.selectedEmoji;
                        peerColor18.background_emoji_id = j5;
                        peerColor17.background_emoji_id = j5;
                    } else {
                        TLRPC.PeerColor peerColor19 = currentUser.profile_color;
                        peerColor19.flags &= -3;
                        peerColor19.background_emoji_id = 0L;
                    }
                    getConnectionsManager().sendRequest(updatecolor2, null);
                }
                if (!eq(currentUser.emoji_status, this.profilePage.selectedEmojiCollectible) && (this.profilePage.selectedEmojiCollectible != null || DialogObject.isEmojiStatusCollectible(currentUser.emoji_status))) {
                    ?? tL_emojiStatusEmpty = new TLRPC.TL_emojiStatusEmpty();
                    if (this.profilePage.selectedEmojiCollectible != null) {
                        long j6 = this.profilePage.selectedEmojiCollectible.collectible_id;
                        for (int i3 = 0; i3 < this.profilePage.uniqueGifts.size(); i3++) {
                            TL_stars.TL_starGiftUnique tL_starGiftUnique2 = this.profilePage.uniqueGifts.get(i3);
                            if (tL_starGiftUnique2.id == j6) {
                                tL_starGiftUnique = tL_starGiftUnique2;
                                break;
                            }
                        }
                    }
                    if (tL_starGiftUnique != null) {
                        tL_emojiStatusEmpty = new TLRPC.TL_inputEmojiStatusCollectible();
                        tL_emojiStatusEmpty.collectible_id = tL_starGiftUnique.id;
                    }
                    getMessagesController().updateEmojiStatus(0L, tL_emojiStatusEmpty, tL_starGiftUnique);
                }
                getMessagesController().putUser(currentUser, false);
                getUserConfig().saveConfig(true);
                finishFragment();
                showBulletin();
            }
            this.applying = true;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_EMOJI_STATUS));
        }
    }

    private void showBulletin() {
        if (this.bulletinFragment != null) {
            if (this.applyingName && (!this.applyingProfile || getCurrentPage() == this.namePage)) {
                if (this.namePage.selectedColor < 0) {
                    if (this.namePage.selectedPeerCollectible == null) {
                        return;
                    } else {
                        BulletinFactory.of(this.bulletinFragment).createSimpleBulletin(PeerColorDrawable.from(this.namePage.selectedPeerCollectible), LocaleController.getString(this.isChannel ? R.string.ChannelColorApplied : R.string.UserColorApplied)).show();
                    }
                } else {
                    BulletinFactory.of(this.bulletinFragment).createSimpleBulletin(PeerColorDrawable.from(this.currentAccount, this.namePage.selectedColor), LocaleController.getString(this.isChannel ? R.string.ChannelColorApplied : R.string.UserColorApplied)).show();
                }
            } else if (this.applyingProfile && (!this.applyingName || getCurrentPage() == this.profilePage)) {
                if (this.profilePage.selectedColor < 0) {
                    long j = this.profilePage.selectedEmoji;
                    BaseFragment baseFragment = this.bulletinFragment;
                    if (j != 0) {
                        BulletinFactory.of(baseFragment).createStaticEmojiBulletin(AnimatedEmojiDrawable.findDocument(this.currentAccount, this.profilePage.selectedEmoji), LocaleController.getString(this.isChannel ? R.string.ChannelProfileColorEmojiApplied : R.string.UserProfileColorEmojiApplied)).show();
                    } else {
                        BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(this.isChannel ? R.string.ChannelProfileColorResetApplied : R.string.UserProfileColorResetApplied)).show();
                    }
                } else {
                    BulletinFactory.of(this.bulletinFragment).createSimpleBulletin(PeerColorDrawable.fromProfile(this.currentAccount, this.profilePage.selectedColor), LocaleController.getString(this.isChannel ? R.string.ChannelProfileColorApplied : R.string.UserProfileColorApplied)).show();
                }
            }
            this.bulletinFragment = null;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentClosed() {
        super.onFragmentClosed();
        Bulletin.removeDelegate(this);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.starUserGiftsLoaded);
        getNotificationCenter().removeObserver(this, NotificationCenter.starGiftsLoaded);
        Bulletin.removeDelegate(this);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.PeerColorActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                this.f$0.updateColors();
            }
        }, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhiteBlackText, Theme.key_windowBackgroundWhiteGrayText2, Theme.key_listSelector, Theme.key_windowBackgroundGray, Theme.key_windowBackgroundWhiteGrayText4, Theme.key_text_RedRegular, Theme.key_windowBackgroundChecked, Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void updateColors() {
        this.contentView.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundGray));
        SimpleTextView simpleTextView = this.titleView;
        if (simpleTextView != null) {
            simpleTextView.setTextColor(getThemedColor(Theme.key_actionBarDefaultTitle));
        }
        this.namePage.updateColors();
        this.profilePage.updateColors();
        ColoredActionBar coloredActionBar = this.colorBar;
        if (coloredActionBar != null) {
            coloredActionBar.updateColors();
        }
        setNavigationBarColor(getNavigationBarColor());
    }

    @Override 
    @SuppressLint({"NotifyDataSetChanged"})
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i2 != this.currentAccount) {
            return;
        }
        if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            this.namePage.premiumChanged();
            this.profilePage.premiumChanged();
        } else if (i == NotificationCenter.starUserGiftsLoaded) {
            this.namePage.update();
            this.profilePage.update();
        } else if (i == NotificationCenter.starGiftsLoaded) {
            this.namePage.update();
            this.profilePage.update();
        }
    }

    public static class LevelLock extends Drawable {
        private int foregroundColor;
        private final PremiumGradient.PremiumGradientTools gradientTools;
        private final Drawable lock;
        private final float lockScale;
        private final Theme.ResourcesProvider resourcesProvider;
        private final Text text;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public LevelLock(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            this(context, false, i, resourcesProvider);
        }

        public LevelLock(Context context, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
            this.lockScale = 0.875f;
            this.foregroundColor = Integer.MIN_VALUE;
            this.resourcesProvider = resourcesProvider;
            this.text = new Text(LocaleController.formatPluralString(z ? "BoostLevelPlus" : "BoostLevel", i, new Object[0]), 12.0f, AndroidUtilities.bold());
            this.lock = context.getResources().getDrawable(R.drawable.mini_switch_lock).mutate();
            this.gradientTools = new PremiumGradient.PremiumGradientTools(Theme.key_premiumGradient1, Theme.key_premiumGradient2, -1, -1, -1, resourcesProvider);
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int i = getBounds().left;
            int iCenterY = getBounds().centerY();
            RectF rectF = AndroidUtilities.rectTmp;
            float f = iCenterY;
            rectF.set(i, f - (getIntrinsicHeight() / 2.0f), getIntrinsicWidth() + i, (getIntrinsicHeight() / 2.0f) + f);
            this.gradientTools.gradientMatrix(rectF);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), this.gradientTools.paint);
            this.lock.setBounds(AndroidUtilities.dp(3.33f) + i, (int) (f - ((this.lock.getIntrinsicHeight() * 0.875f) / 2.0f)), (int) (AndroidUtilities.dp(3.33f) + i + (this.lock.getIntrinsicWidth() * 0.875f)), (int) (((this.lock.getIntrinsicHeight() * 0.875f) / 2.0f) + f));
            int color = Theme.isCurrentThemeMonet(this.resourcesProvider) ? Theme.getColor(Theme.key_chats_actionIcon, this.resourcesProvider) : -1;
            if (this.foregroundColor != color) {
                this.foregroundColor = color;
                this.lock.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
            this.lock.draw(canvas);
            this.text.draw(canvas, i + AndroidUtilities.dp(3.66f) + (this.lock.getIntrinsicWidth() * 0.875f), f, color, 1.0f);
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return (int) (AndroidUtilities.dp(9.66f) + (this.lock.getIntrinsicWidth() * 0.875f) + this.text.getWidth());
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(18.33f);
        }
    }

    public static CharSequence withLevelLock(CharSequence charSequence, int i) {
        if (i <= 0) {
            return charSequence;
        }
        Context context = ApplicationLoader.applicationContext;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        spannableStringBuilder.append((CharSequence) "  L");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(new LevelLock(context, i, null));
        coloredImageSpan.setTranslateY(AndroidUtilities.dp(1.0f));
        spannableStringBuilder.setSpan(coloredImageSpan, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
        return spannableStringBuilder;
    }

    public static class ChangeNameColorCell extends View {
        private final Text buttonText;
        private PeerColorDrawable color1Drawable;
        private PeerColorDrawable color2Drawable;
        private final int currentAccount;
        private final Drawable drawable;
        private final boolean isChannelOrGroup;
        private final boolean isGroup;
        private LevelLock lock;
        private boolean needDivider;
        private final Theme.ResourcesProvider resourcesProvider;
        private Text userText;
        private final Paint userTextBackgroundPaint;
        private int userTextColorKey;

        public ChangeNameColorCell(int i, long j, Context context, Theme.ResourcesProvider resourcesProvider) {
            int i2;
            int i3;
            super(context);
            this.userTextBackgroundPaint = new Paint(1);
            this.userTextColorKey = -1;
            MessagesController messagesController = MessagesController.getInstance(i);
            TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j));
            this.currentAccount = i;
            boolean z = j < 0;
            this.isChannelOrGroup = z;
            boolean z2 = z && !ChatObject.isChannelAndNotMegaGroup(chat);
            this.isGroup = z2;
            this.resourcesProvider = resourcesProvider;
            Drawable drawableMutate = context.getResources().getDrawable(R.drawable.menu_edit_appearance).mutate();
            this.drawable = drawableMutate;
            drawableMutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider), PorterDuff.Mode.SRC_IN));
            if (z) {
                i2 = z2 ? R.string.ChangeGroupAppearance : R.string.ChangeChannelNameColor2;
            } else {
                i2 = R.string.ChangeUserNameColor;
            }
            CharSequence string = LocaleController.getString(i2);
            if (z && !z2 && MessagesController.getInstance(i).getMainSettings().getInt("boostingappearance", 0) < 3) {
                MessagesController.PeerColors peerColors = messagesController.peerColors;
                int i4 = Integer.MAX_VALUE;
                if (peerColors != null) {
                    int iMin = Math.min(Integer.MAX_VALUE, peerColors.maxLevel());
                    int iMax = Math.max(0, messagesController.peerColors.maxLevel());
                    int iMin2 = Math.min(iMin, messagesController.peerColors.minLevel());
                    int iMax2 = Math.max(iMax, messagesController.peerColors.minLevel());
                    i4 = iMin2;
                    i3 = iMax2;
                } else {
                    i3 = 0;
                }
                int iMin3 = Math.min(i4, messagesController.channelBgIconLevelMin);
                int iMin4 = Math.min(i3, messagesController.channelBgIconLevelMin);
                MessagesController.PeerColors peerColors2 = messagesController.profilePeerColors;
                if (peerColors2 != null) {
                    int iMin5 = Math.min(iMin3, peerColors2.maxLevel());
                    int iMax3 = Math.max(iMin4, messagesController.profilePeerColors.maxLevel());
                    iMin3 = Math.min(iMin5, messagesController.profilePeerColors.minLevel());
                    iMin4 = Math.max(iMax3, messagesController.profilePeerColors.minLevel());
                }
                int iMin6 = Math.min(iMin3, messagesController.channelProfileIconLevelMin);
                int iMax4 = Math.max(iMin4, messagesController.channelProfileIconLevelMin);
                int iMin7 = Math.min(iMin6, messagesController.channelEmojiStatusLevelMin);
                int iMax5 = Math.max(iMax4, messagesController.channelEmojiStatusLevelMin);
                int iMin8 = Math.min(iMin7, messagesController.channelWallpaperLevelMin);
                int iMax6 = Math.max(iMax5, messagesController.channelWallpaperLevelMin);
                int iMin9 = Math.min(iMin8, messagesController.channelCustomWallpaperLevelMin);
                int iMax7 = Math.max(iMax6, messagesController.channelCustomWallpaperLevelMin);
                int i5 = chat != null ? chat.level : 0;
                if (i5 < iMax7) {
                    this.lock = new LevelLock(context, true, Math.max(i5, iMin9), resourcesProvider);
                }
            }
            setContentDescription(string);
            if (z && this.lock == null) {
                string = TextCell.applyNewSpan(string);
            }
            this.buttonText = new Text(string, 16.0f);
            updateColors();
        }

        public void updateColors() {
            int i;
            this.drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(this.isChannelOrGroup ? Theme.key_windowBackgroundWhiteGrayIcon : Theme.key_windowBackgroundWhiteBlueText4, this.resourcesProvider), PorterDuff.Mode.SRC_IN));
            this.buttonText.setColor(Theme.getColor(this.isChannelOrGroup ? Theme.key_windowBackgroundWhiteBlackText : Theme.key_windowBackgroundWhiteBlueText4, this.resourcesProvider));
            if (this.userText == null || this.userTextBackgroundPaint == null || (i = this.userTextColorKey) == -1) {
                return;
            }
            int color = Theme.getColor(i, this.resourcesProvider);
            this.userText.setColor(color);
            this.userTextBackgroundPaint.setColor(Theme.multAlpha(color, 0.1f));
        }

        public void set(TLRPC.Chat chat, boolean z) {
            int color;
            ArrayList<Integer> arrayList;
            if (chat == null) {
                return;
            }
            this.needDivider = z;
            this.userText = new Text(Emoji.replaceEmoji(chat.title, Theme.chat_msgTextPaint.getFontMetricsInt(), false), 13.0f, AndroidUtilities.bold());
            PeerColorDrawable peerColorDrawable = this.color1Drawable;
            if (peerColorDrawable != null) {
                peerColorDrawable.setView(null);
            }
            TLRPC.EmojiStatus emojiStatus = chat.emoji_status;
            if (emojiStatus instanceof TLRPC.TL_emojiStatusCollectible) {
                this.color1Drawable = PeerColorDrawable.from((TLRPC.TL_emojiStatusCollectible) emojiStatus);
            } else {
                this.color1Drawable = ChatObject.getProfileColorId(chat) >= 0 ? PeerColorDrawable.fromProfile(this.currentAccount, ChatObject.getProfileColorId(chat)).setRadius(AndroidUtilities.dp(11.0f)) : null;
            }
            PeerColorDrawable peerColorDrawable2 = this.color1Drawable;
            if (peerColorDrawable2 != null) {
                peerColorDrawable2.setView(this);
            }
            TLRPC.PeerColor peerColor = chat.color;
            if (peerColor instanceof TLRPC.TL_peerColorCollectible) {
                TLRPC.TL_peerColorCollectible tL_peerColorCollectible = (TLRPC.TL_peerColorCollectible) peerColor;
                Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
                boolean zIsDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
                int i = (!zIsDark || (tL_peerColorCollectible.flags & 1) == 0) ? tL_peerColorCollectible.accent_color : tL_peerColorCollectible.dark_accent_color;
                if (!zIsDark || (arrayList = tL_peerColorCollectible.dark_colors) == null) {
                    arrayList = tL_peerColorCollectible.colors;
                }
                int iIntValue = arrayList.get(0).intValue() | (-16777216);
                int iIntValue2 = arrayList.size() >= 2 ? arrayList.get(1).intValue() | (-16777216) : iIntValue;
                int iIntValue3 = arrayList.size() >= 3 ? arrayList.get(2).intValue() | (-16777216) : iIntValue;
                this.userText.setColor(i);
                this.userTextBackgroundPaint.setColor(Theme.multAlpha(i, 0.1f));
                PeerColorDrawable radius = new PeerColorDrawable(iIntValue, iIntValue2, iIntValue3, tL_peerColorCollectible.gift_emoji_id).setRadius(AndroidUtilities.dp(11.0f));
                this.color2Drawable = radius;
                radius.setView(this);
                return;
            }
            int colorId = ChatObject.getColorId(chat);
            if (colorId < 7) {
                int i2 = Theme.keys_avatar_nameInMessage[colorId];
                this.userTextColorKey = i2;
                color = Theme.getColor(i2, this.resourcesProvider);
            } else {
                MessagesController.PeerColors peerColors = MessagesController.getInstance(UserConfig.selectedAccount).peerColors;
                MessagesController.PeerColor color2 = peerColors != null ? peerColors.getColor(colorId) : null;
                if (color2 != null) {
                    this.userTextColorKey = -1;
                    color = color2.getColor1();
                } else {
                    int i3 = Theme.keys_avatar_nameInMessage[0];
                    this.userTextColorKey = i3;
                    color = Theme.getColor(i3, this.resourcesProvider);
                }
            }
            this.userText.setColor(color);
            this.userTextBackgroundPaint.setColor(Theme.multAlpha(color, 0.1f));
            PeerColorDrawable radius2 = PeerColorDrawable.from(this.currentAccount, colorId).setRadius(AndroidUtilities.dp(11.0f));
            this.color2Drawable = radius2;
            if (radius2 != null) {
                radius2.setView(this);
            }
        }

        public void set(TLRPC.User user) {
            int color;
            ArrayList<Integer> arrayList;
            if (user == null) {
                return;
            }
            String str = user.first_name;
            String strTrim = str == null ? _UrlKt.FRAGMENT_ENCODE_SET : str.trim();
            int iIndexOf = strTrim.indexOf(" ");
            if (iIndexOf > 0) {
                strTrim = strTrim.substring(0, iIndexOf);
            }
            this.userText = new Text(Emoji.replaceEmoji(strTrim, Theme.chat_msgTextPaint.getFontMetricsInt(), false), 13.0f, AndroidUtilities.bold());
            PeerColorDrawable peerColorDrawable = this.color1Drawable;
            if (peerColorDrawable != null) {
                peerColorDrawable.setView(null);
            }
            TLRPC.EmojiStatus emojiStatus = user.emoji_status;
            if (emojiStatus instanceof TLRPC.TL_emojiStatusCollectible) {
                this.color1Drawable = PeerColorDrawable.from((TLRPC.TL_emojiStatusCollectible) emojiStatus);
            } else {
                this.color1Drawable = UserObject.getProfileColorId(user) >= 0 ? PeerColorDrawable.fromProfile(this.currentAccount, UserObject.getProfileColorId(user)).setRadius(AndroidUtilities.dp(11.0f)) : null;
            }
            PeerColorDrawable peerColorDrawable2 = this.color1Drawable;
            if (peerColorDrawable2 != null) {
                peerColorDrawable2.setView(this);
            }
            TLRPC.PeerColor peerColor = user.color;
            if (peerColor instanceof TLRPC.TL_peerColorCollectible) {
                TLRPC.TL_peerColorCollectible tL_peerColorCollectible = (TLRPC.TL_peerColorCollectible) peerColor;
                Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
                boolean zIsDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
                int i = (!zIsDark || (tL_peerColorCollectible.flags & 1) == 0) ? tL_peerColorCollectible.accent_color : tL_peerColorCollectible.dark_accent_color;
                if (!zIsDark || (arrayList = tL_peerColorCollectible.dark_colors) == null) {
                    arrayList = tL_peerColorCollectible.colors;
                }
                int iIntValue = arrayList.get(0).intValue() | (-16777216);
                int iIntValue2 = arrayList.size() >= 2 ? arrayList.get(1).intValue() | (-16777216) : iIntValue;
                int iIntValue3 = arrayList.size() >= 3 ? arrayList.get(2).intValue() | (-16777216) : iIntValue;
                this.userText.setColor(i);
                this.userTextBackgroundPaint.setColor(Theme.multAlpha(i, 0.1f));
                PeerColorDrawable radius = new PeerColorDrawable(iIntValue, iIntValue2, iIntValue3, tL_peerColorCollectible.gift_emoji_id).setRadius(AndroidUtilities.dp(11.0f));
                this.color2Drawable = radius;
                radius.setView(this);
                return;
            }
            int colorId = UserObject.getColorId(user);
            if (colorId < 7) {
                int i2 = Theme.keys_avatar_nameInMessage[colorId];
                this.userTextColorKey = i2;
                color = Theme.getColor(i2, this.resourcesProvider);
            } else {
                MessagesController.PeerColors peerColors = MessagesController.getInstance(UserConfig.selectedAccount).peerColors;
                MessagesController.PeerColor color2 = peerColors != null ? peerColors.getColor(colorId) : null;
                if (color2 != null) {
                    this.userTextColorKey = -1;
                    color = color2.getColor1();
                } else {
                    int i3 = Theme.keys_avatar_nameInMessage[0];
                    this.userTextColorKey = i3;
                    color = Theme.getColor(i3, this.resourcesProvider);
                }
            }
            this.userText.setColor(color);
            this.userTextBackgroundPaint.setColor(Theme.multAlpha(color, 0.1f));
            this.color2Drawable = PeerColorDrawable.from(this.currentAccount, colorId).setRadius(AndroidUtilities.dp(11.0f));
        }

        @Override // android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), TLObject.FLAG_30));
        }

        private int rtl(int i) {
            return LocaleController.isRTL ? getMeasuredWidth() - i : i;
        }

        @Override // android.view.View
        public void dispatchDraw(Canvas canvas) {
            DrawableUtils.setBounds(this.drawable, rtl(AndroidUtilities.dp(32.0f)), getMeasuredHeight() / 2.0f, 17);
            this.drawable.draw(canvas);
            Text text = this.buttonText;
            int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(171.0f);
            LevelLock levelLock = this.lock;
            text.ellipsize(measuredWidth - (levelLock != null ? levelLock.getIntrinsicWidth() + AndroidUtilities.dp(8.0f) : 0));
            float measuredWidth2 = LocaleController.isRTL ? (getMeasuredWidth() - this.buttonText.getWidth()) - AndroidUtilities.dp(71.0f) : AndroidUtilities.dp(71.0f);
            this.buttonText.draw(canvas, measuredWidth2, getMeasuredHeight() / 2.0f);
            if (this.lock != null) {
                int width = (int) (measuredWidth2 + this.buttonText.getWidth() + AndroidUtilities.dp(6.0f));
                this.lock.setBounds(width, 0, width, getHeight());
                this.lock.draw(canvas);
            }
            boolean z = this.isGroup;
            if (z && this.color2Drawable != null) {
                int iDp = LocaleController.isRTL ? AndroidUtilities.dp(58.0f) : getMeasuredWidth() - AndroidUtilities.dp(24.0f);
                this.color2Drawable.setBounds(iDp - AndroidUtilities.dp(11.0f), (getMeasuredHeight() - AndroidUtilities.dp(11.0f)) / 2, iDp, (getMeasuredHeight() + AndroidUtilities.dp(11.0f)) / 2);
                this.color2Drawable.stroke(AndroidUtilities.dpf2(3.0f), Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider));
                this.color2Drawable.draw(canvas);
            } else if (this.color1Drawable != null && this.color2Drawable != null) {
                int iDp2 = LocaleController.isRTL ? AndroidUtilities.dp(58.0f) : getMeasuredWidth() - AndroidUtilities.dp(24.0f);
                this.color2Drawable.setBounds(iDp2 - AndroidUtilities.dp(11.0f), (getMeasuredHeight() - AndroidUtilities.dp(11.0f)) / 2, iDp2, (getMeasuredHeight() + AndroidUtilities.dp(11.0f)) / 2);
                PeerColorDrawable peerColorDrawable = this.color2Drawable;
                float fDpf2 = AndroidUtilities.dpf2(3.0f);
                int i = Theme.key_windowBackgroundWhite;
                peerColorDrawable.stroke(fDpf2, Theme.getColor(i, this.resourcesProvider));
                this.color2Drawable.draw(canvas);
                int iDp3 = iDp2 - AndroidUtilities.dp(18.0f);
                this.color1Drawable.setBounds(iDp3 - AndroidUtilities.dp(11.0f), (getMeasuredHeight() - AndroidUtilities.dp(11.0f)) / 2, iDp3, (getMeasuredHeight() + AndroidUtilities.dp(11.0f)) / 2);
                this.color1Drawable.stroke(AndroidUtilities.dpf2(3.0f), Theme.getColor(i, this.resourcesProvider));
                this.color1Drawable.draw(canvas);
            } else if (this.userText != null && !z) {
                float measuredWidth3 = getMeasuredWidth() - AndroidUtilities.dp(116.0f);
                float width2 = this.buttonText.getWidth();
                LevelLock levelLock2 = this.lock;
                float fMin = (int) (measuredWidth3 - Math.min(width2 + (levelLock2 == null ? 0 : levelLock2.getIntrinsicWidth() + AndroidUtilities.dp(12.0f)), getMeasuredWidth() - AndroidUtilities.dp(164.0f)));
                int iMin = (int) Math.min(this.userText.getWidth(), fMin);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(LocaleController.isRTL ? AndroidUtilities.dp(15.0f) : (getMeasuredWidth() - AndroidUtilities.dp(33.0f)) - iMin, (getMeasuredHeight() - AndroidUtilities.dp(22.0f)) / 2.0f, LocaleController.isRTL ? AndroidUtilities.dp(33.0f) + iMin : getMeasuredWidth() - AndroidUtilities.dp(15.0f), (getMeasuredHeight() + AndroidUtilities.dp(22.0f)) / 2.0f);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), this.userTextBackgroundPaint);
                this.userText.ellipsize(fMin).draw(canvas, LocaleController.isRTL ? AndroidUtilities.dp(24.0f) : (getMeasuredWidth() - AndroidUtilities.dp(24.0f)) - iMin, getMeasuredHeight() / 2.0f);
            }
            if (this.needDivider) {
                Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
                Paint paint = resourcesProvider != null ? resourcesProvider.getPaint("paintDivider") : null;
                if (paint == null) {
                    paint = Theme.dividerPaint;
                }
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(58.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(58.0f) : 0), getMeasuredHeight() - 1, paint);
            }
        }
    }

    public static class PeerColorGrid extends View {
        private final Paint backgroundPaint;
        private ColorButton[] buttons;
        private final int currentAccount;
        private final Paint dividerPaint;
        private boolean lock;
        private boolean needDivider;
        private Utilities.Callback<Integer> onColorClick;
        final int[] order;
        private ColorButton pressedButton;
        private final Theme.ResourcesProvider resourcesProvider;
        private int selectedColorId;
        private final int type;

        public class ColorButton {
            private final ButtonBounce bounce;
            private Paint closePaint;
            private Path closePath;
            private boolean hasClose;
            private boolean hasColor2;
            private boolean hasColor3;
            public int id;
            private Drawable lockDrawable;
            private boolean pressed;
            private boolean selected;
            private final AnimatedFloat selectedT;
            private final Paint paint1 = new Paint(1);
            private final Paint paint2 = new Paint(1);
            private final Paint paint3 = new Paint(1);
            private final Path circlePath = new Path();
            private final Path color2Path = new Path();
            private final RectF bounds = new RectF();
            public final RectF clickBounds = new RectF();

            public ColorButton() {
                this.bounce = new ButtonBounce(PeerColorGrid.this);
                this.selectedT = new AnimatedFloat(PeerColorGrid.this, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            }

            public void set(int i) {
                this.hasColor3 = false;
                this.hasColor2 = false;
                this.paint1.setColor(i);
            }

            public void setClose(boolean z) {
                this.hasClose = z;
            }

            public void set(MessagesController.PeerColor peerColor) {
                if (peerColor == null) {
                    return;
                }
                boolean zIsCurrentThemeDark = PeerColorGrid.this.resourcesProvider == null ? Theme.isCurrentThemeDark() : PeerColorGrid.this.resourcesProvider.isDark();
                if (PeerColorGrid.this.type == 1) {
                    if (zIsCurrentThemeDark && peerColor.hasColor2() && !peerColor.hasColor3()) {
                        this.paint1.setColor(peerColor.getColor(1, PeerColorGrid.this.resourcesProvider));
                        this.paint2.setColor(peerColor.getColor(0, PeerColorGrid.this.resourcesProvider));
                    } else {
                        this.paint1.setColor(peerColor.getColor(0, PeerColorGrid.this.resourcesProvider));
                        this.paint2.setColor(peerColor.getColor(1, PeerColorGrid.this.resourcesProvider));
                    }
                    this.paint3.setColor(peerColor.getColor(2, PeerColorGrid.this.resourcesProvider));
                    this.hasColor2 = peerColor.hasColor2(zIsCurrentThemeDark);
                    this.hasColor3 = peerColor.hasColor3(zIsCurrentThemeDark);
                    return;
                }
                this.paint1.setColor(peerColor.getColor(0, PeerColorGrid.this.resourcesProvider));
                Paint paint = this.paint2;
                boolean zHasColor6 = peerColor.hasColor6(zIsCurrentThemeDark);
                PeerColorGrid peerColorGrid = PeerColorGrid.this;
                paint.setColor(zHasColor6 ? peerColor.getColor(1, peerColorGrid.resourcesProvider) : peerColor.getColor(0, peerColorGrid.resourcesProvider));
                this.hasColor2 = peerColor.hasColor6(zIsCurrentThemeDark);
                this.hasColor3 = false;
            }

            public void setSelected(boolean z, boolean z2) {
                this.selected = z;
                if (!z2) {
                    this.selectedT.set(z, true);
                }
                PeerColorGrid.this.invalidate();
            }

            public void layout(RectF rectF) {
                this.bounds.set(rectF);
            }

            public void layoutClickBounds(RectF rectF) {
                this.clickBounds.set(rectF);
            }

            public void draw(Canvas canvas) {
                canvas.save();
                float scale = this.bounce.getScale(0.05f);
                canvas.scale(scale, scale, this.bounds.centerX(), this.bounds.centerY());
                canvas.save();
                this.circlePath.rewind();
                this.circlePath.addCircle(this.bounds.centerX(), this.bounds.centerY(), Math.min(this.bounds.height() / 2.0f, this.bounds.width() / 2.0f), Path.Direction.CW);
                canvas.clipPath(this.circlePath);
                canvas.drawPaint(this.paint1);
                if (this.hasColor2) {
                    this.color2Path.rewind();
                    Path path = this.color2Path;
                    RectF rectF = this.bounds;
                    path.moveTo(rectF.right, rectF.top);
                    Path path2 = this.color2Path;
                    RectF rectF2 = this.bounds;
                    path2.lineTo(rectF2.right, rectF2.bottom);
                    Path path3 = this.color2Path;
                    RectF rectF3 = this.bounds;
                    path3.lineTo(rectF3.left, rectF3.bottom);
                    this.color2Path.close();
                    canvas.drawPath(this.color2Path, this.paint2);
                }
                canvas.restore();
                if (this.hasColor3) {
                    canvas.save();
                    float fWidth = this.bounds.width() * 0.315f;
                    RectF rectF4 = AndroidUtilities.rectTmp;
                    float f = fWidth / 2.0f;
                    rectF4.set(this.bounds.centerX() - f, this.bounds.centerY() - f, this.bounds.centerX() + f, this.bounds.centerY() + f);
                    canvas.rotate(45.0f, this.bounds.centerX(), this.bounds.centerY());
                    canvas.drawRoundRect(rectF4, AndroidUtilities.dp(2.33f), AndroidUtilities.dp(2.33f), this.paint3);
                    canvas.restore();
                }
                float f2 = this.selectedT.set(this.selected);
                if (f2 > 0.0f) {
                    PeerColorGrid.this.backgroundPaint.setStrokeWidth(AndroidUtilities.dpf2(2.0f));
                    PeerColorGrid.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite, PeerColorGrid.this.resourcesProvider));
                    canvas.drawCircle(this.bounds.centerX(), this.bounds.centerY(), Math.min(this.bounds.height() / 2.0f, this.bounds.width() / 2.0f) + (PeerColorGrid.this.backgroundPaint.getStrokeWidth() * AndroidUtilities.lerp(0.5f, -2.0f, f2)), PeerColorGrid.this.backgroundPaint);
                }
                if (this.hasClose) {
                    if (PeerColorGrid.this.lock) {
                        if (this.lockDrawable == null) {
                            Drawable drawable = PeerColorGrid.this.getContext().getResources().getDrawable(R.drawable.msg_mini_lock3);
                            this.lockDrawable = drawable;
                            drawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
                        }
                        this.lockDrawable.setBounds((int) (this.bounds.centerX() - ((this.lockDrawable.getIntrinsicWidth() / 2.0f) * 1.2f)), (int) (this.bounds.centerY() - ((this.lockDrawable.getIntrinsicHeight() / 2.0f) * 1.2f)), (int) (this.bounds.centerX() + ((this.lockDrawable.getIntrinsicWidth() / 2.0f) * 1.2f)), (int) (this.bounds.centerY() + ((this.lockDrawable.getIntrinsicHeight() / 2.0f) * 1.2f)));
                        this.lockDrawable.draw(canvas);
                    } else {
                        if (this.closePath == null) {
                            this.closePath = new Path();
                        }
                        if (this.closePaint == null) {
                            Paint paint = new Paint(1);
                            this.closePaint = paint;
                            paint.setColor(Theme.isCurrentThemeMonet() ? Theme.key_chats_actionIcon : -1);
                            this.closePaint.setStyle(Paint.Style.STROKE);
                            this.closePaint.setStrokeCap(Paint.Cap.ROUND);
                        }
                        this.closePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
                        this.closePath.rewind();
                        float fLerp = AndroidUtilities.lerp(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(4.0f), f2);
                        this.closePath.moveTo(this.bounds.centerX() - fLerp, this.bounds.centerY() - fLerp);
                        this.closePath.lineTo(this.bounds.centerX() + fLerp, this.bounds.centerY() + fLerp);
                        this.closePath.moveTo(this.bounds.centerX() + fLerp, this.bounds.centerY() - fLerp);
                        this.closePath.lineTo(this.bounds.centerX() - fLerp, this.bounds.centerY() + fLerp);
                        canvas.drawPath(this.closePath, this.closePaint);
                    }
                }
                canvas.restore();
            }

            public void setPressed(boolean z) {
                ButtonBounce buttonBounce = this.bounce;
                this.pressed = z;
                buttonBounce.setPressed(z);
            }
        }

        public PeerColorGrid(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.order = new int[]{5, 3, 1, 0, 2, 4, 6, -1};
            this.dividerPaint = new Paint(1);
            this.needDivider = true;
            this.selectedColorId = 0;
            this.type = i;
            this.currentAccount = i2;
            this.resourcesProvider = resourcesProvider;
        }

        public void setCloseAsLock(boolean z) {
            this.lock = z;
        }

        public void updateColors() {
            int i;
            if (this.buttons == null) {
                return;
            }
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            MessagesController.PeerColors peerColors = this.type == 1 ? messagesController.peerColors : messagesController.profilePeerColors;
            int i2 = 0;
            while (true) {
                ColorButton[] colorButtonArr = this.buttons;
                if (i2 < colorButtonArr.length) {
                    int i3 = this.type;
                    if (i3 == 2) {
                        ColorButton colorButton = colorButtonArr[i2];
                        int i4 = this.order[i2];
                        colorButton.id = i4;
                        colorButton.setClose(i4 < 0);
                        ColorButton colorButton2 = this.buttons[i2];
                        int i5 = this.order[i2];
                        if (i5 < 0) {
                            i = Theme.key_avatar_backgroundGray;
                        } else {
                            int[] iArr = Theme.keys_avatar_nameInMessage;
                            i = iArr[i5 % iArr.length];
                        }
                        colorButton2.set(Theme.getColor(i, this.resourcesProvider));
                    } else if (i2 < 7 && i3 == 1) {
                        ColorButton colorButton3 = colorButtonArr[i2];
                        int i6 = this.order[i2];
                        colorButton3.id = i6;
                        colorButton3.set(Theme.getColor(Theme.keys_avatar_nameInMessage[i6], this.resourcesProvider));
                    } else if (peerColors != null && i2 >= 0 && i2 < peerColors.colors.size()) {
                        this.buttons[i2].id = peerColors.colors.get(i2).id;
                        this.buttons[i2].set(peerColors.colors.get(i2));
                    }
                    i2++;
                } else {
                    invalidate();
                    return;
                }
            }
        }

        @Override // android.view.View
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            boolean z = true;
            MessagesController.PeerColors peerColors = this.type == 1 ? messagesController.peerColors : messagesController.profilePeerColors;
            int size2 = peerColors == null ? 0 : peerColors.colors.size();
            int i4 = this.type;
            int i5 = 8;
            int i6 = 2;
            if (i4 == 2) {
                size2 = 8;
            }
            if (i4 != 2 && i4 == 1) {
                i5 = 7;
            }
            float f = size;
            float f2 = i5;
            float f3 = i5 + 1;
            float fMin = Math.min(AndroidUtilities.dp(54.0f), f / ((f3 * 0.28947f) + f2));
            float fMin2 = Math.min(0.28947f * fMin, AndroidUtilities.dp(8.0f));
            float fMin3 = Math.min(0.31578946f * fMin, AndroidUtilities.dp(11.33f));
            int i7 = size2 / i5;
            setMeasuredDimension(size, (int) ((i7 * fMin) + ((i7 + 1) * fMin3)));
            ColorButton[] colorButtonArr = this.buttons;
            if (colorButtonArr == null || colorButtonArr.length != size2) {
                this.buttons = new ColorButton[size2];
                int i8 = 0;
                while (i8 < size2) {
                    this.buttons[i8] = new ColorButton();
                    if (this.type == i6) {
                        ColorButton colorButton = this.buttons[i8];
                        int i9 = this.order[i8];
                        colorButton.id = i9;
                        colorButton.setClose(i9 < 0 ? z : false);
                        ColorButton colorButton2 = this.buttons[i8];
                        int i10 = this.order[i8];
                        if (i10 < 0) {
                            i3 = Theme.key_avatar_backgroundGray;
                        } else {
                            int[] iArr = Theme.keys_avatar_nameInMessage;
                            i3 = iArr[i10 % iArr.length];
                        }
                        colorButton2.set(Theme.getColor(i3, this.resourcesProvider));
                    } else {
                        z = z;
                        if (peerColors != null && i8 >= 0 && i8 < peerColors.colors.size()) {
                            this.buttons[i8].id = peerColors.colors.get(i8).id;
                            this.buttons[i8].set(peerColors.colors.get(i8));
                        }
                    }
                    i8++;
                    z = z;
                    i6 = 2;
                }
            }
            boolean z2 = z;
            float f4 = ((f - ((f2 * fMin) + (f3 * fMin2))) / 2.0f) + fMin2;
            if (this.buttons != null) {
                float f5 = f4;
                float f6 = fMin3;
                for (int i11 = 0; i11 < this.buttons.length; i11++) {
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(f5, f6, f5 + fMin, f6 + fMin);
                    this.buttons[i11].layout(rectF);
                    rectF.inset((-fMin2) / 2.0f, (-fMin3) / 2.0f);
                    this.buttons[i11].layoutClickBounds(rectF);
                    ColorButton colorButton3 = this.buttons[i11];
                    colorButton3.setSelected(colorButton3.id == this.selectedColorId ? z2 : false, false);
                    if (i11 % i5 == i5 - 1) {
                        f6 += fMin + fMin3;
                        f5 = f4;
                    } else {
                        f5 += fMin + fMin2;
                    }
                }
            }
        }

        public void setDivider(boolean z) {
            this.needDivider = z;
            invalidate();
        }

        @Override // android.view.View
        public void dispatchDraw(Canvas canvas) {
            if (this.buttons != null) {
                int i = 0;
                while (true) {
                    ColorButton[] colorButtonArr = this.buttons;
                    if (i >= colorButtonArr.length) {
                        break;
                    }
                    colorButtonArr[i].draw(canvas);
                    i++;
                }
            }
            if (this.needDivider) {
                this.dividerPaint.setColor(Theme.getColor(Theme.key_divider, this.resourcesProvider));
                canvas.drawRect(AndroidUtilities.dp(21.0f), getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(21.0f), getMeasuredHeight(), this.dividerPaint);
            }
        }

        public void setSelected(int i, boolean z) {
            this.selectedColorId = i;
            if (this.buttons == null) {
                return;
            }
            int i2 = 0;
            while (true) {
                ColorButton[] colorButtonArr = this.buttons;
                if (i2 >= colorButtonArr.length) {
                    return;
                }
                ColorButton colorButton = colorButtonArr[i2];
                colorButton.setSelected(colorButton.id == i, z);
                i2++;
            }
        }

        public int getColorId() {
            return this.selectedColorId;
        }

        public void setOnColorClick(Utilities.Callback<Integer> callback) {
            this.onColorClick = callback;
        }

        @Override // android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            ColorButton colorButton;
            ColorButton colorButton2;
            Utilities.Callback<Integer> callback;
            Utilities.Callback<Integer> callback2;
            if (this.buttons == null) {
                colorButton = null;
                break;
            }
            int i = 0;
            while (true) {
                ColorButton[] colorButtonArr = this.buttons;
                if (i >= colorButtonArr.length) {
                    colorButton = null;
                    break;
                }
                if (colorButtonArr[i].clickBounds.contains(motionEvent.getX(), motionEvent.getY())) {
                    colorButton = this.buttons[i];
                    break;
                }
                i++;
            }
            if (motionEvent.getAction() == 0) {
                this.pressedButton = colorButton;
                if (colorButton != null) {
                    colorButton.setPressed(true);
                }
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            } else if (motionEvent.getAction() == 2) {
                ColorButton colorButton3 = this.pressedButton;
                if (colorButton3 != colorButton) {
                    if (colorButton3 != null) {
                        colorButton3.setPressed(false);
                    }
                    if (colorButton != null) {
                        colorButton.setPressed(true);
                    }
                    if (this.pressedButton != null && colorButton != null && (callback2 = this.onColorClick) != null) {
                        callback2.run(Integer.valueOf(colorButton.id));
                    }
                    this.pressedButton = colorButton;
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (motionEvent.getAction() == 1 && (colorButton2 = this.pressedButton) != null && (callback = this.onColorClick) != null) {
                    callback.run(Integer.valueOf(colorButton2.id));
                }
                if (this.buttons != null) {
                    int i2 = 0;
                    while (true) {
                        ColorButton[] colorButtonArr2 = this.buttons;
                        if (i2 >= colorButtonArr2.length) {
                            break;
                        }
                        colorButtonArr2[i2].setPressed(false);
                        i2++;
                    }
                }
                this.pressedButton = null;
            }
            return true;
        }
    }

    public static class PeerColorSpan extends ReplacementSpan {
        public PeerColorDrawable drawable;
        private int size = AndroidUtilities.dp(21.0f);

        public PeerColorSpan(boolean z, int i, int i2) {
            this.drawable = z ? PeerColorDrawable.fromProfile(i, i2) : PeerColorDrawable.from(i, i2);
        }

        public PeerColorSpan setSize(int i) {
            PeerColorDrawable peerColorDrawable = this.drawable;
            if (peerColorDrawable != null) {
                peerColorDrawable.setRadius(i / 2.0f);
                this.size = i;
            }
            return this;
        }

        @Override // android.text.style.ReplacementSpan
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            return AndroidUtilities.dp(3.0f) + this.size + AndroidUtilities.dp(3.0f);
        }

        @Override // android.text.style.ReplacementSpan
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            PeerColorDrawable peerColorDrawable = this.drawable;
            if (peerColorDrawable != null) {
                int i6 = (i3 + i5) / 2;
                int iDp = (int) (AndroidUtilities.dp(3.0f) + f);
                int i7 = i6 - this.size;
                float fDp = f + AndroidUtilities.dp(5.0f);
                int i8 = this.size;
                peerColorDrawable.setBounds(iDp, i7, (int) (fDp + i8), i6 + i8);
                this.drawable.draw(canvas);
            }
        }
    }

    public static class PeerColorDrawable extends Drawable {
        private final Path clipCirclePath;
        private final Paint color1Paint;
        private final Paint color2Paint;
        private final Path color2Path;
        private final Paint color3Paint;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emoji;
        private final boolean hasColor3;
        private float radius = AndroidUtilities.dpf2(10.6665f);
        private Paint strokePaint;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public static PeerColorDrawable from(TLRPC.TL_peerColorCollectible tL_peerColorCollectible) {
            ArrayList<Integer> arrayList;
            if (!Theme.isCurrentThemeDark() || (arrayList = tL_peerColorCollectible.dark_colors) == null) {
                arrayList = tL_peerColorCollectible.colors;
            }
            if (arrayList == null || arrayList.isEmpty()) {
                return null;
            }
            int iIntValue = arrayList.get(0).intValue() | (-16777216);
            return new PeerColorDrawable(iIntValue, arrayList.size() >= 2 ? arrayList.get(1).intValue() | (-16777216) : iIntValue, arrayList.size() >= 3 ? arrayList.get(2).intValue() | (-16777216) : iIntValue, tL_peerColorCollectible.gift_emoji_id);
        }

        public static PeerColorDrawable from(TLRPC.TL_emojiStatusCollectible tL_emojiStatusCollectible) {
            int i = tL_emojiStatusCollectible.center_color | (-16777216);
            return new PeerColorDrawable(i, i, i, tL_emojiStatusCollectible.document_id);
        }

        public static PeerColorDrawable from(int i, int i2) {
            if (i2 < 7) {
                return new PeerColorDrawable(Theme.getColor(Theme.keys_avatar_nameInMessage[i2]), Theme.getColor(Theme.keys_avatar_nameInMessage[i2]), Theme.getColor(Theme.keys_avatar_nameInMessage[i2]));
            }
            MessagesController.PeerColors peerColors = MessagesController.getInstance(i).peerColors;
            return from(peerColors == null ? null : peerColors.getColor(i2), false);
        }

        public static PeerColorDrawable fromProfile(int i, int i2) {
            MessagesController.PeerColors peerColors = MessagesController.getInstance(i).profilePeerColors;
            return from(peerColors == null ? null : peerColors.getColor(i2), true);
        }

        public static PeerColorDrawable from(MessagesController.PeerColor peerColor, boolean z) {
            if (peerColor == null) {
                return new PeerColorDrawable(0, 0, 0);
            }
            return new PeerColorDrawable(peerColor.getColor1(), (!z || peerColor.hasColor6(Theme.isCurrentThemeDark())) ? peerColor.getColor2() : peerColor.getColor1(), z ? peerColor.getColor1() : peerColor.getColor3());
        }

        public PeerColorDrawable setRadius(float f) {
            this.radius = f;
            initPath();
            return this;
        }

        public PeerColorDrawable stroke(float f, int i) {
            if (this.strokePaint == null) {
                Paint paint = new Paint(1);
                this.strokePaint = paint;
                paint.setStyle(Paint.Style.STROKE);
            }
            this.strokePaint.setStrokeWidth(f);
            this.strokePaint.setColor(i);
            return this;
        }

        public PeerColorDrawable(int i, int i2, int i3) {
            Paint paint = new Paint(1);
            this.color1Paint = paint;
            Paint paint2 = new Paint(1);
            this.color2Paint = paint2;
            Paint paint3 = new Paint(1);
            this.color3Paint = paint3;
            this.color2Path = new Path();
            this.clipCirclePath = new Path();
            this.hasColor3 = i3 != i;
            paint.setColor(i);
            paint2.setColor(i2);
            paint3.setColor(i3);
            this.emoji = null;
            initPath();
        }

        public PeerColorDrawable(int i, int i2, int i3, long j) {
            Paint paint = new Paint(1);
            this.color1Paint = paint;
            Paint paint2 = new Paint(1);
            this.color2Paint = paint2;
            Paint paint3 = new Paint(1);
            this.color3Paint = paint3;
            this.color2Path = new Path();
            this.clipCirclePath = new Path();
            this.hasColor3 = i3 != i;
            paint.setColor(i);
            paint2.setColor(i2);
            paint3.setColor(i3);
            initPath();
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(null, AndroidUtilities.dp(14.0f));
            this.emoji = swapAnimatedEmojiDrawable;
            swapAnimatedEmojiDrawable.set(j, false);
        }

        public PeerColorDrawable setView(View view) {
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emoji;
            if (view == null) {
                if (swapAnimatedEmojiDrawable != null) {
                    swapAnimatedEmojiDrawable.detach();
                    this.emoji.setParentView(null);
                }
                return this;
            }
            if (swapAnimatedEmojiDrawable != null) {
                swapAnimatedEmojiDrawable.setParentView(view);
            }
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: org.telegram.ui.PeerColorActivity.PeerColorDrawable.1
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View view2) {
                    if (PeerColorDrawable.this.emoji != null) {
                        PeerColorDrawable.this.emoji.attach();
                    }
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View view2) {
                    if (PeerColorDrawable.this.emoji != null) {
                        PeerColorDrawable.this.emoji.detach();
                    }
                }
            });
            return this;
        }

        private void initPath() {
            this.clipCirclePath.rewind();
            Path path = this.clipCirclePath;
            float f = this.radius;
            path.addCircle(f, f, f, Path.Direction.CW);
            this.color2Path.rewind();
            this.color2Path.moveTo(this.radius * 2.0f, 0.0f);
            Path path2 = this.color2Path;
            float f2 = this.radius;
            path2.lineTo(f2 * 2.0f, f2 * 2.0f);
            this.color2Path.lineTo(0.0f, this.radius * 2.0f);
            this.color2Path.close();
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.translate(getBounds().centerX() - this.radius, getBounds().centerY() - this.radius);
            Paint paint = this.strokePaint;
            if (paint != null) {
                float f = this.radius;
                canvas.drawCircle(f, f, f, paint);
            }
            canvas.clipPath(this.clipCirclePath);
            canvas.drawPaint(this.color1Paint);
            canvas.drawPath(this.color2Path, this.color2Paint);
            if (this.hasColor3) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(this.radius - AndroidUtilities.dp(3.66f), this.radius - AndroidUtilities.dp(3.66f), this.radius + AndroidUtilities.dp(3.66f), this.radius + AndroidUtilities.dp(3.66f));
                float f2 = this.radius;
                canvas.rotate(45.0f, f2, f2);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(2.33f), AndroidUtilities.dp(2.33f), this.color3Paint);
            }
            canvas.restore();
            if (this.emoji != null) {
                int iDp = AndroidUtilities.dp(14.0f) / 2;
                this.emoji.setBounds(getBounds().centerX() - iDp, getBounds().centerY() - iDp, getBounds().centerX() + iDp, getBounds().centerY() + iDp);
                this.emoji.draw(canvas);
            }
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return (int) (this.radius * 2.0f);
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return (int) (this.radius * 2.0f);
        }
    }

    public static class ColoredActionBar extends View {
        private RadialGradient backgroundGradient;
        private int backgroundGradientColor1;
        private int backgroundGradientColor2;
        private int backgroundGradientHeight;
        private int backgroundGradientWidth;
        private final Paint backgroundPaint;
        public int color1;
        private final AnimatedColor color1Animated;
        public int color2;
        private final AnimatedColor color2Animated;
        private int defaultColor;
        protected boolean ignoreMeasure;
        public boolean isDefault;
        private float progressToGradient;
        private final Theme.ResourcesProvider resourcesProvider;

        public void onUpdateColor() {
        }

        public ColoredActionBar(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.progressToGradient = 0.0f;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.color1Animated = new AnimatedColor(this, 350L, cubicBezierInterpolator);
            this.color2Animated = new AnimatedColor(this, 350L, cubicBezierInterpolator);
            this.backgroundPaint = new Paint(1);
            this.resourcesProvider = resourcesProvider;
            this.defaultColor = Theme.getColor(Theme.key_actionBarDefault, resourcesProvider);
            setColor(-1, -1, false);
        }

        public void setColor(int i, int i2, boolean z) {
            MessagesController.PeerColors peerColors;
            MessagesController.PeerColor color = null;
            if (i2 >= 0 && i >= 0 && (peerColors = MessagesController.getInstance(i).profilePeerColors) != null) {
                color = peerColors.getColor(i2);
            }
            setColor(color, z);
        }

        public void setColor(MessagesController.PeerColor peerColor, boolean z) {
            this.isDefault = false;
            if (peerColor == null) {
                this.isDefault = true;
                int color = Theme.getColor(Theme.key_actionBarDefault, this.resourcesProvider);
                this.color2 = color;
                this.color1 = color;
            } else {
                Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
                boolean zIsDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
                this.color1 = peerColor.getBgColor1(zIsDark);
                this.color2 = peerColor.getBgColor2(zIsDark);
            }
            if (!z) {
                this.color1Animated.set(this.color1, true);
                this.color2Animated.set(this.color2, true);
            }
            invalidate();
        }

        public void setProgressToGradient(float f) {
            if (Math.abs(this.progressToGradient - f) > 0.001f) {
                this.progressToGradient = f;
                onUpdateColor();
                invalidate();
            }
        }

        @Override // android.view.View
        public void dispatchDraw(Canvas canvas) {
            int i = this.color1Animated.set(this.color1);
            int i2 = this.color2Animated.set(this.color2);
            if (this.backgroundGradient == null || this.backgroundGradientColor1 != i || this.backgroundGradientColor2 != i2 || this.backgroundGradientWidth != getWidth() || this.backgroundGradientHeight != getHeight()) {
                this.backgroundGradientWidth = getWidth();
                this.backgroundGradientHeight = getHeight();
                int i3 = this.backgroundGradientWidth;
                int i4 = this.backgroundGradientHeight;
                float fDistance = AndroidUtilities.distance(0.0f, 0.0f, i3, i4) * 0.75f;
                this.backgroundGradientColor2 = i2;
                this.backgroundGradientColor1 = i;
                RadialGradient radialGradient = new RadialGradient(i3 / 2.0f, i4 * 0.4f, fDistance, new int[]{i2, i}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                this.backgroundGradient = radialGradient;
                this.backgroundPaint.setShader(radialGradient);
                onUpdateColor();
            }
            if (this.progressToGradient < 1.0f) {
                canvas.drawColor(this.defaultColor);
            }
            float f = this.progressToGradient;
            if (f > 0.0f) {
                this.backgroundPaint.setAlpha((int) (f * 255.0f));
                canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), this.backgroundPaint);
            }
        }

        @Override // android.view.View
        public void onMeasure(int i, int i2) {
            if (!this.ignoreMeasure) {
                i2 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.statusBarHeight + AndroidUtilities.dp(230.0f), TLObject.FLAG_30);
            }
            super.onMeasure(i, i2);
        }

        public void updateColors() {
            this.defaultColor = Theme.getColor(Theme.key_actionBarDefault, this.resourcesProvider);
            onUpdateColor();
            invalidate();
        }

        public int getColor() {
            return ColorUtils.blendARGB(Theme.getColor(Theme.key_actionBarDefault, this.resourcesProvider), ColorUtils.blendARGB(this.color1Animated.get(), this.color2Animated.get(), 0.75f), this.progressToGradient);
        }

        public int getActionBarButtonColor() {
            int i = Theme.key_actionBarDefaultIcon;
            return ColorUtils.blendARGB(Theme.getColor(i, this.resourcesProvider), this.isDefault ? Theme.getColor(i, this.resourcesProvider) : -1, this.progressToGradient);
        }

        public int getTabsViewBackgroundColor() {
            int iAdaptHSV;
            int iAdaptHSV2;
            int i = Theme.key_actionBarDefault;
            if (AndroidUtilities.computePerceivedBrightness(Theme.getColor(i, this.resourcesProvider)) > 0.721f) {
                iAdaptHSV = Theme.getColor(Theme.key_actionBarDefaultIcon, this.resourcesProvider);
            } else {
                iAdaptHSV = Theme.adaptHSV(Theme.getColor(i, this.resourcesProvider), 0.08f, -0.08f);
            }
            if (AndroidUtilities.computePerceivedBrightness(ColorUtils.blendARGB(this.color1Animated.get(), this.color2Animated.get(), 0.75f)) > 0.721f) {
                iAdaptHSV2 = Theme.getColor(Theme.key_windowBackgroundWhiteBlueIcon, this.resourcesProvider);
            } else {
                iAdaptHSV2 = Theme.adaptHSV(ColorUtils.blendARGB(this.color1Animated.get(), this.color2Animated.get(), 0.75f), 0.08f, -0.08f);
            }
            return ColorUtils.blendARGB(iAdaptHSV, iAdaptHSV2, this.progressToGradient);
        }
    }

    public static class ProfilePreview extends FrameLayout {
        protected final AvatarDrawable avatarDrawable;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable botVerificationEmoji;
        private final int currentAccount;
        private final long dialogId;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emoji;
        private final AnimatedFloat emojiCollectible;
        protected final ImageReceiver imageReceiver;
        private final boolean isChannel;
        private boolean isEmojiCollectible;
        private boolean isForum;
        private MessagesController.PeerColor peerColor;
        private final RectF rectF;
        private final Theme.ResourcesProvider resourcesProvider;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable statusEmoji;
        private final StoriesUtilities.StoryGradientTools storyGradient;
        protected final SimpleTextView subtitleView;
        protected final SimpleTextView titleView;

        public ProfilePreview(Context context, int i, long j, Theme.ResourcesProvider resourcesProvider) {
            CharSequence userName;
            long botVerificationIcon;
            super(context);
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.imageReceiver = imageReceiver;
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            this.avatarDrawable = avatarDrawable;
            this.emoji = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, false, AndroidUtilities.dp(20.0f), 13);
            this.storyGradient = new StoriesUtilities.StoryGradientTools((View) this, false);
            this.emojiCollectible = new AnimatedFloat(this, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.rectF = new RectF();
            this.currentAccount = i;
            this.dialogId = j;
            this.resourcesProvider = resourcesProvider;
            long emojiStatusDocumentId = 0;
            boolean z = j < 0;
            this.isChannel = z;
            SimpleTextView simpleTextView = new SimpleTextView(context) { // from class: org.telegram.ui.PeerColorActivity.ProfilePreview.1
                @Override // org.telegram.ui.ActionBar.SimpleTextView, android.view.View
                public void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    ProfilePreview.this.statusEmoji.attach();
                }

                @Override // org.telegram.ui.ActionBar.SimpleTextView, android.view.View
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    ProfilePreview.this.statusEmoji.detach();
                }
            };
            this.titleView = simpleTextView;
            this.botVerificationEmoji = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(simpleTextView, AndroidUtilities.dp(17.0f));
            this.statusEmoji = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(simpleTextView, AndroidUtilities.dp(24.0f));
            simpleTextView.setLeftDrawableOutside(true);
            simpleTextView.setRightDrawableOutside(true);
            simpleTextView.setTextColor(-1);
            simpleTextView.setTextSize(20);
            simpleTextView.setTypeface(AndroidUtilities.bold());
            simpleTextView.setWidthWrapContent(true);
            addView(simpleTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 16.0f, 0.0f, 16.0f, 40.33f));
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.subtitleView = simpleTextView2;
            simpleTextView2.setTextSize(14);
            simpleTextView2.setTextColor(-2130706433);
            simpleTextView2.setGravity(1);
            addView(simpleTextView2, LayoutHelper.createFrame(-2, -2.0f, 81, 16.0f, 0.0f, 16.0f, 20.66f));
            imageReceiver.setRoundRadius(AndroidUtilities.dp(96.0f));
            if (z) {
                TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(-j));
                userName = chat == null ? _UrlKt.FRAGMENT_ENCODE_SET : chat.title;
                avatarDrawable.setInfo(i, chat);
                imageReceiver.setForUserOrChat(chat, avatarDrawable);
                botVerificationIcon = DialogObject.getBotVerificationIcon(chat);
                if (chat != null) {
                    emojiStatusDocumentId = DialogObject.getEmojiStatusDocumentId(chat.emoji_status);
                }
            } else {
                TLRPC.User currentUser = UserConfig.getInstance(i).getCurrentUser();
                userName = UserObject.getUserName(currentUser);
                avatarDrawable.setInfo(i, currentUser);
                imageReceiver.setForUserOrChat(currentUser, avatarDrawable);
                botVerificationIcon = DialogObject.getBotVerificationIcon(currentUser);
                if (currentUser != null) {
                    emojiStatusDocumentId = DialogObject.getEmojiStatusDocumentId(currentUser.emoji_status);
                }
            }
            try {
                userName = Emoji.replaceEmoji(userName, null, false);
            } catch (Exception unused) {
            }
            this.titleView.setText(userName);
            this.botVerificationEmoji.set(botVerificationIcon, false);
            this.titleView.setLeftDrawable(this.botVerificationEmoji);
            this.statusEmoji.set(emojiStatusDocumentId, false);
            this.titleView.setRightDrawable(this.statusEmoji);
            if (this.isChannel) {
                long j2 = -j;
                TLRPC.Chat chat2 = MessagesController.getInstance(i).getChat(Long.valueOf(j2));
                TLRPC.ChatFull chatFull = MessagesController.getInstance(i).getChatFull(j2);
                if (chatFull != null && chatFull.participants_count > 0) {
                    boolean zIsChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat2);
                    SimpleTextView simpleTextView3 = this.subtitleView;
                    if (!zIsChannelAndNotMegaGroup) {
                        simpleTextView3.setText(LocaleController.formatPluralStringComma("Members", chatFull.participants_count));
                    } else {
                        simpleTextView3.setText(LocaleController.formatPluralStringComma("Subscribers", chatFull.participants_count));
                    }
                } else if (chat2 != null && chat2.participants_count > 0) {
                    boolean zIsChannelAndNotMegaGroup2 = ChatObject.isChannelAndNotMegaGroup(chat2);
                    SimpleTextView simpleTextView4 = this.subtitleView;
                    if (!zIsChannelAndNotMegaGroup2) {
                        simpleTextView4.setText(LocaleController.formatPluralStringComma("Members", chat2.participants_count));
                    } else {
                        simpleTextView4.setText(LocaleController.formatPluralStringComma("Subscribers", chat2.participants_count));
                    }
                } else {
                    boolean zIsPublic = ChatObject.isPublic(chat2);
                    boolean zIsChannelAndNotMegaGroup3 = ChatObject.isChannelAndNotMegaGroup(chat2);
                    SimpleTextView simpleTextView5 = this.subtitleView;
                    if (zIsChannelAndNotMegaGroup3) {
                        simpleTextView5.setText(LocaleController.getString(zIsPublic ? R.string.ChannelPublic : R.string.ChannelPrivate).toLowerCase());
                    } else {
                        simpleTextView5.setText(LocaleController.getString(zIsPublic ? R.string.MegaPublic : R.string.MegaPrivate).toLowerCase());
                    }
                }
            } else {
                this.subtitleView.setText(LocaleController.getString(R.string.Online));
            }
            setWillNotDraw(false);
        }

        public void overrideAvatarColor(int i) {
            int themedColor;
            int themedColor2;
            if (i >= 14) {
                MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
                MessagesController.PeerColors peerColors = messagesController != null ? messagesController.peerColors : null;
                MessagesController.PeerColor color = peerColors != null ? peerColors.getColor(i) : null;
                if (color != null) {
                    int color1 = color.getColor1();
                    themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getPeerColorIndex(color1)]);
                    themedColor2 = getThemedColor(Theme.keys_avatar_background2[AvatarDrawable.getPeerColorIndex(color1)]);
                } else {
                    long j = i;
                    themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getColorIndex(j)]);
                    themedColor2 = getThemedColor(Theme.keys_avatar_background2[AvatarDrawable.getColorIndex(j)]);
                }
            } else {
                long j2 = i;
                themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getColorIndex(j2)]);
                themedColor2 = getThemedColor(Theme.keys_avatar_background2[AvatarDrawable.getColorIndex(j2)]);
            }
            this.avatarDrawable.setColor(themedColor, themedColor2);
            invalidate();
        }

        public void setForum(boolean z) {
            if (this.isForum != z) {
                invalidate();
            }
            this.isForum = z;
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.emoji.attach();
            this.imageReceiver.onAttachedToWindow();
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.emoji.detach();
            this.imageReceiver.onDetachedFromWindow();
        }

        private int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        public void setColor(int i, boolean z) {
            MessagesController.PeerColors peerColors = MessagesController.getInstance(this.currentAccount).profilePeerColors;
            setColor(peerColors == null ? null : peerColors.getColor(i), z);
        }

        public void setColor(MessagesController.PeerColor peerColor, boolean z) {
            this.peerColor = peerColor;
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            boolean zIsDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
            if (peerColor != null) {
                int i = peerColor.patternColor;
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emoji;
                if (i != 0) {
                    swapAnimatedEmojiDrawable.setColor(Integer.valueOf(i));
                } else {
                    swapAnimatedEmojiDrawable.setColor(Integer.valueOf(PeerColorActivity.adaptProfileEmojiColor(peerColor.getBgColor1(zIsDark))));
                }
                this.statusEmoji.setColor(Integer.valueOf(ColorUtils.blendARGB(peerColor.getStoryColor1(Theme.isCurrentThemeDark()), -1, 0.25f)));
                this.botVerificationEmoji.setColor(Integer.valueOf(ColorUtils.blendARGB(peerColor.getStoryColor1(Theme.isCurrentThemeDark()), -1, 0.25f)));
                int iBlendARGB = ColorUtils.blendARGB(peerColor.getStoryColor1(zIsDark), peerColor.getStoryColor2(zIsDark), 0.5f);
                int i2 = Theme.key_actionBarDefault;
                boolean zHasHue = Theme.hasHue(getThemedColor(i2));
                SimpleTextView simpleTextView = this.subtitleView;
                if (!zHasHue) {
                    simpleTextView.setTextColor(iBlendARGB);
                } else {
                    simpleTextView.setTextColor(Theme.changeColorAccent(getThemedColor(i2), iBlendARGB, getThemedColor(Theme.key_avatar_subtitleInProfileBlue), zIsDark, iBlendARGB));
                }
                this.titleView.setTextColor(-1);
            } else {
                int i3 = Theme.key_actionBarDefault;
                if (AndroidUtilities.computePerceivedBrightness(getThemedColor(i3)) > 0.8f) {
                    this.emoji.setColor(Integer.valueOf(getThemedColor(Theme.key_windowBackgroundWhiteBlueText)));
                } else {
                    float fComputePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(getThemedColor(i3));
                    AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable2 = this.emoji;
                    if (fComputePerceivedBrightness < 0.2f) {
                        swapAnimatedEmojiDrawable2.setColor(Integer.valueOf(Theme.multAlpha(getThemedColor(Theme.key_actionBarDefaultTitle), 0.5f)));
                    } else {
                        swapAnimatedEmojiDrawable2.setColor(Integer.valueOf(PeerColorActivity.adaptProfileEmojiColor(getThemedColor(i3))));
                    }
                }
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable3 = this.statusEmoji;
                int i4 = Theme.key_profile_verifiedBackground;
                swapAnimatedEmojiDrawable3.setColor(Integer.valueOf(Theme.getColor(i4, this.resourcesProvider)));
                this.botVerificationEmoji.setColor(Integer.valueOf(Theme.getColor(i4, this.resourcesProvider)));
                this.subtitleView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubtitle));
                this.titleView.setTextColor(getThemedColor(Theme.key_actionBarDefaultTitle));
            }
            this.storyGradient.setColor(peerColor, z);
            invalidate();
        }

        public void setEmoji(long j, boolean z, boolean z2) {
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.emoji;
            if (j == 0) {
                swapAnimatedEmojiDrawable.set((Drawable) null, z2);
            } else {
                swapAnimatedEmojiDrawable.set(j, z2);
            }
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            boolean zIsDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
            MessagesController.PeerColor peerColor = this.peerColor;
            if (peerColor != null) {
                int i = peerColor.patternColor;
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable2 = this.emoji;
                if (i != 0) {
                    swapAnimatedEmojiDrawable2.setColor(Integer.valueOf(i));
                } else {
                    swapAnimatedEmojiDrawable2.setColor(Integer.valueOf(PeerColorActivity.adaptProfileEmojiColor(peerColor.getBgColor1(zIsDark))));
                }
            } else {
                int i2 = Theme.key_actionBarDefault;
                if (AndroidUtilities.computePerceivedBrightness(getThemedColor(i2)) > 0.8f) {
                    this.emoji.setColor(Integer.valueOf(getThemedColor(Theme.key_windowBackgroundWhiteBlueText)));
                } else {
                    float fComputePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(getThemedColor(i2));
                    AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable3 = this.emoji;
                    if (fComputePerceivedBrightness < 0.2f) {
                        swapAnimatedEmojiDrawable3.setColor(Integer.valueOf(Theme.multAlpha(Theme.getColor(Theme.key_actionBarDefaultTitle), 0.5f)));
                    } else {
                        swapAnimatedEmojiDrawable3.setColor(Integer.valueOf(PeerColorActivity.adaptProfileEmojiColor(Theme.getColor(i2))));
                    }
                }
            }
            MessagesController.PeerColor peerColor2 = this.peerColor;
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable4 = this.statusEmoji;
            if (peerColor2 != null) {
                swapAnimatedEmojiDrawable4.setColor(Integer.valueOf(ColorUtils.blendARGB(peerColor2.getColor(1, this.resourcesProvider), this.peerColor.getColor(this.peerColor.hasColor6(zIsDark) ? 4 : 2, this.resourcesProvider), 0.5f)));
            } else {
                swapAnimatedEmojiDrawable4.setColor(Integer.valueOf(Theme.getColor(Theme.key_profile_verifiedBackground, this.resourcesProvider)));
            }
            this.isEmojiCollectible = z;
            if (!z2) {
                this.emojiCollectible.force(z);
            }
            invalidate();
        }

        public void setStatusEmoji(long j, boolean z, boolean z2) {
            this.statusEmoji.set(j, z2);
            this.statusEmoji.setParticles(z, z2);
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            boolean zIsDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
            MessagesController.PeerColor peerColor = this.peerColor;
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.statusEmoji;
            if (peerColor != null) {
                int color2 = peerColor.getColor2(zIsDark);
                boolean zHasColor6 = this.peerColor.hasColor6(zIsDark);
                MessagesController.PeerColor peerColor2 = this.peerColor;
                swapAnimatedEmojiDrawable.setColor(Integer.valueOf(ColorUtils.blendARGB(color2, zHasColor6 ? peerColor2.getColor5(zIsDark) : peerColor2.getColor3(zIsDark), 0.5f)));
                return;
            }
            swapAnimatedEmojiDrawable.setColor(Integer.valueOf(Theme.getColor(Theme.key_profile_verifiedBackground, this.resourcesProvider)));
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            this.rectF.set((getWidth() - AndroidUtilities.dp(86.0f)) / 2.0f, getHeight() - AndroidUtilities.dp(168.0f), (getWidth() + AndroidUtilities.dp(86.0f)) / 2.0f, getHeight() - AndroidUtilities.dp(82.0f));
            StarGiftPatterns.drawProfileAnimatedPattern(canvas, this.emoji, getWidth(), getHeight(), 1.0f, this.rectF, 1.0f);
            this.imageReceiver.setRoundRadius(ExteraConfig.getAvatarCorners(this.rectF.width(), true, this.isForum));
            this.imageReceiver.setImageCoords(this.rectF);
            this.imageReceiver.draw(canvas);
            float fWidth = (this.rectF.width() / 2.0f) + AndroidUtilities.dp(4.0f);
            float avatarCorners = ExteraConfig.getAvatarCorners(2.0f * fWidth, true, this.isForum);
            canvas.drawRoundRect(this.rectF.centerX() - fWidth, this.rectF.centerY() - fWidth, this.rectF.centerX() + fWidth, this.rectF.centerY() + fWidth, avatarCorners, avatarCorners, this.storyGradient.getPaint(this.rectF));
            super.dispatchDraw(canvas);
        }
    }

    public static int adaptProfileEmojiColor(int i) {
        return Theme.adaptHSV(i, 0.5f, (AndroidUtilities.computePerceivedBrightness(i) > 0.2f ? 1 : (AndroidUtilities.computePerceivedBrightness(i) == 0.2f ? 0 : -1)) < 0 ? 0.28f : -0.28f);
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void toggleTheme() {
        FrameLayout frameLayout = (FrameLayout) getParentActivity().getWindow().getDecorView();
        final Bitmap bitmapCreateBitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmapCreateBitmap);
        this.dayNightItem.setAlpha(0.0f);
        frameLayout.draw(canvas);
        this.dayNightItem.setAlpha(1.0f);
        final Paint paint = new Paint(1);
        paint.setColor(-16777216);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        final Paint paint2 = new Paint(1);
        paint2.setFilterBitmap(true);
        int[] iArr = new int[2];
        this.dayNightItem.getLocationInWindow(iArr);
        final float f = iArr[0];
        final float f2 = iArr[1];
        final float measuredWidth = f + (this.dayNightItem.getMeasuredWidth() / 2.0f);
        final float measuredHeight = f2 + (this.dayNightItem.getMeasuredHeight() / 2.0f);
        final float fMax = Math.max(bitmapCreateBitmap.getHeight(), bitmapCreateBitmap.getWidth()) + AndroidUtilities.navigationBarHeight;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        paint2.setShader(new BitmapShader(bitmapCreateBitmap, tileMode, tileMode));
        View view = new View(getContext()) { // from class: org.telegram.ui.PeerColorActivity.7
            @Override // android.view.View
            public void onDraw(Canvas canvas2) {
                super.onDraw(canvas2);
                if (PeerColorActivity.this.isDark) {
                    if (PeerColorActivity.this.changeDayNightViewProgress > 0.0f) {
                        canvas.drawCircle(measuredWidth, measuredHeight, fMax * PeerColorActivity.this.changeDayNightViewProgress, paint);
                    }
                    canvas2.drawBitmap(bitmapCreateBitmap, 0.0f, 0.0f, paint2);
                } else {
                    canvas2.drawCircle(measuredWidth, measuredHeight, fMax * (1.0f - PeerColorActivity.this.changeDayNightViewProgress), paint2);
                }
                canvas2.save();
                canvas2.translate(f, f2);
                PeerColorActivity.this.dayNightItem.draw(canvas2);
                canvas2.restore();
            }
        };
        this.changeDayNightView = view;
        view.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.PeerColorActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                return PeerColorActivity.$r8$lambda$jN3uxIhJrKqUC9ox4vQUHnPvrw0(view2, motionEvent);
            }
        });
        this.changeDayNightViewProgress = 0.0f;
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.changeDayNightViewAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PeerColorActivity.8
            boolean changedNavigationBarColor = false;

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PeerColorActivity.this.changeDayNightViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                PeerColorActivity.this.changeDayNightView.invalidate();
                if (this.changedNavigationBarColor || PeerColorActivity.this.changeDayNightViewProgress <= 0.5f) {
                    return;
                }
                this.changedNavigationBarColor = true;
            }
        });
        this.changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PeerColorActivity.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (PeerColorActivity.this.changeDayNightView != null) {
                    if (PeerColorActivity.this.changeDayNightView.getParent() != null) {
                        ((ViewGroup) PeerColorActivity.this.changeDayNightView.getParent()).removeView(PeerColorActivity.this.changeDayNightView);
                    }
                    PeerColorActivity.this.changeDayNightView = null;
                }
                PeerColorActivity.this.changeDayNightViewAnimator = null;
                super.onAnimationEnd(animator);
            }
        });
        this.changeDayNightViewAnimator.setDuration(400L);
        this.changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
        this.changeDayNightViewAnimator.start();
        frameLayout.addView(this.changeDayNightView, new ViewGroup.LayoutParams(-1, -1));
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PeerColorActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$toggleTheme$11();
            }
        });
    }

    public static /* synthetic */ boolean $r8$lambda$jN3uxIhJrKqUC9ox4vQUHnPvrw0(View view, MotionEvent motionEvent) {
        return true;
    }

    public /* synthetic */ void lambda$toggleTheme$11() {
        this.isDark = !this.isDark;
        updateThemeColors();
        setForceDark(this.isDark, true);
        updateColors();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        ColoredActionBar coloredActionBar = this.colorBar;
        if (coloredActionBar == null) {
            return super.isLightStatusBar();
        }
        return ColorUtils.calculateLuminance(coloredActionBar.getColor()) > 0.699999988079071d;
    }

    public void updateLightStatusBar() {
        if (getParentActivity() == null) {
            return;
        }
        AndroidUtilities.setLightStatusBar(getParentActivity(), isLightStatusBar());
    }

    public void setForceDark(boolean z, boolean z2) {
        if (this.forceDark == z) {
            return;
        }
        this.forceDark = z;
        if (z2) {
            RLottieDrawable rLottieDrawable = this.sunDrawable;
            rLottieDrawable.setCustomEndFrame(z ? rLottieDrawable.getFramesCount() : 0);
            RLottieDrawable rLottieDrawable2 = this.sunDrawable;
            if (rLottieDrawable2 != null) {
                rLottieDrawable2.start();
                return;
            }
            return;
        }
        int framesCount = z ? this.sunDrawable.getFramesCount() - 1 : 0;
        this.sunDrawable.setCurrentFrame(framesCount, false, true);
        this.sunDrawable.setCustomEndFrame(framesCount);
        ImageView imageView = this.dayNightItem;
        if (imageView != null) {
            imageView.invalidate();
        }
    }

    public static class GiftCell extends FrameLayout {
        public TL_stars.starGiftAttributeBackdrop backdrop;
        public final FrameLayout card;
        public final GiftSheet.CardBackground cardBackground;
        public long id;
        public final BackupImageView imageView;
        private TLRPC.Document lastDocument;
        private long lastDocumentId;
        public TL_stars.starGiftAttributePattern pattern;
        private final GiftSheet.Ribbon ribbon;

        public GiftCell(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            FrameLayout frameLayout = new FrameLayout(context);
            this.card = frameLayout;
            GiftSheet.CardBackground cardBackground = new GiftSheet.CardBackground(frameLayout, resourcesProvider, false);
            this.cardBackground = cardBackground;
            frameLayout.setBackground(cardBackground);
            addView(frameLayout, LayoutHelper.createFrame(-1, -1, 119));
            ScaleStateListAnimator.apply(frameLayout, 0.025f, 1.25f);
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(80, 80.0f, 17, 0.0f, 12.0f, 0.0f, 12.0f));
            if (z) {
                GiftSheet.Ribbon ribbon = new GiftSheet.Ribbon(context);
                this.ribbon = ribbon;
                addView(ribbon, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 2.0f, 1.0f, 0.0f));
                return;
            }
            this.ribbon = null;
        }

        public void set(int i, TL_stars.TL_starGiftUnique tL_starGiftUnique) {
            this.id = tL_starGiftUnique.id;
            boolean z = i % 3 == 1;
            setPadding(z ? AndroidUtilities.dp(4.0f) : 0, 0, z ? AndroidUtilities.dp(4.0f) : 0, 0);
            setSticker(tL_starGiftUnique.getDocument(), tL_starGiftUnique);
            this.backdrop = (TL_stars.starGiftAttributeBackdrop) StarsController.findAttribute(tL_starGiftUnique.attributes, TL_stars.starGiftAttributeBackdrop.class);
            this.pattern = (TL_stars.starGiftAttributePattern) StarsController.findAttribute(tL_starGiftUnique.attributes, TL_stars.starGiftAttributePattern.class);
            this.cardBackground.setBackdrop(this.backdrop);
            this.cardBackground.setPattern(this.pattern);
        }

        public void set(int i, TL_stars.SavedStarGift savedStarGift) {
            this.id = savedStarGift.gift.id;
            boolean z = i % 3 == 1;
            setPadding(z ? AndroidUtilities.dp(4.0f) : 0, 0, z ? AndroidUtilities.dp(4.0f) : 0, 0);
            setSticker(savedStarGift.gift.getDocument(), savedStarGift.gift);
            this.backdrop = (TL_stars.starGiftAttributeBackdrop) StarsController.findAttribute(savedStarGift.gift.attributes, TL_stars.starGiftAttributeBackdrop.class);
            this.pattern = (TL_stars.starGiftAttributePattern) StarsController.findAttribute(savedStarGift.gift.attributes, TL_stars.starGiftAttributePattern.class);
            this.cardBackground.setBackdrop(this.backdrop);
            this.cardBackground.setPattern(this.pattern);
            GiftSheet.Ribbon ribbon = this.ribbon;
            if (ribbon != null) {
                ribbon.setBackdrop(this.backdrop);
                this.ribbon.setText(9, "#" + LocaleController.formatNumber(savedStarGift.gift.num, ','), false);
            }
        }

        public long getGiftId() {
            return this.id;
        }

        public void setSelected(boolean z, boolean z2) {
            this.cardBackground.setSelected(z, z2);
            float f = z ? 0.9f : 1.0f;
            BackupImageView backupImageView = this.imageView;
            if (z2) {
                backupImageView.animate().scaleX(f).scaleY(f).start();
                return;
            }
            backupImageView.animate().cancel();
            this.imageView.setScaleX(f);
            this.imageView.setScaleY(f);
        }

        private void setSticker(TLRPC.Document document, Object obj) {
            if (document == null) {
                this.imageView.clearImage();
                this.lastDocument = null;
                this.lastDocumentId = 0L;
            } else {
                if (this.lastDocument == document) {
                    return;
                }
                this.lastDocument = document;
                this.lastDocumentId = document.id;
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, AndroidUtilities.dp(100.0f));
                this.imageView.setImage(ImageLocation.getForDocument(document), "100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "100_100", DocumentObject.getSvgThumb(document, Theme.key_windowBackgroundGray, 0.3f), obj);
            }
        }

        public static class Factory extends UItem.UItemFactory<GiftCell> {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public GiftCell createView(Context context, RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new GiftCell(context, true, resourcesProvider);
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public void bindView(View view, UItem uItem, boolean z, UniversalAdapter universalAdapter, UniversalRecyclerView universalRecyclerView) {
                GiftCell giftCell = (GiftCell) view;
                giftCell.set(-1, (TL_stars.SavedStarGift) uItem.object);
                giftCell.setSelected(uItem.checked, false);
            }

            public static UItem asGiftCell(TL_stars.SavedStarGift savedStarGift) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.object = savedStarGift;
                return uItemOfFactory;
            }
        }
    }

    public static boolean eq(TLRPC.EmojiStatus emojiStatus, TLRPC.TL_emojiStatusCollectible tL_emojiStatusCollectible) {
        boolean z = emojiStatus instanceof TLRPC.TL_emojiStatusCollectible;
        return (tL_emojiStatusCollectible != null) == z && tL_emojiStatusCollectible != null && z && ((TLRPC.TL_emojiStatusCollectible) emojiStatus).collectible_id == tL_emojiStatusCollectible.collectible_id;
    }

    public static boolean eq(TLRPC.TL_peerColorCollectible tL_peerColorCollectible, TLRPC.TL_peerColorCollectible tL_peerColorCollectible2) {
        if (tL_peerColorCollectible == tL_peerColorCollectible2) {
            return true;
        }
        if (tL_peerColorCollectible == null && tL_peerColorCollectible2 == null) {
            return true;
        }
        return (tL_peerColorCollectible == null || tL_peerColorCollectible2 == null || tL_peerColorCollectible.collectible_id != tL_peerColorCollectible2.collectible_id) ? false : true;
    }
}
