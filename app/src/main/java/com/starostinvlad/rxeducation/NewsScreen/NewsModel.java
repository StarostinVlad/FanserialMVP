package com.starostinvlad.rxeducation.NewsScreen;

import com.starostinvlad.rxeducation.pojos.Datum;

import java.util.List;

public class NewsModel {

    List<Datum> datumList;

    public List<Datum> getDatumList() {
        return datumList;
    }

    public void setDatumList(List<Datum> datumList) {
        this.datumList = datumList;
    }

    public void addToDatumList(List<Datum> arr) {
        if (datumList == null)
            datumList = arr;
        else
            datumList.addAll(arr);
    }
}
