package com.alimapps.senbombardir.ui.screen.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.ui.model.types.SettingsItemType
import com.alimapps.senbombardir.utils.empty
import com.alimapps.senbombardir.BuildConfig
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri
import com.alimapps.senbombardir.ui.navigation.NavigationItem

private const val GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=com.alimapps.senbombardir"
private const val TELEGRAM_URL = "https://t.me/+_Ur1Ixp_1bNhNTc6"

@Composable
fun SettingsScreen(
    navController: NavController,
    onLanguageClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    SettingsScreenContent(
        navController = navController,
        viewModel = viewModel,
        onAction = viewModel::action,
        onLanguageClick = onLanguageClick,
    )
}

@Composable
private fun SettingsScreenContent(
    navController: NavController,
    viewModel: SettingsViewModel,
    onAction: (SettingsAction) -> Unit,
    onLanguageClick: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SettingsEffect.ShowSelectLanguage -> onLanguageClick()
                is SettingsEffect.Share -> openShareIntent(context)
                is SettingsEffect.OpenPlayMarket -> openPlayMarket(context)
                is SettingsEffect.OpenTelegram -> openTelegram(context)
                is SettingsEffect.OpenActivationScreen -> navController.navigate(NavigationItem.Activation.route)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SettingsItemType.entries.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onAction(SettingsAction.OnSettingsItemClicked(item)) }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = String.empty,
                    modifier = Modifier
                )
                Text(
                    text = stringResource(id = item.stringRes),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }

        Text(
            text = "${stringResource(id = R.string.settings_version)}: ${BuildConfig.VERSION_NAME} - ${BuildConfig.BUILD_TYPE}",
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(top = 12.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

private fun openShareIntent(context: Context) {
    val intent = Intent.createChooser(
        Intent().prepareShareIntent(GOOGLE_PLAY_URL),
        context.getString(R.string.settings_item_share)
    )
    context.startActivity(intent)
}

private fun openPlayMarket(context: Context) {
    context.startActivity(
        Intent(Intent.ACTION_VIEW).apply {
            data = GOOGLE_PLAY_URL.toUri()
        }
    )
}

private fun openTelegram(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, TELEGRAM_URL.toUri()))
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.telegram_not_installed), Toast.LENGTH_LONG).show()
    }
}

private fun Intent.prepareShareIntent(text: String): Intent {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, text)
    type = "text/plain"
    return this
}