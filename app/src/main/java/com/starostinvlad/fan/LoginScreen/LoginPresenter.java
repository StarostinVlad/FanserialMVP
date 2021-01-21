package com.starostinvlad.fan.LoginScreen;

import android.util.Log;

import com.starostinvlad.fan.App;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.reactivex.Observable;
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
        Observable.fromCallable(() -> login(email, pass))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        document -> {
                            String login = document.select("#login-box").attr("title");
                            if (!login.equals("Авторизация")) {
                                App.getInstance().getLoginSubject().onNext(login);
                                Log.d(TAG, "login: " + login);
                            } else {
                                String errors = document.selectFirst(".berrors").text();
                                Log.d(TAG, "loginApi: errors: " + errors);
                                view.alarm(errors);
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

    private Document login(String email, String pass) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("login_password", pass)
                .addFormDataPart("login_name", email)
                .addFormDataPart("login", "submit")
                .build();
        Request loginRequest = new Request
                .Builder()
                .url(App.getInstance().getDomain())
                .post(requestBody)
                .build();

        try {
            Response response = App.getInstance().getOkHttpClient().newCall(loginRequest).execute();
            if (response.code() == 200 & response.body() != null) {
                Document doc = Jsoup.parse(response.body().string());
                Log.d(TAG, "doc: " + doc.body());
                return doc;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void registryApi(String email, String pass, String name) {
        Observable.fromCallable(() -> registry(email, pass, name))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> {
                            if (!response.isEmpty()) {
                                if (response.contains("success"))
                                    loginApi(email, pass);
                                else {
                                    view.alarm(response);

                                    view.showLoading(false);
                                }
                            }
                        },
                        e -> {
                            Log.d(TAG, "message: " + e.toString());
                            view.showLoading(false);
                            view.alarm(e.getMessage());
                            e.printStackTrace();
                        }).isDisposed();
    }

    private String registry(String email, String pass, String name) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("password", pass)
                .addFormDataPart("email", email)
                .addFormDataPart("name", name)
                .build();
        Request getSeriaPage = new Request
                .Builder()
                .url(App.getInstance().getDomain() + "/registration/")
                .post(requestBody)
                .build();

        try {
            Response response = App.getInstance().getOkHttpClient().newCall(getSeriaPage).execute();
            Log.d(TAG, "doc: " + response.body());
            if (response.code() == 200 & response.body() != null) {
                Log.d(TAG, "doc: " + response.body());
                return response.body().string();
            }
            return response.message();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
