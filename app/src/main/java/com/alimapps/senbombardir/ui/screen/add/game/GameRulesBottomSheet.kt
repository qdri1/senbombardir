package com.alimapps.senbombardir.ui.screen.add.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.types.GameRule
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam2
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam3
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameRulesBottomSheet(
    rules: List<GameRule>,
    selectedRule: GameRule?,
    onDismissed: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissed() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Text(
            text = stringResource(R.string.game_rules),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            rules.forEach { rule ->
                GameRuleInfoCard(
                    rule = rule,
                    isSelected = rule == selectedRule,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GameRuleInfoCard(
    rule: GameRule,
    isSelected: Boolean,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = shape,
            )
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(rule.stringRes),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = rule.description(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                .padding(12.dp),
        ) {
            Text(
                text = stringResource(R.string.game_rule_example),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
            Text(
                text = rule.example(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun GameRule.description(): String = when (this) {
    GameRuleTeam2.AFTER_TIME_CHANGE_SIDE -> stringResource(R.string.game_rule_description_2_change_side)
    GameRuleTeam2.AFTER_TIME_STAY_SIDE -> stringResource(R.string.game_rule_description_2_stay_side)
    GameRuleTeam3.ONLY_2_GAMES -> stringResource(R.string.game_rule_description_3_only_2_games)
    GameRuleTeam3.WINNER_STAY_3 -> stringResource(R.string.game_rule_description_3_winner_stay, 3)
    GameRuleTeam3.WINNER_STAY_4 -> stringResource(R.string.game_rule_description_3_winner_stay, 4)
    GameRuleTeam3.WINNER_STAY_UNLIMITED -> stringResource(R.string.game_rule_description_3_winner_stay_unlimited)
    GameRuleTeam4.ONLY_2_GAMES -> stringResource(R.string.game_rule_description_4_only_2_games)
    GameRuleTeam4.ROUND_ROBIN -> stringResource(R.string.game_rule_description_4_round_robin)
    GameRuleTeam4.WINNER_STAY_3 -> stringResource(R.string.game_rule_description_4_winner_stay, 3)
    GameRuleTeam4.WINNER_STAY_4 -> stringResource(R.string.game_rule_description_4_winner_stay, 4)
    GameRuleTeam4.WINNER_STAY_5 -> stringResource(R.string.game_rule_description_4_winner_stay, 5)
    GameRuleTeam4.WINNER_STAY_6 -> stringResource(R.string.game_rule_description_4_winner_stay, 6)
    GameRuleTeam4.WINNER_STAY_UNLIMITED -> stringResource(R.string.game_rule_description_4_winner_stay_unlimited)
    else -> ""
}

@Composable
private fun GameRule.example(): String = when (this) {
    GameRuleTeam2.AFTER_TIME_CHANGE_SIDE -> stringResource(R.string.game_rule_example_2_change_side)
    GameRuleTeam2.AFTER_TIME_STAY_SIDE -> stringResource(R.string.game_rule_example_2_stay_side)
    GameRuleTeam3.ONLY_2_GAMES -> stringResource(R.string.game_rule_example_3_only_2_games)
    GameRuleTeam3.WINNER_STAY_3 -> stringResource(R.string.game_rule_example_3_winner_stay, 3)
    GameRuleTeam3.WINNER_STAY_4 -> stringResource(R.string.game_rule_example_3_winner_stay, 4)
    GameRuleTeam3.WINNER_STAY_UNLIMITED -> stringResource(R.string.game_rule_example_3_winner_stay_unlimited)
    GameRuleTeam4.ONLY_2_GAMES -> stringResource(R.string.game_rule_example_4_only_2_games)
    GameRuleTeam4.ROUND_ROBIN -> stringResource(R.string.game_rule_example_4_round_robin)
    GameRuleTeam4.WINNER_STAY_3 -> stringResource(R.string.game_rule_example_4_winner_stay, 3)
    GameRuleTeam4.WINNER_STAY_4 -> stringResource(R.string.game_rule_example_4_winner_stay, 4)
    GameRuleTeam4.WINNER_STAY_5 -> stringResource(R.string.game_rule_example_4_winner_stay, 5)
    GameRuleTeam4.WINNER_STAY_6 -> stringResource(R.string.game_rule_example_4_winner_stay, 6)
    GameRuleTeam4.WINNER_STAY_UNLIMITED -> stringResource(R.string.game_rule_example_4_winner_stay_unlimited)
    else -> ""
}
