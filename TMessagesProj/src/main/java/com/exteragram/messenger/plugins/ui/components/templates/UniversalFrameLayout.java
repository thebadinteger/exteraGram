package com.exteragram.messenger.plugins.ui.components.templates;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.Utilities;

@Metadata(d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0017\u0018\u00002\u00020\u0001:\u0001GB\u001d\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J0\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0011H\u0002J\u0018\u0010\u0015\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u0011H\u0002J\u0010\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\u0010\u0010\u001b\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\u001aH\u0002J\b\u0010\u001d\u001a\u00020\rH\u0002J\b\u0010\u001e\u001a\u00020\rH\u0002J\u0010\u0010\u001f\u001a\u00020\r2\u0006\u0010 \u001a\u00020!H\u0002J\b\u0010\"\u001a\u00020\rH\u0002J\b\u0010#\u001a\u00020\rH\u0002J\u0010\u0010$\u001a\u00020\r2\u0006\u0010 \u001a\u00020!H\u0002J\u0010\u0010%\u001a\u00020\r2\u0006\u0010&\u001a\u00020'H\u0002J\u0010\u0010(\u001a\u00020\u000f2\u0006\u0010)\u001a\u00020*H\u0002J\u0010\u0010+\u001a\u00020\u000f2\u0006\u0010,\u001a\u00020*H\u0002J \u0010-\u001a\u00020\u000f2\u0006\u0010 \u001a\u00020!2\u0006\u0010.\u001a\u00020/2\u0006\u00100\u001a\u000201H\u0002J\u0010\u00102\u001a\u00020\r2\u0006\u00103\u001a\u00020\u0011H\u0002J0\u00104\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0011H\u0014J\u0018\u00105\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u0011H\u0014J\u0010\u00106\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0010\u00107\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\u001aH\u0016J\b\u00108\u001a\u00020\rH\u0014J\b\u00109\u001a\u00020\rH\u0014J\u0010\u0010:\u001a\u00020\r2\u0006\u0010 \u001a\u00020!H\u0014J\b\u0010;\u001a\u00020\rH\u0016J\b\u0010<\u001a\u00020\rH\u0016J(\u0010<\u001a\u00020\r2\u0006\u0010=\u001a\u00020\u00112\u0006\u0010>\u001a\u00020\u00112\u0006\u0010?\u001a\u00020\u00112\u0006\u0010@\u001a\u00020\u0011H\u0017J\u0010\u0010A\u001a\u00020\r2\u0006\u0010 \u001a\u00020!H\u0014J\u0010\u0010B\u001a\u00020\r2\u0006\u0010&\u001a\u00020'H\u0016J\u0010\u0010C\u001a\u00020\u000f2\u0006\u0010)\u001a\u00020*H\u0016J\u0010\u0010D\u001a\u00020\u000f2\u0006\u0010,\u001a\u00020*H\u0017J \u0010E\u001a\u00020\u000f2\u0006\u0010 \u001a\u00020!2\u0006\u0010.\u001a\u00020/2\u0006\u00100\u001a\u000201H\u0014J\u0010\u0010F\u001a\u00020\r2\u0006\u00103\u001a\u00020\u0011H\u0016R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000b¨\u0006H"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFrameLayout;", "Landroid/widget/FrameLayout;", "context", "Landroid/content/Context;", "universalFrameLayoutListener", "Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFrameLayout$UniversalFrameLayoutListener;", "<init>", "(Landroid/content/Context;Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFrameLayout$UniversalFrameLayoutListener;)V", "getUniversalFrameLayoutListener", "()Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFrameLayout$UniversalFrameLayoutListener;", "setUniversalFrameLayoutListener", "(Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFrameLayout$UniversalFrameLayoutListener;)V", "callSuperOnLayout", _UrlKt.FRAGMENT_ENCODE_SET, "changed", _UrlKt.FRAGMENT_ENCODE_SET, "left", _UrlKt.FRAGMENT_ENCODE_SET, "top", "right", "bottom", "callSuperOnMeasure", "widthMeasureSpec", "heightMeasureSpec", "callSuperSetTranslationX", "translationX", _UrlKt.FRAGMENT_ENCODE_SET, "callSuperSetTranslationY", "translationY", "callSuperOnDetachedFromWindow", "callSuperOnAttachedToWindow", "callSuperDispatchDraw", "canvas", "Landroid/graphics/Canvas;", "callSuperRequestLayout", "callSuperInvalidate", "callSuperOnDraw", "callSuperOnInitializeAccessibilityNodeInfo", "info", "Landroid/view/accessibility/AccessibilityNodeInfo;", "callSuperOnInterceptTouchEvent", "ev", "Landroid/view/MotionEvent;", "callSuperOnTouchEvent", "event", "callSuperDrawChild", "child", "Landroid/view/View;", "drawingTime", _UrlKt.FRAGMENT_ENCODE_SET, "callSuperSetVisibility", "visibility", "onLayout", "onMeasure", "setTranslationX", "setTranslationY", "onAttachedToWindow", "onDetachedFromWindow", "dispatchDraw", "requestLayout", "invalidate", "l", "t", "r", "b", "onDraw", "onInitializeAccessibilityNodeInfo", "onInterceptTouchEvent", "onTouchEvent", "drawChild", "setVisibility", "UniversalFrameLayoutListener", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class UniversalFrameLayout extends FrameLayout {
    private UniversalFrameLayoutListener universalFrameLayoutListener;

    @JvmOverloads
    public UniversalFrameLayout(Context context) {
        this(context, null, 2, 0 == true ? 1 : 0);
    }

    @JvmOverloads
    public UniversalFrameLayout(Context context, UniversalFrameLayoutListener universalFrameLayoutListener) {
        super(context);
        this.universalFrameLayoutListener = universalFrameLayoutListener;
    }

    public /* synthetic */ UniversalFrameLayout(Context context, UniversalFrameLayoutListener universalFrameLayoutListener, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : universalFrameLayoutListener);
    }

    public final UniversalFrameLayoutListener getUniversalFrameLayoutListener() {
        return this.universalFrameLayoutListener;
    }

    public final void setUniversalFrameLayoutListener(UniversalFrameLayoutListener universalFrameLayoutListener) {
        this.universalFrameLayoutListener = universalFrameLayoutListener;
    }

    public final void callSuperOnLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public final void callSuperOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public final void callSuperSetTranslationX(float translationX) {
        super.setTranslationX(translationX);
    }

    public final void callSuperSetTranslationY(float translationY) {
        super.setTranslationY(translationY);
    }

    public final void callSuperOnDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public final void callSuperOnAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public final void callSuperDispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public final void callSuperRequestLayout() {
        super.requestLayout();
    }

    public final void callSuperInvalidate() {
        super.invalidate();
    }

    public final void callSuperOnDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public final void callSuperOnInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
    }

    public final boolean callSuperOnInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public final boolean callSuperOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public final boolean callSuperDrawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public final void callSuperSetVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.onLayout(changed, left, top, right, bottom, new Utilities.Callback5() { 
                @Override 
                public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    this.f$0.callSuperOnLayout(((Boolean) obj).booleanValue(), ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), ((Integer) obj5).intValue());
                }
            });
        } else {
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.onMeasure(widthMeasureSpec, heightMeasureSpec, new Utilities.Callback2() { 
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
    public void setTranslationX(float translationX) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.setTranslationX(translationX, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.callSuperSetTranslationX(((Float) obj).floatValue());
                }
            });
        } else {
            super.setTranslationX(translationX);
        }
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.setTranslationY(translationY, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.callSuperSetTranslationY(((Float) obj).floatValue());
                }
            });
        } else {
            super.setTranslationY(translationY);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.onAttachedToWindow(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.callSuperOnAttachedToWindow();
                }
            });
        } else {
            super.onAttachedToWindow();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.onDetachedFromWindow(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.callSuperOnDetachedFromWindow();
                }
            });
        } else {
            super.onDetachedFromWindow();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.dispatchDraw(canvas, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.callSuperDispatchDraw((Canvas) obj);
                }
            });
        } else {
            super.dispatchDraw(canvas);
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.requestLayout(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.callSuperRequestLayout();
                }
            });
        } else {
            super.requestLayout();
        }
    }

    @Override // android.view.View
    public void invalidate() {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.invalidate(new UniversalFrameLayout$$ExternalSyntheticLambda3(this));
        } else {
            super.invalidate();
        }
    }

    @Override // android.view.View
    @Deprecated(message = "Deprecated in Java")
    public void invalidate(int l, int t, int r, int b2) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.invalidate(l, t, r, b2, new UniversalFrameLayout$$ExternalSyntheticLambda3(this));
        } else {
            super.invalidate(l, t, r, b2);
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.onDraw(canvas, new Utilities.Callback() { 
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
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.onInitializeAccessibilityNodeInfo(info, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.callSuperOnInitializeAccessibilityNodeInfo((AccessibilityNodeInfo) obj);
                }
            });
        } else {
            super.onInitializeAccessibilityNodeInfo(info);
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            return universalFrameLayoutListener.onInterceptTouchEvent(ev, new Utilities.CallbackReturn() { 
                @Override 
                public final Object run(Object obj) {
                    return Boolean.valueOf(this.f$0.callSuperOnInterceptTouchEvent((MotionEvent) obj));
                }
            });
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        return universalFrameLayoutListener != null ? universalFrameLayoutListener.onTouchEvent(event, new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return Boolean.valueOf(this.f$0.callSuperOnTouchEvent((MotionEvent) obj));
            }
        }) : super.onTouchEvent(event);
    }

    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            return universalFrameLayoutListener.drawChild(canvas, child, drawingTime, new Utilities.Callback3Return() { 
                @Override 
                public final Object run(Object obj, Object obj2, Object obj3) {
                    return Boolean.valueOf(this.f$0.callSuperDrawChild((Canvas) obj, (View) obj2, ((Long) obj3).longValue()));
                }
            });
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        UniversalFrameLayoutListener universalFrameLayoutListener = this.universalFrameLayoutListener;
        if (universalFrameLayoutListener != null) {
            universalFrameLayoutListener.setVisibility(visibility, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.callSuperSetVisibility(((Integer) obj).intValue());
                }
            });
        } else {
            super.setVisibility(visibility);
        }
    }

    @Metadata(d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001JV\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u00072$\u0010\u000b\u001a \u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\fH\u0016J,\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00072\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u0010H\u0016J\u001e\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00130\u0014H\u0016J\u001e\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\u00132\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00130\u0014H\u0016J\u0010\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\u0018H\u0016J\u0010\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\u0018H\u0016J\u001e\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u001b\u001a\u00020\u001c2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0014H\u0016J\u0010\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\u0018H\u0016J\u0010\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\u0018H\u0016J0\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u001f\u001a\u00020\u00072\u0006\u0010 \u001a\u00020\u00072\u0006\u0010!\u001a\u00020\u00072\u0006\u0010\"\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u0018H\u0016J\u001e\u0010#\u001a\u00020\u00032\u0006\u0010\u001b\u001a\u00020\u001c2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0014H\u0016J\u001e\u0010$\u001a\u00020\u00032\u0006\u0010%\u001a\u00020&2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020&0\u0014H\u0016J$\u0010'\u001a\u00020\u00052\u0006\u0010(\u001a\u00020)2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020)\u0012\u0004\u0012\u00020\u00050*H\u0016J$\u0010+\u001a\u00020\u00052\u0006\u0010,\u001a\u00020)2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020)\u0012\u0004\u0012\u00020\u00050*H\u0016J@\u0010-\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010.\u001a\u00020/2\u0006\u00100\u001a\u0002012\u001e\u0010\u000b\u001a\u001a\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020/\u0012\u0004\u0012\u000201\u0012\u0004\u0012\u00020\u000502H\u0016J\u001e\u00103\u001a\u00020\u00032\u0006\u00104\u001a\u00020\u00072\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0014H\u0016¨\u00065À\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/templates/UniversalFrameLayout$UniversalFrameLayoutListener;", _UrlKt.FRAGMENT_ENCODE_SET, "onLayout", _UrlKt.FRAGMENT_ENCODE_SET, "changed", _UrlKt.FRAGMENT_ENCODE_SET, "left", _UrlKt.FRAGMENT_ENCODE_SET, "top", "right", "bottom", "originalMethod", "Lorg/telegram/messenger/Utilities$Callback5;", "onMeasure", "widthMeasureSpec", "heightMeasureSpec", "Lorg/telegram/messenger/Utilities$Callback2;", "setTranslationX", "translationX", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/telegram/messenger/Utilities$Callback;", "setTranslationY", "translationY", "onAttachedToWindow", "Ljava/lang/Runnable;", "onDetachedFromWindow", "dispatchDraw", "canvas", "Landroid/graphics/Canvas;", "requestLayout", "invalidate", "l", "t", "r", "b", "onDraw", "onInitializeAccessibilityNodeInfo", "info", "Landroid/view/accessibility/AccessibilityNodeInfo;", "onInterceptTouchEvent", "ev", "Landroid/view/MotionEvent;", "Lorg/telegram/messenger/Utilities$CallbackReturn;", "onTouchEvent", "event", "drawChild", "child", "Landroid/view/View;", "drawingTime", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/telegram/messenger/Utilities$Callback3Return;", "setVisibility", "visibility", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface UniversalFrameLayoutListener {

        @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
        public static final class DefaultImpls {
            @Deprecated
            public static void onLayout(UniversalFrameLayoutListener universalFrameLayoutListener, boolean z, int i, int i2, int i3, int i4, Utilities.Callback5<Boolean, Integer, Integer, Integer, Integer> callback5) {
                UniversalFrameLayoutListener.super.onLayout(z, i, i2, i3, i4, callback5);
            }

            @Deprecated
            public static void onMeasure(UniversalFrameLayoutListener universalFrameLayoutListener, int i, int i2, Utilities.Callback2<Integer, Integer> callback2) {
                UniversalFrameLayoutListener.super.onMeasure(i, i2, callback2);
            }

            @Deprecated
            public static void setTranslationX(UniversalFrameLayoutListener universalFrameLayoutListener, float f, Utilities.Callback<Float> callback) {
                UniversalFrameLayoutListener.super.setTranslationX(f, callback);
            }

            @Deprecated
            public static void setTranslationY(UniversalFrameLayoutListener universalFrameLayoutListener, float f, Utilities.Callback<Float> callback) {
                UniversalFrameLayoutListener.super.setTranslationY(f, callback);
            }

            @Deprecated
            public static void onAttachedToWindow(UniversalFrameLayoutListener universalFrameLayoutListener, Runnable runnable) {
                UniversalFrameLayoutListener.super.onAttachedToWindow(runnable);
            }

            @Deprecated
            public static void onDetachedFromWindow(UniversalFrameLayoutListener universalFrameLayoutListener, Runnable runnable) {
                UniversalFrameLayoutListener.super.onDetachedFromWindow(runnable);
            }

            @Deprecated
            public static void dispatchDraw(UniversalFrameLayoutListener universalFrameLayoutListener, Canvas canvas, Utilities.Callback<Canvas> callback) {
                UniversalFrameLayoutListener.super.dispatchDraw(canvas, callback);
            }

            @Deprecated
            public static void requestLayout(UniversalFrameLayoutListener universalFrameLayoutListener, Runnable runnable) {
                UniversalFrameLayoutListener.super.requestLayout(runnable);
            }

            @Deprecated
            public static void invalidate(UniversalFrameLayoutListener universalFrameLayoutListener, Runnable runnable) {
                UniversalFrameLayoutListener.super.invalidate(runnable);
            }

            @Deprecated
            public static void invalidate(UniversalFrameLayoutListener universalFrameLayoutListener, int i, int i2, int i3, int i4, Runnable runnable) {
                UniversalFrameLayoutListener.super.invalidate(i, i2, i3, i4, runnable);
            }

            @Deprecated
            public static void onDraw(UniversalFrameLayoutListener universalFrameLayoutListener, Canvas canvas, Utilities.Callback<Canvas> callback) {
                UniversalFrameLayoutListener.super.onDraw(canvas, callback);
            }

            @Deprecated
            public static void onInitializeAccessibilityNodeInfo(UniversalFrameLayoutListener universalFrameLayoutListener, AccessibilityNodeInfo accessibilityNodeInfo, Utilities.Callback<AccessibilityNodeInfo> callback) {
                UniversalFrameLayoutListener.super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo, callback);
            }

            @Deprecated
            public static boolean onInterceptTouchEvent(UniversalFrameLayoutListener universalFrameLayoutListener, MotionEvent motionEvent, Utilities.CallbackReturn<MotionEvent, Boolean> callbackReturn) {
                return UniversalFrameLayoutListener.super.onInterceptTouchEvent(motionEvent, callbackReturn);
            }

            @Deprecated
            public static boolean onTouchEvent(UniversalFrameLayoutListener universalFrameLayoutListener, MotionEvent motionEvent, Utilities.CallbackReturn<MotionEvent, Boolean> callbackReturn) {
                return UniversalFrameLayoutListener.super.onTouchEvent(motionEvent, callbackReturn);
            }

            @Deprecated
            public static boolean drawChild(UniversalFrameLayoutListener universalFrameLayoutListener, Canvas canvas, View view, long j, Utilities.Callback3Return<Canvas, View, Long, Boolean> callback3Return) {
                return UniversalFrameLayoutListener.super.drawChild(canvas, view, j, callback3Return);
            }

            @Deprecated
            public static void setVisibility(UniversalFrameLayoutListener universalFrameLayoutListener, int i, Utilities.Callback<Integer> callback) {
                UniversalFrameLayoutListener.super.setVisibility(i, callback);
            }
        }

        default void onLayout(boolean changed, int left, int top, int right, int bottom, Utilities.Callback5<Boolean, Integer, Integer, Integer, Integer> originalMethod) {
            originalMethod.run(Boolean.valueOf(changed), Integer.valueOf(left), Integer.valueOf(top), Integer.valueOf(right), Integer.valueOf(bottom));
        }

        default void onMeasure(int widthMeasureSpec, int heightMeasureSpec, Utilities.Callback2<Integer, Integer> originalMethod) {
            originalMethod.run(Integer.valueOf(widthMeasureSpec), Integer.valueOf(heightMeasureSpec));
        }

        default void setTranslationX(float translationX, Utilities.Callback<Float> originalMethod) {
            originalMethod.run(Float.valueOf(translationX));
        }

        default void setTranslationY(float translationY, Utilities.Callback<Float> originalMethod) {
            originalMethod.run(Float.valueOf(translationY));
        }

        default void onAttachedToWindow(Runnable originalMethod) {
            originalMethod.run();
        }

        default void onDetachedFromWindow(Runnable originalMethod) {
            originalMethod.run();
        }

        default void dispatchDraw(Canvas canvas, Utilities.Callback<Canvas> originalMethod) {
            originalMethod.run(canvas);
        }

        default void requestLayout(Runnable originalMethod) {
            originalMethod.run();
        }

        default void invalidate(Runnable originalMethod) {
            originalMethod.run();
        }

        default void invalidate(int l, int t, int r, int b2, Runnable originalMethod) {
            originalMethod.run();
        }

        default void onDraw(Canvas canvas, Utilities.Callback<Canvas> originalMethod) {
            originalMethod.run(canvas);
        }

        default void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info, Utilities.Callback<AccessibilityNodeInfo> originalMethod) {
            originalMethod.run(info);
        }

        default boolean onInterceptTouchEvent(MotionEvent ev, Utilities.CallbackReturn<MotionEvent, Boolean> originalMethod) {
            return originalMethod.run(ev).booleanValue();
        }

        default boolean onTouchEvent(MotionEvent event, Utilities.CallbackReturn<MotionEvent, Boolean> originalMethod) {
            return originalMethod.run(event).booleanValue();
        }

        default boolean drawChild(Canvas canvas, View child, long drawingTime, Utilities.Callback3Return<Canvas, View, Long, Boolean> originalMethod) {
            return originalMethod.run(canvas, child, Long.valueOf(drawingTime)).booleanValue();
        }

        default void setVisibility(int visibility, Utilities.Callback<Integer> originalMethod) {
            originalMethod.run(Integer.valueOf(visibility));
        }
    }
}
