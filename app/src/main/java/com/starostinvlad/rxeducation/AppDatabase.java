package com.starostinvlad.rxeducation;


import com.starostinvlad.rxeducation.GsonModels.Datum;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Datum.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DatumDao datumDao();
}