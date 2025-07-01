package com.alimapps.senbombardir.ui.screen.results

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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.screen.game.widget.block.PlayersResultsBlock
import com.alimapps.senbombardir.ui.screen.game.widget.block.TeamsResultsBlock
import com.alimapps.senbombardir.ui.screen.game.widget.bottomsheet.ConfirmationBottomSheet
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GameResultsScreen(
    navController: NavController,
    viewModel: GameResultsViewModel,
) {
    GameResultsScreenContent(
        navController = navController,
        viewModel = viewModel,
        onAction = viewModel::action,
    )
}

@Composable
private fun GameResultsScreenContent(
    navController: NavController,
    viewModel: GameResultsViewModel,
    onAction: (GameResultsAction) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showClearResultsConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is GameResultsEffect.CloseScreen -> navController.navigateUp()
                is GameResultsEffect.ShowClearResultsConfirmationBottomSheet -> showClearResultsConfirmation = true
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
                    contentDescription = "GameResultsScreenArrowBackIcon",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAction(GameResultsAction.OnBackClicked) }
                )
                Text(
                    text = stringResource(id = R.string.function_all_results),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "GameResultsScreenClearResultsIcon",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAction(GameResultsAction.OnClearResultsClicked) }
                )
            }
        },
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
                    .padding(bottom = 16.dp)
            ) {
                if (uiState.teamUiModelList.size > 2) {
                    TeamsResultsBlock(
                        teamUiModelList = uiState.teamUiModelList,
                    )
                }

                if (uiState.playerUiModelList.isNotEmpty()) {
                    PlayersResultsBlock(
                        playerUiModelList = uiState.playerUiModelList,
                    )
                }
            }
        }
    }

    when {
        showClearResultsConfirmation -> {
            ConfirmationBottomSheet(
                title = stringResource(id = R.string.clear_all_results_title),
                onPositiveClicked = {
                    showClearResultsConfirmation = false
                    onAction(GameResultsAction.OnClearResultsConfirmationClicked)
                },
                onNegativeClicked = { showClearResultsConfirmation = false },
                onDismissed = { showClearResultsConfirmation = false },
            )
        }
    }
}