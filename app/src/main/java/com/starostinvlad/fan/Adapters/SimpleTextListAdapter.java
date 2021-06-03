package com.starostinvlad.fan.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.starostinvlad.fan.R;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleTextListAdapter extends RecyclerView.Adapter<SimpleTextListAdapter.ViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private LayoutInflater mInflater;
    private List<String> itemList;

    public SimpleTextListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        itemList = Collections.emptyList();
    }

    public void setItemList(List<String> infoList) {
        this.itemList = infoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.serial_page_info_item, parent, false);
        Log.d(TAG, "createViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // stores and recycles views as they are scrolled off screen
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.serialPageInfoListItem);
        }
    }
}