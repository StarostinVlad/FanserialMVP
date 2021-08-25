package com.starostinvlad.fan.VideoScreen.PlayerModel

import java.io.Serializable
import kotlin.Throws

class Season : Serializable {
    var number = 0
    var title: String = ""
    var episodes: List<Episode> = mutableListOf()
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("{").append(number).append(" : [")
        for (episode in episodes) {
            stringBuilder.append(episode.toString())
        }
        stringBuilder.append("]}")
        return stringBuilder.toString()
    }

    fun getEpisode(index: Int): Episode {
        return episodes[if (index < episodes.size) index else 0]
    }
}