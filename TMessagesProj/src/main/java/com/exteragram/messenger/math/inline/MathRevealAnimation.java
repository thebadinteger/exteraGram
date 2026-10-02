package com.exteragram.messenger.math.inline;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.plugins.PluginsConstants;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationBadge;
import org.telegram.ui.Components.CubicBezierInterpolator;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 .2\u00020\u0001:\u0002./B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0006\u0010\u001a\u001a\u00020\u001bJ\u000e\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u001d\u001a\u00020\rJ.\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\r2\u0006\u0010#\u001a\u00020\r2\u0006\u0010$\u001a\u00020\u00142\u0006\u0010%\u001a\u00020\u0014J\u0006\u0010&\u001a\u00020\u001fJ\u0016\u0010'\u001a\u00020\u001f2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020\rJ\u0010\u0010+\u001a\u00020\u001f2\u0006\u0010,\u001a\u00020\u001bH\u0002J\b\u0010-\u001a\u00020\u001fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e¢\u0006\u0002\n\u0000¨\u00060"}, d2 = {"Lcom/exteragram/messenger/math/inline/MathRevealAnimation;", _UrlKt.FRAGMENT_ENCODE_SET, PluginsConstants.Settings.VIEW, "Landroid/widget/TextView;", "onFinished", "Ljava/lang/Runnable;", "<init>", "(Landroid/widget/TextView;Ljava/lang/Runnable;)V", "paint", "Landroid/text/TextPaint;", "maskSpan", "Lcom/exteragram/messenger/math/inline/MathRevealAnimation$MaskSpan;", "rangeStart", _UrlKt.FRAGMENT_ENCODE_SET, "rangeEnd", "startedAt", _UrlKt.FRAGMENT_ENCODE_SET, "duration", _UrlKt.FRAGMENT_ENCODE_SET, "fromX", _UrlKt.FRAGMENT_ENCODE_SET, "fromY", "toX", "toY", "targetLayout", "Landroid/text/Layout;", "isRunning", _UrlKt.FRAGMENT_ENCODE_SET, "isCaretOutside", "caret", "begin", _UrlKt.FRAGMENT_ENCODE_SET, "text", "Landroid/text/Editable;", "from", NotificationBadge.NewHtcHomeBadger.COUNT, "originX", "originY", "cancel", "draw", "canvas", "Landroid/graphics/Canvas;", PluginsConstants.Settings.ACCENT, "stop", "deferred", "removeMasks", "Companion", "MaskSpan", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nMathRevealAnimation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MathRevealAnimation.kt\ncom/exteragram/messenger/math/inline/MathRevealAnimation\n+ 2 Canvas.kt\nandroidx/core/graphics/CanvasKt\n*L\n1#1,162:1\n81#2,8:163\n*S KotlinDebug\n*F\n+ 1 MathRevealAnimation.kt\ncom/exteragram/messenger/math/inline/MathRevealAnimation\n*L\n122#1:163,8\n*E\n"})
public final class MathRevealAnimation {
    private MaskSpan maskSpan;
    private final Runnable onFinished;
    private long startedAt;
    private Layout targetLayout;
    private final TextView view;
    private final TextPaint paint = new TextPaint(1);
    private int rangeStart = -1;
    private int rangeEnd = -1;
    private float duration = 200.0f;
    private float[] fromX = new float[0];
    private float[] fromY = new float[0];
    private float[] toX = new float[0];
    private float[] toY = new float[0];

    public MathRevealAnimation(TextView textView, Runnable runnable) {
        this.view = textView;
        this.onFinished = runnable;
    }

    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lcom/exteragram/messenger/math/inline/MathRevealAnimation$MaskSpan;", "Landroid/text/style/CharacterStyle;", "Landroid/text/style/UpdateAppearance;", "owner", "Lcom/exteragram/messenger/math/inline/MathRevealAnimation;", "<init>", "(Lcom/exteragram/messenger/math/inline/MathRevealAnimation;)V", "updateDrawState", _UrlKt.FRAGMENT_ENCODE_SET, "tp", "Landroid/text/TextPaint;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class MaskSpan extends CharacterStyle implements UpdateAppearance {
        private final MathRevealAnimation owner;

        public MaskSpan(MathRevealAnimation mathRevealAnimation) {
            this.owner = mathRevealAnimation;
        }

        @Override // android.text.style.CharacterStyle
        public void updateDrawState(TextPaint tp) {
            if (this.owner.maskSpan == this) {
                tp.setAlpha(0);
            }
        }
    }

    public final boolean isRunning() {
        return this.rangeStart >= 0;
    }

    public final boolean isCaretOutside(int caret) {
        return caret < this.rangeStart || caret > this.rangeEnd;
    }

    public final void begin(Editable text, int from, int count, float[] originX, float[] originY) {
        this.rangeStart = from;
        this.rangeEnd = from + count;
        this.fromX = originX;
        this.fromY = originY;
        this.toX = new float[count];
        this.toY = new float[count];
        this.targetLayout = null;
        this.startedAt = SystemClock.elapsedRealtime();
        this.duration = Math.min(420.0f, (count * 28.0f) + 200.0f);
        MaskSpan maskSpan = new MaskSpan(this);
        this.maskSpan = maskSpan;
        text.setSpan(maskSpan, this.rangeStart, this.rangeEnd, 33);
    }

    public final void cancel() {
        stop(false);
    }

    public final void draw(Canvas canvas, int accent) {
        Layout layout = this.view.getLayout();
        CharSequence text = this.view.getText();
        if (layout == null || text == null || this.rangeEnd > text.length()) {
            stop(true);
            return;
        }
        int i = this.rangeEnd - this.rangeStart;
        if (this.targetLayout != layout) {
            this.targetLayout = layout;
            for (int i2 = 0; i2 < i; i2++) {
                int i3 = this.rangeStart + i2;
                this.toX[i2] = layout.getPrimaryHorizontal(i3);
                this.toY[i2] = layout.getLineBaseline(layout.getLineForOffset(i3));
            }
        }
        float fElapsedRealtime = SystemClock.elapsedRealtime() - this.startedAt;
        float f = 1.0f;
        float fCoerceIn = RangesKt.coerceIn(fElapsedRealtime / this.duration, 0.0f, 1.0f);
        float fCoerceIn2 = RangesKt.coerceIn(fElapsedRealtime / 800.0f, 0.0f, 1.0f);
        int iBlendARGB = ColorUtils.blendARGB(accent, this.view.getCurrentTextColor(), CubicBezierInterpolator.EASE_BOTH.getInterpolation(fCoerceIn2));
        int iAlpha = Color.alpha(iBlendARGB);
        float textSize = this.view.getPaint().getTextSize() * 0.32f;
        this.paint.set(this.view.getPaint());
        this.paint.setColor(iBlendARGB);
        int i4 = 0;
        while (i4 < i) {
            int i5 = this.rangeStart + i4;
            float fCascade = AndroidUtilities.cascade(fCoerceIn, i4, i, 3.5f);
            float interpolation = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(fCascade);
            float interpolation2 = CubicBezierInterpolator.EASE_OUT_BACK.getInterpolation(fCascade);
            float fLerp = AndroidUtilities.lerp(this.fromX[i4], this.toX[i4], interpolation);
            float fLerp2 = AndroidUtilities.lerp(this.fromY[i4], this.toY[i4], interpolation);
            float fLerp3 = AndroidUtilities.lerp(1.12f, f, interpolation2);
            this.paint.setAlpha((int) (iAlpha * AndroidUtilities.lerp(0.4f, f, interpolation)));
            int i6 = i5 + 1;
            float f2 = f;
            int iSave = canvas.save();
            canvas.scale(fLerp3, fLerp3, fLerp + (this.paint.measureText(text, i5, i6) / 2.0f), fLerp2 - textSize);
            try {
                CharSequence charSequence = text;
                canvas.drawText(charSequence, i5, i6, fLerp, fLerp2, this.paint);
                canvas.restoreToCount(iSave);
                i4++;
                text = charSequence;
                f = f2;
            } catch (Throwable th) {
                canvas.restoreToCount(iSave);
                throw th;
            }
        }
        float f3 = f;
        if (fCoerceIn < f3 || fCoerceIn2 < f3) {
            this.view.invalidate();
        } else {
            stop(true);
        }
    }

    private final void stop(boolean deferred) {
        MaskSpan maskSpan = this.maskSpan;
        if (this.rangeStart >= 0 || maskSpan != null) {
            this.rangeStart = -1;
            this.rangeEnd = -1;
            this.maskSpan = null;
            this.targetLayout = null;
            if (maskSpan != null) {
                if (deferred) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.math.inline.MathRevealAnimation$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            MathRevealAnimation.this.removeMasks();
                        }
                    });
                } else {
                    removeMasks();
                }
            }
            this.onFinished.run();
            this.view.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void removeMasks() {
        CharSequence text = this.view.getText();
        if (text instanceof Spannable) {
            Spannable spannable = (Spannable) text;
            Iterator it = ArrayIteratorKt.iterator(spannable.getSpans(0, spannable.length(), MaskSpan.class));
            while (it.hasNext()) {
                spannable.removeSpan((MaskSpan) it.next());
            }
        }
    }
}
