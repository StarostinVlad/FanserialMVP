package com.starostinvlad.fan.VideoScreen

import android.util.Log
import com.starostinvlad.fan.App
import com.starostinvlad.fan.BaseMVP.BasePresenter
import com.starostinvlad.fan.GsonModels.CurrentSerial
import com.starostinvlad.fan.VideoScreen.PlayerModel.Hls
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial
import com.starostinvlad.fan.VideoScreen.PlayerModel.SerialPlayer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal class VideoPresenter : BasePresenter<VideoActivityContract?>() {
    private val TAG: String = this::class.simpleName!!

    private var serial: Serial? = null
    private var serialPlayer: SerialPlayer? = null
    override fun attachView(mvpView: VideoActivityContract?) {
        super.attachView(mvpView)
        serialPlayer = SerialPlayer(App.instance.domain, App.instance.okHttpClient)
    }

    fun onDetach() {
        disposables.dispose()
    }

    fun onStartWithSerial(serial: Serial) {
        this.serial = serial
        view!!.initRecycle(serial.currentSeason.episodes)
        Log.d(TAG, "loadData: serial: $serial")
        view!!.showLoading(false)
    }

    private fun changeVideoSource() {
//        Log.d(TAG, "getVideo: " + serial.getCurrentEpisodeIndex());
        var url = serial!!
                .currentSeason
                .getEpisode(serial!!.currentEpisodeIndex)
                .getTranslation(serial!!.currentTranslationIndex)
                .url
        if (url.contains("youtube")) {
            url = if (url.startsWith("http")) url else "http://$url"
            view!!.openTrailer(url)
        } else {
            startVideo(url)
        }
        changeDescription()
    }

    private fun startVideo(url: String?) {
        disposables.add(
                serialPlayer!!.getHlsObject(url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ hls: Hls? -> view!!.initPlayer(hls!!) }) { throwable: Throwable ->
                            sendErrorMsg(throwable.message)
                            throwable.printStackTrace()
                        }
        )
    }

    fun onStart() {
        if (serial != null) {
            changeVideoSource()
        }
    }

    fun onBuildDialog() {
        view!!
                .translationSelectorDialog(
                        serial!!.currentSeason.getEpisode(serial!!.currentEpisodeIndex).translations!!
                )
    }

    private fun sendErrorMsg(msg: String?) {
        var localMsg = msg
        if (localMsg != null) {
            if (localMsg.contains("timeout")) localMsg = "Превышено время ожидания" else if (localMsg.contains("returned null")) localMsg = "Что-то пошло не так" else if (localMsg.contains("review")) localMsg = "Сериал недоступен в вашей стране"
            view!!.showDialog(localMsg)
        }
    }

    fun onChangeEpisode(position: Int) {
        var localPosition: Int = position
        if (localPosition >= serial!!.currentSeason.episodes.size) localPosition = serial!!.currentSeason.episodes.size - 1
        disposables.add(
                App.instance.database.currentSerialDao()
                        .insert(
                                CurrentSerial(
                                        serial!!.id,
                                        serial!!.currentSeasonIndex,
                                        localPosition,
                                        serial!!.currentTranslationIndex
                                )
                        )
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                { serial: Long -> Log.d(TAG, "changeVideoSource: $serial") }, { obj: Throwable -> obj.printStackTrace() })
        )
        serial!!.currentEpisodeIndex = localPosition
        Log.d(TAG, "getEpisode: " + serial!!.currentEpisodeIndex)
        changeVideoSource()
    }

    fun onChangeTranslation(i: Int) {
        serial!!.currentTranslationIndex = i
        changeVideoSource()
    }

    private fun changeDescription() {
        view!!.changeDescription(
                serial!!.currentSeason
                        .getEpisode(serial!!.currentEpisodeIndex)
                        .getTranslation(serial!!.currentTranslationIndex)
                        .title,
                serial!!.currentSeason
                        .getEpisode(serial!!.currentEpisodeIndex)
                        .title
        )
    }
}