@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.example.todoproject.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.todoproject.AddNewTask
import com.example.todoproject.MainActivity
import com.example.todoproject.R
import com.example.todoproject.model.TodoModel
import com.example.todoproject.utils.DatabaseHandler

class TodoAdapter(private val db: DatabaseHandler, private val activity: MainActivity) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    private var todoList: ArrayList<TodoModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tasks_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db.openDatabase()
        val item = todoList!![position]
        holder.task.text = item.task
        holder.task.setChecked(toBoolean())
        holder.task.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                db.updateStatus(item.id, true)
            } else {
                db.updateStatus(item.id, false)
            }
        }
    }

    override fun getItemCount(): Int {
        return todoList!!.size
    }

    private fun toBoolean(): Boolean {
        return false
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(todoList: ArrayList<TodoModel>?) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    val context: Context
        get() = activity

    fun deleteItem(position: Int) {
        val item = todoList!![position]
        db.deleteTask(item.id.toInt())
        todoList!!.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int) {
        val item = todoList!![position]
        val bundle = Bundle()
        bundle.putInt("id", item.id.toInt())
        bundle.putString("task", item.task)
        val fragment = AddNewTask()
        fragment.arguments = bundle // Set arguments using `arguments` property
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG as String?) // Ensure TAG is a String
    }


    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var task: CheckBox

        init {
            task = view.findViewById(R.id.todoCheckBox)
        }
    }
}

