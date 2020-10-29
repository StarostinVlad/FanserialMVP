package com.starostinvlad.rxeducation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import rx.subjects.PublishSubject;

public class Utils {
    private static final Utils ourInstance = new Utils();
    public static String DOMAIN = "https://fanserial.net";
    public static String TOKEN = "5cb670c76f97f0caa9c715b92158457b7a539aa6afa9afa6e6e6b10c2b44322b1e9fe0c34737869c599378d1d8234c0c3f72238bffb5990770705480d8c7ae5870e3c1403b02562ab2ad266159eb0489396bdd0af6094bf62d60b096348c1e18d4d7122af78829a882d906be0f19e192c62a36e46baf6d9889672758311b29f";
    public static Boolean AUTH = false;
    public static OkHttpClient CLIENT;
    public static PublishSubject<Boolean> AUTH_subject = PublishSubject.create();

    private Utils() {
    }

    static Utils getInstance() {
        return ourInstance;
    }

    static void init(Context context) {
        Authenticator proxyAuthenticator = (route, response) -> {
            String credential = Credentials.basic("GiMvRf5na6", "xp9O9ViUkt");
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        CLIENT = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP,
                        new InetSocketAddress("45.138.159.126", 53429)))
                .connectTimeout(20, TimeUnit.SECONDS)
                .proxyAuthenticator(proxyAuthenticator)
                .build();

        final Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(CLIENT))
                .build();
        picasso.setIndicatorsEnabled(true);
        Picasso.setSingletonInstance(picasso);
    }

    public static boolean isNetworkOnline(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
