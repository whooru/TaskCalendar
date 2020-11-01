package com.example.taskcalendar.objects


import com.google.firebase.firestore.DocumentReference
import org.threeten.bp.LocalDate
import java.io.Serializable


data class CMonth(val numberOfMonth: Int? = null, val name: String = "", val monthSize: Int? = null):
    Serializable {
//    var daysList = mutableListOf<CDay>()
    var daysList = mutableMapOf<String, CDay>()

    fun makeMonth(month: LocalDate, path: DocumentReference) {
        for (i in 1..monthSize!!) {
            val day = CDay(
                month.withDayOfMonth(i).dayOfYear,
                month.withDayOfMonth(i).dayOfMonth,
                month.withDayOfMonth(i).dayOfWeek.toString(),
                path.collection("days").document(month.withDayOfMonth(i).dayOfMonth.toString()).path
            )
            daysList[day.id!!.toString()] = day
            val dayPath = path.collection("days").document(day.numberOfDay.toString())

            dayPath.set(day)
        }
    }
}