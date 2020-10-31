package com.example.taskcalendar.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskcalendar.R
import com.example.taskcalendar.objects.User
import com.example.taskcalendar.veiwsstate.CalendarViewState
import kotlinx.android.synthetic.main.activity_calendar.*
import org.threeten.bp.LocalDateTime

class CalendarActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val user = intent.getSerializableExtra("user") as User
        val calendarName = intent.getStringExtra("calendar")!!
        var currentDate = LocalDateTime.now()
        val calendarViewState = CalendarViewState(this, user)

        calendarViewState.showMonth(
            currentDate.month.toString(),
            calendarName,
            currentDate.year.toString()
        )

        prevMonth.setOnClickListener {
            currentDate = currentDate.minusMonths(1)
            calendarViewState.showMonth(
                currentDate.month.toString(),
                calendarName,
                currentDate.year.toString()
            )
        }

        nextMonth.setOnClickListener {
            currentDate = currentDate.plusMonths(1)
            calendarViewState.showMonth(
                currentDate.month.toString(),
                calendarName,
                currentDate.year.toString()
            )
        }
    }
}