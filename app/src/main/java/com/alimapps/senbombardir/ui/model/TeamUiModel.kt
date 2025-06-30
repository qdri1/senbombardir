package com.alimapps.senbombardir.ui.model

import com.alimapps.senbombardir.data.model.TeamModel
import com.alimapps.senbombardir.ui.model.types.TeamColor

data class TeamUiModel(
    val id: Long,
    val gameId: Long,
    val name: String,
    val color: TeamColor,
    val games: Int,
    val wins: Int,
    val draws: Int,
    val loses: Int,
    val goals: Int,
    val conceded: Int,
    val points: Int,
) {

    val goalsDifference = goals - conceded
}

fun TeamUiModel.toTeamModel(): TeamModel =
    TeamModel(
        id = id,
        gameId = gameId,
        name = name,
        color = color.hexColor,
        games = games,
        wins = wins,
        draws = draws,
        loses = loses,
        goals = goals,
        conceded = conceded,
        points = points,
    )