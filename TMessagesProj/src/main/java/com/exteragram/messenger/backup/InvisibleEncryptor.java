package com.exteragram.messenger.backup;

import android.text.TextUtils;
import java.nio.charset.StandardCharsets;
import kotlin.UByte;
import okhttp3.internal.url._UrlKt;

public abstract class InvisibleEncryptor {
    private static String toStr(int i) {
        StringBuilder sb = new StringBuilder();
        while (i > 0) {
            sb.insert(0, "\u200a\u200b\u200c\u200f \u206a\u206b\u206c\u206d\u206e\u206f".charAt(i % 11));
            i /= 11;
        }
        return sb.toString();
    }

    private static int toNum(String str) {
        int iIndexOf = 0;
        for (int i = 0; i < str.length(); i++) {
            int length = str.length() - i;
            iIndexOf = (int) (((double) iIndexOf) + (((double) "\u200a\u200b\u200c\u200f \u206a\u206b\u206c\u206d\u206e\u206f".indexOf(str.substring(length - 1, length))) * Math.pow(11.0d, i)));
        }
        return iIndexOf;
    }

    public static String encode(String str) {
        try {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            int length = bytes.length;
            String[] strArr = new String[length];
            for (int i = 0; i < length; i++) {
                strArr[i] = toStr(bytes[i] & UByte.MAX_VALUE);
            }
            return "\u2001\u2002" + android.text.TextUtils.join("\u2000", strArr);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public static String decode(String str) {
        try {
            String[] strArrSplit = str.replaceFirst("^\u2001\u2002", _UrlKt.FRAGMENT_ENCODE_SET).split("\u2000");
            int length = strArrSplit.length;
            byte[] bArr = new byte[length];
            for (int i = 0; i < length; i++) {
                bArr[i] = (byte) toNum(strArrSplit[i]);
            }
            return new String(bArr, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public static boolean isEncrypted(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.matches("^\u2001\u2002([\u200a\u200b\u200c\u200f \u206a\u206b\u206c\u206d\u206e\u206f\\s]*)");
    }
}
