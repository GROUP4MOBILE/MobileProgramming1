@file:Suppress("DEPRECATION", "KotlinConstantConditions", "NAME_SHADOWING")

package com.example.tasks

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.tasks.model.TodoModel
import com.example.tasks.utils.DatabaseHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNewTask : BottomSheetDialogFragment() {
    private lateinit var taskTitle: EditText
    private lateinit var taskDescription: EditText
    private lateinit var taskStartDate: EditText
    private lateinit var taskEndDate: EditText
    private lateinit var buttonSetReminder: Button
    private lateinit var save: Button
    private lateinit var db: DatabaseHandler
    private lateinit var uploadBtn: Button
    private var subtask: EditText? = null
    private var addSubtaskBtn: Button? = null
    private val subtasksList: MutableList<String> = arrayListOf()
    private lateinit var subtaskContainer: LinearLayout

    companion object {
        const val TAG = "AddNewTaskFragment"
        const val REQUEST_CODE_FILE_PICKER = 1001 // Define your request code
        fun newInstance(): AddNewTask {
            return AddNewTask()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*" // Specify the MIME type of the document you want to allow the user to select
        startActivityForResult(intent, REQUEST_CODE_FILE_PICKER)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment or activity
        val view = inflater.inflate(R.layout.newtask_layout, container, false)

        // Set soft input mode for the dialog window if it's not null
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // Return the inflated view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskTitle = requireView().findViewById(R.id.taskTitle)
        subtaskContainer = requireView().findViewById(R.id.subtaskContainer)
        taskDescription = requireView().findViewById(R.id.taskDescription)
        taskStartDate = requireView().findViewById(R.id.taskStartDate)
        taskEndDate = requireView().findViewById(R.id.taskEndDate)

        taskStartDate.setOnClickListener {
            showDatePicker(taskStartDate)
        }

        taskEndDate.setOnClickListener {
            showDatePicker(taskEndDate)
        }

        buttonSetReminder = requireView().findViewById(R.id.buttonSetReminder)
        buttonSetReminder.setOnClickListener {
            showTimePicker()
        }

        // Initialize views
        subtask = requireView().findViewById<EditText>(R.id.subtask)
        addSubtaskBtn = requireView().findViewById<Button>(R.id.addSubtaskBtn)

        // Set click listener for add subtask button
        addSubtaskBtn?.setOnClickListener {
            val subtaskText = subtask?.text.toString().trim()
            if (subtaskText.isNotEmpty()) {
                subtasksList.add(subtaskText)
                subtask?.text?.clear()
                val newEditText = EditText(context)
                newEditText.layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                newEditText.setText(subtaskText)
                subtaskContainer.addView(newEditText)
            }
        }

        uploadBtn = requireView().findViewById(R.id.uploadBtn)
        uploadBtn.setOnClickListener {
            openFilePicker()
        }

        save = requireView().findViewById(R.id.save)
        db = DatabaseHandler(requireActivity())
        db.openDatabase()

        val bundle = arguments
        if (bundle != null) {
            val taskId = bundle.getInt("taskId", -1)
            if (taskId != -1) {
                val task = db.getTask(taskId)
                task?.let {
                    taskTitle.setText(it.title)
                    taskDescription.setText(it.description)
                    taskStartDate.setText(it.startDate)
                    taskEndDate.setText(it.endDate)
                    subtask?.setText(it.subtasks)

                    if (it.title.isNotEmpty() || it.description.isNotEmpty() ||
                        it.startDate.isNotEmpty() || it.endDate.isNotEmpty() || it.subtasks.isNotEmpty()
                    ) {
                        save.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    }
                }
            }
        }

        taskTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No changes needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    save.isEnabled = false
                    save.setTextColor(Color.GRAY)
                } else {
                    save.isEnabled = true
                    save.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No changes needed here
            }
        })

        save.setOnClickListener {
            val title = taskTitle.text.toString()
            val description = taskDescription.text.toString()
            val startDate = taskStartDate.text.toString()
            val endDate = taskEndDate.text.toString()
            val subtasks = subtask?.text.toString()

            val taskId = bundle?.getInt("taskId", -1)
            if (taskId != null) {
                if (taskId != -1) {
                    db.updateTask(TodoModel(taskId, false, title, description, startDate, endDate, subtasks))
                } else {
                    db.insertTask(TodoModel(1, false, title, description, startDate, endDate, subtasks))
                }
            }
            dismiss()
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                editText.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedTime.set(Calendar.MINUTE, selectedMinute)
                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedTime.time)
                // Handle the selected time as needed
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            // Get the selected file URI
            val uri = data?.data
            // Do something with the selected file URI
            uri?.let { selectedUri ->
                println("Selected file URI: $selectedUri")
                // Process the selected document URI
                // For example, you can store the URI or its path in your task model
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity = requireActivity()
        if (activity is DialogCloseListener) {
            (activity as DialogCloseListener).handleDialogClose(dialog)
        }
    }
}
