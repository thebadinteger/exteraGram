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
    @Override 
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

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.rolesUpdated) {
            this.listView.adapter.update(true);
        }
    }

    @Override 
    public View createView(Context context) {
        View viewCreateView = super.createView(context);
        this.actionBar.createMenu().addItem(0, R.drawable.msg_add);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { 
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

    @Override 
    public String getTitle() {
        return LocaleController.getString(R.string.Roles);
    }

    @Override 
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(getTitle(), LocaleController.getString(R.string.RolesInfo), "exteraGramPlaceholders", "🎭"));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.Suggestions)));
        for (final Role role : AiController.getInstance().getSuggestedRoles()) {
            arrayList.add(RoleCell.Factory.asRoleCell(role, new View.OnClickListener() { 
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$fillItems$0(role, view);
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
            arrayList.add(RoleCell.Factory.asRoleCell(role2, new View.OnClickListener() { 
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$fillItems$1(role2, view);
                }
            }));
        }
    }

    public void lambda$onLongClick$3(Role role) {
        if (AndroidUtilities.addToClipboard(role.getName() + "\n" + role.getPrompt())) {
            BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
        }
    }

    public void lambda$onLongClick$4(final Role role) {
        if (role == null || getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.Delete));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.DeleteRoleInfo, role.getName())));
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() { 
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                this.f$0.lambda$confirmDeleteRole$5(role, alertDialog, i);
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

    public Boolean lambda$showRoleAlert$6(Role role, String str, String str2, Long l) {
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
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.rolesUpdated, new Object[0]);
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
