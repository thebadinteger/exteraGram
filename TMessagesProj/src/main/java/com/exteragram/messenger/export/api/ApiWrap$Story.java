package com.exteragram.messenger.export.api;

import java.util.ArrayList;

public class ApiWrap$Story {
    public ArrayList<ApiWrap$TextPart> caption;
    public ApiWrap$Media media;
    public int id = 0;
    public int date = 0;
    public int expires = 0;
    public boolean pinned = false;

    public ApiWrap$File file() {
        return this.media.getFile();
    }

    public ApiWrap$Image thumb() {
        return this.media.getThumb();
    }
}
