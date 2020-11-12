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


data class User(
    var name: String = "",
    var login: String = "",
    var email: String = "",
    var calendarsMap: MutableMap<String, String> = mutableMapOf(),
    var calendarsList: MutableMap<String, CCalendar> = mutableMapOf()
) : Serializable {



    fun makeDb() {
        val db = FirebaseFirestore.getInstance()
        val calendar = CCalendar("1", login)
        calendarsMap[(calendarsMap.size + 1).toString()] = calendar.name
        val path =
            db.collection("users").document(email).collection("calendars").document(calendar.name)
        calendar.makeCalendar(path)
        calendarsList[calendar.name] = calendar
        db.collection("users").document(email).set(this)
        path.set(calendar)
    }

    fun addCalendar(calendarName: String){
        val db = FirebaseFirestore.getInstance()
        val calendar = CCalendar(calendarName, login)
        val path =
            db.collection("users").document(email).collection("calendars").document(calendar.name)
        calendar.makeCalendar(path)
        calendarsList[calendar.name] = calendar
        db.collection("users").document(email).set(this)
        path.set(calendar)
    }



//    fun downloadFromDb() {
//        val db = FirebaseFirestore.getInstance()
//
//        for (i in 1..calendarsMap.size + 1) {
//            val docRef = db.collection("users").document(email).collection("calendars")
//                .document(calendarsMap[i.toString()]!!)
//            docRef.get()
//                .addOnSuccessListener { document ->
//                    if (document != null) {
//                        var calendar = document.toObject(CCalendar::class.java)
//                        if (calendar != null) {
//                            this.calendarsList[calendar.name] = calendar
//                        }
//                        Log.d("INFO", "DocumentSnapshot data: ${document.data}")
//                    } else {
//                        Log.d("INFO", "No such document")
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.d("INFO", "get failed with ", exception)
//                }
//        }
//    }

}
