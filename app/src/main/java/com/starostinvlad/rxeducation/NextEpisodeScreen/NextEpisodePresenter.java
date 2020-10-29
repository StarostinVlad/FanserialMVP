package com.starostinvlad.rxeducation.NextEpisodeScreen;

import com.starostinvlad.rxeducation.Api.NetworkService;

import rx.android.schedulers.AndroidSchedulers;

import static com.starostinvlad.rxeducation.Utils.TOKEN;

class NextEpisodePresenter {
    private final String TAG = getClass().getSimpleName();
    private final NextEpisodeFragmentContract view;

    NextEpisodePresenter(NextEpisodeFragmentContract view) {
        this.view = view;
    }


    void loadData() {
        view.showLoading(true);
        view.showButton(false);
        NetworkService.getInstance()
                .getApi()
                .getViewed(TOKEN)
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
                );
    }
}
