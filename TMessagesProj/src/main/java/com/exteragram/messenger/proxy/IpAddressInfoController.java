package com.exteragram.messenger.proxy;

import android.net.Uri;
import android.os.SystemClock;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.sun.jna.Callback;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;
import kotlin.time.DurationKt;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.url._UrlKt;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;

@Metadata(d1 = {"\u0000]\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005*\u0001%\bÆ\u0002\u0018\u00002\u00020\u0001:\u0002+,B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001b\u0010\u0006\u001a\u0004\u0018\u00010\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004H\u0007¢\u0006\u0004\b\u0006\u0010\u0007J)\u0010\r\u001a\u0004\u0018\u00010\f2\u0006\u0010\b\u001a\u00020\u00042\u000e\u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\tH\u0007¢\u0006\u0004\b\r\u0010\u000eJ'\u0010\u0011\u001a\u00020\u00042\u0016\u0010\u0010\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00040\u000f\"\u0004\u0018\u00010\u0004H\u0007¢\u0006\u0004\b\u0011\u0010\u0012J\u0019\u0010\u0015\u001a\u00020\u00142\b\u0010\u0013\u001a\u0004\u0018\u00010\u0004H\u0002¢\u0006\u0004\b\u0015\u0010\u0016J\u0019\u0010\u0018\u001a\u0004\u0018\u00010\u00172\u0006\u0010\b\u001a\u00020\u0004H\u0002¢\u0006\u0004\b\u0018\u0010\u0019J!\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\b\u001a\u00020\u00042\b\u0010\u001a\u001a\u0004\u0018\u00010\nH\u0002¢\u0006\u0004\b\u001c\u0010\u001dJ!\u0010\u001f\u001a\u0004\u0018\u00010\n2\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u0004H\u0002¢\u0006\u0004\b\u001f\u0010 R\u0014\u0010\"\u001a\u00020!8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\"\u0010#R\u0014\u0010$\u001a\u00020!8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b$\u0010#R\u0014\u0010&\u001a\u00020%8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b&\u0010'R\u0014\u0010)\u001a\u00020(8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b)\u0010*¨\u0006-"}, d2 = {"Lcom/exteragram/messenger/proxy/IpAddressInfoController;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", _UrlKt.FRAGMENT_ENCODE_SET, "url", "extractIpAddress", "(Ljava/lang/String;)Ljava/lang/String;", "ipAddress", "Lorg/telegram/messenger/Utilities$Callback;", "Lcom/exteragram/messenger/proxy/IpAddressInfoController$IpAddressInfo;", Callback.METHOD_NAME, "Lokhttp3/Call;", "requestIpAddressInfo", "(Ljava/lang/String;Lorg/telegram/messenger/Utilities$Callback;)Lokhttp3/Call;", _UrlKt.FRAGMENT_ENCODE_SET, "values", "joinInfo", "([Ljava/lang/String;)Ljava/lang/String;", "host", _UrlKt.FRAGMENT_ENCODE_SET, "isIpAddress", "(Ljava/lang/String;)Z", "Lcom/exteragram/messenger/proxy/IpAddressInfoController$CacheEntry;", "getCachedInfo", "(Ljava/lang/String;)Lcom/exteragram/messenger/proxy/IpAddressInfoController$CacheEntry;", "info", _UrlKt.FRAGMENT_ENCODE_SET, "putCachedInfo", "(Ljava/lang/String;Lcom/exteragram/messenger/proxy/IpAddressInfoController$IpAddressInfo;)V", "response", "parseIpAddressInfo", "(Ljava/lang/String;Ljava/lang/String;)Lcom/exteragram/messenger/proxy/IpAddressInfoController$IpAddressInfo;", "Lkotlin/text/Regex;", "ipv4Regex", "Lkotlin/text/Regex;", "ipv6CharsRegex", "com/exteragram/messenger/proxy/IpAddressInfoController$cache$1", "cache", "Lcom/exteragram/messenger/proxy/IpAddressInfoController$cache$1;", "Lokhttp3/OkHttpClient;", "httpClient", "Lokhttp3/OkHttpClient;", "IpAddressInfo", "CacheEntry", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nIpAddressInfoController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IpAddressInfoController.kt\ncom/exteragram/messenger/proxy/IpAddressInfoController\n+ 2 Uri.kt\nandroidx/core/net/UriKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,229:1\n29#2:230\n3938#3:231\n4474#3,2:232\n1786#4,3:234\n*S KotlinDebug\n*F\n+ 1 IpAddressInfoController.kt\ncom/exteragram/messenger/proxy/IpAddressInfoController\n*L\n75#1:230\n168#1:231\n168#1:232,2\n181#1:234,3\n*E\n"})
public final class IpAddressInfoController {
    private static final OkHttpClient httpClient;
    public static final IpAddressInfoController INSTANCE = new IpAddressInfoController();
    private static final Regex ipv4Regex = new Regex("^\\d{1,3}(?:\\.\\d{1,3}){3}$");
    private static final Regex ipv6CharsRegex = new Regex("^[0-9a-f:.]+$", RegexOption.IGNORE_CASE);
    private static final IpAddressInfoController$cache$1 cache = new IpAddressInfoController$cache$1();

    private IpAddressInfoController() {
    }

    static {
        OkHttpClient.Builder builderNewBuilder = ExteraHttpClient.INSTANCE.getClient().newBuilder();
        TimeUnit timeUnit = TimeUnit.SECONDS;
        httpClient = builderNewBuilder.connectTimeout(4L, timeUnit).readTimeout(4L, timeUnit).writeTimeout(4L, timeUnit).build();
    }

    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\u0002\u0012\u0006\u0010\t\u001a\u00020\u0002\u0012\u0006\u0010\n\u001a\u00020\u0002¢\u0006\u0004\b\u000b\u0010\fJ\u0010\u0010\r\u001a\u00020\u0002HÖ\u0001¢\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u0010\u001a\u00020\u000fHÖ\u0001¢\u0006\u0004\b\u0010\u0010\u0011J\u001a\u0010\u0014\u001a\u00020\u00132\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0003\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0003\u0010\u0016R\u0014\u0010\u0004\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0004\u0010\u0016R\u0014\u0010\u0005\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0005\u0010\u0016R\u0014\u0010\u0006\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0006\u0010\u0016R\u0014\u0010\u0007\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0007\u0010\u0016R\u0014\u0010\b\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\b\u0010\u0016R\u0014\u0010\t\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\t\u0010\u0016R\u0014\u0010\n\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b\n\u0010\u0016¨\u0006\u0017"}, d2 = {"Lcom/exteragram/messenger/proxy/IpAddressInfoController$IpAddressInfo;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "ip", "country", "region", "city", "isp", "organization", "domain", "timezone", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "toString", "()Ljava/lang/String;", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", "()I", "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "Ljava/lang/String;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class IpAddressInfo {

        @JvmField
        public final String city;

        @JvmField
        public final String country;

        @JvmField
        public final String domain;

        @JvmField
        public final String ip;

        @JvmField
        public final String isp;

        @JvmField
        public final String organization;

        @JvmField
        public final String region;

        @JvmField
        public final String timezone;

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof IpAddressInfo)) {
                return false;
            }
            IpAddressInfo ipAddressInfo = (IpAddressInfo) other;
            return Intrinsics.areEqual(this.ip, ipAddressInfo.ip) && Intrinsics.areEqual(this.country, ipAddressInfo.country) && Intrinsics.areEqual(this.region, ipAddressInfo.region) && Intrinsics.areEqual(this.city, ipAddressInfo.city) && Intrinsics.areEqual(this.isp, ipAddressInfo.isp) && Intrinsics.areEqual(this.organization, ipAddressInfo.organization) && Intrinsics.areEqual(this.domain, ipAddressInfo.domain) && Intrinsics.areEqual(this.timezone, ipAddressInfo.timezone);
        }

        public int hashCode() {
            return (((((((((((((this.ip.hashCode() * 31) + this.country.hashCode()) * 31) + this.region.hashCode()) * 31) + this.city.hashCode()) * 31) + this.isp.hashCode()) * 31) + this.organization.hashCode()) * 31) + this.domain.hashCode()) * 31) + this.timezone.hashCode();
        }

        public String toString() {
            return "IpAddressInfo(ip=" + this.ip + ", country=" + this.country + ", region=" + this.region + ", city=" + this.city + ", isp=" + this.isp + ", organization=" + this.organization + ", domain=" + this.domain + ", timezone=" + this.timezone + ')';
        }

        public IpAddressInfo(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8) {
            this.ip = str;
            this.country = str2;
            this.region = str3;
            this.city = str4;
            this.isp = str5;
            this.organization = str6;
            this.domain = str7;
            this.timezone = str8;
        }
    }

    @Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\n\b\u0082\b\u0018\u00002\u00020\u0001B!\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\n\u001a\u00020\tHÖ\u0001¢\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\r\u001a\u00020\fHÖ\u0001¢\u0006\u0004\b\r\u0010\u000eJ\u001a\u0010\u0011\u001a\u00020\u00102\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u0011\u0010\u0012R\u0019\u0010\u0003\u001a\u0004\u0018\u00010\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0013\u001a\u0004\b\u0014\u0010\u0015R\u0017\u0010\u0005\u001a\u00020\u00048\u0006¢\u0006\f\n\u0004\b\u0005\u0010\u0016\u001a\u0004\b\u0017\u0010\u0018R\u0017\u0010\u0006\u001a\u00020\u00048\u0006¢\u0006\f\n\u0004\b\u0006\u0010\u0016\u001a\u0004\b\u0019\u0010\u0018¨\u0006\u001a"}, d2 = {"Lcom/exteragram/messenger/proxy/IpAddressInfoController$CacheEntry;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/proxy/IpAddressInfoController$IpAddressInfo;", "info", _UrlKt.FRAGMENT_ENCODE_SET, "time", "ttl", "<init>", "(Lcom/exteragram/messenger/proxy/IpAddressInfoController$IpAddressInfo;JJ)V", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "()Ljava/lang/String;", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", "()I", "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "Lcom/exteragram/messenger/proxy/IpAddressInfoController$IpAddressInfo;", "getInfo", "()Lcom/exteragram/messenger/proxy/IpAddressInfoController$IpAddressInfo;", "J", "getTime", "()J", "getTtl", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class CacheEntry {
        private final IpAddressInfo info;
        private final long time;
        private final long ttl;

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CacheEntry)) {
                return false;
            }
            CacheEntry cacheEntry = (CacheEntry) other;
            return Intrinsics.areEqual(this.info, cacheEntry.info) && this.time == cacheEntry.time && this.ttl == cacheEntry.ttl;
        }

        public int hashCode() {
            IpAddressInfo ipAddressInfo = this.info;
            return ((((ipAddressInfo == null ? 0 : ipAddressInfo.hashCode()) * 31) + Long.hashCode(this.time)) * 31) + Long.hashCode(this.ttl);
        }

        public String toString() {
            return "CacheEntry(info=" + this.info + ", time=" + this.time + ", ttl=" + this.ttl + ')';
        }

        public CacheEntry(IpAddressInfo ipAddressInfo, long j, long j2) {
            this.info = ipAddressInfo;
            this.time = j;
            this.ttl = j2;
        }

        public final IpAddressInfo getInfo() {
            return this.info;
        }

        public final long getTime() {
            return this.time;
        }

        public final long getTtl() {
            return this.ttl;
        }
    }

    @JvmStatic
    public static final String extractIpAddress(String url) {
        String strSubstring = null;
        if (url == null || url.length() == 0) {
            return null;
        }
        try {
            strSubstring = Uri.parse(url).getHost();
            if (strSubstring == null) {
                if (url.contains("://")) {
                    strSubstring = null;
                } else {
                    strSubstring = Uri.parse("http://" + url).getHost();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (strSubstring == null || strSubstring.length() == 0) {
            return null;
        }
        if (strSubstring.startsWith("[") && strSubstring.endsWith("]") && strSubstring.length() > 2) {
            strSubstring = strSubstring.substring(1, strSubstring.length() - 1);
        }
        if (!INSTANCE.isIpAddress(strSubstring)) {
            return null;
        }
        try {
            InetAddress byName = InetAddress.getByName(strSubstring);
            if (!byName.isAnyLocalAddress() && !byName.isLoopbackAddress() && !byName.isLinkLocalAddress() && !byName.isSiteLocalAddress() && !byName.isMulticastAddress()) {
                return byName.getHostAddress();
            }
            return null;
        } catch (Exception e2) {
            FileLog.e(e2);
            return null;
        }
    }

    @JvmStatic
    public static final Call requestIpAddressInfo(final String ipAddress, final Utilities.Callback<IpAddressInfo> callback) {
        HttpUrl.Builder builderNewBuilder;
        HttpUrl.Builder builderAddPathSegment;
        CacheEntry cachedInfo = INSTANCE.getCachedInfo(ipAddress);
        if (cachedInfo != null) {
            callback.run(cachedInfo.getInfo());
            return null;
        }
        HttpUrl httpUrl = HttpUrl.parse("https://ipwho.is");
        HttpUrl httpUrlBuild = (httpUrl == null || (builderNewBuilder = httpUrl.newBuilder()) == null || (builderAddPathSegment = builderNewBuilder.addPathSegment(ipAddress)) == null) ? null : builderAddPathSegment.build();
        if (httpUrlBuild == null) {
            callback.run(null);
            return null;
        }
        Call callNewCall = httpClient.newCall(new Request.Builder().url(httpUrlBuild).build());
        callNewCall.enqueue(new okhttp3.Callback() { 
            @Override // okhttp3.Callback
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    return;
                }
                FileLog.e(e);
                IpAddressInfoController.INSTANCE.putCachedInfo(ipAddress, null);
                callback.run(null);
            }

            @Override // okhttp3.Callback
            public void onResponse(Call call, Response response) {
                IpAddressInfo ipAddressInfo = null;
                String str = ipAddress;
                Utilities.Callback<IpAddressInfo> callback2 = callback;
                try {
                    try {
                        ipAddressInfo = response.isSuccessful() ? IpAddressInfoController.INSTANCE.parseIpAddressInfo(str, response.body().string()) : null;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (!call.isCanceled()) {
                        IpAddressInfoController.INSTANCE.putCachedInfo(str, ipAddressInfo);
                        callback2.run(ipAddressInfo);
                    }
                    Unit unit = Unit.INSTANCE;
                    CloseableKt.closeFinally(response, null);
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        CloseableKt.closeFinally(response, th);
                        throw th2;
                    }
                }
            }
        });
        return callNewCall;
    }

    private final boolean isIpAddress(String host) {
        if (host == null || host.length() == 0) {
            return false;
        }
        if (host.indexOf(':') >= 0) {
            return ipv6CharsRegex.matches(host);
        }
        if (!ipv4Regex.matches(host)) {
            return false;
        }
        List listSplit$default = Arrays.asList(host.split("\\."));
        if ((listSplit$default instanceof Collection) && listSplit$default.isEmpty()) {
            return true;
        }
        Iterator it = listSplit$default.iterator();
        while (it.hasNext()) {
            int iIntValue = Utilities.parseInt((CharSequence) it.next()).intValue();
            if (iIntValue < 0 || iIntValue >= 256) {
                return false;
            }
        }
        return true;
    }

    private final CacheEntry getCachedInfo(String ipAddress) {
        CacheEntry cacheEntry;
        IpAddressInfoController$cache$1 ipAddressInfoController$cache$1 = cache;
        synchronized (ipAddressInfoController$cache$1) {
            cacheEntry = (CacheEntry) ipAddressInfoController$cache$1.get((Object) ipAddress);
            if (cacheEntry == null) {
                cacheEntry = null;
            } else if (SystemClock.elapsedRealtime() - cacheEntry.getTime() > cacheEntry.getTtl()) {
                ipAddressInfoController$cache$1.remove((Object) ipAddress);
                cacheEntry = null;
            }
        }
        return cacheEntry;
    }

    public final void putCachedInfo(String ipAddress, IpAddressInfo info) {
        IpAddressInfoController$cache$1 ipAddressInfoController$cache$1 = cache;
        synchronized (ipAddressInfoController$cache$1) {
            ipAddressInfoController$cache$1.put(ipAddress, new CacheEntry(info, SystemClock.elapsedRealtime(), info != null ? 86400000L : 300000L));
            Unit unit = Unit.INSTANCE;
        }
    }

    public final IpAddressInfo parseIpAddressInfo(String ipAddress, String response) {
        String strOptString;
        String strOptString2;
        String strOptString3;
        String strOptString4;
        try {
            JSONObject jSONObject = new JSONObject(response);
            if (!jSONObject.optBoolean("success", false)) {
                return null;
            }
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("connection");
            JSONObject jSONObjectOptJSONObject2 = jSONObject.optJSONObject("timezone");
            return new IpAddressInfo(jSONObject.optString("ip", ipAddress), jSONObject.optString("country", _UrlKt.FRAGMENT_ENCODE_SET), jSONObject.optString("region", _UrlKt.FRAGMENT_ENCODE_SET), jSONObject.optString("city", _UrlKt.FRAGMENT_ENCODE_SET), (jSONObjectOptJSONObject == null || (strOptString4 = jSONObjectOptJSONObject.optString("isp", _UrlKt.FRAGMENT_ENCODE_SET)) == null) ? _UrlKt.FRAGMENT_ENCODE_SET : strOptString4, (jSONObjectOptJSONObject == null || (strOptString3 = jSONObjectOptJSONObject.optString("org", _UrlKt.FRAGMENT_ENCODE_SET)) == null) ? _UrlKt.FRAGMENT_ENCODE_SET : strOptString3, (jSONObjectOptJSONObject == null || (strOptString2 = jSONObjectOptJSONObject.optString("domain", _UrlKt.FRAGMENT_ENCODE_SET)) == null) ? _UrlKt.FRAGMENT_ENCODE_SET : strOptString2, (jSONObjectOptJSONObject2 == null || (strOptString = jSONObjectOptJSONObject2.optString("id", _UrlKt.FRAGMENT_ENCODE_SET)) == null) ? _UrlKt.FRAGMENT_ENCODE_SET : strOptString);
        } catch (org.json.JSONException e) {
            FileLog.e(e);
            return null;
        }
    }

    @JvmStatic
    public static final String joinInfo(String... values) {
        ArrayList arrayList = new ArrayList();
        int length = values.length;
        for (int i = 0; i < length; i++) {
            String str = values[i];
            if (!(str == null || str.length() == 0)) {
                arrayList.add(str);
            }
        }
        return android.text.TextUtils.join(", ", arrayList);
    }
}
