package com.exteragram.messenger.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.RectF;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.utils.system.SystemUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LinkifyPort;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.StickerImageView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class QRCodeSheet extends BottomSheet {
    private final int TEXT_TYPE_AUTH_TOKEN;
    private final int TEXT_TYPE_LINK;
    private final int TEXT_TYPE_PHONE;
    private final int TEXT_TYPE_TEXT;
    private final int TEXT_TYPE_WIFI;
    private final BaseFragment fragment;
    private String password;
    private String ssid;
    private String wifiAuthType;

    public QRCodeSheet(final BaseFragment baseFragment, String str) {
        super(baseFragment.getParentActivity(), false, baseFragment.getResourceProvider());
        final int i;
        CharSequence textWithIcon;
        CharSequence charSequence;
        String str2;
        String strEnsureUrlHasHttps = str;
        this.TEXT_TYPE_LINK = 0;
        this.TEXT_TYPE_TEXT = 1;
        this.TEXT_TYPE_AUTH_TOKEN = 2;
        this.TEXT_TYPE_PHONE = 3;
        this.TEXT_TYPE_WIFI = 4;
        this.wifiAuthType = "WPA";
        this.fragment = baseFragment;
        if (strEnsureUrlHasHttps.startsWith("tg://login?token=")) {
            String string = LocaleController.getString(R.string.Cancel);
            textWithIcon = LocaleController.getString(R.string.Allow);
            charSequence = string;
            i = 2;
        } else if (LinkifyPort.WEB_URL.matcher(strEnsureUrlHasHttps).matches() || strEnsureUrlHasHttps.startsWith("tel:")) {
            i = strEnsureUrlHasHttps.startsWith("tel:") ? 3 : 0;
            SpannableStringBuilder spannableStringBuilderAppend = new SpannableStringBuilder(LocaleController.getString(R.string.Open)).append((CharSequence) ".");
            int length = spannableStringBuilderAppend.length();
            spannableStringBuilderAppend.setSpan(new ColoredImageSpan(ContextCompat.getDrawable(baseFragment.getParentActivity(), R.drawable.msg_mini_topicarrow)), length - 1, length, 0);
            textWithIcon = getTextWithIcon("share");
            charSequence = spannableStringBuilderAppend;
        } else if (strEnsureUrlHasHttps.startsWith("WIFI:")) {
            parseWifiInfo(strEnsureUrlHasHttps);
            String string2 = LocaleController.getString(R.string.WifiConnect);
            textWithIcon = getTextWithIcon("share");
            charSequence = string2;
            i = 4;
        } else {
            Spanned textWithIcon2 = getTextWithIcon("copy");
            textWithIcon = getTextWithIcon("share");
            charSequence = textWithIcon2;
            i = 1;
        }
        final Activity parentActivity = baseFragment.getParentActivity();
        fixNavigationBar();
        FrameLayout frameLayout = new FrameLayout(parentActivity);
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        frameLayout.addView(linearLayout);
        linearLayout.addView(new View(baseFragment.getParentActivity()) { // from class: com.exteragram.messenger.components.QRCodeSheet.1
            @Override // android.view.View
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                int measuredWidth = getMeasuredWidth();
                RectF rectF = new RectF();
                rectF.set((getMeasuredWidth() - measuredWidth) / 2.0f, 0.0f, (getMeasuredWidth() + measuredWidth) / 2.0f, AndroidUtilities.dp(4.0f));
                Theme.dialogs_onlineCirclePaint.setColor(QRCodeSheet.this.getThemedColor(Theme.key_sheet_scrollUp));
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
            }
        }, LayoutHelper.createLinear(36, 4, 1, 18, 2, 18, 0));
        if (i == 2) {
            StickerImageView stickerImageView = new StickerImageView(parentActivity, this.currentAccount);
            stickerImageView.setStickerPackName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME);
            stickerImageView.setStickerNum(6);
            stickerImageView.getImageReceiver().setAutoRepeat(1);
            stickerImageView.getImageReceiver().setAutoRepeatCount(1);
            linearLayout.addView(stickerImageView, LayoutHelper.createLinear(144, 144, 1, 0, 20, 0, 10));
        } else {
            ImageView imageView = new ImageView(parentActivity);
            ScaleStateListAnimator.apply(imageView, 0.03f, 1.2f);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setOutlineProvider(new ViewOutlineProvider() { // from class: com.exteragram.messenger.components.QRCodeSheet.2
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), AndroidUtilities.dp(12.0f));
                }
            });
            imageView.setClipToOutline(true);
            final Bitmap bitmapCreateQR = createQR(strEnsureUrlHasHttps);
            imageView.setImageBitmap(bitmapCreateQR);
            imageView.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    QRCodeSheet.this.lambda$new$0(bitmapCreateQR, parentActivity, view);
                }
            });
            linearLayout.addView(imageView, LayoutHelper.createLinear(200, 200, 1, 18, 20, 18, 10));
        }
        TextView textView = new TextView(parentActivity);
        ScaleStateListAnimator.apply(textView, 0.02f, 1.5f);
        textView.setGravity(1);
        textView.setTextSize(1, 14.0f);
        textView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f));
        int i2 = Theme.key_windowBackgroundWhiteGrayText;
        textView.setTextColor(getThemedColor(i2));
        if (i == 4 && !TextUtils.isEmpty(this.ssid)) {
            String str3 = this.ssid;
            if (TextUtils.isEmpty(this.password)) {
                str2 = _UrlKt.FRAGMENT_ENCODE_SET;
            } else {
                str2 = ", Password: " + this.password;
            }
            textView.setText(MessageFormat.format("SSID: {0}{1}", str3, str2));
        } else {
            textView.setText(i == 2 ? LocaleController.getString(R.string.AreYouSureToLogin) : strEnsureUrlHasHttps);
        }
        final String finalUrl = i == 0 ? LocaleUtils.ensureUrlHasHttps(strEnsureUrlHasHttps) : strEnsureUrlHasHttps;
        if (i != 2) {
            textView.setBackground(Theme.createSelectorDrawable(Theme.multAlpha(getThemedColor(i2), Theme.isCurrentThemeDark() ? 0.2f : 0.15f), 7, AndroidUtilities.dp(8.0f)));
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    QRCodeSheet.this.lambda$new$1(finalUrl, view);
                }
            });
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 21, 2, 21, 8));
        LinearLayout linearLayout2 = new LinearLayout(parentActivity);
        linearLayout2.setOrientation(0);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, 48, 16.0f, 15.0f, 16.0f, 4.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(parentActivity, baseFragment.getResourceProvider());
        buttonWithCounterView.setRound();
        buttonWithCounterView.setText(charSequence, false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QRCodeSheet.this.lambda$new$2(i, baseFragment, finalUrl, view);
            }
        });
        if (i == 2) {
            buttonWithCounterView.setNeutral();
        }
        linearLayout2.addView(buttonWithCounterView, LayoutHelper.createLinear(0, -1, 1.0f));
        linearLayout2.addView(new View(parentActivity), LayoutHelper.createLinear(0, -1, 0.06f));
        ButtonWithCounterView buttonWithCounterView2 = new ButtonWithCounterView(parentActivity, baseFragment.getResourceProvider());
        buttonWithCounterView2.setRound();
        buttonWithCounterView2.setText(textWithIcon, false);
        buttonWithCounterView2.setFilled(true);
        buttonWithCounterView2.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QRCodeSheet.this.lambda$new$6(i, finalUrl, baseFragment, view);
            }
        });
        linearLayout2.addView(buttonWithCounterView2, LayoutHelper.createLinear(0, -1, 1.0f));
        ScrollView scrollView = new ScrollView(parentActivity);
        scrollView.addView(frameLayout);
        setCustomView(scrollView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Bitmap bitmap, Activity activity, View view) {
        if (bitmap != null) {
            copyQR(bitmap, activity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(String str, View view) {
        if (AndroidUtilities.addToClipboard(str)) {
            showCopyBulletin(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:18:0x0029  */
    public /* synthetic */ void lambda$new$2(int i, BaseFragment baseFragment, String str, View view) {
        if (i == 2) {
            dismiss();
            return;
        }
        if (i == 0) {
            Browser.openUrl(baseFragment.getParentActivity(), Uri.parse(str));
        } else if (i != 1) {
            if (i == 3) {
                Browser.openUrl(baseFragment.getParentActivity(), Uri.parse(str));
            } else if (i == 4) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        QRCodeSheet.this.connectToWifi();
                    }
                }, 750L);
            }
        } else if (AndroidUtilities.addToClipboard(str)) {
            showCopyBulletin(false);
        }
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(int i, final String str, final BaseFragment baseFragment, View view) {
        if (i == 2) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    QRCodeSheet.this.lambda$new$5(str, baseFragment);
                }
            }, 750L);
        } else {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", str);
                baseFragment.startActivityForResult(Intent.createChooser(intent, LocaleController.getString(R.string.QrCode)), 500);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(String str, final BaseFragment baseFragment) {
        try {
            byte[] bArrDecode = Base64.decode(str.substring(17).replaceAll("/", "_").replaceAll("\\+", "-"), 8);
            TLRPC.TL_auth_acceptLoginToken tL_auth_acceptLoginToken = new TLRPC.TL_auth_acceptLoginToken();
            tL_auth_acceptLoginToken.token = bArrDecode;
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_auth_acceptLoginToken, new RequestDelegate() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda10
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    QRCodeSheet.this.lambda$new$3(tLObject, tL_error);
                }
            });
        } catch (Exception e) {
            FileLog.e("Failed to pass qr code auth", e);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    AlertsCreator.showSimpleAlert(baseFragment, LocaleController.getString(R.string.AuthAnotherClient), LocaleController.getString(R.string.ErrorOccurred));
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(TLObject tLObject, TLRPC.TL_error tL_error) {
        dismiss();
    }

    private Spanned getTextWithIcon(String str) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "..").setSpan(new ColoredImageSpan(ContextCompat.getDrawable(this.fragment.getParentActivity(), str.equals("copy") ? R.drawable.msg_copy_filled : R.drawable.msg_share_filled)), 0, 1, 0);
        spannableStringBuilder.setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(4.0f)), 1, 2, 0);
        spannableStringBuilder.append((CharSequence) LocaleController.getString(str.equals("copy") ? R.string.LinkActionCopy : R.string.LinkActionShare));
        return spannableStringBuilder;
    }

    private Bitmap createQR(String str) {
        try {
            HashMap map = new HashMap();
            map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            map.put(EncodeHintType.MARGIN, 0);
            return new QRCodeWriter().encode(str, 768, 768, map, null, 1.0f, -1, -16777216);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    @SuppressLint({"SetWorldReadable"})
    private void copyQR(Bitmap bitmap, Activity activity) {
        try {
            File file = new File(activity.getExternalFilesDir(null), "qr_code.jpg");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            file.setReadable(true, false);
            SystemUtils.addFileToClipboard(file, new Runnable() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    QRCodeSheet.this.lambda$copyQR$7();
                }
            });
        } catch (IOException e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$copyQR$7() {
        BulletinFactory.of(getContainer(), null).createCopyBulletin(LocaleController.getString(R.string.PhotoCopied), this.resourcesProvider).show();
    }

    private void parseWifiInfo(String str) {
        for (String str2 : str.substring(str.indexOf(":") + 1).split("(?<!\\\\);")) {
            if (str2.startsWith("S:")) {
                this.ssid = unescapeWifiString(str2.substring(2));
            } else if (str2.startsWith("P:")) {
                this.password = unescapeWifiString(str2.substring(2));
            } else if (str2.startsWith("T:")) {
                this.wifiAuthType = str2.substring(2);
            }
        }
    }

    private String unescapeWifiString(String str) {
        return str.replace("\\\\", "\\").replace("\\;", ";").replace("\\:", ":").replace("\\,", ",").replace("\\\"", "\"");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectToWifi() {
        if (TextUtils.isEmpty(this.ssid)) {
            showErrorBulletin(LocaleController.getString(R.string.WifiFailed));
            return;
        }
        WifiManager wifiManager = (WifiManager) ApplicationLoader.applicationContext.getSystemService("wifi");
        if (wifiManager == null) {
            showErrorBulletin(LocaleController.getString(R.string.WifiFailed));
            return;
        }
        if (!wifiManager.isWifiEnabled()) {
            if (Build.VERSION.SDK_INT >= 29) {
                showErrorBulletin(LocaleController.getString(R.string.WifiDisabled));
                this.fragment.startActivityForResult(new Intent("android.settings.panel.action.WIFI"), 501);
                return;
            }
            showErrorBulletin(LocaleController.getString(R.string.WifiDisabled));
            return;
        }
        if (Build.VERSION.SDK_INT >= 29) {
            connectWifiModern(wifiManager);
        } else {
            connectWifiLegacy(wifiManager);
        }
    }

    private void connectWifiModern(WifiManager wifiManager) {
        WifiNetworkSuggestion.Builder isAppInteractionRequired = new WifiNetworkSuggestion.Builder().setSsid(this.ssid).setIsAppInteractionRequired(true);
        if (!TextUtils.isEmpty(this.password)) {
            if ("WPA".equalsIgnoreCase(this.wifiAuthType)) {
                isAppInteractionRequired.setWpa2Passphrase(this.password);
            } else if (Build.VERSION.SDK_INT >= 30 && "SAE".equalsIgnoreCase(this.wifiAuthType)) {
                isAppInteractionRequired.setWpa3Passphrase(this.password);
            }
        }
        if (wifiManager.addNetworkSuggestions(Collections.singletonList(isAppInteractionRequired.build())) == 0) {
            this.fragment.getParentActivity().startActivity(new Intent("android.settings.WIFI_SETTINGS"));
        } else {
            showErrorBulletin(LocaleController.getString(R.string.WifiFailed));
        }
    }

    private void connectWifiLegacy(WifiManager wifiManager) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", this.ssid);
        boolean z = TextUtils.isEmpty(this.wifiAuthType) || "nopass".equalsIgnoreCase(this.wifiAuthType);
        if (!TextUtils.isEmpty(this.password) && !z) {
            if ("WPA".equalsIgnoreCase(this.wifiAuthType)) {
                wifiConfiguration.preSharedKey = String.format("\"%s\"", this.password);
            } else if ("WEP".equalsIgnoreCase(this.wifiAuthType)) {
                wifiConfiguration.wepKeys[0] = String.format("\"%s\"", this.password);
                wifiConfiguration.wepTxKeyIndex = 0;
                wifiConfiguration.allowedKeyManagement.set(0);
                wifiConfiguration.allowedGroupCiphers.set(0);
            }
        } else {
            wifiConfiguration.allowedKeyManagement.set(0);
        }
        int iAddNetwork = wifiManager.addNetwork(wifiConfiguration);
        if (iAddNetwork != -1) {
            if (wifiManager.enableNetwork(iAddNetwork, true)) {
                wifiManager.reconnect();
                BulletinFactory.of(this.fragment).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.WifiSuccess)).show();
                return;
            } else {
                showErrorBulletin(LocaleController.getString(R.string.WifiFailed));
                return;
            }
        }
        showErrorBulletin(LocaleController.getString(R.string.WifiFailed));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showErrorBulletin$8(String str) {
        BulletinFactory.of(this.fragment).createErrorBulletin(str).show();
    }

    private void showErrorBulletin(final String str) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                QRCodeSheet.this.lambda$showErrorBulletin$8(str);
            }
        });
    }

    private void showCopyBulletin(final boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.components.QRCodeSheet$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                QRCodeSheet.this.lambda$showCopyBulletin$9(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showCopyBulletin$9(boolean z) {
        (z ? BulletinFactory.of(getContainer(), null) : BulletinFactory.of(this.fragment)).createCopyBulletin(LocaleController.formatString("TextCopied", R.string.TextCopied, new Object[0])).show();
    }
}
