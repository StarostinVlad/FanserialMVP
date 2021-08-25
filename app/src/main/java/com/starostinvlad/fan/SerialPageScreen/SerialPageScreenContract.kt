package com.starostinvlad.fan.SerialPageScreen

import com.starostinvlad.fan.BaseMVP.MvpView
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial
import com.starostinvlad.fan.VideoScreen.PlayerModel.SerialPlayer
import kotlin.Throws

interface SerialPageScreenContract : MvpView {
    fun showLoading(show: Boolean)
    fun fillPage(serialPlayer: SerialPlayer?)
    fun openActivityWithSerial(serial: Serial?)
    fun fillSeasonsList(serial: Serial)
    fun checkViewed(viewed: Boolean)
    fun checkSubscribed(subscribed: Boolean)
    fun fillBtn(currentSeasonIndex: Int, currentEpisodeIndex: Int)
}