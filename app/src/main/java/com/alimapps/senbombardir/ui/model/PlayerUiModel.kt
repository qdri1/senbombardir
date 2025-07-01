package com.alimapps.senbombardir.ui.model

import com.alimapps.senbombardir.data.model.PlayerHistoryModel
import com.alimapps.senbombardir.data.model.PlayerModel
import com.alimapps.senbombardir.ui.model.types.TeamColor

data class PlayerUiModel(
    val id: Long,
    val teamId: Long,
    val teamColor: TeamColor,
    val teamName: String,
    val teamPoints: Int,
    val teamGoalsDifference: Int,
    val name: String,
    val goals: Int,
    val assists: Int,
    val dribbles: Int,
    val passes: Int,
    val shots: Int,
    val saves: Int,
)

fun PlayerUiModel.toPlayerModel(): PlayerModel =
    PlayerModel(
        id = id,
        teamId = teamId,
        name = name,
        goals = goals,
        assists = assists,
        dribbles = dribbles,
        passes = passes,
        shots = shots,
        saves = saves,
    )

fun PlayerUiModel.toPlayerHistoryModel(): PlayerHistoryModel =
    PlayerHistoryModel(
        id = id,
        teamId = teamId,
        name = name,
        goals = goals,
        assists = assists,
        dribbles = dribbles,
        passes = passes,
        shots = shots,
        saves = saves,
    )