package com.starostinvlad.fan.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*
import kotlin.Throws

class SlidePagerAdapter(fm: FragmentManager, behavior: Int, private val fragmentArrayList: ArrayList<Fragment>) : FragmentStatePagerAdapter(fm, behavior) {
    override fun getItem(position: Int): Fragment {
        return fragmentArrayList[position]
    }

    override fun getCount(): Int {
        return fragmentArrayList.size
    }
}