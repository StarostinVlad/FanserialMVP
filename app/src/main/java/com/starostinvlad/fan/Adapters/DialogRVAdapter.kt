package com.starostinvlad.fan.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.starostinvlad.fan.R

interface OnItemSelectListener {
    fun onSelect(position: Int)
}

class DialogRVAdapter(private val itemSelectListener: OnItemSelectListener) : RecyclerView.Adapter<DialogRVAdapter.ViewHolder>() {

    var items: List<String> = mutableListOf()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.simple_text_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
        holder.itemView.setOnClickListener {
            itemSelectListener.onSelect(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.spiner_text_item)
    }
}