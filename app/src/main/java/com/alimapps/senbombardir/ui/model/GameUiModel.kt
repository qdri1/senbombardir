package com.alimapps.senbombardir.ui.model

import com.alimapps.senbombardir.data.model.GameModel
import com.alimapps.senbombardir.ui.model.types.GameFormat
import com.alimapps.senbombardir.ui.model.types.GameRule
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam2
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam3
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam4
import com.alimapps.senbombardir.ui.model.types.TeamQuantity
import com.alimapps.senbombardir.utils.empty

data class GameUiModel(
    val id: Long,
    val name: String,
    val gameFormat: GameFormat,
    val teamQuantity: TeamQuantity,
    val gameRule: GameRule,
    val timeInMinutes: Int,
    val modifiedAt: Long = 0L,
)

fun GameUiModel.toGameModel(): GameModel {
    val rule = when (val localGameRule = gameRule) {
        is GameRuleTeam2 -> localGameRule.name
        is GameRuleTeam3 -> localGameRule.name
        is GameRuleTeam4 -> localGameRule.name
        else -> String.empty
    }
    return GameModel(
        id = id,
        name = name,
        format = gameFormat.format,
        teamQuantity = teamQuantity.quantity,
        rule = rule,
        timeInMinutes = timeInMinutes,
        modifiedAt = modifiedAt,
    )
}