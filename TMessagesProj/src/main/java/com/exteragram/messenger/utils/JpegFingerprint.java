package com.exteragram.messenger.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import kotlin.UByte;
import kotlin.text.Typography;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.FileLog;

/* JADX INFO: loaded from: classes4.dex */
public class JpegFingerprint {
    public int components;
    public boolean exif;
    public boolean frameBeforeQuantization;
    public int height;
    public int horizontalSampling;
    public int huffmanTables;
    public String iccDescription;
    public int iccLength;
    public int iccVersion;
    public int iccYear;
    public int jfifDensityX;
    public int jfifDensityY;
    public boolean jfifThumbnail;
    public boolean progressive;
    public boolean quantizationTable;
    public boolean restartInterval;
    public int verticalSampling;
    public int width;
    public boolean xmp;
    private static final int[] ZIGZAG = {0, 1, 8, 16, 9, 2, 3, 10, 17, 24, 32, 25, 18, 11, 4, 5, 12, 19, 26, 33, 40, 48, 41, 34, 27, 20, 13, 6, 7, 14, 21, 28, 35, 42, 49, 56, 57, 50, 43, 36, 29, 22, 15, 23, 30, 37, 44, 51, 58, 59, 52, 45, 38, 31, 39, 46, 53, 60, 61, 54, 47, 55, 62, 63};
    private static final int[] STANDARD_LUMA = {16, 11, 10, 16, 24, 40, 51, 61, 12, 12, 14, 19, 26, 58, 60, 55, 14, 13, 16, 24, 40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 80, 62, 18, 22, 37, 56, 68, 109, 103, 77, 24, 35, 55, 64, 81, 104, 113, 92, 49, 64, 78, 87, 103, 121, 120, 101, 72, 92, 95, 98, 112, 100, 103, 99};
    public int jfifVersion = -1;
    public int jfifUnits = -1;
    public int quality = -1;
    public String markerOrder = _UrlKt.FRAGMENT_ENCODE_SET;

    public static JpegFingerprint parse(String str) {
        int i;
        int i2;
        long j;
        File file = new File(str);
        if (!file.exists()) {
            return null;
        }
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file), 16384);
            try {
                if (bufferedInputStream.read() == 255 && bufferedInputStream.read() == 216) {
                    JpegFingerprint jpegFingerprint = new JpegFingerprint();
                    StringBuilder sb = new StringBuilder();
                    int i3 = 2;
                    while (i3 < 262144 && bufferedInputStream.read() == 255) {
                        int i4 = bufferedInputStream.read();
                        while (i4 == 255) {
                            i4 = bufferedInputStream.read();
                            i3++;
                        }
                        i3 += 2;
                        if (i4 >= 0 && i4 != 217 && i4 != 218) {
                            if (i4 != 1 && (i4 < 208 || i4 > 215)) {
                                int i5 = bufferedInputStream.read();
                                int i6 = bufferedInputStream.read();
                                if (i5 < 0 || i6 < 0 || (i2 = (i = (i5 << 8) | i6) - 2) < 0) {
                                    break;
                                }
                                i3 += i;
                                if (sb.length() > 0) {
                                    sb.append(Typography.greater);
                                }
                                sb.append(String.format(Locale.US, "%02X", Integer.valueOf(i4)));
                                if (i4 != 224 && i4 != 225 && i4 != 226 && i4 != 219 && i4 != 196 && i4 != 221 && (i4 < 192 || i4 > 194)) {
                                    long j2 = 0;
                                    while (true) {
                                        j = i2;
                                        if (j2 >= j) {
                                            break;
                                        }
                                        long jSkip = bufferedInputStream.skip(j - j2);
                                        if (jSkip <= 0) {
                                            break;
                                        }
                                        j2 += jSkip;
                                    }
                                    if (j2 < j) {
                                        break;
                                    }
                                }
                                byte[] bArr = new byte[i2];
                                int i7 = 0;
                                while (i7 < i2) {
                                    int i8 = bufferedInputStream.read(bArr, i7, i2 - i7);
                                    if (i8 < 0) {
                                        break;
                                    }
                                    i7 += i8;
                                }
                                if (i7 < i2) {
                                    break;
                                }
                                jpegFingerprint.readSegment(i4, bArr);
                            }
                        } else {
                            break;
                        }
                    }
                    jpegFingerprint.markerOrder = sb.toString();
                    bufferedInputStream.close();
                    return jpegFingerprint;
                }
                bufferedInputStream.close();
                return null;
            } catch (Throwable th) {
                try {
                    bufferedInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException e) {
            FileLog.e(e);
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void readSegment(int i, byte[] bArr) {
        int i2 = 0;
        boolean z = false;
        if (i == 224) {
            if (bArr.length < 14 || !startsWith(bArr, "JFIF")) {
                return;
            }
            this.jfifVersion = ((bArr[5] & UByte.MAX_VALUE) << 8) | (bArr[6] & UByte.MAX_VALUE);
            this.jfifUnits = bArr[7] & UByte.MAX_VALUE;
            this.jfifDensityX = ((bArr[8] & UByte.MAX_VALUE) << 8) | (bArr[9] & UByte.MAX_VALUE);
            this.jfifDensityY = ((bArr[10] & UByte.MAX_VALUE) << 8) | (bArr[11] & UByte.MAX_VALUE);
            this.jfifThumbnail = (bArr[12] == 0 && bArr[13] == 0) ? false : true;
            return;
        }
        if (i == 225) {
            this.exif = this.exif || startsWith(bArr, "Exif");
            this.xmp = this.xmp || startsWith(bArr, "http://ns.adobe.com/xap/1.0/");
            return;
        }
        if (i == 226) {
            if (this.iccLength == 0 && bArr.length >= 50 && startsWith(bArr, "ICC_PROFILE")) {
                this.iccLength = ((bArr[14] & UByte.MAX_VALUE) << 24) | ((bArr[15] & UByte.MAX_VALUE) << 16) | ((bArr[16] & UByte.MAX_VALUE) << 8) | (bArr[17] & UByte.MAX_VALUE);
                this.iccVersion = ((bArr[22] & UByte.MAX_VALUE) << 8) | (bArr[23] & UByte.MAX_VALUE);
                this.iccYear = ((bArr[38] & UByte.MAX_VALUE) << 8) | (bArr[39] & UByte.MAX_VALUE);
                this.iccDescription = readIccDescription(bArr, 14);
                return;
            }
            return;
        }
        if (i == 219) {
            while (i2 < bArr.length) {
                byte b2 = bArr[i2];
                int i3 = (b2 & 240) >> 4;
                int z2 = b2 & 15;
                int i4 = i2 + 1;
                int i5 = (i3 == 0 ? 64 : 128) + i4;
                if (i5 > bArr.length) {
                    return;
                }
                if (z2 == 0 && i3 == 0 && !this.quantizationTable) {
                    this.quantizationTable = true;
                    this.quality = standardQuality(bArr, i4);
                }
                i2 = i5;
            }
            return;
        }
        if (i == 196) {
            int i6 = 0;
            while (i6 + 17 <= bArr.length) {
                int i7 = 0;
                for (int i8 = 0; i8 < 16; i8++) {
                    i7 += bArr[i6 + 1 + i8] & UByte.MAX_VALUE;
                }
                this.huffmanTables++;
                i6 += i7 + 17;
            }
            return;
        }
        if (i == 221) {
            if (bArr.length >= 2 && (((bArr[0] & UByte.MAX_VALUE) << 8) | (bArr[1] & UByte.MAX_VALUE)) > 0) {
                z = true;
            }
            this.restartInterval = z;
            return;
        }
        if (i < 192 || i > 194 || bArr.length < 9) {
            return;
        }
        this.progressive = i == 194;
        this.frameBeforeQuantization = !this.quantizationTable;
        this.height = ((bArr[1] & UByte.MAX_VALUE) << 8) | (bArr[2] & UByte.MAX_VALUE);
        this.width = ((bArr[3] & UByte.MAX_VALUE) << 8) | (bArr[4] & UByte.MAX_VALUE);
        this.components = bArr[5] & UByte.MAX_VALUE;
        byte b3 = bArr[7];
        this.horizontalSampling = (b3 & 240) >> 4;
        this.verticalSampling = b3 & 15;
    }

    private static int readInt(byte[] bArr, int i) {
        return (bArr[i + 3] & UByte.MAX_VALUE) | ((bArr[i] & UByte.MAX_VALUE) << 24) | ((bArr[i + 1] & UByte.MAX_VALUE) << 16) | ((bArr[i + 2] & UByte.MAX_VALUE) << 8);
    }

    private static String readIccDescription(byte[] bArr, int i) {
        int i2;
        try {
            int i3 = readInt(bArr, i + 128);
            for (int i4 = 0; i4 < Math.min(i3, 64); i4++) {
                int i5 = i + 132 + (i4 * 12);
                if (i5 + 12 > bArr.length) {
                    break;
                }
                if (bArr[i5] == 100 && bArr[i5 + 1] == 101 && bArr[i5 + 2] == 115 && bArr[i5 + 3] == 99) {
                    int i6 = readInt(bArr, i5 + 4) + i;
                    int i7 = readInt(bArr, i5 + 8);
                    if (i6 < i || i7 < 12 || (i2 = i6 + i7) > bArr.length) {
                        break;
                    }
                    byte b2 = bArr[i6];
                    if (b2 != 100) {
                        if (b2 != 109) {
                            break;
                        }
                        int i8 = readInt(bArr, i6 + 20);
                        int i9 = readInt(bArr, i6 + 24) + i6;
                        if (i8 <= 0 || i9 < i6 || i9 + i8 > i2) {
                            break;
                        }
                        return new String(bArr, i9, i8, StandardCharsets.UTF_16BE);
                    }
                    int iMin = Math.min(readInt(bArr, i6 + 8) - 1, i7 - 12);
                    if (iMin > 0) {
                        return new String(bArr, i6 + 12, iMin, StandardCharsets.US_ASCII);
                    }
                    return null;
                }
            }
        } catch (Exception unused) {
        }
        return null;
    }

    private static int standardQuality(byte[] bArr, int i) {
        int i2 = 100;
        while (i2 >= 1) {
            int i3 = i2 < 50 ? 5000 / i2 : 200 - (i2 * 2);
            for (int i4 = 0; i4 < 64; i4++) {
                int i5 = ((STANDARD_LUMA[ZIGZAG[i4]] * i3) + 50) / 100;
                if (i5 < 1) {
                    i5 = 1;
                } else if (i5 > 255) {
                    i5 = 255;
                }
                if (i5 != (255 & bArr[i + i4])) {
                    i2--;
                }
            }
            return i2;
        }
        return -1;
    }

    private static boolean startsWith(byte[] bArr, String str) {
        if (bArr.length <= str.length()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (bArr[i] != ((byte) str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public String describe() {
        String str;
        StringBuilder sb = new StringBuilder();
        if (this.jfifUnits >= 0) {
            sb.append(String.format(Locale.US, "jfif=%d.%02d/u%d/%dx%d", Integer.valueOf(this.jfifVersion >> 8), Integer.valueOf(this.jfifVersion & 255), Integer.valueOf(this.jfifUnits), Integer.valueOf(this.jfifDensityX), Integer.valueOf(this.jfifDensityY)));
        } else {
            sb.append("jfif=none");
        }
        if (this.jfifThumbnail) {
            sb.append("/thumb");
        }
        int i = this.iccLength;
        if (i > 0) {
            Locale locale = Locale.US;
            Integer numValueOf = Integer.valueOf(i);
            Integer numValueOf2 = Integer.valueOf(this.iccVersion);
            Integer numValueOf3 = Integer.valueOf(this.iccYear);
            String str2 = this.iccDescription;
            if (str2 == null) {
                str2 = "?";
            }
            sb.append(String.format(locale, " icc=%d/%04x/%d/%s", numValueOf, numValueOf2, numValueOf3, str2));
        } else {
            sb.append(" icc=none");
        }
        int i2 = this.quality;
        if (i2 > 0) {
            str = String.format(Locale.US, " dqt=q%d", Integer.valueOf(i2));
        } else {
            str = this.quantizationTable ? " dqt=custom" : " dqt=none";
        }
        sb.append(str);
        Locale locale2 = Locale.US;
        sb.append(String.format(locale2, " sof=%s/%dc/%dx%d/%dx%d", this.progressive ? "prog" : "base", Integer.valueOf(this.components), Integer.valueOf(this.horizontalSampling), Integer.valueOf(this.verticalSampling), Integer.valueOf(this.width), Integer.valueOf(this.height)));
        sb.append(String.format(locale2, " dht=%d", Integer.valueOf(this.huffmanTables)));
        if (this.exif) {
            sb.append(" exif");
        }
        if (this.xmp) {
            sb.append(" xmp");
        }
        if (this.restartInterval) {
            sb.append(" dri");
        }
        sb.append(" order=");
        sb.append(this.markerOrder);
        return sb.toString();
    }
}
