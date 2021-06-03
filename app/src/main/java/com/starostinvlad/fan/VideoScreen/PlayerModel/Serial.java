package com.starostinvlad.fan.VideoScreen.PlayerModel;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;

public class Serial implements Serializable {
    private List<Season> seasonList;

    private int currentSeasonIndex = 0;
    private int currentEpisodeIndex = 0;
    private int currentTranslationCode = 0;

    @NonNull
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("serial : [");
        for (Season season : seasonList) {
            str.append(season.toString()).append(", ");
        }
        str.append("]");
        return str.toString();
    }

    public Season getCurrentSeason() {
        return seasonList.get(currentSeasonIndex);
    }

    public int getCurrentSeasonIndex() {
        return currentSeasonIndex;
    }

    public void setCurrentSeasonIndex(int currentSeasonIndex) {
        this.currentSeasonIndex = currentSeasonIndex;
    }

    public int getCurrentEpisodeIndex() {
        return currentEpisodeIndex;
    }

    public void setCurrentEpisodeIndex(int currentEpisodeIndex) {
        this.currentEpisodeIndex = currentEpisodeIndex;
    }

    public int getCurrentTranslationIndex() {
        return currentTranslationCode;
    }

    public void setCurrentTranslationIndex(int currentTranslation) {
        this.currentTranslationCode = currentTranslation;
    }

    public List<Season> getSeasonList() {
        return seasonList;
    }

    void setSeasonList(List<Season> seasonList) {
        this.seasonList = seasonList;
    }
}
