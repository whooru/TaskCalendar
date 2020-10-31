package com.example.taskcalendar.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskcalendar.R

class CalendarListFragment: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_list_view)
    }

}