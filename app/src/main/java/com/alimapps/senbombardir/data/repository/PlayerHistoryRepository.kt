package com.alimapps.senbombardir.data.repository

import com.alimapps.senbombardir.data.model.PlayerHistoryModel
import com.alimapps.senbombardir.data.source.PlayerHistoryDao
import com.alimapps.senbombardir.data.source.TeamHistoryDao
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.utils.empty
import com.alimapps.senbombardir.utils.orDefault

class PlayerHistoryRepository(
    private val teamHistoryDao: TeamHistoryDao,
    private val playerHistoryDao: PlayerHistoryDao,
) {

    suspend fun getPlayersHistories(teamId: Long): List<PlayerUiModel> {
        val teamModel = teamHistoryDao.getTeamHistory(teamId)
        val teamGoalsDifference = teamModel?.let { it.goals - it.conceded }.orDefault()

        val players = playerHistoryDao.getPlayersHistories(teamId).map {
            PlayerUiModel(
                id = it.id,
                teamId = it.teamId,
                teamColor = TeamColor.getTeamColor(hexColor = teamModel?.color.orEmpty()),
                teamName = teamModel?.name.orEmpty(),
                teamPoints = teamModel?.points.orDefault(),
                teamGoalsDifference = teamGoalsDifference,
                name = it.name,
                goals = it.goals,
                assists = it.assists,
                dribbles = it.dribbles,
                passes = it.passes,
                shots = it.shots,
                saves = it.saves,
            )
        }

        return players
    }

    suspend fun getPlayerHistory(playerId: Long): PlayerUiModel? {
        return playerHistoryDao.getPlayerHistory(playerId)?.let {
            PlayerUiModel(
                id = it.id,
                teamId = it.teamId,
                teamColor = TeamColor.Red,
                teamName = String.empty,
                teamPoints = 0,
                teamGoalsDifference = 0,
                name = it.name,
                goals = it.goals,
                assists = it.assists,
                dribbles = it.dribbles,
                passes = it.passes,
                shots = it.shots,
                saves = it.saves,
            )
        }
    }

    suspend fun getPlayerHistory(teamId: Long, playerName: String): PlayerUiModel? {
        return playerHistoryDao.getPlayerHistory(teamId, playerName)?.let {
            PlayerUiModel(
                id = it.id,
                teamId = it.teamId,
                teamColor = TeamColor.Red,
                teamName = String.empty,
                teamPoints = 0,
                teamGoalsDifference = 0,
                name = it.name,
                goals = it.goals,
                assists = it.assists,
                dribbles = it.dribbles,
                passes = it.passes,
                shots = it.shots,
                saves = it.saves,
            )
        }
    }
    
    suspend fun savePlayerHistory(playerHistoryModel: PlayerHistoryModel) {
        playerHistoryDao.insertPlayerHistory(playerHistoryModel)
    }

    suspend fun updatePlayerHistory(playerHistoryModel: PlayerHistoryModel) {
        playerHistoryDao.updatePlayerHistory(playerHistoryModel)
    }

    suspend fun deletePlayerHistory(playerHistoryModel: PlayerHistoryModel) {
        playerHistoryDao.deletePlayerHistory(playerHistoryModel)
    }

    suspend fun deletePlayerHistory(playerId: Long) {
        playerHistoryDao.deletePlayerHistory(playerId)
    }
}