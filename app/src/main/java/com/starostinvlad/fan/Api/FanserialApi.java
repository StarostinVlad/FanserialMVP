package com.starostinvlad.fan.Api;

import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.GsonModels.Searched;
import com.starostinvlad.fan.GsonModels.Token;
import com.starostinvlad.fan.GsonModels.Viewed;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FanserialApi {
    @GET("/api/v1/episodes?limit=20&offset=0")
    Observable<News> getNews();

    @GET("/api/v1/episodes?limit=20")
    Observable<News> addNews(@Query("offset") int offset);

    @GET("/api/v1/profile/tape")
    Observable<News> getSubscriptions(@Query("token") String token);

    @GET("/api/v1/profile/tape")
    Observable<News> addSubscritions(@Query("token") String token, @Query("offset") int offset);

    @GET("/api/v1/search")
    Observable<List<Searched>> search(@Query("query") String query);

    @GET("/api/v1/profile/viewed")
    Observable<List<Viewed>> getViewed(@Query("token") String token);

    @POST("/api/v1/auth/social")
    Call<String> getToken(@Query("code") String code);

//    @POST("/registration/")
//    Observable<Response<Void>> registry(@Query("email") String email, @Query("password") String pass, @Query("name") String name);

    @POST("/api/v1/auth")
    Observable<Response<Token>> getToken(@Query("email") String email, @Query("password") String pass);

    @POST("/api/v1/profile/viewed/{id}/")
    Observable<String> putViewed(@Path("id") int id, @Query("checked") boolean check);
}