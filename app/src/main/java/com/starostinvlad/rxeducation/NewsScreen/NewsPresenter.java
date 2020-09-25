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

    void updateNews() {
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
                            },
                            (Throwable::printStackTrace))
            ;
        } else {
            view.fillListView(newsModel.getDatumList());
        }
        view.showLoading(false);
    }

    void addNews() {
        if (!loading) {
            loading = true;
            view.showLoading(true);
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
                                loading = false;
                                Log.d(TAG, "offset: " + (20 * page));
                                page++;
                            },
                            (Throwable::printStackTrace));
            view.showLoading(false);
        }
    }
}
