package com.starostinvlad.fan.VideoScreen.PlayerModel

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*
import kotlin.Throws

class SecondPlayerDataDeserializer internal constructor(private val playerId: Int, private val host: String?) : JsonDeserializer<Serial> {
    private val TAG: String = javaClass.getSimpleName()
    private var seasonNumber = 0

    // TODO: 12.04.2021 распределить работу на методы
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Serial {
//        Log.d(TAG, "deserialize2: start");
        val serial = Serial()
        val jsonObject = json.asJsonObject
        //        Log.d(TAG, "deserialize2: " + jsonObject.keySet().toString());
        val seasons: MutableList<Season> = ArrayList()
        for ((key, value) in jsonObject.entrySet()) {
            seasonNumber = key.toInt()
            val episodes: MutableList<Episode> = ArrayList()
            if (value.isJsonObject) {
                val seasonObject = value.asJsonObject
                for ((key1, value1) in seasonObject.entrySet()) {
                    val episodeNumber: Int = key1.toInt()
                    val episodeObject = value1.asJsonObject
                    episodes.add(
                            createEpisode(episodeNumber, episodeObject)
                    )
                }
            } else {
                val episodeJsonArray = value.asJsonArray
                var episodeNumber = 0
                //                Log.d(TAG, "deserialize: episode: " + episodeNumber);
                for (episodeJsonElement in episodeJsonArray) {
                    episodes.add(
                            createEpisode(episodeNumber, episodeJsonElement.asJsonObject)
                    )
                    episodeNumber++
                }
            }
            val season = Season()
            season.number = seasonNumber
            season.episodes = episodes
            seasons.add(season)
        }
        serial.seasonList = seasons
        //        Log.d(TAG, "deserialize: end! tsize: " + translations.size());
        return serial
    }

    @SuppressLint("DefaultLocale")
    private fun createEpisode(episodeNumber: Int, jsonObject: JsonObject): Episode {
        val episode = Episode()
        episode.number = episodeNumber
        episode.title = String.format(
                "%d сезон %s серия",
                seasonNumber,
                if (episodeNumber != 0) episodeNumber else "спецвыпуск"
        )
        episode.translations = parseTranslations(
                jsonObject,
                seasonNumber,
                episodeNumber
        )
        return episode
    }

    @SuppressLint("DefaultLocale")
    private fun parseTranslations(episodeObject: JsonObject, season: Int, episodeNumber: Int): MutableList<Translation> {
        val translations: MutableList<Translation> = ArrayList()
        Log.d(TAG, "parseTranslations: jsonOBject: $episodeObject")
        for ((key, value) in episodeObject.entrySet()) {
            val translation = Translation()
            val voiceString: Array<String> = key.split("#".toRegex()).toTypedArray()
            val translationCode: Int = voiceString[0].toInt()
            val voiceTitle = voiceString[1]
            val uniqueId = value.asInt
            //https://sorb.info/dew/3949?season=1&episode=1&voice=1&alloff=true
            //"https://%s/player/responce.php?id=%d&season=%d&episode=%d&voice=%d&uniqueid=%d",
            val url: String = String.format( //                    "https://%s/dew/%d?season=%d&episode=%d&voice=%d&alloff=true&uniqueid=%d",
                    "https://%s/player/responce.php?id=%d&season=%d&episode=%d&voice=%d&uniqueid=%d",
                    host,
                    playerId,
                    season,
                    episodeNumber,
                    translationCode,
                    uniqueId
            )
            translation.title = voiceTitle
            translation.id = uniqueId
            translation.code = translationCode
            translation.url = url
            translations.add(translation)
        }
        return translations
    }
}