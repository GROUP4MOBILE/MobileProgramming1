package com.example.tasks.model

data class TodoModel(
    val id: Int,
    val Completed: Boolean,
    val Title: String,
    val Description: String,
    val StartDate: String,
    val EndDate: String,
    val Subtasks: String
)
