package com.example.taskcalendar.objects

import java.io.Serializable

data class CDay(val id: Int? = null ,val numberOfDay: Int? = null, val name: String = ""): Serializable {
    val staffList: MutableList<Staff> = mutableListOf()
    val iventList: MutableList<Staff> = mutableListOf()

}