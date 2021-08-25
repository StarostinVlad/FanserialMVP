package com.starostinvlad.fan.GsonModels

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.Throws

@Entity
class Searched : Serializable {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("poster")
    @Expose
    @Embedded
    var poster: Images? = null
}