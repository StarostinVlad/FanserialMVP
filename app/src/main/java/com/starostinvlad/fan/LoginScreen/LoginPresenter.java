package com.starostinvlad.fan.LoginScreen;

import android.util.Log;

import com.starostinvlad.fan.App;
import com.starostinvlad.fan.BaseMVP.BasePresenter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


class LoginPresenter extends BasePresenter<LoginFragmentContract> {
    private final String TAG = getClass().getSimpleName();

    void loginApi(String email, String pass) {
        view.showLoading(true);
        disposables.add(
                login(email, pass)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                document -> {
                                    String login = document.select("#login-box").attr("title");
                                    view.showLoading(false);
                                    if (!login.equals("Авторизация")) {
                                        App.getInstance().getLoginSubject().onNext(login);
                                        Log.d(TAG, "login: " + login);
                                    } else {
                                        String errors = document.selectFirst(".berrors").text();
                                        Log.d(TAG, "loginApi: errors: " + errors);
                                        view.alarm(errors);
                                    }
                                },
                                e -> {
                                    Log.d(TAG, "message: " + e.toString());
                                    view.showLoading(false);
                                    view.alarm(e.getMessage());
                                    e.printStackTrace();
                                }
                        )
        );
    }

    private Maybe<Document> login(String email, String pass) {
        return Maybe.create(emitter -> {
            try {
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


                Response response = App.getInstance().getOkHttpClient().newCall(loginRequest).execute();
                if (response.code() == 200 & response.body() != null) {
                    Document doc = Jsoup.parse(response.body().string());
                    Log.d(TAG, "doc: " + doc.body());
                    emitter.onSuccess(doc);
                }
            } catch (IOException e) {
                emitter.onError(e);
            }
        });
    }

    void registryApi(String email, String pass, String name) {
        disposables.add(
                registry(email, pass, name)
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
                                })
        );
    }

    private Maybe<String> registry(String email, String pass, String name) {
        return Maybe.create(emitter -> {
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
                    emitter.onSuccess(response.body().string());
                }
                emitter.onSuccess(response.message());
            } catch (IOException e) {
                emitter.onError(e);

            }
        });
    }
}
