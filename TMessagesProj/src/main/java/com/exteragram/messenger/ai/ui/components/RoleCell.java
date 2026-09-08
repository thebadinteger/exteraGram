package com.exteragram.messenger.ai.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.exteragram.messenger.ai.data.Role;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

@SuppressLint({"ViewConstructor"})
public class RoleCell extends FrameLayout {
    private final int currentAccount;
    private final BackupImageView emojiView;
    private boolean needDivider;
    private final RadioButton radioButton;
    private final TextView subtitleView;
    private final TextView titleView;

    public RoleCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = i;
        setWillNotDraw(false);
        BackupImageView backupImageView = new BackupImageView(context);
        this.emojiView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrame(32, 32.0f, 19, 18.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 19, 68.0f, 0.0f, 62.0f, 0.0f));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        textView.setTextSize(1, 16.0f);
        textView.setSingleLine(true);
        textView.setMaxLines(1);
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView.setEllipsize(truncateAt);
        textView.setGravity(3);
        textView.setIncludeFontPadding(false);
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        TextView textView2 = new TextView(context);
        this.subtitleView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
        textView2.setTextSize(1, 13.0f);
        textView2.setSingleLine(true);
        textView2.setMaxLines(1);
        textView2.setEllipsize(truncateAt);
        textView2.setGravity(3);
        textView2.setIncludeFontPadding(false);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 4.0f, 0.0f, 0.0f));
        RadioButton radioButton = new RadioButton(context);
        this.radioButton = radioButton;
        radioButton.setSize(AndroidUtilities.dp(20.0f));
        radioButton.setColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_radioBackgroundChecked));
        addView(radioButton, LayoutHelper.createFrame(48, 48.0f, 21, 0.0f, 0.0f, 8.0f, 0.0f));
    }

    public RadioButton getRadioButton() {
        return this.radioButton;
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), TLObject.FLAG_30));
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(AndroidUtilities.dp(68.0f), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    public void set(Role role, boolean z, boolean z2) {
        this.needDivider = z2;
        this.titleView.setText(role.getName());
        this.subtitleView.setText(role.getPrompt());
        this.subtitleView.setVisibility(0);
        this.radioButton.setChecked(z, true);
        this.emojiView.setAnimatedEmojiDrawable(new AnimatedEmojiDrawable(3, this.currentAccount, role.getEmojiId() != 0 ? role.getEmojiId() : 5359441070201513074L));
    }

    public static class Factory extends UItem.UItemFactory<RoleCell> {
        static {
            UItem.UItemFactory.setup(new Factory());
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public RoleCell createView(Context context, RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
            return new RoleCell(context, i, resourcesProvider);
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public void bindView(View view, UItem uItem, boolean z, UniversalAdapter universalAdapter, UniversalRecyclerView universalRecyclerView) {
            if (view instanceof RoleCell) {
                RoleCell roleCell = (RoleCell) view;
                Object obj = uItem.object;
                if (obj instanceof Role) {
                    roleCell.set((Role) obj, uItem.checked, z);
                    roleCell.getRadioButton().setOnClickListener(uItem.clickCallback);
                }
            }
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean contentsEquals(UItem uItem, UItem uItem2) {
            Object obj = uItem.object;
            if (obj instanceof Role) {
                Role role = (Role) obj;
                Object obj2 = uItem2.object;
                if (obj2 instanceof Role) {
                    Role role2 = (Role) obj2;
                    if (uItem.checked == uItem2.checked && TextUtils.equals(role.getName(), role2.getName()) && TextUtils.equals(role.getPrompt(), role2.getPrompt()) && role.getEmojiId() == role2.getEmojiId()) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static UItem asRoleCell(Role role, View.OnClickListener onClickListener) {
            UItem uItemOfFactory = UItem.ofFactory(Factory.class);
            uItemOfFactory.object = role;
            uItemOfFactory.checked = role.isSelected();
            uItemOfFactory.clickCallback = onClickListener;
            return uItemOfFactory;
        }
    }
}
