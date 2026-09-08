package com.exteragram.messenger.ai.data;

import java.util.Objects;

public final class Message {
    private final String content;
    private byte[] imageData;
    private String mimeType;
    private final String role;

    public Message(String str, String str2) {
        this.role = str;
        this.content = str2;
    }

    public Message(String str, String str2, byte[] bArr, String str3) {
        this.role = str;
        this.content = str2;
        this.imageData = bArr;
        this.mimeType = str3;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || Message.class != obj.getClass()) {
            return false;
        }
        return this.content.equals(((Message) obj).content);
    }

    public String role() {
        return this.role;
    }

    public String content() {
        return this.content;
    }

    public byte[] getImageData() {
        return this.imageData;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public int hashCode() {
        return Objects.hash(this.role, this.content);
    }
}
