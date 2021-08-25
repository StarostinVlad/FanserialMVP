package com.starostinvlad.fan.NewsScreen

import com.starostinvlad.fan.BaseMVP.BasePresenter
import com.starostinvlad.fan.GsonModels.News
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

internal class NewsPresenter : BasePresenter<NewsFragmentContract?>() {
    private val TAG: String = this::class.simpleName!!
    private var newsModel: NewsModel? = null
    private var loading = false
    fun refreshNews() {
        newsModel = null
        loadNews()
    }

    fun addNews() {
        if (!loading) {
            startLoading()
            disposables.add(
                    newsModel!!.addNews()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ arr: List<News?>? ->
                                view!!.refreshListView()
                                endLoading()
                            }
                            ) { exception: Throwable ->
                                exception.printStackTrace()
                                endLoading()
                            }
            )
        }
    }

    fun loadNews() {
        if (!loading) {
            startLoading()
            if (newsModel == null) {
                newsModel = NewsModel()
                disposables.add(
                        newsModel!!.loadNews()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .retryWhen { throwableObservable: Flowable<Throwable?> -> throwableObservable.take(3).delay(1, TimeUnit.SECONDS) }
                                .subscribe({ arr ->
                                    newsModel!!.episodeList = arr
                                    view!!.fillListView(arr)
                                    endLoading()
                                }) { exception: Throwable ->
                                    endLoading()
                                    if (exception.message!!.contains("timeout")) view!!.alarm("Превышено время ожидания")
                                    exception.printStackTrace()
                                }
                )
            } else {
                view!!.fillListView(newsModel!!.episodeList)
                endLoading()
            }
        }
    }

    private fun startLoading() {
        loading = true
        view!!.showLoading(true)
    }

    private fun endLoading() {
        loading = false
        view!!.showLoading(false)
    }
}

private operator fun String.contains(s: String): Boolean {
    return s in this
}
