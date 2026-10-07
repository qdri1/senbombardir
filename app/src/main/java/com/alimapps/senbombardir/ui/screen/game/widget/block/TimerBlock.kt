package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.LiveGameUiModel
import com.alimapps.senbombardir.ui.screen.game.GameAction
import com.alimapps.senbombardir.ui.screen.game.GameUiState
import com.alimapps.senbombardir.ui.theme.Montserrat

/** Timer + start/finish button in a pitch card. Mirrors iOS `timerSection`. */
@Composable
fun TimerBlock(
    liveGameUiModel: LiveGameUiModel,
    timerValueState: State<String>,
    uiState: GameUiState,
    onAction: (GameAction) -> Unit,
) {
    PitchCard(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onAction(GameAction.OnTimerClicked) },
            horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.10f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(
                        id = if (uiState.isTimerPlay) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = "GameScreenTimerIcon",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }

            Text(
                text = timerValueState.value,
                color = Color.White,
                style = TextStyle(
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    fontFeatureSettings = "tnum",
                ),
                // Balances the play button on the left so the time stays visually centered.
                modifier = Modifier.padding(end = 58.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.10f))
        )

        StartFinishButton(
            isLive = liveGameUiModel.isLive,
            onClick = { onAction(GameAction.OnStartFinishButtonClicked) },
        )
    }
}

@Composable
private fun StartFinishButton(isLive: Boolean, onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val accentColor = if (isLive) FinishStart else primary
    val gradient = if (isLive) listOf(FinishStart, FinishEnd) else listOf(primary, StartEnd)
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = accentColor.copy(alpha = 0.4f),
                spotColor = accentColor.copy(alpha = 0.4f),
            )
            .clip(shape)
            .background(Brush.horizontalGradient(gradient))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isLive) "FINISH 🏁" else "GO ⚽️",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            letterSpacing = 0.4.sp,
        )
    }
}
