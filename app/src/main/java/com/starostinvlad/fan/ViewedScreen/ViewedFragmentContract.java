package com.starostinvlad.fan.ViewedScreen;

import com.starostinvlad.fan.GsonModels.Viewed;

import java.util.List;

public interface ViewedFragmentContract {
    void fillList(List<Viewed> viewedList);

    void showLoading(boolean show);

    void showButton(boolean show);
}
