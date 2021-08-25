package com.starostinvlad.fan.GsonModels

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.Throws

@Entity
class News : Serializable {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("siteId")
    @Expose
    var siteId: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("subTitle")
    @Expose
    var subTitle: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("href")
    @Expose
    var href: String? = null

    constructor() {}

    @Ignore
    constructor(title: String?, subTitle: String?, image: String?, href: String?, siteId: String?) {
        this.title = title
        this.subTitle = subTitle
        this.image = image
        this.href = href
        this.siteId = siteId
    }

    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        return String.format(
                "{id:%d, siteId:%s, title:%s, subTitle:%s, image:%s, href:%s}",
                id,
                siteId,
                title,
                subTitle,
                image,
                href
        )
    }
}