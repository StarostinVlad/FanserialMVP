package com.starostinvlad.fan.GsonModels

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.Throws

@Entity
class Datum : Serializable {
    @kotlin.jvm.JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @SerializedName("serial")
    @Expose
    @Embedded(prefix = "serial")
    var serial: Serial? = null

    @SerializedName("episode")
    @Expose
    @Embedded(prefix = "episode")
    var episode: Episode? = null
}