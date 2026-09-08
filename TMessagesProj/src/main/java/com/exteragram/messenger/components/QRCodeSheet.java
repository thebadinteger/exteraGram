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
import com.google.android.gms.cast.MediaError;
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

    void lambda$new$0(Bitmap bitmap, Activity activity, View view) {
        if (bitmap != null) {
            copyQR(bitmap, activity);
        }
    }

    public void lambda$new$6(int i, final String str, final BaseFragment baseFragment, View view) {
        if (i == 2) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$5(str, baseFragment);
                }
            }, 750L);
        } else {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", str);
                baseFragment.startActivityForResult(Intent.createChooser(intent, LocaleController.getString(R.string.QrCode)), MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        lambda$new$0();
    }

    public void lambda$new$3(TLObject tLObject, TLRPC.TL_error tL_error) {
        lambda$new$0();
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
            return new QRCodeWriter().encode(str, 768, 768, map, null, 1.0f, -1, -16777216, false);
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
            SystemUtils.addFileToClipboard(file, new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$copyQR$7();
                }
            });
        } catch (IOException e) {
            FileLog.e(e);
        }
    }

    public void lambda$showCopyBulletin$9(boolean z) {
        (z ? BulletinFactory.of(getContainer(), null) : BulletinFactory.of(this.fragment)).createCopyBulletin(LocaleController.formatString("TextCopied", R.string.TextCopied, new Object[0])).show();
    }
}
