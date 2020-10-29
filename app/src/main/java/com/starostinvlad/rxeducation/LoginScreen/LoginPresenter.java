package com.starostinvlad.rxeducation.LoginScreen;

import android.util.Log;

import com.starostinvlad.rxeducation.Api.NetworkService;
import com.starostinvlad.rxeducation.GsonModels.Token;
import com.starostinvlad.rxeducation.Utils;

import java.io.IOException;

import rx.android.schedulers.AndroidSchedulers;

class LoginPresenter {
    private final String TAG = getClass().getSimpleName();
    LoginFragmentContract view;

    LoginPresenter(LoginFragmentContract view) {
        this.view = view;
    }

    void login(String email, String pass) {
        view.showLoading(true);
        NetworkService
                .getInstance()
                .getApi()
                .getToken(email, pass)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccessful()) {
                                Token token = response.body();
                                Utils.TOKEN = token.getToken();
                                Utils.AUTH = true;
                                Utils.AUTH_subject.onNext(true);
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
                );
    }
}
