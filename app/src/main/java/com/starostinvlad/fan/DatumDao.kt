package com.starostinvlad.fan

import androidx.room.*
import com.starostinvlad.fan.GsonModels.Datum
import kotlin.Throws

@Dao
interface DatumDao {
    @get:Query("SELECT * FROM datum")
    val all: List<Datum?>?

    @Query("SELECT * FROM datum WHERE id = :id")
    fun getById(id: Long): Datum?

    @Insert
    fun insert(datum: List<Datum?>?)

    @Insert
    fun insert(datum: Datum?)

    @Update
    fun update(datum: Datum?)

    @Delete
    fun delete(datum: Datum?)
}