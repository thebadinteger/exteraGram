package com.exteragram.messenger.plugins.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EffectsTextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ViewHelper;

@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\r\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u001d\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011J\b\u0010\u0012\u001a\u00020\u0013H\u0016R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0014"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/EmptyPluginsView;", "Landroid/widget/FrameLayout;", "context", "Landroid/content/Context;", "resourcesProvider", "Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;", "<init>", "(Landroid/content/Context;Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;)V", "textView", "Lorg/telegram/ui/Components/EffectsTextView;", "backupImageView", "Lorg/telegram/ui/Components/BackupImageView;", "getBackupImageView", "()Lorg/telegram/ui/Components/BackupImageView;", "setText", _UrlKt.FRAGMENT_ENCODE_SET, "text", _UrlKt.FRAGMENT_ENCODE_SET, "hasOverlappingRendering", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SuppressLint({"ClickableViewAccessibility"})
public final class EmptyPluginsView extends FrameLayout {
    private final BackupImageView backupImageView;
    private final EffectsTextView textView;

    @JvmOverloads
    public EmptyPluginsView(Context context) {
        this(context, null);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @JvmOverloads
    public EmptyPluginsView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        ViewHelper.setPadding(linearLayout, 20.0f, 0.0f, 20.0f, 0.0f);
        linearLayout.setGravity(1);
        linearLayout.setClipChildren(false);
        linearLayout.setClipToPadding(false);
        linearLayout.setOrientation(1);
        BackupImageView backupImageView = new BackupImageView(context);
        this.backupImageView = backupImageView;
        linearLayout.addView(backupImageView, LayoutHelper.createLinear(100, 100, 17, 0.0f, 0.0f, 0.0f, 20.0f));
        EffectsTextView effectsTextView = new EffectsTextView(context);
        effectsTextView.setTextSize(1, 14.0f);
        effectsTextView.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder, resourcesProvider));
        effectsTextView.setGravity(1);
        effectsTextView.setText(LocaleController.getString(R.string.NoResult));
        effectsTextView.setTypeface(AndroidUtilities.regular());
        effectsTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        effectsTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.textView = effectsTextView;
        linearLayout.addView(effectsTextView, LayoutHelper.createLinear(-2, -2, 17));
        addView(linearLayout, LayoutHelper.createFrame(-2, -2, 17));
        setOnTouchListener(new View.OnTouchListener() { // from class: com.exteragram.messenger.plugins.ui.components.EmptyPluginsView$$ExternalSyntheticLambda0
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return EmptyPluginsView.$r8$lambda$Vd6WFN2r51k6Ofv4Z8vIUXzLhEQ(view, motionEvent);
            }
        });
    }

    public /* synthetic */ EmptyPluginsView(Context context, Theme.ResourcesProvider resourcesProvider, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : resourcesProvider);
    }

    public final BackupImageView getBackupImageView() {
        return this.backupImageView;
    }

    public static boolean $r8$lambda$Vd6WFN2r51k6Ofv4Z8vIUXzLhEQ(View view, MotionEvent motionEvent) {
        return true;
    }

    public final void setText(CharSequence text) {
        this.textView.setText(text);
    }
}
