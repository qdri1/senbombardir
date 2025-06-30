package com.alimapps.senbombardir.ui.model.types

import com.alimapps.senbombardir.R

interface GameRule {
    val stringRes: Int

    companion object {

        fun getGameRule(
            teamQuantity: TeamQuantity,
            rule: String,
        ): GameRule = when (teamQuantity) {
            TeamQuantity.Team2 -> GameRuleTeam2.getGameRule(rule)
            TeamQuantity.Team3 -> GameRuleTeam3.getGameRule(rule)
            TeamQuantity.Team4 -> GameRuleTeam4.getGameRule(rule)
        }
    }
}

enum class GameRuleTeam2(override val stringRes: Int) : GameRule {
    AFTER_TIME_CHANGE_SIDE(stringRes = R.string.game_rule_2_change_side),
    AFTER_TIME_STAY_SIDE(stringRes = R.string.game_rule_2_stay_side),
    ;

    companion object {

        fun getGameRule(
            rule: String,
            default: GameRuleTeam2 = AFTER_TIME_CHANGE_SIDE,
        ): GameRuleTeam2 = entries.find { it.name == rule } ?: default
    }
}

enum class GameRuleTeam3(override val stringRes: Int) : GameRule {
    ONLY_2_GAMES(stringRes = R.string.game_rule_3_only_2_games),
    WINNER_STAY_2(stringRes = R.string.game_rule_3_winner_stay_2),
    WINNER_STAY_3(stringRes = R.string.game_rule_3_winner_stay_3),
    WINNER_STAY_4(stringRes = R.string.game_rule_3_winner_stay_4),
    WINNER_STAY_UNLIMITED(stringRes = R.string.game_rule_3_winner_stay_unlimited),
    ;

    companion object {

        fun getGameRule(
            rule: String,
            default: GameRuleTeam3 = ONLY_2_GAMES,
        ): GameRuleTeam3 = entries.find { it.name == rule } ?: default
    }
}

enum class GameRuleTeam4(override val stringRes: Int) : GameRule {
    ONLY_3_GAMES(stringRes = R.string.game_rule_4_only_3_games),
    WINNER_STAY_3(stringRes = R.string.game_rule_4_winner_stay_3),
    WINNER_STAY_4(stringRes = R.string.game_rule_4_winner_stay_4),
    WINNER_STAY_5(stringRes = R.string.game_rule_4_winner_stay_5),
    WINNER_STAY_6(stringRes = R.string.game_rule_4_winner_stay_6),
    WINNER_STAY_UNLIMITED(stringRes = R.string.game_rule_4_winner_stay_unlimited),
    ;

    companion object {

        fun getGameRule(
            rule: String,
            default: GameRuleTeam4 = ONLY_3_GAMES,
        ): GameRuleTeam4 = entries.find { it.name == rule } ?: default
    }
}