package com.starostinvlad.fan.NewsScreen;

import android.util.Log;

import com.starostinvlad.fan.Api.NetworkService;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.GsonModels.News;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class NewsPresenter {

    private final String TAG = getClass().getSimpleName();
    private NewsFragmentContract view;
    private int page = 1;
    private NewsModel newsModel;
    private boolean loading = false;
    private boolean subscriptions = false;

    NewsPresenter(NewsFragmentContract view) {
        this.view = view;
    }

    NewsPresenter(NewsFragmentContract view, boolean subscriptions) {
        this.view = view;
        this.subscriptions = subscriptions;
    }

    void refreshNews() {
        newsModel = null;
        loadNews();
    }

    void addNews() {
        Log.d(TAG, "offset: " + (20 * page) + " addnews");
        if (!loading) {
            Log.d(TAG, "offset: " + (20 * page) + " addnews loading");
            startLoading();
            Observable<News> var = subscriptions ?
                    NetworkService
                            .getInstance()
                            .getApi()
                            .addSubscritions(App.TOKEN_subject.getValue(), 20 * page)
                    : NetworkService
                    .getInstance()
                    .getApi()
                    .addNews(20 * page);

            var.map(News::getData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((arr) -> {
                                newsModel.addToDatumList(arr);
                                view.refreshListView();
                                Log.d(TAG, "offset: " + (20 * page));
                                page++;
                                endLoading();
                            }, (exception) -> {
                                exception.printStackTrace();
                                endLoading();
                            }
                    ).isDisposed();
        }
    }

    void loadNews2() {
        if (newsModel == null) {
            newsModel = new NewsModel();
            Log.d(TAG, "subscribe to list");
            newsModel.episodeListSub().subscribe(view::fillListView).isDisposed();
        }
    }

    void loadNews() {
        if (!loading) {
            startLoading();
            if (newsModel == null) {
                newsModel = new NewsModel();
                page = 1;
                Observable<News> var = subscriptions ?
                        NetworkService
                                .getInstance()
                                .getApi()
                                .getSubscriptions(App.TOKEN_subject.getValue())
                        :
                        NetworkService
                                .getInstance()
                                .getApi()
                                .getNews();

                var.map(News::getData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(throwableObservable -> throwableObservable.take(3).delay(1, TimeUnit.SECONDS))
                        .subscribe((arr) -> {
                            newsModel.setEpisodeList(arr);
//                            employeeDao.insert(arr);
                            view.fillListView(arr);
                            endLoading();
                        }, (exception) -> {
                            endLoading();
                            if (exception.getMessage().contains("timeout"))
                                view.alarm("Превышено время ожидания");
                            exception.printStackTrace();
                        }).isDisposed();
            } else {
                view.fillListView(newsModel.getEpisodeList());
                endLoading();
            }
        }
    }

    private void startLoading() {
        loading = true;
        view.showLoading(true);
    }

    private void endLoading() {
        loading = false;
        view.showLoading(false);
    }

}
