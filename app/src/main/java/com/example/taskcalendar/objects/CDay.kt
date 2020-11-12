package com.example.taskcalendar.objects

import android.content.ContentValues.TAG
import android.graphics.Color
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

data class CDay(val id: Int? = null, val numberOfDay: Int? = null, val name: String = "", val path: String = "") :
    Serializable {
    val staffList: MutableMap<String, Staff> = mutableMapOf()
//    val eventList: MutableList<Event> = mutableListOf()


    fun addStaff(staffName: String) {
        val staff= Staff(staffName)
//        staffList[staffName] = staff
        val staffPath = FirebaseFirestore.getInstance().document(path)
        staffPath.collection("staff").document(staff.name).set(staff)
//        staffPath.update(mapOf<String, Any>(Pair("staffList", staffList)))
        staffPath.collection("staff")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    staffList[document.id] = document.toObject(Staff::class.java)
                    staffPath.update(mapOf<String, Any>(Pair("staffList", staffList)))
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun deleteStaff(staffName: String){
        staffList.remove(staffName)
        val staffPath = FirebaseFirestore.getInstance().document(path)
        staffPath.collection("staff")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    staffList[document.id] = document.toObject(Staff::class.java)
                    staffPath.update(mapOf<String, Any>(Pair("staffList", staffList)))
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun addEvent(event: Event) {
//        eventList.add(event)
    }

    fun changeColor(color: Color) {

    }
}

