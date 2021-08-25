package com.starostinvlad.fan.SettingsScreen

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.starostinvlad.fan.App
import com.starostinvlad.fan.R

class SettingsFragment : Fragment() {
    private val TAG: String = this::class.simpleName!!
    private lateinit var logout: Button
    private lateinit var privatePolicy: Button
    private lateinit var contentPolicy: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        logout = view.findViewById(R.id.btn_logout)
        privatePolicy = view.findViewById(R.id.private_policy)
        contentPolicy = view.findViewById(R.id.content_policy)
        privatePolicy.setOnClickListener { v: View? ->
            Log.d(TAG, "onCreateView: click private!")
            val policyFragmentDialog: PolicyFragmentDialog = PolicyFragmentDialog.newInstance()
            val bundle = Bundle()
            bundle.putByte("type", 1.toByte())
            policyFragmentDialog.arguments = bundle
            policyFragmentDialog.show(activity!!.supportFragmentManager,
                    "add_photo_dialog_fragment")
        }
        contentPolicy.setOnClickListener { v: View? ->
            Log.d(TAG, "onCreateView: click content!")
            val policyFragmentDialog: PolicyFragmentDialog = PolicyFragmentDialog.Companion.newInstance()
            val bundle = Bundle()
            bundle.putByte("type", 2.toByte())
            policyFragmentDialog.arguments = bundle
            policyFragmentDialog.show(activity!!.supportFragmentManager,
                    "add_photo_dialog_fragment")
        }
        if (App.instance.loginSubject.value!!.isEmpty()) {
            disableLogout()
        } else {
            logout.setOnClickListener { v: View? ->
                val cookieJar: ClearableCookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
                cookieJar.clear()
                App.instance.loginSubject.onNext("")
                disableLogout()
            }
        }
        return view
    }

    private fun disableLogout() {
        logout!!.isEnabled = false
        logout!!.setBackgroundColor(Color.DKGRAY)
    }
}