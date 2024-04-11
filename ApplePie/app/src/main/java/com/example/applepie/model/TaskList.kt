package com.example.applepie.model

import androidx.compose.ui.graphics.Color
import com.example.applepie.R

data class TaskList(
    val id_list: Int = 0,
    val list_color: Int = R.color.green,
    val list_icon: String = "",
    val list_name: String = "",
)
