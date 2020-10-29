package com.starostinvlad.rxeducation.SearchScreen;

import com.starostinvlad.rxeducation.GsonModels.Searched;

import java.util.List;

public interface SearchFragmentContract {
    void showLoading(boolean show);

    void fillList(List<Searched> arr);

    void showMessage(String message);
}
