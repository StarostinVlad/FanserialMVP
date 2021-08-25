package com.starostinvlad.fan.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.starostinvlad.fan.R
import com.starostinvlad.fan.VideoScreen.PlayerModel.Episode

class SeasonRecyclerViewAdapter() : RecyclerView.Adapter<SeasonRecyclerViewAdapter.ViewHolder>() {
    private val TAG: String = this::class.simpleName!!
    var items = mutableListOf<Episode>()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    var mClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.episodes_in_videoscreen_item, parent, false)
        Log.d(TAG, "createViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder")
        holder.myTextView.text = items[position].title
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var myTextView: TextView = itemView.findViewById(R.id.tvAnimalName)
        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.invoke(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
            itemView.onFocusChangeListener = OnFocusChangeListener { _: View?, b: Boolean ->
                if (b) {
                    itemView.animate().scaleY(1.2f).scaleX(1.2f).z(1.2f).start()
                } else {
                    itemView.animate().scaleY(1f).scaleX(1f).z(1f).start()
                }
            }
        }
    }

}