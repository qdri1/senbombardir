package com.alimapps.senbombardir.ui.composable

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Size of the colour box / [PlayerTeamBadge] in the name column of the leaderboards. */
internal val TableBadgeSize = 16.dp

/**
 * The leaderboards are built column by column (#, name, each stat), so rows only line up if every
 * cell has the same height. Name cells are at least as tall as the fixed 16dp colour badge, while
 * text cells follow the system font size — with a small system font they got shorter than the badge
 * and the columns drifted apart. This gives every text cell the same minimum height (text centred),
 * so the row height is max(badge, text) in every column.
 */
internal fun Modifier.tableCell(): Modifier =
    heightIn(min = TableBadgeSize).wrapContentHeight(Alignment.CenterVertically)
