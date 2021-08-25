package com.starostinvlad.fan.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.starostinvlad.fan.R
import java.util.Collections.emptyList

class SimpleTextListAdapter(context: Context?) : RecyclerView.Adapter<SimpleTextListAdapter.ViewHolder>() {
    private val TAG: String = this::class.simpleName!!
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList: MutableList<String> = mutableListOf()
    fun setItemList(infoList: MutableList<String>) {
        itemList.addAll(infoList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.serial_page_info_item, parent, false)
        Log.d(TAG, "createViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val arr = itemList[position].split(":")
        holder.textViewTitle.text = arr[0]
        if (arr.size > 1) holder.textViewBody.text = arr[1]
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView
        var textViewBody: TextView

        init {
            textViewTitle = itemView.findViewById(R.id.serialPageInfoListItemTitle)
            textViewBody = itemView.findViewById(R.id.serialPageInfoListItemBody)
        }
    }

}