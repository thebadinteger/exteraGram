package com.exteragram.messenger.plugins.utils;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.ui.ActionBar.BaseFragment;

@Metadata(d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\u0002\b\u0007\u0018\u0000 .2\u00020\u0001:\u0001.B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\b\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\nJ\u0010\u0010\u000b\u001a\u00020\u00002\b\u0010\f\u001a\u0004\u0018\u00010\rJ\u0010\u0010\u000e\u001a\u00020\u00002\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010J\u0010\u0010\u0011\u001a\u00020\u00002\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013J\u0010\u0010\u0014\u001a\u00020\u00002\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016J\u0010\u0010\u0017\u001a\u00020\u00002\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019J\u0010\u0010\u001a\u001a\u00020\u00002\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cJ\u0010\u0010\u001d\u001a\u00020\u00002\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fJ\u000e\u0010 \u001a\u00020\u00002\u0006\u0010!\u001a\u00020\"J\u0010\u0010#\u001a\u00020\u00002\b\u0010$\u001a\u0004\u0018\u00010%J\u0010\u0010&\u001a\u00020\u00002\b\u0010'\u001a\u0004\u0018\u00010(J\u001a\u0010)\u001a\u00020\u00002\b\u0010*\u001a\u0004\u0018\u00010\u00062\b\u0010+\u001a\u0004\u0018\u00010\u0001J\u0012\u0010,\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010-R*\u0010\u0004\u001a\u001e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005j\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0001`\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006/"}, d2 = {"Lcom/exteragram/messenger/plugins/utils/MenuContextBuilder;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "contextData", "Ljava/util/HashMap;", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/collections/HashMap;", "withAccount", "account", _UrlKt.FRAGMENT_ENCODE_SET, "withContext", "context", "Landroid/content/Context;", "withEncryptedChat", "encryptedChat", "Lorg/telegram/tgnet/TLRPC$EncryptedChat;", "withChat", "chat", "Lorg/telegram/tgnet/TLRPC$Chat;", "withChatFull", "chatFull", "Lorg/telegram/tgnet/TLRPC$ChatFull;", "withUser", "user", "Lorg/telegram/tgnet/TLRPC$User;", "withUserFull", "userFull", "Lorg/telegram/tgnet/TLRPC$UserFull;", "withBotInfo", "botInfo", "Lorg/telegram/tgnet/tl/TL_bots$BotInfo;", "withDialogId", "dialogId", _UrlKt.FRAGMENT_ENCODE_SET, "withMessage", "message", "Lorg/telegram/messenger/MessageObject;", "withGroupedMessage", "groupedMessages", "Lorg/telegram/messenger/MessageObject$GroupedMessages;", "withCustom", "key", "value", "build", _UrlKt.FRAGMENT_ENCODE_SET, "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class MenuContextBuilder {

    public static final Companion INSTANCE = new Companion(null);
    private final HashMap<String, Object> contextData;

    public /* synthetic */ MenuContextBuilder(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    @JvmStatic
    public static final MenuContextBuilder create() {
        return INSTANCE.create();
    }

    @JvmStatic
    public static final MenuContextBuilder from(BaseFragment baseFragment) {
        return INSTANCE.from(baseFragment);
    }

    private MenuContextBuilder() {
        this.contextData = new HashMap<>();
    }

    public final MenuContextBuilder withAccount(int account) {
        this.contextData.put("account", Integer.valueOf(account));
        return this;
    }

    public final MenuContextBuilder withContext(Context context) {
        if (context != null) {
            this.contextData.put("context", context);
        }
        return this;
    }

    public final MenuContextBuilder withEncryptedChat(TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.contextData.put("encryptedChat", encryptedChat);
        }
        return this;
    }

    public final MenuContextBuilder withChat(TLRPC.Chat chat) {
        if (chat != null) {
            this.contextData.put("chat", chat);
            this.contextData.put("chatId", Long.valueOf(chat.id));
        }
        return this;
    }

    public final MenuContextBuilder withChatFull(TLRPC.ChatFull chatFull) {
        if (chatFull != null) {
            this.contextData.put("chatFull", chatFull);
        }
        return this;
    }

    public final MenuContextBuilder withUser(TLRPC.User user) {
        if (user != null) {
            this.contextData.put("user", user);
            this.contextData.put("userId", Long.valueOf(user.id));
        }
        return this;
    }

    public final MenuContextBuilder withUserFull(TLRPC.UserFull userFull) {
        if (userFull != null) {
            this.contextData.put("userFull", userFull);
        }
        return this;
    }

    public final MenuContextBuilder withBotInfo(TL_bots.BotInfo botInfo) {
        if (botInfo != null) {
            this.contextData.put("botInfo", botInfo);
        }
        return this;
    }

    public final MenuContextBuilder withDialogId(long dialogId) {
        this.contextData.put("dialog_id", Long.valueOf(dialogId));
        return this;
    }

    public final MenuContextBuilder withMessage(MessageObject message) {
        if (message != null) {
            this.contextData.put("message", message);
        }
        return this;
    }

    public final MenuContextBuilder withGroupedMessage(MessageObject.GroupedMessages groupedMessages) {
        if (groupedMessages != null) {
            this.contextData.put("groupedMessages", groupedMessages);
        }
        return this;
    }

    public final MenuContextBuilder withCustom(String key, Object value) {
        if (key != null && value != null) {
            this.contextData.put(key, value);
        }
        return this;
    }

    public final Map<String, Object> build() {
        return this.contextData;
    }

    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0007J\u0012\u0010\u0006\u001a\u00020\u00052\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0007¨\u0006\t"}, d2 = {"Lcom/exteragram/messenger/plugins/utils/MenuContextBuilder$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "create", "Lcom/exteragram/messenger/plugins/utils/MenuContextBuilder;", "from", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final MenuContextBuilder create() {
            return new MenuContextBuilder(null);
        }

        @JvmStatic
        public final MenuContextBuilder from(BaseFragment fragment) {
            if (fragment == null) {
                return create();
            }
            return create().withCustom("fragment", fragment).withAccount(fragment.getCurrentAccount()).withContext(fragment.getParentActivity());
        }
    }
}
