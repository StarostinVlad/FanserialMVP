package com.starostinvlad.fan.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.starostinvlad.fan.GsonModels.Episode
import com.starostinvlad.fan.R

class EpisodeRecyclerViewAdapter internal constructor(context: Context?, episodes: List<Episode?>?) : RecyclerView.Adapter<EpisodeRecyclerViewAdapter.ViewHolder>() {
    private val TAG: String = this::class.simpleName!!
    private val episodes: List<Episode?>?
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_item_with_image, parent, false)
        Log.d(TAG, "createViewHolder")
        return ViewHolder(view)
    }

    // binds the data to the view and textview in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder")
        val episode = episodes!![position]
        holder.myTextView.text = episode?.name
        Picasso.get()
                .load(episode?.images?.small) //.transform(App.getInstance().isReview() ? new BlurTransformation(holder.itemView.getContext()) : new CropSquareTransformation())
                .placeholder(R.color.cardview_dark_background)
                .into(holder.imageView)
    }

    // total number of rows
    override fun getItemCount(): Int {
        return episodes!!.size
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): Episode? {
        return episodes!![id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var myView: CardView
        var myTextView: TextView
        var imageView: ImageView
        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            myView = itemView.findViewById(R.id.card_item_id)
            myTextView = itemView.findViewById(R.id.tvAnimalName)
            imageView = itemView.findViewById(R.id.episode_poster)
            itemView.setOnClickListener(this)
        }
    }

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        this.episodes = episodes
        Log.d(TAG, "initialize")
    }
}