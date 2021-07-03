package com.starostinvlad.fan.BaseMVP;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<T extends MvpView> implements MvpPresenter<T> {
    protected CompositeDisposable disposables = new CompositeDisposable();

    protected T view;

    @Override
    public void attachView(T mvpView) {
        view = mvpView;
    }

    @Override
    public void detachView() {
        view = null;
        disposables.dispose();
    }

}
