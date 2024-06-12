package com.epikron.myglucose.data.repository

import com.epikron.myglucose.data.local.GlycemicLevelDao
import com.epikron.myglucose.data.model.GlycemicLevelModel
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val glycemicLevelDao: GlycemicLevelDao
) {
    suspend fun getEntries(): List<GlycemicLevelModel> {
        return glycemicLevelDao.getAllEntries()
    }

    suspend fun addEntry(entry: GlycemicLevelModel) {
        glycemicLevelDao.insertEntry(entry)
    }
}