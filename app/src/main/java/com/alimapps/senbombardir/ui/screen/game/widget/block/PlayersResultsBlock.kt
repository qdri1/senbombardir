package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.PlayerResultUiModel
import com.alimapps.senbombardir.ui.model.PlayerUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.ui.model.types.TeamOption
import com.alimapps.senbombardir.ui.utils.parseHexColor

@Composable
fun PlayersResultsBlock(
    playerUiModelList: List<PlayerUiModel>,
    uiLimited: Boolean,
    onPlayerResultClicked: (PlayerResultUiModel) -> Unit,
) {
    Box(
        modifier = Modifier.padding(top = 16.dp),
    ) {
        if (uiLimited) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "GameScreenLockIcon",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.player_result_limited_text),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 12.dp)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                val verticalSpace = 8.dp
                val headerTextColor = MaterialTheme.colorScheme.outline
                val headerTextStyle = MaterialTheme.typography.labelSmall
                val textColor = MaterialTheme.colorScheme.onSurface
                val textStyle = MaterialTheme.typography.labelSmall

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(verticalSpace),
                    ) {
                        Text(
                            text = stringResource(id = R.string.player_result_place),
                            color = headerTextColor,
                            style = headerTextStyle,
                        )
                        playerUiModelList.forEachIndexed { index, _ ->
                            Text(
                                text = (index + 1).toString(),
                                color = textColor,
                                style = textStyle,
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(verticalSpace),
                    ) {
                        Text(
                            text = stringResource(id = R.string.player_result_name),
                            color = headerTextColor,
                            style = headerTextStyle,
                        )
                        playerUiModelList.forEach { playerUiModel ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = parseHexColor(playerUiModel.teamColor.hexColor),
                                            shape = RoundedCornerShape(4.dp),
                                        )
                                        .clip(RoundedCornerShape(4.dp))
                                        .border(
                                            width = if (playerUiModel.teamColor == TeamColor.White) 1.dp else 0.dp,
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(4.dp),
                                        )
                                )
                                Text(
                                    text = playerUiModel.name,
                                    autoSize = TextAutoSize.StepBased(
                                        minFontSize = 10.sp,
                                        maxFontSize = 14.sp,
                                        stepSize = 1.sp
                                    ),
                                    color = textColor,
                                    style = textStyle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(verticalSpace),
                    ) {
                        Text(
                            text = stringResource(id = R.string.player_result_goals),
                            color = headerTextColor,
                            style = headerTextStyle,
                        )
                        playerUiModelList.forEach { playerUiModel ->
                            Text(
                                text = playerUiModel.goals.toString(),
                                color = textColor,
                                style = textStyle,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onPlayerResultClicked(PlayerResultUiModel(playerUiModel, TeamOption.Goal)) }
                                    )
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(verticalSpace),
                    ) {
                        Text(
                            text = stringResource(id = R.string.player_result_assists),
                            color = headerTextColor,
                            style = headerTextStyle,
                        )
                        playerUiModelList.forEach { playerUiModel ->
                            Text(
                                text = playerUiModel.assists.toString(),
                                color = textColor,
                                style = textStyle,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onPlayerResultClicked(PlayerResultUiModel(playerUiModel, TeamOption.Assist)) }
                                    )
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(verticalSpace),
                    ) {
                        Text(
                            text = stringResource(id = R.string.player_result_saves),
                            color = headerTextColor,
                            style = headerTextStyle,
                        )
                        playerUiModelList.forEach { playerUiModel ->
                            Text(
                                text = playerUiModel.saves.toString(),
                                color = textColor,
                                style = textStyle,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onPlayerResultClicked(PlayerResultUiModel(playerUiModel, TeamOption.Save)) }
                                    )
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(verticalSpace),
                    ) {
                        Text(
                            text = stringResource(id = R.string.player_result_dribbles),
                            color = headerTextColor,
                            style = headerTextStyle,
                        )
                        playerUiModelList.forEach { playerUiModel ->
                            Text(
                                text = playerUiModel.dribbles.toString(),
                                color = textColor,
                                style = textStyle,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onPlayerResultClicked(PlayerResultUiModel(playerUiModel, TeamOption.Dribble)) }
                                    )
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(verticalSpace),
                    ) {
                        Text(
                            text = stringResource(id = R.string.player_result_shots),
                            color = headerTextColor,
                            style = headerTextStyle,
                        )
                        playerUiModelList.forEach { playerUiModel ->
                            Text(
                                text = playerUiModel.shots.toString(),
                                color = textColor,
                                style = textStyle,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onPlayerResultClicked(PlayerResultUiModel(playerUiModel, TeamOption.Shot)) }
                                    )
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(verticalSpace),
                    ) {
                        Text(
                            text = stringResource(id = R.string.player_result_passes),
                            color = headerTextColor,
                            style = headerTextStyle,
                        )
                        playerUiModelList.forEach { playerUiModel ->
                            Text(
                                text = playerUiModel.passes.toString(),
                                color = textColor,
                                style = textStyle,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onPlayerResultClicked(PlayerResultUiModel(playerUiModel, TeamOption.Pass)) }
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}