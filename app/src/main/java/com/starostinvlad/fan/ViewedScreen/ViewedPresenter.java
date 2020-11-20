package com.starostinvlad.fan.ViewedScreen;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.starostinvlad.fan.Api.NetworkService;
import com.starostinvlad.fan.App;

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


class ViewedPresenter {
    private final String TAG = getClass().getSimpleName();
    private final ViewedFragmentContract view;

    ViewedPresenter(ViewedFragmentContract view) {
        this.view = view;
    }


    void loadData() {
        view.showLoading(true);
        view.showButton(false);
        NetworkService.getInstance()
                .getApi()
                .getViewed(App.getInstance().getTokenSubject().getValue())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
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
                ).isDisposed();
    }

    String getSubscribtions() {
        Request request = new Request
                .Builder()
                .addHeader("referer", App.getInstance().getDomain() + "/profile/subscriptions/")
                .addHeader("x-requested-with", "XMLHttpRequest")
                .url(App.getInstance().getDomain() + "/profile/subscriptions/")
                .get()
                .build();
        Log.d(TAG, "subscribeRequest: " + request.url());
        try {
            Response response = App.getInstance().getOkHttpClient().newCall(request).execute();
            if (response.code() == 200 & response.body() != null) {
                Log.d(TAG, "subscribeRequest:" + response.headers("Set-Cookie").toString());
                Document doc = Jsoup.parse(response.body().string());
//                Log.d(TAG, "doc: " + doc.body());
                return doc.body().html();
            }
            return String.valueOf(response.code());
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    void updateSubcribtions() {
        Observable.fromCallable(this::getSubscribtions)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(this::fromJson)
                .flatMapIterable(val -> val)
                .doOnNext(val -> Log.d(TAG, "updateSubcribtions: " + FirebaseMessaging.getInstance().subscribeToTopic(val).isSuccessful()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        val -> Log.d(TAG, "updateSubcribtions: " + val),
                        Throwable::printStackTrace
                ).isDisposed();
    }

    private List<String> fromJson(String jsonString) {
        JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("subscribes").getAsJsonArray();
        List<String> ids = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            Log.d(TAG, "fromJson: " + jsonElement.getAsString());
            ids.add(jsonElement.getAsString());
        }
        return ids;
    }
}
