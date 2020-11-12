package com.example.taskcalendar.veiwsstate

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.widget.Button
import com.example.taskcalendar.R
import com.example.taskcalendar.activities.CalendarActivity
import com.example.taskcalendar.objects.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.create_calendar_fragment.*
import java.io.Serializable

class MainViewState : State {
    override fun updateState(activity: Activity, user: User) {
        //make user's calendars
        val db = FirebaseFirestore.getInstance()
        val pathCalendar =
            db.collection("users").document(user.email).collection("calendars")
        println(user.email)
        var snapshot: ListenerRegistration? = null
        snapshot = pathCalendar.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "listen:error", e)
                return@addSnapshotListener
            }
            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        Log.d(ContentValues.TAG, "New calendar: ${dc.document.data}")
                        val calendarBtn = Button(activity)
                        calendarBtn.text = dc.document.id
                        calendarBtn.layoutParams = Parametres().getDayParams()
                        calendarBtn.width = 90
                        calendarBtn.height = 90
                        calendarBtn.setOnClickListener {                               //open calendar
                            val intent: Intent =
                                Intent(activity, CalendarActivity::class.java).apply {
                                    putExtra("user", user as Serializable)
                                    putExtra("calendar", dc.document.id)
                                }
                            activity.startActivity(intent)
                            snapshot!!.remove()
                        }
                        activity.clayout.calendarList.addView(calendarBtn)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.d(ContentValues.TAG, "Modified calendar: ${dc.document.data}")
                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.d(ContentValues.TAG, "Removed calendar: ${dc.document.data}")

                    }
                }
            }
        }

        activity.addCalendar.setOnClickListener {
            val dialog = Dialog(activity)
            dialog.setContentView(R.layout.create_calendar_fragment)
            dialog.show()
            dialog.btn_create_calendar.setOnClickListener {
                user.addCalendar(dialog.txt_calendar_name.text.toString())
                dialog.cancel()
            }
            dialog.btn_cancel.setOnClickListener {
                dialog.cancel()
            }
        }
    }
}