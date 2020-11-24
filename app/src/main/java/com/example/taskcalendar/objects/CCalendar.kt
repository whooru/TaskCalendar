package com.example.taskcalendar.objects


import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.threeten.bp.LocalDate
import java.io.Serializable


data class CCalendar(val name: String = "", val owner: String = "") : Serializable {
    var yearsList = mutableMapOf<String, Year>()

    fun makeCalendar(path: DocumentReference) {
        val year = Year(LocalDate.now().year)
        val calendarPath = path.collection("years").document(year.numberOfYear.toString())
        year.makeNextYear(calendarPath)
        yearsList[year.numberOfYear.toString()] = year
        calendarPath.set(year)
        updateCalendar()
    }

    fun addYear(path: DocumentReference, year: Year) {
        val yearPath = path.collection("years").document(year.numberOfYear.toString())
        year.makeNextYear(yearPath)
        yearsList[year.numberOfYear.toString()] = year
        yearPath.set(year)
        updateCalendar()
    }

    private fun updateCalendar() {
        val user = Firebase.auth.currentUser
        val path =
            FirebaseFirestore.getInstance().collection("users").document(user!!.email.toString())
                .collection("calendars").document(name)
        path.set(this)
    }
}