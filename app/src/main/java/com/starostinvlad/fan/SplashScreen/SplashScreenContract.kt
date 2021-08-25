package com.starostinvlad.fan.SplashScreen

import com.starostinvlad.fan.BaseMVP.MvpView
import kotlin.Throws

interface SplashScreenContract : MvpView {
    fun startNextActivity()
    fun showUpdateDialog()
    fun showProgressDialog()
    fun startInstallIntent()
}