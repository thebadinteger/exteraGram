package com.exteragram.messenger.preferences.appearance.components;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.components.CustomPreferenceCell;
import com.exteragram.messenger.preferences.components.PreviewBackgroundDrawable;
import com.exteragram.messenger.preferences.components.PreviewColors;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;

public abstract class FabShapeCell extends LinearLayout implements CustomPreferenceCell {
    private final FabShape[] fabShape;

    public abstract void rebuildFragments();

    public FabShapeCell(Context context) {
        super(context);
        this.fabShape = new FabShape[2];
        setWillNotDraw(false);
        setOrientation(0);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(21.0f));
        for (int i = 0; i < 2; i++) {
            final boolean z = true;
            if (i != 1) {
                z = false;
            }
            this.fabShape[i] = new FabShape(context, z);
            ScaleStateListAnimator.apply(this.fabShape[i], 0.03f, 1.5f);
            addView(this.fabShape[i], LayoutHelper.createLinear(-1, -1, 0.5f, 8, 0, 8, 0));
            this.fabShape[i].setOnClickListener(new View.OnClickListener() { 
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$0(z, view);
                }
            });
        }
    }

    public void lambda$setSelected$0(ValueAnimator valueAnimator) {
            setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }
}
