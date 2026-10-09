package com.alimapps.senbombardir.ui.model

import com.alimapps.senbombardir.ui.model.types.TeamResultOption

data class TeamResultUiModel(
    val teamUiModel: TeamUiModel,
    val option: TeamResultOption,
) {

    val value: Int = teamUiModel.valueOf(option)
}

fun TeamUiModel.valueOf(option: TeamResultOption): Int =
    when (option) {
        TeamResultOption.Games -> games
        TeamResultOption.Wins -> wins
        TeamResultOption.Draws -> draws
        TeamResultOption.Loses -> loses
        TeamResultOption.Goals -> goals
        TeamResultOption.Conceded -> conceded
        TeamResultOption.Points -> points
    }

fun TeamUiModel.withValue(option: TeamResultOption, value: Int): TeamUiModel =
    when (option) {
        TeamResultOption.Games -> copy(games = value)
        TeamResultOption.Wins -> copy(wins = value)
        TeamResultOption.Draws -> copy(draws = value)
        TeamResultOption.Loses -> copy(loses = value)
        TeamResultOption.Goals -> copy(goals = value)
        TeamResultOption.Conceded -> copy(conceded = value)
        TeamResultOption.Points -> copy(points = value)
    }
