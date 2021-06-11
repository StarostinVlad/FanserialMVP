package com.starostinvlad.fan.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Season;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SerialSeasonListAdapter extends RecyclerView.Adapter<SerialSeasonListAdapter.ViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private LayoutInflater mInflater;
    private List<Season> itemList;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SerialSeasonListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        itemList = Collections.emptyList();
    }

    public void setItemList(List<Season> infoList) {
        this.itemList = infoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.serial_page_season_list_item, parent, false);
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
