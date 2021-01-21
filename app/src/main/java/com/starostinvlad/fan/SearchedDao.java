package com.starostinvlad.fan;

import com.starostinvlad.fan.GsonModels.News;

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

    @Query("SELECT * FROM news")
    Observable<List<News>> getAll();

    @Query("SELECT * FROM news WHERE id = :id")
    News getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<List<Long>> insert(List<News> searched);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<Long> insert(News searched);

    @Update
    void update(News searched);

    @Update
    void update(List<News> searched);

    @Delete
    void delete(News searched);

}