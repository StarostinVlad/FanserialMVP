package com.starostinvlad.fan.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.starostinvlad.fan.BlurTransformation
import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.R

class NewsWithAdRVAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG: String = this::class.simpleName!!
    var elements: List<News> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        if (viewType == 0) {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_gridview_item, parent, false)
        return ViewHolder(view)
        //        } else {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_gridview_ad_item, parent, false);
//            return new AdViewHolder(view);
//        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (getItemViewType(position) == 0) {
        val viewHolder = holder as ViewHolder
        if (position < elements.size) viewHolder.fill(elements[position])
        //        } else {
//            // Span the item if active
//            final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
//            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
//            sglp.setFullSpan(true);
//            holder.itemView.setLayoutParams(sglp);
//            AdViewHolder viewHolder = (AdViewHolder) holder;
//            viewHolder.fill();
//        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemCount) 0 else itemCount
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getItemCount(): Int {
        return elements.size
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title_item_id)
        var subTitle: TextView = itemView.findViewById(R.id.subtitle_item_id)
        var imageView: ImageView = itemView.findViewById(R.id.image_item_id)
        fun fill(news: News) {
            title.text = news.title
            subTitle.text = news.subTitle
            Picasso.get()
                    .load(news.image)
                    .placeholder(R.color.cardview_dark_background)
                    .transform(BlurTransformation(itemView.context))
                    .into(imageView)
            itemView.onFocusChangeListener = OnFocusChangeListener { _: View?, b: Boolean ->
                if (b) {
                    itemView.animate().scaleY(1.2f).scaleX(1.2f).z(1.2f).start()
                } else {
                    itemView.animate().scaleY(1f).scaleX(1f).z(1f).start()
                }
            }
            itemView.setOnClickListener { onItemClickListener!!.onItemClick(news) }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(news: News)
    }
}