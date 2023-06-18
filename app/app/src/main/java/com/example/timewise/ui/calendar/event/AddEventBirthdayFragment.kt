package com.example.timewise.ui.calendar.event

// Packages required by AddEventBirthdayFragment class
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
import com.example.timewise.databinding.AddEventBirthdayBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Event
import com.example.timewise.notification.NotificationUtils
import java.time.LocalDate
import java.util.*

// AddEventBirthdayFragment class to add a birthday event
class AddEventBirthdayFragment : Fragment() {
    // Binding section
    private var _binding: AddEventBirthdayBinding? = null
    private val binding get() = _binding!!

    // Selected time
    private lateinit var date: LocalDate

    // Method to create the add event birthday fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the date from the arguments passed by the previous fragment
        val args: AddEventAppointmentFragmentArgs by navArgs()
        date = args.selectedDate
    }

    // Method to create the view of the add event birthday fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = AddEventBirthdayBinding.inflate(inflater, container, false)

        // Get the root view
        val root: View = binding.root

        // Get the references to the element of the layout
        val editTextName = binding.editTextName
        val editTextNotes = binding.editTextNotes
        val buttonSave = binding.buttonSave

        // Save the event to the database when the user clicks on the save button
        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val notes = editTextNotes.text.toString()

            // If fields are not null, insert the event into the database
            if (name.isEmpty()) {
                Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
            } else {
                // Save event to database
                val db = AppDatabase.getInstance(binding.root.context)
                val eventDao = db.eventDao()
                val event = Event(
                    name = "Compleanno di $name",
                    description = notes,
                    place = null,
                    time = null,
                    date = date,
                    eventType = 1,
                    celebrated = name,
                    dateEnd = null,
                    dateStart = null
                )
                eventDao.insert(event)
                Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()

                // Schedule the notification
                val notificationUtils = NotificationUtils(requireContext())

                // Set the title and the message of the notification
                val title = "Compleanno di $name"
                val message = "Ricorda il compleanno di $name oggi!"

                // Set the date of the event as the date of the notification
                val calendar = Calendar.getInstance()
                val timeZone = TimeZone.getTimeZone("Europe/Rome")
                TimeZone.setDefault(timeZone)
                calendar.time = Date.from(
                    event.date?.atStartOfDay(timeZone.toZoneId())?.toInstant()
                )
                // Set the date of the event in the calendar
                calendar.set(
                    Calendar.HOUR_OF_DAY,
                    0
                )
                // Set the desired time (in 24-hour format)
                calendar.set(Calendar.MINUTE, 0) // Set the desired minutes
                calendar.set(Calendar.SECOND, 0) // Set the seconds to 0
                Log.d(
                    "AddEventBirthdayFragment",
                    "Data di programmazione della notifica: ${calendar.time} $title $message"
                )

                // Get the id of the last event inserted
                val eventId = eventDao.getLastId()

                // Schedule the notification
                notificationUtils.scheduleNotification(calendar.time, title, message, eventId)

                // Navigate back to the day fragment
                findNavController().navigate(R.id.action_addEventBirthdayFragment_to_dayFragment)
            }
        }

        // Return the root view
        return root
    }

    // Method to destroy the view of the add event birthday fragment
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null
        _binding = null
    }
}