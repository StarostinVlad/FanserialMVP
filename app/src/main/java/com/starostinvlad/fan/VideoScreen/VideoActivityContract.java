package com.starostinvlad.fan.VideoScreen;

import com.starostinvlad.fan.GsonModels.Episode;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Hls;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Translation;

import java.util.ArrayList;

public interface VideoActivityContract {
    void showLoading(boolean b);

    void translationSelectorDialog(ArrayList<Translation> translations);

    void qualitySelectorDialog();

    void fillToolbar(String title);

    void initPlayer(Hls hls);

    void initRecycle(Translation translation);

    void alarm(String message);

    void checkViewed(boolean viewed);

    void checkSubscribed(boolean subscribed);

    void prevBtn(Episode episode);

    void nextBtn(Episode episode);

    void showDialog(String msg);

    void openTrailer(String iframe);

    void changeDescription(String title, String title1);
}
