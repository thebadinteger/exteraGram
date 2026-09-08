package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.ProxyDisableCondition;
import com.exteragram.messenger.proxy.ProxyController;
import com.exteragram.messenger.utils.ui.PopupUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.ProxyRotationController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestTimeDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;

public class ProxyListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final ProxyDisableCondition[] PROXY_DISABLE_CONDITIONS = ProxyDisableCondition.values();
    private int callsDetailRow;

    @Keep
    private int callsRow;
    private boolean checkedProxies;
    private int connectionsHeaderRow;
    private int currentConnectionState;
    private int deleteAllRow;
    private ActionBarMenuItem deleteMenuItem;
    private ItemTouchHelper itemTouchHelper;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int mainHeaderRow;
    private ActionBarMenuItem pinMenuItem;

    @Keep
    private int proxyAddRow;
    private int proxyDisableRow;
    private int proxyDisableShadowRow;
    private int proxyEndRow;
    private int proxyShadowRow;
    private int proxyStartRow;
    private boolean refreshActionEnabled;
    private int rotationRow;
    private int rotationTimeoutInfoRow;
    private int rotationTimeoutRow;
    private int rowCount;
    private NumberTextView selectedCountTextView;
    private ActionBarMenuItem shareMenuItem;
    private boolean useProxyForCalls;

    @Keep
    private int useProxyRow;
    private boolean useProxySettings;
    private int useProxyShadowRow;
    private List<SharedConfig.ProxyInfo> selectedItems = new ArrayList();
    private List<SharedConfig.ProxyInfo> proxyList = new ArrayList();
    private final View.OnClickListener refreshActionClickListener = new View.OnClickListener() { // from class: org.telegram.ui.ProxyListActivity$$ExternalSyntheticLambda7
        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            this.f$0.lambda$new$0(view);
        }
    };

    public void lambda$showProxyDisableDialog$5(boolean[] zArr) {
        int i = 0;
        while (true) {
            ProxyDisableCondition[] proxyDisableConditionArr = PROXY_DISABLE_CONDITIONS;
            if (i >= proxyDisableConditionArr.length) {
                break;
            }
            ExteraConfig.setProxyDisabledOn(proxyDisableConditionArr[i], zArr[i]);
            i++;
        }
        ApplicationLoader.checkProxyForNetworkState();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(this.proxyDisableRow);
        }
    }

    public static void lambda$didReceivedNotification$9(View view) {
        View view2 = this.listView.getChildViewHolder(view).itemView;
        if (view2 instanceof TextDetailProxyCell) {
            TextDetailProxyCell textDetailProxyCell = (TextDetailProxyCell) view2;
            textDetailProxyCell.setChecked(textDetailProxyCell.currentInfo == ProxyController.getInstance().getCurrentProxy());
            textDetailProxyCell.updateStatus();
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
            setHasStableIds(true);
        }

        public void toggleSelected(int i) {
            SharedConfig.ProxyInfo proxyAtPosition = ProxyListActivity.this.getProxyAtPosition(i);
            if (proxyAtPosition == null) {
                return;
            }
            boolean zContains = ProxyListActivity.this.selectedItems.contains(proxyAtPosition);
            ProxyListActivity proxyListActivity = ProxyListActivity.this;
            if (zContains) {
                proxyListActivity.selectedItems.remove(proxyAtPosition);
            } else {
                proxyListActivity.selectedItems.add(proxyAtPosition);
            }
            notifyItemChanged(i, 1);
            checkActionMode();
        }

        public void selectForReorder(int i) {
            SharedConfig.ProxyInfo proxyAtPosition = ProxyListActivity.this.getProxyAtPosition(i);
            if (proxyAtPosition == null) {
                return;
            }
            if (ProxyListActivity.this.selectedItems.size() != 1 || !ProxyListActivity.this.selectedItems.contains(proxyAtPosition)) {
                ProxyListActivity.this.selectedItems.clear();
                ProxyListActivity.this.selectedItems.add(proxyAtPosition);
                notifyItemRangeChanged(ProxyListActivity.this.proxyStartRow, ProxyListActivity.this.proxyEndRow - ProxyListActivity.this.proxyStartRow, 1);
            } else {
                notifyItemChanged(i, 1);
            }
            checkActionMode();
        }

        public void clearSelected() {
            ProxyListActivity.this.selectedItems.clear();
            notifyItemRangeChanged(ProxyListActivity.this.proxyStartRow, ProxyListActivity.this.proxyEndRow - ProxyListActivity.this.proxyStartRow, 1);
            checkActionMode();
        }

        private void checkActionMode() {
            int size = ProxyListActivity.this.selectedItems.size();
            boolean zIsActionModeShowed = ((BaseFragment) ProxyListActivity.this).actionBar.isActionModeShowed();
            if (size <= 0) {
                if (zIsActionModeShowed) {
                    ((BaseFragment) ProxyListActivity.this).actionBar.hideActionMode();
                    notifyItemRangeChanged(ProxyListActivity.this.proxyStartRow, ProxyListActivity.this.proxyEndRow - ProxyListActivity.this.proxyStartRow, 2);
                    return;
                }
                return;
            }
            ProxyListActivity.this.selectedCountTextView.setNumber(size, zIsActionModeShowed);
            ProxyListActivity.this.updatePinAction();
            if (zIsActionModeShowed) {
                return;
            }
            ((BaseFragment) ProxyListActivity.this).actionBar.showActionMode();
            notifyItemRangeChanged(ProxyListActivity.this.proxyStartRow, ProxyListActivity.this.proxyEndRow - ProxyListActivity.this.proxyStartRow, 2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ProxyListActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()) {
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    textSettingsCell.setBetterLayout(i == ProxyListActivity.this.proxyDisableRow);
                    if (i == ProxyListActivity.this.proxyAddRow) {
                        textSettingsCell.setText(LocaleController.getString(R.string.AddProxy), ProxyListActivity.this.deleteAllRow != -1);
                    } else if (i == ProxyListActivity.this.deleteAllRow) {
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
                        textSettingsCell.setText(LocaleController.getString(R.string.DeleteAllProxies), false);
                    } else if (i == ProxyListActivity.this.proxyDisableRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString(R.string.ProxyDisableOn), ProxyListActivity.this.getProxyDisableValue(), false);
                    }
                    break;
                case 2:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    SimpleTextView textView2 = headerCell.getTextView2();
                    if (textView2 != null) {
                        textView2.setOnClickListener(null);
                        textView2.setBackground(null);
                        textView2.setPadding(0, 0, 0, 0);
                        textView2.setTranslationY(0.0f);
                    }
                    if (i == ProxyListActivity.this.connectionsHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.ProxyConnections));
                        headerCell.setText2(LocaleController.getString(ProxyListActivity.this.hasCheckingProxies() ? R.string.Checking : R.string.Refresh));
                        if (textView2 != null) {
                            textView2.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                            textView2.setTranslationY(-AndroidUtilities.dpf2(4.0f));
                            textView2.setBackground(Theme.createSelectorDrawable(Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), 0.12f), 7));
                            ProxyListActivity.this.bindRefreshActionView(textView2);
                        }
                    } else if (i == ProxyListActivity.this.mainHeaderRow) {
                        headerCell.setText(LocaleController.getString(R.string.General));
                        headerCell.setText2(null);
                    }
                    break;
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i == ProxyListActivity.this.callsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString(R.string.UseProxyForCalls), ProxyListActivity.this.useProxyForCalls, false);
                    } else if (i == ProxyListActivity.this.rotationRow) {
                        String string = LocaleController.getString(R.string.UseProxyRotation);
                        boolean z = SharedConfig.proxyRotationEnabled;
                        textCheckCell.setTextAndCheck(string, z, z);
                    }
                    break;
                case 4:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ProxyListActivity.this.callsDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString(R.string.UseProxyForCallsInfo));
                    } else if (i == ProxyListActivity.this.rotationTimeoutInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString(R.string.ProxyRotationTimeoutInfo));
                    }
                    break;
                case 5:
                    TextDetailProxyCell textDetailProxyCell = (TextDetailProxyCell) viewHolder.itemView;
                    SharedConfig.ProxyInfo proxyInfo = (SharedConfig.ProxyInfo) ProxyListActivity.this.proxyList.get(i - ProxyListActivity.this.proxyStartRow);
                    textDetailProxyCell.setProxy(proxyInfo);
                    textDetailProxyCell.setChecked(ProxyController.getInstance().getCurrentProxy() == proxyInfo);
                    textDetailProxyCell.setItemSelected(ProxyListActivity.this.selectedItems.contains(ProxyListActivity.this.proxyList.get(i - ProxyListActivity.this.proxyStartRow)), false);
                    textDetailProxyCell.setReorderAvailable(ProxyListActivity.this.canShowReorderHandle(proxyInfo), false);
                    textDetailProxyCell.setSelectionEnabled(!ProxyListActivity.this.selectedItems.isEmpty(), false);
                    break;
                case 6:
                    if (i == ProxyListActivity.this.rotationTimeoutRow) {
                        SlideChooseView slideChooseView = (SlideChooseView) viewHolder.itemView;
                        ArrayList arrayList = new ArrayList(ProxyRotationController.ROTATION_TIMEOUTS);
                        String[] strArr = new String[arrayList.size()];
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            strArr[i2] = LocaleController.formatString(R.string.ProxyRotationTimeoutSeconds, arrayList.get(i2));
                        }
                        slideChooseView.setCallback(new SlideChooseView.Callback() { // from class: org.telegram.ui.ProxyListActivity$ListAdapter$$ExternalSyntheticLambda0
                            @Override // org.telegram.ui.Components.SlideChooseView.Callback
                            public final void onOptionSelected(int i3) {
                                ProxyListActivity.ListAdapter.$r8$lambda$qpOKhu4vpyVJ2fSGHKdC4SSLStA(i3);
                            }
                        });
                        slideChooseView.setOptions(SharedConfig.proxyRotationTimeout, strArr);
                    }
                    break;
                case 7:
                    TextCheckCell textCheckCell2 = (TextCheckCell) viewHolder.itemView;
                    textCheckCell2.setTextAndCheck(LocaleController.getString(R.string.UseProxySettings), ProxyListActivity.this.useProxySettings, false);
                    textCheckCell2.setBackgroundColor(Theme.getColor(ProxyListActivity.this.useProxySettings ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked));
                    break;
            }
        }

        public static /* synthetic */ void $r8$lambda$qpOKhu4vpyVJ2fSGHKdC4SSLStA(int i) {
            SharedConfig.proxyRotationTimeout = i;
            SharedConfig.saveConfig();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List list) {
            if (viewHolder.getItemViewType() == 5 && !list.isEmpty()) {
                TextDetailProxyCell textDetailProxyCell = (TextDetailProxyCell) viewHolder.itemView;
                SharedConfig.ProxyInfo proxyInfo = (SharedConfig.ProxyInfo) ProxyListActivity.this.proxyList.get(i - ProxyListActivity.this.proxyStartRow);
                if (list.contains(1)) {
                    textDetailProxyCell.setItemSelected(ProxyListActivity.this.selectedItems.contains(proxyInfo), true);
                    textDetailProxyCell.setReorderAvailable(ProxyListActivity.this.canShowReorderHandle(proxyInfo), true);
                }
                if (list.contains(2)) {
                    textDetailProxyCell.setReorderAvailable(ProxyListActivity.this.canShowReorderHandle(proxyInfo), true);
                    textDetailProxyCell.setSelectionEnabled(!ProxyListActivity.this.selectedItems.isEmpty(), true);
                    return;
                }
                return;
            }
            if ((viewHolder.getItemViewType() == 3 || viewHolder.getItemViewType() == 7) && list.contains(0)) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                int i2 = ProxyListActivity.this.useProxyRow;
                ProxyListActivity proxyListActivity = ProxyListActivity.this;
                if (i == i2) {
                    textCheckCell.setChecked(proxyListActivity.useProxySettings);
                    textCheckCell.setBackgroundColorAnimated(ProxyListActivity.this.useProxySettings, Theme.getColor(ProxyListActivity.this.useProxySettings ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked));
                    return;
                }
                int i3 = proxyListActivity.callsRow;
                ProxyListActivity proxyListActivity2 = ProxyListActivity.this;
                if (i == i3) {
                    textCheckCell.setChecked(proxyListActivity2.useProxyForCalls);
                    return;
                } else {
                    if (i == proxyListActivity2.rotationRow) {
                        textCheckCell.setChecked(SharedConfig.proxyRotationEnabled);
                        return;
                    }
                    return;
                }
            }
            super.onBindViewHolder(viewHolder, i, list);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 3 || itemViewType == 7) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                int adapterPosition = viewHolder.getAdapterPosition();
                int i = ProxyListActivity.this.useProxyRow;
                ProxyListActivity proxyListActivity = ProxyListActivity.this;
                if (adapterPosition == i) {
                    textCheckCell.setChecked(proxyListActivity.useProxySettings);
                    textCheckCell.setBackgroundColor(Theme.getColor(ProxyListActivity.this.useProxySettings ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked));
                    return;
                }
                int i2 = proxyListActivity.callsRow;
                ProxyListActivity proxyListActivity2 = ProxyListActivity.this;
                if (adapterPosition == i2) {
                    textCheckCell.setChecked(proxyListActivity2.useProxyForCalls);
                } else if (adapterPosition == proxyListActivity2.rotationRow) {
                    textCheckCell.setChecked(SharedConfig.proxyRotationEnabled);
                }
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition == ProxyListActivity.this.useProxyRow || adapterPosition == ProxyListActivity.this.proxyDisableRow || adapterPosition == ProxyListActivity.this.rotationRow || adapterPosition == ProxyListActivity.this.callsRow || adapterPosition == ProxyListActivity.this.proxyAddRow || adapterPosition == ProxyListActivity.this.deleteAllRow) {
                return true;
            }
            return adapterPosition >= ProxyListActivity.this.proxyStartRow && adapterPosition < ProxyListActivity.this.proxyEndRow;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View shadowSectionCell;
            View view;
            if (i == 0) {
                shadowSectionCell = new ShadowSectionCell(this.mContext);
            } else if (i != 1) {
                if (i == 2) {
                    HeaderCell headerCell = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 6, true);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = headerCell;
                } else if (i == 3) {
                    TextCheckCell textCheckCell = new TextCheckCell(this.mContext);
                    textCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    shadowSectionCell = textCheckCell;
                } else if (i == 4) {
                    shadowSectionCell = new TextInfoPrivacyCell(this.mContext);
                } else if (i == 6) {
                    SlideChooseView slideChooseView = new SlideChooseView(this.mContext);
                    slideChooseView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    shadowSectionCell = slideChooseView;
                } else if (i == 7) {
                    TextCheckCell textCheckCell2 = new TextCheckCell(this.mContext);
                    textCheckCell2.setDrawCheckRipple(true);
                    textCheckCell2.setColors(Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
                    textCheckCell2.setTypeface(AndroidUtilities.bold());
                    textCheckCell2.setHeight(56);
                    view = textCheckCell2;
                } else {
                    TextDetailProxyCell textDetailProxyCell = ProxyListActivity.this.new TextDetailProxyCell(this.mContext);
                    textDetailProxyCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    shadowSectionCell = textDetailProxyCell;
                }
                shadowSectionCell = view;
            } else {
                TextSettingsCell textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                shadowSectionCell = textSettingsCell;
            }
            shadowSectionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(shadowSectionCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            if (i == ProxyListActivity.this.useProxyShadowRow) {
                return -1L;
            }
            if (i == ProxyListActivity.this.proxyShadowRow) {
                return -2L;
            }
            if (i == ProxyListActivity.this.proxyAddRow) {
                return -3L;
            }
            if (i == ProxyListActivity.this.useProxyRow) {
                return -4L;
            }
            if (i == ProxyListActivity.this.mainHeaderRow) {
                return -16L;
            }
            if (i == ProxyListActivity.this.proxyDisableRow) {
                return -12L;
            }
            if (i == ProxyListActivity.this.proxyDisableShadowRow) {
                return -17L;
            }
            if (i == ProxyListActivity.this.callsRow) {
                return -5L;
            }
            if (i == ProxyListActivity.this.connectionsHeaderRow) {
                return -6L;
            }
            if (i == ProxyListActivity.this.deleteAllRow) {
                return -8L;
            }
            if (i == ProxyListActivity.this.rotationRow) {
                return -9L;
            }
            if (i == ProxyListActivity.this.rotationTimeoutRow) {
                return -10L;
            }
            if (i == ProxyListActivity.this.rotationTimeoutInfoRow) {
                return -11L;
            }
            if (i < ProxyListActivity.this.proxyStartRow || i >= ProxyListActivity.this.proxyEndRow) {
                return -7L;
            }
            return ((SharedConfig.ProxyInfo) ProxyListActivity.this.proxyList.get(i - ProxyListActivity.this.proxyStartRow)).hashCode();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == ProxyListActivity.this.useProxyShadowRow || i == ProxyListActivity.this.proxyDisableShadowRow || i == ProxyListActivity.this.proxyShadowRow) {
                return 0;
            }
            if (i == ProxyListActivity.this.proxyAddRow || i == ProxyListActivity.this.deleteAllRow || i == ProxyListActivity.this.proxyDisableRow) {
                return 1;
            }
            if (i == ProxyListActivity.this.useProxyRow) {
                return 7;
            }
            if (i == ProxyListActivity.this.rotationRow || i == ProxyListActivity.this.callsRow) {
                return 3;
            }
            if (i == ProxyListActivity.this.connectionsHeaderRow || i == ProxyListActivity.this.mainHeaderRow) {
                return 2;
            }
            if (i == ProxyListActivity.this.rotationTimeoutRow) {
                return 6;
            }
            return (i < ProxyListActivity.this.proxyStartRow || i >= ProxyListActivity.this.proxyEndRow) ? 4 : 5;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ProxyListActivity$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                this.f$0.updateVisibleCheckCellColors();
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailProxyCell.class}, null, null, null, i));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailProxyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText6));
        int i3 = Theme.key_windowBackgroundWhiteGrayText2;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGreenText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_text_RedRegular));
        int i4 = Theme.key_windowBackgroundWhiteGrayText3;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"checkImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"reorderImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, i));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, i2));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundChecked));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundUnchecked));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundCheckText));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_switchTrackBlue));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_switchTrackBlueChecked));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_switchTrackBlueThumb));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_switchTrackBlueThumbChecked));
        return arrayList;
    }
}
