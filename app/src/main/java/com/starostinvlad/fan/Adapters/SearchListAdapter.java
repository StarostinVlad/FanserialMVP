package com.starostinvlad.fan.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.starostinvlad.fan.BlurTransformation;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.R;

import java.util.List;


public class SearchListAdapter extends BaseAdapter {
    private final String TAG = getClass().getSimpleName();
    private List<News> list;

    public SearchListAdapter(List<News> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_list_item, viewGroup, false);
        }

        TextView header = view.findViewById(R.id.search_item_header);
        header.setText(list.get(i).getTitle());

        TextView description = view.findViewById(R.id.search_item_description);
        description.setText(list.get(i).getSubTitle());

        ImageView imageView = view.findViewById(R.id.search_item_poster);
        Picasso.with(viewGroup.getContext())
                .load(list.get(i).getImage())
                .transform(new BlurTransformation(viewGroup.getContext()))
                .placeholder(R.color.cardview_dark_background)
                .into(imageView);

        return view;
    }
}
