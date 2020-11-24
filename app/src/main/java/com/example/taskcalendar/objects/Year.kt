package com.example.taskcalendar.objects

import com.google.firebase.firestore.DocumentReference
import org.threeten.bp.LocalDate
import java.io.Serializable

data class Year(var numberOfYear: Int? = null) : Serializable {

    private val months: MutableList<CMonth> = mutableListOf()
    var monthsList = mutableMapOf<String, CMonth>()

    fun makeNextYear(path: DocumentReference) {
        for (i in 1..12) {
            val month: LocalDate = LocalDate.now().withYear(numberOfYear!!.toInt()).withMonth(i)
            val newMonth = CMonth(month.monthValue, month.month.toString(), month.lengthOfMonth(), numberOfYear)
            val yearPath = path.collection("months").document(month.month.toString())
            newMonth.makeMonth(month, yearPath)
            months.add(newMonth)
            monthsList[newMonth.name] = newMonth
            yearPath.set(newMonth)
        }
    }
}