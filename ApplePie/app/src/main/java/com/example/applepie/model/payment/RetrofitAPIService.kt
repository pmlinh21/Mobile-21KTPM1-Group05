package com.example.applepie.model.payment

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitAPIService {

    @POST("v2/checkout/orders")
    fun createOrder(
        @Header("Content-Type") contentType: String,
        @Header("PayPal-Request-Id") payPalRequestId: String,
        @Header("Authorization") authorization: String,
        @Body orderRequest: OrderIdRequest
    ): Call<OrderIdResponse>

    @POST("v2/checkout/orders/{order_id}/capture")
    fun captureOrder(
        @Header("Content-Type") contentType: String,
        @Header("PayPal-Request-Id") payPalRequestId: String,
        @Header("Authorization") authorization: String,
        @Path("order_id") orderId: String
    ): Call<OrderCaptureResponse>
}