package com.starostinvlad.rxeducation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.starostinvlad.rxeducation.R;
import com.starostinvlad.rxeducation.VideoScreen.Player;

import java.util.ArrayList;

public class PlayersAdapter extends BaseAdapter {
    ArrayList<Player> players;

    public PlayersAdapter(ArrayList<Player> players) {
        this.players = players;
    }

    @Override
    public int getCount() {

        return players != null ? players.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return players.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        if(view == null)
            view  = LayoutInflater.from(context).inflate(R.layout.spiner_item, viewGroup, false);
        TextView textView = view.findViewById(R.id.spiner_text_item);
        textView.setText(players.get(i).getName());
        return view;
    }
}
