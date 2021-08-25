package com.starostinvlad.fan.VideoScreen

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.Cookie
import kotlin.Throws

class Player {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("player")
    @Expose
    var player: String? = null
    var cookie: Cookie? = null
}