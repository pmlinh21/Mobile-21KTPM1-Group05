package com.example.applepie.database

import com.google.firebase.database.PropertyName

// TODO: add current / longest streak into User
data class User(
    val email: String = "",
    val id_user: Int = 0,
    @get:PropertyName("isPremium") val isPremium: Boolean = false,
    val password: String = "",
    val reminder_duration: Int = 0,
    val username: String = ""
)

