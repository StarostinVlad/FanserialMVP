package com.starostinvlad.fan.VideoScreen.PlayerModel;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Season {

    private List<Episode> episodes = null;

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public Episode getEpisode(int i) {
        return episodes.get(i);
    }

    void setEpisodes(List<Episode> episodeJS) {
        this.episodes = episodeJS;
    }

}

