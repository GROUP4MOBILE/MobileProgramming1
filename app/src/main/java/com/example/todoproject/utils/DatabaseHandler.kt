@file:Suppress("unused")

package com.example.todoproject.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todoproject.model.TodoModel

class DatabaseHandler(context: Context?) : SQLiteOpenHelper(context, NAME, null, VERSION) {
    private var db: SQLiteDatabase? = null


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TODO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TODO_TABLE")
        onCreate(db)
    }
    fun openDatabase(): SQLiteDatabase {
        if (db == null || !db!!.isOpen) {
            db = this.writableDatabase
        }
        return db!!
    }


    fun closeDatabase() {
        this.close()
    }

    fun insertTask(task: TodoModel): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TASK, task.task)
        values.put(STATUS, if (task.status) 1 else 0)
        val id = db.insert(TODO_TABLE, null, values)
        db.close()
        return id
    }

    fun updateTask(task: TodoModel): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TASK, task.task)
        values.put(STATUS, if (task.status) 1 else 0)
        val result = db.update(TODO_TABLE, values, "$ID=?", arrayOf(task.id.toString()))
        db.close()
        return result
    }

    fun deleteTask(taskId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TODO_TABLE, "$ID=?", arrayOf(taskId.toString()))
        db.close()
        return result
    }

    fun updateStatus(taskId: Int, status: Boolean): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(STATUS, if (status) 1 else 0)
        val result = db.update(TODO_TABLE, values, "$ID=?", arrayOf(taskId.toString()))
        db.close()
        return result
    }

    val allTasks: ArrayList<TodoModel>
        get() {
            val taskList: ArrayList<TodoModel> = ArrayList()
            val db = this.readableDatabase // Access the readable database

            db?.beginTransaction()
            try {
                db?.query(TODO_TABLE, null, null, null, null, null, null, null).use { cur ->
                    // Check if the cursor is not null and if it contains any data
                    if (cur != null && cur.moveToFirst()) {
                        do {
                            val task = TodoModel(
                                id = cur.getInt(cur.getColumnIndexOrThrow(ID)),
                                task = cur.getString(cur.getColumnIndexOrThrow(TASK)),
                                status = cur.getInt(cur.getColumnIndexOrThrow(STATUS)) == 1
                            )
                            taskList.add(task)
                        } while (cur.moveToNext())
                    }
                }
            } finally {
                db?.endTransaction() // End the transaction
                db?.close() // Close the database after use
            }
            return taskList
        }

    companion object {
        private const val VERSION = 1
        private const val NAME = "todoListDatabase"
        private const val TODO_TABLE = "todo"
        private const val ID = "id"
        private const val TASK = "task"
        private const val STATUS = "status"
        private const val CREATE_TODO_TABLE =
            "CREATE TABLE todo (id INTEGER PRIMARY KEY AUTOINCREMENT, task TEXT, status INTEGER)"
    }
}
