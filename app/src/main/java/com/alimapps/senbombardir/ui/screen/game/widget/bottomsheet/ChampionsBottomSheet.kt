package com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.composable.PlayerTeamBadge
import com.alimapps.senbombardir.ui.model.ChampionPlayerUiModel
import com.alimapps.senbombardir.ui.model.ChampionsUiModel
import com.alimapps.senbombardir.ui.model.TeamUiModel
import com.alimapps.senbombardir.ui.theme.Montserrat
import com.alimapps.senbombardir.ui.utils.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionsBottomSheet(
    champions: ChampionsUiModel,
    onDismissed: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissed() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Text(
            text = stringResource(R.string.champions),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp),
        ) {
            ChampionTeamHeroCard(team = champions.teamUiModel)

            champions.players.forEach { champion ->
                ChampionPlayerRow(champion = champion)
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
private fun ChampionTeamHeroCard(team: TeamUiModel) {
    val goldColor = parseHexColor("#FFD166")
    val cardShape = RoundedCornerShape(20.dp)
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            parseHexColor("#1B1500"),
            parseHexColor("#7A5800"),
            parseHexColor("#E0A800"),
        ),
    )
    val borderGradient = Brush.verticalGradient(
        colors = listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.04f)),
    )
    val shadowColor = parseHexColor("#E0A800").copy(alpha = 0.4f)
    val density = LocalDensity.current
    val stats = listOf(
        stringResource(R.string.champions_points, team.points),
        stringResource(R.string.champions_goals, team.goals, team.conceded),
        stringResource(R.string.champions_record, team.wins, team.draws, team.loses),
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .bestPlayerShadow(shadowColor = shadowColor, cornerRadius = 20.dp, blur = 14.dp, offsetY = 6.dp)
            .clip(cardShape)
            .background(backgroundGradient),
    ) {
        val radialGradient = Brush.radialGradient(
            colors = listOf(parseHexColor("#FFE38A").copy(alpha = 0.55f), Color.Transparent),
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
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
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
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = stringResource(R.string.champions_first_place).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = goldColor,
                    letterSpacing = 0.4.sp,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                PlayerTeamBadge(
                    teamColor = team.color,
                    number = null,
                    size = 24.dp,
                )
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                stats.forEach { statText ->
                    Text(
                        text = statText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color = Color.White.copy(alpha = 0.16f), shape = CircleShape)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }
        }

        MvpBadge(
            text = "TOP 1",
            gradientColors = listOf(parseHexColor("#A86F00"), parseHexColor("#F2B705")),
            shadowColor = parseHexColor("#E0A800").copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 20.dp)
                .rotate(-4f),
        )
    }
}

@Composable
private fun ChampionPlayerRow(champion: ChampionPlayerUiModel) {
    val accent = parseHexColor(champion.title.accentHex)
    val player = champion.playerUiModel
    val stats = listOfNotNull(
        stat(player.goals, "⚽️"),
        stat(player.assists, "👟"),
        stat(player.saves, "🧤"),
        stat(player.tackles, "🛡️"),
        stat(player.dribbles, "🌀"),
        stat(player.shots, "💥"),
        stat(player.passes, "🎯"),
        stat(player.yellowCards, "🟨"),
        stat(player.redCards, "🟥"),
    )
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(color = accent.copy(alpha = 0.18f), shape = CircleShape),
            ) {
                Icon(
                    imageVector = champion.title.icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp),
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(champion.title.stringRes).uppercase(),
                    color = accent,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.5.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                AutoSizeText(
                    text = stringResource(champion.title.descriptionRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    minFontSize = 10.sp,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    PlayerTeamBadge(
                        teamColor = player.teamColor,
                        number = player.number,
                        size = 18.dp,
                    )
                    AutoSizeText(
                        text = player.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        minFontSize = 10.sp,
                        modifier = Modifier.weight(1f),
                    )
                    AutoSizeText(
                        text = stats.joinToString(separator = "  "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        minFontSize = 8.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.widthIn(max = 160.dp),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(vertical = 10.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent),
            )
        }
    }
}

@Composable
private fun AutoSizeText(
    text: String,
    style: TextStyle,
    color: Color,
    minFontSize: TextUnit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = color, textAlign = textAlign),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        autoSize = TextAutoSize.StepBased(
            minFontSize = minFontSize,
            maxFontSize = style.fontSize,
            stepSize = 0.5.sp,
        ),
    )
}

private fun stat(value: Int, icon: String): String? =
    if (value > 0) "$icon$value" else null
