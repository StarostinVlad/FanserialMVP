package com.starostinvlad.fan.GsonModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.Throws

class Settings : Serializable {
    @SerializedName("domain")
    @Expose
    var domain: String? = null

    @SerializedName("proxy")
    @Expose
    var proxy: Proxy? = null

    @SerializedName("review")
    @Expose
    var isReview = false

    @SerializedName("last_version")
    @Expose
    var lastVersion = 0
    fun getReview(): Boolean {
        return isReview
    }
}