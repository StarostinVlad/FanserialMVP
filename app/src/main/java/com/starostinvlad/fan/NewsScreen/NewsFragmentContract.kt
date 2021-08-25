package com.starostinvlad.fan.NewsScreen

import com.starostinvlad.fan.BaseMVP.MvpView
import com.starostinvlad.fan.GsonModels.News
import kotlin.Throws

interface NewsFragmentContract : MvpView {
    fun fillListView(newsList: List<News>)
    fun showLoading(show: Boolean)
    fun refreshListView()
    fun alarm(message: String?)
}