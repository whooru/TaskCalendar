package com.example.taskcalendar.veiwsstate

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import com.example.taskcalendar.activities.MainActivity
import com.example.taskcalendar.objects.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserState {
    lateinit var user: User

    @SuppressLint("SetTextI18n")
    //try to download user data, if it's ok, show user data on activity
    fun downloadUser(db: FirebaseFirestore, currentUser: FirebaseUser?, activity: Activity) {
        val docRef = db.collection("users").document(currentUser?.email.toString())
        runBlocking {
            GlobalScope.launch {
                Tasks.await(docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val user = document.toObject(User::class.java)!!
                            this@UserState.user = user
                            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                            MainViewState().updateState(activity, this@UserState.user) //show user data
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    })
            }.join()
        }
    }

//    fun updateUser(db: FirebaseFirestore, user: User) {
//        var upUser: User? = null
//        val docRef = db.collection("users").document(user.email.toString())
//        runBlocking {
//            GlobalScope.launch {
//                Tasks.await(docRef.get()
//                    .addOnSuccessListener { document ->
//                        if (document != null) {
//                            upUser = document.toObject(User::class.java)!!
//                            this@UserState.user = user
//                            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
//
//                        } else {
//                            Log.d(TAG, "No such document")
//                        }
//                    }
//                    .addOnFailureListener { exception ->
//                        Log.d(TAG, "get failed with ", exception)
//                    })
//            }.join()
//        }
//
//    }
}