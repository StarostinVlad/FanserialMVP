package com.starostinvlad.fan.VideoScreen.PlayerModel;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class SecondPlayerDataDeserializer implements JsonDeserializer<Serial> {
    private final String TAG = getClass().getSimpleName();
    private final String host;
    private int playerId;
    private int seasonNumber = 0;

    SecondPlayerDataDeserializer(int id, String host) {
        this.playerId = id;
        this.host = host;
    }

    // TODO: 12.04.2021 распределить работу на методы

    @Override
    public Serial deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        Log.d(TAG, "deserialize2: start");
        Serial serial = new Serial();
        JsonObject jsonObject = json.getAsJsonObject();
//        Log.d(TAG, "deserialize2: " + jsonObject.keySet().toString());
        List<Season> seasons = new ArrayList<>();
        for (Entry<String, JsonElement> jsonSeason : jsonObject.entrySet()) {
            seasonNumber = Integer.parseInt(jsonSeason.getKey());
            List<Episode> episodes = new ArrayList<>();
            if (jsonSeason.getValue().isJsonObject()) {
                JsonObject seasonObject = jsonSeason.getValue().getAsJsonObject();

                for (Entry<String, JsonElement> jsonEpisode : seasonObject.entrySet()) {
                    int episodeNumber = Integer.parseInt(jsonEpisode.getKey());
                    JsonObject episodeObject = jsonEpisode.getValue().getAsJsonObject();
                    episodes.add(
                            createEpisode(episodeNumber, episodeObject)
                    );
                }
            } else {
                JsonArray episodeJsonArray = jsonSeason.getValue().getAsJsonArray();
                int episodeNumber = 0;
//                Log.d(TAG, "deserialize: episode: " + episodeNumber);
                for (JsonElement episodeJsonElement : episodeJsonArray) {
                    episodes.add(
                            createEpisode(episodeNumber, episodeJsonElement.getAsJsonObject())
                    );
                    episodeNumber++;
                }
            }
            Season season = new Season();
            season.setNumber(seasonNumber);
            season.setEpisodes(episodes);

            seasons.add(season);
        }
        serial.setSeasonList(seasons);
//        Log.d(TAG, "deserialize: end! tsize: " + translations.size());
        return serial;
    }

    @SuppressLint("DefaultLocale")
    private Episode createEpisode(int episodeNumber, JsonObject jsonObject) {
        Episode episode = new Episode();
        episode.setNumber(episodeNumber);
        episode.setTitle(
                String
                        .format(
                                "%d сезон %s серия",
                                seasonNumber,
                                (episodeNumber != 0 ? episodeNumber : "спецвыпуск")
                        )
        );
        episode.setTranslations(
                parseTranslations(
                        jsonObject,
                        seasonNumber,
                        episodeNumber
                )
        );
        return episode;
    }

    @SuppressLint("DefaultLocale")
    private List<Translation> parseTranslations(JsonObject episodeObject, int season, int episodeNumber) {
        List<Translation> translations = new ArrayList<>();
        Log.d(TAG, "parseTranslations: jsonOBject: " + episodeObject.toString());
        for (Entry<String, JsonElement> elementEntry : episodeObject.entrySet()) {
            Translation translation = new Translation();

            String[] voiceString = elementEntry.getKey().split("#");
            int translationCode = Integer.parseInt(voiceString[0]);
            String voiceTitle = voiceString[1];
            int uniqueId = elementEntry.getValue().getAsInt();
            //https://sorb.info/dew/3949?season=1&episode=1&voice=1&alloff=true
            //"https://%s/player/responce.php?id=%d&season=%d&episode=%d&voice=%d&uniqueid=%d",
            String url = String.format(
//                    "https://%s/dew/%d?season=%d&episode=%d&voice=%d&alloff=true&uniqueid=%d",
                    "https://%s/player/responce.php?id=%d&season=%d&episode=%d&voice=%d&uniqueid=%d",
                    host,
                    playerId,
                    season,
                    episodeNumber,
                    translationCode,
                    uniqueId
            );
            translation.setTitle(voiceTitle);
            translation.setId(uniqueId);
            translation.setCode(translationCode);
            translation.setUrl(url);
            translations.add(translation);
        }
        return translations;
    }
}