package com.starostinvlad.rxeducation;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

import static com.starostinvlad.rxeducation.Utils.CLIENT;

public class NetworkService {
    private static final String BASE_URL = "http://fanserial.net";
    private static NetworkService mInstance;
    private Retrofit mRetrofit;

    private NetworkService() {

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(CLIENT)
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

    public FanserialApi getJSONApi() {
        return mRetrofit.create(FanserialApi.class);
    }
}