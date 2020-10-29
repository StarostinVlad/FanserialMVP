package com.starostinvlad.rxeducation;

import com.starostinvlad.rxeducation.GsonModels.Datum;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DatumDao {

    @Query("SELECT * FROM datum")
    List<Datum> getAll();

    @Query("SELECT * FROM datum WHERE id = :id")
    Datum getById(long id);

    @Insert
    void insert(List<Datum> datum);

    @Insert
    void insert(Datum datum);

    @Update
    void update(Datum datum);

    @Delete
    void delete(Datum datum);

}