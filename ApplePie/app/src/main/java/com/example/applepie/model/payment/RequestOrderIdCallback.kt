package com.example.applepie.model.payment

interface RequestOrderIdCallback {
    fun onRequestOrderIdReceived(orderId: String?)
    fun onRequestOrderIdError(error: String?)
}