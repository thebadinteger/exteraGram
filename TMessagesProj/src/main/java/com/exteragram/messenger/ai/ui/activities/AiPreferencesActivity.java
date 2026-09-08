package com.exteragram.messenger.ai.ui.activities;

import android.text.TextUtils;
import android.view.View;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class AiPreferencesActivity extends BasePreferencesActivity {
    @Override 
    public boolean hasWhiteActionBar() {
        return true;
    }

    @Override 
    public boolean needHideTitle() {
        return true;
    }

    public enum PreferenceItem {
        ENDPOINT,
        ROLE,
        SAVE_HISTORY,
        CLEAR_HISTORY,
        TEMPERATURE,
        RESPONSE_STREAMING,
        SHOW_RESPONSE_ONLY,
        INSERT_AS_QUOTE;

        public int getId() {
            return ordinal() + 1;
        }
    }

    @Override 
    public String getTitle() {
        return LocaleController.getString(R.string.AIChat);
    }

    @Override 
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(getTitle(), LocaleController.getString(R.string.AIChatInfo2), "exteraGramPlaceholders", "🤖"));
        arrayList.add(UItem.asShadow());
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.General)));
        arrayList.add(UItem.asButton(PreferenceItem.ENDPOINT.getId(), R.drawable.msg_language, LocaleController.getString(R.string.Services), getEndpointValue()).prioritizeTitleOverValue(true).setSearchable(this).setLinkAlias("aiServices", this));
        arrayList.add(UItem.asButton(PreferenceItem.ROLE.getId(), R.drawable.msg_openprofile, LocaleController.getString(R.string.Roles), AiConfig.getSelectedRole()).prioritizeTitleOverValue(true).setSearchable(this).setLinkAlias("aiRoles", this));
        arrayList.add(UItem.asCheck(PreferenceItem.SAVE_HISTORY.getId(), LocaleController.getString(R.string.MessageHistory), R.drawable.msg_discuss).setChecked(AiConfig.getSaveHistory()).setSearchable(this).setLinkAlias("saveAiHistory", this));
        if (!AiConfig.getConversationHistory().isEmpty()) {
            arrayList.add(UItem.asButton(PreferenceItem.CLEAR_HISTORY.getId(), R.drawable.msg_delete, LocaleController.getString(R.string.ClearHistory)).red().setSearchable(this).setLinkAlias("clearAiHistory", this));
        }
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.HistoryInfo)));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.AIGeneration)));
        arrayList.add(UItem.asCheck(PreferenceItem.RESPONSE_STREAMING.getId(), LocaleController.getString(R.string.ResponseStreaming), LocaleController.getString(R.string.ResponseStreamingInfo), true).setChecked(AiConfig.getResponseStreaming()).setSearchable(this).setLinkAlias("responseStreaming", this));
        arrayList.add(UItem.asCheck(PreferenceItem.SHOW_RESPONSE_ONLY.getId(), LocaleController.getString(R.string.ShowResponseOnly)).setChecked(AiConfig.getShowResponseOnly()).setSearchable(this).setLinkAlias("showResponseOnly", this));
        arrayList.add(UItem.asCheck(PreferenceItem.INSERT_AS_QUOTE.getId(), LocaleController.getString(R.string.InsertResponseAsQuote)).setChecked(AiConfig.getInsertAsQuote()).showDivider(false).setSearchable(this).setLinkAlias("insertResponseAsQuote", this));
        arrayList.add(UItem.asShadow());
        arrayList.add(UItem.asHeader(SettingsRegistry.markAsNewFeature("aiTemperature") ? LocaleUtils.applyNewSpan(LocaleController.getString(R.string.AITemperature)) : LocaleController.getString(R.string.AITemperature)));
        arrayList.add(createTemperatureSliderItem().showDivider(false).setSearchable(this).setLinkAlias("aiTemperature", this));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.AITemperatureInfo)));
    }

    @Override 
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        int i2 = uItem.id;
        if (i2 <= 0 || i2 > PreferenceItem.values().length) {
            return;
        }
        switch (AnonymousClass1.$SwitchMap$com$exteragram$messenger$ai$ui$activities$AiPreferencesActivity$PreferenceItem[PreferenceItem.values()[uItem.id - 1].ordinal()]) {
            case 1:
                presentFragment(new ServicesActivity());
                break;
            case 2:
                presentFragment(new RolesActivity());
                break;
            case 3:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        AiConfig.setSaveHistory(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 4:
                AiController.clearHistory(this, getResourceProvider(), true, new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onClick$0();
                    }
                });
                break;
            case 5:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        AiConfig.setResponseStreaming(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 6:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        AiConfig.setShowResponseOnly(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 7:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        AiConfig.setInsertAsQuote(((Boolean) obj).booleanValue());
                    }
                });
                break;
        }
    }

    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$exteragram$messenger$ai$ui$activities$AiPreferencesActivity$PreferenceItem;

        static {
            int[] iArr = new int[PreferenceItem.values().length];
            $SwitchMap$com$exteragram$messenger$ai$ui$activities$AiPreferencesActivity$PreferenceItem = iArr;
            try {
                iArr[PreferenceItem.ENDPOINT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$ai$ui$activities$AiPreferencesActivity$PreferenceItem[PreferenceItem.ROLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$ai$ui$activities$AiPreferencesActivity$PreferenceItem[PreferenceItem.SAVE_HISTORY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$ai$ui$activities$AiPreferencesActivity$PreferenceItem[PreferenceItem.CLEAR_HISTORY.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$ai$ui$activities$AiPreferencesActivity$PreferenceItem[PreferenceItem.RESPONSE_STREAMING.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$ai$ui$activities$AiPreferencesActivity$PreferenceItem[PreferenceItem.SHOW_RESPONSE_ONLY.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$ai$ui$activities$AiPreferencesActivity$PreferenceItem[PreferenceItem.INSERT_AS_QUOTE.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    public /* synthetic */ void lambda$onClick$0() {
        UniversalAdapter universalAdapter;
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    private UItem createTemperatureSliderItem() {
        UItem uItemAsIntSlideView = UItem.asIntSlideView(1, 0, AiConfig.getTemperature(), 20, new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return this.f$0.formatTemperature(((Integer) obj).intValue());
            }
        }, new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                AiConfig.setTemperature(((Integer) obj).intValue());
            }
        });
        uItemAsIntSlideView.id = PreferenceItem.TEMPERATURE.getId();
        uItemAsIntSlideView.text = LocaleController.getString(R.string.AITemperature);
        return uItemAsIntSlideView;
    }

    public CharSequence formatTemperature(int i) {
        return String.format(Locale.US, "%.1f", Float.valueOf(i / 10.0f));
    }

    private String getEndpointValue() {
        try {
            String host = new URL(AiController.getInstance().getSelected().getUrl()).getHost();
            if (!TextUtils.isEmpty(host) && AiController.canUseAI()) {
                return host.contains("generativelanguage.googleapis") ? "Gemini" : host;
            }
            return LocaleController.getString(R.string.BlockedEmpty);
        } catch (MalformedURLException unused) {
            return LocaleController.getString(R.string.BlockedEmpty);
        }
    }
}
