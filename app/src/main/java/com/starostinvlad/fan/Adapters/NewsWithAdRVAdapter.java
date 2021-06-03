package com.starostinvlad.fan.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.starostinvlad.fan.BlurTransformation;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.SerialPageScreen.SerialPageScreenActivity;
import com.starostinvlad.fan.SerialPageScreen.SerialPageScreenContract;
import com.starostinvlad.fan.VideoScreen.VideoActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsWithAdRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private List<News> elements = new ArrayList<>();

    public void setElements(List<News> elements) {
        this.elements = elements;
//        this.elements.add(8, null);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == 0) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_gridview_item, parent, false);
        return new ViewHolder(view);
//        } else {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_gridview_ad_item, parent, false);
//            return new AdViewHolder(view);
//        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (getItemViewType(position) == 0) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (position < elements.size())
            viewHolder.fill(elements.get(position));
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

    @Override
    public int getItemViewType(int position) {
        if (position < getItemCount())
            return elements.get(position) != null ? 0 : 1;
        else
            return getItemCount();
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

        void fill(News news) {
            title.setText(news.getTitle());

            subTitle.setText(news.getSubTitle());

            Picasso.with(itemView.getContext())
                    .load(news.getImage())
                    .placeholder(R.color.cardview_dark_background)
                    .transform(new BlurTransformation(itemView.getContext()))
                    .into(imageView);
            itemView.setOnClickListener(view1 -> {
                Log.d(TAG, "click: " + news.getTitle());
                SerialPageScreenActivity.start((Activity) itemView.getContext(), news);
//                VideoActivity.start((Activity) itemView.getContext(), news);
            });
        }
    }
}
