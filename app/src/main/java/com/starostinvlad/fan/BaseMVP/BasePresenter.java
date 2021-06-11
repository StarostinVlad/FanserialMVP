package com.starostinvlad.fan.BaseMVP;

import io.reactivex.disposables.CompositeDisposable;

public class BasePresenter {
    protected CompositeDisposable disposables = new CompositeDisposable();

    public void detach() {
        disposables.dispose();
    }
}
