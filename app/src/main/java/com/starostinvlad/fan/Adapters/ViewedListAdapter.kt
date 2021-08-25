package com.starostinvlad.fan.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.starostinvlad.fan.BlurTransformation
import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.R
import java.util.*

class ViewedListAdapter : RecyclerView.Adapter<ViewedListAdapter.ViewHolder>() {
    private val viewedList: MutableList<News?> = ArrayList()
    private val TAG: String = this::class.simpleName!!
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    private var onItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewed_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fill(viewedList[position])
    }

    override fun getItemCount(): Int {
        return viewedList.size
    }

    fun setViewedList(viewedList: List<News?>?) {
        this.viewedList.clear()
        this.viewedList.addAll(viewedList!!)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.viewed_title)
        private var subTitle: TextView = itemView.findViewById(R.id.viewed_subtitle)
        private var poster: ImageView = itemView.findViewById(R.id.viewed_poster)
        private var continueBtn: Button = itemView.findViewById(R.id.viewed_continue)

        fun fill(news: News?) {
            Log.d(TAG, "fill: $news")
            continueBtn.setBackgroundColor(itemView.context.getColor(android.R.color.holo_orange_dark))
            continueBtn.text = "Продолжить..."
            itemView.onFocusChangeListener = OnFocusChangeListener { view: View?, b: Boolean ->
                if (b) {
                    itemView.animate().scaleX(1.2f).scaleY(1.2f).z(1.2f).start()
                } else {
                    itemView.animate().scaleX(1f).scaleY(1f).z(1f).start()
                }
            }

            itemView.setOnClickListener { view: View? -> onItemClickListener!!.onClick(news!!) }

            title.text = news?.title
            subTitle.text = news?.subTitle
            Picasso.get()
                    .load(news?.image)
                    .transform(BlurTransformation(itemView.context))
                    .placeholder(R.color.cardview_dark_background)
                    .into(poster)
        }

    }

    interface OnItemClickListener {
        fun onClick(news: News)
    }
}