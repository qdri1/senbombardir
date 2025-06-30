package com.alimapps.senbombardir.ui.model.types

import com.alimapps.senbombardir.R

enum class LiveGameBlockInfo(val drawableRes: Int, val descriptionRes: Int) {
    ReplaceTeams(drawableRes = R.drawable.ic_change, descriptionRes = R.string.live_game_info_replace_teams),
    PauseTimer(drawableRes = R.drawable.ic_pause_circled, descriptionRes = R.string.live_game_info_pause_timer),
    PlayTimer(drawableRes = R.drawable.ic_play_circled, descriptionRes = R.string.live_game_info_play_timer),
}

enum class TeamsBlockInfo(val stringRes: Int, val descriptionRes: Int) {
    Games(stringRes = R.string.team_result_games, descriptionRes = R.string.teams_block_info_games),
    Wins(stringRes = R.string.team_result_wins, descriptionRes = R.string.teams_block_info_wins),
    Draws(stringRes = R.string.team_result_draws, descriptionRes = R.string.teams_block_info_draws),
    Loses(stringRes = R.string.team_result_loses, descriptionRes = R.string.teams_block_info_loses),
    GoalsConceded(stringRes = R.string.team_result_goals_conceded, descriptionRes = R.string.teams_block_info_goals_conceded),
    GoalsDifference(stringRes = R.string.team_result_goals_difference, descriptionRes = R.string.teams_block_info_goals_difference),
    Points(stringRes = R.string.team_result_points, descriptionRes = R.string.teams_block_info_points),
}

enum class PlayersBlockInfo(val stringRes: Int, val descriptionRes: Int) {
    Goals(stringRes = R.string.player_result_goals, descriptionRes = R.string.players_block_info_goals),
    Assists(stringRes = R.string.player_result_assists, descriptionRes = R.string.players_block_info_assists),
    Saves(stringRes = R.string.player_result_saves, descriptionRes = R.string.players_block_info_saves),
    Dribbles(stringRes = R.string.player_result_dribbles, descriptionRes = R.string.players_block_info_dribbles),
    Shots(stringRes = R.string.player_result_shots, descriptionRes = R.string.players_block_info_shots),
    Passes(stringRes = R.string.player_result_passes, descriptionRes = R.string.players_block_info_passes),
}