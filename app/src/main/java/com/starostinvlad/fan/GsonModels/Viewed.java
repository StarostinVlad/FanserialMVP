package com.starostinvlad.fan.GsonModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Viewed {

    @SerializedName("current")
    @Expose
    private Datum current;
    @SerializedName("next")
    @Expose
    private Datum next;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public Datum getCurrent() {
        return current;
    }

    public void setCurrent(Datum current) {
        this.current = current;
    }

    public Datum getNext() {
        return next;
    }

    public void setNext(Datum next) {
        this.next = next;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}