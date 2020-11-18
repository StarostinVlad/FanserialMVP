package com.starostinvlad.fan.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.starostinvlad.fan.GsonModels.Datum;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.VideoScreen.VideoActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private List<Datum> elements = new ArrayList<>();

    public void setElements(List<Datum> elements) {
        this.elements = elements;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_gridview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(elements.get(position).getSerial().getName());

        holder.subTitle.setText(elements.get(position).getEpisode().getName());

        Picasso.with(holder.itemView.getContext()).load(elements.get(position).getEpisode().getImages().getMedium()).placeholder(R.color.cardview_dark_background).into(holder.imageView);

        holder.itemView.setOnClickListener(view1 -> {
            Log.d(TAG, "click: " + elements.get(position).getEpisode().getName());
            VideoActivity.start((Activity) holder.itemView.getContext(), elements.get(position).getEpisode());
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subTitle;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_item_id);
            subTitle = itemView.findViewById(R.id.subtitle_item_id);
            imageView = itemView.findViewById(R.id.image_item_id);
        }
    }

//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        Context context = viewGroup.getContext();
//        if (view == null)
//            view = LayoutInflater.from(context).inflate(R.layout.news_gridview_item, viewGroup, false);
//
//        TextView title = view.findViewById(R.id.title_item_id);
//        title.setText(elements.get(i).getSerial().getName());
//
//        TextView sub_title = view.findViewById(R.id.subtitle_item_id);
//        sub_title.setText(elements.get(i).getEpisode().getName());
//
//        ImageView imageView = view.findViewById(R.id.image_item_id);
//        Picasso.with(context).load(elements.get(i).getEpisode().getImages().getMedium()).placeholder(R.color.cardview_dark_background).into(imageView);
//
//        return view;
//    }
}
