package com.starostinvlad.fan.VideoScreen.PlayerModel;

public class Hls {
    public String src;
    public String ruSub;
    public String enSub;

    public Hls(String src, String enSub, String ruSub) {
        this.src = src;
        this.ruSub = ruSub;
        this.enSub = enSub;
    }
}
