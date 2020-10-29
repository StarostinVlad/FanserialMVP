package com.starostinvlad.rxeducation.Api;

import com.starostinvlad.rxeducation.GsonModels.News;
import com.starostinvlad.rxeducation.GsonModels.Searched;
import com.starostinvlad.rxeducation.GsonModels.Token;
import com.starostinvlad.rxeducation.GsonModels.Viewed;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface FanserialApi {
    @GET("/api/v1/episodes?limit=20&offset=0")
    Observable<News> getNews();

    @GET("/api/v1/episodes?limit=20")
    Observable<News> addNews(@Query("offset") int offset);

    @GET("/api/v1/search")
    Observable<List<Searched>> search(@Query("query") String query);

    @GET("/api/v1/profile/viewed")
    Observable<List<Viewed>> getViewed(@Query("token") String token);

    @POST("/api/v1/auth/social")
    Call<String> getToken(@Query("code") String code);

    @POST("/api/v1/auth")
    Observable<Response<Token>> getToken(@Query("email") String email, @Query("password") String pass);

    @POST("/api/v1/profile/viewed/{id}/")
    Call<String> putViewed(@Path("id") int id, @Query("checked") boolean check);
}