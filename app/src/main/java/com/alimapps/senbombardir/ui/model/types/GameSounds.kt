package com.alimapps.senbombardir.ui.model.types

import com.alimapps.senbombardir.R

enum class GameSounds(val stringRes: Int, val rawRes: Int) {
    Whistle(stringRes = R.string.sound_whistle, rawRes = R.raw.whistle),
    StadiumApplause(stringRes = R.string.sound_stadium_applause, rawRes = R.raw.stadium_applause),
    GirlsApplause(stringRes = R.string.sound_girls_applause, rawRes = R.raw.girls_applause),
    BilgeninIstepJatyr(stringRes = R.string.sound_bilgenin_istep_jatyr, rawRes = R.raw.bilgenin_istep_jatyr),
    GoalSave(stringRes = R.string.sound_goal_save, rawRes = R.raw.goal_save),
    Suiii(stringRes = R.string.sound_suiii, rawRes = R.raw.suiiiii),
    SuiiiFull(stringRes = R.string.sound_suiii_full, rawRes = R.raw.suiii_full),
    GoalGoalGoal(stringRes = R.string.sound_goal_goal_goal, rawRes = R.raw.gol_gol_gol),
    ModrichtynPasy(stringRes = R.string.sound_modrichtyn_pasy, rawRes = R.raw.modrichtyn_pasy),
    TondyrypTastagan(stringRes = R.string.sound_tondyryp_tastagan, rawRes = R.raw.tondyryp_tastagan),
}
