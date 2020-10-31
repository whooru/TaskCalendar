package com.example.taskcalendar.objects

import com.google.firebase.firestore.DocumentReference
import org.threeten.bp.LocalDate
import java.io.Serializable

data class Year(var numberOfYear: Int? = null) : Serializable {

    private val months: MutableList<CMonth> = mutableListOf()
    private var month: LocalDate = LocalDate.now().minusMonths(2)
    var monthsList = mutableMapOf<String, CMonth>()

    fun makeYear(path: DocumentReference) {
        val startMonthNumber = month.monthValue
        for (i in startMonthNumber..12) {
            val newMonth = CMonth(month.monthValue, month.month.toString(), month.lengthOfMonth())
            val yearPath = path.collection("months").document(month.month.toString())
            newMonth.makeMonth(month, yearPath)
            months.add(newMonth)
            monthsList[newMonth.name] = newMonth
            yearPath.set(newMonth)
            month = month.plusMonths(1)
        }
    }

    fun makeNextYear(path: DocumentReference) {
        for (i in 1..12) {
            val month: LocalDate = LocalDate.now().withMonth(i)
            val newMonth = CMonth(month.monthValue, month.month.toString(), month.lengthOfMonth())
            val yearPath = path.collection("months").document(month.month.toString())
            newMonth.makeMonth(month, yearPath)
            months.add(newMonth)
            monthsList[newMonth.name] = newMonth
            yearPath.set(newMonth)
        }
    }
}