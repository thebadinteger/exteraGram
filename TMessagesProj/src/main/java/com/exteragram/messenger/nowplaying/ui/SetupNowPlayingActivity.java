package com.exteragram.messenger.nowplaying.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.media.MediaRouteProviderProtocol;
import com.exteragram.messenger.api.dto.NowPlayingInfoDTO;
import com.exteragram.messenger.api.model.NowPlayingServiceType;
import com.exteragram.messenger.nowplaying.NowPlayingController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogRadioCell;
import org.telegram.ui.Cells.EditTextCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

@Metadata(d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001)B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J(\u0010\u0013\u001a\u00020\u00142\u0016\u0010\u0015\u001a\u0012\u0012\u0004\u0012\u00020\u00170\u0016j\b\u0012\u0004\u0012\u00020\u0017`\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J0\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u00172\u0006\u0010\u001d\u001a\u00020\u00102\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!H\u0002J\u0010\u0010$\u001a\u00020\u00142\u0006\u0010%\u001a\u00020&H\u0002J\u0010\u0010'\u001a\u00020\u00142\u0006\u0010(\u001a\u00020&H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u001fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006*"}, d2 = {"Lcom/exteragram/messenger/nowplaying/ui/SetupNowPlayingActivity;", "Lorg/telegram/ui/ActionBar/BaseFragment;", "<init>", "()V", "nowPlayingEdit", "Lorg/telegram/ui/Cells/EditTextCell;", "listView", "Lorg/telegram/ui/Components/UniversalRecyclerView;", "initialState", "Lcom/exteragram/messenger/nowplaying/ui/SetupNowPlayingActivity$NowPlayingState;", "currentState", "doneButtonDrawable", "Lorg/telegram/ui/Components/CrossfadeDrawable;", "doneButton", "Lorg/telegram/ui/ActionBar/ActionBarMenuItem;", "createView", "Landroid/view/View;", "context", "Landroid/content/Context;", "fillItems", _UrlKt.FRAGMENT_ENCODE_SET, "items", "Ljava/util/ArrayList;", "Lorg/telegram/ui/Components/UItem;", "Lkotlin/collections/ArrayList;", "adapter", "Lorg/telegram/ui/Components/UniversalAdapter;", "onClick", "item", "view", "position", _UrlKt.FRAGMENT_ENCODE_SET, "x", _UrlKt.FRAGMENT_ENCODE_SET, "y", "shiftDp", "processDone", MediaRouteProviderProtocol.SERVICE_DATA_ERROR, _UrlKt.FRAGMENT_ENCODE_SET, "checkDone", "animated", "NowPlayingState", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nSetupNowPlayingActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SetupNowPlayingActivity.kt\ncom/exteragram/messenger/nowplaying/ui/SetupNowPlayingActivity\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,239:1\n1#2:240\n37#3,2:241\n*S KotlinDebug\n*F\n+ 1 SetupNowPlayingActivity.kt\ncom/exteragram/messenger/nowplaying/ui/SetupNowPlayingActivity\n*L\n181#1:241,2\n*E\n"})
public final class SetupNowPlayingActivity extends BaseFragment {
    private ActionBarMenuItem doneButton;
    private CrossfadeDrawable doneButtonDrawable;
    private NowPlayingState initialState;
    private UniversalRecyclerView listView;
    private EditTextCell nowPlayingEdit;
    private NowPlayingState currentState = new NowPlayingState(NowPlayingServiceType.NONE, null);
    private int shiftDp = -4;

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\b\b\u0082\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004¢\u0006\u0004\b\u0006\u0010\u0007J&\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0004HÆ\u0001¢\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004HÖ\u0001¢\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\r\u001a\u00020\fHÖ\u0001¢\u0006\u0004\b\r\u0010\u000eJ\u001a\u0010\u0011\u001a\u00020\u00102\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0013\u001a\u0004\b\u0014\u0010\u0015R\u0019\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0006¢\u0006\f\n\u0004\b\u0005\u0010\u0016\u001a\u0004\b\u0017\u0010\u000b¨\u0006\u0018"}, d2 = {"Lcom/exteragram/messenger/nowplaying/ui/SetupNowPlayingActivity$NowPlayingState;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/api/model/NowPlayingServiceType;", "serviceType", _UrlKt.FRAGMENT_ENCODE_SET, "username", "<init>", "(Lcom/exteragram/messenger/api/model/NowPlayingServiceType;Ljava/lang/String;)V", "copy", "(Lcom/exteragram/messenger/api/model/NowPlayingServiceType;Ljava/lang/String;)Lcom/exteragram/messenger/nowplaying/ui/SetupNowPlayingActivity$NowPlayingState;", "toString", "()Ljava/lang/String;", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", "()I", "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "Lcom/exteragram/messenger/api/model/NowPlayingServiceType;", "getServiceType", "()Lcom/exteragram/messenger/api/model/NowPlayingServiceType;", "Ljava/lang/String;", "getUsername", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class NowPlayingState {
        private final NowPlayingServiceType serviceType;
        private final String username;

        public static /* synthetic */ NowPlayingState copy$default(NowPlayingState nowPlayingState, NowPlayingServiceType nowPlayingServiceType, String str, int i, Object obj) {
            if ((i & 1) != 0) {
                nowPlayingServiceType = nowPlayingState.serviceType;
            }
            if ((i & 2) != 0) {
                str = nowPlayingState.username;
            }
            return nowPlayingState.copy(nowPlayingServiceType, str);
        }

        public final NowPlayingState copy(NowPlayingServiceType serviceType, String username) {
            return new NowPlayingState(serviceType, username);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof NowPlayingState)) {
                return false;
            }
            NowPlayingState nowPlayingState = (NowPlayingState) other;
            return this.serviceType == nowPlayingState.serviceType && Intrinsics.areEqual(this.username, nowPlayingState.username);
        }

        public int hashCode() {
            int iHashCode = this.serviceType.hashCode() * 31;
            String str = this.username;
            return iHashCode + (str == null ? 0 : str.hashCode());
        }

        public String toString() {
            return "NowPlayingState(serviceType=" + this.serviceType + ", username=" + this.username + ')';
        }

        public NowPlayingState(NowPlayingServiceType nowPlayingServiceType, String str) {
            this.serviceType = nowPlayingServiceType;
            this.username = str;
        }

        public final NowPlayingServiceType getServiceType() {
            return this.serviceType;
        }

        public final String getUsername() {
            return this.username;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.NowPlaying));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { 
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id != -1) {
                    if (id != 0) {
                        return;
                    }
                    SetupNowPlayingActivity.this.processDone(true);
                } else if (SetupNowPlayingActivity.this.onBackPressed(true)) {
                    SetupNowPlayingActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
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
        }, null);
        this.listView = universalRecyclerView;
        universalRecyclerView.setSections();
        ActionBar actionBar = this.actionBar;
        UniversalRecyclerView universalRecyclerView2 = this.listView;
        if (universalRecyclerView2 == null) {
            universalRecyclerView2 = null;
        }
        actionBar.setAdaptiveBackground(universalRecyclerView2);
        UniversalRecyclerView universalRecyclerView3 = this.listView;
        if (universalRecyclerView3 == null) {
            universalRecyclerView3 = null;
        }
        universalRecyclerView3.adapter.setApplyBackground(false);
        UniversalRecyclerView universalRecyclerView4 = this.listView;
        if (universalRecyclerView4 == null) {
            universalRecyclerView4 = null;
        }
        frameLayout.addView(universalRecyclerView4, LayoutHelper.createFrame(-1, -1.0f));
        EditTextCell editTextCell = new EditTextCell(context, LocaleController.getString(R.string.Username), this.resourceProvider) { 
            @Override // org.telegram.ui.Cells.EditTextCell
            public void onTextChanged(CharSequence newText) {
                super.onTextChanged(newText);
                SetupNowPlayingActivity setupNowPlayingActivity = this;
                setupNowPlayingActivity.currentState = NowPlayingState.copy$default(setupNowPlayingActivity.currentState, null, newText.toString(), 1, null);
                this.checkDone(true);
            }
        };
        this.nowPlayingEdit = editTextCell;
        editTextCell.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        EditTextCell editTextCell2 = this.nowPlayingEdit;
        if (editTextCell2 == null) {
            editTextCell2 = null;
        }
        editTextCell2.hideKeyboardOnEnter();
        NowPlayingController.getNowPlayingInfo(new Consumer() { 
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        SetupNowPlayingActivity.createView$lambda$2$0(nowPlayingInfoDTO, setupNowPlayingActivity);
                    }
                });
            }
        });
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_ab_done);
        Drawable drawableMutate = drawable != null ? drawable.mutate() : null;
        if (drawableMutate != null) {
            drawableMutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        }
        this.doneButtonDrawable = new CrossfadeDrawable(drawableMutate, new CircularProgressDrawable(Theme.getColor(Theme.key_actionBarDefaultIcon)));
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(0, this.doneButtonDrawable, AndroidUtilities.dp(56.0f), LocaleController.getString(R.string.Done));
        checkDone(false);
        this.fragmentView = frameLayout;
        return frameLayout;
    }

    public static final void createView$lambda$2$0(NowPlayingInfoDTO nowPlayingInfoDTO, SetupNowPlayingActivity setupNowPlayingActivity) {
        NowPlayingState nowPlayingState;
        String str = _UrlKt.FRAGMENT_ENCODE_SET;
        if (nowPlayingInfoDTO != null) {
            NowPlayingServiceType serviceType = nowPlayingInfoDTO.getServiceType();
            String username = nowPlayingInfoDTO.getUsername();
            if (username != null) {
                str = username;
            }
            nowPlayingState = new NowPlayingState(serviceType, str);
        } else {
            nowPlayingState = new NowPlayingState(NowPlayingServiceType.NONE, _UrlKt.FRAGMENT_ENCODE_SET);
        }
        setupNowPlayingActivity.currentState = nowPlayingState;
        setupNowPlayingActivity.initialState = nowPlayingState;
        EditTextCell editTextCell = setupNowPlayingActivity.nowPlayingEdit;
        if (editTextCell == null) {
            editTextCell = null;
        }
        editTextCell.setText(nowPlayingState.getUsername());
        UniversalRecyclerView universalRecyclerView = setupNowPlayingActivity.listView;
        (universalRecyclerView != null ? universalRecyclerView : null).adapter.update(true);
        setupNowPlayingActivity.checkDone(false);
    }

    public final void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader(LocaleController.getString(R.string.SelectService)));
        Iterator<NowPlayingServiceType> it = NowPlayingServiceType.getEntries().iterator();
        while (it.hasNext()) {
            NowPlayingServiceType next = it.next();
            items.add(UItem.asRadio(next.ordinal(), next.getDisplayName()).setChecked(next == this.currentState.getServiceType() && this.initialState != null));
        }
        items.add(UItem.asShadow(LocaleController.getString(R.string.SelectServiceInfo)));
        if (this.currentState.getServiceType() != NowPlayingServiceType.NONE) {
            items.add(UItem.asHeader(LocaleController.getString(R.string.EnterUsername)));
            EditTextCell editTextCell = this.nowPlayingEdit;
            if (editTextCell == null) {
                editTextCell = null;
            }
            items.add(UItem.asCustom(editTextCell));
            items.add(UItem.asShadow(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.EnterUsernameInfo), new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    SetupNowPlayingActivity setupNowPlayingActivity = this.f$0;
                    Browser.openUrl(setupNowPlayingActivity.getParentActivity(), setupNowPlayingActivity.currentState.getServiceType() == NowPlayingServiceType.LAST_FM ? "https://www.last.fm/" : "https://stats.fm/");
                }
            })));
        }
    }

    public final void onClick(UItem item, View view, int position, float x, float y) {
        if (view instanceof DialogRadioCell) {
            this.currentState = NowPlayingState.copy$default(this.currentState, ((NowPlayingServiceType[]) NowPlayingServiceType.getEntries().toArray(new NowPlayingServiceType[0]))[item.id], null, 2, null);
            UniversalRecyclerView universalRecyclerView = this.listView;
            (universalRecyclerView != null ? universalRecyclerView : null).adapter.update(true);
            checkDone(true);
        }
    }

    public final void processDone(boolean error) {
        if (this.doneButtonDrawable.getProgress() > 0.0f) {
            return;
        }
        if (error && this.currentState.getServiceType() != NowPlayingServiceType.NONE) {
            EditTextCell editTextCell = this.nowPlayingEdit;
            if (editTextCell == null) {
                editTextCell = null;
            }
            CharSequence text = editTextCell.getText();
            if (text == null || text.length() == 0) {
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
                EditTextCell editTextCell2 = this.nowPlayingEdit;
                EditTextCell editTextCell3 = editTextCell2 != null ? editTextCell2 : null;
                int i = -this.shiftDp;
                this.shiftDp = i;
                AndroidUtilities.shakeViewSpring(editTextCell3, i);
                return;
            }
        }
        this.doneButtonDrawable.animateToProgress(1.0f);
        final NowPlayingInfoDTO nowPlayingInfoDTO = new NowPlayingInfoDTO(this.currentState.getServiceType(), this.currentState.getServiceType() == NowPlayingServiceType.NONE ? null : this.currentState.getUsername());
        NowPlayingController.updateNowPlayingInfo$default(nowPlayingInfoDTO, false, new Consumer() { 
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        SetupNowPlayingActivity.processDone$lambda$1$0(bool, setupNowPlayingActivity, nowPlayingInfoDTO);
                    }
                });
            }
        }, 2, null);
    }

    public static final void processDone$lambda$1$0(Boolean bool, SetupNowPlayingActivity setupNowPlayingActivity, NowPlayingInfoDTO nowPlayingInfoDTO) {
        if (!bool.booleanValue()) {
            setupNowPlayingActivity.doneButtonDrawable.animateToProgress(0.0f);
            BulletinFactory.of(setupNowPlayingActivity).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
        } else {
            setupNowPlayingActivity.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.nowPlayingUpdated, nowPlayingInfoDTO.getServiceType());
            setupNowPlayingActivity.finishFragment();
        }
    }

    public final void checkDone(boolean animated) {
        if (this.doneButton == null) {
            return;
        }
        boolean zAreEqual = Intrinsics.areEqual(this.initialState, this.currentState);
        this.doneButton.setEnabled(!zAreEqual);
        ActionBarMenuItem actionBarMenuItem = this.doneButton;
        if (animated) {
            actionBarMenuItem.animate().alpha(!zAreEqual ? 1.0f : 0.0f).scaleX(!zAreEqual ? 1.0f : 0.0f).scaleY(zAreEqual ? 0.0f : 1.0f).setDuration(180L).start();
        } else if (actionBarMenuItem != null) {
            actionBarMenuItem.setAlpha(!zAreEqual ? 1.0f : 0.0f);
            actionBarMenuItem.setScaleX(!zAreEqual ? 1.0f : 0.0f);
            actionBarMenuItem.setScaleY(zAreEqual ? 0.0f : 1.0f);
        }
    }
}
