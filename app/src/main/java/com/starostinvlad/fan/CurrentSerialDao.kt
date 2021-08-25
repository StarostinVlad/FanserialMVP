package com.starostinvlad.fan

import androidx.room.*
import com.starostinvlad.fan.GsonModels.CurrentSerial
import io.reactivex.Maybe
import kotlin.Throws

@Dao
interface CurrentSerialDao {
    @get:Query("SELECT * FROM currentSerial")
    val all: Maybe<List<CurrentSerial?>?>?

    @Query("SELECT * FROM currentSerial WHERE pageId = :pageId")
    fun getById(pageId: String?): Maybe<CurrentSerial?>

    @Insert
    fun insert(currentSerials: List<CurrentSerial?>?): List<Long?>?

    @Insert
    fun insert(currentSerial: CurrentSerial?): Maybe<Long>

    @Update
    fun update(currentSerial: CurrentSerial?)

    @Delete
    fun delete(currentSerial: CurrentSerial?)
}