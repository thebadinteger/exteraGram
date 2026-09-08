package com.exteragram.messenger.regdate;

import androidx.collection.LruCache;
import com.exteragram.messenger.api.db.DatabaseHelper;
import com.exteragram.messenger.api.dto.RegDateDTO;
import com.exteragram.messenger.api.model.RegDateFlag;
import com.exteragram.messenger.backup.InvisibleEncryptor;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jna.Callback;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;

@Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0004\u0010\u0005J\u0017\u0010\b\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0002¢\u0006\u0004\b\b\u0010\tJ%\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0007\u001a\u00020\u00062\u000e\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\n¢\u0006\u0004\b\u000e\u0010\u000fJ-\u0010\u0012\u001a\u00020\r2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u00062\u000e\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\n¢\u0006\u0004\b\u0012\u0010\u0013J\u001f\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0007\u001a\u00020\u00062\b\u0010\u0014\u001a\u0004\u0018\u00010\u000b¢\u0006\u0004\b\u0015\u0010\u0016J\u0015\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\u0017\u0010\u0018R\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0019\u001a\u0004\b\u001a\u0010\u001bR \u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u000b0\u001c8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u001d\u0010\u001e¨\u0006 "}, d2 = {"Lcom/exteragram/messenger/regdate/RegDateController;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "currentAccount", "<init>", "(I)V", _UrlKt.FRAGMENT_ENCODE_SET, "userId", "findUserRegistrationDate", "(J)J", "Ljava/util/function/Consumer;", "Lcom/exteragram/messenger/api/dto/RegDateDTO;", Callback.METHOD_NAME, _UrlKt.FRAGMENT_ENCODE_SET, "fetchRegistrationDate", "(JLjava/util/function/Consumer;)V", "timestamp", _UrlKt.FRAGMENT_ENCODE_SET, "addRegistrationDate", "(JJLjava/util/function/Consumer;)V", "dto", "formatRegistrationDate", "(JLcom/exteragram/messenger/api/dto/RegDateDTO;)Ljava/lang/String;", "getUserRegistrationDate", "(J)Ljava/lang/String;", "I", "getCurrentAccount", "()I", "Landroidx/collection/LruCache;", "regDateCache", "Landroidx/collection/LruCache;", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class RegDateController {

    public static final Companion INSTANCE = new Companion(null);
    private static final RegDateController[] Instance = new RegDateController[16];
    private static final Object[] lockObjects;
    private static Long[] regDates;
    private static Long[] regIds;
    private final int currentAccount;
    private final LruCache<Long, RegDateDTO> regDateCache = new LruCache<>(256);

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    public static final Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final RegDateController getInstance(int num) {
            RegDateController regDateController;
            RegDateController regDateController2 = RegDateController.Instance[num];
            if (regDateController2 != null) {
                return regDateController2;
            }
            synchronized (RegDateController.lockObjects[num]) {
                try {
                    regDateController = RegDateController.Instance[num];
                    if (regDateController == null) {
                        regDateController = new RegDateController(num);
                        RegDateController.Instance[num] = regDateController;
                    }
                    Unit unit = Unit.INSTANCE;
                } catch (Throwable th) {
                    throw th;
                }
            }
            return regDateController;
        }

        public final void initializeRegIds() {
            if (RegDateController.regIds != null) {
                return;
            }
            try {
                InputStream inputStreamOpen = ApplicationLoader.applicationContext.getAssets().open("extera/registration_dates.bin");
                byte[] bArr = new byte[inputStreamOpen.available()];
                inputStreamOpen.read(bArr);
                inputStreamOpen.close();
                Set<Map.Entry<String, JsonElement>> setEntrySet = ((JsonObject) new Gson().fromJson(InvisibleEncryptor.decode(new String(bArr, StandardCharsets.UTF_8)), JsonObject.class)).entrySet();
                Long[] lArr = new Long[setEntrySet.size()];
                Long[] lArr2 = new Long[setEntrySet.size()];
                int i = 0;
                for (Map.Entry<String, JsonElement> entry : setEntrySet) {
                    lArr[i] = Long.valueOf(Long.parseLong(entry.getKey()));
                    lArr2[i] = Long.valueOf(entry.getValue().getAsLong());
                    i++;
                }
                RegDateController.regIds = (Long[]) ArraysKt.requireNoNulls(lArr);
                RegDateController.regDates = (Long[]) ArraysKt.requireNoNulls(lArr2);
            } catch (IOException e) {
                FileLog.e(e);
            }
        }
    }

    static {
        Object[] objArr = new Object[16];
        for (int i = 0; i < 16; i++) {
            objArr[i] = new Object();
        }
        lockObjects = objArr;
    }
}
