package com.starostinvlad.rxeducation;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {

    private static App instance;
    private AppDatabase database;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Utils.init(this);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .allowMainThreadQueries()
                .build();
    }

    public AppDatabase getDatabase() {
        return database;
    }
}