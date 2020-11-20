package com.starostinvlad.fan.VideoScreen;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.GsonModels.Episode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

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
    private ArrayList<Player> players;
    private ArrayList<String> arrayList;
    //    private String cookies;
    private String referer;
    private int currentVoice = 0;
    private ArrayList<Episode> episodes;

    VideoPresenter(VideoActivityContract view) {
        this.view = view;
    }

    private String getHlsUrl(Player player) {
        String url = player.getPlayer();
        Request getSeriaPage = new Request
                .Builder()
                .url(url)
//                .addHeader("Cookie", cookies)
                .addHeader("referer", referer)
                .get()
                .build();

        try {
            Response response = App.getInstance().getOkHttpClient().newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {
                Document doc = Jsoup.parse(response.body().string());
                String data_config = doc
                        .select("#content > div.flowplayer.fp-playful.fp-playful-new.is-splash.js-flowplayer.is-fan")
                        .attr("data-config");
                String hls = new JsonParser().parse(data_config).getAsJsonObject().get("hls").getAsString();
                Log.d(TAG, "data_config: " + referer + " : " + hls);
                return hls;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Observable<Player> getPlayerList(Document document) {
        String script = document.selectFirst("#players > script").data();
        script = script.substring(23, script.length() - 2);

        Gson gson = new Gson();

        Type playerListType = new TypeToken<ArrayList<Player>>() {
        }.getType();
        players = gson.fromJson(script, playerListType);
        Log.d(TAG, "player: " + players.get(0).getPlayer());
        return Observable.fromIterable(players);
    }

    private Document loadPage(String url) {
        url = url.contains("fanserial") ? url : App.getInstance().getDomain() + url;
        referer = url;
        Request getSeriaPage = new Request.Builder()
//                .addHeader("Cookie", App
//                        .getInstance()
//                        .getPreferences()
//                        .getCookie())
                .url(url)
                .get()
                .build();
        try {
            Response response = App.getInstance().getOkHttpClient().newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {
//                cookies = response.header("Set-Cookie");
                Document doc = Jsoup.parse(response.body().string());
                return doc;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getPageAttrs(Document document) {
        String title = document.selectFirst("title").text();
        title = title.substring(0, title.length() - 17);
        Log.d(TAG, "title: " + title);
        view.fillToolbar(title);
        boolean viewed = document.select("#viewedButton").hasClass("active");
        Log.d(TAG, "getPageAttrs viewed: " + viewed);
        view.checkViewed(viewed);
        //body > div.wrapper > main > div > div > div > div > div > div.page.page-serial-series.item-series > div > div > div.title-links-wrapper.clearfix > ul > li > a

        if (!document.select(".subscribe-link").isEmpty()) {
            Element element = document.select(".subscribe-link > li > a").first();
            Log.d(TAG, "getPageAttrs: " + element.outerHtml());
            boolean subscribed = element.hasClass("active");
            String id = element.attr("data-id");
            Log.d(TAG, "getPageAttrs subscribed: " + subscribed);
            view.checkSubscribed(id, subscribed);
        }
        if (!document.select("a.arrow.prev").isEmpty()) {
            String prevHref = document.select("a.arrow.prev").first().attr("href");
            String prevTitle = document.select("a.arrow.prev > span.arrow-label > span > span.number").first().text();
            Episode episode = new Episode();
            episode.setUrl(prevHref);
            episode.setName(prevTitle);
            view.prevBtn(episode);
        }
        if (!document.select("a.arrow.next").isEmpty()) {
            String nextHref = document.select("a.arrow.next").first().attr("href");
            String nextTitle = document.select("a.arrow.next > span.arrow-label > span > span.number").first().text();
            Episode episode = new Episode();
            episode.setUrl(nextHref);
            episode.setName(nextTitle);
            view.nextBtn(episode);
        }


    }

    void loadData(String url) {
        view.showLoading(true);
        arrayList = new ArrayList<>();
        Observable.fromCallable(() -> loadPage(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::getPageAttrs)
                .doOnNext(this::fillSeasonList)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(this::getPlayerList)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(str ->
                        {
                            view.showLoading(false);
                            onStart();
                        }
                        ,
                        (exception) -> {
                            exception.printStackTrace();
                            sendErrorMsg(exception.getMessage());
                        }).isDisposed();
    }

    void clearSeasonList() {
        episodes = null;
    }

    private void fillSeasonList(Document document) {
        if (episodes == null)
            getSeasonUrl(document)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .map(this::getSeasonEpisodeList)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::initRecycle, throwable -> {
                        sendErrorMsg(throwable.getMessage());
                        throwable.printStackTrace();
                    }).isDisposed();
    }

    private Observable<String> getSeasonUrl(Document document) {
        Elements elements = document.select("body > div.wrapper > main > div > div > div > div > section > ul > li > a");
        Log.d(TAG, "seasonHref: " + elements);
        int childNumber = elements.size() == 3 ? 2 : 1;
        String seasonHref = elements.get(childNumber).attr("href");
        seasonHref = App.getInstance().getDomain() + seasonHref;
        Log.d(TAG, "seasonHref: " + seasonHref);
        return Observable.just(seasonHref);
    }

    private ArrayList<Episode> getSeasonEpisodeList(String url) {
        Log.d(TAG, "url: " + url);
        Request getSeriaPage = new Request.Builder().url(url).get().build();
        try {
            Response response = App.getInstance().getOkHttpClient().newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {
//                cookies = response.header("Set-Cookie");
                Document doc = Jsoup.parse(response.body().string());
                Elements elements = doc.select("#episode_list > li > div > div > div.serial-bottom > div.field-description > a");
                episodes = new ArrayList<>();
                for (Element element : elements) {
                    Episode episode = new Episode();
                    episode.setName(element.text());
                    episode.setUrl(element.attr("href"));
                    episodes.add(episode);
                    Log.d(TAG, "episode: " + element.text());
                }
                Collections.reverse(episodes);
                return episodes;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error: " + e.getMessage());
        }
        return null;
    }

    void getVideo(int index) {
        this.currentVoice = index;
        Observable.fromCallable(() -> getHlsUrl(players.get(index)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::initPlayer, throwable -> {
                    sendErrorMsg(throwable.getMessage());
                    throwable.printStackTrace();
                }).isDisposed();
    }

    void onStart() {
        if (players != null && !players.isEmpty()) {
            getVideo(currentVoice);
        }
    }

    void buildDialog() {
        view.voiceSelectorDialog(players);
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
//        String cook = App.getInstance().getPreferences().getCookie();
//        Log.d(TAG, "cookies: " + cook);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("checked", String.valueOf(check ? 1 : 0))
                .build();
        Request getSeriaPage = new Request
                .Builder()
//                .addHeader("host", "fanserial.net")
//                .addHeader("cookie", cook)
                .url(App.getInstance().getDomain() + "/profile/viewed/" + id + "/")
                .post(requestBody)
                .build();
        Log.d(TAG, "viewedRequest: " + getSeriaPage.url());
        try {
            Response response = App.getInstance().getOkHttpClient().newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {

                Document doc = Jsoup.parse(response.body().string());
//                Log.d(TAG, "doc: " + doc.body());
                return doc.body().html();
            }
            return "code: " + response.code();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    private String subscribeRequest(String id, boolean check) {
//        String cook = App.getInstance().getPreferences().getCookie();
//        Log.d(TAG, "cookies: " + cook);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("checked", String.valueOf(check ? 1 : 0))
                .build();
        Request request = new Request
                .Builder()
//                .addHeader("host", "fanserial.net")
//                .addHeader("cookie", cook)
                .url(App.getInstance().getDomain() + "/profile/subscriptions/" + id + "/")
                .post(requestBody)
                .build();
        Log.d(TAG, "subscribeRequest: " + request.url());
        try {
            Response response = App.getInstance().getOkHttpClient().newCall(request).execute();
            if (response.code() == 200 & response.body() != null) {
                Log.d(TAG, "subscribeRequest:" + response.headers("Set-Cookie").toString());
                Document doc = Jsoup.parse(response.body().string());
//                Log.d(TAG, "doc: " + doc.body());
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
}
