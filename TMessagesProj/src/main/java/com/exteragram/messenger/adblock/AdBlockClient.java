package com.exteragram.messenger.adblock;

import android.text.TextUtils;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import com.exteragram.messenger.adblock.data.BlockResult;
import com.exteragram.messenger.adblock.data.UrlCosmeticResources;
import com.exteragram.messenger.adblock.interop.AdBlock;
import com.google.android.gms.cast.MediaError;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: classes4.dex */
public abstract class AdBlockClient {
    private static final IOException BLOCKED_EXCEPTION = new IOException("Blocked by filter") { // from class: com.exteragram.messenger.adblock.AdBlockClient.1
        @Override // java.lang.Throwable
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    };

    public static CosmeticHide getCosmeticHide(String str) {
        UrlCosmeticResources cosmeticResources = AdBlock.getCosmeticResources(str);
        if (cosmeticResources == null) {
            return null;
        }
        return new CosmeticHide(str, createHideCss(cosmeticResources.getHideSelectors()), cosmeticResources.getInjectedScript(), cosmeticResources.getExceptions(), cosmeticResources.isGenericHide());
    }

    public static String getHiddenSelectorsCss(CosmeticHide cosmeticHide, String[] strArr, String[] strArr2) {
        if (strArr.length == 0 && strArr2.length == 0) {
            return null;
        }
        return createHideCss(AdBlock.getHiddenSelectors(strArr, strArr2, cosmeticHide.getExceptions()));
    }

    public static BlockResult isAdRequest(WebResourceRequest webResourceRequest, String str) {
        return AdBlock.getBlockResult(webResourceRequest.getUrl().toString(), str, getRequestType(webResourceRequest, str));
    }

    public static WebResourceResponse createBlockedResponse(String str, BlockResult blockResult) {
        String redirect = blockResult.getRedirect();
        if (redirect != null && redirect.startsWith("data:")) {
            int iIndexOf = redirect.indexOf(59);
            int iIndexOf2 = redirect.indexOf(44);
            if (iIndexOf > 5 && iIndexOf2 > iIndexOf) {
                try {
                    String strSubstring = redirect.substring(5, iIndexOf);
                    byte[] bArrDecode = Base64.decode(redirect.substring(iIndexOf2 + 1), 0);
                    HashMap map = new HashMap();
                    map.put("Content-Type", strSubstring);
                    map.put("Access-Control-Allow-Credentials", "true");
                    map.put("Access-Control-Allow-Headers", "Cache-Control");
                    map.put("Access-Control-Allow-Origin", "*");
                    return new WebResourceResponse(strSubstring, null, 200, "OK", map, new ByteArrayInputStream(bArrDecode));
                } catch (IllegalArgumentException unused) {
                }
            }
        }
        if ("sub_frame".equals(str)) {
            return new WebResourceResponse("text/html", "utf-8", MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, "Internal Server Error", null, null);
        }
        return new WebResourceResponse("text/plain", "utf-8", new BlockedInputStream());
    }

    public static String getRequestType(WebResourceRequest webResourceRequest, String str) {
        if ("OPTIONS".equals(webResourceRequest.getMethod())) {
            return "beacon";
        }
        String string = webResourceRequest.getUrl().toString();
        Map<String, String> requestHeaders = webResourceRequest.getRequestHeaders();
        if (webResourceRequest.isForMainFrame() && string.equals(str)) {
            return "main_frame";
        }
        if (string.startsWith("ws")) {
            return "websocket";
        }
        if (requestHeaders != null && "XMLHttpRequest".equals(requestHeaders.get("X-Requested-With"))) {
            return "xhr";
        }
        String strTrim = requestHeaders != null ? requestHeaders.get("Accept") : null;
        if (!webResourceRequest.isForMainFrame() && strTrim != null && strTrim.startsWith("text/html")) {
            return "sub_frame";
        }
        String requestExtension = getRequestExtension(string);
        if ("js".equals(requestExtension)) {
            return "script";
        }
        if ("css".equals(requestExtension)) {
            return "stylesheet";
        }
        if ("otf".equals(requestExtension) || "ttf".equals(requestExtension) || "ttc".equals(requestExtension) || "woff".equals(requestExtension) || "woff2".equals(requestExtension)) {
            return "font";
        }
        if (!"php".equals(requestExtension)) {
            String requestMime = getRequestMime(requestExtension);
            if (!"application/octet-stream".equals(requestMime)) {
                return getRequestTypeFromMime(requestMime);
            }
        }
        if (TextUtils.isEmpty(strTrim) || "*/*".equals(strTrim)) {
            return "other";
        }
        int iIndexOf = strTrim.indexOf(44);
        if (iIndexOf > 0) {
            strTrim = strTrim.substring(0, iIndexOf).trim();
        }
        return getRequestTypeFromMime(strTrim);
    }

    private static String getRequestExtension(String str) {
        if (str != null && !str.isEmpty()) {
            int iIndexOf = str.indexOf(63);
            if (iIndexOf > 0) {
                str = str.substring(0, iIndexOf);
            }
            int iLastIndexOf = str.lastIndexOf(47);
            if (iLastIndexOf > 0) {
                str = str.substring(iLastIndexOf + 1);
            }
            int iLastIndexOf2 = str.lastIndexOf(46);
            if (iLastIndexOf2 <= 0 || iLastIndexOf2 == str.length() - 1) {
                if (str.endsWith("js")) {
                    return "js";
                }
            } else {
                String lowerCase = str.substring(iLastIndexOf2 + 1).toLowerCase(Locale.ROOT);
                if (!lowerCase.isEmpty() && lowerCase.length() <= 8) {
                    return lowerCase;
                }
                return null;
            }
        }
        return null;
    }

    private static String getRequestMime(String str) {
        if ("mhtml".equals(str) || "mht".equals(str)) {
            return "multipart/related";
        }
        if ("json".equals(str)) {
            return "application/json";
        }
        String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str);
        return (mimeTypeFromExtension == null || mimeTypeFromExtension.isEmpty()) ? "application/octet-stream" : mimeTypeFromExtension;
    }

    private static String getRequestTypeFromMime(String str) {
        if (TextUtils.isEmpty(str)) {
            return "other";
        }
        if ("application/javascript".equals(str) || "application/x-javascript".equals(str) || "text/javascript".equals(str) || "application/json".equals(str)) {
            return "script";
        }
        if ("text/css".equals(str)) {
            return "stylesheet";
        }
        if (str.startsWith("image/")) {
            return "image";
        }
        if (str.startsWith("video/") || str.startsWith("audio/")) {
            return "media";
        }
        return str.startsWith("font/") ? "font" : "other";
    }

    private static String createHideCss(String[] strArr) {
        if (strArr != null && strArr.length != 0) {
            StringBuilder sb = new StringBuilder();
            for (String str : strArr) {
                if (!TextUtils.isEmpty(str) && str.indexOf(123) < 0 && str.indexOf(125) < 0) {
                    sb.append(str);
                    sb.append("{display:none!important}\n");
                }
            }
            if (sb.length() > 0) {
                return sb.toString();
            }
        }
        return null;
    }

    public static class BlockedInputStream extends InputStream {
        private BlockedInputStream() {
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            throw AdBlockClient.BLOCKED_EXCEPTION;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            throw AdBlockClient.BLOCKED_EXCEPTION;
        }
    }

    public static class CosmeticHide {
        private final String[] exceptions;
        private final boolean genericHide;
        private final String hideCss;
        private final String injectedScript;
        private final String url;

        public CosmeticHide(String str, String str2, String str3, String[] strArr, boolean z) {
            this.url = str;
            this.hideCss = str2;
            this.injectedScript = str3;
            this.exceptions = strArr;
            this.genericHide = z;
        }

        public String getUrl() {
            return this.url;
        }

        public String getHideCss() {
            return this.hideCss;
        }

        public String getInjectedScript() {
            return this.injectedScript;
        }

        public String[] getExceptions() {
            return this.exceptions;
        }

        public boolean isGenericHide() {
            return this.genericHide;
        }
    }
}
