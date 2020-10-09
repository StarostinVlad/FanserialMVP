package com.starostinvlad.rxeducation.VideoScreen;

import java.util.ArrayList;

public interface VideoActivityContract {
    void showLoading(boolean b);

    void fillSpiner(ArrayList<Player> players);

    void fillToolbar(String title);

    void initPlayer(String url);

    void initSpinnerClickListener();

    void alarm(String message);
}
