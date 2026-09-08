package com.exteragram.messenger.icons;

import java.io.File;
import kotlin.LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import okhttp3.internal.url._UrlKt;
import okio.Segment$$ExternalSyntheticBUOutline1;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
@DebugMetadata(c = "com.exteragram.messenger.icons.IconManager$handleIconPack$1$1$bottomSheet$1$1", f = "IconManager.kt", i = {1}, l = {775, 776}, m = "invokeSuspend", n = {"installResult"}, s = {"L$0"}, v = 1)
public final class IconManager$handleIconPack$1$1$bottomSheet$1$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final throw new UnsupportedOperationException("Method not decompiled: com.exteragram.messenger.icons.IconManager$handleIconPack$1$1$bottomSheet$1$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }

    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.icons.IconManager$handleIconPack$1$1$bottomSheet$1$1$1", f = "IconManager.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ BaseFragment $baseFragment;
        final /* synthetic */ boolean $enable;
        final /* synthetic */ IconPackStorageResult<Unit> $installResult;
        final /* synthetic */ IconPack $pack;
        final /* synthetic */ boolean $update;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AnonymousClass1(IconPackStorageResult<Unit> iconPackStorageResult, BaseFragment baseFragment, boolean z, IconPack iconPack, boolean z2, Continuation<? super AnonymousClass1> continuation) {
            super(2, continuation);
            this.$installResult = iconPackStorageResult;
            this.$baseFragment = baseFragment;
            this.$update = z;
            this.$pack = iconPack;
            this.$enable = z2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new AnonymousClass1(this.$installResult, this.$baseFragment, this.$update, this.$pack, this.$enable, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            if (this.label != 0) {
                Segment$$ExternalSyntheticBUOutline1.m("call to 'resume' before 'invoke' with coroutine");
                return null;
            }
            ResultKt.throwOnFailure(obj);
            IconPackStorageResult<Unit> iconPackStorageResult = this.$installResult;
            if (iconPackStorageResult instanceof IconPackStorageResult.Success) {
                BulletinFactory.of(this.$baseFragment).createSimpleBulletin(R.raw.contact_check, LocaleController.formatString(this.$update ? R.string.PluginUpdated : R.string.PluginInstalled, this.$pack.getName())).show();
                if (this.$enable) {
                    IconManager.INSTANCE.setActiveCustomPack(this.$pack.getId());
                } else {
                    IconManager iconManager = IconManager.INSTANCE;
                    iconManager.syncInstalledCustomPacks(CollectionsKt.listOf(this.$pack));
                    iconManager.initialize(true);
                }
            } else if (iconPackStorageResult instanceof IconPackStorageResult.Failure) {
                IconManager.INSTANCE.showIconPackError(this.$baseFragment, ((IconPackStorageResult.Failure) iconPackStorageResult).getError());
            } else {
                LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                return null;
            }
            return Unit.INSTANCE;
        }
    }
}
