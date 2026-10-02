package com.exteragram.messenger.appicons;

import android.content.Context;
import android.content.pm.PackageManager;
import com.exteragram.messenger.plugins.PluginsConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Utilities;

/* JADX INFO: loaded from: classes4.dex */
public abstract class AppIconController {
    private static List<AppIcon> allIcons;
    private static AppIcon selectedIcon;

    public static List<AppIcon> getAllIcons() {
        if (allIcons == null) {
            allIcons = Collections.unmodifiableList(Arrays.asList(GeneratedAppIcons.create()));
        }
        return allIcons;
    }

    public static List<AppIcon> getAvailableIcons() {
        ArrayList arrayList = new ArrayList();
        for (AppIcon appIcon : getAllIcons()) {
            if (appIcon.isAvailable()) {
                arrayList.add(appIcon);
            }
        }
        return arrayList;
    }

    public static AppIcon findById(String str) {
        for (AppIcon appIcon : getAllIcons()) {
            if (appIcon.id.equals(str)) {
                return appIcon;
            }
        }
        return null;
    }

    public static AppIcon getDefaultIcon() {
        AppIcon appIconFindById = findById(PluginsConstants.Settings.DEFAULT);
        return appIconFindById != null ? appIconFindById : getAllIcons().get(0);
    }

    public static boolean isEnabled(AppIcon appIcon) {
        Context context = ApplicationLoader.applicationContext;
        int componentEnabledSetting = context.getPackageManager().getComponentEnabledSetting(appIcon.getComponentName(context));
        return componentEnabledSetting == 1 || (componentEnabledSetting == 0 && appIcon.isDefault());
    }

    public static AppIcon getSelectedIcon() {
        if (selectedIcon == null) {
            for (AppIcon appIcon : getAllIcons()) {
                if (isEnabled(appIcon)) {
                    selectedIcon = appIcon;
                    break;
                }
            }
            if (selectedIcon == null) {
                selectedIcon = getDefaultIcon();
            }
        }
        return selectedIcon;
    }

    public static void setIcon(final AppIcon appIcon) {
        selectedIcon = appIcon;
        final List<AppIcon> allIcons2 = getAllIcons();
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.appicons.AppIconController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AppIconController.applyIcon(appIcon, allIcons2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void applyIcon(AppIcon appIcon, List<AppIcon> list) {
        Context context = ApplicationLoader.applicationContext;
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(appIcon.getComponentName(context), 1, 1);
        for (AppIcon appIcon2 : list) {
            if (appIcon2 != appIcon) {
                packageManager.setComponentEnabledSetting(appIcon2.getComponentName(context), 2, 1);
            }
        }
    }

    public static void fixLauncherIconIfNeeded() {
        final List<AppIcon> allIcons2 = getAllIcons();
        final AppIcon defaultIcon = getDefaultIcon();
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.appicons.AppIconController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AppIconController.m1105$r8$lambda$5uIc0UfuYcxsOt72Pr_KfKeTK8(allIcons2, defaultIcon);
            }
        });
    }

    /* JADX INFO: renamed from: $r8$lambda$5uIc0UfuYc-xsOt72Pr_KfKeTK8, reason: not valid java name */
    public static /* synthetic */ void m1105$r8$lambda$5uIc0UfuYcxsOt72Pr_KfKeTK8(List list, final AppIcon appIcon) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            final AppIcon appIcon2 = (AppIcon) it.next();
            if (isEnabled(appIcon2)) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.appicons.AppIconController$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        AppIconController.selectedIcon = appIcon2;
                    }
                });
                if (appIcon2.isMonet()) {
                    applyIcon(appIcon, list);
                    applyIcon(appIcon2, list);
                    return;
                }
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.appicons.AppIconController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AppIconController.selectedIcon = appIcon;
            }
        });
        applyIcon(appIcon, list);
    }
}
