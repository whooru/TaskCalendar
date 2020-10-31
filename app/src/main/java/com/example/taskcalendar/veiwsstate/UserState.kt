package com.example.taskcalendar.veiwsstate

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.taskcalendar.activities.MainActivity
import com.example.taskcalendar.objects.User
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.remote.WriteStream
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserState {
    lateinit var user: User

    @SuppressLint("SetTextI18n")
    fun downloadUser(db: FirebaseFirestore, currentUser: FirebaseUser?, activity: MainActivity) {
        val docRef = db.collection("users").document(currentUser?.email.toString())
        runBlocking { GlobalScope.launch {
            Tasks.await(docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject(User::class.java)!!
                        this@UserState.user = user
                        Log.d("INFO", "DocumentSnapshot data: ${document.data}")
                        println("DocumentSnapshot data: ${document.data}")
                        MainViewState().updateState(activity, this@UserState.user)

                    } else {
                        Log.d("INFO", "No such document")
                        println("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("INFO", "get failed with ", exception)
                })
        }.join()
        }

    }
}