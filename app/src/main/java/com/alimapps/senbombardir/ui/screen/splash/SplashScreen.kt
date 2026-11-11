package com.alimapps.senbombardir.ui.screen.splash

import android.app.Activity
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alimapps.senbombardir.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val SplashBackground = Color(0xFF1E7F2F)

private const val SCENE_ASPECT_RATIO = 1056f / 628f

@Composable
fun SplashScreen(
    onReady: () -> Unit,
    onFinished: () -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current
    val currentOnReady by rememberUpdatedState(onReady)
    val currentOnFinished by rememberUpdatedState(onFinished)

    val animationsEnabled = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) > 0f
    }

    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.dop_tep_splash))
    val composition by compositionResult
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = animationsEnabled,
    )

    val slogans = stringArrayResource(R.array.splash_slogans)
    val slogan = remember { slogans.random() }

    val sceneAlpha = remember { Animatable(1f) }
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.6f) }
    val sloganAlpha = remember { Animatable(0f) }

    DisposableEffect(Unit) {
        val window = (view.context as Activity).window
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        onDispose {
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }

    LaunchedEffect(Unit) {
        delay(1500)
        currentOnReady()
    }

    LaunchedEffect(Unit) {
        snapshotFlow { compositionResult.isComplete }.first { it }
        currentOnReady()

        if (animationsEnabled && compositionResult.isSuccess) {
            snapshotFlow { progress }.first { it >= 1f }
        }

        coroutineScope {
            launch { sceneAlpha.animateTo(0f, tween(300)) }
            launch { logoAlpha.animateTo(1f, tween(300)) }
            launch {
                logoScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
                )
            }
            launch {
                delay(250)
                sloganAlpha.animateTo(1f, tween(350))
            }
        }
        delay(1800)
        currentOnFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { currentOnFinished() },
            ),
    ) {
        if (animationsEnabled) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .aspectRatio(SCENE_ASPECT_RATIO)
                    .graphicsLayer { alpha = sceneAlpha.value },
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .graphicsLayer {
                        alpha = logoAlpha.value
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                    }
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(30.dp))
                    .clip(RoundedCornerShape(30.dp)),
            )
            Text(
                text = slogan,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = sloganAlpha.value
                    translationY = (1f - sloganAlpha.value) * 12.dp.toPx()
                },
            )
        }

        Text(
            text = stringResource(R.string.splash_made_in),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = Color.White.copy(alpha = 0.75f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
        )
    }
}
