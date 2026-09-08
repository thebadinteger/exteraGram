package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.components.IconSelectorAlert;
import com.exteragram.messenger.utils.ui.FolderIcons;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditEmojiTextCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.EditTextSuggestionsFix;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.FolderBottomSheet;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.QRCodeBottomSheet;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.spoilers.SpoilersTextView;

public class FilterCreateActivity extends BaseFragment {
    private ListAdapter adapter;
    private CreateLinkCell createLinkCell;
    private boolean creatingNew;
    private boolean doNotCloseWhenSave;
    private ActionBarMenuItem doneItem;
    private boolean excludeExpanded;
    private MessagesController.DialogFilter filter;
    private HeaderCellColorPreview folderTagsHeader;
    private boolean hasUserChanged;
    private boolean includeExpanded;
    private ArrayList<TL_chatlists.TL_exportedChatlistInvite> invites;
    private ArrayList<ItemInner> items;
    private RecyclerListView listView;
    private boolean loadingInvites;
    private boolean nameChangedManually;
    private EditEmojiTextCell nameEditTextCell;
    private HeaderCellWithRight nameHeaderCell;
    private int nameRow;
    private ArrayList<Long> newAlwaysShow;
    private boolean newFilterAnimations;
    private int newFilterColor;
    private String newFilterEmoticon;
    private int newFilterFlags;
    private CharSequence newFilterName;
    private ArrayList<Long> newNeverShow;
    private LongSparseIntArray newPinned;
    private ArrayList<ItemInner> oldItems;
    private int requestingInvitesReqId;
    private HintView saveHintView;
    float shiftDp;
    private Runnable showBulletinOnResume;
    private boolean showedUpdateBulletin;
    private UndoView undoView;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    private boolean canCreateLink() {
        return !(TextUtils.isEmpty(this.newFilterName) && TextUtils.isEmpty(this.filter.name)) && (this.newFilterFlags & (~(MessagesController.DIALOG_FILTER_FLAG_CHATLIST | MessagesController.DIALOG_FILTER_FLAG_CHATLIST_ADMIN))) == 0 && this.newNeverShow.isEmpty() && !this.newAlwaysShow.isEmpty();
    }

    public static class HintInnerCell extends FrameLayout {
        private RLottieImageView imageView;

        public HintInnerCell(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.filter_new, 100, 100);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.playAnimation();
            addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$HintInnerCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$0(view);
                }
            });
        }

        public void lambda$deleteFolder$14(Boolean bool) {
        finishFragment();
    }

    public int $r8$lambda$EAiVXe9Yl9sL1hdHBEq1o50JJaY(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public static void lambda$onCreateViewHolder$0(String str) {
            FilterCreateActivity.this.newFilterEmoticon = str;
            FilterCreateActivity.this.nameEditTextCell.setIcon(FolderIcons.getTabIcon(FilterCreateActivity.this.newFilterEmoticon), true);
            FilterCreateActivity.this.checkDoneButton(true);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            switch (i) {
                case 0:
                    headerCell = new HeaderCell(this.mContext, 22);
                    break;
                case 1:
                    UserCell userCell = new UserCell(this.mContext, 6, 0, false);
                    userCell.setSelfAsSavedMessages(true);
                    headerCell = userCell;
                    break;
                case 2:
                    FilterCreateActivity filterCreateActivity = FilterCreateActivity.this;
                    EditEmojiTextCell editEmojiTextCell = new EditEmojiTextCell(this.mContext, (SizeNotifierFrameLayout) FilterCreateActivity.this.fragmentView, LocaleController.getString(R.string.FilterNameHint), false, 12, 4, ((BaseFragment) FilterCreateActivity.this).resourceProvider) { // from class: org.telegram.ui.FilterCreateActivity.ListAdapter.1
                        @Override // org.telegram.ui.Cells.EditEmojiTextCell
                        public int emojiCacheType() {
                            return 25;
                        }
                    };
                    filterCreateActivity.nameEditTextCell = editEmojiTextCell;
                    editEmojiTextCell.setOnChangeIconListener(new View.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$ListAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            this.f$0.lambda$onCreateViewHolder$1(view);
                        }
                    });
                    editEmojiTextCell.setIcon(FolderIcons.getTabIcon(FilterCreateActivity.this.newFilterEmoticon), false);
                    editEmojiTextCell.setAllowEntities(false);
                    editEmojiTextCell.editTextEmoji.getEditText().setEmojiColor(Integer.valueOf(FilterCreateActivity.this.getThemedColor(Theme.key_featuredStickers_addButton)));
                    editEmojiTextCell.editTextEmoji.setEmojiViewCacheType(25);
                    editEmojiTextCell.editTextEmoji.setText(FilterCreateActivity.this.newFilterName);
                    AnimatedEmojiDrawable.toggleAnimations(((BaseFragment) FilterCreateActivity.this).currentAccount, FilterCreateActivity.this.newFilterAnimations);
                    EditTextCaption editText = editEmojiTextCell.editTextEmoji.getEditText();
                    editText.addTextChangedListener(new EditTextSuggestionsFix());
                    editText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.FilterCreateActivity.ListAdapter.2
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable editable) {
                            String string;
                            if (!TextUtils.equals(editable, FilterCreateActivity.this.newFilterName)) {
                                FilterCreateActivity.this.nameChangedManually = !TextUtils.isEmpty(editable);
                                FilterCreateActivity.this.newFilterName = AnimatedEmojiSpan.onlyEmojiSpans(editable);
                                if (FilterCreateActivity.this.folderTagsHeader != null) {
                                    FilterCreateActivity.this.folderTagsHeader.setPreviewText(AnimatedEmojiSpan.cloneSpans(FilterCreateActivity.this.newFilterName, -1, FilterCreateActivity.this.folderTagsHeader.getPreviewTextPaint().getFontMetricsInt(), 0.5f), true);
                                }
                                if (FilterCreateActivity.this.nameHeaderCell != null) {
                                    AnimatedTextView animatedTextView = FilterCreateActivity.this.nameHeaderCell.rightTextView;
                                    FilterCreateActivity filterCreateActivity2 = FilterCreateActivity.this;
                                    if (filterCreateActivity2.hasAnimatedEmojis(filterCreateActivity2.newFilterName)) {
                                        string = LocaleController.getString(FilterCreateActivity.this.newFilterAnimations ? R.string.FilterNameAnimationsDisable : R.string.FilterNameAnimationsEnable);
                                    } else {
                                        string = null;
                                    }
                                    animatedTextView.setText(string);
                                }
                                ((BaseFragment) FilterCreateActivity.this).actionBar.setTitle(AnimatedEmojiSpan.cloneSpans(FilterCreateActivity.this.newFilterName, -1, ((BaseFragment) FilterCreateActivity.this).actionBar.getTitleFontMetricsInt()));
                            }
                            FilterCreateActivity.this.checkDoneButton(true);
                        }
                    });
                    editText.setPadding(AndroidUtilities.dp(7.0f), editText.getPaddingTop(), editText.getPaddingRight(), editText.getPaddingBottom());
                    editEmojiTextCell.editTextEmoji.getEditText().setImeOptions(268435462);
                    headerCell = editEmojiTextCell;
                    break;
                case 3:
                    headerCell = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    headerCell = new ButtonCell(this.mContext);
                    break;
                case 5:
                    headerCell = new HintInnerCell(this.mContext);
                    break;
                case 6:
                default:
                    headerCell = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 7:
                    Context context = this.mContext;
                    FilterCreateActivity filterCreateActivity2 = FilterCreateActivity.this;
                    headerCell = new LinkCell(context, filterCreateActivity2, ((BaseFragment) filterCreateActivity2).currentAccount, FilterCreateActivity.this.filter.id) { // from class: org.telegram.ui.FilterCreateActivity.ListAdapter.3
                        @Override // org.telegram.ui.FilterCreateActivity.LinkCell
                        public void onDelete(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite) {
                            FilterCreateActivity.this.onDelete(tL_exportedChatlistInvite);
                        }
                    };
                    break;
                case 8:
                    headerCell = new CreateLinkCell(this.mContext);
                    break;
                case 9:
                    headerCell = FilterCreateActivity.this.new HeaderCellColorPreview(this.mContext);
                    break;
                case 10:
                    headerCell = new PeerColorActivity.PeerColorGrid(FilterCreateActivity.this.getContext(), 2, ((BaseFragment) FilterCreateActivity.this).currentAccount, ((BaseFragment) FilterCreateActivity.this).resourceProvider);
                    break;
                case 11:
                    FilterCreateActivity filterCreateActivity3 = FilterCreateActivity.this;
                    headerCell = filterCreateActivity3.new HeaderCellWithRight(this.mContext, ((BaseFragment) filterCreateActivity3).resourceProvider);
                    break;
            }
            return new RecyclerListView.Holder(headerCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 2 && itemViewType == 9) {
                ((HeaderCellColorPreview) viewHolder.itemView).setPreviewText(AnimatedEmojiSpan.cloneSpans(FilterCreateActivity.this.newFilterName, -1, FilterCreateActivity.this.folderTagsHeader.getPreviewTextPaint().getFontMetricsInt(), 0.5f), true);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 2) {
                EditEmojiTextCell editEmojiTextCell = (EditEmojiTextCell) viewHolder.itemView;
                editEmojiTextCell.editTextEmoji.hidePopup(true);
                editEmojiTextCell.editTextEmoji.closeKeyboard();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String string;
            String string2;
            ItemInner itemInner = (ItemInner) FilterCreateActivity.this.items.get(i);
            if (itemInner == null) {
                return;
            }
            int i2 = i + 1;
            boolean z = i2 < FilterCreateActivity.this.items.size() && !((ItemInner) FilterCreateActivity.this.items.get(i2)).isShadow();
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (itemInner.newSpan) {
                    headerCell.setText(FilterCreateActivity.withNew(0, itemInner.text, false));
                    return;
                } else {
                    headerCell.setText(itemInner.text);
                    return;
                }
            }
            if (itemViewType != 1) {
                if (itemViewType == 4) {
                    ButtonCell buttonCell = (ButtonCell) viewHolder.itemView;
                    buttonCell.setRed(itemInner.isRed);
                    buttonCell.set(itemInner.iconResId, itemInner.text, z);
                    return;
                }
                switch (itemViewType) {
                    case 6:
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(itemInner.text);
                        break;
                    case 7:
                        ((LinkCell) viewHolder.itemView).setInvite(itemInner.link, z);
                        break;
                    case 8:
                        FilterCreateActivity.this.createLinkCell = (CreateLinkCell) viewHolder.itemView;
                        FilterCreateActivity.this.createLinkCell.setDivider(z);
                        break;
                    case 9:
                        FilterCreateActivity.this.folderTagsHeader = (HeaderCellColorPreview) viewHolder.itemView;
                        FilterCreateActivity.this.folderTagsHeader.setPreviewText(AnimatedEmojiSpan.cloneSpans(FilterCreateActivity.this.newFilterName, -1, FilterCreateActivity.this.folderTagsHeader.getPreviewTextPaint().getFontMetricsInt(), 0.5f), false);
                        FilterCreateActivity.this.folderTagsHeader.setPreviewColor(FilterCreateActivity.this.getUserConfig().isPremium() ? FilterCreateActivity.this.newFilterColor : -1, false);
                        FilterCreateActivity.this.folderTagsHeader.setText(LocaleController.getString(R.string.FolderTagColor));
                        break;
                    case 10:
                        final PeerColorActivity.PeerColorGrid peerColorGrid = (PeerColorActivity.PeerColorGrid) viewHolder.itemView;
                        peerColorGrid.setCloseAsLock(!FilterCreateActivity.this.getUserConfig().isPremium());
                        peerColorGrid.setSelected(FilterCreateActivity.this.getUserConfig().isPremium() ? FilterCreateActivity.this.newFilterColor : -1, false);
                        peerColorGrid.setOnColorClick(new Utilities.Callback() { // from class: org.telegram.ui.FilterCreateActivity$ListAdapter$$ExternalSyntheticLambda1
                            @Override 
                            public final void run(Object obj) {
                                this.f$0.lambda$onBindViewHolder$2(peerColorGrid, (Integer) obj);
                            }
                        });
                        break;
                    case 11:
                        HeaderCellWithRight headerCellWithRight = (HeaderCellWithRight) viewHolder.itemView;
                        FilterCreateActivity.this.nameHeaderCell = headerCellWithRight;
                        headerCellWithRight.setText(itemInner.text);
                        headerCellWithRight.rightTextView.setText(itemInner.subtext);
                        headerCellWithRight.rightTextView.setOnClickListener(itemInner.onClickListener);
                        break;
                }
                return;
            }
            UserCell userCell = (UserCell) viewHolder.itemView;
            if (itemInner.chatType != null) {
                userCell.setData(itemInner.chatType, itemInner.text, null, 0, z);
                return;
            }
            long j = itemInner.did;
            FilterCreateActivity filterCreateActivity = FilterCreateActivity.this;
            if (j > 0) {
                TLRPC.User user = filterCreateActivity.getMessagesController().getUser(Long.valueOf(j));
                if (user != null) {
                    if (user.bot) {
                        string2 = LocaleController.getString(R.string.Bot);
                    } else if (user.contact) {
                        string2 = LocaleController.getString(R.string.FilterContact);
                    } else {
                        string2 = LocaleController.getString(R.string.FilterNonContact);
                    }
                    userCell.setData(user, null, string2, 0, z);
                    return;
                }
                return;
            }
            TLRPC.Chat chat = filterCreateActivity.getMessagesController().getChat(Long.valueOf(-j));
            if (chat != null) {
                if (ChatObject.isCommunity(chat)) {
                    string = LocaleController.getString(R.string.Community);
                } else if (chat.participants_count != 0) {
                    boolean zIsChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat);
                    int i3 = chat.participants_count;
                    if (zIsChannelAndNotMegaGroup) {
                        string = LocaleController.formatPluralStringComma("Subscribers", i3);
                    } else {
                        string = LocaleController.formatPluralStringComma("Members", i3);
                    }
                } else if (!ChatObject.isPublic(chat)) {
                    if (ChatObject.isChannel(chat) && !chat.megagroup) {
                        string = LocaleController.getString(R.string.ChannelPrivate);
                    } else {
                        string = LocaleController.getString(R.string.MegaPrivate);
                    }
                } else if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    string = LocaleController.getString(R.string.ChannelPublic);
                } else {
                    string = LocaleController.getString(R.string.MegaPublic);
                }
                userCell.setData(chat, null, string, 0, z);
            }
        }

        public /* synthetic */ void lambda$onBindViewHolder$2(PeerColorActivity.PeerColorGrid peerColorGrid, Integer num) {
            boolean zIsPremium = FilterCreateActivity.this.getUserConfig().isPremium();
            FilterCreateActivity filterCreateActivity = FilterCreateActivity.this;
            if (!zIsPremium) {
                filterCreateActivity.showDialog(new PremiumFeatureBottomSheet(FilterCreateActivity.this, 35, true));
                return;
            }
            int iIntValue = num.intValue();
            filterCreateActivity.newFilterColor = iIntValue;
            peerColorGrid.setSelected(iIntValue, true);
            if (FilterCreateActivity.this.folderTagsHeader != null) {
                FilterCreateActivity.this.folderTagsHeader.setPreviewColor(!FilterCreateActivity.this.getUserConfig().isPremium() ? -1 : FilterCreateActivity.this.newFilterColor, true);
            }
            FilterCreateActivity.this.checkDoneButton(true);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            ItemInner itemInner = (ItemInner) FilterCreateActivity.this.items.get(i);
            if (itemInner == null) {
                return 3;
            }
            return itemInner.viewType;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda13
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                this.f$0.lambda$getThemeDescriptions$28();
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, UserCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        int i = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_text_RedRegular));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"ImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_creatorIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        return arrayList;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$28() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }

    public static class ButtonCell extends FrameLayout {
        private boolean divider;
        private ImageView imageView;
        private int lastIconResId;
        private TextView textView;
        private Boolean translateText;

        public ButtonCell(Context context) {
            super(context);
            this.divider = true;
            this.translateText = null;
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 16, 24.0f, 0.0f, 24.0f, 0.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setSingleLine();
            TextView textView2 = this.textView;
            boolean z = LocaleController.isRTL;
            textView2.setPadding(z ? 24 : 0, 0, z ? 0 : 24, 0);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            TextView textView3 = this.textView;
            boolean z2 = LocaleController.isRTL;
            addView(textView3, LayoutHelper.createFrame(-1, -2.0f, 23, z2 ? 0.0f : 72.0f, 0.0f, z2 ? 72.0f : 0.0f, 0.0f));
        }

        public void setRed(boolean z) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(z ? Theme.key_text_RedBold : Theme.key_windowBackgroundWhiteBlueText2), PorterDuff.Mode.MULTIPLY));
            this.textView.setTextColor(Theme.getColor(z ? Theme.key_text_RedRegular : Theme.key_windowBackgroundWhiteBlueText4));
        }

        public void set(int i, CharSequence charSequence, boolean z) {
            int i2 = LocaleController.isRTL ? -1 : 1;
            ImageView imageView = this.imageView;
            boolean z2 = false;
            if (i == 0) {
                imageView.setVisibility(8);
            } else {
                imageView.setVisibility(0);
                this.imageView.setImageResource(i);
            }
            boolean z3 = LocaleController.isRTL;
            TextView textView = this.textView;
            if (z3) {
                ((ViewGroup.MarginLayoutParams) textView.getLayoutParams()).rightMargin = AndroidUtilities.dp(i == 0 ? 24.0f : 72.0f);
            } else {
                ((ViewGroup.MarginLayoutParams) textView.getLayoutParams()).leftMargin = AndroidUtilities.dp(i == 0 ? 24.0f : 72.0f);
            }
            this.textView.setText(charSequence);
            if (!z && i != 0) {
                z2 = true;
            }
            Boolean bool = this.translateText;
            if (bool == null || bool.booleanValue() != z2) {
                this.translateText = Boolean.valueOf(z2);
                int i3 = this.lastIconResId;
                TextView textView2 = this.textView;
                if (i3 == i) {
                    textView2.clearAnimation();
                    this.textView.animate().translationX(z2 ? AndroidUtilities.dp(i2 * (-7)) : 0.0f).setDuration(180L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                } else {
                    textView2.setTranslationX(z2 ? AndroidUtilities.dp(i2 * (-7)) : 0.0f);
                }
            }
            this.divider = z;
            setWillNotDraw(!z);
            this.lastIconResId = i;
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.divider) {
                canvas.drawRect(this.textView.getLeft(), getMeasuredHeight() - 1, this.textView.getRight(), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), TLObject.FLAG_30));
        }
    }

    public static class CreateLinkCell extends FrameLayout {
        ImageView imageView;
        boolean needDivider;
        TextView textView;

        public CreateLinkCell(Context context) {
            super(context);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setText(LocaleController.getString(R.string.CreateNewLink));
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            TextView textView2 = this.textView;
            boolean z = LocaleController.isRTL;
            textView2.setPadding(z ? 16 : 0, 0, z ? 0 : 16, 0);
            TextView textView3 = this.textView;
            boolean z2 = LocaleController.isRTL;
            addView(textView3, LayoutHelper.createFrame(-1, -2.0f, 23, z2 ? 0.0f : 64.0f, 0.0f, z2 ? 64.0f : 0.0f, 0.0f));
            this.imageView = new ImageView(context);
            Drawable drawable = context.getResources().getDrawable(R.drawable.poll_add_circle);
            Drawable drawable2 = context.getResources().getDrawable(R.drawable.poll_add_plus);
            int color = Theme.getColor(Theme.key_featuredStickers_addButton);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            drawable.setColorFilter(new PorterDuffColorFilter(color, mode));
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_checkboxCheck), mode));
            this.imageView.setImageDrawable(new CombinedDrawable(drawable, drawable2));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView = this.imageView;
            boolean z3 = LocaleController.isRTL;
            addView(imageView, LayoutHelper.createFrame(32, 32.0f, (z3 ? 5 : 3) | 16, z3 ? 0.0f : 16.0f, 0.0f, z3 ? 16.0f : 0.0f, 0.0f));
        }

        public void setText(String str) {
            this.textView.setText(str);
        }

        public void setDivider(boolean z) {
            if (this.needDivider != z) {
                this.needDivider = z;
                setWillNotDraw(!z);
            }
        }

        @Override // android.view.View
        public void setEnabled(boolean z) {
            super.setEnabled(z);
            this.textView.setAlpha(z ? 1.0f : 0.5f);
            this.imageView.setAlpha(z ? 1.0f : 0.5f);
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.needDivider) {
                canvas.drawRect(this.textView.getLeft(), getMeasuredHeight() - 1, this.textView.getRight(), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(45.0f), TLObject.FLAG_30));
        }
    }

    public static class LinkCell extends FrameLayout {
        private int currentAccount;
        private int filterId;
        private BaseFragment fragment;
        private TL_chatlists.TL_exportedChatlistInvite lastInvite;
        private boolean lastRevoked;
        protected String lastUrl;
        Drawable linkIcon;
        boolean needDivider;
        ImageView optionsIcon;
        Paint paint;
        float revokeT;
        Drawable revokedLinkIcon;
        Paint revokedPaint;
        AnimatedTextView subtitleTextView;
        AnimatedTextView titleTextView;
        private ValueAnimator valueAnimator;

        public abstract void onDelete(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite);

        public LinkCell(Context context, BaseFragment baseFragment, int i, int i2) {
            super(context);
            this.fragment = baseFragment;
            this.currentAccount = i;
            this.filterId = i2;
            setImportantForAccessibility(1);
            AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, false);
            this.titleTextView = animatedTextView;
            animatedTextView.setTextSize(AndroidUtilities.dp(15.66f));
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.titleTextView.setEllipsizeByGradient(true);
            AnimatedTextView animatedTextView2 = this.titleTextView;
            boolean z = LocaleController.isRTL;
            addView(animatedTextView2, LayoutHelper.createFrame(-1, 20.0f, 55, z ? 56.0f : 64.0f, 10.33f, z ? 64.0f : 56.0f, 0.0f));
            AnimatedTextView animatedTextView3 = new AnimatedTextView(context, false, false, false);
            this.subtitleTextView = animatedTextView3;
            animatedTextView3.setTextSize(AndroidUtilities.dp(13.0f));
            this.subtitleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.subtitleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            AnimatedTextView animatedTextView4 = this.subtitleTextView;
            boolean z2 = LocaleController.isRTL;
            addView(animatedTextView4, LayoutHelper.createFrame(-1, 16.0f, 55, z2 ? 56.0f : 64.0f, 33.33f, z2 ? 64.0f : 56.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.optionsIcon = imageView;
            imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_ab_other));
            this.optionsIcon.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsIcon.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
            ImageView imageView2 = this.optionsIcon;
            int color = Theme.getColor(Theme.key_stickers_menu);
            PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
            imageView2.setColorFilter(new PorterDuffColorFilter(color, mode));
            this.optionsIcon.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$LinkCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$0(view);
                }
            });
            this.optionsIcon.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
            ImageView imageView3 = this.optionsIcon;
            boolean z3 = LocaleController.isRTL;
            addView(imageView3, LayoutHelper.createFrame(40, 40.0f, (z3 ? 3 : 5) | 16, z3 ? 8.0f : 4.0f, 4.0f, z3 ? 4.0f : 8.0f, 4.0f));
            Paint paint = new Paint();
            this.paint = paint;
            paint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
            Paint paint2 = new Paint();
            this.revokedPaint = paint2;
            paint2.setColor(Theme.getColor(Theme.key_color_red));
            Drawable drawableMutate = getContext().getResources().getDrawable(R.drawable.msg_link_1).mutate();
            this.linkIcon = drawableMutate;
            drawableMutate.setColorFilter(new PorterDuffColorFilter(-1, mode));
            Drawable drawableMutate2 = getContext().getResources().getDrawable(R.drawable.msg_link_2).mutate();
            this.revokedLinkIcon = drawableMutate2;
            drawableMutate2.setColorFilter(new PorterDuffColorFilter(-1, mode));
            setWillNotDraw(false);
        }

        public /* synthetic */ void lambda$new$0(View view) {
            options();
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int measuredWidth = LocaleController.isRTL ? getMeasuredWidth() - AndroidUtilities.dp(32.0f) : AndroidUtilities.dp(32.0f);
            float f = measuredWidth;
            canvas.drawCircle(f, getMeasuredHeight() / 2.0f, AndroidUtilities.dp(16.0f), this.paint);
            if (this.revokeT > 0.0f) {
                canvas.drawCircle(f, getMeasuredHeight() / 2.0f, AndroidUtilities.dp(16.0f) * this.revokeT, this.revokedPaint);
            }
            float f2 = this.revokeT;
            if (f2 < 1.0f) {
                this.linkIcon.setAlpha((int) ((1.0f - f2) * 255.0f));
                this.linkIcon.setBounds(measuredWidth - AndroidUtilities.dp(14.0f), (getMeasuredHeight() / 2) - AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f) + measuredWidth, (getMeasuredHeight() / 2) + AndroidUtilities.dp(14.0f));
                this.linkIcon.draw(canvas);
            }
            float f3 = this.revokeT;
            if (f3 > 0.0f) {
                this.revokedLinkIcon.setAlpha((int) (f3 * 255.0f));
                this.revokedLinkIcon.setBounds(measuredWidth - AndroidUtilities.dp(14.0f), (getMeasuredHeight() / 2) - AndroidUtilities.dp(14.0f), measuredWidth + AndroidUtilities.dp(14.0f), (getMeasuredHeight() / 2) + AndroidUtilities.dp(14.0f));
                this.revokedLinkIcon.draw(canvas);
            }
            if (this.needDivider) {
                canvas.drawRect(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(64.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(64.0f) : 0), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        public void setRevoked(final boolean z, boolean z2) {
            this.lastRevoked = z;
            if ((z ? 1.0f : 0.0f) != this.revokeT) {
                ValueAnimator valueAnimator = this.valueAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.valueAnimator = null;
                }
                if (z2) {
                    ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.revokeT, z ? 1.0f : 0.0f);
                    this.valueAnimator = valueAnimatorOfFloat;
                    valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.FilterCreateActivity$LinkCell$$ExternalSyntheticLambda1
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            this.f$0.lambda$setRevoked$1(valueAnimator2);
                        }
                    });
                    this.valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.FilterCreateActivity.LinkCell.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            LinkCell linkCell = LinkCell.this;
                            linkCell.revokeT = z ? 1.0f : 0.0f;
                            linkCell.invalidate();
                        }
                    });
                    this.valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.valueAnimator.setDuration(350L);
                    this.valueAnimator.start();
                    return;
                }
                this.revokeT = z ? 1.0f : 0.0f;
                invalidate();
            }
        }

        public /* synthetic */ void lambda$setRevoked$1(ValueAnimator valueAnimator) {
            this.revokeT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        public void setInvite(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite, boolean z) {
            boolean z2 = this.lastInvite == tL_exportedChatlistInvite;
            this.lastInvite = tL_exportedChatlistInvite;
            String strSubstring = tL_exportedChatlistInvite.url;
            this.lastUrl = strSubstring;
            if (strSubstring.startsWith("http://")) {
                strSubstring = strSubstring.substring(7);
            }
            if (strSubstring.startsWith("https://")) {
                strSubstring = strSubstring.substring(8);
            }
            boolean zIsEmpty = TextUtils.isEmpty(tL_exportedChatlistInvite.title);
            AnimatedTextView animatedTextView = this.titleTextView;
            if (zIsEmpty) {
                animatedTextView.setText(strSubstring, z2);
            } else {
                animatedTextView.setText(tL_exportedChatlistInvite.title, z2);
            }
            this.subtitleTextView.setText(LocaleController.formatPluralString("FilterInviteChats", tL_exportedChatlistInvite.peers.size(), new Object[0]), z2);
            if (this.needDivider != z) {
                this.needDivider = z;
                invalidate();
            }
            setRevoked(tL_exportedChatlistInvite.revoked, z2);
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), TLObject.FLAG_30));
        }

        public void options() {
            BaseFragment baseFragment = this.fragment;
            if (baseFragment instanceof FilterCreateActivity) {
                RecyclerListView recyclerListView = ((FilterCreateActivity) baseFragment).listView;
                ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions(this.fragment, this);
                itemOptionsMakeOptions.setScrimViewBackground(recyclerListView.getClipBackground(this));
                itemOptionsMakeOptions.add(R.drawable.msg_qrcode, LocaleController.getString(R.string.GetQRCode), new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$LinkCell$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.qrcode();
                    }
                });
                itemOptionsMakeOptions.add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.DeleteLink), true, new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$LinkCell$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.deleteLink();
                    }
                });
                if (LocaleController.isRTL) {
                    itemOptionsMakeOptions.setGravity(3);
                }
                itemOptionsMakeOptions.show();
            }
        }

        private String getSlug() {
            String str = this.lastUrl;
            if (str == null) {
                return null;
            }
            return str.substring(str.lastIndexOf(47) + 1);
        }

        public void deleteLink() {
            String slug = getSlug();
            if (slug == null) {
                return;
            }
            TL_chatlists.TL_chatlists_deleteExportedInvite tL_chatlists_deleteExportedInvite = new TL_chatlists.TL_chatlists_deleteExportedInvite();
            TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
            tL_chatlists_deleteExportedInvite.chatlist = tL_inputChatlistDialogFilter;
            tL_inputChatlistDialogFilter.filter_id = this.filterId;
            tL_chatlists_deleteExportedInvite.slug = slug;
            final Runnable runnable = new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$LinkCell$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$deleteLink$4();
                }
            };
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_chatlists_deleteExportedInvite, new RequestDelegate() { // from class: org.telegram.ui.FilterCreateActivity$LinkCell$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$deleteLink$6(runnable, tLObject, tL_error);
                }
            });
            AndroidUtilities.runOnUIThread(runnable, 150L);
        }

        public /* synthetic */ void lambda$deleteLink$4() {
            onDelete(this.lastInvite);
        }

        public /* synthetic */ void lambda$deleteLink$6(final Runnable runnable, TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$LinkCell$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$deleteLink$5(tL_error, runnable);
                }
            });
        }

        public /* synthetic */ void lambda$deleteLink$5(TLRPC.TL_error tL_error, Runnable runnable) {
            if (tL_error != null) {
                BulletinFactory.of(this.fragment).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
        }

        public void qrcode() {
            if (this.lastUrl == null) {
                return;
            }
            QRCodeBottomSheet qRCodeBottomSheet = new QRCodeBottomSheet(getContext(), LocaleController.getString(R.string.InviteByQRCode), this.lastUrl, LocaleController.getString(R.string.QRCodeLinkHelpFolder), false);
            qRCodeBottomSheet.setCenterAnimation(R.raw.qr_code_logo);
            qRCodeBottomSheet.show();
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            String str;
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            StringBuilder sb = new StringBuilder();
            TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite = this.lastInvite;
            String str2 = _UrlKt.FRAGMENT_ENCODE_SET;
            if (tL_exportedChatlistInvite == null || TextUtils.isEmpty(tL_exportedChatlistInvite.title)) {
                str = _UrlKt.FRAGMENT_ENCODE_SET;
            } else {
                str = this.lastInvite.title + "\n ";
            }
            sb.append(str);
            sb.append(LocaleController.getString(R.string.InviteLink));
            sb.append(", ");
            sb.append((Object) this.subtitleTextView.getText());
            TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite2 = this.lastInvite;
            if (tL_exportedChatlistInvite2 != null && TextUtils.isEmpty(tL_exportedChatlistInvite2.title)) {
                str2 = "\n\n" + this.lastInvite.url;
            }
            sb.append(str2);
            accessibilityNodeInfo.setContentDescription(sb.toString());
        }
    }

    public static void hideNew(int i) {
        MessagesController.getGlobalMainSettings().edit().putBoolean("n_" + i, true).apply();
    }

    public static CharSequence withNew(int i, CharSequence charSequence, boolean z) {
        Context context;
        if (i >= 0) {
            if (!MessagesController.getGlobalMainSettings().getBoolean("n_" + i, false) && (context = ApplicationLoader.applicationContext) != null) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
                spannableStringBuilder.append((CharSequence) "  ");
                SpannableString spannableString = new SpannableString("NEW");
                if (z) {
                    Drawable drawableMutate = context.getResources().getDrawable(R.drawable.msg_other_new_outline).mutate();
                    drawableMutate.setBounds(0, -AndroidUtilities.dp(8.0f), drawableMutate.getIntrinsicWidth(), drawableMutate.getIntrinsicHeight() - AndroidUtilities.dp(8.0f));
                    spannableString.setSpan(new ColorImageSpan(drawableMutate, 0), 0, spannableString.length(), 33);
                } else {
                    Drawable drawableMutate2 = context.getResources().getDrawable(R.drawable.msg_other_new_filled).mutate();
                    Drawable drawableMutate3 = context.getResources().getDrawable(R.drawable.msg_other_new_filled_text).mutate();
                    int color = Theme.getColor(Theme.key_featuredStickers_unread);
                    PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
                    drawableMutate2.setColorFilter(new PorterDuffColorFilter(color, mode));
                    drawableMutate3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_buttonText), mode));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(drawableMutate2, drawableMutate3);
                    combinedDrawable.setBounds(0, 0, combinedDrawable.getIntrinsicWidth(), combinedDrawable.getIntrinsicHeight());
                    spannableString.setSpan(new ImageSpan(combinedDrawable, 0), 0, spannableString.length(), 33);
                }
                spannableStringBuilder.append((CharSequence) spannableString);
                return spannableStringBuilder;
            }
        }
        return charSequence;
    }

    public static class ColorImageSpan extends ImageSpan {
        int lastColor;

        public ColorImageSpan(Drawable drawable, int i) {
            super(drawable, i);
        }

        @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            if (paint.getColor() != this.lastColor && getDrawable() != null) {
                Drawable drawable = getDrawable();
                int color = paint.getColor();
                this.lastColor = color;
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
            super.draw(canvas, charSequence, i, i2, f, i3, i4, i5, paint);
        }
    }

    public static class NewSpan extends ReplacementSpan {
        Paint bgPaint;
        private int color;
        private int fontSize;
        float height;
        StaticLayout layout;
        private boolean outline;
        private CharSequence text;
        private int textColor;
        TextPaint textPaint;
        public boolean usePaintAlpha;
        float width;

        public NewSpan(boolean z, int i) {
            this.textPaint = new TextPaint(1);
            this.bgPaint = new Paint(1);
            this.text = "NEW";
            this.outline = z;
            this.fontSize = i;
            this.textPaint.setTypeface(AndroidUtilities.bold());
            Paint paint = this.bgPaint;
            if (z) {
                paint.setStyle(Paint.Style.STROKE);
                this.bgPaint.setStrokeWidth(AndroidUtilities.dpf2(1.33f));
                this.textPaint.setTextSize(AndroidUtilities.dp(i < 0 ? 10.0f : i));
                this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                this.textPaint.setStrokeWidth(AndroidUtilities.dpf2(0.2f));
                this.textPaint.setLetterSpacing(0.03f);
                return;
            }
            paint.setStyle(Paint.Style.FILL);
            this.textPaint.setTextSize(AndroidUtilities.dp(i < 0 ? 12.0f : i));
        }

        public void setTypeface(Typeface typeface) {
            this.textPaint.setTypeface(typeface);
        }

        public NewSpan(float f) {
            this.textPaint = new TextPaint(1);
            this.bgPaint = new Paint(1);
            this.text = "NEW";
            this.outline = false;
            this.textPaint.setTypeface(AndroidUtilities.bold());
            this.bgPaint.setStyle(Paint.Style.FILL);
            this.textPaint.setTextSize(AndroidUtilities.dp(f));
        }

        public void setColor(int i) {
            this.color = i;
        }

        public void setTextColor(int i) {
            this.textColor = i;
        }

        public void setText(CharSequence charSequence) {
            this.text = charSequence;
            if (this.layout != null) {
                this.layout = null;
                makeLayout();
            }
        }

        public StaticLayout makeLayout() {
            if (this.layout == null) {
                StaticLayout staticLayout = new StaticLayout(this.text, this.textPaint, AndroidUtilities.displaySize.x, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.layout = staticLayout;
                this.width = staticLayout.getLineWidth(0);
                this.height = this.layout.getHeight();
            }
            return this.layout;
        }

        @Override // android.text.style.ReplacementSpan
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            makeLayout();
            return (int) (AndroidUtilities.dp(10.0f) + this.width);
        }

        @Override // android.text.style.ReplacementSpan
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            float fDp;
            makeLayout();
            float alpha = this.usePaintAlpha ? paint.getAlpha() / 255.0f : 1.0f;
            int color = this.color;
            int i6 = this.textColor;
            if (color == 0) {
                color = paint.getColor();
            }
            if (i6 == 0) {
                i6 = AndroidUtilities.computePerceivedBrightness(color) > 0.721f ? -16777216 : -1;
            }
            this.bgPaint.setColor(color);
            boolean z = this.outline;
            TextPaint textPaint = this.textPaint;
            if (z) {
                textPaint.setColor(color);
            } else {
                textPaint.setColor(i6);
            }
            Paint paint2 = this.bgPaint;
            paint2.setAlpha((int) (paint2.getAlpha() * alpha));
            TextPaint textPaint2 = this.textPaint;
            textPaint2.setAlpha((int) (textPaint2.getAlpha() * alpha));
            float fDp2 = f + AndroidUtilities.dp(2.0f);
            float fDp3 = (i4 - this.height) + AndroidUtilities.dp(1.0f);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(fDp2, fDp3, this.width + fDp2, this.height + fDp3);
            if (this.outline) {
                fDp = AndroidUtilities.dp(3.66f);
                rectF.left -= AndroidUtilities.dp(4.0f);
                rectF.top -= AndroidUtilities.dp(2.33f);
                rectF.right += AndroidUtilities.dp(3.66f);
                rectF.bottom += AndroidUtilities.dp(1.33f);
            } else {
                fDp = AndroidUtilities.dp(4.4f);
                rectF.inset(AndroidUtilities.dp(-4.0f), AndroidUtilities.dp(this.fontSize == 8 ? -3.66f : -2.33f));
            }
            canvas.drawRoundRect(rectF, fDp, fDp, this.bgPaint);
            canvas.save();
            canvas.translate(fDp2, fDp3);
            this.layout.draw(canvas);
            canvas.restore();
        }
    }

    public static class TextSpan extends ReplacementSpan {
        Paint bgPaint = new Paint(1);
        private int colorKey;
        private final Theme.ResourcesProvider resourcesProvider;
        private Text text;

        public TextSpan(String str, float f, int i, Theme.ResourcesProvider resourcesProvider) {
            this.resourcesProvider = resourcesProvider;
            this.colorKey = i;
            this.text = new Text(str, f, AndroidUtilities.bold());
            this.bgPaint.setStyle(Paint.Style.FILL);
        }

        @Override // android.text.style.ReplacementSpan
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            return (int) (AndroidUtilities.dp(9.33f) + this.text.getWidth());
        }

        @Override // android.text.style.ReplacementSpan
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            int color = Theme.getColor(this.colorKey, this.resourcesProvider);
            this.bgPaint.setColor(Theme.multAlpha(color, 0.15f));
            float f2 = (i5 + i3) / 2.0f;
            float fDp = AndroidUtilities.dp(14.66f);
            RectF rectF = AndroidUtilities.rectTmp;
            float f3 = fDp / 2.0f;
            rectF.set(f, f2 - f3, this.text.getWidth() + f + AndroidUtilities.dp(9.33f), f3 + f2);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.bgPaint);
            this.text.draw(canvas, f + AndroidUtilities.dp(4.66f), f2, color, 1.0f);
        }
    }

    private void onUpdate(final boolean z, final int i) {
        MessagesController.DialogFilter dialogFilter;
        if (!this.showedUpdateBulletin && (dialogFilter = this.filter) != null && dialogFilter.isChatlist() && this.filter.isMyChatlist()) {
            this.showedUpdateBulletin = true;
            this.showBulletinOnResume = new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onUpdate$29(z, i);
                }
            };
            if (getLayoutContainer() != null) {
                this.showBulletinOnResume.run();
                this.showBulletinOnResume = null;
            }
        }
    }

    public /* synthetic */ void lambda$onUpdate$29(boolean z, int i) {
        String pluralString;
        BulletinFactory bulletinFactoryOf = BulletinFactory.of(this);
        int i2 = z ? R.raw.folder_in : R.raw.folder_out;
        if (z) {
            pluralString = LocaleController.formatPluralString("FolderLinkAddedChats", i, new Object[0]);
        } else {
            pluralString = LocaleController.formatPluralString("FolderLinkRemovedChats", i, new Object[0]);
        }
        bulletinFactoryOf.createSimpleBulletin(i2, pluralString, LocaleController.getString(R.string.FolderLinkChatlistUpdate)).setDuration(5000).show();
    }

    public static class FilterInvitesBottomSheet extends BottomSheetWithRecyclerListView {
        private AdapterWithDiffUtils adapter;
        private FrameLayout bulletinContainer;
        private TextView button;
        private MessagesController.DialogFilter filter;
        private ArrayList<TL_chatlists.TL_exportedChatlistInvite> invites;
        private final ArrayList<ItemInner> items;
        private final ArrayList<ItemInner> oldItems;

        public static void show(final BaseFragment baseFragment, final MessagesController.DialogFilter dialogFilter, final Runnable runnable) {
            final long jCurrentTimeMillis = System.currentTimeMillis();
            TL_chatlists.TL_chatlists_getExportedInvites tL_chatlists_getExportedInvites = new TL_chatlists.TL_chatlists_getExportedInvites();
            TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
            tL_chatlists_getExportedInvites.chatlist = tL_inputChatlistDialogFilter;
            tL_inputChatlistDialogFilter.filter_id = dialogFilter.id;
            baseFragment.getConnectionsManager().sendRequest(tL_chatlists_getExportedInvites, new RequestDelegate() { // from class: org.telegram.ui.FilterCreateActivity$FilterInvitesBottomSheet$$ExternalSyntheticLambda0
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$FilterInvitesBottomSheet$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            FilterCreateActivity.FilterInvitesBottomSheet.$r8$lambda$PtxB57vVtG7SpSeyo74XZjhcgzQ(baseFragment, tLObject, dialogFilter, tL_error, runnable, j);
                        }
                    });
                }
            });
        }

        public static /* synthetic */ void $r8$lambda$PtxB57vVtG7SpSeyo74XZjhcgzQ(BaseFragment baseFragment, TLObject tLObject, MessagesController.DialogFilter dialogFilter, TLRPC.TL_error tL_error, Runnable runnable, long j) {
            if (baseFragment == null || baseFragment.getContext() == null) {
                return;
            }
            if (tLObject instanceof TL_chatlists.TL_chatlists_exportedInvites) {
                TL_chatlists.TL_chatlists_exportedInvites tL_chatlists_exportedInvites = (TL_chatlists.TL_chatlists_exportedInvites) tLObject;
                baseFragment.getMessagesController().putChats(tL_chatlists_exportedInvites.chats, false);
                baseFragment.getMessagesController().putUsers(tL_chatlists_exportedInvites.users, false);
                new FilterInvitesBottomSheet(baseFragment, dialogFilter, tL_chatlists_exportedInvites.invites).show();
            } else if (tL_error != null && "FILTER_ID_INVALID".equals(tL_error.text) && !dialogFilter.isDefault()) {
                new FilterInvitesBottomSheet(baseFragment, dialogFilter, null).show();
            } else {
                BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
            }
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable, Math.max(0L, 200 - (System.currentTimeMillis() - j)));
            }
        }

        public FilterInvitesBottomSheet(BaseFragment baseFragment, MessagesController.DialogFilter dialogFilter, ArrayList<TL_chatlists.TL_exportedChatlistInvite> arrayList) {
            super(baseFragment, false, false);
            this.invites = new ArrayList<>();
            this.oldItems = new ArrayList<>();
            this.items = new ArrayList<>();
            this.filter = dialogFilter;
            if (arrayList != null) {
                this.invites.addAll(arrayList);
            }
            updateRows(false);
            this.actionBar.setTitle(getTitle());
            fixNavigationBar(Theme.getColor(Theme.key_dialogBackground));
            TextView textView = new TextView(getContext());
            this.button = textView;
            textView.setTextSize(1, 14.0f);
            this.button.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.button.setTypeface(AndroidUtilities.bold());
            this.button.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 8.0f));
            this.button.setText(LocaleController.getString(R.string.FolderLinkShareButton));
            this.button.setGravity(17);
            this.button.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$FilterInvitesBottomSheet$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$2(view);
                }
            });
            FrameLayout.LayoutParams layoutParamsCreateFrame = LayoutHelper.createFrame(-1, 48.0f, 87, 16.0f, 10.0f, 16.0f, 10.0f);
            int i = layoutParamsCreateFrame.leftMargin;
            int i2 = this.backgroundPaddingLeft;
            layoutParamsCreateFrame.leftMargin = i + i2;
            layoutParamsCreateFrame.rightMargin += i2;
            this.containerView.addView(this.button, layoutParamsCreateFrame);
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.bulletinContainer = frameLayout;
            this.containerView.addView(frameLayout, LayoutHelper.createFrame(-1, 100.0f, 80, 6.0f, 0.0f, 6.0f, 0.0f));
            updateCreateInviteButton();
        }

        public /* synthetic */ void lambda$new$2(View view) {
            createLink();
        }

        public void updateCreateInviteButton() {
            this.button.setVisibility(this.invites.isEmpty() ? 0 : 8);
            this.recyclerListView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), this.invites.isEmpty() ? AndroidUtilities.dp(68.0f) : 0);
        }

        @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
        public CharSequence getTitle() {
            return getTitle(null);
        }

        public CharSequence getTitle(TextView textView) {
            Object objReplaceAnimatedEmoji;
            if (this.filter == null) {
                objReplaceAnimatedEmoji = _UrlKt.FRAGMENT_ENCODE_SET;
            } else {
                Paint.FontMetricsInt fontMetricsInt = textView == null ? null : textView.getPaint().getFontMetricsInt();
                objReplaceAnimatedEmoji = MessageObject.replaceAnimatedEmoji(Emoji.replaceEmoji(new SpannableStringBuilder(this.filter.name), fontMetricsInt, false), this.filter.entities, fontMetricsInt);
            }
            return LocaleController.formatSpannable(R.string.FolderLinkShareTitle2, objReplaceAnimatedEmoji);
        }

        public void updateRows(boolean z) {
            this.oldItems.clear();
            this.oldItems.addAll(this.items);
            this.items.clear();
            this.items.add(ItemInner.asHeader(null));
            if (!this.invites.isEmpty()) {
                this.items.add(ItemInner.asShadow(null));
                this.items.add(ItemInner.asCreateLink());
                for (int i = 0; i < this.invites.size(); i++) {
                    this.items.add(ItemInner.asLink(this.invites.get(i)));
                }
            }
            AdapterWithDiffUtils adapterWithDiffUtils = this.adapter;
            if (adapterWithDiffUtils != null) {
                if (z) {
                    adapterWithDiffUtils.setItems(this.oldItems, this.items);
                } else {
                    notifyDataSetChanged();
                }
            }
        }

        @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
        public RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
            AdapterWithDiffUtils adapterWithDiffUtils = new AdapterWithDiffUtils() { // from class: org.telegram.ui.FilterCreateActivity.FilterInvitesBottomSheet.1
                private RecyclerView.Adapter realAdapter() {
                    return ((BottomSheetWithRecyclerListView) FilterInvitesBottomSheet.this).recyclerListView.getAdapter();
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyItemChanged(int i) {
                    realAdapter().notifyItemChanged(i + 1);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyItemChanged(int i, Object obj) {
                    realAdapter().notifyItemChanged(i + 1, obj);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyItemInserted(int i) {
                    realAdapter().notifyItemInserted(i + 1);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyItemMoved(int i, int i2) {
                    realAdapter().notifyItemMoved(i + 1, i2);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyItemRangeChanged(int i, int i2) {
                    realAdapter().notifyItemRangeChanged(i + 1, i2);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyItemRangeChanged(int i, int i2, Object obj) {
                    realAdapter().notifyItemRangeChanged(i + 1, i2, obj);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyItemRangeInserted(int i, int i2) {
                    realAdapter().notifyItemRangeInserted(i + 1, i2);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyItemRangeRemoved(int i, int i2) {
                    realAdapter().notifyItemRangeRemoved(i + 1, i2);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyItemRemoved(int i) {
                    realAdapter().notifyItemRemoved(i + 1);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyDataSetChanged() {
                    realAdapter().notifyDataSetChanged();
                }

                @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    int itemViewType = viewHolder.getItemViewType();
                    return itemViewType == 8 || itemViewType == 7;
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View textInfoPrivacyCell;
                    if (i == 8) {
                        textInfoPrivacyCell = new CreateLinkCell(FilterInvitesBottomSheet.this.getContext());
                        textInfoPrivacyCell.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
                    } else if (i == 7) {
                        C00661 c00661 = new C00661(FilterInvitesBottomSheet.this.getContext(), null, ((BottomSheet) FilterInvitesBottomSheet.this).currentAccount, FilterInvitesBottomSheet.this.filter.id);
                        c00661.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
                        textInfoPrivacyCell = c00661;
                    } else if (i == 6 || i == 3) {
                        textInfoPrivacyCell = new TextInfoPrivacyCell(FilterInvitesBottomSheet.this.getContext());
                        textInfoPrivacyCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    } else {
                        FilterInvitesBottomSheet filterInvitesBottomSheet = FilterInvitesBottomSheet.this;
                        textInfoPrivacyCell = filterInvitesBottomSheet.new HeaderView(filterInvitesBottomSheet.getContext());
                    }
                    return new RecyclerListView.Holder(textInfoPrivacyCell);
                }

                public class C00661 extends LinkCell {
                    public C00661(Context context, BaseFragment baseFragment, int i, int i2) {
                        super(context, baseFragment, i, i2);
                    }

                    @Override // org.telegram.ui.FilterCreateActivity.LinkCell
                    public void options() {
                        ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions(FilterInvitesBottomSheet.this.container, this);
                        itemOptionsMakeOptions.add(R.drawable.msg_copy, LocaleController.getString(R.string.CopyLink), new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$FilterInvitesBottomSheet$1$1$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.copy();
                            }
                        });
                        itemOptionsMakeOptions.add(R.drawable.msg_qrcode, LocaleController.getString(R.string.GetQRCode), new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$FilterInvitesBottomSheet$1$1$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.qrcode();
                            }
                        });
                        itemOptionsMakeOptions.add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.DeleteLink), true, new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$FilterInvitesBottomSheet$1$1$$ExternalSyntheticLambda2
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.deleteLink();
                            }
                        });
                        if (LocaleController.isRTL) {
                            itemOptionsMakeOptions.setGravity(3);
                        }
                        itemOptionsMakeOptions.show();
                    }

                    public void copy() {
                        String str = this.lastUrl;
                        if (str != null && AndroidUtilities.addToClipboard(str)) {
                            BulletinFactory.of(FilterInvitesBottomSheet.this.bulletinContainer, null).createCopyLinkBulletin().show();
                        }
                    }

                    @Override // org.telegram.ui.FilterCreateActivity.LinkCell
                    public void onDelete(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite) {
                        FilterInvitesBottomSheet.this.invites.remove(tL_exportedChatlistInvite);
                        FilterInvitesBottomSheet.this.updateCreateInviteButton();
                        FilterInvitesBottomSheet.this.updateRows(true);
                    }
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public int getItemViewType(int i) {
                    return ((ItemInner) FilterInvitesBottomSheet.this.items.get(i)).viewType;
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    int itemViewType = viewHolder.getItemViewType();
                    ItemInner itemInner = (ItemInner) FilterInvitesBottomSheet.this.items.get(i);
                    int i2 = i + 1;
                    boolean z = i2 < FilterInvitesBottomSheet.this.items.size() && !((ItemInner) FilterInvitesBottomSheet.this.items.get(i2)).isShadow();
                    if (itemViewType == 7) {
                        ((LinkCell) viewHolder.itemView).setInvite(itemInner.link, z);
                        return;
                    }
                    if (itemViewType != 6 && itemViewType != 3) {
                        if (itemViewType != 0 && itemViewType == 8) {
                            CreateLinkCell createLinkCell = (CreateLinkCell) viewHolder.itemView;
                            createLinkCell.setText(LocaleController.getString(R.string.CreateNewInviteLink));
                            createLinkCell.setDivider(z);
                            return;
                        }
                        return;
                    }
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (itemViewType == 6) {
                        textInfoPrivacyCell.setFixedSize(0);
                        textInfoPrivacyCell.setText(itemInner.text);
                    } else {
                        textInfoPrivacyCell.setFixedSize(12);
                        textInfoPrivacyCell.setText(_UrlKt.FRAGMENT_ENCODE_SET);
                    }
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public int getItemCount() {
                    return FilterInvitesBottomSheet.this.items.size();
                }
            };
            this.adapter = adapterWithDiffUtils;
            return adapterWithDiffUtils;
        }

        public class HeaderView extends FrameLayout {
            private final ImageView closeImageView;
            private final ImageView imageView;
            private final TextView subtitleView;
            private final SpoilersTextView titleView;

            public HeaderView(Context context) {
                String string;
                super(context);
                ImageView imageView = new ImageView(context);
                this.imageView = imageView;
                ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
                imageView.setScaleType(scaleType);
                imageView.setImageResource(R.drawable.msg_limit_links);
                imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
                imageView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(22.0f), Theme.getColor(Theme.key_featuredStickers_addButton)));
                addView(imageView, LayoutHelper.createFrame(54, 44.0f, 49, 0.0f, 22.0f, 0.0f, 0.0f));
                SpoilersTextView spoilersTextView = new SpoilersTextView(context);
                this.titleView = spoilersTextView;
                spoilersTextView.setTypeface(AndroidUtilities.bold());
                spoilersTextView.setTextSize(1, 20.0f);
                int i = Theme.key_dialogTextBlack;
                spoilersTextView.setTextColor(Theme.getColor(i));
                spoilersTextView.setGravity(1);
                spoilersTextView.setText(FilterInvitesBottomSheet.this.getTitle(spoilersTextView));
                spoilersTextView.cacheType = (FilterInvitesBottomSheet.this.filter == null || !FilterInvitesBottomSheet.this.filter.title_noanimate) ? 0 : 26;
                addView(spoilersTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 20.0f, 84.0f, 20.0f, 0.0f));
                TextView textView = new TextView(context);
                this.subtitleView = textView;
                if (FilterInvitesBottomSheet.this.invites.isEmpty()) {
                    string = LocaleController.getString(R.string.FolderLinkShareSubtitleEmpty);
                } else {
                    string = LocaleController.getString(R.string.FolderLinkShareSubtitle);
                }
                textView.setText(string);
                textView.setLines(2);
                textView.setGravity(1);
                textView.setTextSize(1, 14.0f);
                textView.setTextColor(Theme.getColor(i));
                addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 30.0f, 117.0f, 30.0f, 0.0f));
                ImageView imageView2 = new ImageView(context);
                this.closeImageView = imageView2;
                imageView2.setScaleType(scaleType);
                imageView2.setImageResource(R.drawable.msg_close);
                imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText5), PorterDuff.Mode.MULTIPLY));
                imageView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$FilterInvitesBottomSheet$HeaderView$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$new$0(view);
                    }
                });
                addView(imageView2, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, -4.0f, 2.0f, 0.0f));
            }

            public /* synthetic */ void lambda$new$0(View view) {
                FilterInvitesBottomSheet.this.lambda$new$0();
            }

            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(171.0f), TLObject.FLAG_30));
            }
        }

        private void createLink() {
            ArrayList<TLRPC.InputPeer> arrayList = new ArrayList<>();
            for (int i = 0; i < this.filter.alwaysShow.size(); i++) {
                long jLongValue = this.filter.alwaysShow.get(i).longValue();
                if (jLongValue < 0 && FilterCreateActivity.canAddToFolder(getBaseFragment().getMessagesController().getChat(Long.valueOf(-jLongValue)))) {
                    arrayList.add(getBaseFragment().getMessagesController().getInputPeer(jLongValue));
                }
            }
            if (arrayList.isEmpty()) {
                lambda$new$0();
                getBaseFragment().presentFragment(new FilterChatlistActivity(this.filter, null));
                return;
            }
            TL_chatlists.TL_chatlists_exportChatlistInvite tL_chatlists_exportChatlistInvite = new TL_chatlists.TL_chatlists_exportChatlistInvite();
            TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
            tL_chatlists_exportChatlistInvite.chatlist = tL_inputChatlistDialogFilter;
            tL_inputChatlistDialogFilter.filter_id = this.filter.id;
            tL_chatlists_exportChatlistInvite.peers = arrayList;
            tL_chatlists_exportChatlistInvite.title = _UrlKt.FRAGMENT_ENCODE_SET;
            getBaseFragment().getConnectionsManager().sendRequest(tL_chatlists_exportChatlistInvite, new RequestDelegate() { // from class: org.telegram.ui.FilterCreateActivity$FilterInvitesBottomSheet$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$createLink$4(tLObject, tL_error);
                }
            });
        }

        public /* synthetic */ void lambda$createLink$4(final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$FilterInvitesBottomSheet$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$createLink$3(tL_error, tLObject);
                }
            });
        }

        public /* synthetic */ void lambda$createLink$3(TLRPC.TL_error tL_error, TLObject tLObject) {
            if (FilterCreateActivity.processErrors(tL_error, getBaseFragment(), BulletinFactory.of(this.bulletinContainer, null)) && (tLObject instanceof TL_chatlists.TL_chatlists_exportedChatlistInvite)) {
                FilterCreateActivity.hideNew(0);
                lambda$new$0();
                getBaseFragment().getMessagesController().loadRemoteFilters(true);
                getBaseFragment().presentFragment(new FilterChatlistActivity(this.filter, ((TL_chatlists.TL_chatlists_exportedChatlistInvite) tLObject).invite));
            }
        }

        @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
        public void onViewCreated(FrameLayout frameLayout) {
            super.onViewCreated(frameLayout);
            this.recyclerListView.setOverScrollMode(2);
            this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.FilterCreateActivity$FilterInvitesBottomSheet$$ExternalSyntheticLambda2
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i) {
                    this.f$0.lambda$onViewCreated$5(view, i);
                }
            });
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            defaultItemAnimator.setSupportsChangeAnimations(false);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            defaultItemAnimator.setDurations(350L);
            this.recyclerListView.setItemAnimator(defaultItemAnimator);
        }

        public /* synthetic */ void lambda$onViewCreated$5(View view, int i) {
            int i2 = i - 1;
            if (i2 < 0 || i2 >= this.items.size()) {
                return;
            }
            ItemInner itemInner = this.items.get(i2);
            int i3 = itemInner.viewType;
            if (i3 == 7) {
                lambda$new$0();
                getBaseFragment().presentFragment(new FilterChatlistActivity(this.filter, itemInner.link));
            } else if (i3 == 8) {
                createLink();
            }
        }
    }

    public static boolean processErrors(TLRPC.TL_error tL_error, BaseFragment baseFragment, BulletinFactory bulletinFactory) {
        if (tL_error != null && !TextUtils.isEmpty(tL_error.text)) {
            if ("INVITE_PEERS_TOO_MUCH".equals(tL_error.text)) {
                new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 4, baseFragment.getCurrentAccount(), null).show();
            } else if ("PEERS_LIST_EMPTY".equals(tL_error.text)) {
                bulletinFactory.createErrorBulletin(LocaleController.getString(R.string.FolderLinkNoChatsError)).show();
            } else if ("USER_CHANNELS_TOO_MUCH".equals(tL_error.text)) {
                bulletinFactory.createErrorBulletin(LocaleController.getString(R.string.FolderLinkOtherAdminLimitError)).show();
            } else if ("CHANNELS_TOO_MUCH".equals(tL_error.text)) {
                new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 5, baseFragment.getCurrentAccount(), null).show();
            } else if ("INVITES_TOO_MUCH".equals(tL_error.text)) {
                new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 12, baseFragment.getCurrentAccount(), null).show();
            } else if ("CHATLISTS_TOO_MUCH".equals(tL_error.text)) {
                new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 13, baseFragment.getCurrentAccount(), null).show();
            } else if ("INVITE_SLUG_EXPIRED".equals(tL_error.text)) {
                bulletinFactory.createErrorBulletin(LocaleController.getString(R.string.NoFolderFound)).show();
            } else if ("FILTER_INCLUDE_TOO_MUCH".equals(tL_error.text)) {
                new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 4, baseFragment.getCurrentAccount(), null).show();
            } else if ("DIALOG_FILTERS_TOO_MUCH".equals(tL_error.text)) {
                new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 3, baseFragment.getCurrentAccount(), null).show();
            } else {
                bulletinFactory.createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
            }
        }
        return true;
    }

    public class HeaderCellColorPreview extends HeaderCell {
        private final AnimatedColor animatedColor;
        private int currentColor;
        public final TextView noTag;
        private boolean noTagShown;
        public final AnimatedTextView previewView;

        public HeaderCellColorPreview(Context context) {
            super(context, Theme.key_windowBackgroundWhiteBlueHeader, 22, 15, false, ((BaseFragment) FilterCreateActivity.this).resourceProvider);
            TextView textView = new TextView(getContext());
            this.noTag = textView;
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(FilterCreateActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText2));
            textView.setText(LocaleController.getString(FilterCreateActivity.this.getUserConfig().isPremium() ? R.string.FolderTagNoColor : R.string.FolderTagNoColorPremium));
            textView.setGravity(5);
            int i = (LocaleController.isRTL ? 3 : 5) | 48;
            int i2 = this.padding;
            addView(textView, LayoutHelper.createFrame(-1, -1.0f, i, i2, 16.66f, i2, this.bottomMargin));
            textView.setAlpha(0.0f);
            AnimatedTextView animatedTextView = new AnimatedTextView(getContext(), false, true, true) { // from class: org.telegram.ui.FilterCreateActivity.HeaderCellColorPreview.1
                private final Paint backgroundPaint = new Paint(1);

                @Override // android.view.View
                public void dispatchDraw(Canvas canvas) {
                    int i3 = HeaderCellColorPreview.this.animatedColor.set(HeaderCellColorPreview.this.currentColor);
                    setTextColor(i3);
                    this.backgroundPaint.setColor(Theme.multAlpha(i3, Theme.isCurrentThemeDark() ? 0.2f : 0.1f));
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set((getWidth() - getDrawable().getCurrentWidth()) - AndroidUtilities.dpf2(9.32f), (getHeight() - AndroidUtilities.dpf2(14.66f)) / 2.0f, getWidth(), (getHeight() + AndroidUtilities.dpf2(14.66f)) / 2.0f);
                    canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.backgroundPaint);
                    super.dispatchDraw(canvas);
                }
            };
            this.previewView = animatedTextView;
            this.animatedColor = new AnimatedColor(animatedTextView, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            animatedTextView.setTextSize(AndroidUtilities.dp(10.0f));
            animatedTextView.setTypeface(AndroidUtilities.bold());
            animatedTextView.setGravity(5);
            animatedTextView.setPadding(AndroidUtilities.dp(4.66f), 0, AndroidUtilities.dp(4.66f), 0);
            int i3 = LocaleController.isRTL ? 3 : 5;
            int i4 = this.padding;
            addView(animatedTextView, LayoutHelper.createFrame(-1, -1.0f, i3 | 48, i4, 16.66f, i4, this.bottomMargin));
        }

        public void setPreviewColor(int i, boolean z) {
            this.noTag.setText(LocaleController.getString(FilterCreateActivity.this.getUserConfig().isPremium() ? R.string.FolderTagNoColor : R.string.FolderTagNoColorPremium));
            int themedColor = 0;
            boolean z2 = i < 0;
            if (!z2) {
                FilterCreateActivity filterCreateActivity = FilterCreateActivity.this;
                int[] iArr = Theme.keys_avatar_nameInMessage;
                themedColor = filterCreateActivity.getThemedColor(iArr[i % iArr.length]);
            }
            this.currentColor = themedColor;
            if (!z2) {
                this.previewView.setEmojiColor(themedColor);
            }
            if (!z) {
                this.animatedColor.set(this.currentColor, true);
            }
            if (z2 != this.noTagShown) {
                this.noTagShown = z2;
                ViewPropertyAnimator duration = this.noTag.animate().alpha(z2 ? 1.0f : 0.0f).setDuration(320L);
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
                duration.setInterpolator(cubicBezierInterpolator).start();
                this.previewView.animate().alpha(z2 ? 0.0f : 1.0f).setDuration(320L).setInterpolator(cubicBezierInterpolator).start();
            }
        }

        public void setPreviewText(CharSequence charSequence, boolean z) {
            if (charSequence == null) {
                charSequence = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            boolean z2 = false;
            if (charSequence.length() > 12) {
                charSequence = charSequence.subSequence(0, 12);
            }
            AnimatedTextView animatedTextView = this.previewView;
            CharSequence charSequenceReplaceEmoji = Emoji.replaceEmoji(charSequence, animatedTextView.getPaint().getFontMetricsInt(), false);
            if (z && !LocaleController.isRTL) {
                z2 = true;
            }
            animatedTextView.setText(charSequenceReplaceEmoji, z2);
        }

        public TextPaint getPreviewTextPaint() {
            return this.previewView.getPaint();
        }
    }

    public class HeaderCellWithRight extends HeaderCell {
        private final AnimatedTextView rightTextView;

        public HeaderCellWithRight(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true) { // from class: org.telegram.ui.FilterCreateActivity.HeaderCellWithRight.1
                @Override // org.telegram.ui.Components.AnimatedTextView, android.view.View
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, i2);
                    setPivotX(getMeasuredWidth());
                }
            };
            this.rightTextView = animatedTextView;
            animatedTextView.setGravity(LocaleController.isRTL ? 3 : 5);
            animatedTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader, resourcesProvider));
            animatedTextView.setTextSize(AndroidUtilities.dpf2(15.0f));
            addView(animatedTextView, LayoutHelper.createFrame(-1, 18.0f, (LocaleController.isRTL ? 3 : 5) | 48, 22.0f, 17.0f, 22.0f, 0.0f));
            ScaleStateListAnimator.apply(animatedTextView, 0.04f, 1.2f);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onInsets(int i, int i2, int i3, int i4) {
        this.listView.setPadding(0, 0, 0, i4);
        this.listView.setClipToPadding(false);
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.setTranslationY(-i4);
        }
    }
}
