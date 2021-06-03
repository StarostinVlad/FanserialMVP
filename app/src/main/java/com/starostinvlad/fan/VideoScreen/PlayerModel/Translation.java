package com.starostinvlad.fan.VideoScreen.PlayerModel;

import android.annotation.SuppressLint;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class Translation implements Serializable {

    private Integer id;
    private String title;
    private String url;
    private int code;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("{ %d) %s = %s (%d)}", code, title, url, id);
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
