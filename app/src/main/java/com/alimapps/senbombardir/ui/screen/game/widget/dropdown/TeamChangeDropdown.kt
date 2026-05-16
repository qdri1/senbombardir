package com.alimapps.senbombardir.ui.screen.game.widget.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.ui.screen.game.GameAction
import com.alimapps.senbombardir.ui.utils.parseHexColor

@Composable
fun LeftTeamChangeDropdown(
    teamUiModelList: List<TeamUiModel>,
    onAction: (GameAction) -> Unit,
) {
    Popup(
        alignment = Alignment.TopStart,
        onDismissRequest = { onAction(GameAction.OnLeftTeamChangeClicked(teamId = null)) }
    ) {
        Box(
            modifier = Modifier
                .padding(top = 36.dp)
                .padding(start = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                teamUiModelList.forEach { teamUiModel ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onAction(GameAction.OnLeftTeamChangeClicked(teamUiModel.id)) }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = teamUiModel.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = parseHexColor(teamUiModel.color.hexColor),
                                    shape = RoundedCornerShape(6.dp),
                                )
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    width = if (teamUiModel.color == TeamColor.White) 1.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp),
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RightTeamChangeDropdown(
    teamUiModelList: List<TeamUiModel>,
    onAction: (GameAction) -> Unit,
) {
    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = { onAction(GameAction.OnRightTeamChangeClicked(teamId = null)) }
    ) {
        Box(
            modifier = Modifier
                .padding(top = 36.dp)
                .padding(end = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                teamUiModelList.forEach { teamUiModel ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onAction(GameAction.OnRightTeamChangeClicked(teamUiModel.id)) }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = parseHexColor(teamUiModel.color.hexColor),
                                    shape = RoundedCornerShape(6.dp),
                                )
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    width = if (teamUiModel.color == TeamColor.White) 1.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp),
                                )
                        )
                        Text(
                            text = teamUiModel.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}