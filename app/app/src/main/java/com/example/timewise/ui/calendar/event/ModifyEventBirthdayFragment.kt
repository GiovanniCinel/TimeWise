package com.example.timewise.ui.calendar.event

// Packages required by ModifyEventBirthdayFragment class
import android.content.ContentValues.TAG
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
import com.example.timewise.databinding.ModifyEventBirthdayBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Event
import com.example.timewise.notification.NotificationUtils
import java.time.LocalDate
import java.util.*

// ModifyEventBirthdayFragment class to modify a birthday event
class ModifyEventBirthdayFragment : Fragment() {
    // Binding section
    private var _binding: ModifyEventBirthdayBinding? = null
    private val binding get() = _binding!!

    // Arguments passed through navigation
    private lateinit var date: LocalDate
    private lateinit var event: Event

    // Method to create the modify event birthday fragment
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
    private fun fillBirthdayEventInput() {
        // Fill the event input fields
        binding.editTextName.setText(event.celebrated)
        binding.editTextNotes.setText(event.description)
    }

    // Method to create the modify event birthday fragment view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = ModifyEventBirthdayBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val notificationUtils = NotificationUtils(requireContext())
        // Fill the event input fields
        fillBirthdayEventInput()

        // Save button on click listener
        binding.buttonSave.setOnClickListener {
            // Get event fields input
            val name = binding.editTextName.text.toString()
            val notes = binding.editTextNotes.text.toString()

            // If fields are not null, insert the event into the database
            if (name.isEmpty()) {
                Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
            } else {
                // Get database instance to delete event from database
                notificationUtils.cancelNotification(root.context, event.id)
                val db = AppDatabase.getInstance(binding.root.context)
                val eventDao = db.eventDao()
                // Update the event with new input
                val updatedEvent = Event(
                    id = event.id,
                    name = "Compleanno di $name",
                    description = notes,
                    place = event.place,
                    time = event.time,
                    date = event.date,
                    eventType = event.eventType,
                    celebrated = name,
                    dateEnd = event.dateEnd,
                    dateStart = event.dateStart
                )
                Log.d(TAG, "Updated event: $updatedEvent ")
                // Update the event in the database
                eventDao.update(updatedEvent)
                // Show toast to confirm event saved
                Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()
                val title = "Compleanno di $name"
                val message = "Ricorda il compleanno di $name oggi!"
                val calendar = Calendar.getInstance()
                val timeZone = TimeZone.getTimeZone("Europe/Rome")
                TimeZone.setDefault(timeZone)
                calendar.time = Date.from(
                    event.date?.atStartOfDay(timeZone.toZoneId())?.toInstant()
                )
                // Set the event date in the calendar
                calendar.set(
                    Calendar.HOUR_OF_DAY,
                    0
                )
                // Set the desired time (in 24 hour format)
                calendar.set(Calendar.MINUTE, 0) // Imposta i minuti desiderati
                calendar.set(Calendar.SECOND, 0) // Imposta i secondi a 0

                Log.d(TAG, "Calendar: $title $message ${calendar.time}")

                // Schedule the notification
                notificationUtils.scheduleNotification(calendar.time, title, message, event.id)

                // Navigate back to the day fragment
                findNavController().navigate(R.id.action_modifyEventBirthdayFragment_to_dayFragment)
            }
        }

        // Delete button on click listener
        binding.buttonDelete.setOnClickListener {
            // Get database instance to delete event from database
            notificationUtils.cancelNotification(root.context, event.id)
            val db = AppDatabase.getInstance(binding.root.context)
            val eventDao = db.eventDao()
            // Delete the event from database
            eventDao.delete(event)
            // Show toast to confirm event deleted
            Toast.makeText(context, R.string.delete_event_toast, Toast.LENGTH_SHORT).show()

            // Navigate back to the day fragment
            findNavController().navigate(R.id.action_modifyEventBirthdayFragment_to_dayFragment)
        }

        // Return the root view
        return root
    }

    // Method to destroy the modify event birthday fragment view
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null
        _binding = null
    }
}