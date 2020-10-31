package com.example.taskcalendar.veiwsstate

import android.app.Activity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableRow
import com.example.taskcalendar.objects.CMonth
import com.example.taskcalendar.objects.User
import com.example.taskcalendar.objects.Year
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_calendar.view.*
import org.threeten.bp.LocalDateTime

class CalendarViewState(val activity: Activity, val user: User) : State {
    override fun updateState(activity: Activity, user: User) {

    }


    fun showMonth(selectedMonth: String, calendarName: String, selectedYear: String) {
        val todayData = LocalDateTime.now()
        activity.month.text = selectedMonth
        activity.thisMonth.removeAllViews()

        var currentMonth =
            user.calendarsList[calendarName]?.yearsList?.get(selectedYear)
                ?.monthsList?.get(
                selectedMonth
            )
        println("it's Ok")
        if (currentMonth == null) {
            println("Not ok")
            val db = FirebaseFirestore.getInstance()
            val calendar = user.calendarsList[calendarName]
            val path = db.collection("users").document(user.email).collection("callendars")
                .document(calendar!!.name)
            calendar.addYear(path, Year(selectedYear.toInt()))
            currentMonth =
                user.calendarsList[calendarName]?.yearsList?.get(selectedYear)
                    ?.monthsList?.get(
                    selectedMonth
                )
        }
        val firstDayOfMonth =
            todayData.withMonth(currentMonth!!.numberOfMonth!!).withDayOfMonth(1).dayOfWeek.value - 1
        val lastDayOfMonth =
            todayData.withMonth(currentMonth.numberOfMonth!!).withDayOfMonth(currentMonth.monthSize!!).dayOfWeek.value - 1
        var week = TableRow(activity)
        week.layoutParams = Parametres().getTableParams()
        activity.thisMonth.addView(week)
        var usedLength = 1 - firstDayOfMonth
        while (true) {
            if (week.childCount == 7) {
                week = TableRow(activity)
                week.layoutParams = Parametres().getTableParams()
                activity.thisMonth.addView(week)
            }
            if (usedLength == currentMonth.monthSize!!.toInt() + (7 - lastDayOfMonth)) {
                break
            }
            when (usedLength) {
                in 1..currentMonth.monthSize!!.toInt() -> {
                    val day = Button(activity)
                    day.text = usedLength.toString()
                    day.id = currentMonth.daysList.get(
                        LocalDateTime.now().withMonth(currentMonth.numberOfMonth!!).withDayOfMonth(
                            usedLength
                        ).dayOfYear.toString()
                    )!!.id!!
                    if (LocalDateTime.now().dayOfYear == day.id) {
                        day.setBackgroundColor(123)
                    }
                    day.layoutParams = Parametres().getDayParams()
                    day.setOnClickListener {
                        showWeek(activity, currentMonth, day.id, user, calendarName)
                    }
                    week.addView(day)
                }
                else -> {
                    val clearDay = Button(activity)
                    clearDay.text = ""
                    clearDay.layoutParams = Parametres().getDayParams()
                    week.addView(clearDay)
                }
            }
            usedLength++
        }
    }

    fun changeWindow(activity: Activity) {
        if ((activity.calendarFrame.layoutParams as LinearLayout.LayoutParams).weight == 1f) {
            activity.calendarFrame.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 3f
            )
            activity.weekFrame.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
        } else {
            activity.calendarFrame.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            activity.weekFrame.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 3f
            )
        }
    }

    fun showWeek(
        activity: Activity,
        currentMonth: CMonth,
        currentDay: Int,
        user: User,
        calendarName: String
    ) {
        activity.weekFrame.weekDays.removeAllViews()
        val weekTb = activity.weekFrame.weekTb
        changeWindow(activity)
        var dbDay = currentMonth.daysList[currentDay.toString()]!!
        var iter = 1
        //monday, tuesday wednesday thursday friday saturday sunday
        when (dbDay.name.toUpperCase()) {
            "MONDAY" -> iter = 1
            "TUESDAY" -> iter = 2
            "WEDNESDAY" -> iter = 3
            "THURSDAY" -> iter = 4
            "FRIDAY" -> iter = 5
            "SATURDAY" -> iter = 6
            "SUNDAY" -> iter = 7
        }
        for (i in 1..7) {
            val day = Button(activity)
            try {
                dbDay = currentMonth.daysList[(currentDay - iter + i).toString()]!!
                day.text = dbDay.numberOfDay.toString()
                day.layoutParams = Parametres().getDayParams()
                if (i == iter) {
                    day.setBackgroundColor(532)
                }
                if (dbDay.id == LocalDateTime.now().dayOfYear) {
                    day.setBackgroundColor(123)
                }
                weekTb.weekDays.addView(day)
            } catch (e: KotlinNullPointerException) {
                day.layoutParams = Parametres().getDayParams()
                weekTb.weekDays.addView(day)
            }
        }
        val selectedDay = currentMonth.daysList[(currentDay).toString()]!!

        activity.staffList
    }
    //TODO week panel

}

