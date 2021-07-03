package com.starostinvlad.fan.GsonModels;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class CurrentSerial {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String pageId;
    private int currentSeasonIndex = 0;
    private int currentEpisodeIndex = 0;
    private int currentTranslationIndex = 0;

    public CurrentSerial() {

    }

    @Ignore
    public CurrentSerial(String pageId, int currentSeasonIndex, int currentEpisodeIndex, int currentTranslationIndex) {
        this.pageId = pageId;
        this.currentSeasonIndex = currentSeasonIndex;
        this.currentEpisodeIndex = currentEpisodeIndex;
        this.currentTranslationIndex = currentTranslationIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
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
        return currentTranslationIndex;
    }

    public void setCurrentTranslationIndex(int currentTranslationIndex) {
        this.currentTranslationIndex = currentTranslationIndex;
    }
}
