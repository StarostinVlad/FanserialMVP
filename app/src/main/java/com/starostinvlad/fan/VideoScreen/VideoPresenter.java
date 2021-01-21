package com.starostinvlad.fan.VideoScreen;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Episode;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Hls;
import com.starostinvlad.fan.VideoScreen.PlayerModel.PlayerDataDeserializer;
import com.starostinvlad.fan.VideoScreen.PlayerModel.SecondPlayerDataDeserializer;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class VideoPresenter {
    private static final String TAG = VideoPresenter.class.getSimpleName();
    private final VideoActivityContract view;
    //    private String cookies;
    private String referer;
    private Serial serial = null;
    private String hash;

    VideoPresenter(VideoActivityContract view) {
        this.view = view;
    }

    private Serial getSerialFromSecondPlayer(String url) throws IOException {
        Request getSeriaPage = new Request.Builder()
                .addHeader("referer", referer)
                .url(url)
                .get()
                .build();
        Response response = App.getInstance().getOkHttpClient().newCall(getSeriaPage).execute();

        if (response.code() == 200 & response.body() != null) {
            Document doc = Jsoup.parse(response.body().string());

            Element inputData = doc.selectFirst("#inputData");

            if (inputData != null && !inputData.text().isEmpty()) {
//                Log.d(TAG, "getHlsUrl: inputData: " + inputData.text());
                int id = Integer.parseInt(inputData.attr("data-playlist"));
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .setLenient()
                        .registerTypeAdapter(Serial.class, new SecondPlayerDataDeserializer(id))
                        .create();
                serial = gson.fromJson(inputData.text(), Serial.class);
                return serial;
            }
        }
        return null;
    }

    private Serial getSerial(Document doc) throws IOException {

        String playerJson = doc.select("script:nth-child(3)").html();
        Pattern pattern = Pattern.compile("init\\((.*?)\\);");

        Matcher matcher = pattern.matcher(playerJson);
        Serial serial = null;
        if (matcher.find()) {
            String result = matcher.group(1);
            result = "[" + result + "]";
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

    private Hls getHlsObject(String iframe) throws IOException {
        iframe = iframe.replace("ifr::", "https:");
        Log.d(TAG, "getHlsUrl: iframe: " + iframe);
        if (iframe.contains("youtube")) {
            view.openTrailer(iframe);
            return null;
        }
        if (iframe.contains("list")) {
            Log.e(TAG, "getSerial: url " + iframe);
            iframe = iframe.startsWith("https") ? iframe : "http:" + iframe;
            getSerialFromSecondPlayer(iframe);
        }
        Request getSeriaPage = new Request.Builder()
                .addHeader("referer", referer)
                .url(iframe)
                .get()
                .build();
        Response response = App.getInstance().getOkHttpClient().newCall(getSeriaPage).execute();

        if (response.code() == 200 & response.body() != null) {
            Document doc = Jsoup.parse(response.body().string());
            if (iframe.contains("responce.php")) {
                Log.d(TAG, "getHlsUrl: doc: " + doc);
                String playerJson = doc.text();
                JsonObject jsonObject = JsonParser.parseString(playerJson).getAsJsonObject();
                String src = jsonObject.get("src").getAsString();
                String enSub = jsonObject.get("en_sub").getAsString();
                String ruSub = jsonObject.get("ru_sub").getAsString();
                return new Hls(src, enSub, ruSub);
            } else {
                Log.d(TAG, "getHlsUrl: doc: " + doc);
                Element playerElement = doc.selectFirst(".flowplayer");
                String playerJson = playerElement.attr("data-config");
                String src = JsonParser.parseString(playerJson).getAsJsonObject().get("hls").getAsString();
                String enSub = playerElement.attr("data-ru_subtitle");
                String ruSub = playerElement.attr("data-en_subtitle");
                return new Hls(src, enSub, ruSub);
            }
        }
        return null;
    }

    private Document loadPage(String url) throws IOException {
        url = url.contains("hdseria.tv") ? url : App.getInstance().getDomain() + url;
        referer = url;
        Request getSeriaPage = new Request.Builder()
                .addHeader("referer", url)
                .url(url)
                .get()
                .build();
        Response response = App.getInstance().getOkHttpClient().newCall(getSeriaPage).execute();
        if (response.code() == 200 & response.body() != null) {
//                cookies = response.header("Set-Cookie");
            return Jsoup.parse(response.body().string());
        }
        return null;
    }

    private void getPageAttrs(Document document) {

        hash = document.getElementsByAttributeValue("name", "user_hash").attr("value");


        String title = document.getElementsByAttributeValue("property", "og:title").attr("content");
//        title = title.substring(0, title.length() - 17);
        Log.d(TAG, "title: " + title);
        view.fillToolbar(title);

        boolean subscribed = document.select(".fa.fa-star").hasClass("fav-added");
        view.checkSubscribed(subscribed);
    }

    void loadData(String url) {
        view.showLoading(true);
        Observable.fromCallable(() -> loadPage(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::getPageAttrs)
                .observeOn(Schedulers.io())
                .map(this::getSerial)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(serial ->
                        {
                            this.serial = serial;
                            view.translationSelectorDialog(serial.getTranslations());
                            view.showLoading(false);
                            onStart();
                        }
                        ,
                        (exception) -> {
                            exception.printStackTrace();
                            sendErrorMsg(exception.getMessage());
                        }).isDisposed();
    }

    private void getVideo(int index) {
        Log.d(TAG, "getVideo: " + serial.getCurrentEpisode());
        Observable.fromCallable(() -> getHlsObject(
                serial.getCurrentTranslation()
                        .getEpisodes()
                        .get(index)
                        .getUrl()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::initPlayer, throwable -> {
                    sendErrorMsg(throwable.getMessage());
                    throwable.printStackTrace();
                }).isDisposed();
    }


    void onStart() {
        if (serial != null) {
            if (!serial.getCurrentTranslation().getEpisodes().get(serial.getCurrentEpisode()).getType().equals(2))
                getVideo(serial.getCurrentEpisode());
        }
    }

    void buildDialog() {
        view.translationSelectorDialog(serial.getTranslations());
    }

    private void sendErrorMsg(String msg) {
        if (msg.contains("timeout"))
            msg = "Превышено время ожидания";
        else if (msg.contains("returned null"))
            msg = "Что-то пошло не так";
        else if (msg.contains("review"))
            msg = "Сериал недоступен в вашей стране";
        view.showDialog(msg);

    }

    void putToViewed(boolean check) {
        String id = referer.substring(22);
        id = id.substring(0, id.indexOf("-"));
        String finalId = id;
        Observable.fromCallable(() -> viewedRequest(finalId, check))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        val -> Log.d(TAG, "answer: " + val),
                        throwable -> {
                            sendErrorMsg(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                )
                .isDisposed();
    }

    private String viewedRequest(String id, boolean check) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("checked", String.valueOf(check ? 1 : 0))
                .build();
        Request getSeriaPage = new Request
                .Builder()
                .url(App.getInstance().getDomain() + "/profile/viewed/" + id + "/")
                .post(requestBody)
                .build();
        Log.d(TAG, "viewedRequest: " + getSeriaPage.url());
        try {
            Response response = App.getInstance().getOkHttpClient().newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {

                Document doc = Jsoup.parse(response.body().string());
                return doc.body().html();
            }
            return "code: " + response.code();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    private String subscribeRequest(String id, boolean check) {
        String action = check ? "plus" : "minus";
        Request request = new Request
                .Builder()
                .url(App.getInstance().getDomain() + "/engine/ajax/controller.php?mod=favorites&fav_id=" + id + "&action=" + action + "&skin=seriahd&alert=1&user_hash=" + hash)
                .get()
                .build();
        try {
            Response response = App.getInstance().getOkHttpClient().newCall(request).execute();
            if (response.code() == 200 & response.body() != null) {
                Document doc = Jsoup.parse(response.body().string());
                return doc.body().html();
            }
            return "code: " + response.code();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    void putToSubscribe(String id, boolean checked) {
        Observable.fromCallable(() -> subscribeRequest(id, checked))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        val -> Log.d(TAG, "answer: " + val),
                        throwable -> {
                            sendErrorMsg(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                )
                .isDisposed();
    }

    void setEpisode(int position) {
        if (position >= serial.getCurrentTranslation().getEpisodes().size())
            position = serial.getCurrentTranslation().getEpisodes().size() - 1;
        serial.setCurrentEpisode(position);
        Log.d(TAG, "getEpisode: " + serial.getCurrentEpisode());
        changeDescription();
        getVideo(position);
    }

    void setTranslation(int i) {
        serial.setCurrentTranslationIndex(i);
        view.initRecycle(serial.getTranslations().get(i));
        setEpisode(serial.getCurrentEpisode());
    }

    private void changeDescription() {
        view.changeDescription(
                serial.getCurrentTranslation().getTitle(),
                serial.getCurrentTranslation()
                        .getEpisodes()
                        .get(serial.getCurrentEpisode())
                        .getTitle()
        );
    }

}
