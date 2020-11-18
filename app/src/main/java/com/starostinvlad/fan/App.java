package com.starostinvlad.fan;

import android.app.Application;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import androidx.room.Room;
import io.reactivex.subjects.BehaviorSubject;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

public class App extends Application {

    public static OkHttpClient CLIENT;
    public static String DOMAIN = "https://fanserial.net";
    public static BehaviorSubject<String> TOKEN_subject = BehaviorSubject.createDefault("");
    private static App instance;
    private AppDatabase database;
    private Preferences preferences;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .fallbackToDestructiveMigration()
                .build();
        preferences = new Preferences(getApplicationContext());

        Authenticator proxyAuthenticator = (route, response) -> {
            String credential = Credentials.basic("GiMvRf5na6", "xp9O9ViUkt");
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        TOKEN_subject.onNext(preferences.getToken());
        TOKEN_subject.subscribe(token -> preferences.setToken(token)).isDisposed();

        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));

        CLIENT = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP,
                        new InetSocketAddress("45.138.159.126", 53429)))
                .cookieJar(cookieJar)
                .connectTimeout(20, TimeUnit.SECONDS)
                .proxyAuthenticator(proxyAuthenticator)
                .build();

        final Picasso picasso = new Picasso.Builder(getApplicationContext())
                .downloader(new OkHttp3Downloader(CLIENT))
                .build();
        picasso.setIndicatorsEnabled(true);
        Picasso.setSingletonInstance(picasso);
//
//        try {
//            Utils.init(this);
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public Preferences getPreferences() {
        return preferences;
    }
}