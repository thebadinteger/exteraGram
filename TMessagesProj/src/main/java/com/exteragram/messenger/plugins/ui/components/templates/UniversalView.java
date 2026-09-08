package com.exteragram.messenger.plugins.ui.components.templates;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.Utilities;

@Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001:\u0001!B\u001d\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u0018\u0010\u0014\u001a\u00020\r2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016H\u0002J\u0010\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\u0010\u0010\u001b\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0014J\b\u0010\u001c\u001a\u00020\rH\u0014J\u0010\u0010\u001d\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0017J\u0018\u0010\u001e\u001a\u00020\r2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016H\u0014J\b\u0010\u001f\u001a\u00020\rH\u0014J\u0010\u0010 \u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\u001aH\u0016R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000b¨\u0006\""}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "delegate", "Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalView$UniversalViewDelegate;", "<init>", "(Landroid/content/Context;Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalView$UniversalViewDelegate;)V", "getDelegate", "()Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalView$UniversalViewDelegate;", "setDelegate", "(Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalView$UniversalViewDelegate;)V", "callSuperOnDraw", _UrlKt.FRAGMENT_ENCODE_SET, "canvas", "Landroid/graphics/Canvas;", "callSuperOnTouchEvent", _UrlKt.FRAGMENT_ENCODE_SET, "event", "Landroid/view/MotionEvent;", "callSuperOnMeasure", "widthMeasureSpec", _UrlKt.FRAGMENT_ENCODE_SET, "heightMeasureSpec", "callSuperOnInitializeAccessibilityNodeInfo", "info", "Landroid/view/accessibility/AccessibilityNodeInfo;", "onDraw", "onAttachedToWindow", "onTouchEvent", "onMeasure", "onDetachedFromWindow", "onInitializeAccessibilityNodeInfo", "UniversalViewDelegate", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class UniversalView extends View {
    private UniversalViewDelegate delegate;

    @JvmOverloads
    public UniversalView(Context context) {
        this(context, null, 2, 0 == true ? 1 : 0);
    }

    @JvmOverloads
    public UniversalView(Context context, UniversalViewDelegate universalViewDelegate) {
        super(context);
        this.delegate = universalViewDelegate;
    }

    public /* synthetic */ UniversalView(Context context, UniversalViewDelegate universalViewDelegate, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : universalViewDelegate);
    }

    public final UniversalViewDelegate getDelegate() {
        return this.delegate;
    }

    public final void setDelegate(UniversalViewDelegate universalViewDelegate) {
        this.delegate = universalViewDelegate;
    }

    public final void callSuperOnDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public final boolean callSuperOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public final void callSuperOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public final void callSuperOnInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        UniversalViewDelegate universalViewDelegate = this.delegate;
        if (universalViewDelegate != null) {
            universalViewDelegate.onDraw(canvas, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.callSuperOnDraw((Canvas) obj);
                }
            });
        } else {
            super.onDraw(canvas);
        }
    }

    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        UniversalViewDelegate universalViewDelegate = this.delegate;
        if (universalViewDelegate != null) {
            universalViewDelegate.onAttachedToWindow();
        }
    }

    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        UniversalViewDelegate universalViewDelegate = this.delegate;
        return universalViewDelegate != null ? universalViewDelegate.onTouchEvent(event, new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return Boolean.valueOf(this.f$0.callSuperOnTouchEvent((MotionEvent) obj));
            }
        }) : super.onTouchEvent(event);
    }

    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        UniversalViewDelegate universalViewDelegate = this.delegate;
        if (universalViewDelegate != null) {
            universalViewDelegate.onMeasure(widthMeasureSpec, heightMeasureSpec, new Utilities.Callback2() { 
                @Override 
                public final void run(Object obj, Object obj2) {
                    this.f$0.callSuperOnMeasure(((Integer) obj).intValue(), ((Integer) obj2).intValue());
                }
            });
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        UniversalViewDelegate universalViewDelegate = this.delegate;
        if (universalViewDelegate != null) {
            universalViewDelegate.onDetachedFromWindow();
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        UniversalViewDelegate universalViewDelegate = this.delegate;
        if (universalViewDelegate != null) {
            universalViewDelegate.onInitializeAccessibilityNodeInfo(info, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.callSuperOnInitializeAccessibilityNodeInfo((AccessibilityNodeInfo) obj);
                }
            });
        } else {
            super.onInitializeAccessibilityNodeInfo(info);
        }
    }

    @Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007H\u0016J\b\u0010\b\u001a\u00020\u0003H\u0016J\b\u0010\t\u001a\u00020\u0003H\u0016J$\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000b0\u000eH\u0016J,\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00110\u0013H\u0016J\u001e\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u00162\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00160\u0007H\u0016¨\u0006\u0017À\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalView$UniversalViewDelegate;", _UrlKt.FRAGMENT_ENCODE_SET, "onDraw", _UrlKt.FRAGMENT_ENCODE_SET, "canvas", "Landroid/graphics/Canvas;", "originalMethod", "Lorg/telegram/messenger/Utilities$Callback;", "onAttachedToWindow", "onDetachedFromWindow", "onTouchEvent", _UrlKt.FRAGMENT_ENCODE_SET, "event", "Landroid/view/MotionEvent;", "Lorg/telegram/messenger/Utilities$CallbackReturn;", "onMeasure", "widthMeasureSpec", _UrlKt.FRAGMENT_ENCODE_SET, "heightMeasureSpec", "Lorg/telegram/messenger/Utilities$Callback2;", "onInitializeAccessibilityNodeInfo", "info", "Landroid/view/accessibility/AccessibilityNodeInfo;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface UniversalViewDelegate {
        default void onAttachedToWindow() {
        }

        default void onDetachedFromWindow() {
        }

        @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
        public static final class DefaultImpls {
            @Deprecated
            public static void onDraw(UniversalViewDelegate universalViewDelegate, Canvas canvas, Utilities.Callback<Canvas> callback) {
                UniversalViewDelegate.super.onDraw(canvas, callback);
            }

            @Deprecated
            public static void onAttachedToWindow(UniversalViewDelegate universalViewDelegate) {
                UniversalViewDelegate.super.onAttachedToWindow();
            }

            @Deprecated
            public static void onDetachedFromWindow(UniversalViewDelegate universalViewDelegate) {
                UniversalViewDelegate.super.onDetachedFromWindow();
            }

            @Deprecated
            public static boolean onTouchEvent(UniversalViewDelegate universalViewDelegate, MotionEvent motionEvent, Utilities.CallbackReturn<MotionEvent, Boolean> callbackReturn) {
                return UniversalViewDelegate.super.onTouchEvent(motionEvent, callbackReturn);
            }

            @Deprecated
            public static void onMeasure(UniversalViewDelegate universalViewDelegate, int i, int i2, Utilities.Callback2<Integer, Integer> callback2) {
                UniversalViewDelegate.super.onMeasure(i, i2, callback2);
            }

            @Deprecated
            public static void onInitializeAccessibilityNodeInfo(UniversalViewDelegate universalViewDelegate, AccessibilityNodeInfo accessibilityNodeInfo, Utilities.Callback<AccessibilityNodeInfo> callback) {
                UniversalViewDelegate.super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo, callback);
            }
        }

        default void onDraw(Canvas canvas, Utilities.Callback<Canvas> originalMethod) {
            originalMethod.run(canvas);
        }

        default boolean onTouchEvent(MotionEvent event, Utilities.CallbackReturn<MotionEvent, Boolean> originalMethod) {
            return originalMethod.run(event).booleanValue();
        }

        default void onMeasure(int widthMeasureSpec, int heightMeasureSpec, Utilities.Callback2<Integer, Integer> originalMethod) {
            originalMethod.run(Integer.valueOf(widthMeasureSpec), Integer.valueOf(heightMeasureSpec));
        }

        default void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info, Utilities.Callback<AccessibilityNodeInfo> originalMethod) {
            originalMethod.run(info);
        }
    }
}
