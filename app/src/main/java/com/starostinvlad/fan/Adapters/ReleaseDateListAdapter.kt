package com.starostinvlad.fan.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.starostinvlad.fan.R
import kotlin.Throws

class ReleaseDateListAdapter(context: Context?) : RecyclerView.Adapter<ReleaseDateListAdapter.ViewHolder>() {
    private val TAG: String = javaClass.getSimpleName()
    private val mInflater: LayoutInflater
    private var itemList: MutableList<String>
    fun setItemList(infoList: MutableList<String>) {
        itemList = infoList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.serial_page_release_date_item, parent, false)
        Log.d(TAG, "createViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewBody.text = itemList[position]
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewBody: TextView = itemView.findViewById(R.id.serialPageInfoListItemBody)

    }

    init {
        mInflater = LayoutInflater.from(context)
        itemList = mutableListOf()
    }
}