package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.GameUiModel
import com.alimapps.senbombardir.ui.model.LiveGameResultUiModel
import com.alimapps.senbombardir.ui.model.LiveGameUiModel
import com.alimapps.senbombardir.ui.model.NextPlayingTeamsUiModel
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam3
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.ui.model.types.TeamOption
import com.alimapps.senbombardir.ui.model.types.TeamQuantity
import com.alimapps.senbombardir.ui.screen.game.GameAction
import com.alimapps.senbombardir.ui.screen.game.GameUiState
import com.alimapps.senbombardir.ui.screen.game.widget.dropdown.LeftTeamChangeDropdown
import com.alimapps.senbombardir.ui.screen.game.widget.dropdown.LeftTeamOptionsDropdown
import com.alimapps.senbombardir.ui.screen.game.widget.dropdown.RightTeamChangeDropdown
import com.alimapps.senbombardir.ui.screen.game.widget.dropdown.RightTeamOptionsDropdown
import com.alimapps.senbombardir.ui.theme.Montserrat
import com.alimapps.senbombardir.ui.utils.parseHexColor

@Composable
fun LiveGameBlock(
    gameUiModel: GameUiModel?,
    liveGameUiModel: LiveGameUiModel,
    nextPlayingTeamsUiModelList: List<NextPlayingTeamsUiModel>,
    restTeamUiModelList: List<TeamUiModel>,
    uiState: GameUiState,
    hiddenOptions: Set<TeamOption>,
    onAction: (GameAction) -> Unit,
) {
    PitchCard(modifier = Modifier.padding(top = 16.dp)) {
        MatchStatusBadge(
            isLive = liveGameUiModel.isLive,
            gameCount = liveGameUiModel.gameCount,
            teamQuantity = gameUiModel?.teamQuantity,
        )

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pitchCenterMark(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamScore(
                    name = liveGameUiModel.leftTeamName,
                    color = liveGameUiModel.leftTeamColor,
                    goals = liveGameUiModel.leftTeamGoals,
                    winCount = liveGameUiModel.leftTeamWinCount,
                    isWinning = liveGameUiModel.isLeftTeamWin,
                    onClick = { onAction(GameAction.OnLeftTeamClicked) },
                    onLongClick = {
                        onAction(
                            GameAction.OnLiveGameResultClicked(
                                LiveGameResultUiModel(
                                    liveGameUiModel = liveGameUiModel,
                                    isLeftTeam = true,
                                )
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                )

                CenterDivider(
                    isLive = liveGameUiModel.isLive,
                    onTeamChangeClicked = { onAction(GameAction.OnTeamChangeIconClicked) },
                    modifier = Modifier.padding(horizontal = 2.dp),
                )

                TeamScore(
                    name = liveGameUiModel.rightTeamName,
                    color = liveGameUiModel.rightTeamColor,
                    goals = liveGameUiModel.rightTeamGoals,
                    winCount = liveGameUiModel.rightTeamWinCount,
                    isWinning = liveGameUiModel.isRightTeamWin,
                    onClick = { onAction(GameAction.OnRightTeamClicked) },
                    onLongClick = {
                        onAction(
                            GameAction.OnLiveGameResultClicked(
                                LiveGameResultUiModel(
                                    liveGameUiModel = liveGameUiModel,
                                    isLeftTeam = false,
                                )
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            val otherTeams = uiState.teamUiModelList.filter {
                it.id !in listOf(liveGameUiModel.leftTeamId, liveGameUiModel.rightTeamId)
            }
            when {
                uiState.showLeftTeamOptionsDropdown -> LeftTeamOptionsDropdown(hiddenOptions, onAction)
                uiState.showRightTeamOptionsDropdown -> RightTeamOptionsDropdown(hiddenOptions, onAction)
                uiState.showLeftTeamChangeDropdown -> LeftTeamChangeDropdown(
                    teamUiModelList = otherTeams,
                    onAction = onAction,
                )
                uiState.showRightTeamChangeDropdown -> RightTeamChangeDropdown(
                    teamUiModelList = otherTeams,
                    onAction = onAction,
                )
            }
        }

        if (shouldShowStreak(gameUiModel)) {
            StreakRow(
                leftWinCount = liveGameUiModel.leftTeamWinCount,
                rightWinCount = liveGameUiModel.rightTeamWinCount,
            )
        }

        if (nextPlayingTeamsUiModelList.isNotEmpty()) {
            NextPlayingFooter(nextPlayingTeamsUiModelList)
        } else if (restTeamUiModelList.isNotEmpty()) {
            RestFooter(restTeamUiModelList)
        }
    }
}

// region Pitch mark

/** Halfway line + center circle behind the score row (iOS `PitchCenterMark`). */
private fun Modifier.pitchCenterMark(): Modifier = drawBehind {
    val markColor = Color.White.copy(alpha = 0.06f)
    val strokeWidth = 1.5.dp.toPx()
    drawCircle(
        color = markColor,
        radius = 65.dp.toPx(),
        center = center,
        style = Stroke(width = strokeWidth),
    )
    drawLine(
        color = markColor,
        start = Offset(center.x, 0f),
        end = Offset(center.x, size.height),
        strokeWidth = strokeWidth,
    )
}

// endregion

// region Header

@Composable
private fun MatchStatusBadge(
    isLive: Boolean,
    gameCount: Int,
    teamQuantity: TeamQuantity?,
) {
    if (isLive) {
        val transition = rememberInfiniteTransition(label = "livePulse")
        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "livePulsePhase",
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(LiveRed.copy(alpha = 0.22f))
                .border(1.dp, LiveRed.copy(alpha = 0.5f), CircleShape)
                .padding(horizontal = 12.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .graphicsLayer {
                        val scale = 1f + 0.6f * phase
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f - 0.6f * phase
                    }
                    .background(LiveRed, CircleShape)
            )
            Text(
                text = "LIVE",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.2.sp,
            )
        }
    } else {
        val statusText = if (teamQuantity == TeamQuantity.Team2) {
            stringResource(id = R.string.half_count, gameCount.toString())
        } else {
            stringResource(id = R.string.game_count, gameCount.toString())
        }
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .padding(horizontal = 12.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.SportsSoccer,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = statusText,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

// endregion

// region Score row

@Composable
private fun TeamScore(
    name: String,
    color: TeamColor,
    goals: Int,
    winCount: Int,
    isWinning: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val glowRadius = with(LocalDensity.current) { 12.dp.toPx() }
    Column(
        modifier = modifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        JerseyBadge(color = color, showFlame = winCount > 2)

        Text(
            text = goals.toString(),
            color = Color.White,
            style = TextStyle(
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold,
                fontSize = 52.sp,
                lineHeight = 56.sp,
                fontFeatureSettings = "tnum",
                shadow = if (isWinning) {
                    Shadow(color = Gold.copy(alpha = 0.6f), offset = Offset.Zero, blurRadius = glowRadius)
                } else {
                    null
                },
            ),
        )

        Text(
            text = name,
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = 0.6.sp,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 10.sp,
                maxFontSize = 14.sp,
                stepSize = 1.sp,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun JerseyBadge(color: TeamColor, showFlame: Boolean) {
    Box {
        // Soft drop shadow (iOS: shadow(color: black 0.25, radius: 3, y: 2))
        Icon(
            painter = painterResource(id = R.drawable.ic_jersey),
            contentDescription = null,
            tint = Color.Black.copy(alpha = 0.25f),
            modifier = Modifier
                .size(38.dp)
                .offset(y = 2.dp),
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_jersey),
            contentDescription = null,
            tint = parseHexColor(color.hexColor),
            modifier = Modifier.size(38.dp),
        )
        if (showFlame) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-6).dp)
                    .background(PitchMid, CircleShape)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = FlameOrange,
                    modifier = Modifier.size(10.dp),
                )
            }
        }
    }
}

@Composable
private fun CenterDivider(
    isLive: Boolean,
    onTeamChangeClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            .then(if (isLive) Modifier else Modifier.clickable(onClick = onTeamChangeClicked)),
        contentAlignment = Alignment.Center,
    ) {
        if (isLive) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.soccer_ball)
            )
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(24.dp),
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_replace_sides),
                contentDescription = "GameScreenChangeIcon",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// endregion

// region Streak

private fun shouldShowStreak(gameUiModel: GameUiModel?): Boolean =
    when (gameUiModel?.teamQuantity) {
        TeamQuantity.Team4 -> true
        TeamQuantity.Team3 -> when (gameUiModel?.gameRule) {
            GameRuleTeam3.WINNER_STAY_3,
            GameRuleTeam3.WINNER_STAY_4,
            GameRuleTeam3.WINNER_STAY_UNLIMITED -> true
            else -> false
        }
        else -> false
    }

@Composable
private fun StreakRow(leftWinCount: Int, rightWinCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            StreakPill(count = leftWinCount)
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            StreakPill(count = rightWinCount)
        }
    }
}

@Composable
private fun StreakPill(count: Int) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = FlameOrange,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = stringResource(id = R.string.win_streak, count.toString()),
            color = Color.White.copy(alpha = 0.75f),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

// endregion

// region Footers

@Composable
private fun ScoreboardDivider(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.12f))
        )
        Text(
            text = title.uppercase(),
            color = Gold.copy(alpha = 0.85f),
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 0.8.sp,
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.12f))
        )
    }
}

@Composable
private fun ScoreboardTeamChip(color: TeamColor?) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(shape)
            .background(color?.let { parseHexColor(it.hexColor) } ?: Color.Transparent)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (color == TeamColor.White) 0.6f else 0.15f),
                shape = shape,
            )
    )
}

@Composable
private fun FooterTeamName(text: String, textAlign: TextAlign, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.85f),
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign,
        modifier = modifier,
    )
}

@Composable
private fun NextPlayingFooter(nextPlayingTeamsUiModelList: List<NextPlayingTeamsUiModel>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ScoreboardDivider(title = stringResource(R.string.next_playing_teams))

        nextPlayingTeamsUiModelList.forEach { nextTeams ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                val leftTeam = nextTeams.leftTeam
                if (leftTeam != null) {
                    FooterTeamName(leftTeam.name, TextAlign.End, Modifier.weight(1f))
                    ScoreboardTeamChip(leftTeam.color)
                } else {
                    ScoreboardTeamChip(null)
                    FooterTeamName("?", TextAlign.End, Modifier.weight(1f))
                }

                Text(
                    text = "–",
                    color = Color.White.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.labelSmall,
                )

                val rightTeam = nextTeams.rightTeam
                if (rightTeam != null) {
                    ScoreboardTeamChip(rightTeam.color)
                    FooterTeamName(rightTeam.name, TextAlign.Start, Modifier.weight(1f))
                } else {
                    FooterTeamName("?", TextAlign.Start, Modifier.weight(1f))
                    ScoreboardTeamChip(null)
                }
            }
        }
    }
}

@Composable
private fun RestFooter(restTeamUiModelList: List<TeamUiModel>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScoreboardDivider(title = stringResource(R.string.next_playing_teams))

        restTeamUiModelList.forEach { teamUiModel ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ScoreboardTeamChip(teamUiModel.color)
                FooterTeamName(teamUiModel.name, TextAlign.Center)
            }
        }
    }
}

// endregion
