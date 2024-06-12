package com.epikron.myglucose.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.epikron.myglucose.data.model.GlycemicLevelModel

@Database(entities = [GlycemicLevelModel::class], version = 1)
abstract class GlycemicDatabase : RoomDatabase() {
    abstract fun glycemicLevelDao(): GlycemicLevelDao
}