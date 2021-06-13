package com.starostinvlad.fan.VideoScreen;

import com.starostinvlad.fan.BaseMVP.MvpView;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Episode;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Hls;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Translation;

import java.util.ArrayList;
import java.util.List;

public interface VideoActivityContract extends MvpView {
    void showLoading(boolean b);

    void translationSelectorDialog(List<Translation> translations);

    void qualitySelectorDialog();

    void fillToolbar(String title);

    void initPlayer(Hls hls);

    void initRecycle(List<Episode> episodes);

    void alarm(String message);


    void showDialog(String msg);

    void openTrailer(String url);

    void changeDescription(String title, String title1);
}
