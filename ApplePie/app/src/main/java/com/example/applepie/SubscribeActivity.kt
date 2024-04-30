package com.example.applepie

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.PayPalSDKError
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutClient
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutListener
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutResult
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class SubscribeActivity : AppCompatActivity() {
    private lateinit var closeButton: Button
    private lateinit var goPremiumButton: Button

    private lateinit var billingClient: BillingClient

//    private val CLIENT_ID = "AQwgXzcJ7bz-R12JXRooKiznHGtLI2hU2_nVD7VShEmEooErrCdckWfqFuX53NmkLnD05NnUVSQTVLXu"
//    private val RETURN_URL = "com.example.applepie://paypalpay"
//
//    private val coreConfig = CoreConfig(CLIENT_ID)
//    private val payPalNativeClient = PayPalNativeCheckoutClient(
//        application = this.application,
//        coreConfig = coreConfig,
//        returnUrl = RETURN_URL
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe)

        val intent = intent

         billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

//        setupPaymentListener()
        setupUI()
        handleEventListener()
    }

    private fun setupPaymentListener() {
//        payPalNativeClient.listener = object : PayPalNativeCheckoutListener {
//            override fun onPayPalCheckoutStart() {
//                // the PayPal paysheet is about to show up
//            }
//            override fun onPayPalCheckoutSuccess(result: PayPalNativeCheckoutResult) {
//                // order was approved and is ready to be captured/authorized
//            }
//            override fun onPayPalCheckoutFailure(error: PayPalSDKError) {
//                // handle the error
//            }
//            override fun onPayPalCheckoutCanceled() {
//                // the user canceled the flow
//            }
//        }
    }

//    private fun getOrderId(): String {
//        val url = URL("https://api-m.sandbox.paypal.com/v2/checkout/orders")
//        val httpConn = url.openConnection() as HttpURLConnection
//        httpConn.requestMethod = "POST"
//
//        httpConn.setRequestProperty("Content-Type", "application/json")
//        httpConn.setRequestProperty("PayPal-Request-Id", "7b92603e-77ed-4896-8e78-5dea2050476a")
//        httpConn.setRequestProperty(
//            "Authorization",
//            "Bearer 6V7rbVwmlM1gFZKW_8QtzWXqpcwQ6T5vhEGYNJDAAdn3paCgRpdeMdVYmWzgbKSsECednupJ3Zx5Xd-g"
//        )
//
//        httpConn.doOutput = true
//        val writer = OutputStreamWriter(httpConn.outputStream)
//        writer.write("{ " +
//                "\"intent\": \"CAPTURE\", " +
//                "\"purchase_units\": " +
//                    "[ { " +
//                    "\"reference_id\": \"d9f80740-38f0-11e8-b467-0ed5f89f718b\", " +
//                    "\"amount\": { " +
//                        "\"currency_code\": \"USD\", " +
//                        "\"value\": \"100.00\" } " +
//                    "} ], " +
//                "\"payment_source\": { " +
//                    "\"paypal\": { " +
//                        "\"experience_context\": { " +
//                            "\"payment_method_preference\": \"IMMEDIATE_PAYMENT_REQUIRED\", " +
//                            "\"brand_name\": \"APPLE PIE\", " +
//                            "\"locale\": \"en-US\", " +
//                            "\"landing_page\": \"LOGIN\", " +
//                            "\"shipping_preference\": \"SET_PROVIDED_ADDRESS\", " +
//                            "\"user_action\": \"PAY_NOW\", " +
//                            "\"return_url\": \"com.example.applepie://paypalpay\", " +
//                            "\"cancel_url\": \"https://example.com/cancelUrl\" } " +
//                "} } }")
//        writer.flush()
//        writer.close()
//        httpConn.outputStream.close()
//
//        val responseStream =
//            if (httpConn.responseCode / 100 == 2) httpConn.inputStream else httpConn.errorStream
//        val s = Scanner(responseStream).useDelimiter("\\A")
//        val response = if (s.hasNext()) s.next() else ""
//
//        return response
//    }


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

//            Log.d("SubscribeActivity", "Go Premium button clicked")
//            val orderId = getOrderId()
//            Log.d("SubscribeActivity", orderId)
//            Toast.makeText(this, orderId, Toast.LENGTH_SHORT).show()
////            val request = PayPalNativeCheckoutRequest(""
////            payPalNativeClient.startCheckout(request)
        }
    }
}
