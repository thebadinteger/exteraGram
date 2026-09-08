package com.exteragram.messenger.pillstack.ui.pills.crypto;

import org.telegram.ui.Components.ItemOptions;

public final /* synthetic */ class RatePill$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ItemOptions f$0;

    public /* synthetic */ RatePill$$ExternalSyntheticLambda1(ItemOptions itemOptions) {
        this.f$0 = itemOptions;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.closeSwipeback();
    }
}
