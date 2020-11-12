package com.example.taskcalendar.objects

import android.content.ContentValues.TAG
import android.graphics.Color
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

data class CDay(val id: Int? = null, val numberOfDay: Int? = null, val name: String = "", val path: String = "") :
    Serializable {
    val staffList: MutableMap<String, Staff> = mutableMapOf()

    fun addStaff(staffName: String) {
        val staff= Staff(staffName)
        val dayPath = FirebaseFirestore.getInstance().document(path)
        dayPath.collection("staff").document(staff.name).set(staff)
        dayPath.collection("staff")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    staffList[document.id] = document.toObject(Staff::class.java)
                    dayPath.update(mapOf<String, Any>(Pair("staffList", staffList)))
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun deleteStaff(staffName: String){
        staffList.remove(staffName)
        val dayPath = FirebaseFirestore.getInstance().document(path)
        dayPath.collection("staff")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    staffList[document.id] = document.toObject(Staff::class.java)
                    dayPath.update(mapOf<String, Any>(Pair("staffList", staffList)))
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun addEvent(eventName: String) {
//        eventList.add(event)
    }

    fun deleteEvent(eventName: String){

    }
    fun changeColor(color: Color) {

    }
}

