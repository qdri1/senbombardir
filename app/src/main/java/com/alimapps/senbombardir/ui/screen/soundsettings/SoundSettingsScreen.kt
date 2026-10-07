package com.alimapps.senbombardir.ui.screen.soundsettings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.alimapps.senbombardir.ui.model.types.GameSoundSetting
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun SoundSettingsScreen(
    navController: NavController,
    viewModel: SoundSettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SoundSettingsEffect.CloseScreen -> navController.popBackStack()
            }
        }
    }

    SoundSettingsScreenContent(
        uiState = uiState,
        onAction = viewModel::action,
    )
}

@Composable
private fun SoundSettingsScreenContent(
    uiState: SoundSettingsUiState,
    onAction: (SoundSettingsAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "SoundSettingsArrowBackIcon",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAction(SoundSettingsAction.OnBackClicked) }
                )
                Text(
                    text = stringResource(id = R.string.sound_settings_title),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SettingsCard {
                SoundSettingRow(
                    title = stringResource(id = R.string.sound_settings_all),
                    description = stringResource(id = R.string.sound_settings_all_description),
                    checked = uiState.isAllEnabled,
                    onCheckedChange = { onAction(SoundSettingsAction.OnAllToggled(it)) },
                )
            }

            SettingsCard {
                GameSoundSetting.entries.forEachIndexed { index, setting ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    SoundSettingRow(
                        title = stringResource(id = setting.titleRes),
                        description = stringResource(id = setting.descriptionRes),
                        checked = uiState.settings[setting] ?: true,
                        onCheckedChange = { onAction(SoundSettingsAction.OnSettingToggled(setting, it)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface),
    ) {
        content()
    }
}

@Composable
private fun SoundSettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}
