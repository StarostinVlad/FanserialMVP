package com.starostinvlad.fan;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import androidx.room.Room;
import io.reactivex.subjects.BehaviorSubject;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

public class App extends Application {

    private static App instance;
    private final String TAG = getClass().getSimpleName();
    private BehaviorSubject<String> loginSubject = BehaviorSubject.createDefault("");
    private OkHttpClient okHttpClient;
    private String domain = "https://hdseria.tv";
    private AppDatabase database;
    private Preferences preferences;
    private boolean review;
    private Picasso picasso;

    public void setLastVersion(int lastVersion) {
        this.lastVersion = lastVersion;
    }

    private int lastVersion = 0;

    public static App getInstance() {
        return instance;
    }

    public boolean isReview() {
        Log.d(TAG, "isReview: " + review);
        return Build.PRODUCT.matches(".*_?sdk_?.*") || (review && lastVersion == BuildConfig.VERSION_CODE);
    }

    public void setReview(boolean review) {
        this.review = review;
    }

    public BehaviorSubject<String> getLoginSubject() {
        return loginSubject;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .fallbackToDestructiveMigration()
                .build();
        preferences = new Preferences(getApplicationContext());

        loginSubject.onNext(preferences.getToken());
        loginSubject.subscribe(token -> preferences.setToken(token)).isDisposed();
//
//        okHttpClient = new OkHttpClient.Builder()
//                .proxy(new Proxy(Proxy.Type.HTTP,
//                        new InetSocketAddress("45.138.159.126", 53429)))
//                .cookieJar(cookieJar)
//                .connectTimeout(20, TimeUnit.SECONDS)
//                .proxyAuthenticator(proxyAuthenticator)
//                .build();

    }

    public AppDatabase getDatabase() {
        return database;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setClient(com.starostinvlad.fan.GsonModels.Proxy proxy) {
        Authenticator proxyAuthenticator = (route, response) -> {
            String credential = Credentials.basic("GiMvRf5na6", "xp9O9ViUkt");
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));
        okHttpClient = new OkHttpClient.Builder()
//                .proxy(new Proxy(Proxy.Type.HTTP,
////                        new InetSocketAddress("45.138.159.126", 53429)))
//                        new InetSocketAddress(proxy.getIp(), proxy.getPort())))
                .cookieJar(cookieJar)
                .connectTimeout(20, TimeUnit.SECONDS)
                .proxyAuthenticator(proxyAuthenticator)
                .build();
        if (picasso == null) {
            picasso = new Picasso.Builder(getApplicationContext())
//                .downloader(new OkHttp3Downloader(okHttpClient))
//                .loggingEnabled(true)
                    .indicatorsEnabled(true)
                    .build();

            Picasso.setSingletonInstance(picasso);
        }
    }
}