package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.LiveGameUiModel
import com.alimapps.senbombardir.ui.screen.game.GameAction
import com.alimapps.senbombardir.ui.screen.game.GameUiState

@Composable
fun TimerBlock(
    liveGameUiModel: LiveGameUiModel,
    timerValueState: State<String>,
    uiState: GameUiState,
    onAction: (GameAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 20.dp)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        val alpha = if (liveGameUiModel.isLive) 1f else 0f
        val painterId = if (uiState.isTimerPlay) R.drawable.ic_pause else R.drawable.ic_play

        Icon(
            painter = painterResource(id = painterId),
            contentDescription = "GameScreenChangeIcon",
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onAction(GameAction.OnTimerClicked) }
                .alpha(alpha)
        )
        Text(
            text = timerValueState.value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displayLarge,
            fontSize = 36.sp,
            modifier = Modifier
                .padding(start = 4.dp)
                .padding(end = 28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onAction(GameAction.OnTimerClicked) },
        )
    }
}