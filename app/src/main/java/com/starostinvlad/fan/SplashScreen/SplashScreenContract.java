package com.starostinvlad.fan.SplashScreen;

import com.starostinvlad.fan.BaseMVP.MvpView;

public interface SplashScreenContract extends MvpView {
    void startNextActivity();

    void showUpdateDialog();

    void showProgressDialog();

    void startInstallIntent();
}
