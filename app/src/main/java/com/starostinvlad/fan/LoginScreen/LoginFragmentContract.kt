package com.starostinvlad.fan.LoginScreen

import com.starostinvlad.fan.BaseMVP.MvpView

interface LoginFragmentContract : MvpView {
    fun showLoading(load: Boolean)
    fun alarm(message: String)
}