package com.starostinvlad.fan.Api

import com.starostinvlad.fan.App
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService private constructor() {
    private val mRetrofit: Retrofit
    val api: FanserialApi
        get() = mRetrofit.create(FanserialApi::class.java)

    companion object {
        private var mInstance: NetworkService? = null
        val instance: NetworkService?
            get() {
                if (mInstance == null) {
                    mInstance = NetworkService()
                }
                return mInstance
            }
    }

    init {
        val rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
        mRetrofit = Retrofit.Builder()
                .baseUrl(App.instance.domain)
                .client(App.instance.okHttpClient)
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}