package com.starostinvlad.fan.SplashScreen;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.Native;
import com.starostinvlad.fan.BuildConfig;
import com.starostinvlad.fan.MainActivity.MainActivity;
import com.starostinvlad.fan.R;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import io.reactivex.Observable;

public class SplashActivity extends AppCompatActivity implements SplashScreenContract {

    private final String TAG = getClass().getSimpleName();
    private SplashScreenPresenter presenter;
    private ProgressDialog progressDialog;

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.detach();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setTitle("Загрузка обновления");

        Appodeal.setTesting(BuildConfig.DEBUG);
//        Appodeal.setLogLevel(com.appodeal.ads.utils.Log.LogLevel.verbose);
        Appodeal.setRequiredNativeMediaAssetType(Native.MediaAssetType.ICON);
        Appodeal.setNativeAdType(Native.NativeAdType.Auto);
        Appodeal.initialize(this, "5b840848384e83385753354fd57248b212fbd0a454d85083", Appodeal.INTERSTITIAL);


        presenter = new SplashScreenPresenter(this);
        presenter.loadSettings();
    }


    @Override
    public void startNextActivity() {
        MainActivity.start(this);
        finish();
    }

    @Override
    public void showUpdateDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setMessage("Доступна новая версия приложения. Обновиться?")
                .setCancelable(false)
                .setPositiveButton("Обновить", (dialogInterface, i) -> {
                    startloading();
                })
                .setNegativeButton("Нет", (dialogInterface, i) -> {
                    startNextActivity();
                });
        alertBuilder.show();
    }

    private void startloading() {
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse("https://github.com/StarostinVlad/StarostinVlad.github.io/raw/master/app-release.apk");

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Update!");
        request.setDescription("Загрузка обновления");
        request.setVisibleInDownloadsUi(false);
        request.setDestinationUri(Uri.parse("file:///" + SplashActivity.this.getExternalFilesDir(null) + "/app.apk"));

        long id = downloadmanager.enqueue(request);
        presenter.loadUpdate(id, downloadmanager);
    }

    @Override
    public void startInstallIntent() {
//        progressDialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(getExternalFilesDir(null) + "/app.apk");
        Log.d("UpdateApp", "path: " + file.getAbsolutePath());
        Uri data = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void showProgressDialog() {
//        progressDialog.show();
    }
}

