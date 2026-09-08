package com.exteragram.messenger.utils.text;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;

public abstract class ZalgoFilter {
    private static boolean isDirectionControl(int i) {
        if (i == 1564 || i == 8206 || i == 8207) {
            return true;
        }
        if (i < 8234 || i > 8238) {
            return i >= 8294 && i <= 8297;
        }
        return true;
    }

    private static boolean isZalgoMarkRange(int i) {
        if (i >= 768 && i <= 879) {
            return true;
        }
        if (i >= 6832 && i <= 6911) {
            return true;
        }
        if (i >= 7616 && i <= 7679) {
            return true;
        }
        if (i < 8400 || i > 8447) {
            return i >= 65056 && i <= 65071;
        }
        return true;
    }

    public static boolean canFilter(CharSequence charSequence) {
        return (!ExteraConfig.getFilterZalgo() || TextUtils.isEmpty(charSequence) || findReplacementRanges(charSequence) == null) ? false : true;
    }

    public static CharSequence filterSpannable(CharSequence charSequence) {
        if (!canFilter(charSequence)) {
            return charSequence;
        }
        if (!(charSequence instanceof Spannable)) {
            return filter(charSequence);
        }
        Spannable spannable = (Spannable) charSequence;
        SpannableString spannableString = new SpannableString(filterEnabled(spannable.toString()));
        for (Object obj : spannable.getSpans(0, spannable.length(), Object.class)) {
            int iMin = Math.min(spannable.getSpanStart(obj), spannableString.length());
            int iMin2 = Math.min(spannable.getSpanEnd(obj), spannableString.length());
            int spanFlags = spannable.getSpanFlags(obj);
            if (iMin < 0) {
                iMin = 0;
            }
            if (iMin2 > spannableString.length()) {
                iMin2 = spannableString.length();
            }
            if (iMin > iMin2) {
                iMin = iMin2;
            }
            spannableString.setSpan(obj, iMin, iMin2, spanFlags);
        }
        return spannableString;
    }

    public static String filter(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        return filter(charSequence.toString());
    }

    public static String filter(String str) {
        return (!ExteraConfig.getFilterZalgo() || TextUtils.isEmpty(str)) ? str : filterEnabled(str);
    }

    private static String filterEnabled(String str) {
        ArrayList<int[]> arrayListFindReplacementRanges = findReplacementRanges(str);
        if (arrayListFindReplacementRanges == null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        int size = arrayListFindReplacementRanges.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            int[] iArr = arrayListFindReplacementRanges.get(i2);
            i2++;
            int[] iArr2 = iArr;
            sb.append((CharSequence) str, i, iArr2[0]);
            appendReplacement(sb, iArr2[1] - iArr2[0]);
            i = iArr2[1];
        }
        sb.append((CharSequence) str, i, str.length());
        return sb.toString();
    }

    private static ArrayList<int[]> findReplacementRanges(CharSequence charSequence) {
        int length = charSequence.length();
        ArrayList<int[]> arrayListAddMarkSequenceRange = null;
        int i = -1;
        int i2 = 0;
        int i3 = 0;
        int i4 = 4;
        while (i2 < length) {
            int iCodePointAt = Character.codePointAt(charSequence, i2);
            int iCharCount = Character.charCount(iCodePointAt);
            int allowedMarksPerSequence = getAllowedMarksPerSequence(iCodePointAt);
            if (allowedMarksPerSequence > 0) {
                if (i < 0) {
                    i3 = 0;
                    i = i2;
                } else {
                    allowedMarksPerSequence = Math.min(i4, allowedMarksPerSequence);
                }
                i3++;
                i4 = allowedMarksPerSequence;
            } else {
                arrayListAddMarkSequenceRange = addMarkSequenceRange(arrayListAddMarkSequenceRange, i, i2, i3, i4);
                if (isDirectionControl(iCodePointAt)) {
                    arrayListAddMarkSequenceRange = addRange(arrayListAddMarkSequenceRange, i2, i2 + iCharCount);
                }
                i = -1;
                i3 = 0;
                i4 = 4;
            }
            i2 += iCharCount;
        }
        return addMarkSequenceRange(arrayListAddMarkSequenceRange, i, length, i3, i4);
    }

    private static ArrayList<int[]> addMarkSequenceRange(ArrayList<int[]> arrayList, int i, int i2, int i3, int i4) {
        return (i < 0 || i3 <= i4) ? arrayList : addRange(arrayList, i, i2);
    }

    private static ArrayList<int[]> addRange(ArrayList<int[]> arrayList, int i, int i2) {
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        arrayList.add(new int[]{i, i2});
        return arrayList;
    }

    private static void appendReplacement(StringBuilder sb, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            sb.append((char) 8288);
        }
    }

    private static int getAllowedMarksPerSequence(int i) {
        if (i < 768) {
            return 0;
        }
        int type = Character.getType(i);
        if (type == 6 || type == 7) {
            return isZalgoMarkRange(i) ? 2 : 4;
        }
        return 0;
    }
}
