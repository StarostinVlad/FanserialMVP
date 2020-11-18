package com.starostinvlad.fan.SerialScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.starostinvlad.fan.Adapters.SerialRecyclerViewAdapter;
import com.starostinvlad.fan.GsonModels.Searched;
import com.starostinvlad.fan.R;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SerialActivity extends AppCompatActivity implements SerialActivityContract {

    SerialActivityPresenter presenter;
    RecyclerView listView;
    Toolbar toolbar;

    public static void start(Activity context, Searched url) {
        Intent intent = new Intent(context, SerialActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_serial);
        toolbar = findViewById(R.id.serial_toolbar);
        listView = findViewById(R.id.season_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        listView.setLayoutManager(layoutManager);
        Searched searched = (Searched) getIntent().getSerializableExtra("url");
        presenter = new SerialActivityPresenter(this);
        if (searched != null) {
            toolbar.setTitle(searched.getName());
        }
        setSupportActionBar(toolbar);
        if (searched != null) {
            presenter.loadData(searched.getUrl());
        }
    }

    @Override
    public void fillList(List<String> seasons) {
        SerialRecyclerViewAdapter serialRecyclerViewAdapter = new SerialRecyclerViewAdapter(this, seasons);
        listView.setAdapter(serialRecyclerViewAdapter);
    }
}
