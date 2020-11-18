package com.starostinvlad.fan;

import com.starostinvlad.fan.GsonModels.Searched;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Maybe;
import io.reactivex.Observable;

@Dao
public interface SearchedDao {

    @Query("SELECT * FROM searched")
    Observable<List<Searched>> getAll();

    @Query("SELECT * FROM searched WHERE id = :id")
    Searched getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<List<Long>> insert(List<Searched> searched);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<Long> insert(Searched searched);

    @Update
    void update(Searched searched);

    @Update
    void update(List<Searched> searched);

    @Delete
    void delete(Searched searched);

}