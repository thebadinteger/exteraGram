package com.exteragram.messenger.adblock;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import androidx.annotation.Keep;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.telegram.ui.web.BotWebViewContainer;

public class SelectorsObserver {
    private volatile AdBlockClient.CosmeticHide cosmeticHide;
    private final Set<String> filtered = Collections.synchronizedSet(new HashSet());
    private final Object lock = new Object();
    private final BotWebViewContainer.MyWebView webView;

    public SelectorsObserver(BotWebViewContainer.MyWebView myWebView) {
        this.webView = myWebView;
    }

    public void setCosmeticHide(AdBlockClient.CosmeticHide cosmeticHide) {
        synchronized (this.lock) {
            this.filtered.clear();
            this.cosmeticHide = cosmeticHide;
        }
    }

    @JavascriptInterface
    @Keep
    public void onElementsFound(String str) {
        synchronized (this.lock) {
            try {
                if (this.cosmeticHide == null) {
                    return;
                }
                final String cosmeticHideContinuous = AdBlockClient.getCosmeticHideContinuous(this.cosmeticHide, this.filtered, str);
                if (!TextUtils.isEmpty(cosmeticHideContinuous)) {
                    this.webView.post(new Runnable() { 
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onElementsFound$0(cosmeticHideContinuous);
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public /* synthetic */ void lambda$onElementsFound$0(String str) {
        this.webView.evaluateJS(str);
    }
}
