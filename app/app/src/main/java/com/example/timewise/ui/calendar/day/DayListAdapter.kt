package com.example.timewise.ui.calendar.day

// Packages required by DayListAdapter class
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.timewise.R
import com.example.timewise.db.Event
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate

// This class inherits from DayListAdapter that accept a parametric type
// It respond to RecyclerView requests
class DayListAdapter(
    private var eventDayMap: MutableMap<String, List<Event>>,
    private var dateEventDayMap: LocalDate
) : RecyclerView.Adapter<DayListAdapter.DayViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    // This class knows only the elements type of the RecyclerView but their
    // element is separately specified in the proper event item layout
    open inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Common item binding
        open fun bind(event: Event) {
            // Set on item click listener
            setItemClickListener()

            // Set on item long click listener
            setEventOnLongClickListener(event)
        }

        // Method to setup item click listener
        private fun setItemClickListener() {
            itemView.setOnClickListener {
                // Get reference to details layout
                val detailsLayout: LinearLayout = itemView.findViewById(R.id.details_layout)
                // Toggle visibility of details layout
                if (detailsLayout.visibility == View.GONE) {
                    detailsLayout.visibility = View.VISIBLE
                } else {
                    detailsLayout.visibility = View.GONE
                }
            }
        }

        // Method to setup item long click listener
        // It's overridden by each event type view holder
        open fun setEventOnLongClickListener(event: Event) {
            // Empty body
        }
    }

    // Birthday event view holder class
    inner class DayBirthdayViewHolder(itemView: View) : DayViewHolder(itemView) {
        // Get reference to event card
        private val eventCard: MaterialCardView =
            itemView.findViewById(R.id.event_birthday_item_card)

        // Method to bind birthday event data to the view holder
        override fun bind(event: Event) {
            // Common binding
            super.bind(event)

            // Set event card content description
            eventCard.contentDescription = event.id.toString()

            // Set event card background color
            eventCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.event_birthday_color
                )
            )

            // Event data binding
            val message = "Compleanno di ${event.celebrated}"
            eventCard.findViewById<TextView>(R.id.birthday_person_text_view).text = message
            val notes = eventCard.findViewById<TextView>(R.id.notes_text_view)
            notes.text = event.description
            if (event.description != "") {
                notes.visibility = View.VISIBLE
            }
        }

        // Method to setup item long click listener
        override fun setEventOnLongClickListener(event: Event) {
            itemView.setOnLongClickListener {
                // Navigation to modify event fragment
                val action = DayFragmentDirections.actionNavigationDayToModifyBirthdayFragment(
                    this@DayListAdapter.dateEventDayMap,
                    event.id
                )
                Navigation.findNavController(it).navigate(action)
                true
            }
        }
    }

    // Trip event view holder class
    inner class DayTripViewHolder(itemView: View) : DayViewHolder(itemView) {
        // Get reference to event card
        private val eventCard: MaterialCardView = itemView.findViewById(R.id.event_trip_item_card)

        // Method to bind trip event data to the view holder
        override fun bind(event: Event) {
            // Common binding
            super.bind(event)

            // Set event card content description
            eventCard.contentDescription = event.id.toString()

            // Set event card background color
            eventCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.event_trip_color
                )
            )

            // Event data binding
            eventCard.findViewById<TextView>(R.id.title_text_view).text = event.name
            eventCard.findViewById<TextView>(R.id.input_date_text_view_start).text =
                event.dateStart.toString()
            eventCard.findViewById<TextView>(R.id.input_date_text_view_end).text =
                event.dateEnd.toString()
            eventCard.findViewById<TextView>(R.id.input_location_text_view).text = event.place
            val descr = eventCard.findViewById<TextView>(R.id.description_text_view)
            descr.text = event.description
            if (event.description != "") {
                descr.visibility = View.VISIBLE
            }
        }

        // Method to setup item long click listener
        override fun setEventOnLongClickListener(event: Event) {
            itemView.setOnLongClickListener {
                // Navigation to modify event fragment
                val action = DayFragmentDirections.actionNavigationDayToModifyTripFragment(
                    this@DayListAdapter.dateEventDayMap,
                    event.id
                )
                Navigation.findNavController(it).navigate(action)
                true
            }
        }
    }

    // Meeting event view holder class
    inner class DayMeetingViewHolder(itemView: View) : DayViewHolder(itemView) {
        // Get reference to event card
        private val eventCard: MaterialCardView =
            itemView.findViewById(R.id.event_meeting_item_card)

        // Method to bind meeting event data to the view holder
        override fun bind(event: Event) {
            // Common binding
            super.bind(event)

            // Set event card content description
            eventCard.contentDescription = event.id.toString()

            // Set event card background color
            eventCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.event_meeting_color
                )
            )

            // Event data binding
            eventCard.findViewById<TextView>(R.id.title_text_view).text = event.name
            eventCard.findViewById<TextView>(R.id.input_time_text_view).text = event.time.toString()
            eventCard.findViewById<TextView>(R.id.input_location_text_view).text = event.place
            val descr = eventCard.findViewById<TextView>(R.id.description_text_view)
            descr.text = event.description
            if (event.description != "") {
                descr.visibility = View.VISIBLE
            }
            if (event.place != "") {
                eventCard.findViewById<LinearLayout>(R.id.place_layout).visibility = View.VISIBLE
            }
        }

        // Method to setup item long click listener
        override fun setEventOnLongClickListener(event: Event) {
            itemView.setOnLongClickListener {
                // Navigation to modify event fragment
                val action = DayFragmentDirections.actionNavigationDayToModifyMeetingFragment(
                    this@DayListAdapter.dateEventDayMap,
                    event.id
                )
                Navigation.findNavController(it).navigate(action)
                true
            }
        }
    }

    // Appointment event view holder class
    inner class DayAppointmentViewHolder(itemView: View) : DayViewHolder(itemView) {
        // Get reference to event card
        private val eventCard: MaterialCardView =
            itemView.findViewById(R.id.event_appointment_item_card)

        // Method to bind appointment event data to the view holder
        override fun bind(event: Event) {
            // Common binding
            super.bind(event)

            // Set event card content description
            eventCard.contentDescription = event.id.toString()

            // Set event card background color
            eventCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.event_appointment_color
                )
            )

            // Event data binding
            eventCard.findViewById<TextView>(R.id.title_text_view).text = event.name
            eventCard.findViewById<TextView>(R.id.input_time_text_view).text = event.time.toString()
            eventCard.findViewById<TextView>(R.id.input_location_text_view).text = event.place
            eventCard.findViewById<TextView>(R.id.description_text_view).text = event.description
            if (event.description != "") {
                eventCard.findViewById<LinearLayout>(R.id.description_layout).visibility =
                    View.VISIBLE
            }
            if (event.place != "") {
                eventCard.findViewById<LinearLayout>(R.id.location_layout).visibility = View.VISIBLE
            }
        }

        // Method to setup item long click listener
        override fun setEventOnLongClickListener(event: Event) {
            itemView.setOnLongClickListener {
                // Navigation to modify event fragment
                val action = DayFragmentDirections.actionNavigationDayToModifyAppointmentFragment(
                    this@DayListAdapter.dateEventDayMap,
                    event.id
                )
                Navigation.findNavController(it).navigate(action)
                true
            }
        }
    }

    // Deadline event view holder class
    inner class DayDeadlineViewHolder(itemView: View) : DayViewHolder(itemView) {
        // Get reference to event card
        private val eventCard: MaterialCardView =
            itemView.findViewById(R.id.event_deadline_item_card)

        // Method to bind deadline event data to the view holder
        override fun bind(event: Event) {
            // Common binding
            super.bind(event)

            // Set event card content description
            eventCard.contentDescription = event.id.toString()

            // Set event card background color
            eventCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.event_deadline_color
                )
            )

            // Event data binding
            eventCard.findViewById<TextView>(R.id.title_text_view).text = event.name
            eventCard.findViewById<TextView>(R.id.input_time_text_view).text = event.time.toString()
            eventCard.findViewById<TextView>(R.id.description_text_view).text = event.description
            if (event.description != "") {
                eventCard.findViewById<LinearLayout>(R.id.description_layout).visibility =
                    View.VISIBLE
            }
        }

        // Method to setup item long click listener
        override fun setEventOnLongClickListener(event: Event) {
            itemView.setOnLongClickListener {
                // Navigation to modify event fragment
                val action = DayFragmentDirections.actionNavigationDayToModifyDeadlineFragment(
                    this@DayListAdapter.dateEventDayMap,
                    event.id
                )
                Navigation.findNavController(it).navigate(action)
                true
            }
        }
    }

    // Other event view holder class
    inner class DayOtherViewHolder(itemView: View) : DayViewHolder(itemView) {
        // Get reference to event card
        private val eventCard: MaterialCardView = itemView.findViewById(R.id.event_other_item_card)

        // Method to bind other event data to the view holder
        override fun bind(event: Event) {
            // Common binding
            super.bind(event)

            // Event data binding
            if (event.dateStart == null) {
                // Set visibility of time_layout
                eventCard.findViewById<LinearLayout>(R.id.time_layout).visibility = View.VISIBLE

                // Set event card data
                eventCard.contentDescription = event.id.toString()
                eventCard.findViewById<TextView>(R.id.title_text_view).text = event.name
                eventCard.findViewById<TextView>(R.id.input_time_text_view).text =
                    event.time.toString()
                eventCard.findViewById<TextView>(R.id.description_text_view).text =
                    event.description
                eventCard.findViewById<TextView>(R.id.input_location_text_view).text =
                    event.place
                eventCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.event_other_card_color
                    )
                )

                // Set visibility of location_layout
                if (event.place != "") {
                    eventCard.findViewById<LinearLayout>(R.id.location_layout).visibility =
                        View.VISIBLE
                }

                // Set visibility of description_layout
                if (event.description != "") {
                    eventCard.findViewById<LinearLayout>(R.id.description_layout).visibility =
                        View.VISIBLE
                }
            } else {
                // Set visibility of date_start_layout
                eventCard.findViewById<LinearLayout>(R.id.date_start_layout).visibility =
                    View.VISIBLE
                // Set visibility of date_end_layout
                eventCard.findViewById<LinearLayout>(R.id.date_end_layout).visibility = View.VISIBLE

                // Set event card data
                eventCard.contentDescription = event.id.toString()
                eventCard.findViewById<TextView>(R.id.title_text_view).text = event.name
                eventCard.findViewById<TextView>(R.id.input_date_text_view_start).text =
                    event.dateStart.toString()
                eventCard.findViewById<TextView>(R.id.input_date_text_view_end).text =
                    event.dateEnd.toString()
                eventCard.findViewById<TextView>(R.id.description_text_view).text =
                    event.description
                eventCard.findViewById<TextView>(R.id.input_location_text_view).text = event.place
                // Set event card background color
                eventCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.event_other_card_color
                    )
                )

                // Set visibility of location_layout
                if (event.place != "") {
                    eventCard.findViewById<LinearLayout>(R.id.location_layout).visibility =
                        View.VISIBLE
                }

                // Set visibility of description_layout
                if (event.description != "") {
                    eventCard.findViewById<LinearLayout>(R.id.description_layout).visibility =
                        View.VISIBLE
                }
            }
        }

        // Method to setup item long click listener
        override fun setEventOnLongClickListener(event: Event) {
            itemView.setOnLongClickListener {
                // Navigation to modify event fragment
                val action = DayFragmentDirections.actionNavigationDayToModifyOtherFragment(
                    this@DayListAdapter.dateEventDayMap,
                    event.id
                )
                Navigation.findNavController(it).navigate(action)
                true
            }
        }
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        val flattenedEventList = eventDayMap.flatMap { it.value }
        // Return size of flattened event list
        return flattenedEventList.size
    }

    // Returns the view type of the item at position for the purposes of view recycling
    override fun getItemViewType(position: Int): Int {
        // Get eventType as list of event contained in each values list associated with keys
        val flattenedEventList = eventDayMap.flatMap { it.value }

        // Get event type from position in list and return event type
        return flattenedEventList[position].eventType
    }

    // Return a DayViewHolder reference to the created ViewHolder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayListAdapter.DayViewHolder {
        // Display log for each category element created
        Log.d(TAG, "onCreateViewHolder() called")

        // Get proper card type id according to event type
        var eventLayout = 0 // Invalid value
        val holder = when (viewType) {
            1 -> { // Birthday event
                eventLayout = R.layout.event_birthday_item
                // Get view layout according to event type
                val view = LayoutInflater.from(parent.context)
                    .inflate(eventLayout, parent, false)
                DayBirthdayViewHolder(view)
            }
            2 -> { // Trip event
                eventLayout = R.layout.event_trip_item
                // Get view layout according to event type
                val view = LayoutInflater.from(parent.context)
                    .inflate(eventLayout, parent, false)
                DayTripViewHolder(view)
            }
            3 -> { // Meeting event
                eventLayout = R.layout.event_meeting_item
                // Get view layout according to event type
                val view = LayoutInflater.from(parent.context)
                    .inflate(eventLayout, parent, false)
                DayMeetingViewHolder(view)
            }
            4 -> { // Appointment event
                eventLayout = R.layout.event_appointment_item
                // Get view layout according to event type
                val view = LayoutInflater.from(parent.context)
                    .inflate(eventLayout, parent, false)
                DayAppointmentViewHolder(view)
            }
            5 -> { // Deadline event
                eventLayout = R.layout.event_deadline_item
                // Get view layout according to event type
                val view = LayoutInflater.from(parent.context)
                    .inflate(eventLayout, parent, false)
                DayDeadlineViewHolder(view)
            }
            6 -> { // Other event
                // If the event has a dataStart and dataEnd it is a multiple day event
                eventLayout = R.layout.event_other_item
                // Get view layout according to event type
                val view = LayoutInflater.from(parent.context)
                    .inflate(eventLayout, parent, false)
                DayOtherViewHolder(view)
            }
            else -> { // Invalid value
                val view = LayoutInflater.from(parent.context)
                    .inflate(eventLayout, parent, false)
                DayViewHolder(view)
            }
        }

        // Created interface for the new element
        return holder
    }

    // Bind view holder to specified position
    override fun onBindViewHolder(holder: DayListAdapter.DayViewHolder, position: Int) {
        // Display log for each category element recycled
        Log.d(TAG, "onBindViewHolder() called")

        // Get eventType as list of event contained in each values list associated with keys
        val flattenedEventList = eventDayMap.flatMap { it.value }

        // Bind holder to specified position
        holder.bind(flattenedEventList[position])
    }

    // Companion object used to save constants
    companion object {
        // TAG used to identify class log
        private val TAG = this::class.simpleName
    }
}