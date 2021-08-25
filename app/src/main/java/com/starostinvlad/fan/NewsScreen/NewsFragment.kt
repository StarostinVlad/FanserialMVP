package com.starostinvlad.fan.NewsScreen

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.starostinvlad.fan.Adapters.NewsWithAdRVAdapter
import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.R
import com.starostinvlad.fan.SerialPageScreen.SerialPageScreenActivity

class NewsFragment : Fragment(), NewsFragmentContract, NewsWithAdRVAdapter.OnItemClickListener {
    private val LANDSCAPE_COUNT = 4
    private val PORTRAIT_COUNT = 2
    private var presenter: NewsPresenter? = null
    private val TAG: String = this::class.simpleName!!
    private var recyclerView: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var newsRecyclerViewAdapter: NewsWithAdRVAdapter? = null
    private var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        presenter = NewsPresenter()
        presenter!!.attachView(this)
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val viewId = staggeredGridLayoutManager!!.findFirstCompletelyVisibleItemPositions(null)[0]
                Log.d(TAG, "onCreateView: last: $viewId")
                if (viewId >= newsRecyclerViewAdapter!!.itemCount * 0.5f) presenter!!.addNews()
            }
        })
        swipeRefreshLayout!!.setOnRefreshListener { presenter!!.refreshNews() }
        presenter!!.loadNews()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.news_list_id)
        staggeredGridLayoutManager = StaggeredGridLayoutManager(PORTRAIT_COUNT, StaggeredGridLayoutManager.VERTICAL)
        recyclerView?.layoutManager = staggeredGridLayoutManager
        newsRecyclerViewAdapter = NewsWithAdRVAdapter()
        newsRecyclerViewAdapter!!.setOnItemClickListener(this)
        //        AppodealWrapperAdapter appodealWrapperAdapter = new AppodealWrapperAdapter(newsRecyclerViewAdapter, 12);
        recyclerView?.adapter = newsRecyclerViewAdapter
        val uiMode = context!!.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (uiMode.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            Log.d(TAG, "initViews: is TV!")
            staggeredGridLayoutManager!!.spanCount = 5
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            staggeredGridLayoutManager!!.spanCount = LANDSCAPE_COUNT
        }
        swipeRefreshLayout = view.findViewById(R.id.news_swipe_refresh_id)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        staggeredGridLayoutManager!!.spanCount = if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) LANDSCAPE_COUNT else PORTRAIT_COUNT
        super.onConfigurationChanged(newConfig)
    }

    override fun fillListView(newsList: List<News>) {
        Log.d(TAG, "fillListView: newsList size: " + newsList.size)
        newsRecyclerViewAdapter!!.elements = newsList
    }

    override fun refreshListView() {
        newsRecyclerViewAdapter!!.notifyDataSetChanged()
    }

    override fun alarm(message: String?) {
        Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(R.color.red))
                .show()
    }

    override fun showLoading(show: Boolean) {
        swipeRefreshLayout!!.isRefreshing = show
    }

    override fun onItemClick(news: News) {
        Log.d(TAG, "click: " + news.title)
        SerialPageScreenActivity.start(context as Activity?, news)
    }
}