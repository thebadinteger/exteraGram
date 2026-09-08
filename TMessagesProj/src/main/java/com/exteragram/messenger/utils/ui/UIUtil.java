package com.exteragram.messenger.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BaseCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ScaleStateListAnimator;

@Metadata(d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0014\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JS\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u00062\b\b\u0002\u0010\u000e\u001a\u00020\u0006H\u0007¢\u0006\u0004\b\u0010\u0010\u0011J-\u0010\u0016\u001a\u00020\u00152\b\b\u0002\u0010\u0012\u001a\u00020\u000b2\b\b\u0003\u0010\u0013\u001a\u00020\u000b2\b\b\u0003\u0010\u0014\u001a\u00020\u000bH\u0007¢\u0006\u0004\b\u0016\u0010\u0017J)\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u0019\u001a\u00020\u00182\b\b\u0001\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u000bH\u0007¢\u0006\u0004\b\u001c\u0010\u001dJ'\u0010\"\u001a\u00020!2\u0006\u0010\u001e\u001a\u00020\u00152\u0006\u0010\u001f\u001a\u00020\u000b2\u0006\u0010 \u001a\u00020\u000bH\u0007¢\u0006\u0004\b\"\u0010#J'\u0010&\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u000b2\u0006\u0010$\u001a\u00020\u00062\b\b\u0002\u0010%\u001a\u00020\u0006¢\u0006\u0004\b&\u0010'J5\u0010.\u001a\u00020\u000f2\u0006\u0010)\u001a\u00020(2\u0006\u0010*\u001a\u00020\u00152\u0006\u0010+\u001a\u00020\u00062\u0006\u0010,\u001a\u00020\u00062\u0006\u0010-\u001a\u00020\u0006¢\u0006\u0004\b.\u0010/R\u0014\u00101\u001a\u0002008\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b1\u00102¨\u00063"}, d2 = {"Lcom/exteragram/messenger/utils/ui/UIUtil;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "Landroid/view/View;", "view", _UrlKt.FRAGMENT_ENCODE_SET, "selectorRad", _UrlKt.FRAGMENT_ENCODE_SET, "top", "bottom", _UrlKt.FRAGMENT_ENCODE_SET, "basePadding", "scale", "tension", _UrlKt.FRAGMENT_ENCODE_SET, "applyScaleStateListAnimator", "(Landroid/view/View;FZZIFF)V", "size", "color", "pressedColor", "Landroid/graphics/drawable/Drawable;", "createFabBackground", "(III)Landroid/graphics/drawable/Drawable;", "Landroid/content/Context;", "context", "iconRes", "Lorg/telegram/ui/Components/CombinedDrawable;", "createCircleDrawableWithIcon", "(Landroid/content/Context;II)Lorg/telegram/ui/Components/CombinedDrawable;", "drawable", "width", "height", "Landroid/graphics/Bitmap;", "drawableToBitmap", "(Landroid/graphics/drawable/Drawable;II)Landroid/graphics/Bitmap;", "luminance", "saturation", "adjustHsl", "(IFF)I", "Landroid/graphics/Canvas;", "canvas", "pattern", "w", "h", "alpha", "drawNowPlayingPattern", "(Landroid/graphics/Canvas;Landroid/graphics/drawable/Drawable;FFF)V", _UrlKt.FRAGMENT_ENCODE_SET, "nowPlayingPattern", "[F", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nUIUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIUtil.kt\ncom/exteragram/messenger/utils/ui/UIUtil\n+ 2 Bitmap.kt\nandroidx/core/graphics/BitmapKt\n*L\n1#1,240:1\n83#2,6:241\n*S KotlinDebug\n*F\n+ 1 UIUtil.kt\ncom/exteragram/messenger/utils/ui/UIUtil\n*L\n147#1:241,6\n*E\n"})
public final class UIUtil {
    public static final UIUtil INSTANCE = new UIUtil();
    private static final float[] nowPlayingPattern = {-5.5f, 20.0f, 20.0f, 0.35f, -5.5f, -20.0f, 20.0f, 0.35f, -36.0f, -42.0f, 22.0f, 0.375f, -36.0f, 0.0f, 25.0f, 0.425f, -36.0f, 42.0f, 22.0f, 0.375f, -70.0f, 22.0f, 23.0f, 0.35f, -70.0f, -22.0f, 23.0f, 0.35f, -99.0f, 46.0f, 21.0f, 0.275f, -99.0f, 0.0f, 22.0f, 0.325f, -99.0f, -46.0f, 21.0f, 0.275f, -128.0f, -23.0f, 20.0f, 0.225f, -128.0f, 23.0f, 20.0f, 0.225f};

    private UIUtil() {
    }

    @JvmStatic
    @JvmOverloads
    public static final void applyScaleStateListAnimator(final View view, final float selectorRad, final boolean top, final boolean bottom, final int basePadding, float scale, float tension) {
        final float f = selectorRad - basePadding;
        final Function1 function1 = new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return UIUtil.m1521$r8$lambda$hl85AWD2jXLpmUTmwUZzMtMAPk(view, basePadding, top, selectorRad, f, bottom, ((Float) obj).floatValue());
            }
        };
        ScaleStateListAnimator.apply(view, scale, tension, new Consumer() { 
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                function1.invoke((Float) obj);
            }
        }, new Consumer() { 
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                function1.invoke((Float) obj);
            }
        });
    }

    public static Unit m1521$r8$lambda$hl85AWD2jXLpmUTmwUZzMtMAPk(View view, int i, boolean z, float f, float f2, boolean z2, float f3) {
        Drawable background = view.getBackground();
        BaseCell.RippleDrawableSafe rippleDrawableSafe = background instanceof BaseCell.RippleDrawableSafe ? (BaseCell.RippleDrawableSafe) background : null;
        if (rippleDrawableSafe != null) {
            Drawable drawable = rippleDrawableSafe.mask;
            Theme.RippleRadMaskDrawable rippleRadMaskDrawable = drawable instanceof Theme.RippleRadMaskDrawable ? (Theme.RippleRadMaskDrawable) drawable : null;
            if (rippleRadMaskDrawable != null) {
                float f4 = i * f3;
                rippleRadMaskDrawable.setPadding(0.0f, f4, 0.0f, f4);
                rippleRadMaskDrawable.setRadius(z ? f - f4 : f2 * f3, z2 ? f - f4 : f2 * f3);
            }
        }
        return Unit.INSTANCE;
    }

    @JvmStatic
    @JvmOverloads
    public static final Drawable createFabBackground(int size, int color, int pressedColor) {
        Pair pair;
        if (size == 40) {
            int i = Theme.key_windowBackgroundWhite;
            pair = TuplesKt.to(Integer.valueOf(ColorUtils.blendARGB(Theme.getColor(i), -1, 0.1f)), Integer.valueOf(Theme.blendOver(Theme.getColor(i), Theme.getColor(Theme.key_listSelector))));
        } else {
            pair = TuplesKt.to(Integer.valueOf(color), Integer.valueOf(pressedColor));
        }
        return Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(ExteraConfig.getSquareFab() ? (float) Math.ceil((size * 16) / 56.0f) : size / 2.0f), ((Number) pair.component1()).intValue(), ((Number) pair.component2()).intValue());
    }

    @JvmStatic
    public static final Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }

    @JvmStatic
    public static final CombinedDrawable createCircleDrawableWithIcon(Context context, int iconRes, int size) {
        Drawable drawable;
        Drawable drawableMutate = null;
        if (iconRes != 0 && (drawable = ContextCompat.getDrawable(context, iconRes)) != null) {
            drawableMutate = drawable.mutate();
        }
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setColor(-1);
        shapeDrawable.setIntrinsicWidth(size);
        shapeDrawable.setIntrinsicHeight(size);
        CombinedDrawable combinedDrawable = new CombinedDrawable(shapeDrawable, drawableMutate);
        combinedDrawable.setCustomSize(size, size);
        return combinedDrawable;
    }

    public static /* synthetic */ int adjustHsl$default(UIUtil uIUtil, int i, float f, float f2, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            f2 = -1.0f;
        }
        return uIUtil.adjustHsl(i, f, f2);
    }

    public final int adjustHsl(int color, float luminance, float saturation) {
        float[] fArr = new float[3];
        ColorUtils.colorToHSL(color, fArr);
        if (saturation > 0.0f) {
            fArr[1] = RangesKt.coerceAtMost(fArr[1] * saturation, 1.0f);
        }
        fArr[2] = RangesKt.coerceAtMost(fArr[2] * luminance, 1.0f);
        return ColorUtils.HSLToColor(fArr);
    }

    public final void drawNowPlayingPattern(Canvas canvas, Drawable pattern, float w, float h, float alpha) {
        if (alpha <= 0.0f) {
            return;
        }
        int i = 0;
        while (true) {
            float[] fArr = nowPlayingPattern;
            if (i >= fArr.length) {
                return;
            }
            float f = fArr[i];
            float f2 = fArr[i + 1];
            float f3 = fArr[i + 2];
            float f4 = fArr[i + 3];
            float f5 = h / 2.0f;
            pattern.setBounds((int) ((AndroidUtilities.dpf2(f) + w) - (AndroidUtilities.dpf2(f3) / 2.0f)), (int) ((AndroidUtilities.dpf2(f2) + f5) - (AndroidUtilities.dpf2(f3) / 2.0f)), (int) (AndroidUtilities.dpf2(f) + w + (AndroidUtilities.dpf2(f3) / 2.0f)), (int) (f5 + AndroidUtilities.dpf2(f2) + (AndroidUtilities.dpf2(f3) / 2.0f)));
            pattern.setAlpha((int) (255.0f * alpha * f4));
            pattern.draw(canvas);
            i += 4;
        }
    }
}
