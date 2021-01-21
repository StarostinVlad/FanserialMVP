package com.starostinvlad.fan.NewsScreen;

import com.starostinvlad.fan.GsonModels.News;

import java.util.List;

public interface NewsFragmentContract {
    void fillListView(List<News> newsList);
    void showLoading(boolean show);

    void refreshListView();
    void alarm(String message);
}
