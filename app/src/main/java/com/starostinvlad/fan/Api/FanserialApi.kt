package com.starostinvlad.fan.Api

import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.GsonModels.Searched
import com.starostinvlad.fan.GsonModels.Token
import com.starostinvlad.fan.GsonModels.Viewed
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.Throws

interface FanserialApi {
    @get:GET("/api/v1/episodes?limit=20&offset=0")
    val news: Observable<News?>?

    @GET("/api/v1/episodes?limit=20")
    fun addNews(@Query("offset") offset: Int): Observable<News?>?

    @GET("/api/v1/profile/tape")
    fun getSubscriptions(@Query("token") token: String?): Observable<News?>?

    @GET("/api/v1/profile/tape")
    fun addSubscritions(@Query("token") token: String?, @Query("offset") offset: Int): Observable<News?>?

    @GET("/api/v1/search")
    fun search(@Query("query") query: String?): Observable<List<Searched?>?>?

    @GET("/api/v1/profile/viewed")
    fun getViewed(@Query("token") token: String?): Observable<List<Viewed?>?>?

    @POST("/api/v1/auth/social")
    fun getToken(@Query("code") code: String?): Call<String?>?

    //    @POST("/registration/")
    //    Observable<Response<Void>> registry(@Query("email") String email, @Query("password") String pass, @Query("name") String name);
    @POST("/api/v1/auth")
    fun getToken(@Query("email") email: String?, @Query("password") pass: String?): Observable<Response<Token?>?>?

    @POST("/api/v1/profile/viewed/{id}/")
    fun putViewed(@Path("id") id: Int, @Query("checked") check: Boolean): Observable<String?>?
}