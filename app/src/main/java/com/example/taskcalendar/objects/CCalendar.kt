package com.example.taskcalendar.objects


import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.threeten.bp.LocalDate
import java.io.Serializable


data class CCalendar(val name: String = ""): Serializable {
    private val db = FirebaseFirestore.getInstance()
    private val user = Firebase.auth.currentUser!!
    private val path = db.collection("users").document(user.email!!).collection("calendars").document(name)


//    fun makeCalendar(path: DocumentReference) {
//        val year = Year(LocalDate.now().year, name)
//        val calendarPath = path.collection("years").document(year.numberOfYear.toString())
//        year.makeYear(calendarPath)
//        calendarPath.set(year)
//    }


    fun addYear(year: Year){
        year.makeYear()
        path.collection("years").document(year.numberOfYear.toString()).set(year)
    }

}