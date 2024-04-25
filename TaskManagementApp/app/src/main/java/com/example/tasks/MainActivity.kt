package com.example.tasks
import android.annotation.SuppressLint

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasks.adapter.TodoAdapter
import com.example.tasks.databinding.ActivityMainBinding
import com.example.tasks.model.TodoModel
import com.example.tasks.utils.DatabaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
class MainActivity : AppCompatActivity(), DialogCloseListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tasks: TextView
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var addTasks: FloatingActionButton // Renamed to avoid confusion
    private var tasksAdapter: TodoAdapter? = null
    private var tasksList: ArrayList<TodoModel> = ArrayList() // Initialize as non-nullable
    private var db: DatabaseHandler? = null
    private lateinit var addNewTask: AddNewTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = DatabaseHandler(this)
        db!!.openDatabase()

        tasks = findViewById(R.id.tasks)
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        tasksList = (db?.getAllTasks()
            ?: ArrayList()) as ArrayList<TodoModel> // Fetch data from the database
        tasksList.reverse() // Reverse the list if necessary (as per your requirements)
        tasksAdapter = TodoAdapter(db!!, this)
        tasksAdapter!!.setTasks(tasksList)
        tasksRecyclerView.adapter = tasksAdapter

        val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(tasksAdapter!!))
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)

        addTasks = findViewById(R.id.addTasks)
        addNewTask = AddNewTask()
        addTasks.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun handleDialogClose(dialog: DialogInterface) {
        tasksList.clear()
        tasksList.addAll(db?.getAllTasks() ?: emptyList())
        tasksList.reverse() // Reverse the list if necessary
        tasksAdapter?.setTasks(tasksList)
        tasksAdapter?.notifyDataSetChanged()
    }
}