package com.example.taskcalendar.veiwsstate

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.*
import com.example.taskcalendar.objects.*
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.core.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_calendar.view.*
import kotlinx.android.synthetic.main.activity_registration.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalDateTime
import java.lang.Exception
import kotlin.coroutines.Continuation

class CalendarViewState(val activity: Activity, val calendarName: String) : State {
    lateinit var year: String
    lateinit var month: String
    override fun updateState(activity: Activity, user: User) {

    }


    fun showMonth(selectedMonth: String, selectedYear: String) {
        year = selectedYear
        month = selectedMonth.toUpperCase()
        val user = Firebase.auth.currentUser!!
        val db = FirebaseFirestore.getInstance()
        val monthPath = db.collection("users").document(user.email!!).collection("calendars")
            .document(calendarName).collection("years").document(year).collection("months")
        val todayData = LocalDateTime.now()
        activity.thisMonth.removeAllViews()
        activity.month.text = month
        var currentMonth: CMonth? = null
        //находим текущий месяц
        try {
            val monthTask = monthPath.document(month).get()

            while (!monthTask.isComplete) {
                Log.d(TAG, "download month")
                println("Download month")
            }

            if (monthTask.isComplete) {
                println("Downloaded it")
                runBlocking {
                    GlobalScope.launch {
                        currentMonth = monthTask.result?.toObject(CMonth::class.java)

                    }.join()
                }
            }

//            runBlocking {
//                GlobalScope.launch {
//                    Tasks.await(
//                        monthPath.document(month).get().addOnSuccessListener { document ->
//                            if (document != null) {
//                                currentMonth =document.toObject(CMonth::class.java)
//                            } else {
//                                Log.d(TAG, "No such document")
//                            }
//                        }
//                    )
//                }.join()
//            }
        } catch (e: Exception) {
            Log.d(TAG, "Cant download month. Exception: ", e)
        }

        //если месяца нет, значит этот год не создан, создаем год
        if (currentMonth == null) {
            println("Cant download")
            val path =
                db.collection("users").document(user.email.toString()).collection("callendars")
                    .document(calendarName)
            var calendar: CCalendar? = null

            //TODO исправить!!!!
            // java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.taskcalendar/com.example.taskcalendar.activities.CalendarActivity}: kotlin.KotlinNullPointerException
            //
            runBlocking {
                GlobalScope.launch {
                    Tasks.await(
                        path.get().addOnSuccessListener { document ->
                            if (document != null) {
                                calendar = document.toObject(CCalendar::class.java)!!
                            } else {
                                Log.d(TAG, "No such document")
                            }
                        }
                    )
                }.join()
            }
            calendar!!.addYear(Year(year.toInt(), calendarName))
            try {
                currentMonth = monthPath.document(month).get().result?.toObject(CMonth::class.java)
            } catch (e: Exception) {
                Log.d(TAG, "Cant download month. Exception: ", e)
            }
        }

        val firstDayOfMonth =
            todayData.withMonth(currentMonth!!.numberOfMonth!!)
                .withDayOfMonth(1).dayOfWeek.value - 1
        val lastDayOfMonth =
            todayData.withMonth(currentMonth!!.numberOfMonth!!)
                .withDayOfMonth(currentMonth!!.monthSize!!).dayOfWeek.value - 1
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
            if (usedLength == currentMonth!!.monthSize!!.toInt() + (7 - lastDayOfMonth)) {
                break
            }
            when (usedLength) {
                in 1..currentMonth!!.monthSize!!.toInt() -> {
                    val btnDay = Button(activity)
                    val day =
                        monthPath.document(month).collection("days").document(usedLength.toString())
                            .get().result!!.toObject(CDay::class.java)!!
                    btnDay.text = usedLength.toString()
                    btnDay.id = day.id!!.toInt()
                    if (LocalDateTime.now().dayOfYear == day.id) {
                        btnDay.setBackgroundColor(123)
                    }
                    btnDay.layoutParams = Parametres().getDayParams()
                    btnDay.setOnClickListener {
                        showWeek(
                            activity,
                            currentMonth!!,
                            day.id,
                            monthPath.document(currentMonth!!.name)
                        )
                    }
                    week.addView(btnDay)
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
            activity.backBtn.visibility = View.VISIBLE
        } else {
            activity.calendarFrame.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            activity.weekFrame.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 3f
            )
            activity.backBtn.visibility = View.INVISIBLE
        }
    }

    fun showWeek(
        activity: Activity,
        currentMonth: CMonth,
        currentDay: Int,
        monthPath: DocumentReference
    ) {
        activity.weekFrame.weekDays.removeAllViews()
        val weekTb = activity.weekFrame.weekTb
        if ((activity.calendarFrame.layoutParams as LinearLayout.LayoutParams).weight == 1f) {
            changeWindow(activity)
        }
        var dbDay =
            monthPath.collection("days").document(currentDay.toString()).get().result!!.toObject(
                CDay::class.java
            )!!
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
        var snapshot: ListenerRegistration? = null
        for (i in 1..7) {
            val day = Button(activity)
            try {
                dbDay = monthPath.collection("days").document((currentDay - iter + i).toString())
                    .get().result!!.toObject(CDay::class.java)!!
//                dbDay = currentMonth.daysList[(currentDay - iter + i).toString()]!!
                day.id = dbDay.id!!.toInt()
                day.text = dbDay.numberOfDay.toString()
                day.layoutParams = Parametres().getDayParams()
                if (i == iter) {
                    day.setBackgroundColor(532)
                } else {
                    day.setOnClickListener {
                        showWeek(activity, currentMonth, day.id, monthPath)
                        if (snapshot != null) {
                            snapshot?.remove()
                        }
                    }
                }
                weekTb.weekDays.addView(day)
            } catch (e: KotlinNullPointerException) {
                day.layoutParams = Parametres().getDayParams()
                weekTb.weekDays.addView(day)
            }
        }
        val selectedDay =
            monthPath.collection("days").document(currentDay.toString()).get().result!!.toObject(
                CDay::class.java
            )!!
//        val selectedDay = currentMonth.daysList[(currentDay).toString()]!!
        snapshot = updateWeek(selectedDay)
        activity.addStuff.setOnClickListener {
            addStaff(selectedDay, currentMonth, monthPath)
        }
        activity.backBtn.setOnClickListener {
            changeWindow(activity)
            snapshot.remove()

        }
    }

    private fun cancelAddStaff(
        selectedDay: CDay,
        selectedMonth: CMonth,
        monthPath: DocumentReference
    ) {
        activity.addStuff.setOnClickListener {
            activity.staffList.removeView(activity.staffList.getChildAt(activity.staffList.childCount - 1))
            activity.addStuff.text = "+"
            activity.addStuff.setOnClickListener {
                addStaff(selectedDay, selectedMonth, monthPath)
            }
        }
    }

    private fun addStaff(selectedDay: CDay, selectedMonth: CMonth, monthPath: DocumentReference) {
        activity.addStuff.text = "-"
        val newStaff = LinearLayout(activity)
        val enterStaff = EditText(activity)
        val confirmBtn = Button(activity)
        newStaff.setHorizontalGravity(1)
        activity.staffList.addView(newStaff)
        newStaff.addView(enterStaff)
//        activity.scrollStaff.maxScrollAmount
        newStaff.addView(confirmBtn)
        cancelAddStaff(selectedDay, selectedMonth, monthPath)
        confirmBtn.setOnClickListener {
            if (enterStaff.text.isNotEmpty()) {
                val day = monthPath.collection("days").document(selectedDay.id.toString())
                    .get().result!!.toObject(CDay::class.java)!!
                day.addStaff(Staff(enterStaff.text.toString()))
                activity.addStuff.performClick()
            } else {
                val toast = Toast.makeText(activity, "Enter Name!", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateWeek(selectedDay: CDay): ListenerRegistration {
        activity.staffList.removeAllViews()
        val staffPath =
            FirebaseFirestore.getInstance().document(selectedDay.path).collection("staff")
        val snapshot = staffPath
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(TAG, "New city: ${dc.document.data}")
                            val staffLayout = LinearLayout(activity)
                            staffLayout.tag = dc.document.id
                            val staff = TextView(activity)
                            staff.text = dc.document.id
                            staff.textSize = 24f
//                            staff.tag = dc.document.id

                            staffLayout.setOnLongClickListener {
                                val delBtn = Button(activity)
                                delBtn.text = "delete"
                                delBtn.setBackgroundColor(Color.RED)
                                delBtn.setOnClickListener {
                                    staffPath.document(dc.document.id).delete()
//                                    staffLayout.removeView(delBtn)
                                }
                                staffLayout.addView(delBtn)
                                return@setOnLongClickListener true
                            }
                            staffLayout.addView(staff)
                            activity.staffList.addView(staffLayout)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            Log.d(TAG, "Modified city: ${dc.document.data}")
                        }
                        DocumentChange.Type.REMOVED -> {
                            Log.d(TAG, "Removed city: ${dc.document.data}")
                            val staff =
                                activity.staffList.findViewWithTag<LinearLayout>(dc.document.id)
                            activity.staffList.removeView(staff)
                        }
                    }
                }
            }
        return snapshot
    }
    //TODO week panel

}

