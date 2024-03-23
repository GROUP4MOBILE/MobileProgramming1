package com.example.todoproject.model

data class TodoModel(
    var id: Int,
    var status: Boolean,
    var task: String,
    val reminderTime: Long = 0,
    val iconResId: Int
)

