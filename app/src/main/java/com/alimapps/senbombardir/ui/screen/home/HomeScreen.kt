package com.alimapps.senbombardir.ui.screen.home

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.GameUiModel
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam2
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam3
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam4
import com.alimapps.senbombardir.ui.navigation.NavigationItem
import com.alimapps.senbombardir.ui.navigation.NavigationResultManager
import com.alimapps.senbombardir.ui.screen.game.result.DeleteGameResult
import com.alimapps.senbombardir.ui.screen.game.widget.block.pitchSurfaceBackground
import com.alimapps.senbombardir.utils.empty
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    navController: NavController,
    navigationResultManager: NavigationResultManager = koinInject(),
    viewModel: HomeViewModel = koinViewModel(),
) {
    HomeScreenContent(
        navController = navController,
        navigationResultManager = navigationResultManager,
        viewModel = viewModel,
        onAction = viewModel::action,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    navController: NavController,
    navigationResultManager: NavigationResultManager,
    viewModel: HomeViewModel,
    onAction: (HomeAction) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        onAction(HomeAction.OnScreenResumed)
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        navigationResultManager
            .observeResult(DeleteGameResult::class.java.name)
            .collectLatest { result ->
                onAction(HomeAction.OnInterceptionNavigationResult(result))
            }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HomeEffect.OpenAddGameScreen -> navController.navigate(NavigationItem.AddGame.createRoute())
                is HomeEffect.OpenGameScreen -> navController.navigate(NavigationItem.Game.createRoute(effect.gameId))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onBackground,
                    strokeWidth = 6.dp,
                )
            }
            uiState.games.isNotEmpty() -> {
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { onAction(HomeAction.OnRefreshed) },
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            bottom = 104.dp,
                        ),
                    ) {
                        item {
                            Text(
                                text = stringResource(id = R.string.tab_home),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.displayLarge,
                                fontSize = 28.sp,
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                        }
                        items(uiState.games, key = { it.id }) { item ->
                            GameCard(
                                game = item,
                                onClick = { onAction(HomeAction.OnGameCardClicked(gameId = item.id)) },
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }

                AddGameButton(
                    onClick = { onAction(HomeAction.OnAddGameButtonClicked) },
                    withShadow = true,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                )
            }
            else -> {
                EmptyState(
                    onAddGameClick = { onAction(HomeAction.OnAddGameButtonClicked) },
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    onAddGameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.soccer_ball))

    Column(
        modifier = modifier
            .padding(horizontal = 32.dp)
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(140.dp),
        )
        Text(
            text = stringResource(id = R.string.game_empty),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = stringResource(id = R.string.game_empty_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        AddGameButton(
            onClick = onAddGameClick,
            withShadow = false,
            modifier = Modifier.padding(top = 24.dp),
        )
    }
}

@Composable
private fun AddGameButton(
    onClick: () -> Unit,
    withShadow: Boolean,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .then(
                if (withShadow) {
                    Modifier.shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = primary.copy(alpha = 0.35f),
                        spotColor = primary.copy(alpha = 0.35f),
                    )
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .background(primary)
            .clickable(onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = stringResource(id = R.string.add_game),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun GameCard(
    game: GameUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f),
            )
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
    ) {
        GameCardHeader(game = game)
        GameCardDetails(game = game)
    }
}

@Composable
private fun GameCardHeader(game: GameUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pitchSurfaceBackground(game.pitchSurface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = game.name,
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = updatedText(game.modifiedAt),
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GameCardDetails(game: GameUiModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InfoChip(
                icon = painterResource(id = R.drawable.ic_sports_court),
                text = game.gameFormat.format,
            )
            InfoChip(
                icon = rememberVectorPainter(Icons.Filled.Groups),
                text = stringResource(id = R.string.home_teams_count, game.teamQuantity.quantity),
            )
            InfoChip(
                icon = rememberVectorPainter(Icons.Outlined.Timer),
                text = stringResource(id = R.string.time_in_minutes, game.timeInMinutes),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_list_bullet_rectangle),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = when (val gameRule = game.gameRule) {
                    is GameRuleTeam2 -> stringResource(id = gameRule.stringRes)
                    is GameRuleTeam3 -> stringResource(id = gameRule.stringRes)
                    is GameRuleTeam4 -> stringResource(id = gameRule.stringRes)
                    else -> String.empty
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun InfoChip(
    icon: Painter,
    text: String,
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

/** "Обновлено вчера" / "Обновлено 2 часа назад" — same idea as iOS RelativeDateTimeFormatter. */
@Composable
private fun updatedText(modifiedAt: Long): String {
    val now = System.currentTimeMillis()
    if (modifiedAt <= 0L || now - modifiedAt < DateUtils.MINUTE_IN_MILLIS) {
        return stringResource(id = R.string.home_updated_now)
    }
    val relative = DateUtils.getRelativeTimeSpanString(
        modifiedAt.coerceAtMost(now),
        now,
        DateUtils.MINUTE_IN_MILLIS,
    ).toString()
    return stringResource(id = R.string.home_updated, relative)
}
