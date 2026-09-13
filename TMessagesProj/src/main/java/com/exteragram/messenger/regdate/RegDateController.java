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

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final RegDateController[] Instance = new RegDateController[16];
    private static final Object[] lockObjects;
    private static Long[] regDates;
    private static Long[] regIds;
    private final int currentAccount;
    private final LruCache<Long, RegDateDTO> regDateCache = new LruCache<>(256);

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    public static final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[RegDateFlag.values().length];
            try {
                iArr[RegDateFlag.LT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[RegDateFlag.ET.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    @JvmStatic
    public static final RegDateController getInstance(int i) {
        return INSTANCE.getInstance(i);
    }

    public RegDateController(int i) {
        this.currentAccount = i;
    }

    public final void fetchRegistrationDate(final long userId, final Consumer<RegDateDTO> callback) {
        if (!BadgesController.hasBadge$default(BadgesController.INSTANCE, null, 1, null)) {
            callback.accept(null);
            return;
        }
        RegDateDTO regDateDTO = this.regDateCache.get(Long.valueOf(userId));
        if (regDateDTO != null) {
            callback.accept(regDateDTO);
            return;
        }
        ChatUtils.getInstance(this.currentAccount).sendBotRequest("regdate " + userId, false, new Utilities.Callback() { // from class: com.exteragram.messenger.regdate.RegDateController$$ExternalSyntheticLambda1
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                final String str = (String) obj;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.regdate.RegDateController$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        RegDateController.fetchRegistrationDate$lambda$1$0(str, RegDateController.this, userId, callback);
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void fetchRegistrationDate$lambda$1$0(String str, RegDateController regDateController, long j, Consumer consumer) {
        if (str != null && !str.startsWith("failed")) {
            try {
                RegDateDTO regDateDTO = (RegDateDTO) new Gson().fromJson(str, RegDateDTO.class);
                regDateController.regDateCache.put(Long.valueOf(j), regDateDTO);
                consumer.accept(regDateDTO);
                return;
            } catch (Exception e) {
                FileLog.e(e);
                consumer.accept(null);
                return;
            }
        }
        consumer.accept(null);
    }

    public final void addRegistrationDate(final long userId, final long timestamp, final Consumer<String> callback) {
        if (!BadgesController.isDeveloper$default(BadgesController.INSTANCE, null, 1, null)) {
            callback.accept("прости, но нет");
        } else {
            DatabaseHelper.isRegDateAdded(userId, new Consumer() { // from class: com.exteragram.messenger.regdate.RegDateController$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    final Boolean bool = (Boolean) obj;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.regdate.RegDateController$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            RegDateController.addRegistrationDate$lambda$0$0(bool, callback, userId, timestamp, RegDateController.this);
                        }
                    });
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void addRegistrationDate$lambda$0$0(Boolean bool, final Consumer consumer, final long j, long j2, RegDateController regDateController) {
        if (bool.booleanValue()) {
            consumer.accept("ok");
            return;
        }
        ChatUtils.getInstance(regDateController.currentAccount).sendBotRequest("addregdate " + j + ' ' + j2, false, new Utilities.Callback() { // from class: com.exteragram.messenger.regdate.RegDateController$$ExternalSyntheticLambda3
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                RegDateController.addRegistrationDate$lambda$0$0$0(j, consumer, (String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void addRegistrationDate$lambda$0$0$0(long j, Consumer consumer, String str) {
        if (Intrinsics.areEqual(str, "ok")) {
            DatabaseHelper.setRegDateAdded(j);
        }
        if (str == null) {
            str = "no results";
        }
        consumer.accept(str);
    }

    public final String formatRegistrationDate(long userId, RegDateDTO dto) {
        if (dto == null) {
            return getUserRegistrationDate(userId);
        }
        String yearMont = LocaleController.formatYearMont(dto.getTimestamp(), true);
        String name = ContactsController.formatName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(userId)));
        RegDateFlag flag = dto.getFlag();
        if (userId == UserConfig.getInstance(this.currentAccount).clientUserId) {
            int i = WhenMappings.$EnumSwitchMapping$0[flag.ordinal()];
            if (i == 1) {
                return LocaleController.formatString(R.string.CreationDateSelfEarlier, yearMont);
            }
            if (i == 2) {
                return LocaleController.formatString(R.string.CreationDateSelfLater, yearMont);
            }
            return LocaleController.formatString(R.string.CreationDateSelfApproximately, yearMont);
        }
        int i2 = WhenMappings.$EnumSwitchMapping$0[flag.ordinal()];
        if (i2 == 1) {
            return LocaleController.formatString(R.string.CreationDateUserEarlier, name, yearMont);
        }
        if (i2 == 2) {
            return LocaleController.formatString(R.string.CreationDateUserLater, name, yearMont);
        }
        return LocaleController.formatString(R.string.CreationDateUserApproximately, name, yearMont);
    }

    public final String getUserRegistrationDate(long userId) {
        long jFindUserRegistrationDate = findUserRegistrationDate(userId);
        String dateChat = LocaleController.formatDateChat(jFindUserRegistrationDate);
        String name = ContactsController.formatName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(userId)));
        if (userId == UserConfig.getInstance(this.currentAccount).clientUserId) {
            if (Long.valueOf(jFindUserRegistrationDate).equals(regDates[0])) {
                return LocaleController.formatString(R.string.CreationDateSelfEarlier, dateChat);
            }
            Long lValueOf = Long.valueOf(jFindUserRegistrationDate);
            Long[] lArr = regDates;
            if (lValueOf.equals(lArr[lArr.length - 1])) {
                return LocaleController.formatString(R.string.CreationDateSelfLater, dateChat);
            }
            return LocaleController.formatString(R.string.CreationDateSelfApproximately, dateChat);
        }
        if (Long.valueOf(jFindUserRegistrationDate).equals(regDates[0])) {
            return LocaleController.formatString(R.string.CreationDateUserEarlier, name, dateChat);
        }
        Long lValueOf2 = Long.valueOf(jFindUserRegistrationDate);
        Long[] lArr2 = regDates;
        if (lValueOf2.equals(lArr2[lArr2.length - 1])) {
            return LocaleController.formatString(R.string.CreationDateUserLater, name, dateChat);
        }
        return LocaleController.formatString(R.string.CreationDateUserApproximately, name, dateChat);
    }

    private final long findUserRegistrationDate(long userId) {
        long jLongValue;
        INSTANCE.initializeRegIds();
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(userId);
        MessagesController.DialogPhotos dialogPhotos = MessagesController.getInstance(this.currentAccount).getDialogPhotos(userId);
        int iCoerceAtMost = (userFull != null ? userFull.profile_photo : null) != null ? userFull.profile_photo.date : Integer.MAX_VALUE;
        if (dialogPhotos != null && !dialogPhotos.photos.isEmpty()) {
            for (TLRPC.Photo photo : dialogPhotos.photos) {
                if (photo != null) {
                    iCoerceAtMost = RangesKt.coerceAtMost(photo.date, iCoerceAtMost);
                }
            }
        }
        if (userId < regIds[0].longValue()) {
            jLongValue = regDates[0].longValue();
        } else {
            Long[] lArr = regIds;
            if (userId > lArr[lArr.length - 1].longValue()) {
                jLongValue = regDates[regIds.length - 1].longValue();
            } else {
                int iBinarySearch = Arrays.binarySearch(regIds, Long.valueOf(userId));
                if (iBinarySearch >= 0) {
                    jLongValue = regDates[iBinarySearch].longValue();
                } else {
                    int i = -iBinarySearch;
                    int i2 = i - 2;
                    int i3 = i - 1;
                    long jLongValue2 = regIds[i2].longValue();
                    long jLongValue3 = regIds[i3].longValue();
                    long jLongValue4 = regDates[i2].longValue();
                    jLongValue = (long) (jLongValue4 + ((regDates[i3].longValue() - jLongValue4) * ((userId - jLongValue2) / (jLongValue3 - jLongValue2))));
                }
            }
        }
        return RangesKt.coerceAtMost(iCoerceAtMost, jLongValue);
    }

    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u0010H\u0007J\b\u0010\u0011\u001a\u00020\u0012H\u0002R\u0018\u0010\u0004\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007R\u0016\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\tR\u0018\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\fR\u0018\u0010\r\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\f¨\u0006\u0013"}, d2 = {"Lcom/exteragram/messenger/regdate/RegDateController$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "Instance", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/regdate/RegDateController;", "[Lcom/exteragram/messenger/regdate/RegDateController;", "lockObjects", "[Ljava/lang/Object;", "regIds", _UrlKt.FRAGMENT_ENCODE_SET, "[Ljava/lang/Long;", "regDates", "getInstance", "num", _UrlKt.FRAGMENT_ENCODE_SET, "initializeRegIds", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
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

        /* JADX INFO: Access modifiers changed from: private */
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
