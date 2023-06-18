package com.example.timewise.ui.calendar.calendarhome

// Importing necessary packages
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timewise.databinding.FragmentCalendarBinding
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// CalendarFragment class to display the calendar
class CalendarFragment : Fragment() {
    // Binding variables
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    // RecyclerView containing the calendar grid
    private lateinit var calendarRecyclerView: RecyclerView

    // TextView containing month and year information
    private lateinit var monthYearText: TextView
    private lateinit var nextMonthButton: MaterialButton
    private lateinit var prevMonthButton: MaterialButton

    // Variable to keep track of the selected date
    private lateinit var selectedDate: LocalDate

    // Method to create the view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding code
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialization code for Views
        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV

        // Setup the listener for the previous month button
        prevMonthButton = binding.prevMonthButton
        prevMonthButton.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            setGridContent()
        }

        // Setup the listener for the next month button
        nextMonthButton = binding.nextMonthButton
        nextMonthButton.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            setGridContent()
        }

        // Initialize date to current date
        selectedDate = LocalDate.now()

        // Fill the grid with the days of the month
        setGridContent()

        // Return the root view
        return root
    }

    // Method that fills the grid with the days of the month
    private fun setGridContent() {
        monthYearText.text = monthYearStringFromDate(selectedDate)
        val daysInMonth: Array<String> = daysInMonthArray(selectedDate)
        val calendarAdapter: CalendarAdapter = CalendarAdapter(daysInMonth, selectedDate)

        // Set the layout manager and adapter for the RecyclerView
        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(context, 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = calendarAdapter
    }

    // Method that converts the a LocalDate to a String
    private fun monthYearStringFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    // Method that fills an array of 42 elements with number corresponding to the dates existing
    // in the month to which the date (passed as a parameter) belongs
    private fun daysInMonthArray(date: LocalDate): Array<String> {
        val GRID_ELEMENTS = 42

        // Create a new ArrayList to hold the days in the month
        val daysInMonthArray = Array<String>(GRID_ELEMENTS) { "" }

        // Get the YearMonth object for the given date
        val yearMonth = YearMonth.from(date)

        // Get the number of days in the month
        val daysInMonth = yearMonth.lengthOfMonth()

        // Get the LocalDate object for the first day of the month
        val firstOfMonth = selectedDate.withDayOfMonth(1)

        // Get the day of the week (1-7, where 1 is Monday) for the first day of the month
        val dayOfWeek = firstOfMonth.dayOfWeek.value

        // Loop through each day of the week (up to 42 days total) and add it to the ArrayList
        for (i in 1..GRID_ELEMENTS) {
            // If the current day is before the first day of the month or after the last day of the month, add an empty string
            if (i >= dayOfWeek && i < daysInMonth + dayOfWeek) {
                daysInMonthArray[i - 1] = ((i - dayOfWeek + 1).toString())
            } else {
                daysInMonthArray[i - 1] = ("")
            }
        }

        // Return the ArrayList of days in the month
        return daysInMonthArray
    }

    // Method to destroy the view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}