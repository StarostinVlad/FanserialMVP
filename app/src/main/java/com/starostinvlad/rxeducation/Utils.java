package com.starostinvlad.rxeducation;

import android.content.Context;
import android.media.browse.MediaBrowser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.net.InetSocketAddress;
import java.net.Proxy;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import rx.subjects.PublishSubject;

public class Utils {
    private static final Utils ourInstance = new Utils();
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
