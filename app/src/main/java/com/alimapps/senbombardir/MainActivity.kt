package com.alimapps.senbombardir

import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alimapps.senbombardir.data.repository.BillingRepository
import com.alimapps.senbombardir.data.repository.LanguageRepository
import com.alimapps.senbombardir.domain.model.ActivationPlan
import com.alimapps.senbombardir.ui.navigation.AppNavigation
import com.alimapps.senbombardir.domain.model.AppLanguage
import com.alimapps.senbombardir.domain.model.BillingType
import com.alimapps.senbombardir.ui.theme.AppTheme
import com.alimapps.senbombardir.ui.utils.BillingManager
import com.android.billingclient.api.BillingClient
import org.koin.android.ext.android.inject
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val languageRepository: LanguageRepository by inject()
    private val billingRepository: BillingRepository by inject()

    private var billingManager: BillingManager? = null
    private var selectedActivationPlan: ActivationPlan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val selectedLanguage = languageRepository.getSelectedLanguage()
        selectedLanguage?.let { setLocale(this, it) }
        checkPurchase()
        setContent {
            AppTheme {
                AppNavigation(
                    showLanguage = selectedLanguage == null,
                    onLanguageSelected = { appLanguage ->
                        languageRepository.saveLanguage(appLanguage)
                        setLocale(this, appLanguage)
                    },
                    onActivationPlanSelected = { activationPlan ->
                        selectedActivationPlan = activationPlan
                        startPurchase(activationPlan)
                    },
                )
            }
        }
    }

    private fun startPurchase(activationPlan: ActivationPlan) {
        initBillingManager()
        billingManager?.startConnection(
            onConnected = {
                billingManager?.queryProducts(
                    productIds = listOf(activationPlan.productId),
                    type = if (activationPlan == ActivationPlan.Unlimited) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS,
                    onResult = { products ->
                        if (products.isNotEmpty()) {
                            billingManager?.launchPurchase(this, products.first())
                        }
                    }
                )
            }
        )
    }

    private fun checkPurchase() {
        initBillingManager()
        billingManager?.startConnection(
            onConnected = {
                val billingType = billingRepository.getCurrentBillingType()

                if (billingType == BillingType.Lifetime || billingType == BillingType.Limited) {
                    billingManager?.queryProducts(
                        productIds = listOf(ActivationPlan.Unlimited.productId),
                        type = BillingClient.ProductType.INAPP,
                        onResult = { products ->
                            val price = products.firstOrNull()?.oneTimePurchaseOfferDetails?.formattedPrice
                            billingRepository.setUnlimitedPrice(price)
                            billingManager?.checkPurchase(BillingClient.ProductType.INAPP)
                        }
                    )
                }

                if (billingType == BillingType.Subscribe || billingType == BillingType.Limited) {
                    billingManager?.queryProducts(
                        productIds = listOf(ActivationPlan.Monthly.productId, ActivationPlan.Yearly.productId),
                        type = BillingClient.ProductType.SUBS,
                        onResult = { products ->
                            val monthlyPrice =
                                products.getOrNull(0)?.subscriptionOfferDetails?.firstOrNull()
                                    ?.pricingPhases?.pricingPhaseList?.firstOrNull()
                                    ?.formattedPrice

                            val yearlyPrice =
                                products.getOrNull(1)?.subscriptionOfferDetails?.firstOrNull()
                                    ?.pricingPhases?.pricingPhaseList?.firstOrNull()
                                    ?.formattedPrice

                            billingRepository.setMonthlyPrice(monthlyPrice)
                            billingRepository.setYearlyPrice(yearlyPrice)

                            billingManager?.checkPurchase(BillingClient.ProductType.SUBS)
                        }
                    )
                }
            }
        )
    }

    private fun initBillingManager() {
        if (billingManager == null) {
            billingManager = BillingManager(this, object : BillingManager.BillingUpdatesListener {
                override fun onPurchaseSuccess() {
                    println("###onPurchaseSuccess = $selectedActivationPlan")
                    when (selectedActivationPlan) {
                        ActivationPlan.Monthly,
                        ActivationPlan.Yearly -> billingRepository.setCurrentBillingType(BillingType.Subscribe)
                        ActivationPlan.Unlimited -> billingRepository.setCurrentBillingType(BillingType.Lifetime)
                        else -> Unit
                    }
                    restartApp()
                }

                override fun onPurchaseCheckSuccess(productType: String) {
                    println("###onPurchaseCheckSuccess = $productType")
                    when (productType) {
                        BillingClient.ProductType.INAPP -> billingRepository.setCurrentBillingType(BillingType.Lifetime)
                        BillingClient.ProductType.SUBS -> billingRepository.setCurrentBillingType(BillingType.Subscribe)
                    }
                }

                override fun onPurchaseCheckNoPurchase(productType: String) {
                    println("###onPurchaseCheckNoPurchase = $productType")
                    when (productType) {
                        BillingClient.ProductType.INAPP -> {
                            val billingType = billingRepository.getCurrentBillingType()
                            if (billingType == BillingType.Lifetime) {
                                billingRepository.setCurrentBillingType(BillingType.Limited)
                            }
                        }
                        BillingClient.ProductType.SUBS -> {
                            val billingType = billingRepository.getCurrentBillingType()
                            if (billingType == BillingType.Subscribe) {
                                billingRepository.setCurrentBillingType(BillingType.Limited)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun setLocale(context: Context, language: AppLanguage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            localeManager.applicationLocales = LocaleList.forLanguageTags(language.code)
        } else {
            setLocaleLegacy(context, language)
        }
    }

    @Suppress("DEPRECATION")
    private fun setLocaleLegacy(context: Context, language: AppLanguage) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }

    private fun restartApp() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    override fun onDestroy() {
        billingManager?.endConnection()
        billingManager = null
        super.onDestroy()
    }
}