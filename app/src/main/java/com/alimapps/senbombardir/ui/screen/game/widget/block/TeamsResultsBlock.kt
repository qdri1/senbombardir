package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.utils.parseHexColor

@Composable
fun TeamsResultsBlock(
    teamUiModelList: List<TeamUiModel>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpace),
            ) {
                Text(
                    text = stringResource(id = R.string.team_result_place),
                    color = headerTextColor,
                    style = headerTextStyle,
                )
                teamUiModelList.forEachIndexed { index, _ ->
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
                    text = stringResource(id = R.string.team_result_name),
                    color = headerTextColor,
                    style = headerTextStyle,
                )
                teamUiModelList.forEach { teamUiModel ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = parseHexColor(teamUiModel.color.hexColor),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Text(
                            text = teamUiModel.name,
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
                    text = stringResource(id = R.string.team_result_games),
                    color = headerTextColor,
                    style = headerTextStyle,
                )
                teamUiModelList.forEach { teamUiModel ->
                    Text(
                        text = teamUiModel.games.toString(),
                        color = textColor,
                        style = textStyle,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpace),
            ) {
                Text(
                    text = stringResource(id = R.string.team_result_wins),
                    color = headerTextColor,
                    style = headerTextStyle,
                )
                teamUiModelList.forEach { teamUiModel ->
                    Text(
                        text = teamUiModel.wins.toString(),
                        color = textColor,
                        style = textStyle,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpace),
            ) {
                Text(
                    text = stringResource(id = R.string.team_result_draws),
                    color = headerTextColor,
                    style = headerTextStyle,
                )
                teamUiModelList.forEach { teamUiModel ->
                    Text(
                        text = teamUiModel.draws.toString(),
                        color = textColor,
                        style = textStyle,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpace),
            ) {
                Text(
                    text = stringResource(id = R.string.team_result_loses),
                    color = headerTextColor,
                    style = headerTextStyle,
                )
                teamUiModelList.forEach { teamUiModel ->
                    Text(
                        text = teamUiModel.loses.toString(),
                        color = textColor,
                        style = textStyle,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpace),
            ) {
                Text(
                    text = stringResource(id = R.string.team_result_goals_conceded),
                    color = headerTextColor,
                    style = headerTextStyle,
                )
                teamUiModelList.forEach { teamUiModel ->
                    Text(
                        text = "${teamUiModel.goals}-${teamUiModel.conceded}",
                        color = textColor,
                        style = textStyle,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpace),
            ) {
                Text(
                    text = stringResource(id = R.string.team_result_goals_difference),
                    color = headerTextColor,
                    style = headerTextStyle,
                )
                teamUiModelList.forEach { teamUiModel ->
                    Text(
                        text = if (teamUiModel.goalsDifference > 0) {
                            "+${teamUiModel.goalsDifference}"
                        } else {
                            "${teamUiModel.goalsDifference}"
                        },
                        color = textColor,
                        style = textStyle,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpace),
            ) {
                Text(
                    text = stringResource(id = R.string.team_result_points),
                    color = headerTextColor,
                    style = headerTextStyle,
                )
                teamUiModelList.forEach { teamUiModel ->
                    Text(
                        text = teamUiModel.points.toString(),
                        color = textColor,
                        style = textStyle,
                    )
                }
            }
        }
    }
}