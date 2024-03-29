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
        String[] arr = itemList.get(position).split(":");
        holder.textViewTitle.setText(arr[0]);
        if (arr.length > 1)
            holder.textViewBody.setText(arr[1]);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // stores and recycles views as they are scrolled off screen
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewBody;

        ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.serialPageInfoListItemTitle);
            textViewBody = itemView.findViewById(R.id.serialPageInfoListItemBody);
        }
    }
}
