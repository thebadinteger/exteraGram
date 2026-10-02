package com.exteragram.messenger.adblock.backend;

import android.content.SharedPreferences;
import android.util.Base64;
import com.exteragram.messenger.backup.PreferencesUtils;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class ScriptletsManager {
    private static final Map<String, ScriptletInfo> SCRIPTLETS_MAP = new HashMap<String, ScriptletInfo>() { // from class: com.exteragram.messenger.adblock.backend.ScriptletsManager.1
        {
            put("1x1.gif", new ScriptletInfo("1x1-transparent.gif"));
            put("2x2.png", new ScriptletInfo("2x2-transparent.png"));
            put("3x2.png", new ScriptletInfo("3x2-transparent.png"));
            put("32x32.png", new ScriptletInfo("32x32-transparent.png"));
            put("amazon_ads.js", new ScriptletInfo("amazon-adsystem.com/aax2/amzn_ads.js"));
            put("amazon_apstag.js", new ScriptletInfo(null));
            put("ampproject_v0.js", new ScriptletInfo("ampproject.org/v0.js"));
            put("chartbeat.js", new ScriptletInfo("static.chartbeat.com/chartbeat.js"));
            put("doubleclick_instream_ad_status.js", new ScriptletInfo("doubleclick.net/instream/ad_status.js"));
            put("empty", new ScriptletInfo(null));
            put("fingerprint2.js", new ScriptletInfo(null));
            put("fingerprint3.js", new ScriptletInfo(null));
            put("google-analytics_analytics.js", new ScriptletInfo(new String[]{"google-analytics.com/analytics.js", "googletagmanager_gtm.js", "googletagmanager.com/gtm.js"}));
            put("google-analytics_cx_api.js", new ScriptletInfo("google-analytics.com/cx/api.js"));
            put("google-analytics_ga.js", new ScriptletInfo("google-analytics.com/ga.js"));
            put("google-analytics_inpage_linkid.js", new ScriptletInfo("google-analytics.com/inpage_linkid.js"));
            put("google-ima.js", new ScriptletInfo("google-ima3"));
            put("googlesyndication_adsbygoogle.js", new ScriptletInfo(new String[]{"googlesyndication.com/adsbygoogle.js", "googlesyndication-adsbygoogle"}));
            put("googletagservices_gpt.js", new ScriptletInfo(new String[]{"googletagservices.com/gpt.js", "googletagservices-gpt"}));
            put("hd-main.js", new ScriptletInfo(null));
            put("nobab2.js", new ScriptletInfo(null));
            put("noeval.js", new ScriptletInfo(null));
            put("noeval-silent.js", new ScriptletInfo("silent-noeval.js"));
            put("nofab.js", new ScriptletInfo("fuckadblock.js-3.2.0"));
            put("noop-0.1s.mp3", new ScriptletInfo(new String[]{"noopmp3-0.1s", "abp-resource:blank-mp3"}));
            put("noop-0.5s.mp3", new ScriptletInfo(null));
            put("noop-1s.mp4", new ScriptletInfo(new String[]{"noopmp4-1s", "abp-resource:blank-mp4"}));
            put("noop.css", new ScriptletInfo(null));
            put("noop.html", new ScriptletInfo("noopframe"));
            put("noop.js", new ScriptletInfo(new String[]{"noopjs", "abp-resource:blank-js"}));
            put("noop.json", new ScriptletInfo(new String[]{"noopjson"}));
            put("noop.txt", new ScriptletInfo("nooptext"));
            put("noop-vast2.xml", new ScriptletInfo("noopvast-2.0"));
            put("noop-vast3.xml", new ScriptletInfo("noopvast-3.0"));
            put("noop-vast4.xml", new ScriptletInfo("noopvast-4.0"));
            put("noop-vmap1.xml", new ScriptletInfo(new String[]{"noop-vmap1.0.xml", "noopvmap-1.0"}));
            put("outbrain-widget.js", new ScriptletInfo("widgets.outbrain.com/outbrain.js"));
            put("popads.js", new ScriptletInfo(new String[]{"popads.net.js", "prevent-popads-net.js"}));
            put("popads-dummy.js", new ScriptletInfo(null));
            put("prebid-ads.js", new ScriptletInfo(null));
            put("scorecardresearch_beacon.js", new ScriptletInfo("scorecardresearch.com/beacon.js"));
            put("sensors-analytics.js", new ScriptletInfo(null));
            put("nitropay_ads.js", new ScriptletInfo(null));
        }
    };
    private static ScriptletsManager instance;
    private final Object lock = new Object();
    private final DispatchQueue queue = new DispatchQueue("ScriptletsManager");
    private final OkHttpClient client = ExteraHttpClient.INSTANCE.getClient();
    private final SharedPreferences prefs = PreferencesUtils.getPreferences("ublock_scriptlets");

    public static ScriptletsManager getInstance() {
        if (instance == null) {
            instance = new ScriptletsManager();
        }
        return instance;
    }

    public static String getExtension(String str) {
        if (str.endsWith(".css")) {
            return ".css";
        }
        if (str.endsWith(".gif")) {
            return ".gif";
        }
        if (str.endsWith(".html")) {
            return ".html";
        }
        if (str.endsWith(".js")) {
            return ".js";
        }
        if (str.endsWith(".json")) {
            return ".json";
        }
        if (str.endsWith(".mp3")) {
            return ".mp3";
        }
        if (str.endsWith(".mp4")) {
            return ".mp4";
        }
        if (str.endsWith(".png")) {
            return ".png";
        }
        if (str.endsWith(".txt") || !str.endsWith(".xml")) {
            return ".txt";
        }
        return ".xml";
    }

    public void download(final Utilities.Callback<Boolean> callback) {
        this.queue.postRunnable(() -> downloadInternal(callback));
    }

    private void downloadInternal(Utilities.Callback callback) {
        boolean z;
        Iterator<Map.Entry<String, ScriptletInfo>> it = SCRIPTLETS_MAP.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                z = true;
                break;
            }
            Map.Entry<String, ScriptletInfo> next = it.next();
            if (!downloadScriptlet(next.getKey(), next.getValue())) {
                z = false;
                break;
            }
        }
        if (z) {
            synchronized (this.lock) {
                this.prefs.edit().putBoolean("__downloaded", true).apply();
            }
        }
        if (callback != null) {
            callback.run(Boolean.valueOf(z));
        }
    }

    private boolean downloadScriptlet(String str, ScriptletInfo scriptletInfo) {
        synchronized (this.lock) {
            try {
                if (this.prefs.contains(str)) {
                    return true;
                }
                try {
                    Response responseExecute = this.client.newCall(new Request.Builder().url("https://raw.githubusercontent.com/gorhill/uBlock/master/src/web_accessible_resources/" + str).header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4 Safari/605.1.15").build()).execute();
                    try {
                        if (responseExecute.code() != 404) {
                            if (responseExecute.isSuccessful()) {
                                String strEncodeToString = Base64.encodeToString(responseExecute.body().bytes(), 2);
                                synchronized (this.lock) {
                                    try {
                                        SharedPreferences.Editor editorEdit = this.prefs.edit();
                                        editorEdit.putString(str, strEncodeToString);
                                        if (scriptletInfo.alias != null) {
                                            JSONArray jSONArray = new JSONArray();
                                            Object obj = scriptletInfo.alias;
                                            if (obj instanceof String) {
                                                jSONArray.put(obj);
                                            } else if (obj instanceof String[]) {
                                                for (String str2 : (String[]) obj) {
                                                    jSONArray.put(str2);
                                                }
                                            }
                                            editorEdit.putString(str + "_aliases", jSONArray.toString());
                                        }
                                        editorEdit.apply();
                                    } catch (Throwable th) {
                                        throw th;
                                    }
                                }
                            } else {
                                responseExecute.close();
                                return false;
                            }
                            return false;
                        }
                        responseExecute.close();
                        return true;
                    } catch (Throwable th2) {
                        if (responseExecute != null) {
                            try {
                                responseExecute.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        }
                        throw th2;
                    }
                } catch (IOException unused) {
                    return false;
                }
            } catch (Throwable th4) {
                throw th4;
            }
        }
    }

    public boolean isDownloaded() {
        boolean z;
        synchronized (this.lock) {
            z = this.prefs.getBoolean("__downloaded", false);
        }
        return z;
    }

    public Collection<Scriptlet> iterScriptlets() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.lock) {
            Iterator<Map.Entry<String, ScriptletInfo>> it = SCRIPTLETS_MAP.entrySet().iterator();
            while (it.hasNext()) {
                String key = it.next().getKey();
                String string = this.prefs.getString(key, null);
                if (string != null) {
                    ArrayList arrayList2 = new ArrayList();
                    String string2 = this.prefs.getString(key + "_aliases", null);
                    if (string2 != null) {
                        try {
                            JSONArray jSONArray = new JSONArray(string2);
                            for (int i = 0; i < jSONArray.length(); i++) {
                                arrayList2.add(jSONArray.getString(i));
                            }
                        } catch (JSONException unused) {
                        }
                    }
                    arrayList.add(new Scriptlet(key, arrayList2, string));
                }
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    public static class Scriptlet {
        public final List<String> aliases;
        public final String content;
        public final String filename;

        private Scriptlet(String str, List<String> list, String str2) {
            this.filename = str;
            this.aliases = list;
            this.content = str2;
        }
    }

    public static class ScriptletInfo {
        final Object alias;

        public ScriptletInfo(Object obj) {
            this.alias = obj;
        }
    }
}
