package com.starostinvlad.fan.SearchScreen

import android.util.Log
import com.starostinvlad.fan.App
import com.starostinvlad.fan.BaseMVP.BasePresenter
import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.NewsScreen.NewsModel
import com.starostinvlad.fan.SearchedDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

internal class SearchFragmentPresenter : BasePresenter<SearchFragmentContract?>() {
    private val TAG: String = this::class.simpleName!!
    private var newsModel: NewsModel? = null
    private val searchedDao: SearchedDao = App.instance.database.searchedDao()
    private var loading = false
    override fun attachView(mvpView: SearchFragmentContract?) {
        super.attachView(mvpView)
        newsModel = NewsModel()
    }

    fun searchQuery(query: String) {
        if (!loading) {
            loading = true
            view!!.showLoading(true)
            disposables.add(
                    newsModel!!.search(query)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { arr ->
                                        if (arr!!.isEmpty()) {
                                            view!!.showMessage("По запросу \"$query\" ничего не найдено")
                                        } else {
                                            view!!.fillList(arr)
                                        }
                                        loading = false
                                        view!!.showLoading(false)
                                    }
                            ) { throwable: Throwable ->
                                throwable.printStackTrace()
                                loading = false
                                view!!.showLoading(false)
                            }
            )
        }
    }

    val history: Unit
        get() {
            Log.d(TAG, "get History")
            disposables.add(
                    searchedDao.all
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { news ->
                                        Log.d(TAG, "size: " + news.size)
                                        view!!.fillList(news as MutableList<News>)
                                    }) { obj: Throwable -> obj.printStackTrace() }
            )
        }

    fun addInHistory(searched: News) {
        disposables.add(
                searchedDao
                        .insert(searched)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                { id: Long? -> Log.d(TAG, "id: $id") }) { obj: Throwable -> obj.printStackTrace() }
        )
    }
}