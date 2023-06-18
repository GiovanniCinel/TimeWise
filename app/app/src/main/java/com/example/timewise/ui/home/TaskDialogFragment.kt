package com.example.timewise.ui.home

// Packages required by TaskDialogFragment class
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.DialogFragment
import com.example.timewise.db.Task

// TaskDialogFragment class to show the task dialog
class TaskDialogFragment() : DialogFragment() {
    constructor(task: Task) : this() {
        HomeViewModel.task = task
    }

    // Method to create the task dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Get the task from the HomeViewModel
        val task = HomeViewModel.task

        // Create and show a dialog window with the task description
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(task.name)
        dialogBuilder.setMessage(task.description)
        dialogBuilder.setPositiveButton("OK", null)

        // Set the text color and background color of the dialog window
        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            val textColor = resolveThemeTextColorText(requireContext())
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(textColor)
        }
        val backgroundColor = resolveThemeTextColorBackground(requireContext())
        dialog.window?.setBackgroundDrawable(ColorDrawable(backgroundColor))

        // Return the dialog window
        return dialog
    }

    // Method to resolve the theme text color
    private fun resolveThemeTextColorText(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        return typedValue.data
    }

    // Method to resolve the theme background color
    private fun resolveThemeTextColorBackground(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }
}