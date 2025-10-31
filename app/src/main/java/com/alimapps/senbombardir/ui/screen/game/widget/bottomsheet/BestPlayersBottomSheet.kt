package com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.BestPlayerUiModel
import com.alimapps.senbombardir.ui.model.types.BestPlayerOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestPlayersBottomSheet(
    bestPlayers: List<BestPlayerUiModel>,
    onDismissed: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissed() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp),
        ) {
            bestPlayers.forEach { best ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(best.option.stringRes),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = best.playerUiModel.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = when(best.option) {
                                BestPlayerOption.BestPlayer -> {
                                    listOfNotNull(
                                        best.playerUiModel.goals.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_goal)}" },
                                        best.playerUiModel.assists.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_assist)}" },
                                        best.playerUiModel.saves.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_save)}" },
                                        best.playerUiModel.dribbles.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_dribble)}" },
                                        best.playerUiModel.passes.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_pass)}" },
                                        best.playerUiModel.shots.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_shot)}" }
                                    ).joinToString(separator = ", ")
                                }
                                BestPlayerOption.Goals -> "${best.playerUiModel.goals} ${stringResource(R.string.text_goal)}"
                                BestPlayerOption.Assists -> "${best.playerUiModel.assists} ${stringResource(R.string.text_assist)}"
                                BestPlayerOption.Saves -> "${best.playerUiModel.saves} ${stringResource(R.string.text_save)}"
                                BestPlayerOption.Dribbles -> "${best.playerUiModel.dribbles} ${stringResource(R.string.text_dribble)}"
                                BestPlayerOption.Passes -> "${best.playerUiModel.passes} ${stringResource(R.string.text_pass)}"
                                BestPlayerOption.Shots -> "${best.playerUiModel.shots} ${stringResource(R.string.text_shot)}"
                            },
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
    }
}