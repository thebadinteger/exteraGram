package com.exteragram.messenger.plugins.hooks;

import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001:\u0001\u0019J\"\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0003H&J,\u0010\t\u001a\u00020\n2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\u000b\u001a\u0004\u0018\u00010\u00032\b\u0010\f\u001a\u0004\u0018\u00010\rH&J\"\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u000fH&J\"\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u0013H&J\u001a\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0018\u001a\u00020\u0017H&¨\u0006\u001aÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/plugins/hooks/PluginsHooks;", _UrlKt.FRAGMENT_ENCODE_SET, "executePreRequestHook", "Lorg/telegram/tgnet/TLObject;", "requestName", _UrlKt.FRAGMENT_ENCODE_SET, "account", _UrlKt.FRAGMENT_ENCODE_SET, "request", "executePostRequestHook", "Lcom/exteragram/messenger/plugins/hooks/PluginsHooks$PostRequestResult;", "response", "", "Lorg/telegram/tgnet/TLRPC$TL_error;", "executeUpdateHook", "Lorg/telegram/tgnet/TLRPC$Update;", "updateName", "update", "executeUpdatesHook", "Lorg/telegram/tgnet/TLRPC$Updates;", "containerName", "updates", "executeSendMessageHook", "Lorg/telegram/messenger/SendMessagesHelper$SendMessageParams;", "params", "PostRequestResult", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public interface PluginsHooks {
    PostRequestResult executePostRequestHook(String requestName, int account, TLObject response, TLRPC.TL_error error);

    TLObject executePreRequestHook(String requestName, int account, TLObject request);

    SendMessagesHelper.SendMessageParams executeSendMessageHook(int account, SendMessagesHelper.SendMessageParams params);

    TLRPC.Update executeUpdateHook(String updateName, int account, TLRPC.Update update);

    TLRPC.Updates executeUpdatesHook(String containerName, int account, TLRPC.Updates updates);

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\u001b\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f¨\u0006\u0010"}, d2 = {"Lcom/exteragram/messenger/plugins/hooks/PluginsHooks$PostRequestResult;", _UrlKt.FRAGMENT_ENCODE_SET, "response", "Lorg/telegram/tgnet/TLObject;", "", "Lorg/telegram/tgnet/TLRPC$TL_error;", "<init>", "(Lorg/telegram/tgnet/TLObject;Lorg/telegram/tgnet/TLRPC$TL_error;)V", "getResponse", "()Lorg/telegram/tgnet/TLObject;", "setResponse", "(Lorg/telegram/tgnet/TLObject;)V", "getError", "()Lorg/telegram/tgnet/TLRPC$TL_error;", "setError", "(Lorg/telegram/tgnet/TLRPC$TL_error;)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class PostRequestResult {
        private TLRPC.TL_error error;
        private TLObject response;

        public PostRequestResult(TLObject tLObject, TLRPC.TL_error tL_error) {
            this.response = tLObject;
            this.error = tL_error;
        }

        public final TLObject getResponse() {
            return this.response;
        }

        public final void setResponse(TLObject tLObject) {
            this.response = tLObject;
        }

        public final TLRPC.TL_error getError() {
            return this.error;
        }

        public final void setError(TLRPC.TL_error tL_error) {
            this.error = tL_error;
        }
    }
}
