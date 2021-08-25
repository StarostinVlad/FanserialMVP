package com.starostinvlad.fan.SearchScreen

import com.starostinvlad.fan.BaseMVP.MvpView
import com.starostinvlad.fan.GsonModels.News

interface SearchFragmentContract : MvpView {
    fun showLoading(show: Boolean)
    fun fillList(arr: MutableList<News>)
    fun showMessage(message: String?)
}