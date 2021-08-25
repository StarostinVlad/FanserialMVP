package com.starostinvlad.fan.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.starostinvlad.fan.App
import com.starostinvlad.fan.GsonModels.Episode
import com.starostinvlad.fan.GsonModels.Images
import com.starostinvlad.fan.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import java.util.*
import java.util.Collections.emptyList

class SerialRecyclerViewAdapter(context: Context?) : RecyclerView.Adapter<SerialRecyclerViewAdapter.ViewHolder>() {
    var itemList: MutableList<String> = emptyList()
        set(value) {
            field.addAll(value)
        }
    private val TAG: String = this::class.simpleName!!
    private val seasonEpisodes: MutableList<List<Episode?>?>
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.season_list_item, parent, false)
        Log.d(TAG, "createViewHolder")
        return ViewHolder(view)
    }

    // binds the data to the view and textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder")
        holder.titleView.text = "Сезон ${(position + 1)}"
        val layoutManager = LinearLayoutManager(holder.recyclerView.context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        holder.recyclerView.layoutManager = layoutManager
        if (position < seasonEpisodes.size) holder.recyclerView.adapter = getAdapter(holder.recyclerView.context, seasonEpisodes[position]) else Observable.fromCallable { getseasonEpisodeList(itemList!![position]) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ `val`: ArrayList<Episode?>? ->
                    Log.d(TAG, "val length: " + `val`!!.size)
                    seasonEpisodes.add(`val`)
                    holder.recyclerView.adapter = getAdapter(holder.recyclerView.context, `val`)
                }) { obj: Throwable -> obj.printStackTrace() }
    }

    private fun getAdapter(context: Context, episodes: List<Episode?>?): EpisodeRecyclerViewAdapter {
        //        myRecyclerViewAdapter.setClickListener(
//                (view1, position) -> VideoActivity.start((Activity) context, episodes.get(position))
//        );
        return EpisodeRecyclerViewAdapter(context, episodes)
    }

    private fun getseasonEpisodeList(url: String): ArrayList<Episode?>? {
        Log.d(TAG, "url: $url")
        val getSeriaPage = Request.Builder().url(url).get().build()
        try {
            val response: Response = App.instance.okHttpClient.newCall(getSeriaPage).execute()
            val doc = Jsoup.parse(response.body()!!.string())
            val elements = doc.select("#episode_list > li > div > div")
            //#episode_list > div:nth-child(1) > ul > li:nth-child(1) > div > div > div.serial-top > div.field-img
            val episodes = ArrayList<Episode?>()
            for (element in elements) {
                val episode = Episode()
                var sup = element.select(".serial-bottom > div.field-description > a").first()
                episode.name = sup.text()
                episode.url = sup.attr("href")
                sup = element.select(".serial-top > div.field-img").first()
                val src = sup.attr("style")
                val images = Images()
                images.small = src.subSequence(23, src.length - 3).toString()
                episode.images = images
                episodes.add(episode)
                Log.d(TAG, "episode: " + element.text())
            }
            Collections.reverse(episodes)
            return episodes
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "error: " + e.message)
        }
        return null
    }

    // total number of rows
    override fun getItemCount(): Int {
        return itemList.size
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): List<Episode?>? {
        return seasonEpisodes[id]
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleView: TextView = itemView.findViewById(R.id.season_title)
        var recyclerView: RecyclerView = itemView.findViewById(R.id.season_body)

    }

    // data is passed into the constructor
    init {
        seasonEpisodes = ArrayList()
        Log.d(TAG, "initialize")
    }
}