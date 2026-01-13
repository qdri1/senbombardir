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

    fun getClearResultsRemainingCount(): Int {
        return prefs.clearResultsRemainingCount
    }

    fun decreaseClearResultsRemainingCount() {
        val clearResultsRemainingCount = prefs.clearResultsRemainingCount
        if (clearResultsRemainingCount > 0) {
            prefs.clearResultsRemainingCount = clearResultsRemainingCount - 1
        }
    }
}