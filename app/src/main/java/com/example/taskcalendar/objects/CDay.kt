package com.example.taskcalendar.objects

import android.graphics.Color
import java.io.Serializable

data class CDay(val id: Int? = null, val numberOfDay: Int? = null, val name: String = "") :
    Serializable {
    val staffList: MutableList<Staff> = mutableListOf()
    val eventList: MutableList<Event> = mutableListOf()
    
    fun addStaff(staff: Staff) {
        staffList.add(staff)
    }

    fun addEvent(event: Event) {
        eventList.add(event)
    }

    fun changeColor(color: Color){

    }
}

