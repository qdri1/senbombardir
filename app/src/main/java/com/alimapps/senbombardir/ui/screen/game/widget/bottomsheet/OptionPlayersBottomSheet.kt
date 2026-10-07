package com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.composable.PlayerTeamBadge
import com.alimapps.senbombardir.ui.model.OptionPlayersUiModel
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.ui.model.types.TeamOption
import com.alimapps.senbombardir.ui.screen.game.GameAction
import com.alimapps.senbombardir.ui.utils.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionPlayersBottomSheet(
    optionPlayersUiModel: OptionPlayersUiModel,
    onAction: (GameAction) -> Unit,
    onDismissed: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissed() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Text(
            text = "${stringResource(id = optionPlayersUiModel.option.stringRes)} - ${stringResource(id = R.string.team_option_players_title)}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp),
        ) {
            HorizontalDivider()
            optionPlayersUiModel.playerUiModelList.forEach { playerUiModel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAction(
                                GameAction.OnOptionPlayersSelected(
                                    teamId = optionPlayersUiModel.teamId,
                                    playerUiModel = playerUiModel,
                                    option = optionPlayersUiModel.option,
                                )
                            )
                        }
                        .padding(horizontal = 8.dp)
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PlayerTeamBadge(
                        teamColor = playerUiModel.teamColor,
                        number = playerUiModel.number,
                    )
                    Text(
                        text = remember(playerUiModel.name) { playerUiModel.name.withBoldFirstLetter() },
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                HorizontalDivider()
            }
            if (optionPlayersUiModel.option == TeamOption.Goal) {
                val text = stringResource(id = R.string.team_option_players_auto_goal)
                Text(
                    text = text,
                    color = parseHexColor(TeamColor.Orange.hexColor),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAction(
                                GameAction.OnOptionPlayersAutoGoalSelected(
                                    teamId = optionPlayersUiModel.teamId,
                                    text = text,
                                )
                            )
                        }
                        .padding(horizontal = 8.dp)
                        .padding(vertical = 16.dp)
                )
                HorizontalDivider()
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
        }
    }
}

/** Makes the first visible letter of the name bold (handles leading spaces and surrogate pairs). */
private fun String.withBoldFirstLetter(): AnnotatedString {
    val start = indexOfFirst { !it.isWhitespace() }
    if (start == -1) return AnnotatedString(this)
    val end = start + Character.charCount(codePointAt(start))
    return buildAnnotatedString {
        append(this@withBoldFirstLetter.substring(0, start))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(this@withBoldFirstLetter.substring(start, end))
        }
        append(this@withBoldFirstLetter.substring(end))
    }
}
