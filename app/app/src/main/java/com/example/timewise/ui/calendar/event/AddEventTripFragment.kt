package com.example.timewise.ui.calendar.event

// Packages required by AddEventTripFragment class
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.timewise.R
import com.example.timewise.databinding.AddEventTripBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Event
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate

// AddEventTripFragment class to add a trip event
class AddEventTripFragment : Fragment() {
    // Binding section
    private var _binding: AddEventTripBinding? = null
    private val binding get() = _binding!!

    // Selected date
    private var selectedDate = LocalDate.now()

    // Method to create the add event trip fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = AddEventTripBinding.inflate(inflater, container, false)

        // Get the root view
        val root: View = binding.root

        // Get the references to the element of the layout
        val buttonSave = binding.buttonSave
        val editTextDestination = binding.editTextDestination
        val editTextNotes = binding.editTextNotes
        val editTextArrivalDate = binding.editTextArrivalDate
        val editTextDepartureDate = binding.editTextDepartureDate

        // Add the date picker to the edit text
        editTextArrivalDate.setOnClickListener {
            showDatePickerDialog(it as TextInputEditText)
        }

        // Add the date picker to the edit text
        editTextDepartureDate.setOnClickListener {
            showDatePickerDialog(it as TextInputEditText)
        }

        // Save the event to the database if possible
        buttonSave.setOnClickListener {
            // Get the values from the edit text
            val destination = editTextDestination.text.toString()
            val notes = editTextNotes.text.toString()
            val arrivalDate = editTextArrivalDate.text.toString()
            val departureDate = editTextDepartureDate.text.toString()

            // If fields are not null, insert the event into the database
            if (destination.isEmpty() || arrivalDate.isEmpty() || departureDate.isEmpty()) {
                // Show a toast message if the user did not complete all the mandatory fields
                Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
            } else {
                // Check if the departure date is before the arrival date
                val dateStart = LocalDate.parse(departureDate)
                val dateEnd = LocalDate.parse(arrivalDate)
                if (dateStart > dateEnd)
                // Show a toast message if the departure date is after the arrival date
                    Toast.makeText(
                        context,
                        R.string.date_departure_after_date_return,
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    // Save event to database
                    val db = AppDatabase.getInstance(binding.root.context)
                    val eventDao = db.eventDao()
                    val event = Event(
                        name = "Viaggio --> $destination",
                        description = notes,
                        place = destination,
                        time = null,
                        date = null,
                        eventType = 2,
                        celebrated = null,
                        dateStart = dateStart,
                        dateEnd = dateEnd
                    )
                    eventDao.insert(event)
                    Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()

                    // Navigate to the day fragment
                    findNavController().navigate(R.id.action_addEventTripFragment_to_dayFragment)
                }
            }

        }

        // Return the root view
        return root
    }

    // Show the date picker dialog
    private fun showDatePickerDialog(view: TextInputEditText) {
        // Create the date picker listener, that is used when creating the date picker dialog
        val datePickerListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                view.setText(selectedDate.toString())
            }

        // Create a new instance of DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            datePickerListener,
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        )

        // Show the date picker dialog created
        datePickerDialog.show()
    }

    // Method to destroy the add event trip fragment
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null
        _binding = null
    }
}