package com.starostinvlad.fan.GsonModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.Throws

class Ordinal : Serializable {
    @SerializedName("season")
    @Expose
    var season: Int? = null

    @SerializedName("episode")
    @Expose
    var episode: Int? = null
}