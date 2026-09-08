package com.exteragram.messenger.pillstack.core;

import android.content.Context;
import androidx.annotation.Keep;
import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.pillstack.ui.pills.BasePill;
import com.exteragram.messenger.pillstack.ui.pills.crypto.BtcPill;
import com.exteragram.messenger.pillstack.ui.pills.crypto.GramPill;
import com.exteragram.messenger.pillstack.ui.pills.crypto.UsdPill;
import com.exteragram.messenger.pillstack.ui.pills.system.CachePill;
import com.exteragram.messenger.pillstack.ui.pills.system.ProxyPill;
import com.exteragram.messenger.pillstack.ui.pills.weather.WeatherPill;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.IconBackgroundColors;

public class PillRegistry {
    private static boolean batchRegistration;
    private static final Map<Integer, PillInfo> registry = new LinkedHashMap();

    public interface PillCreator {
        BasePill create(Context context, Theme.ResourcesProvider resourcesProvider);
    }

    public static final class PillInfo extends RecordTag {
        private final PillCreator creator;
        private final int iconColorBottom;
        private final int iconColorTop;
        private final int iconRes;
        private final int id;
        private final CharSequence name;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof PillInfo)) {
                return false;
            }
            PillInfo pillInfo = (PillInfo) obj;
            return this.id == pillInfo.id && this.iconRes == pillInfo.iconRes && this.iconColorTop == pillInfo.iconColorTop && this.iconColorBottom == pillInfo.iconColorBottom && Objects.equals(this.name, pillInfo.name) && Objects.equals(this.creator, pillInfo.creator);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.id), this.name, Integer.valueOf(this.iconRes), Integer.valueOf(this.iconColorTop), Integer.valueOf(this.iconColorBottom), this.creator};
        }

        public PillInfo(int i, CharSequence charSequence, int i2, int i3, int i4, PillCreator pillCreator) {
            this.id = i;
            this.name = charSequence;
            this.iconRes = i2;
            this.iconColorTop = i3;
            this.iconColorBottom = i4;
            this.creator = pillCreator;
        }

        public PillCreator creator() {
            return this.creator;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.id, this.iconRes, this.iconColorTop, this.iconColorBottom, this.name, this.creator);
        }

        public int iconColorBottom() {
            return this.iconColorBottom;
        }

        public int iconColorTop() {
            return this.iconColorTop;
        }

        public int iconRes() {
            return this.iconRes;
        }

        public int id() {
            return this.id;
        }

        public CharSequence name() {
            return this.name;
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), PillInfo.class, "id;name;iconRes;iconColorTop;iconColorBottom;creator");
        }
    }

    static {
        beginTransaction();
        registerDefaultPills();
        endTransaction();
    }

    @Keep
    public static void beginTransaction() {
        batchRegistration = true;
    }

    @Keep
    public static void endTransaction() {
        batchRegistration = false;
        if (PillStackConfig.getConfigLoaded()) {
            PillStackConfig.sanitizePills();
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pillStackLayoutChanged, new Object[0]);
                }
            });
        }
    }

    private static void registerDefaultPills() {
        int id = PillType.WEATHER.getId();
        String string = LocaleController.getString(R.string.WeatherPill);
        int i = R.drawable.weather_cloudy;
        IconBackgroundColors iconBackgroundColors = IconBackgroundColors.BLUE_ALT;
        register(new PillInfo(id, string, i, iconBackgroundColors.top, iconBackgroundColors.bottom, new PillCreator() { 
            @Override 
            public final BasePill create(Context context, Theme.ResourcesProvider resourcesProvider) {
                return new WeatherPill(context, resourcesProvider);
            }
        }));
        int id2 = PillType.GRAM.getId();
        int i2 = R.drawable.settings_gram_24;
        IconBackgroundColors iconBackgroundColors2 = IconBackgroundColors.BLUE_LIGHT;
        register(new PillInfo(id2, "GRAM", i2, iconBackgroundColors2.top, iconBackgroundColors2.bottom, new PillCreator() { 
            @Override 
            public final BasePill create(Context context, Theme.ResourcesProvider resourcesProvider) {
                return new GramPill(context, resourcesProvider);
            }
        }));
        int id3 = PillType.BTC.getId();
        int i3 = R.drawable.pillstack_btc_settings;
        IconBackgroundColors iconBackgroundColors3 = IconBackgroundColors.ORANGE_BRIGHT;
        register(new PillInfo(id3, "BTC", i3, iconBackgroundColors3.top, iconBackgroundColors3.bottom, new PillCreator() { 
            @Override 
            public final BasePill create(Context context, Theme.ResourcesProvider resourcesProvider) {
                return new BtcPill(context, resourcesProvider);
            }
        }));
        int id4 = PillType.USD.getId();
        int i4 = R.drawable.pillstack_usd_settings;
        IconBackgroundColors iconBackgroundColors4 = IconBackgroundColors.GREEN_DEEP;
        register(new PillInfo(id4, "USD", i4, iconBackgroundColors4.top, iconBackgroundColors4.bottom, new PillCreator() { 
            @Override 
            public final BasePill create(Context context, Theme.ResourcesProvider resourcesProvider) {
                return new UsdPill(context, resourcesProvider);
            }
        }));
        int id5 = PillType.CACHE.getId();
        String string2 = LocaleController.getString(R.string.StorageUsage);
        int i5 = R.drawable.msg_filled_storageusage;
        IconBackgroundColors iconBackgroundColors5 = IconBackgroundColors.BLUE_DEEP;
        register(new PillInfo(id5, string2, i5, iconBackgroundColors5.top, iconBackgroundColors5.bottom, new PillCreator() { 
            @Override 
            public final BasePill create(Context context, Theme.ResourcesProvider resourcesProvider) {
                return new CachePill(context, resourcesProvider);
            }
        }));
        int id6 = PillType.PROXY.getId();
        String string3 = LocaleController.getString(R.string.Proxy);
        int i6 = R.drawable.drawer_proxy_on;
        IconBackgroundColors iconBackgroundColors6 = IconBackgroundColors.GREEN;
        register(new PillInfo(id6, string3, i6, iconBackgroundColors6.top, iconBackgroundColors6.bottom, new PillCreator() { 
            @Override 
            public final BasePill create(Context context, Theme.ResourcesProvider resourcesProvider) {
                return new ProxyPill(context, resourcesProvider);
            }
        }));
    }

    public static void register(PillInfo pillInfo) {
        registry.put(Integer.valueOf(pillInfo.id), pillInfo);
        if (batchRegistration) {
            return;
        }
        PillStackConfig.sanitizePills();
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pillStackLayoutChanged, new Object[0]);
            }
        });
    }

    @Keep
    public static void activatePill(int i) {
        if (isRegistered(i) && !PillStackConfig.getActivePills().contains(Integer.valueOf(i))) {
            PillStackConfig.getHiddenPills().remove(Integer.valueOf(i));
            PillStackConfig.getActivePills().add(Integer.valueOf(i));
            PillStackConfig.savePillsLayout();
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pillStackLayoutChanged, new Object[0]);
                }
            });
        }
    }

    public static PillInfo getPillInfo(int i) {
        return registry.get(Integer.valueOf(i));
    }

    public static Collection<PillInfo> getRegisteredPills() {
        return registry.values();
    }

    public static boolean isRegistered(int i) {
        return registry.containsKey(Integer.valueOf(i));
    }

    @Keep
    public static void unregister(int i) {
        if (registry.remove(Integer.valueOf(i)) == null || batchRegistration) {
            return;
        }
        PillStackConfig.sanitizePills();
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pillStackLayoutChanged, new Object[0]);
            }
        });
    }
}
