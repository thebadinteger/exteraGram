package com.exteragram.messenger.preferences.utils;

import android.text.TextUtils;
import android.view.View;
import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.ai.ui.activities.AiPreferencesActivity;
import com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.ui.PluginsInfoActivity;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.exteragram.messenger.preferences.GeneralPreferencesActivity;
import com.exteragram.messenger.preferences.MainPreferencesActivity;
import com.exteragram.messenger.preferences.OtherPreferencesActivity;
import com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity;
import com.exteragram.messenger.preferences.appearance.AppearancePreferencesActivity;
import com.exteragram.messenger.preferences.chats.ChatsPreferencesActivity;
import com.exteragram.messenger.utils.text.LocaleUtils;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import kotlin.time.DurationKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;

public class SettingsRegistry {
    private static final Map<Class<? extends BaseFragment>, Integer> categoriesIcons = com.exteragram.messenger.utils.RecordUtils.mapOf(new Map.Entry[]{new AbstractMap.SimpleEntry(MainPreferencesActivity.class, Integer.valueOf(R.drawable.extera_outline)), new AbstractMap.SimpleEntry(GeneralPreferencesActivity.class, Integer.valueOf(R.drawable.msg_media)), new AbstractMap.SimpleEntry(AppearancePreferencesActivity.class, Integer.valueOf(R.drawable.msg_theme)), new AbstractMap.SimpleEntry(ChatsPreferencesActivity.class, Integer.valueOf(R.drawable.msg_discussion)), new AbstractMap.SimpleEntry(PluginsInfoActivity.class, Integer.valueOf(R.drawable.msg_plugins)), new AbstractMap.SimpleEntry(OtherPreferencesActivity.class, Integer.valueOf(R.drawable.msg_fave)), new AbstractMap.SimpleEntry(AiPreferencesActivity.class, Integer.valueOf(R.drawable.msg_bot)), new AbstractMap.SimpleEntry(AppNavigationPreferencesActivity.class, Integer.valueOf(R.drawable.msg_list)), new AbstractMap.SimpleEntry(PillStackPreferencesActivity.class, Integer.valueOf(R.drawable.outline_header_search))});
    public static List<String> newFeatures = java.util.Arrays.asList("customSavePath", "Camera-ExtendedSettings-StartWithWideAngle", "zoomSlider", "aiFeatures", "hideDialogsSearchBar", "Appearance-M3Styles-ChatHeader", "Appearance-M3Styles-NavigationBar", "Appearance-Sections", "glassOutlineStyle", "glassMessageMenu", "Feed-BottomTab", "aiTemperature", "AI-Service-Reasoning");
    private boolean entriesFetched;
    private String entriesLangCode;
    private final ConcurrentHashMap<Integer, Entry> preparedEntries = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Entry> entriesStringAlias = new ConcurrentHashMap<>();

    public static class SingletonHolder {
        private static final SettingsRegistry INSTANCE = new SettingsRegistry();
    }

    public static SettingsRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static boolean isValidForSearch(UItem uItem) {
        if (uItem.id != 0 && !TextUtils.isEmpty(uItem.text)) {
            return true;
        }
        Integer numValueOf = Integer.valueOf(uItem.id);
        Integer numValueOf2 = Integer.valueOf(uItem.viewType);
        View view = uItem.view;
        FileLog.e(String.format("[Extera] UItems with ID 0 or empty text cannot be added as search result. (UItem ID: %s; View type: %s; View: %s; Text: %s; Subtext: %s)", numValueOf, numValueOf2, view == null ? null : view.getClass().getName(), uItem.text, TextUtils.concat(uItem.subtext, uItem.animatedText)));
        return false;
    }

    public static boolean isValidForLinkAliases(UItem uItem) {
        int i = uItem.id;
        if (i != 0) {
            return true;
        }
        Integer numValueOf = Integer.valueOf(i);
        Integer numValueOf2 = Integer.valueOf(uItem.viewType);
        View view = uItem.view;
        FileLog.e(String.format("[Extera] Cannot set link aliases for UItems with ID 0. (UItem ID: %s; View type: %s; View: %s; Text: %s; Subtext: %s)", numValueOf, numValueOf2, view == null ? null : view.getClass().getName(), uItem.text, TextUtils.concat(uItem.subtext, uItem.animatedText)));
        return false;
    }

    public void addSearchEntry(BaseFragment baseFragment, UItem uItem) {
        if (isValidForSearch(uItem)) {
            Entry entryFromUItem = Entry.fromUItem(baseFragment, uItem);
            if (!this.preparedEntries.containsKey(Integer.valueOf(generateGUIDForUItem(baseFragment.getClass(), uItem)))) {
                FileLog.d("[Extera] Added an entry: " + entryFromUItem);
            }
            this.preparedEntries.putIfAbsent(Integer.valueOf(entryFromUItem.guid), entryFromUItem);
        }
    }

    public static boolean markAsNewFeature(String str) {
        if (!newFeatures.contains(str) || ExteraConfig.getDoNotMarkAsNew().contains(str)) {
            return false;
        }
        Long l = ExteraConfig.getNewFeaturesShowedAt().get(str);
        if (l == null || l.longValue() == 0) {
            ExteraConfig.getNewFeaturesShowedAt().put(str, Long.valueOf(System.currentTimeMillis()));
            ExteraConfig.getEditor().putString("newFeaturesShowedAt", ExteraConfig.getGSON().toJson(ExteraConfig.getNewFeaturesShowedAt())).apply();
            return true;
        }
        if (Math.abs(System.currentTimeMillis() - l.longValue()) <= DurationKt.MILLIS_IN_DAY) {
            return true;
        }
        ExteraConfig.getNewFeaturesShowedAt().remove(str);
        ExteraConfig.getEditor().putString("newFeaturesShowedAt", ExteraConfig.getGSON().toJson(ExteraConfig.getNewFeaturesShowedAt()));
        ExteraConfig.getDoNotMarkAsNew().add(str);
        ExteraConfig.getEditor().putString("doNotMarkAsNew", ExteraConfig.getGSON().toJson(ExteraConfig.getDoNotMarkAsNew())).apply();
        return false;
    }

    public void addLinkAliasForOption(String str, BaseFragment baseFragment, UItem uItem) {
        CharSequence charSequence;
        if (isValidForLinkAliases(uItem)) {
            if (markAsNewFeature(str) && (charSequence = uItem.text) != null && charSequence.length() > 0 && uItem.text.toString().charAt(uItem.text.toString().length() - 1) != 'd') {
                uItem.text = LocaleUtils.applyNewSpan(uItem.text.toString());
            }
            if (this.entriesStringAlias.containsKey(str)) {
                FileLog.d("[Extera] Key '" + str + "' already linked to an entry.");
                return;
            }
            Entry entryFromUItem = this.preparedEntries.get(Integer.valueOf(generateGUIDForUItem(baseFragment.getClass(), uItem)));
            if (entryFromUItem == null) {
                entryFromUItem = Entry.fromUItem(baseFragment, uItem);
            }
            FileLog.d(String.format("[Extera] Added link alias %s for an entry %s", str, entryFromUItem));
            this.entriesStringAlias.put(str, entryFromUItem);
        }
    }

    public void handleLink(String str, String str2) {
        FileLog.d("[Extera] Setting link handler called with alias " + str);
        if (str2 != null && !TextUtils.isEmpty(str2)) {
            PluginsController.openPluginSettings(str2, str);
            return;
        }
        createEntriesIfNeeded();
        Entry entry = this.entriesStringAlias.get(str);
        if (entry == null) {
            onSettingNotFound();
            return;
        }
        FileLog.d("[Extera] Found entry for alias: " + entry);
        FileLog.d("[Extera] Opening fragment...");
        openActivity(entry.fragmentClass, Integer.valueOf(entry.itemId));
    }

    public void onSettingNotFound() {
        onSettingNotFound(LaunchActivity.getLastFragment());
    }

    public void onSettingNotFound(BaseFragment baseFragment) {
        BulletinFactory.of(baseFragment).createEmojiBulletin("🤷\u200d♂️", LocaleController.getString(R.string.NoSuchSetting)).show();
    }

    public String getFirstSettingLink(Class<? extends BaseFragment> cls, UItem uItem) {
        final int iGenerateGUIDForUItem = generateGUIDForUItem(cls, uItem);
        Map.Entry<String, Entry> entryOrElse = this.entriesStringAlias.entrySet().stream().filter(new Predicate() { 
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return SettingsRegistry.$r8$lambda$p4BQB7CEiJF3IYJpUIO4QjqPKlE(iGenerateGUIDForUItem, (Map.Entry) obj);
            }
        }).findFirst().orElse(null);
        if (entryOrElse == null) {
            return null;
        }
        return "https://t.me/exteraSettings?s=" + entryOrElse.getKey();
    }

    public static ProfileActivity.SearchAdapter.SearchResult[] $r8$lambda$Uq61hhP3TlTJmft55hT5WjpJsx0(int i) {
        return new ProfileActivity.SearchAdapter.SearchResult[i];
    }

    public int getCategoryIcon(Class<? extends BaseFragment> cls) {
        return ((Integer) java.util.Objects.requireNonNullElse(categoriesIcons.get(cls), 0)).intValue();
    }

    public BaseFragment initiateFragment(Class<? extends BaseFragment> cls) {
        try {
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment == null) {
                return null;
            }
            BaseFragment baseFragmentNewInstance = cls.getDeclaredConstructor(null).newInstance(null);
            baseFragmentNewInstance.setParentFragment(lastFragment);
            baseFragmentNewInstance.createActionBar(lastFragment.getContext());
            return baseFragmentNewInstance;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public void openActivity(Class<? extends BaseFragment> cls, final Integer num) {
        final BaseFragment baseFragmentInitiateFragment;
        final BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment == null || (baseFragmentInitiateFragment = initiateFragment(cls)) == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                lastFragment.presentFragment(baseFragmentInitiateFragment);
            }
        });
        if (num == null || !(baseFragmentInitiateFragment instanceof BasePreferencesActivity)) {
            return;
        }
        final BasePreferencesActivity basePreferencesActivity = (BasePreferencesActivity) baseFragmentInitiateFragment;
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                basePreferencesActivity.scrollToItem(num.intValue());
            }
        });
    }

    private void createEntriesIfNeeded() {
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String key = currentLocaleInfo == null ? _UrlKt.FRAGMENT_ENCODE_SET : currentLocaleInfo.getKey();
        if (this.entriesFetched) {
            if (TextUtils.equals(this.entriesLangCode, key)) {
                return;
            }
            FileLog.d("[Extera] Language changed to " + key + ", rebuilding entries...");
            this.preparedEntries.clear();
        }
        FileLog.d("[Extera] Initialising activities...");
        categoriesIcons.keySet().forEach(new Consumer() { 
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.initiateFragment((Class) obj);
            }
        });
        this.entriesFetched = true;
        this.entriesLangCode = key;
    }

    public static int generateGUIDForUItem(Class<?> cls, UItem uItem) {
        return Objects.hash(cls.getName(), Integer.valueOf(uItem.id));
    }

    public static final class Entry extends RecordTag {
        private final Class<? extends BaseFragment> fragmentClass;
        private final int guid;
        private final int icon;
        private final int itemId;
        private final String subtext;
        private final String title;

        private void lambda$toSearchResult$0() {
            SettingsRegistry.getInstance().openActivity(this.fragmentClass, Integer.valueOf(this.itemId));
        }
    }
}
