package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.regdate.RegDateController;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Consumer;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AvatarsDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.Text;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;

public class UserInfoCell extends View implements NotificationCenter.NotificationCenterDelegate {
    private boolean animating;
    private int backgroundHeight;
    private MessagesController.CommonChatsList commonChats;
    private final int currentAccount;
    private long dialogId;
    private Text footer;
    private final ButtonBounce fullBounce;
    private final RectF fullBounds;
    private final Drawable groupsArrow;
    private final AvatarsDrawable groupsAvatars;
    private final ButtonBounce groupsBounce;
    private final RectF groupsBounds;
    private final Drawable groupsRipple;
    private Row groupsRow;
    private float height;
    private final Theme.ResourcesProvider resourcesProvider;
    private final ArrayList<Row> rows;
    private float rowsKeysWidth;
    private float rowsValuesWidth;
    private float rowsWidth;
    private Text subtitle;
    private final int textColor;
    private boolean timestampSent;
    private Text title;
    private float viewTop;
    private float width;

    public static /* synthetic */ void m7259$r8$lambda$YM6n_lhdmcTOaVmJNI5iXpaJR4(String str) {
    }

    public class Row {
        public boolean avatars;
        public final RectF bounds = new RectF();
        public Text key;
        public Text value;

        public Row(CharSequence charSequence, CharSequence charSequence2, boolean z) {
            this.key = new Text(charSequence, 12.0f);
            this.value = new Text(charSequence2, 12.0f, AndroidUtilities.bold());
            this.avatars = z;
        }
    }

    private Row addRow(CharSequence charSequence, CharSequence charSequence2, boolean z) {
        if (!this.rows.isEmpty()) {
            this.height += AndroidUtilities.dp(7.0f);
        }
        Row row = new Row(charSequence, charSequence2, z);
        this.rows.add(row);
        this.height += AndroidUtilities.dp(14.0f);
        this.rowsKeysWidth = Math.max(this.rowsKeysWidth, row.key.getCurrentWidth());
        this.rowsValuesWidth = Math.max(this.rowsValuesWidth, row.value.getCurrentWidth() + (z ? AndroidUtilities.dp(38.0f) : 0));
        return row;
    }

    public static long getTimestamp(String str) {
        String[] strArrSplit = str.split("\\.");
        if (strArrSplit.length != 2) {
            return 0L;
        }
        int i = Integer.parseInt(strArrSplit[0]);
        int i2 = Integer.parseInt(strArrSplit[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(i2, i - 1, 1, 0, 0, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis() / 1000;
    }

    public static String displayDate(String str) {
        return LocaleController.formatYearMont(getTimestamp(str), true);
    }

    public UserInfoCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.rows = new ArrayList<>();
        this.fullBounds = new RectF();
        this.fullBounce = new ButtonBounce(this);
        this.groupsBounds = new RectF();
        this.groupsBounce = new ButtonBounce(this);
        AvatarsDrawable avatarsDrawable = new AvatarsDrawable(this, false);
        this.groupsAvatars = avatarsDrawable;
        this.timestampSent = false;
        this.currentAccount = i;
        this.resourcesProvider = resourcesProvider;
        int color = Theme.getColor(Theme.key_chat_serviceText, resourcesProvider);
        this.textColor = color;
        Drawable drawableCreateRadSelectorDrawable = Theme.createRadSelectorDrawable(822083583, 8, 8);
        this.groupsRipple = drawableCreateRadSelectorDrawable;
        drawableCreateRadSelectorDrawable.setCallback(this);
        avatarsDrawable.width = AndroidUtilities.dp(50.0f);
        avatarsDrawable.height = AndroidUtilities.dp(13.0f);
        avatarsDrawable.drawStoriesCircle = false;
        avatarsDrawable.setSize(AndroidUtilities.dp(13.0f));
        avatarsDrawable.setAvatarsTextSize(AndroidUtilities.dp(18.0f));
        Drawable drawableMutate = context.getResources().getDrawable(R.drawable.msg_mini_forumarrow).mutate();
        this.groupsArrow = drawableMutate;
        drawableMutate.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        return drawable == this.groupsRipple || super.verifyDrawable(drawable);
    }

    public void set(long j, TLRPC.PeerSettings peerSettings) {
        TL_bots.botVerification botverification;
        AvatarsDrawable avatarsDrawable;
        this.dialogId = j;
        this.width = 0.0f;
        this.height = 0.0f;
        this.rowsKeysWidth = 0.0f;
        this.rowsValuesWidth = 0.0f;
        this.rows.clear();
        int i = (int) (AndroidUtilities.displaySize.x * 0.95f);
        this.height += AndroidUtilities.dp(14.0f);
        Text text = new Text(DialogObject.getName(j), 14.0f, AndroidUtilities.bold());
        this.title = text;
        this.height += text.getHeight() + AndroidUtilities.dp(3.0f);
        Text text2 = new Text(LocaleController.getString(ContactsController.getInstance(this.currentAccount).isContact(j) ? R.string.ContactInfoIsContact : R.string.ContactInfoIsNotContact), 14.0f);
        this.subtitle = text2;
        this.height += text2.getHeight() + AndroidUtilities.dp(11.0f);
        if (peerSettings != null && peerSettings.phone_country != null) {
            addRow(LocaleController.getString(R.string.ContactInfoPhone), LocaleController.getCountryWithFlag(peerSettings.phone_country, 12, R.string.ContactInfoPhoneFragment), false);
        }
        if (peerSettings != null && peerSettings.registration_month != null) {
            if (!this.timestampSent) {
                RegDateController.getInstance(this.currentAccount).addRegistrationDate(j, getTimestamp(peerSettings.registration_month), new Consumer() { // from class: org.telegram.ui.Cells.UserInfoCell$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        UserInfoCell.m7259$r8$lambda$YM6n_lhdmcTOaVmJNI5iXpaJR4((String) obj);
                    }
                });
                this.timestampSent = true;
            }
            addRow(LocaleController.getString(R.string.ContactInfoRegistration), displayDate(peerSettings.registration_month), false);
        }
        TLRPC.User user = j < 0 ? null : MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
        TLRPC.UserFull userFull = j < 0 ? null : MessagesController.getInstance(this.currentAccount).getUserFull(j);
        if (userFull == null && j > 0) {
            MessagesController.getInstance(this.currentAccount).loadUserInfo(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j)), true, 0);
        }
        if (userFull != null) {
            MessagesController.CommonChatsList commonChats = MessagesController.getInstance(this.currentAccount).getCommonChats(j);
            this.commonChats = commonChats;
            int iMax = Math.max(userFull.common_chats_count, commonChats.getCount());
            if (iMax > 0) {
                this.groupsRow = addRow(LocaleController.getString(R.string.ContactInfoCommonGroups), LocaleController.formatPluralString("Groups", iMax, new Object[0]), true);
                this.groupsAvatars.setCount(Math.min(3, this.commonChats.chats.size()));
                int i2 = 0;
                while (true) {
                    int iMin = Math.min(3, this.commonChats.chats.size());
                    avatarsDrawable = this.groupsAvatars;
                    if (i2 >= iMin) {
                        break;
                    }
                    avatarsDrawable.setObject(i2, this.currentAccount, this.commonChats.chats.get(i2));
                    i2++;
                }
                avatarsDrawable.commitTransition(true);
            } else {
                this.commonChats = null;
                this.groupsRow = null;
            }
        } else {
            this.commonChats = null;
            this.groupsRow = null;
        }
        this.rowsWidth = this.rowsKeysWidth + AndroidUtilities.dp(7.66f) + this.rowsValuesWidth;
        if (user != null && !user.verified && !UserObject.isService(user.id)) {
            BadgesController badgesController = BadgesController.INSTANCE;
            if (badgesController.isDeveloper(user)) {
                BadgeDTO badge = badgesController.getBadge(user);
                if (badge != null) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("i  ");
                    this.footer = new Text(spannableStringBuilder, 12.0f);
                    spannableStringBuilder.setSpan(new AnimatedEmojiSpan(badge.getDocumentId(), this.footer.getFontMetricsInt()), 0, 1, 33);
                    spannableStringBuilder.append((CharSequence) AndroidUtilities.replaceTags(badge.getText() != null ? badge.getText() : LocaleController.getString(R.string.DeveloperCompact)));
                    Text textMultiline = new Text(spannableStringBuilder, 12.0f).align(Layout.Alignment.ALIGN_CENTER).multiline(5);
                    Point point = AndroidUtilities.displaySize;
                    this.footer = textMultiline.setMaxWidth(Math.min(point.x, point.y) * 0.5f).supportAnimatedEmojis(this);
                    this.height += AndroidUtilities.dp(12.0f) + this.footer.getHeight() + AndroidUtilities.dp(15.33f);
                } else {
                    this.footer = null;
                    this.height += AndroidUtilities.dp(14.0f);
                }
            } else if (user.bot_verification_icon == 0) {
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder("i  ");
                ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.filled_info);
                coloredImageSpan.setScale(0.55f, -0.55f);
                coloredImageSpan.translate(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(-1.0f));
                spannableStringBuilder2.setSpan(coloredImageSpan, 0, 1, 33);
                spannableStringBuilder2.append((CharSequence) LocaleController.getString(R.string.ContactInfoNotVerified));
                this.footer = new Text(spannableStringBuilder2, 12.0f);
                this.height += AndroidUtilities.dp(12.0f) + this.footer.getHeight() + AndroidUtilities.dp(15.33f);
            } else if (userFull != null && (botverification = userFull.bot_verification) != null) {
                SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder("i  ");
                this.footer = new Text(spannableStringBuilder3, 12.0f);
                spannableStringBuilder3.setSpan(new AnimatedEmojiSpan(botverification.icon, this.footer.getFontMetricsInt()), 0, 1, 33);
                spannableStringBuilder3.append((CharSequence) botverification.description);
                Text textMultiline2 = new Text(spannableStringBuilder3, 12.0f).align(Layout.Alignment.ALIGN_CENTER).multiline(5);
                Point point2 = AndroidUtilities.displaySize;
                this.footer = textMultiline2.setMaxWidth(Math.min(point2.x, point2.y) * 0.5f).supportAnimatedEmojis(this);
                this.height += AndroidUtilities.dp(12.0f) + this.footer.getHeight() + AndroidUtilities.dp(15.33f);
            } else {
                this.footer = null;
                this.height += AndroidUtilities.dp(14.0f);
            }
        } else {
            this.footer = null;
            this.height += AndroidUtilities.dp(14.0f);
        }
        float fMax = Math.max(this.width, this.title.getWidth());
        this.width = fMax;
        float fMax2 = Math.max(fMax, this.subtitle.getWidth());
        this.width = fMax2;
        float fMax3 = Math.max(fMax2, this.rowsWidth);
        this.width = fMax3;
        this.width = Math.min(fMax3 + AndroidUtilities.dp(32.0f), i);
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        AvatarsDrawable avatarsDrawable;
        int i3 = 0;
        if (i == NotificationCenter.userInfoDidLoad) {
            long jLongValue = ((Long) objArr[0]).longValue();
            long j = this.dialogId;
            if (jLongValue == j) {
                set(j, MessagesController.getInstance(this.currentAccount).getPeerSettings(this.dialogId));
                return;
            }
            return;
        }
        if (i == NotificationCenter.commonChatsLoaded && ((Long) objArr[0]).longValue() == this.dialogId) {
            MessagesController.CommonChatsList commonChats = MessagesController.getInstance(this.currentAccount).getCommonChats(this.dialogId);
            this.commonChats = commonChats;
            int count = commonChats.getCount();
            Row row = this.groupsRow;
            if (row == null || count <= 0) {
                set(this.dialogId, MessagesController.getInstance(this.currentAccount).getPeerSettings(this.dialogId));
                requestLayout();
            } else {
                row.value = new Text(LocaleController.formatPluralString("Groups", count, new Object[0]), 12.0f, AndroidUtilities.bold());
                this.groupsAvatars.setCount(Math.min(3, this.commonChats.chats.size()));
                while (true) {
                    int iMin = Math.min(3, this.commonChats.chats.size());
                    avatarsDrawable = this.groupsAvatars;
                    if (i3 >= iMin) {
                        break;
                    }
                    avatarsDrawable.setObject(i3, this.currentAccount, this.commonChats.chats.get(i3));
                    i3++;
                }
                avatarsDrawable.commitTransition(true);
            }
            invalidate();
        }
    }

    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.commonChatsLoaded);
        this.groupsAvatars.onAttachedToWindow();
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.commonChatsLoaded);
        this.groupsAvatars.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), Math.max(0, ((int) this.height) + AndroidUtilities.dp(16.0f)));
    }

    public static boolean isEmpty(TLRPC.PeerSettings peerSettings) {
        if (peerSettings != null) {
            return peerSettings.phone_country == null && peerSettings.registration_month == null;
        }
        return true;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        float f = 2.0f;
        float width = getWidth() / 2.0f;
        this.fullBounds.set((getWidth() - this.width) / 2.0f, (getHeight() - this.height) / 2.0f, (getWidth() + this.width) / 2.0f, (getHeight() + this.height) / 2.0f);
        float scale = this.fullBounce.getScale(0.025f);
        canvas.scale(scale, scale, this.fullBounds.centerX(), this.fullBounds.centerY());
        applyServiceShaderMatrix();
        canvas.drawRoundRect(this.fullBounds, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.getThemePaint("paintChatActionBackground", this.resourcesProvider));
        if (hasGradientService()) {
            canvas.drawRoundRect(this.fullBounds, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.getThemePaint("paintChatActionBackgroundDarken", this.resourcesProvider));
        }
        canvas.translate(0.0f, (getHeight() - this.height) / 2.0f);
        float height = ((getHeight() - this.height) / 2.0f) + 0.0f;
        float f2 = 14.0f;
        canvas.translate(0.0f, AndroidUtilities.dp(14.0f));
        float fDp = height + AndroidUtilities.dp(14.0f);
        this.title.ellipsize(this.width - AndroidUtilities.dp(32.0f)).draw(canvas, width - (this.title.getWidth() / 2.0f), this.title.getHeight() / 2.0f, this.textColor, 1.0f);
        canvas.translate(0.0f, this.title.getHeight() + AndroidUtilities.dp(3.0f));
        float height2 = fDp + this.title.getHeight() + AndroidUtilities.dp(3.0f);
        this.subtitle.ellipsize(this.width - AndroidUtilities.dp(32.0f)).draw(canvas, width - (this.subtitle.getWidth() / 2.0f), this.subtitle.getHeight() / 2.0f, this.textColor, 0.7f);
        canvas.translate(0.0f, this.subtitle.getHeight() + AndroidUtilities.dp(11.0f));
        float height3 = height2 + this.subtitle.getHeight() + AndroidUtilities.dp(11.0f);
        float f3 = width - (this.rowsWidth / 2.0f);
        int i = 0;
        while (i < this.rows.size()) {
            if (i > 0) {
                canvas.translate(0.0f, AndroidUtilities.dp(7.0f));
                height3 += AndroidUtilities.dp(7.0f);
            }
            canvas.save();
            Row row = this.rows.get(i);
            float currentWidth = (this.rowsKeysWidth + f3) - row.key.getCurrentWidth();
            float fDp2 = this.rowsKeysWidth + f3 + AndroidUtilities.dp(7.66f);
            float f4 = f;
            int i2 = i;
            float f5 = f2;
            row.key.ellipsize((fDp2 - currentWidth) - AndroidUtilities.dp(7.66f)).draw(canvas, currentWidth, row.key.getHeight() / f4, this.textColor, 0.7f);
            row.bounds.set(fDp2, height3, fDp2 + row.value.getCurrentWidth() + (row.avatars ? AndroidUtilities.dp(5.0f) + (this.groupsArrow.getIntrinsicWidth() * 0.8f) + this.groupsAvatars.getMaxX() : 0.0f), row.value.getHeight() + height3);
            if (this.groupsRow == row) {
                this.groupsBounds.set(row.bounds);
                this.groupsBounds.inset(-AndroidUtilities.dp(4.0f), -AndroidUtilities.dp(f4));
                float scale2 = this.groupsBounce.getScale(0.025f);
                canvas.scale(scale2, scale2, this.groupsBounds.centerX(), row.value.getHeight() / f4);
                Drawable drawable = this.groupsRipple;
                if (drawable != null) {
                    RectF rectF = this.groupsBounds;
                    drawable.setBounds((int) rectF.left, (int) (rectF.top - height3), (int) rectF.right, (int) (rectF.bottom - height3));
                    this.groupsRipple.draw(canvas);
                }
            }
            row.value.ellipsize((((this.width / f4) + width) - AndroidUtilities.dp(8.0f)) - fDp2).draw(canvas, fDp2, row.value.getHeight() / f4, this.textColor, 1.0f);
            if (row.avatars) {
                canvas.save();
                canvas.translate(fDp2 + row.value.getCurrentWidth() + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(1.0f));
                this.groupsAvatars.onDraw(canvas);
                canvas.translate(this.groupsAvatars.getMaxX() + AndroidUtilities.dp(1.0f), AndroidUtilities.dp(13.0f) / f4);
                Drawable drawable2 = this.groupsArrow;
                drawable2.setBounds(0, (int) (((-drawable2.getIntrinsicHeight()) * 0.8f) / f4), (int) (this.groupsArrow.getIntrinsicWidth() * 0.8f), (int) ((this.groupsArrow.getIntrinsicHeight() * 0.8f) / f4));
                this.groupsArrow.draw(canvas);
                canvas.restore();
            }
            canvas.restore();
            canvas.translate(0.0f, AndroidUtilities.dp(f5));
            height3 += AndroidUtilities.dp(f5);
            i = i2 + 1;
            f2 = f5;
            f = f4;
        }
        float f6 = f;
        if (this.footer != null) {
            canvas.translate(0.0f, AndroidUtilities.dp(12.0f));
            boolean zIsMultiline = this.footer.isMultiline();
            Text text = this.footer;
            if (zIsMultiline) {
                text.draw(canvas, width - (text.getWidth() / f6), 0.0f, this.textColor, 0.7f);
            } else {
                text.ellipsize(this.width - AndroidUtilities.dp(32.0f)).draw(canvas, width - (this.footer.getWidth() / f6), this.footer.getHeight() / f6, this.textColor, 0.7f);
            }
        }
        canvas.restore();
    }

    public void setVisiblePart(float f, int i) {
        if (Math.abs(this.viewTop - f) > 0.01f || i != this.backgroundHeight) {
            invalidate();
        }
        this.backgroundHeight = i;
        this.viewTop = f;
    }

    public void applyServiceShaderMatrix() {
        applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, getX(), this.viewTop);
    }

    public void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(i, i2, f, f2);
        } else {
            Theme.applyServiceShaderMatrix(i, i2, f, f2);
        }
    }

    public boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = this.groupsRow != null && this.groupsBounds.contains(motionEvent.getX(), motionEvent.getY());
        boolean z2 = !z && this.fullBounds.contains(motionEvent.getX(), motionEvent.getY());
        if (motionEvent.getAction() == 0) {
            this.fullBounce.setPressed(z2);
            this.groupsBounce.setPressed(z);
            this.groupsRipple.setState(z ? new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled} : new int[0]);
        } else if (motionEvent.getAction() == 1) {
            if (this.fullBounce.isPressed()) {
                BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
                if (safeLastFragment instanceof ChatActivity) {
                    ((ChatActivity) safeLastFragment).openThisProfile();
                }
            } else if (this.groupsBounce.isPressed()) {
                BaseFragment safeLastFragment2 = LaunchActivity.getSafeLastFragment();
                if (safeLastFragment2 != null) {
                    Bundle bundle = new Bundle();
                    long j = this.dialogId;
                    if (j >= 0) {
                        bundle.putLong("user_id", j);
                    } else {
                        bundle.putLong("chat_id", -j);
                    }
                    bundle.putBoolean("open_common", true);
                    safeLastFragment2.presentFragment(new ProfileActivity(bundle));
                }
                invalidate();
            }
            this.groupsBounce.setPressed(false);
            this.fullBounce.setPressed(false);
            this.groupsRipple.setState(new int[0]);
        } else if (motionEvent.getAction() == 3) {
            this.groupsBounce.setPressed(false);
            this.fullBounce.setPressed(false);
            this.groupsRipple.setState(new int[0]);
        }
        return this.groupsBounce.isPressed() || this.fullBounce.isPressed();
    }

    public boolean animating() {
        return this.animating;
    }

    public void setAnimating(boolean z) {
        this.animating = z;
    }
}
