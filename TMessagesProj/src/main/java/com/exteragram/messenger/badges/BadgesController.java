package com.exteragram.messenger.badges;

import android.widget.FrameLayout;
import com.exteragram.messenger.api.db.ExteraDatabase;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.badges.source.ApiBadgeSource;
import com.exteragram.messenger.components.SupporterBottomSheet;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.SetsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineStart;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;

public final class BadgesController {
    private static final BadgeDTO DEV_BADGE;
    public static final BadgesController INSTANCE = new BadgesController();
    private static final BadgeDTO SUPPORTER_BADGE;
    private static final BadgeDTO TRUSTED_BADGE;
    static final ApiBadgeSource apiBadgeSource;
    private static final CoroutineScope scope;
    private static final CachedRemoteSet trustedPluginsCache;

    @JvmOverloads
    public final boolean canChangeBadge() {
        return canChangeBadge$default(this, null, 1, null);
    }

    @JvmOverloads
    public final BadgeDTO getBadge() {
        return getBadge$default(this, null, 1, null);
    }

    @JvmOverloads
    public final boolean hasBadge() {
        return hasBadge$default(this, null, 1, null);
    }

    @JvmOverloads
    public final boolean isDeveloper() {
        return isDeveloper$default(this, null, 1, null);
    }

    @JvmOverloads
    public final void showBadgeBulletin(BaseFragment baseFragment, BadgeDTO badgeDTO, TLRPC.Chat chat, Theme.ResourcesProvider resourcesProvider, int i) {
        showBadgeBulletin$default(this, baseFragment, badgeDTO, chat, resourcesProvider, i, (FrameLayout) null, (Boolean) null, 96, (Object) null);
    }

    @JvmOverloads
    public final void showBadgeBulletin(BaseFragment baseFragment, BadgeDTO badgeDTO, TLRPC.User user, Theme.ResourcesProvider resourcesProvider, int i) {
        showBadgeBulletin$default(this, baseFragment, badgeDTO, user, resourcesProvider, i, (FrameLayout) null, (Boolean) null, 96, (Object) null);
    }

    private BadgesController() {
    }

    static {
        CoroutineScope coroutineScope = CoroutineScopeKt.CoroutineScope(Dispatchers.getIO());
        scope = coroutineScope;
        trustedPluginsCache = new CachedRemoteSet("trusted_plugins", SetsKt.setOf(2562664432L));
        DEV_BADGE = new BadgeDTO(5359407509327085568L, null);
        SUPPORTER_BADGE = new BadgeDTO(5391059537102927631L, null);
        TRUSTED_BADGE = new BadgeDTO(5452008215409629764L, null);
        apiBadgeSource = new ApiBadgeSource(ExteraDatabase.getInstance().profileDao());
        BuildersKt.launch(coroutineScope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, (Function2) new AnonymousClass1(null));
    }

    public static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        int label;

        public AnonymousClass1(Continuation continuation) {
            super(2, (Continuation) continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new AnonymousClass1((Continuation) continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) (Object) create(coroutineScope, (Continuation) continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                ApiBadgeSource apiBadgeSource = BadgesController.apiBadgeSource;
                this.label = 1;
                if (apiBadgeSource.loadToCache(this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            return Unit.INSTANCE;
        }
    }

    public static BadgeDTO getBadge$default(BadgesController badgesController, TLObject tLObject, int i, Object obj) {
        if ((i & 1) != 0) {
            tLObject = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        }
        return badgesController.getBadge(tLObject);
    }

    @JvmOverloads
    public final BadgeDTO getBadge(TLObject obj) {
        Pair pair;
        try {
            if (obj instanceof TLRPC.User) {
                pair = TuplesKt.to(Long.valueOf(((TLRPC.User) obj).id), Boolean.TRUE);
            } else {
                if (!(obj instanceof TLRPC.Chat)) {
                    return null;
                }
                pair = TuplesKt.to(Long.valueOf(((TLRPC.Chat) obj).id), Boolean.FALSE);
            }
            long jLongValue = ((Number) pair.component1()).longValue();
            boolean zBooleanValue = ((Boolean) pair.component2()).booleanValue();
            if (!zBooleanValue && isTrusted(jLongValue)) {
                return TRUSTED_BADGE;
            }
            BadgeDTO badge = apiBadgeSource.getBadge(jLongValue, zBooleanValue);
            if (badge != null) {
                return badge;
            }
            return null;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static boolean hasBadge$default(BadgesController badgesController, TLObject tLObject, int i, Object obj) {
        if ((i & 1) != 0) {
            tLObject = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        }
        return badgesController.hasBadge(tLObject);
    }

    @JvmOverloads
    public final boolean hasBadge(TLObject obj) {
        return getBadge(obj) != null;
    }

    public final boolean isTrusted(long id) {
        return trustedPluginsCache.contains(id);
    }

    public final boolean isExtera(long id) {
        return apiBadgeSource.isDeveloper(id);
    }

    public final boolean isExtera(TLRPC.Chat chat) {
        return chat != null && INSTANCE.isExtera(chat.id);
    }

    public final BadgeDTO getDefaultBadge() {
        if (apiBadgeSource.isDeveloper(UserConfig.getInstance(UserConfig.selectedAccount).clientUserId)) {
            return DEV_BADGE;
        }
        return SUPPORTER_BADGE;
    }

    public final BadgeDTO getDefaultBadge(TLRPC.User user) {
        if (user == null) {
            return null;
        }
        return isDeveloper(user) ? DEV_BADGE : SUPPORTER_BADGE;
    }

    public final boolean shouldUseSecondaryBadgeSlot(TLRPC.User user, BadgeDTO badge) {
        return user != null && badge != null && canChangeBadge(user) && (!Intrinsics.areEqual(badge, getDefaultBadge(user)) || isDeveloper(user));
    }

    public final BadgeDTO getSecondaryBadge(TLRPC.User user) {
        BadgeDTO badge = getBadge(user);
        if (badge != null && INSTANCE.shouldUseSecondaryBadgeSlot(user, badge)) {
            return badge;
        }
        return null;
    }

    public static boolean canChangeBadge$default(BadgesController badgesController, TLRPC.User user, int i, Object obj) {
        if ((i & 1) != 0) {
            user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        }
        return badgesController.canChangeBadge(user);
    }

    @JvmOverloads
    public final boolean canChangeBadge(TLRPC.User user) {
        return apiBadgeSource.canChangeBadge(user.id);
    }

    public final void updateBadge(final BadgeDTO badge, final Consumer<String> callback) {
        String str;
        if (badge != null) {
            String text = badge.getText();
            if (text == null || text.length() == 0) {
                str = "badge " + badge.getDocumentId();
            } else {
                str = "badge " + badge.getDocumentId() + ' ' + text;
            }
            ChatUtils.getInstance(UserConfig.selectedAccount).sendBotRequest(str, false, new Utilities.Callback<String>() {
                @Override
                public final void run(String obj) {
                    BadgesController.$r8$lambda$jpdO54lUMx0lwNLyVBxRsjbiNRg(callback, badge, obj);
                }
            });
            return;
        }
        callback.accept(null);
    }

    public static void $r8$lambda$jpdO54lUMx0lwNLyVBxRsjbiNRg(Consumer consumer, BadgeDTO badgeDTO, String str) {
        if (Intrinsics.areEqual("ok", str)) {
            BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, (Function2) new BadgesController$updateBadge$1$1(badgeDTO, null));
        }
        consumer.accept(str);
    }

    public static boolean isDeveloper$default(BadgesController badgesController, TLRPC.User user, int i, Object obj) {
        if ((i & 1) != 0) {
            user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        }
        return badgesController.isDeveloper(user);
    }

    @JvmOverloads
    public final boolean isDeveloper(TLRPC.User user) {
        return apiBadgeSource.isDeveloper(user.id);
    }

    @JvmOverloads
    public final void showBadgeBulletin(BaseFragment fragment, TLRPC.User user, Theme.ResourcesProvider resourcesProvider, int account, FrameLayout containerLayout, Boolean showButton) {
        BadgeDTO badge;
        if (user == null || (badge = getBadge(user)) == null) {
            return;
        }
        showBadgeBulletin(fragment, badge, user, resourcesProvider, account, containerLayout, showButton);
    }

    public static void showBadgeBulletin$default(BadgesController badgesController, BaseFragment baseFragment, BadgeDTO badgeDTO, TLRPC.User user, Theme.ResourcesProvider resourcesProvider, int i, FrameLayout frameLayout, Boolean bool, int i2, Object obj) {
        if ((i2 & 8) != 0) {
            resourcesProvider = baseFragment.getResourceProvider();
        }
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        if ((i2 & 16) != 0) {
            i = baseFragment.getCurrentAccount();
        }
        badgesController.showBadgeBulletin(baseFragment, badgeDTO, user, resourcesProvider2, i, (i2 & 32) != 0 ? null : frameLayout, (i2 & 64) != 0 ? null : bool);
    }

    @JvmOverloads
    public final void showBadgeBulletin(BaseFragment fragment, BadgeDTO badge, TLRPC.User user, Theme.ResourcesProvider resourcesProvider, int account, FrameLayout containerLayout, Boolean showButton) {
        boolean zBooleanValue;
        if (user == null) {
            return;
        }
        boolean zIsDeveloper = isDeveloper(user);
        CharSequence badgeText = formatBadgeText(badge, LocaleController.formatString(zIsDeveloper ? R.string.Developer : R.string.Supporter, user.first_name));
        if (showButton != null) {
            zBooleanValue = showButton.booleanValue();
        } else {
            zBooleanValue = (zIsDeveloper || canChangeBadge(user)) ? false : true;
        }
        showBadgeBulletin(fragment, badge, badgeText, zBooleanValue, resourcesProvider, account, containerLayout);
    }

    public static void showBadgeBulletin$default(BadgesController badgesController, BaseFragment baseFragment, BadgeDTO badgeDTO, TLRPC.Chat chat, Theme.ResourcesProvider resourcesProvider, int i, FrameLayout frameLayout, Boolean bool, int i2, Object obj) {
        if ((i2 & 8) != 0) {
            resourcesProvider = baseFragment.getResourceProvider();
        }
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        if ((i2 & 16) != 0) {
            i = baseFragment.getCurrentAccount();
        }
        badgesController.showBadgeBulletin(baseFragment, badgeDTO, chat, resourcesProvider2, i, (i2 & 32) != 0 ? null : frameLayout, (i2 & 64) != 0 ? null : bool);
    }

    @JvmOverloads
    public final void showBadgeBulletin(BaseFragment fragment, BadgeDTO badge, TLRPC.Chat chat, Theme.ResourcesProvider resourcesProvider, int account, FrameLayout containerLayout, Boolean showButton) {
        String string;
        boolean zBooleanValue;
        if (chat == null) {
            return;
        }
        boolean zIsExtera = isExtera(chat);
        boolean zIsTrusted = isTrusted(chat.id);
        if (zIsExtera) {
            string = LocaleController.formatString(R.string.OfficialChannel, chat.title);
        } else if (zIsTrusted) {
            string = LocaleController.getString(R.string.PluginSourceTrustedInfo);
        } else {
            string = LocaleController.formatString(R.string.Supporter, chat.title);
        }
        CharSequence badgeText = formatBadgeText(badge, string);
        if (showButton != null) {
            zBooleanValue = showButton.booleanValue();
        } else {
            zBooleanValue = (zIsExtera || zIsTrusted) ? false : true;
        }
        showBadgeBulletin(fragment, badge, badgeText, zBooleanValue, resourcesProvider, account, containerLayout);
    }

    private final void showBadgeBulletin(final BaseFragment fragment, BadgeDTO badge, CharSequence text, boolean showButton, final Theme.ResourcesProvider resourcesProvider, int account, FrameLayout containerLayout) {
        BulletinFactory bulletinFactoryOf;
        if (containerLayout != null) {
            bulletinFactoryOf = BulletinFactory.of(containerLayout, resourcesProvider);
        } else {
            bulletinFactoryOf = BulletinFactory.of(fragment);
        }
        Bulletin bulletinCreateEmojiBulletin = bulletinFactoryOf.createEmojiBulletin(AnimatedEmojiDrawable.findDocument(account, badge.getDocumentId()), text, showButton ? LocaleController.getString(R.string.FragmentUsernameOpen) : null, showButton ? new Runnable() {
            @Override
            public final void run() {
                SupporterBottomSheet.showAlert(fragment, resourcesProvider);
            }
        } : null);
        if (!showButton) {
            bulletinCreateEmojiBulletin.wrapContent();
        }
        bulletinCreateEmojiBulletin.show();
    }

    private final CharSequence formatBadgeText(BadgeDTO badge, CharSequence fallbackText) {
        CharSequence withUsernames;
        String text = badge.getText();
        return (text == null || (withUsernames = LocaleUtils.formatWithUsernames(text)) == null) ? fallbackText : withUsernames;
    }
}
