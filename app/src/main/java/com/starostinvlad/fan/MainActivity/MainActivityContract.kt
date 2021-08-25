package com.starostinvlad.fan.MainActivity

import androidx.fragment.app.Fragment
import java.util.*
import kotlin.Throws

interface MainActivityContract {
    fun setActiveFragment(i: Int)
    fun fillPagers(fragments: ArrayList<Fragment>)
}