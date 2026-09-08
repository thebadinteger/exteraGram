package com.exteragram.messenger.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.pillstack.ui.pills.crypto.utils.ExchangeRates;
import com.exteragram.messenger.preferences.OtherPreferencesActivity;
import com.exteragram.messenger.utils.network.RemoteUtils;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class SupporterBottomSheet extends BottomSheet {
    @SuppressLint({"UseCompatLoadingForDrawables"})
    public SupporterBottomSheet(final BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        super(baseFragment.getParentActivity(), false, resourcesProvider);
        Activity parentActivity = baseFragment.getParentActivity();
        fixNavigationBar();
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(getContext());
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        rLottieImageView.setImageResource(R.drawable.extera_heart_large);
        rLottieImageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
        rLottieImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(80.0f), ContextCompat.getColor(parentActivity, R.color.ic_background)));
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(80, 80, 1, 0, 28, 0, 0));
        TextView textView = new TextView(parentActivity);
        textView.setText(LocaleController.getString(R.string.SupportDevelopment));
        textView.setTypeface(AndroidUtilities.bold());
        int i = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i, resourcesProvider));
        textView.setTextSize(1, 20.0f);
        textView.setGravity(1);
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 22, 14, 22, 0));
        TextView textView2 = new TextView(parentActivity);
        textView2.setText(LocaleController.getString(R.string.SupportDevelopmentInfo));
        textView2.setTextColor(Theme.getColor(i, resourcesProvider));
        textView2.setTextSize(1, 14.0f);
        textView2.setGravity(1);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 1, 22, 8, 22, 0));
        final float fFloatValue = RemoteUtils.getFloatConfigValue("donates_amount_usd", 5.0f).floatValue();
        final String str = "$" + fFloatValue;
        final FeatureCell featureCell = new FeatureCell(parentActivity, R.drawable.menu_feature_paid, LocaleController.getString(R.string.MakeDonation), _UrlKt.FRAGMENT_ENCODE_SET);
        linearLayout.addView(featureCell, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 20, 0, 0));
        Utilities.Callback callback = new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$new$1(fFloatValue, str, baseFragment, featureCell, (ExchangeRates.State) obj);
            }
        };
        callback.run(ExchangeRates.getCached());
        ExchangeRates.fetch(callback);
        linearLayout.addView(new FeatureCell(parentActivity, R.drawable.menu_feature_wallpaper, LocaleController.getString(R.string.SendProof), AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.SendProofInfo), Theme.key_chat_messageLinkIn, 0, new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$2(baseFragment);
            }
        })), LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 16, 0, 0));
        linearLayout.addView(new FeatureCell(parentActivity, R.drawable.menu_feature_reactions, LocaleController.getString(R.string.ReceiveBadge), LocaleController.getString(R.string.ReceiveBadgeInfo)), LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 16, 0, 0));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(parentActivity, true, null);
        buttonWithCounterView.setRound();
        buttonWithCounterView.setText(LocaleController.getString(R.string.Close), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$3(view);
            }
        });
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 0, 14, 22, 14, 14));
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    public void lambda$new$0(BaseFragment baseFragment) {
        lambda$new$0();
        baseFragment.presentFragment(new OtherPreferencesActivity());
    }

    public /* synthetic */ void lambda$new$2(BaseFragment baseFragment) {
        lambda$new$0();
        MessagesController.getInstance(this.currentAccount).openByUserName("exteraOwner", baseFragment, 1);
    }

    public /* synthetic */ void lambda$new$3(View view) {
        lambda$new$0();
    }

    public static SupporterBottomSheet showAlert(BaseFragment baseFragment) {
        return showAlert(baseFragment, baseFragment.getResourceProvider());
    }

    public static SupporterBottomSheet showAlert(BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        SupporterBottomSheet supporterBottomSheet = new SupporterBottomSheet(baseFragment, resourcesProvider);
        if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(supporterBottomSheet);
        }
        return supporterBottomSheet;
    }

    public class FeatureCell extends FrameLayout {
        private final LinkSpanDrawable.LinksTextView tvSubtitle;

        public FeatureCell(Context context, int i, CharSequence charSequence, CharSequence charSequence2) {
            super(context);
            boolean z = LocaleController.isRTL;
            ImageView imageView = new ImageView(getContext());
            Drawable drawableMutate = ContextCompat.getDrawable(getContext(), i).mutate();
            int i2 = Theme.key_windowBackgroundWhiteBlackText;
            drawableMutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2, ((BottomSheet) SupporterBottomSheet.this).resourcesProvider), PorterDuff.Mode.MULTIPLY));
            imageView.setImageDrawable(drawableMutate);
            addView(imageView, LayoutHelper.createFrame(24, 24.0f, z ? 5 : 3, z ? 0.0f : 27.0f, 6.0f, z ? 27.0f : 0.0f, 0.0f));
            TextView textView = new TextView(getContext());
            textView.setText(charSequence);
            textView.setTextColor(Theme.getColor(i2, ((BottomSheet) SupporterBottomSheet.this).resourcesProvider));
            textView.setTextSize(1, 14.0f);
            textView.setTypeface(AndroidUtilities.bold());
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, z ? 5 : 3, z ? 27.0f : 68.0f, 0.0f, z ? 68.0f : 27.0f, 0.0f));
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(getContext());
            this.tvSubtitle = linksTextView;
            linksTextView.setText(charSequence2);
            linksTextView.setTextSize(1, 14.0f);
            linksTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3, ((BottomSheet) SupporterBottomSheet.this).resourcesProvider));
            linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, ((BottomSheet) SupporterBottomSheet.this).resourcesProvider));
            linksTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(linksTextView, LayoutHelper.createFrame(-2, -2.0f, z ? 5 : 3, z ? 27.0f : 68.0f, 20.0f, z ? 68.0f : 27.0f, 0.0f));
        }

        public void setSubtitle(CharSequence charSequence) {
            this.tvSubtitle.setText(charSequence);
        }
    }
}
