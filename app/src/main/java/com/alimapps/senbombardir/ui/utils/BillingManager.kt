package com.alimapps.senbombardir.ui.utils

import android.app.Activity
import android.content.Context
import com.revenuecat.purchases.ProductType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getProductsWith
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.purchaseWith

private const val REVENUECAT_API_KEY = "goog_cUzqpjmnAOPcoKJDcaILIbIQAbw"
private const val ENTITLEMENT_ID = "dop_tep_pro"

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
        onResult: (List<StoreProduct>) -> Unit,
    ) {
        Purchases.sharedInstance.getProductsWith(
            productIds = productIds,
            type = type,
            onGetStoreProducts = { products -> onResult(products) },
        )
    }

    fun launchPurchase(activity: Activity, storeProduct: StoreProduct) {
        Purchases.sharedInstance.purchaseWith(
            purchaseParams = PurchaseParams.Builder(activity, storeProduct).build(),
            onSuccess = { _, _ -> listener.onPurchaseSuccess() },
        )
    }

    fun checkPurchase(productIds: List<String>, type: ProductType) {
        Purchases.sharedInstance.getCustomerInfoWith { customerInfo ->
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
    }

    interface BillingUpdatesListener {
        fun onPurchaseSuccess()
        fun onPurchaseCheckSuccess(productType: ProductType)
        fun onPurchaseCheckNoPurchase(productType: ProductType)
    }
}