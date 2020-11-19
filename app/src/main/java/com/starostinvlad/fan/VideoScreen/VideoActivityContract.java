package com.starostinvlad.fan.VideoScreen;

import com.starostinvlad.fan.GsonModels.Episode;

import java.util.ArrayList;

public interface VideoActivityContract {
    void showLoading(boolean b);

    void voiceSelectorDialog(ArrayList<Player> players);

    void qualitySelectorDialog();

    void fillToolbar(String title);

    void initPlayer(String url);

    void initRecycle(ArrayList<Episode> episodes);

    void alarm(String message);

    void checkViewed(boolean viewed);

    void checkSubscribed(String id, boolean subscribed);

    void prevBtn(Episode episode);

    void nextBtn(Episode episode);
}
