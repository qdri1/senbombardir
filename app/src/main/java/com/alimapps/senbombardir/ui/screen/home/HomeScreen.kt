package com.alimapps.senbombardir.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam2
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam3
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam4
import com.alimapps.senbombardir.ui.navigation.NavigationItem
import com.alimapps.senbombardir.ui.navigation.NavigationResultManager
import com.alimapps.senbombardir.ui.screen.game.result.DeleteGameResult
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
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            bottom = 96.dp,
                        ),
                    ) {
                        items(uiState.games) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { onAction(HomeAction.OnGameCardClicked(gameId = item.id)) }
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.home_game_name),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    Text(
                                        text = item.name,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.home_game_format),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    Text(
                                        text = item.gameFormat.format,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.home_game_team_quantity),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    Text(
                                        text = item.teamQuantity.quantity.toString(),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.home_game_time),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    Text(
                                        text = stringResource(id = R.string.time_in_minutes, item.timeInMinutes),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.home_game_rule),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                    Text(
                                        text = when (val gameRule = item.gameRule) {
                                            is GameRuleTeam2 -> stringResource(id = gameRule.stringRes)
                                            is GameRuleTeam3 -> stringResource(id = gameRule.stringRes)
                                            is GameRuleTeam4 -> stringResource(id = gameRule.stringRes)
                                            else -> String.empty
                                        },
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = R.string.game_empty),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "HomeScreenRefreshIcon",
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onAction(HomeAction.OnRefreshIconClicked) }
                    )
                }
            }
        }

        Button(
            onClick = { onAction(HomeAction.OnAddGameButtonClicked) },
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 16.dp
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.add_game))
        }
    }
}