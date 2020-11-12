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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.core.Query
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_calendar.view.*
import kotlinx.android.synthetic.main.activity_registration.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalDateTime

class CalendarViewState(val activity: Activity, var user: User) : State {
    lateinit var calendar: String
    lateinit var year: String
    lateinit var month: String
    override fun updateState(activity: Activity, user: User) {

    }


    fun showMonth(selectedMonth: String, calendarName: String, selectedYear: String) {
        calendar = calendarName
        year = selectedYear
        month = selectedMonth
        val todayData = LocalDateTime.now()
        activity.month.text = selectedMonth
        activity.thisMonth.removeAllViews()
        //определяем выбранный месяц
        var currentMonth =
            user.calendarsList[calendarName]?.yearsList?.get(selectedYear)
                ?.monthsList?.get(
                    selectedMonth
                )
        //если месяца нет, создаем
        if (currentMonth == null) {
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
            todayData.withMonth(currentMonth!!.numberOfMonth!!)
                .withDayOfMonth(1).dayOfWeek.value - 1
        val lastDayOfMonth =
            todayData.withMonth(currentMonth.numberOfMonth!!)
                .withDayOfMonth(currentMonth.monthSize!!).dayOfWeek.value - 1
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
                        showWeek(activity, currentMonth, day.id)
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
        currentDay: Int
    ) {
        activity.weekFrame.weekDays.removeAllViews()
        val weekTb = activity.weekFrame.weekTb
        if ((activity.calendarFrame.layoutParams as LinearLayout.LayoutParams).weight == 1f) {
            changeWindow(activity)
        }
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
        var snapshot: ListenerRegistration? = null
        for (i in 1..7) {
            val day = Button(activity)
            try {
                dbDay = currentMonth.daysList[(currentDay - iter + i).toString()]!!
                day.id = dbDay.id!!.toInt()
                day.text = dbDay.numberOfDay.toString()
                day.layoutParams = Parametres().getDayParams()
                if (i == iter) {
                    day.setBackgroundColor(532)
                } else {
                    day.setOnClickListener {
                        showWeek(activity, currentMonth, day.id)
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
        val selectedDay = currentMonth.daysList[(currentDay).toString()]!!
        snapshot = updateWeek(selectedDay)
        activity.addStuff.setOnClickListener {
            addStaff(selectedDay, currentMonth)
        }
        activity.backBtn.setOnClickListener {
            changeWindow(activity)
            snapshot.remove()

        }
    }

    private fun cancelAddStaff(selectedDay: CDay, selectedMonth: CMonth) {
        activity.addStuff.setOnClickListener {
            activity.staffList.removeView(activity.staffList.getChildAt(activity.staffList.childCount - 1))
            activity.addStuff.text = "+"
            activity.addStuff.setOnClickListener {
                addStaff(selectedDay, selectedMonth)
            }
        }
    }

    private fun addStaff(selectedDay: CDay, selectedMonth: CMonth) {
        activity.addStuff.text = "-"
        val newStaff = LinearLayout(activity)
        val enterStaff = EditText(activity)
        val confirmBtn = Button(activity)
        newStaff.setHorizontalGravity(1)
        activity.staffList.addView(newStaff)
        newStaff.addView(enterStaff)
//        activity.scrollStaff.maxScrollAmount
        newStaff.addView(confirmBtn)
        cancelAddStaff(selectedDay, selectedMonth)
        confirmBtn.setOnClickListener {
            if (enterStaff.text.isNotEmpty()) {
//                val day = selectedMonth.daysList[selectedDay.id.toString()]!!
//                day.addStaff(enterStaff.text.toString())
                user.calendarsList[calendar]!!.yearsList[year]!!.monthsList[month]!!.daysList[selectedDay.id.toString()]!!.addStaff(enterStaff.text.toString())
                activity.addStuff.performClick()
            } else {
                val toast = Toast.makeText(activity, "Enter Name!", Toast.LENGTH_LONG)
                toast.show()
            }
            println(selectedDay.staffList)
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
                            Log.d(TAG, "New: ${dc.document.data}")
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
                                    selectedDay.deleteStaff(staff.text.toString())
                                    println(staff.text.toString())
                                    println(selectedDay.staffList)
                                }
                                staffLayout.addView(delBtn)
                                return@setOnLongClickListener true
                            }
                            staffLayout.addView(staff)
                            activity.staffList.addView(staffLayout)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            Log.d(TAG, "Modified: ${dc.document.data}")
                        }
                        DocumentChange.Type.REMOVED -> {
                            Log.d(TAG, "Removed: ${dc.document.data}")
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

