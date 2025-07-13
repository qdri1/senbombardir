package com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.LiveGameResultUiModel
import com.alimapps.senbombardir.ui.screen.game.GameAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveGameResultBottomSheet(
    liveGameResultUiModel: LiveGameResultUiModel,
    onAction: (GameAction) -> Unit,
    onDismissed: () -> Unit,
) {
    val selectedTeamGoals = if (liveGameResultUiModel.isLeftTeam) {
        liveGameResultUiModel.liveGameUiModel.leftTeamGoals
    } else {
        liveGameResultUiModel.liveGameUiModel.rightTeamGoals
    }

    val selectedTeamName = if (liveGameResultUiModel.isLeftTeam) {
        liveGameResultUiModel.liveGameUiModel.leftTeamName
    } else {
        liveGameResultUiModel.liveGameUiModel.rightTeamName
    }

    var teamGoalsValue by remember { mutableStateOf(selectedTeamGoals) }

    ModalBottomSheet(
        onDismissRequest = { onDismissed() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Text(
            text = selectedTeamName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "GameScreenArrowBackIcon",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { teamGoalsValue++ }
            )
            Text(
                text = teamGoalsValue.toString(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "GameScreenArrowBackIcon",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { teamGoalsValue-- }
            )
        }
        Button(
            onClick = {
                if (selectedTeamGoals != teamGoalsValue) {
                    onAction(GameAction.OnSaveLiveGameResultClicked(liveGameResultUiModel, teamGoalsValue))
                } else {
                    onDismissed()
                }
            },
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 16.dp
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.save))
        }
        Text(
            text = stringResource(id = R.string.result_correction_text),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
    }
}