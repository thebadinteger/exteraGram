package com.exteragram.messenger.export.api;

import java.util.ArrayList;

public class ApiWrap$DialogsInfo {
    public ArrayList<ApiWrap$DialogInfo> chats = new ArrayList<>();
    public ArrayList<ApiWrap$DialogInfo> left = new ArrayList<>();

    public ApiWrap$DialogInfo getItemAt(int i) {
        if (i < 0) {
            return null;
        }
        int size = this.chats.size();
        if (i < size) {
            return this.chats.get(i);
        }
        int i2 = i - size;
        if (i2 < this.left.size()) {
            return this.left.get(i2);
        }
        return null;
    }
}
