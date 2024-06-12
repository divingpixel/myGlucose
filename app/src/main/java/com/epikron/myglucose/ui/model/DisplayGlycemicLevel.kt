package com.epikron.myglucose.ui.model

data class DisplayGlycemicLevel(
    val date: String,
    val value: String
) {
    companion object {
        val test = DisplayGlycemicLevel("Tue, 11 Jun 15:30", "123")
    }
}
