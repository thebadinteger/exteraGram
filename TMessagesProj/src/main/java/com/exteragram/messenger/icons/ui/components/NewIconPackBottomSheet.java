package com.exteragram.messenger.icons.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.icons.IconPack;
import com.exteragram.messenger.icons.ui.IconPacksEditorActivity;
import com.exteragram.messenger.icons.ui.picker.IconPickerController;
import com.exteragram.messenger.utils.system.VibratorUtils;
import java.util.HashMap;
import java.util.UUID;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineEditText;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class NewIconPackBottomSheet extends BottomSheet {
    private OutlineEditText authorField;
    private ButtonWithCounterView doneButton;
    private OutlineEditText nameField;
    private final IconPack packToEdit;
    private final BaseFragment parentFragment;
    private OutlineEditText versionField;

    public NewIconPackBottomSheet(BaseFragment baseFragment, Context context) {
        this(baseFragment, context, null);
    }

    public NewIconPackBottomSheet(BaseFragment baseFragment, Context context, IconPack iconPack) {
        super(context, true);
        this.packToEdit = iconPack;
        fixNavigationBar();
        this.waitingKeyboard = true;
        this.smoothKeyboardAnimationEnabled = true;
        this.parentFragment = baseFragment;
        setCustomView(createView(getContext()));
        setTitle(LocaleController.getString(iconPack == null ? R.string.NewIconPack : R.string.EditIconPackInfo), true);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public View createView(Context context) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        linearLayout.setOrientation(1);
        scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        linearLayout.setOnTouchListener(new View.OnTouchListener() { 
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return NewIconPackBottomSheet.$r8$lambda$BYtrHHfOrmFfWMznjkqv5h6qepQ(view, motionEvent);
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
        OutlineEditText outlineEditText = new OutlineEditText(context);
        this.nameField = outlineEditText;
        outlineEditText.getEditText().setInputType(49152);
        this.nameField.getEditText().setFilters(new InputFilter[]{new InputFilter() { 
            @Override // android.text.InputFilter
            public final CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                return this.f$0.lambda$createView$1(charSequence, i, i2, spanned, i3, i4);
            }
        }});
        this.nameField.getEditText().setImeOptions(5);
        this.nameField.setHint(LocaleController.getString(R.string.PackName));
        if (this.packToEdit != null) {
            this.nameField.getEditText().setText(this.packToEdit.getName());
        }
        frameLayout.addView(this.nameField, LayoutHelper.createFrame(-1, 58.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.nameField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() { 
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return this.f$0.lambda$createView$2(textView, i, keyEvent);
            }
        });
        OutlineEditText outlineEditText2 = new OutlineEditText(context);
        this.authorField = outlineEditText2;
        outlineEditText2.setBackground(null);
        this.authorField.getEditText().setInputType(49152);
        this.authorField.getEditText().setImeOptions(5);
        this.authorField.setHint(LocaleController.getString(R.string.AuthorNameOptional));
        if (this.packToEdit != null) {
            this.authorField.getEditText().setText(this.packToEdit.getAuthor());
        }
        frameLayout.addView(this.authorField, LayoutHelper.createFrame(-1, 58.0f, 51, 0.0f, 68.0f, 0.0f, 0.0f));
        this.authorField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() { 
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return this.f$0.lambda$createView$3(textView, i, keyEvent);
            }
        });
        OutlineEditText outlineEditText3 = new OutlineEditText(context);
        this.versionField = outlineEditText3;
        outlineEditText3.setBackground(null);
        this.versionField.getEditText().setInputType(49152);
        this.versionField.getEditText().setImeOptions(6);
        this.versionField.setHint(LocaleController.getString(R.string.Version));
        IconPack iconPack = this.packToEdit;
        OutlineEditText outlineEditText4 = this.versionField;
        if (iconPack != null) {
            outlineEditText4.getEditText().setText(this.packToEdit.getVersion());
        } else {
            outlineEditText4.getEditText().setText("1.0");
        }
        frameLayout.addView(this.versionField, LayoutHelper.createFrame(-1, 58.0f, 51, 0.0f, 136.0f, 0.0f, 0.0f));
        this.versionField.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() { 
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return this.f$0.lambda$createView$4(textView, i, keyEvent);
            }
        });
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, this.resourcesProvider);
        this.doneButton = buttonWithCounterView;
        buttonWithCounterView.setRound();
        this.doneButton.setText(LocaleController.getString(this.packToEdit == null ? R.string.Create : R.string.Save), false);
        this.doneButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.doneButton.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createView$5(view);
            }
        });
        linearLayout.addView(this.doneButton, LayoutHelper.createLinear(-1, 48, 0.0f, 16.0f, 0.0f, 16.0f));
        return scrollView;
    }

    public static void lambda$doOnDone$7(String str, String str2, String str3) {
        if (this.packToEdit != null) {
            String id = this.packToEdit.getId();
            if (str2.isEmpty()) {
                str2 = LocaleController.getString(R.string.PluginNoAuthor);
            }
            if (!IconManager.INSTANCE.saveIconPackMetadata(new IconPack(id, str, str2, str3, this.packToEdit.getIcons(), this.packToEdit.getPreinstalledMap(), null))) {
                showStorageError();
                return;
            } else {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$new$0();
                    }
                });
                return;
            }
        }
        final String str4 = "custom." + UUID.randomUUID().toString();
        if (str2.isEmpty()) {
            str2 = LocaleController.getString(R.string.PluginNoAuthor);
        }
        final IconPack iconPack = new IconPack(str4, str, str2, str3, new HashMap(), null, null);
        if (!IconManager.INSTANCE.saveIconPackMetadata(iconPack)) {
            showStorageError();
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$doOnDone$6(str4, iconPack);
                }
            });
        }
    }

    public void lambda$showStorageError$8() {
        BulletinFactory.of(this.topBulletinContainer, this.resourcesProvider).createErrorBulletin(LocaleController.getString(R.string.IconPackErrorStorage)).show();
    }

    private void showStorageError() {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showStorageError$8();
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void onOpenAnimationEnd() {
        super.onOpenAnimationEnd();
        OutlineEditText outlineEditText = this.nameField;
        if (outlineEditText == null || outlineEditText.getEditText() == null) {
            return;
        }
        this.nameField.getEditText().requestFocus();
        this.nameField.getEditText().setSelection(this.nameField.getEditText().length());
        AndroidUtilities.showKeyboard(this.nameField.getEditText());
    }
}
