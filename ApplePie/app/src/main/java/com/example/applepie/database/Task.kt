package com.example.applepie.database

data class Task(
    val description: String = "",
    val due_datetime: String = "",
    val id_list: Int = 0,
    val id_task: Int = 0,
    val is_done: Boolean = false,
    val link: String = "",
    val priority: String = "",
    val title: String = "",
)
