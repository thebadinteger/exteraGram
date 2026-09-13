package com.exteragram.messenger.adblock;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.adblock.data.BlockResult;
import com.exteragram.messenger.adblock.data.UrlCosmeticResources;
import com.exteragram.messenger.adblock.interop.AdBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.telegram.messenger.FileLog;

public abstract class AdBlockClient {
    public static CosmeticHide getCosmeticHide(String str) {
        UrlCosmeticResources cosmeticResources = AdBlock.getCosmeticResources(str);
        if (cosmeticResources == null) {
            FileLog.e("err cosmetic: " + str);
            return null;
        }
        FileLog.d("hideSelectors: " + Arrays.toString(cosmeticResources.getHideSelectors()));
        FileLog.d("proceduralActions: " + Arrays.toString(cosmeticResources.getProceduralActions()));
        FileLog.d("exceptions: " + Arrays.toString(cosmeticResources.getExceptions()));
        FileLog.d("injectedScript: " + cosmeticResources.getInjectedScript() + " genericHide: " + cosmeticResources.isGenericHide());
        return new CosmeticHide(createHideScript(cosmeticResources.getHideSelectors()), cosmeticResources.getInjectedScript(), cosmeticResources.getExceptions(), cosmeticResources.isGenericHide());
    }

    public static String getCosmeticHideContinuous(CosmeticHide cosmeticHide, Set<String> set, String str) {
        String[] hiddenSelectors;
        ClassesAndIds classesAndIds = (ClassesAndIds) ExteraConfig.getGSON().fromJson(str, ClassesAndIds.class);
        if (classesAndIds == null) {
            return null;
        }
        String[] classes = classesAndIds.getClasses();
        String[] ids = classesAndIds.getIds();
        if ((classes.length == 0 && ids.length == 0) || (hiddenSelectors = AdBlock.getHiddenSelectors(classes, ids, cosmeticHide.exceptions)) == null || hiddenSelectors.length == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (String str2 : hiddenSelectors) {
            if (!set.contains(str2)) {
                arrayList.add(str2);
                set.add(str2);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return createHideScript((String[]) arrayList.toArray(new String[0]));
    }

    public static BlockResult isAdRequest(WebResourceRequest webResourceRequest, String str) {
        return AdBlock.getBlockResult(webResourceRequest.getUrl().toString(), str, getRequestType(webResourceRequest, str));
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
        String strTrim = requestHeaders != null ? requestHeaders.get("Accept") : null;
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

    private static String createHideScript(String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            return null;
        }
        return "(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + android.util.Base64.encodeToString((android.text.TextUtils.join(",", strArr) + "{display: none !important;}").getBytes(), android.util.Base64.NO_WRAP) + "');parent.appendChild(style)})()";
    }

    public static class CosmeticHide {
        private final String[] exceptions;
        private final boolean genericHide;
        private final String hideCss;
        private final String injectedScript;

        public CosmeticHide(String str, String str2, String[] strArr, boolean z) {
            this.hideCss = str;
            this.injectedScript = str2;
            this.exceptions = strArr;
            this.genericHide = z;
        }

        public String getHideCss() {
            return this.hideCss;
        }

        public String getInjectedScript() {
            return this.injectedScript;
        }

        public boolean isGenericHide() {
            return this.genericHide;
        }
    }

    public static class ClassesAndIds {
        private final String[] classes;
        private final String[] ids;

        public ClassesAndIds(String[] strArr, String[] strArr2) {
            this.classes = strArr;
            this.ids = strArr2;
        }

        public String[] getClasses() {
            return this.classes;
        }

        public String[] getIds() {
            return this.ids;
        }
    }
}
