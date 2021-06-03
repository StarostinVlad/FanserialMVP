package com.starostinvlad.fan.VideoScreen.PlayerModel;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SerialPlayer {
    private final String TAG = getClass().getSimpleName();
    private String referer = "https://seriahd.tv";
    private String hash;

    private String DOMAIN = "";
    private OkHttpClient CLIENT;
    private String playerHost = "";
    private String title = "";

    public List<String> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<String> infoList) {
        this.infoList = infoList;
    }

    private List<String> infoList = new ArrayList<>();

    public List<String> getReleaseDates() {
        return releaseDates;
    }

    public void setReleaseDates(List<String> releaseDates) {
        this.releaseDates = releaseDates;
    }

    private List<String> releaseDates = new ArrayList<>();


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    private boolean subscribed;

    public SerialPlayer(String DOMAIN, OkHttpClient okHttpClient) {
        this.DOMAIN = DOMAIN;
        this.CLIENT = okHttpClient;
    }

    public Maybe<Hls> getHlsObject(final String url) {
        return Maybe.create(emitter -> {
            try {
                Document document = loadHlsFromUrl(url);
                Log.d(TAG, "getHlsObject: url: " + url);
                Log.d(TAG, "getHlsObject: doc: " + document);
                if (url.contains("responce.php")) {
                    emitter.onSuccess(parseHlsFromSecondPlayerPage(document));
                } else {
                    emitter.onSuccess(parseHlsFromDefualtPlayerPage(document));
                }
            } catch (Exception e) {
                emitter.onError(e);
            } finally {
                emitter.onComplete();
            }
        });
    }

    private Document loadHlsFromUrl(String url) throws IOException {
        Log.d(TAG, "getHlsUrl: iframe: " + url);
        Request getSeriaPage = new Request.Builder()
                .addHeader("referer", referer)
                .url(url)
                .get()
                .build();
        Response response = CLIENT.newCall(getSeriaPage).execute();

        if (response.code() == 200 & response.body() != null) {
            return Jsoup.parse(response.body().string());
        }
        throw new NullPointerException();
    }

    private Hls parseHlsFromSecondPlayerPage(Document document) {
        String playerJson = document.text();
        JsonObject jsonObject = JsonParser.parseString(playerJson).getAsJsonObject();
        String src = jsonObject.get("src").getAsString();
        String enSub = jsonObject.get("en_sub").getAsString();
        String ruSub = jsonObject.get("ru_sub").getAsString();
        return new Hls(src, enSub, ruSub);
    }

    private Hls parseHlsFromDefualtPlayerPage(Document document) {
        Element playerElement = document.selectFirst(".flowplayer");
        String playerJson = playerElement.attr("data-config");
        String src = JsonParser.parseString(playerJson).getAsJsonObject().get("hls").getAsString();
        String enSub = playerElement.attr("data-ru_subtitle");
        String ruSub = playerElement.attr("data-en_subtitle");
        return new Hls(src, enSub, ruSub);
    }

    private Document loadSerialPageFromUrl(String url) throws IOException {
        String currentUrl = url.contains(DOMAIN) ? url : DOMAIN + url;
        referer = currentUrl;
        Request getSeriaPage = new Request.Builder()
                .addHeader("referer", currentUrl)
                .url(currentUrl)
                .get()
                .build();
        Response response = CLIENT.newCall(getSeriaPage).execute();
        if (response.code() == 200 & response.body() != null) {
            return Jsoup.parse(
                    response
                            .body()
                            .string()
            );
        }
        return null;
    }

    public Observable<Serial> getSerial(String url) {
        return Observable.create(emitter -> {
            try {
                Document document = loadSerialPageFromUrl(url);
                hash = document.getElementsByAttributeValue("name", "user_hash").attr("value");

                title = document.getElementsByAttributeValue("property", "og:title").attr("content");

                Log.d(TAG, "title: " + title);

                List<TextNode> descriptionNodes = document.selectFirst("div.fdesc.full-text.clearfix").textNodes();
                for (TextNode descriptionNode : descriptionNodes.subList(2, descriptionNodes.size())) {
                    description += descriptionNode.text();
                }
                Log.d(TAG, "description: " + description);
                Elements serialInfoElements = document.select("div.sd-line");
                for (Element serialInfoElement : serialInfoElements) {
                    infoList.add(serialInfoElement.text());
                }
                Log.d(TAG, "serialInfo: " + infoList.toString());

                Elements releaseDateElements = document.select("tr.epscape_tr");
                for (Element date : releaseDateElements) {
                    releaseDates.add(date.text());
                }
                Log.d(TAG, "releaseDates: " + releaseDates.toString());

                subscribed = document.select(".fa.fa-star").hasClass("fav-added");
//                \"scode_begin\":\"([^}]*?dew.*?)\"}
                Pattern pattern = Pattern.compile("\"scode_begin\":\"([^}]*?dew.*?)\"");

                Matcher matcher = pattern.matcher(document.html());
                Serial serial;
                if (matcher.find()) {
                    String result = matcher.group(1);
                    result = result.replace("ifr:", "http").replace("\\", "");
                    Log.d(TAG, "getSerial: EXIST SECOND PLAYER! " + result);
                    serial = parseSerialFromSecondPlayer(getSecondPlayerPageFromUrl(result));
                    emitter.onNext(serial);
                } else {
                    serial = parseSerialFromDefautPlayer(document);
                    emitter.onNext(serial);
//                    throw new Exception("Данный сериал не поддерживается в приложении!");
                }
            } catch (Exception e) {
                emitter.onError(e);
            } finally {
                emitter.onComplete();
            }
        });
    }
    // TODO: 12.04.2021 разделить плеер и страницу сериала на две модели
    // TODO: 12.04.2021 оформить правильный нейминг 
    // TODO: 12.04.2021 собрать все файлы для работы с сайтом в отдельный пакет бизнес логики
    // TODO: 12.04.2021 попробовать переоформить структуру сериала(сезон->серия->озвучка)

    private Document getSecondPlayerPageFromUrl(String url) throws IOException {
        Request getSeriaPage = new Request.Builder()
                .addHeader("referer", referer)
                .url(url)
                .get()
                .build();
        Response response = CLIENT.newCall(getSeriaPage).execute();
        playerHost = Uri.parse(url).getHost();
        Document document = null;
        if (response.code() == 200 & response.body() != null) {
            document = Jsoup.parse(response.body().string());
        }
        return document;
    }

    private Serial parseSerialFromSecondPlayer(Document document) {
        Element inputData = document.selectFirst("#inputData");
        Serial serial = null;
        if (inputData != null && !inputData.text().isEmpty()) {
//                Log.d(TAG, "getHlsUrl: inputData: " + inputData.text());
            int id = Integer.parseInt(inputData.attr("data-playlist"));
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .setLenient()
                    .registerTypeAdapter(Serial.class, new SecondPlayerDataDeserializer(id, playerHost))
                    .create();
            serial = gson.fromJson(inputData.text(), Serial.class);
        }
        return serial;
    }

    private Serial parseSerialFromDefautPlayer(Document document) {
        String playerJson = document.select("script:nth-child(3)").html();
        Pattern pattern = Pattern.compile("init\\((.*?)\\);");

        Matcher matcher = pattern.matcher(playerJson);
        Serial serial = null;
        if (matcher.find()) {
            String result = matcher.group(1);
            result = "[" + result + "]";

            String path = Environment.getExternalStorageDirectory() + "/player.json";
            Log.d(TAG, "parseSerialFromDefautPlayer: path: " + path);
            try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
                byte[] arr = result.getBytes();
                fileOutputStream.write(arr);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.d(TAG, "deserialize: file writed!");
            }

            Log.d(TAG, "getPlayerList: " + result.substring(result.length() - 50));
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .setLenient()
                    .registerTypeAdapter(Serial.class, new PlayerDataDeserializer())
                    .create();
            serial = gson.fromJson(result, Serial.class);
            Log.d(TAG, "getSerial: serial: " + serial.toString());
        }
        return serial;
    }

    public Maybe<String> subscribeRequest(String id, boolean check) {
        return Maybe.create(emitter -> {
            String action = check ? "plus" : "minus";
            Request request = new Request
                    .Builder()
                    .url(DOMAIN + "/engine/ajax/controller.php?mod=favorites&fav_id=" + id + "&action=" + action + "&skin=seriahd&alert=1&user_hash=" + hash)
                    .get()
                    .build();
            try {
                Response response = CLIENT.newCall(request).execute();
                if (response.code() == 200 & response.body() != null) {
                    Document doc = Jsoup.parse(response.body().string());
                    emitter.onSuccess(doc.body().html());
                }
            } catch (IOException e) {
                emitter.onError(e);
            } finally {
                emitter.onComplete();
            }
        });
    }
}
