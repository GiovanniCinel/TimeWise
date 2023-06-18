package com.example.timewise.ui.calendar.day

// Packages required by DayFragment class
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.timewise.R
import com.example.timewise.databinding.FragmentDayBinding
import java.time.LocalDate

// DayFragment class to show the events of a specific day
class DayFragment : Fragment(), DayDialogFragment.DayDialogListener {
    // Binding section
    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!

    // Date of the day
    private lateinit var date: LocalDate

    // Method to navigate to the event fragment selected by the user
    override fun onNavigate(selectedId: Int) {
        // Get the root view
        val it: View = binding.root

        // Redirect to fragment based on event type selected
        when (selectedId) {
            R.id.birthday -> {
                val action = DayFragmentDirections.actionNavigationDayToBirthdayFragment(date)
                Navigation.findNavController(it).navigate(action)
            }
            R.id.trip -> {
                val action = DayFragmentDirections.actionNavigationDayToTripFragment(date)
                Navigation.findNavController(it).navigate(action)
            }
            R.id.meeting -> {
                val action = DayFragmentDirections.actionNavigationDayToMeetingFragment(date)
                Navigation.findNavController(it).navigate(action)
            }
            R.id.appointment -> {
                val action = DayFragmentDirections.actionNavigationDayToAppointmentFragment(date)
                Navigation.findNavController(it).navigate(action)
            }
            R.id.deadline -> {
                val action = DayFragmentDirections.actionNavigationDayToDeadlineFragment(date)
                Navigation.findNavController(it).navigate(action)
            }
            R.id.other -> {
                val action = DayFragmentDirections.actionNavigationDayToOtherFragment(date)
                Navigation.findNavController(it).navigate(action)
            }
        }
    }

    // Method to create the day fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the date from the arguments passed by the previous fragment
        val args: DayFragmentArgs by navArgs()
        date = args.selectedDate
    }

    // Add the click listener to the floating action button
    private fun addEventOnClickListener() {
        binding.fabAddEvent.setOnClickListener {
            // Show the dialog fragment
            DayDialogFragment().show(childFragmentManager, "DayDialogFragment")
        }
    }

    // Method for the day fragment view creation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Day fragment inflating
        _binding = FragmentDayBinding.inflate(inflater, container, false)

        // Get root view
        val root: View = binding.root

        // Extract date from which start navigation
        val dateEventDay = DayFragmentArgs.fromBundle(requireArguments()).selectedDate

        // Events recycler view binding
        bindDayRecyclerView(root, dateEventDay)

        // Setup add event button click listener
        addEventOnClickListener()

        // Inflate the layout for this fragment
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = date.toString()

        // Set the view model for the fragment
        DayViewModel.fragment = this

        // Return root view
        return root
    }

    // Method for events recycler view binding
    private fun bindDayRecyclerView(view: View, dateEventDay: LocalDate): RecyclerView {
        // Copy event data in a list using EventDatasource class
        val dayEventList = DayDatasource(view.context).getEventListOf(dateEventDay)
        // Set the recycler view adapter
        val recyclerView: RecyclerView = binding.eventDayList
        recyclerView.adapter = DayListAdapter(dayEventList, dateEventDay)

        // Return day event recycler view
        return recyclerView
    }

    // Method to destroy the day fragment
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null
        _binding = null
    }
}

