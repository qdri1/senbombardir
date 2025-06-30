package com.alimapps.senbombardir.ui.model.types

import com.alimapps.senbombardir.R

enum class TeamOption(val stringRes: Int) {
    Goal(stringRes = R.string.team_option_goal),
    Assist(stringRes = R.string.team_option_assist),
    Save(stringRes = R.string.team_option_save),
    Dribble(stringRes = R.string.team_option_dribble),
    Shot(stringRes = R.string.team_option_shot),
    Pass(stringRes = R.string.team_option_pass),
}