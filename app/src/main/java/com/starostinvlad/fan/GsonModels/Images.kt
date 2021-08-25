package com.starostinvlad.fan.GsonModels

import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.Throws

class Images : Serializable {
    @SerializedName("small")
    @Expose
    @Ignore
    var small: String? = null

    @SerializedName("medium")
    @Expose
    var medium: String? = null

    @SerializedName("large")
    @Expose
    @Ignore
    var large: String? = null
}