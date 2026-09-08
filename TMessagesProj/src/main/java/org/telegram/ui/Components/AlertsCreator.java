package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.util.Consumer;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.export.ui.ExportMapper$$ExternalSyntheticLambda2;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.google.android.gms.cast.MediaError;
import com.google.android.material.timepicker.TimeModel;
import j$.time.Instant;
import j$.time.LocalDate;
import j$.time.YearMonth;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import kotlin.time.DurationKt;
import okhttp3.internal.url._UrlKt;
import org.mvel2.asm.signature.SignatureVisitor;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AiTonesController$$ExternalSyntheticLambda0;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AppGlobalConfig;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.NotificationsSettingsFacade;
import org.telegram.messenger.OneUIUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.pip.utils.PipUtils;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialogDecor;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.TimezonesController;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LanguageSelectActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PremiumFeatureCell;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.PrivacyControlActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.ProfileNotificationsActivity;
import org.telegram.ui.SelectChatUserSheet;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.DarkThemeResourceProvider;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.TooManyCommunitiesActivity;
import org.telegram.ui.community.cells.CommunityBanGroupConfirmCell;

public abstract class AlertsCreator {
    private static final Pattern URL_PATTERN = Pattern.compile("^([a-zA-Z][a-zA-Z0-9+\\-.]*://)?([a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(/[^\\s]*)?$");

    public interface AccountSelectDelegate {
        void didSelectAccount(int i);
    }

    public interface BlockDialogCallback {
        void run(boolean z, boolean z2);
    }

    public interface DatePickerDelegate {
        void didSelectDate(int i, int i2, int i3);
    }

    public interface FormattedDatePickerDelegate {
        void didSelectDate(int i, int i2);
    }

    public interface ScheduleDatePickerDelegate {
        void didSelectDate(boolean z, int i, int i2);
    }

    public interface SoundFrequencyDelegate {
        void didSelectValues(int i, int i2);
    }

    public interface StatusUntilDatePickerDelegate {
        void didSelectDate(int i);
    }

    public static boolean $r8$lambda$ISGHVganXhbap0xqfzUQvvZf8iE(EditTextBoldCursor editTextBoldCursor, Utilities.Callback callback, AlertDialog[] alertDialogArr, View view, TextView textView, int i, KeyEvent keyEvent) {
                                if (i != 6) {
                                    return false;
                                }
                                String strTrim = editTextBoldCursor.getText().toString().trim();
                                if (!isValidUrl(strTrim)) {
                                    AndroidUtilities.shakeView(editTextBoldCursor);
                                    return true;
                                }
                                callback.run(strTrim);
                                AlertDialog alertDialog = alertDialogArr[0];
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                                if (view != null) {
                                    view.requestFocus();
                                }
                                return true;
                            }

                            public static void $r8$lambda$8iOZHHjibVZj7obo7SH8qRfO5VI(final int i, final ArrayList arrayList, final long j, final Activity activity, final Theme.ResourcesProvider resourcesProvider, final Utilities.Callback callback, final HashMap map, Boolean bool) {
                                if (bool.booleanValue()) {
                                    SharedPreferences.Editor editorEdit = MessagesController.getInstance(i).getMainSettings().edit();
                                    int size = arrayList.size();
                                    int i2 = 0;
                                    while (i2 < size) {
                                        Object obj = arrayList.get(i2);
                                        i2++;
                                        Long l = (Long) obj;
                                        long jLongValue = l.longValue();
                                        long sendPaidMessagesStars = MessagesController.getInstance(i).getSendPaidMessagesStars(jLongValue);
                                        if (sendPaidMessagesStars <= 0 && jLongValue > 0) {
                                            sendPaidMessagesStars = DialogObject.getMessagesStarsPrice(MessagesController.getInstance(i).isUserContactBlocked(jLongValue));
                                        }
                                        editorEdit.putLong("ask_paid_message_" + jLongValue + "_price", sendPaidMessagesStars);
                                        StarsController.getInstance(i).justAgreedToNotAskDialogs.put(l, Long.valueOf(System.currentTimeMillis()));
                                    }
                                    editorEdit.apply();
                                }
                                Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda111
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        AlertsCreator.$r8$lambda$VLjUSpmT6YBIljMXSsRLV2fl7Mw(i, j, activity, arrayList, resourcesProvider, callback, map);
                                    }
                                };
                                if (!StarsController.getInstance(i).balanceAvailable()) {
                                    StarsController.getInstance(i).invalidateBalance(runnable);
                                } else {
                                    runnable.run();
                                }
                            }

                            public static void $r8$lambda$cIEbSFwNMk9x0u8zyC3c0fb61LQ(int i, long j, long j2, Runnable runnable, Boolean bool) {
                                if (bool.booleanValue()) {
                                    MessagesController.getInstance(i).getMainSettings().edit().putLong("ask_paid_message_" + j + "_price", j2).apply();
                                    StarsController.getInstance(i).justAgreedToNotAskDialogs.put(Long.valueOf(j), Long.valueOf(System.currentTimeMillis()));
                                }
                                AndroidUtilities.runOnUIThread(runnable);
                            }

                            public static AlertDialog showAlertWithCheckbox(Context context, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, CharSequence charSequence4, Utilities.Callback<Boolean> callback, Theme.ResourcesProvider resourcesProvider) {
                                return showAlertWithCheckbox(context, charSequence, charSequence2, charSequence3, charSequence4, callback, resourcesProvider, false);
                            }

                            public static AlertDialog showAlertWithCheckboxWithBalance(Context context, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, CharSequence charSequence4, Utilities.Callback<Boolean> callback, Theme.ResourcesProvider resourcesProvider) {
                                return showAlertWithCheckbox(context, charSequence, charSequence2, charSequence3, charSequence4, callback, resourcesProvider, true);
                            }

                            public static AlertDialog showAlertWithCheckbox(Context context, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, CharSequence charSequence4, final Utilities.Callback<Boolean> callback, Theme.ResourcesProvider resourcesProvider, boolean z) {
                                if (context == null) {
                                    callback.run(Boolean.FALSE);
                                    return null;
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
                                final CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
                                final boolean[] zArr = new boolean[1];
                                TextView textView = new TextView(context) { // from class: org.telegram.ui.Components.AlertsCreator.8
                                    @Override // android.widget.TextView
                                    public void setText(CharSequence charSequence5, TextView.BufferType bufferType) {
                                        super.setText(Emoji.replaceEmoji(charSequence5, getPaint().getFontMetricsInt(), false), bufferType);
                                    }
                                };
                                NotificationCenter.listenEmojiLoading(textView);
                                textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
                                textView.setTextSize(1, 16.0f);
                                textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                                textView.setText(charSequence2);
                                FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.9
                                    @Override // android.widget.FrameLayout, android.view.View
                                    public void onMeasure(int i, int i2) {
                                        super.onMeasure(i, i2);
                                        if (checkBoxCellArr[0] != null) {
                                            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + checkBoxCellArr[0].getMeasuredHeight() + AndroidUtilities.dp(7.0f));
                                        }
                                    }
                                };
                                builder.setCustomViewOffset(6);
                                builder.setView(frameLayout);
                                TextView textView2 = new TextView(context);
                                textView2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
                                textView2.setTextSize(1, 20.0f);
                                textView2.setTypeface(AndroidUtilities.bold());
                                textView2.setLines(1);
                                textView2.setMaxLines(1);
                                textView2.setSingleLine(true);
                                textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                                textView2.setEllipsize(TextUtils.TruncateAt.END);
                                textView2.setText(charSequence);
                                frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 8.0f, 24.0f, 0.0f));
                                frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 48.0f, 24.0f, 6.0f));
                                if (!TextUtils.isEmpty(charSequence3)) {
                                    CheckBoxCell checkBoxCell = new CheckBoxCell(context, 1, resourcesProvider);
                                    checkBoxCellArr[0] = checkBoxCell;
                                    checkBoxCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 7, AndroidUtilities.dp(12.0f)));
                                    checkBoxCellArr[0].setMultiline(true);
                                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) checkBoxCellArr[0].getCheckBoxView().getLayoutParams();
                                    layoutParams.topMargin = 0;
                                    layoutParams.gravity = (LocaleController.isRTL ? 5 : 3) | 16;
                                    checkBoxCellArr[0].getCheckBoxView().setLayoutParams(layoutParams);
                                    checkBoxCellArr[0].setText(charSequence3, _UrlKt.FRAGMENT_ENCODE_SET, false, false);
                                    checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(4.0f) : 0, AndroidUtilities.dp(12.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f));
                                    frameLayout.addView(checkBoxCellArr[0], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 0.0f));
                                    checkBoxCellArr[0].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda74
                                        @Override // android.view.View.OnClickListener
                                        public final void onClick(View view) {
                                            AlertsCreator.$r8$lambda$nyOEnuWOBNA9cdKho4EIGHjgFWY(zArr, view);
                                        }
                                    });
                                }
                                builder.setPositiveButton(charSequence4, new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda75
                                    @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                                    public final void onClick(AlertDialog alertDialog, int i) {
                                        callback.run(Boolean.valueOf(zArr[0]));
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                                AlertDialog alertDialogCreate = builder.create();
                                if (z) {
                                    alertDialogCreate.setShowStarsBalance(true);
                                }
                                alertDialogCreate.show();
                                return alertDialogCreate;
                            }

                            public static void $r8$lambda$uVCYcnN8kCCFstlQg99xvO4dOpQ(boolean[] zArr, View view) {
                                boolean z = !zArr[0];
                                zArr[0] = z;
                                ((CheckBoxCell) view).setChecked(z, true);
                            }

                            public static void $r8$lambda$s8IFEOUSMBH81wsbyedoGFRSDi4(BaseFragment baseFragment, boolean z, TLRPC.Chat chat, TLRPC.User user, boolean z2, boolean[] zArr, boolean z3, MessagesStorage.BooleanCallback booleanCallback, Theme.ResourcesProvider resourcesProvider, int i) {
                                if (i >= 50) {
                                    createClearOrDeleteDialogAlert(baseFragment, z, true, chat, user, false, z2, zArr[0], z3, booleanCallback, resourcesProvider);
                                } else if (booleanCallback != null) {
                                    booleanCallback.run(zArr[0]);
                                }
                            }

                            void $r8$lambda$MsXNQ8U6DHyZcXFzwN3CJ_zkd5s(int i, int i2, NumberPicker numberPicker, NumberPicker numberPicker2, int i3, LinearLayout linearLayout, Boolean bool) {
                                int minValue;
                                int value;
                                int i4 = i % 60;
                                int i5 = (i - i4) / 60;
                                int i6 = i2 % 60;
                                int i7 = (i2 - i6) / 60;
                                if (i6 == 0 && i7 > 0) {
                                    i7--;
                                    i6 = 59;
                                }
                                if (bool.booleanValue()) {
                                    value = numberPicker.getValue();
                                    minValue = numberPicker2.getValue();
                                } else {
                                    minValue = i3 % 60;
                                    value = (i3 - minValue) / 60;
                                    if (value == 24) {
                                        value--;
                                        minValue = 59;
                                    }
                                }
                                numberPicker.setMinValue(i5);
                                numberPicker.setMaxValue(i7);
                                if (value > i7) {
                                    numberPicker.setValue(i7);
                                    value = i7;
                                } else if (value < i5) {
                                    numberPicker.setValue(i5);
                                    value = i5;
                                }
                                if (value <= i5) {
                                    numberPicker2.setMinValue(i4);
                                    numberPicker2.setMaxValue(i5 == i7 ? i6 : 59);
                                } else if (value >= i7) {
                                    if (i5 != i7) {
                                        i4 = 0;
                                    }
                                    numberPicker2.setMinValue(i4);
                                    numberPicker2.setMaxValue(i6);
                                } else if (i5 == i7) {
                                    numberPicker2.setMinValue(i4);
                                    numberPicker2.setMaxValue(i6);
                                } else {
                                    numberPicker2.setMinValue(0);
                                    numberPicker2.setMaxValue(59);
                                }
                                if (minValue > numberPicker2.getMaxValue()) {
                                    minValue = numberPicker2.getMaxValue();
                                    numberPicker2.setValue(minValue);
                                } else if (minValue < numberPicker2.getMinValue()) {
                                    minValue = numberPicker2.getMinValue();
                                    numberPicker2.setValue(minValue);
                                }
                                if (!bool.booleanValue()) {
                                    numberPicker.setValue(value);
                                    numberPicker2.setValue(minValue);
                                }
                                linearLayout.invalidate();
                            }

                            public static void $r8$lambda$jpWDy7rQ2rYg8DV8dAjPzQ_3zyo(TextView textView, String str, long j, long j2, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
                                int i3;
                                if (str != null) {
                                    i3 = 3;
                                } else {
                                    i3 = j == j2 ? 1 : 0;
                                }
                                checkScheduleDate(textView, null, i3, numberPicker, numberPicker2, numberPicker3);
                            }

                            public static public static void $r8$lambda$Cz3wuWTWR9Idipo9ackwcAIKHO4() {
                                BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
                                if (safeLastFragment == null) {
                                    return;
                                }
                                BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
                                bottomSheetParams.transitionFromLeft = true;
                                bottomSheetParams.allowNestedScroll = false;
                                safeLastFragment.showAsSheet(new PremiumPreviewFragment("schedule_repeat"), bottomSheetParams);
                            }

                            public static void $r8$lambda$AXfzc1V0Fk6E4ivE9LTDxYqr1bc(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
                                boolean zCheckScheduleDate = checkScheduleDate(null, null, 0, numberPicker, numberPicker2, numberPicker3);
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.add(6, numberPicker.getValue());
                                calendar.set(11, numberPicker2.getValue());
                                calendar.set(12, numberPicker3.getValue());
                                if (zCheckScheduleDate) {
                                    calendar.set(13, 0);
                                    calendar.set(14, 0);
                                }
                                scheduleDatePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000), 0);
                                builder.getDismissRunnable().run();
                            }

                            public static BottomSheet.Builder createBirthdayPickerDialog(Context context, String str, String str2, TL_account.TL_birthday tL_birthday, final Utilities.Callback<TL_account.TL_birthday> callback, Runnable runnable, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
                                if (context == null) {
                                    return null;
                                }
                                final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
                                builder.setApplyBottomPadding(false);
                                final NumberPicker numberPicker = new NumberPicker(context, resourcesProvider);
                                numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
                                numberPicker.setItemCount(5);
                                final NumberPicker numberPicker2 = new NumberPicker(context, resourcesProvider);
                                numberPicker2.setItemCount(5);
                                numberPicker2.setTextOffset(-AndroidUtilities.dp(10.0f));
                                final NumberPicker numberPicker3 = new NumberPicker(context, resourcesProvider);
                                numberPicker3.setItemCount(5);
                                numberPicker3.setTextOffset(-AndroidUtilities.dp(24.0f));
                                LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.33
                                    boolean ignoreLayout = false;

                                    @Override // android.widget.LinearLayout, android.view.View
                                    public void onMeasure(int i, int i2) {
                                        this.ignoreLayout = true;
                                        Point point = AndroidUtilities.displaySize;
                                        int i3 = point.x > point.y ? 3 : 5;
                                        numberPicker.setItemCount(i3);
                                        numberPicker2.setItemCount(i3);
                                        numberPicker3.setItemCount(i3);
                                        numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                                        numberPicker2.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                                        numberPicker3.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                                        this.ignoreLayout = false;
                                        super.onMeasure(i, i2);
                                    }

                                    @Override // android.view.View, android.view.ViewParent
                                    public void requestLayout() {
                                        if (this.ignoreLayout) {
                                            return;
                                        }
                                        super.requestLayout();
                                    }
                                };
                                linearLayout.setOrientation(1);
                                FrameLayout frameLayout = new FrameLayout(context);
                                linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
                                TextView textView = new TextView(context);
                                textView.setText(str);
                                textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
                                textView.setTextSize(1, 20.0f);
                                textView.setTypeface(AndroidUtilities.bold());
                                frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
                                textView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda57
                                    @Override // android.view.View.OnTouchListener
                                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                                        return AlertsCreator.$r8$lambda$quFLlkM89UpQR3a4R3pbmuoonZ0(view, motionEvent);
                                    }
                                });
                                LinearLayout linearLayout2 = new LinearLayout(context);
                                linearLayout2.setGravity(17);
                                linearLayout2.setOrientation(0);
                                linearLayout2.setWeightSum(1.0f);
                                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
                                Calendar calendar = Calendar.getInstance();
                                int i = calendar.get(1) - 149;
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                final int i2 = calendar.get(5);
                                final int i3 = calendar.get(2);
                                final int i4 = calendar.get(1);
                                final int i5 = i4 + 1;
                                final Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda59
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        AlertsCreator.$r8$lambda$ZwCXGgRBLB0dk5jqabrgdShl5J8(numberPicker3, i5, numberPicker, numberPicker2, i4, i3, i2);
                                    }
                                };
                                System.currentTimeMillis();
                                TextView textView2 = new TextView(context) { // from class: org.telegram.ui.Components.AlertsCreator.34
                                    @Override // android.widget.TextView, android.view.View
                                    public CharSequence getAccessibilityClassName() {
                                        return Button.class.getName();
                                    }
                                };
                                linearLayout2.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.25f));
                                numberPicker.setMinValue(1);
                                numberPicker.setMaxValue(31);
                                numberPicker.setWrapSelectorWheel(false);
                                numberPicker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda60
                                    @Override // org.telegram.ui.Components.NumberPicker.Formatter
                                    public final String format(int i6) {
                                        return AlertsCreator.$r8$lambda$A3N78y69brDFDcfSNp5AtDnPszI(i6);
                                    }
                                });
                                NumberPicker.OnScrollListener onScrollListener = new NumberPicker.OnScrollListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda61
                                    @Override // org.telegram.ui.Components.NumberPicker.OnScrollListener
                                    public final void onScrollStateChange(NumberPicker numberPicker4, int i6) {
                                        AlertsCreator.m8867$r8$lambda$pq8j9Pd5XkxSddFgAZmEpPqaSE(runnable2, numberPicker4, i6);
                                    }
                                };
                                numberPicker.setOnScrollListener(onScrollListener);
                                numberPicker2.setMinValue(0);
                                numberPicker2.setMaxValue(11);
                                numberPicker2.setWrapSelectorWheel(false);
                                linearLayout2.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.5f));
                                numberPicker2.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda62
                                    @Override // org.telegram.ui.Components.NumberPicker.Formatter
                                    public final String format(int i6) {
                                        return AlertsCreator.m8870$r8$lambda$qWpnOqCVwltGGH8IFYjVyL_SC4(i6);
                                    }
                                });
                                numberPicker2.setOnScrollListener(onScrollListener);
                                numberPicker3.setMinValue(i);
                                numberPicker3.setMaxValue(i5);
                                numberPicker3.setWrapSelectorWheel(false);
                                numberPicker3.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda63
                                    @Override // org.telegram.ui.Components.NumberPicker.Formatter
                                    public final String format(int i6) {
                                        return AlertsCreator.$r8$lambda$Dn8pmCsIIynmaZM4RXRTLKjWjsc(i5, i6);
                                    }
                                });
                                linearLayout2.addView(numberPicker3, LayoutHelper.createLinear(0, 270, 0.25f));
                                numberPicker3.setOnScrollListener(onScrollListener);
                                if (tL_birthday != null) {
                                    numberPicker.setValue(tL_birthday.day);
                                    numberPicker2.setValue(tL_birthday.month - 1);
                                    if ((tL_birthday.flags & 1) != 0) {
                                        numberPicker3.setValue(tL_birthday.year);
                                    } else {
                                        numberPicker3.setValue(i5);
                                    }
                                } else {
                                    numberPicker.setValue(calendar.get(5));
                                    numberPicker2.setValue(calendar.get(2));
                                    numberPicker3.setValue(i5);
                                }
                                runnable2.run();
                                if (runnable != null) {
                                    FrameLayout frameLayout2 = new FrameLayout(context);
                                    final LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
                                    linksTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                                    linksTextView.setTextSize(1, 13.0f);
                                    linksTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2, resourcesProvider));
                                    linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
                                    linksTextView.setGravity(17);
                                    frameLayout2.addView(linksTextView, LayoutHelper.createFrame(-2, -2, 17));
                                    linearLayout.addView(frameLayout2, LayoutHelper.createLinear(-1, -2));
                                    final int i6 = UserConfig.selectedAccount;
                                    final Runnable runnable3 = new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda64
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            AlertsCreator.$r8$lambda$ybyRgPIDaxmaU2dTTD2itje9Cvg(i6, linksTextView);
                                        }
                                    };
                                    runnable3.run();
                                    NotificationCenter.getInstance(i6).listen(frameLayout2, NotificationCenter.privacyRulesUpdated, new Utilities.Callback() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda65
                                        @Override 
                                        public final void run(Object obj) {
                                            runnable3.run();
                                        }
                                    });
                                    ContactsController.getInstance(i6).loadPrivacySettings();
                                }
                                if (z) {
                                    ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, false, resourcesProvider);
                                    buttonWithCounterView.setText(LocaleController.getString(R.string.DateOfBirthHideYear), false);
                                    buttonWithCounterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda66
                                        @Override // android.view.View.OnClickListener
                                        public final void onClick(View view) {
                                            AlertsCreator.$r8$lambda$jemlvi6mKrQLB9Wj6RbQDKURoB4(numberPicker3, i5, runnable2, view);
                                        }
                                    });
                                    linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 4));
                                }
                                textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
                                textView2.setGravity(17);
                                textView2.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
                                textView2.setTextSize(1, 14.0f);
                                textView2.setTypeface(AndroidUtilities.bold());
                                textView2.setText(str2);
                                textView2.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider), Theme.getColor(Theme.key_featuredStickers_addButtonPressed, resourcesProvider)));
                                ScaleStateListAnimator.apply(textView2);
                                linearLayout.addView(textView2, LayoutHelper.createLinear(-1, 48, 83, 16, z ? 0 : 15, 16, z2 ? 0 : 16));
                                textView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda67
                                    @Override // android.view.View.OnClickListener
                                    public final void onClick(View view) {
                                        AlertsCreator.$r8$lambda$Z6A3x4C4ZBk_BmibMjgXd3XyQH4(numberPicker, numberPicker2, numberPicker3, i5, builder, callback, view);
                                    }
                                });
                                if (z2) {
                                    ButtonWithCounterView buttonWithCounterView2 = new ButtonWithCounterView(context, false, resourcesProvider);
                                    buttonWithCounterView2.setText(LocaleController.getString(R.string.BirthdayRemove), false);
                                    buttonWithCounterView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda58
                                        @Override // android.view.View.OnClickListener
                                        public final void onClick(View view) {
                                            AlertsCreator.$r8$lambda$NCQRxBjszVhUVo0Miw27kuKThQY(builder, callback, view);
                                        }
                                    });
                                    linearLayout.addView(buttonWithCounterView2, LayoutHelper.createLinear(-1, 48, 83, 16, 4, 16, 16));
                                }
                                builder.setCustomView(linearLayout);
                                return builder;
                            }

                            public static String $r8$lambda$NOMVvCxYCTpiagkgFs21K69ZNsU(int i) {
                                return _UrlKt.FRAGMENT_ENCODE_SET + i;
                            }

                            public static void $r8$lambda$ELZyW1pRDW5quWeAPRVNxAsJ25k(int i, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, TextView textView, NumberPicker numberPicker4, int i2, int i3) {
                                checkScheduleDate(null, null, i, 3, numberPicker, numberPicker2, numberPicker3);
                                checkPollCloseCustomDeadline(textView, numberPicker, numberPicker2, numberPicker3);
                            }

                            public static void m8825$r8$lambda$SKiG87VB4cM3SULTE57OxPwepU(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, StatusUntilDatePickerDelegate statusUntilDatePickerDelegate, BottomSheet.Builder builder, View view) {
                                boolean zCheckScheduleDate = checkScheduleDate(null, null, 0, numberPicker, numberPicker2, numberPicker3);
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.add(6, numberPicker.getValue());
                                calendar.set(11, numberPicker2.getValue());
                                calendar.set(12, numberPicker3.getValue());
                                if (zCheckScheduleDate) {
                                    calendar.set(13, 0);
                                    calendar.set(14, 0);
                                }
                                statusUntilDatePickerDelegate.didSelectDate((int) (calendar.getTimeInMillis() / 1000));
                                builder.getDismissRunnable().run();
                            }

                            public static BottomSheet.Builder createAutoDeleteDatePickerDialog(Context context, int i, Theme.ResourcesProvider resourcesProvider, final ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
                                if (context == null) {
                                    return null;
                                }
                                ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors(resourcesProvider);
                                final BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
                                builder.setApplyBottomPadding(false);
                                final int[] iArr = {0, 1440, 2880, 4320, 5760, 7200, 8640, 10080, 20160, 30240, 44640, 89280, 133920, 178560, 223200, 267840, 525600};
                                final NumberPicker numberPicker = new NumberPicker(context, resourcesProvider) { // from class: org.telegram.ui.Components.AlertsCreator.45
                                    @Override // org.telegram.ui.Components.NumberPicker
                                    public CharSequence getContentDescription(int i2) {
                                        int i3 = iArr[i2];
                                        if (i3 == 0) {
                                            return LocaleController.getString(R.string.AutoDeleteNever);
                                        }
                                        if (i3 < 10080) {
                                            return LocaleController.formatPluralString("Days", i3 / 1440, new Object[0]);
                                        }
                                        if (i3 < 44640) {
                                            return LocaleController.formatPluralString("Weeks", i3 / 1440, new Object[0]);
                                        }
                                        if (i3 < 525600) {
                                            return LocaleController.formatPluralString("Months", i3 / 10080, new Object[0]);
                                        }
                                        return LocaleController.formatPluralString("Years", ((i3 * 5) / 31) * 1440, new Object[0]);
                                    }
                                };
                                numberPicker.setMinValue(0);
                                numberPicker.setMaxValue(16);
                                numberPicker.setTextColor(scheduleDatePickerColors.textColor);
                                numberPicker.setValue(0);
                                numberPicker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda154
                                    @Override // org.telegram.ui.Components.NumberPicker.Formatter
                                    public final String format(int i2) {
                                        return AlertsCreator.$r8$lambda$LiMtNDofYI_wQ00E_vlWgvoAMVc(iArr, i2);
                                    }
                                });
                                LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.46
                                    boolean ignoreLayout = false;

                                    @Override // android.widget.LinearLayout, android.view.View
                                    public void onMeasure(int i2, int i3) {
                                        this.ignoreLayout = true;
                                        Point point = AndroidUtilities.displaySize;
                                        int i4 = point.x > point.y ? 3 : 5;
                                        numberPicker.setItemCount(i4);
                                        numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i4;
                                        this.ignoreLayout = false;
                                        super.onMeasure(i2, i3);
                                    }

                                    @Override // android.view.View, android.view.ViewParent
                                    public void requestLayout() {
                                        if (this.ignoreLayout) {
                                            return;
                                        }
                                        super.requestLayout();
                                    }
                                };
                                boolean z = true;
                                linearLayout.setOrientation(1);
                                FrameLayout frameLayout = new FrameLayout(context);
                                linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
                                TextView textView = new TextView(context);
                                textView.setText(LocaleController.getString(R.string.AutoDeleteAfteTitle));
                                textView.setTextColor(scheduleDatePickerColors.textColor);
                                textView.setTextSize(1, 20.0f);
                                textView.setTypeface(AndroidUtilities.bold());
                                frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
                                textView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda155
                                    @Override // android.view.View.OnTouchListener
                                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                                        return AlertsCreator.$r8$lambda$Am4aqzrBNrWuGx35tYfMf3dBUkk(view, motionEvent);
                                    }
                                });
                                LinearLayout linearLayout2 = new LinearLayout(context);
                                linearLayout2.setOrientation(0);
                                linearLayout2.setWeightSum(1.0f);
                                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
                                final AnimatedTextView animatedTextView = new AnimatedTextView(context, z, z, false) { // from class: org.telegram.ui.Components.AlertsCreator.47
                                    @Override // android.view.View
                                    public CharSequence getAccessibilityClassName() {
                                        return Button.class.getName();
                                    }
                                };
                                linearLayout2.addView(numberPicker, LayoutHelper.createLinear(0, 270, 1.0f));
                                animatedTextView.setPadding(0, 0, 0, 0);
                                animatedTextView.setGravity(17);
                                animatedTextView.setTextColor(scheduleDatePickerColors.buttonTextColor);
                                animatedTextView.setTextSize(AndroidUtilities.dp(14.0f));
                                animatedTextView.setTypeface(AndroidUtilities.bold());
                                animatedTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
                                linearLayout.addView(animatedTextView, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
                                animatedTextView.setText(LocaleController.getString(R.string.DisableAutoDeleteTimer));
                                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda156
                                    @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
                                    public final void onValueChange(NumberPicker numberPicker2, int i2, int i3) {
                                        AlertsCreator.m8823$r8$lambda$Rhlm_9WDqGaEyoW1DqFrMvULo(animatedTextView, numberPicker2, i2, i3);
                                    }
                                });
                                animatedTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda157
                                    @Override // android.view.View.OnClickListener
                                    public final void onClick(View view) {
                                        AlertsCreator.$r8$lambda$_nzp2_KeSPjzmK3iynQfljl_Xag(iArr, numberPicker, scheduleDatePickerDelegate, builder, view);
                                    }
                                });
                                builder.setCustomView(linearLayout);
                                BottomSheet bottomSheetShow = builder.show();
                                bottomSheetShow.setBackgroundColor(scheduleDatePickerColors.backgroundColor);
                                bottomSheetShow.fixNavigationBar(scheduleDatePickerColors.backgroundColor);
                                return builder;
                            }

                            public static void m8872$r8$lambda$ry1PicWvO88M1rZD53DUmoPJM(int i, long j, TLRPC.Photo photo, Context context, Theme.ResourcesProvider resourcesProvider, Integer num, String str) {
                                TL_account.reportProfilePhoto reportprofilephoto = new TL_account.reportProfilePhoto();
                                reportprofilephoto.peer = MessagesController.getInstance(i).getInputPeer(j);
                                TLRPC.TL_inputPhoto tL_inputPhoto = new TLRPC.TL_inputPhoto();
                                tL_inputPhoto.id = photo.id;
                                tL_inputPhoto.file_reference = photo.file_reference;
                                tL_inputPhoto.access_hash = photo.access_hash;
                                reportprofilephoto.photo_id = tL_inputPhoto;
                                reportprofilephoto.message = _UrlKt.FRAGMENT_ENCODE_SET;
                                if (num.intValue() == 0) {
                                    reportprofilephoto.reason = new TLRPC.TL_inputReportReasonSpam();
                                } else if (num.intValue() == 1) {
                                    reportprofilephoto.reason = new TLRPC.TL_inputReportReasonViolence();
                                } else if (num.intValue() == 2) {
                                    reportprofilephoto.reason = new TLRPC.TL_inputReportReasonChildAbuse();
                                } else if (num.intValue() == 5) {
                                    reportprofilephoto.reason = new TLRPC.TL_inputReportReasonPornography();
                                } else if (num.intValue() == 3) {
                                    reportprofilephoto.reason = new TLRPC.TL_inputReportReasonIllegalDrugs();
                                } else if (num.intValue() == 4) {
                                    reportprofilephoto.reason = new TLRPC.TL_inputReportReasonPersonalDetails();
                                }
                                ConnectionsManager.getInstance(i).sendRequest(reportprofilephoto, null);
                                BulletinFactory.of(Bulletin.BulletinWindow.make(context), resourcesProvider).createReportSent(resourcesProvider).show();
                            }

                            public static String $r8$lambda$xZJrrEX07ZhPwQAjjR70z5CqeU8(int i) {
                                if (i == 0) {
                                    return LocaleController.getString(R.string.ShortMessageLifetimeForever);
                                }
                                if (i >= 1 && i < 16) {
                                    return LocaleController.formatTTLString(i);
                                }
                                if (i == 16) {
                                    return LocaleController.formatTTLString(30);
                                }
                                if (i == 17) {
                                    return LocaleController.formatTTLString(60);
                                }
                                if (i == 18) {
                                    return LocaleController.formatTTLString(3600);
                                }
                                if (i == 19) {
                                    return LocaleController.formatTTLString(86400);
                                }
                                if (i == 20) {
                                    return LocaleController.formatTTLString(604800);
                                }
                                return _UrlKt.FRAGMENT_ENCODE_SET;
                            }

                            public static void m8835$r8$lambda$V2mFkNc8sl91jWWCM4aHt1POOc(MessageObject.GroupedMessages groupedMessages, int i, BaseFragment baseFragment, int i2, int i3, MessageObject messageObject, AlertDialog alertDialog, int i4) {
                                if (groupedMessages != null && !groupedMessages.messages.isEmpty()) {
                                    SendMessagesHelper.getInstance(i).editMessage(groupedMessages.messages.get(0), null, false, baseFragment, null, i2 + i3, i3);
                                } else {
                                    SendMessagesHelper.getInstance(i).editMessage(messageObject, null, false, baseFragment, null, i2 + i3, i3);
                                }
                            }

                            public static void $r8$lambda$5W5CEmYNNvBTNCjPrtcFRLDjjOU(Runnable runnable, DialogInterface dialogInterface) {
                                if (runnable != null) {
                                    runnable.run();
                                }
                            }

                            public static void m8828$r8$lambda$T6TiM1uGGJA22H59Fb4YK9PtWA(Runnable runnable, DialogInterface dialogInterface) {
                                if (runnable != null) {
                                    runnable.run();
                                }
                            }

                            public static void createThemeCreateDialog(final BaseFragment baseFragment, int i, final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent themeAccent) {
                                if (baseFragment == null || baseFragment.getParentActivity() == null) {
                                    return;
                                }
                                Activity parentActivity = baseFragment.getParentActivity();
                                final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(parentActivity);
                                editTextBoldCursor.setBackground(null);
                                editTextBoldCursor.setLineColors(Theme.getColor(Theme.key_dialogInputField), Theme.getColor(Theme.key_dialogInputFieldActivated), Theme.getColor(Theme.key_text_RedBold));
                                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                                builder.setTitle(LocaleController.getString(R.string.NewTheme));
                                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                                builder.setPositiveButton(LocaleController.getString(R.string.Create), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda123
                                    @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                                    public final void onClick(AlertDialog alertDialog, int i2) {
                                        AlertsCreator.m8878$r8$lambda$vKtI7mBYB6fypztloZfGvH05hI(alertDialog, i2);
                                    }
                                });
                                LinearLayout linearLayout = new LinearLayout(parentActivity);
                                linearLayout.setOrientation(1);
                                builder.setView(linearLayout);
                                TextView textView = new TextView(parentActivity);
                                if (i != 0) {
                                    textView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.EnterThemeNameEdit)));
                                } else {
                                    textView.setText(LocaleController.getString(R.string.EnterThemeName));
                                }
                                textView.setTextSize(1, 16.0f);
                                textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
                                int i2 = Theme.key_dialogTextBlack;
                                textView.setTextColor(Theme.getColor(i2));
                                linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
                                editTextBoldCursor.setTextSize(1, 16.0f);
                                editTextBoldCursor.setTextColor(Theme.getColor(i2));
                                editTextBoldCursor.setMaxLines(1);
                                editTextBoldCursor.setLines(1);
                                editTextBoldCursor.setInputType(16385);
                                editTextBoldCursor.setGravity(51);
                                editTextBoldCursor.setSingleLine(true);
                                editTextBoldCursor.setImeOptions(6);
                                editTextBoldCursor.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                                editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
                                editTextBoldCursor.setCursorWidth(1.5f);
                                editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                                linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
                                editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda124
                                    @Override // android.widget.TextView.OnEditorActionListener
                                    public final boolean onEditorAction(TextView textView2, int i3, KeyEvent keyEvent) {
                                        return AlertsCreator.m8833$r8$lambda$UZvcDYS92eiNSMw8mxIgDsd4pI(textView2, i3, keyEvent);
                                    }
                                });
                                editTextBoldCursor.setText(generateThemeName(themeAccent));
                                editTextBoldCursor.setSelection(editTextBoldCursor.length());
                                final AlertDialog alertDialogCreate = builder.create();
                                alertDialogCreate.setOnShowListener(new DialogInterface.OnShowListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda125
                                    @Override // android.content.DialogInterface.OnShowListener
                                    public final void onShow(DialogInterface dialogInterface) {
                                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda193
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                AlertsCreator.m8803$r8$lambda$7amHu8URFBNRD36GrkRf5MKr8I(editTextBoldCursor);
                                            }
                                        });
                                    }
                                });
                                baseFragment.showDialog(alertDialogCreate);
                                editTextBoldCursor.requestFocus();
                                alertDialogCreate.getButton(-1).setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda126
                                    @Override // android.view.View.OnClickListener
                                    public final void onClick(View view) throws Throwable {
                                        AlertsCreator.m8807$r8$lambda$B32SYB81IKGaTCy4ZjWiasBWcw(baseFragment, editTextBoldCursor, themeAccent, themeInfo, alertDialogCreate, view);
                                    }
                                });
                            }

                            public static boolean m8810$r8$lambda$EiHl3Zc5r3GSKGa2a_fenpvjJ8(View view, MotionEvent motionEvent) {
                                return true;
                            }

                            public static void $r8$lambda$WuqArnhWtKBQT8rKRmxrsbcEqXQ(boolean[] zArr, long j, long j2, int i, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
                                zArr[0] = false;
                                boolean zCheckScheduleDate = checkScheduleDate(null, null, j, j2, i, numberPicker, numberPicker2, numberPicker3);
                                calendar.setTimeInMillis(LocalDate.now().plusDays(numberPicker.getValue()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
                                calendar.set(11, numberPicker2.getValue());
                                calendar.set(12, numberPicker3.getValue());
                                if (zCheckScheduleDate) {
                                    calendar.set(13, 0);
                                }
                                scheduleDatePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000), 0);
                                builder.getDismissRunnable().run();
                            }

                            public static /* synthetic */ void m8822$r8$lambda$QbYgsw9uEcknAJJYYuEhlAgBZ0(boolean[] zArr, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
                                zArr[0] = false;
                                scheduleDatePickerDelegate.didSelectDate(true, -1, 0);
                                builder.getDismissRunnable().run();
                            }

                            public static /* synthetic */ void $r8$lambda$kgKT1JXfzg6RppDHOqrapNO3uAk(Runnable runnable, boolean[] zArr, DialogInterface dialogInterface) {
                                if (runnable == null || !zArr[0]) {
                                    return;
                                }
                                runnable.run();
                            }

                            public static void showCallsForbidden(Context context, final int i, final long j, final Theme.ResourcesProvider resourcesProvider) {
                                BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
                                LinearLayout linearLayout = new LinearLayout(context);
                                linearLayout.setOrientation(1);
                                linearLayout.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
                                FrameLayout frameLayout = new FrameLayout(context);
                                frameLayout.setClipChildren(false);
                                frameLayout.setClipToPadding(false);
                                linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 92, 17, 0, 0, 0, 0));
                                FrameLayout frameLayout2 = new FrameLayout(context);
                                ImageView imageView = new ImageView(context);
                                imageView.setScaleType(ImageView.ScaleType.CENTER);
                                imageView.setImageResource(R.drawable.story_link);
                                imageView.setScaleX(2.0f);
                                imageView.setScaleY(2.0f);
                                frameLayout2.addView(imageView, LayoutHelper.createFrame(-1, -1, 17));
                                frameLayout2.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(80.0f), Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider)));
                                frameLayout.addView(frameLayout2, LayoutHelper.createFrame(80, 80.0f, 1, 0.0f, 12.0f, 0.0f, 0.0f));
                                TextView textView = new TextView(context);
                                int i2 = Theme.key_windowBackgroundWhiteBlackText;
                                textView.setTextColor(Theme.getColor(i2, resourcesProvider));
                                textView.setTextSize(1, 20.0f);
                                textView.setTypeface(AndroidUtilities.bold());
                                textView.setText(LocaleController.getString(R.string.CallForbiddenInviteLinkTitle));
                                textView.setGravity(17);
                                linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 32.0f, 16.0f, 32.0f, 8.0f));
                                TextView textView2 = new TextView(context);
                                textView2.setTextColor(Theme.getColor(i2, resourcesProvider));
                                textView2.setTextSize(1, 14.0f);
                                textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.CallForbiddenInviteLinkText, DialogObject.getName(i, j))));
                                textView2.setGravity(17);
                                linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 32.0f, 0.0f, 32.0f, 18.0f));
                                final ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
                                buttonWithCounterView.setText(LocaleController.getString(R.string.CallForbiddenInviteLinkButton), false);
                                linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 0.0f, 0.0f, 0.0f, 0.0f));
                                builder.setCustomView(linearLayout);
                                final BottomSheet bottomSheetCreate = builder.create();
                                buttonWithCounterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda17
                                    @Override // android.view.View.OnClickListener
                                    public final void onClick(View view) {
                                        AlertsCreator.$r8$lambda$dMO7qOaCKniG93cClqpFwXznWxo(i, buttonWithCounterView, bottomSheetCreate, j, resourcesProvider, view);
                                    }
                                });
                                bottomSheetCreate.fixNavigationBar();
                                bottomSheetCreate.show();
                            }

                            public static /* synthetic */ void $r8$lambda$dMO7qOaCKniG93cClqpFwXznWxo(final int i, final ButtonWithCounterView buttonWithCounterView, final BottomSheet bottomSheet, final long j, final Theme.ResourcesProvider resourcesProvider, View view) {
                                TL_phone.createConferenceCall createconferencecall = new TL_phone.createConferenceCall();
                                createconferencecall.random_id = Utilities.random.nextInt();
                                ConnectionsManager.getInstance(i).sendRequest(createconferencecall, new RequestDelegate() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda46
                                    @Override // org.telegram.tgnet.RequestDelegate
                                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda93
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                AlertsCreator.m8839$r8$lambda$Wz_ES5UyWWV5sQsDiw9Ct85x4(tLObject, i, buttonWithCounterView, bottomSheet, j, tL_error, resourcesProvider);
                                            }
                                        });
                                    }
                                });
                            }

                            public static /* synthetic */ void m8839$r8$lambda$Wz_ES5UyWWV5sQsDiw9Ct85x4(TLObject tLObject, final int i, ButtonWithCounterView buttonWithCounterView, BottomSheet bottomSheet, long j, TLRPC.TL_error tL_error, Theme.ResourcesProvider resourcesProvider) {
                                if (tLObject instanceof TLRPC.Updates) {
                                    final TLRPC.Updates updates = (TLRPC.Updates) tLObject;
                                    MessagesController.getInstance(i).putUsers(updates.users, false);
                                    MessagesController.getInstance(i).putChats(updates.chats, false);
                                    ArrayList arrayListFindUpdates = MessagesController.findUpdates(updates, TL_update.TL_updateGroupCall.class);
                                    int size = arrayListFindUpdates.size();
                                    TLRPC.GroupCall groupCall = null;
                                    int i2 = 0;
                                    while (i2 < size) {
                                        Object obj = arrayListFindUpdates.get(i2);
                                        i2++;
                                        groupCall = ((TL_update.TL_updateGroupCall) obj).call;
                                    }
                                    Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda171
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            MessagesController.getInstance(i).processUpdates(updates, false);
                                        }
                                    });
                                    if (groupCall == null || LaunchActivity.instance == null) {
                                        buttonWithCounterView.setLoading(false);
                                        return;
                                    }
                                    bottomSheet.lambda$new$0();
                                    SendMessagesHelper.getInstance(i).sendMessage(SendMessagesHelper.SendMessageParams.of(groupCall.invite_link, j));
                                    BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
                                    if (safeLastFragment != null) {
                                        if (safeLastFragment instanceof ChatActivity) {
                                            ChatActivity chatActivity = (ChatActivity) safeLastFragment;
                                            if (chatActivity.getDialogId() == j && chatActivity.getChatMode() == 0) {
                                                return;
                                            }
                                        }
                                        safeLastFragment.presentFragment(ChatActivity.of(j));
                                        return;
                                    }
                                    return;
                                }
                                if (!(tLObject instanceof TL_phone.groupCall)) {
                                    if (tL_error != null) {
                                        BulletinFactory.of(bottomSheet.topBulletinContainer, resourcesProvider).showForError(tL_error);
                                        return;
                                    }
                                    return;
                                }
                                TL_phone.groupCall groupcall = (TL_phone.groupCall) tLObject;
                                MessagesController.getInstance(i).putUsers(groupcall.users, false);
                                MessagesController.getInstance(i).putChats(groupcall.chats, false);
                                if (LaunchActivity.instance == null) {
                                    buttonWithCounterView.setLoading(false);
                                    return;
                                }
                                TLRPC.TL_inputGroupCall tL_inputGroupCall = new TLRPC.TL_inputGroupCall();
                                TLRPC.GroupCall groupCall2 = groupcall.call;
                                tL_inputGroupCall.id = groupCall2.id;
                                tL_inputGroupCall.access_hash = groupCall2.access_hash;
                                bottomSheet.lambda$new$0();
                                VoIPHelper.joinConference(LaunchActivity.instance, i, tL_inputGroupCall, false, groupcall.call, null);
                                SendMessagesHelper.getInstance(i).sendMessage(SendMessagesHelper.SendMessageParams.of(groupcall.call.invite_link, j));
                            }

                            public static void showGiftThemeApplyConfirm(Context context, Theme.ResourcesProvider resourcesProvider, int i, TL_stars.StarGift starGift, long j, final Runnable runnable) {
                                TLObject userOrChat = MessagesController.getInstance(i).getUserOrChat(j);
                                LinearLayout linearLayout = new LinearLayout(context);
                                linearLayout.setOrientation(1);
                                linearLayout.addView(new StarGiftSheet.GiftThemeReuseTopView(context, starGift, userOrChat), LayoutHelper.createLinear(-1, -2, 48, 0, -4, 0, 0));
                                TextView textView = new TextView(context);
                                textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
                                textView.setTextSize(1, 16.0f);
                                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.GiftThemesSetInReuseInfo, DialogObject.getDialogTitle(userOrChat))));
                                linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 48, 24, 0, 24, 4));
                                new AlertDialog.Builder(context, resourcesProvider).setView(linearLayout).setPositiveButton(LocaleController.getString(R.string.GiftThemesSetInReuseConfirm), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda199
                                    @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                                    public final void onClick(AlertDialog alertDialog, int i2) {
                                        runnable.run();
                                    }
                                }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
                            }

                            public static BottomSheet createCustomPicker(Context context, String str, int i, final String[] strArr, final Utilities.Callback<Integer> callback) {
                                if (TimezonesController.getInstance(UserConfig.selectedAccount).getTimezones().isEmpty()) {
                                    return null;
                                }
                                ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors();
                                BottomSheet.Builder builder = new BottomSheet.Builder(context, false, null);
                                builder.setApplyBottomPadding(false);
                                LinearLayout linearLayout = new LinearLayout(context);
                                linearLayout.setOrientation(0);
                                linearLayout.setWeightSum(1.0f);
                                final NumberPicker numberPicker = new NumberPicker(context);
                                numberPicker.setAllItemsCount(strArr.length);
                                numberPicker.setItemCount(Math.min(strArr.length, 8));
                                numberPicker.setTextColor(scheduleDatePickerColors.textColor);
                                numberPicker.setGravity(17);
                                numberPicker.setMinValue(0);
                                numberPicker.setMaxValue(strArr.length - 1);
                                numberPicker.setValue(i);
                                linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 432, 1.0f));
                                numberPicker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda262
                                    @Override // org.telegram.ui.Components.NumberPicker.Formatter
                                    public final String format(int i2) {
                                        return AlertsCreator.$r8$lambda$Un4hjSam3yQ0I6lTJPG4yyeqxPE(strArr, i2);
                                    }
                                });
                                LinearLayout linearLayout2 = new LinearLayout(context) { // from class: org.telegram.ui.Components.AlertsCreator.69
                                    boolean ignoreLayout = false;

                                    @Override // android.widget.LinearLayout, android.view.View
                                    public void onMeasure(int i2, int i3) {
                                        this.ignoreLayout = true;
                                        numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * 8;
                                        this.ignoreLayout = false;
                                        super.onMeasure(i2, i3);
                                    }

                                    @Override // android.view.View, android.view.ViewParent
                                    public void requestLayout() {
                                        if (this.ignoreLayout) {
                                            return;
                                        }
                                        super.requestLayout();
                                    }
                                };
                                linearLayout2.setOrientation(1);
                                FrameLayout frameLayout = new FrameLayout(context);
                                TextView textView = new TextView(context);
                                textView.setText(str);
                                textView.setTextColor(scheduleDatePickerColors.textColor);
                                textView.setTextSize(1, 20.0f);
                                textView.setTypeface(AndroidUtilities.bold());
                                frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
                                textView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda263
                                    @Override // android.view.View.OnTouchListener
                                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                                        return AlertsCreator.m8866$r8$lambda$p3mXhtmyDGsLzASOXiFJSLjZi0(view, motionEvent);
                                    }
                                });
                                linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
                                linearLayout2.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
                                ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, null);
                                buttonWithCounterView.setText(LocaleController.getString(R.string.Select), false);
                                buttonWithCounterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda264
                                    @Override // android.view.View.OnClickListener
                                    public final void onClick(View view) {
                                        bottomSheetArr[0].lambda$new$0();
                                    }
                                });
                                linearLayout2.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 0, 16, 12, 16, 12));
                                builder.setCustomView(linearLayout2);
                                BottomSheet bottomSheetShow = builder.show();
                                bottomSheetShow.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda265
                                    @Override // android.content.DialogInterface.OnDismissListener
                                    public final void onDismiss(DialogInterface dialogInterface) {
                                        callback.run(Integer.valueOf(numberPicker.getValue()));
                                    }
                                });
                                bottomSheetShow.setBackgroundColor(scheduleDatePickerColors.backgroundColor);
                                bottomSheetShow.fixNavigationBar(scheduleDatePickerColors.backgroundColor);
                                BottomSheet bottomSheetCreate = builder.create();
                                final BottomSheet[] bottomSheetArr = {bottomSheetCreate};
                                return bottomSheetCreate;
                            }

                            public static /* synthetic */ String $r8$lambda$Un4hjSam3yQ0I6lTJPG4yyeqxPE(String[] strArr, int i) {
                                return strArr[i];
                            }

                            public static /* synthetic */ boolean m8866$r8$lambda$p3mXhtmyDGsLzASOXiFJSLjZi0(View view, MotionEvent motionEvent) {
                                return true;
                            }

                            public static void showDisableSharingInfo(Context context, Theme.ResourcesProvider resourcesProvider, final Runnable runnable) {
                                if (context == null) {
                                    return;
                                }
                                final boolean[] zArr = new boolean[1];
                                BottomSheet.Builder builder = new BottomSheet.Builder(context);
                                final Runnable dismissRunnable = builder.getDismissRunnable();
                                LinearLayout linearLayout = new LinearLayout(context);
                                linearLayout.setOrientation(1);
                                linearLayout.setClipChildren(false);
                                linearLayout.setClipToPadding(false);
                                RLottieImageView rLottieImageView = new RLottieImageView(context);
                                linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(110, 110, 17, 0, 21, 0, 11));
                                rLottieImageView.setAnimation(R.raw.raised_hand, 110, 110);
                                rLottieImageView.setAutoRepeat(false);
                                rLottieImageView.playAnimation();
                                TextView textView = new TextView(context);
                                textView.setTypeface(AndroidUtilities.bold());
                                textView.setGravity(17);
                                textView.setText(LocaleController.getString(R.string.DisableSharingInfoHeader));
                                textView.setTextSize(1, 20.0f);
                                int i = Theme.key_windowBackgroundWhiteBlackText;
                                textView.setTextColor(Theme.getColor(i, resourcesProvider));
                                linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 17, 20, 0, 20, 14));
                                PremiumFeatureCell premiumFeatureCell = new PremiumFeatureCell(context, resourcesProvider);
                                premiumFeatureCell.title.setText(LocaleController.getString(R.string.DisableSharingInfoHeader1));
                                premiumFeatureCell.description.setText(LocaleController.getString(R.string.DisableSharingInfoText1));
                                premiumFeatureCell.nextIcon.setVisibility(8);
                                premiumFeatureCell.imageView.setImageResource(R.drawable.menu_photo_off_24);
                                premiumFeatureCell.imageView.setColorFilter(Theme.getColor(i, resourcesProvider));
                                linearLayout.addView(premiumFeatureCell, LayoutHelper.createLinear(-1, -2, 6.0f, 0.0f, 6.0f, -2.0f));
                                PremiumFeatureCell premiumFeatureCell2 = new PremiumFeatureCell(context, resourcesProvider);
                                premiumFeatureCell2.title.setText(LocaleController.getString(R.string.DisableSharingInfoHeader2));
                                premiumFeatureCell2.description.setText(LocaleController.getString(R.string.DisableSharingInfoText2));
                                premiumFeatureCell2.nextIcon.setVisibility(8);
                                premiumFeatureCell2.imageView.setImageResource(R.drawable.menu_share_off_24);
                                premiumFeatureCell2.imageView.setColorFilter(Theme.getColor(i, resourcesProvider));
                                linearLayout.addView(premiumFeatureCell2, LayoutHelper.createLinear(-1, -2, 6.0f, 0.0f, 6.0f, -2.0f));
                                PremiumFeatureCell premiumFeatureCell3 = new PremiumFeatureCell(context, resourcesProvider);
                                premiumFeatureCell3.title.setText(LocaleController.getString(R.string.DisableSharingInfoHeader3));
                                premiumFeatureCell3.description.setText(LocaleController.getString(R.string.DisableSharingInfoText3));
                                premiumFeatureCell3.nextIcon.setVisibility(8);
                                premiumFeatureCell3.imageView.setImageResource(R.drawable.menu_download_off_24);
                                premiumFeatureCell3.imageView.setColorFilter(Theme.getColor(i, resourcesProvider));
                                linearLayout.addView(premiumFeatureCell3, LayoutHelper.createLinear(-1, -2, 6.0f, 0.0f, 6.0f, 8.0f));
                                ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
                                buttonWithCounterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda70
                                    @Override // android.view.View.OnClickListener
                                    public final void onClick(View view) {
                                        AlertsCreator.m8796$r8$lambda$2ShZuRUm6f3MBR0x0ROyKEMYM8(zArr, dismissRunnable, view);
                                    }
                                });
                                buttonWithCounterView.setRound();
                                buttonWithCounterView.setText(LocaleController.getString(R.string.DisableSharingInfoButton), false);
                                linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 16.0f, 10.0f, 16.0f, 8.0f));
                                builder.setCustomView(linearLayout);
                                builder.show().setOnDismissListener(new Runnable() { // from class: org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda71
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        AlertsCreator.$r8$lambda$7QqWO7kKX_hCREqJN28fu7I9nAo(zArr, runnable);
                                    }
                                });
                            }

                            public static /* synthetic */ void m8796$r8$lambda$2ShZuRUm6f3MBR0x0ROyKEMYM8(boolean[] zArr, Runnable runnable, View view) {
                                zArr[0] = true;
                                runnable.run();
                            }

                            public static /* synthetic */ void $r8$lambda$7QqWO7kKX_hCREqJN28fu7I9nAo(boolean[] zArr, Runnable runnable) {
                                if (!zArr[0] || runnable == null) {
                                    return;
                                }
                                runnable.run();
                            }
                        }
