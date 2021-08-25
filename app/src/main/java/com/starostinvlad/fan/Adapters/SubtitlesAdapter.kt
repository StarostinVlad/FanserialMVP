package com.starostinvlad.fan.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.android.exoplayer2.MediaItem
import com.starostinvlad.fan.R

class SubtitlesAdapter(private val subtitles: List<MediaItem.Subtitle>) : BaseAdapter() {
    override fun getCount(): Int {
        return subtitles.size
    }

    override fun getItem(i: Int): String {
        return subtitles[i].language!!
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, v: View, viewGroup: ViewGroup): View {
        val context = viewGroup.context
        val view = LayoutInflater.from(context).inflate(R.layout.simple_text_item, viewGroup, false)
        val textView = view.findViewById<TextView>(R.id.spiner_text_item)
        textView.text = subtitles[i].language
        return view
    }
}