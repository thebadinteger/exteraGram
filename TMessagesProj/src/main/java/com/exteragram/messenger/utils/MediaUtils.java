package com.exteragram.messenger.utils;

import androidx.exifinterface.media.ExifInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.FileLog;

public abstract class MediaUtils {
    private static final String[] GEO_TAGS = {"GPSLatitude", "GPSLatitudeRef", "GPSLongitude", "GPSLongitudeRef", "GPSAltitude", "GPSAltitudeRef", "GPSTimeStamp", "GPSDateStamp", "GPSProcessingMethod"};
    private static final String[] GEO_TAGS_API_24 = {"GPSAreaInformation", "GPSDOP", "GPSMeasureMode", "GPSSpeedRef", "GPSSpeed", "GPSStatus", "GPSDestLatitude", "GPSDestLatitudeRef", "GPSDestLongitude", "GPSDestLongitudeRef", "GPSDestBearing", "GPSDestBearingRef", "GPSDestDistance", "GPSDestDistanceRef"};
    private static final String[] MOTION_PHOTO_XMP_MARKERS = {"Camera:MotionPhoto", "GCamera:MotionPhoto", "Camera:MicroVideo", "GCamera:MicroVideo", "Camera:MotionPhotoPresentationTimestampUs", "GCamera:MotionPhotoPresentationTimestampUs", "Camera:MicroVideoPresentationTimestampUs", "GCamera:MicroVideoPresentationTimestampUs", "Camera:MicroVideoOffset", "GCamera:MicroVideoOffset", "Container:Directory", "GContainer:Directory"};
    private static final Map<byte[], String> PLATFORM_HEADERS;

    static {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        PLATFORM_HEADERS = linkedHashMap;
        linkedHashMap.put(hexStringToByteArray("FFD8FFE000104A46494600010101006000600000FFDB0043000403030403030404030405040405060A07060606060D090A080A0F0D10100F0D0F0E1113181411"), "Desktop");
        linkedHashMap.put(hexStringToByteArray("FFD8FFE000104A46494600010101004800480000FFE202184943435F50524F46494C4500010100000208"), "Web, K");
        linkedHashMap.put(hexStringToByteArray("FFD8FFE000104A46494600010100000100010000FFE202184943435F50524F46494C450001010000020800000000043000006D6E74725247422058595A2007E0"), "Android");
        linkedHashMap.put(hexStringToByteArray("FFD8FFE000104A46494600010101004800480000FFE201D84943435F50524F46494C45000101000001C800000000043000006D6E74725247422058595A2007E0"), "Android");
        linkedHashMap.put(hexStringToByteArray("FFD8FFE000104A46494600010100000100010000FFE201D84943435F50524F46494C45000101000001C800000000043000006D6E74725247422058595A2007E0"), "Android");
        linkedHashMap.put(hexStringToByteArray("FFD8FFE000104A46494600010100000100010000FFDB004300090607080706090807080A0A090B0D160F0D0C0C0D1B14151016201D2222201D1F1F2428342C24"), "iOS");
        linkedHashMap.put(hexStringToByteArray("FFD8FFE000104A46494600010100000100010000FFDB004300080606070605080707070909080A0C140D0C0B0B0C1912130F141D1A1F1E1D1A1C1C20242E2720"), "macOS");
        linkedHashMap.put(hexStringToByteArray("FFD8FFE000104A46494600010101004800480000FFE201DB4943435F50524F46494C45000101000001CB00000000024000006D6E74725247422058595A200000"), "macOS");
    }

    public static boolean removeGeolocation(String str, String str2) throws Throwable {
        File file = new File(str);
        if (!file.exists()) {
            return false;
        }
        File file2 = new File(str2);
        copyFile(file, file2);
        ArrayList arrayList = new ArrayList(Arrays.asList(GEO_TAGS));
        arrayList.addAll(getApi24GeoTags());
        ExifInterface exifInterface = new ExifInterface(file2.getAbsolutePath());
        int size = arrayList.size();
        boolean z = false;
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            String str3 = (String) obj;
            if (exifInterface.getAttribute(str3) != null) {
                try {
                    exifInterface.setAttribute(str3, null);
                    z = true;
                } catch (Exception unused) {
                }
            }
        }
        if (z) {
            try {
                exifInterface.saveAttributes();
                return true;
            } catch (IOException unused2) {
                file2.delete();
            }
        }
        return false;
    }

    private static List<String> getApi24GeoTags() {
        return Arrays.asList(GEO_TAGS_API_24);
    }

    private static void copyFile(File file, File file2) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                try {
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int i = fileInputStream.read(bArr);
                        if (i > 0) {
                            fileOutputStream.write(bArr, 0, i);
                        } else {
                            fileOutputStream.close();
                            fileInputStream.close();
                            return;
                        }
                        try {
                            fileInputStream.close();
                        } catch (Throwable th) {
                            th.addSuppressed(th);
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                    throw th2;
                }
            } catch (Throwable th4) {
                fileInputStream.close();
                throw th4;
            }
        } catch (IOException e) {
            file2.delete();
            throw e;
        }
    }

    public static String getPhotoPlatform(String str) {
        byte[] fileHeader = readFileHeader(str, 64);
        if (fileHeader == null) {
            return null;
        }
        for (Map.Entry<byte[], String> entry : PLATFORM_HEADERS.entrySet()) {
            byte[] key = entry.getKey();
            if (fileHeader.length >= key.length) {
                for (int i = 0; i < key.length; i++) {
                    if (fileHeader[i] == key[i]) {
                    }
                }
                return entry.getValue();
            }
        }
        return null;
    }

    public static byte[] readFileHeader(String str, int i) {
        File file = new File(str);
        if (!file.exists()) {
            return null;
        }
        byte[] bArr = new byte[i];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                if (fileInputStream.read(bArr) < 1) {
                    fileInputStream.close();
                    return null;
                }
                fileInputStream.close();
                return bArr;
            } catch (Throwable th) {
                try {
                    fileInputStream.close();
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

    private static byte[] hexStringToByteArray(String str) {
        int length = str.length();
        byte[] bArr = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }
}
