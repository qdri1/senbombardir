package com.alimapps.senbombardir.ui.screen.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.LiveGameResultUiModel
import com.alimapps.senbombardir.ui.model.OptionPlayersUiModel
import com.alimapps.senbombardir.ui.model.PlayerResultUiModel
import com.alimapps.senbombardir.ui.model.types.TeamQuantity
import com.alimapps.senbombardir.ui.navigation.NavigationItem
import com.alimapps.senbombardir.ui.navigation.NavigationResultManager
import com.alimapps.senbombardir.ui.screen.add.game.result.UpdateGameResult
import com.alimapps.senbombardir.ui.screen.game.result.DeleteGameResult
import com.alimapps.senbombardir.ui.screen.game.widget.block.FunctionsBlock
import com.alimapps.senbombardir.ui.screen.game.widget.block.GameInfoBlock
import com.alimapps.senbombardir.ui.screen.game.widget.block.LiveGameBlock
import com.alimapps.senbombardir.ui.screen.game.widget.block.PlayersResultsBlock
import com.alimapps.senbombardir.ui.screen.game.widget.block.SoundsBlock
import com.alimapps.senbombardir.ui.screen.game.widget.block.TeamsResultsBlock
import com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet.ConfirmationBottomSheet
import com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet.GameInfoBottomSheet
import com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet.LiveGameResultBottomSheet
import com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet.OptionPlayersBottomSheet
import com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet.PlayerResultBottomSheet
import com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet.StayTeamSelectionBottomSheet
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun GameScreen(
    navController: NavController,
    viewModel: GameViewModel,
    navigationResultManager: NavigationResultManager = koinInject(),
) {
    GameScreenContent(
        navController = navController,
        navigationResultManager = navigationResultManager,
        viewModel = viewModel,
        onAction = viewModel::action,
    )
}

@Composable
private fun GameScreenContent(
    navController: NavController,
    navigationResultManager: NavigationResultManager,
    viewModel: GameViewModel,
    onAction: (GameAction) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var optionPlayersUiModel by remember { mutableStateOf<OptionPlayersUiModel?>(null) }
    var showStayTeamSelection by remember { mutableStateOf(false) }
    var showDeleteGameConfirmation by remember { mutableStateOf(false) }
    var showClearResultsConfirmation by remember { mutableStateOf(false) }
    var showFinishGameConfirmation by remember { mutableStateOf(false) }
    var showGoBackConfirmation by remember { mutableStateOf(false) }
    var showGameInfo by remember { mutableStateOf(false) }
    var playerResultUiModel by remember { mutableStateOf<PlayerResultUiModel?>(null) }
    var liveGameResultUiModel by remember { mutableStateOf<LiveGameResultUiModel?>(null) }

    BackHandler {
        onAction(GameAction.OnBackClicked)
    }

    LaunchedEffect(Unit) {
        navigationResultManager
            .observeResult(UpdateGameResult::class.java.name)
            .collectLatest { result ->
                onAction(GameAction.OnInterceptionNavigationResult(result))
            }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is GameEffect.CloseScreen -> navController.navigateUp()
                is GameEffect.CloseScreenWithResult -> {
                    navigationResultManager.sendResult(
                        result = DeleteGameResult,
                        key = DeleteGameResult::class.java.name,
                    )
                    navController.navigateUp()
                }
                is GameEffect.OpenUpdateGame -> navController.navigate(NavigationItem.AddGame.createRoute(effect.gameId))
                is GameEffect.OpenGameResultsScreen -> navController.navigate(NavigationItem.GameResults.createRoute(effect.gameId))
                is GameEffect.ShowOptionPlayersBottomSheet -> optionPlayersUiModel = effect.optionPlayersUiModel
                is GameEffect.ShowPlayerResultBottomSheet -> playerResultUiModel = effect.playerResultUiModel
                is GameEffect.ShowLiveGameResultBottomSheet -> liveGameResultUiModel = effect.liveGameResultUiModel
                is GameEffect.ShowStayTeamSelectionBottomSheet -> showStayTeamSelection = true
                is GameEffect.ShowDeleteGameConfirmationBottomSheet -> showDeleteGameConfirmation = true
                is GameEffect.ShowClearResultsConfirmationBottomSheet -> showClearResultsConfirmation = true
                is GameEffect.ShowFinishGameConfirmationBottomSheet -> showFinishGameConfirmation = true
                is GameEffect.ShowGoBackConfirmationBottomSheet -> showGoBackConfirmation = true
                is GameEffect.ShowGameInfoBottomSheet -> showGameInfo = true
                is GameEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = context.getString(effect.stringRes))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "GameScreenArrowBackIcon",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAction(GameAction.OnBackClicked) }
                )
                Text(
                    text = uiState.gameUiModel?.name.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(it),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp)
            ) {
                uiState.gameUiModel?.let { gameUiModel ->
                    GameInfoBlock(
                        gameUiModel = gameUiModel,
                    )
                }

                uiState.liveGameUiModel?.let { liveGameUiModel ->
                    LiveGameBlock(
                        liveGameUiModel = liveGameUiModel,
                        timerValueState = viewModel.timerValueState,
                        uiState = uiState,
                        onAction = onAction,
                    )
                    SoundsBlock(
                        onAction = onAction,
                    )
                }

                if (uiState.teamUiModelList.isNotEmpty() && uiState.gameUiModel?.teamQuantity != TeamQuantity.Team2) {
                    TeamsResultsBlock(
                        teamUiModelList = uiState.teamUiModelList,
                    )
                }

                if (uiState.playerUiModelList.isNotEmpty()) {
                    PlayersResultsBlock(
                        playerUiModelList = uiState.playerUiModelList,
                        onPlayerResultClicked = { onAction(GameAction.OnPlayerResultClicked(playerResultUiModel = it)) },
                    )
                }

                uiState.gameUiModel?.let {
                    FunctionsBlock(
                        onAction = onAction,
                    )
                }
            }
        }
    }

    when {
        optionPlayersUiModel != null -> optionPlayersUiModel?.let {
            OptionPlayersBottomSheet(
                optionPlayersUiModel = it,
                onAction = { action ->
                    optionPlayersUiModel = null
                    onAction(action)
                },
                onDismissed = { optionPlayersUiModel = null },
            )
        }
        playerResultUiModel != null -> playerResultUiModel?.let {
            PlayerResultBottomSheet(
                playerResultUiModel = it,
                onSavePlayerResultClicked = { p1, p2 ->
                    playerResultUiModel = null
                    onAction(GameAction.OnSavePlayerResultClicked(playerResultUiModel = p1, playerResultValue = p2))
                },
                onDismissed = { playerResultUiModel = null },
            )
        }
        liveGameResultUiModel != null -> liveGameResultUiModel?.let {
            LiveGameResultBottomSheet(
                liveGameResultUiModel = it,
                onAction = { action ->
                    liveGameResultUiModel = null
                    onAction(action)
                },
                onDismissed = { liveGameResultUiModel = null },
            )
        }
        showStayTeamSelection -> uiState.liveGameUiModel?.let {
            StayTeamSelectionBottomSheet(
                liveGameUiModel = it,
                onAction = { action ->
                    showStayTeamSelection = false
                    onAction(action)
                },
            )
        }
        showDeleteGameConfirmation -> {
            ConfirmationBottomSheet(
                title = stringResource(id = R.string.delete_game_title),
                onPositiveClicked = {
                    showDeleteGameConfirmation = false
                    onAction(GameAction.OnDeleteGameConfirmationClicked)
                },
                onNegativeClicked = { showDeleteGameConfirmation = false },
                onDismissed = { showDeleteGameConfirmation = false },
            )
        }
        showClearResultsConfirmation -> {
            ConfirmationBottomSheet(
                title = stringResource(id = R.string.clear_results_title),
                onPositiveClicked = {
                    showClearResultsConfirmation = false
                    onAction(GameAction.OnClearResultsConfirmationClicked)
                },
                onNegativeClicked = { showClearResultsConfirmation = false },
                onDismissed = { showClearResultsConfirmation = false },
            )
        }
        showFinishGameConfirmation -> {
            ConfirmationBottomSheet(
                title = stringResource(id = R.string.finish_game_title),
                onPositiveClicked = {
                    showFinishGameConfirmation = false
                    onAction(GameAction.OnFinishGameConfirmationClicked)
                },
                onNegativeClicked = { showFinishGameConfirmation = false },
                onDismissed = { showFinishGameConfirmation = false },
            )
        }
        showGoBackConfirmation -> {
            ConfirmationBottomSheet(
                title = stringResource(id = R.string.go_back_title),
                onPositiveClicked = {
                    showGoBackConfirmation = false
                    onAction(GameAction.OnGoBackConfirmationClicked)
                },
                onNegativeClicked = { showGoBackConfirmation = false },
                onDismissed = { showGoBackConfirmation = false },
            )
        }
        showGameInfo -> {
            GameInfoBottomSheet(
                onDismissed = { showGameInfo = false },
            )
        }
    }
}