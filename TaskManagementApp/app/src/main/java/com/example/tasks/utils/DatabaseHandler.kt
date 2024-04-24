@file:Suppress("KotlinConstantConditions")

package com.example.tasks.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tasks.model.TodoModel


class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ToDoDatabase"
        private const val TABLE_TODO = "Todo"
        private const val KEY_ID = "id"
        private const val KEY_COMPLETED = "completed"
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_START_DATE = "startDate"
        private const val KEY_END_DATE = "endDate"
        private const val KEY_SUBTASKS = "subtasks"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_TODO ($KEY_ID INTEGER PRIMARY KEY, " +
                "$KEY_COMPLETED INTEGER, $KEY_TITLE TEXT, $KEY_DESCRIPTION TEXT, " +
                "$KEY_START_DATE TEXT,$KEY_END_DATE TEXT, $KEY_SUBTASKS TEXT)"
        db.execSQL(createTable)


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TODO")

        onCreate(db)
    }

    fun openDatabase(): SQLiteDatabase {
        return writableDatabase
    }

    fun insertTask(todo: TodoModel) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_COMPLETED, if (todo.completed) 1 else 0)
            put(KEY_TITLE, todo.title)
            put(KEY_DESCRIPTION, todo.description)
            put(KEY_START_DATE, todo.startDate)
            put(KEY_END_DATE, todo.endDate)
            put(KEY_SUBTASKS, todo.subtasks)
        }
        db.insert(TABLE_TODO, null, values)
        db.close()
    }

    fun getAllTasks(): List<TodoModel> {
        val taskList = mutableListOf<TodoModel>()
        val selectQuery = "SELECT * FROM $TABLE_TODO"
        val db = readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow(KEY_ID))
                    val title = it.getString(it.getColumnIndexOrThrow(KEY_TITLE))
                    val description = it.getString(it.getColumnIndexOrThrow(KEY_DESCRIPTION))
                    val startDate = it.getString(it.getColumnIndexOrThrow(KEY_START_DATE))
                    val endDate = it.getString(it.getColumnIndexOrThrow(KEY_END_DATE))
                    val completed = it.getInt(it.getColumnIndexOrThrow(KEY_COMPLETED)) != 0
                    val subtasks = it.getString(it.getColumnIndexOrThrow(KEY_SUBTASKS))



                    val todo = TodoModel(id, completed, title, description, startDate, endDate, subtasks)
                    taskList.add(todo)
                } while (it.moveToNext())
            }
        }
        cursor?.close()
        return taskList
    }

    fun getTask(id: Int): TodoModel? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_TODO,
            arrayOf(KEY_ID, KEY_COMPLETED, KEY_TITLE, KEY_DESCRIPTION, KEY_START_DATE, KEY_END_DATE, KEY_SUBTASKS),
            "$KEY_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val todo = TodoModel(
                cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COMPLETED)) != 0,
                cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(KEY_START_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(KEY_END_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(KEY_SUBTASKS)),

            )
            cursor.close()
            todo
        } else {
            cursor.close()
            null
        }
    }



    fun updateTask(todo: TodoModel): Int {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_TITLE, todo.title)
            put(KEY_DESCRIPTION, todo.description)
            put(KEY_START_DATE, todo.startDate)
            put(KEY_END_DATE, todo.endDate)
            put(KEY_SUBTASKS, todo.subtasks)
            put(KEY_COMPLETED, if (todo.completed) 1 else 0)
        }
        val result = db.update(TABLE_TODO, contentValues, "$KEY_ID = ?", arrayOf(todo.id.toString()))
        db.close()
        return result
    }

    fun deleteTask(id: Int) {
        val db = writableDatabase
        db.delete(
            TABLE_TODO,
            "$KEY_ID=?",
            arrayOf(id.toString())
        )
        db.close()
    }

    fun updateStatus(id: Int, completed: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_COMPLETED, if (completed) 1 else 0)
        }
        db.update(TABLE_TODO, values, "$KEY_ID=?", arrayOf(id.toString()))
        db.close()
    }



    }
