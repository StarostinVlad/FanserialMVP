package com.starostinvlad.fan.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.starostinvlad.fan.BlurTransformation;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.SerialPageScreen.SerialPageScreenActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ViewedListAdapter extends RecyclerView.Adapter<ViewedListAdapter.ViewHolder> {
    private List<News> viewedList = new ArrayList<>();
    private final String TAG = getClass().getSimpleName();

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewed_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fill(viewedList.get(position));
    }


    @Override
    public int getItemCount() {
        return viewedList.size();
    }

    public void setViewedList(List<News> viewedList) {
        this.viewedList.clear();
        this.viewedList.addAll(viewedList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, sub_title;
        ImageView poster;
        Button continue_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.viewed_title);
            sub_title = itemView.findViewById(R.id.viewed_subtitle);
            poster = itemView.findViewById(R.id.viewed_poster);
            continue_btn = itemView.findViewById(R.id.viewed_continue);
        }

        void fill(News news) {
            Log.d(TAG, "fill: " + news);
            continue_btn.setBackgroundColor(itemView.getContext().getColor(android.R.color.holo_orange_dark));
            continue_btn.setText("Продолжить...");
            itemView.setOnFocusChangeListener(((view, b) -> {
                if (b) {
                    itemView.animate().scaleX(1.2f).scaleY(1.2f).z(1.2f).start();
                } else {
                    itemView.animate().scaleX(1f).scaleY(1f).z(1f).start();
                }
            }));
            itemView.setOnClickListener(
                    view -> onItemClickListener.onClick(news)
            );
            title.setText(news.getTitle());
            sub_title.setText(news.getSubTitle());

            Picasso.get()
                    .load(news.getImage())
                    .transform(new BlurTransformation(itemView.getContext()))
                    .placeholder(R.color.cardview_dark_background)
                    .into(poster);
        }
    }

    public interface OnItemClickListener {
        void onClick(News news);
    }
}
