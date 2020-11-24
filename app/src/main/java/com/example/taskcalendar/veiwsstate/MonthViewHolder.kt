package com.example.taskcalendar.veiwsstate


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskcalendar.R
import com.example.taskcalendar.objects.CMonth
import com.example.taskcalendar.objects.User
import org.threeten.bp.LocalDateTime
import java.lang.Exception


class MonthViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.month_item, parent, false)) {
    private var mTitleView: TextView? = null
    private var mYearView: TextView? = null
    var thisMonth: TableLayout? = null

    init {
        mTitleView = itemView.findViewById(R.id.month)
        thisMonth = itemView.findViewById(R.id.thisMonth)
    }

    fun bind(month: CMonth?, user: User, calendarName: String, date: LocalDateTime) {
        val todayData = LocalDateTime.now()
        val selectedYear = date.year.toString()
        val selectedMonth = date.month.toString()
        mTitleView?.text = month!!.name
        thisMonth!!.removeAllViews()
        //        //если месяца нет, создаем
//        if (currentMonth == null) {
//            val db = FirebaseFirestore.getInstance()
//            val calendar = user.calendarsList[calendarName]
//            val path = db.collection("users").document(user.email).collection("calendars")
//                .document(calendar!!.name)
//            calendar.addYear(path, Year(selectedYear.toInt()))
//            currentMonth =
//                user.calendarsList[calendarName]?.yearsList?.get(selectedYear)
//                    ?.monthsList?.get(
//                        selectedMonth
//                    )
//        }
        val firstDayOfMonth =
            todayData.withYear(month.year!!).withMonth(month.numberOfMonth!!)
                .withDayOfMonth(1).dayOfWeek.value - 1
        val lastDayOfMonth =
            todayData.withYear(month.year).withMonth(month.numberOfMonth)
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
                            LocalDateTime.now().withYear(month.year)
                                .withMonth(month.numberOfMonth)
                                .withDayOfMonth(
                                    usedLength
                                ).dayOfYear.toString()
                        )!!.id!!
                        if (LocalDateTime.now().dayOfYear == day.id) {
                            day.setBackgroundColor(123)
                        }
                    } catch (e: KotlinNullPointerException) {
                        println(month.name)
                        println(month.daysList)
                        println(usedLength)
                        try {
                            println(
                                month.daysList[LocalDateTime.now()
                                    .withYear(month.year)
                                    .withMonth(month.numberOfMonth)
                                    .withDayOfMonth(usedLength).dayOfYear.toString()]
                            )
                        } catch (e: Exception) {
                            println(e)
                        }
                    }
                    day.layoutParams = Parametres().getDayParams()
                    day.setOnClickListener {
//                     TODO   showWeek(activity, currentMonth, day.id)
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

}