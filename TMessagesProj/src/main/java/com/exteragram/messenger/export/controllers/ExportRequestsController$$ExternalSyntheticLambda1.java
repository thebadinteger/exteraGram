package com.exteragram.messenger.export.controllers;

import com.exteragram.messenger.export.api.ApiWrap$FileProgress;
import org.telegram.messenger.Utilities;

public final /* synthetic */ class ExportRequestsController$$ExternalSyntheticLambda1 implements Utilities.CallbackReturn {
    public final /* synthetic */ ExportRequestsController f$0;

    public /* synthetic */ ExportRequestsController$$ExternalSyntheticLambda1(ExportRequestsController exportRequestsController) {
        this.f$0 = exportRequestsController;
    }

    @Override 
    public final Object run(Object obj) {
        return Boolean.valueOf(this.f$0.loadMessageFileProgress((ApiWrap$FileProgress) obj));
    }
}
