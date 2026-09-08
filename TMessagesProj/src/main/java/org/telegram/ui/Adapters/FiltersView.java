package org.telegram.ui.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.AvatarCornerType;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.gms.cast.CastStatusCodes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.time.DurationKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;

public class FiltersView extends RecyclerListView {
    DiffUtil.Callback diffUtilsCallback;
    public boolean drawDivider;
    LinearLayoutManager layoutManager;
    private ArrayList<MediaFilterData> oldItems;
    private ArrayList<MediaFilterData> usersFilters;
    public static final MediaFilterData[] filters = {new MediaFilterData(R.drawable.search_media_filled, R.string.SharedMediaTab2, new TLRPC.TL_inputMessagesFilterPhotoVideo(), 0), new MediaFilterData(R.drawable.search_links_filled, R.string.SharedLinksTab2, new TLRPC.TL_inputMessagesFilterUrl(), 2), new MediaFilterData(R.drawable.search_files_filled, R.string.SharedFilesTab2, new TLRPC.TL_inputMessagesFilterDocument(), 1), new MediaFilterData(R.drawable.search_music_filled, R.string.SharedMusicTab2, new TLRPC.TL_inputMessagesFilterMusic(), 3), new MediaFilterData(R.drawable.search_voice_filled, R.string.SharedVoiceTab2, new TLRPC.TL_inputMessagesFilterRoundVoice(), 5)};
    private static final Pattern yearPatter = Pattern.compile("20[0-9]{1,2}");
    private static final Pattern monthYearOrDayPatter = Pattern.compile("(\\w{3,}) ([0-9]{0,4})");
    private static final Pattern yearOrDayAndMonthPatter = Pattern.compile("([0-9]{0,4}) (\\w{2,})");
    private static final Pattern shortDate = Pattern.compile("^([0-9]{1,4})(\\.| |/|\\-)([0-9]{1,4})$");
    private static final Pattern longDate = Pattern.compile("^([0-9]{1,2})(\\.| |/|\\-)([0-9]{1,2})(\\.| |/|\\-)([0-9]{1,4})$");
    private static final int[] numberOfDaysEachMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public FiltersView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        this.usersFilters = new ArrayList<>();
        this.oldItems = new ArrayList<>();
        this.drawDivider = true;
        this.diffUtilsCallback = new DiffUtil.Callback() { // from class: org.telegram.ui.Adapters.FiltersView.4
            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public boolean areContentsTheSame(int i, int i2) {
                return true;
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public int getOldListSize() {
                return FiltersView.this.oldItems.size();
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public int getNewListSize() {
                return FiltersView.this.usersFilters.size();
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public boolean areItemsTheSame(int i, int i2) {
                MediaFilterData mediaFilterData = (MediaFilterData) FiltersView.this.oldItems.get(i);
                MediaFilterData mediaFilterData2 = (MediaFilterData) FiltersView.this.usersFilters.get(i2);
                if (mediaFilterData.isSameType(mediaFilterData2)) {
                    int i3 = mediaFilterData.filterType;
                    if (i3 == 4) {
                        TLObject tLObject = mediaFilterData.chat;
                        if (tLObject instanceof TLRPC.User) {
                            TLObject tLObject2 = mediaFilterData2.chat;
                            if (tLObject2 instanceof TLRPC.User) {
                                return ((TLRPC.User) tLObject).id == ((TLRPC.User) tLObject2).id;
                            }
                        }
                        if (tLObject instanceof TLRPC.Chat) {
                            TLObject tLObject3 = mediaFilterData2.chat;
                            return (tLObject3 instanceof TLRPC.Chat) && ((TLRPC.Chat) tLObject).id == ((TLRPC.Chat) tLObject3).id;
                        }
                    } else {
                        if (i3 == 6) {
                            return mediaFilterData.title.equals(mediaFilterData2.title);
                        }
                        if (i3 == 7) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) { // from class: org.telegram.ui.Adapters.FiltersView.1
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(recycler, state, accessibilityNodeInfoCompat);
                if (FiltersView.this.isEnabled()) {
                    return;
                }
                accessibilityNodeInfoCompat.setVisibleToUser(false);
            }
        };
        this.layoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(0);
        setLayoutManager(this.layoutManager);
        setAdapter(new Adapter());
        addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Adapters.FiltersView.2
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                super.getItemOffsets(rect, view, recyclerView, state);
                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                rect.left = AndroidUtilities.dp(8.0f);
                if (childAdapterPosition == state.getItemCount() - 1) {
                    rect.right = AndroidUtilities.dp(10.0f);
                }
                if (childAdapterPosition == 0) {
                    rect.left = AndroidUtilities.dp(10.0f);
                }
            }
        });
        setItemAnimator(new DefaultItemAnimator() { // from class: org.telegram.ui.Adapters.FiltersView.3
            private final float scaleFrom = 0.0f;

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public long getAddAnimationDelay(long j, long j2, long j3) {
                return 0L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getAddDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public long getMoveAnimationDelay() {
                return 0L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getMoveDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
            public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
                boolean zAnimateAdd = super.animateAdd(viewHolder);
                if (zAnimateAdd) {
                    viewHolder.itemView.setScaleX(0.0f);
                    viewHolder.itemView.setScaleY(0.0f);
                }
                return zAnimateAdd;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void animateRemoveImpl(final RecyclerView.ViewHolder viewHolder) {
                final View view = viewHolder.itemView;
                final ViewPropertyAnimator viewPropertyAnimatorAnimate = view.animate();
                this.mRemoveAnimations.add(viewHolder);
                viewPropertyAnimatorAnimate.setDuration(getRemoveDuration()).alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Adapters.FiltersView.3.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        dispatchRemoveStarting(viewHolder);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        viewPropertyAnimatorAnimate.setListener(null);
                        view.setAlpha(1.0f);
                        view.setTranslationX(0.0f);
                        view.setTranslationY(0.0f);
                        view.setScaleX(1.0f);
                        view.setScaleY(1.0f);
                        dispatchRemoveFinished(viewHolder);
                        ((DefaultItemAnimator) AnonymousClass3.this).mRemoveAnimations.remove(viewHolder);
                        dispatchFinishedWhenDone();
                    }
                }).start();
            }
        });
        setWillNotDraw(false);
        setHideIfEmpty(false);
        setSelectorRadius(AndroidUtilities.dp(28.0f));
        setSelectorDrawableColor(getThemedColor(Theme.key_listSelector));
    }

    public MediaFilterData getFilterAt(int i) {
        if (this.usersFilters.isEmpty()) {
            return filters[i];
        }
        return this.usersFilters.get(i);
    }

    public void setUsersAndDates(ArrayList<Object> arrayList, ArrayList<DateData> arrayList2, boolean z) {
        String name;
        this.oldItems.clear();
        this.oldItems.addAll(this.usersFilters);
        this.usersFilters.clear();
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                Object obj = arrayList.get(i);
                if (obj instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) obj;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == user.id) {
                        name = LocaleController.getString(R.string.SavedMessages);
                    } else {
                        name = ContactsController.formatName(user.first_name, user.last_name, 10);
                    }
                    MediaFilterData mediaFilterData = new MediaFilterData(R.drawable.search_users_filled, name, (TLRPC.MessagesFilter) null, 4);
                    mediaFilterData.setUser(user);
                    this.usersFilters.add(mediaFilterData);
                } else if (obj instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) obj;
                    String str = chat.title;
                    if (str.length() > 12) {
                        str = String.format("%s...", str.substring(0, 10));
                    }
                    MediaFilterData mediaFilterData2 = new MediaFilterData(R.drawable.search_users_filled, str, (TLRPC.MessagesFilter) null, 4);
                    mediaFilterData2.setUser(chat);
                    this.usersFilters.add(mediaFilterData2);
                }
            }
        }
        if (arrayList2 != null) {
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                DateData dateData = arrayList2.get(i2);
                MediaFilterData mediaFilterData3 = new MediaFilterData(R.drawable.search_date_filled, dateData.title, (TLRPC.MessagesFilter) null, 6);
                mediaFilterData3.setDate(dateData);
                this.usersFilters.add(mediaFilterData3);
            }
        }
        if (z) {
            this.usersFilters.add(new MediaFilterData(R.drawable.chats_archive, R.string.ArchiveSearchFilter, (TLRPC.MessagesFilter) null, 7));
        }
        if (getAdapter() != null) {
            UpdateCallback updateCallback = new UpdateCallback(getAdapter());
            DiffUtil.calculateDiff(this.diffUtilsCallback).dispatchUpdatesTo(updateCallback);
            if (this.usersFilters.isEmpty() || !updateCallback.changed) {
                return;
            }
            this.layoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    public static void fillTipDates(String str, ArrayList<DateData> arrayList) {
        arrayList.clear();
        if (str == null) {
            return;
        }
        String strTrim = str.trim();
        if (strTrim.length() < 3) {
            return;
        }
        if (LocaleController.getString(R.string.SearchTipToday).toLowerCase().startsWith(strTrim) || "today".startsWith(strTrim)) {
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(1);
            int i2 = calendar.get(2);
            int i3 = calendar.get(5);
            calendar.set(i, i2, i3, 0, 0, 0);
            long timeInMillis = calendar.getTimeInMillis();
            calendar.set(i, i2, i3 + 1, 0, 0, 0);
            arrayList.add(new DateData(LocaleController.getString(R.string.SearchTipToday), timeInMillis, calendar.getTimeInMillis() - 1));
            return;
        }
        if (LocaleController.getString(R.string.SearchTipYesterday).toLowerCase().startsWith(strTrim) || "yesterday".startsWith(strTrim)) {
            Calendar calendar2 = Calendar.getInstance();
            int i4 = calendar2.get(1);
            int i5 = calendar2.get(2);
            int i6 = calendar2.get(5);
            calendar2.set(i4, i5, i6, 0, 0, 0);
            long timeInMillis2 = calendar2.getTimeInMillis() - DurationKt.MILLIS_IN_DAY;
            calendar2.set(i4, i5, i6 + 1, 0, 0, 0);
            arrayList.add(new DateData(LocaleController.getString(R.string.SearchTipYesterday), timeInMillis2, calendar2.getTimeInMillis() - 86400001));
            return;
        }
        int dayOfWeek = getDayOfWeek(strTrim);
        if (dayOfWeek >= 0) {
            Calendar calendar3 = Calendar.getInstance();
            long timeInMillis3 = calendar3.getTimeInMillis();
            calendar3.set(7, dayOfWeek);
            if (calendar3.getTimeInMillis() > timeInMillis3) {
                calendar3.setTimeInMillis(calendar3.getTimeInMillis() - 604800000);
            }
            int i7 = calendar3.get(1);
            int i8 = calendar3.get(2);
            int i9 = calendar3.get(5);
            calendar3.set(i7, i8, i9, 0, 0, 0);
            long timeInMillis4 = calendar3.getTimeInMillis();
            calendar3.set(i7, i8, i9 + 1, 0, 0, 0);
            arrayList.add(new DateData(LocaleController.getInstance().getFormatterWeekLong().format(timeInMillis4), timeInMillis4, calendar3.getTimeInMillis() - 1));
            return;
        }
        Matcher matcher = shortDate.matcher(strTrim);
        if (matcher.matches()) {
            String strGroup = matcher.group(1);
            String strGroup2 = matcher.group(3);
            int i10 = Integer.parseInt(strGroup);
            int i11 = Integer.parseInt(strGroup2);
            if (i10 <= 0 || i10 > 31) {
                if (i10 < 2013 || i11 > 12) {
                    return;
                }
                createForMonthYear(arrayList, i11 - 1, i10);
                return;
            }
            if (i11 >= 2013 && i10 <= 12) {
                createForMonthYear(arrayList, i10 - 1, i11);
                return;
            } else {
                if (i11 <= 12) {
                    createForDayMonth(arrayList, i10 - 1, i11 - 1);
                    return;
                }
                return;
            }
        }
        Matcher matcher2 = longDate.matcher(strTrim);
        if (matcher2.matches()) {
            String strGroup3 = matcher2.group(1);
            String strGroup4 = matcher2.group(3);
            String strGroup5 = matcher2.group(5);
            if (matcher2.group(2).equals(matcher2.group(4))) {
                int i12 = Integer.parseInt(strGroup3);
                int i13 = Integer.parseInt(strGroup4) - 1;
                int i14 = Integer.parseInt(strGroup5);
                if (i14 >= 10 && i14 <= 99) {
                    i14 += CastStatusCodes.AUTHENTICATION_FAILED;
                }
                int i15 = i14;
                int i16 = Calendar.getInstance().get(1);
                if (!validDateForMont(i12 - 1, i13) || i15 < 2013 || i15 > i16) {
                    return;
                }
                Calendar calendar4 = Calendar.getInstance();
                calendar4.set(i15, i13, i12, 0, 0, 0);
                long timeInMillis5 = calendar4.getTimeInMillis();
                calendar4.set(i15, i13, i12 + 1, 0, 0, 0);
                arrayList.add(new DateData(LocaleController.getInstance().getFormatterYearMax().format(timeInMillis5), timeInMillis5, calendar4.getTimeInMillis() - 1));
                return;
            }
            return;
        }
        if (yearPatter.matcher(strTrim).matches()) {
            int iIntValue = Integer.valueOf(strTrim).intValue();
            int i17 = Calendar.getInstance().get(1);
            if (iIntValue >= 2013) {
                if (iIntValue <= i17) {
                    Calendar calendar5 = Calendar.getInstance();
                    calendar5.set(iIntValue, 0, 1, 0, 0, 0);
                    long timeInMillis6 = calendar5.getTimeInMillis();
                    calendar5.set(iIntValue + 1, 0, 1, 0, 0, 0);
                    arrayList.add(new DateData(Integer.toString(iIntValue), timeInMillis6, calendar5.getTimeInMillis() - 1));
                    return;
                }
                return;
            }
            for (int i18 = i17; i18 >= 2013; i18--) {
                Calendar calendar6 = Calendar.getInstance();
                calendar6.set(i18, 0, 1, 0, 0, 0);
                long timeInMillis7 = calendar6.getTimeInMillis();
                calendar6.set(i18 + 1, 0, 1, 0, 0, 0);
                arrayList.add(new DateData(Integer.toString(i18), timeInMillis7, calendar6.getTimeInMillis() - 1));
            }
            return;
        }
        Matcher matcher3 = monthYearOrDayPatter.matcher(strTrim);
        if (matcher3.matches()) {
            String strGroup6 = matcher3.group(1);
            String strGroup7 = matcher3.group(2);
            int month = getMonth(strGroup6);
            if (month >= 0) {
                int iIntValue2 = Integer.valueOf(strGroup7).intValue();
                if (iIntValue2 > 0 && iIntValue2 <= 31) {
                    createForDayMonth(arrayList, iIntValue2 - 1, month);
                    return;
                } else if (iIntValue2 >= 2013) {
                    createForMonthYear(arrayList, month, iIntValue2);
                    return;
                }
            }
        }
        Matcher matcher4 = yearOrDayAndMonthPatter.matcher(strTrim);
        if (matcher4.matches()) {
            String strGroup8 = matcher4.group(1);
            int month2 = getMonth(matcher4.group(2));
            if (month2 >= 0) {
                int iIntValue3 = Integer.valueOf(strGroup8).intValue();
                if (iIntValue3 > 0 && iIntValue3 <= 31) {
                    createForDayMonth(arrayList, iIntValue3 - 1, month2);
                    return;
                } else if (iIntValue3 >= 2013) {
                    createForMonthYear(arrayList, month2, iIntValue3);
                }
            }
        }
        if (TextUtils.isEmpty(strTrim) || strTrim.length() <= 2) {
            return;
        }
        int month3 = getMonth(strTrim);
        long timeInMillis8 = Calendar.getInstance().getTimeInMillis();
        if (month3 >= 0) {
            for (int i19 = Calendar.getInstance().get(1); i19 >= 2013; i19--) {
                Calendar calendar7 = Calendar.getInstance();
                calendar7.set(i19, month3, 1, 0, 0, 0);
                long timeInMillis9 = calendar7.getTimeInMillis();
                if (timeInMillis9 <= timeInMillis8) {
                    calendar7.add(2, 1);
                    arrayList.add(new DateData(LocaleController.getInstance().getFormatterMonthYear().format(timeInMillis9), timeInMillis9, calendar7.getTimeInMillis() - 1));
                }
            }
        }
    }

    private static void createForMonthYear(ArrayList<DateData> arrayList, int i, int i2) {
        int i3 = Calendar.getInstance().get(1);
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        if (i2 < 2013 || i2 > i3) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(i2, i, 1, 0, 0, 0);
        long timeInMillis2 = calendar.getTimeInMillis();
        if (timeInMillis2 > timeInMillis) {
            return;
        }
        calendar.add(2, 1);
        arrayList.add(new DateData(LocaleController.getInstance().getFormatterMonthYear().format(timeInMillis2), timeInMillis2, calendar.getTimeInMillis() - 1));
    }

    private static void createForDayMonth(ArrayList<DateData> arrayList, int i, int i2) {
        if (validDateForMont(i, i2)) {
            int i3 = Calendar.getInstance().get(1);
            long timeInMillis = Calendar.getInstance().getTimeInMillis();
            GregorianCalendar gregorianCalendar = (GregorianCalendar) Calendar.getInstance();
            for (int i4 = i3; i4 >= 2013; i4--) {
                if (i2 != 1 || i != 28 || gregorianCalendar.isLeapYear(i4)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(i4, i2, i + 1, 0, 0, 0);
                    long timeInMillis2 = calendar.getTimeInMillis();
                    if (timeInMillis2 <= timeInMillis) {
                        calendar.set(i4, i2, i + 2, 0, 0, 0);
                        long timeInMillis3 = calendar.getTimeInMillis() - 1;
                        if (i4 == i3) {
                            arrayList.add(new DateData(LocaleController.getInstance().getFormatterDayMonth().format(timeInMillis2), timeInMillis2, timeInMillis3));
                        } else {
                            arrayList.add(new DateData(LocaleController.getInstance().getFormatterYearMax().format(timeInMillis2), timeInMillis2, timeInMillis3));
                        }
                    }
                }
            }
        }
    }

    private static boolean validDateForMont(int i, int i2) {
        return i2 >= 0 && i2 < 12 && i >= 0 && i < numberOfDaysEachMonth[i2];
    }

    public static int getDayOfWeek(String str) {
        Calendar calendar = Calendar.getInstance();
        if (str.length() <= 3) {
            return -1;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        for (int i = 0; i < 7; i++) {
            calendar.set(7, i);
            if (LocaleController.getInstance().getFormatterWeekLong().format(calendar.getTime()).toLowerCase().startsWith(str) || simpleDateFormat.format(calendar.getTime()).toLowerCase().startsWith(str)) {
                return i;
            }
        }
        return -1;
    }

    public static int getMonth(String str) {
        String[] strArr = {LocaleController.getString(R.string.January).toLowerCase(), LocaleController.getString(R.string.February).toLowerCase(), LocaleController.getString(R.string.March).toLowerCase(), LocaleController.getString(R.string.April).toLowerCase(), LocaleController.getString(R.string.May).toLowerCase(), LocaleController.getString(R.string.June).toLowerCase(), LocaleController.getString(R.string.July).toLowerCase(), LocaleController.getString(R.string.August).toLowerCase(), LocaleController.getString(R.string.September).toLowerCase(), LocaleController.getString(R.string.October).toLowerCase(), LocaleController.getString(R.string.November).toLowerCase(), LocaleController.getString(R.string.December).toLowerCase()};
        String[] strArr2 = new String[12];
        Calendar calendar = Calendar.getInstance();
        for (int i = 1; i <= 12; i++) {
            calendar.set(0, 0, 0, 0, 0, 0);
            calendar.set(2, i);
            strArr2[i - 1] = calendar.getDisplayName(2, 2, Locale.ENGLISH).toLowerCase();
        }
        for (int i2 = 0; i2 < 12; i2++) {
            if (strArr2[i2].startsWith(str) || strArr[i2].startsWith(str)) {
                return i2;
            }
        }
        return -1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.drawDivider) {
            canvas.drawRect(0.0f, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight(), Theme.dividerPaint);
        }
    }

    public void updateColors() {
        getRecycledViewPool().clear();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof FilterView) {
                ((FilterView) childAt).updateColors();
            }
        }
        for (int i2 = 0; i2 < getCachedChildCount(); i2++) {
            View cachedChildAt = getCachedChildAt(i2);
            if (cachedChildAt instanceof FilterView) {
                ((FilterView) cachedChildAt).updateColors();
            }
        }
        for (int i3 = 0; i3 < getAttachedScrapChildCount(); i3++) {
            View attachedScrapChildAt = getAttachedScrapChildAt(i3);
            if (attachedScrapChildAt instanceof FilterView) {
                ((FilterView) attachedScrapChildAt).updateColors();
            }
        }
        setSelectorDrawableColor(getThemedColor(Theme.key_listSelector));
    }

    public class Adapter extends RecyclerListView.SelectionAdapter {
        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        private Adapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ViewHolder viewHolder = FiltersView.this.new ViewHolder(new FilterView(viewGroup.getContext(), ((RecyclerListView) FiltersView.this).resourcesProvider));
            viewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(-2, AndroidUtilities.dp(30.0f)));
            return viewHolder;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ((ViewHolder) viewHolder).filterView.setData((MediaFilterData) FiltersView.this.usersFilters.get(i));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return FiltersView.this.usersFilters.size();
        }
    }

    public static class FilterView extends FrameLayout {
        BackupImageView avatarImageView;
        MediaFilterData data;
        private final Theme.ResourcesProvider resourcesProvider;
        CombinedDrawable thumbDrawable;
        TextView titleView;

        public FilterView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(30, 30.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 14.0f);
            addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 16, 36.0f, 0.0f, 14.0f, 0.0f));
            updateColors();
        }

        public void updateColors() {
            setBackground(Theme.createRoundRectDrawable(ExteraConfig.getAvatarCorners(32.0f), getThemedColor(Theme.key_groupcreate_spanBackground)));
            this.titleView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            CombinedDrawable combinedDrawable = this.thumbDrawable;
            if (combinedDrawable != null) {
                if (this.data.filterType == 7) {
                    Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor(Theme.key_featuredStickers_addButton), false);
                    Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_featuredStickers_buttonText), true);
                } else {
                    Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor(Theme.key_featuredStickers_addButton), false);
                    Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_featuredStickers_buttonText), true);
                }
            }
        }

        public void setData(MediaFilterData mediaFilterData) {
            this.data = mediaFilterData;
            this.avatarImageView.getImageReceiver().clearImage();
            if (mediaFilterData.filterType == 7) {
                CombinedDrawable combinedDrawableCreateCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_archive);
                this.thumbDrawable = combinedDrawableCreateCircleDrawableWithIcon;
                combinedDrawableCreateCircleDrawableWithIcon.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_featuredStickers_addButton), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_featuredStickers_buttonText), true);
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
                this.titleView.setText(mediaFilterData.title);
                return;
            }
            CombinedDrawable combinedDrawableCreateCircleDrawableWithIcon2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), mediaFilterData.iconResFilled);
            this.thumbDrawable = combinedDrawableCreateCircleDrawableWithIcon2;
            int i = Theme.key_featuredStickers_addButton;
            Theme.setCombinedDrawableColor(combinedDrawableCreateCircleDrawableWithIcon2, getThemedColor(i), false);
            CombinedDrawable combinedDrawable = this.thumbDrawable;
            int i2 = Theme.key_featuredStickers_buttonText;
            Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor(i2), true);
            if (mediaFilterData.filterType == 4) {
                TLObject tLObject = mediaFilterData.chat;
                if (tLObject instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) tLObject;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == user.id) {
                        CombinedDrawable combinedDrawableCreateCircleDrawableWithIcon3 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_saved);
                        combinedDrawableCreateCircleDrawableWithIcon3.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                        Theme.setCombinedDrawableColor(combinedDrawableCreateCircleDrawableWithIcon3, getThemedColor(i), false);
                        Theme.setCombinedDrawableColor(combinedDrawableCreateCircleDrawableWithIcon3, getThemedColor(i2), true);
                        this.avatarImageView.setImageDrawable(combinedDrawableCreateCircleDrawableWithIcon3);
                    } else {
                        this.avatarImageView.getImageReceiver().setRoundRadius(ExteraConfig.getAvatarCorners(32.0f));
                        this.avatarImageView.getImageReceiver().setForUserOrChat(user, this.thumbDrawable);
                    }
                } else if (tLObject instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) tLObject;
                    this.avatarImageView.getImageReceiver().setRoundRadius(ExteraConfig.getAvatarCorners(32.0f, false, ChatObject.isCommunity(chat) ? AvatarCornerType.COMMUNITY : AvatarCornerType.DEFAULT));
                    this.avatarImageView.getImageReceiver().setForUserOrChat(chat, this.thumbDrawable);
                }
            } else {
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
            }
            this.titleView.setText(mediaFilterData.title);
        }

        public int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        FilterView filterView;

        public ViewHolder(FilterView filterView) {
            super(filterView);
            this.filterView = filterView;
        }
    }

    public static class MediaFilterData {
        public TLObject chat;
        public DateData dateData;
        public TLRPC.MessagesFilter filter;
        public int filterType;
        public int iconResFilled;
        public ReactionsLayoutInBubble.VisibleReaction reaction;
        public boolean removable = true;
        private String title;
        public int titleResId;

        public MediaFilterData(int i, String str, TLRPC.MessagesFilter messagesFilter, int i2) {
            this.iconResFilled = i;
            this.title = str;
            this.filter = messagesFilter;
            this.filterType = i2;
        }

        public MediaFilterData(int i, int i2, TLRPC.MessagesFilter messagesFilter, int i3) {
            this.iconResFilled = i;
            this.titleResId = i2;
            this.filter = messagesFilter;
            this.filterType = i3;
        }

        public String getTitle() {
            String str = this.title;
            return str != null ? str : LocaleController.getString(this.titleResId);
        }

        public void setUser(TLObject tLObject) {
            this.chat = tLObject;
        }

        public boolean isSameType(MediaFilterData mediaFilterData) {
            if (this.filterType == mediaFilterData.filterType) {
                return true;
            }
            return isMedia() && mediaFilterData.isMedia();
        }

        public boolean isMedia() {
            int i = this.filterType;
            return i == 0 || i == 1 || i == 2 || i == 3 || i == 5;
        }

        public void setDate(DateData dateData) {
            this.dateData = dateData;
        }
    }

    public static class DateData {
        public final long maxDate;
        public final long minDate;
        public final String title;

        private DateData(String str, long j, long j2) {
            this.title = str;
            this.minDate = j;
            this.maxDate = j2;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this, 0, null, null, null, null, Theme.key_graySection));
        arrayList.add(new ThemeDescription(this, 0, null, null, null, null, Theme.key_graySectionText));
        return arrayList;
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return false;
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }

    public static class UpdateCallback implements ListUpdateCallback {
        final RecyclerView.Adapter adapter;
        boolean changed;

        private UpdateCallback(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onInserted(int i, int i2) {
            this.changed = true;
            this.adapter.notifyItemRangeInserted(i, i2);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onRemoved(int i, int i2) {
            this.changed = true;
            this.adapter.notifyItemRangeRemoved(i, i2);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onMoved(int i, int i2) {
            this.changed = true;
            this.adapter.notifyItemMoved(i, i2);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onChanged(int i, int i2, Object obj) {
            this.adapter.notifyItemRangeChanged(i, i2, obj);
        }
    }
}
