package com.alimapps.senbombardir.data.source

import android.content.SharedPreferences
import androidx.core.content.edit

class Prefs(private val preferences: SharedPreferences) {

    var timerValue: Long
        get() = preferences.getLong("LIVE_GAME_TIMER_VALUE", 0L)
        set(value) = preferences.edit { putLong("LIVE_GAME_TIMER_VALUE", value) }
}