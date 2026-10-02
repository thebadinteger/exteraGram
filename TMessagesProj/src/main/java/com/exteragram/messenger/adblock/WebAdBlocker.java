package com.exteragram.messenger.adblock;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import androidx.annotation.Keep;
import com.exteragram.messenger.adblock.backend.AdBlockManager;
import com.exteragram.messenger.adblock.data.BlockResult;
import com.exteragram.messenger.adblock.interop.AdBlock;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;
import org.telegram.messenger.Utilities;

/* JADX INFO: loaded from: classes4.dex */
public class WebAdBlocker {
    private volatile boolean allowBlockedPage;
    private final AtomicInteger blockedCount = new AtomicInteger();
    private final String bridgeName;
    private volatile AdBlockClient.CosmeticHide cosmeticHide;
    private volatile boolean navigationPending;
    private volatile boolean pageBlocked;
    private volatile String pageUrl;
    private volatile int previousBlockedCount;
    private volatile String previousPageUrl;
    private final WebView webView;

    public WebAdBlocker(WebView webView) {
        this.webView = webView;
        String str = "_" + Long.toHexString(Utilities.fastRandom.nextLong());
        this.bridgeName = str;
        webView.addJavascriptInterface(this, str);
        AdBlockManager.initialize();
    }

    public int getBlockedCount() {
        return this.blockedCount.get();
    }

    public void allowBlockedPage() {
        this.allowBlockedPage = true;
    }

    public boolean consumePageBlocked() {
        boolean z = this.pageBlocked;
        this.pageBlocked = false;
        return z;
    }

    public void reload() {
        this.webView.reload();
    }

    public WebResourceResponse interceptRequest(WebResourceRequest webResourceRequest) {
        BlockResult blockResultIsAdRequest;
        String string = webResourceRequest.getUrl().toString();
        if (webResourceRequest.isForMainFrame()) {
            if (!this.navigationPending) {
                this.navigationPending = true;
                this.previousPageUrl = this.pageUrl;
                this.previousBlockedCount = this.blockedCount.getAndSet(0);
            }
            this.pageUrl = string;
            boolean z = this.allowBlockedPage;
            this.allowBlockedPage = false;
            if (z || !AdBlockManager.isActive() || (blockResultIsAdRequest = AdBlockClient.isAdRequest(webResourceRequest, string)) == null || !blockResultIsAdRequest.isMatched()) {
                return null;
            }
            this.pageBlocked = true;
            return new WebResourceResponse("plain/text", "utf-8", 590, "Page blocked", null, null);
        }
        if (!AdBlockManager.isActive()) {
            return null;
        }
        String str = this.pageUrl;
        if (TextUtils.isEmpty(str)) {
            str = string;
        }
        String requestType = AdBlockClient.getRequestType(webResourceRequest, str);
        BlockResult blockResult = AdBlock.getBlockResult(string, str, requestType);
        if (blockResult == null || !blockResult.isMatched()) {
            return null;
        }
        this.blockedCount.incrementAndGet();
        return AdBlockClient.createBlockedResponse(requestType, blockResult);
    }

    public void onPageStarted(String str) {
        this.navigationPending = false;
        this.previousPageUrl = null;
        this.pageUrl = str;
        this.cosmeticHide = null;
    }

    public void onDownloadStart() {
        if (this.navigationPending) {
            this.navigationPending = false;
            this.pageUrl = this.previousPageUrl;
            this.previousPageUrl = null;
            this.blockedCount.addAndGet(this.previousBlockedCount);
        }
    }

    public void injectCosmetics(String str) {
        AdBlockClient.CosmeticHide cosmeticHide;
        if (str != null) {
            if ((str.startsWith("http://") || str.startsWith("https://")) && AdBlockManager.isActive()) {
                AdBlockClient.CosmeticHide cosmeticHide2 = this.cosmeticHide;
                if ((cosmeticHide2 == null || !TextUtils.equals(cosmeticHide2.getUrl(), str)) && (cosmeticHide = AdBlockClient.getCosmeticHide(str)) != null) {
                    this.cosmeticHide = cosmeticHide;
                    String hideCss = cosmeticHide.getHideCss();
                    if (hideCss != null || !cosmeticHide.isGenericHide()) {
                        this.webView.evaluateJavascript("(function() {\n    var name = '$BRIDGE';\n    var bridge = window[name];\n    if (!bridge || window[name + '_c']) return;\n    Object.defineProperty(window, name + '_c', {value: true});\n    function addStyle(css) {\n        if (!css) return;\n        var style = document.createElement('style');\n        style.textContent = css;\n        (document.head || document.documentElement).appendChild(style);\n    }\n    addStyle($CSS);\n    if (!$OBSERVE) return;\n    var seenClasses = new Set(), seenIds = new Set(), classes = [], ids = [], scheduled = false;\n    var delay = window.setTimeout.bind(window);\n    function collect(element) {\n        var list = element.classList;\n        if (list) {\n            for (var i = 0; i < list.length; i++) {\n                if (!seenClasses.has(list[i])) {\n                    seenClasses.add(list[i]);\n                    classes.push(list[i]);\n                }\n            }\n        }\n        var id = element.id;\n        if (typeof id === 'string' && id && !seenIds.has(id)) {\n            seenIds.add(id);\n            ids.push(id);\n        }\n    }\n    function collectTree(node) {\n        if (!node || node.nodeType !== 1) return;\n        collect(node);\n        var elements = node.querySelectorAll('[class],[id]');\n        for (var i = 0; i < elements.length; i++) collect(elements[i]);\n    }\n    function flush() {\n        scheduled = false;\n        if (!classes.length && !ids.length) return;\n        var found = bridge.onElementsFound(classes.join(' '), ids.join(' '));\n        classes = [];\n        ids = [];\n        addStyle(found);\n    }\n    new MutationObserver(function(mutations) {\n        for (var i = 0; i < mutations.length; i++) {\n            var mutation = mutations[i];\n            if (mutation.type === 'attributes') {\n                collect(mutation.target);\n            } else {\n                for (var j = 0; j < mutation.addedNodes.length; j++) collectTree(mutation.addedNodes[j]);\n            }\n        }\n        if (!scheduled && (classes.length || ids.length)) {\n            scheduled = true;\n            delay(flush, 100);\n        }\n    }).observe(document, {childList: true, subtree: true, attributes: true, attributeFilter: ['class', 'id']});\n    collectTree(document.documentElement);\n    flush();\n})();\n".replace("$BRIDGE", this.bridgeName).replace("$OBSERVE", String.valueOf(!cosmeticHide.isGenericHide())).replace("$CSS", hideCss != null ? JSONObject.quote(hideCss) : "null"), null);
                    }
                    String injectedScript = cosmeticHide.getInjectedScript();
                    if (TextUtils.isEmpty(injectedScript)) {
                        return;
                    }
                    String str2 = "'" + this.bridgeName + "_s'";
                    this.webView.evaluateJavascript("if (!window[" + str2 + "]) {\nObject.defineProperty(window, " + str2 + ", {value: true});\n" + injectedScript + "\n}", null);
                }
            }
        }
    }

    @JavascriptInterface
    @Keep
    public String onElementsFound(String str, String str2) {
        AdBlockClient.CosmeticHide cosmeticHide = this.cosmeticHide;
        if (cosmeticHide == null || cosmeticHide.isGenericHide() || !AdBlockManager.isActive()) {
            return null;
        }
        return AdBlockClient.getHiddenSelectorsCss(cosmeticHide, split(str), split(str2));
    }

    private static String[] split(String str) {
        return TextUtils.isEmpty(str) ? new String[0] : str.split(" ");
    }
}
