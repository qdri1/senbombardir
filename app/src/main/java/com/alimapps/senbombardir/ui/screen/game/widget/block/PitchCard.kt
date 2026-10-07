package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Pitch-card palette (same values as iOS ScoreboardBackground / scoreboardSection / timerSection)
internal val PitchDark = Color(0xFF0B2F1B)
internal val PitchMid = Color(0xFF123D22)
internal val PitchDeep = Color(0xFF0A2818)
internal val LiveRed = Color(0xFFFF4B4B)
internal val Gold = Color(0xFFFFD54A)
internal val FlameOrange = Color(0xFFFFB020)
internal val FinishStart = Color(0xFFEC7063)
internal val FinishEnd = Color(0xFFC0392B)
internal val StartEnd = Color(0xFF155A22)

/**
 * Shared pitch-card shell (gradient, grass stripes, rounded gradient border, shadow)
 * so the scoreboard and timer blocks read as one visual family. Mirrors iOS `pitchCard`.
 */
@Composable
internal fun PitchCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 20.dp,
                shape = shape,
                ambientColor = PitchDeep,
                spotColor = PitchDeep,
            )
            .clip(shape)
            .pitchBackground()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.02f))
                ),
                shape = shape,
            )
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

private fun Modifier.pitchBackground(): Modifier = drawBehind {
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(PitchDark, PitchMid, PitchDeep),
            start = Offset.Zero,
            end = Offset(size.width, size.height),
        )
    )
    val stripeCount = 8
    val stripeWidth = size.width / stripeCount
    for (index in 0 until stripeCount step 2) {
        drawRect(
            color = Color.White.copy(alpha = 0.025f),
            topLeft = Offset(index * stripeWidth, 0f),
            size = Size(stripeWidth, size.height),
        )
    }
}
