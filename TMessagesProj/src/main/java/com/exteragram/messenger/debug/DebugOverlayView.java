package com.exteragram.messenger.debug;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatTextView;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;

public class DebugOverlayView extends AppCompatTextView {
    private final ContentBuilder contentBuilder;
    private DataSource dataSource;
    private long updateIntervalMs;
    private final Runnable updater;

    public interface DataSource {
        void build(ContentBuilder contentBuilder);
    }

    public static class ContentBuilder {
        private final StringBuilder stringBuilder = new StringBuilder(512);

        public ContentBuilder reset() {
            this.stringBuilder.setLength(0);
            return this;
        }

        public ContentBuilder title(CharSequence charSequence) {
            return line(charSequence);
        }

        public ContentBuilder section(CharSequence charSequence) {
            if (this.stringBuilder.length() > 0) {
                this.stringBuilder.append('\n');
            }
            StringBuilder sb = this.stringBuilder;
            sb.append('[');
            sb.append(charSequence);
            sb.append(']');
            return this;
        }

        public ContentBuilder kv(String str, Object obj) {
            return line(str + "=" + obj);
        }

        public ContentBuilder line(CharSequence charSequence) {
            if (this.stringBuilder.length() > 0) {
                this.stringBuilder.append('\n');
            }
            this.stringBuilder.append(charSequence);
            return this;
        }

        public CharSequence build() {
            return this.stringBuilder;
        }
    }

    public DebugOverlayView(Context context) {
        super(context);
        this.contentBuilder = new ContentBuilder();
        this.updater = new Runnable() { 
            @Override // java.lang.Runnable
            public void run() {
                if (DebugOverlayView.this.isAttachedToWindow()) {
                    DebugOverlayView.this.refresh();
                    DebugOverlayView debugOverlayView = DebugOverlayView.this;
                    debugOverlayView.postDelayed(this, debugOverlayView.updateIntervalMs);
                }
            }
        };
        this.updateIntervalMs = 250L;
        setTextColor(-1);
        setTextSize(10.0f);
        setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MONO));
        setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        setGravity(51);
        setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), -1342177280));
        setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$0(view);
            }
        });
        setLongClickable(false);
        setFocusable(false);
        setImportantForAccessibility(2);
    }

    public /* synthetic */ void lambda$new$0(View view) {
        if (this.dataSource != null && AndroidUtilities.addToClipboard(this.contentBuilder.build()) && AndroidUtilities.shouldShowClipboardToast()) {
            Toast.makeText(getContext(), LocaleController.getString(R.string.TextCopied), 0).show();
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        refresh();
    }

    public void setUpdateInterval(long j) {
        this.updateIntervalMs = Math.max(16L, j);
    }

    public void refresh() {
        if (this.dataSource == null) {
            setText(_UrlKt.FRAGMENT_ENCODE_SET);
            return;
        }
        this.contentBuilder.reset();
        this.dataSource.build(this.contentBuilder);
        setText(this.contentBuilder.build());
    }

    public static FrameLayout.LayoutParams createLayoutParams() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(AndroidUtilities.dp(220.0f), -2, 53);
        layoutParams.setMargins(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(AndroidUtilities.statusBarHeight) - ActionBar.getCurrentActionBarHeight(), AndroidUtilities.dp(10.0f), 0);
        return layoutParams;
    }

    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks(this.updater);
        post(this.updater);
    }

    @Override // androidx.appcompat.widget.AppCompatTextView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.updater);
    }
}
