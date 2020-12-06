package com.example.taskcalendar.veiwsstate

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.widget.Button
import com.example.taskcalendar.R
import com.example.taskcalendar.activities.CalendarActivity
import com.example.taskcalendar.objects.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.create_calendar_fragment.*
import java.io.Serializable

class MainViewState(private val activity: Activity) {
    lateinit var user: User
    fun updateState(user: User) {
        //make user's calendars
        this.user = user
        val db = FirebaseFirestore.getInstance()
//        val pathCalendar =
//            db.collection("users").document(user.email).collection("calendars")
        println(user.email)

        for (calendar in user.calendarsList) {
            val calendarBtn = makeCalendarBtn(calendar.key)
            activity.main.calendarList.addView(calendarBtn)
        }

        activity.addCalendar.setOnClickListener {
            val dialog = Dialog(activity)
            dialog.setContentView(R.layout.create_calendar_fragment)
            dialog.show()
            dialog.btn_create_calendar.setOnClickListener {
                user.addCalendar(
                    dialog.txt_calendar_name.text.toString(),
                    dialog.chk_access.isChecked
                )
                val calendarBtn = makeCalendarBtn(dialog.txt_calendar_name.text.toString())
                activity.main.calendarList.addView(calendarBtn)
                dialog.cancel()
            }

            dialog.btn_cancel.setOnClickListener {
                dialog.cancel()
            }
        }
    }

    private fun makeCalendarBtn(calendarName: String): Button {
        val calendarBtn = Button(activity)
        calendarBtn.text = calendarName
        calendarBtn.layoutParams = Parametres().getDayParams()
        calendarBtn.width = 90
        calendarBtn.height = 90
        calendarBtn.setOnClickListener {                               //open calendar
            val intent: Intent =
                Intent(activity, CalendarActivity::class.java).apply {
                    putExtra("user", user as Serializable)
                    putExtra("calendar", calendarName)
                }
            activity.startActivity(intent)
        }
        return calendarBtn
    }
}