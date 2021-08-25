package com.starostinvlad.fan.VideoScreen.PlayerModel

import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.Collections.emptyList
import java.util.regex.Pattern
import kotlin.Throws

class SerialPlayer(DOMAIN: String, okHttpClient: OkHttpClient) {
    private val TAG: String = this::class.simpleName!!
    private var referer: String = "https://seriahd.tv"
    private var hash = ""
    private var DOMAIN: String = ""
    private val CLIENT: OkHttpClient?
    private var playerHost: String? = ""
    var title = ""
    var description = ""
    var releaseDates: MutableList<String> = mutableListOf()
    var infoList: MutableList<String> = mutableListOf()
    var isSubscribed = false
    private var pageId = ""
    fun getHlsObject(url: String?): Maybe<Hls?> {
        return Maybe.create { emitter: MaybeEmitter<Hls?> ->
            try {
                val document = loadHlsFromUrl(url!!)
                Log.d(TAG, "getHlsObject: url: $url")
                Log.d(TAG, "getHlsObject: doc: $document")
                if (url.contains("responce.php")) {
                    emitter.onSuccess(parseHlsFromSecondPlayerPage(document))
                } else {
                    emitter.onSuccess(parseHlsFromDefualtPlayerPage(document))
                }
            } catch (e: Exception) {
                emitter.onError(e)
            } finally {
                emitter.onComplete()
            }
        }
    }

    @Throws(IOException::class)
    private fun loadHlsFromUrl(url: String): Document {
        Log.d(TAG, "getHlsUrl: iframe: $url")
        val getSeriaPage = Request.Builder()
                .addHeader("referer", referer)
                .url(url)
                .get()
                .build()
        val response = CLIENT!!.newCall(getSeriaPage).execute()
        if (response.code() == 200) {
            return Jsoup.parse(response.body()!!.string())
        }
        throw NullPointerException()
    }

    private fun parseHlsFromSecondPlayerPage(document: Document): Hls {
        val playerJson = document.text()
        val jsonObject = JsonParser.parseString(playerJson).asJsonObject
        val src = jsonObject["src"].asString
        val enSub = jsonObject["en_sub"].asString
        val ruSub = jsonObject["ru_sub"].asString
        return Hls(src, enSub, ruSub)
    }

    private fun parseHlsFromDefualtPlayerPage(document: Document): Hls {
        val playerElement = document.selectFirst(".flowplayer")
        val playerJson = playerElement.attr("data-config")
        val src = JsonParser.parseString(playerJson).asJsonObject["hls"].asString
        val enSub = playerElement.attr("data-ru_subtitle")
        val ruSub = playerElement.attr("data-en_subtitle")
        return Hls(src, enSub, ruSub)
    }

    @Throws(IOException::class)
    private fun loadSerialPageFromUrl(url: String): Document? {
        val currentUrl = if (url.contains(DOMAIN)) url else DOMAIN + url
        Log.d(TAG, "domain: $DOMAIN url : $currentUrl")
        referer = currentUrl
        val getSeriaPage = Request.Builder()
                .addHeader("referer", currentUrl)
                .url(currentUrl)
                .get()
                .build()
        val response = CLIENT!!.newCall(getSeriaPage).execute()
        return if (response.code() == 200) {
            Jsoup.parse(
                    response
                            .body()!!
                            .string()
            )
        } else null
    }

    fun getSerial(url: String): Maybe<Serial?> {
        return Maybe.create { emitter: MaybeEmitter<Serial?> ->
            try {
                val document = loadSerialPageFromUrl(url)
                pageId = url.substring(url.lastIndexOf("/"))
                pageId = pageId.substring(1, pageId.indexOf("-"))
                Log.d(TAG, "loadSerialPageFromUrl: pageID: $pageId")
                hash = document!!.getElementsByAttributeValue("name", "user_hash").attr("value")
                title = document.getElementsByAttributeValue("property", "og:title").attr("content")
                Log.d(TAG, "title: $title")
                val descriptionNodes = document.selectFirst("div.fdesc.full-text.clearfix").textNodes()
                for (descriptionNode in descriptionNodes.subList(2, descriptionNodes.size)) {
                    description += descriptionNode.text()
                }
                Log.d(TAG, "description: $description")
                val serialInfoElements = document.select("div.sd-line")
                for (serialInfoElement in serialInfoElements) {
                    infoList.add(serialInfoElement.text())
                }
                Log.d(TAG, "serialInfo: $infoList")
                val releaseDateElements = document.select("tr.epscape_tr")
                for (date in releaseDateElements) {
                    releaseDates.add(date.text())
                }
                Log.d(TAG, "releaseDates: $releaseDates")
                isSubscribed = document.select(".fa.fa-star").hasClass("fav-added")
                //                \"scode_begin\":\"([^}]*?dew.*?)\"}
                val pattern = Pattern.compile("\"scode_begin\":\"([^}]*?dew.*?)\"")
                val matcher = pattern.matcher(document.html())
                val serial: Serial?
                if (matcher.find()) {
                    var result = matcher.group(1)
                    result = result.replace("ifr:", "http").replace("\\", "")
                    Log.d(TAG, "getSerial: EXIST SECOND PLAYER! $result")
                    serial = parseSerialFromSecondPlayer(getSecondPlayerPageFromUrl(result))
                    serial?.id = pageId
                } else {
                    serial = parseSerialFromDefautPlayer(document)
                    serial?.id = pageId
                    //                    throw new Exception("Данный сериал не поддерживается в приложении!");
                }
                emitter.onSuccess(serial!!)
            } catch (e: Exception) {
                emitter.onError(e)
            } finally {
                emitter.onComplete()
            }
        }
    }

    // TODO: 12.04.2021 оформить правильный нейминг
    // TODO: 12.04.2021 собрать все файлы для работы с сайтом в отдельный пакет бизнес логики
    @Throws(IOException::class)
    private fun getSecondPlayerPageFromUrl(url: String): Document? {
        val getSeriaPage = Request.Builder()
                .addHeader("referer", referer)
                .url(url)
                .get()
                .build()
        val response = CLIENT!!.newCall(getSeriaPage).execute()
        playerHost = Uri.parse(url).host
        var document: Document? = null
        if (response.code() == 200) {
            document = Jsoup.parse(response.body()!!.string())
        }
        return document
    }

    private fun parseSerialFromSecondPlayer(document: Document?): Serial? {
        val inputData = document!!.selectFirst("#inputData")
        var serial: Serial? = null
        if (inputData != null && !inputData.text().isEmpty()) {
//                Log.d(TAG, "getHlsUrl: inputData: " + inputData.text());
            val id: Int = inputData.attr("data-playlist").toInt()
            val gson = GsonBuilder()
                    .setPrettyPrinting()
                    .setLenient()
                    .registerTypeAdapter(Serial::class.java, SecondPlayerDataDeserializer(id, playerHost))
                    .create()
            serial = gson.fromJson(inputData.text(), Serial::class.java)
        }
        return serial
    }

    private fun parseSerialFromDefautPlayer(document: Document?): Serial? {
        val playerJson = document!!.select("script:nth-child(3)").html()
        val pattern = Pattern.compile("init\\((.*?)\\);")
        val matcher = pattern.matcher(playerJson)
        var serial: Serial? = null
        if (matcher.find()) {
            var result = matcher.group(1)
            result = "[$result]"
            val path = Environment.getExternalStorageDirectory().toString() + "/player.json"
            Log.d(TAG, "parseSerialFromDefautPlayer: path: $path")
            try {
                FileOutputStream(path).use { fileOutputStream ->
                    val arr: ByteArray = result.toByteArray()
                    fileOutputStream.write(arr)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                Log.d(TAG, "deserialize: file writed!")
            }
            Log.d(TAG, "getPlayerList: " + result.substring(result.length - 50))
            val gson = GsonBuilder()
                    .setPrettyPrinting()
                    .setLenient()
                    .registerTypeAdapter(Serial::class.java, PlayerDataDeserializer())
                    .create()
            serial = gson.fromJson(result, Serial::class.java)
            Log.d(TAG, "getSerial: serial: $serial")
        }
        return serial
    }

    fun subscribeRequest(id: String?, check: Boolean): Maybe<String?> {
        return Maybe.create { emitter: MaybeEmitter<String?> ->
            val action = if (check) "plus" else "minus"
            val request = Request.Builder()
                    .url("$DOMAIN/engine/ajax/controller.php?mod=favorites&fav_id=$id&action=$action&skin=seriahd&alert=1&user_hash=$hash")
                    .get()
                    .build()
            try {
                val response = CLIENT!!.newCall(request).execute()
                if (response.code() == 200) {
                    val doc = Jsoup.parse(response.body()!!.string())
                    emitter.onSuccess(doc.body().html())
                }
            } catch (e: IOException) {
                emitter.onError(e)
            } finally {
                emitter.onComplete()
            }
        }
    }


    init {
        this.DOMAIN = DOMAIN
        CLIENT = okHttpClient
    }
}