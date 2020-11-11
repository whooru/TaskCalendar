package com.example.taskcalendar.objects


import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.taskcalendar.activities.MainActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable
import org.threeten.bp.LocalDate


data class User(
    var name: String = "",
    var login: String = "",
    var email: String = ""

) : Serializable {


    fun addCalendar(calendarName: String){
        val db = FirebaseFirestore.getInstance()
        val path = db.collection("users").document(email)
        val calendar = CCalendar(calendarName)
        calendar.addYear(Year(LocalDate.now().year, calendarName))
        path.collection("calendars").document(calendarName).set(calendar)

    }



}
