package com.starostinvlad.fan.SplashScreen;

import android.app.DownloadManager;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.starostinvlad.fan.Api.SettingsNetworkService;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.BuildConfig;
import com.starostinvlad.fan.VideoScreen.PlayerModel.PlayerDataDeserializer;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class SplashScreenPresenter {
    private final String TAG = getClass().getSimpleName();
    private SplashScreenContract view;
    private String referer = "";

    SplashScreenPresenter(SplashScreenContract view) {
        this.view = view;
    }

    void loadSettings() {
        SettingsNetworkService
                .getInstance()
                .getApi()
                .getSettings()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(val -> {
                    Log.d(TAG, "loadSettings: " + val.getDomain());
                    App.getInstance().setClient(val.getProxy());
                    App.getInstance().setDomain(val.getDomain());
                    App.getInstance().setReview(val.getReview());
                    App.getInstance().setLastVersion(val.getLastVersion());
                    if (val.getLastVersion() > BuildConfig.VERSION_CODE && !val.getReview())
                        view.showUpdateDialog();
                    else
                        view.startNextActivity();
                }).isDisposed();
    }

    void loadUpdate(long id, DownloadManager downloadManager) {
        view.showProgressDialog();
        Observable.fromCallable(() -> loadApk(id, downloadManager))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(val -> view.startInstallIntent(), Throwable::printStackTrace).isDisposed();
    }

    private boolean loadApk(long id, DownloadManager downloadManager) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById();
        double progress = 0.0;

        while (progress != 100) {
            Cursor c = downloadManager.query(query);

            if (c.moveToFirst()) {
                int sizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                long size = c.getInt(sizeIndex);
                long downloaded = c.getInt(downloadedIndex);

                if (size != -1) progress = downloaded * 100.0 / size;
                // At this point you have the progress as a percentage.
            }
            Log.d("UpdateApp", "progres: " + progress);
        }
        return true;
    }

}
