package com.exteragram.messenger.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\r\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\n\b\u0007\u0018\u00002\u00020\u0001B/\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R(\u0010\u000e\u001a\u0010\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u0010\u0018\u00010\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R$\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u00078F@FX\u0086\u000e¢\u0006\f\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019¨\u0006\u001a"}, d2 = {"Lcom/exteragram/messenger/components/CheckBoxRow;", "Landroid/widget/LinearLayout;", "context", "Landroid/content/Context;", "label", _UrlKt.FRAGMENT_ENCODE_SET, "checked", _UrlKt.FRAGMENT_ENCODE_SET, "resourcesProvider", "Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;", "<init>", "(Landroid/content/Context;Ljava/lang/CharSequence;ZLorg/telegram/ui/ActionBar/Theme$ResourcesProvider;)V", "checkBox", "Lorg/telegram/ui/Components/CheckBox2;", "onCheckedChange", "Lkotlin/Function1;", _UrlKt.FRAGMENT_ENCODE_SET, "getOnCheckedChange", "()Lkotlin/jvm/functions/Function1;", "setOnCheckedChange", "(Lkotlin/jvm/functions/Function1;)V", "value", "isChecked", "()Z", "setChecked", "(Z)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SuppressLint({"ViewConstructor"})
public final class CheckBoxRow extends LinearLayout {
    private final CheckBox2 checkBox;
    private Function1<Boolean, Unit> onCheckedChange;

    public /* synthetic */ CheckBoxRow(Context context, CharSequence charSequence, boolean z, Theme.ResourcesProvider resourcesProvider, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, charSequence, (i & 4) != 0 ? false : z, (i & 8) != 0 ? null : resourcesProvider);
    }

    @JvmOverloads
    public CheckBoxRow(Context context, CharSequence charSequence, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
        checkBox2.setColor(Theme.key_radioBackgroundChecked, Theme.key_checkboxDisabled, Theme.key_checkboxCheck);
        checkBox2.setDrawUnchecked(true);
        checkBox2.setChecked(z, false);
        checkBox2.setDrawBackgroundAsArc(10);
        this.checkBox = checkBox2;
        setOrientation(0);
        setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(6.0f));
        setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 18, 18));
        ScaleStateListAnimator.apply(this, 0.05f, 1.2f);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(checkBox2, LayoutHelper.createFrame(21, 21.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
        addView(frameLayout, LayoutHelper.createLinear(24, 24, 16, 0, 0, 6, 0));
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        textView.setTextSize(1, 14.0f);
        textView.setTypeface(AndroidUtilities.regular());
        textView.setText(charSequence);
        addView(textView, LayoutHelper.createLinear(-2, -2, 16));
        setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.components.CheckBoxRow$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CheckBoxRow.$r8$lambda$3vuXnORUzuzxWlFKMBwpGVji5CU(CheckBoxRow.this, view);
            }
        });
    }

    public final Function1<Boolean, Unit> getOnCheckedChange() {
        return this.onCheckedChange;
    }

    public final void setOnCheckedChange(Function1<Boolean, Unit> function1) {
        this.onCheckedChange = function1;
    }

    public final boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public final void setChecked(boolean z) {
        this.checkBox.setChecked(z, true);
    }

    public static void $r8$lambda$3vuXnORUzuzxWlFKMBwpGVji5CU(CheckBoxRow checkBoxRow, View view) {
        CheckBox2 checkBox2 = checkBoxRow.checkBox;
        checkBox2.setChecked(!checkBox2.isChecked(), true);
        Function1<? super Boolean, Unit> function1 = checkBoxRow.onCheckedChange;
        if (function1 != null) {
            function1.invoke(Boolean.valueOf(checkBoxRow.checkBox.isChecked()));
        }
    }
}
