package com.starostinvlad.fan.NewsScreen

import android.util.Log
import com.starostinvlad.fan.App
import com.starostinvlad.fan.GsonModels.News
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jsoup.Jsoup
import java.util.*
import java.util.Collections.emptyList

class NewsModel {
    private val TAG: String = this::class.simpleName!!
    var episodeList: MutableList<News> = emptyList()
    private var page = 2
    private val CLIENT: OkHttpClient? = App.instance.okHttpClient
    fun addNews(): Maybe<MutableList<News>> {
        return loadNewsList("${App.instance.domain}/page/$page/")
                .doOnSuccess { news ->
                    page++
                    episodeList.addAll(news)
                }
    }

    fun loadNews(): Maybe<MutableList<News>> {
        return loadNewsList(App.instance.domain)
                .doOnSuccess { news -> episodeList = news }
    }

    private fun loadNewsList(url: String): Maybe<MutableList<News>> {
        Log.d(TAG, "loadNewsList: url: $url")
        return Maybe.create { emitter: MaybeEmitter<MutableList<News>> ->
            try {
                val newsPageRequest = Request.Builder()
                        .url(url)
                        .get()
                        .build()
                val response = CLIENT!!.newCall(newsPageRequest).execute()
                if (response.code() == 200 && response.body() != null && !emitter.isDisposed) emitter.onSuccess(
                        parseDocumentToNews(response.body()!!.string())
                )
            } catch (e: Exception) {
                e.printStackTrace()
                if (!emitter.isDisposed) emitter.onError(e)
            }
        }
    }

    fun search(query: String?): Maybe<MutableList<News>?> {
        return Maybe.create { emitter: MaybeEmitter<MutableList<News>?> ->
            try {
                val requestBody: RequestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("do", "search")
                        .addFormDataPart("subaction", "search")
                        .addFormDataPart("story", query)
                        .build()
                val newsPageRequest: Request = Request.Builder()
                        .url("${App.instance.domain}/index.php?do=search")
                        .post(requestBody)
                        .build()
                val response = CLIENT!!.newCall(newsPageRequest).execute()
                if (response.code() == 200 && response.body() != null && !emitter.isDisposed) emitter.onSuccess(
                        parseDocumentToNews(response.body()!!.string())
                )
            } catch (e: Exception) {
                e.printStackTrace()
                if (!emitter.isDisposed) emitter.onError(e)
            }
        }
    }

    private fun parseDocumentToNews(responseBody: String): MutableList<News> {
        val document = Jsoup.parse(responseBody)
        val elements = document.select("#dle-content > div.short.clearfix.with-mask")
        val newsList: MutableList<News> = mutableListOf()
        for (element in elements) {
            val title = element.select("a.short-title").text()
            val subTitleElements = element.select("div.short-text > div.short-desc > div.sd-line2")
            val subTitle = if (subTitleElements.size == 4) subTitleElements[1].text() else subTitleElements[2].text()
            val image: String = App.instance.domain + element.select("div.short-img.img-box > img").attr("src")
            val href = element.select("div.short-text > a").attr("href")
            var siteId: String = href.substring(href.lastIndexOf("/"))
            siteId = siteId.substring(1, siteId.indexOf("-"))
            val news = News(title, subTitle, image, href, siteId)
            newsList.add(news)
            //            Log.d(TAG, "loadpage: " + element.text());
        }
        return newsList
    }

    val favorites: Maybe<List<News?>?>
        get() = Maybe.create { emitter: MaybeEmitter<List<News?>?> ->
            try {
                val favoritesPageRequest: Request = Request.Builder()
                        .url("${App.instance.domain}/favorites")
                        .get()
                        .build()
                val response = CLIENT!!.newCall(favoritesPageRequest).execute()
                if (response.code() == 200 && response.body() != null && !emitter.isDisposed) emitter.onSuccess(parseDocumentToNews(response.body()!!.string()))
            } catch (e: Exception) {
                e.printStackTrace()
                if (!emitter.isDisposed) emitter.onError(e)
            }
        }
}