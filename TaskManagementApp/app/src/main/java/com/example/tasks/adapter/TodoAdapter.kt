package com.example.tasks.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.tasks.MainActivity
import com.example.tasks.AddNewTask
import com.example.tasks.R
import com.example.tasks.model.TodoModel
import com.example.tasks.utils.DatabaseHandler


class TodoAdapter(private val db: DatabaseHandler, private val activity: MainActivity) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    private var tasksList: ArrayList<TodoModel> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tasks_layout, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db.openDatabase()
        val item = tasksList[position]

        // Concatenate or format the text properly before assigning it to the TextView
        holder.task.text = "ID: ${item.id}\nTitle: ${item.title}\nDescription: ${item.description}\nStartDate: ${item.startDate}\nEndDate: ${item.endDate}"

        holder.task.isChecked = item.completed
        holder.task.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Call the appropriate function in your DatabaseHandler with the required parameters
                // Assuming the function is named 'updateStatus'
                db.updateStatus(item.id, true)
            } else {
                db.updateStatus(item.id, false)
            }
        }
    }


    override fun getItemCount(): Int {
        return tasksList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(todoList: ArrayList<TodoModel>) {
        this.tasksList = todoList
        notifyDataSetChanged()
    }

    val context: MainActivity
        get() = activity

    fun deleteItem(position: Int) {
        val item = tasksList[position]
        db.deleteTask(item.id)
        tasksList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int) {
        val item = tasksList[position]
        val bundle = Bundle().apply {
            putInt("id", item.id)
            putString("Title", item.title)
            putString("Description", item.description)
            putString("StartDate", item.startDate)
            putString("EndDate", item.endDate)
        }
        val fragment = AddNewTask().apply {
            arguments = bundle
        }
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var task: CheckBox = view.findViewById(R.id.todoCheckBox)

    }
    }



