package com.starostinvlad.fan.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.starostinvlad.fan.BlurTransformation;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.VideoScreen.VideoActivity;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ViewedListAdapter extends BaseAdapter {
    private List<News> viewedList;

    public ViewedListAdapter(List<News> viewedList) {
        this.viewedList = viewedList;
    }

    @Override
    public int getCount() {
        return viewedList.size();
    }

    @Override
    public Object getItem(int i) {
        return viewedList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewed_list_item, viewGroup, false);

        TextView title = view.findViewById(R.id.viewed_title);
        TextView sub_title = view.findViewById(R.id.viewed_subtitle);
        ImageView poster = view.findViewById(R.id.viewed_poster);
        Button continue_btn = view.findViewById(R.id.viewed_continue);
        News cur_elem;
        continue_btn.setBackgroundColor(viewGroup.getContext().getColor(android.R.color.holo_orange_dark));
        cur_elem = viewedList.get(i);

        continue_btn.setText("Продолжить...");

        continue_btn.setOnClickListener(
                view1 -> VideoActivity.start((AppCompatActivity) viewGroup.getContext(), cur_elem)
        );

        title.setText(cur_elem.getTitle());
        sub_title.setText(cur_elem.getSubTitle());

        Picasso.with(viewGroup.getContext())
                .load(cur_elem.getImage())
                .transform(new BlurTransformation(viewGroup.getContext()))
                .placeholder(R.color.cardview_dark_background)
                .into(poster);


        return view;
    }
}
