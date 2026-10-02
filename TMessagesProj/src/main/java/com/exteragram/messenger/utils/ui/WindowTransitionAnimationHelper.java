package com.exteragram.messenger.utils.ui;

import android.graphics.Canvas;
import android.graphics.RecordingCanvas;
import android.graphics.RenderNode;
import android.provider.Settings;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.CubicBezierInterpolator;

/* JADX INFO: loaded from: classes4.dex */
public abstract class WindowTransitionAnimationHelper {
    public static float getDuration() {
        float f;
        try {
            f = Settings.Global.getFloat(ApplicationLoader.applicationContext.getContentResolver(), "transition_animation_scale", 1.0f);
        } catch (Exception unused) {
            f = 1.0f;
        }
        return Math.max(1.0f, f * 450.0f);
    }

    public static float getSlideDistance() {
        return AndroidUtilities.dp(96.0f);
    }

    public static void apply(View view, View view2, boolean z, float f) {
        float fClamp01 = Utilities.clamp01(f);
        float fDp = AndroidUtilities.dp(96.0f);
        float fRound = Math.round(CubicBezierInterpolator.Emphasized.getInterpolation(fClamp01) * fDp);
        if (view != null) {
            view.setTranslationX(z ? fDp - fRound : fRound - fDp);
            view.setAlpha(z ? fade(fClamp01, 50.0f, 83.0f) : 1.0f);
        }
        if (view2 != null) {
            if (z) {
                fRound = -fRound;
            }
            view2.setTranslationX(fRound);
            view2.setAlpha(z ? 1.0f : 1.0f - fade(fClamp01, 35.0f, 83.0f));
        }
    }

    private static float fade(float f, float f2, float f3) {
        return Utilities.clamp01(((f * 450.0f) - f2) / f3);
    }

    public static final class WindowEdgeExtension {
        private boolean captured;
        private final RenderNode node = new RenderNode("windowEdgeExtension");

        public boolean isCaptured() {
            return this.captured;
        }

        public boolean capture(View view, boolean z) {
            int width = view.getWidth();
            int height = view.getHeight();
            if (width <= 0 || height <= 0) {
                return false;
            }
            this.node.setPosition(0, 0, 1, height);
            this.node.setPivotX(0.0f);
            this.node.setPivotY(0.0f);
            RecordingCanvas recordingCanvasBeginRecording = this.node.beginRecording(1, height);
            try {
                recordingCanvasBeginRecording.translate(z ? 0.0f : -(width - 1), 0.0f);
                view.draw(recordingCanvasBeginRecording);
                this.node.endRecording();
                this.node.setUseCompositingLayer(true, null);
                this.captured = true;
                return true;
            } catch (Throwable th) {
                this.node.endRecording();
                throw th;
            }
        }

        public void draw(Canvas canvas, float f, float f2, float f3) {
            if (!this.captured || f2 <= 0.0f || f3 <= 0.0f || !(canvas instanceof RecordingCanvas)) {
                return;
            }
            this.node.setScaleX(f2 / 1.0f);
            this.node.setTranslationX(f);
            this.node.setAlpha(f3);
            canvas.drawRenderNode(this.node);
        }

        public void release() {
            if (this.captured) {
                this.captured = false;
                this.node.setUseCompositingLayer(false, null);
                this.node.discardDisplayList();
            }
        }
    }
}
