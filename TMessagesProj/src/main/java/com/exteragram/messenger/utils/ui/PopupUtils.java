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
                radioColorCell.setOnClickListener(new View.OnClickListener() { 
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        PopupUtils.m1519$r8$lambda$mmJcbiSq33fgrEuatbB4zTeZuA(builder, onItemClickListener, view);
                    }
                });
                i2++;
            }
        } else {
            if (iArr != null) {
                builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() { 
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        PopupUtils.$r8$lambda$82yGHJGHEfp_DRvEkaoSvyOG5NY(builder, onItemClickListener, dialogInterface, i3);
                    }
                });
            } else {
                builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() { 
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

    public static void $r8$lambda$jpgBnZwRK_QOSpnMgBY4clbw2fE(LinearLayout linearLayout, OnMultiSelectListener onMultiSelectListener, AlertDialog alertDialog, int i) {
        int childCount = linearLayout.getChildCount();
        boolean[] zArr = new boolean[childCount];
        for (int i2 = 0; i2 < childCount; i2++) {
            zArr[i2] = ((CheckBoxCell) linearLayout.getChildAt(i2)).isChecked();
        }
        onMultiSelectListener.onClick(zArr);
    }
}
