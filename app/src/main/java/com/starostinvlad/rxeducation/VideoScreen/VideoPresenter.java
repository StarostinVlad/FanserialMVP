package com.starostinvlad.rxeducation.VideoScreen;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.starostinvlad.rxeducation.pojos.Datum;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.starostinvlad.rxeducation.Utils.CLIENT;

public class VideoPresenter {
    private static final String TAG = VideoPresenter.class.getSimpleName();
    private final VideoFragmentContract view;
    private ArrayList<Player> players;
    private ArrayList<String> arrayList;
    private String cookies;
    private String referer;
    private int currentVoice = 0;

    public VideoPresenter(VideoFragmentContract view) {
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
        view.fillSpiner(players);
        return Observable.from(players);
    }

    private void fillHlsList(String url) {
        arrayList.add(url);
        Log.d(TAG, "items: " + url);

//        view.showLoading(false);
    }

    private Document loadPage(String url) {
        url = url.contains("fanserial") ? url : "http://fanserial.net" + url;
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

    void getPageAttrs(Document document) {
        String title = document.selectFirst("title").text();
        title = title.substring(0, title.length() - 17);
        Log.d(TAG, "title: " + title);
        view.fillToolbar(title);
    }

    void loadData(Datum episode) {
        view.showLoading(true);
        arrayList = new ArrayList<>();
        Observable.fromCallable(() -> loadPage(episode.getEpisode().getUrl()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::getPageAttrs)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(this::getPlayerList)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(str -> view.showLoading(false),
                        Throwable::printStackTrace);
    }

    void getVideo(int index) {
        this.currentVoice = index;
        Observable.fromCallable(() -> getHlsUrl(players.get(index)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::initPlayer, Throwable::printStackTrace);
    }

    void getVideo() {
        getVideo(currentVoice);
    }
}
