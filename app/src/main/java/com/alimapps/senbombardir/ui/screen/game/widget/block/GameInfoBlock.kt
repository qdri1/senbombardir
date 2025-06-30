package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.GameUiModel
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam2
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam3
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam4
import com.alimapps.senbombardir.utils.empty

@Composable
fun GameInfoBlock(
    gameUiModel: GameUiModel,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 12.dp)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(id = R.string.home_game_format),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = gameUiModel.gameFormat.format,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(id = R.string.home_game_team_quantity),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = gameUiModel.teamQuantity.quantity.toString(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(id = R.string.home_game_time),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = stringResource(id = R.string.time_in_minutes, gameUiModel.timeInMinutes),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(id = R.string.home_game_rule),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = when (val gameRule = gameUiModel.gameRule) {
                    is GameRuleTeam2 -> stringResource(id = gameRule.stringRes)
                    is GameRuleTeam3 -> stringResource(id = gameRule.stringRes)
                    is GameRuleTeam4 -> stringResource(id = gameRule.stringRes)
                    else -> String.empty
                },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}