package com.example.taskcalendar.objects


import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable


data class User(
    var name: String = "",
    var login: String = "",
    var email: String = "",
    var calendarsMap: MutableMap<String, String> = mutableMapOf(),
    var calendarsList: MutableMap<String, CCalendar> = mutableMapOf()
) : Serializable {

    fun addCalendar(calendarName: String) {
        val db = FirebaseFirestore.getInstance()
        val calendar = CCalendar(calendarName, login)
        val path =
            db.collection("users").document(email).collection("calendars").document(calendar.name)
        calendar.makeCalendar(path)
        calendarsList[calendar.name] = calendar
        db.collection("users").document(email).set(this)
        path.set(calendar)
    }
}
