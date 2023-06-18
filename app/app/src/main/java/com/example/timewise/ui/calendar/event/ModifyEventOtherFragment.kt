package com.example.timewise.ui.calendar.event

// Packages required by ModifyEventOtherFragment class
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.timewise.R
import com.example.timewise.databinding.ModifyEventOtherBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Event
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.LocalTime

// ModifyEventOtherFragment class to modify an other event
class ModifyEventOtherFragment : Fragment() {
    // Binding section
    private var _binding: ModifyEventOtherBinding? = null
    private val binding get() = _binding!!

    // Arguments passed through navigation
    private lateinit var date: LocalDate
    private lateinit var event: Event

    // Get current time and date
    private lateinit var selectedTime: LocalTime
    private lateinit var selectedDate: LocalDate

    // Method to create the modify event other fragment
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
    private fun fillOtherEventInput() {
        // Fill the event input fields
        binding.editTextName.setText(event.name)
        binding.editTextDescription.setText(event.description)
        binding.editTextLocation.setText(event.place)

        // Single day event and multi day event view
        if (event.time == null) { // Multi day event
            binding.buttonSingleDay.isSelected = false
            binding.buttonMultiDay.isSelected = true
            binding.startTimeLayout.visibility = View.GONE
            binding.dateRangeLayout.visibility = View.VISIBLE

            // Set start and end date
            binding.editTextStartDate.setText(event.dateStart.toString())
            binding.editTextEndDate.setText(event.dateEnd.toString())
        } else { // Single day event
            binding.buttonSingleDay.isSelected = true
            binding.buttonMultiDay.isSelected = false
            binding.startTimeLayout.visibility = View.VISIBLE
            binding.dateRangeLayout.visibility = View.GONE

            // Set start time
            binding.editTextTime.setText(event.time.toString())
        }
    }

    // Method to create the modify event other fragment view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = ModifyEventOtherBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Fill the event input fields
        fillOtherEventInput()

        // Single day event button on click listener
        binding.buttonSingleDay.setOnClickListener {
            binding.buttonSingleDay.isSelected = true
            binding.buttonMultiDay.isSelected = false
            binding.startTimeLayout.visibility = View.VISIBLE
            binding.dateRangeLayout.visibility = View.GONE
        }

        // Multi day event button on click listener
        binding.buttonMultiDay.setOnClickListener {
            binding.buttonSingleDay.isSelected = false
            binding.buttonMultiDay.isSelected = true
            binding.startTimeLayout.visibility = View.GONE
            binding.dateRangeLayout.visibility = View.VISIBLE
        }

        // Time edit text on click listener
        binding.editTextTime.setOnClickListener {
            showTimePickerDialog()
        }

        // Start date edit text on click listener
        binding.editTextStartDate.setOnClickListener {
            selectedDate = LocalDate.now()
            showDatePickerDialog(it as TextInputEditText)
        }

        // End date edit text on click listener
        binding.editTextEndDate.setOnClickListener {
            selectedDate = LocalDate.now()
            showDatePickerDialog(it as TextInputEditText)
        }

        // Save button on click listener
        binding.buttonSave.setOnClickListener {
            // Get the event data from the input fields
            val name = binding.editTextName.text.toString()
            val description = binding.editTextDescription.text.toString()
            val location = binding.editTextLocation.text.toString()

            // Check if the event is a single day event or a multi day event
            if (binding.buttonSingleDay.isSelected) {
                // Get the start time
                val startTime = binding.editTextTime.text.toString()

                // Perform the save operation for an all-day event using the collected data
                saveEvent(name, description, location, startTime)
            } else {
                // Get the start and end date
                val startDate = binding.editTextStartDate.text.toString()
                val endDate = binding.editTextEndDate.text.toString()

                // Perform the save operation for a multi-day event using the collected data
                saveMultiDayEvent(name, description, location, startDate, endDate)
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
            findNavController().navigate(R.id.action_modifyEventOtherFragment_to_dayFragment)
        }

        // Return the root view
        return root
    }

    // Method to show the time picker dialog
    private fun showTimePickerDialog() {
        selectedTime = LocalTime.now()
        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                Log.w("AddEventOtherFragment", "showTimePickerDialog() 3")
                selectedTime = LocalTime.of(hourOfDay, minute)
                binding.editTextTime.setText(selectedTime.toString())
            }

        // Create a new instance of TimePickerDialog and return it
        val timePickerDialog = TimePickerDialog(
            context,
            R.style.TimePickerTheme,
            timePickerListener,
            selectedTime.hour,
            selectedTime.minute,
            true
        )

        timePickerDialog.show()
    }

    // Method to show the date picker dialog
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

    // Method to save the multi day event to the database
    private fun saveMultiDayEvent(
        name: String, description: String, location: String,
        startDate: String, endDate: String
    ) {
        // If fields are not null, insert the event into the database
        val dateEnd = LocalDate.parse(endDate)
        val dateStart = LocalDate.parse(startDate)
        if (dateStart > dateEnd)
        // Show toast to inform user that the start date is after the end date
            Toast.makeText(context, R.string.date_start_after_date_end, Toast.LENGTH_SHORT).show()
        else {
            if (name.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                // Show toast to inform user that all fields must be filled
                Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
            } else {
                // Get database instance to delete event from database
                val db = AppDatabase.getInstance(binding.root.context)
                val eventDao = db.eventDao()
                // Update the event with new input
                val updatedEvent = Event(
                    id = event.id,
                    name = name,
                    description = description,
                    place = location,
                    time = null,
                    date = null,
                    eventType = event.eventType,
                    celebrated = event.celebrated,
                    dateEnd = dateEnd,
                    dateStart = dateStart
                )
                // Update the event in the database
                eventDao.update(updatedEvent)
                // Show toast to confirm event saved
                Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()

                // Navigate back to the day fragment
                findNavController().navigate(R.id.action_modifyEventOtherFragment_to_dayFragment)
            }
        }
    }

    // Method to save the event to the database
    private fun saveEvent(name: String, description: String, location: String, startTime: String) {
        // If fields are not null, insert the event into the database
        if (name.isEmpty() || startTime.isEmpty()) {
            // Show toast to inform user that all fields must be filled
            Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
        } else {
            // Get database instance to delete event from database
            val db = AppDatabase.getInstance(binding.root.context)
            val eventDao = db.eventDao()
            // Update the event with new input
            val updatedEvent = Event(
                id = event.id,
                name = name,
                description = description,
                place = location,
                time = LocalTime.parse(startTime),
                date = date,
                eventType = event.eventType,
                celebrated = event.celebrated,
                dateEnd = null,
                dateStart = null
            )
            // Update the event in the database
            eventDao.update(updatedEvent)
            // Show toast to confirm event saved
            Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()

            // Navigate back to the day fragment
            findNavController().navigate(R.id.action_modifyEventOtherFragment_to_dayFragment)
        }
    }

    // Method to destroy the binding when fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null
        _binding = null
    }
}