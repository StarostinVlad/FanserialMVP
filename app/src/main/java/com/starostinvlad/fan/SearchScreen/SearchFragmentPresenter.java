package com.starostinvlad.fan.SearchScreen;

import android.util.Log;

import com.starostinvlad.fan.App;
import com.starostinvlad.fan.BaseMVP.BasePresenter;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.NewsScreen.NewsModel;
import com.starostinvlad.fan.SearchedDao;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


class SearchFragmentPresenter extends BasePresenter<SearchFragmentContract> {
    private final String TAG = getClass().getSimpleName();
    private NewsModel newsModel;
    private SearchedDao searchedDao = App.getInstance().getDatabase().searchedDao();
    private boolean loading;

    @Override
    public void attachView(SearchFragmentContract mvpView) {
        super.attachView(mvpView);
        newsModel = new NewsModel();
    }

    void searchQuery(String query) {
        if (!loading) {
            loading = true;
            view.showLoading(true);
            disposables.add(
                    newsModel.search(query)
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
                            )
            );
        }
    }

    void getHistory() {
        Log.d(TAG, "get History");
        disposables.add(
                searchedDao.getAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                val -> {
                                    Log.d(TAG, "size: " + val.size());
                                    view.fillList(val);
                                },
                                Throwable::printStackTrace
                        )
        );
    }

    void addInHistory(News searched) {
        disposables.add(
                searchedDao
                        .insert(searched)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                id -> Log.d(TAG, "id: " + id),
                                Throwable::printStackTrace
                        )
        );
    }
}

