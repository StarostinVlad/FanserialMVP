package com.starostinvlad.fan.Adapters;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.starostinvlad.fan.VideoScreen.PlayerModel.Episode;
import com.starostinvlad.fan.R;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SeasonRecyclerViewAdapter extends RecyclerView.Adapter<SeasonRecyclerViewAdapter.ViewHolder> {

    private String TAG = getClass().getSimpleName();
    private List<Episode> episodes;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public SeasonRecyclerViewAdapter(Context context, List<Episode> episodes) {
        this.mInflater = LayoutInflater.from(context);
        this.episodes = episodes;
        Log.d(TAG, "initialize");
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.episodes_in_videoscreen_item, parent, false);
        Log.d(TAG, "createViewHolder");
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        holder.myTextView.setText(episodes.get(position).getTitle());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if (episodes != null)
            return episodes.size();
        return 0;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView myView;
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myView = itemView.findViewById(R.id.card_item_id);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            itemView.setOnClickListener(this);
            itemView.setOnFocusChangeListener((view, b) -> {
                    if (b) {
                        itemView.animate().scaleY(1.2f).scaleX(1.2f).z(1.2f).start();
                    } else {
                        itemView.animate().scaleY(1f).scaleX(1f).z(1f).start();
                    }
            });
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Episode getItem(int id) {
        return episodes.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}