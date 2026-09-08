package com.exteragram.messenger.icons.ui.components;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import com.caverock.androidsvg.SVG;
import com.exteragram.messenger.icons.ExteraResources;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.icons.IconPack;
import com.exteragram.messenger.icons.IconPackStorage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class ReplaceIconBottomSheet extends BottomSheet {
    private final IconPack iconPack;
    private int loadedOriginalHeight;
    private int loadedOriginalWidth;
    private boolean needReset;
    private boolean needSave;
    private Drawable newDrawable;
    private IconInfoView newIconInfoView;
    private String newIconOriginalName;
    private File newIconTempFile;
    private Drawable originalDrawable;
    private IconInfoView originalIconInfoView;
    private final int resId;
    private ButtonWithCounterView resetButton;
    private final String resourceName;
    private int savedCustomFileHeight;
    private int savedCustomFileWidth;
    private boolean waitingForResult;

    public ReplaceIconBottomSheet(Context context, int i, IconPack iconPack) {
        super(context, false);
        this.waitingForResult = false;
        this.loadedOriginalWidth = 0;
        this.loadedOriginalHeight = 0;
        this.savedCustomFileWidth = 0;
        this.savedCustomFileHeight = 0;
        this.needSave = false;
        this.needReset = false;
        this.resId = i;
        this.iconPack = iconPack;
        this.resourceName = context.getResources().getResourceEntryName(i);
        setCustomView(createView(context));
        loadDrawables(context);
    }

    private void loadDrawables(final Context context) {
        Utilities.globalQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$loadDrawables$1(context);
            }
        });
    }

    void lambda$loadDrawables$0(Drawable drawable, int i, int i2, Drawable drawable2, int i3, int i4) {
        this.originalDrawable = drawable;
        this.loadedOriginalWidth = i;
        this.loadedOriginalHeight = i2;
        this.newDrawable = drawable2;
        this.savedCustomFileWidth = i3;
        this.savedCustomFileHeight = i4;
        IconInfoView iconInfoView = this.originalIconInfoView;
        if (iconInfoView != null) {
            iconInfoView.update(drawable, this.resourceName, i, i2);
        }
        ButtonWithCounterView buttonWithCounterView = this.resetButton;
        if (buttonWithCounterView != null) {
            buttonWithCounterView.setText(LocaleController.getString(this.newDrawable != null ? R.string.Reset : R.string.Cancel), false);
        }
        updateNewInfo(this.newDrawable, this.iconPack.getIcons().get(this.resourceName), this.savedCustomFileWidth, this.savedCustomFileHeight);
    }

    private View createView(final Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(16.0f), 0, 0);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(1);
        linearLayout2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        IconInfoView iconInfoView = new IconInfoView(context, false);
        this.originalIconInfoView = iconInfoView;
        iconInfoView.update(this.originalDrawable, this.resourceName, this.loadedOriginalWidth, this.loadedOriginalHeight);
        linearLayout2.addView(this.originalIconInfoView, LayoutHelper.createLinear(0, -2, 1.0f));
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(1);
        linearLayout3.setGravity(1);
        linearLayout3.addView(new ArrowView(context), LayoutHelper.createLinear(24, 60));
        linearLayout2.addView(linearLayout3, LayoutHelper.createLinear(-2, -2, 0.0f, 0, 24, 0, 24, 0));
        IconInfoView iconInfoView2 = new IconInfoView(context, true);
        this.newIconInfoView = iconInfoView2;
        iconInfoView2.setTargetDimensions(this.loadedOriginalWidth, this.loadedOriginalHeight);
        this.newIconInfoView.getIconView().setFocusable(true);
        this.newIconInfoView.getIconView().setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createView$5(context, view);
            }
        });
        linearLayout2.addView(this.newIconInfoView, LayoutHelper.createLinear(0, -2, 1.0f));
        updateNewInfo(this.newDrawable, this.iconPack.getIcons().get(this.resourceName), this.savedCustomFileWidth, this.savedCustomFileHeight);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 24.0f));
        LinearLayout linearLayout4 = new LinearLayout(context);
        linearLayout4.setOrientation(1);
        linearLayout4.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, true, this.resourcesProvider);
        buttonWithCounterView.setRound();
        buttonWithCounterView.setText(LocaleController.getString(R.string.Save), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createView$6(view);
            }
        });
        linearLayout4.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48));
        ButtonWithCounterView buttonWithCounterView2 = new ButtonWithCounterView(context, false, this.resourcesProvider);
        this.resetButton = buttonWithCounterView2;
        buttonWithCounterView2.setRound().setNeutral();
        this.resetButton.setText(LocaleController.getString(this.iconPack.getIcons().get(this.resourceName) != null ? R.string.Reset : R.string.Cancel), false);
        this.resetButton.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createView$7(view);
            }
        });
        linearLayout4.addView(this.resetButton, LayoutHelper.createLinear(-1, 48, 0.0f, 8.0f, 0.0f, 0.0f));
        linearLayout.addView(linearLayout4, LayoutHelper.createLinear(-1, -2));
        return linearLayout;
    }

    public void lambda$createView$2(ClipboardManager clipboardManager, Context context) {
        if (clipboardManager == null || clipboardManager.getPrimaryClip() == null || clipboardManager.getPrimaryClip().getItemCount() <= 0) {
            return;
        }
        ClipData.Item itemAt = clipboardManager.getPrimaryClip().getItemAt(0);
        Uri uri = itemAt.getUri();
        if (uri != null) {
            processSelectedImage(context, uri);
            return;
        }
        CharSequence text = itemAt.getText();
        if (text != null) {
            processClipboardText(context, text);
        }
    }

    public Unit lambda$startPicker$8(Activity activity, Uri uri) {
        this.waitingForResult = false;
        if (uri != null) {
            processSelectedImage(activity, uri);
        }
        return Unit.INSTANCE;
    }

    private void updateNewIconFromFile(Context context, final File file, final String str, final int i, final int i2) {
        Bitmap bitmapCreateBitmapFromFile = IconManager.INSTANCE.createBitmapFromFile(file.getAbsolutePath(), this.resId, AndroidUtilities.displayMetrics.densityDpi, context.getTheme());
        if (bitmapCreateBitmapFromFile != null) {
            final BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmapCreateBitmapFromFile);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$updateNewIconFromFile$9(file, str, bitmapDrawable, i, i2);
                }
            });
        }
    }

    public void lambda$processClipboardText$10(CharSequence charSequence, Context context) {
        try {
            String string = charSequence.toString();
            if (!string.contains("<svg") && !string.contains("<SVG")) {
                if (string.trim().startsWith("/")) {
                    File file = new File(string.trim());
                    if (file.exists()) {
                        processSelectedImage(context, Uri.fromFile(file));
                        return;
                    }
                    return;
                }
                return;
            }
            File file2 = new File(ApplicationLoader.applicationContext.getCacheDir(), "temp_import_" + System.currentTimeMillis() + ".svg");
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            try {
                fileOutputStream.write(string.getBytes());
                fileOutputStream.close();
                FileInputStream fileInputStream = new FileInputStream(file2);
                try {
                    SVG fromInputStream = SVG.getFromInputStream(fileInputStream);
                    int documentWidth = (int) (fromInputStream.getDocumentWidth() > 0.0f ? fromInputStream.getDocumentWidth() : fromInputStream.getDocumentViewBox().width());
                    int documentHeight = (int) (fromInputStream.getDocumentHeight() > 0.0f ? fromInputStream.getDocumentHeight() : fromInputStream.getDocumentViewBox().height());
                    fileInputStream.close();
                    updateNewIconFromFile(context, file2, this.resourceName + ".svg", documentWidth, documentHeight);
                } catch (Throwable th) {
                    try {
                        fileInputStream.close();
                        throw th;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        throw th;
                    }
                }
            } catch (Throwable th3) {
                try {
                    fileOutputStream.close();
                    throw th3;
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                    throw th3;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void processSelectedImage(final Context context, final Uri uri) {
        Utilities.globalQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$processSelectedImage$11(context, uri);
            }
        });
    }

    /* JADX WARN: Code duplicated, block: B:165:0x020f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:181:? A[Catch: all -> 0x00b3, Exception -> 0x00b8, SYNTHETIC, TRY_LEAVE, TryCatch #4 {Exception -> 0x00b8, blocks: (B:31:0x007c, B:53:0x00af, B:99:0x013d, B:101:0x0156, B:103:0x015e, B:104:0x0162, B:105:0x0174, B:108:0x01a2, B:120:0x01db, B:129:0x0200, B:127:0x01ec, B:126:0x01e9, B:128:0x01ed, B:89:0x0110, B:91:0x011c, B:94:0x0129, B:86:0x0108, B:139:0x0218, B:138:0x0215), top: B:158:0x007c }] */
    /* JADX WARN: Code duplicated, block: B:24:0x0040  */
    /* JADX WARN: Code duplicated, block: B:74:0x00f6  */
    public /* synthetic */ void lambda$processSelectedImage$11(Context context, Uri uri) throws Throwable {
        Throwable th;
        Exception exc;
        int columnIndex;
        String string;
        boolean z;
        String str;
        int documentWidth;
        int documentHeight;
        File file = null;
        try {
            try {
                Cursor cursorQuery = context.getContentResolver().query(uri, null, null, null, null);
                if (cursorQuery != null) {
                    try {
                        if (!cursorQuery.moveToFirst() || (columnIndex = cursorQuery.getColumnIndex("_display_name")) == -1) {
                            string = null;
                        } else {
                            string = cursorQuery.getString(columnIndex);
                        }
                    } catch (Throwable th2) {
                        try {
                            cursorQuery.close();
                            throw th2;
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                            throw th2;
                        }
                    }
                } else {
                    string = null;
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                if (TextUtils.isEmpty(string)) {
                    string = "icon_" + System.currentTimeMillis();
                }
                File file2 = new File(ApplicationLoader.applicationContext.getCacheDir(), "temp_import_" + System.currentTimeMillis() + "_raw");
                try {
                    try {
                        InputStream inputStreamOpenInputStream = context.getContentResolver().openInputStream(uri);
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file2);
                            if (inputStreamOpenInputStream != null) {
                                try {
                                    byte[] bArr = new byte[4096];
                                    while (true) {
                                        int i = inputStreamOpenInputStream.read(bArr);
                                        if (i == -1) {
                                            break;
                                        } else {
                                            fileOutputStream.write(bArr, 0, i);
                                        }
                                        if (inputStreamOpenInputStream != null) {
                                            throw th;
                                        }
                                        try {
                                            inputStreamOpenInputStream.close();
                                            throw th;
                                        } catch (Throwable th4) {
                                            th.addSuppressed(th4);
                                            throw th;
                                        }
                                    }
                                } catch (Throwable th5) {
                                    try {
                                        fileOutputStream.close();
                                        throw th5;
                                    } catch (Throwable th6) {
                                        th5.addSuppressed(th6);
                                        throw th5;
                                    }
                                }
                            }
                            fileOutputStream.close();
                            if (inputStreamOpenInputStream != null) {
                                inputStreamOpenInputStream.close();
                            }
                            try {
                                FileInputStream fileInputStream = new FileInputStream(file2);
                                try {
                                    byte[] bArr2 = new byte[1024];
                                    int i2 = fileInputStream.read(bArr2);
                                    if (i2 > 0) {
                                        String lowerCase = new String(bArr2, 0, i2).trim().toLowerCase(Locale.ROOT);
                                        if (lowerCase.contains("<svg") || (lowerCase.startsWith("<?xml") && lowerCase.contains("<svg"))) {
                                            z = true;
                                        } else {
                                            z = false;
                                        }
                                    } else {
                                        z = false;
                                    }
                                    try {
                                        fileInputStream.close();
                                    } catch (Exception e) {
                                        e = e;
                                        FileLog.e(e);
                                    }
                                } catch (Throwable th7) {
                                    try {
                                        fileInputStream.close();
                                        throw th7;
                                    } catch (Throwable th8) {
                                        th7.addSuppressed(th8);
                                        throw th7;
                                    }
                                }
                            } catch (Exception e2) {
                                e = e2;
                                z = false;
                            }
                            if (z) {
                                str = "svg";
                            } else if (string.toLowerCase().endsWith(".jpg") || string.toLowerCase().endsWith(".jpeg")) {
                                str = "jpg";
                            } else if (string.toLowerCase().endsWith(".webp")) {
                                str = "webp";
                            } else {
                                str = "png";
                            }
                            if (!string.toLowerCase().endsWith("." + str)) {
                                int iLastIndexOf = string.lastIndexOf(46);
                                if (iLastIndexOf > 0) {
                                    string = string.substring(0, iLastIndexOf);
                                }
                                string = string + "." + str;
                            }
                            String str2 = string;
                            File file3 = new File(ApplicationLoader.applicationContext.getCacheDir(), "temp_import_" + System.currentTimeMillis() + "." + str);
                            if (file2.renameTo(file3)) {
                                if (z) {
                                    FileInputStream fileInputStream2 = new FileInputStream(file3);
                                    try {
                                        SVG fromInputStream = SVG.getFromInputStream(fileInputStream2);
                                        documentWidth = (int) (fromInputStream.getDocumentWidth() > 0.0f ? fromInputStream.getDocumentWidth() : fromInputStream.getDocumentViewBox().width());
                                        documentHeight = (int) (fromInputStream.getDocumentHeight() > 0.0f ? fromInputStream.getDocumentHeight() : fromInputStream.getDocumentViewBox().height());
                                        fileInputStream2.close();
                                    } catch (Throwable th9) {
                                        try {
                                            fileInputStream2.close();
                                            throw th9;
                                        } catch (Throwable th10) {
                                            th9.addSuppressed(th10);
                                            throw th9;
                                        }
                                    }
                                } else {
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inJustDecodeBounds = true;
                                    BitmapFactory.decodeFile(file3.getAbsolutePath(), options);
                                    documentWidth = options.outWidth;
                                    documentHeight = options.outHeight;
                                }
                                updateNewIconFromFile(context, file3, str2, documentWidth, documentHeight);
                            }
                            if (file2.exists()) {
                                file2.delete();
                            }
                        } catch (Throwable th11) {
                            if (inputStreamOpenInputStream != null) {
                                throw th11;
                            }
                            inputStreamOpenInputStream.close();
                            throw th11;
                        }
                    } catch (Exception e3) {
                        exc = e3;
                        file = file2;
                        FileLog.e(exc);
                        if (file == null || !file.exists()) {
                            return;
                        }
                        file.delete();
                    }
                } catch (Throwable th12) {
                    th = th12;
                    file = file2;
                    if (file != null && file.exists()) {
                        file.delete();
                        throw th;
                    }
                    throw th;
                }
            } catch (Exception e4) {
                exc = e4;
            }
        } catch (Throwable th13) {
            th = th13;
        }
    }

    public static class IconInfoView extends LinearLayout {
        private final BorderedImageView iconView;
        private final TextView infoName;
        private final TextView infoResolution;
        private float targetAspectRatio;

        public IconInfoView(Context context, boolean z) {
            super(context);
            this.targetAspectRatio = -1.0f;
            setOrientation(1);
            setGravity(1);
            BorderedImageView borderedImageView = new BorderedImageView(context);
            this.iconView = borderedImageView;
            borderedImageView.setDashed(z);
            borderedImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
            borderedImageView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
            borderedImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            addView(borderedImageView, LayoutHelper.createLinear(60, 60));
            TextView textView = new TextView(context);
            this.infoName = textView;
            textView.setTextSize(1, 13.0f);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            textView.setGravity(17);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            addView(textView, LayoutHelper.createLinear(-2, -2, 0.0f, 12.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.infoResolution = textView2;
            textView2.setTextSize(1, 13.0f);
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            textView2.setGravity(17);
            addView(textView2, LayoutHelper.createLinear(-2, -2, 0.0f, 2.0f, 0.0f, 0.0f));
        }

        public void setTargetDimensions(int i, int i2) {
            if (i > 0 && i2 > 0) {
                this.targetAspectRatio = i / i2;
            } else {
                this.targetAspectRatio = -1.0f;
            }
        }

        public void update(Drawable drawable, String str, int i, int i2) {
            if (drawable != null) {
                if (i <= 0) {
                    i = drawable.getIntrinsicWidth();
                }
                if (i2 <= 0) {
                    i2 = drawable.getIntrinsicHeight();
                }
                this.infoResolution.setText(String.format("%s (%s)", String.format(Locale.ROOT, "%d×%d", Integer.valueOf(i), Integer.valueOf(i2)), getAspectRatioString(i, i2)));
                this.infoResolution.setVisibility(0);
                float f = this.targetAspectRatio;
                if (f > 0.0f && i2 > 0 && Math.abs((i / i2) - f) > 0.1f) {
                    this.infoResolution.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
                } else {
                    this.infoResolution.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                }
                this.iconView.setImageDrawable(drawable);
            } else {
                this.infoResolution.setVisibility(4);
                this.iconView.setImageDrawable(null);
            }
            TextView textView = this.infoName;
            if (str != null) {
                textView.setText(str);
                this.infoName.setVisibility(0);
            } else {
                textView.setVisibility(4);
            }
        }

        public BorderedImageView getIconView() {
            return this.iconView;
        }

        private String getAspectRatioString(int i, int i2) {
            if (i2 == 0) {
                return "?";
            }
            int iGcd = gcd(i, i2);
            return (i / iGcd) + ":" + (i2 / iGcd);
        }

        private int gcd(int i, int i2) {
            return i2 == 0 ? i : gcd(i2, i % i2);
        }
    }

    public static class ArrowView extends View {
        private final Paint paint;
        private final Path path;

        public ArrowView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.paint = paint;
            this.path = new Path();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon));
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float width = getWidth() / 2.0f;
            float height = getHeight() / 2.0f;
            float fDp = AndroidUtilities.dp(18.0f);
            this.path.reset();
            float f = fDp / 2.0f;
            this.path.moveTo(width - f, height);
            float f2 = width + f;
            this.path.lineTo(f2, height);
            this.path.moveTo(f2 - AndroidUtilities.dp(7.0f), height - AndroidUtilities.dp(7.0f));
            this.path.lineTo(f2, height);
            this.path.lineTo(f2 - AndroidUtilities.dp(7.0f), height + AndroidUtilities.dp(7.0f));
            canvas.drawPath(this.path, this.paint);
        }
    }

    public static class BorderedImageView extends ImageView {
        private final Paint bgPaint;
        private final float cornerRadius;
        private final Paint dashedPaint;
        private boolean isDashed;
        private final Path path;
        private final RectF rect;
        private final Paint solidPaint;
        private final float strokeWidth;

        public BorderedImageView(Context context) {
            this(context, null);
        }

        public BorderedImageView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.isDashed = false;
            this.path = new Path();
            this.rect = new RectF();
            this.cornerRadius = AndroidUtilities.dp(12.0f);
            float fDpf2 = AndroidUtilities.dpf2(1.25f);
            this.strokeWidth = fDpf2;
            Paint paint = new Paint(1);
            this.bgPaint = paint;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            Paint paint2 = new Paint(1);
            this.solidPaint = paint2;
            Paint.Style style = Paint.Style.STROKE;
            paint2.setStyle(style);
            int i = Theme.key_windowBackgroundWhiteGrayText;
            paint2.setColor(AndroidUtilities.multiplyAlphaComponent(Theme.getColor(i), 0.3f));
            paint2.setStrokeWidth(fDpf2);
            Paint paint3 = new Paint(1);
            this.dashedPaint = paint3;
            paint3.setStyle(style);
            paint3.setColor(AndroidUtilities.multiplyAlphaComponent(Theme.getColor(i), 0.3f));
            paint3.setStrokeWidth(fDpf2);
            paint3.setPathEffect(new DashPathEffect(new float[]{AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f)}, 0.0f));
        }

        public void setDashed(boolean z) {
            this.isDashed = z;
            invalidate();
        }

        @Override // android.widget.ImageView, android.view.View
        public void onDraw(Canvas canvas) {
            float f = this.strokeWidth / 2.0f;
            this.rect.set(f, f, getWidth() - f, getHeight() - f);
            RectF rectF = this.rect;
            float f2 = this.cornerRadius;
            canvas.drawRoundRect(rectF, f2, f2, this.bgPaint);
            super.onDraw(canvas);
            this.path.reset();
            Path path = this.path;
            RectF rectF2 = this.rect;
            float f3 = this.cornerRadius;
            path.addRoundRect(rectF2, f3, f3, Path.Direction.CW);
            canvas.drawPath(this.path, this.isDashed ? this.dashedPaint : this.solidPaint);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        super.dismissInternal();
        if (this.needSave && this.newIconTempFile != null && !this.needReset) {
            IconManager.INSTANCE.saveCustomIcon(this.iconPack.getId(), this.resId, this.newIconTempFile, this.newIconOriginalName);
            return;
        }
        File file = this.newIconTempFile;
        if (file != null && file.exists()) {
            this.newIconTempFile.delete();
        }
        if (this.needReset) {
            IconManager.INSTANCE.resetCustomIcon(this.iconPack.getId(), this.resId);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    public void lambda$new$0() {
        if (this.waitingForResult) {
            return;
        }
        super.lambda$new$0();
    }
}
