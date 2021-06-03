package com.starostinvlad.fan.VideoScreen.PlayerModel;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;

public class Episode implements Serializable {
    private Integer id;
    private String title;
    private Integer number;
    private Integer type = 2;
    private List<Translation> translations;
    private int currentTranslationIndex;

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ ").append(number).append(" : [");
        for (Translation translation : translations) {
            stringBuilder.append(translation.toString());
        }
        stringBuilder.append("] }");
        return stringBuilder.toString();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getNumber() {
        return number;
    }

    void setNumber(Integer number) {
        this.number = number;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public Translation getTranslation(int index) {
        return translations.get(index < translations.size() ? index : 0);
    }

    void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }
}
