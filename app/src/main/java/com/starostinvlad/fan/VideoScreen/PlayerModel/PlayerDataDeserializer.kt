package com.starostinvlad.fan.VideoScreen.PlayerModel

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.util.*
import kotlin.Throws

class PlayerDataDeserializer : JsonDeserializer<Serial> {
    private val TAG: String = javaClass.getSimpleName()
    var episodesMap: HashMap<Int, MutableList<Translation>>? = null

    // TODO: 12.04.2021 распределить работу на методы
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Serial {
        Log.d(TAG, "deserialize: start")
        episodesMap = HashMap()
        val serial = Serial()
        val jsonObject = json.asJsonArray[0].asJsonObject
        Log.d(TAG, "deserialize: " + jsonObject.keySet().toString())
        val season = Season()
        season.number = 1
        val episodes: MutableList<Episode> = ArrayList()
        for ((key, value) in jsonObject.entrySet()) {
            Log.d(TAG, "deserialize: $key")
            val translationObject = value.asJsonObject
            val translation = Translation()
            val translationTitle = translationObject["name"].asString
            Log.d(TAG, "deserialize: озвучка : $translationTitle")
            val translationId = translationObject["id"].asInt
            translation.title = translationTitle
            translation.id = translationId
            for ((_, value1) in translationObject["items"].asJsonObject.entrySet()) {
                val episodeObject = value1.asJsonObject
                val number = episodeObject["lssort"].asInt
                val url: String = episodeObject["scode_begin"].asString.replace("ifr::", "https:")
                translation.url = url
                if (episodesMap!!.containsKey(number)) {
                    if (!episodesMap!![number]!!.isEmpty()) episodesMap!![number]!!.add(translation)
                } else {
                    val translations = ArrayList<Translation>()
                    translations.add(translation)
                    episodesMap!![number] = translations
                }
            }
        }
        Log.d(TAG, "deserialize: map: $episodesMap")
        for ((key, value) in episodesMap!!) {
            val episode = Episode()
            episode.translations = value
            episode.number = key
            episode.title = key.toString() + " серия"
            episodes.add(episode)
        }
        season.episodes = episodes
        serial.seasonList = listOf(season)
        //        serial.setTranslations(translations);
        Log.d(TAG, "deserialize: serial: $serial")
        //        Log.d(TAG, "deserialize: end! tsize: " + translations.size());
        return serial
    }
}