package com.starostinvlad.fan.GsonModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.Throws

class Proxy {
    @SerializedName("ip")
    @Expose
    var ip: String? = null

    @SerializedName("port")
    @Expose
    var port: Int? = null
}