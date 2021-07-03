package com.starostinvlad.fan.LoginScreen;

import com.starostinvlad.fan.BaseMVP.MvpView;

public interface LoginFragmentContract extends MvpView {
    void showLoading(boolean load);

    void alarm(String message);
}
