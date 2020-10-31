package com.example.taskcalendar.veiwsstate

import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.taskcalendar.activities.CalendarActivity
import com.example.taskcalendar.objects.User
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable

class MainViewState : State {
    override fun updateState(activity: Activity, user: User) {
        val textView = TextView(activity)
        activity.clayout.addView(textView)
        val calendarList = ConstraintLayout(activity)
        val tableLayout = TableLayout(activity)
        tableLayout.layoutParams = Parametres().getTableParams()
        var tableRow = TableRow(activity)
        tableRow.layoutParams = Parametres().getTableParams()
        calendarList.addView(tableLayout)
        tableLayout.addView(tableRow)
        var i = 0
        for (calendar in user.calendarsMap.keys) {
            if (i < 2) {
                val calendarBtn = Button(activity)
                calendarBtn.text = calendar
                calendarBtn.layoutParams = Parametres().getDayParams()
                calendarBtn.setOnClickListener {
                    val intent: Intent = Intent(activity, CalendarActivity::class.java).apply {
                        putExtra("user", user as Serializable)
                        putExtra("calendar", calendar)
                    }
                    activity.startActivity(intent)
                }
                tableRow.addView(calendarBtn)
            } else {
                tableRow = TableRow(activity)
                tableLayout.addView(tableRow)
                i = 0
            }
            i++
        }
        activity.setContentView(calendarList)
    }
}