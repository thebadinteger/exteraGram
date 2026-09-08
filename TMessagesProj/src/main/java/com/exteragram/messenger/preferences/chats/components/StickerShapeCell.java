package com.exteragram.messenger.preferences.chats.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextPaint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.components.CustomPreferenceCell;
import com.exteragram.messenger.preferences.components.PreviewBackgroundDrawable;
import com.exteragram.messenger.preferences.components.PreviewColors;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;

public abstract class StickerShapeCell extends LinearLayout implements CustomPreferenceCell {
    private final StickerShape[] stickerShape;

    public abstract void updateStickerPreview();

    public StickerShapeCell(Context context) {
        super(context);
        this.stickerShape = new StickerShape[3];
        setOrientation(0);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(13.0f), 0);
        final int i = 0;
        while (i < 3) {
            boolean z = true;
            boolean z2 = i == 1;
            if (i != 2) {
                z = false;
            }
            this.stickerShape[i] = new StickerShape(context, z2, z);
            ScaleStateListAnimator.apply(this.stickerShape[i], 0.03f, 1.5f);
            addView(this.stickerShape[i], LayoutHelper.createLinear(-1, -1, 0.5f, 8, 0, 8, 0));
            this.stickerShape[i].setOnClickListener(new View.OnClickListener() { 
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$0(i, view);
                }
            });
            i++;
        }
    }

    public void lambda$setSelected$0(ValueAnimator valueAnimator) {
            setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }
}
