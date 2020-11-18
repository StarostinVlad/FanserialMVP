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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class NewsWithAdRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private List<Datum> elements = new ArrayList<>();

    public void setElements(List<Datum> elements) {
        this.elements = elements;
        this.elements.add(8, null);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_gridview_item, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_gridview_ad_item, parent, false);
            return new AdViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.fill(elements.get(position));
        } else {
            // Span the item if active
            final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
            sglp.setFullSpan(true);
            holder.itemView.setLayoutParams(sglp);
            AdViewHolder viewHolder = (AdViewHolder) holder;
            viewHolder.fill();
        }

    }

    @Override
    public int getItemViewType(int position) {
        return elements.get(position) != null ? 0 : 1;
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

        void fill(Datum datum) {
            title.setText(datum.getSerial().getName());

            subTitle.setText(datum.getEpisode().getName());

            Picasso.with(itemView.getContext()).load(datum.getEpisode().getImages().getMedium()).placeholder(R.color.cardview_dark_background).into(imageView);
            itemView.setOnClickListener(view1 -> {
                Log.d(TAG, "click: " + datum.getEpisode().getName());
                VideoActivity.start((Activity) itemView.getContext(), datum.getEpisode());
            });
        }
    }

    class AdViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        AdViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_ad_item);

        }

        void fill() {
            title.setText("РЕКЛАМА!");
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
