package com.starostinvlad.rxeducation.NewsScreen;

import com.starostinvlad.rxeducation.pojos.Datum;

import java.util.List;

public interface NewsFragmentContract {
    void fillListView(List<Datum> datumArrayList);
    void showLoading(boolean show);

    void addToListView(List<Datum> arr);
}
