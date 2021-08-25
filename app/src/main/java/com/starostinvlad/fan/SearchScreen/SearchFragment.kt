package com.starostinvlad.fan.SearchScreen

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.starostinvlad.fan.Adapters.NewsWithAdRVAdapter
import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.R
import com.starostinvlad.fan.SerialPageScreen.SerialPageScreenActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment(), SearchFragmentContract {
    private val TAG: String = this::class.simpleName!!
    private var searchProgressBar: ProgressBar? = null
    private lateinit var searchListRV: RecyclerView
    private lateinit var searchListAdapter: NewsWithAdRVAdapter
    private var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null
    private var presenter: SearchFragmentPresenter? = null
    private val searchQuery = BehaviorSubject.createDefault("")
    private val LANDSCAPE_COUNT = 4
    private val PORTRAIT_COUNT = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        presenter?.detachView()
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container!!.context).inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchView = view.findViewById<SearchView>(R.id.searchviewBtn)
        searchProgressBar = view.findViewById(R.id.searchProgressBar)
        searchListRV = view.findViewById(R.id.searchRecycleView)
        staggeredGridLayoutManager = StaggeredGridLayoutManager(PORTRAIT_COUNT, StaggeredGridLayoutManager.VERTICAL)
        searchListRV.layoutManager = staggeredGridLayoutManager

        searchListAdapter = NewsWithAdRVAdapter()
        searchListAdapter.setOnItemClickListener(object : NewsWithAdRVAdapter.OnItemClickListener {

            override fun onItemClick(news: News) {
                presenter!!.addInHistory(news)
                //            VideoActivity.start(getActivity(), arr.get(i));
                SerialPageScreenActivity.start(activity, news)
            }
        })
        searchListRV.adapter = searchListAdapter
        presenter = SearchFragmentPresenter()
        presenter!!.attachView(this)
        presenter!!.history
        searchQuery
                .filter { s: String -> s.length > 3 }
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ query: String -> presenter!!.searchQuery(query) }) { obj: Throwable -> obj.printStackTrace() }
                .isDisposed
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                searchQuery.onNext(s)
                return false
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        staggeredGridLayoutManager!!.spanCount = if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) LANDSCAPE_COUNT else PORTRAIT_COUNT
        super.onConfigurationChanged(newConfig)
    }

    override fun showLoading(show: Boolean) {
        searchProgressBar!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun fillList(arr: MutableList<News>) {
        searchListAdapter.elements = arr
    }

    override fun showMessage(message: String?) {
        Snackbar.make(view!!, message!!, Snackbar.LENGTH_SHORT).show()
    }
}