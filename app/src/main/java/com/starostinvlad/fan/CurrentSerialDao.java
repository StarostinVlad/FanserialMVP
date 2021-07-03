package com.starostinvlad.fan;

import com.starostinvlad.fan.GsonModels.CurrentSerial;
import com.starostinvlad.fan.GsonModels.Datum;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Maybe;
import io.reactivex.Observable;

@Dao
public interface CurrentSerialDao {

    @Query("SELECT * FROM currentSerial")
    Maybe<List<CurrentSerial>> getAll();

    @Query("SELECT * FROM currentSerial WHERE pageId = :pageId")
    Maybe<CurrentSerial> getById(String pageId);

    @Insert
    List<Long> insert(List<CurrentSerial> currentSerials);

    @Insert
    Maybe<Long> insert(CurrentSerial currentSerial);

    @Update
    void update(CurrentSerial currentSerial);

    @Delete
    void delete(CurrentSerial currentSerial);

}