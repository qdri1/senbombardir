package com.alimapps.senbombardir.ui.utils

import android.app.Activity
import android.content.Context
import com.revenuecat.purchases.ProductType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesErrorCode
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.restorePurchasesWith
import com.revenuecat.purchases.getProductsWith
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.purchaseWith

private const val REVENUECAT_API_KEY = "goog_cUzqpjmnAOPcoKJDcaILIbIQAbw"
private const val ENTITLEMENT_ID = "dop_tep_pro"
const val ONE_DAY_ACCESS_MILLIS = 24 * 60 * 60 * 1000L

class BillingManager(
    context: Context,
    private val listener: BillingUpdatesListener
) {

    init {
        if (Purchases.isConfigured.not()) {
            Purchases.configure(PurchasesConfiguration.Builder(context, REVENUECAT_API_KEY).build())
        }
    }

    fun queryProducts(
        productIds: List<String>,
        type: ProductType,
        onError: () -> Unit = {},
        onResult: (List<StoreProduct>) -> Unit,
    ) {
        Purchases.sharedInstance.getProductsWith(
            productIds = productIds,
            type = type,
            onError = { onError() },
            onGetStoreProducts = { products -> onResult(products) },
        )
    }

    fun launchPurchase(activity: Activity, storeProduct: StoreProduct) {
        Purchases.sharedInstance.purchaseWith(
            purchaseParams = PurchaseParams.Builder(activity, storeProduct).build(),
            onError = { error, userCancelled ->
                when {
                    userCancelled -> listener.onPurchaseCancelled()
                    error.code == PurchasesErrorCode.PaymentPendingError -> listener.onPurchasePending()
                    else -> listener.onPurchaseFailed()
                }
            },
            onSuccess = { transaction, _ -> listener.onPurchaseSuccess(purchaseTime = transaction?.purchaseTime ?: 0L) },
        )
    }

    fun checkPurchase(productIds: List<String>, type: ProductType) {
        Purchases.sharedInstance.getCustomerInfoWith { customerInfo ->
            evaluatePurchase(customerInfo, productIds, type)
        }
    }

    fun checkOneDayPurchase(productId: String) {
        Purchases.sharedInstance.getCustomerInfoWith { customerInfo ->
            evaluateOneDayPurchase(customerInfo, productId)
        }
    }

    fun restorePurchases(
        subscriptionIds: List<String>,
        lifetimeIds: List<String>,
        oneDayId: String,
        onResult: (success: Boolean) -> Unit,
    ) {
        Purchases.sharedInstance.restorePurchasesWith(
            onError = { onResult(false) },
            onSuccess = { customerInfo ->
                evaluatePurchase(customerInfo, subscriptionIds, ProductType.SUBS)
                evaluatePurchase(customerInfo, lifetimeIds, ProductType.INAPP)
                evaluateOneDayPurchase(customerInfo, oneDayId)
                onResult(true)
            },
        )
    }

    private fun evaluatePurchase(customerInfo: CustomerInfo, productIds: List<String>, type: ProductType) {
        val isPurchased = when (type) {
            ProductType.SUBS -> customerInfo.activeSubscriptions.any { activeId ->
                productIds.any { activeId == it || activeId.startsWith("$it:") }
            }
            ProductType.INAPP -> {
                val hasLifetimeTransaction = customerInfo.nonSubscriptionTransactions.any { it.productIdentifier in productIds }
                val isEntitlementActive = customerInfo.activeSubscriptions.isEmpty() && customerInfo.entitlements[ENTITLEMENT_ID]?.isActive == true

                hasLifetimeTransaction && isEntitlementActive
            }
            ProductType.UNKNOWN -> false
        }
        if (isPurchased) {
            listener.onPurchaseCheckSuccess(type)
        } else {
            listener.onPurchaseCheckNoPurchase(type)
        }
    }

    private fun evaluateOneDayPurchase(customerInfo: CustomerInfo, productId: String) {
        val latestPurchaseTime = customerInfo.nonSubscriptionTransactions
            .filter { it.productIdentifier == productId }
            .maxOfOrNull { it.purchaseDate.time }
            ?: return
        if (latestPurchaseTime + ONE_DAY_ACCESS_MILLIS > System.currentTimeMillis()) {
            listener.onOneDayPurchaseCheckSuccess(purchaseTime = latestPurchaseTime)
        }
    }

    interface BillingUpdatesListener {
        fun onPurchaseSuccess(purchaseTime: Long)
        fun onPurchasePending()
        fun onPurchaseCancelled()
        fun onPurchaseFailed()
        fun onOneDayPurchaseCheckSuccess(purchaseTime: Long)
        fun onPurchaseCheckSuccess(productType: ProductType)
        fun onPurchaseCheckNoPurchase(productType: ProductType)
    }
}