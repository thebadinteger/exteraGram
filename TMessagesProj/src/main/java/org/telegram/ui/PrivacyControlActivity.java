package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.Keep;
import androidx.mediarouter.media.GlobalMediaRouter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.gms.cast.MediaError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SlideIntChooseView;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.bots.AffiliateProgramFragment;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private int alwaysShareRow;
    private TLRPC.PhotoSize avatarForRest;
    private TLRPC.Photo avatarForRestPhoto;
    private RLottieDrawable cameraDrawable;
    private boolean currentGiftChannelsValue;
    private boolean currentGiftIconValue;
    private boolean currentGiftLimitedValue;
    private boolean currentGiftPremiumValue;
    private boolean currentGiftUniqueValue;
    private boolean currentGiftUnlimitedValue;
    private ArrayList<Long> currentMinus;

    @Keep
    private int currentPhotoForRestRow;
    private ArrayList<Long> currentPlus;
    private final boolean[] currentPlusChannels;
    private final boolean[] currentPlusMiniapps;
    private final boolean[] currentPlusPremium;
    private boolean currentReadValue;
    private long currentStars;
    private int currentSubType;
    private int currentType;
    private int detailRow;
    private int detailRow2;
    private View doneButton;
    private CrossfadeDrawable doneButtonDrawable;

    @Keep
    private int everybodyRow;
    private int giftTypeChannelsRow;
    private int giftTypeLimitedRow;
    private int giftTypePremiumRow;
    private int giftTypeUniqueRow;
    private int giftTypeUnlimitedRow;

    @Keep
    private int giftTypesHeaderRow;
    private int giftTypesInfoRow;
    ImageUpdater imageUpdater;
    private ArrayList<Long> initialMinus;
    private ArrayList<Long> initialPlus;
    private final boolean[] initialPlusChannels;
    private final boolean[] initialPlusMiniapps;
    private final boolean[] initialPlusPremium;
    private int initialRulesSubType;
    private int initialRulesType;
    private long initialStars;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private CharSequence lockSpan;
    private MessageCell messageCell;
    private int messageRow;
    private int myContactsRow;
    private int neverShareRow;

    @Keep
    private int nobodyRow;
    private BackupImageView oldAvatarView;
    private TextCell oldPhotoCell;
    private int p2pDetailRow;
    private int p2pRow;
    private int p2pSectionRow;
    private int payRow;
    private int phoneContactsRow;
    private int phoneDetailRow;
    private int phoneEverybodyRow;
    private int phoneSectionRow;
    private int photoForRestDescriptionRow;

    @Keep
    private int photoForRestRow;
    private boolean prevSubtypeContacts;
    private int priceButtonRow;
    private int priceHeaderRow;
    private int priceInfoRow;

    @Keep
    private int priceRow;
    private int readDetailRow;
    private int readPremiumDetailRow;
    private int readPremiumRow;

    @Keep
    private int readRow;
    private int rowCount;
    private int rulesType;
    private int sectionRow;
    private boolean selectedGiftChannelsValue;
    private boolean selectedGiftIconValue;
    private boolean selectedGiftLimitedValue;
    private boolean selectedGiftPremiumValue;
    private boolean selectedGiftUniqueValue;
    private boolean selectedGiftUnlimitedValue;
    private boolean selectedReadValue;
    private TextCell setAvatarCell;

    @Keep
    private int setBirthdayRow;
    private int shakeDp;
    private int shareDetailRow;
    private int shareSectionRow;
    private int showGiftIconInfoRow;

    @Keep
    private int showGiftIconRow;

    public static public void lambda$finished$11(int i, AlertDialog alertDialog, int i2) {
        presentFragment(new PrivacyControlActivity(i), true);
    }

    public void lambda$processDone$26(SharedPreferences sharedPreferences, AlertDialog alertDialog, int i) {
        applyCurrentPrivacySettings();
        sharedPreferences.edit().putBoolean("privacyAlertShowed", true).apply();
    }

    public boolean checkDiscard(boolean z) {
        if (this.doneButton.getAlpha() != 1.0f) {
            return true;
        }
        if (!z) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.UserRestrictionsApplyChanges));
        builder.setMessage(LocaleController.getString(R.string.PrivacySettingsChangedAlert));
        builder.setPositiveButton(LocaleController.getString(R.string.ApplyTheme), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                this.f$0.lambda$checkDiscard$27(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.PassportDiscard), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                this.f$0.lambda$checkDiscard$28(alertDialog, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public void lambda$onCreateViewHolder$0(View view) {
            PrivacyControlActivity.this.showDialog(new PremiumFeatureBottomSheet(PrivacyControlActivity.this, 27, false));
        }

        private int getUsersCount(ArrayList<Long> arrayList) {
            int i = 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                long jLongValue = arrayList.get(i2).longValue();
                if (jLongValue > 0) {
                    i++;
                } else {
                    TLRPC.Chat chat = PrivacyControlActivity.this.getMessagesController().getChat(Long.valueOf(-jLongValue));
                    if (chat != null) {
                        i += chat.participants_count;
                    }
                }
            }
            return i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String rulesString;
            String string;
            String string2;
            int itemViewType = viewHolder.getItemViewType();
            i = 0;
            int i2 = 0;
            i = 0;
            int i3 = 0;
            i = 0;
            int i4 = 0;
            i = 0;
            int i5 = 0;
            i = 0;
            int i6 = 0;
            if (itemViewType == 0) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                textSettingsCell.setTextColor(PrivacyControlActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
                int i7 = PrivacyControlActivity.this.alwaysShareRow;
                PrivacyControlActivity privacyControlActivity = PrivacyControlActivity.this;
                if (i == i7) {
                    if (privacyControlActivity.currentPlus.size() != 0) {
                        string2 = LocaleController.formatPluralString("Users", getUsersCount(PrivacyControlActivity.this.currentPlus), new Object[0]);
                    } else {
                        string2 = LocaleController.getString(R.string.EmpryUsersPlaceholder);
                    }
                    if (PrivacyControlActivity.this.currentPlusPremium[PrivacyControlActivity.this.currentType == 2 ? (char) 0 : (char) 1]) {
                        if (PrivacyControlActivity.this.currentPlus == null || PrivacyControlActivity.this.currentPlus.isEmpty()) {
                            string2 = LocaleController.formatString(R.string.PrivacyPremium, new Object[0]);
                        } else {
                            string2 = LocaleController.formatString(R.string.PrivacyPremiumAnd, string2);
                        }
                    }
                    if (PrivacyControlActivity.this.rulesType != 10 && PrivacyControlActivity.this.currentPlusMiniapps[PrivacyControlActivity.this.currentType] && PrivacyControlActivity.this.currentType != 0) {
                        if (PrivacyControlActivity.this.currentPlus == null || PrivacyControlActivity.this.currentPlus.isEmpty()) {
                            string2 = LocaleController.formatString(R.string.PrivacyValueBots, new Object[0]);
                        } else {
                            string2 = LocaleController.formatString(R.string.PrivacyValueBotsAnd, string2);
                        }
                    }
                    if (PrivacyControlActivity.this.rulesType == 10) {
                        textSettingsCell.setTextAndValue(LocaleController.getString(R.string.PrivateMessagesExceptions), string2, false);
                    } else if (PrivacyControlActivity.this.rulesType != 0 && PrivacyControlActivity.this.rulesType != 4 && PrivacyControlActivity.this.rulesType != 9 && PrivacyControlActivity.this.rulesType != 14) {
                        textSettingsCell.setTextAndValue(LocaleController.getString(R.string.AlwaysAllow), string2, PrivacyControlActivity.this.neverShareRow != -1);
                    } else {
                        textSettingsCell.setTextAndValue(LocaleController.getString(R.string.AlwaysShareWith), string2, PrivacyControlActivity.this.neverShareRow != -1);
                    }
                    if (PrivacyControlActivity.this.rulesType == 12) {
                        textSettingsCell.setEnabled(!PrivacyControlActivity.this.areAllStarGiftsDisabled());
                        return;
                    }
                    return;
                }
                int i8 = privacyControlActivity.neverShareRow;
                PrivacyControlActivity privacyControlActivity2 = PrivacyControlActivity.this;
                if (i == i8) {
                    if (privacyControlActivity2.currentMinus.size() != 0) {
                        string = LocaleController.formatPluralString("Users", getUsersCount(PrivacyControlActivity.this.currentMinus), new Object[0]);
                    } else {
                        string = LocaleController.getString(R.string.EmpryUsersPlaceholder);
                    }
                    if (PrivacyControlActivity.this.currentPlusMiniapps[PrivacyControlActivity.this.currentType] && PrivacyControlActivity.this.currentType == 0) {
                        if (PrivacyControlActivity.this.currentMinus == null || PrivacyControlActivity.this.currentMinus.isEmpty()) {
                            string = LocaleController.formatString(R.string.PrivacyValueBots, new Object[0]);
                        } else {
                            string = LocaleController.formatString(R.string.PrivacyValueBotsAnd, string);
                        }
                    }
                    if (PrivacyControlActivity.this.rulesType != 0 && PrivacyControlActivity.this.rulesType != 4 && PrivacyControlActivity.this.rulesType != 9 && PrivacyControlActivity.this.rulesType != 14) {
                        textSettingsCell.setTextAndValue(LocaleController.getString(R.string.NeverAllow), string, false);
                    } else {
                        textSettingsCell.setTextAndValue(LocaleController.getString(R.string.NeverShareWith), string, false);
                    }
                    if (PrivacyControlActivity.this.rulesType == 12) {
                        textSettingsCell.setEnabled(!PrivacyControlActivity.this.areAllStarGiftsDisabled());
                        return;
                    }
                    return;
                }
                int i9 = privacyControlActivity2.p2pRow;
                PrivacyControlActivity privacyControlActivity3 = PrivacyControlActivity.this;
                if (i == i9) {
                    if (ContactsController.getInstance(((BaseFragment) privacyControlActivity3).currentAccount).getLoadingPrivacyInfo(3)) {
                        rulesString = LocaleController.getString(R.string.Loading);
                    } else {
                        rulesString = PrivacySettingsActivity.formatRulesString(PrivacyControlActivity.this.getAccountInstance(), 3);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString(R.string.PrivacyP2P2), rulesString, false);
                    return;
                }
                if (i == privacyControlActivity3.readPremiumRow) {
                    textSettingsCell.setText(LocaleController.getString(PrivacyControlActivity.this.getUserConfig().isPremium() ? R.string.PrivacyLastSeenPremiumForPremium : R.string.PrivacyLastSeenPremium), false);
                    textSettingsCell.setTextColor(PrivacyControlActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText));
                    return;
                }
                return;
            }
            if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == PrivacyControlActivity.this.detailRow2) {
                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivateMessagesChargePriceInfo));
                } else if (i == PrivacyControlActivity.this.detailRow && PrivacyControlActivity.this.rulesType == 10) {
                    textInfoPrivacyCell.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.PrivacyMessagesInfo), new Runnable() { // from class: org.telegram.ui.PrivacyControlActivity$ListAdapter$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onBindViewHolder$1();
                        }
                    }));
                } else if (i == PrivacyControlActivity.this.detailRow && PrivacyControlActivity.this.rulesType == 8) {
                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyVoiceMessagesInfo));
                } else if (i == PrivacyControlActivity.this.setBirthdayRow) {
                    textInfoPrivacyCell.setText(AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.PrivacyBirthdaySet), new Runnable() { // from class: org.telegram.ui.PrivacyControlActivity$ListAdapter$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onBindViewHolder$5();
                        }
                    }), true));
                } else {
                    int i10 = PrivacyControlActivity.this.detailRow;
                    PrivacyControlActivity privacyControlActivity4 = PrivacyControlActivity.this;
                    if (i == i10) {
                        int i11 = privacyControlActivity4.rulesType;
                        PrivacyControlActivity privacyControlActivity5 = PrivacyControlActivity.this;
                        if (i11 == 6) {
                            boolean z = privacyControlActivity5.currentType == 1 && PrivacyControlActivity.this.currentSubType == 1;
                            privacyControlActivity5.prevSubtypeContacts = z;
                            if (z) {
                                textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyPhoneInfo3));
                            } else {
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                final String str = String.format(Locale.ENGLISH, "https://t.me/+%s", PrivacyControlActivity.this.getUserConfig().getClientPhone());
                                SpannableString spannableString = new SpannableString(str);
                                spannableString.setSpan(new ClickableSpan() { // from class: org.telegram.ui.PrivacyControlActivity.ListAdapter.2
                                    @Override // android.text.style.ClickableSpan
                                    public void onClick(View view) {
                                        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", str));
                                        BulletinFactory.of(PrivacyControlActivity.this).createCopyLinkBulletin(LocaleController.getString(R.string.LinkCopied), PrivacyControlActivity.this.getResourceProvider()).show();
                                    }
                                }, 0, str.length(), 33);
                                spannableStringBuilder.append((CharSequence) LocaleController.getString(R.string.PrivacyPhoneInfo)).append((CharSequence) "\n\n").append((CharSequence) LocaleController.getString(R.string.PrivacyPhoneInfo4)).append((CharSequence) "\n").append((CharSequence) spannableString);
                                textInfoPrivacyCell.setText(spannableStringBuilder);
                            }
                        } else if (privacyControlActivity5.rulesType == 5) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyForwardsInfo));
                        } else if (PrivacyControlActivity.this.rulesType == 4) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyProfilePhotoInfo));
                        } else if (PrivacyControlActivity.this.rulesType == 9) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyBioInfo3));
                        } else if (PrivacyControlActivity.this.rulesType == 14) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyMusicInfo3));
                        } else if (PrivacyControlActivity.this.rulesType == 11) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyBirthdayInfo));
                        } else if (PrivacyControlActivity.this.rulesType == 12) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyGiftsInfo));
                        } else if (PrivacyControlActivity.this.rulesType == 3) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyCallsP2PHelp));
                        } else if (PrivacyControlActivity.this.rulesType == 2) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.WhoCanCallMeInfo));
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.WhoCanAddMeInfo));
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.CustomHelp));
                        }
                    } else {
                        int i12 = privacyControlActivity4.shareDetailRow;
                        PrivacyControlActivity privacyControlActivity6 = PrivacyControlActivity.this;
                        if (i == i12) {
                            if (privacyControlActivity6.rulesType == 6) {
                                textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyPhoneInfo2));
                            } else if (PrivacyControlActivity.this.rulesType == 5) {
                                textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyForwardsInfo2));
                            } else {
                                int i13 = PrivacyControlActivity.this.rulesType;
                                PrivacyControlActivity privacyControlActivity7 = PrivacyControlActivity.this;
                                if (i13 == 4) {
                                    if (privacyControlActivity7.currentType == 2) {
                                        textInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.PrivacyProfilePhotoInfo5)));
                                    } else if (PrivacyControlActivity.this.currentType == 0) {
                                        textInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.PrivacyProfilePhotoInfo3)));
                                    } else {
                                        textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyProfilePhotoInfo4));
                                    }
                                } else if (privacyControlActivity7.rulesType == 3) {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.CustomP2PInfo));
                                } else if (PrivacyControlActivity.this.rulesType == 9) {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyBioInfo));
                                } else if (PrivacyControlActivity.this.rulesType == 14) {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyMusicInfo));
                                } else if (PrivacyControlActivity.this.rulesType == 11) {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyBirthdayInfo3));
                                } else if (PrivacyControlActivity.this.rulesType == 2) {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.CustomCallInfo));
                                } else if (PrivacyControlActivity.this.rulesType == 1) {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.CustomShareInfo));
                                } else if (PrivacyControlActivity.this.rulesType == 12) {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.CustomShareGiftsInfo));
                                } else if (PrivacyControlActivity.this.rulesType == 8) {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyVoiceMessagesInfo2));
                                } else if (PrivacyControlActivity.this.rulesType == 10) {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivateMessagesExceptionsInfo));
                                } else {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.CustomShareSettingsHelp));
                                }
                            }
                        } else if (i == privacyControlActivity6.photoForRestDescriptionRow) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.PhotoForRestDescription));
                        } else if (i == PrivacyControlActivity.this.readDetailRow) {
                            textInfoPrivacyCell.setText(LocaleController.getString(R.string.HideReadTimeInfo));
                        } else {
                            int i14 = PrivacyControlActivity.this.readPremiumDetailRow;
                            PrivacyControlActivity privacyControlActivity8 = PrivacyControlActivity.this;
                            if (i == i14) {
                                textInfoPrivacyCell.setText(LocaleController.getString(privacyControlActivity8.getUserConfig().isPremium() ? R.string.PrivacyLastSeenPremiumInfoForPremium : R.string.PrivacyLastSeenPremiumInfo));
                            } else {
                                int i15 = privacyControlActivity8.priceInfoRow;
                                PrivacyControlActivity privacyControlActivity9 = PrivacyControlActivity.this;
                                if (i == i15) {
                                    textInfoPrivacyCell.setText(LocaleController.formatString(R.string.PrivateMessagesPriceInfo, AffiliateProgramFragment.percents(PrivacyControlActivity.this.getMessagesController().starsPaidMessageCommissionPermille), String.valueOf(((double) ((int) ((((double) (PrivacyControlActivity.this.currentStars * (privacyControlActivity9.getMessagesController().starsPaidMessageCommissionPermille / 1000.0f))) / 1000.0d) * ((double) PrivacyControlActivity.this.getMessagesController().starsUsdWithdrawRate1000)))) / 100.0d)));
                                } else if (i == privacyControlActivity9.showGiftIconInfoRow) {
                                    SpannableString spannableString2 = new SpannableString(ImageLoader.AUTOPLAY_FILTER);
                                    ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_input_gift);
                                    coloredImageSpan.setScale(0.583f, 0.583f);
                                    spannableString2.setSpan(coloredImageSpan, 0, 1, 33);
                                    textInfoPrivacyCell.setText(LocaleController.formatSpannable(R.string.PrivacyGiftsShowIconInfo, spannableString2));
                                } else if (i == PrivacyControlActivity.this.giftTypesInfoRow) {
                                    textInfoPrivacyCell.setText(LocaleController.getString(R.string.PrivacyGiftsTypeInfo));
                                }
                            }
                        }
                    }
                }
                textInfoPrivacyCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                return;
            }
            if (itemViewType == 2) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                int i16 = PrivacyControlActivity.this.sectionRow;
                PrivacyControlActivity privacyControlActivity10 = PrivacyControlActivity.this;
                if (i == i16) {
                    if (privacyControlActivity10.rulesType == 6) {
                        headerCell.setText(LocaleController.getString(R.string.PrivacyPhoneTitle));
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType == 5) {
                        headerCell.setText(LocaleController.getString(R.string.PrivacyForwardsTitle));
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType == 4) {
                        headerCell.setText(LocaleController.getString(R.string.PrivacyProfilePhotoTitle));
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType == 9) {
                        headerCell.setText(LocaleController.getString(R.string.PrivacyBioTitle));
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType == 14) {
                        headerCell.setText(LocaleController.getString(R.string.PrivacyMusicTitle));
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType == 3) {
                        headerCell.setText(LocaleController.getString(R.string.P2PEnabledWith));
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType == 2) {
                        headerCell.setText(LocaleController.getString(R.string.WhoCanCallMe));
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType == 1) {
                        headerCell.setText(LocaleController.getString(R.string.WhoCanAddMe));
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType == 8) {
                        headerCell.setText(LocaleController.getString(R.string.PrivacyVoiceMessagesTitle));
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType == 10) {
                        headerCell.setText(LocaleController.getString(R.string.PrivacyMessagesTitle));
                        return;
                    }
                    if (PrivacyControlActivity.this.rulesType == 11) {
                        headerCell.setText(LocaleController.getString(R.string.PrivacyBirthdayTitle));
                        return;
                    } else if (PrivacyControlActivity.this.rulesType == 12) {
                        headerCell.setText(LocaleController.getString(R.string.PrivacyGiftsTitle));
                        return;
                    } else {
                        headerCell.setText(LocaleController.getString(R.string.LastSeenTitle));
                        return;
                    }
                }
                int i17 = privacyControlActivity10.shareSectionRow;
                PrivacyControlActivity privacyControlActivity11 = PrivacyControlActivity.this;
                if (i == i17) {
                    if (privacyControlActivity11.rulesType == 10) {
                        headerCell.setText(LocaleController.getString(R.string.PrivateMessagesExceptionsHeader));
                        return;
                    } else {
                        headerCell.setText(LocaleController.getString(R.string.AddExceptions));
                        return;
                    }
                }
                if (i == privacyControlActivity11.p2pSectionRow) {
                    headerCell.setText(LocaleController.getString(R.string.PrivacyP2PHeader));
                    return;
                }
                if (i == PrivacyControlActivity.this.phoneSectionRow) {
                    headerCell.setText(LocaleController.getString(R.string.PrivacyPhoneTitle2));
                    return;
                } else if (i == PrivacyControlActivity.this.priceHeaderRow) {
                    headerCell.setText(LocaleController.getString(R.string.PrivateMessagesPriceHeader));
                    return;
                } else {
                    if (i == PrivacyControlActivity.this.giftTypesHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.PrivacyGiftsTypeHeader));
                        return;
                    }
                    return;
                }
            }
            if (itemViewType == 3) {
                RadioCell radioCell = (RadioCell) viewHolder.itemView;
                radioCell.setRadioIcon(null);
                if (i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow || i == PrivacyControlActivity.this.nobodyRow || i == PrivacyControlActivity.this.payRow) {
                    int i18 = PrivacyControlActivity.this.everybodyRow;
                    PrivacyControlActivity privacyControlActivity12 = PrivacyControlActivity.this;
                    if (i == i18) {
                        if (privacyControlActivity12.rulesType == 3) {
                            radioCell.setText(LocaleController.getString(R.string.P2PEverybody), PrivacyControlActivity.this.currentType == 0, true);
                        } else {
                            radioCell.setText(LocaleController.getString(R.string.LastSeenEverybody), PrivacyControlActivity.this.currentType == 0, true);
                        }
                    } else {
                        int i19 = privacyControlActivity12.myContactsRow;
                        PrivacyControlActivity privacyControlActivity13 = PrivacyControlActivity.this;
                        if (i == i19) {
                            if ((privacyControlActivity13.rulesType == 8 && !PrivacyControlActivity.this.getUserConfig().isPremium()) || (PrivacyControlActivity.this.rulesType == 10 && !PrivacyControlActivity.this.getMessagesController().newNoncontactPeersRequirePremiumWithoutOwnpremium && !PrivacyControlActivity.this.getUserConfig().isPremium())) {
                                radioCell.setRadioIcon(PrivacyControlActivity.this.getContext().getResources().getDrawable(R.drawable.mini_switch_lock).mutate());
                            }
                            if (PrivacyControlActivity.this.rulesType == 3) {
                                radioCell.setText(LocaleController.getString(R.string.P2PContacts), PrivacyControlActivity.this.currentType == 2, (PrivacyControlActivity.this.nobodyRow == -1 && PrivacyControlActivity.this.payRow == -1) ? false : true);
                            } else if (PrivacyControlActivity.this.rulesType == 10) {
                                radioCell.setText(LocaleController.getString(R.string.PrivacyMessagesContactsAndPremium), PrivacyControlActivity.this.currentType == 2, (PrivacyControlActivity.this.nobodyRow == -1 && PrivacyControlActivity.this.payRow == -1) ? false : true);
                            } else {
                                radioCell.setText(LocaleController.getString(R.string.LastSeenContacts), PrivacyControlActivity.this.currentType == 2, (PrivacyControlActivity.this.nobodyRow == -1 && PrivacyControlActivity.this.payRow == -1) ? false : true);
                            }
                        } else {
                            int i20 = privacyControlActivity13.payRow;
                            PrivacyControlActivity privacyControlActivity14 = PrivacyControlActivity.this;
                            if (i == i20) {
                                if (privacyControlActivity14.rulesType == 10 && !PrivacyControlActivity.this.getUserConfig().isPremium()) {
                                    radioCell.setRadioIcon(PrivacyControlActivity.this.getContext().getResources().getDrawable(R.drawable.mini_switch_lock).mutate());
                                }
                                radioCell.setText(LocaleController.getString(R.string.PrivateMessagesChargePrice), PrivacyControlActivity.this.currentType == 3, false);
                            } else {
                                if ((privacyControlActivity14.rulesType == 8 && !PrivacyControlActivity.this.getUserConfig().isPremium()) || (PrivacyControlActivity.this.rulesType == 10 && !PrivacyControlActivity.this.getMessagesController().newNoncontactPeersRequirePremiumWithoutOwnpremium && !PrivacyControlActivity.this.getUserConfig().isPremium())) {
                                    radioCell.setRadioIcon(PrivacyControlActivity.this.getContext().getResources().getDrawable(R.drawable.mini_switch_lock).mutate());
                                }
                                if (PrivacyControlActivity.this.rulesType == 3) {
                                    radioCell.setText(LocaleController.getString(R.string.P2PNobody), PrivacyControlActivity.this.currentType == 1, false);
                                } else {
                                    radioCell.setText(LocaleController.getString(R.string.LastSeenNobody), PrivacyControlActivity.this.currentType == 1, false);
                                }
                            }
                        }
                    }
                } else if (i == PrivacyControlActivity.this.phoneContactsRow) {
                    radioCell.setText(LocaleController.getString(R.string.LastSeenContacts), PrivacyControlActivity.this.currentSubType == 1, false);
                } else if (i == PrivacyControlActivity.this.phoneEverybodyRow) {
                    radioCell.setText(LocaleController.getString(R.string.LastSeenEverybody), PrivacyControlActivity.this.currentSubType == 0, true);
                }
                if (PrivacyControlActivity.this.rulesType == 12) {
                    radioCell.setEnabled(!PrivacyControlActivity.this.areAllStarGiftsDisabled(), null);
                    return;
                }
                return;
            }
            if (itemViewType != 8) {
                if (itemViewType != 9) {
                    return;
                }
                SlideIntChooseView slideIntChooseView = (SlideIntChooseView) viewHolder.itemView;
                if (i == PrivacyControlActivity.this.priceRow) {
                    slideIntChooseView.set((int) Utilities.clamp(PrivacyControlActivity.this.currentStars, PrivacyControlActivity.this.getMessagesController().starsPaidMessageAmountMax, 1L), SlideIntChooseView.Options.make(1, SlideIntChooseView.cut(new int[]{1, 10, 50, 100, 200, 250, MediaError.DetailedErrorCode.MANIFEST_UNKNOWN, MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, 1000, 2500, 5000, 7500, 9000, 10000}, (int) PrivacyControlActivity.this.getMessagesController().starsPaidMessageAmountMax), 20, (Utilities.Callback2Return<Integer, Integer, CharSequence>) new Utilities.Callback2Return() { // from class: org.telegram.ui.PrivacyControlActivity$ListAdapter$$ExternalSyntheticLambda3
                        @Override 
                        public final Object run(Object obj, Object obj2) {
                            return this.f$0.lambda$onBindViewHolder$6((Integer) obj, (Integer) obj2);
                        }
                    }), new Utilities.Callback() { // from class: org.telegram.ui.PrivacyControlActivity$ListAdapter$$ExternalSyntheticLambda4
                        @Override 
                        public final void run(Object obj) {
                            this.f$0.lambda$onBindViewHolder$7((Integer) obj);
                        }
                    });
                    return;
                }
                return;
            }
            TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
            textCheckCell.setCheckBoxIcon(0);
            if (i == PrivacyControlActivity.this.readRow) {
                textCheckCell.setTextAndCheck(LocaleController.getString(R.string.HideReadTime), PrivacyControlActivity.this.selectedReadValue, false);
                return;
            }
            if (i == PrivacyControlActivity.this.showGiftIconRow) {
                textCheckCell.setTextAndCheck(LocaleController.getString(R.string.PrivacyGiftsShowIcon), PrivacyControlActivity.this.selectedGiftIconValue, false);
                return;
            }
            if (i == PrivacyControlActivity.this.giftTypeUnlimitedRow) {
                textCheckCell.setTextAndCheck(LocaleController.getString(R.string.PrivacyGiftsTypeUnlimited), PrivacyControlActivity.this.selectedGiftUnlimitedValue, true);
                if (!PrivacyControlActivity.this.getUserConfig().isPremium() && PrivacyControlActivity.this.selectedGiftUnlimitedValue) {
                    i2 = R.drawable.permission_locked;
                }
                textCheckCell.setCheckBoxIcon(i2);
                return;
            }
            if (i == PrivacyControlActivity.this.giftTypeLimitedRow) {
                textCheckCell.setTextAndCheck(LocaleController.getString(R.string.PrivacyGiftsTypeLimited), PrivacyControlActivity.this.selectedGiftLimitedValue, true);
                if (!PrivacyControlActivity.this.getUserConfig().isPremium() && PrivacyControlActivity.this.selectedGiftLimitedValue) {
                    i3 = R.drawable.permission_locked;
                }
                textCheckCell.setCheckBoxIcon(i3);
                return;
            }
            if (i == PrivacyControlActivity.this.giftTypeUniqueRow) {
                textCheckCell.setTextAndCheck(LocaleController.getString(R.string.PrivacyGiftsTypeUnique), PrivacyControlActivity.this.selectedGiftUniqueValue, true);
                if (!PrivacyControlActivity.this.getUserConfig().isPremium() && PrivacyControlActivity.this.selectedGiftUniqueValue) {
                    i4 = R.drawable.permission_locked;
                }
                textCheckCell.setCheckBoxIcon(i4);
                return;
            }
            if (i == PrivacyControlActivity.this.giftTypeChannelsRow) {
                textCheckCell.setTextAndCheck(LocaleController.getString(R.string.PrivacyGiftsTypeFromChannels), PrivacyControlActivity.this.selectedGiftChannelsValue, true);
                if (!PrivacyControlActivity.this.getUserConfig().isPremium() && PrivacyControlActivity.this.selectedGiftChannelsValue) {
                    i5 = R.drawable.permission_locked;
                }
                textCheckCell.setCheckBoxIcon(i5);
                return;
            }
            if (i == PrivacyControlActivity.this.giftTypePremiumRow) {
                textCheckCell.setTextAndCheck(LocaleController.getString(R.string.PrivacyGiftsTypePremium), PrivacyControlActivity.this.selectedGiftPremiumValue, false);
                if (!PrivacyControlActivity.this.getUserConfig().isPremium() && PrivacyControlActivity.this.selectedGiftPremiumValue) {
                    i6 = R.drawable.permission_locked;
                }
                textCheckCell.setCheckBoxIcon(i6);
            }
        }

        public void lambda$onBindViewHolder$4(TL_account.TL_birthday tL_birthday) {
            TL_account.updateBirthday updatebirthday = new TL_account.updateBirthday();
            updatebirthday.flags |= 1;
            updatebirthday.birthday = tL_birthday;
            final TLRPC.UserFull userFull = PrivacyControlActivity.this.getMessagesController().getUserFull(PrivacyControlActivity.this.getUserConfig().getClientUserId());
            final TL_account.TL_birthday tL_birthday2 = userFull != null ? userFull.birthday : null;
            if (userFull != null) {
                userFull.flags2 |= 32;
                userFull.birthday = tL_birthday;
                PrivacyControlActivity.this.getMessagesStorage().updateUserInfo(userFull, false);
            }
            PrivacyControlActivity.this.getMessagesController().invalidateContentSettings();
            PrivacyControlActivity.this.getConnectionsManager().sendRequest(updatebirthday, new RequestDelegate() { // from class: org.telegram.ui.PrivacyControlActivity$ListAdapter$$ExternalSyntheticLambda6
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$onBindViewHolder$3(userFull, tL_birthday2, tLObject, tL_error);
                }
            }, 1024);
            MessagesController.getInstance(((BaseFragment) PrivacyControlActivity.this).currentAccount).removeSuggestion(0L, "BIRTHDAY_SETUP");
            NotificationCenter.getInstance(((BaseFragment) PrivacyControlActivity.this).currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.premiumPromoUpdated, new Object[0]);
            PrivacyControlActivity.this.updateRows(true);
        }

        public /* synthetic */ void lambda$onBindViewHolder$3(final TLRPC.UserFull userFull, final TL_account.TL_birthday tL_birthday, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PrivacyControlActivity$ListAdapter$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onBindViewHolder$2(tLObject, userFull, tL_birthday, tL_error);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$2(TLObject tLObject, TLRPC.UserFull userFull, TL_account.TL_birthday tL_birthday, TLRPC.TL_error tL_error) {
            String str;
            if (tLObject instanceof TLRPC.TL_boolTrue) {
                BulletinFactory.of(PrivacyControlActivity.this).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.PrivacyBirthdaySetDone)).setDuration(5000).show();
                return;
            }
            if (userFull != null) {
                int i = userFull.flags2;
                if (tL_birthday == null) {
                    userFull.flags2 = i & (-33);
                } else {
                    userFull.flags2 = i | 32;
                }
                userFull.birthday = tL_birthday;
                PrivacyControlActivity.this.getMessagesStorage().updateUserInfo(userFull, false);
            }
            if (tL_error != null && (str = tL_error.text) != null && str.startsWith("FLOOD_WAIT_")) {
                if (PrivacyControlActivity.this.getContext() != null) {
                    PrivacyControlActivity privacyControlActivity = PrivacyControlActivity.this;
                    privacyControlActivity.showDialog(new AlertDialog.Builder(privacyControlActivity.getContext(), ((BaseFragment) PrivacyControlActivity.this).resourceProvider).setTitle(LocaleController.getString(R.string.PrivacyBirthdayTooOftenTitle)).setMessage(LocaleController.getString(R.string.PrivacyBirthdayTooOftenMessage)).setPositiveButton(LocaleController.getString(R.string.OK), null).create());
                    return;
                }
                return;
            }
            BulletinFactory.of(PrivacyControlActivity.this).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.UnknownError)).show();
        }

        public /* synthetic */ CharSequence lambda$onBindViewHolder$6(Integer num, Integer num2) {
            if (num.intValue() == 0) {
                if (!PrivacyControlActivity.this.getUserConfig().isPremium()) {
                    if (PrivacyControlActivity.this.lockSpan == null) {
                        SpannableString spannableString = new SpannableString("l");
                        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_mini_lock3);
                        coloredImageSpan.translate(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(1.0f));
                        spannableString.setSpan(coloredImageSpan, 0, 1, 33);
                        PrivacyControlActivity.this.lockSpan = spannableString;
                    }
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    spannableStringBuilder.append(PrivacyControlActivity.this.lockSpan);
                    spannableStringBuilder.append((CharSequence) " ");
                    spannableStringBuilder.append((CharSequence) LocaleController.formatPluralStringComma("Stars", num2.intValue()));
                    return spannableStringBuilder;
                }
                return LocaleController.formatPluralStringComma("Stars", num2.intValue());
            }
            return LocaleController.formatNumber(num2.intValue(), ',');
        }

        public /* synthetic */ void lambda$onBindViewHolder$7(Integer num) {
            PrivacyControlActivity.this.currentStars = num.intValue();
            AndroidUtilities.updateVisibleRow(PrivacyControlActivity.this.listView, PrivacyControlActivity.this.priceInfoRow);
            PrivacyControlActivity.this.updateDoneButton();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == PrivacyControlActivity.this.alwaysShareRow || i == PrivacyControlActivity.this.neverShareRow || i == PrivacyControlActivity.this.p2pRow || i == PrivacyControlActivity.this.readPremiumRow) {
                return 0;
            }
            if (i == PrivacyControlActivity.this.shareDetailRow || i == PrivacyControlActivity.this.detailRow || i == PrivacyControlActivity.this.detailRow2 || i == PrivacyControlActivity.this.priceInfoRow || i == PrivacyControlActivity.this.p2pDetailRow || i == PrivacyControlActivity.this.photoForRestDescriptionRow || i == PrivacyControlActivity.this.readDetailRow || i == PrivacyControlActivity.this.readPremiumDetailRow || i == PrivacyControlActivity.this.setBirthdayRow || i == PrivacyControlActivity.this.showGiftIconInfoRow || i == PrivacyControlActivity.this.giftTypesInfoRow) {
                return 1;
            }
            if (i == PrivacyControlActivity.this.sectionRow || i == PrivacyControlActivity.this.priceHeaderRow || i == PrivacyControlActivity.this.shareSectionRow || i == PrivacyControlActivity.this.p2pSectionRow || i == PrivacyControlActivity.this.phoneSectionRow || i == PrivacyControlActivity.this.giftTypesHeaderRow) {
                return 2;
            }
            if (i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow || i == PrivacyControlActivity.this.nobodyRow || i == PrivacyControlActivity.this.payRow || i == PrivacyControlActivity.this.phoneEverybodyRow || i == PrivacyControlActivity.this.phoneContactsRow) {
                return 3;
            }
            if (i == PrivacyControlActivity.this.messageRow) {
                return 4;
            }
            if (i == PrivacyControlActivity.this.phoneDetailRow) {
                return 5;
            }
            if (i == PrivacyControlActivity.this.photoForRestRow) {
                return 6;
            }
            if (i == PrivacyControlActivity.this.currentPhotoForRestRow) {
                return 7;
            }
            if (i == PrivacyControlActivity.this.readRow || i == PrivacyControlActivity.this.showGiftIconRow || i == PrivacyControlActivity.this.giftTypeUniqueRow || i == PrivacyControlActivity.this.giftTypeChannelsRow || i == PrivacyControlActivity.this.giftTypePremiumRow || i == PrivacyControlActivity.this.giftTypeUnlimitedRow || i == PrivacyControlActivity.this.giftTypeLimitedRow) {
                return 8;
            }
            if (i == PrivacyControlActivity.this.priceRow) {
                return 9;
            }
            return i == PrivacyControlActivity.this.priceButtonRow ? 10 : 0;
        }
    }

    public class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        SparseIntArray oldPositionToItem;
        int oldRowCount;

        private DiffCallback() {
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getOldListSize() {
            return this.oldRowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getNewListSize() {
            return PrivacyControlActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areItemsTheSame(int i, int i2) {
            int i3 = this.oldPositionToItem.get(i, -1);
            return i3 == this.newPositionToItem.get(i2, -1) && i3 >= 0;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areContentsTheSame(int i, int i2) {
            return areItemsTheSame(i, i2);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            put(1, PrivacyControlActivity.this.messageRow, sparseIntArray);
            put(2, PrivacyControlActivity.this.sectionRow, sparseIntArray);
            put(3, PrivacyControlActivity.this.everybodyRow, sparseIntArray);
            put(4, PrivacyControlActivity.this.myContactsRow, sparseIntArray);
            put(5, PrivacyControlActivity.this.nobodyRow, sparseIntArray);
            put(6, PrivacyControlActivity.this.payRow, sparseIntArray);
            put(7, PrivacyControlActivity.this.detailRow, sparseIntArray);
            put(8, PrivacyControlActivity.this.shareSectionRow, sparseIntArray);
            put(9, PrivacyControlActivity.this.alwaysShareRow, sparseIntArray);
            put(10, PrivacyControlActivity.this.neverShareRow, sparseIntArray);
            put(11, PrivacyControlActivity.this.shareDetailRow, sparseIntArray);
            put(12, PrivacyControlActivity.this.phoneSectionRow, sparseIntArray);
            put(13, PrivacyControlActivity.this.phoneEverybodyRow, sparseIntArray);
            put(14, PrivacyControlActivity.this.phoneContactsRow, sparseIntArray);
            put(15, PrivacyControlActivity.this.phoneDetailRow, sparseIntArray);
            put(16, PrivacyControlActivity.this.photoForRestRow, sparseIntArray);
            put(17, PrivacyControlActivity.this.currentPhotoForRestRow, sparseIntArray);
            put(18, PrivacyControlActivity.this.photoForRestDescriptionRow, sparseIntArray);
            put(19, PrivacyControlActivity.this.p2pSectionRow, sparseIntArray);
            put(20, PrivacyControlActivity.this.p2pRow, sparseIntArray);
            put(21, PrivacyControlActivity.this.p2pDetailRow, sparseIntArray);
            put(22, PrivacyControlActivity.this.readRow, sparseIntArray);
            put(23, PrivacyControlActivity.this.readDetailRow, sparseIntArray);
            put(24, PrivacyControlActivity.this.readPremiumRow, sparseIntArray);
            put(25, PrivacyControlActivity.this.readPremiumDetailRow, sparseIntArray);
            put(26, PrivacyControlActivity.this.priceHeaderRow, sparseIntArray);
            put(27, PrivacyControlActivity.this.priceRow, sparseIntArray);
            put(28, PrivacyControlActivity.this.priceInfoRow, sparseIntArray);
            put(29, PrivacyControlActivity.this.showGiftIconRow, sparseIntArray);
            put(30, PrivacyControlActivity.this.showGiftIconInfoRow, sparseIntArray);
            put(31, PrivacyControlActivity.this.giftTypesHeaderRow, sparseIntArray);
            put(32, PrivacyControlActivity.this.giftTypeLimitedRow, sparseIntArray);
            put(33, PrivacyControlActivity.this.giftTypeUnlimitedRow, sparseIntArray);
            put(34, PrivacyControlActivity.this.giftTypeUniqueRow, sparseIntArray);
            put(35, PrivacyControlActivity.this.giftTypeChannelsRow, sparseIntArray);
            put(36, PrivacyControlActivity.this.giftTypePremiumRow, sparseIntArray);
            put(37, PrivacyControlActivity.this.giftTypesInfoRow, sparseIntArray);
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, null, null, null, i));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        int i3 = Theme.key_windowBackgroundGray;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
        RecyclerListView recyclerListView = this.listView;
        Drawable[] shadowDrawables = Theme.chat_msgInDrawable.getShadowDrawables();
        int i4 = Theme.key_chat_inBubbleShadow;
        arrayList.add(new ThemeDescription(recyclerListView, 0, null, null, shadowDrawables, null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
        RecyclerListView recyclerListView2 = this.listView;
        Drawable[] shadowDrawables2 = Theme.chat_msgOutDrawable.getShadowDrawables();
        int i5 = Theme.key_chat_outBubbleShadow;
        arrayList.add(new ThemeDescription(recyclerListView2, 0, null, null, shadowDrawables2, null, i5));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, i5));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_messageTextIn));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_messageTextOut));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyLine));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyLine));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyNameText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyNameText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyMessageText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyMessageText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inTimeText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outTimeText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inTimeSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outTimeSelectedText));
        return arrayList;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onInsets(int i, int i2, int i3, int i4) {
        this.listView.setPadding(0, 0, 0, i4);
        this.listView.setClipToPadding(false);
    }
}
