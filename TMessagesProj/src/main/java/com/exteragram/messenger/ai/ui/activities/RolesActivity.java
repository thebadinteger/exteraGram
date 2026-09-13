package com.exteragram.messenger.ai.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.data.Role;
import com.exteragram.messenger.ai.ui.components.RoleCell;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import java.util.ArrayList;
import java.util.List;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AIEditorAlert;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;

public class RolesActivity extends BasePreferencesActivity implements NotificationCenter.NotificationCenterDelegate {
    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public boolean needHideTitle() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.rolesUpdated);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.rolesUpdated);
        super.onFragmentDestroy();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.rolesUpdated) {
            this.listView.adapter.update(true);
        }
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity, org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        View viewCreateView = super.createView(context);
        this.actionBar.createMenu().addItem(0, R.drawable.msg_add);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: com.exteragram.messenger.ai.ui.activities.RolesActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    RolesActivity.this.finishFragment();
                } else if (i == 0) {
                    RolesActivity.this.lambda$onLongClick$2(null);
                }
            }
        });
        this.fragmentView = viewCreateView;
        return viewCreateView;
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public String getTitle() {
        return LocaleController.getString(R.string.Roles);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(getTitle(), LocaleController.getString(R.string.RolesInfo), "exteraGramPlaceholders", "🎭"));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.Suggestions)));
        for (final Role role : AiController.getInstance().getSuggestedRoles()) {
            arrayList.add(RoleCell.Factory.asRoleCell(role, new View.OnClickListener() { // from class: com.exteragram.messenger.ai.ui.activities.RolesActivity$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    RolesActivity.this.lambda$fillItems$0(role, view);
                }
            }));
        }
        arrayList.add(UItem.asShadow(null));
        List<Role> roles = AiController.getInstance().getRoles();
        if (roles.isEmpty()) {
            return;
        }
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.Roles)));
        for (final Role role2 : roles) {
            arrayList.add(RoleCell.Factory.asRoleCell(role2, new View.OnClickListener() { // from class: com.exteragram.messenger.ai.ui.activities.RolesActivity$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    RolesActivity.this.lambda$fillItems$1(role2, view);
                }
            }));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fillItems$0(Role role, View view) {
        selectRole(role);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fillItems$1(Role role, View view) {
        selectRole(role);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        Object obj = uItem.object;
        if (obj instanceof Role) {
            Role role = (Role) obj;
            if (role.isSuggestion()) {
                showRolePreview(role);
            } else {
                lambda$onLongClick$2(role);
            }
        }
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem == null) {
            return false;
        }
        Object obj = uItem.object;
        if (!(obj instanceof Role)) {
            return false;
        }
        final Role role = (Role) obj;
        if (role.isSuggestion()) {
            return false;
        }
        ItemOptions.makeOptions(this, view).add(R.drawable.msg_edit, LocaleController.getString(R.string.Edit), new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.RolesActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RolesActivity.this.lambda$onLongClick$2(role);
            }
        }).add(R.drawable.msg_copy, LocaleController.getString(R.string.Copy), new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.RolesActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RolesActivity.this.lambda$onLongClick$3(role);
            }
        }).add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.Delete), true, new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.RolesActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                RolesActivity.this.lambda$onLongClick$4(role);
            }
        }).setScrimViewBackground(this.listView.getClipBackground(view)).setGravity(LocaleController.isRTL ? 3 : 5).show();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClick$3(Role role) {
        if (AndroidUtilities.addToClipboard(role.getName() + "\n" + role.getPrompt())) {
            BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: confirmDeleteRole, reason: merged with bridge method [inline-methods] */
    public void lambda$onLongClick$4(final Role role) {
        if (role == null || getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.Delete));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.DeleteRoleInfo, role.getName())));
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.ai.ui.activities.RolesActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                RolesActivity.this.lambda$confirmDeleteRole$5(role, alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog alertDialogCreate = builder.create();
        showDialog(alertDialogCreate);
        TextView textView = (TextView) alertDialogCreate.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$confirmDeleteRole$5(Role role, AlertDialog alertDialog, int i) {
        deleteRole(role);
    }

    private void deleteRole(Role role) {
        boolean z = role != null && role.isSelected();
        if (role == null || !AiController.getInstance().removeRole(role)) {
            return;
        }
        if (z) {
            AiConfig.setSelectedAiRole(AiController.getInstance().getSuggestedRoles().get(0));
        }
        this.listView.adapter.update(true);
    }

    private void selectRole(Role role) {
        if (role.isSelected()) {
            return;
        }
        AiConfig.setSelectedAiRole(role);
        this.listView.adapter.update(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: showRoleAlert, reason: merged with bridge method [inline-methods] */
    public void lambda$onLongClick$2(final Role role) {
        Activity parentActivity = getParentActivity();
        if (parentActivity == null) {
            return;
        }
        AIEditorAlert.CreateAiStyleAlert createAiStyleAlert = new AIEditorAlert.CreateAiStyleAlert(parentActivity, getResourceProvider());
        String prompt = _UrlKt.FRAGMENT_ENCODE_SET;
        String name = role != null ? role.getName() : _UrlKt.FRAGMENT_ENCODE_SET;
        if (role != null) {
            prompt = role.getPrompt();
        }
        createAiStyleAlert.setLocalStyle(name, prompt, role != null ? role.getEmojiId() : 0L, (role == null || role.isSuggestion()) ? false : true, 64, 1024, new Utilities.Callback3Return() { // from class: com.exteragram.messenger.ai.ui.activities.RolesActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.messenger.Utilities.Callback3Return
            public final Object run(Object obj, Object obj2, Object obj3) {
                return RolesActivity.this.lambda$showRoleAlert$6(role, (String) obj, (String) obj2, (Long) obj3);
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$showRoleAlert$6(Role role, String str, String str2, Long l) {
        boolean zAddRole;
        Role emojiId = new Role(str, str2).setEmojiId(l.longValue());
        boolean z = role != null && role.isSelected();
        if (role != null && !role.isSuggestion()) {
            zAddRole = AiController.getInstance().updateRole(role, emojiId);
        } else {
            zAddRole = AiController.getInstance().addRole(emojiId);
        }
        if (zAddRole) {
            if (z) {
                AiConfig.setSelectedAiRole(emojiId);
            }
            getNotificationCenter().postNotificationNameOnUIThread(NotificationCenter.rolesUpdated, new Object[0]);
        }
        return Boolean.valueOf(zAddRole);
    }

    private void showRolePreview(Role role) {
        Activity parentActivity = getParentActivity();
        if (parentActivity == null) {
            return;
        }
        new AIEditorAlert.CreateAiStyleAlert(parentActivity, getResourceProvider()).setLocalStylePreview(role.getName(), role.getPrompt(), role.getEmojiId(), 64, 1024).show();
    }
}
