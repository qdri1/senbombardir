package com.alimapps.senbombardir.ui.screen.activation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.domain.model.ActivationPlan
import com.alimapps.senbombardir.domain.model.BillingType
import com.alimapps.senbombardir.ui.utils.parseHexColor
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ActivationScreen(
    navController: NavController,
    viewModel: ActivationViewModel,
    onActivationPlanSelected: (ActivationPlan) -> Unit,
) {
    ActivationScreenContent(
        navController = navController,
        viewModel = viewModel,
        onAction = viewModel::action,
        onActivationPlanSelected = onActivationPlanSelected,
    )
}

@Composable
private fun ActivationScreenContent(
    navController: NavController,
    viewModel: ActivationViewModel,
    onAction: (ActivationAction) -> Unit,
    onActivationPlanSelected: (ActivationPlan) -> Unit,
) {
    BackHandler { onAction(ActivationAction.OnBackClicked) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ActivationEffect.CloseScreen -> navController.navigateUp()
                is ActivationEffect.OpenGooglePlaySubscriptions -> openGooglePlaySubscriptions(context)
                is ActivationEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = context.getString(effect.stringRes))
                }
                is ActivationEffect.BuySelectedPlan -> onActivationPlanSelected(effect.plan)
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
                        .clickable { onAction(ActivationAction.OnBackClicked) }
                )
                Text(
                    text = "",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.billingType == BillingType.Limited) {
                when (uiState.pageIndex) {
                    0 -> ActivationTextContent(onAction = onAction)
                    1 -> ActivationPlanContent(uiState = uiState, onAction = onAction)
                }
            } else {
                ActivatedContent(uiState = uiState, onAction = onAction)
            }
        }
    }
}

@Composable
private fun ActivationTextContent(
    onAction: (ActivationAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.activation_text_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        ActivationTextItem(
            icon = painterResource(R.drawable.ic_star_shine),
            text = stringResource(R.string.activation_text_5),
        )
        ActivationTextItem(
            icon = painterResource(R.drawable.ic_star_shine),
            text = stringResource(R.string.activation_text_6),
        )
        ActivationTextItem(
            icon = painterResource(R.drawable.ic_star_shine),
            text = stringResource(R.string.activation_text_1),
        )
        ActivationTextItem(
            icon = painterResource(R.drawable.ic_star_shine),
            text = stringResource(R.string.activation_text_2),
        )
        ActivationTextItem(
            icon = painterResource(R.drawable.ic_star_shine),
            text = stringResource(R.string.activation_text_3),
        )
        ActivationTextItem(
            icon = painterResource(R.drawable.ic_star_shine),
            text = stringResource(R.string.activation_text_4),
        )
        Button(
            onClick = { onAction(ActivationAction.ShowPriceButtonClicked) },
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 16.dp
            ),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.activation_text_button),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ActivationPlanContent(
    uiState: ActivationUiState,
    onAction: (ActivationAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.activation_plan_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        ActivationPlanItem(
            text = stringResource(R.string.activation_plan_1),
            desc = stringResource(R.string.activation_plan_desc_subscribe),
            price = uiState.monthlyPrice ?: "0",
            checked = uiState.selectedPlan == ActivationPlan.Monthly,
            plan = ActivationPlan.Monthly,
            onAction = onAction,
        )
        ActivationPlanItem(
            text = stringResource(R.string.activation_plan_2),
            desc = stringResource(R.string.activation_plan_desc_subscribe),
            price = uiState.yearlyPrice ?: "0",
            checked = uiState.selectedPlan == ActivationPlan.Yearly,
            plan = ActivationPlan.Yearly,
            onAction = onAction,
        )
        ActivationPlanItem(
            text = stringResource(R.string.activation_plan_3),
            desc = stringResource(R.string.activation_plan_desc_lifetime),
            price = uiState.unlimitedPrice ?: "0",
            checked = uiState.selectedPlan == ActivationPlan.Unlimited,
            plan = ActivationPlan.Unlimited,
            onAction = onAction,
        )
        Button(
            onClick = { onAction(ActivationAction.BuyButtonClicked) },
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 16.dp
            ),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.activation_plan_button),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ActivatedContent(
    uiState: ActivationUiState,
    onAction: (ActivationAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.activation_plan_activated_text),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
        )
        if (uiState.billingType == BillingType.Subscribe) {
            Button(
                onClick = { onAction(ActivationAction.ManageSubscriptionsButtonClicked) },
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 16.dp
                ),
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.activation_plan_activated_button),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ActivationTextItem(
    icon: Painter,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = "GameScreenEditIcon",
            tint = parseHexColor("#FFA500"),
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ActivationPlanItem(
    text: String,
    desc: String,
    price: String,
    checked: Boolean,
    plan: ActivationPlan,
    onAction: (ActivationAction) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable { onAction(ActivationAction.OnActivationPlanItemClicked(plan)) }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = desc,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Text(
            text = price,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier,
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null,
        )
    }
}

private fun openGooglePlaySubscriptions(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/account/subscriptions".toUri()))
    } catch (_: Exception) {
        Toast.makeText(context, context.getString(R.string.google_play_not_installed), Toast.LENGTH_LONG).show()
    }
}