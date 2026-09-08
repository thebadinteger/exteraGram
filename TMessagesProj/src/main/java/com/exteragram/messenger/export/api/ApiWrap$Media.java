package com.exteragram.messenger.export.api;

import com.exteragram.messenger.export.output.html.HtmlWriter;

public class ApiWrap$Media {
    public Object content;
    public int ttl = 0;

    public ApiWrap$File getFile() {
        Object obj = this.content;
        if (obj instanceof HtmlWriter.Photo) {
            return ((HtmlWriter.Photo) obj).image.file;
        }
        if (obj instanceof ApiWrap$Document) {
            return ((ApiWrap$Document) obj).file;
        }
        if (obj instanceof ApiWrap$SharedContact) {
            return ((ApiWrap$SharedContact) obj).vcard;
        }
        return new ApiWrap$File();
    }

    public ApiWrap$Image getThumb() {
        Object obj = this.content;
        if (obj instanceof ApiWrap$Document) {
            return ((ApiWrap$Document) obj).thumb;
        }
        return new ApiWrap$Image();
    }
}
