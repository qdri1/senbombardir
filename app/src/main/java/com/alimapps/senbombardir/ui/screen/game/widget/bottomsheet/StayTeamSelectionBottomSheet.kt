package com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.LiveGameUiModel
import com.alimapps.senbombardir.ui.screen.game.GameAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayTeamSelectionBottomSheet(
    liveGameUiModel: LiveGameUiModel,
    onAction: (GameAction) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onAction(GameAction.OnStayTeamSelectionBottomSheetDismissed) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Text(
            text = stringResource(id = R.string.stay_team_selection_title),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp),
        ) {
            HorizontalDivider()
            Text(
                text = liveGameUiModel.leftTeamName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(GameAction.OnLeftTeamStayClicked) }
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 16.dp)
            )
            HorizontalDivider()
            Text(
                text = liveGameUiModel.rightTeamName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(GameAction.OnRightTeamStayClicked) }
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