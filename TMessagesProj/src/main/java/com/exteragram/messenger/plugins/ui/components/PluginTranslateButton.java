package com.exteragram.messenger.plugins.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.content.ContextCompat;
import c.f$$ExternalSyntheticBUOutline1;
import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003\u0018\u0000 !2\u00020\u0001:\u0001!B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u0005J\b\u0010\u0018\u001a\u00020\u0017H\u0002J\u0010\u0010\u0019\u001a\u00020\u00172\u0006\u0010\u001a\u001a\u00020\u001bH\u0014J(\u0010\u001c\u001a\u00020\u00172\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001d\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001fH\u0002R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015¨\u0006\""}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/PluginTranslateButton;", "Landroid/view/View;", "context", "Landroid/content/Context;", "color", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Landroid/content/Context;I)V", "translateIcon", "Landroid/graphics/drawable/Drawable;", "resetIcon", "progressDrawable", "Lorg/telegram/ui/Components/CircularProgressDrawable;", "loadingProgress", "Lorg/telegram/ui/Components/AnimatedFloat;", "switchProgress", "icon", "previousIcon", "value", "state", "getState", "()I", "setState", _UrlKt.FRAGMENT_ENCODE_SET, "updateContentDescription", "onDraw", "canvas", "Landroid/graphics/Canvas;", "drawIcon", "drawable", "alpha", _UrlKt.FRAGMENT_ENCODE_SET, "scale", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nPluginTranslateButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PluginTranslateButton.kt\ncom/exteragram/messenger/plugins/ui/components/PluginTranslateButton\n+ 2 Canvas.kt\nandroidx/core/graphics/CanvasKt\n*L\n1#1,134:1\n44#2,8:135\n81#2,8:143\n*S KotlinDebug\n*F\n+ 1 PluginTranslateButton.kt\ncom/exteragram/messenger/plugins/ui/components/PluginTranslateButton\n*L\n84#1:135,8\n123#1:143,8\n*E\n"})
public final class PluginTranslateButton extends View {
    public static final int STATE_LOADING = 1;
    public static final int STATE_RESET = 2;
    public static final int STATE_TRANSLATE = 0;
    private Drawable icon;
    private final AnimatedFloat loadingProgress;
    private Drawable previousIcon;
    private final CircularProgressDrawable progressDrawable;
    private final Drawable resetIcon;
    private int state;
    private final AnimatedFloat switchProgress;
    private final Drawable translateIcon;

    public PluginTranslateButton(Context context, int i) {
        super(context);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.msg_translate);
        if (drawable == null) {
            f$$ExternalSyntheticBUOutline1.m("Required value was null.");
            throw null;
        }
        Drawable drawableMutate = drawable.mutate();
        this.translateIcon = drawableMutate;
        Drawable drawable2 = ContextCompat.getDrawable(context, R.drawable.photo_undo2);
        if (drawable2 == null) {
            f$$ExternalSyntheticBUOutline1.m("Required value was null.");
            throw null;
        }
        Drawable drawableMutate2 = drawable2.mutate();
        this.resetIcon = drawableMutate2;
        this.progressDrawable = new CircularProgressDrawable(i);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.loadingProgress = new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator);
        this.switchProgress = new AnimatedFloat(1.0f, this, 0L, 250L, cubicBezierInterpolator);
        this.icon = drawableMutate;
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN);
        drawableMutate.setColorFilter(porterDuffColorFilter);
        drawableMutate2.setColorFilter(porterDuffColorFilter);
        updateContentDescription();
    }

    public final int getState() {
        return this.state;
    }

    public final void setState(int state) {
        if (this.state == state) {
            return;
        }
        this.state = state;
        if (state != 1) {
            Drawable drawable = state == 2 ? this.resetIcon : this.translateIcon;
            Drawable drawable2 = this.icon;
            if (drawable != drawable2) {
                this.previousIcon = drawable2;
                this.icon = drawable;
                this.switchProgress.set(0.0f, true);
            }
        }
        updateContentDescription();
        invalidate();
    }

    private final void updateContentDescription() {
        setContentDescription(LocaleController.getString(this.state == 2 ? R.string.ShowOriginalButton : R.string.TranslateMessage));
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        float f = this.loadingProgress.set(this.state == 1 ? 1.0f : 0.0f);
        int iDp = AndroidUtilities.dp(10.0f);
        if (f < 1.0f) {
            float f2 = this.switchProgress.set(1.0f);
            float f3 = 1.0f - f;
            float fLerp = AndroidUtilities.lerp(1.0f, 0.6f, f);
            int iSave = canvas.save();
            canvas.translate(0.0f, (-iDp) * f);
            try {
                Drawable drawable = this.previousIcon;
                if (drawable != null) {
                    if (f2 < 1.0f) {
                        drawIcon(canvas, drawable, (1.0f - f2) * f3, AndroidUtilities.lerp(1.0f, 0.4f, f2) * fLerp);
                    } else {
                        this.previousIcon = null;
                    }
                }
                drawIcon(canvas, this.icon, f3 * f2, fLerp * AndroidUtilities.lerp(0.4f, 1.0f, f2));
                canvas.restoreToCount(iSave);
            } catch (Throwable th) {
                canvas.restoreToCount(iSave);
                throw th;
            }
        }
        if (f > 0.0f) {
            int i = (int) ((1.0f - f) * iDp);
            this.progressDrawable.setBounds(0, i, getWidth(), getHeight() + i);
            this.progressDrawable.setAlpha((int) (255.0f * f));
            this.progressDrawable.draw(canvas);
            invalidate();
        }
    }

    private final void drawIcon(Canvas canvas, Drawable drawable, float alpha, float scale) {
        if (alpha <= 0.0f) {
            return;
        }
        int width = (getWidth() - drawable.getIntrinsicWidth()) / 2;
        int height = (getHeight() - drawable.getIntrinsicHeight()) / 2;
        drawable.setBounds(width, height, drawable.getIntrinsicWidth() + width, drawable.getIntrinsicHeight() + height);
        drawable.setAlpha((int) (255.0f * alpha));
        float width2 = getWidth() / 2.0f;
        float height2 = getHeight() / 2.0f;
        int iSave = canvas.save();
        canvas.scale(scale, scale, width2, height2);
        try {
            drawable.draw(canvas);
        } finally {
            canvas.restoreToCount(iSave);
        }
    }
}
