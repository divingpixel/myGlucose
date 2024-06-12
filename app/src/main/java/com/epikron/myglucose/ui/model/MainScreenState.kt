package com.epikron.myglucose.ui.model

import com.epikron.myglucose.data.model.GlycemicLevelModel
import com.epikron.myglucose.data.model.GlycemicLevelModel.Companion.toDisplay

data class MainScreenState(
    val averageBGMg: String,
    val isUnitMg: Boolean,
    val textInput: String,
    val inputError: Boolean,
    val history: List<DisplayGlycemicLevel>
) {
    companion object {
        val empty = MainScreenState("", true, "", false, listOf())
        val test = MainScreenState("1234", true, "4321", true, GlycemicLevelModel.testList.map { it.toDisplay(true) })
    }
}
