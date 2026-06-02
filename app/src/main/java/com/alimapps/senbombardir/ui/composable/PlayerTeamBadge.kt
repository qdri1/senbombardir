package com.alimapps.senbombardir.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.ui.utils.parseHexColor

@Composable
fun PlayerTeamBadge(
    teamColor: TeamColor,
    number: Int?,
    size: Dp = 16.dp,
) {
    val cornerRadius = size * 0.25f
    val color = parseHexColor(teamColor.hexColor)
    val shape = RoundedCornerShape(cornerRadius)
    val borderColor = MaterialTheme.colorScheme.surfaceVariant

    if (number != null) {
        val fontSize = (size.value * 0.5f).sp
        val horizontalPadding = size * 0.25f
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(size)
                .widthIn(min = size)
                .background(color = color, shape = shape)
                .clip(shape)
                .border(
                    width = if (teamColor == TeamColor.White) 1.dp else 0.dp,
                    color = borderColor,
                    shape = shape,
                )
                .padding(horizontal = horizontalPadding),
        ) {
            Text(
                text = number.toString(),
                color = if (teamColor == TeamColor.White) Color.Black else Color.White,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    lineHeight = fontSize,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both,
                    ),
                ),
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .background(color = color, shape = shape)
                .clip(shape)
                .border(
                    width = if (teamColor == TeamColor.White) 1.dp else 0.dp,
                    color = borderColor,
                    shape = shape,
                )
        )
    }
}
