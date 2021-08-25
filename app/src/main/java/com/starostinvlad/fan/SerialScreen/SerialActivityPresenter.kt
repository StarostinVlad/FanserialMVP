package com.starostinvlad.fan.SerialScreen

import android.os.Build
import android.util.Log
import com.starostinvlad.fan.App
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*
import java.util.Collections.emptyList
import java.util.function.Function
import java.util.stream.Collectors

class SerialActivityPresenter(var view: SerialActivityContract) {
    private val TAG: String = this::class.simpleName!!
    private var seasonUrlList: MutableList<String> = emptyList()
    fun loadData(url: String) {
        if (seasonUrlList.isEmpty()) Observable.fromCallable { loadPage(url) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) //                .flatMapIterable(val->val)
                //                .map(this::getseasonEpisodeList)
                .subscribe { strings: List<String> -> fillSeasonList(strings) } else view.fillList(seasonUrlList)
    }

    private fun fillSeasonList(strings: List<String>) {
        for (string in strings) {
            Log.d(TAG, "season: $string")
        }
        seasonUrlList = strings as MutableList<String>
        view.fillList(strings)
    }

    private fun loadPage(url: String): List<String> {
        val getSeriaPage = Request.Builder().url(url).get().build()
        try {
            val response: Response = App.instance.okHttpClient.newCall(getSeriaPage).execute()
            if (response.code() == 200 && response.body() != null) {
                val cookies = response.header("Set-Cookie")
                val doc = Jsoup.parse(response.body()!!.string())
                val elements = doc.select("body > div.wrapper > main > div > div.row > div > div > div > div.page-header > div.box-serial-seasons > div > ul > li > a")
                var seasons: MutableList<String> = emptyList()
                if (elements.isEmpty()) {
                    seasons = ArrayList()
                    seasons.add(url)
                    return seasons
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seasons = elements.stream().map { element -> App.instance.domain + element.attr("href") }.collect(Collectors.toList())
                    }
                }
                return seasons
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "error: " + e.message)
        }
        return emptyList()
    }
}