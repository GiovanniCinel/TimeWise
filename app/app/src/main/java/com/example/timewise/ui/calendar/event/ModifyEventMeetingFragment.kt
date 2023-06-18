package com.example.timewise.ui.calendar.event

// Packages required by ModifyEventMeetingFragment class
import android.app.TimePickerDialog
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
import com.example.timewise.databinding.ModifyEventMeetingBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Event
import com.example.timewise.notification.NotificationUtils
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

// ModifyEventMeetingFragment class to modify a meeting event
class ModifyEventMeetingFragment : Fragment() {
    // Binding section
    private var _binding: ModifyEventMeetingBinding? = null
    private val binding get() = _binding!!

    // Arguments passed through navigation
    private lateinit var date: LocalDate
    private lateinit var event: Event

    // Current time
    private lateinit var selectedTime: LocalTime

    // Method to create the modify event meeting fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get arguments passed through navigation
        val args: ModifyEventAppointmentFragmentArgs by navArgs()
        date = args.selectedDate
        val eventId = args.event
        val db = AppDatabase.getInstance(requireContext().applicationContext)
        val eventDao = db.eventDao()
        event = eventDao.findById(eventId)
        selectedTime = event.time!!
    }

    // Method to fill the event input fields
    private fun fillMeetingEventInput() {
        // Fill the event input fields
        binding.editTextName.setText(event.name)
        binding.editTextDescription.setText(event.description)
        binding.editTextLocation.setText(event.place)
        binding.editTextTime.setText(event.time.toString())
    }

    // Method to create the modify event meeting fragment view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = ModifyEventMeetingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val notificationUtils = NotificationUtils(requireContext())
        // Fill the event input fields
        fillMeetingEventInput()

        // Time edit text on click listener
        binding.editTextTime.setOnClickListener {
            showTimePickerDialog()
        }

        // Save button on click listener
        binding.buttonSave.setOnClickListener {
            // Get event fields input
            val name = binding.editTextName.text.toString()
            val description = binding.editTextDescription.text.toString()
            val location = binding.editTextLocation.text.toString()
            val time = binding.editTextTime.text.toString()
            // If fields are not null, insert the event into the database
            if (name.isEmpty() || time.isEmpty()) {
                // Show toast to complete all fields
                Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
            } else {
                // Cancel the previous notification
                notificationUtils.cancelNotification(root.context, event.id)
                Log.d(TAG, "modifico notifica con id : ${event.id}")
                // Get database instance to delete event from database
                val db = AppDatabase.getInstance(binding.root.context)
                val eventDao = db.eventDao()
                // Update the event with new input
                val updatedEvent = Event(
                    id = event.id,
                    name = name,
                    description = description,
                    place = location,
                    time = selectedTime,
                    date = event.date,
                    eventType = event.eventType,
                    celebrated = event.celebrated,
                    dateEnd = event.dateEnd,
                    dateStart = event.dateStart
                )
                // Update the event in the database
                eventDao.update(updatedEvent)
                // Show toast to confirm event saved
                Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()
                val title = updatedEvent.name
                val message =
                    "${updatedEvent.time}\n${updatedEvent.place}\n${updatedEvent.description}\n"

                // Set the event date as the notification scheduling date
                val calendar = Calendar.getInstance()
                val timeZone = TimeZone.getTimeZone("Europe/Rome")
                TimeZone.setDefault(timeZone)
                calendar.time = Date.from(
                    updatedEvent.date?.atStartOfDay(timeZone.toZoneId())?.toInstant()
                )
                // Set the event date in the calendar
                updatedEvent.time?.let { it1 ->
                    calendar.set(
                        Calendar.HOUR_OF_DAY,
                        it1.hour
                    )
                }
                // Set the desired time (in 24 hour format)
                updatedEvent.time?.let { it1 ->
                    calendar.set(
                        Calendar.MINUTE,
                        it1.minute
                    )
                }
                // Set the desired minutes
                updatedEvent.time?.let { it1 ->
                    calendar.set(
                        Calendar.SECOND,
                        it1.second
                    )
                }
                // Set the seconds to 0
                notificationUtils.scheduleNotification(
                    calendar.time,
                    title,
                    message,
                    updatedEvent.id
                )

                Log.d(TAG, "aggiorno notifica con id : ${updatedEvent.id}")

                // Navigate back to the day fragment
                findNavController().navigate(R.id.action_modifyEventMeetingFragment_to_dayFragment)
            }
        }

        // Delete button on click listener
        binding.buttonDelete.setOnClickListener {
            // Cancel the previous notification
            notificationUtils.cancelNotification(root.context, event.id)

            Log.d(TAG, "Elimino notifica con id: ${event.id}")

            // Get database instance to delete event from database
            val db = AppDatabase.getInstance(binding.root.context)
            val eventDao = db.eventDao()
            // Delete the event from database
            eventDao.delete(event)
            // Show toast to confirm event deleted
            Toast.makeText(context, R.string.delete_event_toast, Toast.LENGTH_SHORT).show()

            // Navigate back to the day fragment
            findNavController().navigate(R.id.action_modifyEventMeetingFragment_to_dayFragment)
        }

        // Return the root view
        return root
    }

    // Method fully generated by ChatGPT
    private fun showTimePickerDialog() {
        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
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

    // Method to destroy the modify event meeting fragment view
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null
        _binding = null
    }
}