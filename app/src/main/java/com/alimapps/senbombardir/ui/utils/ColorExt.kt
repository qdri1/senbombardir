package com.alimapps.senbombardir.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

fun parseHexColor(hex: String): Color =
    try {
        Color(hex.toColorInt())
    } catch (_: IllegalArgumentException) {
        Color.White
    }