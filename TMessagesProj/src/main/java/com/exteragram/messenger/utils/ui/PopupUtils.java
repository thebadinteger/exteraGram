package com.exteragram.messenger.utils.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.IntFunction;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Components.LayoutHelper;

public abstract class PopupUtils {

    public interface OnItemClickListener {
        void onClick(int i);
    }

    public interface OnMultiSelectListener {
        void onClick(boolean[] zArr);
    }

    public static void showDialog(CharSequence[] charSequenceArr, String str, int i, Context context, OnItemClickListener onItemClickListener) {
        showDialog(charSequenceArr, null, str, i, context, onItemClickListener, null, true);
    }

    public static void showDialog(CharSequence[] charSequenceArr, int[] iArr, String str, int i, Context context, OnItemClickListener onItemClickListener) {
        showDialog(charSequenceArr, iArr, str, i, context, onItemClickListener, null, true);
    }

    public static void showDialog(CharSequence[] charSequenceArr, int[] iArr, String str, int i, Context context, final OnItemClickListener onItemClickListener, Theme.ResourcesProvider resourcesProvider, boolean z) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(str);
        if (z) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            builder.setView(linearLayout);
            int i2 = 0;
            while (i2 < charSequenceArr.length) {
                RadioColorCell radioColorCell = new RadioColorCell(context);
                radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                radioColorCell.setTag(Integer.valueOf(i2));
                radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground, resourcesProvider), Theme.getColor(Theme.key_dialogRadioBackgroundChecked, resourcesProvider));
                radioColorCell.setTextAndValue(charSequenceArr[i2], i == i2);
                radioColorCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
                linearLayout.addView(radioColorCell);
                radioColorCell.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.utils.ui.PopupUtils$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        PopupUtils.m1519$r8$lambda$mmJcbiSq33fgrEuatbB4zTeZuA(builder, onItemClickListener, view);
                    }
                });
                i2++;
            }
        } else {
            if (iArr != null) {
                builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() { // from class: com.exteragram.messenger.utils.ui.PopupUtils$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        PopupUtils.$r8$lambda$82yGHJGHEfp_DRvEkaoSvyOG5NY(builder, onItemClickListener, dialogInterface, i3);
                    }
                });
            } else {
                builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: com.exteragram.messenger.utils.ui.PopupUtils$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        PopupUtils.m1517$r8$lambda$6jb6sQCYPJdYYKDI25r3PRtHZ4(builder, onItemClickListener, dialogInterface, i3);
                    }
                });
            }
            builder.create();
        }
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.show();
    }

    /* JADX INFO: renamed from: $r8$lambda$mmJcbiSq33fgrEuatbB4zTeZu-A, reason: not valid java name */
    public static /* synthetic */ void m1519$r8$lambda$mmJcbiSq33fgrEuatbB4zTeZuA(AlertDialog.Builder builder, OnItemClickListener onItemClickListener, View view) {
        Integer num = (Integer) view.getTag();
        builder.getDismissRunnable().run();
        onItemClickListener.onClick(num.intValue());
    }

    public static /* synthetic */ void $r8$lambda$82yGHJGHEfp_DRvEkaoSvyOG5NY(AlertDialog.Builder builder, OnItemClickListener onItemClickListener, DialogInterface dialogInterface, int i) {
        builder.getDismissRunnable().run();
        onItemClickListener.onClick(i);
    }

    /* JADX INFO: renamed from: $r8$lambda$6jb6sQCYPJdYYKDI25r3PRtH-Z4, reason: not valid java name */
    public static /* synthetic */ void m1517$r8$lambda$6jb6sQCYPJdYYKDI25r3PRtHZ4(AlertDialog.Builder builder, OnItemClickListener onItemClickListener, DialogInterface dialogInterface, int i) {
        builder.getDismissRunnable().run();
        onItemClickListener.onClick(i);
    }

    /* JADX INFO: renamed from: $r8$lambda$RTfkaKFFZM5qIJnQKtbp3r-qWks, reason: not valid java name */
    public static /* synthetic */ CharSequence[] m1518$r8$lambda$RTfkaKFFZM5qIJnQKtbp3rqWks(int i) {
        return new CharSequence[i];
    }

    public static void showDialogWithoutRadio(ArrayList<? extends CharSequence> arrayList, String str, Context context, OnItemClickListener onItemClickListener) {
        showDialog((CharSequence[]) arrayList.stream().map(new Function() { // from class: com.exteragram.messenger.utils.ui.PopupUtils$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return String.valueOf((CharSequence) obj);
            }
        }).toArray(new IntFunction() { // from class: com.exteragram.messenger.utils.ui.PopupUtils$$ExternalSyntheticLambda6
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return PopupUtils.m1518$r8$lambda$RTfkaKFFZM5qIJnQKtbp3rqWks(i);
            }
        }), null, str, -1, context, onItemClickListener, null, false);
    }

    public static void showMultiSelectDialog(CharSequence[] charSequenceArr, boolean[] zArr, String str, Context context, final OnMultiSelectListener onMultiSelectListener, Theme.ResourcesProvider resourcesProvider) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(str);
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        int i = 0;
        while (i < charSequenceArr.length) {
            final CheckBoxCell checkBoxCell = new CheckBoxCell(context, 4, 21, true, resourcesProvider);
            checkBoxCell.getCheckBoxRound().setColor(Theme.key_switch2TrackChecked, Theme.key_radioBackground, Theme.key_checkboxCheck);
            checkBoxCell.setText(charSequenceArr[i], null, i < zArr.length && zArr[i], i < charSequenceArr.length - 1);
            checkBoxCell.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.utils.ui.PopupUtils$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CheckBoxCell checkBoxCell2 = checkBoxCell;
                    checkBoxCell2.setChecked(!checkBoxCell2.isChecked(), true);
                }
            });
            checkBoxCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 2));
            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, -2));
            i++;
        }
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString(R.string.OK), new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.utils.ui.PopupUtils$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i2) {
                PopupUtils.$r8$lambda$jpgBnZwRK_QOSpnMgBY4clbw2fE(linearLayout, onMultiSelectListener, alertDialog, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.show();
    }

    public static /* synthetic */ void $r8$lambda$jpgBnZwRK_QOSpnMgBY4clbw2fE(LinearLayout linearLayout, OnMultiSelectListener onMultiSelectListener, AlertDialog alertDialog, int i) {
        int childCount = linearLayout.getChildCount();
        boolean[] zArr = new boolean[childCount];
        for (int i2 = 0; i2 < childCount; i2++) {
            zArr[i2] = ((CheckBoxCell) linearLayout.getChildAt(i2)).isChecked();
        }
        onMultiSelectListener.onClick(zArr);
    }
}
