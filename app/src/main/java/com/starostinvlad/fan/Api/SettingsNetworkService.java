package com.starostinvlad.fan.Api;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsNetworkService {
    private static SettingsNetworkService mInstance;
    private Retrofit mRetrofit;

    private SettingsNetworkService() {

        RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://starostinvlad.github.io/")
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static SettingsNetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new SettingsNetworkService();
        }
        return mInstance;
    }

    public SettingsApi getApi() {
        return mRetrofit.create(SettingsApi.class);
    }
}