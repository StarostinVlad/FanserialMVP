package com.starostinvlad.fan.GsonModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Datum implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @SerializedName("serial")
    @Expose
    @Embedded(prefix = "serial")
    private Serial serial;
    @SerializedName("episode")
    @Expose
    @Embedded(prefix = "episode")
    private Episode episode;

    public Serial getSerial() {
        return serial;
    }

    public void setSerial(Serial serial) {
        this.serial = serial;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

}
