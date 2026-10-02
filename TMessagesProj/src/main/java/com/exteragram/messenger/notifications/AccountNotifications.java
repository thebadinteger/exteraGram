package com.exteragram.messenger.notifications;

import com.exteragram.messenger.plugins.PluginsConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0007\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007¢\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\n\u001a\u00020\tH\u0007¢\u0006\u0004\b\n\u0010\u0003J\u0015\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u000bH\u0007¢\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000e\u001a\u00020\u0004H\u0007¢\u0006\u0004\b\u000e\u0010\u000fJ\u0017\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u0004H\u0002¢\u0006\u0004\b\u0010\u0010\u0011J\u0017\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0005\u001a\u00020\u0004H\u0002¢\u0006\u0004\b\u0013\u0010\u0014J\u0015\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b\u0015\u0010\bJ\u001d\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u0006¢\u0006\u0004\b\u0017\u0010\u0018¨\u0006\u0019"}, d2 = {"Lcom/exteragram/messenger/notifications/AccountNotifications;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", _UrlKt.FRAGMENT_ENCODE_SET, "account", _UrlKt.FRAGMENT_ENCODE_SET, "shouldShow", "(I)Z", _UrlKt.FRAGMENT_ENCODE_SET, "applyAll", _UrlKt.FRAGMENT_ENCODE_SET, "getAccounts", "()Ljava/util/List;", "getEnabledCount", "()I", "apply", "(I)V", _UrlKt.FRAGMENT_ENCODE_SET, PluginsConstants.Settings.KEY, "(I)Ljava/lang/String;", "isEnabled", "enabled", "setEnabled", "(IZ)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nAccountNotifications.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AccountNotifications.kt\ncom/exteragram/messenger/notifications/AccountNotifications\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,47:1\n1915#2,2:48\n777#2:50\n873#2,2:51\n1834#2,4:53\n*S KotlinDebug\n*F\n+ 1 AccountNotifications.kt\ncom/exteragram/messenger/notifications/AccountNotifications\n*L\n26#1:48,2\n31#1:50\n31#1:51,2\n34#1:53,4\n*E\n"})
public final class AccountNotifications {
    public static final AccountNotifications INSTANCE = new AccountNotifications();

    private AccountNotifications() {
    }

    public final boolean isEnabled(int account) {
        return MessagesController.getGlobalNotificationsSettings().getBoolean(key(account), true);
    }

    public final void setEnabled(int account, boolean enabled) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean(key(account), enabled).apply();
        apply(account);
    }

    @JvmStatic
    public static final boolean shouldShow(int account) {
        if (account != UserConfig.selectedAccount) {
            return SharedConfig.showNotificationsForAllAccounts && INSTANCE.isEnabled(account);
        }
        return true;
    }

    @JvmStatic
    public static final void applyAll() {
        List<Integer> accounts = getAccounts();
        AccountNotifications accountNotifications = INSTANCE;
        for (Integer num : accounts) {
            accountNotifications.apply(num.intValue());
        }
    }

    @JvmStatic
    public static final List<Integer> getAccounts() {
        IntRange intRangeUntil = RangesKt.until(0, 16);
        ArrayList arrayList = new ArrayList();
        for (Integer num : intRangeUntil) {
            if (UserConfig.getInstance(num.intValue()).isClientActivated()) {
                arrayList.add(num);
            }
        }
        return arrayList;
    }

    @JvmStatic
    public static final int getEnabledCount() {
        List<Integer> accounts = getAccounts();
        int i = 0;
        if ((accounts instanceof Collection) && accounts.isEmpty()) {
            return 0;
        }
        for (Integer num : accounts) {
            int iIntValue = num.intValue();
            if (iIntValue == UserConfig.selectedAccount || INSTANCE.isEnabled(iIntValue)) {
                i++;
                if (i < 0) {
                    CollectionsKt.throwCountOverflow();
                }
            }
        }
        return i;
    }

    private final void apply(int account) {
        NotificationsController notificationsController = NotificationsController.getInstance(account);
        if (shouldShow(account)) {
            notificationsController.showNotifications();
        } else {
            notificationsController.hideNotifications();
        }
    }

    private final String key(int account) {
        return "accountNotifications_" + UserConfig.getInstance(account).clientUserId;
    }
}
