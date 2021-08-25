package com.starostinvlad.fan.GsonModels

import androidx.room.Embedded
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.Throws

class Episode : Serializable {
    @SerializedName("id")
    @Expose
    @Ignore
    var id: Int? = null

    @SerializedName("ordinal")
    @Expose
    @Embedded
    var ordinal: Ordinal? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("voices")
    @Expose
    @Ignore
    var voices: List<Voice>? = null

    @SerializedName("images")
    @Expose
    @Embedded
    var images: Images? = null
}