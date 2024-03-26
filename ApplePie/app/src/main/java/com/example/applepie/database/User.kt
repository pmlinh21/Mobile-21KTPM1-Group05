package com.example.applepie.database

data class User(
    val email: String = "",
    val id_user: Int = 0,
    val is_premium: Boolean = false,
    val password: String = "",
    val reminder_duration: Int = 0,
    val username: String = "",
)
