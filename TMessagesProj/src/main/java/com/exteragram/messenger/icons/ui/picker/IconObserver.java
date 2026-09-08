package com.exteragram.messenger.icons.ui.picker;

import android.app.Activity;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.LaunchActivity;

@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010#\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\"\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\bJ\u000e\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u0006J\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\b0\u000fJ\u0006\u0010\u0010\u001a\u00020\nR \u0010\u0004\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"}, d2 = {"Lcom/exteragram/messenger/icons/ui/picker/IconObserver;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "iconSources", "Ljava/util/WeakHashMap;", "Lorg/telegram/ui/ActionBar/BaseFragment;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "log", _UrlKt.FRAGMENT_ENCODE_SET, "resId", "removeSource", "owner", "getUsedIcons", _UrlKt.FRAGMENT_ENCODE_SET, "clear", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nIconObserver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IconObserver.kt\ncom/exteragram/messenger/icons/ui/picker/IconObserver\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,50:1\n410#2,7:51\n1391#3:58\n1480#3,5:59\n*S KotlinDebug\n*F\n+ 1 IconObserver.kt\ncom/exteragram/messenger/icons/ui/picker/IconObserver\n*L\n23#1:51,7\n39#1:58\n39#1:59,5\n*E\n"})
public final class IconObserver {
    public static final IconObserver INSTANCE = new IconObserver();
    private static final WeakHashMap<BaseFragment, Set<Integer>> iconSources = new WeakHashMap<>();

    private IconObserver() {
    }

    public final void log(int resId) {
        BaseFragment safeLastFragment;
        if (ExteraConfig.getEditingIconPackId() == null || (safeLastFragment = LaunchActivity.getSafeLastFragment()) == null) {
            return;
        }
        Activity parentActivity = safeLastFragment.getParentActivity();
        LaunchActivity launchActivity = parentActivity instanceof LaunchActivity ? (LaunchActivity) parentActivity : null;
        if (launchActivity != null) {
            IconPickerController.setActive(launchActivity, true);
        }
        WeakHashMap<BaseFragment, Set<Integer>> weakHashMap = iconSources;
        synchronized (weakHashMap) {
            try {
                Set<Integer> hashSet = weakHashMap.get(safeLastFragment);
                if (hashSet == null) {
                    hashSet = new HashSet<>();
                    weakHashMap.put(safeLastFragment, hashSet);
                }
                hashSet.add(Integer.valueOf(resId));
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void removeSource(BaseFragment owner) {
        WeakHashMap<BaseFragment, Set<Integer>> weakHashMap = iconSources;
        synchronized (weakHashMap) {
            weakHashMap.remove(owner);
        }
    }

    public final Set<Integer> getUsedIcons() {
        Set<Integer> set;
        List<BaseFragment> visibleFragments = LaunchActivity.getVisibleFragments();
        synchronized (iconSources) {
            try {
                ArrayList arrayList = new ArrayList();
                Iterator<T> it = visibleFragments.iterator();
                while (it.hasNext()) {
                    Set<Integer> setEmptySet = iconSources.get((BaseFragment) it.next());
                    if (setEmptySet == null) {
                        setEmptySet = SetsKt.emptySet();
                    }
                    CollectionsKt.addAll(arrayList, setEmptySet);
                }
                set = CollectionsKt.toSet(arrayList);
            } catch (Throwable th) {
                throw th;
            }
        }
        return set;
    }

    public final void clear() {
        WeakHashMap<BaseFragment, Set<Integer>> weakHashMap = iconSources;
        synchronized (weakHashMap) {
            weakHashMap.clear();
            Unit unit = Unit.INSTANCE;
        }
    }
}
