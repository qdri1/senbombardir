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

    fun isSecretActivated(): Boolean = prefs.isSecretActivated

    fun setSecretActivated(value: Boolean) { prefs.isSecretActivated = value }

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

    fun getOnedayPrice(): String? = prefs.onedayPrice

    fun setOnedayPrice(price: String?) {
        prefs.onedayPrice = price
    }

    fun getOnedayExpirationDate(): Long? {
        val value = prefs.onedayExpirationDate
        return if (value == 0L) null else value
    }

    fun setOnedayExpirationDate(value: Long) {
        prefs.onedayExpirationDate = value
    }

    fun hasValidOnedayAccess(): Boolean {
        val expiration = prefs.onedayExpirationDate
        return expiration > 0L && expiration > System.currentTimeMillis()
    }

    fun checkAndUpdateOnedayExpiration(): Boolean {
        if (getCurrentBillingType() == BillingType.OneDay && !hasValidOnedayAccess()) {
            prefs.onedayExpirationDate = 0L
            setCurrentBillingType(BillingType.Limited)
            return true
        }
        return false
    }
}