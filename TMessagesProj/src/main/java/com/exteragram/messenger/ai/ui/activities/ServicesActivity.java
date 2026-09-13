package com.exteragram.messenger.ai.ui.activities;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.data.Service;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class ServicesActivity extends BasePreferencesActivity implements NotificationCenter.NotificationCenterDelegate {
    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public boolean needHideTitle() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.servicesUpdated);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.servicesUpdated);
        super.onFragmentDestroy();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.servicesUpdated) {
            this.listView.adapter.update(true);
        }
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public String getTitle() {
        return LocaleController.getString(R.string.Services);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(getTitle(), LocaleController.getString(R.string.ServicesInfo), "exteraGramPlaceholders", "🔑"));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.Services)));
        List<Service> all = AiController.getInstance().getAll();
        for (int i = 0; i < all.size(); i++) {
            Service service = all.get(i);
            UItem checked = UItem.asRadio2(i + 100, service.getModel(), service.getUrl()).setChecked(service.isSelected());
            checked.object = service;
            arrayList.add(checked);
        }
        Drawable drawable = getContext().getResources().getDrawable(R.drawable.poll_add_circle);
        Drawable drawable2 = getContext().getResources().getDrawable(R.drawable.poll_add_plus);
        int themedColor = getThemedColor(Theme.key_switchTrackChecked);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        drawable.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
        drawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_checkboxCheck), mode));
        UItem uItemAccent = UItem.asButton(1, new CombinedDrawable(drawable, drawable2) { // from class: com.exteragram.messenger.ai.ui.activities.ServicesActivity.1
            @Override // org.telegram.ui.Components.CombinedDrawable, android.graphics.drawable.Drawable
            public void setColorFilter(ColorFilter colorFilter) {
            }

            {
                this.translateX = AndroidUtilities.dp(2.0f);
            }
        }, LocaleController.getString(R.string.NewService)).accent();
        uItemAccent.pad = 61;
        arrayList.add(uItemAccent);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem.id >= 100) {
            Object obj = uItem.object;
            if (obj instanceof Service) {
                Service service = (Service) obj;
                if (!service.isSelected()) {
                    AiConfig.setSelectedServices(service);
                    this.listView.adapter.update(true);
                    return;
                }
            }
        }
        if (uItem.id == 1) {
            presentFragment(new EditServiceActivity());
        }
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem == null) {
            return false;
        }
        Object obj = uItem.object;
        if (!(obj instanceof Service)) {
            return false;
        }
        final Service service = (Service) obj;
        ItemOptions.makeOptions(this, view).add(R.drawable.msg_edit, LocaleController.getString(R.string.Edit), new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.ServicesActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ServicesActivity.this.lambda$onLongClick$0(service);
            }
        }).add(R.drawable.msg_copy, LocaleController.getString(R.string.Copy), new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.ServicesActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ServicesActivity.this.lambda$onLongClick$1(service);
            }
        }).add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.Delete), true, new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.ServicesActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ServicesActivity.this.lambda$onLongClick$2(service);
            }
        }).setGravity(LocaleController.isRTL ? 3 : 5).setScrimViewBackground(this.listView.getClipBackground(view)).show();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClick$0(Service service) {
        presentFragment(new EditServiceActivity(service));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClick$1(Service service) {
        if (AndroidUtilities.addToClipboard(service.getUrl() + "\n" + service.getModel() + "\n" + service.getKey())) {
            BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: confirmDeleteService, reason: merged with bridge method [inline-methods] */
    public void lambda$onLongClick$2(final Service service) {
        if (service == null || getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.Delete));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.DeleteServiceInfo, service.getShortModel())));
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.ai.ui.activities.ServicesActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                ServicesActivity.this.lambda$confirmDeleteService$3(service, alertDialog, i);
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
    public /* synthetic */ void lambda$confirmDeleteService$3(Service service, AlertDialog alertDialog, int i) {
        deleteService(service);
    }

    private void deleteService(Service service) {
        UniversalAdapter universalAdapter;
        boolean z = service != null && service.isSelected();
        if (service == null || !AiController.getInstance().removeService(service)) {
            return;
        }
        if (z) {
            AiConfig.clearSelectedService();
            if (!AiController.getInstance().isServicesEmpty()) {
                AiConfig.setSelectedServices(AiController.getInstance().getAll().get(0));
            }
        }
        getNotificationCenter().postNotificationNameOnUIThread(NotificationCenter.servicesUpdated, new Object[0]);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }
}
