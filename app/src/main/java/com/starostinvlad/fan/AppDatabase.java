package com.starostinvlad.fan;


import com.starostinvlad.fan.GsonModels.Datum;
import com.starostinvlad.fan.GsonModels.News;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Datum.class, News.class}, version = 9, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DatumDao datumDao();

    public abstract SearchedDao searchedDao();
}