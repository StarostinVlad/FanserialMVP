package com.starostinvlad.fan.VideoScreen

import com.starostinvlad.fan.BaseMVP.MvpView
import com.starostinvlad.fan.VideoScreen.PlayerModel.Episode
import com.starostinvlad.fan.VideoScreen.PlayerModel.Hls
import com.starostinvlad.fan.VideoScreen.PlayerModel.Translation
import kotlin.Throws

interface VideoActivityContract : MvpView {
    fun showLoading(show: Boolean)
    fun translationSelectorDialog(translations: List<Translation>)
    fun qualitySelectorDialog()
    fun fillToolbar(title: String)
    fun initPlayer(hls: Hls)
    fun initRecycle(episodes: List<Episode>)
    fun alarm(message: String)
    fun showDialog(msg: String)
    fun openTrailer(url: String)
    fun changeDescription(title: String, subTitle: String)
}