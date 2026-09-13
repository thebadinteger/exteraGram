package com.exteragram.messenger.icons.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.icons.BaseIconPacks;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.icons.IconPack;
import com.exteragram.messenger.icons.ui.components.IconPackCell;
import com.exteragram.messenger.icons.ui.components.NewIconPackBottomSheet;
import com.exteragram.messenger.icons.ui.picker.IconPickerController;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.function.Predicate;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.FragmentFloatingButton;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.LaunchActivity;

public class IconPacksActivity extends BasePreferencesActivity implements NotificationCenter.NotificationCenterDelegate {
    private FragmentFloatingButton floatingButton;
    private Runnable reorderRunnable;
    private boolean scrollUpdated;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.iconPackUpdated);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.iconPackUpdated);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalRecyclerView universalRecyclerView;
        UniversalAdapter universalAdapter;
        if (i != NotificationCenter.iconPackUpdated || (universalRecyclerView = this.listView) == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity, org.telegram.ui.ActionBar.BaseFragment
    public void onInsets(int i, int i2, int i3, int i4) {
        this.floatingButton.setTranslationY(-i4);
        super.onInsets(i, i2, i3, i4);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity, org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        View viewCreateView = super.createView(context);
        FragmentFloatingButton fragmentFloatingButton = new FragmentFloatingButton(context, this.resourceProvider);
        this.floatingButton = fragmentFloatingButton;
        fragmentFloatingButton.imageView.setImageResource(R.drawable.msg_add);
        this.floatingButton.setContentDescription(LocaleController.getString(R.string.Add));
        this.floatingButton.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                IconPacksActivity.this.lambda$createView$0(view);
            }
        });
        if (viewCreateView instanceof FrameLayout) {
            ((FrameLayout) viewCreateView).addView(this.floatingButton, FragmentFloatingButton.createDefaultLayoutParams());
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.allowReorder(true);
            this.listView.listenReorder(new Utilities.Callback2() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity$$ExternalSyntheticLambda4
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    IconPacksActivity.this.updateConfigFromReorder(((Integer) obj).intValue(), (ArrayList) obj2);
                }
            });
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity.1
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    if (i2 != 0 && IconPacksActivity.this.scrollUpdated) {
                        IconPacksActivity.this.floatingButton.setButtonVisible(i2 < 0, true);
                    }
                    IconPacksActivity.this.scrollUpdated = true;
                }
            });
        }
        return viewCreateView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        new NewIconPackBottomSheet(this, getContext()).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConfigFromReorder(int i, ArrayList<UItem> arrayList) {
        UniversalAdapter universalAdapter;
        String str;
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        int i2 = 0;
        int i3 = 0;
        while (i3 < size) {
            UItem uItem = arrayList.get(i3);
            i3++;
            Object obj = uItem.object;
            if (obj instanceof IconPack) {
                arrayList2.add(((IconPack) obj).getId());
            }
        }
        ArrayList<String> iconPacksLayout = ExteraConfig.getIconPacksLayout();
        int size2 = iconPacksLayout.size();
        int i4 = 0;
        int i5 = 0;
        while (i5 < size2) {
            String str2 = iconPacksLayout.get(i5);
            i5++;
            String str3 = str2;
            if (!str3.startsWith("base.") && getPackById(str3) != null) {
                i4++;
            }
        }
        boolean z = i4 > 1 && i == 0;
        if (z) {
            ArrayList<String> iconPacksLayout2 = ExteraConfig.getIconPacksLayout();
            int size3 = iconPacksLayout2.size();
            do {
                if (i2 >= size3) {
                    str = null;
                    break;
                } else {
                    String str4 = iconPacksLayout2.get(i2);
                    i2++;
                    str = str4;
                }
            } while (!str.startsWith("base."));
            ExteraConfig.getIconPacksLayout().clear();
            ExteraConfig.getIconPacksLayout().addAll(arrayList2);
            if (str != null) {
                ExteraConfig.getIconPacksLayout().add(str);
            }
        } else {
            ExteraConfig.getIconPacksHidden().clear();
            ExteraConfig.getIconPacksHidden().addAll(arrayList2);
        }
        ExteraConfig.saveIconPacksLayout();
        if (z) {
            Runnable runnable = this.reorderRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    IconManager.INSTANCE.initialize(true);
                }
            };
            this.reorderRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 500L);
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public String getTitle() {
        return LocaleController.getString(R.string.IconPacks);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        IconPack packById;
        IconPack packById2;
        Object obj = "base.default";
        String[] strArr = {"base.default", "base.solar", "base.remix"};
        ArrayList<String> iconPacksLayout = ExteraConfig.getIconPacksLayout();
        int size = iconPacksLayout.size();
        int i = 0;
        while (i < size) {
            String str = iconPacksLayout.get(i);
            i++;
            String str2 = str;
            if (str2.startsWith("base.")) {
                obj = str2;
                break;
            }
        }
        universalAdapter.whiteSectionStart();
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.BasePacks)));
        for (int i2 = 0; i2 < 3; i2++) {
            String str3 = strArr[i2];
            IconPack packById3 = getPackById(str3);
            if (packById3 != null) {
                UItem uItemAsIconPackCell = IconPackCell.Factory.asIconPackCell(packById3);
                uItemAsIconPackCell.checked = str3.equals(obj);
                arrayList.add(uItemAsIconPackCell);
            }
        }
        universalAdapter.whiteSectionEnd();
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.BaseIconPackInfo)));
        ArrayList<String> iconPacksLayout2 = ExteraConfig.getIconPacksLayout();
        int size2 = iconPacksLayout2.size();
        int i3 = 0;
        int i4 = 0;
        while (i4 < size2) {
            String str4 = iconPacksLayout2.get(i4);
            i4++;
            String str5 = str4;
            if (!str5.startsWith("base.") && getPackById(str5) != null) {
                i3++;
            }
        }
        if (i3 > 0) {
            universalAdapter.whiteSectionStart();
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.EnabledPacks)));
            if (i3 > 1) {
                universalAdapter.reorderSectionStart();
            }
            ArrayList<String> iconPacksLayout3 = ExteraConfig.getIconPacksLayout();
            int size3 = iconPacksLayout3.size();
            int i5 = 0;
            while (i5 < size3) {
                String str6 = iconPacksLayout3.get(i5);
                i5++;
                String str7 = str6;
                if (!str7.startsWith("base.") && (packById2 = getPackById(str7)) != null) {
                    arrayList.add(IconPackCell.Factory.asIconPackCell(packById2).setReordering(i3 > 1));
                }
            }
            if (i3 > 1) {
                universalAdapter.reorderSectionEnd();
            }
            universalAdapter.whiteSectionEnd();
        }
        ArrayList<String> iconPacksHidden = ExteraConfig.getIconPacksHidden();
        int size4 = iconPacksHidden.size();
        int i6 = 0;
        int i7 = 0;
        while (i7 < size4) {
            String str8 = iconPacksHidden.get(i7);
            i7++;
            String str9 = str8;
            if (!str9.startsWith("base.") && getPackById(str9) != null) {
                i6++;
            }
        }
        if (i6 > 0) {
            if (i3 > 0) {
                arrayList.add(UItem.asShadow());
            }
            universalAdapter.whiteSectionStart();
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.AllPacks)));
            if (i6 > 1) {
                universalAdapter.reorderSectionStart();
            }
            ArrayList<String> iconPacksHidden2 = ExteraConfig.getIconPacksHidden();
            int size5 = iconPacksHidden2.size();
            int i8 = 0;
            while (i8 < size5) {
                String str10 = iconPacksHidden2.get(i8);
                i8++;
                String str11 = str10;
                if (!str11.startsWith("base.") && (packById = getPackById(str11)) != null) {
                    arrayList.add(IconPackCell.Factory.asIconPackCell(packById).setReordering(i6 > 1));
                }
            }
            if (i6 > 1) {
                universalAdapter.reorderSectionEnd();
            }
            universalAdapter.whiteSectionEnd();
        }
        if (i3 > 0 || i6 > 0) {
            arrayList.add(UItem.asShadow(LocaleController.getString(R.string.IconPacksHint)));
        }
    }

    private IconPack getPackById(String str) {
        if (str.startsWith("base.")) {
            return BaseIconPacks.INSTANCE.getBasePack(str);
        }
        return IconManager.INSTANCE.findPackById(str);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        Object obj = uItem.object;
        if (obj instanceof IconPack) {
            String id = ((IconPack) obj).getId();
            if (id.startsWith("base.")) {
                ExteraConfig.getIconPacksLayout().removeIf(new Predicate() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity$$ExternalSyntheticLambda7
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj2) {
                        return ((String) obj2).startsWith("base.");
                    }
                });
                ExteraConfig.getIconPacksLayout().add(id);
                ExteraConfig.saveIconPacksLayout();
                IconManager.INSTANCE.initialize(true);
                this.listView.adapter.update(true);
                return;
            }
            if (ExteraConfig.getIconPacksLayout().contains(id)) {
                ExteraConfig.getIconPacksLayout().remove(id);
                if (!ExteraConfig.getIconPacksHidden().contains(id)) {
                    ExteraConfig.getIconPacksHidden().add(0, id);
                }
            } else if (ExteraConfig.getIconPacksHidden().contains(id)) {
                ExteraConfig.getIconPacksHidden().remove(id);
                ExteraConfig.getIconPacksLayout().add(0, id);
            }
            ExteraConfig.saveIconPacksLayout();
            IconManager.INSTANCE.initialize(true);
            this.listView.adapter.update(true);
        }
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
        Object obj = uItem.object;
        if (obj instanceof IconPack) {
            final IconPack iconPack = (IconPack) obj;
            String id = iconPack.getId();
            if (id.startsWith("base.")) {
                return false;
            }
            ItemOptions.makeOptions(this, view).addIf(ExteraConfig.getIconPacksLayout().contains(id), R.drawable.msg_edit, LocaleController.getString(R.string.Edit), new Runnable() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    IconPacksActivity.this.lambda$onLongClick$3(iconPack);
                }
            }).add(R.drawable.msg_share, LocaleController.getString(R.string.ShareFile), new Runnable() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    IconPacksActivity.this.lambda$onLongClick$6(iconPack);
                }
            }).add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.Delete), true, new Runnable() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    IconPacksActivity.this.lambda$onLongClick$8(iconPack);
                }
            }).setScrimViewBackground(this.listView.getClipBackground(view)).show();
            return true;
        }
        return super.onLongClick(uItem, view, i, f, f2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClick$3(IconPack iconPack) {
        ExteraConfig.setEditingIconPackId(iconPack.getId());
        IconPickerController.setActive((LaunchActivity) getParentActivity(), true);
        presentFragment(new IconPacksEditorActivity(iconPack));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClick$6(final IconPack iconPack) {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                IconPacksActivity.this.lambda$onLongClick$5(iconPack);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClick$5(IconPack iconPack) {
        final File fileBundlePackBlocking = IconManager.INSTANCE.bundlePackBlocking(iconPack.getId());
        if (fileBundlePackBlocking != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    IconPacksActivity.this.lambda$onLongClick$4(fileBundlePackBlocking);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClick$4(File file) {
        if (getParentActivity() == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("application/zip");
        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), ApplicationLoader.getApplicationId() + ".provider", file));
        intent.addFlags(1);
        getParentActivity().startActivity(Intent.createChooser(intent, LocaleController.getString(R.string.ShareFile)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLongClick$8(final IconPack iconPack) {
        AlertDialog alertDialogCreate = new AlertDialog.Builder(getParentActivity(), getResourceProvider()).setTitle(LocaleController.getString(R.string.DeletePack)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.DeletePackInfo, iconPack.getName()))).setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.icons.ui.IconPacksActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                IconManager.INSTANCE.deletePack(iconPack.getId());
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
        alertDialogCreate.show();
        TextView textView = (TextView) alertDialogCreate.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }
}
