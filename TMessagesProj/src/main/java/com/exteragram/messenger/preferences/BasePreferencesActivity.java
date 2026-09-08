package com.exteragram.messenger.preferences;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.exteragram.messenger.utils.ui.PopupUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.ToIntFunction;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public abstract class BasePreferencesActivity extends BaseFragment {
    protected LinearLayoutManager layoutManager;
    protected UniversalRecyclerView listView;

    public abstract void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter);

    public abstract String getTitle();

    public boolean hasHeaderCell() {
        return false;
    }

    public boolean hasWhiteActionBar() {
        return false;
    }

    public void initializeOptionStrings() {
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    public boolean needHideTitle() {
        return false;
    }

    public abstract void onClick(UItem uItem, View view, int i, float f, float f2);

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        initializeOptionStrings();
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setTitle(getTitle());
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { 
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    BasePreferencesActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        if (actionBar.menu == null) {
            actionBar.createMenu();
        }
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() { 
            @Override 
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                this.f$0.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, new Utilities.Callback5Return() { 
            @Override 
            public final Object run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                return Boolean.valueOf(this.f$0.onLongClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue()));
            }
        });
        this.listView = universalRecyclerView;
        universalRecyclerView.setSections();
        if (!hasHeaderCell()) {
            this.actionBar.setAdaptiveBackground(this.listView, needHideTitle());
        }
        this.listView.adapter.setApplyBackground(false);
        this.listView.setClipToPadding(false);
        UniversalRecyclerView universalRecyclerView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        universalRecyclerView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.fragmentView = frameLayout;
        return frameLayout;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        this.listView.adapter.update(false);
        Bulletin.addDelegate(this, new Bulletin.Delegate() { 
            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public int getTopOffset(int i) {
                if (BasePreferencesActivity.this.hasHeaderCell()) {
                    return AndroidUtilities.statusBarHeight;
                }
                return 0;
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public int getBottomOffset(int i) {
                return BasePreferencesActivity.this.getBottomInset();
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        Bulletin.removeDelegate(this);
    }

    public void scrollToItem(final int i) {
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || universalRecyclerView.adapter == null || this.layoutManager == null) {
            return;
        }
        int iFindPositionByItemId = universalRecyclerView.findPositionByItemId(i);
        if (iFindPositionByItemId >= 0 && iFindPositionByItemId < this.listView.adapter.getItemCount()) {
            this.layoutManager.scrollToPositionWithOffset(iFindPositionByItemId, AndroidUtilities.dp(80.0f));
            this.listView.highlightRow(new RecyclerListView.IntReturnCallback() { 
                @Override // org.telegram.ui.Components.RecyclerListView.IntReturnCallback
                public final int run() {
                    return this.f$0.lambda$scrollToItem$0(i);
                }
            });
        } else {
            SettingsRegistry.getInstance().onSettingNotFound(this);
        }
    }

    public void lambda$showListDialog$1(boolean z, int i, PopupUtils.OnItemClickListener onItemClickListener, UItem uItem, CharSequence[] charSequenceArr, int i2) {
        if (z && i == i2) {
            return;
        }
        onItemClickListener.onClick(i2);
        View viewFindViewByItemId = this.listView.findViewByItemId(uItem.id);
        if (viewFindViewByItemId instanceof TextCell) {
            ((TextCell) viewFindViewByItemId).setValue(charSequenceArr[i2], true);
        }
        this.listView.adapter.update(true);
    }

    public void showRestartBulletin() {
        BulletinFactory.of(this).createSimpleBulletin(R.raw.info, LocaleController.getString(R.string.RestartRequired), LocaleController.getString(R.string.BotUnblock), new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showRestartBulletin$2();
            }
        }).show();
    }

    public void lambda$showCopyLinkOptions$3(String str) {
        if (AndroidUtilities.addToClipboard(str)) {
            BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.LinkCopied)).show();
        }
    }

    public /* synthetic */ void lambda$showCopyLinkOptions$4(String str) {
        showDialog(new ShareAlert(getContext(), null, str, false, str, false, getResourceProvider()) { 
            @Override // org.telegram.ui.Components.ShareAlert
            public void onSend(LongSparseArray<TLRPC.Dialog> longSparseArray, int i, TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
                String string;
                if (z) {
                    if (longSparseArray != null && longSparseArray.size() == 1) {
                        long j = longSparseArray.valueAt(0).id;
                        if (j == 0 || j == BasePreferencesActivity.this.getUserConfig().getClientUserId()) {
                            string = LocaleController.getString(R.string.SettingLinkToSavedMessages);
                        } else {
                            string = LocaleController.formatString(R.string.SettingLinkToUser, BasePreferencesActivity.this.getMessagesController().getPeerName(j, true));
                        }
                    } else {
                        string = LocaleController.formatString(R.string.SettingLinkToChats, LocaleController.formatPluralString("Chats", i, new Object[0]));
                    }
                    Bulletin bulletinCreateSimpleBulletin = BulletinFactory.of(BasePreferencesActivity.this).createSimpleBulletin(R.raw.forward, string);
                    bulletinCreateSimpleBulletin.hideAfterBottomSheet = false;
                    bulletinCreateSimpleBulletin.show(true);
                }
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onInsets(int i, int i2, int i3, int i4) {
        this.listView.setPadding(0, 0, 0, i4);
        this.listView.setClipToPadding(false);
    }
}
