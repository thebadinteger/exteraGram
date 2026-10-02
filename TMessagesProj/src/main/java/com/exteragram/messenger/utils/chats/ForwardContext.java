package com.exteragram.messenger.utils.chats;

import android.os.Bundle;

/* JADX INFO: loaded from: classes4.dex */
public interface ForwardContext {

    public static class ForwardParams {
        public boolean noQuote;
    }

    ForwardParams getForwardParams();

    boolean isForwardNoQuote();

    default void setForwardParams(boolean z) {
        getForwardParams().noQuote = z;
    }

    default void writeForwardParams(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        bundle.putBoolean("forward_noquote", isForwardNoQuote());
    }

    default void readForwardParams(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        setForwardParams(bundle.getBoolean("forward_noquote", false));
    }
}
