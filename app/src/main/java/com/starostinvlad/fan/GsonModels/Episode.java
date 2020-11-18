package com.starostinvlad.fan.GsonModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Ignore;

public class Episode implements Serializable {

    @SerializedName("id")
    @Expose
    @Ignore
    private Integer id;
    @SerializedName("ordinal")
    @Expose
    @Embedded
    private Ordinal ordinal;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("voices")
    @Expose
    @Ignore
    private List<Voice> voices = null;
    @SerializedName("images")
    @Expose
    @Embedded
    private Images images;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ordinal getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Ordinal ordinal) {
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Voice> getVoices() {
        return voices;
    }

    public void setVoices(List<Voice> voices) {
        this.voices = voices;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

}
