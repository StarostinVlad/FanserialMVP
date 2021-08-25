package com.starostinvlad.fan.SerialPageScreen

import android.util.Log
import com.starostinvlad.fan.App
import com.starostinvlad.fan.BaseMVP.BasePresenter
import com.starostinvlad.fan.GsonModels.CurrentSerial
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial
import com.starostinvlad.fan.VideoScreen.PlayerModel.SerialPlayer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal class SerialPageScreenPresenter : BasePresenter<SerialPageScreenContract?>() {
    private var serialPlayer: SerialPlayer? = null
    private var serial: Serial? = null
    private val TAG: String = this::class.simpleName!!
    override fun attachView(mvpView: SerialPageScreenContract?) {
        super.attachView(mvpView)
        serialPlayer = SerialPlayer(App.instance.domain, App.instance.okHttpClient)
    }

    fun putToSubscribe(id: String?, checked: Boolean) {
        disposables.add(
                serialPlayer!!.subscribeRequest(id, checked)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { `val`: String? -> Log.d(TAG, "answer: $`val`") }
                        ) { throwable: Throwable ->
//                                    sendErrorMsg(throwable.getMessage());
                            throwable.printStackTrace()
                        }
        )
    }

    fun loadData(url: String) {
        view!!.showLoading(true)
        disposables.add(
                serialPlayer!!
                        .getSerial(url)
                        .flatMap(
                                { serial ->
                                    App.instance
                                            .database
                                            .currentSerialDao()
                                            .getById(serial.id)
                                            .defaultIfEmpty(CurrentSerial(
                                                    serial.id,
                                                    serial.currentSeasonIndex,
                                                    serial.currentEpisodeIndex,
                                                    serial.currentTranslationIndex
                                            ))
                                }
                        ) { serial1: Serial?, currentSerial: CurrentSerial? ->
                            if (currentSerial != null) {
                                serial1?.currentSeasonIndex = currentSerial.currentSeasonIndex
                                serial1?.currentEpisodeIndex = currentSerial.currentEpisodeIndex
                                serial1?.currentTranslationIndex = currentSerial.currentTranslationIndex
                            }
                            serial1!!
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ serial: Serial? ->
                            this.serial = serial
                            view!!.fillBtn(
                                    serial!!.currentSeasonIndex + 1,
                                    serial.currentEpisodeIndex + 1
                            )
                            view!!.fillPage(serialPlayer)
                            view!!.fillSeasonsList(serial)
                            view!!.checkSubscribed(serialPlayer!!.isSubscribed)
                            Log.d(TAG, "loadData: serial: $serial")
                            view!!.showLoading(false)
                        }) { obj: Throwable -> obj.printStackTrace() }
        )
    }

    fun openSerialOnClick() {
        view!!.openActivityWithSerial(serial)
    }
}