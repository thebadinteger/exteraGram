package com.exteragram.messenger.ai;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.exteragram.messenger.ai.data.Role;
import com.exteragram.messenger.ai.data.Service;
import com.exteragram.messenger.ai.data.Suggestions;
import com.exteragram.messenger.utils.chats.ChatUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;

public class AiController {
    private final List<Role> roles = new ArrayList();
    private final List<Service> services = new ArrayList();

    public static class SingletonHolder {
        private static final AiController INSTANCE = new AiController();
    }

    public AiController() {
        loadRoles();
        loadServices();
    }

    public static void clearHistory(BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, boolean z) {
        clearHistory(baseFragment, resourcesProvider, z, null);
    }

    public static void clearHistory(final BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, boolean z, final Runnable runnable) {
        if (baseFragment == null) {
            return;
        }
        if (z) {
            AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity(), resourcesProvider);
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.ClearConversationHistoryInfo)));
            builder.setTitle(LocaleController.getString(R.string.ClearHistory));
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString(R.string.ClearButton), new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.ai.AiController$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                public final void onClick(AlertDialog alertDialog, int i) {
                    AiController.m840$r8$lambda$k4Ykokjerf7UwZXWV7hhEULXCk(runnable, baseFragment, alertDialog, i);
                }
            });
            AlertDialog alertDialogCreate = builder.create();
            baseFragment.showDialog(alertDialogCreate);
            TextView textView = (TextView) alertDialogCreate.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                return;
            }
            return;
        }
        AiConfig.clearConversationHistory();
        if (runnable != null) {
            runnable.run();
        }
        BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.ic_delete, LocaleController.getString(R.string.HistoryCleared)).show();
    }

    /* JADX INFO: renamed from: $r8$lambda$k4Ykokjerf7UwZXW-V7hhEULXCk, reason: not valid java name */
    public static /* synthetic */ void m840$r8$lambda$k4Ykokjerf7UwZXWV7hhEULXCk(Runnable runnable, BaseFragment baseFragment, AlertDialog alertDialog, int i) {
        AiConfig.clearConversationHistory();
        if (runnable != null) {
            runnable.run();
        }
        BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.ic_delete, LocaleController.getString(R.string.HistoryCleared)).show();
    }

    public static void showErrorBulletin(ViewGroup viewGroup, Theme.ResourcesProvider resourcesProvider, int i) {
        showErrorBulletin(null, viewGroup, resourcesProvider, i);
    }

    public static void showErrorBulletin(BaseFragment baseFragment, int i) {
        showErrorBulletin(baseFragment, null, null, i);
    }

    private static void showErrorBulletin(BaseFragment baseFragment, ViewGroup viewGroup, Theme.ResourcesProvider resourcesProvider, int i) {
        final int i2;
        final int i3;
        int i4Val;
        final BulletinFactory bulletinFactoryOf;
        if (i == 408) {
            i2 = R.string.AIError408;
            i3 = R.string.AIError408Info;
            i4Val = 5;
        } else if (i != 429) {
            i4Val = 3;
            if (i == 502) {
                i2 = R.string.AIError502;
                i3 = R.string.AIError502Info;
            } else if (i != 503) {
                switch (i) {
                    case 400:
                        i2 = R.string.AIError400;
                        i3 = R.string.AIError400Info;
                        i4Val = 2;
                        break;
                    case 401:
                        i2 = R.string.AIError401;
                        i3 = R.string.AIError401Info;
                        break;
                    case 402:
                        i2 = R.string.AIError402;
                        i3 = R.string.AIError402Info;
                        i4Val = 8;
                        break;
                    case 403:
                        i2 = R.string.AIError403;
                        i3 = R.string.AIError403Info;
                        i4Val = 9;
                        break;
                    default:
                        i2 = R.string.AIError;
                        i3 = R.string.AIErrorInfo;
                        break;
                }
            } else {
                i2 = R.string.AIError503;
                i3 = R.string.AIError503Info;
            }
        } else {
            i2 = R.string.AIError429;
            i3 = R.string.AIError429Info;
            i4Val = 6;
        }
        final int i4 = i4Val;
        if (viewGroup != null) {
            bulletinFactoryOf = BulletinFactory.of((FrameLayout) viewGroup, resourcesProvider);
        } else {
            bulletinFactoryOf = baseFragment != null ? BulletinFactory.of(baseFragment) : BulletinFactory.global();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.ai.AiController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                bulletinFactoryOf.createSimpleBulletin(LocaleController.getString(i2), LocaleController.getString(i3), i4).show();
            }
        });
    }

    public static AiController getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static boolean canUseAI() {
        return !TextUtils.isEmpty(getInstance().getSelected().getKey());
    }

    public void loadRoles() {
        ArrayList<Role> roles = AiConfig.getRoles();
        this.roles.clear();
        this.roles.addAll(roles);
        this.roles.removeIf(new Predicate() { // from class: com.exteragram.messenger.ai.AiController$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return AiController.$r8$lambda$ml9V0PZBMdpgTeM8PwcrlItS8AE((Role) obj);
            }
        });
    }

    public static /* synthetic */ boolean $r8$lambda$ml9V0PZBMdpgTeM8PwcrlItS8AE(Role role) {
        return role == null || role.getName() == null || role.getPrompt() == null;
    }

    public List<Role> getRoles() {
        return Collections.unmodifiableList(this.roles);
    }

    public List<Role> getSuggestedRoles() {
        return (List) Arrays.stream(Suggestions.values()).map(new Function() { // from class: com.exteragram.messenger.ai.AiController$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Suggestions) obj).getRole();
            }
        }).collect(Collectors.toList());
    }

    public boolean isCustomRole(Role role) {
        if (role == null) {
            return false;
        }
        return this.roles.contains(role);
    }

    public boolean isSuggestedRole(Role role) {
        if (role == null) {
            return false;
        }
        return getSuggestedRoles().contains(role);
    }

    public Role getSelectedRole() {
        for (Role role : this.roles) {
            if (role.isSelected()) {
                return role;
            }
        }
        for (Role role2 : getSuggestedRoles()) {
            if (role2.isSelected()) {
                return role2;
            }
        }
        return Suggestions.ASSISTANT.getRole();
    }

    public boolean addRole(Role role) {
        if (isSuggestedRole(role) || isCustomRole(role)) {
            return false;
        }
        this.roles.add(0, role);
        saveRoles();
        return true;
    }

    public boolean removeRole(Role role) {
        if (role == null) {
            return false;
        }
        boolean zRemove = this.roles.remove(role);
        if (zRemove) {
            saveRoles();
        }
        return zRemove;
    }

    public boolean updateRole(Role role, Role role2) {
        int iIndexOf = this.roles.indexOf(role);
        if (iIndexOf == -1) {
            return false;
        }
        if (isSuggestedRole(role2) && !role.equals(role2)) {
            return false;
        }
        this.roles.set(iIndexOf, role2);
        saveRoles();
        return true;
    }

    public void saveRoles() {
        AiConfig.saveRoles(new ArrayList(this.roles));
    }

    public void loadServices() {
        ArrayList<Service> services = AiConfig.getServices();
        this.services.clear();
        this.services.addAll(services);
    }

    public List<Service> getAll() {
        return Collections.unmodifiableList(this.services);
    }

    public boolean isServicesEmpty() {
        return this.services.isEmpty();
    }

    public void addService(Service service) {
        if (this.services.contains(service)) {
            return;
        }
        this.services.add(service);
        saveServices();
    }

    public void updateService(Service service, Service service2) {
        int iIndexOf = this.services.indexOf(service);
        if (iIndexOf != -1) {
            service2.setId(service.getId());
            this.services.set(iIndexOf, service2);
            saveServices();
        }
    }

    public boolean removeService(Service service) {
        boolean zRemove = this.services.remove(service);
        if (zRemove) {
            saveServices();
        }
        return zRemove;
    }

    public Service getSelected() {
        Optional<Service> optionalFindFirst = this.services.stream().filter(new Predicate() { // from class: com.exteragram.messenger.ai.AiController$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((Service) obj).isSelected();
            }
        }).findFirst();
        if (optionalFindFirst.isPresent()) {
            return optionalFindFirst.get();
        }
        return this.services.isEmpty() ? AiConfig.DEFAULT_SERVICE : this.services.get(0);
    }

    public void saveServices() {
        this.services.sort(Comparator.comparing(new Function() { // from class: com.exteragram.messenger.ai.AiController$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Service) obj).getModel();
            }
        }, Comparator.nullsLast(Comparator.naturalOrder())));
        AiConfig.saveServices(new ArrayList(this.services));
    }

    public static boolean canSendImage(MessageObject messageObject) {
        return messageObject != null && canSendImage(ChatUtils.getInstance().getPathToMessage(messageObject));
    }

    public static boolean canSendImage(String str) {
        if (str == null) {
            return false;
        }
        File file = new File(str);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        String lowerCase = str.toLowerCase();
        return lowerCase.endsWith(".png") || lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".webp") || lowerCase.endsWith(".heic") || lowerCase.endsWith(".heif");
    }
}
