package com.starostinvlad.rxeducation.VideoScreen;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import okhttp3.Cookie;

public class Player {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("player")
    @Expose
    private String player;

    Cookie cookie;

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

}
