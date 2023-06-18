package com.example.timewise.ui.calendar.event

// Packages required by ModifyEventTripFragment class
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.timewise.R
import com.example.timewise.databinding.ModifyEventTripBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Event
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate

// ModifyEventTripFragment class to modify a trip event
class ModifyEventTripFragment : Fragment() {
    // Binding section
    private var _binding: ModifyEventTripBinding? = null
    private val binding get() = _binding!!

    // Arguments passed through navigation
    private lateinit var date: LocalDate
    private lateinit var event: Event

    // Current date
    private var selectedDate = LocalDate.now()

    // Method to create the modify event trip fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get arguments passed through navigation
        val args: ModifyEventAppointmentFragmentArgs by navArgs()
        date = args.selectedDate
        val eventId = args.event
        val db = AppDatabase.getInstance(requireContext().applicationContext)
        val eventDao = db.eventDao()
        event = eventDao.findById(eventId)
    }

    // Method to fill the event input fields
    private fun fillTripEventInput() {
        // Fill the event input fields
        binding.editTextDestination.setText(event.place)
        binding.editTextNotes.setText(event.description)
        binding.editTextDepartureDate.setText(event.dateStart.toString())
        binding.editTextArrivalDate.setText(event.dateEnd.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = ModifyEventTripBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Fill the event input fields
        fillTripEventInput()

        // Set the arrival date on click listener
        binding.editTextArrivalDate.setOnClickListener {
            showDatePickerDialog(it as TextInputEditText)
        }

        // Set the departure date on click listener
        binding.editTextDepartureDate.setOnClickListener {
            showDatePickerDialog(it as TextInputEditText)
        }

        // Save button on click listener
        binding.buttonSave.setOnClickListener {
            // Get event fields input
            val destination = binding.editTextDestination.text.toString()
            val notes = binding.editTextNotes.text.toString()
            val arrivalDate = binding.editTextArrivalDate.text.toString()
            val departureDate = binding.editTextDepartureDate.text.toString()
            val dateStart = LocalDate.parse(departureDate)
            val dateEnd = LocalDate.parse(arrivalDate)

            // Check if the departure date is after the arrival date
            if (dateStart > dateEnd)
            // Show toast to inform the user that the departure date is after the arrival date
                Toast.makeText(
                    context,
                    R.string.date_departure_after_date_return,
                    Toast.LENGTH_SHORT
                )
                    .show()
            else {
                // If fields are not null, insert the event into the database
                if (destination.isEmpty() || arrivalDate.isEmpty() || departureDate.isEmpty()) {
                    // Show toast to inform the user that all fields must be filled
                    Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
                } else {
                    // Get database instance to delete event from database
                    val db = AppDatabase.getInstance(binding.root.context)
                    val eventDao = db.eventDao()
                    // Update the event with new input
                    val updatedEvent = Event(
                        id = event.id,
                        name = "Viaggio --> $destination",
                        description = notes,
                        place = destination,
                        time = event.time,
                        date = event.date,
                        eventType = event.eventType,
                        celebrated = event.celebrated,
                        dateStart = dateStart,
                        dateEnd = dateEnd
                    )
                    // Update the event in the database
                    eventDao.update(updatedEvent)
                    // Show toast to confirm event saved
                    Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()

                    // Navigate back to the day fragment
                    findNavController().navigate(R.id.action_modifyEventTripFragment_to_dayFragment)
                }
            }
        }

        // Delete button on click listener
        binding.buttonDelete.setOnClickListener {
            // Get database instance to delete event from database
            val db = AppDatabase.getInstance(binding.root.context)
            val eventDao = db.eventDao()
            // Delete the event from database
            eventDao.delete(event)
            // Show toast to confirm event deleted
            Toast.makeText(context, R.string.delete_event_toast, Toast.LENGTH_SHORT).show()

            // Navigate back to the day fragment
            findNavController().navigate(R.id.action_modifyEventTripFragment_to_dayFragment)
        }

        // Return the root view
        return root
    }

    // The following method was fully generated by ChatGPT
    private fun showDatePickerDialog(view: TextInputEditText) {
        val datePickerListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                view.setText(selectedDate.toString())
            }

        // Create a new instance of DatePickerDialog and return it
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            datePickerListener,
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        )

        datePickerDialog.show()
    }

    // Method to destroy the modify event trip fragment
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null
        _binding = null
    }
}