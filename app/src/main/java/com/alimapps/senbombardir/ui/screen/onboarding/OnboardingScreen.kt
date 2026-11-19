package com.alimapps.senbombardir.ui.screen.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.data.source.NotificationPermissionStorage
import com.alimapps.senbombardir.domain.model.AppLanguage
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * First-launch onboarding (same flow as iOS): language → what the app does → notifications.
 * [onLanguageSelected] applies the locale right away, so the rest is shown in the chosen language.
 */
@Composable
fun OnboardingScreen(
    onLanguageSelected: (AppLanguage) -> Unit,
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
    notificationPermissionStorage: NotificationPermissionStorage = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onAction = viewModel::action

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onAction(OnboardingAction.OnNotificationsAnswered)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is OnboardingEffect.Finish -> onFinished()
            }
        }
    }

    BackHandler(enabled = uiState.stepIndex > 0) {
        onAction(OnboardingAction.OnBackClicked)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        OnboardingTopBar(
            progress = uiState.progress,
            showBack = uiState.stepIndex > 0,
            onBackClick = { onAction(OnboardingAction.OnBackClicked) },
        )

        // Slides in from the right when going forward and from the left when going back.
        AnimatedContent(
            targetState = uiState.stepIndex,
            transitionSpec = {
                val forward = targetState > initialState
                (slideInHorizontally { width -> if (forward) width else -width } + fadeIn())
                    .togetherWith(slideOutHorizontally { width -> if (forward) -width else width } + fadeOut())
            },
            label = "onboardingStep",
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { index ->
            when (uiState.steps[index]) {
                OnboardingStep.Language -> LanguageStep(
                    selectedLanguage = uiState.selectedLanguage,
                    onLanguageClick = { language ->
                        onAction(OnboardingAction.OnLanguageSelected(language))
                        onLanguageSelected(language)
                    },
                )
                OnboardingStep.Welcome -> WelcomeStep()
                OnboardingStep.Rotation -> RotationStep()
                OnboardingStep.Stats -> StatsStep()
                OnboardingStep.Sounds -> SoundsStep(
                    isVoicePlaying = uiState.isVoicePlaying,
                    onVoiceDemoClick = { onAction(OnboardingAction.OnVoiceDemoClicked) },
                )
                OnboardingStep.Notifications -> NotificationsStep()
            }
        }

        OnboardingBottomBar(
            step = uiState.step,
            canContinue = uiState.canContinue,
            onContinueClick = { onAction(OnboardingAction.OnContinueClicked) },
            onAllowNotificationsClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // So the Settings switch later opens the system settings instead of asking again.
                    notificationPermissionStorage.setPermissionRequested()
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    onAction(OnboardingAction.OnNotificationsAnswered)
                }
            },
            onSkipNotificationsClick = { onAction(OnboardingAction.OnNotificationsAnswered) },
        )
    }
}

// region Bars

@Composable
private fun OnboardingTopBar(
    progress: Float,
    showBack: Boolean,
    onBackClick: () -> Unit,
) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "onboardingProgress")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBackClick,
            enabled = showBack,
            modifier = Modifier.alpha(if (showBack) 1f else 0f),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
private fun OnboardingBottomBar(
    step: OnboardingStep,
    canContinue: Boolean,
    onContinueClick: () -> Unit,
    onAllowNotificationsClick: () -> Unit,
    onSkipNotificationsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (step == OnboardingStep.Notifications) {
            PrimaryButton(text = stringResource(R.string.notification_rationale_allow), onClick = onAllowNotificationsClick)
            Text(
                text = stringResource(R.string.notification_rationale_later),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onSkipNotificationsClick)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            )
        } else {
            PrimaryButton(
                text = stringResource(
                    if (step == OnboardingStep.Welcome) R.string.onboarding_start_button else R.string.onboarding_continue_button
                ),
                enabled = canContinue,
                onClick = onContinueClick,
            )
        }
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 16.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

// endregion

// region Steps

private val languages = listOf(
    AppLanguage.KZ to "🇰🇿",
    AppLanguage.RU to "🇷🇺",
    AppLanguage.EN to "🇬🇧",
)

@Composable
private fun LanguageStep(
    selectedLanguage: AppLanguage?,
    onLanguageClick: (AppLanguage) -> Unit,
) {
    StepScroll {
        // Shown in all three languages: the user hasn't picked one yet.
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            Text(
                text = "Тілді таңдаңыз",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.displayLarge,
            )
            Text(
                text = "Выберите язык · Choose language",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        languages.forEach { (language, flag) ->
            LanguageCard(
                flag = flag,
                title = language.title,
                isSelected = selectedLanguage == language,
                onClick = { onLanguageClick(language) },
            )
        }
    }
}

@Composable
private fun LanguageCard(
    flag: String,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        ) {
            Text(text = flag, fontSize = 26.sp)
        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
private fun WelcomeStep() {
    // Same 24dp gap between the logo, the title and the description.
    CenteredStep {
        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = null,
            modifier = Modifier
                .size(112.dp)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(26.dp))
                .clip(RoundedCornerShape(26.dp)),
        )
        Text(
            text = stringResource(R.string.onboarding_welcome_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.onboarding_welcome_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RotationStep() {
    StepScroll {
        StepHeader(title = R.string.onboarding_rotation_title, subtitle = R.string.onboarding_rotation_subtitle)
        ScoreboardPreview()
        FeatureBullet(icon = painterResource(R.drawable.ic_list_bullet_rectangle), text = R.string.onboarding_rotation_bullet_rules)
        FeatureBullet(icon = rememberVectorPainter(Icons.Outlined.Timer), text = R.string.onboarding_rotation_bullet_timer)
        FeatureBullet(icon = rememberVectorPainter(Icons.AutoMirrored.Filled.VolumeUp), text = R.string.onboarding_rotation_bullet_voice, isPro = true)
    }
}

@Composable
private fun StatsStep() {
    StepScroll {
        StepHeader(title = R.string.onboarding_stats_title, subtitle = R.string.onboarding_stats_subtitle)
        TeamsTablePreview()
        PlayersTablePreview()
        FeatureBullet(icon = rememberVectorPainter(Icons.Outlined.TableChart), text = R.string.onboarding_stats_bullet_teams)
        FeatureBullet(icon = painterResource(R.drawable.ic_crown), text = R.string.onboarding_stats_bullet_best, isPro = true)
        FeatureBullet(icon = rememberVectorPainter(Icons.Filled.EmojiEvents), text = R.string.onboarding_stats_bullet_champions, isPro = true)
    }
}

@Composable
private fun SoundsStep(
    isVoicePlaying: Boolean,
    onVoiceDemoClick: () -> Unit,
) {
    StepScroll {
        StepHeader(title = R.string.onboarding_sounds_title, subtitle = R.string.onboarding_sounds_subtitle)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onVoiceDemoClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Icon(
                    imageVector = if (isVoicePlaying) Icons.Filled.GraphicEq else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.onboarding_sounds_demo_button),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.text_to_speech_goal, stringResource(R.string.onboarding_sample_player_1)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        FeatureBullet(icon = rememberVectorPainter(Icons.Filled.RecordVoiceOver), text = R.string.onboarding_sounds_bullet_voice, isPro = true)
        FeatureBullet(icon = rememberVectorPainter(Icons.Filled.LibraryMusic), text = R.string.activation_text_5, isPro = true)
    }
}

@Composable
private fun NotificationsStep() {
    CenteredStep {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        ) {
            Icon(
                imageVector = Icons.Filled.NotificationsActive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.notification_rationale_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.notification_rationale_message),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
        NotificationPreview()
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 1.dp)
                    .size(16.dp),
            )
            Text(
                text = stringResource(R.string.onboarding_notifications_hint),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

// endregion

// region Building blocks

@Composable
private fun StepScroll(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

/** Vertically centred step that still scrolls on small screens. */
@Composable
private fun CenteredStep(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        content = content,
    )
}

@Composable
private fun StepHeader(@StringRes title: Int, @StringRes subtitle: Int) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp),
    ) {
        Text(
            text = stringResource(title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displayLarge,
        )
        Text(
            text = stringResource(subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun FeatureBullet(
    icon: Painter,
    @StringRes text: Int,
    isPro: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = stringResource(text),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.weight(1f),
        )
        if (isPro) {
            Text(
                text = "PRO",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun PreviewCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        content = content,
    )
}

@Composable
private fun ScoreboardPreview() {
    PreviewCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TeamChip(name = stringResource(R.string.onboarding_sample_team_1), color = MaterialTheme.colorScheme.primary)
            Text(
                text = "2 : 1",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displayLarge,
                fontSize = 34.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            TeamChip(name = stringResource(R.string.onboarding_sample_team_2), color = MaterialTheme.colorScheme.tertiary)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "06:59",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.next_playing_teams),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.onboarding_sample_next_teams),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun TeamChip(name: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.width(80.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_jersey),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp),
        )
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

private data class TeamRow(val number: Int, val games: Int, val wins: Int, val draws: Int, val losses: Int) {
    val points: Int get() = wins * 3 + draws
}

// 3 teams, 9 matches: 8 decided + 1 draw between T-1 and T-2.
private val teamRows = listOf(
    TeamRow(number = 1, games = 6, wins = 4, draws = 1, losses = 1),
    TeamRow(number = 2, games = 6, wins = 3, draws = 1, losses = 2),
    TeamRow(number = 3, games = 6, wins = 1, draws = 0, losses = 5),
)

@Composable
private fun TeamsTablePreview() {
    PreviewCard {
        TableRow(
            name = stringResource(R.string.team_result_name),
            values = listOf(
                stringResource(R.string.team_result_games),
                stringResource(R.string.team_result_wins),
                stringResource(R.string.team_result_draws),
                stringResource(R.string.team_result_loses),
                stringResource(R.string.team_result_points),
            ),
            isHeader = true,
            cellWidth = 34,
        )
        teamRows.forEachIndexed { index, row ->
            HorizontalDivider(color = MaterialTheme.colorScheme.background)
            TableRow(
                name = stringResource(R.string.team_number, row.number.toString()),
                values = listOf(row.games, row.wins, row.draws, row.losses, row.points).map(Int::toString),
                leadingIcon = if (index == 0) rememberVectorPainter(Icons.Filled.EmojiEvents) else null,
                boldLastValue = true,
                cellWidth = 34,
            )
        }
    }
}

private data class PlayerRow(@StringRes val name: Int, val goals: Int, val assists: Int, val saves: Int)

private val playerRows = listOf(
    PlayerRow(name = R.string.onboarding_sample_player_1, goals = 5, assists = 2, saves = 0),
    PlayerRow(name = R.string.onboarding_sample_player_2, goals = 3, assists = 4, saves = 1),
    PlayerRow(name = R.string.onboarding_sample_player_3, goals = 1, assists = 1, saves = 6),
)

@Composable
private fun PlayersTablePreview() {
    PreviewCard {
        TableRow(
            name = stringResource(R.string.player_result_name),
            values = listOf(
                stringResource(R.string.player_result_goals),
                stringResource(R.string.player_result_assists),
                stringResource(R.string.player_result_saves),
            ),
            isHeader = true,
            cellWidth = 40,
        )
        playerRows.forEachIndexed { index, row ->
            HorizontalDivider(color = MaterialTheme.colorScheme.background)
            TableRow(
                name = stringResource(row.name),
                values = listOf(row.goals, row.assists, row.saves).map(Int::toString),
                leadingIcon = if (index == 0) painterResource(R.drawable.ic_crown) else null,
                cellWidth = 40,
            )
        }
    }
}

@Composable
private fun TableRow(
    name: String,
    values: List<String>,
    cellWidth: Int,
    isHeader: Boolean = false,
    leadingIcon: Painter? = null,
    boldLastValue: Boolean = false,
) {
    val color = if (isHeader) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
    val style = if (isHeader) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelSmall
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isHeader) 0.dp else 10.dp)
            .padding(bottom = if (isHeader) 8.dp else 0.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(text = name, color = color, style = style)
        }
        values.forEachIndexed { index, value ->
            Text(
                text = value,
                color = color,
                style = style,
                fontWeight = if (boldLastValue && index == values.lastIndex) FontWeight.Bold else null,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(cellWidth.dp),
            )
        }
    }
}

/** Mock of the notification the timer sends. */
@Composable
private fun NotificationPreview() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp)),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = "Dop Tep",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = stringResource(R.string.onboarding_notification_sample),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp),
        )
    }
}

// endregion
