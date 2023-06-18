package com.example.timewise.ui.calendar.calendarhome

// Importing necessary packages
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.timewise.R
import com.example.timewise.ui.calendar.day.DayDatasource
import java.time.LocalDate

// Adapter for the calendar recycler view that creates the grid of days
class CalendarAdapter(private var daysOfMonth: Array<String>, private var date: LocalDate) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    // Variable to keep track of the selected cell, might be removed later
    private var selectedItemPosition: Int = -1

    // Function to check if the theme is white or dark
    private fun isWhiteTheme(context: Context): Boolean {
        val nightModeFlags =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_NO
    }

    // ViewHolder inner class required by the RecyclerView
    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)

        // Bind method to set the text of the cell, required by the RecyclerView
        fun bind(day: String) {
            dayOfMonth.text = day
        }
    }


    // Function creating the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(daysOfMonth[position])
        val itemView = holder.itemView

        // Setting the item's onClickListener
        itemView.setOnClickListener {
            // Navigate to the day fragment only if the cell is not empty
            if (itemView.findViewById<TextView>(R.id.cellDayText).text.toString() != "") {
                // Change color of the selected cell, might be removed later
                selectedItemPosition = holder.adapterPosition

                // Get the date of the selected cell
                val selectedDate = getCellDate(itemView)
                // Navigate to the day fragment passing the selected date
                val action =
                    CalendarFragmentDirections.actionNavigationCalendarToDayFragment(selectedDate)
                Navigation.findNavController(itemView).navigate(action)
            }
        }


        // Setting the color of the empty cells to the color of the grid separating lines
        if (itemView.findViewById<TextView>(R.id.cellDayText).text.toString() == "") {

            // Setting the background color of the cell according to the theme
            val isWhiteTheme = isWhiteTheme(holder.itemView.context)
            var color = R.color.white
            if (!isWhiteTheme) {
                color = R.color.theme_night_color1
            }
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, color))
        }
        // Adding lines according to events present in the selected day
        else {
            // Fetching the events of the current cell
            val eventsList = DayDatasource(itemView.context).getEventListOf(getCellDate(itemView))

            // Adding lines for each type of event
            val eventsContainer = itemView.findViewById<LinearLayout>(R.id.eventsList)

            // Insert a line for each type of event in the calendar cell
            for (eventType in eventsList) {
                val line = View(itemView.context)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    eventType.value.size.toFloat() // Makes the size proportional to the number of events
                )

                // Set the line color according to the event type
                when (eventType.key) {
                    "Compleanno" -> line.setBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.event_birthday_color
                        )
                    )
                    "Viaggio" -> line.setBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.event_trip_color
                        )
                    )
                    "Riunione" -> line.setBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.event_meeting_color
                        )
                    )
                    "Appuntamento" -> line.setBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.event_appointment_color
                        )
                    )
                    "Scadenza" -> line.setBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.event_deadline_color
                        )
                    )
                    "Altro" -> line.setBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.event_other_card_color
                        )
                    )
                }

                eventsContainer.addView(line, layoutParams)
            }
        }
    }

    // Function returning the number of cells in the calendar
    override fun getItemCount(): Int {
        return daysOfMonth.size
    }

    // Function that returns the date of the current cell, throws an exception if the cell is empty
    private fun getCellDate(itemView: View): LocalDate {
        // Set the day that will be passed to the day fragment according to the selected cell
        val dayNumber = itemView.findViewById<TextView>(R.id.cellDayText).text.toString()
        if (dayNumber != "")
            return date.withDayOfMonth(dayNumber.toInt())
        else
            throw Exception("The selected cell is empty")
    }
}