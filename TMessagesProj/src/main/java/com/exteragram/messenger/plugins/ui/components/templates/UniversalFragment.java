package com.exteragram.messenger.plugins.ui.components.templates;

import android.content.Context;
import android.view.View;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;

@Metadata(d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\r\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\b\u0018\u00002\u00020\u0001:\u0001,B\u0013\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0006\u0010\r\u001a\u00020\u000eJ\n\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0014J \u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017J(\u0010\u0018\u001a\u00020\u00122\u0016\u0010\u0019\u001a\u0012\u0012\u0004\u0012\u00020\u001b0\u001aj\b\u0012\u0004\u0012\u00020\u001b`\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0014J0\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u001b2\u0006\u0010!\u001a\u00020\n2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020%H\u0014J0\u0010'\u001a\u00020\u00152\u0006\u0010 \u001a\u00020\u001b2\u0006\u0010!\u001a\u00020\n2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020%H\u0014J\b\u0010(\u001a\u00020\u0015H\u0016J\b\u0010)\u001a\u00020\u0012H\u0016J\u0010\u0010*\u001a\u00020\u00152\u0006\u0010+\u001a\u00020\u0015H\u0016R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\u0005¨\u0006-"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFragment;", "Lorg/telegram/ui/Components/UniversalFragment;", "delegate", "Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFragment$UniversalFragmentDelegate;", "<init>", "(Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFragment$UniversalFragmentDelegate;)V", "getDelegate", "()Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFragment$UniversalFragmentDelegate;", "setDelegate", "createView", "Landroid/view/View;", "context", "Landroid/content/Context;", "getActionBarMenu", "Lorg/telegram/ui/ActionBar/ActionBarMenu;", "getTitle", _UrlKt.FRAGMENT_ENCODE_SET, "setTitle", _UrlKt.FRAGMENT_ENCODE_SET, "title", "animated", _UrlKt.FRAGMENT_ENCODE_SET, "duration", _UrlKt.FRAGMENT_ENCODE_SET, "fillItems", "items", "Ljava/util/ArrayList;", "Lorg/telegram/ui/Components/UItem;", "Lkotlin/collections/ArrayList;", "adapter", "Lorg/telegram/ui/Components/UniversalAdapter;", "onClick", "item", "view", "position", _UrlKt.FRAGMENT_ENCODE_SET, "x", _UrlKt.FRAGMENT_ENCODE_SET, "y", "onLongClick", "onFragmentCreate", "onFragmentDestroy", "onBackPressed", "invoked", "UniversalFragmentDelegate", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class UniversalFragment extends org.telegram.ui.Components.UniversalFragment {
    private UniversalFragmentDelegate delegate;

    /* JADX WARN: Multi-variable type inference failed */
    public UniversalFragment() {
        this(null, 1, 0 == true ? 1 : 0);
    }

    public UniversalFragment(UniversalFragmentDelegate universalFragmentDelegate) {
        this.delegate = universalFragmentDelegate;
    }

    public /* synthetic */ UniversalFragment(UniversalFragmentDelegate universalFragmentDelegate, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : universalFragmentDelegate);
    }

    public final UniversalFragmentDelegate getDelegate() {
        return this.delegate;
    }

    public final void setDelegate(UniversalFragmentDelegate universalFragmentDelegate) {
        this.delegate = universalFragmentDelegate;
    }

    @Override // org.telegram.ui.Components.UniversalFragment, org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        UniversalFragmentDelegate universalFragmentDelegate = this.delegate;
        View viewBeforeCreateView = universalFragmentDelegate != null ? universalFragmentDelegate.beforeCreateView() : null;
        if (viewBeforeCreateView != null) {
            return viewBeforeCreateView;
        }
        View viewCreateView = super.createView(context);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { 
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                UniversalFragment universalFragment = UniversalFragment.this;
                if (id == -1) {
                    if (universalFragment.onBackPressed(true)) {
                        UniversalFragment.this.finishFragment();
                    }
                } else {
                    UniversalFragmentDelegate delegate = universalFragment.getDelegate();
                    if (delegate != null) {
                        delegate.onMenuItemClick(id);
                    }
                }
            }
        });
        UniversalFragmentDelegate universalFragmentDelegate2 = this.delegate;
        View viewAfterCreateView = universalFragmentDelegate2 != null ? universalFragmentDelegate2.afterCreateView(viewCreateView) : null;
        return viewAfterCreateView == null ? viewCreateView : viewAfterCreateView;
    }

    public final ActionBarMenu getActionBarMenu() {
        return this.actionBar.createMenu();
    }

    @Override // org.telegram.ui.Components.UniversalFragment
    public CharSequence getTitle() {
        UniversalFragmentDelegate universalFragmentDelegate = this.delegate;
        if (universalFragmentDelegate != null) {
            return universalFragmentDelegate.getTitle();
        }
        return null;
    }

    public final void setTitle(CharSequence title, boolean animated, long duration) {
        ActionBar actionBar = this.actionBar;
        if (animated) {
            if (duration <= 0) {
                duration = 300;
            }
            actionBar.setTitleAnimated(title, false, duration);
            return;
        }
        actionBar.setTitle(title);
    }

    @Override // org.telegram.ui.Components.UniversalFragment
    public void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        UniversalFragmentDelegate universalFragmentDelegate = this.delegate;
        if (universalFragmentDelegate != null) {
            universalFragmentDelegate.fillItems(items, adapter);
        }
    }

    @Override // org.telegram.ui.Components.UniversalFragment
    public void onClick(UItem item, View view, int position, float x, float y) {
        UniversalFragmentDelegate universalFragmentDelegate = this.delegate;
        if (universalFragmentDelegate != null) {
            universalFragmentDelegate.onClick(item, view, position, x, y);
        }
    }

    @Override // org.telegram.ui.Components.UniversalFragment
    public boolean onLongClick(UItem item, View view, int position, float x, float y) {
        UniversalFragmentDelegate universalFragmentDelegate = this.delegate;
        if (universalFragmentDelegate != null) {
            return universalFragmentDelegate.onLongClick(item, view, position, x, y);
        }
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        UniversalFragmentDelegate universalFragmentDelegate = this.delegate;
        if (universalFragmentDelegate != null) {
            universalFragmentDelegate.onFragmentCreate();
        }
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        UniversalFragmentDelegate universalFragmentDelegate = this.delegate;
        if (universalFragmentDelegate != null) {
            universalFragmentDelegate.onFragmentDestroy();
        }
        super.onFragmentDestroy();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed(boolean invoked) {
        UniversalFragmentDelegate universalFragmentDelegate = this.delegate;
        Boolean boolOnBackPressed = universalFragmentDelegate != null ? universalFragmentDelegate.onBackPressed() : null;
        if (boolOnBackPressed != null) {
            return !boolOnBackPressed.booleanValue();
        }
        return super.onBackPressed(invoked);
    }

    @Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\r\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u0016J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0005\u001a\u00020\u0003H\u0016J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\b\u0010\b\u001a\u00020\u0007H\u0016J\u000f\u0010\t\u001a\u0004\u0018\u00010\nH\u0016¢\u0006\u0002\u0010\u000bJ\n\u0010\f\u001a\u0004\u0018\u00010\rH\u0016J(\u0010\u000e\u001a\u00020\u00072\u0016\u0010\u000f\u001a\u0012\u0012\u0004\u0012\u00020\u00110\u0010j\b\u0012\u0004\u0012\u00020\u0011`\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J0\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001aH\u0016J0\u0010\u001c\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001aH\u0016J\u0010\u0010\u001d\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u0018H\u0016¨\u0006\u001fÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFragment$UniversalFragmentDelegate;", _UrlKt.FRAGMENT_ENCODE_SET, "beforeCreateView", "Landroid/view/View;", "afterCreateView", "view", "onFragmentCreate", _UrlKt.FRAGMENT_ENCODE_SET, "onFragmentDestroy", "onBackPressed", _UrlKt.FRAGMENT_ENCODE_SET, "()Ljava/lang/Boolean;", "getTitle", _UrlKt.FRAGMENT_ENCODE_SET, "fillItems", "items", "Ljava/util/ArrayList;", "Lorg/telegram/ui/Components/UItem;", "Lkotlin/collections/ArrayList;", "adapter", "Lorg/telegram/ui/Components/UniversalAdapter;", "onClick", "item", "position", _UrlKt.FRAGMENT_ENCODE_SET, "x", _UrlKt.FRAGMENT_ENCODE_SET, "y", "onLongClick", "onMenuItemClick", "id", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface UniversalFragmentDelegate {
        default View afterCreateView(View view) {
            return view;
        }

        default View beforeCreateView() {
            return null;
        }

        default void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        }

        default CharSequence getTitle() {
            return null;
        }

        default Boolean onBackPressed() {
            return null;
        }

        default void onClick(UItem item, View view, int position, float x, float y) {
        }

        default void onFragmentCreate() {
        }

        default void onFragmentDestroy() {
        }

        default boolean onLongClick(UItem item, View view, int position, float x, float y) {
            return false;
        }

        default void onMenuItemClick(int id) {
        }

        @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
        public static final class DefaultImpls {
            @Deprecated
            public static View beforeCreateView(UniversalFragmentDelegate universalFragmentDelegate) {
                return UniversalFragmentDelegate.super.beforeCreateView();
            }

            @Deprecated
            public static View afterCreateView(UniversalFragmentDelegate universalFragmentDelegate, View view) {
                return UniversalFragmentDelegate.super.afterCreateView(view);
            }

            @Deprecated
            public static void onFragmentCreate(UniversalFragmentDelegate universalFragmentDelegate) {
                UniversalFragmentDelegate.super.onFragmentCreate();
            }

            @Deprecated
            public static void onFragmentDestroy(UniversalFragmentDelegate universalFragmentDelegate) {
                UniversalFragmentDelegate.super.onFragmentDestroy();
            }

            @Deprecated
            public static Boolean onBackPressed(UniversalFragmentDelegate universalFragmentDelegate) {
                return UniversalFragmentDelegate.super.onBackPressed();
            }

            @Deprecated
            public static CharSequence getTitle(UniversalFragmentDelegate universalFragmentDelegate) {
                return UniversalFragmentDelegate.super.getTitle();
            }

            @Deprecated
            public static void fillItems(UniversalFragmentDelegate universalFragmentDelegate, ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
                UniversalFragmentDelegate.super.fillItems(arrayList, universalAdapter);
            }

            @Deprecated
            public static void onClick(UniversalFragmentDelegate universalFragmentDelegate, UItem uItem, View view, int i, float f, float f2) {
                UniversalFragmentDelegate.super.onClick(uItem, view, i, f, f2);
            }

            @Deprecated
            public static boolean onLongClick(UniversalFragmentDelegate universalFragmentDelegate, UItem uItem, View view, int i, float f, float f2) {
                return UniversalFragmentDelegate.super.onLongClick(uItem, view, i, f, f2);
            }

            @Deprecated
            public static void onMenuItemClick(UniversalFragmentDelegate universalFragmentDelegate, int i) {
                UniversalFragmentDelegate.super.onMenuItemClick(i);
            }
        }
    }
}
