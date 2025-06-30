package com.alimapps.senbombardir.utils

fun Int?.orDefault(default: Int = 0): Int = this ?: default

fun Int.toMillis(): Long = this * 60000L

fun Long?.orDefault(default: Long = 0L): Long = this ?: default