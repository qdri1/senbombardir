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

    var isSecretActivated: Boolean
        get() = preferences.getBoolean("SECRET_ACTIVATED", false)
        set(value) = preferences.edit { putBoolean("SECRET_ACTIVATED", value) }

    var monthlyPrice: String?
        get() = preferences.getString("MONTHLY_PRICE", null)
        set(value) = preferences.edit { putString("MONTHLY_PRICE", value) }

    var yearlyPrice: String?
        get() = preferences.getString("YEARLY_PRICE", null)
        set(value) = preferences.edit { putString("YEARLY_PRICE", value) }

    var unlimitedPrice: String?
        get() = preferences.getString("UNLIMITED_PRICE", null)
        set(value) = preferences.edit { putString("UNLIMITED_PRICE", value) }

    var onedayPrice: String?
        get() = preferences.getString("ONEDAY_PRICE", null)
        set(value) = preferences.edit { putString("ONEDAY_PRICE", value) }

    fun getLastLeftTeamId(gameId: Long): Long? =
        preferences.getLong(lastLeftTeamIdKey(gameId), NO_TEAM_ID).takeIf { it != NO_TEAM_ID }

    fun setLastLeftTeamId(gameId: Long, teamId: Long) =
        preferences.edit { putLong(lastLeftTeamIdKey(gameId), teamId) }

    fun clearLastLeftTeamId(gameId: Long) =
        preferences.edit { remove(lastLeftTeamIdKey(gameId)) }

    private fun lastLeftTeamIdKey(gameId: Long) = "LAST_LEFT_TEAM_ID_$gameId"

    fun getTeamOrder(gameId: Long): List<Long>? =
        preferences.getString(teamOrderKey(gameId), null)
            ?.split(",")
            ?.mapNotNull { it.toLongOrNull() }

    fun setTeamOrder(gameId: Long, teamIds: List<Long>) =
        preferences.edit { putString(teamOrderKey(gameId), teamIds.joinToString(",")) }

    fun clearTeamOrder(gameId: Long) =
        preferences.edit { remove(teamOrderKey(gameId)) }

    private fun teamOrderKey(gameId: Long) = "TEAM_ORDER_$gameId"

    fun getRoundRobinStart(gameId: Long): Int = preferences.getInt(roundRobinStartKey(gameId), 0)

    fun setRoundRobinStart(gameId: Long, gameCount: Int) =
        preferences.edit { putInt(roundRobinStartKey(gameId), gameCount) }

    fun clearRoundRobinStart(gameId: Long) =
        preferences.edit { remove(roundRobinStartKey(gameId)) }

    private fun roundRobinStartKey(gameId: Long) = "ROUND_ROBIN_START_$gameId"

    var onedayExpirationDate: Long
        get() = preferences.getLong("ONEDAY_EXPIRATION_DATE", 0L)
        set(value) = preferences.edit { putLong("ONEDAY_EXPIRATION_DATE", value) }

    private companion object {
        const val NO_TEAM_ID = -1L
    }
}