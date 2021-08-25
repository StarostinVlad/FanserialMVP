package com.starostinvlad.fan.Adapters

import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.appodeal.ads.Appodeal
import com.appodeal.ads.NativeAd
import com.appodeal.ads.NativeCallbacks
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed
import com.starostinvlad.fan.R

/**
 * Wrapper adapter to show Native Ad in recycler view with fixed step
 */
class AppodealWrapperAdapter(userAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, nativeStep: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), NativeCallbacks {
    private val TAG: String = this::class.simpleName!!
    private val userAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?
    private var nativeStep = DEFAULT_NATIVE_STEP
    private val nativeAdList: SparseArray<NativeAd?>? = SparseArray()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_HOLDER_NATIVE_AD_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.news_gridview_ad_item, parent, false)
            AdViewHolder(view)
        } else {
            userAdapter!!.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AdViewHolder) {
            val lp = holder.itemView.layoutParams
            val sglp = lp as StaggeredGridLayoutManager.LayoutParams
            sglp.isFullSpan = true
            holder.itemView.layoutParams = sglp
            holder.fill(nativeAdList!![position])
        } else {
            userAdapter!!.onBindViewHolder(holder, getPositionInUserAdapter(position))
        }
    }

    override fun getItemCount(): Int {
        var resultCount = 0
        resultCount += nativeAdsCount
        resultCount += userAdapterItemCount
        return resultCount
    }

    override fun getItemViewType(position: Int): Int {
        return if (isNativeAdPosition(position)) {
            VIEW_HOLDER_NATIVE_AD_TYPE
        } else {
            userAdapter!!.getItemViewType(getPositionInUserAdapter(position))
        }
    }

    /**
     * Destroy all used native ads
     */
    fun destroyNativeAds() {
        if (nativeAdList != null) {
            for (i in 0..nativeAdList.size()) {
                val nativeAd = nativeAdList.valueAt(i)
                nativeAd!!.destroy()
            }
            nativeAdList.clear()
        }
    }

    override fun onNativeLoaded() {
        Log.d(TAG, "onNativeLoaded: native LOADED!")
        fillListWithAd()
    }

    override fun onNativeFailedToLoad() {
        Log.d(TAG, "onNativeLoaded: native NOT LOADED!")
    }

    override fun onNativeShown(nativeAd: NativeAd) {}
    override fun onNativeShowFailed(nativeAd: NativeAd) {
        Log.e(TAG, "onNativeShowFailed: show error!")
    }

    override fun onNativeClicked(nativeAd: NativeAd) {}
    override fun onNativeExpired() {
        Log.d(TAG, "onNativeLoaded: native EXPIRED!")
    }

    /**
     * @return count of loaded ads [com.appodeal.ads.NativeAd]
     */
    private val nativeAdsCount: Int
        private get() = nativeAdList?.size() ?: 0

    /**
     * @return user items count
     */
    private val userAdapterItemCount: Int
        private get() = userAdapter?.itemCount ?: 0

    /**
     * @param position index in wrapper adapter
     * @return `true` if item by position is [com.appodeal.ads.NativeAd]
     */
    private fun isNativeAdPosition(position: Int): Boolean {
        return nativeAdList!![position] != null
    }

    /**
     * Method for searching position in user adapter
     *
     * @param position index in wrapper adapter
     * @return index in user adapter
     */
    private fun getPositionInUserAdapter(position: Int): Int {
        return position - Math.min(nativeAdList!!.size(), position / nativeStep)
    }

    /**
     * Method for filling list with [com.appodeal.ads.NativeAd]
     */
    private fun fillListWithAd() {
        var insertPosition = findNextAdPosition()
        var nativeAd: NativeAd? = nativeAdItem
        while (canUseThisPosition(insertPosition) && nativeAd != null) {
            nativeAdList!!.put(insertPosition, nativeAd)
            notifyItemInserted(insertPosition)
            insertPosition = findNextAdPosition()
        }
    }

    /**
     * Get native ad item
     *
     * @return [com.appodeal.ads.NativeAd]
     */
    private val nativeAdItem: NativeAd?
        private get() {
            val ads = Appodeal.getNativeAds(1)
            return if (!ads.isEmpty()) ads[0] else null
        }

    /**
     * Method for finding next position suitable for [com.appodeal.ads.NativeAd]
     *
     * @return position for next native ad view
     */
    private fun findNextAdPosition(): Int {
        return if (nativeAdList!!.size() > 0) {
            nativeAdList.keyAt(nativeAdList.size() - 1) + nativeStep
        } else nativeStep - 1
    }

    /**
     * @param position index in wrapper adapter
     * @return `true` if you can add [com.appodeal.ads.NativeAd] to this position
     */
    private fun canUseThisPosition(position: Int): Boolean {
        return nativeAdList!![position] == null && itemCount > position
    }

    internal class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nav_nf: NativeAdViewNewsFeed
        fun fill(nativeAd: NativeAd?) {
            nav_nf.setNativeAd(nativeAd)
        }

        init {
            nav_nf = itemView.findViewById(R.id.native_ad_view_news_feed)
        }
    }

    companion object {
        private const val DEFAULT_NATIVE_STEP = 5
        private const val VIEW_HOLDER_NATIVE_AD_TYPE = 600
    }

    /**
     * @param userAdapter user adapter
     * @param nativeStep  step show [com.appodeal.ads.NativeAd]
     */
    init {
        this.userAdapter = userAdapter
        this.nativeStep = nativeStep + 1
        userAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                notifyDataSetChanged()
                fillListWithAd()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                notifyDataSetChanged()
                fillListWithAd()
            }
        })
        Appodeal.setNativeCallbacks(this)
        fillListWithAd()
    }
}