package com.starostinvlad.fan.ViewedScreen

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.starostinvlad.fan.BaseMVP.BasePresenter
import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.NewsScreen.NewsModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal class ViewedPresenter : BasePresenter<ViewedFragmentContract?>() {
    private val TAG: String = this::class.simpleName!!
    private var newsModel: NewsModel? = null
    override fun attachView(mvpView: ViewedFragmentContract?) {
        super.attachView(mvpView)
        newsModel = NewsModel()
    }

    fun loadData() {
        view!!.showLoading(true)
        view!!.showButton(false)
        disposables.add(newsModel!!.favorites
                .subscribeOn(Schedulers.io())
                .doAfterSuccess { news: List<News?>? -> updateSubcribtions(news) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { `val`: List<News?>? ->
                            view!!.fillList(`val`)
                            view!!.showLoading(false)
                        }
                ) { e: Throwable ->
                    e.printStackTrace()
                    view!!.showLoading(false)
                    view!!.showButton(true)
                })
    }

    private fun updateSubcribtions(news: List<News?>?) {
        disposables.add(Observable.just(news)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapIterable { `val`: List<News?>? -> `val` }
                .doOnNext { new: News? -> Log.d(TAG, "updateSubcribtions: " + FirebaseMessaging.getInstance().subscribeToTopic(new!!.siteId!!).isSuccessful) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { `val`: News? -> Log.d(TAG, "updateSubcribtions successed: $`val`") }) { obj: Throwable -> obj.printStackTrace() })
    }
}