package com.starostinvlad.fan.SerialPageScreen;

import io.reactivex.disposables.CompositeDisposable;

class BasePresenter {
    CompositeDisposable disposables = new CompositeDisposable();

    void detach() {
        disposables.dispose();
    }
}
