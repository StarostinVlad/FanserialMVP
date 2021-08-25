package com.starostinvlad.fan.BaseMVP

import kotlin.Throws

interface MvpPresenter<V> {
    fun attachView(mvpView: V)
    fun detachView()
}