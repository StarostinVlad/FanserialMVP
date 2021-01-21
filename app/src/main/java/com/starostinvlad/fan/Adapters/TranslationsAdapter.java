package com.starostinvlad.fan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.VideoScreen.Player;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Translation;

import java.util.ArrayList;

public class TranslationsAdapter extends BaseAdapter {
    ArrayList<Translation> translations;

    public TranslationsAdapter(ArrayList<Translation> translations) {
        this.translations = translations;
    }

    @Override
    public int getCount() {

        return translations != null ? translations.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return translations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.spiner_item, viewGroup, false);
        TextView textView = view.findViewById(R.id.spiner_text_item);
        textView.setText(translations.get(i).getTitle());
        return view;
    }
}
