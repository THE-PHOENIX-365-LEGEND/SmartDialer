package dev.phoenix365.smartdialer.controller

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OpenSourcePurchaseHelper : PurchaseHelper {
    private val _isPro = MutableStateFlow(true)
    override val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    private val _proCheckDone = MutableStateFlow(true)
    override val proCheckDone: StateFlow<Boolean> = _proCheckDone.asStateFlow()

    private val _purchaseSuccess = MutableStateFlow(false)
    override val purchaseSuccess: StateFlow<Boolean> = _purchaseSuccess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isBillingReady = MutableStateFlow(false)
    override val isBillingReady: StateFlow<Boolean> = _isBillingReady.asStateFlow()

    private val _iapSkuDetailsInitialized = MutableStateFlow(false)
    override val iapSkuDetailsInitialized: StateFlow<Boolean> = _iapSkuDetailsInitialized.asStateFlow()

    private val _subSkuDetailsInitialized = MutableStateFlow(false)
    override val subSkuDetailsInitialized: StateFlow<Boolean> = _subSkuDetailsInitialized.asStateFlow()

    override fun initBilling() {}
    override fun checkProStatus() {}
    override fun setProStatusImmediate(isPro: Boolean) {}
    override fun clearErrors() {}
    override fun clearPurchaseSuccess() {}
    override fun getPriceDonation(product: String): String = ""
    override fun getPriceSubscription(product: String, planId: String?): String = ""
    override fun isIapPurchased(product: String): Boolean = true
    override fun isSubPurchased(product: String): Boolean = true
    override fun purchaseDonation(product: String, activity: Activity) {}
    override fun purchaseSubscription(product: String, activity: Activity, planId: String?) {}
    override fun restorePurchases() {}
    override fun refreshAllData() {}
    override fun clearProPrefsIfNeeded() {}
    override fun loadProducts(productIds: List<String>) {}
    override fun handleNewIntent(intent: Intent?, activity: ComponentActivity) {}
}
