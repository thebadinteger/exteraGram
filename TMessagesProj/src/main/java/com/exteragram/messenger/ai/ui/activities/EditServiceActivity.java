package com.exteragram.messenger.ai.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.data.Service;
import com.exteragram.messenger.ai.data.Suggestions;
import com.exteragram.messenger.ai.network.Client;
import com.exteragram.messenger.ai.network.GenerationCallback;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.Objects;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EditTextCell;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class EditServiceActivity extends BasePreferencesActivity {
    private static final ServicePreset[] SERVICE_PRESETS = {new ServicePreset("Gemini", "https://generativelanguage.googleapis.com/v1beta", "gemini-3.5-flash"), new ServicePreset("OpenAI", "https://api.openai.com/v1", "gpt-5.4-mini"), new ServicePreset("OpenRouter", "https://openrouter.ai/api/v1", "openai/gpt-5.4-mini"), new ServicePreset(0 == true ? 1 : 0, 0 == true ? 1 : 0, 0 == true ? 1 : 0)};
    private final ClipboardManager.OnPrimaryClipChangedListener clipChangedListener;
    private ClipboardManager clipboardManager;
    private final Service currentService;
    private boolean forceCustomPreset;
    private boolean hasChanges;
    private String initialKey;
    private String initialModel;
    private boolean initialReasoningEnabled;
    private String initialUrl;
    private boolean isTesting;
    private EditTextCell keyCell;
    private EditTextCell modelCell;
    private ParsedServiceInput pasteInput;
    private String pasteString;
    private boolean reasoningEnabled;
    private int selectedPresetIndex;
    private int shiftDp;
    private Client testingClient;
    private AlertDialog testingProgressDialog;
    private String testingRequestId;
    private boolean updatingFields;
    private EditTextCell urlCell;

    public EditServiceActivity() {
        this(null);
    }

    public EditServiceActivity(Service service) {
        this.clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() { 
            @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
            public final void onPrimaryClipChanged() {
                this.f$0.updateClipboardState();
            }
        };
        this.shiftDp = -4;
        this.currentService = service;
    }

    @Override 
    public View createView(Context context) {
        this.clipboardManager = (ClipboardManager) context.getSystemService("clipboard");
        createFields(context);
        initializeState();
        updateClipboardState(context, false);
        View viewCreateView = super.createView(context);
        this.actionBar.setAllowOverlayTitle(true);
        return viewCreateView;
    }

    @Override 
    public String getTitle() {
        return LocaleController.getString(this.currentService != null ? R.string.EditService : R.string.NewService);
    }

    @Override 
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        boolean z;
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.ServiceProvider)));
        boolean z2 = false;
        int i = 0;
        while (true) {
            z = true;
            if (i >= SERVICE_PRESETS.length) {
                break;
            }
            arrayList.add(UItem.asRadio(i + 100, getPresetTitle(i)).setChecked(i == this.selectedPresetIndex).setEnabled(true ^ this.isTesting));
            i++;
        }
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.ServicesInfo)));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.ServiceInfo)));
        arrayList.add(UItem.asCustom(this.urlCell));
        arrayList.add(UItem.asCustom(this.modelCell));
        arrayList.add(UItem.asCustom(this.keyCell));
        arrayList.add(UItem.asCheck(203, SettingsRegistry.markAsNewFeature("AI-Service-Reasoning") ? LocaleUtils.applyNewSpan(LocaleController.getString(R.string.AIReasoning)) : LocaleController.getString(R.string.AIReasoning), LocaleController.getString(R.string.AIReasoningInfo), true).setChecked(this.reasoningEnabled).setEnabled(!this.isTesting));
        arrayList.add(UItem.asShadow());
        ParsedServiceInput parsedServiceInput = this.pasteInput;
        if (parsedServiceInput != null) {
            arrayList.add(UItem.asButton(200, R.drawable.msg_copy, LocaleController.getString(parsedServiceInput.hasServiceFields() ? R.string.ServicePasteService : R.string.ServicePasteKey)).accent().setEnabled(!this.isTesting));
            z2 = true;
        }
        if (this.hasChanges) {
            arrayList.add(createSaveItem());
            z2 = true;
        }
        if (this.currentService != null) {
            arrayList.add(UItem.asButton(202, R.drawable.msg_delete, LocaleController.getString(R.string.Delete)).red().setEnabled(!this.isTesting));
        } else {
            z = z2;
        }
        if (z) {
            arrayList.add(UItem.asShadow());
        }
    }

    @Override 
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        if (this.isTesting) {
            return;
        }
        int i2 = uItem.id;
        if (i2 >= 100 && i2 < SERVICE_PRESETS.length + 100) {
            applyPreset(i2 - 100);
            return;
        }
        if (i2 == 200) {
            ParsedServiceInput serviceInput = this.pasteInput;
            if (serviceInput == null) {
                serviceInput = parseServiceInput(this.pasteString);
            }
            if (serviceInput != null) {
                applyParsedServiceInput(serviceInput);
                return;
            }
            return;
        }
        if (i2 == 201) {
            saveConfig();
        } else if (i2 == 202) {
            confirmDeleteService();
        } else if (i2 == 203) {
            toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$onClick$0((Boolean) obj);
                }
            });
        }
    }

    public boolean lambda$createFields$1(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        this.modelCell.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.modelCell.editText);
        return true;
    }

    public void lambda$onResponse$0(String str, Service service) {
            EditServiceActivity.this.clearTestingState();
            if (TextUtils.isEmpty(str)) {
                return;
            }
            if (EditServiceActivity.this.currentService != null) {
                AiController.getInstance().updateService(EditServiceActivity.this.currentService, service);
            } else {
                AiController.getInstance().addService(service);
            }
            AiConfig.setSelectedServices(service);
            EditServiceActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.servicesUpdated, new Object[0]);
            EditServiceActivity.this.finishFragment();
        }

        @Override 
        public void onError(final int i, String str) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onError$1(i);
                }
            });
        }

        public void lambda$confirmDeleteService$4(AlertDialog alertDialog, int i) {
        deleteCurrentService();
    }

    private void deleteCurrentService() {
        Service service = this.currentService;
        boolean z = service != null && service.isSelected();
        if (this.currentService == null || !AiController.getInstance().removeService(this.currentService)) {
            return;
        }
        if (z) {
            AiConfig.clearSelectedService();
            if (!AiController.getInstance().isServicesEmpty()) {
                AiConfig.setSelectedServices(AiController.getInstance().getAll().get(0));
            }
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.servicesUpdated, new Object[0]);
        finishFragment();
    }

    private String getFieldText(EditTextCell editTextCell) {
        return (editTextCell == null || editTextCell.getText() == null) ? _UrlKt.FRAGMENT_ENCODE_SET : editTextCell.getText().toString().trim();
    }

    private String safeString(String str) {
        return str != null ? str : _UrlKt.FRAGMENT_ENCODE_SET;
    }

    public static final class ServicePreset extends RecordTag {
        private final String model;
        private final String name;
        private final String url;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof ServicePreset)) {
                return false;
            }
            ServicePreset servicePreset = (ServicePreset) obj;
            return Objects.equals(this.name, servicePreset.name) && Objects.equals(this.url, servicePreset.url) && Objects.equals(this.model, servicePreset.model);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.name, this.url, this.model};
        }

        private ServicePreset(String str, String str2, String str3) {
            this.name = str;
            this.url = str2;
            this.model = str3;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.name, this.url, this.model);
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), ServicePreset.class, "name;url;model");
        }
    }

    public static final class ParsedServiceInput extends RecordTag {
        private final String key;
        private final String model;
        private final String url;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof ParsedServiceInput)) {
                return false;
            }
            ParsedServiceInput parsedServiceInput = (ParsedServiceInput) obj;
            return Objects.equals(this.url, parsedServiceInput.url) && Objects.equals(this.model, parsedServiceInput.model) && Objects.equals(this.key, parsedServiceInput.key);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.url, this.model, this.key};
        }

        private ParsedServiceInput(String str, String str2, String str3) {
            this.url = str;
            this.model = str2;
            this.key = str3;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.url, this.model, this.key);
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), ParsedServiceInput.class, "url;model;key");
        }

        public boolean hasServiceFields() {
            return (TextUtils.isEmpty(this.url) && TextUtils.isEmpty(this.model)) ? false : true;
        }

        public boolean differsFrom(String str, String str2, String str3) {
            if (!TextUtils.isEmpty(this.url) && !TextUtils.equals(this.url, str)) {
                return true;
            }
            if (TextUtils.isEmpty(this.model) || TextUtils.equals(this.model, str2)) {
                return (TextUtils.isEmpty(this.key) || TextUtils.equals(this.key, str3)) ? false : true;
            }
            return true;
        }
    }
}
