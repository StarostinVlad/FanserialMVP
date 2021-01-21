package com.starostinvlad.fan.SearchScreen;

import android.util.Log;

import com.starostinvlad.fan.App;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.NewsScreen.NewsModel;
import com.starostinvlad.fan.SearchedDao;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


class SearchFragmentPresenter {
    private final String TAG = getClass().getSimpleName();
    private final SearchFragmentContract view;
    private final NewsModel newsModel;
    private SearchedDao searchedDao = App.getInstance().getDatabase().searchedDao();
    private boolean loading;

    SearchFragmentPresenter(SearchFragmentContract view) {
        this.view = view;
        newsModel = new NewsModel();
    }

    void searchQuery(String query) {
        if (!loading) {
            loading = true;
            view.showLoading(true);
            Observable.fromCallable(() -> newsModel.search(query))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            arr -> {
                                if (arr.isEmpty()) {
                                    view.showMessage("По запросу \"" + query + "\" ничего не найдено");
                                } else {
                                    view.fillList(arr);
                                }
                                loading = false;
                                view.showLoading(false);
                            },
                            throwable -> {
                                throwable.printStackTrace();
                                loading = false;
                                view.showLoading(false);
                            }
                    ).isDisposed();
        }
    }

    void getHistory() {
        Log.d(TAG, "get History");
        searchedDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        val -> {
                            Log.d(TAG, "size: " + val.size());
                            view.fillList(val);
                        },
                        Throwable::printStackTrace
                ).isDisposed();
    }

    void addInHistory(News searched) {
        searchedDao
                .insert(searched)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        id -> Log.d(TAG, "id: " + id),
                        Throwable::printStackTrace
                ).isDisposed();
    }
}

