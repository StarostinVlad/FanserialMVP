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
import java.util.Map.Entry;

public class SecondPlayerDataDeserializer implements JsonDeserializer<Serial> {
    private final String TAG = getClass().getSimpleName();
    private final String host;
    private int playerId;
    private HashMap<String, ArrayList<Episode>> translationMap;

    public SecondPlayerDataDeserializer(int id, String host) {
        this.playerId = id;
        this.host = host;
    }

    // TODO: 12.04.2021 распределить работу на методы

    @Override
    public Serial deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d(TAG, "deserialize2: start");
        Serial serial = new Serial();
        ArrayList<Translation> translations = new ArrayList<>();
        JsonObject jsonObject = json.getAsJsonObject();
        Log.d(TAG, "deserialize2: " + jsonObject.keySet().toString());
        translationMap = new HashMap<>();
        for (Entry<String, JsonElement> jsonTranslation : jsonObject.entrySet()) {

            Integer season = Integer.parseInt(jsonTranslation.getKey());
            Log.d(TAG, "season: " + jsonTranslation.getKey());

            if (jsonTranslation.getValue().isJsonObject()) {
                JsonObject translationObject = jsonTranslation.getValue().getAsJsonObject();

                for (Entry<String, JsonElement> episodeTranslation : translationObject.entrySet()) {
                    Log.d(TAG, "deserialize: episode: " + episodeTranslation.getKey());
                    int episodeNumber = Integer.parseInt(episodeTranslation.getKey());
                    JsonObject episodeObject = episodeTranslation.getValue().getAsJsonObject();
                    parseEpisode(episodeObject, season, episodeNumber);
                }
            } else {
                JsonArray translationArray = jsonTranslation.getValue().getAsJsonArray();
                int episodeNumber = 0;
                Log.d(TAG, "deserialize: episode: " + episodeNumber);
                for (JsonElement element : translationArray) {
                    parseEpisode(element.getAsJsonObject(), season, episodeNumber);
                    episodeNumber++;
                }
            }
        }
        for (Entry<String, ArrayList<Episode>> listEntry : translationMap.entrySet()) {
            Translation translation = new Translation();
            translation.setTitle(listEntry.getKey());
            translation.setEpisodes(listEntry.getValue());
            translations.add(translation);
        }
        Log.d(TAG, "deserialize: hash: " + translationMap);
        serial.setTranslations(translations);
        Log.d(TAG, "deserialize: end! tsize: " + translations.size());
        return serial;
    }

    @SuppressLint("DefaultLocale")
    void parseEpisode(JsonObject episodeObject, int season, int episodeNumber) {
        for (Entry<String, JsonElement> elementEntry : episodeObject.entrySet()) {

            String[] voiceString = elementEntry.getKey().split("#");
            Integer voiceID = Integer.parseInt(voiceString[0]);
            String voiceTitle = voiceString[1];

            Integer uniqueId = elementEntry.getValue().getAsInt();

            String url = String.format(
                    "https://%s/player/responce.php?id=%d&season=%d&episode=%d&voice=%d&uniqueid=%d",
                    host,
                    playerId,
                    season,
                    episodeNumber,
                    voiceID,
                    uniqueId
            );

            Episode episode = new Episode();
            episode.setNumber(episodeNumber);
            episode.setTitle(String.format("сезон: %d серия: %s", season, (episodeNumber != 0 ? episodeNumber : "спецвыпуск")));
            episode.setType(110);
            episode.setUrl(url);
            if (translationMap.containsKey(voiceTitle))
                translationMap.get(voiceTitle).add(episode);
            else {
                ArrayList<Episode> episodes = new ArrayList<>();
                episodes.add(episode);
                translationMap.put(voiceTitle, episodes);
            }
            Log.d(TAG, "deserialize: url: " + url + " " + voiceTitle);
        }
    }
}