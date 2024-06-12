package com.epikron.myglucose.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epikron.myglucose.data.model.GlycemicLevelModel

@Dao
interface GlycemicLevelDao {

    @Query("SELECT * FROM glycemiclevelmodel ORDER BY timestamp DESC")
    suspend fun getAllEntries(): List<GlycemicLevelModel>

    @Insert
    suspend fun insertEntry(continent: GlycemicLevelModel)

    @Delete
    suspend fun deleteEntry(continent: GlycemicLevelModel)

    @Update
    suspend fun updateEntry(continent: GlycemicLevelModel)
}