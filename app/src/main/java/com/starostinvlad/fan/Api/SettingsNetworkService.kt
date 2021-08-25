package com.starostinvlad.fan.Api

import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            val rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(rxAdapter)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit!!
    }
}

object ApiUtils{
    private val BASE_URL = "https://starostinvlad.github.io/"
    val retrofitService: SettingsApi
        get() = RetrofitClient.getClient(BASE_URL).create(SettingsApi::class.java)
}
