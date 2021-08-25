package com.starostinvlad.fan

import androidx.room.Database
import androidx.room.RoomDatabase
import com.starostinvlad.fan.GsonModels.CurrentSerial
import com.starostinvlad.fan.GsonModels.Datum
import com.starostinvlad.fan.GsonModels.News
import kotlin.Throws

@Database(entities = [Datum::class, News::class, CurrentSerial::class], version = 12, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun datumDao(): DatumDao?
    abstract fun currentSerialDao(): CurrentSerialDao
    abstract fun searchedDao(): SearchedDao
}