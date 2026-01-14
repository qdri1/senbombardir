package com.alimapps.senbombardir.ui.utils

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

class BillingManager(
    context: Context,
    private val listener: BillingUpdatesListener
) : PurchasesUpdatedListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.find { it.purchaseState == Purchase.PurchaseState.PURCHASED }?.let { purchase ->
                if (purchase.isAcknowledged.not()) {
                    val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                    billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            listener.onPurchaseSuccess()
                        }
                    }
                }
            }
        }
    }

    fun startConnection(onConnected: (() -> Unit)) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    onConnected()
                }
            }
            override fun onBillingServiceDisconnected() {}
        })
    }

    fun endConnection() {
        billingClient.endConnection()
    }

    fun queryProducts(
        productIds: List<String>,
        type: String,
        onResult: (List<ProductDetails>) -> Unit,
    ) {
        val productList = productIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(type)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                onResult(productDetailsList.productDetailsList)
            } else {
                onResult(emptyList())
            }
        }
    }

    fun launchPurchase(activity: Activity, productDetails: ProductDetails) {
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .apply { if (offerToken != null) setOfferToken(offerToken) }
                .build()
        )

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, params)
    }

    fun checkPurchase() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.find { it.purchaseState == Purchase.PurchaseState.PURCHASED }?.let { purchase ->
                    if (purchase.isAcknowledged) {
                        listener.onPurchaseCheckSuccess()
                    } else {
                        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()

                        billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                listener.onPurchaseCheckSuccess()
                            }
                        }
                    }
                }
            }
        }
    }

    interface BillingUpdatesListener {
        fun onPurchaseSuccess()
        fun onPurchaseCheckSuccess()
    }
}