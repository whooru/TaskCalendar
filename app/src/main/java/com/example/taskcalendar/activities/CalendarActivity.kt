package com.example.taskcalendar.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.example.taskcalendar.R
import com.example.taskcalendar.objects.User
import com.example.taskcalendar.veiwsstate.CalendarViewState
import com.example.taskcalendar.veiwsstate.Parametres
import kotlinx.android.synthetic.main.activity_calendar.*
import org.threeten.bp.LocalDateTime
import java.time.LocalDate
import kotlin.collections.get as get1

class CalendarActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val user = intent.getSerializableExtra("user") as User
        val calendarName = intent.getStringExtra("calendar")
        var currentDate = LocalDateTime.now()
        val calendarViewState = CalendarViewState(this, user)
        calendarViewState.showMonth(currentDate.month.toString(), calendarName, currentDate.year.toString())
        prevMonth.setOnClickListener {
            currentDate = currentDate.minusMonths(1)
            calendarViewState.showMonth(currentDate.month.toString(), calendarName, currentDate.year.toString())
        }
        nextMonth.setOnClickListener {
            currentDate = currentDate.plusMonths(1)
            println("Month ${currentDate.month}")
            println("Year ${currentDate.year}")
            calendarViewState.showMonth(currentDate.month.toString(), calendarName, currentDate.year.toString())
        }
    }
}