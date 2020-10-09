package com.starostinvlad.rxeducation.NewsScreen;

import android.util.Log;

import com.starostinvlad.rxeducation.NetworkService;
import com.starostinvlad.rxeducation.pojos.News;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewsPresenter {

    final String TAG = getClass().getSimpleName();
    NewsFragmentContract view;
    int page = 1;
    NewsModel newsModel;
    private boolean loading = false;

    public NewsPresenter(NewsFragmentContract view) {
        this.view = view;
    }

    void refreshNews() {
        newsModel = null;
        loadNews();
    }

    void addNews() {
        if (!loading) {
            startLoading();
            NetworkService
                    .getInstance()
                    .getJSONApi()
                    .addNews(20 * page)
                    .map(News::getData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((arr) -> {
                                newsModel.addToDatumList(arr);
                                view.addToListView(arr);
                                Log.d(TAG, "offset: " + (20 * page));
                                page++;
                                endLoading();
                            }, (exception) -> {
                                exception.printStackTrace();
                                endLoading();
                            }
                    );
        }
    }

    public void loadNews() {
        if (!loading) {
            startLoading();
            if (newsModel == null) {
                newsModel = new NewsModel();
                page = 1;
                NetworkService
                        .getInstance()
                        .getJSONApi()
                        .getNews()
                        .map(News::getData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((arr) -> {
                            newsModel.setDatumList(arr);
                            view.fillListView(arr);
                            endLoading();
                        }, (exception) -> {
                            endLoading();
                            if(exception.getMessage().contains("timeout"))
                                view.alarm("Превышено время ожидания");
                            exception.printStackTrace();
                        })
                ;
            } else {
                view.fillListView(newsModel.getDatumList());
                endLoading();
            }
        }
    }

    void startLoading() {
        loading = true;
        view.showLoading(true);
    }

    void endLoading() {
        loading = false;
        view.showLoading(false);
    }

}
