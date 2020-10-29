package com.starostinvlad.rxeducation.NewsScreen;

import android.util.Log;

import com.starostinvlad.rxeducation.Api.NetworkService;
import com.starostinvlad.rxeducation.GsonModels.News;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class NewsPresenter {

    private final String TAG = getClass().getSimpleName();
    private NewsFragmentContract view;
    private int page = 1;
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
        Log.d(TAG, "offset: " + (20 * page) + " addnews");
        if (!loading) {
            Log.d(TAG, "offset: " + (20 * page) + " addnews loading");
            startLoading();
            NetworkService
                    .getInstance()
                    .getApi()
                    .addNews(20 * page)
                    .map(News::getData)
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
                    );
        }
    }

    void loadNews() {
//        AppDatabase db = App.getInstance().getDatabase();
//        DatumDao employeeDao = db.datumDao();

//        Observable.fromCallable(employeeDao::getAll)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe((array) -> {
//                    if (!array.isEmpty()) {
//                        newsModel.setEpisodeList(array);
//                        view.fillListView(array);
//                    }
//                });
        if (!loading) {
            startLoading();
            if (newsModel == null) {
                newsModel = new NewsModel();
                page = 1;
                NetworkService
                        .getInstance()
                        .getApi()
                        .getNews()
                        .map(News::getData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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
                        })
                ;
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
