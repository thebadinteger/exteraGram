package org.telegram.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import com.exteragram.messenger.utils.AppUtils;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.R;

public class LauncherIconController {
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

    public static boolean isEnabled(LauncherIcon icon) {
        Context ctx = ApplicationLoader.applicationContext;
        int i = ctx.getPackageManager().getComponentEnabledSetting(icon.getComponentName(ctx));
        return i == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || i == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && icon == LauncherIcon.DEFAULT;
    }

    public static void setIcon(LauncherIcon icon) {
        Context ctx = ApplicationLoader.applicationContext;
        PackageManager pm = ctx.getPackageManager();
        for (LauncherIcon i : LauncherIcon.values()) {
            pm.setComponentEnabledSetting(i.getComponentName(ctx), i == icon ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public enum LauncherIcon {
        DEFAULT("DefaultIcon", BuildVars.isBetaApp() ? R.mipmap.ic_launcher_beta_background : R.color.ic_background, BuildVars.isBetaApp() ? R.mipmap.ic_launcher_beta_foreground : R.mipmap.ic_launcher_foreground, R.string.AppIconDefault),
        WINTER("WinterIcon", R.mipmap.ic_launcher_winter_background, R.mipmap.ic_launcher_foreground, R.string.AppIconWinter, !AppUtils.isWinter()),
        MONET("MonetIcon", R.color.ic_background_monet, R.drawable.ic_foreground_monet, R.string.AppIconMonet, !(Build.VERSION.SDK_INT >= 31 && Build.VERSION.SDK_INT <= 32)),
        ORBIT("OrbitIcon", R.color.ic_background, R.mipmap.ic_launcher_orbit_foreground, R.string.AppIconOrbit),
        AURORA("AuroraIcon", R.mipmap.ic_launcher_aurora_background, R.mipmap.ic_launcher_aurora_foreground, R.string.AppIconAurora),
        SUNSET("SunsetIcon", R.mipmap.ic_launcher_sunset_background, R.mipmap.ic_launcher_sunset_foreground, R.string.AppIconSunset),
        ICEAGE("IceAgeIcon", R.mipmap.ic_launcher_ice_age_background, R.mipmap.ic_launcher_ice_age_foreground, R.string.AppIconIceAge),
        EDITOR("EditorIcon", R.mipmap.ic_launcher_editor_background, R.mipmap.ic_launcher_editor_foreground, R.string.AppIconEditor),
        SPACE("SpaceIcon", R.mipmap.ic_launcher_space_background, R.mipmap.ic_launcher_space_foreground, R.string.AppIconSpace),
        SAPPHIRE("SapphireIcon", R.mipmap.ic_launcher_sapphire_background, R.mipmap.ic_launcher_sapphire_foreground, R.string.AppIconSapphire),
        AMETHYST("AmethystIcon", R.mipmap.ic_launcher_amethyst_background, R.mipmap.ic_launcher_amethyst_foreground, R.string.AppIconAmethyst),
        DSGN480("Dsgn480Icon", R.mipmap.ic_launcher_480dsgn_background, R.mipmap.ic_launcher_480dsgn_foreground, R.string.AppIcon480DSGN),
        CYBERPUNK("CyberpunkIcon", R.color.ic_background_cyberpunk, R.mipmap.ic_launcher_cyberpunk_foreground, R.string.AppIconCyberpunk),
        GOOGLE("GoogleIcon", R.color.white, R.mipmap.ic_launcher_google_foreground, R.string.AppIconGoogle),
        INVINCIBLE("InvincibleIcon", R.mipmap.ic_launcher_invincible_background, R.mipmap.ic_launcher_invincible_foreground, R.string.AppIconInvincible),
        SUS("SusIcon", R.color.ic_background_sus, R.mipmap.ic_launcher_sus_foreground, R.string.AppIconSus);

        public final int background;
        private ComponentName componentName;
        public final int foreground;
        public final boolean hidden;
        public final String key;
        public final boolean premium;
        public final int title;

        public ComponentName getComponentName(Context context) {
            if (this.componentName == null) {
                this.componentName = new ComponentName(context.getPackageName(), "com.exteragram.messenger." + this.key);
            }
            return this.componentName;
        }

        LauncherIcon(String str, int i, int i2, int i3) {
            this(str, i, i2, i3, false);
        }

        LauncherIcon(String str, int i, int i2, int i3, boolean z) {
            this.key = str;
            this.background = i;
            this.foreground = i2;
            this.title = i3;
            this.premium = false;
            this.hidden = z;
        }
    }
}
