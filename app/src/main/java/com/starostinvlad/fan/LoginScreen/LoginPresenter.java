package com.starostinvlad.fan.LoginScreen;

import android.util.Log;

import com.starostinvlad.fan.Api.NetworkService;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.GsonModels.Token;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


class LoginPresenter {
    private final String TAG = getClass().getSimpleName();
    private LoginFragmentContract view;

    LoginPresenter(LoginFragmentContract view) {
        this.view = view;
    }

    void loginApi(String email, String pass) {
        view.showLoading(true);
        NetworkService
                .getInstance()
                .getApi()
                .getToken(email, pass)
                .observeOn(Schedulers.io())
                .doOnNext(val -> login(email, pass))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccessful()) {
                                Token token = response.body();
                                App.TOKEN_subject.onNext(token.getToken());
                                Log.d(TAG, "token: " + token.getToken());
                            } else {
                                try {
                                    if (response.errorBody() != null) {
                                        String error = response.errorBody().string();
                                        Log.d(TAG, "error: " + error);
                                        view.alarm(error);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            view.showLoading(false);
                        },
                        e -> {
                            Log.d(TAG, "message: " + e.toString());
                            view.showLoading(false);
                            view.alarm(e.getMessage());
                            e.printStackTrace();
                        }
                ).isDisposed();
    }

    private void login(String email, String pass) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("password", pass)
                .addFormDataPart("email", email)
                .build();
        Request getSeriaPage = new Request
                .Builder()
                .url(App.DOMAIN + "/authorization/")
                .post(requestBody)
                .build();

        try {
            Response response = App.CLIENT.newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {
                Document doc = Jsoup.parse(response.body().string());
                Log.d(TAG, "doc: " + doc.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
