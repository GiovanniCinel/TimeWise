package com.example.timewise.ui.todo.task

// Package required by AddTaskDialogFragment class
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.timewise.R
import com.example.timewise.databinding.AddTaskDialogBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// AddTaskDialogFragment class to add a task
class AddTaskDialogFragment : DialogFragment() {
    // Binding section
    private var _binding: AddTaskDialogBinding? = null
    private val binding get() = _binding!!

    // Method to create the add task dialog fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        _binding = AddTaskDialogBinding.inflate(inflater, container, false)

        // Get the root view
        return binding.root
    }

    // Method to create the add task dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create the add task dialog
        val dialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.DialogTheme
        ).setTitle(R.string.add_task).setView(R.layout.add_task_dialog)
            .setPositiveButton(R.string.accept) { dialog, _ ->
                // Get the dialog as AlertDialog
                dialog as AlertDialog
                // Get input fields
                val inputTaskName = dialog.findViewById<TextView>(R.id.add_task_name)
                val inputTaskDescription = dialog.findViewById<TextView>(R.id.add_task_description)

                // Priority button toggle group
                val toggleGroup =
                    dialog.findViewById<MaterialButtonToggleGroup>(R.id.priority_toggle_group)
                val idPrioritySelectedButton = toggleGroup?.checkedButtonId
                val idPrioritySelected: Int = dialog.findViewById<MaterialButton>(
                    idPrioritySelectedButton!!
                )!!.contentDescription.toString().trim().toInt()

                // Check if input fields are empty
                if (!isEmpty(inputTaskName) || !isEmpty(inputTaskDescription)) {
                    // Get database instance and task Dao
                    val db = AppDatabase.getInstance(binding.root.context)
                    val taskDao = db.taskDao()

                    // Get input fields values
                    val taskName = inputTaskName?.text.toString()
                    val taskDescription = inputTaskDescription!!.text.toString()
                    val idCategorySelected = TaskViewModel.idCategorySelected
                    // Insert task into database
                    taskDao.insert(
                        Task(
                            name = taskName,
                            description = taskDescription,
                            category = idCategorySelected,
                            priority = idPrioritySelected
                        )
                    )
                    // Show toast message to user to confirm task added
                    Toast.makeText(
                        context,
                        requireView().context.getString(R.string.add_task_toast) + ": " + taskName,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Show toast message to user that input fields cannot be empty
                    Toast.makeText(context, R.string.no_empty_element, Toast.LENGTH_LONG).show()
                }
            }.setNegativeButton(R.string.decline, null).create()


        // Set on show listener to dialog
        dialog.setOnShowListener {
            // Get color to resolve theme text color
            val textColor = resolveThemeTextColor(requireContext())

            // Get dialog buttons
            val cancelButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            val saveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            // Set dialog buttons text color
            cancelButton?.setTextColor(textColor)
            saveButton?.setTextColor(textColor)

            // Dialog cover whole screen weight
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window?.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            dialog.window?.attributes = lp
        }

        // Return dialog
        return dialog
    }

    // Method to check if the text view given is empty
    private fun isEmpty(textView: TextView?): Boolean {
        // Return true if text view is empty
        return textView?.text.toString().trim().isEmpty()
    }

    // Method to dismiss the add task dialog
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (TaskViewModel.taskFragment as OnDialogCloseListener).onDialogClose()
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