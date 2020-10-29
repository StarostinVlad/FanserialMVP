package com.starostinvlad.rxeducation.SerialScreen;

import android.util.Log;

import com.starostinvlad.rxeducation.GsonModels.Episode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.starostinvlad.rxeducation.Utils.CLIENT;
import static com.starostinvlad.rxeducation.Utils.DOMAIN;

class SerialActivityPresenter {
    private final String TAG = getClass().getSimpleName();
    SerialActivityContract view;
    private List<String> seasonUrlList;

    SerialActivityPresenter(SerialActivityContract view) {
        this.view = view;
    }

    void loadData(String url) {
        if (seasonUrlList == null)
            Observable.fromCallable(() -> loadPage(url))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
//                .flatMapIterable(val->val)
//                .map(this::getseasonEpisodeList)
                    .subscribe(this::fillSeasonList);
        else
            view.fillList(seasonUrlList);
    }

    private void fillSeasonList(List<String> strings) {
        for (String string : strings) {
            Log.d(TAG, "season: " + string);
        }
        seasonUrlList = strings;
        view.fillList(strings);
    }

    private List<String> loadPage(String url) {
        Request getSeriaPage = new Request.Builder().url(url).get().build();
        try {
            Response response = CLIENT.newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {
                String cookies = response.header("Set-Cookie");
                Document doc = Jsoup.parse(response.body().string());
                Elements elements = doc.select("body > div.wrapper > main > div > div.row > div > div > div > div.page-header > div.box-serial-seasons > div > ul > li > a");
                List<String> seasons = null;
                if (elements.isEmpty()) {
                    seasons = new ArrayList<>();
                    seasons.add(url);
                    return seasons;
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        seasons = elements.stream().map(val -> DOMAIN + val.attr("href")).collect(Collectors.toList());
                    }
                }
                return seasons;

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error: " + e.getMessage());
        }
        return null;
    }
}
