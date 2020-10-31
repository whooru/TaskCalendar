package com.example.taskcalendar.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskcalendar.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CalendarView: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = FirebaseFirestore.getInstance()
        val auth = Firebase.auth

    }
}