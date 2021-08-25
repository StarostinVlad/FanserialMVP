package com.starostinvlad.fan.GsonModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.Throws

class Serial : Serializable {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("closed")
    @Expose
    var closed: Boolean? = null
}