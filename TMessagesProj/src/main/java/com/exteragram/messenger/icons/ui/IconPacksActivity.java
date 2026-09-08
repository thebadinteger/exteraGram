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

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalRecyclerView universalRecyclerView;
        UniversalAdapter universalAdapter;
        if (i != NotificationCenter.iconPackUpdated || (universalRecyclerView = this.listView) == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    @Override 
    public void onInsets(int i, int i2, int i3, int i4) {
        this.floatingButton.setTranslationY(-i4);
        super.onInsets(i, i2, i3, i4);
    }

    @Override 
    public View createView(Context context) {
        View viewCreateView = super.createView(context);
        FragmentFloatingButton fragmentFloatingButton = new FragmentFloatingButton(context, this.resourceProvider);
        this.floatingButton = fragmentFloatingButton;
        fragmentFloatingButton.imageView.setImageResource(R.drawable.msg_add);
        this.floatingButton.setContentDescription(LocaleController.getString(R.string.Add));
        this.floatingButton.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createView$0(view);
            }
        });
        if (viewCreateView instanceof FrameLayout) {
            ((FrameLayout) viewCreateView).addView(this.floatingButton, FragmentFloatingButton.createDefaultLayoutParams());
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.allowReorder(true);
            this.listView.setReorderHandleOnly(true);
            this.listView.listenReorder(new Utilities.Callback2() { 
                @Override 
                public final void run(Object obj, Object obj2) {
                    this.f$0.updateConfigFromReorder(((Integer) obj).intValue(), (ArrayList) obj2);
                }
            });
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { 
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

    public void lambda$onLongClick$3(IconPack iconPack) {
        ExteraConfig.setEditingIconPackId(iconPack.getId());
        IconPickerController.setActive((LaunchActivity) getParentActivity(), true);
        presentFragment(new IconPacksEditorActivity(iconPack));
    }

    public void lambda$onLongClick$5(IconPack iconPack) {
        final File fileBundlePackBlocking = IconManager.INSTANCE.bundlePackBlocking(iconPack.getId());
        if (fileBundlePackBlocking != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onLongClick$4(fileBundlePackBlocking);
                }
            });
        }
    }

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

    public /* synthetic */ void lambda$onLongClick$8(final IconPack iconPack) {
        AlertDialog alertDialogCreate = new AlertDialog.Builder(getParentActivity(), getResourceProvider()).setTitle(LocaleController.getString(R.string.DeletePack)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.DeletePackInfo, iconPack.getName()))).setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() { 
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
