package com.starostinvlad.fan.SerialPageScreen;

import com.starostinvlad.fan.BaseMVP.MvpView;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial;
import com.starostinvlad.fan.VideoScreen.PlayerModel.SerialPlayer;

public interface SerialPageScreenContract extends MvpView {
    void showLoading(boolean show);

    void fillPage(SerialPlayer serialPlayer);

    void openActivityWithSerial(Serial serial);

    void fillSeasonsList(Serial serial);

    void checkViewed(boolean viewed);

    void checkSubscribed(boolean subscribed);

    void fillBtn(int currentSeason, int currentEpisodeIndex);
}
