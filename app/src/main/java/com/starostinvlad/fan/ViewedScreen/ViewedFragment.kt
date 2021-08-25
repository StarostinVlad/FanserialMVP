package com.starostinvlad.fan.ViewedScreen

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.starostinvlad.fan.Adapters.ViewedListAdapter
import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.R
import com.starostinvlad.fan.SerialPageScreen.SerialPageScreenActivity

class ViewedFragment : Fragment(), ViewedFragmentContract, ViewedListAdapter.OnItemClickListener {
    private var progressBar: ProgressBar? = null
    private lateinit var reload_btn: Button
    private var presenter: ViewedPresenter? = null
    private var viewedListAdapter: ViewedListAdapter? = null
    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        if (presenter != null) {
            presenter!!.detachView()
        }
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation) staggeredGridLayoutManager!!.spanCount = 2 else staggeredGridLayoutManager!!.spanCount = 1
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(container!!.context).inflate(R.layout.fragment_viewed, container, false)
        retainInstance = true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        staggeredGridLayoutManager = StaggeredGridLayoutManager(PORTRAIT_COUNT, StaggeredGridLayoutManager.VERTICAL)
        val nextEpisodeRV: RecyclerView = view.findViewById(R.id.nextEpisodeRV)
        nextEpisodeRV.layoutManager = staggeredGridLayoutManager
        viewedListAdapter = ViewedListAdapter()
        nextEpisodeRV.adapter = viewedListAdapter
        progressBar = view.findViewById(R.id.progress_viewed_list)
        reload_btn = view.findViewById(R.id.reload_viewed_list)
        reload_btn.setOnClickListener(View.OnClickListener { v: View? -> presenter!!.loadData() })
        presenter = ViewedPresenter()
        presenter!!.attachView(this)
        val uiMode = context!!.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (uiMode.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            staggeredGridLayoutManager!!.spanCount = 3
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            staggeredGridLayoutManager!!.spanCount = LANDSCAPE_COUNT
        }
        viewedListAdapter!!.setOnItemClickListener(this)
        presenter!!.loadData()
    }

    override fun fillList(viewedList: List<News?>?) {
        viewedListAdapter!!.setViewedList(viewedList)
    }

    override fun showLoading(show: Boolean) {
        progressBar!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showButton(show: Boolean) {
        reload_btn.visibility = if (show) View.VISIBLE else View.GONE
    }

    companion object {
        private const val PORTRAIT_COUNT = 1
        private const val LANDSCAPE_COUNT = 2
    }

    override fun onClick(news: News) {
        SerialPageScreenActivity.start(context as AppCompatActivity?, news)
    }
}