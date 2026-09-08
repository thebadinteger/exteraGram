package com.exteragram.messenger.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.view.DragEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import androidx.core.view.ContentInfoCompat;
import androidx.core.view.OnReceiveContentViewBehavior;
import androidx.core.view.ViewCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.widget.TextViewOnReceiveContentListener;

@SuppressLint({"RestrictedApi", "AppCompatCustomView"})
public abstract class ReceiveContentEditText extends EditText implements OnReceiveContentViewBehavior {
    private final TextViewOnReceiveContentListener defaultOnReceiveContentListener;

    public ReceiveContentEditText(Context context) {
        super(context);
        this.defaultOnReceiveContentListener = new TextViewOnReceiveContentListener();
    }

    @Override // android.widget.EditText, android.widget.TextView
    public Editable getText() {
        if (Build.VERSION.SDK_INT >= 28) {
            return super.getText();
        }
        return super.getEditableText();
    }

    @Override // android.widget.TextView, android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        String[] onReceiveContentMimeTypes;
        InputConnection inputConnectionOnCreateInputConnection = super.onCreateInputConnection(editorInfo);
        if (inputConnectionOnCreateInputConnection == null || Build.VERSION.SDK_INT > 30 || (onReceiveContentMimeTypes = ViewCompat.getOnReceiveContentMimeTypes(this)) == null) {
            return inputConnectionOnCreateInputConnection;
        }
        EditorInfoCompat.setContentMimeTypes(editorInfo, onReceiveContentMimeTypes);
        return InputConnectionCompat.createWrapper(this, inputConnectionOnCreateInputConnection, editorInfo);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onDragEvent(DragEvent dragEvent) {
        if (handleDragEventViaReceiveContent(dragEvent)) {
            return true;
        }
        return super.onDragEvent(dragEvent);
    }

    @Override // android.widget.EditText, android.widget.TextView
    public boolean onTextContextMenuItem(int i) {
        if (handleMenuActionViaReceiveContent(i)) {
            return true;
        }
        return super.onTextContextMenuItem(i);
    }

    @Override // androidx.core.view.OnReceiveContentViewBehavior
    public ContentInfoCompat onReceiveContent(ContentInfoCompat contentInfoCompat) {
        return this.defaultOnReceiveContentListener.onReceiveContent(this, contentInfoCompat);
    }

    private boolean handleMenuActionViaReceiveContent(int i) {
        if (ViewCompat.getOnReceiveContentMimeTypes(this) == null || !(i == 16908322 || i == 16908337)) {
            return false;
        }
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService("clipboard");
        ClipData primaryClip = clipboardManager == null ? null : clipboardManager.getPrimaryClip();
        if (primaryClip != null && primaryClip.getItemCount() > 0) {
            ViewCompat.performReceiveContent(this, new ContentInfoCompat.Builder(primaryClip, 1).setFlags(i != 16908322 ? 1 : 0).build());
        }
        return true;
    }

    private boolean handleDragEventViaReceiveContent(DragEvent dragEvent) {
        Activity activityFindActivity;
        if (Build.VERSION.SDK_INT >= 31 || dragEvent.getLocalState() != null || ViewCompat.getOnReceiveContentMimeTypes(this) == null || (activityFindActivity = findActivity()) == null || dragEvent.getAction() == 1 || dragEvent.getAction() != 3) {
            return false;
        }
        return OnDropApi24Impl.onDropForTextView(dragEvent, this, activityFindActivity);
    }

    private Activity findActivity() {
        for (Context context = getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
        }
        return null;
    }

    public static final class OnDropApi24Impl {
        public static boolean onDropForTextView(DragEvent dragEvent, ReceiveContentEditText receiveContentEditText, Activity activity) {
            activity.requestDragAndDropPermissions(dragEvent);
            int offsetForPosition = receiveContentEditText.getOffsetForPosition(dragEvent.getX(), dragEvent.getY());
            receiveContentEditText.beginBatchEdit();
            try {
                Selection.setSelection(receiveContentEditText.getText(), offsetForPosition);
                ViewCompat.performReceiveContent(receiveContentEditText, new ContentInfoCompat.Builder(dragEvent.getClipData(), 3).build());
                return true;
            } finally {
                receiveContentEditText.endBatchEdit();
            }
        }
    }
}
