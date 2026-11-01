package com.alimapps.senbombardir.ui.screen.game.widget.block

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alimapps.senbombardir.ui.model.types.PitchSurface
import kotlin.math.ceil
import kotlin.math.sin

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

internal data class PitchColors(
    val dark: Color,
    val mid: Color,
    val deep: Color,
    val centerMarkAlpha: Float,
    val dividerAlpha: Float,
    val ballSize: Dp,
)

internal val GrassPitchColors = PitchColors(
    dark = PitchDark,
    mid = PitchMid,
    deep = PitchDeep,
    centerMarkAlpha = 0.06f,
    dividerAlpha = 0.10f,
    ballSize = 24.dp,
)

internal val ParquetPitchColors = PitchColors(
    dark = Color(0xFF6E4310),
    mid = Color(0xFFA2661C),
    deep = Color(0xFF54320A),
    centerMarkAlpha = 0.22f,
    dividerAlpha = 0.28f,
    ballSize = 20.dp,
)

internal val PitchSurface.colors: PitchColors
    get() = when (this) {
        PitchSurface.Grass -> GrassPitchColors
        PitchSurface.Parquet -> ParquetPitchColors
    }

internal val LocalPitchColors = staticCompositionLocalOf { GrassPitchColors }

/**
 * Shared pitch-card shell (gradient, grass stripes, rounded gradient border, shadow)
 * so the scoreboard and timer blocks read as one visual family. Mirrors iOS `pitchCard`.
 */
@Composable
internal fun PitchCard(
    modifier: Modifier = Modifier,
    surface: PitchSurface = PitchSurface.Grass,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    val pitchColors = surface.colors
    val dark by animateColorAsState(targetValue = pitchColors.dark, label = "pitchDark")
    val mid by animateColorAsState(targetValue = pitchColors.mid, label = "pitchMid")
    val deep by animateColorAsState(targetValue = pitchColors.deep, label = "pitchDeep")
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 20.dp,
                shape = shape,
                ambientColor = deep,
                spotColor = deep,
            )
            .clip(shape)
            .pitchBackground(
                dark = dark,
                mid = mid,
                deep = deep,
                surface = surface,
            )
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
        CompositionLocalProvider(LocalPitchColors provides pitchColors.copy(dark = dark, mid = mid, deep = deep)) {
            content()
        }
    }
}

private fun Modifier.pitchBackground(
    dark: Color,
    mid: Color,
    deep: Color,
    surface: PitchSurface,
): Modifier = drawWithCache {
    val parquet = if (surface == PitchSurface.Parquet) buildParquet() else null
    onDrawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(dark, mid, deep),
                start = Offset.Zero,
                end = Offset(size.width, size.height),
            )
        )
        if (parquet == null) drawGrassStripes() else drawParquet(parquet)
    }
}

private fun DrawScope.drawGrassStripes() {
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

private class ParquetGeometry(
    val stripHeight: Float,
    val stripTones: List<Float>,
    val seams: Path,
    val joints: Path,
    val grain: Path,
)

private fun CacheDrawScope.buildParquet(): ParquetGeometry {
    val stripHeight = 7.dp.toPx()
    val rows = ceil(size.height / stripHeight).toInt()
    val grainStep = 40.dp.toPx()
    val grainWave = 90.dp.toPx()
    val grainAmplitude = 0.8.dp.toPx()
    val stripTones = ArrayList<Float>(rows)
    val seams = Path()
    val joints = Path()
    val grain = Path()
    for (row in 0 until rows) {
        val top = row * stripHeight
        stripTones += (pseudoRandom(row * 3 + 1) - 0.5f) * 0.07f
        seams.moveTo(0f, top)
        seams.lineTo(size.width, top)
        var jointX = pseudoRandom(row * 7 + 3) * 300.dp.toPx()
        while (jointX < size.width) {
            joints.moveTo(jointX, top)
            joints.lineTo(jointX, top + stripHeight)
            jointX += (260f + pseudoRandom(row * 11 + jointX.toInt()) * 240f).dp.toPx()
        }
        repeat(2) { line ->
            val grainY = top + stripHeight * (0.3f + 0.4f * pseudoRandom(row * 5 + line))
            grain.moveTo(0f, grainY)
            var x = grainStep
            while (x <= size.width + grainStep) {
                grain.lineTo(x, grainY + sin(x / grainWave + row) * grainAmplitude)
                x += grainStep
            }
        }
    }
    return ParquetGeometry(stripHeight, stripTones, seams, joints, grain)
}

private fun DrawScope.drawParquet(parquet: ParquetGeometry) {
    parquet.stripTones.forEachIndexed { row, tone ->
        drawRect(
            color = if (tone > 0) WoodLight.copy(alpha = tone) else WoodShadow.copy(alpha = -tone),
            topLeft = Offset(0f, row * parquet.stripHeight),
            size = Size(size.width, parquet.stripHeight),
        )
    }
    drawPath(parquet.seams, Color.Black.copy(alpha = 0.06f), style = Stroke(width = 0.75.dp.toPx()))
    drawPath(parquet.joints, Color.Black.copy(alpha = 0.045f), style = Stroke(width = 0.75.dp.toPx()))
    drawPath(parquet.grain, WoodGrain.copy(alpha = 0.035f), style = Stroke(width = 0.6.dp.toPx()))
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.08f), Color.Transparent),
            start = Offset(size.width * 0.2f, 0f),
            end = Offset(size.width * 0.65f, size.height),
        )
    )
    drawLampReflection(centerX = 0.25f, centerY = 0.18f, radius = 0.55f, alpha = 0.16f)
    drawLampReflection(centerX = 0.78f, centerY = 0.12f, radius = 0.45f, alpha = 0.12f)
}

private fun DrawScope.drawLampReflection(centerX: Float, centerY: Float, radius: Float, alpha: Float) {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(LampLight.copy(alpha = alpha), Color.Transparent),
            center = Offset(size.width * centerX, size.height * centerY),
            radius = size.height * radius,
        )
    )
}

private fun pseudoRandom(seed: Int): Float {
    var x = (seed shl 13) xor seed
    x = x * (x * x * 15731 + 789221) + 1376312589
    return (x and 0x7fffffff) / 2147483647f
}

private val WoodLight = Color(0xFFFFF0D2)
private val WoodShadow = Color(0xFF281400)
private val WoodGrain = Color(0xFFFFEBC8)
private val LampLight = Color(0xFFFFF8E6)
