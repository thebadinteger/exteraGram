package com.exteragram.messenger.plugins.utils;

import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.CharsKt;
import kotlin.text.Charsets;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.FileLog;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J.\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u00052\b\u0010\f\u001a\u0004\u0018\u00010\u00052\b\u0010\r\u001a\u0004\u0018\u00010\u0005H\u0007J\u0010\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\nH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/exteragram/messenger/plugins/utils/SdkSignature;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "PUBLIC_KEY", _UrlKt.FRAGMENT_ENCODE_SET, "PAYLOAD_PREFIX", "verify", _UrlKt.FRAGMENT_ENCODE_SET, "archive", "Ljava/io/File;", "version", "abi", "signature", "digest", "file", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nSdkSignature.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SdkSignature.kt\ncom/exteragram/messenger/plugins/utils/SdkSignature\n+ 2 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,79:1\n492#2,12:80\n*S KotlinDebug\n*F\n+ 1 SdkSignature.kt\ncom/exteragram/messenger/plugins/utils/SdkSignature\n*L\n48#1:80,12\n*E\n"})
public final class SdkSignature {
    public static final SdkSignature INSTANCE = new SdkSignature();
    private static final String PAYLOAD_PREFIX = "extera-pysdk";
    private static final String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAER7oj20N/vwmRC/Zig0lX/xOQvh6kSkPOhuoZlIm7IYHyY0qo3YqMGVfGqjjqX4ZcsGiDy91ELUEElv3bCSfHTg==";

    private SdkSignature() {
    }

    @JvmStatic
    public static final boolean verify(File archive, String version, String abi, String signature) {
        if (version == null || version.length() == 0 || abi == null || abi.length() == 0 || signature == null || signature.length() == 0) {
            FileLog.e("Python SDK update is not signed");
            return false;
        }
        if (!archive.exists()) {
            FileLog.e("Python SDK archive to verify does not exist");
            return false;
        }
        try {
            String str = "extera-pysdk|" + version + "|" + abi + "|" + INSTANCE.digest(archive);
            PublicKey publicKeyGeneratePublic = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.decode(PUBLIC_KEY, 0)));
            Signature signature2 = Signature.getInstance("SHA256withECDSA");
            signature2.initVerify(publicKeyGeneratePublic);
            signature2.update(str.getBytes(Charsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < signature.length(); i++) {
                char cCharAt = signature.charAt(i);
                if (!CharsKt.isWhitespace(cCharAt)) {
                    sb.append(cCharAt);
                }
            }
            boolean zVerify = signature2.verify(Base64.decode(sb.toString(), 0));
            if (!zVerify) {
                FileLog.e("Python SDK signature does not match (" + version + ", " + abi + ")");
            }
            return zVerify;
        } catch (Throwable th) {
            FileLog.e("Failed to verify Python SDK signature", th);
            return false;
        }
    }

    private final String digest(File file) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bArr = new byte[1048576];
            while (true) {
                int i2 = fileInputStream.read(bArr);
                if (i2 <= 0) {
                    break;
                }
                messageDigest.update(bArr, 0, i2);
            }
        }
        byte[] bArrDigest = messageDigest.digest();
        StringBuilder sb = new StringBuilder(bArrDigest.length * 2);
        for (byte b2 : bArrDigest) {
            sb.append(String.format("%02x", Byte.valueOf(b2)));
        }
        return sb.toString();
    }
}
