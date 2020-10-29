package com.starostinvlad.rxeducation.NewsScreen;

import com.starostinvlad.rxeducation.GsonModels.Datum;

import java.util.List;

public interface NewsFragmentContract {
    void fillListView(List<Datum> datumArrayList);
    void showLoading(boolean show);

    void refreshListView();
    void alarm(String message);
}
