package com.starostinvlad.fan.GsonModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Settings implements Serializable {

    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("proxy")
    @Expose
    private Proxy proxy;
    @SerializedName("review")
    @Expose
    private boolean review;

    public boolean isReview() {
        return review;
    }

    public int getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(int lastVersion) {
        this.lastVersion = lastVersion;
    }

    @SerializedName("last_version")
    @Expose
    private int lastVersion = 0;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public boolean getReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }

}
