package com.exteragram.messenger.ai.data;

import com.exteragram.messenger.ai.AiConfig;
import java.io.Serializable;
import java.util.Objects;

public class Role implements Comparable<Role>, Serializable {
    private long emojiId;
    private boolean isSuggestion;
    private String name;
    private String prompt;

    public Role(String str, String str2) {
        this.name = str;
        this.prompt = str2;
    }

    @Override // java.lang.Comparable
    public int compareTo(Role role) {
        return this.name.compareTo(role.getName());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.name.equals(((Role) obj).name);
    }

    public String getName() {
        return this.name;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public long getEmojiId() {
        return this.emojiId;
    }

    public Role setEmojiId(long j) {
        this.emojiId = j;
        return this;
    }

    public boolean isSuggestion() {
        return this.isSuggestion;
    }

    public Role setSuggestion(boolean z) {
        this.isSuggestion = z;
        return this;
    }

    public boolean isSelected() {
        return Objects.equals(AiConfig.getSelectedRole(), this.name);
    }
}
