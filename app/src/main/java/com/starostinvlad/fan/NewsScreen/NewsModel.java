package com.starostinvlad.fan.NewsScreen;

import android.util.Log;

import com.starostinvlad.fan.App;
import com.starostinvlad.fan.DatumDao;
import com.starostinvlad.fan.GsonModels.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsModel {

    private final String TAG = getClass().getSimpleName();
    private List<News> episodeList;
    private int page = 1;

    Maybe<List<News>> addNews() {
        return Maybe.create(emitter -> {
            try {
                page++;
                episodeList.addAll(
                        loadNewsList(App.getInstance().getDomain() + "/page/" + page)
                );
                emitter.onSuccess(episodeList);
            } catch (Exception e) {
                emitter.onError(e);
            }
        });

    }

    Maybe<List<News>> loadNews(){
        return Maybe.create(emitter -> {
            try {
                episodeList = loadNewsList(App.getInstance().getDomain());
                emitter.onSuccess(episodeList);
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    private List<News> loadNewsList(String url) throws IOException {
        Log.d(TAG, "loadNewsList: url: " + url);
        Request newsPageRequest = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = App.getInstance().getOkHttpClient().newCall(newsPageRequest).execute();

        if (response.code() == 200 & response.body() != null) {
            return parseDocumentToNews(response);
        }
        return Collections.emptyList();
    }

    public List<News> search(String query) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("do", "search")
                .addFormDataPart("subaction", "search")
                .addFormDataPart("story", query)
                .build();
        Request newsPageRequest = new Request.Builder()
                .url(App.getInstance().getDomain() + "/index.php?do=search")
                .post(requestBody)
                .build();
        Response response = App.getInstance().getOkHttpClient().newCall(newsPageRequest).execute();
        return parseDocumentToNews(response);
    }

    private List<News> parseDocumentToNews(Response response) throws IOException {
        if (response.code() == 200 & response.body() != null) {
            Document document = Jsoup.parse(response.body().string());
            Elements elements = document.select("#dle-content > div.short.clearfix.with-mask");
            List<News> newsList = new ArrayList<>();
            for (Element element : elements) {
                String title = element.select("a.short-title").text();
                Elements subTitleElements = element.select("div.short-text > div.short-desc > div.sd-line2");
                String subTitle = subTitleElements.size() == 4 ? subTitleElements.get(1).text() : subTitleElements.get(2).text();
                String image = App.getInstance().getDomain() + element.select("div.short-img.img-box > img").attr("src");
                String href = element.select("div.short-text > a").attr("href");
                String siteId = href.substring(href.lastIndexOf("/"));
                siteId = siteId.substring(1, siteId.indexOf("-"));
                News news = new News(title, subTitle, image, href, siteId);
                newsList.add(news);
//            Log.d(TAG, "loadpage: " + element.text());
            }
            return newsList;
        }
        return Collections.emptyList();
    }

    List<News> getEpisodeList() {
        if (episodeList == null) {
            episodeList = new ArrayList<>();
        }

        return episodeList;
    }

    void setEpisodeList(List<News> episodeList) {
        this.episodeList = episodeList;
    }

    public Maybe<List<News>> getFavorites() {
        return Maybe.create(emitter -> {
            try {
                Request favoritesPageRequest = new Request.Builder()
                        .url(App.getInstance().getDomain() + "/favorites")
                        .get()
                        .build();
                Response response = App.getInstance().getOkHttpClient().newCall(favoritesPageRequest).execute();
                emitter.onSuccess(parseDocumentToNews(response));
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}
