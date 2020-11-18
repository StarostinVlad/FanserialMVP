package com.starostinvlad.fan.ViewedScreen;

import com.starostinvlad.fan.Api.NetworkService;
import com.starostinvlad.fan.App;

import io.reactivex.android.schedulers.AndroidSchedulers;


class ViewedPresenter {
    private final String TAG = getClass().getSimpleName();
    private final ViewedFragmentContract view;

    ViewedPresenter(ViewedFragmentContract view) {
        this.view = view;
    }


    void loadData() {
        view.showLoading(true);
        view.showButton(false);
        NetworkService.getInstance()
                .getApi()
                .getViewed(App.TOKEN_subject.getValue())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        val -> {
                            view.fillList(val);
                            view.showLoading(false);
                        },
                        e -> {
                            e.printStackTrace();
                            view.showLoading(false);
                            view.showButton(true);
                        }
                ).isDisposed();
    }
}
