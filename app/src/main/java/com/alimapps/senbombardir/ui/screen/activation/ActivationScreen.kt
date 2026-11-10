package com.alimapps.senbombardir.ui.screen.activation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alimapps.senbombardir.R
import com.alimapps.senbombardir.domain.model.ActivationPlan
import com.alimapps.senbombardir.domain.model.BillingType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

private val PaywallGreen = Color(0xFF1E7F2F)
private val PaywallGreenDark = Color(0xFF0B3D16)
private val PaywallGold = Color(0xFFFFC83D)

@Composable
fun ActivationScreen(
    navController: NavController,
    viewModel: ActivationViewModel,
    onActivationPlanSelected: (ActivationPlan) -> Unit,
    showOneDay: Boolean = false,
) {
    ActivationScreenContent(
        navController = navController,
        viewModel = viewModel,
        onAction = viewModel::action,
        onActivationPlanSelected = onActivationPlanSelected,
        showOneDay = showOneDay,
    )
}

@Composable
private fun ActivationScreenContent(
    navController: NavController,
    viewModel: ActivationViewModel,
    onAction: (ActivationAction) -> Unit,
    onActivationPlanSelected: (ActivationPlan) -> Unit,
    showOneDay: Boolean = false,
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        if (uiState.billingType == BillingType.Limited) {
            PaywallContent(
                uiState = uiState,
                onAction = onAction,
                showOneDay = showOneDay,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                ActivatedTopBar(onAction = onAction)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    ActivatedContent(uiState = uiState, onAction = onAction)
                }
            }
        }
    }
}

@Composable
private fun ActivatedTopBar(onAction: (ActivationAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onAction(ActivationAction.OnBackClicked) },
        )
    }
}

private data class PaywallFeature(val titleRes: Int, val descRes: Int)

private val paywallFeatures = listOf(
    PaywallFeature(R.string.activation_text_1, R.string.activation_desc_1),
    PaywallFeature(R.string.activation_text_2, R.string.activation_desc_2),
    PaywallFeature(R.string.paywall_feature_champions, R.string.paywall_feature_champions_desc),
    PaywallFeature(R.string.activation_text_history, R.string.activation_desc_history),
    PaywallFeature(R.string.activation_text_3, R.string.activation_desc_3),
    PaywallFeature(R.string.activation_text_4, R.string.activation_desc_4),
    PaywallFeature(R.string.activation_text_5, R.string.activation_desc_5),
)

@Composable
private fun PaywallContent(
    uiState: ActivationUiState,
    onAction: (ActivationAction) -> Unit,
    showOneDay: Boolean,
    modifier: Modifier = Modifier,
) {
    val economyPercent = calculateEconomy(uiState.monthlyPrice, uiState.yearlyPrice)
    val yearlyPerMonth = calculatePerMonth(uiState.yearlyPrice)
    val selectedPlan = uiState.selectedPlan

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            PaywallHero(onAction = onAction)

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.activation_plan_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                PaywallPlanCard(
                    title = stringResource(R.string.activation_plan_2),
                    subtitle = yearlyPerMonth?.let { stringResource(R.string.paywall_per_month, it) }
                        ?: stringResource(R.string.activation_plan_desc_subscribe),
                    price = uiState.yearlyPrice,
                    period = stringResource(R.string.paywall_period_year),
                    badge = economyPercent?.let { stringResource(R.string.activation_plan_badge_save, it) }
                        ?: stringResource(R.string.paywall_badge_best),
                    badgeHighlighted = true,
                    selected = selectedPlan == ActivationPlan.Yearly,
                    onClick = { onAction(ActivationAction.OnActivationPlanItemClicked(ActivationPlan.Yearly)) },
                )
                PaywallPlanCard(
                    title = stringResource(R.string.activation_plan_3),
                    subtitle = stringResource(R.string.activation_plan_desc_lifetime),
                    price = uiState.unlimitedPrice,
                    period = stringResource(R.string.paywall_period_forever),
                    badge = stringResource(R.string.paywall_badge_one_time),
                    badgeHighlighted = false,
                    selected = selectedPlan == ActivationPlan.Unlimited,
                    onClick = { onAction(ActivationAction.OnActivationPlanItemClicked(ActivationPlan.Unlimited)) },
                )
                PaywallPlanCard(
                    title = stringResource(R.string.activation_plan_1),
                    subtitle = stringResource(R.string.activation_plan_desc_subscribe),
                    price = uiState.monthlyPrice,
                    period = stringResource(R.string.paywall_period_month),
                    selected = selectedPlan == ActivationPlan.Monthly,
                    onClick = { onAction(ActivationAction.OnActivationPlanItemClicked(ActivationPlan.Monthly)) },
                )
                if (showOneDay) {
                    PaywallPlanCard(
                        title = stringResource(R.string.activation_plan_4),
                        subtitle = stringResource(R.string.activation_plan_desc_lifetime),
                        price = uiState.onedayPrice,
                        period = stringResource(R.string.paywall_period_day),
                        selected = selectedPlan == ActivationPlan.OneDay,
                        onClick = { onAction(ActivationAction.OnActivationPlanItemClicked(ActivationPlan.OneDay)) },
                    )
                }

                Text(
                    text = stringResource(R.string.activation_text_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 12.dp),
                )
                PaywallFeaturesCard()
            }
        }

        PaywallBottomBar(
            selectedPlan = selectedPlan,
            price = when (selectedPlan) {
                ActivationPlan.Monthly -> uiState.monthlyPrice
                ActivationPlan.Yearly -> uiState.yearlyPrice
                ActivationPlan.Unlimited -> uiState.unlimitedPrice
                ActivationPlan.OneDay -> uiState.onedayPrice
            },
            onAction = onAction,
        )
    }
}

@Composable
private fun PaywallHero(onAction: (ActivationAction) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PaywallGreenDark, PaywallGreen),
                )
            )
            .padding(top = 8.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .clickable { onAction(ActivationAction.OnBackClicked) },
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(width = 2.dp, color = Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(20.dp)),
            )
            Text(
                text = stringResource(R.string.paywall_title),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.paywall_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PaywallPlanCard(
    title: String,
    subtitle: String,
    price: String?,
    period: String,
    selected: Boolean,
    onClick: () -> Unit,
    badge: String? = null,
    badgeHighlighted: Boolean = false,
) {
    val shape = RoundedCornerShape(16.dp)
    val borderColor = if (selected) PaywallGreen else MaterialTheme.colorScheme.surfaceVariant

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (badge != null) 10.dp else 0.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface)
                .border(width = if (selected) 2.dp else 1.dp, color = borderColor, shape = shape)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            PaywallRadio(selected = selected)
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) PaywallGreen else MaterialTheme.colorScheme.outline,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = price ?: "—",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = period,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }

        if (badge != null) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (badgeHighlighted) Color.Black else Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (badgeHighlighted) PaywallGold else PaywallGreen)
                    .padding(horizontal = 10.dp, vertical = 3.dp),
            )
        }
    }
}

@Composable
private fun PaywallRadio(selected: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(if (selected) PaywallGreen else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (selected) PaywallGreen else MaterialTheme.colorScheme.outline,
                shape = CircleShape,
            ),
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun PaywallFeaturesCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface),
    ) {
        paywallFeatures.forEachIndexed { index, feature ->
            if (index > 0) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(start = 52.dp),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(24.dp)
                        .background(color = PaywallGreen.copy(alpha = 0.12f), shape = CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = PaywallGreen,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(feature.titleRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(feature.descRes),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaywallBottomBar(
    selectedPlan: ActivationPlan,
    price: String?,
    onAction: (ActivationAction) -> Unit,
) {
    val isSubscription = selectedPlan == ActivationPlan.Monthly || selectedPlan == ActivationPlan.Yearly
    val buttonText = stringResource(R.string.activation_button).let { activate ->
        if (price != null) "$activate · $price" else activate
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Button(
                onClick = { onAction(ActivationAction.BuyButtonClicked) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PaywallGreen,
                    contentColor = Color.White,
                ),
                contentPadding = PaddingValues(vertical = 16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = stringResource(
                    if (isSubscription) R.string.paywall_note_subscription else R.string.paywall_note_one_time
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
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
        if (uiState.billingType == BillingType.Lifetime) {
            Text(
                text = stringResource(R.string.activation_lifetime_description),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
            )
        }
        if (uiState.billingType == BillingType.OneDay) {
            OnedayRemainingView(
                expirationDate = uiState.onedayExpirationDate,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
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
private fun OnedayRemainingView(expirationDate: Long?, modifier: Modifier = Modifier) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            now = System.currentTimeMillis()
        }
    }

    val remaining = if (expirationDate != null) maxOf(0L, expirationDate - now) else 0L
    val hours = (remaining / 3_600_000L).toInt()
    val minutes = ((remaining % 3_600_000L) / 60_000L).toInt()
    val hAbbr = stringResource(R.string.activation_oneday_hours_abbr)
    val minAbbr = stringResource(R.string.activation_oneday_minutes_abbr)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.activation_oneday_time_remaining),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "$hours $hAbbr $minutes $minAbbr",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displayLarge,
        )
        Text(
            text = stringResource(R.string.activation_oneday_not_subscription),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp),
        )
    }
}

private fun extractNumericValue(price: String?): Double? {
    if (price.isNullOrBlank()) return null
    val noSpaces = price.replace(" ", "").replace("\u00A0", "")
    val numericStr = noSpaces.filter { it.isDigit() || it == '.' || it == ',' }
    if (numericStr.isEmpty()) return null
    val dotCount = numericStr.count { it == '.' }
    val commaCount = numericStr.count { it == ',' }
    return when {
        dotCount == 0 && commaCount == 0 -> numericStr.toDoubleOrNull()
        dotCount == 1 && commaCount == 0 -> numericStr.toDoubleOrNull()
        dotCount == 0 && commaCount == 1 -> numericStr.replace(',', '.').toDoubleOrNull()
        dotCount > 1 -> numericStr.replace(".", "").replace(",", ".").toDoubleOrNull()
        commaCount > 1 -> numericStr.replace(",", "").toDoubleOrNull()
        else -> {
            val lastDot = numericStr.lastIndexOf('.')
            val lastComma = numericStr.lastIndexOf(',')
            if (lastDot > lastComma) numericStr.replace(",", "").toDoubleOrNull()
            else numericStr.replace(".", "").replace(",", ".").toDoubleOrNull()
        }
    }
}

private fun calculateEconomy(monthlyPrice: String?, yearlyPrice: String?): Int? {
    val monthly = extractNumericValue(monthlyPrice) ?: return null
    val yearly = extractNumericValue(yearlyPrice) ?: return null
    if (monthly <= 0) return null
    val monthlyPerYear = monthly * 12
    if (monthlyPerYear <= yearly) return null
    return ((monthlyPerYear - yearly) / monthlyPerYear * 100).toInt()
}

private fun openGooglePlaySubscriptions(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/account/subscriptions".toUri()))
    } catch (_: Exception) {
        Toast.makeText(context, context.getString(R.string.google_play_not_installed), Toast.LENGTH_LONG).show()
    }
}
private fun calculatePerMonth(yearlyPrice: String?): String? {
    val yearly = extractNumericValue(yearlyPrice) ?: return null
    if (yearly <= 0) return null
    return formatLikePrice(yearlyPrice ?: return null, yearly / 12)
}

private fun formatLikePrice(template: String, value: Double): String? {
    val match = Regex("\\d[\\d\\s\u00A0\u202F.,]*\\d|\\d").find(template) ?: return null
    val numberPart = match.value
    val hasDecimals = Regex("[.,]\\d{2}$").containsMatchIn(numberPart)
    val decimalSeparator = if (hasDecimals) numberPart[numberPart.length - 3] else null
    val groupingSeparator = numberPart.firstOrNull { !it.isDigit() && it != decimalSeparator }
    val rounded = if (hasDecimals) Math.round(value * 100) / 100.0 else Math.round(value).toDouble()
    val integerPart = rounded.toLong().toString()
    val groupedInteger = if (groupingSeparator != null) {
        integerPart.reversed().chunked(3).joinToString(groupingSeparator.toString()).reversed()
    } else {
        integerPart
    }
    val formatted = if (hasDecimals && decimalSeparator != null) {
        val cents = Math.round((rounded - rounded.toLong()) * 100).toInt()
        "$groupedInteger$decimalSeparator${cents.toString().padStart(2, '0')}"
    } else {
        groupedInteger
    }
    return template.replaceRange(match.range, formatted)
}
