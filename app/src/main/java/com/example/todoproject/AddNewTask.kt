@file:Suppress("DEPRECATION", "KotlinConstantConditions", "NAME_SHADOWING")

package com.example.todoproject

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.todoproject.model.TodoModel
import com.example.todoproject.utils.DatabaseHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar

class AddNewTask : BottomSheetDialogFragment() {
    private var newTask: EditText? = null
    private var save: Button? = null
    private var db: DatabaseHandler? = null
    private var reminderTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
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

    private fun showTimePickerDialog() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                reminderTime = calendar.timeInMillis // Set the reminder time
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newTask = requireView().findViewById(R.id.newTask)
        save = requireView().findViewById(R.id.save)
        db = DatabaseHandler(requireActivity())
        db!!.openDatabase()

        val isUpdate: Boolean
        val bundle = arguments
        if (bundle != null) {
            isUpdate = true
            val task = bundle.getString("task")
            task?.let {
                newTask?.setText(it)
                if (it.isNotEmpty()) {
                    save?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        )
                    )
                }
            }

            newTask?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // No changes needed here
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        save?.isEnabled = false
                        save?.setTextColor(Color.GRAY)
                    } else {
                        save?.isEnabled = true // Corrected to enable the button
                        save?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // No changes needed here
                }
            })

            save?.setOnClickListener {
                val text = newTask?.text.toString()
                if (isUpdate) {
                    bundle.getInt("id").let { id ->
                        db!!.updateTask(TodoModel(id, false, text,1))
                    }
                } else {
                    val task = TodoModel(1, false, text,0)
                    db!!.insertTask(task)
                }
                dismiss()
            }
        }
    }

    companion object {
        val TAG: Any = "New task updated"
    }
}
