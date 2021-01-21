package com.starostinvlad.fan.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.GsonModels.Episode;
import com.starostinvlad.fan.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class EpisodeRecyclerViewAdapter extends RecyclerView.Adapter<EpisodeRecyclerViewAdapter.ViewHolder> {

    private String TAG = getClass().getSimpleName();
    private List<Episode> episodes;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    EpisodeRecyclerViewAdapter(Context context, List<Episode> episodes) {
        this.mInflater = LayoutInflater.from(context);
        this.episodes = episodes;
        Log.d(TAG, "initialize");
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item_with_image, parent, false);
        Log.d(TAG, "createViewHolder");
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        Episode episode = episodes.get(position);
        holder.myTextView.setText(episode.getName());
        Picasso.with(holder.itemView.getContext())
                .load(episode.getImages().getSmall())
                //.transform(App.getInstance().isReview() ? new BlurTransformation(holder.itemView.getContext()) : new CropSquareTransformation())
                .placeholder(R.color.cardview_dark_background)
                .into(holder.imageView);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return episodes.size();
    }

    // convenience method for getting data at click position
    public Episode getItem(int id) {
        return episodes.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView myView;
        TextView myTextView;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            myView = itemView.findViewById(R.id.card_item_id);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            imageView = itemView.findViewById(R.id.episode_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}