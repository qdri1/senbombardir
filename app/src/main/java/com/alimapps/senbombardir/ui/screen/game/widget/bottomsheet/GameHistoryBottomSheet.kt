package com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.platform.LocalResources
import kotlinx.coroutines.delay

private enum class AutoScrollDirection { None, Up, Down }

private enum class ScrollSpeed(val delta: Float, val label: String) {
    X1(3f, "1x"),
    X2(6f, "2x"),
    X3(12f, "3x"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameHistoryBottomSheet(
    gameHistory: List<GameHistoryEntryUiModel>,
    onDismissed: () -> Unit,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val scrollState = rememberScrollState()
    var autoScroll by remember { mutableStateOf(AutoScrollDirection.None) }
    var speed by remember { mutableStateOf(ScrollSpeed.X1) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.Drag) {
                    autoScroll = AutoScrollDirection.None
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(autoScroll, speed) {
        while (autoScroll != AutoScrollDirection.None) {
            val delta = if (autoScroll == AutoScrollDirection.Down) speed.delta else -speed.delta
            scrollState.scrollBy(delta)
            if (autoScroll == AutoScrollDirection.Down && scrollState.value >= scrollState.maxValue) {
                autoScroll = AutoScrollDirection.None
            } else if (autoScroll == AutoScrollDirection.Up && scrollState.value <= 0) {
                autoScroll = AutoScrollDirection.None
            }
            delay(16)
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismissed() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .nestedScroll(nestedScrollConnection)
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.function_history),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    if (gameHistory.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "ShareHistory",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    shareText(context, buildHistoryText(resources, gameHistory))
                                }
                        )
                    }
                }

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

            if (gameHistory.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ScrollButton(
                        active = autoScroll == AutoScrollDirection.Up,
                        onClick = {
                            autoScroll = if (autoScroll == AutoScrollDirection.Up) {
                                AutoScrollDirection.None
                            } else {
                                AutoScrollDirection.Up
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Scroll up",
                            tint = if (autoScroll == AutoScrollDirection.Up)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    ScrollButton(
                        active = false,
                        onClick = {
                            speed = when (speed) {
                                ScrollSpeed.X1 -> ScrollSpeed.X2
                                ScrollSpeed.X2 -> ScrollSpeed.X3
                                ScrollSpeed.X3 -> ScrollSpeed.X1
                            }
                        },
                    ) {
                        Text(
                            text = speed.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    ScrollButton(
                        active = autoScroll == AutoScrollDirection.Down,
                        onClick = {
                            autoScroll = if (autoScroll == AutoScrollDirection.Down) {
                                AutoScrollDirection.None
                            } else {
                                AutoScrollDirection.Down
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Scroll down",
                            tint = if (autoScroll == AutoScrollDirection.Down)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScrollButton(
    active: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                color = if (active) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape,
            )
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun GameHistoryEntryItem(entry: GameHistoryEntryUiModel) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundBrush = when {
        entry.winnerTeamName.isEmpty() -> null
        entry.winnerTeamName == entry.leftTeamName -> Brush.horizontalGradient(
            listOf(
                parseHexColor(entry.leftTeamColor.hexColor).copy(alpha = 0.25f),
                surfaceColor,
            )
        )
        else -> Brush.horizontalGradient(
            listOf(
                surfaceColor,
                parseHexColor(entry.rightTeamColor.hexColor).copy(alpha = 0.25f),
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (backgroundBrush != null) Modifier.background(backgroundBrush) else Modifier)
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
                modifier = Modifier.weight(1f),
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
            Row(modifier = Modifier.fillMaxWidth()) {
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

private fun buildHistoryText(resources: Resources, gameHistory: List<GameHistoryEntryUiModel>): String {
    val sb = StringBuilder()
    gameHistory.forEach { entry ->
        sb.appendLine("──────────────")
        sb.appendLine()
        val durationPart = entry.durationFormatted?.let { " | ⏱ $it" } ?: ""
        sb.appendLine("${resources.getString(R.string.game_history_game_number, entry.gameNumber)}$durationPart")
        sb.appendLine()
        sb.appendLine("${entry.leftTeamName}  ${entry.leftTeamGoals} - ${entry.rightTeamGoals}  ${entry.rightTeamName}")
        if (entry.actionEvents.isNotEmpty()) {
            sb.appendLine()
            entry.actionEvents.forEach { event ->
                val actionLabel = when (event.actionType) {
                    "goal" -> "${resources.getString(R.string.text_goal)} ⚽"
                    "assist" -> resources.getString(R.string.text_assist)
                    "save" -> resources.getString(R.string.text_save)
                    "dribble" -> resources.getString(R.string.text_dribble)
                    "pass" -> resources.getString(R.string.text_pass)
                    "shot" -> resources.getString(R.string.text_shot)
                    "yellowCard" -> resources.getString(R.string.text_yellow_card)
                    "redCard" -> resources.getString(R.string.text_red_card)
                    else -> event.actionType
                }
                sb.appendLine("${event.elapsedFormatted}  ${event.playerName} — $actionLabel")
            }
        }
        sb.appendLine()
    }
    return sb.toString().trimEnd()
}

private fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, null))
}