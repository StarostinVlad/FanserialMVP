package com.starostinvlad.rxeducation.VideoScreen;

import java.util.ArrayList;

public interface VideoFragmentContract {
    void showLoading(boolean b);

    void fillSpiner(ArrayList<Player> players);

    void fillToolbar(String title);

    void initPlayer(String url);
}
