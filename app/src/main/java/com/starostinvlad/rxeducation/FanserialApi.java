package com.starostinvlad.rxeducation;

import com.starostinvlad.rxeducation.pojos.News;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface FanserialApi {
    @GET("/api/v1/episodes?limit=20&offset=0")
    Observable<News> getNews();

    @GET("/api/v1/episodes?limit=20")
    Observable<News> addNews(@Query("offset") int offset);
}