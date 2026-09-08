package com.exteragram.messenger.pillstack.ui.pills.crypto;

import android.annotation.SuppressLint;
import android.content.Context;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.pillstack.core.PillType;
import com.exteragram.messenger.pillstack.ui.pills.crypto.utils.ColoredBackground;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

@SuppressLint({"ViewConstructor"})
public class GramPill extends RatePill {
    private static final RatePill.RateCache CACHE = new RatePill.RateCache();

    public GramPill(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider, CACHE, "TON", 3, R.drawable.mini_gram_16, new ColoredBackground());
    }

    @Override 
    public int getPillId() {
        return PillType.GRAM.getId();
    }

    @Override 
    public String getTargetSelection() {
        return PillStackConfig.getGramTargetCurrency();
    }

    @Override 
    public void setTargetSelection(String str) {
        PillStackConfig.setGramTargetCurrency(str);
    }
}
