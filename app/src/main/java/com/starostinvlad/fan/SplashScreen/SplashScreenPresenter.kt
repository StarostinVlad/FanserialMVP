package com.starostinvlad.fan.SplashScreen

import android.app.DownloadManager
import android.util.Log
import com.starostinvlad.fan.Api.ApiUtils
import com.starostinvlad.fan.App
import com.starostinvlad.fan.BaseMVP.BasePresenter
import com.starostinvlad.fan.BuildConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SplashScreenPresenter : BasePresenter<SplashScreenContract?>() {
    private val TAG: String = this::class.simpleName!!
    fun loadSettings() {

        GlobalScope.launch(Dispatchers.IO) {
            val settings = ApiUtils.retrofitService
                    .getSettings()
            Log.d(TAG, "loadSettings: " + settings.domain)
            App.instance.domain = settings.domain.toString()
            App.instance.isReview = settings.isReview
            App.instance.setLastVersion(settings.lastVersion)
            withContext(Dispatchers.Main) {
                if (settings.lastVersion > BuildConfig.VERSION_CODE && !settings.isReview) view!!.showUpdateDialog() else view!!.startNextActivity()
            }
        }
    }

    fun loadUpdate(id: Long, downloadManager: DownloadManager) {
        GlobalScope.launch(Dispatchers.Main) {
            view!!.showProgressDialog()
            try {

                withContext(Dispatchers.IO)
                {
                    loadApk(id, downloadManager)
                }
                view!!.startInstallIntent()
            } catch (e: Exception) {
                e.printStackTrace();
            }

        }
    }

    private fun loadApk(id: Long, downloadManager: DownloadManager): Boolean {
        val query = DownloadManager.Query()
        query.setFilterById()
        var progress = 0.0
        while (progress != 100.0) {
            val c = downloadManager.query(query)
            if (c.moveToFirst()) {
                val sizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                val downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val size = c.getInt(sizeIndex).toLong()
                val downloaded = c.getInt(downloadedIndex).toLong()
                if (size != -1L) progress = downloaded * 100.0 / size
                // At this point you have the progress as a percentage.
            }
            Log.d("UpdateApp", "progres: $progress")
        }
        return true
    }
}