package com.exteragram.messenger.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import androidx.annotation.Keep;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.adblock.AdBlockClient;
import com.exteragram.messenger.adblock.data.BlockResult;
import com.google.android.gms.cast.MediaError;
import com.google.android.gms.cast.framework.media.NotificationOptions;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ReverseImageSearchSheet extends BottomSheet {
    private final AdblockBridge adblockBridge;
    private final boolean adblockEnabled;
    private volatile String currentUrl;
    private int injectedAtStartCount;
    private int pageStartCount;
    private String pendingScript;
    private final Provider provider;
    private Runnable revealTimeout;
    private boolean revealed;
    private final CircularProgressIndicator spinner;
    private boolean uploadInjected;
    private WebView webView;

    public enum Provider {
        YANDEX("Yandex", "https://yandex.com/images/"),
        GOOGLE("Google", "https://www.google.com/"),
        BING("Bing", "https://www.bing.com/images"),
        TINEYE("TinEye", "https://tineye.com/");

        public final String landingUrl;
        public final String title;

        Provider(String str, String str2) {
            this.title = str;
            this.landingUrl = str2;
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public ReverseImageSearchSheet(Context context, final File file, final Provider provider, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        boolean enableAdBlock = ExteraConfig.getEnableAdBlock();
        this.adblockEnabled = enableAdBlock;
        AdblockBridge adblockBridge = new AdblockBridge();
        this.adblockBridge = adblockBridge;
        this.injectedAtStartCount = -1;
        this.provider = provider;
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        this.useBackgroundTopPadding = false;
        setCanDismissWithSwipe(false);
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
        FrameLayout frameLayout = new FrameLayout(context) { 
            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), TLObject.FLAG_30));
            }
        };
        int i = Theme.key_windowBackgroundWhite;
        frameLayout.setBackgroundColor(getThemedColor(i));
        WebView webView = new WebView(context);
        this.webView = webView;
        webView.setVisibility(4);
        this.webView.setHorizontalScrollBarEnabled(false);
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.getSettings().setDatabaseEnabled(true);
        this.webView.getSettings().setCacheMode(-1);
        this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        this.webView.getSettings().setMixedContentMode(0);
        File file2 = new File(ApplicationLoader.getFilesDirFixed(), "webview_database");
        if ((file2.exists() && file2.isDirectory()) || file2.mkdirs()) {
            this.webView.getSettings().setDatabasePath(file2.getAbsolutePath());
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        if (enableAdBlock) {
            this.webView.addJavascriptInterface(adblockBridge, "Android");
        }
        this.webView.setWebViewClient(new WebViewClient() { 
            @Override // android.webkit.WebViewClient
            public WebResourceResponse shouldInterceptRequest(WebView webView2, WebResourceRequest webResourceRequest) {
                if (ReverseImageSearchSheet.this.adblockEnabled && webResourceRequest != null && !webResourceRequest.isForMainFrame()) {
                    BlockResult blockResultIsAdRequest = AdBlockClient.isAdRequest(webResourceRequest, !TextUtils.isEmpty(ReverseImageSearchSheet.this.currentUrl) ? ReverseImageSearchSheet.this.currentUrl : webResourceRequest.getUrl().toString());
                    if (blockResultIsAdRequest != null && blockResultIsAdRequest.isMatched()) {
                        String redirect = blockResultIsAdRequest.getRedirect();
                        if (TextUtils.isEmpty(redirect)) {
                            return new WebResourceResponse("text/plain", "utf-8", MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, "Blocked", null, null);
                        }
                        if (redirect.startsWith("data:")) {
                            try {
                                String strSubstring = redirect.substring(redirect.indexOf(":") + 1, redirect.indexOf(";"));
                                String strSubstring2 = redirect.substring(redirect.indexOf(",") + 1);
                                HashMap map = new HashMap();
                                map.put("Content-Type", strSubstring);
                                map.put("Access-Control-Allow-Origin", "*");
                                return new WebResourceResponse(strSubstring, null, 200, "OK", map, new ByteArrayInputStream(Base64.decode(strSubstring2, 0)));
                            } catch (Exception unused) {
                                return new WebResourceResponse("text/plain", "utf-8", MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, "Blocked", null, null);
                            }
                        }
                    }
                }
                return super.shouldInterceptRequest(webView2, webResourceRequest);
            }

            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView2, WebResourceRequest webResourceRequest) {
                if (webResourceRequest != null && webResourceRequest.isForMainFrame()) {
                    Uri url = webResourceRequest.getUrl();
                    String host = url != null ? url.getHost() : null;
                    if (host != null && !ReverseImageSearchSheet.isProviderHost(provider, host)) {
                        Browser.openUrlInSystemBrowser(ReverseImageSearchSheet.this.getContext(), url.toString());
                        return true;
                    }
                }
                return false;
            }

            @Override // android.webkit.WebViewClient
            public void onPageStarted(WebView webView2, String str, Bitmap bitmap) {
                super.onPageStarted(webView2, str, bitmap);
                ReverseImageSearchSheet.this.pageStartCount++;
                ReverseImageSearchSheet.this.onUrlChanged(str);
            }

            @Override // android.webkit.WebViewClient
            public void onPageFinished(WebView webView2, String str) {
                super.onPageFinished(webView2, str);
                ReverseImageSearchSheet.this.onUrlChanged(str);
                if (!ReverseImageSearchSheet.this.uploadInjected) {
                    if (str != null && (str.startsWith("http://") || str.startsWith("https://"))) {
                        ReverseImageSearchSheet.this.uploadInjected = true;
                        ReverseImageSearchSheet reverseImageSearchSheet = ReverseImageSearchSheet.this;
                        reverseImageSearchSheet.injectedAtStartCount = reverseImageSearchSheet.pageStartCount;
                        if (ReverseImageSearchSheet.this.pendingScript != null) {
                            webView2.evaluateJavascript(ReverseImageSearchSheet.this.pendingScript, null);
                            ReverseImageSearchSheet.this.pendingScript = null;
                        }
                    }
                } else if (ReverseImageSearchSheet.this.pageStartCount > ReverseImageSearchSheet.this.injectedAtStartCount) {
                    ReverseImageSearchSheet.this.reveal();
                }
                if (ReverseImageSearchSheet.this.adblockEnabled) {
                    ReverseImageSearchSheet.this.applyCosmetic(str);
                }
                ReverseImageSearchSheet.this.hideProviderAds(webView2);
            }

            @Override // android.webkit.WebViewClient
            public void doUpdateVisitedHistory(WebView webView2, String str, boolean z) {
                String path;
                super.doUpdateVisitedHistory(webView2, str, z);
                ReverseImageSearchSheet.this.hideProviderAds(webView2);
                ReverseImageSearchSheet.this.onUrlChanged(str);
                if (provider == Provider.TINEYE && ReverseImageSearchSheet.this.uploadInjected && str != null) {
                    try {
                        path = Uri.parse(str).getPath();
                    } catch (Exception unused) {
                        path = null;
                    }
                    if (path == null || !path.startsWith("/search")) {
                        return;
                    }
                    ReverseImageSearchSheet.this.reveal();
                }
            }
        });
        float f = currentActionBarHeight;
        frameLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, f / AndroidUtilities.density, 0.0f, 0.0f));
        View view = new View(context);
        view.setBackgroundColor(getThemedColor(Theme.key_divider));
        float f2 = AndroidUtilities.density;
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 1.0f / f2, 51, 0.0f, f / f2, 0.0f, 0.0f));
        ActionBar actionBar = new ActionBar(context, resourcesProvider);
        actionBar.setOccupyStatusBar(true);
        actionBar.setBackgroundColor(getThemedColor(i));
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        actionBar.setTitleColor(getThemedColor(i2));
        actionBar.setItemsColor(getThemedColor(i2), false);
        actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_actionBarWhiteSelector), false);
        actionBar.setBackButtonImage(R.drawable.ic_close_white);
        actionBar.setTitle(provider.title);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { 
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    ReverseImageSearchSheet.this.dismiss();
                } else {
                    if (i3 != 1 || TextUtils.isEmpty(ReverseImageSearchSheet.this.currentUrl)) {
                        return;
                    }
                    Browser.openUrlInSystemBrowser(ReverseImageSearchSheet.this.getContext(), ReverseImageSearchSheet.this.currentUrl);
                }
            }
        });
        if (provider == Provider.YANDEX) {
            actionBar.createMenu().addItem(1, R.drawable.msg_openin);
        }
        frameLayout.addView(actionBar, LayoutHelper.createFrame(-1, f / AndroidUtilities.density));
        CircularProgressIndicator circularProgressIndicator = new CircularProgressIndicator(context);
        this.spinner = circularProgressIndicator;
        circularProgressIndicator.setIndeterminate(true);
        circularProgressIndicator.setIndicatorColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueText));
        circularProgressIndicator.setTrackColor(getThemedColor(Theme.key_windowBackgroundWhiteInputField));
        circularProgressIndicator.setIndicatorSize(AndroidUtilities.dp(48.0f));
        circularProgressIndicator.setTrackThickness(AndroidUtilities.dp(4.0f));
        circularProgressIndicator.setTrackCornerRadius(AndroidUtilities.dp(2.0f));
        circularProgressIndicator.setIndicatorTrackGapSize(AndroidUtilities.dp(3.0f));
        frameLayout.addView(circularProgressIndicator, LayoutHelper.createFrame(-2, -2, 17));
        setCustomView(frameLayout);
        Utilities.globalQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$1(file, provider);
            }
        });
    }

    public void lambda$new$0(String str, Provider provider) {
        if (this.webView == null) {
            return;
        }
        if (str == null) {
            dismiss();
            return;
        }
        seedConsentCookies(provider);
        this.pendingScript = buildUploadScript(provider, str);
        this.webView.loadUrl(provider.landingUrl);
        Runnable runnable = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.reveal();
            }
        };
        this.revealTimeout = runnable;
        AndroidUtilities.runOnUIThread(runnable, NotificationOptions.SKIP_STEP_THIRTY_SECONDS_IN_MS);
    }

    public void reveal() {
        if (this.revealed || this.webView == null) {
            return;
        }
        this.revealed = true;
        Runnable runnable = this.revealTimeout;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.revealTimeout = null;
        }
        this.webView.setAlpha(0.0f);
        this.webView.setVisibility(0);
        this.webView.animate().alpha(1.0f).setDuration(150L).start();
        CircularProgressIndicator circularProgressIndicator = this.spinner;
        if (circularProgressIndicator != null) {
            circularProgressIndicator.animate().alpha(0.0f).setDuration(150L).withEndAction(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$reveal$2();
                }
            }).start();
        }
    }

    public void lambda$onElementsFound$0(String str) {
            if (ReverseImageSearchSheet.this.webView != null) {
                ReverseImageSearchSheet.this.webView.evaluateJavascript(str, null);
            }
        }
    }

    public static boolean isProviderHost(Provider provider, String str) {
        String lowerCase = str.toLowerCase();
        int i = AnonymousClass4.$SwitchMap$com$exteragram$messenger$components$ReverseImageSearchSheet$Provider[provider.ordinal()];
        if (i == 1) {
            return lowerCase.contains("google.") || lowerCase.endsWith("gstatic.com") || lowerCase.endsWith("googleusercontent.com");
        }
        if (i == 2) {
            return lowerCase.endsWith("bing.com") || lowerCase.endsWith("bingapis.com") || lowerCase.endsWith("live.com") || lowerCase.endsWith("microsoft.com");
        }
        if (i != 4) {
            return lowerCase.endsWith("tineye.com");
        }
        return lowerCase.contains("yandex.") || lowerCase.endsWith("ya.ru") || lowerCase.contains("yastatic.");
    }

    private static String buildUploadScript(Provider provider, String str) {
        int i = AnonymousClass4.$SwitchMap$com$exteragram$messenger$components$ReverseImageSearchSheet$Provider[provider.ordinal()];
        if (i == 1) {
            return "(function(){try{" + bytesFromBase64(str) + "var file=new File([a],'image.jpg',{type:'image/jpeg'});var f=document.createElement('form');f.method='POST';f.enctype='multipart/form-data';f.action='https://lens.google.com/v3/upload';var i=document.createElement('input');i.type='file';i.name='encoded_image';f.appendChild(i);document.body.appendChild(f);var dt=new DataTransfer();dt.items.add(file);i.files=dt.files;f.submit();}catch(e){}})();";
        }
        if (i == 2) {
            return "(function(){try{var f=document.createElement('form');f.method='POST';f.enctype='multipart/form-data';f.action='https://www.bing.com/images/search?view=detailv2&iss=sbiupload&FORM=SBIHMP&sbifnm=image.jpg';var i=document.createElement('input');i.type='hidden';i.name='imageBin';i.value='" + str + "';f.appendChild(i);document.body.appendChild(f);f.submit();}catch(e){}})();";
        }
        if (i == 4) {
            return "(function(){try{" + bytesFromBase64(str) + "var o=location.protocol+'//'+location.host;var blob=new Blob([a],{type:'image/jpeg'});var d=new FormData();d.append('upfile',blob,'image.jpg');var u=o+'/images/touch/search?rpt=imageview&format=json&request='+encodeURIComponent('{\"blocks\":[{\"block\":\"cbir-uploader__get-cbir-id\"}]}');fetch(u,{method:'POST',credentials:'include',headers:{'X-Requested-With':'XMLHttpRequest','Accept':'application/json, text/javascript, */*; q=0.01'},body:d}).then(function(r){return r.json();}).then(function(j){var p=j.blocks[0].params;if(p&&p.cbirId){location.href=o+'/images/search?cbir_id='+encodeURIComponent(p.cbirId)+'&rpt=imageview&tabInt=1&url='+encodeURIComponent(p.originalImageUrl||'');}}).catch(function(e){});}catch(e){}})();";
        }
        return "(function(){try{" + bytesFromBase64(str) + "var file=new File([a],'image.jpg',{type:'image/jpeg'});var n=0;var t=setInterval(function(){var i=document.querySelector('input#upload-box');if(i){clearInterval(t);try{var dt=new DataTransfer();dt.items.add(file);i.files=dt.files;i.dispatchEvent(new Event('change',{bubbles:true}));}catch(e){}}else if(++n>24){clearInterval(t);}},250);}catch(e){}})();";
    }

    private static String bytesFromBase64(String str) {
        return "var b='" + str + "';var bin=atob(b);var a=new Uint8Array(bin.length);for(var k=0;k<bin.length;k++)a[k]=bin.charCodeAt(k);";
    }

    private static String encodeImage(File file) {
        if (file != null && file.exists()) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                int i = 1;
                while (Math.max(options.outWidth, options.outHeight) / i > 2560) {
                    i *= 2;
                }
                options2.inSampleSize = i;
                Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(file.getAbsolutePath(), options2);
                if (bitmapDecodeFile == null) {
                    return null;
                }
                int width = bitmapDecodeFile.getWidth();
                int height = bitmapDecodeFile.getHeight();
                int iMax = Math.max(width, height);
                if (iMax > 1280) {
                    float f = 1280.0f / iMax;
                    Bitmap bitmapCreateScaledBitmap = Bitmap.createScaledBitmap(bitmapDecodeFile, Math.round(width * f), Math.round(height * f), true);
                    if (bitmapCreateScaledBitmap != bitmapDecodeFile) {
                        bitmapDecodeFile.recycle();
                        bitmapDecodeFile = bitmapCreateScaledBitmap;
                    }
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmapDecodeFile.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                bitmapDecodeFile.recycle();
                return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        return null;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void lambda$openCrafting$8() {
        WebView webView = this.webView;
        if (webView != null && webView.canGoBack()) {
            this.webView.goBack();
        } else {
            super.lambda$openCrafting$8();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    public void dismiss() {
        Runnable runnable = this.revealTimeout;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.revealTimeout = null;
        }
        WebView webView = this.webView;
        if (webView != null) {
            try {
                webView.stopLoading();
                this.webView.loadUrl("about:blank");
                this.webView.destroy();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.webView = null;
        }
        super.dismiss();
    }
}
