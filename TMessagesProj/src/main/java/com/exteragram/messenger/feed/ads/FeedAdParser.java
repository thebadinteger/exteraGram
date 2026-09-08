package com.exteragram.messenger.feed.ads;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.TLRPC;

abstract class FeedAdParser {
    public static ArrayList<FeedAd> parse(TLRPC.messages_Messages messages_messages) {
        TLRPC.MessageMedia messageMedia;
        String str;
        FeedAd manifest;
        ArrayList<FeedAd> arrayList = new ArrayList<>();
        if (messages_messages != null && messages_messages.messages != null) {
            HashSet hashSet = new HashSet();
            ArrayList<TLRPC.Message> arrayList2 = messages_messages.messages;
            int size = arrayList2.size();
            int i = 0;
            while (i < size) {
                TLRPC.Message message = arrayList2.get(i);
                i++;
                TLRPC.Message message2 = message;
                if ((message2 instanceof TLRPC.TL_message) && (str = message2.message) != null && str.startsWith("feed_ad") && (manifest = parseManifest(message2.message)) != null && hashSet.add(manifest.id)) {
                    arrayList.add(manifest);
                }
            }
            if (!arrayList.isEmpty()) {
                ArrayList<TLRPC.Message> arrayList3 = messages_messages.messages;
                int size2 = arrayList3.size();
                int i2 = 0;
                while (i2 < size2) {
                    TLRPC.Message message3 = arrayList3.get(i2);
                    i2++;
                    TLRPC.Message message4 = message3;
                    if (message4 instanceof TLRPC.TL_message) {
                        for (int i3 = 0; i3 < arrayList.size(); i3++) {
                            FeedAd feedAd = arrayList.get(i3);
                            int i4 = feedAd.bodyMessageId;
                            if (i4 != 0 && message4.id == i4) {
                                feedAd.bodyText = message4.message;
                                ArrayList<TLRPC.MessageEntity> arrayList4 = message4.entities;
                                feedAd.entities = (arrayList4 == null || arrayList4.isEmpty()) ? null : new ArrayList<>(message4.entities);
                            }
                            int i5 = feedAd.mediaMessageId;
                            if (i5 != 0 && message4.id == i5 && (messageMedia = message4.media) != null) {
                                feedAd.media = messageMedia;
                            }
                        }
                    }
                }
                ArrayList<FeedAd> arrayList5 = new ArrayList<>(arrayList.size());
                for (int i6 = 0; i6 < arrayList.size(); i6++) {
                    if (arrayList.get(i6).isDisplayable()) {
                        arrayList5.add(arrayList.get(i6));
                    }
                }
                return arrayList5;
            }
        }
        return arrayList;
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private static FeedAd parseManifest(String str) {
        try {
            FeedAd feedAd = new FeedAd();
            for (String str2 : str.split("\n")) {
                int iIndexOf = str2.indexOf(61);
                if (iIndexOf >= 0) {
                    String strTrim = str2.substring(0, iIndexOf).trim();
                    String strTrim2 = str2.substring(iIndexOf + 1).trim();
                    if (!strTrim2.isEmpty()) {
                        switch (strTrim.hashCode()) {
                            case -1677022541:
                                if (strTrim.equals("sponsor_info")) {
                                    feedAd.sponsorInfo = strTrim2;
                                }
                                break;
                            case -1377687758:
                                if (strTrim.equals("button")) {
                                    feedAd.buttonText = strTrim2;
                                }
                                break;
                            case -1097462182:
                                if (strTrim.equals("locale")) {
                                    feedAd.locales = parseLocales(strTrim2);
                                }
                                break;
                            case -791592328:
                                if (strTrim.equals("weight")) {
                                    feedAd.weight = Math.max(1, parseInt(strTrim2, 1));
                                }
                                break;
                            case -318452137:
                                if (strTrim.equals("premium")) {
                                    feedAd.premium = parseMatch(strTrim2);
                                }
                                break;
                            case -118282810:
                                if (strTrim.equals("additional_info")) {
                                    feedAd.additionalInfo = strTrim2;
                                }
                                break;
                            case 3355:
                                if (strTrim.equals("id")) {
                                    feedAd.id = strTrim2;
                                }
                                break;
                            case 116079:
                                if (strTrim.equals("url")) {
                                    feedAd.url = strTrim2;
                                }
                                break;
                            case 3029410:
                                if (strTrim.equals("body")) {
                                    feedAd.bodyMessageId = parseInt(strTrim2, 0);
                                }
                                break;
                            case 93494179:
                                if (strTrim.equals("badge")) {
                                    feedAd.badge = parseMatch(strTrim2);
                                }
                                break;
                            case 94842723:
                                if (strTrim.equals("color")) {
                                    feedAd.colorId = parseInt(strTrim2, -1);
                                }
                                break;
                            case 103772132:
                                if (strTrim.equals("media")) {
                                    feedAd.mediaMessageId = parseInt(strTrim2, 0);
                                }
                                break;
                            case 110371416:
                                if (strTrim.equals("title")) {
                                    feedAd.title = strTrim2;
                                }
                                break;
                            case 1437916763:
                                if (strTrim.equals("recommended")) {
                                    feedAd.recommended = Boolean.parseBoolean(strTrim2);
                                }
                                break;
                        }
                    }
                }
            }
            String str3 = feedAd.id;
            if (str3 == null || str3.isEmpty()) {
                return null;
            }
            return feedAd;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private static Set<String> parseLocales(String str) {
        HashSet hashSet = new HashSet();
        for (String str2 : str.split(",")) {
            String lowerCase = str2.trim().toLowerCase();
            if (!lowerCase.isEmpty()) {
                hashSet.add(lowerCase);
            }
        }
        if (hashSet.isEmpty()) {
            return null;
        }
        return hashSet;
    }

    private static int parseMatch(String str) {
        if (TextUtils.equals(str, "true") || TextUtils.equals(str, "has")) {
            return 1;
        }
        return (TextUtils.equals(str, "false") || TextUtils.equals(str, "none")) ? 2 : 0;
    }

    private static int parseInt(String str, int i) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return i;
        }
    }
}
