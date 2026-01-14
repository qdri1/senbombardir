package com.alimapps.senbombardir.data.source

import android.content.SharedPreferences
import androidx.core.content.edit

class Prefs(private val preferences: SharedPreferences) {

    var timerValue: Long
        get() = preferences.getLong("LIVE_GAME_TIMER_VALUE", 0L)
        set(value) = preferences.edit { putLong("LIVE_GAME_TIMER_VALUE", value) }

    var selectedLanguage: String?
        get() = preferences.getString("SELECTED_LANGUAGE", null)
        set(value) = preferences.edit { putString("SELECTED_LANGUAGE", value) }

    var billingType: String?
        get() = preferences.getString("BILLING_TYPE", null)
        set(value) = preferences.edit { putString("BILLING_TYPE", value) }

    var clearResultsRemainingCount: Int
        get() = preferences.getInt("CLEAR_RESULTS_REMAINING_COUNT", 3)
        set(value) = preferences.edit { putInt("CLEAR_RESULTS_REMAINING_COUNT", value) }

    var monthlyPrice: String?
        get() = preferences.getString("MONTHLY_PRICE", null)
        set(value) = preferences.edit { putString("MONTHLY_PRICE", value) }

    var yearlyPrice: String?
        get() = preferences.getString("YEARLY_PRICE", null)
        set(value) = preferences.edit { putString("YEARLY_PRICE", value) }

    var unlimitedPrice: String?
        get() = preferences.getString("UNLIMITED_PRICE", null)
        set(value) = preferences.edit { putString("UNLIMITED_PRICE", value) }
}