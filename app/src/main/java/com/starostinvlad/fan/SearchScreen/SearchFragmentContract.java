package com.starostinvlad.fan.SearchScreen;

import com.starostinvlad.fan.BaseMVP.MvpView;
import com.starostinvlad.fan.GsonModels.News;

import java.util.List;

public interface SearchFragmentContract extends MvpView {
    void showLoading(boolean show);

    void fillList(List<News> arr);

    void showMessage(String message);
}
