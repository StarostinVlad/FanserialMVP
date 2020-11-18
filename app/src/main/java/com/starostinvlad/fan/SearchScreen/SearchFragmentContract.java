package com.starostinvlad.fan.SearchScreen;

import com.starostinvlad.fan.GsonModels.Searched;

import java.util.List;

public interface SearchFragmentContract {
    void showLoading(boolean show);

    void fillList(List<Searched> arr);

    void showMessage(String message);
}
