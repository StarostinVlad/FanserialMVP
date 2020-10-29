package com.starostinvlad.rxeducation.VideoScreen;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.starostinvlad.rxeducation.GsonModels.Episode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.starostinvlad.rxeducation.Utils.CLIENT;
import static com.starostinvlad.rxeducation.Utils.DOMAIN;

class VideoPresenter {
    private static final String TAG = VideoPresenter.class.getSimpleName();
    private final VideoActivityContract view;
    private ArrayList<Player> players;
    private ArrayList<String> arrayList;
    private String cookies;
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
                .addHeader("Cookie", cookies)
                .addHeader("referer", referer)
                .get()
                .build();

        try {
            Response response = CLIENT.newCall(getSeriaPage).execute();
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
        return Observable.from(players);
    }

    private Document loadPage(String url) {
        url = url.contains("fanserial") ? url : DOMAIN + url;
        referer = url;
        Request getSeriaPage = new Request.Builder().url(url).get().build();
        try {
            Response response = CLIENT.newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {
                cookies = response.header("Set-Cookie");
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
    }

    void loadData(String url) {
        view.showLoading(true);
        arrayList = new ArrayList<>();
        Observable.fromCallable(() -> loadPage(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::getPageAttrs)
                .doOnNext(this::fillseasonList)
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
                            view.alarm(exception.getMessage());
                        });
    }

    private void fillseasonList(Document document) {
        if (episodes == null)
            getseasonUrl(document)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .map(this::getseasonEpisodeList)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::initRecycle);
    }

    private Observable<String> getseasonUrl(Document document) {
        Elements elements = document.select("body > div.wrapper > main > div > div > div > div > section > ul > li > a");
        Log.d(TAG, "seasonHref: " + elements);
        int childNumber = elements.size() == 3 ? 2 : 1;
        String seasonHref = elements.get(childNumber).attr("href");
        seasonHref = DOMAIN + seasonHref;
        Log.d(TAG, "seasonHref: " + seasonHref);
        return Observable.just(seasonHref);
    }

    private ArrayList<Episode> getseasonEpisodeList(String url) {
        Log.d(TAG, "url: " + url);
        Request getSeriaPage = new Request.Builder().url(url).get().build();
        try {
            Response response = CLIENT.newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {
                cookies = response.header("Set-Cookie");
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
                .subscribe(view::initPlayer, Throwable::printStackTrace);
    }

    void onStart() {
        if (players != null && !players.isEmpty()) {
            getVideo(currentVoice);
        }
    }

    void buildDialog() {
        view.voiceSelectorDialog(players);
    }

    public void setQuality(int which) {

    }
}
