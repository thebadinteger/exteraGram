package com.exteragram.messenger.ai.network.backend;

import com.exteragram.messenger.ai.data.Message;
import com.exteragram.messenger.ai.data.Role;
import com.exteragram.messenger.ai.data.Service;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public final class BackendRequest {
    private final Message currentMessage;
    private final List<Message> history;
    private final int maxOutputTokens;
    private final Role role;
    private final Service service;
    private final boolean stream;
    private final float temperature;

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof BackendRequest)) {
            return false;
        }
        BackendRequest backendRequest = (BackendRequest) obj;
        return this.stream == backendRequest.stream && this.maxOutputTokens == backendRequest.maxOutputTokens && this.temperature == backendRequest.temperature && Objects.equals(this.service, backendRequest.service) && Objects.equals(this.role, backendRequest.role) && Objects.equals(this.history, backendRequest.history) && Objects.equals(this.currentMessage, backendRequest.currentMessage);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{this.service, this.role, this.history, this.currentMessage, Boolean.valueOf(this.stream), Float.valueOf(this.temperature), Integer.valueOf(this.maxOutputTokens)};
    }

    public BackendRequest(Service service, Role role, List<Message> list, Message message, boolean z, float f, int i) {
        this.service = service;
        this.role = role;
        this.history = list;
        this.currentMessage = message;
        this.stream = z;
        this.temperature = f;
        this.maxOutputTokens = i;
    }

    public Message currentMessage() {
        return this.currentMessage;
    }

    public final boolean equals(Object obj) {
        return $record$equals(obj);
    }

    public final int hashCode() {
        return Objects.hash(this.service, this.role, this.history, this.currentMessage, this.stream, this.temperature, this.maxOutputTokens);
    }

    public List<Message> history() {
        return this.history;
    }

    public int maxOutputTokens() {
        return this.maxOutputTokens;
    }

    public Role role() {
        return this.role;
    }

    public Service service() {
        return this.service;
    }

    public boolean stream() {
        return this.stream;
    }

    public float temperature() {
        return this.temperature;
    }

    public final String toString() {
        return "BackendRequest[service=" + this.service + ", role=" + this.role + "]";
    }
}
