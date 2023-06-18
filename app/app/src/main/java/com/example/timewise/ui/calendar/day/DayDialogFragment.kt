package com.example.timewise.ui.calendar.day

// Packages required by DayDialogFragment class
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.timewise.R
import com.example.timewise.databinding.AddEventDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// DayDialogFragment class to show the dialog for adding an event
class DayDialogFragment : DialogFragment() {

    // Interface used by the DialogFragment to communicate with the Fragment
    interface DayDialogListener {
        fun onNavigate(selectedId: Int)
    }

    // Binding section
    private var _binding: AddEventDialogBinding? = null
    private val binding get() = _binding!!

    // Method to create the view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        _binding = AddEventDialogBinding.inflate(inflater, container, false)

        // Return the root view
        return binding.root
    }

    // Method to create the dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create the dialog
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
            .setTitle("Evento da aggiungere:")
            .setView(R.layout.add_event_dialog)
            // Positive button logic
            .setPositiveButton(R.string.next) { dialog, _ ->
                // Cast dialog to AlertDialog
                dialog as AlertDialog

                // Get event type choices
                val radioGroup = dialog.findViewById<RadioGroup>(R.id.add_event_type_radio_group)

                // Get selected radio button from radioGroup
                val selectedId = radioGroup!!.checkedRadioButtonId
                // Find the radiobutton by returned id
                val radioButton = dialog.findViewById<RadioButton>(selectedId)
                dialog.dismiss()
                (DayViewModel.fragment as DayDialogListener).onNavigate(selectedId)

                // Overlay label view for event type selected
                Toast.makeText(
                    context,
                    getString(R.string.event_type) + radioButton?.text,
                    Toast.LENGTH_LONG
                ).show()
            }
            // Negative button logic
            .setNegativeButton(R.string.decline, null)
            .create()

        // Set on show listener to change the text color of the buttons
        dialog.setOnShowListener {
            // Set the text color of the buttons
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val textColor = resolveThemeTextColor(requireContext())
            negativeButton.setTextColor(textColor)
            positiveButton.setTextColor(textColor)
        }

        // Return the dialog
        return dialog
    }

    // Method for resolving the theme text color
    private fun resolveThemeTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        return typedValue.data
    }
}