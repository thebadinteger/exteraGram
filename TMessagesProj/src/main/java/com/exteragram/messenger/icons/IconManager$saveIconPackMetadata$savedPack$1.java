package com.exteragram.messenger.icons;

import java.util.Map;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "Lcom/exteragram/messenger/icons/IconPack;", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
@DebugMetadata(c = "com.exteragram.messenger.icons.IconManager$saveIconPackMetadata$savedPack$1", f = "IconManager.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {}, v = 1)
@SourceDebugExtension({"SMAP\nIconManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IconManager.kt\ncom/exteragram/messenger/icons/IconManager$saveIconPackMetadata$savedPack$1\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,853:1\n1#2:854\n*E\n"})
public final class IconManager$saveIconPackMetadata$savedPack$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super IconPack>, Object> {
    final /* synthetic */ IconPack $iconPack;
    int label;

    public IconManager$saveIconPackMetadata$savedPack$1(IconPack iconPack, Continuation<? super IconManager$saveIconPackMetadata$savedPack$1> continuation) {
        super(2, continuation);
        this.$iconPack = iconPack;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new IconManager$saveIconPackMetadata$savedPack$1(this.$iconPack, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super IconPack> continuation) {
        return ((IconManager$saveIconPackMetadata$savedPack$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        IconPack iconPackCopy$default;
        IntrinsicsKt.getCOROUTINE_SUSPENDED();
        if (this.label != 0) {
            Segment$$ExternalSyntheticBUOutline1.m("call to 'resume' before 'invoke' with coroutine");
            return null;
        }
        ResultKt.throwOnFailure(obj);
        IconPackStorage iconPackStorage = IconPackStorage.INSTANCE;
        IconPack iconPackFindPackById = iconPackStorage.findPackById(this.$iconPack.getId());
        Map<String, String> icons = iconPackFindPackById != null ? iconPackFindPackById.getIcons() : null;
        if (icons == null || (iconPackCopy$default = IconPack.copy$default(this.$iconPack, null, null, null, null, icons, null, null, 111, null)) == null) {
            iconPackCopy$default = this.$iconPack;
        }
        if (iconPackStorage.saveIconPackMetadata(iconPackCopy$default)) {
            return iconPackCopy$default;
        }
        return null;
    }
}
