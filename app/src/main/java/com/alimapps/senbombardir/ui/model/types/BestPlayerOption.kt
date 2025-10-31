package com.alimapps.senbombardir.ui.model.types

import com.alimapps.senbombardir.R

enum class BestPlayerOption(val stringRes: Int) {
    BestPlayer(stringRes = R.string.best_player_option_best_player),
    Goals(stringRes = R.string.best_player_option_goals),
    Assists(stringRes = R.string.best_player_option_assists),
    Saves(stringRes = R.string.best_player_option_saves),
    Dribbles(stringRes = R.string.best_player_option_dribbles),
    Passes(stringRes = R.string.best_player_option_passes),
    Shots(stringRes = R.string.best_player_option_shots),
}