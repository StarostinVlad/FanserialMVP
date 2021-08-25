package com.starostinvlad.fan.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.starostinvlad.fan.BlurTransformation
import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.R


interface OnItemClickListener {
    fun onClick(item: News)
}

class SearchListAdapter(private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<SearchListAdapter.ViewHolder>() {
    private val TAG: String = this::class.simpleName!!
    var list: MutableList<News> = mutableListOf()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.apply {
            header.text = list[position].title
            description.text = list[position].subTitle
            Picasso.get()
                    .load(list[position].image)
                    .transform(BlurTransformation(holder.itemView.context))
                    .placeholder(R.color.cardview_dark_background)
                    .into(imageView)
            itemView.setOnClickListener {
                itemClickListener.onClick(list[position])
            }
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val header = itemView.findViewById<TextView>(R.id.search_item_header)
        val description = itemView.findViewById<TextView>(R.id.search_item_description)
        val imageView = itemView.findViewById<ImageView>(R.id.search_item_poster)
    }
}