package com.exteragram.messenger.math.inline;

import android.graphics.Canvas;
import android.graphics.Region;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.MetricAffectingSpan;
import android.view.KeyEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.math.MathExpression;
import com.exteragram.messenger.math.MathOptions;
import com.exteragram.messenger.math.MathSuggestion;
import com.exteragram.messenger.plugins.PluginsConstants;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.QuoteSpan;
import org.telegram.ui.Components.TextStyleSpan;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u008a\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\r\n\u0002\b\u0006\u0018\u0000 M2\u00020\u0001:\u0002LMB\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0006\u0010\"\u001a\u00020\u0013J\u0006\u0010#\u001a\u00020\u0011J\u0006\u0010$\u001a\u00020%J\u0006\u0010&\u001a\u00020%J\u0016\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020\u0013J.\u0010,\u001a\u00020(2\u0006\u0010)\u001a\u00020*2\u0006\u0010-\u001a\u00020\u00132\u0006\u0010+\u001a\u00020\u00132\u0006\u0010.\u001a\u00020%2\u0006\u0010/\u001a\u00020%J\u0006\u00100\u001a\u00020(J\u0006\u00101\u001a\u00020(J\u0006\u00102\u001a\u00020(J\u000e\u00103\u001a\u00020(2\u0006\u00104\u001a\u00020\u0011J\u0006\u00105\u001a\u00020(J\u0006\u00106\u001a\u00020\u0011J\b\u00107\u001a\u00020(H\u0002J\b\u00108\u001a\u00020\u0011H\u0002J\b\u00109\u001a\u00020(H\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0002J\b\u0010:\u001a\u00020(H\u0002J\u000e\u0010;\u001a\u00020\u00112\u0006\u0010<\u001a\u00020=J\u000e\u0010>\u001a\u00020?2\u0006\u0010@\u001a\u00020?J\b\u0010A\u001a\u00020\u0011H\u0002J\b\u0010B\u001a\u00020\u0011H\u0002J\u0016\u0010C\u001a\u00020\u00112\f\u0010D\u001a\b\u0012\u0004\u0012\u00020(0EH\u0002J \u0010F\u001a\u00020\u00112\u0006\u0010G\u001a\u00020H2\u0006\u0010I\u001a\u00020\u00132\u0006\u0010J\u001a\u00020\u0013H\u0002J\b\u0010K\u001a\u00020(H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006N"}, d2 = {"Lcom/exteragram/messenger/math/inline/InlineMathController;", _UrlKt.FRAGMENT_ENCODE_SET, PluginsConstants.Settings.VIEW, "Landroid/widget/TextView;", "delegate", "Lcom/exteragram/messenger/math/inline/InlineMathController$Delegate;", "<init>", "(Landroid/widget/TextView;Lcom/exteragram/messenger/math/inline/InlineMathController$Delegate;)V", "ghost", "Lcom/exteragram/messenger/math/inline/GhostTextLayout;", "appear", "Lorg/telegram/ui/Components/AnimatedFloat;", "reveal", "Lcom/exteragram/messenger/math/inline/MathRevealAnimation;", "suggestion", "Lcom/exteragram/messenger/math/MathSuggestion;", "dirty", _UrlKt.FRAGMENT_ENCODE_SET, "layoutWidth", _UrlKt.FRAGMENT_ENCODE_SET, "announcedValue", _UrlKt.FRAGMENT_ENCODE_SET, "announce", "Ljava/lang/Runnable;", "options", "Lcom/exteragram/messenger/math/MathOptions;", "optionsLocale", "Ljava/util/Locale;", "undoStart", "undoEnd", "suppressedAt", "insertingSelf", "caretMovedByTouch", "swallowKeyCode", "getExtraBottom", "hasCursorShift", "getCursorShiftX", _UrlKt.FRAGMENT_ENCODE_SET, "getCursorShiftY", "clipReplacedParagraph", _UrlKt.FRAGMENT_ENCODE_SET, "canvas", "Landroid/graphics/Canvas;", "top", "draw", "left", "clipTop", "clipBottom", "invalidateState", "onTextChanged", "onTouchDown", "onFocusChanged", "focused", "cancel", "updateOnMeasure", "schedule", "canTrigger", "updateAnnouncement", PluginsConstants.UPDATE, "onKeyEvent", "event", "Landroid/view/KeyEvent;", "wrap", "Landroid/view/inputmethod/InputConnection;", "connection", "commit", "undo", "edit", "block", "Lkotlin/Function0;", "hasUnsupportedSpans", "text", _UrlKt.FRAGMENT_ENCODE_SET, "start", "end", "clearUndo", "Delegate", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nInlineMathController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InlineMathController.kt\ncom/exteragram/messenger/math/inline/InlineMathController\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,430:1\n13213#2,2:431\n*S KotlinDebug\n*F\n+ 1 InlineMathController.kt\ncom/exteragram/messenger/math/inline/InlineMathController\n*L\n422#1:431,2\n*E\n"})
public final class InlineMathController {
    private String announcedValue;
    private final AnimatedFloat appear;
    private boolean caretMovedByTouch;
    private final Delegate delegate;
    private boolean insertingSelf;
    private Locale optionsLocale;
    private final MathRevealAnimation reveal;
    private MathSuggestion suggestion;
    private int swallowKeyCode;
    private final TextView view;
    private final GhostTextLayout ghost = new GhostTextLayout();
    private boolean dirty = true;
    private int layoutWidth = -1;
    private final Runnable announce = new Runnable() { // from class: com.exteragram.messenger.math.inline.InlineMathController$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            InlineMathController.m1525$r8$lambda$bC5G05WbzVXNEF_Hy5TiXUXrI(InlineMathController.this);
        }
    };
    private MathOptions options = new MathOptions('.');
    private int undoStart = -1;
    private int undoEnd = -1;
    private int suppressedAt = -1;

    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&¨\u0006\bÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/math/inline/InlineMathController$Delegate;", _UrlKt.FRAGMENT_ENCODE_SET, "runProgrammatic", _UrlKt.FRAGMENT_ENCODE_SET, "action", "Ljava/lang/Runnable;", "accentColor", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface Delegate {
        int accentColor();

        void runProgrammatic(Runnable action);
    }

    public InlineMathController(TextView textView, Delegate delegate) {
        this.view = textView;
        this.delegate = delegate;
        this.appear = new AnimatedFloat(textView, 0L, 120L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.reveal = new MathRevealAnimation(textView, new Runnable() { // from class: com.exteragram.messenger.math.inline.InlineMathController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                InlineMathController.this.dirty = true;
            }
        });
    }

    /* JADX INFO: renamed from: $r8$lambda$bC5G05WbzVXNEF-_Hy-5TiXUXrI, reason: not valid java name */
    public static void m1525$r8$lambda$bC5G05WbzVXNEF_Hy5TiXUXrI(InlineMathController inlineMathController) {
        MathSuggestion mathSuggestion = inlineMathController.suggestion;
        String value = mathSuggestion != null ? mathSuggestion.getValue() : null;
        if (value != null) {
            inlineMathController.announcedValue = value;
            AndroidUtilities.makeAccessibilityAnnouncement(LocaleController.formatString(R.string.InlineMathResultAnnouncement, value));
        }
    }

    public final int getExtraBottom() {
        return this.ghost.getExtraHeight();
    }

    public final boolean hasCursorShift() {
        return this.ghost.getMovedText();
    }

    public final float getCursorShiftX() {
        if (hasCursorShift()) {
            return this.ghost.getCursorShiftX();
        }
        return 0.0f;
    }

    public final float getCursorShiftY() {
        if (hasCursorShift()) {
            return this.ghost.getCursorShiftY();
        }
        return 0.0f;
    }

    public final void clipReplacedParagraph(Canvas canvas, int top) {
        Layout layout;
        if (this.ghost.isEmpty() || this.ghost.getDetached() || (layout = this.view.getLayout()) == null) {
            return;
        }
        float f = top;
        canvas.clipRect(0.0f, f + layout.getLineTop(layout.getLineForOffset(this.ghost.getParagraphStart())), this.view.getWidth(), f + layout.getLineBottom(layout.getLineForOffset(this.ghost.getParagraphEnd())), Region.Op.DIFFERENCE);
    }

    public final void draw(Canvas canvas, int left, int top, float clipTop, float clipBottom) {
        boolean zIsRunning = this.reveal.isRunning();
        if (!zIsRunning && this.ghost.isEmpty()) {
            this.appear.set(0.0f, true);
            return;
        }
        canvas.save();
        canvas.clipRect(0.0f, clipTop, this.view.getWidth(), clipBottom);
        canvas.translate(left, top);
        if (zIsRunning) {
            MathRevealAnimation mathRevealAnimation = this.reveal;
            Delegate delegate = this.delegate;
            mathRevealAnimation.draw(canvas, delegate != null ? delegate.accentColor() : this.view.getCurrentTextColor());
        } else {
            this.ghost.setAlpha(this.appear.set(1.0f) * 0.4f);
            this.ghost.draw(canvas);
        }
        canvas.restore();
    }

    public final void invalidateState() {
        if (!this.insertingSelf) {
            int selectionStart = this.view.getSelectionStart();
            if (selectionStart != this.suppressedAt) {
                this.suppressedAt = -1;
            }
            if (selectionStart != this.undoEnd || this.view.getSelectionEnd() != this.undoEnd) {
                clearUndo();
            }
            if (this.reveal.isRunning() && this.reveal.isCaretOutside(selectionStart)) {
                this.reveal.cancel();
            }
        }
        schedule();
    }

    public final void onTextChanged() {
        if (!this.insertingSelf) {
            this.reveal.cancel();
            clearUndo();
            this.suppressedAt = -1;
            this.caretMovedByTouch = false;
        }
        schedule();
    }

    public final void onTouchDown() {
        if (this.caretMovedByTouch) {
            return;
        }
        this.caretMovedByTouch = true;
        schedule();
    }

    public final void onFocusChanged(boolean focused) {
        if (!focused) {
            this.reveal.cancel();
            clearUndo();
        }
        schedule();
    }

    public final void cancel() {
        AndroidUtilities.cancelRunOnUIThread(this.announce);
        this.announcedValue = null;
        this.reveal.cancel();
        clearUndo();
        this.suppressedAt = -1;
        this.caretMovedByTouch = false;
        InlineMathController.this.suggestion = null;
        this.ghost.clear();
        schedule();
    }

    public final boolean updateOnMeasure() {
        int extraHeight = this.ghost.getExtraHeight();
        Layout layout = this.view.getLayout();
        int width = layout != null ? layout.getWidth() : 0;
        if (width != this.layoutWidth) {
            this.layoutWidth = width;
            this.dirty = true;
        }
        update();
        if (AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
            updateAnnouncement();
        }
        return this.ghost.getExtraHeight() != extraHeight;
    }

    private final void schedule() {
        this.dirty = true;
        this.view.invalidate();
        if (this.ghost.getExtraHeight() == 0 && InlineMathController.this.suggestion == null && !canTrigger()) {
            return;
        }
        this.view.requestLayout();
    }

    private final boolean canTrigger() {
        CharSequence text;
        int selectionStart;
        return ExteraConfig.getInlineMathResult() && (text = this.view.getText()) != null && 1 <= (selectionStart = this.view.getSelectionStart()) && selectionStart <= text.length() && text.charAt(selectionStart - 1) == '=';
    }

    private final void updateAnnouncement() {
        MathSuggestion mathSuggestion = InlineMathController.this.suggestion;
        String value = mathSuggestion != null ? mathSuggestion.getValue() : null;
        if (value == null) {
            this.announcedValue = null;
            AndroidUtilities.cancelRunOnUIThread(this.announce);
        } else {
            if (Intrinsics.areEqual(value, this.announcedValue)) {
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.announce);
            AndroidUtilities.runOnUIThread(this.announce, 600L);
        }
    }

    private final MathOptions options() {
        Locale currentLocale = LocaleController.getInstance().getCurrentLocale();
        if (currentLocale == null) {
            currentLocale = Locale.US;
        }
        if (!Intrinsics.areEqual(currentLocale, this.optionsLocale)) {
            this.optionsLocale = currentLocale;
            this.options = new MathOptions(DecimalFormatSymbols.getInstance(currentLocale).getDecimalSeparator());
        }
        return this.options;
    }

    private final void update() {
        Layout layout;
        CharSequence text;
        int selectionStart;
        MathSuggestion mathSuggestionSuggestionAt;
        if (this.dirty) {
            this.dirty = false;
            InlineMathController.this.suggestion = null;
            this.ghost.clear();
            if (!ExteraConfig.getInlineMathResult() || this.reveal.isRunning() || this.suppressedAt >= 0 || !this.view.isFocused() || !this.view.isEnabled() || (layout = this.view.getLayout()) == null || (text = this.view.getText()) == null || (selectionStart = this.view.getSelectionStart()) <= 0 || selectionStart != this.view.getSelectionEnd() || selectionStart > text.length() || (mathSuggestionSuggestionAt = MathExpression.INSTANCE.suggestionAt(text, selectionStart, options())) == null) {
                return;
            }
            if (!(text instanceof Spannable) || BaseInputConnection.getComposingSpanStart((Spannable) text) == -1) {
                if (text instanceof Spanned) {
                    if (!(((Spanned) text).getSpans(selectionStart + (-1), selectionStart, MetricAffectingSpan.class).length == 0)) {
                        return;
                    }
                }
                if (layout.getParagraphDirection(layout.getLineForOffset(selectionStart)) != 1) {
                    return;
                }
                int iLastIndexOf$default = text.toString().lastIndexOf('\n', selectionStart - 1) + 1;
                int iIndexOf$default = text.toString().indexOf('\n', selectionStart);
                if (iIndexOf$default < 0) {
                    iIndexOf$default = text.length();
                }
                int i = iIndexOf$default;
                boolean z = i >= text.length();
                if (hasUnsupportedSpans(text, iLastIndexOf$default, i)) {
                    if (z && this.ghost.buildDetached(this.view, layout, i, mathSuggestionSuggestionAt.getInsertText())) {
                        InlineMathController.this.suggestion = mathSuggestionSuggestionAt;
                        return;
                    }
                    return;
                }
                if (this.ghost.build(this.view, layout, iLastIndexOf$default, i, selectionStart, mathSuggestionSuggestionAt.getInsertText())) {
                    if (((this.ghost.getExtraHeight() > 0 && !z) || (this.ghost.getMovedText() && this.caretMovedByTouch)) && (!z || !this.ghost.buildDetached(this.view, layout, i, mathSuggestionSuggestionAt.getInsertText()))) {
                        this.ghost.clear();
                    } else {
                        InlineMathController.this.suggestion = mathSuggestionSuggestionAt;
                    }
                }
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:33:0x0052  */
    public final boolean onKeyEvent(KeyEvent event) {
        boolean zCommit = false;
        if (event.getAction() == 1) {
            if (this.swallowKeyCode == 0 || event.getKeyCode() != this.swallowKeyCode) {
                return false;
            }
            this.swallowKeyCode = 0;
            return true;
        }
        if (event.getAction() == 0 && event.getRepeatCount() == 0 && !event.isCtrlPressed() && !event.isAltPressed() && !event.isShiftPressed()) {
            int keyCode = event.getKeyCode();
            if (keyCode == 22) {
                zCommit = commit();
            } else if (keyCode == 67) {
                zCommit = undo();
            } else if (keyCode == 61 || keyCode == 62) {
                zCommit = commit();
            }
            if (zCommit) {
                this.swallowKeyCode = event.getKeyCode();
            }
        }
        return zCommit;
    }

    public final InputConnection wrap(InputConnection connection) {
        return new InputConnectionWrapper(connection, true) { // from class: com.exteragram.messenger.math.inline.InlineMathController.wrap.1
            @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
            public boolean commitText(CharSequence text, int newCursorPosition) {
                if (text != null && text.length() == 1 && text.charAt(0) == ' ' && InlineMathController.this.suggestion != null) {
                    finishComposingText();
                    if (InlineMathController.this.commit()) {
                        return true;
                    }
                }
                return super.commitText(text, newCursorPosition);
            }

            @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                if (beforeLength == 1 && afterLength == 0 && InlineMathController.this.undo()) {
                    return true;
                }
                return super.deleteSurroundingText(beforeLength, afterLength);
            }

            @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
            public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) {
                if (beforeLength == 1 && afterLength == 0 && InlineMathController.this.undo()) {
                    return true;
                }
                return super.deleteSurroundingTextInCodePoints(beforeLength, afterLength);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean commit() {
        final MathSuggestion mathSuggestion = InlineMathController.this.suggestion;
        if (mathSuggestion == null) {
            return false;
        }
        CharSequence text = this.view.getText();
        final Editable editable = text instanceof Editable ? (Editable) text : null;
        if (editable == null || mathSuggestion.getInsertAt() != this.view.getSelectionStart() || this.view.getSelectionStart() != this.view.getSelectionEnd() || mathSuggestion.getInsertAt() > editable.length()) {
            return false;
        }
        int length = mathSuggestion.getInsertText().length() - mathSuggestion.getValue().length();
        int length2 = mathSuggestion.getValue().length();
        float[] fArr = new float[length2];
        float[] fArr2 = new float[length2];
        this.ghost.readInsertedPositions(length, length2, fArr, fArr2);
        BaseInputConnection.removeComposingSpans(editable);
        if (!edit(new Function0() { // from class: com.exteragram.messenger.math.inline.InlineMathController$$ExternalSyntheticLambda3
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return InlineMathController.$r8$lambda$qWOovbGAMnapn2V8igAcpMi9X1g(editable, mathSuggestion);
            }
        })) {
            return false;
        }
        Selection.setSelection(editable, mathSuggestion.getInsertAt() + mathSuggestion.getInsertText().length());
        InlineMathController.this.suggestion = null;
        this.ghost.clear();
        this.dirty = true;
        this.reveal.begin(editable, mathSuggestion.getInsertAt() + length, length2, fArr, fArr2);
        this.undoStart = mathSuggestion.getInsertAt();
        this.undoEnd = mathSuggestion.getInsertAt() + mathSuggestion.getInsertText().length();
        this.appear.set(0.0f, true);
        this.view.requestLayout();
        this.view.invalidate();
        return true;
    }

    public static Unit $r8$lambda$qWOovbGAMnapn2V8igAcpMi9X1g(Editable editable, MathSuggestion mathSuggestion) {
        editable.insert(mathSuggestion.getInsertAt(), mathSuggestion.getInsertText());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean undo() {
        if (this.undoStart < 0) {
            return false;
        }
        CharSequence text = this.view.getText();
        final Editable editable = text instanceof Editable ? (Editable) text : null;
        if (editable == null) {
            return false;
        }
        if (this.undoEnd > editable.length() || this.view.getSelectionStart() != this.undoEnd || this.view.getSelectionEnd() != this.undoEnd) {
            clearUndo();
            return false;
        }
        this.reveal.cancel();
        int i = this.undoStart;
        if (!edit(new Function0() { // from class: com.exteragram.messenger.math.inline.InlineMathController$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return InlineMathController.m1526$r8$lambda$laHnhw0LcXrrnF6Rszt1kM4BwM(editable, InlineMathController.this);
            }
        })) {
            return false;
        }
        Selection.setSelection(editable, i);
        clearUndo();
        this.suppressedAt = i;
        this.dirty = true;
        this.view.requestLayout();
        this.view.invalidate();
        return true;
    }

    /* JADX INFO: renamed from: $r8$lambda$laHnhw0LcXrrnF6-Rszt1kM4BwM, reason: not valid java name */
    public static Unit m1526$r8$lambda$laHnhw0LcXrrnF6Rszt1kM4BwM(Editable editable, InlineMathController inlineMathController) {
        editable.delete(inlineMathController.undoStart, inlineMathController.undoEnd);
        return Unit.INSTANCE;
    }

    private final boolean edit(final Function0<Unit> block) {
        final Ref.BooleanRef booleanRef = new Ref.BooleanRef();
        booleanRef.element = true;
        Runnable runnable = new Runnable() { // from class: com.exteragram.messenger.math.inline.InlineMathController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                InlineMathController.$r8$lambda$guhZXD64x1PCf_PzIoYeK7FQ_rY(InlineMathController.this, block, booleanRef);
            }
        };
        Delegate delegate = this.delegate;
        if (delegate != null) {
            delegate.runProgrammatic(runnable);
        } else {
            runnable.run();
        }
        return booleanRef.element;
    }

    public static void $r8$lambda$guhZXD64x1PCf_PzIoYeK7FQ_rY(InlineMathController inlineMathController, Function0 function0, Ref.BooleanRef booleanRef) {
        inlineMathController.insertingSelf = true;
        try {
            function0.invoke();
        } catch (Exception e) {
            FileLog.e(e);
            booleanRef.element = false;
        } finally {
            inlineMathController.insertingSelf = false;
        }
    }

    private final boolean hasUnsupportedSpans(CharSequence text, int start, int end) {
        if (!(text instanceof Spanned)) {
            return false;
        }
        Spanned spanned = (Spanned) text;
        if (!(spanned.getSpans(start, end, AnimatedEmojiSpan.class).length == 0)) {
            return true;
        }
        if (!(spanned.getSpans(start, end, QuoteSpan.class).length == 0)) {
            return true;
        }
        Object[] spans = spanned.getSpans(start, end, TextStyleSpan.class);
        for (Object obj : spans) {
            if (((TextStyleSpan) obj).isSpoiler()) {
                return true;
            }
        }
        return false;
    }

    private final void clearUndo() {
        this.undoStart = -1;
        this.undoEnd = -1;
    }
}
