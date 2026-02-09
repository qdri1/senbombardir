package com.alimapps.senbombardir.ui.screen.add.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.types.GameFormat
import com.alimapps.senbombardir.ui.model.types.GameRule
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam2
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam3
import com.alimapps.senbombardir.ui.model.types.GameRuleTeam4
import com.alimapps.senbombardir.ui.model.types.TeamColor
import com.alimapps.senbombardir.ui.model.types.TeamQuantity
import com.alimapps.senbombardir.ui.navigation.NavigationItem
import com.alimapps.senbombardir.ui.navigation.NavigationResultManager
import com.alimapps.senbombardir.ui.screen.add.game.result.UpdateGameResult
import com.alimapps.senbombardir.ui.utils.parseHexColor
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun AddGameScreen(
    navController: NavController,
    viewModel: AddGameViewModel,
    navigationResultManager: NavigationResultManager = koinInject(),
) {
    AddGameScreenContent(
        navController = navController,
        navigationResultManager = navigationResultManager,
        viewModel = viewModel,
        onAction = viewModel::action,
    )
}

@Composable
private fun AddGameScreenContent(
    navController: NavController,
    navigationResultManager: NavigationResultManager,
    viewModel: AddGameViewModel,
    onAction: (AddGameAction) -> Unit,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showColorsBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AddGameEffect.ShowColorsBottomSheet -> showColorsBottomSheet = true
                is AddGameEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = context.getString(effect.stringRes))
                }
                is AddGameEffect.OpenGameScreen -> navController.navigate(
                    route = NavigationItem.Game.createRoute(effect.gameId),
                    builder = {
                        popUpTo(route = NavigationItem.AddGame.route) { inclusive = true }
                    },
                )
                is AddGameEffect.CloseScreen -> navController.navigateUp()
                is AddGameEffect.CloseScreenWithResult -> {
                    navigationResultManager.sendResult(
                        result = UpdateGameResult,
                        key = UpdateGameResult::class.java.name,
                    )
                    navController.navigateUp()
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
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "AddGameScreenArrowBackIcon",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAction(AddGameAction.CloseScreen) }
                )
                Text(
                    text = when (viewModel.screenStateType.value) {
                        ScreenStateType.Add -> stringResource(id = R.string.add_game)
                        ScreenStateType.Update -> stringResource(id = R.string.update_game)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                )
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "AddGameScreenDoneIcon",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAction(AddGameAction.OnFinishClicked) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it),
        ) {
            TextFieldWidget(
                value = viewModel.gameNameFieldState.value,
                onValueChange = { value -> onAction(AddGameAction.OnGameTextValueChanged(value)) },
                hint = stringResource(id = R.string.game_name),
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            TextFieldWidget(
                value = viewModel.timeInMinuteFieldState.value,
                onValueChange = { value -> onAction(AddGameAction.OnTimeTextValueChanged(value)) },
                hint = stringResource(id = R.string.game_time),
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            if (viewModel.screenStateType.value == ScreenStateType.Add) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Text(
                        text = stringResource(id = R.string.game_format),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                            .padding(bottom = 8.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        GameFormat.entries.forEach { format ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = format.format,
                                    color = if (format == viewModel.gameFormatState.value) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                RadioButton(
                                    selected = format == viewModel.gameFormatState.value,
                                    onClick = { onAction(AddGameAction.OnGameFormatSelected(format)) },
                                    colors = RadioButtonDefaults.colors(
                                        unselectedColor = MaterialTheme.colorScheme.surfaceVariant,
                                    ),
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Text(
                        text = stringResource(id = R.string.team_quantity),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                            .padding(bottom = 8.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TeamQuantity.entries.forEach { teamQuantity ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "${teamQuantity.quantity}",
                                    color = if (teamQuantity == viewModel.teamQuantityState.value) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                RadioButton(
                                    selected = teamQuantity == viewModel.teamQuantityState.value,
                                    onClick = { onAction(AddGameAction.OnTeamQuantitySelected(teamQuantity)) },
                                    colors = RadioButtonDefaults.colors(
                                        unselectedColor = MaterialTheme.colorScheme.surfaceVariant,
                                    ),
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Text(
                        text = stringResource(id = R.string.game_rules),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        val rules = when (viewModel.gameRuleState.value) {
                            is GameRuleTeam2 -> GameRuleTeam2.entries
                            is GameRuleTeam3 -> GameRuleTeam3.entries
                            is GameRuleTeam4 -> GameRuleTeam4.entries
                            else -> emptyList()
                        }
                        rules.forEach { rule ->
                            GameRulesItemWidget(
                                rule = rule,
                                selectedGameRule = viewModel.gameRuleState.value,
                                onAction = onAction,
                            )
                        }
                    }
                }
            }

            TabWithTextFields(
                viewModel = viewModel,
                onAction = onAction,
            )
        }
    }

    if (showColorsBottomSheet) {
        TeamColorsBottomSheet(
            onColorSelected = {
                showColorsBottomSheet = false
                onAction(AddGameAction.OnTeamColorSelected(it))
            },
            onDismissed = { showColorsBottomSheet = false },
        )
    }
}

@Composable
fun GameRulesItemWidget(
    rule: GameRule,
    selectedGameRule: GameRule,
    onAction: (AddGameAction) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = rule == selectedGameRule,
            onClick = { onAction(AddGameAction.OnGameRuleSelected(rule)) },
            colors = RadioButtonDefaults.colors(
                unselectedColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
        Text(
            text = stringResource(id = rule.stringRes),
            color = if (rule == selectedGameRule) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onAction(AddGameAction.OnGameRuleSelected(rule))
                }
        )
    }
}

@Composable
fun TabWithTextFields(
    viewModel: AddGameViewModel,
    onAction: (AddGameAction) -> Unit,
) {
    val tabTitles = mutableListOf<String>().apply {
        repeat(viewModel.teamQuantityState.value.quantity) { index -> add(stringResource(id = R.string.team_number, "${index + 1}")) }
    }

    Column(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        TabRow(selectedTabIndex = viewModel.selectedTeamTabIndex.value) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = viewModel.selectedTeamTabIndex.value == index,
                    onClick = { onAction(AddGameAction.OnTeamTabClicked(index)) },
                    text = {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                )
            }
        }

        TabContent(
            viewModel = viewModel,
            onAction = onAction,
        )
    }
}

@Composable
fun TabContent(
    viewModel: AddGameViewModel,
    onAction: (AddGameAction) -> Unit,
) {

    val tabIndex = viewModel.selectedTeamTabIndex.value
    val tabTeamNameFieldValue = viewModel.teamNameFields[tabIndex]
    val tabTeamColor = viewModel.teamColors[tabIndex]
    val tabPlayersTextFields = viewModel.playersTextFields[tabIndex]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = parseHexColor(tabTeamColor.value.hexColor),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onAction(AddGameAction.OnTeamColorClicked) }
            )
            TextFieldWidget(
                value = tabTeamNameFieldValue.value,
                onValueChange = { value -> onAction(AddGameAction.OnTeamNameValueChanged(tabIndex, value)) },
                hint = stringResource(id = R.string.team_name),
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )
        }

        tabPlayersTextFields.forEachIndexed { fieldIndex, fieldValue ->
            TextFieldWidget(
                value = fieldValue,
                onValueChange = { value -> onAction(AddGameAction.OnPlayerNameValueChanged(tabIndex, fieldIndex, value)) },
                hint = stringResource(id = R.string.player_number, "${fieldIndex + 1}"),
                imeAction = if (fieldIndex < tabPlayersTextFields.lastIndex) ImeAction.Next else ImeAction.Done,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = { onAction(AddGameAction.OnAddPlayerClicked(tabIndex)) },
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 16.dp
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.add_player))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamColorsBottomSheet(
    onColorSelected: (TeamColor) -> Unit,
    onDismissed: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissed() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Text(
            text = stringResource(id = R.string.choose_team_color),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .padding(bottom = 8.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TeamColor.entries.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = parseHexColor(color.hexColor),
                            shape = RoundedCornerShape(16.dp),
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                        )
                        .clickable { onColorSelected(color) }
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        )
    }
}

@Composable
private fun TextFieldWidget(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    imeAction: ImeAction = ImeAction.Default,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        label = {
            Text(
                text = hint,
                color = MaterialTheme.colorScheme.surfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                        tint = Color.Black,
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() },
        ),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        modifier = modifier,
    )
}