package com.starostinvlad.fan.SplashScreen;

import android.util.Log;

import com.starostinvlad.fan.Api.SettingsNetworkService;
import com.starostinvlad.fan.App;

class SplashScreenPresenter {
    private final String TAG = getClass().getSimpleName();
    private SplashScreenContract view;

    SplashScreenPresenter(SplashScreenContract view) {
        this.view = view;
    }

    void loadSettings() {
        SettingsNetworkService
                .getInstance()
                .getApi()
                .getSettings()
                .subscribe(val -> {
                    Log.d(TAG, "loadSettings: " + val.getDomain());
                    App.getInstance().setClient(val.getProxy());
                    App.getInstance().setDomain(val.getDomain());
                    App.getInstance().setReview(val.getReview());
                    view.startNextActivity();
                }).isDisposed();
    }
}
