package com.starostinvlad.fan.ViewedScreen;

import com.starostinvlad.fan.BaseMVP.MvpView;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.GsonModels.Viewed;

import java.util.List;

public interface ViewedFragmentContract extends MvpView {
    void fillList(List<News> viewedList);

    void showLoading(boolean show);

    void showButton(boolean show);
}
