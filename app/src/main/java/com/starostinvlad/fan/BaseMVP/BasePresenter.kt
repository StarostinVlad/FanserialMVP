package com.starostinvlad.fan.BaseMVP

import io.reactivex.disposables.CompositeDisposable
import kotlin.Throws

abstract class BasePresenter<T : MvpView?> : MvpPresenter<T> {
    protected var disposables = CompositeDisposable()
    protected var view: T? = null
    override fun attachView(mvpView: T) {
        view = mvpView
    }

    override fun detachView() {
        view = null
        disposables.dispose()
    }
}