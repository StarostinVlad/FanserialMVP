package com.starostinvlad.fan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.starostinvlad.fan.R;

import java.util.ArrayList;
import java.util.List;

public class SubtitlesAdapter extends BaseAdapter {
    private List<MediaItem.Subtitle> subtitles;

    public SubtitlesAdapter(List<MediaItem.Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    @Override
    public int getCount() {

        return subtitles != null ? subtitles.size() : 0;
    }

    @Override
    public String getItem(int i) {
        return subtitles.get(i).language;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.spiner_item, viewGroup, false);
        TextView textView = view.findViewById(R.id.spiner_text_item);
        textView.setText(subtitles.get(i).language);
        return view;
    }
}
