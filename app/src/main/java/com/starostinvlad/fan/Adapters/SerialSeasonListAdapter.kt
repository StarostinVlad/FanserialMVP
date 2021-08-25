package com.starostinvlad.fan.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.starostinvlad.fan.R
import com.starostinvlad.fan.VideoScreen.PlayerModel.Season
import java.util.*

class SerialSeasonListAdapter : RecyclerView.Adapter<SerialSeasonListAdapter.ViewHolder>() {
    private val TAG: String = this::class.simpleName!!
    private val mInflater: LayoutInflater? = null
    private val itemList: MutableList<Season?> = ArrayList()
    var itemClickListener: ((Int) -> Unit)? = null

    fun setItemList(infoList: List<Season?>?) {
        itemList.clear()
        itemList.addAll(infoList!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.serial_page_season_list_item, parent, false)
        Log.d(TAG, "createViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = "${itemList[position]?.number.toString()} сезон"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var textView: TextView
        override fun onClick(view: View) {
            if (itemClickListener != null) {
                itemClickListener!!.invoke(adapterPosition)
            }
        }

        init {
            textView = itemView.findViewById(R.id.serialPageInfoListSeasonTitle)
            itemView.onFocusChangeListener = OnFocusChangeListener { view: View?, b: Boolean ->
                if (b) {
                    itemView.animate().scaleY(1.2f).scaleX(1.2f).z(1.2f).start()
                } else {
                    itemView.animate().scaleY(1f).scaleX(1f).z(1f).start()
                }
            }
            itemView.setOnClickListener(this)
        }
    }
}