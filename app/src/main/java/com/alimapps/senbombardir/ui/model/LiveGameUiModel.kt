package com.alimapps.senbombardir.ui.model

import com.alimapps.senbombardir.data.model.LiveGameModel
import com.alimapps.senbombardir.ui.model.types.TeamColor

data class LiveGameUiModel(
    val id: Long,
    val gameId: Long,
    val leftTeamId: Long,
    val leftTeamName: String,
    val leftTeamColor: TeamColor,
    val leftTeamGoals: Int,
    val leftTeamWinCount: Int,
    val rightTeamId: Long,
    val rightTeamName: String,
    val rightTeamColor: TeamColor,
    val rightTeamGoals: Int,
    val rightTeamWinCount: Int,
    val gameCount: Int,
    val isLive: Boolean,
) {

    val isLeftTeamWin: Boolean get() = leftTeamGoals > rightTeamGoals
    val isRightTeamWin: Boolean get() = leftTeamGoals < rightTeamGoals
}

fun LiveGameUiModel.toLiveGameModel(): LiveGameModel =
    LiveGameModel(
        id = id,
        gameId = gameId,
        leftTeamId = leftTeamId,
        leftTeamName = leftTeamName,
        leftTeamColor = leftTeamColor.hexColor,
        leftTeamGoals = leftTeamGoals,
        leftTeamWinCount = leftTeamWinCount,
        rightTeamId = rightTeamId,
        rightTeamName = rightTeamName,
        rightTeamColor = rightTeamColor.hexColor,
        rightTeamGoals = rightTeamGoals,
        rightTeamWinCount = rightTeamWinCount,
        gameCount = gameCount,
        isLive = isLive,
    )