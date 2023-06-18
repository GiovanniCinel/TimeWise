package com.example.timewise.ui.home

// Package required by EventExpandableListAdapter class
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.example.timewise.R
import com.example.timewise.db.Event

// EventExpandableListAdapter class to manage the list of events
class EventExpandableListAdapter(private val context: Context) : BaseExpandableListAdapter() {
    // List of events
    private var eventList: List<Event> = emptyList()

    // Set the list of events
    fun setEventList(eventList: List<Event>) {
        this.eventList = eventList
        notifyDataSetChanged()
    }

    // Get the number of events
    override fun getGroupCount(): Int {
        return eventList.size
    }

    // Get the number of children
    override fun getChildrenCount(groupPosition: Int): Int {
        // There are no children to show
        return 0
    }

    // Get the group at the specified position
    override fun getGroup(groupPosition: Int): Any {
        return eventList[groupPosition]
    }

    // Get the child at the specified position
    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        // There are no children to show, so we return a dummy object
        return Any()
    }

    // Get the group id at the specified position
    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    // Get the child id at the specified position
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    // Check if the ids are stable
    override fun hasStableIds(): Boolean {
        return true
    }

    // Check if the child is selectable
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    // Get the view of the group at the specified position
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        // Get the event at the specified position
        val event = getGroup(groupPosition) as Event

        // Inflate the view if it is null
        var view = convertView
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_group, parent, false)
        }

        // Set the title of the event
        val titleTextView = view?.findViewById<TextView>(R.id.groupTitleTextView)
        titleTextView?.text = event.name
        titleTextView?.gravity = Gravity.CENTER

        // Set the background color of the event
        val backgroundColor = when (event.eventType) {
            1 -> { // Birthday event
                getColor(context, R.color.event_birthday_color)
            }
            2 -> { // Trip event
                getColor(context, R.color.event_trip_color)
            }
            3 -> { // Meeting event
                getColor(context, R.color.event_meeting_color)
            }
            4 -> { // Appointment event
                getColor(context, R.color.event_appointment_color)
            }
            5 -> { // Deadline event
                getColor(context, R.color.event_deadline_color)
            }
            6 -> { // Other event
                getColor(context, R.color.event_other_card_color)
            }
            else -> { // Invalid event
                getColor(context, R.color.black)
            }
        }
        view?.setBackgroundColor(backgroundColor)

        // Set the text color to black
        titleTextView?.setTextColor(getColor(context, android.R.color.black))

        // Add the click listener to the group element
        view?.setOnClickListener {
            EventDialogFragment(event).show(
                (context as FragmentActivity).supportFragmentManager,
                "EventDialogFragment"
            )
        }

        // Return the view
        return view!!
    }

    // Method to get the view of the child at the specified position
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        // There are no children to show, so we return an empty view
        return View(context)
    }
}