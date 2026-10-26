package com.alimapps.senbombardir.data.source

import android.content.SharedPreferences
import androidx.core.content.edit
import com.alimapps.senbombardir.ui.model.types.TeamOption

class HiddenColumnsStorage(private val preferences: SharedPreferences) {

    private fun key(gameId: Long) = "hidden_columns_$gameId"

    fun load(gameId: Long): Set<TeamOption> {
        if (!preferences.contains(key(gameId))) {
            return DEFAULT_HIDDEN_OPTIONS
        }
        val stored = preferences.getStringSet(key(gameId), emptySet()) ?: emptySet()
        return stored.mapNotNull { name ->
            TeamOption.entries.find { it.name == name }
        }.toSet()
    }

    fun save(gameId: Long, hiddenOptions: Set<TeamOption>) {
        preferences.edit { putStringSet(key(gameId), hiddenOptions.map { it.name }.toSet()) }
    }

    private companion object {
        val DEFAULT_HIDDEN_OPTIONS = setOf(TeamOption.YellowCard, TeamOption.RedCard)
    }
}
