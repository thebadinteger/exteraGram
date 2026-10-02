package com.exteragram.messenger.math.inline;

import android.graphics.Canvas;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import android.widget.TextView;
import com.exteragram.messenger.plugins.PluginsConstants;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationBadge;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\r\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0014\n\u0002\b\u000b\u0018\u0000 >2\u00020\u0001:\u0002>?B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u001e\u001a\u00020\u001fJ\u0006\u0010 \u001a\u00020\rJ\u0006\u0010!\u001a\u00020\rJ6\u0010\"\u001a\u00020\r2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&2\u0006\u0010'\u001a\u00020\t2\u0006\u0010(\u001a\u00020\t2\u0006\u0010)\u001a\u00020\t2\u0006\u0010*\u001a\u00020+J&\u0010,\u001a\u00020\r2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&2\u0006\u0010(\u001a\u00020\t2\u0006\u0010*\u001a\u00020+J\u000e\u0010-\u001a\u00020\u001f2\u0006\u0010\u000e\u001a\u00020\u000bJ\u000e\u0010.\u001a\u00020\u001f2\u0006\u0010/\u001a\u000200J&\u00101\u001a\u00020\u001f2\u0006\u00102\u001a\u00020\t2\u0006\u00103\u001a\u00020\t2\u0006\u00104\u001a\u0002052\u0006\u00106\u001a\u000205J(\u00107\u001a\u00020\r2\u0006\u0010%\u001a\u00020&2\u0006\u00108\u001a\u00020\u00072\u0006\u00109\u001a\u00020\t2\u0006\u0010:\u001a\u00020\tH\u0002J*\u0010;\u001a\u0004\u0018\u00010\u00072\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&2\u0006\u0010<\u001a\u00020+2\u0006\u0010=\u001a\u00020\tH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u000f\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\t@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u001e\u0010\u0012\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\t@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u001e\u0010\u0014\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u001e\u0010\u0017\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\t@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0011R\u001e\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000b@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u001e\u0010\u001c\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000b@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001b¨\u0006@"}, d2 = {"Lcom/exteragram/messenger/math/inline/GhostTextLayout;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "ghostAlpha", "Lcom/exteragram/messenger/math/inline/GhostTextLayout$GhostAlphaSpan;", "layout", "Landroid/text/StaticLayout;", "insertOffset", _UrlKt.FRAGMENT_ENCODE_SET, "drawTop", _UrlKt.FRAGMENT_ENCODE_SET, "movedText", _UrlKt.FRAGMENT_ENCODE_SET, "value", "paragraphStart", "getParagraphStart", "()I", "paragraphEnd", "getParagraphEnd", "detached", "getDetached", "()Z", "extraHeight", "getExtraHeight", "cursorShiftX", "getCursorShiftX", "()F", "cursorShiftY", "getCursorShiftY", "clear", _UrlKt.FRAGMENT_ENCODE_SET, "isEmpty", "hasMovedText", "build", PluginsConstants.Settings.VIEW, "Landroid/widget/TextView;", "real", "Landroid/text/Layout;", "start", "end", "caret", "insert", _UrlKt.FRAGMENT_ENCODE_SET, "buildDetached", "setAlpha", "draw", "canvas", "Landroid/graphics/Canvas;", "readInsertedPositions", "from", NotificationBadge.NewHtcHomeBadger.COUNT, "x", _UrlKt.FRAGMENT_ENCODE_SET, "y", "moved", "shadow", "realOffset", "shadowOffset", "newLayout", "text", "width", "Companion", "GhostAlphaSpan", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class GhostTextLayout {
    private float cursorShiftX;
    private float cursorShiftY;
    private boolean detached;
    private float drawTop;
    private int extraHeight;
    private final GhostAlphaSpan ghostAlpha = new GhostAlphaSpan();
    private int insertOffset;
    private StaticLayout layout;
    private boolean movedText;
    private int paragraphEnd;
    private int paragraphStart;

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0007\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0017\u0010\b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u0005H\u0016¢\u0006\u0004\b\b\u0010\tR\"\u0010\u000b\u001a\u00020\n8\u0006@\u0006X\u0086\u000e¢\u0006\u0012\n\u0004\b\u000b\u0010\f\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010¨\u0006\u0011"}, d2 = {"Lcom/exteragram/messenger/math/inline/GhostTextLayout$GhostAlphaSpan;", "Landroid/text/style/CharacterStyle;", "Landroid/text/style/UpdateAppearance;", "<init>", "()V", "Landroid/text/TextPaint;", "tp", _UrlKt.FRAGMENT_ENCODE_SET, "updateDrawState", "(Landroid/text/TextPaint;)V", _UrlKt.FRAGMENT_ENCODE_SET, "alpha", "F", "getAlpha", "()F", "setAlpha", "(F)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class GhostAlphaSpan extends CharacterStyle implements UpdateAppearance {
        private float alpha = 1.0f;

        public final void setAlpha(float f) {
            this.alpha = f;
        }

        @Override // android.text.style.CharacterStyle
        public void updateDrawState(TextPaint tp) {
            tp.setAlpha((int) (tp.getAlpha() * this.alpha));
        }
    }

    public final int getParagraphStart() {
        return this.paragraphStart;
    }

    public final int getParagraphEnd() {
        return this.paragraphEnd;
    }

    public final boolean getDetached() {
        return this.detached;
    }

    public final int getExtraHeight() {
        return this.extraHeight;
    }

    public final float getCursorShiftX() {
        return this.cursorShiftX;
    }

    public final float getCursorShiftY() {
        return this.cursorShiftY;
    }

    public final void clear() {
        this.layout = null;
        this.insertOffset = 0;
        this.drawTop = 0.0f;
        this.movedText = false;
        this.detached = false;
        this.paragraphStart = 0;
        this.paragraphEnd = 0;
        this.extraHeight = 0;
        this.cursorShiftX = 0.0f;
        this.cursorShiftY = 0.0f;
    }

    public final boolean isEmpty() {
        return this.layout == null;
    }

    /* JADX INFO: renamed from: hasMovedText, reason: from getter */
    public final boolean getMovedText() {
        return this.movedText;
    }

    public final boolean build(TextView view, Layout real, int start, int end, int caret, CharSequence insert) {
        int width;
        clear();
        CharSequence text = view.getText();
        boolean z = false;
        if (text == null || start < 0 || end > text.length() || caret < start || caret > end || (width = real.getWidth()) <= 0) {
            return false;
        }
        int i = caret - start;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text, start, end);
        spannableStringBuilder.insert(i, insert);
        spannableStringBuilder.setSpan(this.ghostAlpha, i, insert.length() + i, 33);
        StaticLayout staticLayoutNewLayout = newLayout(view, real, spannableStringBuilder, width);
        if (staticLayoutNewLayout == null) {
            return false;
        }
        int lineForOffset = real.getLineForOffset(start);
        int lineForOffset2 = real.getLineForOffset(end);
        this.layout = staticLayoutNewLayout;
        this.insertOffset = i;
        this.paragraphStart = start;
        this.paragraphEnd = end;
        this.drawTop = real.getLineTop(lineForOffset);
        this.extraHeight = Math.max(0, staticLayoutNewLayout.getHeight() - (real.getLineTop(lineForOffset2 + 1) - real.getLineTop(lineForOffset)));
        if (i > 0 && moved(real, staticLayoutNewLayout, (start + i) - 1, i - 1)) {
            z = true;
        }
        this.movedText = z;
        this.cursorShiftX = staticLayoutNewLayout.getPrimaryHorizontal(i) - real.getPrimaryHorizontal(caret);
        this.cursorShiftY = (staticLayoutNewLayout.getLineTop(staticLayoutNewLayout.getLineForOffset(i)) + this.drawTop) - real.getLineTop(real.getLineForOffset(caret));
        return true;
    }

    public final boolean buildDetached(TextView view, Layout real, int end, CharSequence insert) {
        clear();
        int width = real.getWidth();
        if (width <= 0) {
            return false;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(insert);
        spannableStringBuilder.setSpan(this.ghostAlpha, 0, spannableStringBuilder.length(), 33);
        StaticLayout staticLayoutNewLayout = newLayout(view, real, spannableStringBuilder, width);
        if (staticLayoutNewLayout == null) {
            return false;
        }
        this.layout = staticLayoutNewLayout;
        this.detached = true;
        this.drawTop = real.getLineBottom(real.getLineForOffset(end));
        this.extraHeight = staticLayoutNewLayout.getHeight();
        return true;
    }

    public final void setAlpha(float value) {
        this.ghostAlpha.setAlpha(value);
    }

    public final void draw(Canvas canvas) {
        StaticLayout staticLayout = this.layout;
        if (staticLayout == null) {
            return;
        }
        canvas.save();
        canvas.translate(0.0f, this.drawTop);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    public final void readInsertedPositions(int from, int count, float[] x, float[] y) {
        StaticLayout staticLayout = this.layout;
        if (staticLayout == null) {
            return;
        }
        for (int i = 0; i < count; i++) {
            int i2 = this.insertOffset + from + i;
            x[i] = staticLayout.getPrimaryHorizontal(i2);
            y[i] = this.drawTop + staticLayout.getLineBaseline(staticLayout.getLineForOffset(i2));
        }
    }

    private final boolean moved(Layout real, StaticLayout shadow, int realOffset, int shadowOffset) {
        return Math.abs(shadow.getPrimaryHorizontal(shadowOffset) - real.getPrimaryHorizontal(realOffset)) >= 0.5f || Math.abs(((float) shadow.getLineTop(shadow.getLineForOffset(shadowOffset))) - (((float) real.getLineTop(real.getLineForOffset(realOffset))) - this.drawTop)) >= 0.5f;
    }

    private final StaticLayout newLayout(TextView view, Layout real, CharSequence text, int width) {
        try {
            StaticLayout.Builder textDirection = StaticLayout.Builder.obtain(text, 0, text.length(), view.getPaint(), width).setAlignment(real.getAlignment()).setLineSpacing(view.getLineSpacingExtra(), view.getLineSpacingMultiplier()).setIncludePad(view.getIncludeFontPadding()).setBreakStrategy(view.getBreakStrategy()).setHyphenationFrequency(view.getHyphenationFrequency()).setTextDirection(TextDirectionHeuristics.FIRSTSTRONG_LTR);
            int i = Build.VERSION.SDK_INT;
            if (i >= 26) {
                textDirection.setJustificationMode(view.getJustificationMode());
            }
            if (i >= 28) {
                textDirection.setUseLineSpacingFromFallbacks(view.isFallbackLineSpacing());
            }
            return textDirection.build();
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }
}
