package com.starostinvlad.fan.NewsScreen;

import com.starostinvlad.fan.BaseMVP.BasePresenter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class NewsPresenter extends BasePresenter {

    private final String TAG = getClass().getSimpleName();
    private NewsFragmentContract view;
    private NewsModel newsModel;
    private boolean loading = false;

    NewsPresenter(NewsFragmentContract view) {
        this.view = view;
    }

    void refreshNews() {
        newsModel = null;
        loadNews();
    }

    void addNews() {
        if (!loading) {
            startLoading();
            disposables.add(
                    newsModel.addNews()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((arr) -> {
                                        view.refreshListView();
                                        endLoading();
                                    }, (exception) -> {
                                        exception.printStackTrace();
                                        endLoading();
                                    }
                            )
            );
        }
    }

    void loadNews() {
        if (!loading) {
            startLoading();
            if (newsModel == null) {
                newsModel = new NewsModel();
                disposables.add(
                        newsModel.loadNews()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .retryWhen(throwableObservable -> throwableObservable.take(3).delay(1, TimeUnit.SECONDS))
                                .subscribe((arr) -> {
                                    newsModel.setEpisodeList(arr);
                                    view.fillListView(arr);
                                    endLoading();
                                }, (exception) -> {
                                    endLoading();
                                    if (exception.getMessage().contains("timeout"))
                                        view.alarm("Превышено время ожидания");
                                    exception.printStackTrace();
                                })
                );
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
