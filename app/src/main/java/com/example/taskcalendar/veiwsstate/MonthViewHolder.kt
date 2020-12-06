package com.example.taskcalendar.veiwsstate


import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.taskcalendar.R
import com.example.taskcalendar.objects.CDay
import com.example.taskcalendar.objects.CMonth
import com.example.taskcalendar.objects.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.month_item.view.*
import kotlinx.android.synthetic.main.week_item.view.*
import kotlinx.coroutines.*
import org.threeten.bp.LocalDateTime
import java.util.*


class MonthViewHolder(private val inflater: LayoutInflater, private val parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.month_item, parent, false)) {
    private var mTitleView: TextView? = null
    private var mYearView: TextView? = null
    var week: FrameLayout? = null
    var thisMonth: TableLayout? = null
    private var weekFragment: View? = null


    init {
        mTitleView = itemView.findViewById(R.id.monthTitle)
        thisMonth = itemView.findViewById(R.id.thisMonth)
        week = itemView.findViewById(R.id.weekframe)
    }

    fun bind(month: CMonth?, user: User, calendarName: String, date: LocalDateTime) {
        var snapshot: ListenerRegistration? = null
        week!!.removeAllViews()
        val todayData = LocalDateTime.now()
        mTitleView?.text = month!!.name
        thisMonth!!.removeAllViews()
//        coroutineScope {
//            launch(Dispatchers.Main) {
//
//            }
//        }
        val firstDayOfMonth =
            todayData.withYear(month.year!!).withMonth(month.numberOfMonth!!)
                .withDayOfMonth(1).dayOfWeek.value - 1
        val lastDayOfMonth =
            todayData.withYear(month.year!!).withMonth(month.numberOfMonth)
                .withDayOfMonth(month.monthSize!!).dayOfWeek.value - 1
        var week = TableRow(itemView.context)
        week.layoutParams = Parametres().getTableParams()
        thisMonth?.addView(week)

        var usedLength = 1 - firstDayOfMonth
        while (true) {
            if (week.childCount == 7) {
                week = TableRow(itemView.context)
                week.layoutParams = Parametres().getTableParams()
                thisMonth?.addView(week)
            }
            if (usedLength == month.monthSize.toInt() + (7 - lastDayOfMonth)) {
                break
            }
            when (usedLength) {
                in 1..month.monthSize.toInt() -> {
                    val day = Button(itemView.context)
                    day.text = usedLength.toString()
                    try {
                        day.id = month.daysList.get(
                            (
                                    date.withYear(month.year)
                                        .withMonth(month.numberOfMonth)
                                        .withDayOfMonth(
                                            usedLength
                                        ).dayOfYear + month.year * 1000).toString()
                        )!!.id!!
                        if (LocalDateTime.now().dayOfYear == day.id) {
                            day.setBackgroundColor(123)
                        }
                    } catch (e: KotlinNullPointerException) {

                    }
                    day.layoutParams = Parametres().getDayParams()
                    day.setOnClickListener {
                        //TODO
                        if (snapshot != null) {
                            snapshot!!.remove()
                        }
                        snapshot = showWeek(month, day.id)
                    }
                    week.addView(day)
                }
                else -> {
                    val clearDay = Button(itemView.context)
                    clearDay.text = ""
                    clearDay.layoutParams = Parametres().getDayParams()
                    week.addView(clearDay)
                }
            }
            usedLength++
        }
    }

    private fun showWeek(
        currentMonth: CMonth,
        currentDay: Int
    ): ListenerRegistration {
        weekFragment = inflater.inflate(R.layout.week_item, parent, false)!!
        week!!.removeAllViews()
        var snapshot: ListenerRegistration? = null
        var dbDay = currentMonth.daysList[currentDay.toString()]!!
        println("${dbDay.staffList} from ViewHolder")
        var iter = 1
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
            val day = Button(week!!.context)
            try {
                dbDay = currentMonth.daysList[(currentDay - iter + i).toString()]!!
                day.id = dbDay.id!!.toInt()
                day.text = dbDay.numberOfDay.toString()
                day.layoutParams = Parametres().getDayParams()
                if (i == iter) {
                    day.setBackgroundColor(532)
                } else {
                    day.setOnClickListener {
                        showWeek(currentMonth, day.id)
                        if (snapshot != null) {
                            snapshot?.remove()
                        }
                    }
                }
                weekFragment!!.weekDays.addView(day)
            } catch (e: KotlinNullPointerException) {
                day.layoutParams = Parametres().getDayParams()
                weekFragment!!.weekDays.addView(day)
            }
        }

        val selectedDay = currentMonth.daysList[(currentDay).toString()]!!
        snapshot = updateWeek(selectedDay)
        weekFragment!!.btn_add_staff.setOnClickListener {
            addStaff(selectedDay, currentMonth)
        }
        week!!.addView(weekFragment)
        return snapshot
    }


    private fun addStaff(selectedDay: CDay, selectedMonth: CMonth) {
        weekFragment!!.btn_add_staff.text = "-"
        val newStaff = LinearLayout(itemView.context)
        val enterStaff = EditText(itemView.context)
        val confirmBtn = Button(itemView.context)
        newStaff.setHorizontalGravity(1)
        weekFragment!!.staffList.addView(newStaff)
        newStaff.addView(enterStaff)
        newStaff.addView(confirmBtn)
        cancelAddStaff(selectedDay, selectedMonth)
        confirmBtn.setOnClickListener {
            if (enterStaff.text.isNotEmpty()) {
                val day = selectedMonth.daysList[selectedDay.id.toString()]!!
                day.addStaff(enterStaff.text.toString())
                weekFragment!!.btn_add_staff.performClick()
            } else {
                val toast = Toast.makeText(itemView.context, "Enter Name!", Toast.LENGTH_LONG)
                toast.show()
            }
            println(selectedDay.staffList)
        }
    }

    private fun cancelAddStaff(selectedDay: CDay, selectedMonth: CMonth) {
        weekFragment!!.btn_add_staff.setOnClickListener {
            weekFragment!!.staffList.removeView(itemView.staffList.getChildAt(itemView.staffList.childCount - 1))
            weekFragment!!.btn_add_staff.text = "+"
            weekFragment!!.btn_add_staff.setOnClickListener {
                addStaff(selectedDay, selectedMonth)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateWeek(selectedDay: CDay): ListenerRegistration {
        weekFragment!!.staffList.removeAllViews()
        val staffPath =
            FirebaseFirestore.getInstance().document(selectedDay.path).collection("staff")
        return staffPath
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(ContentValues.TAG, "New: ${dc.document.data}")
                            val staffLayout = LinearLayout(itemView.context)
                            staffLayout.tag = dc.document.id
                            val staff = TextView(itemView.context)
                            staff.text = dc.document.id
                            staff.textSize = 24f

                            staffLayout.setOnLongClickListener {
                                val delBtn = Button(itemView.context)
                                delBtn.text = "delete"
                                delBtn.setBackgroundColor(Color.RED)
                                delBtn.setOnClickListener {
                                    staffPath.document(dc.document.id).delete()
                                    selectedDay.deleteStaff(staff.text.toString())
                                }
                                staffLayout.addView(delBtn)
                                return@setOnLongClickListener true
                            }
                            staffLayout.addView(staff)
                            weekFragment!!.staffList.addView(staffLayout)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            Log.d(ContentValues.TAG, "Modified: ${dc.document.data}")
                        }
                        DocumentChange.Type.REMOVED -> {
                            Log.d(ContentValues.TAG, "Removed: ${dc.document.data}")
                            val staff =
                                weekFragment!!.staffList.findViewWithTag<LinearLayout>(dc.document.id)
                            weekFragment!!.staffList.removeView(staff)
                        }
                    }
                }
            }
    }
}


