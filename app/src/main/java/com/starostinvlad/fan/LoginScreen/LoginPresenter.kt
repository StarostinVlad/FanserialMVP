package com.starostinvlad.fan.LoginScreen

import android.util.Log
import com.starostinvlad.fan.App
import com.starostinvlad.fan.BaseMVP.BasePresenter
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

internal class LoginPresenter : BasePresenter<LoginFragmentContract?>() {
    private val TAG: String = this::class.simpleName!!
    fun loginApi(email: String, pass: String) {
        view!!.showLoading(true)
        disposables.add(
                login(email, pass)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                { document: Document ->
                                    val login = document.select("#login-box").attr("title")
                                    view!!.showLoading(false)
                                    if (login != "Авторизация") {
                                        App.instance.loginSubject.onNext(login)
                                        Log.d(TAG, "login: $login")
                                    } else {
                                        val errors = document.selectFirst(".berrors").text()
                                        Log.d(TAG, "loginApi: errors: $errors")
                                        view!!.alarm(errors)
                                    }
                                }
                        ) { e: Throwable ->
                            Log.d(TAG, "message: $e")
                            view!!.showLoading(false)
                            view!!.alarm(e.message!!)
                            e.printStackTrace()
                        }
        )
    }

    private fun login(email: String, pass: String): Maybe<Document> {
        return Maybe.create { emitter: MaybeEmitter<Document> ->
            try {
                val requestBody: RequestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("login_password", pass)
                        .addFormDataPart("login_name", email)
                        .addFormDataPart("login", "submit")
                        .build()
                val loginRequest: Request = Request.Builder()
                        .url(App.instance.domain)
                        .post(requestBody)
                        .build()
                val response: Response = App.instance.okHttpClient.newCall(loginRequest).execute()
                if (response.code() == 200 && response.body() != null) {
                    val doc = Jsoup.parse(response.body()!!.string())
                    Log.d(TAG, "doc: " + doc.body())
                    emitter.onSuccess(doc)
                }
            } catch (e: IOException) {
                emitter.onError(e)
            }
        }
    }

    fun registryApi(email: String, pass: String, name: String) {
        disposables.add(
                registry(email, pass, name)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response: String ->
                            if (!response.isEmpty()) {
                                if (response.contains("success")) loginApi(email, pass) else {
                                    view!!.alarm(response)
                                    view!!.showLoading(false)
                                }
                            }
                        }
                        ) { e: Throwable ->
                            Log.d(TAG, "message: $e")
                            view!!.showLoading(false)
                            view!!.alarm(e.message!!)
                            e.printStackTrace()
                        }
        )
    }

    private fun registry(email: String, pass: String, name: String): Maybe<String> {
        return Maybe.create { emitter: MaybeEmitter<String> ->
            val requestBody: RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("password", pass)
                    .addFormDataPart("email", email)
                    .addFormDataPart("name", name)
                    .build()
            val getSeriaPage: Request = Request.Builder()
                    .url("${App.instance.domain}/registration/")
                    .post(requestBody)
                    .build()
            try {
                val response: Response = App.instance.okHttpClient.newCall(getSeriaPage).execute()
                Log.d(TAG, "doc: " + response.body())
                if (response.code() == 200 && response.body() != null) {
                    Log.d(TAG, "doc: " + response.body())
                    emitter.onSuccess(response.body()!!.string())
                }
                emitter.onSuccess(response.message())
            } catch (e: IOException) {
                emitter.onError(e)
            }
        }
    }
}