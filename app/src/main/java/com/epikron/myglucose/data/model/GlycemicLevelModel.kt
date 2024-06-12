package com.epikron.myglucose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.epikron.myglucose.ui.logic.closestInt
import com.epikron.myglucose.ui.logic.toMmol
import com.epikron.myglucose.ui.model.DisplayGlycemicLevel

const val TIMESTAMP = 1718114768L
const val NICE_DATE = "Tue, 11 Jun 15:30"

@Entity
data class GlycemicLevelModel(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "milligrams") val mg: Double,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "local_time") val localTime: String
) {
    companion object {
        private val testValue1 = GlycemicLevelModel(0, 123.45, TIMESTAMP, NICE_DATE)
        private val testValue2 = GlycemicLevelModel(1, 543.21, TIMESTAMP, NICE_DATE)
        val testList = listOf(testValue1, testValue2)

        fun GlycemicLevelModel.toDisplay(isMg: Boolean): DisplayGlycemicLevel {
            return DisplayGlycemicLevel(
                date = this.localTime,
                value = if (isMg) this.mg.closestInt().toString() else this.mg.toMmol().closestInt().toString()
            )
        }
    }
}
