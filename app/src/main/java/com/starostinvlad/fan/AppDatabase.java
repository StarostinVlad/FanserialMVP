package com.starostinvlad.fan;


import com.starostinvlad.fan.GsonModels.CurrentSerial;
import com.starostinvlad.fan.GsonModels.Datum;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Episode;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Season;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Translation;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                Datum.class, News.class, CurrentSerial.class
        },
        version = 12,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DatumDao datumDao();

    public abstract CurrentSerialDao currentSerialDao();

    public abstract SearchedDao searchedDao();
}