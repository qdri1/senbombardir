package com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.GameHistoryActionEventUiModel
import com.alimapps.senbombardir.ui.model.GameHistoryEntryUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.ui.utils.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameHistoryBottomSheet(
    gameHistory: List<GameHistoryEntryUiModel>,
    onDismissed: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissed() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.function_history),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
            )

            if (gameHistory.isEmpty()) {
                Text(
                    text = stringResource(R.string.game_history_empty),
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                )
            } else {
                gameHistory.forEach { entry ->
                    GameHistoryEntryItem(entry = entry)
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
private fun GameHistoryEntryItem(entry: GameHistoryEntryUiModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Game number + duration
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.game_history_game_number, entry.gameNumber),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelSmall,
            )
            entry.durationFormatted?.let { duration ->
                Text(
                    text = stringResource(R.string.game_history_duration, duration),
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Score row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    text = entry.leftTeamName,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            TeamColorDot(teamColor = entry.leftTeamColor)
            Text(
                text = "${entry.leftTeamGoals} - ${entry.rightTeamGoals}",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            TeamColorDot(teamColor = entry.rightTeamColor)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = entry.rightTeamName,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        // Winner / draw
        if (entry.winnerTeamName.isEmpty()) {
            Text(
                text = stringResource(R.string.game_history_draw),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                val leftTeamText = if (entry.winnerTeamName == entry.leftTeamName) {
                    stringResource(R.string.game_history_winner)
                } else {
                    stringResource(R.string.game_history_loser)
                }
                val leftTeamColor = if (entry.winnerTeamName == entry.leftTeamName) {
                    MaterialTheme.colorScheme.primary
                } else {
                    parseHexColor(TeamColor.Red.hexColor)
                }
                val leftTeamStyle = if (entry.winnerTeamName == entry.leftTeamName) {
                    MaterialTheme.typography.labelLarge
                } else {
                    MaterialTheme.typography.labelMedium
                }
                Text(
                    text = leftTeamText,
                    color = leftTeamColor,
                    style = leftTeamStyle,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )

                val rightTeamText = if (entry.winnerTeamName == entry.rightTeamName) {
                    stringResource(R.string.game_history_winner)
                } else {
                    stringResource(R.string.game_history_loser)
                }
                val rightTeamColor = if (entry.winnerTeamName == entry.rightTeamName) {
                    MaterialTheme.colorScheme.primary
                } else {
                    parseHexColor(TeamColor.Red.hexColor)
                }
                val rightTeamStyle = if (entry.winnerTeamName == entry.rightTeamName) {
                    MaterialTheme.typography.labelLarge
                } else {
                    MaterialTheme.typography.labelMedium
                }
                Text(
                    text = rightTeamText,
                    color = rightTeamColor,
                    style = rightTeamStyle,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Action timeline
        if (entry.actionEvents.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            entry.actionEvents.forEach { event ->
                ActionEventRow(event = event)
            }
        }
    }
}

@Composable
private fun TeamColorDot(teamColor: TeamColor) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .background(
                color = parseHexColor(teamColor.hexColor),
                shape = RoundedCornerShape(4.dp),
            )
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = if (teamColor == TeamColor.White) 1.dp else 0.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp),
            )
    )
}

@Composable
private fun PlayerTeamColorDot(teamColor: TeamColor) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(
                color = parseHexColor(teamColor.hexColor),
                shape = RoundedCornerShape(4.dp),
            )
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = if (teamColor == TeamColor.White) 1.dp else 0.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp),
            )
    )
}

@Composable
private fun ActionEventRow(event: GameHistoryActionEventUiModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = event.elapsedFormatted,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
        )
        PlayerTeamColorDot(teamColor = event.teamColor)
        Text(
            text = event.playerName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = actionTypeLabel(event.actionType),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
private fun actionTypeLabel(actionType: String): String = when (actionType) {
    "goal" -> stringResource(R.string.text_goal) + " ⚽\uFE0F"
    "assist" -> stringResource(R.string.text_assist)
    "save" -> stringResource(R.string.text_save)
    "dribble" -> stringResource(R.string.text_dribble)
    "pass" -> stringResource(R.string.text_pass)
    "shot" -> stringResource(R.string.text_shot)
    "yellowCard" -> stringResource(R.string.text_yellow_card)
    "redCard" -> stringResource(R.string.text_red_card)
    else -> actionType
}
