package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CodeHighlighting;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.CopyUtilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialogDecor;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.FloatingActionMode;
import org.telegram.ui.ActionBar.FloatingToolbar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LaunchActivity;

public class EditTextCaption extends EditTextBoldCursor implements FloatingToolbar.StyleDelegate {
    private static final int ACCESSIBILITY_ACTION_SHARE = 268435456;
    private static final int[] STYLE_FLAGS = {1, 2, 4, 8, 16, 256, 16384, 32768};
    public boolean adaptiveCreateLinkDialog;
    private boolean allowTextEntitiesIntersection;
    private String caption;
    private StaticLayout captionLayout;
    private boolean copyPasteShowed;
    private AlertDialog creationLinkDialog;
    private EditTextCaptionDelegate delegate;
    private int hintColor;
    private boolean isInitLineCount;
    private CharSequence lastLayoutText;
    private int lastLayoutWidth;
    private int lineCount;
    private final Theme.ResourcesProvider resourcesProvider;
    private Text rightText;
    private int selectionEnd;
    private int selectionStart;
    private int userNameLength;
    private int xOffset;
    private int yOffset;

    public interface EditTextCaptionDelegate {
        void onSpansChanged();
    }

    public static void lambda$translateSelected$5(int i, int i2, CharSequence charSequence) {
        getText().replace(i, i2, charSequence);
        setSelection(i, charSequence.length() + i);
    }

    public void makeSelectedUrl() {
        int selectionEnd;
        String url;
        AlertDialog.Builder builder;
        CharSequence charSequenceCoerceToText;
        int selectionStart = this.selectionStart;
        if (selectionStart >= 0 && (selectionEnd = this.selectionEnd) >= 0) {
            this.selectionEnd = -1;
            this.selectionStart = -1;
        } else {
            selectionStart = getSelectionStart();
            selectionEnd = getSelectionEnd();
        }
        final int i = selectionEnd;
        final int i2 = selectionStart;
        if (i2 < 0 || i <= i2) {
            url = null;
        } else {
            Editable text = getText();
            URLSpan[] uRLSpanArr = (URLSpan[]) text.getSpans(i2, i, URLSpan.class);
            int length = uRLSpanArr.length;
            int i3 = 0;
            while (true) {
                if (i3 >= length) {
                    url = null;
                    break;
                }
                URLSpan uRLSpan = uRLSpanArr[i3];
                if (text.getSpanStart(uRLSpan) <= i2 && text.getSpanEnd(uRLSpan) >= i) {
                    url = uRLSpan.getURL();
                    break;
                }
                i3++;
            }
            if (url == null && uRLSpanArr.length == 1) {
                url = uRLSpanArr[0].getURL();
            }
        }
        boolean zIsEmpty = TextUtils.isEmpty(url);
        if (this.adaptiveCreateLinkDialog) {
            builder = new AlertDialogDecor.Builder(getContext(), this.resourcesProvider);
        } else {
            builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
        }
        builder.setTitle(LocaleController.getString(R.string.CreateLink));
        FrameLayout frameLayout = new FrameLayout(getContext());
        final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(getContext()) { // from class: org.telegram.ui.Components.EditTextCaption.3
            @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
            public void onMeasure(int i4, int i5) {
                super.onMeasure(i4, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), TLObject.FLAG_30));
            }
        };
        editTextBoldCursor.setTextSize(1, 18.0f);
        if (zIsEmpty) {
            url = "http://";
        }
        editTextBoldCursor.setText(url);
        editTextBoldCursor.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        editTextBoldCursor.setHintText(LocaleController.getString(R.string.URL));
        editTextBoldCursor.setHeaderHintColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader));
        editTextBoldCursor.setSingleLine(true);
        editTextBoldCursor.setFocusable(true);
        editTextBoldCursor.setTransformHintToHeader(true);
        editTextBoldCursor.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_text_RedRegular));
        editTextBoldCursor.setImeOptions(6);
        editTextBoldCursor.setBackgroundDrawable(null);
        editTextBoldCursor.requestFocus();
        editTextBoldCursor.setPadding(0, 0, 0, 0);
        editTextBoldCursor.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
        editTextBoldCursor.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
        frameLayout.addView(editTextBoldCursor, LayoutHelper.createFrame(-1, -1, 119));
        final TextView textView = new TextView(getContext());
        textView.setTextSize(1, 12.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setText(LocaleController.getString(R.string.Paste));
        textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        textView.setGravity(17);
        int themedColor = getThemedColor(Theme.key_windowBackgroundWhiteBlueText2);
        textView.setTextColor(themedColor);
        textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.multAlpha(themedColor, 0.12f), Theme.multAlpha(themedColor, 0.15f)));
        ScaleStateListAnimator.apply(textView, 0.1f, 1.5f);
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, 26.0f, 21, 0.0f, 0.0f, 24.0f, 3.0f));
        final Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.EditTextCaption$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$makeSelectedUrl$6(editTextBoldCursor, textView);
            }
        };
        textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EditTextCaption$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$makeSelectedUrl$7(editTextBoldCursor, runnable, view);
            }
        });
        editTextBoldCursor.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.EditTextCaption.4
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                runnable.run();
            }
        });
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService("clipboard");
        if (zIsEmpty && clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            try {
                charSequenceCoerceToText = clipboardManager.getPrimaryClip().getItemAt(0).coerceToText(getContext());
            } catch (Exception e) {
                FileLog.e(e);
                charSequenceCoerceToText = null;
            }
            if (charSequenceCoerceToText != null) {
                editTextBoldCursor.setText(charSequenceCoerceToText);
                editTextBoldCursor.setSelection(0, editTextBoldCursor.getText().length());
            }
        }
        runnable.run();
        builder.setView(frameLayout);
        builder.setPositiveButton(LocaleController.getString(R.string.OK), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.Components.EditTextCaption$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i4) {
                this.f$0.lambda$makeSelectedUrl$8(i2, i, editTextBoldCursor, alertDialog, i4);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        if (this.adaptiveCreateLinkDialog) {
            AlertDialog alertDialogCreate = builder.create();
            this.creationLinkDialog = alertDialogCreate;
            alertDialogCreate.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.EditTextCaption$$ExternalSyntheticLambda7
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    this.f$0.lambda$makeSelectedUrl$9(dialogInterface);
                }
            });
            this.creationLinkDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: org.telegram.ui.Components.EditTextCaption$$ExternalSyntheticLambda8
                @Override // android.content.DialogInterface.OnShowListener
                public final void onShow(DialogInterface dialogInterface) {
                    EditTextCaption.$r8$lambda$xetTtzgiciy4T6bo1Q40IE2ZMeg(editTextBoldCursor, dialogInterface);
                }
            });
            this.creationLinkDialog.showDelayed(250L);
        } else {
            builder.show().setOnShowListener(new DialogInterface.OnShowListener() { // from class: org.telegram.ui.Components.EditTextCaption$$ExternalSyntheticLambda9
                @Override // android.content.DialogInterface.OnShowListener
                public final void onShow(DialogInterface dialogInterface) {
                    EditTextCaption.$r8$lambda$FMzUblB34zJHW4PnnG6siUqQslA(editTextBoldCursor, dialogInterface);
                }
            });
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) editTextBoldCursor.getLayoutParams();
        if (marginLayoutParams != null) {
            if (marginLayoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) marginLayoutParams).gravity = 1;
            }
            int iDp = AndroidUtilities.dp(24.0f);
            marginLayoutParams.leftMargin = iDp;
            marginLayoutParams.rightMargin = iDp;
            marginLayoutParams.height = AndroidUtilities.dp(36.0f);
            editTextBoldCursor.setLayoutParams(marginLayoutParams);
        }
        editTextBoldCursor.setSelection(0, editTextBoldCursor.getText().length());
    }

    public /* synthetic */ void lambda$makeSelectedUrl$6(EditTextBoldCursor editTextBoldCursor, TextView textView) {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService("clipboard");
        boolean z = (TextUtils.isEmpty(editTextBoldCursor.getText()) || TextUtils.equals(editTextBoldCursor.getText().toString(), "http://")) && clipboardManager != null && clipboardManager.hasPrimaryClip();
        textView.animate().alpha(z ? 1.0f : 0.0f).scaleX(z ? 1.0f : 0.7f).scaleY(z ? 1.0f : 0.7f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(300L).start();
    }

    public /* synthetic */ void lambda$makeSelectedUrl$7(EditTextBoldCursor editTextBoldCursor, Runnable runnable, View view) {
        CharSequence charSequenceCoerceToText;
        try {
            charSequenceCoerceToText = ((ClipboardManager) getContext().getSystemService("clipboard")).getPrimaryClip().getItemAt(0).coerceToText(getContext());
        } catch (Exception e) {
            FileLog.e(e);
            charSequenceCoerceToText = null;
        }
        if (charSequenceCoerceToText != null) {
            editTextBoldCursor.setText(charSequenceCoerceToText);
            editTextBoldCursor.setSelection(0, editTextBoldCursor.getText().length());
        }
        runnable.run();
    }

    public /* synthetic */ void lambda$makeSelectedUrl$8(int i, int i2, EditTextBoldCursor editTextBoldCursor, AlertDialog alertDialog, int i3) {
        Editable text = getText();
        CharacterStyle[] characterStyleArr = (CharacterStyle[]) text.getSpans(i, i2, CharacterStyle.class);
        if (characterStyleArr != null && characterStyleArr.length > 0) {
            for (CharacterStyle characterStyle : characterStyleArr) {
                if (!(characterStyle instanceof AnimatedEmojiSpan) && !(characterStyle instanceof QuoteSpan.QuoteStyleSpan)) {
                    int spanStart = text.getSpanStart(characterStyle);
                    int spanEnd = text.getSpanEnd(characterStyle);
                    text.removeSpan(characterStyle);
                    if (spanStart < i) {
                        text.setSpan(characterStyle, spanStart, i, 33);
                    }
                    if (spanEnd > i2) {
                        text.setSpan(characterStyle, i2, spanEnd, 33);
                    }
                }
            }
        }
        try {
            text.setSpan(new URLSpanReplacement(editTextBoldCursor.getText().toString().trim()), i, i2, 33);
        } catch (Exception unused) {
        }
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    public /* synthetic */ void lambda$makeSelectedUrl$9(DialogInterface dialogInterface) {
        this.creationLinkDialog = null;
        requestFocus();
    }

    public static /* synthetic */ void $r8$lambda$xetTtzgiciy4T6bo1Q40IE2ZMeg(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public static /* synthetic */ void $r8$lambda$FMzUblB34zJHW4PnnG6siUqQslA(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public boolean closeCreationLinkDialog(boolean z) {
        AlertDialog alertDialog = this.creationLinkDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return false;
        }
        if (!z) {
            return true;
        }
        this.creationLinkDialog.dismiss();
        return true;
    }

    public void makeSelectedRegular() {
        applyTextStyleToSelection(null);
    }

    public void setSelectionOverride(int i, int i2) {
        this.selectionStart = i;
        this.selectionEnd = i2;
    }

    private static int spanStyleFlags(TextStyleSpan textStyleSpan) {
        int styleFlags = textStyleSpan.getStyleFlags();
        return (styleFlags & 512) != 0 ? styleFlags | 256 : styleFlags;
    }

    @Override // org.telegram.ui.ActionBar.FloatingToolbar.StyleDelegate
    public int getCurrentStyle(int i, int i2) {
        Editable text = getText();
        if (text == null) {
            return 0;
        }
        int iMax = Math.max(0, i);
        int iMin = Math.min(i2, text.length());
        if (iMax < 0 || iMin < 0 || iMax >= iMin) {
            return 0;
        }
        TextStyleSpan[] textStyleSpanArr = (TextStyleSpan[]) text.getSpans(iMax, iMin, TextStyleSpan.class);
        int i3 = 0;
        for (int i4 : STYLE_FLAGS) {
            int i5 = iMax;
            boolean z = true;
            while (z && i5 < iMin) {
                z = false;
                for (int i6 = 0; i6 < textStyleSpanArr.length; i6++) {
                    if ((spanStyleFlags(textStyleSpanArr[i6]) & i4) != 0) {
                        int spanStart = text.getSpanStart(textStyleSpanArr[i6]);
                        int spanEnd = text.getSpanEnd(textStyleSpanArr[i6]);
                        if (spanStart <= i5 && spanEnd > i5) {
                            z = true;
                            i5 = spanEnd;
                        }
                    }
                }
            }
            if (i5 >= iMin) {
                i3 |= i4;
            }
        }
        return i3;
    }

    @Override // org.telegram.ui.ActionBar.FloatingToolbar.StyleDelegate
    public void addStyle(int i, int i2, int i3) {
        int iMin;
        Editable text = getText();
        if (text == null || i2 < 0 || i3 < 0 || i2 >= i3 || i2 >= (iMin = Math.min(i3, text.length()))) {
            return;
        }
        TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
        textStyleRun.flags = i;
        MediaDataController.addStyleToText(new TextStyleSpan(textStyleRun), i2, iMin, text, true);
        if ((i & 256) != 0) {
            invalidateSpoilers();
        }
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.FloatingToolbar.StyleDelegate
    public void removeStyle(int i, int i2, int i3) {
        Editable text = getText();
        if (text == null || i2 < 0 || i3 < 0 || i2 >= i3) {
            return;
        }
        int iMin = Math.min(i3, text.length());
        int i4 = i & 256;
        if (i4 != 0) {
            i |= 512;
        }
        for (TextStyleSpan textStyleSpan : (TextStyleSpan[]) text.getSpans(i2, iMin, TextStyleSpan.class)) {
            int styleFlags = textStyleSpan.getStyleFlags();
            if ((styleFlags & i) != 0) {
                int spanStart = text.getSpanStart(textStyleSpan);
                int spanEnd = text.getSpanEnd(textStyleSpan);
                text.removeSpan(textStyleSpan);
                if (spanStart < i2) {
                    text.setSpan(new TextStyleSpan(new TextStyleSpan.TextStyleRun(textStyleSpan.getTextStyleRun())), spanStart, i2, 33);
                }
                if (spanEnd > iMin) {
                    text.setSpan(new TextStyleSpan(new TextStyleSpan.TextStyleRun(textStyleSpan.getTextStyleRun())), iMin, spanEnd, 33);
                }
                int iMax = Math.max(spanStart, i2);
                int iMin2 = Math.min(spanEnd, iMin);
                int i5 = styleFlags & (~i);
                if (i5 != 0 && iMax < iMin2) {
                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun(textStyleSpan.getTextStyleRun());
                    textStyleRun.flags = i5;
                    text.setSpan(new TextStyleSpan(textStyleRun), iMax, iMin2, 33);
                }
            }
        }
        if (i4 != 0) {
            invalidateSpoilers();
        }
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    private void applyTextStyleToSelection(TextStyleSpan textStyleSpan) {
        int selectionEnd;
        int selectionStart = this.selectionStart;
        if (selectionStart >= 0 && (selectionEnd = this.selectionEnd) >= 0) {
            this.selectionEnd = -1;
            this.selectionStart = -1;
        } else {
            selectionStart = getSelectionStart();
            selectionEnd = getSelectionEnd();
        }
        MediaDataController.addStyleToText(textStyleSpan, selectionStart, selectionEnd, getText(), this.allowTextEntitiesIntersection);
        if (textStyleSpan == null) {
            Editable text = getText();
            for (CodeHighlighting.Span span : (CodeHighlighting.Span[]) text.getSpans(selectionStart, selectionEnd, CodeHighlighting.Span.class)) {
                text.removeSpan(span);
            }
            QuoteSpan[] quoteSpanArr = (QuoteSpan[]) text.getSpans(selectionStart, selectionEnd, QuoteSpan.class);
            for (int i = 0; i < quoteSpanArr.length; i++) {
                text.removeSpan(quoteSpanArr[i]);
                text.removeSpan(quoteSpanArr[i].styleSpan);
                QuoteSpan.QuoteCollapsedPart quoteCollapsedPart = quoteSpanArr[i].collapsedSpan;
                if (quoteCollapsedPart != null) {
                    text.removeSpan(quoteCollapsedPart);
                }
            }
            if (quoteSpanArr.length > 0) {
                invalidateQuotes(true);
            }
        }
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onWindowFocusChanged(boolean z) {
        try {
            super.onWindowFocusChanged(z);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    private ActionMode.Callback overrideCallback(final ActionMode.Callback callback) {
        final ActionMode.Callback callback2 = new ActionMode.Callback() { // from class: org.telegram.ui.Components.EditTextCaption.5
            @Override // android.view.ActionMode.Callback
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                EditTextCaption.this.copyPasteShowed = true;
                EditTextCaption.this.onContextMenuOpen();
                return callback.onCreateActionMode(actionMode, menu);
            }

            @Override // android.view.ActionMode.Callback
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return callback.onPrepareActionMode(actionMode, menu);
            }

            @Override // android.view.ActionMode.Callback
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (EditTextCaption.this.performMenuAction(menuItem.getItemId())) {
                    actionMode.finish();
                    return true;
                }
                try {
                    return callback.onActionItemClicked(actionMode, menuItem);
                } catch (Exception unused) {
                    return true;
                }
            }

            @Override // android.view.ActionMode.Callback
            public void onDestroyActionMode(ActionMode actionMode) {
                EditTextCaption.this.copyPasteShowed = false;
                EditTextCaption.this.onContextMenuClose();
                callback.onDestroyActionMode(actionMode);
            }
        };
        return new ActionMode.Callback2() { // from class: org.telegram.ui.Components.EditTextCaption.6
            @Override // android.view.ActionMode.Callback
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return callback2.onCreateActionMode(actionMode, menu);
            }

            @Override // android.view.ActionMode.Callback
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return callback2.onPrepareActionMode(actionMode, menu);
            }

            @Override // android.view.ActionMode.Callback
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return callback2.onActionItemClicked(actionMode, menuItem);
            }

            @Override // android.view.ActionMode.Callback
            public void onDestroyActionMode(ActionMode actionMode) {
                callback2.onDestroyActionMode(actionMode);
            }

            @Override // android.view.ActionMode.Callback2
            public void onGetContentRect(ActionMode actionMode, View view, Rect rect) {
                ActionMode.Callback callback3 = callback;
                if (callback3 instanceof ActionMode.Callback2) {
                    ((ActionMode.Callback2) callback3).onGetContentRect(actionMode, view, rect);
                } else {
                    super.onGetContentRect(actionMode, view, rect);
                }
            }
        };
    }

    public boolean performMenuAction(int i) {
        if (i == R.id.menu_regular) {
            makeSelectedRegular();
            return true;
        }
        if (i == R.id.menu_bold) {
            makeSelectedBold();
            return true;
        }
        if (i == R.id.menu_italic) {
            makeSelectedItalic();
            return true;
        }
        if (i == R.id.menu_bold_italic) {
            makeSelectedBold();
            makeSelectedItalic();
            return true;
        }
        if (i == R.id.menu_mono) {
            makeSelectedMono();
            return true;
        }
        if (i == R.id.menu_code) {
            makeSelectedCode();
            return true;
        }
        if (i == R.id.menu_link) {
            makeSelectedUrl();
            return true;
        }
        if (i == R.id.menu_strike) {
            makeSelectedStrike();
            return true;
        }
        if (i == R.id.menu_underline) {
            makeSelectedUnderline();
            return true;
        }
        if (i == R.id.menu_spoiler) {
            makeSelectedSpoiler();
            return true;
        }
        if (i == R.id.menu_quote) {
            makeSelectedQuote();
            return true;
        }
        if (i == R.id.menu_date) {
            makeSelectedDate();
            return true;
        }
        if (i != R.id.menu_translate) {
            return false;
        }
        translateSelected();
        return true;
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.view.View
    public ActionMode startActionMode(ActionMode.Callback callback, int i) {
        return super.startActionMode(overrideCallback(callback), i);
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.view.View
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return super.startActionMode(overrideCallback(callback));
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
    @SuppressLint({"DrawAllocation"})
    public void onMeasure(int i, int i2) {
        int iIndexOf;
        try {
            this.isInitLineCount = getMeasuredWidth() == 0 && getMeasuredHeight() == 0;
            super.onMeasure(i, i2);
            if (this.isInitLineCount) {
                this.lineCount = getLineCount();
            }
            this.isInitLineCount = false;
        } catch (Exception e) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(51.0f));
            FileLog.e(e);
        }
        int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
        Editable text = getText();
        this.captionLayout = null;
        String str = this.caption;
        this.lastLayoutText = str;
        this.lastLayoutWidth = measuredWidth;
        if (str == null || str.length() <= 0 || text.length() <= 1 || text.charAt(0) != '@' || (iIndexOf = TextUtils.indexOf((CharSequence) text, ' ')) == -1) {
            return;
        }
        TextPaint paint = getPaint();
        paint.setTypeface(AndroidUtilities.regular());
        int i3 = iIndexOf + 1;
        CharSequence charSequenceSubSequence = text.subSequence(0, i3);
        int iCeil = (int) Math.ceil(paint.measureText(text, 0, i3));
        int iMax = Math.max(0, measuredWidth - iCeil);
        this.userNameLength = charSequenceSubSequence.length();
        CharSequence charSequenceEllipsize = TextUtils.ellipsize(this.caption, paint, iMax, TextUtils.TruncateAt.END);
        this.xOffset = iCeil;
        try {
            StaticLayout staticLayout = new StaticLayout(charSequenceEllipsize, paint, iMax, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.captionLayout = staticLayout;
            if (staticLayout.getLineCount() > 0) {
                this.xOffset = (int) (this.xOffset + (-this.captionLayout.getLineLeft(0)));
            }
            this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2) + AndroidUtilities.dp(0.5f);
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public boolean isNearRightCaption(int i) {
        Layout layout = getLayout();
        return layout != null && layout.getLineCount() > 0 && (layout.getLineCount() > 1 || layout.getLineRight(0) + ((float) i) >= ((float) ((getWidth() - getPaddingLeft()) - getPaddingRight())));
    }

    public String getCaption() {
        return this.caption;
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor
    public void setHintColor(int i) {
        super.setHintColor(i);
        this.hintColor = i;
        invalidate();
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, org.telegram.ui.Components.EditTextEffects, android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        Canvas canvas2;
        Layout layout;
        canvas.save();
        canvas.translate(0.0f, this.offsetY);
        super.onDraw(canvas);
        try {
            if (this.captionLayout != null && this.userNameLength == length()) {
                TextPaint paint = getPaint();
                int color = getPaint().getColor();
                paint.setColor(this.hintColor);
                canvas.save();
                canvas.translate(this.xOffset, this.yOffset);
                this.captionLayout.draw(canvas);
                canvas.restore();
                paint.setColor(color);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (this.rightText == null || length() == 0 || (layout = getLayout()) == null || layout.getLineCount() <= 0) {
            canvas2 = canvas;
        } else {
            canvas2 = canvas;
            this.rightText.draw(canvas2, layout.getLineRight(0), (getHeight() / 2.0f) + AndroidUtilities.dp(1.0f), this.hintColor, 1.0f);
        }
        canvas2.restore();
    }

    public void setRightText(CharSequence charSequence) {
        this.rightText = new Text(charSequence, 16.0f, getTypeface());
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        AccessibilityNodeInfoCompat accessibilityNodeInfoCompatWrap = AccessibilityNodeInfoCompat.wrap(accessibilityNodeInfo);
        if (!TextUtils.isEmpty(this.caption)) {
            accessibilityNodeInfoCompatWrap.setHintText(this.caption);
        }
        List<AccessibilityNodeInfoCompat.AccessibilityActionCompat> actionList = accessibilityNodeInfoCompatWrap.getActionList();
        int size = actionList.size();
        for (int i = 0; i < size; i++) {
            AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat = actionList.get(i);
            if (accessibilityActionCompat.getId() == 268435456) {
                accessibilityNodeInfoCompatWrap.removeAction(accessibilityActionCompat);
                break;
            }
        }
        if (hasSelection()) {
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_spoiler, LocaleController.getString(R.string.Spoiler)));
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_bold, LocaleController.getString(R.string.Bold)));
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_italic, LocaleController.getString(R.string.Italic)));
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_bold_italic, LocaleController.getString(R.string.BoldItalic)));
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_mono, LocaleController.getString(R.string.Mono)));
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_code, LocaleController.getString(R.string.CodeBlock)));
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_strike, LocaleController.getString(R.string.Strike)));
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_underline, LocaleController.getString(R.string.Underline)));
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_link, LocaleController.getString(R.string.CreateLink)));
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_regular, LocaleController.getString(R.string.Regular)));
            accessibilityNodeInfoCompatWrap.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_date, LocaleController.getString(R.string.FormattedDate)));
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        return performMenuAction(i) || super.performAccessibilityAction(i, bundle);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    @Override 
    public boolean onTextContextMenuItem(int i) {
        if (i != 16908322) {
            try {
                if (i == 16908321) {
                    int iMax = Math.max(0, getSelectionStart());
                    int iMin = Math.min(getText().length(), getSelectionEnd());
                    AndroidUtilities.addToClipboard(getText().subSequence(iMax, iMin));
                    AndroidUtilities.findActivity(getContext()).closeContextMenu();
                    FloatingActionMode floatingActionMode = this.floatingActionMode;
                    if (floatingActionMode != null) {
                        floatingActionMode.finish();
                    }
                    setSelection(iMax, iMin);
                    return true;
                }
                if (i == 16908320) {
                    int iMax2 = Math.max(0, getSelectionStart());
                    int iMin2 = Math.min(getText().length(), getSelectionEnd());
                    AndroidUtilities.addToClipboard(getText().subSequence(iMax2, iMin2));
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    if (iMax2 != 0) {
                        spannableStringBuilder.append(getText().subSequence(0, iMax2));
                    }
                    if (iMin2 != getText().length()) {
                        spannableStringBuilder.append(getText().subSequence(iMin2, getText().length()));
                    }
                    setText(spannableStringBuilder);
                    setSelection(iMax2);
                    return true;
                }
            } catch (Exception unused) {
            }
        } else if (handleRichPaste(((ClipboardManager) getContext().getSystemService("clipboard")).getPrimaryClip())) {
            return true;
        }
        return super.onTextContextMenuItem(i);
    }

    @Override 
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        InputConnection inputConnectionOnCreateInputConnection = super.onCreateInputConnection(editorInfo);
        if (inputConnectionOnCreateInputConnection == null) {
            return null;
        }
        String[] contentMimeTypes = EditorInfoCompat.getContentMimeTypes(editorInfo);
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        Collections.addAll(linkedHashSet, contentMimeTypes);
        linkedHashSet.add("text/html");
        linkedHashSet.add("text/plain");
        EditorInfoCompat.setContentMimeTypes(editorInfo, (String[]) linkedHashSet.toArray(new String[0]));
        return new InputConnectionWrapper(inputConnectionOnCreateInputConnection, true) { // from class: org.telegram.ui.Components.EditTextCaption.7
            @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
            public boolean commitText(CharSequence charSequence, int i) {
                ClipData primaryClip;
                CharSequence charSequenceCoerceToText;
                if (charSequence != null && charSequence.length() > 1) {
                    try {
                        ClipboardManager clipboardManager = (ClipboardManager) EditTextCaption.this.getContext().getSystemService("clipboard");
                        if (clipboardManager != null && clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClipDescription().hasMimeType("text/html") && (primaryClip = clipboardManager.getPrimaryClip()) != null && primaryClip.getItemCount() > 0 && (charSequenceCoerceToText = primaryClip.getItemAt(0).coerceToText(EditTextCaption.this.getContext())) != null && TextUtils.equals(charSequence, charSequenceCoerceToText) && EditTextCaption.this.handleRichPaste(primaryClip)) {
                            return true;
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                return super.commitText(charSequence, i);
            }
        };
    }

    public boolean handleRichPaste(ClipData clipData) {
        if (clipData != null && clipData.getItemCount() != 0) {
            ClipDescription description = clipData.getDescription();
            if (!description.hasMimeType("image/*") && !description.hasMimeType("video/*") && !description.hasMimeType("audio/*")) {
                boolean zHasMimeType = clipData.getDescription().hasMimeType("text/html");
                boolean z = clipData.getItemAt(0).getUri() != null;
                if (zHasMimeType || z) {
                    try {
                        ClipData.Item itemAt = clipData.getItemAt(0);
                        String htmlText = itemAt.getHtmlText();
                        if (htmlText == null && itemAt.getUri() != null) {
                            htmlText = readHtmlFromUri(itemAt.getUri());
                        }
                        if (htmlText == null) {
                            return false;
                        }
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(CopyUtilities.fromHTML(htmlText));
                        Emoji.replaceEmoji((CharSequence) spannableStringBuilder, getPaint().getFontMetricsInt(), false, (int[]) null);
                        AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AnimatedEmojiSpan.class);
                        if (animatedEmojiSpanArr != null) {
                            for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
                                animatedEmojiSpan.applyFontMetrics(getPaint().getFontMetricsInt(), AnimatedEmojiDrawable.getCacheTypeForEnterView());
                            }
                        }
                        int iMax = Math.max(0, getSelectionStart());
                        int iMin = Math.min(getText().length(), getSelectionEnd());
                        QuoteSpan.QuoteStyleSpan[] quoteStyleSpanArr = (QuoteSpan.QuoteStyleSpan[]) getText().getSpans(iMax, iMin, QuoteSpan.QuoteStyleSpan.class);
                        if (quoteStyleSpanArr != null && quoteStyleSpanArr.length > 0) {
                            for (QuoteSpan.QuoteStyleSpan quoteStyleSpan : (QuoteSpan.QuoteStyleSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), QuoteSpan.QuoteStyleSpan.class)) {
                                spannableStringBuilder.removeSpan(quoteStyleSpan);
                                spannableStringBuilder.removeSpan(quoteStyleSpan.span);
                            }
                        } else {
                            QuoteSpan.normalizeQuotes(spannableStringBuilder);
                        }
                        setText(getText().replace(iMax, iMin, spannableStringBuilder));
                        setSelection(spannableStringBuilder.length() + iMax, iMax + spannableStringBuilder.length());
                        return true;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        }
        return false;
    }

    private String readHtmlFromUri(Uri uri) {
        try {
            InputStream inputStreamOpenInputStream = getContext().getContentResolver().openInputStream(uri);
            if (inputStreamOpenInputStream == null) {
                if (inputStreamOpenInputStream == null) {
                    return null;
                }
                inputStreamOpenInputStream.close();
                return null;
            }
            try {
                StringBuilder sb = new StringBuilder();
                byte[] bArr = new byte[4096];
                int i = 0;
                do {
                    int i2 = inputStreamOpenInputStream.read(bArr);
                    if (i2 == -1) {
                        break;
                    }
                    sb.append(new String(bArr, 0, i2));
                    i += i2;
                } while (i < 32768);
                String string = sb.toString();
                inputStreamOpenInputStream.close();
                return string;
            } catch (Throwable th) {
                try {
                    inputStreamOpenInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }
}
