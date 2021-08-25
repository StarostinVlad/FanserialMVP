package com.starostinvlad.fan

import androidx.room.*
import com.starostinvlad.fan.GsonModels.News
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface SearchedDao {
    @get:Query("SELECT * FROM news")
    val all: Maybe<List<News>>

    @Query("SELECT * FROM news WHERE id = :id")
    fun getById(id: Long): News

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searched: List<News>): Maybe<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searched: News): Maybe<Long>

    @Update
    fun update(searched: News)

    @Update
    fun update(searched: List<News>)

    @Delete
    fun delete(searched: News)
}