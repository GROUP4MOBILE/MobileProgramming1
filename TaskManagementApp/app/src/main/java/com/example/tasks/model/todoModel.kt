package com.example.tasks.model

data class TodoModel(
    val id: Int,
    val completed: Boolean,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val subtasks: String

)
