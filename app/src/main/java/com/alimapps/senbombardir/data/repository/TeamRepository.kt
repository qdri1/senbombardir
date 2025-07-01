package com.alimapps.senbombardir.data.repository

import com.alimapps.senbombardir.data.model.TeamModel
import com.alimapps.senbombardir.data.source.TeamDao
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor

class TeamRepository(
    private val teamDao: TeamDao,
) {

    suspend fun getTeams(gameId: Long): List<TeamUiModel> {
        val teams = teamDao.getTeams(gameId).map {
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
    
    suspend fun saveTeam(teamModel: TeamModel): Long = teamDao.insertTeam(teamModel)

    suspend fun updateTeam(teamModel: TeamModel) { teamDao.updateTeam(teamModel) }
}