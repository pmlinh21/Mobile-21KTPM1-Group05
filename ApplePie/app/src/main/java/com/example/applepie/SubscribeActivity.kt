package com.example.applepie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class SubscribeActivity : AppCompatActivity() {
    private lateinit var closeButton: Button
    private lateinit var goPremiumButton: Button

    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe)

        val intent = intent

         billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        setupUI()
        handleEventListener()
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
            Toast.makeText(this, "Billing unavailable", Toast.LENGTH_SHORT).show()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Toast.makeText(this, "Item already owned", Toast.LENGTH_SHORT).show()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
            Toast.makeText(this, "Developer error", Toast.LENGTH_SHORT).show()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.NETWORK_ERROR) {
            Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Service disconnected", Toast.LENGTH_SHORT).show()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "Billing unavailable", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState === Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener)

            }
        }
    }

    private val acknowledgePurchaseResponseListener: AcknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener { billingResult ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Toast.makeText(this, "Purchase acknowledged", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Purchase not acknowledged", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        closeButton = findViewById<Button>(R.id.close_button)
        goPremiumButton = findViewById<Button>(R.id.go_premium_button)
    }

    private fun handleEventListener() {
        closeButton.setOnClickListener {
            finish()
        }
        goPremiumButton.setOnClickListener {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                        val queryProductDetailsParams =
                            QueryProductDetailsParams.newBuilder()
                                .setProductList(
                                    ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                            .setProductId("android.test.purchased")
                                            .setProductType(BillingClient.ProductType.INAPP)
                                            .build()))
                                .build()

                        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                                billingResult,
                                productDetailsList ->
                            for (productDetails in productDetailsList) {
                                val productDetailsParamsList = listOf(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                        .setProductDetails(productDetails)
                                        .build()
                                )
                                val billingFlowParams = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(productDetailsParamsList)
                                    .build()

                                // Launch the billing flow
                                billingClient.launchBillingFlow(this@SubscribeActivity, billingFlowParams)
                            }
                        }
                    }
                }
                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            })
        }
    }
}
