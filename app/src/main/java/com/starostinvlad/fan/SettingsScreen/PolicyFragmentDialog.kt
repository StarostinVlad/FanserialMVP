package com.starostinvlad.fan.SettingsScreen

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.starostinvlad.fan.R
import kotlin.Throws

class PolicyFragmentDialog : BottomSheetDialogFragment() {
    private val TAG: String = javaClass.getSimpleName()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pp, container, false)
        val textView = view.findViewById<TextView>(R.id.policy_text)
        var policy = ""
        if (activity != null && arguments!!.containsKey("type")) {
            if (arguments!!.getByte("type").toInt() == 1) {
                policy = getString(R.string.private_policy)
                Log.d(TAG, "onCreate: type 1")
            } else {
                policy = getString(R.string.content_policy)
                Log.d(TAG, "onCreate: type 2")
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(policy, Html.FROM_HTML_MODE_LEGACY)
        } else textView.text = Html.fromHtml(policy)
        return view
    }

    companion object {
        fun newInstance(): PolicyFragmentDialog {
            val args = Bundle()
            val fragment = PolicyFragmentDialog()
            fragment.arguments = args
            return fragment
        }
    }
}