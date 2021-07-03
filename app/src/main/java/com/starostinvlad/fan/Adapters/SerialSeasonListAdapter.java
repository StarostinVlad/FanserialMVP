package com.starostinvlad.fan.Adapters;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Season;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SerialSeasonListAdapter extends RecyclerView.Adapter<SerialSeasonListAdapter.ViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private LayoutInflater mInflater;
    private List<Season> itemList = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemList(List<Season> infoList) {
        itemList.clear();
        this.itemList.addAll(infoList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.serial_page_season_list_item, parent, false);
        Log.d(TAG, "createViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(itemList.get(position).getNumber() + " сезон");
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.serialPageInfoListSeasonTitle);
            itemView.setOnFocusChangeListener((view, b) -> {
                if (b) {
                    itemView.animate().scaleY(1.2f).scaleX(1.2f).z(1.2f).start();
                } else {
                    itemView.animate().scaleY(1f).scaleX(1f).z(1f).start();
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.OnItemClickListener(view, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void OnItemClickListener(View view, int position);
    }
}
