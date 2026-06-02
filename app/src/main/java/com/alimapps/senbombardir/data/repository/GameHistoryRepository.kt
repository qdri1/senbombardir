package com.alimapps.senbombardir.data.repository

import com.alimapps.senbombardir.data.model.GameHistoryActionEventModel
import com.alimapps.senbombardir.data.model.GameHistoryEntryModel
import com.alimapps.senbombardir.data.source.GameHistoryDao
import com.alimapps.senbombardir.ui.model.GameHistoryActionEventUiModel
import com.alimapps.senbombardir.ui.model.GameHistoryEntryUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor

class GameHistoryRepository(
    private val gameHistoryDao: GameHistoryDao,
) {

    suspend fun getGameHistory(gameId: Long): List<GameHistoryEntryUiModel> {
        return gameHistoryDao.getGameHistoryEntries(gameId).map { entry ->
            val actionEvents = gameHistoryDao.getGameHistoryActionEvents(entry.id).map { event ->
                val (parsedName, parsedNumber) = parsePlayerName(event.playerName)
                GameHistoryActionEventUiModel(
                    teamName = event.teamName,
                    teamColor = TeamColor.getTeamColor(event.teamColor),
                    playerName = parsedName,
                    playerNumber = parsedNumber,
                    actionType = event.actionType,
                    elapsedSeconds = event.elapsedSeconds,
                )
            }
            GameHistoryEntryUiModel(
                gameNumber = entry.gameNumber,
                leftTeamName = entry.leftTeamName,
                leftTeamColor = TeamColor.getTeamColor(entry.leftTeamColor),
                leftTeamGoals = entry.leftTeamGoals,
                rightTeamName = entry.rightTeamName,
                rightTeamColor = TeamColor.getTeamColor(entry.rightTeamColor),
                rightTeamGoals = entry.rightTeamGoals,
                winnerTeamName = entry.winnerTeamName,
                durationSeconds = entry.durationSeconds,
                actionEvents = actionEvents,
            )
        }
    }

    private fun parsePlayerName(stored: String): Pair<String, Int?> {
        if (!stored.startsWith("№")) return Pair(stored, null)
        val withoutPrefix = stored.removePrefix("№")
        val spaceIndex = withoutPrefix.indexOf(' ')
        if (spaceIndex < 0) return Pair(stored, null)
        val number = withoutPrefix.substring(0, spaceIndex).toIntOrNull() ?: return Pair(stored, null)
        return Pair(withoutPrefix.substring(spaceIndex + 1), number)
    }

    suspend fun saveGameHistoryEntry(entry: GameHistoryEntryModel): Long {
        return gameHistoryDao.insertGameHistoryEntry(entry)
    }

    suspend fun saveGameHistoryActionEvent(event: GameHistoryActionEventModel) {
        gameHistoryDao.insertGameHistoryActionEvent(event)
    }

    suspend fun deleteGameHistory(gameId: Long) {
        gameHistoryDao.deleteGameHistory(gameId)
    }
}
