package com.example.taskcalendar.objects


import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.threeten.bp.LocalDate
import java.io.Serializable


data class CMonth(
    val numberOfMonth: Int? = null,
    val name: String = "",
    val monthSize: Int? = null,
    val year: Int? = null
) :
    Serializable {

    fun makeMonth(path: DocumentReference) {
        val monthRef = path.collection("months").document(name)
        for (i in 1..monthSize!!) {
            addDay(monthRef, i)
        }
    }

    fun addDay(path: DocumentReference, numberOfDay: Int) {
        val dayDate: LocalDate =
            LocalDate.now().withYear(year!!).withMonth(numberOfMonth!!).withDayOfMonth(numberOfDay)
        val day = CDay(
            dayDate.dayOfYear,
            dayDate.dayOfMonth,
            dayDate.dayOfWeek.toString(),
            path.collection("days").document(dayDate.dayOfMonth.toString()).path
        )
        path.collection("days").document(day.numberOfDay.toString()).set(day)
    }
}