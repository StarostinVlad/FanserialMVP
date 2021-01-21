package com.starostinvlad.fan.Api;

import com.starostinvlad.fan.App;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService mInstance;
    private Retrofit mRetrofit;

    private NetworkService() {

        RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());


        mRetrofit = new Retrofit.Builder()
                .baseUrl(App.getInstance().getDomain())
                .client(App.getInstance().getOkHttpClient())
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    public FanserialApi getApi() {
        return mRetrofit.create(FanserialApi.class);
    }
}
