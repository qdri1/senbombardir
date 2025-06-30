package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.LiveGameUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.ui.screen.game.GameAction
import com.alimapps.senbombardir.ui.screen.game.GameUiState
import com.alimapps.senbombardir.ui.screen.game.widget.dropdown.LeftTeamChangeDropdown
import com.alimapps.senbombardir.ui.screen.game.widget.dropdown.LeftTeamOptionsDropdown
import com.alimapps.senbombardir.ui.screen.game.widget.dropdown.RightTeamChangeDropdown
import com.alimapps.senbombardir.ui.screen.game.widget.dropdown.RightTeamOptionsDropdown
import com.alimapps.senbombardir.ui.utils.parseHexColor

@Composable
fun LiveGameBlock(
    liveGameUiModel: LiveGameUiModel,
    timerValueState: State<String>,
    uiState: GameUiState,
    onAction: (GameAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 20.dp)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = timerValueState.value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (liveGameUiModel.isLive) {
            val painterId = if (uiState.isTimerPlay) R.drawable.ic_pause_circled else R.drawable.ic_play_circled
            Icon(
                painter = painterResource(id = painterId),
                contentDescription = "GameScreenChangeIcon",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onAction(GameAction.OnTimerClicked) }
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_change),
                contentDescription = "GameScreenChangeIcon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onAction(GameAction.OnTeamChangeIconClicked) }
            )
        }

        Box {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = liveGameUiModel.leftTeamName,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onAction(GameAction.OnLeftTeamClicked) }
                        )
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = parseHexColor(liveGameUiModel.leftTeamColor.hexColor),
                            shape = RoundedCornerShape(4.dp),
                        )
                        .clip(RoundedCornerShape(4.dp))
                )
                Text(
                    text = liveGameUiModel.leftTeamGoals.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "-",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = liveGameUiModel.rightTeamGoals.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = parseHexColor(liveGameUiModel.rightTeamColor.hexColor),
                            shape = RoundedCornerShape(4.dp),
                        )
                        .clip(RoundedCornerShape(4.dp))
                )
                Text(
                    text = liveGameUiModel.rightTeamName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onAction(GameAction.OnRightTeamClicked) }
                        )
                )
            }
            when {
                uiState.showLeftTeamOptionsDropdown -> LeftTeamOptionsDropdown(onAction)
                uiState.showRightTeamOptionsDropdown -> RightTeamOptionsDropdown(onAction)
                uiState.showLeftTeamChangeDropdown -> LeftTeamChangeDropdown(
                    teamUiModelList = uiState.teamUiModelList.filter {
                        it.id !in listOf(
                            liveGameUiModel.leftTeamId,
                            liveGameUiModel.rightTeamId,
                        )
                    },
                    onAction = onAction,
                )
                uiState.showRightTeamChangeDropdown -> RightTeamChangeDropdown(
                    teamUiModelList = uiState.teamUiModelList.filter {
                        it.id !in listOf(
                            liveGameUiModel.leftTeamId,
                            liveGameUiModel.rightTeamId,
                        )
                    },
                    onAction = onAction,
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Button(
                onClick = { onAction(GameAction.OnStartFinishButtonClicked) },
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 16.dp
                ),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = if (liveGameUiModel.isLive) parseHexColor(TeamColor.Red.hexColor) else MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(
                    text = if (liveGameUiModel.isLive) "FINISH \uD83C\uDFC1" else "GO ⚽\uFE0F",
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            if (liveGameUiModel.isLive) {
                Text(
                    text = "Live",
                    color = parseHexColor(TeamColor.Red.hexColor),
                    style = MaterialTheme.typography.labelSmall,
                )
            } else {
                Text(
                    text = stringResource(id = R.string.game_count, liveGameUiModel.gameCount.toString()),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}