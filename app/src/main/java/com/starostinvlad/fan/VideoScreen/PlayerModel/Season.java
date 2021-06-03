package com.starostinvlad.fan.VideoScreen.PlayerModel;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;


public class Season implements Serializable {

    private int number;
    private String title;
    private List<Episode> episodes;

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{").append(number).append(" : [");
        for (Episode episode : episodes) {
            stringBuilder.append(episode.toString());
        }
        stringBuilder.append("]}");
        return stringBuilder.toString();
    }

    public Episode getEpisode(int index) {
            return episodes.get(index < episodes.size() ? index : 0);
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

