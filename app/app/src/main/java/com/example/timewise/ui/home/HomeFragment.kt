package com.example.timewise.ui.home

// Packages required by HomeFragment class
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.timewise.R
import com.example.timewise.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

// HomeFragment class to display the homepage
class HomeFragment : Fragment() {
    // Binding section
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Calendar view
    private lateinit var calendarView: CalendarView

    // Expandable list view
    private lateinit var expandableListViewTask: ExpandableListView
    private lateinit var expandableListViewEvent: ExpandableListView

    // Expandable list view adapters
    private lateinit var taskAdapter: TaskExpandableListAdapter
    private lateinit var eventAdapter: EventExpandableListAdapter

    // Method to create the view of the home fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Get calendar view
        calendarView = binding.calendarView

        // Return the view
        return view
    }

    // Method invoked when the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set calendar on date change listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = dateFormat.format(calendar.time)
            Toast.makeText(requireContext(), selectedDate, Toast.LENGTH_SHORT).show()
        }

        // Get the references to the task element of the layout
        expandableListViewTask = view.findViewById(R.id.expandableListViewTask)
        taskAdapter = TaskExpandableListAdapter(requireContext())

        // Set the adapter for the task ExpandableListView
        expandableListViewTask.setAdapter(taskAdapter)

        // Handle click events on the task ExpandableListView items, if necessary
        expandableListViewTask.setOnChildClickListener { _, _, _, _, _ ->
            // Handle the click event on the child element
            true
        }

        // Get the references to the event element of the layout
        expandableListViewEvent = view.findViewById(R.id.expandableListViewEvent)
        eventAdapter = EventExpandableListAdapter(requireContext())

        // Set the adapter for the event ExpandableListView
        expandableListViewEvent.setAdapter(eventAdapter)

        // Handle click events on the event ExpandableListView items, if necessary
        expandableListViewEvent.setOnChildClickListener { _, _, _, _, _ ->
            // Handle the click event on the child element
            true
        }

        // Load task data from the database and pass it to the adapter
        val taskList = HomeDatasource(view.context)
        taskAdapter.setTaskList(taskList.getTaskByPriority())

        // Load event data from the database and pass it to the adapter
        val eventList = HomeDatasource(view.context)
        eventAdapter.setEventList(eventList.getEventByDay(LocalDate.now()))
    }

    // Method to destroy the view of the home fragment
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null
        _binding = null
    }
}