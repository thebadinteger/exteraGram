package com.exteragram.messenger.plugins.hooks;

import java.io.Serializable;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import org.mvel2.MVEL;

@Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
public final /* synthetic */ class MenuItemRecord$checkCondition$compiled$1 extends FunctionReferenceImpl implements Function1<String, Serializable> {
    public static final MenuItemRecord$checkCondition$compiled$1 INSTANCE = new MenuItemRecord$checkCondition$compiled$1();

    public MenuItemRecord$checkCondition$compiled$1() {
        super(1, MVEL.class, "compileExpression", "compileExpression(Ljava/lang/String;)Ljava/io/Serializable;", 0);
    }

    @Override // kotlin.jvm.functions.Function1
    public final Serializable invoke(String str) {
        return MVEL.compileExpression(str);
    }
}
