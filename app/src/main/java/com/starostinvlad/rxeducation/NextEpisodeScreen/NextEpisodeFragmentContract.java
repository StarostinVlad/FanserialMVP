package com.starostinvlad.rxeducation.NextEpisodeScreen;

import com.starostinvlad.rxeducation.GsonModels.Viewed;

import java.util.List;

public interface NextEpisodeFragmentContract {
    void fillList(List<Viewed> viewedList);

    void showLoading(boolean show);

    void showButton(boolean show);
}
