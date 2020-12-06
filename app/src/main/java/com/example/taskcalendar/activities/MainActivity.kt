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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = FirebaseFirestore.getInstance()
        val auth = Firebase.auth
        val currentUser = auth.currentUser!!.email!!
        val userState = UserState(db)
        val mainViewState = MainViewState(this)
        CoroutineScope(Dispatchers.Main).launch {
            val user = userState.downloadUser(
                currentUser
            )!!
            mainViewState.updateState(user)
        }

    }

}





