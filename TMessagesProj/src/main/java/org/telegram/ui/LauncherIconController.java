package org.telegram.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import com.exteragram.messenger.utils.AppUtils;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.R;

public abstract class LauncherIconController {
    public static void tryFixLauncherIconIfNeeded() {
        for (LauncherIcon launcherIcon : LauncherIcon.values()) {
            if (isEnabled(launcherIcon)) {
                LauncherIcon launcherIcon2 = LauncherIcon.MONET;
                if (launcherIcon == launcherIcon2) {
                    setIcon(LauncherIcon.DEFAULT);
                    setIcon(launcherIcon2);
                    return;
                }
                return;
            }
        }
        setIcon(LauncherIcon.DEFAULT);
    }

    public static boolean isEnabled(LauncherIcon launcherIcon) {
        Context context = ApplicationLoader.applicationContext;
        int componentEnabledSetting = context.getPackageManager().getComponentEnabledSetting(launcherIcon.getComponentName(context));
        return componentEnabledSetting == 1 || (componentEnabledSetting == 0 && launcherIcon == LauncherIcon.DEFAULT);
    }

    public static void setIcon(LauncherIcon launcherIcon) {
        Context context = ApplicationLoader.applicationContext;
        PackageManager packageManager = context.getPackageManager();
        LauncherIcon[] launcherIconArrValues = LauncherIcon.values();
        int length = launcherIconArrValues.length;
        for (int i = 0; i < length; i++) {
            LauncherIcon launcherIcon2 = launcherIconArrValues[i];
            packageManager.setComponentEnabledSetting(launcherIcon2.getComponentName(context), launcherIcon2 == launcherIcon ? 1 : 2, 1);
        }
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    public static final class LauncherIcon {
        private static final /* synthetic */ LauncherIcon[] $VALUES;
        public static final LauncherIcon AMETHYST;
        public static final LauncherIcon AURORA;
        public static final LauncherIcon CYBERPUNK;
        public static final LauncherIcon DEFAULT;
        public static final LauncherIcon DSGN480;
        public static final LauncherIcon EDITOR;
        public static final LauncherIcon GOOGLE;
        public static final LauncherIcon ICEAGE;
        public static final LauncherIcon INVINCIBLE;
        public static final LauncherIcon MONET;
        public static final LauncherIcon ORBIT;
        public static final LauncherIcon SAPPHIRE;
        public static final LauncherIcon SPACE;
        public static final LauncherIcon SUNSET;
        public static final LauncherIcon SUS;
        public static final LauncherIcon WINTER;
        public final int background;
        private ComponentName componentName;
        public final int foreground;
        public final boolean hidden;
        public final String key;
        public final boolean premium;
        public final int title;

        private static /* synthetic */ LauncherIcon[] $values() {
            return new LauncherIcon[]{DEFAULT, WINTER, MONET, ORBIT, AURORA, SUNSET, ICEAGE, EDITOR, SPACE, SAPPHIRE, AMETHYST, DSGN480, CYBERPUNK, GOOGLE, INVINCIBLE, SUS};
        }

        public static LauncherIcon valueOf(String str) {
            return (LauncherIcon) Enum.valueOf(LauncherIcon.class, str);
        }

        public static LauncherIcon[] values() {
            return (LauncherIcon[]) $VALUES.clone();
        }

        static {
            DEFAULT = new LauncherIcon("DEFAULT", 0, "DefaultIcon", BuildVars.isBetaApp() ? R.mipmap.ic_launcher_beta_background : R.color.ic_background, BuildVars.isBetaApp() ? R.mipmap.ic_launcher_beta_foreground : R.mipmap.ic_launcher_foreground, R.string.AppIconDefault);
            boolean z = true;
            WINTER = new LauncherIcon("WINTER", 1, "WinterIcon", R.mipmap.ic_launcher_winter_background, R.mipmap.ic_launcher_foreground, R.string.AppIconWinter, !AppUtils.isWinter());
            int i = R.color.ic_background_monet;
            int i2 = R.drawable.ic_foreground_monet;
            int i3 = R.string.AppIconMonet;
            int i4 = Build.VERSION.SDK_INT;
            if (i4 >= 31 && i4 <= 32) {
                z = false;
            }
            MONET = new LauncherIcon("MONET", 2, "MonetIcon", i, i2, i3, z);
            ORBIT = new LauncherIcon("ORBIT", 3, "OrbitIcon", R.color.ic_background, R.mipmap.ic_launcher_orbit_foreground, R.string.AppIconOrbit);
            AURORA = new LauncherIcon("AURORA", 4, "AuroraIcon", R.mipmap.ic_launcher_aurora_background, R.mipmap.ic_launcher_aurora_foreground, R.string.AppIconAurora);
            SUNSET = new LauncherIcon("SUNSET", 5, "SunsetIcon", R.mipmap.ic_launcher_sunset_background, R.mipmap.ic_launcher_sunset_foreground, R.string.AppIconSunset);
            ICEAGE = new LauncherIcon("ICEAGE", 6, "IceAgeIcon", R.mipmap.ic_launcher_ice_age_background, R.mipmap.ic_launcher_ice_age_foreground, R.string.AppIconIceAge);
            EDITOR = new LauncherIcon("EDITOR", 7, "EditorIcon", R.mipmap.ic_launcher_editor_background, R.mipmap.ic_launcher_editor_foreground, R.string.AppIconEditor);
            SPACE = new LauncherIcon("SPACE", 8, "SpaceIcon", R.mipmap.ic_launcher_space_background, R.mipmap.ic_launcher_space_foreground, R.string.AppIconSpace);
            SAPPHIRE = new LauncherIcon("SAPPHIRE", 9, "SapphireIcon", R.mipmap.ic_launcher_sapphire_background, R.mipmap.ic_launcher_sapphire_foreground, R.string.AppIconSapphire);
            AMETHYST = new LauncherIcon("AMETHYST", 10, "AmethystIcon", R.mipmap.ic_launcher_amethyst_background, R.mipmap.ic_launcher_amethyst_foreground, R.string.AppIconAmethyst);
            DSGN480 = new LauncherIcon("DSGN480", 11, "Dsgn480Icon", R.mipmap.ic_launcher_480dsgn_background, R.mipmap.ic_launcher_480dsgn_foreground, R.string.AppIcon480DSGN);
            CYBERPUNK = new LauncherIcon("CYBERPUNK", 12, "CyberpunkIcon", R.color.ic_background_cyberpunk, R.mipmap.ic_launcher_cyberpunk_foreground, R.string.AppIconCyberpunk);
            GOOGLE = new LauncherIcon("GOOGLE", 13, "GoogleIcon", R.color.white, R.mipmap.ic_launcher_google_foreground, R.string.AppIconGoogle);
            INVINCIBLE = new LauncherIcon("INVINCIBLE", 14, "InvincibleIcon", R.mipmap.ic_launcher_invincible_background, R.mipmap.ic_launcher_invincible_foreground, R.string.AppIconInvincible);
            SUS = new LauncherIcon("SUS", 15, "SusIcon", R.color.ic_background_sus, R.mipmap.ic_launcher_sus_foreground, R.string.AppIconSus);
            $VALUES = $values();
        }

        public ComponentName getComponentName(Context context) {
            if (this.componentName == null) {
                this.componentName = new ComponentName(context.getPackageName(), "com.exteragram.messenger." + this.key);
            }
            return this.componentName;
        }

        private LauncherIcon(String str, int i, String str2, int i2, int i3, int i4) {
            this(str, i, str2, i2, i3, i4, false);
        }

        private LauncherIcon(String str, int i, String str2, int i2, int i3, int i4, boolean z) {
            super(str, i);
            this.key = str2;
            this.background = i2;
            this.foreground = i3;
            this.title = i4;
            this.premium = false;
            this.hidden = z;
        }
    }
}
