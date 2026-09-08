package com.exteragram.messenger.ai.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import com.exteragram.messenger.utils.chats.ChatUtils;
import java.util.Objects;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class GenerateFromMessageBottomSheet extends BottomSheet {
    private boolean includeImage;
    BaseFragment parentFragment;
    private final EditTextBoldCursor promptField;
    private final OutlineTextContainerView promptFieldContainer;
    private boolean useHistory;

    public GenerateFromMessageBottomSheet(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, BaseFragment baseFragment, Context context, Utilities.Callback<GenerationData> callback) {
        this(Objects.toString(ChatUtils.getInstance().getMessageText(messageObject, groupedMessages), _UrlKt.FRAGMENT_ENCODE_SET), ChatUtils.getInstance().getPathToMessage(messageObject), baseFragment, context, callback);
    }

    public GenerateFromMessageBottomSheet(BaseFragment baseFragment, Context context, Utilities.Callback<GenerationData> callback, boolean z) {
        this(_UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, baseFragment, context, callback, z);
    }

    public GenerateFromMessageBottomSheet(String str, String str2, BaseFragment baseFragment, Context context, Utilities.Callback<GenerationData> callback) {
        this(str, str2, baseFragment, context, callback, AiConfig.getSaveHistory());
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public GenerateFromMessageBottomSheet(String str, final String str2, BaseFragment baseFragment, Context context, final Utilities.Callback<GenerationData> callback, boolean z) {
        boolean z2;
        boolean z3;
        super(context, true, baseFragment.getResourceProvider());
        fixNavigationBar();
        this.smoothKeyboardAnimationEnabled = true;
        this.parentFragment = baseFragment;
        this.useHistory = AiConfig.getSaveHistory();
        this.includeImage = AiController.canSendImage(str2);
        ScrollView scrollView = new ScrollView(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        linearLayout.setOrientation(1);
        linearLayout.setClipChildren(false);
        linearLayout.setClipToPadding(false);
        scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setClipChildren(false);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
        OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context, this.resourcesProvider);
        this.promptFieldContainer = outlineTextContainerView;
        outlineTextContainerView.setText(LocaleController.getString(R.string.RolePrompt));
        frameLayout.addView(outlineTextContainerView, LayoutHelper.createFrame(-1, -2.0f, 1, 0.0f, 0.0f, 0.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.promptField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 16.0f);
        int i = Theme.key_windowBackgroundWhiteBlackText;
        editTextBoldCursor.setTextColor(getThemedColor(i));
        editTextBoldCursor.setBackground(null);
        editTextBoldCursor.setMaxLines(8);
        if (!TextUtils.isEmpty(str)) {
            editTextBoldCursor.setText(str);
        }
        editTextBoldCursor.setInputType(147457);
        editTextBoldCursor.setImeOptions(268435456);
        editTextBoldCursor.setImeOptions(6);
        editTextBoldCursor.setCursorColor(getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
        editTextBoldCursor.setCursorWidth(1.5f);
        editTextBoldCursor.setGravity(LocaleController.isRTL ? 5 : 3);
        editTextBoldCursor.setOnFocusChangeListener(new View.OnFocusChangeListener() { 
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z4) {
                this.f$0.lambda$new$0(view, z4);
            }
        });
        editTextBoldCursor.setOnTouchListener(new View.OnTouchListener() { 
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return this.f$0.lambda$new$1(view, motionEvent);
            }
        });
        int iDp = AndroidUtilities.dp(16.0f);
        editTextBoldCursor.setPadding(iDp, 0, iDp, 0);
        editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
        outlineTextContainerView.addView(editTextBoldCursor, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 16.0f, 0.0f, 16.0f));
        outlineTextContainerView.attachEditText(editTextBoldCursor);
        final CheckBox2 checkBox2 = new CheckBox2(context, 21, this.resourcesProvider);
        int i2 = Theme.key_radioBackgroundChecked;
        int i3 = Theme.key_checkboxDisabled;
        int i4 = Theme.key_checkboxCheck;
        checkBox2.setColor(i2, i3, i4);
        checkBox2.setDrawUnchecked(true);
        checkBox2.setChecked(this.useHistory, false);
        checkBox2.setDrawBackgroundAsArc(10);
        TextView textView = new TextView(context);
        textView.setTextColor(getThemedColor(i));
        textView.setTextSize(1, 14.0f);
        textView.setText(LocaleController.getString(R.string.MessageHistory));
        FrameLayout frameLayout2 = new FrameLayout(context);
        frameLayout2.addView(checkBox2, LayoutHelper.createFrame(21, 21.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(6.0f));
        linearLayout2.addView(frameLayout2, LayoutHelper.createLinear(24, 24, 16, 0, 0, 6, 0));
        linearLayout2.addView(textView, LayoutHelper.createLinear(-2, -2, 16));
        linearLayout2.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$2(checkBox2, view);
            }
        });
        ScaleStateListAnimator.apply(linearLayout2, 0.05f, 1.2f);
        int i5 = Theme.key_listSelector;
        linearLayout2.setBackground(Theme.createRadSelectorDrawable(getThemedColor(i5), 6, 6));
        View view = new View(context);
        final CheckBox2 checkBox3 = new CheckBox2(context, 21, this.resourcesProvider);
        checkBox3.setColor(i2, i3, i4);
        checkBox3.setDrawUnchecked(true);
        checkBox3.setChecked(this.includeImage, false);
        checkBox3.setDrawBackgroundAsArc(10);
        TextView textView2 = new TextView(context);
        textView2.setTextColor(getThemedColor(i));
        textView2.setTextSize(1, 14.0f);
        textView2.setText(LocaleController.getString(R.string.AttachPhoto));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.getImageReceiver().setImage(str2, "24_24", null, null, 0L);
        backupImageView.setRoundRadius(AndroidUtilities.dp(4.0f));
        FrameLayout frameLayout3 = new FrameLayout(context);
        frameLayout3.addView(checkBox3, LayoutHelper.createFrame(21, 21.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(0);
        linearLayout3.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
        linearLayout3.addView(frameLayout3, LayoutHelper.createLinear(24, 24, 16, 0, 0, 6, 0));
        linearLayout3.addView(textView2, LayoutHelper.createLinear(-2, -2, 16));
        linearLayout3.addView(backupImageView, LayoutHelper.createLinear(24, 24, 16, 9, 0, 0, 0));
        linearLayout3.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.lambda$new$3(checkBox3, view2);
            }
        });
        ScaleStateListAnimator.apply(linearLayout3, 0.05f, 1.2f);
        linearLayout3.setBackground(Theme.createRadSelectorDrawable(getThemedColor(i5), 6, 6));
        boolean zCanSendImage = AiController.canSendImage(str2);
        if ((AiConfig.getSaveHistory() && z) || zCanSendImage) {
            LinearLayout linearLayout4 = new LinearLayout(context);
            linearLayout4.setOrientation(0);
            linearLayout4.setGravity(3);
            if (AiConfig.getSaveHistory() && z) {
                linearLayout4.addView(linearLayout2, LayoutHelper.createLinear(-2, -2));
                z2 = true;
            } else {
                z2 = false;
            }
            if (zCanSendImage) {
                if (z2) {
                    linearLayout4.addView(view, LayoutHelper.createLinear(8, -1));
                }
                linearLayout4.addView(linearLayout3, LayoutHelper.createLinear(-2, -2));
                z3 = true;
            } else {
                z3 = false;
            }
            if (z2 || z3) {
                linearLayout.addView(linearLayout4, LayoutHelper.createLinear(-1, -2, 1, 0, 16, 0, 0));
            }
        }
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, this.resourcesProvider);
        buttonWithCounterView.setRound();
        buttonWithCounterView.setColor(getThemedColor(Theme.key_featuredStickers_addButton));
        buttonWithCounterView.setText(LocaleController.getString(R.string.Proceed), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.lambda$new$4(str2, callback, view2);
            }
        });
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 0, 0, 16, 0, 16));
        setCustomView(scrollView);
        setTitle(LocaleController.getString(R.string.Generate), true);
    }

    public /* synthetic */ void lambda$new$0(View view, boolean z) {
        this.promptFieldContainer.animateSelection(z ? 1.0f : 0.0f);
    }

    public /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
        if (view.getId() == this.promptField.getId()) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if ((motionEvent.getAction() & 255) == 1) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$new$2(CheckBox2 checkBox2, View view) {
        checkBox2.setChecked(!checkBox2.isChecked(), true);
        this.useHistory = checkBox2.isChecked();
    }

    public /* synthetic */ void lambda$new$3(CheckBox2 checkBox2, View view) {
        checkBox2.setChecked(!checkBox2.isChecked(), true);
        this.includeImage = checkBox2.isChecked();
    }

    public /* synthetic */ void lambda$new$4(String str, Utilities.Callback callback, View view) {
        String string = this.promptField.getText().toString();
        if (TextUtils.isEmpty(string)) {
            if (!this.includeImage) {
                AndroidUtilities.shakeViewSpring(this.promptFieldContainer);
                view.performHapticFeedback(0);
                return;
            }
            string = LocaleController.getString(R.string.AttachPhoto);
        }
        lambda$new$0();
        if (!this.includeImage) {
            str = null;
        }
        callback.run(new GenerationData(string, this.useHistory, str));
    }

    public static final class GenerationData extends RecordTag {
        private final String imagePath;
        private final String prompt;
        private final boolean useHistory;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof GenerationData)) {
                return false;
            }
            GenerationData generationData = (GenerationData) obj;
            return this.useHistory == generationData.useHistory && Objects.equals(this.prompt, generationData.prompt) && Objects.equals(this.imagePath, generationData.imagePath);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.prompt, Boolean.valueOf(this.useHistory), this.imagePath};
        }

        public GenerationData(String str, boolean z, String str2) {
            this.prompt = str;
            this.useHistory = z;
            this.imagePath = str2;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return GenerateFromMessageBottomSheet$GenerationData$$ExternalSyntheticRecord0.m(this.useHistory, this.prompt, this.imagePath);
        }

        public String imagePath() {
            return this.imagePath;
        }

        public String prompt() {
            return this.prompt;
        }

        public final String toString() {
            return Client$ImagePayload$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), GenerationData.class, "prompt;useHistory;imagePath");
        }

        public boolean useHistory() {
            return this.useHistory;
        }
    }
}
