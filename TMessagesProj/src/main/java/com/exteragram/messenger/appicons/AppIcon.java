package com.exteragram.messenger.appicons;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import com.exteragram.messenger.plugins.PluginsConstants;
import com.exteragram.messenger.utils.AppUtils;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

/* JADX INFO: loaded from: classes4.dex */
public final class AppIcon {
    public final String authorName;
    public final String authorUsername;
    public final String availability;
    public final int background;
    public final boolean backgroundIsColor;
    public final int color;
    public final String component;
    private ComponentName componentName;
    public final String description;
    public final int descriptionRes;
    public final int foreground;
    public final String id;
    public final int monochrome;
    public final String title;
    public final int titleRes;

    private AppIcon(String str, String str2, String str3, int i, String str4, int i2, String str5, int i3, String str6, String str7, int i4, boolean z, int i5, int i6) {
        this.id = str;
        this.component = str2;
        this.availability = str3;
        this.titleRes = i;
        this.title = str4;
        this.descriptionRes = i2;
        this.description = str5;
        this.color = i3;
        this.authorUsername = str6;
        this.authorName = str7;
        this.background = i4;
        this.backgroundIsColor = z;
        this.foreground = i5;
        this.monochrome = i6;
    }

    public static AppIcon of(String str, String str2, String str3, int i, String str4, int i2, String str5, int i3, String str6, String str7, int i4, boolean z, int i5, int i6) {
        return new AppIcon(str, str2, str3, i, str4, i2, str5, i3, str6, str7, i4, z, i5, i6);
    }

    public ComponentName getComponentName(Context context) {
        if (this.componentName == null) {
            this.componentName = new ComponentName(context.getPackageName(), this.component);
        }
        return this.componentName;
    }

    public boolean isDefault() {
        return PluginsConstants.Settings.DEFAULT.equals(this.id);
    }

    public boolean isMonet() {
        return "monet".equals(this.availability);
    }

    public boolean isAvailable() {
        if ("winter".equals(this.availability)) {
            return AppUtils.isWinter();
        }
        if (!isMonet()) {
            return true;
        }
        int i = Build.VERSION.SDK_INT;
        return i >= 31 && i <= 32;
    }

    public int getBackground() {
        return (isDefault() && BuildVars.isBetaApp()) ? R.mipmap.ic_launcher_beta_background : this.background;
    }

    public boolean isBackgroundColor() {
        if (this.backgroundIsColor) {
            return (isDefault() && BuildVars.isBetaApp()) ? false : true;
        }
        return false;
    }

    public int getForeground() {
        return (isDefault() && BuildVars.isBetaApp()) ? R.mipmap.ic_launcher_beta_foreground : this.foreground;
    }

    public CharSequence getTitle() {
        int i = this.titleRes;
        return i != 0 ? LocaleController.getString(i) : this.title;
    }

    public CharSequence getDescription() {
        int i = this.descriptionRes;
        if (i != 0) {
            return LocaleController.getString(i);
        }
        return this.description;
    }

    public String getAuthor() {
        String str = this.authorName;
        if (str != null) {
            return str;
        }
        String str2 = this.authorUsername;
        if (str2 == null || "exteraGram".equalsIgnoreCase(str2)) {
            return null;
        }
        return "@" + this.authorUsername;
    }
}
