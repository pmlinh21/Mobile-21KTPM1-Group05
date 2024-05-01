package com.example.applepie.model.payment

interface CaptureOrderCallback {
    fun onCaptureOrderReceived(status: String?)
    fun onCaptureOrderError(error: String?)
}