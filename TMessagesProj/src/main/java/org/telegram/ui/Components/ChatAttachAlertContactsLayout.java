package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import okhttp3.internal.url._UrlKt;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.utils.TextWatcherImpl;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;

@SuppressLint({"ViewConstructor"})
public class ChatAttachAlertContactsLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate, FactorAnimator.Target {
    private final BoolAnimator animatorFadeVisible;
    private PhonebookShareAlertDelegate delegate;
    private final EmptyTextProgressView emptyView;
    private final View fadeView;
    private final FrameLayout frameLayout;
    private final FillLastLinearLayoutManager layoutManager;
    private final ShareAdapter listAdapter;
    private final RecyclerListView listView;
    private boolean multipleSelectionAllowed;
    private final ShareSearchAdapter searchAdapter;
    private final FragmentSearchField searchField;
    private final HashMap<ListItemID, Object> selectedContacts;
    private final ArrayList<ListItemID> selectedContactsOrder;
    private boolean sendPressed;

    public interface PhonebookShareAlertDelegate {
        void didSelectContact(TLRPC.User user, boolean z, int i, long j, boolean z2, long j2);

        default void didSelectContacts(ArrayList<TLRPC.User> arrayList, String str, boolean z, int i, long j, boolean z2, long j2) {
        }
    }

    public static class UserCell extends FrameLayout {
        private AvatarDrawable avatarDrawable;
        private BackupImageView avatarImageView;
        private CheckBox2 checkBox;
        private int currentAccount;
        private int currentId;
        private CharSequence currentName;
        private CharSequence currentStatus;
        private TLRPC.User currentUser;
        private CharSequence formattedPhoneNumber;
        private TLRPC.User formattedPhoneNumberUser;
        private TLRPC.FileLocation lastAvatar;
        private String lastName;
        private int lastStatus;
        private SimpleTextView nameTextView;
        private boolean needDivider;
        private final Theme.ResourcesProvider resourcesProvider;
        private SimpleTextView statusTextView;

        public interface CharSequenceCallback {
            CharSequence run();
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }

        public UserCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.currentAccount = UserConfig.selectedAccount;
            this.resourcesProvider = resourcesProvider;
            this.avatarDrawable = new AvatarDrawable(resourcesProvider);
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(ExteraConfig.getAvatarCorners(46.0f));
            BackupImageView backupImageView2 = this.avatarImageView;
            boolean z = LocaleController.isRTL;
            addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z ? 5 : 3) | 48, z ? 0.0f : 14.0f, 9.0f, z ? 14.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context) { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout.UserCell.1
                @Override // org.telegram.ui.ActionBar.SimpleTextView
                public boolean setText(CharSequence charSequence, boolean z2) {
                    return super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), false), z2);
                }
            };
            this.nameTextView = simpleTextView;
            NotificationCenter.listenEmojiLoading(simpleTextView);
            this.nameTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            this.nameTextView.setTypeface(AndroidUtilities.bold());
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            SimpleTextView simpleTextView2 = this.nameTextView;
            boolean z2 = LocaleController.isRTL;
            addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z2 ? 5 : 3) | 48, z2 ? 28.0f : 72.0f, 12.0f, z2 ? 72.0f : 28.0f, 0.0f));
            SimpleTextView simpleTextView3 = new SimpleTextView(context);
            this.statusTextView = simpleTextView3;
            simpleTextView3.setTextSize(13);
            this.statusTextView.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
            this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            SimpleTextView simpleTextView4 = this.statusTextView;
            boolean z3 = LocaleController.isRTL;
            addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 28.0f : 72.0f, 36.0f, z3 ? 72.0f : 28.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
            this.checkBox = checkBox2;
            checkBox2.setColor(-1, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            CheckBox2 checkBox3 = this.checkBox;
            boolean z4 = LocaleController.isRTL;
            addView(checkBox3, LayoutHelper.createFrame(24, 24.0f, (z4 ? 5 : 3) | 48, z4 ? 0.0f : 44.0f, 37.0f, z4 ? 44.0f : 0.0f, 0.0f));
        }

        public void setCurrentId(int i) {
            this.currentId = i;
        }

        public void setData(TLRPC.User user, CharSequence charSequence, CharSequence charSequence2, boolean z) {
            if (user == null && charSequence == null && charSequence2 == null) {
                this.currentStatus = null;
                this.currentName = null;
                this.nameTextView.setText(_UrlKt.FRAGMENT_ENCODE_SET);
                this.statusTextView.setText(_UrlKt.FRAGMENT_ENCODE_SET);
                this.avatarImageView.setImageDrawable(null);
                return;
            }
            this.currentStatus = charSequence2;
            this.currentName = charSequence;
            this.currentUser = user;
            this.needDivider = z;
            setWillNotDraw(!z);
            update(0);
        }

        public void setData(TLRPC.User user, CharSequence charSequence, final CharSequenceCallback charSequenceCallback, boolean z) {
            setData(user, charSequence, (CharSequence) null, z);
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$UserCell$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setData$1(charSequenceCallback);
                }
            });
        }

        public void lambda$sendSelectedItems$3(ArrayList arrayList, boolean z, int i, long j, boolean z2, Long l) {
        this.delegate.didSelectContacts(arrayList, this.parentAlert.getCommentView().getText().toString(), z, i, j, z2, l.longValue());
        this.parentAlert.lambda$new$0();
    }

    public ArrayList<TLRPC.User> getSelected() {
        ArrayList<TLRPC.User> arrayList = new ArrayList<>(this.selectedContacts.size());
        ArrayList<ListItemID> arrayList2 = this.selectedContactsOrder;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            ListItemID listItemID = arrayList2.get(i);
            i++;
            arrayList.add(prepareContact(this.selectedContacts.get(listItemID)));
        }
        return arrayList;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = (childAt.getTop() - AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(8.0f);
        int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            this.animatorFadeVisible.setValue(false, true);
        } else {
            this.animatorFadeVisible.setValue(true, true);
            top = i;
        }
        this.frameLayout.setTranslationY(top);
        return top + AndroidUtilities.dp(12.0f);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(4.0f);
    }

    @Override // android.view.View
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    CharSequence $r8$lambda$P5ZiTS1sCM5N3fBMSNztNGvaA88(ContactsController.Contact contact) {
            return contact.phones.isEmpty() ? _UrlKt.FRAGMENT_ENCODE_SET : PhoneFormat.getInstance().format(contact.phones.get(0));
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getItemViewType(int i, int i2) {
            if (i == 0) {
                return 1;
            }
            return i == getSectionCount() - 1 ? 2 : 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertContactsLayout.this.updateEmptyView();
        }
    }

    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastSearchId;
        private Context mContext;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public ShareSearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(final String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
            } else {
                final int i = this.lastSearchId + 1;
                this.lastSearchId = i;
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$search$0(str, i);
                    }
                };
                this.searchRunnable = runnable;
                dispatchQueue.postRunnable(runnable, 300L);
            }
        }

        public void lambda$search$0(final String str, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$processSearch$2(str, i);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$2(final String str, final int i) {
            final int i2 = UserConfig.selectedAccount;
            final ArrayList arrayList = new ArrayList(ContactsController.getInstance(i2).contactsBook.values());
            final ArrayList arrayList2 = new ArrayList(ContactsController.getInstance(i2).contacts);
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$processSearch$1(str, arrayList, arrayList2, i2, i);
                }
            });
        }

        /* JADX WARN: Code duplicated, block: B:36:0x00c9  */
        /* JADX WARN: Code duplicated, block: B:37:0x00cb  */
        /* JADX WARN: Code duplicated, block: B:39:0x00d1  */
        /* JADX WARN: Code duplicated, block: B:49:0x00f7  */
        /* JADX WARN: Code duplicated, block: B:51:0x00fd  */
        /* JADX WARN: Code duplicated, block: B:53:0x010f  */
        /* JADX WARN: Code duplicated, block: B:54:0x0111  */
        /* JADX WARN: Code duplicated, block: B:56:0x0117  */
        /* JADX WARN: Code duplicated, block: B:60:0x012d  */
        /* JADX WARN: Instruction removed from duplicated block: B:39:0x00d1, please report this as an issue */
        /* JADX WARN: Instruction removed from duplicated block: B:51:0x00fd, please report this as an issue */
        /* JADX WARN: Instruction removed from duplicated block: B:56:0x0117, please report this as an issue */
        public /* synthetic */ void lambda$processSearch$1(String str, ArrayList arrayList, ArrayList arrayList2, int i, int i2) {
            int i3;
            String lowerCase;
            String translitString;
            int i4;
            TLRPC.User user;
            char c2;
            String publicUsername;
            String lowerCase2 = str.trim().toLowerCase();
            if (lowerCase2.length() == 0) {
                this.lastSearchId = -1;
                updateSearchResults(str, new ArrayList<>(), new ArrayList<>(), this.lastSearchId);
                return;
            }
            String translitString2 = LocaleController.getInstance().getTranslitString(lowerCase2);
            if (lowerCase2.equals(translitString2) || translitString2.length() == 0) {
                translitString2 = null;
            }
            int i5 = (translitString2 != null ? 1 : 0) + 1;
            String[] strArr = new String[i5];
            strArr[0] = lowerCase2;
            if (translitString2 != null) {
                strArr[1] = translitString2;
            }
            ArrayList<Object> arrayList3 = new ArrayList<>();
            ArrayList<CharSequence> arrayList4 = new ArrayList<>();
            LongSparseIntArray longSparseIntArray = new LongSparseIntArray();
            int i6 = 0;
            while (i6 < arrayList.size()) {
                ContactsController.Contact contact = (ContactsController.Contact) arrayList.get(i6);
                String lowerCase3 = ContactsController.formatName(contact.first_name, contact.last_name).toLowerCase();
                String translitString3 = LocaleController.getInstance().getTranslitString(lowerCase3);
                TLRPC.User user2 = contact.user;
                if (user2 != null) {
                    lowerCase = ContactsController.formatName(user2.first_name, user2.last_name).toLowerCase();
                    translitString = LocaleController.getInstance().getTranslitString(lowerCase3);
                } else {
                    lowerCase = null;
                    translitString = null;
                }
                if (lowerCase3.equals(translitString3)) {
                    translitString3 = null;
                }
                String[] strArr2 = strArr;
                int i7 = 0;
                char c3 = 0;
                while (true) {
                    if (i7 >= i5) {
                        i4 = i6;
                        break;
                    }
                    int i8 = i7;
                    String str2 = strArr2[i8];
                    i4 = i6;
                    if (lowerCase != null) {
                        if (!lowerCase.startsWith(str2)) {
                            if (!lowerCase.contains(" " + str2)) {
                                if (translitString != null) {
                                    if (!translitString.startsWith(str2)) {
                                        if (translitString.contains(" " + str2)) {
                                        }
                                    }
                                }
                                user = contact.user;
                                if (user == null) {
                                    if (lowerCase3.startsWith(str2)) {
                                        c2 = 3;
                                    } else {
                                        if (lowerCase3.contains(" " + str2)) {
                                            c2 = 3;
                                        } else {
                                            if (translitString3 != null) {
                                                if (!translitString3.startsWith(str2)) {
                                                    if (translitString3.contains(" " + str2)) {
                                                    }
                                                }
                                                c2 = 3;
                                            }
                                            c2 = c3;
                                        }
                                    }
                                } else if (lowerCase3.startsWith(str2)) {
                                    if (lowerCase3.contains(" " + str2)) {
                                        c2 = 3;
                                    } else {
                                        if (translitString3 != null) {
                                            if (!translitString3.startsWith(str2)) {
                                                if (translitString3.contains(" " + str2)) {
                                                }
                                            }
                                            c2 = 3;
                                        }
                                        c2 = c3;
                                    }
                                } else {
                                    c2 = 3;
                                }
                            }
                        }
                        c2 = 1;
                    } else {
                        if (translitString != null) {
                            if (!translitString.startsWith(str2)) {
                                if (translitString.contains(" " + str2)) {
                                }
                            }
                            c2 = 1;
                        }
                        user = contact.user;
                        if (user == null && (publicUsername = UserObject.getPublicUsername(user)) != null && publicUsername.startsWith(str2)) {
                            c2 = 2;
                        } else if (lowerCase3.startsWith(str2)) {
                            if (lowerCase3.contains(" " + str2)) {
                                c2 = 3;
                            } else {
                                if (translitString3 != null) {
                                    if (!translitString3.startsWith(str2)) {
                                        if (translitString3.contains(" " + str2)) {
                                        }
                                    }
                                    c2 = 3;
                                }
                                c2 = c3;
                            }
                        } else {
                            c2 = 3;
                        }
                    }
                    String str3 = lowerCase3;
                    if (c2 != 0 && (!contact.phones.isEmpty() || !contact.shortPhones.isEmpty())) {
                        if (c2 == 3) {
                            arrayList4.add(AndroidUtilities.generateSearchName(contact.first_name, contact.last_name, str2));
                        } else {
                            TLRPC.User user3 = contact.user;
                            if (c2 == 1) {
                                arrayList4.add(AndroidUtilities.generateSearchName(user3.first_name, user3.last_name, str2));
                            } else {
                                arrayList4.add(AndroidUtilities.generateSearchName("@" + UserObject.getPublicUsername(user3), null, "@" + str2));
                            }
                        }
                        TLRPC.User user4 = contact.user;
                        if (user4 != null) {
                            longSparseIntArray.put(user4.id, 1);
                        }
                        arrayList3.add(contact);
                        break;
                    }
                    i7 = i8 + 1;
                    lowerCase3 = str3;
                    c3 = c2;
                    i6 = i4;
                }
                i6 = i4 + 1;
                strArr = strArr2;
            }
            String[] strArr3 = strArr;
            int i9 = 0;
            while (i9 < arrayList2.size()) {
                TLRPC.TL_contact tL_contact = (TLRPC.TL_contact) arrayList2.get(i9);
                if (longSparseIntArray.indexOfKey(tL_contact.user_id) < 0) {
                    TLRPC.User user5 = MessagesController.getInstance(i).getUser(Long.valueOf(tL_contact.user_id));
                    String lowerCase4 = ContactsController.formatName(user5.first_name, user5.last_name).toLowerCase();
                    String translitString4 = LocaleController.getInstance().getTranslitString(lowerCase4);
                    if (lowerCase4.equals(translitString4)) {
                        translitString4 = null;
                    }
                    char c4 = 0;
                    int i10 = 0;
                    while (true) {
                        if (i10 >= i5) {
                            i3 = i9;
                            break;
                            break;
                        }
                        String str4 = strArr3[i10];
                        if (!lowerCase4.startsWith(str4)) {
                            i3 = i9;
                            if (!lowerCase4.contains(" " + str4)) {
                                if (translitString4 != null) {
                                    if (!translitString4.startsWith(str4)) {
                                        if (translitString4.contains(" " + str4)) {
                                        }
                                    }
                                }
                                String publicUsername2 = UserObject.getPublicUsername(user5);
                                if (publicUsername2 != null && publicUsername2.startsWith(str4)) {
                                    c4 = 2;
                                }
                            }
                            if (c4 == 0 && user5.phone != null) {
                                if (c4 == 1) {
                                    arrayList4.add(AndroidUtilities.generateSearchName(user5.first_name, user5.last_name, str4));
                                } else {
                                    arrayList4.add(AndroidUtilities.generateSearchName("@" + UserObject.getPublicUsername(user5), null, "@" + str4));
                                }
                                arrayList3.add(user5);
                                break;
                            }
                            i10++;
                            i9 = i3;
                        } else {
                            i3 = i9;
                        }
                        c4 = 1;
                        if (c4 == 0) {
                        }
                        i10++;
                        i9 = i3;
                    }
                } else {
                    i3 = i9;
                    break;
                }
                i9 = i3 + 1;
            }
            updateSearchResults(str, arrayList3, arrayList4, i2);
        }

        private void updateSearchResults(String str, final ArrayList<Object> arrayList, final ArrayList<CharSequence> arrayList2, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$updateSearchResults$3(i, arrayList, arrayList2);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$3(int i, ArrayList arrayList, ArrayList arrayList2) {
            if (i != this.lastSearchId) {
                return;
            }
            if (i != -1 && ChatAttachAlertContactsLayout.this.listView.getAdapter() != ChatAttachAlertContactsLayout.this.searchAdapter) {
                ChatAttachAlertContactsLayout.this.listView.setAdapter(ChatAttachAlertContactsLayout.this.searchAdapter);
            }
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.searchResult.size() + 2;
        }

        public Object getItem(int i) {
            int i2 = i - 1;
            if (i2 < 0 || i2 >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View userCell;
            if (i == 0) {
                userCell = new UserCell(this.mContext, ChatAttachAlertContactsLayout.this.resourcesProvider);
            } else if (i == 1) {
                userCell = new View(this.mContext);
                userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                userCell.setTag(-33024);
            } else {
                userCell = new View(this.mContext);
                userCell.setTag(-33024);
            }
            return new RecyclerListView.Holder(userCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            final TLRPC.User user;
            if (viewHolder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                boolean z = i != getItemCount() + (-2);
                Object item = getItem(i);
                if (item instanceof ContactsController.Contact) {
                    final ContactsController.Contact contact = (ContactsController.Contact) item;
                    user = contact.user;
                    if (user == null) {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData((TLRPC.User) null, this.searchResultNames.get(i - 1), new UserCell.CharSequenceCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda0
                            @Override // org.telegram.ui.Components.ChatAttachAlertContactsLayout.UserCell.CharSequenceCallback
                            public final CharSequence run() {
                                return ChatAttachAlertContactsLayout.ShareSearchAdapter.$r8$lambda$ohVs6ue6ETDwcw3OZ9SHGpDXFzI(contact);
                            }
                        }, z);
                        user = null;
                    }
                } else {
                    user = (TLRPC.User) item;
                }
                if (user != null) {
                    userCell.setData(user, this.searchResultNames.get(i - 1), new UserCell.CharSequenceCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$ShareSearchAdapter$$ExternalSyntheticLambda1
                        @Override // org.telegram.ui.Components.ChatAttachAlertContactsLayout.UserCell.CharSequenceCallback
                        public final CharSequence run() {
                            return PhoneFormat.getInstance().format("+" + user.phone);
                        }
                    }, z);
                }
                userCell.setChecked(ChatAttachAlertContactsLayout.this.selectedContacts.containsKey(ListItemID.of(item)), false);
            }
        }

        public static /* synthetic */ CharSequence $r8$lambda$ohVs6ue6ETDwcw3OZ9SHGpDXFzI(ContactsController.Contact contact) {
            return contact.phones.isEmpty() ? _UrlKt.FRAGMENT_ENCODE_SET : PhoneFormat.getInstance().format(contact.phones.get(0));
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 1;
            }
            return i == getItemCount() - 1 ? 2 : 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertContactsLayout.this.updateEmptyView();
        }
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 0) {
            this.fadeView.setAlpha(f);
            this.fadeView.setVisibility(f > 0.0f ? 0 : 4);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertContactsLayout$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                this.f$0.lambda$getThemeDescriptions$4();
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        int i = Theme.key_dialogTextGray2;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, i));
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

    public /* synthetic */ void lambda$getThemeDescriptions$4() {
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
        FragmentSearchField fragmentSearchField = this.searchField;
        if (fragmentSearchField != null) {
            fragmentSearchField.updateColors();
        }
    }
}
