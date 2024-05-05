package com.example.applepie.model

import com.google.firebase.database.PropertyName

data class Task(
    val description: String = "",
    val due_datetime: String = "",
    val id_list: String = "",
    val id_task: String = "",
    @get:PropertyName("isDone") var isDone: Boolean = false,
    val link: String = "",
    val priority: String = "",
    val title: String = "",
    var reminder: Int = 0
)
