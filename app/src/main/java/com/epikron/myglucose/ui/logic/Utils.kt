package com.epikron.myglucose.ui.logic

import java.text.SimpleDateFormat
import java.util.*

val numbersPattern = Regex("^\\d+\$")

const val NICE_DATE_TIME = "EEE, dd MMM yyyy, HH:mm"

fun Double.closestInt():Int {
    val base = this.toInt()
    val decimals = ((this - base) * 10).toInt()
    return if (decimals >=5) base + 1 else base
}

fun Double.toMmol(): Double = this / 18.0182

fun Double.toMg(): Double = this * 18.0182

fun Long.toNiceTime(): String = SimpleDateFormat(NICE_DATE_TIME, Locale.getDefault()).format(Date(this))