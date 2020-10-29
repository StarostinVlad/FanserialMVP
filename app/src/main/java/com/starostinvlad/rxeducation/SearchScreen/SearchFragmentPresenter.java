package com.starostinvlad.rxeducation.SearchScreen;

import com.starostinvlad.rxeducation.Api.NetworkService;

import rx.android.schedulers.AndroidSchedulers;

class SearchFragmentPresenter {
    private final String TAG = getClass().getSimpleName();
    private final SearchFragmentContract view;

    SearchFragmentPresenter(SearchFragmentContract view) {
        this.view = view;
    }

    void searchQuery(String query) {
        if (query.length() > 3) {
            view.showLoading(true);
            NetworkService
                    .getInstance()
                    .getApi()
                    .search(query)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            arr -> {
                                if (arr.isEmpty()) {
                                    view.showMessage("По запросу \"" + query + "\" ничего не найдено");
                                } else {
                                    view.fillList(arr);
                                }
                                view.showLoading(false);
                            },
                            throwable -> {
                                throwable.printStackTrace();
                                view.showLoading(false);
                            }
                    );
        } else {
            view.showMessage("Длина запроса должна быть более трёх символов");
        }
    }
}
