package com.starostinvlad.fan.VideoScreen.PlayerModel;

import java.util.ArrayList;

public class Serial {

    private ArrayList<Translation> translations;

    public ArrayList<Translation> getTranslations() {
        return translations;
    }

    void setTranslations(ArrayList<Translation> translations) {
        this.translations = translations;
    }

    public int getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(int currentSeason) {
        this.currentSeason = currentSeason;
    }

    public int getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(int currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    public int getCurrentTranslationIndex() {
        return currentTranslation;
    }

    public void setCurrentTranslationIndex(int currentTranslation) {
        this.currentTranslation = currentTranslation;
    }

    public Translation getCurrentTranslation() {
        return translations.get(currentTranslation);
    }

    private int currentSeason = 0;
    private int currentEpisode = 0;
    private int currentTranslation = 0;

}
