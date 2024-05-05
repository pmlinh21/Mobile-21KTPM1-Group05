package com.example.applepie.model.mail

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.mail.internet.InternetAddress

private val username = "ngo.ngoclien@outlook.com"
private val password = "Ngongoclien7423nnl"

fun sendEmail(toEmail: String, code: String) {
    // Send email confirmation
    val auth = EmailService.UserPassAuthenticator(username, password)
    val to = listOf(InternetAddress(toEmail))
    val from = InternetAddress(username)
    val email = EmailService.Email(auth, to, from, "[APPLE PIE] Account Confirmation", "Enter this code to confirm your account: $code\n\nIf you did not request this, please ignore this email.")
    val emailService = EmailService("smtp-mail.outlook.com", 587)

    GlobalScope.launch { // or however you do background threads
        emailService.send(email)
    }
}