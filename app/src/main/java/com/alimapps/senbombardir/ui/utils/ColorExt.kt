package com.alimapps.senbombardir.ui.utils

import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor

fun parseHexColor(hex: String): Color =
    try {
        Color(AndroidColor.parseColor(hex))
    } catch (e: IllegalArgumentException) {
        Color.White
    }