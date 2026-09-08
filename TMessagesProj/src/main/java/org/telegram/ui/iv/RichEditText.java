package org.telegram.ui.iv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.ui.ActionBar.FloatingActionMode;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.TextStyleSpan;

public class RichEditText extends EditTextCaption {
    private boolean accentHint;
    private boolean allowNewlines;
    private boolean applyingEmptyHint;
    public TL_iv.PageBlock block;
    private boolean centerEmptyHint;
    private boolean ignoreTextChange;
    private boolean insertingNewline;
    private Layout lastMarkLayout;
    private int lastMarkTextLength;
    private Listener listener;
    private boolean locked;
    private final InputFilter lockingFilter;
    private Paint markPaint;
    private LinkPath markPath;
    private boolean markPathDirty;
    private long mathDownTime;
    private float mathDownX;
    private float mathDownY;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean softEnterNewline;
    private int textColorKey;
    private int touchSlop;

    public interface Listener {
        default boolean onBackspaceAtStart(RichEditText richEditText) {
            return false;
        }

        default void onBackspaceOnEmpty(RichEditText richEditText) {
        }

        default void onEnterPressed(RichEditText richEditText) {
        }

        default void onLockedInsert(RichEditText richEditText, CharSequence charSequence) {
        }

        default boolean onPaste(RichEditText richEditText) {
            return false;
        }

        default void onRequestWindowFocusable(RichEditText richEditText, boolean z) {
        }

        default boolean onSelectAll(RichEditText richEditText) {
            return false;
        }

        void onSelectionChanged(RichEditText richEditText, int i, int i2);

        default boolean onTab(RichEditText richEditText, boolean z) {
            return false;
        }

        void onTextChanged(RichEditText richEditText, Editable editable);

        default void onTextWillChange(RichEditText richEditText, int i, int i2) {
        }
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor
    public void extendActionMode(ActionMode actionMode, Menu menu) {
    }

    public void lambda$openMathEditor$3(MathSpan mathSpan, String str) {
        MathSpan mathSpanCreate;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        Editable text = getText();
        int spanStart = text.getSpanStart(mathSpan);
        int spanEnd = text.getSpanEnd(mathSpan);
        if (spanStart < 0 || spanEnd < 0 || (mathSpanCreate = MathSpan.create(str, getCurrentTextColor(), AndroidUtilities.dp(SharedConfig.fontSize + 4))) == null) {
            return;
        }
        boolean z = this.locked;
        if (z) {
            setLocked(false);
        }
        SpannableString spannableString = new SpannableString(" ");
        spannableString.setSpan(mathSpanCreate, 0, 1, 33);
        int iMax = Math.max(0, Math.min(spanStart, length()));
        text.replace(iMax, Math.max(iMax, Math.min(spanEnd, length())), spannableString);
        setSelection(Math.min(iMax + 1, length()));
        if (z) {
            setLocked(true);
        }
    }

    @Override // android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        Listener listener;
        if (keyEvent.getKeyCode() == 61) {
            if (keyEvent.getAction() == 0 && (listener = this.listener) != null) {
                listener.onTab(this, keyEvent.isShiftPressed());
            }
            return true;
        }
        int keyCode = keyEvent.getKeyCode();
        if ((keyCode == 66 || keyCode == 160) && this.listener != null && !this.allowNewlines) {
            if (keyEvent.getAction() == 0) {
                boolean z = (keyEvent.getFlags() & 2) != 0;
                if (this.softEnterNewline && (z || keyEvent.isShiftPressed())) {
                    insertNewlineAtSelection();
                } else {
                    this.listener.onEnterPressed(this);
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 67 && this.listener != null) {
            if (length() == 0) {
                this.listener.onBackspaceOnEmpty(this);
                return true;
            }
            if (getSelectionStart() == 0 && getSelectionEnd() == 0 && this.listener.onBackspaceAtStart(this)) {
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    private void insertNewlineAtSelection() {
        int iMax = Math.max(0, getSelectionStart());
        int iMax2 = Math.max(0, getSelectionEnd());
        if (iMax > iMax2) {
            iMax2 = iMax;
            iMax = iMax2;
        }
        this.insertingNewline = true;
        getText().replace(iMax, iMax2, "\n");
        this.insertingNewline = false;
        setSelection(iMax + 1);
    }

    @Override 
    public boolean onTextContextMenuItem(int i) {
        Listener listener;
        Listener listener2;
        if (i == 16908319 && (listener2 = this.listener) != null && listener2.onSelectAll(this)) {
            return true;
        }
        if (i == 16908322 && (listener = this.listener) != null && listener.onPaste(this)) {
            return true;
        }
        return super.onTextContextMenuItem(i);
    }

    public void updateLongClickForEmpty() {
        setLongClickable(length() == 0);
    }

    @Override // org.telegram.ui.Components.EditTextCaption
    public void notifySpansChanged() {
        super.notifySpansChanged();
        this.markPathDirty = true;
        invalidate();
    }

    @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.Components.EditTextBoldCursor, org.telegram.ui.Components.EditTextEffects, android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        buildMarkPath();
        if (this.markPath != null) {
            if (this.markPaint == null) {
                Paint paint = new Paint(1);
                this.markPaint = paint;
                paint.setPathEffect(LinkPath.getRoundedEffect());
            }
            this.markPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection, this.resourcesProvider) & 872415231);
            canvas.save();
            canvas.translate(getPaddingLeft(), this.offsetY);
            canvas.drawPath(this.markPath, this.markPaint);
            canvas.restore();
        }
        super.onDraw(canvas);
    }

    private void buildMarkPath() {
        int iDp;
        int iDp2;
        Layout layout = getLayout();
        LinkPath linkPath = null;
        if (layout == null) {
            this.markPath = null;
            this.lastMarkLayout = null;
            this.lastMarkTextLength = -1;
            return;
        }
        CharSequence text = layout.getText();
        if (!this.markPathDirty && layout == this.lastMarkLayout && text.length() == this.lastMarkTextLength) {
            return;
        }
        this.markPathDirty = false;
        this.lastMarkLayout = layout;
        this.lastMarkTextLength = text.length();
        this.markPath = null;
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            TextStyleSpan[] textStyleSpanArr = (TextStyleSpan[]) spanned.getSpans(0, spanned.length(), TextStyleSpan.class);
            int length = textStyleSpanArr.length;
            int i = 0;
            while (i < length) {
                TextStyleSpan textStyleSpan = textStyleSpanArr[i];
                int styleFlags = textStyleSpan.getStyleFlags();
                if ((65536 & styleFlags) != 0) {
                    int spanStart = spanned.getSpanStart(textStyleSpan);
                    int spanEnd = spanned.getSpanEnd(textStyleSpan);
                    if (spanStart < 0) {
                        linkPath = linkPath;
                    } else if (spanEnd > spanStart) {
                        if (linkPath == null) {
                            LinkPath linkPath2 = new LinkPath(true);
                            linkPath2.setAllowReset(false);
                            linkPath = linkPath2;
                        }
                        linkPath.setCurrentLayout(layout, spanStart, 0.0f);
                        if ((32768 & styleFlags) != 0) {
                            iDp = -AndroidUtilities.dp(6.0f);
                        } else {
                            iDp = (styleFlags & 16384) != 0 ? AndroidUtilities.dp(2.0f) : 0;
                        }
                        if (iDp != 0) {
                            iDp2 = iDp + AndroidUtilities.dp(iDp > 0 ? 5.0f : -2.0f);
                        } else {
                            iDp2 = 0;
                        }
                        linkPath.setBaselineShift(iDp2);
                        layout.getSelectionPath(spanStart, spanEnd, linkPath);
                    }
                }
                i++;
                linkPath = linkPath;
            }
            if (linkPath != null) {
                linkPath.setAllowReset(true);
            }
            this.markPath = linkPath;
        }
    }

    @Override // org.telegram.ui.Components.EditTextEffects, android.widget.TextView
    public void onSelectionChanged(int i, int i2) {
        super.onSelectionChanged(i, i2);
        Listener listener = this.listener;
        if (listener != null) {
            listener.onSelectionChanged(this, i, i2);
        }
    }

    @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.ActionBar.FloatingToolbar.StyleDelegate
    public int getCurrentStyle(int i, int i2) {
        int iMax;
        int iMin;
        Editable text = getText();
        if (text != null && (iMax = Math.max(0, i)) < (iMin = Math.min(i2, text.length()))) {
            return RichTextStyle.stylesFullyCovering(text, iMax, iMin);
        }
        return 0;
    }

    @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.ActionBar.FloatingToolbar.StyleDelegate
    public void addStyle(int i, int i2, int i3) {
        int iMin;
        Editable text = getText();
        if (text == null || i2 < 0 || i3 < 0 || i2 >= i3 || i2 >= (iMin = Math.min(i3, text.length()))) {
            return;
        }
        RichTextStyle.setStyle(text, i2, iMin, i, true, this.block);
        if ((i & 256) != 0) {
            invalidateSpoilers();
        }
        notifySpansChanged();
    }

    @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.ActionBar.FloatingToolbar.StyleDelegate
    public void removeStyle(int i, int i2, int i3) {
        int iMin;
        Editable text = getText();
        if (text == null || i2 < 0 || i3 < 0 || i2 >= i3 || i2 >= (iMin = Math.min(i3, text.length()))) {
            return;
        }
        RichTextStyle.setStyle(text, i2, iMin, i, false, this.block);
        if ((i & 256) != 0) {
            invalidateSpoilers();
        }
        notifySpansChanged();
    }
}
