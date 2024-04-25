package com.example.applepie.model

import com.google.firebase.database.PropertyName

data class Task(
    val description: String = "",
    val due_datetime: String = "",
    val id_list: Int = 0,
    val id_task: Int = 0,
    @get:PropertyName("isDone") val isDone: Boolean = false,
    val link: String = "",
    val priority: String = "",
    val title: String = "",
    var listName: String = ""
)
