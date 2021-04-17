package com.starostinvlad.fan.VideoScreen.PlayerModel;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

public class PlayerDataDeserializer implements JsonDeserializer<Serial> {
    private final String TAG = getClass().getSimpleName();

    // TODO: 12.04.2021 распределить работу на методы

    @Override
    public Serial deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d(TAG, "deserialize: start");
        Serial serial = new Serial();
        ArrayList<Translation> translations = new ArrayList<>();
        JsonObject jsonObject = json.getAsJsonArray().get(0).getAsJsonObject();
        Log.d(TAG, "deserialize: " + jsonObject.keySet().toString());
        for (Entry<String, JsonElement> jsonTranslation : jsonObject.entrySet()) {

            Log.d(TAG, "deserialize: " + jsonTranslation.getKey());

            JsonObject translationObject = jsonTranslation.getValue().getAsJsonObject();

            Translation translation = new Translation();
            String translationTitle = translationObject.get("name").getAsString();
            Log.d(TAG, "deserialize: озвучка : " + translationTitle);
            Integer translationId = translationObject.get("id").getAsInt();
            Integer season = translationObject.get("ssort").getAsInt();

            ArrayList<Episode> episodes = new ArrayList<>();

            for (Entry<String, JsonElement> episodeTranslation : translationObject.get("items").getAsJsonObject().entrySet()) {
                Episode episode = new Episode();
                JsonObject episodeObject = episodeTranslation.getValue().getAsJsonObject();

                Integer episodeId = episodeObject.get("id").getAsInt();
                Integer parent = episodeObject.get("parent").getAsInt();
                String episodeTitle = episodeObject.get("sname").getAsString();
                Integer number = episodeObject.get("lssort").getAsInt();
                Integer type = episodeObject.get("codetype").getAsInt();
                String url = episodeObject.get("scode_begin").getAsString().replace("ifr::", "https:");

                episode.setId(episodeId);
                episode.setParent(parent);
                episode.setTitle(episodeTitle);
                episode.setNumber(number);
                episode.setType(type);
                episode.setUrl(url);
                Log.d(TAG, "deserialize: эпизод: " + episodeId + " " + episodeTitle);
                episodes.add(episode);
            }
            translation.setTitle(translationTitle);
            translation.setId(translationId);
            translation.setSeason(season);
            translation.setEpisodes(episodes);
            translations.add(translation);
        }
        serial.setTranslations(translations);
        Log.d(TAG, "deserialize: end! tsize: " + translations.size());
        return serial;
    }
}