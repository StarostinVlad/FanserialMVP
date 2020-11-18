package com.starostinvlad.fan.NewsScreen;

import com.starostinvlad.fan.GsonModels.Datum;

import java.util.List;

public interface NewsFragmentContract {
    void fillListView(List<Datum> datumArrayList);
    void showLoading(boolean show);

    void refreshListView();
    void alarm(String message);
}
