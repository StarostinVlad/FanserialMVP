package com.starostinvlad.fan.GsonModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.Throws

class Viewed {
    @SerializedName("current")
    @Expose
    var current: Datum? = null

    @SerializedName("next")
    @Expose
    var next: Datum? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
}