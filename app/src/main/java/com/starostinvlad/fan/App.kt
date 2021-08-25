package com.starostinvlad.fan

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.room.Room
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.squareup.picasso.Picasso
import com.starostinvlad.fan.GsonModels.Proxy
import io.reactivex.subjects.BehaviorSubject
import okhttp3.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern.matches

class App : Application() {
    private val TAG: String = this::class.simpleName!!
    val loginSubject = BehaviorSubject.createDefault("")
    lateinit var okHttpClient: OkHttpClient
        private set
    var domain: String = ""
    lateinit var database: AppDatabase
        private set
    var preferences: Preferences? = null
        private set
    var isReview = false
        get() {
            Log.d(TAG, "isReview: $field")
            return matches(".*_?sdk_?.*", Build.PRODUCT) || field && lastVersion == BuildConfig.VERSION_CODE
        }
    private var picasso: Picasso? = null
    fun setLastVersion(lastVersion: Int) {
        this.lastVersion = lastVersion
    }

    private var lastVersion = 0
    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder<AppDatabase>(this, AppDatabase::class.java, "database")
                .fallbackToDestructiveMigration()
                .build()
        preferences = Preferences(applicationContext)
        loginSubject.onNext(preferences?.token!!)
        loginSubject.subscribe { token: String? -> preferences?.token = token }.isDisposed
        //
        val cookieJar: ClearableCookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(applicationContext))
        okHttpClient = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    fun setClient(proxy: Proxy?) {
        val proxyAuthenticator = Authenticator { route: Route?, response: Response ->
            val credential = Credentials.basic("GiMvRf5na6", "xp9O9ViUkt")
            response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build()
        }
        val cookieJar: ClearableCookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(applicationContext))
        okHttpClient = OkHttpClient.Builder() //                .proxy(new Proxy(Proxy.Type.HTTP,
                ////                        new InetSocketAddress("45.138.159.126", 53429)))
                //                        new InetSocketAddress(proxy.getIp(), proxy.getPort())))
                .cookieJar(cookieJar)
                .connectTimeout(20, TimeUnit.SECONDS)
                .proxyAuthenticator(proxyAuthenticator)
                .build()
        if (picasso == null) {
            picasso = Picasso.Builder(applicationContext) //                .downloader(new OkHttp3Downloader(okHttpClient))
                    //                .loggingEnabled(true)
                    .indicatorsEnabled(true)
                    .build()
            Picasso.setSingletonInstance(picasso!!)
        }
    }

    companion object {
        lateinit var instance: App
            private set

    }
}