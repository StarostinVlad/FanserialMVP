package com.starostinvlad.fan.VideoScreen.PlayerModel;

import android.net.Uri;
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
    private final Uri uri;
    private int playerId;

    public SecondPlayerDataDeserializer(int id, Uri uri) {
        this.playerId = id;
        this.uri = uri;
    }

    @Override
    public Serial deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d(TAG, "deserialize2: start");
        Serial serial = new Serial();
        ArrayList<Translation> translations = new ArrayList<>();
        JsonObject jsonObject = json.getAsJsonObject();
        Log.d(TAG, "deserialize2: " + jsonObject.keySet().toString());
        HashMap<String, ArrayList<Episode>> translationMap = new HashMap<>();
        for (Entry<String, JsonElement> jsonTranslation : jsonObject.entrySet()) {

            Integer season = Integer.parseInt(jsonTranslation.getKey());
            Log.d(TAG, "season: " + jsonTranslation.getKey());

            if (jsonTranslation.getValue().isJsonArray()) {
                JsonArray jsonArray = jsonTranslation.getValue().getAsJsonArray();
                int episodeNumber = 0;
                for (JsonElement jsonElement : jsonArray) {
                    for (Entry<String, JsonElement> elementEntry : jsonElement.getAsJsonObject().entrySet()) {

                        String[] voiceString = elementEntry.getKey().split("#");
                        Integer voiceID = Integer.parseInt(voiceString[0]);
                        String voiceTitle = voiceString[1];
                        String url = String.format(
                                uri.getScheme() + "://" + uri.getHost() + "/player/responce.php?id=%d&season=%d&episode=%d&voice=%d",
                                playerId,
                                season,
                                episodeNumber,
                                voiceID
                        );

                        Episode episode = new Episode();
                        episode.setNumber(episodeNumber);
                        episode.setTitle(season + "x" + episodeNumber);
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
                    episodeNumber++;
                }
            } else {

                for (Entry<String, JsonElement> episodeTranslation : jsonTranslation.getValue().getAsJsonObject().entrySet()) {
                    Log.d(TAG, "deserialize: episode: " + episodeTranslation.getKey());
                    Integer episodeNumber = Integer.parseInt(episodeTranslation.getKey());

                    JsonObject episodeObject = episodeTranslation.getValue().getAsJsonObject();
                    for (Entry<String, JsonElement> elementEntry : episodeObject.entrySet()) {

                        String[] voiceString = elementEntry.getKey().split("#");
                        Integer voiceID = Integer.parseInt(voiceString[0]);
                        String voiceTitle = voiceString[1];
                        String url = String.format(
                                "https://fplay.online/player/responce.php?id=%d&season=%d&episode=%d&voice=%d",
                                playerId,
                                season,
                                episodeNumber,
                                voiceID
                        );

                        Episode episode = new Episode();
                        episode.setNumber(episodeNumber);
                        episode.setTitle(season + "x" + episodeNumber);
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
}