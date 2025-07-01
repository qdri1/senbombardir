package com.alimapps.senbombardir.data.repository

import com.alimapps.senbombardir.data.model.TeamHistoryModel
import com.alimapps.senbombardir.data.source.TeamHistoryDao
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor

class TeamHistoryRepository(
    private val teamHistoryDao: TeamHistoryDao,
) {

    suspend fun getTeamsHistories(gameId: Long): List<TeamUiModel> {
        val teams = teamHistoryDao.getTeamsHistories(gameId).map {
            TeamUiModel(
                id = it.id,
                gameId = it.gameId,
                name = it.name,
                color = TeamColor.getTeamColor(hexColor = it.color),
                games = it.games,
                wins = it.wins,
                draws = it.draws,
                loses = it.loses,
                goals = it.goals,
                conceded = it.conceded,
                points = it.points,
            )
        }

        return teams
    }

    suspend fun getTeamHistory(teamId: Long): TeamUiModel? {
        val teamHistoryModel = teamHistoryDao.getTeamHistory(teamId)

        return teamHistoryModel?.let {
            TeamUiModel(
                id = it.id,
                gameId = it.gameId,
                name = it.name,
                color = TeamColor.getTeamColor(hexColor = it.color),
                games = it.games,
                wins = it.wins,
                draws = it.draws,
                loses = it.loses,
                goals = it.goals,
                conceded = it.conceded,
                points = it.points,
            )
        }
    }
    
    suspend fun saveTeamHistory(teamHistoryModel: TeamHistoryModel) {
        teamHistoryDao.insertTeamHistory(teamHistoryModel)
    }

    suspend fun updateTeamHistory(teamHistoryModel: TeamHistoryModel) {
        teamHistoryDao.updateTeamHistory(teamHistoryModel)
    }
}