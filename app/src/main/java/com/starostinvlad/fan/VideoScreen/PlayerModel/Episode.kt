package com.starostinvlad.fan.VideoScreen.PlayerModel

import java.io.Serializable
import kotlin.Throws

class Episode : Serializable {
    var id: Int = 0
    var title: String = ""
    var number: Int = 0
    var type = 2
    var translations: MutableList<Translation>? = null
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("{ ").append(number).append(" : [")
        for (translation in translations!!) {
            stringBuilder.append(translation.toString())
        }
        stringBuilder.append("] }")
        return stringBuilder.toString()
    }

    fun getTranslation(index: Int): Translation {
        return translations!![if (index < translations!!.size) index else 0]
    }
}