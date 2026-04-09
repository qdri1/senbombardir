package com.alimapps.senbombardir.ui.model

import com.alimapps.senbombardir.ui.model.types.TeamColor

data class GameHistoryEntryUiModel(
    val gameNumber: Int,
    val leftTeamName: String,
    val leftTeamColor: TeamColor,
    val leftTeamGoals: Int,
    val rightTeamName: String,
    val rightTeamColor: TeamColor,
    val rightTeamGoals: Int,
    val winnerTeamName: String,
    val durationSeconds: Int,
    val actionEvents: List<GameHistoryActionEventUiModel>,
) {
    val durationFormatted: String?
        get() {
            if (durationSeconds <= 0) return null
            val m = durationSeconds / 60
            val s = durationSeconds % 60
            return String.format("%02d:%02d", m, s)
        }
}

data class GameHistoryActionEventUiModel(
    val teamName: String,
    val teamColor: TeamColor,
    val playerName: String,
    val actionType: String,
    val elapsedSeconds: Int,
) {
    val elapsedFormatted: String
        get() {
            val m = elapsedSeconds / 60
            val s = elapsedSeconds % 60
            return String.format("%02d:%02d", m, s)
        }
}
