package com.alimapps.senbombardir.ui.model.types

import com.alimapps.senbombardir.R

enum class TeamResultOption(val stringRes: Int) {
    Games(stringRes = R.string.teams_block_info_games),
    Wins(stringRes = R.string.teams_block_info_wins),
    Draws(stringRes = R.string.teams_block_info_draws),
    Loses(stringRes = R.string.teams_block_info_loses),
    Goals(stringRes = R.string.team_result_option_goals),
    Conceded(stringRes = R.string.team_result_option_conceded),
    Points(stringRes = R.string.teams_block_info_points),
}
