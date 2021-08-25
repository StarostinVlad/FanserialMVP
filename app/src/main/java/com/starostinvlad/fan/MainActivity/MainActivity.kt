package com.starostinvlad.fan.MainActivity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.starostinvlad.fan.Adapters.SlidePagerAdapter
import com.starostinvlad.fan.App
import com.starostinvlad.fan.LoginScreen.LoginFragment
import com.starostinvlad.fan.NewsScreen.NewsFragment
import com.starostinvlad.fan.R
import com.starostinvlad.fan.SearchScreen.SearchFragment
import com.starostinvlad.fan.SettingsScreen.SettingsFragment
import com.starostinvlad.fan.ViewedScreen.ViewedFragment
import io.reactivex.functions.Consumer
import java.util.*

class MainActivity : AppCompatActivity(), MainActivityContract {
    private val TAG: String = this::class.simpleName!!
    private var pager: ViewPager? = null
    private var bottomNavigationView: BottomNavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pager = findViewById(R.id.fragment_container_id)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        App.instance.loginSubject.subscribe({ token: String? ->
            Log.d(TAG, "onCreate: token: $token")
            fillFragments(token)
            activeFragment(bottomNavigationView!!.selectedItemId, 0)
        }, { obj: Throwable -> obj.printStackTrace() }).isDisposed()
        pager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                bottomNavigationView!!
                        .setSelectedItemId(
                                bottomNavigationView!!
                                        .getMenu()
                                        .getItem(i)
                                        .itemId
                        )
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
        bottomNavigationView!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
            activeFragment(
                    item.itemId,
                    bottomNavigationView!!.getSelectedItemId()
            )
        })
    }

    fun activeFragment(id: Int, selected_id: Int): Boolean {
        Log.d(TAG, "selected item: $id")
        if (id == selected_id) return false
        when (id) {
            R.id.navigation_news -> {
                setActiveFragment(0)
                return true
            }
            R.id.navigation_search -> {
                setActiveFragment(1)
                return true
            }
            R.id.navigation_subscribtions -> {
                setActiveFragment(2)
                return true
            }
            R.id.navigation_settings -> {
                setActiveFragment(3)
                return true
            }
        }
        return false
    }

    fun fillFragments(token: String?) {
        val fragments = ArrayList<Fragment>()
        fragments.add(NewsFragment())
        if (TextUtils.isEmpty(token)) {
            fragments.add(SearchFragment())
            fragments.add(LoginFragment())
        } else {
            fragments.add(SearchFragment())
            fragments.add(ViewedFragment())
        }
        fragments.add(SettingsFragment())
        fillPagers(fragments)
    }

    override fun fillPagers(fragments: ArrayList<Fragment>) {
        val pagerAdapter = SlidePagerAdapter(
                supportFragmentManager,
                fragments.size,
                fragments
        )
        pager!!.adapter = pagerAdapter
    }

    override fun setActiveFragment(i: Int) {
        pager!!.setCurrentItem(i, true)
    }

    companion object {
        fun start(activity: AppCompatActivity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }
}