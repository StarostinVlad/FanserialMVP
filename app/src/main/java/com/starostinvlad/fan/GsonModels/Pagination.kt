package com.starostinvlad.fan.GsonModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.Throws

class Pagination {
    @SerializedName("offset")
    @Expose
    var offset: Int? = null

    @SerializedName("limit")
    @Expose
    var limit: Int? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null
}