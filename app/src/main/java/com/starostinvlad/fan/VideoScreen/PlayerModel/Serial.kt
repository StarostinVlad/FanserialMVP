package com.starostinvlad.fan.VideoScreen.PlayerModel

import java.io.Serializable
import kotlin.Throws

class Serial : Serializable {
    var id: String? = null
    var seasonList: List<Season>? = null
    var currentSeasonIndex = 0
    var currentEpisodeIndex = 0
    var currentTranslationIndex = 0
    override fun toString(): String {
        val str = StringBuilder()
        str.append("serial : [")
        for (season in seasonList!!) {
            str.append(season.toString()).append(", ")
        }
        str.append("]")
        return str.toString()
    }

    val currentSeason: Season
        get() = seasonList!![currentSeasonIndex]
}