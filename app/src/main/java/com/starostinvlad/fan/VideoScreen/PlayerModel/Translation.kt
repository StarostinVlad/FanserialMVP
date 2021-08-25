package com.starostinvlad.fan.VideoScreen.PlayerModel

import android.annotation.SuppressLint
import java.io.Serializable
import kotlin.Throws

class Translation : Serializable {
    var id: Int = 0
    var title: String = ""
    var url: String = ""
    var code = 0

    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        return String.format("{ %d) %s = %s (%d)}", code, title, url, id)
    }
}