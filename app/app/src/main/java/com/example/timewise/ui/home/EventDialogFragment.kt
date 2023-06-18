package com.example.timewise.ui.home

// Package required by EventDialogFragment
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.DialogFragment
import com.example.timewise.db.Event

// EventDialogFragment class to show event dialog
class EventDialogFragment() : DialogFragment() {
    constructor(event: Event) : this() {
        HomeViewModel.event = event
    }

    // Method to create event dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Get event from HomeViewModel
        val event = HomeViewModel.event

        // Create and show a dialog window with the description of the event
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(event.name)
        dialogBuilder.setMessage(event.description)
        dialogBuilder.setPositiveButton("OK", null)

        // Set the colors of the dialog
        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            val textColor = resolveThemeTextColorText(requireContext())
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(textColor)
        }
        val backgroundColor = resolveThemeTextColorBackground(requireContext())
        dialog.window?.setBackgroundDrawable(ColorDrawable(backgroundColor))

        // Return the dialog
        return dialog
    }

    // Method to resolve the theme text color
    private fun resolveThemeTextColorText(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        return typedValue.data
    }

    // Method to resolve the theme text color background
    private fun resolveThemeTextColorBackground(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }
}