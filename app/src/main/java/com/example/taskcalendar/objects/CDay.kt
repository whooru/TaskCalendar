package com.example.taskcalendar.objects

import android.graphics.Color
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

data class CDay(val id: Int? = null, val numberOfDay: Int? = null, val name: String = "", val path: String = "") :
    Serializable {
    val staffList: MutableMap<String, Staff> = mutableMapOf()
//    val eventList: MutableList<Event> = mutableListOf()


    fun addStaff(staffName: String) {
        val staff= Staff(staffName)
        staffList[staffName] = staff
        println(path)
        val staffPath = FirebaseFirestore.getInstance().document(path)
        staffPath.collection("staff").document(staff.name).set(staff)
        println(staffList)
        staffPath.update(mapOf<String, Any>(Pair("staffList", staffList)))
    }

    fun deleteStaff(staffName: String){
        staffList.remove(staffName)
        val staffPath = FirebaseFirestore.getInstance().document(path)
        staffPath.update(mapOf<String, Any>(Pair("staffList", staffList)))
    }

    fun addEvent(event: Event) {
//        eventList.add(event)
    }

    fun changeColor(color: Color) {

    }
}

