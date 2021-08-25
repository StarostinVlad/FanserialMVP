package com.starostinvlad.fan.ViewedScreen

import com.starostinvlad.fan.BaseMVP.MvpView
import com.starostinvlad.fan.GsonModels.News
import kotlin.Throws

interface ViewedFragmentContract : MvpView {
    fun fillList(viewedList: List<News?>?)
    fun showLoading(show: Boolean)
    fun showButton(show: Boolean)
}