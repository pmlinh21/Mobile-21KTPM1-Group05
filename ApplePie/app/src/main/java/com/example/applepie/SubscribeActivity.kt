package com.example.applepie

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.applepie.model.payment.Amount
import com.example.applepie.model.payment.CaptureOrderCallback
import com.example.applepie.model.payment.ExperienceContext
import com.example.applepie.model.payment.OrderCaptureResponse
import com.example.applepie.model.payment.OrderIdRequest
import com.example.applepie.model.payment.OrderIdResponse
import com.example.applepie.model.payment.PayPal
import com.example.applepie.model.payment.PaymentSource
import com.example.applepie.model.payment.PurchaseUnit
import com.example.applepie.model.payment.RequestOrderIdCallback
import com.example.applepie.model.payment.RetrofitAPIService
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.PayPalSDKError
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutClient
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutListener
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutRequest
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

class SubscribeActivity : AppCompatActivity() {
    private lateinit var closeButton: Button
    private lateinit var goPremiumButton: Button

    private val CLIENT_ID = "AQwgXzcJ7bz-R12JXRooKiznHGtLI2hU2_nVD7VShEmEooErrCdckWfqFuX53NmkLnD05NnUVSQTVLXu"
    private val CLIENT_SECRET = "EHflaaZL9eQs6GAZskU8AlIL8QvVe0BBRO3UOmYX-zb5Dswci6mG3MWCeAFCovLtatpyTvusrY7fDVX_"
    private val RETURN_URL = "com.example.applepie://paypalpay"
    private val BASE_URL = "https://api-m.sandbox.paypal.com"

    private var order_id: String? = null
    private var paypal_request_id: String? = null

    private lateinit var payPalNativeClient: PayPalNativeCheckoutClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe)

        val intent = intent

        val coreConfig = CoreConfig(CLIENT_ID)
         payPalNativeClient = PayPalNativeCheckoutClient(
            application = application,
            coreConfig = coreConfig,
            returnUrl = RETURN_URL
        )

        setupUI()
        setupPaymentListener()
        handleEventListener()
    }

    private fun setupUI() {
        closeButton = findViewById<Button>(R.id.close_button)
        goPremiumButton = findViewById<Button>(R.id.go_premium_button)
    }

    private fun setupPaymentListener() {
        payPalNativeClient.listener = object : PayPalNativeCheckoutListener {
            override fun onPayPalCheckoutStart() {
                // the PayPal paysheet is about to show up
                Log.d("PayPal", "PayPal checkout started")
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onPayPalCheckoutSuccess(result: PayPalNativeCheckoutResult) {
                // order was approved and is ready to be captured/authorized
                captureOrder(object : CaptureOrderCallback {
                    override fun onCaptureOrderReceived(status: String?) {
                        Log.d("PayPal", "Order status: $status")
                        Toast.makeText(applicationContext, "Payment successful", Toast.LENGTH_SHORT).show()
                    }
                    override fun onCaptureOrderError(error: String?) {
                        Log.e("PayPal", "Error: $error")
                        Toast.makeText(applicationContext, "Payment failed", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            override fun onPayPalCheckoutFailure(error: PayPalSDKError) {
                // handle the error
                Log.e("PayPal", "Error: ${error.message}")
                Toast.makeText(applicationContext, "Payment failed", Toast.LENGTH_SHORT).show()
            }
            override fun onPayPalCheckoutCanceled() {
                // the user canceled the flow
                Log.d("PayPal", "User canceled the flow")
                Toast.makeText(applicationContext, "Payment canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getOrderID(callback: RequestOrderIdCallback): String? {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPIService::class.java)

        val orderRequest = OrderIdRequest(
            intent = "CAPTURE",
            purchase_units = listOf(
                PurchaseUnit(
                    reference_id = "apple-pie-price",
                    amount = Amount("USD", "9.99")
                )
            ),
            payment_source = PaymentSource(
                paypal = PayPal(
                    experience_context = ExperienceContext(
                        payment_method_preference = "IMMEDIATE_PAYMENT_REQUIRED",
                        brand_name = "Test",
                        locale = "en-US",
                        landing_page = "LOGIN",
                        shipping_preference = "NO_SHIPPING",
                        user_action = "PAY_NOW",
                        return_url = RETURN_URL,
                        cancel_url = "https://example.com/cancelUrl"
                    )
                )
            )
        )
        val contentType = "application/json"
        val payPalRequestId = paypal_request_id!!
        val encodedCredentials = Base64.getEncoder().encodeToString("$CLIENT_ID:$CLIENT_SECRET".toByteArray())
        val authorization = "Basic $encodedCredentials"

        var orderId: String? = null
        retrofit.createOrder(contentType, payPalRequestId, authorization, orderRequest).enqueue(object :
            Callback<OrderIdResponse> {
            override fun onResponse(call: Call<OrderIdResponse>, response: Response<OrderIdResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    orderId = data?.id
                    callback.onRequestOrderIdReceived(orderId)
                } else {
                    callback.onRequestOrderIdError("Unsuccessful: ${response.code()} ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<OrderIdResponse>, t: Throwable) {
                callback.onRequestOrderIdError("Failure: ${t.message}")
            }
        })
        return orderId
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun captureOrder(callback: CaptureOrderCallback) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPIService::class.java)

        val contentType = "application/json"
        val payPalRequestId = paypal_request_id!!
//        val authorization = "Bearer $access_token"
        val encodedCredentials = Base64.getEncoder().encodeToString("$CLIENT_ID:$CLIENT_SECRET".toByteArray())
        val authorization = "Basic $encodedCredentials"
        val orderId = order_id!!

        retrofit.captureOrder(contentType, payPalRequestId, authorization, orderId).enqueue(object :
            Callback<OrderCaptureResponse> {
            override fun onResponse(call: Call<OrderCaptureResponse>, response: Response<OrderCaptureResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    callback.onCaptureOrderReceived("Status: ${data?.status}")
                } else {
                    callback.onCaptureOrderError("Unsuccessful: ${response.code()} ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<OrderCaptureResponse>, t: Throwable) {
                callback.onCaptureOrderError("Failure: ${t.message}")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleEventListener() {
        closeButton.setOnClickListener {
            finish()
        }
        goPremiumButton.setOnClickListener {
            paypal_request_id = "apple-pie" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
            getOrderID(object : RequestOrderIdCallback {
                override fun onRequestOrderIdReceived(orderId: String?) {
                    Log.d("Request Order ID", "Order ID: $orderId")
                    order_id = orderId

                    val request = PayPalNativeCheckoutRequest(orderId!!)
                    payPalNativeClient.startCheckout(request)
                }
                override fun onRequestOrderIdError(error: String?) {
                    Log.e("Request Order ID", "$error")
                }
            })
        }
    }
}
