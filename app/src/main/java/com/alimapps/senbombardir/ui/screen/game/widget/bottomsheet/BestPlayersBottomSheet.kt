package com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.composable.PlayerTeamBadge
import com.alimapps.senbombardir.ui.model.BestPlayerUiModel
import com.alimapps.senbombardir.ui.model.types.BestPlayerOption
import com.alimapps.senbombardir.ui.theme.Montserrat
import com.alimapps.senbombardir.ui.utils.parseHexColor

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
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp),
        ) {
            bestPlayers.forEach { best ->
                if (best.option == BestPlayerOption.BestPlayer) {
                    BestPlayerHeroCard(bestPlayer = best)
                } else {
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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PlayerTeamBadge(
                                    teamColor = best.playerUiModel.teamColor,
                                    number = best.playerUiModel.number,
                                )
                                Text(
                                    text = best.playerUiModel.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                            Text(
                                text = when (best.option) {
                                    BestPlayerOption.BestPlayer -> ""
                                    BestPlayerOption.Goals -> "${best.playerUiModel.goals} ${stringResource(R.string.text_goal)}"
                                    BestPlayerOption.Assists -> "${best.playerUiModel.assists} ${stringResource(R.string.text_assist)}"
                                    BestPlayerOption.Saves -> "${best.playerUiModel.saves} ${stringResource(R.string.text_save)}"
                                    BestPlayerOption.Tackles -> "${best.playerUiModel.tackles} ${stringResource(R.string.text_tackle)}"
                                    BestPlayerOption.Dribbles -> "${best.playerUiModel.dribbles} ${stringResource(R.string.text_dribble)}"
                                    BestPlayerOption.Passes -> "${best.playerUiModel.passes} ${stringResource(R.string.text_pass)}"
                                    BestPlayerOption.Shots -> "${best.playerUiModel.shots} ${stringResource(R.string.text_shot)}"
                                    BestPlayerOption.AggressivePlayer -> {
                                        listOfNotNull(
                                            best.playerUiModel.yellowCards.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_yellow_card)}" },
                                            best.playerUiModel.redCards.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_red_card)}" },
                                        ).joinToString(separator = ", ")
                                    }
                                },
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
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
}

@Composable
private fun BestPlayerHeroCard(bestPlayer: BestPlayerUiModel) {
    val goldColor = parseHexColor("#FFD166")
    val cardShape = RoundedCornerShape(20.dp)
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            parseHexColor("#3D0B02"),
            parseHexColor("#B3200B"),
            parseHexColor("#FF7A1A"),
        ),
    )
    val borderGradient = Brush.verticalGradient(
        colors = listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.04f)),
    )
    val shadowColor = parseHexColor("#FF4E00").copy(alpha = 0.4f)
    val density = LocalDensity.current

    val stats = listOfNotNull(
        bestPlayer.playerUiModel.goals.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_goal)}" },
        bestPlayer.playerUiModel.assists.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_assist)}" },
        bestPlayer.playerUiModel.saves.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_save)}" },
        bestPlayer.playerUiModel.tackles.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_tackle)}" },
        bestPlayer.playerUiModel.dribbles.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_dribble)}" },
        bestPlayer.playerUiModel.passes.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_pass)}" },
        bestPlayer.playerUiModel.shots.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_shot)}" },
        bestPlayer.playerUiModel.yellowCards.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_yellow_card)}" },
        bestPlayer.playerUiModel.redCards.takeIf { it > 0 }?.let { "$it ${stringResource(R.string.text_red_card)}" },
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = cardShape, ambientColor = shadowColor, spotColor = shadowColor)
            .clip(cardShape)
            .background(backgroundGradient),
    ) {
        val radialGradient = Brush.radialGradient(
            colors = listOf(parseHexColor("#FFD166").copy(alpha = 0.55f), Color.Transparent),
            center = with(density) { Offset(x = maxWidth.toPx(), y = 0f) },
            radius = with(density) { 170.dp.toPx() },
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(radialGradient),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(width = 1.dp, brush = borderGradient, shape = cardShape),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(color = Color.White.copy(alpha = 0.18f), shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = goldColor,
                        modifier = Modifier.size(14.dp),
                    )
                }
                Text(
                    text = stringResource(bestPlayer.option.stringRes).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = goldColor,
                    letterSpacing = 0.4.sp,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PlayerTeamBadge(
                    teamColor = bestPlayer.playerUiModel.teamColor,
                    number = bestPlayer.playerUiModel.number,
                    size = 24.dp,
                )
                Text(
                    text = bestPlayer.playerUiModel.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (stats.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    stats.forEach { statText ->
                        Text(
                            text = statText,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(color = Color.White.copy(alpha = 0.16f), shape = CircleShape)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }

        MvpBadge(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 20.dp)
                .rotate(-4f),
        )
    }
}

@Composable
private fun MvpBadge(modifier: Modifier = Modifier) {
    val shape = CircleShape
    val gradient = Brush.horizontalGradient(
        colors = listOf(parseHexColor("#FF3D00"), parseHexColor("#FF9F1C")),
    )
    val shadowColor = parseHexColor("#FF3D00").copy(alpha = 0.5f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
            .shadow(elevation = 6.dp, shape = shape, ambientColor = shadowColor, spotColor = shadowColor)
            .clip(shape)
            .background(gradient)
            .border(width = 0.75.dp, color = Color.White.copy(alpha = 0.5f), shape = shape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Whatshot,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "MVP",
            color = Color.White,
            fontFamily = Montserrat,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = 0.5.sp,
        )
    }
}
