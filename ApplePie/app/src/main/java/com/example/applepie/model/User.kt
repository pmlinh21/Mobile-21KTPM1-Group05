package com.example.applepie.model

import com.google.firebase.database.PropertyName

// TODO: add current / longest streak into User
data class User(
    val current_streak: Int = 0,
    val email: String = "",
    val id_user: Int = 0,
    @get:PropertyName("isPremium") val isPremium: Boolean = false,
    val longest_streak: Int = 0,
    val password: String = "",
    val reminder_duration: Int = 0,
    val username: String = ""
)

