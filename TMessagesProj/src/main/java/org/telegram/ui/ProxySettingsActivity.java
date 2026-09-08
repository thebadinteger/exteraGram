package org.telegram.ui;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.proxy.ProxyController;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.mvel2.MVEL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.QRCodeBottomSheet;

public class ProxySettingsActivity extends BaseFragment {
    private boolean addingNewProxy;
    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener;
    private ClipboardManager clipboardManager;
    private SharedConfig.ProxyInfo currentProxyInfo;
    private int currentType;
    private ActionBarMenuItem doneItem;
    private HeaderCell headerCell;
    private boolean ignoreOnTextChange;
    private OutlineTextContainerView[] inputFieldContainers;
    private EditTextBoldCursor[] inputFields;
    private LinearLayout inputFieldsContainer;
    private LinearLayout linearLayout2;
    private TextSettingsCell pasteCell;
    private LinearLayout pasteContainer;
    private String[] pasteFields;
    private String pasteString;
    private int pasteType;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell shareCell;
    private LinearLayout shareContainer;
    private ValueAnimator shareDoneAnimator;
    private boolean shareDoneEnabled;
    private float shareDoneProgress;
    private float[] shareDoneProgressAnimValues;
    private TextInfoPrivacyCell sponsorInfoCell;
    private RadioCell[] typeCell;
    private LinearLayout typeContainer;

    public ProxySettingsActivity() {
        this.sectionCell = new ShadowSectionCell[3];
        this.typeCell = new RadioCell[2];
        this.currentType = -1;
        this.pasteType = -1;
        this.shareDoneProgress = 1.0f;
        this.shareDoneProgressAnimValues = new float[2];
        this.shareDoneEnabled = true;
        this.clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda0
            @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
            public final void onPrimaryClipChanged() {
                this.f$0.updatePasteCell();
            }
        };
        this.currentProxyInfo = new SharedConfig.ProxyInfo(_UrlKt.FRAGMENT_ENCODE_SET, 1080, _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET);
        this.addingNewProxy = true;
    }

    public ProxySettingsActivity(SharedConfig.ProxyInfo proxyInfo) {
        this.sectionCell = new ShadowSectionCell[3];
        this.typeCell = new RadioCell[2];
        this.currentType = -1;
        this.pasteType = -1;
        this.shareDoneProgress = 1.0f;
        this.shareDoneProgressAnimValues = new float[2];
        this.shareDoneEnabled = true;
        this.clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda0
            @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
            public final void onPrimaryClipChanged() {
                this.f$0.updatePasteCell();
            }
        };
        this.currentProxyInfo = proxyInfo;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        this.clipboardManager.addPrimaryClipChangedListener(this.clipChangedListener);
        updatePasteCell();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        this.clipboardManager.removePrimaryClipChangedListener(this.clipChangedListener);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setTitle(LocaleController.getString(R.string.ProxyDetails));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null && iNavigationLayout.isLayersLayout()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ProxySettingsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    ProxySettingsActivity.this.finishFragment();
                    return;
                }
                if (i != 1 || ProxySettingsActivity.this.getParentActivity() == null) {
                    return;
                }
                String strTrim = ProxySettingsActivity.this.inputFields[0].getText().toString().trim();
                String link = ProxySettingsActivity.this.addingNewProxy ? null : ProxySettingsActivity.this.currentProxyInfo.getLink();
                ProxySettingsActivity.this.currentProxyInfo.address = ProxySettingsActivity.this.inputFields[1].getText().toString();
                ProxySettingsActivity.this.currentProxyInfo.port = Utilities.parseInt((CharSequence) ProxySettingsActivity.this.inputFields[2].getText().toString()).intValue();
                int i2 = ProxySettingsActivity.this.currentType;
                ProxySettingsActivity proxySettingsActivity = ProxySettingsActivity.this;
                if (i2 == 0) {
                    proxySettingsActivity.currentProxyInfo.secret = _UrlKt.FRAGMENT_ENCODE_SET;
                    ProxySettingsActivity.this.currentProxyInfo.username = ProxySettingsActivity.this.inputFields[3].getText().toString();
                    ProxySettingsActivity.this.currentProxyInfo.password = ProxySettingsActivity.this.inputFields[4].getText().toString();
                } else {
                    proxySettingsActivity.currentProxyInfo.secret = ProxySettingsActivity.this.inputFields[5].getText().toString();
                    ProxySettingsActivity.this.currentProxyInfo.username = _UrlKt.FRAGMENT_ENCODE_SET;
                    ProxySettingsActivity.this.currentProxyInfo.password = _UrlKt.FRAGMENT_ENCODE_SET;
                }
                ProxyController proxyController = ProxyController.getInstance();
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                boolean z = ProxySettingsActivity.this.addingNewProxy || globalMainSettings.getBoolean("proxy_enabled", false);
                ProxySettingsActivity proxySettingsActivity2 = ProxySettingsActivity.this;
                proxySettingsActivity2.currentProxyInfo = proxyController.saveProxy(proxySettingsActivity2.currentProxyInfo, link, strTrim);
                if (ProxySettingsActivity.this.addingNewProxy) {
                    proxyController.setCurrentProxy(ProxySettingsActivity.this.currentProxyInfo);
                }
                if (ProxySettingsActivity.this.addingNewProxy || proxyController.getCurrentProxy() == ProxySettingsActivity.this.currentProxyInfo) {
                    SharedPreferences.Editor editorEdit = globalMainSettings.edit();
                    editorEdit.putBoolean("proxy_enabled", z);
                    editorEdit.putString("proxy_ip", ProxySettingsActivity.this.currentProxyInfo.address);
                    editorEdit.putString("proxy_pass", ProxySettingsActivity.this.currentProxyInfo.password);
                    editorEdit.putString("proxy_user", ProxySettingsActivity.this.currentProxyInfo.username);
                    editorEdit.putInt("proxy_port", ProxySettingsActivity.this.currentProxyInfo.port);
                    editorEdit.putString("proxy_secret", ProxySettingsActivity.this.currentProxyInfo.secret);
                    ConnectionsManager.setProxySettings(z, ProxySettingsActivity.this.currentProxyInfo.address, ProxySettingsActivity.this.currentProxyInfo.port, ProxySettingsActivity.this.currentProxyInfo.username, ProxySettingsActivity.this.currentProxyInfo.password, ProxySettingsActivity.this.currentProxyInfo.secret);
                    editorEdit.apply();
                }
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.proxySettingsChanged, new Object[0]);
                ProxySettingsActivity.this.finishFragment();
            }
        });
        ActionBarMenuItem actionBarMenuItemAddItemWithWidth = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f));
        this.doneItem = actionBarMenuItemAddItemWithWidth;
        actionBarMenuItemAddItemWithWidth.setContentDescription(LocaleController.getString(R.string.Done));
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ScrollView scrollView = new ScrollView(context);
        this.scrollView = scrollView;
        scrollView.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.linearLayout2 = linearLayout;
        linearLayout.setOrientation(1);
        this.scrollView.addView(this.linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        int i = 6;
        this.inputFields = new EditTextBoldCursor[6];
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createView$0(view);
            }
        };
        LinearLayout linearLayoutCreateRoundedContainer = createRoundedContainer(context);
        this.pasteContainer = linearLayoutCreateRoundedContainer;
        linearLayoutCreateRoundedContainer.setVisibility(8);
        this.linearLayout2.addView(this.pasteContainer, LayoutHelper.createLinear(-1, -2, 16.0f, 14.0f, 16.0f, 0.0f));
        LinearLayout linearLayoutCreateRoundedContainer2 = createRoundedContainer(context);
        this.typeContainer = linearLayoutCreateRoundedContainer2;
        this.linearLayout2.addView(linearLayoutCreateRoundedContainer2, LayoutHelper.createLinear(-1, -2, 16.0f, 14.0f, 16.0f, 0.0f));
        int i2 = 0;
        while (i2 < 2) {
            this.typeCell[i2] = new RadioCell(context);
            View[] viewArr = this.typeCell;
            setRoundedSelector(viewArr[i2], i2 == 0, i2 == viewArr.length - 1);
            this.typeCell[i2].setTag(Integer.valueOf(i2));
            RadioCell[] radioCellArr = this.typeCell;
            if (i2 == 0) {
                radioCellArr[i2].setText(LocaleController.getString(R.string.UseProxySocks5), i2 == this.currentType, true);
            } else {
                radioCellArr[i2].setText(LocaleController.getString(R.string.UseProxyTelegram), i2 == this.currentType, false);
            }
            this.typeContainer.addView(this.typeCell[i2], LayoutHelper.createLinear(-1, 50));
            this.typeCell[i2].setOnClickListener(onClickListener);
            i2++;
        }
        this.sectionCell[0] = new ShadowSectionCell(context);
        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, 14));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.inputFieldsContainer = linearLayout2;
        linearLayout2.setOrientation(1);
        this.linearLayout2.addView(this.inputFieldsContainer, LayoutHelper.createLinear(-1, -2, 16.0f, 0.0f, 16.0f, 0.0f));
        this.inputFieldContainers = new OutlineTextContainerView[6];
        int i3 = 0;
        while (true) {
            int i4 = 4;
            int i5 = 5;
            if (i3 < i) {
                OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
                this.inputFieldContainers[i3] = outlineTextContainerView;
                this.inputFields[i3] = new EditTextBoldCursor(context);
                this.inputFields[i3].setTag(Integer.valueOf(i3));
                this.inputFields[i3].setTextSize(1, 16.0f);
                this.inputFields[i3].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                EditTextBoldCursor editTextBoldCursor = this.inputFields[i3];
                int i6 = Theme.key_windowBackgroundWhiteBlackText;
                editTextBoldCursor.setTextColor(Theme.getColor(i6));
                this.inputFields[i3].setBackground(null);
                this.inputFields[i3].setCursorColor(Theme.getColor(i6));
                this.inputFields[i3].setCursorSize(AndroidUtilities.dp(20.0f));
                this.inputFields[i3].setCursorWidth(1.5f);
                this.inputFields[i3].setSingleLine(true);
                this.inputFields[i3].setGravity(LocaleController.isRTL ? 5 : 3);
                if (i3 == 0) {
                    this.inputFields[i3].setInputType(524289);
                    this.inputFields[i3].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ProxySettingsActivity.2
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence charSequence, int i7, int i8, int i9) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence charSequence, int i7, int i8, int i9) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable editable) {
                            ProxySettingsActivity.this.updateActionBarTitle();
                            ProxySettingsActivity proxySettingsActivity = ProxySettingsActivity.this;
                            proxySettingsActivity.updateFieldContainerState(0, proxySettingsActivity.inputFields[0].hasFocus(), true);
                        }
                    });
                } else if (i3 == 1) {
                    this.inputFields[i3].setInputType(524305);
                    this.inputFields[i3].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ProxySettingsActivity.3
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence charSequence, int i7, int i8, int i9) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence charSequence, int i7, int i8, int i9) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable editable) {
                            ProxySettingsActivity proxySettingsActivity = ProxySettingsActivity.this;
                            proxySettingsActivity.updateFieldContainerState(1, proxySettingsActivity.inputFields[1].hasFocus(), true);
                            ProxySettingsActivity.this.checkShareDone(true);
                        }
                    });
                } else if (i3 == 2) {
                    this.inputFields[i3].setInputType(2);
                    this.inputFields[i3].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ProxySettingsActivity.4
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence charSequence, int i7, int i8, int i9) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence charSequence, int i7, int i8, int i9) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable editable) {
                            if (ProxySettingsActivity.this.ignoreOnTextChange) {
                                return;
                            }
                            EditTextBoldCursor editTextBoldCursor2 = ProxySettingsActivity.this.inputFields[2];
                            int selectionStart = editTextBoldCursor2.getSelectionStart();
                            String string = editTextBoldCursor2.getText().toString();
                            StringBuilder sb = new StringBuilder(string.length());
                            int i7 = 0;
                            while (i7 < string.length()) {
                                int i8 = i7 + 1;
                                String strSubstring = string.substring(i7, i8);
                                if ("0123456789".contains(strSubstring)) {
                                    sb.append(strSubstring);
                                }
                                i7 = i8;
                            }
                            ProxySettingsActivity.this.ignoreOnTextChange = true;
                            int iIntValue = Utilities.parseInt((CharSequence) sb.toString()).intValue();
                            if (iIntValue < 0 || iIntValue > 65535 || !string.equals(sb.toString())) {
                                if (iIntValue < 0) {
                                    editTextBoldCursor2.setText(MVEL.VERSION_SUB);
                                } else if (iIntValue > 65535) {
                                    editTextBoldCursor2.setText("65535");
                                } else {
                                    editTextBoldCursor2.setText(sb.toString());
                                }
                            } else if (selectionStart >= 0) {
                                editTextBoldCursor2.setSelection(Math.min(selectionStart, editTextBoldCursor2.length()));
                            }
                            ProxySettingsActivity.this.ignoreOnTextChange = false;
                            ProxySettingsActivity proxySettingsActivity = ProxySettingsActivity.this;
                            proxySettingsActivity.updateFieldContainerState(2, proxySettingsActivity.inputFields[2].hasFocus(), true);
                            ProxySettingsActivity.this.checkShareDone(true);
                        }
                    });
                } else if (i3 == 4) {
                    this.inputFields[i3].setInputType(129);
                    this.inputFields[i3].setTypeface(Typeface.DEFAULT);
                    this.inputFields[i3].setTransformationMethod(PasswordTransformationMethod.getInstance());
                    this.inputFields[i3].addTextChangedListener(new SimpleFieldTextWatcher(i4));
                } else {
                    EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                    if (i3 == 5) {
                        editTextBoldCursorArr[i3].setInputType(524289);
                        this.inputFields[i3].setTypeface(Typeface.DEFAULT);
                        this.inputFields[i3].setTransformationMethod(PasswordTransformationMethod.getInstance());
                        this.inputFields[i3].addTextChangedListener(new SimpleFieldTextWatcher(i5));
                    } else {
                        editTextBoldCursorArr[i3].setInputType(524289);
                        this.inputFields[i3].addTextChangedListener(new SimpleFieldTextWatcher(i3));
                    }
                }
                this.inputFields[i3].setImeOptions(268435461);
                if (i3 == 0) {
                    outlineTextContainerView.setText(LocaleController.getString(R.string.ProxyRename));
                    this.inputFields[i3].setText(ProxyController.getInstance().getName(this.currentProxyInfo));
                } else if (i3 == 1) {
                    outlineTextContainerView.setText(LocaleController.getString(R.string.UseProxyAddress));
                    this.inputFields[i3].setText(this.currentProxyInfo.address);
                } else if (i3 == 2) {
                    outlineTextContainerView.setText(LocaleController.getString(R.string.UseProxyPort));
                    this.inputFields[i3].setText(_UrlKt.FRAGMENT_ENCODE_SET + this.currentProxyInfo.port);
                } else if (i3 == 3) {
                    outlineTextContainerView.setText(LocaleController.getString(R.string.UseProxyUsername));
                    this.inputFields[i3].setText(this.currentProxyInfo.username);
                } else if (i3 == 4) {
                    outlineTextContainerView.setText(LocaleController.getString(R.string.UseProxyPassword));
                    this.inputFields[i3].setText(this.currentProxyInfo.password);
                } else if (i3 == 5) {
                    outlineTextContainerView.setText(LocaleController.getString(R.string.UseProxySecret));
                    this.inputFields[i3].setText(this.currentProxyInfo.secret);
                }
                EditTextBoldCursor editTextBoldCursor2 = this.inputFields[i3];
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                this.inputFields[i3].setPadding(0, AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f));
                outlineTextContainerView.addView(this.inputFields[i3], LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 0.0f, 16.0f, 0.0f));
                outlineTextContainerView.attachEditText(this.inputFields[i3]);
                this.inputFields[i3].setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view, boolean z) {
                        this.f$0.lambda$createView$1(view, z);
                    }
                });
                updateFieldContainerState(i3, false, false);
                this.inputFields[i3].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda3
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView, int i7, KeyEvent keyEvent) {
                        return this.f$0.lambda$createView$2(textView, i7, keyEvent);
                    }
                });
                i3++;
                i = 6;
            } else {
                this.inputFieldsContainer.addView(this.inputFieldContainers[0], LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 12.0f));
                LinearLayout linearLayout3 = new LinearLayout(context);
                linearLayout3.setOrientation(0);
                this.inputFieldsContainer.addView(linearLayout3, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 12.0f));
                linearLayout3.addView(this.inputFieldContainers[1], LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 8, 0));
                linearLayout3.addView(this.inputFieldContainers[2], LayoutHelper.createLinear(112, -2, 8.0f, 0.0f, 0.0f, 0.0f));
                this.inputFieldsContainer.addView(this.inputFieldContainers[3], LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 12.0f));
                this.inputFieldsContainer.addView(this.inputFieldContainers[4], LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 12.0f));
                this.inputFieldsContainer.addView(this.inputFieldContainers[5], LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 12.0f));
                updateActionBarTitle();
                TextSettingsCell textSettingsCell = new TextSettingsCell(this.fragmentView.getContext());
                this.pasteCell = textSettingsCell;
                setRoundedSelector(textSettingsCell, true, true);
                this.pasteCell.setText(LocaleController.getString(R.string.PasteFromClipboard), false);
                TextSettingsCell textSettingsCell2 = this.pasteCell;
                int i7 = Theme.key_windowBackgroundWhiteBlueText4;
                textSettingsCell2.setTextColor(Theme.getColor(i7));
                this.pasteCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda4
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$createView$4(view);
                    }
                });
                this.pasteContainer.addView(this.pasteCell, LayoutHelper.createLinear(-1, -2));
                this.pasteCell.setVisibility(8);
                LinearLayout linearLayoutCreateRoundedContainer3 = createRoundedContainer(context);
                this.shareContainer = linearLayoutCreateRoundedContainer3;
                this.linearLayout2.addView(linearLayoutCreateRoundedContainer3, LayoutHelper.createLinear(-1, -2, 16.0f, 0.0f, 16.0f, 0.0f));
                TextSettingsCell textSettingsCell3 = new TextSettingsCell(context);
                this.shareCell = textSettingsCell3;
                setRoundedSelector(textSettingsCell3, true, true);
                this.shareCell.setText(LocaleController.getString(R.string.ShareFile), false);
                this.shareCell.setTextColor(Theme.getColor(i7));
                this.shareContainer.addView(this.shareCell, LayoutHelper.createLinear(-1, -2));
                this.shareCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda5
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$createView$5(context, view);
                    }
                });
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
                this.sponsorInfoCell = textInfoPrivacyCell;
                textInfoPrivacyCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                this.sponsorInfoCell.setText(LocaleController.getString(R.string.UseProxyTelegramInfo2));
                this.sponsorInfoCell.setVisibility(8);
                this.linearLayout2.addView(this.sponsorInfoCell, LayoutHelper.createLinear(-1, -2));
                this.sectionCell[1] = new ShadowSectionCell(context);
                this.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawableByKey(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
                this.clipboardManager = (ClipboardManager) context.getSystemService("clipboard");
                this.shareDoneEnabled = true;
                this.shareDoneProgress = 1.0f;
                checkShareDone(false);
                this.currentType = -1;
                setProxyType(!TextUtils.isEmpty(this.currentProxyInfo.secret) ? 1 : 0, false);
                this.pasteType = -1;
                this.pasteString = null;
                updatePasteCell();
                return this.fragmentView;
            }
        }
    }

    public /* synthetic */ void lambda$createView$0(View view) {
        setProxyType(((Integer) view.getTag()).intValue(), true);
    }

    public /* synthetic */ void lambda$createView$1(View view, boolean z) {
        int iIntValue = ((Integer) view.getTag()).intValue();
        updateFieldContainerState(iIntValue, z, true);
        if (iIntValue == 5) {
            updateSecretVisibility(z);
        }
    }

    public /* synthetic */ boolean lambda$createView$2(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            if (i != 6) {
                return false;
            }
            finishFragment();
            return true;
        }
        int iIntValue = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (iIntValue < editTextBoldCursorArr.length) {
            editTextBoldCursorArr[iIntValue].requestFocus();
        }
        return true;
    }

    public /* synthetic */ void lambda$createView$4(View view) {
        if (this.pasteType == -1) {
            return;
        }
        int i = 0;
        while (true) {
            String[] strArr = this.pasteFields;
            if (i < strArr.length) {
                int i2 = this.pasteType;
                if ((i2 != 0 || i != 5) && (i2 != 1 || (i != 3 && i != 4))) {
                    String str = strArr[i];
                    EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                    if (str != null) {
                        editTextBoldCursorArr[i].setText(str);
                    } else {
                        editTextBoldCursorArr[i].setText((CharSequence) null);
                    }
                    updateFieldContainerState(i, this.inputFields[i].hasFocus(), false);
                }
                i++;
            } else {
                EditTextBoldCursor editTextBoldCursor = this.inputFields[1];
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
                setProxyType(this.pasteType, true, new Runnable() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$createView$3();
                    }
                });
                return;
            }
        }
    }

    public /* synthetic */ void lambda$createView$3() {
        AndroidUtilities.hideKeyboard(this.inputFieldsContainer.findFocus());
        for (int i = 1; i < this.pasteFields.length; i++) {
            int i2 = this.pasteType;
            if ((i2 != 0 || i == 5) && (i2 != 1 || i == 3 || i == 4)) {
                this.inputFields[i].setText((CharSequence) null);
                updateFieldContainerState(i, this.inputFields[i].hasFocus(), false);
            }
        }
    }

    public /* synthetic */ void lambda$createView$5(Context context, View view) {
        String strBuildShareLink = ProxyController.getInstance().buildShareLink(this.currentProxyInfo, this.inputFields[0].getText().toString().trim());
        if (TextUtils.isEmpty(strBuildShareLink)) {
            return;
        }
        QRCodeBottomSheet qRCodeBottomSheet = new QRCodeBottomSheet(context, LocaleController.getString(R.string.ShareQrCode), strBuildShareLink, LocaleController.getString(R.string.QRCodeLinkHelpProxy), true);
        qRCodeBottomSheet.setCenterImage(SvgHelper.getBitmap(AndroidUtilities.readRes(R.raw.qr_dog), AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f), false));
        showDialog(qRCodeBottomSheet);
    }

    private LinearLayout createRoundedContainer(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setBackground(Theme.createRoundRectDrawable(getProxyContainerCornerRadius(), Theme.getColor(Theme.key_windowBackgroundWhite)));
        linearLayout.setClipToPadding(false);
        return linearLayout;
    }

    private int getProxyContainerCornerRadius() {
        return AndroidUtilities.dp(ExteraConfig.getSectionRadiusDp());
    }

    private void setRoundedSelector(View view, boolean z, boolean z2) {
        if (view != null) {
            int sectionRadiusDp = ExteraConfig.getSectionRadiusDp();
            int color = Theme.getColor(Theme.key_listSelector);
            int i = z ? sectionRadiusDp : 0;
            if (!z2) {
                sectionRadiusDp = 0;
            }
            view.setBackground(Theme.createRadSelectorDrawable(color, i, sectionRadiusDp));
        }
    }

    private void updateRoundedContainerColors() {
        int proxyContainerCornerRadius = getProxyContainerCornerRadius();
        int color = Theme.getColor(Theme.key_windowBackgroundWhite);
        LinearLayout linearLayout = this.pasteContainer;
        if (linearLayout != null) {
            linearLayout.setBackground(Theme.createRoundRectDrawable(proxyContainerCornerRadius, color));
        }
        LinearLayout linearLayout2 = this.typeContainer;
        if (linearLayout2 != null) {
            linearLayout2.setBackground(Theme.createRoundRectDrawable(proxyContainerCornerRadius, color));
        }
        LinearLayout linearLayout3 = this.shareContainer;
        if (linearLayout3 != null) {
            linearLayout3.setBackground(Theme.createRoundRectDrawable(proxyContainerCornerRadius, color));
        }
        setRoundedSelector(this.pasteCell, true, true);
        setRoundedSelector(this.shareCell, true, true);
        if (this.typeCell == null) {
            return;
        }
        int i = 0;
        while (true) {
            RadioCell[] radioCellArr = this.typeCell;
            if (i >= radioCellArr.length) {
                return;
            }
            setRoundedSelector(radioCellArr[i], i == 0, i == radioCellArr.length - 1);
            i++;
        }
    }

    private void updateSecretVisibility(boolean z) {
        EditTextBoldCursor editTextBoldCursor;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (editTextBoldCursorArr == null || (editTextBoldCursor = editTextBoldCursorArr[5]) == null) {
            return;
        }
        int selectionStart = editTextBoldCursor.getSelectionStart();
        this.inputFields[5].setTransformationMethod(z ? null : PasswordTransformationMethod.getInstance());
        EditTextBoldCursor editTextBoldCursor2 = this.inputFields[5];
        editTextBoldCursor2.setSelection(Math.max(0, Math.min(selectionStart, editTextBoldCursor2.length())));
    }

    public void updateFieldContainerState(int i, boolean z, boolean z2) {
        EditTextBoldCursor[] editTextBoldCursorArr;
        EditTextBoldCursor editTextBoldCursor;
        OutlineTextContainerView[] outlineTextContainerViewArr = this.inputFieldContainers;
        if (outlineTextContainerViewArr == null || (editTextBoldCursorArr = this.inputFields) == null || i < 0 || i >= outlineTextContainerViewArr.length || outlineTextContainerViewArr[i] == null || (editTextBoldCursor = editTextBoldCursorArr[i]) == null) {
            return;
        }
        this.inputFieldContainers[i].animateSelection(z ? 1.0f : 0.0f, (z || (editTextBoldCursor.getText() != null && this.inputFields[i].length() > 0)) ? 1.0f : 0.0f, z2);
    }

    public class SimpleFieldTextWatcher implements TextWatcher {
        private final int field;

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        private SimpleFieldTextWatcher(int i) {
            this.field = i;
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            ProxySettingsActivity proxySettingsActivity = ProxySettingsActivity.this;
            proxySettingsActivity.updateFieldContainerState(this.field, proxySettingsActivity.inputFields[this.field].hasFocus(), true);
        }
    }

    public void updatePasteCell() {
        Uri proxyUriFromText;
        ClipData primaryClip = this.clipboardManager.getPrimaryClip();
        String string = null;
        if (primaryClip != null && primaryClip.getItemCount() > 0) {
            try {
                string = primaryClip.getItemAt(0).coerceToText(this.fragmentView.getContext()).toString();
            } catch (Exception unused) {
            }
        }
        if (TextUtils.equals(string, this.pasteString)) {
            return;
        }
        this.pasteType = -1;
        this.pasteString = string;
        this.pasteFields = new String[this.inputFields.length];
        if (string != null && (proxyUriFromText = getProxyUriFromText(string)) != null) {
            String path = proxyUriFromText.getPath();
            String host = proxyUriFromText.getHost();
            if (TextUtils.equals(host, "socks") || TextUtils.equals(path, "/socks")) {
                this.pasteType = 0;
            } else if (TextUtils.equals(host, "proxy") || TextUtils.equals(path, "/proxy")) {
                this.pasteType = 1;
            }
            if (this.pasteType != -1) {
                this.pasteFields[0] = proxyUriFromText.getQueryParameter("title");
                this.pasteFields[1] = proxyUriFromText.getQueryParameter("server");
                this.pasteFields[2] = proxyUriFromText.getQueryParameter("port");
                int i = this.pasteType;
                String[] strArr = this.pasteFields;
                if (i == 0) {
                    strArr[3] = proxyUriFromText.getQueryParameter("user");
                    this.pasteFields[4] = proxyUriFromText.getQueryParameter("pass");
                } else {
                    strArr[5] = proxyUriFromText.getQueryParameter("secret");
                }
            }
        }
        int i2 = this.pasteType;
        TextSettingsCell textSettingsCell = this.pasteCell;
        if (i2 != -1) {
            if (textSettingsCell.getVisibility() != 0) {
                this.pasteContainer.setVisibility(0);
                this.pasteCell.setVisibility(0);
                return;
            }
            return;
        }
        if (textSettingsCell.getVisibility() != 8) {
            this.pasteCell.setVisibility(8);
            this.pasteContainer.setVisibility(8);
        }
    }

    private Uri getProxyUriFromText(String str) {
        String[] strArr = {"https://t.me/socks?", "http://t.me/socks?", "t.me/socks?", "tg://socks?", "https://t.me/proxy?", "http://t.me/proxy?", "t.me/proxy?", "tg://proxy?"};
        for (int i = 0; i < 8; i++) {
            int iIndexOf = str.indexOf(strArr[i]);
            if (iIndexOf >= 0) {
                String strTrim = str.substring(iIndexOf).trim();
                int iIndexOf2 = strTrim.indexOf(32);
                if (iIndexOf2 >= 0) {
                    strTrim = strTrim.substring(0, iIndexOf2);
                }
                if (strTrim.startsWith("t.me/")) {
                    strTrim = "https://".concat(strTrim);
                }
                return Uri.parse(strTrim);
            }
        }
        return null;
    }

    private void setShareDoneEnabled(boolean z, boolean z2) {
        if (this.shareDoneEnabled != z) {
            ValueAnimator valueAnimator = this.shareDoneAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            } else if (z2) {
                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.shareDoneAnimator = valueAnimatorOfFloat;
                valueAnimatorOfFloat.setDuration(200L);
                this.shareDoneAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda8
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        this.f$0.lambda$setShareDoneEnabled$6(valueAnimator2);
                    }
                });
            }
            if (z2) {
                float[] fArr = this.shareDoneProgressAnimValues;
                fArr[0] = this.shareDoneProgress;
                fArr[1] = z ? 1.0f : 0.0f;
                this.shareDoneAnimator.start();
            } else {
                this.shareDoneProgress = z ? 1.0f : 0.0f;
                this.shareCell.setTextColor(Theme.getColor(z ? Theme.key_windowBackgroundWhiteBlueText4 : Theme.key_windowBackgroundWhiteGrayText2));
                this.doneItem.setAlpha(z ? 1.0f : 0.5f);
            }
            this.shareCell.setEnabled(z);
            this.doneItem.setEnabled(z);
            this.shareDoneEnabled = z;
        }
    }

    public /* synthetic */ void lambda$setShareDoneEnabled$6(ValueAnimator valueAnimator) {
        this.shareDoneProgress = AndroidUtilities.lerp(this.shareDoneProgressAnimValues, valueAnimator.getAnimatedFraction());
        this.shareCell.setTextColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4), this.shareDoneProgress));
        this.doneItem.setAlpha((this.shareDoneProgress / 2.0f) + 0.5f);
    }

    public void checkShareDone(boolean z) {
        if (this.shareCell == null || this.doneItem == null) {
            return;
        }
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        EditTextBoldCursor editTextBoldCursor = editTextBoldCursorArr[1];
        if (editTextBoldCursor == null || editTextBoldCursorArr[2] == null) {
            return;
        }
        setShareDoneEnabled((editTextBoldCursor.length() == 0 || Utilities.parseInt((CharSequence) this.inputFields[2].getText().toString()).intValue() == 0) ? false : true, z);
    }

    public void updateActionBarTitle() {
        String name;
        EditTextBoldCursor editTextBoldCursor;
        if (this.actionBar == null) {
            return;
        }
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (editTextBoldCursorArr != null && editTextBoldCursorArr.length > 0 && (editTextBoldCursor = editTextBoldCursorArr[0]) != null) {
            name = editTextBoldCursor.getText().toString().trim();
        } else {
            name = this.currentProxyInfo != null ? ProxyController.getInstance().getName(this.currentProxyInfo) : null;
        }
        ActionBar actionBar = this.actionBar;
        if (TextUtils.isEmpty(name)) {
            name = LocaleController.getString(R.string.ProxyDetails);
        }
        actionBar.setTitle(name);
    }

    private void setProxyType(int i, boolean z) {
        setProxyType(i, z, null);
    }

    private void setProxyType(int i, boolean z, final Runnable runnable) {
        if (this.currentType != i) {
            this.currentType = i;
            TransitionManager.endTransitions(this.linearLayout2);
            if (z) {
                TransitionSet duration = new TransitionSet().addTransition(new Fade(2)).addTransition(new ChangeBounds()).addTransition(new Fade(1)).setInterpolator((TimeInterpolator) CubicBezierInterpolator.DEFAULT).setDuration(250L);
                if (runnable != null) {
                    duration.addListener(new Transition.TransitionListener() { // from class: org.telegram.ui.ProxySettingsActivity.5
                        @Override // android.transition.Transition.TransitionListener
                        public void onTransitionCancel(Transition transition) {
                        }

                        @Override // android.transition.Transition.TransitionListener
                        public void onTransitionPause(Transition transition) {
                        }

                        @Override // android.transition.Transition.TransitionListener
                        public void onTransitionResume(Transition transition) {
                        }

                        @Override // android.transition.Transition.TransitionListener
                        public void onTransitionStart(Transition transition) {
                        }

                        @Override // android.transition.Transition.TransitionListener
                        public void onTransitionEnd(Transition transition) {
                            runnable.run();
                        }
                    });
                }
                TransitionManager.beginDelayedTransition(this.linearLayout2, duration);
            }
            int i2 = this.currentType;
            if (i2 == 0) {
                this.sponsorInfoCell.setVisibility(8);
                ((View) this.inputFields[5].getParent()).setVisibility(8);
                ((View) this.inputFields[4].getParent()).setVisibility(0);
                ((View) this.inputFields[3].getParent()).setVisibility(0);
            } else if (i2 == 1) {
                this.sponsorInfoCell.setVisibility(0);
                ((View) this.inputFields[5].getParent()).setVisibility(0);
                ((View) this.inputFields[4].getParent()).setVisibility(8);
                ((View) this.inputFields[3].getParent()).setVisibility(8);
            }
            this.typeCell[0].setChecked(this.currentType == 0, z);
            this.typeCell[1].setChecked(this.currentType == 1, z);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2 && this.addingNewProxy) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ProxySettingsActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                this.f$0.lambda$getThemeDescriptions$7();
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_listSelector));
        int i3 = Theme.key_windowBackgroundWhiteBlueText4;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, i3));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.pasteCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        int i4 = 0;
        for (int i5 = 0; i5 < this.typeCell.length; i5++) {
            arrayList.add(new ThemeDescription(this.typeCell[i5], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.typeCell[i5], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
            arrayList.add(new ThemeDescription(this.typeCell[i5], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
        }
        if (this.inputFields != null) {
            for (int i6 = 0; i6 < this.inputFields.length; i6++) {
                EditTextBoldCursor editTextBoldCursor = this.inputFields[i6];
                int i7 = ThemeDescription.FLAG_TEXTCOLOR;
                int i8 = Theme.key_windowBackgroundWhiteBlackText;
                arrayList.add(new ThemeDescription(editTextBoldCursor, i7, null, null, null, null, i8));
                arrayList.add(new ThemeDescription(this.inputFields[i6], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
                arrayList.add(new ThemeDescription(this.inputFields[i6], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
                arrayList.add(new ThemeDescription(this.inputFields[i6], ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, i8));
            }
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteHintText));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteInputField));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteInputFieldActivated));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_text_RedBold));
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }
        arrayList.add(new ThemeDescription(this.headerCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        while (true) {
            ShadowSectionCell[] shadowSectionCellArr = this.sectionCell;
            if (i4 < shadowSectionCellArr.length) {
                if (shadowSectionCellArr[i4] != null) {
                    arrayList.add(new ThemeDescription(this.sectionCell[i4], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
                }
                i4++;
            } else {
                arrayList.add(new ThemeDescription(this.sponsorInfoCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
                arrayList.add(new ThemeDescription(this.sponsorInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
                arrayList.add(new ThemeDescription(this.sponsorInfoCell, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
                return arrayList;
            }
        }
    }

    public /* synthetic */ void lambda$getThemeDescriptions$7() {
        ValueAnimator valueAnimator;
        updateRoundedContainerColors();
        if (this.shareCell != null && ((valueAnimator = this.shareDoneAnimator) == null || !valueAnimator.isRunning())) {
            this.shareCell.setTextColor(Theme.getColor(this.shareDoneEnabled ? Theme.key_windowBackgroundWhiteBlueText4 : Theme.key_windowBackgroundWhiteGrayText2));
        }
        OutlineTextContainerView[] outlineTextContainerViewArr = this.inputFieldContainers;
        if (outlineTextContainerViewArr != null) {
            for (OutlineTextContainerView outlineTextContainerView : outlineTextContainerViewArr) {
                if (outlineTextContainerView != null) {
                    outlineTextContainerView.updateColor();
                }
            }
        }
    }
}
