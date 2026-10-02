package com.exteragram.messenger.adblock.backend;

import android.content.SharedPreferences;
import android.util.Base64;
import com.exteragram.messenger.backup.PreferencesUtils;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.url._UrlKt;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;

/* JADX INFO: loaded from: classes.dex */
public class SubscriptionsManager {
    private static SubscriptionsManager instance;
    private static final Pattern redirectPattern = Pattern.compile("!\\s*Redirect:\\s*(\\S+)");
    private final Object lock = new Object();
    private final DispatchQueue queue = new DispatchQueue("SubscriptionsManager");
    private final OkHttpClient client = ExteraHttpClient.INSTANCE.getClient();
    private final SharedPreferences prefs = PreferencesUtils.getPreferences("ublock_subscriptions");

    public static SubscriptionsManager getInstance() {
        if (instance == null) {
            instance = new SubscriptionsManager();
        }
        return instance;
    }

    private File getFileForUrl(String str) {
        File file = new File(ApplicationLoader.applicationContext.getFilesDir(), "adblock");
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, Base64.encodeToString(str.getBytes(StandardCharsets.UTF_8), 10) + ".txt");
    }

    public void update(final String[] strArr, final Runnable runnable) {
        this.queue.postRunnable(() -> updateInternal(strArr, runnable));
    }

    private void updateInternal(String[] strArr, Runnable runnable) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        long jCurrentTimeMillis = System.currentTimeMillis();
        for (FilterMetadata filterMetadata : getSubscriptions()) {
            if (jCurrentTimeMillis >= filterMetadata.expires || !getFileForUrl(filterMetadata.url).exists()) {
                linkedHashSet.add(filterMetadata.url);
            }
        }
        synchronized (this.lock) {
            try {
                for (String str : strArr) {
                    if (!this.prefs.contains("metadata_" + str)) {
                        if (!this.prefs.contains("redirect_" + str)) {
                            linkedHashSet.add(str);
                        }
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        Iterator it = linkedHashSet.iterator();
        while (it.hasNext()) {
            fetchSubscription((String) it.next(), 0);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public boolean hasFilters() {
        return !getSubscriptionFilePaths().isEmpty();
    }

    private void unsubscribe(String str) {
        synchronized (this.lock) {
            SharedPreferences.Editor editorEdit = this.prefs.edit();
            editorEdit.remove("metadata_" + str);
            editorEdit.apply();
        }
        File fileForUrl = getFileForUrl(str);
        if (fileForUrl.exists()) {
            fileForUrl.delete();
        }
    }

    public List<FilterMetadata> getSubscriptions() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.lock) {
            for (String str : this.prefs.getAll().keySet()) {
                if (str.startsWith("metadata_")) {
                    try {
                        String string = this.prefs.getString(str, null);
                        if (string != null) {
                            arrayList.add(FilterMetadata.fromJson(new JSONObject(string)));
                        }
                    } catch (JSONException unused) {
                    }
                }
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    public List<String> getSubscriptionFilePaths() {
        ArrayList arrayList = new ArrayList();
        Iterator<FilterMetadata> it = getSubscriptions().iterator();
        while (it.hasNext()) {
            File fileForUrl = getFileForUrl(it.next().url);
            if (fileForUrl.exists()) {
                arrayList.add(fileForUrl.getAbsolutePath());
            }
        }
        return arrayList;
    }

    /* JADX WARN: Undo finally extract visitor
    java.lang.NullPointerException: Cannot invoke "Object.hashCode()" because "this.second" is null
    	at jadx.core.utils.Pair.hashCode(Pair.java:35)
    	at java.base/java.util.HashMap.hash(Unknown Source)
    	at java.base/java.util.HashMap.getNode(Unknown Source)
    	at java.base/java.util.HashMap.containsKey(Unknown Source)
    	at jadx.core.dex.visitors.finaly.traverser.state.TraverserGlobalCommonState.hasBlocksBeenCached(TraverserGlobalCommonState.java:35)
    	at jadx.core.dex.visitors.finaly.traverser.handlers.MergePathActivePathTraverserHandler.handle(MergePathActivePathTraverserHandler.java:174)
    	at jadx.core.dex.visitors.finaly.traverser.handlers.AbstractActivePathTraverserHandler.process(AbstractActivePathTraverserHandler.java:19)
    	at jadx.core.dex.visitors.finaly.traverser.TraverserController.processHandlerImplementations(TraverserController.java:43)
    	at jadx.core.dex.visitors.finaly.traverser.TraverserController.advance(TraverserController.java:156)
    	at jadx.core.dex.visitors.finaly.traverser.TraverserController.process(TraverserController.java:79)
    	at jadx.core.dex.visitors.finaly.MarkFinallyVisitor.findCommonInsns(MarkFinallyVisitor.java:404)
    	at jadx.core.dex.visitors.finaly.MarkFinallyVisitor.extractFinally(MarkFinallyVisitor.java:284)
    	at jadx.core.dex.visitors.finaly.MarkFinallyVisitor.processTryBlock(MarkFinallyVisitor.java:202)
    	at jadx.core.dex.visitors.finaly.MarkFinallyVisitor.visit(MarkFinallyVisitor.java:135)
     */
    private boolean fetchSubscription(String str, int i) {
        try {
            Response responseExecute = this.client.newCall(new Request.Builder().url(str).header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4 Safari/605.1.15").build()).execute();
            try {
                if (responseExecute.isSuccessful()) {
                    String strString = responseExecute.body().string();
                    String strExtractRedirect = extractRedirect(strString);
                    if (strExtractRedirect != null) {
                        if (i < 3 && fetchSubscription(strExtractRedirect, i + 1)) {
                            unsubscribe(str);
                            synchronized (this.lock) {
                                try {
                                    this.prefs.edit().putString("redirect_" + str, strExtractRedirect).apply();
                                } catch (Throwable th) {
                                    throw th;
                                }
                            }
                            responseExecute.close();
                            return true;
                        }
                    } else {
                        FilterMetadata metadata = parseMetadata(str, strString);
                        File fileForUrl = getFileForUrl(str);
                        File file = new File(fileForUrl.getPath() + ".tmp");
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            try {
                                fileOutputStream.write(strString.getBytes(StandardCharsets.UTF_8));
                                fileOutputStream.close();
                                if (!file.renameTo(fileForUrl)) {
                                    file.delete();
                                } else {
                                    synchronized (this.lock) {
                                        try {
                                            SharedPreferences.Editor editorEdit = this.prefs.edit();
                                            editorEdit.putString("metadata_" + str, metadata.toJson().toString());
                                            editorEdit.apply();
                                        } catch (Throwable th2) {
                                            throw th2;
                                        }
                                    }
                                    responseExecute.close();
                                    return true;
                                }
                            } catch (Throwable th3) {
                                try {
                                    fileOutputStream.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                                throw th3;
                            }
                        } catch (IOException unused) {
                            file.delete();
                            responseExecute.close();
                            return false;
                        }
                    }
                    return false;
                }
                responseExecute.close();
                return false;
            } catch (Throwable th5) {
                if (responseExecute != null) {
                    try {
                        responseExecute.close();
                    } catch (Throwable th6) {
                        th5.addSuppressed(th6);
                    }
                }
                throw th5;
            }
        } catch (Exception unused2) {
            return false;
        }
    }

    private String extractRedirect(String str) {
        Matcher matcher = redirectPattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private FilterMetadata parseMetadata(String str, String str2) {
        String strExtractMetadataValue = extractMetadataValue(str2, "Title");
        String strExtractMetadataValue2 = extractMetadataValue(str2, "Homepage");
        int iCountRules = countRules(str2);
        long jCalculateExpiration = calculateExpiration(str2);
        if (strExtractMetadataValue == null) {
            strExtractMetadataValue = "Unnamed list";
        }
        String str3 = strExtractMetadataValue;
        if (strExtractMetadataValue2 == null) {
            strExtractMetadataValue2 = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        return new FilterMetadata(str, str3, strExtractMetadataValue2, iCountRules, jCalculateExpiration);
    }

    private String extractMetadataValue(String str, String str2) {
        Matcher matcher = Pattern.compile("!\\s*" + str2 + ":\\s*(.+)").matcher(str);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private int countRules(String str) {
        int i = 0;
        for (String str2 : str.split("\n")) {
            String strTrim = str2.trim();
            if (!strTrim.isEmpty() && !strTrim.startsWith("!")) {
                i++;
            }
        }
        return i;
    }

    private long calculateExpiration(String str) {
        long jCurrentTimeMillis;
        String strExtractMetadataValue = extractMetadataValue(str, "Expires");
        if (strExtractMetadataValue == null) {
            jCurrentTimeMillis = System.currentTimeMillis();
        } else {
            try {
                int i = Integer.parseInt(strExtractMetadataValue.replaceAll("[^0-9]", _UrlKt.FRAGMENT_ENCODE_SET));
                if (strExtractMetadataValue.contains("hour")) {
                    return System.currentTimeMillis() + TimeUnit.HOURS.toMillis(i);
                }
                if (strExtractMetadataValue.contains("day")) {
                    return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(i);
                }
                return System.currentTimeMillis() + 432000000;
            } catch (NumberFormatException unused) {
                jCurrentTimeMillis = System.currentTimeMillis();
            }
        }
        return jCurrentTimeMillis + 432000000;
    }

    public static class FilterMetadata {
        public final long expires;
        public final String homepage;
        public final int rulesCount;
        public final String title;
        public final String url;

        private FilterMetadata(String str, String str2, String str3, int i, long j) {
            this.url = str;
            this.title = str2;
            this.homepage = str3;
            this.rulesCount = i;
            this.expires = j;
        }

        public static FilterMetadata fromJson(JSONObject jSONObject) throws JSONException {
            return new FilterMetadata(jSONObject.getString("url"), jSONObject.getString("title"), jSONObject.getString("homepage"), jSONObject.getInt("rulesCount"), jSONObject.getLong("expires"));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public JSONObject toJson() throws JSONException {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("url", this.url);
            jSONObject.put("title", this.title);
            jSONObject.put("homepage", this.homepage);
            jSONObject.put("rulesCount", this.rulesCount);
            jSONObject.put("expires", this.expires);
            return jSONObject;
        }
    }
}
