package com.exteragram.messenger.preferences.components;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.preferences.utils.IconShapeHelper;
import com.exteragram.messenger.utils.ui.MonetUtils;
import okhttp3.HttpUrl$$ExternalSyntheticBUOutline0;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;

public class HeaderSettingsCell extends LinearLayout implements CustomPreferenceCell {
    public final RLottieImageView imageView;
    private Path shapePath;
    public final TextView subtitleTextView;
    public final TextView titleTextView;

    public HeaderSettingsCell(Context context) {
        super(context);
        setOrientation(1);
        setGravity(17);
        Drawable drawableMutate = ContextCompat.getDrawable(context, R.drawable.ic_foreground).mutate();
        Theme.ThemeInfo activeTheme = Theme.getActiveTheme();
        int color = ContextCompat.getColor(context, R.color.ic_background);
        if (activeTheme.isMonet() && Build.VERSION.SDK_INT >= 31) {
            color = MonetUtils.getColor(activeTheme.isDark() ? "a2_800" : "a1_100");
            drawableMutate = ContextCompat.getDrawable(context, R.drawable.ic_foreground_solid).mutate();
            drawableMutate.setColorFilter(new PorterDuffColorFilter(MonetUtils.getColor(activeTheme.isDark() ? "a1_200" : "a1_700"), PorterDuff.Mode.MULTIPLY));
        }
        RLottieImageView rLottieImageView = new RLottieImageView(context) { 
            @Override // android.view.View
            public void draw(Canvas canvas) {
                canvas.save();
                canvas.clipPath(HeaderSettingsCell.this.getPath());
                super.draw(canvas);
                canvas.restore();
            }
        };
        this.imageView = rLottieImageView;
        rLottieImageView.setBackgroundColor(color);
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        rLottieImageView.setImageDrawable(drawableMutate);
        addView(rLottieImageView, LayoutHelper.createLinear(72, 72, 49, 0, 28, 0, 0));
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(1, 20.0f);
        textView.setText(LocaleController.getString(R.string.exteraAppName));
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setPadding(0, 0, 0, 0);
        textView.setGravity(17);
        addView(textView, LayoutHelper.createLinear(-2, -2, 49, 50, 16, 50, 0));
        TextView textView2 = new TextView(context);
        this.subtitleTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setTextSize(1, 15.0f);
        textView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        try {
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            StringBuilder sb = new StringBuilder(BuildVars.BUILD_VERSION_STRING);
            if (packageInfo != null) {
                sb.append(" (");
                sb.append(packageInfo.versionCode);
                sb.append(")");
            }
            textView2.setText(sb);
            textView2.setGravity(17);
            textView2.setLines(0);
            textView2.setMaxLines(0);
            textView2.setSingleLine(false);
            textView2.setPadding(0, 0, 0, 0);
            addView(textView2, LayoutHelper.createLinear(-2, -2, 49, 60, 2, 60, 28));
        } catch (PackageManager.NameNotFoundException e) {
            HttpUrl$$ExternalSyntheticBUOutline0.m(e);
            throw null;
        }
    }

    public Path getPath() {
        if (this.shapePath == null) {
            this.shapePath = IconShapeHelper.INSTANCE.getFinalIconShapePath(72.0f, 72.0f, 18.0f);
        }
        return this.shapePath;
    }

    @Override // android.view.View
    public void invalidate() {
        this.shapePath = null;
        this.imageView.invalidate();
        super.invalidate();
    }

    @Override 
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof HeaderSettingsCell) {
            HeaderSettingsCell headerSettingsCell = (HeaderSettingsCell) obj;
            if (this.imageView.equals(headerSettingsCell.imageView) && this.titleTextView.equals(headerSettingsCell.titleTextView) && this.subtitleTextView.equals(headerSettingsCell.subtitleTextView)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(0, 0));
    }
}
