package com.starostinvlad.fan.SplashScreen

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.appodeal.ads.Appodeal
import com.appodeal.ads.Native
import com.starostinvlad.fan.BuildConfig
import com.starostinvlad.fan.MainActivity.MainActivity
import com.starostinvlad.fan.R
import java.io.File

class SplashActivity : AppCompatActivity(), SplashScreenContract {
    private val TAG: String = javaClass.getSimpleName()
    private var presenter: SplashScreenPresenter? = null
    private val progressDialog: ProgressDialog? = null
    override fun onDestroy() {
        if (presenter != null) {
            presenter!!.detachView()
        }
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView(R.layout.activity_splash);
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setTitle("Загрузка обновления");
        Appodeal.setTesting(BuildConfig.DEBUG)
        //        Appodeal.setLogLevel(com.appodeal.ads.utils.Log.LogLevel.verbose);
        Appodeal.setRequiredNativeMediaAssetType(Native.MediaAssetType.ICON)
        Appodeal.setNativeAdType(Native.NativeAdType.Auto)
        Appodeal.initialize(this, "5b840848384e83385753354fd57248b212fbd0a454d85083", Appodeal.INTERSTITIAL)
        presenter = SplashScreenPresenter()
        presenter!!.attachView(this)
        presenter!!.loadSettings()
    }

    override fun startNextActivity() {
        MainActivity.start(this)
        finish()
    }

    override fun showUpdateDialog() {
        val alertBuilder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.SplashDialogTheme))
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Внимание")
                .setMessage("Доступна новая версия приложения. Обновиться?")
                .setCancelable(false)
                .setPositiveButton("Обновить") { _: DialogInterface?, _: Int -> startloading() }
                .setNegativeButton("Нет") { _: DialogInterface?, _: Int -> startNextActivity() }
        alertBuilder.show()
    }

    private fun startloading() {
        val downloadmanager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse("https://github.com/StarostinVlad/StarostinVlad.github.io/raw/master/app-debug.apk")
        val request = DownloadManager.Request(uri)
        request.setTitle("Загрузка")
        request.setDescription("Загрузка обновления")
        request.setVisibleInDownloadsUi(false)
        request.setDestinationUri(Uri.parse("file:///" + getExternalFilesDir(null) + "/app.apk"))
        val id = downloadmanager.enqueue(request)
        presenter!!.loadUpdate(id, downloadmanager)
    }

    override fun startInstallIntent() {
//        progressDialog.dismiss();
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(getExternalFilesDir(null).toString() + "/app.apk")
        Log.d("UpdateApp", "path: " + file.absolutePath)
        val data = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        intent.setDataAndType(data, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    override fun showProgressDialog() {
//        progressDialog.show();
    }
}