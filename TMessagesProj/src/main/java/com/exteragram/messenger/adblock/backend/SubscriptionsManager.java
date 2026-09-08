package com.exteragram.messenger.adblock.backend;

import android.content.SharedPreferences;
import android.util.Base64;
import com.exteragram.messenger.backup.PreferencesUtils;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.url._UrlKt;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;

public class SubscriptionsManager {
    private static SubscriptionsManager instance;
    private static final Pattern redirectPattern = Pattern.compile("!\\s*Redirect:\\s*(\\S+)");
    private final Object lock = new Object();
    private final DispatchQueue queue = new DispatchQueue("SubscriptionsManager");
    private final OkHttpClient client = ExteraHttpClient.INSTANCE.getClient();
    private final SharedPreferences prefs = PreferencesUtils.getPreferences("ublock_subscriptions");

    public interface SubscriptionCallback {
        void onComplete(boolean z);
    }

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

    public void initialize(final Runnable runnable) {
        this.queue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$initialize$1(runnable);
            }
        });
    }

    public void $r8$lambda$ex7K0DzJlEYCVNCVLClmUrbS_nY(AtomicInteger atomicInteger, List list, Runnable runnable, boolean z) {
        if (atomicInteger.incrementAndGet() == list.size()) {
            runnable.run();
        }
    }

    public void subscribe(final String str, final SubscriptionCallback subscriptionCallback) {
        this.queue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$subscribe$2(str, subscriptionCallback);
            }
        });
    }

    public void unsubscribe(String str) {
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

    public void lambda$subscribe$2(String str, final SubscriptionCallback subscriptionCallback) {
        try {
            Response responseExecute = this.client.newCall(new Request.Builder().url(str).header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4 Safari/605.1.15").build()).execute();
            try {
                if (responseExecute.getIsSuccessful()) {
                    String strString = responseExecute.body().string();
                    String strExtractRedirect = extractRedirect(strString);
                    if (strExtractRedirect != null) {
                        unsubscribe(str);
                        lambda$subscribe$2(strExtractRedirect, subscriptionCallback);
                    } else {
                        FilterMetadata metadata = parseMetadata(str, strString);
                        FileOutputStream fileOutputStream = new FileOutputStream(getFileForUrl(str));
                        try {
                            fileOutputStream.write(strString.getBytes(StandardCharsets.UTF_8));
                            fileOutputStream.close();
                            synchronized (this.lock) {
                                SharedPreferences.Editor editorEdit = this.prefs.edit();
                                editorEdit.putString("metadata_" + str, metadata.toJson().toString());
                                editorEdit.apply();
                            }
                            if (subscriptionCallback != null) {
                                AndroidUtilities.runOnUIThread(new Runnable() { 
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        subscriptionCallback.onComplete(true);
                                    }
                                });
                            }
                        } catch (Throwable th) {
                            try {
                                fileOutputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                    }
                } else if (subscriptionCallback != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() { 
                        @Override // java.lang.Runnable
                        public final void run() {
                            subscriptionCallback.onComplete(false);
                        }
                    });
                }
                responseExecute.close();
            } catch (Throwable th3) {
                if (responseExecute != null) {
                    try {
                        responseExecute.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        } catch (Exception unused) {
            if (subscriptionCallback != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        subscriptionCallback.onComplete(false);
                    }
                });
            }
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

        public static FilterMetadata fromJson(JSONObject jSONObject) {
            return new FilterMetadata(jSONObject.getString("url"), jSONObject.getString("title"), jSONObject.getString("homepage"), jSONObject.getInt("rulesCount"), jSONObject.getLong("expires"));
        }

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
