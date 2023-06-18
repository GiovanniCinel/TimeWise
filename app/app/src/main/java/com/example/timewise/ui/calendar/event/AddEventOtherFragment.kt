package com.example.timewise.ui.calendar.event

// Packages required by AddEventOtherFragment class
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.timewise.R
import com.example.timewise.databinding.AddEventOtherBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Event
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.LocalTime

// AddEventOtherFragment class to add an event other than an appointment
class AddEventOtherFragment : Fragment() {

    // Binding section
    private var _binding: AddEventOtherBinding? = null
    private val binding get() = _binding!!

    // References to the elements of the layout
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var editTextLocation: TextInputEditText
    private lateinit var buttonSingleDay: MaterialButton
    private lateinit var buttonMultiDay: MaterialButton
    private lateinit var startTimeLayout: View
    private lateinit var editTextTime: TextInputEditText
    private lateinit var dateRangeLayout: View
    private lateinit var editTextStartDate: TextInputEditText
    private lateinit var editTextEndDate: TextInputEditText
    private lateinit var buttonSave: MaterialButton

    // Selected time
    private lateinit var selectedTime: LocalTime

    // Selected date
    private lateinit var selectedDate: LocalDate

    // Date of the event
    private lateinit var date: LocalDate

    // Method to create the add event other fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the date from the arguments passed by the previous fragment
        val args: AddEventAppointmentFragmentArgs by navArgs()
        date = args.selectedDate
    }

    // Method to create the view of the add event other fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = AddEventOtherBinding.inflate(inflater, container, false)

        // Get the root view
        val root = binding.root

        // Get the references to the elements of the layout
        editTextName = binding.editTextName
        editTextDescription = binding.editTextDescription
        editTextLocation = binding.editTextLocation
        buttonSingleDay = binding.buttonSingleDay
        buttonMultiDay = binding.buttonMultiDay
        startTimeLayout = binding.startTimeLayout
        editTextTime = binding.editTextTime
        dateRangeLayout = binding.dateRangeLayout
        editTextStartDate = binding.editTextStartDate
        editTextEndDate = binding.editTextEndDate
        buttonSave = binding.buttonSave


        // Set the fields to show when the user selects the single-day option
        buttonSingleDay.setOnClickListener {
            buttonSingleDay.isSelected = true
            buttonMultiDay.isSelected = false
            startTimeLayout.visibility = View.VISIBLE
            dateRangeLayout.visibility = View.GONE
        }

        // Set the fields to show when the user selects the multi-day option
        buttonMultiDay.setOnClickListener {
            buttonSingleDay.isSelected = false
            buttonMultiDay.isSelected = true
            startTimeLayout.visibility = View.GONE
            dateRangeLayout.visibility = View.VISIBLE
        }

        // Set the time field to show the time picker dialog when clicked
        editTextTime.setOnClickListener {
            showTimePickerDialog()
        }

        // Set the start date field to show the date picker dialog when clicked
        editTextStartDate.setOnClickListener {
            selectedDate = LocalDate.now()
            showDatePickerDialog(it as TextInputEditText)
        }

        // Set the end date field to show the date picker dialog when clicked
        editTextEndDate.setOnClickListener {
            selectedDate = LocalDate.now()
            showDatePickerDialog(it as TextInputEditText)
        }

        // Save the event when the save button is clicked
        buttonSave.setOnClickListener {
            // Get the data entered by the user
            val name = editTextName.text.toString()
            val description = editTextDescription.text.toString()
            val location = editTextLocation.text.toString()

            // Check if the user selected the single-day option or the multi-day option
            if (buttonSingleDay.isSelected) {
                // Get the start time entered by the user
                val startTime = editTextTime.text.toString()

                // Perform the save operation for a single-day event using the collected data
                saveEvent(name, description, location, startTime)
            } else {
                // Get the start date and end date entered by the user
                val startDate = editTextStartDate.text.toString()
                val endDate = editTextEndDate.text.toString()

                // Perform the save operation for a multi-day event using the collected data
                saveMultiDayEvent(name, description, location, startDate, endDate)
            }
        }

        // Return the root view
        return root
    }

    // Show the time picker dialog
    private fun showTimePickerDialog() {
        selectedTime = LocalTime.now()
        // Create a time picker listener used later on
        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                selectedTime = LocalTime.of(hourOfDay, minute)
                editTextTime.setText(selectedTime.toString())
            }

        // Create a new instance of TimePickerDialog using the listener
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

    // Show the date picker dialog
    private fun showDatePickerDialog(view: TextInputEditText) {
        // Create a date picker listener used later on
        val datePickerListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                view.setText(selectedDate.toString())
            }

        // Create a new instance of DatePickerDialog using the listener created previously
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            datePickerListener,
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        )

        datePickerDialog.show()
    }

    // Save a multi-day event to the database
    private fun saveMultiDayEvent(
        name: String,
        description: String,
        location: String,
        startDate: String,
        endDate: String
    ) {
        // If fields are not null, insert the event into the database
        if (name.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
        } else {
            // Check if the start date is before the end date
            val dateStart = LocalDate.parse(startDate)
            val dateEnd = LocalDate.parse(endDate)
            if (dateStart > dateEnd) {
                // Show a toast message if the start date is after the end date
                Toast.makeText(context, R.string.date_start_after_date_end, Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Save event to database
                val db = AppDatabase.getInstance(binding.root.context)
                val eventDao = db.eventDao()
                val event = Event(
                    name = name,
                    description = description,
                    place = location,
                    time = null,
                    date = null,
                    eventType = 6,
                    celebrated = null,
                    dateStart = dateStart,
                    dateEnd = dateEnd
                )
                eventDao.insert(event)
                Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()

                // Navigate to the day fragment
                findNavController().navigate(R.id.action_addEventOtherFragment_to_dayFragment)
            }
        }
    }

    // Save a single-day event to the database
    private fun saveEvent(name: String, description: String, location: String, startTime: String) {
        // If fields are not null, insert the event into the database
        if (name.isEmpty() || startTime.isEmpty()) {
            // Show a toast message if the user did not complete all the mandatory fields
            Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
        } else {
            // Save event to database
            val db = AppDatabase.getInstance(binding.root.context)
            val eventDao = db.eventDao()
            val event = Event(
                name = name,
                description = description,
                place = location,
                time = LocalTime.parse(startTime),
                date = date,
                eventType = 6,
                celebrated = null,
                dateEnd = null,
                dateStart = null
            )
            eventDao.insert(event)
            Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()

            // Navigate to the day fragment
            findNavController().navigate(R.id.action_addEventOtherFragment_to_dayFragment)
        }
    }

    // Method to destroy the view od the add event other fragment
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null
        _binding = null
    }
}