package com.example.applepie.model.payment

data class Amount(
    val currency_code: String,
    val value: String
)

data class PurchaseUnit(
    val reference_id: String,
    val amount: Amount
)

data class ExperienceContext(
    val payment_method_preference: String,
    val brand_name: String,
    val locale: String,
    val landing_page: String,
    val shipping_preference: String,
    val user_action: String,
    val return_url: String,
    val cancel_url: String
)

data class PayPal(
    val experience_context: ExperienceContext
)

data class PaymentSource(
    val paypal: PayPal
)

data class OrderIdRequest(
    val intent: String,
    val purchase_units: List<PurchaseUnit>,
    val payment_source: PaymentSource
)

// ************************************************************
data class Link(
    val href: String,
    val rel: String,
    val method: String
)

data class OrderIdResponse(
    val id: String,
    val status: String,
    val payment_source: PaymentSource,
    val links: List<Link>,
    val create_time: String,
    val update_time: String,
    val processing_instruction: String
)

// ************************************************************
data class Name(
    val given_name: String,
    val surname: String
)

data class Payer(
    val name: Name,
    val email_address: String,
    val payer_id: String
)

data class OrderCaptureResponse(
    val id: String,
    val status: String,
    val payment_source: PaymentSource,
    val purchase_units: List<PurchaseUnit>,
    val payer: Payer,
    val links: List<Link>
)