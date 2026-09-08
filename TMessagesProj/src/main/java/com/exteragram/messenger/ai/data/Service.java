package com.exteragram.messenger.ai.data;

import android.text.TextUtils;
import com.exteragram.messenger.ai.AiConfig;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import okhttp3.internal.url._UrlKt;

public class Service implements Serializable {
    private String id;
    private String key;
    private String model;
    private boolean reasoningEnabled;
    private String url;

    public Service(String str, String str2, String str3, boolean z) {
        this(UUID.randomUUID().toString(), str, str2, str3, z);
    }

    public Service(String str, String str2, String str3, String str4) {
        this(str, str2, str3, str4, false);
    }

    public Service(String str, String str2, String str3, String str4, boolean z) {
        this.id = str;
        this.url = str2;
        this.model = str3;
        this.key = str4;
        this.reasoningEnabled = z;
    }

    public String getId() {
        ensureId();
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public boolean ensureId() {
        String str = this.id;
        if (str != null && !str.isEmpty()) {
            return false;
        }
        this.id = UUID.randomUUID().toString();
        return true;
    }

    public String getUrl() {
        return this.url;
    }

    public String getModel() {
        return this.model;
    }

    public String getShortModel() {
        String str = this.model;
        if (str == null) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        String[] strArrSplit = str.split("/");
        String str2 = strArrSplit[strArrSplit.length - 1];
        int iIndexOf = str2.indexOf(58);
        return iIndexOf != -1 ? str2.substring(0, iIndexOf) : str2;
    }

    public String getKey() {
        return this.key;
    }

    public boolean isReasoningEnabled() {
        return this.reasoningEnabled;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            Service service = (Service) obj;
            if (Objects.equals(this.url, service.url) && Objects.equals(this.model, service.model) && Objects.equals(this.key, service.key)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.url, this.model, this.key);
    }

    public int getLegacyHash() {
        return (this.url + this.model + this.key).hashCode();
    }

    public boolean isSelected() {
        String selectedServiceId = AiConfig.getSelectedServiceId();
        if (!TextUtils.isEmpty(selectedServiceId)) {
            return Objects.equals(selectedServiceId, getId());
        }
        return Objects.equals(AiConfig.getSelectedService().getId(), getId());
    }
}
