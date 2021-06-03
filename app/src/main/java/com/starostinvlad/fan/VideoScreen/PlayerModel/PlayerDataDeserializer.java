package com.starostinvlad.fan.VideoScreen.PlayerModel;

import android.os.Environment;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class PlayerDataDeserializer implements JsonDeserializer<Serial> {
    private final String TAG = getClass().getSimpleName();
    HashMap<Integer, List<Translation>> episodesMap;

    // TODO: 12.04.2021 распределить работу на методы

    @Override
    public Serial deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d(TAG, "deserialize: start");
        episodesMap = new HashMap<>();
        Serial serial = new Serial();

        JsonObject jsonObject = json.getAsJsonArray().get(0).getAsJsonObject();
        Log.d(TAG, "deserialize: " + jsonObject.keySet().toString());

        Season season = new Season();
        season.setNumber(1);
        List<Episode> episodes = new ArrayList<>();

        for (Entry<String, JsonElement> jsonTranslation : jsonObject.entrySet()) {

            Log.d(TAG, "deserialize: " + jsonTranslation.getKey());

            JsonObject translationObject = jsonTranslation.getValue().getAsJsonObject();

            Translation translation = new Translation();
            String translationTitle = translationObject.get("name").getAsString();
            Log.d(TAG, "deserialize: озвучка : " + translationTitle);
            Integer translationId = translationObject.get("id").getAsInt();

            translation.setTitle(translationTitle);
            translation.setId(translationId);

            for (Entry<String, JsonElement> episodeTranslation : translationObject.get("items").getAsJsonObject().entrySet()) {
                JsonObject episodeObject = episodeTranslation.getValue().getAsJsonObject();
                Integer number = episodeObject.get("lssort").getAsInt();
                String url = episodeObject.get("scode_begin").getAsString().replace("ifr::", "https:");

                translation.setUrl(url);


                if (episodesMap.containsKey(number)) {
                    if (!episodesMap.get(number).isEmpty())
                        episodesMap.get(number).add(translation);
                } else {
                    ArrayList<Translation> translations = new ArrayList<>();
                    translations.add(translation);
                    episodesMap.put(number, translations);
                }
            }
        }
        Log.d(TAG, "deserialize: map: " + episodesMap);
        for (Entry<Integer, List<Translation>> entry : episodesMap.entrySet()) {
            Episode episode = new Episode();
            episode.setTranslations(entry.getValue());
            episode.setNumber(entry.getKey());
            episode.setTitle(entry.getKey() + " серия");
            episodes.add(episode);
        }
        season.setEpisodes(episodes);
        serial.setSeasonList(Collections.singletonList(season));
//        serial.setTranslations(translations);
        Log.d(TAG, "deserialize: serial: " + serial.toString());
//        Log.d(TAG, "deserialize: end! tsize: " + translations.size());
        return serial;
    }
}