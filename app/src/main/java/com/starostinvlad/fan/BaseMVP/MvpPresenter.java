package com.starostinvlad.fan.BaseMVP;

public interface MvpPresenter<V> {
    void attachView(V mvpView);

    void detachView();
}
