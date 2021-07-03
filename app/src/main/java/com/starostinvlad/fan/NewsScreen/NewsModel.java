package com.starostinvlad.fan.NewsScreen;

import android.util.Log;

import com.starostinvlad.fan.App;
import com.starostinvlad.fan.GsonModels.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsModel {

    private final String TAG = getClass().getSimpleName();
    private List<News> episodeList;
    private int page = 2;
    private OkHttpClient CLIENT = App.getInstance().getOkHttpClient();

    Maybe<List<News>> addNews() {
        return loadNewsList(App.getInstance().getDomain() + "/page/" + page+"/")
                .doOnSuccess(val -> {
                    page++;
                    episodeList.addAll(val);
                });
    }

    Maybe<List<News>> loadNews() {
        return
                loadNewsList(App.getInstance().getDomain())
                        .doOnSuccess(val -> episodeList = val);
    }

    private Maybe<List<News>> loadNewsList(String url) {
        Log.d(TAG, "loadNewsList: url: " + url);
        return Maybe.create(emitter -> {
            try {
                Request newsPageRequest = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                Response response = CLIENT.newCall(newsPageRequest).execute();

                if (response.code() == 200 && response.body() != null && !emitter.isDisposed())
                    emitter.onSuccess(
                            parseDocumentToNews(response.body().string())
                    );
            } catch (Exception e) {
                e.printStackTrace();
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public Maybe<List<News>> search(String query) {
        return Maybe.create(emitter -> {
            try {
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
                Response response = CLIENT.newCall(newsPageRequest).execute();
                if (response.code() == 200 && response.body() != null && !emitter.isDisposed())
                    emitter.onSuccess(
                            parseDocumentToNews(response.body().string())
                    );
            } catch (Exception e) {
                e.printStackTrace();
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    private List<News> parseDocumentToNews(String responseBody) {
        Document document = Jsoup.parse(responseBody);
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
                Response response = CLIENT.newCall(favoritesPageRequest).execute();
                if (response.code() == 200 && response.body() != null && !emitter.isDisposed())
                    emitter.onSuccess(parseDocumentToNews(response.body().string()));
            } catch (Exception e) {
                e.printStackTrace();
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }
}
