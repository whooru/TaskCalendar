package com.example.taskcalendar.veiwsstate

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.taskcalendar.R
import com.example.taskcalendar.activities.CalendarActivity
import com.example.taskcalendar.objects.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.calendarList
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.calendar_list_view.*
import kotlinx.android.synthetic.main.create_calendar_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable
import java.lang.Exception

class MainViewState {
    fun updateState(activity: Activity /*, user: User*/) {
        //make user's calendars
//        activity.calendarList.removeAllViews()
        val user = Firebase.auth.currentUser!!
        val db = FirebaseFirestore.getInstance()
//        var pathCalendar: CollectionReference? = null
        val pathCalendar =
            db.collection("users").document(user.email!!).collection("calendars")

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
//            val user: User? = db.collection("users").document(user.email.toString())
//                .get().result!!.toObject(User::class.java)
            var dbUser: User? = null
            runBlocking {
                GlobalScope.launch {
                    Tasks.await(
                        db.collection("users").document(user.email.toString())
                            .get().addOnSuccessListener { document ->
                                if (document != null) {
                                    dbUser = document.toObject(User::class.java)
                                } else {
                                    Log.d(TAG, "No such document")
                                }
                            }
                    )
                }
            }
            val dialog = Dialog(activity)
            dialog.setContentView(R.layout.create_calendar_fragment)
            dialog.show()
            dialog.btn_create_calendar.setOnClickListener {
                dbUser!!.addCalendar(dialog.txt_calendar_name.text.toString())
                dialog.cancel()
//                updateState(activity)
            }
            dialog.btn_cancel.setOnClickListener {
                dialog.cancel()
            }
        }


//        for (calendar in user.calendarsMap.values) {
//            val calendarBtn = Button(activity)
//            calendarBtn.text = calendar
//            calendarBtn.layoutParams = Parametres().getDayParams()
//            calendarBtn.width = 90
//            calendarBtn.height = 90
//            calendarBtn.setOnClickListener {                               //open calendar
//                val intent: Intent = Intent(activity, CalendarActivity::class.java).apply {
//                    putExtra("user", user as Serializable)
//                    putExtra("calendar", calendar)
//                }
//                activity.startActivity(intent)
//            }
//            activity.clayout.calendarList.addView(calendarBtn)
//        }

    }
}