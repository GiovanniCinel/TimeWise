package com.example.timewise.ui.calendar.event

// Packages required by AddEventAppointmentFragment class
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
import com.example.timewise.databinding.AddEventAppointmentBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Event
import com.example.timewise.notification.NotificationUtils
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

// AddEventAppointmentFragment class to add an appointment event
class AddEventAppointmentFragment : Fragment() {
    // Binding section
    private var _binding: AddEventAppointmentBinding? = null
    private val binding get() = _binding!!

    // Selected time
    private var selectedTime = LocalTime.now()

    // Date of the event
    private lateinit var date: LocalDate

    // Method to create the add event appointment fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the date from the arguments passed by the previous fragment
        val args: AddEventAppointmentFragmentArgs by navArgs()
        date = args.selectedDate
    }

    // Method to create the view of the add event appointment fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = AddEventAppointmentBinding.inflate(inflater, container, false)

        // Get the root view
        val root: View = binding.root

        // Get the references to the element of the layout
        val buttonSave = binding.buttonSave
        val editTextName = binding.editTextName
        val editTextDescription = binding.editTextDescription
        val editTextLocation = binding.editTextLocation
        val editTextTime = binding.editTextTime

        // Show the time picker dialog when the user clicks on the time field
        editTextTime.setOnClickListener {
            showTimePickerDialog()
        }

        // Save the event to the database when the user clicks on the save button
        buttonSave.setOnClickListener {
            // Get the values from the fields
            val name = editTextName.text.toString()
            val description = editTextDescription.text.toString()
            val location = editTextLocation.text.toString()
            val time = editTextTime.text.toString()

            // If fields are not null, insert the event into the database
            if (name.isEmpty() || time.isEmpty()) {
                Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
            } else {
                // Save event to database
                val db = AppDatabase.getInstance(binding.root.context)
                val eventDao = db.eventDao()
                val event = Event(
                    name = name,
                    description = description,
                    place = location,
                    time = selectedTime,
                    date = date,
                    eventType = 4,
                    celebrated = null,
                    dateEnd = null,
                    dateStart = null
                )
                eventDao.insert(event)
                Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()

                // Create a notification for the event
                val notificationUtils = NotificationUtils(requireContext())

                // Set the notification title and message
                val title = event.name
                val message = "${event.time}\n${event.place}\n${event.description}\n"

                // Set the date of the event as the date of the notification
                val calendar = Calendar.getInstance()
                val timeZone = TimeZone.getTimeZone("Europe/Rome")
                TimeZone.setDefault(timeZone)
                // Set the date of the event in the calendar
                calendar.time =
                    Date.from(event.date?.atStartOfDay(timeZone.toZoneId())?.toInstant())
                // Set the desired time (in 24-hour format)
                event.time?.let { it1 -> calendar.set(Calendar.HOUR_OF_DAY, it1.hour) }
                // Set the desired minutes
                event.time?.let { it1 -> calendar.set(Calendar.MINUTE, it1.minute) }
                // Set the desired seconds to 0
                event.time?.let { it1 -> calendar.set(Calendar.SECOND, it1.second) }

                // Get the id of the last event inserted
                val eventId = eventDao.getLastId()

                // Schedule the notification
                notificationUtils.scheduleNotification(calendar.time, title, message, eventId)

                // Navigate to the day fragment
                findNavController().navigate(R.id.action_addEventAppointmentFragment_to_dayFragment)
            }

        }

        // Return the root view
        return root
    }

    // Show the time picker dialog
    private fun showTimePickerDialog() {
        // Create the time picker listener
        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                selectedTime = LocalTime.of(hourOfDay, minute)
                binding.editTextTime.setText(selectedTime.toString())
            }

        // Create a new instance of TimePickerDialog using the previously created listener
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

    // Method to destroy the view of the add event appointment fragment
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null
        _binding = null
    }
}