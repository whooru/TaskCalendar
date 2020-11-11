package com.example.taskcalendar.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taskcalendar.R
import com.example.taskcalendar.veiwsstate.MainViewState
import com.example.taskcalendar.veiwsstate.UserState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

//        userState.downloadUser(db, currentUser, this@MainActivity ) // download user data and show it
        MainViewState().updateState(this)
    }
}





