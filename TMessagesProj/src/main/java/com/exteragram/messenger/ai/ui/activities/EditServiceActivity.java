package com.exteragram.messenger.ai.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.exteragram.messenger.utils.RecordTag;
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
    private static final ServicePreset[] SERVICE_PRESETS = {new ServicePreset("Gemini", "https://generativelanguage.googleapis.com/v1beta", "gemini-3.5-flash"), new ServicePreset("OpenAI", "https://api.openai.com/v1", "gpt-5.4-mini"), new ServicePreset("OpenRouter", "https://openrouter.ai/api/v1", "openai/gpt-5.4-mini"), new ServicePreset(null, null, null)};
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
        this.clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity$$ExternalSyntheticLambda3
            @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
            public final void onPrimaryClipChanged() {
                EditServiceActivity.this.updateClipboardState();
            }
        };
        this.shiftDp = -4;
        this.currentService = service;
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity, org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.clipboardManager = (ClipboardManager) context.getSystemService("clipboard");
        createFields(context);
        initializeState();
        updateClipboardState(context, false);
        View viewCreateView = super.createView(context);
        this.actionBar.setAllowOverlayTitle(true);
        return viewCreateView;
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public String getTitle() {
        return LocaleController.getString(this.currentService != null ? R.string.EditService : R.string.NewService);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
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

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
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
            toggleBooleanSettingAndRefresh(uItem, new Consumer() { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity$$ExternalSyntheticLambda4
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj) {
                    EditServiceActivity.this.lambda$onClick$0((Boolean) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClick$0(Boolean bool) {
        this.reasoningEnabled = bool.booleanValue();
        updateFormState();
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity, org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        EditTextCell editTextCell;
        super.onResume();
        ClipboardManager clipboardManager = this.clipboardManager;
        if (clipboardManager != null) {
            clipboardManager.removePrimaryClipChangedListener(this.clipChangedListener);
            this.clipboardManager.addPrimaryClipChangedListener(this.clipChangedListener);
        }
        updateClipboardState();
        if (MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) || (editTextCell = this.keyCell) == null) {
            return;
        }
        editTextCell.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.keyCell.editText);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity, org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        ClipboardManager clipboardManager = this.clipboardManager;
        if (clipboardManager != null) {
            clipboardManager.removePrimaryClipChangedListener(this.clipChangedListener);
        }
        super.onPause();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        if (this.testingClient != null && !TextUtils.isEmpty(this.testingRequestId)) {
            this.testingClient.stopRequest(this.testingRequestId);
        }
        hideTestingProgressDialog();
        this.testingRequestId = null;
        this.testingClient = null;
        super.onFragmentDestroy();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        EditTextCell editTextCell;
        if (!z || (editTextCell = this.keyCell) == null) {
            return;
        }
        editTextCell.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.keyCell.editText);
    }

    private void createFields(Context context) {
        boolean z = false;
        boolean z2 = false;
        EditTextCell editTextCell = new EditTextCell(context, LocaleController.getString(R.string.ServiceURL), z, z2, 128, this.resourceProvider) { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity.1
            @Override // org.telegram.ui.Cells.EditTextCell
            public void onTextChanged(CharSequence charSequence) {
                EditServiceActivity.this.updateFormState();
            }
        };
        this.urlCell = editTextCell;
        editTextCell.editText.setInputType(524305);
        this.urlCell.editText.setRawInputType(524305);
        this.urlCell.editText.setImeOptions(268435461);
        this.urlCell.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity$$ExternalSyntheticLambda0
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return EditServiceActivity.this.lambda$createFields$1(textView, i, keyEvent);
            }
        });
        EditTextCell editTextCell2 = new EditTextCell(context, LocaleController.getString(R.string.ServiceModel), z, z2, 64, this.resourceProvider) { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity.2
            @Override // org.telegram.ui.Cells.EditTextCell
            public void onTextChanged(CharSequence charSequence) {
                EditServiceActivity.this.updateFormState();
            }
        };
        this.modelCell = editTextCell2;
        editTextCell2.editText.setInputType(524289);
        this.modelCell.editText.setRawInputType(524289);
        this.modelCell.editText.setImeOptions(268435461);
        this.modelCell.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity$$ExternalSyntheticLambda1
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return EditServiceActivity.this.lambda$createFields$2(textView, i, keyEvent);
            }
        });
        EditTextCell editTextCell3 = new EditTextCell(context, LocaleController.getString(R.string.ServiceKey), z, z2, 256, this.resourceProvider) { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity.3
            @Override // org.telegram.ui.Cells.EditTextCell
            public void onTextChanged(CharSequence charSequence) {
                EditServiceActivity.this.updateFormState();
            }
        };
        this.keyCell = editTextCell3;
        editTextCell3.editText.setInputType(524433);
        this.keyCell.editText.setRawInputType(524433);
        this.keyCell.editText.setImeOptions(268435462);
        this.keyCell.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity$$ExternalSyntheticLambda2
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return EditServiceActivity.this.lambda$createFields$3(textView, i, keyEvent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createFields$1(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        this.modelCell.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.modelCell.editText);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createFields$2(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        this.keyCell.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.keyCell.editText);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createFields$3(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        saveConfig();
        return true;
    }

    private void initializeState() {
        Service service = this.currentService;
        if (service != null) {
            this.initialUrl = service.getUrl();
            this.initialModel = this.currentService.getModel();
            this.initialKey = this.currentService.getKey();
            this.initialReasoningEnabled = this.currentService.isReasoningEnabled();
        } else {
            Service service2 = AiConfig.DEFAULT_SERVICE;
            this.initialUrl = service2.getUrl();
            this.initialModel = service2.getModel();
            this.initialKey = service2.getKey();
            this.initialReasoningEnabled = service2.isReasoningEnabled();
        }
        this.reasoningEnabled = this.initialReasoningEnabled;
        this.urlCell.setText(safeString(this.initialUrl));
        this.modelCell.setText(safeString(this.initialModel));
        this.keyCell.setText(safeString(this.initialKey));
        this.hasChanges = false;
        int iFindPresetIndex = findPresetIndex(this.initialUrl, this.initialModel);
        this.selectedPresetIndex = iFindPresetIndex;
        this.forceCustomPreset = iFindPresetIndex == getCustomPresetIndex();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFormState() {
        UniversalAdapter universalAdapter;
        if (this.updatingFields || this.urlCell == null || this.modelCell == null || this.keyCell == null) {
            return;
        }
        boolean z = this.hasChanges;
        int i = this.selectedPresetIndex;
        ParsedServiceInput parsedServiceInput = this.pasteInput;
        boolean z2 = false;
        boolean z3 = parsedServiceInput != null;
        boolean z4 = parsedServiceInput != null && parsedServiceInput.hasServiceFields();
        this.hasChanges = (TextUtils.equals(getFieldText(this.urlCell), safeString(this.initialUrl)) && TextUtils.equals(getFieldText(this.modelCell), safeString(this.initialModel)) && TextUtils.equals(getFieldText(this.keyCell), safeString(this.initialKey)) && this.reasoningEnabled == this.initialReasoningEnabled) ? false : true;
        int iFindPresetIndex = findPresetIndex(getFieldText(this.urlCell), getFieldText(this.modelCell));
        if (this.forceCustomPreset) {
            iFindPresetIndex = getCustomPresetIndex();
        }
        this.selectedPresetIndex = iFindPresetIndex;
        updateClipboardState(false);
        ParsedServiceInput parsedServiceInput2 = this.pasteInput;
        boolean z5 = parsedServiceInput2 != null;
        if (parsedServiceInput2 != null && parsedServiceInput2.hasServiceFields()) {
            z2 = true;
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        if (z == this.hasChanges && i == this.selectedPresetIndex && z3 == z5 && z4 == z2) {
            return;
        }
        universalAdapter.update(true);
    }

    private void setTesting(boolean z) {
        UniversalAdapter universalAdapter;
        this.isTesting = z;
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    private UItem createSaveItem() {
        return UItem.asButton(201, R.drawable.ic_ab_done, LocaleController.getString(R.string.ServiceTestAndSave)).accent().setEnabled(!this.isTesting);
    }

    private void applyPreset(int i) {
        ServicePreset servicePreset = SERVICE_PRESETS[i];
        if (servicePreset.url == null || servicePreset.model == null) {
            this.forceCustomPreset = true;
            this.updatingFields = true;
            this.urlCell.setText(_UrlKt.FRAGMENT_ENCODE_SET);
            this.modelCell.setText(_UrlKt.FRAGMENT_ENCODE_SET);
            this.updatingFields = false;
            updateFormState();
            this.urlCell.editText.requestFocus();
            AndroidUtilities.showKeyboard(this.urlCell.editText);
            return;
        }
        this.forceCustomPreset = false;
        this.updatingFields = true;
        this.urlCell.setText(servicePreset.url);
        this.modelCell.setText(servicePreset.model);
        this.updatingFields = false;
        updateFormState();
        this.keyCell.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.keyCell.editText);
    }

    private String getPresetTitle(int i) {
        ServicePreset servicePreset = SERVICE_PRESETS[i];
        return servicePreset.name != null ? servicePreset.name : LocaleController.getString(R.string.ServiceProviderCustom);
    }

    private int findPresetIndex(String str, String str2) {
        int i = 0;
        while (true) {
            ServicePreset[] servicePresetArr = SERVICE_PRESETS;
            if (i < servicePresetArr.length - 1) {
                ServicePreset servicePreset = servicePresetArr[i];
                if (TextUtils.equals(str, servicePreset.url) && TextUtils.equals(str2, servicePreset.model)) {
                    return i;
                }
                i++;
            } else {
                return getCustomPresetIndex();
            }
        }
    }

    private int getCustomPresetIndex() {
        return SERVICE_PRESETS.length - 1;
    }

    private void saveConfig() {
        if (!this.isTesting && validateFields()) {
            Service service = new Service(getFieldText(this.urlCell), getFieldText(this.modelCell), getFieldText(this.keyCell), this.reasoningEnabled);
            Service serviceFindExistingService = findExistingService(service);
            Service service2 = this.currentService;
            boolean z = (service2 == null || service2.isReasoningEnabled() == this.reasoningEnabled) ? false : true;
            if (serviceFindExistingService == null || (serviceFindExistingService == this.currentService && z)) {
                this.testingClient = new Client.Builder().serviceOverride(service).roleOverride(Suggestions.ASSISTANT.getRole()).build();
                showTestingProgressDialog();
                setTesting(true);
                this.testingRequestId = this.testingClient.getResponse("Say 'hi'.", new AnonymousClass4(service));
                return;
            }
            AiConfig.setSelectedServices(serviceFindExistingService);
            getNotificationCenter().postNotificationNameOnUIThread(NotificationCenter.servicesUpdated, new Object[0]);
            finishFragment();
        }
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.ai.ui.activities.EditServiceActivity$4, reason: invalid class name */
    public class AnonymousClass4 implements GenerationCallback {
        final /* synthetic */ Service val$service;

        @Override // com.exteragram.messenger.ai.network.GenerationCallback
        public void onChunk(String str) {
        }

        public AnonymousClass4(Service service) {
            this.val$service = service;
        }

        @Override // com.exteragram.messenger.ai.network.GenerationCallback
        public void onResponse(final String str) {
            final Service service = this.val$service;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity$4$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AnonymousClass4.this.lambda$onResponse$0(str, service);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onResponse$0(String str, Service service) {
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
            EditServiceActivity.this.getNotificationCenter().postNotificationNameOnUIThread(NotificationCenter.servicesUpdated, new Object[0]);
            EditServiceActivity.this.finishFragment();
        }

        @Override // com.exteragram.messenger.ai.network.GenerationCallback
        public void onError(final int i, String str) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity$4$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AnonymousClass4.this.lambda$onError$1(i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$1(int i) {
            EditServiceActivity.this.clearTestingState();
            AiController.showErrorBulletin(EditServiceActivity.this, i);
            EditServiceActivity editServiceActivity = EditServiceActivity.this;
            editServiceActivity.showFieldError(editServiceActivity.keyCell);
        }
    }

    private Service findExistingService(Service service) {
        for (Service service2 : AiController.getInstance().getAll()) {
            if (service2.equals(service)) {
                return service2;
            }
        }
        return null;
    }

    private boolean validateFields() {
        if (!isValidServiceUrl(getFieldText(this.urlCell))) {
            showFieldError(this.urlCell);
            return false;
        }
        if (TextUtils.isEmpty(getFieldText(this.modelCell))) {
            showFieldError(this.modelCell);
            return false;
        }
        if (!TextUtils.isEmpty(getFieldText(this.keyCell))) {
            return true;
        }
        showFieldError(this.keyCell);
        return false;
    }

    private boolean isValidServiceUrl(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Uri uri = Uri.parse(str);
        String scheme = uri.getScheme();
        return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) && !TextUtils.isEmpty(uri.getHost());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showFieldError(EditTextCell editTextCell) {
        if (editTextCell == null) {
            return;
        }
        BotWebViewVibrationEffect.APP_ERROR.vibrate();
        int i = -this.shiftDp;
        this.shiftDp = i;
        AndroidUtilities.shakeViewSpring(editTextCell, i);
        editTextCell.editText.requestFocus();
        AndroidUtilities.showKeyboard(editTextCell.editText);
    }

    private void applyParsedServiceInput(ParsedServiceInput parsedServiceInput) {
        this.updatingFields = true;
        if (!TextUtils.isEmpty(parsedServiceInput.url)) {
            this.urlCell.setText(parsedServiceInput.url);
        }
        if (!TextUtils.isEmpty(parsedServiceInput.model)) {
            this.modelCell.setText(parsedServiceInput.model);
        }
        if (!TextUtils.isEmpty(parsedServiceInput.key)) {
            this.keyCell.setText(parsedServiceInput.key);
        }
        this.forceCustomPreset = false;
        this.updatingFields = false;
        updateFormState();
    }

    private void showTestingProgressDialog() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.testingProgressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        showDialog(this.testingProgressDialog);
    }

    private void hideTestingProgressDialog() {
        AlertDialog alertDialog = this.testingProgressDialog;
        if (alertDialog == null) {
            return;
        }
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        this.testingProgressDialog = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearTestingState() {
        hideTestingProgressDialog();
        setTesting(false);
        this.testingRequestId = null;
        this.testingClient = null;
    }

    private ParsedServiceInput parseServiceInput(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String strTrim = str.trim();
        if (TextUtils.isEmpty(strTrim)) {
            return null;
        }
        String[] strArrSplit = strTrim.split("\\r?\\n");
        ArrayList arrayList = new ArrayList();
        int i = 0;
        for (String str2 : strArrSplit) {
            String strTrim2 = str2.trim();
            if (!TextUtils.isEmpty(strTrim2)) {
                arrayList.add(strTrim2);
            }
        }
        if (arrayList.size() >= 3 && isLikelyService((String) arrayList.get(0), (String) arrayList.get(1), (String) arrayList.get(2))) {
            return new ParsedServiceInput((String) arrayList.get(0), (String) arrayList.get(1), (String) arrayList.get(2));
        }
        while (i <= arrayList.size() - 3) {
            int i2 = i + 1;
            int i3 = i + 2;
            if (isLikelyService((String) arrayList.get(i), (String) arrayList.get(i2), (String) arrayList.get(i3))) {
                return new ParsedServiceInput((String) arrayList.get(i), (String) arrayList.get(i2), (String) arrayList.get(i3));
            }
            i = i2;
        }
        if (arrayList.size() == 1 && isLikelyApiKey(strTrim)) {
            return new ParsedServiceInput(null, null, strTrim);
        }
        return null;
    }

    private boolean isLikelyService(String str, String str2, String str3) {
        return isLikelyServiceUrl(str) && isLikelyModel(str2) && isLikelyApiKey(str3);
    }

    private boolean isLikelyServiceUrl(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.startsWith("https://") || str.startsWith("http://");
    }

    private boolean isLikelyModel(String str) {
        return (TextUtils.isEmpty(str) || str.length() > 128 || str.matches(".*\\s+.*")) ? false : true;
    }

    private boolean isLikelyApiKey(String str) {
        return !TextUtils.isEmpty(str) && str.length() >= 20 && str.length() <= 512 && str.matches("[A-Za-z0-9_.\\-]+");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateClipboardState() {
        updateClipboardState(true);
    }

    private void updateClipboardState(boolean z) {
        View view = this.fragmentView;
        updateClipboardState(view != null ? view.getContext() : getContext(), z);
    }

    private void updateClipboardState(Context context, boolean z) {
        UniversalRecyclerView universalRecyclerView;
        UniversalAdapter universalAdapter;
        if (this.clipboardManager == null || context == null || this.urlCell == null || this.modelCell == null || this.keyCell == null) {
            return;
        }
        ParsedServiceInput parsedServiceInput = this.pasteInput;
        boolean z2 = false;
        boolean z3 = parsedServiceInput != null;
        boolean z4 = parsedServiceInput != null && parsedServiceInput.hasServiceFields();
        String clipboardText = readClipboardText(context);
        ParsedServiceInput serviceInput = parseServiceInput(clipboardText);
        if (serviceInput != null && serviceInput.differsFrom(getFieldText(this.urlCell), getFieldText(this.modelCell), getFieldText(this.keyCell))) {
            this.pasteInput = serviceInput;
            this.pasteString = clipboardText;
        } else {
            this.pasteInput = null;
            this.pasteString = null;
        }
        ParsedServiceInput parsedServiceInput2 = this.pasteInput;
        boolean z5 = parsedServiceInput2 != null;
        if (parsedServiceInput2 != null && parsedServiceInput2.hasServiceFields()) {
            z2 = true;
        }
        if (!z || (universalRecyclerView = this.listView) == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        if (z3 == z5 && z4 == z2) {
            return;
        }
        universalAdapter.update(true);
    }

    private String readClipboardText(Context context) {
        ClipData primaryClip = this.clipboardManager.getPrimaryClip();
        if (primaryClip != null && primaryClip.getItemCount() > 0) {
            try {
                CharSequence charSequenceCoerceToText = primaryClip.getItemAt(0).coerceToText(context);
                if (charSequenceCoerceToText != null) {
                    return charSequenceCoerceToText.toString();
                }
            } catch (Exception unused) {
            }
        }
        return null;
    }

    private void confirmDeleteService() {
        if (this.currentService == null || getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.Delete));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.DeleteServiceInfo, this.currentService.getShortModel())));
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.ai.ui.activities.EditServiceActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                EditServiceActivity.this.lambda$confirmDeleteService$4(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog alertDialogCreate = builder.create();
        showDialog(alertDialogCreate);
        TextView textView = (TextView) alertDialogCreate.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$confirmDeleteService$4(AlertDialog alertDialog, int i) {
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
        getNotificationCenter().postNotificationNameOnUIThread(NotificationCenter.servicesUpdated);
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
            return Objects.hash(this.name, this.url, this.model);
        }

        public final String toString() {
            return "ServicePreset[name=" + this.name + ", url=" + this.url + ", model=" + this.model + "]";
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
            return Objects.hash(this.url, this.model, this.key);
        }

        public final String toString() {
            return "ParsedServiceInput[url=" + this.url + ", model=" + this.model + ", key=" + this.key + "]";
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
