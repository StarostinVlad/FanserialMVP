package com.starostinvlad.fan;


import com.starostinvlad.fan.GsonModels.Datum;
import com.starostinvlad.fan.GsonModels.Searched;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Datum.class, Searched.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DatumDao datumDao();

    public abstract SearchedDao searchedDao();
}