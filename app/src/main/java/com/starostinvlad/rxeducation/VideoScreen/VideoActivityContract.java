package com.starostinvlad.rxeducation.VideoScreen;

import com.starostinvlad.rxeducation.GsonModels.Episode;

import java.util.ArrayList;

public interface VideoActivityContract {
    void showLoading(boolean b);

    void voiceSelectorDialog(ArrayList<Player> players);

    void qualitySelectorDialog();

    void fillToolbar(String title);

    void initPlayer(String url);
    void initRecycle(ArrayList<Episode> episodes);

    void alarm(String message);
}
