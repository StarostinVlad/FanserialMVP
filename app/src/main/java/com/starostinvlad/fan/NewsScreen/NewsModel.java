package com.starostinvlad.fan.NewsScreen;

import android.util.Log;

import com.starostinvlad.fan.Api.NetworkService;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.DatumDao;
import com.starostinvlad.fan.GsonModels.Datum;
import com.starostinvlad.fan.GsonModels.News;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

class NewsModel {

    private final String TAG = getClass().getSimpleName();
    DatumDao employeeDao = App.getInstance().getDatabase().datumDao();
    private List<Datum> episodeList;
    private PublishSubject<List<Datum>> episodeListSub = PublishSubject.create();

    PublishSubject<List<Datum>> episodeListSub() {
        Observable.concat(getEpisodeFromDb(), getEpisodeFromNet()).subscribe(episodeListSub::onNext);
        return episodeListSub;
    }

    List<Datum> getEpisodeList() {
        if (episodeList == null) {
            episodeList = new ArrayList<>();
        }

        return episodeList;
    }

    void setEpisodeList(List<Datum> episodeList) {
        this.episodeList = episodeList;
    }

    private Observable<List<Datum>> getEpisodeFromDb() {
        Log.d(TAG, "from db!!");
        return Observable.fromCallable(employeeDao::getAll).doOnNext(val -> Log.d(TAG, "size:" + val.size()));
    }

    private Observable<List<Datum>> getEpisodeFromNet() {
        Log.d(TAG, "from nettt!!");
        return NetworkService
                .getInstance()
                .getApi()
                .getNews()
                .map(News::getData)
                .observeOn(Schedulers.io())
                .doOnNext(val -> employeeDao.insert(val))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(throwableObservable -> throwableObservable.take(3).delay(1, TimeUnit.SECONDS));
    }

    void addToDatumList(List<Datum> arr) {
        if (episodeList == null)
            episodeList = arr;
        else
            episodeList.addAll(arr);
    }
}
