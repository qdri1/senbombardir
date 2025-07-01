package com.alimapps.senbombardir.data.repository

import com.alimapps.senbombardir.data.model.PlayerModel
import com.alimapps.senbombardir.data.source.PlayerDao
import com.alimapps.senbombardir.data.source.TeamDao
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.utils.empty
import com.alimapps.senbombardir.utils.orDefault

class PlayerRepository(
    private val teamDao: TeamDao,
    private val playerDao: PlayerDao,
) {

    suspend fun getPlayers(teamId: Long): List<PlayerUiModel> {
        val teamModel = teamDao.getTeam(teamId)
        val teamGoalsDifference = teamModel?.let { it.goals - it.conceded }.orDefault()

        val players = playerDao.getPlayers(teamId).map {
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

    suspend fun getPlayer(playerId: Long): PlayerUiModel? {
        val playerModel = playerDao.getPlayer(playerId)

        return playerModel?.let {
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
    
    suspend fun savePlayer(playerModel: PlayerModel): Long = playerDao.insertPlayer(playerModel)

    suspend fun updatePlayer(playerModel: PlayerModel) { playerDao.updatePlayer(playerModel) }

    suspend fun deletePlayer(playerModel: PlayerModel) { playerDao.deletePlayer(playerModel) }

    suspend fun deletePlayer(playerId: Long) { playerDao.deletePlayer(playerId) }
}