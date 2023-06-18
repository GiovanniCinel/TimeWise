package com.example.timewise.ui.todo.task

// Package required by TaskFullListDialogFragment class
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.timewise.R
import com.example.timewise.databinding.ModifyTaskDialogBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TaskFullListDialogFragment : DialogFragment {
    // Binding section
    private var _binding: ModifyTaskDialogBinding? = null
    private val binding get() = _binding!!

    // Flag to check if the dialog is recreated by the system
    private var recreated: Boolean


    // This class has two constructors:
    //  - One with categoryName parameter: called when the user click on the "modify" button
    //  - One without parameters: used by the system to recreate the dialog when rotation is performed
    // This is necessary because we need to pass the selected category name to the dialog,
    // but we also need the system to recreate the dialog when rotation is performed

    // Constructor without parameters
    constructor() : super() {
        recreated = true
    }

    // Constructor with currentTask parameter
    constructor(currentTask: Task) : super() {
        recreated = false
        TaskViewModel.currentTask = currentTask
    }

    // Method to create the modify task dialog fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        _binding = ModifyTaskDialogBinding.inflate(inflater, container, false)

        // Get the root view
        return binding.root
    }

    // Method to create the modify task dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Get the database instance and the task Dao
        val db = AppDatabase.getInstance(requireContext())
        val taskDao = db.taskDao()

        // Get the current task from the view model
        val currentTask = TaskViewModel.currentTask

        // Get current task name
        val taskName = currentTask.name
        // Get current task description
        val taskDescription = currentTask.description
        // Get current task priority
        val taskPriority = currentTask.priority

        // Show task modify dialog
        val dialog = MaterialAlertDialogBuilder(
            requireContext(), R.style.DialogTheme
        ).setTitle(R.string.modify_task).setView(R.layout.modify_task_dialog)
            // Positive button logic
            .setPositiveButton(R.string.accept) { dialog, _ ->
                // Set casting for next instructions
                dialog as AlertDialog
                // Get task name input from edit text
                val modifyTaskName = dialog.findViewById<TextView>(R.id.modify_task_name)
                // Get task description input from edit text
                val modifyTaskDescription =
                    dialog.findViewById<TextView>(R.id.modify_task_description)
                // Get task priority from button toggle group
                val toggleGroup =
                    dialog.findViewById<MaterialButtonToggleGroup>(R.id.priority_toggle_group)

                // Check if task name is empty or if task description is empty
                if (!isEmpty(modifyTaskName.toString()) || !isEmpty(modifyTaskDescription.toString())) {
                    // Cast new task name to string
                    val newTaskName = modifyTaskName?.text.toString()
                    // Cast new task description to string
                    val newTaskDescription = modifyTaskDescription?.text.toString()
                    // Cast new task priority to int
                    val newPriorityId: Int = dialog.findViewById<MaterialButton>(
                        toggleGroup?.checkedButtonId!!
                    )!!.contentDescription.toString().trim().toInt()

                    // Update task name, description and priority with new one
                    taskDao.update(
                        Task(
                            currentTask.id,
                            newTaskName,
                            newTaskDescription,
                            currentTask.category,
                            newPriorityId
                        )
                    )

                    // onDialogClose method call
                    (TaskViewModel.taskFullListFragment as OnDialogCloseListener).onDialogClose()

                    // Show modify task name overlay label
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.modify_task_toast) + ": " + newTaskName,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Show label for task not modified
                    Toast.makeText(
                        requireContext(), R.string.no_empty_task_name_description, Toast.LENGTH_LONG
                    ).show()
                }
            }
            // Negative button logic
            .setNegativeButton(R.string.delete) { _, _ ->
                // Delete current task from database
                taskDao.delete(currentTask)

                // onDialogClose method call
                (TaskViewModel.taskFullListFragment as OnDialogCloseListener).onDialogClose()

                // Show delete task overlay label
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.delete_task_toast) + ": " + taskName,
                    Toast.LENGTH_SHORT
                ).show()
            }.create()

        // Set dialog on show listener to set current task name, description and priority
        dialog.setOnShowListener {
            // Check if the dialog is recreated by the system
            if (!recreated) {
                // Set current task name in edit text for modification
                dialog.findViewById<EditText>(R.id.modify_task_name)!!.setText(taskName)
                // Set current task description in edit text for modification
                dialog.findViewById<EditText>(R.id.modify_task_description)!!
                    .setText(taskDescription)
                // Set current task priority in priority toggle group for modification
                dialog.findViewById<MaterialButtonToggleGroup>(R.id.priority_toggle_group)!!.check(
                    when (taskPriority) {
                        1 -> R.id.btn_white_priority
                        2 -> R.id.btn_yellow_priority
                        3 -> R.id.btn_orange_priority
                        4 -> R.id.btn_red_priority
                        else -> R.id.btn_white_priority // Default color for out of bound priority id values
                    }
                )
            }

            // Get text color from theme
            val textColor = resolveThemeTextColor(requireContext())
            // Get dialog buttons
            val cancelButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            val saveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            // Set text color for dialog buttons
            cancelButton?.setTextColor(textColor)
            saveButton?.setTextColor(textColor)
        }

        // Return modify task dialog
        return dialog
    }

    // Method to dismiss the modify task dialog
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (TaskViewModel.taskFullListFragment as OnDialogCloseListener).onDialogClose()
    }

    // Method to resolve theme text color
    private fun resolveThemeTextColor(context: Context): Int {
        // Resolve theme text color
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        // Return theme text color
        return typedValue.data
    }
}