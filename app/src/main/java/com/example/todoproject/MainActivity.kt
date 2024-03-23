

package com.example.todoproject

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoproject.adapter.TodoAdapter
import com.example.todoproject.databinding.ActivityMainBinding
import com.example.todoproject.model.TodoModel
import com.example.todoproject.utils.DatabaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity(), DialogCloseListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tasks: TextView
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var addTasks: FloatingActionButton
    private var tasksAdapter: TodoAdapter? = null
    private var tasksList: ArrayList<TodoModel?>? = null
    private var db: DatabaseHandler? = null
    private lateinit var notificationIcon:ImageView
    private lateinit var notificationTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        this.enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        tasksList = ArrayList()
        db = DatabaseHandler(this)
        db!!.openDatabase()
        notificationIcon=findViewById(R.id.notificationIcon)
        notificationTextView=findViewById(R.id.notificationText)
        val reminderBroadcast = ReminderBroadcast()
        reminderBroadcast.createNotification(this, "TaskName", 1)
        tasks=findViewById(R.id.tasks)
        addTasks=findViewById(R.id.addTasks)
        tasksRecyclerView = findViewById<RecyclerView>(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        tasksAdapter = TodoAdapter(db!!, this)
        tasksRecyclerView.adapter = tasksAdapter

        val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(tasksAdapter!!))
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)
        val addTasks = findViewById<FloatingActionButton>(R.id.addTasks)
        addTasks.setOnClickListener {

            val todoInstance = TodoModel(
                id = 1/* assign an ID to the new task */,
                task = "New Task",
                status = true,
                reminderTime = System.currentTimeMillis(),
                iconResId = R.drawable.notifications
            )
            db?.insertTask(todoInstance)
            updateTaskList()

        }
    }
    override fun onResume() {
        super.onResume()
        updateTaskList()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun updateTaskList() {
        val tasksList: ArrayList<TodoModel> = db?.allTasks ?: ArrayList()
        tasksList.reverse()
        tasksAdapter?.setTasks(tasksList)
        tasksAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun handleDialogClose(dialog: DialogInterface?) {
       updateTaskList()
    }
}
