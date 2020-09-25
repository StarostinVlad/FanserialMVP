package com.starostinvlad.rxeducation;

import android.os.Bundle;

import com.starostinvlad.rxeducation.NewsScreen.NewsFragment;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NewsFragment newsFragment = new NewsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_id, newsFragment,"newsFragment")
                .commit();

    }
}
