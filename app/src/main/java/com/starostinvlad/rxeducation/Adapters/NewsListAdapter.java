package com.starostinvlad.rxeducation.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.starostinvlad.rxeducation.GsonModels.Datum;
import com.starostinvlad.rxeducation.R;
import com.starostinvlad.rxeducation.VideoScreen.VideoActivity;

import java.util.List;

public class NewsListAdapter extends BaseAdapter {

    final String TAG = getClass().getSimpleName();
    List<Datum> elements;

    public NewsListAdapter(List<Datum> elements) {
        this.elements = elements;
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public Object getItem(int i) {
        return elements.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.news_gridview_item, viewGroup, false);

        TextView title = view.findViewById(R.id.title_item_id);
        title.setText(elements.get(i).getSerial().getName());

        TextView sub_title = view.findViewById(R.id.subtitle_item_id);
        sub_title.setText(elements.get(i).getEpisode().getName());

        ImageView imageView = view.findViewById(R.id.image_item_id);
        Picasso.with(context).load(elements.get(i).getEpisode().getImages().getMedium()).placeholder(R.color.cardview_dark_background).into(imageView);

        view.setOnClickListener(view1 -> {
            Log.d(TAG, "click: " + elements.get(i).getEpisode().getName());
            VideoActivity.start((Activity) viewGroup.getContext(), elements.get(i).getEpisode());

        });

        return view;
    }
}
