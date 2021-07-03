package com.starostinvlad.fan.GsonModels;

import android.annotation.SuppressLint;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class News implements Serializable {


    @PrimaryKey()
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("siteId")
    @Expose
    private String siteId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("subTitle")
    @Expose
    private String subTitle;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("href")
    @Expose
    private String href;

    public News() {
    }

    @Ignore
    public News(String title, String subTitle, String image, String href, String siteId) {
        this.title = title;
        this.subTitle = subTitle;
        this.image = image;
        this.href = href;
        this.siteId = siteId;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {

        return String.format(
                "{id:%d, siteId:%s, title:%s, subTitle:%s, image:%s, href:%s}",
                id,
                siteId,
                title,
                subTitle,
                image,
                href
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}
