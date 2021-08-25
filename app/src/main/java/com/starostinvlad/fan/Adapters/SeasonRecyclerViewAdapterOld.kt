package com.starostinvlad.fan.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.starostinvlad.fan.GsonModels.Episode
import com.starostinvlad.fan.R
import kotlin.Throws

class SeasonRecyclerViewAdapterOld(context: Context?, episodes: List<Episode>) : RecyclerView.Adapter<SeasonRecyclerViewAdapterOld.ViewHolder>() {
    private val TAG: String = javaClass.getSimpleName()
    private val episodes: List<Episode>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.episodes_in_videoscreen_item, parent, false)
        Log.d(TAG, "createViewHolder")
        return ViewHolder(view)
    }

    // binds the data to the view and textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder")
        val episode = episodes[position]
        holder.myTextView.text = episode.name
    }

    // total number of rows
    override fun getItemCount(): Int {
        return episodes.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var myView: CardView
        var myTextView: TextView
        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            myView = itemView.findViewById(R.id.card_item_id)
            myTextView = itemView.findViewById(R.id.tvAnimalName)
            itemView.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): Episode {
        return episodes[id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        this.episodes = episodes
        Log.d(TAG, "initialize")
    }
}