package com.alimapps.senbombardir.data.repository

import com.alimapps.senbombardir.data.source.Prefs
import com.alimapps.senbombardir.domain.model.BillingType

class BillingRepository(
    private val prefs: Prefs,
) {

    fun getCurrentBillingType(): BillingType {
        val billingType = prefs.billingType

        return BillingType.entries.find {
            it.name == billingType
        } ?: BillingType.Limited
    }
    
    fun setCurrentBillingType(billingType: BillingType) {
        prefs.billingType = billingType.name
    }

    fun getMonthlyPrice(): String? {
        return prefs.monthlyPrice
    }

    fun setMonthlyPrice(price: String?) {
        prefs.monthlyPrice = price
    }

    fun getYearlyPrice(): String? {
        return prefs.yearlyPrice
    }

    fun setYearlyPrice(price: String?) {
        prefs.yearlyPrice = price
    }

    fun getUnlimitedPrice(): String? {
        return prefs.unlimitedPrice
    }

    fun setUnlimitedPrice(price: String?) {
        prefs.unlimitedPrice = price
    }
}