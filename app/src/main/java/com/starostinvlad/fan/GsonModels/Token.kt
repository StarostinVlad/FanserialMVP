package com.starostinvlad.fan.GsonModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.Throws

class Token {
    @SerializedName("token")
    @Expose
    var token: String? = null
}