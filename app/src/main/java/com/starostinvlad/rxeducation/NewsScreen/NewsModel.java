package com.starostinvlad.rxeducation.NewsScreen;

import com.starostinvlad.rxeducation.GsonModels.Datum;

import java.util.ArrayList;
import java.util.List;

class NewsModel {

    List<Datum> episodeList;

    List<Datum> getEpisodeList() {
        if (episodeList == null)
            episodeList = new ArrayList<>();
        return episodeList;
    }

    void setEpisodeList(List<Datum> episodeList) {
        this.episodeList = episodeList;
    }

    void addToDatumList(List<Datum> arr) {
        if (episodeList == null)
            episodeList = arr;
        else
            episodeList.addAll(arr);
    }
}
