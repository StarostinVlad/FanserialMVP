package com.starostinvlad.fan.ViewedScreen;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.BaseMVP.BasePresenter;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.NewsScreen.NewsModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;


class ViewedPresenter extends BasePresenter<ViewedFragmentContract> {
    private final String TAG = getClass().getSimpleName();
    private NewsModel newsModel;

    @Override
    public void attachView(ViewedFragmentContract mvpView) {
        super.attachView(mvpView);
        newsModel = new NewsModel();
    }

    void loadData() {
        view.showLoading(true);
        view.showButton(false);
        disposables.add(newsModel.getFavorites()
                .subscribeOn(Schedulers.io())
                .doAfterSuccess(this::updateSubcribtions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        val -> {
                            view.fillList(val);
                            view.showLoading(false);
                        },
                        e -> {
                            e.printStackTrace();
                            view.showLoading(false);
                            view.showButton(true);
                        }
                ));
    }

    private void updateSubcribtions(List<News> news) {
        disposables.add(Observable.just(news)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapIterable(val -> val)
                .doOnNext(val -> Log.d(TAG, "updateSubcribtions: " + FirebaseMessaging.getInstance().subscribeToTopic(val.getSiteId()).isSuccessful()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        val -> Log.d(TAG, "updateSubcribtions successed: " + val),
                        Throwable::printStackTrace
                ));
    }
}
