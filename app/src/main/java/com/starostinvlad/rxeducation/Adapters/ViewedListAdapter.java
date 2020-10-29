package com.starostinvlad.rxeducation.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.starostinvlad.rxeducation.GsonModels.Datum;
import com.starostinvlad.rxeducation.GsonModels.Viewed;
import com.starostinvlad.rxeducation.R;
import com.starostinvlad.rxeducation.VideoScreen.VideoActivity;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ViewedListAdapter extends BaseAdapter {
    private List<Viewed> viewedList;

    public ViewedListAdapter(List<Viewed> viewedList) {
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
        Viewed viewed;
        Datum cur_elem;
        if ((viewed = viewedList.get(i)).getNext() == null) {
            continue_btn.setBackgroundColor(Color.DKGRAY);
            cur_elem = viewed.getCurrent();
        } else {
            continue_btn.setBackgroundColor(viewGroup.getContext().getColor(android.R.color.holo_orange_dark));
            cur_elem = viewed.getNext();
        }

        continue_btn.setText("Продолжить просмотр");

        continue_btn.setOnClickListener(
                view1 -> VideoActivity.start((AppCompatActivity) viewGroup.getContext(), cur_elem.getEpisode())
        );

        title.setText(cur_elem.getSerial().getName());
        sub_title.setText(cur_elem.getEpisode().getName());

        Picasso.with(viewGroup.getContext())
                .load(
                        cur_elem.getEpisode().getImages().getSmall()
                )
                .placeholder(R.color.cardview_dark_background)
                .into(poster);


        return view;
    }
}
