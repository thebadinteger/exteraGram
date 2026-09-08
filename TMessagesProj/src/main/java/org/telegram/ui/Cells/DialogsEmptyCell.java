package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import androidx.core.util.Consumer;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.TextViewSwitcher;

public class DialogsEmptyCell extends LinearLayout {
    private final int currentAccount;
    private int currentType;
    private final RLottieImageView imageView;
    private Runnable onUtyanAnimationEndListener;
    private Consumer<Float> onUtyanAnimationUpdateListener;
    private int prevIcon;
    private final TextViewSwitcher subtitleView;
    private final TextView titleView;
    private boolean utyanAnimationTriggered;
    private ValueAnimator utyanAnimator;
    private float utyanCollapseProgress;

    public DialogsEmptyCell(final Context context) {
        super(context);
        this.currentType = -1;
        this.currentAccount = UserConfig.selectedAccount;
        setGravity(17);
        setOrientation(1);
        setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Cells.DialogsEmptyCell$$ExternalSyntheticLambda0
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return DialogsEmptyCell.m7054$r8$lambda$ugpKNg6fsNS82BgCbqgLNnBV0(view, motionEvent);
            }
        });
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(rLottieImageView, LayoutHelper.createFrame(100, 100.0f, 17, 52.0f, 4.0f, 52.0f, 0.0f));
        rLottieImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.DialogsEmptyCell$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$1(view);
            }
        });
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_chats_nameMessage_threeLines));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setGravity(17);
        addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 10.0f, 52.0f, 0.0f));
        TextViewSwitcher textViewSwitcher = new TextViewSwitcher(context);
        this.subtitleView = textViewSwitcher;
        textViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() { // from class: org.telegram.ui.Cells.DialogsEmptyCell$$ExternalSyntheticLambda2
            @Override // android.widget.ViewSwitcher.ViewFactory
            public final View makeView() {
                return DialogsEmptyCell.$r8$lambda$orrQ6KUp7SgjvkysqPE4x0aE658(context);
            }
        });
        textViewSwitcher.setInAnimation(context, R.anim.alpha_in);
        textViewSwitcher.setOutAnimation(context, R.anim.alpha_out);
        addView(textViewSwitcher, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 7.0f, 52.0f, 0.0f));
    }

    public static /* synthetic */ boolean m7054$r8$lambda$ugpKNg6fsNS82BgCbqgLNnBV0(View view, MotionEvent motionEvent) {
        return true;
    }

    public /* synthetic */ void lambda$new$1(View view) {
        if (this.imageView.isPlaying()) {
            return;
        }
        this.imageView.setProgress(0.0f);
        this.imageView.playAnimation();
    }

    public static /* synthetic */ View $r8$lambda$orrQ6KUp7SgjvkysqPE4x0aE658(Context context) {
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_chats_message));
        textView.setTextSize(1, 14.0f);
        textView.setGravity(17);
        textView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        return textView;
    }

    public void setOnUtyanAnimationEndListener(Runnable runnable) {
        this.onUtyanAnimationEndListener = runnable;
    }

    public void setOnUtyanAnimationUpdateListener(Consumer<Float> consumer) {
        this.onUtyanAnimationUpdateListener = consumer;
    }

    public void setType(int i, boolean z) {
        int i2;
        String string;
        if (this.currentType == i) {
            return;
        }
        this.currentType = i;
        if (i == 0 || i == 1) {
            i2 = R.raw.utyan_newborn;
            string = LocaleController.getString(R.string.NoChatsHelp);
            this.titleView.setText(LocaleController.getString(R.string.NoChats));
        } else {
            RLottieImageView rLottieImageView = this.imageView;
            if (i == 2) {
                rLottieImageView.setAutoRepeat(false);
                i2 = R.raw.filter_no_chats;
                TextView textView = this.titleView;
                if (z) {
                    textView.setText(LocaleController.getString(R.string.FilterNoChatsToForward));
                    string = LocaleController.getString(R.string.FilterNoChatsToForwardInfo);
                } else {
                    textView.setText(LocaleController.getString(R.string.FilterNoChatsToDisplay));
                    string = LocaleController.getString(R.string.FilterNoChatsToDisplayInfo);
                }
            } else {
                rLottieImageView.setAutoRepeat(true);
                i2 = R.raw.filter_new;
                string = LocaleController.getString(R.string.FilterAddingChatsInfo);
                this.titleView.setText(LocaleController.getString(R.string.FilterAddingChats));
            }
        }
        RLottieImageView rLottieImageView2 = this.imageView;
        if (i2 != 0) {
            rLottieImageView2.setVisibility(0);
            if (this.currentType == 1) {
                if (isUtyanAnimationTriggered()) {
                    this.utyanCollapseProgress = 1.0f;
                    String string2 = LocaleController.getString(R.string.NoChatsContactsHelp);
                    if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                        string2 = string2.replace('\n', ' ');
                    }
                    this.subtitleView.setText(string2, true);
                    requestLayout();
                } else {
                    startUtyanCollapseAnimation(true);
                }
            }
            if (this.prevIcon != i2) {
                this.imageView.setAnimation(i2, 100, 100);
                this.imageView.playAnimation();
                this.prevIcon = i2;
            }
        } else {
            rLottieImageView2.setVisibility(8);
        }
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            string = string.replace('\n', ' ');
        }
        this.subtitleView.setText(string, false);
        int i3 = this.currentType;
        if ((i3 == 0 || i3 == 1) && ExteraConfig.getHideFloatingButton()) {
            this.subtitleView.setVisibility(8);
        } else {
            this.subtitleView.setVisibility(0);
        }
    }

    public boolean isUtyanAnimationTriggered() {
        return this.utyanAnimationTriggered;
    }

    public void startUtyanExpandAnimation() {
        ValueAnimator valueAnimator = this.utyanAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.utyanAnimationTriggered = false;
        ValueAnimator duration = ValueAnimator.ofFloat(this.utyanCollapseProgress, 0.0f).setDuration(250L);
        this.utyanAnimator = duration;
        duration.setInterpolator(Easings.easeOutQuad);
        this.utyanAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.DialogsEmptyCell$$ExternalSyntheticLambda4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$startUtyanExpandAnimation$3(valueAnimator2);
            }
        });
        this.utyanAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.DialogsEmptyCell.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (DialogsEmptyCell.this.onUtyanAnimationEndListener != null) {
                    DialogsEmptyCell.this.onUtyanAnimationEndListener.run();
                }
                if (animator == DialogsEmptyCell.this.utyanAnimator) {
                    DialogsEmptyCell.this.utyanAnimator = null;
                }
            }
        });
        this.utyanAnimator.start();
    }

    public /* synthetic */ void lambda$startUtyanExpandAnimation$3(ValueAnimator valueAnimator) {
        this.utyanCollapseProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        requestLayout();
        Consumer<Float> consumer = this.onUtyanAnimationUpdateListener;
        if (consumer != null) {
            consumer.accept(Float.valueOf(this.utyanCollapseProgress));
        }
    }

    public void startUtyanCollapseAnimation(boolean z) {
        ValueAnimator valueAnimator = this.utyanAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.utyanAnimationTriggered = true;
        if (z) {
            String string = LocaleController.getString(R.string.NoChatsContactsHelp);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                string = string.replace('\n', ' ');
            }
            this.subtitleView.setText(string, true);
        }
        ValueAnimator duration = ValueAnimator.ofFloat(this.utyanCollapseProgress, 1.0f).setDuration(250L);
        this.utyanAnimator = duration;
        duration.setInterpolator(Easings.easeOutQuad);
        this.utyanAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.DialogsEmptyCell$$ExternalSyntheticLambda3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$startUtyanCollapseAnimation$4(valueAnimator2);
            }
        });
        this.utyanAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.DialogsEmptyCell.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (DialogsEmptyCell.this.onUtyanAnimationEndListener != null) {
                    DialogsEmptyCell.this.onUtyanAnimationEndListener.run();
                }
                if (animator == DialogsEmptyCell.this.utyanAnimator) {
                    DialogsEmptyCell.this.utyanAnimator = null;
                }
            }
        });
        this.utyanAnimator.start();
    }

    public /* synthetic */ void lambda$startUtyanCollapseAnimation$4(ValueAnimator valueAnimator) {
        this.utyanCollapseProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        requestLayout();
        Consumer<Float> consumer = this.onUtyanAnimationUpdateListener;
        if (consumer != null) {
            consumer.accept(Float.valueOf(this.utyanCollapseProgress));
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateLayout();
    }

    @Override // android.view.View
    public void offsetTopAndBottom(int i) {
        super.offsetTopAndBottom(i);
        updateLayout();
    }

    public void updateLayout() {
        int i;
        int currentActionBarHeight = 0;
        if ((getParent() instanceof View) && (((i = this.currentType) == 2 || i == 3) && ((View) getParent()).getPaddingTop() != 0)) {
            currentActionBarHeight = 0 - (getTop() / 2);
        }
        int i2 = this.currentType;
        if (i2 == 0 || i2 == 1) {
            currentActionBarHeight -= (int) ((ActionBar.getCurrentActionBarHeight() / 2.0f) * (1.0f - this.utyanCollapseProgress));
        }
        float f = currentActionBarHeight;
        this.imageView.setTranslationY(f);
        this.titleView.setTranslationY(f);
        this.subtitleView.setTranslationY(f);
    }

    private int measureUtyanHeight(int i) {
        int size;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            size = (view.getMeasuredHeight() - view.getPaddingTop()) - view.getPaddingBottom();
        } else {
            size = View.MeasureSpec.getSize(i);
        }
        if (size == 0) {
            size = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight;
        }
        return (int) (size + ((AndroidUtilities.dp(320.0f) - size) * this.utyanCollapseProgress));
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int size;
        int i3 = this.currentType;
        if (i3 == 0 || i3 == 1) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(measureUtyanHeight(i2), TLObject.FLAG_30));
            return;
        }
        if (i3 == 2 || i3 == 3) {
            if (getParent() instanceof View) {
                View view = (View) getParent();
                size = view.getMeasuredHeight();
                if (view.getPaddingTop() != 0) {
                    size -= AndroidUtilities.statusBarHeight;
                }
            } else {
                size = View.MeasureSpec.getSize(i2);
            }
            if (size == 0) {
                size = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight;
            }
            if (getParent() instanceof BlurredRecyclerView) {
                size -= ((BlurredRecyclerView) getParent()).blurTopPadding;
            }
            ArrayList<TLRPC.RecentMeUrl> arrayList = MessagesController.getInstance(this.currentAccount).hintDialogs;
            if (!arrayList.isEmpty()) {
                size -= (((AndroidUtilities.dp(72.0f) * arrayList.size()) + arrayList.size()) - 1) + AndroidUtilities.dp(50.0f);
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(size, TLObject.FLAG_30));
            return;
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(166.0f), TLObject.FLAG_30));
    }
}
