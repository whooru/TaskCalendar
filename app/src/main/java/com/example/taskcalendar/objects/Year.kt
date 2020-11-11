package com.example.taskcalendar.objects

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.threeten.bp.LocalDate
import java.io.Serializable

data class Year(var numberOfYear: Int? = null, val calendar: String) : Serializable {
    private val db = FirebaseFirestore.getInstance()
    private val user = Firebase.auth.currentUser!!
    private val path =
        db.collection("users").document(user.email.toString()).collection("calendars")
            .document(calendar).collection("years").document(numberOfYear.toString())


    fun makeYear() {
        var month: LocalDate = LocalDate.now().withYear(numberOfYear!!.toInt()).withMonth(1)
        val startMonthNumber = month.monthValue
        for (i in startMonthNumber..12) {
            addMonth(month)
            month = month.plusMonths(1)
        }
    }

    fun addMonth(month: LocalDate) {
        val newMonth =
            CMonth(month.monthValue, month.month.toString(), month.lengthOfMonth(), numberOfYear!!)
        newMonth.makeMonth(path)
        path.collection("months").document(newMonth.name).set(newMonth)
    }

}